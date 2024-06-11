/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.wizards;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.icdm.client.bo.cdr.ParamRulesModel;
import com.bosch.caltool.icdm.client.bo.cdr.RuleDefinition;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;


/**
 * @author mkl2cob data class for add new config wizard
 */
public class AddNewConfigWizardData<D extends IParameterAttribute, P extends IParameter> {

  /**
   * Parameter for which new configuration is to be created
   */
  private final IParameter cdrParameter;

  /**
   * Function for which the parameter is mapped
   */
  private final ParamCollection cdrFunction;

  /**
   * Attribute value model set
   */
  private Set<AttributeValueModel> attrValueModSet;

  /**
   * CDRRule
   */
  private ReviewRule cdrRule;

  /**
   * ParamRulesModel instance
   */
  private final ParamRulesModel paramRulesModel;

  /**
   * Rule definition instance
   */
  private RuleDefinition ruleDefinition;

  /**
   * list of selected rules to be updated
   */
  private List<ReviewRule> cdrRulesList;

  /**
   * true if the selected rules have same maturity level
   */
  private boolean sameMaturityLevel;

  /**
   * true if selected rules have same hint
   */
  private boolean sameHint;

  /**
   * true if selected rules have same lower limit
   */
  private boolean sameLowerLmt;

  /**
   * true if selected rules have same upper limit
   */
  private boolean sameUpperLmt;

  /**
   * true if selected rules have same ref val
   */
  private boolean sameRefVal;

  /**
   * true if selected rules have same exact match flag
   */
  private boolean sameExactMatchFlag;

  /**
   * true if selected rules have same ready for series
   */
  private boolean sameReadyForSeries;

  /**
   * true if selected rules have same unit
   */
  private boolean sameUnit;
  /**
   * true if the modified users are same
   */
  private boolean sameModifiedUser;

  /**
   * ICDM-1199 boolean to indicate whether attr val mapping is complete for rule(s)
   */
  private boolean attrValMapIncomplete;

  /**
   * ICDM-1199 attribute values to be defined
   */
  private Set<D> attrributesIncomplete;

  /**
   * Stores paramAttr and its selected values
   */
  private final ConcurrentMap<D, SortedSet<AttributeValue>> attrVals = new ConcurrentHashMap<>();

  ConcurrentMap<Integer, Set<AttributeValueModel>> attrValModels =
      new ConcurrentHashMap<Integer, Set<AttributeValueModel>>();

  private ReviewRule unModifiedTargetRule;
  /**
   * true if selected rules have same bitiwse
   */
  private boolean sameBitWise;


  /**
   * @return the unModifiedTargetRule
   */
  public ReviewRule getUnModifiedTargetRule() {
    return this.unModifiedTargetRule;
  }


  /**
   * @param unModifiedTargetRule the unModifiedTargetRule to set
   */
  public void setUnModifiedTargetRule(final ReviewRule unModifiedTargetRule) {
    this.unModifiedTargetRule = unModifiedTargetRule;
  }


  /**
   * @return the attrValModels
   */
  public Map<Integer, Set<AttributeValueModel>> getAttrValModels() {
    return this.attrValModels;
  }


  /**
   * @param attrValModels the attrValModels to set
   */
  public void setAttrValModels(final ConcurrentMap<Integer, Set<AttributeValueModel>> attrValModels) {
    this.attrValModels = attrValModels;
  }

  /**
   * @return the attrVals
   */
  public Map<D, SortedSet<AttributeValue>> getAttrVals() {
    return this.attrVals;
  }

  /**
   * Constructor
   *
   * @param cdrParam CDRFuncParameter
   * @param cdrFunction CDRFunction
   * @param paramRulesModel ParamRulesModel
   */
  public AddNewConfigWizardData(final IParameter cdrParam, final ParamCollection cdrFunction,
      final ParamRulesModel paramRulesModel) {
    this.cdrParameter = cdrParam;
    this.cdrFunction = cdrFunction;
    this.paramRulesModel = paramRulesModel;
  }

  /**
   * @return the cdrParameter
   */
  public IParameter getCdrParameter() {
    return this.cdrParameter;
  }

  /**
   * @return the cdrFunction
   */
  public ParamCollection getCdrFunction() {
    return this.cdrFunction;
  }

  /**
   * @return the attrValueModSet
   */
  public Set<AttributeValueModel> getAttrValueModSet() {
    return this.attrValueModSet;
  }

  /**
   * @param attrValueModSet the attrValueModSet to set
   */
  public void setAttrValueModSet(final Set<AttributeValueModel> attrValueModSet) {
    this.attrValueModSet = attrValueModSet;
  }

  /**
   * @return the cdrRule
   */
  public ReviewRule getCdrRule() {
    return this.cdrRule;
  }

  /**
   * @param cdrRule the cdrRule to set
   */
  public void setCdrRule(final ReviewRule cdrRule) {
    this.cdrRule = cdrRule;
  }

  /**
   * @return the paramRulesModel
   */
  public ParamRulesModel getParamRulesModel() {
    return this.paramRulesModel;
  }

  /**
   * @param object RuleDefinition
   */
  public void setRuleDefinition(final RuleDefinition object) {
    this.ruleDefinition = object;

  }

  /**
   * @return the ruleDefinition
   */
  public RuleDefinition getRuleDefinition() {
    return this.ruleDefinition;
  }


  /**
   * @return the cdrRulesList
   */
  public List<ReviewRule> getCdrRulesList() {
    return this.cdrRulesList;
  }

  /**
   * @param cdrRulesList the cdrRulesList to set
   */
  public void setCdrRulesList(final List<ReviewRule> cdrRulesList) {
    this.cdrRulesList = cdrRulesList;
  }

  /**
   * @return the sameMaturityLevel
   */
  public boolean isSameMaturityLevel() {
    return this.sameMaturityLevel;
  }

  /**
   * @param sameMaturityLevel the sameMaturityLevel to set
   */
  public void setSameMaturityLevel(final boolean sameMaturityLevel) {
    this.sameMaturityLevel = sameMaturityLevel;
  }

  /**
   * @return the sameHint
   */
  public boolean isSameHint() {
    return this.sameHint;
  }

  /**
   * @param sameHint the sameHint to set
   */
  public void setSameHint(final boolean sameHint) {
    this.sameHint = sameHint;
  }

  /**
   * @return the sameLowerLmt
   */
  public boolean isSameLowerLmt() {
    return this.sameLowerLmt;
  }

  /**
   * @param sameLowerLmt the sameLowerLmt to set
   */
  public void setSameLowerLmt(final boolean sameLowerLmt) {
    this.sameLowerLmt = sameLowerLmt;
  }

  /**
   * @return the sameUpperLmt
   */
  public boolean isSameUpperLmt() {
    return this.sameUpperLmt;
  }

  /**
   * @param sameUpperLmt the sameUpperLmt to set
   */
  public void setSameUpperLmt(final boolean sameUpperLmt) {
    this.sameUpperLmt = sameUpperLmt;
  }

  /**
   * @return the sameRefVal
   */
  public boolean isSameRefVal() {
    return this.sameRefVal;
  }

  /**
   * @param sameRefVal the sameRefVal to set
   */
  public void setSameRefVal(final boolean sameRefVal) {
    this.sameRefVal = sameRefVal;
  }

  /**
   * @return the sameExactMatchFlag
   */
  public boolean isSameExactMatchFlag() {
    return this.sameExactMatchFlag;
  }

  /**
   * @param sameExactMatchFlag the sameExactMatchFlag to set
   */
  public void setSameExactMatchFlag(final boolean sameExactMatchFlag) {
    this.sameExactMatchFlag = sameExactMatchFlag;
  }


  /**
   * @param sameReadyForSeries the sameRvwMtd to set
   */
  public void setSameReadyForSeries(final boolean sameReadyForSeries) {
    this.sameReadyForSeries = sameReadyForSeries;
  }


  /**
   * @return the sameReadyForSeries
   */
  public boolean isSameReadyForSeries() {
    return this.sameReadyForSeries;
  }


  /**
   * @return the sameUnit
   */
  public boolean isSameUnit() {
    return this.sameUnit;
  }

  /**
   * @param sameUnit the sameUnit to set
   */
  public void setSameUnit(final boolean sameUnit) {
    this.sameUnit = sameUnit;
  }

  // ICDM-1822
  /**
   * @return the sameModifiedUser
   */
  public boolean isSameModifiedUser() {
    return this.sameModifiedUser;
  }


  /**
   * @param sameModifiedUser the sameModifiedUser to set
   */
  public void setSameModifiedUser(final boolean sameModifiedUser) {
    this.sameModifiedUser = sameModifiedUser;
  }

  /**
   * @return the attrValMapIncomplete
   */
  public boolean isAttrValMapIncomplete() {
    return this.attrValMapIncomplete;
  }

  /**
   * @param attrValMapIncomplete the attrValMapIncomplete to set
   */
  public void setAttrValMapIncomplete(final boolean attrValMapIncomplete) {
    this.attrValMapIncomplete = attrValMapIncomplete;
  }

  /**
   * @return the attrributesIncomplete
   */
  public Set<D> getAttrributesIncomplete() {
    return this.attrributesIncomplete;
  }

  /**
   * @param attrributesIncomplete the attrributesIncomplete to set
   */
  public void setAttrributesIncomplete(final Set<D> attrributesIncomplete) {
    this.attrributesIncomplete = attrributesIncomplete;
  }


  /**
   * @return the sameBitWise
   */
  public boolean isSameBitWise() {
    return this.sameBitWise;
  }

  /**
   * @param sameBitWise
   */
  public void setSameBitWise(final boolean sameBitWise) {
    this.sameBitWise = sameBitWise;

  }
}
