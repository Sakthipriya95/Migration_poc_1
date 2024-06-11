package com.bosch.caltool.apic.ws.rest.service.uc;

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
import com.bosch.caltool.icdm.bo.uc.UseCaseSectionCommand;
import com.bosch.caltool.icdm.bo.uc.UseCaseSectionLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.uc.UseCaseSection;
import com.bosch.caltool.icdm.model.uc.UseCaseSectionResponse;


/**
 * Service class for Usecase Section
 *
 * @author MKL2COB
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_UC + "/" + WsCommonConstants.RWS_USECASESECTION)
public class UseCaseSectionService extends AbstractRestService {


  /**
   * Get Usecase Section using its id
   *
   * @param objId object's id
   * @return Rest response, with UseCaseSection object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getById(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    UseCaseSectionLoader loader = new UseCaseSectionLoader(getServiceData());
    UseCaseSection ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }

  /**
   * Get all Usecase Section records
   *
   * @return Rest response, with Map of UseCaseSection objects
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_ALL)
  @CompressData
  public Response getAll() throws IcdmException {
    UseCaseSectionLoader loader = new UseCaseSectionLoader(getServiceData());
    Map<Long, UseCaseSection> retMap = loader.getAll();
    getLogger().info(" Usecase Section getAll completed. Total records = {}", retMap.size());
    return Response.ok(retMap).build();
  }

  /**
   * Create a Usecase Section record
   *
   * @param obj object to create
   * @return Rest response, with created UseCaseSection object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final UseCaseSection obj) throws IcdmException {
    UseCaseSectionResponse response = new UseCaseSectionResponse();
    UseCaseSectionCommand cmd = new UseCaseSectionCommand(getServiceData(), obj, false);
    executeCommand(cmd);
    UseCaseSectionLoader useCaseSectionLoader = new UseCaseSectionLoader(getServiceData());
    for (Long ucSecId : cmd.getUcSectionIdSet()) {
      response.getUcSectionSet().add(useCaseSectionLoader.getDataObjectByID(ucSecId));
    }
    return Response.ok(response).build();
  }


  /**
   * Update a Usecase Section record
   *
   * @param obj object to update
   * @return Rest response, with updated UseCaseSection object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final UseCaseSection obj) throws IcdmException {
    UseCaseSectionCommand cmd = new UseCaseSectionCommand(getServiceData(), obj, true);
    executeCommand(cmd);
    UseCaseSection ret = cmd.getNewData();
    getLogger().info("Updated Usecase Section Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

}
