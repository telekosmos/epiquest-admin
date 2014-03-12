package org.cnio.appform.test.util.dump;

import org.cnio.appform.util.dump.RepeteableRetriever;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.endsWith;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.mock;

public class RepetibleRetrieverTest {
  protected String prjCode = "157";
  protected String intrvId = "50";
  protected String grpId = "400"; // 400 12Octubre, 304 01HospitalDelMar, 401 Cajal
  protected Integer orderSec = 8; // 12;
  protected RepeteableRetriever dr;

  protected Integer[] repeatableParents8 = {1469,1470,1603,1617};
  protected Integer[] repeatableParents10 = {1713, 2209, 2224};

  protected String resourcesPath = "/Users/telekosmos/DevOps/epiquest/epiquest-admin/resources";
  protected BufferedReader reader;

  @Before
  public void setUp() throws Exception {
    dr = new RepeteableRetriever();

    reader = new BufferedReader(new FileReader(resourcesPath+"/repeatable-blocks.txt"));
  }

  @After
  public void tearDown() throws Exception {
    dr = null;
  }


  @Test
  public void testBuildNormalRepHeader() throws Exception {
    LinkedHashMap<String, String> hdrList =
      dr.getRepeatHeader(prjCode, null, Integer.parseInt(intrvId), null, orderSec);

    Set<Map.Entry<String, String>> repSet = hdrList.entrySet();
    Iterator<Map.Entry<String, String>> itRep = repSet.iterator();

    while (itRep.hasNext()) {
      Map.Entry<String, String> entry = itRep.next();
      System.out.println(entry.getKey()+" -> "+entry.getValue());
    }
  }


  /**
   *
   * @throws Exception
   */
  @Test
  public void testRepBlockHeadingItems () throws Exception {
    String listReps = dr.getRepeatableItems(prjCode,Integer.parseInt(intrvId), orderSec);

    assertThat(listReps, notNullValue());
    assertThat(listReps.length(), greaterThan(0));
    assertThat(listReps.split(",").length, equalTo(4));
  }


  @Test
  public void buildHeader4RepBlock () throws Exception {
    Integer parentId = repeatableParents8[0];
    String header = dr.buildRepBlockHeader(prjCode, Integer.parseInt(intrvId),
                          orderSec, parentId);

    assertThat(header, notNullValue());
    assertThat(header.split("\\|"), not(emptyArray()));
    assertThat(header.split("\\|"), arrayWithSize(equalTo(5)));

    // no repeats this time
    parentId = repeatableParents8[1];
    header = dr.buildRepBlockHeader(prjCode, Integer.parseInt(intrvId),
                          orderSec, parentId);
    assertThat(header, nullValue());

    parentId = repeatableParents10[0];
    header = dr.buildRepBlockHeader(prjCode, Integer.parseInt(intrvId),
                          orderSec, parentId);
    System.out.println(header);
    assertThat(header, notNullValue());
    List listHdr = Arrays.asList(header.split("\\|"));
    List headerItems = listHdr.subList(3, listHdr.size());

    assertThat(header.split("\\|"), arrayWithSize(greaterThan(3)));
    // assertThat(headerItems, contains(anyOf(Matchers.endsWith("1"), Matchers.endsWith("2"))));
    // assertThat(headerItems, contains(Matchers.startsWith("I")));
  }


  @Test
  public void retrievedRepBlockAnswers () throws Exception {

    // List<Object[]> rs = dr.getAnswersForRepBlock(prjCode, Integer.parseInt(intrvId), Integer.parseInt(grpId), orderSec, 1713);
    orderSec = 10;
    Integer parentItem = 1713;
    List<Object[]> rs = dr.getAnswersForRepBlock(prjCode, Integer.parseInt(intrvId), null, orderSec, parentItem);
    assertThat(rs, notNullValue());
    assertThat(rs, hasSize(1000));

    Iterator<Object[]> itRs = rs.iterator();

    while (itRs.hasNext()) {
      String line = reader.readLine();
      if (line == null)
        break;
      line = line.replaceAll("\\|", ", ");
      String row = Arrays.toString(itRs.next());
      row = row.substring(1, row.length()-1);

      assertThat(line, equalTo(row));
    }
  }


}
