/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.dataassessment;

import com.bosch.caltool.icdm.model.general.ExternalLinkInfo;

/**
 * @author TRL1COB
 */
public class DataAssessmentQuestionnaires {

  /**
   * Unique Id of Data assessment Qnaires
   */
  private Long id;
  /**
   * Id of the Work Package
   */
  private Long a2lWpId;
  /**
   * Id of the Responsibility
   */
  private Long a2lRespId;
  /**
   * Name of the Work Package
   */
  private String a2lWpName;
  /**
   * Name of the Responsibility
   */
  private String a2lRespName;

  /**
   * Name of the Responsibility Type
   */
  private String a2lRespType;
  /**
   * Flag to indicate of qnaire is ready for prod
   */
  private boolean qnaireReadyForProd;
  /**
   * Flag to indicate of there are existing qnaire baselines
   */
  private boolean qnaireBaselineExisting;
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
   * Id of qnaire response version
   */
  private Long qnaireRespVersId;
  /**
   * Name of qnaire response version
   */
  private String qnaireRespVersName;
  /**
   * NT-ID of the user who reviewed qnaire
   */
  private String qnaireReviewedUser;
  /**
   * User's display name who reviewed qnaire
   */
  private String qnaireRvdUserDisplayName;
  /**
   * Date when the qnaire was reviewed
   */
  private String qnaireReviewedDate;
  /**
   * Link of the qnaire baseline
   */
  private ExternalLinkInfo qnaireBaselineLink;
  /**
   * Link display name of the qnaire baseline
   */
  private String qnaireBaselineLinkDisplayText;
  /**
   * Questionnaire Response Id
   */
  private Long qnaireRespId;
  /**
   * Questionnaire Response Name
   */
  private String qnaireRespName;

  /**
   * @return the id
   */
  public Long getId() {
    return this.id;
  }

  /**
   * @param id the id to set
   */
  public void setId(final Long id) {
    this.id = id;
  }

  /**
   * @return the a2lWpId
   */
  public Long getA2lWpId() {
    return this.a2lWpId;
  }

  /**
   * @param a2lWpId the a2lWpId to set
   */
  public void setA2lWpId(final Long a2lWpId) {
    this.a2lWpId = a2lWpId;
  }

  /**
   * @return the a2lRespId
   */
  public Long getA2lRespId() {
    return this.a2lRespId;
  }

  /**
   * @param a2lRespId the a2lRespId to set
   */
  public void setA2lRespId(final Long a2lRespId) {
    this.a2lRespId = a2lRespId;
  }

  /**
   * @return the a2lWpName
   */
  public String getA2lWpName() {
    return this.a2lWpName;
  }

  /**
   * @param a2lWpName the a2lWpName to set
   */
  public void setA2lWpName(final String a2lWpName) {
    this.a2lWpName = a2lWpName;
  }

  /**
   * @return the a2lRespName
   */
  public String getA2lRespName() {
    return this.a2lRespName;
  }

  /**
   * @param a2lRespName the a2lRespName to set
   */
  public void setA2lRespName(final String a2lRespName) {
    this.a2lRespName = a2lRespName;
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
   * @return the qnaireRespVersName
   */
  public String getQnaireRespVersName() {
    return this.qnaireRespVersName;
  }

  /**
   * @param qnaireRespVersName the qnaireRespVersName to set
   */
  public void setQnaireRespVersName(final String qnaireRespVersName) {
    this.qnaireRespVersName = qnaireRespVersName;
  }

  /**
   * @return the qnaireReviewedUser
   */
  public String getQnaireReviewedUser() {
    return this.qnaireReviewedUser;
  }

  /**
   * @param qnaireReviewedUser the qnaireReviewedUser to set
   */
  public void setQnaireReviewedUser(final String qnaireReviewedUser) {
    this.qnaireReviewedUser = qnaireReviewedUser;
  }


  /**
   * @return the qnaireRvdUserDisplayName
   */
  public String getQnaireRvdUserDisplayName() {
    return this.qnaireRvdUserDisplayName;
  }


  /**
   * @param qnaireRvdUserDisplayName the qnaireRvdUserDisplayName to set
   */
  public void setQnaireRvdUserDisplayName(final String qnaireRvdUserDisplayName) {
    this.qnaireRvdUserDisplayName = qnaireRvdUserDisplayName;
  }

  /**
   * @return the qnaireReviewedDate
   */
  public String getQnaireReviewedDate() {
    return this.qnaireReviewedDate;
  }

  /**
   * @param qnaireReviewedDate the qnaireReviewedDate to set
   */
  public void setQnaireReviewedDate(final String qnaireReviewedDate) {
    this.qnaireReviewedDate = qnaireReviewedDate;
  }

  /**
   * @return the qnaireBaselineLink
   */
  public ExternalLinkInfo getQnaireBaselineLink() {
    return this.qnaireBaselineLink;
  }

  /**
   * @param qnaireBaselineLink the qnaireBaselineLink to set
   */
  public void setQnaireBaselineLink(final ExternalLinkInfo qnaireBaselineLink) {
    this.qnaireBaselineLink = qnaireBaselineLink;
  }


  /**
   * @return the qnaireBaselineLinkDisplayText
   */
  public String getQnaireBaselineLinkDisplayText() {
    return this.qnaireBaselineLinkDisplayText;
  }


  /**
   * @param qnaireBaselineLinkDisplayText the qnaireBaselineLinkDisplayText to set
   */
  public void setQnaireBaselineLinkDisplayText(final String qnaireBaselineLinkDisplayText) {
    this.qnaireBaselineLinkDisplayText = qnaireBaselineLinkDisplayText;
  }

  /**
   * @return the qnaireRespId
   */
  public Long getQnaireRespId() {
    return this.qnaireRespId;
  }

  /**
   * @param qnaireRespId the qnaireRespId to set
   */
  public void setQnaireRespId(final Long qnaireRespId) {
    this.qnaireRespId = qnaireRespId;
  }

  /**
   * @return the qnaireRespName
   */
  public String getQnaireRespName() {
    return this.qnaireRespName;
  }

  /**
   * @param qnaireRespName the qnaireRespName to set
   */
  public void setQnaireRespName(final String qnaireRespName) {
    this.qnaireRespName = qnaireRespName;
  }


  /**
   * @return the a2lRespType
   */
  public String getA2lRespType() {
    return this.a2lRespType;
  }


  /**
   * @param a2lRespType the a2lRespType to set
   */
  public void setA2lRespType(final String a2lRespType) {
    this.a2lRespType = a2lRespType;
  }


}
