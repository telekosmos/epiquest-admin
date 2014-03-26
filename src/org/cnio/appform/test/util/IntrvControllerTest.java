package org.cnio.appform.test.util;

import junit.framework.TestCase;
import org.cnio.appform.entity.AppGroup;
import org.cnio.appform.entity.Interview;
import org.cnio.appform.util.HibernateUtil;
import org.cnio.appform.util.IntrvController;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;


public class IntrvControllerTest {
  private IntrvController intrvCtrl;
  Session hibSes;

  @Before
  public void setUp() throws Exception {
    // super.setUp();
    hibSes = HibernateUtil.getSessionFactory().openSession();
    intrvCtrl = new IntrvController(hibSes);
  }

  @After
  public void tearDown () {
    hibSes.close();
  }

  @Test
  public void testGetInterviews() throws Exception {
    int prjId = 50;
    Integer grpId = null;

    List<Interview> list = intrvCtrl.getInterviews(prjId, grpId);
    // assertNotNull("no interviews retrieved", list);
    assertThat(list, notNullValue());
  }


  @Test
  public void secondaryGroups () throws Exception {
    int groupId = 4; // SPAIN

    AppGroup g = (AppGroup)hibSes.get(AppGroup.class, groupId);
    List<AppGroup> groups = HibernateUtil.getSecondaryGroups(hibSes,g);

    assertThat(groups, notNullValue());
    assertThat(groups.size(), greaterThan(4));
  }


  @Test
  public void noSecondaryGroups () throws Exception {
    int groupId = 304; // Hospital del Mar

    AppGroup g = (AppGroup)hibSes.get(AppGroup.class, groupId);
    List<AppGroup> groups = HibernateUtil.getSecondaryGroups(hibSes, g);

    assertThat(groups, emptyCollectionOf(AppGroup.class));

  }
}
