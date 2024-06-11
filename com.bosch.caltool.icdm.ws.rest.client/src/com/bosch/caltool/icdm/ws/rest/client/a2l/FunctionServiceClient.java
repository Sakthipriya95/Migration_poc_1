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
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.FunctionInput;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dja7cob Service client to get functions based on search criteria
 */
public class FunctionServiceClient extends AbstractRestServiceClient {

  /**
   * Service client for WorkPackageDivisionService
   */
  public FunctionServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_FUNCTIONS);
  }

  /**
   * Get all functions which matches the search criteria
   *
   * @param searchString searchString to get functions with the search string as a substing
   * @return Set of all FunctionDetails which matches the search criteria
   * @throws ApicWebServiceException error during service call
   */
  public Set<Function> getSearchFunctions(final String searchString) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_SEARCH_FUNCTIONS)
        .queryParam(WsCommonConstants.RWS_QP_FUNC_SEARCH, searchString);
    GenericType<Set<Function>> type = new GenericType<Set<Function>>() {};
    return get(wsTarget, type);
  }

  /**
   * get All user funcstions
   */
  public SortedSet<Function> getAllUserFunctions() throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL);
    GenericType<SortedSet<Function>> type = new GenericType<SortedSet<Function>>() {};
    return get(wsTarget, type);

  }

  /**
   * @param nameSet nameSet
   * @return the map of function
   */
  public Map<String, Function> getFunctionsByName(final List<String> nameSet) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_FUNCTIONS_BY_NAME);
    GenericType<Map<String, Function>> type = new GenericType<Map<String, Function>>() {};
    return post(wsTarget, nameSet, type);

  }

  /**
   * @param functionSet set of function
   * @param a2lFunctionset set of function in a2l file
   * @return invalid function
   * @throws ApicWebServiceException exception
   */
  public Set<String> getMissingFunctions(final Set<String> functionSet, final Set<String> a2lFunctionset)
      throws ApicWebServiceException {
    FunctionInput functionInput = new FunctionInput();
    functionInput.setFunctionSet(functionSet);
    functionInput.setA2lFunctionset(a2lFunctionset);
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_FUNCTION_MISMATCH);
    GenericType<Set<String>> type = new GenericType<Set<String>>() {};
    return post(wsTarget, functionInput, type);
  }

  /**
   * Get all invalid functions, that is, doesnt exist in db
   *
   * @param functionList functionList from UI
   * @return functionList which doesnt exist in DB
   * @throws ApicWebServiceException error during service call
   */
  public List<String> getInvalidFunctions(final List<String> functionList) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_INVALID_FUNCTIONS);
    GenericType<List<String>> type = new GenericType<List<String>>() {};
    return post(wsTarget, functionList, type);
  }


  /**
   * @param paramName
   * @return
   * @throws ApicWebServiceException
   */
  public SortedSet<Function> getFunctionsByParamName(final String paramName) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_FUNCTIONS_BY_PARAMNAME)
        .queryParam(WsCommonConstants.RWS_QP_PARAM_NAME, paramName);
    GenericType<SortedSet<Function>> type = new GenericType<SortedSet<Function>>() {};
    return get(wsTarget, type);

  }
}
