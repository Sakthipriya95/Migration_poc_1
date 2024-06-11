/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.db.WSObjectStore;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.rm.PidcRmProjCharCommand;
import com.bosch.caltool.icdm.bo.rm.PidcRmProjCharacterLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.rm.PidcRmProjCharacter;
import com.bosch.caltool.icdm.model.rm.PidcRmProjCharacterExt;


/**
 * Get Icdm functions which contains the search string
 *
 * @author dja7cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_RISK_DEFINTION)
public class PidcRmProjCharService extends AbstractRestService {


  /**
   * Create Pidc Rm Proj Character
   *
   * @param projChar pidc rm proj char
   * @return Rest response
   * @throws IcdmException error during execution
   */
  @PUT
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_PID_RM_PROJ_CHAR)
  @CompressData
  public Response createPidcRmProjChar(final PidcRmProjCharacter projChar) throws IcdmException {

    ServiceData servData = getServiceData();
    // Invoke command
    PidcRmProjCharCommand cmd = new PidcRmProjCharCommand(servData, projChar, false);
    executeCommand(cmd);

    PidcRmProjCharacter ret = cmd.getNewData();

    return Response.ok(ret).build();

  }

  /**
   * Update Pidc RmProj Character
   *
   * @param projChar pidc rm proj char
   * @return Rest response
   * @throws IcdmException error during execution
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_PID_RM_PROJ_CHAR_UPDT)
  @CompressData
  public Response updatePidcRmProjCharacter(final PidcRmProjCharacter projChar) throws IcdmException {

    ServiceData servData = getServiceData();
    // Invoke command
    PidcRmProjCharCommand cmd = new PidcRmProjCharCommand(servData, projChar, true);
    executeCommand(cmd);

    PidcRmProjCharacter ret = cmd.getNewData();

    WSObjectStore.getLogger().info("PidcRmProjCharacterService.PidcRmProjCharacter. ID = {}", ret.getId());

    return Response.ok(ret).build();

  }

  /**
   * @param pidcRmId pidc Rm id
   * @return Response with list of functions
   * @throws IcdmException IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_PID_RM_OUTPUT)
  @CompressData
  public Response getPidRmOutputMatrix(@QueryParam(value = WsCommonConstants.RWS_QP_PIDC_RM_ID) final Long pidcRmId)
      throws IcdmException {


    ServiceData serviceData = getServiceData();


    PidcRmProjCharacterLoader loader = new PidcRmProjCharacterLoader(serviceData);

    // Fetch all pidc rm defintions
    Set<PidcRmProjCharacterExt> retSet = loader.getPidRmProjCharExt(pidcRmId);

    return Response.ok(retSet).build();

  }
}
