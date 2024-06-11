/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.qnaire;

import com.bosch.caltool.icdm.model.general.ExternalLinkInfo;

/**
 * The Model RvwQnaireRespVersModel contains Review Questionnaire Response Version additional details
 *
 * @author hnu1cob
 */
public class RvwQnaireRespVersModel {

  /**
   * Questionnaire Response Version Link
   */
  private Long qnaireRespVersId;

  /**
   * Link of the qnaire baseline
   */
  private ExternalLinkInfo rvwQnaireLink;

  /**
   * Count of positive answers
   */
  private int qnairePositiveAnsCount;
  /**
   * Count of negative answers
   */
  private int qnaireNegativeAnsCount;
  /**
   * Count of neutral answers
   */
  private int qnaireNeutralAnsCount;

  /**
   * true, Questionnaire Response Version is a baseline
   */
  private boolean qnaireBaselineExisting;

  /**
   * true, if Questionnaire is Ready for Production Questionnaire is Ready for Production if 1. Questionnaire is
   * answered, baselined and answer is allowed to Finish WP 2. If 'Questionnaire does not allow Negative answer to
   * Finish WP', then any of the answer should not be 'Negative'
   */
  private boolean qnaireReadyForProd;

  /**
   * provides the questionnaire version status
   * 
   */
  private String qnaireVersStatus;

  /**
   * @return the qnaireRespVersId
   */
  public Long getQnaireRespVersId() {
    return this.qnaireRespVersId;
  }

  /**
   * @param qnaireRespVersId the qnaireRespVersId to set
   */
  public void setQnaireRespVersId(final Long qnaireRespVersId) {
    this.qnaireRespVersId = qnaireRespVersId;
  }

  /**
   * @return the rvwQnaireLink
   */
  public ExternalLinkInfo getRvwQnaireLink() {
    return this.rvwQnaireLink;
  }

  /**
   * @param rvwQnaireLink the rvwQnaireLink to set
   */
  public void setRvwQnaireLink(final ExternalLinkInfo rvwQnaireLink) {
    this.rvwQnaireLink = rvwQnaireLink;
  }

  /**
   * @return the qnairePositiveAnsCount
   */
  public int getQnairePositiveAnsCount() {
    return this.qnairePositiveAnsCount;
  }

  /**
   * @param qnairePositiveAnsCount the qnairePositiveAnsCount to set
   */
  public void setQnairePositiveAnsCount(final int qnairePositiveAnsCount) {
    this.qnairePositiveAnsCount = qnairePositiveAnsCount;
  }

  /**
   * @return the qnaireNegativeAnsCount
   */
  public int getQnaireNegativeAnsCount() {
    return this.qnaireNegativeAnsCount;
  }

  /**
   * @param qnaireNegativeAnsCount the qnaireNegativeAnsCount to set
   */
  public void setQnaireNegativeAnsCount(final int qnaireNegativeAnsCount) {
    this.qnaireNegativeAnsCount = qnaireNegativeAnsCount;
  }

  /**
   * @return the qnaireNeutralAnsCount
   */
  public int getQnaireNeutralAnsCount() {
    return this.qnaireNeutralAnsCount;
  }

  /**
   * @param qnaireNeutralAnsCount the qnaireNeutralAnsCount to set
   */
  public void setQnaireNeutralAnsCount(final int qnaireNeutralAnsCount) {
    this.qnaireNeutralAnsCount = qnaireNeutralAnsCount;
  }

  /**
   * @return the qnaireBaselineExisting
   */
  public boolean isQnaireBaselineExisting() {
    return this.qnaireBaselineExisting;
  }

  /**
   * @param qnaireBaselineExisting the qnaireBaselineExisting to set
   */
  public void setQnaireBaselineExisting(final boolean qnaireBaselineExisting) {
    this.qnaireBaselineExisting = qnaireBaselineExisting;
  }

  /**
   * @return the qnaireReadyForProd
   */
  public boolean isQnaireReadyForProd() {
    return this.qnaireReadyForProd;
  }

  /**
   * @param qnaireReadyForProd the qnaireReadyForProd to set
   */
  public void setQnaireReadyForProd(final boolean qnaireReadyForProd) {
    this.qnaireReadyForProd = qnaireReadyForProd;
  }


  /**
   * @return the qnaireVersStatus
   */
  public String getQnaireVersStatus() {
    return this.qnaireVersStatus;
  }


  /**
   * @param qnaireVersStatus the qnaireVersStatus to set
   */
  public void setQnaireVersStatus(final String qnaireVersStatus) {
    this.qnaireVersStatus = qnaireVersStatus;
  }
}
