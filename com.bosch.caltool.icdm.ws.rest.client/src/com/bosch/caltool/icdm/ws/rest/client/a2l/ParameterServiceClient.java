/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.a2l.FunctionInput;
import com.bosch.caltool.icdm.model.a2l.FunctionParamProperties;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.a2l.ParameterInput;
import com.bosch.caltool.icdm.model.a2l.ParameterRulesResponse;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author rgo7cob
 */
public class ParameterServiceClient extends AbstractRestServiceClient {

  /**
   */
  public ParameterServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_FUNCTION_PARAM);
  }

  /**
   * @param funcName funcName
   * @param version version
   * @param byVariant func variant
   * @return the parameter Rules output
   * @throws ApicWebServiceException error while calling service
   */
  public ParameterRulesResponse getParamRulesOutput(final String funcName, final String version, final String byVariant)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_PARAM_RULES)
        .queryParam(WsCommonConstants.RWS_QP_NAME, funcName).queryParam(WsCommonConstants.RWS_QP_VERSION, version)
        .queryParam(WsCommonConstants.RWS_QP_BY_VAR, byVariant);
    GenericType<ParameterRulesResponse> type = new GenericType<ParameterRulesResponse>() {};
    return get(wsTarget, type);
  }

  /**
   * @param paramName paramName
   * @param paramType paramType
   * @return the parameter Rules output
   * @throws ApicWebServiceException error while calling service
   */
  public ParameterRulesResponse getSingleParamRules(final String paramName, final String paramType)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_SINGLE_PARAM_RULES)
        .queryParam(WsCommonConstants.RWS_QP_NAME, paramName)
        .queryParam(WsCommonConstants.RWS_QP_PARAM_TYPE, paramType);
    GenericType<ParameterRulesResponse> type = new GenericType<ParameterRulesResponse>() {};
    return get(wsTarget, type);
  }

  /**
   * @param funcNameSet set of function names
   * @return parameters in the functions
   * @throws ApicWebServiceException error while calling service
   */
  public Set<String> getParamsListByFuncNameSet(final Set<String> funcNameSet) throws ApicWebServiceException {
    FunctionInput funcInput = new FunctionInput();
    funcInput.setFunctionSet(funcNameSet);
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_PARAMS_BY_FUNC_NAME);
    GenericType<Set<String>> type = new GenericType<Set<String>>() {};
    return post(wsTarget, funcInput, type);
  }

  /**
   * Method to fetch the count of params for set of selected functions
   *
   * @param funcNameSet as input
   * @return count of params
   * @throws ApicWebServiceException error while calling service
   */
  public Long getParamCountbyFunctionNameSet(final Set<String> funcNameSet) throws ApicWebServiceException {
    FunctionInput funcInput = new FunctionInput();
    funcInput.setFunctionSet(funcNameSet);
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_PARAM_COUNT);
    return post(wsTarget, funcInput, Long.class);
  }

  /**
   * @param paramName name
   * @param paramType type
   * @return the parameter Rules output
   * @throws ApicWebServiceException error while calling service
   */
  public Parameter getParameter(final String paramName, final String paramType) throws ApicWebServiceException {
    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_GET_PARAM).queryParam(WsCommonConstants.RWS_QP_NAME, paramName)
            .queryParam(WsCommonConstants.RWS_QP_PARAM_TYPE, paramType);
    GenericType<Parameter> type = new GenericType<Parameter>() {};
    return get(wsTarget, type);
  }

  /**
   * @param paramName parameter Name
   * @return Parameter object
   * @throws ApicWebServiceException error while calling service
   */
  public Parameter getParameterOnlyByName(final String paramName) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_PARAMS_ONLY_BY_NAME)
        .queryParam(WsCommonConstants.RWS_QP_NAME, paramName);
    GenericType<Parameter> type = new GenericType<Parameter>() {};
    return get(wsTarget, type);
  }

  /**
   * Get all parameter objects, with the given name.
   *
   * @param paramName parameter name
   * @return Map. Key - param ID; value - parameter
   * @throws ApicWebServiceException error while calling service
   */
  public Map<Long, Parameter> getParamByNameOnly(final String paramName) throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_PARAM_BY_NAME_ONLY)
        .queryParam(WsCommonConstants.RWS_QP_NAME, paramName);
    GenericType<Map<Long, Parameter>> retMap = new GenericType<Map<Long, Parameter>>() {};
    return get(wsTarget, retMap);
  }

  /**
   * @param paramNameSet paramNameSet
   * @return the param name set
   * @throws ApicWebServiceException error while calling service
   */
  public Map<String, Parameter> getParamsByName(final Set<String> paramNameSet) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_PARAMS_BY_NAME)
        .queryParam(WsCommonConstants.RWS_QP_NAME_SET, paramNameSet);

    GenericType<Map<String, Parameter>> type = new GenericType<Map<String, Parameter>>() {};
    return get(wsTarget, type);
  }


  /**
   * @param paramIdSet Param id set
   * @return map of param id and parameter object
   * @throws ApicWebServiceException error while calling service
   */
  public Map<Long, Parameter> getParamsUsingIds(final Set<Long> paramIdSet) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.PARAMS_USING_IDS);

    GenericType<Map<Long, Parameter>> type = new GenericType<Map<Long, Parameter>>() {};
    return post(wsTarget, paramIdSet, type);
  }

  /**
   * @param parameter parameter
   * @return the parameter
   * @throws ApicWebServiceException error while calling service
   */
  public Parameter update(final Parameter parameter) throws ApicWebServiceException {
    return update(getWsBase(), parameter);
  }


  /**
   * @param labelSet set of labels
   * @param a2lLabelset set of labels in a2l file
   * @return invalid label set
   * @throws ApicWebServiceException error while calling service
   */
  public Set<String> getMissingLabels(final Set<String> labelSet, final Set<String> a2lLabelset)
      throws ApicWebServiceException {
    ParameterInput parameterInput = new ParameterInput();
    parameterInput.setLabelSet(labelSet);
    parameterInput.setA2lLabelset(a2lLabelset);

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_LABEL_MISMATCH);
    GenericType<Set<String>> type = new GenericType<Set<String>>() {};
    return post(wsTarget, parameterInput, type);
  }

  /**
   * @param paramName parameter Name
   * @param funcName function Name
   * @param ruleSetId ruleSetId
   * @return FunctionParamProperties List
   * @throws ApicWebServiceException error while calling service
   */
  public SortedSet<FunctionParamProperties> getsearchParameters(final String paramName, final String funcName,
      final Long ruleSetId)
      throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_SEARCH_PARAMETERS)
        .queryParam(WsCommonConstants.RWS_QP_PARAM_NAME, paramName)
        .queryParam(WsCommonConstants.RWS_QP_FUNCTION_NAME, funcName)
        .queryParam(WsCommonConstants.RWS_QP_RULESET_ID, ruleSetId);
    GenericType<SortedSet<FunctionParamProperties>> type = new GenericType<SortedSet<FunctionParamProperties>>() {};
    return get(wsTarget, type);
  }

  /**
   * @param firstName param Fisrt Name
   * @param lastName param Last name
   * @return param id list
   * @throws ApicWebServiceException error while calling service
   */
  public List<Long> getParamIdList(final String firstName, final String lastName) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_VAR_CODED_PARAM_ID)
        .queryParam(WsCommonConstants.RWS_QP_PARAM_FIRST_NAME, firstName)
        .queryParam(WsCommonConstants.RWS_QP_PARAM_LAST_NAME, lastName);
    GenericType<List<Long>> type = new GenericType<List<Long>>() {};
    return get(wsTarget, type);
  }


  /**
   * Get Parameter using its id
   *
   * @param objId object's id
   * @return Parameter object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Parameter get(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, Parameter.class);
  }

  /**
   * @param paramNames input list
   * @return invalid parameters among the input list
   * @throws ApicWebServiceException service error
   */
  public List<String> getInvalidParameters(final List<String> paramNames) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_INVALID_PARAMETER);
    GenericType<List<String>> type = new GenericType<List<String>>() {};
    List<String> retList = post(wsTarget, paramNames, type);
    LOGGER.debug("Invalid params count among the input = {}", retList.size());

    return retList;
  }
}
