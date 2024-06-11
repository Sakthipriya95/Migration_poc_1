/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient.ss;

import com.bosch.calmodel.caldataphy.AtomicValuePhy;
import com.bosch.caltool.apic.ws.client.seriesstatisticsfilter.DefaultSeriesStatisticsFilter;
import com.bosch.caltool.apic.ws.client.seriesstatisticsfilter.ISeriesStatisticsFilter;
import com.bosch.caltool.apic.ws.client.seriesstatisticsfilter.ISeriesStatisticsFilter.DataType;
import com.bosch.caltool.apic.ws.client.seriesstatisticsfilter.ISeriesStatisticsFilter.ValueType;

/**
 * Factory class that creates CalDataAnalyzer objects and Filter objects. Used to create objects for testcases. <br>
 * <b>Usage notes</b>:
 * <ul>
 * <li>Use one of the methods createXXXfilter to create a filter you need.</li>
 * </ul>
 * 
 * @author imi2si
 * @since 1.14
 */
public final class FilterFactory {

  public static DefaultSeriesStatisticsFilter createFunctionFilter(final String name, final String value) {
    return new DefaultSeriesStatisticsFilter(name, DataType.FUNCTION_FILTER, ValueType.EQUALS_VALUE,
        new AtomicValuePhy(value));
  }

  public static DefaultSeriesStatisticsFilter createBaseCompFilter(final String baseCompName,
      final String baseCompVariant) {
    return new DefaultSeriesStatisticsFilter(baseCompName, DataType.BASE_COMPONENTS_FILTER, ValueType.EQUALS_VALUE,
        new AtomicValuePhy(baseCompVariant));
  }

  public static DefaultSeriesStatisticsFilter createSysConstFilter(final String sysConstName, final double sysConstValue) {
    return new DefaultSeriesStatisticsFilter(sysConstName, DataType.SYSTEM_CONSTANTS_FILTER, ValueType.EQUALS_VALUE,
        new AtomicValuePhy(sysConstValue));
  }

  public static DefaultSeriesStatisticsFilter createParamFilter(final String parameterName,
      final ISeriesStatisticsFilter.ValueType valueType, final double parameterValue) {
    return new DefaultSeriesStatisticsFilter(parameterName, DataType.PARAMETER_FILTER, valueType, new AtomicValuePhy(
        parameterValue));
  }

  /**
   * Class should not be initialized because it has only static methods
   */
  private FilterFactory() {}
}
