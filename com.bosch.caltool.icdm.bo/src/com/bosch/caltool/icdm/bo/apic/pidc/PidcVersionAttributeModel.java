/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;

/**
 * @author bne4cob
 */
public class PidcVersionAttributeModel {

  /**
   * PIDC Version
   */
  private final PidcVersion pidcVersion;

  /**
   * PID Card
   */
  private final Pidc pidc;
  /**
   * PIDC Version level project attributes <br>
   * Key - Attribute ID; Value - project attribute
   */
  private Map<Long, PidcVersionAttribute> pidcVersAttrMap = new HashMap<>();

  /**
   * Attributes in PIDC version that are marked as hidden
   */
  private final Set<Long> hiddenAttrSet = new HashSet<>();

  /**
   * Set of Invisible attributes at PIDC Version Level.<br>
   * Note : Attribute IDs are stored here
   */
  private Set<Long> pidcVersInvisibleAttrSet = new HashSet<>();

  /**
   * Map of variants of the PIDC version<br>
   * Key - Variant ID; Value - Variant
   */
  private Map<Long, PidcVariant> variantMap = new HashMap<>();

  /**
   * Map of variant attributes. <br>
   * Key - variant ID <br>
   * Value - map of attributes. Key - atrribute ID, Value - variant attribute
   */
  private final Map<Long, Map<Long, PidcVariantAttribute>> allVariantAttributeMap = new HashMap<>();


  /**
   * Set of Invisible attributes for each variant at variant level.<br>
   * Key - Variant ID<br>
   * Value - Set of 'attribute' ID
   */
  private final Map<Long, Set<Long>> variantInvisbleAttributeMap = new HashMap<>();

  /**
   * map of sub variants<br>
   * Key - sub variant ID; Value - Sub Variant
   */
  private final Map<Long, PidcSubVariant> allSubVariantMap = new HashMap<>();
  /**
   * Sub variants of each variant<br>
   * Key - Variant ID<br>
   * Value - Set of sub variant IDs
   */
  private final Map<Long, Set<Long>> subVariantsByVariantMap = new HashMap<>();
  /**
   * Sub variant level attributes<br>
   * Key - Sub Variant ID<br>
   * Value - map of attributes. Key - atrribute ID, Value - Sub variant attribute
   */
  private final Map<Long, Map<Long, PidcSubVariantAttribute>> allSubVariantAttrMap = new HashMap<>();

  /**
   * Set of Invisible attributes for each sub variant at subvariant level.<br>
   * Key - Sub Variant ID<br>
   * Value - Set of 'attribute' ID
   */
  private final Map<Long, Set<Long>> subVariantInvisbleAttributeMap = new HashMap<>();

  /**
   * Map of all attributes <br>
   * Key - attribute ID; Value - attribute
   */
  private Map<Long, Attribute> allAttrMap = new HashMap<>();

  /**
   * Set of relevant attributes for this model
   */
  private final Set<Long> relevantAttributeIdSet = new HashSet<>();

  /**
   * Map of relevant attribute values<br>
   * Key - attribute value ID; value - attribute value
   */
  private final Map<Long, AttributeValue> relevantAttrValueMap = new HashMap<>();

  /**
   * If true, deleted Project Variant/SubVariant are also included
   */
  private final boolean deletedIncluded;

  /**
   * @param pidcVersion PIDC Version
   * @param pidc PIDC
   * @param deletedIncluded If true, deleted Project Variant/SubVariant are also included
   */
  public PidcVersionAttributeModel(final PidcVersion pidcVersion, final Pidc pidc, final boolean deletedIncluded) {
    this.pidcVersion = pidcVersion;
    this.pidc = pidc;
    this.deletedIncluded = deletedIncluded;
  }

  /**
   * @return the pidcVersion
   */
  public PidcVersion getPidcVersion() {
    return this.pidcVersion;
  }

  /**
   * @return the pidc
   */
  public Pidc getPidc() {
    return this.pidc;
  }

  /**
   * @param pidcVersAttrMap the pidcVersAttrMap to set
   */
  public void setPidcVersAttrMap(final Map<Long, PidcVersionAttribute> pidcVersAttrMap) {
    if (pidcVersAttrMap != null) {
      this.pidcVersAttrMap = new HashMap<>(pidcVersAttrMap);
      this.relevantAttributeIdSet.addAll(pidcVersAttrMap.keySet());
    }
  }

  /**
   * @return the pidcVersAttrMap
   */
  public Map<Long, PidcVersionAttribute> getPidcVersAttrMap() {
    return new HashMap<>(this.pidcVersAttrMap);
  }

  /**
   * @param attrId attribute ID
   * @return the pidcVersAttrMap
   */
  public PidcVersionAttribute getPidcVersAttr(final Long attrId) {
    return this.pidcVersAttrMap.get(attrId);
  }

  /**
   * @param attrId attribute ID
   * @return true, if the given attribute is marked as hidden
   */
  public boolean isHiddenAttr(final Long attrId) {
    return this.hiddenAttrSet.contains(attrId);
  }

  /**
   * @param attrId attribute ID
   */
  public void addHiddenAttrId(final Long attrId) {
    this.hiddenAttrSet.add(attrId);
  }

  /**
   * @return the pidcVersInvisibleAttrSet
   */
  public Set<Long> getPidcVersInvisibleAttrSet() {
    return new HashSet<>(this.pidcVersInvisibleAttrSet);
  }

  /**
   * @param pidcVersInvisibleAttrSet the pidcVersInvisibleAttrSet to set
   */
  public void setPidcVersInvisibleAttrSet(final Set<Long> pidcVersInvisibleAttrSet) {
    if (pidcVersInvisibleAttrSet != null) {
      this.pidcVersInvisibleAttrSet = new HashSet<>(pidcVersInvisibleAttrSet);
    }
  }

  /**
   * @return the variantMap
   */
  public Map<Long, PidcVariant> getVariantMap() {
    return new HashMap<>(this.variantMap);
  }

  /**
   * @param variantMap the variantMap to set
   */
  public void setVariantMap(final Map<Long, PidcVariant> variantMap) {
    if (variantMap != null) {
      this.variantMap = new HashMap<>(variantMap);
    }
  }

  /**
   * @param variantId variant Id
   * @return the variantAttributeMap
   */
  public Map<Long, PidcVariantAttribute> getVariantAttributeMap(final Long variantId) {
    Map<Long, PidcVariantAttribute> retMap = this.allVariantAttributeMap.get(variantId);
    return retMap == null ? new HashMap<>() : new HashMap<>(retMap);
  }

  /**
   * @param variantId variant Id
   * @param variantAttributeMap the variantAttributeMap to set
   */
  public void setVariantAttributeMap(final Long variantId, final Map<Long, PidcVariantAttribute> variantAttributeMap) {
    if (variantAttributeMap != null) {
      this.allVariantAttributeMap.put(variantId, new HashMap<>(variantAttributeMap));
      this.relevantAttributeIdSet.addAll(variantAttributeMap.keySet());
    }
  }

  /**
   * @param variantId variant Id
   * @return the set of invisible 'attribute' IDs
   */
  public Set<Long> getVariantInvisbleAttributeSet(final Long variantId) {
    Set<Long> retSet = this.variantInvisbleAttributeMap.get(variantId);
    return retSet == null ? new HashSet<>() : new HashSet<>(retSet);
  }

  /**
   * @param variantId variant Id
   * @param variantInvisbleAttributeSet set of invisible 'attribute' IDs
   */
  public void setVariantInvisbleAttributeSet(final Long variantId, final Set<Long> variantInvisbleAttributeSet) {
    if (variantInvisbleAttributeSet != null) {
      this.variantInvisbleAttributeMap.put(variantId, new HashSet<>(variantInvisbleAttributeSet));
    }
  }

  /**
   * @return the subVariantMap
   */
  public Map<Long, PidcSubVariant> getSubVariantMap() {
    return new HashMap<>(this.allSubVariantMap);
  }

  /**
   * @param variantId variant Id
   * @return the subVariantMap
   */
  public Map<Long, PidcSubVariant> getSubVariantMap(final Long variantId) {
    Map<Long, PidcSubVariant> retMap = new HashMap<>();
    getSubVariantIdSet(variantId).forEach(svarId -> retMap.put(svarId, this.allSubVariantMap.get(svarId)));

    return retMap;
  }

  /**
   * @param variantId variant Id
   * @param subVariantMap the subVariantMap to set
   */
  public void setSubVariantMap(final Long variantId, final Map<Long, PidcSubVariant> subVariantMap) {
    if (subVariantMap != null) {
      this.subVariantsByVariantMap.put(variantId, new HashSet<>(subVariantMap.keySet()));
      this.allSubVariantMap.putAll(subVariantMap);
    }
  }

  /**
   * @param variantId variant Id
   * @return the subVariantsByVariantMap
   */
  public Set<Long> getSubVariantIdSet(final Long variantId) {
    Set<Long> subVarSet = this.subVariantsByVariantMap.get(variantId);
    return subVarSet == null ? new HashSet<>() : new HashSet<>(subVarSet);
  }

  /**
   * @param subVarId Sub Variant ID
   * @return the subVariantAttributeMap
   */
  public Map<Long, PidcSubVariantAttribute> getSubVariantAttributeMap(final Long subVarId) {
    Map<Long, PidcSubVariantAttribute> retMap = this.allSubVariantAttrMap.get(subVarId);
    return retMap == null ? new HashMap<>() : new HashMap<>(retMap);
  }

  /**
   * @param subVarId Sub Variant ID
   * @param subVarAttrMap the subVariantAttributeMap to set
   */
  public void setSubVariantAttributeMap(final Long subVarId, final Map<Long, PidcSubVariantAttribute> subVarAttrMap) {
    if (subVarAttrMap != null) {
      this.allSubVariantAttrMap.put(subVarId, new HashMap<>(subVarAttrMap));
      this.relevantAttributeIdSet.addAll(subVarAttrMap.keySet());
    }

  }

  /**
   * @param subVarId Sub Variant ID
   * @return the subVariantInvisbleAttributeSet
   */
  public Set<Long> getSubVariantInvisbleAttributeSet(final Long subVarId) {
    Set<Long> retSet = this.subVariantInvisbleAttributeMap.get(subVarId);
    return retSet == null ? new HashSet<>() : new HashSet<>(retSet);
  }

  /**
   * @param subVarId Sub Variant ID
   * @param subVariantInvisbleAttributeSet the subVariantInvisbleAttributeSet to set
   */
  public void setSubVariantInvisbleAttributeSet(final Long subVarId, final Set<Long> subVariantInvisbleAttributeSet) {
    if (subVariantInvisbleAttributeSet != null) {
      this.subVariantInvisbleAttributeMap.put(subVarId, new HashSet<>(subVariantInvisbleAttributeSet));
    }
  }

  /**
   * @return the allAttrMap
   */
  public Map<Long, Attribute> getAllAttrMap() {
    return new HashMap<>(this.allAttrMap);
  }

  /**
   * @param allAttrMap the allAttrMap to set
   */
  public void setAllAttrMap(final Map<Long, Attribute> allAttrMap) {
    this.allAttrMap = new HashMap<>(allAttrMap);
  }

  /**
   * @return the attributeMap
   */
  public Map<Long, Attribute> getRelevantAttributeMap() {
    Map<Long, Attribute> retMap = new HashMap<>();
    this.relevantAttributeIdSet.forEach(attrId -> retMap.put(attrId, this.allAttrMap.get(attrId)));

    return retMap;
  }

  /**
   * @return the relevantAttrValueMap
   */
  public Map<Long, AttributeValue> getRelevantAttrValueMap() {
    return new HashMap<>(this.relevantAttrValueMap);
  }


  /**
   * @param valueId value Id
   * @return
   */
  AttributeValue getRelevantAttrValue(final Long valueId) {
    return this.relevantAttrValueMap.get(valueId);
  }

  /**
   * @param value the AttributeValue to add
   */
  public void addRelevantAttrValue(final AttributeValue value) {
    this.relevantAttrValueMap.put(value.getId(), value);
  }

  /**
   * @return the deletedIncluded
   */
  public boolean isDeletedIncluded() {
    return this.deletedIncluded;
  }

  /**
   * @param svarId SubVariant ID
   * @param attrId attribute ID
   * @return sub variant attribute, or null if attribute not found
   */
  public PidcSubVariantAttribute getSubVariantAttribute(final Long svarId, final Long attrId) {
    Map<Long, PidcSubVariantAttribute> map = this.allSubVariantAttrMap.get(svarId);
    return map == null ? null : map.get(attrId);
  }

  /**
   * @param attrId attribute ID
   * @return Attribute
   */
  public Attribute getAttribute(final Long attrId) {
    return this.allAttrMap.get(attrId);
  }

  /**
   * @param varId Variant ID
   * @param attrId attribute ID
   * @return variant attribute, or null if attribute not found
   */
  public PidcVariantAttribute getVariantAttribute(final Long varId, final Long attrId) {
    Map<Long, PidcVariantAttribute> map = this.allVariantAttributeMap.get(varId);
    return map == null ? null : map.get(attrId);
  }

  /**
   * @return the allVariantAttributeMap
   */
  public Map<Long, Map<Long, PidcVariantAttribute>> getAllVariantAttributeMap() {
    return this.allVariantAttributeMap;
  }


  /**
   * @return the allSubVariantAttrMap
   */
  public Map<Long, Map<Long, PidcSubVariantAttribute>> getAllSubVariantAttrMap() {
    return this.allSubVariantAttrMap;
  }


}
