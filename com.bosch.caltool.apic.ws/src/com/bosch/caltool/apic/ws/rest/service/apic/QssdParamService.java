/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

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
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.QssdParamOutput;

/**
 * @author hnu1cob
 */

@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_QSSD)
public class QssdParamService extends AbstractRestService {

  /**
   * @return Response with Map of QSSD Parameters. Key - parameter name, value - parameter type
   * @throws IcdmException Exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response getQssdParams() throws IcdmException {
    Map<String, String> ssdParamsMap = new ParameterLoader(getServiceData()).getQssdParams();

    QssdParamOutput qssdParamOutput = new QssdParamOutput();
    qssdParamOutput.setQssdParamMap(ssdParamsMap);

    return Response.ok(qssdParamOutput).build();
  }
}
