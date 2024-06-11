package com.bosch.caltool.apic.ws.rest.service.a2l;

import java.util.ArrayList;
import java.util.HashMap;
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
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.CompletionCallback;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ManagedAsync;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.db.WSObjectStore;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.icdm.bo.a2l.A2lWpDefaultDefinitionCommand;
import com.bosch.caltool.icdm.bo.a2l.A2lWpDefinitionActiveVersionCommand;
import com.bosch.caltool.icdm.bo.a2l.A2lWpDefinitionVersionCommand;
import com.bosch.caltool.icdm.bo.a2l.A2lWpDefnVersionLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWpResponsibilityLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWpResponsibilityStatusLoader;
import com.bosch.caltool.icdm.bo.a2l.CopyPar2WpFromA2lCommand;
import com.bosch.caltool.icdm.bo.a2l.PidcA2LPar2WPCommand;
import com.bosch.caltool.icdm.bo.a2l.WpImportFromFuncCommand;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.A2lWpRespStatusUpdationModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibilityStatus;
import com.bosch.caltool.icdm.model.a2l.CopyA2lWpRespResponse;
import com.bosch.caltool.icdm.model.a2l.CopyPar2WpFromA2lInput;
import com.bosch.caltool.icdm.model.a2l.WpImportFromFuncInput;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcTakeOverA2lWrapper;


/**
 * Service class for A2lWpDefinitionVersion
 *
 * @author pdh2cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_A2L_WP_DEFINITION_VERSION)
public class A2lWpDefinitionVersionService extends AbstractRestService {


  /**
   * Get A2lWpDefinitionVersion using its id
   *
   * @param objId object's id
   * @return Rest response, with A2lWpDefinitionVersion object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response get(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    A2lWpDefnVersionLoader loader = new A2lWpDefnVersionLoader(getServiceData());
    A2lWpDefnVersion ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }


  /**
   * Get A2lWpDefinitionVersion objects using pidc a2l id
   *
   * @param pidcA2lId - pidc a2l id
   * @return Rest response, Map of A2lWpDefinitionVersion
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_WP_DEFN_VERS_BY_PIDC_A2L_ID)
  @CompressData
  public Response getWPDefnVersForPidcA2l(@QueryParam(WsCommonConstants.RWS_QP_PIDC_A2L_ID) final Long pidcA2lId)
      throws IcdmException {
    A2lWpDefnVersionLoader loader = new A2lWpDefnVersionLoader(getServiceData());
    Map<Long, A2lWpDefnVersion> retMap = loader.getWPDefnVersionsForPidcA2lId(pidcA2lId);
    return Response.ok(retMap).build();
  }

  /**
   * @param pidcA2lId pidc a2l id
   * @return boolean flg - whether pidc a2l has active wp defn version
   * @throws IcdmException Exception in fetching active wp defn version
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_ACTIVE_WP_DEFN_VERS_BY_PIDC_A2L_ID)
  @CompressData
  public Response hasActiveWPDefnVersForPidcA2l(@QueryParam(WsCommonConstants.RWS_QP_PIDC_A2L_ID) final Long pidcA2lId)
      throws IcdmException {
    A2lWpDefnVersionLoader loader = new A2lWpDefnVersionLoader(getServiceData());
    Map<Long, A2lWpDefnVersion> retMap = loader.getWPDefnVersionsForPidcA2lId(pidcA2lId);
    boolean hasActiveVers = false;
    for (A2lWpDefnVersion wpDefVers : retMap.values()) {
      if (wpDefVers.isActive()) {
        hasActiveVers = true;
        break;
      }
    }
    return Response.ok(hasActiveVers).build();
  }


  /**
   * Create a A2lWpDefinitionVersion record
   *
   * @param a2lWpDefnVersion object to create
   * @return Rest response, with created A2lWpDefinitionVersion object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final A2lWpDefnVersion a2lWpDefnVersion) throws IcdmException {
    A2lWpDefnVersion ret = null;
    if (a2lWpDefnVersion.isWorkingSet()) {
      A2lWpDefaultDefinitionCommand cmd =
          new A2lWpDefaultDefinitionCommand(getServiceData(), a2lWpDefnVersion, false, true);
      executeCommand(cmd);
      ret = cmd.getNewData();
      getLogger().info("Working Set & DEFAULT Workpackage definition created. A2lWpDefinitionVersion Id : {}",
          ret.getId());
    }
    else {
      A2lWpDefinitionActiveVersionCommand activeVersCmd =
          new A2lWpDefinitionActiveVersionCommand(getServiceData(), a2lWpDefnVersion);
      executeCommand(activeVersCmd);
      ret = activeVersCmd.getNewData();
      getLogger().info("Created A2lWpDefinitionVersion Id : {}", ret.getId());
    }

    return Response.ok(ret).build();
  }

  /**
   * Update a list of A2lWpDefinitionVersion record
   *
   * @param wpDefnVersionList A2lWpDefinitionVersion list to update
   * @return Rest response, with updated A2lWpDefinitionVersion object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final List<A2lWpDefnVersion> wpDefnVersionList) throws IcdmException {
    List<AbstractSimpleCommand> wpDefnVersionCmdList = new ArrayList<>();
    for (A2lWpDefnVersion a2lWpDefinitionVersion : wpDefnVersionList) {
      A2lWpDefinitionVersionCommand cmd =
          new A2lWpDefinitionVersionCommand(getServiceData(), a2lWpDefinitionVersion, true, false, false);
      wpDefnVersionCmdList.add(cmd);
    }
    executeCommand(wpDefnVersionCmdList);
    Set<A2lWpDefnVersion> updatedWpDefnSet = new HashSet<>();
    for (AbstractSimpleCommand cmd : wpDefnVersionCmdList) {
      updatedWpDefnSet.add(((A2lWpDefinitionVersionCommand) cmd).getNewData());
    }
    getLogger().info("Updated A2lWpDefinitionVersion list");
    return Response.ok(updatedWpDefnSet).build();
  }

  /**
   * Copy Par2wp mapping from one a2l to another.
   *
   * @param inputDataList the input
   * @return void
   * @throws IcdmException the icdm exception
   */
  @POST
  @Path(WsCommonConstants.RWS_COPY_PAR2WP)
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response importA2lWpResp(final List<CopyPar2WpFromA2lInput> inputDataList) throws IcdmException {
    CopyA2lWpRespResponse response = new CopyA2lWpRespResponse();
    List<AbstractSimpleCommand> copyPar2WpCmdList = new ArrayList<>();
    for (CopyPar2WpFromA2lInput inputData : inputDataList) {
      CopyPar2WpFromA2lCommand copyCmd = new CopyPar2WpFromA2lCommand(getServiceData(), inputData);
      copyPar2WpCmdList.add(copyCmd);
    }

    if (!copyPar2WpCmdList.isEmpty()) {
      executeCommand(copyPar2WpCmdList);
      setResponse(response, copyPar2WpCmdList);
      PidcA2lLoader pidcLoader = new PidcA2lLoader(getServiceData());
      for (CopyPar2WpFromA2lInput inputData : inputDataList) {
        response.getPidcA2lSet().add(pidcLoader.getDataObjectByID(inputData.getDescPidcA2lId()));
      }
      WSObjectStore.getLogger().info("Copy of A2lWpResponsibility is completed");
    }
    return Response.ok(response).build();
  }


  /**
   * @param response
   * @param copyPar2WpCmdList
   * @throws DataException
   */
  private void setResponse(final CopyA2lWpRespResponse response, final List<AbstractSimpleCommand> copyPar2WpCmdList)
      throws DataException {
    for (AbstractSimpleCommand copyCmd : copyPar2WpCmdList) {
      for (AbstractSimpleCommand newCmd : ((CopyPar2WpFromA2lCommand) copyCmd).getA2lWpDefCommandSet()) {
        if (newCmd instanceof A2lWpDefinitionVersionCommand) {
          response.getWpDefSet().add(((A2lWpDefinitionVersionCommand) newCmd).getNewData());
        }
        if (newCmd instanceof A2lWpDefaultDefinitionCommand) {
          response.getWpDefSet().add(((A2lWpDefaultDefinitionCommand) newCmd).getNewData());
        }
      }
    }

  }

  /**
   * @param pidcTakeOverA2lWrapper as input
   * @return CopyA2lWpRespResponse
   * @throws IcdmException Exception
   */
  @POST
  @Path(WsCommonConstants.RWS_TAKE_OVER_A2L)
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response createPidcA2landTakeOverFromA2l(final PidcTakeOverA2lWrapper pidcTakeOverA2lWrapper)
      throws IcdmException {

    PidcA2LPar2WPCommand pidcA2lPar2WPcommand = new PidcA2LPar2WPCommand(getServiceData(), pidcTakeOverA2lWrapper);
    executeCommand(pidcA2lPar2WPcommand);

    CopyA2lWpRespResponse response = new CopyA2lWpRespResponse();
    response.setPidcA2lSet(pidcA2lPar2WPcommand.getPidcA2lSet());
    List<AbstractSimpleCommand> copyPar2WpCmdList = pidcA2lPar2WPcommand.getCopyPar2WpCmdList();
    if (!copyPar2WpCmdList.isEmpty()) {
      setResponse(response, copyPar2WpCmdList);
      getLogger().info("Create PidcA2l and Copy of A2lWpResponsibility is completed");
    }
    return Response.ok(response).build();
  }

  /**
   * @param pidcA2lId pidc A2l Id
   * @return isDefaultWpRespLabelAssignmentExist
   * @throws IcdmException exception in ws
   */
  @GET
  @Path(WsCommonConstants.RWS_DEFAULT_WP_RESP_ASSIGN_EXIST)
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response isDefaultWpRespLabelAssignmentExist(
      @QueryParam(WsCommonConstants.RWS_QP_PIDC_A2L_ID) final Long pidcA2lId)
      throws IcdmException {
    boolean isDefaultWpRespLabelAssignmentExist =
        new A2lWpDefnVersionLoader(getServiceData()).isDefaultWpRespLabelAssignmentExist(pidcA2lId);
    return Response.ok(isDefaultWpRespLabelAssignmentExist).build();
  }

  /**
   * @param inputData WpImportFromFuncInput
   * @return CopyA2lWpRespResponse
   * @throws IcdmException Exception
   */
  @POST
  @Path(WsCommonConstants.RWS_WP_IMPORT_FROM_FUNC)
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response importWpFromFunctions(final WpImportFromFuncInput inputData) throws IcdmException {

    WpImportFromFuncCommand wpImportFromFuncCmd = new WpImportFromFuncCommand(getServiceData(), inputData);
    executeCommand(wpImportFromFuncCmd);

    CopyA2lWpRespResponse response = new CopyA2lWpRespResponse();
    PidcA2l pidcA2L = new PidcA2lLoader(getServiceData()).getDataObjectByID(wpImportFromFuncCmd.getPidcA2lId());
    response.getPidcA2lSet().add(pidcA2L);

    for (AbstractSimpleCommand cmd : wpImportFromFuncCmd.getA2lWpDefCommandSet()) {
      response.getWpDefSet().add(((A2lWpDefinitionVersionCommand) cmd).getNewData());
    }
    getLogger().info("Create Work Packages from Functions is completed");
    return Response.ok(response).build();
  }

  /**
   * @param asyncResponse Response type
   * @param prevWpDefActiveVersId prevWpDefActiveVersId
   * @param newWpDefActiveVersId newWpDefActiveVersId
   * @throws IcdmException Exception
   */
  @GET
  @Path(WsCommonConstants.RWS_UPDATE_WP_STATUS_FOR_WP_DEF_VER)
  @Produces(MediaType.APPLICATION_JSON)
  @ManagedAsync
  public void updateWpFinishedStatus(@Suspended final AsyncResponse asyncResponse,
      @QueryParam(WsCommonConstants.RWS_PREVIOUS_WP_DEF_ACTIVE_VERS_ID) final Long prevWpDefActiveVersId,
      @QueryParam(WsCommonConstants.RWS_NEW_WP_DEF_ACTIVE_VERS_ID) final Long newWpDefActiveVersId)
      throws IcdmException {

    asyncResponse.register(new CompletionCallback() {

      @Override
      public void onComplete(final Throwable throwable) {
        if (throwable != null) {
          getLogger().error(throwable.getMessage(), throwable);
        }
      }
    });

    A2lWpDefnVersion newWpDefActiveVers =
        new A2lWpDefnVersionLoader(getServiceData()).getDataObjectByID(newWpDefActiveVersId);

    A2lWpDefinitionVersionCommand cmd =
        new A2lWpDefinitionVersionCommand(getServiceData(), prevWpDefActiveVersId, newWpDefActiveVers);
    executeCommand(cmd);
    Map<Long, A2lWpResponsibilityStatus> a2lWpRespStatusMapAfterUpdate = new HashMap<>();
    Map<Long, A2lWpResponsibility> a2lWpRespMapForWpDefVersAfterUpdate =
        new A2lWpResponsibilityLoader(getServiceData()).getA2lWpRespMapForWpDefVers(newWpDefActiveVersId);

    if (CommonUtils.isNotEmpty(a2lWpRespMapForWpDefVersAfterUpdate)) {
      for (Long wpRespId : a2lWpRespMapForWpDefVersAfterUpdate.keySet()) {

        Set<A2lWpResponsibilityStatus> a2lWpRespStatusForA2lResp =
            new A2lWpResponsibilityStatusLoader(getServiceData()).getA2lWpRespStatusBasedOnWPRespId(wpRespId);

        for (A2lWpResponsibilityStatus a2lWpResponsibilityStatus : a2lWpRespStatusForA2lResp) {
          a2lWpRespStatusMapAfterUpdate.put(a2lWpResponsibilityStatus.getId(), a2lWpResponsibilityStatus);
        }
      }
    }
    // listOfNewlyCreatedA2lWpRespStatus has not been set to the A2lWpRespStatusUpdationModel
    // as that data was not necessary for CNS refresh.
    A2lWpRespStatusUpdationModel a2lWpRespStatusUpdateModel = new A2lWpRespStatusUpdationModel();
    a2lWpRespStatusUpdateModel.setA2lWpRespStatusMapAfterUpdate(a2lWpRespStatusMapAfterUpdate);
    asyncResponse.resume(a2lWpRespStatusUpdateModel);

  }
}
