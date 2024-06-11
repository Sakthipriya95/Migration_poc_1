/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;

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
import com.bosch.caltool.icdm.bo.cdr.qnaire.QuestionnaireVersionCommand;
import com.bosch.caltool.icdm.bo.cdr.qnaire.QuestionnaireVersionLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireVersionModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireVersion;

/**
 * @author nip4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_QNAIRE_VERSION)

public class QnaireVersionService extends AbstractRestService {

  /**
   * @param qnaireVersionId questionnaireVersionId
   * @return SortedSet of Question
   * @throws IcdmException error during webservice call
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_QNAIRE_VER_WITH_DETAILS)
  @CompressData
  public Response getQnaireVersionWithDetails(
      @QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long qnaireVersionId) throws IcdmException {

    // fetch the questionnaire version with details
    QnaireVersionModel ret =
        new QuestionnaireVersionLoader(getServiceData()).getQnaireVersionWithDetails(qnaireVersionId);

    return Response.ok(ret).build();
  }

  /**
   * @param questionId question id
   * @return set of QuestionConfigModel
   * @throws IcdmException exception during web service call
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_QUEST_VERSION_MODEL_BY_QUES_ID)
  @CompressData
  public Response getUpdatedQnaireModel(@QueryParam(value = WsCommonConstants.RWS_QP_QUESTION_ID) final Long questionId)
      throws IcdmException {
    QuestionnaireVersionLoader questionLoader = new QuestionnaireVersionLoader(getServiceData());
    QnaireVersionModel retModel = questionLoader.getQnaireVModelByQuesId(questionId);
    return Response.ok(retModel).build();
  }

  /**
   * @param qnaireVersion
   * @return
   * @throws IcdmException
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response create(final QuestionnaireVersion qnaireVersion) throws IcdmException {
    QuestionnaireVersionCommand cmd = new QuestionnaireVersionCommand(getServiceData(), qnaireVersion);
    // create a new questionnaire
    executeCommand(cmd);
    QuestionnaireVersion ret = cmd.getNewData();
    return Response.ok(ret).build();
  }

  /**
   * @param qnaireVersion
   * @return
   * @throws IcdmException
   */
  @PUT
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response update(final QuestionnaireVersion qnaireVersion) throws IcdmException {
    QuestionnaireVersionCommand cmd = new QuestionnaireVersionCommand(getServiceData(), qnaireVersion, true);
    // update an existing questionnaire
    executeCommand(cmd);
    QuestionnaireVersion ret = cmd.getNewData();
    return Response.ok(ret).build();
  }

  /**
   * @param qnaireVersionId
   * @return
   * @throws IcdmException
   */
  @DELETE
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response delete(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long qnaireVersionId)
      throws IcdmException {
    QuestionnaireVersionCommand cmd = new QuestionnaireVersionCommand(getServiceData(),
        new QuestionnaireVersionLoader(getServiceData()).getDataObjectByID(qnaireVersionId), false);
    // delete the questionnaire
    executeCommand(cmd);
    return Response.ok().build();
  }
}
