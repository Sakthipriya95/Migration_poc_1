/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

/**
 * Model for A2l WP definition page
 *
 * @author pdh2cob
 */
public class A2lWpDefinitionModel {

  /**
   * Flag to indicate if Responsibilities can be mapped to parameters
   */
  private boolean isParamMappingAllowed;

  /**
   * Instance of selected A2lWpDefinitionVersion
   */
  private Long selectedWpDefnVersionId;


  /**
   * Map of A2lWpResponsibility for selected A2lWpDefinitionVersion. Key - A2lWpResponsibility id, value -
   * A2lWpResponsibility
   */
  private Map<Long, A2lWpResponsibility> wpRespMap = new HashMap<>();


  /**
   * key -Name of resposible , value - List of wp Def listed based on Wp responsible
   */
  private Map<String, SortedSet<A2lWpResponsibility>> a2lWPResponsibleMap = new HashMap<>();


  /**
   * key -Name of Workpackage , value - List of wp Def listed based on Wp
   */
  private Map<String, Set<A2lWpResponsibility>> a2lWpRespNodeMergedMap = new HashMap<>();


  /**
   * key - Variant group id and value is the Variant group Object
   */
  private final Map<Long, A2lVariantGroup> a2lVariantGroupMap = new HashMap<>();

  /**
   * Instance of selected Active A2lWpDefinitionVersion
   */
  private Long activeWpDefnVersionId;

  /**
   * key - Variant id ; value - Variant Group id
   */
  private Map<Long, Long> a2lVarToVarGrpMap = new HashMap<>();


  /**
   * @return the a2lVariantGroupMap
   */
  public Map<Long, A2lVariantGroup> getA2lVariantGroupMap() {
    return this.a2lVariantGroupMap;
  }


  /**
   * @return the isParamMappingAllowed
   */
  public boolean isParamMappingAllowed() {
    return this.isParamMappingAllowed;
  }


  /**
   * @param isParamMappingAllowed the isParamMappingAllowed to set
   */
  public void setParamMappingAllowed(final boolean isParamMappingAllowed) {
    this.isParamMappingAllowed = isParamMappingAllowed;
  }


  /**
   * @return the selectedWpDefnVersionId
   */
  public Long getSelectedWpDefnVersionId() {
    return this.selectedWpDefnVersionId;
  }


  /**
   * @param selectedWpDefnVersionId the selectedWpDefnVersionId to set
   */
  public void setSelectedWpDefnVersionId(final Long selectedWpDefnVersionId) {
    this.selectedWpDefnVersionId = selectedWpDefnVersionId;
  }


  /**
   * @return the wpRespMap
   */
  public Map<Long, A2lWpResponsibility> getWpRespMap() {
    return this.wpRespMap;
  }


  /**
   * @param wpRespMap the wpRespMap to set
   */
  public void setWpRespMap(final Map<Long, A2lWpResponsibility> wpRespMap) {
    this.wpRespMap = wpRespMap;
  }


  /**
   * @return the a2lWPResponsibleMap
   */
  public Map<String, SortedSet<A2lWpResponsibility>> getA2lWPResponsibleMap() {
    return this.a2lWPResponsibleMap;
  }

  /**
   * @return the a2lWPResponsibleMap
   */
  public void setA2lWPResponsibleMap(final Map<String, SortedSet<A2lWpResponsibility>> a2lWPRespMap) {
    this.a2lWPResponsibleMap = a2lWPRespMap;
  }


  /**
   * @return the a2lWpRespNodeMergedMap
   */
  public Map<String, Set<A2lWpResponsibility>> getA2lWpRespNodeMergedMap() {
    return this.a2lWpRespNodeMergedMap;
  }


  /**
   * @param a2lWpRespNodeMergedMap the a2lWpRespNodeMergedMap to set
   */
  public void setA2lWpRespNodeMergedMap(final Map<String, Set<A2lWpResponsibility>> a2lWpRespNodeMergedMap) {
    this.a2lWpRespNodeMergedMap = a2lWpRespNodeMergedMap;
  }


  /**
   * @return the activeWpDefnVersionId
   */
  public Long getActiveWpDefnVersionId() {
    return this.activeWpDefnVersionId;
  }


  /**
   * @param activeWpDefnVersionId the activeWpDefnVersionId to set
   */
  public void setActiveWpDefnVersionId(final Long activeWpDefnVersionId) {
    this.activeWpDefnVersionId = activeWpDefnVersionId;
  }


  /**
   * @return the a2lVarToVarGrpMap
   */
  public Map<Long, Long> getA2lVarToVarGrpMap() {
    return this.a2lVarToVarGrpMap;
  }


  /**
   * @param a2lVarToVarGrpMap the a2lVarToVarGrpMap to set
   */
  public void setA2lVarToVarGrpMap(final Map<Long, Long> a2lVarToVarGrpMap) {
    this.a2lVarToVarGrpMap = a2lVarToVarGrpMap;
  }


}
