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
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcSubVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.wbutils.WorkbenchUtils;

/**
 * @author nip4cob
 */
public class PidcSubVariantExternalLinkOpenAction extends AbstractExternalLinkOpenAction {

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean openLink(final Map<String, String> properties) {
    // method to open PIDC variant from an external link
    boolean ret = false;
    try {

      String keyId = properties.get(EXTERNAL_LINK_TYPE.SUB_VARIANT.getKey());
      // Link delimiter
      String[] keyIds = keyId.split(DELIMITER_MULTIPLE_ID);
      // separate PIDC version ID and PIDC variant ID from delimited keyIds
      Long pidcVersId = Long.valueOf(keyIds[0].trim());
      // get the pidc variant id
      Long pidcSubVarId = Long.valueOf(keyIds[1].trim());
      // Open PIDC editor
      openPIDCVersionEditor(pidcVersId, pidcSubVarId);
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
   * @param pidcVersId
   * @param pidcSubVarId
   * @throws ApicWebServiceException
   */
  private void openPIDCVersionEditor(final Long pidcVersId, final Long pidcSubVarId) throws ApicWebServiceException {
    // get the pidc version and variant
    PidcVersion pidcVersion = new PidcVersionServiceClient().getById(pidcVersId);
    PidcSubVariant pidcSubVariant = null == pidcSubVarId ? null : new PidcSubVariantServiceClient().get(pidcSubVarId);

    PIDCActionSet actionSet = new PIDCActionSet();

    // Open the corresponding PIDc in editor
    PIDCEditor pidcEditor = actionSet.openPIDCEditor(pidcVersion, false);
    if (null != pidcEditor) {
      // Reset tree nodes
      pidcEditor.getPidcPage().resetNodeSelections();

      CommonUiUtils.forceActive(pidcEditor.getEditorSite().getShell());
      if (null != pidcSubVariant) {
        setDetailsPageSelection(pidcSubVariant, pidcEditor);
      }
    }


  }

  /**
   * @param pidcSubVariant
   * @param pidcEditor
   */
  private void setDetailsPageSelection(final PidcSubVariant pidcSubVariant, final PIDCEditor pidcEditor) {
    // Check whether PIDC variant is valid
    pidcEditor.getPidcPage().setVaraintNodeSelected(false);
    pidcEditor.getPidcPage().setSelectedPidcSubVariant(pidcSubVariant);
    IWorkbenchPart part = WorkbenchUtils.getView(PIDCDetailsViewPart.VIEW_ID);
    pidcEditor.getPidcPage().setSubVaraintNodeSelected(true);
    PIDCDetailsViewPart pidcDetView = (PIDCDetailsViewPart) part;
    pidcDetView.setVirStructDispEnbld(false);
    pidcDetView.refreshPages();
    PIDCDetailsPage page = (PIDCDetailsPage) pidcDetView.getCurrentPage();
    // Get the variant tree item
    TreeItem[] treeItems = page.getViewer().getTree().getItems();
    TreeItem[] pidcVarItems = treeItems[0].getItems();
    if (null != pidcVarItems) {
      setSubVarSelectionInDetailsPage(pidcSubVariant, pidcEditor, page, pidcVarItems);
    }
  }

  /**
   * @param pidcSubVariant
   * @param pidcEditor
   * @param page
   * @param pidcVarItems
   */
  private void setSubVarSelectionInDetailsPage(final PidcSubVariant pidcSubVariant, final PIDCEditor pidcEditor,
      final PIDCDetailsPage page, final TreeItem[] pidcVarItems) {

    for (TreeItem varTreeItem : pidcVarItems) {
      PidcVariant variant = (PidcVariant) varTreeItem.getData();
      if (variant.getId().equals(pidcSubVariant.getPidcVariantId())) {
        // expand the variant to show subvariants
        page.getViewer().setExpandedState(variant, true);
        TreeItem[] items = varTreeItem.getItems();
        for (TreeItem subVarItem : items) {
          PidcSubVariant currSubVar = (PidcSubVariant) subVarItem.getData();
          if ((null != currSubVar) && pidcSubVariant.getId().equals(currSubVar.getId())) {
            page.getViewer().getTree().select(subVarItem);
            page.getViewer().getTree().setSelection(subVarItem);
            // set selection to the corresponding subvariant in PIDC details page
            pidcEditor.getPidcPage().pidcDetNodeSelection(pidcSubVariant);
            break;
          }
        }
      }
    }
  }

}
