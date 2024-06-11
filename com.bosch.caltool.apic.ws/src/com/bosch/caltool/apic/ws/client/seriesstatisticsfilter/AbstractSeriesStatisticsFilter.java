/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.seriesstatisticsfilter;

import java.math.BigDecimal;

import com.bosch.calcomp.caldataanalyzer.filter.IFilter;
import com.bosch.calcomp.caldataanalyzer.model.DataHolder;
import com.bosch.caltool.apic.ws.AtomicValuePhy;
import com.bosch.caltool.apic.ws.DefaultSeriesStatisticsFilterType;


/**
 * Abstract adapter class that provides functionalities to convert a webservice series statistics filter to a IFilter
 * object that can be read by the CalDataAnalyzer. An DefaultSeriesStatisticsFilterType is passed and translated into a
 * CalDataAnalyzer filter.<br>
 * Implements java.lang.Comparable to compare two filters against each other to determine if they are equal. Compare is
 * used instead of equals because in the CalDataAnalyzer equals is used in antother context.<br>
 * <b>Usage notes</b>:
 * <ul>
 * <li>Instanziate this class with a DefaultSeriesStatisticsFilterType out of the webservice.</li>
 * <li>Use {@link AbstractSeriesStatisticsFilter#isValid()} to check if the created filter is valid.</li>
 * <li>Use {@link AbstractSeriesStatisticsFilter#compareTo(AbstractSeriesStatisticsFilter)} to compare this filter with
 * another filter.</li>
 * <li>Use {@link AbstractSeriesStatisticsFilter#getFilter()} to get a filter that can be passed to the CalDataAnalyzer.
 * </li>
 * </ul>
 *
 * @author imi2si
 * @since 1.14
 * @see java.lang.Comparable
 */
public abstract class AbstractSeriesStatisticsFilter implements Comparable<AbstractSeriesStatisticsFilter> {

  /**
   * Data Type of the current object. One of the values of ISeriesStatisticsFilter.DataType.
   */
  protected transient AbstractSeriesStatisticsFilter.DataType dataType =
      AbstractSeriesStatisticsFilter.DataType.PARAMETER_FILTER;

  /**
   * The filter of the web service that should be converted in a CalDataAnalyzer filter.
   */
  protected transient DefaultSeriesStatisticsFilterType filter;

  /**
   * The AtomicValuePhy of the web service.
   */
  protected transient AtomicValuePhy atomicValue;

  /**
   * The CalDataAnalyzer DataHolder.
   */
  protected transient DataHolder dataHolder;

  /**
   * The data types available for the CalDataAnalyzer filters.
   */
  public static enum DataType {
                               /**
                                * Represents a parameter filter (As label existance filter and If there's an
                                * AtomicValuePhy as value filter)
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
                                 * Represents a equals filter, that means parameter values are considerer in
                                 * CalDataAnalyzerthat are qual to the filter.
                                 */
                                EQUALS_VALUE
  }

  /**
   * Constructor that needs an DefaultSeriesStatisticsFilterType and a data holder. After passing these two parameters
   * the conversiton happends. That means, that after calling the constructor, the result can be get by calling
   * {@link AbstractSeriesStatisticsFilter#getFilter() getFilter()}.
   *
   * @param filter an DefaultSeriesStatisticsFilterType object from the webservice that should be translated to an
   *          IFilter object
   * @param dataHolder the DataHolder object from the CalDataAnalyzer
   */
  public AbstractSeriesStatisticsFilter(final DefaultSeriesStatisticsFilterType filter, final DataHolder dataHolder) {
    this.filter = filter;
    this.atomicValue = this.filter.getValue();
    this.dataHolder = dataHolder;
  }

  /**
   * Check for record validity. An filter is basically valid if the source datatype of the webservice filter matches the
   * filter for this adapter object. If there are additional checks required, for example if a value is existing, the
   * method should be overwritten in a subclass. <br>
   * If <b>false</b> is returned, the filter should <b>not be used</b>.
   *
   * @return true if the filter is valid, otherwise false.
   */
  public boolean isValid() {
    return this.dataType.toString().equalsIgnoreCase(this.filter.getDataType());
  }


  /**
   * Returns the original passed DefaultSeriesStatisticsFilterType passed to the constructor. Needed for the compareTo
   * method to compare this filter against another filter:
   *
   * @return the original DefaultSeriesStatisticsFilterType.
   */
  public final DefaultSeriesStatisticsFilterType getSourceFilter() {
    return this.filter;
  }

  /**
   * Checks, if the filter value of this filter is numeric or not. Depending on the return value different methods in
   * the CalDataAnalyzer must be called to set up the filter.
   *
   * @return true, if the filter value is numeric, otherwise false
   */
  protected final boolean isValueNumeric() {
    final String strValue = this.atomicValue.getStringValue();
    final BigDecimal dblValue = BigDecimal.valueOf(this.atomicValue.getDoubleValue());
    final BigDecimal zero = BigDecimal.valueOf(0.0);

    return (!zero.equals(dblValue)) || (("0.0").equals(strValue) && (zero.equals(dblValue)));
  }

  /**
   * Returns the converted filter of the webservice as a IFilter object that can be passed to the CalDataAnalyzer.
   *
   * @return an IFilter objects
   */
  public abstract IFilter getFilter();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract int compareTo(AbstractSeriesStatisticsFilter comparator);

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract String toString();
}
