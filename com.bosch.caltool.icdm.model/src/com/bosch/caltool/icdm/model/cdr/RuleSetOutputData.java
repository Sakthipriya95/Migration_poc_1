/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.icdm.model.user.NodeAccess;

/**
 * @author say8cob
 */
public class RuleSetOutputData {

  private RuleSet ruleSet;

  private Set<NodeAccess> ruleSetOwnerNodeAccess = new HashSet<>();


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
   * @return the ruleSetOwnerNodeAccess
   */
  public Set<NodeAccess> getRuleSetOwnerNodeAccess() {
    return this.ruleSetOwnerNodeAccess;
  }


  /**
   * @param ruleSetOwnerNodeAccess the ruleSetOwnerNodeAccess to set
   */
  public void setRuleSetOwnerNodeAccess(final Set<NodeAccess> ruleSetOwnerNodeAccess) {
    this.ruleSetOwnerNodeAccess = ruleSetOwnerNodeAccess;
  }


}
