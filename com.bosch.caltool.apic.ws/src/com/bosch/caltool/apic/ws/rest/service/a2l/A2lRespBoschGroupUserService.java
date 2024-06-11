package com.bosch.caltool.apic.ws.rest.service.a2l;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import com.bosch.caltool.icdm.bo.a2l.A2lRespBoschGroupUserCommand;
import com.bosch.caltool.icdm.bo.a2l.A2lRespBoschGroupUserLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lResponsibilityLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.A2lRespBoschGroupUser;


/**
 * Service class for A2L Responsibility Bosch Group User
 *
 * @author PDH2COB
 */
@Path(("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_A2LRESPBOSCHGROUPUSER))
public class A2lRespBoschGroupUserService extends AbstractRestService {


  /**
   * Get A2L Responsibility Bosch Group User using its id
   *
   * @param objId object's id
   * @return Rest response, with A2lRespBoschGroupUser object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response get(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    A2lRespBoschGroupUserLoader loader = new A2lRespBoschGroupUserLoader(getServiceData());
    A2lRespBoschGroupUser ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }


  /**
   * Create a A2L Responsibility Bosch Group User record
   *
   * @param obj object to create
   * @return Rest response, with created A2lRespBoschGroupUser object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final A2lRespBoschGroupUser obj) throws IcdmException {
    A2lRespBoschGroupUserCommand cmd = new A2lRespBoschGroupUserCommand(getServiceData(), obj, false);
    executeCommand(cmd);
    A2lRespBoschGroupUser ret = cmd.getNewData();
    getLogger().info("Created A2L Responsibility Bosch Group User Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * @param respId A2l Responsibility ID
   * @return List of A2lRespBoschGroupUser
   * @throws IcdmException exception from loader
   */
  @GET
  @Path(WsCommonConstants.RWS_A2LRESP_BSHGRP_USER_FOR_RESP)
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getBoschGrpUserForRespId(@QueryParam(WsCommonConstants.RWS_A2L_RESP_ID) final Long respId)
      throws IcdmException {
    new A2lResponsibilityLoader(getServiceData()).validateId(respId);
    Map<Long, A2lRespBoschGroupUser> a2lRespBoschGroupUserList =
        new A2lRespBoschGroupUserLoader(getServiceData()).getBoschGrpRespUsers(respId);
    return Response.ok(a2lRespBoschGroupUserList).build();
  }

  /**
   * Delete a A2L Responsibility Bosch Group User record
   *
   * @param objId id of object to delete
   * @return Empty Rest response
   * @throws IcdmException exception while invoking service
   */
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response delete(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    A2lRespBoschGroupUserLoader loader = new A2lRespBoschGroupUserLoader(getServiceData());
    A2lRespBoschGroupUser obj = loader.getDataObjectByID(objId);
    A2lRespBoschGroupUserCommand cmd = new A2lRespBoschGroupUserCommand(getServiceData(), obj, true);
    executeCommand(cmd);
    return Response.ok().build();
  }

}
