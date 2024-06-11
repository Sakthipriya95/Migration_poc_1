/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.Form;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.dialogs.QnaireRespListDialog;
import com.bosch.caltool.cdr.ui.dialogs.SimplifiedGeneralQnaireDialog;
import com.bosch.caltool.cdr.ui.editors.pages.ReviewResultParamListPage;
import com.bosch.caltool.cdr.ui.util.CdrUIConstants;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CDRReviewResultServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author rgo7cob
 */
public class ReviewResultLockUnLockAction extends Action {


  /**
   * String for Unlock button
   */
  private static final String UNLOCK_REVIEW_RESULT = "Unlock Review Result";
  /**
   * String for lock button
   */
  private static final String LOCK_REVIEW_RESULT = "Lock Review Result";

  /**
   * Review Locked desc
   */
  private static final String REV_LOCKED_DESC = "Review is Locked";
  /**
   * page to use the Section Description Control
   */
  private final ReviewResultParamListPage reviewResultNatPage;

  private final Form nonScrollableForm;
  ReviewResultClientBO resultData;
  private boolean isDivIdConfigured;

  /**
   * @param reviewResultNatPage reviewResultNatPage
   * @param nonScrollableForm parent
   * @param reviewResultData reviewResultClientBO
   */
  public ReviewResultLockUnLockAction(final ReviewResultParamListPage reviewResultNatPage, final Form nonScrollableForm,
      final ReviewResultClientBO reviewResultData) {
    super();

    this.reviewResultNatPage = reviewResultNatPage;
    this.nonScrollableForm = nonScrollableForm;
    this.resultData = reviewResultData;
    if (CommonUtils.isNotNull(nonScrollableForm)) {
      setProperties();
    }
  }


  /**
   * set the image and the tool tip and Enable Disable
   */
  public final void update() {
    // It is called from outside class.
    if (isWidgetNotDisposed()) {
      setProperties();
    }
  }

  /**
   * Set the action properties, based on the CDR Result status
   */
  private void setProperties() {
    ImageDescriptor imageDesc;
    // if result is locked then display the Unlock Image
    if (this.resultData.getResultBo().isResultLocked()) {
      imageDesc = ImageManager.getImageDescriptor(ImageKeys.UNLOCK_16X16);
      setText(UNLOCK_REVIEW_RESULT);
      this.nonScrollableForm.setText(REV_LOCKED_DESC);
      this.nonScrollableForm.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
    }
    // if result is not locked then display the lock Image
    else {
      imageDesc = ImageManager.getImageDescriptor(ImageKeys.LOCK_16X16);
      setText(LOCK_REVIEW_RESULT);
      this.nonScrollableForm.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_TITLE_FOREGROUND));
      this.nonScrollableForm.setText("");
    }
    // Always set enabled true.
    setEnabled(true);
    setImageDescriptor(imageDesc);

  }


  /**
   * @return
   */
  private boolean isWidgetNotDisposed() {
    return (this.nonScrollableForm != null) && !this.nonScrollableForm.isDisposed();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    String lockStatus = null;
    if (this.resultData.getResultBo().isResultLocked()) {
      // Result is locked now. Trying to unlock
      if (this.resultData.getResultBo().canUnLockResult()) {
        lockStatus = ApicConstants.CODE_NO;
      }
      else {
        CDMLogger.getInstance().errorDialog(
            "You are not allowed to unlock the review. Only a user with owner access rights to the PIDC can unlock a review. Please ask one of the PIDC owners to unlock your review.",
            Activator.PLUGIN_ID);
        return;
      }
    }
    else {
      // Result is unlocked now. Trying to lock
      if (this.resultData.getResultBo().canLockResult()) {
        if (CommonUtils.isEqual(openSimpGnrlQnaire(), 0)) {
          boolean isQuesRespNotFilled = isQuesRespNotFilled();
          if (isDivIdConfigured() && isQuesRespNotFilled) {
            return;
          }
          lockStatus = ApicConstants.CODE_YES;
        }
      }
      else {
        CDMLogger.getInstance().errorDialog("User does not have access to lock review", Activator.PLUGIN_ID);
        return;
      }
    }
    updateReviewResult(lockStatus);
    // Update UI objects' properties
    setProperties();
  }

  /**
   * @param cdrReviewResult
   * @return
   */
  private int openSimpGnrlQnaire() {

    CDRReviewResult cdrResult = this.resultData.getResultBo().getCDRResult();
    // Simplified Qnaire only available for Official Review
    if (this.resultData.isSimpQuesEnab() &&
        CommonUtils.isEqual(cdrResult.getReviewType(), CDRConstants.REVIEW_TYPE.OFFICIAL.getDbType()) &&
        isAtleastOneParamChecked()) {
      SimplifiedGeneralQnaireDialog simpGnrlQnaireDialog =
          new SimplifiedGeneralQnaireDialog(Display.getCurrent().getActiveShell(), cdrResult);

      return simpGnrlQnaireDialog.open();
    }

    return 0;
  }


  /**
   * @param lockStatus - status of the review result (locked or not locked)
   */
  public void updateReviewResult(final String lockStatus) {
    CDRReviewResult updatedResult = null;
    try {
      CDRReviewResultServiceClient resultClient = new CDRReviewResultServiceClient();
      CDRReviewResult cdrResult = this.resultData.getResultBo().getCDRResult();

      CDRReviewResult cdrReviewResult = resultClient.getById(cdrResult.getId());
      cdrReviewResult.setLockStatus(lockStatus);
      cdrReviewResult.setSimpQuesRemarks(cdrResult.getSimpQuesRemarks());
      cdrReviewResult.setSimpQuesRespValue(cdrResult.getSimpQuesRespValue());

      updatedResult = resultClient.update(cdrReviewResult);
      CommonUtils.shallowCopy(cdrResult, updatedResult);


    }
    catch (ApicWebServiceException error) {
      CDMLogger.getInstance().error(error.getMessage(), error, Activator.PLUGIN_ID);
    }
  }


  /**
   * show ques resp to be filled msg dialog
   *
   * @return is questionnaires responses not completely filled
   */
  public boolean isQuesRespNotFilled() {
    boolean quesRespToBeFilledForLock = this.reviewResultNatPage.isQuesNotFilled() && isAtleastOneParamChecked();
    if (quesRespToBeFilledForLock) {
      openQnaireRespListDialog(isDivIdApplicable() ? this.resultData.getResultBo().checkOfficialOrStartReview()
          : CdrUIConstants.WARN_MSG_ABOUT_UNFILLED_QNAIRE_RESP);
    }
    return quesRespToBeFilledForLock;
  }


  /**
   * @param warningMessage -warning message about un-filles qnaire response
   */
  public void openQnaireRespListDialog(final String warningMessage) {
    CDMLogger.getInstance().warnDialog(warningMessage, Activator.PLUGIN_ID);
    QnaireRespListDialog qnaireRespListDialog = new QnaireRespListDialog(Display.getCurrent().getActiveShell(),
        this.resultData.getResponse().getQnaireDataForRvwSet(), this.resultData.getResultBo(), true, true);
    qnaireRespListDialog.open();
  }


  /**
   * @return true if the division attribute value of the pidc is configrued in common parameters table
   */
  public boolean isDivIdApplicable() {
    setDivIdConfigured(ReviewResultClientBO.isDivIdMappedToGivComParamKey(
        this.resultData.getResultBo().getPidcVersion().getId(), CommonParamKey.ICDM_QNAIRE_CONFIG_ATTR));
    return isDivIdConfigured();
  }


  /**
   * @return the isDivIdConfigured
   */
  public boolean isDivIdConfigured() {
    return this.isDivIdConfigured;
  }


  /**
   * @param isDivIdConfigured the isDivIdConfigured to set
   */
  public void setDivIdConfigured(final boolean isDivIdConfigured) {
    this.isDivIdConfigured = isDivIdConfigured;
  }


  /**
   * @return
   */
  private boolean isAtleastOneParamChecked() {
    return this.reviewResultNatPage.getResultData().getResultBo().isAtleastOneParamChecked();
  }


}
