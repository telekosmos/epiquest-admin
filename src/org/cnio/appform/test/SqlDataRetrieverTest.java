package org.cnio.appform.test;

import org.cnio.appform.util.dump.SqlDataRetriever;

public class SqlDataRetrieverTest extends junit.framework.TestCase {

  public void testGetResultSet() throws Exception {
    SqlDataRetriever sdr = new SqlDataRetriever();
    /*
      grpid	4
      intrvid	50
      prjid	157
      secid	4
      */
    java.sql.ResultSet rs = sdr.getResultSet("157",50, 4, 4);

    assertNotNull("resultset is null :(", rs);
  }
}
