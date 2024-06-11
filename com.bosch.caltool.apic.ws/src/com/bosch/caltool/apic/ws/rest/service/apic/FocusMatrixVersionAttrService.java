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
import com.bosch.caltool.icdm.bo.apic.pidc.FocusMatrixVersionAttrCommand;
import com.bosch.caltool.icdm.bo.apic.pidc.FocusMatrixVersionAttrLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.FocusMatrixVersionLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixVersionAttr;


/**
 * Service class for Focus Matrix Version Attribute
 *
 * @author MKL2COB
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_FOCUSMATRIXVERSIONATTR)
public class FocusMatrixVersionAttrService extends AbstractRestService {

  /**
   * Rest web service path for Focus Matrix Version Attribute
   */
  public final static String RWS_FOCUSMATRIXVERSIONATTR = "focusmatrixversionattr";

  /**
   * Get Focus Matrix Version Attribute using its id
   *
   * @param objId object's id
   * @return Rest response, with FocusMatrixVersionAttr object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getById(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    FocusMatrixVersionAttrLoader loader = new FocusMatrixVersionAttrLoader(getServiceData());
    FocusMatrixVersionAttr ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }


  /**
   * Get all Focus Matrix records
   *
   * @param fmVersionId focus matrix version id
   * @return Rest response, with Map of FocusMatrix objects
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_FOCUS_MATRIX_ATTR_FOR_VERSION)
  @CompressData
  public Response getFocusMatrixForVersion(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long fmVersionId)
      throws IcdmException {
    FocusMatrixVersionLoader loader = new FocusMatrixVersionLoader(getServiceData());
    Map<Long, FocusMatrixVersionAttr> retMap = loader.getFocusMatrixAttrForVersion(fmVersionId);
    getLogger().info(" Focus Matrix getAll completed. Total records = {}", retMap.size());
    return Response.ok(retMap).build();
  }

  /**
   * Create a Focus Matrix Version Attribute record
   *
   * @param obj object to create
   * @return Rest response, with created FocusMatrixVersionAttr object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final FocusMatrixVersionAttr obj) throws IcdmException {
    FocusMatrixVersionAttrCommand cmd = new FocusMatrixVersionAttrCommand(getServiceData(), obj, false, false);
    executeCommand(cmd);
    FocusMatrixVersionAttr ret = cmd.getNewData();
    getLogger().info("Created Focus Matrix Version Attribute Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Update a Focus Matrix Version Attribute record
   *
   * @param obj object to update
   * @return Rest response, with updated FocusMatrixVersionAttr object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final FocusMatrixVersionAttr obj) throws IcdmException {
    FocusMatrixVersionAttrCommand cmd = new FocusMatrixVersionAttrCommand(getServiceData(), obj, true, false);
    executeCommand(cmd);
    FocusMatrixVersionAttr ret = cmd.getNewData();
    getLogger().info("Updated Focus Matrix Version Attribute Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Delete a Focus Matrix Version Attribute record
   *
   * @param objId id of object to delete
   * @return Empty Rest response
   * @throws IcdmException exception while invoking service
   */
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response delete(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    FocusMatrixVersionAttrLoader loader = new FocusMatrixVersionAttrLoader(getServiceData());
    FocusMatrixVersionAttr obj = loader.getDataObjectByID(objId);
    FocusMatrixVersionAttrCommand cmd = new FocusMatrixVersionAttrCommand(getServiceData(), obj, false, true);
    executeCommand(cmd);
    return Response.ok().build();
  }

}
