/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.common.exception.SsdInterfaceException;
import com.bosch.caltool.icdm.common.util.Timer;
import com.bosch.caltool.icdm.logger.SSDLogger;
import com.bosch.caltool.icdm.model.cdr.SSDProjectVersion;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
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


/**
 * Handler class for SSD interface. Manages interaction with SSDService.
 *
 * @author DMO5COB
 */
public class SSDServiceHandler extends AbstractSimpleBusinessObject {

  /**
   * Error code of all SSD interface wrapper errors
   */
  private static final String SSD_INTERFACE_ERR_CODE = "SSD.INTERFACE_ERR";

  /**
   * Dummy name of the Entity Manager given for SSD interface
   */
  private static final String SSD_ENTITY_MANAGER = "SSD_EM";

  /**
   * SSDService instance
   */
  private final SSDInterface ssdService;


  /**
   * Constructor
   *
   * @param serviceData service data
   * @throws SsdInterfaceException Interface creation error
   */
  public SSDServiceHandler(final ServiceData serviceData) throws SsdInterfaceException {

    super(serviceData);

    CommonParamLoader cpLdr = new CommonParamLoader(serviceData);
    final String nodeIdStr = cpLdr.getValue(CommonParamKey.SSD_NODE_ID);
    final String cmpPkgNodeIdStr = cpLdr.getValue(CommonParamKey.SSD_COMP_PKG_NODE_ID);
    final String compliNodeIdStr = cpLdr.getValue(CommonParamKey.SSD_COMPLI_RULE_NODE_ID);

    BigDecimal nodeId = BigDecimal.valueOf(Long.valueOf(nodeIdStr));

//    SSDiCDMServiceAccessor ssdServiceAccessor = new SSDiCDMServiceAccessor(SSDLogger.getInstance(),serviceData.getUsername(), );
//    SSDServiceAccessor.initAccessor(SSDLogger.getInstance(), ParserLogger.getInstance(), nodeId);
//    this.ssdService =
//        ssdServiceAccessor.createServiceInstance(serviceData.getEntMgr(SSD_ENTITY_MANAGER), serviceData.getUsername());

    // TODO Pass ParserLogger to SSD service
    this.ssdService =
        new SSDService(serviceData.getUsername(), serviceData.getEntMgr(SSD_ENTITY_MANAGER), SSDLogger.getInstance());
    setUserName(serviceData.getUsername());

    setNodeId(nodeId);

    setCompPkgNodeId(BigDecimal.valueOf(Long.valueOf(cmpPkgNodeIdStr)));

    // ICDM-2440
    setCompliParamNodeId(BigDecimal.valueOf(Long.valueOf(compliNodeIdStr)));
  }


  /**
   * @param compliParamNodeId
   * @throws SsdInterfaceException
   */
  private void setCompliParamNodeId(final BigDecimal compliParamNodeId) throws SsdInterfaceException {
    try {
      this.ssdService.setCompliNodeId(compliParamNodeId);
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }


  /**
   * @param compPkgNodeId
   * @throws SsdInterfaceException
   */
  private void setCompPkgNodeId(final BigDecimal compPkgNodeId) throws SsdInterfaceException {
    try {
      this.ssdService.setCompPkgNodeId(compPkgNodeId);
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * to create review rules in SSD based on the information in the model
   *
   * @param model - to be filled
   * @param paramName parameter name
   * @return - 0 when rule gets created and populate CDRModel with ruleid == -1 when label is not defined in SSD == -2
   *         when rule validation fails
   * @throws SsdInterfaceException exception during insert
   */
  public SSDMessage createReviewRule(final CDRRule model, final String paramName) throws SsdInterfaceException {
    getLogger().debug("Invoking SSDInterface.createReviewRule() for CDRRule model and paramName = {}", paramName);
    Timer timer = new Timer();

    try {
      SSDMessage message = this.ssdService.createReviewRule(model);
      getLogger().debug("Invoking SSDInterface.createReviewRule() completed. SSD Message = {}, time taken = {}",
          message, timer.finish());
      return message;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }

  }

  /**
   * to update an exiting review rule
   *
   * @param model - model to be filled with data for update
   * @return 0 on success
   * @throws SsdInterfaceException exception during update
   */
  public SSDMessage updateReviewRule(final CDRRule model) throws SsdInterfaceException {
    getLogger().debug("Invoking SSDInterface.updateReviewRule() on CDRRule model");
    Timer timer = new Timer();

    try {
      SSDMessage message = this.ssdService.updateReviewRule(model);
      getLogger().debug("Invoking SSDInterface.updateReviewRule() completed. SSD Message = {}, time taken = {}",
          message, timer.finish());

      return message;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }


  /**
   * to delete an exiting ruleSet rule
   *
   * @param rsId RuleSet ID
   * @param cdrRule CDRRule to be filled with data for update
   * @return SSDMessage
   * @throws SsdInterfaceException ssd interface error
   */
  public SSDMessage deleteRuleSetRule(final Long rsId, final CDRRule cdrRule) throws SsdInterfaceException {
    getLogger().debug("Invoking SSDInterface.deleteRuleSetRule() on CDRRule model");
    Timer timer = new Timer();

    try {
      SSDMessage message = this.ssdService.deleteReviewRule(cdrRule);
      getLogger().debug("Invoking SSDInterface.deleteRuleSetRule() completed. SSD Message = {}, time taken = {}",
          message, timer.finish());
      return message;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }

  }

  /**
   * Method to read review rules from database for a single label
   *
   * @param functionName - name of the funcion
   * @param functionVersion - version if specific, or null to select all versions
   * @return - populated model
   * @throws SsdInterfaceException ssd interface error
   * @deprecated not used anymore
   */
  @Deprecated
  public Map<String, List<CDRRule>> readReviewRule(final String functionName, final String functionVersion)
      throws SsdInterfaceException {
    getLogger().debug("Invoking SSDInterface.readReviewRule() with function={}, version={}", functionName,
        functionVersion);
    Timer timer = new Timer();

    try {
      Map<String, List<CDRRule>> retMap = this.ssdService.readReviewRule(functionName, functionVersion);
      getLogger().debug("Invoking SSDInterface.readReviewRule() completed. Ret Map Size = {}, time taken = {}",
          retMap.size(), timer.finish());

      return retMap;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }


  /**
   * New method for fetching the rules Method to read review rules from database for a list of labels
   *
   * @param labelNames - name of the funcions
   * @return - populated model
   * @throws SsdInterfaceException ssd interface error
   */
  // Icdm-896
  public Map<String, List<CDRRule>> readReviewRule(final List<String> labelNames) throws SsdInterfaceException {
    getLogger().debug("Invoking SSDInterface.readReviewRule() for list of label names, count = {}", labelNames.size());
    Timer timer = new Timer();

    try {
      Map<String, List<CDRRule>> retMap = this.ssdService.readReviewRule(labelNames);
      getLogger().debug("Invoking SSDInterface.readReviewRule() completed. RetMap = {}, time taken = {}", retMap.size(),
          timer.finish());

      return retMap;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * iCDM-1366 - Read rules for a specific node ( project specific rule set)
   *
   * @param labelNames paramater names
   * @param ssdNodeId nodeId from which the rules needs to be fetched
   * @return CDRRules
   * @throws SsdInterfaceException ssd interface error
   */
  public Map<String, List<CDRRule>> readReviewRule(final List<String> labelNames, final Long ssdNodeId)
      throws SsdInterfaceException {
    getLogger().debug("Invoking SSDInterface.readReviewRule() of labels={}, node={}", labelNames.size(), ssdNodeId);
    Timer timer = new Timer();

    try {
      Map<String, List<CDRRule>> retMap = this.ssdService.readSSDRule(labelNames, BigDecimal.valueOf(ssdNodeId));
      getLogger().debug("Invoking SSDInterface.readReviewRule() completed. Ret size = {}, time taken = {}",
          retMap.size(), timer.finish());

      return retMap;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * to read review rule for one parameter
   *
   * @param labelName - parameter name
   * @return list of cdrrules
   * @throws SsdInterfaceException ssd interface error
   */
  public List<CDRRule> readReviewRule(final String labelName) throws SsdInterfaceException {
    getLogger().debug("Invoking SSDInterface.readReviewRule() of label={}", labelName);
    Timer timer = new Timer();

    try {
      List<CDRRule> retList = this.ssdService.readReviewRule(labelName);
      getLogger().debug("Invoking SSDInterface.readReviewRule() completed. Ret-count = {}, time taken = {}",
          retList.size(), timer.finish());

      return retList;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * Read rules for a specific node ( project specific rule set)
   *
   * @param labelName paramater names
   * @param ssdNodeId nodeId from which the rules needs to be fetched
   * @return CDRRules
   * @throws SsdInterfaceException ssd interface error
   */
  // iCDM-1366
  public List<CDRRule> readReviewRule(final String labelName, final Long ssdNodeId) throws SsdInterfaceException {
    getLogger().debug("Invoking SSDInterface.readReviewRule() of label={}, node={}", labelName, ssdNodeId);
    Timer timer = new Timer();

    try {
      List<CDRRule> retList = this.ssdService.readSSDRules(labelName, BigDecimal.valueOf(ssdNodeId));
      getLogger().debug("Invoking SSDInterface.readReviewRule() completed. Ret = {}, time taken = {}", retList.size(),
          timer.finish());

      return retList;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  // ICDM-1086
  /**
   * Method to get rules history
   *
   * @param rule selected rule
   * @return history of selected rule
   * @throws SsdInterfaceException ssd interface error
   */
  public List<CDRRuleExt> getRuleHistory(final CDRRule rule) throws SsdInterfaceException {
    getLogger().debug("Invoking SSDInterface.getRuleHistory() of CDRRule");
    Timer timer = new Timer();

    try {
      List<CDRRuleExt> retList = this.ssdService.getRuleHistory(rule);
      getLogger().debug("Invoking SSDInterface.getRuleHistory() completed. Ret={}, time taken = {}", retList.size(),
          timer.finish());

      return retList;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }

  }

  /**
   * Get compli rule's history
   *
   * @param rule selected rule
   * @return history of selected rule
   * @throws SsdInterfaceException interface error
   */
  public List<CDRRuleExt> getRuleHistoryForNodeCompli(final CDRRule rule) throws SsdInterfaceException {
    getLogger().debug("Invoking SSDInterface.getRuleHistoryForNodeCompli() of CDRRule");
    try {
      Timer timer = new Timer();

      List<CDRRuleExt> retList = this.ssdService.getRuleHistoryForCDRRule(rule);
      getLogger().debug(
          "Invoking SSDInterface.getRuleHistoryForNodeCompli() completed. Ret count = {}, time taken = {}",
          retList.size(), timer.finish());

      return retList;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }

  }

  /**
   * username for the session to be set
   *
   * @param userName - user connected
   * @throws SsdInterfaceException ssd interface error
   */
  private void setUserName(final String userName) throws SsdInterfaceException {
    try {
      this.ssdService.setUserName(userName);
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * nodeid on which the rules are to be populated
   *
   * @param nodeId - to be taken from config tables
   * @throws SsdInterfaceException
   */
  private void setNodeId(final BigDecimal nodeId) throws SsdInterfaceException {
    try {
      this.ssdService.setNodeId(nodeId);
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * @return ssd service node id
   * @throws SsdInterfaceException ssd interface error
   */
  public BigDecimal getNodeId() throws SsdInterfaceException {
    try {
      return this.ssdService.getNodeId();
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * @return ssd pro release id
   * @throws SsdInterfaceException ssd interface error
   */
  public BigDecimal getProReleaseId() throws SsdInterfaceException {
    try {
      return this.ssdService.getProReleaseID();
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * @param labelMap map of lables
   * @param compPkgBCs list of BCs
   * @return SSDMessage
   * @throws SsdInterfaceException ssd interface error
   * @deprecated comp package module is obsolete now
   */
  @Deprecated
  public SSDMessageOptions invokeCompPckgRelease(final Map<String, String> labelMap,
      final Set<ComPkgBcModel> compPkgBCs)
      throws SsdInterfaceException {

    getLogger().debug("Invoking SSDInterface.invokeCompPckgRelease() with labellist={} and BCs={}", labelMap.size(),
        compPkgBCs.size());
    Timer timer = new Timer();


    try {
      SSDMessageOptions ssdMsgOpt = this.ssdService.invokeCompPckgRelease(labelMap, compPkgBCs);
      getLogger().debug("Invoking SSDInterface.invokeCompPckgRelease() completed. Time taken = {}", timer.finish());

      if ((ssdMsgOpt.getNoNodeBcList() != null) && !ssdMsgOpt.getNoNodeBcList().isEmpty()) {
        getLogger()
            .error("Missing BC nodes : " + ssdMsgOpt.getNoNodeBcList().stream().collect(Collectors.joining(", ")));
      }

      return ssdMsgOpt;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * @return map
   * @throws SsdInterfaceException ssd interface error
   */
  public Map<BigDecimal, FeaValModel> getFeaValueForSelection() throws SsdInterfaceException {
    try {
      return this.ssdService.getFeaValueForSelection();
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * @param feaValMap .
   * @return .
   * @throws SsdInterfaceException ssd interface error
   */
  public SSDMessage contReleaseWithfeaValSelection(final Map<BigDecimal, FeaValModel> feaValMap)
      throws SsdInterfaceException {
    getLogger().debug("Invoking SSDInterface.contReleaseWithfeaValSelection() with feaValMap={}", feaValMap.size());
    Timer timer = new Timer();

    try {
      SSDMessage message = this.ssdService.contReleaseWithfeaValSelection(feaValMap);
      getLogger().debug(
          "Invoking SSDInterface.contReleaseWithfeaValSelection() completed. SSD Message = {}, time taken = {}",
          message, timer.finish());

      return message;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * @param path path
   * @return path of .ssd and .cdfx file
   * @throws SsdInterfaceException ssd interface error
   * @deprecated comp package module is obsolete now
   */
  @Deprecated
  public String[] getReleaseReports(final String path) throws SsdInterfaceException {
    getLogger().debug("Invoking SSDInterface.getReleaseReports() to output path={}", path);
    Timer timer = new Timer();

    try {
      String[] retArr = this.ssdService.getReleaseReports(path, false);
      getLogger().debug("Invoking SSDInterface.getReleaseReports() completed. Time taken = {}", timer.finish());
      return retArr;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * Get the release errros from the previous release step
   *
   * @return Map containing SSD release Error messages and label list
   * @throws SsdInterfaceException ssd interface error
   */
  // iCDM-991
  public Map<String, SortedSet<String>> getReleaseErrors() throws SsdInterfaceException {
    getLogger().debug("Invoking SSDInterface.getReleaseErrors()");
    Timer timer = new Timer();

    // iCDM-991
    Map<String, SortedSet<String>> releaseErrMap = new ConcurrentHashMap<>();
    try {
      List<ReleaseErrorModel> releaseErrList = this.ssdService.getReleaseErrors();
      getLogger().debug("Invoking SSDInterface.getReleaseErrors() completed. Time taken = {}", timer.finish());

      if (releaseErrList != null) {
        for (ReleaseErrorModel errModel : releaseErrList) {
          addReleaseErrorToMap(releaseErrMap, errModel);
        }
        getLogger().debug("SSDInterface.getReleaseErrors() - releaseErrors={}", releaseErrList.size());
      }
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }


    return releaseErrMap;
  }

  /**
   * @param releaseErrMap
   * @param errModel
   */
  private void addReleaseErrorToMap(final Map<String, SortedSet<String>> releaseErrMap,
      final ReleaseErrorModel errModel) {
    if (releaseErrMap.get(errModel.getErrorDescription()) == null) {
      SortedSet<String> labelSetNew = new TreeSet<>();
      labelSetNew.add(errModel.getLabel());
      releaseErrMap.put(errModel.getErrorDescription(), labelSetNew);
    }
    else {
      SortedSet<String> labelSet = releaseErrMap.get(errModel.getErrorDescription());
      labelSet.add(errModel.getLabel());
      releaseErrMap.put(errModel.getErrorDescription(), labelSet);
    }
  }

  /**
   * Cancel the prevous SSD release. to be invoked for errors.
   *
   * @throws SsdInterfaceException ssd interface error
   */
  public void cancelRelease() throws SsdInterfaceException {
    getLogger().debug("Invoking SSDInterface.cancelRelease()");
    Timer timer = new Timer();

    try {
      this.ssdService.cancelRelease();
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }

    getLogger().debug("Invoking SSDInterface.cancelRelease() completed, time taken = {}", timer.finish());
  }

  /**
   * @return number of errors
   * @throws SsdInterfaceException ssd interface error exception
   */
  public long getNumberOfReleaseErrors() throws SsdInterfaceException {

    long retRelErrCount = 0;

    try {
      getLogger().debug("Invoking SSDInterface.getNumberOfReleaseErrors()");
      Timer timer = new Timer();

      List<ReleaseErrorModel> releaseErrList = this.ssdService.getReleaseErrors();
      getLogger().debug("Invoking SSDInterface.getNumberOfReleaseErrors() completed, time taken = {}", timer.finish());

      if (releaseErrList != null) {
        retRelErrCount = releaseErrList.size();
        getLogger().debug("SSDInterface.getNumberOfReleaseErrors() -  releaseErrList size = {}", retRelErrCount);
      }
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }

    return retRelErrCount;
  }

  /**
   * to get the number of paramters for which rules were obtained form release
   *
   * @return - no of params
   * @throws SsdInterfaceException ssd interface error
   */
  public long getNoOfParamsInRelease() throws SsdInterfaceException {
    getLogger().debug("Invoking SSDInterface.getNoOfParamsInRelease()");
    Timer timer = new Timer();

    try {
      long count = this.ssdService.getNoOfParamsInRelease();
      getLogger().debug("Invoking SSDInterface.getNoOfParamsInRelease() completed. Count={}, time taken = {}", count,
          timer.finish());

      return count;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * Gets the CalData model for number of params in CDFX file
   *
   * @return map of calmodel
   * @throws SsdInterfaceException ssd interface error
   */
  public Map<String, CalData> getCalDataMapForCdfx() throws SsdInterfaceException {
    getLogger().debug("Invoking SSDInterface.getCalDataMapForCdfx()");
    Timer timer = new Timer();

    try {
      Map<String, CalData> retMap = this.ssdService.getCalDataMapForCdfx();
      getLogger().debug("Invoking SSDInterface.getCalDataMapForCdfx() completed. retMap={}, time taken = {}",
          retMap.size(), timer.finish());

      return retMap;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }


  /**
   * Method to read review rules based on the list of labels supplied
   *
   * @param labelNames - parameter names
   * @param dependencies - feature value combination
   * @return - CDR rules
   * @throws SsdInterfaceException ssd interface error
   */
  public Map<String, List<CDRRule>> readReviewRuleForDependency(final List<String> labelNames,
      final List<FeatureValueModel> dependencies)
      throws SsdInterfaceException {

    getLogger().debug("Invoking SSDInterface.readReviewRuleForDependency() for labels={}, dependencies={}",
        labelNames.size(), dependencies.size());
    Timer timer = new Timer();
    try {
      Map<String, List<CDRRule>> retMap = this.ssdService.readReviewRuleForDependency(labelNames, dependencies);
      getLogger().debug("Invoking SSDInterface.readReviewRuleForDependency() completed. retMap={}, time taken = {}",
          retMap.size(), timer.finish());

      return retMap;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * @param labelNames label names
   * @param dependencies dependencies
   * @param filePath path to save output files
   * @return the rules and the SSD File path
   * @throws SsdInterfaceException ssd interface error
   */
  public CDRRulesWithFile readRulesandGetSSDFileDependency(final List<String> labelNames,
      final List<FeatureValueModel> dependencies, final String filePath)
      throws SsdInterfaceException {

    getLogger().debug(
        "Invoking SSDInterface.readRulesandGetSSDFileDependency() with labels={}, dependencies={}, filepath={}",
        labelNames.size(), dependencies.size(), filePath);
    Timer timer = new Timer();
    try {
      CDRRulesWithFile ret = this.ssdService.readRulesandGetSSDFileDependency(labelNames, dependencies, filePath);

      getLogger().debug(
          "Invoking SSDInterface.readRulesandGetSSDFileDependency() completed. Params with rules={}, time taken = {}",
          ret.getCdrRules().size(), timer.finish());

      return ret;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * to create review rules in SSD based on the information in the model
   *
   * @param model - to be filled
   * @param ssdNodeId - node id on which the rule has to be defined
   * @param ssdVersNodeId - node id on which release has to be created
   * @param isCompPckRule - to be set to true for comppckg rules for labellist creation
   * @return - 0 when rule gets created and populate CDRModel with ruleid == -1 when label is not defined in SSD == -2
   *         when rule validation fails
   * @throws SsdInterfaceException - exception during insert are thrown
   */
  public SSDMessage createSSDRule(final CDRRule model, final Long ssdNodeId, final Long ssdVersNodeId,
      final boolean isCompPckRule)
      throws SsdInterfaceException {

    getLogger().debug("Invoking SSDInterface.createSSDRule() with CDRRule, node={}, ssdVersnodeId={}, isCompPckRule={}",
        ssdNodeId, ssdVersNodeId, isCompPckRule);
    Timer timer = new Timer();

    try {
      SSDMessage message = this.ssdService.createSSDRule(model, longToBigDecimal(ssdNodeId),
          longToBigDecimal(ssdVersNodeId), isCompPckRule);
      getLogger().debug("Invoking SSDInterface.createSSDRule() completed. SSD Message = {}, time taken = {}", message,
          timer.finish());

      return message;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * to update an exiting review rule
   *
   * @param model - model to be filled with data for update
   * @param ssdNodeId - node id on which the rule has to be updated
   * @return 0 on success
   * @throws SsdInterfaceException exception during update
   */
  public SSDMessage updateSSDRule(final CDRRule model, final Long ssdNodeId) throws SsdInterfaceException {
    getLogger().debug("Invoking SSDInterface.updateSSDRule() with CDRRule, node={}", ssdNodeId);
    Timer timer = new Timer();

    try {
      SSDMessage message = this.ssdService.updateSSDRule(model, longToBigDecimal(ssdNodeId));
      getLogger().debug("Invoking SSDInterface.updateSSDRule() completed. SSD Message = {}, time taken = {}", message,
          timer.finish());

      return message;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * Method to read review rules from database for a single label
   *
   * @param functionName - name of the funcion
   * @param functionVersion - version if specific, or null to select all versions
   * @param ssdNodeId - node id on which the rule has to be defined
   * @return - populated model
   * @throws SsdInterfaceException ssd interface error
   */
  public Map<String, List<CDRRule>> readSSDRule(final String functionName, final String functionVersion,
      final Long ssdNodeId)
      throws SsdInterfaceException {

    getLogger().debug("Invoking SSDInterface.readSSDRule() with function={}, version={}, node={}", functionName,
        functionVersion, ssdNodeId);
    Timer timer = new Timer();

    try {
      Map<String, List<CDRRule>> retMap =
          this.ssdService.readSSDRule(functionName, functionVersion, longToBigDecimal(ssdNodeId));
      getLogger().debug("Invoking SSDInterface.readSSDRule() completed. retSize = {}, time taken = {}", retMap.size(),
          timer.finish());

      return retMap;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * to read the history of review rule for one parameter
   *
   * @param rule - rule
   * @param ssdNodeId - node id on which the rule has to be defined
   * @return - CDR rules with history
   * @throws SsdInterfaceException ssd interface error
   */
  public List<CDRRuleExt> getRuleHistoryForNode(final CDRRule rule, final Long ssdNodeId) throws SsdInterfaceException {
    getLogger().debug("Invoking SSDInterface.getRuleHistoryForNode() for CDRRule model, node={}", ssdNodeId);
    Timer timer = new Timer();

    try {
      List<CDRRuleExt> retList = this.ssdService.getRuleHistoryForNode(rule, longToBigDecimal(ssdNodeId));
      getLogger().debug("Invoking SSDInterface.getRuleHistoryForNode() completed. retSize = {}, time taken = {}",
          retList.size(), timer.finish());

      return retList;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * to read SSD rules for the one parameter
   *
   * @param labelName - parameter name
   * @param nodeId - node id on which the rule has to be defined
   * @return - list of cdr rules
   * @throws SsdInterfaceException ssd interface error
   */
  public List<CDRRule> readSSDRules(final String labelName, final Long nodeId) throws SsdInterfaceException {
    getLogger().debug("Invoking SSDInterface.readSSDRules() for label={}, node={}", labelName, nodeId);
    Timer timer = new Timer();

    try {
      List<CDRRule> retList = this.ssdService.readSSDRules(labelName, longToBigDecimal(nodeId));
      getLogger().debug("Invoking SSDInterface.readSSDRules() completed. retSize = {}, time taken = {}", retList.size(),
          timer.finish());

      return retList;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  // ICDM-1476
  /**
   * To read rules defined on a node
   *
   * @param nodeId - SSD Node
   * @return - list of rules
   * @throws SsdInterfaceException ssd interface error
   */
  public Map<String, List<CDRRule>> readSSDRulesFromNode(final Long nodeId) throws SsdInterfaceException {
    getLogger().debug("Invoking SSDInterface.readSSDRulesFromNode() for node={}", nodeId);
    Timer timer = new Timer();

    try {
      Map<String, List<CDRRule>> retMap = this.ssdService.readSSDRulesFromNode(longToBigDecimal(nodeId));
      getLogger().debug("Invoking SSDInterface.readSSDRulesFromNode() completed. retSize = {}, time taken = {}",
          retMap.size(), timer.finish());

      return retMap;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * Method to read review rules based on the list of labels supplied
   *
   * @param labelNames - paramters list
   * @param ssdNodeId - node id on which the rule has to be defined
   * @return - CDR rules
   * @throws SsdInterfaceException ssd interface error
   */
  public Map<String, List<CDRRule>> readSSDRule(final List<String> labelNames, final Long ssdNodeId)
      throws SsdInterfaceException {
    getLogger().debug("Invoking SSDInterface.readSSDRule() for labels={}, node={}", labelNames.size(), ssdNodeId);
    Timer timer = new Timer();

    try {
      Map<String, List<CDRRule>> retMap = this.ssdService.readSSDRule(labelNames, longToBigDecimal(ssdNodeId));
      getLogger().debug("Invoking SSDInterface.readSSDRule() completed. retSize = {}, time taken = {}", retMap.size(),
          timer.finish());

      return retMap;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * Method to read review rules based on the list of labels supplied
   *
   * @param labelNames - parameter names
   * @param dependencies - feature value combination
   * @param ssdNodeId - node id on which the rule has to be defined
   * @return - CDR rules
   * @throws SsdInterfaceException ssd interface error
   */
  public Map<String, List<CDRRule>> readSSDRuleForDependency(final List<String> labelNames,
      final List<FeatureValueModel> dependencies, final Long ssdNodeId)
      throws SsdInterfaceException {

    getLogger().debug("Invoking SSDInterface.readSSDRuleForDependency() for labels={}, dependencies={}, node={}",
        labelNames.size(), dependencies.size(), ssdNodeId);
    Timer timer = new Timer();

    try {
      Map<String, List<CDRRule>> retMap =
          this.ssdService.readSSDRuleForDependency(labelNames, dependencies, longToBigDecimal(ssdNodeId));
      getLogger().debug("Invoking SSDInterface.readSSDRuleForDependency() completed. retSize = {}, time taken = {}",
          retMap.size(), timer.finish());

      return retMap;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * to create more than one review rule at the same time
   *
   * @param cdrRules - list of rules to be created
   * @return - {@link SSDMessage}
   * @throws SsdInterfaceException exception during creation
   */
  public SSDMessage createMultReviewRules(final List<CDRRule> cdrRules) throws SsdInterfaceException {
    getLogger().debug("Invoking SSDInterface.createMultReviewRules() for cdrRules={}", cdrRules.size());
    Timer timer = new Timer();

    try {
      SSDMessage message = this.ssdService.createMultReviewRules(cdrRules);
      getLogger().debug("Invoking SSDInterface.createMultReviewRules() completed. SSD Message = {}, time taken = {}",
          message, timer.finish());

      return message;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }

  }


  /**
   * to create more than one review rule at the same time
   *
   * @param cdrRules - list of rules to be created
   * @param ssdNodeId - RuleSet ssdNodeId
   * @param rsID - Rule Set id
   * @return SSDMessage
   * @throws SsdInterfaceException exception during creation
   */
  public SSDMessage createMultRuleSetRules(final List<CDRRule> cdrRules, final Long ssdNodeId, final Long rsID)
      throws SsdInterfaceException {

    return createMultSSDRules(cdrRules, ssdNodeId, rsID, null, false);
  }

  /**
   * to create more than one review rule at the same time
   *
   * @param cdrRules - list of rules to be created
   * @param ssdNodeId - RuleSet ssdNodeId
   * @param rsID - Rule Set id
   * @return - {@link SSDMessage}
   * @throws SsdInterfaceException exception during creation
   */
  public SSDMessage updateMultRuleSetRules(final List<CDRRule> cdrRules, final Long ssdNodeId, final Long rsID)
      throws SsdInterfaceException {

    return updateMultSSDRules(cdrRules, ssdNodeId, rsID);
  }


  /**
   * to create more than one review rule at the same time
   *
   * @param cdrRules - list of rules to be created
   * @param ssdNodeId - node id on which rules have to be defined
   * @param icdmNodeID ID of iCDM node
   * @param ssdVersNodeId - node id on which label list has to be created - sw vers id
   * @param isCompPckRule - to be set to true for comppckg rules for labellist creation
   * @return - {@link SSDMessage}
   * @throws SsdInterfaceException exception
   */
  public SSDMessage createMultSSDRules(final List<CDRRule> cdrRules, final Long ssdNodeId, final Long icdmNodeID,
      final Long ssdVersNodeId, final boolean isCompPckRule)
      throws SsdInterfaceException {

    getLogger().debug(
        "Invoking SSDInterface.createMultSSDRules() for cdrRules={}, ssdNodeId={}, icdmNodeID={}, isCompPckRule={}",
        cdrRules.size(), ssdNodeId, icdmNodeID, isCompPckRule);
    Timer timer = new Timer();

    try {
      SSDMessage message = this.ssdService.createMultSSDRules(cdrRules, longToBigDecimal(ssdNodeId),
          longToBigDecimal(ssdVersNodeId), isCompPckRule);
      getLogger().debug("Invoking SSDInterface.createMultSSDRules() completed. SSD Message = {}, time taken = {}",
          message, timer.finish());

      return message;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * to update more than one review rule at the same time
   *
   * @param cdrRules - list of rules to be created
   * @return - {@link SSDMessage}
   * @throws SsdInterfaceException exception
   */
  public SSDMessage updateMultReviewRules(final List<CDRRule> cdrRules) throws SsdInterfaceException {
    getLogger().debug("Invoking SSDInterface.updateMultReviewRules() for cdrRules={}", cdrRules.size());
    Timer timer = new Timer();

    try {
      SSDMessage message = this.ssdService.updateMultReviewRules(cdrRules);
      getLogger().debug("Invoking SSDInterface.updateMultReviewRules() completed. SSD Message = {}, time taken = {}",
          message, timer.finish());

      return message;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * to update more than one review rule at the same time
   *
   * @param cdrRules - list of rules to be created
   * @param ssdNodeId - ssd node id on which the rule has to be updated
   * @param icdmNodeID ID of iCDM node
   * @return - {@link SSDMessage}
   * @throws SsdInterfaceException exception
   */
  public SSDMessage updateMultSSDRules(final List<CDRRule> cdrRules, final Long ssdNodeId, final Long icdmNodeID)
      throws SsdInterfaceException {

    getLogger().debug("Invoking SSDInterface.updateMultSSDRules() for cdrRules={}, ssdNodeId={}, icdmNodeID={}",
        cdrRules.size(), ssdNodeId, icdmNodeID);
    Timer timer = new Timer();

    try {
      SSDMessage message = this.ssdService.updateMultSSDRules(cdrRules, longToBigDecimal(ssdNodeId));
      getLogger().debug("Invoking SSDInterface.updateMultSSDRules() completed. SSD Message = {}, time taken = {}",
          message, timer.finish());

      return message;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * to delete the rules
   *
   * @param rule - rule to be deleted
   * @return - message
   * @throws SsdInterfaceException exception
   */
  public SSDMessage deleteReviewRule(final CDRRule rule) throws SsdInterfaceException {
    getLogger().debug("Invoking SSDInterface.deleteReviewRule() for CDRRule");
    Timer timer = new Timer();

    try {
      SSDMessage message = this.ssdService.deleteReviewRule(rule);
      getLogger().debug("Invoking SSDInterface.deleteReviewRule() completed. SSD Message = {}, time taken = {}",
          message, timer.finish());

      return message;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * to delete the rules
   *
   * @param cdrRules - list of rules to be deleted
   * @return - message
   * @throws SsdInterfaceException exception
   */
  public SSDMessage deleteMultiReviewRule(final List<CDRRule> cdrRules) throws SsdInterfaceException {
    getLogger().debug("Invoking SSDInterface.deleteMultiReviewRule() on cdrRules={}", cdrRules.size());
    Timer timer = new Timer();

    try {
      SSDMessage message = this.ssdService.deleteMultiReviewRule(cdrRules);
      getLogger().debug("Invoking SSDInterface.deleteMultiReviewRule() completed. SSD Message = {}, time taken = {}",
          message, timer.finish());

      return message;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * to delete the rules
   *
   * @param rsId rule set id
   * @param cdrRules - list of rules to be deleted
   * @return - message
   * @throws SsdInterfaceException exception
   */
  public SSDMessage deleteMultiRuleSetRule(final Long rsId, final List<CDRRule> cdrRules) throws SsdInterfaceException {
    getLogger().debug("Invoking SSDInterface.deleteMultiRuleSetRule() on rsId={}, cdrRules={}", rsId, cdrRules.size());
    Timer timer = new Timer();

    try {
      SSDMessage message = this.ssdService.deleteMultiReviewRule(cdrRules);
      getLogger().debug("Invoking SSDInterface.deleteMultiRuleSetRule() completed. SSD Message = {}, time taken = {}",
          message, timer.finish());

      return message;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * Method to read review rules based on the list of labels supplied
   *
   * @param labelNames - parameter names
   * @param dependencies - feature value combination
   * @param filePath path to save output files
   * @return path of the .ssd file temp folder
   * @throws SsdInterfaceException ssd interface error
   */
  public String getSSDFileForDependency(final List<String> labelNames, final List<FeatureValueModel> dependencies,
      final String filePath)
      throws SsdInterfaceException {

    getLogger().debug("Invoking SSDInterface.getSSDFileForDependency() for labels={}, dependencies={}, path={}",
        labelNames.size(), dependencies.size(), filePath);
    Timer timer = new Timer();

    try {
      String ssdFile = this.ssdService.getSSDFileForDependency(labelNames, dependencies, filePath);
      getLogger().debug("Invoking SSDInterface.getSSDFileForDependency() completed. ssdFile = {}, time taken = {}",
          ssdFile, timer.finish());

      return ssdFile;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * Method to read review rules based on the list of labels supplied
   *
   * @param labelNames - parameter names
   * @param dependencies - feature value combination
   * @param ssdNodeId - ssd node id
   * @param filePath path to save output files
   * @return - path of the .ssd file temp folder
   * @throws SsdInterfaceException ssd interface error
   */
  public String getSSDFileForDependencyForNode(final List<String> labelNames,
      final List<FeatureValueModel> dependencies, final Long ssdNodeId, final String filePath)
      throws SsdInterfaceException {

    getLogger().debug(
        "Invoking SSDInterface.getSSDFileForDependencyForNode() for labels={}, dependencies={}, ssdNodeId={}, path={}",
        labelNames.size(), dependencies.size(), ssdNodeId, filePath);
    Timer timer = new Timer();

    try {
      String ssdFile = this.ssdService.getSSDFileForDependencyForNode(labelNames, dependencies,
          longToBigDecimal(ssdNodeId), filePath);

      getLogger().debug(
          "Invoking SSDInterface.getSSDFileForDependencyForNode() completed. ssdFile = {}, time taken = {}", ssdFile,
          timer.finish());

      return ssdFile;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * Method to populate ssd rules in cdr model and create ssd file containing rules for the labels given
   *
   * @param labelNames - list of labels from Icdm
   * @param dependencies - fea-value
   * @param ssdNodeId - node id on which the rule has to be defined
   * @param filePath path to save output files
   * @return path of the .ssd file
   * @throws SsdInterfaceException ssd interface error
   */
  public CDRRulesWithFile readRulesandGetSSDFileDpndyForNode(final List<String> labelNames,
      final List<FeatureValueModel> dependencies, final Long ssdNodeId, final String filePath)
      throws SsdInterfaceException {

    getLogger().debug(
        "Invoking SSDInterface.readRulesandGetSSDFileDpndyForNode() for labels={}, dependencies={}, ssdNodeId={}, path={}",
        labelNames.size(), dependencies.size(), ssdNodeId, filePath);
    Timer timer = new Timer();

    try {
      CDRRulesWithFile ret = this.ssdService.readRulesandGetSSDFileDpndyForNode(labelNames, dependencies,
          longToBigDecimal(ssdNodeId), filePath);

      getLogger().debug(
          "Invoking SSDInterface.readRulesandGetSSDFileDpndyForNode() completed. Params with rules={}, time taken = {}",
          ret.getCdrRules().size(), timer.finish());

      return ret;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * Method to populate ssd rules in cdr model and create ssd file containing rules for the labels given - for Rulset
   * Rules
   *
   * @param labelNames - list of labels from Icdm
   * @param dependencies - fea-value
   * @param ssdNodeId - node id on which the rule has to be defined
   * @param filePath path to save output files
   * @param isCallFromReview Is this method invoked from Review - (Rule Sets rules/PreCal)
   * @return path of the .ssd file
   * @throws SsdInterfaceException ssd interface error
   */
  public CDRRulesWithFile readRulesandGetSSDFileDpndyForNode(final List<String> labelNames,
      final List<FeatureValueModel> dependencies, final Long ssdNodeId, final String filePath,
      final boolean isCallFromReview)
      throws SsdInterfaceException {

    getLogger().debug(
        "Invoking SSDInterface.readRulesandGetSSDFileDpndyForNode() with labels={}, dependencies={}, node={}, callFromReview={}, filepath={}",
        labelNames.size(), dependencies.size(), ssdNodeId, isCallFromReview, filePath);
    Timer timer = new Timer();

    try {
      CDRRulesWithFile ret = this.ssdService.readRulesandGetSSDFileDpndyForNode(labelNames, dependencies,
          longToBigDecimal(ssdNodeId), filePath);

      getLogger().debug(
          "Invoking SSDInterface.readRulesandGetSSDFileDpndyForNode() completed. Params with rules={}, time taken = {}",
          ret.getCdrRules().size(), timer.finish());

      return ret;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * Method to convert long to bigdecimal
   *
   * @param longVal input long value
   * @return BigDecimal
   */
  private BigDecimal longToBigDecimal(final Long longVal) {
    if (null != longVal) {
      return BigDecimal.valueOf(longVal);
    }
    return null;
  }

  /**
   * ICDM-2097
   *
   * @param cdrRules List of CDRRule
   * @param ssdNodeId Long
   * @param icdmNodeID Long
   * @param ssdVersNodeId Long
   * @param isCompPckRule boolean
   * @return Map<CDRRule, SSDMessage> map of invalid rules
   * @throws SsdInterfaceException Exception
   */
  public Map<CDRRule, SSDMessage> createValidMultRuleSetRules(final List<CDRRule> cdrRules, final Long ssdNodeId,
      final Long icdmNodeID, final Long ssdVersNodeId, final boolean isCompPckRule)
      throws SsdInterfaceException {

    getLogger().debug(
        "Invoking SSDInterface.createValidMultRuleSetRules() with cdrRules={}, ssdNodeId={}, icdmNodeID={}, ssdVersNodeId={}, isCompPckRule={}",
        cdrRules.size(), ssdNodeId, icdmNodeID, ssdVersNodeId, isCompPckRule);
    Timer timer = new Timer();

    try {
      Map<CDRRule, SSDMessage> invalidRulesMap = this.ssdService.createValidMultSSDRules(cdrRules,
          longToBigDecimal(ssdNodeId), longToBigDecimal(ssdVersNodeId), isCompPckRule);

      getLogger().debug(
          "Invoking SSDInterface.createValidMultRuleSetRules() completed. Invalid rules={}, time taken = {}",
          invalidRulesMap.size(), timer.finish());

      return invalidRulesMap;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }


  /**
   * ICDM-2097
   *
   * @param cdrRules List<CDRRule>
   * @param ssdNodeId Long
   * @param ssdVersNodeId Long
   * @param isCompPckRule boolean
   * @return Map<CDRRule, SSDMessage> map of invalid rules
   * @throws SsdInterfaceException Exception
   */
  public Map<CDRRule, SSDMessage> createValidMultFuncRules(final List<CDRRule> cdrRules, final Long ssdNodeId,
      final Long ssdVersNodeId, final boolean isCompPckRule)
      throws SsdInterfaceException {

    getLogger().debug(
        "Invoking SSDInterface.createValidMultFuncRules() with cdrRules={}, ssdNodeId={}, ssdVersNodeId={}, isCompPckRule={}",
        cdrRules.size(), ssdNodeId, ssdVersNodeId, isCompPckRule);
    Timer timer = new Timer();

    try {
      Map<CDRRule, SSDMessage> invalidRulesMap = this.ssdService.createValidMultSSDRules(cdrRules,
          longToBigDecimal(ssdNodeId), longToBigDecimal(ssdVersNodeId), isCompPckRule);
      getLogger().debug("Invoking SSDInterface.createValidMultFuncRules() completed. Invalid rules={}, time taken = {}",
          invalidRulesMap.size(), timer.finish());

      return invalidRulesMap;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * @param cdrRules List<CDRRule>
   * @param ssdNodeId Long
   * @param icdmNodeID Long
   * @param isCompPkg boolean
   * @return Map<CDRRule, SSDMessage> map of invalid rules
   * @throws SsdInterfaceException Exception
   */
  public Map<CDRRule, SSDMessage> updateValidMultSSDRules(final List<CDRRule> cdrRules, final Long ssdNodeId,
      final Long icdmNodeID, final boolean isCompPkg)
      throws SsdInterfaceException {

    getLogger().debug(
        "Invoking SSDInterface.updateValidMultSSDRules() with cdrRules={}, ssdNodeId={}, icdmNodeID={}, isCompPkg={}",
        cdrRules.size(), ssdNodeId, icdmNodeID, isCompPkg);
    Timer timer = new Timer();

    try {
      Map<CDRRule, SSDMessage> invalidRulesMap =
          this.ssdService.updateValidMultSSDRules(cdrRules, longToBigDecimal(ssdNodeId));
      getLogger().debug("Invoking SSDInterface.updateValidMultSSDRules() completed. Invalid rules={}, time taken = {}",
          invalidRulesMap.size(), timer.finish());

      return invalidRulesMap;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }


  /**
   * @param cdrRules List<CDRRule>
   * @param ssdNodeId Long
   * @return Map<CDRRule, SSDMessage> map of invalid rules
   * @throws SsdInterfaceException Exception
   */
  public Map<CDRRule, SSDMessage> updateValidMultFuncRules(final List<CDRRule> cdrRules, final Long ssdNodeId)
      throws SsdInterfaceException {

    getLogger().debug("Invoking SSDInterface.updateValidMultFuncRules() with cdrRules={}, ssdNodeId={}",
        cdrRules.size(), ssdNodeId);
    Timer timer = new Timer();

    try {
      Map<CDRRule, SSDMessage> invalidRulesMap =
          this.ssdService.updateValidMultSSDRules(cdrRules, longToBigDecimal(ssdNodeId));
      getLogger().debug("Invoking SSDInterface.updateValidMultFuncRules() completed. Invalid rules={}, time taken = {}",
          invalidRulesMap.size(), timer.finish());

      return invalidRulesMap;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * @param cdrRules List<CDRRule>
   * @param ssdNodeId Long
   * @param icdmNodeID Long
   * @return Map<CDRRule, SSDMessage> map of invalid rules
   * @throws SsdInterfaceException Exception
   */
  public Map<CDRRule, SSDMessage> updateValidMultRuleSetRules(final List<CDRRule> cdrRules, final Long ssdNodeId,
      final Long icdmNodeID)
      throws SsdInterfaceException {

    getLogger().debug(
        "Invoking SSDInterface.updateValidMultRuleSetRules() with cdrRules={}, ssdNodeId={}, icdmNodeID={}",
        cdrRules.size(), ssdNodeId, icdmNodeID);
    Timer timer = new Timer();

    try {
      Map<CDRRule, SSDMessage> invalidRulesMap =
          this.ssdService.updateValidMultSSDRules(cdrRules, longToBigDecimal(ssdNodeId));
      getLogger().debug(
          "Invoking SSDInterface.updateValidMultRuleSetRules() completed. Invalid rules={}, time taken = {}",
          invalidRulesMap.size(), timer.finish());

      return invalidRulesMap;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * ICDM-2440
   *
   * @param labelList list of lables
   * @param compPkgBCs list of BCs
   * @param isOnlyQSSDRelease TRUE when only QSSD Parameters is sent for release, else FALSE
   * @param isNonSDOMBCRelease
   * @return SSDMessage
   * @throws SsdInterfaceException Exception
   */
  public SSDMessageOptions invokeComplianceRelease(final Map<String, String> labelList,
      final Set<ComPkgBcModel> compPkgBCs, final boolean isOnlyQSSDRelease, final boolean isNonSDOMBCRelease)
      throws SsdInterfaceException {

    getLogger().debug(
        "Invoking SSDInterface.invokeComplianceRelease() with labelList={}, compPkgBCs={}, isOnlyQSSDRelease={}",
        labelList.size(), compPkgBCs.size(), isOnlyQSSDRelease);
    Timer timer = new Timer();

    try {
      SSDMessageOptions ssdMsgOpt;
      if (isNonSDOMBCRelease) {
        ssdMsgOpt = this.ssdService.invokeNonSDOMComplianceRelease(labelList, isOnlyQSSDRelease);
        getLogger().debug("Invoking SSDInterface.invokeNonSDOMComplianceRelease() completed, time taken = {}",
            timer.finish());
      }
      else {
        ssdMsgOpt = this.ssdService.invokeComplianceRelease(labelList, compPkgBCs, isOnlyQSSDRelease);
        getLogger().debug("Invoking SSDInterface.invokeComplianceRelease() completed, time taken = {}", timer.finish());
      }

      if ((ssdMsgOpt.getNoNodeBcList() != null) && !ssdMsgOpt.getNoNodeBcList().isEmpty()) {
        getLogger()
            .error("Missing BC nodes : " + ssdMsgOpt.getNoNodeBcList().stream().collect(Collectors.joining(", ")));
      }

      return ssdMsgOpt;
    }
    catch (SSDiCDMInterfaceException exp) {
      getLogger().error(exp.getMessage(), exp);
      throw createSsdInterfaceException(exp);
    }
  }


  /**
   * @return list of rules from compliance release
   * @throws SsdInterfaceException ssd interface error
   */
  // ICDM-2440
  public List<CDRRule> readRuleForCompliRelease() throws SsdInterfaceException {
    try {
      return this.ssdService.readRuleForCompliRelease();
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * @param path file path
   * @throws SsdInterfaceException interface error
   */
  // ICDM-2440
  public void getReleaseReportsForCompli(final String path) throws SsdInterfaceException {
    getLogger().debug("Invoking SSDInterface.getReleaseReportsForCompli() with path={}", path);
    Timer timer = new Timer();

    try {
      this.ssdService.getReleaseReportsForCompli(path, false);
      getLogger().debug("Invoking SSDInterface.getReleaseReportsForCompli() completed, time taken = {}",
          timer.finish());
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * @param name param name
   * @return param value
   * @throws SsdInterfaceException interface error
   */
  public String getConfigValue(final SSDConfigParams name) throws SsdInterfaceException {
    getLogger().debug("Invoking SSDInterface.getConfigValue() with SSDConfigParams={}", name);
    Timer timer = new Timer();

    try {
      String configValue = this.ssdService.getConfigValue(name);
      getLogger().debug("Invoking SSDInterface.getConfigValue() completed. configValue={}, time taken = {}",
          configValue, timer.finish());

      return configValue;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }

  }

  /**
   * @param swProjNodeId swProjNodeId
   * @return the software Project List
   * @throws SsdInterfaceException Icdm ecception
   */
  public SortedSet<SSDProjectVersion> getSwVersionListBySwProjId(final long swProjNodeId) throws SsdInterfaceException {
    SortedSet<SSDProjectVersion> softwareVersionList = new TreeSet<>();

    getLogger().debug("Invoking SSDInterface.getSwVersionListBySwProjId() for swProjNodeId={}", swProjNodeId);
    Timer timer = new Timer();

    try {
      List<SoftwareVersion> swVersionListBySwProjId = this.ssdService.getSwVersionListBySwProjId(swProjNodeId);
      getLogger().debug("Invoking SSDInterface.getSwVersionListBySwProjId() completed, time taken = {}",
          timer.finish());

      if (swVersionListBySwProjId == null) {
        return softwareVersionList;
      }
      for (SoftwareVersion softwareVersion : swVersionListBySwProjId) {
        SSDProjectVersion version = new SSDProjectVersion();
        version.setProjectId(softwareVersion.getSwProjNodeId().longValue());
        version.setVersionDesc(softwareVersion.getSwVersionDesc());
        version.setVersionId(softwareVersion.getSwVersId().longValue());
        version.setVersionName(softwareVersion.getSwVersionName());
        version.setVersionNumber(softwareVersion.getSwVersionNumber());
        softwareVersionList.add(version);
      }
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
    return softwareVersionList;
  }

  /**
   * @param ssdSoftwareVersionID ssdSoftwareVersionID
   * @return the release as per version id
   * @throws SsdInterfaceException interface error
   */
  public List<SSDRelease> getSSDRelesesBySwVersionId(final Long ssdSoftwareVersionID) throws SsdInterfaceException {
    getLogger().debug("Invoking SSDInterface.getSSDRelesesBySwVersionId() for ssdSoftwareVersionID={}",
        ssdSoftwareVersionID);
    Timer timer = new Timer();

    try {
      List<SSDRelease> retList = this.ssdService.getSSDRelesesBySwVersionId(ssdSoftwareVersionID);
      getLogger().debug("Invoking SSDInterface.getSSDRelesesBySwVersionId() completed. retList={}, time taken = {}",
          retList.size(), timer.finish());

      return retList;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * @param releaseId ssd release id
   * @return the release id.
   * @throws SsdInterfaceException releaseId get the rules based on release id.
   */
  public List<CDRRule> readRuleForRelease(final BigDecimal releaseId) throws SsdInterfaceException {
    getLogger().debug("Invoking SSDInterface.readRuleForRelease() for releaseId={}", releaseId);
    Timer timer = new Timer();

    try {
      List<CDRRule> retList = this.ssdService.readRuleForRelease(releaseId);
      getLogger().debug("Invoking SSDInterface.readRuleForRelease() completed. retList={}, time taken = {}",
          retList.size(), timer.finish());

      return retList;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * @param path release report path
   * @param releaseId ssd release id
   * @return release report path
   * @throws SsdInterfaceException interface error
   */
  public String getReleaseReportsByReleaseId(final String path, final BigDecimal releaseId)
      throws SsdInterfaceException {

    getLogger().debug("Invoking SSDInterface.getReleaseReportsByReleaseId(). path={}, releaseId={}", path, releaseId);
    Timer timer = new Timer();

    try {
      String reportPath = this.ssdService.getReleaseReportsByReleaseId(path, releaseId, false);
      getLogger().debug(
          "Invoking SSDInterface.getReleaseReportsByReleaseId() completed. reportPath={}, time taken = {}", reportPath,
          timer.finish());

      return reportPath;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  /**
   * @param ruleIdSet set of rule ids
   * @return map of rule description
   * @throws SsdInterfaceException interface error
   */
  public Map<String, RuleIdDescriptionModel> getOEMDetailsForRuleId(final Set<OEMRuleDescriptionInput> ruleIdSet)
      throws SsdInterfaceException {

    getLogger().debug("Invoking SSDInterface.getOEMDetailsForRuleId() for ruleIdSet={}", ruleIdSet.size());
    Timer timer = new Timer();

    try {
      Map<String, RuleIdDescriptionModel> retMap = this.ssdService.getOEMDetailsForRuleId(ruleIdSet);
      getLogger().debug("Invoking SSDInterface.getOEMDetailsForRuleId() completed. retMap={}, time taken = {}",
          retMap.size(), timer.finish());

      return retMap;
    }
    catch (SSDiCDMInterfaceException exp) {
      throw createSsdInterfaceException(exp);
    }
  }

  private SsdInterfaceException createSsdInterfaceException(final SSDiCDMInterfaceException exp) {
    return new SsdInterfaceException(SSD_INTERFACE_ERR_CODE, exp, exp.getMessage());
  }

}
