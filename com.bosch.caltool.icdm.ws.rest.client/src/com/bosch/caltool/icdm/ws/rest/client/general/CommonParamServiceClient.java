/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.general;

import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author gge6cob
 */
public class CommonParamServiceClient extends AbstractRestServiceClient {

  /**
   * Constructor.
   */
  public CommonParamServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_GEN, WsCommonConstants.RWS_COMMON_PARAM);
  }

  /**
   * Gets all common parameters.
   *
   * @return Map ; Key - parameter ID, Value - Parameter value
   * @throws ApicWebServiceException the apic web service exception
   */
  public Map<String, String> getAll() throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL);

    GenericType<Map<String, String>> commonParamMap = new GenericType<Map<String, String>>() {};
    return get(wsTarget, commonParamMap);
  }

  /**
   * @param paramId
   * @return
   * @throws ApicWebServiceException
   */
  public String getParameterValue(final String paramId) throws ApicWebServiceException {
    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_GET_PARAM).queryParam(WsCommonConstants.RWS_QP_PARAM_ID, paramId);

    GenericType<String> commonParamMap = new GenericType<String>() {};
    return get(wsTarget, commonParamMap);
  }
}
