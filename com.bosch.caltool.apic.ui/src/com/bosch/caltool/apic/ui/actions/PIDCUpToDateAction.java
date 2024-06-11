/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import java.util.Calendar;

import org.eclipse.jface.action.Action;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.editors.pages.PIDCAttrPage;
import com.bosch.caltool.icdm.client.bo.apic.ApicDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * This action is to make pidc version as up to date
 *
 * @author dmo5cob
 */
public class PIDCUpToDateAction extends Action {

  /**
   * message if pidc version is up to date
   */
  private static final String PIDC_VERSION_UP_TO_DATE_MSG = "PIDC Version is up to date";
  /**
   * message if pidc version is not up to date
   */
  private static final String PIDC_VERSION_NOT_UP_TO_DATE_MSG = "PIDC Version is NOT up to date";

  /**
   * true if up to date is confirmed
   */
  private final boolean confirmUpToDate;
  /**
   * PIDCAttrPage
   */
  private final PIDCAttrPage pidcPage;

  /**
   * @param confirmUpToDate confirm up to date/ not up to date
   * @param page PIDCAttrPage instance
   */
  public PIDCUpToDateAction(final boolean confirmUpToDate, final PIDCAttrPage page) {
    this.confirmUpToDate = confirmUpToDate;
    this.pidcPage = page;
    enableDisableUpToDateNotUpToDateBtns();
    if (confirmUpToDate) {
      // Set the icon
      setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ALL_16X16));
      // Set description for the action
      setText("Confirm Up To Date");
    }
    else {
      // Set the icon
      setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NONE_16X16));
      // Set description for the action
      setText("Not Up To Date");
    }
  }

  /**
   * enable/disable up to date and not up to date buttons
   */
  public void enableDisableUpToDateNotUpToDateBtns() {
    CurrentUserBO currentUserBO = new CurrentUserBO();
    NodeAccess currentUserRight;
    try {
      // get the current user access
      currentUserRight =
          currentUserBO.getNodeAccessRight(this.pidcPage.getPidcVersionBO().getPidcVersion().getPidcId());
      if ((currentUserRight != null) && currentUserRight.isOwner() && this.pidcPage.getPidcVersionBO().isModifiable()) {
        // enable the button based on whether up to date (confirm or not confirm)

        if (this.confirmUpToDate) {
          /**
           * Pidc is up to date Case 1: Pidc is unlocked----enable not up to date button Case 2:Pidc is locked and user
           * has owner access----enable confirm up to date button for not up to date pidc
           */

          setEnabled(!this.pidcPage.isUpToDate());
        }
        else {
          /**
           * Pidc is not up to date Case 1: Pidc is unlocked----enable confirm up to date button Case 2:Pidc is locked
           * and user has owner access----enable not up to date button for updated pidc
           */
          // case2- pidc unlocked and it is not up to date ------- enable confirm up to date button
          setEnabled(this.pidcPage.isUpToDate());
        }

      }
      else {
        setEnabled(false);
      }
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
    // get the pidc version
    PidcVersion pidcVrsn = this.pidcPage.getPidcVersionBO().getPidcVersion();
    if (canUnlockPidcDialog()) {
      if (this.confirmUpToDate) {
        // confirm up to date with current date and time
        pidcVrsn.setLastConfirmationDate(ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_15, Calendar.getInstance()));
      }
      else {
        // set the last confirmation date to null to make it not up to date
        pidcVrsn.setLastConfirmationDate(null);
      }
      // call the following method to call the service
      this.pidcPage.getPidcVersionBO().upToDate(pidcVrsn);

      // change UI controls
      if (this.confirmUpToDate) {
        // in case of up to date
        this.pidcPage.getNonScrollableForm().setMessage("[ " + PIDC_VERSION_UP_TO_DATE_MSG + " ]");
        this.pidcPage.getNonScrollableForm()
            .setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.ALL_16X16));
        setEnabled(false);
        this.pidcPage.getPidcNotUpToDateAction().setEnabled(true);
      }
      else {
        // in case of not up to date
        this.pidcPage.getNonScrollableForm().setMessage("[ " + PIDC_VERSION_NOT_UP_TO_DATE_MSG + " ]", 3);
        setEnabled(false);
        this.pidcPage.getPidcUpToDateAction().setEnabled(true);
      }
    }
  }

  /**
   *
   */
  private boolean canUnlockPidcDialog() {
    // If PIDc version is locked in session,
    // show dialog to unlock PIDC
    ApicDataBO apicBo = new ApicDataBO();
    PidcVersion selectedPidcVersion = this.pidcPage.getSelectedPidcVersion();
    if (!apicBo.isPidcUnlockedInSession(selectedPidcVersion)) {
      final PIDCActionSet pidcActionSet = new PIDCActionSet();
      return pidcActionSet.showUnlockPidcDialog(selectedPidcVersion);
    }
    // if pidc is unlocked in session
    return true;
  }

}
