/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.dialogs.ARCReleaseDisplayDialog;
import com.bosch.caltool.cdr.ui.editors.pages.ReviewResultParamListPage;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CDRReviewResultServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author say8cob
 */
public class ReviewResultMarkWorkPackageAsFinishedAction extends Action {

  private final ReviewResultParamListPage reviewResultNatPage;
  private final ReviewResultClientBO resultData;

  /**
   * @param reviewResultNatPage reviewResultNatPage
   * @param reviewResultData reviewResultClientBO
   */
  public ReviewResultMarkWorkPackageAsFinishedAction(final ReviewResultParamListPage reviewResultNatPage,
      final ReviewResultClientBO reviewResultData) {
    super();
    this.reviewResultNatPage = reviewResultNatPage;
    this.resultData = reviewResultData;
    setProperties();
  }

  private void setProperties() {
    setText("Mark Workpackage Responsibility as Finished");

    boolean isMarkWPAsFinishedAllowed = this.resultData.getResultBo().canLockResult() &&
        CDRConstants.REVIEW_TYPE.OFFICIAL.getDbType().equals(this.reviewResultNatPage.getCdrResult().getReviewType());
    setEnabled(isMarkWPAsFinishedAllowed);
    setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ALL_16X16));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    ReviewResultLockUnLockAction lockAction =
        new ReviewResultLockUnLockAction(this.reviewResultNatPage, null, this.resultData);
    if (!this.resultData.getResultBo().isResultLocked()) {
      CDMLogger.getInstance().errorDialog(getMessageForKey("REVIEW_NOT_LOCKED_ERROR_MSG"), Activator.PLUGIN_ID);
      return;
    }
    // for monica review type
    if (isQnaireFilledCheckNotNeeded()) {
      checkToCompleteWpRespAndLockReview(false, this.reviewResultNatPage.getCdrResult());
    }
    else {
      boolean isQuesRespNotFilled = lockAction.isQuesRespNotFilled();
      if (lockAction.isDivIdConfigured() && isQuesRespNotFilled) {
        return;
      }
      checkToCompleteWpRespAndLockReview(isQuesRespNotFilled, this.reviewResultNatPage.getCdrResult());
    }

  }


  /**
   * @return
   */
  private boolean isQnaireFilledCheckNotNeeded() {
    return this.resultData.getResultBo().canLockResult() && this.reviewResultNatPage.isMonicaReview() &&
        isAtleastOneParamChecked();
  }

  /**
   * @return
   */
  private boolean isAtleastOneParamChecked() {
    return this.reviewResultNatPage.getResultData().getResultBo().isAtleastOneParamChecked();
  }

  private List<CDRResultParameter> getARCReleaseParams() {
    return this.resultData.getResultBo().getARCReleaseParams();
  }

  private void confirmArcReleaseParams() {
    if (!getARCReleaseParams().isEmpty()) {
      ARCReleaseDisplayDialog arcReleasedDialog = new ARCReleaseDisplayDialog(Display.getCurrent().getActiveShell(),
          getARCReleaseParams(), this.reviewResultNatPage);
      arcReleasedDialog.open();
    }
  }

  /**
   * Method to check and get the response from user for validating WpResp status
   *
   * @param isQuesRespNotFilled as input
   * @param cdrReviewResult as input
   */
  public void checkToCompleteWpRespAndLockReview(final boolean isQuesRespNotFilled,
      final CDRReviewResult cdrReviewResult) {
    boolean isAllParamsReviewed = this.resultData.getResultBo().checkIfAllParamsAreReviewed();

    // if the Review is not Test review type then the WP-RESP check should happen
    if (CDRConstants.REVIEW_TYPE.OFFICIAL.getDbType().equals(cdrReviewResult.getReviewType()) && !isQuesRespNotFilled &&
        isAllParamsReviewed) {
      boolean openConfirm = MessageDialog.openQuestion(Display.getDefault().getActiveShell(), "Confirmation Dialog",
          getMessageForKey("FINISHED_CONFIRMATION_MSG"));
      if (openConfirm) {
        confirmArcReleaseParams();
        if (this.resultData.getResultBo().isAllParamCheckedOrARCReleased()) {
          new CDRReviewResultServiceClient().updateWorkpackageStatus(cdrReviewResult);
        }
      }
    }
    else if (!isAllParamsReviewed) {
      CDMLogger.getInstance().errorDialog(getMessageForKey("NOT_REVIEWED_PARAMS_ERROR_MSG"), Activator.PLUGIN_ID);
    }
  }

  private String getMessageForKey(final String key) {
    String message = "";
    try {
      message = new CommonDataBO().getMessage("REVIEW_RESULT", key);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return message;
  }
}
