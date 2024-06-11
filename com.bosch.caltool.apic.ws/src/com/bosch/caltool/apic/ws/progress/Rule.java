/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.progress;


/**
 * @author imi2si
 */
public class Rule {

  private int minRowNum = 0;
  private int maxRowNum = 0;
  private double percentageRange = 0;
  private double interval = 0;

  public Rule(final int minRowNum, final int maxRowNum, final double percentageRange) {
    // Max Row num should be always greater than min

    assert maxRowNum > minRowNum : "MaxRowNum must be greater than minRowNum";

    this.minRowNum = minRowNum;
    this.maxRowNum = maxRowNum;
    this.percentageRange = percentageRange;
  }

  /**
   * @return the minRowNum
   */
  public int getMinRowNum() {
    return this.minRowNum;
  }

  /**
   * @return the maxRowNum
   */
  public int getMaxRowNum() {
    return this.maxRowNum;
  }

  /**
   * @return the percentage range in which the interval should be calculated
   */
  public double getPercentageRange() {
    return this.percentageRange;
  }

  /**
   * @return the percentage range in which the interval should be calculated
   */
  public double getInterval() {
    return this.interval;
  }

  /**
   * @return the percentage range in which the interval should be calculated
   */
  public void setInterval(final double interval) {
    this.interval = interval;
  }

}
