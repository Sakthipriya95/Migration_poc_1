/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.ssd;


/**
 * @author rgo7cob
 */
public class RuleIdIcdmDescriptionModel {


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
    return this.dataDescription;
  }


  /**
   * @param dataDescription the dataDescription to set
   */
  public void setDataDescription(final String dataDescription) {
    this.dataDescription = dataDescription;
  }


  /**
   * @return the internalAdaptionDescription
   */
  public String getInternalAdaptionDescription() {
    return this.internalAdaptionDescription;
  }


  /**
   * @param internalAdaptionDescription the internalAdaptionDescription to set
   */
  public void setInternalAdaptionDescription(final String internalAdaptionDescription) {
    this.internalAdaptionDescription = internalAdaptionDescription;
  }


  /**
   * @return the historyDescription
   */
  public String getHistoryDescription() {
    return this.historyDescription;
  }


  /**
   * @param historyDescription the historyDescription to set
   */
  public void setHistoryDescription(final String historyDescription) {
    this.historyDescription = historyDescription;
  }


  /**
   * @return the ruleIdWihtRev
   */
  public String getRuleIdWihtRev() {
    return this.ruleIdWihtRev;
  }


  /**
   * @param ruleIdWihtRev the ruleIdWihtRev to set
   */
  public void setRuleIdWihtRev(final String ruleIdWihtRev) {
    this.ruleIdWihtRev = ruleIdWihtRev;
  }


  /**
   * @return the ruleId
   */
  public String getRuleId() {
    return this.ruleId;
  }


  /**
   * @param ruleId the ruleId to set
   */
  public void setRuleId(final String ruleId) {
    this.ruleId = ruleId;
  }


  /**
   * @return the revId
   */
  public String getRevId() {
    return this.revId;
  }


  /**
   * @param revId the revId to set
   */
  public void setRevId(final String revId) {
    this.revId = revId;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return super.toString() + " [ruleId=" + this.ruleId + ", revId=" + this.revId + ", dataDescription=" +
        this.dataDescription + ", internalAdaptionDescription=" + this.internalAdaptionDescription +
        ", historyDescription=" + this.historyDescription + ", ruleIdWihtRev=" + this.ruleIdWihtRev + "]";
  }


}