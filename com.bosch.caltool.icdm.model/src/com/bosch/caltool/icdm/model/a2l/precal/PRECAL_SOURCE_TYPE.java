/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l.precal;


/**
 * @author bne4cob
 */
public enum PRECAL_SOURCE_TYPE {
                                /**
                                 * Common Rules
                                 */
                                COMMON_RULES("C"),
                                /**
                                 * RuleSet rules
                                 */
                                RULESET_RULES("R"),
                                /**
                                 * Series Statistics
                                 */
                                SERIES_STATISTICS("S"),
                                /**
                                 * Data review results
                                 */
                                DATA_REVIEW_RESULTS("D");


  private String typeCode;

  PRECAL_SOURCE_TYPE(final String typeCode) {
    this.typeCode = typeCode;
  }

  /**
   * Convert source type code to enum value
   *
   * @param code source type code
   * @return enum value, or null
   */
  public static PRECAL_SOURCE_TYPE getType(final String code) {

    PRECAL_SOURCE_TYPE ret = null;
    for (PRECAL_SOURCE_TYPE type : PRECAL_SOURCE_TYPE.values()) {
      if (type.getTypeCode().equals(code)) {
        ret = type;
        break;
      }
    }

    return ret;
  }

  /**
   * @return the typeCode
   */
  public String getTypeCode() {
    return this.typeCode;
  }

}
