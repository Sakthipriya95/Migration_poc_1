package com.bosch.caltool.apic.ws.rest.service.a2l;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import com.bosch.caltool.apic.ws.rest.service.JsonWriter;
import com.bosch.caltool.dmframework.bo.AbstractCommand.COMMAND_MODE;
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.icdm.bo.a2l.A2lRespBoschGroupUserCommand;
import com.bosch.caltool.icdm.bo.a2l.A2lResponsibilityCommand;
import com.bosch.caltool.icdm.bo.a2l.A2lResponsibilityLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lResponsibilityMergeCommand;
import com.bosch.caltool.icdm.bo.a2l.A2lWpParamMappingLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWpResponsibilityLoader;
import com.bosch.caltool.icdm.bo.cdr.CDRResultParameterLoader;
import com.bosch.caltool.icdm.bo.cdr.RvwWpRespLoader;
import com.bosch.caltool.icdm.bo.cdr.cdfx.CDFxDelWpRespLoader;
import com.bosch.caltool.icdm.bo.cdr.cdfx.CdfxDelvryParamLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireResponseLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.JsonUtil;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.model.a2l.A2lRespMaintenanceData;
import com.bosch.caltool.icdm.model.a2l.A2lRespMergeData;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibilityMergeModel;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibilityModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.RvwWpResp;
import com.bosch.caltool.icdm.model.cdr.cdfx.CDFxDelWpResp;
import com.bosch.caltool.icdm.model.cdr.cdfx.CdfxDelvryParam;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;


/**
 * Service class for A2lResponsibility
 *
 * @author pdh2cob
 */
@Path(("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_A2L_RESPONSIBILITY))
public class A2lResponsibilityService extends AbstractRestService {

  /**
   * server location
   */
  public static final String SERVER_PATH =
      Messages.getString("SERVICE_WORK_DIR") + File.separator + ApicConstants.A2L_RESP_MERGE;

  /**
   * Get A2lResponsibility using its id
   *
   * @param objId object's id
   * @return Rest response, with A2lResponsibility object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response get(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    A2lResponsibilityLoader loader = new A2lResponsibilityLoader(getServiceData());
    A2lResponsibility ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }


  /**
   * Get A2lResponsibility objects for given pidc id
   *
   * @param pidcId - PIDC id
   * @return Rest response, Map -> key - a2lresponsibility id, value A2lResponsibility
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_A2L_RESPONSIBILITY_BY_PIDC)
  @CompressData
  public Response getByPidc(@QueryParam(WsCommonConstants.RWS_QP_PIDC_ID) final Long pidcId) throws IcdmException {
    A2lResponsibilityModel a2lRespModel = new A2lResponsibilityLoader(getServiceData()).getByPidc(pidcId);
    getLogger().info("Responsibility definitions for PIDC {} = {}", pidcId,
        a2lRespModel.getA2lResponsibilityMap().size());
    return Response.ok(a2lRespModel).build();
  }


  /**
   * Create a A2lResponsibility record
   *
   * @param obj object to create
   * @return Rest response, with created A2lResponsibility object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final A2lResponsibility obj) throws IcdmException {
    A2lResponsibilityCommand cmd = new A2lResponsibilityCommand(getServiceData(), obj, false, false);
    executeCommand(cmd);
    A2lResponsibility ret = cmd.getNewData();
    getLogger().info("Created A2lResponsibility Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Create/Update a A2lResponsibility record
   *
   * @param a2lRespMaintenanceData objects to create/delete
   * @return Rest response, with created A2lResponsibility object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Path(WsCommonConstants.RWS_A2L_RESPONSIBILITY_MAINTENANCE)
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response maintainA2lResp(final A2lRespMaintenanceData a2lRespMaintenanceData) throws IcdmException {
    createUpdateA2lRespAndAddToResponse(a2lRespMaintenanceData);
    return Response.ok(a2lRespMaintenanceData).build();
  }

  private void createUpdateA2lRespAndAddToResponse(final A2lRespMaintenanceData a2lRespMaintenanceData)
      throws IcdmException {
    A2lResponsibilityCommand cmd = new A2lResponsibilityCommand(getServiceData(), a2lRespMaintenanceData);
    executeCommand(cmd);

    if (CommonUtils.isEqual(COMMAND_MODE.CREATE, cmd.getCmdMode())) {
      a2lRespMaintenanceData.setA2lRespToCreate(cmd.getNewData());
    }
    else if (CommonUtils.isEqual(COMMAND_MODE.UPDATE, cmd.getCmdMode())) {
      a2lRespMaintenanceData.setA2lRespUpdated(cmd.getNewData());
    }
    updateBshGrpUsrInResponse(a2lRespMaintenanceData, cmd);

  }


  /**
   * @param a2lRespMaintenanceData
   * @param createCmd
   * @throws UnAuthorizedAccessException
   * @throws DataException
   */
  private void updateBshGrpUsrInResponse(final A2lRespMaintenanceData a2lRespMaintenanceData,
      final A2lResponsibilityCommand createCmd)
      throws DataException {
    if (CommonUtils.isNotEmpty(createCmd.getA2lBoschGroupUserCreateCmndList())) {
      a2lRespMaintenanceData.getBoschUsrsCreationList().clear();
      for (A2lRespBoschGroupUserCommand a2lRespBoschGroupUserCommand : createCmd.getA2lBoschGroupUserCreateCmndList()) {
        a2lRespMaintenanceData.getBoschUsrsCreationList().add(a2lRespBoschGroupUserCommand.getNewData());
      }
    }
  }


  /**
   * Update a A2lResponsibility record
   *
   * @param obj object to update
   * @return Rest response, with updated A2lResponsibility object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final List<A2lResponsibility> obj) throws IcdmException {
    List<AbstractSimpleCommand> cmdList = new ArrayList<>();
    Map<Long, A2lResponsibility> ret = new HashMap<>();

    // update multiple objects in a single service call with multiple commands
    for (A2lResponsibility a2lResp : obj) {
      A2lResponsibilityCommand cmd = new A2lResponsibilityCommand(getServiceData(), a2lResp, true, false);
      cmdList.add(cmd);
    }

    executeCommand(cmdList);

    for (AbstractSimpleCommand cmd : cmdList) {
      A2lResponsibilityCommand fObj = (A2lResponsibilityCommand) cmd;
      ret.put(fObj.getNewData().getId(), fObj.getNewData());
    }
    return Response.ok(ret).build();
  }

  /**
   * Merge A2l responsibility
   *
   * @param mergeModel object to create
   * @return Rest response, {@link A2lResponsibilityMergeModel}obj
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Path(WsCommonConstants.RWS_MERGE_A2L_RESPONSIBILITY)
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response mergeResponsibility(final A2lResponsibilityMergeModel mergeModel) throws IcdmException {
    A2lResponsibilityMergeCommand a2lResponsibilityMergeCommand =
        new A2lResponsibilityMergeCommand(getServiceData(), mergeModel);
    executeCommand(a2lResponsibilityMergeCommand);
    updateMergeModel(mergeModel);
    return Response.ok(mergeModel).build();
  }

  /**
   * @param mergeModel
   * @throws UnAuthorizedAccessException
   * @throws DataException
   */
  private void updateMergeModel(final A2lResponsibilityMergeModel mergeModel)
      throws DataException, UnAuthorizedAccessException {

    updA2lWpParamMappingInMergeModel(mergeModel);

    updA2lWpRespInMergeModel(mergeModel);

    updRvwQnaireResponseInMergeModel(mergeModel);

    updCdrResultParamInMergeModel(mergeModel);

    updRvwWpRespInMergeModel(mergeModel);

    updCdfxDlvryParamInMergeModel(mergeModel);

    updCdfxDlvrWpRespInMergeModel(mergeModel);

  }


  /**
   * @param mergeModel
   * @throws DataException
   * @throws UnAuthorizedAccessException
   */
  private void updRvwQnaireResponseInMergeModel(final A2lResponsibilityMergeModel mergeModel)
      throws DataException, UnAuthorizedAccessException {

    Map<Long, RvwQnaireResponse> rvwQnaireResponseUpdate = mergeModel.getRvwQnaireResponseUpdate();

    for (RvwQnaireResponse updatedQnaireResp : mergeModel.getRvwQnaireResponseOld()) {
      rvwQnaireResponseUpdate.put(updatedQnaireResp.getId(),
          new RvwQnaireResponseLoader(getServiceData()).getDataObjectByID(updatedQnaireResp.getId()));
    }

    mergeModel.setRvwQnaireResponseUpdate(rvwQnaireResponseUpdate);
  }


  /**
   * @param mergeModel
   * @throws DataException
   * @throws UnAuthorizedAccessException
   */
  private void updCdrResultParamInMergeModel(final A2lResponsibilityMergeModel mergeModel)
      throws DataException, UnAuthorizedAccessException {

    Map<Long, CDRResultParameter> cdrResultParameterUpdate = mergeModel.getCdrResultParameterUpdate();

    for (CDRResultParameter cdrResultParamEntry : mergeModel.getCdrResultParameterOld()) {
      cdrResultParameterUpdate.put(cdrResultParamEntry.getId(),
          new CDRResultParameterLoader(getServiceData()).getDataObjectByID(cdrResultParamEntry.getId()));
    }

    mergeModel.setCdrResultParameterUpdate(cdrResultParameterUpdate);
  }


  /**
   * @param mergeModel
   * @throws DataException
   * @throws UnAuthorizedAccessException
   */
  private void updRvwWpRespInMergeModel(final A2lResponsibilityMergeModel mergeModel)
      throws DataException, UnAuthorizedAccessException {

    Map<Long, RvwWpResp> rvwWpRespUpdate = mergeModel.getRvwWpRespUpdate();

    for (RvwWpResp rvwWpResp : mergeModel.getRvwWpRespOld()) {
      rvwWpRespUpdate.put(rvwWpResp.getId(),
          new RvwWpRespLoader(getServiceData()).getDataObjectByID(rvwWpResp.getId()));
    }

    mergeModel.setRvwWpRespUpdate(rvwWpRespUpdate);
  }


  /**
   * @param mergeModel
   * @throws DataException
   * @throws UnAuthorizedAccessException
   */
  private void updCdfxDlvryParamInMergeModel(final A2lResponsibilityMergeModel mergeModel)
      throws DataException, UnAuthorizedAccessException {

    Map<Long, CdfxDelvryParam> cdfxDelvryParamUpdate = mergeModel.getCdfxDelvryParamUpdate();

    for (CdfxDelvryParam cdfxDlvrParam : mergeModel.getCdfxDelvryParamOld()) {
      cdfxDelvryParamUpdate.put(cdfxDlvrParam.getId(),
          new CdfxDelvryParamLoader(getServiceData()).getDataObjectByID(cdfxDlvrParam.getId()));
    }

    mergeModel.setCdfxDelvryParamUpdate(cdfxDelvryParamUpdate);
  }


  /**
   * @param mergeModel
   * @throws DataException
   * @throws UnAuthorizedAccessException
   */
  private void updCdfxDlvrWpRespInMergeModel(final A2lResponsibilityMergeModel mergeModel)
      throws DataException, UnAuthorizedAccessException {

    Map<Long, CDFxDelWpResp> cdfxDlvryWpRespUpdate = mergeModel.getCdfxDlvryWpRespUpdate();

    for (CDFxDelWpResp cdfxDelvryWpResp : mergeModel.getCdfxDlvryWpRespOld()) {
      cdfxDlvryWpRespUpdate.put(cdfxDelvryWpResp.getId(),
          new CDFxDelWpRespLoader(getServiceData()).getDataObjectByID(cdfxDelvryWpResp.getId()));
    }

    mergeModel.setCdfxDlvryWpRespUpdate(cdfxDlvryWpRespUpdate);
  }


  /**
   * @param mergeModel
   * @throws DataException
   * @throws UnAuthorizedAccessException
   */
  private void updA2lWpRespInMergeModel(final A2lResponsibilityMergeModel mergeModel)
      throws DataException, UnAuthorizedAccessException {

    Map<Long, A2lWpResponsibility> a2lWpResponsibilityUpdate = mergeModel.getA2lWpResponsibilityUpdate();

    for (A2lWpResponsibility a2lWpResponsibility : mergeModel.getA2lWpResponsibilityOld()) {
      a2lWpResponsibilityUpdate.put(a2lWpResponsibility.getId(),
          new A2lWpResponsibilityLoader(getServiceData()).getDataObjectByID(a2lWpResponsibility.getId()));
    }

    mergeModel.setA2lWpResponsibilityUpdate(a2lWpResponsibilityUpdate);
  }


  /**
   * @param mergeModel
   * @throws DataException
   * @throws UnAuthorizedAccessException
   */
  private void updA2lWpParamMappingInMergeModel(final A2lResponsibilityMergeModel mergeModel)
      throws DataException, UnAuthorizedAccessException {

    Map<Long, A2lWpParamMapping> updatedA2lWpParamMapping = mergeModel.getA2lWpParamMappingUpdate();

    for (A2lWpParamMapping a2lWpParamMapping : mergeModel.getA2lWpParamMappingOld()) {
      updatedA2lWpParamMapping.put(a2lWpParamMapping.getId(),
          new A2lWpParamMappingLoader(getServiceData()).getDataObjectByID(a2lWpParamMapping.getId()));
    }

    mergeModel.setA2lWpParamMappingUpdate(updatedA2lWpParamMapping);
  }


  /**
   * Parse selected qnaire response to JSON
   *
   * @param a2lRespMergeData selected qnaire resp
   * @return execution id
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Path(WsCommonConstants.RWS_PARSE_A2L_RESP_DETAILS_TO_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public String parseSelectedA2lRespStatToJson(final A2lRespMergeData a2lRespMergeData) throws IcdmException {

    String currentDate = new SimpleDateFormat(DateFormat.DATE_FORMAT_18, Locale.getDefault()).format(new Date());
    File file = new File(SERVER_PATH);
    if (!file.exists()) {
      file.mkdir();
    }

    file = new File(file.getAbsoluteFile() + File.separator + currentDate);
    file.mkdir();

    getLogger().debug("Creating JSON report...");
    JsonWriter.createJsonFile(a2lRespMergeData, file.getAbsolutePath(), ApicConstants.A2L_RESP_MERGE_DATA_JSON_NAME);
    getLogger().debug("JSON report created successfully");

    return currentDate;
  }

  /**
   * A2l resp merge data will be stored in any of the 3 server in poduction. Iterate throw all folders and select the
   * file which matches the execution id and parse it back to {@link A2lRespMergeData}
   *
   * @param executionId id to select json stored in server
   * @return {@link A2lRespMergeData}
   * @throws IcdmException Exception in ws call
   */
  @GET
  @Path(WsCommonConstants.RWS_A2L_RESP_MERGE_INPUT_FETCH)
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public A2lRespMergeData fetchA2lRespMergeInputData(
      @QueryParam(WsCommonConstants.RWS_QP_EXECUTION_ID) final String executionId)
      throws IcdmException {

    String jsonPath = new A2lResponsibilityLoader(getServiceData()).fetchA2lRespMergeInputData(executionId);
    return JsonUtil.toModel(new File(jsonPath), A2lRespMergeData.class);
  }

}
