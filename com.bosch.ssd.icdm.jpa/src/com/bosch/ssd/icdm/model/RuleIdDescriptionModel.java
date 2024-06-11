/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.model;


/**
 * @author SSN9COB
 * Model class for the Rule Id with Detailed description for each revision
 */
public class RuleIdDescriptionModel {

  private String ruleId;
  
  private String revId;
  
  private String dataDescription;
  
  private String internalAdaptionDescription;
  
  private String historyDescription;
 
  /**
   * Contains Rule Id with RevId. Format - "RuleId:RevId"
   */
  private String ruleIdWihtRev;

  
  /**
   * @return the dataDescription
   */
  public String getDataDescription() {
    return dataDescription;
  }

  
  /**
   * @param dataDescription the dataDescription to set
   */
  public void setDataDescription(String dataDescription) {
    this.dataDescription = dataDescription;
  }

  
  /**
   * @return the internalAdaptionDescription
   */
  public String getInternalAdaptionDescription() {
    return internalAdaptionDescription;
  }

  
  /**
   * @param internalAdaptionDescription the internalAdaptionDescription to set
   */
  public void setInternalAdaptionDescription(String internalAdaptionDescription) {
    this.internalAdaptionDescription = internalAdaptionDescription;
  }

  
  /**
   * @return the historyDescription
   */
  public String getHistoryDescription() {
    return historyDescription;
  }

  
  /**
   * @param historyDescription the historyDescription to set
   */
  public void setHistoryDescription(String historyDescription) {
    this.historyDescription = historyDescription;
  }

  
  /**
   * @return the ruleIdWihtRev
   */
  public String getRuleIdWihtRev() {
    return ruleIdWihtRev;
  }

  
  /**
   * @param ruleIdWihtRev the ruleIdWihtRev to set
   */
  public void setRuleIdWihtRev(String ruleIdWihtRev) {
    this.ruleIdWihtRev = ruleIdWihtRev;
  }


  /**
   * @return the ruleId
   */
  public String getRuleId() {
    return ruleId;
  }


  /**
   * @param ruleId the ruleId to set
   */
  public void setRuleId(String ruleId) {
    this.ruleId = ruleId;
  }


  /**
   * @return the revId
   */
  public String getRevId() {
    return revId;
  }


  /**
   * @param revId the revId to set
   */
  public void setRevId(String revId) {
    this.revId = revId;
  }
  
  
}