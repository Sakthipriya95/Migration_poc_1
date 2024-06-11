/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.attr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;

/**
 * @author dmo5cob
 */
public class ProjectAttributesUpdationModel {

  private PidcVersion pidcVersion;

  private PidcVersion newPidcVersion;

  private Map<Long, PidcVersionAttribute> pidcAttrsToBeCreated = new HashMap<>();

  private Map<Long, PidcVersionAttribute> pidcAttrsToBeUpdated = new HashMap<>();

  private final Map<Long, PidcVersionAttribute> pidcAttrsToBeCreatedwithNewVal = new HashMap<>();

  private Map<Long, PidcVersionAttribute> pidcAttrsToBeUpdatedwithNewVal = new HashMap<>();

  private Map<Long, PidcVersionAttribute> pidcAttrsAfterUpdation = new HashMap<>();

  private Map<Long, Map<Long, PidcVariantAttribute>> pidcVarAttrsToBeCreated = new HashMap<>();

  private Map<Long, Map<Long, PidcVariantAttribute>> pidcVarAttrsToBeCreatedWithNewVal = new HashMap<>();

  private Map<Long, Map<Long, PidcVariantAttribute>> pidcVarAttrsToBeUpdated = new HashMap<>();

  private Map<Long, Map<Long, PidcVariantAttribute>> pidcVarAttrsToBeUpdatedWithNewVal = new HashMap<>();

  private Map<Long, Map<Long, PidcSubVariantAttribute>> pidcSubVarAttrsToBeCreated = new HashMap<>();

  private Map<Long, Map<Long, PidcSubVariantAttribute>> pidcSubVarAttrsToBeCreatedWithNewVal = new HashMap<>();

  private Map<Long, Map<Long, PidcSubVariantAttribute>> pidcSubVarAttrsToBeUpdated = new HashMap<>();

  private final Map<Long, Map<Long, PidcSubVariantAttribute>> pidcSubVarAttrsToBeUpdatedWithNewVal = new HashMap<>();

  private Set<PidcVersionAttribute> pidcVersVisibleAttrSet = new HashSet<>();

  private Map<Long, PidcVariant> pidcVarsToBeUpdated = new HashMap<>();
  private Map<Long, PidcVariant> pidcVarsToBeUpdatedWithNewVal = new HashMap<>();

  private Map<Long, PidcSubVariant> pidcSubVarsToBeUpdated = new HashMap<>();

  private Map<Long, PidcSubVariant> pidcSubVarsToBeUpdatedWithNewVal = new HashMap<>();


  /**
   * Set of Invisible attributes at PIDC Version Level.<br>
   * Note : Attribute IDs are stored here
   */
  private Set<PidcVersionAttribute> pidcVersInvisibleAttrSet = new HashSet<>();

  /**
   * Set of Invisible attributes for each variant at variant level.<br>
   * Key - Variant ID<br>
   * Value - Set of 'attribute' ID
   */
  private Map<Long, Set<PidcVariantAttribute>> variantInvisbleAttributeMap = new HashMap<>();
  /**
   * Set of visible attributes for each variant at variant level.<br>
   * Key - Variant ID<br>
   * Value - Set of 'attribute' ID
   */
  private Map<Long, Set<PidcVariantAttribute>> variantVisbleAttributeMap = new HashMap<>();


  /**
   * Set of Invisible attributes for each sub variant at subvariant level.<br>
   * Key - Sub Variant ID<br>
   * Value - Set of 'attribute' ID
   */
  private Map<Long, Set<PidcSubVariantAttribute>> subVariantInvisbleAttributeMap = new HashMap<>();
  /**
   * Set of visible attributes for each sub variant at subvariant level.<br>
   * Key - Sub Variant ID<br>
   * Value - Set of 'attribute' ID
   */
  private Map<Long, Set<PidcSubVariantAttribute>> subVariantVisbleAttributeMap = new HashMap<>();

  private Map<Long, PidcVersionAttribute> pidcVersionAttributeMap = new HashMap<>();

  private Map<Long, Map<Long, PidcVariantAttribute>> pidcVariantAttributeMap = new HashMap<>();

  private Map<Long, Map<Long, PidcSubVariantAttribute>> pidcSubVariantAttributeMap = new HashMap<>();

  private Map<Long, Map<Long, PidcVariantAttribute>> pidcVariantAttributeDeletedMap = new HashMap<>();

  private Map<Long, Map<Long, PidcSubVariantAttribute>> pidcSubVariantAttributeDeletedMap = new HashMap<>();


  /**
   * @return the pidcVarsToBeUpdated
   */
  public Map<Long, PidcVariant> getPidcVarsToBeUpdated() {
    return this.pidcVarsToBeUpdated;
  }


  /**
   * @param pidcVarsToBeUpdated the pidcVarsToBeUpdated to set
   */
  public void setPidcVarsToBeUpdated(final Map<Long, PidcVariant> pidcVarsToBeUpdated) {
    this.pidcVarsToBeUpdated = pidcVarsToBeUpdated;
  }


  /**
   * @return the pidcVarsToBeUpdatedWithNewVal
   */
  public Map<Long, PidcVariant> getPidcVarsToBeUpdatedWithNewVal() {
    return this.pidcVarsToBeUpdatedWithNewVal;
  }


  /**
   * @param pidcVarsToBeUpdatedWithNewVal the pidcVarsToBeUpdatedWithNewVal to set
   */
  public void setPidcVarsToBeUpdatedWithNewVal(final Map<Long, PidcVariant> pidcVarsToBeUpdatedWithNewVal) {
    this.pidcVarsToBeUpdatedWithNewVal = pidcVarsToBeUpdatedWithNewVal;
  }


  /**
   * @return the pidcSubVarsToBeUpdated
   */
  public Map<Long, PidcSubVariant> getPidcSubVarsToBeUpdated() {
    return this.pidcSubVarsToBeUpdated;
  }


  /**
   * @param pidcSubVarsToBeUpdated the pidcSubVarsToBeUpdated to set
   */
  public void setPidcSubVarsToBeUpdated(final Map<Long, PidcSubVariant> pidcSubVarsToBeUpdated) {
    this.pidcSubVarsToBeUpdated = pidcSubVarsToBeUpdated;
  }


  /**
   * @return the pidcSubVarsToBeUpdatedWithNewVal
   */
  public Map<Long, PidcSubVariant> getPidcSubVarsToBeUpdatedWithNewVal() {
    return this.pidcSubVarsToBeUpdatedWithNewVal;
  }


  /**
   * @param pidcSubVarsToBeUpdatedWithNewVal the pidcSubVarsToBeUpdatedWithNewVal to set
   */
  public void setPidcSubVarsToBeUpdatedWithNewVal(final Map<Long, PidcSubVariant> pidcSubVarsToBeUpdatedWithNewVal) {
    this.pidcSubVarsToBeUpdatedWithNewVal = pidcSubVarsToBeUpdatedWithNewVal;
  }


  /**
   * @return the pidcAttrsToBeCreated
   */
  public Map<Long, PidcVersionAttribute> getPidcAttrsToBeCreated() {
    return this.pidcAttrsToBeCreated;
  }


  /**
   * @return the pidcAttrsToBeUpdated
   */
  public Map<Long, PidcVersionAttribute> getPidcAttrsToBeUpdated() {
    return this.pidcAttrsToBeUpdated;
  }


  /**
   * @return the pidcVarAttrsToBeCreated
   */
  public Map<Long, Map<Long, PidcVariantAttribute>> getPidcVarAttrsToBeCreated() {
    return this.pidcVarAttrsToBeCreated;
  }


  /**
   * @return the pidcVarAttrsToBeUpdated
   */
  public Map<Long, Map<Long, PidcVariantAttribute>> getPidcVarAttrsToBeUpdated() {
    return this.pidcVarAttrsToBeUpdated;
  }


  /**
   * @return the pidcSubVarAttrsToBeCreated
   */
  public Map<Long, Map<Long, PidcSubVariantAttribute>> getPidcSubVarAttrsToBeCreated() {
    return this.pidcSubVarAttrsToBeCreated;
  }


  /**
   * @return the pidcSubVarAttrsToBeUpdated
   */
  public Map<Long, Map<Long, PidcSubVariantAttribute>> getPidcSubVarAttrsToBeUpdated() {
    return this.pidcSubVarAttrsToBeUpdated;
  }


  /**
   * @return the pidcVersion
   */
  public PidcVersion getPidcVersion() {
    return this.pidcVersion;
  }


  /**
   * @param pidcVersion the pidcVersion to set
   */
  public void setPidcVersion(final PidcVersion pidcVersion) {
    this.pidcVersion = pidcVersion;
  }


  /**
   * @param pidcAttrsToBeCreated the pidcAttrsToBeCreated to set
   */
  public void setPidcAttrsToBeCreated(final Map<Long, PidcVersionAttribute> pidcAttrsToBeCreated) {
    this.pidcAttrsToBeCreated = pidcAttrsToBeCreated;
  }


  /**
   * @param pidcAttrsToBeUpdated the pidcAttrsToBeUpdated to set
   */
  public void setPidcAttrsToBeUpdated(final Map<Long, PidcVersionAttribute> pidcAttrsToBeUpdated) {
    this.pidcAttrsToBeUpdated = pidcAttrsToBeUpdated;
  }


  /**
   * @param pidcVarAttrsToBeCreated the pidcVarAttrsToBeCreated to set
   */
  public void setPidcVarAttrsToBeCreated(final Map<Long, Map<Long, PidcVariantAttribute>> pidcVarAttrsToBeCreated) {
    this.pidcVarAttrsToBeCreated = pidcVarAttrsToBeCreated;
  }


  /**
   * @param pidcVarAttrsToBeUpdated the pidcVarAttrsToBeUpdated to set
   */
  public void setPidcVarAttrsToBeUpdated(final Map<Long, Map<Long, PidcVariantAttribute>> pidcVarAttrsToBeUpdated) {
    this.pidcVarAttrsToBeUpdated = pidcVarAttrsToBeUpdated;
  }


  /**
   * @param pidcSubVarAttrsToBeCreated the pidcSubVarAttrsToBeCreated to set
   */
  public void setPidcSubVarAttrsToBeCreated(
      final Map<Long, Map<Long, PidcSubVariantAttribute>> pidcSubVarAttrsToBeCreated) {
    this.pidcSubVarAttrsToBeCreated = pidcSubVarAttrsToBeCreated;
  }


  /**
   * @param pidcSubVarAttrsToBeUpdated the pidcSubVarAttrsToBeUpdated to set
   */
  public void setPidcSubVarAttrsToBeUpdated(
      final Map<Long, Map<Long, PidcSubVariantAttribute>> pidcSubVarAttrsToBeUpdated) {
    this.pidcSubVarAttrsToBeUpdated = pidcSubVarAttrsToBeUpdated;
  }


  /**
   * @return the pidcVersInvisibleAttrSet
   */
  public Set<PidcVersionAttribute> getPidcVersInvisibleAttrSet() {
    return this.pidcVersInvisibleAttrSet;
  }


  /**
   * @param pidcVersInvisibleAttrSet the pidcVersInvisibleAttrSet to set
   */
  public void setPidcVersInvisibleAttrSet(final Set<PidcVersionAttribute> pidcVersInvisibleAttrSet) {
    if (pidcVersInvisibleAttrSet != null) {
      this.pidcVersInvisibleAttrSet = new HashSet<>(pidcVersInvisibleAttrSet);
    }
  }


  /**
   * @return the variantInvisbleAttributeMap
   */
  public Map<Long, Set<PidcVariantAttribute>> getVariantInvisbleAttributeMap() {
    return this.variantInvisbleAttributeMap;
  }


  /**
   * @param variantInvisbleAttributeMap the variantInvisbleAttributeMap to set
   */
  public void setVariantInvisbleAttributeMap(final Map<Long, Set<PidcVariantAttribute>> variantInvisbleAttributeMap) {
    this.variantInvisbleAttributeMap = variantInvisbleAttributeMap;
  }


  /**
   * @return the subVariantInvisbleAttributeMap
   */
  public Map<Long, Set<PidcSubVariantAttribute>> getSubVariantInvisbleAttributeMap() {
    return this.subVariantInvisbleAttributeMap;
  }


  /**
   * @param subVariantInvisbleAttributeMap the subVariantInvisbleAttributeMap to set
   */
  public void setSubVariantInvisbleAttributeMap(
      final Map<Long, Set<PidcSubVariantAttribute>> subVariantInvisbleAttributeMap) {
    this.subVariantInvisbleAttributeMap = subVariantInvisbleAttributeMap;
  }


  /**
   * @return the pidcAttrsAfterUpdation
   */
  public Map<Long, PidcVersionAttribute> getPidcAttrsAfterUpdation() {
    return this.pidcAttrsAfterUpdation;
  }


  /**
   * @return the variantVisbleAttributeMap
   */
  public Map<Long, Set<PidcVariantAttribute>> getVariantVisbleAttributeMap() {
    return this.variantVisbleAttributeMap;
  }


  /**
   * @param variantVisbleAttributeMap the variantVisbleAttributeMap to set
   */
  public void setVariantVisbleAttributeMap(final Map<Long, Set<PidcVariantAttribute>> variantVisbleAttributeMap) {
    this.variantVisbleAttributeMap = variantVisbleAttributeMap;
  }


  /**
   * @return the subVariantVisbleAttributeMap
   */
  public Map<Long, Set<PidcSubVariantAttribute>> getSubVariantVisbleAttributeMap() {
    return this.subVariantVisbleAttributeMap;
  }


  /**
   * @param subVariantVisbleAttributeMap the subVariantVisbleAttributeMap to set
   */
  public void setSubVariantVisbleAttributeMap(
      final Map<Long, Set<PidcSubVariantAttribute>> subVariantVisbleAttributeMap) {
    this.subVariantVisbleAttributeMap = subVariantVisbleAttributeMap;
  }


  /**
   * @return the pidcVersVisibleAttrSet
   */
  public Set<PidcVersionAttribute> getPidcVersVisibleAttrSet() {
    return this.pidcVersVisibleAttrSet;
  }


  /**
   * @param pidcAttrsAfterUpdation the pidcAttrsAfterUpdation to set
   */
  public void setPidcAttrsAfterUpdation(final Map<Long, PidcVersionAttribute> pidcAttrsAfterUpdation) {
    this.pidcAttrsAfterUpdation = pidcAttrsAfterUpdation;
  }


  /**
   * @param pidcVersVisibleAttrSet the pidcVersVisibleAttrSet to set
   */
  public void setPidcVersVisibleAttrSet(final Set<PidcVersionAttribute> pidcVersVisibleAttrSet) {
    this.pidcVersVisibleAttrSet = pidcVersVisibleAttrSet == null ? null : new HashSet<>(pidcVersVisibleAttrSet);
  }

  /**
   * @return the pidcVersionAttributeMap
   */
  public Map<Long, PidcVersionAttribute> getPidcVersionAttributeMap() {
    return this.pidcVersionAttributeMap == null ? null : new HashMap<>(this.pidcVersionAttributeMap);
  }


  /**
   * @param pidcVersionAttributeMap the pidcVersionAttributeMap to set
   */
  public void setPidcVersionAttributeMap(final Map<Long, PidcVersionAttribute> pidcVersionAttributeMap) {
    this.pidcVersionAttributeMap = pidcVersionAttributeMap;
  }


  /**
   * @return the pidcVariantAttributeMap
   */
  public Map<Long, Map<Long, PidcVariantAttribute>> getPidcVariantAttributeMap() {
    return this.pidcVariantAttributeMap;
  }


  /**
   * @param pidcVariantAttributeMap the pidcVariantAttributeMap to set
   */
  public void setPidcVariantAttributeMap(final Map<Long, Map<Long, PidcVariantAttribute>> pidcVariantAttributeMap) {
    this.pidcVariantAttributeMap = pidcVariantAttributeMap;
  }


  /**
   * @return the pidcSubVariantAttributeMap
   */
  public Map<Long, Map<Long, PidcSubVariantAttribute>> getPidcSubVariantAttributeMap() {
    return this.pidcSubVariantAttributeMap;
  }


  /**
   * @param pidcSubVariantAttributeMap the pidcSubVariantAttributeMap to set
   */
  public void setPidcSubVariantAttributeMap(
      final Map<Long, Map<Long, PidcSubVariantAttribute>> pidcSubVariantAttributeMap) {
    this.pidcSubVariantAttributeMap = pidcSubVariantAttributeMap;
  }


  /**
   * @return the pidcVariantAttributeDeletedMap
   */
  public Map<Long, Map<Long, PidcVariantAttribute>> getPidcVariantAttributeDeletedMap() {
    return this.pidcVariantAttributeDeletedMap;
  }


  /**
   * @param pidcVariantAttributeDeletedMap the pidcVariantAttributeDeletedMap to set
   */
  public void setPidcVariantAttributeDeletedMap(
      final Map<Long, Map<Long, PidcVariantAttribute>> pidcVariantAttributeDeletedMap) {
    this.pidcVariantAttributeDeletedMap = pidcVariantAttributeDeletedMap;
  }


  /**
   * @return the pidcSubVariantAttributeDeletedMap
   */
  public Map<Long, Map<Long, PidcSubVariantAttribute>> getPidcSubVariantAttributeDeletedMap() {
    return this.pidcSubVariantAttributeDeletedMap;
  }


  /**
   * @param pidcSubVariantAttributeDeletedMap the pidcSubVariantAttributeDeletedMap to set
   */
  public void setPidcSubVariantAttributeDeletedMap(
      final Map<Long, Map<Long, PidcSubVariantAttribute>> pidcSubVariantAttributeDeletedMap) {
    this.pidcSubVariantAttributeDeletedMap = pidcSubVariantAttributeDeletedMap;
  }


  /**
   * @return the pidcAttrsToBeUpdatedwithNewVal
   */
  public Map<Long, PidcVersionAttribute> getPidcAttrsToBeUpdatedwithNewVal() {
    return this.pidcAttrsToBeUpdatedwithNewVal;
  }


  /**
   * @param pidcAttrsToBeUpdatedwithNewVal the pidcAttrsToBeUpdatedwithNewVal to set
   */
  public void setPidcAttrsToBeUpdatedwithNewVal(final Map<Long, PidcVersionAttribute> pidcAttrsToBeUpdatedwithNewVal) {
    this.pidcAttrsToBeUpdatedwithNewVal = pidcAttrsToBeUpdatedwithNewVal;
  }


  /**
   * @return the pidcVarAttrsToBeUpdatedWithNewVal
   */
  public Map<Long, Map<Long, PidcVariantAttribute>> getPidcVarAttrsToBeUpdatedWithNewVal() {
    return this.pidcVarAttrsToBeUpdatedWithNewVal;
  }


  /**
   * @param pidcVarAttrsToBeUpdatedWithNewVal the pidcVarAttrsToBeUpdatedWithNewVal to set
   */
  public void setPidcVarAttrsToBeUpdatedWithNewVal(
      final Map<Long, Map<Long, PidcVariantAttribute>> pidcVarAttrsToBeUpdatedWithNewVal) {
    this.pidcVarAttrsToBeUpdatedWithNewVal = pidcVarAttrsToBeUpdatedWithNewVal;
  }


  /**
   * @return the pidcSubVarAttrsToBeUpdatedWithNewVal
   */
  public Map<Long, Map<Long, PidcSubVariantAttribute>> getPidcSubVarAttrsToBeUpdatedWithNewVal() {
    return this.pidcSubVarAttrsToBeUpdatedWithNewVal;
  }


  /**
   * @return the pidcAttrsToBeCreatedwithNewVal
   */
  public Map<Long, PidcVersionAttribute> getPidcAttrsToBeCreatedwithNewVal() {
    return this.pidcAttrsToBeCreatedwithNewVal;
  }


  /**
   * @return the pidcVarAttrsToBeCreatedWithNewVal
   */
  public Map<Long, Map<Long, PidcVariantAttribute>> getPidcVarAttrsToBeCreatedWithNewVal() {
    return this.pidcVarAttrsToBeCreatedWithNewVal;
  }


  /**
   * @param pidcVarAttrsToBeCreatedWithNewVal the pidcVarAttrsToBeCreatedWithNewVal to set
   */
  public void setPidcVarAttrsToBeCreatedWithNewVal(
      final Map<Long, Map<Long, PidcVariantAttribute>> pidcVarAttrsToBeCreatedWithNewVal) {
    this.pidcVarAttrsToBeCreatedWithNewVal = pidcVarAttrsToBeCreatedWithNewVal;
  }


  /**
   * @return the pidcSubVarAttrsToBeCreatedWithNewVal
   */
  public Map<Long, Map<Long, PidcSubVariantAttribute>> getPidcSubVarAttrsToBeCreatedWithNewVal() {
    return this.pidcSubVarAttrsToBeCreatedWithNewVal;
  }


  /**
   * @param pidcSubVarAttrsToBeCreatedWithNewVal the pidcSubVarAttrsToBeCreatedWithNewVal to set
   */
  public void setPidcSubVarAttrsToBeCreatedWithNewVal(
      final Map<Long, Map<Long, PidcSubVariantAttribute>> pidcSubVarAttrsToBeCreatedWithNewVal) {
    this.pidcSubVarAttrsToBeCreatedWithNewVal = pidcSubVarAttrsToBeCreatedWithNewVal;
  }


  /**
   * @return the newPidcVersion
   */
  public PidcVersion getNewPidcVersion() {
    return this.newPidcVersion;
  }


  /**
   * @param newPidcVersion the newPidcVersion to set
   */
  public void setNewPidcVersion(final PidcVersion newPidcVersion) {
    this.newPidcVersion = newPidcVersion;
  }

}
