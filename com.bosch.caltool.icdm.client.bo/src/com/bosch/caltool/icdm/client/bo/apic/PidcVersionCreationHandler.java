/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dja7cob
 */
public class PidcVersionCreationHandler {


  /**
   * @param newPidcVer
   * @param wizardData
   * @param lvlAttrTreeNode
   * @return
   */
  public PidcVersion createPidcVersion(final PidcVersion newPidcVer) {
    PidcVersionServiceClient pidcVerSerClient = new PidcVersionServiceClient();
    PidcVersion pidcVerCreated = null;
    try {
      pidcVerCreated = pidcVerSerClient.createNewPidcVersion(newPidcVer);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return pidcVerCreated;
  }
}
