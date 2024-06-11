/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.a2l.FunctionVersionUnique;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author rgo7cob
 */
public class FunctionVersionServiceClient extends AbstractRestServiceClient {
  

  /**
   */
  public FunctionVersionServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_FUNCTION_VERSIONS);
  }

  /**
   * @param funcName funcName
   * @return
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public Set<FunctionVersionUnique> getAllFuncVersions(final String funcName) throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL_BY_PARENT)
        .queryParam(WsCommonConstants.RWS_QP_NAME, funcName);
    GenericType<Set<FunctionVersionUnique>> type = new GenericType<Set<FunctionVersionUnique>>() {};
    return get(wsTarget, type);
  }

}
