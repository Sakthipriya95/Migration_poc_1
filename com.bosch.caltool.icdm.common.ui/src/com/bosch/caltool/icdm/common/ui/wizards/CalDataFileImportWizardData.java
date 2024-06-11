/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.wizards;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.IParamRuleResponse;
import com.bosch.caltool.icdm.model.apic.ApicConstants.READY_FOR_SERIES;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportData;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;


/**
 * @author bru2cob
 */
public class CalDataFileImportWizardData {

  /**
   * Store the selected attrs and its values
   */
  private final Map<Attribute, AttributeValue> attrValMap = new HashMap<Attribute, AttributeValue>();
  /**
   * Hold the name of the file which is selected
   */
  private Set<String> filesSelected;

  /**
   * boolean to know whether an exception has occured
   */
  private boolean exceptionOccured;

  private final ParamCollection importObject;

  /**
   * Stores paramAttr and its selected values
   */
  private final Map<Attribute, SortedSet<AttributeValue>> attrVals =
      new HashMap<Attribute, SortedSet<AttributeValue>>();

  /**
   * variable to store default base comment
   */
  private String defaultBaseComment;

  /**
   * variable to store default maturity level
   */
  private String defaultMaturityLvl;
  /**
   * variable to store default exact match flag
   */
  private boolean defaultExactMatchFlag;
  /**
   * REVIEW_METHOD
   */
  private READY_FOR_SERIES readyForSeries;

  /**
   * param properties map
   */
  private Map<String, Map<String, String>> newParamPropsMap;

  /**
   * store initial class values
   */
  private final Map<String, String> oldParamPropsMap = new HashMap<>();
  private ParamCollectionDataProvider paramColDataProvider;
  private IParamRuleResponse paramRuleResponse;

  private String funcVersion;


  /**
   * Key for function name in parameter properties map
   */
  public static final String KEY_FUNCTION_NAME = "function";

  /**
   * Key for parameter name in parameter properties map
   */
  public static final String KEY_PARAM_NAME = "name";


  /**
   * Key for parameter class in parameter properties map
   */
  public static final String KEY_PARAM_CLASS = "pclass";

  /**
   * Key to indicate param props have changed
   */
  public static final String KEY_PROP_CHANGED = "change";

  /**
   * Key for parameter iscodeword in parameter properties map
   */
  public static final String KEY_CODE_WORD = "codeword";

  /**
   * Key for parameter long name in parameter properties map
   */
  public static final String KEY_LONG_NAME = "longname";

  /**
   * Key for parameter iscodeword in parameter properties map
   */
  public static final String KEY_CAL_HINT = "calhint";

  /**
   * Key for use check box of class
   */
  public static final String KEY_USE_CLASS = "usenewclass";

  /**
   * Key for parameter iscodeword in parameter properties map
   */
  public static final String KEY_BIT_WISE = "bitwise";

  /**
   * @param importObj the instance of object to be imported (CompPkg , RuleSet..)
   */
  public CalDataFileImportWizardData(final ParamCollection importObj) {
    this.importObject = importObj;
  }

  /**
   * @return the fileSelected
   */
  public Set<String> getFileSelected() {
    return this.filesSelected;
  }


  /**
   * @param fileNames the fileSelected to set
   */
  public void setFileSelected(final Set<String> fileNames) {
    this.filesSelected = fileNames == null ? null : new HashSet<>(fileNames);
  }


  /**
   * @return the attrValMap
   */
  public Map<Attribute, AttributeValue> getAttrValMap() {
    return this.attrValMap;
  }

  /**
   * @return the isExceptionOccured
   */
  public boolean isExceptionOccured() {
    return this.exceptionOccured;
  }

  /**
   * @param isExceptionOccured the isExceptionOccured to set
   */
  public void setExceptionOccured(final boolean isExceptionOccured) {
    this.exceptionOccured = isExceptionOccured;
  }

  /**
   * @return the importObject
   */
  public ParamCollection getImportObject() {
    return this.importObject;
  }

  /**
   * @return the attrVals
   */
  public Map<Attribute, SortedSet<AttributeValue>> getAttrVals() {
    return this.attrVals;
  }

  /**
   * @param baseComment String
   */
  public void setDefaultBaseComment(final String baseComment) {
    this.defaultBaseComment = baseComment;
  }

  /**
   * @param defaultMaturityLvl String
   */
  public void setDefaultMaturityLevel(final String defaultMaturityLvl) {
    this.defaultMaturityLvl = defaultMaturityLvl;
  }

  /**
   * @param selection boolean
   */
  public void setDefaultExactMatchFlag(final boolean selection) {
    this.defaultExactMatchFlag = selection;
  }

  /**
   * @param readyForSeries REVIEW_METHOD
   */
  public void setDefaultReadyForSer(final READY_FOR_SERIES readyForSeries) {
    this.readyForSeries = readyForSeries;
  }

  /**
   * @return the defaultBaseComment
   */
  public String getDefaultBaseComment() {
    return this.defaultBaseComment;
  }


  /**
   * @return the defaultMaturityLvl
   */
  public String getDefaultMaturityLvl() {
    return this.defaultMaturityLvl;
  }


  /**
   * @return the defaultExactMatchFlag
   */
  public boolean isDefaultExactMatchFlag() {
    return this.defaultExactMatchFlag;
  }


  /**
   * @return the readyForSeries
   */
  public READY_FOR_SERIES getReadyForSeries() {
    return this.readyForSeries;
  }

  /**
   * @return the newParamPropsMap
   */
  public Map<String, Map<String, String>> getNewParamPropsMap() {
    return this.newParamPropsMap;
  }

  /**
   * @param newParamPropsMap the newParamPropsMap to set
   */
  public void setNewParamPropsMap(final Map<String, Map<String, String>> newParamPropsMap) {
    this.newParamPropsMap = newParamPropsMap;
  }

  /**
   * this method initialises hint text for parameters
   *
   * @param calImportData
   */
  public void initialiseHintAndClass(final CalDataImportData calImportData) {
    // initialise class values with values from imported file

    for (Map<String, String> props : this.newParamPropsMap.values()) {
      String paramClass = "";
      if (null != calImportData.getParamClasses()) {
        // if class is available
        paramClass = calImportData.getParamClasses().get(props.get(KEY_PARAM_NAME));
      }
      // store the old value in old props map
      this.oldParamPropsMap.put(props.get(KEY_PARAM_NAME), props.get(KEY_PARAM_CLASS));
      // now replace the value from imported file
      props.put(KEY_PARAM_CLASS, "");
      if (CommonUtils.isNotNull(paramClass)) {
        // now replace the value from imported file
        props.put(KEY_PARAM_CLASS, paramClass.toUpperCase(Locale.getDefault()));
      }
    }


    // initialise the hint with icdm and imported values
    if ((null != calImportData.getParamHints()) && (this.importObject instanceof Function)) {
      for (Map<String, String> props : this.newParamPropsMap.values()) {
        String oldHint = props.get(KEY_CAL_HINT);
        String importedHint = calImportData.getParamHints().get(props.get(KEY_PARAM_NAME));
        boolean appendNewHint =
            !CommonUtils.isEmptyString(importedHint) && !CommonUtils.checkNull(oldHint).contains(importedHint);
        boolean appendDefaultComment = !CommonUtils.isEmptyString(this.defaultBaseComment) &&
            !CommonUtils.checkNull(oldHint).contains(this.defaultBaseComment);
        StringBuilder hint = new StringBuilder(100);
        if (!CommonUtils.isEmptyString(oldHint)) {
          // if old hint is not empty
          if (!oldHint.startsWith("iCDM:") && (appendNewHint || appendDefaultComment)) {
            hint.append("iCDM:\n");
          }
          hint.append(CommonUtils.checkNull(oldHint));
        }
        if (!CommonUtils.isEmptyString(oldHint) && (appendNewHint || appendDefaultComment)) {
          // if base comment is not empty
          hint.append("\n").append("Import -");
        }
        if (appendDefaultComment) {
          hint.append(this.defaultBaseComment);
        }
        if (appendNewHint && appendDefaultComment) {
          hint.append("\n");
        }
        if (appendNewHint) {
          hint.append(CommonUtils.checkNull(importedHint));
        }
        props.put(KEY_CAL_HINT, hint.toString());
      }
    }

  }

  /**
   * @return old map
   */
  public Map<String, String> getOldParamClassMap() {
    return this.oldParamPropsMap;
  }


  /**
   * @return the funcVersion
   */
  public String getFuncVersion() {
    return this.funcVersion;
  }


  /**
   * @param funcVersion the funcVersion to set
   */
  public void setFuncVersion(final String funcVersion) {
    this.funcVersion = funcVersion;
  }

  /**
   * @param paramColDataProvider
   */
  public void setParamColDataProvider(final ParamCollectionDataProvider paramColDataProvider) {
    this.paramColDataProvider = paramColDataProvider;

  }


  /**
   * @return the paramColDataProvider
   */
  public ParamCollectionDataProvider getParamColDataProvider() {
    return this.paramColDataProvider;
  }

  /**
   * @param parameterDataProvider parameterDataProvider
   */
  public void setParamRulesOutput(final IParamRuleResponse paramRulesOutput) {
    this.paramRuleResponse = paramRulesOutput;

  }


  /**
   * @return the parameterDataProvider
   */
  public IParamRuleResponse getParameterDataProvider() {
    return this.paramRuleResponse;
  }


  /**
   * @return the paramRuleResponse
   */
  public IParamRuleResponse getParamRuleResponse() {
    return this.paramRuleResponse;
  }


  /**
   * @param paramRuleResponse the paramRuleResponse to set
   */
  public void setParamRuleResponse(final IParamRuleResponse paramRuleResponse) {
    this.paramRuleResponse = paramRuleResponse;
  }
}
