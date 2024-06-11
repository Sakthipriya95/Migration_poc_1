/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.resource.ImageDescriptor;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.editors.pages.ReviewResultParamListPage;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.SR_ACCEPTED_FLAG;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CDRResultParameterServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author dmo5cob
 */
public class AcceptShapeCheckResultAction extends Action {


  /**
   * menu manager
   */
  private final IMenuManager manager;
  /**
   * Result element
   */
  private final Object firstElement;
  /**
   * Accept/Decline indication flag
   */
  private final boolean acceptFlag;
  ReviewResultClientBO resultData;
  ReviewResultParamListPage reviewResultNatPageNew;

  /**
   * @param manager manager
   * @param firstElement firstElement
   * @param acceptFlag accept Flag
   * @param resultData Review Result Client BO
   * @param reviewResultNatPageNew review Result list Page
   */
  public AcceptShapeCheckResultAction(final IMenuManager manager, final Object firstElement, final boolean acceptFlag,
      final ReviewResultClientBO resultData, final ReviewResultParamListPage reviewResultNatPageNew) {
    super();
    this.manager = manager;
    this.firstElement = firstElement;
    this.acceptFlag = acceptFlag;
    this.resultData = resultData;
    this.reviewResultNatPageNew = reviewResultNatPageNew;
    setProperties();
  }


  /**
   * set the properties
   */
  private void setProperties() {
    if (this.acceptFlag) {
      setText("Accept shape check failure ");
    }
    else {
      setText("Decline shape check failure ");
    }
    this.manager.add(this);
    if (this.acceptFlag) {
      final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.SHAPE_CHECK_ACCEPT_16X16);
      setImageDescriptor(imageDesc);
    }
    else {
      final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.SHAPE_CHECK_DECLINE_16X16);
      setImageDescriptor(imageDesc);
    }
    setEnabled(true);


  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {

    if (this.resultData.getResultBo().isResultLocked()) {
      CDMLogger.getInstance().errorDialog("Review is Locked and cannot be modified", Activator.PLUGIN_ID);
      return;
    }
    String srAcceptedFlag;
    String reviewScore = null;
    com.bosch.caltool.icdm.model.cdr.CDRResultParameter cdrResultParameter =
        (com.bosch.caltool.icdm.model.cdr.CDRResultParameter) this.firstElement;
    String oldValue = cdrResultParameter.getSrAcceptedFlag();
    if (SR_ACCEPTED_FLAG.YES == this.resultData.getShapeCheckResultEnum(cdrResultParameter)) {
      srAcceptedFlag = SR_ACCEPTED_FLAG.NO.getDbType();
      reviewScore = DATA_REVIEW_SCORE.S_0.getDbType();
    }
    else {
      srAcceptedFlag = SR_ACCEPTED_FLAG.YES.getDbType();
      if ((null != this.resultData.getReadyForSeries(cdrResultParameter)) &&
          (this.resultData.getReadyForSeries(cdrResultParameter) == ApicConstants.READY_FOR_SERIES.YES)) {
        reviewScore = DATA_REVIEW_SCORE.S_8.getDbType();
      }
    }
    CDRResultParameterServiceClient client = new CDRResultParameterServiceClient();
    if (this.resultData.getResultBo().isModifiable()) {
      try {
        List<CDRResultParameter> paramList = new ArrayList<>();
        CDRResultParameter paramClone = cdrResultParameter.clone();
        CommonUtils.shallowCopy(paramClone, cdrResultParameter);
        paramClone.setSrAcceptedFlag(srAcceptedFlag);
        paramClone.setReviewScore(reviewScore);

        paramList.add(paramClone);
        client.update(paramList);
        List<Object> valList = new ArrayList<>();
        valList.add(oldValue);
        AcceptShapeCheckResultAction.this.reviewResultNatPageNew.refreshColFilters(valList);
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }
    else {
      CDMLogger.getInstance().errorDialog("Insufficient privileges to do this operation!", Activator.PLUGIN_ID);
    }

  }


}
