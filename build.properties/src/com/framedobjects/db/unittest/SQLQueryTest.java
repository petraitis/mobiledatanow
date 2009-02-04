package com.framedobjects.db.unittest;

import java.util.List;

import com.framedobjects.db.SQLTester;

import junit.framework.TestCase;

public class SQLQueryTest extends TestCase {

  public static void main(String[] args) {
    //junit.swingui.TestRunner.run(SQLQueryTest.class);
  }

  public SQLQueryTest(String arg0) {
    super(arg0);
  }

  protected void setUp() throws Exception {
    super.setUp();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }

  /*
   * Test method for 'com.framedobjects.db.SQLSingleQuery.findObject(String, String)'
   */
  public void testFindObjectStringString() {
    SQLTester tester = new SQLTester();
    assertNotNull("Checks for user existence", tester.getLoginUser("admin01", "admin01"));
  }
  /*
   * Test method for 'com.framedobjects.db.SQLSingleQuery.execute()'
   */
  public void testExecute(){
    SQLTester tester = new SQLTester();
    List list = tester.getAllUsers();
    assertEquals("Users are in the DB", true, list.size() > 0);
  }
  
  public void testMultiRequest(){
    SQLTester tester = new SQLTester();
    List list = tester.getAllUsersWithGroups();
    assertEquals("Multiple requests", true, list.size() > 0);
  }
}
