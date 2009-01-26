package com.framedobjects.db.unittest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

  public static void main(String[] args) {
    //junit.swingui.TestRunner.run(AllTests.class);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite("Test for com.framedobjects.db.unittest");
    //$JUnit-BEGIN$
    suite.addTestSuite(SQLQueryTest.class);
    //$JUnit-END$
    return suite;
  }

}
