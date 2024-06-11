/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.extlink;

import java.util.Map;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.actions.PIDCActionSet;
import com.bosch.caltool.apic.ui.editors.PIDCEditor;
import com.bosch.caltool.apic.ui.views.PIDCOutlinePage;
import com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItemNode;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.common.bo.general.EXTERNAL_LINK_TYPE;
import com.bosch.caltool.icdm.common.ui.AbstractExternalLinkOpenAction;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author apj4cob
 */
public class UsecaseInPidcVersnExternalLinkOpenAction extends AbstractExternalLinkOpenAction {

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
      // get the PIDC version and uc ID from the external link text
      String linkTypeId;
      Long pidcVersID;
      Long ucItemId;
      boolean projUseCase = false;
//      if external link has project use case type
      if (null == properties.get(EXTERNAL_LINK_TYPE.USE_CASE.getKey())) {
        linkTypeId = properties.get(EXTERNAL_LINK_TYPE.PROJECT_USE_CASE.getKey());
        projUseCase = true;
        String[] splittedStr = linkTypeId.split("-");
        pidcVersID = Long.valueOf(splittedStr[1]);
        ucItemId = Long.valueOf(splittedStr[2]);
      }
      else {
//      if external link has use case
        linkTypeId = properties.get(EXTERNAL_LINK_TYPE.USE_CASE.getKey());
        pidcVersID = Long.valueOf(linkTypeId.substring(0, linkTypeId.indexOf('-')));
        ucItemId = Long.valueOf(linkTypeId.substring(linkTypeId.indexOf('-') + 1));
      }

      if (pidcVersID == PIDC_VERSNID_DUMMY) {
        return false;
      }
      // Find the PIDC verison. Retrieve from DB, if not loaded already.
      PidcVersion pidVersion = new PidcVersionServiceClient().getById(pidcVersID);
      // ICDM-343
      // Open the PIDC editor
      PIDCEditor pidcEditor = new PIDCActionSet().openPIDCEditor(pidVersion, ucItemId, false, projUseCase);
      if (null != pidcEditor) {
        // ICDM-1254
        // Activate the PIDC Editor opened, if not active
        CommonUiUtils.forceActive(pidcEditor.getEditorSite().getShell());
        // the filters in the PIDC editor must not hide any attributes to show all attributes that are in this use case
        // (except hidden ones due to dependency).
        PIDCOutlinePage pidcOutlinePage = pidcEditor.getOutlinePageCreator().getPidcOutlinePage(ucItemId);

        if ((null != pidcOutlinePage) && (pidcOutlinePage.getItemToBeSelected() != null)) {
          // check whether the FavUseCaseItemNode or IUseCaseItemClientBO object is to be selected in outline view
          if (pidcOutlinePage.getItemToBeSelected() instanceof FavUseCaseItemNode) {
            pidcEditor.getPidcPage().getOutLineFilter()
                .setFavUseCaseItem((FavUseCaseItemNode) pidcOutlinePage.getItemToBeSelected());
          }
          else if (pidcOutlinePage.getItemToBeSelected() instanceof IUseCaseItemClientBO) {
            pidcEditor.getPidcPage().getOutLineFilter()
                .setUseCaseItem((IUseCaseItemClientBO) pidcOutlinePage.getItemToBeSelected());

          }
          // filter attribute related to usecase item selected in pidc outline view
          pidcEditor.getPidcPage().applyOutlineFilter();
          pidcEditor.setFocus();
        }
      }
      return true;
    }
    catch (NumberFormatException exp) {
//      log if invalid url number entered
      logInvalidUrlNumberError(exp);
    }
    catch (ApicWebServiceException ex) {
//      invoking level 4 error dialog to show exception message to the user when unable to open the external link
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
    }
    return false;
  }
}
