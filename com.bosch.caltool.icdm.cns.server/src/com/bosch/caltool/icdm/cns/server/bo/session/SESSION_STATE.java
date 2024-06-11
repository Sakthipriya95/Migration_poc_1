/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.server.bo.session;


/**
 * @author bne4cob
 */
public enum SESSION_STATE {
                           /**
                            * Active session
                            */
                           ACTIVE("ACTIVE"),
                           /**
                            * Inactive session
                            */
                           INACTIVE("INACTIVE"),
                           /**
                            * Closed session
                            */
                           CLOSED("CLOSED"),
                           /**
                            * No session available
                            */
                           NO_SESSION("NONE"),
                           /**
                            * Invalid session
                            */
                           INVALID("INVALID");

  private String code;

  SESSION_STATE(final String code) {
    this.code = code;
  }

  /**
   * @return state code
   */
  public String getCode() {
    return this.code;
  }

  /**
   * Convert code string to session state
   *
   * @param input code
   * @return session state
   */
  public static SESSION_STATE getType(final String input) {
    SESSION_STATE ret = null;
    for (SESSION_STATE state : SESSION_STATE.values()) {
      if (state.code.equals(input)) {
        ret = state;
      }
    }

    return ret;
  }
}
