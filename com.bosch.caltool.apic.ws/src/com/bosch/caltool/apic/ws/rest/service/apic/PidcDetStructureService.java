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
import com.bosch.caltool.icdm.bo.apic.pidc.PidcDetStructureCommand;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcDetStructureLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDetStructure;


/**
 * Service class for PidcDetailsStructure
 *
 * @author pdh2cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_PIDC_DET_STRUCTURE)
public class PidcDetStructureService extends AbstractRestService {

  /**
   * Rest web service path for PidcDetailsStructure
   */
  public final static String RWS_PIDCDETSTRUCTURE = "pidcdetstructure";

  /**
   * Get PidcDetailsStructure using its id
   *
   * @param objId object's id
   * @return Rest response, with PidcDetStructure object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getById(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    PidcDetStructureLoader loader = new PidcDetStructureLoader(getServiceData());
    PidcDetStructure ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }


  /**
   * Create a PIDC Details Structure record
   *
   * @param obj object to create
   * @return Rest response, with created PidcDetStructure object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final PidcDetStructure obj) throws IcdmException {
    PidcDetStructureCommand cmd = new PidcDetStructureCommand(getServiceData(), obj, false, false);
    executeCommand(cmd);
    PidcDetStructure ret = cmd.getNewData();
    getLogger().info("Created PIDC Details Structure Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Update a PIDC Details Structure record
   *
   * @param obj object to update
   * @return Rest response, with updated PidcDetStructure object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final PidcDetStructure obj) throws IcdmException {
    PidcDetStructureCommand cmd = new PidcDetStructureCommand(getServiceData(), obj, true, false);
    executeCommand(cmd);
    PidcDetStructure ret = cmd.getNewData();
    getLogger().info("Updated PIDC Details Structure Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Delete a PIDC Details Structure record
   *
   * @param objId id of object to delete
   * @return Empty Rest response
   * @throws IcdmException exception while invoking service
   */
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response delete(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    PidcDetStructureLoader loader = new PidcDetStructureLoader(getServiceData());
    PidcDetStructure obj = loader.getDataObjectByID(objId);
    PidcDetStructureCommand cmd = new PidcDetStructureCommand(getServiceData(), obj, false, true);
    executeCommand(cmd);
    return Response.ok().build();
  }

  /**
   * Get all PidcDetailsStructure records
   *
   * @return Rest response, with Map of PidcDetStructure objects
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_DET_STRUCT_FOR_VERSION)
  @CompressData
  public Response getDetStructureForVersion(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long pidcVersionId)
      throws IcdmException {
    PidcDetStructureLoader loader = new PidcDetStructureLoader(getServiceData());
    Map<Long, PidcDetStructure> retMap = loader.getDetStructureForVersion(pidcVersionId);
    getLogger().info(" PidcDetailsStructure getAll completed. Total records = {}", retMap.size());
    return Response.ok(retMap).build();
  }

}
