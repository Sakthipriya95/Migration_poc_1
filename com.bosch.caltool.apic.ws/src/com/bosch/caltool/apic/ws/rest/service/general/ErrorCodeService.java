/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.general;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.general.MessageLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.general.ErrorCode;

/**
 * @author ukt1cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_GEN + "/" + WsCommonConstants.RWS_ERRORCODE)
public class ErrorCodeService extends AbstractRestService {

  /**
   * Get all Error Codes
   *
   * @return a Error Codes
   * @throws IcdmException exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ALL)
  @CompressData
  public Response getAll() throws IcdmException {

    MessageLoader loader = new MessageLoader(getServiceData());
    Map<String, ErrorCode> errorCodeMap = loader.getAllErrorCodes();
    return Response.ok(errorCodeMap).build();
  }

  /**
   * Get ErrorCode by passing code as parameter
   *
   * @param code - ErrorCode code
   * @return ErrorCode
   * @throws IcdmException - exception for handling unauthorized access exception thrown by getServiceData() method and
   *           for handling DataNotFoundException thrown by getErrorCodeByID() method.
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response getErrorCodeByID(@QueryParam(value = WsCommonConstants.RWS_QP_ERRORCODE) final String code)
      throws IcdmException {

    MessageLoader loader = new MessageLoader(getServiceData());
    ErrorCode errorCode = loader.getErrorCodeById(code);
    return Response.ok(errorCode).build();
  }

}
