/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.a2l;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.db.WSObjectStore;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.a2l.PTTypeLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.PTType;

/**
 * @author gge6cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_POWER_TRAIN_TYPE)
public class PTTypeService extends AbstractRestService {

  /**
   * Get all power train types
   *
   * @return Rest response of the all Pt types
   * @throws IcdmException error in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ALL)
  @CompressData
  public Response getAllPTtypes() throws IcdmException {
    // Create PT-type loader object
    PTTypeLoader loader = new PTTypeLoader(getServiceData());
    // Fetch PTtypes
    Set<PTType> retSet = loader.getAllPTtypes();
    WSObjectStore.getLogger().info("PowerTrainTypeService.getAllPTtypes() completed. Number of PT-Types = {} ",
        retSet.size());
    // Return the PTType set object
    return Response.ok(retSet).build();
  }
}
