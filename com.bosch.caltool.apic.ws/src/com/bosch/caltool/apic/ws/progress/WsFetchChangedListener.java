/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.progress;

import com.bosch.caltool.apic.ws.session.Session;
import com.bosch.caltool.caldataanalyzer.event.FetchChangedEvent;
import com.bosch.caltool.caldataanalyzer.listener.IFetchChangedListener;


/**
 * @author imi2si
 */
public abstract class WsFetchChangedListener implements IFetchChangedListener {

  private long handledCalls;
  private final double upperPercentageLimit;
  private final Session sessionStatus;

  public WsFetchChangedListener(final Session sessionStatus, final double upperPercentageLimit) {
    this.upperPercentageLimit = upperPercentageLimit;
    this.sessionStatus = sessionStatus;
  }

  /**
   * Called within fetchChanged. getIntervall gets the value that should be added to the current value.
   */
  public abstract double getInterval();

  /**
   * @param numOfElements
   * @param percentageRange
   * @param elementsWeight
   */
  public double calcInterval(final int numOfElements, final double percentageRange, final double elementsWeight) {

    assert percentageRange <= 100 : "The percentage range should be between 0 and 100";
    assert percentageRange >= 0 : "The percentage range should be between 0 and 100";
    assert (elementsWeight > 0) && (elementsWeight <= 1) : "The elementsWeight should be between 0 and 1";

    double interval = 0D;

    if (numOfElements > 0) {
      interval = (percentageRange / numOfElements) * elementsWeight;
    }

    return interval;
  }

  public long getHandledCalls() {
    return this.handledCalls;
  }

  @Override
  public void fetchChanged(final FetchChangedEvent event) {

    Double curVal = this.sessionStatus.getPercentageFinished();
    this.handledCalls++;


    curVal += getInterval();

    if (curVal > this.upperPercentageLimit) {
      curVal = this.upperPercentageLimit;
    }

    this.sessionStatus.setPercentageFinished(curVal);
  }

  @Override
  public void checkIfCancelled() throws InterruptedException {
    // TODO Auto-generated method stub

  }
}
