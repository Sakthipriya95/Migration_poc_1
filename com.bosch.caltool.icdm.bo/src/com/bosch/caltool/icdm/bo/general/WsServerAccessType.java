/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.general;


/**
 * Web Service Access Type
 *
 * @author bne4cob
 */
enum WsServerAccessType {
                         /**
                          * Full access
                          */
                         ALL("A"),
                         /**
                          * Access to all internal services
                          */
                         ALL_INTERNAL("I"),
                         /**
                          * Restricted access. Requires service mapping
                          */
                         RESTRICTED("R");


  private String code;

  WsServerAccessType(final String code) {
    this.code = code;
  }

  /**
   * @return access type code
   */
  public String getCode() {
    return this.code;
  }

}
