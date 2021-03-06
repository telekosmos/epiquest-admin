package org.cnio.appform.util.dump;


import java.io.IOException;
import java.sql.*;
import java.util.Properties;

/**
 * This class retrieve the resultset for the interview, patient, section and, 
 * if so, group, by using the java.sql API classes
 * @author bioinfo
 *
 */
public class SqlDataRetriever {
	
	
	private Connection conn;
	private Statement stmt;


  public SqlDataRetriever () {

  }
	
/**
 * Retrieves the full resultset in order to retrive all answers for a section and group
 * @param prjCode, the project code
 * @param intrvId, the database interview id
 * @param grpId, the database groupId
 * @param secOrder, the section order
 * @return a java.sql.ResultSet object with ALL queried rows
 */
	public ResultSet getFullResultSet(String prjCode, Integer intrvId, Integer grpId,
                                    Integer secOrder) {
		
		try {
			// this.conn = this.getConnection ();
			// this.stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			String secParam = (secOrder == null)?"s.section_order ": secOrder.toString();
	  	String grpParam = (grpId == null? "1=1 ": "g.idgroup = "+grpId);

	     String sqlqry = "select p.codpatient, g.name as grpname, "+
	        "i.name as intrvname, s.name as secname, "+
	      "q.codquestion as codq, a.thevalue, s.section_order, "+
	      "it.item_order, pga.answer_order, pga.answer_number, it.\"repeatable\" as itrep "+
	      "from patient p, pat_gives_answer2ques pga, appgroup g,	performance pf, "+
	        "question q, answer a, interview i, item it, section s, project pj "+
	      "where "+ grpParam +
	      " and i.idinterview = "+intrvId +
	      " and pj.project_code = '"+prjCode+"' " +
	      "and pj.idprj = i.codprj "+
	      "and pf.codinterview = i.idinterview "+
	      "and pf.codgroup = g.idgroup "+
	      "and s.codinterview = i.idinterview "+
	      "and pf.codpat = p.idpat "+
	      "and pga.codpat = p.idpat "+
	      "and pga.codquestion = q.idquestion "+
	      "and pga.codanswer = a.idanswer "+
	      "and q.idquestion = it.iditem "+
	      "and it.idsection = s.idsection " +
	      "and s.section_order = " + secParam +
	      " order by 1, 7, 10, 8, 5, 9";

      System.out.println ("\nSqlDataRetriever => ResultSet query:\n"+sqlqry);
      // ResultSet rs = this.stmt.executeQuery(sqlqry);
      ResultSet rs = this.getScrollableRS(sqlqry);
      
      return rs;
		}
    catch (Exception ex) {
      System.out.println("SqlDataRetreiver.getFullResultSet: "+ex.getMessage());

      return null;
    }
    /*
		catch (ClassNotFoundException cnfe) {
	    System.out.println("Couldn't find the driver!");
	    System.out.println("Let's print a stack trace, and exit.");
	    cnfe.printStackTrace();
	    
	    return null;
	  }
		catch (SQLException sqlEx) {
			System.out.println ("Err getting conncection or querying...");
			sqlEx.printStackTrace();
			
			return null;
		}
		*/
	}



  /**
   * Retrieves the full resultset in order to retrive all answers for a section
   * and a country, represented by all groups which are child groups for that country
   * @param prjCode, the project code
   * @param intrvId, the database interview id
   * @param grpIds, the secondary groups for the chosen country
   * @param secOrder, the section order
   * @return a java.sql.ResultSet object with ALL queried rows
   */
  public ResultSet getFullResultsetForCountry (String prjCode, Integer intrvId,
                                               String grpIds,  Integer secOrder) {
    try {
      // this.conn = this.getConnection ();
      // this.stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

      String secParam = (secOrder == null)?"s.section_order ": secOrder.toString();
      // String grpParam = (grpId == null? "1=1 ": "g.idgroup = "+grpId);
      String grpParam = "g.idgroup in ("+grpIds+")";

      // change for the other
      String sqlqry = "select p.codpatient, g.name as grpname, "+
        "i.name as intrvname, s.name as secname, "+
        "q.codquestion as codq, a.thevalue, s.section_order, "+
        "it.item_order, pga.answer_order, pga.answer_number, it.\"repeatable\" as itrep "+
        "from patient p, pat_gives_answer2ques pga, appgroup g,	performance pf, "+
        "question q, answer a, interview i, item it, section s, project pj "+
        "where "+ grpParam +
        " and i.idinterview = "+intrvId +
        " and pj.project_code = '"+prjCode+"' " +
        "and pj.idprj = i.codprj "+
        "and pf.codinterview = i.idinterview "+
        "and pf.codgroup = g.idgroup "+
        "and s.codinterview = i.idinterview "+
        "and pf.codpat = p.idpat "+
        "and pga.codpat = p.idpat "+
        "and pga.codquestion = q.idquestion "+
        "and pga.codanswer = a.idanswer "+
        "and q.idquestion = it.iditem "+
        "and it.idsection = s.idsection " +
        "and s.section_order = " + secParam +
        " order by 1, 7, 10, 8, 5, 9";

      // cambio de orden en el dominio de la query, igual al de arriba
      sqlqry = "select pats.codpatient, pats.grpname as grpname, " +
        "pgas.intrvname as intrvname, pgas.secname as secname, " +
        "pgas.codq as codq, a.thevalue, pgas.section_order, " +
        "pgas.item_order, pgas.answer_order, pgas.answer_number, " +
        "pgas.itrep, pats.idpat " +
        "from (" +
        "select pf.codpat, codpatient, g.name as grpname, p.idpat " +
        "from interview i, performance pf, patient p, appgroup g " +
        "where " + grpParam +
        " and i.idinterview = " + intrvId +
        " and pf.codinterview = i.idinterview " +
        "and pf.codgroup = g.idgroup " +
        "and pf.codpat = p.idpat" +
        ") " +
        " pats left join (" +
        "select items.*, pga.codpat, pga.codanswer, pga.answer_number, pga.answer_order " +
        "from (" +
        "select it.iditem, it.\"content\", it.item_order, it.repeatable as itrep, " +
        "pj.\"name\" as prjname, i.name as intrvname,s.name as secname, s.section_order, " +
        "q.codquestion as codq " +
        "from interview i, section s, item it, question q, project pj " +
        "where i.idinterview = " + intrvId +
        " and pj.project_code = '" + prjCode+"' " +
        "and pj.idprj = i.codprj " +
        "and i.idinterview = s.codinterview " +
        "and s.section_order = " + secParam +
        " and it.idsection = s.idsection " +
        "and it.iditem = q.idquestion   " +
        ") items left join pat_gives_answer2ques pga on (pga.codquestion = items.iditem) " +
        ") pgas on (pats.codpat = pgas.codpat) " +
        "left join answer a on (pgas.codanswer = a.idanswer) " +
        "order by 1, 7, 10, 8, 5, 9;";

      System.out.println ("\nSqlDataRetriever => getResultsetForCountry query:\n"+sqlqry);
      ResultSet rs = this.getScrollableRS(sqlqry); // .stmt.executeQuery(sqlqry);

      return rs;
    }
    catch (Exception sqlEx) {
      System.out.println ("Err getting scrollable resultset...");
      sqlEx.printStackTrace();

      return null;
    }
  }


  public ResultSet getScrollableRS (String qry) {

    try {
      if (this.conn == null)
        this.conn = this.getConnection();

      this.conn.setAutoCommit(false);
      this.stmt = conn.createStatement();
      this.stmt.setFetchSize(5000);
      ResultSet rs = this.stmt.executeQuery(qry);

      return rs;
    }
    catch (ClassNotFoundException cnfe) {
      System.out.println("SqlDataRetriever::getScrollableRS: Couldn't find the driver!");
      System.out.println("SqlDataRetriever::getScrollableRS: Let's print a stack trace, and exit.");
      cnfe.printStackTrace();

      return null;
    }
    catch (SQLException sqlEx) {
      System.out.println ("SqlDataRetriever::getScrollableRS: Err getting conncection or querying...");
      sqlEx.printStackTrace();

      return null;
    }
  }

	public void closeConn () throws SQLException {
		this.stmt.close();
		this.conn.close();
	}
	
	
	public void printResultsetOut (ResultSet rs) {
		String msgRow;
		int counter = 120000;
		
		try {
			if (counter > 0)
				rs.absolute(counter);
			
			while (rs.next() && counter < 120500) {
				msgRow = rs.getString(1)+" => "+rs.getString(5)+"-"+rs.getInt(10);
				msgRow += "-"+rs.getInt(9)+": "+rs.getString(6);
				
	      if (rs.wasNull()) 
	        msgRow += "...theValue is null";
	      
	      System.out.println (msgRow);
	      System.out.println("---------------");
	      counter++;
		  }
//		  rs.close();
		}
		catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
		}
		
	}
	
	
	
	
	private Connection getConnection() throws ClassNotFoundException, SQLException {
    Properties properties = new Properties();
    try {
      java.io.InputStream in = SqlDataRetriever.class.getResourceAsStream("/app.props");
      properties.load(in);
      in.close();
    }
    catch (IOException ioEx) {
      ioEx.printStackTrace();
      return null;
    }
    Class.forName("org.postgresql.Driver");
    String host = properties.getProperty("host");
    String port = properties.getProperty("port");
    String user = properties.getProperty("user");
    String pw = properties.getProperty("pass");
    String ds = properties.getProperty("datasource");
    String url = "jdbc:postgresql://"+host+":"+port+"/"+ds;

    return DriverManager.getConnection(url, user, pw);
  }

}
