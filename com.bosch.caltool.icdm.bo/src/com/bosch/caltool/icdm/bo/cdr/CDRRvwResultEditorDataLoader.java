/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2lResponsibilityLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWorkPackageLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWpDefnVersionLoader;
import com.bosch.caltool.icdm.bo.a2l.FunctionLoader;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireRespVersionLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireResponseLoader;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.bo.wp.WorkPackageDivisionLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.Timer;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lResponsibility;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWorkPackage;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpDefnVersion;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResponsibility;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResponsibilityStatus;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireRespVariant;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireResponse;
import com.bosch.caltool.icdm.database.entity.cdr.TA2lDepParam;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParameter;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwVariant;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.WP_RESP_STATUS_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRResultFunction;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.ReviewResultEditorData;
import com.bosch.caltool.icdm.model.cdr.RvwFile;
import com.bosch.caltool.icdm.model.cdr.RvwParametersSecondary;
import com.bosch.caltool.icdm.model.cdr.RvwResultWPandRespModel;
import com.bosch.caltool.icdm.model.cdr.RvwWpResp;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespStatusData;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;

/**
 * Loader class for Review result editor Data
 *
 * @author hnu1cob
 */
public class CDRRvwResultEditorDataLoader extends AbstractSimpleBusinessObject {

  /**
   * @param serviceData Service Data
   */
  public CDRRvwResultEditorDataLoader(final ServiceData serviceData) {
    super(serviceData);
  }


  /**
   * @param resultId , cdr review result id
   * @param rvwVarId review variant id
   * @return ReviewResultEditorData
   * @throws IcdmException error during fetching data
   */
  public ReviewResultEditorData getReviewResultEditorData(final Long resultId, final Long rvwVarId)
      throws IcdmException {
    CDRReviewResultLoader resultLoader = new CDRReviewResultLoader(getServiceData());
    // to validate whether the result id is valid , exception occurs if the id is invalid
    resultLoader.validateId(resultId);
    TRvwResult entityObject = resultLoader.getEntityObject(resultId);

    CDRResultParameterLoader cdrParamLoader = new CDRResultParameterLoader(getServiceData());
    Map<Long, CDRResultParameter> paramMap = cdrParamLoader.getByResultObj(entityObject);

    CDRResultFunctionLoader funcLoader = new CDRResultFunctionLoader(getServiceData());
    Map<Long, CDRResultFunction> funcMap = funcLoader.getByResultObj(entityObject);

    ReviewResultEditorData data = new ReviewResultEditorData();

    data.setFuncMap(funcMap);
    data.setParamMap(paramMap);
    data.setPverVarName(entityObject.getTPidcA2l().getMvTa2lFileinfo().getSdomPverVariant());

    String a2lFileName = entityObject.getTPidcA2l().getMvTa2lFileinfo().getFilename();

    if (entityObject.getTPidcA2l().getVcdmA2lName() != null) {
      a2lFileName = entityObject.getTPidcA2l().getVcdmA2lName();
    }
    data.setA2lFileName(a2lFileName);
    RvwParametersSecondaryLoader secondayParamLoader = new RvwParametersSecondaryLoader(getServiceData());

    Map<Long, List<CDRResultParameter>> funcParamMap = new HashMap<>();
    Map<Long, List<RvwFile>> paramAdditionalFilesMap = new HashMap<>();
    Map<Long, Map<Long, RvwParametersSecondary>> rvwParamSecondaryMap = new ConcurrentHashMap<>();
    RvwFileLoader rvwFileLoader = new RvwFileLoader(getServiceData());

// Fetch the dependent parameters from T_A2L_DEP_PARAMS table
    Long a2lFileId = entityObject.getTPidcA2l().getMvTa2lFileinfo().getId();
    List<TA2lDepParam> a2lDepParamList = new A2lDepParamLoader(getServiceData()).getByA2lFileId(a2lFileId);

    // read only parameter relevant for review result
    Set<Long> readOnlyParamInRvwResult = new HashSet<>();
    // Dependent parameter details for review result
    Map<Long, List<String>> depParamInRvwResult = new HashMap<>();
    // iterating through review result parameters
    for (CDRResultParameter param : paramMap.values()) {
      Long funcId = param.getRvwFunId();
      List<CDRResultParameter> paramList = funcParamMap.get(funcId);
      if (paramList == null) {
        paramList = new ArrayList<>();
      }
      paramList.add(param);
      funcParamMap.put(funcId, paramList);

      TRvwParameter entity = cdrParamLoader.getEntityObject(param.getId());
      paramAdditionalFilesMap.putAll(rvwFileLoader.getParamFiles(entity));
      rvwParamSecondaryMap.putAll(secondayParamLoader.getSecondaryParams(entity));

      Long paramId = param.getParamId();
      // Fetching Read only param falg from T_rvw_parameters and store the value in review result
      if (yOrNToBoolean(entity.getReadOnlyParamFlag())) {
        readOnlyParamInRvwResult.add(paramId);
      }

      // Fetch Dependent param details and store in review result
      List<TA2lDepParam> depParamList =
          a2lDepParamList.stream().filter(p -> (p.getParamName()).equals(param.getName())).collect(Collectors.toList());
      // If the param Id does not have any depends on parameters, 'depParamList' would be empty
      if (CommonUtils.isNotEmpty(depParamList)) {
        List<String> depOnParamList =
            depParamList.stream().map(TA2lDepParam::getDependsOnParamName).collect(Collectors.toList());
        depParamInRvwResult.put(paramId, depOnParamList);
      }
    }
    data.setReadOnlyParamSet(readOnlyParamInRvwResult);
    data.setDepParamMap(depParamInRvwResult);
    data.setRvwParamSecondaryMap(rvwParamSecondaryMap);
    data.setFuncParamMap(funcParamMap);
    data.setParamAdditionalFiles(paramAdditionalFilesMap);

    CDRReviewResult reviewResult = resultLoader.getDataObjectByID(resultId);

    data.setReviewResult(reviewResult);

    // get the parent review details
    getParentReviewDetails(resultLoader, cdrParamLoader, paramMap, data, reviewResult);
    data.setIcdmFiles(rvwFileLoader.getFiles(entityObject));

    RvwAttrValueLoader attrValLoader = new RvwAttrValueLoader(getServiceData());
    data.setAttrValMap(attrValLoader.getByResultObj(entityObject));

    RvwParticipantLoader participantsLoader = new RvwParticipantLoader(getServiceData());
    data.setParticipantsMap(participantsLoader.getByResultObj(entityObject));

    UserLoader userLoader = new UserLoader(getServiceData());

    data.setCreatedUser(userLoader.getDataObjectByID(userLoader.getUserIdByUserName(entityObject.getCreatedUser())));

    RvwVariantLoader variantLoader = new RvwVariantLoader(getServiceData());
    data.setVariantsMap(variantLoader.getByResultObj(entityObject));

    RvwResultsSecondaryLoader secondaryResLoader = new RvwResultsSecondaryLoader(getServiceData());
    data.setSecondayResultsMap(secondaryResLoader.getByResultObj(entityObject));

    // get the variant used in review
    getResultVariant(entityObject, data);

    if (null != entityObject.gettRuleSet()) {
      RuleSetLoader ruleSetLoader = new RuleSetLoader(getServiceData());
      data.setRuleSet(ruleSetLoader.getDataObjectByID(entityObject.gettRuleSet().getRsetId()));
    }

    if (null != entityObject.getTWorkpackageDivision()) {
      WorkPackageDivisionLoader wpDivLoader = new WorkPackageDivisionLoader(getServiceData());
      data.setWpDivision(wpDivLoader.getDataObjectByID(entityObject.getTWorkpackageDivision().getWpDivId()));
    }

    // get pidc details
    getPidcDetails(data, reviewResult);

    Map<String, Set<RvwResultWPandRespModel>> a2lWpMap = getA2LWpRespData(entityObject, data);
    data.setParamIdAndWpAndRespMap(cdrParamLoader.getParamMappedToWpAndResp(resultId));

    data.setRespWpFinishedStatusMap(fillCompletedWPRESPStaus(entityObject, data));

    // get the func and func param corresponding to result func and param
    getFuncParamObjs(paramMap, funcMap, data);

    // 489480 - Show WP definition version and variant group name in review result editor
    A2lWpDefnVersionLoader a2lWpDefinitionVersionLoader = new A2lWpDefnVersionLoader(getServiceData());
    data.setRvwInfoWpDefDetails(a2lWpDefinitionVersionLoader.getWpDefnDetails(reviewResult.getWpDefnVersId(),
        reviewResult.getPrimaryVariantId()));

    Timer timer = new Timer();
    Set<QnaireRespStatusData> respVersWithStatusSet = getRespVersWithStatusForRvwRes(entityObject, a2lWpMap, rvwVarId);
    data.setQnaireDataForRvwSet(respVersWithStatusSet);
    timer.finish();
    getLogger().info("Questionnaires count={} , Time taken = {}", respVersWithStatusSet.size(), timer.getTimeTaken());

    getLogger().info(
        " CDR Review Result getReviewResultEditorData completed. CDR Result Parameters = {} , CDR Result Functions = {}, " +
            " Questionnaires count={} ",
        paramMap.size(), funcMap.size(), respVersWithStatusSet.size());
    return data;
  }


  /**
   * @param entityObject
   * @param data
   * @return
   * @throws DataException
   */
  private Map<String, Set<RvwResultWPandRespModel>> getA2LWpRespData(final TRvwResult entityObject,
      final ReviewResultEditorData data)
      throws DataException {
    Map<Long, RvwWpResp> rvwWpRespMap = new RvwWpRespLoader(getServiceData()).getByResultObj(entityObject);
    Map<Long, String> wpIdAndRespMap = new HashMap<>();
    // key - resp name, value - set of RvwResultWPandRespModel
    Map<String, Set<RvwResultWPandRespModel>> a2lWpMap = new HashMap<>();
    if (!rvwWpRespMap.isEmpty()) {
      Set<RvwResultWPandRespModel> a2lWpSet = new HashSet<>();
      Set<RvwResultWPandRespModel> a2lWpRespSet = new HashSet<>();
      Map<Long, A2lResponsibility> a2lResponsibilityMap = new HashMap<>();
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

        fillWpRespMap(a2lWpMap, objForResponsibleNodes, a2lResp);
        wpIdAndRespMap.put(rvwWpResp.getA2lWpId(), a2lResp.getAliasName());
        a2lResponsibilityMap.put(a2lResp.getId(), a2lResp);
        // object contain both A2lWp and A2lResp
        a2lWpRespSet.add(objForResponsibleNodes);
      }
      data.setA2lWpMap(a2lWpMap);
      data.setA2lWpSet(a2lWpSet);
      data.setA2lWpRespSet(a2lWpRespSet);
      data.setA2lResponsibilityMap(a2lResponsibilityMap);
    }
    return a2lWpMap;
  }

  private Map<Long, Map<Long, String>> fillCompletedWPRESPStaus(final TRvwResult tRvwResult,
      final ReviewResultEditorData data) {
    Map<Long, Map<Long, String>> respWpStatusMap = new HashMap<>();
    TA2lWpDefnVersion ta2lWpDefnVersion = new A2lWpDefnVersionLoader(getServiceData())
        .getActiveA2lWPDefnVersionEntityFromA2l(tRvwResult.getTPidcA2l().getPidcA2lId());
    if (ta2lWpDefnVersion != null) {
      for (TA2lWpResponsibility ta2lWpResponsibility : ta2lWpDefnVersion.getTA2lWpResponsibility()) {
        TA2lResponsibility a2lResponsibility = ta2lWpResponsibility.getA2lResponsibility();
        TA2lWorkPackage a2lWp = ta2lWpResponsibility.getA2lWp();
        List<TA2lWpResponsibilityStatus> gettA2lWPRespStatus = ta2lWpResponsibility.gettA2lWPRespStatus();
        fillRespWpStatusMap(data, respWpStatusMap, a2lResponsibility, a2lWp, gettA2lWPRespStatus);
      }
    }
    return respWpStatusMap;
  }


  /**
   * @param data
   * @param respWpStatusMap
   * @param a2lResponsibility
   * @param a2lWp
   * @param gettA2lWPRespStatus
   */
  private void fillRespWpStatusMap(final ReviewResultEditorData data,
      final Map<Long, Map<Long, String>> respWpStatusMap, final TA2lResponsibility a2lResponsibility,
      final TA2lWorkPackage a2lWp, final List<TA2lWpResponsibilityStatus> gettA2lWPRespStatus) {
    if (gettA2lWPRespStatus.isEmpty()) {
      // adding respstatus as not finished if the statusmap is empty
      setWpNotFinishedStatus(respWpStatusMap, a2lResponsibility, a2lWp);
    }
    else {
      for (TA2lWpResponsibilityStatus ta2lWpResponsibilityStatus : gettA2lWPRespStatus) {
        // Check whether the status is applicable for review result's variant
        if (validateWpRespStatusBasedOnVar(data, ta2lWpResponsibilityStatus)) {
          setFinishedStatusForVar(respWpStatusMap, a2lResponsibility, a2lWp, ta2lWpResponsibilityStatus);
        }
      }
    }
  }


  /**
   * @param respWpStatusMap
   * @param a2lResponsibility
   * @param a2lWp
   * @param ta2lWpResponsibilityStatus
   */
  private void setFinishedStatusForVar(final Map<Long, Map<Long, String>> respWpStatusMap,
      final TA2lResponsibility a2lResponsibility, final TA2lWorkPackage a2lWp,
      final TA2lWpResponsibilityStatus ta2lWpResponsibilityStatus) {
    Long a2lRespId = CommonUtils.isNotNull(ta2lWpResponsibilityStatus.gettA2lResp())
        ? ta2lWpResponsibilityStatus.gettA2lResp().getA2lRespId() : a2lResponsibility.getA2lRespId();
    if (respWpStatusMap.containsKey(a2lRespId)) {
      Map<Long, String> wpStatusMap = respWpStatusMap.get(a2lRespId);
      if (!wpStatusMap.containsKey(a2lWp.getA2lWpId())) {
        respWpStatusMap.get(a2lRespId).put(a2lWp.getA2lWpId(), ta2lWpResponsibilityStatus.getWpRespFinStatus());
      }

    }
    else {
      Map<Long, String> tempWpStatusMap = new HashMap<>();
      tempWpStatusMap.put(a2lWp.getA2lWpId(), ta2lWpResponsibilityStatus.getWpRespFinStatus());
      respWpStatusMap.put(a2lRespId, tempWpStatusMap);
    }
  }


  /**
   * @param respWpStatusMap
   * @param a2lResponsibility
   * @param a2lWp
   */
  private void setWpNotFinishedStatus(final Map<Long, Map<Long, String>> respWpStatusMap,
      final TA2lResponsibility a2lResponsibility, final TA2lWorkPackage a2lWp) {
    if (respWpStatusMap.containsKey(a2lResponsibility.getA2lRespId())) {
      Map<Long, String> wpStatusMap = respWpStatusMap.get(a2lResponsibility.getA2lRespId());
      if (!wpStatusMap.containsKey(a2lWp.getA2lWpId())) {
        respWpStatusMap.get(a2lResponsibility.getA2lRespId()).put(a2lWp.getA2lWpId(),
            WP_RESP_STATUS_TYPE.NOT_FINISHED.getDbType());
      }
    }
    else {
      Map<Long, String> tempWpStatusMap = new HashMap<>();
      tempWpStatusMap.put(a2lWp.getA2lWpId(), WP_RESP_STATUS_TYPE.NOT_FINISHED.getDbType());
      respWpStatusMap.put(a2lResponsibility.getA2lRespId(), tempWpStatusMap);
    }
  }


  private boolean validateWpRespStatusBasedOnVar(final ReviewResultEditorData data,
      final TA2lWpResponsibilityStatus ta2lWpResponsibilityStatus) {
    return (CommonUtils.isNull(ta2lWpResponsibilityStatus.getTabvProjVar()) &&
        CommonUtils.isNull(data.getFirstVariant())) ||
        (CommonUtils.isNotNull(ta2lWpResponsibilityStatus.getTabvProjVar()) &&
            CommonUtils.isNotNull(data.getFirstVariant()) && CommonUtils.isEqual(data.getFirstVariant().getId(),
                ta2lWpResponsibilityStatus.getTabvProjVar().getVariantId()));
  }

  /**
   * @param entityObject Result Id
   * @param a2lWpMap
   * @param rvwVarId
   * @throws DataException
   */
  private Set<QnaireRespStatusData> getRespVersWithStatusForRvwRes(final TRvwResult entityObject,
      final Map<String, Set<RvwResultWPandRespModel>> a2lWpMap, final Long rvwVarId)
      throws DataException {

    Set<QnaireRespStatusData> qnaireRespVersSet = new HashSet<>();
    if (CommonUtils.isNotEmpty(entityObject.getTRvwVariants()) && (null != rvwVarId)) {
      // review results with variant
      for (TRvwVariant dbRvwVariant : entityObject.getTRvwVariants()) {
        // select only the relevant review variant id
        if (dbRvwVariant.getRvwVarId() == rvwVarId.longValue()) {
          // get all qnaire resp varinats attached to project variant
          Set<TRvwQnaireRespVariant> tRvwQnaireRespVariants =
              dbRvwVariant.getTabvProjectVariant().getTRvwQnaireRespVariants();
          getQnaireVersFromQRespVariants(a2lWpMap, qnaireRespVersSet, tRvwQnaireRespVariants, false);
        }
      }
    }
    else {
      // review result with <NO-VARIANT>
      Set<TRvwQnaireRespVariant> tRvwQnaireRespVariants =
          entityObject.getTPidcA2l().getTPidcVersion().getTRvwQnaireRespVariants();
      getQnaireVersFromQRespVariants(a2lWpMap, qnaireRespVersSet, tRvwQnaireRespVariants, true);
    }
    return qnaireRespVersSet;

  }

  /**
   * @param a2lWpMap
   * @param qnaireRespVersSet
   * @param tRvwQnaireRespVariants
   * @param noVarReview
   * @throws DataException
   */
  private void getQnaireVersFromQRespVariants(final Map<String, Set<RvwResultWPandRespModel>> a2lWpMap,
      final Set<QnaireRespStatusData> qnaireRespVersSet, final Set<TRvwQnaireRespVariant> tRvwQnaireRespVariants,
      final boolean noVarReview)
      throws DataException {
    // iterate through all ques resp variants
    for (TRvwQnaireRespVariant tRvwQnaireRespVariant : tRvwQnaireRespVariants) {
      if (!noVarReview || (tRvwQnaireRespVariant.getTabvProjectVariant() == null)) {
        TRvwQnaireResponse tRvwQnaireResponse = tRvwQnaireRespVariant.getTRvwQnaireResponse();
        // tRvwQnaireResponse is Null for Simplified Qnaire -Empty WP/Resp Structure
        if (CommonUtils.isNotNull(tRvwQnaireResponse)) {
          // get Responsisbility name and workpackage id from ques response
          long a2lRespId = tRvwQnaireRespVariant.gettA2lResponsibility().getA2lRespId();
          String respName = new A2lResponsibilityLoader(getServiceData()).getDataObjectByID(a2lRespId).getName();
          // get RvwResultWPandRespModel set for given resp name from review result
          Set<RvwResultWPandRespModel> wpRespset = a2lWpMap.get(respName);
          // check if the model has resp and wp combinations
          checkAndAddQuesRespVersion(qnaireRespVersSet, tRvwQnaireResponse, tRvwQnaireRespVariant.gettA2lWorkPackage(),
              wpRespset, respName);
        }
      }
    }
  }

  /**
   * @param qnaireRespVersSet
   * @param tRvwQnaireResponse
   * @param ta2lWorkPackage
   * @param wpRespset
   * @param respName
   * @throws DataException
   */
  private void checkAndAddQuesRespVersion(final Set<QnaireRespStatusData> qnaireRespVersSet,
      final TRvwQnaireResponse tRvwQnaireResponse, final TA2lWorkPackage ta2lWorkPackage,
      final Set<RvwResultWPandRespModel> wpRespset, final String respName)
      throws DataException {
    RvwQnaireResponseLoader rvwQnaireRespLoader = new RvwQnaireResponseLoader(getServiceData());
    if ((null != wpRespset) && ApicConstants.CODE_NO.equals(tRvwQnaireResponse.getDeletedFlag())) {
      for (RvwResultWPandRespModel wpRespModel : wpRespset) {
        if (ta2lWorkPackage.getA2lWpId() == wpRespModel.getA2lWorkPackage().getId().longValue()) {
          RvwQnaireResponse rvwQnaireResp = rvwQnaireRespLoader.getDataObjectByID(tRvwQnaireResponse.getQnaireRespId());
          fillRvwQnaireRespVersMap(qnaireRespVersSet, respName, wpRespModel.getA2lWorkPackage().getName(),
              rvwQnaireResp);
          break;
        }
      }
    }
  }

  /**
   * @param qnaireRespVersSet key - qnaire resp id Value - Qnairerespversion working set
   * @param qnaireRespId qnaire resp id
   * @param respName Responsibility name
   * @param wpName workpackage name
   * @param rvwQnaireResp RvwQnaireResponse
   * @param qnaireRespName qnaire Response name
   * @throws IcdmException exception in ws
   */
  public void fillRvwQnaireRespVersMap(final Set<QnaireRespStatusData> qnaireRespVersSet, final String respName,
      final String wpName, final RvwQnaireResponse rvwQnaireResp)
      throws DataException {
    RvwQnaireRespVersionLoader loader = new RvwQnaireRespVersionLoader(getServiceData());
    Map<Long, RvwQnaireRespVersion> retMap = loader.getQnaireRespVersionsByRespId(rvwQnaireResp.getId());
    for (RvwQnaireRespVersion rvwQnaireRespVersion : retMap.values()) {
      if (rvwQnaireRespVersion.getRevNum() == 0l) {
        // add working set version
        QnaireRespStatusData qnaireDataForRvw = new QnaireRespStatusData();
        qnaireDataForRvw.setQnaireRespName(rvwQnaireResp.getName());
        qnaireDataForRvw.setRespName(respName);
        qnaireDataForRvw.setStatus(rvwQnaireRespVersion.getQnaireRespVersStatus());
        qnaireDataForRvw.setWpName(wpName);
        qnaireDataForRvw.setQuesRespId(rvwQnaireResp.getId());
        qnaireDataForRvw.setPrimaryVarName(rvwQnaireResp.getPrimaryVarRespWpName());
        qnaireRespVersSet.add(qnaireDataForRvw);
      }
    }
  }

  /**
   * @param a2lWpRespMap
   * @param resultWPandRespModel
   * @param a2lWpRespPal
   * @param a2lResp
   */
  public void fillWpRespMap(final Map<String, Set<RvwResultWPandRespModel>> a2lWpRespMap,
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
   * @param resultLoader
   * @param loader
   * @param paramMap
   * @param data
   * @param reviewResult
   * @throws DataException
   */
  private void getParentReviewDetails(final CDRReviewResultLoader resultLoader, final CDRResultParameterLoader loader,
      final Map<Long, CDRResultParameter> paramMap, final ReviewResultEditorData data,
      final CDRReviewResult reviewResult)
      throws DataException {
    if (reviewResult.getDeltaReviewType() != null) {
      if (CDRConstants.DELTA_REVIEW_TYPE.DELTA_REVIEW.getDbType().equals(reviewResult.getDeltaReviewType()) &&
          (null != reviewResult.getOrgResultId())) {
        CDRReviewResult parentResult = resultLoader.getDataObjectByID(reviewResult.getOrgResultId());
        data.setParentReviewResult(parentResult);
        TRvwResult parentEntity = resultLoader.getEntityObject(reviewResult.getOrgResultId());
        Map<Long, CDRResultParameter> parentParamMap = loader.getByResultObj(parentEntity);
        data.setParentParamMap(parentParamMap);
      }
      else if (CDRConstants.DELTA_REVIEW_TYPE.PROJECT_DELTA_REVIEW.getDbType()
          .equals(reviewResult.getDeltaReviewType())) {
        Map<Long, CDRResultParameter> projDeltaParentParamMap = new HashMap<>();
        CDRResultParameter parentParameter = null;
        for (CDRResultParameter param : paramMap.values()) {

          if (param.getParentParamId() != null) {

            CDRResultParameter parentParam = loader.getDataObjectByID(param.getParentParamId());
            projDeltaParentParamMap.put(parentParam.getId(), parentParam);
            parentParameter = parentParam;
          }
        }

        if (parentParameter != null) {

          CDRReviewResult parentResult = resultLoader.getDataObjectByID(parentParameter.getResultId());

          PidcA2l pidcA2l = new PidcA2lLoader(getServiceData()).getDataObjectByID(parentResult.getPidcA2lId());

          PidcVersion pidcVers = new PidcVersionLoader(getServiceData()).getDataObjectByID(pidcA2l.getPidcVersId());

          data.setProjDeltaParentPidcVersName(pidcVers.getName());
          data.setProjDeltaParentVariantName(parentResult.getPrimaryVariantName());
        }
        data.setProjDeltaParentParamMap(projDeltaParentParamMap);
      }
    }
  }


  /**
   * @param entityObject
   * @param data
   * @throws DataException
   */
  private void getResultVariant(final TRvwResult entityObject, final ReviewResultEditorData data) throws DataException {
    PidcVariant firstVariant = null;
    Timestamp createdDate = null;
    if (CommonUtils.isNotNull(entityObject.getTRvwVariants())) {
      Iterator<TRvwVariant> iterator = entityObject.getTRvwVariants().iterator();
      while (iterator.hasNext()) {
        TRvwVariant dbRvwVar = iterator.next();
        if ((createdDate == null) || createdDate.after(dbRvwVar.getCreatedDate())) {
          createdDate = dbRvwVar.getCreatedDate();
          Long variantID = dbRvwVar.getTabvProjectVariant().getVariantId();
          firstVariant = new PidcVariantLoader(getServiceData()).getDataObjectByID(variantID);
        }
      }
    }

    data.setFirstVariant(firstVariant);
  }


  /**
   * @param data
   * @param reviewResult
   * @throws DataException
   */
  private void getPidcDetails(final ReviewResultEditorData data, final CDRReviewResult reviewResult)
      throws DataException {
    PidcA2lLoader pidcA2lLoader = new PidcA2lLoader(getServiceData());
    PidcA2l pidcA2l = pidcA2lLoader.getDataObjectByID(reviewResult.getPidcA2lId());
    data.setPidcA2l(pidcA2l);

    PidcVersionLoader pidcVersLoader = new PidcVersionLoader(getServiceData());
    PidcVersion pidcVers = pidcVersLoader.getDataObjectByID(pidcA2l.getPidcVersId());
    data.setPidcVers(pidcVers);

    PidcLoader pidcLoader = new PidcLoader(getServiceData());
    Pidc pidc = pidcLoader.getDataObjectByID(pidcA2l.getProjectId());
    data.setPidc(pidc);
  }


  /**
   * @param paramMap
   * @param funcMap
   * @param data
   * @throws UnAuthorizedAccessException
   * @throws DataException
   */
  private void getFuncParamObjs(final Map<Long, CDRResultParameter> paramMap,
      final Map<Long, CDRResultFunction> funcMap, final ReviewResultEditorData data)
      throws IcdmException {

    ParameterLoader paramLoader = new ParameterLoader(getServiceData());
    Map<String, Parameter> paramObjMap = new HashMap<>();
    for (CDRResultParameter param : paramMap.values()) {
      paramObjMap.put(param.getName().toUpperCase(Locale.getDefault()),
          paramLoader.getDataObjectByID(param.getParamId()));
    }
    data.setParamMappingMap(paramObjMap);

    FunctionLoader cdrFuncLoader = new FunctionLoader(getServiceData());
    Map<String, Function> funcObjMap = new HashMap<>();
    for (CDRResultFunction func : funcMap.values()) {
      funcObjMap.put(func.getName().toUpperCase(Locale.getDefault()),
          cdrFuncLoader.getDataObjectByID(func.getFunctionId()));
    }
    data.setFuncMappingMap(funcObjMap);
  }
}
