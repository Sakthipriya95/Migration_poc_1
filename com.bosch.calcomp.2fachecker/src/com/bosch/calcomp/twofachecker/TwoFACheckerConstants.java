/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.twofachecker;


/**
 * Constants applicable for 2FA authentication check
 *
 * @author BNE4COB
 */
class TwoFACheckerConstants {

  /**
   * SID of 2FA Authentication group <code>BOSCH\RB_AMAEnable</code>
   */
  static final String[] TWO_FA_GROUP_SIDS = { "S-1-5-21-2000478354-1614895754-1801674531-238684" };
  /**
   * SID of Remote Logon group <code>NT AUTHORITY\REMOTE INTERACTIVE LOGON</code>
   */
  static final String[] REMOTE_LOGIN_GROUP_SIDS = { "S-1-5-14" };

  /**
   * Private constructor for constants
   */
  private TwoFACheckerConstants() {
    // Private constructor for constants
  }
}
