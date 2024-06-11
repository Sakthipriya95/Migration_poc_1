/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.bosch.ssd.icdm.service.utility.CreateSSDRelease;


/**
 *
 */
public interface SSDInterface {

  /**
   * @return val
   * @throws SSDiCDMInterfaceException Exception
   */
  public BigDecimal getNodeId() throws SSDiCDMInterfaceException;

  /**
   * Method inserts the label in SSD and returns the message
   *
   * @param model - to be filled
   * @return labId- label inserted successfully -1 - label is not inserted
   * @throws SSDiCDMInterfaceException Exception
   */
  public SSDMessage createLabel(CDRRule model) throws SSDiCDMInterfaceException;

  /**
   * Checks if the label is present in SSD or not
   *
   * @param paramName - label from icdm
   * @return - if label present in SSD then labId or return -1
   * @throws SSDiCDMInterfaceException Exception
   */
  public BigDecimal isLabelPresent(String paramName) throws SSDiCDMInterfaceException;


  /**
   * to create review rules in SSD based on the information in the model
   *
   * @param model - to be filled
   * @return - 0 when rule gets created and populate CDRModel with ruleid == -1 when label is not defined in SSD == -2
   *         when rule validation fails * @throws Exception - exception during insert are thrown
   * @throws SSDiCDMInterfaceException E
   */
  public SSDMessage createReviewRule(CDRRule model) throws SSDiCDMInterfaceException;

  /**
   * to create review rules in SSD based on the information in the model
   *
   * @param model - to be filled
   * @param ssdNodeId - node id on which the rule has to be defined
   * @param ssdVersNodeId - node id on which release has to be created
   * @param isCompPckRule - to be set to true for comppckg rules for labellist creation
   * @return - 0 when rule gets created and populate CDRModel with ruleid == -1 when label is not defined in SSD == -2
   *         when rule validation fails * @throws Exception - exception during insert are thrown
   * @throws SSDiCDMInterfaceException -
   */
  public SSDMessage createSSDRule(CDRRule model, BigDecimal ssdNodeId, BigDecimal ssdVersNodeId, boolean isCompPckRule)
      throws SSDiCDMInterfaceException;

  /**
   * to update an exiting review rule
   *
   * @param model - model to be filled with data for update
   * @return 0 on success
   * @throws SSDiCDMInterfaceException exception during update
   */
  public SSDMessage updateReviewRule(CDRRule model) throws SSDiCDMInterfaceException;

  /**
   * to update an exiting review rule
   *
   * @param model - model to be filled with data for update
   * @param ssdNodeId - node id on which the rule has to be updated
   * @return 0 on success
   * @throws SSDiCDMInterfaceException exception during update
   */
  public SSDMessage updateSSDRule(CDRRule model, BigDecimal ssdNodeId) throws SSDiCDMInterfaceException;

  /**
   * Method to read review rules from database for a single label
   *
   * @param functionName - name of the funcion
   * @param functionVersion - version if specific, or null to select all versions
   * @return - populated model
   * @throws SSDiCDMInterfaceException Exception
   */
  public Map<String, List<CDRRule>> readReviewRule(String functionName, String functionVersion)
      throws SSDiCDMInterfaceException;

  /**
   * Method to read review rules from database for a single label
   *
   * @param functionName - name of the funcion
   * @param functionVersion - version if specific, or null to select all versions
   * @param ssdNodeId -
   * @return - populated model
   * @throws SSDiCDMInterfaceException exception
   */
  public Map<String, List<CDRRule>> readSSDRule(String functionName, String functionVersion, BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException;

  /**
   * to read the history of review rule for one parameter
   *
   * @Deprecated
   * @param labelName - parameter name
   * @param labelType - parameter type
   * @return - CDR rules with history
   * @throws SSDiCDMInterfaceException Exception
   */
  @Deprecated
  public List<CDRRuleExt> getRuleHistory(String labelName, String labelType) throws SSDiCDMInterfaceException;

  /**
   * to read the history of review rule for one parameter
   *
   * @Deprecated
   * @param labelName - parameter name
   * @param labelType - parameter type
   * @param ssdNodeId -
   * @return - CDR rules with history
   * @throws SSDiCDMInterfaceException Exception
   */
  @Deprecated
  public List<CDRRuleExt> getRuleHistoryForNode(String labelName, String labelType, BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException;

  /**
   * to read the history of review rule for one parameter
   *
   * @param rule -
   * @return - CDR rules with history
   * @throws SSDiCDMInterfaceException Exception
   */
  public List<CDRRuleExt> getRuleHistory(CDRRule rule) throws SSDiCDMInterfaceException;

  /**
   * to read the history of review rule for one parameter
   *
   * @param rule -
   * @param ssdNodeId -
   * @return - CDR rules with history
   * @throws SSDiCDMInterfaceException Exception
   */
  public List<CDRRuleExt> getRuleHistoryForNode(CDRRule rule, BigDecimal ssdNodeId) throws SSDiCDMInterfaceException;

  /**
   * to read review rule for one parameter
   *
   * @param labelName - parameter name
   * @return - CDR rules
   * @throws SSDiCDMInterfaceException Exception
   */
  public List<CDRRule> readReviewRule(String labelName) throws SSDiCDMInterfaceException;

  /**
   * @param labelName -
   * @param nodeId -
   * @return -
   * @throws SSDiCDMInterfaceException Exception
   */
  public List<CDRRule> readSSDRules(String labelName, BigDecimal nodeId) throws SSDiCDMInterfaceException;


  /**
   * To read rules defined on a node
   *
   * @param nodeId -
   * @return - list of rules
   * @throws SSDiCDMInterfaceException Exception
   */
  public Map<String, List<CDRRule>> readSSDRulesFromNode(BigDecimal nodeId) throws SSDiCDMInterfaceException;

  /**
   * Method to read review rules based on the list of labels supplied
   *
   * @param labelNames - paramters list
   * @return - CDR rules
   * @throws SSDiCDMInterfaceException Exception
   */
  public Map<String, List<CDRRule>> readReviewRule(List<String> labelNames) throws SSDiCDMInterfaceException;

  /**
   * Method to read review rules based on the list of labels supplied
   *
   * @param labelNames - paramters list
   * @param ssdNodeId -
   * @return - CDR rules
   * @throws SSDiCDMInterfaceException Exception
   */
  public Map<String, List<CDRRule>> readSSDRule(List<String> labelNames, BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException;

  /**
   * Method to read review rules based on the list of labels supplied
   *
   * @param labelNames - parameter names
   * @param dependencies - feature value combination
   * @return - CDR rules
   * @throws SSDiCDMInterfaceException Exception
   */
  public Map<String, List<CDRRule>> readReviewRuleForDependency(List<String> labelNames,
      List<FeatureValueModel> dependencies)
      throws SSDiCDMInterfaceException;

  /**
   * Method to read review rules based on the list of labels supplied
   *
   * @param labelNames - parameter names
   * @param dependencies - feature value combination
   * @param ssdNodeId -
   * @return - CDR rules
   * @throws SSDiCDMInterfaceException Exception
   */
  public Map<String, List<CDRRule>> readSSDRuleForDependency(List<String> labelNames,
      List<FeatureValueModel> dependencies, BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException;

  /**
   * Method to create ssd file containing rules for the labels given
   *
   * @Deprecated
   * @param labelNames - list of labels from Icdm
   * @return path of the .ssd file
   * @throws SSDiCDMInterfaceException Exception
   */
  @Deprecated
  public String getSSDFile(List<String> labelNames) throws SSDiCDMInterfaceException;

  /**
   * Method to create ssd file containing rules for the labels given
   *
   * @Deprecated
   * @param labelNames - list of labels from Icdm
   * @param ssdNodeId -
   * @return path of the .ssd file
   * @throws SSDiCDMInterfaceException Exception
   */
  @Deprecated
  public String getSSDFileForNode(List<String> labelNames, BigDecimal ssdNodeId) throws SSDiCDMInterfaceException;

  /**
   * Method to populate ssd rules in cdr model and create ssd file containing rules for the labels given
   *
   * @Deprecated
   * @param labelNames - list of labels from Icdm
   * @return path of the .ssd file
   * @throws SSDiCDMInterfaceException Exception
   */
  @Deprecated
  public CDRRulesWithFile readRulesandGetSSDFile(List<String> labelNames) throws SSDiCDMInterfaceException;

  /**
   * Method to populate ssd rules in cdr model and create ssd file containing rules for the labels given
   *
   * @Deprecated
   * @param labelNames - list of labels from Icdm
   * @param ssdNodeId - node id from which rules have to be read
   * @return path of the .ssd file
   * @throws SSDiCDMInterfaceException Exception
   */
  @Deprecated
  public CDRRulesWithFile readRulesandGetSSDFileForNode(List<String> labelNames, BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException;

  /**
   * to create more than one review rule at the same time
   *
   * @param cdrRules - list of rules to be created
   * @return - {@link SSDMessage}
   * @throws SSDiCDMInterfaceException - interuption exception
   */
  public SSDMessage createMultReviewRules(List<CDRRule> cdrRules) throws SSDiCDMInterfaceException;

  /**
   * to create more than one review rule at the same time
   *
   * @Deprecated
   * @param cdrRules - list of rules to be created
   * @param ssdNodeId - node id on which rules have to be defined
   * @param ssdVersNodeId - node id on which label list has to be created - sw vers id
   * @param isCompPckRule - to be set to true for comppckg rules for labellist creation
   * @return - {@link SSDMessage}
   * @throws SSDiCDMInterfaceException - interuption exception
   */

  public SSDMessage createMultSSDRules(List<CDRRule> cdrRules, BigDecimal ssdNodeId, BigDecimal ssdVersNodeId,
      boolean isCompPckRule)
      throws SSDiCDMInterfaceException;

  /**
   * to update more than one review rule at the same time
   *
   * @param cdrRules - list of rules to be created
   * @return - {@link SSDMessage}
   * @throws SSDiCDMInterfaceException - interuption exception
   */
  public SSDMessage updateMultReviewRules(List<CDRRule> cdrRules) throws SSDiCDMInterfaceException;

  /**
   * to update more than one review rule at the same time
   *
   * @Deprecated
   * @param cdrRules - list of rules to be created
   * @param ssdNodeId - ssd node id on which the rule has to be updated
   * @return - {@link SSDMessage}
   * @throws SSDiCDMInterfaceException - interuption exception
   */

  public SSDMessage updateMultSSDRules(List<CDRRule> cdrRules, BigDecimal ssdNodeId) throws SSDiCDMInterfaceException;

  /**
   * to delete the rules
   *
   * @param rule - rule to be deleted
   * @return - message
   * @throws SSDiCDMInterfaceException Exception
   */
  public SSDMessage deleteReviewRule(CDRRule rule) throws SSDiCDMInterfaceException;

  /**
   * to delete the rules
   *
   * @param cdrRules - list of rules to be deleted
   * @return - message
   * @throws SSDiCDMInterfaceException Exception
   */
  public SSDMessage deleteMultiReviewRule(List<CDRRule> cdrRules) throws SSDiCDMInterfaceException;

  /**
   * Method to read review rules based on the list of labels supplied
   *
   * @param labelNames - parameter names
   * @param dependencies - feature value combination
   * @param ssdFilePath - server location in which created .ssd file need to be stored
   * @return - path of the .ssd file temp folder
   * @throws SSDiCDMInterfaceException Exception
   */
  public String getSSDFileForDependency(List<String> labelNames, List<FeatureValueModel> dependencies,
      String ssdFilePath)
      throws SSDiCDMInterfaceException;

  /**
   * Method to read review rules based on the list of labels supplied
   *
   * @param labelNames - parameter names
   * @param dependencies - feature value combination
   * @param ssdNodeId - ssd node id
   * @param ssdFilePath - server location in which created .ssd file need to be stored
   * @return - path of the .ssd file temp folder
   * @throws SSDiCDMInterfaceException Exception
   */
  public String getSSDFileForDependencyForNode(List<String> labelNames, List<FeatureValueModel> dependencies,
      BigDecimal ssdNodeId, String ssdFilePath)
      throws SSDiCDMInterfaceException;

  /**
   * Method to populate ssd rules in cdr model and create ssd file containing rules for the labels given
   *
   * @param labelNames - list of labels from Icdm
   * @param dependencies - fea-value
   * @param ssdFilePath - server location in which created .ssd file need to be stored
   * @return path of the .ssd file
   * @throws SSDiCDMInterfaceException Exception
   */
  public CDRRulesWithFile readRulesandGetSSDFileDependency(List<String> labelNames,
      List<FeatureValueModel> dependencies, String ssdFilePath)
      throws SSDiCDMInterfaceException;

  /**
   * Method to populate ssd rules in cdr model and create ssd file containing rules for the labels given
   *
   * @param labelNames - list of labels from Icdm
   * @param dependencies - fea-value
   * @param ssdNodeId -
   * @param ssdFilePath - server location in which created .ssd file need to be stored
   * @return path of the .ssd file
   * @throws SSDiCDMInterfaceException Exception
   */
  public CDRRulesWithFile readRulesandGetSSDFileDpndyForNode(List<String> labelNames,
      List<FeatureValueModel> dependencies, BigDecimal ssdNodeId, String ssdFilePath)
      throws SSDiCDMInterfaceException;

  /**
   * username for the session to be set
   *
   * @param userName - user connected
   * @throws SSDiCDMInterfaceException Exception
   */
  public void setUserName(String userName) throws SSDiCDMInterfaceException;

  /**
   * nodeid on which the rules are to be populated
   *
   * @param nodeId - to be taken from config tables
   * @throws SSDiCDMInterfaceException Exception
   */
  public void setNodeId(BigDecimal nodeId) throws SSDiCDMInterfaceException;

  /**
   * Method generates the ssd and cdfx file for the release
   *
   * @param path - path of the reports
   * @param ruleIdFlag Flag
   * @return - path of .ssd and .cdfx file
   * @throws SSDiCDMInterfaceException Exception
   */
  public String[] getReleaseReports(String path, boolean ruleIdFlag) throws SSDiCDMInterfaceException;

  /**
   * Method generates the list of errors for the release
   *
   * @return - list of {@link ReleaseErrorModel} - error details
   * @throws SSDiCDMInterfaceException Exception
   */
  public List<ReleaseErrorModel> getReleaseErrors() throws SSDiCDMInterfaceException;

  /**
   * Method to set node id for the component package release generation
   *
   * @param compNodeId - ssd ndoe id
   * @throws SSDiCDMInterfaceException Exception
   */
  public void setCompPkgNodeId(BigDecimal compNodeId) throws SSDiCDMInterfaceException;

  // SSD-399
  /**
   * Method to set node id for the component package release generation
   *
   * @param compliNodeId - ssd ndoe id
   * @throws SSDiCDMInterfaceException Exception
   */
  public void setCompliNodeId(BigDecimal compliNodeId) throws SSDiCDMInterfaceException;

  /**
   * method to invoke release creation process in ssd for component packages
   *
   * @param labelList - label key and unit value
   * @param compPkgBCs - bc info of component package
   * @return - feature value model
   * @throws SSDiCDMInterfaceException - interuption exception
   */
  public SSDMessageOptions invokeCompPckgRelease(Map<String, String> labelList, Set<ComPkgBcModel> compPkgBCs)
      throws SSDiCDMInterfaceException;

  /**
   * error messages from labellist creation
   *
   * @return - errors
   * @throws SSDiCDMInterfaceException Exception
   */
  public List<String> getErrorListFromLabellist() throws SSDiCDMInterfaceException;

  /**
   * to get feature value selected from release
   *
   * @return - feature value from ssd for release
   * @throws SSDiCDMInterfaceException Exception
   */
  public Map<BigDecimal, FeaValModel> getFeaValueForSelection() throws SSDiCDMInterfaceException;

  /**
   * to continue release with user slected featrue avlue model - to be called only when continue release message had
   * been sent form prevoius call
   *
   * @param feaValMap - feature value model populated with selected value form user
   * @return - if release is successfull or not
   * @throws SSDiCDMInterfaceException Exception
   */
  public SSDMessage contReleaseWithfeaValSelection(Map<BigDecimal, FeaValModel> feaValMap)
      throws SSDiCDMInterfaceException;

  /**
   * in case if release has to be cancelled during feature value selection
   *
   * @throws SSDiCDMInterfaceException Exception
   */
  public void cancelRelease() throws SSDiCDMInterfaceException;

  /**
   * to get the number of paramters for which rules were obtained form release
   *
   * @return - no of params
   * @throws SSDiCDMInterfaceException Exception
   */
  public long getNoOfParamsInRelease() throws SSDiCDMInterfaceException;

  /**
   * to get the model populated with values for cdfx generation
   *
   * @return - caldata map
   * @throws SSDiCDMInterfaceException Exception
   */
  public Map<String, CalData> getCalDataMapForCdfx() throws SSDiCDMInterfaceException;

  /**
   * SSD-373 Method used to create multiple review rules and return a map containing invalid rules and their messages,
   *
   * @param cdrRules
   * @param ssdNodeId
   * @param ssdVersNodeId
   * @param isCompPckRule
   * @return Map<CDRRule, SSDMessage> in case of invalid rules or failure during comp package creation
   * @throws SSDiCDMInterfaceException e
   */
  Map<CDRRule, SSDMessage> createValidMultSSDRules(List<CDRRule> cdrRules, BigDecimal ssdNodeId,
      BigDecimal ssdVersNodeId, boolean isCompPckRule)
      throws SSDiCDMInterfaceException;

  /**
   * SSD-373 Method used to update multiple review rules and return a map containing invalid rules and their messages
   * respectively,
   *
   * @param cdrRules
   * @param ssdNodeId
   * @return Map
   * @throws SSDiCDMInterfaceException e
   */
  Map<CDRRule, SSDMessage> updateValidMultSSDRules(List<CDRRule> cdrRules, BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException;

  /**
   * SSD-399 method to invoke release creation process in ssd for COMPLIANCE labels
   *
   * @param labelList - label key and unit value
   * @param compPkgBCs - bc info of component package
   * @param isQSSDOnlyParameters TRUE When labellist contains only QSSD Parameters, else FALSE
   * @return - feature value model
   * @throws SSDiCDMInterfaceException - interuption exception ALM-294077 return type changed from SSDMessage to
   *           SSDMessageOptions
   */
  public SSDMessageOptions invokeComplianceRelease(Map<String, String> labelList, Set<ComPkgBcModel> compPkgBCs,
      final boolean isQSSDOnlyParameters)
      throws SSDiCDMInterfaceException;

  /**
   * method to invoke release creation process in ssd for COMPLIANCE labels of Non SDOM SWs
   * 
   * @param labelList - label key and unit value
   * @param isQSSDOnlyParameters TRUE When labellist contains only QSSD Parameters, else FALSE
   * @return - feature value model
   * @throws SSDiCDMInterfaceException - interuption exception ALM-294077 return type changed from SSDMessage to
   *           SSDMessageOptions
   
  public SSDMessageOptions invokeNonSDOMComplianceRelease(final Map<String, String> labelList,
      final boolean isQSSDOnlyParameters)
      throws SSDiCDMInterfaceException;
*/
  /**
   * SSD-399 method to read the rules for Compliance release and assign them to CDR model; ALM-526562 - Retrieve
   * Compliance Use case rules (CSSD, SSD2RV) and also QSSD case rules QSSD Rules are always mandatory for any SSD
   * Release
   *
   * @return list
   * @throws SSDiCDMInterfaceException Exception
   */
  public List<CDRRule> readRuleForCompliRelease() throws SSDiCDMInterfaceException;

  /**
   * SSD-399 Method to get the release reports of Compliance
   *
   * @param path
   * @param ruleIdFlag
   * @return val
   * @throws SSDiCDMInterfaceException
   */
  String getReleaseReportsForCompli(String path, boolean ruleIdFlag) throws SSDiCDMInterfaceException;

  /**
   * //SSD V3.2.1 Method to get the rule history for Compliance rule //ALM-526562 Updated method name to CDRRule as QSSD
   * case is also included along with Compli Rule
   *
   * @param rule -
   * @return -
   * @throws SSDiCDMInterfaceException -
   */

  public List<CDRRuleExt> getRuleHistoryForCDRRule(final CDRRule rule) throws SSDiCDMInterfaceException;

  /**
   * ALM-249761 Method to get the software version list based on software project NodeId
   *
   * @param swProjNodeId - software Project node Id from SSD
   * @return List of swversions
   * @throws SSDiCDMInterfaceException -
   */
  public List<SoftwareVersion> getSwVersionListBySwProjId(long swProjNodeId) throws SSDiCDMInterfaceException;

  /**
   * ALM- 249674 Method to get the SSD Release List based on the software Version Id from VILLADB
   *
   * @param swVersionId - software version Id of Villa DB
   * @return List of SSD Releases
   * @throws SSDiCDMInterfaceException -
   */
  public List<SSDRelease> getSSDRelesesBySwVersionId(long swVersionId) throws SSDiCDMInterfaceException;

  /**
   * ALM-249675 Method to get the CDR Rule list based on the releaseId
   *
   * @param releaseId - proReleaseId of SSD Release
   * @return List of CDR Rules
   * @throws SSDiCDMInterfaceException -
   */

  public List<CDRRule> readRuleForRelease(BigDecimal releaseId) throws SSDiCDMInterfaceException;

  /**
   * ALM-249675 Method to get the release reports by Release Id
   *
   * @param path -
   * @param releaseId -
   * @param ruleIdFlag -
   * @return -
   * @throws SSDiCDMInterfaceException -
   */
  public String getReleaseReportsByReleaseId(String path, BigDecimal releaseId, boolean ruleIdFlag)
      throws SSDiCDMInterfaceException;


  /**
   * ALM-340565 Method to get the input model list for specififed list of Rule id and Version id - Required for OEM
   * related Implementation request
   *
   * @param ruleIdWithRevList list of rules
   * @return map containng the descr
   * @throws SSDiCDMInterfaceException ex
   */
  public Map<String, RuleIdDescriptionModel> getOEMDetailsForRuleId(Set<OEMRuleDescriptionInput> ruleIdWithRevList)
      throws SSDiCDMInterfaceException;


  /**
   * @param nodeId
   * @param revId
   * @return map
   * @throws SSDiCDMInterfaceException Exception
   */
  Map<String, String> getQSSDLabelFromQSSDNode() throws SSDiCDMInterfaceException;

  /**
   * Returns the Release ID for the current release
   *
   * @return Release ID
   * @throws SSDiCDMInterfaceException exception
   */
  public BigDecimal getProReleaseID() throws SSDiCDMInterfaceException;

  /**
   * Retreive the Config parameter value from DB with key
   *
   * @param name key
   * @return Value
   * @throws SSDiCDMInterfaceException Exception
   */
  public String getConfigValue(SSDConfigParams name) throws SSDiCDMInterfaceException;

  /**
   * @return list
   * @throws SSDiCDMInterfaceException Exception
   */
  public List<String> getQSSDLabels() throws SSDiCDMInterfaceException;


  /**
   * @param nodeId nodeid
   * @return Returns ProRevid - The id of the active labellist of the passed nodeID
   * @throws SSDiCDMInterfaceException Exception
   */
  public BigDecimal getProRevId(BigDecimal nodeId) throws SSDiCDMInterfaceException;

  /**
   * @return CreateSSDRelease
   */
  public CreateSSDRelease getReleaseUtils();

  /**
   * To retrieve the additional rule information like RO, CoC, Description etc
   *
   * @param ruleId Rule ID
   * @param revId Rev ID
   * @return additional information
   * @throws SSDiCDMInterfaceException Exception
   */
  public CDRRuleExt getAdditionalRuleInformation(BigDecimal ruleId, BigDecimal revId) throws SSDiCDMInterfaceException;
  
  /**
   * method to invoke release creation process in ssd for COMPLIANCE labels of Non SDOM SWs
   * 
   * @param labelList - label key and unit value
   * @param isQSSDOnlyParameters TRUE When labellist contains only QSSD Parameters, else FALSE
   * @return - feature value model
   * @throws SSDiCDMInterfaceException - interuption exception ALM-294077 return type changed from SSDMessage to
   *           SSDMessageOptions
   */
  public SSDMessageOptions invokeNonSDOMComplianceRelease(final Map<String, String> labelList,
      final boolean isQSSDOnlyParameters)
      throws SSDiCDMInterfaceException;

}
