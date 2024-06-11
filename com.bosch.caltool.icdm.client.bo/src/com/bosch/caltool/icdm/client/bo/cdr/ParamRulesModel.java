/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.RuleDependency;


/**
 * @author dmo5cob
 */
public class ParamRulesModel<D extends IParameterAttribute, P extends IParameter> {

  /**
   * Column start index
   */
  private static final int COL_START_INDEX = 7;
  /**
   * Parameter obj
   */
  private final IParameter param;
  // ICDM-1063
  /**
   * Map of rule dependencies
   */
  private final Map<Integer, RuleDependency> ruleDepnMap = new ConcurrentHashMap<>();
  // ICDM-1063
  private final ParameterDataProvider<D, P> paramDataProvider;


  /**
   * Text constant for VALUE
   */
  public static final String VALUE_TEXT = "VALUE";


  /**
   * @param apicDataProvider dp
   * @param param CDRFuncParameter obj
   */
  public ParamRulesModel(final IParameter param, final ParameterDataProvider<D, P> paramDataProvider) {
    this.param = param;
    this.paramDataProvider = paramDataProvider;

  }

  /**
   * @return the param
   */
  public IParameter getParam() {
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
   */
  public Set<RuleDefinition> getRuleDefenitionSet() {


    // get the list of review rules from the parameter
    List<ReviewRule> rules = this.paramDataProvider.getRuleList(this.param);
    if (rules == null) {
      return null;
    }
    // create a sorted set out of the rules list
    List<ReviewRule> sortedCDRRules = new ArrayList<>(rules);
    int colIndex = COL_START_INDEX;
    Set<RuleDefinition> ruleDefnSet = new TreeSet<>();

    try {

      // Iterate through the rules
      for (ReviewRule cdrRule : sortedCDRRules) {
        // create the model for each row in the parameter rules page
        RuleDefinition ruleDfn = new RuleDefinition();
        ruleDfn.setParamName(this.param.getName());
        ruleDfn.setLowerLimit(cdrRule.getLowerLimit());
        ruleDfn.setUpperLimit(cdrRule.getUpperLimit());
        ruleDfn.setBitWise(cdrRule.getBitWiseRule());
        ruleDfn.setValueType(cdrRule.getValueType());
        ruleDfn.setRefValueCalData(getRefValForRule(cdrRule));
        ruleDfn.setRefValue(cdrRule.getRefValue());
        ruleDfn.setRefValueDisplayString(cdrRule.getRefValueDispString());
        ruleDfn.setReviewMethod(cdrRule.getReviewMethod());
        ruleDfn.setUnit(cdrRule.getUnit());
        ruleDfn.setMaturityLevel(cdrRule.getMaturityLevel());
        ruleDfn.setExactMatch(cdrRule.isDcm2ssd());
        ruleDfn.setFormulaDesc(cdrRule.getFormulaDesc());
        // check whether it is a default rule
        if (this.paramDataProvider.hasDependency(this.param) && cdrRule.getDependencyList().isEmpty()) {
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
        // 491086 - Defect fix - Validate properties view for Normal Rule in Parameter Rule Page in Rule Editor
        ruleDfn.setRuleCreatedDate(cdrRule.getRuleCreatedDate());
        ruleDfn.setRuleCreatedUser(cdrRule.getRuleCreatedUser());

        ruleDefnSet.add(ruleDfn);

        // create the model for each column in the parameter rules page
        colIndex = populateDependency(colIndex, cdrRule, ruleDfn);
      }
    }
    catch (DataException exp) {
      CDMLogger.getInstance().error("Error when creating the Rule definition", exp);
    }
    return ruleDefnSet;

  }


  /**
   * @param rule
   * @return
   * @throws ClassNotFoundException
   * @throws IOException
   */
  private CalData getRefValForRule(final ReviewRule rule) throws DataException {
    CalData caldataObj = null;
    try {
      caldataObj = CalDataUtil.getCalDataObj(rule.getRefValueCalData());
    }
    catch (ClassNotFoundException | IOException exp) {
      throw new DataException("error when creating cal data object" + exp);
    }

    return caldataObj;
  }

  /**
   * @param colIndex
   * @param cdrRule
   * @param ruleDfn
   * @return
   */
  private int populateDependency(final int colIndex, final ReviewRule cdrRule, final RuleDefinition ruleDfn) {
    int columnIndex = colIndex;
    // get the dependency list
    SortedSet<AttributeValueModel> attrValModelSet = cdrRule.getDependencyList();

    RuleDependency ruleDepn = new RuleDependency();

    for (AttributeValueModel attrValModel : attrValModelSet) {
      ruleDepn.addAttrVal(attrValModel);
    }

    if ((attrValModelSet != null) && !attrValModelSet.isEmpty()) {
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


  /**
   * @return the paramDataProvider
   */
  public ParameterDataProvider<D, P> getParamDataProvider() {
    return this.paramDataProvider;
  }


}
