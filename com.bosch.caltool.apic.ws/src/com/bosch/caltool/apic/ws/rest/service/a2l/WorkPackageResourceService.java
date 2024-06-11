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
import com.bosch.caltool.icdm.bo.wp.WPResourceDetailsLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.wp.WPResourceDetails;


/**
 * Services for Work package resources
 *
 * @author dja7cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_WP_RES)
public class WorkPackageResourceService extends AbstractRestService {


  /**
   * Fetch all Work Package Resources
   *
   * @return Rest response of all WPResourceDetails
   * @throws IcdmException if input data is invalid
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ALL_WP_RES)
  @CompressData
  public Response findAll() throws IcdmException {

    WPResourceDetailsLoader loader = new WPResourceDetailsLoader(getServiceData());

    // Fetch all WP resources
    Set<WPResourceDetails> retSet = loader.getAllWpRes();

    WSObjectStore.getLogger().info("WorkPackageResource.findAll() completed. Record count = {}", retSet.size());
    // return the set of all wp resource details
    return Response.ok(retSet).build();

  }
}
