package org.cnio.appform.util.curation;


import org.cnio.appform.util.HibernateUtil;
import org.hibernate.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class DropForeignKeys {


  private class Duet {
    String first, second;

    public Duet (String first, String second) {
      this.first = first;
      this.second = second;
    }
    /*
    public String getFirst() {
      return this.first;
    }

    public String getSecond() {
      return this.second;
    }
    */
  }

  private Session hibSes;

  private String selectFKs = "SELECT tc.constraint_name, tc.table_name, kcu.column_name, " +
    "    ccu.table_name AS foreign_table_name," +
    "    ccu.column_name AS foreign_column_name " +
    "FROM " +
    "    information_schema.table_constraints AS tc " +
    "    JOIN information_schema.key_column_usage AS kcu" +
    "      ON tc.constraint_name = kcu.constraint_name" +
    "    JOIN information_schema.constraint_column_usage AS ccu" +
    "      ON ccu.constraint_name = tc.constraint_name " +
    "WHERE constraint_type = 'FOREIGN KEY'; ";

  private String dropFK = "alter table TABLE drop constraint FK;";

  /**
   * Execute an sql query on hibernate and return the result.
   * It is autocontained here, maybe should be in a more common place
   * @param qryStr, the string representing the query and ready to be runned
   * @param offset, the row number to start to get data, -1 for no offset
   * @param maxRows, the number maximum of rows to retrieve, -1 for no limit
   * @return
   */
  protected List<Object[]> execQuery (String qryStr, int offset, int maxRows) {
    Session myHibSes = HibernateUtil.getSessionFactory().openSession();
    Transaction tx = null;
    List<Object[]> rows = null;

    try {
      tx = myHibSes.beginTransaction();
      SQLQuery sqlQry = myHibSes.createSQLQuery(qryStr);

      if (offset > 0)
        sqlQry.setFirstResult(offset);

      if (maxRows > 0) {
        sqlQry.setMaxResults(maxRows);
        sqlQry.setFetchSize(5000);
      }

      rows = sqlQry.list();
      tx.commit();
    }
    catch (HibernateException ex) {
      if (tx != null)
        tx.rollback();

      System.out.println(ex.getMessage());
      ex.printStackTrace ();
    }
    finally {
      myHibSes.close();
    }
    return rows;
  }


  /**
   * Drops the foreign key for table
   * @param fkName the foreign key name
   * @param table the table name the foreign key belongs to
   * @return the number of entities updated; -1 if something was wrong
   */
  private int dropForeignKey(String fkName, String table) {
    String dropQry = "alter table "+table+" drop constraint "+fkName+";";

    Session myHibSes = HibernateUtil.getSessionFactory().openSession();
    Transaction tx = null;
    int res = -1;

    try {
      tx = myHibSes.beginTransaction();
      SQLQuery sqlQry = myHibSes.createSQLQuery(dropQry);

      res = sqlQry.executeUpdate();
      tx.commit();
    }
    catch (HibernateException ex) {
      if (tx != null)
        tx.rollback();

      System.out.println(ex.getMessage());
      ex.printStackTrace ();
    }
    finally {
      myHibSes.close();
    }

    return res;
  }



  /**
   * Gets a list of duplicate foreign keys
   * @return as list with pairs foreign key-table, in order to drop them
   */
  private List<Duet> getDuplicateFKs() {
    List<Duet> fks = new ArrayList<Duet>();

    List<Object[]> rows = this.execQuery(selectFKs, 0, 0);
    Iterator<Object[]> rowIt = rows.iterator();
    StringBuffer table = new StringBuffer();
    while (rowIt.hasNext()) {
      Object[] row = rowIt.next();
      String fk = (String)row[0];
      if (!fk.contains("_")) { // fk does contain _ in name => true fk
        table.append((String)row[1]);
        Duet duet = new Duet(fk, table.toString());
        fks.add(duet);
        table.delete(0, table.length());
      }
    }

    return fks;
  }


  /**
   * Drop all duplicate foreign keys found
   * @return the number of foreign keys affected
   */
  public int dropDuplicateFKs () {
    List<Duet> fkPairs = this.getDuplicateFKs();
    int numOfDropped = 0;

    if (fkPairs.size() > 0) { // there are duplicate FKs
      Iterator<Duet> fkIt = fkPairs.iterator();
      int updateRes = -1;
      while(fkIt.hasNext()) {
        Duet fkPair = fkIt.next();
        updateRes = this.dropForeignKey(fkPair.first, fkPair.second);
        if (updateRes > 0) {
          System.out.println("Dropped '"+fkPair.first+"' from table "+fkPair.second);
          numOfDropped+=updateRes;
        }
        else
          System.out.println("Nothing dropped for "+fkPair.first+" in table "+fkPair.second);
      }
    }

    return numOfDropped;
  }


  public void printOutDuplicates() {
    List<Duet> fkPairs = this.getDuplicateFKs();
    Iterator<Duet> fkIt = fkPairs.iterator();

    System.out.println("Duplicate foreign keys detected: "+fkPairs.size());
    while(fkIt.hasNext()) {
      Duet fkPair = fkIt.next();
      System.out.println("Table: "+fkPair.second+"; FK: "+fkPair.first);
    }
  }

}

