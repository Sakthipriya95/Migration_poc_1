/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.a2l;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.UnmapA2lCommand;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.UnmapA2LDeletionResponse;
import com.bosch.caltool.icdm.model.a2l.UnmapA2LResponse;

/**
 * @author mkl2cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L)
public class UnmapA2LService extends AbstractRestService {

  /**
   * Get related objects for pidc a2l id
   *
   * @param pidcA2lId PIDC A2L id
   * @return Objects that are linked to the pidc A2L
   * @throws IcdmException Error during fetching the data
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_A2L_RELATED_OBJECTS)
  @CompressData
  public Response getByPidc(@QueryParam(WsCommonConstants.RWS_QP_PIDC_A2L_ID) final Long pidcA2lId)
      throws IcdmException {
    UnmapA2LResponse a2lRespModel;
    //GetServiceData() method fetches the service data
    //Also checks if the user has privileges to do the requested operation
    PidcA2lLoader pidcA2lLoader = new PidcA2lLoader(getServiceData());
    a2lRespModel = pidcA2lLoader.getUnmapA2LData(pidcA2lId);
    return Response.ok(a2lRespModel).build();
  }


  /**
   * @param pidcA2lId pidc a2l id to be unmapped
   * @return service response
   * @throws IcdmException Error during deletion of a2l related data
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_DELETE_A2L_RELATED_OBJECTS)
  public Response deleteA2lrelatedEntries(final Long pidcA2lId) throws IcdmException {
    // This method deletes the A2L's entries and unmaps the A2L file for the particular PIDC
    UnmapA2lCommand command = new UnmapA2lCommand(getServiceData(), pidcA2lId);
    executeCommand(command);
    UnmapA2LDeletionResponse delResponse = command.getDelResponse();
    return Response.ok(delResponse).build();
  }
}
