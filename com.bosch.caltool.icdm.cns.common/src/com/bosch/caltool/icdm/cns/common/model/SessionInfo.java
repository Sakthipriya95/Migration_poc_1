/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.common.model;


/**
 * @author bne4cob
 */
public class SessionInfo {

  private String sessionId;
  private String clientIp;
  private int listenPort;
  private String user;
  private String createdAt;
  private String closedAt;
  private String inactiveAt;
  private String lastContactAt;
  private long totalDataSize;
  private int eventCount;
  private String state;

  /**
   * @return the sessionId
   */
  public String getSessionId() {
    return this.sessionId;
  }

  /**
   * @param sessionId the sessionId to set
   */
  public void setSessionId(final String sessionId) {
    this.sessionId = sessionId;
  }

  /**
   * @return the clientIp
   */
  public String getClientIp() {
    return this.clientIp;
  }

  /**
   * @param clientIp the clientIp to set
   */
  public void setClientIp(final String clientIp) {
    this.clientIp = clientIp;
  }

  /**
   * @return the listenPort
   */
  public int getListenPort() {
    return this.listenPort;
  }

  /**
   * @param listenPort the listenPort to set
   */
  public void setListenPort(final int listenPort) {
    this.listenPort = listenPort;
  }


  /**
   * @return the user
   */
  public String getUser() {
    return this.user;
  }

  /**
   * @param user the user to set
   */
  public void setUser(final String user) {
    this.user = user;
  }

  /**
   * @return the createdAt
   */
  public String getCreatedAt() {
    return this.createdAt;
  }


  /**
   * @param createdAt the createdAt to set
   */
  public void setCreatedAt(final String createdAt) {
    this.createdAt = createdAt;
  }


  /**
   * @return the closedAt
   */
  public String getClosedAt() {
    return this.closedAt;
  }


  /**
   * @param closedAt the closedAt to set
   */
  public void setClosedAt(final String closedAt) {
    this.closedAt = closedAt;
  }


  /**
   * @return the inactiveAt
   */
  public String getInactiveAt() {
    return this.inactiveAt;
  }


  /**
   * @param inactiveAt the inactiveAt to set
   */
  public void setInactiveAt(final String inactiveAt) {
    this.inactiveAt = inactiveAt;
  }


  /**
   * @return the state
   */
  public String getState() {
    return this.state;
  }


  /**
   * @param state the state to set
   */
  public void setState(final String state) {
    this.state = state;
  }


  /**
   * @return the lastContactAt
   */
  public String getLastContactAt() {
    return this.lastContactAt;
  }


  /**
   * @param lastContactAt the lastContactAt to set
   */
  public void setLastContactAt(final String lastContactAt) {
    this.lastContactAt = lastContactAt;
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
   * @return the eventCount
   */
  public int getEventCount() {
    return this.eventCount;
  }

  /**
   * @param eventCount the eventCount to set
   */
  public void setEventCount(final int eventCount) {
    this.eventCount = eventCount;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "SessionInfo [sessionId=" + this.sessionId + ", clientIp=" + this.clientIp + ", listenPort=" +
        this.listenPort + ", createdAt=" + this.createdAt + ", state=" + this.state + ", inactiveAt=" +
        this.inactiveAt + ", closedAt=" + this.closedAt + "]";
  }

}
