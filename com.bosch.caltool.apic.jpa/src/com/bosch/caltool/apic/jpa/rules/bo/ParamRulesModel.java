/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.rules.bo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.apic.jpa.bo.ApicDataProvider;
import com.bosch.caltool.apic.jpa.bo.AttributeValueModel;
import com.bosch.caltool.apic.jpa.bo.FeatureAttributeAdapter;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.SsdInterfaceException;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.FeatureValueModel;


/**
 * @author dmo5cob
 */
public class ParamRulesModel {

  /**
   * Column start index
   */
  private static final int COL_START_INDEX = 7;
  /**
   * Parameter obj
   */
  private final IParameter<?> param;
  // ICDM-1063
  /**
   * Map of rule dependencies
   */
  private final Map<Integer, RuleDependency> ruleDepnMap = new ConcurrentHashMap<Integer, RuleDependency>();
  // ICDM-1063
  /**
   * ApicDataProvider instance
   */
  private final ApicDataProvider dataProvider;

  /**
   * Text constant for VALUE
   */
  public static final String VALUE_TEXT = "VALUE";


  /**
   * @param apicDataProvider dp
   * @param param CDRFuncParameter obj
   */
  public ParamRulesModel(final ApicDataProvider apicDataProvider, final IParameter<?> param) {
    this.param = param;
    this.dataProvider = apicDataProvider;
  }

  /**
   * @return the param
   */
  public IParameter<?> getParam() {
    return this.param;
  }


  /**
   * @return the colModelMap
   */
  public Map<Integer, RuleDependency> getRuleDependencyMap() {
    return this.ruleDepnMap;
  }


  /**
   * @return the paramRuleInputSet
   * @throws SsdInterfaceException
   */
  public Set<RuleDefinition> getRuleDefenitionSet() throws SsdInterfaceException {


    // get the list of review rules from the parameter
    List<CDRRule> rules = this.param.getReviewRuleList();
    if (rules == null) {
      return null;
    }
    // create a sorted set out of the rules list
    List<CDRRule> sortedCDRRules = new ArrayList<>(rules);
    FeatureAttributeAdapter fAdapter = new FeatureAttributeAdapter(ParamRulesModel.this.dataProvider);
    // sort the feature values
    Collections.sort(sortedCDRRules, new CDRRuleComparator(fAdapter));
    int colIndex = COL_START_INDEX;
    Set<RuleDefinition> ruleDefnSet = new TreeSet<>();

    // Iterate through the rules
    for (CDRRule cdrRule : sortedCDRRules) {
      // create the model for each row in the parameter rules page
      RuleDefinition ruleDfn = new RuleDefinition();
      ruleDfn.setParamName(this.param.getName());
      ruleDfn.setLowerLimit(cdrRule.getLowerLimit());
      ruleDfn.setUpperLimit(cdrRule.getUpperLimit());
      ruleDfn.setBitWise(cdrRule.getBitWiseRule());
      ruleDfn.setValueType(cdrRule.getValueType());
      ruleDfn.setRefValueCalData(cdrRule.getRefValueCalData());
      ruleDfn.setRefValue(cdrRule.getRefValue());
      ruleDfn.setRefValueDisplayString(cdrRule.getRefValueDispString());
      ruleDfn.setReviewMethod(cdrRule.getReviewMethod());
      ruleDfn.setUnit(cdrRule.getUnit());
      ruleDfn.setMaturityLevel(cdrRule.getMaturityLevel());
      ruleDfn.setExactMatch(cdrRule.isDcm2ssd());
      // check whether it is a default rule
      if (this.param.hasDependencies() && cdrRule.getDependencyList().isEmpty()) {
        ruleDfn.setDefaultRule(true);
        ruleDfn.setDefaultRule(cdrRule);
      }
      if (ruleDefnSet.contains(ruleDfn)) {
        for (RuleDefinition ruleDefnItem : ruleDefnSet) {
          if (ruleDefnItem.equals(ruleDfn)) {
            ruleDfn = ruleDefnItem;
          }
        }
      }
      ruleDefnSet.add(ruleDfn);

      // create the model for each column in the parameter rules page
      colIndex = populateDependency(colIndex, cdrRule, ruleDfn);
    }
    return ruleDefnSet;

  }

  /**
   * @param colIndex
   * @param cdrRule
   * @param ruleDfn
   * @return
   */
  private int populateDependency(final int colIndex, final CDRRule cdrRule, final RuleDefinition ruleDfn) {
    int columnIndex = colIndex;
    // get the dependency list
    List<FeatureValueModel> fvModelList = cdrRule.getDependencyList();
    Map<FeatureValueModel, AttributeValueModel> mapOfAttrFeature;
    FeatureAttributeAdapter fAdapter = new FeatureAttributeAdapter(this.dataProvider);
    // Get the attr-values corresponding to feature-values
    if ((fvModelList != null) && !fvModelList.isEmpty()) {
      try {
        mapOfAttrFeature = fAdapter.createAttrValModel(new HashSet<FeatureValueModel>(fvModelList));
      }
      catch (IcdmException exp) {
        ObjectStore.getInstance().getLogger().info(exp.getLocalizedMessage(), exp);
        return columnIndex;
      }
      // create the column model object
      RuleDependency ruleDepn = new RuleDependency();
      // add the attr value combination to it
      for (FeatureValueModel featureValueModel : fvModelList) {
        AttributeValueModel attrVal = mapOfAttrFeature.get(featureValueModel);
        if (null != attrVal) {
          ruleDepn.addAttrVal(attrVal);
        }
      }
      // if maturity level is null then set it as "NONE"
      String maturityLevel = cdrRule.getMaturityLevel();
      if (maturityLevel == null) {
        maturityLevel = "NONE";
      }
      // set the maturity level to a map with col index as key
      ruleDfn.getRuleMappedIndex().put(colIndex, maturityLevel);
      // set the cdr rule to a map with col index as key
      ruleDfn.getRuleMapping().put(String.valueOf(colIndex), cdrRule);
      this.ruleDepnMap.put(Integer.valueOf(columnIndex), ruleDepn);
      columnIndex++;
    }
    return columnIndex;
  }

}
