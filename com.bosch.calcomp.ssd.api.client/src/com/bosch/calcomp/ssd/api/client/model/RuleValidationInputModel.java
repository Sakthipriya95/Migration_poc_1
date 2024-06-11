/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.ssd.api.client.model;


/**
 * Input model class for Rule validation service
 */
public class RuleValidationInputModel {

  private String ruleText;


  /**
   *
   */
  public RuleValidationInputModel() {
    // TODO Auto-generated constructor stub
  }


  /**
   *
   */
  public RuleValidationInputModel(final String rule) {
    this.ruleText = rule;
  }

  /**
   * @return the ruleText
   */
  public String getRuleText() {
    return this.ruleText;
  }

  /**
   * @param ruleText the ruleText to set
   */
  public void setRuleText(final String ruleText) {
    this.ruleText = ruleText;
  }


}
