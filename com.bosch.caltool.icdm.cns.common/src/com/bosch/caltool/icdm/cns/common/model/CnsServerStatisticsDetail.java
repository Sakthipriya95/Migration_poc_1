/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.common.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author bne4cob
 */
public class CnsServerStatisticsDetail {

  private CnsServerStatisticsSummary summary;

  private List<SessionInfo> sessionInfo = new ArrayList<>();

  private Map<String, Set<Long>> sessionEvents = new HashMap<>();

  private List<Long> eventIds = new ArrayList<>();


  /**
   * @return the summary
   */
  public CnsServerStatisticsSummary getSummary() {
    return this.summary;
  }


  /**
   * @param summary the summary to set
   */
  public void setSummary(final CnsServerStatisticsSummary summary) {
    this.summary = summary;
  }


  /**
   * @return the allSessions
   */
  public List<SessionInfo> getSessionInfo() {
    return this.sessionInfo == null ? null : new ArrayList<>(this.sessionInfo);
  }


  /**
   * @param sessionInfo the sessionInfo to set
   */
  public void setSessionInfo(final List<SessionInfo> sessionInfo) {
    this.sessionInfo = sessionInfo == null ? null : new ArrayList<>(sessionInfo);
  }

  /**
   * @return the sessionEventsMap
   */
  public Map<String, Set<Long>> getSessionEvents() {
    return this.sessionEvents == null ? null : new HashMap<>(this.sessionEvents);
  }


  /**
   * @param sessionEventsMap the sessionEventsMap to set
   */
  public void setSessionEvents(final Map<String, Set<Long>> sessionEventsMap) {
    this.sessionEvents = sessionEventsMap == null ? null : new HashMap<>(sessionEventsMap);
  }


  /**
   * @return the eventIds
   */
  public List<Long> getEventIds() {
    return this.eventIds == null ? null : new ArrayList<>(this.eventIds);
  }


  /**
   * @param eventIds the eventIds to set
   */
  public void setEventIds(final List<Long> eventIds) {
    this.eventIds = eventIds == null ? null : new ArrayList<>(eventIds);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "CnsServerStatisticsDetail [summary=" + this.summary + "]";
  }


}
