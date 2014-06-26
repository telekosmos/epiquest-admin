package org.cnio.appform.test.util;

import org.cnio.appform.entity.Interview;
import org.cnio.appform.entity.Patient;
import org.cnio.appform.util.HibernateUtil;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class HibernateUtilTest {

    Session hibSes;

    @Before
    public void setUp() throws Exception {
      // super.setUp();
      hibSes = HibernateUtil.getSessionFactory().openSession();
    }

    @After
    public void tearDown () {
      hibSes.close();
    }

    @Test
    public void testGetSubjectsPrjGrp1() throws Exception {
      int prjId = 50;
      Integer grpId = null;
      String prjCode = "157", grpCode = "01", typeSubj = "1";

      List<Patient> list = HibernateUtil.getPatiens4ProjsGrps(hibSes, prjCode, grpCode, typeSubj);
      // assertNotNull("no interviews retrieved", list);
      assertThat(list, notNullValue());
    }

    @Test
    public void testGetSubjectsPrjGrp2() throws Exception {
      int prjId = 50;
      Integer grpId = null;
      String prjCode = "157", grpCode = "01", typeSubj = "1";

      List<Patient> list = HibernateUtil.getPatiens4ProjsGrps(hibSes, prjCode, grpCode, typeSubj);
      // assertNotNull("no interviews retrieved", list);
      assertThat(list, notNullValue());
    }

    @Test
    public void testGetSubjectsPrjGrp3() throws Exception {
      int prjId = 50;
      Integer grpId = null;
      String prjCode = "188", grpCode = "01", typeSubj = "1";

      List<Patient> list = HibernateUtil.getPatiens4ProjsGrps(hibSes, prjCode, grpCode, typeSubj);
      // assertNotNull("no interviews retrieved", list);
      assertThat(list, notNullValue());
    }

    @Test
    public void testGetSubjectsPrjGrp4() throws Exception {
      int prjId = 50;
      Integer grpId = null;
      String prjCode = "157", grpCode = "08", typeSubj = "1";

      List<Patient> list = HibernateUtil.getPatiens4ProjsGrps(hibSes, prjCode, grpCode, typeSubj);
      // assertNotNull("no interviews retrieved", list);
      assertThat(list, notNullValue());
    }

    @Test
    public void testGetSubjectsPrjGrp5() throws Exception {
      int prjId = 50;
      Integer grpId = null;
      String prjCode = "162", grpCode = "09", typeSubj = "1";

      List<Patient> list = HibernateUtil.getPatiens4ProjsGrps(hibSes, prjCode, grpCode, typeSubj);
      // assertNotNull("no interviews retrieved", list);
      assertThat(list, notNullValue());
    }
}
