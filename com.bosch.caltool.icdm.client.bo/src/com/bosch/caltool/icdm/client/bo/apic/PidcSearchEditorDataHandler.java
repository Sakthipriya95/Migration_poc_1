/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.framework.AbstractClientDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.ChangeDataInfo;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSearchCondition;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSearchInput;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSearchResult;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcScoutServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

// ICDM-1135
/**
 * @author bru2cob
 */
public class PidcSearchEditorDataHandler extends AbstractClientDataHandler {

  /**
   * Key - level, value - attr ID
   */
  private final Map<Long, Long> levelAttrMap = new ConcurrentHashMap<>();

  /**
   * Map which holds the attrs for which used flag is set. Key - attr id, value - display text of used flag
   */
  private final Map<Long, String> selAttrUsedMap = new ConcurrentHashMap<>();
  /**
   * Map which holds the checked attrs and its selected values. Key - attr id, value - set of value id
   */
  private final Map<Long, Set<Long>> selAttrValMap = new ConcurrentHashMap<>();

  // ICDM-1158
  /**
   * element - attribute Id
   */
  private final Set<Long> attrPartiallyCheckedSet = new HashSet<>();
  // ICDM-1213
  private PidcVersion selPidcVer;

  private final Map<Long, Attribute> allAttrMap = new ConcurrentHashMap<>();

  private final Map<Long, AttributeValue> allAttrValMap = new ConcurrentHashMap<>();

  /**
   * Key - attr id, value - set of value id
   */
  private final Map<Long, Set<Long>> valByAttrMap = new ConcurrentHashMap<>();

  /**
   * {@inheritDoc}
   */
  @Override
  protected void registerForCns() {
    registerCns(this::loadData, MODEL_TYPE.ATTRIBUTE, MODEL_TYPE.ATTRIB_VALUE);

    registerCnsActionLocal(this::mergeAttribute, MODEL_TYPE.ATTRIBUTE);
    registerCnsActionLocal(this::mergeAttrValue, MODEL_TYPE.ATTRIB_VALUE);
  }

  /**
   * @return the levelAttrs
   */
  public Map<Long, Attribute> getLevelAttrs() {
    Map<Long, Attribute> retMap = new HashMap<>();
    this.levelAttrMap.forEach((lvl, attrId) -> retMap.put(lvl, getAttribute(attrId)));
    return retMap;
  }


  /**
   * @return selected pidc
   */
  public PidcVersion getSelectedPidcVersion() {
    return this.selPidcVer;
  }

  /**
   * @param pidcVer the PIDCVersion selected
   */
  public void setSelectedPidcVersion(final PidcVersion pidcVer) {
    this.selPidcVer = pidcVer;
  }

  /**
   * @return all sorted attributes
   */
  public SortedSet<Attribute> getAllAttrsSorted() {
    return new TreeSet<>(this.allAttrMap.values());
  }

  /**
   * @param attrId attribute id
   * @return attribute
   */
  public Attribute getAttribute(final Long attrId) {
    return this.allAttrMap.get(attrId);
  }

  /**
   * @param attrValId attr Val Id
   * @return AttributeValue
   */
  public AttributeValue getAttrValue(final Long attrValId) {
    return this.allAttrValMap.get(attrValId);
  }

  /**
   * @return Create search input
   */
  private PidcSearchInput createSearchInput() {

    final Set<PidcSearchCondition> searchConditions = new HashSet<>();

    // Create search conditions for attr-value mappings
    this.selAttrValMap.forEach((atrId, valIdSet) -> {
      PidcSearchCondition searchInp = new PidcSearchCondition();
      searchInp.setAttributeId(atrId);
      searchInp.getAttributeValueIds().addAll(valIdSet);
      // add the values to search conditions
      searchConditions.add(searchInp);
    });

    // Create search conditions for attr- used flag mappings
    this.selAttrUsedMap.forEach((attrId, flag) -> {
      PidcSearchCondition searchInp = new PidcSearchCondition();
      searchInp.setAttributeId(attrId);
      searchInp.setUsedFlag(flag);
      // add the values to search conditions
      searchConditions.add(searchInp);
    });

    PidcSearchInput srInput = new PidcSearchInput();
    srInput.setSearchConditions(searchConditions);

    return srInput;
  }

  /**
   * @return count of selected attributes
   */
  public int getSelAttrCount() {
    Set<Long> selAttrSet = this.selAttrUsedMap.entrySet().stream().filter(entry -> entry.getValue() != null)
        .map(Entry::getKey).collect(Collectors.toSet());

    this.selAttrValMap.forEach((attrId, selValSet) -> {
      if ((selValSet != null) && !selValSet.isEmpty()) {
        selAttrSet.add(attrId);
      }
    });

    return selAttrSet.size();
  }

  /**
   * Load data of this editor
   */
  public void loadData() {
    loadData(null);
  }

  private void loadData(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    loadAllAttributes();
    this.valByAttrMap.clear();
    this.allAttrValMap.clear();
  }

  private void loadAllAttributes() {
    try {
      Map<Long, Attribute> all = new AttributeServiceClient().getAll(false);

      this.allAttrMap.clear();
      this.allAttrMap.putAll(all);
      all.values().forEach(attr -> {
        if (attr.getLevel() > 0) {
          this.levelAttrMap.put(attr.getLevel(), attr.getId());
        }
      });

    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }


  /**
   * @param attrId attr Id
   */
  private void loadAttrValues(final Long attrId) {
    try {
      Map<Long, Map<Long, AttributeValue>> srvRetMap = new AttributeValueServiceClient().getValuesByAttribute(attrId);

      srvRetMap.forEach((aId, valMap) -> {
        this.valByAttrMap.put(aId, new HashSet<>(valMap.keySet()));
        this.allAttrValMap.putAll(valMap);
      });

    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }

  }

  /**
   * @param attr attribute
   * @return all values of the given attribute
   */
  public Set<AttributeValue> getAllAttrValues(final Attribute attr) {
    Set<Long> valIdSet = this.valByAttrMap.get(attr.getId());
    if (valIdSet == null) {
      loadAttrValues(attr.getId());
    }
    valIdSet = this.valByAttrMap.get(attr.getId());

    return valIdSet == null ? null
        : valIdSet.stream().map(this.allAttrValMap::get).filter(v -> !v.isDeleted()).collect(Collectors.toSet());
  }

  /**
   * Add all the values of attr to the map when attr is selected
   *
   * @param attr selected attribute
   */
  public void selectAllValsOfAttr(final Attribute attr) {
    Set<Long> values = this.selAttrValMap.get(attr.getId());
    if (values == null) {
      values = new HashSet<>();
      this.selAttrValMap.put(attr.getId(), values);
    }

    // excluding the deleted values , all other attr values are added to the map when attr is checked
    values.addAll(getAllAttrValues(attr).stream().map(AttributeValue::getId).collect(Collectors.toList()));

    // all attr values are checked hence attr is removed from partially checked map
    this.attrPartiallyCheckedSet.remove(attr.getId());

    removeSelAttrUsed(attr);

  }


  /**
   * @param attrVal Attribute Value
   */
  public void selectAttributeValue(final AttributeValue attrVal) {
    selectAttributeValue(attrVal.getAttributeId(), attrVal.getId());
  }

  /**
   * Remove the attr from used map and partially checked list when an attr is unselected
   *
   * @param attr selected attr
   */
  public void uncheckAttribute(final Attribute attr) {
    this.selAttrValMap.remove(attr.getId());
    this.attrPartiallyCheckedSet.remove(attr.getId());

  }

  /**
   * @param attrVal AttributeValue
   */
  public void uncheckAttributeValue(final AttributeValue attrVal) {
    Set<Long> vals = this.selAttrValMap.get(attrVal.getAttributeId());
    if (vals != null) {
      vals.remove(attrVal.getId());
      if (vals.isEmpty()) {
        this.selAttrValMap.remove(attrVal.getAttributeId());
        this.attrPartiallyCheckedSet.remove(attrVal.getAttributeId());
      }
      else {
        // Comparing the current size of the selected values and complete values is not required, since a value was just
        // un-checked, meaning it is partial anyway
        this.attrPartiallyCheckedSet.add(attrVal.getAttributeId());
      }
    }
  }

  /**
   * Provides the search resuts for the given search conditions. Search results contains PIDC versions, along with the
   * other flags
   *
   * @return PIDCScoutResult
   */
  // ICDM-1189
  public SortedSet<PIDCScoutResult> runPidcScoutService() {

    SortedSet<PIDCScoutResult> scoutResultSet = new TreeSet<>();

    try {
      // gets the pidcs based on the seach condition
      Set<PidcSearchResult> resSet = new PidcScoutServiceClient().searchProjects(createSearchInput());

      scoutResultSet.addAll(resSet.stream().map(PIDCScoutResult::new).collect(Collectors.toList()));

      if (scoutResultSet.isEmpty()) {
        CDMLogger.getInstance().warnDialog("No results found for the given search conditions", Activator.PLUGIN_ID);
      }

    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog("Unable to fetch the results. " + exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

    return scoutResultSet;

  }


  /**
   * @param attr attribute
   * @return set of selected values
   */
  public Set<AttributeValue> getSelAttrValues(final Attribute attr) {
    return getAllAttrValues(attr).stream().filter(val -> {
      Set<Long> selValSet = this.selAttrValMap.get(attr.getId());
      return (selValSet != null) && selValSet.contains(val.getId());
    }).collect(Collectors.toSet());
  }

  /**
   * @param attrId attribute ID
   * @param usedFlag Used Flag
   * @deprecated use {@link #setSelAttrUsed(Attribute, String)} instead
   */
  @Deprecated
  public void setSelAttrUsed(final Long attrId, final String usedFlag) {
    setSelAttrUsed(getAttribute(attrId), usedFlag);
  }

  /**
   * @param attr attribute
   * @param usedFlag Used Flag
   */
  public void setSelAttrUsed(final Attribute attr, final String usedFlag) {
    this.selAttrUsedMap.put(attr.getId(), usedFlag);
    uncheckAttribute(attr);
  }

  /**
   * @param attr remove used flag selection
   */
  public void removeSelAttrUsed(final Attribute attr) {
    this.selAttrUsedMap.remove(attr.getId());
  }

  /**
   * @param attribute attribute
   * @return selected used flag
   */
  public String getSelAttrUsed(final Attribute attribute) {
    return this.selAttrUsedMap.get(attribute.getId());
  }

  /**
   * Clear all selections
   */
  public void clearSelection() {
    this.selAttrUsedMap.clear();
    this.selAttrValMap.clear();
    this.attrPartiallyCheckedSet.clear();
  }

  /**
   * @param attr attribute
   * @return true if value/used flag is selected
   */
  public boolean isAttrSelected(final Attribute attr) {
    return this.selAttrValMap.containsKey(attr.getId()) || this.selAttrUsedMap.containsKey(attr.getId());
  }

  /**
   * @param attr Attribute
   * @return true if one or more values are selected
   */
  public boolean isAttrValueSelected(final Attribute attr) {
    return this.selAttrValMap.containsKey(attr.getId());
  }

  /**
   * Mark the given value as selected
   *
   * @param attrId attribute ID
   * @param attrValId value ID
   * @deprecated use {@link #selectAttributeValue(AttributeValue)} instead
   */
  @Deprecated
  public void selectAttributeValue(final Long attrId, final Long attrValId) {
    selectAttributeValue(attrId, new HashSet<>(Arrays.asList(attrValId)));
  }

  /**
   * Mark the given values as selected
   *
   * @param attrId attribute Id
   * @param attrValIdSet values to select
   * @deprecated use {@link #selectAttributeValue(Attribute, Set) } instead
   */
  @Deprecated
  public void selectAttributeValue(final Long attrId, final Set<Long> attrValIdSet) {

    Attribute attr = getAttribute(attrId);
    Set<AttributeValue> attrValSet = getAllAttrValues(attr).stream()
        .filter(val -> attrValIdSet.contains(val.getId()) && !val.isDeleted()).collect(Collectors.toSet());

    selectAttributeValue(attr, attrValSet);
  }

  /**
   * Mark the given values as selected
   *
   * @param attr attribute
   * @param attrValSet values to select
   */
  public void selectAttributeValue(final Attribute attr, final Set<AttributeValue> attrValSet) {
    Set<Long> valSet = this.selAttrValMap.get(attr.getId());
    if (valSet == null) {
      valSet = new HashSet<>();
      this.selAttrValMap.put(attr.getId(), valSet);
    }
    valSet.addAll(attrValSet.stream().map(AttributeValue::getId).collect(Collectors.toList()));

    Set<AttributeValue> nonDeletedValueSet = getAllAttrValues(attr).stream().collect(Collectors.toSet());

    if (nonDeletedValueSet.size() == valSet.size()) {
      this.attrPartiallyCheckedSet.remove(attr.getId());
    }
    else {
      this.attrPartiallyCheckedSet.add(attr.getId());
    }

    removeSelAttrUsed(attr);

  }

  /**
   * @param attr Attribute
   * @return true if values are selected partially
   */
  public boolean isAttrSelectedPartially(final Attribute attr) {
    return this.attrPartiallyCheckedSet.contains(attr.getId());
  }

  private void mergeAttribute(final ChangeData<?> chData) {
    Attribute newValue = (Attribute) chData.getNewData();
    Attribute existingValue = getAttribute(chData.getObjId());
    if (existingValue == null) {
      this.allAttrMap.put(chData.getObjId(), newValue);
    }
    else {
      CommonUtils.shallowCopy(existingValue, newValue);
    }

  }

  private void mergeAttrValue(final ChangeData<?> chData) {
    AttributeValue newValue = (AttributeValue) chData.getNewData();
    Long attrId = newValue.getAttributeId();
    if ((getAttribute(attrId) == null) || !this.valByAttrMap.containsKey(attrId)) {
      // Attribute or values not loaded yet
      return;
    }

    AttributeValue existingValue = this.allAttrValMap.get(chData.getObjId());
    if (existingValue == null) {
      this.allAttrValMap.put(chData.getObjId(), newValue);
      this.valByAttrMap.get(attrId).add(chData.getObjId());
    }
    else {
      CommonUtils.shallowCopy(existingValue, newValue);
    }

  }

}
