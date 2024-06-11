/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.caldataimport;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.AbstractCommand.COMMAND_MODE;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.bo.cdr.CDRRuleUtil;
import com.bosch.caltool.icdm.bo.cdr.FeatureAttributeAdapterNew;
import com.bosch.caltool.icdm.bo.cdr.ParameterCommand;
import com.bosch.caltool.icdm.bo.cdr.ParameterRuleFetcher;
import com.bosch.caltool.icdm.bo.cdr.SSDServiceHandler;
import com.bosch.caltool.icdm.common.bo.a2l.RuleMaturityLevel;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.a2l.ParameterAttribute;
import com.bosch.caltool.icdm.model.a2l.ParameterClass;
import com.bosch.caltool.icdm.model.a2l.ParameterRulesResponse;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportComparisonModel;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportData;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.FeatureValueModel;
import com.bosch.ssd.icdm.model.SSDMessage;


/**
 * ICDM-1540
 *
 * @author bru2cob
 */
public class FunctionImportRuleManager extends AbstractImportRuleManager {

  /**
   * @param cdrFunction CDRFunction
   */
  public FunctionImportRuleManager(final ServiceData serviceData, final SSDServiceHandler ssdServiceHandler) {
    super(serviceData, ssdServiceHandler);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void readRules(final SSDServiceHandler ssdServiceHandler, final CalDataImportData importData,
      final FeatureAttributeAdapterNew adapter, final ParamCollection calDataImporterObject)
      throws IcdmException {
    Map<String, Map<Integer, Set<AttributeValueModel>>> commonCombiAttrValModel =
        importData.getMappedObjCombiAttrValModelMap();

    List<String> allParamList = new ArrayList<>();
    allParamList.addAll(importData.getInputDataMap().keySet());

    ParameterRuleFetcher fetcher = new ParameterRuleFetcher(getServiceData());

    ParameterRulesResponse paramRuleResponse =
        fetcher.createParamRulesOutput(calDataImporterObject.getName(), null, null, null);

    Map<String, List<CDRRule>> ssdRuleMap = ssdServiceHandler.readReviewRule(allParamList);
    Map<String, Parameter> paramMap = new HashMap<>();
    Map<String, List<ParameterAttribute>> paramAttrMap = new HashMap<>();
    paramMap.putAll(paramRuleResponse.getParamMap());
    paramAttrMap.putAll(paramRuleResponse.getAttrMap());
    for (Function func : importData.getParamFuncObjMap().values()) {
      paramRuleResponse = importData.getFuncParamRespMap().get(func.getName());
      paramMap.putAll(paramRuleResponse.getParamMap());
      paramAttrMap.putAll(paramRuleResponse.getAttrMap());
    }

    //
    for (Entry<String, Map<Integer, Set<AttributeValueModel>>> commonCombiAttrValModelEntry : commonCombiAttrValModel
        .entrySet()) {
      Map<Integer, Set<AttributeValueModel>> attrValModCombinations = commonCombiAttrValModelEntry.getValue();
      Map<Integer, Set<FeatureValueModel>> feaValCombiModels =
          attrValToFeatureValCombi(attrValModCombinations, adapter);
      List<String> paramList = new ArrayList<>();

      Parameter param = paramMap.get(commonCombiAttrValModelEntry.getKey());

      paramList.add(param.getName());


      if (attrValModCombinations.isEmpty()) {
        findRulesWithoutCombinations(importData, ssdRuleMap, paramAttrMap, paramList, param);
      }
      else {
        findRuleWithCombinations(importData, ssdRuleMap, paramAttrMap, feaValCombiModels, param, paramList);
      }
    }

  }


  /**
   * @param importData
   * @param ssdRuleMap
   * @param paramAttrMap
   * @param feaValCombiModels
   * @param feaValCombiModels
   * @param paramList
   * @param attrValModCombinations
   * @param string
   * @throws IcdmException
   */
  private void findRuleWithCombinations(final CalDataImportData importData, final Map<String, List<CDRRule>> ssdRuleMap,
      final Map<String, List<ParameterAttribute>> paramAttrMap,
      final Map<Integer, Set<FeatureValueModel>> feaValCombiModels, final Parameter parameter,
      final List<String> paramList)
      throws IcdmException {
    for (Entry<Integer, Set<FeatureValueModel>> featureValCombiEntry : feaValCombiModels.entrySet()) {
      Map<String, List<CDRRule>> consolidatedParamRuleListMap =
          consolidateRules(ssdRuleMap, featureValCombiEntry.getValue(), paramList);
      // check whether rule already exists for same attr-val combination
      if (consolidatedParamRuleListMap.isEmpty()) {
        // Below code indicates Rule not found for specific combination in Ruleset and hence needs to
        // be
        // created
        Map<String, ReviewRule> tempMap = new HashMap<>(importData.getInputDataMap());
        tempMap.keySet().retainAll(paramList);
        for (String param : tempMap.keySet()) {
          createCompObj(importData, param, tempMap.get(param),
              new ArrayList<FeatureValueModel>(featureValCombiEntry.getValue()));
        }
      }
      else {
        consolidateRulesForSpecficCombination(importData, paramList, paramAttrMap, consolidatedParamRuleListMap,
            parameter, featureValCombiEntry);
      }
    }

  }


  /**
   * @param importData
   * @param paramAttrMap
   * @param paramList
   * @param featureValCombiEntry
   * @param consolidatedParamRuleListMap
   * @param parameter
   * @param featureValCombiEntry
   * @throws IcdmException
   */
  private void consolidateRulesForSpecficCombination(final CalDataImportData importData, final List<String> paramList,
      final Map<String, List<ParameterAttribute>> paramAttrMap,
      final Map<String, List<CDRRule>> consolidatedParamRuleListMap, final Parameter parameter,
      final Entry<Integer, Set<FeatureValueModel>> featureValCombiEntry)
      throws IcdmException {

    if (consolidatedParamRuleListMap.size() != paramList.size()) { // Applicable for CompPackage where some
      // rules already exist
      // and some rules need to be created
      // In case of Rule Set parameter, SSD call is made for each
      // parameter and ruleList is either empty or filled
      for (String paramName : paramList) {
        if (consolidatedParamRuleListMap.get(paramName) == null) {// Rule not existing for this parameter
          consolidatedParamRuleListMap.put(paramName, null);
        }
      }
    }

    for (Entry<String, List<CDRRule>> ssdRuleMapEntry : consolidatedParamRuleListMap.entrySet()) {
      String paramName = ssdRuleMapEntry.getKey();
      Set<ReviewRule> paramRules = importData.getExistingSSDRuleListMap().get(paramName);
      if (paramRules == null) {
        paramRules = new TreeSet<>(new Comparator<ReviewRule>() {

          @Override
          public int compare(final ReviewRule cdrRule1, final ReviewRule cdrRule2) {
            int compareTo = cdrRule1.compareTo(cdrRule2);
            if (compareTo == 0) {
              return cdrRule1.getRuleId().compareTo(cdrRule2.getRuleId());
            }
            return compareTo;
          }
        });
        importData.getExistingSSDRuleListMap().put(paramName, paramRules);
      }
      // Since set is used duplicate rules are removed if any is present

      // Prevent addition of default rule (Default rule is a rule for a parameter which has attribute
      // dependencies,
      // When no matching attribute value combination is found for a parameter, a default rule is returned. This
      // default rule should not be considered)

      // Also SSD returns rule with partial attribute value dependencies which should not be considered
      List<CDRRule> ruleList = ssdRuleMapEntry.getValue();

      if ((ruleList != null) && !ruleList.isEmpty()) {
        List<ReviewRule> rulesToAdd = new ArrayList<>();
        for (CDRRule cdrRul : ruleList) {
          // rule set
          List<ParameterAttribute> parmAttrList = paramAttrMap.get(parameter.getName());
          if (parmAttrList == null) {
            parmAttrList = new ArrayList<>();
          }
          if (parameter.getName().equals(paramName)) {
            if ((!parmAttrList.isEmpty() && (cdrRul.getDependencyList().size() == parmAttrList.size())) ||
                parmAttrList.isEmpty()) {
              rulesToAdd.add(convertCdrRule(cdrRul));
            }
            else {

              // Below code indicates Rule not found for specific combination in RULESETand hence needs to be
              // created
              createCompObj(importData, paramName, importData.getInputDataMap().get(paramName),
                  new ArrayList<>(featureValCombiEntry.getValue()));
            }
          }
          else {
            // TODO: Code on reaching here indicates param to be newly added to RuleSet
          }
        }
        paramRules.addAll(rulesToAdd);
      }
      else {
        // Applicable for CompPackage where some rules already exist
        // and some rules need to be created
        // In case of Rule Set parameter, SSD call is made for each
        // parameter and ruleList is either empty or filled
        createCompObj(importData, paramName, importData.getInputDataMap().get(paramName),
            new ArrayList<>(featureValCombiEntry.getValue()));
      }
    }

  }


  /**
   * @param importData
   * @param param
   * @param reviewRule
   * @param depList
   * @throws IcdmException
   */
  private void createCompObj(final CalDataImportData importData, final String param, final ReviewRule reviewRule,
      final ArrayList<FeatureValueModel> depList)
      throws IcdmException {
    CalDataImportComparison calDataComp =
        new CalDataImportComparison(param, importData.getParamNameTypeMap().get(param), depList, getServiceData());
    calDataComp.setOldRule(null);
    CDRRule cdrRule = convertReviewRule(reviewRule);
    CDRRule copyRule = CDRRuleUtil.createCopy(cdrRule);
    calDataComp.setNewRule(convertCdrRule(copyRule));
    // set the maturity level here
    String maturityLvl = CalDataUtil.getStatus(cdrRule.getRefValueCalData());
    if (CommonUtils.isEmptyString(maturityLvl)) {
      reviewRule.setMaturityLevel(RuleMaturityLevel.getICDMMaturityLevelEnum(maturityLvl).getSSDMaturityLevel());
    }
    List<CalDataImportComparisonModel> paramCompList = importData.getCalDataCompMap().get(param);
    if (paramCompList == null) {
      paramCompList = new ArrayList<>();
      importData.getCalDataCompMap().put(param, paramCompList);
    }
    CalDataImportComparisonModel calDataImportCompModel = createCalDataImportCompModel(calDataComp, importData);
    paramCompList.add(calDataImportCompModel);

  }


  /**
   * @param calDataComp
   * @param importData
   * @throws IcdmException
   */
  private CalDataImportComparisonModel createCalDataImportCompModel(final CalDataImportComparison calDataComp,
      final CalDataImportData importData)
      throws IcdmException {
    CalDataImportComparisonModel calCompModel = new CalDataImportComparisonModel();
    calCompModel.setParamName(calDataComp.getParamName());
    calCompModel.setParamType(calDataComp.getParamType());
    calCompModel.setNewReadyForSeries(calDataComp.getNewReadyForSeries());
    calCompModel.setOldRule(calDataComp.getOldRule());
    calCompModel.setNewRule(calDataComp.getNewRule());
    String funcName = importData.getParamFuncMap().get(calDataComp.getParamName());
    if (funcName != null) {
      calCompModel.setFuncNames(funcName);
    }
    FeatureAttributeAdapterNew adapter = new FeatureAttributeAdapterNew(getServiceData());

    Map<FeatureValueModel, AttributeValueModel> createAttrValModel =
        adapter.createAttrValModel(new HashSet<>(calDataComp.getDependencyList()));
    calCompModel.setDependencyList(new TreeSet<>(createAttrValModel.values()));
    return calCompModel;

  }


  /**
   * @param importData
   * @param ssdRuleMap
   * @param paramAttrMap
   * @param paramList
   * @param attrValModCombinations
   * @param param2
   * @param paramName
   * @throws IcdmException
   */
  private void findRulesWithoutCombinations(final CalDataImportData importData,
      final Map<String, List<CDRRule>> ssdRuleMap, final Map<String, List<ParameterAttribute>> paramAttrMap,
      final List<String> paramList, final Parameter parameter)
      throws IcdmException {
    Set<String> newParamSet = new HashSet<>(paramList);

    // Check for Rules without attribute dependencies

    Map<String, List<CDRRule>> ssdRuleWithoutDependenciesMap =
        consolidateRulesWithoutDependencies(ssdRuleMap, paramList);
    if (!ssdRuleWithoutDependenciesMap.isEmpty()) {
      for (Entry<String, List<CDRRule>> ssdRuleMapEntry : ssdRuleWithoutDependenciesMap.entrySet()) {
        String paramName = ssdRuleMapEntry.getKey();
        Set<ReviewRule> paramRules = importData.getExistingSSDRuleListMap().get(paramName);

        if (paramRules == null) {
          paramRules = new TreeSet<>(new Comparator<ReviewRule>() {

            /**
             * @param cdrRule1 rule 1
             * @param cdrRule2 rule 2
             * @return compare result
             */
            @Override
            public int compare(final ReviewRule cdrRule1, final ReviewRule cdrRule2) {
              int compareTo = cdrRule1.compareTo(cdrRule2);
              if (compareTo == 0) {
                return cdrRule1.getRuleId().compareTo(cdrRule2.getRuleId());
              }
              return compareTo;
            }
          });
          importData.getExistingSSDRuleListMap().put(paramName, paramRules);
        }
        // Since set is used duplicate rules are removed if any is present

        // Prevent addition of default rule (Default rule is a rule for a parameter which has attribute
        // dependencies,
        // When no matching attribute value combination is found for a parameter, a default rule is returned. This
        // default rule should not be considered)

        // Also SSD returns rule with partial attribute value dependencies which should not be considered
        List<CDRRule> ruleList = ssdRuleMapEntry.getValue();
        List<ReviewRule> rulesToAdd = new ArrayList<>();
        for (CDRRule cdrRul : ruleList) {

          List<ParameterAttribute> parmAttrList = paramAttrMap.get(parameter.getName());

          if (parmAttrList == null) {
            parmAttrList = new ArrayList<>();
          }
          // rule set
          if (parameter.getName().equals(paramName)) {
            if ((!parmAttrList.isEmpty() && (cdrRul.getDependencyList().size() == parmAttrList.size())) ||
                parmAttrList.isEmpty()) {
              rulesToAdd.add(convertCdrRule(cdrRul));
            }
            else {
              // Below code indicates Rule without dependenies not found for parameter in RULESETand hence needs to
              // be created
              ReviewRule newCalData = importData.getInputDataMap().get(paramName);
              createCompObj(importData, paramName, newCalData, new ArrayList<FeatureValueModel>());
            }

          }
        }
        paramRules.addAll(rulesToAdd);
        newParamSet.remove(paramName);
      }

    }

    // Add all the new parameters for create rule
    for (String param : newParamSet) {
      ReviewRule newCalData = importData.getInputDataMap().get(param);
      createCompObj(importData, param, newCalData, new ArrayList<FeatureValueModel>());
    }

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Map<CDRRule, SSDMessage> createRules(final ParamCollection calDataImporterObject,
      final CalDataImportData importData, final List<CDRRule> listOfRulesToBeCreated)
      throws IcdmException {

    SSDServiceHandler handler = new SSDServiceHandler(getServiceData());

    Map<CDRRule, SSDMessage> createMsg = null;
    createRuleObjects(importData, calDataImporterObject, listOfRulesToBeCreated);
    if (!listOfRulesToBeCreated.isEmpty()) {
      // for Componenet package we need to take it
      createMsg =
          handler.createValidMultFuncRules(listOfRulesToBeCreated, handler.getNodeId().longValue(), null, false);
    }

    List<String> labelNames = new ArrayList<>();
    listOfRulesToBeCreated.forEach(cdr -> labelNames.add(cdr.getParameterName()));
    Map<String, List<CDRRule>> newCdrMapUpdated = new HashMap<>();
    if(!labelNames.isEmpty()) {
    newCdrMapUpdated =
        getServiceHandler().readReviewRule(labelNames, getServiceHandler().getNodeId().longValue());
    }
    createUnicodeRemarksWhileImport(listOfRulesToBeCreated, newCdrMapUpdated, createMsg);
    return createMsg;

  }

  /**
   * {@inheritDoc}
   *
   * @throws DataException
   */
  @Override
  protected void createRuleObjects(final CalDataImportData importData, final ParamCollection calDataImporterObject,
      final List<CDRRule> newRuleList)
      throws DataException {
    for (String paramName : importData.getCalDataCompMap().keySet()) {
      createRules(importData, newRuleList, paramName);
    }

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Map<CDRRule, SSDMessage> updateRules(final ParamCollection calDataImporterObject,
      final List<CalDataImportComparisonModel> modifyingData, final CalDataImporter calDataImporter)
      throws IcdmException {

    Map<CDRRule, ReviewRule> unicodeUpdateMap = new HashMap<>();
    List<String> labelNames = new ArrayList<>();

    List<CDRRule> oldCdrList =
        updateRuleObjectsUnicode(calDataImporterObject, modifyingData, calDataImporter, unicodeUpdateMap, labelNames);
    Map<CDRRule, SSDMessage> cdrRuleSsdMsgMap =
        getServiceHandler().updateValidMultFuncRules(oldCdrList, getServiceHandler().getNodeId().longValue());

    Map<String, List<CDRRule>> newCdrMapUpdated = new HashMap<>();
    if(!labelNames.isEmpty()) {
    newCdrMapUpdated =
        getServiceHandler().readReviewRule(labelNames, getServiceHandler().getNodeId().longValue());
    }
    if (!unicodeUpdateMap.isEmpty()) {
      updateUnicodeRemarksWhileImport(unicodeUpdateMap, newCdrMapUpdated, cdrRuleSsdMsgMap);
    }

    return cdrRuleSsdMsgMap;
  }


  /**
   * {@inheritDoc}
   *
   * @throws IcdmException
   */
  @Override
  public List<AbstractCommand> updateParamProps(final Map<String, Map<String, String>> mapWithNewParamProps,
      final Map<String, String> paramNameType, final Map<String, String> classMap,
      final Map<String, List<CalDataImportComparisonModel>> caldataCompMap)
      throws IcdmException {
    SortedSet<String> paramsToBeUpdated = findParamNamesToUpdate(caldataCompMap);
    List<AbstractCommand> listCommands = new ArrayList<>();
    for (String paramName : paramsToBeUpdated) {

      Map<String, String> propMap = mapWithNewParamProps.get(paramName);

      ParameterLoader loader = new ParameterLoader(getServiceData());
      Parameter parameter = loader.getParamByName(paramName, paramNameType.get(paramName));
      parameter.setCodeWord(CommonUtils.isEqual(propMap.get(CDRConstants.CDIKEY_CODE_WORD), "Yes") ? "Y" : "N");
      ParameterClass pClass = ParameterClass.getParamClassT(classMap.get(paramName));
      if ("true".equals(propMap.get(CDRConstants.CDIKEY_USE_CLASS))) {
        // if use check box is checked , use the imported /new value
        pClass = ParameterClass.getParamClassT(propMap.get(CDRConstants.CDIKEY_PARAM_CLASS));
      }
      parameter.setpClassText(pClass == null ? "" : pClass.getText());

      parameter.setParamHint(propMap.get(CDRConstants.CDIKEY_CAL_HINT));

      ParameterCommand command = new ParameterCommand(getServiceData(), parameter, loader, COMMAND_MODE.UPDATE);


      listCommands.add(command);

    }
    return listCommands;
  }

  /**
   * @param caldataCompMap Map<String, List<CalDataImportComparison>>
   * @return SortedSet<String>
   */
  private SortedSet<String> findParamNamesToUpdate(
      final Map<String, List<CalDataImportComparisonModel>> caldataCompMap) {
    SortedSet<String> paramNameSet = new TreeSet<>();
    for (List<CalDataImportComparisonModel> calDataCompList : caldataCompMap.values()) {
      for (CalDataImportComparisonModel calDataImportComparison : calDataCompList) {
        if (calDataImportComparison.isUpdateInDB()) {
          paramNameSet.add(calDataImportComparison.getParamName());
        }

      }
    }
    return paramNameSet;
  }


}
