/*
 * /' * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.caldataimport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.cdr.CDRRuleUtil;
import com.bosch.caltool.icdm.bo.cdr.FeatureAttributeAdapterNew;
import com.bosch.caltool.icdm.bo.cdr.ReviewRuleAdapter;
import com.bosch.caltool.icdm.bo.cdr.RuleRemarkMasterCommand;
import com.bosch.caltool.icdm.bo.cdr.SSDServiceHandler;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportComparisonModel;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportData;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.FeatureValueModel;
import com.bosch.ssd.icdm.model.SSDCase;
import com.bosch.ssd.icdm.model.SSDConfigEnums.ParameterClass;
import com.bosch.ssd.icdm.model.SSDMessage;


/**
 * Abstract class which manages rule related to ICalDataImporterObject
 *
 * @author jvi6cob
 */
public abstract class AbstractImportRuleManager extends AbstractSimpleBusinessObject {


  private final SSDServiceHandler ssdServiceHandler;


  private ParameterClass paramClass;

  private SSDCase ssdCase;


  /**
   * @param serviceData serviceData
   * @param ssdServiceHandler SSD service handler
   */
  protected AbstractImportRuleManager(final ServiceData serviceData, final SSDServiceHandler ssdServiceHandler) {
    super(serviceData);
    this.ssdServiceHandler = ssdServiceHandler;
  }

  /**
   * Method used to READ rules from SSD
   *
   * @param ssdServiceHandler SSDServiceHandler
   * @param importData CalDataImportData
   * @param adapter FeatureAttributeAdapter
   * @param calDataImporterObject ICalDataImporterObject
   * @throws IcdmException Exception thrown from SSD
   */
  public abstract void readRules(final SSDServiceHandler ssdServiceHandler, final CalDataImportData importData,
      final FeatureAttributeAdapterNew adapter, final ParamCollection calDataImporterObject)
      throws IcdmException;

  /**
   * Method used to CREATE rules in SSD
   *
   * @param calDataImporterObject ICalDataImporterObject
   * @param importData CalDataImportData
   * @param listOfRulesCreated List<CDRRule>
   * @return SSDMessage
   * @throws IcdmException Exception thrown from SSD
   */
  public abstract Map<CDRRule, SSDMessage> createRules(final ParamCollection calDataImporterObject,
      final CalDataImportData importData, final List<CDRRule> listOfRulesCreated)
      throws IcdmException;

  /**
   * Method used to CREATE rule objects. </br>
   * This method fills the newRuleList
   *
   * @param importData CalDataImportData
   * @param calDataImporterObject ICalDataImporterObject
   * @param newRuleList List<CDRRule>
   */
  protected abstract void createRuleObjects(final CalDataImportData importData,
      final ParamCollection calDataImporterObject, final List<CDRRule> newRuleList)
      throws DataException;


  /**
   * Method used to UPDATE rules in SSD
   *
   * @param dataProvider ApicDataProvider
   * @param calDataImporterObject ICalDataImporterObject
   * @param modifyingData SortedSet<CalDataImportComparison>
   * @param calDataImporter CalDataImporter
   * @return SSDMessage
   * @throws IcdmException Exception thrown from SSD
   */
  public abstract Map<CDRRule, SSDMessage> updateRules(final ParamCollection calDataImporterObject,
      final List<CalDataImportComparisonModel> modifyingData, CalDataImporter calDataImporter)
      throws IcdmException;

  /**
   * Update the details of existing CDRRule objects
   *
   * @param calDataImporterObject ICalDataImporterObject
   * @param modifyingData SortedSet<CalDataImportComparison>
   * @param importer TODO
   * @return list of updated rules
   * @throws IcdmException Exception thrown from SSD
   */
  protected List<CDRRule> updateRuleObjects(final ParamCollection calDataImporterObject,
      final List<CalDataImportComparisonModel> modifyingData, final CalDataImporter importer)
      throws IcdmException {

    List<CDRRule> updatedRuleList = new ArrayList<>();
    for (CalDataImportComparisonModel calDataComp : modifyingData) {
      if ((calDataComp.getOldRule() != null) && calDataComp.isUpdateInDB()) {
        CDRRule rule = convertReviewRule(calDataComp.getOldRule());
        // ICDM-1893
        CDRRule newRule = convertReviewRule(calDataComp.getNewRule());
        if (calDataComp.isUseNewRefVal()) { // change ref values only if the check box is checked
          rule.setRefValCalData(newRule.getRefValueCalData());
          rule.setRefValue(newRule.getRefValue());
        }
        if (calDataComp.isUseNewMaturityLvl()) {// change value based on check box value
          rule.setMaturityLevel(newRule.getMaturityLevel());
        }
        if (calDataComp.isUseNewUnit()) {// change value based on check box value
          rule.setUnit(CommonUtils.checkNull(newRule.getUnit()));
        }
        rule.setHint(CommonUtils.checkNull(newRule.getHint()));
        rule.setSsdCase(getSsdCase());
        rule.setParamClass(getParamClass());

        if (calDataComp.isUseNewRvwMtd()) {// change value based on check box value
          rule.setReviewMethod(CommonUtils.checkNull(newRule.getReviewMethod()));
        }
        rule.setDcm2ssd(newRule.isDcm2ssd());
        if (newRule.isDcm2ssd()) {
          // ICDM-1927
          // for Exact Match, upper and lower limits are made as null
          rule.setLowerLimit(null);
          rule.setUpperLimit(null);
        }
        else {
          setLimitValues(importer, calDataComp, rule, newRule);
        }
        updatedRuleList.add(rule);
      }
    }
    return updatedRuleList;
  }

  /**
   * Update the details of existing CDRRule objects
   *
   * @param calDataImporterObject ICalDataImporterObject
   * @param modifyingData SortedSet<CalDataImportComparison>
   * @param importer TODO
   * @param unicodeUpdateMap rules with unicode remarks
   * @param paramNames parameter names
   * @return list of updated rules
   * @throws IcdmException Exception thrown from SSD
   */
  protected List<CDRRule> updateRuleObjectsUnicode(final ParamCollection calDataImporterObject,
      final List<CalDataImportComparisonModel> modifyingData, final CalDataImporter importer,
      final Map<CDRRule, ReviewRule> unicodeUpdateMap, final List<String> paramNames)
      throws IcdmException {

    List<CDRRule> updatedRuleList = new ArrayList<>();
    for (CalDataImportComparisonModel calDataComp : modifyingData) {
      if ((calDataComp.getOldRule() != null) && calDataComp.isUpdateInDB()) {
        CDRRule rule = convertReviewRule(calDataComp.getOldRule());
        ReviewRule newRevRule = calDataComp.getOldRule();
        // ICDM-1893
        CDRRule newRule = convertReviewRule(calDataComp.getNewRule());
        if (calDataComp.isUseNewRefVal()) { // change ref values only if the check box is checked
          rule.setRefValCalData(newRule.getRefValueCalData());
          rule.setRefValue(newRule.getRefValue());
        }
        if (calDataComp.isUseNewMaturityLvl()) {// change value based on check box value
          rule.setMaturityLevel(newRule.getMaturityLevel());
        }
        if (calDataComp.isUseNewUnit()) {// change value based on check box value
          rule.setUnit(CommonUtils.checkNull(newRule.getUnit()));
        }
        if ((null != calDataComp.getNewRule().getUnicodeRemarks()) &&
            !CommonUtils.isEqual(rule.getHint(), calDataComp.getNewRule().getUnicodeRemarks())) {
          unicodeUpdateMap.put(rule, calDataComp.getNewRule());
          newRevRule.setUnicodeRemarks(calDataComp.getNewRule().getUnicodeRemarks());
        }
        rule.setHint(CommonUtils.checkNull(newRule.getHint()));
        rule.setSsdCase(getSsdCase());
        rule.setParamClass(getParamClass());

        if (calDataComp.isUseNewRvwMtd()) {// change value based on check box value
          rule.setReviewMethod(CommonUtils.checkNull(newRule.getReviewMethod()));
        }
        rule.setDcm2ssd(newRule.isDcm2ssd());
        if (newRule.isDcm2ssd()) {
          // ICDM-1927
          // for Exact Match, upper and lower limits are made as null
          rule.setLowerLimit(null);
          rule.setUpperLimit(null);
        }
        else {
          setLimitValues(importer, calDataComp, rule, newRule);
        }
        paramNames.add(rule.getParameterName());
        updatedRuleList.add(rule);
      }
    }
    return updatedRuleList;
  }

  /**
   * @param listOfRulesToBeCreated rules created
   * @param newCdrMapUpdated parameter,new cdr rules map
   * @param createMsgMap rules for which rule creation failed
   * @throws IcdmException exception
   */
  public void createUnicodeRemarksWhileImport(final List<CDRRule> listOfRulesToBeCreated,
      final Map<String, List<CDRRule>> newCdrMapUpdated, final Map<CDRRule, SSDMessage> createMsgMap)
      throws IcdmException {
    ReviewRuleAdapter adapter = new ReviewRuleAdapter(getServiceData());
    Map<CDRRule, ReviewRule> unicodeCdrRevRuleMap = new HashMap<>();
    for (Entry<String, List<CDRRule>> cdrEntry : newCdrMapUpdated.entrySet()) {
      for (CDRRule newCdr : cdrEntry.getValue()) {
        if (!createMsgMap.containsKey(newCdr)) {
          fillUnicodeRmrksMap(listOfRulesToBeCreated, adapter, unicodeCdrRevRuleMap, newCdr);
        }
      }
    }
    RuleRemarkMasterCommand ruleRmkMasterCmd = new RuleRemarkMasterCommand(getServiceData(), unicodeCdrRevRuleMap);
    getServiceData().getCommandExecutor().execute(ruleRmkMasterCmd);
  }

  /**
   * @param listOfRulesToBeCreated
   * @param adapter
   * @param unicodeCdrRevRuleMap
   * @param newCdr
   * @throws DataException
   */
  private void fillUnicodeRmrksMap(final List<CDRRule> listOfRulesToBeCreated, final ReviewRuleAdapter adapter,
      final Map<CDRRule, ReviewRule> unicodeCdrRevRuleMap, final CDRRule newCdr)
      throws DataException {
    for (CDRRule cdrObjForRevRuleToCreate : listOfRulesToBeCreated) {
      if (CDRRuleUtil.isSameRuleRecord(newCdr, cdrObjForRevRuleToCreate) && (null != newCdr.getHint()) &&
          !newCdr.getHint().isEmpty()) {
        ReviewRule newRevRule = adapter.createReviewRule(newCdr);
        newRevRule.setUnicodeRemarks(cdrObjForRevRuleToCreate.getHint());
        unicodeCdrRevRuleMap.put(newCdr, newRevRule);
      }
    }
  }

  /**
   * @param unicodeUpdateMap rules for which unicode remarks should be updated
   * @param newCdrMapUpdated map of new cdr rules created
   * @param cdrRuleSsdMsgMap ssd messages for rules for which rule creation has failed
   * @throws IcdmException exception
   */
  public void updateUnicodeRemarksWhileImport(final Map<CDRRule, ReviewRule> unicodeUpdateMap,
      final Map<String, List<CDRRule>> newCdrMapUpdated, final Map<CDRRule, SSDMessage> cdrRuleSsdMsgMap)
      throws IcdmException {
    ReviewRuleAdapter adapter = new ReviewRuleAdapter(getServiceData());
    Map<CDRRule, ReviewRule> unicodeCdrRevRuleMap = new HashMap<>();
    for (Entry<CDRRule, ReviewRule> cdrRevRuleEntry : unicodeUpdateMap.entrySet()) {
      if (!cdrRuleSsdMsgMap.containsKey(cdrRevRuleEntry.getKey())) {
        List<CDRRule> updatedCdrList = newCdrMapUpdated.get(cdrRevRuleEntry.getKey().getParameterName());
        for (CDRRule cdrRule : updatedCdrList) {
          if (cdrRule.getRuleId().equals(cdrRevRuleEntry.getKey().getRuleId())) {
            ReviewRule newRevRule = adapter.createReviewRule(cdrRule);
            newRevRule.setUnicodeRemarks(cdrRevRuleEntry.getValue().getUnicodeRemarks());
            unicodeCdrRevRuleMap.put(cdrRule, newRevRule);
          }
        }
      }
    }
    RuleRemarkMasterCommand ruleRmkMasterCmd = new RuleRemarkMasterCommand(getServiceData(), unicodeCdrRevRuleMap);
    getServiceData().getCommandExecutor().execute(ruleRmkMasterCmd);
  }


  /**
   * @param importer
   * @param calDataComp
   * @param rule
   * @param newRule
   */
  private void setLimitValues(final CalDataImporter importer, final CalDataImportComparisonModel calDataComp,
      final CDRRule rule, final CDRRule newRule) {
    String bitwiseRule =
        importer.getImportData().getParamDetMap().get(rule.getParameterName()).get(CDRConstants.CDIKEY_BIT_WISE);

    if ((bitwiseRule == null) || CommonUtils.isEqual(bitwiseRule, ApicConstants.CODE_NO)) {
      // store the values only if it is not a bitwise rule
      // if not exact match
      if (calDataComp.isUseNewLowLmt()) {// change value based on check box value
        rule.setLowerLimit(newRule.getLowerLimit());
      }
      if (calDataComp.isUseNewUpLmt()) {// change value based on check box value
        rule.setUpperLimit(newRule.getUpperLimit());
      }
    }
  }


  /**
   * Method which initialises CDRRule object
   *
   * @param importData
   * @param newRuleList
   * @param paramName
   * @throws DataException
   */
  public void createRules(final CalDataImportData importData, final List<CDRRule> newRuleList, final String paramName)
      throws DataException {
    Map<String, Map<Integer, Set<AttributeValueModel>>> commonCombiFeatureModel =
        importData.getMappedObjCombiAttrValModelMap();

    // CompPack
    Map<Integer, Set<AttributeValueModel>> featureValueCombinations = commonCombiFeatureModel.get(paramName);
    List<CalDataImportComparisonModel> comObjList = importData.getCalDataCompMap().get(paramName);
    int index = 0;
    if ((null == featureValueCombinations) || featureValueCombinations.isEmpty()) {

      Set<AttributeValueModel> depSet = new HashSet<>();
      createRule(importData, newRuleList, paramName, depSet, comObjList.get(0));
    }
    else {
      for (Set<AttributeValueModel> depSet : featureValueCombinations.values()) {
        // list is not needed. for one param one caldatacomp obj only will be there
        CalDataImportComparisonModel selCompObj = comObjList.get(index);
        createRule(importData, newRuleList, paramName, depSet, selCompObj);
        index++;
      }
    }
  }

  /**
   * @param importData
   * @param newRuleList
   * @param paramName
   * @param dependList
   * @param selCompObj
   * @return
   * @throws DataException
   */
  private void createRule(final CalDataImportData importData, final List<CDRRule> newRuleList, final String paramName,
      final Set<AttributeValueModel> dependList, final CalDataImportComparisonModel selCompObj)
      throws DataException {
    if ((selCompObj.getOldRule() == null) && selCompObj.isUpdateInDB()) {


      // ICDM-1893
      CDRRule newRule = convertReviewRule(selCompObj.getNewRule());

      FeatureAttributeAdapterNew apdapter = new FeatureAttributeAdapterNew(getServiceData());

      List<FeatureValueModel> feaValModelList = apdapter.getFeaValModelList(dependList);

      newRule.setDependencyList(feaValModelList);
      // change the values based on check box selection
      if (newRule.isDcm2ssd() || !selCompObj.isUseNewLowLmt()) {
        // ICDM-1927
        // if exact match or use check box not checked
        newRule.setLowerLimit(null);
      }
      if (newRule.isDcm2ssd() || !selCompObj.isUseNewUpLmt()) {
        // icdm-1927
        // if exact match or use check box not checked
        newRule.setUpperLimit(null);
      }
      if (!selCompObj.isUseNewMaturityLvl()) {
        newRule.setMaturityLevel(null);
      }
      if (!selCompObj.isUseNewUnit()) {
        newRule.setUnit(null);
      }
      if (!selCompObj.isUseNewRvwMtd()) {
        newRule.setReviewMethod("M");
      }
      if (!selCompObj.isUseNewRefVal()) { // change ref values only if the check box is checked
        newRule.setRefValCalData(null);
        newRule.setRefValue(null);
      }
      // label function is got from the param props map
      newRule.setLabelFunction(importData.getParamDetMap().get(paramName).get(CDRConstants.CDIKEY_FUNCTION_NAME));
      newRuleList.add(newRule);

    }
  }


  /**
   * Method to convert AttributeValueModel to FeatureValueModel
   *
   * @param combiAttrValModel Map<Integer, Set<FeatureValueModel>>
   * @param adapter FeatureAttributeAdapter
   * @return Map<Integer, Set<FeatureValueModel>>
   * @throws IcdmException Exception from SSD
   */
  protected final Map<Integer, Set<FeatureValueModel>> attrValToFeatureValCombi(
      final Map<Integer, Set<AttributeValueModel>> combiAttrValModel, final FeatureAttributeAdapterNew adapter)
      throws IcdmException {
    Map<Integer, Set<FeatureValueModel>> combiFeaValModMap = new HashMap<>();
    for (Entry<Integer, Set<AttributeValueModel>> combiAttrValModelEntry : combiAttrValModel.entrySet()) {
      combiFeaValModMap.put(combiAttrValModelEntry.getKey(),
          new HashSet<>(adapter.createFeaValModel(combiAttrValModelEntry.getValue()).values()));
    }
    return combiFeaValModMap;
  }

  /**
   * Method which returns a rule which does not have a feature value model
   *
   * @param ssdAllRuleMap Map<String, List<CDRRule>>
   * @param params List<String>
   * @return Map<String, List<CDRRule>>
   */
  protected final Map<String, List<CDRRule>> consolidateRulesWithoutDependencies(
      final Map<String, List<CDRRule>> ssdAllRuleMap, final List<String> params) {
    Map<String, List<CDRRule>> consolidatedSSDRuleMap = new HashMap<>(); // This collection is synonymous to individual
    // call to SSD without combination
    for (String paramName : params) {
      Set<CDRRule> cdrRulesSet = new TreeSet<>(new Comparator<CDRRule>() {

        @Override
        public int compare(final CDRRule rule1, final CDRRule rule2) {
          return rule1.getRuleId().compareTo(rule2.getRuleId());
        }
      });
      List<CDRRule> allRuleList = ssdAllRuleMap.get(paramName);
      if (allRuleList != null) {
        addToRulesSet(cdrRulesSet, allRuleList);
      }
      if (!cdrRulesSet.isEmpty()) {
        List<CDRRule> cdrRuleList;
        cdrRuleList = new ArrayList<>(cdrRulesSet);
        consolidatedSSDRuleMap.put(paramName, cdrRuleList);
      }
    }
    return consolidatedSSDRuleMap;
  }

  /**
   * @param cdrRulesSet
   * @param allRuleList
   */
  private void addToRulesSet(final Set<CDRRule> cdrRulesSet, final List<CDRRule> allRuleList) {
    for (CDRRule cdrRule : allRuleList) {
      List<FeatureValueModel> dependencyList = cdrRule.getDependencyList(); // 3
      if ((dependencyList == null) || (dependencyList.size() == 0)) {
        // Code reaching here indicates matching rule found
        cdrRulesSet.add(cdrRule);
      }
    }
  }


  /**
   * Method which returns rules which matches the specified feature value model
   *
   * @param ssdAllRuleMap Map<String, List<CDRRule>>
   * @param feaValCombiModels Set<FeatureValueModel>
   * @param params List<String>
   * @return Map<String, List<CDRRule>>
   */
  protected final Map<String, List<CDRRule>> consolidateRules(final Map<String, List<CDRRule>> ssdAllRuleMap,
      final Set<FeatureValueModel> feaValCombiModels, final List<String> params) {
    Map<String, List<CDRRule>> consolidatedSSDRuleMap = new HashMap<>(); // This collection is synonymous to individual
    // call to SSD with particular combination
    for (String paramName : params) {
      Set<CDRRule> cdrRulesSet = new TreeSet<>(new Comparator<CDRRule>() {

        @Override
        public int compare(final CDRRule rule1, final CDRRule rule2) {
          return rule1.getRuleId().compareTo(rule2.getRuleId());
        }
      });
      List<CDRRule> allRuleList = ssdAllRuleMap.get(paramName);
      if (allRuleList != null) {
        ruleLevel: for (CDRRule cdrRule : allRuleList) {
          List<FeatureValueModel> dependencyList = cdrRule.getDependencyList(); // 3
          addToRuleSetIfMatchFound(feaValCombiModels, cdrRulesSet, cdrRule, dependencyList);
        }
      }
      if (!cdrRulesSet.isEmpty()) {
        List<CDRRule> cdrRuleList;
        cdrRuleList = new ArrayList<>(cdrRulesSet);
        consolidatedSSDRuleMap.put(paramName, cdrRuleList);
      }
    }
    return consolidatedSSDRuleMap;
  }

  /**
   * @param feaValCombiModels
   * @param cdrRulesSet
   * @param cdrRule
   * @param dependencyList
   */
  private void addToRuleSetIfMatchFound(final Set<FeatureValueModel> feaValCombiModels, final Set<CDRRule> cdrRulesSet,
      final CDRRule cdrRule, final List<FeatureValueModel> dependencyList) {
    if (dependencyList.size() == feaValCombiModels.size()) {
      for (FeatureValueModel feaValModel : feaValCombiModels) { // 3
        if (!checkIfPresentInRule(feaValModel, dependencyList)) {
          return;
        }
      }
      // Code reaching here indicates matching rule found
      cdrRulesSet.add(cdrRule);
    }
  }

  /**
   * @param feaValModel
   * @param dependencyList
   */
  private boolean checkIfPresentInRule(final FeatureValueModel feaValModel,
      final List<FeatureValueModel> dependencyList) {
    for (FeatureValueModel featureValueModel : dependencyList) {
      if (isFeaValEqual(featureValueModel, feaValModel)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param feaValModel
   * @param allFeaValModel
   * @return
   */
  private boolean isFeaValEqual(final FeatureValueModel feaValModel1, final FeatureValueModel feaValModel2) {
    BigDecimal featureId1 = feaValModel1.getFeatureId();
    BigDecimal featureId2 = feaValModel2.getFeatureId();
    BigDecimal valueId1 = feaValModel1.getValueId();
    BigDecimal valueId2 = feaValModel2.getValueId();
    return CommonUtils.isEqual(featureId1, featureId2) && CommonUtils.isEqual(valueId1, valueId2);
  }

  /**
   * updates the parameter properties
   *
   * @param mapWithNewParamProps map with new parameter properties
   * @param paramNameType parameter name and type
   * @param classMap Map<String, String>
   * @param caldataCompMap Map<String, List<CalDataImportComparison>>
   */
  public abstract List<AbstractCommand> updateParamProps(Map<String, Map<String, String>> mapWithNewParamProps,
      Map<String, String> paramNameType, Map<String, String> classMap,
      Map<String, List<CalDataImportComparisonModel>> caldataCompMap)
      throws IcdmException;

  /**
   * @param reviewRule
   * @return
   * @throws DataException
   */
  public CDRRule convertReviewRule(final ReviewRule reviewRule) throws DataException {
    return new ReviewRuleAdapter(getServiceData()).createCdrRule(reviewRule);
  }

  /**
   * @param reviewRule
   * @return
   * @throws DataException
   */
  public ReviewRule convertCdrRule(final CDRRule cdrRule) throws DataException {
    return new ReviewRuleAdapter(getServiceData()).createReviewRule(cdrRule);
  }

  /**
   * @return
   */
  public SSDServiceHandler getServiceHandler() {
    return this.ssdServiceHandler;
  }


  /**
   * @return the paramClass
   */
  public ParameterClass getParamClass() {
    return this.paramClass;
  }


  /**
   * @param paramClass the paramClass to set
   */
  public void setParamClass(final ParameterClass paramClass) {
    this.paramClass = paramClass;
  }


  /**
   * @return the ssdCase
   */
  public SSDCase getSsdCase() {
    return this.ssdCase;
  }


  /**
   * @param ssdCase the ssdCase to set
   */
  public void setSsdCase(final SSDCase ssdCase) {
    this.ssdCase = ssdCase;
  }
}

