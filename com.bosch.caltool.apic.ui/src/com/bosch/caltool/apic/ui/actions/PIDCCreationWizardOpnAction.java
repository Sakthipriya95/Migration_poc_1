/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.apic.ui.wizards.PIDCCreationWizard;
import com.bosch.caltool.apic.ui.wizards.PIDCCreationWizardDialog;
import com.bosch.caltool.icdm.client.bo.apic.PidcCreationHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * ICDM-2488
 *
 * @author mkl2cob
 */
public class PIDCCreationWizardOpnAction extends Action {

  /**
   * pidc node
   */
  private final PidcTreeNode pidcNode;
  /**
   * Treeviewer
   */
  private final TreeViewer viewer;
  /**
   * Current User BO model
   */
  private final CurrentUserBO currUserBo;


  /**
   * Constructor
   *
   * @param pidcNode Pidc Tree Node
   * @param viewer Tree Viewer
   */
  public PIDCCreationWizardOpnAction(final PidcTreeNode pidcNode, final TreeViewer viewer) {
    this.pidcNode = pidcNode;
    this.viewer = viewer;
    this.currUserBo = new CurrentUserBO();
    setText(ApicUiConstants.ADD_PRO_ID_CARD);

    ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.ADD_16X16);
    setImageDescriptor(imageDesc);
    // ICDM-94

    // The context menu "Add Project ID Card" should be enabled for all access types - APIC_READ, APIC_READ_ALL,
    // APIC_WRITE, PID_WRITE
    setEnabled(true);
  }


  @Override
  public void run() {

    try {
      if (this.currUserBo.hasPidcWriteAccess() || this.currUserBo.hasApicWriteAccess()) {
        PIDCCreationWizard createPidcWizard = new PIDCCreationWizard(this.pidcNode, this.viewer, false, null);
        PIDCCreationWizardDialog createPidcWizardDlg = new PIDCCreationWizardDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), createPidcWizard);
        createPidcWizardDlg.create();
        int returnValue = createPidcWizardDlg.open();

        if (returnValue == 0) {
          final PIDCActionSet actionSet = new PIDCActionSet();
          PidcVersion newlyCreatedVer =
              new PidcCreationHandler().getActiveVersion(createPidcWizard.getNewlyCreatedPidc());
          actionSet.openPIDCEditor(newlyCreatedVer);
        }
      }
      else {
        CDMLogger.getInstance().infoDialog("You don't have sufficient access to create a PIDC.\r\n" +
            "To raise an access request, please refer the steps in the wiki link to which you will be redirected now, click OK.",
            Activator.PLUGIN_ID);
        // Open the web browser with the specified URL
        String url =
            "https://inside-docupedia.bosch.com/confluence/display/ATW/2.1+iCDM+FAQ#id-2.1iCDMFAQ-AccessRightstoCreatePIDC";
        Program.launch(url);
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog("Error while trying to create PIDC" + e.getMessage(), Activator.PLUGIN_ID);
      ;
    }
  }
}
