/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.extlink;

import java.util.Map;
import com.bosch.caltool.icdm.common.bo.general.EXTERNAL_LINK_TYPE;
import com.bosch.caltool.icdm.common.ui.AbstractExternalLinkOpenAction;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2lFileExt;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcA2lServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Action to open an A2L file link
 *
 * @author bne4cob
 */
// ICDM-1649
public class A2LFileExternalLinkOpenAction extends AbstractExternalLinkOpenAction {

  /**
   * Define size for the cmd args
   */
  private static final int CMD_ARGS_SIZE = 2;

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean openLink(final Map<String, String> properties) {

    String a2lID = properties.get(EXTERNAL_LINK_TYPE.A2L_FILE.getKey());
    String[] strIds = a2lID.split(DELIMITER_MULTIPLE_ID);
    if ((strIds == null) || (strIds.length != CMD_ARGS_SIZE)) {
      CDMLogger.getInstance().errorDialog("Invalid hyperlink for A2L file!", Activator.PLUGIN_ID);
      return false;
    }

    try {
      Long pidcA2LFileId = Long.valueOf(strIds[0]);
      // Get the a2l file for the pidc id
      PidcA2lFileExt a2lFileExt = new PidcA2lServiceClient().getPidcA2LFileDetails(pidcA2LFileId);
      new CommonActionSet().openA2lFile(a2lFileExt.getPidcA2l().getId());
      return true;
    }
    catch (NumberFormatException exp) {
      logInvalidUrlNumberError(exp);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return false;
  }
}
