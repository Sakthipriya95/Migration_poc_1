/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.datamodelstub.ssd;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.CDRRuleExt;
import com.bosch.ssd.icdm.model.CDRRulesWithFile;
import com.bosch.ssd.icdm.model.ComPkgBcModel;
import com.bosch.ssd.icdm.model.FeaValModel;
import com.bosch.ssd.icdm.model.FeatureValueModel;
import com.bosch.ssd.icdm.model.OEMRuleDescriptionInput;
import com.bosch.ssd.icdm.model.ReleaseErrorModel;
import com.bosch.ssd.icdm.model.RuleIdDescriptionModel;
import com.bosch.ssd.icdm.model.SSDConfigEnums.SSDConfigParams;
import com.bosch.ssd.icdm.model.SSDMessage;
import com.bosch.ssd.icdm.model.SSDMessageOptions;
import com.bosch.ssd.icdm.model.SSDRelease;
import com.bosch.ssd.icdm.model.SoftwareVersion;
import com.bosch.ssd.icdm.service.SSDInterface;
import com.bosch.ssd.icdm.service.SSDService;
import com.bosch.ssd.icdm.service.utility.CreateSSDRelease;


/**
 * SSD Service stub
 *
 * @author bne4cob
 */
public class SSDServiceStub implements SSDInterface {

  /**
   * The actual SSD implementation
   */
  private final SSDInterface service;

  /**
   * @param em Entity Manager
   * @param userName user Name
   * @param ssdLogger ssd Logger
   * @param parserLogger parser Logger
   */
  public SSDServiceStub(final EntityManager em, final String userName, final ILoggerAdapter ssdLogger,
      final ILoggerAdapter parserLogger) {
    this.service = new SSDService(userName, em, ssdLogger);
  }

  /**
   * {@inheritDoc}
   *
   * @throws SSDiCDMInterfaceException
   */
  @Override
  public SSDMessage createLabel(final CDRRule model) throws SSDiCDMInterfaceException {
    return this.service.createLabel(model);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BigDecimal isLabelPresent(final String paramName) throws SSDiCDMInterfaceException {
    return this.service.isLabelPresent(paramName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage createReviewRule(final CDRRule model) throws SSDiCDMInterfaceException {
    return this.service.createReviewRule(model);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage updateReviewRule(final CDRRule model) throws SSDiCDMInterfaceException {
    return this.service.updateReviewRule(model);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, List<CDRRule>> readReviewRule(final String functionName, final String functionVersion)
      throws SSDiCDMInterfaceException {
    Map<String, List<CDRRule>> labelRulesMap = this.service.readReviewRule(functionName, functionVersion);
    // fill dummy rules
    createDummyRules(labelRulesMap);
    return labelRulesMap;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<CDRRuleExt> getRuleHistory(final String labelName, final String labelType)
      throws SSDiCDMInterfaceException {
    return this.service.getRuleHistory(labelName, labelType);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<CDRRule> readReviewRule(final String labelName) throws SSDiCDMInterfaceException {
    return this.service.readReviewRule(labelName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, List<CDRRule>> readReviewRule(final List<String> labelNames) throws SSDiCDMInterfaceException {
    return this.service.readReviewRule(labelNames);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSSDFile(final List<String> labelNames) throws SSDiCDMInterfaceException {
    return this.service.getSSDFile(labelNames);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CDRRulesWithFile readRulesandGetSSDFile(final List<String> labelNames) throws SSDiCDMInterfaceException {
    return this.service.readRulesandGetSSDFile(labelNames);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setUserName(final String userName) throws SSDiCDMInterfaceException {
    this.service.setUserName(userName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setNodeId(final BigDecimal nodeId) throws SSDiCDMInterfaceException {
    this.service.setNodeId(nodeId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<ReleaseErrorModel> getReleaseErrors() throws SSDiCDMInterfaceException {
    return this.service.getReleaseErrors();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCompPkgNodeId(final BigDecimal compNodeId) throws SSDiCDMInterfaceException {
    this.service.setCompPkgNodeId(compNodeId);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> getErrorListFromLabellist() throws SSDiCDMInterfaceException {
    return this.service.getErrorListFromLabellist();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<BigDecimal, FeaValModel> getFeaValueForSelection() throws SSDiCDMInterfaceException {
    return this.service.getFeaValueForSelection();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage contReleaseWithfeaValSelection(final Map<BigDecimal, FeaValModel> feaValMap)
      throws SSDiCDMInterfaceException {
    return this.service.contReleaseWithfeaValSelection(feaValMap);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void cancelRelease() throws SSDiCDMInterfaceException {
    this.service.cancelRelease();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long getNoOfParamsInRelease() throws SSDiCDMInterfaceException {
    return this.service.getNoOfParamsInRelease();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String[] getReleaseReports(final String path, final boolean ruleIDFlag) throws SSDiCDMInterfaceException {
    return this.service.getReleaseReports(path, ruleIDFlag);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, CalData> getCalDataMapForCdfx() throws SSDiCDMInterfaceException {
    return this.service.getCalDataMapForCdfx();
  }

  /**
   * {@inheritDoc}
   *
   * @return
   */
  @Override
  public SSDMessage createMultReviewRules(final List<CDRRule> cdrRules) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   *
   * @return
   */
  @Override
  public SSDMessage updateMultReviewRules(final List<CDRRule> cdrRules) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   *
   * @return
   */
  @Override
  public SSDMessage deleteReviewRule(final CDRRule rule) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * This method creates dummy rules for TESTING
   *
   * @param labelRulesMap
   */
  private void createDummyRules(final Map<String, List<CDRRule>> labelRulesMap) {
    final int minCount = 1;
    final int maxCount = 2;
    int index = 0;
    for (String label : labelRulesMap.keySet()) {
      List<CDRRule> ruleList = labelRulesMap.get(label);
      if (ruleList.size() == minCount) {
        while (index < maxCount) {
          CDRRule rule = new CDRRule();
          CDRRule orgRule = ruleList.get(0);
          index = setRuleDetails(index, ruleList, rule, orgRule);
        }
        labelRulesMap.put(label, ruleList);
        index = 0;
      }
    }
  }

  /**
   * @param index
   * @param ruleList
   * @param rule
   * @param orgRule
   * @return
   */
  private int setRuleDetails(int index, final List<CDRRule> ruleList, final CDRRule rule, final CDRRule orgRule) {
    if (orgRule != null) {
      // set unit
      if (orgRule.getUnit() != null) {
        rule.setUnit(orgRule.getUnit());
      }
      // set name
      rule.setParameterName(orgRule.getParameterName());
      if (orgRule.getValueType().equals(CDRRule.VALUE_TEXT)) {
        // set limits
        rule.setLowerLimit(new BigDecimal(index));
        rule.setUpperLimit(new BigDecimal(index + 5));
        // set ref value
        rule.setRefValue(new BigDecimal(index + 3));
        // set dependencies
        setDependencies(orgRule, false);
        setDependencies(rule, true);
        // ruleList.remove(orgRule);
        ruleList.add(rule);
      }
      else if (orgRule.getValueType().equals("MAP")) { // complex types
        // do not add - means - MAP type sample has no dependencies and rules
      }
      else if (orgRule.getValueType().equals("CURVE")) { // complex types
        // add dependency to original rule
        setDependencies(orgRule, true);
        ruleList.add(orgRule);
      }
      index++;
    }
    return index;
  }

  /**
   * @param rule
   */
  private void setDependencies(final CDRRule rule, final boolean isMultiple) {
    // create feature value model list (dummy list)
    List<FeatureValueModel> fvModelList = createFeatureValueModelList(isMultiple);
    rule.setDependencyList(fvModelList);
  }

  /**
   * @return
   */
  private List<FeatureValueModel> createFeatureValueModelList(final boolean isMultiple) {
    // feature ids and value ids are from mapping table
    BigDecimal fId_1 = new BigDecimal(10082);
    BigDecimal vId_1 = new BigDecimal(868);

    BigDecimal fId_2 = new BigDecimal(10023);
    BigDecimal vId_2 = new BigDecimal(852);
    // Create the featureModel list
    List<FeatureValueModel> fvModelList = new ArrayList<FeatureValueModel>();
    FeatureValueModel fvModel = new FeatureValueModel();
    fvModel.setFeatureId(fId_1);
    fvModel.setValueId(vId_1);
    fvModelList.add(fvModel);
    // another model
    if (isMultiple) {
      fvModel = new FeatureValueModel();
      fvModel.setFeatureId(fId_2);
      fvModel.setValueId(vId_2);
      fvModelList.add(fvModel);
    }
    return fvModelList;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage deleteMultiReviewRule(final List<CDRRule> cdrRules) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, List<CDRRule>> readReviewRuleForDependency(final List<String> labelNames,
      final List<FeatureValueModel> dependencies) {
    // TODO Auto-generated method stub
    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public List<CDRRuleExt> getRuleHistory(final CDRRule rule) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage createSSDRule(final CDRRule model, final BigDecimal ssdNodeId, final BigDecimal ssdVersNodeId,
      final boolean isCompPckRule)
      throws SSDiCDMInterfaceException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage updateSSDRule(final CDRRule model, final BigDecimal ssdNodeId) throws SSDiCDMInterfaceException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, List<CDRRule>> readSSDRule(final String functionName, final String functionVersion,
      final BigDecimal ssdNodeId) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<CDRRuleExt> getRuleHistoryForNode(final String labelName, final String labelType,
      final BigDecimal ssdNodeId) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<CDRRuleExt> getRuleHistoryForNode(final CDRRule rule, final BigDecimal ssdNodeId) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<CDRRule> readSSDRules(final String labelName, final BigDecimal nodeId) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, List<CDRRule>> readSSDRule(final List<String> labelNames, final BigDecimal ssdNodeId) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, List<CDRRule>> readSSDRuleForDependency(final List<String> labelNames,
      final List<FeatureValueModel> dependencies, final BigDecimal ssdNodeId) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSSDFileForNode(final List<String> labelNames, final BigDecimal ssdNodeId) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CDRRulesWithFile readRulesandGetSSDFileForNode(final List<String> labelNames, final BigDecimal ssdNodeId) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage createMultSSDRules(final List<CDRRule> cdrRules, final BigDecimal ssdNodeId,
      final BigDecimal ssdVersNodeId, final boolean isCompPckRule)
      throws SSDiCDMInterfaceException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage updateMultSSDRules(final List<CDRRule> cdrRules, final BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException {
    // TODO Auto-generated method stub
    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, List<CDRRule>> readSSDRulesFromNode(final BigDecimal nodeId) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<CDRRule, SSDMessage> createValidMultSSDRules(final List<CDRRule> cdrRules, final BigDecimal ssdNodeId,
      final BigDecimal ssdVersNodeId, final boolean isCompPckRule)
      throws SSDiCDMInterfaceException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<CDRRule, SSDMessage> updateValidMultSSDRules(final List<CDRRule> cdrRules, final BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCompliNodeId(final BigDecimal compliNodeId) {
    // TODO Auto-generated method stub

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public List<CDRRule> readRuleForCompliRelease() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getReleaseReportsForCompli(final String path, final boolean ruleIDFlag)
      throws SSDiCDMInterfaceException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessageOptions invokeComplianceRelease(final Map<String, String> labelList,
      final Set<ComPkgBcModel> compPkgBCs, final boolean isOnlyQSSDParameters)
      throws SSDiCDMInterfaceException {
    // TODO Auto-generated method stub
    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public List<SoftwareVersion> getSwVersionListBySwProjId(final long swProjNodeId) throws SSDiCDMInterfaceException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<SSDRelease> getSSDRelesesBySwVersionId(final long swVersionId) throws SSDiCDMInterfaceException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<CDRRule> readRuleForRelease(final BigDecimal releaseId) throws SSDiCDMInterfaceException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getReleaseReportsByReleaseId(final String path, final BigDecimal releaseId, final boolean flag)
      throws SSDiCDMInterfaceException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BigDecimal getNodeId() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, RuleIdDescriptionModel> getOEMDetailsForRuleId(
      final Set<OEMRuleDescriptionInput> ruleIdWithRevList)
      throws SSDiCDMInterfaceException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSSDFileForDependency(final List<String> labelNames, final List<FeatureValueModel> dependencies,
      final String ssdFilePath) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSSDFileForDependencyForNode(final List<String> labelNames,
      final List<FeatureValueModel> dependencies, final BigDecimal ssdNodeId, final String ssdFilePath) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CDRRulesWithFile readRulesandGetSSDFileDependency(final List<String> labelNames,
      final List<FeatureValueModel> dependencies, final String ssdFilePath) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CDRRulesWithFile readRulesandGetSSDFileDpndyForNode(final List<String> labelNames,
      final List<FeatureValueModel> dependencies, final BigDecimal ssdNodeId, final String ssdFilePath) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, String> getQSSDLabelFromQSSDNode() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BigDecimal getProReleaseID() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getConfigValue(final SSDConfigParams name) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> getQSSDLabels() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<CDRRuleExt> getRuleHistoryForCDRRule(final CDRRule rule) throws SSDiCDMInterfaceException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BigDecimal getProRevId(final BigDecimal nodeId) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CDRRuleExt getAdditionalRuleInformation(final BigDecimal ruleId, final BigDecimal revId) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessageOptions invokeCompPckgRelease(final Map<String, String> labelList,
      final Set<ComPkgBcModel> compPkgBCs)
      throws SSDiCDMInterfaceException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CreateSSDRelease getReleaseUtils() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessageOptions invokeNonSDOMComplianceRelease(final Map<String, String> labelList,
      final boolean isQSSDOnlyParameters)
      throws SSDiCDMInterfaceException {
    // TODO Auto-generated method stub
    return null;
  }


}
