/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import com.bosch.caltool.icdm.model.cdr.CDRConstants.COMPLI_RESULT_FLAG;

/**
 * The Enum CompliResValues.
 *
 * @author rgo7cob Enum For Compli/Shape Result
 */
public enum CompliResValues {

                             /** Ok value. */
                             OK(COMPLI_RESULT_FLAG.OK.getUiType()),

                             /** No rule ui Value. */
                             NO_RULE(COMPLI_RESULT_FLAG.NO_RULE.getUiType()),

                             /**
                              * Compli Failed - usecase type cssd
                              */
                             CSSD(COMPLI_RESULT_FLAG.CSSD.getUiType()),
                             /**
                              * Compli Failed - usecase type ssd2rv
                              */
                             SSD2RV(COMPLI_RESULT_FLAG.SSD2RV.getUiType()),

                             /** Shape Ui Value. */
                             SHAPE("SHAPE"),

                             /** NA. */
                             NA("");


  /** The ui value. */
  private final String uiValue;

  /**
   * Instantiates a new compli res values.
   *
   * @param uiValue the ui value
   */
  CompliResValues(final String uiValue) {
    this.uiValue = uiValue;
  }

  /**
   * Gets the ui value.
   *
   * @return the Ui Value
   */
  public String getUiValue() {
    return this.uiValue;
  }

  /**
   * Gets the compli res value.
   *
   * @param uiValue uiValue
   * @return the compli Result value
   */
  public static CompliResValues getCompliResValue(final String uiValue) {

    for (CompliResValues compliValue : CompliResValues.values()) {
      if (compliValue.uiValue.equalsIgnoreCase(uiValue)) {
        return compliValue;
      }
    }
    return CompliResValues.NA;
  }
}