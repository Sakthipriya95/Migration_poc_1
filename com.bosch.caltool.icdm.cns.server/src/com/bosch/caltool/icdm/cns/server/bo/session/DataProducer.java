/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.server.bo.session;

import java.time.Instant;
import java.util.UUID;

/**
 * @author bne4cob
 */
class DataProducer {

  private final String producerId = UUID.randomUUID().toString();
  private final String ipAddress;
  private final int port;

  private Instant lastContactAt;

  private long totalDataSize;

  /**
   * @param producerIp producer IP address
   * @param port port number
   */
  public DataProducer(final String producerIp, final int port) {
    this.ipAddress = producerIp;
    this.port = port;
  }

  /**
   * @return the producerId
   */
  public String getProducerId() {
    return this.producerId;
  }


  /**
   * @return the ipAddress
   */
  public String getIpAddress() {
    return this.ipAddress;
  }


  /**
   * @return the port
   */
  public int getPort() {
    return this.port;
  }


  /**
   * @return the lastContactAt
   */
  public Instant getLastContactAt() {
    return this.lastContactAt;
  }


  /**
   * Update last contact at
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
   * @param dataSize the totalDataSize to set
   */
  public void addDataSize(final long dataSize) {
    this.totalDataSize += dataSize;
  }

}
