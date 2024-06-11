/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.jpa.bo;

import java.util.List;
import java.util.Map;

import com.bosch.caltool.apic.jpa.IRuleManager;
import com.bosch.caltool.icdm.bo.cdr.SSDServiceHandler;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.SsdInterfaceException;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.CDRRuleExt;
import com.bosch.ssd.icdm.model.FeatureValueModel;
import com.bosch.ssd.icdm.model.SSDMessage;


/**
 * Rule manager of component packages
 *
 * @author bne4cob
 */
@Deprecated
public class CompPkgRuleManager implements IRuleManager {


  private final SSDServiceHandler ssdServiceHandler;

  /**
   * Constructor
   *
   * @param compPkg component package
   */
  public CompPkgRuleManager(final SSDServiceHandler ssdServiceHandler) {

    this.ssdServiceHandler = ssdServiceHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage createRule(final CDRRule model, final String paramName) throws IcdmException {
    // return this.ssdServiceHandler.createSSDRule(model, getSsdNodeID(),
    // this.compPkg.getCaldataImporterObject().getSsdVersNodeID(), false);
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage createMultipleRules(final List<CDRRule> cdrRules) throws IcdmException {
    // return this.ssdServiceHandler.createMultRuleSetRules(cdrRules, getSsdNodeID(), this.compPkg.getID());
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage updateRule(final CDRRule cdrRule) throws IcdmException {
    return this.ssdServiceHandler.updateSSDRule(cdrRule, getSsdNodeID());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage updateMultipleRules(final List<CDRRule> cdrRules) throws IcdmException {
    // return this.ssdServiceHandler.updateMultSSDRules(cdrRules, getSsdNodeID(), this.compPkg.getID());
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage deleteRule(final CDRRule cdrRule) throws IcdmException {
    // return this.ssdServiceHandler.deleteRuleSetRule(this.compPkg.getID(), cdrRule);
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage deleteMultipleRules(final List<CDRRule> cdrRules) throws IcdmException {
    // return this.ssdServiceHandler.deleteMultiReviewRule(cdrRules, this.compPkg.getApicDataProvider());
    return null;
  }

  /**
   * {@inheritDoc}
   * 
   * @throws SsdInterfaceException
   */
  @Override
  public List<CDRRule> readRule(final String labelName) throws SsdInterfaceException {
    return this.ssdServiceHandler.readReviewRule(labelName, getSsdNodeID());
  }

  /**
   * {@inheritDoc}
   * 
   * @throws SsdInterfaceException
   */
  @Override
  public Map<String, List<CDRRule>> readRule(final List<String> labelNames) throws SsdInterfaceException {
    return this.ssdServiceHandler.readReviewRule(labelNames, getSsdNodeID());
  }

  // ICDM-1476
  /**
   * Read all avaiable rules of this component package, mapped to the SSD Node
   * 
   * @throws SsdInterfaceException
   */
  @Override
  public Map<String, List<CDRRule>> readAllRules() throws SsdInterfaceException {
    Map<String, List<CDRRule>> ruleMap = this.ssdServiceHandler.readSSDRulesFromNode(getSsdNodeID());
    for (List<CDRRule> paramRuleList : ruleMap.values()) {
      // this.compPkg.getApicDataProvider().getRuleCache().updateRules(getSsdNodeID(), this.compPkg.getID(),
      // paramRuleList,
      // true);
    }
    return ruleMap;
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
    return this.ssdServiceHandler.readSSDRuleForDependency(labelNames, dependencies, getSsdNodeID());
  }

  /**
   * {@inheritDoc}
   * 
   * @throws SsdInterfaceException
   */
  @Override
  public List<CDRRuleExt> getRuleHistory(final CDRRule rule) throws SsdInterfaceException {
    return this.ssdServiceHandler.getRuleHistoryForNode(rule, getSsdNodeID());
  }


  /**
   * @return SSD Node ID of component package
   */
  private Long getSsdNodeID() {
    // return this.compPkg.getCaldataImporterObject().getSsdNodeID();
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<CDRRuleExt> getRuleHistoryForNodeCompli(final CDRRule rule) throws IcdmException {
    return this.ssdServiceHandler.getRuleHistoryForNodeCompli(rule);
  }

}
