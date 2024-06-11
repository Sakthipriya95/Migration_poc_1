/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.HashSet;
import java.util.Set;

/**
 * @author say8cob
 */
public class RuleSetInputData {

  private RuleSet ruleSet;

  private Set<Long> ruleSetOwnerIds = new HashSet<>();

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
   * @return the ruleSetOwnerIds
   */
  public Set<Long> getRuleSetOwnerIds() {
    return this.ruleSetOwnerIds;
  }


  /**
   * @param ruleSetOwnerIds the ruleSetOwnerIds to set
   */
  public void setRuleSetOwnerIds(final Set<Long> ruleSetOwnerIds) {
    this.ruleSetOwnerIds = ruleSetOwnerIds;
  }


}
