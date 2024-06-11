/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.common.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bne4cob
 */
public class CnsServerStatisticsSession {

  private Map<String, SessionInfo> sessionInfo = new HashMap<>();
  private Map<String, List<EventInfo>> sessionEventInfo = new HashMap<>();

  /**
   * @return the sessionInfo
   */
  public Map<String, SessionInfo> getSessionInfo() {
    return this.sessionInfo;
  }

  /**
   * @param sessionInfo the sessionInfo to set
   */
  public void setSessionInfo(final Map<String, SessionInfo> sessionInfo) {
    this.sessionInfo = sessionInfo;
  }

  /**
   * @return the sessionEventsMap
   */
  public Map<String, List<EventInfo>> getSessionEventInfo() {
    return this.sessionEventInfo;
  }

  /**
   * @param sessionEventInfoMap the sessionEventsMap to set
   */
  public void setSessionEventInfo(final Map<String, List<EventInfo>> sessionEventInfoMap) {
    this.sessionEventInfo = sessionEventInfoMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return super.toString() + " [sessionInfo=" + this.sessionInfo + ", sessionEvents=" + this.sessionEventInfo + "]";
  }


}
