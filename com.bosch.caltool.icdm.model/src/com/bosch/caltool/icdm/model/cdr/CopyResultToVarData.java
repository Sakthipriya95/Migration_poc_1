/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;

/**
 * @author bru2cob
 */
public class CopyResultToVarData {

  private Map<Long, RvwVariant> rvwVariantsMap = new ConcurrentHashMap<>();


  private SortedSet<PidcVariant> pidcVariants;

  private Map<Long, PidcVariant> a2lMappedVariantsMap = new ConcurrentHashMap<>();


  private SortedSet<PidcVariant> reviewPidcVars = new TreeSet<PidcVariant>();

  private PidcVersion pidcVersion;
  /**
   * Map of variant attributes. <br>
   * Key - variant ID <br>
   * Value - map of attributes. Key - atrribute ID, Value - variant attribute
   */
  private Map<Long, Map<Long, PidcVariantAttribute>> allVariantAttributeMap = new HashMap<>();
  /**
   * Key -attr id Value - attr value id
   */
  Map<Long, Long> attrValMap = new ConcurrentHashMap<>();
  Map<Long, String> attrUsedMap = new ConcurrentHashMap<>();

  /**
   * key- pidc variant id , value - true if the variant belongs to same variant group
   */
  private final Map<Long, Boolean> sameVarGrpFlag = new ConcurrentHashMap<>();

  /**
   * @param allVariantAttributeMap the allVariantAttributeMap to set
   */
  public void setAllVariantAttributeMap(final Map<Long, Map<Long, PidcVariantAttribute>> allVariantAttributeMap) {
    this.allVariantAttributeMap = allVariantAttributeMap;
  }


  /**
   * @return the allVariantAttributeMap
   */
  public Map<Long, Map<Long, PidcVariantAttribute>> getAllVariantAttributeMap() {
    return this.allVariantAttributeMap;
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
   * @return the reviewPidcVars
   */
  public SortedSet<PidcVariant> getReviewPidcVars() {
    return this.reviewPidcVars;
  }


  /**
   * @param reviewPidcVars the reviewPidcVars to set
   */
  public void setReviewPidcVars(final SortedSet<PidcVariant> reviewPidcVars) {
    this.reviewPidcVars = reviewPidcVars;
  }


  /**
   * @param rvwVariantsMap the rvwVariantsMap to set
   */
  public void setRvwVariantsMap(final Map<Long, RvwVariant> rvwVariantsMap) {
    this.rvwVariantsMap = rvwVariantsMap;
  }


  /**
   * @param a2lMappedVariantsMap the a2lMappedVariantsMap to set
   */
  public void setA2lMappedVariantsMap(final Map<Long, PidcVariant> a2lMappedVariantsMap) {
    this.a2lMappedVariantsMap = a2lMappedVariantsMap;
  }


  /**
   * @return the pidcVariants
   */
  public SortedSet<PidcVariant> getPidcVariants() {
    return this.pidcVariants;
  }


  /**
   * @param pidcVariants the pidcVariants to set
   */
  public void setPidcVariants(final SortedSet<PidcVariant> pidcVariants) {
    this.pidcVariants = pidcVariants;
  }


  /**
   * @return the rvwVariantsMap
   */
  public Map<Long, RvwVariant> getRvwVariantsMap() {
    return this.rvwVariantsMap;
  }


  /**
   * @return the a2lMappedVariantsMap
   */
  public Map<Long, PidcVariant> getA2lMappedVariantsMap() {
    return this.a2lMappedVariantsMap;
  }


  /**
   * @return the attrValMap
   */
  public Map<Long, Long> getAttrValMap() {
    return this.attrValMap;
  }


  /**
   * @param attrValMap the attrValMap to set
   */
  public void setAttrValMap(final Map<Long, Long> attrValMap) {
    this.attrValMap = attrValMap;
  }


  /**
   * @return the attrUsedMap
   */
  public Map<Long, String> getAttrUsedMap() {
    return this.attrUsedMap;
  }


  /**
   * @param attrUsedMap the attrUsedMap to set
   */
  public void setAttrUsedMap(final Map<Long, String> attrUsedMap) {
    this.attrUsedMap = attrUsedMap;
  }


  /**
   * @return the sameVarGrpFlag
   */
  public Map<Long, Boolean> getSameVarGrpFlag() {
    return sameVarGrpFlag;
  }


}
