/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

/**
 * @author gge6cob
 */
public class A2lWpParamMappingModel {

  /*
   * Key : A2lWpParamMappping Id
   */
  private Map<Long, A2lWpParamMapping> a2lWpParamMapping = new HashMap<>();

  private Long selectedWpDefnVersionId;


  /**
   * key -Name of resposible , value - List of wp Def listed based on Wp responsible
   */
  private Map<String, SortedSet<A2lWpResponsibility>> a2lWPResponsibleMap = new HashMap<>();

  /**
   * key -param id , value - map of wp id and Resp name
   */
  private Map<Long, Map<Long, String>> paramIdWithWpAndRespMap = new HashMap<>();

  /**
   * key -A2lWpResponsibility id , value - set of paramIds assigned to A2lWpResponsibility
   */
  private  Map<Long, Set<Long>> paramAndRespPalMap = new HashMap<>();


  /**
   * @return the a2lWpParamMapping
   */
  public Map<Long, A2lWpParamMapping> getA2lWpParamMapping() {
    return this.a2lWpParamMapping;
  }


  /**
   * @param a2lWpParamMapping the a2lWpParamMapping to set
   */
  public void setA2lWpParamMapping(final Map<Long, A2lWpParamMapping> a2lWpParamMapping) {
    this.a2lWpParamMapping = a2lWpParamMapping;
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
   * @return the a2lWPResponsibleMap
   */
  public Map<String, SortedSet<A2lWpResponsibility>> getA2lWPResponsibleMap() {
    return this.a2lWPResponsibleMap;
  }


  /**
   * @param a2lWPResponsibleMap the a2lWPResponsibleMap to set
   */
  public void setA2lWPResponsibleMap(final Map<String, SortedSet<A2lWpResponsibility>> a2lWPResponsibleMap) {
    this.a2lWPResponsibleMap = a2lWPResponsibleMap;
  }


  /**
   * @return the paramIdWithWpAndRespMap
   */
  public Map<Long, Map<Long, String>> getParamIdWithWpAndRespMap() {
    return this.paramIdWithWpAndRespMap;
  }


  /**
   * @param paramIdWithWpAndRespMap the paramIdWithWpAndRespMap to set
   */
  public void setParamIdWithWpAndRespMap(final Map<Long, Map<Long, String>> paramIdWithWpAndRespMap) {
    this.paramIdWithWpAndRespMap = paramIdWithWpAndRespMap;
  }


  /**
   * @return the paramAndRespPalMap
   */
  public Map<Long, Set<Long>> getParamAndRespPalMap() {
    return this.paramAndRespPalMap;
  }


}
