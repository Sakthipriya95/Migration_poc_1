/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.rm.RmMetaDataLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.rm.RmMetaData;


/**
 * get the Rm meta data. All Risks,
 *
 * @author rgo7cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_RISK_DEFINTION)
public class RmMetaDataService extends AbstractRestService {


  /**
   * @return Response with list of functions
   * @throws IcdmException IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_METADATA)
  @CompressData
  public Response getRmMetaData() throws IcdmException {

    ServiceData serviceData = getServiceData();
    // use the meta Data loader
    RmMetaDataLoader metaDataLoader = new RmMetaDataLoader(serviceData);
    // create meta data
    metaDataLoader.createMetaData();
    // retrun the meta data object
    RmMetaData metaData = metaDataLoader.getMetaDataObject();
    return Response.ok(metaData).build();

  }
}
