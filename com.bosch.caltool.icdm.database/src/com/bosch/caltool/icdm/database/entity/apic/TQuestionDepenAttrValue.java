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
 * The persistent class for the T_QUESTION_DEPEN_ATTR_VALUES database table.
 */
@Entity
@OptimisticLocking(cascade = true)
@Table(name = "T_QUESTION_DEPEN_ATTR_VALUES")
public class TQuestionDepenAttrValue implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "DEPENATTRVALID_GENERATOR", sequenceName = "SEQV_ATTRIBUTES")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DEPENATTRVALID_GENERATOR")
  @Column(name = "DEPEN_ATTR_VAL_ID")
  private long depenAttrValId;

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

  // bi-directional many-to-one association to TQuestionDepenAttribute
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "Q_ATTR_DEP_ID", nullable = false)
  private TQuestionDepenAttribute tQuestionDepenAttribute;

  // bi-directional many-to-one association to TabvAttrValue
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "VALUE_ID", nullable = false)
  private TabvAttrValue tabvAttrValue;

  @Column(name = "Q_COMBI_NUM")
  private Long qCombiNum;

  public TQuestionDepenAttrValue() {}

  public long getDepenAttrValId() {
    return this.depenAttrValId;
  }

  public void setDepenAttrValId(final long depenAttrValId) {
    this.depenAttrValId = depenAttrValId;
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

  public TQuestionDepenAttribute getTQuestionDepenAttribute() {
    return this.tQuestionDepenAttribute;
  }

  public void setTQuestionDepenAttribute(final TQuestionDepenAttribute tQuestionDepenAttribute) {
    this.tQuestionDepenAttribute = tQuestionDepenAttribute;
  }

  public TabvAttrValue getTabvAttrValue() {
    return this.tabvAttrValue;
  }

  public void setTabvAttrValue(final TabvAttrValue tabvAttrValue) {
    this.tabvAttrValue = tabvAttrValue;
  }

  public Long getQCombiNum() {
    return this.qCombiNum;
  }

  public void setQCombiNum(final Long qCombiNum) {
    this.qCombiNum = qCombiNum;
  }
}