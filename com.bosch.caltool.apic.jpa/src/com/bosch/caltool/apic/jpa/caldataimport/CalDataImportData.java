/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.caldataimport;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.apic.jpa.bo.Attribute;
import com.bosch.caltool.apic.jpa.bo.AttributeValue;
import com.bosch.caltool.apic.jpa.bo.AttributeValueModel;
import com.bosch.caltool.apic.jpa.bo.IAttributeMappedObject;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.FeatureValueModel;


/**
 * @author bne4cob
 */
public class CalDataImportData {

  /**
   * Input file name, provided by user
   */
  private Set<String> fileNames;


  /**
   * Map of key- param name and value- CDRRule
   */
  private final Map<String, CDRRule> inputDataMap = new ConcurrentHashMap<>();
  /**
   * Stores the attributes, values selected by user
   */
  private final Map<Attribute, AttributeValue> attrValMap = new ConcurrentHashMap<>();

  /**
   * Records to be updated
   */
  private final Map<String, List<CalDataImportComparison>> calDataCompMap = new ConcurrentHashMap<>();

  /**
   * Map of parameter details, loaded using param info loader
   */
  private final Map<String, Map<String, String>> paramDetMap = new ConcurrentHashMap<>();


  /**
   * Invalid parameters
   */
  private final Set<String> invalidParamsSet = new HashSet<>();

  /**
   * Existing SSD rules
   */
  private final Map<String, CDRRule> existingSSDRuleMap = new ConcurrentHashMap<>();
  /**
   * Existing SSD rules
   */
  private final Map<String, Set<CDRRule>> existingSSDRuleListMap = new ConcurrentHashMap<>();
  /**
   * flag to prevent re-loading of rules, file parsing etc.
   */
  private boolean rulesRead;

  /**
   * <b>ICDM - 1403</b></br>
   * Map which contains the Attribute Value combinations specific to IAttributeMappedObject</br>
   * In case of RuleSet IAttributeMappedObject is a RuleSetParameter for which there can be multiple entries as Key</br>
   * In case of CompPac IAttributeMappedObject is a CompPac for which there can be only one entry as Key
   */
  private final Map<IAttributeMappedObject, Map<Integer, Set<AttributeValueModel>>> mappedObjCombiAttrValModelMap =
      new ConcurrentHashMap<>();
  /**
   * <b>ICDM - 1403</b></br>
   * Map which contains the Feature Value combinations specific to IAttributeMappedObject's <b>NAME</b></br>
   * In case of RuleSet IAttributeMappedObject is a RuleSetParameter for which there can be multiple entries as Key</br>
   * In case of CompPac IAttributeMappedObject is a CompPac for which there can be only one entry as Key
   */
  private final Map<String, Map<Integer, Set<FeatureValueModel>>> mapdObjFeaValMap = new ConcurrentHashMap<>();

  /**
   * map of parameter name and type
   */
  private Map<String, String> paramNameType;

  /**
   * param name and class map
   */
  private Map<String, String> paramClasses;


  /**
   * param name and hint map
   */
  private Map<String, String> paramHints;


  /**
   * @return the paramHints
   */
  public Map<String, String> getParamHints() {
    return this.paramHints;
  }


  /**
   * @return the paramClasses
   */
  public Map<String, String> getParamClasses() {
    return this.paramClasses;
  }


  /**
   * @return the input file Name
   */
  public Set<String> getFileName() {
    return this.fileNames;
  }


  /**
   * @param set the input file Name to set
   */
  public void setFileName(final Set<String> set) {
    this.fileNames = set;
    reset();
  }


  /**
   * NOTE : Use this getter only to fetch the details. Use <code>setAttrVals()</code> to set the new values
   *
   * @return the attrValMap
   */
  public Map<Attribute, AttributeValue> getAttrValMap() {
    return this.attrValMap;
  }

  /**
   * Set the attribute values selected by the user
   *
   * @param newAttrValMap map of attribute and values
   */
  public void setAttrVals(final Map<Attribute, AttributeValue> newAttrValMap) {
    boolean attrValChanged = false;
    // Attribute value input has changed if
    // a) value of any attribute is different
    // b) old entry count is not same as new entry count(for first time set)

    if (this.attrValMap.size() == newAttrValMap.size()) {
      for (Entry<Attribute, AttributeValue> oldAVEntry : this.attrValMap.entrySet()) {
        Attribute attr = oldAVEntry.getKey();
        if (!CommonUtils.isEqual(this.attrValMap.get(attr), newAttrValMap.get(attr))) {
          attrValChanged = true;
          break;
        }
      }
    }
    else {
      attrValChanged = true;
    }

    // If there is a change, clear all existing, and add all the new items
    if (attrValChanged) {
      this.attrValMap.clear();
      this.attrValMap.putAll(newAttrValMap);
      reset();
    }
    // In case where new parameter is added and there is no attr value dependency the below map would be empty.
    // Only if reset is called the readrule flag is reset
    if (newAttrValMap.isEmpty()) {
      reset();
    }
  }


  /**
   * * TODO Check for If there is a change, clear all existing, and add all the new items
   *
   * @param validAttrValMap
   */
  public void setMappedObjCombiAttrValModel(
      final Map<IAttributeMappedObject, Map<Integer, Set<AttributeValueModel>>> validAttrValMap) {
    this.mappedObjCombiAttrValModelMap.clear();
    this.mappedObjCombiAttrValModelMap.putAll(validAttrValMap);
    reset();
  }


  /**
   * @return Set of input parameters
   */
  public Set<String> getInputParams() {
    return getInputDataMap().keySet();
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
   * @return the existingSSDRuleMap
   */
  public Map<String, CDRRule> getExistingSSDRuleMap() {
    return this.existingSSDRuleMap;
  }

  /**
   * @return the existingSSDRuleMap
   */
  public Map<String, Set<CDRRule>> getExistingSSDRuleListMap() {
    return this.existingSSDRuleListMap;
  }

  /**
   * @return true, if rules to be created
   */
  public boolean hasRulesToCreate() {
    for (String paramName : this.calDataCompMap.keySet()) {
      for (CalDataImportComparison compObj : this.calDataCompMap.get(paramName)) {
        if (compObj.isInsertRule() && compObj.isUpdateInDB()) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Retruns true if there are any rules to be updated
   *
   * @return true, if rules are present to be updated in DB, as per the user selection
   */
  public boolean hasRulesToUpdate() {
    for (String paramName : this.calDataCompMap.keySet()) {
      for (CalDataImportComparison compObj : this.calDataCompMap.get(paramName)) {
        if (!compObj.isInsertRule() && compObj.isUpdateInDB()) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * @return the updateDataMap
   */
  public Map<String, List<CalDataImportComparison>> getCalDataCompMap() {
    return this.calDataCompMap;
  }


  /**
   * @return set of invalid parameters
   */
  public Set<String> getInvalidParamSet() {
    return this.invalidParamsSet;
  }

  /**
   * @return the paramDetMap
   */
  public Map<String, Map<String, String>> getParamDetMap() {
    return this.paramDetMap;
  }


  /**
   * Reset the data
   */
  private void reset() {
    this.paramDetMap.clear();
    this.calDataCompMap.clear();
    this.invalidParamsSet.clear();
    this.existingSSDRuleMap.clear();
    this.existingSSDRuleListMap.clear();
    this.mapdObjFeaValMap.clear();
    this.rulesRead = false;
  }


  /**
   * @return the commonCombiAttrValModel
   */
  public Map<IAttributeMappedObject, Map<Integer, Set<AttributeValueModel>>> getMappedObjCombiAttrValModelMap() {
    return this.mappedObjCombiAttrValModelMap;
  }

  /**
   * @return the commonCombiAttrValModel
   */
  public Map<String, Map<Integer, Set<FeatureValueModel>>> getMappedObjNameCombiFeaValModelMap() {
    return this.mapdObjFeaValMap;
  }


  /**
   * @return the inputDataMap
   */
  public Map<String, CDRRule> getInputDataMap() {
    return this.inputDataMap;
  }


  /**
   * @param paramNameType Map<String, String>
   */
  public void setParamNameTypeMap(final Map<String, String> paramNameType) {
    this.paramNameType = paramNameType;
  }


  /**
   * @return the paramNameType
   */
  public Map<String, String> getParamNameType() {
    return this.paramNameType;
  }


  /**
   * @param paramClasses Map of param name and class
   */
  public void setParamClasses(final Map<String, String> paramClasses) {
    this.paramClasses = paramClasses;
  }


  /**
   * @param paramHint map
   */
  public void setParamHints(final Map<String, String> paramHint) {
    this.paramHints = paramHint;
  }


}
