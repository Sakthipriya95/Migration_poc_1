/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.icdm.model.cdr.CDRConstants.ParameterType;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;

/**
 * @author hnu1cob
 */
public class CDRReportParameter {

  private String paramName;

  private ParameterType paramType;

  private Long paramId;

  private String codeWordStr;

  private String funcName;

  private String funcVersion;

  private boolean isCompliParam;

  private boolean isQssdParam;

  private boolean isReadOnlyParam;

  private boolean isBlackListParam;

  private boolean isDependentParam;

  private String[] dependentCharacteristics;

  private String a2lWpName;

  private Long a2lWpId;

  private String a2lRespName;

  private Long a2lRespId;

  private String respType;

  private String reviewScore;


  /**
   * @return the respType
   */
  public String getRespType() {
    return this.respType;
  }


  /**
   * @param respType the respType to set
   */
  public void setRespType(final String respType) {
    this.respType = respType;
  }

  private Long wpRespId;

  private String latestA2lVersion;

  private String latestFuncVersion;

  private String reviewedStatus;

  private String latestReviewComment;

  private final List<ReviewDetails> rvwDetails = new ArrayList<>();

  private final Map<Long, ParameterReviewDetails> paramRvwDetailsMap = new HashMap<>();

  private String qnaireStatus;

  private final SortedSet<RvwQnaireRespVersion> rvwQnaireVersSet = new TreeSet<>();

  private String wpFinishedStatus;

  /**
   * @return the paramName
   */
  public String getParamName() {
    return this.paramName;
  }

  /**
   * @param paramName the paramName to set
   */
  public void setParamName(final String paramName) {
    this.paramName = paramName;
  }

  /**
   * @return the paramType
   */
  public ParameterType getParamType() {
    return this.paramType;
  }

  /**
   * @param paramType the paramType to set
   */
  public void setParamType(final ParameterType paramType) {
    this.paramType = paramType;
  }

  /**
   * @return the paramId
   */
  public Long getParamId() {
    return this.paramId;
  }

  /**
   * @param paramId the paramId to set
   */
  public void setParamId(final Long paramId) {
    this.paramId = paramId;
  }

  /**
   * @return the codeWordStr
   */
  public String getCodeWordStr() {
    return this.codeWordStr;
  }

  /**
   * @param codeWordStr the codeWordStr to set
   */
  public void setCodeWordStr(final String codeWordStr) {
    this.codeWordStr = codeWordStr;
  }

  /**
   * @return the funcName
   */
  public String getFuncName() {
    return this.funcName;
  }

  /**
   * @param funcName the funcName to set
   */
  public void setFuncName(final String funcName) {
    this.funcName = funcName;
  }

  /**
   * @return the funcVersion
   */
  public String getFuncVersion() {
    return this.funcVersion;
  }

  /**
   * @param funcVersion the funcVersion to set
   */
  public void setFuncVersion(final String funcVersion) {
    this.funcVersion = funcVersion;
  }

  /**
   * @return the isCompliParam
   */
  public boolean isCompliParam() {
    return this.isCompliParam;
  }

  /**
   * @param isCompliParam the isCompliParam to set
   */
  public void setCompliParam(final boolean isCompliParam) {
    this.isCompliParam = isCompliParam;
  }

  /**
   * @return the isQssdParam
   */
  public boolean isQssdParam() {
    return this.isQssdParam;
  }

  /**
   * @param isQssdParam the isQssdParam to set
   */
  public void setQssdParam(final boolean isQssdParam) {
    this.isQssdParam = isQssdParam;
  }

  /**
   * @return the isReadOnlyParam
   */
  public boolean isReadOnlyParam() {
    return this.isReadOnlyParam;
  }

  /**
   * @param isReadOnlyParam the isReadOnlyParam to set
   */
  public void setReadOnlyParam(final boolean isReadOnlyParam) {
    this.isReadOnlyParam = isReadOnlyParam;
  }

  /**
   * @return the isBlackListParam
   */
  public boolean isBlackListParam() {
    return this.isBlackListParam;
  }

  /**
   * @param isBlackListParam the isBlackListParam to set
   */
  public void setBlackListParam(final boolean isBlackListParam) {
    this.isBlackListParam = isBlackListParam;
  }

  /**
   * @return the isDependentParam
   */
  public boolean isDependentParam() {
    return this.isDependentParam;
  }

  /**
   * @param isDependentParam the isDependentParam to set
   */
  public void setDependentParam(final boolean isDependentParam) {
    this.isDependentParam = isDependentParam;
  }

  /**
   * @return the dependentCharacteristics
   */
  public String[] getDependentCharacteristics() {
    return this.dependentCharacteristics;
  }

  /**
   * @param dependentCharacteristics the dependentCharacteristics to set
   */
  public void setDependentCharacteristics(final String[] dependentCharacteristics) {
    this.dependentCharacteristics = dependentCharacteristics;
  }

  /**
   * @return the a2lWpName
   */
  public String getA2lWpName() {
    return this.a2lWpName;
  }

  /**
   * @param a2lWpName the a2lWpName to set
   */
  public void setA2lWpName(final String a2lWpName) {
    this.a2lWpName = a2lWpName;
  }

  /**
   * @return the a2lWpId
   */
  public Long getA2lWpId() {
    return this.a2lWpId;
  }

  /**
   * @param a2lWpId the a2lWpId to set
   */
  public void setA2lWpId(final Long a2lWpId) {
    this.a2lWpId = a2lWpId;
  }

  /**
   * @return the a2lRespName
   */
  public String getA2lRespName() {
    return this.a2lRespName;
  }

  /**
   * @param a2lRespName the a2lRespName to set
   */
  public void setA2lRespName(final String a2lRespName) {
    this.a2lRespName = a2lRespName;
  }

  /**
   * @return the a2lRespId
   */
  public Long getA2lRespId() {
    return this.a2lRespId;
  }

  /**
   * @param a2lRespId the a2lRespId to set
   */
  public void setA2lRespId(final Long a2lRespId) {
    this.a2lRespId = a2lRespId;
  }

  /**
   * @return the wpRespId
   */
  public Long getWpRespId() {
    return this.wpRespId;
  }

  /**
   * @param wpRespId the wpRespId to set
   */
  public void setWpRespId(final Long wpRespId) {
    this.wpRespId = wpRespId;
  }

  /**
   * @return the latestA2lVersion
   */
  public String getLatestA2lVersion() {
    return this.latestA2lVersion;
  }

  /**
   * @param latestA2lVersion the latestA2lVersion to set
   */
  public void setLatestA2lVersion(final String latestA2lVersion) {
    this.latestA2lVersion = latestA2lVersion;
  }

  /**
   * @return the latestFuncVersion
   */
  public String getLatestFuncVersion() {
    return this.latestFuncVersion;
  }

  /**
   * @param latestFuncVersion the latestFuncVersion to set
   */
  public void setLatestFuncVersion(final String latestFuncVersion) {
    this.latestFuncVersion = latestFuncVersion;
  }

  /**
   * @return the reviewedStatus
   */
  public String getReviewedStatus() {
    return this.reviewedStatus;
  }

  /**
   * @param reviewedStatus the reviewedStatus to set
   */
  public void setReviewedStatus(final String reviewedStatus) {
    this.reviewedStatus = reviewedStatus;
  }

  /**
   * @return the latestReviewComment
   */
  public String getLatestReviewComment() {
    return this.latestReviewComment;
  }

  /**
   * @param latestReviewComment the latestReviewComment to set
   */
  public void setLatestReviewComment(final String latestReviewComment) {
    this.latestReviewComment = latestReviewComment;
  }

  /**
   * @return the rvwDetails
   */
  public List<ReviewDetails> getRvwDetails() {
    return this.rvwDetails;
  }

  /**
   * @return the paramRvwDetailsMap
   */
  public Map<Long, ParameterReviewDetails> getParamRvwDetailsMap() {
    return this.paramRvwDetailsMap;
  }

  /**
   * @return the qnaireStatus
   */
  public String getQnaireStatus() {
    return this.qnaireStatus;
  }

  /**
   * @param qnaireStatus the qnaireStatus to set
   */
  public void setQnaireStatus(final String qnaireStatus) {
    this.qnaireStatus = qnaireStatus;
  }

  /**
   * @return the rvwQnaireVersSet
   */
  public SortedSet<RvwQnaireRespVersion> getRvwQnaireVersSet() {
    return this.rvwQnaireVersSet;
  }

  /**
   * @return the wpFinishedStatus
   */
  public String getWpFinishedStatus() {
    return this.wpFinishedStatus;
  }

  /**
   * @param wpFinishedStatus the wpFinishedStatus to set
   */
  public void setWpFinishedStatus(final String wpFinishedStatus) {
    this.wpFinishedStatus = wpFinishedStatus;
  }


  /**
   * @return the reviewScore
   */
  public String getReviewScore() {
    return this.reviewScore;
  }


  /**
   * @param reviewScore the reviewScore to set
   */
  public void setReviewScore(final String reviewScore) {
    this.reviewScore = reviewScore;
  }
}
