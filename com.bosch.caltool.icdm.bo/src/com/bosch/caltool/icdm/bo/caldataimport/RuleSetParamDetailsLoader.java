/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.caldataimport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.bo.a2l.ParameterPropOutput;
import com.bosch.caltool.icdm.bo.cdr.RuleSetParamWithFunction;
import com.bosch.caltool.icdm.bo.cdr.RuleSetParameterLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.Activator;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportData;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;


/**
 * Class to load and validate the parameter details of the rule set
 *
 * @author dmo5cob
 */
public class RuleSetParamDetailsLoader extends AbstractSimpleBusinessObject
    implements ICalDataImportParamDetailsLoader {


  /**
   * component package
   */
  private final RuleSet ruleSet;

  /**
   * Set of invalid parameters identified
   */
  private final Set<String> paramsNotInRuleset = new HashSet<>();

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
   * @param ruleSet component package
   * @param cdrDataProvider CDRDataProvider
   */
  public RuleSetParamDetailsLoader(final RuleSet ruleSet, final ServiceData serviceData) {
    super(serviceData);
    this.ruleSet = ruleSet;

  }

  /**
   * Identify the parameters in the given input, that are not part of the given component package
   *
   * @throws DataException if base component mapping is invalid
   */
  @Override
  public void run(final Set<String> paramNameSet, final String funcName, final CalDataImportData importData)
      throws DataException {
    getLogger().debug("Fetching details of input parameters ...");
    // ICDM-1931
    handleVarCodedParams(paramNameSet);
    findParamProps(paramNameSet);

    // Identifiy the parameters in the input file but not in the ruleset
    findParamsNotInRuleSet(paramNameSet);

    ParameterLoader loader = new ParameterLoader(getServiceData());

    ParameterPropOutput paramPropOutput = loader.fetchCDRParamsAndFuncs(this.paramsNotInRuleset);

    this.paramFuncObjMap.putAll(paramPropOutput.getParamFuncObjMap());
    this.paramPropMap.putAll(paramPropOutput.getParamPropMap());
    this.paramNameObjMap.putAll(paramPropOutput.getParamNameObjMap());

    this.paramNameTypeMap.putAll(paramPropOutput.getParamNameTypeMap());
  }


  /**
   * @param paramNameSet
   */
  private void handleVarCodedParams(final Set<String> paramNameSet) {
    List<String> varCodedParams = new ArrayList<>();
    Set<String> setToIterate = new HashSet<>();
    setToIterate.addAll(paramNameSet);
    for (String paramName : setToIterate) {
      if (ApicUtil.isVariantCoded(paramName)) { // ICDM-1931
        // remove the parameter name if it is a variant coded parameter
        paramNameSet.remove(paramName);
        // add it to the list for display purpose
        varCodedParams.add(paramName);
      }
    }
    if (CommonUtils.isNotEmpty(varCodedParams)) {
      CDMLogger.getInstance().infoDialog(
          "The import file contains variant encoded parameters which won't be considered for the import. They are not supported for rule creation.\n" +
              varCodedParams,
          Activator.PLUGIN_ID);
    }

  }


  /**
   * Fetch the parameter properties
   *
   * @param inputParamSet
   */
  private void findParamProps(final Set<String> paramNameSet) throws DataException {

    getLogger().debug("Fetching properties of valid parameters ...");

    this.paramPropMap.clear();

    RuleSetParameterLoader loader = new RuleSetParameterLoader(getServiceData());

    for (RuleSetParamWithFunction paramWithFunction : loader.getAllRuleSetParamWithFunc(this.ruleSet.getId())) {

      RuleSetParameter param = paramWithFunction.getRuleSetParam();
      if (paramNameSet.contains(param.getName())) {

        this.paramNameTypeMap.put(param.getName(), param.getType());
        // Param properties can be null. Cannot be changed to CHM
        Map<String, String> propMap = new HashMap<>();
        propMap.put(CDRConstants.CDIKEY_PARAM_NAME, param.getName());
        propMap.put(CDRConstants.CDIKEY_FUNCTION_NAME, paramWithFunction.getFunction().getName());
        propMap.put(CDRConstants.CDIKEY_PARAM_CLASS, param.getpClassText());
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
  private void findParamsNotInRuleSet(final Set<String> inputDataMap) {

    getLogger().debug("Finding invalid parameters that are not part of rule set - " + this.ruleSet.getId());

    this.paramsNotInRuleset.clear();
    this.paramsNotInRuleset.addAll(inputDataMap);
    this.paramsNotInRuleset.removeAll(this.paramPropMap.keySet());

    getLogger().debug("Invalid paramters identified = " + this.paramsNotInRuleset.size());

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Set<String> getInvalidParams() {
    return new HashSet<>(this.paramsNotInRuleset);
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

}
