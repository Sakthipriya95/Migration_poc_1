/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.qnaire;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2lResponsibilityLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWorkPackageLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeModel;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader.LOAD_LEVEL;
import com.bosch.caltool.icdm.bo.cdr.CDRReviewResultLoader;
import com.bosch.caltool.icdm.bo.cdr.CDRRvwResultEditorDataLoader;
import com.bosch.caltool.icdm.bo.cdr.RvwWpRespLoader;
import com.bosch.caltool.icdm.bo.cdr.review.CDRReviewResultUtil;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.bo.general.ExternalLinkInfoLoader;
import com.bosch.caltool.icdm.bo.general.LinkLoader;
import com.bosch.caltool.icdm.bo.user.NodeAccessLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.IcdmRuntimeException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lResponsibility;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWorkPackage;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionnaire;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionnaireVersion;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireAnswer;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireAnswerOpl;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireRespVariant;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireRespVersion;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireResponse;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcQnaireInfo;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CdrReportQnaireRespWrapper;
import com.bosch.caltool.icdm.model.cdr.RvwResultWPandRespModel;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;
import com.bosch.caltool.icdm.model.cdr.RvwWpAndRespModel;
import com.bosch.caltool.icdm.model.cdr.RvwWpResp;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespDetails;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespStatusData;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespVersionData;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireVersionModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuesRespCopyDataWrapper;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswerOpl;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponseLink;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponseModel;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.general.ExternalLinkInfo;
import com.bosch.caltool.icdm.model.general.Link;


/**
 * Loader class for QuestionnaireResponse
 *
 * @author dja7cob
 */
public class RvwQnaireResponseLoader extends AbstractBusinessObject<RvwQnaireResponse, TRvwQnaireResponse> {


  private Set<TRvwQnaireRespVariant> qnaireRespVarList;
  /**
   * Questionnaire Response ID - made as field to fix sonar issue "Only 7 params are allowed for method"
   */
  private Long qnRespId;

  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public RvwQnaireResponseLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.RVW_QNAIRE_RESPONSE, TRvwQnaireResponse.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected RvwQnaireResponse createDataObject(final TRvwQnaireResponse entity) throws DataException {
    RvwQnaireResponse object = new RvwQnaireResponse();

    setCommonFields(object, entity);

    object.setName(getQnaireRespName(entity));

    // Initializing dummy variant to handle No variant case
    Long variantId = ApicConstants.NO_VARIANT_ID;
    Long a2lWpId = null;
    Long a2lRespId = null;

    String variantName = ApicConstants.DUMMY_VAR_NODE_NOVAR;
    String a2lWpName = null;
    String a2lRespName = null;

    if (CommonUtils.isNotEmpty(entity.getTRvwQnaireRespVariants())) {
      TRvwQnaireRespVariant primaryVariantEntity = getPrimaryVariantEntity(entity);
      TabvProjectVariant tabvProjectVariant = primaryVariantEntity.getTabvProjectVariant();
      if ((null != tabvProjectVariant) && (null != Long.valueOf(tabvProjectVariant.getVariantId()))) {
        variantId = tabvProjectVariant.getVariantId();
        PidcVariantLoader varloader = new PidcVariantLoader(getServiceData());
        variantName = varloader.getDataObjectByID(tabvProjectVariant.getVariantId()).getName();

      }

      object.setVariantName(variantName);
      object.setVariantId(variantId);

      a2lWpId = primaryVariantEntity.gettA2lWorkPackage().getA2lWpId();
      a2lWpName = primaryVariantEntity.gettA2lWorkPackage().getWpName();
      a2lRespId = primaryVariantEntity.gettA2lResponsibility().getA2lRespId();
      // Loading A2lResponsibility model to get the Name of Responsibility
      A2lResponsibility a2lResponsibility =
          new A2lResponsibilityLoader(getServiceData()).createDataObject(primaryVariantEntity.gettA2lResponsibility());
      a2lRespName = a2lResponsibility.getName();

      object.setPidcVersId(primaryVariantEntity.getTPidcVersion().getPidcVersId());
      object.setPidcId(primaryVariantEntity.getTPidcVersion().getTabvProjectidcard().getProjectId());

      // Setting primary Qnaire resp Name which will be displayed when Qnaire is linked
      // primary Qnaire resp Name is the Combination of
      // " Variant Name,A2l Responsibility Name,Al2 Work package Name "
      object.setPrimaryVarRespWpName(variantName + ApicConstants.QNAIRE_RESP_PRIMARY_NAME_DELIMITER + a2lRespName +
          ApicConstants.QNAIRE_RESP_PRIMARY_NAME_DELIMITER + a2lWpName);
      object.setSecondaryQnaireLinkMap(getSecondaryVariant(entity, variantId, a2lWpId, a2lRespId));

    }

    object.setDeletedFlag(yOrNToBoolean(entity.getDeletedFlag()));

    object.setReviewed(yOrNToBoolean(entity.getReviewedFlag()));
    object.setReviewedDate(timestamp2String(entity.getReviewedDate()));
    object.setReviewedUser(entity.getReviewedUser());

    object.setA2lRespId(a2lRespId);
    object.setA2lWpId(a2lWpId);


    return object;
  }

  /**
   * @param pidcVariant as input pidcVariant
   * @param qnaireId as qnaireId
   * @param a2lRespId A2l resp Id
   * @param a2lWpId A2l Wp Id
   * @return List Of TA2lWpResponsibilityStatus From table
   * @throws DataException DataException
   */
  public List<BigDecimal> getQnaireRespIdByQnaireVarRespWpIds(final PidcVariant pidcVariant, final Long qnaireRespId,
      final Long a2lRespId, final Long a2lWpId)
      throws DataException {
    Long qnaireId = getQnaireIdByQnaireRespID(qnaireRespId);
    TypedQuery<BigDecimal> typedQuery;
    if (CommonUtils.isEqual(ApicConstants.NO_VARIANT_ID, pidcVariant.getId())) {
      typedQuery =
          getEntMgr().createNamedQuery(TRvwQnaireResponse.GET_MATCHING_QNAIRERESPID_IN_DEST_NOVAR, BigDecimal.class);
      typedQuery.setParameter(1, qnaireId).setParameter(2, a2lRespId).setParameter(3, a2lWpId).setParameter(4,
          qnaireRespId);
    }
    else {
      typedQuery = getEntMgr().createNamedQuery(TRvwQnaireResponse.GET_MATCHING_QNAIRERESPID_IN_DEST, BigDecimal.class);
      typedQuery.setParameter(1, qnaireId).setParameter(2, a2lRespId).setParameter(3, a2lWpId)
          .setParameter(4, pidcVariant.getId()).setParameter(5, qnaireRespId);
    }
    return typedQuery.getResultList();
  }

  /**
   * Method that returns qnaire response if there are two qnaire response with same name when linking
   *
   * @param pidcVariant as input pidcVariant
   * @param qnaireRespId as qnaireRespId
   * @param a2lRespId A2l resp Id
   * @param a2lWpId A2l Wp Id
   * @return List<BigDecimal>
   * @throws DataException Data Exception
   */
  public List<BigDecimal> checkForQnaireWithSameNameWhenVarLinking(final PidcVariant pidcVariant,
      final Long qnaireRespId, final Long a2lRespId, final Long a2lWpId)
      throws DataException {
    return getQnaireRespIdByQnaireVarRespWpIds(pidcVariant, qnaireRespId, a2lRespId, a2lWpId);
  }

  /**
   * <ul>
   * <li>This Method is called when a Qnaire in a Var-resp-wp combination is already Linked</li>
   * <li>Again Same Qnaire from different node is linked to this Var-resp-wp combination</li>
   * <li>In this case the already Linked Qnaire should be unlinked 1st</li>
   * <li>This method finds the already Linked QnaireRespVarId when we pass original and linked qnaire Resp Ids</li>
   * </ul>
   *
   * @param qnaireRespId1 qnaireRespId1
   * @param qnaireRespId2 qnaireRespId2
   * @param pidcVariant pidcVariant
   * @param a2lRespId a2lRespId
   * @param a2lWpId a2lWpId
   * @return QnaireRespVarId Long
   */
  public Long getQnaireRespVarIdForUnlink(final Long qnaireRespId1, final Long qnaireRespId2,
      final PidcVariant pidcVariant, final Long a2lRespId, final Long a2lWpId) {
    TypedQuery<BigDecimal> typedQuery;
    if (CommonUtils.isEqual(ApicConstants.NO_VARIANT_ID, pidcVariant.getId())) {
      typedQuery =
          getEntMgr().createNamedQuery(TRvwQnaireResponse.GET_LINKED_QNAIRE_RESP_VAR_ID_NOVAR, BigDecimal.class);
      typedQuery.setParameter(1, qnaireRespId1).setParameter(2, qnaireRespId2).setParameter(3, a2lRespId)
          .setParameter(4, a2lWpId);
    }
    else {
      typedQuery = getEntMgr().createNamedQuery(TRvwQnaireResponse.GET_LINKED_QNAIRE_RESP_VAR_ID, BigDecimal.class);
      typedQuery.setParameter(1, qnaireRespId1).setParameter(2, qnaireRespId2).setParameter(3, a2lRespId)
          .setParameter(4, a2lWpId).setParameter(5, pidcVariant.getId());
    }
    if (CommonUtils.isNotEmpty(typedQuery.getResultList())) {
      return typedQuery.getResultList().get(0).longValue();
    }
    return null;
  }


  /**
   * Method to fetch the ques resp of the same questionnaire in target along general Question Relevant Flag for Copied
   * Qnaire Resp
   *
   * @param destPidcVersId as pidc_vers_id
   * @param destPidcVarId as variant id
   * @param srcPidcVersId src pidc vers id
   * @return QuesRespCopyDataWrapper
   * @throws IcdmException exception
   */
  public QuesRespCopyDataWrapper getDataForCopyQnaireRespValidation(final Long destPidcVersId, final Long destPidcVarId,
      final Long destWpId, final Long destRespId, final Long srcQnaireRespId, final Long srcPidcVersId)
      throws IcdmException {

    QuesRespCopyDataWrapper quesRespCopyDataWrapper = new QuesRespCopyDataWrapper();

    // fetching all the qnaire resp under pidcVersId -> pidcVarId -> respId -> wpId
    Set<TRvwQnaireRespVariant> qnaireRespVariantList = null;

    // Handling No variant case
    if (destPidcVarId > 0) {
      qnaireRespVariantList =
          new RvwQnaireRespVariantLoader(getServiceData()).getQnaireRespVariantList(destPidcVersId).stream()
              .filter(tRvwQnaireRespVar -> CommonUtils.isNotNull(tRvwQnaireRespVar.getTabvProjectVariant()) &&
                  CommonUtils.isEqual(tRvwQnaireRespVar.getTabvProjectVariant().getVariantId(), destPidcVarId) &&
                  isSelectedDest(tRvwQnaireRespVar, destRespId, destWpId))
              .collect(Collectors.toSet());
    }
    else {
      qnaireRespVariantList = new RvwQnaireRespVariantLoader(getServiceData()).getQnaireRespVariantList(destPidcVersId)
          .stream().filter(tRvwQnaireRespVar -> CommonUtils.isNull(tRvwQnaireRespVar.getTabvProjectVariant()) &&
              isSelectedDest(tRvwQnaireRespVar, destRespId, destWpId))
          .collect(Collectors.toSet());
    }

    fillQuesRespCopyDataWrapper(destPidcVarId, destWpId, destRespId, srcQnaireRespId, quesRespCopyDataWrapper,
        qnaireRespVariantList);
    quesRespCopyDataWrapper.setIsCopiedQuesGnrlQuesEqvlnt(getGeneralQuestionRelevantFlag(srcQnaireRespId));
    quesRespCopyDataWrapper.setSrcPidcDivId(getDivIdByPidcVersId(srcPidcVersId));
    quesRespCopyDataWrapper.setDestPidcDivId(getDivIdByPidcVersId(destPidcVersId));

    return quesRespCopyDataWrapper;
  }

  /**
   * @param destPidcVarId
   * @param destWpId
   * @param destRespId
   * @param srcQnaireRespId
   * @param quesRespCopyDataWrapper
   * @param qnaireRespVariantList
   * @throws DataException
   */
  private void fillQuesRespCopyDataWrapper(final Long destPidcVarId, final Long destWpId, final Long destRespId,
      final Long srcQnaireRespId, final QuesRespCopyDataWrapper quesRespCopyDataWrapper,
      final Set<TRvwQnaireRespVariant> qnaireRespVariantList)
      throws DataException {
    for (TRvwQnaireRespVariant tRvwQnaireResponseVar : qnaireRespVariantList) {
      // tRvwQnaireResp is null for simplified Qnaire
      TRvwQnaireResponse destQnaireResp = tRvwQnaireResponseVar.getTRvwQnaireResponse();
      if (CommonUtils.isNotNull(destQnaireResp)) {
        RvwQnaireResponse destQnaireRespDataObj = createDataObject(destQnaireResp);
        // checking whether the destination has same questionnaire as copied one and verifying if there is same
        // questionnaire linked in destination
        if (CommonUtils.isEqual(getQnaireIdByQnaireRespID(destQnaireResp.getQnaireRespId()),
            getQnaireIdByQnaireRespID(srcQnaireRespId))) {
          if (isSameCopiedQnaireLinkedInDest(destQnaireRespDataObj.getSecondaryQnaireLinkMap(), destPidcVarId,
              destRespId, destWpId)) {
            quesRespCopyDataWrapper.setSameCopiedQnaireLinkedInDest(true);
          }
          else {
            quesRespCopyDataWrapper.setQnaireRespWithSameQnaireInDest(destQnaireRespDataObj);
          }
        }
        if (destQnaireRespDataObj.getName().startsWith(ApicConstants.GENERAL_QUESTIONS) ||
            destQnaireRespDataObj.getName().startsWith(ApicConstants.OBD_GENERAL_QUESTIONS)) {
          quesRespCopyDataWrapper.setDestGeneralQuesResp(destQnaireRespDataObj);
        }
      }
    }
  }

  /*
   * This method loops through the secondary Qnaire Resp variants and checks if given Qnaire is available
   */
  private boolean isSameCopiedQnaireLinkedInDest(final Map<Long, Map<Long, Set<Long>>> secondaryQnaireLinkMap,
      final Long pidcVarId, final Long respId, final Long wpId) {
    for (Map.Entry<Long, Map<Long, Set<Long>>> varEntry : secondaryQnaireLinkMap.entrySet()) {
      if (CommonUtils.isEqual(varEntry.getKey(), pidcVarId)) {
        for (Map.Entry<Long, Set<Long>> respEntry : varEntry.getValue().entrySet()) {
          if (CommonUtils.isEqual(respEntry.getKey(), respId) && respEntry.getValue().contains(wpId)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private boolean isSelectedDest(final TRvwQnaireRespVariant tRvwQnaireRespVar, final Long respId, final Long wpId) {
    return CommonUtils.isEqual(tRvwQnaireRespVar.gettA2lResponsibility().getA2lRespId(), respId) &&
        CommonUtils.isEqual(tRvwQnaireRespVar.gettA2lWorkPackage().getA2lWpId(), wpId);
  }

  /**
   * @param pidcVersId
   * @return
   * @throws IcdmException
   */
  private Long getDivIdByPidcVersId(final Long pidcVersId) throws IcdmException {

    PidcVersionAttributeModel pidcVersAttrModel =
        new ProjectAttributeLoader(getServiceData()).createModel(pidcVersId, LOAD_LEVEL.L1_PROJ_ATTRS);
    Long divAttrId =
        Long.valueOf(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.ICDM_QNAIRE_CONFIG_ATTR));
    return pidcVersAttrModel.getPidcVersAttr(divAttrId).getValueId();
  }

  /**
   * @param copiedQnaireRespId
   * @return
   * @throws DataException
   */
  private String getGeneralQuestionRelevantFlag(final Long copiedQnaireRespId) throws DataException {
    return new QuestionnaireVersionLoader(getServiceData())
        .getEntityObject(getQnaireVersIdByQnaireRespID(copiedQnaireRespId)).getGenQuesEquivalentFlag();
  }

  /**
   * Method to fetch the Qnaire Id for the given Questionnaire response Id
   *
   * @param qnaireRespId as questionnaire response id
   * @return questionnaire id
   * @throws DataException as exception
   */
  private Long getQnaireIdByQnaireRespID(final Long qnaireRespId) throws DataException {
    return new QuestionnaireVersionLoader(getServiceData())
        .getDataObjectByID(getQnaireVersIdByQnaireRespID(qnaireRespId)).getQnaireId();
  }

  /**
   * Method to fetch the Qnaire Version Id for the given Questionnaire response Id
   *
   * @param qnaireRespId as questionnaire response id
   * @return questionnaire version id
   * @throws DataException as exception
   */
  public Long getQnaireVersIdByQnaireRespID(final Long qnaireRespId) throws DataException {
    Long qnaireRespVersId = null;
    Optional<TRvwQnaireRespVersion> streamValue =
        getEntityObject(qnaireRespId).getTRvwQnaireRespVersions().stream().findFirst();
    if (streamValue.isPresent()) {
      qnaireRespVersId = streamValue.get().getQnaireRespVersId();
    }
    return new RvwQnaireRespVersionLoader(getServiceData()).getDataObjectByID(qnaireRespVersId).getQnaireVersionId();
  }

  /**
   * Method to fetch the Qnaire related data for Data Review Report
   *
   * @param pidcVersId as pidc_vers_id
   * @param pidcVarId as variant id
   * @param includeDeletedResp as boolean value
   * @return CdrReportQnaireRespWrapper
   * @throws IcdmException
   */
  public CdrReportQnaireRespWrapper getQniareRespVersByPidcVersIdAndVarId(final Long pidcVersId, final Long pidcVarId,
      final boolean includeDeletedResp)
      throws IcdmException {
    // Map Key - A2l_resp_id , Value - Key -A2l_wp_id and value - Set of Qnaire_Resp_ID
    Map<Long, Map<Long, Map<String, Long>>> respWpQniareMap = new HashMap<>();
    // Map Key - A2l_resp_id , Value - Key -A2l_wp_id and value - RvwQnaireRespVersion
    Map<Long, Map<Long, Set<RvwQnaireRespVersion>>> respWpQniareRespVersMap = new HashMap<>();
    // Map Key - A2l_resp_id , Value - Key -A2l_wp_id and value - all the rvw qnaire resp including not baselined qnaire
    // resp
    Map<Long, Map<Long, Set<RvwQnaireRespVersion>>> allRespWpQnaireRespVersMap = new HashMap<>();
    // Map key-Qnaire_Resp_Vers_ID and value - RvwQnaireResponse
    Map<Long, RvwQnaireResponse> rvwQnaireResponseMap = new HashMap<>();
    // Key - A2L Wp Id Value - A2L Wp Name
    Map<Long, String> wpIdAndNameMap = new HashMap<>();
    // Key - A2L Resp Id Value -A2L Resp Name
    Map<Long, String> respIdAndNameMap = new HashMap<>();


    // to fetch the set of qnaire resp variant set with pidcVersId as input
    Set<TRvwQnaireRespVariant> qnaireRespVariantList =
        new RvwQnaireRespVariantLoader(getServiceData()).getQnaireRespVariantList(pidcVersId);
    // fetching WP and RESP combination of qnaire responses
    for (TRvwQnaireRespVariant tRvwQnaireRespVar : qnaireRespVariantList) {
      Long varId = tRvwQnaireRespVar.getTabvProjectVariant() != null
          ? tRvwQnaireRespVar.getTabvProjectVariant().getVariantId() : null;
      // checking variant id is equal
      if (((null != varId) && varId.equals(pidcVarId)) || ((null == pidcVarId) && (varId == null))) {
        fillRespWpQnaireRespMap(pidcVarId, respWpQniareMap, tRvwQnaireRespVar, wpIdAndNameMap, respIdAndNameMap,
            includeDeletedResp);
      }
    }

    return fillCDRReportQnaireWrapper(respWpQniareMap, respWpQniareRespVersMap, allRespWpQnaireRespVersMap,
        rvwQnaireResponseMap, wpIdAndNameMap, respIdAndNameMap, pidcVarId);
  }

  /**
   * @param pidcVarId
   * @param respWpQniareMap
   * @param tRvwQnaireRespVar
   * @param respIdAndNameMap
   * @param wpIdAndNameMap
   * @param includeDeletedResp
   * @throws DataException
   */
  private void fillRespWpQnaireRespMap(final Long pidcVarId,
      final Map<Long, Map<Long, Map<String, Long>>> respWpQniareMap, final TRvwQnaireRespVariant tRvwQnaireRespVar,
      final Map<Long, String> wpIdAndNameMap, final Map<Long, String> respIdAndNameMap,
      final boolean includeDeletedResp)
      throws DataException {
    TRvwQnaireResponse tRvwQnaireResp = tRvwQnaireRespVar.getTRvwQnaireResponse();
    // to check a2l wp and resp id should not be null
    TA2lResponsibility tA2lResponsibility = tRvwQnaireRespVar.gettA2lResponsibility();
    TA2lWorkPackage tA2lWorkPackage = tRvwQnaireRespVar.gettA2lWorkPackage();
    if ((null != tA2lResponsibility) && (null != tA2lWorkPackage)) {
      Long a2lRespId = tA2lResponsibility.getA2lRespId();
      Long a2lWpId = tA2lWorkPackage.getA2lWpId();
      fillWpAndRespMaps(tA2lWorkPackage, tA2lResponsibility, wpIdAndNameMap, respIdAndNameMap);
      // only non deleted qnaire responses should be considered if the include deleted response flag is false
      // tRvwQnaireResp is null for simplified Qnaire
      if (includeDeletedResp ||
          (CommonUtils.isNotNull(tRvwQnaireResp) && ApicConstants.CODE_NO.equals(tRvwQnaireResp.getDeletedFlag())) ||
          CommonUtils.isNull(tRvwQnaireResp)) {
        Long qnaireRespId =
            CommonUtils.isNull(tRvwQnaireResp) ? ApicConstants.SIMP_QUES_RESP_ID : tRvwQnaireResp.getQnaireRespId();
        fillRespWpQnaireMap(respWpQniareMap, a2lRespId, a2lWpId, qnaireRespId, pidcVarId);
      }
    }
  }

  /**
   * @param tA2lWorkPackage
   * @param tA2lResponsibility
   * @param wpIdAndNameMap
   * @param respIdAndNameMap
   * @throws DataException
   */
  private void fillWpAndRespMaps(final TA2lWorkPackage tA2lWorkPackage, final TA2lResponsibility tA2lResponsibility,
      final Map<Long, String> wpIdAndNameMap, final Map<Long, String> respIdAndNameMap)
      throws DataException {

    if (wpIdAndNameMap.get(tA2lWorkPackage.getA2lWpId()) == null) {
      String wpName = new A2lWorkPackageLoader(getServiceData()).createDataObject(tA2lWorkPackage).getName();
      wpIdAndNameMap.put(tA2lWorkPackage.getA2lWpId(), wpName);
    }

    if (respIdAndNameMap.get(tA2lResponsibility.getA2lRespId()) == null) {
      String respName = new A2lResponsibilityLoader(getServiceData()).createDataObject(tA2lResponsibility).getName();
      respIdAndNameMap.put(tA2lResponsibility.getA2lRespId(), respName);
    }
  }

  /**
   * Method to
   *
   * @param respWpQniareMap
   * @param respWpQniareRespVersMap
   * @param allRespWpQnaireRespVersMap
   * @param rvwQnaireResponseMap
   * @param respIdAndNameMap
   * @param wpIdAndNameMap
   * @param pidcVarId
   * @return CdrReportQnaireRespWrapper
   * @throws IcdmException
   */
  private CdrReportQnaireRespWrapper fillCDRReportQnaireWrapper(
      final Map<Long, Map<Long, Map<String, Long>>> respWpQniareMap,
      final Map<Long, Map<Long, Set<RvwQnaireRespVersion>>> respWpQniareRespVersMap,
      final Map<Long, Map<Long, Set<RvwQnaireRespVersion>>> allRespWpQnaireRespVersMap,
      final Map<Long, RvwQnaireResponse> rvwQnaireResponseMap, final Map<Long, String> wpIdAndNameMap,
      final Map<Long, String> respIdAndNameMap, final Long pidcVarId)
      throws IcdmException {


    Map<Long, Map<Long, String>> respWpQniareStatusMap = new HashMap<>();
    // Key - Rvw Qnaire Response Version Id, Value - RvwQnaireRespVersModel
    final Map<Long, RvwQnaireRespVersModel> rvwQnaireRespVersModelMap = new HashMap<>();

    fillAllQnaireRespMap(respWpQniareMap, respWpQniareRespVersMap, allRespWpQnaireRespVersMap, rvwQnaireResponseMap,
        pidcVarId, rvwQnaireRespVersModelMap);

    fillConsolidatedQnaireRespStatusMap(respWpQniareRespVersMap, respWpQniareStatusMap);

    return setCdrReportWrapperData(respWpQniareRespVersMap, allRespWpQnaireRespVersMap, rvwQnaireResponseMap,
        wpIdAndNameMap, respIdAndNameMap, respWpQniareStatusMap, rvwQnaireRespVersModelMap);
  }

  /**
   * @param respWpQniareMap
   * @param respWpQniareRespVersMap
   * @param allRespWpQnaireRespVersMap
   * @param rvwQnaireResponseMap
   * @param pidcVarId
   * @param rvwQnaireRespVersModelMap
   * @throws DataException
   * @throws IcdmException
   */
  private void fillAllQnaireRespMap(final Map<Long, Map<Long, Map<String, Long>>> respWpQniareMap,
      final Map<Long, Map<Long, Set<RvwQnaireRespVersion>>> respWpQniareRespVersMap,
      final Map<Long, Map<Long, Set<RvwQnaireRespVersion>>> allRespWpQnaireRespVersMap,
      final Map<Long, RvwQnaireResponse> rvwQnaireResponseMap, final Long pidcVarId,
      final Map<Long, RvwQnaireRespVersModel> rvwQnaireRespVersModelMap)
      throws IcdmException {

    // Key - A2lRespId Value - Map(Key - WpId Value - Map(Key -QnaireRespName, value - Qnaire resp Id))
    for (Entry<Long, Map<Long, Map<String, Long>>> respWpQnaireEntry : respWpQniareMap.entrySet()) {
      for (Entry<Long, Map<String, Long>> wpQnaireEntry : respWpQnaireEntry.getValue().entrySet()) {
        for (Entry<String, Long> qnaireRespMap : wpQnaireEntry.getValue().entrySet()) {

          this.qnRespId = qnaireRespMap.getValue();
          if (CommonUtils.isNotEqual(ApicConstants.SIMP_QUES_RESP_ID, this.qnRespId)) {
            fillQnaireRespMapForNonSimpQnaire(respWpQniareRespVersMap, allRespWpQnaireRespVersMap, rvwQnaireResponseMap,
                pidcVarId, rvwQnaireRespVersModelMap, respWpQnaireEntry, wpQnaireEntry);
          }
          else {
            // fillAllQnaireResp for Simplified Qnaire
            fillAllQnaireRespWithLatestVers(allRespWpQnaireRespVersMap, rvwQnaireResponseMap, respWpQnaireEntry,
                wpQnaireEntry, null, null);
          }
        }
      }
    }
  }

  /**
   * @param respWpQniareRespVersMap
   * @param allRespWpQnaireRespVersMap
   * @param rvwQnaireResponseMap
   * @param pidcVarId
   * @param rvwQnaireRespVersModelMap
   * @param respWpQnaireEntry
   * @param wpQnaireEntry
   * @throws DataException
   * @throws IcdmException
   */
  private void fillQnaireRespMapForNonSimpQnaire(
      final Map<Long, Map<Long, Set<RvwQnaireRespVersion>>> respWpQniareRespVersMap,
      final Map<Long, Map<Long, Set<RvwQnaireRespVersion>>> allRespWpQnaireRespVersMap,
      final Map<Long, RvwQnaireResponse> rvwQnaireResponseMap, final Long pidcVarId,
      final Map<Long, RvwQnaireRespVersModel> rvwQnaireRespVersModelMap,
      final Entry<Long, Map<Long, Map<String, Long>>> respWpQnaireEntry,
      final Entry<Long, Map<String, Long>> wpQnaireEntry)
      throws IcdmException {
    Set<TRvwQnaireRespVersion> tRvwQnaireRespVersions = getEntityObject(this.qnRespId).getTRvwQnaireRespVersions();

    TRvwQnaireRespVersion latestQnaireRespVers = getLatestQnaireRespVersUsingRevNum(tRvwQnaireRespVersions, false);
    RvwQnaireRespVersion rvwQnaireRespVersion = null;
    RvwQnaireRespVersionLoader rvwQnaireRespVersionLoader = new RvwQnaireRespVersionLoader(getServiceData());
    if (null != latestQnaireRespVers) {
      rvwQnaireRespVersion = rvwQnaireRespVersionLoader.createDataObject(latestQnaireRespVers);
      // Filling respWpQniareRespVersMap
      fillRespWpQnaireRespVersMap(respWpQniareRespVersMap, respWpQnaireEntry.getKey(), wpQnaireEntry.getKey(),
          rvwQnaireRespVersion);
    }

    TRvwQnaireRespVersion allQnaireRespWithLatestVers =
        getLatestQnaireRespVersUsingRevNum(tRvwQnaireRespVersions, true);
    if (null != allQnaireRespWithLatestVers) {
      // allQnaireRespWithLatestVers and rvwQnaireRespVersion can be null for simplified Qnaire
      rvwQnaireRespVersion = null != rvwQnaireRespVersion ? rvwQnaireRespVersion
          : rvwQnaireRespVersionLoader.createDataObject(allQnaireRespWithLatestVers);
      fillAllQnaireRespWithLatestVers(allRespWpQnaireRespVersMap, rvwQnaireResponseMap, respWpQnaireEntry,
          wpQnaireEntry, allQnaireRespWithLatestVers, rvwQnaireRespVersion);
      fillRvwQnaireRespVersModelMap(allQnaireRespWithLatestVers, rvwQnaireRespVersModelMap,
          rvwQnaireResponseMap.get(allQnaireRespWithLatestVers.getQnaireRespVersId()), pidcVarId);
    }
  }

  /**
   * @param latestQnaireRespVers
   * @param rvwQnaireRespVersModelMap
   * @param rvwQnaireResponse
   * @param pidcVarId
   * @throws IcdmException
   */
  private void fillRvwQnaireRespVersModelMap(final TRvwQnaireRespVersion latestQnaireRespVers,
      final Map<Long, RvwQnaireRespVersModel> rvwQnaireRespVersModelMap, final RvwQnaireResponse rvwQnaireResponse,
      final Long pidcVarId)
      throws IcdmException {
    Set<TRvwQnaireAnswer> tRvwQnaireAnswers = latestQnaireRespVers.getTRvwQnaireAnswers();

    int qnairePositiveAnsCount = 0;
    int qnaireNegativeAnsCount = 0;
    int qnaireNeutralAnsCount = 0;
    for (TRvwQnaireAnswer tRvwQnaireAnswer : tRvwQnaireAnswers) {
      String result = tRvwQnaireAnswer.getResult();
      if (CommonUtils.isEqual(result, CDRConstants.QS_ASSESMENT_TYPE.POSITIVE.getDbType())) {
        qnairePositiveAnsCount = qnairePositiveAnsCount + 1;
      }
      else if (CommonUtils.isEqual(result, CDRConstants.QS_ASSESMENT_TYPE.NEGATIVE.getDbType())) {
        qnaireNegativeAnsCount = qnaireNegativeAnsCount + 1;
      }
      else if (CommonUtils.isEqual(result, CDRConstants.QS_ASSESMENT_TYPE.NEUTRAL.getDbType())) {
        qnaireNeutralAnsCount = qnaireNeutralAnsCount + 1;
      }
    }
    RvwQnaireRespVersModel qnaireRespVersModel = new RvwQnaireRespVersModel();

    long qnaireRespVersId = latestQnaireRespVers.getQnaireRespVersId();
    qnaireRespVersModel.setQnaireRespVersId(qnaireRespVersId);
    qnaireRespVersModel.setQnairePositiveAnsCount(qnairePositiveAnsCount);
    qnaireRespVersModel.setQnaireNegativeAnsCount(qnaireNegativeAnsCount);
    qnaireRespVersModel.setQnaireNeutralAnsCount(qnaireNeutralAnsCount);

    qnaireRespVersModel.setRvwQnaireLink(fetchQnaireExternaLink(rvwQnaireResponse, pidcVarId));
    qnaireRespVersModel.setQnaireBaselineExisting(
        CommonUtils.isNotEqual(latestQnaireRespVers.getRevNum(), CDRConstants.WORKING_SET_REV_NUM));

    qnaireRespVersModel.setQnaireReadyForProd(isQnaireReadyForProd(latestQnaireRespVers));
    qnaireRespVersModel.setQnaireVersStatus(latestQnaireRespVers.getQnaireVersStatus());

    rvwQnaireRespVersModelMap.put(qnaireRespVersId, qnaireRespVersModel);

  }

  private boolean isQnaireReadyForProd(final TRvwQnaireRespVersion latestQnaireRespVers) {
    boolean noNegAnsAllowed =
        CommonUtils.getBooleanType(latestQnaireRespVers.getTQuestionnaireVersion().getNoNegativeAnswersAllowed());

    String qnaireVersStatus = latestQnaireRespVers.getQnaireVersStatus();
    // Ready For Production is False
    // 1. If 'No Negative answers allowed' Flag is Set for Questionnaire Version, and answers are negative
    // 2. If Answer is 'Not Allowed to Finish WP
    if ((CommonUtils.isEqual(qnaireVersStatus, CDRConstants.QS_STATUS_TYPE.NOT_ALL_POSITIVE.getDbType()) &&
        noNegAnsAllowed) ||
        CommonUtils.isEqual(qnaireVersStatus, CDRConstants.QS_STATUS_TYPE.NOT_ALLOWED_FINISHED_WP.getDbType()) ||
        CommonUtils.isEqual(qnaireVersStatus, CDRConstants.QS_STATUS_TYPE.NOT_ALLOW_NEGATIVE.getDbType()) ||
        (latestQnaireRespVers.getRevNum() == 0)) {
      return false;
    }
    // Ready For Production is True, If all Questions are answered
    return CommonUtils.isEqual(qnaireVersStatus, CDRConstants.QS_STATUS_TYPE.ALL_POSITIVE.getDbType()) ||
        CommonUtils.isEqual(qnaireVersStatus, CDRConstants.QS_STATUS_TYPE.NOT_ALL_POSITIVE.getDbType());
  }

  /**
   * @param rvwQnaireResponse
   * @param pidcVarId
   * @throws IcdmException
   */
  public ExternalLinkInfo fetchQnaireExternaLink(final RvwQnaireResponse rvwQnaireResponse, final Long pidcVarId)
      throws IcdmException {

    List<RvwQnaireRespVariant> rvwQnaireRespVarList =
        new RvwQnaireRespVariantLoader(getServiceData()).getRvwQnaireRespVariant(rvwQnaireResponse.getId());

    RvwQnaireRespVariant qnaireRespVarForVariant = null;
    for (RvwQnaireRespVariant qnaireRespVariant : rvwQnaireRespVarList) {

      Long variantId = qnaireRespVariant.getVariantId();
      if (((null != variantId) && variantId.equals(pidcVarId)) || ((null == pidcVarId) && (variantId == null))) {
        qnaireRespVarForVariant = qnaireRespVariant;
        break;
      }
    }

    ExternalLinkInfoLoader externalLinkInfoLoader = new ExternalLinkInfoLoader(getServiceData());
    ExternalLinkInfo linkInfo = null;
    if ((qnaireRespVarForVariant != null) && (qnaireRespVarForVariant.getVariantId() != null)) {
      linkInfo = externalLinkInfoLoader.getLinkInfo(qnaireRespVarForVariant.getId(),
          MODEL_TYPE.RVW_QNAIRE_RESP_VARIANT.getTypeCode(), new HashMap<>());
    }
    else {
      linkInfo = externalLinkInfoLoader.getLinkInfo(rvwQnaireResponse.getId(),
          MODEL_TYPE.RVW_QNAIRE_RESPONSE.getTypeCode(), new HashMap<>());
    }

    return linkInfo;
  }

  /**
   * @param allRespWpQnaireRespVersMap
   * @param rvwQnaireResponseMap
   * @param respWpQnaireEntry
   * @param wpQnaireEntry
   * @param allQnaireRespWithLatestVers
   * @param rvwQnaireRespVersionLoader
   * @param rvwQnaireRespVersion
   * @throws DataException
   */
  private void fillAllQnaireRespWithLatestVers(
      final Map<Long, Map<Long, Set<RvwQnaireRespVersion>>> allRespWpQnaireRespVersMap,
      final Map<Long, RvwQnaireResponse> rvwQnaireResponseMap,
      final Entry<Long, Map<Long, Map<String, Long>>> respWpQnaireEntry,
      final Entry<Long, Map<String, Long>> wpQnaireEntry, final TRvwQnaireRespVersion allQnaireRespWithLatestVers,
      final RvwQnaireRespVersion rvwQnaireRespVersion)
      throws DataException {

    // Filling allRespWpQniareRespVersMap
    fillRespWpQnaireRespVersMap(allRespWpQnaireRespVersMap, respWpQnaireEntry.getKey(), wpQnaireEntry.getKey(),
        rvwQnaireRespVersion);

    // No Need to fill Rvw Qnaire Response Map if the qnaireResp is null(Simplified Qnaire)
    if (null != rvwQnaireRespVersion) {
      // Filling Rvw Qnaire Response Map
      fillRvwQnaireResponseMap(rvwQnaireResponseMap, rvwQnaireRespVersion,
          createDataObject(allQnaireRespWithLatestVers.getTRvwQnaireResponse()));
    }
  }


  /**
   * @param respWpQniareRespVersMap
   * @param allRespWpQnaireRespVersMap
   * @param rvwQnaireResponseMap
   * @param wpIdAndNameMap
   * @param respIdAndNameMap
   * @param respWpQniareStatusMap
   * @param rvwQnaireRespVersModelMap
   * @return
   */
  private CdrReportQnaireRespWrapper setCdrReportWrapperData(
      final Map<Long, Map<Long, Set<RvwQnaireRespVersion>>> respWpQniareRespVersMap,
      final Map<Long, Map<Long, Set<RvwQnaireRespVersion>>> allRespWpQnaireRespVersMap,
      final Map<Long, RvwQnaireResponse> rvwQnaireResponseMap, final Map<Long, String> wpIdAndNameMap,
      final Map<Long, String> respIdAndNameMap, final Map<Long, Map<Long, String>> respWpQniareStatusMap,
      final Map<Long, RvwQnaireRespVersModel> rvwQnaireRespVersModelMap) {
    CdrReportQnaireRespWrapper cdrReportQnaireRespWrapper = new CdrReportQnaireRespWrapper();
    cdrReportQnaireRespWrapper.setWpRespQnaireRespVersMap(respWpQniareRespVersMap);
    cdrReportQnaireRespWrapper.setAllWpRespQnaireRespVersMap(allRespWpQnaireRespVersMap);
    cdrReportQnaireRespWrapper.setQnaireResponseMap(rvwQnaireResponseMap);
    cdrReportQnaireRespWrapper.setWpRespQnaireRespVersStatusMap(respWpQniareStatusMap);
    cdrReportQnaireRespWrapper.setWpIdAndNameMap(wpIdAndNameMap);
    cdrReportQnaireRespWrapper.setRespIdAndNameMap(respIdAndNameMap);
    cdrReportQnaireRespWrapper.setRvwQnaireRespVersModelMap(rvwQnaireRespVersModelMap);
    return cdrReportQnaireRespWrapper;
  }

  /**
   * @param respWpQniareRespVersMap
   * @param respWpQniareStatusMap
   * @throws DataException
   */
  private void fillConsolidatedQnaireRespStatusMap(
      final Map<Long, Map<Long, Set<RvwQnaireRespVersion>>> respWpQniareRespVersMap,
      final Map<Long, Map<Long, String>> respWpQniareStatusMap)
      throws DataException {
    // fill the consolidated status map
    for (Entry<Long, Map<Long, Set<RvwQnaireRespVersion>>> respWpQnaireRespVerEntrySet : respWpQniareRespVersMap
        .entrySet()) {
      for (Entry<Long, Set<RvwQnaireRespVersion>> wpQnaireRespVerEntrySet : respWpQnaireRespVerEntrySet.getValue()
          .entrySet()) {
        for (RvwQnaireRespVersion rvwQnaireRespVersion : wpQnaireRespVerEntrySet.getValue()) {
          String qnaireRespVersStatus = rvwQnaireRespVersion.getQnaireRespVersStatus();

          if (CommonUtils.isEqual(CDRConstants.QS_STATUS_TYPE.NOT_ALL_POSITIVE.getDbType(), qnaireRespVersStatus)) {
            qnaireRespVersStatus = getStatusForQnaireWithNoNegAnsAllowed(rvwQnaireRespVersion, qnaireRespVersStatus);
          }

          fillRespWpQnaireRespStatusMap(respWpQniareStatusMap, respWpQnaireRespVerEntrySet.getKey(),
              wpQnaireRespVerEntrySet.getKey(), qnaireRespVersStatus);
        }
      }
    }
  }

  /**
   * @param rvwQnaireRespVersion
   * @param qnaireRespVersStatus
   * @return
   * @throws DataException
   */
  private String getStatusForQnaireWithNoNegAnsAllowed(final RvwQnaireRespVersion rvwQnaireRespVersion,
      String qnaireRespVersStatus)
      throws DataException {

    QnaireVersionModel qnaireVerModel = new QuestionnaireVersionLoader(getServiceData())
        .getQnaireVersionWithDetails(rvwQnaireRespVersion.getQnaireVersionId());

    // checking whether the questionnaire definition allows WP to be finished when the answers are negative
    String noNegativeAnsAllowedFlag = qnaireVerModel.getQuestionnaireVersion().getNoNegativeAnsAllowedFlag();
    if (CommonUtils.isNotNull(noNegativeAnsAllowedFlag) &&
        noNegativeAnsAllowedFlag.equalsIgnoreCase(ApicConstants.CODE_YES)) {
      qnaireRespVersStatus = CDRConstants.QS_STATUS_TYPE.NOT_ALLOW_NEGATIVE.getDbType();
    }
    return qnaireRespVersStatus;
  }

  /**
   * @param respWpQniareStatusMap as map
   * @param a2lRespId as a2l_resp_id
   * @param a2lWpID as a2l_wp_id
   * @param rvwQnaireVersStatus as input
   */
  private void fillRespWpQnaireRespStatusMap(final Map<Long, Map<Long, String>> respWpQniareStatusMap,
      final Long a2lRespId, final Long a2lWpID, final String rvwQnaireVersStatus) {
    if (respWpQniareStatusMap.containsKey(a2lRespId)) {
      Map<Long, String> wpQnaireRespStatusMap = respWpQniareStatusMap.get(a2lRespId);
      if (wpQnaireRespStatusMap.containsKey(a2lWpID)) {
        updateWpRespStatusWithIncomStatus(a2lWpID, rvwQnaireVersStatus, wpQnaireRespStatusMap);
      }
      else {
        wpQnaireRespStatusMap.put(a2lWpID, rvwQnaireVersStatus);
      }
    }
    else {
      Map<Long, String> wpQNaireRespStatusMap = new HashMap<>();
      wpQNaireRespStatusMap.put(a2lWpID, rvwQnaireVersStatus);
      respWpQniareStatusMap.put(a2lRespId, wpQNaireRespStatusMap);
    }
  }

  /**
   * @param a2lWpID
   * @param rvwQnaireVersStatus
   * @param wpQnaireRespStatusMap
   */
  private void updateWpRespStatusWithIncomStatus(final Long a2lWpID, final String rvwQnaireVersStatus,
      final Map<Long, String> wpQnaireRespStatusMap) {
    String qnaireRespStatus = wpQnaireRespStatusMap.get(a2lWpID);
    String incomQnaireStatus = CDRConstants.QS_STATUS_TYPE.getTypeByDbCode(rvwQnaireVersStatus).getDbType();
    String existQnaireStatus = CDRConstants.QS_STATUS_TYPE.getTypeByDbCode(qnaireRespStatus).getDbType();
    // if both the status are positive
    if (existQnaireStatus.equals(CDRConstants.QS_STATUS_TYPE.ALL_POSITIVE.getDbType()) &&
        incomQnaireStatus.equals(CDRConstants.QS_STATUS_TYPE.ALL_POSITIVE.getDbType())) {
      wpQnaireRespStatusMap.put(a2lWpID, incomQnaireStatus);
    }
    // update status based on conditions
    if (isIncomQnaireNegForQnaireWithNegAnsNotAllowed(incomQnaireStatus, existQnaireStatus) ||
        isIncomingQnaireNotAllPositive(incomQnaireStatus, existQnaireStatus) ||
        isIncomingQnaireNotAnswered(incomQnaireStatus, existQnaireStatus) ||
        isIncomingQnaireNotAllwedToFinishWP(incomQnaireStatus, existQnaireStatus)) {
      wpQnaireRespStatusMap.put(a2lWpID, incomQnaireStatus);
    }
  }

  /**
   * @param incomQnaireStatus
   * @param existQnaireStatus
   * @return
   */
  private boolean isIncomingQnaireNotAllwedToFinishWP(final String incomQnaireStatus, final String existQnaireStatus) {

    return (existQnaireStatus.equals(CDRConstants.QS_STATUS_TYPE.ALL_POSITIVE.getDbType()) ||
        existQnaireStatus.equals(CDRConstants.QS_STATUS_TYPE.NOT_ALL_POSITIVE.getDbType()) ||
        existQnaireStatus.equals(CDRConstants.QS_STATUS_TYPE.NOT_ALLOW_NEGATIVE.getDbType())) &&
        incomQnaireStatus.equals(CDRConstants.QS_STATUS_TYPE.NOT_ALLOWED_FINISHED_WP.getDbType());
  }

  /**
   * @param incomQnaireStatus
   * @param existQnaireStatus
   * @return
   */
  private boolean isIncomingQnaireNotAnswered(final String incomQnaireStatus, final String existQnaireStatus) {
    return (existQnaireStatus.equals(CDRConstants.QS_STATUS_TYPE.ALL_POSITIVE.getDbType()) ||
        existQnaireStatus.equals(CDRConstants.QS_STATUS_TYPE.NOT_ALL_POSITIVE.getDbType()) ||
        existQnaireStatus.equals(CDRConstants.QS_STATUS_TYPE.NOT_ALLOW_NEGATIVE.getDbType())) &&
        incomQnaireStatus.equals(CDRConstants.QS_STATUS_TYPE.NOT_ANSWERED.getDbType());
  }

  /**
   * @param incomQnaireStatus
   * @param existQnaireStatus
   * @return
   */
  private boolean isIncomingQnaireNotAllPositive(final String incomQnaireStatus, final String existQnaireStatus) {
    return existQnaireStatus.equals(CDRConstants.QS_STATUS_TYPE.ALL_POSITIVE.getDbType()) &&
        incomQnaireStatus.equals(CDRConstants.QS_STATUS_TYPE.NOT_ALL_POSITIVE.getDbType());
  }

  /**
   * @param incomQnaireStatus
   * @param existQnaireStatus
   * @return
   */
  private boolean isIncomQnaireNegForQnaireWithNegAnsNotAllowed(final String incomQnaireStatus,
      final String existQnaireStatus) {
    return (existQnaireStatus.equals(CDRConstants.QS_STATUS_TYPE.ALL_POSITIVE.getDbType()) ||
        existQnaireStatus.equals(CDRConstants.QS_STATUS_TYPE.NOT_ALL_POSITIVE.getDbType())) &&
        incomQnaireStatus.equals(CDRConstants.QS_STATUS_TYPE.NOT_ALLOW_NEGATIVE.getDbType());
  }

  private void fillRvwQnaireResponseMap(final Map<Long, RvwQnaireResponse> rvwQnaireResponseMap,
      final RvwQnaireRespVersion rvwQnaireRespVersion, final RvwQnaireResponse rvwQnaireResponse) {
    if (!rvwQnaireResponseMap.containsKey(rvwQnaireRespVersion.getId())) {
      rvwQnaireResponseMap.put(rvwQnaireRespVersion.getId(), rvwQnaireResponse);
    }
  }

  private void fillRespWpQnaireRespVersMap(
      final Map<Long, Map<Long, Set<RvwQnaireRespVersion>>> respWpQniareRespVersMap, final Long a2lRespId,
      final long a2lWpId, final RvwQnaireRespVersion qnaireRespVersion) {
    if (respWpQniareRespVersMap.containsKey(a2lRespId)) {
      Map<Long, Set<RvwQnaireRespVersion>> wpQnaireRespVerMap = respWpQniareRespVersMap.get(a2lRespId);
      if (wpQnaireRespVerMap.containsKey(a2lWpId)) {
        wpQnaireRespVerMap.get(a2lWpId).add(qnaireRespVersion);
      }
      else {
        Set<RvwQnaireRespVersion> rvwQnaireRespVersSet = new HashSet<>();
        rvwQnaireRespVersSet.add(qnaireRespVersion);
        wpQnaireRespVerMap.put(a2lWpId, rvwQnaireRespVersSet);
      }
    }
    else {
      Map<Long, Set<RvwQnaireRespVersion>> wpQnaireRespVerMap = new HashMap<>();
      Set<RvwQnaireRespVersion> rvwQnaireRespVersSet = new HashSet<>();
      rvwQnaireRespVersSet.add(qnaireRespVersion);
      wpQnaireRespVerMap.put(a2lWpId, rvwQnaireRespVersSet);
      respWpQniareRespVersMap.put(a2lRespId, wpQnaireRespVerMap);
    }
  }

  /**
   * Method to fill the map of a2l_resp_id , map of a2l_wp_id and Map of qnaire_resp
   *
   * @param respWpQniareMap as input map
   * @param Long a2lRespId as a2l_resp_id
   * @param Long a2lWpId as a2l_wp_id
   * @param Long qnaireRespId qnaire_resp_id
   * @param Long variantId as input
   * @throws DataException
   */
  private void fillRespWpQnaireMap(final Map<Long, Map<Long, Map<String, Long>>> respWpQniareMap, final Long a2lRespId,
      final Long a2lWpId, final Long qnaireRespId, final Long variantId)
      throws DataException {
    RvwQnaireResponse inputQnaireResponse =
        CommonUtils.isEqual(ApicConstants.SIMP_QUES_RESP_ID, qnaireRespId) ? null : getDataObjectByID(qnaireRespId);
    // check whether Resp Id is already available
    String qnaireRespName =
        (null != inputQnaireResponse) ? inputQnaireResponse.getName() : ApicConstants.SIMP_QUES_RESP_NAME;
    if (respWpQniareMap.containsKey(a2lRespId)) {
      // check whether Wp Id is already available
      Map<Long, Map<String, Long>> wpQnaireRespMap = respWpQniareMap.get(a2lRespId);
      if (wpQnaireRespMap.containsKey(a2lWpId)) {
        Map<String, Long> qnaireRespMap = wpQnaireRespMap.get(a2lWpId);
        populateQnaireRespMap(a2lRespId, a2lWpId, qnaireRespId, variantId, inputQnaireResponse, qnaireRespName,
            qnaireRespMap);
      }
      else {
        // Add WP Id and Qnaire Resp Id
        Map<String, Long> qnaireRespIDSet = new java.util.HashMap<>();
        qnaireRespIDSet.put(qnaireRespName, qnaireRespId);
        wpQnaireRespMap.put(a2lWpId, qnaireRespIDSet);
      }
    }
    else {
      // Add Resp Id ,WP Id and Qnaire Resp Id
      Map<Long, Map<String, Long>> wpQnaireMap = new HashMap<>();
      Map<String, Long> qnaireRespMap = new java.util.HashMap<>();
      qnaireRespMap.put(qnaireRespName, qnaireRespId);
      wpQnaireMap.put(a2lWpId, qnaireRespMap);
      respWpQniareMap.put(a2lRespId, wpQnaireMap);
    }
  }

  /**
   * @param a2lRespId
   * @param a2lWpId
   * @param qnaireRespId
   * @param variantId
   * @param inputQnaireResponse
   * @param qnaireRespName
   * @param qnaireRespMap
   * @throws DataException
   */
  private void populateQnaireRespMap(final Long a2lRespId, final Long a2lWpId, final Long qnaireRespId,
      final Long variantId, final RvwQnaireResponse inputQnaireResponse, final String qnaireRespName,
      final Map<String, Long> qnaireRespMap)
      throws DataException {
    if (qnaireRespMap.containsKey(qnaireRespName)) {
      Long existQnaireRespId = qnaireRespMap.get(qnaireRespName);
      RvwQnaireResponse existQnaireResp = getDataObjectByID(existQnaireRespId);
      if (CommonUtils.isNotNull(variantId) && isExistingQnaireRespVariantValid(variantId, existQnaireResp) &&
          (null != inputQnaireResponse) && isSameCopiedQnaireLinkedInDest(
              inputQnaireResponse.getSecondaryQnaireLinkMap(), variantId, a2lRespId, a2lWpId)) {
        qnaireRespMap.put(qnaireRespName, inputQnaireResponse.getId());
      }
    }
    else {
      qnaireRespMap.put(qnaireRespName, qnaireRespId);
    }
  }

  /**
   * @param variantId
   * @param existQnaireResp
   * @return
   */
  private boolean isExistingQnaireRespVariantValid(final Long variantId, final RvwQnaireResponse existQnaireResp) {
    return CommonUtils.isNullOrEmpty(existQnaireResp.getSecondaryQnaireLinkMap()) &&
        CommonUtils.isNotNull(existQnaireResp.getVariantId()) && existQnaireResp.getVariantId().equals(variantId);
  }


  /**
   * Method identify the latest questionnaire response version based on revision number
   *
   * @param rvwQnaireRespVersions
   * @param includeWorkingSet
   * @return TRvwQnaireRespVersion
   */
  public TRvwQnaireRespVersion getLatestQnaireRespVersUsingRevNum(
      final Set<TRvwQnaireRespVersion> rvwQnaireRespVersions, final boolean includeWorkingSet) {
    // filtering the working set from the set of TRvwQnaireRespVersion based on input preference
    List<TRvwQnaireRespVersion> qnaireRespVersions =
        includeWorkingSet ? rvwQnaireRespVersions.stream().collect(Collectors.toList())
            : rvwQnaireRespVersions.stream().filter(val -> val.getReviewedDate() != null).collect(Collectors.toList());
    // sort the set in desc order
    qnaireRespVersions.sort(Comparator.comparing(TRvwQnaireRespVersion::getRevNum).reversed());
    if (!qnaireRespVersions.isEmpty()) {
      // return the first elemet of set
      return qnaireRespVersions.get(0);
    }
    return null;
  }


  /**
   * @param entity as TRvwQnaireResponse
   * @return as TRvwQnaireRespVariant
   */
  public TRvwQnaireRespVariant getPrimaryVariantEntity(final TRvwQnaireResponse entity) {
    TRvwQnaireRespVariant tRvwQnaireRespVariant = new TRvwQnaireRespVariant();
    Timestamp createdDate = null;
    if (CommonUtils.isNotNull(entity.getTRvwQnaireRespVariants())) {
      Iterator<TRvwQnaireRespVariant> iterator = entity.getTRvwQnaireRespVariants().iterator();
      while (iterator.hasNext()) {
        TRvwQnaireRespVariant tRvwQnaireRespVar = iterator.next();
        if ((createdDate == null) || createdDate.after(tRvwQnaireRespVar.getCreatedDate())) {
          createdDate = tRvwQnaireRespVar.getCreatedDate();
          tRvwQnaireRespVariant = tRvwQnaireRespVar;
        }
      }
    }
    return tRvwQnaireRespVariant;
  }

  /**
   * @param entity
   * @param primaryVariant
   * @return
   * @throws DataException
   */
  private Map<Long, Map<Long, Set<Long>>> getSecondaryVariant(final TRvwQnaireResponse entity,
      final Long primaryVariantId, final Long primaryA2lWpId, final Long primaryA2lRespId) {
    Map<Long, Map<Long, Set<Long>>> secondaryQnaireLinkMap = new HashMap<>();
    if (CommonUtils.isNotNull(entity.getTRvwQnaireRespVariants())) {
      Iterator<TRvwQnaireRespVariant> iterator = entity.getTRvwQnaireRespVariants().iterator();
      while (iterator.hasNext()) {
        TRvwQnaireRespVariant tRvwQnaireRespVar = iterator.next();
        // removing the primary Linked Qnaire
        if (!checkIfPrimaryLinking(primaryVariantId, primaryA2lWpId, primaryA2lRespId, tRvwQnaireRespVar)) {
          loadSecondaryLinkMap(tRvwQnaireRespVar, secondaryQnaireLinkMap);
        }
      }
    }
    return secondaryQnaireLinkMap;
  }

  /**
   * @param tRvwQnaireRespVar
   */
  private Map<Long, Map<Long, Set<Long>>> loadSecondaryLinkMap(final TRvwQnaireRespVariant tRvwQnaireRespVar,
      final Map<Long, Map<Long, Set<Long>>> secondaryQnaireLinkMap) {

    Long varId = ApicConstants.NO_VARIANT_ID;
    Long respId = tRvwQnaireRespVar.gettA2lResponsibility().getA2lRespId();
    Long wpId = tRvwQnaireRespVar.gettA2lWorkPackage().getA2lWpId();
    if (CommonUtils.isNotNull(tRvwQnaireRespVar.getTabvProjectVariant())) {
      varId = tRvwQnaireRespVar.getTabvProjectVariant().getVariantId();
    }
    if (secondaryQnaireLinkMap.containsKey(varId)) {
      Map<Long, Set<Long>> respWpMap = secondaryQnaireLinkMap.get(varId);
      if (respWpMap.containsKey(respId)) {
        Set<Long> wpIds = respWpMap.get(respId);
        wpIds.add(wpId);
        respWpMap.put(respId, wpIds);
      }
      else {
        Set<Long> wpIds = new HashSet<>();
        wpIds.add(wpId);
        respWpMap.put(respId, wpIds);
      }
      secondaryQnaireLinkMap.put(varId, respWpMap);
    }
    else {
      Map<Long, Set<Long>> respWpMap = new HashMap<>();
      Set<Long> wpIds = new HashSet<>();
      wpIds.add(wpId);
      respWpMap.put(respId, wpIds);
      secondaryQnaireLinkMap.put(varId, respWpMap);
    }
    return secondaryQnaireLinkMap;
  }

  /**
   * @param primaryVariantId
   * @param primaryA2lWpId
   * @param primaryA2lRespId
   * @param tRvwQnaireRespVar
   * @return
   */
  private boolean checkIfPrimaryLinking(final Long primaryVariantId, final Long primaryA2lWpId,
      final Long primaryA2lRespId, final TRvwQnaireRespVariant tRvwQnaireRespVar) {
    if (CommonUtils.isNull(tRvwQnaireRespVar.getTabvProjectVariant()) &&
        CommonUtils.isEqual(primaryVariantId, ApicConstants.NO_VARIANT_ID)) {
      return CommonUtils.isEqual(tRvwQnaireRespVar.gettA2lResponsibility().getA2lRespId(), primaryA2lRespId) &&
          CommonUtils.isEqual(tRvwQnaireRespVar.gettA2lWorkPackage().getA2lWpId(), primaryA2lWpId);
    }
    else if (CommonUtils.isNull(tRvwQnaireRespVar.getTabvProjectVariant()) ||
        CommonUtils.isEqual(primaryVariantId, ApicConstants.NO_VARIANT_ID)) {
      return false;
    }
    return CommonUtils.isEqual(tRvwQnaireRespVar.getTabvProjectVariant().getVariantId(), primaryVariantId) &&
        CommonUtils.isEqual(tRvwQnaireRespVar.gettA2lResponsibility().getA2lRespId(), primaryA2lRespId) &&
        CommonUtils.isEqual(tRvwQnaireRespVar.gettA2lWorkPackage().getA2lWpId(), primaryA2lWpId);
  }

  /**
   * @param entity
   * @return
   */
  public String getQnaireRespName(final TRvwQnaireResponse entity) {
    StringBuilder wpName = new StringBuilder();
    // to fetch the questionnaire version
    if (CommonUtils.isNotEmpty(entity.getTRvwQnaireRespVersions())) {
      TQuestionnaireVersion tQuestionnaireVersion =
          getWorkingSetQuestionnaireVersion(entity).getTQuestionnaireVersion();
      TQuestionnaire tQuestionnaire = tQuestionnaireVersion.getTQuestionnaire();
      String nameEng = getLangSpecTxt(tQuestionnaire.getNameEng(), tQuestionnaire.getNameGer());
      if (CommonUtils.isEmptyString(nameEng)) {
        nameEng = tQuestionnaire.getTWorkpackageDivision().getTWorkpackage().getWpNameE();
      }
      wpName.append(nameEng);
      wpName.append(" (Version ");
      wpName.append(tQuestionnaireVersion.getMajorVersionNum());
      if (null != tQuestionnaireVersion.getMinorVersionNum()) {
        wpName.append(".");
        wpName.append(tQuestionnaireVersion.getMinorVersionNum());
      }
      wpName.append(")");
    }
    return wpName.toString();

  }

  /**
   * @param entity TRvwQnaireResponse
   * @return TRvwQnaireRespVersion
   */
  public TRvwQnaireRespVersion getWorkingSetQuestionnaireVersion(final TRvwQnaireResponse entity) {
    TRvwQnaireRespVersion tRvwQnaireRespVersion = null;
    for (TRvwQnaireRespVersion tRvwQnaireRespVer : entity.getTRvwQnaireRespVersions()) {
      if (RvwQnaireRespVersionLoader.isWorkingSet(tRvwQnaireRespVer)) {
        tRvwQnaireRespVersion = tRvwQnaireRespVer;
        break;
      }
    }

    if (tRvwQnaireRespVersion == null) {
      throw new IcdmRuntimeException("Working set does not exist for this Questionnaire response");
    }

    return tRvwQnaireRespVersion;
  }

  /**
   * @param pidcVerId pidc Version ID
   * @return true if this PIDC version has questionnaire responses
   * @throws IcdmException
   */
  public boolean isPidcQnaireRespPresent(final Long pidcVerId) throws IcdmException {
    return CDRReviewResultUtil.isRvwQnaireRespAllowed(getServiceData(), pidcVerId) &&
        (!getQnaireRespList(pidcVerId).isEmpty() || isSimpQuesAvailable(pidcVerId));
  }

  /**
   * @param pidcVerId
   * @return
   */
  public boolean isSimpQuesAvailable(final Long pidcVerId) {
    // QnaireRespId of RvwQnaireRespVariant will be null if the Wp/Resp has Simplified Qnaire
    if (CommonUtils.isNullOrEmpty(this.qnaireRespVarList)) {
      this.qnaireRespVarList = new RvwQnaireRespVariantLoader(getServiceData()).getQnaireRespVariantList(pidcVerId);
    }

    return CommonUtils.isNotEmpty(this.qnaireRespVarList.stream()
        .filter(rvwQnaireRespVariants -> CommonUtils.isNull(rvwQnaireRespVariants.getTRvwQnaireResponse()))
        .collect(Collectors.toSet()));
  }

  /**
   * @param pidcVerId
   * @param varId
   * @param rvwWpAndRespModel
   * @return
   * @throws DataException
   */
  public boolean isSimpAvailAndGnrlQnaireNotAvail(final Long pidcVerId, final Long genQniareVerId, final Long varId,
      final RvwWpAndRespModel rvwWpAndRespModel)
      throws DataException {

    return (!isGnrlQnaireAlreadyAvail(pidcVerId, genQniareVerId, varId, rvwWpAndRespModel) &&
        isSimpQuesAvailable(pidcVerId));
  }


  /**
   * Method to check whether General qnaire is already Available and OBD Qnaire not available for adding OBD general
   * Qnaire (Handled for Old review)
   *
   * @param pidcVerId
   * @param varId
   * @param rvwWpAndRespModel
   * @return
   * @throws DataException
   */
  public boolean isGnrlQuesAvailAndOBDQnaireNotAvail(final Long pidcVerId, final Long stnrdGenQnaireVerId,
      final Long obdGenQnaireVerId, final Long varId, final RvwWpAndRespModel rvwWpAndRespModel)
      throws DataException {

    return (!isGnrlQnaireAlreadyAvail(pidcVerId, obdGenQnaireVerId, varId, rvwWpAndRespModel) &&
        isGnrlQnaireAlreadyAvail(pidcVerId, stnrdGenQnaireVerId, varId, rvwWpAndRespModel));
  }

  /**
   * @param pidcVerId
   * @param genQnaireVerId
   * @param varId
   * @param rvwWpAndRespModel
   * @throws DataException
   */
  private boolean isGnrlQnaireAlreadyAvail(final Long pidcVerId, final Long genQnaireVerId, final Long varId,
      final RvwWpAndRespModel rvwWpAndRespModel)
      throws DataException {
    this.qnaireRespVarList = new RvwQnaireRespVariantLoader(getServiceData()).getQnaireRespVariantList(pidcVerId);
    List<TRvwQnaireRespVersion> matchingQnaireResp = new ArrayList<>();

    for (TRvwQnaireRespVariant rvwQnaireRespVariants : this.qnaireRespVarList) {
      TabvProjectVariant projVar = rvwQnaireRespVariants.getTabvProjectVariant();
      Long tabvProjVarId =
          CommonUtils.isNull(projVar) || CommonUtils.isEqual(projVar.getVariantId(), ApicConstants.NO_VARIANT_ID) ? null
              : projVar.getVariantId();
      if (CommonUtils.isEqual(tabvProjVarId, varId) &&
          CommonUtils.isEqual(rvwWpAndRespModel.getA2lRespId(),
              rvwQnaireRespVariants.gettA2lResponsibility().getA2lRespId()) &&
          CommonUtils.isEqual(rvwWpAndRespModel.getA2lWpId(),
              rvwQnaireRespVariants.gettA2lWorkPackage().getA2lWpId())) {
        TRvwQnaireResponse tRvwQnaireResponse = rvwQnaireRespVariants.getTRvwQnaireResponse();
        // tRvwQnaireResponse is Null for Simplified Qnaire
        if (CommonUtils.isNotNull(tRvwQnaireResponse)) {
          RvwQnaireRespVersionLoader rvwQnaireRespVersionLoader = new RvwQnaireRespVersionLoader(getServiceData());

          matchingQnaireResp.addAll(rvwQnaireRespVersionLoader
              .getQnaireRespVersionsByRespId(tRvwQnaireResponse.getQnaireRespId()).values().stream()
              .map(rvwQnaireRespVers -> rvwQnaireRespVersionLoader.getEntityObject(rvwQnaireRespVers.getId()))
              .filter(rvwQnaireRespVers -> CommonUtils.isEqual(
                  rvwQnaireRespVers.getTQuestionnaireVersion().getTQuestionnaire().getQnaireId(), genQnaireVerId))
              .collect(Collectors.toList()));
        }
      }
    }

    return CommonUtils.isNotEmpty(matchingQnaireResp);
  }

  /**
   * @param pidcVerId pidc Version ID
   * @return the questionnaire response details of this PIDC Version
   * @throws DataException data error
   */
  public PidcQnaireInfo getPidcQnaireResponse(final Long pidcVerId) throws DataException {

    Map<Long, PidcVariant> pidcVarIdVarMap = new HashMap<>();
    // key - variant id , Value - Map of <resp id, Map<wp id , Set<QnaireRespId>>>>
    Map<Long, Map<Long, Map<Long, Set<Long>>>> varRespWpQniareMap = new HashMap<>();
    Map<Long, A2lResponsibility> a2lRespMap = new HashMap<>();
    Map<Long, A2lWorkPackage> a2lWpMap = new HashMap<>();
    Map<Long, RvwQnaireResponse> rvwQnaireRespMap = new HashMap<>();
    // key - Qnaire Resp id , value - RvwQnaireRespVersion - Working Set
    Map<Long, RvwQnaireRespVersion> rvwQnaireRespVersMap = new HashMap<>();

    Set<TRvwQnaireRespVariant> qnaireRespVariantList =
        new RvwQnaireRespVariantLoader(getServiceData()).getQnaireRespVariantList(pidcVerId);

    for (TRvwQnaireRespVariant tRvwQnaireRespVar : qnaireRespVariantList) {
      Long variantId = tRvwQnaireRespVar.getTabvProjectVariant() != null
          ? tRvwQnaireRespVar.getTabvProjectVariant().getVariantId() : null;

      // This is added to compensate the non migrated records
      // once the migration is successful, not null constrains can be added to the DB columns
      // and the null check can be removed
      if ((null != tRvwQnaireRespVar.gettA2lResponsibility()) && (null != tRvwQnaireRespVar.gettA2lWorkPackage())) {
        Long a2lRespId = tRvwQnaireRespVar.gettA2lResponsibility().getA2lRespId();
        Long a2lWpId = tRvwQnaireRespVar.gettA2lWorkPackage().getA2lWpId();
        fillA2lRespMap(a2lRespMap, a2lRespId);
        fillA2LWpMap(a2lWpMap, a2lWpId);

        TRvwQnaireResponse tRvwQnaireResponse = tRvwQnaireRespVar.getTRvwQnaireResponse();
        // QnaireRespId will be Null for Simplified Qnaire
        Long qnaireRespId = CommonUtils.isNotNull(tRvwQnaireResponse) ? tRvwQnaireResponse.getQnaireRespId()
            : ApicConstants.SIMP_QUES_RESP_ID;
        if (CommonUtils.isNotNull(tRvwQnaireResponse)) {
          fillRvwQnaireRespMap(rvwQnaireRespMap, qnaireRespId);
          fillRvwQnaireRespVersMap(rvwQnaireRespVersMap, qnaireRespId);
        }

        fillVarRespWpQnaireRespMap(pidcVarIdVarMap, varRespWpQniareMap, variantId, a2lRespId, a2lWpId, qnaireRespId);
      }
    }

    PidcQnaireInfo qnaireInfo = new PidcQnaireInfo();

    qnaireInfo.getVarRespWpQniareMap().putAll(varRespWpQniareMap);
    qnaireInfo.getA2lRespMap().putAll(a2lRespMap);
    qnaireInfo.getA2lWpMap().putAll(a2lWpMap);
    qnaireInfo.getRvwQnaireRespMap().putAll(rvwQnaireRespMap);
    qnaireInfo.getVarIdVarMap().putAll(pidcVarIdVarMap);
    qnaireInfo.getRvwQnaireRespVersMap().putAll(rvwQnaireRespVersMap);

    return qnaireInfo;
  }

  /**
   * @param pidcVarIdVarMap
   * @param varRespWpQniareMap
   * @param variantId
   * @param a2lRespId
   * @param a2lWpId
   * @param qnaireRespId
   * @throws DataException
   */
  private void fillVarRespWpQnaireRespMap(final Map<Long, PidcVariant> pidcVarIdVarMap,
      final Map<Long, Map<Long, Map<Long, Set<Long>>>> varRespWpQniareMap, Long variantId, final Long a2lRespId,
      final Long a2lWpId, final Long qnaireRespId)
      throws DataException {
    if (variantId == null) {
      // For no-variant variant id is -1
      variantId = ApicConstants.NO_VARIANT_ID;
      fillVarRespWpQnaireMap(varRespWpQniareMap, variantId, a2lRespId, a2lWpId, qnaireRespId);
    }
    else {
      fillPidcVarMap(pidcVarIdVarMap, variantId);
      fillVarRespWpQnaireMap(varRespWpQniareMap, variantId, a2lRespId, a2lWpId, qnaireRespId);
    }
  }


  /**
   * @param rvwQnaireRespVersMap key - qnaire resp id Value - Qnairerespversion working set
   * @param qnaireRespId qnaire resp id
   * @throws IcdmException exception in ws
   */
  private void fillRvwQnaireRespVersMap(final Map<Long, RvwQnaireRespVersion> rvwQnaireRespVersMap,
      final Long qnaireRespId)
      throws DataException {

    Map<Long, RvwQnaireRespVersion> retMap =
        new RvwQnaireRespVersionLoader(getServiceData()).getQnaireRespVersionsByRespId(qnaireRespId);

    for (RvwQnaireRespVersion rvwQnaireRespVersion : retMap.values()) {
      if (RvwQnaireRespVersionLoader.isWorkingSet(rvwQnaireRespVersion)) {
        rvwQnaireRespVersMap.put(qnaireRespId, rvwQnaireRespVersion);
      }
    }
  }

  /**
   * @param varRespWpQniareMap
   * @param variantId
   * @param a2lRespId
   * @param a2lWpId
   * @param qnaireRespId
   */
  private void fillVarRespWpQnaireMap(final Map<Long, Map<Long, Map<Long, Set<Long>>>> varRespWpQniareMap,
      final Long variantId, final Long a2lRespId, final Long a2lWpId, final Long qnaireRespId) {

    if (varRespWpQniareMap.containsKey(variantId)) {
      // check whether Resp Id is already available
      Map<Long, Map<Long, Set<Long>>> wpRespMap = varRespWpQniareMap.get(variantId);
      if (wpRespMap.containsKey(a2lRespId)) {
        // check whether Wp Id is already available
        Map<Long, Set<Long>> wpQnaireRespMap = wpRespMap.get(a2lRespId);
        if (wpQnaireRespMap.containsKey(a2lWpId)) {
          wpQnaireRespMap.get(a2lWpId).add(qnaireRespId);
        }
        else {
          // Add WP Id and Qnaire Resp Id
          Set<Long> qnaireRespIDSet = new java.util.HashSet<>();
          qnaireRespIDSet.add(qnaireRespId);
          wpQnaireRespMap.put(a2lWpId, qnaireRespIDSet);
        }
      }
      else {
        // Add Resp Id ,WP Id and Qnaire Resp Id
        Map<Long, Set<Long>> wpQnaireMap = new HashMap<>();
        Set<Long> qnaireRespIDSet = new java.util.HashSet<>();
        qnaireRespIDSet.add(qnaireRespId);
        wpQnaireMap.put(a2lWpId, qnaireRespIDSet);
        wpRespMap.put(a2lRespId, wpQnaireMap);
      }
    }
    else {
      addVarRespWpQnaireMap(varRespWpQniareMap, variantId, a2lRespId, a2lWpId, qnaireRespId);
    }
  }

  /**
   * @param pidcVarIdVarMap
   * @param varLoader
   * @param variantId
   * @throws DataException
   */
  private void fillPidcVarMap(final Map<Long, PidcVariant> pidcVarIdVarMap, final long variantId) throws DataException {
    if (!pidcVarIdVarMap.containsKey(variantId)) {
      pidcVarIdVarMap.put(variantId, new PidcVariantLoader(getServiceData()).getDataObjectByID(variantId));
    }
  }

  /**
   * @param varRespWpQniareMap
   * @param variantId
   * @param a2lRespId
   * @param a2lWpId
   * @param qnaireRespId
   */
  private void addVarRespWpQnaireMap(final Map<Long, Map<Long, Map<Long, Set<Long>>>> varRespWpQniareMap,
      final long variantId, final long a2lRespId, final long a2lWpId, final long qnaireRespId) {

    Map<Long, Map<Long, Set<Long>>> respWPQnaireMap = new HashMap<>();
    Map<Long, Set<Long>> wpQnaireMap = new HashMap<>();
    Set<Long> qnaireRespIDSet = new java.util.HashSet<>();
    qnaireRespIDSet.add(qnaireRespId);
    wpQnaireMap.put(a2lWpId, qnaireRespIDSet);
    respWPQnaireMap.put(a2lRespId, wpQnaireMap);
    varRespWpQniareMap.put(variantId, respWPQnaireMap);
  }

  /**
   * @param rvwQnaireRespMap
   * @param a2lRespId
   * @param qnaireRespId
   * @throws DataException
   */
  private void fillRvwQnaireRespMap(final Map<Long, RvwQnaireResponse> rvwQnaireRespMap, final long qnaireRespId)
      throws DataException {

    if (!rvwQnaireRespMap.containsKey(qnaireRespId)) {
      rvwQnaireRespMap.put(qnaireRespId, getDataObjectByID(qnaireRespId));
    }
  }

  /**
   * @param a2lWpMap
   * @param a2lWorkPackageLoader
   * @param a2lRespId
   * @param a2lWpId
   * @throws DataException
   */
  private void fillA2LWpMap(final Map<Long, A2lWorkPackage> a2lWpMap, final long a2lWpId) throws DataException {
    if (!a2lWpMap.containsKey(a2lWpId)) {
      a2lWpMap.put(a2lWpId, new A2lWorkPackageLoader(getServiceData()).getDataObjectByID(a2lWpId));
    }
  }

  /**
   * @param a2lRespMap
   * @param a2lRespLoader
   * @param a2lRespId
   * @throws DataException
   */
  private void fillA2lRespMap(final Map<Long, A2lResponsibility> a2lRespMap, final long a2lRespId)
      throws DataException {

    if (!a2lRespMap.containsKey(a2lRespId)) {
      a2lRespMap.put(a2lRespId, new A2lResponsibilityLoader(getServiceData()).getDataObjectByID(a2lRespId));
    }
  }

  /**
   * @param pidcVerId PIDC Version ID
   * @return list of response entities
   */
  public List<TRvwQnaireResponse> getQnaireRespList(final Long pidcVerId) {

    TypedQuery<TRvwQnaireResponse> tQuery =
        getEntMgr().createNamedQuery(TRvwQnaireResponse.GET_BY_PIDCVERSID, TRvwQnaireResponse.class);
    tQuery.setParameter("pidcVersId", pidcVerId);

    return tQuery.getResultList();
  }

  /**
   * Gets the qnaire response model.
   *
   * @param rvwQnaireRespVerId the qnaire resp version id
   * @return the qnaire response model
   * @throws DataException the data exception
   */
  public RvwQnaireResponseModel getQnaireResponseModel(final Long rvwQnaireRespVerId) throws DataException {

    RvwQnaireResponseModel respModel = new RvwQnaireResponseModel();

    RvwQnaireRespVersion qnaireRespVersion =
        new RvwQnaireRespVersionLoader(getServiceData()).getDataObjectByID(rvwQnaireRespVerId);

    TRvwQnaireRespVersion rvwQnaireRespVersion =
        new RvwQnaireRespVersionLoader(getServiceData()).getEntityObject(rvwQnaireRespVerId);

    RvwQnaireResponse rvwQnareRsp = getDataObjectByID(rvwQnaireRespVersion.getTRvwQnaireResponse().getQnaireRespId());
    respModel.setRvwQnrResponse(rvwQnareRsp);


    respModel.setRvwQnrRespVersion(qnaireRespVersion);

    loadRvwQnaireAnswers(respModel, rvwQnaireRespVersion);

    respModel.setPidcVersion(new PidcVersionLoader(getServiceData()).getDataObjectByID(rvwQnareRsp.getPidcVersId()));
    respModel.setPidc(new PidcLoader(getServiceData()).getDataObjectByID(rvwQnareRsp.getPidcId()));
    if (CommonUtils.isNotNull(rvwQnareRsp.getVariantId()) &&
        CommonUtils.isNotEqual(ApicConstants.NO_VARIANT_ID, rvwQnareRsp.getVariantId())) {
      respModel.setPidcVariant(new PidcVariantLoader(getServiceData()).getDataObjectByID(rvwQnareRsp.getVariantId()));
    }
    // Modifiable should be possible only for working set
    respModel.setModifiable(isModifiable(rvwQnaireRespVersion));
    respModel.setQnaireRespA2lResponsibility(new A2lResponsibilityLoader(getServiceData()).getDataObjectByID(
        getPrimaryVariantEntity(rvwQnaireRespVersion.getTRvwQnaireResponse()).gettA2lResponsibility().getA2lRespId()));
    return respModel;
  }

  /**
   * Load answers for questionnaire response version
   *
   * @param respModel
   * @param qnaireRespVers
   * @throws DataException
   */
  private void loadRvwQnaireAnswers(final RvwQnaireResponseModel respModel, final TRvwQnaireRespVersion qnaireRespVers)
      throws DataException {

    Map<Long, RvwQnaireAnswer> rvwQnrAnswrMap = new HashMap<>();
    Map<Long, Map<Long, RvwQnaireAnswerOpl>> rvwQnaireAnsOplMap = new HashMap<>();
    Map<Long, Map<Long, Link>> rvwQnaireAnsLinksMap = new HashMap<>();

    if (CommonUtils.isNotEmpty(qnaireRespVers.getTRvwQnaireAnswers())) {
      RvwQnaireAnswerLoader ansLoader = new RvwQnaireAnswerLoader(getServiceData());
      for (TRvwQnaireAnswer rqAnswr : qnaireRespVers.getTRvwQnaireAnswers()) {
        rvwQnrAnswrMap.put(rqAnswr.getRvwAnswerId(), ansLoader.createDataObject(rqAnswr));
        // General Questionnaire related answers
        if (qnaireRespVers.getTQuestionnaireVersion() != rqAnswr.getTQuestion().getTQuestionnaireVersion()) {
          respModel.setGeneralQnaireVersId(rqAnswr.getTQuestion().getTQuestionnaireVersion().getQnaireVersId());
          // added to remove the rvw answer that doesnt associated to the questionnaire version
          rvwQnrAnswrMap.remove(rqAnswr.getRvwAnswerId());
        }
        // Opl
        Map<Long, RvwQnaireAnswerOpl> qnaireAnsOplMap = loadQnaireAnsOpl(rqAnswr);
        if (!qnaireAnsOplMap.isEmpty()) {
          rvwQnaireAnsOplMap.put(rqAnswr.getRvwAnswerId(), qnaireAnsOplMap);
        }

        Map<Long, Link> linksMap = getQnaireAnsLinks(rqAnswr);
        if (!linksMap.isEmpty()) {
          rvwQnaireAnsLinksMap.put(rqAnswr.getRvwAnswerId(), linksMap);
        }
      }
    }
    respModel.setRvwQnrAnswrMap(rvwQnrAnswrMap);
    respModel.setOpenPointsMap(rvwQnaireAnsOplMap);
    respModel.setLinksMap(rvwQnaireAnsLinksMap);
  }

  /**
   * @param rqAnswr
   * @return
   * @throws DataException
   */
  private Map<Long, Link> getQnaireAnsLinks(final TRvwQnaireAnswer rqAnswr) throws DataException {
    return new LinkLoader(getServiceData()).getLinksByNode(rqAnswr.getRvwAnswerId(),
        MODEL_TYPE.RVW_QNAIRE_ANS.getTypeCode());
  }

  /**
   * @param rqAnswr
   * @return
   * @throws DataException
   */
  private Map<Long, RvwQnaireAnswerOpl> loadQnaireAnsOpl(final TRvwQnaireAnswer rqAnswr) throws DataException {
    Map<Long, RvwQnaireAnswerOpl> oplMap = new HashMap<>();
    for (TRvwQnaireAnswerOpl oplEntity : rqAnswr.getTQnaireAnsOpenPoints()) {
      RvwQnaireAnswerOpl qnaireAnsOpl = new RvwQnaireAnswerOplLoader(getServiceData()).createDataObject(oplEntity);
      oplMap.put(qnaireAnsOpl.getId(), qnaireAnsOpl);
    }
    return oplMap;
  }

  /**
   * Validate if current user has access to this Qnaire response
   *
   * @param pidcId pidcId
   * @param a2lRespId a2lRespId
   * @return true if current user has access to this Qnaire response
   * @throws DataException data retrieval error
   */
  public boolean validateQnaireAccess(final Long pidcId, final Long a2lRespId) throws DataException {
    if (new NodeAccessLoader(getServiceData()).isCurrentUserOwner(pidcId)) {
      return true;
    }

    if (a2lRespId != 0L) {
      A2lResponsibility a2lResp = new A2lResponsibilityLoader(getServiceData()).getDataObjectByID(a2lRespId);
      return (a2lResp.getUserId() == null) || CommonUtils.isEqual(a2lResp.getUserId(), getServiceData().getUserId());
    }

    return false;
  }

  /**
   * @param rvwQnaireRespVersion the rvwQnaireRespVersion
   * @return true, if is modifiable
   * @throws DataException the data exception
   */
  private boolean isModifiable(final TRvwQnaireRespVersion rvwQnaireRespVersion) throws DataException {
    if (RvwQnaireRespVersionLoader.isWorkingSet(rvwQnaireRespVersion)) {
      // Iterating through all the linked Qnaire Resps
      // If Qnaire is linked to the Responsible with current User, then return true
      for (TRvwQnaireRespVariant tRvwQnaireRespVar : rvwQnaireRespVersion.getTRvwQnaireResponse()
          .getTRvwQnaireRespVariants()) {
        if (validateQnaireAccess(tRvwQnaireRespVar.getTPidcVersion().getTabvProjectidcard().getProjectId(),
            tRvwQnaireRespVar.gettA2lResponsibility().getA2lRespId())) {
          return true;
        }
      }

    }
    return false;
  }


  /**
   * Find the WP IDs (via FC-WP mapping) of the given questionnaire response IDs
   *
   * @param qnaireRespIdSet set of questionnaire response IDs
   * @return list of WP ids of the responses
   */
  public List<Long> getWorkpackageId(final Set<Long> qnaireRespIdSet) {
    List<Long> wpId = new ArrayList<>();
    for (Long qnaireRespId : qnaireRespIdSet) {
      // qnaireRespId will be Null if there is Simplified Qnaire
      if (CommonUtils.isNotNull(qnaireRespId) &&
          CommonUtils.isNotEqual(qnaireRespId, ApicConstants.SIMP_QUES_RESP_ID)) {
        wpId.add(getWorkingSetQuestionnaireVersion(getEntityObject(qnaireRespId)).getTQuestionnaireVersion()
            .getTQuestionnaire().getTWorkpackageDivision().getTWorkpackage().getWpId());
      }
    }
    return wpId;
  }


  /**
   * Fetch the Set of available Wp and Resp Combination in Questionnaire Response for the given Pidc Version and Variant
   *
   * @param pidcVersId Pidc Version ID
   * @param variantId Variant ID
   * @return Set of RvwWpAndRespModel
   */
  public Set<RvwWpAndRespModel> getWPAndRespUsingPidcAndVariant(final Long pidcVersId, final Long variantId) {
    Set<RvwWpAndRespModel> rvwWpAndRespModels = new HashSet<>();

    Set<TRvwQnaireRespVariant> qnaireRespVariantList =
        new RvwQnaireRespVariantLoader(getServiceData()).getQnaireRespVariantList(pidcVersId);

    for (TRvwQnaireRespVariant tRvwQnaireRespVariant : qnaireRespVariantList) {
      Long rvwVarId = tRvwQnaireRespVariant.getTabvProjectVariant() != null
          ? tRvwQnaireRespVariant.getTabvProjectVariant().getVariantId() : null;
      if (CommonUtils.isEqual(rvwVarId, variantId)) {
        RvwWpAndRespModel rvwWpAndRespModel = new RvwWpAndRespModel();
        rvwWpAndRespModel.setA2lRespId(tRvwQnaireRespVariant.gettA2lResponsibility().getA2lRespId());
        rvwWpAndRespModel.setA2lWpId(tRvwQnaireRespVariant.gettA2lWorkPackage().getA2lWpId());
        rvwWpAndRespModels.add(rvwWpAndRespModel);
      }

    }
    return rvwWpAndRespModels;
  }


  /**
   * Fetch all qnaire responses, with resp variants linked to the variant ID
   *
   * @param pidcVersId PIDC Version ID
   * @param variantId Variant ID
   * @return set of responses
   * @throws DataException data retrieval error
   */
  public Set<RvwQnaireResponse> getRvwQnaireResponse(final Long pidcVersId, final Long variantId) throws DataException {
    Set<RvwQnaireResponse> qnaireResponseSet = new HashSet<>();

    Set<TRvwQnaireRespVariant> qnaireRespVariantList =
        new RvwQnaireRespVariantLoader(getServiceData()).getQnaireRespVariantList(pidcVersId);

    for (TRvwQnaireRespVariant tRvwQnaireRespVariant : qnaireRespVariantList) {
      Long rvwVarId = tRvwQnaireRespVariant.getTabvProjectVariant() != null
          ? tRvwQnaireRespVariant.getTabvProjectVariant().getVariantId() : null;

      // tRvwQnaireResponse is null for Simplified Qnaire - Empty WP/resp Structure
      TRvwQnaireResponse tRvwQnaireResponse = tRvwQnaireRespVariant.getTRvwQnaireResponse();
      if (CommonUtils.isNotNull(tRvwQnaireResponse) && CommonUtils.isEqual(rvwVarId, variantId)) {
        qnaireResponseSet.add(createDataObject(tRvwQnaireResponse));
      }
    }
    return qnaireResponseSet;
  }

  /**
   * Get the already existing primary and linked qnaire response variants under a pidc variant to which the result is
   * linked
   *
   * @param rvwVariant newly created review variant
   * @return Map of qnaire id and primary/linked qnaire response
   * @throws DataException Exception
   */
  public RvwQnaireResponseLink getQnaireInTargetWpResp(final RvwVariant rvwVariant) throws DataException {

    Set<TRvwQnaireRespVariant> tRvwQnaireRespVariants =
        new PidcVariantLoader(getServiceData()).getEntityObject(rvwVariant.getVariantId()).getTRvwQnaireRespVariants();

    RvwQnaireResponseLink qnaireRespLink = new RvwQnaireResponseLink();

    for (TRvwQnaireRespVariant tQnaireRespVar : tRvwQnaireRespVariants) {
      RvwQnaireRespVariant qnaireRespVarObj =
          new RvwQnaireRespVariantLoader(getServiceData()).getDataObjectByID(tQnaireRespVar.getQnaireRespVarId());

      TRvwQnaireResponse tRvwQnaireResponse = tQnaireRespVar.getTRvwQnaireResponse();
      // tRvwQnaireResp is null for simplified Qnaire
      if (CommonUtils.isNotNull(tRvwQnaireResponse)) {

        Long qnaireRespId = qnaireRespVarObj.getQnaireRespId();
        // qnaireRespId is Null for Simplified Qnaire
        if (CommonUtils.isNotNull(qnaireRespId)) {
          RvwQnaireResponse qnaireRespObj =
              new RvwQnaireResponseLoader(getServiceData()).getDataObjectByID(qnaireRespId);

          long a2lWpId = tQnaireRespVar.gettA2lWorkPackage().getA2lWpId();
          long a2lRespId = tQnaireRespVar.gettA2lResponsibility().getA2lRespId();
          Long qnaireId = tRvwQnaireResponse.getTRvwQnaireRespVersions().iterator().next().getTQuestionnaireVersion()
              .getTQuestionnaire().getQnaireId();

          if (!CommonUtils.isEqual(qnaireRespObj.getVariantId(), rvwVariant.getVariantId()) &&
              (CommonUtils.isNotEmpty(qnaireRespObj.getSecondaryQnaireLinkMap())) && isSameCopiedQnaireLinkedInDest(
                  qnaireRespObj.getSecondaryQnaireLinkMap(), rvwVariant.getVariantId(), a2lRespId, a2lWpId)) {
            fillQnaireLink(qnaireRespLink, qnaireId, a2lWpId, a2lRespId, false);
          }
          else if (CommonUtils.isEqual(qnaireRespObj.getVariantId(), rvwVariant.getVariantId())) {
            fillQnaireLink(qnaireRespLink, qnaireId, a2lWpId, a2lRespId, true);
          }
        }
      }
    }

    return qnaireRespLink;
  }

  /**
   * @param qnaireRespLink
   * @param qnaireId
   * @param a2lWpId
   * @param a2lRespId
   * @param isPrimary
   */
  private void fillQnaireLink(final RvwQnaireResponseLink qnaireRespLink, final Long qnaireId, final long a2lWpId,
      final long a2lRespId, final boolean isPrimary) {

    if (isPrimary) {
      fillQnaireLinkMap(qnaireRespLink.getPrimaryQnaireWpRespLinkMap(), qnaireId, a2lWpId, a2lRespId);
    }
    else {
      fillQnaireLinkMap(qnaireRespLink.getSecondaryQnaireWpRespLinkMap(), qnaireId, a2lWpId, a2lRespId);
    }
  }

  /**
   * @param qnaireLinkMap
   * @param qnaireId
   * @param a2lWpId
   * @param a2lRespId
   */
  private void fillQnaireLinkMap(final Map<Long, Map<Long, Set<Long>>> qnaireLinkMap, final Long qnaireId,
      final long a2lWpId, final long a2lRespId) {
    if (qnaireLinkMap.containsKey(a2lWpId)) {
      if (qnaireLinkMap.get(a2lWpId).containsKey(a2lRespId)) {
        qnaireLinkMap.get(a2lWpId).get(a2lRespId).add(qnaireId);
      }
      else {
        Set<Long> qnaireLink = new HashSet<>();
        qnaireLink.add(qnaireId);
        qnaireLinkMap.get(a2lWpId).put(a2lRespId, qnaireLink);
      }
    }
    else {
      Set<Long> qnaireLink = new HashSet<>();
      qnaireLink.add(qnaireId);

      Map<Long, Set<Long>> a2lRespQnaireLink = new HashMap<>();
      a2lRespQnaireLink.put(a2lRespId, qnaireLink);

      qnaireLinkMap.put(a2lWpId, a2lRespQnaireLink);
    }
  }

  /**
   * @param rvwVariant newly create review variant
   * @return Set of rvw qnaire response variants in source pidc variant
   * @throws DataException Exception
   */
  public Set<RvwQnaireRespVariant> getQnaireRespVarInSrcWpResp(final RvwVariant rvwVariant) throws DataException {

    Map<String, Set<RvwResultWPandRespModel>> a2lWpRespMap = getA2lWpRespForReviewResult(rvwVariant.getResultId());

    Long varId =
        new CDRReviewResultLoader(getServiceData()).getDataObjectByID(rvwVariant.getResultId()).getPrimaryVariantId();

    Set<TRvwQnaireRespVariant> tRvwQnaireRespVariants =
        new PidcVariantLoader(getServiceData()).getEntityObject(varId).getTRvwQnaireRespVariants();

    Set<RvwQnaireRespVariant> qnaireRespVarToBeLinked = new HashSet<>();
    if (CommonUtils.isNotEmpty(tRvwQnaireRespVariants)) {
      collectQnaireRespToBeLinked(a2lWpRespMap, tRvwQnaireRespVariants, qnaireRespVarToBeLinked);
    }

    return qnaireRespVarToBeLinked;
  }


  /**
   * @param a2lWpRespMap
   * @param tRvwQnaireRespVariants
   * @param qnaireRespVarToBeLinked
   * @throws DataException
   */
  private void collectQnaireRespToBeLinked(final Map<String, Set<RvwResultWPandRespModel>> a2lWpRespMap,
      final Set<TRvwQnaireRespVariant> tRvwQnaireRespVariants, final Set<RvwQnaireRespVariant> qnaireRespVarToBeLinked)
      throws DataException {
    // iterate through all ques resp variants
    for (TRvwQnaireRespVariant tRvwQnaireRespVariant : tRvwQnaireRespVariants) {
      // get Responsisbility name and workpackage id from ques response
      long a2lRespId = tRvwQnaireRespVariant.gettA2lResponsibility().getA2lRespId();
      String respName = new A2lResponsibilityLoader(getServiceData()).getDataObjectByID(a2lRespId).getName();
      // get RvwResultWPandRespModel set for given resp name from review result
      Set<RvwResultWPandRespModel> wpRespset = a2lWpRespMap.get(respName);
      // check if the model has resp and wp combinations
      checkAndAddQuesRespVar(tRvwQnaireRespVariant, tRvwQnaireRespVariant.gettA2lWorkPackage(), wpRespset,
          qnaireRespVarToBeLinked);
    }
  }

  /**
   * @param tRvwQnaireRespVariant
   * @param gettA2lWorkPackage
   * @param wpRespset
   * @param qnaireRespVarToBeLinked
   * @throws DataException
   */
  private void checkAndAddQuesRespVar(final TRvwQnaireRespVariant tRvwQnaireRespVariant,
      final TA2lWorkPackage ta2lWorkPackage, final Set<RvwResultWPandRespModel> wpRespset,
      final Set<RvwQnaireRespVariant> qnaireRespVarToBeLinked)
      throws DataException {
    // RvwQnaireResp will be null for simplified Qnaire
    if ((null != wpRespset) && CommonUtils.isNotNull(tRvwQnaireRespVariant.getTRvwQnaireResponse()) &&
        ApicConstants.CODE_NO.equals(tRvwQnaireRespVariant.getTRvwQnaireResponse().getDeletedFlag())) {
      for (RvwResultWPandRespModel wpRespModel : wpRespset) {
        if (ta2lWorkPackage.getA2lWpId() == wpRespModel.getA2lWorkPackage().getId().longValue()) {
          qnaireRespVarToBeLinked.add(new RvwQnaireRespVariantLoader(getServiceData())
              .getDataObjectByID(tRvwQnaireRespVariant.getQnaireRespVarId()));
          break;
        }
      }
    }
  }

  /**
   * @param resultId
   * @return
   * @throws DataException
   */
  private Map<String, Set<RvwResultWPandRespModel>> getA2lWpRespForReviewResult(final Long resultId)
      throws DataException {
    Map<Long, RvwWpResp> rvwWpRespMap = new RvwWpRespLoader(getServiceData())
        .getByResultObj(new CDRReviewResultLoader(getServiceData()).getEntityObject(resultId));

    // key - resp name, value - set of RvwResultWPandRespModel
    Map<String, Set<RvwResultWPandRespModel>> a2lWpRespMap = new HashMap<>();
    if (!rvwWpRespMap.isEmpty()) {
      CDRRvwResultEditorDataLoader cdrRvwResultEditorDataLoader = new CDRRvwResultEditorDataLoader(getServiceData());
      for (RvwWpResp rvwWpResp : rvwWpRespMap.values()) {
        A2lResponsibility a2lResp =
            new A2lResponsibilityLoader(getServiceData()).getDataObjectByID(rvwWpResp.getA2lRespId());
        A2lWorkPackage a2lWp = new A2lWorkPackageLoader(getServiceData()).getDataObjectByID(rvwWpResp.getA2lWpId());
        // Object for Workpackage level nodes to avoid repitation of wp with same name
        RvwResultWPandRespModel objForWorkpackageNodes = new RvwResultWPandRespModel();
        objForWorkpackageNodes.setA2lWorkPackage(a2lWp);
        // Object for Responsible level nodes
        RvwResultWPandRespModel objForResponsibleNodes = new RvwResultWPandRespModel();
        objForResponsibleNodes.setA2lWorkPackage(a2lWp);
        objForResponsibleNodes.setA2lResponsibility(a2lResp);
        cdrRvwResultEditorDataLoader.fillWpRespMap(a2lWpRespMap, objForResponsibleNodes, a2lResp);
      }
    }
    return a2lWpRespMap;
  }


  /**
   * @param a2lRespIdSet a2lRespSet selected a2l responsibility set
   * @return Rvw qnaire response details of selected responsbility
   * @throws DataException exception in ws call
   */
  public List<QnaireRespDetails> getQnaireRespDetailsList(final List<Long> a2lRespIdSet) throws DataException {
    List<QnaireRespDetails> qnaireRespdetailsList = new ArrayList<>();
    A2lResponsibilityLoader a2lResponsibilityLoader = new A2lResponsibilityLoader(getServiceData());
    A2lWorkPackageLoader a2lWorkPackageLoader = new A2lWorkPackageLoader(getServiceData());
    PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());

    List<TRvwQnaireRespVariant> tRvwQnaireRespVariantList =
        new RvwQnaireRespVariantLoader(getServiceData()).getQnaireRespVariantListForA2lRespIds(a2lRespIdSet);
    if (CommonUtils.isNotEmpty(tRvwQnaireRespVariantList)) {
      for (TRvwQnaireRespVariant tRvwQnaireRespVariant : tRvwQnaireRespVariantList) {

        PidcVariant pidcVariant = createDummyVarObj();
        if (null != tRvwQnaireRespVariant.getTabvProjectVariant()) {
          pidcVariant = new PidcVariantLoader(getServiceData())
              .getDataObjectByID(tRvwQnaireRespVariant.getTabvProjectVariant().getVariantId());
        }

        // Fill Qnaire resp Details
        QnaireRespDetails qnaireRespDetails = new QnaireRespDetails();
        TRvwQnaireResponse tRvwQnaireResponse = tRvwQnaireRespVariant.getTRvwQnaireResponse();

        // tRvwQnaireResponse is Null for Simplified Qnaire
        if (CommonUtils.isNotNull(tRvwQnaireResponse)) {
          qnaireRespDetails.setRvwQnaireResponse(createDataObject(tRvwQnaireResponse));
          qnaireRespDetails.setA2lResponsibility(
              a2lResponsibilityLoader.createDataObject(tRvwQnaireRespVariant.gettA2lResponsibility()));
          qnaireRespDetails
              .setA2lWorkPackage(a2lWorkPackageLoader.createDataObject(tRvwQnaireRespVariant.gettA2lWorkPackage()));
          qnaireRespDetails.setPidcVariant(pidcVariant);
          qnaireRespDetails.setPidcVersion(
              pidcVersionLoader.getDataObjectByID(tRvwQnaireRespVariant.getTPidcVersion().getPidcVersId()));

          qnaireRespdetailsList.add(qnaireRespDetails);
        }
      }
    }
    return qnaireRespdetailsList;
  }

  /**
   * @return
   */
  private PidcVariant createDummyVarObj() {
    PidcVariant dummyVar = new PidcVariant();
    dummyVar.setId(-1000L);
    dummyVar.setName(ApicConstants.DUMMY_VAR_NODE_NOVAR);
    return dummyVar;
  }

  /**
   * @param wpId WP ID
   * @param respId Resp ID
   * @param pidVarId variant ID
   * @return true, if Gen questionnaire not required
   * @throws DataException data retrieval error
   */
  public boolean isGenQuesNotRequired(final Long wpId, final Long respId, final Long pidVarId) throws DataException {
    boolean isGenQuesNotReq = false;
    A2lWorkPackageLoader wpLoader = new A2lWorkPackageLoader(getServiceData());
    Long pidcVersId = wpLoader.getDataObjectByID(wpId).getPidcVersId();
    PidcQnaireInfo qnaireInfo = getPidcQnaireResponse(pidcVersId);

    Set<Long> respIdSet = qnaireInfo.getVarRespWpQniareMap().get(pidVarId).get(respId).get(wpId);
    // respIdSet will have 'null' value due to simplified General Qnaireso remove it before checking match
    respIdSet.removeIf(Objects::isNull);

    isGenQuesNotReq =
        respIdSet.stream().filter(qnaireRespId -> CommonUtils.isNotEqual(ApicConstants.SIMP_QUES_RESP_ID, qnaireRespId))
            .anyMatch(qnaireRespId -> ApicConstants.CODE_YES
                .equals(getWorkingSetQuestionnaireVersion(getEntityObject(qnaireRespId)).getTQuestionnaireVersion()
                    .getGenQuesEquivalentFlag()) &&
                ApicConstants.CODE_NO.equals(getEntityObject(qnaireRespId).getDeletedFlag()));
    return isGenQuesNotReq;
  }

  /**
   * @param respId Questionnaire Resp ID
   * @return true, if questionnaire version update is required
   * @throws DataException DataException
   */
  public boolean isQnaireVersionUpdateRequired(final Long respId) throws DataException {

    RvwQnaireRespVersionLoader rvwQnaireRespVersionLoader = new RvwQnaireRespVersionLoader(getServiceData());
    Optional<RvwQnaireRespVersion> workingSetOptionalQuesRespVersion =
        rvwQnaireRespVersionLoader.getQnaireRespVersionsByRespId(respId).values().stream()
            .filter(rvwQuesRespVersion -> CommonUtils.isEqual(0L, rvwQuesRespVersion.getRevNum())).findFirst();

    RvwQnaireRespVersion workingSetRvwQnaireRespVersion = new RvwQnaireRespVersion();
    if (workingSetOptionalQuesRespVersion.isPresent()) {
      workingSetRvwQnaireRespVersion = workingSetOptionalQuesRespVersion.get();
    }

    Long questionnaireId = rvwQnaireRespVersionLoader.getEntityObject(workingSetRvwQnaireRespVersion.getId())
        .getTQuestionnaireVersion().getTQuestionnaire().getQnaireId();

    // Comparing Qnaire resp current version with Qnaire active version return true if both are not equal.
    return CommonUtils.isNotEqual(workingSetRvwQnaireRespVersion.getQnaireVersionId(),
        new QuestionnaireVersionLoader(getServiceData()).getActiveQnnaireVersion(questionnaireId).getId());
  }

  /**
   * @param qnaireRespId qnaireRespId
   * @return RvwQnaireResponse
   * @throws DataException DataException
   */
  public RvwQnaireResponse setUpdatedRvwQnaireResponse(final Long qnaireRespId) throws DataException {

    return createDataObject(getEntityObject(qnaireRespId));
  }

  /**
   * @param qnaireRespVersDataSet qnaireRespVersDataSet
   * @return Map<Long, QnaireRespVersionData>
   * @throws DataException DataException
   * @throws UnAuthorizedAccessException UnAuthorizedAccessException
   */
  public Map<Long, QnaireRespVersionData> getQnaireRespVersData(final Set<QnaireRespStatusData> qnaireRespVersDataSet)
      throws DataException, UnAuthorizedAccessException {

    Map<Long, QnaireRespVersionData> reponseMap = new HashMap<>();

    for (QnaireRespStatusData dataSet : qnaireRespVersDataSet) {

      QnaireRespVersionData qnaireRespVersionData = new QnaireRespVersionData();
      Long quesRespId = dataSet.getQuesRespId();
      TRvwQnaireResponse tRvwQnaireResponse = getEntityObject(quesRespId);
      qnaireRespVersionData.setQnaireRespName(getQnaireRespName(tRvwQnaireResponse));
      RvwQnaireRespVersion rvwQnaireRespVersion = new RvwQnaireRespVersionLoader(getServiceData())
          .createDataObject(getLatestQnaireRespVersUsingRevNum(tRvwQnaireResponse.getTRvwQnaireRespVersions(), true));

      qnaireRespVersionData.setVersionName(rvwQnaireRespVersion.getVersionName());
      qnaireRespVersionData.setRevisionNum(rvwQnaireRespVersion.getRevNum());
      reponseMap.put(quesRespId, qnaireRespVersionData);
    }
    return reponseMap;
  }
}

