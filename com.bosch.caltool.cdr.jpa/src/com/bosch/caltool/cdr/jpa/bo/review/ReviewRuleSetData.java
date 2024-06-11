/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo.review;

import java.util.List;
import java.util.Map;

import com.bosch.caltool.cdr.jpa.bo.CDRSecondaryResult.RULE_SOURCE;
import com.bosch.caltool.cdr.jpa.bo.CheckSSDResultParam;
import com.bosch.caltool.cdr.jpa.bo.RuleSet;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.ssd.icdm.model.CDRRule;


/**
 * @author rgo7cob
 */
public class ReviewRuleSetData {


  /**
   * rule set Object
   */
  private RuleSet ruleSet;
  /**
   * map of rules for param
   */
  private Map<String, List<CDRRule>> cdrRules;
  /**
   * ssd file path
   */
  private String ssdFilePath;

  /**
   * check SSD result map for params
   */
  private Map<String, CheckSSDResultParam> checkSSDResParamMap;

  /**
   * ssd release ID
   */
  public Long ssdReleaseID;


  /**
   * boolean to say if the rule data has SSD file.
   */
  public boolean isSSDFileReview;

  /**
   * ssd version id
   */
  private long ssdVersionID;


  /**
   * @return the isSSDFileReview
   */
  public boolean isSSDFileReview() {
    return this.isSSDFileReview;
  }


  /**
   * @param isSSDFileReview the isSSDFileReview to set
   */
  public void setSSDFileReview(final boolean isSSDFileReview) {
    this.isSSDFileReview = isSSDFileReview;
  }


  /**
   * @return the ruleSet
   */
  public RuleSet getRuleSet() {
    return this.ruleSet;
  }


  /**
   * @param ruleSet the ruleSet to set
   */
  public void setRuleSet(final RuleSet ruleSet) {
    this.ruleSet = ruleSet;
  }


  /**
   * @param cdrRules Map<String, List<CDRRule>
   */
  public void setSSDRules(final Map<String, List<CDRRule>> cdrRules) {
    this.cdrRules = cdrRules;
  }


  /**
   * @return the cdrRules
   */
  public Map<String, List<CDRRule>> getCdrRules() {
    return this.cdrRules;
  }


  /**
   * @param ssdFilePath String
   */
  public void setSSDFilePath(final String ssdFilePath) {
    this.ssdFilePath = ssdFilePath;
  }


  /**
   * @return the ssdFilePath
   */
  public String getSsdFilePath() {
    return this.ssdFilePath;
  }


  /**
   * @return the checkSSDResParamMap
   */
  public Map<String, CheckSSDResultParam> getCheckSSDResParamMap() {
    return this.checkSSDResParamMap;
  }


  /**
   * @param checkSSDResParamMap the checkSSDResParamMap to set
   */
  public void setCheckSSDResParamMap(final Map<String, CheckSSDResultParam> checkSSDResParamMap) {
    this.checkSSDResParamMap = checkSSDResParamMap;
  }


  /**
   * @return the ssdReleaseID
   */
  public Long getSsdReleaseID() {
    return this.ssdReleaseID;
  }


  /**
   * @param ssdReleaseID the ssdReleaseID to set
   */
  public void setSsdReleaseID(final Long ssdReleaseID) {
    this.ssdReleaseID = ssdReleaseID;
  }


  /**
   * @return the ssdVersionID
   */
  public long getSsdVersionID() {
    return this.ssdVersionID;
  }


  /**
   * @param ssdVersionID the ssdVersionID to set
   */
  public void setSsdVersionID(final long ssdVersionID) {
    this.ssdVersionID = ssdVersionID;
  }


  /**
   * @return thes source
   */
  public RULE_SOURCE getSource() {
    if (this.isSSDFileReview) {
      return RULE_SOURCE.SSD_FILE;
    }

    if (CommonUtils.isNotNull(this.ssdReleaseID) && (this.ssdReleaseID != 0l)) {
      return RULE_SOURCE.SSD_RELEASE;
    }
    if (CommonUtils.isNotNull(this.ruleSet)) {
      return RULE_SOURCE.RULE_SET;
    }
    return RULE_SOURCE.COMMON_RULES;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof ReviewRuleSetData) {
      ReviewRuleSetData data = (ReviewRuleSetData) obj;
      if (getSource() == data.getSource()) {
        return getRuleSet() == data.getRuleSet();
      }
      return getSource() == data.getSource();
    }

    return super.equals(obj);
  }

}
