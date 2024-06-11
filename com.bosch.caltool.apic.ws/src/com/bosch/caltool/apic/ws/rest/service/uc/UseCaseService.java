package com.bosch.caltool.apic.ws.rest.service.uc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
import com.bosch.caltool.icdm.bo.uc.UseCaseCommand;
import com.bosch.caltool.icdm.bo.uc.UseCaseLoader;
import com.bosch.caltool.icdm.bo.uc.UsecaseEditorDataLoader;
import com.bosch.caltool.icdm.bo.user.NodeAccessLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.general.DataCreationModel;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UsecaseCreationData;
import com.bosch.caltool.icdm.model.uc.UsecaseEditorModel;
import com.bosch.caltool.icdm.model.uc.UsecaseType;


/**
 * Service class for Usecase
 *
 * @author MKL2COB
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_UC + "/" + WsCommonConstants.RWS_USECASE)
public class UseCaseService extends AbstractRestService {


  /**
   * Get Usecase using its id
   *
   * @param objId object's id
   * @return Rest response, with UseCase object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getById(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    UseCaseLoader loader = new UseCaseLoader(getServiceData());
    UseCase ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }

  /**
   * Get all Usecase records
   *
   * @return Rest response, with Map of UseCase objects
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_ALL)
  @CompressData
  public Response getAll() throws IcdmException {
    UseCaseLoader loader = new UseCaseLoader(getServiceData());
    Map<Long, UseCase> retMap = loader.getAll();
    getLogger().info(" Usecase getAll completed. Total records = {}", retMap.size());
    return Response.ok(retMap).build();
  }

  /**
   * Create a Usecase record
   *
   * @param obj object to create
   * @return Rest response, with created UseCase object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final UsecaseCreationData obj) throws IcdmException {
    UseCaseCommand cmd = new UseCaseCommand(getServiceData(), obj, false, false);
    executeCommand(cmd);
    // DataCreationModel is used as response object so that both UseCase and NodeAccess object can be available in
    // response object
    DataCreationModel<UseCase> ret = new DataCreationModel<>();
    ret.setDataCreated(cmd.getNewData());
    // Use NodeAccessLoader to get the created node access object for the user who has created usecase
    ret.setNodeAccess(new NodeAccessLoader(getServiceData()).getDataObjectByID(cmd.getNodeAccess().getId()));
    getLogger().info("Created Usecase Id : {}", ret.getDataCreated().getId());
    return Response.ok(ret).build();
  }

  /**
   * Update a Usecase record
   *
   * @param obj object to update
   * @return Rest response, with updated UseCase object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final UseCase obj) throws IcdmException {
    UseCaseCommand cmd = new UseCaseCommand(getServiceData(), obj);
    executeCommand(cmd);
    UseCase ret = cmd.getNewData();
    getLogger().info("Updated Usecase Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Update a Usecase record
   *
   * @param objId usecsae id
   * @param upToDate up to date or not up to date
   * @return Rest response, with updated UseCase object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_UPDATE_CONFRM_DATE)
  @CompressData
  public Response changeUpToDate(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId, final Boolean upToDate)
      throws IcdmException {
    UseCaseLoader usecaseLoader = new UseCaseLoader(getServiceData());
    UseCase obj = usecaseLoader.getDataObjectByID(objId);
    UseCaseCommand cmd = new UseCaseCommand(getServiceData(), obj, upToDate);
    executeCommand(cmd);

    UseCase ret = cmd.getNewData();
    getLogger().info("Updated Usecase Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * @param objId Long
   * @return Response with data needed for usecase tree view
   * @throws IcdmException IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_UC_EDITOR_DATA)
  @CompressData
  public Response getUseCaseEditorData(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId)
      throws IcdmException {
    ServiceData serviceData = getServiceData();
    getLogger().info("Fetching use case editor data");
    UsecaseEditorDataLoader ucEditorDataLoader = new UsecaseEditorDataLoader(serviceData);
    UsecaseEditorModel ucEditorModel = ucEditorDataLoader.getUsecaseEditorData(objId);

    // log the number of usecse sections
    getLogger().info("Number of Usecase sections  = {}", ucEditorModel.getUcSectionMap().size());
    return Response.ok(ucEditorModel).build();
  }

  /**
   * @param idSet
   * @return
   * @throws IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_UC_EXPORT_DATA)
  @CompressData
  public Response getUsecaseExportData(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Set<Long> idSet)
      throws IcdmException {
    ServiceData serviceData = getServiceData();
    getLogger().info("Fetching use case export data");
    Map<Long, UsecaseEditorModel> ucModelMap = new HashMap<>();
    UsecaseEditorDataLoader ucEditorDataLoader = new UsecaseEditorDataLoader(serviceData);
    for (Long usecaseId : idSet) {
      UsecaseEditorModel ucEditorModel = ucEditorDataLoader.getUsecaseEditorData(usecaseId);
      ucModelMap.put(usecaseId, ucEditorModel);
    }

    // log the number of usecse models
    getLogger().info("Number of Usecase models  = {}", ucModelMap.size());
    return Response.ok(ucModelMap).build();
  }

  /**
   * @param idSet
   * @return
   * @throws IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_USECASES)
  @CompressData
  public Response getUseCases(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Set<Long> idSet)
      throws IcdmException {
    ServiceData serviceData = getServiceData();
    getLogger().info("Fetching Usecases");
    UseCaseLoader ucLoader = new UseCaseLoader(serviceData);
    Set<UsecaseType> ucTypeSet = ucLoader.getUsecaseTypes(idSet);

    // log the number of usecse
    getLogger().info("Number of Usecase   = {}", ucTypeSet.size());
    return Response.ok(ucTypeSet).build();
  }

  /**
   * @param idSet
   * @return
   * @throws IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_USECASE_WITH_SECTION)
  @CompressData
  public Response getUseCaseWithSectionTree(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Set<Long> idSet)
      throws IcdmException {
    ServiceData serviceData = getServiceData();
    getLogger().info("Fetching Usecases with Section Tree");
    UseCaseLoader ucLoader = new UseCaseLoader(serviceData);
    Set<UsecaseType> ucTypeSet = ucLoader.getUsecaseTypes(idSet);

    // log the number of usecse
    getLogger().info("Number of Usecase with Section Tree = {}", ucTypeSet.size());
    return Response.ok(ucTypeSet).build();
  }
}
