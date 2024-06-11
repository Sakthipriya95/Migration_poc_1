/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l.precal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeModel;
import com.bosch.caltool.icdm.model.a2l.precal.PRECAL_SOURCE_TYPE;
import com.bosch.caltool.icdm.model.a2l.precal.PreCalInputData;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.ssd.icdm.model.CDRRule;

/**
 * @author bne4cob
 */
class PreCalProcessData {

  private final PreCalInputData input;

  private PRECAL_SOURCE_TYPE sourceType;


  /**
   * Key - Attribute ID, Value - attribute
   */
  private final Map<Long, Attribute> attrMap = new HashMap<>();
  /**
   * Key - value ID, value - attribute value
   */
  private final Map<Long, AttributeValue> attrValMap = new HashMap<>();

  /**
   * Key - Attribute ID, Value - pidc attribute
   */
  private final Map<Long, PidcVersionAttribute> pidcAttrMap = new HashMap<>();
  /**
   * Key - Attribute ID, Value - variant attribute
   */
  private final Map<Long, PidcVariantAttribute> pidcVarAttrMap = new HashMap<>();
  /**
   * Key - parameter name, Value - set of attribute IDs
   */
  private final Map<String, Set<Long>> paramAttrMap = new HashMap<>();
  /**
   * Key -param name , Value - parameter ID
   */
  private final Map<String, Long> paramIdMap = new HashMap<>();

  private PidcVersionAttributeModel projAttrModel;

  private Set<String> paramsToProcessSet = new HashSet<>();

  /**
   * Key - input parameter name, value - equivalent base parameter name
   */
  private final Map<String, String> paramBaseParamMap = new HashMap<>();

  private PidcA2l pidcA2l;
  private PidcVersion pidcVers;
  private String projInfo;
  private RuleSet ruleSet;

  /**
   * Key - parameter name, value rule
   */
  private final Map<String, CDRRule> cdrRuleMap = new HashMap<>();
  /**
   * Key- parameter name, Value - cal data object, serialized and zipped
   */
  private final Map<String, byte[]> preCalDataMap = new HashMap<>();

  private Set<String> varCodeParamsToExcludeSet = new HashSet<>();


  /**
   * @param input Pre Cal Input Data
   */
  public PreCalProcessData(final PreCalInputData input) {
    super();
    this.input = input;
  }

  /**
   * @return the input
   */
  public PreCalInputData getInput() {
    return this.input;
  }

  /**
   * @return the sourceType
   */
  public PRECAL_SOURCE_TYPE getSourceType() {
    return this.sourceType;
  }

  /**
   * @param sourceType the sourceType to set
   */
  public void setSourceType(final PRECAL_SOURCE_TYPE sourceType) {
    this.sourceType = sourceType;
  }

  /**
   * @return the attrMap
   */
  public Map<Long, Attribute> getAttrMap() {
    return this.attrMap;
  }


  /**
   * @return the pidcAttrMap
   */
  public Map<Long, PidcVersionAttribute> getPidcAttrMap() {
    return this.pidcAttrMap;
  }

  /**
   * @return the pidcVarAttrMap
   */
  public Map<Long, PidcVariantAttribute> getPidcVarAttrMap() {
    return this.pidcVarAttrMap;
  }


  /**
   * @return the paramAttrMap
   */
  public Map<String, Set<Long>> getParamAttrMap() {
    return this.paramAttrMap;
  }

  /**
   * @return the paramIdMap
   */
  public Map<String, Long> getParamIdMap() {
    return this.paramIdMap;
  }

  /**
   * @return the projAttrModel
   */
  public PidcVersionAttributeModel getProjAttrModel() {
    return this.projAttrModel;
  }


  /**
   * @param projAttrModel the projAttrModel to set
   */
  public void setProjAttrModel(final PidcVersionAttributeModel projAttrModel) {
    this.projAttrModel = projAttrModel;
  }


  /**
   * @return the paramBaseParamMap
   */
  public Map<String, String> getParamBaseParamMap() {
    return this.paramBaseParamMap;
  }

  /**
   * @return the preCalDataMap
   */
  public Map<String, byte[]> getPreCalDataMap() {
    return this.preCalDataMap;
  }

  /**
   * @return the pidcA2l
   */
  public PidcA2l getPidcA2l() {
    return this.pidcA2l;
  }

  /**
   * @param pidcA2l the pidcA2l to set
   */
  public void setPidcA2l(final PidcA2l pidcA2l) {
    this.pidcA2l = pidcA2l;
  }

  /**
   * @return the pidcVers
   */
  public PidcVersion getPidcVers() {
    return this.pidcVers;
  }

  /**
   * @param pidcVers the pidcVers to set
   */
  public void setPidcVers(final PidcVersion pidcVers) {
    this.pidcVers = pidcVers;
  }

  /**
   * @return the projInfo
   */
  public String getProjInfo() {
    return this.projInfo;
  }

  /**
   * @param projInfo the projInfo to set
   */
  public void setProjInfo(final String projInfo) {
    this.projInfo = projInfo;
  }

  /**
   * @return the ruleSet
   */
  public RuleSet getRuleSet() {
    return this.ruleSet;
  }

  /**
   * @param ruleSet the ruleSet to set
   */
  public void setRuleSet(final RuleSet ruleSet) {
    this.ruleSet = ruleSet;
  }

  /**
   * @return the cdrRuleMap
   */
  public Map<String, CDRRule> getCdrRuleMap() {
    return this.cdrRuleMap;
  }

  /**
   * @return the paramsToProcessSet
   */
  public Set<String> getParamsToProcessSet() {
    return this.paramsToProcessSet;
  }

  /**
   * @param paramsToProcessSet the paramsToProcessSet to set
   */
  public void setParamsToProcessSet(final Set<String> paramsToProcessSet) {
    this.paramsToProcessSet = new HashSet<>(paramsToProcessSet);
  }

  /**
   * @return variant coded params to exclude from data fetch
   */
  public Set<String> getVarCodeParamsToExcludeSet() {
    return new HashSet<>(this.varCodeParamsToExcludeSet);
  }

  /**
   * @param varCodeParamsToExcludeSet the varCodeParamsToExcludeSet to set
   */
  public void setVarCodeParamsToExcludeSet(final Set<String> varCodeParamsToExcludeSet) {
    this.varCodeParamsToExcludeSet = new HashSet<>(varCodeParamsToExcludeSet);
  }

  /**
   * @return AttrValMap
   */
  public Map<Long, AttributeValue> getAttrValMap() {
    return this.attrValMap;
  }

}
