/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.bo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.datamodel.core.cns.ChangeEvent;
import com.bosch.caltool.datamodel.core.cns.ICnsAsyncMessage;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.common.util.CommonUtils;

/**
 * Utility to send CNS messages to CNS server. Uses a Fixed Thread Pool Executor internally. During shutdown, invoke
 * {@link #shutdown()} to finish sending pending messags and shutdown the executor.
 *
 * @author bne4cob
 */
public final class CnsMessageSender {

  private static final int EXEC_POOL_SIZE = 20;

  private static final ExecutorService CNS_EXECUTOR = Executors.newFixedThreadPool(EXEC_POOL_SIZE);

  /**
   *
   */
  private CnsMessageSender() {
    // Private constructor
  }


  /**
   * @param serviceData service data
   * @param event change event
   */
  static final void send(final ServiceData serviceData, final ChangeEvent event) {

    ICnsAsyncMessage cnsMessage;

    try {
      Class<?> cnsMessageType = ObjectStore.getInstance().getCnsMessageType();
      if (cnsMessageType == null) {
        getLogger().warn("CNS Message provider client not configured");
        return;
      }

      String sessionId =
          CommonUtils.isEmptyString(serviceData.getCnsSessionId()) ? "DEFAULT" : serviceData.getCnsSessionId();

      event.setClientSessionId(sessionId);

      cnsMessage = (ICnsAsyncMessage) cnsMessageType.newInstance();
      cnsMessage.setChangeEvent(event);
      cnsMessage.setSessionId(sessionId);

      CNS_EXECUTOR.execute(cnsMessage);

    }
    catch (Exception e) {
      getLogger().error("Error while sending change event to CNS Server : " + e.getMessage(), e);
    }
  }

  private static ILoggerAdapter getLogger() {
    return ObjectStore.getInstance().getLogger();
  }

  /**
   * Shutdown the message sender
   */
  public static final void shutdown() {
    getLogger().info("CNS Message Sender shutting down ...");
    CNS_EXECUTOR.shutdown();
    getLogger().info("CNS Message Sender shutdown complete");
  }

}
