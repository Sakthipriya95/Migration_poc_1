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
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.cdr.CDRRuleUtil;
import com.bosch.caltool.icdm.bo.cdr.FeatureAttributeAdapterNew;
import com.bosch.caltool.icdm.bo.cdr.SSDServiceHandler;
import com.bosch.caltool.icdm.bo.comppkg.CompPackageLoader;
import com.bosch.caltool.icdm.common.bo.a2l.RuleMaturityLevel;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportComparisonModel;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportData;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.caltool.icdm.model.comppkg.CompPkgParamAttr;
import com.bosch.caltool.icdm.model.comppkg.CompPkgParameter;
import com.bosch.caltool.icdm.model.comppkg.CompPkgRuleResponse;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.FeatureValueModel;
import com.bosch.ssd.icdm.model.SSDMessage;


/**
 * @author rgo7cob
 */
public class CPImportRuleManager extends AbstractImportRuleManager {

  /**
   * @param serviceData
   * @param ssdServiceHandler
   */
  public CPImportRuleManager(final ServiceData serviceData, final SSDServiceHandler ssdServiceHandler) {
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
    // Exclude all invalid parameters from fetching the rules
    // TODO: When only one combination is present for RuleSet, all parameters can be added to improve performance
    allParamList.removeAll(importData.getInvalidParamSet());

    CompPackageLoader fetcher = new CompPackageLoader(getServiceData());

    CompPkgRuleResponse paramRuleResponse = fetcher.getCompPkgRule(calDataImporterObject.getId());

    Map<String, List<CDRRule>> ssdRuleMap =
        ssdServiceHandler.readReviewRule(allParamList, ((CompPackage) calDataImporterObject).getSsdNodeId());

    for (Entry<String, Map<Integer, Set<AttributeValueModel>>> commonCombiAttrValModelEntry : commonCombiAttrValModel
        .entrySet()) {
      Map<Integer, Set<AttributeValueModel>> attrValModCombinations = commonCombiAttrValModelEntry.getValue();
      Map<Integer, Set<FeatureValueModel>> feaValCombiModels =
          attrValToFeatureValCombi(attrValModCombinations, adapter);
      List<String> paramList = new ArrayList<>();

      CompPkgParameter param = paramRuleResponse.getParamMap().get(commonCombiAttrValModelEntry.getKey());
      paramList.add(commonCombiAttrValModelEntry.getKey());
      // Exclude all invalid parameters from fetching the rules
      // TODO: When only one combination is present for RuleSet, all parameters can be added to improve performance
      paramList.removeAll(importData.getInvalidParamSet());

      if (!attrValModCombinations.isEmpty()) {
        readRuleWithCombinations(importData, ssdRuleMap, paramRuleResponse, feaValCombiModels, param, paramList);
      }
      else {
        readRulesWithoutCombinations(importData, ssdRuleMap, paramRuleResponse, paramList, param);
      }
    }

  }

  /**
   * @param importData
   * @param ssdRuleMap
   * @param commonCombiAttrValModelEntry
   * @param paramList
   * @throws IcdmException
   */
  private void readRulesWithoutCombinations(final CalDataImportData importData,
      final Map<String, List<CDRRule>> ssdRuleMap, final CompPkgRuleResponse paramRuleResponse,
      final List<String> paramList, final CompPkgParameter parameter)
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

          List<CompPkgParamAttr> parmAttrList = paramRuleResponse.getAttrMap().get(parameter.getName());

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
      createCompObj(importData, param, importData.getInputDataMap().get(param), new ArrayList<FeatureValueModel>());
    }
  }

  /**
   * @param importData
   * @param ssdRuleMap
   * @param commonCombiAttrValModelEntry
   * @param feaValCombiModels
   * @param paramList
   * @throws IcdmException
   */
  private void readRuleWithCombinations(final CalDataImportData importData, final Map<String, List<CDRRule>> ssdRuleMap,
      final CompPkgRuleResponse paramRuleResponse, final Map<Integer, Set<FeatureValueModel>> feaValCombiModels,
      final CompPkgParameter parameter, final List<String> paramList)
      throws IcdmException {
    for (Entry<Integer, Set<FeatureValueModel>> featureValEntry : feaValCombiModels.entrySet()) {
      Map<String, List<CDRRule>> consolidatedParamRuleListMap =
          consolidateRules(ssdRuleMap, featureValEntry.getValue(), paramList);
      // check whether rule already exists for same attr-val combination
      if (!consolidatedParamRuleListMap.isEmpty()) {
        consolidateRulesForSpecficCombination(importData, paramList, paramRuleResponse, consolidatedParamRuleListMap,
            parameter, featureValEntry);
      }
      else {
        // Below code indicates Rule not found for specific combination in Ruleset and hence needs to
        // be
        // created
        Map<String, ReviewRule> tempMap = new HashMap<>(importData.getInputDataMap());
        tempMap.keySet().retainAll(paramList);
        for (String param : tempMap.keySet()) {
          createCompObj(importData, param, tempMap.get(param),
              new ArrayList<FeatureValueModel>(featureValEntry.getValue()));
        }

      }
    }
  }


  /**
   * @param importData
   * @param commonCombiAttrValModelEntry
   * @param paramList
   * @param feaValModelEntry
   * @param consolidatedParamRuleListMap
   * @throws IcdmException
   */
  private void consolidateRulesForSpecficCombination(final CalDataImportData importData, final List<String> paramList,
      final CompPkgRuleResponse paramRuleResponse, final Map<String, List<CDRRule>> consolidatedParamRuleListMap,
      final CompPkgParameter parameter, final Entry<Integer, Set<FeatureValueModel>> featureValCombiEntry)
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

          List<CompPkgParamAttr> parmAttrList = paramRuleResponse.getAttrMap().get(parameter.getName());
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
   * @param arrayList
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
    CalDataImportComparisonModel calDataImportCompModel = createCalDataImportCompModel(calDataComp);
    paramCompList.add(calDataImportCompModel);
  }


  /**
   * @param calDataComp
   * @return
   * @throws IcdmException
   */
  private CalDataImportComparisonModel createCalDataImportCompModel(final CalDataImportComparison calDataComp)
      throws IcdmException {
    CalDataImportComparisonModel calCompModel = new CalDataImportComparisonModel();
    calCompModel.setParamName(calDataComp.getParamName());
    calCompModel.setParamType(calDataComp.getParamType());
    calCompModel.setNewReadyForSeries(calDataComp.getNewReadyForSeries());
    calCompModel.setOldRule(calDataComp.getOldRule());
    calCompModel.setNewRule(calDataComp.getNewRule());

    FeatureAttributeAdapterNew adapter = new FeatureAttributeAdapterNew(getServiceData());

    Map<FeatureValueModel, AttributeValueModel> createAttrValModel =
        adapter.createAttrValModel(new HashSet<>(calDataComp.getDependencyList()));
    calCompModel.setDependencyList(new TreeSet<>(createAttrValModel.values()));
    return calCompModel;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<CDRRule, SSDMessage> createRules(final ParamCollection calDataImporterObject,
      final CalDataImportData importData, final List<CDRRule> listOfRulesCreated)
      throws IcdmException {
    Map<CDRRule, SSDMessage> createMsg = null;

    SSDServiceHandler handler = new SSDServiceHandler(getServiceData());

    CompPackage compPkg = (CompPackage) calDataImporterObject;


    createRuleObjects(importData, calDataImporterObject, listOfRulesCreated);
    if (!listOfRulesCreated.isEmpty()) {
      createMsg = handler.createValidMultRuleSetRules(listOfRulesCreated, compPkg.getSsdNodeId(),
          calDataImporterObject.getId(), null, false);
    }
    return createMsg;
  }


  /**
   * {@inheritDoc}
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
    return getServiceHandler().updateValidMultRuleSetRules(
        updateRuleObjects(calDataImporterObject, modifyingData, calDataImporter),
        ((CompPackage) calDataImporterObject).getSsdNodeId(), calDataImporterObject.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<AbstractCommand> updateParamProps(final Map<String, Map<String, String>> mapWithNewParamProps,
      final Map<String, String> paramNameType, final Map<String, String> classMap,
      final Map<String, List<CalDataImportComparisonModel>> caldataCompMap)
      throws IcdmException {

    return null;
  }

}
