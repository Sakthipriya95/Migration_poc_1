/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.math.BigDecimal;

/**
 * Class to store the statistic information of each parameter.
 *
 * @author svj7cob
 */
public class PreCalibrationDataOutput {

  /**
   * Most Frequent value in byte array
   */
  private byte[] mostFreqValue;

  /**
   * The percentage of Most Frequent value
   */
  private BigDecimal percentOfMostFreqCheckValue;

  /**
   * Other values count
   */
  private Integer otherCheckValuesCount;

  /**
   * The type of caldata
   */
  private String type;

  /**
   * @return the otherCheckValuesCount
   */
  public Integer getOtherCheckValuesCount() {
    return this.otherCheckValuesCount;
  }


  /**
   * @param otherCheckValuesCount the otherCheckValuesCount to set
   */
  public void setOtherCheckValuesCount(final Integer otherCheckValuesCount) {
    this.otherCheckValuesCount = otherCheckValuesCount;
  }


  /**
   * @return the mostFreqValue
   */
  public byte[] getMostFreqValue() {
    return this.mostFreqValue;
  }


  /**
   * @param mostFreqValue the mostFreqValue to set
   */
  public void setMostFreqValue(final byte[] mostFreqValue) {
    this.mostFreqValue = mostFreqValue.clone();
  }


  /**
   * @return the percentOfMostFreqCheckValue
   */
  public BigDecimal getPercentOfMostFreqCheckValue() {
    return this.percentOfMostFreqCheckValue;
  }


  /**
   * @param percentOfMostFreqCheckValue the percentOfMostFreqCheckValue to set
   */
  public void setPercentOfMostFreqCheckValue(final BigDecimal percentOfMostFreqCheckValue) {
    this.percentOfMostFreqCheckValue = percentOfMostFreqCheckValue;
  }


  /**
   * @return the type
   */
  public String getType() {
    return this.type;
  }


  /**
   * @param type the type to set
   */
  public void setType(final String type) {
    this.type = type;
  }

}
