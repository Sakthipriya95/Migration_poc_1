/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.pidc;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.db.WSObjectStore;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.model.apic.pidc.PIDCVersionReport;


/**
 * Rest service to fetch all pidcs from iCDM
 *
 * @author jvi6cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_PIDC)
public class PIDCService extends AbstractRestService {

  /**
   * Search projects satisfying the given input criteria. Supports invocation using POST method. Service output is
   * compressed.
   *
   * @param pidcVersionID PIDC Version ID
   * @return Rest response
   * @throws UnAuthorizedAccessException
   * @throws DataException
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response searchProject(final long pidcVersionID) throws IcdmException {

    WSObjectStore.getLogger().info("Fetching PIDC Report for PIDCVersion with ID - " + pidcVersionID + " - STARTED");

    PidcLoader pidcLoader = new PidcLoader(getServiceData());
    PIDCVersionReport pidcReport = pidcLoader.fetchPidcVersionInformation(Long.valueOf(pidcVersionID));

    WSObjectStore.getLogger().info("Fetching PIDC Report for PIDCVersion with ID - " + pidcVersionID + " -COMPLETED");
    return Response.ok(pidcReport).build();
  }

}
