/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.FunctionLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.cdr.review.ReviewedInfo;
import com.bosch.caltool.icdm.bo.cdr.review.RuleUtility;
import com.bosch.caltool.icdm.bo.compli.CompliResultUtil;
import com.bosch.caltool.icdm.bo.shapereview.ShapeReviewParamResult;
import com.bosch.caltool.icdm.bo.util.CalDataComparisonWrapper;
import com.bosch.caltool.icdm.common.bo.cdr.ReviewRuleUtil;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.COMPLI_RESULT_FLAG;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.DELTA_REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.QSSD_RESULT_FLAG;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.RESULT_FLAG;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRResultFunction;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.RvwFile;
import com.bosch.caltool.icdm.model.cdr.review.ReviewInput;
import com.bosch.checkssd.reports.reportmodel.FormtdRptValModel;
import com.bosch.checkssd.reports.reportmodel.ReportModel;
import com.bosch.ssd.icdm.model.CDRRule;

/**
 * @author bru2cob
 */
class CDRResultParamCreator extends AbstractSimpleBusinessObject {

  /**
   *
   */
  private static final String CMT_NO_QSSD_RULE_AVAILABLE = "No QSSD Rule available";
  /**
   *
   */
  private static final String CMT_NO_COMPLI_RULE_AVAILABLE = "No Compliance Rule available";


  private static final String CMT_PARAM_NOT_IN_CALMEMORY = "Parameter not in CalMemory";

  /**
   * Error message
   */
  private static final String SAV_CALDATA_ERR = "Error saving Cal data for parameter";

  /**
   * String constant for compliance
   */
  private static final String COMPLIANCE = "COMPLIANCE";
  private final ReviewedInfo reviewInfo;
  private final ReviewInput reviewInputData;
  private final CDRReviewResult reviewResult;


  private int changeBitNum;
  private final TRvwResult resultEntity;
  /**
   * File name and the corresponding icdm file object
   */
  private Map<String, RvwFile> filesCreatedMap = new HashMap<>();
  private boolean deltaReviewValid;
  private final Map<String, Characteristic> charMap;

  /**
   * @param reviewInfo reviewInfo
   * @param reviewInputData reviewInputData
   * @param reviewResult reviewResult
   * @param reviewResultEntity reviewResultEntity
   * @param filesCreatedMap reviewFilesMap
   * @param serviceData serviceData
   */
  public CDRResultParamCreator(final ReviewedInfo reviewInfo, final ReviewInput reviewInputData,
      final CDRReviewResult reviewResult, final TRvwResult reviewResultEntity,
      final Map<String, RvwFile> filesCreatedMap, final ServiceData serviceData) {
    super(serviceData);
    this.reviewInfo = reviewInfo;
    this.reviewInputData = reviewInputData;
    this.reviewResult = reviewResult;

    this.resultEntity = reviewResultEntity;
    this.filesCreatedMap = filesCreatedMap;

    this.charMap = this.reviewInfo.getA2lFileContents() == null ? new HashMap<>()
        : this.reviewInfo.getA2lFileContents().getAllModulesLabels();

  }

  /**
   * @return the Cdr Result param Set
   * @throws IcdmException error while creating review parameter objects
   */
  public SortedSet<CDRResultParameter> createRvwParams() throws IcdmException {

    getLogger().debug("Preparing review parameters to be created");

    Map<Long, CDRResultParameter> projDeltaParamMap = new HashMap<>();

    // Fetch all the review functions for the result. The review params need review functions.
    CDRResultFunctionLoader funcLoader = new CDRResultFunctionLoader(getServiceData());
    Map<Long, CDRResultFunction> cdrResultFunctionsMap = funcLoader.getByResultObj(this.resultEntity);

    if ((this.reviewInputData.isDeltaReview()) &&
        this.reviewInputData.getDeltaReviewType().equals(DELTA_REVIEW_TYPE.PROJECT_DELTA_REVIEW.getDbType())) {
      projDeltaParamMap = createProjDelParamMap(cdrResultFunctionsMap);
      getLogger().debug("Parent parameters of Project Delta review. Count = {}", projDeltaParamMap.size());
    }

    SortedSet<CDRResultParameter> paramsToBeCreated = new TreeSet<>();
    if (this.reviewInfo.getReviewFuncParamMap() != null) {
      paramsToBeCreated = createParamsFromRvwFunc(cdrResultFunctionsMap, projDeltaParamMap);
    }
    getLogger().debug("Review parameters prepared. Count = {}", paramsToBeCreated.size());

    return paramsToBeCreated;
  }

  /**
   * @param cdrResultFunctionsMap
   * @param projDeltaParamMap
   * @return
   * @throws DataException
   * @throws CommandException
   * @throws ClassNotFoundException
   * @throws IOException
   */
  private SortedSet<CDRResultParameter> createParamsFromRvwFunc(
      final Map<Long, CDRResultFunction> cdrResultFunctionsMap, final Map<Long, CDRResultParameter> projDeltaParamMap)
      throws IcdmException {

    SortedSet<CDRResultParameter> paramsToBeCreated = new TreeSet<>();

    // Get the parent cdr map for delta review changes
    Map<Long, CDRResultParameter> parentCdrParamMap = new HashMap<>();
    if (this.reviewInputData.getResultData().getParentResultId() != null) {
      TRvwResult parentResEntity = new CDRReviewResultLoader(getServiceData())
          .getEntityObject(this.reviewInputData.getResultData().getParentResultId());
      parentCdrParamMap = new CDRResultParameterLoader(getServiceData()).getParamsByResultObj(parentResEntity);
    }

    for (CDRResultFunction cdrResultFunction : cdrResultFunctionsMap.values()) {
      // Get the Parameters corresponding to each function.
      Set<Parameter> funcParamSet = this.reviewInfo.getReviewFuncParamMap().get(cdrResultFunction.getFunctionId());
      // handle the case for Non relevant params
      if (funcParamSet == null) {
        funcParamSet = getParamSetForFunction(cdrResultFunction);
      }
      for (Parameter param : funcParamSet) {
        CheckSSDResultParam checkSSDResultParam = null;
        // ICDM-2440
        if (null != this.reviewInfo.getPimaryChkSSDResParamMap()) {
          checkSSDResultParam = this.reviewInfo.getPimaryChkSSDResParamMap().get(param.getName());
        }
        CheckSSDResultParam checkSSDCompliParam = null;

        if (null != this.reviewInfo.getCompliData().getCheckSSDCompliParamMap()) {
          checkSSDCompliParam = this.reviewInfo.getCompliData().getCheckSSDCompliParamMap().get(param.getName());
        }
        CDRResultParameter parentCDRParam = getParentCdrParam(parentCdrParamMap, projDeltaParamMap, param);
        // Create cdr Result param. Populate the data and return the result param Object.
        CDRResultParameter resultPram =
            createCdrResParam(cdrResultFunction, param, checkSSDResultParam, checkSSDCompliParam, parentCDRParam);
        paramsToBeCreated.add(resultPram);
      }
    }
    return paramsToBeCreated;
  }

  /**
   * @param param
   * @param qssdRule
   * @return
   * @throws CommandException
   */
  private CDRRule getQssdRuleForParam(final Parameter param) throws CommandException {
    CDRRule qssdRule = null;
    if (null != this.reviewInfo.getCompliData().getSsdRulesForQssd()) {
      qssdRule = CDRRuleUtil.assertSingleRule(this.reviewInfo.getCompliData().getSsdRulesForQssd(), param.getName());
    }
    return qssdRule;
  }

  /**
   * @param param
   * @param compliRule
   * @return
   * @throws CommandException
   */
  private CDRRule getcompliRuleForParam(final Parameter param) throws CommandException {

    CDRRule compliRule = null;
    if (null != this.reviewInfo.getCompliData().getSsdRulesForCompliance()) {
      compliRule =
          CDRRuleUtil.assertSingleRule(this.reviewInfo.getCompliData().getSsdRulesForCompliance(), param.getName());
    }
    return compliRule;
  }

  /**
   * @param param parameter
   * @return review rule
   * @throws CommandException
   */
  private ReviewRule getReviewRuleForParam(final Parameter param) throws CommandException {
    ReviewRule rule = null;
    if (null != this.reviewInfo.getPrimarySSDRules()) {
      rule = CDRRuleUtil.assertRule(this.reviewInfo.getPrimarySSDRules(), param.getName());
    }
    return rule;
  }

  /**
   * @param cdrFuncIDResultParamMap
   * @param parentParamsMap
   * @param param
   * @return
   */
  private CDRResultParameter getParentCdrParam(final Map<Long, CDRResultParameter> parentCdrParamMap,
      final Map<Long, CDRResultParameter> parentParamsMap, final Parameter param) {

    CDRResultParameter parentCDRParam = null;

    if (this.reviewInputData.isDeltaReview() &&
        this.reviewInputData.getDeltaReviewType().equals(DELTA_REVIEW_TYPE.DELTA_REVIEW.getDbType())) {
      // Delta review

      parentCDRParam = parentCdrParamMap.get(param.getId());
      if (!this.deltaReviewValid && (null != parentCDRParam)) {
        // if there is a parent cdr parameter for any parameter , then the review is a valid delta review
        this.deltaReviewValid = true;
      }
    }
    else {
      // Normal review

      parentCDRParam = parentParamsMap.get(param.getId());
      if (!this.deltaReviewValid && (null != parentCDRParam)) {
        // if there is a parent cdr parameter for any parameter , then the review is a valid delta review
        this.deltaReviewValid = true;
      }
    }

    return parentCDRParam;
  }

  /**
   * @param paramsToBeCreated
   * @param cdrResultFunction
   * @param param
   * @param rule
   * @param checkSSDResultParam
   * @param compliRule
   * @param qssdRule
   * @param checkSSDCompliParam
   * @param parentCDRParam
   * @return
   * @throws CommandException
   * @throws ClassNotFoundException
   * @throws IOException
   * @throws DataException
   */
  private CDRResultParameter createCdrResParam(final CDRResultFunction cdrResultFunction, final Parameter param,
      final CheckSSDResultParam checkSSDResultParam, final CheckSSDResultParam checkSSDCompliParam,
      final CDRResultParameter parentCDRParam)
      throws IcdmException {

    CDRResultParameter cdrResultParameter = new CDRResultParameter();

    ReviewRule rule = getReviewRuleForParam(param);

    // Default ref flag is null for 'no rule'
    String matchRefFlag = null;
    if (rule != null) {
      cdrResultParameter.setLowerLimit(rule.getLowerLimit());
      cdrResultParameter.setUpperLimit(rule.getUpperLimit());
      cdrResultParameter.setBitwiseLimit(rule.getBitWiseRule());
      cdrResultParameter.setHint(rule.getHint());
      // Ready For series
      cdrResultParameter.setRvwMethod(rule.getReviewMethod());
      cdrResultParameter.setMaturityLvl(rule.getMaturityLevel());
      // exact match
      matchRefFlag = rule.isDcm2ssd() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO;
    }
    cdrResultParameter.setMatchRefFlag(matchRefFlag);

    // If param is readonly in the input A2lFile then the value set as true
    cdrResultParameter.setReadOnlyParam(this.reviewInfo.getReadOnlyParamNameSet().contains(param.getName()));

    CalData calData = this.reviewInfo.getCalDataMap().get(param.getName());
    cdrResultParameter.setCheckedValue(convertCalDataToZippedByteArr(calData));

    setCheckUnit(checkSSDResultParam, cdrResultParameter, calData);

    CDRRule compliRule = getcompliRuleForParam(param);

    CDRRule qssdRule = getQssdRuleForParam(param);

    setResultForReviewParam(param, compliRule, qssdRule, checkSSDCompliParam, cdrResultParameter);

    cdrResultParameter.setFuncName(cdrResultFunction.getName());

    cdrResultParameter.setIsbitwise(param.getIsBitWise() == null ? "N" : param.getIsBitWise());
    setLabObjRevId(rule, compliRule, qssdRule, cdrResultParameter);


    cdrResultParameter.setName(param.getName());
    cdrResultParameter.setParamId(param.getId());

    if ((null != parentCDRParam) &&
        this.reviewInputData.getDeltaReviewType().equals(DELTA_REVIEW_TYPE.PROJECT_DELTA_REVIEW.getDbType())) {
      cdrResultParameter.setParentParamId(parentCDRParam.getId());
    }
    setRvwRespId(param, cdrResultParameter);

    cdrResultParameter.setpType(param.getType());
    cdrResultParameter.setRefUnit(rule == null ? null : rule.getUnit());
    cdrResultParameter.setRefValue(getRefValue(rule, param));
    String resultFlag = getResultFlag(checkSSDResultParam, rule);
    if (!resultFlag.isEmpty()) {
      cdrResultParameter.setResult(resultFlag);
    }
    cdrResultParameter.setResultId(this.resultEntity.getResultId());


    if (this.reviewInputData.isDeltaReview() && (null != parentCDRParam)) {
      cdrResultParameter.setRvwComment(parentCDRParam.getRvwComment());
    }
    setRvwFileId(param, cdrResultParameter);
    cdrResultParameter.setRvwFunId(cdrResultFunction.getId());
    this.changeBitNum = new ChangeBitEvaluator().findChangeBit(parentCDRParam, cdrResultParameter, checkSSDResultParam,
        calData, this.charMap);
    setReviewScore(parentCDRParam, cdrResultParameter, param, rule, cdrResultParameter.getResult());
    this.changeBitNum =
        new ChangeBitEvaluator().setRvwScoreChangeBit(parentCDRParam, cdrResultParameter, this.changeBitNum);
    // set change flag using the change bit num.
    cdrResultParameter.setChangeFlag(Long.parseLong(String.valueOf(this.changeBitNum)));

    setArcReleasedFlag(cdrResultParameter, parentCDRParam);
    return cdrResultParameter;
  }

  /**
   * 1. Take Over the ARC Released Flag <br>
   * 2. Reset the ARC Released Flag when score is reset to 0, 8 or 9
   *
   * @param cdrResultParameter
   * @param parentCDRParam
   */
  private void setArcReleasedFlag(final CDRResultParameter cdrResultParameter,
      final CDRResultParameter parentCDRParam) {
    // By Default, ARC Released Flag is False
    boolean arcReleasedFlag = false;

    // Take Over the ARC Released flag when score is not modified to 0,8,9
    DATA_REVIEW_SCORE reviewScore = DATA_REVIEW_SCORE.getType(cdrResultParameter.getReviewScore());
    if ((parentCDRParam != null) && (CommonUtils.isNotEqual(reviewScore, DATA_REVIEW_SCORE.S_0) &&
        CommonUtils.isNotEqual(reviewScore, DATA_REVIEW_SCORE.S_8) &&
        CommonUtils.isNotEqual(reviewScore, DATA_REVIEW_SCORE.S_9))) {
      arcReleasedFlag = parentCDRParam.getArcReleasedFlag();
    }

    cdrResultParameter.setArcReleasedFlag(arcReleasedFlag);
  }

  /**
   * @param checkSSDResultParam
   * @param cdrResultParameter
   * @param calData
   */
  private void setCheckUnit(final CheckSSDResultParam checkSSDResultParam, final CDRResultParameter cdrResultParameter,
      final CalData calData) {
    if (checkSSDResultParam == null) {
      // For a compliance parameter, if a normal rule is not present, get check unit from caldata
      if ((null != calData) && (null != calData.getCalDataPhy())) {
        cdrResultParameter.setCheckUnit(calData.getCalDataPhy().getUnit());
      }

    }
    else {
      cdrResultParameter.setCheckUnit(checkSSDResultParam.getUnit());
    }
  }

  /**
   * @param cdrFuncParameter
   * @param cdrResultParameter
   */
  private void setRvwFileId(final Parameter cdrFuncParameter, final CDRResultParameter cdrResultParameter) {
    if ((null != this.reviewInfo.getParamFilesMap()) &&
        (null != this.reviewInfo.getParamFilesMap().get(cdrFuncParameter.getName()))) {
      cdrResultParameter.setRvwFileId(
          this.filesCreatedMap.get(this.reviewInfo.getParamFilesMap().get(cdrFuncParameter.getName())).getId());
    }
  }

  /**
   * @param cdrFuncParameter
   * @param compliRule
   * @param qssdRule
   * @param checkSSDCompliParam
   * @param cdrResultParameter
   */
  private void setResultForReviewParam(final Parameter cdrFuncParameter, final CDRRule compliRule,
      final CDRRule qssdRule, final CheckSSDResultParam checkSSDCompliParam,
      final CDRResultParameter cdrResultParameter) {
    if (COMPLIANCE.equalsIgnoreCase(cdrFuncParameter.getSsdClass())) {
      cdrResultParameter.setCompliResult(getCompliResultFlag(compliRule, checkSSDCompliParam, cdrFuncParameter));
    }
    if (cdrFuncParameter.isQssdFlag()) {
      cdrResultParameter.setQssdResult(getQssdResultFlag(qssdRule, checkSSDCompliParam, cdrFuncParameter));
    }

    // Set the secondary rseult type to db.
    if (this.reviewInfo.getSecResultMap() != null) {
      RESULT_FLAG resultFlagEnum = this.reviewInfo.getSecResultMap().get(cdrFuncParameter.getName());
      if (resultFlagEnum != null) {
        cdrResultParameter.setSecondaryResult(resultFlagEnum.getDbType());
      }
    }
    ShapeReviewParamResult shapeParam =
        this.reviewInfo.getSrResult().getParamResultMap().get(cdrFuncParameter.getName());
    if (null != shapeParam) {
      cdrResultParameter.setSrErrorDetails(shapeParam.getErrorDetails());
      cdrResultParameter.setSrResult(shapeParam.getResult());
    }
  }


  /**
   * @param cdrFuncParameter
   * @param cdrResultParameter
   */
  private void setRvwRespId(final Parameter cdrFuncParameter, final CDRResultParameter cdrResultParameter) {
    // Adding RVWWpResp id to RVW Parameters
    if (this.reviewInfo.getRvwParamAndWpRespModelMap().containsKey(cdrFuncParameter.getId()) &&
        this.reviewInfo.getRvwWpRespModelAndRvwWPRespIdMap()
            .containsKey(this.reviewInfo.getRvwParamAndWpRespModelMap().get(cdrFuncParameter.getId()))) {
      cdrResultParameter.setRvwWpRespId(this.reviewInfo.getRvwWpRespModelAndRvwWPRespIdMap()
          .get(this.reviewInfo.getRvwParamAndWpRespModelMap().get(cdrFuncParameter.getId())));
    }
  }

  /**
   * @param rule
   * @param compliRule
   * @param qssdRule
   * @param cdrResultParameter
   */
  private void setLabObjRevId(final ReviewRule rule, final CDRRule compliRule, final CDRRule qssdRule,
      final CDRResultParameter cdrResultParameter) {
    if (rule != null) {
      cdrResultParameter.setLabObjId(rule.getRuleId());
      cdrResultParameter.setRevId(rule.getRevId());
    }
    if (compliRule != null) {
      cdrResultParameter.setCompliLabObjId(compliRule.getRuleId());
      cdrResultParameter.setCompliRevId(compliRule.getRevId());
    }
    if (qssdRule != null) {
      cdrResultParameter.setQssdLabObjId(qssdRule.getRuleId());
      cdrResultParameter.setQssdRevId(qssdRule.getRevId());
    }
  }

  /**
   * @param cdrResultFunctionsMap
   * @return
   * @throws DataException
   */
  private Map<Long, CDRResultParameter> createProjDelParamMap(final Map<Long, CDRResultFunction> cdrResultFunctionsMap)
      throws DataException {
    Map<Long, CDRResultParameter> parentParamsMap;


    // Create the ParentRvwParamLoaderInput object and fetch the Project delta param map
    ParentRvwParamLoaderInput parentParamLoadInput = new ParentRvwParamLoaderInput();
    PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());
    PidcVersion sourcePidcVers =
        pidcVersionLoader.getDataObjectByID(this.reviewInputData.getPidcData().getSourcePidcVerId());

    if (sourcePidcVers == null) {
      parentParamLoadInput.setProjectId(this.reviewInfo.getPidcDetails().getPidc().getId());
    }
    else {
      parentParamLoadInput.setProjectId(sourcePidcVers.getPidcId());
    }


    if (CommonUtils.isNotNull(this.reviewInputData.getPidcData().getSourcePIDCVariantId())) {
      parentParamLoadInput.setVariantId(this.reviewInputData.getPidcData().getSourcePIDCVariantId());
    }
    else if (CommonUtils.isNotNull(this.reviewInputData.getPidcData().getSelPIDCVariantId())) {
      parentParamLoadInput.setVariantId(this.reviewInputData.getPidcData().getSelPIDCVariantId());
    }

    parentParamLoadInput.setConsiderOfficial(this.reviewInputData.getResultData().isOffReviewType());
    parentParamLoadInput.setConsiderOfficialLockd(this.reviewInputData.getResultData().isOnlyLockedOffReview());
    parentParamLoadInput.setConsiderStart(this.reviewInputData.getResultData().isStartReviewType());
    parentParamLoadInput.setConsiderStartLocked(this.reviewInputData.getResultData().isOnlyLockedStartResults());
    Set<Parameter> cdrFuncParametersSet = new TreeSet<>();
    for (CDRResultFunction cdrResultFunction : cdrResultFunctionsMap.values()) {
      Set<Parameter> paramSet = this.reviewInfo.getReviewFuncParamMap().get(cdrResultFunction.getFunctionId());
      // handle the case for Non relevant params
      if (paramSet == null) {
        paramSet = getParamSetForFunction(cdrResultFunction);
      }
      cdrFuncParametersSet.addAll(paramSet);
    }
    parentParamLoadInput.setParamSet(cdrFuncParametersSet);
    ParentRvwParameterLoader parentRvwLoader = new ParentRvwParameterLoader(getServiceData());
    parentParamsMap = parentRvwLoader.fetchParentParameters(parentParamLoadInput);
    return parentParamsMap;
  }


  /**
   * @return
   */
  private boolean isRulechanged() {
    return this.changeBitNum != 0;
  }


  /**
   * @param dbRvwParam
   */
  private void setCommentsForNoRuleCompli(final CDRResultParameter cdrResultParameter) {

    // set review comment if there is no compliance rule. Method renamed to set score
    if ((this.reviewInfo.getCompliData().getParamsWithNoRules() != null) &&
        this.reviewInfo.getCompliData().getParamsWithNoRules().contains(cdrResultParameter.getName())) {
      cdrResultParameter.setRvwComment(CMT_NO_COMPLI_RULE_AVAILABLE);
    }

  }

  /**
   * @param dbRvwParam
   */
  private void setCommentsForNoQssdRule(final CDRResultParameter cdrResultParameter) {

    // set review comment if there is no compliance rule
    if ((this.reviewInfo.getCompliData().getQssdParamsWithNoRules() != null) &&
        this.reviewInfo.getCompliData().getQssdParamsWithNoRules().contains(cdrResultParameter.getName())) {
      cdrResultParameter.setRvwComment(CMT_NO_QSSD_RULE_AVAILABLE);
    }

  }

  /**
   * @param dbRvwParam
   * @param reviewResult
   * @param compliResult
   * @param qssdResult
   * @throws IOException
   * @throws DataException
   * @throws ClassNotFoundException
   */
  private void setReviewScore(final CDRResultParameter parentResultParam, final CDRResultParameter resultParam,
      final Parameter param, final ReviewRule reviewRule, final String reviewResult)
      throws IcdmException {

    // Set the score to 0 initially
    String score = null;


    // For normal review
    if (parentResultParam == null) {
      score = findScoreForNormalReview(resultParam, param, reviewRule, reviewResult);
    }
    // For delta review
    else {
      score = findScoreForDeltaReview(parentResultParam, resultParam, param, reviewRule, reviewResult);
    }

    resultParam.setReviewScore(score);

  }

  /**
   * @param parentResultParam
   * @param resultParam
   * @param param
   * @param reviewRule
   * @param rvwResult
   * @param qssdResult
   * @param compliResult
   * @param reviewScore
   * @param currentResult
   * @param shapeResult
   * @return
   * @throws IcdmException
   */
  private String findScoreForDeltaReview(final CDRResultParameter parentResultParam,
      final CDRResultParameter resultParam, final Parameter param, final ReviewRule reviewRule, final String rvwResult)
      throws IcdmException {

    String reviewScore = DATA_REVIEW_SCORE.S_0.getDbType();

    ShapeReviewParamResult shapeResult = this.reviewInfo.getSrResult().getParamResultMap().get(param.getName());

    // Check for Shape and param not in cal Memory.
    if (isShapeCheckFailed(shapeResult)) {
      reviewScore = DATA_REVIEW_SCORE.S_0.getDbType();
    }

    // If not in cal memory and not in review data file set score to 8 and set comments.
    else if (isParamNotInCalMem(param)) {
      reviewScore = DATA_REVIEW_SCORE.S_8.getDbType();
      resultParam.setRvwComment(CDRResultParamCreator.CMT_PARAM_NOT_IN_CALMEMORY);
    }

    else {
      // If ready for series and result is ok and parent is 0 then set score to 8. Here the Rule or Check Val is
      // changed.
      if ((ReviewRuleUtil.isRuleValid(reviewRule) && ReviewRuleUtil.isReadyForSeries(reviewRule) &&
          isResultOk(rvwResult) &&
          CommonUtils.isEqual(DATA_REVIEW_SCORE.S_0.getDbType(), parentResultParam.getReviewScore()))) {
        reviewScore = DATA_REVIEW_SCORE.S_8.getDbType();
      }
      boolean compliQssdPass = true;
      // Change the score in case of Compli case params. Even if series stat then set to 0 incase of Compli failure.
      if (isCompliOrQssd(param)) {
        compliQssdPass = isCompliQssdPass(resultParam, param);
      }

      // If not Compli / Qssd or Compli Qssd passed
      if (compliQssdPass) {
        reviewScore =
            findScoreForDeltaWithCopyParentScore(parentResultParam, param, reviewRule, rvwResult, reviewScore);
      }
      // compli or Q-ssd faliure
      else {
        reviewScore = DATA_REVIEW_SCORE.S_0.getDbType();
      }
      // Labels that are commented as ’Label not relevant for current release, still to be calibrated’
      // parent and child review score should be equal and child review score != 8
      // Set to score 3[25%] again in a new Delta Review automatically by the tool
      if (CommonUtils.isEqualIgnoreCase(parentResultParam.getRvwComment(), ApicConstants.LABEL_NOT_RELEVANT_COMMENT) &&
          CommonUtils.isEqual(reviewScore, parentResultParam.getReviewScore()) &&
          CommonUtils.isNotEqual(DATA_REVIEW_SCORE.S_8.getDbType(), reviewScore)) {
        reviewScore = DATA_REVIEW_SCORE.S_3.getDbType();
      }
    }
    return reviewScore;
  }

  /**
   * @param parentResultParam
   * @param cdrResultParameter
   * @param param
   * @param reviewRule
   * @param rvwResult
   * @param currentResult
   * @param reviewScore
   * @return
   * @throws IcdmException
   */
  private String findScoreForDeltaWithCopyParentScore(final CDRResultParameter parentResultParam, final Parameter param,
      final ReviewRule reviewRule, final String rvwResult, final String currentScore)
      throws IcdmException {

    String newScore = currentScore;

    if (canCopyParentScore(parentResultParam, rvwResult, this.reviewInfo.getCalDataMap().get(param.getName()))) {

      // If the series stat is false then set the score as 0 if the score is 8. (Here the parent the result is ok with
      // ready for series.)
      if (parentResultParam.getReviewScore().equals(DATA_REVIEW_SCORE.S_8.getDbType()) &&
          (!ReviewRuleUtil.isReadyForSeries(reviewRule) || !isResultOk(rvwResult))) {
        newScore = DATA_REVIEW_SCORE.S_0.getDbType();
      }
      // If any other value set then take it as it is from parent.
      else {
        newScore = setResultForTestStartDelta(parentResultParam, newScore);
      }


    }
    // If we cannot copy then keep it as 0.
    else {
      newScore = DATA_REVIEW_SCORE.S_0.getDbType();
    }

    return newScore;
  }

  /**
   * @param param
   * @return
   */
  private boolean isCompliOrQssd(final Parameter param) {
    return COMPLIANCE.equals(param.getSsdClass()) || param.isQssdFlag();
  }

  /**
   * @param resultParam
   * @param param
   * @param reviewRule
   * @param rvwResult
   * @param compliResult
   * @param qssdResult
   * @param reviewScore
   * @param rvwComment
   * @param shapeResult
   * @return
   */
  private String findScoreForNormalReview(final CDRResultParameter resultParam, final Parameter param,
      final ReviewRule reviewRule, final String rvwResult) {

    String score = DATA_REVIEW_SCORE.S_0.getDbType();

    ShapeReviewParamResult shapeResult = this.reviewInfo.getSrResult().getParamResultMap().get(param.getName());


    // Set Comments for compli
    if ((COMPLIANCE.equalsIgnoreCase(param.getSsdClass())) && (CDRConstants.COMPLI_RESULT_FLAG
        .getType(resultParam.getCompliResult()) == CDRConstants.COMPLI_RESULT_FLAG.NO_RULE)) {
      setCommentsForNoRuleCompli(resultParam);
    }
    // Set score to 0 in case of compliance rule deviation
    else if (((COMPLIANCE.equalsIgnoreCase(param.getSsdClass())) &&
        ((resultParam.getCompliResult() == null) || (CDRConstants.COMPLI_RESULT_FLAG
            .getType(resultParam.getCompliResult()) == CDRConstants.COMPLI_RESULT_FLAG.CSSD))) ||
        (CDRConstants.COMPLI_RESULT_FLAG
            .getType(resultParam.getCompliResult()) == CDRConstants.COMPLI_RESULT_FLAG.SSD2RV)) {
      score = DATA_REVIEW_SCORE.S_0.getDbType();
    }
    // Qssd Faliure Comments
    else if (param.isQssdFlag() &&
        (CDRConstants.QSSD_RESULT_FLAG.getType(resultParam.getQssdResult()) == CDRConstants.QSSD_RESULT_FLAG.NO_RULE)) {
      setCommentsForNoQssdRule(resultParam);
    }
    // Set score to 0 in case of quality rule deviation
    else if (param.isQssdFlag() && ((resultParam.getQssdResult() == null) ||
        (CDRConstants.QSSD_RESULT_FLAG.getType(resultParam.getQssdResult()) == CDRConstants.QSSD_RESULT_FLAG.QSSD))) {
      score = DATA_REVIEW_SCORE.S_0.getDbType();
    }
    // for shape review
    else if (isShapeCheckFailed(shapeResult)) {
      score = DATA_REVIEW_SCORE.S_0.getDbType();
    }
    // not in cal memory and param not in review data file , set score to 8 and set comments.
    else if (isParamNotInCalMem(param)) {
      score = DATA_REVIEW_SCORE.S_8.getDbType();
      // set review comment
      resultParam.setRvwComment(CDRResultParamCreator.CMT_PARAM_NOT_IN_CALMEMORY);
    }
    else {
      // TODO - To move isRuleValid and isReadyForSeries in ReviewRuleUtil. (Make this common for Rules editor and
      // review process.)
      if (ReviewRuleUtil.isRuleValid(reviewRule) && ReviewRuleUtil.isReadyForSeries(reviewRule) &&
          isResultOk(rvwResult)) {
        score = DATA_REVIEW_SCORE.S_8.getDbType();
      }
    }

    return score;

  }

  /**
   * @param param
   * @return
   */
  private boolean isParamNotInCalMem(final Parameter param) {
    return (this.charMap != null) && !this.charMap.get(param.getName()).isInCalMemory() &&
        (this.reviewInfo.getCalDataMap().get(param.getName()) == null);
  }

  /**
   * @param shapeResult
   * @return
   */
  private boolean isShapeCheckFailed(final ShapeReviewParamResult shapeResult) {
    return (null != shapeResult) && CommonUtilConstants.CODE_FAILED.equals(shapeResult.getResult());
  }

  /**
   * @param parentResultParam
   * @param resultParam
   * @param param
   * @param reviewScore
   * @param currentResult
   * @return
   */
  private boolean isCompliQssdPass(final CDRResultParameter resultParam, final Parameter param) {

    boolean compliQssdPassed = true;
    if (COMPLIANCE.equals(param.getSsdClass())) {
      COMPLI_RESULT_FLAG compliResult = CDRConstants.COMPLI_RESULT_FLAG.getType(resultParam.getCompliResult());
      // If compli Result is not available. or any compli Failure. Set the score to zero
      if ((compliResult == null) || (compliResult == CDRConstants.COMPLI_RESULT_FLAG.NO_RULE) ||
          (compliResult == CDRConstants.COMPLI_RESULT_FLAG.CSSD) ||
          (compliResult == CDRConstants.COMPLI_RESULT_FLAG.SSD2RV)) {
        compliQssdPassed = false;
      }
    }

    if (compliQssdPassed && param.isQssdFlag()) {
      QSSD_RESULT_FLAG qssdResult = CDRConstants.QSSD_RESULT_FLAG.getType(resultParam.getQssdResult());
      // qssd Failure case then score is 0.
      if (CDRConstants.QSSD_RESULT_FLAG.QSSD == qssdResult) {
        compliQssdPassed = false;
      }

    }
    return compliQssdPassed;
  }

  /**
   * @param parentResultParam
   * @param currentResult
   * @param reviewScore2
   * @param reviewScore
   * @return
   */
  private String setResultForTestStartDelta(final CDRResultParameter parentResultParam, final String currentScore) {

    String newScore = currentScore;

    // If the ready for series and result is ok , score must be 8..
    // Part of task 571805.
    if (!currentScore.equals(DATA_REVIEW_SCORE.S_8.getDbType())) {

      // For start and test review , if parent score is 9 then set it to 7.
      if ((REVIEW_TYPE.START.getDbType().equals(this.reviewResult.getReviewType()) ||
          REVIEW_TYPE.TEST.getDbType().equals(this.reviewResult.getReviewType())) &&
          (parentResultParam.getReviewScore().equals(DATA_REVIEW_SCORE.S_9.getDbType()))) {

        newScore = DATA_REVIEW_SCORE.S_7.getDbType();

      }
      else {
        newScore = parentResultParam.getReviewScore();
      }

    }

    return newScore;
  }


  /**
   * @param result
   * @return
   */
  private boolean isResultOk(final String result) {
    return (result != null) && result.equals(CDRConstants.RESULT_FLAG.OK.getDbType());
  }

  /**
   * @param result
   * @param cdrResultParameter
   * @return
   * @throws IcdmException
   */
  private boolean canCopyParentScore(final CDRResultParameter parantResultParam, final String result,
      final CalData calData)
      throws IcdmException {


    CDRReviewResult parentResult =
        new CDRReviewResultLoader(getServiceData()).getDataObjectByID(parantResultParam.getResultId());
    boolean currentResultStartoOrOff = REVIEW_TYPE.OFFICIAL.getDbType().equals(this.reviewResult.getReviewType()) ||
        REVIEW_TYPE.START.getDbType().equals(this.reviewResult.getReviewType());

    // for project level delta reviews parent cannot be from TEST type results (condition included to avoid NPE)
    // if parent review type is test and the delta review type is either official or start then cannot copy.
    if ((!DELTA_REVIEW_TYPE.PROJECT_DELTA_REVIEW.getDbType().equals(this.reviewInputData.getDeltaReviewType())) &&
        (REVIEW_TYPE.TEST.getDbType().equals(parentResult.getReviewType())) && currentResultStartoOrOff) {
      return false;
    }
    Characteristic characteristic = this.charMap.get(parantResultParam.getName());
    // Compare cal data obj used in parent and current review.
    boolean calDataEql = new CalDataComparisonWrapper(characteristic)
        .isEqual(CalDataUtil.getCalDataObjIEx(parantResultParam.getCheckedValue()), calData);
    // If there is no rule change and check value change then copy the parent score
    // If rule changes , check value is same and result is OK then copy the parent score
    if (checkNoRuleChange(calDataEql) || checkRuleChange(calDataEql, result, parantResultParam)) {
      return true;
    }

    // can copy if no rule changes , result is not ok and score is not 7,8,9
    return checkNoRuleChange(calDataEql) && !CDRConstants.RESULT_FLAG.OK.getDbType().equals(result) &&
        isNotMaxScore(parantResultParam);

  }

  /**
   * @param parantResultParam
   * @return true if score is not 7, 8 or 9.
   */
  private boolean isNotMaxScore(final CDRResultParameter parantResultParam) {
    return (!DATA_REVIEW_SCORE.S_7.getDbType().equals(parantResultParam.getReviewScore())) &&
        (!DATA_REVIEW_SCORE.S_8.getDbType().equals(parantResultParam.getReviewScore())) &&
        (!DATA_REVIEW_SCORE.S_9.getDbType().equals(parantResultParam.getReviewScore()));
  }

  /**
   * @param calDataeql flag to indicate whether checked value is equal
   * @param result ,result at delta review parameter
   * @param parantResultParam CDRResultParameter
   * @return
   */
  private boolean checkRuleChange(final boolean calDataeql, final String result,
      final CDRResultParameter parantResultParam) {
    // 637986: Labels are shown as changed that have not been changed
    return isRulechanged() && calDataeql && compareDeltaRvwResult(result, parantResultParam);
  }

  // 641772: If the result switches from any value to OK, the score must not be reset
  private boolean compareDeltaRvwResult(final String result, final CDRResultParameter parantResultParam) {
    return Objects.equals(result, parantResultParam.getResult()) ||
        CDRConstants.RESULT_FLAG.OK.getDbType().equals(result);
  }

  /**
   * @param compResult
   * @return
   */
  private boolean checkNoRuleChange(final boolean calDataEql) {
    return !isRulechanged() && calDataEql;
  }

  /**
   * @param cdrFuncParameter
   * @return
   */
  private String getCompliResultFlag(final CDRRule cdrRule, final CheckSSDResultParam checkSSDCompliParam,
      final Parameter cdrFuncParameter) {
    boolean isSkippedParam = false;
    if (this.reviewInfo.getCompliData().getSkippedParamsList() != null) {
      isSkippedParam = this.reviewInfo.getCompliData().getSkippedParamsList().contains(cdrFuncParameter.getName());
    }
    if (COMPLIANCE.equalsIgnoreCase(cdrFuncParameter.getSsdClass()) || (checkSSDCompliParam != null)) {
      // default
      CDRConstants.COMPLI_RESULT_FLAG result = CompliResultUtil.getCompliResult(cdrRule, checkSSDCompliParam,
          this.reviewInfo.getCompliData().isErrorinSSDFile(), cdrFuncParameter.getName(),
          COMPLIANCE.equalsIgnoreCase(cdrFuncParameter.getSsdClass()), isSkippedParam);

      return result.getDbType();
    }
    return null;

  }

  /**
   * @param cdrFuncParameter
   * @return
   */
  private String getQssdResultFlag(final CDRRule qssdRule, final CheckSSDResultParam checkSSDCompliParam,
      final Parameter cdrFuncParameter) {
    if (cdrFuncParameter.isQssdFlag() || (checkSSDCompliParam != null)) {
      // default
      CDRConstants.QSSD_RESULT_FLAG result = CompliResultUtil.getQssdResult(qssdRule, checkSSDCompliParam,
          this.reviewInfo.getCompliData().isErrorinSSDFile(), cdrFuncParameter.getName(),
          cdrFuncParameter.isQssdFlag());
      return result.getDbType();
    }
    return null;

  }

  /**
   * @param result
   * @return
   */
  private String getResultFlag(final CheckSSDResultParam checkSSDResultParam, final ReviewRule reviewRule) {

    // by default, result is not reviewed
    CDRConstants.RESULT_FLAG result = CDRConstants.RESULT_FLAG.NOT_REVIEWED;
    RuleUtility ruleUtility = new RuleUtility();
    // get the result from check Ssd Report model.
    if (checkSSDResultParam != null) {
      ReportModel checkSsdRptModel = checkSSDResultParam.getCompliReportModel();
      if (checkSsdRptModel != null) {
        if (checkSsdRptModel.isRuleOk()) {
          result = CDRConstants.RESULT_FLAG.OK;
        }
        else {
          result = getResFlagFromRptModel(checkSsdRptModel);
        }
      }
      // If the report model is not available and the rule is complete then the Result is not ok
      else if (ruleUtility.isRuleComplete(reviewRule)) {
        result = CDRConstants.RESULT_FLAG.NOT_OK;
      }
    }
    // If the rule is Complete and the check ssd result param is empty then result is not ok
    else if (ruleUtility.isRuleComplete(reviewRule)) {
      result = CDRConstants.RESULT_FLAG.NOT_OK;
    }
    return result.getDbType();

  }

  /**
   * @param reportModel
   * @return the result. High , Low or NOT OK
   */
  private CDRConstants.RESULT_FLAG getResFlagFromRptModel(final ReportModel reportModel) {

    // by default Not ok.
    CDRConstants.RESULT_FLAG result = CDRConstants.RESULT_FLAG.NOT_OK;
    if (reportModel instanceof FormtdRptValModel) {
      FormtdRptValModel formtdRptValModel = (FormtdRptValModel) reportModel;
      // Added a new method to check for the number validataion. In case of string if not equal the result is Not
      // ok instead of High or low
      if ((CommonUtils.isNotEmptyString(formtdRptValModel.getValGE())) &&
          ApicUtil.isNumber(formtdRptValModel.getValGE().trim())) {
        result = CDRConstants.RESULT_FLAG.HIGH;
      }
      else if (CommonUtils.isNotEmptyString(formtdRptValModel.getValLE()) &&
          ApicUtil.isNumber(formtdRptValModel.getValLE().trim())) {
        result = CDRConstants.RESULT_FLAG.LOW;
      }

    }

    return result;
  }

  /**
   * Get reference value object from RULE for all types (VALUE, CURVE, MAP..)
   *
   * @return CalData object
   * @throws IcdmException
   */
  private byte[] getRefValue(final ReviewRule cdrRule, final Parameter param) throws IcdmException {
    CalData calData = null;
    byte[] calDataBytes = null;
    if (cdrRule != null) {
      // VALUE type label
      if ((ApicUtil.compare(param.getType(), ApicConstants.VALUE_TEXT) == 0) && (cdrRule.getRefValue() != null)) { // ICDM-1253
        // Prepare DCM string for this decimal string and convert to CalData
        calData = CalDataUtil.dcmToCalData(
            CalDataUtil.createDCMStringForNumber(param.getName(), cdrRule.getUnit(), cdrRule.getRefValue().toString()),
            param.getName(), getLogger());
      } // For Complex type labels, get DCM string
      else if (cdrRule.getRefValueCalData() != null) {
        calData = CalDataUtil.getCalDataObjIEx(cdrRule.getRefValueCalData());
      }
    }
    if (calData != null) {
      calDataBytes = CalDataUtil.convertCalDataToZippedByteArr(calData, CDMLogger.getInstance());
    }
    // default case
    return calDataBytes;
  }

  /**
   * Convert the caldata object to a zipped byte array
   *
   * @param data caldata object
   * @return zipped byte array
   * @throws CommandException on any error during conversion
   */
  private byte[] convertCalDataToZippedByteArr(final CalData data) throws CommandException {
    if (data == null) {
      return new byte[0];
    }
    try (ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream outputStm = new ObjectOutputStream(out);) {

      outputStm.writeObject(data);
      ConcurrentMap<String, byte[]> dataMap = new ConcurrentHashMap<>();
      dataMap.put(CalDataUtil.KEY_CALDATA_ZIP, out.toByteArray());
      return ZipUtils.createZip(dataMap);

    }
    catch (IOException exp) {
      throw new CommandException(SAV_CALDATA_ERR, exp);
    }
  }

  /**
   * @param cdrResultFunction
   * @return
   * @throws DataException
   */
  private Set<Parameter> getParamSetForFunction(final CDRResultFunction cdrResultFunction) throws DataException {
    Set<Parameter> paramSet = null;
    // get all the function parameter
    Set<Function> cdrFunList = new FunctionLoader(getServiceData()).getSearchFunctions(cdrResultFunction.getName());
    // get the one matching
    for (Function cdrFunction : cdrFunList) {
      paramSet = this.reviewInfo.getReviewFuncParamMap().get(cdrFunction.getId());
      if (paramSet != null) {
        break;
      }
    }
    return paramSet == null ? new HashSet<>() : paramSet;
  }


  /**
   * @return the deltaReviewValid
   */
  public boolean isDeltaReviewValid() {
    return this.deltaReviewValid;
  }


}
