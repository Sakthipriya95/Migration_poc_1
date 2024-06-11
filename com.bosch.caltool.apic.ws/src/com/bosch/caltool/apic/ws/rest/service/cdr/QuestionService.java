/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;

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
import com.bosch.caltool.icdm.bo.cdr.qnaire.QuestionCommand;
import com.bosch.caltool.icdm.bo.cdr.qnaire.QuestionConfigLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.QuestionCreationCommand;
import com.bosch.caltool.icdm.bo.cdr.qnaire.QuestionDepenAttrLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.QuestionLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.QuestionUpdationCommand;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestAttrAndValDepModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionConfigModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionCreationData;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionDepenAttr;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionUpdationData;

/**
 * @author nip4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_QUESTION)

public class QuestionService extends AbstractRestService {

  /**
   * @param questionId
   * @return
   * @throws IcdmException Error during webservice call
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_QUEST_DEP_ATTR)
  @CompressData
  public Response getQuestionDependentAttributes(
      @QueryParam(value = WsCommonConstants.RWS_QP_QUESTION_ID) final Long questionId)
      throws IcdmException {
    QuestionLoader questionLoader = new QuestionLoader(getServiceData());
    Set<Attribute> ret = questionLoader.getAttributes(questionId);
    return Response.ok(ret).build();
  }

  /**
   * @param questionId
   * @return
   * @throws IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_QUEST_DEP_ATTR_MAP)
  @CompressData
  public Response getQuestionDepenAttrMap(
      @QueryParam(value = WsCommonConstants.RWS_QP_QUESTION_ID) final Long questionId)
      throws IcdmException {
    QuestionDepenAttrLoader quesDepAttrLoader = new QuestionDepenAttrLoader(getServiceData());
    Map<Long, QuestionDepenAttr> ret = quesDepAttrLoader.getDepenAttrMap(questionId);
    return Response.ok(ret).build();
  }

  /**
   * Gets the quest dependent attr and val model.
   *
   * @param questionId the question id
   * @return the quest dependent attr and val model
   * @throws IcdmException the icdm exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_QN_DEP_ATTR_VAL_MODEL)
  @CompressData
  public Response getQuestDependentAttrAndValModel(
      @QueryParam(value = WsCommonConstants.RWS_QP_QUESTION_ID) final Long questionId)
      throws IcdmException {
    QuestionLoader questionLoader = new QuestionLoader(getServiceData());
    QuestAttrAndValDepModel ret = questionLoader.getQuestAttrAndValDepModel(questionId);
    return Response.ok(ret).build();
  }

  /**
   * @param question QuestionCommandData
   * @return the created Question
   * @throws IcdmException Error during webservice call
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response create(final QuestionCreationData question) throws IcdmException {
    QuestionCreationCommand cmd = new QuestionCreationCommand(getServiceData(), question);
    executeCommand(cmd);
    Question ret = new QuestionLoader(getServiceData()).getDataObjectByID(question.getQuestion().getId());
    QuestionConfigModel retModel = new QuestionConfigModel();
    retModel.setQuestion(ret);
    if (!ret.getHeadingFlag()) {
      retModel
          .setQuestionConfig(new QuestionConfigLoader(getServiceData()).getDataObjectByID(ret.getQuestionConfigId()));
    }
    return Response.ok(retModel).build();
  }

  /**
   * @param question QuestionCommandData
   * @return the updated question
   * @throws IcdmException Error during webservice call
   */
  @PUT
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response update(final QuestionUpdationData question) throws IcdmException {
    QuestionUpdationCommand cmd = new QuestionUpdationCommand(getServiceData(), question);
    executeCommand(cmd);
    QuestionConfigModel retModel = new QuestionConfigModel();
    Question headerQues = question.getQuestion();
    Question updatedQuestion = new QuestionLoader(getServiceData()).getDataObjectByID(headerQues.getId());
    retModel.setQuestion(updatedQuestion);

    if (updatedQuestion.getQuestionConfigId() != null) {
      retModel.setQuestionConfig(
          new QuestionConfigLoader(getServiceData()).getDataObjectByID(updatedQuestion.getQuestionConfigId()));
    }
    if (headerQues.getHeadingFlag()) {
      checkAndUpdateChildNodes(headerQues.getId(), headerQues.getDeletedFlag());
    }
    return Response.ok(retModel).build();
  }

  private void checkAndUpdateChildNodes(final Long questionId, final boolean headerDeletedFlag) throws IcdmException {
    Set<Question> childQuesChildren = new QuestionLoader(getServiceData()).getChildQuestions(questionId);
    if (!childQuesChildren.isEmpty()) {
      for (Question childQues : childQuesChildren) {
        checkAndUpdateChildNodes(childQues.getId(), headerDeletedFlag);
        // to group delete and undelete
        childQues.setDeletedFlag(headerDeletedFlag);
        executeCommand(new QuestionCommand(getServiceData(), childQues, true));
      }
    }
  }


  /**
   * Gets the all qn depn attr val model.
   *
   * @param qnaireVersId the qnaire vers id
   * @return the all qn depn attr val model
   * @throws IcdmException the icdm exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ALL_QN_DEP_ATTR_VAL_MODEL)
  @CompressData
  public Response getAllQnDepnAttrValModel(
      @QueryParam(value = WsCommonConstants.RWS_QP_QNAIRE_VERS_ID) final Long qnaireVersId)
      throws IcdmException {
    QuestionLoader questionLoader = new QuestionLoader(getServiceData());
    QuestAttrAndValDepModel ret = questionLoader.getAllQnDepnAttrValModel(qnaireVersId);
    return Response.ok(ret).build();
  }
}
