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
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.annotations.OptimisticLocking;
import org.eclipse.persistence.annotations.PrivateOwned;


/**
 * The persistent class for the T_QUESTIONNAIRE database table.
 */
@Entity
@OptimisticLocking(cascade = true)
@NamedQuery(name = TQuestionnaire.GET_ALL, query = "SELECT t FROM TQuestionnaire t")
@Table(name = "T_QUESTIONNAIRE")
public class TQuestionnaire implements Serializable {

  /**
   * Named query - Get all
   */
  public static final String GET_ALL = "TQuestionnaire.findAll";

  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "QNAIREID_GENERATOR", sequenceName = "SEQV_ATTRIBUTES")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "QNAIREID_GENERATOR")
  @Column(name = "QNAIRE_ID")
  private long qnaireId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 100)
  private String createdUser;

  @Column(name = "DELETED_FLAG", length = 1)
  private String deletedFlag;

  @Column(name = "DESC_ENG", nullable = false, length = 4000)
  private String descEng;

  @Column(name = "DESC_GER", length = 4000)
  private String descGer;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 100)
  private String modifiedUser;

  @Column(name = "NAME_ENG", nullable = false, length = 100)
  private String nameEng;

  @Column(name = "NAME_GER", length = 100)
  private String nameGer;

  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;

  // bi-directional many-to-one association to TQuestionnaireVersion
  @OneToMany(mappedBy = "TQuestionnaire", fetch = FetchType.LAZY)
  @PrivateOwned
  private Set<TQuestionnaireVersion> TQuestionnaireVersions;

  // bi-directional many-to-one association to TWorkpackageDivision
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "WP_DIV_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TWorkpackageDivision tWorkpackageDivision;


  public TQuestionnaire() {}

  public long getQnaireId() {
    return this.qnaireId;
  }

  public void setQnaireId(final long qnaireId) {
    this.qnaireId = qnaireId;
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

  public String getDeletedFlag() {
    return this.deletedFlag;
  }

  public void setDeletedFlag(final String deletedFlag) {
    this.deletedFlag = deletedFlag;
  }

  public String getDescEng() {
    return this.descEng;
  }

  public void setDescEng(final String descEng) {
    this.descEng = descEng;
  }

  public String getDescGer() {
    return this.descGer;
  }

  public void setDescGer(final String descGer) {
    this.descGer = descGer;
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

  public String getNameEng() {
    return this.nameEng;
  }

  public void setNameEng(final String nameEng) {
    this.nameEng = nameEng;
  }

  public String getNameGer() {
    return this.nameGer;
  }

  public void setNameGer(final String nameGer) {
    this.nameGer = nameGer;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public Set<TQuestionnaireVersion> getTQuestionnaireVersions() {
    return this.TQuestionnaireVersions;
  }

  public void setTQuestionnaireVersions(final Set<TQuestionnaireVersion> TQuestionnaireVersions) {
    this.TQuestionnaireVersions = TQuestionnaireVersions;
  }


  public TWorkpackageDivision getTWorkpackageDivision() {
    return this.tWorkpackageDivision;
  }

  public void setTWorkpackageDivision(final TWorkpackageDivision tWorkpackageDivision) {
    this.tWorkpackageDivision = tWorkpackageDivision;
  }
}