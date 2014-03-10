package org.cnio.appform.test.util.dump;

import junit.framework.TestCase;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

import org.cnio.appform.util.dump.DataRetriever;

import java.util.List;
public class DataRetrieverTest extends TestCase {

  DataRetriever dr;
  private String prjId = "157";
  private String intrvId = "50";
  private String grpId = "08"; // 08 12Octubre, 01HospitalDelMar
  private Integer orderSec = 4;

  @Before
  public void setUp() throws Exception {
    dr = new DataRetriever();
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

    assertEquals(orderSec, new Integer(4));
  }

  @Test
  public void testTransposedRS () throws Exception {


  }
}
