/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.common.model;

/**
 * @author bne4cob
 */
public class ProducerInfo {

  private String producerId;

  private String ipAddress;
  private int port;

  private String lastContactAt;

  private long totalDataSize;

  private int eventCount;


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
   * @return the ipAddress
   */
  public String getIpAddress() {
    return this.ipAddress;
  }


  /**
   * @param ipAddress the ipAddress to set
   */
  public void setIpAddress(final String ipAddress) {
    this.ipAddress = ipAddress;
  }


  /**
   * @return the port
   */
  public int getPort() {
    return this.port;
  }


  /**
   * @param port the port to set
   */
  public void setPort(final int port) {
    this.port = port;
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
    return super.toString() + " [producerId=" + this.producerId + ", ipAddress=" + this.ipAddress + ", port=" +
        this.port + ", lastContactAt=" + this.lastContactAt + ", totalDataSize=" + this.totalDataSize +
        ", eventCount=" + this.eventCount + "]";
  }


}
