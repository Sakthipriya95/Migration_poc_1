/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.common.model;

/**
 * @author bne4cob
 */
public class EventInfo2 extends EventInfo {

  private String user;

  /**
   * Empty constructor.
   */
  // NOTE : do NOT remove this constructor !!!
  public EventInfo2() {
    // Nothing to do
  }

  /**
   * Create an EventInfo2 object from an EventInfo object
   *
   * @param evt EventInfo
   */
  public EventInfo2(final EventInfo evt) {
    setCreatedAt(evt.getCreatedAt());
    setDataAvailable(evt.getDataAvailable());
    setDataLength(evt.getDataLength());
    setEventId(evt.getEventId());
    setServiceId(evt.getServiceId());
    setProducerId(evt.getProducerId());
    setSessionId(evt.getSessionId());
    setSessionState(evt.getSessionState());
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
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "EventInfo2 [eventId=" + getEventId() + ", user=" + getUser() + ", sessionId=" + getSessionId() +
        ", producerId=" + getProducerId() + ", dataLength=" + getDataLength() + ", createdAt=" + getCreatedAt() +
        ", dataAvailable=" + getDataAvailable() + "]";
  }

}
