/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.ssd;


/**
 * @author rgo7cob
 */
public class OEMRuleDescriptionInput {


  private String ruleId;

  private String revision;


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
   * @return the revision
   */
  public String getRevision() {
    return this.revision;
  }


  /**
   * @param revision the revision to set
   */
  public void setRevision(final String revision) {
    this.revision = revision;
  }

}
