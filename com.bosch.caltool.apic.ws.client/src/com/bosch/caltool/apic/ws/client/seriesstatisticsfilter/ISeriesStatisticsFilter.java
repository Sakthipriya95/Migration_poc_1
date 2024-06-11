/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.seriesstatisticsfilter;


/**
 * @author imi2si
 */
public interface ISeriesStatisticsFilter {

  /**
   * The data types available for the CalDataAnalyzer filters.
   */
  public static enum DataType {
    /**
     * Represents a parameter filter (As label existance filter and If there's an AtomicValuePhy as value filter)
     */
    PARAMETER_FILTER,
    /**
     * Represents a system constants filter
     */
    SYSTEM_CONSTANTS_FILTER,
    /**
     * Represents a function filter
     */
    FUNCTION_FILTER,
    /**
     * Represents a base components filter
     */
    BASE_COMPONENTS_FILTER
  }

  /**
   * The filter value types. Describes if a filter is a max, min or equals filter
   */
  public static enum ValueType {
    /**
     * Represents a min value filter, that means the value must be larger or equals.
     */
    MIN_VALUE,
    /**
     * Represents a max value filter, that means the value must be smaller or equals.
     */
    MAX_VALUE,
    /**
     * Represents a equals filter, that means parameter values are considerer in CalDataAnalyzerthat are qual to the
     * filter.
     */
    EQUALS_VALUE
  }
}
