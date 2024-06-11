/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.seriesstatisticsfilter;

import com.bosch.calmodel.caldataphy.AtomicValuePhy;


/**
 * @author imi2si
 */
public class DefaultSeriesStatisticsFilter implements ISeriesStatisticsFilter {

  private String name;
  private AtomicValuePhy value;
  private ISeriesStatisticsFilter.DataType dataType;
  private ISeriesStatisticsFilter.ValueType valueType;

  public DefaultSeriesStatisticsFilter(final String name, final ISeriesStatisticsFilter.DataType dataType,
      final ISeriesStatisticsFilter.ValueType valueType, final AtomicValuePhy value) {
    this.name = name;
    this.value = value;
    this.dataType = dataType;
    this.valueType = valueType;
  }

  /**
   * @return the name
   */
  public String getName() {
    return this.name;

  }

  /**
   * @param name the name to set
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * @return the value
   */
  public AtomicValuePhy getValue() {
    return this.value;
  }

  /**
   * @param value the value to set
   */
  public void setValue(final AtomicValuePhy value) {
    this.value = value;
  }

  /**
   * @return the dataType
   */
  public ISeriesStatisticsFilter.DataType getDataType() {
    return this.dataType;
  }

  /**
   * @param dataType the dataType to set
   */
  public void setDataType(final ISeriesStatisticsFilter.DataType dataType) {
    this.dataType = dataType;
  }

  /**
   * @return the valueType
   */
  public ISeriesStatisticsFilter.ValueType getValueType() {
    return this.valueType;
  }

  /**
   * @param valueType the valueType to set
   */
  public void setValueType(final ISeriesStatisticsFilter.ValueType valueType) {
    this.valueType = valueType;
  }
}
