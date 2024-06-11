/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.common.model;


/**
 * @author BNE4COB
 */
public class EventDetInfo {

  private EventInfo eventInfo;
  private SessionInfo sessionInfo;
  private ProducerInfo producerInfo;

  /**
   * @return the eventInfo
   */
  public EventInfo getEventInfo() {
    return this.eventInfo;
  }

  /**
   * @param eventInfo the eventInfo to set
   */
  public void setEventInfo(final EventInfo eventInfo) {
    this.eventInfo = eventInfo;
  }

  /**
   * @return the sessionInfo
   */
  public SessionInfo getSessionInfo() {
    return this.sessionInfo;
  }

  /**
   * @param sessionInfo the sessionInfo to set
   */
  public void setSessionInfo(final SessionInfo sessionInfo) {
    this.sessionInfo = sessionInfo;
  }

  /**
   * @return the producerInfo
   */
  public ProducerInfo getProducerInfo() {
    return this.producerInfo;
  }

  /**
   * @param producerInfo the producerInfo to set
   */
  public void setProducerInfo(final ProducerInfo producerInfo) {
    this.producerInfo = producerInfo;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return super.toString() + " [eventInfo=" + this.eventInfo + "]";
  }


}
