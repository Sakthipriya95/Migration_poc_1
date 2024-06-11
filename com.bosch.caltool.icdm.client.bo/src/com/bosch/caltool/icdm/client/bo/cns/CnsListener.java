/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cns;

/**
 * @author bne4cob
 */
public enum CnsListener {
                         /**
                          * Singleton
                          */
                         INSTANCE;


  /**
   * Start the connection polling
   */
  public void start() {
    Thread thr = new Thread(CnsListenerDaemon.INSTANCE, CnsListenerDaemon.CNS_LISTENER_NAME);
    thr.setDaemon(true);
    thr.start();
  }

  /**
   * Trigger daemon stop
   */
  public void stop() {
    CnsListenerDaemon.INSTANCE.stop();
  }


}
