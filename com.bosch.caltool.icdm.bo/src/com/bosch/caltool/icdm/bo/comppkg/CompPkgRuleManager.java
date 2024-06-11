/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.comppkg;

import java.util.List;
import java.util.Map;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.cdr.IRuleManager;
import com.bosch.caltool.icdm.bo.cdr.ReviewRuleAdapter;
import com.bosch.caltool.icdm.bo.cdr.SSDServiceHandler;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.CDRRuleExt;
import com.bosch.ssd.icdm.model.FeatureValueModel;
import com.bosch.ssd.icdm.model.SSDMessage;

/**
 * @author dmr1cob
 */
public class CompPkgRuleManager extends AbstractSimpleBusinessObject implements IRuleManager {

  private final SSDServiceHandler ssdServiceHandler;
  private final CompPackage compPackage;

  /**
   * Constructor
   *
   * @param serviceHandler SSDServiceHandler
   */
  public CompPkgRuleManager(final SSDServiceHandler serviceHandler, final CompPackage compPackage,ServiceData serviceData) {
    super(serviceData);
    this.ssdServiceHandler = serviceHandler;
    this.compPackage = compPackage;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage createRule(final CDRRule cdrRules, final String paramName) throws IcdmException {
    return this.ssdServiceHandler.createReviewRule(cdrRules, paramName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage createMultipleRules(final List<CDRRule> cdrRules) throws IcdmException {
    return this.ssdServiceHandler.createMultReviewRules(cdrRules);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage updateRule(final CDRRule cdrRule) throws IcdmException {
    return this.ssdServiceHandler.updateReviewRule(cdrRule);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage updateMultipleRules(final List<CDRRule> cdrRules) throws IcdmException {
    return this.ssdServiceHandler.updateMultReviewRules(cdrRules);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage deleteRule(final CDRRule cdrRule) throws IcdmException {
    return this.ssdServiceHandler.deleteReviewRule(cdrRule);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage deleteMultipleRules(final List<CDRRule> cdrRules) throws IcdmException {
    return this.ssdServiceHandler.deleteMultiReviewRule(cdrRules);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<CDRRule> readRule(final String labelName) throws IcdmException {
    return this.ssdServiceHandler.readReviewRule(labelName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, List<CDRRule>> readRule(final List<String> labelNames) throws IcdmException {
    return this.ssdServiceHandler.readReviewRule(labelNames);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, List<CDRRule>> readAllRules() throws IcdmException {
    return this.ssdServiceHandler.readSSDRulesFromNode(this.compPackage.getSsdNodeId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, List<CDRRule>> readRuleForDependency(final List<String> labelNames,
      final List<FeatureValueModel> dependencies)
      throws IcdmException {
    return this.ssdServiceHandler.readReviewRuleForDependency(labelNames, dependencies);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<CDRRuleExt> getRuleHistory(final ReviewRule rule) throws IcdmException {
    CDRRule cdrRule = convertReviewRuleToCDRRule(rule);
    return this.ssdServiceHandler.getRuleHistory(cdrRule);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<CDRRuleExt> getRuleHistoryForNodeCompli(final ReviewRule rule) throws IcdmException {
    CDRRule cdrRule = convertReviewRuleToCDRRule(rule);
    return this.ssdServiceHandler.getRuleHistoryForNodeCompli(cdrRule);
  }

  /**
   * This method will convert the ReviewRule to CDRRule which internally handles the isVariantCoded parameter basename checks
   * @param rule
   * @return
   * @throws DataException
   */
  private CDRRule convertReviewRuleToCDRRule(final ReviewRule rule) throws DataException {
    ReviewRuleAdapter adapter = new ReviewRuleAdapter(getServiceData());
    boolean variantCoded = ApicUtil.isVariantCoded(rule.getParameterName());
    rule.setParameterName(variantCoded ? ApicUtil.getBaseParamName(rule.getParameterName()): rule.getParameterName());
    return adapter.createCdrRule(rule);
  }
}
