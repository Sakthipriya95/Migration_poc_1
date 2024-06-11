/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.seriesstatisticsfilter.wsadapter;

import com.bosch.caltool.apic.ws.client.APICStub;
import com.bosch.caltool.apic.ws.client.seriesstatisticsfilter.DefaultSeriesStatisticsFilter;
import com.bosch.caltool.apic.ws.client.seriesstatisticsfilter.ISeriesStatisticsFilter;


/**
 * @author imi2si
 */
public class DefaultSeriesStatisticsFilterWsAdapter extends APICStub.DefaultSeriesStatisticsFilterType implements
    ISeriesStatisticsFilter {

  DefaultSeriesStatisticsFilter filter;

  public DefaultSeriesStatisticsFilterWsAdapter(final DefaultSeriesStatisticsFilter filter) {
    this.filter = filter;
    super.setName(getName());
    super.setDataType(getDataType());
    super.setValueType(getValueType());
    super.setValue(getValue());
  }

  @Override
  public java.lang.String getName() {
    return this.filter.getName();
  }

  @Override
  public java.lang.String getDataType() {
    return this.filter.getDataType().toString();
  }

  @Override
  public java.lang.String getValueType() {
    return this.filter.getValueType().toString();
  }

  @Override
  public APICStub.AtomicValuePhy getValue() {
    return new AtomicValuePhyWsAdapter(this.filter.getValue());
  }

}
