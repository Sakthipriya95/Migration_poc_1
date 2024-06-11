package com.bosch.caltool.apic.ws.rest.service.apic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.icdm.bo.apic.pidc.FocusMatrixCommand;
import com.bosch.caltool.icdm.bo.apic.pidc.FocusMatrixLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.FocusMatrixVersionLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader;
import com.bosch.caltool.icdm.bo.uc.UcpAttrLoader;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrix;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixDetailsModel;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixMappingData;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixVersion;


/**
 * Service class for Focus Matrix
 *
 * @author MKL2COB
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_FOCUSMATRIX)
public class FocusMatrixService extends AbstractRestService {

  /**
   * Rest web service path for Focus Matrix
   */
  public static final String RWS_FOCUSMATRIX = "focusmatrix";

  /**
   * Get Focus Matrix using its id
   *
   * @param objId object's id
   * @return Rest response, with FocusMatrix object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getById(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    FocusMatrixLoader loader = new FocusMatrixLoader(getServiceData());
    FocusMatrix ret = loader.getDataObjectByID(objId);
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
  @Path(WsCommonConstants.RWS_GET_FOCUS_MATRIX_FOR_VERSION)
  @CompressData
  public Response getFocusMatrixForVersion(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long fmVersionId)
      throws IcdmException {
    // initialise loaders
    FocusMatrixVersionLoader loader = new FocusMatrixVersionLoader(getServiceData());
    UcpAttrLoader ucpaLoader = new UcpAttrLoader(getServiceData());
    UserLoader userLoader = new UserLoader(getServiceData());

    // set values to the FocusMatrixDetailsModel
    FocusMatrixDetailsModel retObj = new FocusMatrixDetailsModel();
    retObj.setFocusMatrixMap(loader.getFocusMatrixForVersion(fmVersionId));
    retObj.setUcpAttrSet(ucpaLoader.getUCPAForFocusMatrixVersion(fmVersionId));
    FocusMatrixVersion fmVersionObj = loader.getDataObjectByID(fmVersionId);
    if (null != fmVersionObj.getReviewedUser()) {
      // get the reviewed user info
      retObj.setReviewedUser(userLoader.getDataObjectByID(fmVersionObj.getReviewedUser()));
    }
    getLogger().info(" Focus Matrix getAll completed. Total records = {}", retObj.getFocusMatrixMap().size());
    return Response.ok(retObj).build();
  }

  /**
   * Create a Focus Matrix record
   *
   * @param obj object to create
   * @return Rest response, with created FocusMatrix object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final FocusMatrix obj) throws IcdmException {
    FocusMatrixCommand cmd = new FocusMatrixCommand(getServiceData(), obj, false, false);
    executeCommand(cmd);
    FocusMatrix ret = cmd.getNewData();
    getLogger().info("Created Focus Matrix Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * create multiple focus matrix record
   *
   * @param fmList
   * @return Response - Map<Long, FocusMatrix>
   * @throws IcdmException
   */
  @POST
  @Path(WsCommonConstants.RWS_MULTIPLE_CREATE_FOCUS_MATRIX)
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response multipleCreate(final List<FocusMatrix> fmList) throws IcdmException {

    List<AbstractSimpleCommand> cmdList = new ArrayList<>();
    Map<Long, FocusMatrix> ret = new HashMap<>();

    for (FocusMatrix fcsMatrix : fmList) {
      FocusMatrixCommand cmd = new FocusMatrixCommand(getServiceData(), fcsMatrix, false, false);
      cmdList.add(cmd);
    }

    executeCommand(cmdList);

    for (AbstractSimpleCommand cmd : cmdList) {
      FocusMatrixCommand fObj = (FocusMatrixCommand) cmd;
      ret.put(fObj.getNewData().getId(), fObj.getNewData());
    }

    return Response.ok(ret).build();
  }

  /**
   * Update a Focus Matrix record
   *
   * @param obj object to update
   * @return Rest response, with updated FocusMatrix object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final FocusMatrix obj) throws IcdmException {
    FocusMatrixCommand cmd = new FocusMatrixCommand(getServiceData(), obj, true, false);
    executeCommand(cmd);
    FocusMatrix ret = cmd.getNewData();
    getLogger().info("Updated Focus Matrix Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Update Multiple Focus Matrix record
   *
   * @param fmList List of focusMatrix Object
   * @return Response - Map<Long, FocusMatrix>
   * @throws IcdmException
   **/
  @PUT
  @Path(WsCommonConstants.RWS_MULTIPLE_UPDATE_FOCUS_MATRIX)
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response multipleUpdate(final List<FocusMatrix> fmList) throws IcdmException {

    List<AbstractSimpleCommand> cmdList = new ArrayList<>();
    Map<Long, FocusMatrix> ret = new HashMap<>();

    for (FocusMatrix fcsMatrix : fmList) {
      FocusMatrixCommand cmd = new FocusMatrixCommand(getServiceData(), fcsMatrix, true, false);
      cmdList.add(cmd);
    }

    executeCommand(cmdList);

    for (AbstractSimpleCommand cmd : cmdList) {
      FocusMatrixCommand fObj = (FocusMatrixCommand) cmd;
      ret.put(fObj.getNewData().getId(), fObj.getNewData());
    }
    return Response.ok(ret).build();
  }

  /**
   * Delete a Focus Matrix record
   *
   * @param objId id of object to delete
   * @return Empty Rest response
   * @throws IcdmException exception while invoking service
   */
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response delete(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    FocusMatrixLoader loader = new FocusMatrixLoader(getServiceData());
    FocusMatrix obj = loader.getDataObjectByID(objId);
    FocusMatrixCommand cmd = new FocusMatrixCommand(getServiceData(), obj, false, true);
    executeCommand(cmd);
    return Response.ok().build();
  }

  /**
   * @param fmData FocusMatrixMappingData
   * @return true if FM mapping is available while mapping
   * @throws IcdmException
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_IS_FM_WHILE_MAPPING)
  @CompressData
  public Response isFMAvailableWhileMapping(final FocusMatrixMappingData fmData) throws IcdmException {
    FocusMatrixLoader loader = new FocusMatrixLoader(getServiceData());
    boolean isFocusMatrixMappingAvailable = loader.isFMAvailableWhileMapping(fmData);
    return Response.ok(isFocusMatrixMappingAvailable).build();
  }

  /**
   * @param fmData FocusMatrixMappingData
   * @return true if FM mapping is available while mapping
   * @throws IcdmException
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_IS_FM_WHILE_UNMAPPING)
  @CompressData
  public Response isFMAvailableWhileUnMapping(final FocusMatrixMappingData fmData) throws IcdmException {
    FocusMatrixLoader loader = new FocusMatrixLoader(getServiceData());
    boolean isFocusMatrixMappingAvailable = loader.isFocusMatrixAvailableWhileUnMapping(fmData);
    return Response.ok(isFocusMatrixMappingAvailable).build();
  }


  /**
   * Checks if pidc Focus Matrix is empty
   *
   * @param pidcVersId the pidc vers id
   * @return boolean true if Focus Matrix is empty else false
   * @throws IcdmException IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_FOCUSMATRIX_EMPTY)
  @CompressData
  public Response isFocusMatrixEmpty(@QueryParam(value = WsCommonConstants.RWS_PIDC_VERSION_ID) final Long pidcVersId)
      throws IcdmException {

    // checks if there exists a Focus Matris Version other than the working set
    // if multiple fm versions exist the fm tab should be shown so return true
    if (new PidcVersionLoader(getServiceData()).hasMultipleFocusMatrixVersForPidcVers(pidcVersId)) {
      return Response.ok(false).build();
    }

    // checks if any attributes have been assigned to the focus matrix for this PIDC Version ID
    boolean hasAttributesMappedToFocusMatrix =
        new ProjectAttributeLoader(getServiceData()).hasAttributesMappedToFocusMatrixForPidc(pidcVersId);

    return Response.ok(!hasAttributesMappedToFocusMatrix).build();
  }
}
