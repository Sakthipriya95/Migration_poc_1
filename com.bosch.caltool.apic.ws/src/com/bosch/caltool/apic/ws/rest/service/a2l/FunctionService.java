/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.a2l;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.db.WSObjectStore;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.a2l.FunctionLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.FunctionInput;


/**
 * Get Icdm functions which contains the search string
 *
 * @author dja7cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_FUNCTIONS)
public class FunctionService extends AbstractRestService {


  private static final String FUNCTION_FOUND = "Function.getSearchFunctions() completed. Functions found = {}";

  /**
   * @param searchString string tp search functions which contains the search string
   * @return Response with list of functions
   * @throws IcdmException IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_SEARCH_FUNCTIONS)
  @CompressData
  public Response getSearchFunctions(
      @QueryParam(value = WsCommonConstants.RWS_QP_FUNC_SEARCH) final String searchString)
      throws IcdmException {

    FunctionLoader loader = new FunctionLoader(getServiceData());

    // Fetch all functions
    Set<Function> retSet = loader.getSearchFunctions(searchString);

    WSObjectStore.getLogger().info(FUNCTION_FOUND, retSet.size());

    return Response.ok(retSet).build();

  }

  /**
   * @param searchString string tp search functions which contains the search string
   * @return Response with list of functions
   * @throws IcdmException IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ALL)
  @CompressData
  public Response getAllUserFunctions() throws IcdmException {

    FunctionLoader loader = new FunctionLoader(getServiceData());

    // Fetch all functions
    Set<Function> retSet = loader.getSortedFunctions();

    WSObjectStore.getLogger().info(FUNCTION_FOUND, retSet.size());

    return Response.ok(retSet).build();

  }

  /**
   * @param funcNameSet paramName
   * @return Response with list of functions
   * @throws IcdmException IcdmException
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_FUNCTIONS_BY_NAME)
  @CompressData
  public Response getFunctionsByName(final List<String> funcNameSet) throws IcdmException {

    FunctionLoader loader = new FunctionLoader(getServiceData());

    List<String> modifiedList = new ArrayList<>();

    for (String funcName : funcNameSet) {
      modifiedList.add(funcName.toUpperCase(Locale.getDefault()));
    }


    // Fetch all functions
    Map<String, Function> functionMap = loader.getFunctionsByName(modifiedList);

    WSObjectStore.getLogger().info("Function.getFunctionsByName() completed. Functions found = {}", functionMap.size());

    return Response.ok(functionMap).build();

  }

  /**
   * @param functionInput functionInput complex object
   * @return list of invalid fuctions
   * @throws IcdmException IcdmException
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_FUNCTION_MISMATCH)
  @CompressData
  public Set<String> getMissingFunctions(final FunctionInput functionInput) throws IcdmException {
    FunctionLoader loader = new FunctionLoader(getServiceData());
    return loader.getMismatchFunctList(functionInput.getFunctionSet(), functionInput.getA2lFunctionset());
  }

  /**
   * @param searchString string tp search functions which contains the search string
   * @return Response with list of functions
   * @throws IcdmException IcdmException
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_INVALID_FUNCTIONS)
  @CompressData
  public Response getInvalidFunctions(final List<String> funcList) throws IcdmException {

    FunctionLoader loader = new FunctionLoader(getServiceData());

    Map<String, String> funcNameMap = new HashMap<>();

    for (String funcName : funcList) {
      funcNameMap.put(funcName, funcName.toUpperCase(Locale.getDefault()));
    }

    // Fetch all functions
    List<String> retSet = loader.getInvalidFunctions(funcNameMap);

    WSObjectStore.getLogger().info(FUNCTION_FOUND, retSet.size());

    return Response.ok(retSet).build();

  }

  /**
   * @param paramName
   * @return
   * @throws IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_FUNCTIONS_BY_PARAMNAME)
  @CompressData
  public Response getFunctionsByParamName(
      @QueryParam(value = WsCommonConstants.RWS_QP_PARAM_NAME) final String paramName)
      throws IcdmException {
    FunctionLoader loader = new FunctionLoader(getServiceData());
    // Fetch all functions
    Set<Function> functionSet = loader.getFunctionsByparamName(paramName);
    WSObjectStore.getLogger().info("Function.getFunctionsByparamName() completed. Functions found = {}",
        functionSet.size());
    return Response.ok(functionSet).build();
  }

}
