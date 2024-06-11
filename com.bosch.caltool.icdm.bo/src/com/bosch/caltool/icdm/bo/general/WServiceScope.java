/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.general;


/**
 * Web Service Scope
 *
 * @author bne4cob
 */
enum WServiceScope {
                    /**
                     * External service - used by non iCDM clients
                     */
                    EXTERNAL("E"),
                    /**
                     * Internal service - used only for iCDM client
                     */
                    INTERNAL("I");

  private String code;

  WServiceScope(final String code) {
    this.code = code;
  }

  /**
   * @return access type code
   */
  public String getCode() {
    return this.code;
  }
}
