/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;

/**
 * New class created for the purpose for Copy of param mapping from source to destination A2l file.
 *
 * @author rgo7cob
 */
public class ParamMappingRespBo {

  /**
   * Wp resp of the given wp defn version
   */
  private Map<Long, A2lWpResponsibility> wpRespMap = new HashMap<>();


  /**
   * Map of all db mapping records.
   */
  private final Map<Long, A2lWpParamMapping> wpParamMapping = new HashMap<>();

  /**
   * Key is param id and value is list of param mapping objects, original object can be taken from the wpParamMapping
   * map
   */
  private Map<Long, List<Long>> paramAndMappingMap = new HashMap<>();


  /**
   * Variant group map
   */
  private final Map<Long, A2lVariantGroup> a2lVarGrpMap = new HashMap<>();


  /**
   * @return the a2lVarGrpMap
   */
  public Map<Long, A2lVariantGroup> getA2lVarGrpMap() {
    return this.a2lVarGrpMap;
  }


  /**
   * @return the wpParamMapping
   */
  public Map<Long, A2lWpParamMapping> getWpParamMapping() {
    return this.wpParamMapping;
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
   * @return the paramAndMappingMap
   */
  public Map<Long, List<Long>> getParamAndMappingMap() {
    return this.paramAndMappingMap;
  }


  /**
   * @param paramAndMappingMap the paramAndMappingMap to set
   */
  public void setParamAndMappingMap(final Map<Long, List<Long>> paramAndMappingMap) {
    this.paramAndMappingMap = paramAndMappingMap;
  }


}
