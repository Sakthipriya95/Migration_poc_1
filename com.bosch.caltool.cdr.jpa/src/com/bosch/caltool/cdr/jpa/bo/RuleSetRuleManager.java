/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.List;
import java.util.Map;

import com.bosch.caltool.apic.jpa.IRuleManager;
import com.bosch.caltool.icdm.bo.cdr.SSDServiceHandler;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.SsdInterfaceException;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.CDRRuleExt;
import com.bosch.ssd.icdm.model.FeatureValueModel;
import com.bosch.ssd.icdm.model.SSDMessage;


/**
 * @author jvi6cob
 */
public class RuleSetRuleManager implements IRuleManager {


  private final SSDServiceHandler ssdServiceHandler;
  private final RuleSet ruleSet;

  /**
   * Constructor
   *
   * @param ruleSet RuleSet
   */
  public RuleSetRuleManager(final SSDServiceHandler ssdServiceHandler, final RuleSet ruleSet) {
    super();
    this.ruleSet = ruleSet;
    this.ssdServiceHandler = ssdServiceHandler;
  }

  /**
   * {@inheritDoc}
   */
  // Create rule
  @Override
  public SSDMessage createRule(final CDRRule model, final String paramName) throws IcdmException {
    return this.ssdServiceHandler.createSSDRule(model, this.ruleSet.getSsdNodeId(), null, false);
  }

  /**
   * {@inheritDoc}
   */
  // Create multiple ruleset rules
  @Override
  public SSDMessage createMultipleRules(final List<CDRRule> cdrRules) throws IcdmException {
    return this.ssdServiceHandler.createMultRuleSetRules(cdrRules, this.ruleSet.getSsdNodeId(), this.ruleSet.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage updateRule(final CDRRule cdrRule) throws IcdmException {
    return this.ssdServiceHandler.updateSSDRule(cdrRule, this.ruleSet.getSsdNodeId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage updateMultipleRules(final List<CDRRule> cdrRules) throws IcdmException {
    return this.ssdServiceHandler.updateMultSSDRules(cdrRules, this.ruleSet.getSsdNodeId(), this.ruleSet.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage deleteRule(final CDRRule cdrRule) throws IcdmException {
    return this.ssdServiceHandler.deleteRuleSetRule(this.ruleSet.getId(), cdrRule);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage deleteMultipleRules(final List<CDRRule> cdrRules) throws IcdmException {
    return this.ssdServiceHandler.deleteMultiRuleSetRule(this.ruleSet.getId(), cdrRules);
  }

  /**
   * {@inheritDoc}
   * 
   * @throws SsdInterfaceException
   */
  @Override
  public List<CDRRule> readRule(final String labelName) throws SsdInterfaceException {
    return this.ssdServiceHandler.readReviewRule(labelName, this.ruleSet.getSsdNodeId());
  }

  /**
   * {@inheritDoc}
   * 
   * @throws SsdInterfaceException
   */
  @Override
  public Map<String, List<CDRRule>> readRule(final List<String> labelNames) throws SsdInterfaceException {
    return this.ssdServiceHandler.readReviewRule(labelNames, this.ruleSet.getSsdNodeId());
  }

  // ICDM-1476
  /**
   * Read all avaiable rules of this ruleset, mapped to the SSD Node
   * 
   * @throws SsdInterfaceException
   */
  @Override
  public Map<String, List<CDRRule>> readAllRules() throws SsdInterfaceException {
    return this.ssdServiceHandler.readSSDRulesFromNode(this.ruleSet.getSsdNodeId());
  }

  /**
   * {@inheritDoc}
   * 
   * @throws SsdInterfaceException
   */
  @Override
  public Map<String, List<CDRRule>> readRuleForDependency(final List<String> labelNames,
      final List<FeatureValueModel> dependencies)
      throws SsdInterfaceException {
    return this.ssdServiceHandler.readSSDRuleForDependency(labelNames, dependencies, this.ruleSet.getSsdNodeId());
  }

  /**
   * {@inheritDoc}
   * 
   * @throws SsdInterfaceException
   */
  @Override
  public List<CDRRuleExt> getRuleHistory(final CDRRule rule) throws SsdInterfaceException {
    return this.ssdServiceHandler.getRuleHistoryForNode(rule, this.ruleSet.getSsdNodeId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<CDRRuleExt> getRuleHistoryForNodeCompli(final CDRRule rule) throws IcdmException {
    return this.ssdServiceHandler.getRuleHistoryForNodeCompli(rule);
  }

}
