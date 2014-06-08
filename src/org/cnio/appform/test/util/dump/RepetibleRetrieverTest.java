package org.cnio.appform.test.util.dump;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.cnio.appform.entity.AppGroup;
import org.cnio.appform.util.HibernateUtil;
import org.cnio.appform.util.dump.RepeatableRetriever;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.sql.ResultSet;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RepetibleRetrieverTest {
  protected String prjCode = "157";
  protected String intrvId = "50";
  protected String aliqIntrvid = "4202";
  protected String grpId = "400"; // 400 12Octubre, 304 01HospitalDelMar, 401 Cajal
  protected String countryGrpId = "4";
  protected Integer orderSec = 8; // 12;
  protected RepeatableRetriever dr;

  protected Integer[] repeatableParents8 = {1469,1470,1603,1617};
  protected Integer[] repeatableParents10 = {1713, 2209, 2224};

  protected String resourcesPath = "/Users/telekosmos/DevOps/epiquest/epiquest-admin/resources";
  protected BufferedReader reader;
  protected String sec10Hdr = "Subject|Project|Questionnaire|Section|I1A_1|I1B_1|I1C_1|I1C_2|I1C_3|I1D_1|I1D_2|I1D_3|I1E_1|I1E_2|I1E_3|I1F_1|I1F_2|I1F_3|I1G_1|I1G_2|I1G_3|I1H_1|I1H_2|I1H_3";
  protected String sec8Hdr1 = "Subject|Project|Questionnaire|Section|G41A_1|G41_1_1";

  protected String xlsxFilename = "resources/workbooktest.xlsx";


  @Before
  public void setUp() throws Exception {
    dr = new RepeatableRetriever();
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
    String header = dr.buildRepBlockHeader(parentId);

    assertThat(header, notNullValue());
    assertThat(header.split("\\|"), not(emptyArray()));
//     assertThat(header.split("\\|"), arrayWithSize(equalTo(5)));
    System.out.println(header);

    // no repeats this time
    parentId = repeatableParents8[1];
    header = dr.buildRepBlockHeader(parentId);
    assertThat(header, nullValue());

    parentId = repeatableParents10[0];
    header = dr.buildRepBlockHeader(parentId);

    assertThat(header, notNullValue());
    // List listHdr = Arrays.asList(header.split("\\|"));
    // List headerItems = listHdr.subList(3, listHdr.size());

    assertThat(header.split("\\|"), arrayWithSize(greaterThan(3)));
    // assertThat(headerItems, contains(anyOf(Matchers.endsWith("1"), Matchers.endsWith("2"))));
  }


  @Test
  public void retrievedRepBlockAnswers () throws Exception {

    // List<Object[]> rs = dr.getAnswersForRepBlock(prjCode, Integer.parseInt(intrvId), Integer.parseInt(grpId), orderSec, 1713);
    orderSec = 10;
    Integer parentItem = 1713;
    ResultSet rs = dr.getAnswersForRepBlock(prjCode, Integer.parseInt(intrvId), null, orderSec, parentItem);
    String header = dr.buildRepBlockHeader(parentItem);
    System.out.println(header);
    assertThat(header, notNullValue());
    String[] headerArr = header.split("\\|");
    assertThat(headerArr, not(emptyArray()));

    assertThat(rs, notNullValue());
  }


  @Test
  public void simpleItemsHeader () throws Exception {
    String noRepIds = dr.buildHeaderSimpleItems(prjCode, Integer.parseInt(intrvId), 6);

    assertThat(noRepIds, not(isEmptyOrNullString()));
    String[] noRepIdsArr = noRepIds.split("\\|");
    assertThat(noRepIdsArr.length, greaterThan(3));
    System.out.println(noRepIds);
  }


  /*
  @Test
  public void xlsxDumpSimplesHeader () throws Exception {

    // Workbook wb = dr.getRepBlocksDump(prjCode, intrvId, grpId, orderSec);
    XSSFWorkbook wb = new XSSFWorkbook();
    XSSFSheet sheet = wb.createSheet("one");
    String noRepIds = dr.buildHeaderSimpleItems(prjCode, Integer.parseInt(intrvId), orderSec);
    sheet = dr.writeOutHeader(noRepIds.split("\\|"), sheet);

    assertThat(wb.getNumberOfSheets(), equalTo(1));
    assertThat(sheet.getLastRowNum(), equalTo(0));

    FileOutputStream fos = new FileOutputStream(xlsxFilename);
    wb.write(fos);
    fos.close();

    File f = new File(xlsxFilename);
    assertThat(f.exists(), equalTo(true));
  }
  */


  @Test
  public void xlsxSimmpleAnswersDump () throws Exception {

    // Workbook wb = dr.getRepBlocksDump(prjCode, intrvId, grpId, orderSec);
    // XSSFWorkbook wb = new XSSFWorkbook();
    // XSSFSheet sheet = wb.createSheet("one");
    /*
    String noRepIds = dr.buildHeaderSimpleItems(prjCode, Integer.parseInt(intrvId), orderSec);
    sheet = dr.writeOutHeader(noRepIds.split("\\|"), sheet);
    */

    //dr.xlsxSimpleAnswersDump(prjCode, Integer.parseInt(intrvId),
      //                              Integer.parseInt(grpId), orderSec);
    dr.xlsxAnswerDump(prjCode, Integer.parseInt(intrvId),
                      grpId, orderSec, null);
    Workbook wb = dr.getExcelWb();
    assertThat(wb.getNumberOfSheets(), equalTo(1));
    assertThat(wb.getSheetAt(0).getRow(0).getCell(0).getStringCellValue(), equalToIgnoringCase("subject"));
//    assertThat(sheet.getLastRowNum(), greaterThan(100));

    FileOutputStream fos = new FileOutputStream(xlsxFilename);
    wb.write(fos);
    fos.close();

    File f = new File(xlsxFilename);
    assertThat(f.exists(), equalTo(true));
  }


  @Test
  public void xlsxBlockDump () throws Exception {

    dr.composeXlsxSectionRepBlocks(prjCode, Integer.parseInt(intrvId),
                                  grpId, 12);

    Workbook wb = dr.getExcelWb();

    FileOutputStream fos = new FileOutputStream(xlsxFilename);
    wb.write(fos);
    fos.close();

    File f = new File(xlsxFilename);
    assertThat(f.exists(), equalTo(true));
  }


  @Test
  public void xlsxSectionDump () throws Exception {

    Workbook wb = dr.getRepBlocksDump(prjCode, intrvId, grpId, 12);
    FileOutputStream fileOut = new FileOutputStream("resources/workbook.xlsx");
    wb.write(fileOut);
    fileOut.close();

    File f = new File("resources/workbook.xlsx");
    assertThat(f.exists(), equalTo(true));

    assertThat(wb.getNumberOfSheets(), equalTo(4));

  }


  @Test
  public void xlsxSeciontDump4Country () throws Exception {
    grpId = "4";
    Workbook wb = dr.getRepBlocksDump(prjCode, intrvId, grpId, 12);
    FileOutputStream fileOut = new FileOutputStream("resources/workbook.xlsx");
    wb.write(fileOut);
    fileOut.close();

    File f = new File("resources/workbook.xlsx");
    assertThat(f.exists(), equalTo(true));

    assertThat(wb.getNumberOfSheets(), equalTo(4));
  }


  @Test
  public void xlsxSectionNoReps () throws Exception {
    Workbook wb = dr.getRepBlocksDump(prjCode, aliqIntrvid, grpId, 2);
    FileOutputStream fileOut = new FileOutputStream("resources/workbook.xlsx");
    wb.write(fileOut);
    fileOut.close();

    File f = new File("resources/workbook.xlsx");
    assertThat(f.exists(), equalTo(true));

    assertThat(wb.getNumberOfSheets(), equalTo(1));
  }


  @Test
  public void xlsxSectionNoSimples () throws Exception {
    Workbook wb = dr.getRepBlocksDump(prjCode, aliqIntrvid, grpId, 3);

    FileOutputStream fileOut = new FileOutputStream("resources/workbook.xlsx");
    wb.write(fileOut);
    fileOut.close();

    File f = new File("resources/workbook.xlsx");
    assertThat(f.exists(), equalTo(true));

    assertThat(wb.getNumberOfSheets(), equalTo(1));
  }


  @Test
  public void xlsxDump4Country () throws Exception {
    String grpIds = "1,2,3,304, 400, 401, 402, 403, 501, 800, 1302, 1350";

    SXSSFWorkbook wb = dr.getRepBlocksDump(prjCode, intrvId, grpIds, 12);

    FileOutputStream fileOut = new FileOutputStream("resources/fam-workbook.xlsx");
    wb.write(fileOut);
    fileOut.close();
    wb.dispose();

    File f = new File("resources/fam-workbook.xlsx");
    assertThat(f.exists(), equalTo(true));

    assertThat(wb.getNumberOfSheets(), equalTo(4));
  }


  @Test
  public void xlsxDumpFromServlet () throws Exception {
    Session hibSes = HibernateUtil.getSessionFactory().openSession();
    List<AppGroup> groups;
    AppGroup group = (AppGroup)hibSes.get(AppGroup.class, Integer.parseInt(countryGrpId));
    groups = HibernateUtil.getSecondaryGroups(hibSes, group);
    String grpIds = "";
    if (!groups.isEmpty()) {
      for (int i=0; i < groups.size(); i++)
        grpIds += groups.get(i).getId()+",";

      grpIds = grpIds.substring(0, grpIds.length()-1);
    }
    else
      grpIds = grpId;

    hibSes.close();
    this.prjCode = "188";
    this.aliqIntrvid = "4100";

    SXSSFWorkbook wb = dr.getRepBlocksDump(prjCode, aliqIntrvid, grpIds, 3);

    FileOutputStream fileOut = new FileOutputStream("resources/aliq-workbook.xlsx");
    wb.write(fileOut);
    fileOut.close();
    wb.dispose();

    File f = new File("resources/aliq-workbook.xlsx");
    assertThat(f.exists(), equalTo(true));

    assertThat(wb.getNumberOfSheets(), equalTo(1));

  }

/*
  @Test
  public void makeRepBlockDump () throws Exception {
    RepeatableRetriever spyRet = spy(dr);
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
*

  @Test
  public void makeSecRepBlockDump () throws Exception {
    System.out.println(prjCode+", "+intrvId+", null, "+orderSec);
    dr.printoutSectionRepblocksDump(prjCode, Integer.parseInt(intrvId),
      null, orderSec);
  }
*/

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
