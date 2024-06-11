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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.annotations.OptimisticLocking;


/**
 * The persistent class for the T_QUESTION_DEPEN_ATTRIBUTES database table.
 */
@Entity
@OptimisticLocking(cascade = true)
@Table(name = "T_QUESTION_DEPEN_ATTRIBUTES")
public class TQuestionDepenAttribute implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "QATTRDEPENID_GENERATOR", sequenceName = "SEQV_ATTRIBUTES")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "QATTRDEPENID_GENERATOR")
  @Column(name = "QATTR_DEPEN_ID")
  private long qattrDepenId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 100)
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 100)
  private String modifiedUser;

  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;

  // bi-directional many-to-one association to TQuestion
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "Q_ID", nullable = false)
  private TQuestion TQuestion;

  // bi-directional many-to-one association to TQuestionDepenAttrValue
  @OneToMany(mappedBy = "tQuestionDepenAttribute", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private Set<TQuestionDepenAttrValue> tQuestionDepenAttrValues;

  // bi-directional many-to-one association to TabvAttribute
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ATTR_ID", nullable = false)
  private TabvAttribute tabvAttribute;

  public TQuestionDepenAttribute() {}

  public long getQattrDepenId() {
    return this.qattrDepenId;
  }

  public void setQattrDepenId(final long qattrDepenId) {
    this.qattrDepenId = qattrDepenId;
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

  public Set<TQuestionDepenAttrValue> getTQuestionDepenAttrValues() {
    return this.tQuestionDepenAttrValues;
  }

  public void setTQuestionDepenAttrValues(final Set<TQuestionDepenAttrValue> tQuestionDepenAttrValues) {
    this.tQuestionDepenAttrValues = tQuestionDepenAttrValues;
  }

  public TQuestionDepenAttrValue addTQuestionDepenAttrValues(final TQuestionDepenAttrValue tQuestionDepenAttrValue) {
    getTQuestionDepenAttrValues().add(tQuestionDepenAttrValue);
    tQuestionDepenAttrValue.setTQuestionDepenAttribute(this);

    return tQuestionDepenAttrValue;
  }

  public TQuestionDepenAttrValue removeTQuestionDepenAttrValues(final TQuestionDepenAttrValue tQuestionDepenAttrValue) {
    getTQuestionDepenAttrValues().remove(tQuestionDepenAttrValue);
    return tQuestionDepenAttrValue;
  }

  public TabvAttribute getTabvAttribute() {
    return this.tabvAttribute;
  }

  public void setTabvAttribute(final TabvAttribute tabvAttribute) {
    this.tabvAttribute = tabvAttribute;
  }

}