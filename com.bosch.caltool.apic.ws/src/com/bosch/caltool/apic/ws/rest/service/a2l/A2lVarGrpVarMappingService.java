package com.bosch.caltool.apic.ws.rest.service.a2l;

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
import com.bosch.caltool.icdm.bo.a2l.A2lVarGrpVarMappingCommand;
import com.bosch.caltool.icdm.bo.a2l.A2lVarGrpVariantMappingLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lVarMapUnMapCommand;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.A2lVarGrpMapCmdModel;
import com.bosch.caltool.icdm.model.a2l.A2lVarGrpVariantMapping;


/**
 * Service class for A2lVarGrpVarMapping
 *
 * @author pdh2cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_A2L_VAR_GRP_VAR_MAPPING)
public class A2lVarGrpVarMappingService extends AbstractRestService {

  /**
   * Get A2lVarGrpVarMapping using its id
   *
   * @param objId object's id
   * @return Rest response, with A2lVarGrpVarMapping object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response get(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    A2lVarGrpVariantMappingLoader loader = new A2lVarGrpVariantMappingLoader(getServiceData());
    A2lVarGrpVariantMapping ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }


  /**
   * Create a A2lVarGrpVarMapping record
   *
   * @param obj object to create
   * @return Rest response, with created A2lVarGrpVarMapping object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final A2lVarGrpVariantMapping obj) throws IcdmException {
    A2lVarGrpVarMappingCommand cmd = new A2lVarGrpVarMappingCommand(getServiceData(), obj, false, false);
    executeCommand(cmd);
    A2lVarGrpVariantMapping ret = cmd.getNewData();
    getLogger().info("Created A2lVarGrpVarMapping Id : {}", ret.getId());
    return Response.ok(ret).build();
  }


  /**
   * Update a A2lVarGrpVarMapping record
   *
   * @param a2lVarMappingGroupId object id to delete
   * @return Rest response, with updated A2lVarGrpVarMapping object
   * @throws IcdmException exception while invoking service
   */
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response delete(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long a2lVarMappingGroupId)
      throws IcdmException {
    A2lVarGrpVariantMappingLoader loader = new A2lVarGrpVariantMappingLoader(getServiceData());
    A2lVarGrpVariantMapping ret = loader.getDataObjectByID(a2lVarMappingGroupId);
    A2lVarGrpVarMappingCommand cmd = new A2lVarGrpVarMappingCommand(getServiceData(), ret, false, true);
    executeCommand(cmd);

    getLogger().info("Deleted A2lVarGrpVarMapping Id : {}", ret.getId());
    return Response.ok().build();
  }

  /**
   * Get all A2lVarGrpVarMapping records
   *
   * @return Rest response, with Map of A2lVarGrpVarMapping objects
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_MAP_UNMAP)
  @CompressData
  public Response mapUnMap(final A2lVarGrpMapCmdModel a2lVarCmdModel) throws IcdmException {

    A2lVarMapUnMapCommand command = new A2lVarMapUnMapCommand(getServiceData(), a2lVarCmdModel);
    executeCommand(command);

    return Response.ok(a2lVarCmdModel).build();
  }
}
