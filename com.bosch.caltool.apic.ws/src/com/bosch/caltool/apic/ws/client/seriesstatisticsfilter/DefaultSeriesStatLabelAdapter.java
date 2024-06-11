/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.seriesstatisticsfilter;

import java.math.BigDecimal;

import com.bosch.calcomp.caldataanalyzer.filter.IFilter;
import com.bosch.calcomp.caldataanalyzer.filter.labelfilter.impl.LabelValueFilterImpl;
import com.bosch.calcomp.caldataanalyzer.model.DataHolder;
import com.bosch.caltool.apic.ws.DefaultSeriesStatisticsFilterType;

/**
 * Implementation of an AbstractSeriesStatisticsFilter for a CalDataAnalyzer LabelValueFilterImpl.
 *
 * @author imi2si
 * @since 1.14
 * @see com.bosch.caltool.apic.ws.client.seriesstatisticsfilter.AbstractSeriesStatisticsFilter
 * @see java.lang.Comparable
 */
public class DefaultSeriesStatLabelAdapter extends AbstractSeriesStatisticsFilter {

  /**
   * The LabelValueFilterImpl as the result of the Translation process.
   */
  private final transient LabelValueFilterImpl iFilter;

  /**
   * Default constructor. A DefaultSeriesStatisticsFilterType and a DataHolder are passed. The
   * DefaultSeriesStatisticsFilterType is translated to a LabelValueFilterImpl. If
   * {@link com.bosch.caltool.apic.ws.client.seriesstatisticsfilter.DefaultSeriesStatLabelAdapter#isValid() isValid()}
   * returns true, the translation process was successfull and the filter can be used.
   *
   * @param filter an DefaultSeriesStatisticsFilterType object from the webservice that should be translated to an
   *          IFilter object
   * @param dataHolder the DataHolder object from the CalDataAnalyzer
   */
  public DefaultSeriesStatLabelAdapter(final DefaultSeriesStatisticsFilterType filter, final DataHolder dataHolder) {
    super(filter, dataHolder);
    super.dataType = AbstractSeriesStatisticsFilter.DataType.PARAMETER_FILTER;
    this.iFilter = new LabelValueFilterImpl(filter.getName(), dataHolder);

    // If the filter is not valid a translation makes no sense. The filter shouldn't be used if he is invalid
    if (isValid()) { // NOPMD - calling during construction is not a problem at this point
      setFilter(); // NOPMD - calling during construction is not a problem at this point
    }
  }

  /**
   * Converts the webservice filter values into a LabelValueFilterImpl. Depending on the value type EQUALS_VALUE,
   * MAX_VALUE or MAX_VALUE the associated method in the LabelValueFilterImpl object is called.
   */
  public final void setFilter() {
    if (isValueNumeric()) {
      switch (this.filter.getValueType()) {
        case "EQUALS_VALUE":
          this.iFilter.equals(this.atomicValue.getDoubleValue(), 0, false);
          break;
        case "MAX_VALUE":
          this.iFilter.lessOrEqual(this.atomicValue.getDoubleValue(), 0);
          break;
        case "MIN_VALUE":
          this.iFilter.greaterOrEqual(this.atomicValue.getDoubleValue(), 0);
          break;
        default:
          throw new IllegalArgumentException(
              "\"" + this.filter.getValueType() + "\" is not a valid datatype for filter " + this.filter.getName());
      }
    }
    // For String values, only the equals method is possible. It wouldn't make sense to use greater or less for a String
    else {
      this.iFilter.equals(this.atomicValue.getStringValue(), LabelValueFilterImpl.AT_ALL_POSITION, false);
    }
  }

  @Override
  public final boolean isValid() {
    return super.isValid() && !this.filter.getValue().getStringValue().isEmpty();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final int compareTo(final AbstractSeriesStatisticsFilter compObject) {
    if (this.filter.getDataType().equals(compObject.getSourceFilter().getDataType()) &&
        this.filter.getName().equals(compObject.getSourceFilter().getName()) && checkValues(compObject) &&
        (BigDecimal.valueOf(this.filter.getValue().getDoubleValue())
            .equals(BigDecimal.valueOf(compObject.getSourceFilter().getValue().getDoubleValue())))) {
      return 0; // NOPMD
    }

    return 1;
  }

  /**
   * @param compObject
   * @return
   */
  private boolean checkValues(final AbstractSeriesStatisticsFilter compObject) {
    return this.filter.getValueType().equals(compObject.getSourceFilter().getValueType()) &&
        this.filter.getValue().getStringValue().equals(compObject.getSourceFilter().getValue().getStringValue()) &&
        this.filter.getValue().getCharValue().equals(compObject.getSourceFilter().getValue().getCharValue());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final IFilter getFilter() {
    return this.iFilter;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    // TODO Auto-generated method stub
    return this.iFilter.toString();
  }
}
