/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cda;

import java.util.List;
import java.util.stream.Collectors;

import com.bosch.caltool.icdm.model.cda.FunctionFilter;
import com.bosch.caltool.icdm.model.cda.ParameterFilterLabel;
import com.bosch.caltool.icdm.model.cda.SystemConstantFilter;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lSysconstServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.FunctionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.ParameterServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author pdh2cob
 */
public class CDAFilterValidationBo {

  /**
   * @param paramLabels input params to verify
   * @return invalid param names
   * @throws ApicWebServiceException service error
   */
  public List<String> validateParameters(final List<ParameterFilterLabel> paramLabels) throws ApicWebServiceException {
    List<String> paramNames = paramLabels.stream().map(p -> p.getLabel().trim()).collect(Collectors.toList());
    return new ParameterServiceClient().getInvalidParameters(paramNames);
  }


  /**
   * @param funFilters function filers from UI
   * @return list of function names which doesnt exist in DB
   * @throws ApicWebServiceException service error
   */
  public List<String> validateFunctions(final List<FunctionFilter> funFilters) throws ApicWebServiceException {
    List<String> functionNames = funFilters.stream().map(p -> p.getFunctionName().trim()).collect(Collectors.toList());
    return new FunctionServiceClient().getInvalidFunctions(functionNames);
  }

  /**
   * @param sysconFilters Filters from UI
   * @return list of function names which doesnt exist in DB
   * @throws ApicWebServiceException service error
   */
  public List<String> validateSystemConstants(final List<SystemConstantFilter> sysconFilters)
      throws ApicWebServiceException {

    List<String> sysconNames =
        sysconFilters.stream().map(p -> p.getSystemConstantName().trim()).collect(Collectors.toList());
    return new A2lSysconstServiceClient().getInvalidSystemConstants(sysconNames);
  }

}
