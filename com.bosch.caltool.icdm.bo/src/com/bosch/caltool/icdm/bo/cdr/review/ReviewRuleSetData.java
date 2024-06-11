/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.review;

import java.util.List;
import java.util.Map;

import com.bosch.caltool.icdm.bo.cdr.CheckSSDResultParam;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.RuleSet;


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
  private Map<String, List<ReviewRule>> cdrRules;
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
  private Long ssdReleaseID;


  /**
   * boolean to say if the rule data has SSD file.
   */
  private boolean isSSDFileReview;

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
  public void setSSDRules(final Map<String, List<ReviewRule>> cdrRules) {
    this.cdrRules = cdrRules;
  }


  /**
   * @return the cdrRules
   */
  public Map<String, List<ReviewRule>> getCdrRules() {
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

    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (obj.getClass() == this.getClass()) {
      ReviewRuleSetData data = (ReviewRuleSetData) obj;
      if (getSource() == data.getSource()) {
        return getRuleSet() == data.getRuleSet();
      }
      return getSource() == data.getSource();
    }

    return false;
  }

  /**
   * @author rgo7cob
   */
  public enum RULE_SOURCE {
                           /**
                            * Common rules
                            */
                           COMMON_RULES("Common Rules", "C"),
                           /**
                            * Rule set
                            */
                           RULE_SET("Rule Set", "R"),
                           /**
                            * ssd release
                            */
                           SSD_RELEASE("SSD Release", "S"),

                           /**
                            * ssd file
                            */
                           SSD_FILE("SSD File", "F");

    private final String uiVal;

    /**
     * @return the uiVal
     */
    public String getUiVal() {
      return this.uiVal;
    }


    /**
     * @return the dbVal
     */
    public String getDbVal() {
      return this.dbVal;
    }

    private final String dbVal;

    RULE_SOURCE(final String uiVal, final String dbVal) {
      this.uiVal = uiVal;
      this.dbVal = dbVal;
    }

    /**
     * @param dbvalue dbvalue
     * @return the db val
     */
    public static RULE_SOURCE getSource(final String dbvalue) {
      for (RULE_SOURCE source : RULE_SOURCE.values()) {
        if (source.dbVal.equals(dbvalue)) {
          return source;
        }
      }
      return null;
    }

  }
}
