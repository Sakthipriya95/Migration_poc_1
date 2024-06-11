/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc.projcons;

/**
 * @author bne4cob
 */
public enum ErrorType {
                       /**
                        * Attribute missing error
                        */
                       ATTR_MISSING(ErrorType.ECODE_ATTR_MISSING, "Attribute Missing"),
                       /**
                        * Attribute extra error
                        */
                       ATTR_EXTRA(ErrorType.ECODE_ATTR_EXTRA, "Attribute Extra"),
                       /**
                        * No versions defined for PIDC error
                        */
                       PIDC_NO_VERS(ErrorType.ECODE_PIDC_NO_VERS, "No versions defined for PIDC"),
                       /**
                        * Missing structure attribute in PIDC Version Error
                        */
                       PIDVERS_STRUCT_MISSING(ErrorType.ECODE_PIDVERS_STRUCT_MISSING, "Missing structure attribute in PIDC Version"),
                       /**
                        * No focus matrix versions
                        */
                       FM_VERS_MISSING(ErrorType.ECODE_FM_VERS_MISSING, "No focus matrix versions found"),
                       /**
                        * No focus matrix versions
                        */
                       FM_VERS_NO_WS(ErrorType.ECODE_FM_VERS_NO_WS, "Missing working set version for focus matrix version");


  /**
   * Error code - attribute missing
   */
  public static final long ECODE_ATTR_MISSING = -1000L;
  /**
   * Error code - Attribute Extra
   */
  public static final long ECODE_ATTR_EXTRA = -1001L;
  /**
   * Error code - No versions defined for PIDC
   */
  public static final long ECODE_PIDC_NO_VERS = -1002L;
  /**
   * Error code - Missing structure attribute in PIDC Version Error
   */
  public static final long ECODE_PIDVERS_STRUCT_MISSING = -1003L;
  /**
   * Error code - No focus matrix versions
   */
  public static final long ECODE_FM_VERS_MISSING = -1004L;
  /**
   * Error code - Missing working set version for focus matrix version
   */
  public static final long ECODE_FM_VERS_NO_WS = -1005L;

  /**
   * error code
   */
  private final Long code;

  /**
   * error description
   */
  private final String description;

  /**
   * Constructor
   *
   * @param code error code
   * @param description error description
   */
  ErrorType(final Long code, final String description) {
    this.code = code;
    this.description = description;
  }

  /**
   * @return error code
   */
  public Long getCode() {
    return this.code;
  }

  /**
   * @return error description
   */
  public String getDescription() {
    return this.description;
  }

}
