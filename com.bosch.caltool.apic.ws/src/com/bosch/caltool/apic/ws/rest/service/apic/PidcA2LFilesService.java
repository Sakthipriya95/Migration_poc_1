/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.apic.PidcA2LFilesServiceDataLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2LFiles;

/**
 * Rest service to fetch a2l files which have been reviewed
 *
 * @author bru2cob
 */
//Note : This is an external service
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_PIDC_A2L)
public class PidcA2LFilesService extends AbstractRestService {

  /**
   * Get the pidc a2l files
   *
   * @param projectID Project(PIDC) ID
   * @return Rest response
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  @CompressData
  public Response getPidcA2lFilesWithResults(
      @QueryParam(value = WsCommonConstants.RWS_QP_PROJECT_ID) final Long projectID) {

    getLogger().info("Input PIDC ID : {}", projectID);

    PidcA2LFiles ret = new PidcA2LFiles();

    try {
      // fetch the details
      ret = new PidcA2LFilesServiceDataLoader(getServiceData()).getPidcA2lFiles(projectID);
    }
    catch (IcdmException excep) {
      getLogger().error(excep.getMessage(), excep);
    }

    // log the returned result details
    getLogger().info("Data retrieved. PIDC versions = {}; Relevant A2L files = {}; Variants = {}",
        ret.getPidcVersMap().size(), ret.getPidcA2LInfo().size(), ret.getPidcVarsMap().size());
    // return the PidcA2LFiles associated with the Project ID
    return Response.ok(ret).build();
  }

}
