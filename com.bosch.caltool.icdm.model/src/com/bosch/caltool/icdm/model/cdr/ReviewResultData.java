/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.List;

import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.CDR_SOURCE_TYPE;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author say8cob
 */
public class ReviewResultData implements Comparable<ReviewResultData> {

  private CDRReviewResult cdrReviewResult;

  private CDR_SOURCE_TYPE cdrSourceType;

  private Long fc2wpId;

  private String fc2WorkPkgName;


  private PidcVariant pidcVariant;

  private String rvwRltName;

  private List<Long> variantIds;

  /**
   * linked variant to cdr result.
   */
  private PidcVariant linkedVar;

  private String calEngineer;

  private String auditor;

  private String parentReview;

  private String ruleSetName;

  private String baseReview;

  private String pidcVariantName;

  private String scopeName;

  private String pverName;


  /**
   * @return the cdrSourceType
   */
  public CDR_SOURCE_TYPE getCdrSourceType() {
    return this.cdrSourceType;
  }


  /**
   * @param cdrSourceType the cdrSourceType to set
   */
  public void setCdrSourceType(final CDR_SOURCE_TYPE cdrSourceType) {
    this.cdrSourceType = cdrSourceType;
  }


  /**
   * @return the fc2wpId
   */
  public Long getFc2wpId() {
    return this.fc2wpId;
  }


  /**
   * @param fc2wpId the fc2wpId to set
   */
  public void setFc2wpId(final Long fc2wpId) {
    this.fc2wpId = fc2wpId;
  }


  /**
   * @return the fc2WorkPkgName
   */
  public String getFc2WorkPkgName() {
    return this.fc2WorkPkgName;
  }


  /**
   * @param fc2WorkPkgName the fc2WorkPkgName to set
   */
  public void setFc2WorkPkgName(final String fc2WorkPkgName) {
    this.fc2WorkPkgName = fc2WorkPkgName;
  }


  /**
   * @return the pidcVariant
   */
  public PidcVariant getPidcVariant() {
    return this.pidcVariant;
  }


  /**
   * @param pidcVariant the pidcVariant to set
   */
  public void setPidcVariant(final PidcVariant pidcVariant) {
    this.pidcVariant = pidcVariant;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final ReviewResultData o) {
    return ModelUtil.compare(this.cdrReviewResult.getId(), o.cdrReviewResult.getId());

  }


  /**
   * @return the rvwRltName
   */
  public String getRvwRltName() {
    return this.rvwRltName;
  }


  /**
   * @param rvwRltName the rvwRltName to set
   */
  public void setRvwRltName(final String rvwRltName) {
    this.rvwRltName = rvwRltName;
  }


  /**
   * @return the variantIds
   */
  public List<Long> getVariantIds() {
    return this.variantIds;
  }


  /**
   * @param variantIds the variantIds to set
   */
  public void setVariantIds(final List<Long> variantIds) {
    this.variantIds = variantIds;
  }


  /**
   * @return the linkedVar
   */
  public PidcVariant getLinkedVar() {
    return this.linkedVar;
  }


  /**
   * @param linkedVar the linkedVar to set
   */
  public void setLinkedVar(final PidcVariant linkedVar) {
    this.linkedVar = linkedVar;
  }


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
   * @return the pidcVariantName
   */
  public String getPidcVariantName() {
    return this.pidcVariantName;
  }


  /**
   * @param pidcVariantName the pidcVariantName to set
   */
  public void setPidcVariantName(final String pidcVariantName) {
    this.pidcVariantName = pidcVariantName;
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
    return ModelUtil.isEqual(getCdrReviewResult().getId(), ((ReviewResultData) obj).getCdrReviewResult().getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getCdrReviewResult().getId());
  }
}
