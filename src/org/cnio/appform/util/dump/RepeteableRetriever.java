package org.cnio.appform.util.dump;


import java.util.Iterator;
import java.util.List;

public class RepeteableRetriever extends  DataRetriever {


  // retrieve items in a repeatable wrapped by the parent item. for header
  protected String repBlockItems =
    "select it.iditem, it.item_order,qai.answer_order, q.codquestion, it.content, ait.idansitem, s.\"name\" " +
    "from item it, question q, answer_item ait, question_ansitem qai, section s " +
    "where it.ite_iditem = xxx " +
    "  and q.idquestion = it.iditem" +
    "  and ait.idansitem = qai.codansitem" +
    "  and it.idsection = s.idsection" +
    "  and q.idquestion = qai.codquestion " +
    "order by 2, 3;";

  protected String repBlockAnswers =
    "select p.codpatient, pj.\"name\" as prjname, g.name as grpname, i.name as intrvname, " +
      "s.name as secname, q.codquestion as codq, a.thevalue, s.section_order, " +
      "it.item_order, pga.answer_number, pga.answer_order, it.repeatable as itrep " +
    "from patient p, pat_gives_answer2ques pga, appgroup g,performance pf, " +
    " question q, answer a, interview i, item it, section s, project pj " +
    "where gggg " +
    "and i.idinterview = iiii " +
    "and pj.project_code = 'pppp' " +
    "and pj.idprj = i.codprj " +
    "and pf.codinterview = i.idinterview " +
    "and pf.codgroup = g.idgroup " +
    "and s.codinterview = i.idinterview " +
    "and pf.codpat = p.idpat " +
    "and pga.codpat = p.idpat " +
    "and pga.codquestion = q.idquestion " +
    "and pga.codanswer = a.idanswer " +
    "and q.idquestion = it.iditem " +
    "and it.ite_iditem = hhhh " +
    "and it.idsection = s.idsection  " +
    "and s.section_order =  ssss " +
    " order by 1, 8, 10, 9, 11 ";


  /**
   * Builds up a header for the items inside a repeatable block whose parent is
   * parentItem
   * @param prjCode the project code
   * @param intrvId the questionnaire database id
   * @param secOrder the section order in the questionnaire
   * @param parentItem the "parent" of the block
   * @return a string which will be the header for the file, with the shape
   * Subject|Project|Questionnaire|itemX1-1-1|itemX2-1-1|itemX3-1-1|itemX3-1-2
   */
  public String buildRepBlockHeader (String prjCode, Integer intrvId,
                                     Integer secOrder, Integer parentItem) {

    String qry = repBlockItems.replaceFirst("xxx", parentItem.toString());
    List<Object[]> rs = execQuery(qry, -1, -1);
    String header = null;

    if (rs != null && rs.size() > 0) {

      header = "Subject|Project|Questionnaire|Section";
      Iterator<Object[]> itRs = rs.iterator();
      while (itRs.hasNext()) {
        Object[] row = itRs.next();
        header += "|"+ row[3]+"_"+row[2].toString().trim();
      }
    }

    return header;
  }



  /**
   * Just get the resultset with all answers for a repeatable block
   * @param prjCode the project code
   * @param intrvId the database questionnaire id
   * @param grpId the database group id
   * @param secOrder the number of section in the questionnaire
   * @param parentItem the item which is the parent for the repeatable block
   * @return a list of Object[] with the resultset content
   */
  public List<Object[]> getAnswersForRepBlock (String prjCode, Integer intrvId, Integer grpId, Integer secOrder, Integer parentItem) {
    String secParam = (secOrder == null)?"s.section_order ": secOrder.toString();
    String grpParam = (grpId == null? "1=1 ": "g.idgroup = "+grpId.toString());
    String qry = repBlockAnswers;
    qry = qry.replaceAll("ssss", secParam)
              .replaceAll("gggg", grpParam)
              .replaceAll("pppp", prjCode)
              .replaceAll("hhhh", parentItem.toString())
              .replaceAll("iiii", intrvId.toString());

    System.out.println("getAnswersForRepBlock: Running query");
    List<Object[]> rs = execQuery(qry, -1, 1000);

    return rs;
  }



  /**
   * Yields a dump for a repeatable block. To do this:
   * - one block per entry in the dumpfile
   * - answers are one answer per row in the resultset
   * - one repeat finishes in the rs when answer_number changes but pat is the same
   * - on new repeat, subject-project-questionnaire-section has to be filled
   * - on each row, value has to be added
   * @param prjCode the project code
   * @param intrvId the database questionnaire id
   * @param grpId the database group id
   * @param secOrder the number of section in the questionnaire
   * @param parentItem the item which is the parent for the repeatable block
   */
  public void buildupRepBlockDump (String prjCode, Integer intrvId, Integer grpId,
                                   Integer secOrder, Integer parentItem) {

    String header = buildRepBlockHeader(prjCode, intrvId, secOrder, parentItem);
    if (header == null)
      return;

    String[] headerArr = header.split("\\|");
    System.out.println(header);

    List<Object[]> rs = getAnswersForRepBlock(prjCode, intrvId, grpId, secOrder, parentItem);
    Iterator<Object[]> itRs = rs.iterator();

    int oldItemOrder = 9999, newItemOrder = -1;
    StringBuffer line = new StringBuffer();
    // how to add elements according the header
    // i. as queries are ordered, don't have to do anything
    // ii. keeping a data structure in memory to enforce matching header<->answers
    while (itRs.hasNext()) {
      Object[] row = itRs.next();
      if (row[8] instanceof Integer)
        newItemOrder = ((Integer)row[8]).intValue();
      else
        newItemOrder = Integer.parseInt((String)row[8]);

      if (newItemOrder < oldItemOrder) { // start new repeating block
        if (line.length() > 0) {
          System.out.println(line.toString());
          line.delete(0, line.length());
        }

        line.append(row[0].toString())
            .append("|"+row[1].toString())
            .append("|"+row[3].toString())
            .append("|"+row[4].toString());

        // line.append("|"+row[6].toString());
      }

      line.append("|"+row[6].toString());
      oldItemOrder = newItemOrder;
    }
    System.out.println(line.toString());

  }


  /**
   * Builds up all repeating blocks dumps for a section
   * @param prjCode the project code
   * @param intrvId the database questionnaire id
   * @param grpId the database group id
   * @param secOrder the number of section in the questionnaire
   */
  public void buildupSectionRepblocksDump (String prjCode, Integer intrvId, Integer grpId,
                                           Integer secOrder) {

    String repBlocksParents = getRepeatableItems(prjCode, intrvId, secOrder);
    if (repBlocksParents.length() > 0) {
      String[] blockParents = repBlocksParents.split(",");

      for (int i=0; i<blockParents.length; i++) {
        Integer parentItem = Integer.parseInt(blockParents[i]);
        buildupRepBlockDump(prjCode, intrvId, grpId, secOrder, parentItem);
      }
    }
  }


}
