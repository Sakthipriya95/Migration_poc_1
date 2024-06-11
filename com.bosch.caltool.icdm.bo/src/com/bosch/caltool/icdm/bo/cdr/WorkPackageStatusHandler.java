/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2lResponsibilityLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lVariantGroupLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWorkPackageLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWpDefnVersionLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWpParamMappingLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWpResponsibilityLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.QuestionnaireVersionLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireResponseLoader;
import com.bosch.caltool.icdm.common.bo.a2l.ParamWpRespResolver;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lVarGrpVariantMapping;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpDefnVersion;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpParamMapping;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResponsibility;
import com.bosch.caltool.icdm.database.entity.apic.TPidcA2l;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParameter;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwWpResp;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LDetailsStructureModel;
import com.bosch.caltool.icdm.model.a2l.A2lVarGrpVariantMapping;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWPRespModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.WpRespLabelResponse;
import com.bosch.caltool.icdm.model.a2l.WpRespModel;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CdrReportQnaireRespWrapper;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.model.cdr.TreeViewSelectnRespWP;
import com.bosch.caltool.icdm.model.cdr.WPFinishRvwDet;
import com.bosch.caltool.icdm.model.cdr.WPRespStatusMsgWrapper;
import com.bosch.caltool.icdm.model.cdr.WPRespStatusOutputInternalModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireVersionModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;

/**
 * @author say8cob
 */
public class WorkPackageStatusHandler extends AbstractSimpleBusinessObject {


  /**
   * @param serviceData service data
   */
  public WorkPackageStatusHandler(final ServiceData serviceData) {
    super(serviceData);
  }


  /**
   * method check the workpackage resp status based on questionnaire response and review parameters
   *
   * @param tRvwResult as input
   * @return WPRespStatusOutputModel
   * @throws IcdmException as exception
   */
  public WPRespStatusOutputInternalModel calculateWorkpackageRespStatus(final TRvwResult tRvwResult)
      throws IcdmException {

    // Map Holds the Status of all the WP Resp combination
    Map<A2lWPRespModel, String> wpRespStatusMap = new HashMap<>();

    // Map to hold uncompleted/ unfinished wp resp map
    Map<A2lWPRespModel, WPRespStatusMsgWrapper> inCompleteWPRespMap = new HashMap<>();

    // Map to hold completed/ finished wp resp map
    Map<A2lWPRespModel, WPRespStatusMsgWrapper> completedWPRespMap = new HashMap<>();

    RvwQnaireResponseLoader qnaireResponseLoader = new RvwQnaireResponseLoader(getServiceData());

    A2lWpResponsibilityLoader a2lWpResponsibilityLoader = new A2lWpResponsibilityLoader(getServiceData());

    TPidcA2l tPidcA2l = tRvwResult.getTPidcA2l();
    long pidcVersId = tPidcA2l.getTPidcVersion().getPidcVersId();

    CDRReviewResultLoader cdrReviewResultLoader = new CDRReviewResultLoader(getServiceData());
    PidcVariant variant = cdrReviewResultLoader.getVariant(tRvwResult);

    CdrReportQnaireRespWrapper reportQnaireRespWrapper = qnaireResponseLoader
        .getQniareRespVersByPidcVersIdAndVarId(pidcVersId, variant != null ? variant.getId() : null, false);

    // review a2lwpdefversion
    TA2lWpDefnVersion rvwA2lWpDefnVersion = tRvwResult.gettA2lWpDefnVersion();
    // active a2lwpdefversion
    TA2lWpDefnVersion activeA2lWpDefnVersion =
        new A2lWpDefnVersionLoader(getServiceData()).getActiveA2lWPDefnVersionEntityFromA2l(tPidcA2l.getPidcA2lId());
    // contains list of WPResp that is not available/ faled in validation if there is
    // A2lWpDefVersion mismatch between the Review result and active a2lWPDefVersion
    Set<TA2lWpResponsibility> notFinishedWpRespSet = new HashSet<>();
    // validWpRespSet that matched with the active WPDefVersion
    Set<TA2lWpResponsibility> validWpRespSet = new HashSet<>();

    PidcVariant reviewPIDCVariant = cdrReviewResultLoader.getReviewVariant(tRvwResult);
    // method to validate the WpResp that is matching with active WpDefVers
    validWpRespSet.addAll(validateA2lWpRespForActiveWPDefVers(rvwA2lWpDefnVersion, activeA2lWpDefnVersion,
        notFinishedWpRespSet, inCompleteWPRespMap, reviewPIDCVariant));
    // for no-variant and variant case
    Long pidcVarId = CommonUtils.isNotNull(reviewPIDCVariant) ? reviewPIDCVariant.getId() : null;
    // fetch the matching variant group for the pidcVaraint
    A2lVariantGroup a2lVarGrp = fetchVariantGroup(activeA2lWpDefnVersion, pidcVarId);

    // fetching a2lwpRespModel based on validated wp resp
    Set<A2lWPRespModel> wpRespLabelResponse = getWpRespLabelResponse(a2lVarGrp, validWpRespSet);

    long pidcA2lId = tRvwResult.getTPidcA2l().getPidcA2lId();

    List<WpRespLabelResponse> wpRespLabResponse = a2lWpResponsibilityLoader.getWpResp(pidcA2lId, pidcVarId);

    Map<WpRespModel, List<Long>> resolveWpRespLabels = resolveWpRespLabels(wpRespLabResponse);

    // iterating all the rvw wp resp in the review result
    for (TRvwWpResp tRvwWpResp : tRvwResult.getTRvwWpResps()) {
      // fetching a2lresp and a2lwp id
      Long a2lRespId = tRvwWpResp.gettA2lResponsibility().getA2lRespId();
      Long a2lWpId = tRvwWpResp.getTA2lWorkPackage().getA2lWpId();
      boolean isWPRespAvailable = false;
      // looping through the validWpRespSet
      for (A2lWPRespModel a2lWPRespModel : wpRespLabelResponse) {
        // To validate only the WP Resp that is available in the review
        if (CommonUtils.isEqual(a2lWpId, a2lWPRespModel.getA2lWpId()) &&
            CommonUtils.isEqual(a2lRespId, a2lWPRespModel.getA2lRespId()) &&
            checkMatchingVaraintGroup(a2lVarGrp, a2lWPRespModel)) {
          isWPRespAvailable = true;
          WpRespModel wpRespModelVal = getWpRespModel(resolveWpRespLabels, a2lRespId, a2lWpId);
          // method to validate the questionnaire response status if it is available in both normal and monica review
          validateRvwQnaireResponseStatus(wpRespStatusMap, inCompleteWPRespMap, completedWPRespMap,
              reportQnaireRespWrapper, a2lRespId, a2lWpId, a2lWPRespModel);
          // method to validate the parameter mismatch between the review parameter and a2l wp resp parameter mappings
          validateParamMatchBtwRvwParamAndA2lWpParam(wpRespStatusMap, inCompleteWPRespMap, completedWPRespMap,
              tRvwWpResp, a2lWPRespModel, wpRespModelVal);
          // methsod to validate the review parameter socre
          validateReviewParamScore(wpRespStatusMap, inCompleteWPRespMap, completedWPRespMap, tRvwWpResp,
              a2lWPRespModel);
        }
      }
      // this will happen if the WP RESP is not found in the active wp definition version
      if (!isWPRespAvailable) {
        // dummy a2l wp resp model
        A2lWPRespModel a2lWPRespModel = new A2lWPRespModel();
        a2lWPRespModel.setA2lRespId(a2lRespId);
        a2lWPRespModel.setA2lWpId(a2lWpId);

        fillWPRespMap(inCompleteWPRespMap, a2lWPRespModel, CDRConstants.RVW_WORKPACKAGE_RESP_MSG);
      }
    }
    fillingNotFinishedWPResp(wpRespStatusMap, notFinishedWpRespSet);

    return fillWPRESPStatusOutputModel(wpRespStatusMap, inCompleteWPRespMap, completedWPRespMap);
  }


  /**
   * @param activeA2lWpDefnVersion
   * @param pidcVarId
   * @return
   * @throws IcdmException
   * @throws DataException
   */
  public A2lVariantGroup fetchVariantGroup(final TA2lWpDefnVersion activeA2lWpDefnVersion, final Long pidcVarId)
      throws IcdmException {
    A2lVariantGroup a2lVarGrp = null;
    A2LDetailsStructureModel detailsModel =
        new A2lWpDefnVersionLoader(getServiceData()).getDetailsModel(activeA2lWpDefnVersion.getWpDefnVersId(), true);
    // for no-variant case in wprespstatus caluclation
    if (CommonUtils.isNotNull(pidcVarId)) {
      A2lVarGrpVariantMapping a2lVarGrpVariantMapping = detailsModel.getGroupMappingMap().get(pidcVarId);
      a2lVarGrp = fetchVarGrpData(a2lVarGrp, a2lVarGrpVariantMapping);
    }
    return a2lVarGrp;
  }


  /**
   * @param a2lVarGrp
   * @param activeVersion
   * @param projectId
   * @param pidcVersId
   * @param a2lRespModel
   * @return
   * @throws IcdmException
   */
  public Set<A2lWPRespModel> getWpRespLabelResponse(final A2lVariantGroup a2lVarGrp,
      final Set<TA2lWpResponsibility> validWpRespSet)
      throws IcdmException {

    Map<Long, A2lVariantGroup> a2lVarGrpMap = new HashMap<>();
    Map<Long, A2lWpParamMapping> a2lWParamInfoMap = new HashMap<>();
    Map<Long, A2lWpResponsibility> wpRespMap = new HashMap<>();

    for (TA2lWpResponsibility tA2lWPRespPal : validWpRespSet) {
      A2lWpResponsibility a2lWpResponsibility =
          new A2lWpResponsibilityLoader(getServiceData()).getDataObjectByID(tA2lWPRespPal.getWpRespId());
      wpRespMap.put(tA2lWPRespPal.getWpRespId(), a2lWpResponsibility);
      for (TA2lWpParamMapping tA2lParamMapping : tA2lWPRespPal.getTA2lWpParamMappings()) {
        a2lWParamInfoMap.put(tA2lParamMapping.getWpParamMappingId(),
            new A2lWpParamMappingLoader(getServiceData()).getDataObjectByID(tA2lParamMapping.getWpParamMappingId()));
      }
    }

    ParamWpRespResolver resolver = new ParamWpRespResolver(a2lVarGrpMap, wpRespMap, a2lWParamInfoMap);

    return resolver.getWPRespForVariant(a2lVarGrp != null ? a2lVarGrp.getId() : null);

  }


  /**
   * @param a2lVarGrp
   * @param a2lWpResponsibility
   * @return
   * @throws DataException
   */
  public boolean checkMatchingVaraintGroup(final A2lVariantGroup a2lVarGrp, final A2lWPRespModel a2lWPRespModel)
      throws DataException {
    A2lWpResponsibility a2lWpResponsibility =
        new A2lWpResponsibilityLoader(getServiceData()).getDataObjectByID(a2lWPRespModel.getWpRespId());
    return ((null == a2lVarGrp) && (null == a2lWpResponsibility.getVariantGrpId())) ||
        ((null != a2lVarGrp) && CommonUtils.isEqual(a2lWpResponsibility.getVariantGrpId(), a2lVarGrp.getId()));
  }

  /**
   * @param a2lVarGrp
   * @param a2lVarGrpVariantMapping
   * @return
   * @throws DataException
   */
  private A2lVariantGroup fetchVarGrpData(A2lVariantGroup a2lVarGrp,
      final A2lVarGrpVariantMapping a2lVarGrpVariantMapping)
      throws DataException {
    if (CommonUtils.isNotNull(a2lVarGrpVariantMapping)) {
      a2lVarGrp =
          new A2lVariantGroupLoader(getServiceData()).getDataObjectByID(a2lVarGrpVariantMapping.getA2lVarGroupId());
    }
    return a2lVarGrp;
  }


  /**
   * @param resolveWpRespLabels
   * @param a2lRespId
   * @param a2lWpId
   * @param wpRespModelVal
   * @return
   */
  public WpRespModel getWpRespModel(final Map<WpRespModel, List<Long>> resolveWpRespLabels, final Long a2lRespId,
      final Long a2lWpId) {
    WpRespModel wpRespModelVal = new WpRespModel();
    for (Entry<WpRespModel, List<Long>> wpRespLabelEntrySet : resolveWpRespLabels.entrySet()) {
      WpRespModel wpRespModel = wpRespLabelEntrySet.getKey();
      if (a2lWpId.equals(wpRespModel.getA2lWpId()) && a2lRespId.equals(wpRespModel.getA2lResponsibility().getId())) {
        wpRespModelVal = wpRespModel;
        break;
      }
    }
    return wpRespModelVal;
  }

  /**
   * This method to get Wp Resp Map based on wpRespLabResponse
   *
   * @param wpRespLabResponse as input
   */
  public Map<WpRespModel, List<Long>> resolveWpRespLabels(final List<WpRespLabelResponse> wpRespLabResponse) {

    Map<WpRespModel, List<Long>> wpRespLabMap = new HashMap<>();
    for (WpRespLabelResponse wpRespLabelResponse : wpRespLabResponse) {
      long paramId = wpRespLabelResponse.getParamId();
      WpRespModel wpRespModel = wpRespLabelResponse.getWpRespModel();
      List<Long> paramIdList = new ArrayList<>();
      if (wpRespLabMap.get(wpRespModel) == null) {
        wpRespLabMap.put(wpRespModel, paramIdList);
      }
      else {
        paramIdList = wpRespLabMap.get(wpRespModel);
      }
      paramIdList.add(paramId);
    }
    // For Adding parameter count in the WpRespModel
    wpRespLabMap.entrySet().forEach(wpMap -> wpMap.getKey().setParamCount((long) wpMap.getValue().size()));

    return wpRespLabMap;
  }


  /**
   * @param tRvwResult
   * @param rvwA2lWpDefnVersion
   * @param validWpRespSet
   * @param reviewPIDCVariant
   * @return Set<TA2lWpResponsibility>
   * @throws DataException
   */
  private Set<TA2lWpResponsibility> validateA2lWpRespBasedOnRvwVariant(final Set<TA2lWpResponsibility> validWpRespSet,
      final PidcVariant reviewPIDCVariant) {
    Set<TA2lWpResponsibility> tempValidWpRespSet = new HashSet<>();
    for (TA2lWpResponsibility ta2lWpResponsibility : validWpRespSet) {
      if (CommonUtils.isNotNull(ta2lWpResponsibility.getVariantGroup())) {
        for (TA2lVarGrpVariantMapping ta2lVarGrpVariantMapping : ta2lWpResponsibility.getVariantGroup()
            .getTA2lVarGrpVariantMappings()) {
          if (CommonUtils.isEqual(reviewPIDCVariant.getId(),
              ta2lVarGrpVariantMapping.getTabvProjectVariant().getVariantId())) {
            tempValidWpRespSet.add(ta2lWpResponsibility);
          }
        }
      }
      else {
        tempValidWpRespSet.add(ta2lWpResponsibility);
      }
    }
    return tempValidWpRespSet;
  }

  /**
   * Method to validate the questionnaire response status that has been used in the workpackage and responsibility
   *
   * @param wpRespStatusMap
   * @param unCompletedWPRespMap
   * @param completedWPRespMap
   * @param reportQnaireRespWrapper
   * @param a2lRespId
   * @param a2lWpId
   * @param ta2lWpResponsibility
   * @throws IcdmException
   */
  private void validateRvwQnaireResponseStatus(final Map<A2lWPRespModel, String> wpRespStatusMap,
      final Map<A2lWPRespModel, WPRespStatusMsgWrapper> unCompletedWPRespMap,
      final Map<A2lWPRespModel, WPRespStatusMsgWrapper> completedWPRespMap,
      final CdrReportQnaireRespWrapper reportQnaireRespWrapper, final Long a2lRespId, final Long a2lWpId,
      final A2lWPRespModel a2lWPRespModel)
      throws IcdmException {
    String wpName = reportQnaireRespWrapper.getWpIdAndNameMap().get(a2lWpId);
    if (CommonUtils.isEqual(wpName, ApicConstants.DEFAULT_A2L_WP_NAME)) {
      wpRespStatusMap.put(a2lWPRespModel, CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getDbType());
    }
    else if (CommonUtils.isNotEmpty(reportQnaireRespWrapper.getAllWpRespQnaireRespVersMap())) {
      boolean isRespAvailable = isRespAvailableInQnaireMap(reportQnaireRespWrapper, a2lRespId);
      // To check whether the questionnaire response is available in getAllWpRespQnaireRespVersMap for the a2l
      // workpackage
      boolean isWpAvailable = isWpAvailableInQnaireMap(reportQnaireRespWrapper, a2lRespId, a2lWpId, isRespAvailable);
      if (isRespAvailable && isWpAvailable) {
        // to validate if there is questionnaire responses without baseline
        if (isAnyQnaireNotBaselined(a2lRespId, a2lWpId, reportQnaireRespWrapper.getAllWpRespQnaireRespVersMap())) {
          fillWPRespMap(unCompletedWPRespMap, a2lWPRespModel, CDRConstants.QNAIRE_NOT_BASELINED_MSG);
          wpRespStatusMap.put(a2lWPRespModel, CDRConstants.WP_RESP_STATUS_TYPE.NOT_FINISHED.getDbType());
        }
        else {
          Set<RvwQnaireRespVersion> rvwQnaireRespVersSet =
              reportQnaireRespWrapper.getAllWpRespQnaireRespVersMap().get(a2lRespId).get(a2lWpId);
          // adding finished status in WP Resp Status Map for Simplified questionnaires
          if (rvwQnaireRespVersSet.contains(null)) {
            wpRespStatusMap.put(a2lWPRespModel, CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getDbType());
          }
          // method to validate the review questionniare response status
          validateRvwQnaireRespStatus(wpRespStatusMap, unCompletedWPRespMap, completedWPRespMap, rvwQnaireRespVersSet,
              a2lWPRespModel);
        }
      }
      else {
        // adding finished status in WP Resp Status Map for monica review without any questionnaires
        wpRespStatusMap.put(a2lWPRespModel, CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getDbType());
      }
    }
    else {
      // adding finished status in WP Resp Status Map for monica review without any questionnaires
      wpRespStatusMap.put(a2lWPRespModel, CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getDbType());
    }
  }


  /**
   * @param reportQnaireRespWrapper
   * @param a2lRespId
   * @return
   */
  private boolean isRespAvailableInQnaireMap(final CdrReportQnaireRespWrapper reportQnaireRespWrapper,
      final Long a2lRespId) {

    return reportQnaireRespWrapper.getAllWpRespQnaireRespVersMap().containsKey(a2lRespId);
  }


  /**
   * @param reportQnaireRespWrapper
   * @param a2lRespId
   * @param a2lWpId
   * @param isRespAvailable
   * @return
   */
  private boolean isWpAvailableInQnaireMap(final CdrReportQnaireRespWrapper reportQnaireRespWrapper,
      final Long a2lRespId, final Long a2lWpId, final boolean isRespAvailable) {

    return isRespAvailable ? reportQnaireRespWrapper.getAllWpRespQnaireRespVersMap().get(a2lRespId).containsKey(a2lWpId)
        : isRespAvailable;
  }


  /**
   * Method validate and identify if there is parameter mismatch between the review parameters and active
   * a2lWpDefVersion
   *
   * @param wpRespStatusMap
   * @param unCompletedWPRespMap
   * @param completedWPRespMap
   * @param reportQnaireRespWrapper
   * @param tRvwWpResp
   * @param ta2lWpResponsibility
   * @param wpRespModelVal
   * @throws IcdmException
   */
  private void validateParamMatchBtwRvwParamAndA2lWpParam(final Map<A2lWPRespModel, String> wpRespStatusMap,
      final Map<A2lWPRespModel, WPRespStatusMsgWrapper> unCompletedWPRespMap,
      final Map<A2lWPRespModel, WPRespStatusMsgWrapper> completedWPRespMap, final TRvwWpResp tRvwWpResp,
      final A2lWPRespModel a2lWPRespModel, final WpRespModel wpRespModelVal)
      throws IcdmException {
    if (CommonUtils.isNotEmpty(wpRespStatusMap) && CommonUtils
        .isEqual(CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getDbType(), wpRespStatusMap.get(a2lWPRespModel))) {
      Set<TRvwParameter> gettRvwParameter = tRvwWpResp.gettRvwParameter();
      if (CommonUtils.isNotNull(wpRespModelVal) &&
          CommonUtils.isEqual(gettRvwParameter.size(), wpRespModelVal.getParamCount().intValue())) {
        // Adding finished status if there is no status available in WP Resp Status Map
        wpRespStatusMap.put(a2lWPRespModel, CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getDbType());
        // filling the completed wp resp map
        fillWPRespMap(completedWPRespMap, a2lWPRespModel, CDRConstants.COMPLETED_WPRESP_MSG);
      }
      else {
        wpRespStatusMap.put(a2lWPRespModel, CDRConstants.WP_RESP_STATUS_TYPE.NOT_FINISHED.getDbType());
        // filling the uncompleted wp resp map
        fillWPRespMap(unCompletedWPRespMap, a2lWPRespModel, CDRConstants.RVW_PARAM_WPRESP_NOT_REVIEWED);
        // remove from completed map if the WP Resp status Fails in parameters score
        if (completedWPRespMap.containsKey(a2lWPRespModel)) {
          completedWPRespMap.remove(a2lWPRespModel);
        }
      }
    }
  }


  /**
   * @param wpRespStatusMap
   * @param a2lWpResponsibilityLoader
   * @param notFinishedWpRespSet
   */
  private void fillingNotFinishedWPResp(final Map<A2lWPRespModel, String> wpRespStatusMap,
      final Set<TA2lWpResponsibility> notFinishedWpRespSet) {
    notFinishedWpRespSet.forEach(wpResp ->
    // Not Finished Wp RESP are set to not finished in wpRespStatusMap
    wpRespStatusMap.put(getA2lWPRespModel(wpResp), CDRConstants.WP_RESP_STATUS_TYPE.NOT_FINISHED.getDbType()));
  }

  /**
   * @param wpRespStatusMap
   * @param unCompletedWPRespMap
   * @param completedWPRespMap
   * @param rvwQnaireRespVersSet
   * @param ta2lWpResponsibility
   * @throws IcdmException
   */
  private void validateRvwQnaireRespStatus(final Map<A2lWPRespModel, String> wpRespStatusMap,
      final Map<A2lWPRespModel, WPRespStatusMsgWrapper> unCompletedWPRespMap,
      final Map<A2lWPRespModel, WPRespStatusMsgWrapper> completedWPRespMap,
      final Set<RvwQnaireRespVersion> rvwQnaireRespVersSet, final A2lWPRespModel a2lWPRespModel)
      throws IcdmException {
    // iterate the set of questionnaire response for validating the status
    for (RvwQnaireRespVersion rvwQnaireRespVersion : rvwQnaireRespVersSet) {
      try {
        // rvwQnaireRespVersion is null for simplified Qnaire
        if (CommonUtils.isNotNull(rvwQnaireRespVersion)) {
          QnaireVersionModel qnaireVerModel = new QuestionnaireVersionLoader(getServiceData())
              .getQnaireVersionWithDetails(rvwQnaireRespVersion.getQnaireVersionId());
          // checking whether the status is all positive
          if (CDRConstants.QS_STATUS_TYPE.ALL_POSITIVE.getDbType()
              .equals(rvwQnaireRespVersion.getQnaireRespVersStatus())) {
            // adding finished status in WP Resp Status Map
            wpRespStatusMap.put(a2lWPRespModel, CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getDbType());
            fillWPRespMap(completedWPRespMap, a2lWPRespModel, CDRConstants.QNAIRE_RESP_STATUS_IS_COMPLETED_MSG);
          }
          else if (CDRConstants.QS_STATUS_TYPE.NOT_ALLOW_NEGATIVE.getDbType()
              .equals(rvwQnaireRespVersion.getQnaireRespVersStatus())) {
            wpRespStatusMap.put(a2lWPRespModel, CDRConstants.WP_RESP_STATUS_TYPE.NOT_FINISHED.getDbType());

            StringBuilder errorMsg = new StringBuilder();
            errorMsg.append("\"").append(qnaireVerModel.getQuestionnaireVersion().getName()).append("\"")
                .append(CDRConstants.QNAIRE_NEGATIVE_ANSWERS_FAIL_MSG);
            fillWPRespMap(unCompletedWPRespMap, a2lWPRespModel, errorMsg.toString());

            if (completedWPRespMap.containsKey(a2lWPRespModel)) {
              completedWPRespMap.remove(a2lWPRespModel);
            }
            break;
          }
          // checking whether the status is all positive (or) not all positive
          else if (CDRConstants.QS_STATUS_TYPE.NOT_ALL_POSITIVE.getDbType()
              .equals(rvwQnaireRespVersion.getQnaireRespVersStatus())) {
            // checking whether the questionnaire definition allows WP to be finished when the answers are negative
            if (CommonUtils.isNotNull(qnaireVerModel.getQuestionnaireVersion().getNoNegativeAnsAllowedFlag()) &&
                qnaireVerModel.getQuestionnaireVersion().getNoNegativeAnsAllowedFlag()
                    .equalsIgnoreCase(ApicConstants.CODE_YES)) {
              wpRespStatusMap.put(a2lWPRespModel, CDRConstants.WP_RESP_STATUS_TYPE.NOT_FINISHED.getDbType());

              StringBuilder errorMsg = new StringBuilder();
              errorMsg.append("\"").append(qnaireVerModel.getQuestionnaireVersion().getName()).append("\"")
                  .append(CDRConstants.QNAIRE_NEGATIVE_ANSWERS_FAIL_MSG);
              fillWPRespMap(unCompletedWPRespMap, a2lWPRespModel, errorMsg.toString());

              if (completedWPRespMap.containsKey(a2lWPRespModel)) {
                completedWPRespMap.remove(a2lWPRespModel);
              }
              break;
            }
            else if (CommonUtils.isNull(qnaireVerModel.getQuestionnaireVersion().getNoNegativeAnsAllowedFlag()) ||
                qnaireVerModel.getQuestionnaireVersion().getNoNegativeAnsAllowedFlag()
                    .equalsIgnoreCase(ApicConstants.CODE_NO)) {
              wpRespStatusMap.put(a2lWPRespModel, CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getDbType());
              fillWPRespMap(completedWPRespMap, a2lWPRespModel, CDRConstants.QNAIRE_RESP_STATUS_IS_COMPLETED_MSG);
            }
          }
          else {
            // adding Not finished status in WP Resp Status Map
            wpRespStatusMap.put(a2lWPRespModel, CDRConstants.WP_RESP_STATUS_TYPE.NOT_FINISHED.getDbType());
            fillWPRespMap(unCompletedWPRespMap, a2lWPRespModel, CDRConstants.QNAIRE_RESP_STATUS_FAIL_MSG);

            if (completedWPRespMap.containsKey(a2lWPRespModel)) {
              completedWPRespMap.remove(a2lWPRespModel);
            }
            break;
          }
        }
      }
      catch (DataException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
      }
    }
  }


  /**
   * @param rvwA2lWpDefnVersion
   * @param activeA2lWpDefnVersion
   * @param notFinishedWpRespSet
   * @param validWpRespSet
   * @param tRvwResult
   * @param reviewPIDCVariant
   * @return
   * @throws IcdmException
   */
  private Set<TA2lWpResponsibility> validateA2lWpRespForActiveWPDefVers(final TA2lWpDefnVersion rvwA2lWpDefnVersion,
      final TA2lWpDefnVersion activeA2lWpDefnVersion, final Set<TA2lWpResponsibility> notFinishedWpRespSet,
      final Map<A2lWPRespModel, WPRespStatusMsgWrapper> unCompletedWPRespMap, final PidcVariant reviewPIDCVariant)
      throws IcdmException {
    Set<TA2lWpResponsibility> validWpRespSet = new HashSet<>();
    if (rvwA2lWpDefnVersion.getWpDefnVersId() != activeA2lWpDefnVersion.getWpDefnVersId()) {
      checkForParamMismatchInWPDefVers(rvwA2lWpDefnVersion, activeA2lWpDefnVersion, notFinishedWpRespSet,
          validWpRespSet, unCompletedWPRespMap);
    }
    else {
      validWpRespSet.addAll(activeA2lWpDefnVersion.getTA2lWpResponsibility());
    }
    return validateA2lWpRespBasedOnRvwVariant(validWpRespSet, reviewPIDCVariant);
  }


  /**
   * @param rvwA2lWpDefnVersion
   * @param activeA2lWpDefnVersion
   * @param notFinishedWpRespSet
   * @param validWpRespSet
   * @param unCompletedWPRespMap
   * @throws IcdmException
   */
  private void checkForParamMismatchInWPDefVers(final TA2lWpDefnVersion rvwA2lWpDefnVersion,
      final TA2lWpDefnVersion activeA2lWpDefnVersion, final Set<TA2lWpResponsibility> notFinishedWpRespSet,
      final Set<TA2lWpResponsibility> validWpRespSet,
      final Map<A2lWPRespModel, WPRespStatusMsgWrapper> unCompletedWPRespMap)
      throws IcdmException {
    for (TA2lWpResponsibility ta2lWpResp : rvwA2lWpDefnVersion.getTA2lWpResponsibility()) {
      // find the matching Review a2lwpResponsibity from active a2l wp definition version
      TA2lWpResponsibility tActiveA2lWpResponsibility =
          findMatchingtA2lWpResp(ta2lWpResp, activeA2lWpDefnVersion.getTA2lWpResponsibility());
      // tActiveA2lWpResponsibility is null if the Review WP Resp not found in Active A2l WpDefVersion
      if (tActiveA2lWpResponsibility != null) {
        checkForParamMismatchBtwWPResp(notFinishedWpRespSet, validWpRespSet, tActiveA2lWpResponsibility, ta2lWpResp,
            unCompletedWPRespMap);
      }
      else {
        // if the Review WP Resp not found in Active WpDefVersion then add the wpResp to notFinishedWpRespSet
        notFinishedWpRespSet.add(ta2lWpResp);
        // add the message to unCompletedWPRespMap
        fillWPRespMap(unCompletedWPRespMap, getA2lWPRespModel(ta2lWpResp), CDRConstants.RVW_WORKPACKAGE_RESP_MSG);
      }
    }
  }


  /**
   * @param notFinishedWpRespSet
   * @param validWpRespSet
   * @param tActiveA2lWpResponsibility
   * @param ta2lWpResp
   * @throws IcdmException
   */
  private void checkForParamMismatchBtwWPResp(final Set<TA2lWpResponsibility> notFinishedWpRespSet,
      final Set<TA2lWpResponsibility> validWpRespSet, final TA2lWpResponsibility tActiveA2lWpResponsibility,
      final TA2lWpResponsibility ta2lWpResp, final Map<A2lWPRespModel, WPRespStatusMsgWrapper> unCompletedWPRespMap)
      throws IcdmException {
    // A2lWpParamMapping set for the Review WPResp
    Set<String> wpRespParamMapSet =
        new A2lWpParamMappingLoader(getServiceData()).getWpParamMapNameForWPResp(ta2lWpResp.getWpRespId());

    // A2lWpParamMapping set for the Active A2lWpDefVersions WPResp
    Set<A2lWpParamMapping> activeWPRespParamMapSet = new A2lWpParamMappingLoader(getServiceData())
        .getWpParamMappingForWPResp(tActiveA2lWpResponsibility.getWpRespId());
    // check if the WP param mappings are same for both the active and rvw a2lWpdefversion
    if (wpRespParamMapSet.size() == activeWPRespParamMapSet.size()) {
      boolean isAllParamAvailable = true;
      for (A2lWpParamMapping a2lWpParamMapping : activeWPRespParamMapSet) {
        if (!wpRespParamMapSet.contains(a2lWpParamMapping.getName())) {
          // if any of the param not available
          // add tActiveA2lWpResponsibility to notFinishedWpRespSet
          notFinishedWpRespSet.add(tActiveA2lWpResponsibility);
          isAllParamAvailable = false;
          fillWPRespMap(unCompletedWPRespMap, getA2lWPRespModel(tActiveA2lWpResponsibility),
              CDRConstants.PARAMETER_MISMATCH_MSG);
          break;
        }
      }
      if (isAllParamAvailable) {
        // Add tActiveA2lWpResponsibility to validWpRespSet
        validWpRespSet.add(tActiveA2lWpResponsibility);
      }
    }
    else {
      // add to notFinishedWpRespSet if there is a mismatch in param count
      notFinishedWpRespSet.add(tActiveA2lWpResponsibility);
      fillWPRespMap(unCompletedWPRespMap, getA2lWPRespModel(tActiveA2lWpResponsibility),
          CDRConstants.PARAMETER_MISMATCH_MSG);
    }
  }

  private TA2lWpResponsibility findMatchingtA2lWpResp(final TA2lWpResponsibility ta2lWpResp,
      final List<TA2lWpResponsibility> ta2lWpResponsibilityList) {
    for (TA2lWpResponsibility ta2lWpResponsibility : ta2lWpResponsibilityList) {
      if (ta2lWpResp.getA2lWp().equals(ta2lWpResponsibility.getA2lWp()) &&
          ta2lWpResp.getA2lResponsibility().equals(ta2lWpResponsibility.getA2lResponsibility())) {
        return ta2lWpResponsibility;
      }
    }
    return null;
  }

  /**
   * @param wpRespStatusMap
   * @param unCompletedWPRespMap
   * @param completedWPRespMap
   * @param tRvwWpResp
   * @throws IcdmException
   */
  private void validateReviewParamScore(final Map<A2lWPRespModel, String> wpRespStatusMap,
      final Map<A2lWPRespModel, WPRespStatusMsgWrapper> unCompletedWPRespMap,
      final Map<A2lWPRespModel, WPRespStatusMsgWrapper> completedWPRespMap, final TRvwWpResp tRvwWpResp,
      final A2lWPRespModel a2lWPRespModel)
      throws IcdmException {
    // Validating the status of Review Parameters for that WP Resp Combination
    if (CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getDbType().equals(wpRespStatusMap.get(a2lWPRespModel))) {
      for (TRvwParameter tRvwParameter : tRvwWpResp.gettRvwParameter()) {
        if (isParamRvwScoreValid(tRvwParameter.getArcReleasedFlag(), tRvwParameter.getReviewScore())) {
          // Adding finished status if there is no status available in WP Resp Status Map
          wpRespStatusMap.put(a2lWPRespModel, CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getDbType());
          // filling the completed wp resp map
          fillWPRespMap(completedWPRespMap, a2lWPRespModel, CDRConstants.COMPLETED_WPRESP_MSG);
        }
        else {
          wpRespStatusMap.put(a2lWPRespModel, CDRConstants.WP_RESP_STATUS_TYPE.NOT_FINISHED.getDbType());

          // filling the uncompleted wp resp map
          fillWPRespMap(unCompletedWPRespMap, a2lWPRespModel, CDRConstants.RVW_PARAM_WPRESP_FAIL_MSG);
          // remove from completed map if the WP Resp status Fails in parameters score
          if (completedWPRespMap.containsKey(a2lWPRespModel)) {
            completedWPRespMap.remove(a2lWPRespModel);
          }
          // breaking the loop if there is any parameter with score other than 8 or 9
          break;
        }
      }
    }
  }


  /**
   * @param rvwScore
   * @param tRvwParameter
   * @return
   */
  private boolean isARCReleaseCompleted(final String arcReleaseFlag, final String rvwScore) {
    return CommonUtils.getBooleanType(arcReleaseFlag) &&
        CommonUtils.isNotEqual(DATA_REVIEW_SCORE.S_8.getDbType(), rvwScore) &&
        CommonUtils.isNotEqual(DATA_REVIEW_SCORE.S_9.getDbType(), rvwScore) &&
        CommonUtils.isNotEqual(DATA_REVIEW_SCORE.S_0.getDbType(), rvwScore);
  }


  /**
   * @param wpRespStatusMap
   * @param unCompletedWPRespMap
   * @param completedWPRespMap
   * @param a2lWpRespUpdatedSet
   * @return
   */
  private WPRespStatusOutputInternalModel fillWPRESPStatusOutputModel(final Map<A2lWPRespModel, String> wpRespStatusMap,
      final Map<A2lWPRespModel, WPRespStatusMsgWrapper> unCompletedWPRespMap,
      final Map<A2lWPRespModel, WPRespStatusMsgWrapper> completedWPRespMap) {
    WPRespStatusOutputInternalModel wpRespStatusOutputModel = new WPRespStatusOutputInternalModel();
    wpRespStatusOutputModel.setWpRespStatus(wpRespStatusMap);
    wpRespStatusOutputModel.setInCompleteWPRespMap(unCompletedWPRespMap);
    wpRespStatusOutputModel.setCompletedWPRespMap(completedWPRespMap);
    return wpRespStatusOutputModel;
  }

  /**
   * @param inCompleteWPRespMap
   * @param tRvwWpResp
   * @param failureMsg
   * @throws IcdmException
   */
  private void fillWPRespMap(final Map<A2lWPRespModel, WPRespStatusMsgWrapper> wpRespMap,
      final A2lWPRespModel a2lWPRespModel, final String failureMsg)
      throws IcdmException {
    WPRespStatusMsgWrapper respStatusFailureWrapper = new WPRespStatusMsgWrapper();
    setWPRespStatusMsgWrapper(a2lWPRespModel, failureMsg, respStatusFailureWrapper);
    if (CommonUtils.isNotEmptyString(respStatusFailureWrapper.getWorkPackageName()) &&
        CommonUtils.isNotEmptyString(respStatusFailureWrapper.getRespName())) {
      wpRespMap.put(a2lWPRespModel, respStatusFailureWrapper);
    }
  }

  /**
   * @param tRvwWpResp
   * @param outputMessage
   * @param respStatusFailureWrapper
   * @throws IcdmException
   */
  private void setWPRespStatusMsgWrapper(final A2lWPRespModel a2lWPRespModel, final String outputMessage,
      final WPRespStatusMsgWrapper respStatusFailureWrapper)
      throws IcdmException {
    respStatusFailureWrapper.setWpRespId(a2lWPRespModel.getWpRespId());
    respStatusFailureWrapper.setWorkPackageName(
        new A2lWorkPackageLoader(getServiceData()).getDataObjectByID(a2lWPRespModel.getA2lWpId()).getName());
    respStatusFailureWrapper.setRespName(
        new A2lResponsibilityLoader(getServiceData()).getDataObjectByID(a2lWPRespModel.getA2lRespId()).getName());
    respStatusFailureWrapper.setOutputStatusMsg(outputMessage);

  }

  private A2lWPRespModel getA2lWPRespModel(final TA2lWpResponsibility ta2lWpResponsibility) {
    A2lWPRespModel a2lWPRespModel = new A2lWPRespModel();
    a2lWPRespModel.setA2lRespId(ta2lWpResponsibility.getA2lResponsibility().getA2lRespId());
    a2lWPRespModel.setA2lWpId(ta2lWpResponsibility.getA2lWp().getA2lWpId());
    a2lWPRespModel.setWpRespId(ta2lWpResponsibility.getWpRespId());
    return a2lWPRespModel;
  }


  public WPRespStatusOutputInternalModel computeTreeViewSelWPRespStatus(final TreeViewSelectnRespWP selWpResp)
      throws IcdmException {

    // Map to hold uncompleted/ unfinished wp resp map
    Map<A2lWPRespModel, WPRespStatusMsgWrapper> unCompletedWPRespMap = new HashMap<>();

    // Map to hold completed/ finished wp resp map
    Map<A2lWPRespModel, WPRespStatusMsgWrapper> completedWPRespMap = new HashMap<>();

    // Map Holds the Status of all the WP Resp combination
    Map<A2lWPRespModel, String> wpRespStatusMap = new HashMap<>();

    Long variantID = selWpResp.getVariantID();

    Long pidcA2lID = selWpResp.getPidcA2lID();

    TPidcA2l tPidcA2l = new PidcA2lLoader(getServiceData()).getEntityObject(pidcA2lID);
    long pidcVersId = tPidcA2l.getTPidcVersion().getPidcVersId();

    List<WpRespLabelResponse> wpRespLabResponse =
        new A2lWpResponsibilityLoader(getServiceData()).getWpResp(pidcA2lID, variantID);

    Map<WpRespModel, List<Long>> resolveWpRespLabels = resolveWpRespLabels(wpRespLabResponse);

    // Map key-a2l responsibility id,value map of key-a2l workpackage id and value-a2l wp resposibility id
    for (Entry<Long, Map<Long, A2lWPRespModel>> respIDWpIDA2lWpRespIDEntry : selWpResp.getRespWpA2lWpRespModelMap()
        .entrySet()) {

      for (Entry<Long, A2lWPRespModel> wpIDA2lWpRespIDEntry : respIDWpIDA2lWpRespIDEntry.getValue().entrySet()) {

        A2lWPRespModel a2lWpRespModel = wpIDA2lWpRespIDEntry.getValue();
        if (CommonUtils.isNotNull(a2lWpRespModel.getWpRespId())) {

          WpRespModel wpRespModelVal =
              getWpRespModel(resolveWpRespLabels, respIDWpIDA2lWpRespIDEntry.getKey(), wpIDA2lWpRespIDEntry.getKey());

          List<Long> paramIdList = resolveWpRespLabels.get(wpRespModelVal);

          Map<Long, WPFinishRvwDet> paramWpFinishedRvwDetMap =
              new CDRReportLoader(getServiceData()).fetchRvwDataForA2lWpRespParam(
                  tPidcA2l.getTabvProjectidcard().getProjectId(), pidcVersId, variantID, pidcA2lID, paramIdList);

          if (paramWpFinishedRvwDetMap.size() != paramIdList.size()) {
            fillWPRespMap(unCompletedWPRespMap, a2lWpRespModel, CDRConstants.RVW_PARAM_WPRESP_NOT_REVIEWED);
            wpRespStatusMap.put(a2lWpRespModel, CDRConstants.WP_RESP_STATUS_TYPE.NOT_FINISHED.getDbType());
            continue;
          }
          /**
           * validate whether parameters are reviewed and review score is validated if param review is complete for all
           * the parameters then check for qnaire status
           */

          validateToFinishTreeViewSelWpResp(selWpResp, unCompletedWPRespMap, completedWPRespMap, wpRespStatusMap,
              a2lWpRespModel, paramIdList, paramWpFinishedRvwDetMap);
        }
        // Scenario:Variant and WP Resp avaiable at different varaint group
        else {
          fillWPRespMap(unCompletedWPRespMap, a2lWpRespModel, CDRConstants.RVW_WORKPACKAGE_RESP_MSG);
        }
      }
    }
    return fillWPRESPStatusOutputModel(wpRespStatusMap, unCompletedWPRespMap, completedWPRespMap);
  }


  /**
   * @param selRespWP
   * @param unCompletedWPRespMap
   * @param completedWPRespMap
   * @param wpRespStatusMap
   * @param a2lWpRespModel
   * @param paramIdList
   * @param paramWpFinishedRvwDetMap
   * @throws IcdmException
   */
  private void validateToFinishTreeViewSelWpResp(final TreeViewSelectnRespWP selRespWP,
      final Map<A2lWPRespModel, WPRespStatusMsgWrapper> unCompletedWPRespMap,
      final Map<A2lWPRespModel, WPRespStatusMsgWrapper> completedWPRespMap,
      final Map<A2lWPRespModel, String> wpRespStatusMap, final A2lWPRespModel a2lWpRespModel,
      final List<Long> paramIdList, final Map<Long, WPFinishRvwDet> paramWpFinishedRvwDetMap)
      throws IcdmException {

    TPidcA2l tPidcA2l = new PidcA2lLoader(getServiceData()).getEntityObject(selRespWP.getPidcA2lID());
    long pidcVersId = tPidcA2l.getTPidcVersion().getPidcVersId();
    if (validateSelWpRespParamRvwStatus(unCompletedWPRespMap, wpRespStatusMap, paramWpFinishedRvwDetMap, paramIdList,
        a2lWpRespModel) &&
        validateSelWpRespQnaireStatus(pidcVersId, unCompletedWPRespMap, wpRespStatusMap, selRespWP.getVariantID(),
            a2lWpRespModel)) {

      wpRespStatusMap.put(a2lWpRespModel, CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getDbType());
      fillWPRespMap(completedWPRespMap, a2lWpRespModel, CDRConstants.COMPLETED_WPRESP_MSG);
    }
  }

  /**
   * @param pidcVersId
   * @param unCompletedWPRespMap
   * @param wpRespStatusMap
   * @param variantID
   * @param statusValidationMsg
   * @param a2lWpRespModel
   * @throws IcdmException
   */
  private boolean validateSelWpRespQnaireStatus(final long pidcVersId,
      final Map<A2lWPRespModel, WPRespStatusMsgWrapper> unCompletedWPRespMap,
      final Map<A2lWPRespModel, String> wpRespStatusMap, final Long variantID, final A2lWPRespModel a2lWpRespModel)
      throws IcdmException {

    // for checking qnaire status of the parameter.Qnaire is fetched on the basis of pidc version and variant
    RvwQnaireResponseLoader qnaireResponseLoader = new RvwQnaireResponseLoader(getServiceData());

    CdrReportQnaireRespWrapper reportQnaireRespWrapper = qnaireResponseLoader.getQniareRespVersByPidcVersIdAndVarId(
        pidcVersId,
        CommonUtils.isNull(variantID) || CommonUtils.isEqual(variantID, ApicConstants.NO_VARIANT_ID) ? null : variantID,
        false);

    long a2lRespID = a2lWpRespModel.getA2lRespId();
    long a2lWpID = a2lWpRespModel.getA2lWpId();

    Map<Long, Map<Long, Set<RvwQnaireRespVersion>>> allWpRespQnaireRespVersMap =
        reportQnaireRespWrapper.getAllWpRespQnaireRespVersMap();

    // Story 689081 one out of two questionnaires is not baselined, we should show the status for this label as ‘Not
    // baselined’.
    if (isAnyQnaireNotBaselined(a2lRespID, a2lWpID, allWpRespQnaireRespVersMap)) {
      fillWPRespMap(unCompletedWPRespMap, a2lWpRespModel, CDRConstants.QNAIRE_NOT_BASELINED_MSG);
      wpRespStatusMap.put(a2lWpRespModel, CDRConstants.WP_RESP_STATUS_TYPE.NOT_FINISHED.getDbType());
      return false;
    }

    /**
     * Story 679331 condition to validate questionnaire status Scenario 1.)for monica review without any questionnaires
     * Scenario 2.)For some Divisions questionnaires are not applicable, in that case questionnaire status should be
     * 'Ok' and WP Finished as 'Finished' 3) For Simplified Qnaire Enabled PIDC, there will be Empty WP/Resp Structure
     * under Review Qnaire
     */

    if (CommonUtils.isNullOrEmpty(allWpRespQnaireRespVersMap) ||
        !isWpAvailableInQnaireMap(reportQnaireRespWrapper, a2lRespID, a2lWpID,
            isRespAvailableInQnaireMap(reportQnaireRespWrapper, a2lRespID)) ||
        CommonUtils.isNullOrEmpty(reportQnaireRespWrapper.getWpRespQnaireRespVersMap()) ||
        CommonUtils.isNull(reportQnaireRespWrapper.getWpRespQnaireRespVersMap().get(a2lRespID)) ||
        CommonUtils.isNull(reportQnaireRespWrapper.getWpRespQnaireRespVersMap().get(a2lRespID).get(a2lWpID))) {
      return true;
    }

    boolean isQnaireRespStatusComplete = false;
    String qnaireStatus = "";
    // evaluate the qnaire status if all the qnaires available for wp responsibility combination is baselined
    if (reportQnaireRespWrapper.getWpRespQnaireRespVersMap().containsKey(a2lRespID)) {
      Map<Long, String> wpQnaireRespVerMap = reportQnaireRespWrapper.getWpRespQnaireRespVersStatusMap().get(a2lRespID);

      if (wpQnaireRespVerMap.containsKey(a2lWpID)) {

        // get qnaire status
        qnaireStatus = wpQnaireRespVerMap.get(a2lWpID);
      }
      // validateQnaire
      isQnaireRespStatusComplete = isWpRespQnaireStatusComplete(qnaireStatus);

    }
    if (!isQnaireRespStatusComplete) {

      fillWPRespMap(unCompletedWPRespMap, a2lWpRespModel, getVarWpRespQnaireStatusFailureMsg(qnaireStatus,
          reportQnaireRespWrapper.getWpRespQnaireRespVersMap().get(a2lRespID).get(a2lWpID)));
      wpRespStatusMap.put(a2lWpRespModel, CDRConstants.WP_RESP_STATUS_TYPE.NOT_FINISHED.getDbType());
    }
    return isQnaireRespStatusComplete;
  }


  /**
   * @param a2lRespID
   * @param a2lWpID
   * @param allWpRespQnaireRespVersMap
   * @return
   */
  private boolean isAnyQnaireNotBaselined(final long a2lRespID, final long a2lWpID,
      final Map<Long, Map<Long, Set<RvwQnaireRespVersion>>> allWpRespQnaireRespVersMap) {
    if (allWpRespQnaireRespVersMap.containsKey(a2lRespID)) {
      Map<Long, Set<RvwQnaireRespVersion>> wpQnaireRespVerMap = allWpRespQnaireRespVersMap.get(a2lRespID);


      if (wpQnaireRespVerMap.containsKey(a2lWpID)) {
        Set<RvwQnaireRespVersion> rvwQniareRespVersSet = wpQnaireRespVerMap.get(a2lWpID);
        // Iterate through Latest RvwQnaireRespVersions belonging to WP Resp Combination
        for (RvwQnaireRespVersion rvwQnaireRespVers : rvwQniareRespVersSet) {
          // rvwQnaireRespVers is null for simpified Qnaire
          // If latest RvwQnaireRespVersion is 'Working Set', then baseline is not available for the RvwQnaireResponse
          if (CommonUtils.isNotNull(rvwQnaireRespVers) && isRvwQnaireRespVersWorkingSet().test(rvwQnaireRespVers)) {
            return true;
          }
        }
      }
    }

    return false;
  }

  /**
   * @return true, if RvwQnaireRespVersion is working set, else false
   */
  private Predicate<RvwQnaireRespVersion> isRvwQnaireRespVersWorkingSet() {
    return rvwQnaireRespVers -> CommonUtils.isEqual(rvwQnaireRespVers.getRevNum(), CDRConstants.WORKING_SET_REV_NUM);
  }

  /**
   * @param unCompletedWPRespMap
   * @param wpRespStatusMap
   * @param statusValidationMsg
   * @param wpRespModelVal
   * @param tA2lWpResp
   * @param paramWpFinishedRvwDetMap
   * @param paramIdList
   * @param a2lWpRespModel
   * @param isWpRespComplete
   * @return
   * @throws IcdmException
   */
  private boolean validateSelWpRespParamRvwStatus(
      final Map<A2lWPRespModel, WPRespStatusMsgWrapper> unCompletedWPRespMap,
      final Map<A2lWPRespModel, String> wpRespStatusMap, final Map<Long, WPFinishRvwDet> paramWpFinishedRvwDetMap,
      final List<Long> paramIdList, final A2lWPRespModel a2lWpRespModel)
      throws IcdmException {


    boolean isWpRespParamRvwComplete = false;

    for (Long paramId : paramIdList) {

      WPFinishRvwDet paramWpFinishedDet = paramWpFinishedRvwDetMap.get(paramId);
      if (isLatestParamRvwValidated(paramWpFinishedDet)) {

        isWpRespParamRvwComplete = true;
      }
      else {

        isWpRespParamRvwComplete = false;

        wpRespStatusMap.put(a2lWpRespModel, CDRConstants.WP_RESP_STATUS_TYPE.NOT_FINISHED.getDbType());

        // filling the uncompleted wp resp map
        fillWPRespMap(unCompletedWPRespMap, a2lWpRespModel, getParamRvwStatusFailureMsg(paramWpFinishedDet));

        break;
      }
    }
    return isWpRespParamRvwComplete;
  }

  /**
   * @param paramWpFinishedDet
   * @return parameter review failure output message
   */
  private String getParamRvwStatusFailureMsg(final WPFinishRvwDet paramWpFinishedDet) {
    if (!isParamRvwed(paramWpFinishedDet)) {
      return CDRConstants.RVW_PARAM_WPRESP_NOT_REVIEWED;
    }
    if (!isParamRvwOfficial(paramWpFinishedDet)) {
      return CDRConstants.RVW_PARAM_WPRESP_NOT_OFFICIAL;
    }
    if (!isParamRvwScoreValid(paramWpFinishedDet.getArcReleaseFlag(), paramWpFinishedDet.getRvwScore())) {
      return CDRConstants.RVW_PARAM_WPRESP_FAIL_MSG;
    }
    if (!isParamRvwLocked(paramWpFinishedDet)) {
      return CDRConstants.RVW_PARAM_WPRESP_NOT_LOCKED;
    }
    return "";
  }

  /**
   * @param paramWpFinishedDet
   * @return parameter review failure output message
   * @throws IcdmException
   */
  private String getVarWpRespQnaireStatusFailureMsg(final String qnaireStatus,
      final Set<RvwQnaireRespVersion> rvwQnaireRespVrsnSet)
      throws IcdmException {

    if (CDRConstants.QS_STATUS_TYPE.NOT_ALLOW_NEGATIVE.getDbType().equals(qnaireStatus)) {

      return updateStatusMsgForNoNegAnsAllwdQnaire(rvwQnaireRespVrsnSet);
    }
    if (CDRConstants.QS_STATUS_TYPE.NOT_ANSWERED.getDbType().equals(qnaireStatus) ||
        CDRConstants.QS_STATUS_TYPE.NOT_ALLOWED_FINISHED_WP.getDbType().equals(qnaireStatus) ||
        CommonUtils.isEmptyString(qnaireStatus)) {

      return CDRConstants.QNAIRE_RESP_STATUS_FAIL_MSG;

    }

    return "";
  }

  /**
   * @param qnaireStatus
   * @param rvwQnaireRespVrsnSet
   * @param rvwQnaireRespVrsnSet
   * @param tA2lWpResp
   * @param statusValidationMsg
   * @return true
   */
  private boolean isWpRespQnaireStatusComplete(final String qnaireStatus) {

    if (CommonUtils.isEmptyString(qnaireStatus)) {

      return false;
    }
    if (CDRConstants.QS_STATUS_TYPE.NOT_ALLOW_NEGATIVE.getDbType().equals(qnaireStatus)) {

      return false;
    }
    if (CDRConstants.QS_STATUS_TYPE.NOT_ANSWERED.getDbType().equals(qnaireStatus) ||
        CDRConstants.QS_STATUS_TYPE.NOT_ALLOWED_FINISHED_WP.getDbType().equals(qnaireStatus)) {

      return false;
    }
    // Qnaire status to finsih wp
    return CDRConstants.QS_STATUS_TYPE.ALL_POSITIVE.getDbType().equals(qnaireStatus) ||
        CDRConstants.QS_STATUS_TYPE.NOT_ALL_POSITIVE.getDbType().equals(qnaireStatus);

  }

  /**
   * @param rvwQnaireRespVrsnSet
   * @param tA2lWpResp
   * @return
   * @throws IcdmException
   */
  private String updateStatusMsgForNoNegAnsAllwdQnaire(final Set<RvwQnaireRespVersion> rvwQnaireRespVrsnSet)
      throws IcdmException {


    // iterate the set of questionnaire response for validating the status
    for (RvwQnaireRespVersion rvwQnaireRespVersion : rvwQnaireRespVrsnSet) {

      QnaireVersionModel qnaireVerModel = new QuestionnaireVersionLoader(getServiceData())
          .getQnaireVersionWithDetails(rvwQnaireRespVersion.getQnaireVersionId());
      // checking whether the questionnaire definition allows WP to be finished when the answers are negative
      if (CDRConstants.QS_STATUS_TYPE.NOT_ALLOW_NEGATIVE.getDbType()
          .equals(rvwQnaireRespVersion.getQnaireRespVersStatus()) &&
          CommonUtils.isNotNull(qnaireVerModel.getQuestionnaireVersion().getNoNegativeAnsAllowedFlag()) &&
          qnaireVerModel.getQuestionnaireVersion().getNoNegativeAnsAllowedFlag()
              .equalsIgnoreCase(ApicConstants.CODE_YES)) {

        return new StringBuilder().append("\"").append(qnaireVerModel.getQuestionnaireVersion().getName()).append("\"")
            .append(CDRConstants.QNAIRE_NEGATIVE_ANSWERS_FAIL_MSG).toString();

      }
    }
    return "";
  }

  /**
   * @param paramWpFinishedDet Rvw Details to finish WP
   * @return true if the latest review on the parameter under the given variant satisfies the conditions
   */
  private boolean isLatestParamRvwValidated(final WPFinishRvwDet paramWpFinishedDet) {

    return isParamRvwed(paramWpFinishedDet) && isParamRvwOfficial(paramWpFinishedDet) &&
        isParamRvwLocked(paramWpFinishedDet) &&
        isParamRvwScoreValid(paramWpFinishedDet.getArcReleaseFlag(), paramWpFinishedDet.getRvwScore());
  }


  /**
   * @param arcReleaseFlag
   * @param paramRvwScore
   * @return true if the review score for parameter is allowed to finish wp responsibility
   */
  private boolean isParamRvwScoreValid(final String arcReleaseFlag, final String paramRvwScore) {

    return isARCReleaseCompleted(arcReleaseFlag, paramRvwScore) ||
        (DATA_REVIEW_SCORE.S_8.getDbType().equals(paramRvwScore) ||
            DATA_REVIEW_SCORE.S_9.getDbType().equals(paramRvwScore));
  }


  /**
   * @param paramWpFinishedDet
   * @return
   */
  private boolean isParamRvwed(final WPFinishRvwDet paramWpFinishedDet) {

    String rvwScore = paramWpFinishedDet.getRvwScore();

    return CommonUtils.isNotEmptyString(rvwScore) && !DATA_REVIEW_SCORE.S_0.getDbType().equals(rvwScore);

  }

  private boolean isParamRvwOfficial(final WPFinishRvwDet paramWpFinishedDet) {

    return CDRConstants.REVIEW_TYPE.OFFICIAL.getDbType().equals(paramWpFinishedDet.getRvwType());
  }

  private boolean isParamRvwLocked(final WPFinishRvwDet paramWpFinishedDet) {

    return CDRConstants.REVIEW_LOCK_STATUS.YES.getDbType().equals(paramWpFinishedDet.getLockStatus());
  }


}
