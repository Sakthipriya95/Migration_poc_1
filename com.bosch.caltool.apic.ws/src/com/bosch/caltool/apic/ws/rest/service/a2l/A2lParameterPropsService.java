/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.a2l;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.ParamProperties;


/**
 * Get the parameter properties of an A2L file
 *
 * @author bne4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_PARAM_PROPS)
public class A2lParameterPropsService extends AbstractRestService {

  /**
   * Get the parameter properties of an A2L file.
   *
   * @param a2lFileID A2L File ID
   * @param httpRequest the http request
   * @return Rest response
   * @throws IcdmException the icdm exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response getParamProps(@QueryParam(value = WsCommonConstants.RWS_QP_A2L_FILE_ID) final Long a2lFileID,
      @Context final HttpServletRequest httpRequest) throws IcdmException {

    getLogger().info("A2lParameterPropsService started. User Inputs : a2lfileID = " + a2lFileID);

    // Fetch parameter properties. Only parameters with atleast one property set are retrieved.
    Map<String, ParamProperties> retMap = new ParameterLoader(getServiceData()).fetchAllA2lParamProps(a2lFileID);

    getLogger().info("A2lParameterPropsService completed. Number of parameters = " + retMap.size());

    return Response.ok(retMap).build();
  }
}
