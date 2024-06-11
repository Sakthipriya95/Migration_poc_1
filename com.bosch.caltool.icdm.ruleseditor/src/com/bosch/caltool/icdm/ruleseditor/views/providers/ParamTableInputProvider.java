/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.views.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.model.a2l.IParamRuleResponse;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.a2l.ParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.DefaultRuleDefinition;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditor;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditorInput;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author rgo7cob
 */
public class ParamTableInputProvider {

  private final ParamCollection cdrFunction;
  private final ReviewParamEditor editor;

  private String versionSel;


  private String chkVar;
  private final ReviewParamEditorInput editorInput;

  /**
   * @param cdrFunction cdrFunction
   * @param editor editorInput
   */
  public ParamTableInputProvider(final ParamCollection cdrFunction, final ReviewParamEditor editor) {
    this.cdrFunction = cdrFunction;
    this.editor = editor;
    this.editorInput = editor.getEditorInput();
  }


  /**
   * @return the map
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public Map<String, ?> getParamMap() throws ApicWebServiceException {
    Map<String, ?> paramMap;
    IParamRuleResponse<ParameterAttribute, Parameter> paramRulesOutput =
        this.editorInput.getDataProvider().getRulesOutput(this.cdrFunction.getName(), this.versionSel, this.chkVar);
    ParameterDataProvider paramDataProvider = new ParameterDataProvider(paramRulesOutput);
    this.editorInput.setParamDataProvider(paramDataProvider);
    this.editor.getParamRulesPage().setParameterDataProvider(paramDataProvider);
    this.editor.getListPage().setParameterDataProvider(paramDataProvider);
    paramMap = paramRulesOutput.getParamMap();
    this.editorInput.setParamMap(paramMap);
    this.editorInput.setParamRulesOutput(paramRulesOutput);
    return paramMap;
  }


  /**
   * @return
   */
  public List<Object> getTableInput(final Map<String, ?> paramsMap) {

    List<Object> inputList = new ArrayList<Object>();
    final Map<String, ?> allParamsMap = paramsMap;
    for (Object param : allParamsMap.values()) {

      IParameter funcParam = (IParameter) param;
      inputList.add(funcParam);

      // To be implmented for rules
      /**
       * Parameter with dependencies and one rule
       */

      ParameterDataProvider paramDataProvider = this.editorInput.getParamDataProvider();

      if (paramDataProvider.hasDependency(funcParam) && (paramDataProvider.getRuleList(funcParam) != null) &&
          !paramDataProvider.getRuleList(funcParam).isEmpty() &&
          (paramDataProvider.getRuleList(funcParam).size() == 1)) {
        inputList.add(getParamSingleRule(funcParam, paramDataProvider));
      }

      if ((paramDataProvider.getRuleList(funcParam) != null) && !paramDataProvider.getRuleList(funcParam).isEmpty() &&
          (paramDataProvider.getRuleList(funcParam).size() > 1)) {
        inputList.addAll(getParamMultipleRules(funcParam, paramDataProvider));
      }

    }

    return inputList;
  }

  /**
   * Returns the rule of the param
   *
   * @param param sel param
   * @param paramDataProvider
   * @return rule
   */
  private Object getParamSingleRule(final IParameter param, final ParameterDataProvider paramDataProvider) {
    /**
     * If the rule does not have dependenices , default rule is created
     */
    List<ReviewRule> ruleList = paramDataProvider.getRuleList(param);
    if (ruleList.get(0).getDependencyList().isEmpty()) {
      DefaultRuleDefinition defaultRuleDefinition = new DefaultRuleDefinition();
      defaultRuleDefinition.setReviewRule(ruleList.get(0));
      return defaultRuleDefinition;
    }
    /**
     * Rule with dependenices will be displayed
     */
    return ruleList.get(0);
  }

  /**
   * Returns array of rules
   *
   * @param param selected parameter
   * @return rules for sel param
   */
  private List<Object> getParamMultipleRules(final com.bosch.caltool.icdm.model.cdr.IParameter param,
      final ParameterDataProvider paramDataProvider) {
    List<Object> rules = new ArrayList<Object>();
    SortedSet<ReviewRule> sortedRuleSet = paramDataProvider.getSortedRuleSet(param);
    for (ReviewRule rule : sortedRuleSet) {
      /**
       * If the rule does not have dependenices , default rule is created
       */
      if (rule.getDependencyList().isEmpty()) {
        DefaultRuleDefinition defaultRuleDefinition = new DefaultRuleDefinition();
        defaultRuleDefinition.setReviewRule(rule);
        rules.add(defaultRuleDefinition);
      }
      else {
        rules.add(rule);
      }
    }
    return rules;
  }


  /**
   * @return the versionSel
   */
  public String getVersionSel() {
    return this.versionSel;
  }


  /**
   * @param versionSel the versionSel to set
   */
  public void setVersionSel(final String versionSel) {
    this.versionSel = versionSel;
  }


  /**
   * @return the chkVar
   */
  public String getChkVar() {
    return this.chkVar;
  }


  /**
   * @param chkVar the chkVar to set
   */
  public void setChkVar(final String chkVar) {
    this.chkVar = chkVar;
  }

}
