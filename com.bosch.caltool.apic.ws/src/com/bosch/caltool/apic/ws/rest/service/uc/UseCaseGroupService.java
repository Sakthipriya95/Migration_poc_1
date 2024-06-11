package com.bosch.caltool.apic.ws.rest.service.uc;

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
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.uc.UseCaseGroupCommand;
import com.bosch.caltool.icdm.bo.uc.UseCaseGroupLoader;
import com.bosch.caltool.icdm.bo.uc.UsecaseDetailsModelLoader;
import com.bosch.caltool.icdm.bo.uc.UsecaseTreeViewModelLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.uc.UseCaseGroup;
import com.bosch.caltool.icdm.model.uc.UsecaseDetailsModel;
import com.bosch.caltool.icdm.model.uc.UsecaseTreeGroupModel;


/**
 * Service class for Usecase Group
 *
 * @author MKL2COB
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_UC + "/" + WsCommonConstants.RWS_USECASEGROUP)
public class UseCaseGroupService extends AbstractRestService {

  /**
   * @return Response with data needed for usecase tree view
   * @throws IcdmException IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_UC_TREE_DATA)
  @CompressData
  public Response getUseCaseTreeViewData() throws IcdmException {
    ServiceData serviceData = getServiceData();

    UsecaseTreeViewModelLoader ucTreeModelLoader = new UsecaseTreeViewModelLoader(serviceData);
    UsecaseTreeGroupModel ucTreeGrpModel = ucTreeModelLoader.getUsecaseTreeGroupModel();

    // fetch data from DB
    getLogger().info("Number of Usecase groups  = {}", ucTreeGrpModel.getUseCaseGroupMap().size());
    return Response.ok(ucTreeGrpModel).build();
  }

  /**
   * @return Response with data needed for usecase tree view
   * @throws IcdmException IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_UC_DETAILS_DATA)
  @CompressData
  public Response getUseCaseDetailsModel() throws IcdmException {
    ServiceData serviceData = getServiceData();

    UsecaseDetailsModelLoader ucModelLoader = new UsecaseDetailsModelLoader(serviceData);
    UsecaseDetailsModel ucDetailsModel = ucModelLoader.getUsecaseDetailsModel();

    // fetch data from DB
    getLogger().info("Number of Usecase groups  = {}", ucDetailsModel.getUseCaseGroupMap().size());
    return Response.ok(ucDetailsModel).build();
  }

  /**
   * Get Usecase Group using its id
   *
   * @param objId object's id
   * @return Rest response, with UseCaseGroup object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getById(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    UseCaseGroupLoader loader = new UseCaseGroupLoader(getServiceData());
    UseCaseGroup ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }


  /**
   * Create a Usecase Group record
   *
   * @param obj object to create
   * @return Rest response, with created UseCaseGroup object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final UseCaseGroup obj) throws IcdmException {
    UseCaseGroupCommand cmd = new UseCaseGroupCommand(getServiceData(), obj, false);
    executeCommand(cmd);
    UseCaseGroup ret = cmd.getNewData();
    getLogger().info("Created Usecase Group Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Update a Usecase Group record
   *
   * @param obj object to update
   * @return Rest response, with updated UseCaseGroup object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final UseCaseGroup obj) throws IcdmException {
    UseCaseGroupCommand cmd = new UseCaseGroupCommand(getServiceData(), obj, true);
    executeCommand(cmd);
    UseCaseGroup ret = cmd.getNewData();
    getLogger().info("Updated Usecase Group Id : {}", ret.getId());
    return Response.ok(ret).build();
  }
}
