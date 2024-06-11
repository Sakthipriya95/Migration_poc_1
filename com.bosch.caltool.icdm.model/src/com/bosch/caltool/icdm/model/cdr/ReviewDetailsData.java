/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import com.bosch.caltool.icdm.model.a2l.Function;

/**
 * @author dmr1cob
 */
public class ReviewDetailsData {

  private CDRResultParameter cdrResultParameter;

  private RuleSet ruleSet;

  private Function function;


  /**
   * @return the cdrResultParameter
   */
  public CDRResultParameter getCdrResultParameter() {
    return cdrResultParameter;
  }


  /**
   * @param cdrResultParameter the cdrResultParameter to set
   */
  public void setCdrResultParameter(CDRResultParameter cdrResultParameter) {
    this.cdrResultParameter = cdrResultParameter;
  }


  /**
   * @return the ruleSet
   */
  public RuleSet getRuleSet() {
    return ruleSet;
  }


  /**
   * @param ruleSet the ruleSet to set
   */
  public void setRuleSet(RuleSet ruleSet) {
    this.ruleSet = ruleSet;
  }


  /**
   * @return the function
   */
  public Function getFunction() {
    return function;
  }


  /**
   * @param function the function to set
   */
  public void setFunction(Function function) {
    this.function = function;
  }

}
