/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.a2l;

import java.util.Map;

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
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.a2l.A2lWorkPackageCommand;
import com.bosch.caltool.icdm.bo.a2l.A2lWorkPackageLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;

/**
 * @author nip4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_A2L_WORK_PACKAGE)
public class A2lWorkPackageService extends AbstractRestService {

  /**
   * @param a2lWpToCreate - WP to be created
   * @return - the newly created WP
   * @throws IcdmException - Error during webservice call
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final A2lWorkPackage a2lWpToCreate) throws IcdmException {
    A2lWorkPackageCommand cmd = new A2lWorkPackageCommand(getServiceData(), a2lWpToCreate, false, false);
    executeCommand(cmd);
    A2lWorkPackage ret = cmd.getNewData();
    getLogger().info("Created A2lWorkPackage Id : {}", ret.getId());
    return Response.ok(ret).build();
  }


  /**
   * Get A2lWorkPackageService using its id
   *
   * @param objId Set of A2lWorkPackage ids
   * @return Rest response, with map of A2lWorkPackage object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getById(@QueryParam(WsCommonConstants.A2L_WP_ID) final Long objId) throws IcdmException {
    A2lWorkPackageLoader loader = new A2lWorkPackageLoader(getServiceData());
    A2lWorkPackage ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }

  /**
   * @param a2lWpToUpdate - WP to be updated
   * @return - the updated WP
   * @throws IcdmException - Error during webservice call
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final A2lWorkPackage a2lWpToUpdate) throws IcdmException {
    // Single update command
    A2lWorkPackageCommand cmd = new A2lWorkPackageCommand(getServiceData(), a2lWpToUpdate, true, false);
    executeCommand(cmd);
    A2lWorkPackage ret = cmd.getNewData();
    getLogger().info("Updated A2lWorkPackage Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * @param pidcVersId - pidcversion id
   * @return map of workpackages mapped to the pidcversion
   * @throws IcdmException - error during webservice call
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_WP_BY_PIDC_VERS)
  @CompressData
  public Response getWpByPidcVers(@QueryParam(value = WsCommonConstants.RWS_QP_PIDC_VERS_ID) final Long pidcVersId)
      throws IcdmException {
    Map<Long, A2lWorkPackage> ret = new A2lWorkPackageLoader(getServiceData()).getWpByPidcVers(pidcVersId);
    return Response.ok(ret).build();
  }
}
