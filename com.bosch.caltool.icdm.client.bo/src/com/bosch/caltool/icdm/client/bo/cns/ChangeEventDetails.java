/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cns;

import com.bosch.caltool.datamodel.core.cns.ChangeEvent;

/**
 * @author BNE4COB
 */
public class ChangeEventDetails {

  private String sessionId;
  private String producerId;
  private int dataLength;
  private String createdAt;
  private String dataAvailable;
  private ChangeEvent event;

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
   * @return the dataAvailable
   */
  public String getDataAvailable() {
    return this.dataAvailable;
  }

  /**
   * @param dataAvailable the dataAvailable to set
   */
  public void setDataAvailable(final String dataAvailable) {
    this.dataAvailable = dataAvailable;
  }

  /**
   * @return the event
   */
  public ChangeEvent getEvent() {
    return this.event;
  }

  /**
   * @param event the event to set
   */
  public void setEvent(final ChangeEvent event) {
    this.event = event;
  }


}
