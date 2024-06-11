/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.calcomp.caldatautils.CalDataComparism.CompareResult;
import com.bosch.calcomp.caldatautils.CompareQuantized;
import com.bosch.calcomp.caldatautils.ItemsToCompare.AvailableItemsForComparison;
import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldataphy.CalDataPhy;
import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2lVariantGroupLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWpDefnVersionLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWpParamMappingLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWpRespStatusUpdationCommand;
import com.bosch.caltool.icdm.bo.a2l.A2lWpResponsibilityStatusLoader;
import com.bosch.caltool.icdm.bo.a2l.FunctionLoader;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireRespCreationCommand;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireResponseCommand;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireResponseLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireResponseStatusHandler;
import com.bosch.caltool.icdm.bo.cdr.review.CDRReviewResultUtil;
import com.bosch.caltool.icdm.bo.cdr.review.ReviewRuleSetData;
import com.bosch.caltool.icdm.bo.cdr.review.ReviewedInfo;
import com.bosch.caltool.icdm.bo.general.IcdmFileDataCommand;
import com.bosch.caltool.icdm.bo.general.IcdmFileDataLoader;
import com.bosch.caltool.icdm.bo.general.IcdmFilesCommand;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lVariantGroup;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpDefnVersion;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResponsibility;
import com.bosch.caltool.icdm.database.entity.apic.TPidcA2l;
import com.bosch.caltool.icdm.database.entity.cdr.TA2lDepParam;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwVariant;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwWpResp;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWPRespModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpRespStatusUpdationModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibilityStatus;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.A2lDepParam;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.DELTA_REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_FILE_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_STATUS;
import com.bosch.caltool.icdm.model.cdr.CDRResultFunction;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.model.cdr.MonicaReviewData;
import com.bosch.caltool.icdm.model.cdr.MonicaReviewFileData;
import com.bosch.caltool.icdm.model.cdr.RvwAttrValue;
import com.bosch.caltool.icdm.model.cdr.RvwFile;
import com.bosch.caltool.icdm.model.cdr.RvwFunctionModel;
import com.bosch.caltool.icdm.model.cdr.RvwParametersSecondary;
import com.bosch.caltool.icdm.model.cdr.RvwParticipant;
import com.bosch.caltool.icdm.model.cdr.RvwResultsSecondary;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;
import com.bosch.caltool.icdm.model.cdr.RvwWpAndRespModel;
import com.bosch.caltool.icdm.model.cdr.RvwWpResp;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespCreationModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespUpdationModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;
import com.bosch.caltool.icdm.model.cdr.review.ReviewInput;
import com.bosch.caltool.icdm.model.general.IcdmFileData;
import com.bosch.caltool.icdm.model.general.IcdmFiles;
import com.bosch.caltool.icdm.model.user.User;


/**
 * Command class for Review Result
 *
 * @author BRU2COB
 */
public class CDRReviewResultCommand extends AbstractCommand<CDRReviewResult, CDRReviewResultLoader> {

  private final Map<String, Long> rvwFunctionWithParamMap = new HashMap<>();

  private ReviewedInfo reviewInfo;


  private MonicaReviewFileData monicaReviewFileData;
  private ReviewInput reviewInputData;

  /**
   * File name and the corresponding icdm file object
   */
  private final Map<String, RvwFile> filesCreatedMap = new HashMap<>();

  private final Map<ReviewRuleSetData, RvwResultsSecondary> ruleDataMap = new ConcurrentHashMap<>();

  private CDRResultParamCreator cdrResParamCreator;

  private RvwVariant rvwVariant;

  private A2LFileInfo a2lFileInfo;

  // Monica Review Map to Hold the RVWWpRespId
  private final Map<Long, RvwWpAndRespModel> monicaRvwWPRespIdAndWPRespModelMap = new HashMap<>();

  // Map to hold A2lWpResponsibilityStatus object after Wp finished status reset
  private Map<Long, A2lWpResponsibilityStatus> a2lWpRespStatusAfterUpd = new HashMap<>();

  // Map to hold A2lWpResponsibilityStatus object before Wp finished status reset
  private Map<Long, A2lWpResponsibilityStatus> a2lWpRespStatusBeforeUpd = new HashMap<>();

  /**
   * List of newly created A2lWpResponsibilityStatus entries in T_A2L_WP_RESPONSIBILITY_STATUS table - for CNS purpose
   */
  private List<A2lWpResponsibilityStatus> listOfNewlyCreatedA2lWpRespStatus = new ArrayList<>();

  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @param monicaReviewFileData MoniCa file details
   * @throws IcdmException error when initializing
   */
  public CDRReviewResultCommand(final ServiceData serviceData, final CDRReviewResult input,
      final MonicaReviewFileData monicaReviewFileData, final boolean isUpdate, final boolean isDelete)
      throws IcdmException {
    super(serviceData, input, new CDRReviewResultLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : isUpdateCmd(isUpdate));
    this.monicaReviewFileData = monicaReviewFileData;
  }

  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @param reviewInfo review result details
   * @param reviewInputData reviewInputData
   * @throws IcdmException error when initializing
   */
  public CDRReviewResultCommand(final ServiceData serviceData, final CDRReviewResult input,
      final ReviewedInfo reviewInfo, final ReviewInput reviewInputData, final boolean isUpdate, final boolean isDelete)
      throws IcdmException {
    super(serviceData, input, new CDRReviewResultLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : isUpdateCmd(isUpdate));
    this.reviewInfo = reviewInfo;
    this.reviewInputData = reviewInputData;
  }

  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public CDRReviewResultCommand(final ServiceData serviceData, final CDRReviewResult input, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, input, new CDRReviewResultLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : isUpdateCmd(isUpdate));
  }

  /**
   * @param isUpdate
   * @return
   */
  private static COMMAND_MODE isUpdateCmd(final boolean isUpdate) {
    return isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {

    TRvwResult entity = createRvwResult();

    Long rvwResultId = entity.getResultId();

    // create secondary result
    createSecondaryResult(rvwResultId);

    // create wp resp
    createWpResp(rvwResultId);

    // method to reset the a2lWPResp status to not finished based on current active wpdefinition version
    // a2lWPResp status reset to not finished should happen only for start and official review
    if (CommonUtils.isNotEqual(entity.getReviewType(), CDRConstants.REVIEW_TYPE.TEST.getDbType())) {
      resetA2lWpRespStatusForActiveWpDefVer(entity);
    }

    // Create TrvwFunction
    createReviewFunctions(rvwResultId);

    // Create TRvwFiles for InputFiles ex : DCM
    createInputFiles(rvwResultId);

    // create a2l dependent parameters
    createA2lDepParam(entity.getTPidcA2l());

    // Create Rvw Parameter
    createReviewParams(entity);
    createSecRvwParams(entity);
    createFunLabFiles(rvwResultId);
    createRuleFile(rvwResultId);

    // Create TRvwFiles for MonicaFiles ex : .xlsx
    createMonicaFiles(rvwResultId);
    createOutputFiles(rvwResultId);
    createOutputFilesForCompliParams(rvwResultId);

    // Create TRvwParticipants
    createParticipants(rvwResultId);
    // Set status of the review result, based on the reviewed flag of all result params
    updateCDRReviewStatus(entity);
    createRvwAttrValue(entity);
    // Create Review Variant
    createRvwVariant(rvwResultId);
    // To Add General Questions Response for WP and Resp for Official Review
    if (isRvwQnaireApplicable(entity)) {
      createGeneralRvwQnairesResponse(entity);
    }

    checkForValidDeltaReview(entity);
  }


  /**
   * @param tRvwResult
   * @throws DataException
   * @throws IcdmException
   */
  private void resetA2lWpRespStatusForActiveWpDefVer(final TRvwResult tRvwResult) throws IcdmException {
    for (TA2lWpDefnVersion ta2lWpDefnVersion : tRvwResult.getTPidcA2l().gettA2lWpDefnVersions()) {
      if (CommonUtils.getBooleanType((ta2lWpDefnVersion.getIsActive())) &&
          CommonUtils.isNotNull(tRvwResult.getTRvwWpResps())) {

        A2lWpRespStatusUpdationModel a2lWpRespStatusInputUpdModel =
            getInputUpdationModel(tRvwResult, ta2lWpDefnVersion);

        if (CommonUtils.isNotEmpty(a2lWpRespStatusInputUpdModel.getA2lWpRespStatusListToBeCreated()) ||
            CommonUtils.isNotEmpty(a2lWpRespStatusInputUpdModel.getA2lWpRespStatusToBeUpdatedMap())) {
          // Update the 'WP finished Status' in T_A2L_WP_Responsibility_Status
          A2lWpRespStatusUpdationCommand a2lWPRespUpdCmd =
              new A2lWpRespStatusUpdationCommand(getServiceData(), a2lWpRespStatusInputUpdModel);
          executeChildCommand(a2lWPRespUpdCmd);
        }
      }
    }
  }

  /**
   * @param tRvwResult
   * @param ta2lWpDefnVersion
   * @return
   * @throws DataException
   */
  private A2lWpRespStatusUpdationModel getInputUpdationModel(final TRvwResult tRvwResult,
      final TA2lWpDefnVersion ta2lWpDefnVersion)
      throws DataException {

    A2lWpResponsibilityStatusLoader a2lWpResponsibilityStatusLoader =
        new A2lWpResponsibilityStatusLoader(getServiceData());
    List<A2lWpResponsibilityStatus> listOfA2lWPRespStatusToBeCreated = new ArrayList<>();
    Map<Long, A2lWpResponsibilityStatus> a2lWpRespStatusToBeUpdMap = new HashMap<>();
    Long variantId = getVarId();
    Long varId =
        CommonUtils.isNull(variantId) || CommonUtils.isEqual(variantId, ApicConstants.NO_VARIANT_ID) ? null : variantId;

    for (TRvwWpResp tRvwWpResp : tRvwResult.getTRvwWpResps()) {

      A2lWPRespModel a2lWpRespModel = findMatchinA2lWpRespModel(ta2lWpDefnVersion, tRvwWpResp, varId);

      if (null != a2lWpRespModel) {

        Long wpRespId = a2lWpRespModel.getWpRespId();
        A2lWpResponsibilityStatus a2lWpRespStatusBeforeUpd =
            a2lWpResponsibilityStatusLoader.getA2lWpStatusByVarAndWpRespId(varId, wpRespId,
                a2lWpRespModel.isInheritedFlag() ? null : a2lWpRespModel.getA2lRespId());

        // If A2lResponsibilityStatus record is Not Available Already, then create else update the status
        createOrUpdA2lWPRespStatus(listOfA2lWPRespStatusToBeCreated, a2lWpRespStatusToBeUpdMap, a2lWpRespModel, varId,
            wpRespId, a2lWpRespStatusBeforeUpd);
      }
    }
    A2lWpRespStatusUpdationModel a2lWpRespStatusUpdModelBeforeUpdate = new A2lWpRespStatusUpdationModel();
    a2lWpRespStatusUpdModelBeforeUpdate.setA2lWpRespStatusToBeUpdatedMap(a2lWpRespStatusToBeUpdMap);
    a2lWpRespStatusUpdModelBeforeUpdate.setA2lWpRespStatusListToBeCreated(listOfA2lWPRespStatusToBeCreated);

    return a2lWpRespStatusUpdModelBeforeUpdate;
  }

  /**
   * @param listOfA2lWPRespStatusToBeCreated
   * @param a2lWpRespStatusMapBeforeUpd
   * @param a2lWpRespModel
   * @param varId
   * @param wpRespId
   * @param a2lWpRespStatusBeforeUpd
   * @param wpFinStatus
   */
  private void createOrUpdA2lWPRespStatus(final List<A2lWpResponsibilityStatus> listOfA2lWPRespStatusToBeCreated,
      final Map<Long, A2lWpResponsibilityStatus> a2lWpRespStatusMapBeforeUpd, final A2lWPRespModel a2lWpRespModel,
      final Long varId, final Long wpRespId, final A2lWpResponsibilityStatus a2lWpRespStatusBeforeUpd) {

    String wpFinStatus = CDRConstants.WP_RESP_STATUS_TYPE.NOT_FINISHED.getDbType();

    if (CommonUtils.isNull(a2lWpRespStatusBeforeUpd)) {

      A2lWpResponsibilityStatus newA2lWPRespStatus = new A2lWpResponsibilityStatus();
      newA2lWPRespStatus.setVariantId(varId);
      newA2lWPRespStatus.setWpRespId(wpRespId);
      newA2lWPRespStatus.setWpRespFinStatus(wpFinStatus);
      if (!a2lWpRespModel.isInheritedFlag()) {
        newA2lWPRespStatus.setA2lRespId(a2lWpRespModel.getA2lRespId());
      }

      listOfA2lWPRespStatusToBeCreated.add(newA2lWPRespStatus);
    }
    else {
      // Setting the workpackage responsible status in a2lWpResponsibility object
      a2lWpRespStatusBeforeUpd.setWpRespFinStatus(wpFinStatus);

      a2lWpRespStatusMapBeforeUpd.put(a2lWpRespStatusBeforeUpd.getId(), a2lWpRespStatusBeforeUpd);
    }
  }

  private A2lWPRespModel findMatchinA2lWpRespModel(final TA2lWpDefnVersion ta2lWpDefnVersion,
      final TRvwWpResp tRvwWpResp, final Long varId)
      throws DataException {

    A2lWPRespModel a2lWPRespModel = null;

    // loop through all WP/Resp in Active Wp defn Version
    for (TA2lWpResponsibility ta2lWpResponsibility : ta2lWpDefnVersion.getTA2lWpResponsibility()) {

      Long tA2lWPRespWPId = ta2lWpResponsibility.getA2lWp().getA2lWpId();
      Long rvwWPId = tRvwWpResp.getTA2lWorkPackage().getA2lWpId();
      Long rvwA2lRespId = tRvwWpResp.gettA2lResponsibility().getA2lRespId();
      Long wpRespId = ta2lWpResponsibility.getWpRespId();

      // Check whether the variant group (var grp will be null if variant is under Default WP) and Workpackage are same
      TA2lVariantGroup wpRespVarGrp = ta2lWpResponsibility.getVariantGroup();
      A2lVariantGroup rvwVarGroup =
          new A2lVariantGroupLoader(getServiceData()).getVariantGroup(ta2lWpDefnVersion.getWpDefnVersId(), varId);
      boolean isVarGrpEqual = isVarGrpNull(wpRespVarGrp, rvwVarGroup) ? CommonUtils.isEqual(rvwVarGroup, wpRespVarGrp)
          : CommonUtils.isEqual(rvwVarGroup.getId(), wpRespVarGrp.getA2lVarGrpId());

      if (isVarGrpEqual && CommonUtils.isEqual(rvwWPId, tA2lWPRespWPId)) {

        // check whether the responsibility are same.
        if (CommonUtils.isEqual(rvwA2lRespId, ta2lWpResponsibility.getA2lResponsibility().getA2lRespId())) {
          a2lWPRespModel = getWPRespModel(tRvwWpResp, wpRespId, true);
        }
        else {
          // Handling Customized Responsibility for which WPRespId is same as Original WP/Responsibility but resp Id
          // will
          // be different
          a2lWPRespModel = getWPRespModelForCustomizedResp(tRvwWpResp, a2lWPRespModel, tA2lWPRespWPId, rvwWPId,
              rvwA2lRespId, wpRespId);
        }
      }
    }

    return a2lWPRespModel;
  }

  private boolean isVarGrpNull(final TA2lVariantGroup wpRespVarGrp, final A2lVariantGroup rvwVarGroup) {
    return CommonUtils.isNull(rvwVarGroup) || CommonUtils.isNull(wpRespVarGrp);
  }

  /**
   * @param tRvwWpResp
   * @param a2lWPRespModel
   * @param tA2lWPRespWPId
   * @param rvwWPId
   * @param rvwA2lRespId
   * @param wpRespId
   * @return
   * @throws DataException
   */
  private A2lWPRespModel getWPRespModelForCustomizedResp(final TRvwWpResp tRvwWpResp, A2lWPRespModel a2lWPRespModel,
      final Long tA2lWPRespWPId, final Long rvwWPId, final Long rvwA2lRespId, final Long wpRespId)
      throws DataException {
    Set<A2lWpParamMapping> a2lWPParamMappingSet =
        new A2lWpParamMappingLoader(getServiceData()).getWpParamMappingForWPResp(wpRespId);

    for (A2lWpParamMapping a2lWPParamMapping : a2lWPParamMappingSet) {
      Long parA2lRespId = a2lWPParamMapping.getParA2lRespId();
      if (CommonUtils.isNotNull(parA2lRespId) && CommonUtils.isEqual(parA2lRespId, rvwA2lRespId) &&
          CommonUtils.isEqual(rvwWPId, tA2lWPRespWPId)) {
        a2lWPRespModel = getWPRespModel(tRvwWpResp, wpRespId, false);
        break;
      }
    }
    return a2lWPRespModel;
  }

  /**
   * @param tRvwWpResp
   * @param ta2lWpResponsibility
   * @return
   */
  private A2lWPRespModel getWPRespModel(final TRvwWpResp tRvwWpResp, final Long wpRespId,
      final boolean isInheritedFlag) {

    A2lWPRespModel a2lWPRespModel = new A2lWPRespModel();
    a2lWPRespModel.setA2lRespId(tRvwWpResp.gettA2lResponsibility().getA2lRespId());
    a2lWPRespModel.setA2lWpId(tRvwWpResp.getTA2lWorkPackage().getA2lWpId());
    a2lWPRespModel.setWpRespId(wpRespId);
    a2lWPRespModel.setInheritedFlag(isInheritedFlag);

    return a2lWPRespModel;
  }

  /**
   * @param tPidcA2l
   * @throws IcdmException
   */
  private void createA2lDepParam(final TPidcA2l tPidcA2l) throws IcdmException {

    Map<String, List<String>> depParamsMap = CommonUtils.isNotNull(this.reviewInfo) ? this.reviewInfo.getDepParams()
        : new CDRResultParamBO().getDepParamMapForA2L(this.a2lFileInfo);
    Long a2lFileId = new PidcA2lLoader(getServiceData()).getDataObjectByID(tPidcA2l.getPidcA2lId()).getA2lFileId();
    List<TA2lDepParam> a2lDepParamList = new A2lDepParamLoader(getServiceData()).getByA2lFileId(a2lFileId);

    if (CommonUtils.isNotEmpty(depParamsMap) && CommonUtils.isNullOrEmpty(a2lDepParamList)) {
      for (Entry<String, List<String>> depParams : depParamsMap.entrySet()) {
        for (String depOnParamName : depParams.getValue()) {
          A2lDepParam a2lDepParam = new A2lDepParam();
          a2lDepParam.setParamName(depParams.getKey());
          a2lDepParam.setDependsOnParamName(depOnParamName);
          a2lDepParam.setA2lFileId(a2lFileId);

          A2lDepParamCommand a2lDepParamCmd = new A2lDepParamCommand(getServiceData(), a2lDepParam);
          executeChildCommand(a2lDepParamCmd);
        }
      }
    }
  }

  /**
   * Method to validate the the review type and review status
   *
   * @param entity
   * @return true, if Questionnaire is applicable <br>
   *         Questionnaires are applicable for official and start review only
   */
  private boolean isRvwQnaireApplicable(final TRvwResult entity) {
    return !entity.getRvwStatus().equals(CDRConstants.REVIEW_STATUS.OPEN.getDbType()) &&
        !entity.getReviewType().equals(CDRConstants.REVIEW_TYPE.TEST.getDbType());
  }


  /**
   * Create wp resp
   */
  private void createWpResp(final Long rvwResultId) throws IcdmException {
    if ((this.reviewInfo != null)) {
      Map<RvwWpAndRespModel, Long> rvwWpRespAndRvwWPRespIdMap = new HashMap<>();

      // Removed is empty condition. if empty for loop does not run
      if ((this.reviewInfo.getRvwWpAndRespModelSet() != null)) {
        for (RvwWpAndRespModel wpAndRespModel : this.reviewInfo.getRvwWpAndRespModelSet()) {
          RvwWpResp wpResp = new RvwWpResp();
          wpResp.setResultId(rvwResultId);
          wpResp.setA2lWpId(wpAndRespModel.getA2lWpId());
          wpResp.setA2lRespId(wpAndRespModel.getA2lRespId());
          RvwWpRespCommand wpRespCmd = new RvwWpRespCommand(getServiceData(), wpResp, false, false);
          executeChildCommand(wpRespCmd);
          rvwWpRespAndRvwWPRespIdMap.put(wpAndRespModel, wpRespCmd.getObjId());
        }
        this.reviewInfo.setRvwWpRespModelAndRvwWPRespIdMap(rvwWpRespAndRvwWPRespIdMap);
      }
    }
    // Removed is empty condition. if empty for loop does not run
    else if ((this.monicaReviewFileData != null) &&
        (this.monicaReviewFileData.getRvwMonicaWpAndRespModelSet() != null)) {
      for (RvwWpAndRespModel wpAndRespModel : this.monicaReviewFileData.getRvwMonicaWpAndRespModelSet()) {
        RvwWpResp wpResp = new RvwWpResp();
        wpResp.setResultId(rvwResultId);
        wpResp.setA2lWpId(wpAndRespModel.getA2lWpId());
        wpResp.setA2lRespId(wpAndRespModel.getA2lRespId());
        RvwWpRespCommand wpRespCmd = new RvwWpRespCommand(getServiceData(), wpResp, false, false);
        executeChildCommand(wpRespCmd);
        this.monicaRvwWPRespIdAndWPRespModelMap.put(wpRespCmd.getObjId(), wpAndRespModel);
      }
    }
  }

  /**
   * Method to create general Questionnaire Response
   *
   * @param tRvwResult as input
   * @throws IcdmException
   */
  private void createGeneralRvwQnairesResponse(final TRvwResult tRvwResult) throws IcdmException {
    if ((null != this.reviewInfo) && (null != this.reviewInfo.getRvwQnaireRespCreationModelSet())) {
      createRvwQuestionnairesResponse(tRvwResult, this.reviewInfo.getRvwQnaireRespCreationModelSet());
    }
  }

  private void createRvwQuestionnairesResponse(final TRvwResult tRvwResult,
      final Set<QnaireRespUpdationModel> rvwQnaireRespCreationModelSet)
      throws IcdmException {
    String qnaireRespStatus = null;
    for (QnaireRespUpdationModel rvwQnaireRespCreationModel : rvwQnaireRespCreationModelSet) {
      // for general question creation the status is calculated only once
      // QnaireVersId will be null for simplified General Qnaire
      if (CommonUtils.isNull(qnaireRespStatus) && CommonUtils.isNotNull(rvwQnaireRespCreationModel.getQnaireVersId())) {
        RvwQnaireResponseStatusHandler rvwQnaireResponseStatusHandler =
            new RvwQnaireResponseStatusHandler(getServiceData());
        qnaireRespStatus =
            rvwQnaireResponseStatusHandler.getQnaireRespStatus(rvwQnaireRespCreationModel.getPidcVersionId(),
                rvwQnaireRespCreationModel.getPidcVariantId(), rvwQnaireRespCreationModel.getQnaireVersId());
      }
      // qnaire resp creation model creation
      QnaireRespCreationModel qnaireRespCreationModel = new QnaireRespCreationModel();
      qnaireRespCreationModel.setPidcVersionId(rvwQnaireRespCreationModel.getPidcVersionId());
      qnaireRespCreationModel.setPidcVariantId(rvwQnaireRespCreationModel.getPidcVariantId());
      qnaireRespCreationModel.setQnaireVersId(rvwQnaireRespCreationModel.getQnaireVersId());
      qnaireRespCreationModel.setSelRespId(rvwQnaireRespCreationModel.getSelRespId());
      qnaireRespCreationModel.setSelWpId(rvwQnaireRespCreationModel.getSelWpId());

      RvwQnaireRespCreationCommand rvwQnaireRespUpdationCommand =
          new RvwQnaireRespCreationCommand(getServiceData(), qnaireRespCreationModel, qnaireRespStatus);
      executeChildCommand(rvwQnaireRespUpdationCommand);
    }
    // to undelete the deleted review questionnaire response
    // if the review result created for the first time under the variant
    int reviewResultCount = new CDRReviewResultLoader(getServiceData()).getOfficialOrStartReviewResultCount(tRvwResult);
    if (reviewResultCount == 1) {
      softDeleteUnDeleteRvwQnaireResponse(tRvwResult, false);
    }

  }

  /**
   * new Creation of Review Attr Value
   *
   * @param entity
   * @throws IcdmException
   */
  private void createRvwAttrValue(final TRvwResult entity) throws IcdmException {

    // Icdm-1805-changes for Not storing the Values
    // key - attr id
    ConcurrentMap<Long, RvwAttrValue> existingReviewAttrMap = new ConcurrentHashMap<>();
    RvwAttrValueLoader attrValLoader = new RvwAttrValueLoader(getServiceData());
    Map<Long, RvwAttrValue> attrValMap = attrValLoader.getByResultObj(entity);
    for (RvwAttrValue attrValue : attrValMap.values()) {

      existingReviewAttrMap.put(attrValue.getAttrId(), attrValue);
    }

    ConcurrentMap<Long, AttributeValueModel> newAttrvalMap = new ConcurrentHashMap<>();

    if (this.reviewInfo != null) {
      if (!CommonUtils.isNull(this.reviewInfo.getAttrValModel())) {
        for (AttributeValueModel attrValue : this.reviewInfo.getAttrValModel()) {
          newAttrvalMap.put(attrValue.getAttr().getId(), attrValue);
        }
      }
      if (CommonUtils.isNullOrEmpty(this.reviewInfo.getAttrValModel())) {
        // Delete Attr Values existing in DB but not in Current CDRReviewResult Data
        for (RvwAttrValue attrValue : existingReviewAttrMap.values()) {
          RvwAttrValueCommand attrValCmd = new RvwAttrValueCommand(getServiceData(), attrValue, false, true);
          executeChildCommand(attrValCmd);
        }
      }
      else {
        createDelRvwAttrModel(entity, existingReviewAttrMap, newAttrvalMap);
      }
    }
  }

  /**
   * @param entity
   * @param existingReviewAttrMap
   * @param newAttrvalMap
   * @throws IcdmException
   */
  private void createDelRvwAttrModel(final TRvwResult entity,
      final ConcurrentMap<Long, RvwAttrValue> existingReviewAttrMap,
      final ConcurrentMap<Long, AttributeValueModel> newAttrvalMap)
      throws IcdmException {

    // Delete Attr Values existing in DB but not in Current CDRReviewResult Data
    for (RvwAttrValue attrValue : existingReviewAttrMap.values()) {
      if (newAttrvalMap.get(attrValue.getAttrId()) == null) {
        RvwAttrValueCommand attrValCmd = new RvwAttrValueCommand(getServiceData(), attrValue, false, true);
        executeChildCommand(attrValCmd);
      }
    }
    // Add newly added Attr values if any during cancel operation
    for (AttributeValueModel attrValModel : this.reviewInfo.getAttrValModel()) {
      if ((existingReviewAttrMap.get(attrValModel.getAttr().getId()) == null) && (attrValModel.getValue() != null)) {

        RvwAttrValue attrVal = new RvwAttrValue();
        attrVal.setResultId(entity.getResultId());
        attrVal.setAttrId(attrValModel.getAttr().getId());
        attrVal.setValueId(attrValModel.getValue().getId());
        if (attrValModel.getValue().getName().equals(ApicConstants.USED)) {
          attrVal.setUsed(ApicConstants.CODE_YES);
        }
        else {
          attrVal.setUsed(ApicConstants.CODE_NO);
        }
        attrVal.setName(attrValModel.getAttr().getName());
        RvwAttrValueCommand attrValCmd = new RvwAttrValueCommand(getServiceData(), attrVal, false, false);
        executeChildCommand(attrValCmd);
      }

    }
  }

  /**
   * @param rvwResultId
   * @throws IcdmException
   */
  private void createOutputFilesForCompliParams(final Long rvwResultId) throws IcdmException {

    if (this.reviewInfo != null) {
      if ((this.reviewInfo.getCompliData() == null) ||
          (this.reviewInfo.getCompliData().getCompliCheckSSDOutputFiles() == null)) {
        return;
      }
      for (String outputFilePath : this.reviewInfo.getCompliData().getCompliCheckSSDOutputFiles()) {
        int size = 0;
        try (FileInputStream inputStream = new FileInputStream(new File(outputFilePath));) {
          // Modified from url.openStream() to handle special characters in filename
          size = inputStream.available();
          if (size != 0) {
            inserRvwFileCommand(null, outputFilePath, REVIEW_FILE_TYPE.OUTPUT, rvwResultId);
          }
        }
        catch (IOException exp) {
          throw new IcdmException(exp.getMessage(), exp);
        }
      }
    }
  }

  /**
   * @param rvwResultId
   * @throws IcdmException
   */
  private void createOutputFiles(final Long rvwResultId) throws IcdmException {

    Set<String> outputFiles = new HashSet<>();
    if (this.reviewInfo != null) {
      if ((this.reviewInfo.getCompliData() != null) && (this.reviewInfo.getCompliData().getSsdErrorPath() != null)) {
        outputFiles.add(this.reviewInfo.getCompliData().getSsdErrorPath());
      }

      if (this.reviewInfo.getGeneratedCheckSSDFiles() != null) {
        outputFiles.addAll(this.reviewInfo.getGeneratedCheckSSDFiles());
      }
      if (outputFiles.isEmpty()) {
        return;
      }
      for (String outputFilePath : outputFiles) {
        File file = new File(outputFilePath);
        if (file.length() != 0) {
          inserRvwFileCommand(null, outputFilePath, REVIEW_FILE_TYPE.OUTPUT, rvwResultId);
        }
      }
    }
  }

  /**
   * @param rvwResultId
   * @throws IcdmException
   */
  private void createRuleFile(final Long rvwResultId) throws IcdmException {
    if (this.reviewInfo != null) {

      if (this.reviewInfo.getPrimarySSDFilePath() != null) {
        inserRvwFileCommand(null, this.reviewInfo.getPrimarySSDFilePath(), REVIEW_FILE_TYPE.RULE, rvwResultId);
      }
      if ((this.reviewInfo.getCompliData() != null) &&
          (this.reviewInfo.getCompliData().getCompliSSDFilePath() != null)) {
        inserRvwFileCommand(null, this.reviewInfo.getCompliData().getCompliSSDFilePath(), REVIEW_FILE_TYPE.RULE,
            rvwResultId);
      }

      if (this.reviewInfo.getSecRuleSetDataList() != null) {
        for (ReviewRuleSetData ruleSetData : this.reviewInfo.getSecRuleSetDataList()) {
          String ssdFilePath = ruleSetData.getSsdFilePath();
          if (ssdFilePath != null) {
            inserRvwFileCommand(this.reviewInfo.getFilesStreamMap().get(ssdFilePath), ssdFilePath,
                REVIEW_FILE_TYPE.RULE, rvwResultId);
          }

        }

      }

    }


  }

  /**
   * Command Changes for creating Lab\Fun files create the Fun /lab files
   *
   * @param rvwResultId
   * @throws IcdmException
   */
  private void createFunLabFiles(final Long rvwResultId) throws IcdmException {
    if (this.reviewInputData != null) {
      String funLabFilePath = this.reviewInputData.getFileData().getFunLabFilePath();
      if (funLabFilePath != null) {
        // changed contains to end with
        if (funLabFilePath.endsWith(".fun")) {
          inserRvwFileCommand(this.reviewInfo.getFilesStreamMap().get(funLabFilePath), funLabFilePath,
              REVIEW_FILE_TYPE.FUNCTION_FILE, rvwResultId);
        }
        // changed contains to end with
        else if (funLabFilePath.endsWith(".lab")) {
          inserRvwFileCommand(this.reviewInfo.getFilesStreamMap().get(funLabFilePath), funLabFilePath,
              REVIEW_FILE_TYPE.LAB_FILE, rvwResultId);
        }
      }
    }
  }

  /**
   * @throws IcdmException
   */
  private void createSecRvwParams(final TRvwResult resultEntity) throws IcdmException {
    if ((this.reviewInfo != null) && (this.reviewInputData != null)) {
      RvwParametersSecondaryCmdDataCreator cmdData = new RvwParametersSecondaryCmdDataCreator(this.reviewInfo,
          this.reviewInputData, resultEntity, getServiceData(), this.ruleDataMap);
      SortedSet<RvwParametersSecondary> secondaryParams = cmdData.createSecRvwParams();
      for (RvwParametersSecondary secondaryParam : secondaryParams) {
        RvwParametersSecondaryCommand secParamCmd =
            new RvwParametersSecondaryCommand(getServiceData(), secondaryParam, false, false);
        executeChildCommand(secParamCmd);
      }
    }


  }

  /**
   * @param rvwResultId
   * @throws IcdmException
   */
  private void createSecondaryResult(final Long rvwResultId) throws IcdmException {
    if ((this.reviewInfo != null) && (this.reviewInfo.getSecRuleSetDataList() != null)) {

      for (ReviewRuleSetData ruleSetData : this.reviewInfo.getSecRuleSetDataList()) {
        RvwResultsSecondary secondaryResult = new RvwResultsSecondary();
        secondaryResult.setResultId(rvwResultId);
        secondaryResult.setRsetId(ruleSetData.getRuleSet() == null ? null : ruleSetData.getRuleSet().getId());
        secondaryResult.setSource(ruleSetData.getSource().getDbVal());
        secondaryResult.setSsdReleaseId(ruleSetData.getSsdReleaseID());
        secondaryResult.setSsdVersionId(ruleSetData.getSsdVersionID());
        RvwResultsSecondaryCommand secResultCmd =
            new RvwResultsSecondaryCommand(getServiceData(), secondaryResult, false, false);
        executeChildCommand(secResultCmd);
        this.ruleDataMap.put(ruleSetData, secResultCmd.getNewData());
      }
    }
  }

  /**
   * @return
   * @throws IcdmException
   */
  private TRvwResult createRvwResult() throws IcdmException {
    Long pidcVersId =
        new PidcA2lLoader(getServiceData()).getDataObjectByID(getInputData().getPidcA2lId()).getPidcVersId();
    boolean isSimpQuesEnabled = CDRReviewResultUtil.isSimpQuesEnabled(getServiceData(), pidcVersId);
    boolean isOBDOptionEnabled = CDRReviewResultUtil.isOBDOptionEnabled(getServiceData(), pidcVersId);

    TRvwResult resultEntity = new TRvwResult();
    TPidcA2l tPidcA2l = new PidcA2lLoader(getServiceData()).getEntityObject(getInputData().getPidcA2lId());
    if (tPidcA2l.getTRvwResults() != null) {
      tPidcA2l.getTRvwResults().add(resultEntity);
    }
    resultEntity.setTPidcA2l(tPidcA2l);
    if (null != getInputData().getRsetId()) {
      resultEntity.settRuleSet(new RuleSetLoader(getServiceData()).getEntityObject(getInputData().getRsetId()));
    }


    resultEntity.setGrpWorkPkg(getInputData().getGrpWorkPkg());
    resultEntity.setDescription(getInputData().getDescription());
    resultEntity.setRvwStatus(getInputData().getRvwStatus());
    if (null != getInputData().getWpDefnVersId()) {
      resultEntity.settA2lWpDefnVersion(
          new A2lWpDefnVersionLoader(getServiceData()).getEntityObject(getInputData().getWpDefnVersId()));
    }
    if (null != getInputData().getOrgResultId()) {
      resultEntity
          .setTRvwResult(new CDRReviewResultLoader(getServiceData()).getEntityObject(getInputData().getOrgResultId()));
    }
    resultEntity.setDeltaReviewType(getInputData().getDeltaReviewType());
    resultEntity.setSourceType(getInputData().getSourceType());
    resultEntity.setReviewType(getInputData().getReviewType());

    resultEntity.setLockStatus(getInputData().getLockStatus());
    resultEntity.settA2lWpDefnVersion(
        new A2lWpDefnVersionLoader(getServiceData()).getEntityObject(getInputData().getWpDefnVersId()));
    resultEntity.setComments(getInputData().getComments());

    setOBDAndSimpQnaireCol(resultEntity, isSimpQuesEnabled, isOBDOptionEnabled);

    setUserDetails(COMMAND_MODE.CREATE, resultEntity);

    persistEntity(resultEntity);
    return resultEntity;
  }


  /**
   * {@inheritDoc}
   */


  @Override
  protected void update() throws IcdmException {
    CDRReviewResultLoader loader = new CDRReviewResultLoader(getServiceData());
    TRvwResult resultEntity = updateResult(loader);
    Set<TRvwVariant> rvwVariants = resultEntity.getTRvwVariants();
    if ((rvwVariants == null) || rvwVariants.isEmpty()) {
      if (getInputData().getPrimaryVariantId() != null) {
        RvwVariant rvVariant = new RvwVariant();
        rvVariant.setResultId(resultEntity.getResultId());
        rvVariant.setVariantId(getInputData().getPrimaryVariantId());
        RvwVariantCommand rvwVariantCommand = new RvwVariantCommand(getServiceData(), rvVariant, false, false);
        executeChildCommand(rvwVariantCommand);
        this.rvwVariant = rvwVariantCommand.getNewData();
      }
    }
    else {
      TRvwVariant tRvwVariant = rvwVariants.iterator().next();
      long pidcVarId = tRvwVariant.getTabvProjectVariant().getVariantId();
      long rvwVarId = tRvwVariant.getRvwVarId();
      // case when we update the variant of the cancelled review and execute , only one variant would be available
      if ((rvwVariants.size() == 1) && CommonUtils.isNotNull(getInputData().getPrimaryVariantId()) &&
          (pidcVarId != getInputData().getPrimaryVariantId())) {

        RvwVariant rvVariant = new RvwVariantLoader(getServiceData()).getDataObjectByID(rvwVarId);
        rvVariant.setVariantId(getInputData().getPrimaryVariantId());
        RvwVariantCommand rvwVariantCommand = new RvwVariantCommand(getServiceData(), rvVariant, true, false);
        executeChildCommand(rvwVariantCommand);
        this.rvwVariant = rvwVariantCommand.getNewData();
      }
      else {
        this.rvwVariant = new RvwVariantLoader(getServiceData()).getDataObjectByID(rvwVarId);
      }
    }
    // if only review type , lock status , desc (in result editor) to be updated no need to call other updates
    if (this.reviewInfo != null) {
      deleteSecondaryResult(resultEntity);
      createSecondaryResult(resultEntity.getResultId());
      updateWpResp(resultEntity);
      updateRvwFunctions(resultEntity);
      RvwFileLoader rvwFileLoader = new RvwFileLoader(getServiceData());
      Map<String, List<RvwFile>> rvwFilesMap = rvwFileLoader.getFiles(resultEntity);

      updateInputFiles(resultEntity, rvwFilesMap);
      updateFunLabFiles(resultEntity, rvwFilesMap);
      updatePartipants(resultEntity);

      // create a2l dependent parameters
      createA2lDepParam(resultEntity.getTPidcA2l());

      createReviewParams(resultEntity);

      createSecRvwParams(resultEntity);

      createRuleFile(resultEntity.getResultId());
      createOutputFiles(resultEntity.getResultId());
      createOutputFilesForCompliParams(resultEntity.getResultId());// ICDM-2440
      createRvwAttrValue(resultEntity);

      if (isRvwQnaireApplicable(resultEntity)) {
        createGeneralRvwQnairesResponse(resultEntity);
      }
      checkForValidDeltaReview(resultEntity);
    }

  }

  /**
   * @param resultEntity
   * @throws IcdmException
   */
  private void updateWpResp(final TRvwResult resultEntity) throws IcdmException {
    RvwWpRespLoader wpRspLoader = new RvwWpRespLoader(getServiceData());

    Set<RvwWpAndRespModel> wpAndRespModelSet = this.reviewInfo.getRvwWpAndRespModelSet();
    if ((wpAndRespModelSet != null) && !wpAndRespModelSet.isEmpty()) {
      Map<Long, RvwWpResp> existingWpRespMap = wpRspLoader.getByResultObj(resultEntity);
      for (RvwWpAndRespModel wpAndRespModel : wpAndRespModelSet) {
        // create new mappings
        if (!existingWpRespMap.containsKey(wpAndRespModel.getA2lWpId())) {
          RvwWpResp wpResp = new RvwWpResp();
          wpResp.setResultId(resultEntity.getResultId());
          wpResp.setA2lWpId(wpAndRespModel.getA2lWpId());
          wpResp.setA2lRespId(wpAndRespModel.getA2lRespId());
          RvwWpRespCommand wpRespCmd = new RvwWpRespCommand(getServiceData(), wpResp, false, false);
          executeChildCommand(wpRespCmd);
          existingWpRespMap.remove(wpAndRespModel.getA2lWpId());
          if (null != this.reviewInfo.getRvwWpRespModelAndRvwWPRespIdMap()) {
            this.reviewInfo.getRvwWpRespModelAndRvwWPRespIdMap().put(wpAndRespModel, wpRespCmd.getObjId());
          }
          else {
            Map<RvwWpAndRespModel, Long> newRvwWpRespMap = new HashMap<>();
            newRvwWpRespMap.put(wpAndRespModel, wpRespCmd.getObjId());
            this.reviewInfo.setRvwWpRespModelAndRvwWPRespIdMap(newRvwWpRespMap);
          }
        }
      }
      // delete the existing wp's
      for (RvwWpResp wpResp : existingWpRespMap.values()) {
        RvwWpRespCommand wpRespCmd = new RvwWpRespCommand(getServiceData(), wpResp, false, true);
        executeChildCommand(wpRespCmd);
      }
    }
  }


  /**
   * ICDM-2579 check if the delta review is valid and make the necessary changes
   *
   * @param dbRvwResult
   */
  private void checkForValidDeltaReview(final TRvwResult dbRvwResult) {

    if ((this.reviewInputData != null) && this.reviewInputData.isFinish() &&
        (null != this.reviewInputData.getDeltaReviewType()) && !this.cdrResParamCreator.isDeltaReviewValid()) {
      // if it is a delta review but not valid
      // set the parent result id to null
      dbRvwResult.setTRvwResult(null);
      // ICDM-609
      if (this.reviewInputData.getResultData().getParentResultId() != null) {
        // Newly Added for associating the Parent and the child Review
        final TRvwResult parentDb = new CDRReviewResultLoader(getServiceData())
            .getEntityObject(this.reviewInputData.getResultData().getParentResultId());
        Set<TRvwResult> tRvwResults = parentDb.getTRvwResults();
        if (null != tRvwResults) {
          tRvwResults.remove(dbRvwResult);
        }
      }
      // set the delta review type to null
      dbRvwResult.setDeltaReviewType(null);
    }
  }

  /**
   * @throws IcdmException
   */
  private void updatePartipants(final TRvwResult resultEntity) throws IcdmException {
    RvwParticipantLoader participantsLoader = new RvwParticipantLoader(getServiceData());
    Map<Long, RvwParticipant> rvwParticipantsMap = participantsLoader.getByResultObj(resultEntity);
    ConcurrentMap<CDRConstants.REVIEW_USER_TYPE, Map<Long, RvwParticipant>> existingCdrParticipantsMap =
        new ConcurrentHashMap<>();
    UserLoader userLoader = new UserLoader(getServiceData());
    for (RvwParticipant cdrParticipant : rvwParticipantsMap.values()) {
      Map<Long, RvwParticipant> participantType =
          existingCdrParticipantsMap.get(CDRConstants.REVIEW_USER_TYPE.getType(cdrParticipant.getActivityType()));

      if (participantType == null) {
        participantType = new HashMap<>();
        existingCdrParticipantsMap.put(CDRConstants.REVIEW_USER_TYPE.getType(cdrParticipant.getActivityType()),
            participantType);
      }
      participantType.put(cdrParticipant.getUserId(), cdrParticipant);
    }
    // Participants
    if (this.reviewInputData.getUserData().getSelParticipantsIds() != null) {

      addRemoveAddlParticipants(resultEntity, existingCdrParticipantsMap, userLoader);

    }

    // Auditor Update existing ApicUser with new User if user is changed during cancel operation
    if (this.reviewInputData.getUserData().getSelAuditorId() != null) {
      createUpdateAuditorDetails(resultEntity, existingCdrParticipantsMap, userLoader);
    }

    // Calibration Engineer name Update existing ApicUser with new User if user is changed during cancel operation
    if (this.reviewInputData.getUserData().getSelCalEngineerId() != null) {
      createUpdateCalEngineerDetails(resultEntity, existingCdrParticipantsMap, userLoader);
    }
  }

  /**
   * @param resultEntity
   * @param existingCdrParticipantsMap
   * @param userLoader
   * @throws IcdmException
   */
  private void createUpdateCalEngineerDetails(final TRvwResult resultEntity,
      final ConcurrentMap<CDRConstants.REVIEW_USER_TYPE, Map<Long, RvwParticipant>> existingCdrParticipantsMap,
      final UserLoader userLoader)
      throws IcdmException {
    Map<Long, RvwParticipant> calMap = existingCdrParticipantsMap.get(CDRConstants.REVIEW_USER_TYPE.CAL_ENGINEER);
    User newCalEngineer = userLoader.getDataObjectByID(this.reviewInputData.getUserData().getSelCalEngineerId());
    if ((calMap == null) || calMap.isEmpty()) {

      createTParticipants(CDRConstants.REVIEW_USER_TYPE.CAL_ENGINEER.getDbType(), resultEntity.getResultId(),
          newCalEngineer);
    }
    else if (!calMap.containsKey(this.reviewInputData.getUserData().getSelCalEngineerId())) {
      Long existingCalId = calMap.keySet().iterator().next();
      RvwParticipant existingUser = calMap.get(existingCalId);
      existingUser.setName(newCalEngineer.getName());
      existingUser.setUserId(newCalEngineer.getId());
      RvwParticipantCommand participantCalEngCommand =
          new RvwParticipantCommand(getServiceData(), existingUser, true, false);
      executeChildCommand(participantCalEngCommand);
    }
  }

  /**
   * @param resultEntity
   * @param existingCdrParticipantsMap
   * @param userLoader
   * @throws IcdmException
   */
  private void createUpdateAuditorDetails(final TRvwResult resultEntity,
      final ConcurrentMap<CDRConstants.REVIEW_USER_TYPE, Map<Long, RvwParticipant>> existingCdrParticipantsMap,
      final UserLoader userLoader)
      throws IcdmException {
    Map<Long, RvwParticipant> auditorMap = existingCdrParticipantsMap.get(CDRConstants.REVIEW_USER_TYPE.AUDITOR);
    User newAuditor = userLoader.getDataObjectByID(this.reviewInputData.getUserData().getSelAuditorId());
    if ((auditorMap == null) || auditorMap.isEmpty()) {

      createTParticipants(CDRConstants.REVIEW_USER_TYPE.AUDITOR.getDbType(), resultEntity.getResultId(), newAuditor);
    }
    else if (!auditorMap.containsKey(this.reviewInputData.getUserData().getSelAuditorId())) {
      Long existingAuditorId = auditorMap.keySet().iterator().next();
      RvwParticipant existingUser = auditorMap.get(existingAuditorId);
      existingUser.setName(newAuditor.getName());
      existingUser.setUserId(newAuditor.getId());
      RvwParticipantCommand participantCalEngCommand =
          new RvwParticipantCommand(getServiceData(), existingUser, true, false);
      executeChildCommand(participantCalEngCommand);
    }
  }

  /**
   * @param resultEntity
   * @param existingCdrParticipantsMap
   * @param userLoader
   * @throws DataException
   * @throws IcdmException
   */
  private void addRemoveAddlParticipants(final TRvwResult resultEntity,
      final ConcurrentMap<CDRConstants.REVIEW_USER_TYPE, Map<Long, RvwParticipant>> existingCdrParticipantsMap,
      final UserLoader userLoader)
      throws IcdmException {
    Map<Long, RvwParticipant> addParticipants =
        existingCdrParticipantsMap.get(CDRConstants.REVIEW_USER_TYPE.ADDL_PARTICIPANT);
    ConcurrentMap<Long, User> selPartcipntsMap = new ConcurrentHashMap<>();

    // Add new participants
    for (Long userId : this.reviewInputData.getUserData().getSelParticipantsIds()) {
      User user = userLoader.getDataObjectByID(userId);
      if ((addParticipants == null) || (addParticipants.get(userId) == null)) {
        createTParticipants(CDRConstants.REVIEW_USER_TYPE.ADDL_PARTICIPANT.getDbType(), resultEntity.getResultId(),
            user);

      }
      selPartcipntsMap.put(userId, user);
    }

    // Delete invalid participants
    if (addParticipants != null) {
      for (Entry<Long, RvwParticipant> exstingPartcipntEntry : addParticipants.entrySet()) {
        if (selPartcipntsMap.get(exstingPartcipntEntry.getKey()) == null) {
          // ApicUser null since it is not required during delete
          RvwParticipantCommand participantCalEngCommand =
              new RvwParticipantCommand(getServiceData(), exstingPartcipntEntry.getValue(), false, true);
          executeChildCommand(participantCalEngCommand);
        }
      }
    }

  }

  /**
   * CommandException- In case of parallel changes detected icdm-943
   *
   * @param rvwFilesMap
   * @throws IcdmException
   */
  private void updateFunLabFiles(final TRvwResult resultEntity, final Map<String, List<RvwFile>> rvwFilesMap)
      throws IcdmException {

    SortedSet<RvwFile> existingFunLabFiles = new TreeSet<>();
    List<RvwFile> labFileList = rvwFilesMap.get(CDRConstants.REVIEW_FILE_TYPE.LAB_FILE.getDbType());
    if (labFileList != null) {
      existingFunLabFiles.addAll(labFileList);
    }
    List<RvwFile> funFileList = rvwFilesMap.get(CDRConstants.REVIEW_FILE_TYPE.FUNCTION_FILE.getDbType());
    if (funFileList != null) {
      existingFunLabFiles.addAll(funFileList);
    }
    ConcurrentMap<String, RvwFile> existingLabFunFilesMap = new ConcurrentHashMap<>();
    for (RvwFile icdmFile : existingFunLabFiles) {
      existingLabFunFilesMap.put(icdmFile.getName(), icdmFile);
    }

    // Add newly added Fun lab files if any during cancel operation
    String funLabFilePath = this.reviewInputData.getFileData().getFunLabFilePath();
    if (funLabFilePath != null) {
      final File file = new File(funLabFilePath);
      final String fileName = file.getName();
      if (existingLabFunFilesMap.get(fileName) == null) {
        // changed contains to end with
        if (funLabFilePath.endsWith(".fun")) {
          inserRvwFileCommand(this.reviewInfo.getFilesStreamMap().get(funLabFilePath), funLabFilePath,
              REVIEW_FILE_TYPE.FUNCTION_FILE, resultEntity.getResultId());
        }
        // changed contains to end with
        else if (funLabFilePath.endsWith(".lab")) {
          inserRvwFileCommand(this.reviewInfo.getFilesStreamMap().get(funLabFilePath), funLabFilePath,
              REVIEW_FILE_TYPE.LAB_FILE, resultEntity.getResultId());
        }
      }
    }


    // Delete Lab Fun Files existing in DB but not in Current CDRReviewResult Data
    for (Entry<String, RvwFile> existingInputFilesMapEntry : existingLabFunFilesMap.entrySet()) {
      if (((funLabFilePath == null) || funLabFilePath.isEmpty()) ||
          (!funLabFilePath.contains(existingInputFilesMapEntry.getKey()))) {
        RvwFileCommand rvwInputFileCommand =
            new RvwFileCommand(getServiceData(), existingInputFilesMapEntry.getValue(), false, true);
        executeChildCommand(rvwInputFileCommand);
      }
    }

  }

  /**
   * @param resultEntity
   * @throws IcdmException
   */
  private void updateRvwFunctions(final TRvwResult resultEntity) throws IcdmException {
    CDRResultFunctionLoader resultFunLoader = new CDRResultFunctionLoader(getServiceData());

    Map<Long, CDRResultFunction> resultFuncMap = resultFunLoader.getByResultObj(resultEntity);

    ConcurrentMap<Long, CDRResultFunction> existingCdrResultFunctionMap = new ConcurrentHashMap<>();


    for (CDRResultFunction cdrResultFunction : resultFuncMap.values()) {
      existingCdrResultFunctionMap.put(cdrResultFunction.getFunctionId(), cdrResultFunction);
    }

    if (CommonUtils.isNullOrEmpty(this.reviewInfo.getCdrFunctionsList())) {
      // Delete functions existing in DB but not in Current CDRReviewResult Data
      for (CDRResultFunction existingCdrFunctionEntry : existingCdrResultFunctionMap.values()) {
        CDRResultFunctionCommand resultFunctionCommand =
            new CDRResultFunctionCommand(getServiceData(), existingCdrFunctionEntry, false, true);
        executeChildCommand(resultFunctionCommand);
      }
    }
    else {
      SortedSet<Long> newFuncsIdSet = new TreeSet<>();
      // Add newly added Functions if any during cancel operation
      for (Function cdrFunction : this.reviewInfo.getCdrFunctionsList()) {
        newFuncsIdSet.add(cdrFunction.getId());
        if (existingCdrResultFunctionMap.get(cdrFunction.getId()) == null) {
          CDRResultFunction cdrResultFunction = new CDRResultFunction();
          cdrResultFunction.setFunctionId(cdrFunction.getId());
          cdrResultFunction.setResultId(resultEntity.getResultId());

          setFunctionVersion(cdrFunction, cdrResultFunction);

          CDRResultFunctionCommand resultFunctionCommand =
              new CDRResultFunctionCommand(getServiceData(), cdrResultFunction, false, false);
          executeChildCommand(resultFunctionCommand);
        }
      }

      // Delete functions existing in DB but not in Current CDRReviewResult Data
      for (Entry<Long, CDRResultFunction> existingCdrFunctionEntry : existingCdrResultFunctionMap.entrySet()) {
        if (!newFuncsIdSet.contains(existingCdrFunctionEntry.getKey())) {

          CDRResultFunctionCommand resultFunctionCommand =
              new CDRResultFunctionCommand(getServiceData(), existingCdrFunctionEntry.getValue(), false, true);
          executeChildCommand(resultFunctionCommand);
        }
      }
    }
  }

  /**
   * @param cdrFunction
   * @param cdrResultFunction
   */
  private void setFunctionVersion(final Function cdrFunction, final CDRResultFunction cdrResultFunction) {
    // If the function is not available in the new A2l file. then the all module map will not have it.
    if (isModuleFuncAvailable() &&
        (this.reviewInfo.getA2lFileContents().getAllModulesFunctions().get(cdrFunction.getName()) != null)) {
      String funcVer =
          this.reviewInfo.getA2lFileContents().getAllModulesFunctions().get(cdrFunction.getName()).getFunctionVersion();
      cdrResultFunction.setFunctionVers(funcVer);
    }
  }

  private boolean isModuleFuncAvailable() {
    return (this.reviewInfo.getA2lFileContents() != null) &&
        (this.reviewInfo.getA2lFileContents().getAllModulesFunctions() != null);
  }

  /**
   * @param rvwFilesMap
   * @throws IcdmException
   */
  private void updateInputFiles(final TRvwResult resultEntity, final Map<String, List<RvwFile>> rvwFilesMap)
      throws IcdmException {

    List<RvwFile> fileList = rvwFilesMap.get(CDRConstants.REVIEW_FILE_TYPE.INPUT.getDbType());
    ConcurrentMap<String, RvwFile> existingInputFilesMap = new ConcurrentHashMap<>();
    if (fileList != null) {
      for (RvwFile icdmFile : fileList) {
        existingInputFilesMap.put(icdmFile.getName(), icdmFile);
      }
    }
    SortedSet<String> newFileNames = new TreeSet<>();
    // Add newly added Input Files if any during cancel operation
    if (this.reviewInputData.getFileData().getSelFilesPath() != null) {
      for (String inputFilePath : this.reviewInputData.getFileData().getSelFilesPath()) {
        String fileName = inputFilePath.substring(inputFilePath.lastIndexOf('/') + 1);
        newFileNames.add(fileName);
        if (existingInputFilesMap.get(fileName) == null) {
          inserRvwFileCommand(this.reviewInfo.getFilesStreamMap().get(inputFilePath), inputFilePath,
              REVIEW_FILE_TYPE.INPUT, resultEntity.getResultId());
        }
        else {
          this.filesCreatedMap.put(inputFilePath.toUpperCase(Locale.getDefault()), existingInputFilesMap.get(fileName));
        }
      }

      // Delete Input Files existing in DB but not in Current CDRReviewResult Data
      for (Entry<String, RvwFile> existingInputFilesMapEntry : existingInputFilesMap.entrySet()) {
        if (!newFileNames.contains(existingInputFilesMapEntry.getKey())) {
          RvwFileCommand rvwInputFileCommand =
              new RvwFileCommand(getServiceData(), existingInputFilesMapEntry.getValue(), false, true);
          executeChildCommand(rvwInputFileCommand);

        }
      }
    }
  }


  /**
   * @param resultEntity
   * @throws IcdmException
   */
  private void deleteSecondaryResult(final TRvwResult resultEntity) throws IcdmException {

    RvwResultsSecondaryLoader secResultsLoader = new RvwResultsSecondaryLoader(getServiceData());
    Map<Long, RvwResultsSecondary> secResultsMap = secResultsLoader.getByResultObj(resultEntity);
    if (secResultsMap != null) {
      for (RvwResultsSecondary secResult : secResultsMap.values()) {
        RvwResultsSecondaryCommand cmdSecResult =
            new RvwResultsSecondaryCommand(getServiceData(), secResult, false, true);
        executeChildCommand(cmdSecResult);
        getServiceData().getEntMgr().flush();
      }
    }
  }

  /**
   * Update the status of the review (Open, In-Progress, Closed)
   *
   * @throws IcdmException IcdmException
   */
  private void updateCDRReviewStatus(final TRvwResult resultEntity) throws IcdmException {

    // ICDM-1746
    if (CommonUtils.isEqual(resultEntity.getRvwStatus(), REVIEW_STATUS.OPEN.getDbType()) &&
        (CommonUtils.isEqual(getInputData().getRvwStatus(), REVIEW_STATUS.IN_PROGRESS.getDbType()) ||
            CommonUtils.isEqual(getInputData().getRvwStatus(), REVIEW_STATUS.CLOSED.getDbType()))) {
      // change the created user to the current user if he finishes the review
      resultEntity.setCreatedUser(getServiceData().getUsername());
    }
    if (!getInputData().getRvwStatus().equals(CDRConstants.REVIEW_STATUS.OPEN.getDbType())) {
      Collection<CDRResultParameter> params =
          new CDRResultParameterLoader(getServiceData()).getByResultObj(resultEntity).values();

      if (startTestReview(params) || officialReview(params)) {
        resultEntity.setRvwStatus(CDRConstants.REVIEW_STATUS.CLOSED.getDbType());
      }
      else {
        resultEntity.setRvwStatus(CDRConstants.REVIEW_STATUS.IN_PROGRESS.getDbType());
      }
    }
  }

  /**
   * @param params
   * @return
   */
  private boolean officialReview(final Collection<CDRResultParameter> params) {
    // Official review should be closed if all params score is 8 or 9
    return getInputData().getReviewType().equals(CDRConstants.REVIEW_TYPE.OFFICIAL.getDbType()) &&
        isAllParamsChecked(params);
  }

  /**
   * @param params
   * @return
   */
  private boolean startTestReview(final Collection<CDRResultParameter> params) {
    // Start and test review should be closed if all params score is greater than 0
    return (getInputData().getReviewType().equals(CDRConstants.REVIEW_TYPE.START.getDbType()) ||
        getInputData().getReviewType().equals(CDRConstants.REVIEW_TYPE.TEST.getDbType())) &&
        isParamScoreGreaterThanZero(params);
  }

  /**
   * @return
   */
  private boolean isParamScoreGreaterThanZero(final Collection<CDRResultParameter> params) {
    // check if all params are reviewed
    if (params.isEmpty()) {
      return false;
    }
    for (CDRResultParameter param : params) {
      if (DATA_REVIEW_SCORE.getType(param.getReviewScore()) == DATA_REVIEW_SCORE.S_0) {
        return false;
      }
    }
    return true;
  }

  /**
   * @param loader
   * @throws IcdmException
   */
  private TRvwResult updateResult(final CDRReviewResultLoader loader) throws IcdmException {
    Long pidcVersId =
        new PidcA2lLoader(getServiceData()).getDataObjectByID(getInputData().getPidcA2lId()).getPidcVersId();
    boolean isSimpQuesEnabled = CDRReviewResultUtil.isSimpQuesEnabled(getServiceData(), pidcVersId);
    boolean isOBDOptionEnabled = CDRReviewResultUtil.isOBDOptionEnabled(getServiceData(), pidcVersId);

    TRvwResult resultEntity = loader.getEntityObject(getInputData().getId());
//The following condition has been commented - to delete a parent review result, the parent reference in child object should be set to null for Delete Review Result under Admin
//    if (null != getInputData().getOrgResultId()) {
    resultEntity
        .setTRvwResult(new CDRReviewResultLoader(getServiceData()).getEntityObject(getInputData().getOrgResultId()));
    // }
    resultEntity.setDescription(getInputData().getDescription());
    if (getInputData().getSourceType() == null) {
      resultEntity.setSourceType(CDRConstants.CDR_SOURCE_TYPE.NOT_DEFINED.getDbType());
    }
    else {
      resultEntity.setSourceType(getInputData().getSourceType());
    }

    resultEntity.setReviewType(getInputData().getReviewType());
    if (null != getInputData().getRsetId()) {
      resultEntity.settRuleSet(new RuleSetLoader(getServiceData()).getEntityObject(getInputData().getRsetId()));
    }
    if (null != getInputData().getPidcA2lId()) {
      resultEntity.setTPidcA2l(new PidcA2lLoader(getServiceData()).getEntityObject(getInputData().getPidcA2lId()));
    }
    resultEntity.setLockStatus(getInputData().getLockStatus());

    resultEntity.setDeltaReviewType(getInputData().getDeltaReviewType());

    resultEntity.setGrpWorkPkg(getInputData().getGrpWorkPkg());

    if (CommonUtils.isEqual(getInputData().getRvwStatus(), REVIEW_STATUS.OPEN.getDbType()) &&
        ((CommonUtils.isEqual(getInputData().getRvwStatus(), REVIEW_STATUS.IN_PROGRESS.getDbType())) ||
            CommonUtils.isEqual(getInputData().getRvwStatus(), REVIEW_STATUS.CLOSED.getDbType()))) {
      // change the created user to the current user if he finishes the review
      resultEntity.setCreatedUser(getServiceData().getUsername());
    }
    resultEntity.setComments(getInputData().getComments());

    setOBDAndSimpQnaireCol(resultEntity, isSimpQuesEnabled, isOBDOptionEnabled);

    updateCDRReviewStatus(resultEntity);

    setUserDetails(COMMAND_MODE.UPDATE, resultEntity);
    return resultEntity;
  }

  /**
   * @param resultEntity
   * @param isSimpQues
   * @param isOBDOptionEnabled
   * @throws IcdmException
   */
  private void setOBDAndSimpQnaireCol(final TRvwResult resultEntity, final boolean isSimpQues,
      final boolean isOBDOptionEnabled) {

    if (CommonUtils.isNotEqual(getInputData().getReviewType(), CDRConstants.REVIEW_TYPE.TEST.getDbType())) {
      String obdFlag = getInputData().getObdFlag();
      resultEntity.setObdFlag(obdFlag);

      if (CommonUtils.isNotEqual(getInputData().getReviewType(), CDRConstants.REVIEW_TYPE.START.getDbType()) &&
          ((isOBDOptionEnabled && (CommonUtils.isEqual(obdFlag, CDRConstants.OBD_OPTION.NO_OBD_LABELS.getDbType()) ||
              CommonUtils.isEqual(obdFlag, CDRConstants.OBD_OPTION.BOTH_OBD_AND_NON_OBD_LABELS.getDbType()))) ||
              (!isOBDOptionEnabled && isSimpQues))) {
        resultEntity.setSimpQuesRespFlag(getInputData().getSimpQuesRespValue());
        resultEntity.setSimpQuesRemarks(getInputData().getSimpQuesRemarks());
      }
    }
  }


  /**
   * Checks if all params are Checked iCDM-665
   *
   * @param params params
   * @return true if all params are checked
   */
  public boolean isAllParamsChecked(final Collection<CDRResultParameter> params) {
    // check if all params are reviewed
    if (params.isEmpty()) {
      return false;
    }
    for (CDRResultParameter param : params) {
      if (!DATA_REVIEW_SCORE.getType(param.getReviewScore()).isChecked()) {
        return false;
      }
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {

    CDRReviewResultLoader cdrloader = new CDRReviewResultLoader(getServiceData());
    TRvwResult tReviewResult = cdrloader.getEntityObject(getInputData().getId());
    SortedSet<RvwFile> reviewFileSet = cdrloader.getReviewFile(tReviewResult);

    int reviewResultCount = cdrloader.getReviewResultCount(tReviewResult);
    // if the review is the last one under that variant then the questionnaire should be soft deleted
    if (reviewResultCount == 1) {
      softDeleteUnDeleteRvwQnaireResponse(tReviewResult, true);
    }

    // To delete from tabv_icdm_file_data

    deleteIcdmReviewFileData(tReviewResult);

    // To delete from tabv_icdm_files

    deleteIcdmReviewFile(tReviewResult);

    // To delete Review File

    deleteReviewFiles(reviewFileSet);

    // To delete rvw Participant

    deleteRvwParticipant(tReviewResult);

    // To delete secondary parameter

    deleteSecondaryParam(tReviewResult);

    // To delete Review parameter

    deleteReviewParam(tReviewResult);

    // To delete function

    deleteResultFunction(tReviewResult);

    // To delete review Attibute value

    deleteReviewAttribute(tReviewResult);


    // To delete review variant

    deleteReviewVariant(tReviewResult);

    // To delete secondary review

    deleteSecondaryReview(tReviewResult);

    // To check and delete the RVW Workpackage Resp
    deleteRvwWorkPackageResp(tReviewResult);

    // To delete a review

    // Get the parent and delete the relationship
    if (tReviewResult.getTRvwResult() != null) {
      tReviewResult.getTRvwResult().getTRvwResults().remove(tReviewResult);
    }
    tReviewResult.getTPidcA2l().getTRvwResults().remove(tReviewResult);
    getEm().remove(tReviewResult);

  }

  private void softDeleteUnDeleteRvwQnaireResponse(final TRvwResult tReviewResult, final boolean deleteFlag)
      throws IcdmException {
    RvwQnaireResponseLoader rvwQnaireResponseLoader = new RvwQnaireResponseLoader(getServiceData());
    TRvwVariant tRvwVariant = new CDRReviewResultLoader(getServiceData()).getTRvwVariant(tReviewResult);

    Set<RvwQnaireResponse> rvwQnaireResponse =
        rvwQnaireResponseLoader.getRvwQnaireResponse(tReviewResult.getTPidcA2l().getTPidcVersion().getPidcVersId(),
            null != tRvwVariant ? tRvwVariant.getTabvProjectVariant().getVariantId() : null);

    for (RvwQnaireResponse qnaireResponse : rvwQnaireResponse) {
      qnaireResponse.setDeletedFlag(deleteFlag);
      RvwQnaireResponseCommand rvwQnaireResponseCommand =
          new RvwQnaireResponseCommand(getServiceData(), qnaireResponse, true, false, true);
      executeChildCommand(rvwQnaireResponseCommand);
    }
  }

  private void createRvwVariant(final Long rvwResultId) throws IcdmException {
    Long varId = getVarId();
    if (varId != null) {
      RvwVariant rvVariant = new RvwVariant();
      rvVariant.setResultId(rvwResultId);
      rvVariant.setVariantId(varId);
      RvwVariantCommand rvwVariantCommand = new RvwVariantCommand(getServiceData(), rvVariant, false, false);
      executeChildCommand(rvwVariantCommand);
      this.rvwVariant = rvwVariantCommand.getNewData();
    }
  }

  /**
   * @return
   */
  private Long getVarId() {
    Long varId;
    if (this.monicaReviewFileData == null) {
      varId = this.reviewInputData.getPidcData().getSelPIDCVariantId();
    }
    else {
      varId = this.monicaReviewFileData.getMonicaReviewInputData().getVariantId();
    }
    return varId;
  }

  /**
   * @param rvwResultId
   * @param monitoringToolParserMap
   * @param rvwResultId
   * @throws IcdmException
   */
  private void createReviewFunctions(final Long rvwResultId) throws IcdmException {
    FunctionLoader functionLoader = new FunctionLoader(new ServiceData());
    if (this.monicaReviewFileData == null) {
      if (this.reviewInfo.getCdrFunctionsList() != null) {
        for (Function func : this.reviewInfo.getCdrFunctionsList()) {
          CDRResultFunction cdrResultFunction = new CDRResultFunction();
          cdrResultFunction.setFunctionId(func.getId());
          cdrResultFunction.setResultId(rvwResultId);
          setFunctionVersion(func, cdrResultFunction);
          CDRResultFunctionCommand resultFunctionCommand =
              new CDRResultFunctionCommand(getServiceData(), cdrResultFunction, false, false);
          executeChildCommand(resultFunctionCommand);
        }
      }
    }
    else {
      if (!this.monicaReviewFileData.getMonicaReviewInputData().getMonicaObject().isEmpty()) {
        Map<RvwFunctionModel, Set<String>> rvwFuncMap = functionLoader
            .getFunctionParamMapByParamList(this.monicaReviewFileData.getMonicaReviewInputData().getMonicaObject());

        if (!rvwFuncMap.isEmpty()) {

          createRvwfuncForMonicaReview(rvwResultId, rvwFuncMap);
        }

      }
    }
  }

  /**
   * @param rvwResultId
   * @param rvwFuncMap
   * @throws IcdmException
   */
  private void createRvwfuncForMonicaReview(final Long rvwResultId, final Map<RvwFunctionModel, Set<String>> rvwFuncMap)
      throws IcdmException {
    for (Entry<RvwFunctionModel, Set<String>> entry : rvwFuncMap.entrySet()) {
      RvwFunctionModel rvwFunc = entry.getKey();
      String rvwFuncName = rvwFunc.getFuncName();
      if (this.monicaReviewFileData.getA2lFunctionList().contains(rvwFuncName)) {
        CDRResultFunction cdrResultFunction = new CDRResultFunction();
        cdrResultFunction.setFunctionId(rvwFunc.getFuncId());
        cdrResultFunction.setResultId(rvwResultId);
        // If the function is not available in the new A2l file. then the all module map will not have it.
        if ((this.a2lFileInfo.getAllModulesFunctions() != null) &&
            (this.a2lFileInfo.getAllModulesFunctions().get(rvwFuncName) != null)) {
          String funcVer = this.a2lFileInfo.getAllModulesFunctions().get(rvwFuncName).getFunctionVersion();
          cdrResultFunction.setFunctionVers(funcVer);
        }
        CDRResultFunctionCommand resultFunctionCommand =
            new CDRResultFunctionCommand(getServiceData(), cdrResultFunction, false, false);
        executeChildCommand(resultFunctionCommand);
        entry.getValue().forEach(param -> this.rvwFunctionWithParamMap.put(param, resultFunctionCommand.getObjId()));
      }
    }
  }

  private void createInputFiles(final Long rvwResultId) throws IcdmException {
    if ((this.monicaReviewFileData == null) && (this.reviewInputData.getFileData().getSelFilesPath() != null)) {
      for (String inputFilePath : this.reviewInputData.getFileData().getSelFilesPath()) {
        inserRvwFileCommand(this.reviewInfo.getFilesStreamMap().get(inputFilePath), inputFilePath,
            REVIEW_FILE_TYPE.INPUT, rvwResultId);
      }
    }
    else if (this.monicaReviewFileData != null) {
      inserRvwFileCommand(this.monicaReviewFileData.getDcmByteArray(), this.monicaReviewFileData.getDcmFileName(),
          REVIEW_FILE_TYPE.INPUT, rvwResultId);
    }
  }

  /**
   * @param inputFileBytes
   * @param inputFilePath
   * @param input
   * @param rvwResultId
   * @throws IcdmException
   */
  private void inserRvwFileCommand(final byte[] inputFileBytes, final String inputFilePath,
      final REVIEW_FILE_TYPE input, final Long rvwResultId)
      throws IcdmException {

    RvwFile inputFiles = new RvwFile();
    final File file = new File(inputFilePath);
    inputFiles.setName(file.getName());
    inputFiles.setNodeType(RvwFile.NodeType.REVIEW_RESULT.getDbNodeType());
    inputFiles.setResultId(rvwResultId);
    inputFiles.setFileType(input.getDbType());
    RvwFileCommand rvwInputFileCommand = new RvwFileCommand(getServiceData(), inputFiles, false, false);
    if (inputFileBytes == null) {
      inputFiles.setFilePath(file.getAbsolutePath());
    }
    else {
      rvwInputFileCommand.setFileData(inputFileBytes);
    }
    executeChildCommand(rvwInputFileCommand);
    this.filesCreatedMap.put(inputFilePath.toUpperCase(Locale.getDefault()), rvwInputFileCommand.getNewData());
  }

  /**
   *
   */
  private void createMonicaFiles(final Long rvwResultId) throws IcdmException {
    if (null != this.monicaReviewFileData) {
      inserRvwFileCommand(this.monicaReviewFileData.getMonicaByteArray(), this.monicaReviewFileData.getMonicaFileName(),
          REVIEW_FILE_TYPE.MONICA_FILE, rvwResultId);
    }
  }

  private void createTParticipants(final String userType, final Long resultId, final User user) throws IcdmException {
    // create rvw participants
    RvwParticipant rvwParticipant = new RvwParticipant();
    rvwParticipant.setActivityType(userType);
    rvwParticipant.setResultId(resultId);

    rvwParticipant.setName(user.getDescription());
    rvwParticipant.setUserId(user.getId());
    RvwParticipantCommand participantCalEngCommand =
        new RvwParticipantCommand(getServiceData(), rvwParticipant, false, false);
    executeChildCommand(participantCalEngCommand);
  }

  private void createParticipants(final Long rvwResultId) throws IcdmException {
    UserLoader userLoader = new UserLoader(getServiceData());

    if (this.monicaReviewFileData == null) {
      createParticipantForNormalReview(rvwResultId, userLoader);

    }
    else {
      createParticipantsForMonica(rvwResultId, userLoader);
    }

  }

  /**
   * @param rvwResultId
   * @param userLoader
   * @throws IcdmException
   */
  private void createParticipantsForMonica(final Long rvwResultId, final UserLoader userLoader) throws IcdmException {
    // For Creation of Additional Participants
    if (this.monicaReviewFileData.getMonicaReviewInputData().getReviewParticipants() != null) {
      for (String userName : this.monicaReviewFileData.getMonicaReviewInputData().getReviewParticipants()) {

        createTParticipants(CDRConstants.REVIEW_USER_TYPE.ADDL_PARTICIPANT.getDbType(), rvwResultId,
            userLoader.getDataObjectByUserName(userName.toUpperCase(Locale.getDefault())));
      }
    }
    // For Creation of CalEngineer Participants
    if (this.monicaReviewFileData.getMonicaReviewInputData().getCalEngUserName() != null) {

      createTParticipants(CDRConstants.REVIEW_USER_TYPE.CAL_ENGINEER.getDbType(), rvwResultId,
          userLoader.getDataObjectByUserName(
              this.monicaReviewFileData.getMonicaReviewInputData().getCalEngUserName().toUpperCase()));
    }
    // For Creation of Auditor Participants
    if (this.monicaReviewFileData.getMonicaReviewInputData().getAudUserName() != null) {

      createTParticipants(CDRConstants.REVIEW_USER_TYPE.AUDITOR.getDbType(), rvwResultId,
          userLoader.getDataObjectByUserName(
              this.monicaReviewFileData.getMonicaReviewInputData().getAudUserName().toUpperCase()));
    }
  }

  /**
   * @param rvwResultId
   * @param userLoader
   * @throws IcdmException
   */
  private void createParticipantForNormalReview(final Long rvwResultId, final UserLoader userLoader)
      throws IcdmException {
    if (this.reviewInputData.getUserData().getSelParticipantsIds() != null) {
      for (Long userId : this.reviewInputData.getUserData().getSelParticipantsIds()) {
        createTParticipants(CDRConstants.REVIEW_USER_TYPE.ADDL_PARTICIPANT.getDbType(), rvwResultId,
            userLoader.getDataObjectByID(userId));
      }
    }
    if (this.reviewInputData.getUserData().getSelCalEngineerId() != null) {
      createTParticipants(CDRConstants.REVIEW_USER_TYPE.CAL_ENGINEER.getDbType(), rvwResultId,
          userLoader.getDataObjectByID(this.reviewInputData.getUserData().getSelCalEngineerId()));
    }
    if (this.reviewInputData.getUserData().getSelAuditorId() != null) {

      createTParticipants(CDRConstants.REVIEW_USER_TYPE.AUDITOR.getDbType(), rvwResultId,
          userLoader.getDataObjectByID(this.reviewInputData.getUserData().getSelAuditorId()));
    }
  }


  private void createReviewParams(final TRvwResult entity) throws IcdmException {
    if (this.monicaReviewFileData == null) {
      try {
        this.cdrResParamCreator = new CDRResultParamCreator(this.reviewInfo, this.reviewInputData, getInputData(),
            entity, this.filesCreatedMap, getServiceData());
        SortedSet<CDRResultParameter> paramsToBeCreated = this.cdrResParamCreator.createRvwParams();
        for (CDRResultParameter resultParam : paramsToBeCreated) {
          CDRResultParameterCommand paramCmd =
              new CDRResultParameterCommand(getServiceData(), resultParam, false, false);
          executeChildCommand(paramCmd);
        }
      }
      catch (NumberFormatException exp) {
        throw new IcdmException(exp.getMessage(), exp);
      }
    }
    else {
      createRvaParamForMonicaReview(entity);
    }

  }

  /**
   * @param entity
   * @throws DataException
   * @throws IcdmException
   */
  private void createRvaParamForMonicaReview(final TRvwResult entity) throws IcdmException {
    Map<String, byte[]> calDataByteArrayMap = this.monicaReviewFileData.getCalDataByteArray();
    ParameterLoader parameterLoader = new ParameterLoader(new ServiceData());


    Map<String, Characteristic> charMap =
        this.a2lFileInfo == null ? new HashMap<>() : this.a2lFileInfo.getAllModulesLabels();

    List<MonicaReviewData> monicaObjectList = this.monicaReviewFileData.getMonicaReviewInputData().getMonicaObject();
    List<String> monicaLabels = new ArrayList<>();

    monicaObjectList.forEach(monicaobj -> monicaLabels.add(monicaobj.getLabel()));
    List<Parameter> paramObjList = parameterLoader.getParamObjListByParamName(monicaLabels);
    Map<Parameter, MonicaReviewData> paramObjListByParamNameMap = getParamObjMap(paramObjList, monicaObjectList);

    // Added for MoniCa Delta review
    Map<Long, CDRResultParameter> cdrFuncIDResultParamMap = null;
    if (this.monicaReviewFileData.getMonicaReviewInputData().getOrgResultId() != null) {

      TRvwResult parentEntity = new CDRReviewResultLoader(getServiceData())
          .getEntityObject(this.monicaReviewFileData.getMonicaReviewInputData().getOrgResultId());


      Map<Long, CDRResultParameter> parentResultParamsMap =
          new CDRResultParameterLoader(getServiceData()).getByResultObj(parentEntity);

      cdrFuncIDResultParamMap = new HashMap<>();
      for (CDRResultParameter cdrResultParam : parentResultParamsMap.values()) {
        cdrFuncIDResultParamMap.put(cdrResultParam.getParamId(), cdrResultParam);
      }
    }
    // END code for MoniCa Delta review
    Set<String> getReadOnlyParamNameSet = new CDRResultParamBO().getReadOnlyParamNameSet(this.a2lFileInfo);
    for (Entry<Parameter, MonicaReviewData> reviewDataEntry : paramObjListByParamNameMap.entrySet()) {
      CDRResultParameter cdrResultParameter = new CDRResultParameter();


      setResultFlag(reviewDataEntry, cdrResultParameter);

      // Adding RVWWpResp id to RVW Parameters in monica review
      setRvwResp(reviewDataEntry, cdrResultParameter);

      String paramName = reviewDataEntry.getKey().getName();
      cdrResultParameter.setName(paramName);
      cdrResultParameter.setParamId(reviewDataEntry.getKey().getId());
      cdrResultParameter.setResultId(entity.getResultId());
      cdrResultParameter.setRvwFunId(this.rvwFunctionWithParamMap.get(reviewDataEntry.getKey().getName()));
      cdrResultParameter.setCheckedValue(calDataByteArrayMap.get(reviewDataEntry.getKey().getName()));
      cdrResultParameter.setRvwComment(reviewDataEntry.getValue().getComment());

      // Added for MoniCa Delta review
      CDRResultParameter parentCDRParam = null;
      int changeBitNum = 0;
      if (this.monicaReviewFileData.getMonicaReviewInputData().isDeltaReview() && this.monicaReviewFileData
          .getMonicaReviewInputData().getDeltaReviewType().equals(DELTA_REVIEW_TYPE.DELTA_REVIEW.getDbType()) &&
          (cdrFuncIDResultParamMap != null)) {
        parentCDRParam = cdrFuncIDResultParamMap.get(reviewDataEntry.getKey().getId());
        CalData parentCalData;
        CalData calData;
        try {
          parentCalData = CalDataUtil.getCalDataObj(parentCDRParam.getCheckedValue());
          calData = CalDataUtil.getCalDataObj(cdrResultParameter.getCheckedValue());

        }
        catch (ClassNotFoundException | IOException exp) {
          throw new IcdmException(exp.getLocalizedMessage(), exp);
        }

        Characteristic a2lChar = charMap.get(cdrResultParameter.getName());

        // parentCDRParam cannot be null.

        changeBitNum = setChangeBit(cdrResultParameter, parentCDRParam, parentCalData, calData, a2lChar);

        cdrResultParameter.setChangeFlag(Long.parseLong(String.valueOf(changeBitNum)));
      }
      else {
        cdrResultParameter.setChangeFlag(0L);
      }
      // END code for Monnica Delta review

      // If param is readonly in the input A2lFile then the value is set as true
      cdrResultParameter.setReadOnlyParam(getReadOnlyParamNameSet.contains(paramName));

      CDRResultParameterCommand parameterCommand =
          new CDRResultParameterCommand(getServiceData(), cdrResultParameter, false, false);
      executeChildCommand(parameterCommand);
    }
  }

  /**
   * @param reviewDataEntry
   * @param cdrResultParameter
   */
  private void setRvwResp(final Entry<Parameter, MonicaReviewData> reviewDataEntry,
      final CDRResultParameter cdrResultParameter) {
    if (this.monicaReviewFileData.getRvwMonicaParamAndWpRespModelMap().containsKey(reviewDataEntry.getKey().getId()) &&
        this.monicaRvwWPRespIdAndWPRespModelMap.containsValue(
            this.monicaReviewFileData.getRvwMonicaParamAndWpRespModelMap().get(reviewDataEntry.getKey().getId()))) {
      RvwWpAndRespModel rvwWpAndRespModel =
          this.monicaReviewFileData.getRvwMonicaParamAndWpRespModelMap().get(reviewDataEntry.getKey().getId());
      for (Entry<Long, RvwWpAndRespModel> wpRespEnrtySet : this.monicaRvwWPRespIdAndWPRespModelMap.entrySet()) {
        if (wpRespEnrtySet.getValue().equals(rvwWpAndRespModel)) {
          cdrResultParameter.setRvwWpRespId(wpRespEnrtySet.getKey());
          break;
        }
      }
    }
  }

  /**
   * @param reviewDataEntry
   * @param cdrResultParameter
   */
  private void setResultFlag(final Entry<Parameter, MonicaReviewData> reviewDataEntry,
      final CDRResultParameter cdrResultParameter) {
    if (CDRConstants.RESULT_FLAG.OK.name().equalsIgnoreCase(reviewDataEntry.getValue().getStatus())) {
      cdrResultParameter.setReviewScore(DATA_REVIEW_SCORE.S_8.getDbType());
      cdrResultParameter.setResult(CDRConstants.RESULT_FLAG.OK.getDbType());
    }
    else if (CDRConstants.RESULT_FLAG.NOT_REVIEWED.name().equalsIgnoreCase(reviewDataEntry.getValue().getStatus())) {
      cdrResultParameter.setReviewScore(DATA_REVIEW_SCORE.S_0.getDbType());
    }
    else {
      cdrResultParameter.setReviewScore(DATA_REVIEW_SCORE.S_0.getDbType());
      cdrResultParameter.setResult(CDRConstants.RESULT_FLAG.NOT_OK.getDbType());
    }
  }

  /**
   * @param cdrResultParameter
   * @param parentCDRParam
   * @param changeBitNum
   * @param parentCalData
   * @param calData
   * @param a2lChar
   * @return
   */
  private int setChangeBit(final CDRResultParameter cdrResultParameter, final CDRResultParameter parentCDRParam,
      final CalData parentCalData, final CalData calData, final Characteristic a2lChar) {

    int changeBitNum = 0;
    // if there is a parent cdr parameter for any parameter , then the review is a valid delta review
    if (!CommonUtils.isEqualIgnoreCase(CommonUtils.checkNull(parentCDRParam.getResult()),
        CommonUtils.checkNull(cdrResultParameter.getResult()))) {
      changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.RESULT.setFlag(changeBitNum);
    }

    // check for MoniCa delta review
    if (CommonUtils.isNotNull(calData) && (CommonUtils.isNotNull(parentCalData))) {
      if (!compareObjects(calData.getCalDataPhy(), parentCalData.getCalDataPhy(), a2lChar)) {
        changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.CHECK_VALUE.setFlag(changeBitNum);
      }
    }
    else if (!compareCalObjects(parentCalData, calData, a2lChar)) {
      changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.CHECK_VALUE.setFlag(changeBitNum);
    }
    return changeBitNum;
  }


  /**
   * Need a overloaded compareobjects method since CalDataPhy doesnt override equals(Object) method
   *
   * @param characteristic
   */
  private boolean compareObjects(final CalDataPhy parentCalDataPhy, final CalDataPhy childCalDataPhy,
      final Characteristic characteristic) {
    // ICDM-1785
    CompareResult compareResult = CompareQuantized.isEqualForAllItemsExceptExcluded(characteristic, parentCalDataPhy,
        childCalDataPhy, AvailableItemsForComparison.A2L_UNIT, AvailableItemsForComparison.CAL_DATA_UNITS,
        AvailableItemsForComparison.NO_OF_CHARACTERS, AvailableItemsForComparison.TEXT_BIT);

    return compareResult == CompareResult.EQUAL;
  }

  /**
   * Need a overloaded compareobjects method since CalDataPhy doesnt override equals(Object) method
   *
   * @param characteristic
   */
  private boolean compareCalObjects(final CalData parentCalData, final CalData childCalData,
      final Characteristic characteristic) {
    if ((parentCalData == null) && (childCalData == null)) {
      return true;
    }
    if ((parentCalData == null) || (childCalData == null)) {
      // if either of the objects are null
      return false;
    }
    return compareObjects(parentCalData.getCalDataPhy(), childCalData.getCalDataPhy(), characteristic);
  }


  /**
   * @param paramObjList
   * @param monicaObjectList
   * @return
   */
  private Map<Parameter, MonicaReviewData> getParamObjMap(final List<Parameter> paramObjList,
      final List<MonicaReviewData> monicaObjectList) {
    Map<Parameter, MonicaReviewData> monicaReviewMap = new HashMap<>();
    for (MonicaReviewData monicaReviewData : monicaObjectList) {

      for (Parameter parameter : paramObjList) {
        if (monicaReviewData.getLabel().equals(parameter.getName())) {
          monicaReviewMap.put(parameter, monicaReviewData);
        }

      }

    }
    return monicaReviewMap;

  }

  private void deleteIcdmReviewFileData(final TRvwResult tReviewResult) throws IcdmException {
    CDRReviewResultLoader loader = new CDRReviewResultLoader(getServiceData());
    IcdmFileDataLoader fileDataLoader = new IcdmFileDataLoader(getServiceData());
    SortedSet<IcdmFiles> reviewFileSet = loader.getReviewFileData(tReviewResult);
    for (IcdmFiles icdmFile : reviewFileSet) {
      Set<IcdmFileData> fileData = fileDataLoader.getFileData(icdmFile.getId());
      for (IcdmFileData icdmFileData : fileData) {
        IcdmFileDataCommand fileDataCommand = new IcdmFileDataCommand(getServiceData(), icdmFileData, false, true);
        executeChildCommand(fileDataCommand);
      }
    }
  }

  private void deleteIcdmReviewFile(final TRvwResult tReviewResult) throws IcdmException {
    CDRReviewResultLoader loader = new CDRReviewResultLoader(getServiceData());
    SortedSet<IcdmFiles> reviewFileSet = loader.getReviewFileData(tReviewResult);
    for (IcdmFiles icdmFile : reviewFileSet) {
      IcdmFilesCommand fileCommand = new IcdmFilesCommand(getServiceData(), icdmFile, false, true);
      executeChildCommand(fileCommand);
    }
  }

  private void deleteReviewFiles(final Set<RvwFile> reviewFileSet) throws IcdmException {
    if (!reviewFileSet.isEmpty()) {
      for (RvwFile rvwFile : reviewFileSet) {
        RvwFileCommand fileCommand = new RvwFileCommand(getServiceData(), rvwFile, false, true);
        executeChildCommand(fileCommand);
      }
    }
  }


  private void deleteRvwParticipant(final TRvwResult tReviewResult) throws IcdmException {
    RvwParticipantLoader participantLoader = new RvwParticipantLoader(getServiceData());
    Map<Long, RvwParticipant> rveParticipantMap = participantLoader.getByResultObj(tReviewResult);
    if (!rveParticipantMap.values().isEmpty()) {
      for (RvwParticipant paricipant : rveParticipantMap.values()) {
        RvwParticipantCommand participantCommand = new RvwParticipantCommand(getServiceData(), paricipant, false, true);
        executeChildCommand(participantCommand);
      }
    }
  }

  private void deleteRvwWorkPackageResp(final TRvwResult tReviewResult) throws IcdmException {
    RvwWpRespLoader rvwWpRespLoader = new RvwWpRespLoader(getServiceData());
    Map<Long, RvwWpResp> rvwWpRespMap = rvwWpRespLoader.getByResultObj(tReviewResult);
    if (!rvwWpRespMap.values().isEmpty()) {
      for (RvwWpResp rvwWpResp : rvwWpRespMap.values()) {
        RvwWpRespCommand rvwWpRespCommand = new RvwWpRespCommand(getServiceData(), rvwWpResp, false, true);
        executeChildCommand(rvwWpRespCommand);
      }
    }
  }

  private void deleteSecondaryParam(final TRvwResult tReviewResult) throws IcdmException {
    RvwParametersSecondaryLoader parametersSecondaryLoader = new RvwParametersSecondaryLoader(getServiceData());
    CDRResultParameterLoader parameterLoader = new CDRResultParameterLoader(getServiceData());

    Map<Long, CDRResultParameter> resultparamMap = parameterLoader.getByResultObj(tReviewResult);
    if (!resultparamMap.isEmpty()) {
      for (CDRResultParameter parameter : resultparamMap.values()) {
        Map<Long, Map<Long, RvwParametersSecondary>> secondaryParams =
            parametersSecondaryLoader.getSecondaryParams(parameterLoader.getEntityObject(parameter.getId()));
        for (Map<Long, RvwParametersSecondary> paramMap : secondaryParams.values()) {
          for (RvwParametersSecondary rvwFile : paramMap.values()) {
            RvwParametersSecondaryCommand parametersSecondaryCommand =
                new RvwParametersSecondaryCommand(getServiceData(), rvwFile, false, true);
            executeChildCommand(parametersSecondaryCommand);
          }
        }
      }
    }
  }

  private void deleteReviewParam(final TRvwResult tReviewResult) throws IcdmException {
    CDRResultParameterLoader parameterLoader = new CDRResultParameterLoader(getServiceData());
    Map<Long, CDRResultParameter> resultparamMap = parameterLoader.getByResultObj(tReviewResult);
    for (CDRResultParameter parameter : resultparamMap.values()) {
      CDRResultParameterCommand parameterCommand =
          new CDRResultParameterCommand(getServiceData(), parameter, false, true);
      executeChildCommand(parameterCommand);
    }
  }


  private void deleteResultFunction(final TRvwResult tReviewResult) throws IcdmException {
    CDRResultFunctionLoader functionLoader = new CDRResultFunctionLoader(getServiceData());
    Map<Long, CDRResultFunction> cdrFuncMap = functionLoader.getByResultObj(tReviewResult);
    if (!cdrFuncMap.isEmpty()) {
      for (CDRResultFunction cdrResultFunction : cdrFuncMap.values()) {
        CDRResultFunctionCommand functionCommand =
            new CDRResultFunctionCommand(getServiceData(), cdrResultFunction, false, true);
        executeChildCommand(functionCommand);
      }
    }
  }

  private void deleteReviewAttribute(final TRvwResult tReviewResult) throws IcdmException {
    RvwAttrValueLoader attrValueLoader = new RvwAttrValueLoader(getServiceData());
    Map<Long, RvwAttrValue> rvwAttMap = attrValueLoader.getByResultObj(tReviewResult);
    if (!rvwAttMap.isEmpty()) {
      for (RvwAttrValue rvwAttrValue : rvwAttMap.values()) {
        RvwAttrValueCommand attrValueCommand = new RvwAttrValueCommand(getServiceData(), rvwAttrValue, false, true);
        executeChildCommand(attrValueCommand);
      }
    }
  }

  private void deleteReviewVariant(final TRvwResult tReviewResult) throws IcdmException {
    RvwVariantLoader rvwVariantLoader = new RvwVariantLoader(getServiceData());
    Map<Long, RvwVariant> rvwVarMap = rvwVariantLoader.getByResultObj(tReviewResult);
    if (!rvwVarMap.isEmpty()) {
      for (RvwVariant rvwVariantForDel : rvwVarMap.values()) {
        RvwVariantCommand rvwVariantCommand = new RvwVariantCommand(getServiceData(), rvwVariantForDel, false, true);
        executeChildCommand(rvwVariantCommand);
      }
    }
  }

  private void deleteSecondaryReview(final TRvwResult tReviewResult) throws IcdmException {
    RvwResultsSecondaryLoader resultsSecondaryLoader = new RvwResultsSecondaryLoader(getServiceData());
    Map<Long, RvwResultsSecondary> secRvwResult = resultsSecondaryLoader.getByResultObj(tReviewResult);
    if (!secRvwResult.isEmpty()) {
      for (RvwResultsSecondary rvwResultsSecondary : secRvwResult.values()) {
        RvwResultsSecondaryCommand secondaryCommand =
            new RvwResultsSecondaryCommand(getServiceData(), rvwResultsSecondary, false, true);
        executeChildCommand(secondaryCommand);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // No Implementation
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // No Implemenation
  }

  /**
   * @return the deltaReviewValid
   */
  public boolean isDeltaReviewValid() {
    return this.cdrResParamCreator.isDeltaReviewValid();
  }

  /**
   * @return the rvwVariant
   */
  public RvwVariant getRvwVariant() {
    return this.rvwVariant;
  }


  /**
   * @return the a2lFileInfo
   */
  public A2LFileInfo getA2lFileInfo() {
    return this.a2lFileInfo;
  }


  /**
   * @param a2lFileInfo the a2lFileInfo to set
   */
  public void setA2lFileInfo(final A2LFileInfo a2lFileInfo) {
    this.a2lFileInfo = a2lFileInfo;
  }


  /**
   * @return the a2lWpRespStatusAfterUpd
   */
  public Map<Long, A2lWpResponsibilityStatus> getA2lWpRespStatusAfterUpd() {
    return this.a2lWpRespStatusAfterUpd;
  }


  /**
   * @param a2lWpRespStatusAfterUpd the a2lWpRespStatusAfterUpd to set
   */
  public void setA2lWpRespStatusAfterUpd(final Map<Long, A2lWpResponsibilityStatus> a2lWpRespStatusAfterUpd) {
    this.a2lWpRespStatusAfterUpd = a2lWpRespStatusAfterUpd;
  }


  /**
   * @return the a2lWpRespStatusBeforeUpd
   */
  public Map<Long, A2lWpResponsibilityStatus> getA2lWpRespStatusBeforeUpd() {
    return this.a2lWpRespStatusBeforeUpd;
  }


  /**
   * @param a2lWpRespStatusBeforeUpd the a2lWpRespStatusBeforeUpd to set
   */
  public void setA2lWpRespStatusBeforeUpd(final Map<Long, A2lWpResponsibilityStatus> a2lWpRespStatusBeforeUpd) {
    this.a2lWpRespStatusBeforeUpd = a2lWpRespStatusBeforeUpd;
  }


  /**
   * @return the listOfNewlyCreatedA2lWpRespStatus
   */
  public List<A2lWpResponsibilityStatus> getListOfNewlyCreatedA2lWpRespStatus() {
    return this.listOfNewlyCreatedA2lWpRespStatus;
  }


  /**
   * @param listOfNewlyCreatedA2lWpRespStatus the listOfNewlyCreatedA2lWpRespStatus to set
   */
  public void setListOfNewlyCreatedA2lWpRespStatus(
      final List<A2lWpResponsibilityStatus> listOfNewlyCreatedA2lWpRespStatus) {
    this.listOfNewlyCreatedA2lWpRespStatus = listOfNewlyCreatedA2lWpRespStatus;
  }

}
