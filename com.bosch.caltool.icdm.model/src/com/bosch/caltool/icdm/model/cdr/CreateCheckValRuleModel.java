/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.SortedSet;

import com.bosch.caltool.icdm.model.a2l.ParameterRulesResponse;
import com.bosch.caltool.icdm.model.a2l.RuleSetRulesResponse;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;

/**
 * @author bru2cob
 */
public class CreateCheckValRuleModel {


  private ReviewRule ruleToBeEdited;

  private String erroMsg;

  private boolean canCreateRule;

  private SortedSet<AttributeValueModel> attrValModel;

  private ParameterRulesResponse paramRules;

  private RuleSetRulesResponse rulesetRules;

  private String pidcVersName;

  private String resultName;

  private byte[] checkedValueObj;

  /**
   * @return the ruleToBeEdited
   */
  public ReviewRule getRuleToBeEdited() {
    return this.ruleToBeEdited;
  }


  /**
   * @param ruleToBeEdited the ruleToBeEdited to set
   */
  public void setRuleToBeEdited(final ReviewRule ruleToBeEdited) {
    this.ruleToBeEdited = ruleToBeEdited;
  }


  /**
   * @return the erroMsg
   */
  public String getErroMsg() {
    return this.erroMsg;
  }


  /**
   * @param erroMsg the erroMsg to set
   */
  public void setErroMsg(final String erroMsg) {
    this.erroMsg = erroMsg;
  }


  /**
   * @return the canCreateRule
   */
  public boolean isCanCreateRule() {
    return this.canCreateRule;
  }


  /**
   * @param canCreateRule the canCreateRule to set
   */
  public void setCanCreateRule(final boolean canCreateRule) {
    this.canCreateRule = canCreateRule;
  }


  /**
   * @return the attrValModel
   */
  public SortedSet<AttributeValueModel> getAttrValModel() {
    return this.attrValModel;
  }


  /**
   * @param attrValModel the attrValModel to set
   */
  public void setAttrValModel(final SortedSet<AttributeValueModel> attrValModel) {
    this.attrValModel = attrValModel;
  }


  /**
   * @return the paramRules
   */
  public ParameterRulesResponse getParamRules() {
    return this.paramRules;
  }


  /**
   * @param paramRules the paramRules to set
   */
  public void setParamRules(final ParameterRulesResponse paramRules) {
    this.paramRules = paramRules;
  }


  /**
   * @return the rulesetRules
   */
  public RuleSetRulesResponse getRulesetRules() {
    return this.rulesetRules;
  }


  /**
   * @param rulesetRules the rulesetRules to set
   */
  public void setRulesetRules(final RuleSetRulesResponse rulesetRules) {
    this.rulesetRules = rulesetRules;
  }


  /**
   * @return the pidcVersName
   */
  public String getPidcVersName() {
    return this.pidcVersName;
  }


  /**
   * @param pidcVersName the pidcVersName to set
   */
  public void setPidcVersName(final String pidcVersName) {
    this.pidcVersName = pidcVersName;
  }


  /**
   * @return the resultName
   */
  public String getResultName() {
    return this.resultName;
  }


  /**
   * @param resultName the resultName to set
   */
  public void setResultName(final String resultName) {
    this.resultName = resultName;
  }


  /**
   * @return the checkedValueObj
   */
  public byte[] getCheckedValueObj() {
    return this.checkedValueObj;
  }


  /**
   * @param checkVal the checkedValueObj to set
   */
  public void setCheckedValueObj(final byte[] checkVal) {
    this.checkedValueObj = checkVal;
  }


}
