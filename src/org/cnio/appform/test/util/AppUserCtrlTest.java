package org.cnio.appform.test.util;

import org.cnio.appform.entity.AppUser;
import org.cnio.appform.util.HibController;
import org.cnio.appform.util.HibernateUtil;
import org.cnio.appform.util.AppUserCtrl;
import org.cnio.appform.entity.AppGroup;
import org.cnio.appform.util.IntrvController;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;
import java.util.List;

public class AppUserCtrlTest {

  private AppUserCtrl appUsrCtrl;
  Session hibSes;

  @Before
  public void setUp() throws Exception {
    // super.setUp();
    hibSes = HibernateUtil.getSessionFactory().openSession();
    appUsrCtrl = new AppUserCtrl(hibSes);
  }

  @After
  public void tearDown () {
    hibSes.close();
  }


  @Test
  public void getAllGroupsTest () {
    List<AppGroup> groups = appUsrCtrl.getAllGroups();
    AppGroup tert = (AppGroup)hibSes.get(AppGroup.class, 1400),
        terta = (AppGroup)hibSes.get(AppGroup.class, 1401);

    assertThat("foo", isIn(Arrays.asList("foo", "one", "two")));
    assertThat(groups, hasSize(greaterThan(10)));
    // assertThat(groups, hasSize(equalTo(58)));

    assertThat(tert, notNullValue());
    assertThat(tert, isIn(groups));
    assertThat(terta, isIn(groups));
  }


  @Test
  public void getSecondaryGroups() {
    AppUser user = appUsrCtrl.getUser("mmarquez");
    AppGroup main = appUsrCtrl.getGroupFromName("SPAIN");
    List<AppGroup> groups = appUsrCtrl.getSecondaryGroups(user, main);
    AppGroup tert = (AppGroup)hibSes.get(AppGroup.class, 1400),
        terta = (AppGroup)hibSes.get(AppGroup.class, 1401);

    assertThat(groups, hasSize(greaterThan(10)));
    assertThat(tert, isIn(groups));
    assertThat(terta, isIn(groups));

    user = appUsrCtrl.getUser("lpalencia");
    groups = appUsrCtrl.getSecondaryGroups(user, main);
    assertThat(tert, not(isIn(groups)));
    assertThat(terta, not(isIn(groups)));
  }



  @Test
  public void setUserDisabled() {
    // Session ses = HibernateUtil.getSessionFactory().openSession();
    AppUserCtrl usrCtrl = new AppUserCtrl(hibSes);

    String usrId = "2351";
    AppUser theUser = (AppUser)hibSes.get(AppUser.class, Integer.parseInt(usrId));
    assertThat(theUser, notNullValue());
    boolean res = usrCtrl.setUserDisabled(theUser, true);
    assertThat(res, is(true));

    boolean disabled = theUser.getRemoved().toString().equals("1");
    assertThat(disabled, is(true));
  }


  @Test
  public void setUserEnabled() {
    // Session ses = HibernateUtil.getSessionFactory().openSession();
    AppUserCtrl usrCtrl = new AppUserCtrl(hibSes);

    String usrId = "2351";
    AppUser theUser = (AppUser)hibSes.get(AppUser.class, Integer.parseInt(usrId));
    assertThat(theUser, notNullValue());
    boolean res = usrCtrl.setUserDisabled(theUser, false);
    assertThat(res, is(true));

    boolean enabled = theUser.getRemoved().toString().equals("0");
    assertThat(enabled, is(true));
  }
}
