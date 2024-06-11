/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;

/**
 * @author rgo7cob
 */
public class RuleSetParamWithFunction {

  private RuleSetParameter ruleSetParam;

  private Function function;


  /**
   * @return the ruleSetParam
   */
  public RuleSetParameter getRuleSetParam() {
    return this.ruleSetParam;
  }


  /**
   * @param ruleSetParam the ruleSetParam to set
   */
  public void setRuleSetParam(final RuleSetParameter ruleSetParam) {
    this.ruleSetParam = ruleSetParam;
  }


  /**
   * @return the function
   */
  public Function getFunction() {
    return this.function;
  }


  /**
   * @param function the function to set
   */
  public void setFunction(final Function function) {
    this.function = function;
  }

}
