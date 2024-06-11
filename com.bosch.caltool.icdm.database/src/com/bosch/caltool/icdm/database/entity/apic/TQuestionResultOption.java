package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the T_QUESTION_RESULT_OPTIONS database table.
 */
@Entity
@Table(name = "T_QUESTION_RESULT_OPTIONS")

public class TQuestionResultOption implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "QUESTION_RESULT_OPTIONS_GENERATOR", sequenceName = "SEQV_ATTRIBUTES")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "QUESTION_RESULT_OPTIONS_GENERATOR")
  @Column(name = "Q_RESULT_OPT_ID")
  private long qResultOptionId;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "Q_RESULT_NAME")
  private String qResultName;

  @Column(name = "Q_RESULT_TYPE")
  private String qResultType;

  @Column(name = "Q_RESULT_ALW_FINISH_WP", length = 1)
  private String qResultAlwFinishWP;

  @Column(name = "\"VERSION\"")
  @Version
  private long version;

  // bi-directional many-to-one association to TQuestion
  @ManyToOne
  @JoinColumn(name = "Q_ID")
  private TQuestion TQuestion;

  // bi-directional many-to-one association to TRvwQnaireAnswer
  @OneToMany(mappedBy = "tQuestionResultOption")
  private List<TRvwQnaireAnswer> tRvwQnaireAnswers;

  public TQuestionResultOption() {}

  public long getQResultOptionId() {
    return this.qResultOptionId;
  }

  public void setQResultOptionId(final long qResultId) {
    this.qResultOptionId = qResultId;
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

  public String getQResultName() {
    return this.qResultName;
  }

  public void setQResultName(final String qResultName) {
    this.qResultName = qResultName;
  }

  public String getQResultType() {
    return this.qResultType;
  }

  public void setQResultType(final String qResultType) {
    this.qResultType = qResultType;
  }

  public long getVersion() {
    return this.version;
  }

  public void setVersion(final long version) {
    this.version = version;
  }

  public TQuestion getTQuestion() {
    return this.TQuestion;
  }

  public void setTQuestion(final TQuestion TQuestion) {
    this.TQuestion = TQuestion;
  }

  public List<TRvwQnaireAnswer> getTRvwQnaireAnswers() {
    return this.tRvwQnaireAnswers;
  }

  public void setTRvwQnaireAnswers(final List<TRvwQnaireAnswer> TRvwQnaireAnswers) {
    this.tRvwQnaireAnswers = TRvwQnaireAnswers;
  }

  public TRvwQnaireAnswer addTRvwQnaireAnswer(final TRvwQnaireAnswer TRvwQnaireAnswer) {
    getTRvwQnaireAnswers().add(TRvwQnaireAnswer);
    TRvwQnaireAnswer.setTQuestionResultOption(this);

    return TRvwQnaireAnswer;
  }

  public TRvwQnaireAnswer removeTRvwQnaireAnswer(final TRvwQnaireAnswer TRvwQnaireAnswer) {
    getTRvwQnaireAnswers().remove(TRvwQnaireAnswer);
    TRvwQnaireAnswer.setTQuestionResultOption(null);

    return TRvwQnaireAnswer;
  }


  /**
   * @return the qResultAlwFinishWP
   */
  public String getqResultAlwFinishWP() {
    return this.qResultAlwFinishWP;
  }


  /**
   * @param qResultAlwFinishWP the qResultAlwFinishWP to set
   */
  public void setqResultAlwFinishWP(final String qResultAlwFinishWP) {
    this.qResultAlwFinishWP = qResultAlwFinishWP;
  }

}