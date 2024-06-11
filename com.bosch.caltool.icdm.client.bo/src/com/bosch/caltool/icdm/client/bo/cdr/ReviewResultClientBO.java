/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldata.history.CalDataHistory;
import com.bosch.calmodel.caldata.history.HistoryEntry;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.bo.a2l.RuleMaturityLevel;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.a2l.ParameterClass;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.COMPLI_RESULT_FLAG;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.DELTA_REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.QSSD_RESULT_FLAG;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.RESULT_FLAG;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.SR_ACCEPTED_FLAG;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.SR_RESULT;
import com.bosch.caltool.icdm.model.cdr.CDRResultFunction;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.model.cdr.ReviewResultEditorData;
import com.bosch.caltool.icdm.model.cdr.RvwAttrValue;
import com.bosch.caltool.icdm.model.cdr.RvwFile;
import com.bosch.caltool.icdm.model.cdr.RvwParametersSecondary;
import com.bosch.caltool.icdm.model.cdr.RvwParticipant;
import com.bosch.caltool.icdm.model.cdr.RvwResultWPandRespModel;
import com.bosch.caltool.icdm.model.cdr.RvwResultsSecondary;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionAttributeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author bru2cob
 */
public class ReviewResultClientBO {

  private final ReviewResultEditorData response;


  private final Map<Long, CalData> refValMap = new HashMap<>();

  private final Map<Long, CalData> checkValMap = new HashMap<>();

  private final ReviewResultBO resultBo;

  private final Map<CDRResultParameter, CalData> importValueMap = new HashMap<>();


  /**
   * variant from selected node in pidc tree(used for populating ques list)
   */
  private final RvwVariant varFromPidTree;


  /**
   * enum for columns
   */
  public enum SortColumns {
                           /**
                            * Function Name
                            */
                           SORT_FUNC_NAME,
                           /**
                            * Parameter Name
                            */
                           SORT_PARAM_NAME,
                           // iCDM-848
                           /**
                            * Parameter Long Name
                            */
                           SORT_PARAM_LONG_NAME,
                           /**
                            * Workpackage name
                            */
                           SORT_WORKPACKAGE,
                           /**
                            * Responsibility Name
                            */
                           SORT_RESPONSIBILITY,
                           /**
                            * Responsibility Type
                            */
                           SORT_TYPE,
                           /**
                            * Parameter Class
                            */
                           SORT_PARAM_CLASS,
                           /**
                            * Parameter Code word
                            */
                           SORT_PARAM_CODEWORD,
                           /**
                            * Parameter Hint
                            */
                           SORT_PARAM_HINT,
                           /**
                            * Lower limit
                            */
                           SORT_LOWER_LIMIT,
                           /**
                            * Upper limit
                            */
                           SORT_UPPER_LIMIT,
                           /**
                            * Check Value
                            */
                           SORT_CHECK_VALUE,

                           /**
                            * Check value's unit
                            */
                           // ICDM-2151
                           SORT_CHECK_VALUE_UNIT,
                           /**
                            * Result
                            */
                           SORT_RESULT,

                           // Task 231286
                           /**
                            * Sec Result
                            */
                           SORT_SEC_RESULT,
                           /**
                            * Ready for series
                            */
                           SORT_READY_FOR_SERIES,
                           /**
                            * Review Flag
                            */
                           SORT_SCORE,
                           /**
                            * Comment
                            */
                           SORT_COMMENT,
                           /**
                            * Reference Value Icdm-851 Show reference Value in Result editor
                            */
                           SORT_REFERENCE_VALUE,

                           /**
                            * Reference value's unit
                            */
                           // ICDM-2151
                           SORT_REFERENCE_VALUE_UNIT,
                           /**
                            * History flag
                            */
                           SORT_HISTORY_FLAG,
                           /**
                            * Is bitwise flag
                            */
                           SORT_PARAM_BITWISE,
                           /**
                            * Bitwise value
                            */
                           SORT_BITWISE,
                           /**
                            * History Status
                            */
                           SORT_STATUS,
                           /**
                            * User name in History block
                            */
                           SORT_USER,
                           /**
                            * Date in History block
                            */
                           SORT_DATE,
                           /**
                            * Work package in History block
                            */
                           SORT_WP,
                           /**
                            * Project in history block
                            */
                           SORT_PROJECT,
                           /**
                            * Project Variant in history block
                            */
                           SORT_PROJVAR,
                           /**
                            * Test object in history block
                            */
                           SORT_TESTOBJ,
                           /**
                            * Program identifier in history block
                            */
                           SORT_PGMIDENTIFIER,
                           /**
                            * Data identifier in history block
                            */
                           SORT_DATAIDENTIFIER,
                           /**
                            * Remark in history block
                            */
                           SORT_REMARK,
                           /***
                            * Import Column. There is no corresponding variable to hold this value in CDRResultParameter
                            * object as imported values are stored against the review result editor.
                            */
                           SORT_IMP_VALUE,

                           /**
                           *
                           */
                           // ICDM-2439
                           SORT_PARAM_TYPE_COMPLIANCE,
                           /**
                           *
                           */
                           SORT_SHAPE_CHECK,
                           /**
                            * Score description
                            */
                           SORT_SCORE_DESCRIPTION,
                           /**
                            * Maturity level
                            */
                           SORT_MATURITY_LEVEL,
                           /**
                            * Exact Match
                            */
                           SORT_EXACT_MATCH,
                           /**
                            * Parent Reference Value
                            */
                           SORT_PARENT_REF_VALUE,
                           /**
                            * Parent Check Value
                            */
                           SORT_PARENT_CHECK_VALUE,
  }

  /**
   * @param response ReviewResultEditorData
   * @param varFromPidTree variant node in tree from which review result is opened
   */
  public ReviewResultClientBO(final ReviewResultEditorData response, final RvwVariant varFromPidTree) {
    this.response = response;
    this.varFromPidTree = varFromPidTree;
    this.resultBo = new ReviewResultBO(this);
  }


  // ICDM-2307
  /**
   * @param param result parameter
   * @return Review score
   */
  public DATA_REVIEW_SCORE getScore(final CDRResultParameter param) {
    return DATA_REVIEW_SCORE.getType(param.getReviewScore());

  }

  /**
   * @param param result parameter
   * @return score display string
   */
  public String getScoreUIType(final CDRResultParameter param) {
    return getScore(param).getScoreDisplay();
  }

  /**
   * @param param result parameter
   * @return score + description of score
   */
  public String getScoreExtDescription(final CDRResultParameter param) {
    return DataReviewScoreUtil.getInstance().getScoreDisplayExt(getScore(param));

  }

  /**
   * @param param result parameter
   * @return description of score
   */
  public String getScoreDescription(final CDRResultParameter param) {
    return DataReviewScoreUtil.getInstance().getDescription(getScore(param));
  }

  /**
   * @param param result param
   * @return true if param is read only
   */
  public boolean isReadOnly(final CDRResultParameter param) {
    return this.response.getReadOnlyParamSet().contains(param.getParamId());
  }

  /**
   * @param param
   * @return
   */
  public boolean isDependentParam(final CDRResultParameter param) {
    return this.response.getDepParamMap().containsKey(param.getParamId());
  }

  /**
   * @param param result parameter
   * @return State in History block
   */
  public String getHistoryState(final CDRResultParameter param) {
    String result = "";
    if (null != getLatestHistory(param)) {
      result = CommonUtils
          .checkNull(getLatestHistory(param).getState() == null ? "" : getLatestHistory(param).getState().getValue());
    }
    return result;
  }

  /**
   * @param param result parameter
   * @return User in History block
   */
  public String getHistoryUser(final CDRResultParameter param) {
    String result = "";
    if (null != getLatestHistory(param)) {
      result = CommonUtils.checkNull(
          getLatestHistory(param).getPerformedBy() == null ? "" : getLatestHistory(param).getPerformedBy().getValue());
    }
    return result;
  }

  /**
   * @param param result parameter
   * @return Date in History block
   */
  public String getHistoryDate(final CDRResultParameter param) {
    String result = "";
    if (null != getLatestHistory(param)) {
      result = CommonUtils
          .checkNull(getLatestHistory(param).getDate() == null ? "" : getLatestHistory(param).getDate().getValue());
    }
    return result;
  }

  /**
   * @param param result parameter
   * @return the latest hitory entry
   */
  public HistoryEntry getLatestHistory(final CDRResultParameter param) {
    CalData calData = getCheckedValueObj(param);
    HistoryEntry latestHistoryEntry = null;
    if (calData != null) {

      CalDataHistory calDataHistory = calData.getCalDataHistory();
      if ((calDataHistory != null) && (calDataHistory.getHistoryEntryList() != null) &&
          !calDataHistory.getHistoryEntryList().isEmpty()) {
        // Last Element from the HistoryEntryList is considered as the one with latest timestamp
        latestHistoryEntry = calDataHistory.getHistoryEntryList().get(calDataHistory.getHistoryEntryList().size() - 1);
      }
    }
    return latestHistoryEntry;
  }

  /**
   * @param param result parameter
   * @return Context in History block
   */
  public String getHistoryContext(final CDRResultParameter param) {
    String result = "";
    if (null != getLatestHistory(param)) {
      result = CommonUtils.checkNull(
          getLatestHistory(param).getContext() == null ? "" : getLatestHistory(param).getContext().getValue());
    }
    return result;
  }

  /**
   * @param param result parameter
   * @return Project in History block
   */
  public String getHistoryProject(final CDRResultParameter param) {
    String result = "";
    if (null != getLatestHistory(param)) {
      result = CommonUtils.checkNull(
          getLatestHistory(param).getProject() == null ? "" : getLatestHistory(param).getProject().getValue());
    }
    return result;
  }

  /**
   * @param param result parameter
   * @return TargetVariant in History block
   */
  public String getHistoryTargetVariant(final CDRResultParameter param) {
    String result = "";
    if (null != getLatestHistory(param)) {
      result = CommonUtils.checkNull(getLatestHistory(param).getTargetVariant() == null ? ""
          : getLatestHistory(param).getTargetVariant().getValue());
    }
    return result;
  }

  /**
   * @param param result parameter
   * @return TestObject in History block
   */
  public String getHistoryTestObject(final CDRResultParameter param) {
    String result = "";
    if (null != getLatestHistory(param)) {
      result = CommonUtils.checkNull(
          getLatestHistory(param).getTestObject() == null ? "" : getLatestHistory(param).getTestObject().getValue());
    }
    return result;
  }

  /**
   * @param param result parameter
   * @return ProgramIdentifier in History block
   */
  public String getHistoryProgramIdentifier(final CDRResultParameter param) {
    String result = "";
    if (null != getLatestHistory(param)) {
      result = CommonUtils.checkNull(getLatestHistory(param).getProgramIdentifier() == null ? ""
          : getLatestHistory(param).getProgramIdentifier().getValue());
    }
    return result;
  }

  /**
   * @param param result parameter
   * @return DataIdentifier() in History block
   */
  public String getHistoryDataIdentifier(final CDRResultParameter param) {
    String result = "";
    if (null != getLatestHistory(param)) {
      result = CommonUtils.checkNull(getLatestHistory(param).getDataIdentifier() == null ? ""
          : getLatestHistory(param).getDataIdentifier().getValue());
    }
    return result;
  }

  /**
   * @param param result parameter
   * @return Remark() in History block
   */
  public String getHistoryRemark(final CDRResultParameter param) {
    String result = "";
    if (null != getLatestHistory(param)) {
      result = CommonUtils
          .checkNull(getLatestHistory(param).getRemark() == null ? "" : getLatestHistory(param).getRemark().getValue());
    }
    return result;
  }


  /**
   * @param param result parameter
   * @return true if the parameter is compliant
   */
  public boolean isComplianceParameter(final CDRResultParameter param) {
    Parameter functionParameter = getFunctionParameter(param);
    ParameterDataProvider paramData = new ParameterDataProvider(null);
    return paramData.isComplianceParam(functionParameter);
  }

  /**
   * @param param result parameter
   * @return true if the parameter is qssd
   */
  public boolean isQssdParameter(final CDRResultParameter param) {
    Parameter functionParameter = getFunctionParameter(param);
    ParameterDataProvider paramData = new ParameterDataProvider(null);
    return paramData.isQssdParam(functionParameter);
  }

  /**
   * @param param result parameter
   * @return the parameter object
   */
  public Parameter getFunctionParameter(final CDRResultParameter param) {
    // check if parameter is available in cache
    return getCDRFuncParameter(param);
  }

  /**
   * @param param
   * @return
   */
  public boolean isBlackList(final CDRResultParameter param) {
    return getFunctionParameter(param).isBlackList();
  }

  /**
   * @param param result parameter
   * @return the function name of this parameter
   */
  public String getFunctionName(final CDRResultParameter param) {
    CDRResultFunction func = getFunction(param);
    return getName(func);
  }

  /**
   * @param func result function
   * @return name of result function
   */
  public String getName(final CDRResultFunction func) {
    return func.getName();
  }

  /**
   * @param param result parameter
   * @return the parent CDR result function object Icdm-548 method made as public
   */
  public CDRResultFunction getFunction(final CDRResultParameter param) {
    return this.response.getFuncMap().get(param.getRvwFunId());
  }

  /**
   * @param param result parameter
   * @return reviewed flag
   */
  public boolean isReviewed(final CDRResultParameter param) {
    // ICDM-2307
    return DATA_REVIEW_SCORE.getType(param.getReviewScore()).isReviewed();
  }

  /**
   * @param param result parameter
   * @return reviewed flag
   */
  public boolean isChecked(final CDRResultParameter param) {
    return DATA_REVIEW_SCORE.getType(param.getReviewScore()).isChecked();
  }

  /**
   * @return the set of functions used for this review
   */
  public final SortedSet<CDRResultFunction> getFunctions() {
    return new TreeSet<>(this.response.getFuncMap().values());
  }

  /**
   * @param param result parameter
   * @return
   */
  private Parameter getCDRFuncParameter(final CDRResultParameter param) {
    return this.response.getParamMappingMap().get(param.getName().toUpperCase(Locale.getDefault()));
  }

  /**
   * @param func result function
   * @return CDRFunction
   */
  public Function getCDRFunction(final CDRResultFunction func) {
    return this.response.getFuncMappingMap().get(func.getName().toUpperCase());
  }


  /**
   * @param param result parameter
   * @return parameter's bit wise status as string
   */
  public String getParamIsBitWiseDisplay(final CDRResultParameter param) {
    Parameter par = getCDRFuncParameter(param);
    return ApicConstants.CODE_YES.equalsIgnoreCase(par.getIsBitWise()) ? ApicConstants.CODE_WORD_YES
        : ApicConstants.CODE_WORD_NO;
  }

  /**
   * @param param result parameter
   * @return parameter's class as string
   */
  public String getParameterClassStr(final CDRResultParameter param) {
    ParameterClass pCl = getParameterClass(param);
    return pCl == null ? "" : pCl.getText();
  }

  /**
   * @param param result parameter
   * @return parameter's class
   */
  public ParameterClass getParameterClass(final CDRResultParameter param) {
    Parameter par = getCDRFuncParameter(param);
    return ParameterClass.getParamClassT(par.getpClassText());
  }

  /**
   * @param param result parameter
   * @return parameter's code word flag as string
   */
  public String getParamIsCodeWordDisplay(final CDRResultParameter param) {
    Parameter par = getCDRFuncParameter(param);
    return ApicConstants.CODE_YES.equalsIgnoreCase(par.getCodeWord()) ? ApicConstants.CODE_WORD_YES
        : ApicConstants.CODE_WORD_NO;
  }

  /**
   * Return the string representation of reference value
   *
   * @param param result parameter
   * @return String
   */
  public String getRefValueString(final CDRResultParameter param) {
    if (getRefValueObj(param) != null) {
      return getRefValueObj(param).getCalDataPhy().getSimpleDisplayValue();
    }
    return "";
  }

  /**
   * Method to get Reference value object
   *
   * @param param result parameter
   * @return actual review output
   */
  public CalData getRefValueObj(final CDRResultParameter param) {
    CalData refVal = this.refValMap.get(param.getId());
    if (refVal == null) {
      refVal = getCDPObj(param.getRefValue());
      this.refValMap.put(param.getId(), refVal);
    }
    return refVal;
  }

  /**
   * Method to convert byte array to CaldataPhy object
   *
   * @return actual review output
   */
  private CalData getCDPObj(final byte[] dbData) {
    try {
      return CalDataUtil.getCalDataObj(dbData);
    }
    catch (ClassNotFoundException | IOException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return null;

  }


  /**
   * Return the string representation of checked value
   *
   * @param param result parameter
   * @return String
   */
  public String getCheckedValueString(final CDRResultParameter param) {

    if (getCheckedValueObj(param) != null) {
      return getCheckedValueObj(param).getCalDataPhy().getSimpleDisplayValue();
    }
    return "";
  }

  /**
   * Method to get CheckedValue object
   *
   * @param param result parameter
   * @return actual review output
   */
  public CalData getCheckedValueObj(final CDRResultParameter param) {

    CalData checkVal = this.checkValMap.get(param.getId());
    if (checkVal == null) {
      checkVal = getCDPObj(param.getCheckedValue());
      this.checkValMap.put(param.getId(), checkVal);
    }
    return checkVal;
  }

  /**
   * To shown secondary result column in review result editor
   *
   * @param param result parameter
   * @return string of custom secondary result in result editor
   */
  // Task 231287
  public String getCustomSecondaryResult(final CDRResultParameter param) {
    String result = "";
    RESULT_FLAG secondaryResStateEnum = getSecondaryResStateEnum(param);
    if (RESULT_FLAG.CHECKED == secondaryResStateEnum) {
      result = RESULT_FLAG.CHECKED.getUiType();
    }
    else {
      String secondaryResult = getSecondaryResult(param);
      switch (secondaryResult) {
        case "OK":
        case "COMPLI":
          result = secondaryResult;
          break;
        case "Not OK":
        case "Low":
        case "High":
          result = "Not OK";
          break;
        case "???":
          result = "N/A";
          break;
        default:
          break;
      }
    }
    return result;
  }

  // ICDM-1197
  /**
   * Checks whether history information is available for the parameter
   *
   * @param param result parameter
   * @return true if history is available
   */
  public boolean hasHistory(final CDRResultParameter param) {
    if (getCheckedValueObj(param) != null) {
      CalDataHistory calDataHistory = getCheckedValueObj(param).getCalDataHistory();
      if ((calDataHistory != null) && (calDataHistory.getHistoryEntryList() != null) &&
          !calDataHistory.getHistoryEntryList().isEmpty()) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param param result parameter
   * @return the secondary Result
   */
  // Task 236308
  public String getSecondaryResult(final CDRResultParameter param) {
    if (RESULT_FLAG.CHECKED == getSecondaryResStateEnum(param)) {
      return RESULT_FLAG.CHECKED.getUiType();
    }
    return getSecondaryResEnum(param).getUiType();
  }

  /**
   * @param param result parameter
   * @return the secondary result state
   */
  // Task 236308
  public String getSecondaryResultState(final CDRResultParameter param) {
    return getSecondaryResStateEnum(param).getUiType();
  }

  /**
   * @param param result parameter
   * @return the secondary Result
   */
  // Task 236308
  public RESULT_FLAG getSecondaryResEnum(final CDRResultParameter param) {

    return CDRConstants.RESULT_FLAG.getType(param.getSecondaryResult());
  }

  /**
   * @param param result parameter gets the sec rvw result state enum
   * @return the result falg
   */
  // Task 236308
  public RESULT_FLAG getSecondaryResStateEnum(final CDRResultParameter param) {

    return CDRConstants.RESULT_FLAG.getType(param.getSecondaryResultState());
  }

  /**
   * @param secResParam Secondary Rvw Parameter
   * @return result
   */
  public String getSecondaryCommonResult(final RvwParametersSecondary secResParam) {
    return CDRConstants.RESULT_FLAG.getType(secResParam.getResult()).getUiType();
  }

  /**
   * @param param result parameter
   * @return String Shape Alaysis Review result
   */
  public String getSrResult(final CDRResultParameter param) {
    if (null != param.getSrResult()) {
      SR_RESULT srResultEnum = CDRConstants.SR_RESULT.getType(param.getSrResult());
      return srResultEnum.getUiType();
    }
    return null;
  }

  /**
   * @param param result parameter
   * @return the hint of the rule
   */
  public String getHint(final CDRResultParameter param) {
    StringBuilder hint = new StringBuilder();
    String paramHint = getFunctionParameter(param).getParamHint();
    String ruleHint = param.getHint();
    if (!CommonUtils.isEmptyString(paramHint)) {
      hint.append(paramHint).append("\n\n");
    }
    if (!CommonUtils.isEmptyString(ruleHint)) {
      hint.append(ruleHint);
    }
    return hint.toString();
  }

  /**
   * @param param2
   * @return
   */
  private int caseChkdVal(final CDRResultParameter param1, final CDRResultParameter param2) {
    int compareResult;

    CalData chkVal1 = getCheckedValueObj(param1);
    CalData chkVal2 = getCheckedValueObj(param2);
    if ((null != chkVal1) && (null != chkVal2) && (null != chkVal1.getCalDataPhy()) &&
        (null != chkVal2.getCalDataPhy())) {
      compareResult = chkVal1.getCalDataPhy().compareTo(chkVal2.getCalDataPhy(),
          com.bosch.calmodel.caldataphy.CalDataPhy.SortColumns.SIMPLE_DISPLAY_VALUE);
    }
    else {
      // comparing the checked values
      // In this case string comparison takes place
      compareResult = ApicUtil.compare(getCheckedValueString(param1), getCheckedValueString(param2));
    }
    return compareResult;
  }

  /**
   * @param param result parameter
   * @return the consolidated result
   */
  public String getResult(final CDRResultParameter param) {

    // If the parameter is compliant and its compli result is not OK, display review result as 'COMPLI'
    COMPLI_RESULT_FLAG compliType = CDRConstants.COMPLI_RESULT_FLAG.getType(param.getCompliResult());
    if ((null != param.getCompliResult()) && (CDRConstants.COMPLI_RESULT_FLAG.OK != compliType)) {
      return compliType.getUiType();
    }
    // If the parameter is compliant and its compli result is not OK, display review result as 'QSSD'
    QSSD_RESULT_FLAG qssdType = CDRConstants.QSSD_RESULT_FLAG.getType(param.getQssdResult());
    if ((null != param.getQssdResult()) && (CDRConstants.QSSD_RESULT_FLAG.OK != qssdType)) {
      return qssdType.getUiType();
    }
    // If the parameter is compliant and its compli result is OK (or) If the parameter is not compliant ,check for Shape
    // review result (or)
    // If the shape review fails, check for the Shape review acceptance flag
    if ((null != param.getSrResult()) &&
        (CDRConstants.SR_RESULT.FAIL == CDRConstants.SR_RESULT.getType(param.getSrResult())) &&
        ((null == param.getSrAcceptedFlag()) ||
            (CDRConstants.SR_ACCEPTED_FLAG.NO == CDRConstants.SR_ACCEPTED_FLAG.getType(param.getSrAcceptedFlag())))) {
      // If the failed Shape review result is not acccepted, display review result as 'SHAPE'
      return CDRConstants.RESULT_FLAG.SHAPE.getUiType();
    }
    return getCommonResult(param);
  }

  /**
   * @param param result parameter
   * @return result
   */
  public String getCommonResult(final CDRResultParameter param) {
    return getResultEnum(param).getUiType();
  }

  /**
   * @param param result parameter
   * @return the review result enum.
   */
  private RESULT_FLAG getResultEnum(final CDRResultParameter param) {
    return CDRConstants.RESULT_FLAG.getType(param.getResult());
  }

  /**
   * @param param result parameter
   * @return the string representation of Ready for series
   */
  public String getParentReadyForSeriesString(final CDRResultParameter param) {
    CDRResultParameter parentParam = getParentParam(param);
    return (null != parentParam) ? getReadyForSeries(parentParam).getUiType() : "";
  }

  /**
   * @param param result parameter
   * @return the string representation of Ready for series
   */
  public String getReadyForSeriesStr(final CDRResultParameter param) {
    return getReadyForSeries(param).getUiType();
  }

  /**
   * @param param result parameter
   * @return the ready for series as object
   */
  public ApicConstants.READY_FOR_SERIES getReadyForSeries(final CDRResultParameter param) {
    return ApicConstants.READY_FOR_SERIES.getType(param.getRvwMethod());
  }

  /**
   * This method returns the function name and the function version, if function version not available, then this
   * returns only function name
   *
   * @param cdrResultFunction result function
   * @return function name and version together
   */
  // ICDM-1333
  public String getNameWithFuncVersion(final CDRResultFunction cdrResultFunction) {
    String returnString = getName(cdrResultFunction);
    String functionVersion = cdrResultFunction.getFunctionVers();
    if (CommonUtils.isNotEmptyString(functionVersion)) {
      returnString = CommonUtils.concatenate(returnString, " (", functionVersion, ")");
    }
    return returnString;
  }

  /**
   * @param param1 parameter 1
   * @param param2 parameter to be compared with
   * @param sortColumn name of the sortColumn
   * @return int
   */
  public int compareTo(final CDRResultParameter param1, final CDRResultParameter param2, final SortColumns sortColumn) { // NOPMD

    int compareResult;

    switch (sortColumn) {

      case SORT_PARAM_TYPE_COMPLIANCE:
        compareResult = ApicUtil.compareBoolean(isComplianceParameter(param1), isComplianceParameter(param2));
        break;
      case SORT_SHAPE_CHECK:
        compareResult = ApicUtil.compare(getSrResult(param1), getSrResult(param2));
        break;
      case SORT_FUNC_NAME:
        // comparing the function names
        compareResult = ApicUtil.compare(getFunctionName(param1), getFunctionName(param2));
        break;
      // iCDM-848
      case SORT_PARAM_LONG_NAME:
        // comparing the parameter long name
        compareResult =
            ApicUtil.compare(getCDRFuncParameter(param1).getLongName(), getCDRFuncParameter(param2).getLongName());
        break;
      // 496339 - Enable sorting, text filtering in the WP, Resp columns in Review Result Editor
      case SORT_WORKPACKAGE:
        // comparing the Work Package name
        compareResult = ApicUtil.compare(getWpName(param1), getWpName(param2));
        break;
      // 496339 - Enable sorting, text filtering in the WP, Resp columns in Review Result Editor
      case SORT_RESPONSIBILITY:
        // comparing the Responsibility name
        compareResult = ApicUtil.compare(getRespName(param1), getRespName(param2));
        break;
      case SORT_TYPE:
        // comparing the Responsibility type
        compareResult = ApicUtil.compare(getRespType(param1), getRespType(param2));
        break;
      case SORT_PARAM_CLASS:
        // comparing the parameter class (screw, nail, revit..)
        compareResult =
            ApicUtil.compare(getCDRFuncParameter(param1).getpClassText(), getCDRFuncParameter(param2).getpClassText());
        break;
      case SORT_PARAM_CODEWORD:
        // comparing the parameter code word ( yes, no)
        compareResult =
            ApicUtil.compare(getCDRFuncParameter(param1).getCodeWord(), getCDRFuncParameter(param2).getCodeWord());
        break;
      case SORT_PARAM_HINT:
        // comparing the parameter hint
        compareResult = ApicUtil.compare(getHint(param1), getHint(param2));
        break;
      case SORT_PARAM_BITWISE:
        // comparing the parameter hint
        compareResult = ApicUtil.compare(param1.getIsbitwise(), param2.getIsbitwise());
        break;
      case SORT_BITWISE:
        // comparing the parameter hint
        compareResult = ApicUtil.compare(param1.getBitwiseLimit(), param2.getBitwiseLimit());
        break;
      case SORT_LOWER_LIMIT:
        // compare the lower limits of the parameters
        compareResult = ApicUtil.compareBigDecimal(param1.getLowerLimit(), param2.getLowerLimit());
        break;

      case SORT_UPPER_LIMIT:
        // comparing the upper limits of the parameters
        compareResult = ApicUtil.compareBigDecimal(param1.getUpperLimit(), param2.getUpperLimit());
        break;

      case SORT_CHECK_VALUE:
        compareResult = caseChkdVal(param1, param2);
        break;

      // ICDM-2151
      case SORT_CHECK_VALUE_UNIT:
        compareResult = ApicUtil.compare(param1.getCheckUnit(), param2.getCheckUnit());
        break;

      case SORT_RESULT:
        // comparing the result using string compare
        compareResult = ApicUtil.compare(getResult(param1), getResult(param2));
        break;

      // Task 236308
      case SORT_SEC_RESULT:
        // comparing the result using string compare
        compareResult = ApicUtil.compare(getCustomSecondaryResult(param1), getCustomSecondaryResult(param2));
        break;

      case SORT_READY_FOR_SERIES:
        // comparing the revady for series using string compare
        compareResult = ApicUtil.compare(getReadyForSeriesStr(param1), getReadyForSeriesStr(param2));
        break;

      case SORT_SCORE:
        // review flags are checked using boolean compare
        compareResult = ApicUtil.compare(getScoreUIType(param1), getScoreUIType(param2));
        break;
      case SORT_SCORE_DESCRIPTION:
        compareResult = ApicUtil.compare(getScoreDescription(param1), getScoreDescription(param2));
        break;
      case SORT_COMMENT:
        // comparing the comments using string compare
        compareResult = ApicUtil.compare(param1.getRvwComment(), param2.getRvwComment());
        break;
      // Icdm-851 Sort the Reference Values
      case SORT_REFERENCE_VALUE:
        compareResult = ApicUtil.compare(getRefValueString(param1), getRefValueString(param2));
        break;

      // ICDM-2151
      case SORT_REFERENCE_VALUE_UNIT:
        compareResult = ApicUtil.compare(param1.getRefUnit(), param2.getRefUnit());
        break;
      case SORT_STATUS:
        compareResult = ApicUtil.compare(getHistoryState(param1), getHistoryState(param2));
        break;
      case SORT_USER:
        compareResult = ApicUtil.compare(getHistoryUser(param1), getHistoryUser(param2));
        break;
      case SORT_DATE:
        compareResult = ApicUtil.compare(getHistoryDate(param1), getHistoryDate(param2));
        break;
      case SORT_WP:
        compareResult = ApicUtil.compare(getHistoryContext(param1), getHistoryContext(param2));
        break;
      case SORT_PROJECT:
        compareResult = ApicUtil.compare(getHistoryProject(param1), getHistoryProject(param2));
        break;
      case SORT_PROJVAR:
        compareResult = ApicUtil.compare(getHistoryTargetVariant(param2), getHistoryTargetVariant(param2));
        break;
      case SORT_TESTOBJ:
        compareResult = ApicUtil.compare(getHistoryTestObject(param1), getHistoryTestObject(param2));
        break;
      case SORT_PARENT_REF_VALUE:
        compareResult = ApicUtil.compare(getParentRefValString(param1), getParentRefValString(param2));
        break;
      case SORT_PARENT_CHECK_VALUE:
        compareResult = ApicUtil.compare(getParentCheckedValString(param1), getParentCheckedValString(param2));
        break;
      case SORT_PGMIDENTIFIER:
        compareResult = ApicUtil.compare(getHistoryProgramIdentifier(param1), getHistoryProgramIdentifier(param2));
        break;
      case SORT_DATAIDENTIFIER:
        compareResult = ApicUtil.compare(getHistoryDataIdentifier(param1), getHistoryDataIdentifier(param2));
        break;
      case SORT_REMARK:
        compareResult = ApicUtil.compare(getHistoryRemark(param1), getHistoryRemark(param2));
        break;
      case SORT_MATURITY_LEVEL:
        compareResult = ApicUtil.compare(param1.getMaturityLvl(), param2.getMaturityLvl());
        break;
      case SORT_EXACT_MATCH:
        compareResult = ApicUtil.compare(getExactMatchUiStr(param1), getExactMatchUiStr(param2));
        break;
      default:
        // Compare name
        compareResult = param1.compareTo(param2);
        break;
    }

    // additional compare if both the values are same
    if (compareResult == 0) {
      // compare result is equal, compare the parameter name
      compareResult = param1.compareTo(param2);
    }

    return compareResult;
  }

  /**
   * @param param CDR Result Parameter
   * @return String
   */
  public String getExactMatchUiStr(final CDRResultParameter param) {
    return ApicConstants.EXACT_MATCH.getType(param.getMatchRefFlag()).getUiType();
  }


  /**
   * @param cdrResultFunction result function
   * @return sorted set of result parameters
   */
  public SortedSet<CDRResultParameter> getParameters(final CDRResultFunction cdrResultFunction) {
    List<CDRResultParameter> funcParams = this.response.getFuncParamMap().get(cdrResultFunction.getId());
    if (funcParams == null) {
      return new TreeSet<>();
    }
    return new TreeSet<>(funcParams);
  }


  /**
   * Indicates whether secondary result has changed from parent review. If this is not a delta review, the method
   * returns false.
   *
   * @param param result parameter
   * @return true/false
   */
  // Task 231287
  public boolean isSecondaryResultChanged(final CDRResultParameter param) {
    if (!this.resultBo.isDeltaReview()) {
      return false;
    }
    return CDRConstants.PARAM_CHANGE_FLAG.SECONDARY_RESULT.isSet(param.getChangeFlag().intValue());
  }

  /**
   * @param param result parameter
   * @return the compliance param enum
   */
  public COMPLI_RESULT_FLAG getCompliResultEnum(final CDRResultParameter param) {
    if (param.getCompliResult() != null) {
      return CDRConstants.COMPLI_RESULT_FLAG.getType(param.getCompliResult());
    }
    return null;
  }

  /**
   * @param param result parameter
   * @return the compliance param enum
   */
  public QSSD_RESULT_FLAG getQssdResultEnum(final CDRResultParameter param) {
    if (param.getQssdResult() != null) {
      return CDRConstants.QSSD_RESULT_FLAG.getType(param.getQssdResult());
    }
    return null;
  }

  /**
   * @param param result parameter
   * @return result
   */
  public String getCompliResult(final CDRResultParameter param) {
    COMPLI_RESULT_FLAG compliResultEnum = getCompliResultEnum(param);
    if (compliResultEnum == null) {
      return "";
    }
    return compliResultEnum.getUiType();
  }

  /**
   * @param param result parameter
   * @return result
   */
  public String getQssResult(final CDRResultParameter param) {
    QSSD_RESULT_FLAG qssdResultEnum = getQssdResultEnum(param);
    if (qssdResultEnum == null) {
      return "";
    }
    return qssdResultEnum.getUiType();
  }

  /**
   * Indicates whether result has changed from parent review. If this is not a delta review, the method returns false.
   *
   * @param param result parameter
   * @return true/false
   */
  public boolean isResultChanged(final CDRResultParameter param) {
    if (!this.resultBo.isDeltaReview()) {
      return false;
    }
    return CDRConstants.PARAM_CHANGE_FLAG.RESULT.isSet(param.getChangeFlag().intValue()) ||
        CDRConstants.PARAM_CHANGE_FLAG.COMPLI_RESULT.isSet(param.getChangeFlag().intValue()) ||
        CDRConstants.PARAM_CHANGE_FLAG.QSSD_RESULT.isSet(param.getChangeFlag().intValue());
  }

  /**
   * Indicates whether lower limit has changed from parent review. If this is not a delta review, the method returns
   * false.
   *
   * @param param result parameter
   * @return true/false
   */
  public boolean isLowerLimitChanged(final CDRResultParameter param) {
    if (!this.resultBo.isDeltaReview()) {
      return false;
    }
    return CDRConstants.PARAM_CHANGE_FLAG.LOWER_LIMT.isSet(param.getChangeFlag().intValue());
  }

  /**
   * Indicates whether upper limit has changed from parent review. If this is not a delta review, the method returns
   * false.
   *
   * @param param result parameter
   * @return true/false
   */
  public boolean isUpperLimitChanged(final CDRResultParameter param) {
    if (!this.resultBo.isDeltaReview()) {
      return false;
    }
    return CDRConstants.PARAM_CHANGE_FLAG.UPPER_LIMIT.isSet(param.getChangeFlag().intValue());
  }

  /**
   * Icdm-945 Indicates whether Ref value has changed from parent review. If this is not a delta review, the method
   * returns false.
   *
   * @param param result parameter
   * @return true/false
   */
  public boolean isRefValChanged(final CDRResultParameter param) {
    if (!this.resultBo.isDeltaReview()) {
      return false;
    }
    return CDRConstants.PARAM_CHANGE_FLAG.REF_VALUE.isSet(param.getChangeFlag().intValue());
  }

  /**
   * Indicates whether checked value has changed from parent review. If this is not a delta review, the method returns
   * false.
   *
   * @param param result parameter
   * @return true/false
   */
  public boolean isCheckedValueChanged(final CDRResultParameter param) {
    if (!this.resultBo.isDeltaReview()) {
      return false;
    }
    return CDRConstants.PARAM_CHANGE_FLAG.CHECK_VALUE.isSet(param.getChangeFlag().intValue());
  }

  /**
   * Indicates whether checked value unit is different from reference value unit
   *
   * @param param result parameter
   * @return true/false
   */
  // iCDM-2151
  public boolean isCheckValueRefValueUnitDifferent(final CDRResultParameter param) {
    if (CommonUtils.isEmptyString(param.getRefUnit())) {
      return false;
    }
    return !CommonUtils.isEqual(CommonUtils.checkNull(param.getCheckUnit()), param.getRefUnit());
  }

  /**
   * Indicates whether checked value has changed from parent review. If this is not a delta review, the method returns
   * false.
   *
   * @param param result parameter
   * @return true/false
   */
  public boolean isBitwiseFlagChanged(final CDRResultParameter param) {
    if (!this.resultBo.isDeltaReview()) {
      return false;
    }
    return CDRConstants.PARAM_CHANGE_FLAG.BITWISE_FLAG.isSet(param.getChangeFlag().intValue());
  }

  /**
   * Indicates whether checked value has changed from parent review. If this is not a delta review, the method returns
   * false.
   *
   * @param param result parameter
   * @return true/false
   */
  public boolean isBitwiseLimitChanged(final CDRResultParameter param) {
    if (!this.resultBo.isDeltaReview()) {
      return false;
    }
    return CDRConstants.PARAM_CHANGE_FLAG.BITWISE_LIMIT.isSet(param.getChangeFlag().intValue());
  }

  /**
   * Indicates whether checked value has changed from parent review. If this is not a delta review, the method returns
   * false.
   *
   * @param param result parameter
   * @return true/false
   */
  public boolean isReadyForSeriesFlagChanged(final CDRResultParameter param) {
    if (!this.resultBo.isDeltaReview()) {
      return false;
    }
    return CDRConstants.PARAM_CHANGE_FLAG.READY_FOR_SERIES.isSet(param.getChangeFlag().intValue());
  }

  /**
   * Indicates whether checked value has changed from parent review. If this is not a delta review, the method returns
   * false.
   *
   * @param param result parameter
   * @return true/false
   */
  public boolean isScoreChanged(final CDRResultParameter param) {
    if (!this.resultBo.isDeltaReview()) {
      return false;
    }
    return CDRConstants.PARAM_CHANGE_FLAG.SCORE_VALUE.isSet(param.getChangeFlag().intValue());
  }

  /**
   * Returns the parameters of this review for the given function
   *
   * @param cdrResultFunction result function
   * @return the parameters
   */
  protected Map<Long, CDRResultParameter> getParameterMap(final CDRResultFunction cdrResultFunction) {
    Map<Long, CDRResultParameter> paramMap = new HashMap<>();
    List<CDRResultParameter> list = this.response.getFuncParamMap().get(cdrResultFunction.getId());
    for (CDRResultParameter param : list) {
      paramMap.put(param.getId(), param);
    }
    return paramMap;
  }

  /**
   * @param param result parameter
   * @return ParentResultValueString
   */
  public String getParentScoreValueString(final CDRResultParameter param) {
    CDRResultParameter parentParam = getParentParam(param);
    if (null != parentParam) {
      return getScoreExtDescription(parentParam);
    }
    return "";
  }

  // ICDM-1940
  /**
   * @param param result parameter
   * @return ParentParam
   */
  public CDRResultParameter getParentParam(final CDRResultParameter param) {
    if (this.resultBo.isDeltaReview()) {
      // For normal delta review get the parameter from the parent review
      if (DELTA_REVIEW_TYPE.DELTA_REVIEW.equals(this.resultBo.getDeltaReviewType())) {
        return getDeltaReviewParentParam(param);
      } // For project data delta review get the parameter from the parent param id stored in TRvwParameter table
      else if (DELTA_REVIEW_TYPE.PROJECT_DELTA_REVIEW.equals(this.resultBo.getDeltaReviewType()) &&
          (null != param.getParentParamId())) {
        long rvwParamId = param.getParentParamId();
        return this.response.getProjDeltaParentParamMap().get(rvwParamId);
      }
    }
    return null;
  }

  /**
   * @return parent param for delta review
   */
  private CDRResultParameter getDeltaReviewParentParam(final CDRResultParameter param) {

    for (CDRResultParameter selParam : this.resultBo.getParentParametersMap().values()) {
      if (ApicUtil.compare(selParam.getName(), param.getName()) == 0) {
        return selParam;
      }
    }
    return null;
  }

  // ICDM-1320
  /**
   * This method returns the checked value display string for the parameter
   *
   * @param param result parameter
   * @return checkvalue string
   */
  public String getParentCheckedValString(final CDRResultParameter param) {
    if (null != getParentCheckedVal(param)) {
      return getParentCheckedVal(param).getCalDataPhy().getSimpleDisplayValue();
    }
    return "";
  }

  /**
   * This method returns the checked value for the parameter
   *
   * @param param result parameter
   * @return checkvalue string
   */
  public CalData getParentCheckedVal(final CDRResultParameter param) {

    CDRResultParameter parentParam = getParentParam(param);
    if (null != parentParam) {
      return getCheckedValueObj(parentParam);
    }
    return null;
  }

  /**
   * @param param result parameter
   * @return ParentLowerLimitString
   */
  public String getParentLowerLimitString(final CDRResultParameter param) {
    CDRResultParameter parentParam = getParentParam(param);
    if ((null != parentParam) && (null != parentParam.getLowerLimit())) {
      return parentParam.getLowerLimit().toString();
    }
    return "";
  }

  /**
   * @param param result parameter
   * @return ParentUpperLimitString
   */
  public String getParentUpperLimitString(final CDRResultParameter param) {
    CDRResultParameter parentParam = getParentParam(param);
    if ((null != parentParam) && (null != parentParam.getUpperLimit())) {
      return parentParam.getUpperLimit().toString();
    }
    return "";
  }

  /**
   * @param param result parameter
   * @return ParentBitwiseValString
   */
  public String getParentBitwiseValString(final CDRResultParameter param) {
    CDRResultParameter parentParam = getParentParam(param);
    if ((null != parentParam) && (null != parentParam.getIsbitwise())) {
      return parentParam.getIsbitwise().equals(ApicConstants.CODE_YES) ? ApicConstants.CODE_WORD_YES
          : ApicConstants.CODE_WORD_NO;
    }
    return "";
  }

  /**
   * @param param result parameter
   * @return ParentBitwiseLimitString
   */
  public String getParentBitwiseLimitString(final CDRResultParameter param) {
    CDRResultParameter parentParam = getParentParam(param);
    if ((null != parentParam) && (null != parentParam.getBitwiseLimit())) {
      return parentParam.getBitwiseLimit();
    }
    return "";
  }

  /**
   * @param param result parameter
   * @return ParentResultValueString
   */
  public String getParentResultValueString(final CDRResultParameter param) {
    CDRResultParameter parentParam = getParentParam(param);
    if ((null != parentParam) && (null != parentParam.getResult())) {
      return parentParam.getResult();
    }
    return "";
  }

  // Task 236308
  /**
   * @param param result parameter
   * @return the state of parent review review secondary result
   */
  public String getParentSecResultValueString(final CDRResultParameter param) {
    CDRResultParameter parentParam = getParentParam(param);
    if ((null != parentParam) && (null != getCustomSecondaryResult(parentParam))) {
      return getCustomSecondaryResult(parentParam);
    }
    return "";
  }

  /**
   * @param param result parameter
   * @return ParentResultValueString
   */
  public String getParentCompResultValStr(final CDRResultParameter param) {
    CDRResultParameter parentParam = getParentParam(param);
    if ((null != parentParam) && (null != parentParam.getCompliResult())) {
      return getCompliResult(parentParam);
    }
    return "";
  }

  /**
   * @param param result parameter
   * @return ParentResultValueString
   */
  public String getParentQssdResultValStr(final CDRResultParameter param) {
    CDRResultParameter parentParam = getParentParam(param);
    if ((null != parentParam) && (null != parentParam.getQssdResult())) {
      return getQssResult(parentParam);
    }
    return "";
  }

  /**
   * This method returns the reference value display string for the parameter
   *
   * @param param result parameter
   * @return refvalue string
   */
  public String getParentRefValString(final CDRResultParameter param) {
    if (null != getParentRefVal(param)) {
      return getParentRefVal(param).getCalDataPhy().getSimpleDisplayValue();
    }
    return "";
  }

  /**
   * @param param result parameter This method returns the reference value for the parameter
   * @return refvalue
   */
  public CalData getParentRefVal(final CDRResultParameter param) {
    // ICDM-1940
    CDRResultParameter parentParam = getParentParam(param);
    if (null != parentParam) {
      return getRefValueObj(parentParam);
    }
    return null;
  }

  /**
   * @param param result parameter
   * @return true if exact match flag set
   */
  public boolean isExactMatchRefValue(final CDRResultParameter param) {
    return ApicConstants.CODE_YES.equals(param.getMatchRefFlag());

  }

  /**
   * @return the resultBo
   */
  public ReviewResultBO getResultBo() {
    return this.resultBo;
  }


  /**
   * @return the response
   */
  public ReviewResultEditorData getResponse() {
    return this.response;
  }

  /**
   * @param participant review participant
   * @return type type of participation
   */
  public CDRConstants.REVIEW_USER_TYPE getParticipationType(final RvwParticipant participant) {
    return CDRConstants.REVIEW_USER_TYPE.getType(participant.getActivityType());
  }


  /**
   * @param paramID parameter ID
   * @return files attached to this parameter
   */
  public SortedSet<RvwFile> getAttachments(final Long paramID) {
    final SortedSet<RvwFile> attachedFilesSet = new TreeSet<>();
    if (null != this.response.getParamAdditionalFiles().get(paramID)) {
      attachedFilesSet.addAll(this.response.getParamAdditionalFiles().get(paramID));
    }
    return attachedFilesSet;
  }

  /**
   * @param param result parameter
   * @return the map with param id as key and CDRResParamSecondary as value
   */
  public Map<Long, RvwParametersSecondary> getSecondaryResParams(final CDRResultParameter param) {
    return this.response.getRvwParamSecondaryMap().get(param.getId());
  }

  /**
   * @param secResultId secondary result id
   * @return the secondayResultsMap
   */
  public RvwResultsSecondary getSecondaryResult(final Long secResultId) {
    return this.response.getSecondayResultsMap().get(secResultId);
  }

  /**
   * @return all secondary results
   */
  public SortedSet<RvwResultsSecondary> getSecondaryResultsSorted() {
    return new TreeSet<>(this.response.getSecondayResultsMap().values());
  }

  /**
   * gets theshape check result result enum
   *
   * @param param result parameter
   * @return shape check accepted flag
   */
  public SR_ACCEPTED_FLAG getShapeCheckResultEnum(final CDRResultParameter param) {
    return CDRConstants.SR_ACCEPTED_FLAG.getType(param.getSrAcceptedFlag());
  }

  /**
   * Compare using sort columns
   *
   * @param first RvwAttrValue obj1
   * @param second RvwAttrValue obj2
   * @param sortColumn sortColumn
   * @return the comparison result
   */
  public int compareTo(final RvwAttrValue first, final RvwAttrValue second, final int sortColumn) {
    int compareResult;

    switch (sortColumn) {
      // sort on Attr Name
      case ApicConstants.SORT_ATTRNAME:
        compareResult = ApicUtil.compare(first.getName(), second.getName());
        break;
      // sort on Attr Value
      case ApicConstants.SORT_ATTR_VAL_SEC:
        compareResult = ApicUtil.compare(first.getValueDisplay(), second.getValueDisplay());
        break;
      default:
        compareResult = ApicConstants.OBJ_EQUAL_CHK_VAL;
        break;
    }

    if (compareResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      // compare result is equal, compare the attribute name
      compareResult = first.compareTo(second);
    }

    return compareResult;
  }


  /**
   * @param function result function
   * @return function's name + version in formatted text
   */
  public String getFunctionNameWithVersion(final CDRResultFunction function) {
    String returnString = function.getName();
    String functionVersion = function.getFunctionVers();
    if (CommonUtils.isNotEmptyString(functionVersion)) {
      returnString += " (" + functionVersion + ")";
    }
    return returnString;
  }

  /**
   * 496338 - Add WP, Resp columns in NAT table in Review Result Editor ----------------------------------------------
   * This Method give workpackage name of given parameter
   *
   * @param cdrResParam {@link CDRResultParameter}
   * @return workPackage name
   */
  public String getWpName(final CDRResultParameter cdrResParam) {
    String wpName = "";
    Entry<Long, String> wpRespMapEntry = getWpRespMap(cdrResParam);
    if (null != wpRespMapEntry) {
      Set<RvwResultWPandRespModel> wpRespModelSet = getwpRespModelSet(wpRespMapEntry);
      if (CommonUtils.isNotEmpty(wpRespModelSet)) {
        for (RvwResultWPandRespModel wpRespModel : wpRespModelSet) {
          if ((null != wpRespModel.getA2lWorkPackage()) && (null != wpRespMapEntry.getKey()) &&
              wpRespModel.getA2lWorkPackage().getId().equals(wpRespMapEntry.getKey())) {
            wpName = wpRespModel.getA2lWorkPackage().getName();
            break;
          }
        }
      }
    }
    return wpName;
  }


  private Entry<Long, String> getWpRespMap(final CDRResultParameter cdrResParam) {
    Entry<Long, String> wpRespMapEntry = null;
    Map<Long, Map<Long, String>> paramIdAndWpAndRespMap = getResponse().getParamIdAndWpAndRespMap();
    if (CommonUtils.isNotEmpty(paramIdAndWpAndRespMap)) {
      Map<Long, String> wpRespMap = paramIdAndWpAndRespMap.get(cdrResParam.getParamId());
      if (CommonUtils.isNotEmpty(wpRespMap)) {
        wpRespMapEntry = wpRespMap.entrySet().iterator().next();
      }
    }
    return wpRespMapEntry;
  }

  private Set<RvwResultWPandRespModel> getwpRespModelSet(final Entry<Long, String> wpRespMapEntry) {
    Set<RvwResultWPandRespModel> wpRespModelSet = null;
    Map<String, Set<RvwResultWPandRespModel>> a2lWpMap = getResponse().getA2lWpMap();
    if (CommonUtils.isNotEmpty(a2lWpMap)) {
      wpRespModelSet = a2lWpMap.get(wpRespMapEntry.getValue());
    }
    return wpRespModelSet;
  }

  /**
   * @param cdrResParam {@link CDRResultParameter}
   * @return Responsibility Name
   */
  public String getRespName(final CDRResultParameter cdrResParam) {
    Map<Long, Map<Long, String>> paramIdAndWpAndRespMap = getResponse().getParamIdAndWpAndRespMap();
    String respName = "";
    if (CommonUtils.isNotEmpty(paramIdAndWpAndRespMap)) {
      Map<Long, String> respMap = paramIdAndWpAndRespMap.get(cdrResParam.getParamId());
      if (CommonUtils.isNotEmpty(respMap)) {
        respName = respMap.entrySet().iterator().next().getValue();
      }
    }
    return respName;
  }

  /**
   * @param cdrResParam {@link CDRResultParameter}
   * @return Responsibility type
   */
  public String getRespType(final CDRResultParameter cdrResParam) {

    String respName = getRespName(cdrResParam);

    for (A2lResponsibility resp : getResponse().getA2lResponsibilityMap().values()) {
      if (CommonUtils.isEqual(respName, resp.getName())) {
        return WpRespType.getType(resp.getRespType()).getDispName();
      }
    }
    return "";
  }

  /**
   * @param cdrResParam
   * @return true is qssd review failed
   */
  public boolean isQssdFail(final CDRResultParameter cdrResParam) {
    return (getQssdResultEnum(cdrResParam) != null) && (getQssdResultEnum(cdrResParam) != QSSD_RESULT_FLAG.OK);
  }

  /**
   * @param cdrResParam
   * @return true is compli review failed
   */
  public boolean isCompliFail(final CDRResultParameter cdrResParam) {
    return ((getCompliResultEnum(cdrResParam) != null) && (getCompliResultEnum(cdrResParam) != COMPLI_RESULT_FLAG.OK));
  }

  /**
   * @param cdrResParam
   * @return true is shape review failed
   */
  public boolean isShapeFail(final CDRResultParameter cdrResParam) {
    return (null != cdrResParam.getResult()) &&
        cdrResParam.getResult().equalsIgnoreCase(CDRConstants.RESULT_FLAG.SHAPE.getUiType());
  }

  /**
   * @param newCommentValue String
   * @param selectedParameter CDRResultParameter
   * @return true if comment can be changed
   */
  public boolean canChangeComment(final String newCommentValue, final CDRResultParameter selectedParameter) {
    // comment cannot be changed to empty string if score is 9 and rule is violated
    return !(isCompliOrQssdFailOrNotFulfilledRules(selectedParameter) &&
        isScoreNineNoComments(newCommentValue, getScore(selectedParameter).getDbType()));

  }


  /**
   * @param newCommentValue
   * @param scoreDbType
   * @return
   */
  private boolean isScoreNineNoComments(final String newCommentValue, final String scoreDbType) {
    return (scoreDbType.equals(DATA_REVIEW_SCORE.S_9.getDbType())) &&
        ((newCommentValue == null) || newCommentValue.isEmpty());
  }


  /**
   * @param selectedParameter
   * @return
   */
  private boolean isCompliOrQssdFailOrNotFulfilledRules(final CDRResultParameter selectedParameter) {
    return isCompliFail(selectedParameter) || isQssdFail(selectedParameter) || notFulfilledRules(selectedParameter);
  }

  /**
   * @param newScoreValue Object
   * @param selectedParameter CDRResultParameter
   * @return true if the score can be set to 9
   */
  public boolean canChangeScoreToNine(final Object newScoreValue, final CDRResultParameter selectedParameter) {
    String newScore = newScoreValue.toString().substring(0, 1);
    // score cannot be set to 9 , if rule is violated and comment is empty
    return !(isCompliOrQssdFailOrNotFulfilledRules(selectedParameter) &&
        isScoreNineNoComments(selectedParameter.getRvwComment(), newScore));
  }

  /**
   * @param selectedParameter
   * @return true if the rule is violated
   */
  public boolean notFulfilledRules(final CDRResultParameter cdrResParam) {
    return ((null != cdrResParam.getResult()) && isResultNotOk(cdrResParam));
  }


  /**
   * @param cdrResParam
   * @return
   */
  private boolean isResultNotOk(final CDRResultParameter cdrResParam) {
    return cdrResParam.getResult().equalsIgnoreCase(CDRConstants.RESULT_FLAG.SHAPE.getDbType()) ||
        cdrResParam.getResult().equalsIgnoreCase(CDRConstants.RESULT_FLAG.NOT_OK.getDbType()) ||
        isResultHighOrLow(cdrResParam);
  }


  /**
   * @param cdrResParam
   * @return
   */
  private boolean isResultHighOrLow(final CDRResultParameter cdrResParam) {
    return cdrResParam.getResult().equalsIgnoreCase(CDRConstants.RESULT_FLAG.HIGH.getDbType()) ||
        cdrResParam.getResult().equalsIgnoreCase(CDRConstants.RESULT_FLAG.LOW.getDbType());
  }


  /**
   * @param cdrResParam CDR Result Parameter
   * @return paramHint
   */
  public String getParameterHint(final CDRResultParameter cdrResParam) {
    StringBuilder hint = new StringBuilder();
    String paramHint = cdrResParam.getParamHint();
    String ruleHint = cdrResParam.getHint();
    if (!CommonUtils.isEmptyString(paramHint)) {
      hint.append(paramHint).append("\n\n");
    }
    if (!CommonUtils.isEmptyString(ruleHint)) {
      hint.append(ruleHint);
    }
    return hint.toString();
  }

  /**
   * @param cdrResParam CDR Result Parameter
   * @return Maturity level
   */
  public String getMaturityLevel(final CDRResultParameter cdrResParam) {
    return RuleMaturityLevel.getIcdmMaturityLvlEnumForSsdText(cdrResParam.getMaturityLvl()).getICDMMaturityLevel();
  }


  /**
   * @return the newImportValueMap
   */
  public Map<CDRResultParameter, CalData> getImportValueMap() {
    return this.importValueMap;
  }

  /**
   * @param cdrResParam CDR Result Parameter
   * @return imported value
   */
  public Object getParamCalDataPhy(final CDRResultParameter cdrResParam) {
    Object result;
    Map<CDRResultParameter, CalData> values = getImportValueMap();
    if (values != null) {
      CalData importedValue = values.get(cdrResParam);
      result = (importedValue != null) ? importedValue.getCalDataPhy().getSimpleDisplayValue() : "";
    }
    else {
      result = "";
    }
    return result;
  }


  /**
   * @param parameter cdr result parameter
   * @return whether exact match has changed in delta review
   */
  public boolean isExactMatchFlagChanged(final CDRResultParameter parameter) {
    if (!this.resultBo.isDeltaReview()) {
      return false;
    }
    return CDRConstants.PARAM_CHANGE_FLAG.EXACT_MATCH.isSet(parameter.getChangeFlag().intValue());
  }


  /**
   * @return the varFromPidTree
   */
  public RvwVariant getVarFromPidTree() {
    return this.varFromPidTree;
  }


  /**
   * @param param result parameter
   * @return parent Exact match string
   */
  public String getParentExactMatchUiStr(final CDRResultParameter param) {
    CDRResultParameter parentParam = getParentParam(param);
    return (null != parentParam) ? getExactMatchUiStr(parentParam) : "";
  }

  /**
   * @return true if Smplified General Qnaire is enabled for the selected review result
   */
  public boolean isSimpQuesEnab() {
    boolean isSimpQuesEnab = false;

    String obdFlag = getResultBo().getCDRResult().getObdFlag();
    boolean isSimpQnaireEnabled =
        isDivIdMappedToGivComParamKey(getResultBo().getPidcVersion().getId(), CommonParamKey.DIV_WITH_SIMPL_GEN_QNAIRE);
    boolean isOBDQnaireEnabled =
        isDivIdMappedToGivComParamKey(getResultBo().getPidcVersion().getId(), CommonParamKey.DIVISIONS_WITH_OBD_OPTION);

    if (isDivIdMappedToGivComParamKey(getResultBo().getPidcVersion().getId(), CommonParamKey.DIVISIONS_WITH_QNAIRES) &&
        ((isOBDQnaireEnabled && (CommonUtils.isEqual(obdFlag, CDRConstants.OBD_OPTION.NO_OBD_LABELS.getDbType()) ||
            CommonUtils.isEqual(obdFlag, CDRConstants.OBD_OPTION.BOTH_OBD_AND_NON_OBD_LABELS.getDbType()))) ||
            (!isOBDQnaireEnabled && isSimpQnaireEnabled))) {
      isSimpQuesEnab = true;
    }

    return isSimpQuesEnab;
  }

  /**
   * @param pidcVersionId PidcVersionId
   * @param commonParamKey CommonParamKey
   * @return true if Div Id of PIDC is mapped to given commonParamkey
   */
  public static boolean isDivIdMappedToGivComParamKey(final Long pidcVersionId, final CommonParamKey commonParamKey) {
    CommonDataBO dataBO = new CommonDataBO();
    Long valueId = 0L;
    try {
      if (pidcVersionId != null) {
        Long attrId = Long.parseLong(dataBO.getParameterValue(CommonParamKey.ICDM_QNAIRE_CONFIG_ATTR));
        PidcVersionAttribute versAttr =
            new PidcVersionAttributeServiceClient().getPidcVersionAttribute(pidcVersionId, attrId);
        // Pidc version attribute will be null, if no value is set for attribute
        if (versAttr != null) {
          valueId = versAttr.getValueId();
        }
      }
      String divIdListStr = dataBO.getParameterValue(commonParamKey);
      if ((null != divIdListStr) && (null != valueId)) {
        // comma seperated Division id's
        String[] divIdsArray = divIdListStr.split(",");
        return Arrays.asList(divIdsArray).contains(valueId.toString());
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return false;
  }

  /**
   * @param cdrResultParameter cdrResultParameter
   * @return ParentCheckedValue
   */
  public String getParentCheckedValue(final CDRResultParameter cdrResultParameter) {
    return isCheckedValueChanged(cdrResultParameter) ? String.valueOf(getParentCheckedValString(cdrResultParameter))
        : checkParentCheckVal(cdrResultParameter);
  }

  /**
   * @param cdrResultParameter cdrResultParameter
   * @return ParentRefValue
   */
  public String getParentRefValue(final CDRResultParameter cdrResultParameter) {
    return isRefValChanged(cdrResultParameter) ? String.valueOf(getParentRefValString(cdrResultParameter))
        : checkParentRefVal(cdrResultParameter);
  }

  /**
   * @param cdrResultParameter cdrResultParameter
   * @return ParentCheckVal
   */
  public String checkParentCheckVal(final CDRResultParameter cdrResultParameter) {
    return getParentCheckedVal(cdrResultParameter) == null ? "" : "<same as checked value>";
  }

  /**
   * @param cdrResultParameter cdrResultParameter
   * @return ParentRefVal
   */
  public String checkParentRefVal(final CDRResultParameter cdrResultParameter) {
    return getParentRefVal(cdrResultParameter) == null ? "" : "<same as reference value>";
  }

  /**
   * @param isCompliFail
   * @param isQssdFail
   * @param notFulfilledRules
   */
  public void checkAndDisplayError(final boolean isCompliFail, final boolean isQssdFail,
      final boolean notFulfilledRules) {
    if (isCompliFail) {
      CDMLogger.getInstance().errorDialog(ApicConstants.MSG_CANNOT_CHANGE_SCORE_COMPLI_FAILURE, Activator.PLUGIN_ID);
    }
    else if (isQssdFail) {
      CDMLogger.getInstance().errorDialog(ApicConstants.MSG_CANNOT_CHANGE_SCORE_QSSD_FAILURE, Activator.PLUGIN_ID);
    }
    else if (notFulfilledRules) {
      CDMLogger.getInstance().errorDialog(ApicConstants.MSG_CANNOT_CHANGE_SCORE_UNFULFILLED_RULE, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param isCompliFail
   * @param isQssdFail
   * @param notFulfilledRules
   * @param strBuilder
   */
  public void checkAndAppendFailMsg(final boolean isCompliFail, final boolean isQssdFail,
      final boolean notFulfilledRules, final StringBuilder strBuilder) {
    if (isCompliFail) {
      strBuilder.append(ApicConstants.MSG_CANNOT_CHANGE_SCORE_COMPLI_FAILURE);
      strBuilder.append("\n");
    }
    else if (isQssdFail) {
      strBuilder.append(ApicConstants.MSG_CANNOT_CHANGE_SCORE_QSSD_FAILURE);
      strBuilder.append("\n");
    }
    else if (notFulfilledRules) {
      strBuilder.append(ApicConstants.MSG_CANNOT_CHANGE_SCORE_UNFULFILLED_RULE);
      strBuilder.append("\n");
    }
  }

}

