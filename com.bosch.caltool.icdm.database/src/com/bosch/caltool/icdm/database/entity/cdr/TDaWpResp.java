package com.bosch.caltool.icdm.database.entity.cdr;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the T_DA_WP_RESP database table.
 */
@Entity
@Table(name = "T_DA_WP_RESP")
@NamedQuery(name = "TDaWpResp.findAll", query = "SELECT t FROM TDaWpResp t")
public class TDaWpResp implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "DA_WP_RESP_ID")
  private long daWpRespId;

  @Column(name = "A2L_RESP_ALIAS_NAME")
  private String a2lRespAliasName;

  @Column(name = "A2L_RESP_ID")
  private BigDecimal a2lRespId;

  @Column(name = "A2L_RESP_NAME")
  private String a2lRespName;

  @Column(name = "A2L_RESP_TYPE")
  private String a2lRespType;

  @Column(name = "A2L_WP_ID")
  private BigDecimal a2lWpId;

  @Column(name = "A2L_WP_NAME")
  private String a2lWpName;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "HEX_RVW_EQUAL_FLAG")
  private String hexRvwEqualFlag;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "PARAMETER_REVIEWED_FLAG")
  private String parameterReviewedFlag;

  @Column(name = "QNAIRES_ANSWERED_FLAG")
  private String qnairesAnsweredFlag;

  @Column(name = "\"VERSION\"")
  @Version
  private Long version;

  @Column(name = "WP_FINISHED_FLAG")
  private String wpFinishedFlag;

  @Column(name = "WP_READY_FOR_PRODUCTION_FLAG")
  private String wpReadyForProductionFlag;

  // bi-directional many-to-one association to TDaParameter
  @OneToMany(mappedBy = "TDaWpResp")
  private List<TDaParameter> TDaParameters;

  // bi-directional many-to-one association to TDaQnaireResp
  @OneToMany(mappedBy = "TDaWpResp")
  private List<TDaQnaireResp> TDaQnaireResps;

  // bi-directional many-to-one association to TDaDataAssessment
  @ManyToOne
  @JoinColumn(name = "DATA_ASSESSMENT_ID")
  private TDaDataAssessment TDaDataAssessment;

  public TDaWpResp() {}

  public long getDaWpRespId() {
    return this.daWpRespId;
  }

  public void setDaWpRespId(final long daWpRespId) {
    this.daWpRespId = daWpRespId;
  }

  public String getA2lRespAliasName() {
    return this.a2lRespAliasName;
  }

  public void setA2lRespAliasName(final String a2lRespAliasName) {
    this.a2lRespAliasName = a2lRespAliasName;
  }

  public BigDecimal getA2lRespId() {
    return this.a2lRespId;
  }

  public void setA2lRespId(final BigDecimal a2lRespId) {
    this.a2lRespId = a2lRespId;
  }

  public String getA2lRespName() {
    return this.a2lRespName;
  }

  public void setA2lRespName(final String a2lRespName) {
    this.a2lRespName = a2lRespName;
  }

  public String getA2lRespType() {
    return this.a2lRespType;
  }

  public void setA2lRespType(final String a2lRespType) {
    this.a2lRespType = a2lRespType;
  }

  public BigDecimal getA2lWpId() {
    return this.a2lWpId;
  }

  public void setA2lWpId(final BigDecimal a2lWpId) {
    this.a2lWpId = a2lWpId;
  }

  public String getA2lWpName() {
    return this.a2lWpName;
  }

  public void setA2lWpName(final String a2lWpName) {
    this.a2lWpName = a2lWpName;
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

  public String getHexRvwEqualFlag() {
    return this.hexRvwEqualFlag;
  }

  public void setHexRvwEqualFlag(final String hexRvwEqualFlag) {
    this.hexRvwEqualFlag = hexRvwEqualFlag;
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

  public String getParameterReviewedFlag() {
    return this.parameterReviewedFlag;
  }

  public void setParameterReviewedFlag(final String parameterReviewedFlag) {
    this.parameterReviewedFlag = parameterReviewedFlag;
  }

  public String getQnairesAnsweredFlag() {
    return this.qnairesAnsweredFlag;
  }

  public void setQnairesAnsweredFlag(final String qnairesAnsweredFlag) {
    this.qnairesAnsweredFlag = qnairesAnsweredFlag;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public String getWpFinishedFlag() {
    return this.wpFinishedFlag;
  }

  public void setWpFinishedFlag(final String wpFinishedFlag) {
    this.wpFinishedFlag = wpFinishedFlag;
  }

  public String getWpReadyForProductionFlag() {
    return this.wpReadyForProductionFlag;
  }

  public void setWpReadyForProductionFlag(final String wpReadyForProductionFlag) {
    this.wpReadyForProductionFlag = wpReadyForProductionFlag;
  }

  public List<TDaParameter> getTDaParameters() {
    return this.TDaParameters;
  }

  public void setTDaParameters(final List<TDaParameter> TDaParameters) {
    this.TDaParameters = TDaParameters;
  }

  public TDaParameter addTDaParameter(final TDaParameter TDaParameter) {
    getTDaParameters().add(TDaParameter);
    TDaParameter.setTDaWpResp(this);

    return TDaParameter;
  }

  public TDaParameter removeTDaParameter(final TDaParameter TDaParameter) {
    getTDaParameters().remove(TDaParameter);
    TDaParameter.setTDaWpResp(null);

    return TDaParameter;
  }

  public List<TDaQnaireResp> getTDaQnaireResps() {
    return this.TDaQnaireResps;
  }

  public void setTDaQnaireResps(final List<TDaQnaireResp> TDaQnaireResps) {
    this.TDaQnaireResps = TDaQnaireResps;
  }

  public TDaQnaireResp addTDaQnaireResp(final TDaQnaireResp TDaQnaireResp) {
    getTDaQnaireResps().add(TDaQnaireResp);
    TDaQnaireResp.setTDaWpResp(this);

    return TDaQnaireResp;
  }

  public TDaQnaireResp removeTDaQnaireResp(final TDaQnaireResp TDaQnaireResp) {
    getTDaQnaireResps().remove(TDaQnaireResp);
    TDaQnaireResp.setTDaWpResp(null);

    return TDaQnaireResp;
  }

  public TDaDataAssessment getTDaDataAssessment() {
    return this.TDaDataAssessment;
  }

  public void setTDaDataAssessment(final TDaDataAssessment TDaDataAssessment) {
    this.TDaDataAssessment = TDaDataAssessment;
  }

}
