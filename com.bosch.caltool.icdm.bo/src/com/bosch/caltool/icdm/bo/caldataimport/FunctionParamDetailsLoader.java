/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.caldataimport;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.FunctionLoader;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.bo.a2l.ParameterPropOutput;
import com.bosch.caltool.icdm.bo.cdr.ParameterRuleFetcher;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.a2l.ParameterRulesResponse;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportData;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;


/**
 * Load and validate the parameter details of the function(s)
 *
 * @author bru2cob
 */
//ICDM-1540
public class FunctionParamDetailsLoader extends AbstractSimpleBusinessObject
    implements ICalDataImportParamDetailsLoader {

  /**
   * cdr function
   */
  private final Function cdrFunction;

  /**
   * Set of invalid parameters identified
   */
  private final Set<String> invalidParamMap = new HashSet<>();

  /**
   * Properties of parameters retrieved. <br>
   * Key - parameter name <br>
   * Value - Map of &ltproperty, value as string&gt
   */
  private final Map<String, Map<String, String>> paramPropMap = new ConcurrentHashMap<>();

  /**
   * Map of param name and type
   */
  private final Map<String, String> paramNameTypeMap = new HashMap<>();
  private final String funcVersion;


  /**
   * key - param name , value - CDRFunction
   */
  private final Map<String, Function> paramFuncObjMap = new HashMap<>();

  /**
   * key - param name , value - CDRFuncParameter
   */
  private final Map<String, Parameter> paramNameObjMap = new HashMap<>();

  /**
   * Constructor
   *
   * @param cdrFunction Function
   * @param funcVersion function version
   * @param serviceData service data
   */
  public FunctionParamDetailsLoader(final Function cdrFunction, final String funcVersion,
      final ServiceData serviceData) {

    super(serviceData);
    this.cdrFunction = cdrFunction;
    this.funcVersion = funcVersion;

  }

  /**
   * Identify the parameters in the given input, that are not part of the given cdr function
   *
   * @throws DataException if base component mapping is invalid
   */
  @Override
  public void run(final Set<String> paramNameSet, final String funcName, final CalDataImportData importData)
      throws IcdmException {

    getLogger().debug("Fetching details of input parameters ...");
    findParamProps(paramNameSet);

    // Identifiy the parameters in the input file but not in the function
    findParamsNotInFunction(paramNameSet);

    // if function name not same in case of plausibel import
    if ((null != funcName) && !CommonUtils.isEqual(funcName, this.cdrFunction.getName())) {
      throw new DataException(
          "Function name in the Plausibel file not matching with the name - " + this.cdrFunction.getName());
    }

    ParameterPropOutput paramPropOutput =
        new ParameterLoader(getServiceData()).fetchCDRParamsAndFuncs(this.invalidParamMap);

    this.paramFuncObjMap.putAll(paramPropOutput.getParamFuncObjMap());
    this.paramPropMap.putAll(paramPropOutput.getParamPropMap());
    this.paramNameObjMap.putAll(paramPropOutput.getParamNameObjMap());

    this.paramNameTypeMap.putAll(paramPropOutput.getParamNameTypeMap());

    FunctionLoader funcLoader = new FunctionLoader(getServiceData());
    for (String paramName : paramNameSet) {
      StringBuilder funNames = new StringBuilder();
      SortedSet<Function> functions = funcLoader.getFunctionsByparamName(paramName);
      for (Function func : functions) {
        funNames.append(func.getName()).append(",");
      }
      importData.getParamFuncMap().put(paramName, funNames.toString().substring(0, funNames.toString().length() - 1));
    }

    // Collect the function names involved to a set, to avoid duplicate processing
    Set<String> funcNameSet = this.paramFuncObjMap.values().stream().map(Function::getName).collect(Collectors.toSet());

    ParameterRuleFetcher fetcher = new ParameterRuleFetcher(getServiceData());
    for (String func : funcNameSet) {
      ParameterRulesResponse paramRuleResponse = fetcher.createParamRulesOutput(func, null, null, paramNameSet);
      importData.getFuncParamRespMap().put(func, paramRuleResponse);
    }

  }

  /**
   * Fetch the parameter properties
   *
   * @param inputParamSet
   * @throws DataException
   */
  private void findParamProps(final Set<String> paramNameSet) throws DataException {

    getLogger().debug("Fetching properties of valid parameters ...");

    this.paramPropMap.clear();

    ParameterLoader loader = new ParameterLoader(getServiceData());

    // pass null in case of function version all
    String funcVersToUse = null;
    if ((this.funcVersion != null) && !ApicConstants.OPTION_ALL.equalsIgnoreCase(this.funcVersion)) {
      funcVersToUse = this.funcVersion;
    }

    for (Parameter param : loader.getParamsMap(this.cdrFunction.getName(), funcVersToUse, null, null).values()) {
      if (paramNameSet.contains(param.getName())) {
        this.paramNameTypeMap.put(param.getName(), param.getType());
        // Param properties can be null. Cannot be changed to CHM
        Map<String, String> propMap = new HashMap<>();
        propMap.put(CDRConstants.CDIKEY_PARAM_NAME, param.getName());
        propMap.put(CDRConstants.CDIKEY_FUNCTION_NAME, this.cdrFunction.getName());
        propMap.put(CDRConstants.CDIKEY_PARAM_CLASS, param.getpClassText() == null ? "" : param.getpClassText());
        propMap.put(CDRConstants.CDIKEY_CODE_WORD, param.getCodeWord());
        propMap.put(CDRConstants.CDIKEY_LONG_NAME, param.getLongName());
        propMap.put(CDRConstants.CDIKEY_CAL_HINT, param.getParamHint());
        propMap.put(CDRConstants.CDIKEY_BIT_WISE, param.getIsBitWise());
        this.paramPropMap.put(param.getName(), propMap);
      }
    }
    getLogger().debug("No. of records retrieved = " + this.paramPropMap.size());

  }


  /**
   * Fetches the parameters in the input file but not in rule set
   *
   * @param inputDataMap
   */
  private void findParamsNotInFunction(final Set<String> inputDataMap) {

    getLogger().debug("Finding invalid parameters that are not part of function - " + this.cdrFunction.getId());

    this.invalidParamMap.clear();
    this.invalidParamMap.addAll(inputDataMap);
    this.invalidParamMap.removeAll(this.paramPropMap.keySet());

    getLogger().debug("Invalid paramters identified = " + this.invalidParamMap.size());

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Set<String> getInvalidParams() {
    return this.invalidParamMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, Map<String, String>> getParamProps() {
    return this.paramPropMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, String> getParamNameType() {
    return this.paramNameTypeMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, Function> getParamFuncObjMap() {
    return this.paramFuncObjMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, Parameter> getParamNameObjMap() {
    return this.paramNameObjMap;
  }


}
