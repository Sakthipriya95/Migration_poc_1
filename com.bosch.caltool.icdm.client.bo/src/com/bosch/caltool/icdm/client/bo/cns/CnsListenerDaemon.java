/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cns;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.datamodel.core.cns.ChangeEvent;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.connectionstate.ConnectionPoller;
import com.bosch.caltool.icdm.cns.client.CnsClientConfiguration;
import com.bosch.caltool.icdm.cns.client.CnsServiceClientException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.ws.rest.client.cns.ChangeHandler;

/**
 * @author bne4cob
 */
enum CnsListenerDaemon implements Runnable {
                                            /**
                                             * Singleton
                                             */
                                            INSTANCE;

  /**
   * Name of the listener
   */
  static final String CNS_LISTENER_NAME = "CNS Listener Daemon";

  private static final long CNS_CHECK_INTERVAL = 30 * 1000L;
  private Long latestEventId;

  private boolean runFlag = true;

  private final String sessionId = CnsClientConfiguration.getDefaultConfig().getSessionId();

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    if (checkInitialized()) {
      getLogger().info("{} started", CNS_LISTENER_NAME);

      while (this.runFlag) {
        refreshCnsEvent();
        sleep();
      }

      getLogger().info("{} stopped", CNS_LISTENER_NAME);
    }
  }

  private void refreshCnsEvent() {
    ConcurrentMap<Long, ChangeEvent> eventsAfter = new ConcurrentHashMap<>();

    try {
      eventsAfter.putAll(new CnsDataConsumerServiceClientWrapper().getEventsAfter(this.latestEventId));
    }
    catch (CnsServiceClientException e) {
      String message = CommonUtils.checkNull(e.getMessage());
      if (message.contains("CNS-2001") || message.contains("CNS-2002")) {
        // CNS-2001 - Invalid session ID, primarly due to CNS server restart
        // CNS-2002 - Closed session
        message += ". Remote changes will not be received anymore. Please restart iCDM client.";
        getLogger().errorDialog(message, e, Activator.PLUGIN_ID);
        stop();
      }
      else {
        getLogger().error(message, e);
      }
    }

    if (!eventsAfter.isEmpty()) {
      new ChangeHandler().triggerRemoteChangeEvent(eventsAfter);
      updateLatestEventId(eventsAfter.keySet());
    }

  }

  private CDMLogger getLogger() {
    return CDMLogger.getInstance();
  }

  /**
   * @param eventIdSet
   */
  private void updateLatestEventId(final Set<Long> eventIdSet) {
    for (Long eventId : eventIdSet) {
      if (eventId > this.latestEventId) {
        this.latestEventId = eventId;
      }
    }

  }

  private void sleep() {
    try {
      Thread.sleep(CNS_CHECK_INTERVAL);
    }
    catch (InterruptedException e) {
      getLogger().error(e.getMessage(), e);
      Thread.currentThread().interrupt();
    }
  }

  /**
   *
   */
  private boolean checkInitialized() {
    boolean ret = true;
    if (this.latestEventId == null) {
      try {
        this.latestEventId = new CnsDataConsumerServiceClientWrapper().getLatestEventId();
      }
      catch (CnsServiceClientException e) {
        ret = false;
        getLogger().errorDialog(CNS_LISTENER_NAME + " failed to start properly. " + e.getMessage(), e,
            Activator.PLUGIN_ID);
      }
    }
    return ret;

  }

  /**
   * @return the stop
   */
  public boolean isRunning() {
    return this.runFlag;
  }

  /**
   * Stop the thread
   */
  public void stop() {
    this.runFlag = false;
    ConnectionPoller.INSTANCE.setCnsServerDisconnected();
  }

  /**
   * @return the sessionId
   */
  public String getSessionId() {
    return this.sessionId;
  }


}
