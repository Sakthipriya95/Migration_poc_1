/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.progress;

import com.bosch.caltool.apic.ws.session.Session;


/**
 * @author imi2si
 */
public class WsStandardFetchChangedListener extends WsFetchChangedListener {

  private final double interval;

  /**
   * @param sessID
   * @param statusAsyncExecution
   * @param upperPercentageLimit
   */
  public WsStandardFetchChangedListener(final Session sessionStatus, final double upperPercentageLimit,
      final int numberOfElements, final double percentageRange, final double elementsWeight) {
    super(sessionStatus, upperPercentageLimit);
    this.interval = calcInterval(numberOfElements, percentageRange, elementsWeight);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double getInterval() {
    // TODO Auto-generated method stub
    return this.interval;
  }
}
