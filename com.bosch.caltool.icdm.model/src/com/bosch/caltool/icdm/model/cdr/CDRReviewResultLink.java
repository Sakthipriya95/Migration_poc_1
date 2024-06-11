/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;


/**
 * @author say8cob
 */
public class CDRReviewResultLink {

  private String cdrLinkDisplayText;

  private String cdrLinkUrl;

  private Long resultId;

  private String monicaReviewStatus;


  /**
   * @return the cdrLinkDisplayText
   */
  public String getCdrLinkDisplayText() {
    return this.cdrLinkDisplayText;
  }


  /**
   * @param cdrLinkDisplayText the cdrLinkDisplayText to set
   */
  public void setCdrLinkDisplayText(final String cdrLinkDisplayText) {
    this.cdrLinkDisplayText = cdrLinkDisplayText;
  }


  /**
   * @return the cdrLinkUrl
   */
  public String getCdrLinkUrl() {
    return this.cdrLinkUrl;
  }


  /**
   * @param cdrLinkUrl the cdrLinkUrl to set
   */
  public void setCdrLinkUrl(final String cdrLinkUrl) {
    this.cdrLinkUrl = cdrLinkUrl;
  }


  /**
   * @return
   */
  public String getMonicaReviewStatus() {
    return this.monicaReviewStatus;
  }


  /**
   * @param monicaReviewStatus
   */
  public void setMonicaReviewStatus(final String monicaReviewStatus) {
    this.monicaReviewStatus = monicaReviewStatus;
  }


  /**
   * @return the resultId
   */
  public Long getResultId() {
    return this.resultId;
  }


  /**
   * @param resultId the resultId to set
   */
  public void setResultId(final Long resultId) {
    this.resultId = resultId;
  }


}
