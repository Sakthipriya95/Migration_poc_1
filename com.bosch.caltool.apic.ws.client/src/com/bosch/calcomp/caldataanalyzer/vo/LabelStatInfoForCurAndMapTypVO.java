/**********************************************************
 * Copyright (c) 2007, Robert Bosch GmbH All rights reserved.
 ***********************************************************/
package com.bosch.calcomp.caldataanalyzer.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ram6kor
 *
 *         <pre>
 * Version 	Date			Modified by			Changes
 * ----------------------------------------------------------------------------
 * 0.1 		02-Feb-2010 	Ramesh		    CDA-132 To store Statistical Info about Curve and Map  <br>
 *         </pre>
 */
/**
 * This class stores the Statistical information for the Curves ana map.
 *
 * @author ram6kor
 */
public class LabelStatInfoForCurAndMapTypVO extends LabelInfoVO implements Serializable {

  /**
   * Serial Version ID.
   */
  private static final long serialVersionUID = 7826519744528080000L;

  /**
   * The minimum value of this label .
   */
  private double minimumValue;
  /**
   * The maximum value of this label
   */
  private double maximumValue;

  /**
   * @return the minimumValue
   */
  public double getMinimumValue() {
    return this.minimumValue;
  }

  /**
   * @param minimumValue the minimumValue to set
   */
  public void setMinimumValue(final double minimumValue) {
    this.minimumValue = minimumValue;
  }

  /**
   * @return the maximumValue
   */
  public double getMaximumValue() {
    return this.maximumValue;
  }

  /**
   * @param maximumValue the maximumValue to set
   */
  public void setMaximumValue(final double maximumValue) {
    this.maximumValue = maximumValue;
  }

  /**
   * The list of zValue List .
   */
  private List<Double> zValueList = new ArrayList<>();

  /**
   * @return the zValueList
   */
  public List<Double> getZValueList() {
    // Task 290992 : Mutable members should not be stored or returned directly
    return new ArrayList<>(this.zValueList);
  }

  /**
   * @param valueList the zValueList to set
   */
  public void setZValueList(final List<Double> valueList) {
    // Task 290992 : Mutable members should not be stored or returned directly
    this.zValueList = new ArrayList<>(valueList);
  }

}
