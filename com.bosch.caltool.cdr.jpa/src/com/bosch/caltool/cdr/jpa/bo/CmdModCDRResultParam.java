/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.calcomp.caldatautils.CalDataComparism.CompareResult;
import com.bosch.calcomp.caldatautils.CompareQuantized;
import com.bosch.calcomp.caldatautils.ItemsToCompare.AvailableItemsForComparison;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldataphy.CalDataPhy;
import com.bosch.caltool.apic.jpa.bo.CommandErrorCodes;
import com.bosch.caltool.apic.jpa.bo.IcdmFile;
import com.bosch.caltool.cdr.jpa.bo.shapereview.ShapeReviewParamResult;
import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.bo.compli.CompliResultUtil;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwFile;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwFunction;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParameter;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.DELTA_REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.RESULT_FLAG;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.SR_ACCEPTED_FLAG;
import com.bosch.caltool.monicareportparser.data.ParameterInfo;
import com.bosch.caltool.monicareportparser.data.ParameterInfo.MONICA_REVIEW_STATUS;
import com.bosch.checkssd.reports.reportMessage.ReportMessages;
import com.bosch.checkssd.reports.reportmodel.FormtdRptValModel;
import com.bosch.checkssd.reports.reportmodel.ReportModel;
import com.bosch.ssd.icdm.model.CDRRule;


/**
 * CmdModCDRResultParam - Command handles all db operations (INSERT, UPDATE) on result params
 */
// ICDM-560
public class CmdModCDRResultParam extends AbstractCDRCommand {

  /**
   * Error message
   */
  private static final String SAV_CALDATA_ERR = "Error saving Cal data for parameter";
  /**
   * Transaction Summary data instance
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);
  /**
   * Parent review result of this review param entry
   */
  private final CDRResult reviewResult;

  /**
   * Parent Function of this review param entry
   */
  private final CDRResultFunction reviewFunc;

  /**
   * Parameter which is involved inr eview
   */
  private final CDRFuncParameter param;

  /**
   * The created result function object
   */
  private CDRResultParameter resParam;


  /**
   * @return the resParam
   */
  public CDRResultParameter getResParam() {
    return this.resParam;
  }


  /**
   * Check SSD result
   */
  private CheckSSDResultParam checkSSDResultParam;

  /**
   * Old Review comment
   */
  private String oldRvwComment;

  /**
   * New Review comment
   */
  private String newRvwComment;

  private String oldReviewScore;

  private String newReviewScore;

  private int changeBitNum;

  private CDRResultParameter parentCdrResultParameter;
  private CDRRule rule;
  private boolean multiUpdate;

  private RESULT_FLAG newResultFlag;

  // Task 236308
  /**
   * new sec rvw result state
   */
  private RESULT_FLAG newSecResultStateFlag;

  // Task 236308
  /**
   * old sec rvw result state
   */
  private RESULT_FLAG oldSecResultStateFlag;
  /**
   * new shape rvw result accept flag
   */
  private SR_ACCEPTED_FLAG newShapeResultAcceptFlag;

  /**
   * old sshape rvw result accept flag
   */
  private SR_ACCEPTED_FLAG oldShapeResultAcceptFlag;
  /**
   * child command stack
   */
  private final ChildCommandStack childCmdStack = new ChildCommandStack(this);

  /**
   * Review parameter entity ID
   */
  private static final String ENTITY_ID = "RVW_PARAMETER";
  // ICDM-1720
  private IcdmFile icdmFile;
  private CalData calData;
  private ParameterInfo monicaParamInfo;

  /**
   * Characteristic object from A2l file
   */
  private Characteristic a2lCharacteristic;
  /**
   * delta review
   */
  private DELTA_REVIEW_TYPE deltaReviewType;
  /**
   *
   */
  private CDRRule compliRule;
  /**
   *
   */
  private CheckSSDResultParam compliSSDResParam;
  /**
   *
   */
  private Map<String, RESULT_FLAG> secResultMap;
  /**
   * Task 237698
   */
  private boolean noRuleForCompli;


  private ShapeReviewParamResult shapeRvwParamResult;
  private boolean errorinSSDFile;

  /**
   * CmdModCDRResultParam, Constructor for INSERT
   *
   * @param dataProvider data provider
   * @param funcParam func param
   * @param cdrResultParameter
   * @param compliRule CDRRule
   * @param compliResParam CheckSSDResultParam
   * @param shapeReviewParamResult
   * @param rvwResult review result
   * @param ruleList
   * @param chkSSDRes result from check ssd
   */
  public CmdModCDRResultParam(final CDRDataProvider dataProvider, final CDRResultFunction rvwResultFunction,
      final CDRFuncParameter funcParam, final CheckSSDResultParam checkSSDResultParam,
      final CDRResultParameter cdrResultParameter, final DELTA_REVIEW_TYPE deltaReviewType, final CDRRule rule,
      final CDRRule compliRule, final CheckSSDResultParam compliResParam,
      final ShapeReviewParamResult shapeReviewParamResult) {
    super(dataProvider);
    this.reviewResult = rvwResultFunction.getCDRResult();
    this.reviewFunc = rvwResultFunction;
    this.param = funcParam;
    this.parentCdrResultParameter = cdrResultParameter;
    this.checkSSDResultParam = checkSSDResultParam;
    this.rule = rule;
    this.deltaReviewType = deltaReviewType;
    this.commandMode = COMMAND_MODE.INSERT;
    // ICDM-2440
    this.compliRule = compliRule;
    this.compliSSDResParam = compliResParam;
    this.shapeRvwParamResult = shapeReviewParamResult;
  }

  /**
   * CmdModCDRResultParam, Constructor for INSERT which accepts a CheckSSDResultParam
   *
   * @param dataProvider
   * @param compliRule CDRRule
   * @param compliSSDResParam CheckSSDResultParam
   * @param cdrResultFunction
   * @param cdrFuncParameter
   * @param checkSSDResultParam2
   * @param cdrResultParameter
   */
  public CmdModCDRResultParam(final CDRDataProvider dataProvider, final CDRResultFunction rvwResultFunction,
      final CDRFuncParameter funcParam, final CheckSSDResultParam checkSSDResultParam, final CDRRule rule,
      final CDRRule compliRule, final CheckSSDResultParam compliSSDResParam,
      final ShapeReviewParamResult shapeReviewParamResult) {
    super(dataProvider);
    this.reviewResult = rvwResultFunction.getCDRResult();
    this.reviewFunc = rvwResultFunction;
    this.param = funcParam;
    this.checkSSDResultParam = checkSSDResultParam;
    this.rule = rule;
    this.commandMode = COMMAND_MODE.INSERT;
    // ICDM-2440
    this.compliRule = compliRule;
    this.compliSSDResParam = compliSSDResParam;
    this.shapeRvwParamResult = shapeReviewParamResult;
  }

  /**
   * CmdModCDRResultParam, Constructor for UPDATE
   *
   * @param dataProvider the data provider
   * @param cdrResParam CDRResultParameter to be updated
   * @param delete delete flag
   * @param rvwResult review result
   */
  public CmdModCDRResultParam(final CDRDataProvider dataProvider, final CDRResultParameter cdrResParam,
      final boolean delete, final boolean isMultiUpdate) {
    super(dataProvider);
    this.reviewResult = cdrResParam.getReviewResult();
    this.reviewFunc = cdrResParam.getFunction();
    this.resParam = cdrResParam;
    this.param = cdrResParam.getFunctionParameter();
    this.multiUpdate = isMultiUpdate;
    if (delete) {
      this.commandMode = COMMAND_MODE.DELETE;
    }
    else {
      this.commandMode = COMMAND_MODE.UPDATE;
      setParamFields();
    }
  }

  /**
   * This method sets the old & new values of param
   */
  private void setParamFields() {

    this.oldReviewScore = this.resParam.getScore().getDbType();
    this.newReviewScore = this.oldReviewScore;
    this.oldRvwComment = this.resParam.getReviewComments() == null ? "" : this.resParam.getReviewComments();
    this.newRvwComment = this.oldRvwComment;

    this.oldSecResultStateFlag = this.resParam.getSecondaryResStateEnum();
    this.oldShapeResultAcceptFlag = this.resParam.getShapeCheckResultEnum();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {

    final TRvwParameter dbRvwParam = new TRvwParameter();

    /* Set Rules */
    setRules(dbRvwParam);

    String isbitwise = this.param.isBitWise() ? ApicConstants.YES : ApicConstants.CODE_NO;
    dbRvwParam.setIsbitwise(isbitwise);
    // bitwise flag
    if (this.parentCdrResultParameter != null) {
      if ((this.parentCdrResultParameter.getCheckedValueObj() != null) &&
          (this.parentCdrResultParameter.getCheckedValueObj().getCalDataPhy() != null)) {
        if (!compareObjects(this.parentCdrResultParameter.isBitWise(), isbitwise)) {
          this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.BITWISE_FLAG.setFlag(this.changeBitNum);
        }
      }
    }

    // Set the secondary rseult type to db.
    if (this.secResultMap != null) {
      RESULT_FLAG result_FLAG = this.secResultMap.get(this.param.getName());
      if (null == result_FLAG) {
        result_FLAG = RESULT_FLAG.NOT_REVIEWED;
      }
      else {
        dbRvwParam.setSecondaryResult(result_FLAG.getDbType());
      }
      if (this.parentCdrResultParameter != null) {
        if (!compareObjects(this.parentCdrResultParameter.getSecondaryResEnum(), result_FLAG)) {
          this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.SECONDARY_RESULT.setFlag(this.changeBitNum);

        }
      }
    }


    /* Set checked value as BLOB */
    setCheckedValue(dbRvwParam);

    // iCDM-577
    /* Set reference value */
    setRefValue(dbRvwParam);

    /* Set Ready for series */
    setReviewMethod(dbRvwParam);

    /* Result info from checkSSD */
    String result = setResultFlag();
    /* Result - OK, Not-ok.. */
    dbRvwParam.setResult(result);

    // ICDM-2440
    String compliResult = getCompliResultFlag();
    dbRvwParam.setCompliResult(compliResult);
    setLabelRevIds(dbRvwParam);


    // Set Reference value unit
    dbRvwParam.setRefUnit(this.rule == null ? null : this.rule.getUnit());

    // Set Checked value unit
    if (this.checkSSDResultParam != null) {
      dbRvwParam.setCheckUnit(this.checkSSDResultParam.getUnit());
    }
    else {
      // ICDM-2520
      // For a compliance parameter, if a normal rule is not present, get check unit from caldata
      if ((null != this.calData) && (null != this.calData.getCalDataPhy())) {
        dbRvwParam.setCheckUnit(this.calData.getCalDataPhy().getUnit());
      }
    }
    // Set hint
    dbRvwParam.setHint(this.rule == null ? null : this.rule.getHint());

    /* Review comment, would not be filled during creation */
    String rvwComment = "";
    if (this.parentCdrResultParameter != null) {
      rvwComment = this.parentCdrResultParameter.getReviewComments();
    }
    if (CommonUtils.isNotNull(this.monicaParamInfo)) {
      rvwComment = this.monicaParamInfo.getComment();
    }
    dbRvwParam.setRvwComment(rvwComment);

    setReviewScore(dbRvwParam, result, compliResult);

    /* Changebit */
    if (this.parentCdrResultParameter != null) {
      dbRvwParam.setChangeFlag(this.changeBitNum);
    }

    /* Set other references */
    TRvwFunction dbRvwFunc = getEntityProvider().getDbCDRResFunction(this.reviewFunc.getID());
    dbRvwParam.setTRvwFunction(dbRvwFunc);
    dbRvwParam.setTParameter(getEntityProvider().getDbParameter(this.param.getID()));
    if ((null != this.parentCdrResultParameter) &&
        this.deltaReviewType.equals(DELTA_REVIEW_TYPE.PROJECT_DELTA_REVIEW)) {
      dbRvwParam.setTRvwParameter(getEntityProvider().getDbCDRResParameter(this.parentCdrResultParameter.getID()));
    }
    dbRvwParam.setTRvwResult(getEntityProvider().getDbCDRResult(this.reviewResult.getID()));
    // ICDM-1720
    TRvwFile dbCDRFile = null;
    Set<TRvwFile> tRvwFiles = getEntityProvider().getDbCDRResult(this.reviewResult.getID()).getTRvwFiles();

    if (null != tRvwFiles) {
      for (TRvwFile tRvwFile : tRvwFiles) {
        if ((this.icdmFile != null) && (tRvwFile.getTabvIcdmFile().getFileId() == this.icdmFile.getID())) {
          dbCDRFile = getEntityProvider().getDbCDRFile(tRvwFile.getRvwFileId());
          break;
        }

      }
    }

    // set review file reference
    dbRvwParam.setTRvwFile(dbCDRFile);

    // ICDM-639
    String matchRefFlag = this.rule != null ? (this.rule.isDcm2ssd() ? ApicConstants.YES : ApicConstants.CODE_NO) : "";
    dbRvwParam.setMatchRefFlag(matchRefFlag);


    // Set the shape check result
    if (null != this.shapeRvwParamResult) {
      dbRvwParam.setSrResult(this.shapeRvwParamResult.getResult());
      dbRvwParam.setSrErrorDetails(this.shapeRvwParamResult.getErrorDetails());
    }

    /* Set user informations */
    setUserDetails(COMMAND_MODE.INSERT, dbRvwParam, ENTITY_ID);

    // register the new Entity to get the ID
    getEntityProvider().registerNewEntity(dbRvwParam);

    this.resParam = new CDRResultParameter(getDataProvider(), dbRvwParam.getRvwParamId());

    // adding the CDRResultParameter to parent CDRResultFuntion and CDRResult in entity
    // check if not null
    Set<TRvwParameter> paramSet = getEntityProvider().getDbCDRResult(this.reviewResult.getID()).getTRvwParameters();
    if (paramSet == null) {
      paramSet = new HashSet<>();
    }
    paramSet.add(dbRvwParam);

    Set<TRvwParameter> functionParamSet =
        getEntityProvider().getDbCDRResFunction(this.reviewFunc.getID()).getTRvwParameters();
    if (functionParamSet == null) {
      functionParamSet = new HashSet<>();
    }
    functionParamSet.add(dbRvwParam);

    getEntityProvider().getDbCDRResFunction(this.reviewFunc.getID()).setTRvwParameters(functionParamSet);
    getEntityProvider().getDbCDRResult(this.reviewResult.getID()).setTRvwParameters(functionParamSet);
    // adding the CDRResultParameter to parent CDRResultFuntion and CDRResult in cache
    Map<Long, CDRResultParameter> reviewResultParametersMap = this.reviewResult.getParametersMap();
    reviewResultParametersMap.put(this.resParam.getID(), this.resParam);
    this.reviewResult.getResultParamNameMap().put(this.resParam.getName(), this.resParam);
    Map<Long, CDRResultParameter> funcParameterMap = this.reviewFunc.getParameterMap();
    funcParameterMap.put(this.resParam.getID(), this.resParam);


    getChangedData().put(dbRvwParam.getRvwParamId(), new ChangedData(ChangeType.INSERT, dbRvwParam.getRvwParamId(),
        TRvwParameter.class, DisplayEventSource.COMMAND));
  }

  /**
   * @return
   */
  private String getCompliResultFlag() {
    if (this.param.isComplianceParameter() || (this.compliSSDResParam != null)) {
      // default
      // Passing dummy values to remove compilatin error. This is old framework, and is no longer used
      CDRConstants.COMPLI_RESULT_FLAG result = CompliResultUtil.getCompliResult(this.compliRule, null,
          this.errorinSSDFile, this.param.getName(), this.param.isComplianceParameter(), false);
      setCompliResultChangeBit(result.getUiType());
      return result.getDbType();
    }
    // return null if this is not a compliance parameter
    return null;
  }

  /**
   * @param uiType
   */
  private void setCompliResultChangeBit(final String uiType) {

    // set the change bit for result flag in delta
    if (this.parentCdrResultParameter != null) {
      if ((this.parentCdrResultParameter.getCheckedValueObj() != null) &&
          (this.parentCdrResultParameter.getCheckedValueObj().getCalDataPhy() != null) &&
          !compareObjects(this.parentCdrResultParameter.getCompliResult(), uiType)) {
        this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.COMPLI_RESULT.setFlag(this.changeBitNum);
      }
    }


  }

  /**
   * @param dbRvwParam
   */
  private void setLabelRevIds(final TRvwParameter dbRvwParam) {
    if (null != this.rule) {
      dbRvwParam.setLabObjId(this.rule.getRuleId() == null ? null : this.rule.getRuleId().longValue());
      dbRvwParam.setRevId(this.rule.getRevId() == null ? null : this.rule.getRevId().longValue());
    }
    if (null != this.compliRule) {
      dbRvwParam
          .setCompliLabObjId(this.compliRule.getRuleId() == null ? null : this.compliRule.getRuleId().longValue());
      dbRvwParam.setCompliRevId(this.compliRule.getRevId() == null ? null : this.compliRule.getRevId().longValue());
    }

  }

  /**
   * @param dbRvwParam
   * @param result
   * @param compliResult
   */
  private void setReviewScore(final TRvwParameter dbRvwParam, final String result, final String compliResult) {
    DATA_REVIEW_SCORE reviewScore = DATA_REVIEW_SCORE.S_0;
    String rvwComment = "Parameter not in CalMemory";
    // For normal review
    if (this.parentCdrResultParameter == null) {

      if (this.param.isComplianceParameter() &&
          CommonUtils.isEqual(compliResult, CDRConstants.COMPLI_RESULT_FLAG.NO_RULE.getDbType())) {
        reviewScore = setScoreInCaseOfCOMPLI(dbRvwParam);
      }
      else if ((null != this.shapeRvwParamResult) && (null != this.shapeRvwParamResult.getResult()) &&
          this.shapeRvwParamResult.getResult().equals(CommonUtilConstants.CODE_FAILED)) {
        reviewScore = setScoreInCaseOfSHAPE(dbRvwParam);
      }
      else if (!this.a2lCharacteristic.isInCalMemory() && (this.calData == null)) {
        reviewScore = DATA_REVIEW_SCORE.S_8;
        // set review comment
        dbRvwParam.setRvwComment(rvwComment);
      }
      else {
        if (checkSeriesReview(result) || checkMonicaReview(result)) {
          reviewScore = DATA_REVIEW_SCORE.S_8;
        }
      }
    } // For delta review
    else {
      // If compliance parameter and if compliance result is COMPLI then set score 0
      if (this.param.isComplianceParameter() &&
          CommonUtils.isEqual(compliResult, CDRConstants.COMPLI_RESULT_FLAG.NO_RULE.getDbType())) {
        reviewScore = setScoreInCaseOfCOMPLI(dbRvwParam);
      }
      else if ((null != this.shapeRvwParamResult) && (null != this.shapeRvwParamResult.getResult()) &&
          this.shapeRvwParamResult.getResult().equals(CommonUtilConstants.CODE_FAILED)) {
        reviewScore = setScoreInCaseOfSHAPE(dbRvwParam);
      }
      else if (!this.a2lCharacteristic.isInCalMemory() && (this.calData == null)) {
        reviewScore = DATA_REVIEW_SCORE.S_8;
        // set review comment
        dbRvwParam.setRvwComment(rvwComment);
      }
      else {
        // If rule changed or check value change and the Ready for series flag is Y and result is ok, the set
        // the
        // score to 8
        if ((isRulechanged() || ((null != this.checkSSDResultParam) &&
            !compareCalObjects(this.parentCdrResultParameter.getCheckedValueObj(), this.calData))) &&
            ((this.rule != null) && (this.rule.getReviewMethod() != null) &&
                this.rule.getReviewMethod().equals(ApicConstants.READY_FOR_SERIES.YES.getDbType())) &&
            result.equals(CDRConstants.RESULT_FLAG.OK.getDbType())) {
          reviewScore = DATA_REVIEW_SCORE.S_8;
        }
        else {
          if (canCopyParentScore(result)) {
            reviewScore = this.parentCdrResultParameter.getScore();
          }
          else {
            reviewScore = DATA_REVIEW_SCORE.S_0;
          }
        }
      }
    }
    dbRvwParam.setReviewScore(reviewScore.getDbType());
    setReviewScoreChangeMarker(reviewScore);
  }

  /**
   * @param result
   * @return
   */
  private boolean checkMonicaReview(final String result) {
    return CommonUtils.isNotNull(this.monicaParamInfo) && result.equals(CDRConstants.RESULT_FLAG.OK.getDbType());
  }

  /**
   * @param result
   * @return
   */
  private boolean checkSeriesReview(final String result) {
    return ((this.rule != null) && (this.rule.getReviewMethod() != null)) &&
        this.rule.getReviewMethod().equals(ApicConstants.READY_FOR_SERIES.YES.getDbType()) &&
        result.equals(CDRConstants.RESULT_FLAG.OK.getDbType());
  }

  /**
   * @param dbRvwParam
   * @return
   */
  private DATA_REVIEW_SCORE setScoreInCaseOfSHAPE(final TRvwParameter dbRvwParam) {
    DATA_REVIEW_SCORE reviewScore;
    reviewScore = DATA_REVIEW_SCORE.S_0;
    return reviewScore;
  }

  /**
   * @param dbRvwParam
   * @return
   */
  private DATA_REVIEW_SCORE setScoreInCaseOfCOMPLI(final TRvwParameter dbRvwParam) {
    DATA_REVIEW_SCORE reviewScore;
    reviewScore = DATA_REVIEW_SCORE.S_0;
    // set review comment if there is no compliance rule
    if (this.noRuleForCompli) {
      dbRvwParam.setRvwComment("No Compliance Rule available");
    }
    return reviewScore;
  }

  /**
   * @param reviewScore
   */
  private void setReviewScoreChangeMarker(final DATA_REVIEW_SCORE reviewScore) {
    // set the change bit for review score flag in delta
    if (this.parentCdrResultParameter != null) {
      if (CommonUtils.isNotNull(this.parentCdrResultParameter.getCheckedValueObj()) &&
          CommonUtils.isNotNull(this.parentCdrResultParameter.getCheckedValueObj().getCalDataPhy()) &&
          (this.parentCdrResultParameter.getScore() != reviewScore)) {
        this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.SCORE_VALUE.setFlag(this.changeBitNum);
      }
      else {
        this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.SCORE_VALUE.removeFlag(this.changeBitNum);
      }
    }
  }

  /**
   * @param result
   * @return
   */
  private boolean canCopyParentScore(final String result) {
    // for project level delta reviews parent cannot be from TEST type results (condition included to avoid NPE)
    // if parent review type is test and the delta review type is either official or start then

    if ((this.deltaReviewType != DELTA_REVIEW_TYPE.PROJECT_DELTA_REVIEW) &&
        (this.parentCdrResultParameter.getReviewResult().getReviewType() == REVIEW_TYPE.TEST)) {

      if ((this.reviewResult.getReviewType() == REVIEW_TYPE.OFFICIAL) ||
          (this.reviewResult.getReviewType() == REVIEW_TYPE.START)) {
        return false;
      }
    }
    // If there is no rule change or check value change then copy the parent score
    // If rule changes , check value is same and result is OK then copy the parent score
    boolean compResult = compareCalObjects(this.parentCdrResultParameter.getCheckedValueObj(), this.calData);
    if ((!isRulechanged() && compResult) ||
        (isRulechanged() && compResult && result.equals(CDRConstants.RESULT_FLAG.OK.getDbType()))) {
      return true;
    }
    // If rule changes , check value is same and result is NOT OK , then if the parent score is scores other than
    // 7,8
    // and 9 then take parent score.
    else if (isRulechanged() && !result.equals(CDRConstants.RESULT_FLAG.OK.getDbType()) &&
        compareCalObjects(this.parentCdrResultParameter.getCheckedValueObj(), this.calData)) {
      if ((this.parentCdrResultParameter.getScore() != DATA_REVIEW_SCORE.S_7) &&
          (this.parentCdrResultParameter.getScore() != DATA_REVIEW_SCORE.S_8) &&
          (this.parentCdrResultParameter.getScore() != DATA_REVIEW_SCORE.S_9)) {
        return true;
      }
    }


    return false;
  }


  /**
   * @return
   */
  private boolean isRulechanged() {
    if (this.changeBitNum == 0) {
      return false;
    }
    return true;
  }

  /**
   * @param result
   * @return
   */
  private String setResultFlag() {


    // by default, not reviwed
    CDRConstants.RESULT_FLAG result = CDRConstants.RESULT_FLAG.NOT_REVIEWED;
    // Set proper result from CheckSSD
    if (this.checkSSDResultParam != null) {
      ReportModel reportModel = this.checkSSDResultParam.getReportModel();
      if (reportModel != null) {
        if ((reportModel.getMessType() == ReportMessages.LOG_MSG) ||
            (reportModel.getMessType() == ReportMessages.WRN_MSG)) {
          result = CDRConstants.RESULT_FLAG.OK;
        }
        else {
          if (reportModel instanceof FormtdRptValModel) {
            FormtdRptValModel formtdRptValModel = (FormtdRptValModel) reportModel;
            // Added a new method to check for the number validataion. In case of string if not equal the result is Not
            // ok instead of High or low
            if ((formtdRptValModel.getValGE() != null) && !formtdRptValModel.getValGE().isEmpty() &&
                ApicUtil.isNumber(formtdRptValModel.getValGE().trim())) {
              result = CDRConstants.RESULT_FLAG.HIGH;
            }
            else if ((formtdRptValModel.getValLE() != null) && !formtdRptValModel.getValLE().isEmpty() &&
                ApicUtil.isNumber(formtdRptValModel.getValLE().trim())) {
              result = CDRConstants.RESULT_FLAG.LOW;
            }
            else {
              result = CDRConstants.RESULT_FLAG.NOT_OK;
            }
          }
          else {
            result = CDRConstants.RESULT_FLAG.NOT_OK;
          }
        }
      }
      else if ((this.rule != null) && this.rule.isRuleComplete() && CommonUtils.isNull(this.monicaParamInfo)) {
        result = CDRConstants.RESULT_FLAG.NOT_OK;
      }
    }
    // Rule is complete and the check ssd is not available
    else if ((this.rule != null) && this.rule.isRuleComplete() && CommonUtils.isNull(this.monicaParamInfo)) {
      result = CDRConstants.RESULT_FLAG.NOT_OK;
    }
    // check or MoniCa type review
    else if (CommonUtils.isNotNull(this.monicaParamInfo)) {
      MONICA_REVIEW_STATUS enumValue = this.monicaParamInfo.getReviewStatus(this.monicaParamInfo.getStatus());
      result = getCdrResultFromMonica(enumValue);
    }
    setResultChangeBit(result.getUiType());


    return result.getDbType();
  }


  /**
   * @param enumValue
   * @return
   */
  private CDRConstants.RESULT_FLAG getCdrResultFromMonica(final MONICA_REVIEW_STATUS enumValue) {
    CDRConstants.RESULT_FLAG result;
    if ((enumValue == MONICA_REVIEW_STATUS.FALIED) || (enumValue == MONICA_REVIEW_STATUS.NOT_OK)) {
      result = CDRConstants.RESULT_FLAG.NOT_OK;
    }
    else if (enumValue == MONICA_REVIEW_STATUS.HIGH) {
      result = CDRConstants.RESULT_FLAG.HIGH;
    }
    else if (enumValue == MONICA_REVIEW_STATUS.LOW) {
      result = CDRConstants.RESULT_FLAG.LOW;
    }
    else if (enumValue == MONICA_REVIEW_STATUS.OK) {
      result = CDRConstants.RESULT_FLAG.OK;
    }
    else {
      result = CDRConstants.RESULT_FLAG.NOT_REVIEWED;
    }
    return result;
  }

  /**
   * @param string
   */
  private void setResultChangeBit(final String uiType) {
    // set the change bit for result flag in delta
    if (this.parentCdrResultParameter != null) {
      if ((this.parentCdrResultParameter.getCheckedValueObj() != null) &&
          (this.parentCdrResultParameter.getCheckedValueObj().getCalDataPhy() != null) &&
          !compareObjects(this.parentCdrResultParameter.getCommonResult(), uiType)) {
        this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.RESULT.setFlag(this.changeBitNum);
      }
    }
  }

  /**
   * @param dbRvwParam
   */
  private void setReviewMethod(final TRvwParameter dbRvwParam) {
    // Ready for series to be selected based on Rule defined for this parameter
    if ((this.rule != null) && (this.rule.getReviewMethod() != null)) {
      dbRvwParam.setRvwMethod(this.rule.getReviewMethod());
    }
  }

  /**
   * Set the checked value to the DB object
   *
   * @param dbRvwParam
   * @throws CommandException
   */
  // iCDM-577
  private void setCheckedValue(final TRvwParameter dbRvwParam) throws CommandException {

    dbRvwParam.setCheckedValue(convertCalDataToZippedByteArr(this.calData));

    // Icdm-945 changes made to the Check Value Change Marker
    if (CommonUtils.isNotNull(this.parentCdrResultParameter)) {
      // get the parent value
      CalData parentCheckedValueObj = this.parentCdrResultParameter.getCheckedValueObj();
      // check for values for a normal review
      if (CommonUtils.isNotNull(this.checkSSDResultParam)) {

        if ((CommonUtils.isNotNull(parentCheckedValueObj)) &&
            (CommonUtils.isNotNull(this.checkSSDResultParam.getCheckedValue()))) {
          if (!compareObjects(parentCheckedValueObj.getCalDataPhy(),
              this.checkSSDResultParam.getCheckedValue().getCalDataPhy())) {
            this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.CHECK_VALUE.setFlag(this.changeBitNum);
          }
        }
        else if (!compareObjects(parentCheckedValueObj, this.checkSSDResultParam.getCheckedValue())) {
          this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.CHECK_VALUE.setFlag(this.changeBitNum);
        }
      }
      // check for MoniCa delta review
      else if (CommonUtils.isNotNull(this.calData) && (CommonUtils.isNotNull(parentCheckedValueObj))) {
        if (!compareObjects(this.calData.getCalDataPhy(), parentCheckedValueObj.getCalDataPhy())) {
          this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.CHECK_VALUE.setFlag(this.changeBitNum);
        }

      }
      // check for MoniCa delta review
      else if (!compareObjects(parentCheckedValueObj, this.calData)) {
        this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.CHECK_VALUE.setFlag(this.changeBitNum);
      }
    }

  }

  /**
   * Set the reference value
   *
   * @param dbRvwParam
   * @throws CommandException
   */
  private void setRefValue(final TRvwParameter dbRvwParam) throws CommandException {
    dbRvwParam.setRefValue(convertCalDataToZippedByteArr(getRefValue()));
    setRefValChangeMarker();
  }

  /**
   * Icdm-945 Ref value Change Marker
   */
  private void setRefValChangeMarker() {
    if (this.parentCdrResultParameter != null) {
      CalData parentRefValueObject = this.parentCdrResultParameter.getRefValueObj();
      if ((parentRefValueObject != null) && ((this.rule != null) && (getRefValue() != null))) {
        if (!compareObjects(parentRefValueObject.getCalDataPhy(), getRefValue().getCalDataPhy())) {
          this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.REF_VALUE.setFlag(this.changeBitNum);
        }
      }
      else if (!compareObjects(parentRefValueObject, getRefValue())) {
        this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.REF_VALUE.setFlag(this.changeBitNum);
      }
    }
  }

  /**
   * Convert the caldata object to a zipped byte array
   *
   * @param data caldata object
   * @return zipped byte array
   * @throws CommandException on any error during conversion
   */
  // ICDM-2069
  private byte[] convertCalDataToZippedByteArr(final CalData data) throws CommandException {
    if (data == null) {
      return null;
    }
    try (ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream outputStm = new ObjectOutputStream(out);) {

      outputStm.writeObject(data);
      ConcurrentMap<String, byte[]> dataMap = new ConcurrentHashMap<>();
      dataMap.put(CalDataUtil.KEY_CALDATA_ZIP, out.toByteArray());
      return ZipUtils.createZip(dataMap);

    }
    catch (IOException e) {
      getDataProvider().getLogger().error(SAV_CALDATA_ERR, e);
      throw new CommandException(SAV_CALDATA_ERR, CommandErrorCodes.ERR_SERIALIZE_DATA, e);
    }
  }


  /**
   * Get reference value object from RULE for all types (VALUE, CURVE, MAP..)
   *
   * @return CalData object
   */
  private CalData getRefValue() {
    final CDRRule cdrRule = this.rule;
    if (cdrRule != null) {
      // VALUE type label
      if ((ApicUtil.compare(this.param.getType(), ApicConstants.VALUE_TEXT) == 0) && (cdrRule.getRefValue() != null)) { // ICDM-1253
        // Prepare DCM string for this decimal string and convert to CalData
        return cdrRule.dcmToCalData(CalDataUtil.createDCMStringForNumber(this.param.getName(), this.rule.getUnit(),
            cdrRule.getRefValue().toString()), this.param.getName());
      } // For Complex type labels, get DCM string
      else if (cdrRule.getRefValueCalData() != null) {
        return cdrRule.getRefValueCalData();
      }
    }
    // default case
    return null;
  }


  /**
   * @param dbRvwParam
   */
  private void setRules(final TRvwParameter dbRvwParam) {
    BigDecimal lowerLimit =
        this.rule == null ? null : this.rule.getLowerLimit() == null ? null : this.rule.getLowerLimit();
    BigDecimal upperLimit =
        this.rule == null ? null : this.rule.getUpperLimit() == null ? null : this.rule.getUpperLimit();
    String bitWiseLimit = this.rule == null ? null : this.rule.getBitWiseRule();
    dbRvwParam.setLowerLimit(lowerLimit);
    dbRvwParam.setUpperLimit(upperLimit);
    dbRvwParam.setBitwiseLimit(bitWiseLimit);
    if (this.parentCdrResultParameter != null) {
      if ((this.parentCdrResultParameter.getCheckedValueObj() != null) &&
          (this.parentCdrResultParameter.getCheckedValueObj().getCalDataPhy() != null)) {
        // Lower limit
        if (!compareObjects(this.parentCdrResultParameter.getLowerLimit(), lowerLimit)) {
          this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.LOWER_LIMT.setFlag(this.changeBitNum);
        }
        // Upper limit
        if (!compareObjects(this.parentCdrResultParameter.getUpperLimit(), upperLimit)) {
          this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.UPPER_LIMIT.setFlag(this.changeBitNum);
        }
        // bitwise rule
        if (this.param.isBitWise() && !compareObjects(this.parentCdrResultParameter.getBitwiseLimit(), bitWiseLimit)) {
          this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.BITWISE_LIMIT.setFlag(this.changeBitNum);
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    final TRvwParameter dbRvwParam = getEntityProvider().getDbCDRResParameter(this.resParam.getID());

    // Disabled for this version
    // Check parallel changes
    final TRvwResult dbRvwRes = getEntityProvider().getDbCDRResult(this.reviewResult.getID());
    this.parentCdrResultParameter = this.resParam.getParentParam();
    this.changeBitNum = dbRvwParam.getChangeFlag();
    if (CommonUtils.isNotEqual(this.oldReviewScore, this.newReviewScore)) {
      dbRvwParam.setReviewScore(this.newReviewScore);

      if (!this.multiUpdate) {
        resultStatusUpdate(dbRvwRes);
      }
    }

    if (this.newResultFlag != null) {
      dbRvwParam.setSecondaryResult(this.newResultFlag.getDbType());
    }

    // Task 236308
    if (null != this.newSecResultStateFlag) {
      dbRvwParam.setSecondaryResultState(this.newSecResultStateFlag.getDbType());
    }
    if (null != this.newShapeResultAcceptFlag) {
      dbRvwParam.setSrAcceptedFlag(this.newShapeResultAcceptFlag.getDbType());
      dbRvwParam.setSrAcceptedUser(getDataProvider().getApicDataProvider().getCurrentUser().getUserName());
      dbRvwParam.setSrAcceptedDate(getCurrentTime());
    }
    dbRvwParam.setRvwComment(this.newRvwComment);
    // Update user details
    setUserDetails(COMMAND_MODE.UPDATE, dbRvwParam, ENTITY_ID);
  }

  /**
   * This method updates the result status if needed
   *
   * @param dbRvwParam
   * @param dbRvwRes
   * @throws CommandException
   */
  private void resultStatusUpdate(final TRvwResult dbRvwRes) throws CommandException {

    // Set new changes
    if (this.newReviewScore.equalsIgnoreCase(DATA_REVIEW_SCORE.S_8.getDbType()) ||
        this.newReviewScore.equalsIgnoreCase(DATA_REVIEW_SCORE.S_9.getDbType())) {
      // iCDM-665
      if (this.reviewResult.isAllParamsReviewed()) {
        CmdModCDRResult cmdResult = new CmdModCDRResult(getDataProvider(), this.reviewResult,
            CDRConstants.REVIEW_STATUS.CLOSED, false, this.reviewResult.getSrResult());
        this.childCmdStack.addCommand(cmdResult);
      }
    }
    else {
      // iCDM-665
      if (!dbRvwRes.getRvwStatus().equals(CDRConstants.REVIEW_STATUS.IN_PROGRESS.getDbType())) {
        CmdModCDRResult cmdResult = new CmdModCDRResult(getDataProvider(), this.reviewResult,
            CDRConstants.REVIEW_STATUS.IN_PROGRESS, false, this.reviewResult.getSrResult());
        this.childCmdStack.addCommand(cmdResult);
      }
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    // Icdm-877 implementation of Delete Command
    final TRvwParameter dbRvwParam = getEntityProvider().getDbCDRResParameter(this.resParam.getID());

    if ((this.reviewFunc != null) && (getEntityProvider().getDbCDRResFunction(this.reviewFunc.getID()) != null) &&
        (getEntityProvider().getDbCDRResFunction(this.reviewFunc.getID()).getTRvwParameters() != null)) {
      getEntityProvider().getDbCDRResFunction(this.reviewFunc.getID()).getTRvwParameters().remove(dbRvwParam);
      getDataCache().getCDRResultFunction(this.reviewFunc.getID()).getParameterMap().remove(this.resParam.getID());
    }
    if (getEntityProvider().getDbCDRResult(this.reviewResult.getID()).getTRvwParameters() != null) {
      getEntityProvider().getDbCDRResult(this.reviewResult.getID()).getTRvwParameters().remove(dbRvwParam);
    }

    this.reviewResult.getResultParamNameMap().remove(this.resParam.getName());
    this.reviewResult.getParametersMap().remove(this.resParam.getID());
    getDataCache().getAllCDRResultParameters().remove(this.resParam.getID());
    getEntityProvider().deleteEntity(dbRvwParam);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() throws CommandException {
    final TRvwParameter dbRvwParam = getEntityProvider().getDbCDRResParameter(this.resParam.getID());

    getEntityProvider().deleteEntity(dbRvwParam);
    this.reviewResult.getParametersMap().remove(this.resParam.getID());
    if ((this.reviewFunc != null) && (getEntityProvider().getDbCDRResFunction(this.reviewFunc.getID()) != null) &&
        (getEntityProvider().getDbCDRResFunction(this.reviewFunc.getID()).getTRvwParameters() != null)) {
      getEntityProvider().getDbCDRResFunction(this.reviewFunc.getID()).getTRvwParameters().remove(dbRvwParam);
      getDataCache().getCDRResultFunction(this.reviewFunc.getID()).getParameterMap().remove(this.resParam.getID());
    }
    if (getEntityProvider().getDbCDRResult(this.reviewResult.getID()).getTRvwParameters() != null) {
      getEntityProvider().getDbCDRResult(this.reviewResult.getID()).getTRvwParameters().remove(dbRvwParam);
    }
    getDataCache().getAllCDRResultParameters().remove(this.resParam.getID());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoUpdateCommand() throws CommandException {
    final TRvwParameter dbRvwParam = getEntityProvider().getDbCDRResParameter(this.resParam.getID());
    // Check parallel changes
    validateStaleData(dbRvwParam);
    // Set new changes

    dbRvwParam.setReviewScore(this.oldReviewScore);
    dbRvwParam.setRvwComment(this.oldRvwComment);
    // Update user details
    setUserDetails(COMMAND_MODE.UPDATE, dbRvwParam, ENTITY_ID);

  }

  /**
   * {@inheritDoc}
   */

  @Override
  public TransactionSummary getTransactionSummary() {
    // ICDM-723
    final SortedSet<TransactionSummaryDetails> detailsList = new TreeSet<TransactionSummaryDetails>();
    switch (this.commandMode) {
      case INSERT:
        caseCmdIns(detailsList);
        break;
      case UPDATE:
        addTransactionSummaryDetails(detailsList, this.oldRvwComment, this.newRvwComment, "Review Comment");

        addTransactionSummaryDetails(detailsList, this.oldReviewScore, this.newReviewScore, "Review Score");
        break;
      default:
        // Do nothing
        break;
    }
    // set the details to summary data
    this.summaryData.setTrnDetails(detailsList);
    // return the filled summary data
    return super.getTransactionSummary(this.summaryData);
  }

  /**
   * @param detailsList
   */
  private void caseCmdIns(final SortedSet<TransactionSummaryDetails> detailsList) {
    final TransactionSummaryDetails details;
    details = new TransactionSummaryDetails();
    details.setOldValue("");
    details.setNewValue(getPrimaryObjectIdentifier());
    details.setModifiedItem(getPrimaryObjectType());
    detailsList.add(details);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoDeleteCommand() throws CommandException {
    // Not Applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    return isStringChanged(this.oldRvwComment, this.newRvwComment) ||
        (CommonUtils.isNotEqual(this.oldReviewScore, this.newReviewScore) ||
            (CommonUtils.isNotEqual(this.oldSecResultStateFlag, this.newSecResultStateFlag)) ||
            (CommonUtils.isNotEqual(this.oldShapeResultAcceptFlag, this.newShapeResultAcceptFlag)));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {
    return super.getString("", getPrimaryObjectIdentifier());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc} return result id to which the parameter is mapped
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.resParam == null ? null : this.resParam.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "CDR Result Parameter";
  }


  /**
   * @param reviewCmt review comment
   */
  public void setReviewComment(final String reviewCmt) {
    this.newRvwComment = reviewCmt;
  }


  /**
   * @param newReviewScore the newReviewScore to set
   */
  public void setNewReviewScore(final String newReviewScore) {
    this.newReviewScore = newReviewScore;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    if (this.commandMode == COMMAND_MODE.INSERT) {
      if (this.resParam != null) {
        getDataCache().getAllCDRResultParameters().remove(this.resParam.getID());
        this.reviewResult.getParametersMap().remove(this.resParam.getID());
        this.reviewResult.getResultParamNameMap().remove(this.resParam.getName());
        getDataCache().getCDRResultFunction(this.reviewFunc.getID()).getParameterMap().remove(this.resParam.getID());
      }
    }

  }

  /** Method used to custom compare two objects */
  private boolean compareObjects(final Object obj1, final Object obj2) {
    boolean isEqual;
    if ((obj1 == null) && (obj2 == null)) {
      isEqual = true;
    }
    else if ((obj1 == null) || (obj2 == null)) {
      isEqual = false;
    }

    else {
      isEqual = obj1.equals(obj2);
    }
    return isEqual;
  }

  /** Need a overloaded compareobjects method since CalDataPhy doesnt override equals(Object) method */
  private boolean compareObjects(final CalDataPhy parentCalDataPhy, final CalDataPhy childCalDataPhy) {
    // ICDM-1785
    CompareResult compareResult =
        CompareQuantized.isEqualForAllItemsExceptExcluded(this.a2lCharacteristic, parentCalDataPhy, childCalDataPhy,
            AvailableItemsForComparison.A2L_UNIT, AvailableItemsForComparison.CAL_DATA_UNITS,
            AvailableItemsForComparison.NO_OF_CHARACTERS, AvailableItemsForComparison.TEXT_BIT);

    return compareResult == CompareResult.EQUAL;
  }


  /** Need a overloaded compareobjects method since CalDataPhy doesnt override equals(Object) method */
  private boolean compareCalObjects(final CalData parentCalData, final CalData childCalData) {
    if ((parentCalData == null) && (childCalData == null)) {
      return true;
    }
    if ((parentCalData == null) || (childCalData == null)) {
      // if either of the objects are null
      return false;
    }
    return compareObjects(parentCalData.getCalDataPhy(), childCalData.getCalDataPhy());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    return this.param.getName();
  }


  /**
   * @return the icdmFile
   */
  public IcdmFile getIcdmFile() {
    return this.icdmFile;
  }


  /**
   * @param icdmFile the icdmFile to set
   */
  public void setIcdmFile(final IcdmFile icdmFile) {
    this.icdmFile = icdmFile;
  }

  /**
   * @param calDataMap calDataMap
   */
  public void setCaldataObj(final CalData calData) {
    this.calData = calData;

  }

  /**
   * @param parameterInfo monicaOutput
   */
  public void setMonicaOutput(final ParameterInfo parameterInfo) {
    this.monicaParamInfo = parameterInfo;

  }


  /**
   * @param a2lcharMap2
   */
  public void setCharacteristicsObj(final Characteristic a2lcharObj) {
    this.a2lCharacteristic = a2lcharObj;

  }

  /**
   * @param secResultMap secResultMap
   */
  public void setSecResultMap(final Map<String, RESULT_FLAG> secResultMap) {
    this.secResultMap = secResultMap;
  }


  /**
   * @return the newResultFlag
   */
  public RESULT_FLAG getNewResultFlag() {
    return this.newResultFlag;
  }


  /**
   * @param newResultFlag the newResultFlag to set
   */
  public void setNewResultFlag(final RESULT_FLAG newResultFlag) {
    this.newResultFlag = newResultFlag;
  }


  /**
   * @return the newSecResultStateFlag
   */
  public RESULT_FLAG getNewSecResultStateFlag() {
    return this.newSecResultStateFlag;
  }


  /**
   * @param newSecResultStateFlag the newSecResultStateFlag to set
   */
  public void setNewSecResultStateFlag(final RESULT_FLAG newSecResultStateFlag) {
    this.newSecResultStateFlag = newSecResultStateFlag;
  }


  /**
   * @return the oldSecResultStateFlag
   */
  public RESULT_FLAG getOldSecResultStateFlag() {
    return this.oldSecResultStateFlag;
  }


  /**
   * @param oldSecResultStateFlag the oldSecResultStateFlag to set
   */
  public void setOldSecResultStateFlag(final RESULT_FLAG oldSecResultStateFlag) {
    this.oldSecResultStateFlag = oldSecResultStateFlag;
  }

  /**
   * @param noRuleForCompli
   */
  public void setNoRuleForCompli(final boolean noRuleForCompli) {
    this.noRuleForCompli = noRuleForCompli;

  }


  /**
   * @return the newShapeResultAcceptFlag
   */
  public SR_ACCEPTED_FLAG getNewShapeResultAcceptFlag() {
    return this.newShapeResultAcceptFlag;
  }


  /**
   * @param newShapeResultAcceptFlag the newShapeResultAcceptFlag to set
   */
  public void setNewShapeResultAcceptFlag(final SR_ACCEPTED_FLAG newShapeResultAcceptFlag) {
    this.newShapeResultAcceptFlag = newShapeResultAcceptFlag;
  }


  /**
   * @return the oldShapeResultAcceptFlag
   */
  public SR_ACCEPTED_FLAG getOldShapeResultAcceptFlag() {
    return this.oldShapeResultAcceptFlag;
  }


  /**
   * @param oldShapeResultAcceptFlag the oldShapeResultAcceptFlag to set
   */
  public void setOldShapeResultAcceptFlag(final SR_ACCEPTED_FLAG oldShapeResultAcceptFlag) {
    this.oldShapeResultAcceptFlag = oldShapeResultAcceptFlag;
  }
}
