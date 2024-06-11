/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;

/**
 * @author rgo7cob
 */
public class ReviewResultType {

  private Long reviewId;

  private String reviewName;

  private PidcVersion pidcversion;


  private PidcVariant pidcVar;

  private String result;

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
   * @return the pidcversion
   */
  public PidcVersion getPidcversion() {
    return this.pidcversion;
  }


  /**
   * @param pidcversion the pidcversion to set
   */
  public void setPidcversion(final PidcVersion pidcversion) {
    this.pidcversion = pidcversion;
  }


  /**
   * @return the pidcVar
   */
  public PidcVariant getPidcVar() {
    return this.pidcVar;
  }


  /**
   * @param pidcVar the pidcVar to set
   */
  public void setPidcVar(final PidcVariant pidcVar) {
    this.pidcVar = pidcVar;
  }


  /**
   * @return the result
   */
  public String getResult() {
    return this.result;
  }


  /**
   * @param result the result to set
   */
  public void setResult(final String result) {
    this.result = result;
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
    this.checkValue = checkValue == null ? null : checkValue.clone();
  }

}
