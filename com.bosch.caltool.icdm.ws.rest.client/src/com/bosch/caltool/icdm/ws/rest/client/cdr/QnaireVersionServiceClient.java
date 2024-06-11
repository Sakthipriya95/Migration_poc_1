/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireVersionModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireVersion;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author nip4cob
 */
public class QnaireVersionServiceClient extends AbstractRestServiceClient {

  /**
   *
   */
  public QnaireVersionServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_QNAIRE_VERSION);
  }

  /**
   * @param questionnaireVersionId questionnaireVersionId
   * @return SortedSet of question
   * @throws ApicWebServiceException error during web service call
   */
  public QnaireVersionModel getQnaireVersionWithDetails(final Long questionnaireVersionId)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_QNAIRE_VER_WITH_DETAILS)
        .queryParam(WsCommonConstants.RWS_QP_OBJ_ID, questionnaireVersionId);
    return get(wsTarget, QnaireVersionModel.class);
  }

  /**
   * @param questionId set of question id
   * @return set of QuestionConfigModel
   * @throws ApicWebServiceException error during web service call
   */
  public QnaireVersionModel getQnaireVModelByQuesId(final Long questionId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_QUEST_VERSION_MODEL_BY_QUES_ID)
        .queryParam(WsCommonConstants.RWS_QP_QUESTION_ID, questionId);
    GenericType<QnaireVersionModel> type = new GenericType<QnaireVersionModel>() {};
    return get(wsTarget, type);
  }


  /**
   * @param qVersion - QuestionnaireVersion to create
   * @return the created QuestionnaireVersion
   * @throws ApicWebServiceException error during webservice call
   */
  public QuestionnaireVersion create(final QuestionnaireVersion qVersion) throws ApicWebServiceException {
    return create(getWsBase(), qVersion, QuestionnaireVersion.class);
  }

  /**
   * @param qVersion - QuestionnaireVersion to update
   * @return the updated QuestionnaireVersion
   * @throws ApicWebServiceException error during webservice call
   */
  public QuestionnaireVersion update(final QuestionnaireVersion qVersion) throws ApicWebServiceException {
    return update(getWsBase(), qVersion);
  }

  /**
   * @param qnaireVersId - ID of the QuestionnaireVersion to be deleted
   * @throws ApicWebServiceException error during webservice call
   */
  public void delete(final Long qnaireVersId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, qnaireVersId);
    delete(wsTarget);
  }
}