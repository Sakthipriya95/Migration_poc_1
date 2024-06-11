/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2lResponsibilityLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWorkPackageLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.QuestionLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.QuestionnaireVersionLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireResponseLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireRespVariant;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireRespVersion;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireResponse;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwVariant;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetails;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.CombinedReviewResultExcelExportData;
import com.bosch.caltool.icdm.model.cdr.CombinedRvwExportInputModel;
import com.bosch.caltool.icdm.model.cdr.QnaireResponseCombinedModel;
import com.bosch.caltool.icdm.model.cdr.ReviewResultEditorData;
import com.bosch.caltool.icdm.model.cdr.RvwResultWPandRespModel;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;
import com.bosch.caltool.icdm.model.cdr.RvwWpResp;

/**
 * @author say8cob
 */
public class CombinedReportExportHandler extends AbstractSimpleBusinessObject {

  private final CombinedRvwExportInputModel exportInputModel;

  /**
   * @param serviceData
   * @param exportInputModel as input
   */
  public CombinedReportExportHandler(final ServiceData serviceData,
      final CombinedRvwExportInputModel exportInputModel) {
    super(serviceData);
    this.exportInputModel = exportInputModel;
  }

  /**
   * @param exportInputModel as CombinedRvwExportInputModel
   * @return CombinedReviewResultExcelExportData
   * @throws IcdmException as exception
   */
  public CombinedReviewResultExcelExportData getCombinedEditorDataForExport() throws IcdmException {
    // to get the review variant data for the review result
    CDRReviewResult reviewResult =
        new CDRReviewResultLoader(getServiceData()).getDataObjectByID(this.exportInputModel.getRvwResultId());
    // added null for no-variant case
    Long rvwVarId = null;
    // identifying review variant for variant case
    if (CommonUtils.isNotNull(reviewResult.getPrimaryVariantId())) {
      RvwVariant rvwVariant = new RvwVariantLoader(getServiceData()).getRvwVariantByResultNVarId(reviewResult.getId(),
          reviewResult.getPrimaryVariantId());
      rvwVarId = rvwVariant.getId();
    }
    CombinedReviewResultExcelExportData exportData = new CombinedReviewResultExcelExportData();
    // to load editor data based on the flag
    if (this.exportInputModel.isLoadEditorData()) {
      ReviewResultEditorData reviewResultEditorData = new CDRRvwResultEditorDataLoader(getServiceData())
          .getReviewResultEditorData(this.exportInputModel.getRvwResultId(), rvwVarId);
      exportData.setReviewResultEditorData(reviewResultEditorData);
    }
    // to load questionnaire response data model for export based on the flag
    if (this.exportInputModel.isOnlyRvwResAndQnaireLstBaseLine() ||
        this.exportInputModel.isOnlyRvwResAndQnaireWrkSet()) {
      TRvwResult tRvwResult =
          new CDRReviewResultLoader(getServiceData()).getEntityObject(this.exportInputModel.getRvwResultId());

      Map<Long, RvwWpResp> rvwWpRespMap = new RvwWpRespLoader(getServiceData()).getByResultObj(tRvwResult);

      // key - resp name, value - set of RvwResultWPandRespModel
      Map<String, Set<RvwResultWPandRespModel>> a2lRespToRvwWpRespMap = new HashMap<>();
      if (!rvwWpRespMap.isEmpty()) {
        Set<RvwResultWPandRespModel> a2lWpSet = new HashSet<>();
        // adding RvwWpResp to Set
        exportData.setRvwWpRespSet(new HashSet<>(rvwWpRespMap.values()));
        for (RvwWpResp rvwWpResp : rvwWpRespMap.values()) {
          A2lResponsibility a2lResp =
              new A2lResponsibilityLoader(getServiceData()).getDataObjectByID(rvwWpResp.getA2lRespId());
          A2lWorkPackage a2lWp = new A2lWorkPackageLoader(getServiceData()).getDataObjectByID(rvwWpResp.getA2lWpId());
          // Object for Workpackage level nodes to avoid repitation of wp with same name
          RvwResultWPandRespModel objForWorkpackageNodes = new RvwResultWPandRespModel();
          objForWorkpackageNodes.setA2lWorkPackage(a2lWp);
          a2lWpSet.add(objForWorkpackageNodes);
          // Object for Responsible level nodes
          RvwResultWPandRespModel objForResponsibleNodes = new RvwResultWPandRespModel();
          objForResponsibleNodes.setA2lWorkPackage(a2lWp);
          objForResponsibleNodes.setA2lResponsibility(a2lResp);

          fillWpRespMap(a2lRespToRvwWpRespMap, objForResponsibleNodes, a2lResp);
        }
      }

      PidcVersionWithDetails pidcVersionWithDetails = new PidcVersionLoader(getServiceData())
          .getPidcVersionWithDetails(tRvwResult.getTPidcA2l().getTPidcVersion().getPidcVersId());

      exportData.setPidcVersionWithDetails(pidcVersionWithDetails);

      Map<Long, Set<QnaireResponseCombinedModel>> qnaireRespCombinedModelMap =
          getQnaireRespCombinedModelMap(tRvwResult, a2lRespToRvwWpRespMap, rvwVarId);

      exportData.setQnaireRespCombinedModelMap(qnaireRespCombinedModelMap);

    }

    return exportData;
  }

  /**
   * @param a2lWpRespMap
   * @param resultWPandRespModel
   * @param a2lWpRespPal
   * @param a2lResp
   */
  private void fillWpRespMap(final Map<String, Set<RvwResultWPandRespModel>> a2lWpRespMap,
      final RvwResultWPandRespModel resultWPandRespModel, final A2lResponsibility a2lResp) {
    String respName = a2lResp.getName();
    Set<RvwResultWPandRespModel> a2lWpRespSet = a2lWpRespMap.get(respName);
    if (a2lWpRespSet == null) {
      a2lWpRespSet = new HashSet<>();
    }
    a2lWpRespSet.add(resultWPandRespModel);
    a2lWpRespMap.put(respName, a2lWpRespSet);
  }


  /**
   * @param entityObject Result Id
   * @param a2lWpMap
   * @param rvwVarId
   * @throws DataException
   */
  private Map<Long, Set<QnaireResponseCombinedModel>> getQnaireRespCombinedModelMap(final TRvwResult tRvwResult,
      final Map<String, Set<RvwResultWPandRespModel>> a2lWpMap, final Long rvwVarId)
      throws IcdmException {

    Map<Long, Set<QnaireResponseCombinedModel>> qnaireRespCombinedModelMap = new HashMap<>();
    if (CommonUtils.isNotEmpty(tRvwResult.getTRvwVariants()) && (null != rvwVarId)) {
      // review results with variant
      for (TRvwVariant dbRvwVariant : tRvwResult.getTRvwVariants()) {
        // select only the relevant review variant id
        if (dbRvwVariant.getRvwVarId() == rvwVarId.longValue()) {
          // get all qnaire resp varinats attached to project variant
          Set<TRvwQnaireRespVariant> tRvwQnaireRespVariants =
              dbRvwVariant.getTabvProjectVariant().getTRvwQnaireRespVariants();
          getQnaireVersFromQRespVariants(a2lWpMap, qnaireRespCombinedModelMap, tRvwQnaireRespVariants, false,
              tRvwResult);
        }
      }
    }
    else {
      // review result with <NO-VARIANT>
      Set<TRvwQnaireRespVariant> tRvwQnaireRespVariants =
          tRvwResult.getTPidcA2l().getTPidcVersion().getTRvwQnaireRespVariants();
      getQnaireVersFromQRespVariants(a2lWpMap, qnaireRespCombinedModelMap, tRvwQnaireRespVariants, true, tRvwResult);
    }
    return qnaireRespCombinedModelMap;

  }

  /**
   * @param a2lWpMap
   * @param qnaireRespCombinedModelMap
   * @param tRvwQnaireRespVariants
   * @param noVarReview
   * @throws IcdmException
   */
  private void getQnaireVersFromQRespVariants(final Map<String, Set<RvwResultWPandRespModel>> a2lWpMap,
      final Map<Long, Set<QnaireResponseCombinedModel>> qnaireRespCombinedModelMap,
      final Set<TRvwQnaireRespVariant> tRvwQnaireRespVariants, final boolean noVarReview, final TRvwResult tRvwResult)
      throws IcdmException {
    // iterate through all ques resp variants
    for (TRvwQnaireRespVariant tRvwQnaireRespVariant : tRvwQnaireRespVariants) {
      if (!noVarReview || (tRvwQnaireRespVariant.getTabvProjectVariant() == null)) {
        // get Responsisbility name and workpackage id from ques response
        long a2lRespId = tRvwQnaireRespVariant.gettA2lResponsibility().getA2lRespId();
        String respName = new A2lResponsibilityLoader(getServiceData()).getDataObjectByID(a2lRespId).getName();
        // get RvwResultWPandRespModel set for given resp name from review result
        Set<RvwResultWPandRespModel> wpRespset = a2lWpMap.get(respName);
        // check if the model has resp and wp combinations
        checkAndAddQuesRespVersion(qnaireRespCombinedModelMap, tRvwQnaireRespVariant, wpRespset, tRvwResult);
      }
    }
  }

  /**
   * @param qnaireRespVersSet
   * @param tRvwQnaireResponse
   * @param ta2lWorkPackage
   * @param wpRespset
   * @throws IcdmException
   */
  private void checkAndAddQuesRespVersion(final Map<Long, Set<QnaireResponseCombinedModel>> qnaireRespCombinedModelMap,
      final TRvwQnaireRespVariant tRvwQnaireRespVariant, final Set<RvwResultWPandRespModel> wpRespset,
      final TRvwResult tRvwResult)
      throws IcdmException {
    RvwQnaireResponseLoader rvwQnaireRespLoader = new RvwQnaireResponseLoader(getServiceData());
    TRvwQnaireResponse tRvwQnaireResponse = tRvwQnaireRespVariant.getTRvwQnaireResponse();
    // RvwQnaireResp will be null for simplified Qnaire
    if ((null != wpRespset) && CommonUtils.isNotNull(tRvwQnaireResponse) &&
        ApicConstants.CODE_NO.equals(tRvwQnaireResponse.getDeletedFlag())) {
      for (RvwResultWPandRespModel wpRespModel : wpRespset) {
        if (tRvwQnaireRespVariant.gettA2lWorkPackage().getA2lWpId() == wpRespModel.getA2lWorkPackage().getId()
            .longValue()) {
          fillQnaireRespCombinedModelMap(qnaireRespCombinedModelMap, tRvwQnaireRespVariant, tRvwQnaireResponse,
              tRvwResult, rvwQnaireRespLoader);
          break;
        }
      }
    }
  }

  /**
   * @param qnaireRespCombinedModelMap
   * @param tRvwQnaireResponse
   * @param tRvwResult
   * @param rvwQnaireRespLoader
   * @param qnaireResponseCombinedModel
   * @throws DataException
   * @throws IcdmException
   */
  private void fillQnaireRespCombinedModelMap(
      final Map<Long, Set<QnaireResponseCombinedModel>> qnaireRespCombinedModelMap,
      final TRvwQnaireRespVariant tRvwQnaireRespVariant, final TRvwQnaireResponse tRvwQnaireResponse,
      final TRvwResult tRvwResult, final RvwQnaireResponseLoader rvwQnaireRespLoader)
      throws IcdmException {
    QnaireResponseCombinedModel qnaireResponseCombinedModel = new QnaireResponseCombinedModel();

    TRvwQnaireRespVersion tRvwQnaireRespVersion;
    // getting resp version based on boolean flag
    if (this.exportInputModel.isOnlyRvwResAndQnaireLstBaseLine()) {
      tRvwQnaireRespVersion =
          rvwQnaireRespLoader.getLatestQnaireRespVersUsingRevNum(tRvwQnaireResponse.getTRvwQnaireRespVersions(), false);
      if (CommonUtils.isNull(tRvwQnaireRespVersion)) {
        tRvwQnaireRespVersion = rvwQnaireRespLoader.getWorkingSetQuestionnaireVersion(tRvwQnaireResponse);
      }
    }
    else {
      tRvwQnaireRespVersion = rvwQnaireRespLoader.getWorkingSetQuestionnaireVersion(tRvwQnaireResponse);
    }

    // fetch the RvwQnaireResponseModel with details
    qnaireResponseCombinedModel.setRvwQnaireResponseModel(new RvwQnaireResponseLoader(getServiceData())
        .getQnaireResponseModel(tRvwQnaireRespVersion.getQnaireRespVersId()));
    // fetch the questionnaire version with details
    qnaireResponseCombinedModel.setQnaireDefModel(new QuestionnaireVersionLoader(getServiceData())
        .getQnaireVersionWithDetails(tRvwQnaireRespVersion.getTQuestionnaireVersion().getQnaireVersId()));
    // fetch the AllQnAttrValDepModel with details
    qnaireResponseCombinedModel.setAllQnAttrValDepModel(new QuestionLoader(getServiceData())
        .getAllQnDepnAttrValModel(tRvwQnaireRespVersion.getTQuestionnaireVersion().getQnaireVersId()));

    long a2lWpId = tRvwQnaireRespVariant.gettA2lWorkPackage().getA2lWpId();
    long a2lRespId = tRvwQnaireRespVariant.gettA2lResponsibility().getA2lRespId();
    RvwWpResp rvwWpResp =
        new RvwWpRespLoader(getServiceData()).getMatchingRvwWpResp(a2lWpId, a2lRespId, tRvwResult.getResultId());
    qnaireResponseCombinedModel.setA2lResponsibility(
        new A2lResponsibilityLoader(getServiceData()).createDataObject(tRvwQnaireRespVariant.gettA2lResponsibility()));
    qnaireResponseCombinedModel.setA2lWorkPackage(
        new A2lWorkPackageLoader(getServiceData()).createDataObject(tRvwQnaireRespVariant.gettA2lWorkPackage()));

    qnaireRespCombinedModelMap.computeIfAbsent(rvwWpResp.getId(), newSet -> new HashSet<QnaireResponseCombinedModel>())
        .add(qnaireResponseCombinedModel);

  }
}
