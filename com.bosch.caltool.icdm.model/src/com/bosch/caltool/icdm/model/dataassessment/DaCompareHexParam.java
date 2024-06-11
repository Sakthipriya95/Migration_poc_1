/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.dataassessment;


import com.bosch.caltool.icdm.model.cdr.CDRConstants.ParameterType;
import com.bosch.caltool.icdm.model.cdr.CompliResValues;
import com.bosch.caltool.icdm.model.cdr.QSSDResValues;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author hnu1cob
 */
public class DaCompareHexParam implements Comparable<DaCompareHexParam> {

  /** true for compliance parameter. */
  private boolean isCompli;

  /** true for qssd parameter. */
  private boolean isQssdParameter;

  /**
   * true for black listed parameters
   */
  private boolean isBlackList;

  /**
   * true for readOnly parameters
   */
  private boolean readOnly;

  /**
   * true for Dependent characteristics parameters
   */
  private boolean isDependantCharacteristic;
  /**
   * Characteristics(parameter) is Dependent on Characteristics (Not stored in DB,Filled in Client Side)
   */
  private String[] depCharsName;

  /** param type. */
  private ParameterType paramType;

  /**
   * Parameter ID
   */
  private Long parameterId;

  /** paramater name. */
  private String paramName;

  /** function name. */
  private String funcName;

  /** function version. */
  private String funcVers;

  /** true if param is reviewed. */
  private boolean isReviewed;

  /** true if param is never reviewed. */
  private boolean neverReviewed;

  /** true if the cal data objects are equal. */
  private boolean isEqual;

  /** The hex cal data */
  private byte[] hexValue;

  /** The cdfx cal data */
  private byte[] reviewedValue;

  /** The compli tooltip. */
  private String compliTooltip;

  /** The QSSD tooltip. */
  private String qssdTooltip;

  /** The COMPLI result. */
  private CompliResValues compliResult;

  /** The QSSD result. */
  private QSSDResValues qssdResult;

  /** The workpackage. */
  private String wpName;

  /**
   * The Responsibility type.
   */
  private String respType;

  /** The responsibility. */
  private String respName;

  /** WP Finished Status */
  private String wpFinishedStatus;

  /** The latest A 2 l version. */
  private String latestA2lVersion;

  /** The latest function version. */
  private String latestFunctionVersion;

  /** Questionnaire Status */
  private String qnaireStatus;

  /** The Review Score.[DB Type of DATA_REVIEW_SCORE] */
  private String reviewScore;

  /** The Hundred Percent Review Score.[Hundred Percent Display value of DATA_REVIEW_SCORE] */
  private String hundredPecReviewScore;

  /** The latest Review comments. */
  private String latestReviewComments;

  /**
   * Review result Id
   */
  private Long cdrResultId;

  /**
   * Name of the review result
   */
  private String rvwResultName;

  /**
   * Review parameter ID
   */
  private Long rvwParamId;

  /**
   * @return the isCompli
   */
  public boolean isCompli() {
    return this.isCompli;
  }

  /**
   * @param isCompli the isCompli to set
   */
  public void setCompli(final boolean isCompli) {
    this.isCompli = isCompli;
  }

  /**
   * @return the isQssdParameter
   */
  public boolean isQssdParameter() {
    return this.isQssdParameter;
  }

  /**
   * @param isQssdParameter the isQssdParameter to set
   */
  public void setQssdParameter(final boolean isQssdParameter) {
    this.isQssdParameter = isQssdParameter;
  }

  /**
   * @return the isBlackList
   */
  public boolean isBlackList() {
    return this.isBlackList;
  }

  /**
   * @param isBlackList the isBlackList to set
   */
  public void setBlackList(final boolean isBlackList) {
    this.isBlackList = isBlackList;
  }

  /**
   * @return the readOnly
   */
  public boolean isReadOnly() {
    return this.readOnly;
  }

  /**
   * @param readOnly the readOnly to set
   */
  public void setReadOnly(final boolean readOnly) {
    this.readOnly = readOnly;
  }

  /**
   * @return the isDependantCharacteristic
   */
  public boolean isDependantCharacteristic() {
    return this.isDependantCharacteristic;
  }

  /**
   * @param isDependantCharacteristic the isDependantCharacteristic to set
   */
  public void setDependantCharacteristic(final boolean isDependantCharacteristic) {
    this.isDependantCharacteristic = isDependantCharacteristic;
  }

  /**
   * @return the depCharsName
   */
  public String[] getDepCharsName() {
    return this.depCharsName;
  }

  /**
   * @param depCharsName the depCharsName to set
   */
  public void setDepCharsName(final String[] depCharsName) {
    this.depCharsName = depCharsName;
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
   * @return the parameterId
   */
  public Long getParameterId() {
    return this.parameterId;
  }


  /**
   * @param parameterId the parameterId to set
   */
  public void setParameterId(final Long parameterId) {
    this.parameterId = parameterId;
  }

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
   * @return the funcVers
   */
  public String getFuncVers() {
    return this.funcVers;
  }

  /**
   * @param funcVers the funcVers to set
   */
  public void setFuncVers(final String funcVers) {
    this.funcVers = funcVers;
  }

  /**
   * @return the isReviewed
   */
  public boolean isReviewed() {
    return this.isReviewed;
  }

  /**
   * @param isReviewed the isReviewed to set
   */
  public void setReviewed(final boolean isReviewed) {
    this.isReviewed = isReviewed;
  }

  /**
   * @return the neverReviewed
   */
  public boolean isNeverReviewed() {
    return this.neverReviewed;
  }

  /**
   * @param neverReviewed the neverReviewed to set
   */
  public void setNeverReviewed(final boolean neverReviewed) {
    this.neverReviewed = neverReviewed;
  }

  /**
   * @return the isEqual
   */
  public boolean isEqual() {
    return this.isEqual;
  }

  /**
   * @param isEqual the isEqual to set
   */
  public void setEqual(final boolean isEqual) {
    this.isEqual = isEqual;
  }

  /**
   * @return the hexValue
   */
  public byte[] getHexValue() {
    return this.hexValue;
  }

  /**
   * @param hexValue the hexValue to set
   */
  public void setHexValue(final byte[] hexValue) {
    this.hexValue = hexValue;
  }

  /**
   * @return the reviewedValue
   */
  public byte[] getReviewedValue() {
    return this.reviewedValue;
  }

  /**
   * @param reviewedValue the reviewedValue to set
   */
  public void setReviewedValue(final byte[] reviewedValue) {
    this.reviewedValue = reviewedValue;
  }

  /**
   * @return the compliTooltip
   */
  public String getCompliTooltip() {
    return this.compliTooltip;
  }

  /**
   * @param compliTooltip the compliTooltip to set
   */
  public void setCompliTooltip(final String compliTooltip) {
    this.compliTooltip = compliTooltip;
  }

  /**
   * @return the qssdTooltip
   */
  public String getQssdTooltip() {
    return this.qssdTooltip;
  }

  /**
   * @param qssdTooltip the qssdTooltip to set
   */
  public void setQssdTooltip(final String qssdTooltip) {
    this.qssdTooltip = qssdTooltip;
  }

  /**
   * @return the compliResult
   */
  public CompliResValues getCompliResult() {
    return this.compliResult;
  }

  /**
   * @param compliResult the compliResult to set
   */
  public void setCompliResult(final CompliResValues compliResult) {
    this.compliResult = compliResult;
  }

  /**
   * @return the qssdResult
   */
  public QSSDResValues getQssdResult() {
    return this.qssdResult;
  }

  /**
   * @param qssdResult the qssdResult to set
   */
  public void setQssdResult(final QSSDResValues qssdResult) {
    this.qssdResult = qssdResult;
  }

  /**
   * @return the wpName
   */
  public String getWpName() {
    return this.wpName;
  }

  /**
   * @param wpName the wpName to set
   */
  public void setWpName(final String wpName) {
    this.wpName = wpName;
  }

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

  /**
   * @return the respName
   */
  public String getRespName() {
    return this.respName;
  }

  /**
   * @param respName the respName to set
   */
  public void setRespName(final String respName) {
    this.respName = respName;
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
   * @return the latestFunctionVersion
   */
  public String getLatestFunctionVersion() {
    return this.latestFunctionVersion;
  }

  /**
   * @param latestFunctionVersion the latestFunctionVersion to set
   */
  public void setLatestFunctionVersion(final String latestFunctionVersion) {
    this.latestFunctionVersion = latestFunctionVersion;
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

  /**
   * @return the hundredPecReviewScore
   */
  public String getHundredPecReviewScore() {
    return this.hundredPecReviewScore;
  }

  /**
   * @param hundredPecReviewScore the hundredPecReviewScore to set
   */
  public void setHundredPecReviewScore(final String hundredPecReviewScore) {
    this.hundredPecReviewScore = hundredPecReviewScore;
  }

  /**
   * @return the latestReviewComments
   */
  public String getLatestReviewComments() {
    return this.latestReviewComments;
  }

  /**
   * @param latestReviewComments the latestReviewComments to set
   */
  public void setLatestReviewComments(final String latestReviewComments) {
    this.latestReviewComments = latestReviewComments;
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

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    DaCompareHexParam other = (DaCompareHexParam) obj;
    return getParamName().equals(other.getParamName());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final DaCompareHexParam object) {
    return ModelUtil.compare(getParamName(), object.getParamName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getParamName());
  }

  /**
   * @return the cdrResultId
   */
  public Long getCdrResultId() {
    return this.cdrResultId;
  }

  /**
   * @param cdrResultId the cdrResultId to set
   */
  public void setCdrResultId(final Long cdrResultId) {
    this.cdrResultId = cdrResultId;
  }


  /**
   * @return the rvwResultName
   */
  public String getRvwResultName() {
    return this.rvwResultName;
  }


  /**
   * @param rvwResultName the rvwResultName to set
   */
  public void setRvwResultName(final String rvwResultName) {
    this.rvwResultName = rvwResultName;
  }

  /**
   * @return the rvwParamId
   */
  public Long getRvwParamId() {
    return this.rvwParamId;
  }

  /**
   * @param rvwParamId the rvwParamId to set
   */
  public void setRvwParamId(final Long rvwParamId) {
    this.rvwParamId = rvwParamId;
  }


}
