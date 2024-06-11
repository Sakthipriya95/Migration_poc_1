/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.connectionstate;

/**
 * @author bne4cob
 */
public enum ConnectionPoller {
                              /**
                               * Singleton
                               */
                              INSTANCE;


  /**
   * Start the connection polling
   */
  public void start() {
    Thread thr = new Thread(ConnectionPollerDaemon.INSTANCE, ConnectionPollerDaemon.POLLER_THREAD_NAME);
    thr.setDaemon(true);
    thr.start();
  }

  /**
   * Trigger daemon stop
   */
  public void stop() {
    ConnectionPollerDaemon.INSTANCE.stop();
  }

  /**
   * Set CNS server connection status to connected
   */
  public void setCnsServerConnected() {
    ConnectionPollerDaemon.INSTANCE.setCnsServerConnected(true);
  }

  /**
   * Set CNS server connection status to disconnected
   */
  public void setCnsServerDisconnected() {
    ConnectionPollerDaemon.INSTANCE.setCnsServerConnected(false);
  }


}
