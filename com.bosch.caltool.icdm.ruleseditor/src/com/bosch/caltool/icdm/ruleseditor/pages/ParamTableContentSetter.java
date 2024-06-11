/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.pages;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.nebula.jface.gridviewer.GridTableViewer;

import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.IParamRuleResponse;
import com.bosch.caltool.icdm.model.cdr.ConfigBasedParam;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameterAttr;
import com.bosch.caltool.icdm.ruleseditor.Activator;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditor;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author rgo7cob
 */
public class ParamTableContentSetter {


  /**
   * Set input for Rule set
   *
   * @param refreshNeeded refreshNeeded
   * @param tableViewer tableViewer
   * @param editor editor
   * @param paramCol paramCol
   */
  public void setInputForRuleSet(final boolean refreshNeeded, final GridTableViewer tableViewer,
      final ReviewParamEditor editor, final ParamCollection paramCol, final AbstractFormPage page) {
    try {
      if (tableViewer != null) {
        Map<String, ?> paramMap = null;
        Map<IParameter, ConfigBasedParam> setConfigParams = null;

        // Set input for List, details page if the value is not loaded

        if (!refreshNeeded) {

          if ((editor.getEditorInput().getParamMap() != null) && !editor.getEditorInput().getParamMap().isEmpty()) {
            paramMap = editor.getEditorInput().getParamMap();
          }

          if ((editor.getEditorInput().getConfigParamMap() != null) &&
              !editor.getEditorInput().getConfigParamMap().isEmpty()) {
            setConfigParams = editor.getEditorInput().getConfigParamMap();
          }

        }
        else {
          tableViewer.getGrid().removeAll();
        }
        if (paramMap == null) {
          IParamRuleResponse<RuleSetParameterAttr, RuleSetParameter> paramRulesOutput =
              editor.getEditorInput().getDataProvider().getRulesOutput(paramCol.getId());
          ParameterDataProvider<RuleSetParameterAttr, RuleSetParameter> paramDataProvider =
              new ParameterDataProvider<>(paramRulesOutput);
          editor.getEditorInput().setParamDataProvider(paramDataProvider);

          editor.getParamRulesPage().setParameterDataProvider(paramDataProvider);
          editor.getListPage().setParameterDataProvider(paramDataProvider);
          paramMap = paramRulesOutput.getParamMap();
        }
        if ((setConfigParams == null) && (page instanceof ConfigBasedRulesPage)) {
          setConfigParams = getConfigParams(tableViewer, editor, page, paramMap);
        }
        if ((paramMap != null) && !(page instanceof ConfigBasedRulesPage)) {
          editor.getEditorInput().setParamMap(paramMap);
          if (refreshNeeded || (tableViewer.getGrid().getItemCount() == 0) ||
              (tableViewer.getGrid().getItemCount() != paramMap.size())) {
            tableViewer.setInput(paramMap.values());
          }
        }
        else if (setConfigParams != null) {
          tableViewer.getGrid().removeAll();
          tableViewer.setInput(setConfigParams.values());
        }
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }


  /**
   * @param tableViewer
   * @param editor
   * @param page
   * @param paramMap
   * @param setConfigParams
   * @return
   */
  private Map<IParameter, ConfigBasedParam> getConfigParams(final GridTableViewer tableViewer,
      final ReviewParamEditor editor, final AbstractFormPage page, final Map<String, ?> paramMap) {
    Map<IParameter, ConfigBasedParam> setConfigParams = null;
    if (configParamsNeed(page)) {
      setConfigParams = setConfigParams(paramMap, editor);
      tableViewer.setInput(setConfigParams.values());

    }
    return setConfigParams;
  }


  /**
   * @param allParamsMap
   * @param editor
   * @return
   */
  private Map<IParameter, ConfigBasedParam> setConfigParams(final Map<String, ?> allParamsMap,
      final ReviewParamEditor editor) {
    Map<IParameter, ConfigBasedParam> configParamMap = new ConcurrentHashMap<>();
    for (Object funcParam : allParamsMap.values()) {
      ConfigBasedParam configParam = createConfigBasedParam((IParameter) funcParam);
      configParamMap.put((IParameter) funcParam, configParam);
    }
    editor.getEditorInput().setConfigParamMap(configParamMap);
    return configParamMap;
  }


  /**
   * @param funcParam
   * @return
   */
  private ConfigBasedParam createConfigBasedParam(final IParameter funcParam) {
    ConfigBasedParam configParam = new ConfigBasedParam(funcParam);
    configParam.setChecked(true);
    return configParam;
  }


  private boolean configParamsNeed(final AbstractFormPage page) {

    return page instanceof ConfigBasedRulesPage;

  }

}
