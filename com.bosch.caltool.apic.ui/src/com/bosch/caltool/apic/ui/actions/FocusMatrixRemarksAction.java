/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.dialogs.FocusMatrixRemarksDialog;
import com.bosch.caltool.apic.ui.editors.pages.FocusMatrixPage;
import com.bosch.caltool.apic.ui.util.PIDCPageEditUtil;
import com.bosch.caltool.icdm.client.bo.apic.ApicDataBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixAttributeClientBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.ProjectAttributesUpdationModel;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Action for focus matrix remarks
 *
 * @author mkl2cob
 */
public class FocusMatrixRemarksAction extends Action {


  /**
   * FocusMatrixPage instance
   */
  private final FocusMatrixPage focusMatrixPage;
  /**
   * Return code for Ok pressed
   */
  private static final int CODE_FOR_OK = 0;

  /**
   * FocusMatrixAttributeClientBO
   */
  private final FocusMatrixAttributeClientBO fmAttr;


  /**
   * Constructor
   *
   * @param focusMatrixPage FocusMatrixPage
   * @param fmAttr FocusMatrixAttributeClientBO
   */
  public FocusMatrixRemarksAction(final FocusMatrixPage focusMatrixPage, final FocusMatrixAttributeClientBO fmAttr) {
    this.focusMatrixPage = focusMatrixPage;
    this.fmAttr = fmAttr;
    // set text
    setText(IMessageConstants.SET_REVIEW_COMMENTS);
    // get comments image for the action
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.COMMENTS_TAG_16X16);
    setImageDescriptor(imageDesc);
    // enable or disable based on isModifiable() method
    setEnabled(focusMatrixPage.getDataHandler().getPidcVersionBO().isModifiable());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    PidcVersionBO pidcVersionBO = this.focusMatrixPage.getDataHandler().getPidcVersionBO();
    boolean canModify = pidcVersionBO.isModifiable();
    // check if the result can be modified
    if (canModify) {
      ApicDataBO apicBo = new ApicDataBO();
      CurrentUserBO currUser = new CurrentUserBO();
      // ICDM-2487 P1.27.101
      try {
        if (!apicBo.isPidcUnlockedInSession(pidcVersionBO.getPidcVersion()) && (currUser.hasApicWriteAccess() &&
            currUser.hasNodeWriteAccess(pidcVersionBO.getPidcVersion().getPidcId()))) {
          // if the user has apic write or node write access
          final PIDCActionSet pidcActionSet = new PIDCActionSet();
          pidcActionSet.showUnlockPidcDialog(pidcVersionBO.getPidcVersion());
        }
      }
      catch (ApicWebServiceException exp) {
        // show error dialog in case of exception
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
      if (apicBo.isPidcUnlockedInSession(pidcVersionBO.getPidcVersion())) {
        // if the pidc is unlocked
        // show remarks dialog
        FocusMatrixRemarksDialog remarksDialog =
            new FocusMatrixRemarksDialog(Display.getCurrent().getActiveShell(), this.fmAttr);
        int isOkPressed = remarksDialog.open();
        if (isOkPressed == CODE_FOR_OK) {
          compareOldValAndUpdateRemarks(remarksDialog);
        }
      }
    }
  }


  /**
   * @param remarksDialog
   */
  private void compareOldValAndUpdateRemarks(final FocusMatrixRemarksDialog remarksDialog) {
    final String newValue = remarksDialog.getReviewComments();
    // get the old value
    String oldValue = this.fmAttr.getPidcAttr().getPidcVersAttr().getFmAttrRemark();
    if (CommonUtils.isNotEqual(oldValue, newValue)) {
      // if old and new value are not same, update remarks
      updateRemarks(this.fmAttr, newValue);
    }
  }

  /**
   * @param currentFmAttr FocusMatrixAttributeClientBO
   * @param newValue String
   */
  private void updateRemarks(final FocusMatrixAttributeClientBO currentFmAttr, final String newValue) {

    // get the proj attr
    PidcVersionAttribute pidcVersionAttribute = this.focusMatrixPage.getDataHandler().getPidcDataHandler()
        .getPidcVersAttrMap().get(currentFmAttr.getAttribute().getId());
    PidcVersionAttribute projAttrClone = pidcVersionAttribute.clone();
    projAttrClone.setFmAttrRemark(CommonUtils.checkNull(newValue));
    // set the project attribute updation model
    ProjectAttributesUpdationModel updationModel = new ProjectAttributesUpdationModel();
    updationModel.setPidcVersion(this.focusMatrixPage.getDataHandler().getPidcVersion());
    PIDCPageEditUtil editUtil = new PIDCPageEditUtil(this.focusMatrixPage.getDataHandler().getPidcVersionBO());
    // call the PIDCPageEditUtil to update via webservice
    editUtil.updateProjectAttribute(projAttrClone, updationModel);

  }
}

