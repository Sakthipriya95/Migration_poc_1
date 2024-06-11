/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.general;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.cns.CnsListener;
import com.bosch.caltool.icdm.cns.client.CnsDataConsumerServiceClient;
import com.bosch.caltool.icdm.cns.client.CnsServiceClientException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.ws.rest.client.ClientConfiguration;

/**
 * @author bne4cob
 */
public class IcdmSessionHandler {

  /**
   * Close sessions
   */
  public void closeSessions() {
    CDMLogger.getInstance().info("Closing server sessions...");

    String cnsSessionId = ClientConfiguration.getDefault().getCnsSessionId();
    if (!CommonUtils.isEmptyString(cnsSessionId)) {
      CnsListener.INSTANCE.stop();
      try {
        new CnsDataConsumerServiceClient().closeSession(cnsSessionId);
        ClientConfiguration.getDefault().setCnsSessionId(null);
      }
      catch (CnsServiceClientException e) {
        CDMLogger.getInstance().warn(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }

    CDMLogger.getInstance().info("Server session closing completed");

  }
}
