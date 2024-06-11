/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.client.bo.apic.ApicDataBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionStatus;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dja7cob PIDC Auto lock Action
 */
public class PIDCVersionLockAction extends Action {

  /**
   * String for Unlock button
   */
  private static final String UNLOCK_PIDC_EDIT = "PIDC edit unlocked";
  /**
   * String for lock button
   */
  private static final String LOCK_PIDC_EDIT = "PIDC edit locked";
  /**
   * selected pidc id
   */
  private Long pidcId;
  /**
   * selected pidc version
   */
  private PidcVersion pidcVersion;
  /**
   * selected pidc name
   */
  private String pidcName;

  /**
   * @param selectedPidcVersion PIDC version
   */
  public PIDCVersionLockAction(final PidcVersion selectedPidcVersion) {
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
    // check whether pidc is locked, display the Unlock Image
    ApicDataBO apicBo = new ApicDataBO();
    if (PidcVersionStatus.LOCKED == PidcVersionStatus.getStatus(this.pidcVersion.getPidStatus())) {
      imageDesc = ImageManager.getImageDescriptor(ImageKeys.UNLOCK_16X16);
      setText(UNLOCK_PIDC_EDIT);
    }
    // check whether pidc is locked, display the lock Image
    else {
      imageDesc = ImageManager.getImageDescriptor(ImageKeys.LOCK_16X16);
      setText(LOCK_PIDC_EDIT);
    }

    CurrentUserBO currUser = new CurrentUserBO();

    // check whether the user has no access rights for the selected PIDC, disable the lock
    try {

      setEnabled((currUser.hasApicWriteAccess() || (currUser.hasNodeWriteAccess(this.pidcId))) &&
          apicBo.isPidcUnlockedInSession(this.pidcVersion));
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

    // Set the icon based on PIDC lock status
    setImageDescriptor(imageDesc);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {

    // ICDM-2487 P1.27.101
    // Check if the PIDC is locked
    // If PIDC is locked, open dialog to unlock the PIDC
    // check If PIDC is unlocked in session, lock it
    ApicDataBO apicBo = new ApicDataBO();
    CurrentUserBO currUser = new CurrentUserBO();
    try {
      // Check if the user has access to PIDC
      // The user should have APIC_WRITE access or write access to that PIDC
      if (!apicBo.isPidcUnlockedInSession(this.pidcVersion) &&
          (currUser.hasApicWriteAccess() || currUser.hasNodeWriteAccess(this.pidcVersion.getPidcId()))) {
        final PIDCActionSet pidcActionSet = new PIDCActionSet();
        pidcActionSet.showUnlockPidcDialog(this.pidcVersion);
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    // Perform action only if the PIDC is unlocked in session

    if (apicBo.isPidcUnlockedInSession(this.pidcVersion)) {
      updatePidcVersionStatus();
    }
    else {
      // check If PIDC is locked in session, unlock it
      CDMLogger.getInstance().info(ApicUiConstants.EDIT_NOT_ALLOWED, Activator.PLUGIN_ID);
    }
  }

  /**
   * Update pidc version status
   */
  private void updatePidcVersionStatus() {
    PidcVersionServiceClient client = new PidcVersionServiceClient();
    try {
   // set the lock status
      PidcVersion clonedPidcVersion = this.pidcVersion.clone();
      if (PidcVersionStatus.LOCKED == PidcVersionStatus.getStatus(this.pidcVersion.getPidStatus())) {
        clonedPidcVersion.setPidStatus(PidcVersionStatus.IN_WORK.getDbStatus());
      }
      else {
        clonedPidcVersion.setPidStatus(PidcVersionStatus.LOCKED.getDbStatus());
      }
      // update the Pidc Lock status
      client.editPidcVersion(clonedPidcVersion);
    }
    catch (ApicWebServiceException | CloneNotSupportedException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
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
