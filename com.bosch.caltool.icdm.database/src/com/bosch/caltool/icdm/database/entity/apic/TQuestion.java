package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.annotations.OptimisticLocking;


/**
 * The persistent class for the T_QUESTION database table.
 */
@Entity
@OptimisticLocking(cascade = true)
@Table(name = "T_QUESTION")
public class TQuestion implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "QUESTION_QID_GENERATOR", sequenceName = "SEQV_ATTRIBUTES")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "QUESTION_QID_GENERATOR")
  @Column(name = "Q_ID")
  private long qId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 100)
  private String createdUser;

  @Column(name = "DELETED_FLAG", length = 1)
  private String deletedFlag;

  @Column(name = "RESULT_RELEVANT_FLAG", length = 1)
  private String resultRelevantFlag;

  @Column(name = "HEADING_FLAG", length = 1)
  private String headingFlag;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 100)
  private String modifiedUser;

  @Column(name = "Q_HINT_ENG", nullable = false, length = 4000)
  private String qHintEng;

  @Column(name = "Q_HINT_GER", length = 4000)
  private String qHintGer;

  @Column(name = "Q_NAME_ENG", nullable = false, length = 1000)
  private String qNameEng;

  @Column(name = "Q_NAME_GER", length = 1000)
  private String qNameGer;

  @Column(name = "Q_NUMBER", nullable = false)
  private Long qNumber;

  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;

  // bi-directional one-to-one association to TQuestionConfig
  @OneToMany(mappedBy = "TQuestion", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TQuestionConfig> TQuestionConfigs;

  // bi-directional many-to-one association to TQuestionnaireVersion
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "QNAIRE_VERS_ID", nullable = false)
  private TQuestionnaireVersion TQuestionnaireVersion;

  // bi-directional many-to-one association to TQuestionDepenAttribute
  @OneToMany(mappedBy = "TQuestion", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private Set<TQuestionDepenAttribute> tQuestionDepenAttributes;

  // bi-directional many-to-one association to TQuestion
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PARENT_Q_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TQuestion TQuestion;

  // bi-directional many-to-one association to TRvwResult
  @OneToMany(mappedBy = "TQuestion", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private Set<TQuestion> TQuestions;

  // ICDM-1979
  // bi-directional many-to-one association to TRvwQnaireAnswer
  @OneToMany(mappedBy = "tQuestion", fetch = FetchType.LAZY)
  private Set<TRvwQnaireAnswer> TRvwQnaireAnswers;

  // bi-directional many-to-one association to tQuestionResultsOptions
  @OneToMany(mappedBy = "TQuestion", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TQuestionResultOption> tQuestionResultsOptions;

  /**
   * positive Result column
   */
  @Column(name = "POSITIVE_RESULT", length = 1)
  private String positiveResult;
  /**
   * question response for dependency
   */
  @Column(name = "DEP_QUES_RESP", length = 1)
  private String depQuesResponse;
  /**
   * dependency Question many to one
   */
  // bi-directional many-to-one association to TQuestion
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "DEP_QUES_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TQuestion depQuestion;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "DEP_Q_RESULT_OPT_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TQuestionResultOption depQResultOption;

  public TQuestion() {}


  public long getQId() {
    return this.qId;
  }

  public void setQId(final long qId) {
    this.qId = qId;
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

  /**
   * @return the resultRelevant
   */
  public String getResultRelevantFlag() {
    return this.resultRelevantFlag;
  }

  /**
   * @param resultRelevantFlag the resultRelevant to set
   */
  public void setResultRelevantFlag(final String resultRelevantFlag) {
    this.resultRelevantFlag = resultRelevantFlag;
  }

  public String getHeadingFlag() {
    return this.headingFlag;
  }

  public void setHeadingFlag(final String headingFlag) {
    this.headingFlag = headingFlag;
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

  public String getQHintEng() {
    return this.qHintEng;
  }

  public void setQHintEng(final String qHintEng) {
    this.qHintEng = qHintEng;
  }

  public String getQHintGer() {
    return this.qHintGer;
  }

  public void setQHintGer(final String qHintGer) {
    this.qHintGer = qHintGer;
  }

  public String getQNameEng() {
    return this.qNameEng;
  }

  public void setQNameEng(final String qNameEng) {
    this.qNameEng = qNameEng;
  }

  public String getQNameGer() {
    return this.qNameGer;
  }

  public void setQNameGer(final String qNameGer) {
    this.qNameGer = qNameGer;
  }

  public Long getQNumber() {
    return this.qNumber;
  }

  public void setQNumber(final Long qNumber) {
    this.qNumber = qNumber;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public TQuestionnaireVersion getTQuestionnaireVersion() {
    return this.TQuestionnaireVersion;
  }

  public void setTQuestionnaireVersion(final TQuestionnaireVersion TQuestionnaireVersion) {
    this.TQuestionnaireVersion = TQuestionnaireVersion;
  }

  public Set<TQuestionDepenAttribute> getTQuestionDepenAttributes() {
    return this.tQuestionDepenAttributes;
  }

  public void setTQuestionDepenAttributes(final Set<TQuestionDepenAttribute> tQuestionDepenAttributes) {
    this.tQuestionDepenAttributes = tQuestionDepenAttributes;
  }

  /**
   * @return the tQuestionConfigs
   */
  public List<TQuestionConfig> getTQuestionConfigs() {
    return this.TQuestionConfigs;
  }


  /**
   * @param tQuestionConfigs the tQuestionConfigs to set
   */
  public void setTQuestionConfigs(final List<TQuestionConfig> tQuestionConfigs) {
    this.TQuestionConfigs = tQuestionConfigs;
  }


  public TQuestionConfig getTQuestionConfig() {

    List<TQuestionConfig> configSet = getTQuestionConfigs();
    TQuestionConfig ret = null;
    if ((configSet != null) && !configSet.isEmpty()) {
      ret = configSet.get(0);
    }
    return ret;
  }

  public void setTQuestionConfig(final TQuestionConfig TQuestionConfig) {
    if (this.TQuestionConfigs == null) {
      this.TQuestionConfigs = new ArrayList<>();
    }
    else {
      this.TQuestionConfigs.clear();
    }
    this.TQuestionConfigs.add(TQuestionConfig);

  }

  public TQuestion getTQuestion() {
    return this.TQuestion;
  }

  public void setTQuestion(final TQuestion TQuestion) {
    this.TQuestion = TQuestion;
  }


  /**
   * @return the tQuestions
   */
  public Set<TQuestion> getTQuestions() {
    return this.TQuestions;
  }


  /**
   * @param tQuestions the tQuestions to set
   */
  public void setTQuestions(final Set<TQuestion> tQuestions) {
    this.TQuestions = tQuestions;
  }

  public Set<TRvwQnaireAnswer> getTRvwQnaireAnswers() {
    return this.TRvwQnaireAnswers;
  }

  public void setTRvwQnaireAnswers(final Set<TRvwQnaireAnswer> TRvwQnaireAnswers) {
    this.TRvwQnaireAnswers = TRvwQnaireAnswers;
  }


  /**
   * @return the positiveResult
   */
  public String getPositiveResult() {
    return this.positiveResult;
  }


  /**
   * @param positiveResult the positiveResult to set
   */
  public void setPositiveResult(final String positiveResult) {
    this.positiveResult = positiveResult;
  }


  /**
   * @return the depQuesResponse
   */
  public String getDepQuesResponse() {
    return this.depQuesResponse;
  }


  /**
   * @param depQuesResponse the depQuesResponse to set
   */
  public void setDepQuesResponse(final String depQuesResponse) {
    this.depQuesResponse = depQuesResponse;
  }


  /**
   * @return the depQuestion
   */
  public TQuestion getDepQuestion() {
    return this.depQuestion;
  }


  /**
   * @param depQuestion the depQuestion to set
   */
  public void setDepQuestion(final TQuestion depQuestion) {
    this.depQuestion = depQuestion;
  }

  /**
   * Adds the Tquestiondepenattributes.
   *
   * @param tQuestionDepenAttribute the t question depen attribute
   * @return the tquestiondepenattribute
   */
  public TQuestionDepenAttribute addTQuestionDepenAttributes(final TQuestionDepenAttribute tQuestionDepenAttribute) {
    getTQuestionDepenAttributes().add(tQuestionDepenAttribute);
    tQuestionDepenAttribute.setTQuestion(this);

    return tQuestionDepenAttribute;
  }

  /**
   * Removes the Tquestiondepenattributes.
   *
   * @param tQuestionDepenAttribute the t question depen attribute
   * @return the tquestiondepenattribute
   */
  public TQuestionDepenAttribute removeTQuestionDepenAttributes(final TQuestionDepenAttribute tQuestionDepenAttribute) {
    getTQuestionDepenAttributes().remove(tQuestionDepenAttribute);
    return tQuestionDepenAttribute;
  }


  /**
   * @param TQuestionResultOption as input
   * @return TQuestionResultOption
   */
  public TQuestionResultOption addTQuestionResultOption(final TQuestionResultOption tQuestionResultOption) {
    if (getTQuestionResultOptions() == null) {
      setTQuestionResultOptions(new ArrayList<>());
    }
    getTQuestionResultOptions().add(tQuestionResultOption);

    tQuestionResultOption.setTQuestion(this);

    return tQuestionResultOption;
  }

  /**
   * @param TQuestionResultOption as input
   * @return TQuestionResultOption
   */
  public TQuestionResultOption removeTQuestionResultOption(final TQuestionResultOption tQuestionResultOption) {
    getTQuestionResultOptions().remove(tQuestionResultOption);
    return tQuestionResultOption;
  }

  /**
   * @return the tQuestionResults
   */
  public List<TQuestionResultOption> getTQuestionResultOptions() {
    return this.tQuestionResultsOptions;
  }


  /**
   * @param tQuestionResultOptions the tQuestionResults to set
   */
  public void setTQuestionResultOptions(final List<TQuestionResultOption> tQuestionResultOptions) {
    this.tQuestionResultsOptions = tQuestionResultOptions;
  }


  /**
   * @return the depQResultOption
   */
  public TQuestionResultOption getDepQResultOption() {
    return depQResultOption;
  }


  /**
   * @param depQResultOption the depQResultOption to set
   */
  public void setDepQResultOption(TQuestionResultOption depQResultOption) {
    this.depQResultOption = depQResultOption;
  }
}