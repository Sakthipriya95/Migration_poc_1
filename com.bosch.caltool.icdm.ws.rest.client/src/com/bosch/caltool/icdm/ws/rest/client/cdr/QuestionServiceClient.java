/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestAttrAndValDepModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionConfigModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionCreationData;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionDepenAttr;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionUpdationData;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapper;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author NIP4COB
 */
public class QuestionServiceClient extends AbstractRestServiceClient {

  private static final IMapper QUESTION_UPDATION_MAPPER =
      obj -> new HashSet<>(Arrays.asList(((QuestionUpdationData) obj).getQuestion()));

  private static final IMapper QUESTION_RESP_MAPPER =
      obj -> new HashSet<>(Arrays.asList(((QuestionConfigModel) obj).getQuestion()));

  /**
   */
  public QuestionServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_QUESTION);
  }

  /**
   * @param question question to be created
   * @return the question created
   * @throws ApicWebServiceException error during webservicecall
   */
  public QuestionConfigModel create(final QuestionCreationData question) throws ApicWebServiceException {
    return create(getWsBase(), question, QuestionConfigModel.class, QuestionServiceClient.QUESTION_RESP_MAPPER);
  }

  /**
   * @param questionId questionId
   * @return set of QuestionDepenAttr for a question
   * @throws ApicWebServiceException error during web service call
   */
  public Set<Attribute> getQuestionDependentAttributes(final Long questionId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_QUEST_DEP_ATTR)
        .queryParam(WsCommonConstants.RWS_QP_QUESTION_ID, questionId);
    GenericType<Set<Attribute>> type = new GenericType<Set<Attribute>>() {};
    return get(wsTarget, type);
  }


  /**
   * @param questionId
   * @return
   * @throws ApicWebServiceException
   */
  public Map<Long, QuestionDepenAttr> getQuestionDepenAttrMap(final Long questionId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_QUEST_DEP_ATTR_MAP)
        .queryParam(WsCommonConstants.RWS_QP_QUESTION_ID, questionId);
    GenericType<Map<Long, QuestionDepenAttr>> type = new GenericType<Map<Long, QuestionDepenAttr>>() {};
    return get(wsTarget, type);
  }

  /**
   * @param questionId
   * @return
   * @throws ApicWebServiceException
   */
  public QuestAttrAndValDepModel getQuestDependentAttrAndValModel(final Long questionId)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_QN_DEP_ATTR_VAL_MODEL)
        .queryParam(WsCommonConstants.RWS_QP_QUESTION_ID, questionId);
    return get(wsTarget, QuestAttrAndValDepModel.class);
  }


  /**
   * @param updateModel question to be updated
   * @return the updated question
   * @throws ApicWebServiceException error during webservicecall
   */
  public QuestionConfigModel update(final QuestionUpdationData updateModel) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase();
    return update(wsTarget, updateModel, QuestionConfigModel.class, QuestionServiceClient.QUESTION_UPDATION_MAPPER,
        QuestionServiceClient.QUESTION_RESP_MAPPER);
  }


  /**
   * Gets the all qn depn attr val model.
   *
   * @param qnaireVersId the question vers id
   * @return the all qn depn attr val model
   * @throws ApicWebServiceException the apic web service exception
   */
  public QuestAttrAndValDepModel getAllQnDepnAttrValModelByVersion(final Long qnaireVersId)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL_QN_DEP_ATTR_VAL_MODEL)
        .queryParam(WsCommonConstants.RWS_QP_QNAIRE_VERS_ID, qnaireVersId);
    return get(wsTarget, QuestAttrAndValDepModel.class);
  }
}