/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author say8cob
 */
public class ReviewResult implements Comparable<ReviewResult> {

  private String reviewDate;

  private String pverName;

  private String a2lVersion;

  private String pidcVariant;

  private String reviewDescription;

  private String reviewType;

  private String reviewStatus;

  private String isLocked;

  private String reviewScope;

  private String scopeName;

  private String calEngineer;

  private String auditor;

  private String parentReview;

  private String ruleSetName;

  private String baseReview;


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
   * @return the pverName
   */
  public String getPverName() {
    return this.pverName;
  }


  /**
   * @param pverName the pverName to set
   */
  public void setPverName(final String pverName) {
    this.pverName = pverName;
  }


  /**
   * @return the a2lVersion
   */
  public String getA2lVersion() {
    return this.a2lVersion;
  }


  /**
   * @param a2lVersion the a2lVersion to set
   */
  public void setA2lVersion(final String a2lVersion) {
    this.a2lVersion = a2lVersion;
  }


  /**
   * @return the pidcVariant
   */
  public String getPidcVariant() {
    return this.pidcVariant;
  }


  /**
   * @param pidcVariant the pidcVariant to set
   */
  public void setPidcVariant(final String pidcVariant) {
    this.pidcVariant = pidcVariant;
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
   * @return the isLocked
   */
  public String getIsLocked() {
    return this.isLocked;
  }


  /**
   * @param isLocked the isLocked to set
   */
  public void setIsLocked(final String isLocked) {
    this.isLocked = isLocked;
  }


  /**
   * @return the reviewScope
   */
  public String getReviewScope() {
    return this.reviewScope;
  }


  /**
   * @param reviewScope the reviewScope to set
   */
  public void setReviewScope(final String reviewScope) {
    this.reviewScope = reviewScope;
  }


  /**
   * @return the scopeName
   */
  public String getScopeName() {
    return this.scopeName;
  }


  /**
   * @param scopeName the scopeName to set
   */
  public void setScopeName(final String scopeName) {
    this.scopeName = scopeName;
  }


  /**
   * @return the calEngineer
   */
  public String getCalEngineer() {
    return this.calEngineer;
  }


  /**
   * @param calEngineer the calEngineer to set
   */
  public void setCalEngineer(final String calEngineer) {
    this.calEngineer = calEngineer;
  }


  /**
   * @return the auditor
   */
  public String getAuditor() {
    return this.auditor;
  }


  /**
   * @param auditor the auditor to set
   */
  public void setAuditor(final String auditor) {
    this.auditor = auditor;
  }


  /**
   * @return the parentReview
   */
  public String getParentReview() {
    return this.parentReview;
  }


  /**
   * @param parentReview the parentReview to set
   */
  public void setParentReview(final String parentReview) {
    this.parentReview = parentReview;
  }


  /**
   * @return the ruleSetName
   */
  public String getRuleSetName() {
    return this.ruleSetName;
  }


  /**
   * @param ruleSetName the ruleSetName to set
   */
  public void setRuleSetName(final String ruleSetName) {
    this.ruleSetName = ruleSetName;
  }


  /**
   * @return the baseReview
   */
  public String getBaseReview() {
    return this.baseReview;
  }


  /**
   * @param baseReview the baseReview to set
   */
  public void setBaseReview(final String baseReview) {
    this.baseReview = baseReview;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final ReviewResult o) {
    return ModelUtil.compare(getPverName(), o.getPverName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    return ModelUtil.isEqual(getPverName(), ((ReviewResult) obj).getPverName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getPverName());
  }
}
