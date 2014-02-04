package org.cnio.appform.test.util;

import junit.framework.TestCase;
import org.cnio.appform.entity.Interview;
import org.cnio.appform.util.HibernateUtil;
import org.cnio.appform.util.IntrvController;
import org.hibernate.Session;

import java.util.List;


public class IntrvControllerTest extends TestCase {
  private IntrvController intrvCtrl;
  Session hibSes;

  public void setUp() throws Exception {
    super.setUp();
    hibSes = HibernateUtil.getSessionFactory().openSession();
    intrvCtrl = new IntrvController(hibSes);
  }


  public void tearDown () {
    hibSes.close();
  }

  public void testGetInterviews() throws Exception {
    int prjId = 50;
    Integer grpId = null;

    List<Interview> list = intrvCtrl.getInterviews(prjId, grpId);
    assertNotNull("no interviews retrieved", list);
  }
}
