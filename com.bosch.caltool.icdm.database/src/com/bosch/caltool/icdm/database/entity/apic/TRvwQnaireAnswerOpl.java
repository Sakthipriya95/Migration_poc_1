package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;


/**
 * The persistent class for the T_RVW_QNAIRE_ANSWER_OPL database table.
 */
@Entity
@Table(name = "T_RVW_QNAIRE_ANSWER_OPL")
@NamedQuery(name = TRvwQnaireAnswerOpl.GET_ALL, query = "SELECT t FROM TRvwQnaireAnswerOpl t")
public class TRvwQnaireAnswerOpl implements Serializable {

  private static final long serialVersionUID = 1L;
  /**
   * Named query - Get all
   */
  public static final String GET_ALL = "TRvwQnaireAnswerOpl.findAll";
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "OPEN_POINTS_ID", unique = true, nullable = false)
  private long openPointsId;

  @Column(name = "COMPLETION_DATE")
  private Timestamp completionDate;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", length = 100)
  private String createdUser;

  @Column(length = 4000)
  private String measure;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 100)
  private String modifiedUser;

  @Column(name = "OPEN_POINTS", nullable = false, length = 4000)
  private String openPoints;

  @Column(name = "\"RESULT\"", length = 1)
  private String result;

  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;

  // bi-directional many-to-one association to TabvApicUser
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "RESPONSIBLE")
  private TabvApicUser tabvApicUser;

  // bi-directional many-to-one association to TRvwQnaireAnswer
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "RVW_ANSWER_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TRvwQnaireAnswer TRvwQnaireAnswer;

  public TRvwQnaireAnswerOpl() {}

  public long getOpenPointsId() {
    return this.openPointsId;
  }

  public void setOpenPointsId(final long openPointsId) {
    this.openPointsId = openPointsId;
  }

  public Timestamp getCompletionDate() {
    return this.completionDate;
  }

  public void setCompletionDate(final Timestamp completionDate) {
    this.completionDate = completionDate;
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

  public String getMeasure() {
    return this.measure;
  }

  public void setMeasure(final String measure) {
    this.measure = measure;
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

  public String getOpenPoints() {
    return this.openPoints;
  }

  public void setOpenPoints(final String openPoints) {
    this.openPoints = openPoints;
  }

  public String getResult() {
    return this.result;
  }

  public void setResult(final String result) {
    this.result = result;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public TabvApicUser getTabvApicUser() {
    return this.tabvApicUser;
  }

  public void setTabvApicUser(final TabvApicUser tabvApicUser) {
    this.tabvApicUser = tabvApicUser;
  }

  public TRvwQnaireAnswer getTRvwQnaireAnswer() {
    return this.TRvwQnaireAnswer;
  }

  public void setTRvwQnaireAnswer(final TRvwQnaireAnswer TRvwQnaireAnswer) {
    this.TRvwQnaireAnswer = TRvwQnaireAnswer;
  }

}