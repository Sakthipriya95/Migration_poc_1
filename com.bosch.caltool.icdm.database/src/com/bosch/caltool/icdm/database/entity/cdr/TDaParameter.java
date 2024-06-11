package com.bosch.caltool.icdm.database.entity.cdr;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the T_DA_PARAMETERS database table.
 */
@Entity
@Table(name = "T_DA_PARAMETERS")
@NamedQuery(name = "TDaParameter.findAll", query = "SELECT t FROM TDaParameter t")
public class TDaParameter implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "DA_PARAM_ID")
  private long daParamId;

  @Column(name = "COMPLI_RESULT")
  private String compliResult;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "EQUALS_FLAG")
  private String equalsFlag;

  @Column(name = "FUNCTION_NAME")
  private String functionName;

  @Column(name = "FUNCTION_VERSION")
  private String functionVersion;

  @Lob
  @Column(name = "HEX_VALUE")
  private byte[] hexValue;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "PARAMETER_ID")
  private BigDecimal parameterId;

  @Column(name = "PARAMETER_NAME")
  private String parameterName;

  @Column(name = "PARAMETER_TYPE")
  private String parameterType;

  @Column(name = "QUESTIONNAIRE_STATUS")
  private String questionnaireStatus;

  @Column(name = "RESULT_ID")
  private BigDecimal resultId;

  @Column(name = "REVIEW_REMARK")
  private String reviewRemark;

  @Column(name = "REVIEW_SCORE")
  private String reviewScore;

  @Column(name = "REVIEWED_FLAG")
  private String reviewedFlag;

  @Lob
  @Column(name = "REVIEWED_VALUE")
  private byte[] reviewedValue;

  @Column(name = "RVW_A2L_VERSION")
  private String rvwA2lVersion;

  @Column(name = "RVW_FUNC_VERSION")
  private String rvwFuncVersion;

  @Column(name = "RVW_PARAM_ID")
  private BigDecimal rvwParamId;

  @Column(name = "RVW_RESULT_NAME")
  private String rvwResultName;

  @Column(name = "\"VERSION\"")
  @Version
  private Long version;

  @Column(name = "COMPLI_FLAG")
  private String compliFlag;

  @Column(name = "QSSD_FLAG")
  private String qssdFlag;

  @Column(name = "READ_ONLY_FLAG")
  private String readOnlyFlag;

  @Column(name = "DEPENDENT_CHARACTERISTIC_FLAG")
  private String dependentCharacteristicFlag;

  @Column(name = "BLACK_LIST_FLAG")
  private String blackListFlag;

  @Column(name = "NEVER_REVIEWED_FLAG")
  private String neverReviewedFlag;

  @Column(name = "QSSD_RESULT")
  private String qssdResult;

  @Column(name = "COMPLI_TOOLTIP")
  private String compliTooltip;

  @Column(name = "QSSD_TOOLTIP")
  private String qssdTooltip;

  // bi-directional many-to-one association to TDaWpResp
  @ManyToOne
  @JoinColumn(name = "DA_WP_RESP_ID")
  private TDaWpResp TDaWpResp;

  public TDaParameter() {}

  public long getDaParamId() {
    return this.daParamId;
  }

  public void setDaParamId(final long daParamId) {
    this.daParamId = daParamId;
  }

  public String getCompliResult() {
    return this.compliResult;
  }

  public void setCompliResult(final String compliResult) {
    this.compliResult = compliResult;
  }

  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }

  public String getCreatedUser() {
    return this.createdUser;
  }

  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  public String getEqualsFlag() {
    return this.equalsFlag;
  }

  public void setEqualsFlag(final String equalsFlag) {
    this.equalsFlag = equalsFlag;
  }

  public String getFunctionName() {
    return this.functionName;
  }

  public void setFunctionName(final String functionName) {
    this.functionName = functionName;
  }

  public String getFunctionVersion() {
    return this.functionVersion;
  }

  public void setFunctionVersion(final String functionVersion) {
    this.functionVersion = functionVersion;
  }

  public byte[] getHexValue() {
    return this.hexValue;
  }

  public void setHexValue(final byte[] hexValue) {
    this.hexValue = hexValue;
  }

  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }

  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  public String getModifiedUser() {
    return this.modifiedUser;
  }

  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  public BigDecimal getParameterId() {
    return this.parameterId;
  }

  public void setParameterId(final BigDecimal parameterId) {
    this.parameterId = parameterId;
  }

  public String getParameterName() {
    return this.parameterName;
  }

  public void setParameterName(final String parameterName) {
    this.parameterName = parameterName;
  }

  public String getParameterType() {
    return this.parameterType;
  }

  public void setParameterType(final String parameterType) {
    this.parameterType = parameterType;
  }

  public String getQuestionnaireStatus() {
    return this.questionnaireStatus;
  }

  public void setQuestionnaireStatus(final String questionnaireStatus) {
    this.questionnaireStatus = questionnaireStatus;
  }

  public BigDecimal getResultId() {
    return this.resultId;
  }

  public void setResultId(final BigDecimal resultId) {
    this.resultId = resultId;
  }

  public String getReviewRemark() {
    return this.reviewRemark;
  }

  public void setReviewRemark(final String reviewRemark) {
    this.reviewRemark = reviewRemark;
  }

  public String getReviewScore() {
    return this.reviewScore;
  }

  public void setReviewScore(final String reviewScore) {
    this.reviewScore = reviewScore;
  }

  public String getReviewedFlag() {
    return this.reviewedFlag;
  }

  public void setReviewedFlag(final String reviewedFlag) {
    this.reviewedFlag = reviewedFlag;
  }

  public byte[] getReviewedValue() {
    return this.reviewedValue;
  }

  public void setReviewedValue(final byte[] reviewedValue) {
    this.reviewedValue = reviewedValue;
  }

  public String getRvwA2lVersion() {
    return this.rvwA2lVersion;
  }

  public void setRvwA2lVersion(final String rvwA2lVersion) {
    this.rvwA2lVersion = rvwA2lVersion;
  }

  public String getRvwFuncVersion() {
    return this.rvwFuncVersion;
  }

  public void setRvwFuncVersion(final String rvwFuncVersion) {
    this.rvwFuncVersion = rvwFuncVersion;
  }

  public BigDecimal getRvwParamId() {
    return this.rvwParamId;
  }

  public void setRvwParamId(final BigDecimal rvwParamId) {
    this.rvwParamId = rvwParamId;
  }

  public String getRvwResultName() {
    return this.rvwResultName;
  }

  public void setRvwResultName(final String rvwResultName) {
    this.rvwResultName = rvwResultName;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public TDaWpResp getTDaWpResp() {
    return this.TDaWpResp;
  }

  public void setTDaWpResp(final TDaWpResp tDaWpResp) {
    this.TDaWpResp = tDaWpResp;
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
