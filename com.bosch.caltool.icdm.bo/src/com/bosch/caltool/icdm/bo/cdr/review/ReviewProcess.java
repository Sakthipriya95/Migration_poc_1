/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.review;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.stream.Collectors;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.calmodel.a2ldata.ref.concrete.DefCharacteristic;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2LFileInfoLoader;
import com.bosch.caltool.icdm.bo.a2l.A2LFileInfoProvider;
import com.bosch.caltool.icdm.bo.a2l.A2lWpResponsibilityLoader;
import com.bosch.caltool.icdm.bo.a2l.FunctionLoader;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeModel;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader.LOAD_LEVEL;
import com.bosch.caltool.icdm.bo.cdr.CDRResultParamBO;
import com.bosch.caltool.icdm.bo.cdr.CheckSSDResultParam;
import com.bosch.caltool.icdm.bo.cdr.FeatureAttributeAdapterNew;
import com.bosch.caltool.icdm.bo.cdr.ParameterRepeatExport;
import com.bosch.caltool.icdm.bo.cdr.RuleSetLoader;
import com.bosch.caltool.icdm.bo.cdr.RuleSetParameterLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.QuestionnaireVersionLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireResponseLoader;
import com.bosch.caltool.icdm.bo.compli.CDRCompliReview;
import com.bosch.caltool.icdm.bo.compli.CompliParamResolver;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.bo.shapereview.ShapeReview;
import com.bosch.caltool.icdm.bo.util.ErrorCodeHandler;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CaldataFileParserHandler;
import com.bosch.caltool.icdm.common.util.CaldataFileParserHandler.CALDATA_FILE_TYPE;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.logger.CheckSSDLogger;
import com.bosch.caltool.icdm.logger.ParserLogger;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.a2l.ParameterInput;
import com.bosch.caltool.icdm.model.a2l.WpRespLabelResponse;
import com.bosch.caltool.icdm.model.a2l.WpRespModel;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.cdr.AbstractParameter;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.CDR_SOURCE_TYPE;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.ParamRepeatExcelData;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.model.cdr.RvwWpAndRespModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespUpdationModel;
import com.bosch.caltool.icdm.model.cdr.review.PidcData;
import com.bosch.caltool.icdm.model.cdr.review.ReviewInput;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.checkssd.reports.reportmodel.FormtdRptValModel;
import com.bosch.checkssd.reports.reportmodel.ReportModel;
import com.bosch.ssd.icdm.model.FeatureValueModel;

/**
 * @author bru2cob
 */
public class ReviewProcess extends AbstractReviewProcess {

  /**
   * Single param in input.
   */
  private static final int SINGLE_PARAM_COUNT = 1;

  /**
   * DEFAULT WP ID.
   */
  private Long DEFAULT_WP_ID;
  /**
   * Param repeat count.
   */
  private static final int PARAM_REPEAT_COUNT = 2;
  /**
   * String constant for CDR_Repeated_Parameters
   */
  private static final String CDR_REPEATED_PARAMETERS = "CDR_Repeated_Parameters";

  private static final String HEX_EXTENSION = ".hex";

  IReviewProcessResolver primaryReview;
  List<IReviewProcessResolver> secondaryReview;
  private IReviewProcessResolver compliReview;
  IReviewProcessResolver shapeReview;

  private A2LFileInfo a2lFileContents;

  private final Map<String, byte[]> filesStreamMap;

  private PidcA2l pidcA2l;

  private final List<String> labelList = new ArrayList<>();

  boolean isGnrlQnaire = false;


  /**
   * Key - label name, value - list of warnings
   */
  private final Map<String, List<String>> parserWarningsMap = new HashMap<>();
  /**
   * true - if all the labels selected for review is not in CalMemory Labels
   */
  private boolean isAllSelLblsNotInCalMemory;

  /**
   * @param reviewInputData Input Data
   * @param filesStreamMap files as streams
   * @param serviceData Service Data
   */
  public ReviewProcess(final ReviewInput reviewInputData, final Map<String, byte[]> filesStreamMap,
      final ServiceData serviceData) {

    super(reviewInputData, serviceData);
    this.filesStreamMap = filesStreamMap;
  }

  /**
   * @return Review summary
   * @throws IcdmException error during review process
   */
  public ReviewedInfo performReview() throws IcdmException {
    PidcData pidcData = getReviewInputData().getPidcData();

    this.pidcA2l = new PidcA2lLoader(getServiceData()).getDataObjectByID(pidcData.getPidcA2lId());
    getReviewOutput().setSsdSoftwareVersionId(this.pidcA2l.getSsdSoftwareVersionId());

    getReviewOutput().setFilesStreamMap(this.filesStreamMap);

    A2LFile a2lFile = new A2LFileInfoLoader(getServiceData()).getDataObjectByID(this.pidcA2l.getA2lFileId());
    this.a2lFileContents = new A2LFileInfoProvider(getServiceData()).fetchA2LFileInfo(a2lFile);

    if (this.pidcA2l.getVcdmA2lName() != null) {
      this.a2lFileContents.setFileName(this.pidcA2l.getVcdmA2lName());
    }

    getReviewOutput().setA2lFileContents(this.a2lFileContents);

    getReviewOutput().setCheckSSDLogger(CheckSSDLogger.getInstance());

    final boolean allParamsSelection = getReviewInputData().isFilesToBeReviewed();

    // load pidc details
    ProjectAttributeLoader pidcAttrLoader = new ProjectAttributeLoader(getServiceData());
    getReviewOutput().setPidcDetails(pidcAttrLoader.createModel(this.pidcA2l.getPidcVersId(), LOAD_LEVEL.L3_VAR_ATTRS));

    setMappingSourceID();
    setSecondaryRuleSets();

    if (allParamsSelection) {
      handleErrorForHugeParams();
    }
    boolean labFunSelection = false;
    String funLabFilePath = getReviewInputData().getFileData().getFunLabFilePath();
    if ((null != funLabFilePath) && !funLabFilePath.isEmpty()) {
      labFunSelection = true;
    }

    // fetching the cal data map and also validating the input files like cdfx, hex, xml, dcm
    // if error available, <cal_Data_Review_Summary_Instance>_setExceptioninReview As true
    Map<String, CalData> reviewMap = fetchCalDataMap(labFunSelection, allParamsSelection);

    if ((reviewMap.size() > 0) || this.isAllSelLblsNotInCalMemory) {
      getReviewOutput().setCalDataMap(reviewMap);

      // set warnings map
      getReviewOutput().setParserWarningsMap(this.parserWarningsMap);

      SortedSet<String> selReviewFuncs = new TreeSet<>();
      if (getReviewInputData().getSelReviewFuncs() != null) {
        selReviewFuncs.addAll(getReviewInputData().getSelReviewFuncs());
      }

      setFuncParam(allParamsSelection, selReviewFuncs);

      Map<Long, WpRespModel> tempParamWpRespModelMap = new HashMap<>();

      Map<Long, RvwWpAndRespModel> paramWpRespModelMap = new HashMap<>();
      Set<RvwWpAndRespModel> rvwWpAndRespModelSet = new HashSet<>();

      createWpRespModelSet(tempParamWpRespModelMap, paramWpRespModelMap, rvwWpAndRespModelSet);
      getReviewOutput().setRvwWpAndRespModelSet(rvwWpAndRespModelSet);
      getReviewOutput().setRvwParamAndWpRespModelMap(paramWpRespModelMap);

      if (getReviewOutput().isNoValidRuleFlag()) {
        // if there are no parameters with rules, no need to invoke CheckSSD
        Map<String, CalData> calDataMap = getReviewOutput().getCalDataMap();
        Map<String, CheckSSDResultParam> checkSSDResultParamMap = new HashMap<>();
        setCheckSSDMap(calDataMap, checkSSDResultParamMap, null, null);
      }
      invokeCheckSSD();
      setResultsToParam();
      if (getReviewOutput().isCompliParamsPresent()) {
        // ICDM-2440 if there is no exception and compliance parameters are present
        ((CDRCompliReview) this.compliReview).invokeCheckSSDForCompliParam();
      }

      Long varId = getReviewInputData().getPidcData().getSelPIDCVariantId();
      fillRvwQnaireRespCreationModel(
          CommonUtils.isNull(varId) || CommonUtils.isEqual(varId, ApicConstants.NO_VARIANT_ID) ? null : varId);

      this.shapeReview = new ShapeReview(getReviewOutput().getCalDataMap(), this.a2lFileContents, getServiceData());
      this.shapeReview.performReview();
      getReviewOutput().setSrResult(((ShapeReview) this.shapeReview).getResult());
    }

    fillReadOnlyAndDepParams();

    return getReviewOutput();
  }

  /**
   * @param allParamsSelection
   * @param selReviewFuncs
   * @throws IcdmException
   */
  private void setFuncParam(final boolean allParamsSelection, final SortedSet<String> selReviewFuncs)
      throws IcdmException {
    // If Fun file is selected or the Work package is selected
    if (!selReviewFuncs.isEmpty() && !allParamsSelection && this.labelList.isEmpty()) {
      setFuncParamsForFun();
    }
    // If the lab file or Hex File, CDFX file or DCm file is selected
    else {
      setFuncParamsForLab();
    }
  }

  /**
   * fill read only and dep params in
   *
   * @throws DataException
   */
  private void fillReadOnlyAndDepParams() {

    CDRResultParamBO cdrRsltParamBo = new CDRResultParamBO();

    // get read only parameters name List and store in ReviewedInfo
    getReviewOutput().setReadOnlyParamNameSet(cdrRsltParamBo.getReadOnlyParamNameSet(this.a2lFileContents));

    // get dependency parameter Map and store in ReviewedInfo
    getReviewOutput().setDepParams(cdrRsltParamBo.getDepParamMapForA2L(this.a2lFileContents));
  }

  /**
   * Fill questionnaire responses for official reviews
   *
   * @param varId
   * @param tempWpRespModelMap
   * @throws IcdmException
   */
  private void fillRvwQnaireRespCreationModel(final Long varId) throws IcdmException {
    Long pidcVersId = this.pidcA2l.getPidcVersId();
    boolean isOBDOptionEnabled = CDRReviewResultUtil.isOBDOptionEnabled(getServiceData(), pidcVersId);
    boolean isSimpQuesEnabled =
        CDRReviewResultUtil.isSimpQuesEnabled(getServiceData(), pidcVersId) || (isOBDOptionEnabled &&
            CommonUtils.isEqual(getReviewInputData().getObdFlag(), CDRConstants.OBD_OPTION.NO_OBD_LABELS.getDbType()));

    // To be filled only for Official and start Review(should not be filled incase of Simplified Qnaire || OBD option
    // with 'NO OBD labels')
    String reviewType = getReviewInputData().getReviewType();
    if (!CDRReviewResultUtil.isRvwQnaireRespAllowed(getServiceData(), pidcVersId) ||
        (isSimpQuesEnabled && CommonUtils.isEqual(CDRConstants.REVIEW_TYPE.START.getDbType(), reviewType)) ||
        CDRConstants.REVIEW_TYPE.TEST.getDbType().equals(reviewType)) {
      return;
    }

    Long activeGenQniareVerId = getGeneralQnaireId(isSimpQuesEnabled, isOBDOptionEnabled);

    Set<QnaireRespUpdationModel> rvwQnaireRespCreationModelSet = new HashSet<>();
    RvwQnaireResponseLoader rvwQnaireResponseLoader = new RvwQnaireResponseLoader(getServiceData());
    Set<RvwWpAndRespModel> wpAndRespUsingPidcAndVarSet = rvwQnaireResponseLoader
        .getWPAndRespUsingPidcAndVariant(pidcVersId, getReviewInputData().getPidcData().getSelPIDCVariantId());


    for (RvwWpAndRespModel rvwWpAndRespModel : getReviewOutput().getRvwWpAndRespModelSet()) {
      // First Check WP/Resp Structure already not available - if false Check Gnrl Qnaire to be added on
      // completion of review is either standard general qnaire when there is empty Wp/Resp for Simplified Qnaire
      // only when not an WP pass qnaire ID
      Long generalQnaireID = rvwWpAndRespModel.getA2lWpId().equals(this.DEFAULT_WP_ID) ? null : activeGenQniareVerId;
      boolean checkQnaireValidation = validateQnaireCreation(varId, pidcVersId, isOBDOptionEnabled, generalQnaireID,
          rvwQnaireResponseLoader, rvwWpAndRespModel);

      // allow only if it is not a default WP and no Node is already present in qnaire
      if (!wpAndRespUsingPidcAndVarSet.contains(rvwWpAndRespModel) || checkQnaireValidation) {

        QnaireRespUpdationModel rvwQnaireRespCreationModel = new QnaireRespUpdationModel();
        rvwQnaireRespCreationModel.setSelRespId(rvwWpAndRespModel.getA2lRespId());
        rvwQnaireRespCreationModel.setSelWpId(rvwWpAndRespModel.getA2lWpId());
        rvwQnaireRespCreationModel.setPidcVersionId(pidcVersId);
        rvwQnaireRespCreationModel.setPidcVariantId(getReviewInputData().getPidcData().getSelPIDCVariantId());
        rvwQnaireRespCreationModel.setQnaireVersId(generalQnaireID);
        rvwQnaireRespCreationModelSet.add(rvwQnaireRespCreationModel);
      }
    }
    getReviewOutput().setRvwQnaireRespCreationModelSet(rvwQnaireRespCreationModelSet);
  }

  /**
   * @param varId
   * @param pidcVersId
   * @param isOBDOptionEnabled
   * @param activeGenQniareVerId
   * @param rvwQnaireResponseLoader
   * @param rvwWpAndRespModel
   * @return
   * @throws DataException
   */
  private boolean validateQnaireCreation(final Long varId, final Long pidcVersId, final boolean isOBDOptionEnabled,
      final Long activeGenQniareVerId, final RvwQnaireResponseLoader rvwQnaireResponseLoader,
      final RvwWpAndRespModel rvwWpAndRespModel)
      throws DataException {
    // First Check WP/Resp Structure already not available - if false Check Gnrl Qnaire to be added on
    // completion of review is either standard general qnaire when there is empty Wp/Resp for Simplified Qnaire
    boolean isGnrlQuesAvailAndOBDQnaireNotAvail =
        !this.isGnrlQnaire && isOBDOptionEnabled && rvwQnaireResponseLoader.isGnrlQuesAvailAndOBDQnaireNotAvail(
            pidcVersId, getStrdGnrlQnaireId(), getOBDGnrlQnaireId(), varId, rvwWpAndRespModel);

    return CommonUtils.isNotNull(activeGenQniareVerId) &&
        (rvwQnaireResponseLoader.isSimpAvailAndGnrlQnaireNotAvail(pidcVersId, getGnrlQnaireId(), varId,
            rvwWpAndRespModel) || isGnrlQuesAvailAndOBDQnaireNotAvail);
  }

  /**
   *
   */
  private Long getGnrlQnaireId() {
    return this.isGnrlQnaire ? getStrdGnrlQnaireId() : getOBDGnrlQnaireId();
  }

  /**
  *
  */
  private Long getStrdGnrlQnaireId() {
    return Long.valueOf(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.GENERAL_QNAIRE_ID));
  }

  /**
  *
  */
  private Long getOBDGnrlQnaireId() {
    return Long.valueOf(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.OBD_GENERAL_QNAIRE_ID));
  }

  /**
   * @param rvwResultServerBo
   * @param isSimpQuesEnabled
   * @param isOBDOptionEnabled
   * @return
   * @throws IcdmException
   */
  private Long getGeneralQnaireId(final boolean isSimpQuesEnabled, final boolean isOBDOptionEnabled)
      throws IcdmException {
    // for simplified Qnaire and Official Review type the gnrl Qnaire will be null
    Long gnrlQnaireId = null;

    QuestionnaireVersionLoader qnaireVersLoader = new QuestionnaireVersionLoader(getServiceData());
    boolean isDefaultWP =
        CommonUtils.isEqual(getReviewOutput().getGrpWorkPackageName(), ApicConstants.DEFAULT_A2L_WP_NAME);
    if (isOBDOptionEnabled) {
      gnrlQnaireId = isDefaultWP ||
          CommonUtils.isEqual(getReviewInputData().getObdFlag(), CDRConstants.OBD_OPTION.NO_OBD_LABELS.getDbType())
              ? null : qnaireVersLoader.getActiveOBDGenrlQnaireVers();
    }
    else if (!isSimpQuesEnabled) {
      // if simplified Qnaire and OBD general qnaire is not enabled then add standard general Qnaire
      gnrlQnaireId = isDefaultWP ? null : qnaireVersLoader.getActiveGenrlQnaireVers();
      this.isGnrlQnaire = !isDefaultWP;
    }

    return gnrlQnaireId;
  }

  /**
   * @throws InvalidInputException
   */
  private void handleErrorForHugeParams() throws InvalidInputException {
    final boolean hexFileAvailable = checkForHexFile(getReviewInputData().getFileData().getSelFilesPath());
    if (hexFileAvailable) {

      final Integer parameterValue =
          Integer.valueOf((new CommonParamLoader(getServiceData())).getValue(CommonParamKey.CDR_MAX_PARAM_COUNT));
      SortedSet<Characteristic> characteristics = this.a2lFileContents.getAllSortedLabels(true);
      // Icdm-1751- check removed for Reviews with Rule set.
      if ((parameterValue <= characteristics.size()) &&
          (getReviewInputData().getRulesData().getPrimaryRuleSetId() == null)) {
        final String dialogMessage =
            "Number of parameters to be reviewed is too high(> " + parameterValue + "). Review cannot be performed.";
        // throw invalid input exception in case the input stream cannot be
        throw new InvalidInputException(dialogMessage);
      }
    }
  }

  /**
   * @param tempParamWpRespModelMap
   * @param paramWpRespModelMap
   * @param rvwWpAndRespModelSet
   * @throws IcdmException
   */
  private void createWpRespModelSet(final Map<Long, WpRespModel> tempParamWpRespModelMap,
      final Map<Long, RvwWpAndRespModel> paramWpRespModelMap, final Set<RvwWpAndRespModel> rvwWpAndRespModelSet)
      throws IcdmException {
    List<WpRespLabelResponse> wpRespLabelRespMap = new A2lWpResponsibilityLoader(getServiceData())
        .getWpResp(this.pidcA2l.getId(), getReviewInputData().getPidcData().getSelPIDCVariantId());
    for (WpRespLabelResponse response : wpRespLabelRespMap) {
      WpRespModel wpRespModel = response.getWpRespModel();
      tempParamWpRespModelMap.put(response.getParamId(), wpRespModel);
      if (wpRespModel.getWpName().equals(ApicConstants.DEFAULT_A2L_WP_NAME)) {
        this.DEFAULT_WP_ID = wpRespModel.getA2lWpId();
      }
    }

    for (Set<Parameter> wpRespLabelResponse : getReviewOutput().getReviewFuncParamMap().values()) {
      for (Parameter param : wpRespLabelResponse) {
        if (tempParamWpRespModelMap.containsKey(param.getId())) {
          WpRespModel wpRespModel = tempParamWpRespModelMap.get(param.getId());
          RvwWpAndRespModel rvwWpAndRespModel = new RvwWpAndRespModel();
          rvwWpAndRespModel.setA2lRespId(wpRespModel.getA2lResponsibility().getId());
          rvwWpAndRespModel.setA2lWpId(wpRespModel.getA2lWpId());
          paramWpRespModelMap.put(param.getId(), rvwWpAndRespModel);
          rvwWpAndRespModelSet.add(rvwWpAndRespModel);

        }
      }
    }
  }


  /**
   *
   */
  private void setMappingSourceID() {
    PidcVersionAttributeModel pidcDetails = getReviewOutput().getPidcDetails();
    PidcVersionAttribute wpTypeAttr = getPidcWpAttr(pidcDetails);
    if ((wpTypeAttr != null) && (wpTypeAttr.getValueId() != null) && (com.bosch.caltool.icdm.common.util.ApicUtil
        .compare(wpTypeAttr.getUsedFlag(), ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType()) == 0)) {
      getReviewOutput().setMappingSource(wpTypeAttr.getValueId());
    }
  }

  /**
   * @param pidcVersion
   * @return
   */
  private PidcVersionAttribute getPidcWpAttr(final PidcVersionAttributeModel pidcDetails) {
    Map<Long, PidcVersionAttribute> pidcattrMap = pidcDetails.getPidcVersAttrMap();
    long wpAttrId = Long.parseLong(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.WP_TYPE_ATTR_ID));
    return pidcattrMap.get(wpAttrId);
  }

  /**
   * @throws DataException
   */
  private void setSecondaryRuleSets() throws DataException {
    MandateRuleSetResolver resolver =
        new MandateRuleSetResolver(getServiceData(), getReviewOutput(), getReviewInputData());
    resolver.addMandateRuleSet();
  }


  /**
   * set the secondary results map for the result param.
   */
  private void setResultsToParam() {
    Map<String, CDRConstants.RESULT_FLAG> secondaryResMap = new ConcurrentHashMap<>();
    List<ReviewRuleSetData> ruleSetDataList = getReviewOutput().getSecRuleSetDataList();

    for (ReviewRuleSetData reviewRuleSetData : ruleSetDataList) {
      if (reviewRuleSetData.getCheckSSDResParamMap() != null) {
        getResultFromCheckSSDResParam(secondaryResMap, reviewRuleSetData);
      }
    }

    getReviewOutput().getSecResultMap().putAll(secondaryResMap);
  }

  /**
   * @param secondaryResMap
   * @param reviewRuleSetData
   * @param result
   */
  private void getResultFromCheckSSDResParam(final Map<String, CDRConstants.RESULT_FLAG> secondaryResMap,
      final ReviewRuleSetData reviewRuleSetData) {

    CDRConstants.RESULT_FLAG result = CDRConstants.RESULT_FLAG.NOT_REVIEWED;
    for (Entry<String, CheckSSDResultParam> checkSSDResultParam : reviewRuleSetData.getCheckSSDResParamMap()
        .entrySet()) {

      ReportModel reportModel = checkSSDResultParam.getValue().getCompliReportModel();
      if (reportModel != null) {
        if (reportModel.isRuleOk()) {
          result = CDRConstants.RESULT_FLAG.OK;
        }
        else {
          result = getResultFromResultModel(reportModel);
        }
      }
      else {

        result = getResultForNoReport(reviewRuleSetData, result, checkSSDResultParam);
      }
      setSecondResMap(secondaryResMap, result, checkSSDResultParam);
      result = CDRConstants.RESULT_FLAG.NOT_REVIEWED;
    }
  }

  /**
   * @param reviewRuleSetData
   * @param result
   * @param checkSSDResultParam
   * @return
   */
  private CDRConstants.RESULT_FLAG getResultForNoReport(final ReviewRuleSetData reviewRuleSetData,
      final CDRConstants.RESULT_FLAG result, final Entry<String, CheckSSDResultParam> checkSSDResultParam) {

    Map<String, List<ReviewRule>> rulesMap = reviewRuleSetData.getCdrRules();
    CDRConstants.RESULT_FLAG resultFlag = result;
    if (rulesMap != null) {

      List<ReviewRule> ruleList = rulesMap.get(checkSSDResultParam.getKey());
      RuleUtility ruleUtil = new RuleUtility();
      if (CommonUtils.isNotEmpty(ruleList)) {
        ReviewRule cdrRule = ruleList.get(0);
        if (ruleUtil.isRuleComplete(cdrRule)) {
          resultFlag = CDRConstants.RESULT_FLAG.NOT_OK;
        }
      }
    }
    return resultFlag;
  }

  /**
   * @param secondaryResMap
   * @param currentRes
   * @param checkSSDResultParam
   */
  private void setSecondResMap(final Map<String, CDRConstants.RESULT_FLAG> secondaryResMap,
      final CDRConstants.RESULT_FLAG currentRes, final Entry<String, CheckSSDResultParam> checkSSDResultParam) {

    if (secondaryResMap.get(checkSSDResultParam.getKey()) == null) {
      whenSecResIsNull(secondaryResMap, currentRes, checkSSDResultParam);
    }
    else if ((currentRes == CDRConstants.RESULT_FLAG.NOT_OK) || (currentRes == CDRConstants.RESULT_FLAG.HIGH) ||
        (currentRes == CDRConstants.RESULT_FLAG.LOW)) {
      secondaryResMap.put(checkSSDResultParam.getKey(), CDRConstants.RESULT_FLAG.NOT_OK);
    }

    else if ((secondaryResMap.get(checkSSDResultParam.getKey()) == CDRConstants.RESULT_FLAG.OK) &&
        (currentRes != CDRConstants.RESULT_FLAG.NOT_REVIEWED)) {
      secondaryResMap.put(checkSSDResultParam.getKey(), currentRes);
    }
    // 236207
    else if ((currentRes == CDRConstants.RESULT_FLAG.OK) &&
        (secondaryResMap.get(checkSSDResultParam.getKey()) == CDRConstants.RESULT_FLAG.NOT_REVIEWED)) {
      secondaryResMap.put(checkSSDResultParam.getKey(), CDRConstants.RESULT_FLAG.OK);
    }
  }

  /**
   * @param secondaryResMap
   * @param currentRes
   * @param checkSSDResultParam
   */
  private void whenSecResIsNull(final Map<String, CDRConstants.RESULT_FLAG> secondaryResMap,
      final CDRConstants.RESULT_FLAG currentRes, final Entry<String, CheckSSDResultParam> checkSSDResultParam) {

    if ((currentRes == CDRConstants.RESULT_FLAG.NOT_OK) || (currentRes == CDRConstants.RESULT_FLAG.HIGH) ||
        (currentRes == CDRConstants.RESULT_FLAG.LOW)) {
      secondaryResMap.put(checkSSDResultParam.getKey(), CDRConstants.RESULT_FLAG.NOT_OK);
    }
    else {
      secondaryResMap.put(checkSSDResultParam.getKey(), currentRes);
    }
  }

  /**
   * @param reportModel ReportModel
   * @return CDRConstants.RESULT_FLAG
   */
  private CDRConstants.RESULT_FLAG getResultFromResultModel(final ReportModel reportModel) {
    CDRConstants.RESULT_FLAG result;
    if (reportModel instanceof FormtdRptValModel) {
      FormtdRptValModel formtdRptValModel = (FormtdRptValModel) reportModel;
      if ((formtdRptValModel.getValGE() != null) && !formtdRptValModel.getValGE().isEmpty()) {
        result = CDRConstants.RESULT_FLAG.HIGH;
      }
      else if ((formtdRptValModel.getValLE() != null) && !formtdRptValModel.getValLE().isEmpty()) {
        result = CDRConstants.RESULT_FLAG.LOW;
      }
      else {
        result = CDRConstants.RESULT_FLAG.NOT_OK;
      }
    }
    else {
      result = CDRConstants.RESULT_FLAG.NOT_OK;
    }
    return result;
  }


  /**
   * new method for fetching the CDRFunction and CDRParameter for a list of labels
   *
   * @throws IcdmException
   */
  // Icdm-870
  private void setFuncParamsForLab() throws IcdmException {

    // Get the editor Provider
    Set<String> funNames = new HashSet<>();
    Set<String> labelNames = new HashSet<>();
    Set<String> charSet = new HashSet<>();
    Map<String, Set<Characteristic>> funcCharMap = new HashMap<>();
    Map<String, Characteristic> allModulesLabels = this.a2lFileContents.getAllModulesLabels();
    // Fill the Fun names in a list and also the Character objects set
    for (String label : this.labelList) {

      fillFunLabNames(allModulesLabels, funNames, label, charSet, labelNames, funcCharMap);
    }
    if (funNames.isEmpty() || labelNames.isEmpty()) {
      throw new IcdmException("CDR.PARAM_NOT_FOUND");
    }
    SortedSet<com.bosch.caltool.icdm.model.a2l.Function> reviewFuncsSet;
    if ("LAB".equalsIgnoreCase(getReviewInputData().getSourceType())) {
      // Fetch the CDR functions from the T_Function table
      FunctionLoader cdrFuncLoader = new FunctionLoader(getServiceData());
      Map<String, com.bosch.caltool.icdm.model.a2l.Function> funcMappingMap = cdrFuncLoader.getFunctionsByName(
          getReviewInputData().getSelReviewFuncs(), !getReviewOutput().getUnassParaminReview().isEmpty());
      reviewFuncsSet = new TreeSet<>(funcMappingMap.values());
    }
    else {
      FunctionLoader cdrFuncLoader = new FunctionLoader(getServiceData());
      Map<String, com.bosch.caltool.icdm.model.a2l.Function> funcMappingMap =
          cdrFuncLoader.getFunctionsByName(funNames, !getReviewOutput().getUnassParaminReview().isEmpty());
      reviewFuncsSet = new TreeSet<>(funcMappingMap.values());
    }
    // Set the value to the CDR function list
    if (!reviewFuncsSet.isEmpty()) {
      getReviewOutput().setCdrFunctionsList(reviewFuncsSet);
    }

    // Fetch the Func Param from tParam table
    ParameterLoader paramLoader = new ParameterLoader(getServiceData());
    Map<String, Parameter> fetchFuncParams = paramLoader.getParamsByName(charSet, allModulesLabels);


    // Set of Cdr Functions for the Review. -reviewFuncsSet
    Map<Long, Set<Parameter>> rvwFunParamMap = new HashMap<>();
    populateFuncParam(reviewFuncsSet, rvwFunParamMap, funcCharMap, fetchFuncParams);

    // Load the CDR func parameter object
    Set<AbstractParameter> modParamSet = null;
    // Set the value to the wizard
    if (!rvwFunParamMap.isEmpty()) {
      modParamSet = createCdrFuncParam(labelNames, rvwFunParamMap);

    }
    setErrorForEmptyParamSet(rvwFunParamMap, modParamSet);

  }

  /**
   * @param labelNames
   * @param rvwFunParamMap
   * @return
   * @throws IcdmException
   */
  private Set<AbstractParameter> createCdrFuncParam(final Set<String> labelNames,
      final Map<Long, Set<Parameter>> rvwFunParamMap)
      throws IcdmException {
    Set<AbstractParameter> modParamSet;
    getReviewOutput().setReviewFuncParamMap(rvwFunParamMap);
    SortedSet<Parameter> treeSet = new TreeSet<>();

    for (Set<Parameter> cdrFuncParameterSet : rvwFunParamMap.values()) {
      if (cdrFuncParameterSet != null) {
        for (Parameter cdrFuncParameter : cdrFuncParameterSet) {
          if (null != cdrFuncParameter) {
            treeSet.add(cdrFuncParameter);
          }
        }
      }

    }
    modParamSet = fetchDepAndRules(labelNames, treeSet);

    getReviewOutput().setCdrFuncParams(modParamSet);
    return modParamSet;
  }


  /**
   * set the Func params if a list of Function names are available
   *
   * @throws IcdmException
   */
  private void setFuncParamsForFun() throws IcdmException {

    final SortedSet<Function> allFuncsList = this.a2lFileContents.getAllSortedFunctions();
    Set<Function> functions = new HashSet<>();
    Set<String> charSet = new HashSet<>();

    Map<Long, Set<Parameter>> reviewFuncParamMap = new HashMap<>();
    Set<String> labelNames = new HashSet<>();
    Map<String, Set<Characteristic>> funcCharMap = new HashMap<>();


    addFunctions(allFuncsList, functions);


    // Get the CDRFunction set from the T_FUnctions table
    // Fetch the CDR functions from the T_Function table
    FunctionLoader cdrFuncLoader = new FunctionLoader(getServiceData());
    Map<String, com.bosch.caltool.icdm.model.a2l.Function> funcMappingMap = cdrFuncLoader.getFunctionsByName(
        getReviewInputData().getSelReviewFuncs(), !getReviewOutput().getUnassParaminReview().isEmpty());
    SortedSet<com.bosch.caltool.icdm.model.a2l.Function> reviewFuncsSet = new TreeSet<>(funcMappingMap.values());

    // Iterate the function list and store the store the char list
    fillCharSet(functions, charSet, labelNames, funcCharMap);
    // Fetch the Func Param from tParam table
    ParameterLoader paramLoader = new ParameterLoader(getServiceData());
    Map<String, Parameter> fetchFuncParams =
        paramLoader.getParamsByName(charSet, this.a2lFileContents.getAllModulesLabels());

    // Set of Cdr Functions for the Review. -reviewFuncsSet
    populateFuncParam(reviewFuncsSet, reviewFuncParamMap, funcCharMap, fetchFuncParams);

    // Fetch the func params for the char set

    if (!reviewFuncsSet.isEmpty()) {
      getReviewOutput().setCdrFunctionsList(reviewFuncsSet);
    }
    Set<AbstractParameter> modParamSet = null;


    SortedSet<Parameter> paramSet = new TreeSet<>();

    for (Set<Parameter> cdrFuncParameterSet : reviewFuncParamMap.values()) {
      if (cdrFuncParameterSet != null) {
        for (Parameter cdrFuncParameter : cdrFuncParameterSet) {
          if (null != cdrFuncParameter) {
            paramSet.add(cdrFuncParameter);
          }
        }
      }

    }
    modParamSet = fetchDepAndRules(labelNames, paramSet);
    getReviewOutput().setReviewFuncParamMap(reviewFuncParamMap);
    getReviewOutput().setCdrFuncParams(modParamSet);

    setErrorForEmptyParamSet(reviewFuncParamMap, modParamSet);
  }

  /**
   * @param labelNames
   * @param paramSet
   * @return
   * @throws IcdmException
   */
  private Set<AbstractParameter> fetchDepAndRules(final Set<String> labelNames, final SortedSet<Parameter> paramSet)
      throws IcdmException {
    FeatureAttributeAdapterNew faAdapter = new FeatureAttributeAdapterNew(getServiceData());
    Set<AbstractParameter> reviewParamSet = null;
    // secondary commmon param variables
    Set<AbstractParameter> secondaryCommonParamSet = new HashSet<>();
    Set<FeatureValueModel> secondaryComParamFeaValSet = new HashSet<>();
    // secondary rule set variables
    Map<Long, Set<AbstractParameter>> secReviewParamMap = new HashMap<>();
    Map<Long, Set<FeatureValueModel>> secFeatureValueModelMap = new HashMap<>();
    try {
      if (getReviewOutput().getUnassParaminReview() != null) {
        labelNames.addAll(getReviewOutput().getUnassParaminReview());
      }

      Set<String> paramNotInRuleset = new TreeSet<>();
      reviewParamSet =
          getReviewFuncParamSet(paramSet, labelNames, paramNotInRuleset, secReviewParamMap, secondaryCommonParamSet);
      getReviewOutput().getParamNotInRuleset().clear();
      getReviewOutput().getParamNotInRuleset().addAll(paramNotInRuleset);
      performCompliReview(paramSet, faAdapter);
      List<IParameter> paramsToRemove = new ArrayList<>();

      Set<FeatureValueModel> featureValModSet =
          getFeatureValModel(new HashSet<>(reviewParamSet), faAdapter, labelNames, paramsToRemove);
      // Loading secondary Ruleset FeatureValueModel
      for (Entry<Long, Set<AbstractParameter>> secReviewParamEntrySet : secReviewParamMap.entrySet()) {
        List<IParameter> secParamsToRemove = new ArrayList<>();
        Set<FeatureValueModel> tempFeatureValModSet = getFeatureValModel(
            new HashSet<>(secReviewParamEntrySet.getValue()), faAdapter, labelNames, secParamsToRemove);
        secFeatureValueModelMap.put(secReviewParamEntrySet.getKey(), tempFeatureValModSet);
      }
      // Loading secondary common param feature value model
      if (getReviewInputData().getRulesData().isCommonRulesSecondary()) {
        List<IParameter> secCommonParamsToRemove = new ArrayList<>();
        secondaryComParamFeaValSet =
            getFeatureValModel(new HashSet<>(secondaryCommonParamSet), faAdapter, labelNames, secCommonParamsToRemove);
      }

      getReviewOutput().setAttrWithoutMapping(faAdapter.getValueNotSetAttr());
      Set<AttributeValueModel> attrValModel = getAttrValModel(featureValModSet);

      for (IParameter obj : paramsToRemove) {
        getReviewOutput().getParamWithoutRule().add(obj.getName());
      }

      // LabelNames will be null if all the labels selected for review is not in Cal Memory Labels
      if (CommonUtils.isNotEmpty(labelNames)) {
        invokeSSD(labelNames, featureValModSet, reviewParamSet, secReviewParamMap, secFeatureValueModelMap,
            secondaryCommonParamSet, secondaryComParamFeaValSet);
      }
      getReviewOutput().setAttrValModel(attrValModel);
      if ((getReviewOutput().getAttrValModel() != null) && (getReviewOutput().getCompliData() != null) &&
          (getReviewOutput().getCompliData().getAttrValueModSet() != null)) {
        getReviewOutput().getAttrValModel().addAll(getReviewOutput().getCompliData().getAttrValueModSet());
      }
    }
    catch (IcdmException exp) {
      paramSet.clear();
      if (null != getReviewOutput().getReviewFuncParamMap()) {
        getReviewOutput().getReviewFuncParamMap().clear();
      }
      CDMLogger.getInstance().error(exp.getMessage(), exp);
      // TODO Handle 1002
      if ("1002".equals(exp.getErrorCode())) {
        throw new IcdmException("SSD.FILE_CREATION_ERROR", exp, exp.getMessage());
      }
      getReviewOutput().setValueNotSetAttr(faAdapter.getValueNotSetAttr());
      throw exp;
    }
    return reviewParamSet;
  }

  /**
   * @param paramSet
   * @param faAdapter
   * @throws IcdmException
   */
  private void performCompliReview(final SortedSet<Parameter> paramSet, final FeatureAttributeAdapterNew faAdapter)
      throws IcdmException {
    try {
      this.compliReview = new CDRCompliReview(paramSet, faAdapter, getReviewOutput(), getReviewInputData(),
          getServiceData(), this.a2lFileContents);
      this.compliReview.performReview();
    }
    catch (IcdmException exp) {
      String errorMsg =
          "Review - COMPLI check failed :\n" + new ErrorCodeHandler(getServiceData()).getErrorMessage(exp);
      getLogger().error(errorMsg, exp);

      if (exp.getErrorCode().contains("FEAVAL")) {
        throw new IcdmException(errorMsg);
      }
    }
  }


  /**
   * Icdm-1215 - UI Changes for Review Attr Val
   *
   * @param featureValModel
   * @return
   */
  private Set<AttributeValueModel> getAttrValModel(final Set<FeatureValueModel> featureValModel) throws IcdmException {
    final FeatureAttributeAdapterNew faAdapter = new FeatureAttributeAdapterNew(getServiceData());

    HashSet<AttributeValueModel> hashSet = new HashSet<>(faAdapter.createAttrValModel(featureValModel).values());
    getReviewOutput().setValueNotSetAttr(faAdapter.getValueNotSetAttr());
    return hashSet;


  }

  /**
   * @param treeSet
   * @param faAdapter
   * @param labelNames
   * @param paramsToRemove
   * @return
   */
  private Set<FeatureValueModel> getFeatureValModel(final Set<IParameter> treeSet,
      final FeatureAttributeAdapterNew faAdapter, final Set<String> labelNames, final List<IParameter> paramsToRemove)
      throws IcdmException {


    Set<FeatureValueModel> feaValModelSet;
    if (CommonUtils.isNotNull(getReviewInputData().getPidcData().getSelPIDCVariantId())) {

      feaValModelSet = faAdapter.createAllFeaValModel(treeSet, (new PidcVariantLoader(getServiceData()))
          .getDataObjectByID(getReviewInputData().getPidcData().getSelPIDCVariantId()), labelNames, paramsToRemove);
    }
    else {

      feaValModelSet = faAdapter.createAllFeaValModel(treeSet,
          (new PidcVersionLoader(getServiceData())).getDataObjectByID(this.pidcA2l.getPidcVersId()));
    }

    return feaValModelSet;
  }

  /**
   * @param treeSet
   * @param labelNames
   * @param labelNames
   * @param paramNotInRuleset2
   * @return the parameters which are available only in the Cal data map.
   * @throws IcdmException
   */
  private Set<AbstractParameter> getReviewFuncParamSet(final SortedSet<Parameter> treeSet, final Set<String> labelNames,
      final Set<String> paramNotInRuleset, final Map<Long, Set<AbstractParameter>> secRuleSetParamMap,
      final Set<AbstractParameter> secondaryCommonParamSet)
      throws IcdmException {

    Set<AbstractParameter> secRuleSetParamTreeSet = new TreeSet<>();

    Map<String, CalData> calDataMap = getReviewOutput().getCalDataMap();
    RuleSetLoader ruleSetLoader = new RuleSetLoader(getServiceData());
    // Fetching RuleSetParameter for Secondary Rule SetIds
    if (CommonUtils.isNotNull(getReviewInputData().getRulesData().getSecondaryRuleSetIds())) {
      addReviewParamsForSecRuleSet(treeSet, secRuleSetParamMap, secRuleSetParamTreeSet, calDataMap, ruleSetLoader);
    }
    // check params for secondary common rules
    if (getReviewInputData().getRulesData().isCommonRulesSecondary()) {
      for (Parameter cdrFuncParameter : treeSet) {
        if (calDataMap.containsKey(cdrFuncParameter.getName())) {
          secondaryCommonParamSet.add(cdrFuncParameter);
        }
      }
    }
    Set<AbstractParameter> modifiedTreeSet = new TreeSet<>();
    if (CommonUtils.isNotNull(getReviewInputData().getRulesData().getPrimaryRuleSetId())) {
      addRvwParamForPrimRuleSet(treeSet, paramNotInRuleset, calDataMap, ruleSetLoader, modifiedTreeSet);
    }

    else {
      Set<Parameter> validFuncParamSet = new HashSet<>();
      for (Parameter cdrFuncParameter : treeSet) {

        if (calDataMap.containsKey(cdrFuncParameter.getName())) {
          modifiedTreeSet.add(cdrFuncParameter);
          validFuncParamSet.add(cdrFuncParameter);
        }
        else {
          labelNames.remove(cdrFuncParameter.getName());
        }
      }

    }
    return modifiedTreeSet;
  }

  /**
   * @param treeSet
   * @param paramNotInRuleset
   * @param calDataMap
   * @param ruleSetLoader
   * @param modifiedTreeSet
   * @throws DataException
   * @throws IcdmException
   */
  private void addRvwParamForPrimRuleSet(final SortedSet<Parameter> treeSet, final Set<String> paramNotInRuleset,
      final Map<String, CalData> calDataMap, final RuleSetLoader ruleSetLoader,
      final Set<AbstractParameter> modifiedTreeSet)
      throws IcdmException {
    RuleSet ruleSet = ruleSetLoader.getDataObjectByID(getReviewInputData().getRulesData().getPrimaryRuleSetId());
    RuleSetParameterLoader paramLoader = new RuleSetParameterLoader(getServiceData());
    Map<String, RuleSetParameter> allParameters = paramLoader.getAllRuleSetParams(ruleSet.getId());
    for (AbstractParameter cdrFuncParameter : treeSet) {
      String paramName = cdrFuncParameter.getName();

      // ICDM-2477
      if (!(allParameters.containsKey(paramName))) {
        paramNotInRuleset.add(paramName);
      }

      if (calDataMap.containsKey(paramName) && (allParameters.containsKey(paramName))) {
        modifiedTreeSet.add(allParameters.get(paramName));
      }
      else if (ApicUtil.isVariantCoded(paramName) && calDataMap.containsKey(paramName) &&
          (allParameters.containsKey(ApicUtil.getBaseParamName(paramName)))) {
        modifiedTreeSet.add(allParameters.get(ApicUtil.getBaseParamName(paramName)));
      }

    }
    if (!(this.isAllSelLblsNotInCalMemory) && modifiedTreeSet.isEmpty()) {
      throw new IcdmException("RULE_SET.INVALID_PARAMETER_SELECTED");
    }
  }

  /**
   * @param treeSet
   * @param secRuleSetParamMap
   * @param secRuleSetParamTreeSet
   * @param calDataMap
   * @param ruleSetLoader
   * @throws DataException
   * @throws IcdmException
   */
  private void addReviewParamsForSecRuleSet(final SortedSet<Parameter> treeSet,
      final Map<Long, Set<AbstractParameter>> secRuleSetParamMap, final Set<AbstractParameter> secRuleSetParamTreeSet,
      final Map<String, CalData> calDataMap, final RuleSetLoader ruleSetLoader)
      throws IcdmException {
    for (Long secRuleSetId : getReviewInputData().getRulesData().getSecondaryRuleSetIds()) {
      secRuleSetParamTreeSet.clear();
      RuleSet ruleSet = ruleSetLoader.getDataObjectByID(secRuleSetId);
      RuleSetParameterLoader paramLoader = new RuleSetParameterLoader(getServiceData());
      Map<String, RuleSetParameter> allParameters = paramLoader.getAllRuleSetParams(ruleSet.getId());
      for (AbstractParameter cdrFuncParameter : treeSet) {
        String paramName = cdrFuncParameter.getName();
        if (calDataMap.containsKey(paramName) && (allParameters.containsKey(paramName))) {
          secRuleSetParamTreeSet.add(allParameters.get(paramName));
        }
        else if (ApicUtil.isVariantCoded(paramName) && calDataMap.containsKey(paramName) &&
            (allParameters.containsKey(ApicUtil.getBaseParamName(paramName)))) {
          secRuleSetParamTreeSet.add(allParameters.get(ApicUtil.getBaseParamName(paramName)));
        }
      }
      secRuleSetParamMap.put(secRuleSetId, secRuleSetParamTreeSet);
      if (secRuleSetParamMap.isEmpty()) {
        throw new IcdmException("RULE_SET.INVALID_PARAMETER_SELECTED");
      }
    }
  }

  /**
   * Icdm-870 fill the char set
   *
   * @param functions
   * @param charSet
   * @param labelNames
   * @throws IcdmException
   */
  private void fillCharSet(final Set<Function> functions, final Set<String> charSet, final Set<String> labelNames,
      final Map<String, Set<Characteristic>> funcCharMap) {

    for (Function function : functions) {
      List<DefCharacteristic> defCharRefList = function.getDefCharRefList();
      Set<String> funcParamsSet = getReviewInputData().getFunctionMap().get(function.getName());
      Set<Characteristic> funCharSet = new HashSet<>();
      if (defCharRefList != null) {
        for (DefCharacteristic defCharacteristic : defCharRefList) {

          if ((funcParamsSet == null) || funcParamsSet.contains(defCharacteristic.getObj().getName())) {
            charSet.add(defCharacteristic.getObj().getName());
            funCharSet.add(defCharacteristic.getObj());
            labelNames.add(defCharacteristic.getObj().getName());
            funcCharMap.put(function.getName(), funCharSet);
          }
        }
      }
    }
    // to fill charset and funcCharMap for not assigned parameters
    fillCharMapForNotAssignedParams(charSet, funcCharMap);
  }

  /**
   * To fill charset and funcCharMap for not assigned parameters
   *
   * @param charSet
   * @param funcCharMap
   */
  private void fillCharMapForNotAssignedParams(final Set<String> charSet,
      final Map<String, Set<Characteristic>> funcCharMap) {
    SortedSet<Characteristic> characteristics = this.a2lFileContents.getAllSortedLabels(true);
    Set<Characteristic> funCharSet = new HashSet<>();
    if (CDRConstants.CDR_SOURCE_TYPE.getType(getReviewInputData().getSourceType()) == CDR_SOURCE_TYPE.WP) {
      Set<String> funcParamsSet = getReviewInputData().getFunctionMap().get(ApicConstants.NOT_ASSIGNED);
      fillCharSetForWpGroup(charSet, characteristics, funCharSet, funcParamsSet);
    }
    else {
      fillCharSetforOtherGroups(charSet, characteristics, funCharSet);
    }
    funcCharMap.put(ApicConstants.NOT_ASSIGNED, funCharSet);
  }

  /**
   * To fill charset and funcCharMap for not assigned parameters for Other groups
   *
   * @param charSet
   * @param characteristics
   * @param funCharSet
   */
  private void fillCharSetforOtherGroups(final Set<String> charSet, final SortedSet<Characteristic> characteristics,
      final Set<Characteristic> funCharSet) {
    for (Characteristic characteristic : characteristics) {
      for (String paramName : getReviewOutput().getUnassParaminReview()) {
        if (characteristic.getName().equals(paramName)) {
          charSet.add(characteristic.getName());
          funCharSet.add(characteristic);
        }
      }
    }
  }

  /**
   * To fill charset and funcCharMap for not assigned parameters for WP Groups
   *
   * @param charSet
   * @param characteristics
   * @param funCharSet
   * @param funcParamsSet
   */
  private void fillCharSetForWpGroup(final Set<String> charSet, final SortedSet<Characteristic> characteristics,
      final Set<Characteristic> funCharSet, final Set<String> funcParamsSet) {
    for (Characteristic characteristic : characteristics) {
      for (String paramName : getReviewOutput().getUnassParaminReview()) {
        if (funcParamsSet.contains(paramName) && characteristic.getName().equals(paramName)) {
          charSet.add(characteristic.getName());
          funCharSet.add(characteristic);
        }
      }
    }
  }


  /**
   * @param allFuncsList
   * @param functions
   */
  private void addFunctions(final SortedSet<Function> allFuncsList, final Set<Function> functions) {
    // Iterate the all function list of the a2l file and filter the required Functions and also fill the rules
    for (String funcName : getReviewInputData().getSelReviewFuncs()) {
      for (Function function : allFuncsList) {
        if (function.getName().equals(funcName)) {
          functions.add(function);
        }
      }
    }
  }

  /**
   * @param reviewFuncParamMap
   * @param modParamSet
   * @throws IcdmException
   */
  private void setErrorForEmptyParamSet(final Map<Long, Set<Parameter>> reviewFuncParamMap,
      final Set<AbstractParameter> modParamSet)
      throws IcdmException {
    if ((getReviewOutput().getUnassParaminReview().isEmpty()) && (reviewFuncParamMap.isEmpty() ||
        (!this.isAllSelLblsNotInCalMemory && CommonUtils.isNullOrEmpty(modParamSet)))) {
      throw new IcdmException("CDR.SELECTED_PARAM_NOT_IN_A2L");
    }
  }

  /**
   * @param reviewFuncsSet
   * @param reviewFuncParamMap
   * @param funcCharMap
   * @param fetchFuncParams
   */
  private void populateFuncParam(final SortedSet<com.bosch.caltool.icdm.model.a2l.Function> reviewFuncsSet,
      final Map<Long, Set<Parameter>> reviewFuncParamMap, final Map<String, Set<Characteristic>> funcCharMap,
      final Map<String, Parameter> fetchFuncParams) {
    for (com.bosch.caltool.icdm.model.a2l.Function cdrFun : reviewFuncsSet) {
      // funcCharMap contains key Function name and Value set of Char From A2l File.
      Set<Characteristic> set = funcCharMap.get(cdrFun.getName());
      if (set == null) {
        continue;
      }
      for (Characteristic charac : set) {
        // fetchFuncParams is the Cdr Func Params from DB- From tParameter table Map contaning values
        Parameter funcParFromMap = fetchFuncParams.get(charac.getName());
        // get the func Param set from the review Func Param map
        Set<Parameter> funcParSet = reviewFuncParamMap.get(cdrFun.getId());
        if (funcParSet == null) {
          funcParSet = new HashSet<>();
        }
        if (funcParFromMap != null) {
          funcParSet.add(funcParFromMap);
        }
        reviewFuncParamMap.put(cdrFun.getId(), funcParSet);

      }
    }
  }

  /**
   * @param a2lCharacterMap
   * @param funNames
   * @param label
   * @param charSet
   * @param dataLoader
   * @param labelNames
   * @param funcParamMap
   */
  private void fillFunLabNames(final Map<String, Characteristic> a2lCharacterMap, final Set<String> funNames,
      final String label, final Set<String> charSet, final Set<String> labelNames,
      final Map<String, Set<Characteristic>> funcParamMap) {
    Characteristic charObj = a2lCharacterMap.get(label);
    if (charObj != null) {
      Function func = charObj.getDefFunction();
      String funcName;
      if (func == null) {
        funNames.add(ApicConstants.NOT_ASSIGNED);
        funcName = ApicConstants.NOT_ASSIGNED;
        getReviewOutput().getUnassParaminReview().add(charObj.getName());
      }
      else {
        funcName = func.getName();
      }
      funNames.add(funcName);
      labelNames.add(label);
      charSet.add(charObj.getName());

      Set<Characteristic> set = funcParamMap.computeIfAbsent(funcName, k -> new HashSet<>());
      set.add(charObj);

      funcParamMap.put(funcName, set);
    }

  }

  /**
   * @param a2lFileContents
   * @param filesList
   * @param allParamsSelection
   * @return
   * @throws IcdmException
   */
  private Map<String, CalData> fetchCalDataMap(final boolean labFunSelection, final boolean allParamsSelection)
      throws IcdmException {

    Map<String, Map<String, CalData>> fileCalDataMap = new HashMap<>();
    Map<String, CalData> consolidatedCaldataMap = new ConcurrentHashMap<>();

    // Parsing all the input files selected
    for (Entry<String, byte[]> entry : this.filesStreamMap.entrySet()) {
      InputStream fileInputStream = new ByteArrayInputStream(entry.getValue());
      String filePath = entry.getKey().toUpperCase(Locale.getDefault());

      CALDATA_FILE_TYPE fileType = null;
      try {
        fileType = CALDATA_FILE_TYPE.getTypeFromFileNameNoEx(entry.getKey());
        if (fileType == null) {
          continue;
        }

        CaldataFileParserHandler parserHandler =
            new CaldataFileParserHandler(ParserLogger.getInstance(), this.a2lFileContents);
        Map<String, CalData> calDataMap = parserHandler.getCalDataObjects(fileType, fileInputStream);

        // set warnings map
        this.parserWarningsMap.putAll(parserHandler.getWarningsMap());

        fileCalDataMap.put(filePath, calDataMap);
        // ICDM 636
        consolidatedCaldataMap.putAll(calDataMap);
      }
      catch (IcdmException exp) {
        throw exp;
      }
      catch (Exception exp) {
        // Wrap other exceptions to an IcdmException
        CDMLogger.getInstance().error(exp.getMessage(), exp);
        throw new IcdmException(exp.getMessage(), exp);
      }
    }

    // Check if the parameters repeat in any of the multiple input files to be reviewed.If not then get the map of
    // caldata objects to be reviewed
    checkForAmbiguousParamaters(fileCalDataMap, labFunSelection, allParamsSelection);
    return consolidatedCaldataMap;
  }

  /**
   * @param fileCalDataMap Map of file name and Caldata Map
   * @param labFunSelection
   * @param allParamsSelection2
   * @return calDataReviewMap boolean added for checking the Radio Button selection
   * @throws IcdmException
   */
  private Map<String, CalData> checkForAmbiguousParamaters(final Map<String, Map<String, CalData>> fileCalDataMap,
      final boolean labFunSelection, final boolean allParamsSelection2)
      throws IcdmException {


    SortedSet<Function> allFuncsList = this.a2lFileContents.getAllSortedFunctions();
    SortedSet<Characteristic> allSortedLabels = this.a2lFileContents.getAllSortedLabels(true);
    SortedSet<Function> reviewFuncsList = new TreeSet<>();

    if (getReviewInputData().getSelReviewFuncs() != null) {
      addUnassignedParams(allFuncsList, allSortedLabels, reviewFuncsList);
    }

    Set<String> labelSet = new HashSet<>();

    if (!getReviewOutput().getUnassParaminReview().isEmpty()) {
      labelSet.addAll(getReviewOutput().getUnassParaminReview());
    }

    return getLabelsBasedOnFileType(labelSet, fileCalDataMap, reviewFuncsList, labFunSelection, allParamsSelection2);
  }

  /**
   * @param allFuncsList
   * @param allSortedLabels
   * @param reviewFuncsList
   */
  private void addUnassignedParams(final SortedSet<Function> allFuncsList,
      final SortedSet<Characteristic> allSortedLabels, final SortedSet<Function> reviewFuncsList) {
    // Getting the Function objects from the function name
    for (String funcName : getReviewInputData().getSelReviewFuncs()) {
      for (Function function : allFuncsList) {
        if (function.getName().equalsIgnoreCase(funcName)) {
          reviewFuncsList.add(function);
        }
        if (funcName.equalsIgnoreCase(ApicConstants.NOT_ASSIGNED)) {
          fillUnassignedParams(allSortedLabels);
        }
      }
    }
  }

  /**
   * @param allSortedLabels
   */
  private void fillUnassignedParams(final SortedSet<Characteristic> allSortedLabels) {
    for (Characteristic characteristic : allSortedLabels) {
      Function defFunction = characteristic.getDefFunction();
      if (defFunction == null) {
        getReviewOutput().getUnassParaminReview().add(characteristic.getName());
      }
    }
  }

  /**
   * @param reviewFuncsList
   * @param labFunSelection
   * @param allParamsSelection
   * @return
   * @throws IcdmException
   */
  private Map<String, CalData> getLabelsBasedOnFileType(final Set<String> labelSet,
      final Map<String, Map<String, CalData>> fileCalDataMap, final SortedSet<Function> reviewFuncsList,
      final boolean labFunSelection, final boolean allParamsSelection)
      throws IcdmException {

    // Checking whether the same parameter repeats in multiple files
    boolean paramRepeats;
    List<ParamRepeatExcelData> paramsRepeated = new ArrayList<>();
    Map<String, CalData> calDataReviewMap = new HashMap<>();
    IReviewParamResolver paramResolver;
    IReviewParamResolver inputParamResolver;
    ConcurrentMap<String, String> labelFunMap = null;
    this.labelList.clear();

    final boolean hexFileAvailable = checkForHexFile(getReviewInputData().getFileData().getSelFilesPath());
    inputParamResolver =
        new ReviewFileParamResolver(hexFileAvailable, getReviewInputData().getRulesData().getPrimaryRuleSetId(),
            this.a2lFileContents, getServiceData(), fileCalDataMap);

    // compli params
    if (isA2LCompliSelected(reviewFuncsList, labFunSelection)) {
      paramResolver = new CompliParamResolver(getServiceData());
      this.labelList.addAll(paramResolver.getParameters());
    }
    // Selecting the Labels from the Lab/Fun File
    else if (labFunSelection) {
      String labFunPath = getReviewInputData().getFileData().getFunLabFilePath();
      InputStream fileInputStream = new ByteArrayInputStream(this.filesStreamMap.get(labFunPath));
      paramResolver = new LabFunFileResolver(labFunPath, fileInputStream, this.a2lFileContents);
      List<String> paramList = paramResolver.getParameters();
      // if fun file doesnt have any labels and only functions , parse the functions
      if (labFunPath.toLowerCase(Locale.getDefault()).contains(".fun") && paramList.isEmpty()) {
        labelFunMap = new ConcurrentHashMap<>();
        paramResolver = new FunctionResolver(labelFunMap, reviewFuncsList);
      }
      else {
        this.labelList.addAll(paramList);
      }
    }

    // Selecting the Labels from the DCM, Hex, CDFX files etc
    else if (allParamsSelection) {
      paramResolver = inputParamResolver;
      this.labelList.addAll(paramResolver.getParameters());
    }
    // Workpackage/ A2LFunctions selected
    else {
      labelFunMap = new ConcurrentHashMap<>();
      paramResolver = new FunctionResolver(labelFunMap, reviewFuncsList);
    }

    if (this.labelList.isEmpty()) {
      labelSet.addAll(paramResolver.getParameters());
    }
    else {
      labelSet.addAll(this.labelList);
    }
    paramRepeats = fillCalDataRevMap(fileCalDataMap, calDataReviewMap, paramsRepeated, labelFunMap, labelSet);
    if (paramRepeats) {
      handleParamRepeatError(paramsRepeated);
    }

    Map<String, Characteristic> a2lCharMap = this.a2lFileContents.getAllModulesLabels();

    // Remove invalid Labels from LabelSet
    ParameterInput paramInput = new ParameterInput();
    paramInput.setLabelSet(labelSet);
    paramInput.setA2lLabelset(a2lCharMap.keySet());
    getInvalidLabels(paramInput, a2lCharMap).stream().forEach(labelSet::remove);

    Set<String> notInCalMemoryLabels = labelSet.stream()
        .filter(label -> CommonUtils.isNotNull(a2lCharMap.get(label)) && !(a2lCharMap.get(label).isInCalMemory()))
        .collect(Collectors.toSet());
    List<String> inputFileLabels = inputParamResolver.getParameters();
    boolean isAllSelParamsNotInInputFile = inputFileLabels.stream().noneMatch(labelSet::contains);
    this.isAllSelLblsNotInCalMemory = CommonUtils.isEqual(notInCalMemoryLabels, labelSet);

    if ((isAllSelParamsNotInInputFile && !this.isAllSelLblsNotInCalMemory) &&
        CommonUtils.isNullOrEmpty(calDataReviewMap.values())) {
      throw new IcdmException("CDR.PARAM_NOT_FOUND");
    }
    return calDataReviewMap;
  }

  public Set<String> getInvalidLabels(final ParameterInput parameterInput,
      final Map<String, Characteristic> a2lCharMap) {
    Set<String> invalidLabels =
        parameterInput.getLabelSet().stream().filter(label -> CommonUtils.isNull(a2lCharMap.get(label)) ||
            CommonUtils.isNull(a2lCharMap.get(label).getDefFunction())).collect(Collectors.toSet());
    invalidLabels.addAll(new ParameterLoader(getServiceData()).getMismatchLabelList(parameterInput.getLabelSet(),
        parameterInput.getA2lLabelset()));

    return invalidLabels;
  }

  /**
   * @param paramsRepeated
   * @throws IcdmException
   */
  private void handleParamRepeatError(final List<ParamRepeatExcelData> paramsRepeated) throws IcdmException {
    getReviewOutput().setParamsRepeated(paramsRepeated);
    // Added for the Export Excel Files
    String fileName = CDR_REPEATED_PARAMETERS;
    fileName = fileName.replaceAll("[^a-zA-Z0-9]+", "_");
    ParameterRepeatExport parameterRepeatExport = new ParameterRepeatExport();
    // 493963 - Excel file "CDR_Repeated_Parameters...xlsx" is created in the server root directory, not in the review
    // specific directory
    String currentDate = ApicUtil.getCurrentTime(DateFormat.DATE_FORMAT_20);
    File file = new File(DATARVW_ROOT_DIR_PATH);
    if (!file.exists()) {
      file.mkdir();
    }
    file = new File(file.getAbsoluteFile() + "\\review_" + currentDate);
    file.mkdir();
    String filepath = file.getAbsolutePath() + File.separator + fileName + java.time.Instant.now().toEpochMilli();
    try {
      filepath = parameterRepeatExport.exportParamRepeatInfo(paramsRepeated, filepath, "xlsx");
    }
    catch (IOException e) {
      throw new IcdmException(e.getLocalizedMessage(), e);
    }
    throw new IcdmException("CDR.REPEAT_PARAM_EXCEL_REPORT", filepath);
  }

  /**
   * @param reviewFuncsList
   * @param labFunSelection
   * @return
   */
  private boolean isA2LCompliSelected(final SortedSet<Function> reviewFuncsList, final boolean labFunSelection) {
    return isFuncA2lGrpNotPresent(reviewFuncsList, labFunSelection) && isGrpWpNameCompli();
  }

  /**
   * @param reviewFuncsList
   * @param labFunSelection
   * @return
   */
  private boolean isFuncA2lGrpNotPresent(final SortedSet<Function> reviewFuncsList, final boolean labFunSelection) {
    return reviewFuncsList.isEmpty() && !labFunSelection && (null == getReviewInputData().getA2lGroupName());
  }

  /**
   * @return
   */
  private boolean isGrpWpNameCompli() {
    return (getReviewOutput().getGrpWorkPackageName() != null) &&
        getReviewOutput().getGrpWorkPackageName().equals(CDRConstants.CDR_SOURCE_TYPE.COMPLI_PARAM.getUIType());
  }


  /**
   * @param fileCalDataMap
   * @param calDataReviewMap
   * @param paramsRepeated
   * @param labelFunMap
   * @param labels
   * @return New mehod refractor
   */
  private boolean fillCalDataRevMap(final Map<String, Map<String, CalData>> fileCalDataMap,
      final Map<String, CalData> calDataReviewMap, final List<ParamRepeatExcelData> paramsRepeated,
      final Map<String, String> labelFunMap, final Collection<String> labels) {


    boolean isRepeat = false;
    // ICDM-1720
    ConcurrentMap<String, String> paramFilesMap = new ConcurrentHashMap<>();
    for (String label : labels) {
      isRepeat = createParamRepeats(fileCalDataMap, calDataReviewMap, paramsRepeated, labelFunMap, isRepeat,
          paramFilesMap, label);
    }
    // ICDM-1720
    getReviewOutput().setParamFilesMap(paramFilesMap);
    return isRepeat;
  }

  /**
   * @param fileCalDataMap
   * @param calDataReviewMap
   * @param paramsRepeated
   * @param labelFunMap
   * @param isRepeat
   * @param paramFilesMap
   * @param label
   * @return
   */
  private boolean createParamRepeats(final Map<String, Map<String, CalData>> fileCalDataMap,
      final Map<String, CalData> calDataReviewMap, final List<ParamRepeatExcelData> paramsRepeated,
      final Map<String, String> labelFunMap, final boolean isRepeat, final ConcurrentMap<String, String> paramFilesMap,
      final String label) {
    int count;
    boolean isRepeated = isRepeat;
    String functionName = labelFunMap == null ? "" : labelFunMap.get(label);
    // iteretion for labels
    count = 0;
    CalData calDataObj = null;
    String reviewFileName = "";
    for (Entry<String, Map<String, CalData>> mapEntry : fileCalDataMap.entrySet()) {
      // iterations for caldata in files
      Map<String, CalData> calDataMap = mapEntry.getValue();
      if (calDataMap.containsKey(label)) {
        count++;
        calDataObj = calDataMap.get(label);
        if (count > SINGLE_PARAM_COUNT) {
          isRepeated = true;
          // ICDM 636
          // If the count is true , create excel data for the first file where the label was found
          if (count == PARAM_REPEAT_COUNT) {
            ParamRepeatExcelData excelData = new ParamRepeatExcelData(functionName, label, reviewFileName);
            paramsRepeated.add(excelData);
          }
          // create excel data with empty function name
          ParamRepeatExcelData excelData = new ParamRepeatExcelData(functionName, label, mapEntry.getKey());
          paramsRepeated.add(excelData);
        }
        reviewFileName = mapEntry.getKey();
      }
    }
    if ((count == 1) && (calDataObj != null)) {
      // ICDM-1720
      // add to the map for review
      paramFilesMap.put(label, reviewFileName);
      // put into map to be reviewed
      calDataReviewMap.put(label, calDataObj);
    }
    return isRepeated;
  }

  /**
   * Icdm-729 Check if there is a hex file aviable in the Selceted file path
   *
   * @param selFilesPath
   * @return
   */
  private boolean checkForHexFile(final Set<String> selFilesPath) {
    for (String string : selFilesPath) {
      if (string.toLowerCase(Locale.ENGLISH).contains(HEX_EXTENSION)) {
        return true;
      }
    }
    return false;
  }


}
