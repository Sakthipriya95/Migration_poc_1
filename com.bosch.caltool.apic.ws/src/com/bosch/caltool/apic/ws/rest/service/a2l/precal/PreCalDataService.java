/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.a2l.precal;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.a2l.precal.PreCalDataLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.precal.PreCalAttrValResponse;
import com.bosch.caltool.icdm.model.a2l.precal.PreCalData;
import com.bosch.caltool.icdm.model.a2l.precal.PreCalInputData;


/**
 * @author bne4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_PRECAL)
public class PreCalDataService extends AbstractRestService {

  /**
   * Get pre calibration data for the given input
   *
   * @param input PreCalInputData
   * @return pre-calibration data
   * @throws IcdmException any error while fetching data
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response getPreCalData(final PreCalInputData input) throws IcdmException {
    PreCalData preCalData = new PreCalDataLoader(getServiceData()).getPreCalData(input);
    getLogger().info("PreCalDataService.getPreCalData() completed. Number of parameters with data = {}",
        preCalData.getPreCalDataMap().size());
    return Response.ok(preCalData).build();
  }

  /**
   * Get the attributes and values to be used to find the pre-cal data for the given project
   *
   * @param input PreCalInputData
   * @return pre-calibration data
   * @throws IcdmException any error while fetching data
   */
  @POST
  @Path(WsCommonConstants.RWS_PRECAL_ATTR_VAL)
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response getPreCalAttrVals(final PreCalInputData input) throws IcdmException {
    PreCalAttrValResponse preCalAttrValResponse = new PreCalDataLoader(getServiceData()).getPreCalAttrVals(input);
    getLogger().info("PreCalDataService.getPreCalAttrVals() completed. Number of dependant attributes = {}",
        preCalAttrValResponse.getAttrMap().size());
    // return the preCalAttrValResponse.
    return Response.ok(preCalAttrValResponse).build();
  }

}

