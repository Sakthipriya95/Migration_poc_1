/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.adapter.logger.util;


/**
 * @author bne4cob
 */
public class LoggerUtilTest {

  /**
   * Increment this value for a performance comparsion
   */
  private static final int ITR_COUNT = 1;

  /**
   * Do test, by calling any one of the methods
   * 
   * @param message
   * @param args
   * @return
   */
  private static String doTest(final String message, final Object... args) {
    return LoggerUtil.buildLogMessage(message, args);

    // return message;
    // return strAddCall(message, args);

    // return strAppendCall(message, args);
  }


  /**
   * Tests string concatenation, for perf comparsion with actual method
   * 
   * @param message
   * @param args
   * @return
   */
  private static String strAddCall(final String message, final Object[] args) {
    String ret = message;
    if (args != null) {
      for (Object arg : args) {
        ret += arg;
      }
    }
    return ret;
  }

  /**
   * Tests string builder appending, for perf comparsion with actual method
   * 
   * @param message
   * @param args
   * @return
   */
  private static String strAppendCall(final String message, final Object[] args) {
    StringBuilder ret = new StringBuilder(message);
    if (args != null) {
      for (Object arg : args) {
        ret.append(arg);
      }
    }
    return ret.toString();
  }

  /**
   * Tests different types of messages and parameters
   * 
   * @param args not applicable
   */
  public static void main(final String args[]) {
    Long startTime = System.currentTimeMillis();
    for (int i = 0; i < ITR_COUNT; i++) {
      System.out.println("------------------------------");
      test("Test 1", "Hello 'TestUser', \"DEPT\" how are you? From 62371",
          doTest("Hello '{}', \"{}\" how are you? From {}", "TestUser", "DEPT", 62371));
      test("Test 1.1", "Hello how are you TestUser", doTest("Hello how are you {}", "TestUser"));
      test("Test 1.2", "Hello how are you TestUser, DEPT", doTest("Hello how are you {}, {}", "TestUser", "DEPT"));
      test("Test 1.3", "Hello how are you TestUser", doTest("Hello how are you {}", "TestUser", "DEPT"));
      test("Test 2", "Hello how are you", doTest("Hello how are you", "TestUser", "DEPT", 62371));
      test("Test 3", "TestUser, Hello how are you", doTest("{}, Hello how are you", "TestUser", "DEPT", 62371));
      test("Test 3.1", "TestUser, Hello how are you", doTest("{}, Hello how are you", "TestUser"));
      test("Test 3.2", "TestUser, Hello DEPT how are you",
          doTest("{}, Hello {} how are you", "TestUser", "DEPT", 62371));
      test("Test 4", "Hello TestUser, DEPT how are you? From ",
          doTest("Hello {}, {} how are you? From {}", "TestUser", "DEPT"));
      test("Test 5", "Hello ,  how are you? From ", doTest("Hello {}, {} how are you? From {}"));
      test("Test 6", "No arguments", doTest("No arguments"));
      test("Test 7", "Null arg ", doTest("Null arg {}", null));
      test("Test 8", "Null arg null", doTest("Null arg {}", (Object) null));
      test("Test 9", "Null arg null", doTest("Null arg {}", new Object[] { null }));
      test("Test 10", "Null arg null-null", doTest("Null arg {}-{}", null, null));
      test("Test 11", "Hello how are you TestUser100", doTest("Hello how are you {}{}", "TestUser", 100));
    }
    System.out.println("\nTime taken : " + (System.currentTimeMillis() - startTime));
  }

  private static void test(final String testID, final String exp, final String act) {
    if (exp.equals(act)) {
      System.out.println(testID + ":Success : Expected=" + exp + "; Actual=" + act);
    }
    else {
      System.out.println(testID + ":Failure : Expected=" + exp + "; Actual=" + act);
    }
  }

}
