/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.extlink;

import java.util.Map;

import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPart;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.actions.PIDCActionSet;
import com.bosch.caltool.apic.ui.editors.PIDCEditor;
import com.bosch.caltool.apic.ui.views.PIDCDetailsPage;
import com.bosch.caltool.icdm.common.bo.general.EXTERNAL_LINK_TYPE;
import com.bosch.caltool.icdm.common.ui.AbstractExternalLinkOpenAction;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.views.PIDCDetailsViewPart;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.wbutils.WorkbenchUtils;


/**
 * @author BNE4COB
 */
public class PidcVariantExternalLinkOpenAction extends AbstractExternalLinkOpenAction {

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean openLink(final Map<String, String> properties) {
    // method to open PIDC variant from an external link
    boolean ret = false;
    try {

      String keyId = properties.get(EXTERNAL_LINK_TYPE.PIDC_VARIANT.getKey());
      // Link delimiter
      String[] keyIds = keyId.split(DELIMITER_MULTIPLE_ID);
   // separate PIDC version ID and PIDC variant ID from delimited keyIds
      Long pidcVersId = Long.valueOf(keyIds[0].trim());
      // get the pidc variant id
      Long pidcVarId = Long.valueOf(keyIds[1].trim());
    //Open PIDC editor
      openPIDCVersionEditor(pidcVersId, pidcVarId);
      ret = true;
    }
    catch (NumberFormatException exp) {
      logInvalidUrlNumberError(exp);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }

    return ret;
  }


  /**
   * Open the PIDC Version in the PIDC editor. Also make the editor active
   *
   * @param pidVersion pidc version
   * @throws ApicWebServiceException error from web service
   */
  private void openPIDCVersionEditor(final Long pidcId, final Long pidcVarId) throws ApicWebServiceException {
    // get the pidc version and variant
    PidcVersion pidcVersion = new PidcVersionServiceClient().getById(pidcId);
    PidcVariant pidcVariant = null == pidcVarId ? null : new PidcVariantServiceClient().get(pidcVarId);

    PIDCActionSet actionSet = new PIDCActionSet();

    // Open the corresponding PIDc in editor
    PIDCEditor pidcEditor = actionSet.openPIDCEditor(pidcVersion, false);
    if (null != pidcEditor) {
      // Reset tree nodes
      pidcEditor.getPidcPage().resetNodeSelections();

      CommonUiUtils.forceActive(pidcEditor.getEditorSite().getShell());
      if (null != pidcVariant) {
        pidcVarValid(pidcVariant, pidcEditor);
      }
    }


  }

  /**
   * @param pidcVar
   * @param pidcEditor
   */
  private void pidcVarValid(final PidcVariant pidcVar, final PIDCEditor pidcEditor) {
    // Check whether PIDC variant is valid
    pidcEditor.getPidcPage().setVaraintNodeSelected(true);
    pidcEditor.getPidcPage().setSelectedPidcVariant(pidcVar);
    IWorkbenchPart part = WorkbenchUtils.getView(PIDCDetailsViewPart.VIEW_ID);
    PIDCDetailsViewPart pidcDetView = (PIDCDetailsViewPart) part;
    pidcDetView.setVirStructDispEnbld(false);
    pidcDetView.refreshPages();
    PIDCDetailsPage page = (PIDCDetailsPage) pidcDetView.getCurrentPage();

    // Get the variant tree item
    TreeItem[] treeItems = page.getViewer().getTree().getItems();

    TreeItem[] pidcVarItems = treeItems[0].getItems();
    if (null != pidcVarItems) {
      pidcVarItemsValid(pidcVar, pidcEditor, page, pidcVarItems);
    }
  }

  /**
   * @param pidcVar
   * @param pidcEditor
   * @param page
   * @param pidcVarItems
   */
  private void pidcVarItemsValid(final PidcVariant pidcVar, final PIDCEditor pidcEditor, final PIDCDetailsPage page,
      final TreeItem[] pidcVarItems) {
    for (TreeItem varTreeItem : pidcVarItems) {
      PidcVariant currVar = (PidcVariant) varTreeItem.getData();
      if (currVar.getId().equals(pidcVar.getId())) {
        page.getViewer().getTree().select(varTreeItem);
        page.getViewer().getTree().setSelection(varTreeItem);
        // set selection to the corresponding PIDC variant in PIDC details page
        pidcEditor.getPidcPage().pidcDetNodeSelection(currVar);
        break;
      }
    }
  }

}
