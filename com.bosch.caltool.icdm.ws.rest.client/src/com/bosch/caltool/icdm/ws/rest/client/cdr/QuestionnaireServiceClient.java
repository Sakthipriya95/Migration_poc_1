/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.SortedSet;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireCreationModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.Questionnaire;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireVersion;
import com.bosch.caltool.icdm.model.general.DataCreationModel;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapper;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for Questionnaire
 *
 * @author bne4cob
 */
public class QuestionnaireServiceClient extends AbstractRestServiceClient {

  private static final IMapper QUESTIONAIRE_CREATION_MAPPER =
      obj -> new HashSet<>(Arrays.asList(((DataCreationModel<Questionnaire>) obj).getDataCreated(),
          ((DataCreationModel<Questionnaire>) obj).getNodeAccess()));

  /**
   * Constructor
   */
  public QuestionnaireServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_QUESTIONNAIRE);
  }

  /**
   * Get all Questionnaire records in system
   *
   * @param includeDeleted deleted flag
   * @param includeQnaireWithoutQues include qnaire without question flag
   * @return Map. Key - id, Value - Questionnaire object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Map<Long, Questionnaire> getAll(final boolean includeDeleted, final boolean includeQnaireWithoutQues)
      throws ApicWebServiceException {
    LOGGER.debug("Get all Questionnaire records ");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL)
        .queryParam(WsCommonConstants.RWS_QP_INCLUDE_DELETED, includeDeleted)
        .queryParam(WsCommonConstants.RWS_QP_INCLUDE_QNAIRE_WITHOUT_QUEST, includeQnaireWithoutQues);
    GenericType<Map<Long, Questionnaire>> type = new GenericType<Map<Long, Questionnaire>>() {};
    Map<Long, Questionnaire> retMap = get(wsTarget, type);
    LOGGER.debug("Questionnaire records loaded count = {}", retMap.size());
    return retMap;
  }

  /**
   * @param questionnaireId
   * @return
   * @throws ApicWebServiceException
   */
  public SortedSet<QuestionnaireVersion> getAllVersions(final Long questionnaireId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL_VERSIONS)
        .queryParam(WsCommonConstants.RWS_QP_OBJ_ID, questionnaireId);
    GenericType<SortedSet<QuestionnaireVersion>> type = new GenericType<SortedSet<QuestionnaireVersion>>() {};
    return get(wsTarget, type);

  }

  /**
   * Get Questionnaire using its id
   *
   * @param objId object's id
   * @return Questionnaire object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Questionnaire get(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, Questionnaire.class);
  }

  /**
   * @param questionnaireId questionnaireId
   * @return QuestionnaireVersion
   * @throws ApicWebServiceException error during webservice call
   */
  public QuestionnaireVersion getWorkingSet(final Long questionnaireId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_WORKING_SET)
        .queryParam(WsCommonConstants.RWS_QP_OBJ_ID, questionnaireId);
    return get(wsTarget, QuestionnaireVersion.class);
  }


  /**
   * Create a Questionnaire record
   *
   * @param qnaireCreationData QnaireCreationModel
   * @return created Questionnaire object
   * @throws ApicWebServiceException exception while invoking service
   */
  public DataCreationModel<Questionnaire> createQnaireAndVersion(final QnaireCreationModel qnaireCreationData)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_CREATE_QNAIRE_MODEL);
    return create(wsTarget, qnaireCreationData, new GenericType<DataCreationModel<Questionnaire>>() {},
        QuestionnaireServiceClient.QUESTIONAIRE_CREATION_MAPPER);
  }

  /**
   * Update a Questionnaire record
   *
   * @param obj object to update
   * @return updated Questionnaire object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Questionnaire update(final Questionnaire obj) throws ApicWebServiceException {
    return update(getWsBase(), obj);
  }

}
