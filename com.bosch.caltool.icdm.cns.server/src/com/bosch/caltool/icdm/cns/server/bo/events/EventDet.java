/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.server.bo.events;

import java.time.Instant;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.cns.server.bo.CnsObjectStore;
import com.bosch.caltool.icdm.cns.server.bo.session.SESSION_STATE;

/**
 * @author bne4cob
 */
class EventDet {

  private final Long eventId;
  private final String serviceId;
  private final String sessionId;
  private byte[] data;
  private final int dataSize;
  /**
   * State of session at the time of event creation
   */
  private final SESSION_STATE sessionState;

  private final Instant createdAt = Instant.now();
  private final String producerId;

  EventDet(final Long eventId, final String sessionId, final String producerId, final String serviceId,
      final byte[] data, final SESSION_STATE sessionState) {
    this.eventId = eventId;
    this.sessionId = sessionId;
    this.producerId = producerId;
    this.serviceId = serviceId;
    this.data = data == null ? null : data.clone();
    this.dataSize = data == null ? 0 : data.length;
    this.sessionState = sessionState;
  }


  /**
   * @return the eventId
   */
  public Long getEventId() {
    return this.eventId;
  }

  /**
   * @return service id
   */
  public String getServiceId() {
    return this.serviceId;
  }


  /**
   * @return the sessionId
   */
  public String getSessionId() {
    return this.sessionId;
  }


  /**
   * @return the data
   */
  public byte[] getData() {
    return this.data == null ? null : this.data.clone();
  }

  public int getDataSize() {
    return this.dataSize;
  }

  public boolean isDataAvailable() {
    return this.data != null;
  }

  public void clearData() {
    this.data = null;
    getLogger().debug("Session ID {}, event ID {}, SID {} : Data cleared.", this.sessionId, this.eventId,
        this.serviceId);
  }

  private ILoggerAdapter getLogger() {
    return CnsObjectStore.getLogger();
  }


  /**
   * @return the createdAt
   */
  public Instant getCreatedAt() {
    return this.createdAt;
  }


  /**
   * @return the producerId
   */
  public String getProducerId() {
    return this.producerId;
  }


  /**
   * @return State of session at the time of event creation
   */
  public SESSION_STATE getSessionState() {
    return this.sessionState;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "Event Det [Event ID = " + this.eventId + ", SID = " + this.serviceId + ", Session ID = " + this.sessionId +
        ", Data Available = " + isDataAvailable() + ", Data Size = " + this.dataSize + ", Session State = " +
        this.sessionState + ", Created At = " + this.createdAt + ", Producer ID = " + this.producerId + "]";
  }


}
