package org.cnio.appform.util.dump;

import java.sql.SQLException;
import java.util.*;
import java.sql.ResultSet;

/**
 * Class TransposedDataRetriever
 *
 * It yields data from a dump with the following dump file format:
 * GroupName | InterviewName | SectionName | SectionOrder
 * CodPatient1 | CodQues1-ansNumber-ansOrder | Value1
 * Codpatient1 | CodQues2-ansNumber-ansOrder | Value2
 * .
 * .
 * .
 * CodPatientN | CodQuesI-ansNumber-ansOrder | ValueI
 */
public class TransposedDataRetriever extends DataRetriever {

  public TransposedDataRetriever () {
    super ();
  }

  public TransposedDataRetriever (String path, Hashtable map) {
    filePath = path;
    mapVarNames = map;

    dw = new DataWriter (mapVarNames);
  }


  /**
   * Gets the answer values for all subjects which fits the parameters.
   * @param prjCode, a project code
   * @param intrvId, the interview database id (as interview name can be troublesome)
   * @param grpId, the group database id (same as interviews)
   * @param orderSec, the number of the section (again, name can be troublesome)
   * @return java.sql.Resultset for exploring the data
   */
  public ResultSet getResultSetBy (String prjCode, String intrvId,
                                      String grpId, Integer orderSec) {
    SqlDataRetriever sqldr = new SqlDataRetriever();
    java.sql.ResultSet rs = sqldr.getFullResultSet(prjCode, Integer.parseInt(intrvId),
      Integer.parseInt(grpId), orderSec);

    return rs;
  }



  /**
   * Converts a row in the resultset into an object array
   * @param rs, the resultset with the cursor in the right position
   * @param columnCount, the number of columns for the resultset rows
   * @return
   * @throws SQLException
   */
  public Object[] row2Array (ResultSet rs, int columnCount) throws SQLException {
    Object[] innRow = new Object[columnCount];
    for (int cols=0; cols < columnCount; cols++) {
      innRow[cols] = rs.getObject(cols+1);
    }

    return innRow;
  }



  /**
   * Convenient method to get the subject field from teh resultset. Mostly for
   * testing purposes only.
   * @param rs, the resultset with the cursor correctly positioned
   * @return an string with the subject in the current row in the resultset
   */
  public String getSubject (ResultSet rs) {
    try {
      java.sql.ResultSetMetaData rmd = rs.getMetaData();
      int columnCount = rmd.getColumnCount();

      Object[] row = this.row2Array(rs, columnCount);
      return (String)row[0];
    }
    catch (SQLException sqlEx) {
      return null;
    }
  }



  /**
   * It yields the header as a string with the format
   * GroupName | InterviewName | SectionName | SectionOrder
   *
   * @param row an {Object} array with the contents of (the first) row
   * @return a {String} correctly formatted
   */
  public String yieldHeader (Object[] row) {

    String header = row[1]+"|"+row[2]+"|"+row[3]+"|"+row[6];
    return header;
  }



  /**
   * It returns an string which will be one row subject|question_coords|value in
   * the final format
   * @param row
   * @return
   */
  public String yieldResultRow (Object[] row) {
    String subject = row[0].toString(),
           questionCoords = (String)row[4]+"-"+row[9].toString()+"-"+row[8].toString();
    String answerValue = (String)row[5];

    StringBuffer strOut = new StringBuffer();
    strOut.append("\""+subject+"\"|");
    strOut.append("\""+questionCoords+"\"|");
    strOut.append("\""+answerValue+"\"");

    return strOut.toString();
  }



  /**
   * Fetchs the values for the reference subject and returns a list with them, same
   * order than the resultset.
   * The cursor has to be placed in the very first result for the reference subject
   * @param rs, the resultset with all results
   * @param refSubj, the reference subject
   * @param columnCount, the number of columns for each row in the resultstet
   * @return a list with all values for the reference subject
   * @throws SQLException
   */
  public ArrayList<String> getValuesForSubj (ResultSet rs, String refSubj, int columnCount) throws SQLException{
    String currentSubj = refSubj, rowResult;
    Object[] rowArray;
    ArrayList subjectVals = new ArrayList();

    rowArray = this.row2Array(rs, columnCount);
    currentSubj = (String)rowArray[0];
    while (currentSubj.equalsIgnoreCase(refSubj)) {
      rowResult = this.yieldResultRow(rowArray);
      subjectVals.add(rowResult);
      if (rs.next()) {
        rowArray = this.row2Array(rs, columnCount);
        currentSubj = (String)rowArray[0];
      }
      else
        break;
    }

    return subjectVals;
  }



  /**
   * Gets a hash of hashes; the first hash key is the patient and the value is the
   * second hash, which have the question codes as key and the answer values as
   * value. This is a representation of the final format for downloads:
   * a row a value.
   * It is prologed with a header
   * @param prjCode, the code of the project
   * @param intrvId, the database id of the interview
   * @param grpId, the database id of the group
   * @param orderSec, the number of the section
   */
  public TreeMap<String, ArrayList> getTransposedRS (String prjCode, String intrvId, String grpId,
                                                       Integer orderSec) throws SQLException {
    java.sql.ResultSet rs = this.getResultSetBy(prjCode, intrvId, grpId, orderSec);

    String header="", rowResult, currentSubj="", oldSubj="";
    java.sql.ResultSetMetaData rmd = rs.getMetaData();
    int columnCount = rmd.getColumnCount(), countRows = 0;
    TreeMap<String, ArrayList> fullRes = new TreeMap<String, ArrayList>();
    ArrayList<String> subjectValues = new ArrayList<String>();

    rs.next();
    header = yieldHeader(row2Array(rs, columnCount));
    String refSubj = this.getSubject(rs);
    while (!rs.isAfterLast() && refSubj != null) {
      subjectValues = this.getValuesForSubj(rs, refSubj, columnCount);
      fullRes.put(refSubj, subjectValues);
      refSubj = this.getSubject(rs);
    }
    /* processResultset (rs, columnCount
    while (rs.next()) {

      Object[] rowArray = this.row2Array(rs, columnCount);
      currentSubj = (String)rowArray[0];
      if (rs.isFirst()) {
        header = this.yieldHeader(rowArray);
        oldSubj = currentSubj;
      }


      if (!currentSubj.equalsIgnoreCase(oldSubj)) {
        fullRes.put(oldSubj, subjectValues);
        subjectValues.clear();
        oldSubj = currentSubj;
      }
      rowResult = this.yieldResultRow(rowArray);
      subjectValues.add(rowResult);
    } // EO while
    */

    ArrayList headerList = new ArrayList();
    headerList.add(header);

    fullRes.put("HEADER", headerList);
    return fullRes;
  }


  /**
   * Builds up an string which is ready to be dumped in a file. This will be the
   * actual dump file
   * @param prjCode, the project code
   * @param intrvId, the database interview id
   * @param grpId, the group id
   * @param orderSec, the number of section
   * @return the dump regarding to the parameters
   * @throws SQLException
   */
  public String writOutDump (String prjCode, String intrvId, String grpId,
                            Integer orderSec) throws SQLException {
    StringBuffer outputDump = new StringBuffer();
    TreeMap<String, ArrayList> fullRes = this.getTransposedRS(prjCode, intrvId, grpId, orderSec);

    outputDump.append(fullRes.get("HEADER").get(0)+"\n");
    for (String k: fullRes.keySet())
      if (!k.equalsIgnoreCase("HEADER"))
        for (String line: (ArrayList<String>)fullRes.get(k))
          outputDump.append(line+"\n");

    return outputDump.toString();
  }

}
