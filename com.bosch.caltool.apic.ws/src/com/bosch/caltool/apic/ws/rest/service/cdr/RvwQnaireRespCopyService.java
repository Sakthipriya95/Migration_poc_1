/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;

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
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireRespCopyCommand;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireResponseLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespActionData;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespCopyData;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuesRespCopyDataWrapper;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespChangeModel;

/**
 * Service class for QuestionnaireResponse Copy/Paste functionality
 *
 * @author UKT1COB
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_RVW_QNAIRE_RESP_COPY)
public class RvwQnaireRespCopyService extends AbstractRestService {

  /**
   * @param pidcVersId as pidc_vers_id
   * @param pidcVarId as variant id
   * @param wpId workpackage Id
   * @param respId responsiblity Id
   * @param copiedQnaireRespId copied Qnaire Resp Id
   * @param srcPidcVersId copied qnaire resp Pidc version Id
   * @return QuesRespCopyDataWrapper
   * @throws IcdmException as exception
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_RETREIVE_DATA_FOR_COPY_VALIDATION)
  @CompressData
  public Response getDataForCopyQnaireRespValidation(
      @QueryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ID) final Long pidcVersId,
      @QueryParam(WsCommonConstants.RWS_QP_VARIANT_ID) final Long pidcVarId,
      @QueryParam(WsCommonConstants.RWS_QP_WP_ID) final Long wpId,
      @QueryParam(WsCommonConstants.RWS_A2L_RESP_ID) final Long respId,
      @QueryParam(WsCommonConstants.RVW_QNAIRE_RESP_ID) final Long copiedQnaireRespId,
      @QueryParam(WsCommonConstants.RWS_QP_SRC_PIDC_VERS_ID) final Long srcPidcVersId)
      throws IcdmException {
    RvwQnaireResponseLoader rvwQnaireRespLoader = new RvwQnaireResponseLoader(getServiceData());
    QuesRespCopyDataWrapper copyDataForValidation = rvwQnaireRespLoader.getDataForCopyQnaireRespValidation(pidcVersId,
        pidcVarId, wpId, respId, copiedQnaireRespId, srcPidcVersId);

    getLogger().info("RvwQnaireRespCopyService.getDataForCopyQnaireRespValidation completed.");

    return Response.ok(copyDataForValidation).build();
  }

  /**
   * Service to create the copied questionnaire response in the selected destination workpackage
   *
   * @param qnaireRespCopyData input data to create qnaire response
   * @return RvwQnaireRespChangeModel
   * @throws IcdmException exception in web service call
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_COPY_QNAIRE_RESP_TO_DEST_WP)
  @CompressData
  public Response createQnaireResp(final QnaireRespCopyData qnaireRespCopyData) throws IcdmException {

    RvwQnaireRespCopyCommand rvwQnaireRespCopyCmd = new RvwQnaireRespCopyCommand(getServiceData(), qnaireRespCopyData,
        CommonUtils.isNull(qnaireRespCopyData.getExistingTargetQnaireResp()));
    executeCommand(rvwQnaireRespCopyCmd);
    RvwQnaireRespChangeModel pasteOutput = new RvwQnaireRespChangeModel();
    pasteOutput.setPastedRvwQnaireResp(rvwQnaireRespCopyCmd.getPastedQnaireResp());
    pasteOutput.setDestGeneralQuesRespAfterCopy(rvwQnaireRespCopyCmd.getDestGeneralQnaireRespAfterUpdate());

    getLogger().info("RvwQnaireRespCopyService.createQnaireResp completed. ID = {}",
        pasteOutput.getPastedRvwQnaireResp().getId());

    return Response.ok(pasteOutput).build();
  }

  /**
   * Service to update already existing same qnaire resp as copied one in the selected destination workpackage
   *
   * @param qnaireRespCopyData input data to create qnaire response
   * @return RvwQnaireRespChangeModel
   * @throws IcdmException exception in web service call
   */
  @PUT
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_UPDATE_QNAIRE_RESP_IN_DEST_WP)
  @CompressData
  public Response updateQnaireResp(final QnaireRespCopyData qnaireRespCopyData) throws IcdmException {

    RvwQnaireRespCopyCommand rvwQnaireRespCopyCmd = new RvwQnaireRespCopyCommand(getServiceData(), qnaireRespCopyData,
        CommonUtils.isNull(qnaireRespCopyData.getExistingTargetQnaireResp()));
    executeCommand(rvwQnaireRespCopyCmd);
    RvwQnaireRespChangeModel pasteOutput = new RvwQnaireRespChangeModel();
    pasteOutput.setPastedRvwQnaireResp(rvwQnaireRespCopyCmd.getPastedQnaireResp());
    pasteOutput.setDestGeneralQuesRespAfterCopy(rvwQnaireRespCopyCmd.getDestGeneralQnaireRespAfterUpdate());
    pasteOutput.setBaselinedRvwQnaireRespVersion(rvwQnaireRespCopyCmd.getBaselinedRvwQnaireRespVersion());
    pasteOutput.setDeletedRvwQnaireRespWSVersion(rvwQnaireRespCopyCmd.getDeletedRvwQnaireRespWSVersion());
    pasteOutput.setCopiedRvwQnaireRespVersionList(rvwQnaireRespCopyCmd.getCopiedRvwQnaireRespVersionList());
    pasteOutput.setCopiedRvwQnaireAnswerList(rvwQnaireRespCopyCmd.getCopiedRvwQnaireAnswerList());
    pasteOutput.setCopiedRvwQnaireAnswerOplList(rvwQnaireRespCopyCmd.getCopiedRvwQnaireAnswerOplList());
    pasteOutput.setCopiedRvwQnaireAnswerLinkList(rvwQnaireRespCopyCmd.getCopiedRvwQnaireAnswerLinkList());

    getLogger().info("RvwQnaireRespCopyService.updateQnaireResp completed. ID = {}",
        pasteOutput.getPastedRvwQnaireResp().getId());

    return Response.ok(pasteOutput).build();
  }

  /**
   * Service to update the QnaireVersion to the latest version
   *
   * @param qnaireRespUpdateData input data to create qnaire response
   * @return RvwQnaireRespChangeModel
   * @throws IcdmException exception in web service call
   */
  @PUT
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_UPDATE_QNAIRE_VERSION)
  @CompressData
  public Response updateQnaireRespVersion(final QnaireRespActionData qnaireRespUpdateData) throws IcdmException {

    RvwQnaireRespCopyCommand rvwQnaireRespCopyCmd =
        new RvwQnaireRespCopyCommand(getServiceData(), new QnaireRespCopyData(), false);
    rvwQnaireRespCopyCmd.setQuesUpdate(true);
    rvwQnaireRespCopyCmd.setQnaireRespVersionUpdateData(qnaireRespUpdateData);
    executeCommand(rvwQnaireRespCopyCmd);

    RvwQnaireRespChangeModel qnaireVersUpdateOutput = new RvwQnaireRespChangeModel();
    // setting updated Qnaire response to field 'pastedRvwQnaireResp' reusing the existing copy paste
    // model(RvwQnaireRespChangeModel)
    qnaireVersUpdateOutput.setPastedRvwQnaireResp(new RvwQnaireResponseLoader(getServiceData())
        .setUpdatedRvwQnaireResponse(qnaireRespUpdateData.getExistingTargetQnaireResp().getId()));
    qnaireVersUpdateOutput.setCopiedRvwQnaireRespVersionList(rvwQnaireRespCopyCmd.getCopiedRvwQnaireRespVersionList());
    qnaireVersUpdateOutput.setBaselinedRvwQnaireRespVersion(rvwQnaireRespCopyCmd.getBaselinedRvwQnaireRespVersion());
    qnaireVersUpdateOutput.setDeletedRvwQnaireRespWSVersion(rvwQnaireRespCopyCmd.getDeletedRvwQnaireRespWSVersion());

    getLogger().info("RvwQnaireRespCopyService.updateQnaireVersion completed. ID = {}",
        qnaireVersUpdateOutput.getPastedRvwQnaireResp().getId());

    return Response.ok(qnaireVersUpdateOutput).build();
  }
}