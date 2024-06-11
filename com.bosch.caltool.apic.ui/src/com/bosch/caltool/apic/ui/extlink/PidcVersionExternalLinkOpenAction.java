/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.extlink;

import java.util.Map;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.actions.PIDCActionSet;
import com.bosch.caltool.apic.ui.editors.PIDCEditor;
import com.bosch.caltool.icdm.common.bo.general.EXTERNAL_LINK_TYPE;
import com.bosch.caltool.icdm.common.ui.AbstractExternalLinkOpenAction;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Open a PIDC Version link
 *
 * @author bne4cob
 */
// ICDM-1649
public class PidcVersionExternalLinkOpenAction extends AbstractExternalLinkOpenAction {

  /**
   * This id can be used in external link when it is required to open iCDM without opening any particular pidc editor.
   */
  private static final int PIDC_VERSNID_DUMMY = 0;

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean openLink(final Map<String, String> properties) {


    try {
      // get the PIDC version ID from the external link
      Long pidcVersID = Long.valueOf(properties.get(EXTERNAL_LINK_TYPE.PIDC_VERSION.getKey()));

      if (pidcVersID == PIDC_VERSNID_DUMMY) {
        return false;
      }
      // Find the PIDC verison. Retrieve from DB, if not loaded already.
      PidcVersion pidVersion = new PidcVersionServiceClient().getById(pidcVersID);

      // ICDM-343
      // Open the PIDC editor
      PIDCEditor pidcEditor = new PIDCActionSet().openPIDCEditor(pidVersion, false);
      if (null != pidcEditor) {
        // ICDM-1254
        // Activate the PIDC Editor opened, if not active
        CommonUiUtils.forceActive(pidcEditor.getEditorSite().getShell());
      }
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
