package org.cnio.appform.test.util.dump;

import junit.framework.TestCase;
import org.cnio.appform.util.dump.RepeatableRetriever;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

import org.cnio.appform.util.dump.DataRetriever;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
public class DataRetrieverTest extends TestCase {
  /*
  DataRetriever dr;
  private String prjId = "157";
  private String intrvId = "50";
  private String grpId = "08"; // 08 12Octubre, 01HospitalDelMar
  private Integer orderSec = 4;
  */
  protected String prjCode = "157";
  protected String intrvId = "50";
  protected String grpId = "400"; // 400 12Octubre, 304 01HospitalDelMar, 401 Cajal
  protected Integer orderSec = 8; // 12;
  protected RepeatableRetriever dr;

  protected Integer[] repeatableParents8 = {1469,1470,1603,1617};
  protected Integer[] repeatableParents10 = {1713, 2209, 2224};

  protected String resourcesPath = "/Users/telekosmos/DevOps/epiquest/epiquest-admin/resources";


  @Before
  public void setUp() throws Exception {
    dr = new RepeatableRetriever();
  }

  @After
  public void tearDown() throws Exception {

  }

  @Test
  public void testGetPatients4Intrv() {
    String prjId = "157";
    String intrvId = "50"; // database Id
    int grpId = 304; // this is the id of DelMar
    List<Object[]> pats = dr.getPats4Intrv(prjId, Integer.parseInt(intrvId), grpId);

    assertNotNull(pats);
    assertThat(pats.size(), greaterThan(0));
  }


  @Test
  public void testGetDump() throws Exception {

    String filename = resourcesPath+"/testGetDump.csv";
    dr.getDump(prjCode, Integer.parseInt(intrvId), Integer.parseInt(grpId), orderSec, null, filename);

    File f = new File(filename);
    assertThat(f, notNullValue());
    assertThat(f.exists(), equalTo(true));
    assertThat(new Long(f.length()).intValue(), greaterThan(0));

    BufferedReader br = new BufferedReader(new FileReader(filename));
    String line = br.readLine();
    String currentPat="", oldPat="";
    while (line != null) {
      String[] fields = line.split("\\|");
      assertThat(new Integer(fields.length), greaterThan(1));
      currentPat = fields[0];
      assertThat(currentPat, not(equalTo(oldPat)));
      oldPat = currentPat;
      line = br.readLine();
    }
  }


  @Test
  public void testGetRepeatables() throws Exception {
    String repIds = dr.getRepeatableItems(prjCode, Integer.parseInt(intrvId), orderSec);

    assertThat(repIds, not(isEmptyOrNullString()));
    assertThat(repIds.matches("(\\d+)(,\\s*\\d+)*"), equalTo(true));


    String noRepIds = dr.getNoRepeatableItems(prjCode, Integer.parseInt(intrvId),orderSec, repIds);

    assertThat(noRepIds, not(isEmptyOrNullString()));
    assertThat(noRepIds.matches("(\\d+)(,\\s*\\d+)*"), equalTo(true));
    for (String repId: repIds.split(",")) {
      assertThat(noRepIds, not(containsString(repId)));
    }

    noRepIds = dr.getNoRepeatableItems(prjCode, Integer.parseInt(intrvId),orderSec, null);
    assertThat(noRepIds, not(isEmptyOrNullString()));
    assertThat(noRepIds.matches("(\\d+)(,\\s*\\d+)*"), equalTo(true));

  }

}
