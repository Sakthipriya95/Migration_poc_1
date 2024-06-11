/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.List;

/**
 * @author say8cob
 */
public class MonicaReviewInputData {

  private Long pidcA2lId;

  private Long variantId;

  private String description;

  private String ownUserName;

  private List<String> reviewParticipants;

  private String calEngUserName;

  private String audUserName;

  private List<MonicaReviewData> monicaObject;

  /**
   * is delta review
   */
  private boolean isDeltaReview;

  /**
   * Org Result Id
   */
  private Long orgResultId;

  /**
   * Delta Review Type
   */
  private String deltaReviewType;

  private String selMoniCaSheet;


  /**
   * @return the pidcA2lId
   */
  public Long getPidcA2lId() {
    return this.pidcA2lId;
  }


  /**
   * @param pidcA2lId the pidcA2lId to set
   */
  public void setPidcA2lId(final Long pidcA2lId) {
    this.pidcA2lId = pidcA2lId;
  }


  /**
   * @return the varaintId
   */
  public Long getVariantId() {
    return this.variantId;
  }


  /**
   * @param varaintId the varaintId to set
   */
  public void setVariantId(final Long varaintId) {
    this.variantId = varaintId;
  }


  /**
   * @return the description
   */
  public String getDescription() {
    return this.description;
  }


  /**
   * @param description the description to set
   */
  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * @return the ownUserName
   */
  public String getOwnUserName() {
    return this.ownUserName;
  }


  /**
   * @param ownUserName the ownUserName to set
   */
  public void setOwnUserName(final String ownUserName) {
    this.ownUserName = ownUserName;
  }


  /**
   * @return the calEngUserName
   */
  public String getCalEngUserName() {
    return this.calEngUserName;
  }


  /**
   * @param calEngUserName the calEngUserName to set
   */
  public void setCalEngUserName(final String calEngUserName) {
    this.calEngUserName = calEngUserName;
  }


  /**
   * @return the audUserName
   */
  public String getAudUserName() {
    return this.audUserName;
  }


  /**
   * @param audUserName the audUserName to set
   */
  public void setAudUserName(final String audUserName) {
    this.audUserName = audUserName;
  }


  /**
   * @return the monicaObject
   */
  public List<MonicaReviewData> getMonicaObject() {
    return this.monicaObject;
  }


  /**
   * @param monicaObject the monicaObject to set
   */
  public void setMonicaObject(final List<MonicaReviewData> monicaObject) {
    this.monicaObject = monicaObject;
  }


  /**
   * @return the reviewParticipants
   */
  public List<String> getReviewParticipants() {
    return this.reviewParticipants;
  }


  /**
   * @param reviewParticipants the reviewParticipants to set
   */
  public void setReviewParticipants(final List<String> reviewParticipants) {
    this.reviewParticipants = reviewParticipants;
  }


  /**
   * @return the orgResultId
   */
  public Long getOrgResultId() {
    return this.orgResultId;
  }


  /**
   * @param orgResultId the orgResultId to set
   */
  public void setOrgResultId(final Long orgResultId) {
    this.orgResultId = orgResultId;
  }


  /**
   * @return the deltaReviewType
   */
  public String getDeltaReviewType() {
    return this.deltaReviewType;
  }


  /**
   * @param deltaReviewType the deltaReviewType to set
   */
  public void setDeltaReviewType(final String deltaReviewType) {
    this.deltaReviewType = deltaReviewType;
  }


  /**
   * @return the isDeltaReview
   */
  public boolean isDeltaReview() {
    return this.isDeltaReview;
  }


  /**
   * @param isDeltaReview the isDeltaReview to set
   */
  public void setDeltaReview(final boolean isDeltaReview) {
    this.isDeltaReview = isDeltaReview;
  }


  /**
   * @return the selMoniCaSheet
   */
  public String getSelMoniCaSheet() {
    return this.selMoniCaSheet;
  }


  /**
   * @param selMoniCaSheet the selMoniCaSheet to set
   */
  public void setSelMoniCaSheet(final String selMoniCaSheet) {
    this.selMoniCaSheet = selMoniCaSheet;
  }


}
