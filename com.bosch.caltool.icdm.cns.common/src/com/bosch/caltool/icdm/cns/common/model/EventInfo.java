/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.common.model;

/**
 * @author bne4cob
 */
public class EventInfo {

  private Long eventId;
  private String serviceId;
  private String sessionId;
  private String producerId;
  private int dataLength;
  private String createdAt;
  private String dataAvailable;
  private String sessionState;


  /**
   * @return the eventId
   */
  public Long getEventId() {
    return this.eventId;
  }

  /**
   * @param eventId the eventId to set
   */
  public void setEventId(final Long eventId) {
    this.eventId = eventId;
  }


  /**
   * @return the serviceId
   */
  public String getServiceId() {
    return this.serviceId;
  }


  /**
   * @param serviceId the serviceId to set
   */
  public void setServiceId(final String serviceId) {
    this.serviceId = serviceId;
  }

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
   * @return the producerId
   */
  public String getProducerId() {
    return this.producerId;
  }

  /**
   * @param producerId the producerId to set
   */
  public void setProducerId(final String producerId) {
    this.producerId = producerId;
  }

  /**
   * @return the dataLength
   */
  public int getDataLength() {
    return this.dataLength;
  }

  /**
   * @param dataLength the dataLength to set
   */
  public void setDataLength(final int dataLength) {
    this.dataLength = dataLength;
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
   * @param dataAvailable data available
   */
  public void setDataAvailable(final String dataAvailable) {
    this.dataAvailable = dataAvailable;
  }


  /**
   * @return the dataAvailable
   */
  public String getDataAvailable() {
    return this.dataAvailable;
  }

  /**
   * @return the sessionState
   */
  public String getSessionState() {
    return this.sessionState;
  }

  /**
   * @param sessionState the sessionState to set
   */
  public void setSessionState(final String sessionState) {
    this.sessionState = sessionState;
  }


}
