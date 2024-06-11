/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import com.bosch.caltool.icdm.model.apic.pidc.ProjectIdCardVariantInfoType;
import com.bosch.caltool.icdm.model.apic.pidc.ProjectIdCardVersinfoType;

/**
 * @author NIP4COB
 */
public class ReviewResultsType {

  private Long reviewId;

  private String reviewName;

  private ProjectIdCardVersinfoType pidcId;

  private ProjectIdCardVariantInfoType variantId;

  private String reviewResult;

  private String comment;

  private String checkValStr;

  private String unit;

  private String reviewDate;

  private String reviewType;

  private String reviewMethod;

  private String reviewStatus;

  private String reviewDescription;

  private String paramName;

  private byte[] checkValue;


  /**
   * @return the reviewId
   */
  public Long getReviewId() {
    return this.reviewId;
  }


  /**
   * @param reviewId the reviewId to set
   */
  public void setReviewId(final Long reviewId) {
    this.reviewId = reviewId;
  }


  /**
   * @return the reviewName
   */
  public String getReviewName() {
    return this.reviewName;
  }


  /**
   * @param reviewName the reviewName to set
   */
  public void setReviewName(final String reviewName) {
    this.reviewName = reviewName;
  }


  /**
   * @return the projectIdCardVersInfoType
   */
  public ProjectIdCardVersinfoType getProjectIdCardVersInfoType() {
    return this.pidcId;
  }


  /**
   * @param projectIdCardVersInfoType the projectIdCardVersInfoType to set
   */
  public void setProjectIdCardVersInfoType(final ProjectIdCardVersinfoType projectIdCardVersInfoType) {
    this.pidcId = projectIdCardVersInfoType;
  }


  /**
   * @return the projectIdCardVariantInfoType
   */
  public ProjectIdCardVariantInfoType getProjectIdCardVariantInfoType() {
    return this.variantId;
  }


  /**
   * @param projectIdCardVariantInfoType the projectIdCardVariantInfoType to set
   */
  public void setProjectIdCardVariantInfoType(final ProjectIdCardVariantInfoType projectIdCardVariantInfoType) {
    this.variantId = projectIdCardVariantInfoType;
  }


  /**
   * @return the result
   */
  public String getResult() {
    return this.reviewResult;
  }


  /**
   * @param result the result to set
   */
  public void setResult(final String result) {
    this.reviewResult = result;
  }


  /**
   * @return the comment
   */
  public String getComment() {
    return this.comment;
  }


  /**
   * @param comment the comment to set
   */
  public void setComment(final String comment) {
    this.comment = comment;
  }


  /**
   * @return the checkValStr
   */
  public String getCheckValStr() {
    return this.checkValStr;
  }


  /**
   * @param checkValStr the checkValStr to set
   */
  public void setCheckValStr(final String checkValStr) {
    this.checkValStr = checkValStr;
  }


  /**
   * @return the unit
   */
  public String getUnit() {
    return this.unit;
  }


  /**
   * @param unit the unit to set
   */
  public void setUnit(final String unit) {
    this.unit = unit;
  }


  /**
   * @return the reviewDate
   */
  public String getReviewDate() {
    return this.reviewDate;
  }


  /**
   * @param reviewDate the reviewDate to set
   */
  public void setReviewDate(final String reviewDate) {
    this.reviewDate = reviewDate;
  }


  /**
   * @return the reviewType
   */
  public String getReviewType() {
    return this.reviewType;
  }


  /**
   * @param reviewType the reviewType to set
   */
  public void setReviewType(final String reviewType) {
    this.reviewType = reviewType;
  }


  /**
   * @return the reviewMethod
   */
  public String getReviewMethod() {
    return this.reviewMethod;
  }


  /**
   * @param reviewMethod the reviewMethod to set
   */
  public void setReviewMethod(final String reviewMethod) {
    this.reviewMethod = reviewMethod;
  }


  /**
   * @return the reviewStatus
   */
  public String getReviewStatus() {
    return this.reviewStatus;
  }


  /**
   * @param reviewStatus the reviewStatus to set
   */
  public void setReviewStatus(final String reviewStatus) {
    this.reviewStatus = reviewStatus;
  }


  /**
   * @return the reviewDescription
   */
  public String getReviewDescription() {
    return this.reviewDescription;
  }


  /**
   * @param reviewDescription the reviewDescription to set
   */
  public void setReviewDescription(final String reviewDescription) {
    this.reviewDescription = reviewDescription;
  }


  /**
   * @return the paramName
   */
  public String getParamName() {
    return this.paramName;
  }


  /**
   * @param paramName the paramName to set
   */
  public void setParamName(final String paramName) {
    this.paramName = paramName;
  }


  /**
   * @return the checkValue
   */
  public byte[] getCheckValue() {
    return this.checkValue;
  }


  /**
   * @param checkValue the checkValue to set
   */
  public void setCheckValue(final byte[] checkValue) {
    this.checkValue = checkValue;
  }

}
