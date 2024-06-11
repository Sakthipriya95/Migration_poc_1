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
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.OptimisticLocking;


/**
 * The persistent class for the T_QUESTIONNAIRE_VERSION database table.
 */
@Entity
@OptimisticLocking(cascade = true)
@Table(name = "T_QUESTIONNAIRE_VERSION")
@NamedQuery(name = TQuestionnaireVersion.NQ_GET_QNVERS_FOR_WPDIV, query = "                                                     " +
    "Select qsVer from TQuestionnaireVersion qsVer                                                                              " +
    "where qsVer.TQuestionnaire.qnaireId IN (                                                                                   " +
    "      select qs.qnaireId from TQuestionnaire qs where qs.tWorkpackageDivision.wpDivId IN :wpDivIds)                        ")
public class TQuestionnaireVersion implements Serializable {

  /**
   * Named query to defined questionnaire versions for the given work package - divsion IDs
   *
   * @param wpDivIds wp division IDs
   */
  public static final String NQ_GET_QNVERS_FOR_WPDIV = "TQuestionnaireVersion.getQnaireVersForWpDivs";

  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "QNAIREVERSID_GENERATOR", sequenceName = "SEQV_ATTRIBUTES")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "QNAIREVERSID_GENERATOR")
  @Column(name = "QNAIRE_VERS_ID")
  private long qnaireVersId;

  @Column(name = "ACTIVE_FLAG", length = 1)
  private String activeFlag;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 100)
  private String createdUser;

  @Column(name = "DESC_ENG", nullable = false, length = 4000)
  private String descEng;

  @Column(name = "DESC_GER", length = 4000)
  private String descGer;

  @Column(name = "INWORK_FLAG", length = 1)
  private String inworkFlag;

  @Column(name = "LINK_HIDDEN_FLAG", length = 1)
  private String linkHiddenFlag;

  @Column(name = "LINK_RELEVANT_FLAG", length = 1)
  private String linkRelevantFlag;

  @Column(name = "MAJOR_VERSION_NUM", nullable = false)
  private Long majorVersionNum;

  @Column(name = "MEASUREMENT_HIDDEN_FLAG", length = 1)
  private String measurementHiddenFlag;

  @Column(name = "MEASUREMENT_RELEVANT_FLAG", length = 1)
  private String measurementRelevantFlag;

  @Column(name = "MINOR_VERSION_NUM")
  private Long minorVersionNum;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 100)
  private String modifiedUser;

  @Column(name = "OPEN_POINTS_HIDDEN_FLAG", length = 1)
  private String openPointsHiddenFlag;

  @Column(name = "OPEN_POINTS_RELEVANT_FLAG", length = 1)
  private String openPointsRelevantFlag;

  @Column(name = "REMARK_RELEVANT_FLAG", length = 1)
  private String remarkRelevantFlag;

  @Column(name = "REMARKS_HIDDEN_FLAG", length = 1)
  private String remarksHiddenFlag;

  @Column(name = "RESULT_HIDDEN_FLAG", length = 1)
  private String resultHiddenFlag;

  @Column(name = "RESULT_RELEVANT_FLAG", length = 1)
  private String resultRelevantFlag;

  @Column(name = "SERIES_HIDDEN_FLAG", length = 1)
  private String seriesHiddenFlag;

  @Column(name = "SERIES_RELEVANT_FLAG", length = 1)
  private String seriesRelevantFlag;

  @Column(name = "GENERAL_QUES_EQUIVALENT", length = 1)
  private String genQuesEquivalentFlag;
  
  @Column(name = "NO_NEGATIVE_ANSWERS_ALLOWED", length = 1)
  private String noNegativeAnswersAllowed;

 
  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;

  @Column(name = "RESPONSIBLE_HIDDEN_FLAG")
  private String responsibleHiddenFlag;

  @Column(name = "RESPONSIBLE_RELAVENT_FLAG")
  private String responsibleRelaventFlag;

  @Column(name = "COMPLETION_DATE_HIDDEN_FLAG")
  private String completionDateHiddenFlag;

  @Column(name = "COMPLETION_DATE_RELAVENT_FLAG")
  private String completionDateRelaventFlag;

  @Column(name = "MEASURE_HIDDEN_FLAG")
  private String measureHiddenFlag;

  @Column(name = "MEASURE_RELAVENT_FLAG")
  private String measureRelaventFlag;

  // bi-directional many-to-one association to TQuestion
  @OneToMany(mappedBy = "TQuestionnaireVersion", fetch = FetchType.LAZY)
  private Set<TQuestion> TQuestions;


  // bi-directional many-to-one association to TQuestionnaire
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "QNAIRE_ID", nullable = false)
  private TQuestionnaire TQuestionnaire;

  // bi-directional many-to-one association to TRvwQnaireRespVersion
  @OneToMany(mappedBy = "TQuestionnaireVersion", fetch = FetchType.LAZY)
  private Set<TRvwQnaireRespVersion> tRvwQnaireRespVersions;

  public TQuestionnaireVersion() {
   // Do nothing 
  }

  public long getQnaireVersId() {
    return this.qnaireVersId;
  }

  public void setQnaireVersId(final long qnaireVersId) {
    this.qnaireVersId = qnaireVersId;
  }

  public String getActiveFlag() {
    return this.activeFlag;
  }

  public void setActiveFlag(final String activeFlag) {
    this.activeFlag = activeFlag;
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

  public String getInworkFlag() {
    return this.inworkFlag;
  }

  public void setInworkFlag(final String inworkFlag) {
    this.inworkFlag = inworkFlag;
  }

  public String getLinkHiddenFlag() {
    return this.linkHiddenFlag;
  }

  public void setLinkHiddenFlag(final String linkHiddenFlag) {
    this.linkHiddenFlag = linkHiddenFlag;
  }

  public String getLinkRelevantFlag() {
    return this.linkRelevantFlag;
  }

  public void setLinkRelevantFlag(final String linkRelevantFlag) {
    this.linkRelevantFlag = linkRelevantFlag;
  }

  public Long getMajorVersionNum() {
    return this.majorVersionNum;
  }

  public void setMajorVersionNum(final Long majorVersionNum) {
    this.majorVersionNum = majorVersionNum;
  }

  public String getMeasurementHiddenFlag() {
    return this.measurementHiddenFlag;
  }

  public void setMeasurementHiddenFlag(final String measurementHiddenFlag) {
    this.measurementHiddenFlag = measurementHiddenFlag;
  }

  public String getMeasurementRelevantFlag() {
    return this.measurementRelevantFlag;
  }

  public void setMeasurementRelevantFlag(final String measurementRelevantFlag) {
    this.measurementRelevantFlag = measurementRelevantFlag;
  }

  public Long getMinorVersionNum() {
    return this.minorVersionNum;
  }

  public void setMinorVersionNum(final Long minorVersionNum) {
    this.minorVersionNum = minorVersionNum;
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

  public String getOpenPointsHiddenFlag() {
    return this.openPointsHiddenFlag;
  }

  public void setOpenPointsHiddenFlag(final String openPointsHiddenFlag) {
    this.openPointsHiddenFlag = openPointsHiddenFlag;
  }

  public String getOpenPointsRelevantFlag() {
    return this.openPointsRelevantFlag;
  }

  public void setOpenPointsRelevantFlag(final String openPointsRelevantFlag) {
    this.openPointsRelevantFlag = openPointsRelevantFlag;
  }


  public String getRemarkRelevantFlag() {
    return this.remarkRelevantFlag;
  }

  public void setRemarkRelevantFlag(final String remarkRelevantFlag) {
    this.remarkRelevantFlag = remarkRelevantFlag;
  }

  public String getRemarksHiddenFlag() {
    return this.remarksHiddenFlag;
  }

  public void setRemarksHiddenFlag(final String remarksHiddenFlag) {
    this.remarksHiddenFlag = remarksHiddenFlag;
  }

  public String getResultHiddenFlag() {
    return this.resultHiddenFlag;
  }

  public void setResultHiddenFlag(final String resultHiddenFlag) {
    this.resultHiddenFlag = resultHiddenFlag;
  }

  public String getResultRelevantFlag() {
    return this.resultRelevantFlag;
  }

  public void setResultRelevantFlag(final String resultRelevantFlag) {
    this.resultRelevantFlag = resultRelevantFlag;
  }

  public String getSeriesHiddenFlag() {
    return this.seriesHiddenFlag;
  }

  public void setSeriesHiddenFlag(final String seriesHiddenFlag) {
    this.seriesHiddenFlag = seriesHiddenFlag;
  }

  public String getSeriesRelevantFlag() {
    return this.seriesRelevantFlag;
  }

  public void setSeriesRelevantFlag(final String seriesRelevantFlag) {
    this.seriesRelevantFlag = seriesRelevantFlag;
  }


  /**
   * @return the genQuesEquivalentFlag
   */
  public String getGenQuesEquivalentFlag() {
    return this.genQuesEquivalentFlag;
  }


  /**
   * @param genQuesEquivalentFlag the genQuesEquivalentFlag to set
   */
  public void setGenQuesEquivalentFlag(final String genQuesEquivalentFlag) {
    this.genQuesEquivalentFlag = genQuesEquivalentFlag;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public Set<TQuestion> getTQuestions() {
    return this.TQuestions;
  }

  public void setTQuestions(final Set<TQuestion> TQuestions) {
    this.TQuestions = TQuestions;
  }

  public TQuestionnaire getTQuestionnaire() {
    return this.TQuestionnaire;
  }

  public void setTQuestionnaire(final TQuestionnaire TQuestionnaire) {
    this.TQuestionnaire = TQuestionnaire;
  }

  public String getCompletionDateHiddenFlag() {
    return this.completionDateHiddenFlag;
  }

  public void setCompletionDateHiddenFlag(final String completionDateHiddenFlag) {
    this.completionDateHiddenFlag = completionDateHiddenFlag;
  }

  public String getCompletionDateRelaventFlag() {
    return this.completionDateRelaventFlag;
  }

  public void setCompletionDateRelaventFlag(final String completionDateRelaventFlag) {
    this.completionDateRelaventFlag = completionDateRelaventFlag;
  }

  public String getMeasureHiddenFlag() {
    return this.measureHiddenFlag;
  }

  public void setMeasureHiddenFlag(final String measureHiddenFlag) {
    this.measureHiddenFlag = measureHiddenFlag;
  }

  public String getMeasureRelaventFlag() {
    return this.measureRelaventFlag;
  }

  public void setMeasureRelaventFlag(final String measureRelaventFlag) {
    this.measureRelaventFlag = measureRelaventFlag;
  }

  public String getResponsibleHiddenFlag() {
    return this.responsibleHiddenFlag;
  }

  public void setResponsibleHiddenFlag(final String responsibleHiddenFlag) {
    this.responsibleHiddenFlag = responsibleHiddenFlag;
  }

  public String getResponsibleRelaventFlag() {
    return this.responsibleRelaventFlag;
  }

  public void setResponsibleRelaventFlag(final String responsibleRelaventFlag) {
    this.responsibleRelaventFlag = responsibleRelaventFlag;
  }

  /**
   * @return the tRvwQnaireRespVersions
   */
  public Set<TRvwQnaireRespVersion> getTRvwQnaireRespVersions() {
    return this.tRvwQnaireRespVersions;
  }


  /**
   * @param tRvwQnaireRespVersions the tRvwQnaireRespVersions to set
   */
  public void setTRvwQnaireRespVersions(final Set<TRvwQnaireRespVersion> tRvwQnaireRespVersions) {
    this.tRvwQnaireRespVersions = tRvwQnaireRespVersions;
  }
  
  
  /**
   * @return the noNegativeAnswersAllowed
   */
  public String getNoNegativeAnswersAllowed() {
    return noNegativeAnswersAllowed;
  }

  
  /**
   * @param noNegativeAnswersAllowed the noNegativeAnswersAllowed to set
   */
  public void setNoNegativeAnswersAllowed(String noNegativeAnswersAllowed) {
    this.noNegativeAnswersAllowed = noNegativeAnswersAllowed;
  }


  /**
   * @param tRvwQnaireRespVersion as input
   * @return tRvwQnaireRespVersion
   */
  public TRvwQnaireRespVersion addTRvwQnaireRespVersion(final TRvwQnaireRespVersion tRvwQnaireRespVersion) {
    if (getTRvwQnaireRespVersions() == null) {
      setTRvwQnaireRespVersions(new HashSet<>());
    }
    getTRvwQnaireRespVersions().add(tRvwQnaireRespVersion);

    tRvwQnaireRespVersion.setTQuestionnaireVersion(this);

    return tRvwQnaireRespVersion;
  }
}