package org.cnio.appform.util.dump;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public class RepeatableRetriever extends  DataRetriever {

  protected XSSFWorkbook wb;
  protected static final int firstFieldIndex = 4;

  // retrieve items in a repeatable wrapped by the parent item. for header
  protected String repBlockItemsQry =
    "select it.iditem, it.item_order,qai.answer_order, q.codquestion, it.content, ait.idansitem, s.\"name\" " +
    "from item it, question q, answer_item ait, question_ansitem qai, section s " +
    "where it.ite_iditem = xxx " +
    "  and q.idquestion = it.iditem" +
    "  and ait.idansitem = qai.codansitem" +
    "  and it.idsection = s.idsection" +
    "  and q.idquestion = qai.codquestion " +
    "order by 2, 3;";


  protected String noRepeatItemsQry =
    "select it.iditem, q.codquestion, it.content, s.section_order, it.item_order, qai.answer_order " +
      "from interview i, section s, item it, project prj, question q, answer_item ait, question_ansitem qai "+
      "where prj.project_code = 'pppp' "+
      "and prj.idprj = i.codprj " +
      "and s.codinterview = i.idinterview "+
      "and i.idinterview in (iiii) "+
      "and q.idquestion = it.iditem " +
      "and ait.idansitem = qai.codansitem " +
      "and it.idsection = s.idsection " +
      "and q.idquestion = qai.codquestion " +
      "and s.section_order = ssss "+
      "and it.\"repeatable\" = 0 "+
      "and (it.ite_iditem is null rrrr) "+
      "order by 5, 6;";

  protected String repBlockAnswersQry =
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


  protected String simpleAnswersQry = "select p.codpatient, pj.\"name\" as prjname, g.name as grpname, i.name as intrvname, s.name as secname, " +
    "q.codquestion as codq, a.thevalue, s.section_order, it.item_order, pga.answer_number, " +
    "pga.answer_order, it.repeatable as itrep " +
    "from patient p, pat_gives_answer2ques pga, appgroup g, performance pf, " +
    "  question q, answer a, interview i, item it, section s, project pj " +
    "where 1=1 " +
    "and i.idinterview = iiii " +
    "and pj.project_code = 'pppp' " +
    "and pj.idprj = i.codprj  " +
    "and pf.codinterview = i.idinterview " +
    "and pf.codgroup = g.idgroup  " +
    "and s.codinterview = i.idinterview " +
    "and pf.codpat = p.idpat " +
    "and pga.codpat = p.idpat " +
    "and pga.codquestion = q.idquestion " +
    "and pga.codanswer = a.idanswer " +
    "and q.idquestion = it.iditem " +
    "  and (it.ite_iditem is null rrrr) " +
    "and it.\"repeatable\" = 0 " +
    "and it.idsection = s.idsection   " +
    "and s.section_order = ssss " +
    "order by 1, 8, 10, 9, 11";



  private String repIds;

  public RepeatableRetriever(String path, Hashtable map) {
    super (path, map);

    wb = new XSSFWorkbook();
  }


  public RepeatableRetriever() {
    wb = new XSSFWorkbook();
    repIds = "";
  }



  public Workbook getExcelWb () {
    return this.wb;
  }



  /**
   * Builds up the header for the set of simple items in a section
   * @param prjCode the project code
   * @param intrvId the questionnaire database id
   * @param secOrder the section order in the questionnaire
   * @return a string forming the header of the no repeating items
   */
  public String buildHeaderSimpleItems (String prjCode, Integer intrvId,
                                        Integer secOrder) {

    repIds = repIds.equalsIgnoreCase("")?
                getRepeatableItems(prjCode, intrvId, secOrder):
                repIds;

    String repIdsQry = repIds.equalsIgnoreCase("")? "":
                "or it.ite_iditem not in ("+repIds+") ";

    String qry = noRepeatItemsQry;
    qry = qry.replaceAll("pppp", prjCode);
    qry = qry.replaceAll("iiii", intrvId.toString());
    qry = qry.replaceAll("ssss", secOrder.toString());
    qry = qry.replaceAll("rrrr", repIdsQry);

    List<Object[]> rs = execQuery(qry, -1, -1);
    String header = null;
    if (rs != null && rs.size() > 0) {

      header = "Subject|Project|Questionnaire|Section";
      // Iterator<Object[]> itRs = rs.iterator();
      // while (itRs.hasNext()) {
      for (Iterator<Object[]> itRs = rs.iterator(); itRs.hasNext();) {
        Object[] row = itRs.next();
        header += "|"+ row[1]+"_"+row[5].toString().trim();
      }
    }
    return header;
  }



  /**
   * Builds up a header for the items inside a repeatable block whose parent is
   * parentItem
   * @param parentItem the "parent" of the block
   * @return a string which will be the header for the file, with the shape
   * Subject|Project|Questionnaire|itemX1-1-1|itemX2-1-1|itemX3-1-1|itemX3-1-2
   */
  public String buildRepBlockHeader (Integer parentItem) {

    String qry = repBlockItemsQry.replaceFirst("xxx", parentItem.toString());
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
  public List<Object[]> getAnswersForRepBlock (String prjCode, Integer intrvId,
                                               Integer grpId, Integer secOrder, Integer parentItem) {

    String secParam = (secOrder == null)?"s.section_order ": secOrder.toString();
    String grpParam = (grpId == null? "1=1 ": "g.idgroup = "+grpId.toString());
    String qry = repBlockAnswersQry;
    qry = qry.replaceAll("ssss", secParam)
              .replaceAll("gggg", grpParam)
              .replaceAll("pppp", prjCode)
              .replaceAll("hhhh", parentItem.toString())
              .replaceAll("iiii", intrvId.toString());

    List<Object[]> rs = execQuery(qry, -1, 1000);

    return rs;
  }



  /**
   * Get the answers for the items which are not in repeatable blocks and they are not
   * repeatables themselves
   * @param prjCode the project code
   * @param intrvId the database questionnaire id
   * @param grpId the database group id
   * @param secOrder the number of section in the questionnaire
   * @return a list with the retrieved answers
   */
  public List<Object[]> getAnswers4SimpleItems (String prjCode, Integer intrvId,
                                                Integer grpId, Integer secOrder) {

    String secParam = (secOrder == null)?"s.section_order ": secOrder.toString();
    String grpParam = (grpId == null? "1=1 ": "g.idgroup = "+grpId.toString());

    repIds = repIds.equalsIgnoreCase("")?
      getRepeatableItems(prjCode, intrvId, secOrder):
      repIds;
    String repIdsQry = repIds.equalsIgnoreCase("")? "":
      "or it.ite_iditem not in ("+repIds+") ";

    String qry = simpleAnswersQry;
    qry = qry.replaceAll("ssss", secParam)
      .replaceAll("gggg", grpParam)
      .replaceAll("pppp", prjCode)
      .replaceAll("iiii", intrvId.toString())
      .replaceAll("rrrr", repIdsQry);

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
  public void printOutRepBlockDump (String prjCode, Integer intrvId, Integer grpId,
                                   Integer secOrder, Integer parentItem) {

    String header = buildRepBlockHeader(parentItem);
    if (header == null)
      return;

    // String[] headerArr = header.split("\\|");
    // String firstFieldCode = headerArr[RepeatableRetriever.firstFieldIndex];
    // XSSFSheet sheet = (XSSFSheet)wb.createSheet(firstFieldCode);
    // sheet = writeOutHeader(headerArr, sheet);
    System.out.println(header);
    List<Object[]> rs = getAnswersForRepBlock(prjCode, intrvId, grpId, secOrder, parentItem);
    Iterator<Object[]> itRs = rs.iterator();

    int oldItemOrder = 9999, newItemOrder;
    // StringBuffer line = new StringBuffer();
    StringBuilder line = new StringBuilder();

    // int lineCount = 1;
    // Row xlsRow = sheet.createRow(lineCount);
    // how to add elements according the header
    // i. as queries are ordered, don't have to do anything
    // ii. keeping a data structure in memory to enforce matching header<->answers
    while (itRs.hasNext()) {
      Object[] row = itRs.next();
      if (row[8] instanceof Integer)
        newItemOrder = ((Integer)row[8]);
      else
        newItemOrder = Integer.parseInt((String)row[8]);

      if (newItemOrder < oldItemOrder) { // start new repeating block
        if (line.length() > 0) {
          System.out.println(line.toString());
          line.delete(0, line.length()); // reset/clear line
        }
        // Initialize the new record with the fixed fields
        line.append(row[0].toString()).append("|").append(row[1].toString()).
          append("|").append(row[3].toString()).append("|").append(row[4].toString());

        // line.append("|"+row[6].toString());
      }

      line.append("|").append(row[6].toString());
      oldItemOrder = newItemOrder;
    }
    System.out.println(line.toString());

  }



  /**
   * Builds up all repeating blocks dumps for a section.
   * Yields a xlsx excel file, one sheet for each block.
   *
   * @param prjCode the project code
   * @param intrvId the database questionnaire id
   * @param grpId the database group id
   * @param secOrder the number of section in the questionnaire
   */
  public void printoutSectionRepblocksDump (String prjCode, Integer intrvId, Integer grpId,
                                            Integer secOrder) {

    repIds = repIds.equalsIgnoreCase("")?
      getRepeatableItems(prjCode, intrvId, secOrder):
      repIds;

    if (repIds.length() > 0) {
      String[] blockParents = repIds.split(",");

      for (int i=0; i<blockParents.length; i++) {
        Integer parentItem = Integer.parseInt(blockParents[i]);
        printOutRepBlockDump(prjCode, intrvId, grpId, secOrder, parentItem);
      }
    }
  }




  /**
   * Builds up a sheet with the data of a repeatable block
   * @param prjCode the project code
   * @param intrvId the database questionnaire id
   * @param grpId the database group id
   * @param secOrder the number of section in the questionnaire
   * @param parentItem the item which is the parent for the repeatable block
   * @return a xlsx sheet with all rows for the repeatable block
   */
  public XSSFSheet xlsxRepBlockDump (String prjCode, Integer intrvId, Integer grpId,
                                Integer secOrder, Integer parentItem) {

    String header = buildRepBlockHeader(parentItem);
    if (header == null)
      return null;

    String[] headerArr = header.split("\\|");
    String firstFieldCode = headerArr[RepeatableRetriever.firstFieldIndex];

    XSSFSheet sheet = this.wb.createSheet(firstFieldCode);
    sheet = writeOutHeader(headerArr, sheet);
    System.out.println(header);
    List<Object[]> rs = getAnswersForRepBlock(prjCode, intrvId, grpId, secOrder, parentItem);
    Iterator<Object[]> itRs = rs.iterator();

    int oldItemOrder = 9999, newItemOrder;
    int lineCount = 1;
    Row xlsRow = sheet.createRow(lineCount);
    // how to add elements according the header
    // i. as queries are ordered, don't have to do anything
    // ii. keeping a data structure in memory to enforce matching header<->answers

    while (itRs.hasNext()) {
      Object[] row = itRs.next();
      if (row[8] instanceof Integer)
        newItemOrder = ((Integer)row[8]);
      else
        newItemOrder = Integer.parseInt((String)row[8]);

      if (newItemOrder < oldItemOrder) { // start new repeating block
        if (xlsRow.getLastCellNum()-1 > 0) {
          lineCount++;
          xlsRow = sheet.createRow(lineCount);
        }
        // Initialize the new record with the fixed fields
        xlsRow = writeOutItem((String)row[0], xlsRow);
        xlsRow = writeOutItem((String)row[1], xlsRow);
        xlsRow = writeOutItem((String)row[3], xlsRow);
        xlsRow = writeOutItem((String)row[4], xlsRow);
      }
      xlsRow = writeOutItem((String)row[6], xlsRow);
      oldItemOrder = newItemOrder;
    }
    return sheet;
  }



  /**
   * Builds up a sheet with the data of answers for simple questions
   * @param prjCode the project code
   * @param intrvId the database questionnaire id
   * @param grpId the database group id
   * @param secOrder the number of section in the questionnaire
   * @return a xlsx sheet with all rows for the answers to simple questions
   */
  public XSSFSheet xlsxSimpleAnswersDump (String prjCode, Integer intrvId,
                                          Integer grpId, Integer secOrder, XSSFSheet sheet) {

// Subject|Project|Questionnaire|Section";
    List<Object[]> rs = getAnswers4SimpleItems(prjCode, intrvId, grpId, secOrder);
    Iterator<Object[]> itRs = rs.iterator();

    int oldItemOrder = 9999, newItemOrder;
    int lineCount = 1;
    Row xlsRow = sheet.createRow(lineCount);
    // how to add elements according the header
    // i. as queries are ordered, don't have to do anything
    // ii. keeping a data structure in memory to enforce matching header<->answers

    while (itRs.hasNext()) {
      Object[] row = itRs.next();
      if (row[8] instanceof Integer)
        newItemOrder = ((Integer)row[8]);
      else
        newItemOrder = Integer.parseInt((String)row[8]);

      if (newItemOrder < oldItemOrder) { // start new subject
        if (xlsRow.getLastCellNum()-1 > 0) {
          lineCount++;
          xlsRow = sheet.createRow(lineCount);
        }
        // Initialize the new record with the fixed fields
        xlsRow = writeOutItem((String)row[0], xlsRow);
        xlsRow = writeOutItem((String)row[1], xlsRow);
        xlsRow = writeOutItem((String)row[3], xlsRow);
        xlsRow = writeOutItem((String)row[4], xlsRow);
      }
      xlsRow = writeOutItem((String)row[6], xlsRow);
      oldItemOrder = newItemOrder;
    }

    return sheet;
  }


  /**
   * It yields (num repeatable blocks + 1) sheets.
   * @param prjCode the project code
   * @param intrvId the database questionnaire id
   * @param grpId the database group id
   * @param secOrder the number of section in the questionnaire
   */
  public void composeXlsxSectionRepBlocks (String prjCode, Integer intrvId,
                                           Integer grpId, Integer secOrder) {

    repIds = repIds.equalsIgnoreCase("")?
              getRepeatableItems(prjCode, intrvId, secOrder):
              repIds;

    if (repIds.length() > 0) {
      String[] blockParents = repIds.split(",");

      for (int i=0; i<blockParents.length; i++) {
        Integer parentItem = Integer.parseInt(blockParents[i]);
        XSSFSheet sheet = xlsxRepBlockDump(prjCode, intrvId, grpId, secOrder, parentItem);
      }
    }

  }



  /**
   * Builds up a dump by slicing the section in different files (or excel sheets)
   * containing the data belonging to repeatable blocks plus and additional file
   * with data from simple questions (no repeatables). This is the main method to
   * yield the final dump file
   * @param prjCode, the project code
   * @param intrvId, the interview database id
   * @param grpId, the group database id
   * @param orderSec, the number of the section
   */
  public XSSFWorkbook getRepBlocksDump (String prjCode, String intrvId,
                                        String grpId, Integer orderSec)
                                      throws IOException {

    // First, get the not-repeating items header
    String headerStr = buildHeaderSimpleItems(prjCode, Integer.parseInt(intrvId), orderSec);
    String[] header = {};
    header = (headerStr == null || headerStr.length() == 0)? header: headerStr.split("\\|");

    if (header.length > 0) {
      XSSFSheet simpleSheet = this.wb.createSheet();
      simpleSheet = writeOutHeader(header, simpleSheet);
      // Get the answers for all patients for these questions
      simpleSheet = xlsxSimpleAnswersDump(prjCode, Integer.parseInt(intrvId),
                                          Integer.parseInt(grpId), orderSec, simpleSheet);
    }

    // Then, get the remainder items (repeating blocks) to add to the excel
    // For each block, HEADER + ANSWERS = xlsx sheet
    composeXlsxSectionRepBlocks(prjCode, Integer.parseInt(intrvId),
                                Integer.parseInt(grpId), orderSec);

    FileOutputStream fileOut = new FileOutputStream("resources/workbook.xlsx");
    wb.write(fileOut);
    fileOut.close();

    return wb;
  }



  /**
   * Write out the header (an array of strings) and returns either the sheet or null
   * if what is request is just a console dump of the header
   * @param header, the header as an array of Strings
   * @param sheet, the xlsx sheet to set the header
   * @return the modified/filled sheet
   */
  public XSSFSheet writeOutHeader (String[] header, XSSFSheet sheet) {

    if (sheet == null)
      System.out.println(header);
    else {
      Row row = sheet.createRow((short)0);
      for (int i=0; i<header.length; i++)
        row.createCell(i).setCellValue(header[i]);
    }

    return sheet;
  }



  /**
   * Append a value to the xlsx row as parameter or just print out to console the
   * value if xlsRow param is null
   * @param item, the item to be printed out
   * @param xlsRow, the xlsx row holding the cells
   * @return the row with the value appended or null if xlsRow is null
   */
  protected Row writeOutItem (String item, Row xlsRow) {
    if (xlsRow == null)
      System.out.println(item);
    else {
      short cellIndex = xlsRow.getLastCellNum() == -1? 0: xlsRow.getLastCellNum();
      xlsRow.createCell(cellIndex).setCellValue(item);
    }
    return xlsRow;
  }




}
