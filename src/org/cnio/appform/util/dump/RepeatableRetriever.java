package org.cnio.appform.util.dump;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.cnio.appform.entity.AppGroup;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 * This class yields an xlsx file based on repeatable blocks for the section. Each
 * repeatable block dump will be a sheet in the workbook file, and there will be
 * an additional sheet for the non-repeatable questions. Within those workbooks,
 * every row will be a repeatable block for the patient. In such a way, curating gets
 * easier than doing it in the 'normal' dump format.
 */
public class RepeatableRetriever extends DataRetriever {

  protected SXSSFWorkbook wb;
  protected final int FIRST_FIELD_INDEX = 4;

  /**
   * MAX_QUERY_SIZE is the max number of rows to retrieve in one query
   */
  private final int MAX_QUERY_SIZE = 30000;
  /**
   * queryTimes is the number of times the query for rows was called. This is
   * necessary in order to get the next set of rows, which will has an offset
   * MAX_QUERY_SIZE*queryTimes
   */
  private int queryTimes = 0;

  // counter for blocks, used to name the sheets in the excel
  private int blockCount = 0;


  protected SqlDataRetriever sdr;

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
  // " limit "+this.MAX_QUERY_SIZE;


  protected String simpleAnswersQry = "select p.codpatient, pj.\"name\" as prjname, g.name as grpname, i.name as intrvname, s.name as secname, " +
    "q.codquestion as codq, a.thevalue, s.section_order, it.item_order, pga.answer_number, " +
    "pga.answer_order, it.repeatable as itrep " +
    "from patient p, pat_gives_answer2ques pga, appgroup g, performance pf, " +
    "  question q, answer a, interview i, item it, section s, project pj " +
    "where gggg " +
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

  public RepeatableRetriever (String path, Hashtable map) {
    super (path, map);

    wb = new SXSSFWorkbook(100);
  }


  public RepeatableRetriever() {
    super();
    wb = new SXSSFWorkbook(100);
    repIds = "";

    sdr = new SqlDataRetriever();
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

    this.repIds = this.repIds.equalsIgnoreCase("")?
      getRepeatableItems(prjCode, intrvId, secOrder): this.repIds;

    String repIdsQry = this.repIds.equalsIgnoreCase("")? "":
      "or it.ite_iditem not in ("+this.repIds+") ";

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
    List<Object[]> rs = super.execQuery(qry, -1, -1);
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
   * @param grpId the database group id or a comma separated list of group ids
   * @param secOrder the number of section in the questionnaire
   * @param parentItem the item which is the parent for the repeatable block
   * @return a list of Object[] with the resultset content
   */
  public ResultSet getAnswersForRepBlock (String prjCode, Integer intrvId,
                                          String grpId, Integer secOrder, Integer parentItem)
    throws SQLException {

    String secParam = (secOrder == null)?"s.section_order ": secOrder.toString();
    String grpParam = (grpId == null? "1=1 ": "g.idgroup in ("+grpId.toString()+")");
    String qry = repBlockAnswersQry;
    qry = qry.replaceAll("ssss", secParam)
      .replaceAll("gggg", grpParam)
      .replaceAll("pppp", prjCode)
      .replaceAll("hhhh", parentItem.toString())
      .replaceAll("iiii", intrvId.toString());
    // qry += " offset "+ (MAX_QUERY_SIZE*queryTimes)+";";
    System.out.println(this.queryTimes+": "+qry);
    // List<Object[]> rs = execQuery(qry, MAX_QUERY_SIZE*queryTimes, MAX_QUERY_SIZE);

    ResultSet scrollRs = this.sdr.getScrollableRS(qry);
    // List <Object[]> rs = execQuery(qry, -1, -1);
    System.out.println("Qry done!");

    return scrollRs;
  }



  /**
   * Get the answers for the items which are not in repeatable blocks and they are not
   * repeatables themselves
   * @param prjCode the project code
   * @param intrvId the database questionnaire id
   * @param grpId the database group id or a comma separated list of group ids
   * @param secOrder the number of section in the questionnaire
   * @return a list with the retrieved answers
   */
  public ResultSet getAnswers4SimpleItems (String prjCode, Integer intrvId,
                                           String grpId, Integer secOrder) {

    String secParam = (secOrder == null)?"s.section_order ": secOrder.toString();
    String grpParam = (grpId == null? "1=1 ": "g.idgroup in ("+grpId.toString()+")");

    this.repIds = this.repIds.equalsIgnoreCase("")?
      getRepeatableItems(prjCode, intrvId, secOrder):
      this.repIds;
    String repIdsQry = this.repIds.equalsIgnoreCase("")? "":
      "or it.ite_iditem not in ("+this.repIds+") ";

    String qry = simpleAnswersQry;
    qry = qry.replaceAll("ssss", secParam)
      .replaceAll("gggg", grpParam)
      .replaceAll("pppp", prjCode)
      .replaceAll("iiii", intrvId.toString())
      .replaceAll("rrrr", repIdsQry);

    // List<Object[]> rs = execQuery(qry, MAX_QUERY_SIZE*queryTimes, MAX_QUERY_SIZE);
    ResultSet scrollRs = this.sdr.getScrollableRS(qry);
    return scrollRs;

  }





  /**
   * Builds up a sheet with the data of a repeatable block
   * @param prjCode the project code
   * @param intrvId the database questionnaire id
   * @param grpId the database group id or a comma separated list of group ids
   * @param secOrder the number of section in the questionnaire
   * @param parentItem the item which is the parent for the repeatable block
   * @return a xlsx sheet with all rows for the repeatable block
   */
  public Sheet xlsxAnswerDump (String prjCode, Integer intrvId,
                               String grpId, Integer secOrder, Integer parentItem)
    throws SQLException {

    String sheetName;
    // first, the header
    String headerStr;
    if (parentItem == null) {
      headerStr = buildHeaderSimpleItems(prjCode, intrvId, secOrder);
      sheetName = "Simples";
    }
    else {
      headerStr = buildRepBlockHeader(parentItem);
      this.blockCount++;
      sheetName = "Block_"+this.blockCount;
    }
    if (headerStr == null || headerStr.length() == 0)
      return null;

    String[] header = {};
    header = headerStr.split("\\|");
    // XSSFSheet sheet = this.wb.createSheet(sheetName);
    Sheet sheet = this.wb.createSheet(sheetName);
    sheet = writeOutHeader(header, sheet);

    // Then, dump all answers for the items
    // Queries retrieve
    int oldItemOrder = 9999, newItemOrder;
    int lineCount = 1;
    Row xlsRow = sheet.createRow(lineCount);

    ResultSet scrollRs;
    Object[] row;
    boolean moreResults = true;
    // this.queryTimes = 0;

    if (parentItem == null)
      scrollRs = getAnswers4SimpleItems(prjCode, intrvId, grpId, secOrder);
    else
      scrollRs = getAnswersForRepBlock(prjCode, intrvId, grpId, secOrder, parentItem);

    // how to add elements according the header
    // i. as queries are ordered, don't have to do anything
    // ii. keeping a data structure in memory to enforce matching header<->answers

    // Object[] row;
    long ini = new Date().getTime();
    while (scrollRs.next()) {
      // row = rs.remove(0);
      if (scrollRs.getObject(9) instanceof Integer)
        newItemOrder = scrollRs.getInt(9);
      else
        newItemOrder = Integer.parseInt(scrollRs.getString(9));

      if (newItemOrder < oldItemOrder) { // start new subject
        if (xlsRow.getLastCellNum()-1 > 0) {
          lineCount++;
          xlsRow = sheet.createRow(lineCount);
        }
        // Initialize the new record with the fixed fields
        xlsRow = writeOutItem(scrollRs.getString(1), xlsRow);
        xlsRow = writeOutItem(scrollRs.getString(2), xlsRow);
        xlsRow = writeOutItem(scrollRs.getString(4), xlsRow);
        xlsRow = writeOutItem(scrollRs.getString(5), xlsRow);
      }
      // xlsRow = writeOutItem((String)row[6], xlsRow);
      xlsRow = writeOutItem(scrollRs.getString(7), xlsRow);
      oldItemOrder = newItemOrder;
      this.queryTimes++;
    }
    long end = new Date().getTime();
    System.out.println("Time processing resultset: "+(end-ini)+" ms");

    return sheet;
  }



  /**
   * It yields (num repeatable blocks) sheets.
   * @param prjCode the project code
   * @param intrvId the database questionnaire id
   * @param grpId the database group id or a comma separated list of group ids
   * @param secOrder the number of section in the questionnaire
   */
  public void composeXlsxSectionRepBlocks (String prjCode, Integer intrvId,
                                           String grpId, Integer secOrder)
    throws SQLException {
    this.repIds = this.repIds.equalsIgnoreCase("")?
      getRepeatableItems(prjCode, intrvId, secOrder):
      this.repIds;

    if (this.repIds.length() > 0) {
      String[] blockParents = this.repIds.split(",");

      for (int i=0; i<blockParents.length; i++) {
        Integer parentItem = Integer.parseInt(blockParents[i]);
        // The next method creates a sheet for a block, header included
        // XSSFSheet sheet = xlsxRepBlockDump(prjCode, intrvId, grpId, secOrder, parentItem);
        xlsxAnswerDump(prjCode, intrvId, grpId, secOrder, parentItem);
      }
    }
  }



  /**
   * Builds up a dump by slicing the section in different files (or excel sheets)
   * containing the data belonging to repeatable blocks plus and additional file
   * with data from simple questions (no repeatables). This is the main method to
   * yield the final dump file and, currently, it yields a Excel workbook object
   * containing (num-of-rep-blocks + 1) excel sheets
   * @param prjCode, the project code
   * @param intrvId, the interview database id
   * @param grpId the database group id or a comma separated list of group ids
   * @param orderSec, the number of the section
   */
  public SXSSFWorkbook getRepBlocksDump (String prjCode, String intrvId,
                                         String grpId, Integer orderSec)
    throws IOException, SQLException {

    // First, get the not-repeating items
    // xlsxSimpleAnswersDump(prjCode, Integer.parseInt(intrvId),
    //                 Integer.parseInt(grpId), orderSec);
    xlsxAnswerDump(prjCode, Integer.parseInt(intrvId),
      grpId, orderSec, null);

    // Then, get the remainder items (repeating blocks) to add to the excel
    // For each block, HEADER + ANSWERS = xlsx sheet
    composeXlsxSectionRepBlocks(prjCode, Integer.parseInt(intrvId),
      grpId, orderSec);
    this.sdr.closeConn();

    return this.wb;
  }



  /**
   * Write out the header (an array of strings) and returns either the sheet or null
   * if what is request is just a console dump of the header
   * @param header, the header as an array of Strings
   * @param sheet, the xlsx sheet to set the header
   * @return the modified/filled sheet
   */
  protected Sheet writeOutHeader (String[] header, Sheet sheet) {

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
