/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.Parameter;

/**
 * @author rgo7cob
 */
public class ParameterPropOutput {


  /**
   * Map of param name and type
   */
  private final Map<String, String> paramNameTypeMap = new HashMap<>();


  /**
   * key - param name , value - CDRFunction
   */
  private final Map<String, Function> paramFuncObjMap = new HashMap<>();

  /**
   * key - param name , value - CDRFuncParameter
   */
  private final Map<String, Parameter> paramNameObjMap = new HashMap<>();


  private final Map<String, Map<String, String>> paramPropMap = new ConcurrentHashMap<>();


  /**
   * @return the paramNameTypeMap
   */
  public Map<String, String> getParamNameTypeMap() {
    return this.paramNameTypeMap;
  }


  /**
   * @return the paramFuncObjMap
   */
  public Map<String, Function> getParamFuncObjMap() {
    return this.paramFuncObjMap;
  }


  /**
   * @return the paramNameObjMap
   */
  public Map<String, Parameter> getParamNameObjMap() {
    return this.paramNameObjMap;
  }


  /**
   * @return the paramPropMap
   */
  public Map<String, Map<String, String>> getParamPropMap() {
    return this.paramPropMap;
  }

}
