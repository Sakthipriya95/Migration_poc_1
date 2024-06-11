/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.common.model;


/**
 * @author bne4cob
 */
public class CnsServerStatisticsSummary {

  private Long latestEventId;

  private int totalEvents;

  private int totalSessions;

  private int activeSessions;

  private int peakActiveSession;

  private String peakActiveSessionAt;

  private int closedSessions;

  private int inactiveSessions;

  private String startTime;

  private String currentTime;

  private long totalDataSize;

  private long currentDataSize;

  private long peakDataSize;

  private String peakDataSizeAt;

  private int totalProducers;

  private int evntNoSessionCount;

  private int evntInvalidSessionCount;

  private int evntClosedSessionCount;

  /**
   * @return the latestEventId
   */
  public Long getLatestEventId() {
    return this.latestEventId;
  }


  /**
   * @param latestEventId the latestEventId to set
   */
  public void setLatestEventId(final Long latestEventId) {
    this.latestEventId = latestEventId;
  }


  /**
   * @return the totalEvents
   */
  public int getTotalEvents() {
    return this.totalEvents;
  }


  /**
   * @param totalEvents the totalEvents to set
   */
  public void setTotalEvents(final int totalEvents) {
    this.totalEvents = totalEvents;
  }


  /**
   * @return the totalSessions
   */
  public int getTotalSessions() {
    return this.totalSessions;
  }


  /**
   * @param totalSessions the totalSessions to set
   */
  public void setTotalSessions(final int totalSessions) {
    this.totalSessions = totalSessions;
  }


  /**
   * @return the activeSessions
   */
  public int getActiveSessions() {
    return this.activeSessions;
  }


  /**
   * @param activeSessions the activeSessions to set
   */
  public void setActiveSessions(final int activeSessions) {
    this.activeSessions = activeSessions;
  }


  /**
   * @return the peakActiveSession
   */
  public int getPeakActiveSession() {
    return this.peakActiveSession;
  }


  /**
   * @param peakActiveSession the peakActiveSession to set
   */
  public void setPeakActiveSession(final int peakActiveSession) {
    this.peakActiveSession = peakActiveSession;
  }


  /**
   * @return the peakActiveSessionAt
   */
  public String getPeakActiveSessionAt() {
    return this.peakActiveSessionAt;
  }


  /**
   * @param peakActiveSessionAt the peakActiveSessionAt to set
   */
  public void setPeakActiveSessionAt(final String peakActiveSessionAt) {
    this.peakActiveSessionAt = peakActiveSessionAt;
  }


  /**
   * @return the closedSessions
   */
  public int getClosedSessions() {
    return this.closedSessions;
  }


  /**
   * @param closedSessions the closedSessions to set
   */
  public void setClosedSessions(final int closedSessions) {
    this.closedSessions = closedSessions;
  }


  /**
   * @return the inactiveSessions
   */
  public int getInactiveSessions() {
    return this.inactiveSessions;
  }


  /**
   * @param inactiveSessions the inactiveSessions to set
   */
  public void setInactiveSessions(final int inactiveSessions) {
    this.inactiveSessions = inactiveSessions;
  }


  /**
   * @return the startTime
   */
  public String getStartTime() {
    return this.startTime;
  }


  /**
   * @param startTime the startTime to set
   */
  public void setStartTime(final String startTime) {
    this.startTime = startTime;
  }


  /**
   * @return the currentTime
   */
  public String getCurrentTime() {
    return this.currentTime;
  }


  /**
   * @param currentTime the currentTime to set
   */
  public void setCurrentTime(final String currentTime) {
    this.currentTime = currentTime;
  }


  /**
   * @return the totalDataSize
   */
  public long getTotalDataSize() {
    return this.totalDataSize;
  }


  /**
   * @param totalDataSize the totalDataSize to set
   */
  public void setTotalDataSize(final long totalDataSize) {
    this.totalDataSize = totalDataSize;
  }


  /**
   * @return the currentDataSize
   */
  public long getCurrentDataSize() {
    return this.currentDataSize;
  }


  /**
   * @param currentDataSize the currentDataSize to set
   */
  public void setCurrentDataSize(final long currentDataSize) {
    this.currentDataSize = currentDataSize;
  }


  /**
   * @return the peakDataSize
   */
  public long getPeakDataSize() {
    return this.peakDataSize;
  }


  /**
   * @param peakDataSize the peakDataSize to set
   */
  public void setPeakDataSize(final long peakDataSize) {
    this.peakDataSize = peakDataSize;
  }


  /**
   * @return the peakDataSizeAt
   */
  public String getPeakDataSizeAt() {
    return this.peakDataSizeAt;
  }


  /**
   * @param peakDataSizeAt the peakDataSizeAt to set
   */
  public void setPeakDataSizeAt(final String peakDataSizeAt) {
    this.peakDataSizeAt = peakDataSizeAt;
  }


  /**
   * @return the totalProducers
   */
  public int getTotalProducers() {
    return this.totalProducers;
  }


  /**
   * @param totalProducers the totalProducers to set
   */
  public void setTotalProducers(final int totalProducers) {
    this.totalProducers = totalProducers;
  }


  /**
   * @return the evntNoSessionCount
   */
  public int getEvntNoSessionCount() {
    return this.evntNoSessionCount;
  }


  /**
   * @param evntNoSessionCount the evntNoSessionCount to set
   */
  public void setEvntNoSessionCount(final int evntNoSessionCount) {
    this.evntNoSessionCount = evntNoSessionCount;
  }


  /**
   * @return the evntInvalidSessionCount
   */
  public int getEvntInvalidSessionCount() {
    return this.evntInvalidSessionCount;
  }


  /**
   * @param evntInvalidSessionCount the evntInvalidSessionCount to set
   */
  public void setEvntInvalidSessionCount(final int evntInvalidSessionCount) {
    this.evntInvalidSessionCount = evntInvalidSessionCount;
  }


  /**
   * @return the evntClosedSessionCount
   */
  public int getEvntClosedSessionCount() {
    return this.evntClosedSessionCount;
  }


  /**
   * @param evntClosedSessionCount the evntClosedSessionCount to set
   */
  public void setEvntClosedSessionCount(final int evntClosedSessionCount) {
    this.evntClosedSessionCount = evntClosedSessionCount;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "CNS Server Statistics Summary [startTime=" + this.startTime + ", latestEventId=" + this.latestEventId +
        ", totalEvents=" + this.totalEvents + ", totalSessions=" + this.totalSessions + ", activeSessions=" +
        this.activeSessions + ", closedSessions=" + this.closedSessions + ", inactiveSessions=" +
        this.inactiveSessions + "]";
  }


}
