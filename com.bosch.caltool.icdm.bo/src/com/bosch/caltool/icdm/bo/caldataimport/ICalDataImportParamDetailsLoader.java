/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.caldataimport;

import java.util.Map;
import java.util.Set;

import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportData;


/**
 * Retrieve the details and validate the input parameters before importing the calibaration data
 *
 * @author bne4cob
 */
public interface ICalDataImportParamDetailsLoader {

  /**
   * Load the details of the input parameters, validations if any related to the parameters will also be performed
   *
   * @param set map of input parameters and CalData objects
   * @param funcName Func name
   * @param importData
   * @throws IcdmException any error during validation
   */
  void run(Set<String> set, String funcName, CalDataImportData importData) throws IcdmException;


  /**
   * @return the set of invalid parameters among the input parameters
   */
  Set<String> getInvalidParams();

  /**
   * Properties of valid parameters<br>
   * Key - parameter name <br>
   * Value - Map of &ltproperty, value as string&gt
   *
   * @return the map of parameter details
   */
  Map<String, Map<String, String>> getParamProps();


  /**
   * @return Map of param name and type
   */
  Map<String, String> getParamNameType();


  /**
   * @return the paramFuncObjMap
   */
  public Map<String, Function> getParamFuncObjMap();


  /**
   * @return the paramNameObjMap
   */
  public Map<String, Parameter> getParamNameObjMap();


}
