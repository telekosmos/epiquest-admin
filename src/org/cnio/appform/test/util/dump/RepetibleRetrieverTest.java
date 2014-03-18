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
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.spy;

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
  protected String sec10Hdr = "Subject|Project|Questionnaire|Section|I1A_1|I1B_1|I1C_1|I1C_2|I1C_3|I1D_1|I1D_2|I1D_3|I1E_1|I1E_2|I1E_3|I1F_1|I1F_2|I1F_3|I1G_1|I1G_2|I1G_3|I1H_1|I1H_2|I1H_3";
  protected String sec8Hdr1 = "Subject|Project|Questionnaire|Section|G41A_1|G41_1_1";
  @Before
  public void setUp() throws Exception {
    dr = new RepeteableRetriever();

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
//     assertThat(header.split("\\|"), arrayWithSize(equalTo(5)));
    System.out.println(header);

    // no repeats this time
    parentId = repeatableParents8[1];
    header = dr.buildRepBlockHeader(prjCode, Integer.parseInt(intrvId),
                          orderSec, parentId);
    assertThat(header, nullValue());

    parentId = repeatableParents10[0];
    header = dr.buildRepBlockHeader(prjCode, Integer.parseInt(intrvId),
                          orderSec, parentId);

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
    String header = dr.buildRepBlockHeader(prjCode, Integer.parseInt(intrvId),
                                orderSec, parentItem);
    System.out.println(header);
    assertThat(header, notNullValue());
    String[] headerArr = header.split("\\|");
    assertThat(headerArr, not(emptyArray()));

    assertThat(rs, notNullValue());
    assertThat(rs, hasSize(1000));

  }


/*
  @Test
  public void makeRepBlockDump () throws Exception {
    RepeteableRetriever spyRet = spy(dr);
    List<Object[]> mockRS;
    String header;
    // mockRS = getMockedRS("sec10-repblocks-prj.txt");
    // header = sec10Hdr
    mockRS = getMockedRS("sec8-repblocks.txt");
    header = sec8Hdr1;

    when(spyRet.buildRepBlockHeader(anyString(),anyInt(),anyInt(),anyInt())).
      thenReturn(header);
    when(spyRet.getAnswersForRepBlock(anyString(), anyInt(), anyInt(), anyInt(),anyInt())).
      thenReturn(mockRS);

    // String header = spyRet.buildRepBlockHeader("1", 1, 1, 1);
    // assertThat(header, equalTo(sec10Hdr));
    spyRet.buildupRepBlockDump("1", 1, 1, 1, 1);

  }
*/

  @Test
  public void makeSecRepBlockDump () throws Exception {
    dr.buildupSectionRepblocksDump(prjCode, Integer.parseInt(intrvId),
      null, orderSec);
  }


  private List<Object[]> getMockedRS (String filename) throws Exception {

    reader = new BufferedReader(new FileReader(resourcesPath+"/"+filename));
    List<Object[]> mockedRS = new ArrayList<Object[]>();
    String line = reader.readLine();
    while (line != null) {
      Object[] row = line.split("\\|");
      mockedRS.add(row);
      line = reader.readLine();
    }
    reader.close();
    return mockedRS;
  }
}
