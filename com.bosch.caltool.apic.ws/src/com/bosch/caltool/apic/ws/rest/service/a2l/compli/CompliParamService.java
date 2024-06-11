/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.a2l.compli;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.model.a2l.CompliParamOutput;


/**
 * Rest service to fetch a2l files which have been reviewed
 *
 * @author rgo7cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_CONTEXT_COMPLI)
public class CompliParamService extends AbstractRestService { 


  /**
   * method to fetch the compliance parameters

   * @return Map of compliance parameters. Key - parameter name, value - parameter type
   * @throws UnAuthorizedAccessException error while calling service
   **/
  
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response getCompliParams() throws UnAuthorizedAccessException {
   
   //Map of compliance parameters
    Map<String, String> compliParamPtTypeMap = new ParameterLoader(getServiceData()).getCompliParamWithType();
    CompliParamOutput compliOutput = new CompliParamOutput();
    //Set the map to  compliOutput
    compliOutput.getCompliParamMap().putAll(compliParamPtTypeMap);
    getLogger().info("Number of compli params  = {}", compliOutput.getCompliParamMap().size());
    // build the response object
    return Response.ok(compliOutput).build();
  }
}
