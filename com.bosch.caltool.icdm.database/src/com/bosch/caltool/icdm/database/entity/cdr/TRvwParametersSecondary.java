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

import org.eclipse.persistence.annotations.OptimisticLocking;


/**
 * The persistent class for the T_RVW_PARAMETERS_SECONDARY database table.
 */
@Entity
@OptimisticLocking(cascade = true)
@Table(name = "T_RVW_PARAMETERS_SECONDARY")
@NamedQuery(name = "TRvwParametersSecondary.findAll", query = "SELECT t FROM TRvwParametersSecondary t")
public class TRvwParametersSecondary implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CDR_SEQ_GENERATOR")
  @Column(name = "SEC_RVW_PARAM_ID", unique = true, nullable = false)
  private long secRvwParamId;

  @Column(name = "BITWISE_LIMIT")
  private String bitwiseLimit;

  @Column(name = "CHANGE_FLAG")
  private int changeFlag;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(length = 1)
  private String isbitwise;

  @Column(name = "LAB_OBJ_ID")
  private Long labObjId;

  @Column(name = "LOWER_LIMIT")
  private BigDecimal lowerLimit;

  @Column(name = "MATCH_REF_FLAG")
  private String matchRefFlag;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "REF_UNIT")
  private String refUnit;

  @Lob
  @Column(name = "REF_VALUE")
  private byte[] refValue;

  @Column(name = "\"RESULT\"")
  private String result;

  @Column(name = "REV_ID")
  private Long revId;

  @Column(name = "RVW_METHOD")
  private String rvwMethod;

  @Column(name = "UPPER_LIMIT")
  private BigDecimal upperLimit;

  @Version
  @Column(name = "\"VERSION\"")
  private Long version;

  // bi-directional many-to-one association to TRvwParameter
  @ManyToOne
  @JoinColumn(name = "RVW_PARAM_ID")
  private TRvwParameter TRvwParameter;

  // bi-directional many-to-one association to TRvwResultsSecondary
  @ManyToOne
  @JoinColumn(name = "SEC_REVIEW_ID")
  private TRvwResultsSecondary TRvwResultsSecondary;

  public TRvwParametersSecondary() {}

  public long getSecRvwParamId() {
    return this.secRvwParamId;
  }

  public void setSecRvwParamId(final long secRvwParamId) {
    this.secRvwParamId = secRvwParamId;
  }

  public String getBitwiseLimit() {
    return this.bitwiseLimit;
  }

  public void setBitwiseLimit(final String bitwiseLimit) {
    this.bitwiseLimit = bitwiseLimit;
  }

  public int getChangeFlag() {
    return this.changeFlag;
  }

  public void setChangeFlag(final int changeFlag) {
    this.changeFlag = changeFlag;
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

  public String getIsbitwise() {
    return this.isbitwise;
  }

  public void setIsbitwise(final String isbitwise) {
    this.isbitwise = isbitwise;
  }

  public Long getLabObjId() {
    return this.labObjId;
  }

  public void setLabObjId(final Long labObjId) {
    this.labObjId = labObjId;
  }

  public BigDecimal getLowerLimit() {
    return this.lowerLimit;
  }

  public void setLowerLimit(final BigDecimal lowerLimit) {
    this.lowerLimit = lowerLimit;
  }

  public String getMatchRefFlag() {
    return this.matchRefFlag;
  }

  public void setMatchRefFlag(final String matchRefFlag) {
    this.matchRefFlag = matchRefFlag;
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

  public String getRefUnit() {
    return this.refUnit;
  }

  public void setRefUnit(final String refUnit) {
    this.refUnit = refUnit;
  }

  public byte[] getRefValue() {
    return this.refValue;
  }

  public void setRefValue(final byte[] refValue) {
    this.refValue = refValue;
  }

  public String getResult() {
    return this.result;
  }

  public void setResult(final String result) {
    this.result = result;
  }

  public Long getRevId() {
    return this.revId;
  }

  public void setRevId(final Long revId) {
    this.revId = revId;
  }

  public String getRvwMethod() {
    return this.rvwMethod;
  }

  public void setRvwMethod(final String rvwMethod) {
    this.rvwMethod = rvwMethod;
  }

  public BigDecimal getUpperLimit() {
    return this.upperLimit;
  }

  public void setUpperLimit(final BigDecimal upperLimit) {
    this.upperLimit = upperLimit;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public TRvwParameter getTRvwParameter() {
    return this.TRvwParameter;
  }

  public void setTRvwParameter(final TRvwParameter TRvwParameter) {
    this.TRvwParameter = TRvwParameter;
  }

  public TRvwResultsSecondary getTRvwResultsSecondary() {
    return this.TRvwResultsSecondary;
  }

  public void setTRvwResultsSecondary(final TRvwResultsSecondary TRvwResultsSecondary) {
    this.TRvwResultsSecondary = TRvwResultsSecondary;
  }

}