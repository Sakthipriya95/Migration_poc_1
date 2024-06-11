/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.twofachecker;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;

/**
 * Checks if the current user has 2FA authentication
 * <p>
 * Usage :<br>
 * <code>new TwoFAChecker(logger).check();</code>
 *
 * @author BNE4COB
 */
public class TwoFAChecker {

  /*
   * Approach : a) Get the current user's group SIDs using NTSystem. b) Check if this list contains any one among the
   * predefined list of the valid groups' SIDs
   */
  private final com.sun.security.auth.module.NTSystem ntSystem = new com.sun.security.auth.module.NTSystem();

  private final ILoggerAdapter logger;

  private TWOFA_STATUS status = TWOFA_STATUS.AUTH_NONE;

  /**
   * @param logger Logger
   */
  public TwoFAChecker(final ILoggerAdapter logger) {
    this.logger = logger;
  }

  /**
   * Validate if the current user's windows login is via 2FA authentication
   *
   * @return validation status
   */
  public TWOFA_STATUS check() {
    String[] groupSids = this.ntSystem.getGroupIDs();

    String name = this.ntSystem.getName();

    if (!is2FAAuthenticated(groupSids) && !isRemoteLogin(groupSids)) {
      this.status = TWOFA_STATUS.AUTH_NT;
      this.logger.debug("User {} logged in normally using NT credentials", name);
    }

    this.logger.info("2FA Validation status for user {} is : {}", name, this.status);

    return this.status;
  }

  /**
   * @param groupSids
   * @return
   */
  private boolean is2FAAuthenticated(final String[] groupSids) {
    // 2FA authentication passes, if group SIDs of the current user contains 2FA group's SID
    boolean present = isPresent(groupSids, TwoFACheckerConstants.TWO_FA_GROUP_SIDS);
    if (present) {
      this.status = TWOFA_STATUS.AUTH_2FA;
      this.logger.debug("User {} is logged in via 2FA", this.ntSystem.getName());
    }

    return present;
  }

  /**
   * @param groupSids
   * @return
   */
  private boolean isRemoteLogin(final String[] groupSids) {
    // Login is via remote, if group SIDs of the current user contains remote login group's SID
    boolean present = isPresent(groupSids, TwoFACheckerConstants.REMOTE_LOGIN_GROUP_SIDS);
    if (present) {
      this.status = TWOFA_STATUS.AUTH_NT_REMOTE;
      this.logger.debug("User {} is logged in via Remote Login", this.ntSystem.getName());
    }

    return present;
  }

  /**
   * Check if the <code>itemToCheck</code> is present in the array <code>items</code>
   * <p>
   * Note : check is successful ONLY if all the elements in <code>expectedItems</code> are present in
   * <code>itemsToCheck</code>
   *
   * @return true/false
   */
  private boolean isPresent(final String[] items, final String[] expectedItems) {

    Set<String> expectedItemsSet = new HashSet<>(Arrays.asList(expectedItems));

    boolean found = false;
    int matchCount = 0;

    for (String item : items) {
      if (expectedItemsSet.contains(item)) {
        matchCount++;
        if (matchCount == expectedItemsSet.size()) {
          found = true;
          break;
        }
      }
    }

    return found;
  }

}
