package org.cnio.appform.test.util.dump;

import junit.framework.TestCase;
import org.cnio.appform.util.dump.TransposedDataRetriever;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.TreeMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.mock;


/**
 * Non-unit tests for TransposedDataRetriever as database is involved.
 * Mocking database huge amount of data is tricky, so the tests are built upon
 * a specific database resultset. The resultset should have the same format always,
 * with a different number of rows only.
 * Para hacerlo unitario de verdad habría que preparar un ejemplo de resultset un
 * poco grande a partir de uno real y mockear todos los métodos de la clase ResultSet,
 * y si tal ni aún así
 */
public class TransposedDataRetrieverTest {

  protected TransposedDataRetriever tdr;
  protected String prjCode = "157";
  protected String intrvId = "50";
  protected String grpId = "400"; // 400 12Octubre, 304 01HospitalDelMar, 401 Cajal
  protected Integer orderSec = 4;
  protected Object[] mockRow = {"157081001","Hospital 12 de Octubre","QES_Español","C. Tabaco","C1","1",4,2,1,1,0};

  @Before
  public void setUp() throws Exception {
    tdr = new TransposedDataRetriever();
  }

  @After
  public void tearDown() throws Exception {
    tdr = null;
  }



  @Test
  public void testGetResultSetBy() throws Exception {
    System.out.println("TransposedDataRetrieverTest.testGetResultSetBy");
    java.sql.ResultSet rs = tdr.getResultSetBy(prjCode, intrvId, grpId, orderSec);
    assertThat(rs, notNullValue());

    // test for order of elements
    String currentPat = "", newPat;
    ArrayList<Integer> orderedItems = new ArrayList<Integer>();

    java.sql.ResultSetMetaData rmd = rs.getMetaData();
    int columnCount = rmd.getColumnCount(), countRows = 0;

    // loop over the resultset
    while (rs.next()) {
      countRows++;

      String refSubject = tdr.getSubject(rs);
      assertThat(refSubject, notNullValue());
      assertThat(refSubject, not(""));
      if (countRows == 1)
        assertThat(refSubject, equalTo("157081001"));

      newPat = rs.getString(1);
      assertThat(refSubject, equalTo(newPat));

      if (!newPat.equalsIgnoreCase(currentPat)) {
        ArrayList<Integer> sorted = (ArrayList)orderedItems.clone();
        Collections.sort(sorted);

        assertThat(orderedItems, equalTo(sorted));
        orderedItems.clear();
        currentPat = newPat;
      }
      else {
        orderedItems.add(rs.getInt(7));
      }
    }

    System.out.println("rows: "+countRows);

  }


  @Test
  public void testGetValuesForSubj () throws Exception{
    String refSubj = "157081001";

    java.sql.ResultSet rs = tdr.getResultSetBy(prjCode, intrvId, grpId, orderSec);
    assertThat(rs, notNullValue());
    java.sql.ResultSetMetaData rmd = rs.getMetaData();
    int columnCount = rmd.getColumnCount();
    rs.next();

    ArrayList<String> subjectVals = tdr.getValuesForSubj(rs,refSubj, columnCount);
    assertThat(subjectVals, notNullValue());
    assertThat(subjectVals.size(), equalTo(22));
    for (int i=0; i<subjectVals.size(); i++) {
      String[] vals = subjectVals.get(i).split("\\|");
      assertThat(vals[0], equalTo("\""+refSubj+"\""));
    }
  }



  @Test
  public void testGetValuesForSubj157081003 () throws Exception {
    String refSubj = "157081003";
    java.sql.ResultSet rs = tdr.getResultSetBy(prjCode, intrvId, grpId, orderSec);
    assertThat(rs, notNullValue());
    java.sql.ResultSetMetaData rmd = rs.getMetaData();
    int columnCount = rmd.getColumnCount();

    // according with the parameters and current db state, the subject few results
    // starts in row 28
    rs.absolute(28);
    ArrayList<String> subjectVals = tdr.getValuesForSubj(rs,refSubj, columnCount);
    assertThat(subjectVals, notNullValue());
    assertThat(subjectVals.size(), equalTo(3));
    for (int i=0; i<subjectVals.size(); i++) {
      String[] vals = subjectVals.get(i).split("\\|");
      assertThat(vals[0], equalTo("\""+refSubj+"\""));
    }

  }



  @Test
  public void testYieldResultRow () {
    String rowResult = tdr.yieldResultRow(mockRow);

    assertThat(rowResult, notNullValue());
    String[] items = rowResult.split("\\|");
    assertThat(items.length, equalTo(3));
    assertThat("no patient code", items[0], org.hamcrest.Matchers.startsWith("\"157"));
  }


  @Test
  public void testYieldHeader () {

    String hdr = tdr.yieldHeader(mockRow);
    assertThat(hdr, notNullValue());
    assertThat(hdr, containsString("|"));

    String[] headerItems = hdr.split("\\|");
    assertThat(headerItems.length, equalTo(4));
    assertThat(headerItems[2], equalTo("C. Tabaco"));
  }


  @Test
  public void testTransposedRS () throws SQLException {
    Object[][] mockRs = {
      {"157081001","Hospital 12 de Octubre","QES_Español","C. Tabaco","C4B","30",4,26,1,1,0},
      {"157081001","Hospital 12 de Octubre","QES_Español","C. Tabaco","C5B","2",4,27,1,1,0},
      {"157081001","Hospital 12 de Octubre","QES_Español","C. Tabaco","C7B","20",4,30,1,1,0},
      {"157081001","Hospital 12 de Octubre","QES_Español","C. Tabaco","C7B","1",4,30,2,1,0},
      {"157081001","Hospital 12 de Octubre","QES_Español","C. Tabaco","C8B","2",4,31,1,1,0},
      {"157081001","Hospital 12 de Octubre","QES_Español","C. Tabaco","C9B","1",4,32,1,1,0},
      {"157081001","Hospital 12 de Octubre","QES_Español","C. Tabaco","C10","2",4,34,1,1,0},
      {"157081001","Hospital 12 de Octubre","QES_Español","C. Tabaco","C12","2",4,37,1,1,0},
      {"157081001","Hospital 12 de Octubre","QES_Español","C. Tabaco","C13","9999",4,39,1,1,0},
      {"157081002","Hospital 12 de Octubre","QES_Español","C. Tabaco","C1","2",4,2,1,1,0},
      {"157081002","Hospital 12 de Octubre","QES_Español","C. Tabaco","C1A","9999",4,5,1,1,0},
      {"157081002","Hospital 12 de Octubre","QES_Español","C. Tabaco","C10","2",4,34,1,1,0},
      {"157081002","Hospital 12 de Octubre","QES_Español","C. Tabaco","C11","9999",4,36,1,1,0},
      {"157081002","Hospital 12 de Octubre","QES_Español","C. Tabaco","C12","2",4,37,1,1,0},
      {"157081003","Hospital 12 de Octubre","QES_Español","C. Tabaco","C1","2",4,2,1,1,0},
      {"157081003","Hospital 12 de Octubre","QES_Español","C. Tabaco","C10","2",4,34,1,1,0},
      {"157081003","Hospital 12 de Octubre","QES_Español","C. Tabaco","C12","2",4,37,1,1,0},
      {"157081004","Hospital 12 de Octubre","QES_Español","C. Tabaco","C1","1",4,2,1,1,0},
      {"157081004","Hospital 12 de Octubre","QES_Español","C. Tabaco","C1A","1",4,5,1,1,0},
      {"157081004","Hospital 12 de Octubre","QES_Español","C. Tabaco","C1B","3",4,6,1,1,0}
    };

    // TransposedDataRetriever mockTdr = mock(TransposedDataRetriever.class);
    TreeMap<String, ArrayList> res = tdr.getTransposedRS(prjCode, intrvId, grpId, orderSec);
    assertThat(res, notNullValue());
    assertThat(res, hasKey("HEADER"));
    assertThat(res, hasKey("157081001"));

    ArrayList<String> subj081001 = (ArrayList<String>)res.get("157081001"),
      subj081003 = (ArrayList<String>)res.get("157081003");
    assertThat(subj081001.size(), equalTo(22));
    assertThat(subj081003, notNullValue());
    assertThat(subj081003.size(), equalTo(3));

    for (int i=0; i < subj081001.size(); i++)
      assertThat(subj081001.get(i), Matchers.startsWith("\"157081001"));

    for (int i=0; i < subj081003.size(); i++)
      assertThat(subj081003.get(i), Matchers.startsWith("\"157081003"));
  }


  @Test
  public void testWriteOutDump () throws Exception {
    String fileContents = tdr.writOutDump(prjCode, intrvId, grpId, orderSec);

    assertThat(fileContents, notNullValue());
    assertThat(fileContents, not(equalTo("")));
    String[] fileLines = fileContents.split("\\n");

    assertThat(fileLines.length, greaterThan(0));
    assertThat(fileLines[0], not(containsString("157"))); // header
    assertThat(fileLines[1], containsString("157"));
    assertThat(fileLines[1], containsString("|"));

    for (int i=1; i<fileLines.length; i++) {
      assertThat(fileLines[i], Matchers.startsWith("\"157"));
      assertThat(fileLines[i], containsString("|"));
    }

    // System.out.println(fileContents);
  }
}
