/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.vcdm;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.vcdm.VcdmCalDataLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.vcdm.VcdmCalDataInput;

/**
 * Service to fetch cal data from vCDM
 * 
 * @author dja7cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_VCDM + "/" + WsCommonConstants.RWS_VCDM_CALDATA)
public class VcdmCalDataService extends AbstractRestService {


  /**
   * @param input vcdm cal data input
   * @return byte array of vcdm cal data map
   * @throws IcdmException exception in retrieving vcdm cal data
   */
  @POST
  @Produces({ MediaType.APPLICATION_OCTET_STREAM })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response fetchVcdmCalDataCdfx(final VcdmCalDataInput input) throws IcdmException {
    byte[] ret = new VcdmCalDataLoader(getServiceData()).fetchCalDataCdfx(input);
    return Response.ok(ret).build();
  }

  /**
   * @param input vcdm cal data input
   * @return byte array of vcdm cal data map
   * @throws IcdmException exception in retrieving vcdm cal data
   */
  @POST
  @Path(WsCommonConstants.RWS_SERIALIZED)
  @Produces({ MediaType.APPLICATION_OCTET_STREAM })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response fetchVcdmCalDataSerialized(final VcdmCalDataInput input) throws IcdmException {
    byte[] ret = new VcdmCalDataLoader(getServiceData()).fetchCalDataSerialized(input);
    return Response.ok(ret).build();
  }
}
