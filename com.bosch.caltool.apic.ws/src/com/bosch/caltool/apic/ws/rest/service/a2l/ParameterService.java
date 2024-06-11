/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.a2l;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.dmframework.bo.AbstractCommand.COMMAND_MODE;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.bo.cdr.ParameterCommand;
import com.bosch.caltool.icdm.bo.cdr.ParameterRuleFetcher;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.FunctionInput;
import com.bosch.caltool.icdm.model.a2l.FunctionParamProperties;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.a2l.ParameterInput;
import com.bosch.caltool.icdm.model.a2l.ParameterRulesResponse;


/**
 * Parameter services
 *
 * @author rgo7cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_FUNCTION_PARAM)
public class ParameterService extends AbstractRestService {


  /**
   * @param funcName funcName
   * @param version version
   * @param byVariant func variant
   * @return Response with list of functions
   * @throws IcdmException data retreval error
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_PARAM_RULES)
  @CompressData
  public Response getParamRules(@QueryParam(value = WsCommonConstants.RWS_QP_NAME) final String funcName,
      @QueryParam(value = WsCommonConstants.RWS_QP_VERSION) final String version,
      @QueryParam(value = WsCommonConstants.RWS_QP_BY_VAR) final String byVariant)
      throws IcdmException {

    // Fetch rules
    ParameterRuleFetcher ruleFetcher = new ParameterRuleFetcher(getServiceData());
    ParameterRulesResponse rulesOutput = ruleFetcher.createParamRulesOutput(funcName, version, byVariant, null);

    getLogger().info("Parameter.getSearchFunctions() completed. Parameters found = {}", rulesOutput);

    return Response.ok(rulesOutput).build();

  }

  /**
   * @param functionInput functions
   * @return set of parameter names
   * @throws IcdmException data retreval error
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_PARAMS_BY_FUNC_NAME)
  @CompressData
  public Response getParamsListByFuncNameSet(final FunctionInput functionInput) throws IcdmException {
    Set<String> paramNameSet = new HashSet<>();
    ParameterLoader parameterLoader = new ParameterLoader(getServiceData());
    for (String funcName : functionInput.getFunctionSet()) {
      paramNameSet.addAll(parameterLoader.getParamsMap(funcName, null, null, null).keySet());
    }
    return Response.ok(paramNameSet).build();
  }


  /**
   * @param paramName parameter Name
   * @param paramType type
   * @return Response with list of functions
   * @throws IcdmException data retreval error
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_SINGLE_PARAM_RULES)
  @CompressData
  public Response getParamRules(@QueryParam(value = WsCommonConstants.RWS_QP_NAME) final String paramName,
      @QueryParam(value = WsCommonConstants.RWS_QP_PARAM_TYPE) final String paramType)
      throws IcdmException {

    // Fetch parameter
    Parameter parameter = new ParameterLoader(getServiceData()).getParamByName(paramName, paramType);

    Map<String, Parameter> paramMap = new HashMap<>();
    paramMap.put(paramName, parameter);

    // Fetch rules
    ParameterRuleFetcher ruleFetcher = new ParameterRuleFetcher(getServiceData());
    ParameterRulesResponse rulesOutput = ruleFetcher.getParamRuleOutput(null, null, paramMap);
    rulesOutput.setParamMap(paramMap);

    getLogger().info("Parameter.getParamRules() completed. Parameters found = {}", rulesOutput);

    return Response.ok(rulesOutput).build();
  }


  /**
   * @param paramName parameter Name
   * @param paramType Type
   * @return Response with list of functions
   * @throws IcdmException data retreval error
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_PARAM)
  @CompressData
  public Response getParamByName(@QueryParam(value = WsCommonConstants.RWS_QP_NAME) final String paramName,
      @QueryParam(value = WsCommonConstants.RWS_QP_PARAM_TYPE) final String paramType)
      throws IcdmException {

    Parameter parameter = new ParameterLoader(getServiceData()).getParamByName(paramName, paramType);

    getLogger().info("Parameter.getParamByName() completed. Parameters found = {}", parameter);

    return Response.ok(parameter).build();

  }

  /**
   * @param paramName parameter name
   * @return parameter object
   * @throws IcdmException data retreval error
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_PARAMS_ONLY_BY_NAME)
  @CompressData
  public Response getParamOnlyByName(@QueryParam(value = WsCommonConstants.RWS_QP_NAME) final String paramName)
      throws IcdmException {

    ParameterLoader loader = new ParameterLoader(getServiceData());

    // Fetch all functions
    Parameter parameter = loader.getParamOnlyByName(paramName);

    getLogger().info("Parameter.getParamOnlyByName() completed. Parameters found = {}", parameter);

    return Response.ok(parameter).build();
  }

  /**
   * @param paramNameSet paramName
   * @return Response with list of functions
   * @throws IcdmException IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_PARAMS_BY_NAME)
  @CompressData
  public Response getParamsByName(@QueryParam(value = WsCommonConstants.RWS_QP_NAME_SET) final Set<String> paramNameSet)
      throws IcdmException {

    // TODO can we change the service so that the below substring logic is done at the client level?
    Set<String> modifiedSet =
        paramNameSet.stream().map(p -> p.substring(1, p.length() - 1)).collect(Collectors.toSet());

    // Fetch all functions
    Map<String, Parameter> paramMap = new ParameterLoader(getServiceData()).getParamsByName(modifiedSet, null);

    getLogger().info("Parameter.getParamsByName() completed. Parameters found = {}", paramMap.size());

    return Response.ok(paramMap).build();
  }

  /**
   * @param paramIds param id
   * @return map of param id and param object
   * @throws IcdmException exception
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.PARAMS_USING_IDS)
  @CompressData
  public Response getParamsUsingIds(final Set<Long> paramIds) throws IcdmException {
    Map<Long, Parameter> paramMap = new ParameterLoader(getServiceData()).getParamsByIds(paramIds);
    return Response.ok(paramMap).build();
  }


  /**
   * Get all parameter objects, with the given name.
   *
   * @param paramName param Name
   * @return Response with Map. Key - param ID; value - parameter
   * @throws IcdmException IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_PARAM_BY_NAME_ONLY)
  @CompressData
  public Response getParamByNameOnly(@QueryParam(value = WsCommonConstants.RWS_QP_NAME) final String paramName)
      throws IcdmException {

    // Fetch all params with the given name
    Map<Long, Parameter> paramMap = new ParameterLoader(getServiceData()).getParamByNameOnly(paramName);

    getLogger().info("Parameter.getParamByNameOnly() completed. Parameters found = {}", paramMap.size());

    return Response.ok(paramMap).build();
  }

  /**
   * @param param parameter
   * @return Response with updated parameter
   * @throws IcdmException IcdmException
   */
  @PUT
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response update(final Parameter param) throws IcdmException {
    ParameterLoader loader = new ParameterLoader(getServiceData());
    ParameterCommand command = new ParameterCommand(getServiceData(), param, loader, COMMAND_MODE.UPDATE);
    executeCommand(command);
    Parameter newData = command.getNewData();
    getLogger().info("Parameter.update() completed.");
    return Response.ok(newData).build();
  }


  /**
   * @param parameterInput parameterInput complex object
   * @return invlaid labels
   * @throws IcdmException exception
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_LABEL_MISMATCH)
  @CompressData
  public Set<String> getMissingLabels(final ParameterInput parameterInput) throws IcdmException {
    ParameterLoader loader = new ParameterLoader(getServiceData());
    return loader.getMismatchLabelList(parameterInput.getLabelSet(), parameterInput.getA2lLabelset());
  }

  /**
   * Method to fetch the count of params for set of selected functions
   *
   * @param functionInput as input
   * @return count of params
   * @throws IcdmException exception
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_PARAM_COUNT)
  @CompressData
  public Long getParamCountbyFunctionNameSet(final FunctionInput functionInput) throws IcdmException {

    return new ParameterLoader(getServiceData()).getFunctionsParamCount(
        functionInput.getFunctionSet().stream().map(String::toUpperCase).collect(Collectors.toSet()));

  }


  /**
   * @param paramName paramName
   * @param funcName funcName
   * @param ruleSetId ruleSetId
   * @return FunctionParamProperties List
   * @throws IcdmException Exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_SEARCH_PARAMETERS)
  @CompressData
  public SortedSet<FunctionParamProperties> getsearchParameters(
      @QueryParam(value = WsCommonConstants.RWS_QP_PARAM_NAME) final String paramName,
      @QueryParam(value = WsCommonConstants.RWS_QP_FUNCTION_NAME) final String funcName,
      @QueryParam(value = WsCommonConstants.RWS_QP_RULESET_ID) final Long ruleSetId)
      throws IcdmException {

    return new ParameterLoader(getServiceData()).getSearchParameters(paramName, funcName, ruleSetId);
  }

  /**
   * @param firstName firstName
   * @param lastName lastName
   * @return ParamIdList
   * @throws IcdmException Exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_VAR_CODED_PARAM_ID)
  @CompressData
  public List<Long> getParamIdList(
      @QueryParam(value = WsCommonConstants.RWS_QP_PARAM_FIRST_NAME) final String firstName,
      @QueryParam(value = WsCommonConstants.RWS_QP_PARAM_LAST_NAME) final String lastName)
      throws IcdmException {

    return new ParameterLoader(getServiceData()).fetchAllVarCodedParam(firstName, lastName);
  }

  /**
   * Get Parameter using its id
   *
   * @param objId object's id
   * @return Rest response, with Parameter object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response get(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    Parameter ret = new ParameterLoader(getServiceData()).getDataObjectByID(objId);
    return Response.ok(ret).build();
  }

  /**
   * /
   *
   * @param paramNames - set of param names from ui
   * @return response
   * @throws IcdmException exception in service
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_INVALID_PARAMETER)
  @CompressData
  public Response getInvalidParameters(final List<String> paramNames) throws IcdmException {
    // Fetch invalid params
    List<String> ret = new ParameterLoader(getServiceData()).getInvalidParams(paramNames);
    getLogger().info("Parameter.getInvalidParameters() completed. Invalid Parameters found = {}", ret.size());
    return Response.ok(ret).build();
  }

}
