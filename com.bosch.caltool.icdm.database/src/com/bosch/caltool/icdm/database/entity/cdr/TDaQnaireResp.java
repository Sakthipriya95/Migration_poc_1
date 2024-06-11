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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the T_DA_QNAIRE_RESP database table.
 */
@Entity
@Table(name = "T_DA_QNAIRE_RESP")
@NamedQuery(name = "TDaQnaireResp.findAll", query = "SELECT t FROM TDaQnaireResp t")
public class TDaQnaireResp implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "DA_QNAIRE_RESP_ID")
  private long daQnaireRespId;

  @Column(name = "BASELINE_EXISTING_FLAG")
  private String baselineExistingFlag;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "NUM_NEGATIVE_ANSWERS")
  private BigDecimal numNegativeAnswers;

  @Column(name = "NUM_NEUTRAL_ANSWERS")
  private BigDecimal numNeutralAnswers;

  @Column(name = "NUM_POSITIVE_ANSWERS")
  private BigDecimal numPositiveAnswers;

  @Column(name = "QNAIRE_RESP_ID")
  private BigDecimal qnaireRespId;

  @Column(name = "QNAIRE_RESP_NAME")
  private String qnaireRespName;

  @Column(name = "QNAIRE_RESP_VERS_ID")
  private BigDecimal qnaireRespVersId;

  @Column(name = "QNAIRE_RESP_VERSION_NAME")
  private String qnaireRespVersionName;

  @Column(name = "READY_FOR_PRODUCTION_FLAG")
  private String readyForProductionFlag;

  @Column(name = "REVIEWED_DATE")
  private Timestamp reviewedDate;

  @Column(name = "REVIEWED_USER")
  private String reviewedUser;

  @Column(name = "\"VERSION\"")
  @Version
  private Long version;

  // bi-directional many-to-one association to TDaWpResp
  @ManyToOne
  @JoinColumn(name = "DA_WP_RESP_ID")
  private TDaWpResp TDaWpResp;

  public TDaQnaireResp() {}

  public long getDaQnaireRespId() {
    return this.daQnaireRespId;
  }

  public void setDaQnaireRespId(final long daQnaireRespId) {
    this.daQnaireRespId = daQnaireRespId;
  }

  public String getBaselineExistingFlag() {
    return this.baselineExistingFlag;
  }

  public void setBaselineExistingFlag(final String baselineExistingFlag) {
    this.baselineExistingFlag = baselineExistingFlag;
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

  public BigDecimal getNumNegativeAnswers() {
    return this.numNegativeAnswers;
  }

  public void setNumNegativeAnswers(final BigDecimal numNegativeAnswers) {
    this.numNegativeAnswers = numNegativeAnswers;
  }

  public BigDecimal getNumNeutralAnswers() {
    return this.numNeutralAnswers;
  }

  public void setNumNeutralAnswers(final BigDecimal numNeutralAnswers) {
    this.numNeutralAnswers = numNeutralAnswers;
  }

  public BigDecimal getNumPositiveAnswers() {
    return this.numPositiveAnswers;
  }

  public void setNumPositiveAnswers(final BigDecimal numPositiveAnswers) {
    this.numPositiveAnswers = numPositiveAnswers;
  }

  public BigDecimal getQnaireRespId() {
    return this.qnaireRespId;
  }

  public void setQnaireRespId(final BigDecimal qnaireRespId) {
    this.qnaireRespId = qnaireRespId;
  }

  public String getQnaireRespName() {
    return this.qnaireRespName;
  }

  public void setQnaireRespName(final String qnaireRespName) {
    this.qnaireRespName = qnaireRespName;
  }

  public BigDecimal getQnaireRespVersId() {
    return this.qnaireRespVersId;
  }

  public void setQnaireRespVersId(final BigDecimal qnaireRespVersId) {
    this.qnaireRespVersId = qnaireRespVersId;
  }

  public String getQnaireRespVersionName() {
    return this.qnaireRespVersionName;
  }

  public void setQnaireRespVersionName(final String qnaireRespVersionName) {
    this.qnaireRespVersionName = qnaireRespVersionName;
  }

  public String getReadyForProductionFlag() {
    return this.readyForProductionFlag;
  }

  public void setReadyForProductionFlag(final String readyForProductionFlag) {
    this.readyForProductionFlag = readyForProductionFlag;
  }

  public Timestamp getReviewedDate() {
    return this.reviewedDate;
  }

  public void setReviewedDate(final Timestamp reviewedDate) {
    this.reviewedDate = reviewedDate;
  }

  public String getReviewedUser() {
    return this.reviewedUser;
  }

  public void setReviewedUser(final String reviewedUser) {
    this.reviewedUser = reviewedUser;
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

  public void setTDaWpResp(final TDaWpResp TDaWpResp) {
    this.TDaWpResp = TDaWpResp;
  }

}
