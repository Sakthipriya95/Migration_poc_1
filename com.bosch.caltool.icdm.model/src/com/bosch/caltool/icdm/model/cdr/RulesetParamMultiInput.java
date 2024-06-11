/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.HashSet;
import java.util.Set;

/**
 * @author dmo5cob
 */
public class RulesetParamMultiInput {

  /**
   * Set of Validity attribute values selected from the AddValidityAttrValDialog
   */
  private Set<RuleSetParameter> ruleSetParamtoInsert = new HashSet<>();
  /**
   * Set of Validity attribute values to be deleted
   */
  private Set<RuleSetParameter> ruleSetParamtoDel = new HashSet<>();

  /**
   * @return the ruleSetParamtoInsert
   */
  public Set<RuleSetParameter> getRuleSetParamtoInsert() {
    return this.ruleSetParamtoInsert;
  }

  /**
   * @param ruleSetParamtoInsert the ruleSetParamtoInsert to set
   */
  public void setRuleSetParamtoInsert(final Set<RuleSetParameter> ruleSetParamtoInsert) {
    this.ruleSetParamtoInsert = ruleSetParamtoInsert == null ? new HashSet<>() : new HashSet<>(ruleSetParamtoInsert);
  }


  /**
   * @return the ruleSetParamtoDel
   */
  public Set<RuleSetParameter> getRuleSetParamtoDel() {
    return this.ruleSetParamtoDel;
  }


  /**
   * @param ruleSetParamtoDel the ruleSetParamtoDel to set
   */
  public void setRuleSetParamtoDel(final Set<RuleSetParameter> ruleSetParamtoDel) {
    this.ruleSetParamtoDel = ruleSetParamtoDel == null ? new HashSet<>() : new HashSet<>(ruleSetParamtoDel);
  }


}
