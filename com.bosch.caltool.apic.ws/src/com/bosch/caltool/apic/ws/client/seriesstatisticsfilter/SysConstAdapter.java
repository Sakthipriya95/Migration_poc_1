/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.seriesstatisticsfilter;

import java.math.BigDecimal;

import com.bosch.calcomp.caldataanalyzer.filter.IFilter;
import com.bosch.calcomp.caldataanalyzer.filter.datasetfilter.impl.A2LSysConstFilter;
import com.bosch.calcomp.caldataanalyzer.model.DataHolder;
import com.bosch.caltool.apic.ws.DefaultSeriesStatisticsFilterType;

/**
 * Implementation of an AbstractSeriesStatisticsFilter for a CalDataAnalyzer System COnstants Adapter.
 *
 * @author imi2si
 * @since 1.14
 * @see com.bosch.caltool.apic.ws.client.seriesstatisticsfilter.AbstractSeriesStatisticsFilter
 * @see java.lang.Comparable
 */
public class SysConstAdapter extends AbstractSeriesStatisticsFilter {

  /**
   * The A2LSysConstFilter as the result of the Translation process.
   */
  private final transient A2LSysConstFilter iFilter;

  /**
   * Default constructor. A DefaultSeriesStatisticsFilterType and a DataHolder are passed. The
   * DefaultSeriesStatisticsFilterType is translated to a A2LSysConstFilter. If
   * {@link com.bosch.caltool.apic.ws.client.seriesstatisticsfilter.LabelExistsAdapter#isValid() isValid()} returns
   * true, the translation process was successfull and the filter can be used.
   *
   * @param filter an DefaultSeriesStatisticsFilterType object from the webservice that should be translated to an
   *          IFilter object
   * @param dataHolder the DataHolder object from the CalDataAnalyzer
   */
  public SysConstAdapter(final DefaultSeriesStatisticsFilterType filter, final DataHolder dataHolder) {
    super(filter, dataHolder);
    super.dataType = AbstractSeriesStatisticsFilter.DataType.SYSTEM_CONSTANTS_FILTER;
    this.iFilter = new A2LSysConstFilter(filter.getName(), filter.getValue().getDoubleValue());
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
        this.filter.getName().equals(compObject.getSourceFilter().getName()) &&
        (BigDecimal.valueOf(this.filter.getValue().getDoubleValue())
            .equals(BigDecimal.valueOf(compObject.getSourceFilter().getValue().getDoubleValue())))) {
      return 0; // NOPMD
    }

    return 1;
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
