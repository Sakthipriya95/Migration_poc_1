/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.seriesstatisticsfilter.wsadapter;

import com.bosch.calmodel.caldataphy.AtomicValuePhy;
import com.bosch.caltool.apic.ws.client.APICStub;


/**
 * @author imi2si
 */
public class AtomicValuePhyWsAdapter extends APICStub.AtomicValuePhy {

  AtomicValuePhy atomicValuePhy;

  public AtomicValuePhyWsAdapter(final AtomicValuePhy atomicValuePhy) {
    this.atomicValuePhy = atomicValuePhy;

    // Webservice excepts a non empty AtomicValuePhy; iCDM could deliver en emptys one if no min/max values are entered
    if (atomicValuePhy == null) {
      this.atomicValuePhy = new AtomicValuePhy("");
    }

    super.setCharValue(getCharValue());
    super.setDoubleValue(getDoubleValue());
    super.setStringValue(getStringValue());
  }

  @Override
  public java.lang.String getStringValue() {
    return this.atomicValuePhy.getSValue();
  }

  @Override
  public java.lang.String getCharValue() {
    return this.atomicValuePhy.getSValue();
  }

  @Override
  public double getDoubleValue() {
    return this.atomicValuePhy.getDValue();
  }
}
