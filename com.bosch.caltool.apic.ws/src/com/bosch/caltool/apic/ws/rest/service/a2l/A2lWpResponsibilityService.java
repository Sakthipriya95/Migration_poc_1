package com.bosch.caltool.apic.ws.rest.service.a2l;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import com.bosch.caltool.apic.ws.db.WSObjectStore;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.icdm.bo.a2l.A2LWpDefVersionChecker;
import com.bosch.caltool.icdm.bo.a2l.A2lVariantGroupLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWpDefnVersionLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWpResponsibilityCommand;
import com.bosch.caltool.icdm.bo.a2l.A2lWpResponsibilityDeleteCommand;
import com.bosch.caltool.icdm.bo.a2l.A2lWpResponsibilityLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefinitionModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.A2lWpRespResetModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.WpRespLabelResponse;


/**
 * Service class for A2lWpResponsibility
 *
 * @author pdh2cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_A2L_WP_RESPONSIBILITY)
public class A2lWpResponsibilityService extends AbstractRestService {

  /**
   * Get A2lWpResponsibility using its id
   *
   * @param objId object's id
   * @return Rest response, with A2lWpResponsibility object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response get(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    A2lWpResponsibilityLoader loader = new A2lWpResponsibilityLoader(getServiceData());
    A2lWpResponsibility ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }

  /**
   * Get A2lWpDefinitionModel for given wp defn version id
   *
   * @param wpDefnVersId - WP Defn version id
   * @return Rest response, A2lWpDefinitionModel object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_WP_RESP_BY_WP_DEFN_VERS_ID)
  @CompressData
  public Response getA2lWpRespForWpDefnVers(
      @QueryParam(WsCommonConstants.RWS_QP_WP_DEFN_VERS_ID) final Long wpDefnVersId)
      throws IcdmException {
    A2lWpResponsibilityLoader loader = new A2lWpResponsibilityLoader(getServiceData());
    A2lWpDefinitionModel a2lWpDefinitionModel = loader.getWpRespForWpDefnVers(wpDefnVersId);
    getLogger().info("getA2lWpRespForWpDefnVers completed.");
    return Response.ok(a2lWpDefinitionModel).build();
  }


  /**
   * Create a A2lWpResponsibility record
   *
   * @param objList List of objects to create
   * @return Rest response, with created A2lWpResponsibility object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final List<A2lWpResponsibility> objList) throws IcdmException {

    List<AbstractSimpleCommand> WpRespCmdList = new ArrayList<>();
    // Create multiple wp Resp values
    for (A2lWpResponsibility a2lWpResponsibility : objList) {
      A2lWpResponsibilityCommand cmd =
          new A2lWpResponsibilityCommand(getServiceData(), a2lWpResponsibility, false, false);
      WpRespCmdList.add(cmd);
    }
    executeCommand(WpRespCmdList);
    Set<A2lWpResponsibility> createdRespSet = new HashSet<>();
    for (AbstractSimpleCommand cmd : WpRespCmdList) {
      createdRespSet.add(((A2lWpResponsibilityCommand) cmd).getNewData());
    }
    getLogger().info("Created A2lWpResponsibility Id : {}");
    return Response.ok(createdRespSet).build();
  }

  /**
   * Update a A2lWpResponsibility record
   *
   * @param objList object list to update
   * @return Rest response, with updated A2lWpResponsibility object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final List<A2lWpResponsibility> objList) throws IcdmException {
    List<AbstractSimpleCommand> a2lWpRespCmdList = new ArrayList<>();
    // update multiple wp Resp values
    for (A2lWpResponsibility a2lWpResponsibility : objList) {
      A2lWpResponsibilityCommand cmd =
          new A2lWpResponsibilityCommand(getServiceData(), a2lWpResponsibility, true, false);
      a2lWpRespCmdList.add(cmd);
    }
    executeCommand(a2lWpRespCmdList);
    Set<A2lWpResponsibility> updatedWpRespSet = new HashSet<>();
    for (AbstractSimpleCommand cmd : a2lWpRespCmdList) {
      updatedWpRespSet.add(((A2lWpResponsibilityCommand) cmd).getNewData());
    }
    WSObjectStore.getLogger().info("Updating A2lWpResponsibility is completed");
    return Response.ok(updatedWpRespSet).build();
  }


  /**
   * @param a2lWpRespIds - ids to delete
   * @return after update model
   * @throws IcdmException - icdm exception
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_DELETE_A2L_WP_RESPONSIBILITY)
  @CompressData
  public Response deleteA2lWpParamMapping(final Set<Long> a2lWpRespIds) throws IcdmException {
    A2lWpResponsibilityDeleteCommand deleteCommand =
        new A2lWpResponsibilityDeleteCommand(getServiceData(), a2lWpRespIds);
    executeCommand(deleteCommand);
    return Response.ok(deleteCommand.getA2lWpRespDeleteModel()).build();
  }


  /**
   * Get A2lWpResponsibility using its id
   *
   * @param pidcA2lId pidcA2lId
   * @param varId varId
   * @return Rest response, with A2lWpResponsibility list objects for the given Pidc A2l Id and variant id
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Path(WsCommonConstants.RWS_WP_RESP)
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getA2lWpResp(@QueryParam(WsCommonConstants.RWS_QP_PIDC_A2L_ID) final Long pidcA2lId,
      @QueryParam(WsCommonConstants.RWS_QP_VARIANT_ID) final Long varId)
      throws IcdmException {
    // If no wpDefVersion exits create a working set and active version
    A2LWpDefVersionChecker wpDefVerChecker = new A2LWpDefVersionChecker(getServiceData());
    wpDefVerChecker.ensureActiveWpDefVerForA2l(pidcA2lId);
    A2lWpResponsibilityLoader loader = new A2lWpResponsibilityLoader(getServiceData());
    // get the wp Resp for the given Pidc a2l id and var id
    List<WpRespLabelResponse> wpRespLabelList = loader.getWpResp(pidcA2lId, varId);
    return Response.ok(wpRespLabelList).build();
  }


  /**
   * Reset wp params with _DEFAULT_WP
   *
   * @param pidcA2lId pidcA2lId
   * @return A2lWpRespResetModel
   * @throws IcdmException IcdmException
   */
  @POST
  @Path(WsCommonConstants.RWS_RESET_A2L_WP_PARAMS)
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response resetWorkSplit(@QueryParam(WsCommonConstants.RWS_QP_PIDC_A2L_ID) final Long pidcA2lId)
      throws IcdmException {

    A2lWpDefnVersionLoader a2lWpDefnVersionLoader = new A2lWpDefnVersionLoader(getServiceData());

    // Fetch A2L wp definition working set version
    A2lWpDefnVersion workingSetVersion = a2lWpDefnVersionLoader.getWorkingSetVersion(pidcA2lId);

    // A2lWpRespResetModel FOR CNS(Output response model)
    A2lWpRespResetModel a2lWpRespResetModel = new A2lWpRespResetModel();

    // Reset all the wp params with _DEFAULT_WP
    resetA2lWpParams(workingSetVersion, a2lWpRespResetModel);

    a2lWpRespResetModel.setNewA2lWpDefnVersion(a2lWpDefnVersionLoader.getActiveVersion(pidcA2lId));

    return Response.ok(a2lWpRespResetModel).build();
  }

  /**
   * @param workingSetVersion
   * @param a2lWpRespResetModel
   * @throws IcdmException
   */
  private void resetA2lWpParams(final A2lWpDefnVersion workingSetVersion, final A2lWpRespResetModel a2lWpRespResetModel)
      throws IcdmException {

    // Fetch all A2LWPResponsibilty's for working set wp def version id
    Map<Long, A2lWpResponsibility> a2lWpRespMapForWpDefVers =
        new A2lWpResponsibilityLoader(getServiceData()).getA2lWpRespMapForWpDefVers(workingSetVersion.getId());

    Set<Long> a2lWpRespIds = new HashSet<>();
    if (CommonUtils.isNotEmpty(a2lWpRespMapForWpDefVers)) {

      for (Long wpRespId : a2lWpRespMapForWpDefVers.keySet()) {
        a2lWpRespIds.add(wpRespId);
      }
    }

    // Fetch all the variant groups for working set wp definition version id.
    Map<Long, A2lVariantGroup> deleteVarGroupsMap =
        new A2lVariantGroupLoader(getServiceData()).getA2LVarGrps(workingSetVersion.getId());

    List<A2lVariantGroup> deleteVarGroupsList = new ArrayList<>();

    deleteVarGroupsMap.values().forEach(varGroup -> deleteVarGroupsList.add(varGroup));

    // Command to Reset all the wp params with _DEFAULT_WP
    A2lWpResponsibilityDeleteCommand deleteCommand =
        new A2lWpResponsibilityDeleteCommand(getServiceData(), a2lWpRespIds);
    deleteCommand.setResetWorkSplit(true);
    deleteCommand.setWorkingSetVersion(workingSetVersion);
    deleteCommand.setDeleteVarGroups(deleteVarGroupsList);
    executeCommand(deleteCommand);

    // Setting data to output response model
    a2lWpRespResetModel.setDeletedA2lVariantGroup(deleteVarGroupsList);
    a2lWpRespResetModel.setA2lWpRespDeleteModel(deleteCommand.getA2lWpRespDeleteModel());

  }

}

