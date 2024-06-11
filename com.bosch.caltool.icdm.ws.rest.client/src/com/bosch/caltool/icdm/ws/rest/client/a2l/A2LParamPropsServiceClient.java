/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.a2l.ParamProperties;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author gge6cob
 */
public class A2LParamPropsServiceClient extends AbstractRestServiceClient {

  /**
   * Instantiates a new A2L param props service client.
   */
  public A2LParamPropsServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_PARAM_PROPS);
  }

  /**
   * Get the review details of all parameters in the given A2L File
   *
   * @param a2lFileID A2L File ID
   * @return the Map of parameter properties - Key - parameter name; Value - ParamProperties
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  public Map<String, ParamProperties> getA2LParamProps(final Long a2lFileID) throws ApicWebServiceException {

    LOGGER.debug("Loading A2L Parameter properties for a2lFileID = {}", a2lFileID);

    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_A2L_FILE_ID, a2lFileID);

    GenericType<Map<String, ParamProperties>> type = new GenericType<Map<String, ParamProperties>>() {};

    Map<String, ParamProperties> response = get(wsTarget, type);

    LOGGER.debug("A2L Parameter properties loaded. No. of properties : {}", response.size());

    return response;
  }
}
