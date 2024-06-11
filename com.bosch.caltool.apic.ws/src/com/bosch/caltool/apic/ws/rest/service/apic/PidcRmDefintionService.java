/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

import java.util.SortedSet;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
import com.bosch.caltool.icdm.bo.rm.PidcRmDefinitionLoader;
import com.bosch.caltool.icdm.bo.rm.PidcRmDefintionCommand;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.rm.PidcRmDefinition;


/**
 * Get Icdm functions which contains the search string
 *
 * @author dja7cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_RISK_DEFINTION)
public class PidcRmDefintionService extends AbstractRestService {


  /**
   * @param pidcVersId version id
   * @return Response with list of functions
   * @throws IcdmException IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_PID_RM_DEF)
  @CompressData
  public Response getPidRmDefintions(@QueryParam(value = WsCommonConstants.RWS_QP_VERS_ID) final Long pidcVersId)
      throws IcdmException {

    ServiceData serviceData = getServiceData();

    PidcRmDefinitionLoader loader = new PidcRmDefinitionLoader(serviceData);

    // Fetch all pidc rm defintions
    SortedSet<PidcRmDefinition> retSet = loader.getPidRmDefintions(pidcVersId);
    WSObjectStore.getLogger().info("Number of Pidc Rm defintions for pidc version " + retSet.size());
    return Response.ok(retSet).build();

  }

  /**
   * Create a FC2WP definition
   *
   * @param def FC2WP Definition details
   * @return Rest response
   * @throws IcdmException error during execution
   */
  @PUT
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_PID_RM_DEF)
  @CompressData
  public Response createPidcRmDefintion(final PidcRmDefinition def) throws IcdmException {
    ServiceData servData = getServiceData();

    // Invoke command
    PidcRmDefintionCommand cmd = new PidcRmDefintionCommand(servData, def);
    executeCommand(cmd);

    PidcRmDefinition ret = cmd.getNewData();

    return Response.ok(ret).build();

  }


  /**
   * Checks if is pid rm relevant.
   *
   * @param pidcVersId the pidc vers id
   * @return Response with list of functions
   * @throws IcdmException IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_IS_PID_RM_EMPTY)
  @CompressData
  public Response isPidcRmEmpty(@QueryParam(value = WsCommonConstants.RWS_QP_VERS_ID) final Long pidcVersId)
      throws IcdmException {
    boolean isRelevant = new PidcRmDefinitionLoader(getServiceData()).isPidcRmEmpty(pidcVersId);
    return Response.ok(isRelevant).build();
  }
}
