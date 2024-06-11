/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.dialogs.AssignCustVarsToICDMVarsDialog;
import com.bosch.caltool.icdm.client.bo.apic.ApicDataBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Task 233015 Action to invoke the dialog to assign cust var names to icdm var names
 *
 * @author dmo5cob
 */
public class AssignVarNamesAction extends Action {

  /**
   * Instance of PidcVersionBO
   */
  private final PidcVersionBO pidcVersionBO;
  /**
   * pidc details tree viewer instance
   */
  private final TreeViewer pidcDetailsTreeViewer;


  /**
   * @param pidcVersionBO BO of editor pidc version
   * @param pidcDetailsTreeViewer treeviewer in pidcdetailsviewpart
   */
  public AssignVarNamesAction(final PidcVersionBO pidcVersionBO, final TreeViewer pidcDetailsTreeViewer) {
    setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ASSIGN_VARNAMES_16X16));
    setText("Assignment to customer CDM system ");
    this.pidcVersionBO = pidcVersionBO;
    this.pidcDetailsTreeViewer = pidcDetailsTreeViewer;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {

    ApicDataBO apicBo = new ApicDataBO();
    CurrentUserBO currUser = new CurrentUserBO();

    // check whether the PIDc is unlocked in session
    try {
      // check whether the user has the write access to the PIDC
      if (!apicBo.isPidcUnlockedInSession(this.pidcVersionBO.getPidcVersion()) &&
          (null != currUser.getNodeAccessRight(this.pidcVersionBO.getPidcVersion().getPidcId())) &&
          currUser.hasNodeWriteAccess(this.pidcVersionBO.getPidcVersion().getPidcId())) {


        final PIDCActionSet pidcActionSet = new PIDCActionSet();
        // If locked, show dialog to unlock PIDC
        pidcActionSet.showUnlockPidcDialog(this.pidcVersionBO.getPidcVersion());
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    // If pidc is unlocked then show the dialog to assign var names
    if (apicBo.isPidcUnlockedInSession(this.pidcVersionBO.getPidcVersion())) {

      // Dialog to assign var names
      AssignCustVarsToICDMVarsDialog assignDialog = new AssignCustVarsToICDMVarsDialog(
          Display.getCurrent().getActiveShell(), this.pidcVersionBO, this.pidcDetailsTreeViewer);
      // open the dialog to assign cust var names to icdm var names
      assignDialog.open();
    }

  }


}
