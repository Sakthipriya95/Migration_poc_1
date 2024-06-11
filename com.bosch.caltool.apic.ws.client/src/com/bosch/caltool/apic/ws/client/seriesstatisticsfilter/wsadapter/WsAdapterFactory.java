/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.seriesstatisticsfilter.wsadapter;

import java.util.ArrayList;
import java.util.List;

import com.bosch.caltool.apic.ws.client.seriesstatisticsfilter.DefaultSeriesStatisticsFilter;
import com.bosch.caltool.apic.ws.client.seriesstatisticsfilter.ISeriesStatisticsFilter;


/**
 * @author imi2si
 */
public class WsAdapterFactory {

  ISeriesStatisticsFilter[] filters;
  List<DefaultSeriesStatisticsFilterWsAdapter> defaultSeriesStatFilter = new ArrayList<>();

  public WsAdapterFactory(final ISeriesStatisticsFilter[] filters) {
    this.filters = filters;
    createArrays();
  }

  public DefaultSeriesStatisticsFilterWsAdapter[] getDefaultSeriesStatFilterWsAdapter() {
    return this.defaultSeriesStatFilter.toArray(new DefaultSeriesStatisticsFilterWsAdapter[0]);
  }

  private void createArrays() {
    for (ISeriesStatisticsFilter filter : this.filters) {
      if (filter instanceof DefaultSeriesStatisticsFilter) {
        this.defaultSeriesStatFilter.add(new DefaultSeriesStatisticsFilterWsAdapter(
            (DefaultSeriesStatisticsFilter) filter));
      }
    }
  }

}
