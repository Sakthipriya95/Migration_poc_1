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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.OptimisticLocking;


/**
 * The persistent class for the T_QUESTION_CONFIG database table.
 */
@Entity
@OptimisticLocking(cascade = true)
@Table(name = "T_QUESTION_CONFIG")
public class TQuestionConfig implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "QCONFIGID_GENERATOR", sequenceName = "SEQV_ATTRIBUTES")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "QCONFIGID_GENERATOR")
  @Column(name = "QCONFIG_ID")
  private long qconfigId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 100)
  private String createdUser;

  @Column(name = "\"LINK\"", length = 1)
  private String link;

  @Column(name = "MEASUREMENT", length = 1)
  private String measurement;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 100)
  private String modifiedUser;

  @Column(name = "OPEN_POINTS", length = 1)
  private String openPoints;

  @Column(name = "REMARK", length = 1)
  private String remark;

  @Column(name = "\"RESULT\"", length = 1)
  private String result;

  @Column(name = "SERIES", length = 1)
  private String series;

  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;

  @Column(name = "COMPLETION_DATE", length = 1)
  private String completionDate;

  @Column(name = "MEASURE", length = 1)
  private String measure;

  @Column(name = "RESPONSIBLE", length = 1)
  private String responsible;

  // bi-directional one-to-one association to TQuestion
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "Q_ID", nullable = false)
  private TQuestion TQuestion;

  public TQuestionConfig() {}

  public long getQconfigId() {
    return this.qconfigId;
  }

  public void setQconfigId(final long qconfigId) {
    this.qconfigId = qconfigId;
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

  public String getLink() {
    return this.link;
  }

  public void setLink(final String link) {
    this.link = link;
  }

  public String getMeasurement() {
    return this.measurement;
  }

  public void setMeasurement(final String measurement) {
    this.measurement = measurement;
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

  public String getRemark() {
    return this.remark;
  }

  public void setRemark(final String remark) {
    this.remark = remark;
  }

  public String getResult() {
    return this.result;
  }

  public void setResult(final String result) {
    this.result = result;
  }

  public String getSeries() {
    return this.series;
  }

  public void setSeries(final String series) {
    this.series = series;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public TQuestion getTQuestion() {
    return this.TQuestion;
  }

  public void setTQuestion(final TQuestion TQuestion) {
    this.TQuestion = TQuestion;
  }

  public String getCompletionDate() {
    return this.completionDate;
  }

  public void setCompletionDate(final String completionDate) {
    this.completionDate = completionDate;
  }

  public String getMeasure() {
    return this.measure;
  }

  public void setMeasure(final String measure) {
    this.measure = measure;
  }

  public String getResponsible() {
    return this.responsible;
  }

  public void setResponsible(final String responsible) {
    this.responsible = responsible;
  }

}