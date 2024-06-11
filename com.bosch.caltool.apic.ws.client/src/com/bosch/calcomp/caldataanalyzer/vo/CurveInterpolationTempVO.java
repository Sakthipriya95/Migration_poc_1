/**********************************************************
 * Copyright (c) 2010, Robert Bosch GmbH All rights reserved.
 ***********************************************************/
package com.bosch.calcomp.caldataanalyzer.vo;

import java.util.ArrayList;
import java.util.List;

import com.bosch.calmodel.caldataphy.CalDataPhy;

/**
 * Class which is used for curve interpolation for a label name. When X equals a particular value i t stores the Y
 * values available in all the cure values for the same label name. It also stores the reference curves which contain a
 * Y value for this X value.
 *
 * @author par7kor
 */
// CDA-138
public class CurveInterpolationTempVO {

  /**
   * X value for a label name.
   */
  private double xAxisValue;
  /**
   * Y values available in all the cure values for the same label name.
   */
  private List<Double> yAxisValues = new ArrayList<>();
  /**
   * CalDataPhy objects which contains the Y values for this X value.
   */
  private List<CalDataPhy> refCalDataPhys = new ArrayList<>();

  /**
   * Gets X value for a label name.
   *
   * @return double.
   */
  public double getXAxisValue() {
    return this.xAxisValue;
  }

  /**
   * Sets X value for a label name.
   *
   * @param axisValue
   */
  public void setXAxisValue(final double axisValue) {
    this.xAxisValue = axisValue;
  }

  /**
   * Gets Y values available in all the cure values for the same label name.
   *
   * @return list of Y values.
   */
  public List<Double> getYAxisValues() {
    // Task 290992 : Mutable members should not be stored or returned directly
    return new ArrayList<>(this.yAxisValues);
  }

  /**
   * Sets Y values available in all the cure values for the same label name.
   *
   * @param axisValues
   */
  public void setYAxisValues(final List<Double> axisValues) {
    // Task 290992 : Mutable members should not be stored or returned directly
    this.yAxisValues = new ArrayList<>(axisValues);
  }

  /**
   * Gets list of CalDataPhy objects which contains the Y values for this X value.
   *
   * @return list of CalDataPhy objects.
   */
  public List<CalDataPhy> getRefCalDataPhys() {
    // Task 290992 : Mutable members should not be stored or returned directly
    return new ArrayList<>(this.refCalDataPhys);
  }

  /**
   * Sets list of CalDataPhy objects which contains the Y values for this X value.
   *
   * @param refCalDataPhys
   */
  public void setRefCalDataPhys(final List<CalDataPhy> refCalDataPhys) {
    // Task 290992 : Mutable members should not be stored or returned directly
    this.refCalDataPhys = new ArrayList<>(refCalDataPhys);
  }
}
