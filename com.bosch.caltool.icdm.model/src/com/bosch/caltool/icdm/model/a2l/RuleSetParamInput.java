/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

/**
 * @author say8cob
 */
public class RuleSetParamInput {

  private Long ruleSetId;

  private Long a2lFileId;

  /**
   * @return the ruleSetId
   */
  public Long getRuleSetId() {
    return this.ruleSetId;
  }


  /**
   * @param ruleSetId the ruleSetId to set
   */
  public void setRuleSetId(final Long ruleSetId) {
    this.ruleSetId = ruleSetId;
  }


  /**
   * @return the a2lFileId
   */
  public Long getA2lFileId() {
    return this.a2lFileId;
  }


  /**
   * @param a2lFileId the a2lFileId to set
   */
  public void setA2lFileId(final Long a2lFileId) {
    this.a2lFileId = a2lFileId;
  }


}
