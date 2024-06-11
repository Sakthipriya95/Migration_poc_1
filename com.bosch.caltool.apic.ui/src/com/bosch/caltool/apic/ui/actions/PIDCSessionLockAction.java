/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.editors.PIDCEditor;
import com.bosch.caltool.icdm.client.bo.apic.ApicDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dja7cob PIDC Auto lock Action
 */
public class PIDCSessionLockAction extends Action {

  /**
   * String for Unlock button
   */
  private static final String UNLOCK_PIDC_EDIT = "PIDC edit unlocked";
  /**
   * String for lock button
   */
  private static final String LOCK_PIDC_EDIT = "PIDC edit locked";
  /**
   * pidc id
   */
  private Long pidcId;
  /**
   * pidc version
   */
  private PidcVersion pidcVersion;
  /**
   * pidc name
   */
  private String pidcName;

  /**
   * @param selectedPidcVersion PIDC version
   */
  public PIDCSessionLockAction(final PidcVersion selectedPidcVersion) {
    this.pidcVersion = selectedPidcVersion;
    setPidcId(this.pidcVersion.getPidcId());
    String[] pidcNameArr = this.pidcVersion.getName().split("\\(");
    this.pidcName = pidcNameArr[0].trim();
    setActionProperties();
  }

  /**
   *
   */
  public void setActionProperties() {
    // Icon for PIDC lock action
    ImageDescriptor imageDesc;
    enableDisableLockBtn();
    // check whether pidc is unlocked, display the Unlock Image
    if (new ApicDataBO().isPidcUnlockedInSession(this.pidcVersion)) {
      imageDesc = ImageManager.getImageDescriptor(ImageKeys.UNLOCK_16X16);
      setText(UNLOCK_PIDC_EDIT);
    }
    // check whether pidc is locked, display the lock Image
    else {
      imageDesc = ImageManager.getImageDescriptor(ImageKeys.LOCK_16X16);
      setText(LOCK_PIDC_EDIT);
    }
    // Set the icon based on PIDC lock status
    setImageDescriptor(imageDesc);
  }


  /**
   * Enable/disable pidc lock button based on access rights
   */
  private void enableDisableLockBtn() {
    // check whether the user has no access rights for the selected PIDC, disable the lock
    try {
      setEnabled(new CurrentUserBO().hasNodeWriteAccess(this.pidcId));
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    // check If PIDC is unlocked in session, lock it
    ApicDataBO apicBo = new ApicDataBO();
    if (apicBo.isPidcUnlockedInSession(this.pidcVersion)) {
      apicBo.setPidcLockedInSession(this.pidcVersion);
      CDMLogger.getInstance().info("PIDC: " + this.pidcName + " locked for editing!", Activator.PLUGIN_ID);

    }
    else {
      // check If PIDC is locked in session, unlock it
      apicBo.setPidcUnLockedInSession(this.pidcVersion);
      CDMLogger.getInstance().info("PIDC: " + this.pidcName + " unlocked for editing!", Activator.PLUGIN_ID);
    }

    PIDCEditor pidcEditor = null;
    IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

    if (activePage.isEditorAreaVisible()) {
      IEditorPart openedEditor = activePage.getActiveEditor();
      if ((openedEditor instanceof PIDCEditor)) {
        pidcEditor = (PIDCEditor) openedEditor;
        // To update the status of Lock/Unlock icon in UI
        pidcEditor.getPidcPage().getPidcLockAction().setActionProperties();
        if (pidcEditor.getPidcCoCWpPage().getPidcLockAction() != null) {
          pidcEditor.getPidcCoCWpPage().getPidcLockAction().setActionProperties();
        }
      }
    }
  }


  /**
   * @return the pidcId
   */
  public Long getPidcId() {
    return this.pidcId;
  }

  /**
   * @param pidcId the pidcId to set
   */
  public void setPidcId(final Long pidcId) {
    this.pidcId = pidcId;
  }

  /**
   * @return the pidcVersion
   */
  public PidcVersion getPidcVersion() {
    return this.pidcVersion;
  }

  /**
   * @param pidcVersion the pidcVersion to set
   */
  public void setPidcVersion(final PidcVersion pidcVersion) {
    this.pidcVersion = pidcVersion;
  }

  /**
   * @return the pidcName
   */
  public String getPidcName() {
    return this.pidcName;
  }

  /**
   * @param pidcName the pidcName to set
   */
  public void setPidcName(final String pidcName) {
    this.pidcName = pidcName;
  }
}
