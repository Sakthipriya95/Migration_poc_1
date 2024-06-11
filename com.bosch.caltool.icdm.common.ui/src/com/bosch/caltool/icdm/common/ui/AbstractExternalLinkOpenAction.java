/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui;

import com.bosch.calcomp.externallink.process.ILinkProcessAction;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author BNE4COB
 */
public abstract class AbstractExternalLinkOpenAction implements ILinkProcessAction {

  /**
   * Delimter when passing mutiple ids
   */
  protected static final String DELIMITER_MULTIPLE_ID = "-";

  /**
   * @param exp error
   */
  protected final void logInvalidUrlNumberError(final NumberFormatException exp) {
    try {
      String msgToDisplay = new CommonDataBO().getMessage("OPEN_ICDM_LINK", "INVALID_CHARACTERS");
      CDMLogger.getInstance().errorDialog(msgToDisplay, exp, Activator.PLUGIN_ID);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

}
