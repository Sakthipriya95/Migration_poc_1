/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.ssd.icdm.exception.ExceptionUtils;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException.SSDiCDMInterfaceErrorCodes;
import com.bosch.ssd.icdm.logger.SSDiCDMInterfaceLogger;
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
import com.bosch.ssd.icdm.service.internal.servinterface.SSDInternalAccessor;
import com.bosch.ssd.icdm.service.utility.CreateSSDRelease;


/**
 * SSD Service class that iCDM Code will invoke
 *
 * @author SSN9COB
 */
public class SSDService implements SSDInterface {

  /**
   *
   */
  private static final String UN_IMPLEMENTED_METHODS = "Method Deprecated /  not implemented in SSD-Interface Service";
  private final SSDInternalAccessor serviceAccessor;


  /**
   * usename would be set to the given username, if not already set
   *
   * @param userName name
   * @param em entity Manager
   * @param logger loggerInst
   */
  public SSDService(final String userName, final EntityManager em, final ILoggerAdapter logger) {
    this.serviceAccessor = new SSDInternalAccessor(logger, userName, em);
  }

  /**
   * usename would be set to the given username, if not already set, Without logger
   *
   * @param userName name
   * @param em entity Manager
   */
  public SSDService(final String userName, final EntityManager em) {
    this.serviceAccessor = new SSDInternalAccessor(SSDiCDMInterfaceLogger.getLogger(), userName, em);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BigDecimal getNodeId() throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getSSDNodeInfo().getReviewRuleNodeId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage createLabel(final CDRRule model) throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getSSDReviewRulesController().createLabel(model);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BigDecimal isLabelPresent(final String paramName) throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getSSDReviewRulesController().isLabelPresent(paramName);
  }

  /**
   * to create review rules in SSD based on the information in the model
   *
   * @param model - to be filled
   * @return CDR Rule
   * @throws SSDiCDMInterfaceException Exception return - 0 when rule gets created and populate CDRModel with ruleid ==
   *           -1 when label is not defined in SSD == -2 when rule validation fails (eg- insert called when update
   *           should be called, or labels other than value type are passed) == -3 when rule already exist for given
   *           label
   */
  @Override
  public SSDMessage createReviewRule(final CDRRule model) throws SSDiCDMInterfaceException {

    return this.serviceAccessor.getSSDReviewRulesController().createReviewSSDRule(model,
        this.serviceAccessor.getSSDNodeInfo().getReviewRuleNodeId(), null, false);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage createSSDRule(final CDRRule model, final BigDecimal ssdNodeId, final BigDecimal ssdVersNodeId,
      final boolean isCompPckRule)
      throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getSSDReviewRulesController().createReviewSSDRule(model, ssdNodeId, ssdVersNodeId,
        isCompPckRule);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage updateReviewRule(final CDRRule model) throws SSDiCDMInterfaceException {

    return this.serviceAccessor.getSSDReviewRulesController().updateReviewSSDRule(model,
        this.serviceAccessor.getSSDNodeInfo().getReviewRuleNodeId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage updateSSDRule(final CDRRule model, final BigDecimal ssdNodeId) throws SSDiCDMInterfaceException {

    return this.serviceAccessor.getSSDReviewRulesController().updateReviewSSDRule(model, ssdNodeId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, List<CDRRule>> readReviewRule(final String functionName, final String functionVersion)
      throws SSDiCDMInterfaceException {

    return this.serviceAccessor.getSSDReviewRulesController().readSSDRuleFromFunction(functionName, functionVersion,
        this.serviceAccessor.getSSDNodeInfo().getReviewRuleNodeId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, List<CDRRule>> readSSDRule(final String functionName, final String functionVersion,
      final BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getSSDReviewRulesController().readSSDRuleFromFunction(functionName, functionVersion,
        ssdNodeId);
  }

  /**
   * {@inheritDoc}
   */
  @Deprecated
  @Override
  public List<CDRRuleExt> getRuleHistory(final String labelName, final String labelType)
      throws SSDiCDMInterfaceException {

    throw ExceptionUtils.createAndThrowException(null, UN_IMPLEMENTED_METHODS,
        SSDiCDMInterfaceErrorCodes.GENERAL_EXCEPTION, true);
  }

  /**
   * {@inheritDoc}
   */
  @Deprecated
  @Override
  public List<CDRRuleExt> getRuleHistoryForNode(final String labelName, final String labelType,
      final BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException {

    throw ExceptionUtils.createAndThrowException(null, UN_IMPLEMENTED_METHODS,
        SSDiCDMInterfaceErrorCodes.GENERAL_EXCEPTION, true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<CDRRuleExt> getRuleHistory(final CDRRule rule) throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getRuleHistoryController().getRuleHistoryForNode(rule,
        this.serviceAccessor.getSSDNodeInfo().getReviewRuleNodeId(), false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<CDRRuleExt> getRuleHistoryForNode(final CDRRule rule, final BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getRuleHistoryController().getRuleHistoryForNode(rule, ssdNodeId, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<CDRRule> readReviewRule(final String labelName) throws SSDiCDMInterfaceException {

    return this.serviceAccessor.getSSDReviewRulesController().readSSDRules(labelName,
        this.serviceAccessor.getSSDNodeInfo().getReviewRuleNodeId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<CDRRule> readSSDRules(final String labelName, final BigDecimal nodeId) throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getSSDReviewRulesController().readSSDRules(labelName, nodeId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, List<CDRRule>> readSSDRulesFromNode(final BigDecimal nodeId) throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getSSDReviewRulesController().readSSDRulesFromNode(nodeId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, List<CDRRule>> readReviewRule(final List<String> labelNames) throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getSSDReviewRulesController().readSSDRule(labelNames,
        this.serviceAccessor.getSSDNodeInfo().getReviewRuleNodeId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, List<CDRRule>> readSSDRule(final List<String> labelNames, final BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getSSDReviewRulesController().readSSDRule(labelNames, ssdNodeId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, List<CDRRule>> readReviewRuleForDependency(final List<String> labelNames,
      final List<FeatureValueModel> dependencies)
      throws SSDiCDMInterfaceException {

    return this.serviceAccessor.getSSDReviewRulesController().readSSDRuleForDependency(labelNames, dependencies,
        this.serviceAccessor.getSSDNodeInfo().getReviewRuleNodeId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, List<CDRRule>> readSSDRuleForDependency(final List<String> labelNames,
      final List<FeatureValueModel> dependencies, final BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getSSDReviewRulesController().readSSDRuleForDependency(labelNames, dependencies,
        ssdNodeId);
  }

  /**
   * {@inheritDoc}
   */
  @Deprecated
  @Override
  public String getSSDFile(final List<String> labelNames) throws SSDiCDMInterfaceException {
    throw ExceptionUtils.createAndThrowException(null, UN_IMPLEMENTED_METHODS,
        SSDiCDMInterfaceErrorCodes.GENERAL_EXCEPTION, true);
  }

  /**
   * {@inheritDoc}
   */
  @Deprecated
  @Override
  public String getSSDFileForNode(final List<String> labelNames, final BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException {
    throw ExceptionUtils.createAndThrowException(null, UN_IMPLEMENTED_METHODS,
        SSDiCDMInterfaceErrorCodes.GENERAL_EXCEPTION, true);
  }

  /**
   * {@inheritDoc}
   */
  @Deprecated
  @Override
  public CDRRulesWithFile readRulesandGetSSDFile(final List<String> labelNames) throws SSDiCDMInterfaceException {
    throw ExceptionUtils.createAndThrowException(null, UN_IMPLEMENTED_METHODS,
        SSDiCDMInterfaceErrorCodes.GENERAL_EXCEPTION, true);
  }

  /**
   * {@inheritDoc}
   */
  @Deprecated
  @Override
  public CDRRulesWithFile readRulesandGetSSDFileForNode(final List<String> labelNames, final BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException {
    throw ExceptionUtils.createAndThrowException(null, UN_IMPLEMENTED_METHODS,
        SSDiCDMInterfaceErrorCodes.GENERAL_EXCEPTION, true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage createMultReviewRules(final List<CDRRule> cdrRules) throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getSSDReviewRulesController().createMultReviewSSDRules(cdrRules,
        this.serviceAccessor.getSSDNodeInfo().getReviewRuleNodeId(), null, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage createMultSSDRules(final List<CDRRule> cdrRules, final BigDecimal ssdNodeId,
      final BigDecimal ssdVersNodeId, final boolean isCompPckRule)
      throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getSSDReviewRulesController().createMultReviewSSDRules(cdrRules, ssdNodeId,
        ssdVersNodeId, isCompPckRule);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage updateMultReviewRules(final List<CDRRule> cdrRules) throws SSDiCDMInterfaceException {

    return this.serviceAccessor.getSSDReviewRulesController().updateMultReviewSSDRules(cdrRules,
        this.serviceAccessor.getSSDNodeInfo().getReviewRuleNodeId());
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public SSDMessage updateMultSSDRules(final List<CDRRule> cdrRules, final BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getSSDReviewRulesController().updateMultReviewSSDRules(cdrRules, ssdNodeId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage deleteReviewRule(final CDRRule rule) throws SSDiCDMInterfaceException {

    return this.serviceAccessor.getSSDReviewRulesController().deleteReviewRules(Arrays.asList(rule));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage deleteMultiReviewRule(final List<CDRRule> cdrRules) throws SSDiCDMInterfaceException {

    return this.serviceAccessor.getSSDReviewRulesController().deleteReviewRules(cdrRules);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSSDFileForDependency(final List<String> labelNames, final List<FeatureValueModel> dependencies,
      final String ssdFilePath)
      throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getReportController().getSSDFile(labelNames, dependencies,
        this.serviceAccessor.getSSDNodeInfo().getReviewRuleNodeId(), ssdFilePath);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSSDFileForDependencyForNode(final List<String> labelNames,
      final List<FeatureValueModel> dependencies, final BigDecimal ssdNodeId, final String ssdFilePath)
      throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getReportController().getSSDFile(labelNames, dependencies, ssdNodeId, ssdFilePath);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CDRRulesWithFile readRulesandGetSSDFileDependency(final List<String> labelNames,
      final List<FeatureValueModel> dependencies, final String ssdFilePath)
      throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getReportController().getRulesAndSSDFile(labelNames, dependencies,
        this.serviceAccessor.getSSDNodeInfo().getReviewRuleNodeId(), ssdFilePath);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CDRRulesWithFile readRulesandGetSSDFileDpndyForNode(final List<String> labelNames,
      final List<FeatureValueModel> dependencies, final BigDecimal ssdNodeId, final String ssdFilePath)
      throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getReportController().getRulesAndSSDFile(labelNames, dependencies, ssdNodeId,
        ssdFilePath);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setUserName(final String userName) throws SSDiCDMInterfaceException {
    this.serviceAccessor.getSSDNodeInfo().setUserName(userName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setNodeId(final BigDecimal nodeId) throws SSDiCDMInterfaceException {
    this.serviceAccessor.getSSDNodeInfo().setReviewRuleNodeId(nodeId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String[] getReleaseReports(final String path, final boolean ruleIdFlag) throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getReportController().getReleaseReports(path, ruleIdFlag,
        this.serviceAccessor.getReleaseGenerationController().getSSDReleaseGenerationService().getReleaseUtils());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<ReleaseErrorModel> getReleaseErrors() throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getReportController().getReleaseErrors(
        this.serviceAccessor.getReleaseGenerationController().getSSDReleaseGenerationService().getReleaseUtils());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCompPkgNodeId(final BigDecimal compNodeId) throws SSDiCDMInterfaceException {
    this.serviceAccessor.getSSDNodeInfo().setCompPkgNodeId(compNodeId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCompliNodeId(final BigDecimal compliNodeId) throws SSDiCDMInterfaceException {
    this.serviceAccessor.getSSDNodeInfo().setCompliNodeId(compliNodeId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessageOptions invokeCompPckgRelease(final Map<String, String> labelList,
      final Set<ComPkgBcModel> compPkgBCs)
      throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getReleaseGenerationController().invokeCompPckgRelease(labelList, compPkgBCs,
        this.serviceAccessor.getSSDNodeInfo().getCompPkgNodeId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> getErrorListFromLabellist() throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getReleaseGenerationController().getErrorListFromLabellist();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<BigDecimal, FeaValModel> getFeaValueForSelection() throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getReleaseGenerationController().getFeaValueForSelection();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessage contReleaseWithfeaValSelection(final Map<BigDecimal, FeaValModel> feaValMap)
      throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getReleaseGenerationController().contReleaseWithfeaValSelection(feaValMap);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void cancelRelease() throws SSDiCDMInterfaceException {
    this.serviceAccessor.getReleaseGenerationController().cancelRelease();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long getNoOfParamsInRelease() throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getReportController().getNoOfParamsInRelease();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, CalData> getCalDataMapForCdfx() throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getReportController().getCalDataMapForCdfx();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<CDRRule, SSDMessage> createValidMultSSDRules(final List<CDRRule> cdrRules, final BigDecimal ssdNodeId,
      final BigDecimal ssdVersNodeId, final boolean isCompPckRule)
      throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getSSDReviewRulesController().createValidMultSSDRules(cdrRules, ssdNodeId,
        ssdVersNodeId, isCompPckRule);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<CDRRule, SSDMessage> updateValidMultSSDRules(final List<CDRRule> cdrRules, final BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException {

    return this.serviceAccessor.getSSDReviewRulesController().updateValidMultSSDRules(cdrRules, ssdNodeId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessageOptions invokeComplianceRelease(final Map<String, String> labelList,
      final Set<ComPkgBcModel> compPkgBCs, final boolean isQSSDOnlyParameters)
      throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getReleaseGenerationController().invokeComplianceRelease(labelList, compPkgBCs,
        isQSSDOnlyParameters, this.serviceAccessor.getSSDNodeInfo().getCompliNodeId());
  }

  /**
   * {@inheritDoc}
  
  @Override
  public SSDMessageOptions invokeNonSDOMComplianceRelease(final Map<String, String> labelList,
      final boolean isQSSDOnlyRelease)
      throws SSDiCDMInterfaceException {
    // TODO - Create new revision node for NON SDOM SW release
    return this.serviceAccessor.getReleaseGenerationController().invokeComplianceReleaseForNonSDOMSW(labelList,
        isQSSDOnlyRelease, this.serviceAccessor.getSSDNodeInfo().getCompliNodeId());
  }
 */
  /**
   * {@inheritDoc}
   */
  @Override
  public List<CDRRule> readRuleForCompliRelease() throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getReportController().readRuleForRelease(
        this.serviceAccessor.getReleaseGenerationController().getSSDReleaseGenerationService().getProReleaseID(), true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getReleaseReportsForCompli(final String path, final boolean ruleIdFlag)
      throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getReportController().getReleaseReportsForCompli(path, ruleIdFlag,
        this.serviceAccessor.getReleaseGenerationController().getSSDReleaseGenerationService().getReleaseUtils());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<CDRRuleExt> getRuleHistoryForCDRRule(final CDRRule rule) throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getRuleHistoryController().getRuleHistoryForNode(rule,
        this.serviceAccessor.getSSDNodeInfo().getReviewRuleNodeId(), true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<SoftwareVersion> getSwVersionListBySwProjId(final long swProjNodeId) throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getReportController().getSwVersionListBySwProjId(swProjNodeId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<SSDRelease> getSSDRelesesBySwVersionId(final long swVersionId) throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getReportController().getSSDRelesesBySwVersionId(new BigDecimal(swVersionId));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<CDRRule> readRuleForRelease(final BigDecimal releaseId) throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getReportController().readRuleForRelease(releaseId, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getReleaseReportsByReleaseId(final String path, final BigDecimal releaseId, final boolean ruleIdFlag)
      throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getReportController().getReleaseReportsByReleaseId(path, releaseId, ruleIdFlag);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, RuleIdDescriptionModel> getOEMDetailsForRuleId(
      final Set<OEMRuleDescriptionInput> ruleIdWithRevList)
      throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getSSDCommonController().fetchOEMDetails(ruleIdWithRevList);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, String> getQSSDLabelFromQSSDNode() throws SSDiCDMInterfaceException {
    throw ExceptionUtils.createAndThrowException(null, UN_IMPLEMENTED_METHODS,
        SSDiCDMInterfaceErrorCodes.GENERAL_EXCEPTION, true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BigDecimal getProReleaseID() throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getReleaseGenerationController().getSSDReleaseGenerationService().getProReleaseID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getConfigValue(final SSDConfigParams name) throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getSSDCommonController().getConfigValue(name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> getQSSDLabels() throws SSDiCDMInterfaceException {
    throw ExceptionUtils.createAndThrowException(null, UN_IMPLEMENTED_METHODS,
        SSDiCDMInterfaceErrorCodes.GENERAL_EXCEPTION, true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BigDecimal getProRevId(final BigDecimal nodeId) throws SSDiCDMInterfaceException {
    return this.serviceAccessor.getSSDCommonController().getProRevId(nodeId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CDRRuleExt getAdditionalRuleInformation(final BigDecimal ruleId, final BigDecimal revId)
      throws SSDiCDMInterfaceException {
    throw ExceptionUtils.createAndThrowException(null, UN_IMPLEMENTED_METHODS,
        SSDiCDMInterfaceErrorCodes.GENERAL_EXCEPTION, true);
  }

  /**
   * @return the releaseUtils
   */
  @Override
  public CreateSSDRelease getReleaseUtils() {
    return this.serviceAccessor.getReleaseGenerationController().getReleaseUtils();
  }
  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessageOptions invokeNonSDOMComplianceRelease(final Map<String, String> labelList,
      final boolean isQSSDOnlyRelease)
      throws SSDiCDMInterfaceException {
    // TODO - Create new revision node for NON SDOM SW release
    return this.serviceAccessor.getReleaseGenerationController().invokeComplianceReleaseForNonSDOMSW(labelList,
        isQSSDOnlyRelease, this.serviceAccessor.getSSDNodeInfo().getCompliNodeId());
  }

}
