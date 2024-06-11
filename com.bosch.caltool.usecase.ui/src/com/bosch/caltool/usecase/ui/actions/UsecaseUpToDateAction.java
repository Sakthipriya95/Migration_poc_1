/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.usecase.ui.actions;

import org.eclipse.jface.action.Action;

import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.uc.UseCaseServiceClient;
import com.bosch.caltool.usecase.ui.Activator;


/**
 * @author dja7cob
 */
public class UsecaseUpToDateAction extends Action {

  /**
   * boolean
   */
  private final boolean isConfirmUpToDateBtn;
  /**
   * UsecaseClientBO
   */
  private final UsecaseClientBO usecaseBO;

  /**
   * @param isConfirmUpToDateBtn true if action is created for 'Confirm up to date' btn, else false
   * @param ucClientBO UsecaseClientBO
   */
  public UsecaseUpToDateAction(final boolean isConfirmUpToDateBtn, final UsecaseClientBO ucClientBO) {
    this.isConfirmUpToDateBtn = isConfirmUpToDateBtn;
    this.usecaseBO = ucClientBO;

    enableDisableButton();
    setButtonProperties();
  }

  /**
   */
  private void setButtonProperties() {
    if (this.isConfirmUpToDateBtn) {
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
   * method to enable/ disable 'Confirm up to date'/ 'Not up to date' buttons
   */
  public void enableDisableButton() {
    boolean isOwner = false;
    try {
      NodeAccess nodeAccess = new CurrentUserBO().getNodeAccessRight(this.usecaseBO.getUseCase().getId());
      isOwner = (nodeAccess != null) && nodeAccess.isOwner();
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
    }

    if (isOwner) {
      if (this.isConfirmUpToDateBtn) {
        // enable 'Confirm up to date' btn only when the use case is not up to date
        setEnabled(!this.usecaseBO.isUpToDate());
      }
      else {
        // enable 'Not up to date' btn only when the use case is up to date
        setEnabled(this.usecaseBO.isUpToDate());
      }
    }
    else {
      // if not owner
      setEnabled(false);
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {

    UseCaseServiceClient ucServiceClient = new UseCaseServiceClient();
    UseCase useCase = this.usecaseBO.getUseCase();


    try {
      ucServiceClient.changeUpToDateStatus(useCase, this.isConfirmUpToDateBtn);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }


}
