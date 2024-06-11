/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;

/**
 * @author bru2cob
 */
public class A2LDetailsStructureModel {

  /**
   * Key - id , value - variants belonging to a2l file
   */
  Map<Long, PidcVariant> a2lMappedVariantsMap = new TreeMap<>();
  /**
   * Key - id , value - variant group
   */
  Map<Long, A2lVariantGroup> a2lVariantGrpMap = new TreeMap<>();
  /**
   * Key - variant group id , value - variants mapped to a2l variant group
   */
  Map<Long, List<PidcVariant>> mappedVariantsMap = new TreeMap<>();
  /**
   * A2l definition version
   */
  A2lWpDefnVersion wpDefVersion;
  /**
   * variants set not mapped to variant group
   */
  SortedSet<PidcVariant> unmappedVariants = new TreeSet<>();


  /**
   * kwy is the Pidc variant id and value is group mapping
   */
  private final Map<Long, A2lVarGrpVariantMapping> groupMappingMap = new HashMap<>();


  /**
   * @return the unmappedVariants
   */
  public Set<PidcVariant> getUnmappedVariants() {
    return this.unmappedVariants;
  }


  /**
   * @param unmappedVariants the unmappedVariants to set
   */
  public void setUnmappedVariants(final SortedSet<PidcVariant> unmappedVariants) {
    this.unmappedVariants = unmappedVariants;
  }

  /**
   * @return the a2lMappedVariantsMap
   */
  public Map<Long, PidcVariant> getA2lMappedVariantsMap() {
    return this.a2lMappedVariantsMap;
  }

  /**
   * @param a2lMappedVariantsMap the a2lMappedVariantsMap to set
   */
  public void setA2lMappedVariantsMap(final Map<Long, PidcVariant> a2lMappedVariantsMap) {
    this.a2lMappedVariantsMap = a2lMappedVariantsMap;
  }

  /**
   * @return the a2lVariantGrpMap
   */
  public Map<Long, A2lVariantGroup> getA2lVariantGrpMap() {
    return this.a2lVariantGrpMap;
  }

  /**
   * @param a2lVariantGrpMap the a2lVariantGrpMap to set
   */
  public void setA2lVariantGrpMap(final Map<Long, A2lVariantGroup> a2lVariantGrpMap) {
    this.a2lVariantGrpMap = a2lVariantGrpMap;
  }

  /**
   * @return the mappedVariantsMap
   */
  public Map<Long, List<PidcVariant>> getMappedVariantsMap() {
    return this.mappedVariantsMap;
  }

  /**
   * @param mappedVariantsMap the mappedVariantsMap to set
   */
  public void setMappedVariantsMap(final Map<Long, List<PidcVariant>> mappedVariantsMap) {
    this.mappedVariantsMap = mappedVariantsMap;
  }

  /**
   * @return the wpDefVersion
   */
  public A2lWpDefnVersion getWpDefVersion() {
    return this.wpDefVersion;
  }

  /**
   * @param wpDefVersion the wpDefVersion to set
   */
  public void setWpDefVersion(final A2lWpDefnVersion wpDefVersion) {
    this.wpDefVersion = wpDefVersion;
  }


  /**
   * @return the groupMappingMap
   */
  public Map<Long, A2lVarGrpVariantMapping> getGroupMappingMap() {
    return this.groupMappingMap;
  }


}
