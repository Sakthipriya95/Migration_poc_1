/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;

import java.util.List;
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
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireRespUpdationCommand;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireResponseCommand;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireResponseLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.pidc.PidcQnaireInfo;
import com.bosch.caltool.icdm.model.cdr.CdrReportQnaireRespWrapper;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespDetails;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespUpdationModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuesRespDeletionOutput;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponseModel;


/**
 * Service class for QuestionnaireResponse
 *
 * @author dja7cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_RVWQNAIRERESPONSE)
public class RvwQnaireResponseService extends AbstractRestService {


  /**
   * Get QuestionnaireResponse using its id
   *
   * @param objId object's id
   * @return Rest response, with RvwQnaireResponse object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getById(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    RvwQnaireResponseLoader loader = new RvwQnaireResponseLoader(getServiceData());
    RvwQnaireResponse ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }

  /**
   * @param pidcVersId as pidcVersId
   * @param variantId as Varaint Id
   * @return CdrReportQnaireRespWrapper
   * @throws IcdmException as exception
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_QNAIRERESP_FOR_WPRESP)
  @CompressData
  public Response getQniareRespVersByPidcVersIdAndVarId(
      @QueryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ID) final Long pidcVersId,
      @QueryParam(WsCommonConstants.RWS_QP_VARIANT_ID) final Long variantId)
      throws IcdmException {
    RvwQnaireResponseLoader loader = new RvwQnaireResponseLoader(getServiceData());
    CdrReportQnaireRespWrapper cdrReportQnaireRespWrapper =
        loader.getQniareRespVersByPidcVersIdAndVarId(pidcVersId, variantId, false);
    return Response.ok(cdrReportQnaireRespWrapper).build();
  }

  /**
   * @param pidcId as project ID
   * @param a2lRespId as a2l Response ID
   * @return boolean value
   * @throws IcdmException exception
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_HAS_QNAIRE_ACCESS)
  @CompressData
  public Response validateQnaireAccess(@QueryParam(WsCommonConstants.RWS_QP_PIDC_ID) final Long pidcId,
      @QueryParam(WsCommonConstants.RWS_A2L_RESP_ID) final Long a2lRespId)
      throws IcdmException {
    RvwQnaireResponseLoader loader = new RvwQnaireResponseLoader(getServiceData());
    return Response.ok(loader.validateQnaireAccess(pidcId, a2lRespId)).build();
  }

  /**
   * Get QuestionnaireResponseModel using its id.
   *
   * @param qnaireRespVersId the qnaire resp version id
   * @return Rest response, with RvwQnaireResponse object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_ALL_MAPPING)
  @CompressData
  public RvwQnaireResponseModel getPidcQnaireResponseModel(
      @QueryParam(WsCommonConstants.RWS_RVWQNAIRERESPVERSION) final Long qnaireRespVersId)
      throws IcdmException {
    RvwQnaireResponseModel respModel =
        new RvwQnaireResponseLoader(getServiceData()).getQnaireResponseModel(qnaireRespVersId);
    getLogger().info(" QuestionnaireResponse getPidcQnaireResponseModel completed.");
    return respModel;
  }


  /**
   * @param obj as RvwQnaireResponse
   * @return as RvwQnaireResponse
   * @throws IcdmException as IcdmException
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final RvwQnaireResponse obj) throws IcdmException {
    RvwQnaireResponseCommand cmd = new RvwQnaireResponseCommand(getServiceData(), obj, true, false, false);
    executeCommand(cmd);
    RvwQnaireResponse ret = cmd.getNewData();
    getLogger().info("Updated RvwQnaireResponse Id : {}", ret.getId());
    return Response.ok(ret).build();
  }


  /**
   * Service for fetching all pidc versions for a pidc
   *
   * @param pidcVerId pidc version id
   * @return response
   * @throws IcdmException web service exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_PIDC_QNAIRE_RESP)
  @CompressData
  public Response getPidcQnaireVariants(@QueryParam(value = WsCommonConstants.RWS_QP_PIDC_VERS_ID) final Long pidcVerId)
      throws IcdmException {
    RvwQnaireResponseLoader qnaireRespLoader = new RvwQnaireResponseLoader(getServiceData());
    PidcQnaireInfo qnaireRespMap = qnaireRespLoader.getPidcQnaireResponse(pidcVerId);
    return Response.ok(qnaireRespMap).build();
  }

  /**
   * @param qnaireRespInputData {@link QnaireRespUpdationModel}
   * @return {@link RvwQnaireResponse}
   * @throws IcdmException web service exception
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_QNAIRE_RESP_FROM_WP)
  @CompressData
  public QnaireRespUpdationModel createQnaireResp(final QnaireRespUpdationModel qnaireRespInputData)
      throws IcdmException {
    RvwQnaireRespUpdationCommand command = new RvwQnaireRespUpdationCommand(getServiceData(), qnaireRespInputData);
    executeCommand(command);
    return qnaireRespInputData;
  }

  /**
   * @param qnaireRespId RESP ID
   * @return {@link RvwQnaireResponse}
   * @throws IcdmException web service exception
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_WP_ID_FROM_QNAIRE_RESP_ID)
  @CompressData
  public List<Long> getWorkpackageId(final Set<Long> qnaireRespId) throws IcdmException {
    RvwQnaireResponseLoader qnaireRespLoader = new RvwQnaireResponseLoader(getServiceData());
    return qnaireRespLoader.getWorkpackageId(qnaireRespId);
  }


  /**
   * Service to get all qnaire response deatils (Variant, wp, Resp) from selected a2l responsibilty for merging
   *
   * @param a2lRespIdSet selected a2l responsibility Id set
   * @return Rvw qnaire response details of selected responsbility
   * @throws IcdmException exception in ws call
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_RVW_QNAIRE_DETAILS_FROM_RESP_ID)
  @CompressData
  public List<QnaireRespDetails> getQnaireRespDetailsList(final List<Long> a2lRespIdSet) throws IcdmException {
    RvwQnaireResponseLoader qnaireRespLoader = new RvwQnaireResponseLoader(getServiceData());
    return qnaireRespLoader.getQnaireRespDetailsList(a2lRespIdSet);
  }

  /**
   * @param wpId workpackage id
   * @param respId responsibillity id
   * @param pidVarId pidc variant id
   * @return {@link RvwQnaireResponse}
   * @throws IcdmException web service exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_IS_GEN_QUES_NOT_REQ)
  @CompressData
  public Response isGenQuesNotRequired(@QueryParam(value = WsCommonConstants.RWS_QP_WP_ID) final Long wpId,
      @QueryParam(value = WsCommonConstants.RWS_QP_RESP_ID) final Long respId,
      @QueryParam(value = WsCommonConstants.RWS_QP_PIDC_VARIANT_ID) final Long pidVarId)
      throws IcdmException {
    RvwQnaireResponseLoader qnaireRespLoader = new RvwQnaireResponseLoader(getServiceData());
    return Response.ok(qnaireRespLoader.isGenQuesNotRequired(wpId, respId, pidVarId)).build();
  }

  /**
   * Deletes/undeletes a questionnaire response (soft-delete)
   *
   * @param obj model to delete
   * @return deleted model
   * @throws IcdmException error during deletion
   */
  @PUT
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_DELETE_UNDELETE_QUES_RESP)
  @CompressData
  public Response deleteUndeleteQuesResp(final RvwQnaireResponse obj) throws IcdmException {

    RvwQnaireResponseCommand cmd = new RvwQnaireResponseCommand(getServiceData(), obj, true, false, false);
    executeCommand(cmd);
    RvwQnaireResponse ret = cmd.getNewData();

    getLogger().info("Deleted/Undeleted RvwQnaireResponse Id : {}", ret.getId());

    QuesRespDeletionOutput model = new QuesRespDeletionOutput();
    model.setDelUndeleteQnaireResp(ret);
    model.setUndeletedGenQues(cmd.getUnDeletedGenQues());

    return Response.ok(model).build();
  }

  /**
   * @param respId Qnaire resp id
   * @return boolean true/false
   * @throws IcdmException web service exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_IS_QUES_VERSION_UPDATE_REQ)
  @CompressData
  public Response isQnaireVersUpdateReq(@QueryParam(value = WsCommonConstants.RWS_QP_RESP_ID) final Long respId)
      throws IcdmException {
    RvwQnaireResponseLoader qnaireRespLoader = new RvwQnaireResponseLoader(getServiceData());
    return Response.ok(qnaireRespLoader.isQnaireVersionUpdateRequired(respId)).build();
  }
}
