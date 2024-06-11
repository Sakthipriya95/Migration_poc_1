/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.twofachecker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.bosch.calcomp.junittestframework.JUnitTest;


/**
 * @author BNE4COB
 */
public class TwoFACheckerTest extends JUnitTest {

  /**
   * DevOps user - does not have 2FA authentication
   */
  private static final String DEVOPS_USER = "CTN8COB";

  /**
   * Test method for {@link com.bosch.calcomp.twofachecker.TwoFAChecker#check()}.
   */
  @Test
  public void testValidate() {
    TwoFAChecker validator = new TwoFAChecker(AUT_LOGGER);

    String currentUser = System.getenv("USERNAME");
    TWOFA_STATUS state = validator.check();

    if (DEVOPS_USER.equalsIgnoreCase(currentUser)) {
      assertEquals("State is Normal login", TWOFA_STATUS.AUTH_NT, state);
    }
    else {
      assertTrue("State is 2FA or Remote", (TWOFA_STATUS.AUTH_2FA == state) || (TWOFA_STATUS.AUTH_NT_REMOTE == state));
    }

  }

}
