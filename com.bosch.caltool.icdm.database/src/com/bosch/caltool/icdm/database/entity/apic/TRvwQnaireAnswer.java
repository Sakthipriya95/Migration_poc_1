package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.annotations.OptimisticLocking;

/**
 * The persistent class for the T_RVW_QNAIRE_ANSWER database table.
 */
@Entity
@OptimisticLocking(cascade = true)
@NamedQueries(value = { @NamedQuery(name = TRvwQnaireAnswer.GET_ALL, query = "SELECT t FROM TRvwQnaireAnswer t") })
@Table(name = "T_RVW_QNAIRE_ANSWER")
public class TRvwQnaireAnswer implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  /**
   * Named query - Get all
   */
  public static final String GET_ALL = "TRvwQnaireAnswer.findAll";


  /** The rvw answer id. */
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "RVW_ANSWER_ID", unique = true, nullable = false, precision = 15)
  private long rvwAnswerId;

  /** The created date. */
  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  /** The created user. */
  @Column(name = "CREATED_USER", nullable = false, length = 100)
  private String createdUser;

  /** The measurement. */
  @Column(name = "MEASUREMENT", length = 1)
  private String measurement;

  /** The modified date. */
  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  /** The modified user. */
  @Column(name = "MODIFIED_USER", length = 100)
  private String modifiedUser;


  /** The remark. */
  @Column(name = "REMARK", length = 4000)
  private String remark;

  /** The result. */
  @Column(name = "\"RESULT\"", length = 1)
  private String result;

  /** The series. */
  @Column(name = "SERIES", length = 1)
  private String series;

  /** The version. */
  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;


  /** The T question. */
  // bi-directional many-to-one association to TQuestion
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "Q_ID", nullable = false)
  @BatchFetch(value = BatchFetchType.JOIN)
  private TQuestion tQuestion;

  /** The T qnaire ans open points. */
  // bi-directional many-to-one association to TQnaireAnsOpenPoint
  @OneToMany(mappedBy = "TRvwQnaireAnswer", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private Set<TRvwQnaireAnswerOpl> tQnaireAnsOpenPoints;


//bi-directional many-to-one association to TQuestionResultOption
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "Q_RESULT_OPT_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TQuestionResultOption tQuestionResultOption;

//bi-directional many-to-one association to TRvwQnaireRespVersion
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "QNAIRE_RESP_VERS_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TRvwQnaireRespVersion tRvwQnaireRespVersion;

  /**
   * Gets the rvw answer id.
   *
   * @return the rvw answer id
   */
  public long getRvwAnswerId() {
    return this.rvwAnswerId;
  }

  /**
   * Sets the rvw answer id.
   *
   * @param rvwAnswerId the new rvw answer id
   */
  public void setRvwAnswerId(final long rvwAnswerId) {
    this.rvwAnswerId = rvwAnswerId;
  }

  /**
   * Gets the created date.
   *
   * @return the created date
   */
  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  /**
   * Sets the created date.
   *
   * @param createdDate the new created date
   */
  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * Gets the created user.
   *
   * @return the created user
   */
  public String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * Sets the created user.
   *
   * @param createdUser the new created user
   */
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  /**
   * Gets the measurement.
   *
   * @return the measurement
   */
  public String getMeasurement() {
    return this.measurement;
  }

  /**
   * Sets the measurement.
   *
   * @param measurement the new measurement
   */
  public void setMeasurement(final String measurement) {
    this.measurement = measurement;
  }

  /**
   * Gets the modified date.
   *
   * @return the modified date
   */
  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * Sets the modified date.
   *
   * @param modifiedDate the new modified date
   */
  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  /**
   * Gets the modified user.
   *
   * @return the modified user
   */
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  /**
   * Sets the modified user.
   *
   * @param modifiedUser the new modified user
   */
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }


  /**
   * Gets the remark.
   *
   * @return the remark
   */
  public String getRemark() {
    return this.remark;
  }

  /**
   * Sets the remark.
   *
   * @param remark the new remark
   */
  public void setRemark(final String remark) {
    this.remark = remark;
  }

  /**
   * Gets the result.
   *
   * @return the result
   */
  public String getResult() {
    return this.result;
  }

  /**
   * Sets the result.
   *
   * @param result the new result
   */
  public void setResult(final String result) {
    this.result = result;
  }

  /**
   * Gets the series.
   *
   * @return the series
   */
  public String getSeries() {
    return this.series;
  }

  /**
   * Sets the series.
   *
   * @param series the new series
   */
  public void setSeries(final String series) {
    this.series = series;
  }

  /**
   * Gets the version.
   *
   * @return the version
   */
  public Long getVersion() {
    return this.version;
  }

  /**
   * Sets the version.
   *
   * @param version the new version
   */
  public void setVersion(final Long version) {
    this.version = version;
  }

  /**
   * Gets the t question.
   *
   * @return the t question
   */
  public TQuestion getTQuestion() {
    return this.tQuestion;
  }

  /**
   * Sets the t question.
   *
   * @param tQuestion the new t question
   */
  public void setTQuestion(final TQuestion tQuestion) {
    this.tQuestion = tQuestion;
  }

  /**
   * Gets the t qnaire ans open points.
   *
   * @return the t qnaire ans open points
   */
  public Set<TRvwQnaireAnswerOpl> getTQnaireAnsOpenPoints() {
    return this.tQnaireAnsOpenPoints;
  }

  /**
   * Sets the t qnaire ans open points.
   *
   * @param tQnaireAnsOpenPoints the new t qnaire ans open points
   */
  public void setTQnaireAnsOpenPoints(final Set<TRvwQnaireAnswerOpl> tQnaireAnsOpenPoints) {
    this.tQnaireAnsOpenPoints = tQnaireAnsOpenPoints;
  }

  /**
   * @return the tQuestionResult
   */
  public TQuestionResultOption getTQuestionResultOption() {
    return this.tQuestionResultOption;
  }


  /**
   * @param tQuestionResult the tQuestionResult to set
   */
  public void setTQuestionResultOption(final TQuestionResultOption tQuestionResult) {
    this.tQuestionResultOption = tQuestionResult;
  }

  public TRvwQnaireRespVersion getTRvwQnaireRespVersion() {
    return this.tRvwQnaireRespVersion;
  }

  public void setTRvwQnaireRespVersion(final TRvwQnaireRespVersion TRvwQnaireRespVersion) {
    this.tRvwQnaireRespVersion = TRvwQnaireRespVersion;
  }
}