/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.server.bo.session;

import java.time.Instant;

/**
 * @author bne4cob
 */
class Session {

  private String sessionId;
  private String clientIp;
  private int listenPort;
  private String clientUser;
  private final Instant createdAt = Instant.now();
  private Instant closedAt;
  private Instant inactiveAt;
  private Instant lastContactAt;
  private long totalDataSize;
  private SESSION_STATE state = SESSION_STATE.ACTIVE;

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
   * @return the clientUser
   */
  public String getClientUser() {
    return this.clientUser;
  }

  /**
   * @param clientUser the clientUser to set
   */
  public void setClientUser(final String clientUser) {
    this.clientUser = clientUser;
  }

  /**
   * @return the createdAt
   */
  public Instant getCreatedAt() {
    return this.createdAt;
  }


  /**
   * @return the closedAt
   */
  public Instant getClosedAt() {
    return this.closedAt;
  }


  /**
   * @return the inactiveAt
   */
  public Instant getInactiveAt() {
    return this.inactiveAt;
  }


  /**
   * @return the state
   */
  public SESSION_STATE getState() {
    return this.state;
  }

  void close() {
    this.closedAt = Instant.now();
    this.state = SESSION_STATE.CLOSED;
  }

  void deactivate() {
    this.inactiveAt = Instant.now();
    this.state = SESSION_STATE.INACTIVE;
  }

  void activate() {
    this.inactiveAt = null;
    this.state = SESSION_STATE.ACTIVE;
  }


  /**
   * @return the lastContactAt
   */
  public Instant getLastContactAt() {
    return this.lastContactAt;
  }


  /**
   * Update the last contact time to current time
   */
  public void updateLastContactAt() {
    this.lastContactAt = Instant.now();
  }


  /**
   * @return the totalDataSize
   */
  public long getTotalDataSize() {
    return this.totalDataSize;
  }


  /**
   * @param dataSize the size of current data
   */
  public void addToTotalDataSize(final long dataSize) {
    this.totalDataSize += dataSize;
  }


}
