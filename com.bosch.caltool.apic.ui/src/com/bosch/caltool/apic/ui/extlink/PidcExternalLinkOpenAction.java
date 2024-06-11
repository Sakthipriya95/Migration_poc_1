/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.extlink;

import java.util.Map;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.actions.PIDCActionSet;
import com.bosch.caltool.icdm.common.bo.general.EXTERNAL_LINK_TYPE;
import com.bosch.caltool.icdm.common.ui.AbstractExternalLinkOpenAction;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Open a PIDC External Link. This will open the active version of the PIDC. If active version is not available yet, an
 * error message is displayed.
 *
 * @author bne4cob
 */
// ICDM-1649
public class PidcExternalLinkOpenAction extends AbstractExternalLinkOpenAction {

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean openLink(final Map<String, String> properties) {

    boolean ret = false;
    try {
      // get the pidc link
      Long pidcId = Long.valueOf(properties.get(EXTERNAL_LINK_TYPE.PIDC.getKey()));
      // get the pidc version
      PidcVersion pidcVersion = new PidcVersionServiceClient().getActivePidcVersion(pidcId);
      // Open the corresponding PIDC in editor
      new PIDCActionSet().openPIDCEditor(pidcVersion, false);
      ret = true;
    }
    catch (NumberFormatException exp) {
      // invalid Url Number error
      logInvalidUrlNumberError(exp);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }

    return ret;
  }


}
