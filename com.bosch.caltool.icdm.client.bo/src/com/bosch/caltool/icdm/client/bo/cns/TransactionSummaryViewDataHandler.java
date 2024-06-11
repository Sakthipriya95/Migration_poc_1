/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cns;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.cns.client.CnsServiceClientException;
import com.bosch.caltool.icdm.logger.CDMLogger;


/**
 * @author elm1cob
 */
public class TransactionSummaryViewDataHandler {

  private final SortedSet<CnsChangeEventSummary> eventSummarySet = new TreeSet<>();

  private Long latestEventId;

  private final CnsDataConsumerServiceClientWrapper cnsDataConsumerServiceClientWrapper =
      new CnsDataConsumerServiceClientWrapper();

  /**
   * Fetch the set of summary of events occured
   */
  public void fetchAllEventSummary() {
    try {

      Map<Long, ChangeEventDetails> eventsMap = this.cnsDataConsumerServiceClientWrapper.getEventsOfCurrentSession();

      eventsMap.values().stream().map(CnsChangeEventSummary::new).forEach(this.eventSummarySet::add);
      this.latestEventId = this.cnsDataConsumerServiceClientWrapper.getLatestEventId();
    }
    catch (CnsServiceClientException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * Get the set of events occured after the latest event ID
   */
  public SortedSet<CnsChangeEventSummary> getLatestChangeEvents() {
    try {
      Map<Long, ChangeEventDetails> latestEventsMap =
          this.cnsDataConsumerServiceClientWrapper.getEventsOfCurrentSession(this.latestEventId);

      latestEventsMap.values().stream().map(CnsChangeEventSummary::new).forEach(this.eventSummarySet::add);
      this.latestEventId = this.cnsDataConsumerServiceClientWrapper.getLatestEventId();
    }
    catch (CnsServiceClientException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return this.eventSummarySet;
  }

  /**
   * @return the eventSummary
   */
  public SortedSet<CnsChangeEventSummary> getEventSummary() {
    if (this.eventSummarySet.isEmpty()) {
      fetchAllEventSummary();
    }

    return this.eventSummarySet;
  }

}
