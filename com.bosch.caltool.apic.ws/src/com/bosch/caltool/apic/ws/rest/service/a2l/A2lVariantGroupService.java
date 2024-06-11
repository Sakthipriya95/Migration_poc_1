package com.bosch.caltool.apic.ws.rest.service.a2l;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import com.bosch.caltool.icdm.bo.a2l.A2lVariantGroupCommand;
import com.bosch.caltool.icdm.bo.a2l.A2lVariantGroupLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWpDefnVersionLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.A2LDetailsStructureModel;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;


/**
 * Service class for A2lVariantGroup
 *
 * @author pdh2cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_A2L_VARIANT_GROUP)
public class A2lVariantGroupService extends AbstractRestService {

  /**
   * Get A2lVariantGroup using its id
   *
   * @param objId object's id
   * @return Rest response, with A2lVariantGroup object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response get(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    A2lVariantGroupLoader loader = new A2lVariantGroupLoader(getServiceData());
    A2lVariantGroup ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }


  /**
   * Create a A2lVariantGroup record
   *
   * @param obj object to create
   * @return Rest response, with created A2lVariantGroup object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final A2lVariantGroup obj) throws IcdmException {
    A2lVariantGroupCommand cmd = new A2lVariantGroupCommand(getServiceData(), obj, false, false);
    executeCommand(cmd);
    A2lVariantGroup ret = cmd.getNewData();
    getLogger().info("Created A2lVariantGroup Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Update a A2lVariantGroup record
   *
   * @param obj object to update
   * @return Rest response, with updated A2lVariantGroup object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final A2lVariantGroup obj) throws IcdmException {
    A2lVariantGroupCommand cmd = new A2lVariantGroupCommand(getServiceData(), obj, true, false);
    executeCommand(cmd);
    A2lVariantGroup ret = cmd.getNewData();
    getLogger().info("Updated A2lVariantGroup Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * delete a A2lVariantGroup record
   *
   * @param a2lVarGroupId object id to delete
   * @return Rest response,empty
   * @throws IcdmException exception while invoking service
   */
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response delete(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long a2lVarGroupId)
      throws IcdmException {
    A2lVariantGroupLoader loader = new A2lVariantGroupLoader(getServiceData());
    A2lVariantGroup ret = loader.getDataObjectByID(a2lVarGroupId);
    A2lVariantGroupCommand cmd = new A2lVariantGroupCommand(getServiceData(), ret, false, true);
    executeCommand(cmd);

    getLogger().info("Deleted A2lVariantGroup Id : {}", ret.getId());
    return Response.ok().build();
  }

  /**
   * Get A2l variant group using wp def vers id
   *
   * @param a2lWpDefVerId a2lWpDefVerId
   * @return Rest response, with A2LDetailsStructureModel object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_BY_A2L_WP_DEF_VER_ID)
  @CompressData
  public Response getByWpDefVerId(@QueryParam(WsCommonConstants.RWS_A2L_WP_DEF_VER_ID) final Long a2lWpDefVerId)
      throws IcdmException {

    A2lWpDefnVersionLoader loader = new A2lWpDefnVersionLoader(getServiceData());

    A2LDetailsStructureModel detailsModel = loader.getDetailsModel(a2lWpDefVerId,false);

    return Response.ok(detailsModel).build();

  }

  /**
   * Get A2l variant group using wp def vers id
   *
   * @param a2lWpDefVerId a2lWpDefVerId
   * @return Rest response, with A2LDetailsStructureModel object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_VAR_GRP_A2L_WP_DEF_VER_ID)
  @CompressData
  public Response getVarGrpByWpDefVerId(@QueryParam(WsCommonConstants.RWS_A2L_WP_DEF_VER_ID) final Long a2lWpDefVerId)
      throws IcdmException {
    A2lVariantGroupLoader varGrpLoader = new A2lVariantGroupLoader(getServiceData());
    Map<Long, A2lVariantGroup> a2lVarGrpMap = varGrpLoader.getA2LVarGrps(a2lWpDefVerId);
    return Response.ok(a2lVarGrpMap).build();

  }
}
