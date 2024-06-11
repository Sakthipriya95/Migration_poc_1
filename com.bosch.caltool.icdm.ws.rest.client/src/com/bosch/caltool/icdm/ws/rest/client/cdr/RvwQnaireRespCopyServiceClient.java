/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.datamodel.core.cns.ChangeDataCreator;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespActionData;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespCopyData;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuesRespCopyDataWrapper;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespChangeModel;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.ChangeHandler;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapperChangeData;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.ModelParser;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Service client class for QuestionnaireResponse Copy/Paste functionality
 *
 * @author UKT1COB
 */
public class RvwQnaireRespCopyServiceClient extends AbstractRestServiceClient {

  /**
   *
   */
  public RvwQnaireRespCopyServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_RVW_QNAIRE_RESP_COPY);
  }


  private static final IMapperChangeData QNAIRE_RESPONSE_CREATION_MAPPER = data -> {

    Collection<ChangeData<IModel>> changeDataList = new HashSet<>();
    ChangeDataCreator<IModel> changeDataCreator = new ChangeDataCreator<>();
    RvwQnaireRespChangeModel changeModel = (RvwQnaireRespChangeModel) data;

    changeDataList.add(changeDataCreator.createDataForCreate(0L, changeModel.getPastedRvwQnaireResp()));
    if ((null != changeModel.getDestGeneralQuesRespBeforeCopy()) &&
        (null != changeModel.getDestGeneralQuesRespAfterCopy())) {
      changeDataList.add(changeDataCreator.createDataForUpdate(0L, changeModel.getDestGeneralQuesRespBeforeCopy(),
          changeModel.getDestGeneralQuesRespAfterCopy()));
    }

    return changeDataList;
  };

  private static final IMapperChangeData QNAIRE_RESPONSE_UPDATION_MAPPER = data -> {

    Collection<ChangeData<IModel>> changeDataList = new HashSet<>();
    ChangeDataCreator<IModel> changeDataCreator = new ChangeDataCreator<>();
    RvwQnaireRespChangeModel changeModel = (RvwQnaireRespChangeModel) data;

    changeDataList.add(changeDataCreator.createDataForUpdate(0L, changeModel.getExistingTargetRvwQnaireResp(),
        changeModel.getPastedRvwQnaireResp()));
    if ((null != changeModel.getDestGeneralQuesRespBeforeCopy()) &&
        (null != changeModel.getDestGeneralQuesRespAfterCopy())) {
      changeDataList.add(changeDataCreator.createDataForUpdate(0L, changeModel.getDestGeneralQuesRespBeforeCopy(),
          changeModel.getDestGeneralQuesRespAfterCopy()));
    }
    changeDataList.add(changeDataCreator.createDataForCreate(0L, changeModel.getBaselinedRvwQnaireRespVersion()));
    changeModel.getCopiedRvwQnaireRespVersionList().forEach(
        rvwQnaireRespVersion -> changeDataList.add(changeDataCreator.createDataForCreate(0L, rvwQnaireRespVersion)));
    changeModel.getCopiedRvwQnaireAnswerList().forEach(
        rvwQnaireRespAnswer -> changeDataList.add(changeDataCreator.createDataForCreate(0L, rvwQnaireRespAnswer)));
    changeModel.getCopiedRvwQnaireAnswerOplList().forEach(rvwQnaireRespAnswerOpl -> changeDataList
        .add(changeDataCreator.createDataForCreate(0L, rvwQnaireRespAnswerOpl)));
    changeModel.getCopiedRvwQnaireAnswerLinkList().forEach(rvwQnaireRespAnswerLink -> changeDataList
        .add(changeDataCreator.createDataForCreate(0L, rvwQnaireRespAnswerLink)));
    changeDataList.add(changeDataCreator.createDataForDelete(0L, changeModel.getDeletedRvwQnaireRespWSVersion()));

    return changeDataList;
  };

  private static final IMapperChangeData QNAIRE_RESPONSE_VERSION_UPDATION_MAPPER = data -> {

    Collection<ChangeData<IModel>> changeDataList = new HashSet<>();
    ChangeDataCreator<IModel> changeDataCreator = new ChangeDataCreator<>();
    RvwQnaireRespChangeModel changeModel = (RvwQnaireRespChangeModel) data;

    changeDataList.add(changeDataCreator.createDataForUpdate(0L, changeModel.getExistingTargetRvwQnaireResp(),
        changeModel.getPastedRvwQnaireResp()));
    changeDataList.add(changeDataCreator.createDataForCreate(0L, changeModel.getBaselinedRvwQnaireRespVersion()));
    changeModel.getCopiedRvwQnaireRespVersionList().forEach(
        rvwQnaireRespVersion -> changeDataList.add(changeDataCreator.createDataForCreate(0L, rvwQnaireRespVersion)));
    changeDataList.add(changeDataCreator.createDataForDelete(0L, changeModel.getDeletedRvwQnaireRespWSVersion()));

    return changeDataList;
  };

  /**
   * @param pidcVersId as pidc_vers_id
   * @param wpId workpackage Id
   * @param respId responsiblity Id
   * @param copiedQnaireRespId copied Qnaire Resp Id
   * @param pidcVarId as variant id
   * @param srcPidcVersId src_pidc_vers_id
   * @return QuesRespCopyDataWrapper
   * @throws ApicWebServiceException as exception
   */
  public QuesRespCopyDataWrapper getDataForCopyQnaireRespValidation(final Long pidcVersId, final Long pidcVarId,
      final Long wpId, final Long respId, final Long copiedQnaireRespId, final Long srcPidcVersId)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_RETREIVE_DATA_FOR_COPY_VALIDATION)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ID, pidcVersId)
        .queryParam(WsCommonConstants.RWS_QP_VARIANT_ID, pidcVarId).queryParam(WsCommonConstants.RWS_QP_WP_ID, wpId)
        .queryParam(WsCommonConstants.RWS_A2L_RESP_ID, respId)
        .queryParam(WsCommonConstants.RVW_QNAIRE_RESP_ID, copiedQnaireRespId)
        .queryParam(WsCommonConstants.RWS_QP_SRC_PIDC_VERS_ID, srcPidcVersId);
    return get(wsTarget, QuesRespCopyDataWrapper.class);
  }

  /**
   * Service to create the copied questionnaire response in the selected destination workpackage
   *
   * @param qnaireRespCopyData input data to create qnaire response
   * @return RvwQnaireRespChangeModel
   * @throws ApicWebServiceException exception in web service call
   */
  public RvwQnaireRespChangeModel createQnaireResp(final QnaireRespCopyData qnaireRespCopyData)
      throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_COPY_QNAIRE_RESP_TO_DEST_WP);
    RvwQnaireRespChangeModel rvwQnaireRespChangeData =
        post(wsTarget, qnaireRespCopyData, RvwQnaireRespChangeModel.class);
    rvwQnaireRespChangeData.setDestGeneralQuesRespBeforeCopy(qnaireRespCopyData.getDestGeneralQuesResp());

    Collection<ChangeData<IModel>> newDataModelSet =
        ModelParser.getChangeData(rvwQnaireRespChangeData, QNAIRE_RESPONSE_CREATION_MAPPER);
    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(newDataModelSet));

    displayMessage("Questionnaire Response [Name = " + rvwQnaireRespChangeData.getPastedRvwQnaireResp().getName() +
        "] copied successfully.");

    return rvwQnaireRespChangeData;
  }

  /**
   * Service to update the existing qnaire resp in destination with copied questionnaire response
   *
   * @param qnaireRespCopyData input data to create qnaire response
   * @return RvwQnaireRespChangeModel
   * @throws ApicWebServiceException exception in web service call
   */
  public RvwQnaireRespChangeModel updateQnaireResp(final QnaireRespCopyData qnaireRespCopyData)
      throws ApicWebServiceException {


    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_UPDATE_QNAIRE_RESP_IN_DEST_WP);
    RvwQnaireRespChangeModel rvwQnaireRespChangeData =
        put(wsTarget, qnaireRespCopyData, RvwQnaireRespChangeModel.class);
    rvwQnaireRespChangeData.setExistingTargetRvwQnaireResp(qnaireRespCopyData.getExistingTargetQnaireResp());
    rvwQnaireRespChangeData.setDestGeneralQuesRespBeforeCopy(qnaireRespCopyData.getDestGeneralQuesResp());

    Collection<ChangeData<IModel>> newDataModelSet =
        ModelParser.getChangeData(rvwQnaireRespChangeData, QNAIRE_RESPONSE_UPDATION_MAPPER);
    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(newDataModelSet));

    displayMessage("Questionnaire Response copied successfully. [Name =" +
        rvwQnaireRespChangeData.getPastedRvwQnaireResp().getName() + "]");

    return rvwQnaireRespChangeData;
  }

  /**
   * Service to update the qnaire version if the qnaire active version is not equal with the version used for answering
   * qnaire's
   *
   * @param qnaireRespUpdateData input data to create qnaire response
   * @return RvwQnaireRespChangeModel
   * @throws ApicWebServiceException exception in web service call
   */
  public RvwQnaireRespChangeModel updateQnaireRespVersion(final QnaireRespActionData qnaireRespUpdateData)
      throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_UPDATE_QNAIRE_VERSION);
    RvwQnaireRespChangeModel rvwQnaireRespChangeData =
        put(wsTarget, qnaireRespUpdateData, RvwQnaireRespChangeModel.class);

    rvwQnaireRespChangeData.setExistingTargetRvwQnaireResp(qnaireRespUpdateData.getExistingTargetQnaireResp());

    Collection<ChangeData<IModel>> newDataModelSet =
        ModelParser.getChangeData(rvwQnaireRespChangeData, QNAIRE_RESPONSE_VERSION_UPDATION_MAPPER);
    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(newDataModelSet));

    displayMessage("Questionnaire Response version updated successfully. [Name =" +
        rvwQnaireRespChangeData.getPastedRvwQnaireResp().getName() + "]");

    return rvwQnaireRespChangeData;
  }

}
