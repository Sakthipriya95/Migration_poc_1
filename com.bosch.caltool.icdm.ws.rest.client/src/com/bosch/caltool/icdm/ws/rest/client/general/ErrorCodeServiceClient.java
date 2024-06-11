/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.general;

import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.general.ErrorCode;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author ukt1cob
 */
public class ErrorCodeServiceClient extends AbstractRestServiceClient {

  /**
   * Constructor.
   */
  public ErrorCodeServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_GEN, WsCommonConstants.RWS_ERRORCODE);
  }

  /**
   * Gets all errorcodes.
   *
   * @return Map of errorcodes -> Key - ErrorCode ID, combination of group name and obj name ,Value - List of errorCodes
   * @throws ApicWebServiceException the apic web service exception
   */
  public Map<String, ErrorCode> getAll() throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL);
    GenericType<Map<String, ErrorCode>> errorcodes = new GenericType<Map<String, ErrorCode>>() {};
    Map<String, ErrorCode> errorCodes = get(wsTarget, errorcodes);

    LOGGER.debug("Error codes returned = {}", errorCodes.size());
    return errorCodes;
  }

  /**
   * Gets errorcode by ID.
   *
   * @param Code - 'GroupName.Name'
   * @return ErrorCode retreived by passing Code
   * @throws ApicWebServiceException the apic web service exception
   */
  public ErrorCode getErrorCodeByID(final String Code) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_ERRORCODE, Code);
    return get(wsTarget, ErrorCode.class);
  }

}
