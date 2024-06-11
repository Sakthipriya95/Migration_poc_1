package com.bosch.caltool.icdm.model.cdr;

import java.math.BigDecimal;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * DaParameter Model class
 *
 * @author say8cob
 */
public class DaParameter implements Cloneable, Comparable<DaParameter>, IModel {

  /**
   * Serial UID
   */
  private final static long serialVersionUID = 118019809191396L;
  /**
   * Da Param Id
   */
  private Long id;
  /**
   * Da Wp Resp Id
   */
  private Long daWpRespId;
  /**
   * Parameter Id
   */
  private BigDecimal parameterId;
  /**
   * Parameter Name
   */
  private String parameterName;
  /**
   * Parameter Type
   */
  private String parameterType;
  /**
   * Function Name
   */
  private String functionName;
  /**
   * Function Version
   */
  private String functionVersion;
  /**
   * Rvw A2l Version
   */
  private String rvwA2lVersion;
  /**
   * Rvw Func Version
   */
  private String rvwFuncVersion;
  /**
   * Questionnaire Status
   */
  private String questionnaireStatus;
  /**
   * Reviewed Flag
   */
  private String reviewedFlag;
  /**
   * Equals Flag
   */
  private String equalsFlag;
  /**
   * Compli Result
   */
  private String compliResult;
  /**
   * Review Score
   */
  private String reviewScore;
  /**
   * Review Remark
   */
  private String reviewRemark;
  /**
   * Result Id
   */
  private BigDecimal resultId;
  /**
   * Rvw Param Id
   */
  private BigDecimal rvwParamId;
  /**
   * Rvw Result Name
   */
  private String rvwResultName;
  /**
   * Created Date
   */
  private String createdDate;
  /**
   * Created User
   */
  private String createdUser;
  /**
   * Modified Date
   */
  private String modifiedDate;
  /**
   * Modified User
   */
  private String modifiedUser;
  /**
   * Version
   */
  private Long version;
  /**
   * Hex Value
   */
  private byte[] hexValue;
  /**
   * Reviewed Value
   */
  private byte[] reviewedValue;
  /**
   * true, if Compliance Parameter
   */
  private String compliFlag;
  /**
   * true, if Qssd Parameter
   */
  private String qssdFlag;
  /**
   * true, if Read Only Parameter
   */
  private String readOnlyFlag;
  /**
   * true, if Paramter is Dependent Characteristic
   */
  private String dependentCharacteristicFlag;
  /**
   * true, if Black list parameter
   */
  private String blackListFlag;
  /**
   * true, if Parameter is never reviewed
   */
  private String neverReviewedFlag;
  /**
   * Qssd Result
   */
  private String qssdResult;
  /**
   * compli Tooltip
   */
  private String compliTooltip;
  /**
   * Qssd Tooltip
   */
  private String qssdTooltip;

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return this.id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long id) {
    this.id = id;
  }

  /**
   * @return daWpRespId
   */
  public Long getDaWpRespId() {
    return this.daWpRespId;
  }

  /**
   * @param daWpRespId set daWpRespId
   */
  public void setDaWpRespId(final Long daWpRespId) {
    this.daWpRespId = daWpRespId;
  }

  /**
   * @return parameterId
   */
  public BigDecimal getParameterId() {
    return this.parameterId;
  }

  /**
   * @param parameterId set parameterId
   */
  public void setParameterId(final BigDecimal parameterId) {
    this.parameterId = parameterId;
  }

  /**
   * @return parameterName
   */
  public String getParameterName() {
    return this.parameterName;
  }

  /**
   * @param parameterName set parameterName
   */
  public void setParameterName(final String parameterName) {
    this.parameterName = parameterName;
  }

  /**
   * @return parameterType
   */
  public String getParameterType() {
    return this.parameterType;
  }

  /**
   * @param parameterType set parameterType
   */
  public void setParameterType(final String parameterType) {
    this.parameterType = parameterType;
  }

  /**
   * @return functionName
   */
  public String getFunctionName() {
    return this.functionName;
  }

  /**
   * @param functionName set functionName
   */
  public void setFunctionName(final String functionName) {
    this.functionName = functionName;
  }

  /**
   * @return functionVersion
   */
  public String getFunctionVersion() {
    return this.functionVersion;
  }

  /**
   * @param functionVersion set functionVersion
   */
  public void setFunctionVersion(final String functionVersion) {
    this.functionVersion = functionVersion;
  }

  /**
   * @return rvwA2lVersion
   */
  public String getRvwA2lVersion() {
    return this.rvwA2lVersion;
  }

  /**
   * @param rvwA2lVersion set rvwA2lVersion
   */
  public void setRvwA2lVersion(final String rvwA2lVersion) {
    this.rvwA2lVersion = rvwA2lVersion;
  }

  /**
   * @return rvwFuncVersion
   */
  public String getRvwFuncVersion() {
    return this.rvwFuncVersion;
  }

  /**
   * @param rvwFuncVersion set rvwFuncVersion
   */
  public void setRvwFuncVersion(final String rvwFuncVersion) {
    this.rvwFuncVersion = rvwFuncVersion;
  }

  /**
   * @return questionnaireStatus
   */
  public String getQuestionnaireStatus() {
    return this.questionnaireStatus;
  }

  /**
   * @param questionnaireStatus set questionnaireStatus
   */
  public void setQuestionnaireStatus(final String questionnaireStatus) {
    this.questionnaireStatus = questionnaireStatus;
  }

  /**
   * @return reviewedFlag
   */
  public String getReviewedFlag() {
    return this.reviewedFlag;
  }

  /**
   * @param reviewedFlag set reviewedFlag
   */
  public void setReviewedFlag(final String reviewedFlag) {
    this.reviewedFlag = reviewedFlag;
  }

  /**
   * @return equalsFlag
   */
  public String getEqualsFlag() {
    return this.equalsFlag;
  }

  /**
   * @param equalsFlag set equalsFlag
   */
  public void setEqualsFlag(final String equalsFlag) {
    this.equalsFlag = equalsFlag;
  }

  /**
   * @return compliResult
   */
  public String getCompliResult() {
    return this.compliResult;
  }

  /**
   * @param compliResult set compliResult
   */
  public void setCompliResult(final String compliResult) {
    this.compliResult = compliResult;
  }

  /**
   * @return reviewScore
   */
  public String getReviewScore() {
    return this.reviewScore;
  }

  /**
   * @param reviewScore set reviewScore
   */
  public void setReviewScore(final String reviewScore) {
    this.reviewScore = reviewScore;
  }

  /**
   * @return reviewRemark
   */
  public String getReviewRemark() {
    return this.reviewRemark;
  }

  /**
   * @param reviewRemark set reviewRemark
   */
  public void setReviewRemark(final String reviewRemark) {
    this.reviewRemark = reviewRemark;
  }

  /**
   * @return resultId
   */
  public BigDecimal getResultId() {
    return this.resultId;
  }

  /**
   * @param resultId set resultId
   */
  public void setResultId(final BigDecimal resultId) {
    this.resultId = resultId;
  }

  /**
   * @return rvwParamId
   */
  public BigDecimal getRvwParamId() {
    return this.rvwParamId;
  }

  /**
   * @param rvwParamId set rvwParamId
   */
  public void setRvwParamId(final BigDecimal rvwParamId) {
    this.rvwParamId = rvwParamId;
  }

  /**
   * @return rvwResultName
   */
  public String getRvwResultName() {
    return this.rvwResultName;
  }

  /**
   * @param rvwResultName set rvwResultName
   */
  public void setRvwResultName(final String rvwResultName) {
    this.rvwResultName = rvwResultName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return this.version;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    this.version = version;
  }

  /**
   * @return hexValue
   */
  public byte[] getHexValue() {
    return this.hexValue;
  }

  /**
   * @param hexValue set hexValue
   */
  public void setHexValue(final byte[] hexValue) {
    this.hexValue = hexValue;
  }

  /**
   * @return reviewedValue
   */
  public byte[] getReviewedValue() {
    return this.reviewedValue;
  }

  /**
   * @param reviewedValue set reviewedValue
   */
  public void setReviewedValue(final byte[] reviewedValue) {
    this.reviewedValue = reviewedValue;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DaParameter clone() {
    try {
      return (DaParameter) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      // TODO
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final DaParameter object) {
    return ModelUtil.compare(getId(), object.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return ModelUtil.isEqual(getId(), ((DaParameter) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

  /**
   * @return createdDate
   */
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * @param createdDate set createdDate
   */
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * @return createdUser
   */
  public String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * @param createdUser set createdUser
   */
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  /**
   * @return modifiedDate
   */
  public String getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * @param modifiedDate set modifiedDate
   */
  public void setModifiedDate(final String modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  /**
   * @return modifiedUser
   */
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  /**
   * @param modifiedUser set modifiedUser
   */
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  /**
   * @return the compliFlag
   */
  public String getCompliFlag() {
    return this.compliFlag;
  }

  /**
   * @param compliFlag the compliFlag to set
   */
  public void setCompliFlag(final String compliFlag) {
    this.compliFlag = compliFlag;
  }

  /**
   * @return the qssdFlag
   */
  public String getQssdFlag() {
    return this.qssdFlag;
  }

  /**
   * @param qssdFlag the qssdFlag to set
   */
  public void setQssdFlag(final String qssdFlag) {
    this.qssdFlag = qssdFlag;
  }

  /**
   * @return the readOnlyFlag
   */
  public String getReadOnlyFlag() {
    return this.readOnlyFlag;
  }

  /**
   * @param readOnlyFlag the readOnlyFlag to set
   */
  public void setReadOnlyFlag(final String readOnlyFlag) {
    this.readOnlyFlag = readOnlyFlag;
  }

  /**
   * @return the dependentCharacteristicFlag
   */
  public String getDependentCharacteristicFlag() {
    return this.dependentCharacteristicFlag;
  }

  /**
   * @param dependentCharacteristicFlag the dependentCharacteristicFlag to set
   */
  public void setDependentCharacteristicFlag(final String dependentCharacteristicFlag) {
    this.dependentCharacteristicFlag = dependentCharacteristicFlag;
  }

  /**
   * @return the blackListFlag
   */
  public String getBlackListFlag() {
    return this.blackListFlag;
  }

  /**
   * @param blackListFlag the blackListFlag to set
   */
  public void setBlackListFlag(final String blackListFlag) {
    this.blackListFlag = blackListFlag;
  }

  /**
   * @return the neverReviewedFlag
   */
  public String getNeverReviewedFlag() {
    return this.neverReviewedFlag;
  }

  /**
   * @param neverReviewedFlag the neverReviewedFlag to set
   */
  public void setNeverReviewedFlag(final String neverReviewedFlag) {
    this.neverReviewedFlag = neverReviewedFlag;
  }

  /**
   * @return the qssdResult
   */
  public String getQssdResult() {
    return this.qssdResult;
  }

  /**
   * @param qssdResult the qssdResult to set
   */
  public void setQssdResult(final String qssdResult) {
    this.qssdResult = qssdResult;
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

}
