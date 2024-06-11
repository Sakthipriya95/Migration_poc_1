/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcScout;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSearchInput;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSearchResponse;


/**
 * Rest service for PIDC Search(Scout).
 * <p>
 * Service path ../rest/apic/pidcscout
 *
 * @author bne4cob
 */
// ICDM-2326
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_PIDC_SCOUT)
public class PidcScoutService extends AbstractRestService {


  /**
   * Search projects satisfying the given input criteria.
   *
   * @param searchInput search conditions
   * @return Rest response
   * @throws IcdmException error during execution
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response searchProjects(final PidcSearchInput searchInput) throws IcdmException {
    // PIDC Search
    PidcSearchResponse scResp = new PidcScout(getServiceData(), searchInput).findProjects();

    return Response.ok(scResp).build();
  }

}
