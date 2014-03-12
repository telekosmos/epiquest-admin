package org.cnio.appform.util.dump;


import java.util.Iterator;
import java.util.List;

public class RepeteableRetriever extends  DataRetriever {


  // retrieve items in a repeatable wrapped by the parent item. for header
  protected String repBlockItems = "select it.iditem, it.item_order,qai.answer_order, q.codquestion, it.content, ait.idansitem " +
    "from item it, question q, answer_item ait, question_ansitem qai " +
    "where it.ite_iditem = xxx " +
    "  and q.idquestion = it.iditem" +
    "  and ait.idansitem = qai.codansitem" +
    "  and q.idquestion = qai.codquestion " +
    "order by 2, 3;";

  protected String repBlockAnswers = "select p.codpatient, g.name as grpname, i.name as intrvname, s.name as secname," +
    "q.codquestion as codq, a.thevalue, s.section_order, it.item_order, pga.answer_order, " +
    "pga.answer_number, it.repeatable as itrep " +
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
    " order by 1, 7, 10, 8, 5, 9 ";


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
  public String buildRepBlockHeader (String prjCode, Integer intrvId, Integer secOrder, Integer parentItem) {

    String qry = repBlockItems.replaceFirst("xxx", parentItem.toString());
    List<Object[]> rs = execQuery(qry, -1, -1);
    String header = null;

    if (rs != null && rs.size() > 0) {

      header = "Subject|Project|Questionnaire";
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
   * @return
   */
  public List getAnswersForRepBlock (String prjCode, Integer intrvId, Integer grpId, Integer secOrder, Integer parentItem) {
    String secParam = (secOrder == null)?"s.section_order ": secOrder.toString();
    String grpParam = (grpId == null? "1=1 ": "g.idgroup = "+grpId.toString());

    repBlockAnswers = repBlockAnswers.replaceAll("ssss", secParam);
    repBlockAnswers = repBlockAnswers.replaceAll("gggg", grpParam);
    repBlockAnswers = repBlockAnswers.replaceAll("pppp", prjCode);
    repBlockAnswers = repBlockAnswers.replaceAll("hhhh", parentItem.toString());
    repBlockAnswers = repBlockAnswers.replaceAll("iiii", intrvId.toString());

    System.out.println(repBlockAnswers);
    List<Object[]> rs = execQuery(repBlockAnswers, -1, 1000);

    return rs;
  }
}
