/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.seriesstatisticsfilter;

import com.bosch.calcomp.caldataanalyzer.filter.IFilter;
import com.bosch.calcomp.caldataanalyzer.filter.datasetfilter.impl.LabelExistDatasetFilterImpl;
import com.bosch.calcomp.caldataanalyzer.model.DataHolder;
import com.bosch.caltool.apic.ws.DefaultSeriesStatisticsFilterType;

/**
 * Implementation of an AbstractSeriesStatisticsFilter for a CalDataAnalyzer LabelExistsAdapter.
 * 
 * @author imi2si
 * @since 1.14
 * @see com.bosch.caltool.apic.ws.client.seriesstatisticsfilter.AbstractSeriesStatisticsFilter
 * @see java.lang.Comparable
 */
public class LabelExistsAdapter extends AbstractSeriesStatisticsFilter {

  /**
   * The LabelExistDatasetFilterImpl as the result of the Translation process.
   */
  private final transient LabelExistDatasetFilterImpl iFilter;

  /**
   * Default constructor. A DefaultSeriesStatisticsFilterType and a DataHolder are passed. The
   * DefaultSeriesStatisticsFilterType is translated to a LabelExistDatasetFilterImpl. If
   * {@link com.bosch.caltool.apic.ws.client.seriesstatisticsfilter.LabelExistsAdapter#isValid() isValid()} returns
   * true, the translation process was successfull and the filter can be used.
   * 
   * @param filter an DefaultSeriesStatisticsFilterType object from the webservice that should be translated to an
   *          IFilter object
   * @param dataHolder the DataHolder object from the CalDataAnalyzer
   */
  public LabelExistsAdapter(final DefaultSeriesStatisticsFilterType filter, final DataHolder dataHolder) {
    super(filter, dataHolder);
    super.dataType = AbstractSeriesStatisticsFilter.DataType.PARAMETER_FILTER;
    this.iFilter = new LabelExistDatasetFilterImpl(filter.getName(), false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final int compareTo(final AbstractSeriesStatisticsFilter compObject) {
    if (this.filter.getDataType().equals(compObject.getSourceFilter().getDataType()) &&
        this.filter.getName().equals(compObject.getSourceFilter().getName())

    ) {
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
