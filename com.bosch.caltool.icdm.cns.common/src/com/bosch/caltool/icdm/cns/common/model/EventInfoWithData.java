/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.common.model;


/**
 * @author BNE4COB
 */
public class EventInfoWithData extends EventInfo {

  private byte[] data;

  /**
   * Constructor
   */
  public EventInfoWithData() {
    // Mandatory constructor, for jackson parsing
  }

  /**
   * Create a new instance from an existing event info object
   *
   * @param input event info
   */
  public EventInfoWithData(final EventInfo input) {
    setCreatedAt(input.getCreatedAt());
    setDataAvailable(input.getDataAvailable());
    setDataLength(input.getDataLength());
    setEventId(input.getEventId());
    setServiceId(input.getServiceId());
    setProducerId(input.getProducerId());
    setSessionId(input.getSessionId());
    setSessionState(input.getSessionState());
  }

  /**
   * @return the data
   */
  public byte[] getData() {
    return this.data == null ? null : this.data.clone();
  }

  /**
   * @param data the data to set
   */
  public void setData(final byte[] data) {
    this.data = data == null ? null : data.clone();
  }

}
