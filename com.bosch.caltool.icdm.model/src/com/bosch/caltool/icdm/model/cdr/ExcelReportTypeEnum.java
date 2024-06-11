/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;


/**
 * @author NDV4KOR
 */
public enum ExcelReportTypeEnum {

                                 /**
                                  * 
                                  */
                                 ONEFILECHECK(0),
                                 SINGLEFILEWITHSUMMARY(1),
                                 SINGLEFILEWITHREDUCTIONSUMMARY(2);

  private int value;

  private ExcelReportTypeEnum(final int reportType) {
    setValue(reportType);
  }

  /**
   * @param value the value to set
   */
  public void setValue(final int value) {
    this.value = value;
  }

  /**
   * @return selected value
   */
  public int getValue() {
    return this.value;
  }

}

