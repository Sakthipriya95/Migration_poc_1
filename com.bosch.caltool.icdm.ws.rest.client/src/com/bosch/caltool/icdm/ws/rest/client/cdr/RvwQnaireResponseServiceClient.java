package com.bosch.caltool.icdm.ws.rest.client.cdr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.datamodel.core.cns.ChangeDataCreator;
import com.bosch.caltool.icdm.model.apic.pidc.PidcQnaireInfo;
import com.bosch.caltool.icdm.model.cdr.CdrReportQnaireRespWrapper;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespDetails;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespUpdationModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuesRespDeletionOutput;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponseModel;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.ChangeHandler;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapperChangeData;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.ModelParser;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for QuestionnaireResponse
 *
 * @author dja7cob
 */
public class RvwQnaireResponseServiceClient extends AbstractRestServiceClient {

  private static final IMapperChangeData QNAIRE_RESPONSE_CREATION = data -> {

    Collection<ChangeData<IModel>> changeDataList = new HashSet<>();
    ChangeDataCreator<IModel> changeDataCreator = new ChangeDataCreator<>();
    QnaireRespUpdationModel updationModel = (QnaireRespUpdationModel) data;

    updationModel.getOldQnaireRespSet().forEach(qnaireResp -> changeDataList.add(changeDataCreator
        .createDataForUpdate(0L, qnaireResp, updationModel.getUpdatedQnaireRespMap().get(qnaireResp.getId()))));
    updationModel.getNewQnaireRespSet()
        .forEach(qnaireResp -> changeDataList.add(changeDataCreator.createDataForCreate(0L, qnaireResp)));
    updationModel.getCreatedQnaireRespVariantSet()
        .forEach(qnaireRespVar -> changeDataList.add(changeDataCreator.createDataForCreate(0L, qnaireRespVar)));
    updationModel.getDeletedQnaireRespVariant()
        .forEach(qnaireRespVar -> changeDataList.add(changeDataCreator.createDataForDelete(0L, qnaireRespVar)));

    return changeDataList;
  };

  private static final IMapperChangeData QNAIRE_RESPONSE_DELETION = data -> {

    Collection<ChangeData<IModel>> changeDataList = new HashSet<>();
    ChangeDataCreator<IModel> changeDataCreator = new ChangeDataCreator<>();
    QuesRespDeletionOutput updationModel = (QuesRespDeletionOutput) data;
    changeDataList.add(changeDataCreator.createDataForUpdate(0L, null, updationModel.getDelUndeleteQnaireResp()));

    if (null != updationModel.getUndeletedGenQues()) {
      changeDataList.add(changeDataCreator.createDataForUpdate(0L, null, updationModel.getUndeletedGenQues()));
    }
    return changeDataList;
  };

  /**
   * Constructor
   */
  public RvwQnaireResponseServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_RVWQNAIRERESPONSE);
  }

  /**
   * Get QuestionnaireResponse using its id
   *
   * @param objId object's id
   * @return RvwQnaireResponse object
   * @throws ApicWebServiceException exception while invoking service
   */
  public RvwQnaireResponse getById(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, RvwQnaireResponse.class);
  }

  /**
   * @param pidcVersId as pidc Version Id
   * @param variantId as selected Variant ID
   * @return CdrReportQnaireRespWrapper
   * @throws ApicWebServiceException as exception
   */
  public CdrReportQnaireRespWrapper getQniareRespVersByPidcVersIdAndVarId(final Long pidcVersId, final Long variantId)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_QNAIRERESP_FOR_WPRESP)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ID, pidcVersId)
        .queryParam(WsCommonConstants.RWS_QP_VARIANT_ID, variantId);
    return get(wsTarget, CdrReportQnaireRespWrapper.class);
  }

  /**
   * Gets the all mapping model.
   *
   * @param objId the obj id
   * @return the all mapping model
   * @throws ApicWebServiceException the apic web service exception
   */
  public RvwQnaireResponseModel getAllMappingModel(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL_MAPPING)
        .queryParam(WsCommonConstants.RWS_RVWQNAIRERESPVERSION, objId);
    return get(wsTarget, RvwQnaireResponseModel.class);
  }


  /**
   * @param pidcId as PIDCIDCard
   * @param a2lRespId as a2lrespId
   * @return boolean true/false
   * @throws ApicWebServiceException as exception
   */
  public boolean validateQnaireAccess(final Long pidcId, final Long a2lRespId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_HAS_QNAIRE_ACCESS)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_ID, pidcId).queryParam(WsCommonConstants.RWS_A2L_RESP_ID, a2lRespId);
    return get(wsTarget, boolean.class);
  }

  /**
   * @param obj as RvwQnaireResponse Input
   * @return as RvwQnaireResponse output
   * @throws ApicWebServiceException as exception
   */
  public RvwQnaireResponse update(final RvwQnaireResponse obj) throws ApicWebServiceException {
    return update(getWsBase(), obj);
  }

  /**
   * @param pidcVerId pidc version id
   * @return PidcQnaireInfo
   * @throws ApicWebServiceException Web service exception
   */
  public PidcQnaireInfo getPidcQnaireVariants(final Long pidcVerId) throws ApicWebServiceException {
    LOGGER.debug("Started Fetching Questionnaire variants for a Pidc");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_PIDC_QNAIRE_RESP)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ID, pidcVerId);
    PidcQnaireInfo qnaireResponse = get(wsTarget, PidcQnaireInfo.class);
    LOGGER.debug("Fetching Questionnaire variants for a Pidc completed");
    return qnaireResponse;
  }


  /**
   * Service to create questionnaire response from selected workpackage and update the existing questionnaire response
   *
   * @param qnaireRespInputData {@link QnaireRespUpdationModel} input data to create qnaire response
   * @return updated {@link QnaireRespUpdationModel}
   * @throws ApicWebServiceException exception in web service call
   */
  public QnaireRespUpdationModel createQnaireResp(final QnaireRespUpdationModel qnaireRespInputData)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_QNAIRE_RESP_FROM_WP);
    GenericType<QnaireRespUpdationModel> type = new GenericType<QnaireRespUpdationModel>() {};

    QnaireRespUpdationModel resp = post(wsTarget, qnaireRespInputData, type);

    Collection<ChangeData<IModel>> newDataModelSet = ModelParser.getChangeData(resp, QNAIRE_RESPONSE_CREATION);

    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(newDataModelSet));

    displayMessage("Review qnaire response updated");

    return resp;
  }

  /**
   * Service to get the corresponsing work package id(s) for the questionnaire response
   *
   * @param qnaireRespId set of already available qnaire resp id
   * @return corresponding work package id
   * @throws ApicWebServiceException exception in web service call
   */
  public List<Long> getWorkpackageId(final Set<Long> qnaireRespId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_WP_ID_FROM_QNAIRE_RESP_ID);
    GenericType<List<Long>> type = new GenericType<List<Long>>() {};
    return post(wsTarget, qnaireRespId, type);
  }

  /**
   * Service to get all qnaire response deatils (Variant, wp, Resp) from selected a2l responsibilty for merging
   *
   * @param a2lRespIdSet selected a2l responsibility Id set
   * @return Rvw qnaire response details of selected responsbility
   * @throws ApicWebServiceException exception in ws call
   */
  public List<QnaireRespDetails> getQnaireRespDetailsList(final List<Long> a2lRespIdSet)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_RVW_QNAIRE_DETAILS_FROM_RESP_ID);
    GenericType<List<QnaireRespDetails>> type = new GenericType<List<QnaireRespDetails>>() {};
    return post(wsTarget, a2lRespIdSet, type);
  }

  /**
   * @param pidcVarId pidc variant id
   * @param respId responsibility id
   * @param wpId workpackage id
   * @return true if gen questionnaires are not required
   * @throws ApicWebServiceException service error
   */
  public boolean isGenQuesNotRequired(final Long wpId, final Long respId, final Long pidcVarId)
      throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_IS_GEN_QUES_NOT_REQ)
        .queryParam(WsCommonConstants.RWS_QP_WP_ID, wpId).queryParam(WsCommonConstants.RWS_QP_RESP_ID, respId)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_VARIANT_ID, pidcVarId);
    return get(wsTarget, boolean.class);
  }

  /**
   * @param obj response object
   * @throws ApicWebServiceException service error
   */
  public void deleteUndeleteQuesResp(final RvwQnaireResponse obj) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_DELETE_UNDELETE_QUES_RESP);
    QuesRespDeletionOutput outputModel = put(wsTarget, obj, QuesRespDeletionOutput.class);
    Collection<ChangeData<IModel>> newDataModelSet = ModelParser.getChangeData(outputModel, QNAIRE_RESPONSE_DELETION);

    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(newDataModelSet));

    displayMessage("Review qnaire response updated");


  }

  /**
   * @param respId qnaireRespId
   * @return true or false
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public boolean isQnaireVersUpdateReq(final Long respId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_IS_QUES_VERSION_UPDATE_REQ)
        .queryParam(WsCommonConstants.RWS_QP_RESP_ID, respId);
    return get(wsTarget, boolean.class);
  }
}
