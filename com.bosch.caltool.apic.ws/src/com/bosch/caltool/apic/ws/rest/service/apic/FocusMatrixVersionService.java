package com.bosch.caltool.apic.ws.rest.service.apic;

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
import com.bosch.caltool.icdm.bo.apic.pidc.FocusMatrixVersionCommand;
import com.bosch.caltool.icdm.bo.apic.pidc.FocusMatrixVersionLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixVersion;


/**
 * Service class for Focus Matrix Version
 *
 * @author MKL2COB
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_FOCUSMATRIXVERSION)
public class FocusMatrixVersionService extends AbstractRestService {

  /**
   * Rest web service path for Focus Matrix Version
   */
  public final static String RWS_FOCUSMATRIXVERSION = "focusmatrixversion";

  /**
   * Get Focus Matrix Version using its id
   *
   * @param objId object's id
   * @return Rest response, with FocusMatrixVersion object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getById(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    FocusMatrixVersionLoader loader = new FocusMatrixVersionLoader(getServiceData());
    FocusMatrixVersion ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }


  /**
   * Get all Focus Matrix records
   *
   * @param pidcVersionId focus matrix version id
   * @return Rest response, with Map of FocusMatrix objects
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_FOCUS_MATRIX_VERSION_FOR_PIDC)
  @CompressData
  public Response getFocusMatrixForVersion(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long pidcVersionId)
      throws IcdmException {
    PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());
    Map<Long, FocusMatrixVersion> retMap = pidcVersionLoader.getFocusMatrixVersions(pidcVersionId);
    getLogger().info(" Focus Matrix getAll completed. Total records = {}", retMap.size());
    return Response.ok(retMap).build();
  }

  /**
   * Create a Focus Matrix Version record
   *
   * @param obj object to create
   * @return Rest response, with created FocusMatrixVersion object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final FocusMatrixVersion obj) throws IcdmException {
    FocusMatrixVersionCommand cmd = new FocusMatrixVersionCommand(getServiceData(), obj, false, false);
    executeCommand(cmd);
    FocusMatrixVersion ret = cmd.getNewData();
    getLogger().info("Created Focus Matrix Version Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Update a Focus Matrix Version record
   *
   * @param obj object to update
   * @return Rest response, with updated FocusMatrixVersion object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final FocusMatrixVersion obj) throws IcdmException {
    FocusMatrixVersionCommand cmd = new FocusMatrixVersionCommand(getServiceData(), obj, true, false);
    executeCommand(cmd);
    FocusMatrixVersion ret = cmd.getNewData();
    getLogger().info("Updated Focus Matrix Version Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Delete a Focus Matrix Version record
   *
   * @param objId id of object to delete
   * @return Empty Rest response
   * @throws IcdmException exception while invoking service
   */
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response delete(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    FocusMatrixVersionLoader loader = new FocusMatrixVersionLoader(getServiceData());
    FocusMatrixVersion obj = loader.getDataObjectByID(objId);
    FocusMatrixVersionCommand cmd = new FocusMatrixVersionCommand(getServiceData(), obj, false, true);
    executeCommand(cmd);
    return Response.ok().build();
  }

}
