/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.comphex;

import com.bosch.caltool.icdm.model.cdr.CDRConstants.ParameterType;
import com.bosch.caltool.icdm.model.cdr.CompliResValues;
import com.bosch.caltool.icdm.model.cdr.QSSDResValues;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * ICDM-2496 class to store values for each row in comparison of CDFx and HEX file.
 *
 * @author mkl2cob
 */
public class CompHexWithCDFParam implements Comparable<CompHexWithCDFParam> {

  /** paramater name. */
  private String paramName;

  /** function name. */
  private String funcName;

  /** true if param is reviewed. */
  private boolean isReviewed;

  /** true if param is never reviewed. */
  private boolean neverReviewed;

  /** true if the cal data objects are equal. */
  private boolean isEqual;

  /** The hex cal data phy simple disp value. */
  private String hexCalDataPhySimpleDispValue;

  /** The cdfx cal data phy simple disp value. */
  private String cdfxCalDataPhySimpleDispValue;

  /** param type. */
  private ParameterType paramType;

  /** true for compliance parameter. */
  private boolean isCompli;

  /** true for qssd parameter. */
  private boolean isQssdParameter;

  /**
   * true for black listed parameters
   */
  private boolean isBlackList;

  /** The is sr rev done. */
  private boolean isSrRevDone;

  /** The compli tooltip. */
  private String compliTooltip;

  /** The compli tooltip. */
  private String qssdTooltip;

  /** function version. */
  private String funcVers;

  /** The COMPLI result. */
  private CompliResValues compliResult;

  /** The QSSD result. */
  private QSSDResValues qssdResult;

  /** The Review result. */
  private String reviewResult;

  /** The rule usecase. */
  private String usecase;

  /** The workpackage. */
  private String wpName;

  /** The wp resp. */
  private String wpResp;

  /** The latest A 2 l version. */
  private String latestA2lVersion;

  /** The latest function version. */
  private String latestFunctionVersion;

  private boolean readOnly;

  private boolean isDependantCharacteristic;

  /** The latest Review comments. */

  private String latestReviewComments;

  /** The Review Score. */

  private String reviewScore;

  /** The Hundred Percent Review Score. */
  private String hundredPecReviewScore;

  private String[] depCharsName;


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
   * @return the hexCalDataPhySimpleDispValue
   */
  public String getHexCalDataPhySimpleDispValue() {
    return this.hexCalDataPhySimpleDispValue;
  }


  /**
   * @param hexCalDataPhySimpleDispValue the hexCalDataPhySimpleDispValue to set
   */
  public void setHexCalDataPhySimpleDispValue(final String hexCalDataPhySimpleDispValue) {
    this.hexCalDataPhySimpleDispValue = hexCalDataPhySimpleDispValue;
  }


  /**
   * @return the cdfxCalDataPhySimpleDispValue
   */
  public String getCdfxCalDataPhySimpleDispValue() {
    return this.cdfxCalDataPhySimpleDispValue;
  }


  /**
   * @param cdfxCalDataPhySimpleDispValue the cdfxCalDataPhySimpleDispValue to set
   */
  public void setCdfxCalDataPhySimpleDispValue(final String cdfxCalDataPhySimpleDispValue) {
    this.cdfxCalDataPhySimpleDispValue = cdfxCalDataPhySimpleDispValue;
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
   * @return the isSrRevDone
   */
  public boolean isSrRevDone() {
    return this.isSrRevDone;
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
   * @param isSrRevDone the isSrRevDone to set
   */
  public void setSrRevDone(final boolean isSrRevDone) {
    this.isSrRevDone = isSrRevDone;
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
   * @return the compliResult
   */
  public CompliResValues getCompliResult() {
    return this.compliResult;
  }


  /**
   * @return the readOnly
   */
  public boolean isReadOnly() {
    return this.readOnly;
  }


  /**
   * @param compliResult the compliResult to set
   */
  public void setCompliResult(final CompliResValues compliResult) {
    this.compliResult = compliResult;
  }


  /**
   * @return the reviewResult
   */
  public String getReviewResult() {
    return this.reviewResult;
  }


  /**
   * @param reviewResult the reviewResult to set
   */
  public void setReviewResult(final String reviewResult) {
    this.reviewResult = reviewResult;
  }


  /**
   * @return the usecase
   */
  public String getUsecase() {
    return this.usecase;
  }


  /**
   * @param usecase the usecase to set
   */
  public void setUsecase(final String usecase) {
    this.usecase = usecase;
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
   * @return the wpResp
   */
  public String getWpResp() {
    return this.wpResp;
  }


  /**
   * @param wpResp the wpResp to set
   */
  public void setWpResp(final String wpResp) {
    this.wpResp = wpResp;
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
   * @param isEqual the isEqual to set
   */
  public void setEqual(final boolean isEqual) {
    this.isEqual = isEqual;
  }


  /**
   * @return the isEqual
   */
  public boolean isEqual() {
    return this.isEqual;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CompHexWithCDFParam object) {
    return ModelUtil.compare(getParamName(), object.getParamName());
  }

  /**
   * {@inheritDoc}
   */

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
    CompHexWithCDFParam other = (CompHexWithCDFParam) obj;
    return getParamName().equals(other.getParamName());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getParamName());
  }


  /**
   * @param readOnly readOnly
   */
  public void setReadOnly(final boolean readOnly) {
    this.readOnly = readOnly;

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
   * @return the depChars
   */
  public String[] getDepCharsName() {
    return this.depCharsName;
  }


  /**
   * @param depChars the depChars to set
   */
  public void setDepCharsName(final String[] depChars) {
    this.depCharsName = depChars;
  }


}
