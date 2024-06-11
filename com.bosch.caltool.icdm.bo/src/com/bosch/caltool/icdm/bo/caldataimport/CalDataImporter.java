/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.caldataimport;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.caldataphy2dcm.exception.CalDataPhy2DcmException;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.bo.cdr.CDRRuleUtil;
import com.bosch.caltool.icdm.bo.cdr.FeatureAttributeAdapterNew;
import com.bosch.caltool.icdm.bo.cdr.ReviewRuleAdapter;
import com.bosch.caltool.icdm.bo.cdr.RuleManagerFactory;
import com.bosch.caltool.icdm.bo.cdr.SSDServiceHandler;
import com.bosch.caltool.icdm.common.bo.a2l.RuleMaturityLevel;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportComparisonModel;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportData;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportSummary;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.FeatureValueModel;
import com.bosch.ssd.icdm.model.SSDCase;
import com.bosch.ssd.icdm.model.SSDConfigEnums.ParameterClass;
import com.bosch.ssd.icdm.model.SSDMessage;


/**
 * Imports the calibration data to SSD database.
 *
 * @author bne4cob
 */
public class CalDataImporter {

  /**
   * Stores the data related to import action
   */
  private CalDataImportData importData = new CalDataImportData();

  /**
   * Summary class, having details of import result
   */
  private final CalDataImportSummary importSummary = new CalDataImportSummary();

  /**
   * Loader class to get input parameter information
   */
  private final ParamCollection iCalDataImporterObject;

  /**
   * This logger is only for parser.
   */
  private final ILoggerAdapter parserLogger;

  /**
   * Import parameter details loader
   */
  private ICalDataImportParamDetailsLoader paramDetailsLoader;

  /**
   * function name (Present in case of Plausibel file)
   */
  private String funcName;

  private final ServiceData serviceData;


  /**
   * Constructor
   *
   * @param iCalDataImporterObject Loader to fetch the details of input parameters
   * @param parserLogger parser Logger
   * @param serviceData Service Data
   */
  public CalDataImporter(final ParamCollection iCalDataImporterObject, final ILoggerAdapter parserLogger,
      final ServiceData serviceData) {

    this.iCalDataImporterObject = iCalDataImporterObject;
    this.parserLogger = parserLogger;
    this.serviceData = serviceData;
  }

  /**
   * Set the input file. If the old file, if present, is different from the current file, this will also enable fetching
   * of the rule and other details
   *
   * @param inputfileStreamMap input file
   */
  public void setInputFile(final Map<String, InputStream> inputfileStreamMap) {
    this.importData.setFileNames(inputfileStreamMap.keySet());
  }


  private void readExistingRules(final ParamCollection paramCollection) throws IcdmException {
    SSDServiceHandler ssdServiceHandler = new SSDServiceHandler(this.serviceData);

    FeatureAttributeAdapterNew adapter = new FeatureAttributeAdapterNew(this.serviceData);

    AbstractImportRuleManager importRuleManager = getRuleImportManager(paramCollection);
    importRuleManager.readRules(ssdServiceHandler, this.importData, adapter, this.iCalDataImporterObject);
  }

  /**
   * @param paramCollection
   * @return
   */
  AbstractImportRuleManager getRuleImportManager(final ParamCollection paramCollection) throws IcdmException {
    AbstractImportRuleManager importRuleManager =
        new RuleManagerFactory(this.serviceData).createCalImportRuleManager(paramCollection);
    return importRuleManager;
  }

  /**
   * Returns true if there are rules are already existing
   *
   * @return true if data modification is present
   * @throws IcdmException any exception during retrieving existing rules
   */
  public boolean hasModifyingData(final ParamCollection paramCollection) throws IcdmException {
    loadParameterDetails(paramCollection);
    return !this.importData.getCalDataCompMap().isEmpty();
  }

  /**
   * This method gives the rules to be updated
   *
   * @param paramCollection
   * @throws IcdmException excep
   */
  public void loadParameterDetails(final ParamCollection paramCollection) throws IcdmException {
    // Prevent fetching rules from SSD multiple times
    if (!this.importData.isRulesRead()) {

      getImportLogger().debug("Loading parameter details and rules...");

      readExistingRules(paramCollection);
      createComparisonObjects();
      this.importData.setRulesRead(true);

      getImportLogger().debug("Loading parameter details and rules completed");
    }
  }

  /**
   * @return Logger for the import
   */
  protected final ILoggerAdapter getImportLogger() {
    return ObjectStore.getInstance().getLogger();

  }

  /**
   * Validates and reads the input file to get the cal data model
   *
   * @param ssdCase
   * @param parameterClass
   * @param inputfileStreamMap
   * @throws IcdmException exception if the param details loader fails
   * @throws DataException exception if the file is not valid
   */
  public void readInputFile(final ParameterClass parameterClass, final SSDCase ssdCase,
      final Map<String, InputStream> inputfileStreamMap)
      throws IcdmException {


    // ICDM-1892
    ParseFileIntoCDRRules parser =
        new ParseFileIntoCDRRules(this.parserLogger, inputfileStreamMap, ssdCase, parameterClass);

    parser.readFile();

    // ICDM-2352 display the repeated parameters
    if (CommonUtils.isNotEmpty(parser.getRepeatedParamNamesSet())) {
      MessageDialogUtils.getWarningMessageDialog("Parameters repeated",
          "The following parameters exists in more than one file. The first occurrence is taken for import.\n" +
              parser.getRepeatedParamNamesSet());
    }

    Set<String> parameters = parser.getParameters();

    // set the total no of parameters from the file
    this.importSummary.setTotalParamsInInput(parameters.size());

    this.funcName = parser.getFuncName();
    // Run paramter details loader and set the details to CalDataImportData instance
    getParamDetailsLoader().run(parameters, parser.getFuncName(), this.importData);

    parser.setParamDetails(getParamDetailsLoader().getParamNameType());

    this.importData.setParamFuncObjMap(getParamDetailsLoader().getParamFuncObjMap());
    this.importData.setTotalNoOfParams(parameters.size());

    this.importData.setParamNameObjMap(getParamDetailsLoader().getParamNameObjMap());

    this.importData.getParamDetMap().putAll(getParamDetailsLoader().getParamProps());
    this.importData.setParamNameTypeMap(getParamDetailsLoader().getParamNameType());
    this.importData.getInvalidParamSet().addAll(getParamDetailsLoader().getInvalidParams());

    this.importData.getInputDataMap().clear();
    Map<String, CDRRule> cdrRuleObjects = parser.getCDRRuleObjects();
    for (Entry<String, CDRRule> cdrRuleObjEntry : cdrRuleObjects.entrySet()) {
      String paramName = cdrRuleObjEntry.getKey();
      CDRRule rule = cdrRuleObjEntry.getValue();
      try {
        if (rule.getMaturityLevel() != null) {
          String icdmMaturityLevel = RuleMaturityLevel.getIcdmMaturityLvlFromImportFileTxt(rule.getMaturityLevel());
          rule.setMaturityLevel(RuleMaturityLevel.getSsdMaturityLevelText(icdmMaturityLevel));
        }
        this.importData.getInputDataMap().put(paramName,
            new ReviewRuleAdapter(this.serviceData).createReviewRule(rule));
      }
      catch (CalDataPhy2DcmException exp) {
        throw new IcdmException("Error while parsing file: " + exp.getLocalizedMessage(), exp);
      }
    }


    this.importData.setParamClasses(parser.getParamClasses());
    this.importData.setParamHints(parser.getParamHint());

  }


  /**
   * This method gives the rules to be updated
   *
   * @param paramCollection
   * @return the modifying data
   * @throws IcdmException excep
   */
  public List<CalDataImportComparisonModel> getModifyingData(final ParamCollection paramCollection)
      throws IcdmException {
    loadParameterDetails(paramCollection);
    List<CalDataImportComparisonModel> consolidatedCalImpCompList = new ArrayList<>();
    for (List<CalDataImportComparisonModel> list : this.importData.getCalDataCompMap().values()) {
      consolidatedCalImpCompList.addAll(list);
    }
    return consolidatedCalImpCompList;
  }


  /**
   * Creates instances of CalDataImportComparison for parameters, for which data is being updated
   *
   * @throws DataException
   */
  private void createComparisonObjects() throws DataException {
    for (String paramName : this.importData.getInputDataMap().keySet()) {

      ReviewRule reviewRule = this.importData.getInputDataMap().get(paramName);
      if (this.importData.getInvalidParamSet().contains(paramName)) {
        if (canAddParamsFromFile()) {
          // create a comparison object in case of ruleset

          CalDataImportComparisonModel calDataComparisonModel = new CalDataImportComparisonModel();

          calDataComparisonModel.setParamName(paramName);

          calDataComparisonModel.setParamType(this.importData.getParamNameTypeMap().get(paramName) == null
              ? reviewRule.getValueType() : this.importData.getParamNameTypeMap().get(paramName));

          calDataComparisonModel.setOldRule(null);

          CDRRule cdrRule = convertReviewRule(reviewRule);
          CDRRule copyRule = CDRRuleUtil.createCopy(cdrRule);
          calDataComparisonModel.setNewRule(convertCdrRule(copyRule));
          // set the maturity level here
          String maturityLvl = CalDataUtil.getStatus(cdrRule.getRefValueCalData());
          if (CommonUtils.isEmptyString(maturityLvl)) {
            reviewRule.setMaturityLevel(RuleMaturityLevel.getICDMMaturityLevelEnum(maturityLvl).getSSDMaturityLevel());
          }


          List<CalDataImportComparisonModel> paramCompList = this.importData.getCalDataCompMap().get(paramName);
          if (paramCompList == null) {
            paramCompList = new ArrayList<>();
            this.importData.getCalDataCompMap().put(paramName, paramCompList);
          }
          paramCompList.add(calDataComparisonModel);
        }
        else {
          createCompObjWithoutRule(paramName, reviewRule);
          // Skip all invalid parameters
          continue;
        }
      }

      createCompObjWithoutRule(paramName, reviewRule);


    }
  }

  /**
   * @param paramName
   * @param reviewRule
   * @throws DataException
   */
  private void createCompObjWithoutRule(final String paramName, final ReviewRule reviewRule) throws DataException {
    if ((this.importData.getExistingSSDRuleListMap().get(paramName) != null) &&
        !this.importData.getExistingSSDRuleListMap().get(paramName).isEmpty()) {
      createCompObjListForParamWithRules(paramName, reviewRule);
    }
  }


  private boolean canAddParamsFromFile() {
    return (this.iCalDataImporterObject instanceof RuleSet);
  }


  /**
   * @param reviewRule
   * @return
   * @throws DataException
   */
  public CDRRule convertReviewRule(final ReviewRule reviewRule) throws DataException {
    return new ReviewRuleAdapter(this.serviceData).createCdrRule(reviewRule);
  }

  /**
   * @param reviewRule
   * @return
   * @throws DataException
   */
  public ReviewRule convertCdrRule(final CDRRule cdrRule) throws DataException {
    return new ReviewRuleAdapter(this.serviceData).createReviewRule(cdrRule);
  }

  /**
   * @param paramName String
   * @param reviewRule CDRRule
   * @throws DataException
   */
  private void createCompObjListForParamWithRules(final String paramName, final ReviewRule reviewRule)
      throws DataException {
    for (ReviewRule existingRule : this.importData.getExistingSSDRuleListMap().get(paramName)) {
      if (null != existingRule) {

        CalDataImportComparisonModel calDataComparisonModel = new CalDataImportComparisonModel();


        calDataComparisonModel.setParamName(paramName);
        calDataComparisonModel.setParamType(this.importData.getParamNameTypeMap().get(paramName));
        String funcName = this.importData.getParamFuncMap().get(paramName);
        if (funcName != null) {
          calDataComparisonModel.setFuncNames(funcName);
        }
        calDataComparisonModel.setDependencyList(new TreeSet<>(existingRule.getDependencyList()));

        calDataComparisonModel.setOldRule(existingRule);
        CDRRule cdrRule = convertReviewRule(reviewRule);
        CDRRule copyRule = CDRRuleUtil.createCopy(cdrRule);
        calDataComparisonModel.setNewRule(convertCdrRule(copyRule));

        // set the maturity level here

        String maturityLvl = CalDataUtil.getStatus(cdrRule.getRefValueCalData());
        if (CommonUtils.isEmptyString(maturityLvl)) {
          reviewRule.setMaturityLevel(RuleMaturityLevel.getICDMMaturityLevelEnum(maturityLvl).getSSDMaturityLevel());
        }
        List<CalDataImportComparisonModel> paramCompList = this.importData.getCalDataCompMap().get(paramName);
        if (paramCompList == null) {
          paramCompList = new ArrayList<>();
          this.importData.getCalDataCompMap().put(paramName, paramCompList);
        }
        paramCompList.add(calDataComparisonModel);

      }
    }
  }


  /**
   * Imports Caldata to SSD database
   *
   * @return import summary
   * @throws IcdmException exception during import
   */
  public CalDataImportSummary importCalData(final ParamCollection paramCollection) throws IcdmException {
    loadParameterDetails(paramCollection);

    Map<CDRRule, SSDMessage> createMsg = null;
    Map<CDRRule, SSDMessage> updateMsg = null;

    List<CDRRule> listOfRulesCreated = null;
    AbstractImportRuleManager importRuleManager =
        new RuleManagerFactory(this.serviceData).createCalImportRuleManager(paramCollection);
    if (hasRulesToCreate(this.importData)) {
      listOfRulesCreated = new ArrayList<>();

      createMsg = importRuleManager.createRules(this.iCalDataImporterObject, this.importData, listOfRulesCreated);
    }
    if (hasRulesToUpdate(this.importData)) {

      updateMsg = importRuleManager.updateRules(this.iCalDataImporterObject, getModifyingData(paramCollection), this);
    }

    setStatusMsg(createMsg, updateMsg, listOfRulesCreated, paramCollection);
    this.importSummary.setTotalParamsInInput(this.importData.getTotalNoOfParams());
    return this.importSummary;
  }


  /**
   * @return true, if rules to be created
   */
  public boolean hasRulesToCreate(final CalDataImportData importData) {
    for (String paramName : importData.getCalDataCompMap().keySet()) {
      for (CalDataImportComparisonModel compObj : importData.getCalDataCompMap().get(paramName)) {
        if ((compObj.getOldRule() == null) && compObj.isUpdateInDB()) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Retruns true if there are any rules to be updated
   *
   * @return true, if rules are present to be updated in DB, as per the user selection
   */
  private boolean hasRulesToUpdate(final CalDataImportData importData) {
    for (String paramName : importData.getCalDataCompMap().keySet()) {
      for (CalDataImportComparisonModel compObj : importData.getCalDataCompMap().get(paramName)) {
        if ((compObj.getOldRule() != null) && compObj.isUpdateInDB()) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Sets the status msg for updation/deletion
   *
   * @param createMsg created ssdmsg
   * @param updateMsg updated ssdmsg
   * @param listOfRulesCreated
   * @param paramCollection
   * @throws IcdmException excep
   */
  private void setStatusMsg(final Map<CDRRule, SSDMessage> createMsg, final Map<CDRRule, SSDMessage> updateMsg,
      final List<CDRRule> listOfRulesCreated, final ParamCollection paramCollection)
      throws IcdmException {
    StringBuilder msg = new StringBuilder();
    // add the param name for which the rule created
    setCreatedStatus(createMsg, msg, listOfRulesCreated);

    // add the param name for which the rule created
    setUpdatedStatus(updateMsg, msg, paramCollection);
    // Import not done
    if ((createMsg == null) && (updateMsg == null)) {
      msg.append(this.importSummary.getMessage());
    }
    this.importSummary.getParamInvalidSet().addAll(this.importData.getInvalidParamSet());
    this.importSummary.setMessage(msg.toString());
    int rulesSkippedCount = 0;
    for (CalDataImportComparisonModel rulesSet : getModifyingData(paramCollection)) {
      if (!rulesSet.isUpdateInDB()) {
        rulesSkippedCount++;
      }
    }
    this.importSummary.setSkippedParamsCount(rulesSkippedCount);
  }

  /**
   * Sets the created msg text
   *
   * @param createMsg created ssdmsg
   * @param msg msg
   * @param listOfRulesCreated
   */
  private void setCreatedStatus(final Map<CDRRule, SSDMessage> createMsg, final StringBuilder msg,
      final List<CDRRule> listOfRulesCreated) {
    // ICDM-2097
    if (CommonUtils.isNotEmpty(createMsg)) {
      msg.append("Rules creation failed for the rules:\n");
      for (CDRRule rule : createMsg.keySet()) {
        if (rule == null) {
          msg.append("Component package creation failed");
        }
        else {
          String paramName = rule.getParameterName();
          // add the param name to the invlid param set
          this.importSummary.getParamInvalidSet().add(paramName);
          msg.append(paramName);
          String dependencyStr = createAttrVal(rule.getDependencyList());
          if (CommonUtils.isNotEmptyString(dependencyStr)) {
            msg.append(":");
            msg.append(dependencyStr);
          }
          msg.append("-");
          msg.append(createMsg.get(rule).getDescription());
          msg.append("\n");
        }
      }
      msg.append("\n\n");
    }
    else {
      // if there is no invalid rules
      if (CommonUtils.isNotEmpty(listOfRulesCreated)) {
        msg.append("Rules created successfully");
        msg.append("\n\n");
      }
    }
    if (CommonUtils.isNotEmpty(listOfRulesCreated)) {
      addCreatedRulesToSummary(createMsg, listOfRulesCreated);
    }

  }

  private String createAttrVal(final List<FeatureValueModel> features) {
    String result = "";
    SortedSet<AttributeValueModel> attrValSet = new TreeSet<AttributeValueModel>();
    Set<FeatureValueModel> featureSet = new HashSet<FeatureValueModel>(features);
    FeatureAttributeAdapterNew adapter = new FeatureAttributeAdapterNew(this.serviceData);
    try {
      attrValSet.addAll(adapter.createAttrValModel(featureSet).values());
    }
    catch (IcdmException exception) {
      ObjectStore.getInstance().getLogger().error(exception.getLocalizedMessage(), exception);
      return result;
    }
    result = CDRRuleUtil.getAttrValString(attrValSet);
    return result;
  }


  /**
   * @param createMsg Map<CDRRule, SSDMessage>
   * @param listOfRulesCreated List<CDRRule>
   */
  private void addCreatedRulesToSummary(final Map<CDRRule, SSDMessage> createMsg,
      final List<CDRRule> listOfRulesCreated) {
    if (!(ApicConstants.COMP_PKG.equals(getObjectTypeName(this.iCalDataImporterObject))) || createMsg.isEmpty()) {
      // if it is not a comp pkg, or if all the rules are inserted without any failure
      for (CDRRule cdrRule : listOfRulesCreated) {
        if (!createMsg.containsKey(cdrRule)) {
          this.importSummary.getParamRulesCreatedSet().add(cdrRule.getRuleId().toString());
        }
      }
    }
  }


  /**
   * @param paramCol paramCol
   * @return the object type name
   */
  private String getObjectTypeName(final ParamCollection paramCol) {
    if (paramCol instanceof Function) {
      return ApicConstants.FUNCTION;
    }
    if (paramCol instanceof RuleSet) {
      return ApicConstants.RULE_SET_NODE_TYPE;
    }
    return ApicConstants.COMP_PKG;
  }


  /**
   * Sets the updated msg text
   *
   * @param updateMsg updated ssdmsg
   * @param msg msg
   * @param paramCollection
   * @throws IcdmException exep
   */
  private void setUpdatedStatus(final Map<CDRRule, SSDMessage> updateMsg, final StringBuilder msg,
      final ParamCollection paramCollection)
      throws IcdmException {
    // ICDM-2097
    if (CommonUtils.isNotEmpty(updateMsg)) {
      msg.append("Rules updation failed for the rules:\n");
      for (CDRRule rule : updateMsg.keySet()) {
        String paramName = rule.getParameterName();
        // add the param name to the invlid param set
        this.importSummary.getParamInvalidSet().add(paramName);
        msg.append(paramName);
        String dependencyStr = createAttrVal(rule.getDependencyList());
        if (CommonUtils.isNotEmptyString(dependencyStr)) {
          msg.append(":");
          msg.append(dependencyStr);
        }
        msg.append("-");
        msg.append(updateMsg.get(rule).getDescription());
        msg.append("\n");
      }
    }
    else {
      // if there is no invalid rules
      if (hasRulesToUpdate(this.importData)) {
        msg.append("Rules updated successfully");
      }
    }
    if (null != getModifyingData(paramCollection)) {
      for (CalDataImportComparisonModel calDataComp : getModifyingData(paramCollection)) {
        if (calDataComp.isUpdateInDB() && (calDataComp.getOldRule() != null) &&
            !updateMsg.containsKey(calDataComp.getOldRule())) {
          this.importSummary.getParamRulesUpdatedSet().add(calDataComp.getOldRule().getRuleId().toString());
        }
      }
    }
  }

  /**
   * Returns the summary of caldata import. If invoked before calling <code>importCalData()</code>, the method returns
   * null
   *
   * @return the summary of caldata import
   */
  public CalDataImportSummary getImportSummary() {
    return this.importSummary;
  }

  /**
   * @return input data, parsed from the file
   */
  public ConcurrentHashMap<String, ReviewRule> getInputData() {
    return new ConcurrentHashMap<>(this.importData.getInputDataMap());
  }

  /**
   * @return the paramDetailsLoader
   */
  public ICalDataImportParamDetailsLoader getParamDetailsLoader() {
    return this.paramDetailsLoader;
  }


  /**
   * @param paramDetailsLoader the paramDetailsLoader to set
   */
  public void setParamDetailsLoader(final ICalDataImportParamDetailsLoader paramDetailsLoader) {
    this.paramDetailsLoader = paramDetailsLoader;
  }

  /**
   * @return the importData
   */
  public CalDataImportData getImportData() {
    return this.importData;
  }


  /**
   * @return the funcName
   */
  public String getFuncName() {
    return this.funcName;
  }


  /**
   * @param importData the importData to set
   */
  public void setImportData(final CalDataImportData importData) {
    this.importData = importData;
  }

}
