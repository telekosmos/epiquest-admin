package org.cnio.appform.test.util.dump;

import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import org.cnio.appform.util.dump.SqlDataRetriever;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SqlDataRetrieverTest extends junit.framework.TestCase {

  SqlDataRetriever sdr;

  @Before
  public void setUp () {
    sdr = new SqlDataRetriever();
  }

  @After
  public void tearDown () {
    sdr = null;
  }

  @Test
  public void testGetResultSet() throws Exception {
    /*
      grpid	304 -> HospitalDelMar; 400 -> 12deOctubre
      intrvid	50
      prjid	157
      secid	4
      */
    java.sql.ResultSet rs = sdr.getResultSet("157",50, 304, 4);
    int countRows = 0;
    while (rs.next()) {
      countRows++;
    }
    assertNotNull("resultset is null :(", rs);

    boolean assertRows = countRows > 2000;
    assertEquals(assertRows, true);
  }

}
