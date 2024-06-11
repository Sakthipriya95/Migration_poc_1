/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibilityStatus;

/**
 * @author say8cob
 */
public class MonicaReviewOutputData {

  private CDRReviewResult cdrReviewResult;

  private RvwVariant rvwVariant;

  private String warningMsg;

  private String errorMsg;

  private boolean reviewFailed;

  private MonicaInputData monicaInputData;

  // Map to hold A2lWpResponsibility Status object before Wp finished status reset. key -
  private Map<Long, A2lWpResponsibilityStatus> a2lWpRespStatusBeforeUpd = new HashMap<>();

  // Map to hold A2lWpResponsibility Status object after Wp finished status reset
  private Map<Long, A2lWpResponsibilityStatus> a2lWpRespStatusAfterUpd = new HashMap<>();

  /**
   * List of newly created A2lWpResponsibilityStatus entries in T_A2L_WP_RESPONSIBILITY_STATUS table
   */
  private List<A2lWpResponsibilityStatus> listOfNewlyCreatedA2lWpRespStatus = new ArrayList<>();

  /**
   * @return the cdrReviewResult
   */
  public CDRReviewResult getCdrReviewResult() {
    return this.cdrReviewResult;
  }


  /**
   * @param cdrReviewResult the cdrReviewResult to set
   */
  public void setCdrReviewResult(final CDRReviewResult cdrReviewResult) {
    this.cdrReviewResult = cdrReviewResult;
  }


  /**
   * @return the rvwVariant
   */
  public RvwVariant getRvwVariant() {
    return this.rvwVariant;
  }


  /**
   * @param rvwVariant the rvwVariant to set
   */
  public void setRvwVariant(final RvwVariant rvwVariant) {
    this.rvwVariant = rvwVariant;
  }


  /**
   * @return the warningMsg
   */
  public String getWarningMsg() {
    return this.warningMsg;
  }


  /**
   * @param warningMsg the warningMsg to set
   */
  public void setWarningMsg(final String warningMsg) {
    this.warningMsg = warningMsg;
  }


  /**
   * @return the errorMsg
   */
  public String getErrorMsg() {
    return this.errorMsg;
  }


  /**
   * @param errorMsg the errorMsg to set
   */
  public void setErrorMsg(final String errorMsg) {
    this.errorMsg = errorMsg;
  }


  /**
   * @return the reviewFailed
   */
  public boolean isReviewFailed() {
    return this.reviewFailed;
  }


  /**
   * @param reviewFailed the reviewFailed to set
   */
  public void setReviewFailed(final boolean reviewFailed) {
    this.reviewFailed = reviewFailed;
  }


  /**
   * @return the monicaInputData
   */
  public MonicaInputData getMonicaInputData() {
    return this.monicaInputData;
  }


  /**
   * @param monicaInputData the monicaInputData to set
   */
  public void setMonicaInputData(final MonicaInputData monicaInputData) {
    this.monicaInputData = monicaInputData;
  }


  /**
   * @return the a2lWpRespStatusBeforeUpd
   */
  public Map<Long, A2lWpResponsibilityStatus> getA2lWpRespStatusBeforeUpd() {
    return this.a2lWpRespStatusBeforeUpd;
  }


  /**
   * @param a2lWpRespStatusBeforeUpd the a2lWpRespStatusBeforeUpd to set
   */
  public void setA2lWpRespStatusBeforeUpd(final Map<Long, A2lWpResponsibilityStatus> a2lWpRespStatusBeforeUpd) {
    this.a2lWpRespStatusBeforeUpd = a2lWpRespStatusBeforeUpd;
  }


  /**
   * @return the a2lWpRespStatusAfterUpd
   */
  public Map<Long, A2lWpResponsibilityStatus> getA2lWpRespStatusAfterUpd() {
    return this.a2lWpRespStatusAfterUpd;
  }


  /**
   * @param a2lWpRespStatusAfterUpd the a2lWpRespStatusAfterUpd to set
   */
  public void setA2lWpRespStatusAfterUpd(final Map<Long, A2lWpResponsibilityStatus> a2lWpRespStatusAfterUpd) {
    this.a2lWpRespStatusAfterUpd = a2lWpRespStatusAfterUpd;
  }


  /**
   * @return the listOfNewlyCreatedA2lWpRespStatus
   */
  public List<A2lWpResponsibilityStatus> getListOfNewlyCreatedA2lWpRespStatus() {
    return this.listOfNewlyCreatedA2lWpRespStatus;
  }


  /**
   * @param listOfNewlyCreatedA2lWpRespStatus the listOfNewlyCreatedA2lWpRespStatus to set
   */
  public void setListOfNewlyCreatedA2lWpRespStatus(
      final List<A2lWpResponsibilityStatus> listOfNewlyCreatedA2lWpRespStatus) {
    this.listOfNewlyCreatedA2lWpRespStatus = listOfNewlyCreatedA2lWpRespStatus;
  }


}
