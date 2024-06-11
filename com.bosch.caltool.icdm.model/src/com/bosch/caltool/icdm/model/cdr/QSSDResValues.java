/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import com.bosch.caltool.icdm.model.cdr.CDRConstants.QSSD_RESULT_FLAG;

/**
 * @author say8cob
 */

public enum QSSDResValues {

                           /** Ok value. */
                           OK(QSSD_RESULT_FLAG.OK.getUiType()),

                           /** No rule ui Value. */
                           NO_RULE(QSSD_RESULT_FLAG.NO_RULE.getUiType()),

                           /**
                            * QSSD Failed - usecase type QSSD
                            */
                           QSSD(QSSD_RESULT_FLAG.QSSD.getUiType()),


                           /** NA. */
                           NA("");


  /** The ui value. */
  private final String uiValue;

  /**
   * Instantiates a new QSSD res values.
   *
   * @param uiValue the ui value
   */
  QSSDResValues(final String uiValue) {
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
   * Gets the qssd res value.
   *
   * @param uiValue uiValue
   * @return the qssd Result value
   */
  public static QSSDResValues getQSSDResValue(final String uiValue) {

    for (QSSDResValues qssdValue : QSSDResValues.values()) {
      if (qssdValue.uiValue.equalsIgnoreCase(uiValue)) {
        return qssdValue;
      }
    }
    return QSSDResValues.NA;
  }

}
