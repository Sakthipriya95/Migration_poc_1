/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.wizards.ImportPIDCWizardDialog;
import com.bosch.caltool.apic.ui.wizards.ImportPidcWizard;
import com.bosch.caltool.icdm.client.bo.apic.ApicDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author jvi6cob
 */
public class ImportPIDCAction extends Action {

  /**
   * current shell
   */
  private final Shell shell;
  /**
   * Project version
   */
  private final PidcVersion pidcVer;


  /**
   * @param shell current shell
   * @param pidcVersion project version
   */
  public ImportPIDCAction(final Shell shell, final PidcVersion pidcVersion) {
    super();
    this.shell = shell;
    this.pidcVer = pidcVersion;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {

    // Check if the pidc is unlocked in session
    CurrentUserBO currUser = new CurrentUserBO();
    ApicDataBO apicBo = new ApicDataBO();
    try {
      // check for access rights and if pidc is locked
      if (!apicBo.isPidcUnlockedInSession(this.pidcVer) && currUser.hasNodeWriteAccess(this.pidcVer.getPidcId())) {
        final PIDCActionSet pidcActionSet = new PIDCActionSet();
        // show unlock msg if the pidc is locked
        pidcActionSet.showUnlockPidcDialog(this.pidcVer);
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    // check pidc is unlocked
    if (apicBo.isPidcUnlockedInSession(this.pidcVer)) {
      // open the import changes dialog
      ImportPidcWizard wizard = new ImportPidcWizard(this.pidcVer);
      ImportPIDCWizardDialog importPIDCWizardDialog = new ImportPIDCWizardDialog(this.shell, wizard);
      importPIDCWizardDialog.setPageSize(1180, 400);
      importPIDCWizardDialog.create();
      importPIDCWizardDialog.open();
    }
  }
}
