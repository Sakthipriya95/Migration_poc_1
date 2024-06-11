/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.client.bo.a2l.A2LEditorDataProvider;
import com.bosch.caltool.icdm.client.bo.a2l.A2LFileInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.common.bo.a2l.ParamWpRespResolver;
import com.bosch.caltool.icdm.common.bo.a2l.ParamWpResponsibility;
import com.bosch.caltool.icdm.common.util.Activator;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LDetailsStructureModel;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWPRespModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibilityStatus;
import com.bosch.caltool.icdm.model.a2l.ParamProperties;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_LOCK_STATUS;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.CdrReport;
import com.bosch.caltool.icdm.model.cdr.CdrReportQnaireRespWrapper;
import com.bosch.caltool.icdm.model.cdr.CompliResValues;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.model.cdr.ParameterReviewDetails;
import com.bosch.caltool.icdm.model.cdr.ReviewDetails;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;
import com.bosch.caltool.icdm.model.comphex.CompHexWithCDFParam;
import com.bosch.caltool.icdm.model.util.ModelUtil;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lVariantGroupServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.PidcA2lTreeStructureServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwQnaireResponseServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author mkl2cob
 */
public class CdrReportDataHandler {

  /**
   *
   */
  private static final String DELIMITER_SEMI_COLEN = "; ";

  /**
   * Maximum files to be displayed in tooltip
   */
  private static final int MAX_FILE_TO_DISPLAY_TOOLTIP = 10;

  /**
   * Maximum reviews to be fetched (report input)
   */
  private final int maxReviews;

  /**
   * A2LFileInfo instance
   */
  private final A2LEditorDataProvider a2lEditorDataProvider;

  /**
   * CdrReportData instance
   */
  private final CdrReport cdrReport;

  /**
   * A2LFile instance
   */
  private final PidcA2l pidcA2l;

  /**
   * PIDCVariant instance
   */
  private final PidcVariant pidcVariant;

  /**
   * PIDCVersion instance
   */
  private final PidcVersion pidcVers;

  /**
   * Time at which report was generated
   */
  private final Calendar reportGenDate = Calendar.getInstance();
  // ICDM-1723
  /**
   * Map to store the checkvalue caldata objs
   */
  private final Map<String, Map<Integer, CalData>> chechValMap = new HashMap<>();
  /**
   * Flag to fetch checkvalue or not
   */
  private final boolean fetchCheckVal;

  /**
   * Variant group Id
   */
  private Long varGrpId;

  private ParamWpRespResolver paramWpResolver;
  // param responsibilities corresponding to variant group - KEY - param Id, Value - ParamWpResponsibility
  private Map<Long, ParamWpResponsibility> paramWpMap;

  /**
   * key -Name of resposible , value - List of wp Def listed based on Wp responsible
   */
  private final Map<String, SortedSet<A2lWpResponsibility>> a2lWPResponsibleMap = new HashMap<>();

  /**
   * key -param id , value - map of wp id and Resp name
   */
  private final Map<Long, Map<Long, String>> paramIdWithWpAndRespMap = new HashMap<>();

  /**
   * key -A2lWpResponsibility id , value - set of paramIds assigned to A2lWpResponsibility
   */
  private Map<Long, Set<Long>> paramAndRespPalMap = new HashMap<>();

  /**
   * Map to find Set of ParamIds for WP-Resp This map is constructed to resolve param level resp, var grp specific wp
   * resp Key - WP Id , Value - <resp Id, List<ParamId>>
   */
  private final Map<Long, Map<Long, Set<Long>>> wpRespParamMap = new HashMap<>();

  private CdrReportQnaireRespWrapper cdrReportQnaireRespWrapper;

  private final Long selA2lRespId;

  private final Long selA2lWpId;


  /**
   * To store general question object for the logic to place General Questionnaire at the top of qnaire status tooltip
   */
  private RvwQnaireRespVersion rvwQnaireRespVersObj = null;

  /**
   * Key - WP IdResp Id, Value - WP Finished Status
   */
  private Map<Long, Map<Long, String>> wpFinishedStatusMap = new HashMap<>();

  /**
   * @param a2lEditorDP A2LEditorDataProvider
   * @param maxReviews input Number of reviews
   * @param pidcVariant PIDCVariant
   * @param cdrReport review information of the parameters
   * @param fetchCheckVal true if checkvalue has to be fetched
   * @param selA2lWpId selected A2L WP Id
   * @param selA2lRespId selected A2L Resp Id
   */
  public CdrReportDataHandler(final A2LEditorDataProvider a2lEditorDP, final PidcVariant pidcVariant,
      final int maxReviews, final CdrReport cdrReport, final boolean fetchCheckVal, final Long selA2lRespId,
      final Long selA2lWpId) {

    this.a2lEditorDataProvider = a2lEditorDP;
    this.pidcVariant = pidcVariant;
    this.pidcA2l = cdrReport.getPidcA2l();
    this.pidcVers = cdrReport.getPidcVersion();
    this.fetchCheckVal = fetchCheckVal;
    this.cdrReport = cdrReport;
    this.maxReviews = maxReviews;
    this.paramWpResolver = null;
    this.selA2lRespId = selA2lRespId;
    this.selA2lWpId = selA2lWpId;
    setVarGrpId();
    constructOutlineWPRespData();
  }


  private void constructOutlineWPRespData() {
    Map<Long, ParamWpResponsibility> paramAndWpRespMap = getParamWpMap();
    if (paramAndWpRespMap != null) {
      for (ParamWpResponsibility paramWpResponsibility : paramAndWpRespMap.values()) {
        A2lResponsibility a2lResponsibility = this.a2lEditorDataProvider.getA2lWpInfoBO().getA2lResponsibilityModel()
            .getA2lResponsibilityMap().get(paramWpResponsibility.getRespId());
        A2lWpResponsibility a2lWpResponsibility = this.a2lEditorDataProvider.getA2lWpInfoBO().getA2lWpDefnModel()
            .getWpRespMap().get(paramWpResponsibility.getWpRespId());
        if ((a2lWpResponsibility != null) && (a2lResponsibility != null) &&
            (CommonUtils.isNull(this.selA2lRespId) ||
                CommonUtils.isEqual(this.selA2lRespId, paramWpResponsibility.getRespId())) &&
            (CommonUtils.isNull(this.selA2lWpId) ||
                CommonUtils.isEqual(this.selA2lWpId, paramWpResponsibility.getWpId()))) {
          constructParamIdMap(getParamIdWithWpAndRespMap(), paramWpResponsibility, a2lWpResponsibility,
              getRespName(getA2lWPResponsibleMap(), a2lResponsibility, a2lWpResponsibility));
        }
      }
    }
  }

  /**
   * @param paramWpAndRespMap Param WP-RESP map
   * @param paramWpResponsibility param Wp-Responsibility object
   * @param a2lWpResponsibility a2l Wp Responsibility
   * @param respName responsiblity name
   */
  public void constructParamIdMap(final Map<Long, Map<Long, String>> paramWpAndRespMap,
      final ParamWpResponsibility paramWpResponsibility, final A2lWpResponsibility a2lWpResponsibility,
      final String respName) {

    // For Construction of param id map based on Wp
    fillParamAndRespMap(paramWpResponsibility, a2lWpResponsibility);

    if (paramWpAndRespMap.containsKey(paramWpResponsibility.getParamId())) {
      paramWpAndRespMap.get(paramWpResponsibility.getParamId()).put(a2lWpResponsibility.getA2lWpId(), respName);
    }
    else {
      Map<Long, String> wpIdRespMap = new HashMap<>();
      wpIdRespMap.put(a2lWpResponsibility.getA2lWpId(), respName);
      paramWpAndRespMap.put(paramWpResponsibility.getParamId(), wpIdRespMap);
    }
  }

  /**
   * @param a2lWpParamInfo
   * @param a2lWpResponsibility
   */
  private void fillParamAndRespMap(final ParamWpResponsibility paramWpResponsibility,
      final A2lWpResponsibility a2lWpResponsibility) {
    // loaded for showing param count based on the responsibility from outline selection in Data Review Report
    Set<Long> paramIdSet;
    if (null == getParamAndRespPalMap().get(a2lWpResponsibility.getId())) {
      paramIdSet = new HashSet<>();
    }
    else {
      paramIdSet = getParamAndRespPalMap().get(a2lWpResponsibility.getId());
    }
    paramIdSet.add(paramWpResponsibility.getParamId());
    getParamAndRespPalMap().put(a2lWpResponsibility.getId(), paramIdSet);
  }

  /**
   * @param wpRespMap WP RESP map
   * @param a2lResp A2L RESP
   * @param a2lWpResponsibility A2L WP RESP
   * @return resposibility name
   */
  public String getRespName(final Map<String, SortedSet<A2lWpResponsibility>> wpRespMap,
      final A2lResponsibility a2lResp, final A2lWpResponsibility a2lWpResponsibility) {

    String respName = a2lResp.getName();

    A2lWpResponsibility wpResponsibility = a2lWpResponsibility.clone();
    if (wpRespMap.containsKey(respName)) {
      wpResponsibility.setMappedWpRespName(respName);
      wpRespMap.get(respName).add(wpResponsibility);
    }
    else {
      wpResponsibility.setMappedWpRespName(respName);
      SortedSet<A2lWpResponsibility> a2lWpRespPals = new TreeSet<>();
      a2lWpRespPals.add(wpResponsibility);
      wpRespMap.put(respName, a2lWpRespPals);
    }

    return respName;
  }

  /**
   * Display text of the parameter for the given review - type 1
   *
   * @param paramName name of parameter
   * @param colIndex review result number, identified by column index
   * @return string
   */
  public String getDisplayText(final String paramName, final int colIndex) {
    String dispText = ApicConstants.EMPTY_STRING;
    DATA_REVIEW_SCORE reviewScore = getReviewScore(paramName, colIndex);
    if (reviewScore != null) {
      dispText = reviewScore.getScoreDisplay();
    }

    return dispText;
  }

  /**
   * Display text of the parameter for the given review - type 2
   *
   * @param paramName name of parameter
   * @param colIndex review result number, identified by column index
   * @return string
   */
  public String getDisplayText2(final String paramName, final int colIndex) {
    StringBuilder rvwInfo = new StringBuilder();

    ParameterReviewDetails parameterRvwDetails = getParamReviewDetails(paramName, colIndex);
    if (parameterRvwDetails != null) {

      ReviewDetails reviewDetails = getReviewDetails(paramName, colIndex);

      rvwInfo.append(reviewDetails.getRvwDesc()).append(DELIMITER_SEMI_COLEN)
          .append(CDRConstants.RESULT_FLAG.getType(parameterRvwDetails.getReviewResult()).getUiType())
          .append(DELIMITER_SEMI_COLEN).append(reviewDetails.getRvwInputFileMap().values());

      rvwInfo.append(DELIMITER_SEMI_COLEN).append(getReviewDate(reviewDetails));
    }
    return rvwInfo.toString();
  }


  /**
   * @param colIndex column index
   * @param paramName A2LParameter name
   * @return String Information = description+ review result + reviewed files
   */
  public String getReviewInfoTooltip(final String paramName, final int colIndex) {
    StringBuilder rvwInfo = new StringBuilder();

    ParameterReviewDetails parameterRvwDetails = getParamReviewDetails(paramName, colIndex);
    if (parameterRvwDetails != null) {
      ReviewDetails reviewDetails = getReviewDetails(paramName, colIndex);

      rvwInfo.append("Review Desc\t: ").append(reviewDetails.getRvwDesc()).append("\nResult\t\t: ")
          .append(CDRConstants.RESULT_FLAG.getType(parameterRvwDetails.getReviewResult()).getUiType());
      DATA_REVIEW_SCORE scoreEnum = getReviewScore(paramName, colIndex);

      rvwInfo.append("\nReview Score \t: ").append(DataReviewScoreUtil.getInstance().getScoreDisplayExt(scoreEnum));
      // ICDM-1839
      if (null == reviewDetails.getRvwInputFileMap().get(parameterRvwDetails.getRvwFileID())) {
        rvwInfo.append("\nReviewed file(s)\t: ");
        addRvwFileNames(rvwInfo, reviewDetails);
      }
      else {
        rvwInfo.append("\nInput file \t: ")
            .append(reviewDetails.getRvwInputFileMap().get(parameterRvwDetails.getRvwFileID()));
      }
      // ICDM-1723
      addCheckVal(paramName, colIndex, rvwInfo);

      rvwInfo.append("\nPIDC Version \t: ").append(reviewDetails.getPidcVersName()).append("\nA2L File \t\t: ")
          .append(reviewDetails.getSdomPverVariant()).append(" : ").append(reviewDetails.getA2lFileName())
          .append("\nReview Date\t: ").append(getReviewDate(reviewDetails));

      rvwInfo.append("\nReview Date\t: ").append(getReviewDate(reviewDetails));

      if (CommonUtils.isNotNull(scoreEnum) && !scoreEnum.isReviewed()) {
        rvwInfo.append("\nReview Status\t: ")
            .append("Parameter has not been marked as reviewed in this review result!");
      }

      // ICDM-2584
      rvwInfo.append("\nReview Type\t: ")
          .append(CDRConstants.REVIEW_TYPE.getType(reviewDetails.getReviewType()).getUiType());

      // ICDM-2584
      rvwInfo.append("\nLock Status\t: ").append(REVIEW_LOCK_STATUS.getType(reviewDetails.getLockStatus()).getUiType());

    }
    return rvwInfo.toString();
  }


  /**
   * @param paramName
   * @param colIndex
   * @param rvwInfo
   */
  private void addCheckVal(final String paramName, final int colIndex, final StringBuilder rvwInfo) {
    if (this.fetchCheckVal) {
      rvwInfo.append("\nChecked Value \t: ");
      try {
        CalData checkVal = getParamCheckedVal(paramName, colIndex);
        if ((null != checkVal) && (null != checkVal.getCalDataPhy())) {
          rvwInfo.append(checkVal.getCalDataPhy().getSimpleDisplayValue());
        }

      }
      catch (Exception e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        rvwInfo.append("<Conversion error>");
      }
    }
  }

  /**
   * @param rvwInfo
   * @param reviewDetails
   */
  private void addRvwFileNames(final StringBuilder rvwInfo, final ReviewDetails reviewDetails) {
    if (reviewDetails.getRvwInputFileMap().size() > ApicConstants.COLUMN_INDEX_1) {
      int index = 0;
      for (String fileName : reviewDetails.getRvwInputFileMap().values()) {
        rvwInfo.append("\n\t");
        if (index == MAX_FILE_TO_DISPLAY_TOOLTIP) {
          rvwInfo.append("...");
          break;// stop displaying the file names if they cross 10
        }
        rvwInfo.append(fileName); // place line feed after file name
        index++;
      }
    }
    else {
      rvwInfo.append(reviewDetails.getRvwInputFileMap().values());
    }
  }


  // ICDM-1701
  /**
   * Returns the review result in the specified col for the given param
   *
   * @param paramName param Name
   * @param colIndex col index
   * @return review result
   */
  public CDRReviewResult getReviewResult(final String paramName, final int colIndex) {
    ParameterReviewDetails paramRvwDet = getParamReviewDetails(paramName, colIndex);
    // ICDM-2492
    return paramRvwDet == null ? null : this.cdrReport.getCdrReviewResultMap().get(paramRvwDet.getRvwID());
  }


  public String getReviewResultName(final String paramName) {
    CDRReviewResult cdrReviewResult = getReviewResult(paramName, 0);

    return CommonUtils.isNotEqual(cdrReviewResult, null) ? cdrReviewResult.getName()
        : CDRConstants.NO_REVIEW_RESULT_FOUND;
  }


  /**
   * Returns the PidcA2l in the specified col for the given param
   *
   * @param paramName param Name
   * @param colIndex col index
   * @return review result
   */
  public PidcA2l getPidcA2l(final String paramName, final int colIndex) {
    CDRReviewResult result = getReviewResult(paramName, colIndex);
    return result == null ? null : this.cdrReport.getReviewDetA2lMap().get(result.getPidcA2lId());
  }

  /**
   * Returns the Pidc Version in the specified col for the given param
   *
   * @param paramName param Name
   * @param colIndex col index
   * @return review result
   */
  public PidcVersion getPidcVersion(final String paramName, final int colIndex) {
    CDRReviewResult result = getReviewResult(paramName, colIndex);
    return result == null ? null : this.cdrReport.getReviewDetPidcVersMap().get(result.getPidcVersionId());
  }


  // ICDM-1703
  /**
   * Returns whether the parameter is reviewed as 'YES' or 'NO'
   *
   * @param paramName selc param name
   * @return 'Reviewed' or 'Not Reviewed' or 'Never Reviewed'
   */
  public String isReviewedStr(final String paramName) {
    DATA_REVIEW_SCORE reviewScore = getReviewScore(paramName);

    if (CommonUtils.isNotNull(reviewScore)) {
      // ICDM-2585 (Parent Task ICDM-2412)-2
      if (reviewScore.isReviewed() && isOfficialAndLocked(paramName)) {
        return ApicConstants.REVIEWED;
      }
      return ApicConstants.NOT_REVIEWED;
    }
    return ApicConstants.NEVER_REVIEWED;
  }

  /**
   * Returns true if the parameter is reviewed and has Score 8/9 or with score ARC Release Flag is set
   *
   * @param paramWPRespObj selc param name
   * @return 'Checked_Review_Score' or 'Not Reviewed' or 'Never Reviewed'
   */
  public String isCheckedRvwScore(final ParamWpResponsibility paramWPRespObj) {
    DATA_REVIEW_SCORE reviewScore = getReviewScore(paramWPRespObj.getParamName());
    if (CommonUtils.isNotNull(reviewScore)) {
      String arcReleaseVal = this.cdrReport.getParamwithARCReleaseFlagMap().get(paramWPRespObj.getParamId());
      if (reviewScore.isReviewed() && (reviewScore.isChecked() || CommonUtils.getBooleanType(arcReleaseVal)) &&
          isOfficialAndLocked(paramWPRespObj.getParamName())) {
        return ApicConstants.CHECKED_REVIEW_SCORE;
      }
      return ApicConstants.NOT_REVIEWED;
    }
    return ApicConstants.NEVER_REVIEWED;
  }

  /**
   * Returns true if the a2l parameter is reviewed and official and locked
   *
   * @param paramName the given a2l param name
   * @return true if the a2l parameter is reviewed and official and locked
   */
  // ICDM-2585 (Parent Task ICDM-2412)-2
  public boolean isOfficialAndLocked(final String paramName) {
    ReviewDetails reviewDetails = getReviewDetailsLatest(paramName);
    return ((CDRConstants.REVIEW_TYPE.OFFICIAL == CDRConstants.REVIEW_TYPE.getType(reviewDetails.getReviewType())) &&
        (REVIEW_LOCK_STATUS.YES == REVIEW_LOCK_STATUS.getType(reviewDetails.getLockStatus())));
  }

  // ICDM-2045
  /**
   * @param a2lParam a2lParam
   * @return true if param func version equals latest func version
   */
  public boolean isLatestVersMatch(final A2LParameter a2lParam) {
    String paramFuncVers =
        a2lParam.getDefFunction() == null ? ApicConstants.EMPTY_STRING : a2lParam.getDefFunction().getFunctionVersion();
    String latestFuncVers = getLatestFunctionVersion(a2lParam.getName());
    if (CommonUtils.isNotEmptyString(latestFuncVers)) {
      return CommonUtils.isEqual(paramFuncVers, latestFuncVers);
    }
    return false;
  }

  /**
   * Returns latest review score string
   *
   * @param paramName selc param name
   * @return true if reviewed
   */
  private String getLatestReviewScore(final String paramName) {
    ParameterReviewDetails paramReviewDetailsLatest = getParamReviewDetailsLatest(paramName);
    if (paramReviewDetailsLatest == null) {
      return ApicConstants.EMPTY_STRING;
    }
    return paramReviewDetailsLatest.getReviewScore();
  }

  /**
   * Returns latest review comment
   *
   * @param paramName selc param name
   * @return true if reviewed
   */
  public String getLatestReviewComment(final String paramName) {
    ParameterReviewDetails paramReviewDetailsLatest = getParamReviewDetailsLatest(paramName);
    if (paramReviewDetailsLatest == null) {
      return ApicConstants.EMPTY_STRING;
    }
    return paramReviewDetailsLatest.getRvwComment();
  }

  /**
   * @param paramName paramName
   * @return the enumeration value. For no value retrun null.
   */
  public DATA_REVIEW_SCORE getReviewScore(final String paramName) {
    String reviewScore = getLatestReviewScore(paramName);
    if ((reviewScore == null) || reviewScore.isEmpty()) {
      return null;
    }
    return DATA_REVIEW_SCORE.getType(reviewScore);

  }

  /**
   * @param paramName as input
   * @return review result OK/NOT OK /???
   */
  public String getLatestReviewResult(final String paramName) {
    ParameterReviewDetails paramReviewDetails = getParamReviewDetails(paramName, 0);
    if (paramReviewDetails != null) {
      String reviewResult = paramReviewDetails.getReviewResult();
      return CDRConstants.RESULT_FLAG.getType(reviewResult).getUiType();
    }
    // returns NULL for Never Reviwed parameters
    return null;
  }

  /**
   * @param paramName
   * @return the latest review of the parameter
   */
  public ParameterReviewDetails getParamReviewDetailsLatest(final String paramName) {
    return getParamReviewDetails(paramName, 0);
  }


  /**
   * @param paramName parameter name
   * @return the latest review of the parameter
   */
  public ReviewDetails getReviewDetailsLatest(final String paramName) {
    // ICDM-2585 (Parent Task ICDM-2412), changing private scope to public scope, to re-use this method.
    return getReviewDetails(paramName, 0);
  }

  /**
   * @return the a2lFile
   */
  public PidcA2l getPidcA2l() {
    return this.pidcA2l;
  }

  /**
   * @return the a2lEditorDataProvider
   */
  public A2LEditorDataProvider getA2lEditorDataProvider() {
    return this.a2lEditorDataProvider;
  }


  /**
   * @return the cdrReport
   */
  public CdrReport getCdrReport() {
    return this.cdrReport;
  }

  /**
   * @return the pidcVariant
   */
  public PidcVariant getPidcVariant() {
    return this.pidcVariant;
  }

  /**
   * @return the pidcVers
   */
  public PidcVersion getPidcVers() {
    return this.pidcVers;
  }

  /**
   * @param paramName param name
   * @return wp name or group name
   */
  public String getWp(final String paramName) {
    return this.cdrReport.getParameterWorkPackageMap().get(paramName);
  }

  /**
   * @return the varGrpId
   */
  public Long getVarGrpId() {
    return this.varGrpId;
  }


  /**
   * @param varGrpId the varGrpId to set
   */
  public void setVarGrpId(final Long varGrpId) {
    this.varGrpId = varGrpId;
  }


  private void setVarGrpId() {
    A2LDetailsStructureModel detailsModel = null;
    try {
      detailsModel = new A2lVariantGroupServiceClient()
          .getA2lDetailsStructureData(this.a2lEditorDataProvider.getA2lWpInfoBO().getActiveVers().getId());
      // get the a2l var group id to which the selected variant is mapped - null if mapped to default level
      if ((null != this.pidcVariant) && (detailsModel.getGroupMappingMap().get(this.pidcVariant.getId()) != null)) {
        this.varGrpId = detailsModel.getGroupMappingMap().get(this.pidcVariant.getId()).getA2lVarGroupId();
      }
      else {
        this.varGrpId = null;
      }
      loadData();
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp);
    }
  }

  private void loadData() {
    A2LWPInfoBO a2lWpInfoBO = getA2lEditorDataProvider().getA2lWpInfoBO();
    // Already data got loaded in A2lFileValidationJob of CDRReportGeneration Class
    if (CommonUtils.isNullOrEmpty(a2lWpInfoBO.getA2lWpDefnVersMap())) {
      a2lWpInfoBO.loadWPDefnVersionsForA2l();
    }
    A2LFileInfoBO a2lFileInfoBO = getA2lEditorDataProvider().getA2lFileInfoBO();

    // if the "Generate Data Review Report" is selected from A2L Resp /WP node then the boolean
    // isToGenDataRvwRprtForWPResp is true
    boolean isToGenDataRvwRprtForWPResp = getCdrReport().isToGenDataRvwRprtForWPResp();
    a2lFileInfoBO.setDataRvwRprtFrmA2LRespWP(isToGenDataRvwRprtForWPResp);
    Map<String, ParamProperties> paramProps = a2lFileInfoBO.getParamProps();
    if (CommonUtils.isNotEmpty(paramProps)) {
      a2lWpInfoBO.initialiseA2LWParamInfo(isToGenDataRvwRprtForWPResp
          ? a2lFileInfoBO.getA2lParamMapForWPResp(getCdrReport()) : a2lFileInfoBO.getA2lParamMap(paramProps));
    }
    A2lWpDefnVersion activeVers = a2lWpInfoBO.getActiveVers();
    if (null != activeVers) {
      a2lWpInfoBO.loadWpMappedToPidcVers();
      a2lWpInfoBO.initializeModelBasedOnWpDefVers(activeVers.getId(), false, true);
      a2lWpInfoBO.formVirtualRecords();
    }
    fillWpRespForParam();
  }


  /** fill the map , to group the paramIds which has same wp resp **/
  private void fillWpRespForParam() {
    A2LFileInfoBO a2lFileInfoBO = getA2lEditorDataProvider().getA2lFileInfoBO();
    Collection<A2LParameter> getA2lParam = this.cdrReport.isToGenDataRvwRprtForWPResp()
        ? a2lFileInfoBO.getA2lParamMapForWPResp(this.cdrReport).values() : a2lFileInfoBO.getA2lParamMap(null).values();
    for (A2LParameter a2lParam : getA2lParam) {
      ParamWpResponsibility respObj = getParamWpResp(a2lParam.getParamId());

      if (respObj != null) {
        Map<Long, Set<Long>> wpRespMap = getWpRespParamMap().get(respObj.getWpId());
        if (getWpRespParamMap().containsKey(respObj.getWpId()) && (wpRespMap != null) &&
            wpRespMap.containsKey(respObj.getRespId())) {
          wpRespMap.get(respObj.getRespId()).add(respObj.getParamId());
        }
        else if (getWpRespParamMap().containsKey(respObj.getWpId()) && (wpRespMap != null) &&
            !wpRespMap.containsKey(respObj.getRespId())) {
          Map<Long, Set<Long>> respParamMapToAdd = new HashMap<>();
          Set<Long> paramSet = new HashSet<>();
          paramSet.add(respObj.getParamId());
          respParamMapToAdd.put(respObj.getRespId(), paramSet);
          wpRespMap.putAll(respParamMapToAdd);
        }
        else {
          Map<Long, Set<Long>> respParamMapToAdd = new HashMap<>();
          Set<Long> paramSet = new HashSet<>();
          paramSet.add(respObj.getParamId());
          respParamMapToAdd.put(respObj.getRespId(), paramSet);
          getWpRespParamMap().put(respObj.getWpId(), respParamMapToAdd);
        }
      }
    }
  }

  /**
   * @param paramId , the Param Id
   * @return ParamWpResponsibility
   */
  public ParamWpResponsibility getParamWpResp(final Long paramId) {

    // load the details for the first time
    if (this.paramWpResolver == null) {
      // Filtering WPResp Map based on Selected WP and Resp to show only selected WP and Resp in Outline View
      fillParamWPMap();
    }
    if (this.paramWpMap != null) {
      return (this.paramWpMap.get(paramId));
    }
    return null;
  }

  private Map<Long, ParamWpResponsibility> getParamWpMap() {
    if ((this.paramWpResolver == null) && (this.paramWpMap == null)) {
      // Filtering WPResp Map based on Selected WP and Resp to show only selected WP and Resp in Outline View
      fillParamWPMap();
    }
    return this.paramWpMap;
  }


  /**
   *
   */
  private void fillParamWPMap() {
    Map<Long, A2lWpResponsibility> a2lWPDefVersWPRespMap =
        this.a2lEditorDataProvider.getA2lWpInfoBO().getA2lWpDefnModel().getWpRespMap();
    this.paramWpResolver = new ParamWpRespResolver(
        this.a2lEditorDataProvider.getA2lWpInfoBO().getA2lWpDefnModel().getA2lVariantGroupMap(), a2lWPDefVersWPRespMap,
        this.a2lEditorDataProvider.getA2lWpInfoBO().getA2lWpParamMappingModel().getA2lWpParamMapping());
    this.paramWpMap = this.paramWpResolver.getRespForParam(getVarGrpId());
  }

  /**
   * @param paramId paramId
   * @return parmwpresp object
   */
  public String getRespName(final Long paramId) {
    ParamWpResponsibility respObj = getParamWpResp(paramId);
    if (respObj != null) {
      return this.a2lEditorDataProvider.getA2lWpInfoBO().getA2lResponsibilityModel().getA2lResponsibilityMap()
          .get(respObj.getRespId()).getName();
    }
    return ApicConstants.EMPTY_STRING;
  }


  /**
   * This method is to fetch the WpResp Status column value
   *
   * @param paramId parameter ID
   * @return wprespStatus
   */
  public String getWpFinishedRespStatus(final Long paramId) {
    ParamWpResponsibility respObj = getParamWpResp(paramId);
    Map<Long, Map<Long, String>> wpFinStatusMap = getWpFinishedStatusMap();
    if ((respObj != null) && wpFinStatusMap.containsKey(respObj.getWpId())) {
      return wpFinStatusMap.get(respObj.getWpId()).get(respObj.getRespId());
    }
    return ApicConstants.EMPTY_STRING;
  }

  /**
   * @return
   */
  public void calWpFinStatusAndFillWPFinStatusMap() {
    Map<Long, Map<Long, A2lWpResponsibilityStatus>> wpIdRespIdAndStatusMap = this.cdrReport.getWpIdRespIdAndStatusMap();
    Map<Long, Map<Long, String>> wpFinStatusMap = new HashMap<>();

    for (Entry<Long, Map<Long, Set<Long>>> paramWPRespMap : getWpRespParamMap().entrySet()) {

      Long wpId = paramWPRespMap.getKey();
      if (wpIdRespIdAndStatusMap.containsKey(wpId)) {
        Map<Long, A2lWpResponsibilityStatus> respIdAndStatusMap = wpIdRespIdAndStatusMap.get(wpId);
        for (Entry<Long, Set<Long>> respIdAndParamIdsEntry : paramWPRespMap.getValue().entrySet()) {
          calStatusAndAddStatusToMap(wpFinStatusMap, wpId, respIdAndStatusMap, respIdAndParamIdsEntry);
        }
      }
      else {
        for (Long respId : paramWPRespMap.getValue().keySet()) {
          wpFinStatusMap.computeIfAbsent(wpId, value -> new HashMap<>()).put(respId,
              CDRConstants.WP_RESP_STATUS_TYPE.NOT_FINISHED.getDbType());
        }
      }
    }
    // Update status Map to update status decorator based on updated status in Data Review Report
    // report and in Compare Hex Outline page
    setWpFinishedStatusMap(wpFinStatusMap);
  }


  private void calStatusAndAddStatusToMap(final Map<Long, Map<Long, String>> wpFinStatusMap, final Long wpId,
      final Map<Long, A2lWpResponsibilityStatus> respIdAndStatusMap,
      final Entry<Long, Set<Long>> respIdAndParamIdsEntry) {
    if (respIdAndStatusMap.containsKey(respIdAndParamIdsEntry.getKey())) {
      A2lWpResponsibilityStatus wpRespStatus = respIdAndStatusMap.get(respIdAndParamIdsEntry.getKey());
      wpFinStatusMap.computeIfAbsent(wpRespStatus.getA2lWpId(), value -> new HashMap<>())
          .put(wpRespStatus.getA2lRespId(), wpRespStatus.getWpRespFinStatus());
    }
    else {
      wpFinStatusMap.computeIfAbsent(wpId, value -> new HashMap<>()).put(respIdAndParamIdsEntry.getKey(),
          CDRConstants.WP_RESP_STATUS_TYPE.NOT_FINISHED.getDbType());
    }
  }

  /*
   * checking whether QnaireResp status is 'All Positive' or 'All Answered with Negative', If negative, check whether
   * the questionnaire definition allows WP to be finished when the answers are negative
   */
  /**
   * @param qnaireStatus Questionnaire Response Version Status
   * @return true, if all the questionaires are answered
   */
  public boolean isQnaireAllAnswered(final String qnaireStatus) {
    return CommonUtils.isEqual(qnaireStatus, CDRConstants.QS_STATUS_TYPE.ALL_POSITIVE.getDbType()) ||
        CommonUtils.isEqual(qnaireStatus, CDRConstants.QS_STATUS_TYPE.NOT_ALL_POSITIVE.getDbType());
  }

  /**
   * Method to return questionnaire status based on input param id
   *
   * @param paramId parameter Id
   * @param includeNotBaselinedQnaireResp true means include Not baselined Qnaire Response
   * @return status of questionnaire resp version
   */
  public String getQnaireRespVersStatus(final Long paramId, final boolean includeNotBaselinedQnaireResp) {

    ParamWpResponsibility respObj = getParamWpResp(paramId);
    // Checking whether the param is reviewed - score > 0 (official review and locked)
    if (respObj != null) {
      String isReviewed = isReviewedStr(respObj.getParamName());

      return ApicConstants.REVIEWED.equals(isReviewed) ? getRvwQnaireRespVersStatus(paramId)
          : CDRConstants.RVW_QNAIRE_STATUS_N_A;
    }
    return CDRConstants.RVW_QNAIRE_STATUS_N_A;
  }


  /**
   * Method to return questionnaire status based on input param id
   *
   * @param paramWPRespObj paramWPResp object
   * @param includeNotBaselinedQnaireResp true means include Not baselined Qnaire Response
   * @return status of questionnaire resp version
   */
  public boolean isParamValidToMarkWPasFin(final ParamWpResponsibility paramWPRespObj,
      final boolean includeNotBaselinedQnaireResp) {
    // Checking whether the param review score- 8/9 or ARC flag is set to yes and check whether the review is official
    // review and locked)
    String isCheckedReviewScore = isCheckedRvwScore(paramWPRespObj);

    String status = CommonUtils.isEqual(ApicConstants.CHECKED_REVIEW_SCORE, isCheckedReviewScore)
        ? getRvwQnaireRespVersStatus(paramWPRespObj.getParamId()) : CDRConstants.RVW_QNAIRE_STATUS_N_A;

    // return true if the status is valid to mark WP as finished
    return isQnaireAllAnswered(status) || CommonUtils.isEqual(status, CDRConstants.NO_QNAIRE_STATUS);
  }


  /**
   * Method to return questionnaire status based on input param name (For Compare Hex report)
   *
   * @param paramName as input
   * @param includeNotBaselinedQnaireResp true means include working set
   * @return status of questionnaire resp version
   */
  public String getQnaireRespVersStatus(final String paramName, final boolean includeNotBaselinedQnaireResp) {
    A2LParameter a2lParameter = this.a2lEditorDataProvider.getA2lFileInfoBO().getA2lParamMap(null).get(paramName);
    String isReviewed = isReviewedStr(a2lParameter.getName());

    return ApicConstants.REVIEWED.equals(isReviewed) ? getRvwQnaireRespVersStatus(a2lParameter.getParamId())
        : CDRConstants.RVW_QNAIRE_STATUS_N_A;
  }

  /**
   * @param paramId
   * @return
   */
  private String getRvwQnaireRespVersStatus(final Long paramId) {
    String rvwQnaireRespVersStatus = getRvwQnaireResponseVersionStatus(paramId);

    return CommonUtils.isNotNull(rvwQnaireRespVersStatus) ? rvwQnaireRespVersStatus : ApicConstants.EMPTY_STRING;
  }


  private String getRvwQnaireResponseVersionStatus(final Long paramId) {
    ParamWpResponsibility respObj = getParamWpResp(paramId);
    if (CommonUtils.isNotNull(respObj)) {
      if (isAnyQnaireRespNotBaselined(respObj)) {
        return CDRConstants.NOT_BASELINED_QNAIRE_RESP;
      }
      if (getCdrReportQnaireRespWrapper().getWpRespQnaireRespVersMap().containsKey(respObj.getRespId())) {
        Map<Long, String> wpQnaireRespVerMap =
            getCdrReportQnaireRespWrapper().getWpRespQnaireRespVersStatusMap().get(respObj.getRespId());
        if (wpQnaireRespVerMap.containsKey(respObj.getWpId())) {
          return wpQnaireRespVerMap.get(respObj.getWpId());
        }
      }
      // NO_QNAIRE_STATUS for Monica Review and Simplified Qnaire
      if (!isQnaireRespNotBaselined(respObj)) {
        return CDRConstants.NO_QNAIRE_STATUS;
      }
    }

    return null;
  }

  /**
   * @param respObj ParamWpResponsibility
   * @return true, if baseline is not created for the questionnaire response else false
   */
  public boolean isAnyQnaireRespNotBaselined(final ParamWpResponsibility respObj) {

    Map<Long, Map<Long, Set<RvwQnaireRespVersion>>> allWpRespQnaireRespVersMap =
        getCdrReportQnaireRespWrapper().getAllWpRespQnaireRespVersMap();

    Long respId = respObj.getRespId();
    if (allWpRespQnaireRespVersMap.containsKey(respId)) {
      Map<Long, Set<RvwQnaireRespVersion>> wpQnaireRespVerMap = allWpRespQnaireRespVersMap.get(respId);

      Long wpId = respObj.getWpId();
      if (wpQnaireRespVerMap.containsKey(wpId)) {
        Set<RvwQnaireRespVersion> rvwQniareRespVersSet = wpQnaireRespVerMap.get(wpId);
        // Iterate through Latest RvwQnaireRespVersions belonging to WP Resp Combination
        for (RvwQnaireRespVersion rvwQnaireRespVers : rvwQniareRespVersSet) {
          // rvwQnaireRespVersion is null for Simplified Qnaire
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
  public Predicate<RvwQnaireRespVersion> isRvwQnaireRespVersWorkingSet() {
    return rvwQnaireRespVers -> CommonUtils.isEqual(rvwQnaireRespVers.getRevNum(), CDRConstants.WORKING_SET_REV_NUM);
  }

  /**
   * Method to give the qnaire resp tooltip
   *
   * @param paramName parameter id
   * @return tooltip
   */
  public String getQnaireRespVersStatusToolTip(final String paramName) {
    A2LParameter a2lParameter = this.a2lEditorDataProvider.getA2lFileInfoBO().getA2lParamMap(null).get(paramName);
    return getQnaireRespVersStatusToolTip(a2lParameter.getParamId());
  }


  /**
   * Method to give the qnaire resp tooltip
   *
   * @param paramId parameter id
   * @return tooltip
   */
  public String getQnaireRespVersStatusToolTip(final Long paramId) {
    ParamWpResponsibility respObj = getParamWpResp(paramId);
    StringBuilder qnaireToolTip = new StringBuilder();

    if (CommonUtils.isNotNull(respObj) && ApicConstants.REVIEWED.equals(isReviewedStr(respObj.getParamName()))) {
      // if label is reviewed only questionnaire status should be displayed
      if (isQnaireRespNotBaselined(respObj)) {
        // if baseline is available display questionnaire response
        for (Entry<Long, Set<RvwQnaireRespVersion>> wpQnaireVersionsEntrySet : getCdrReportQnaireRespWrapper()
            .getAllWpRespQnaireRespVersMap().get(respObj.getRespId()).entrySet()) {
          if (wpQnaireVersionsEntrySet.getKey().equals(respObj.getWpId())) {
            constructQnaireRespToolTip(qnaireToolTip, wpQnaireVersionsEntrySet);
          }
        }
      }
      else {
        qnaireToolTip.append(CDRConstants.NO_QNAIRE_STATUS);
      }
    }
    else {
      // if label is not reviewed display N\A tooltip
      qnaireToolTip.append(CDRConstants.RVW_QNAIRE_STATUS_N_A_TOOLTIP);
    }

    return qnaireToolTip.toString();
  }


  /**
   * @param respObj
   * @return true, if questionnaire are not baselined
   */
  private boolean isQnaireRespNotBaselined(final ParamWpResponsibility paramWpResponsibility) {

    boolean isQnaireRespNotBaselined = false;
    Map<Long, Map<Long, Set<RvwQnaireRespVersion>>> allWpRespQnaireRespVersMap =
        getCdrReportQnaireRespWrapper().getAllWpRespQnaireRespVersMap();

    if (allWpRespQnaireRespVersMap.containsKey(paramWpResponsibility.getRespId())) {
      for (Entry<Long, Set<RvwQnaireRespVersion>> wpQnaireVersionsEntrySet : allWpRespQnaireRespVersMap
          .get(paramWpResponsibility.getRespId()).entrySet()) {
        Set<RvwQnaireRespVersion> rvwQnaireRespVersSet = wpQnaireVersionsEntrySet.getValue();
        if (wpQnaireVersionsEntrySet.getKey().equals(paramWpResponsibility.getWpId())) {
          // If there is only simplified Qnaire, then return false
          isQnaireRespNotBaselined = (CommonUtils.isEqual(rvwQnaireRespVersSet.size(), 1) &&
              CommonUtils.isNull(rvwQnaireRespVersSet.iterator().next())) ? isQnaireRespNotBaselined
                  : !isQnaireRespNotBaselined;
          break;
        }
      }
    }

    return isQnaireRespNotBaselined;
  }


  /**
   * @param qnaireToolTip
   * @param wpQnaireVersionsEntrySet
   */
  private void constructQnaireRespToolTip(final StringBuilder qnaireToolTip,
      final Entry<Long, Set<RvwQnaireRespVersion>> wpQnaireVersionsEntrySet) {

    Set<RvwQnaireRespVersion> rvwQnaireVerSet = wpQnaireVersionsEntrySet.getValue();
    // rvwQnaireVerSet has null value if there is simplified Qnaire
    rvwQnaireVerSet.removeIf(Objects::isNull);
    // Sorting rvw Qnaire Resp list in Ascending Order
    List<RvwQnaireRespVersion> rvwQnaireRespVersionList = rvwQnaireVerSet.stream().sorted((o1, o2) -> {

      Map<Long, RvwQnaireResponse> qnaireResponseMap = getCdrReportQnaireRespWrapper().getQnaireResponseMap();
      RvwQnaireResponse rvwQnaireResponse1 = qnaireResponseMap.get(o1.getId());
      RvwQnaireResponse rvwQnaireResponse2 = qnaireResponseMap.get(o2.getId());

      if (isGeneralQnaire().test(rvwQnaireResponse1)) {
        setRvwQnaireRespVersObj(o1);
      }
      else if (isGeneralQnaire().test(rvwQnaireResponse2)) {
        setRvwQnaireRespVersObj(o2);
      }

      // checking whether obj is same using name and id
      int compareName = ModelUtil.compare(rvwQnaireResponse1.getName(), rvwQnaireResponse2.getName());
      if (CommonUtils.isEqual(compareName, 0)) {
        return ModelUtil.compare(rvwQnaireResponse1.getId(), rvwQnaireResponse2.getId());
      }

      return compareName;
    }).collect(Collectors.toList());

    // Logic to move General Question to first position in List after sorting
    if (CommonUtils.isNotNull(getRvwQnaireRespVersObj())) {
      // Removing General Qnaire from existing position in the List
      int genQnairePosInList = rvwQnaireRespVersionList.indexOf(getRvwQnaireRespVersObj());
      rvwQnaireRespVersionList.remove(genQnairePosInList);

      // Moving it to First position in the List
      rvwQnaireRespVersionList.add(0, getRvwQnaireRespVersObj());
    }

    constructToolTip(qnaireToolTip, rvwQnaireRespVersionList);
    setRvwQnaireRespVersObj(null);
  }

  /**
   * @param rvwQnaireResponse2
   * @return
   */
  private Predicate<RvwQnaireResponse> isGeneralQnaire() {
    return (rvwQnaireResponse -> ((null != (rvwQnaireResponse.getName())) &&
        (rvwQnaireResponse.getName().startsWith(ApicConstants.GENERAL_QUESTIONS) ||
            rvwQnaireResponse.getName().startsWith(ApicConstants.OBD_GENERAL_QUESTIONS))));
  }


  private void constructToolTip(final StringBuilder qnaireToolTip,
      final List<RvwQnaireRespVersion> rvwQnaireRespVersionList) {

    int counter = 1;

    for (RvwQnaireRespVersion qnaireRespVersion : rvwQnaireRespVersionList) {
      if (!qnaireToolTip.toString().isEmpty()) {
        qnaireToolTip.append("\n");
      }
      if (null != qnaireRespVersion) {
        RvwQnaireResponse rvwQnaireResponse =
            getCdrReportQnaireRespWrapper().getQnaireResponseMap().get(qnaireRespVersion.getId());
        qnaireToolTip.append(counter);
        qnaireToolTip.append(" : ");
        qnaireToolTip.append(rvwQnaireResponse.getName());
        if (CommonUtils.isNotNull(rvwQnaireResponse.getPrimaryVarRespWpName())) {
          qnaireToolTip.append(" - ");
          qnaireToolTip.append(rvwQnaireResponse.getPrimaryVarRespWpName());
        }
        qnaireToolTip.append("\n");
        qnaireToolTip.append("   Status : ");

        qnaireToolTip.append(
            isRvwQnaireRespVersWorkingSet().test(qnaireRespVersion) ? CDRConstants.QNAIRE_RESP_NOT_BASELINED_TOOLTIP
                : CDRConstants.QS_STATUS_TYPE.getTypeByDbCode(qnaireRespVersion.getQnaireRespVersStatus()).getUiType());

        counter++;
      }
    }
  }


  /**
   * @param paramName paramName
   * @return responsibility name
   */
  public String getRespName(final String paramName) {
    A2LParameter a2lParameter = this.a2lEditorDataProvider.getA2lFileInfoBO().getA2lParamMap(null).get(paramName);
    return getRespName(a2lParameter.getParamId());
  }

  /**
   * @param paramId paramId
   * @return responsible type
   */
  public String getRespType(final Long paramId) {
    ParamWpResponsibility respObj = getParamWpResp(paramId);
    if (respObj != null) {
      A2lResponsibility a2lResp = this.a2lEditorDataProvider.getA2lWpInfoBO().getA2lResponsibilityModel()
          .getA2lResponsibilityMap().get(respObj.getRespId());
      return this.a2lEditorDataProvider.getA2lWpInfoBO().getRespTypeName(a2lResp);
    }
    return ApicConstants.EMPTY_STRING;
  }

  /**
   * @param paramId
   * @return
   */
  private boolean ifValuePresent(final Long paramId) {
    if (null != getA2lEditorDataProvider().getA2lWpInfoBO().getA2lWpParamMappingModel()) {
      return getA2lEditorDataProvider().getA2lWpInfoBO().getA2lWpParamMappingModel().getParamIdWithWpAndRespMap()
          .containsKey(paramId);
    }
    return false;
  }

  /**
   * @param paramId parameter id
   * @return workpackage name
   */
  public String getWpName(final Long paramId) {
    if (ifValuePresent(paramId)) {
      ParamWpResponsibility respObj = getParamWpResp(paramId);
      if (respObj != null) {
        String wpName = this.a2lEditorDataProvider.getA2lWpInfoBO().getA2lWpDefnModel().getWpRespMap()
            .get(respObj.getWpRespId()).getName();
        if (null != wpName) {
          return wpName;
        }
      }
    }
    return ApicConstants.EMPTY_STRING;
  }


  /**
   * @param paramName paramName
   * @return the work package map
   */
  public String getWpName(final String paramName) {
    A2LParameter a2lParameter = this.a2lEditorDataProvider.getA2lFileInfoBO().getA2lParamMap(null).get(paramName);
    return getWpName(a2lParameter.getParamId());
  }

  /**
   * @param paramName parameter name
   * @return wpFinishedStatus
   */
  public String getWpFinishedRespStatuswithName(final String paramName) {
    A2LParameter a2lParameter = this.a2lEditorDataProvider.getA2lFileInfoBO().getA2lParamMap(null).get(paramName);
    return getWpFinishedRespStatus(a2lParameter.getParamId());
  }


  /**
   * @param paramName parameter name
   * @return the responsibility type
   */
  public String getRespType(final String paramName) {
    A2LParameter a2lParameter = this.a2lEditorDataProvider.getA2lFileInfoBO().getA2lParamMap(null).get(paramName);
    return getRespType(a2lParameter.getParamId());
  }

  /**
   * Get the a2l version(SDOM PVER Varian)t of the first review
   *
   * @param paramName String
   * @return latest a2l version(variant name
   */
  public String getLatestA2LVersion(final String paramName) {
    ReviewDetails rvwDet = getReviewDetailsLatest(paramName);
    return rvwDet == null ? null : rvwDet.getSdomPverVariant();
  }


  // ICDM-1701
  /**
   * @param paramName param name
   * @param colIndex selected col index
   * @return Review's a2l file
   */
  public long getReviewA2LFileId(final String paramName, final int colIndex) {
    ReviewDetails rvwDet = getReviewDetails(paramName, colIndex);
    return rvwDet == null ? 0 : rvwDet.getA2lFileID();
  }

  /**
   * Get latest Function version in the latest review
   *
   * @param paramName name of parameter
   * @return function version
   */
  public String getLatestFunctionVersion(final String paramName) {
    ParameterReviewDetails paramRvwDetails = getParamReviewDetailsLatest(paramName);
    return paramRvwDetails == null ? ApicConstants.EMPTY_STRING
        : this.cdrReport.getRvwFuncMap().get(paramRvwDetails.getFuncID());
  }

  /**
   * Get the review date of the parameter in the given review, identified by column index. The date is returned in
   * client's time zone.
   *
   * @param paramName name of parameter
   * @param colIndex review result number, identified by column index
   * @return review date as calendar, else null
   */
  public String getParamReviewDate(final String paramName, final int colIndex) {
    ReviewDetails rvwDet = getReviewDetails(paramName, colIndex);
    return rvwDet == null ? null : getReviewDate(rvwDet);
  }

  /**
   * Get the parameter review detail object for the given parameter and column index
   *
   * @param paramName name of parameter
   * @param colIndex review result number, identified by column index
   * @return ParameterReviewDetails
   */
  private ParameterReviewDetails getParamReviewDetails(final String paramName, final int colIndex) {
    List<ParameterReviewDetails> paramRvwDetList = this.cdrReport.getParamRvwDetMap().get(paramName);
    if ((paramRvwDetList != null) && (colIndex < paramRvwDetList.size())) {
      return paramRvwDetList.get(colIndex);
    }
    return null;
  }


  /**
   * Get the review detail object for the review represented by given parameter and column index
   *
   * @param paramName name of parameter
   * @param colIndex review result number, identified by column index
   * @return ReviewDetails
   */
  public ReviewDetails getReviewDetails(final String paramName, final int colIndex) {
    ParameterReviewDetails paramRvwDet = getParamReviewDetails(paramName, colIndex);
    return paramRvwDet == null ? null : this.cdrReport.getReviewDetMap().get(paramRvwDet.getRvwID());
  }

  /**
   * Get review date in client time zone
   *
   * @param rvwDet Review Details object
   * @return review date as a calendar object, with timezone
   */
  private String getReviewDate(final ReviewDetails rvwDet) {
    return rvwDet.getRvwDate();
  }

  /**
   * @return the reportGenDate
   */
  public Calendar getReportGenDate() {
    return this.reportGenDate;
  }

  /**
   * @return the maxReviews
   */
  public int getMaxReviews() {
    return this.maxReviews;
  }

  // ICDM-1723
  /**
   * @param paramName paramName
   * @param colIndex colIndex
   * @return checked value caldata obj
   * @throws IOException IOException
   * @throws ClassNotFoundException ClassNotFoundException
   */
  public CalData getParamCheckedVal(final String paramName, final int colIndex) {
    Map<Integer, CalData> checkVals = this.chechValMap.computeIfAbsent(paramName, k -> new HashMap<>());

    return checkVals.computeIfAbsent(colIndex, k -> {
      ParameterReviewDetails paramRvwDet = getParamReviewDetails(paramName, colIndex);
      if (paramRvwDet != null) {
        try {
          checkVals.put(colIndex, CalDataUtil.getCalDataObj(paramRvwDet.getCheckedVal()));
          return CalDataUtil.getCalDataObj(paramRvwDet.getCheckedVal());
        }
        catch (ClassNotFoundException | IOException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }
      return null;
    });

  }

  /**
   * @return the fetchCheckVal
   */
  public boolean isFetchCheckVal() {
    return this.fetchCheckVal;
  }

  /**
   * @param paramName paramName
   * @param colIdx colIdx
   * @return the
   */
  private String getReviewScoreForColIdx(final String paramName, final int colIdx) {
    ParameterReviewDetails paramReviewDetailsLatest = getParamReviewDetails(paramName, colIdx);
    if (paramReviewDetailsLatest == null) {
      return ApicConstants.EMPTY_STRING;
    }
    return paramReviewDetailsLatest.getReviewScore();
  }

  /**
   * @param paramName paramName
   * @param colIdx colIdx
   * @return the review score enum for given column index
   */
  public DATA_REVIEW_SCORE getReviewScore(final String paramName, final int colIdx) {
    String reviewScore = getReviewScoreForColIdx(paramName, colIdx);
    if ((reviewScore == null) || reviewScore.isEmpty()) {
      return null;
    }
    return DATA_REVIEW_SCORE.getType(reviewScore);

  }

  /**
   * @param paramName paramName
   * @param colIdx column index (starting from 0 for first review column)
   * @return true, if review is available for given input
   */
  public boolean hasReviewResult(final String paramName, final int colIdx) {
    return getParamReviewDetails(paramName, colIdx) != null;
  }


  /**
   * ICDM-2496 method to get check values of latest reviews in the format Map<String, CalData>.
   *
   * @param calDataObjectsFromHex the cal data objects from hex
   * @return map of key- param name, value - caldata object
   * @throws ClassNotFoundException the class not found exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public Map<String, CalData> getChkValCalDataMap(final Map<String, CalData> calDataObjectsFromHex)
      throws ClassNotFoundException, IOException {
    Map<String, CalData> calDataMap = new HashMap<>();
    Set<String> paramNames = calDataObjectsFromHex.keySet();

    // iterate the check value map
    for (String paramName : paramNames) {


      ParameterReviewDetails paramReviewDetails = getParamReviewDetails(paramName, 0);
      if (null != paramReviewDetails) {
        CalData calData = CalDataUtil.getCalDataObj(paramReviewDetails.getCheckedVal());
        // this check is done to avoid exception in CDF Writer
        if (calData != null) {
          calDataMap.put(paramName, calData);
        }
      }
    }
    return calDataMap;
  }


  /**
   * reviewed parameters with bosch responsibility count and total parameter count in an array
   *
   * @param paramSet set of parameters
   * @return percentage of reviewed parameter
   */
  public String getReviewedParameterWithBoschResp(final Set<String> paramSet) {
    int a2lReviewedCount = 0;
    double totalParamWithBoschResp = 0;
    for (String parameter : paramSet) {
      String respType = getRespType(parameter);
      String reviewData = isReviewedStr(parameter);
      if (WpRespType.RB.getDispName().equals(respType)) {
        totalParamWithBoschResp++;
        if (ApicConstants.REVIEWED.equalsIgnoreCase(reviewData)) {
          a2lReviewedCount++;
        }
      }
    }
    // Store reviewed parameters with bosch responsibility count and total parameter count in an array
    if (totalParamWithBoschResp > 0) {
      return findPercentage(a2lReviewedCount, totalParamWithBoschResp);
    }
    return String.format("%.3f", totalParamWithBoschResp);

  }

  /**
   * the count of number of parameters in bosch responsibility
   *
   * @param paramSet set of parameters
   * @return count of parameters
   */
  public int getParameterInBoschResp(final Set<String> paramSet) {
    int totalParamWithBoschResp = 0;
    for (String parameter : paramSet) {
      String respType = getRespType(parameter);
      if (WpRespType.RB.getDispName().equals(respType)) {
        totalParamWithBoschResp++;
      }
    }
    return totalParamWithBoschResp;

  }

  /**
   * reviewed parameters with bosch responsibility count and total parameter count in an array
   *
   * @param paramSet set of parameters
   * @return percentage of reviewed parameter
   */
  public String getRvwParamWithBoschRespForCompletedQnaire(final Set<String> paramSet) {
    int a2lReviewedCount = 0;
    double totalParamWithBoschResp = 0;
    for (String parameter : paramSet) {
      String respType = getRespType(parameter);
      String reviewData = isReviewedStr(parameter);
      String qnaireRespVersStatus = getQnaireRespVersStatus(parameter, false);
      if (WpRespType.RB.getDispName().equals(respType)) {
        totalParamWithBoschResp++;
        if (ApicConstants.REVIEWED.equalsIgnoreCase(reviewData) &&
            (CDRConstants.QS_STATUS_TYPE.ALL_POSITIVE.getDbType().equals(qnaireRespVersStatus) ||
                CDRConstants.NO_QNAIRE_STATUS.equals(qnaireRespVersStatus) ||
                CDRConstants.QS_STATUS_TYPE.NOT_ALL_POSITIVE.getDbType().equals(qnaireRespVersStatus))) {
          a2lReviewedCount++;
        }
      }
    }
    // Store reviewed parameters with bosch responsibility count and total parameter count in an array
    if (totalParamWithBoschResp > 0) {
      return findPercentage(a2lReviewedCount, totalParamWithBoschResp);
    }
    return String.format("%.3f", totalParamWithBoschResp);
  }

  /**
   * the count of number of parameters in bosch responsibility reviewed
   *
   * @param paramSet set of parameters
   * @return count of parameters reviewed
   */
  public int getParameterInBoschRespRvwed(final Set<String> paramSet) {
    int a2lBoschReviewedCount = 0;
    for (String parameter : paramSet) {
      String respType = getRespType(parameter);
      String reviewData = isReviewedStr(parameter);
      if (WpRespType.RB.getDispName().equals(respType) && ApicConstants.REVIEWED.equalsIgnoreCase(reviewData)) {
        a2lBoschReviewedCount++;
      }
    }
    return a2lBoschReviewedCount;
  }


  /**
   * getQnaireWithNegativeAnswersCount
   *
   * @return qnaire negative count
   */
  public int getQnaireWithNegativeAnswersCount() {
    Set<A2lWPRespModel> wpRespModelSet = new HashSet<>();
    int qnaireRespVersCount = 0;
    try {
      wpRespModelSet = new PidcA2lTreeStructureServiceClient().getA2lWpRespModelsForVarGrpWpDefnVersId(getVarGrpId(),
          getA2lEditorDataProvider().getA2lWpInfoBO().getActiveVers().getId());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog("Error occurred while getting wp responsibility : " + e.getLocalizedMessage(),
          e, Activator.PLUGIN_ID);
    }
    Map<Long, Map<Long, Set<RvwQnaireRespVersion>>> allQnaireRespVersMap =
        this.cdrReportQnaireRespWrapper.getAllWpRespQnaireRespVersMap();
    for (A2lWPRespModel wpRespModel : wpRespModelSet) {
      Long a2lRespId = wpRespModel.getA2lRespId();
      Long a2lWpId = wpRespModel.getA2lWpId();
      Map<Long, Set<RvwQnaireRespVersion>> wpIdAndQnaireMap = allQnaireRespVersMap.get(a2lRespId);
      if (CommonUtils.isNotEmpty(wpIdAndQnaireMap)) {
        qnaireRespVersCount =
            calculateQnaireWithNegativeAnswers(qnaireRespVersCount, a2lRespId, a2lWpId, wpIdAndQnaireMap);
      }
    }
    return qnaireRespVersCount;
  }

  /**
   * @param qnaireRespVersCount
   * @param a2lRespId
   * @param a2lWpId
   * @param wpIdAndQnaireMap
   * @return
   */
  private int calculateQnaireWithNegativeAnswers(int qnaireRespVersCount, final Long a2lRespId, final Long a2lWpId,
      final Map<Long, Set<RvwQnaireRespVersion>> wpIdAndQnaireMap) {
    Set<RvwQnaireRespVersion> rvwQnaireVerSet = wpIdAndQnaireMap.get(a2lWpId);
    if (CommonUtils.isNotEmpty(rvwQnaireVerSet)) {
      for (RvwQnaireRespVersion rvwQnaireRespVersion : rvwQnaireVerSet) {
        // rvwQnaireRespVersion is null for Simplified Qnaire
        if (CommonUtils.isNotNull(rvwQnaireRespVersion)) {
          boolean genRprtFrmWPResp = this.cdrReport.isToGenDataRvwRprtForWPResp();
          Map<Long, RvwQnaireRespVersModel> rvwQnaireRespVersModelMap =
              this.cdrReportQnaireRespWrapper.getRvwQnaireRespVersModelMap();
          if (((!genRprtFrmWPResp) || (isRprtGenFrmRespWp(a2lRespId, a2lWpId, genRprtFrmWPResp))) &&
              rvwQnaireRespVersModelMap.containsKey(rvwQnaireRespVersion.getId()) &&
              (rvwQnaireRespVersModelMap.get(rvwQnaireRespVersion.getId()).getQnaireNegativeAnsCount() != 0)) {
            qnaireRespVersCount++;
          }
        }
      }
    }

    return qnaireRespVersCount;
  }


  /**
   * @param respWpQnaireRespVersMap contains Resp Qnaire Details
   * @param wpQnaireRespMap contains WP and Qnaire details
   * @param genRprtFrmWPResp equals tru if report is generated from Wp and Resp nodes
   * @return true if report generated from WP/Resp
   */
  public boolean isRprtGenFrmRespWp(final Long a2lRespId, final Long a2lWpId, final boolean genRprtFrmWPResp) {
    // if report is generated from responsibility Wp ID would be null
    return (genRprtFrmWPResp &&
        ((CommonUtils.isNull(this.selA2lWpId) && CommonUtils.isEqualIgnoreNull(a2lRespId, this.selA2lRespId)) ||
            CommonUtils.isEqualIgnoreNull(a2lWpId, this.selA2lWpId)));
  }

  private String findPercentage(final double obtainedParam, final double totalParam) {
    double number = (obtainedParam * 100) / totalParam;
    return String.format("%.3f", number);
  }

  /**
   * @param comparedObj param in compare hex
   * @return Result value
   */
  public Object getResult(final CompHexWithCDFParam comparedObj) {
    Object result;
    if (comparedObj.isQssdParameter()) {
      if (comparedObj.isCompli() && !CompliResValues.OK.equals(comparedObj.getCompliResult())) {
        result = comparedObj.getCompliResult().getUiValue();
      }
      else {
        result = comparedObj.getQssdResult().getUiValue();
      }
    }
    else {
      result = comparedObj.getCompliResult().getUiValue();
    }
    return result;
  }

  /**
   * @return A2L file object
   */
  public A2LFile getA2lFile() {
    return this.cdrReport.getA2lFile();
  }


  /**
   * @return the a2lWPResponsibleMap
   */
  public Map<String, SortedSet<A2lWpResponsibility>> getA2lWPResponsibleMap() {
    return this.a2lWPResponsibleMap;
  }


  /**
   * @return the paramIdWithWpAndRespMap
   */
  public Map<Long, Map<Long, String>> getParamIdWithWpAndRespMap() {
    return this.paramIdWithWpAndRespMap;
  }


  /**
   * @return the paramAndRespPalMap
   */
  public Map<Long, Set<Long>> getParamAndRespPalMap() {
    return this.paramAndRespPalMap;
  }


  /**
   * @param paramAndRespPalMap the paramAndRespPalMap to set
   */
  public void setParamAndRespPalMap(final Map<Long, Set<Long>> paramAndRespPalMap) {
    this.paramAndRespPalMap = paramAndRespPalMap;
  }

  /**
   * @return the wpRespParamMap
   */
  public Map<Long, Map<Long, Set<Long>>> getWpRespParamMap() {
    return this.wpRespParamMap;
  }


  /**
   * @return the cdrReportQnaireRespWrapper
   */
  public CdrReportQnaireRespWrapper getCdrReportQnaireRespWrapper() {
    return this.cdrReportQnaireRespWrapper;
  }


  /**
   * @param cdrReportQnaireRespWrapper the cdrReportQnaireRespWrapper to set
   */
  public void setCdrReportQnaireRespWrapper(final CdrReportQnaireRespWrapper cdrReportQnaireRespWrapper) {
    this.cdrReportQnaireRespWrapper = cdrReportQnaireRespWrapper;
  }


  /**
   * Forms a Map of RespType and A2lResponsibility from available Work Package - A2lResponsibility Mapping
   *
   * @return map of Key - String of Resposibility Type and Value - Set<A2lResponsibility> of Resposibility
   */
  public Map<String, Set<A2lResponsibility>> getRespTypeAndRespMap() {
    Map<String, Set<A2lResponsibility>> respTypeAndRespMap = new TreeMap<>();

    getA2lWPResponsibleMap().keySet().forEach(respName -> {
      for (A2lResponsibility a2lResponsibility : this.a2lEditorDataProvider.getA2lWpInfoBO().getA2lResponsibilityModel()
          .getA2lResponsibilityMap().values()) {
        if (a2lResponsibility.getName().equals(respName)) {
          String key = WpRespType.getType(a2lResponsibility.getRespType()).getDispName();
          if (respTypeAndRespMap.containsKey(key)) {
            Set<A2lResponsibility> value = respTypeAndRespMap.get(key);
            value.add(a2lResponsibility);
            respTypeAndRespMap.put(key, value);
          }
          else {
            Set<A2lResponsibility> value = new HashSet<>();
            value.add(a2lResponsibility);
            respTypeAndRespMap.put(key, value);
          }
        }
      }
    });

    return respTypeAndRespMap;
  }


  /**
   * @return the selA2lRespId
   */
  public Long getSelA2lRespId() {
    return this.selA2lRespId;
  }


  /**
   * @return the selA2lWpId
   */
  public Long getSelA2lWpId() {
    return this.selA2lWpId;
  }


  /**
   * @return the wpFinishedStatusMap
   */
  public Map<Long, Map<Long, String>> getWpFinishedStatusMap() {
    return this.wpFinishedStatusMap;
  }


  /**
   * @param wpFinishedStatusMap the wpFinishedStatusMap to set
   */
  public void setWpFinishedStatusMap(final Map<Long, Map<Long, String>> wpFinishedStatusMap) {
    this.wpFinishedStatusMap = wpFinishedStatusMap;
  }

  /**
   * @return A2l WP Responsibility Model at variant
   */
  public Set<A2lWPRespModel> getWPRespModelAtVariant() {
    return this.paramWpResolver.getWPRespForVariant(getVarGrpId());
  }

  public A2lResponsibility getResponsibilityForRespName(final String respName) {

    A2lResponsibility a2lResponsibility = null;
    for (A2lResponsibility a2lResp : getA2lEditorDataProvider().getA2lWpInfoBO().getA2lResponsibilityModel()
        .getA2lResponsibilityMap().values()) {
      if (CommonUtils.isEqual(a2lResp.getName(), respName)) {
        a2lResponsibility = a2lResp;
        break;
      }
    }

    return a2lResponsibility;
  }

  public boolean wpRespFinishAccessValidation(final Long a2lRespId, final Long pidcId) {
    boolean accessFlag = false;
    try {
      accessFlag = new RvwQnaireResponseServiceClient().validateQnaireAccess(pidcId, a2lRespId);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return accessFlag;
  }


  public void populateWpRespModelForRespNode(final Long a2lRespId, final Set<A2lWpResponsibility> a2lWpRespSet,
      final Set<A2lWPRespModel> wpRespModelAtVariant, final Map<Long, A2lWPRespModel> wpA2lWpRespModelMap) {
    for (A2lWpResponsibility a2lWpResp : a2lWpRespSet) {
      wpA2lWpRespModelMap.put(a2lWpResp.getA2lWpId(),
          resolveA2lWpRespModel(a2lRespId, a2lWpResp.getA2lWpId(), wpRespModelAtVariant));
    }
  }

  /**
   * @param a2lRespId
   * @param a2lWpId
   * @param pidcVariantID
   * @param wpRespModelAtVariant
   * @return
   */
  private A2lWPRespModel resolveA2lWpRespModel(final Long a2lRespId, final Long a2lWpId,
      final Set<A2lWPRespModel> wpRespModelAtVariant) {

    Optional<A2lWPRespModel> a2lWpRespModelOptl = wpRespModelAtVariant.stream()
        .filter(wpRespModel -> wpRespModel.getA2lRespId().equals(a2lRespId) && wpRespModel.getA2lWpId().equals(a2lWpId))
        .findAny();

    return a2lWpRespModelOptl.isPresent() ? a2lWpRespModelOptl.get() : createDummyA2lWpRespModel(a2lRespId, a2lWpId);
  }

  /**
   * @param a2lRespId
   * @param a2lWpId
   * @return A2lWPRespModel
   */
  private A2lWPRespModel createDummyA2lWpRespModel(final Long a2lRespId, final Long a2lWpId) {

    A2lWPRespModel a2lWpRespModel = new A2lWPRespModel();
    a2lWpRespModel.setA2lRespId(a2lRespId);
    a2lWpRespModel.setA2lWpId(a2lWpId);

    return a2lWpRespModel;
  }

  /**
   * @return the rvwQnaireRespVersObj
   */
  public RvwQnaireRespVersion getRvwQnaireRespVersObj() {
    return this.rvwQnaireRespVersObj;
  }


  /**
   * @param rvwQnaireRespVersObj the rvwQnaireRespVersObj to set
   */
  public void setRvwQnaireRespVersObj(final RvwQnaireRespVersion rvwQnaireRespVersObj) {
    this.rvwQnaireRespVersObj = rvwQnaireRespVersObj;
  }


}
