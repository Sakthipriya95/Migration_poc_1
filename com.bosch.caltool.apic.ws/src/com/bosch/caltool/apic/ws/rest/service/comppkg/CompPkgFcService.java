package com.bosch.caltool.apic.ws.rest.service.comppkg;

import java.util.SortedSet;

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
import com.bosch.caltool.icdm.bo.comppkg.CompPkgFcCommand;
import com.bosch.caltool.icdm.bo.comppkg.CompPkgFcLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.comppkg.CompPkgFc;


/**
 * Service class for TCompPkgBcFc
 *
 * @author say8cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_COMP + "/" + WsCommonConstants.RWS_COMP_BC_FC)
public class CompPkgFcService extends AbstractRestService {

  /**
   * Rest web service path for TCompPkgBcFc
   */
  public final static String RWS_COMPPKGBCFC = "comppkgbcfc";
  /**
   * Rest web service path for comp_bc_id
   */
  public final static String RWS_GET_BY_COMP_BC_ID = "bycompbcid";
  /**
   * Rest web service path for comp_bc_id
   */
  public final static String RWS_QP_COMP_BC_ID = "bycompbcid";

  /**
   * Get TCompPkgBcFc using its id
   *
   * @param objId object's id
   * @return Rest response, with CompPkgFc object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getById(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    CompPkgFcLoader loader = new CompPkgFcLoader(getServiceData());
    CompPkgFc ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }

  /**
   * @param compBcId CompPkgBc id
   * @return Rest response
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_TITLE_BY_ID)
  @CompressData
  public Response getPropertyTitle(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long compBcId)
      throws IcdmException {
    CompPkgFcLoader loader = new CompPkgFcLoader(getServiceData());
    String propTitle = loader.getPropertyTitle(compBcId);
    getLogger().info("Component Package Functions property view title={}", propTitle);
    return Response.ok(propTitle).build();
  }

  /**
   * Get TCompPkgBcFc using CompBcId id
   *
   * @param compBcId Comp Bc Id id
   * @return Rest response, with CompPkgFc object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_BY_COMP_BC_ID)
  @CompressData
  public Response getByCompBcId(@QueryParam(WsCommonConstants.RWS_QP_COMP_BC_ID) final Long compBcId)
      throws IcdmException {
    CompPkgFcLoader loader = new CompPkgFcLoader(getServiceData());
    SortedSet<CompPkgFc> retMap = loader.getByCompBcId(compBcId);
    getLogger().info(" TCompPkgBcFc getByCompBcId completed. Total records = {}", retMap.size());
    return Response.ok(retMap).build();
  }


  /**
   * Create a TCompPkgBcFc record
   *
   * @param obj object to create
   * @return Rest response, with created CompPkgFc object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final CompPkgFc obj) throws IcdmException {
    CompPkgFcCommand cmd = new CompPkgFcCommand(getServiceData(), obj, false, false);
    executeCommand(cmd);
    CompPkgFc ret = cmd.getNewData();
    getLogger().info("Created TCompPkgBcFc Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Update a TCompPkgBcFc record
   *
   * @param obj object to update
   * @return Rest response, with updated CompPkgFc object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final CompPkgFc obj) throws IcdmException {
    CompPkgFcCommand cmd = new CompPkgFcCommand(getServiceData(), obj, true, false);
    executeCommand(cmd);
    CompPkgFc ret = cmd.getNewData();
    getLogger().info("Updated TCompPkgBcFc Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Delete a TCompPkgBcFc record
   *
   * @param objId id of object to delete
   * @return Empty Rest response
   * @throws IcdmException exception while invoking service
   */
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response delete(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    CompPkgFcLoader loader = new CompPkgFcLoader(getServiceData());
    CompPkgFc obj = loader.getDataObjectByID(objId);
    CompPkgFcCommand cmd = new CompPkgFcCommand(getServiceData(), obj, false, true);
    executeCommand(cmd);
    return Response.ok().build();
  }

}
