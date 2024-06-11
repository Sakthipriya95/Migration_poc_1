/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.ArrayList;
import java.util.List;

/**
 * @author UKT1COB
 */
public class RuleLinkWrapperData {

  private List<RuleLinks> listOfRuleLinksToBeCreated = new ArrayList<>();

  private List<RuleLinks> listOfRuleLinksToBeUpd = new ArrayList<>();

  private List<RuleLinks> listOfRuleLinksToBeDel = new ArrayList<>();

  private List<RuleLinks> listOfExistingLinksForSelRule = new ArrayList<>();


  /**
   * @return the listOfRuleLinksToBeCreated
   */
  public List<RuleLinks> getListOfRuleLinksToBeCreated() {
    return this.listOfRuleLinksToBeCreated;
  }


  /**
   * @return the listOfRuleLinksToBeDel
   */
  public List<RuleLinks> getListOfRuleLinksToBeDel() {
    return this.listOfRuleLinksToBeDel;
  }


  /**
   * @param listOfRuleLinksToBeCreated the listOfRuleLinksToBeCreated to set
   */
  public void setListOfRuleLinksToBeCreated(final List<RuleLinks> listOfRuleLinksToBeCreated) {
    this.listOfRuleLinksToBeCreated = listOfRuleLinksToBeCreated;
  }

  /**
   * @param listOfRuleLinksToBeDel the listOfRuleLinksToBeDel to set
   */
  public void setListOfRuleLinksToBeDel(final List<RuleLinks> listOfRuleLinksToBeDel) {
    this.listOfRuleLinksToBeDel = listOfRuleLinksToBeDel;
  }


  /**
   * @return the listOfRuleLinksToBeUpd
   */
  public List<RuleLinks> getListOfRuleLinksToBeUpd() {
    return this.listOfRuleLinksToBeUpd;
  }


  /**
   * @param listOfRuleLinksToBeUpd the listOfRuleLinksToBeUpd to set
   */
  public void setListOfRuleLinksToBeUpd(final List<RuleLinks> listOfRuleLinksToBeUpd) {
    this.listOfRuleLinksToBeUpd = listOfRuleLinksToBeUpd;
  }


  /**
   * @return the listOfExistingLinksForSelRule
   */
  public List<RuleLinks> getListOfExistingLinksForSelRule() {
    return this.listOfExistingLinksForSelRule;
  }


  /**
   * @param listOfExistingLinksForSelRule the listOfExistingLinksForSelRule to set
   */
  public void setListOfExistingLinksForSelRule(final List<RuleLinks> listOfExistingLinksForSelRule) {
    this.listOfExistingLinksForSelRule = listOfExistingLinksForSelRule;
  }
}
