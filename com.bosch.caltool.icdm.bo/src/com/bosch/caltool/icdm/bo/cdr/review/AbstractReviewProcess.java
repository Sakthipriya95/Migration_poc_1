/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.review;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.ParameterAttributeLoader;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.bo.cdr.CheckSSDResultParam;
import com.bosch.caltool.icdm.bo.cdr.ReviewRuleAdapter;
import com.bosch.caltool.icdm.bo.cdr.RuleSetLoader;
import com.bosch.caltool.icdm.bo.cdr.SSDServiceHandler;
import com.bosch.caltool.icdm.bo.checkssd.RuleDescriptionLoader;
import com.bosch.caltool.icdm.bo.compli.CompliParamCheckSSDInvoker;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.FileNameUtil;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.a2l.ParameterAttribute;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.AbstractParameter;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.review.ReviewInput;
import com.bosch.checkssd.CheckSSDCoreInstances;
import com.bosch.checkssd.CheckSSDInfo;
import com.bosch.checkssd.exception.CheckSSDException;
import com.bosch.checkssd.reports.CheckSSDReport;
import com.bosch.checkssd.reports.reportmodel.ReportModel;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.CDRRulesWithFile;
import com.bosch.ssd.icdm.model.FeatureValueModel;

/**
 * @author bru2cob
 */
public abstract class AbstractReviewProcess extends AbstractSimpleBusinessObject {


  /**
   *
   */
  private static final String SSD_FILE_ERROR = "SSD file not created. Please perform the review again";
  /**
   * Output files to be skipped
   */
  private static final String[] UNWANTED_OUTPUT_FILES = { ".wrn", ".lab", ".err", ".log" };
  /**
   *
   */
  private static final int CHKSSD_GENERATE_ALL_TYPES = 2;

  /**
   * Dir name of data review root folder
   */
  public static final String DATARVW_ROOT_DIR_NAME = "DATA_RVW";

  /**
   * Dir path of data review root folder
   */
  // 493963
  public static final String DATARVW_ROOT_DIR_PATH =
      Messages.getString("SERVICE_WORK_DIR") + File.separator + DATARVW_ROOT_DIR_NAME;

  private final ReviewInput reviewInputData;
  private final ReviewedInfo reviewOutput = new ReviewedInfo();

  private String dataFileName;

  /**
   * @param reviewInputData input data
   * @param serviceData Service Data
   */
  public AbstractReviewProcess(final ReviewInput reviewInputData, final ServiceData serviceData) {
    super(serviceData);
    this.reviewInputData = reviewInputData;
  }

  /**
   * Invoke CheckSSD tool
   *
   * @param paramNameSet parameter names
   * @param featureValModelSet feature value model set
   * @param paramSet parameter model
   * @param secReviewParamMap secondary review parameters
   * @param secFeatureValueModelMap feature value model for secondary review
   * @param secondaryCommonParamSet common params for secondary review
   * @param secondaryComParamFeaValSet feature values of secondary review of common params
   * @throws IcdmException processing error
   */
  protected void invokeSSD(final Set<String> paramNameSet, final Set<FeatureValueModel> featureValModelSet,
      final Set<AbstractParameter> paramSet, final Map<Long, Set<AbstractParameter>> secReviewParamMap,
      final Map<Long, Set<FeatureValueModel>> secFeatureValueModelMap,
      final Set<AbstractParameter> secondaryCommonParamSet, final Set<FeatureValueModel> secondaryComParamFeaValSet)
      throws IcdmException {

    Set<String> modifiedLabelList = createLabelListForSSD(paramNameSet);

    fetchSSDRulesForRuleSets(featureValModelSet, paramSet, secReviewParamMap, secFeatureValueModelMap,
        modifiedLabelList);

    // ssd file path review
    if (this.reviewInputData.getRulesData().getSsdRuleFilePath() != null) {
      ReviewRuleSetData ruleData = new ReviewRuleSetData();
      ruleData.setSSDFilePath(this.reviewInputData.getRulesData().getSsdRuleFilePath());
      ruleData.setSSDFileReview(true);
      this.reviewOutput.getSecRuleSetDataList().add(ruleData);
    }

    // ssd release id review
    if (this.reviewInputData.getRulesData().getSsdReleaseId() != null) {
      setRulesForSSDRel();
    }

    fetchSSDRulesForCommon(featureValModelSet, paramSet, secondaryCommonParamSet, secondaryComParamFeaValSet,
        modifiedLabelList);

    fillParamWithNoRules(paramSet);

  }

  /**
   * @param featureValModelSet
   * @param paramSet
   * @param secondaryCommonParamSet
   * @param secondaryComParamFeaValSet
   * @param modifiedLabelList
   * @throws IcdmException
   */
  private void fetchSSDRulesForCommon(final Set<FeatureValueModel> featureValModelSet,
      final Set<AbstractParameter> paramSet, final Set<AbstractParameter> secondaryCommonParamSet,
      final Set<FeatureValueModel> secondaryComParamFeaValSet, final Set<String> modifiedLabelList)
      throws IcdmException {

    CDRRulesWithFile rulesWithFilePath;
    // common rules review
    // fetch the common rules if Common Rules is for primary
    if (this.reviewInputData.getRulesData().isCommonRulesPrimary()) {
      // fetching common rules if it is needed
      CommonRulesResolver commonRulesResolver =
          new CommonRulesResolver(modifiedLabelList, featureValModelSet, getServiceData());
      rulesWithFilePath = commonRulesResolver.getRules();
      checkForRuleFile(rulesWithFilePath);
      fetchRulesForGivenSSDFile(paramSet, rulesWithFilePath, this.reviewInputData.getRulesData().isCommonRulesPrimary(),
          null, null);
    }
    else if (this.reviewInputData.getRulesData().isCommonRulesSecondary()) {
      // fetching common rules if it is needed
      CommonRulesResolver commonRulesResolver =
          new CommonRulesResolver(modifiedLabelList, secondaryComParamFeaValSet, getServiceData());
      rulesWithFilePath = commonRulesResolver.getRules();
      checkForRuleFile(rulesWithFilePath);
      fetchRulesForGivenSSDFile(secondaryCommonParamSet, rulesWithFilePath,
          this.reviewInputData.getRulesData().isCommonRulesPrimary(), null, null);
    }
  }

  /**
   * @param featureValModelSet
   * @param paramSet
   * @param secReviewParamMap
   * @param secFeatureValueModelMap
   * @param modifiedLabelList
   * @param ruleSetLoader
   * @throws DataException
   * @throws IcdmException
   */
  private void fetchSSDRulesForRuleSets(final Set<FeatureValueModel> featureValModelSet,
      final Set<AbstractParameter> paramSet, final Map<Long, Set<AbstractParameter>> secReviewParamMap,
      final Map<Long, Set<FeatureValueModel>> secFeatureValueModelMap, final Set<String> modifiedLabelList)
      throws IcdmException {

    final RuleSetLoader ruleSetLoader = new RuleSetLoader(getServiceData());

    CDRRulesWithFile rulesWithFilePath;

    // rule set review
    if (CommonUtils.isNotNull(this.reviewInputData.getRulesData().getPrimaryRuleSetId())) {
      RuleSet ruleSet = ruleSetLoader.getDataObjectByID(this.reviewInputData.getRulesData().getPrimaryRuleSetId());
      RuleSetRulesResolver ruleSetRuleResolver =
          new RuleSetRulesResolver(ruleSet, modifiedLabelList, featureValModelSet, getServiceData());

      // if rule set is selected, then fetch rules from ruleset for primary rules
      rulesWithFilePath = ruleSetRuleResolver.getRules();
      checkForRuleFile(rulesWithFilePath);
      fetchRulesForGivenSSDFile(paramSet, rulesWithFilePath, true, ruleSet, null);
    }

    // for mandatory ruleset
    if (!CommonUtils.isNullOrEmpty(this.reviewOutput.getSecRuleSetDataList())) {
      for (ReviewRuleSetData ruleSetData : this.reviewOutput.getSecRuleSetDataList()) {
        RuleSet secondaryRuleSet = ruleSetData.getRuleSet();
        if (secondaryRuleSet != null) {
          RuleSetRulesResolver ruleSetRuleResolver =
              new RuleSetRulesResolver(secondaryRuleSet, modifiedLabelList, featureValModelSet, getServiceData());
          rulesWithFilePath = ruleSetRuleResolver.getRules();
          checkForRuleFile(rulesWithFilePath);
          fetchRulesForGivenSSDFile(paramSet, rulesWithFilePath, false, secondaryRuleSet, ruleSetData);
        }
      }
    }
    if (!CommonUtils.isNullOrEmpty(this.reviewInputData.getRulesData().getSecondaryRuleSetIds())) {
      // fetching secondary rule sets
      for (Long secondaryRuleSetId : this.reviewInputData.getRulesData().getSecondaryRuleSetIds()) {
        Set<FeatureValueModel> secfeatureValModelSet = secFeatureValueModelMap.get(secondaryRuleSetId);
        Set<AbstractParameter> secParamSet = secReviewParamMap.get(secondaryRuleSetId);
        RuleSet secondaryRuleSet = ruleSetLoader.getDataObjectByID(secondaryRuleSetId);
        if (secondaryRuleSet != null) {
          ReviewRuleSetData secondaryRuleSetData = new ReviewRuleSetData();
          secondaryRuleSetData.setRuleSet(secondaryRuleSet);
          this.reviewOutput.getSecRuleSetDataList().add(secondaryRuleSetData);
          RuleSetRulesResolver ruleSetRuleResolver =
              new RuleSetRulesResolver(secondaryRuleSet, modifiedLabelList, secfeatureValModelSet, getServiceData());
          rulesWithFilePath = ruleSetRuleResolver.getRules();
          checkForRuleFile(rulesWithFilePath);
          fetchRulesForGivenSSDFile(secParamSet, rulesWithFilePath, false, secondaryRuleSet, secondaryRuleSetData);
        }
      }
    }
  }

  /**
   * @param paramNameSet
   * @return
   */
  private Set<String> createLabelListForSSD(final Set<String> paramNameSet) {
    // Icdm-1255-Varaint coded params
    Map<String, String> baseParamMap = ApicUtil.getBaseParamMap(paramNameSet);
    Set<String> paramsWithDepencies = new HashSet<>();
    if (!baseParamMap.isEmpty()) {
      paramsWithDepencies =
          (new ParameterAttributeLoader(getServiceData())).fetchParamsWithDep(new HashSet<>(baseParamMap.values()));
    }

    return ApicUtil.removeBaseParamWithDep(paramNameSet, paramsWithDepencies);

  }

  /**
   * @param paramSet
   */
  private void fillParamWithNoRules(final Set<AbstractParameter> paramSet) {
    // ICDM-2477
    getLogger().debug("Finding params without rules. Input size = {}", paramSet.size());

    Set<String> paramWithoutRule = new HashSet<>();
    for (AbstractParameter param : paramSet) {
      checkRuleAvailability(param.getName(), paramWithoutRule);
    }

    this.reviewOutput.getParamWithoutRule().addAll(paramWithoutRule);

    getLogger().debug("Finding params without rules completed. Items found = {}",
        this.reviewOutput.getParamWithoutRule().size());
  }

  /**
   * @param rulesWithFile
   * @throws IcdmException
   */
  private void checkForRuleFile(final CDRRulesWithFile rulesWithFile) throws IcdmException {

    if (rulesWithFile == null) {
      throw new IcdmException(SSD_FILE_ERROR);
    }

    List<String> validRuleParameters = rulesWithFile.getValidRuleParameters();
    int validParams = validRuleParameters == null ? 0 : validRuleParameters.size();

    String ssdFilePath = CommonUtils.checkNull(rulesWithFile.getSsdFilePath());

    // If valid rules are available, but SSD file not created, then there is an internal problem.
    if ((validParams > 0) && ssdFilePath.isEmpty()) {
      throw new IcdmException(SSD_FILE_ERROR);
    }

    Map<String, List<CDRRule>> rulesMap = rulesWithFile.getCdrRules();
    int totalRules = rulesMap == null ? 0 : rulesMap.size();

    getLogger().debug("Rules with file : Total Rules = {}, Params with valid rules = {} ", totalRules, validParams);
    getLogger().debug("  SSD file = {}", ssdFilePath);
    getLogger().debug("  SSD Message = {}", rulesWithFile.getSsdMessage());
  }

  /**
   * @throws IcdmException
   */
  private void setRulesForSSDRel() throws IcdmException {
    SSDServiceHandler ssdServiceHandler = new SSDServiceHandler(getServiceData());
    List<CDRRule> cdrRules;
    try {
      cdrRules = ssdServiceHandler
          .readRuleForRelease(BigDecimal.valueOf(this.reviewInputData.getRulesData().getSsdReleaseId()));
      String currentDate = ApicUtil.getCurrentTime(DateFormat.DATE_FORMAT_20);
      String ssdFilePath = CommonUtils.getSystemUserTempDirPath() + File.separator + CDRConstants.SECONDARY_REVIEW +
          CDRConstants.SSD_FILE_PATH_SEPARATOR + CDRConstants.SSD_RULES + CDRConstants.SSD_FILE_PATH_SEPARATOR +
          currentDate;

      String reprtPath = ssdServiceHandler.getReleaseReportsByReleaseId(ssdFilePath,
          BigDecimal.valueOf(this.reviewInputData.getRulesData().getSsdReleaseId()));

      ReviewRuleSetData ruleData = new ReviewRuleSetData();
      ruleData.setSSDFilePath(reprtPath);
      ruleData.setSsdReleaseID(this.reviewInputData.getRulesData().getSsdReleaseId());
      ruleData.setSsdVersionID(this.reviewOutput.getSsdSoftwareVersionId());
      setRules(ruleData, cdrRules);
      this.reviewOutput.getSecRuleSetDataList().add(ruleData);
    }
    catch (IcdmException exp) {
      throw new IcdmException("REVIEW.SSD_RULE_FETCH_ERROR", exp, exp.getMessage());
    }
  }

  /**
   * @param ruleData
   * @param cdrRules
   * @throws DataException
   */
  private void setRules(final ReviewRuleSetData ruleData, final List<CDRRule> cdrRules) throws DataException {
    if (null != cdrRules) {
      Map<String, List<CDRRule>> ssdRulesMap = new HashMap<>();

      for (CDRRule cdrRule : cdrRules) {
        List<CDRRule> listOfRules = ssdRulesMap.get(cdrRule.getParameterName());
        if (null == listOfRules) {
          listOfRules = new ArrayList<>();
        }
        listOfRules.add(cdrRule);
        ssdRulesMap.put(cdrRule.getParameterName(), listOfRules);
      }

      if ((ssdRulesMap.keySet() != null)) {
        ReviewRuleAdapter ruleAdapter = new ReviewRuleAdapter(getServiceData());
        ruleData.setSSDRules(ruleAdapter.convertSSDRule(ssdRulesMap));
      }
    }

  }

  /**
   * @param allParameters
   * @param paramName
   * @param paramWithoutRule
   */
  private void checkRuleAvailability(final String paramName, final Set<String> paramWithoutRule) {
    if (null == this.reviewOutput.getPrimarySSDRules()) {
      paramWithoutRule.add(paramName);
    }
    else {
      List<ReviewRule> paramRulesList = this.reviewOutput.getPrimarySSDRules().get(paramName);
      if ((null == paramRulesList) || paramRulesList.isEmpty()) {
        paramWithoutRule.add(paramName);
      }
      else {
        addRuleIncompleteParams(paramName, paramWithoutRule, paramRulesList);
      }
    }
  }

  /**
   * @param paramName
   * @param paramWithoutRule
   * @param ruleUtility
   * @param paramRulesList
   */
  private void addRuleIncompleteParams(final String paramName, final Set<String> paramWithoutRule,
      final List<ReviewRule> paramRulesList) {
    RuleUtility ruleUtility = new RuleUtility();
    for (ReviewRule paramRule : paramRulesList) {
      if (!ruleUtility.isRuleComplete(paramRule)) {
        paramWithoutRule.add(paramName);
      }
    }
  }

  /**
   * @param paramSet
   * @param ssdRulesWithPath
   * @param isPrimaryRule
   * @param ruleSet
   * @param reviewRuleSetData
   * @throws DataException
   */
  private void fetchRulesForGivenSSDFile(final Set<AbstractParameter> paramSet, final CDRRulesWithFile ssdRulesWithPath,
      final boolean isPrimaryRule, final RuleSet ruleSet, final ReviewRuleSetData rvwRuleSetDataInput)
      throws IcdmException {

    ReviewRuleSetData rvwRuleSetData = rvwRuleSetDataInput;
    ReviewRuleAdapter ruleAdapter = new ReviewRuleAdapter(getServiceData());

    if ((null == rvwRuleSetData) && !isPrimaryRule) {
      // if it is not initialised (in case of common rules)
      rvwRuleSetData = new ReviewRuleSetData();
      // add it to the secondary rules list
      this.reviewOutput.getSecRuleSetDataList().add(rvwRuleSetData);
    }
    if (ssdRulesWithPath.getSsdFilePath() == null) {
      // if there are no rules found from SSD
      if (isPrimaryRule) {
        // set the error flag only in case of primary rules
        this.reviewOutput.setNoValidRuleFlag(true);
        this.reviewOutput.setPrimarySSDRules(ruleAdapter.convertSSDRule(ssdRulesWithPath.getCdrRules()));
      }
      return;
    }

    if (isPrimaryRule) {
      setRulesFilePathForPrimary(ssdRulesWithPath, ruleSet, ruleAdapter);
    }
    else {
      setRulesFilePathForSecondary(ssdRulesWithPath, ruleSet, rvwRuleSetData, ruleAdapter);
    }

    Map<String, List<ReviewRule>> cdrRules = ruleAdapter.convertSSDRule(ssdRulesWithPath.getCdrRules());

    validateMultiRules(paramSet, cdrRules);
    validateAttrIncompleteRules(paramSet, cdrRules);
  }

  /**
   * @param ssdRulesWithPath
   * @param ruleSet
   * @param rvwRuleSetData
   * @param ruleAdapter
   * @throws DataException
   */
  private void setRulesFilePathForSecondary(final CDRRulesWithFile ssdRulesWithPath, final RuleSet ruleSet,
      final ReviewRuleSetData rvwRuleSetData, final ReviewRuleAdapter ruleAdapter)
      throws DataException {

    String secRevFileName;
    String currentDate = ApicUtil.getCurrentTime(DateFormat.DATE_FORMAT_20);
    if (null != ruleSet) {
      String ruleSetName = FileNameUtil.formatFileName(ruleSet.getName(), ApicConstants.SPL_CHAR_PTRN);
      secRevFileName = CDRConstants.SECONDARY_REVIEW + CDRConstants.SSD_FILE_PATH_SEPARATOR + ruleSetName +
          CDRConstants.SSD_FILE_PATH_SEPARATOR + currentDate + CDRConstants.SSD_FILE_EXT;
    }
    else if (ReviewRuleSetData.RULE_SOURCE.SSD_RELEASE.equals(rvwRuleSetData.getSource())) {
      secRevFileName = CDRConstants.MAIN_REVIEW + CDRConstants.SSD_FILE_PATH_SEPARATOR + CDRConstants.SSD_RULES +
          CDRConstants.SSD_FILE_PATH_SEPARATOR + currentDate + CDRConstants.SSD_FILE_EXT;
    }
    else {
      secRevFileName = CDRConstants.SECONDARY_REVIEW + CDRConstants.SSD_FILE_PATH_SEPARATOR +
          CDRConstants.COMMON_RULES + CDRConstants.SSD_FILE_PATH_SEPARATOR + currentDate + CDRConstants.SSD_FILE_EXT;
    }
    File newSSDFile = renameSSDFile(ssdRulesWithPath, secRevFileName);

    rvwRuleSetData.setSSDRules(ruleAdapter.convertSSDRule(ssdRulesWithPath.getCdrRules()));
    rvwRuleSetData.setSSDFilePath(newSSDFile.getPath());
  }

  /**
   * @param ssdRulesWithPath
   * @param secRevFileName
   * @return
   */
  private File renameSSDFile(final CDRRulesWithFile ssdRulesWithPath, final String secRevFileName) {
    String[] filePathsplit = ssdRulesWithPath.getSsdFilePath().split("\\\\");
    String fileName = filePathsplit[(filePathsplit.length) - 1];
    File newSSDFile = new File(ssdRulesWithPath.getSsdFilePath().replace(fileName, secRevFileName));

    if (newSSDFile.exists()) {
      try {
        Files.delete(newSSDFile.toPath());
      }
      catch (IOException e) {
        getLogger().warn("Failed to new SSD file " + newSSDFile + ". " + e.getMessage(), e);
      }
    }

    newSSDFile = new File(ssdRulesWithPath.getSsdFilePath().replace(fileName, secRevFileName));

    File oldSSDFile = new File(ssdRulesWithPath.getSsdFilePath());
    boolean renamed = oldSSDFile.renameTo(newSSDFile);
    if (!renamed) {
      getLogger().warn("Rename to new file returned FALSE. File:" + newSSDFile);
    }

    return newSSDFile;
  }

  /**
   * @param ssdRulesWithPath
   * @param ruleSet
   * @param ruleAdapter
   * @throws DataException
   */
  private void setRulesFilePathForPrimary(final CDRRulesWithFile ssdRulesWithPath, final RuleSet ruleSet,
      final ReviewRuleAdapter ruleAdapter)
      throws DataException {

    // for primary rules
    this.reviewOutput.setPrimarySSDRules(ruleAdapter.convertSSDRule(ssdRulesWithPath.getCdrRules()));

    String mainRevFileName;
    String currentDate = ApicUtil.getCurrentTime(DateFormat.DATE_FORMAT_20);
    if (null != ruleSet) {
      String ruleSetName = FileNameUtil.formatFileName(ruleSet.getName(), ApicConstants.SPL_CHAR_PTRN);
      mainRevFileName = CDRConstants.MAIN_REVIEW + CDRConstants.SSD_FILE_PATH_SEPARATOR + ruleSetName +
          CDRConstants.SSD_FILE_PATH_SEPARATOR + currentDate + CDRConstants.SSD_FILE_EXT;
    }
    else {
      mainRevFileName = CDRConstants.MAIN_REVIEW + CDRConstants.SSD_FILE_PATH_SEPARATOR + CDRConstants.COMMON_RULES +
          CDRConstants.SSD_FILE_PATH_SEPARATOR + currentDate + CDRConstants.SSD_FILE_EXT;
    }

    File newSSDFile = renameSSDFile(ssdRulesWithPath, mainRevFileName);


    this.reviewOutput.setPrimarySSDFilePath(newSSDFile.getPath());
  }

  /**
   * @param paramSet
   * @param cdrRules
   * @throws IcdmException
   */
  private void validateMultiRules(final Set<AbstractParameter> paramSet, final Map<String, List<ReviewRule>> cdrRules)
      throws IcdmException {

    getLogger().debug("Validation of multi rules started...");

    // Error : Multiple rules retrieved for a parameter
    for (AbstractParameter param : paramSet) { // icdm-1199
      List<ReviewRule> ruleList = cdrRules.get(param.getName());
      if ((ruleList != null) && (ruleList.size() > 1)) {
        throw new IcdmException("SSD.MULTIPLE_RULE", param.getName());
      }
    }

    getLogger().debug("Validation of multi rules completed...");
  }

  /**
   * @param paramSet
   * @param cdrRules
   * @throws IcdmException
   */
  private void validateAttrIncompleteRules(final Set<AbstractParameter> paramSet,
      final Map<String, List<ReviewRule>> cdrRulesMap)
      throws IcdmException {

    // ICDM-1199

    getLogger().debug("Validation of attr-incomplete rules started...");

    Set<String> paramNameSet = paramSet.stream().map(AbstractParameter::getName).collect(Collectors.toSet());

    // identifying and adding parameter names that has dependencies
    Set<String> paramNamesWithDepn = new ParameterAttributeLoader(getServiceData()).fetchParamsWithDep(paramNameSet);

    // Check for parameter with attr dependencies, rules are present without complete dependency
    // Skip this validation if there are no parameters with dependencies
    if (!paramNamesWithDepn.isEmpty()) {

      // TODO can we reuse the rules retrieved using Rules with file call?
      Map<String, List<CDRRule>> cdrRuleMap =
          new SSDServiceHandler(getServiceData()).readReviewRule(new ArrayList<>(paramNamesWithDepn));
      ReviewRuleAdapter ruleAdapter = new ReviewRuleAdapter(getServiceData());
      Map<String, List<ReviewRule>> allRuleForParams = ruleAdapter.convertSSDRule(cdrRuleMap);

      getLogger().debug("Begin finding whether param rules are incomplete...");
      List<String> paramWithIncompleteAttrDef = new ArrayList<>();
      for (List<ReviewRule> ruleList : allRuleForParams.values()) {
        findIfParamRulesAttrIncomplete(paramWithIncompleteAttrDef, ruleList);
      }
      getLogger().debug("End finding whether param rules are incomplete");

      if (!paramWithIncompleteAttrDef.isEmpty()) {
        throw new IcdmException("Rule is incomplete for \n" + paramWithIncompleteAttrDef);
      }

    }

    getLogger().debug("Validation of attr-incomplete rules completed.");
  }

  /**
   * If the Rule returned by the SSD has more or less Dep than the dep list of Parameter then praram rule is incomplete.
   *
   * @param paramWithIncompleteAttrDef List of parameter names with incomplete attr definition
   * @param attrDefForRuleIncomplete true if attr definition is incomplete atleast for a parameter
   * @param ruleList List of rules for the review
   * @throws DataException
   */
  private void findIfParamRulesAttrIncomplete(final List<String> paramWithIncompleteAttrDef,
      final List<ReviewRule> ruleList)
      throws DataException {

    if (CommonUtils.isNotEmpty(ruleList)) {
      ParameterAttributeLoader paramAttrLoader = new ParameterAttributeLoader(getServiceData());
      for (ReviewRule rule : ruleList) {
        SortedSet<AttributeValueModel> dependencyList = rule.getDependencyList();
        ParameterLoader paramLoader = new ParameterLoader(getServiceData());
        Map<Long, Parameter> paramByNameMap = paramLoader.getParamByNameOnly(rule.getParameterName());
        Parameter cdrFuncParameter = null;
        if (paramByNameMap != null) {
          cdrFuncParameter = paramByNameMap.values().iterator().next();
        }

        // Icdm-1289- If the Parameter type or the parameter lower upper case is different in ICDM and SSD the CDR func
        // parameter object can be null. So null check is made for CDR func Parameter
        Set<IParameter> paramSet = new HashSet<>();
        if (cdrFuncParameter != null) {
          paramSet.add(cdrFuncParameter);
        }
        Map<String, List<ParameterAttribute>> parameterAttrDepnMap = paramAttrLoader.fetchParameterAttrDepn(paramSet);
        if (CommonUtils.isNotEmpty(dependencyList) && CommonUtils.isNotNull(cdrFuncParameter) &&
            CommonUtils.isNotNull(parameterAttrDepnMap.get(rule.getParameterName())) &&
            !CommonUtils.isEqual(dependencyList.size(), parameterAttrDepnMap.get(rule.getParameterName()).size())) {
          // avoid checking // the condition for default rule
          paramWithIncompleteAttrDef.add(rule.getParameterName());
          break;

        }
      }
    }

  }

  void invokeCheckSSD() throws IcdmException {

    Map<String, CalData> calDataMap = this.reviewOutput.getCalDataMap();
    A2LFileInfo a2lFileContents = this.reviewOutput.getA2lFileContents();

    String primarySsdFilePath = this.reviewOutput.getPrimarySSDFilePath();

    // Task 231284
    // invoke check ssd for primary SSD file
    if (primarySsdFilePath != null) {
      checkSSDCallForSSDFile(calDataMap, a2lFileContents, primarySsdFilePath, true, null);
    }
    // invoke check ssd for secondary SSD files
    for (ReviewRuleSetData ruleSetData : this.reviewOutput.getSecRuleSetDataList()) {
      if (ruleSetData.getSsdFilePath() != null) {
        checkSSDCallForSSDFile(calDataMap, a2lFileContents, ruleSetData.getSsdFilePath(), false, ruleSetData);
      }
    }
  }

  /**
   * @param calDataMap
   * @param a2lFileContents
   * @param ssdFilePath
   * @param isPrimaryRule
   * @param ruleSetData
   * @param checkSSDResultParamMap
   * @throws DataException
   */
  private void checkSSDCallForSSDFile(final Map<String, CalData> calDataMap, final A2LFileInfo a2lFileContents,
      final String ssdFilePath, final boolean isPrimaryRule, final ReviewRuleSetData ruleSetData)
      throws IcdmException {

    File ssdFile;

    if (this.reviewOutput.getFilesStreamMap().containsKey(ssdFilePath)) {
      String currentDate = ApicUtil.getCurrentTime(DateFormat.DATE_FORMAT_20);
      ssdFile = writeSSDFile(ssdFilePath, currentDate);
    }
    else {
      ssdFile = new File(ssdFilePath);
    }

    if (ssdFile.exists() && (calDataMap != null) && !calDataMap.isEmpty() && (a2lFileContents != null)) {
      Map<String, CheckSSDResultParam> checkSSDResultParamMap = new HashMap<>();
      getCheckSSDData(calDataMap, a2lFileContents, isPrimaryRule, ruleSetData, checkSSDResultParamMap, ssdFile);
    }
  }

  /**
   * @param ssdFilePath
   * @param currentDate
   * @return
   * @throws IcdmException
   */
  private File writeSSDFile(final String ssdFilePath, final String currentDate) throws IcdmException {
    File ssdFile;
    InputStream inputStream = new ByteArrayInputStream(this.reviewOutput.getFilesStreamMap().get(ssdFilePath));
    if (this.reviewOutput.getPrimarySSDFilePath() == null) {
      File file = new File(Messages.getString("SERVICE_WORK_DIR"));
      if (!file.exists()) {
        file.mkdir();
      }
      file = new File(file.getAbsoluteFile() + File.separator + DATARVW_ROOT_DIR_NAME);
      if (!file.exists()) {
        file.mkdir();
      }
      file = new File(file.getAbsoluteFile() + "\\review_" + currentDate);
      file.mkdir();
      ssdFile = new File(file.getAbsolutePath() + File.separator + "SSD_Review" + CDRConstants.SSD_FILE_PATH_SEPARATOR +
          currentDate + ".ssd");
    }
    else {
      File ssd = new File(this.reviewOutput.getPrimarySSDFilePath());
      ssdFile = new File(ssd.getParent() + File.separator + "SSD_Review" + CDRConstants.SSD_FILE_PATH_SEPARATOR +
          currentDate + ".ssd");
    }
    // Call with new try method
    try (FileOutputStream outputStream = new FileOutputStream(ssdFile)) {
      int read = 0;
      byte[] bytes = new byte[1024];
      while ((read = inputStream.read(bytes)) != -1) {
        outputStream.write(bytes, 0, read);
      }
    }
    catch (IOException exp) {
      throw new IcdmException(exp.getLocalizedMessage(), exp);
    }
    return ssdFile;
  }

  /**
   * @param calDataMap
   * @param a2lFileContents
   * @param isPrimaryRule
   * @param ruleSetData
   * @param checkSSDResultParamMap
   * @param ssdFile
   * @param currentDate
   * @throws DataException
   * @throws IcdmException
   */
  private void getCheckSSDData(final Map<String, CalData> calDataMap, final A2LFileInfo a2lFileContents,
      final boolean isPrimaryRule, final ReviewRuleSetData ruleSetData,
      final Map<String, CheckSSDResultParam> checkSSDResultParamMap, final File ssdFile)
      throws IcdmException {

    CheckSSDInfo checkSSDInfo = new CheckSSDInfo(ssdFile.getAbsolutePath());
    checkSSDInfo.setLogger(this.reviewOutput.getCheckSSDLogger());

    String suffix = getCheckSSDFileSuffix(ruleSetData);
    Map<String, CalData> clonedCalDataMap = CommonUtils.cloneCalData(calDataMap);

    String reviewName = isPrimaryRule ? CDRConstants.MAIN_REVIEW : CDRConstants.SECONDARY_REVIEW;

    this.reviewInputData.getFileData().getSelFilesPath()
        .forEach(filePath -> this.dataFileName = new File(filePath).getName());

    // Removing currentDate from dir name to limit file path length
    String dirName =
        new StringBuilder(reviewName).append(CDRConstants.SSD_FILE_PATH_SEPARATOR).append(suffix).toString();
    if (dirName.length() > 60) {
      dirName = StringUtils.abbreviateMiddle(dirName, "_", 60);
    }
    File dir = new File(ssdFile.getParent() + File.separator + dirName);

    if (!dir.exists()) {
      dir.mkdir();
    }
    checkSSDInfo.setOptFileLocForRpts(dir.getAbsolutePath());
    checkSSDInfo.setCalDataMap(clonedCalDataMap);
    checkSSDInfo.setA2lFileInfo(a2lFileContents);

    checkSSDInfo.setRptTypeFlag(CHKSSD_GENERATE_ALL_TYPES);
    checkSSDInfo.setGrpSwitch(CompliParamCheckSSDInvoker.CHKSSD_INFO_GRP_SWITCH_ONEFILECHECK);

    checkSSDInfo.setA2lFilePath(a2lFileContents.getFileName());
    checkSSDInfo.setHexFilePath(this.dataFileName);
    CheckSSDCoreInstances cssdCoreInstances = new CheckSSDCoreInstances();
    checkSSDInfo.setCssdCoreInstances(cssdCoreInstances);
    ParameterLoader paramLoader = new ParameterLoader(getServiceData());
    checkSSDInfo.setCompliMapFromIcdm(paramLoader.getCompliParamWithType());
    checkSSDInfo.setQualityLblMapFromIcdm(paramLoader.getQssdParams());
    // to set ruleDescProvider - to invoke service directly
    checkSSDInfo.setRuleDescProvider(new RuleDescriptionLoader(getServiceData())::getOEMParamForCheckssd);
    try {
      checkSSDInfo.runCheckSSD();
      Set<String> chkSsdOpFiles = checkSSDInfo.getOutPutFilesPath();

      // Removing current timestamp from file name
      changeCheckSSDFileNames(checkSSDInfo, new StringBuilder(reviewName).append(CDRConstants.SSD_FILE_PATH_SEPARATOR)
          .append(suffix).append(CDRConstants.SSD_FILE_PATH_SEPARATOR).append(CDRConstants.CHECK_SSD).toString(),
          chkSsdOpFiles);

      // Task 231284
      if (this.reviewOutput.getGeneratedCheckSSDFiles() == null) {
        final Set<String> outputFiles = new HashSet<>();
        this.reviewOutput.setGeneratedCheckSSDFiles(outputFiles);
      }
      // log, warn, lab and error file to be not set in the output.
      chkSsdOpFiles.forEach(outputFile -> {
        if (!CommonUtils.containsString(outputFile, UNWANTED_OUTPUT_FILES)) {
          this.reviewOutput.getGeneratedCheckSSDFiles().add(outputFile);
        }
      });
    }
    catch (CheckSSDException exp) {
      getLogger().error("Exception occured while running CheckSSD. " + exp.getMessage(), exp);
      throw new IcdmException(exp.getMessage());
    }

    // Report
    CheckSSDReport checkSSDReport = checkSSDInfo.getRptInstance();
    Map<String, ReportModel> reportModelMap = new HashMap<>();

    List<ReportModel> allModels = new ArrayList<>();
    // Value Type
    allModels.addAll(checkSSDReport.getValueModelList());
    allModels.addAll(checkSSDReport.getWildCardValLblList());
    // MapCurve Type
    allModels.addAll(checkSSDReport.getMcvModelList());
    allModels.addAll(checkSSDReport.getMcVblkModelList());
    // ValueBlock Type
    allModels.addAll(checkSSDReport.getVblkModelList());
    allModels.addAll(checkSSDReport.getWildCardVblkLblList());

    for (ReportModel reportModel : allModels) {
      String labelName = reportModel.getLabelName();

      /*
       * ICDM-1460 - If any of the result is not ok , then add it to map , otherwise do not add [eg. AXIS_PTS[4] will
       * have one reportmodel for each of the 4 values.If any of these values are NOT OK then result should be
       * summarised as NOT OK
       */
      ReportModel existingRepModel = reportModelMap.get(labelName);
      if ((existingRepModel == null) || checkIfErrorOccured(reportModel)) {
        reportModelMap.put(labelName, reportModel);
      }
    }

    Map<String, CheckSSDResultParam> checkSSDMap =
        setCheckSSDMap(clonedCalDataMap, checkSSDResultParamMap, reportModelMap, null);

    if (isPrimaryRule) {
      this.reviewOutput.setPimaryChkSSDResParamMap(checkSSDMap);
    }
    else if (ruleSetData != null) {
      ruleSetData.setCheckSSDResParamMap(checkSSDMap);
    }
  }

  /**
   * @param ruleSetData
   * @return
   * @throws DataException
   */
  private String getCheckSSDFileSuffix(final ReviewRuleSetData ruleSetData) throws DataException {
    String suffix = "CommonRules";
    if (!this.reviewInputData.getRulesData().isCommonRulesPrimary()) {
      RuleSetLoader loader = new RuleSetLoader(getServiceData());
      RuleSet ruleSet = loader.getDataObjectByID(this.reviewInputData.getRulesData().getPrimaryRuleSetId());
      suffix = FileNameUtil.formatFileName(ruleSet.getName(), ApicConstants.SPL_CHAR_PTRN);
    }
    if (ruleSetData != null) {
      if ((ruleSetData.getSsdReleaseID() != null) || ruleSetData.isSSDFileReview()) {
        suffix = CDRConstants.SSD_RULES;
      }
      else {
        suffix = ruleSetData.getRuleSet() == null ? "CommonRules"
            : FileNameUtil.formatFileName(ruleSetData.getRuleSet().getName(), ApicConstants.SPL_CHAR_PTRN);
      }
    }
    return suffix;
  }

  /**
   * @param calDataMap data map
   * @param checkSSDResultParamMap CheckSSD data review result map
   * @param compliReportModelMap compli review result map
   * @param qssdReportModelMap qssd review result map
   * @return consolidated map
   */
  protected Map<String, CheckSSDResultParam> setCheckSSDMap(final Map<String, CalData> calDataMap,
      final Map<String, CheckSSDResultParam> checkSSDResultParamMap,
      final Map<String, ReportModel> compliReportModelMap, final Map<String, ReportModel> qssdReportModelMap) {

    for (Entry<String, CalData> cdEntry : calDataMap.entrySet()) {
      String labelName = cdEntry.getKey();
      CalData calData = cdEntry.getValue();
      calDataMap.get(labelName);
      ReportModel compliReportModel = null;
      if (compliReportModelMap != null) {
        compliReportModel = compliReportModelMap.get(labelName);
      }
      ReportModel qssdReportModel = null;
      if (qssdReportModelMap != null) {
        qssdReportModel = qssdReportModelMap.get(labelName);
      }
      CheckSSDResultParam checkSSDResultParam =
          new CheckSSDResultParam(labelName, calData, "", compliReportModel, qssdReportModel);
      checkSSDResultParamMap.put(labelName, checkSSDResultParam);
    }
    return checkSSDResultParamMap;
  }

  /**
   * @param reportModel
   * @param existingRepModel
   * @return
   */
  private boolean checkIfErrorOccured(final ReportModel reportModel) {
    // return false if result is OK
    return !reportModel.isRuleOk();
  }

  /**
   * @param checkSSDInfo
   * @param strTobeAdded
   * @param outputStrSet
   * @throws IcdmException
   */
  private void changeCheckSSDFileNames(final CheckSSDInfo checkSSDInfo, final String newFileName,
      final Set<String> outputStrSet)
      throws IcdmException {
    for (String filePath : checkSSDInfo.getOutPutFilesPath()) {
      if (filePath.endsWith(".xlsx")) {
        File checkSSDFile = new File(filePath);
        try {

          Path newFilePath = Files.move(checkSSDFile.toPath(),
              new File(filePath.replace(checkSSDFile.getName(), newFileName + ".xlsx")).toPath(),
              java.nio.file.StandardCopyOption.REPLACE_EXISTING);


          outputStrSet.remove(checkSSDFile.toString());
          outputStrSet.add(newFilePath.toString());
        }
        catch (IOException ex) {
          throw new IcdmException(ex.getLocalizedMessage(), ex);
        }
        break;
      }
    }
  }


  /**
   * @return the reviewInputData
   */
  protected ReviewInput getReviewInputData() {
    return this.reviewInputData;
  }

  /**
   * @return the reviewOutput
   */
  public ReviewedInfo getReviewOutput() {
    return this.reviewOutput;
  }
}
