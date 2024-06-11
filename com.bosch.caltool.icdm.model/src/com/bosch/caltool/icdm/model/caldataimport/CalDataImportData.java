/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.caldataimport;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.a2l.ParameterRulesResponse;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;


/**
 * @author bne4cob
 */
public class CalDataImportData {

  /**
   * Input file name, provided by user
   */
  private Set<String> fileNames;


  /**
   * Map of key- param name and value- ReviewRule
   */
  private final Map<String, ReviewRule> inputDataMap = new ConcurrentHashMap<>();
  /**
   * Stores the attributes, values selected by user
   */
  private final Map<Long, AttributeValue> attrValMap = new ConcurrentHashMap<>();

  /**
   * Records to be updated
   */
  private final Map<String, List<CalDataImportComparisonModel>> calDataCompMap = new ConcurrentHashMap<>();

  /**
   * Map of parameter details, loaded using param info loader
   */
  private final Map<String, Map<String, String>> paramDetMap = new ConcurrentHashMap<>();


  /**
   * Existing SSD rules
   */
  private final Map<String, ReviewRule> existingSSDRuleMap = new ConcurrentHashMap<>();
  /**
   * Existing SSD rules
   */
  private final Map<String, Set<ReviewRule>> existingSSDRuleListMap = new ConcurrentHashMap<>();
  /**
   * flag to prevent re-loading of rules, file parsing etc.
   */
  private boolean rulesRead;
  /**
   * Total number of parameters available in file
   */
  private int totalNoOfParams;

  /**
   * <b>ICDM - 1403</b></br>
   * Map which contains the Attribute Value combinations specific to IAttributeMappedObject</br>
   * In case of RuleSet IAttributeMappedObject is a RuleSetParameter for which there can be multiple entries as Key</br>
   * In case of CompPac IAttributeMappedObject is a CompPac for which there can be only one entry as Key
   */
  private Map<String, Map<Integer, Set<AttributeValueModel>>> mappedObjCombiAttrValModelMap = new ConcurrentHashMap<>();
  /**
   * <b>ICDM - 1403</b></br>
   * Map which contains the Feature Value combinations specific to IAttributeMappedObject's <b>NAME</b></br>
   * In case of RuleSet IAttributeMappedObject is a RuleSetParameter for which there can be multiple entries as Key</br>
   * In case of CompPac IAttributeMappedObject is a CompPac for which there can be only one entry as Key
   */
  private Map<String, Map<Integer, Set<AttributeValueModel>>> mapdObjFeaValMap = new ConcurrentHashMap<>();


  /**
   * param name and class map
   */
  private Map<String, String> paramClasses;


  /**
   * param name and hint map
   */
  private Map<String, String> paramHints;


  private Set<String> invalidParamSet = new HashSet<>();


  private Map<String, String> paramNameTypeMap;


  private List<String> paramsTobeInserted;


  private Map<String, Function> paramFuncObjMap;


  private Map<String, Parameter> paramNameObjMap;


  private Map<String, Map<String, String>> newParamPropsMap;


  private Map<String, String> oldParamClassMap;
  private final Map<String, String> paramFuncMap = new HashMap<String, String>();

  private Map<String, ParameterRulesResponse> funcParamRespMap = new HashMap<String, ParameterRulesResponse>();

  /**
   * @return the funcParamRespMap
   */
  public Map<String, ParameterRulesResponse> getFuncParamRespMap() {
    return this.funcParamRespMap;
  }


  /**
   * @param funcParamRespMap the funcParamRespMap to set
   */
  public void setFuncParamRespMap(final Map<String, ParameterRulesResponse> funcParamRespMap) {
    this.funcParamRespMap = funcParamRespMap;
  }


  /**
   * @return the paramFuncMap
   */
  public Map<String, String> getParamFuncMap() {
    return this.paramFuncMap;
  }


  /**
   * @param paramFuncObjMap the paramFuncObjMap to set
   */
  public void setParamFuncObjMap(final Map<String, Function> paramFuncObjMap) {
    this.paramFuncObjMap = paramFuncObjMap;
  }


  /**
   * @param paramNameObjMap the paramNameObjMap to set
   */
  public void setParamNameObjMap(final Map<String, Parameter> paramNameObjMap) {
    this.paramNameObjMap = paramNameObjMap;
  }


  /**
   * @return the paramFuncObjMap
   */
  public Map<String, Function> getParamFuncObjMap() {
    return this.paramFuncObjMap;
  }


  /**
   * @return the paramNameObjMap
   */
  public Map<String, Parameter> getParamNameObjMap() {
    return this.paramNameObjMap;
  }


  /**
   * @return the fileNames
   */
  public Set<String> getFileNames() {
    return this.fileNames;
  }


  /**
   * @param fileNames the fileNames to set
   */
  public void setFileNames(final Set<String> fileNames) {
    this.fileNames = fileNames;
  }


  /**
   * @return the rulesRead
   */
  public boolean isRulesRead() {
    return this.rulesRead;
  }


  /**
   * @param rulesRead the rulesRead to set
   */
  public void setRulesRead(final boolean rulesRead) {
    this.rulesRead = rulesRead;
  }


  /**
   * @return the paramClasses
   */
  public Map<String, String> getParamClasses() {
    return this.paramClasses;
  }


  /**
   * @param paramClasses the paramClasses to set
   */
  public void setParamClasses(final Map<String, String> paramClasses) {
    this.paramClasses = paramClasses;
  }


  /**
   * @return the paramHints
   */
  public Map<String, String> getParamHints() {
    return this.paramHints;
  }


  /**
   * @param paramHints the paramHints to set
   */
  public void setParamHints(final Map<String, String> paramHints) {
    this.paramHints = paramHints;
  }


  /**
   * @return the inputDataMap
   */
  public Map<String, ReviewRule> getInputDataMap() {
    return this.inputDataMap;
  }


  /**
   * @return the attrValMap
   */
  public Map<Long, AttributeValue> getAttrValMap() {
    return this.attrValMap;
  }


  /**
   * @return the paramDetMap
   */
  public Map<String, Map<String, String>> getParamDetMap() {
    return this.paramDetMap;
  }


  /**
   * @return the existingSSDRuleMap
   */
  public Map<String, ReviewRule> getExistingSSDRuleMap() {
    return this.existingSSDRuleMap;
  }


  /**
   * @return the existingSSDRuleListMap
   */
  public Map<String, Set<ReviewRule>> getExistingSSDRuleListMap() {
    return this.existingSSDRuleListMap;
  }


  /**
   * @return the mappedObjCombiAttrValModelMap
   */
  public Map<String, Map<Integer, Set<AttributeValueModel>>> getMappedObjCombiAttrValModelMap() {
    return this.mappedObjCombiAttrValModelMap;
  }


  /**
   * @param validAttrValModel the mappedObjCombiAttrValModelMap to set
   */
  public void setMappedObjCombiAttrValModelMap(
      final Map<String, Map<Integer, Set<AttributeValueModel>>> validAttrValModel) {
    this.mappedObjCombiAttrValModelMap = validAttrValModel;
  }


  /**
   * @return the mapdObjFeaValMap
   */
  public Map<String, Map<Integer, Set<AttributeValueModel>>> getMapdObjFeaValMap() {
    return this.mapdObjFeaValMap;
  }


  /**
   * @param mapdObjFeaValMap the mapdObjFeaValMap to set
   */
  public void setMapdObjFeaValMap(final Map<String, Map<Integer, Set<AttributeValueModel>>> mapdObjFeaValMap) {
    this.mapdObjFeaValMap = mapdObjFeaValMap;
  }


  /**
   * @return
   */
  public Set<String> getInvalidParamSet() {

    return this.invalidParamSet;
  }


  /**
   * @param paramNameType2
   */
  public void setParamNameTypeMap(final Map<String, String> paramNameTypeMap) {
    this.paramNameTypeMap = paramNameTypeMap;

  }


  /**
   * @return the paramNameTypeMap
   */
  public Map<String, String> getParamNameTypeMap() {
    return this.paramNameTypeMap;
  }


  /**
   * @param invalidParamSet the invalidParamSet to set
   */
  public void setInvalidParamSet(final Set<String> invalidParamSet) {
    this.invalidParamSet = invalidParamSet;
  }


  /**
   * @return the calDataCompMap
   */
  public Map<String, List<CalDataImportComparisonModel>> getCalDataCompMap() {
    return this.calDataCompMap;
  }


  /**
   * @param paramsTobeInserted paramsTobeInserted
   */
  public void setParamsTobeInserted(final List<String> paramsTobeInserted) {
    this.paramsTobeInserted = paramsTobeInserted;

  }


  /**
   * @return the paramsTobeInserted
   */
  public List<String> getParamsTobeInserted() {
    return this.paramsTobeInserted;
  }


  /**
   * @param newParamPropsMap
   */
  public void setNewParamPropsMap(final Map<String, Map<String, String>> newParamPropsMap) {
    this.newParamPropsMap = newParamPropsMap;

  }


  /**
   * @param oldParamClassMap
   */
  public void setOldParamClassMap(final Map<String, String> oldParamClassMap) {
    this.oldParamClassMap = oldParamClassMap;
  }


  /**
   * @return the newParamPropsMap
   */
  public Map<String, Map<String, String>> getNewParamPropsMap() {
    return this.newParamPropsMap;
  }


  /**
   * @return the oldParamClassMap
   */
  public Map<String, String> getOldParamClassMap() {
    return this.oldParamClassMap;
  }


  /**
   * @return the totalNoOfParams
   */
  public int getTotalNoOfParams() {
    return this.totalNoOfParams;
  }


  /**
   * @param totalNoOfParams the totalNoOfParams to set
   */
  public void setTotalNoOfParams(final int totalNoOfParams) {
    this.totalNoOfParams = totalNoOfParams;
  }


}
