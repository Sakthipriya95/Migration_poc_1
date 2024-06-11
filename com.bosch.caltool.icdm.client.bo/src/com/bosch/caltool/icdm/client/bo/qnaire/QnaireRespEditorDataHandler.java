/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.qnaire;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.bosch.caltool.datamodel.core.cns.CHANGE_OPERATION;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcDetailsLoader;
import com.bosch.caltool.icdm.client.bo.framework.AbstractClientDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.ChangeDataInfo;
import com.bosch.caltool.icdm.client.bo.framework.CnsUtils;
import com.bosch.caltool.icdm.client.bo.framework.ICnsApplicabilityCheckerChangeData;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.bo.qnaire.QnaireRespVersDataResolver;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetails;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetailsInput;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.QS_ASSESMENT_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.QS_STATUS_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.QUESTION_CONFIG_TYPE;
import com.bosch.caltool.icdm.model.cdr.QnaireResponseCombinedModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionResultOption;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireConstants.QUESTION_RESP_SERIES_MEASURE;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswerOpl;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponseModel;
import com.bosch.caltool.icdm.model.general.Link;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.NodeAccessDetails;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwQnaireAnswerOplServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwQnaireAnswerServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwQnaireRespVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwQnaireResponseServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.LinkServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.general.NodeAccessServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.general.UserServiceClient;

/**
 * The Class QnaireEditorDataHandler.
 *
 * @author nip4cob
 */
public class QnaireRespEditorDataHandler extends AbstractClientDataHandler {


  /**
   *
   */
  private static final String PREFIX_RESULT_COL_VAL = " with ";

  /**
   *
   */
  private static final String SUFFIX_RESULT_COL_VAL = " answer";

  /**
   * Qnaire response answer - Answer positive or neutral
   */
  public static final Long ANSWERED_POSITIVE_OR_NEUTRAL = 1L;

  /**
   * Qnaire response answer - Answer negative
   */
  public static final Long ANSWERED_NEGATIVE = -1L;

  /**
   * Qnaire response answer - Not answered yet
   */
  public static final Long ANSWERED_INCOMPLETE = 2L;

  /**
   * Qnaire response answer - answered but not allowed to finish the WP
   */
  public static final Long ANSWERED_NOT_ALLOWED_TO_FINISH_WP = 3L;

  // ICDM-1987
  /**
   * enum for columns.
   */
  public enum SortColumns {

                           /** Ques Name. */
                           SORT_QUES_NAME,

                           /** Ques Number. */
                           SORT_QUES_NUMBER,

                           /** Ques Hint. */
                           SORT_QUES_HINT,

                           /** Measurable. */
                           SORT_MEASURABLE,

                           /** Series. */
                           SORT_SERIES,

                           /** Link. */
                           SORT_LINK,

                           /** Result. */
                           SORT_RESULT,

                           /** Remark. */
                           SORT_REMARK,

                           /** Open points. */
                           SORT_OP,
                           /** Calculated Result */
                           SORT_CALU_RESULT
  }


  /**
   * The qnaire def model.
   */
  private RvwQnaireResponseModel qnaireRespModel = new RvwQnaireResponseModel();

  /**
   * The qnaire def editor data hdlr.
   */
  private QnaireDefBO mainQnaireDefBo;

  /**
   * not applicable constant.
   */
  private static final String NOT_APPL = "<NOT APPLICABLE>";

  /**
   * hashcode number.
   */
  static final int HASHCODE_PRIME = 31;

  private String qnaireLanguage;

  private Set<RvwQnaireRespVersion> qnaireVersions;

  private QnaireRespVersDataResolver quesRespdataResolver;

  private RvwQnaireRespVersion selRvwQnaireRespVersion;

  private Long currUserId;

  private QnaireRespEditorInputData qnaireRespEditorInputData;


  /**
   * @param qnaireRespId - qnaire resp id
   * @param variant - secondary variant
   */
  public QnaireRespEditorDataHandler(final long qnaireRespId, final PidcVariant variant) {
    RvwQnaireRespVersion rvwQnaireRespVersion = getQnaireRespWorkingSetVersion(qnaireRespId);
    populateDataModel(rvwQnaireRespVersion, variant);
  }

  /**
   * Instantiates a new qnaire resp editor data handler.
   *
   * @param rvwQnaireRespVersion the rvw qnaire resp version
   */
  public QnaireRespEditorDataHandler(final RvwQnaireRespVersion rvwQnaireRespVersion) {
    populateDataModel(rvwQnaireRespVersion, null);
  }

  /**
   * Instantiates a new qnaire resp editor data handler based on the given input
   *
   * @param qnaireResponseCombinedModel as input
   * @param pidcVersionWithDetails as input
   */
  public QnaireRespEditorDataHandler(final QnaireResponseCombinedModel qnaireResponseCombinedModel,
      final PidcVersionWithDetails pidcVersionWithDetails) {
    this.qnaireRespModel = qnaireResponseCombinedModel.getRvwQnaireResponseModel();
    this.mainQnaireDefBo = new QnaireDefBO(qnaireResponseCombinedModel.getQnaireDefModel(),
        qnaireResponseCombinedModel.getAllQnAttrValDepModel());
    PidcDataHandler pidcDataHandler = new PidcDataHandler();
    new PidcDetailsLoader(pidcDataHandler).loadDataModel(pidcVersionWithDetails);
    initializeAllQuestionsForExport(pidcDataHandler);
  }


  /**
   * @param rvwQnaireRespVersion as input
   * @param mappedPidcVariant linked variant
   */
  public void populateDataModel(final RvwQnaireRespVersion rvwQnaireRespVersion, final PidcVariant mappedPidcVariant) {
    loadDataModel(rvwQnaireRespVersion.getId(), mappedPidcVariant);
    this.mainQnaireDefBo = new QnaireDefBO(this.qnaireRespModel.getRvwQnrRespVersion().getQnaireVersionId(), true);

    initializeAllQuestions();
    // to calculate and update the qnaire resp vers status, based on visibility of questions
    // Because visibility of questions will also be changed due to attribute value dependency. At that time status
    // should be recalculated and updated
    setSelRvwQnaireRespVersion(updateQnaireRespVersStatus());
    // loading all the version in qnaireVersions set for the below refreshQRespVerDataModel method
    getQnaireVersions(rvwQnaireRespVersion.getQnaireRespId());
    // to refresh the maps manually, because cns action will not be triggered as the editor is not yet opened
    refreshQRespVerDataModel(getSelRvwQnaireRespVersion(), CHANGE_OPERATION.UPDATE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void registerForCns() {
    registerCnsQnaireResponse();
    registerCnsQnaireRespVersion();
    registerCnsRvwQnaireAnswers();
    registerCnsRvwQnaireAnsOpl();
    registerCnsRvwQnaireAnsLinks();
    registerCnsPidcAccess();
  }


  /**
   * Register cns for questionnaire response object
   */
  private void registerCnsQnaireResponse() {

    registerCnsChecker(MODEL_TYPE.RVW_QNAIRE_RESPONSE,
        chData -> CommonUtils.isEqual(((RvwQnaireResponse) CnsUtils.getModel(chData)).getId(),
            this.qnaireRespModel.getRvwQnrResponse().getId()));

    registerCnsActionLocal(
        chData -> refreshQResponseDataModel((RvwQnaireResponse) chData.getNewData(), chData.getChangeType()),
        MODEL_TYPE.RVW_QNAIRE_RESPONSE);

    registerCnsAction(this::refreshQnaireResponseRemote, MODEL_TYPE.RVW_QNAIRE_RESPONSE);
  }

  /**
   * Cns for questionnaire response object from remote chnage
   *
   * @param chDataInfoMap
   */
  private void refreshQnaireResponseRemote(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      try {
        RvwQnaireResponse rvwQnaireResponse = new RvwQnaireResponseServiceClient().getById(data.getObjId());
        refreshQResponseDataModel(rvwQnaireResponse, data.getChangeType());
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * Refresh questionnaire response object in data model
   *
   * @param newData
   * @param changeType
   */
  private void refreshQResponseDataModel(final RvwQnaireResponse rvwQnaireResponse, final CHANGE_OPERATION changeType) {

    if (changeType == CHANGE_OPERATION.UPDATE) {
      this.qnaireRespModel.setRvwQnrResponse(rvwQnaireResponse);
    }
  }

  /**
   * Register cns for questionnaire answer links
   */
  private void registerCnsRvwQnaireAnsLinks() {

    registerCnsChecker(MODEL_TYPE.LINK, isValidChangeLink());

    registerCnsActionLocal(chData -> refreshQAnsLinkDataModel(
        chData.getChangeType() == CHANGE_OPERATION.DELETE ? (Link) chData.getOldData() : (Link) chData.getNewData(),
        chData.getChangeType()), MODEL_TYPE.LINK);

    registerCnsAction(this::refreshQnaireAnsLinksRemote, MODEL_TYPE.LINK);

  }

  /**
   * Method to check whether change in questionnaire answer link is for current qnaire response version
   *
   * @return
   */
  private ICnsApplicabilityCheckerChangeData isValidChangeLink() {
    return chData -> {
      Link chDataLink = (Link) CnsUtils.getModel(chData);
      if (CommonUtils.isEqual(chDataLink.getNodeType(), MODEL_TYPE.RVW_QNAIRE_ANS.getTypeCode())) {
        return this.qnaireRespModel.getRvwQnrAnswrMap().containsKey(chDataLink.getNodeId());
      }
      return false;
    };
  }

  /**
   * cns for qnaire answer link object from remote change
   *
   * @param chDataInfoMap
   */
  private void refreshQnaireAnsLinksRemote(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      if (data.getChangeType() == CHANGE_OPERATION.DELETE) {
        refreshQAnsLinkDataModel((Link) data.getRemovedData(), data.getChangeType());
      }
      else {
        try {
          Link rvwQnaireAnsLink = new LinkServiceClient().getById(data.getObjId());
          refreshQAnsLinkDataModel(rvwQnaireAnsLink, data.getChangeType());
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }
      }
    }
  }

  /**
   * Method to refersh qnaire answer link object in data model
   *
   * @param rvwQnaireAnslink
   * @param changeType
   */
  private void refreshQAnsLinkDataModel(final Link rvwQnaireAnslink, final CHANGE_OPERATION changeType) {

    if ((changeType == CHANGE_OPERATION.CREATE) || (changeType == CHANGE_OPERATION.UPDATE)) {
      this.qnaireRespModel.getLinksMap()
          .computeIfAbsent(rvwQnaireAnslink.getNodeId(), newMap -> new HashMap<Long, Link>())
          .put(rvwQnaireAnslink.getId(), rvwQnaireAnslink);
    }
    else {
      this.qnaireRespModel.getLinksMap().get(rvwQnaireAnslink.getNodeId()).remove(rvwQnaireAnslink.getId());
    }
  }

  /**
   * Method to register cns for changes in review questionnaire answer
   */
  private void registerCnsRvwQnaireAnswers() {

    registerCnsChecker(MODEL_TYPE.RVW_QNAIRE_ANS,
        chData -> CommonUtils.isEqual(((RvwQnaireAnswer) CnsUtils.getModel(chData)).getQnaireRespVersId(),
            this.qnaireRespModel.getRvwQnrRespVersion().getId()));

    registerCnsActionLocal(
        chData -> refreshQAnsDataModel((RvwQnaireAnswer) chData.getNewData(), chData.getChangeType()),
        MODEL_TYPE.RVW_QNAIRE_ANS);

    registerCnsAction(this::refreshQnaireAnswersRemote, MODEL_TYPE.RVW_QNAIRE_ANS);
  }

  /**
   * Method to register cns for changes in access rights of pidc for current user
   */
  private void registerCnsPidcAccess() {

    try {
      this.currUserId = new CurrentUserBO().getUserID();
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().debug(e.getMessage(), Activator.PLUGIN_ID);
    }

    registerCnsCheckerForNodeAccess(MODEL_TYPE.PIDC, this::getPidc);

    registerCnsActionLocal(
        chData -> refreshQAccessDataModel(chData.getChangeType() == CHANGE_OPERATION.DELETE
            ? (NodeAccess) chData.getOldData() : (NodeAccess) chData.getNewData(), chData.getChangeType()),
        MODEL_TYPE.NODE_ACCESS);

    registerCnsAction(this::refreshQnaireAccessRemote, MODEL_TYPE.NODE_ACCESS);
  }

  /**
   * Method to get pidc for cns checker
   */
  private Pidc getPidc() {
    return this.qnaireRespModel.getPidc();
  }

  /**
   * Method to register cns for changes in review questionnaire answer open points
   */
  private void registerCnsRvwQnaireAnsOpl() {

    registerCnsChecker(MODEL_TYPE.RVW_QNAIRE_ANSWER_OPL, chData -> this.qnaireRespModel.getOpenPointsMap()
        .containsKey(((RvwQnaireAnswerOpl) CnsUtils.getModel(chData)).getRvwAnswerId()));

    registerCnsActionLocal(chData -> refreshQAnsOplDataModel(chData.getChangeType() == CHANGE_OPERATION.DELETE
        ? (RvwQnaireAnswerOpl) chData.getOldData() : (RvwQnaireAnswerOpl) chData.getNewData(), chData.getChangeType()),
        MODEL_TYPE.RVW_QNAIRE_ANSWER_OPL);

    registerCnsAction(this::refreshQnaireAnswersOplRemote, MODEL_TYPE.RVW_QNAIRE_ANSWER_OPL);
  }

  /**
   * Method to register cns for changes in review questionnaire response version
   */
  private void registerCnsQnaireRespVersion() {

    registerCnsChecker(MODEL_TYPE.RVW_QNAIRE_RESP_VERSION,
        chData -> CommonUtils.isEqual(((RvwQnaireRespVersion) CnsUtils.getModel(chData)).getQnaireRespId(),
            this.qnaireRespModel.getRvwQnrResponse().getId()));

    registerCnsActionLocal(chData -> refreshQRespVerDataModel(chData.getChangeType() == CHANGE_OPERATION.DELETE
        ? (RvwQnaireRespVersion) chData.getOldData() : (RvwQnaireRespVersion) chData.getNewData(),
        chData.getChangeType()), MODEL_TYPE.RVW_QNAIRE_RESP_VERSION);

    registerCnsAction(this::refreshQnaireRespVersionsRemote, MODEL_TYPE.RVW_QNAIRE_RESP_VERSION);
  }

  /**
   * Method to refresh the data model for changes in access rights of pidc
   *
   * @param nodeAccess
   * @param change_OPERATION
   */
  private void refreshQAccessDataModel(final NodeAccess nodeAccess, final CHANGE_OPERATION changeType) {

    if (changeType == CHANGE_OPERATION.DELETE) {
      this.qnaireRespModel.setModifiable(isQnaireRespModifiable(false));
    }
    else {
      this.qnaireRespModel.setModifiable(isQnaireRespModifiable(nodeAccess.isOwner()));
    }
  }

  /**
   * Method to check if a questionnaire response is editable based on owner and a2l responsibility
   *
   * @param nodeAccess
   * @param owner
   * @return
   */
  private boolean isQnaireRespModifiable(final boolean isOwner) {
    return isOwner ||
        ((this.qnaireRespModel.getQnaireRespA2lResponsibility().getUserId() != null) &&
            CommonUtils.isEqual(this.qnaireRespModel.getQnaireRespA2lResponsibility().getUserId(), this.currUserId)) ||
        (this.qnaireRespModel.getQnaireRespA2lResponsibility().getUserId() == null);
  }

  /**
   * Method to refresh questionnaire response for changes in acces rights (remote)
   */
  private void refreshQnaireAccessRemote(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      try {
        NodeAccessDetails nodeAccessDetails = new NodeAccessServiceClient().getNodeAccessDetailsByNode(MODEL_TYPE.PIDC,
            this.qnaireRespModel.getPidcVersion().getPidcId());
        if (nodeAccessDetails.getNodeAccessMap().containsKey(this.qnaireRespModel.getPidcVersion().getPidcId())) {
          Optional<NodeAccess> nodeAccessOpt =
              nodeAccessDetails.getNodeAccessMap().get(this.qnaireRespModel.getPidcVersion().getPidcId()).stream()
                  .filter(access -> CommonUtils.isEqual(this.currUserId, access.getUserId())).findFirst();
          if (nodeAccessOpt.isPresent()) {
            refreshQAccessDataModel(nodeAccessOpt.get(), data.getChangeType());
          }
          else {
            this.qnaireRespModel.setModifiable(isQnaireRespModifiable(false));
          }
        }
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * Method to refresh questionnaire response for changes in questionnaire response version (remote)
   */
  private void refreshQnaireRespVersionsRemote(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      RvwQnaireRespVersion rvwQnaireRespVersion;
      try {
        if (data.getChangeType() == CHANGE_OPERATION.DELETE) {
          rvwQnaireRespVersion = (RvwQnaireRespVersion) data.getRemovedData();
        }
        else {
          rvwQnaireRespVersion = new RvwQnaireRespVersionServiceClient().getById(data.getObjId());
        }
        refreshQRespVerDataModel(rvwQnaireRespVersion, data.getChangeType());
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * Method to refresh qnaire response version object in data model
   *
   * @param rvwQnaireRespVersion
   * @param change_OPERATION
   */
  private void refreshQRespVerDataModel(final RvwQnaireRespVersion rvwQnaireRespVersion,
      final CHANGE_OPERATION changeType) {

    if ((changeType == CHANGE_OPERATION.CREATE) && CommonUtils.isNotEmpty(this.qnaireVersions)) {
      this.qnaireVersions.add(rvwQnaireRespVersion);
    }
    else if ((changeType == CHANGE_OPERATION.UPDATE)) {
      if (rvwQnaireRespVersion.getId().equals(this.qnaireRespModel.getRvwQnrRespVersion().getId())) {
        this.qnaireRespModel.setRvwQnrRespVersion(rvwQnaireRespVersion);
      }
      this.qnaireVersions.remove(rvwQnaireRespVersion);
      this.qnaireVersions.add(rvwQnaireRespVersion);
    }
    else if (changeType == CHANGE_OPERATION.DELETE) {
      this.qnaireVersions.remove(rvwQnaireRespVersion);
    }
  }

  /**
   * Method to refresh questionnaire response for changes in questionnaire answers open points(remote)
   */
  private void refreshQnaireAnswersOplRemote(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      RvwQnaireAnswerOpl rvwQnaireAnsOpl;
      try {
        if (data.getChangeType() == CHANGE_OPERATION.DELETE) {
          rvwQnaireAnsOpl = (RvwQnaireAnswerOpl) data.getRemovedData();
        }
        else {
          rvwQnaireAnsOpl = new RvwQnaireAnswerOplServiceClient().getById(data.getObjId());
        }
        refreshQAnsOplDataModel(rvwQnaireAnsOpl, data.getChangeType());
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }
  }


  /**
   * Metod to refresh qnire answer opl bject in data model
   *
   * @param rvwQnaireRespVersion
   * @param change_OPERATION
   */
  private void refreshQAnsOplDataModel(final RvwQnaireAnswerOpl rvwQnaireAnswerOpl, final CHANGE_OPERATION changeType) {

    if ((changeType == CHANGE_OPERATION.CREATE) || (changeType == CHANGE_OPERATION.UPDATE)) {
      this.qnaireRespModel.getOpenPointsMap()
          .computeIfAbsent(rvwQnaireAnswerOpl.getRvwAnswerId(), newMap -> new HashMap<Long, RvwQnaireAnswerOpl>())
          .put(rvwQnaireAnswerOpl.getId(), rvwQnaireAnswerOpl);
    }
    else {
      this.qnaireRespModel.getOpenPointsMap().get(rvwQnaireAnswerOpl.getRvwAnswerId())
          .remove(rvwQnaireAnswerOpl.getId());
    }
  }


  /**
   * Method to refresh questionnaire response for changes in questionnaire answers (remote)
   */
  private void refreshQnaireAnswersRemote(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      try {
        RvwQnaireAnswer rvwQnaireAnswer = new RvwQnaireAnswerServiceClient().getById(data.getObjId());
        refreshQAnsDataModel(rvwQnaireAnswer, data.getChangeType());
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * Method to refresh qnaire answer object in data model
   *
   * @param rvwQnaireRespVersion
   * @param change_OPERATION
   */
  private void refreshQAnsDataModel(final RvwQnaireAnswer rvwQnaireAnswer, final CHANGE_OPERATION changeType) {
    if ((changeType == CHANGE_OPERATION.CREATE) ||
        ((changeType == CHANGE_OPERATION.UPDATE) && getAllQuestionMap().containsKey(rvwQnaireAnswer.getQuestionId()) &&
            this.qnaireRespModel.getRvwQnrAnswrMap().containsKey(rvwQnaireAnswer.getId()))) {
      getAllQuestionMap().put(rvwQnaireAnswer.getQuestionId(), rvwQnaireAnswer);
      this.qnaireRespModel.getRvwQnrAnswrMap().put(rvwQnaireAnswer.getId(), rvwQnaireAnswer);
    }
  }

  /**
   * Initialize data.
   *
   * @param qnaireRespVersionId the qnaire resp version id
   */
  private void loadDataModel(final long qnaireRespVersionId, final PidcVariant mappedPidcVariant) {
    try {
      this.qnaireRespModel = new RvwQnaireResponseServiceClient().getAllMappingModel(qnaireRespVersionId);
      fillQnaireRespEditorInputData(mappedPidcVariant);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  private void fillQnaireRespEditorInputData(final PidcVariant mappedPidcVariant) {
    QnaireRespEditorInputData qnaireRespEdrInputData = new QnaireRespEditorInputData();
    qnaireRespEdrInputData.setPidcVariant(this.qnaireRespModel.getPidcVariant());
    qnaireRespEdrInputData.setSecondaryPidcVariant(mappedPidcVariant);
    qnaireRespEdrInputData.setPidcVersion(this.qnaireRespModel.getPidcVersion());
    qnaireRespEdrInputData.setRvwQnaireRespVersion(this.qnaireRespModel.getRvwQnrRespVersion());
    qnaireRespEdrInputData.setRvwQnaireResponse(this.qnaireRespModel.getRvwQnrResponse());
    setQnaireRespEditorInputData(qnaireRespEdrInputData);
  }

  /**
   * Gets the qnaire def editor data hdlr.
   *
   * @return the qnaireDefEditorDataHdlr
   */
  public QnaireDefBO getQnaireDefHandler() {
    return this.mainQnaireDefBo;
  }

  /**
   * initialise all questions
   */
  private void initializeAllQuestions() {

    QuesRespDataProviderClient quesRespDataProvider = new QuesRespDataProviderClient(this.mainQnaireDefBo,
        this.qnaireRespModel.getPidcVersion(), this.qnaireRespModel.getPidcVariant(),
        createPidcDataHandler(this.qnaireRespModel.getPidcVersion().getId()));
    this.quesRespdataResolver = new QnaireRespVersDataResolver(quesRespDataProvider);
    this.quesRespdataResolver.loadMainQuestions(this.qnaireRespModel);
  }

  /**
   * initialise all questions for combined excel export
   */
  private void initializeAllQuestionsForExport(final PidcDataHandler createPidcDataHandler) {
    QuesRespDataProviderClient quesRespDataProvider = new QuesRespDataProviderClient(this.mainQnaireDefBo,
        this.qnaireRespModel.getPidcVersion(), this.qnaireRespModel.getPidcVariant(), createPidcDataHandler);
    this.quesRespdataResolver = new QnaireRespVersDataResolver(quesRespDataProvider);
    this.quesRespdataResolver.loadMainQuestions(this.qnaireRespModel);
  }

  /**
   * @param qnaireResponse as Questionnaire Response
   * @return as RvwQnaireResponse
   * @throws ApicWebServiceException as exception
   */
  public RvwQnaireResponse updateRvwQnaireResponse(final RvwQnaireResponse qnaireResponse)
      throws ApicWebServiceException {
    return new RvwQnaireResponseServiceClient().update(qnaireResponse);
  }


  /**
   * Gets the qnaire def handler.
   *
   * @return the qnaire def handler
   */
  public QnaireDefBO getQnaireDefBo() {
    return this.mainQnaireDefBo;
  }

  /**
   * Gets the question.
   *
   * @param questionId the question id
   * @return the Question associated with this QA
   */
  public Question getQuestion(final long questionId) {
    if (!getQnaireDefBo().hasQuestion(questionId)) {
      return null;
    }
    return getQnaireDefBo().getQuestion(questionId);
  }


  /**
   * @param questionId as input
   * @return Set<QuestionResultOption>
   */
  public Map<Long, QuestionResultOption> getQuestionResultOptionsMap(final Long questionId) {
    if (!getQnaireDefBo().hasQuestion(questionId)) {
      return new HashMap<>();
    }
    return getQnaireDefBo().getQnaireDefModel().getQuesWithQuestionResultOptionsMap().get(questionId);
  }

  /**
   * @param questionId as input
   * @return a set of ques Result options
   */
  public Set<String> getQuestionResultOptionsForComboBox(final Long questionId) {
    Set<String> qResultOptions = new HashSet<>();
    for (QuestionResultOption questionResultOption : getQuestionResultOptionsMap(questionId).values()) {
      qResultOptions.add(questionResultOption.getQResultName());
    }
    return qResultOptions;
  }


  /**
   * @param questionId as input
   * @param questResultOptId as input
   * @return resultOptionString
   */
  public String getQuestionResultOptionUIString(final Long questionId, final Long questResultOptId) {
    String resultOptionString = ApicConstants.EMPTY_STRING;
    if (getQnaireDefBo().isHeading(questionId) || isQnaireHeading(questionId)) {
      return resultOptionString;
    }
    if (null == questResultOptId) {
      return resultOptionString;
    }
    if (showResult(questionId)) {
      Map<Long, QuestionResultOption> questionResultOptionsMap = getQuestionResultOptionsMap(questionId);

      QuestionResultOption questionResultOption = questionResultOptionsMap.get(questResultOptId);
      if (null != questionResultOption) {
        resultOptionString = questionResultOption.getQResultName();
      }
    }
    return resultOptionString;
  }


  /**
   * @param resultOptName as input
   * @param questionId as input
   * @return as QuestionResultOption
   */
  public QuestionResultOption getQuestionResultOptionByResultName(final String resultOptName, final long questionId) {
    for (QuestionResultOption questionResultOption : getQuestionResultOptionsMap(questionId).values()) {
      if (questionResultOption.getQResultName().equals(resultOptName)) {
        return questionResultOption;
      }
    }
    return null;
  }

  /**
   * Generates the open point data.
   *
   * @param rvwQnaireAns the rvw qnaire ans
   * @param rvwQnaireAnswerOpl rvw qnaire ans opl object
   * @return open points display string
   * @throws ApicWebServiceException the apic web service exception
   */
  public String getOpenPointUIString(final RvwQnaireAnswer rvwQnaireAns, final RvwQnaireAnswerOpl rvwQnaireAnswerOpl)
      throws ApicWebServiceException {
    long questionId = rvwQnaireAns.getQuestionId();
    // If this is dummy answer (i.e. heading)
    if (getQnaireDefBo().isHeading(questionId) || isQnaireHeading(questionId)) {
      return "";
    }
    if (showOpenPoints(questionId)) {
      StringBuilder opPoints = new StringBuilder();
      if (rvwQnaireAnswerOpl != null) {
        generateOPString(rvwQnaireAns, rvwQnaireAnswerOpl, opPoints);
      }
      return opPoints.toString();
    }
    return NOT_APPL;
  }


  /**
   * Generates the open point data.
   *
   * @param rvwQnaireAns the rvw qnaire ans
   * @return open points display string
   * @throws ApicWebServiceException the apic web service exception
   */
  public String getOpenPointsUIString(final RvwQnaireAnswer rvwQnaireAns) throws ApicWebServiceException {
    long questionId = rvwQnaireAns.getQuestionId();
    // If this is dummy answer (i.e. heading)
    if (getQnaireDefBo().isHeading(questionId) || isQnaireHeading(questionId)) {
      return "";
    }
    if (showOpenPoints(questionId)) {
      StringBuilder opPoints = new StringBuilder();
      if (!getOpenPointsList(rvwQnaireAns).isEmpty()) {
        generateOPString(rvwQnaireAns, opPoints);
      }
      return opPoints.toString();
    }
    return NOT_APPL;
  }

  /**
   * Gets the open points UI string.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @param openPoint the open point
   * @return open points display string
   */
  public String getOpenPointsUIString(final RvwQnaireAnswer rvwQnaireAns, final RvwQnaireAnswerOpl openPoint) {
    long questionId = rvwQnaireAns.getQuestionId();
    if (getQnaireDefBo().isHeading(questionId) || isQnaireHeading(questionId)) {
      return "";
    }
    if (showOpenPoints(rvwQnaireAns.getQuestionId())) {
      return CommonUtils.isNotNull(openPoint) ? openPoint.getOpenPoints() : "";
    }
    return NOT_APPL;
  }


  /**
   * String representation of all open points of this question response.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @param opPoints the op points
   * @throws ApicWebServiceException the apic web service exception
   */
  private void generateOPString(final RvwQnaireAnswer rvwQnaireAns, final RvwQnaireAnswerOpl openPoint,
      final StringBuilder opPoints)
      throws ApicWebServiceException {
    Long questionId = rvwQnaireAns.getQuestionId();
    getOpenPoints(rvwQnaireAns, openPoint, opPoints, questionId);

  }


  /**
   * @param rvwQnaireAns
   * @param openPoint
   * @param opPoints
   * @param questionId
   * @param openPoints
   * @param oplMeasures
   * @param oplResponsible
   * @param oplDate
   * @param oplStatus
   * @throws ApicWebServiceException
   */
  private void getOpenPoints(final RvwQnaireAnswer rvwQnaireAns, final RvwQnaireAnswerOpl openPoint,
      final StringBuilder opPoints, final Long questionId)
      throws ApicWebServiceException {
    String openPoints =
        new CommonDataBO().getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_OPEN_POINTS);

    String oplMeasures =
        new CommonDataBO().getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_MEASURE);

    String oplResponsible =
        new CommonDataBO().getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_RESPONSIBLE);
    String oplDate = new CommonDataBO().getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_DATE);

    String oplStatus =
        new CommonDataBO().getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_STATUS);

    opPoints.append(openPoints).append(" : ").append(getOpenPointsUIString(rvwQnaireAns, openPoint)).append('\n');

    // Measures(optional)
    if (showMeasures(questionId)) {
      opPoints.append(oplMeasures).append(" : ").append(openPoint.getMeasure()).append('\n');
    }

    // Responsible (optional)
    if (showResponsible(questionId)) {
      opPoints.append(oplResponsible).append(" : ").append(getOplResponsible(openPoint, questionId)).append('\n');
    }

    // Completion date(optional)
    if (showCompletionDate(questionId)) {
      opPoints.append(oplDate).append(" : ").append(getOplCompletionDate(openPoint, questionId)).append('\n');
    }

    // Status
    opPoints.append(oplStatus).append(" : ").append(getOplResult(openPoint, questionId)).append('\n');
  }

  /**
   * String representation of all open points of this question response.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @param opPoints the op points
   * @return the int
   * @throws ApicWebServiceException the apic web service exception
   */
  private int generateOPString(final RvwQnaireAnswer rvwQnaireAns, final StringBuilder opPoints)
      throws ApicWebServiceException {
    int count = 1;
    for (RvwQnaireAnswerOpl openPoint : this.qnaireRespModel.getOpenPointsMap().get(rvwQnaireAns.getId()).values()) {
      Long questionId = rvwQnaireAns.getQuestionId();
      if (count > 1) {
        opPoints.append('\n');
      }
      // ICDM-2188
      getOpenPoints(rvwQnaireAns, openPoint, opPoints, questionId);
      count++;
    }
    return count;
  }


  /**
   * Gets the opl responsible.
   *
   * @param openPoint the open point
   * @param questionId the question id
   * @return the opl responsible
   */
  private String getOplResponsible(final RvwQnaireAnswerOpl openPoint, final long questionId) {
    String responsible = NOT_APPL;
    if (getQnaireDefBo().isHeading(questionId)) {
      responsible = "";
    }
    if (showResponsible(questionId)) {
      responsible = (openPoint.getResponsible() == null) && (openPoint.getResponsibleName() == null) ? ""
          : openPoint.getResponsibleName();
    }
    return responsible;
  }

  /**
   * Gets the opl completion date.
   *
   * @param openPoint the open point
   * @param questionId the question id
   * @return the opl completion date
   */
  private String getOplCompletionDate(final RvwQnaireAnswerOpl openPoint, final long questionId) {
    String completionDate = NOT_APPL;
    if (getQnaireDefBo().isHeading(questionId)) {
      completionDate = "";
    }
    if (showCompletionDate(questionId)) {
      try {
        completionDate = openPoint.getCompletionDate() == null ? ""
            : ApicUtil.formatDate(DateFormat.DATE_FORMAT_09, openPoint.getCompletionDate());
      }
      catch (ParseException e) {
        CDMLogger.getInstance().error(
            "Error in converting Open point completion date to format:" + DateFormat.DATE_FORMAT_09, e,
            Activator.PLUGIN_ID);
        completionDate = openPoint.getCompletionDate();
      }
    }
    return completionDate;
  }


  /**
   * Gets the opl result.
   *
   * @param openPoint the open point
   * @param questionId the question id
   * @return the opl result
   */
  private String getOplResult(final RvwQnaireAnswerOpl openPoint, final long questionId) {
    String result = CommonUtils.getBooleanType(openPoint.getResult()) ? CommonUtilConstants.DISPLAY_YES
        : CommonUtilConstants.DISPLAY_NO;
    if (getQnaireDefBo().isHeading(questionId)) {
      result = "";
    }
    return result;
  }

  /**
   * Gets the link UI string
   *
   * @param answer the rvw qnaire ans
   * @return links display string
   */
  public String getLinkUIString(final RvwQnaireAnswer answer) {
    long questionId = answer.getQuestionId();

    String uiLinkStr;

    // Empty if heading
    if (getQnaireDefBo().isHeading(questionId) || isQnaireHeading(questionId)) {
      uiLinkStr = "";
    }
    else if (showLinks(questionId)) {
      // Sorted URLs, one per line
      uiLinkStr = getLinks(answer).stream().map(Link::getLinkUrl).sorted().collect(Collectors.joining("\n"));
    }
    else {
      uiLinkStr = NOT_APPL;
    }

    return uiLinkStr;
  }

  /**
   * @param answer as input
   * @return calculated answers status finished / not finished
   */
  public String getCalculatedResults(final RvwQnaireAnswer answer) {
    long questionId = answer.getQuestionId();

    String calculatedResultTxt;

    // Empty if heading
    if (getQnaireDefBo().isHeading(questionId) || isQnaireHeading(questionId)) {
      calculatedResultTxt = "";
    }
    else if (checkMandatoryItemsFilled(answer)) {

      // Sorted URLs, one per line
      calculatedResultTxt = answer.getSelQnaireResultAssement() == null ? ""
          : PREFIX_RESULT_COL_VAL + QS_ASSESMENT_TYPE.getTypeByDbCode(answer.getSelQnaireResultAssement()).getUiType() +
              QnaireRespEditorDataHandler.SUFFIX_RESULT_COL_VAL;
      calculatedResultTxt = CDRConstants.FINISHED_MARKER + calculatedResultTxt;
    }
    else {
      calculatedResultTxt = CDRConstants.NOT_FINISHED_MARKER;
    }
    return calculatedResultTxt;
  }

  /**
   * to check whether all the mandaroty item for the answers is filled
   *
   * @param answer as input
   * @return true/false
   */
  public boolean checkMandatoryItemsFilled(final RvwQnaireAnswer answer) {
    boolean seriesNotValid = isSeriesMandatory(answer) && CommonUtils.isEmptyString(answer.getSeries());
    boolean measurementNotValid = isMeasurementMandatory(answer) && CommonUtils.isEmptyString(answer.getMeasurement());
    boolean linkNotValid = isLinkMandatory(answer) && CommonUtils.isNullOrEmpty(getLinks(answer));
    boolean remarksNotValid = isRemarksMandatory(answer) && CommonUtils.isEmptyString(answer.getRemark());
    boolean resultNotValid =
        isResultMandatory(answer) && CommonUtils.isEmptyString(answer.getSelQnaireResultAssement());
    boolean allItemsFilled = seriesNotValid || measurementNotValid || linkNotValid || remarksNotValid || resultNotValid;

    return !allItemsFilled;
  }


  /**
   * Gets the remark.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @return Remarks answer
   */
  public String getRemark(final RvwQnaireAnswer rvwQnaireAns) {
    long questionId = rvwQnaireAns.getQuestionId();
    if (getQnaireDefBo().isHeading(questionId)) {
      return "";
    }

    return CommonUtils.isNotNull(rvwQnaireAns.getRemark()) ? rvwQnaireAns.getRemark() : "";
  }

  /**
   * Gets the remarks UI string.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @return Remarks display string
   */
  public String getRemarksUIString(final RvwQnaireAnswer rvwQnaireAns) {
    long questionId = rvwQnaireAns.getQuestionId();
    if (getQnaireDefBo().isHeading(questionId) || isQnaireHeading(questionId)) {
      return "";
    }
    if (showRemarks(rvwQnaireAns.getQuestionId())) {
      return getRemark(rvwQnaireAns);
    }
    return NOT_APPL;
  }

  /**
   * Gets the result.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @return Result answer
   */
  public String getResult(final RvwQnaireAnswer rvwQnaireAns) {
    long questionId = rvwQnaireAns.getQuestionId();
    if (getQnaireDefBo().isHeading(questionId)) {
      return "";
    }
    return CommonUtils.isNotNull(rvwQnaireAns.getResult()) ? rvwQnaireAns.getResult() : "";
  }

  /**
   * Checks if is result positive.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @return true if result is positive
   */
  public boolean isResultPositive(final RvwQnaireAnswer rvwQnaireAns) {
    long questionId = rvwQnaireAns.getQuestionId();
    if (getQnaireDefBo().isHeading(questionId)) {
      return false;
    }
    return CDRConstants.QS_ASSESMENT_TYPE.POSITIVE.equals(QS_ASSESMENT_TYPE.getTypeByDbCode(getResult(rvwQnaireAns)));
  }

  /**
   * Checks if is result negative.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @return true if result is negative
   */
  public boolean isResultNegative(final RvwQnaireAnswer rvwQnaireAns) {
    long questionId = rvwQnaireAns.getQuestionId();
    if (getQnaireDefBo().isHeading(questionId)) {
      return false;
    }
    return CDRConstants.QS_ASSESMENT_TYPE.NEGATIVE.equals(QS_ASSESMENT_TYPE.getTypeByDbCode(getResult(rvwQnaireAns)));
  }

  /**
   * Checks if is result neutral.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @return true if result is neutral
   */
  public boolean isResultNeutral(final RvwQnaireAnswer rvwQnaireAns) {
    long questionId = rvwQnaireAns.getQuestionId();
    if (getQnaireDefBo().isHeading(questionId)) {
      return false;
    }
    return CDRConstants.QS_ASSESMENT_TYPE.NEUTRAL.equals(QS_ASSESMENT_TYPE.getTypeByDbCode(getResult(rvwQnaireAns)));
  }

  /**
   * Checks if is result not answered.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @return true if result is not answered
   */
  public boolean isResultNotAnswered(final RvwQnaireAnswer rvwQnaireAns) {
    long questionId = rvwQnaireAns.getQuestionId();
    if (getQnaireDefBo().isHeading(questionId)) {
      return false;
    }
    return CommonUtils.isEmptyString(getResult(rvwQnaireAns));
  }

  /**
   * Gets the series.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @return Series
   */
  public String getSeries(final RvwQnaireAnswer rvwQnaireAns) {
    long questionId = rvwQnaireAns.getQuestionId();
    if (getQnaireDefBo().isHeading(questionId)) {
      return "";
    }
    return CommonUtils.isNotNull(rvwQnaireAns.getSeries()) ? rvwQnaireAns.getSeries() : "";
  }


  /**
   * Checks if is series.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @return true is series
   */
  public boolean isSeries(final RvwQnaireAnswer rvwQnaireAns) {
    return CommonUtils.getBooleanType(getSeries(rvwQnaireAns));
  }

  /**
   * Gets the checks if is series UI string.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @return is series display string
   */
  public String getSeriesUIString(final RvwQnaireAnswer rvwQnaireAns) {
    long questionId = rvwQnaireAns.getQuestionId();
    if (getQnaireDefBo().isHeading(questionId) || isQnaireHeading(questionId)) {
      return "";
    }
    if (showSeriesMaturity(rvwQnaireAns.getQuestionId())) {
      return QUESTION_RESP_SERIES_MEASURE.getType(getSeries(rvwQnaireAns)).getUiType();
    }
    return NOT_APPL;
  }


  /**
   * Checks if is measurement.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @return true is measurement
   */
  public boolean isMeasurement(final RvwQnaireAnswer rvwQnaireAns) {
    return CommonUtils.getBooleanType(getMeasurement(rvwQnaireAns));
  }

  /**
   * Gets the checks if is measurement UI string.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @return if measurement display string
   */
  public String getMeasurementUIString(final RvwQnaireAnswer rvwQnaireAns) {
    long questionId = rvwQnaireAns.getQuestionId();
    if (getQnaireDefBo().isHeading(questionId) || isQnaireHeading(questionId)) {
      return "";
    }
    if (showMeasurement(questionId)) {
      return QUESTION_RESP_SERIES_MEASURE.getType(getMeasurement(rvwQnaireAns)).getUiType();
    }
    return NOT_APPL;
  }

  /**
   * @param questionId
   * @return
   */
  private boolean isQnaireHeading(final long questionId) {
    return (questionId == this.mainQnaireDefBo.getQnaireVersion().getId());
  }


  /**
   * Gets the measurement.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public String getMeasurement(final RvwQnaireAnswer rvwQnaireAns) {
    long questionId = rvwQnaireAns.getQuestionId();
    if (getQnaireDefBo().isHeading(questionId)) {
      return NOT_APPL;
    }
    return rvwQnaireAns.getMeasurement();
  }


  /**
   * Checks if is filled.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @return true, if response object is available in database, false if this object is a dummy object for the question
   *         object
   */
  public boolean isFilled(final RvwQnaireAnswer rvwQnaireAns) {
    return this.qnaireRespModel.getRvwQnrAnswrMap().containsKey(rvwQnaireAns.getId());
  }

  /**
   * Show series maturity.
   *
   * @param questionId the question id
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public boolean showSeriesMaturity(final long questionId) {
    return !getQnaireDefBo().isHeading(questionId) &&
        CommonUtils.getBooleanType(getQnaireDefBo().getQnaireVersion().getSeriesRelevantFlag()) &&
        ((getQnaireDefBo().getSeries(questionId) == QUESTION_CONFIG_TYPE.MANDATORY) ||
            (getQnaireDefBo().getSeries(questionId) == QUESTION_CONFIG_TYPE.OPTIONAL));
  }

  /**
   * Show gen qnaire series maturity.
   *
   * @return true if relevancy in questionnaire
   */
  public boolean showQnaireVersSeriesMaturity() {
    return CommonUtils.getBooleanType(this.mainQnaireDefBo.getQnaireVersion().getSeriesRelevantFlag());
  }

  /**
   * Show measurement.
   *
   * @param questionId the question id
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public boolean showMeasurement(final long questionId) {
    return !getQnaireDefBo().isHeading(questionId) &&
        CommonUtils.getBooleanType(getQnaireDefBo().getQnaireVersion().getMeasurementRelevantFlag()) &&
        ((getQnaireDefBo().getMeasurement(questionId) == QUESTION_CONFIG_TYPE.MANDATORY) ||
            (getQnaireDefBo().getMeasurement(questionId) == QUESTION_CONFIG_TYPE.OPTIONAL));
  }

  /**
   * Show gen qnaire measurement.
   *
   * @return true if relevancy in questionnaire
   */
  public boolean showQnaireVersMeasurement() {
    return CommonUtils.getBooleanType(this.mainQnaireDefBo.getQnaireVersion().getMeasurementRelevantFlag());
  }

  /**
   * Show links.
   *
   * @param questionId the question id
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public boolean showLinks(final long questionId) {
    return !getQnaireDefBo().isHeading(questionId) &&
        CommonUtils.getBooleanType(getQnaireDefBo().getQnaireVersion().getLinkRelevantFlag()) &&
        ((getQnaireDefBo().getLink(questionId) == QUESTION_CONFIG_TYPE.MANDATORY) ||
            (getQnaireDefBo().getLink(questionId) == QUESTION_CONFIG_TYPE.OPTIONAL));
  }

  /**
   * Show gen qnaire links.
   *
   * @return true if relevancy in questionnaire
   */
  public boolean showQnaireVersLinks() {
    return CommonUtils.getBooleanType(this.mainQnaireDefBo.getQnaireVersion().getLinkRelevantFlag());
  }

  /**
   * Show open points.
   *
   * @param questionId the question id
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public boolean showOpenPoints(final long questionId) {
    return !getQnaireDefBo().isHeading(questionId) &&
        CommonUtils.getBooleanType(getQnaireDefBo().getQnaireVersion().getOpenPointsRelevantFlag()) &&
        ((getQnaireDefBo().getOpenPoints(questionId) == QUESTION_CONFIG_TYPE.MANDATORY) ||
            (getQnaireDefBo().getOpenPoints(questionId) == QUESTION_CONFIG_TYPE.OPTIONAL));
  }

  /**
   * Show gen qnaire open points.
   *
   * @return true if relevancy in questionnaire
   */
  public boolean showQnaireVersOpenPoints() {
    return CommonUtils.getBooleanType(this.mainQnaireDefBo.getQnaireVersion().getOpenPointsRelevantFlag());
  }

  /**
   * Show measures.
   *
   * @param questionId the question id
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public boolean showMeasures(final long questionId) {
    return !getQnaireDefBo().isHeading(questionId) &&
        CommonUtils.getBooleanType(getQnaireDefBo().getQnaireVersion().getMeasureRelaventFlag()) &&
        ((getQnaireDefBo().getMeasure(questionId) == QUESTION_CONFIG_TYPE.MANDATORY) ||
            (getQnaireDefBo().getMeasure(questionId) == QUESTION_CONFIG_TYPE.OPTIONAL));
  }

  /**
   * Show qnaire measures.
   *
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public boolean showQnaireVersMeasures() {
    return CommonUtils.getBooleanType(getQnaireDefBo().getQnaireVersion().getMeasureRelaventFlag());
  }

  /**
   * Show completion date.
   *
   * @param questionId the question id
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public boolean showCompletionDate(final long questionId) {
    return !getQnaireDefBo().isHeading(questionId) &&
        CommonUtils.getBooleanType(getQnaireDefBo().getQnaireVersion().getCompletionDateRelaventFlag()) &&
        ((getQnaireDefBo().getCompletionDate(questionId) == QUESTION_CONFIG_TYPE.MANDATORY) ||
            (getQnaireDefBo().getCompletionDate(questionId) == QUESTION_CONFIG_TYPE.OPTIONAL));
  }

  /**
   * Show Qnaire completion date.
   *
   * @return true if relevancy in questionnaire
   */
  public boolean showQnaireVersCompletionDate() {
    return CommonUtils.getBooleanType(getQnaireDefBo().getQnaireVersion().getCompletionDateRelaventFlag());
  }

  /**
   * Show responsible.
   *
   * @param questionId the question id
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public boolean showResponsible(final long questionId) {
    return !getQnaireDefBo().isHeading(questionId) &&
        CommonUtils.getBooleanType(getQnaireDefBo().getQnaireVersion().getResponsibleRelaventFlag()) &&
        ((getQnaireDefBo().getResponsible(questionId) == QUESTION_CONFIG_TYPE.MANDATORY) ||
            (getQnaireDefBo().getResponsible(questionId) == QUESTION_CONFIG_TYPE.OPTIONAL));
  }

  /**
   * Show qnaire responsible.
   *
   * @return true if relevancy in questionnaire
   */
  public boolean showQnaireVersResponsible() {
    return CommonUtils.getBooleanType(getQnaireDefBo().getQnaireVersion().getResponsibleRelaventFlag());
  }

  /**
   * Show remarks.
   *
   * @param questionId the question id
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public boolean showRemarks(final long questionId) {
    return !getQnaireDefBo().isHeading(questionId) &&
        CommonUtils.getBooleanType(getQnaireDefBo().getQnaireVersion().getRemarkRelevantFlag()) &&
        ((getQnaireDefBo().getRemark(questionId) == QUESTION_CONFIG_TYPE.MANDATORY) ||
            (getQnaireDefBo().getRemark(questionId) == QUESTION_CONFIG_TYPE.OPTIONAL));

  }

  /**
   * Show gen qnaire remarks.
   *
   * @return true if relevancy in questionnaire
   */
  public boolean showQnaireVersRemarks() {
    return CommonUtils.getBooleanType(this.mainQnaireDefBo.getQnaireVersion().getRemarkRelevantFlag());
  }

  /**
   * Show result.
   *
   * @param questionId the question id
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public boolean showResult(final long questionId) {
    return !getQnaireDefBo().isHeading(questionId) &&
        CommonUtils.getBooleanType(getQnaireDefBo().getQnaireVersion().getResultRelevantFlag()) &&
        ((getQnaireDefBo().getResult(questionId) == QUESTION_CONFIG_TYPE.MANDATORY) ||
            (getQnaireDefBo().getResult(questionId) == QUESTION_CONFIG_TYPE.OPTIONAL));
  }

  /**
   * Show result.
   *
   * @param questionId the question id
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public boolean checkResultIsMandatory(final long questionId) {
    return !getQnaireDefBo().isHeading(questionId) &&
        CommonUtils.getBooleanType(getQnaireDefBo().getQnaireVersion().getResultRelevantFlag()) &&
        (getQnaireDefBo().getResult(questionId) == QUESTION_CONFIG_TYPE.MANDATORY);
  }

  /**
   * Show gen qnaire result.
   *
   * @return true if relevancy in questionnaire
   */
  public boolean showQnaireVersResult() {
    return CommonUtils.getBooleanType(this.mainQnaireDefBo.getQnaireVersion().getResultRelevantFlag());
  }

  /**
   * Parses through the CDRResults applicable to this {@link RvwQnaireResponse}'s {@link QuestionnaireVersion}, if a
   * match is found ,true is returned.
   *
   * @return boolean
   */
  public boolean isModifiable() {
    return /* userBO.hasApicWriteAccess() || */ this.qnaireRespModel.isModifiable() &&
        !this.qnaireRespModel.getRvwQnrResponse().isDeletedFlag();
  }


  /**
   * @return true if the user has OWNER access to the PIDC
   * @throws ApicWebServiceException as Exception
   */
  public boolean isPidcOwner() throws ApicWebServiceException {
    return new CurrentUserBO().hasNodeOwnerAccess(this.qnaireRespModel.getPidcVersion().getPidcId());
  }

  /**
   * @param userName as Input
   * @return username asd LastName , FirstName and Department Name
   * @throws ApicWebServiceException as Exception
   */
  public String getLastReviewedUser(final String userName) throws ApicWebServiceException {
    User apicUserByUsername = new UserServiceClient().getApicUserByUsername(userName);

    StringBuilder builder = new StringBuilder();

    builder.append(apicUserByUsername.getLastName());
    builder.append(" ");
    builder.append(apicUserByUsername.getFirstName());
    builder.append("(" + apicUserByUsername.getDepartment() + ")");

    return builder.toString();
  }

  /**
   * Compare to.
   *
   * @param rvwQnAns1 the rvw qn ans 1
   * @param rvwQnAns2 the rvw qn ans 2
   * @return the int
   */
  public int compareTo(final RvwQnaireAnswer rvwQnAns1, final RvwQnaireAnswer rvwQnAns2) {
    // ICDM-2155
    // Uses similiar comparsion as questions
    // Cannot reuse the methods, since response contains questions from general questionnaire also
    int compResult = 0;
    if ((null != rvwQnAns1) && (null != rvwQnAns2)) {
      compResult = ApicUtil.compare(getPaddedQuestionNumber(rvwQnAns1.getQuestionId()),
          getPaddedQuestionNumber(rvwQnAns2.getQuestionId()));
      if (compResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
        return ApicUtil.compare(rvwQnAns1.getId(), rvwQnAns2.getId());
      }
    }
    return compResult;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }


  /**
   * Find the padded question number for the given question.
   *
   * @param questionId the question id
   * @return the question number with '0' padded at each level to the width of 9
   */
  // ICDM-2155
  public String getPaddedQuestionNumber(final long questionId) {
    String padQn = (getQnaireDefBo() == this.mainQnaireDefBo ? "M" : "G") + String.format("%09d", getQNum(questionId));

    Question currQstn = getQuestion(questionId);
    if (currQstn != null) {
      Question parentQstn = getQnaireDefBo().getQuestion(currQstn.getParentQId());
      if (null == parentQstn) {
        return padQn;
      }
      return getPaddedQuestionNumber(parentQstn.getId()) + "." + padQn;
    }
    return padQn;
  }

  /**
   * Gets the q num.
   *
   * @param questionId the question id
   * @return Question Number of this question without the parent's number
   */
  protected long getQNum(final long questionId) {
    return getQuestion(questionId) != null ? getQuestion(questionId).getQNumber() : 0l;
  }

  /**
   * Gets the ques response.
   *
   * @return the quesResponse
   */
  public RvwQnaireResponse getQuesResponse() {
    return this.qnaireRespModel.getRvwQnrResponse();
  }

  /**
   * Gets the question number with name.
   *
   * @param questionId the question id
   * @return question number of this question
   */
  public String getQuestionNumberWithNameByLanguage(final long questionId) {
    return getQuestionNumber(questionId) + "  " + getNameByLanguage(questionId);
  }


  /**
   * Gets the question number.
   *
   * @param questionId the question id
   * @return question number of this question
   */
  public String getQuestionNumber(final long questionId) {
    String prefix = "";
    if (getQuestion(questionId) != null) {
      prefix = getQnaireDefBo().isGeneralType() ? "G" : "";
      return prefix + getQnaireDefBo().getQuestionNumber(questionId);
    }
    return prefix;
  }

  // ICDM-1987
  /**
   * Compare question response instances using sort columns.
   *
   * @param rvwQnAns1 Long
   * @param rvwQnAns2 Long
   * @param sortColumn SortColumns
   * @return -1/0/1 as standard <code>compareTo()</code> response
   */

  public int compareTo(final RvwQnaireAnswer rvwQnAns1, final RvwQnaireAnswer rvwQnAns2, final SortColumns sortColumn) {

    int compareResult = -1;
    switch (sortColumn) {
      case SORT_QUES_NUMBER:
        compareResult = compareTo(rvwQnAns1, rvwQnAns2);
        break;

      case SORT_QUES_NAME:
        // // comparing the question names
        compareResult = ApicUtil.compare(getName(rvwQnAns1.getQuestionId()), getName(rvwQnAns2.getQuestionId()));
        break;

      case SORT_QUES_HINT:
        // // comparing the question hint
        compareResult = ApicUtil.compare(getDescription(rvwQnAns1), getDescription(rvwQnAns2));
        break;
      case SORT_MEASURABLE:
        // // comparing the measurable
        compareResult = ApicUtil.compare(getMeasurementUIString(rvwQnAns1), getMeasurementUIString(rvwQnAns2));
        break;
      case SORT_SERIES:
        // // comparing the series
        compareResult = ApicUtil.compare(getSeriesUIString(rvwQnAns1), getSeriesUIString(rvwQnAns2));
        break;
      case SORT_RESULT:
        // // comparing the result
        compareResult = ApicUtil.compare(
            getQuestionResultOptionUIString(rvwQnAns1.getQuestionId(), rvwQnAns1.getSelQnaireResultOptID()),
            getQuestionResultOptionUIString(rvwQnAns2.getQuestionId(), rvwQnAns2.getSelQnaireResultOptID()));
        break;
      case SORT_LINK:
        // // comparing the link
        compareResult = ApicUtil.compare(getLinkUIString(rvwQnAns1), getLinkUIString(rvwQnAns2));
        break;
      case SORT_REMARK:
        // // compare the remark
        compareResult = ApicUtil.compare(getRemarksUIString(rvwQnAns1), getRemarksUIString(rvwQnAns2));
        break;

      case SORT_CALU_RESULT:
        // // comparing the result
        compareResult = ApicUtil.compare(
            getQuestionResultOptionUIString(rvwQnAns1.getQuestionId(), rvwQnAns1.getSelQnaireResultOptID()),
            getQuestionResultOptionUIString(rvwQnAns2.getQuestionId(), rvwQnAns2.getSelQnaireResultOptID()));
        break;

      default:
        // // Compare q number
        compareResult = compareTo(rvwQnAns1, rvwQnAns2);
        break;
    }

    // additional compare if both the values are same
    if (compareResult == 0) {
      // compare result is equal, compare the q number
      compareResult = compareTo(rvwQnAns1, rvwQnAns2);
    }

    return compareResult;

  }

  /**
   * @param rvwQnAnsOpl1 as Input
   * @param rvwQnAnsOpl2 as Input
   * @param sortColumn as Input
   * @return compare result
   */
  public int compareTo(final RvwQnaireAnswerOpl rvwQnAnsOpl1, final RvwQnaireAnswerOpl rvwQnAnsOpl2,
      final SortColumns sortColumn) {
    int compareResult = -1;


    if (SortColumns.SORT_OP.equals(sortColumn)) {
      // compare the open points
      try {
        compareResult = ApicUtil.compare(
            getOpenPointUIString(getAllRvwAnswerMap().get(rvwQnAnsOpl1.getRvwAnswerId()), rvwQnAnsOpl1),
            getOpenPointUIString(getAllRvwAnswerMap().get(rvwQnAnsOpl2.getRvwAnswerId()), rvwQnAnsOpl1));
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }
    if (compareResult == 0) {
      // compare result is equal, compare the q number
      compareResult = ApicUtil.compare(rvwQnAnsOpl1.getId(), rvwQnAnsOpl2.getId());
    }

    return compareResult;
  }

  /**
   * Get the heading type question responses at the immediate child level.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @return sorted set of child headings as RvwQnaireAnswer
   */
  public SortedSet<RvwQnaireAnswer> getChildHeadings(final RvwQnaireAnswer rvwQnaireAns) {
    SortedSet<RvwQnaireAnswer> reviewQnaireAnswerSet = new TreeSet<>();
    if (getQuestion(rvwQnaireAns.getQuestionId()) != null) {
      Map<Long, List<Long>> childQuestions = getQnaireDefBo().getChildQuestions();
      for (Long childQuesId : childQuestions.get(rvwQnaireAns.getQuestionId())) {
        // Get child questions
        if (getQnaireDefBo().isHeading(childQuesId) && getAllQuestionMap().containsKey(childQuesId)) {
          RvwQnaireAnswer childRvwQuesAns = getAllQuestionMap().get(childQuesId);
          reviewQnaireAnswerSet.add(childRvwQuesAns);
        }
      }
    }

    return reviewQnaireAnswerSet;
  }

  /**
   * Get the question responses at the immediate child level.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @return sorted set of children as RvwQnaireAnswer
   */
  public SortedSet<RvwQnaireAnswer> getChildQuestions(final RvwQnaireAnswer rvwQnaireAns) {
    SortedSet<RvwQnaireAnswer> allChildrenSet = new TreeSet<>();
    if (getQuestion(rvwQnaireAns.getQuestionId()) != null) {
      Map<Long, List<Long>> childQuestions = getQnaireDefBo().getChildQuestions();
      for (Long childQuesId : childQuestions.get(rvwQnaireAns.getQuestionId())) {
        // Get child questions
        if (getAllQuestionMap().containsKey(childQuesId)) {
          RvwQnaireAnswer childRvwQuesAns = getAllQuestionMap().get(childQuesId);
          allChildrenSet.add(childRvwQuesAns);
        }
        for (Long innerChildQuesId : childQuestions.get(childQuesId)) {
          if (getAllQuestionMap().containsKey(innerChildQuesId)) {
            allChildrenSet.add(getAllQuestionMap().get(innerChildQuesId));
          }
        }
      }
    }

    return allChildrenSet;
  }


  /**
   * Checks if is question visible.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @return true if the question is visible
   */
  public boolean isQuestionVisible(final RvwQnaireAnswer rvwQnaireAns) {
    return this.quesRespdataResolver.isQuestionVisible(rvwQnaireAns, true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }


  /**
   * Get the parent question object. Return could be a question/heading or a dummy questionnaire version object
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @return parent question as RvwQnaireAnswer
   */
  public RvwQnaireAnswer getParentQuestion(final RvwQnaireAnswer rvwQnaireAns) {
    if (getQuestion(rvwQnaireAns.getQuestionId()) != null) {
      Question currQuestion = getQuestion(rvwQnaireAns.getQuestionId());
      Question parentQstn = getQnaireDefBo().getQuestion(currQuestion.getParentQId());
      if (CommonUtils.isNotNull(parentQstn)) {
        return getAllQuestionMap().get(parentQstn.getId());
      }
    }
    return null;
  }

  /**
   * Search through the question tree below and find the first question response of type 'question'.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @return first 'question' type RvwQnaireAnswer
   */
  public RvwQnaireAnswer getFirstQuestionResponse(final RvwQnaireAnswer rvwQnaireAns) {
    return getDeepChildQuestionInternal(rvwQnaireAns);
  }

  /**
   * Search through the questionnaire tree and find the first question response of type 'question'.
   *
   * @return first question response
   */
  public RvwQnaireAnswer getFirstQuestionResponse() {
    /**
     * sort questions based on ques no. to select the first non heading question in list view when given rvwQnaireAnswer
     * is selected in outline view
     **/
    SortedSet<RvwQnaireAnswer> allQnaireAns = new TreeSet<>(getRvwQuestionAnserComp());
    allQnaireAns.addAll(getAllQuestionAnswers());
    for (RvwQnaireAnswer rvwQnaireAnswer : allQnaireAns) {
      long questionId = rvwQnaireAnswer.getQuestionId();
      if (!getQnaireDefBo().isHeading(questionId)) {
        return rvwQnaireAnswer;
      }
    }
    return null;
  }

  /**
   * Find the first child question, recursively.
   *
   * @param rvwQnaireAnswer RvwQnaireAnswer
   * @return RvwQnaireAnswer
   */
  private RvwQnaireAnswer getDeepChildQuestionInternal(final RvwQnaireAnswer rvwQnaireAnswer) {
    long questionId = rvwQnaireAnswer.getQuestionId();
    if (getQnaireDefBo().isHeading(questionId)) {
      SortedSet<RvwQnaireAnswer> children = new TreeSet<>(getRvwQuestionAnserComp());
      /**
       * sort child questions based on ques no. to select the first non heading child question in list view when given
       * rvwQnaireAnswer is selected in outline view
       **/
      children.addAll(getChildQuestions(rvwQnaireAnswer));
      if (CommonUtils.isNotEmpty(children)) {
        // Now the code has to handle invisible questions. So do not show first Question.
        for (RvwQnaireAnswer reviewQnaireAnswer : children) {
          if (isQuestionVisible(reviewQnaireAnswer)) {
            return getDeepChildQuestionInternal(reviewQnaireAnswer);
          }
        }
      }
    }
    // If this is a question, then return directly
    return rvwQnaireAnswer;
  }

  /**
   * Get the open points against this question response.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @return the openPointsList
   */
  public Map<Long, RvwQnaireAnswerOpl> getOpenPointsList(final RvwQnaireAnswer rvwQnaireAns) {
    Map<Long, RvwQnaireAnswerOpl> openPointsList = new HashMap<>();
    if (getQuestion(rvwQnaireAns.getQuestionId()) != null) {
      long questionId = rvwQnaireAns.getQuestionId();
      if (getQnaireDefBo().isHeading(questionId)) {
        return openPointsList;
      }
      return this.qnaireRespModel.getOpenPointsMap().containsKey(rvwQnaireAns.getId())
          ? this.qnaireRespModel.getOpenPointsMap().get(rvwQnaireAns.getId()) : openPointsList;
    }
    return openPointsList;
  }

  /**
   * @param rvwQnaireAnsOpl as input
   * @return boolean value
   */
  public boolean isResultSet(final RvwQnaireAnswerOpl rvwQnaireAnsOpl) {
    return CommonUtils.getBooleanType(rvwQnaireAnsOpl.getResult());
  }

  /**
   * Gets the all question answers.
   *
   * @return the all question answers
   */
  public SortedSet<RvwQnaireAnswer> getAllQuestionAnswers() {
    return getAllQuestionMap().values().stream().collect(Collectors.toCollection(TreeSet::new));
  }


  /**
   * To Get all the Objects of Questionnaire Response
   *
   * @return set of objects
   */
  public List<Object> getAllObjects() {
    List<Object> objSet = new ArrayList<>();

    TreeSet<RvwQnaireAnswer> collect =
        getAllQuestionMap().values().stream().collect(Collectors.toCollection(TreeSet::new));
    for (RvwQnaireAnswer rvwQnaireAnswer : collect) {
      objSet.add(rvwQnaireAnswer);
      loadOplObjectsInList(objSet, rvwQnaireAnswer);
    }
    return objSet;
  }

  /**
   * @param objSet
   * @param rvwQnaireAnswer
   */
  private void loadOplObjectsInList(final List<Object> objSet, final RvwQnaireAnswer rvwQnaireAnswer) {
    if (this.qnaireRespModel.getOpenPointsMap().containsKey(rvwQnaireAnswer.getId())) {
      Collection<RvwQnaireAnswerOpl> oplAnswerCollection =
          this.qnaireRespModel.getOpenPointsMap().get(rvwQnaireAnswer.getId()).values();

      if ((oplAnswerCollection != null) && !oplAnswerCollection.isEmpty()) {
        for (RvwQnaireAnswerOpl oplAnswer : oplAnswerCollection) {
          if (rvwQnaireAnswer.getOplId().contains(oplAnswer.getId())) {
            objSet.add(oplAnswer);
          }
        }
      }
    }
  }

  /**
   * Gets the name ext.
   *
   * @return the name ext
   */
  public String getNameExt() {
    StringBuilder extName = new StringBuilder();
    extName.append(this.mainQnaireDefBo.getDisplayVersionName());
    if (this.qnaireRespModel.getPidcVersion() != null) {
      extName.append(" >> ").append(this.qnaireRespModel.getPidcVersion().getName());
    }
    return extName.toString();
  }

  /**
   * @return as English text
   */
  public String getNameExtByLanguage() {
    StringBuilder extName = new StringBuilder();
    if (isQuestionnaireLangEng()) {
      extName.append(this.mainQnaireDefBo.getDisplayVersionNameEnglish());
    }
    else {
      extName.append(this.mainQnaireDefBo.getDisplayVersionNameGerman());
    }
    extName.append(" - ");
    extName.append(this.qnaireRespModel.getRvwQnrRespVersion().getName());
    if (this.qnaireRespModel.getPidcVersion() != null) {
      extName.append(" >> ").append(this.qnaireRespModel.getPidcVersion().getName());
    }
    return extName.toString();
  }

  /**
   * Gets the name for part.
   *
   * @return the name for part
   */
  public String getNameForPart() {
    return this.mainQnaireDefBo.getNameForPart();
  }

  /**
   * Gets the links.
   *
   * @param answer rvw qnaire ans object
   * @return the links
   */
  public Set<Link> getLinks(final RvwQnaireAnswer answer) {
    Map<Long, Link> links =
        this.qnaireRespModel.getLinksMap().computeIfAbsent(answer.getId(), k -> getLinksFromServer(answer));

    return null == links ? new HashSet<>() : links.values().stream().collect(Collectors.toCollection(HashSet::new));
  }

  /**
   * Get links from server via service
   *
   * @param answer RvwQnaireAnswer
   * @return map of links with key as link ID
   */
  private Map<Long, Link> getLinksFromServer(final RvwQnaireAnswer answer) {
    Map<Long, Link> links = null;
    try {
      links = new LinkServiceClient().getAllLinksByNode(answer.getId(), MODEL_TYPE.RVW_QNAIRE_ANS);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().warn(e.getMessage(), e);
    }

    return links;
  }


  /**
   * Checks if is heading.
   *
   * @param rvwQnaireAns the rvw qnaire ans
   * @return true, if is heading
   */
  public boolean checkHeading(final RvwQnaireAnswer rvwQnaireAns) {
    if (getQuestion(rvwQnaireAns.getQuestionId()) != null) {
      long questionId = rvwQnaireAns.getQuestionId();
      return getQnaireDefBo().isHeading(questionId);
    }
    return true;
  }


  /**
   * @param rvwQnaireAns tree node in outline view
   * @param resultSet set of result ids
   */
  public void checkAllQuestionAnswered(final RvwQnaireAnswer rvwQnaireAns, final Set<Long> resultSet) {
    SortedSet<RvwQnaireAnswer> childQuestions = getChildQuestions(rvwQnaireAns);
    for (RvwQnaireAnswer rvwQnaireChildAnswer : childQuestions) {
      // validate if the question is visible
      if (isQuestionVisible(rvwQnaireChildAnswer)) {
        if (checkHeading(rvwQnaireChildAnswer)) {
          checkAllQuestionAnswered(rvwQnaireChildAnswer, resultSet);
        }
        else {
          checkQuesAnswered(resultSet, rvwQnaireChildAnswer);
        }
      }
    }
  }

  /**
   * @param resultSet
   * @param rvwQnaireChildAnswer
   */
  private void checkQuesAnswered(final Set<Long> resultSet, final RvwQnaireAnswer rvwQnaireChildAnswer) {
    // To check if the review answers results field is not relevant
    if (!checkResultIsMandatory(rvwQnaireChildAnswer.getQuestionId())) {
      // if the result is not relevent then check has to be done
      // to validate whether all other mandatory fields are filled
      if (checkMandatoryItemsFilled(rvwQnaireChildAnswer)) {
        resultSet.add(ANSWERED_POSITIVE_OR_NEUTRAL);
        if (CDRConstants.QS_ASSESMENT_TYPE.POSITIVE.getDbType()
            .equals(rvwQnaireChildAnswer.getSelQnaireResultAssement()) ||
            CDRConstants.QS_ASSESMENT_TYPE.NEUTRAL.getDbType()
                .equals(rvwQnaireChildAnswer.getSelQnaireResultAssement())) {
          resultSet.add(ANSWERED_POSITIVE_OR_NEUTRAL);
        }
        else if (CDRConstants.QS_ASSESMENT_TYPE.NEGATIVE.getDbType()
            .equals(rvwQnaireChildAnswer.getSelQnaireResultAssement())) {
          resultSet.add(ANSWERED_NEGATIVE);
        }
      }
      else {
        // incomplete is added if the mandatory items are not filled
        resultSet.add(ANSWERED_INCOMPLETE);
      }
    }
    else {
      // for normal flow when review answers result column is either mandatory / optional
      if (CDRConstants.QS_ASSESMENT_TYPE.POSITIVE.getDbType()
          .equals(rvwQnaireChildAnswer.getSelQnaireResultAssement()) ||
          CDRConstants.QS_ASSESMENT_TYPE.NEUTRAL.getDbType()
              .equals(rvwQnaireChildAnswer.getSelQnaireResultAssement())) {
        resultSet.add(ANSWERED_POSITIVE_OR_NEUTRAL);
      }
      else if (CDRConstants.QS_ASSESMENT_TYPE.NEGATIVE.getDbType()
          .equals(rvwQnaireChildAnswer.getSelQnaireResultAssement())) {
        resultSet.add(ANSWERED_NEGATIVE);
      }
      else {
        resultSet.add(ANSWERED_INCOMPLETE);
      }
    }
  }


  /**
   * Checks if is result mandatory.
   *
   * @param rvwQnaireAns the rvw qnaire ans
   * @return true, if is result mandatory
   */
  public boolean isResultMandatory(final RvwQnaireAnswer rvwQnaireAns) {
    if (getQuestion(rvwQnaireAns.getQuestionId()) != null) {
      return CommonUtils.getBooleanType(getQnaireDefBo().getQnaireVersion().getResultRelevantFlag()) &&
          (getQnaireDefBo().getResult(rvwQnaireAns.getQuestionId()) == QUESTION_CONFIG_TYPE.MANDATORY);
    }
    return false;
  }

  /**
   * Checks if is remarks mandatory.
   *
   * @param rvwQnaireAns the rvw ans obj
   * @return true, if is remarks mandatory
   */
  public boolean isRemarksMandatory(final RvwQnaireAnswer rvwQnaireAns) {
    if (getQuestion(rvwQnaireAns.getQuestionId()) != null) {
      return CommonUtils.getBooleanType(getQnaireDefBo().getQnaireVersion().getRemarkRelevantFlag()) &&
          (getQnaireDefBo().getRemark(rvwQnaireAns.getQuestionId()) == QUESTION_CONFIG_TYPE.MANDATORY);
    }
    return false;
  }

  /**
   * Checks if is open points optional.
   *
   * @param rvwQnaireAns the rvw ans obj
   * @return true, if is open points optional
   */
  public boolean isOpenPointsOptional(final RvwQnaireAnswer rvwQnaireAns) {
    if (getQuestion(rvwQnaireAns.getQuestionId()) != null) {
      return CommonUtils.getBooleanType(getQnaireDefBo().getQnaireVersion().getOpenPointsRelevantFlag()) &&
          (getQnaireDefBo().getOpenPoints(rvwQnaireAns.getQuestionId()) == QUESTION_CONFIG_TYPE.OPTIONAL);
    }
    return false;
  }

  /**
   * Checks if is link mandatory.
   *
   * @param rvwQnaireAns the rvw ans obj
   * @return true, if is link mandatory
   */
  public boolean isLinkMandatory(final RvwQnaireAnswer rvwQnaireAns) {
    if (getQuestion(rvwQnaireAns.getQuestionId()) != null) {
      return CommonUtils.getBooleanType(getQnaireDefBo().getQnaireVersion().getLinkRelevantFlag()) &&
          (getQnaireDefBo().getLink(rvwQnaireAns.getQuestionId()) == QUESTION_CONFIG_TYPE.MANDATORY);
    }
    return false;
  }

  /**
   * Checks if is measurement mandatory.
   *
   * @param rvwQnaireAns the rvw ans obj
   * @return true, if is measurement mandatory
   */
  public boolean isMeasurementMandatory(final RvwQnaireAnswer rvwQnaireAns) {
    if (getQuestion(rvwQnaireAns.getQuestionId()) != null) {
      return CommonUtils.getBooleanType(getQnaireDefBo().getQnaireVersion().getMeasurementRelevantFlag()) &&
          (getQnaireDefBo().getMeasurement(rvwQnaireAns.getQuestionId()) == QUESTION_CONFIG_TYPE.MANDATORY);
    }
    return false;
  }

  /**
   * Checks if is series mandatory.
   *
   * @param rvwQnaireAns the rvw ans obj
   * @return true, if is series mandatory
   */
  public boolean isSeriesMandatory(final RvwQnaireAnswer rvwQnaireAns) {
    if (getQuestion(rvwQnaireAns.getQuestionId()) != null) {
      return CommonUtils.getBooleanType(getQnaireDefBo().getQnaireVersion().getSeriesRelevantFlag()) &&
          (getQnaireDefBo().getSeries(rvwQnaireAns.getQuestionId()) == QUESTION_CONFIG_TYPE.MANDATORY);
    }
    return false;
  }

  /**
   * Checks if is measures mandatory.
   *
   * @param rvwQnaireAns the rvw ans obj
   * @return true, if is measures mandatory
   */
  public boolean isMeasuresMandatory(final RvwQnaireAnswer rvwQnaireAns) {
    if (getQuestion(rvwQnaireAns.getQuestionId()) != null) {
      return CommonUtils.getBooleanType(getQnaireDefBo().getQnaireVersion().getMeasureRelaventFlag()) &&
          (getQnaireDefBo().getMeasure(rvwQnaireAns.getQuestionId()) == QUESTION_CONFIG_TYPE.MANDATORY);
    }
    return false;
  }


  /**
   * Checks if is responsible mandatory.
   *
   * @param rvwQnaireAns the rvw ans obj
   * @return true, if is responsible mandatory
   */
  public boolean isResponsibleMandatory(final RvwQnaireAnswer rvwQnaireAns) {
    if (getQuestion(rvwQnaireAns.getQuestionId()) != null) {
      return CommonUtils.getBooleanType(getQnaireDefBo().getQnaireVersion().getResponsibleRelaventFlag()) &&
          (getQnaireDefBo().getResponsible(rvwQnaireAns.getQuestionId()) == QUESTION_CONFIG_TYPE.MANDATORY);
    }
    return false;
  }

  /**
   * Checks if is date mandatory.
   *
   * @param rvwQnaireAns the rvw ans obj
   * @return true, if is date mandatory
   */
  public boolean isDateMandatory(final RvwQnaireAnswer rvwQnaireAns) {
    if (getQuestion(rvwQnaireAns.getQuestionId()) != null) {
      return CommonUtils.getBooleanType(getQnaireDefBo().getQnaireVersion().getCompletionDateRelaventFlag()) &&
          (getQnaireDefBo().getCompletionDate(rvwQnaireAns.getQuestionId()) == QUESTION_CONFIG_TYPE.MANDATORY);
    }
    return false;
  }

  /**
   * Creates the rvw qnaire ans.
   *
   * @param rvwQnaireAnswer the rvw qnaire answer
   * @return updated rvwQnaireAnswer
   */
  public RvwQnaireAnswer updateRvwQnaireAns(final RvwQnaireAnswer rvwQnaireAnswer) {

    boolean isUpdate = true;
    // To check whether this is a dummy/empty answer with questionId set as its ID. => Then insert the answer, else
    // update
    // existing answer
    if (rvwQnaireAnswer.getId().equals(rvwQnaireAnswer.getQuestionId()) &&
        !this.qnaireRespModel.getRvwQnrAnswrMap().containsKey(rvwQnaireAnswer.getId())) {
      isUpdate = false;
    }
    RvwQnaireAnswer rvwQnaireAnswerUpdated = null;
    if (isUpdate) {
      try {
        rvwQnaireAnswerUpdated = new RvwQnaireAnswerServiceClient().update(rvwQnaireAnswer);
        refreshDataMap(rvwQnaireAnswer.getQuestionId(), rvwQnaireAnswerUpdated);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
    else {
      try {
        rvwQnaireAnswer.setId(null);
        rvwQnaireAnswerUpdated = new RvwQnaireAnswerServiceClient().create(rvwQnaireAnswer);
        // Incase of dummy answer, questionId was set as ID (key).
        refreshDataMap(rvwQnaireAnswer.getQuestionId(), rvwQnaireAnswerUpdated);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
    return rvwQnaireAnswerUpdated;
  }

  /**
   * Method to calculate the Qnaire Response Version Status and update the statis
   *
   * @return RvwQnaireRespVersion
   */
  public RvwQnaireRespVersion updateQnaireRespVersStatus() {
    // To find the questionnaire status
    String dbType = findQnaireRespStatus().getDbType();
    // Update RvwQnaireRespVersion Status
    RvwQnaireRespVersion rvwQnrRespVersion = this.qnaireRespModel.getRvwQnrRespVersion();
    try {
      // Qnaire Resp Version status will be updated only when there is change in Status
      if (CommonUtils.isNotEqual(dbType, rvwQnrRespVersion.getQnaireRespVersStatus())) {
        rvwQnrRespVersion.setQnaireRespVersStatus(dbType);
        rvwQnrRespVersion = new RvwQnaireRespVersionServiceClient().update(rvwQnrRespVersion);
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return rvwQnrRespVersion;
  }


  /**
   * Refresh data map.
   *
   * @param key the key
   * @param newData new rvw qnaire answer object
   */
  public void refreshDataMap(final long key, final RvwQnaireAnswer newData) {
    try {
      RvwQnaireAnswer rvwQnaireAnswer = new RvwQnaireAnswerServiceClient().getById(newData.getId());
      if (getAllQuestionMap().containsKey(key)) {
        RvwQnaireAnswer local = getAllQuestionMap().get(key);
        CommonUtils.shallowCopy(local, rvwQnaireAnswer);
      }
      this.qnaireRespModel.getRvwQnrAnswrMap().put(newData.getId(), rvwQnaireAnswer);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), Activator.PLUGIN_ID);
    }

  }

  /**
   * @param addList List<RvwQnaireAnswerOpl>
   * @param editList List<RvwQnaireAnswerOpl>
   * @param delList List<Long>
   * @param updatedAns RvwQnaireAnswer
   */
  public void updateRvwQnaireAnsOpl(final List<RvwQnaireAnswerOpl> addList, final List<RvwQnaireAnswerOpl> editList,
      final List<Long> delList, final RvwQnaireAnswer updatedAns) {
    boolean isDeleted = false;
    if (CommonUtils.isNotEmpty(addList)) {
      try {
        List<RvwQnaireAnswerOpl> createdOpl = new RvwQnaireAnswerOplServiceClient().create(addList);
        refreshOplDataMap(updatedAns.getId(), createdOpl, delList, isDeleted);
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);

      }
    }
    if (CommonUtils.isNotEmpty(editList)) {
      try {
        List<RvwQnaireAnswerOpl> updatedOpl = new RvwQnaireAnswerOplServiceClient().update(editList);
        refreshOplDataMap(updatedAns.getId(), updatedOpl, delList, isDeleted);
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }
    if (CommonUtils.isNotEmpty(delList)) {
      try {
        new RvwQnaireAnswerOplServiceClient().delete(delList);
        isDeleted = true;
        refreshOplDataMap(updatedAns.getId(), null, delList, isDeleted);
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }

  }

  /**
   *
   */
  private void refreshOplDataMap(final Long answerId, final List<RvwQnaireAnswerOpl> updatedData,
      final List<Long> delList, final boolean isDeleted) {

    RvwQnaireAnswer answer = this.qnaireRespModel.getRvwQnrAnswrMap().get(answerId);

    Map<Long, RvwQnaireAnswerOpl> answerOplMap = this.qnaireRespModel.getOpenPointsMap().get(answerId);
    if (answerOplMap == null) {
      addAnswerOplMap(answerId, updatedData, answer);
    }
    else {
      updateAnswerOplMap(updatedData, delList, isDeleted, answer, answerOplMap);
    }

  }

  /**
   * @param answerId
   * @param updatedData
   * @param rvwQnaireAnswer
   */
  private void addAnswerOplMap(final Long answerId, final List<RvwQnaireAnswerOpl> updatedData,
      final RvwQnaireAnswer rvwQnaireAnswer) {

    // create new OPL map
    Map<Long, RvwQnaireAnswerOpl> newOplDataMap = new HashMap<>();
    for (RvwQnaireAnswerOpl newOpl : updatedData) {
      newOplDataMap.put(newOpl.getId(), newOpl);
      rvwQnaireAnswer.getOplId().add(newOpl.getId());
      refreshDataMap(rvwQnaireAnswer.getQuestionId(), rvwQnaireAnswer);
    }
    this.qnaireRespModel.getOpenPointsMap().put(answerId, newOplDataMap);
  }

  /**
   * @param updatedData
   * @param delList
   * @param isDeleted
   * @param answer
   * @param answerOplMap
   */
  private void updateAnswerOplMap(final List<RvwQnaireAnswerOpl> updatedData, final List<Long> delList,
      final boolean isDeleted, final RvwQnaireAnswer answer, final Map<Long, RvwQnaireAnswerOpl> answerOplMap) {

    if (CommonUtils.isNotEmpty(delList) && isDeleted) {
      // delete existing OPL
      for (Long delOplId : delList) {
        answerOplMap.remove(delOplId, answerOplMap.get(delOplId));
        answer.getOplId().remove(delOplId);
        refreshDataMap(answer.getQuestionId(), answer);
      }
    }
    else {
      // Create or update existing OPL
      for (RvwQnaireAnswerOpl newOpl : updatedData) {
        answerOplMap.put(newOpl.getId(), newOpl);
        if (answer.getOplId() != null) {
          answer.getOplId().add(newOpl.getId());
        }
        else {
          Set<Long> oplSet = new HashSet<>();
          oplSet.add(newOpl.getId());
          answer.setOplId(oplSet);

        }
        refreshDataMap(answer.getQuestionId(), answer);
      }
    }
  }


  /**
   * @return the parentQnMap
   */
  public Map<Long, Long> getParentQnMap() {
    return this.quesRespdataResolver.getParentQnMap();
  }


  /**
   * Gets the description.
   *
   * @param rvwQnaireAns the rvw qnaire ans
   * @return the description
   */
  public String getDescription(final RvwQnaireAnswer rvwQnaireAns) {
    long questionId = rvwQnaireAns.getQuestionId();
    // If this is dummy answer (i.e. heading)
    if (getQnaireDefBo().isHeading(questionId)) {
      return "";
    }
    if (getQuestion(questionId) != null) {
      return getQuestion(questionId).getDescription();
    }
    return this.mainQnaireDefBo.getQnaireVersion().getDescription();
  }


  /**
   * @return true if the Language is english
   */
  public boolean isQuestionnaireLangEng() {
    return Language.ENGLISH.getText().equalsIgnoreCase(getQnaireLanguage());
  }

  /**
   * @param rvwQnaireAns as input
   * @return description as english
   */
  public String getDescriptionByLanguage(final RvwQnaireAnswer rvwQnaireAns) {

    long questionId = rvwQnaireAns.getQuestionId();
    // If this is dummy answer (i.e. heading)
    if (getQnaireDefBo().isHeading(questionId)) {
      return "";
    }
    // For English
    if (isQuestionnaireLangEng()) {
      if (getQuestion(questionId) != null) {
        return getQuestion(questionId).getQHintEng() != null ? getQuestion(questionId).getQHintEng()
            : getQuestion(questionId).getQHintGer();
      }
      return "";
    }
    // For German
    if (getQuestion(questionId) != null) {
      return getQuestion(questionId).getQHintGer() != null ? getQuestion(questionId).getQHintGer()
          : getQuestion(questionId).getQHintEng();
    }
    return "";
  }


  /**
   * Gets the name.
   *
   * @param questionId the question id
   * @return the name
   */
  public String getName(final long questionId) {
    if (getQuestion(questionId) != null) {
      return getQuestion(questionId).getName();
    }
    return this.mainQnaireDefBo.getDisplayVersionName();
  }

  /**
   * @param questionId as input
   * @return the english name
   */
  public String getNameByLanguage(final long questionId) {
    // For English
    if (isQuestionnaireLangEng()) {
      if (getQuestion(questionId) != null) {
        return getQuestion(questionId).getQNameEng() != null ? getQuestion(questionId).getQNameEng()
            : getQuestion(questionId).getQNameGer();
      }
      return "";
    }
    // For German
    if (getQuestion(questionId) != null) {
      return getQuestion(questionId).getQNameGer() != null ? getQuestion(questionId).getQNameGer()
          : getQuestion(questionId).getQNameEng();
    }
    return "";
  }

  /**
   * @param parentQnId as input
   * @return desc based on Language
   */
  public String getQuestionDescriptionByLanguage(final Long parentQnId) {
    String quesDesc;
    if (isQuestionnaireLangEng()) {
      quesDesc = getQuestion(parentQnId).getQHintEng() != null ? getQuestion(parentQnId).getQHintEng()
          : getQuestion(parentQnId).getQHintGer();
      return quesDesc;
    }
    quesDesc = getQuestion(parentQnId).getQHintGer() != null ? getQuestion(parentQnId).getQHintGer()
        : getQuestion(parentQnId).getQHintEng();
    return quesDesc;
  }

  /**
   * @param qnaireRespId as input
   * @return collection of questionnaire versions belonging to the ques response
   */
  public Collection<RvwQnaireRespVersion> getQnaireVersions(final long qnaireRespId) {
    try {
      if (CommonUtils.isNullOrEmpty(this.qnaireVersions)) {
        this.qnaireVersions =
            new HashSet<>(new RvwQnaireRespVersionServiceClient().getQnaireRespVersionsByRespId(qnaireRespId).values());
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return this.qnaireVersions.stream().sorted(Comparator.comparing(RvwQnaireRespVersion::getRevNum))
        .collect(Collectors.toList());
  }

  /**
   * @param qnaireRespId as input
   * @return RvwQnaireRespVersion
   */
  public RvwQnaireRespVersion getQnaireRespWorkingSetVersion(final long qnaireRespId) {
    for (RvwQnaireRespVersion rvwQnaireRespVersion : getQnaireVersions(qnaireRespId)) {
      if (rvwQnaireRespVersion.getRevNum() == 0l) {
        return rvwQnaireRespVersion;
      }
    }
    return null;
  }

  /**
   * @return questionnaire response version name
   */
  public String getQnaireRespVersName() {
    return this.mainQnaireDefBo.getDisplayVersionName();
  }

  /**
   * Creates the pidc data handler.
   *
   * @param pidcVersId the pidc vers id
   * @return the pidc data handler
   */
  public PidcDataHandler createPidcDataHandler(final Long pidcVersId) {
    PidcDataHandler handler = new PidcDataHandler();
    PidcDetailsLoader loader = new PidcDetailsLoader(handler);
    PidcVersionWithDetailsInput input = new PidcVersionWithDetailsInput(pidcVersId, true);
    input.setPidcVersionId(pidcVersId);
    loader.loadDataModel(input);
    return handler;
  }

  /**
   * Gets the all question map.
   *
   * @return the all question map
   */
  public Map<Long, RvwQnaireAnswer> getAllQuestionMap() {
    return this.quesRespdataResolver.getAllQAMap();
  }

  /**
   * Gets all the rvw answer map
   *
   * @return the all the rvw answer map
   */
  public Map<Long, RvwQnaireAnswer> getAllRvwAnswerMap() {
    return this.quesRespdataResolver.getAllRvwAnsMap();
  }


  /**
   * @return the qnaireRespModel
   */
  public RvwQnaireResponseModel getQnaireRespModel() {
    return this.qnaireRespModel;
  }


  /**
   * @return the qnaireLanguage
   */
  public String getQnaireLanguage() {
    return this.qnaireLanguage;
  }


  /**
   * @param qnaireLanguage the qnaireLanguage to set
   */
  public void setQnaireLanguage(final String qnaireLanguage) {
    this.qnaireLanguage = qnaireLanguage;
  }


  /**
   * find the Qnaire Resp Version Status
   *
   * @return QS_STATUS_TYPE as output
   */
  public QS_STATUS_TYPE findQnaireRespStatus() {
    List<Long> resultList = new ArrayList<>();
    for (RvwQnaireAnswer rvwQnaireAnswer : getAllQuestionAnswers()) {
      if (!checkHeading(rvwQnaireAnswer) && isQuestionVisible(rvwQnaireAnswer) &&
          !getQuestion(rvwQnaireAnswer.getQuestionId()).getDeletedFlag() &&
          getQuestion(rvwQnaireAnswer.getQuestionId()).getResultRelevantFlag()) {
        // 618430 : Status of questions that have just mandatory/optional fields, but no answer
        if (!checkResultIsMandatory(rvwQnaireAnswer.getQuestionId())) {
          checkQnaireMandatoryItems(resultList, rvwQnaireAnswer);
        }
        else {
          String assesmentCode = rvwQnaireAnswer.getSelQnaireResultAssement();
          boolean resultAlwFinishWPFlag = rvwQnaireAnswer.isResultAlwFinishWPFlag();
          checkQnaireResultAssessment(resultList, assesmentCode, resultAlwFinishWPFlag);
        }
      }
    }

    QS_STATUS_TYPE statusTYPE = CDRConstants.QS_STATUS_TYPE.ALL_POSITIVE;

    if (resultList.contains(ANSWERED_NOT_ALLOWED_TO_FINISH_WP) && !resultList.contains(ANSWERED_INCOMPLETE)) {
      statusTYPE = CDRConstants.QS_STATUS_TYPE.NOT_ALLOWED_FINISHED_WP;
    }
    else if (resultList.contains(ANSWERED_NEGATIVE) && !resultList.contains(ANSWERED_INCOMPLETE)) {
      statusTYPE = CDRConstants.QS_STATUS_TYPE.NOT_ALL_POSITIVE;
      Optional<String> isNoNegativeAnsAllowedFlagOn = Optional
          .ofNullable(this.mainQnaireDefBo.getQnaireDefModel().getQuestionnaireVersion().getNoNegativeAnsAllowedFlag());
      if (isNoNegativeAnsAllowedFlagOn.isPresent() && this.mainQnaireDefBo.getQnaireDefModel().getQuestionnaireVersion()
          .getNoNegativeAnsAllowedFlag().equals("Y")) {
        statusTYPE = CDRConstants.QS_STATUS_TYPE.NOT_ALLOW_NEGATIVE;
      }
    }
    else if (resultList.contains(ANSWERED_INCOMPLETE)) {
      statusTYPE = CDRConstants.QS_STATUS_TYPE.NOT_ANSWERED;
    }
    return statusTYPE;
  }

  /**
   * @param resultList
   * @param assesmentCode
   */
  private void checkQnaireResultAssessment(final List<Long> resultList, final String assesmentCode,
      final boolean resultAlwFinishWPFlag) {
    // check to result Allow to Finish WP
    if (!resultAlwFinishWPFlag) {
      resultList.add(ANSWERED_NOT_ALLOWED_TO_FINISH_WP);
    }
    else {
      if (CDRConstants.QS_ASSESMENT_TYPE.POSITIVE.getDbType().equals(assesmentCode) ||
          CDRConstants.QS_ASSESMENT_TYPE.NEUTRAL.getDbType().equals(assesmentCode)) {
        // add value 1 for positive case and neutral cases
        resultList.add(ANSWERED_POSITIVE_OR_NEUTRAL);
      }
      else if (CDRConstants.QS_ASSESMENT_TYPE.NEGATIVE.getDbType().equals(assesmentCode)) {
        // add value -1 for negative case
        resultList.add(ANSWERED_NEGATIVE);
      }
      else {
        // add value 2 if result value is not set
        resultList.add(ANSWERED_INCOMPLETE);
      }
    }

  }

  /**
   * @param resultList
   * @param rvwQnaireAnswer
   */
  private void checkQnaireMandatoryItems(final List<Long> resultList, final RvwQnaireAnswer rvwQnaireAnswer) {
    // check to result Allow to Finish WP
    if (!rvwQnaireAnswer.isResultAlwFinishWPFlag()) {
      resultList.add(ANSWERED_NOT_ALLOWED_TO_FINISH_WP);
    }
    // to check whether the mandatory items for the answer item is filled
    else {
      if (checkMandatoryItemsFilled(rvwQnaireAnswer)) {
        if (!isResultMandatory(rvwQnaireAnswer) &&
            CommonUtils.isNotEmptyString(rvwQnaireAnswer.getSelQnaireResultAssement())) {
          checkQnaireResultAssessment(resultList, rvwQnaireAnswer.getSelQnaireResultAssement(),
              rvwQnaireAnswer.isResultAlwFinishWPFlag());
        }
        else {
          // add value 1 for positive case and neutral cases
          resultList.add(ANSWERED_POSITIVE_OR_NEUTRAL);
        }
      }
      else {
        // add value 2 if result value is not set
        resultList.add(ANSWERED_INCOMPLETE);
      }
    }
  }


  /**
   * @return the selRvwQnaireRespVersion
   */
  public RvwQnaireRespVersion getSelRvwQnaireRespVersion() {
    return this.selRvwQnaireRespVersion;
  }


  /**
   * @param selRvwQnaireRespVersion the selRvwQnaireRespVersion to set
   */
  public void setSelRvwQnaireRespVersion(final RvwQnaireRespVersion selRvwQnaireRespVersion) {
    this.selRvwQnaireRespVersion = selRvwQnaireRespVersion;
  }


  /**
   * @return the qnaireRespEditorInputData
   */
  public QnaireRespEditorInputData getQnaireRespEditorInputData() {
    return this.qnaireRespEditorInputData;
  }


  /**
   * @param qnaireRespEditorInputData the qnaireRespEditorInputData to set
   */
  public void setQnaireRespEditorInputData(final QnaireRespEditorInputData qnaireRespEditorInputData) {
    this.qnaireRespEditorInputData = qnaireRespEditorInputData;
  }

  /**
   * @return SortedSet<RvwQnaireAnswer> to be used as first level children in outline view
   */
  public SortedSet<RvwQnaireAnswer> getFirstLevelHeading() {
    return this.mainQnaireDefBo.getFirstLevelQuestionsWithHeading().stream()
        .map(ques -> getAllQuestionMap().get(ques.getId())).filter(this::isQuestionVisible)
        .collect(Collectors.toCollection(TreeSet::new));
  }

  /**
   * rvwQuestionAnserComp Comparator
   */
  private final Comparator<RvwQnaireAnswer> rvwQuestionAnserComp = (o1, o2) -> {
    String ques1PaddedNumber = getPaddedQuestionNumber(o1.getQuestionId());
    String ques2PaddedNumber = getPaddedQuestionNumber(o2.getQuestionId());
    return ApicUtil.compare(ques1PaddedNumber, ques2PaddedNumber);
  };


  /**
   * @return the rvwQuestionAnserComp
   */
  public Comparator<RvwQnaireAnswer> getRvwQuestionAnserComp() {
    return this.rvwQuestionAnserComp;
  }
}
