/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.Query;

import com.bosch.caltool.apic.jpa.bo.AbstractProjectObject;
import com.bosch.caltool.apic.jpa.bo.IPIDCAttribute;
import com.bosch.caltool.apic.jpa.bo.PIDCVariant;
import com.bosch.caltool.apic.jpa.bo.PIDCVersion;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireAnswer;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * @author jvi6cob
 */
@Deprecated
public class QuestionnaireResponse extends AbstractCdrObject implements Comparable<QuestionnaireResponse> {

  /**
   * Initial buffer length of tooltip SB
   */
  private static final int SB_TOOLTIP_LEN = 100;

  /**
   * Filled answers
   */
  private final ConcurrentMap<Question, ReviewQnaireAnswer> definedQAMap = new ConcurrentHashMap<>();


  /**
   * @return the definedQAMap
   */
  protected ConcurrentMap<Question, ReviewQnaireAnswer> getDefinedQAMap() {
    return this.definedQAMap;
  }

  /**
   * All answer objects. Includes dummy objects created against questions, headings, questionnaire versions, when answer
   * not created yet. Key - Question ID, Value - answer objects
   * <p>
   * It is assumed that question id and questionnaire id will never be same
   */
  private final ConcurrentMap<Long, ReviewQnaireAnswer> allQAMap = new ConcurrentHashMap<>();

  /**
   * List of applicable review results
   */
  private final Set<CDRResult> rvwResults = new HashSet<>();

  /**
   * If true, response is modifiable for the current user
   */
  private Boolean modifiable;

  /**
   * Sorted set of all question responses, including headings
   */
  private final SortedSet<ReviewQnaireAnswer> qnResponseSet = new TreeSet<>();

  /**
   * If true, children should be reloaded again
   */
  private boolean refreshChildren = true;

  /**
   * Constructor
   *
   * @param cdrDataProvider CDRDataProvider
   * @param respID response ID, primary key
   */
  // ICDM-2404
  public QuestionnaireResponse(final CDRDataProvider cdrDataProvider, final Long respID) {
    super(cdrDataProvider, respID);
    getDataCache().addRemoveQnaireResponse(this, true);
  }

  /**
   * @return Name of the questionnaire version
   */
  @Override
  public String getName() {
    return getQNaireVersion().getName();
  }

  /**
   * Extended name of this response object including the PIDC variant
   *
   * @return extended name as string
   */
  public String getNameExt() {
    StringBuilder extName = new StringBuilder();
    extName.append(getQNaireVersion().getName()).append(" : ").append(getPidcVersion().getName());
    if (getPidcVariant() != null) {
      extName.append(" >> ").append(getPidcVariant().getName());
    }
    return extName.toString();
  }

  /**
   * @return Description of questionnaire and questionnaire version
   */
  @Override
  public String getDescription() {
    return getQNaireVersion().getQuestionnaire().getDescription() + " - " + getQNaireVersion().getDescription();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTip() {
    StringBuilder tooltip = new StringBuilder(SB_TOOLTIP_LEN);
    tooltip.append("Name : ").append(getQNaireVersion().getName()).append("\nPIDC Version : ")
        .append(getPidcVersion().getName());

    if (getPidcVariant() != null) {
      tooltip.append("\nVariant : ").append(getPidcVariant().getName());
    }
    tooltip.append("\nDescription: ").append(getQNaireVersion().getDescription());

    return tooltip.toString();
  }

  /**
   * Get all question responses as a map. This also includes the response objects for headings. If a question response
   * is not filled yet, a dummy object is returned, which gets updated later when response is defined.
   *
   * @param refresh if true, refreshes the response collection
   * @return Map of responses. Key - Question/Questionnaire version ID(dummy objects). Value - Response object
   */
  protected Map<Long, ReviewQnaireAnswer> getAllQuestionResponsesMap(final boolean refresh) {
    if (refresh || this.refreshChildren) {

      fillAnswers();
      this.refreshChildren = false;
    }
    return this.allQAMap;
  }

  /**
   * Get all question responses as a sorted set order by question number. This also includes the response objects for
   * headings. If a question response is not filled yet, a dummy object is returned, which gets updated later when
   * response is defined.
   *
   * @param refresh if true, refreshes the response collection
   * @return sorted set of responses
   */
  public SortedSet<ReviewQnaireAnswer> getAllQuestionResponses(final boolean refresh) {
    if (refresh || this.refreshChildren) {
      this.qnResponseSet.addAll(getAllQuestionResponsesMap(refresh).values());
    }
    return this.qnResponseSet;
  }

  /**
   * Get the review answer object for the given question in the questionnaire. For heading type questions, a dummy
   * object will be returned.
   *
   * @param question for which the response is sought.
   * @return the response object
   */
  public ReviewQnaireAnswer getReviewQnaireAnswer(final Question question) {
    return getAllQuestionResponsesMap(false).get(question.getID());
  }

  /**
   * Get the review answer object for the given QuestionnaireVersion in the questionnaire. Note : this is a dummy object
   *
   * @param qnaireVers for which the response is sought.
   * @return the response object
   */
  public ReviewQnaireAnswer getReviewQnaireAnswer(final QuestionnaireVersion qnaireVers) {
    return getAllQuestionResponsesMap(false).get(qnaireVers.getID());
  }

  /**
   * Fill all question responses.
   */
  private void fillAnswers() {
    this.definedQAMap.clear();
    this.allQAMap.clear();
    this.qnResponseSet.clear();

    fillDefinedAnswers();

    // Consolidate all project - variant attributes to resolve questions' attribute dependencies
    Map<Long, IPIDCAttribute> projAttrMap = getEffectiveProjectAttrs();

    // General Questionnaire
    if (getGeneralQnaireVers() != null) {
      fillAllAnswersMap(getGeneralQnaireVers(), projAttrMap);
    }
    // Specific questionnaire
    fillAllAnswersMap(getQNaireVersion(), projAttrMap);


  }

  /**
   * Validate defined answers against project attributes. Add dummy answers for all headings, not defined answers, dummy
   * questionnaire objects
   *
   * @param questions
   * @param projAttrMap
   */
  private void fillAllAnswersMap(final QuestionnaireVersion qnaireVers, final Map<Long, IPIDCAttribute> projAttrMap) {

    SortedSet<Question> questions = qnaireVers.getAllQuestions();

    boolean qnsFound = false;

    for (Question question : questions) {
      if (!question.isValid(projAttrMap)) {
        continue;
      }
      ReviewQnaireAnswer rvwAnsForQuestion = this.definedQAMap.get(question);
      if (rvwAnsForQuestion == null) {
        rvwAnsForQuestion = new ReviewQnaireAnswer(getDataProvider(), question, null, this);
        // Dummy object created
      }
      this.allQAMap.put(question.getID(), rvwAnsForQuestion);
      qnsFound = true;
    }

    // Add questionnaire version type dummy heading, only if questions are present in the questionnaire version attached
    if (qnsFound) {
      this.allQAMap.put(qnaireVers.getID(), new ReviewQnaireAnswerDummyQnaireVers(getDataProvider(), qnaireVers, this));
    }

  }

  /**
   * Fill questionnaire answers
   */
  // ICDM-2332
  private void fillDefinedAnswers() {
    // Set<TRvwQnaireAnswer> tRvwQnaireAnswers =
    // getEntityProvider().getDbQnaireResponse(getID()).getTRvwQnaireAnswers();
    // populateAnswers(tRvwQnaireAnswers);
    getLogger().debug("Question response count for questionnaire response {} = {} ", this, this.definedQAMap.size());
  }

  /**
   * @return true if it is deleted in db
   */
  public boolean isDeleted() {
    return getEntityProvider().getDbQnaireResponse(getID()) == null;
  }

  /**
   * @param tRvwQnaireAnswers
   */
  private void populateAnswers(final Set<TRvwQnaireAnswer> tRvwQnaireAnswers) {
    ReviewQnaireAnswer rvwQnaireAnsBO;
    Question question;
    long rvwAnswrId;
    Long qID;
    for (TRvwQnaireAnswer tRvwQnaireAnswer : tRvwQnaireAnswers) {
      qID = tRvwQnaireAnswer.getTQuestion().getQId();
      question = getDataProvider().getQuestion(qID);
      // Question object might not have been created yet
      if (question == null) {
        question = new Question(getDataProvider(), qID);
      }

      rvwAnswrId = tRvwQnaireAnswer.getRvwAnswerId();
      rvwQnaireAnsBO = getDataProvider().getDataCache().getReviewQnaireAnswer(rvwAnswrId);
      if (rvwQnaireAnsBO == null) {
        rvwQnaireAnsBO = new ReviewQnaireAnswer(getDataProvider(), question, rvwAnswrId, this);
      }

      this.definedQAMap.put(question, rvwQnaireAnsBO);
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final QuestionnaireResponse other) {
    // Compare name
    int compResult = ApicUtil.compare(getName(), other.getName());
    // If name is same, then compare by id(primary key)
    if (compResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      return ApicUtil.compare(getID(), other.getID());
    }
    return compResult;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * Parses through the CDRResults applicable to this {@link QuestionnaireResponse}'s {@link QuestionnaireVersion}, if a
   * match is found ,true is returned
   *
   * @param refresh True to iterate through all results to find the access rights
   * @return boolean
   * @throws ApicWebServiceException
   */
  // TODO: The list of editable users from the available CDRResults can be cached. But when new review results are added
  // this list has to be modified. The above case is applicable for isModifiable boolean variable also
  public boolean isModifiable(final boolean refresh) {

    if (refresh || (this.modifiable == null)) {
      if (getApicDataProvider().getCurrentUser().hasApicWriteAccess()) {
        this.modifiable = true;
        return this.modifiable;
      }
      this.modifiable = false;
      final Long currentUserID = getApicDataProvider().getCurrentUser().getUserID();
      // Allow editing, only if the current user is assigned as calibration engineer or auditor for this review
      // ICDM-1746 Extending the modifying privilege to the user who created the review

      for (CDRResult cdrResult : getRvwResults()) {
        if (((cdrResult.getCalibrationEngineer() != null) &&
            (currentUserID.longValue() == cdrResult.getCalibrationEngineer().getID().longValue())) ||
            ((cdrResult.getAuditor() != null) &&
                (currentUserID.longValue() == cdrResult.getAuditor().getID().longValue())) ||
            CommonUtils.isEqual(cdrResult.getCreatedUser(), getApicDataProvider().getCurrentUser().getUserName())) {
          this.modifiable = true;
          break;
        }
      }
    }
    return this.modifiable;
  }


  /**
   * @return the Questionnaire Version
   */
  public QuestionnaireVersion getQNaireVersion() {
    // Long qnaireVersID =
    // getEntityProvider().getDbQnaireResponse(getID()).getTQuestionnaireVersion().getQnaireVersId();
    // QuestionnaireVersion qnaireVers = getDataProvider().getQuestionnaireVersion(qnaireVersID);
//    if (qnaireVers == null) {
//      // TODO : create qnaire?
//      qnaireVers = new QuestionnaireVersion(getDataProvider(), qnaireVersID);
//    }
    return null;
  }

  /**
   * @return the genQnaireVers
   */
  // ICDM-2155
  public QuestionnaireVersion getGeneralQnaireVers() {
    AbstractProjectObject<?> projObj = getPidcVariant();
    if (projObj == null) {
      projObj = getPidcVersion();
    }
    return getDataCache().getProjGenQnaireVers(projObj);
  }

  /**
   * @return true if this questionnaire reponse has general questions
   */
  // ICDM-2155
  public boolean hasGeneralQuestions() {
    return getGeneralQnaireVers() != null;
  }

  /**
   * @return true if relevancy in questionnaire
   */
  public boolean showSeriesMaturity() {
    boolean genQniareStatus = getGeneralQnaireVers() == null ? false
        : CommonUtils.getBooleanType(getGeneralQnaireVers().getSeriesRelevantFlag());
    return CommonUtils.getBooleanType(getQNaireVersion().getSeriesRelevantFlag()) || genQniareStatus;

  }

  /**
   * @return true if relevancy in questionnaire
   */
  public boolean showMeasurement() {
    boolean genQniareStatus = getGeneralQnaireVers() == null ? false
        : CommonUtils.getBooleanType(getGeneralQnaireVers().getMeasurementRelevantFlag());
    return CommonUtils.getBooleanType(getQNaireVersion().getMeasurementRelevantFlag()) || genQniareStatus;
  }

  /**
   * @return true if relevancy in questionnaire
   */
  public boolean showLinks() {
    boolean genQniareStatus = getGeneralQnaireVers() == null ? false
        : CommonUtils.getBooleanType(getGeneralQnaireVers().getLinkRelevantFlag());
    return CommonUtils.getBooleanType(getQNaireVersion().getLinkRelevantFlag()) || genQniareStatus;
  }

  /**
   * @return true if relevancy in questionnaire
   */
  public boolean showOpenPoints() {
    boolean genQniareStatus = getGeneralQnaireVers() == null ? false
        : CommonUtils.getBooleanType(getGeneralQnaireVers().getOpenPointsRelevantFlag());
    return CommonUtils.getBooleanType(getQNaireVersion().getOpenPointsRelevantFlag()) || genQniareStatus;
  }

  /**
   * @return true if relevancy in questionnaire
   */
  public boolean showMeasures() {
    return false;
  }

  /**
   * @return true if relevancy in questionnaire
   */
  public boolean showRemarks() {
    boolean genQniareStatus = getGeneralQnaireVers() == null ? false
        : CommonUtils.getBooleanType(getGeneralQnaireVers().getRemarkRelevantFlag());
    return CommonUtils.getBooleanType(getQNaireVersion().getRemarkRelevantFlag()) || genQniareStatus;
  }

  /**
   * @return true if relevancy in questionnaire
   */
  public boolean showResult() {
    boolean genQniareStatus = getGeneralQnaireVers() == null ? false
        : CommonUtils.getBooleanType(getGeneralQnaireVers().getResultRelevantFlag());
    return CommonUtils.getBooleanType(getQNaireVersion().getResultRelevantFlag()) || genQniareStatus;
  }


  /**
   * @return the pidc Variant. Can be <code>null</code>.
   */
  public PIDCVariant getPidcVariant() {
//    TabvProjectVariant dbVar = getEntityProvider().getDbQnaireResponse(getID()).getTabvProjectVariant();
//    return dbVar == null ? null : getApicDataProvider().getPidcVaraint(dbVar.getVariantId());
    return null;
  }


  /**
   * @return the PIDC Version
   */
  public PIDCVersion getPidcVersion() {
//    Long pidcVersID = getEntityProvider().getDbQnaireResponse(getID()).getTPidcVersion().getPidcVersId();
//    return getApicDataProvider().getPidcVersion(pidcVersID);
    return null;
  }

  /**
   * @return all top level child questions of this question response (level = 1)
   */
  public SortedSet<ReviewQnaireAnswer> getTopLevelQuestions() {
    SortedSet<ReviewQnaireAnswer> topLevlQnSet = new TreeSet<>();

    ReviewQnaireAnswer dummyAnswer;
    // ICDM-2155
    // Top level entry for 'General' questionnaire
    if (getGeneralQnaireVers() != null) {
      dummyAnswer = getReviewQnaireAnswer(getGeneralQnaireVers());
      if (dummyAnswer != null) {
        topLevlQnSet.add(dummyAnswer);
      }
    }

    // Top level entry for main questionnaire
    dummyAnswer = getReviewQnaireAnswer(getQNaireVersion());
    if (dummyAnswer != null) {
      topLevlQnSet.add(dummyAnswer);
    }

    return topLevlQnSet;

  }

  /**
   * Triggers a children refresh with the next call of relevant get methods
   */
  public void setRefreshChildren() {
    this.refreshChildren = true;
  }

  /**
   * Search through the questionnaire tree and find the first question response of type 'question'
   *
   * @return first question response
   */
  public ReviewQnaireAnswer getFirstQuestionResponse() {
    for (ReviewQnaireAnswer rvwQnaireAnswer : getAllQuestionResponses(false)) {
      if (!rvwQnaireAnswer.isHeading()) {
        return rvwQnaireAnswer;
      }
    }
    return null;
  }

  /**
   * Find the effective project attributes for this variant + parent project
   *
   * @return map of project attributes. Key - attribute ID, Value - IPIDCAttribute
   */
  private ConcurrentMap<Long, IPIDCAttribute> getEffectiveProjectAttrs() {
    ConcurrentMap<Long, IPIDCAttribute> projAttrMap = new ConcurrentHashMap<>();
    projAttrMap.putAll(getPidcVersion().getAttributes());
    if (getPidcVariant() != null) {
      projAttrMap.putAll(getPidcVariant().getAttributes());
    }
    return projAttrMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {

    StringBuilder str = new StringBuilder(super.toString());
    str.append(" [qNaireVersion=").append(getQNaireVersion().getID());
    if (getPidcVariant() != null) {
      str.append(", pidcVariant=").append(getPidcVariant().getID());
    }
    if (getPidcVersion() != null) {
      str.append(", pidcVersion=").append(getPidcVersion().getID());
    }
    str.append(']');

    return str.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbQnaireResponse(getID()).getCreatedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbQnaireResponse(getID()).getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbQnaireResponse(getID()).getCreatedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbQnaireResponse(getID()).getModifiedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return CDREntityType.RVW_QNAIRE_RESPONSE;
  }

  /**
   * @return the rvwResults
   */
  public Set<CDRResult> getRvwResults() {
    if (this.refreshChildren || this.rvwResults.isEmpty()) {
      this.rvwResults.clear();
      fetchReviewResults();
    }
    return new HashSet<>(this.rvwResults);
  }

  /**
   * Add or remove review results
   *
   * @param result review result
   * @param add if true, then add, else delete
   */
  void addRemoveReviewResult(final CDRResult result, final boolean add) {
    if (add) {
      this.rvwResults.add(result);
    }
    else {
      this.rvwResults.remove(result);
    }
    this.modifiable = null;
  }

  /**
   * Fetch review results against the questionnaire response.
   */
  private void fetchReviewResults() {
    // TODO use entity relationship
    String queryStr =
        "select rvwqnare.result_id from T_Rvw_Results res, T_RVW_QNAIRE_RESULTS rvwqnare where res.result_id  = rvwqnare.result_id and rvwqnare.QNAIRE_RESP_ID = ?";
    Query qry = getEntityProvider().getEm().createNativeQuery(queryStr);
    qry.setParameter(1, getID());
    List<?> qryList = qry.getResultList();
    Long resultID;
    for (Object obj : qryList) {
      resultID = ((BigDecimal) obj).longValue();
      CDRResult cdrResult = getDataCache().getDataProvider().getCDRResult(resultID);
      if (cdrResult == null) {
        getDataCache().reloadPIDCResults(getPidcVersion());
        cdrResult = getDataCache().getDataProvider().getCDRResult(resultID);
      }
      this.rvwResults.add(cdrResult);
    }
  }

}
