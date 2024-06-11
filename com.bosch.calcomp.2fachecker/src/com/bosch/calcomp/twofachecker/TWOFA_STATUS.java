/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.twofachecker;


/**
 * @author BNE4COB
 */
public enum TWOFA_STATUS {
                               /**
                                * 2FA authentication available
                                */
                               AUTH_2FA,
                               /**
                                * Logged in via Remote Logon
                                */
                               AUTH_NT_REMOTE,
                               /**
                                * Normal Log-in
                                */
                               AUTH_NT,
                               /**
                                * Not validated
                                */
                               AUTH_NONE;
}
