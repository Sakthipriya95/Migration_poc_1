package com.bosch.caltool.apic.ws.rest.service.comppkg;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
import com.bosch.caltool.icdm.bo.comppkg.CompPkgBcCommand;
import com.bosch.caltool.icdm.bo.comppkg.CompPkgBcLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.comppkg.CompPkgBc;
import com.bosch.caltool.icdm.model.comppkg.CompPkgData;
import com.bosch.caltool.icdm.model.comppkg.CompPkgFc;


/**
 * Service class for TCompPkgBc
 *
 * @author say8cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_COMP + "/" + WsCommonConstants.RWS_COMPPKGBC)
public class CompPkgBcService extends AbstractRestService {


  /**
   * Get TCompPkgBc using its id
   *
   * @param compPkgId object's id
   * @return Rest response, with CompPkgBc object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_BY_COMP_BC_ID)
  @CompressData
  public Response getBCByCompId(@QueryParam(WsCommonConstants.RWS_QP_COMP_ID) final Long compPkgId)
      throws IcdmException {
    CompPkgBcLoader loader = new CompPkgBcLoader(getServiceData());
    SortedSet<CompPkgBc> ret = loader.getBCByCompId(compPkgId);
    return Response.ok(ret).build();
  }

  /**
   * Get TCompPkgBc using its id
   *
   * @param compPkgId object's id
   * @return Rest response, with CompPkgBc object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_COMP_BC_FC)
  @CompressData
  public Response getCompBcFcByCompId(@QueryParam(WsCommonConstants.RWS_QP_COMP_ID) final Long compPkgId)
      throws IcdmException {
    CompPkgData compPkgData = new CompPkgData();
    Map<Long, Set<CompPkgFc>> fcMap = new HashMap<>();
    CompPkgBcLoader bcLoader = new CompPkgBcLoader(getServiceData());
    Set<CompPkgBc> bcSet = bcLoader.getBCByCompId(compPkgId);
    compPkgData.setBcSet(bcSet);
    for (CompPkgBc compPkgBc : bcSet) {
      fcMap.put(compPkgBc.getId(), compPkgBc.getFcList());
    }
    compPkgData.setFcMap(fcMap);
    return Response.ok(compPkgData).build();
  }


  /**
   * Create a TCompPkgBc record
   *
   * @param obj object to create
   * @return Rest response, with created CompPkgBc object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final CompPkgBc obj) throws IcdmException {
    CompPkgBcCommand cmd = new CompPkgBcCommand(getServiceData(), obj, false, false, false);
    executeCommand(cmd);
    CompPkgBc ret = cmd.getNewData();
    getLogger().info("Created TCompPkgBc Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * @param compPkgBcId object's ID
   * @return Rest response,with CompPkgBc object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getCompPkgBcById(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long compPkgBcId)
      throws IcdmException {
    CompPkgBcLoader bcLoader = new CompPkgBcLoader(getServiceData());
    CompPkgBc compPkgBc = bcLoader.getDataObjectByID(compPkgBcId);
    getLogger().info(" CompPkgBc.getCompPkgBcById = {}", compPkgBc.getBcName());
    return Response.ok(compPkgBc).build();
  }


  /**
   * Update a TCompPkgBc record
   *
   * @param obj object to update
   * @param isUp
   * @return Rest response, with updated CompPkgBc object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final CompPkgBc obj, @QueryParam(WsCommonConstants.RWS_QP_IS_UP) final boolean isUp)
      throws IcdmException {
    CompPkgBcCommand cmd = new CompPkgBcCommand(getServiceData(), obj, true, false, isUp);

    executeCommand(cmd);
    CompPkgBc ret = cmd.getNewData();
    getLogger().info("Updated TCompPkgBc Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Delete a TCompPkgBc record
   *
   * @param objId id of object to delete
   * @return Empty Rest response
   * @throws IcdmException exception while invoking service
   */
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response delete(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    CompPkgBcLoader loader = new CompPkgBcLoader(getServiceData());
    CompPkgBc obj = loader.getDataObjectByID(objId);
    CompPkgBcCommand cmd = new CompPkgBcCommand(getServiceData(), obj, false, true, false);
    executeCommand(cmd);
    return Response.ok().build();
  }

}
