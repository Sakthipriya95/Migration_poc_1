package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
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


/**
 * The persistent class for the T_RVW_QNAIRE_RESP_VERSIONS database table.
 */
@Entity
@Table(name = "T_RVW_QNAIRE_RESP_VERSIONS")
@NamedQueries(value = {
    @NamedQuery(name = "TRvwQnaireRespVersion.findAll", query = "SELECT t FROM TRvwQnaireRespVersion t") })

public class TRvwQnaireRespVersion implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "QNAIRE_RESP_VERS_ID", unique = true, nullable = false)
  private long qnaireRespVersId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  private String description;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;

  private String name;

  @Column(name = "REV_NUM", nullable = false)
  private long revNum;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private long version;

  @Column(name = "QNAIRE_VERS_STATUS", nullable = false, length = 1)
  private String qnaireVersStatus;

  @Column(name = "REVIEWED_DATE", nullable = false)
  private Timestamp reviewedDate;

  @Column(name = "REVIEWED_USER", nullable = false, length = 30)
  private String reviewedUser;

  // bi-directional many-to-one association to TRvwQnaireAnswer
  @OneToMany(mappedBy = "tRvwQnaireRespVersion", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private Set<TRvwQnaireAnswer> tRvwQnaireAnswers;

//bi-directional many-to-one association to TQuestionnaireVersion
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "QNAIRE_VERS_ID", nullable = false)
  @BatchFetch(value = BatchFetchType.JOIN)
  private TQuestionnaireVersion TQuestionnaireVersion;

  // bi-directional many-to-one association to TRvwQnaireResponse
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "QNAIRE_RESP_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TRvwQnaireResponse tRvwQnaireResponse;


  public TRvwQnaireRespVersion() {}

  public long getQnaireRespVersId() {
    return this.qnaireRespVersId;
  }

  public void setQnaireRespVersId(final long qnaireRespVersId) {
    this.qnaireRespVersId = qnaireRespVersId;
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

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
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

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public long getRevNum() {
    return this.revNum;
  }

  public void setRevNum(final long revNum) {
    this.revNum = revNum;
  }

  public long getVersion() {
    return this.version;
  }

  public void setVersion(final long version) {
    this.version = version;
  }

  public Set<TRvwQnaireAnswer> getTRvwQnaireAnswers() {
    return this.tRvwQnaireAnswers;
  }

  public void setTRvwQnaireAnswers(final Set<TRvwQnaireAnswer> TRvwQnaireAnswers) {
    this.tRvwQnaireAnswers = TRvwQnaireAnswers;
  }

  /**
   * @return the tRvwQnaireResponse
   */
  public TRvwQnaireResponse getTRvwQnaireResponse() {
    return this.tRvwQnaireResponse;
  }


  /**
   * @param tRvwQnaireResponse the tRvwQnaireResponse to set
   */
  public void setTRvwQnaireResponse(final TRvwQnaireResponse tRvwQnaireResponse) {
    this.tRvwQnaireResponse = tRvwQnaireResponse;
  }

  /**
   * @param TRvwQnaireAnswer as input
   * @return TRvwQnaireAnswer
   */
  public TRvwQnaireAnswer addTRvwQnaireAnswer(final TRvwQnaireAnswer TRvwQnaireAnswer) {
    if (getTRvwQnaireAnswers() == null) {
      setTRvwQnaireAnswers(new HashSet<>());
    }
    getTRvwQnaireAnswers().add(TRvwQnaireAnswer);
    TRvwQnaireAnswer.setTRvwQnaireRespVersion(this);

    return TRvwQnaireAnswer;
  }

  /**
   * @param TRvwQnaireAnswer as input
   * @return TRvwQnaireAnswer
   */
  public TRvwQnaireAnswer removeTRvwQnaireAnswer(final TRvwQnaireAnswer TRvwQnaireAnswer) {
    getTRvwQnaireAnswers().remove(TRvwQnaireAnswer);

    return TRvwQnaireAnswer;
  }


  /**
   * @return the tQuestionnaireVersion
   */
  public TQuestionnaireVersion getTQuestionnaireVersion() {
    return this.TQuestionnaireVersion;
  }


  /**
   * @param tQuestionnaireVersion the tQuestionnaireVersion to set
   */
  public void setTQuestionnaireVersion(final TQuestionnaireVersion tQuestionnaireVersion) {
    this.TQuestionnaireVersion = tQuestionnaireVersion;
  }


  /**
   * @return the qnaireVersStatus
   */
  public String getQnaireVersStatus() {
    return this.qnaireVersStatus;
  }


  /**
   * @param qnaireVersStatus the qnaireVersStatus to set
   */
  public void setQnaireVersStatus(final String qnaireVersStatus) {
    this.qnaireVersStatus = qnaireVersStatus;
  }


  /**
   * @return the reviewedDate
   */
  public Timestamp getReviewedDate() {
    return this.reviewedDate;
  }


  /**
   * @param reviewedDate the reviewedDate to set
   */
  public void setReviewedDate(final Timestamp reviewedDate) {
    this.reviewedDate = reviewedDate;
  }


  /**
   * @return the reviewedUser
   */
  public String getReviewedUser() {
    return this.reviewedUser;
  }


  /**
   * @param reviewedUser the reviewedUser to set
   */
  public void setReviewedUser(final String reviewedUser) {
    this.reviewedUser = reviewedUser;
  }

}