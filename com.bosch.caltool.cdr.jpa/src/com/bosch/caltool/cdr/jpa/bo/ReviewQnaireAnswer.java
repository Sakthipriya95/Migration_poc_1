/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */

package com.bosch.caltool.cdr.jpa.bo;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.apic.jpa.bo.Link;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireAnswerOpl;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.QS_RESULT_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.QUESTION_CONFIG_TYPE;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireConstants.QUESTION_RESP_RESULT;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireConstants.QUESTION_RESP_RESULT_FINISHED;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireConstants.QUESTION_RESP_RESULT_NO;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireConstants.QUESTION_RESP_RESULT_YES;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireConstants.QUESTION_RESP_SERIES_MEASURE;


/**
 * @author jvi6cob
 */
@Deprecated
public class ReviewQnaireAnswer extends AbstractCdrObject implements Comparable<ReviewQnaireAnswer> {

  /**
   * not applicable constant
   */
  private static final String NOT_APPL = "<NOT APPLICABLE>";

  /**
   * id
   */
  private Long revQnaireAnsObjID;

  /**
   * Question
   */
  private final Question question;

  /**
   * QuestionnaireResponse
   */
  private final QuestionnaireResponse quesResponse;
  /**
   * hashcode number
   */
  static final int HASHCODE_PRIME = 31;

  // ICDM-1987
  /**
   * enum for columns
   */
  public enum SortColumns {
                           /**
                            * Ques Name
                            */
                           SORT_QUES_NAME,
                           /**
                            * Ques Number
                            */
                           SORT_QUES_NUMBER,
                           /**
                            * Ques Hint
                            */
                           SORT_QUES_HINT,
                           /**
                            * Measurable
                            */
                           SORT_MEASURABLE,
                           /**
                            * Series
                            */
                           SORT_SERIES,
                           /**
                            * Link
                            */
                           SORT_LINK,
                           /**
                            * Result
                            */
                           SORT_RESULT,

                           /**
                            * Remark
                            */
                           SORT_REMARK,
                           /**
                            * Open points
                            */
                           SORT_OP
  }

  /**
   * List of open points mapped to each review answer
   */
  private final List<QnaireAnsOpenPoint> openPointsList = new ArrayList<QnaireAnsOpenPoint>();


  /**
   * @param cdrDataProvider CDRDataProvider
   * @param question Question
   * @param rvwQnaireAnsID Long id of ReviewQnaireAnswer
   * @param quesResponse QuestionnaireResponse
   */
  public ReviewQnaireAnswer(final CDRDataProvider cdrDataProvider, final Question question, final Long rvwQnaireAnsID,
      final QuestionnaireResponse quesResponse) {
    super(cdrDataProvider, rvwQnaireAnsID);
    if (null != rvwQnaireAnsID) {
      getDataCache().getReviewQnaireAnsMap().put(rvwQnaireAnsID, this);
    }
    this.revQnaireAnsObjID = rvwQnaireAnsID;
    this.question = question;
    this.quesResponse = quesResponse;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbReviewQnaireAnswer(getID()).getCreatedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbReviewQnaireAnswer(getID()).getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbReviewQnaireAnswer(getID()).getCreatedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbReviewQnaireAnswer(getID()).getModifiedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return CDREntityType.RVW_QNAIRE_ANS;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return this.question.getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return this.question.getDescription();
  }

  /**
   * @return the Question associated with this QA
   */
  public Question getQuestion() {
    return this.question;
  }


  /**
   * Generates the open point data
   *
   * @return open points display string
   */
  public String getOpenPointsUIString() {
    if (isHeading()) {
      return "";
    }
    if (showOpenPoints()) {
      StringBuilder opPoints = new StringBuilder();
      if (!getOpenPointsList().isEmpty()) {
        generateOPString(opPoints);
      }
      return opPoints.toString();
    }
    return NOT_APPL;
  }

  /**
   * String representation of all open points of this question response
   *
   * @param opPoints
   * @param count
   * @return
   */
  private int generateOPString(final StringBuilder opPoints) {
    int count = 1;
    for (QnaireAnsOpenPoint openPoint : this.openPointsList) {
      if (count > 1) {
        opPoints.append('\n');
      }
      // ICDM-2188

      // Open point text
      opPoints.append(count)
          .append(".\n").append(getDataProvider().getApicDataProvider()
              .getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_OPEN_POINTS))
          .append(" : ").append(openPoint.getOpenPointsUIString()).append('\n');

      // Measures(optional)
      if (showMeasures()) {
        opPoints.append(getDataProvider().getApicDataProvider().getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP,
            ApicConstants.KEY_OPL_MEASURE)).append(" : ").append(openPoint.getMeasure()).append('\n');
      }

      // Responsible (optional)
      if (showResponsible()) {
        opPoints
            .append(getDataProvider().getApicDataProvider().getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP,
                ApicConstants.KEY_OPL_RESPONSIBLE))
            .append(" : ").append(openPoint.getResponsibleUIString()).append('\n');
      }

      // Completion date(optional)
      if (showCompletionDate()) {
        opPoints.append(getDataProvider().getApicDataProvider().getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP,
            ApicConstants.KEY_OPL_DATE)).append(" : ").append(openPoint.getCompletionDateUIString()).append('\n');
      }

      // Status
      opPoints.append(getDataProvider().getApicDataProvider().getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP,
          ApicConstants.KEY_OPL_STATUS)).append(" : ").append(openPoint.getResultUIString()).append('\n');

      count++;
    }
    return count;
  }

  /**
   * @return a set of links associated with this answer
   */
  public Set<Link> getLinks() {
    return (CommonUtils.isNotNull(getID()) && !isHeading()) ? getApicDataProvider().getLinks(this) : null;
  }

  /**
   * @return links display string . Only the first link is returned
   */
  public String getLinkUIString() {
    String links = "";
    if (isHeading()) {
      return links;
    }
    if (showLinks()) {
      Set<Link> linksSet = getLinks();
      if (CommonUtils.isNotNull(linksSet)) {
        Iterator<Link> iterator = linksSet.iterator();
        if (iterator.hasNext()) {
          links = iterator.next().getLink();
        }
      }
    }
    else {
      links = NOT_APPL;
    }
    return links;
  }

  /**
   * @return Remarks answer
   */
  public String getRemark() {
    if (isHeading()) {
      return "";
    }
    return CommonUtils.isNotNull(getID()) ? getEntityProvider().getDbReviewQnaireAnswer(getID()).getRemark() : "";
  }

  /**
   * @return Remarks display string
   */
  public String getRemarksUIString() {
    if (isHeading()) {
      return "";
    }
    if (showRemarks()) {
      return getRemark();
    }
    return NOT_APPL;
  }

  /**
   * @return Result answer
   */
  public String getResult() {
    if (isHeading()) {
      return "";
    }
    return CommonUtils.isNotNull(getID()) ? getEntityProvider().getDbReviewQnaireAnswer(getID()).getResult() : "";
  }

  @Override
  public boolean isModifiable() {
    return this.quesResponse.isModifiable(true);
  }

  /**
   * @return Result display string
   */
  public String getResultUIString() {
    String resultUIString = NOT_APPL;
    if (isHeading()) {
      resultUIString = "";
    }
    if (showResult()) {
      final QS_RESULT_TYPE qsPositiveRes = QS_RESULT_TYPE.getTypeByDbCode(getQuestion().getPositiveResult());
      if (qsPositiveRes == QS_RESULT_TYPE.YES) {
        resultUIString = QUESTION_RESP_RESULT_YES.getType(getResult()).getUiType();

      }
      else if (qsPositiveRes == QS_RESULT_TYPE.NO) {
        resultUIString = QUESTION_RESP_RESULT_NO.getType(getResult()).getUiType();
      }
      else {
        resultUIString = QUESTION_RESP_RESULT_FINISHED.getType(getResult()).getUiType();
      }

    }
    return resultUIString;
  }

  /**
   * @return true if result is finished
   */
  public boolean isResultFinished() {
    if (isHeading()) {
      return false;
    }
    if (QUESTION_RESP_RESULT.getType(getResult()).equals(QUESTION_RESP_RESULT.FINISHED)) {
      return true;
    }
    return false;
  }

  /**
   * @return true if result is not finished
   */
  public boolean isResultNotFinished() {
    if (isHeading()) {
      return false;
    }
    if (QUESTION_RESP_RESULT.getType(getResult()) == QUESTION_RESP_RESULT.NOT_FINISHED) {
      return true;
    }
    return false;
  }

  /**
   * @return true if result is to be done
   */
  public boolean isResultToBeDone() {
    if (isHeading()) {
      return false;
    }
    if (QUESTION_RESP_RESULT.getType(getResult()) == QUESTION_RESP_RESULT.TO_BE_DONE) {
      return true;
    }
    return false;
  }

  /**
   * @return Series
   */
  public String getSeries() {
    if (isHeading()) {
      return "";
    }
    return CommonUtils.isNotNull(getID()) ? getEntityProvider().getDbReviewQnaireAnswer(getID()).getSeries() : "";
  }


  /**
   * @return true is series
   */
  public boolean isSeries() {
    return CommonUtils.getBooleanType(getSeries());
  }

  /**
   * @return is series display string
   */
  public String getIsSeriesUIString() {
    if (isHeading()) {
      return null;
    }
    if (showSeriesMaturity()) {
      return QUESTION_RESP_SERIES_MEASURE.getType(getSeries()).getUiType();
    }
    return NOT_APPL;
  }


  /**
   * @return true is measurement
   */
  public boolean isMeasurement() {
    return CommonUtils.getBooleanType(getMeasurement());
  }

  /**
   * @return if measurement display string
   */
  public String getIsMeasurementUIString() {
    if (isHeading()) {
      return null;
    }
    if (showMeasurement()) {
      return QUESTION_RESP_SERIES_MEASURE.getType(getMeasurement()).getUiType();
    }
    return NOT_APPL;
  }

  /**
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public String getMeasurement() {
    if (isHeading()) {
      return null;
    }
    return CommonUtils.isNotNull(getID()) ? getEntityProvider().getDbReviewQnaireAnswer(getID()).getMeasurement()
        : null;
  }


  /**
   * Sets the ID of this {@link ReviewQnaireAnswer}
   *
   * @param revQnaireAnsID Primary Key ID
   */
  void setID(final Long revQnaireAnsID) {
    this.revQnaireAnsObjID = revQnaireAnsID;
    if (null != revQnaireAnsID) {
      getDataCache().getReviewQnaireAnsMap().put(revQnaireAnsID, this);
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getID() {
    return this.revQnaireAnsObjID;
  }

  /**
   * @return true, if response object is available in database, false if this object is a dummy object for the question
   *         object
   */
  public boolean isFilled() {
    return getID() != null;
  }

  /**
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public boolean showSeriesMaturity() {
    return !isHeading() &&
        CommonUtils.getBooleanType(this.question.getQuestionnaireVersion().getSeriesRelevantFlag()) &&
        ((this.question.getSeries() == QUESTION_CONFIG_TYPE.MANDATORY) ||
            (this.question.getSeries() == QUESTION_CONFIG_TYPE.OPTIONAL));
  }

  /**
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public boolean showMeasurement() {
    return !isHeading() &&
        CommonUtils.getBooleanType(this.question.getQuestionnaireVersion().getMeasurementRelevantFlag()) &&
        ((this.question.getMeasurement() == QUESTION_CONFIG_TYPE.MANDATORY) ||
            (this.question.getMeasurement() == QUESTION_CONFIG_TYPE.OPTIONAL));
  }

  /**
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public boolean showLinks() {
    return !isHeading() && CommonUtils.getBooleanType(this.question.getQuestionnaireVersion().getLinkRelevantFlag()) &&
        ((this.question.getLink() == QUESTION_CONFIG_TYPE.MANDATORY) ||
            (this.question.getLink() == QUESTION_CONFIG_TYPE.OPTIONAL));
  }

  /**
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public boolean showOpenPoints() {
    return !isHeading() &&
        CommonUtils.getBooleanType(this.question.getQuestionnaireVersion().getOpenPointsRelevantFlag()) &&
        ((this.question.getOpenPoints() == QUESTION_CONFIG_TYPE.MANDATORY) ||
            (this.question.getOpenPoints() == QUESTION_CONFIG_TYPE.OPTIONAL));
  }

  /**
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public boolean showMeasures() {
    return !isHeading() &&
        CommonUtils.getBooleanType(this.question.getQuestionnaireVersion().getMeasureRelaventFlag()) &&
        ((this.question.getMeasure() == QUESTION_CONFIG_TYPE.MANDATORY) ||
            (this.question.getMeasure() == QUESTION_CONFIG_TYPE.OPTIONAL));
  }

  /**
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public boolean showCompletionDate() {
    return !isHeading() &&
        CommonUtils.getBooleanType(this.question.getQuestionnaireVersion().getCompletionDateRelaventFlag()) &&
        ((this.question.getCompletionDate() == QUESTION_CONFIG_TYPE.MANDATORY) ||
            (this.question.getCompletionDate() == QUESTION_CONFIG_TYPE.OPTIONAL));
  }

  /**
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public boolean showResponsible() {
    return !isHeading() &&
        CommonUtils.getBooleanType(this.question.getQuestionnaireVersion().getResponsibleRelaventFlag()) &&
        ((this.question.getResponsible() == QUESTION_CONFIG_TYPE.MANDATORY) ||
            (this.question.getResponsible() == QUESTION_CONFIG_TYPE.OPTIONAL));
  }

  /**
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public boolean showRemarks() {
    return !isHeading() &&
        CommonUtils.getBooleanType(this.question.getQuestionnaireVersion().getRemarkRelevantFlag()) &&
        ((this.question.getRemark() == QUESTION_CONFIG_TYPE.MANDATORY) ||
            (this.question.getRemark() == QUESTION_CONFIG_TYPE.OPTIONAL));

  }

  /**
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public boolean showResult() {
    return !isHeading() &&
        CommonUtils.getBooleanType(this.question.getQuestionnaireVersion().getResultRelevantFlag()) &&
        ((this.question.getResult() == QUESTION_CONFIG_TYPE.MANDATORY) ||
            (this.question.getResult() == QUESTION_CONFIG_TYPE.OPTIONAL));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final ReviewQnaireAnswer rvwAns) {
    // ICDM-2155
    // Uses similiar comparsion as questions
    // Cannot reuse the methods, since response contains questions from general questionnaire also
    int compResult = 0;
    if (null != rvwAns) {
      compResult = ApicUtil.compare(getPaddedQuestionNumber(), rvwAns.getPaddedQuestionNumber());
      if (compResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
        return ApicUtil.compare(getID(), rvwAns.getID());
      }
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
   * @return the question number, including the parent's number, with '0' padded at each level to the width of 9
   */
  // ICDM-2155
  private String getPaddedQuestionNumber() {
    return getPaddedQuestionNumber(this);
  }

  /**
   * Find the padded question number for the given question
   *
   * @param question for which the padding to be done
   * @return the question number with '0' padded at each level to the width of 9
   */
  // ICDM-2155
  private String getPaddedQuestionNumber(final ReviewQnaireAnswer rvwAns) {
    String padQn = String.format("%09d", rvwAns.getQNum());

    ReviewQnaireAnswer parent = rvwAns.getParentQuestion();
    if (null == parent) {
      return padQn;
    }

    return getPaddedQuestionNumber(parent) + "." + padQn;

  }

  /**
   * @return Question Number of this question without the parent's number
   */
  protected long getQNum() {
    return this.question.getQNum();
  }

  /**
   * @return the quesResponse
   */
  public QuestionnaireResponse getQuesResponse() {
    return this.quesResponse;
  }

  /**
   * @return question number of this question
   */
  public String getQuestionNumberWithName() {
    return getQuestionNumber() + "  " + getName();
  }

  /**
   * @return question number of this question
   */
  public String getQuestionNumber() {
    String prefix = this.question.isGeneralType() ? "G" : "";
    return prefix + this.question.getQuestionNumber();
  }

  // ICDM-1987
  /**
   * Compare question response instances using sort columns
   *
   * @param rvwAns other ReviewQnaireAnswer instance
   * @param sortColumn sort column
   * @return -1/0/1 as standard <code>compareTo()</code> response
   */
  public int compareTo(final ReviewQnaireAnswer rvwAns, final SortColumns sortColumn) {
    int compareResult;

    switch (sortColumn) {
      case SORT_QUES_NAME:
        // comparing the question names
        compareResult = ApicUtil.compare(getName(), rvwAns.getName());
        break;

      case SORT_QUES_HINT:
        // comparing the question hint
        compareResult = ApicUtil.compare(getDescription(), rvwAns.getDescription());
        break;
      case SORT_MEASURABLE:
        // comparing the measurable

        compareResult = ApicUtil.compare(getIsMeasurementUIString(), rvwAns.getIsMeasurementUIString());

        break;
      case SORT_SERIES:
        // comparing the series

        compareResult = ApicUtil.compare(getIsSeriesUIString(), rvwAns.getIsSeriesUIString());

        break;
      case SORT_RESULT:
        // comparing the result

        compareResult = ApicUtil.compare(getResultUIString(), rvwAns.getResultUIString());

        break;
      case SORT_LINK:
        // comparing the link
        compareResult = ApicUtil.compare(getLinkUIString(), rvwAns.getLinkUIString());
        break;
      case SORT_REMARK:
        // compare the remark
        compareResult = ApicUtil.compare(getRemarksUIString(), rvwAns.getRemarksUIString());
        break;
      case SORT_OP:
        // compare the open points
        compareResult = ApicUtil.compare(getOpenPointsUIString(), rvwAns.getOpenPointsUIString());
        break;
      case SORT_QUES_NUMBER:
      default:
        // Compare q number
        compareResult = compareTo(rvwAns);
        break;
    }

    // additional compare if both the values are same
    if (compareResult == 0) {
      // compare result is equal, compare the q number
      compareResult = compareTo(rvwAns);
    }

    return compareResult;
  }

  /**
   * @return all the immediate children
   */
  public SortedSet<ReviewQnaireAnswer> getChildren() {
    SortedSet<ReviewQnaireAnswer> allChildrenSet = new TreeSet<>();
    for (Question ques : getQuestion().getChildQuestions()) {
      ReviewQnaireAnswer answer = getQuesResponse().getReviewQnaireAnswer(ques);
      if (answer != null) {
        allChildrenSet.add(answer);
      }
    }
    return allChildrenSet;
  }

  /**
   * Get the heading type question responses at the immediate child level
   *
   * @return sorted set of child headings as ReviewQnaireAnswer
   */
  public SortedSet<ReviewQnaireAnswer> getChildHeadings() {
    SortedSet<ReviewQnaireAnswer> reviewQnaireAnswerSet = new TreeSet<>();
    for (Question ques : getQuestion().getChildQuestions()) {
      if (ques.isHeading()) {
        ReviewQnaireAnswer answer = getQuesResponse().getReviewQnaireAnswer(ques);
        // If the question is a valid child for this response question,
        // then the object is available in Qestionnaire response's question response map.
        // The check is done this way instead of directly verifying question's validity to
        // avoid duplicate hierarchy checks
        if (answer != null) {
          reviewQnaireAnswerSet.add(answer);
        }
      }
    }
    return reviewQnaireAnswerSet;
  }

  /**
   * Get the question responses at the immediate child level
   *
   * @return sorted set of children as ReviewQnaireAnswer
   */
  public SortedSet<ReviewQnaireAnswer> getChildQuestions() {
    SortedSet<ReviewQnaireAnswer> reviewQnaireAnswerSet = new TreeSet<>();
    for (Question ques : getQuestion().getChildQuestions()) {
      ReviewQnaireAnswer answer = getQuesResponse().getReviewQnaireAnswer(ques);
      // If the question is a valid child for this response question,
      // then the object is available in Qestionnaire response's question response map.
      // The check is done this way instead of directly verifying question's validity to
      // avoid duplicate hierarchy checks
      if (answer != null) {
        reviewQnaireAnswerSet.add(answer);
      }
    }
    return reviewQnaireAnswerSet;
  }


  /**
   * @return true if the question is visible
   */
  public boolean isQuestionVisible() {
    // If not a Question then return true
    if (this.question != null) {
      // get the parent question of the current question
      Question parentQuestion = this.question.getParentQuestion();
      Question currQues = this.question;
      // If parent question is null then goto current Question
      while (parentQuestion != null) {
        // Get the parent question dep
        Question dependentQuestionForPar = parentQuestion.getDependentQuestion();
        // If the parent question dep is not matching return false
        if (!checkQuesVisible(dependentQuestionForPar, parentQuestion)) {
          return false;
        }
        // Now get the parent of the parent
        parentQuestion = parentQuestion.getParentQuestion();
      }
      // check for current question dep.
      Question dependentQuestion = this.question.getDependentQuestion();

      while (dependentQuestion != null) {
        if (!checkQuesVisible(dependentQuestion, currQues)) {
          return false;
        }
        dependentQuestion = dependentQuestion.getDependentQuestion();
        currQues = dependentQuestion;
      }
    }
    return true;
  }

  /**
   * @param dependentQuestion
   */
  private boolean checkQuesVisible(final Question dependentQuestion, final Question currQues) {
    // If the dep question is null then return true
    if (dependentQuestion != null) {
      ReviewQnaireAnswer reviewQnaireAnswer = getQuesResponse().getReviewQnaireAnswer(dependentQuestion);
      if (reviewQnaireAnswer == null) {
        return false;
      }
      // Answer to the question same as dependency constraint
      return QUESTION_RESP_RESULT_YES.getTypeForResponse(currQues.getDepQuesResponse()) == QUESTION_RESP_RESULT_YES
          .getTypeForResponse(reviewQnaireAnswer.getResult());
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }


  /**
   * Get the parent question object. Return could be a question/heading or a dummy questionnaire version object
   *
   * @return parent question as ReviewQnaireAnswer
   */
  public ReviewQnaireAnswer getParentQuestion() {
    ReviewQnaireAnswer parent;
    Question qstn = this.question.getParentQuestion();
    if (qstn == null) {
      // Top Level dummy node, created for questionnaire version
      parent = this.quesResponse.getReviewQnaireAnswer(this.question.getQuestionnaireVersion());
    }
    else {
      parent = this.quesResponse.getReviewQnaireAnswer(qstn);
    }
    return parent;
  }

  /**
   * Search through the question tree below and find the first question response of type 'question'
   *
   * @return first 'question' type ReviewQnaireAnswer
   */
  public ReviewQnaireAnswer getFirstQuestionResponse() {
    return getDeepChildQuestionInternal(this);
  }

  /**
   * Find the first child question, recursively
   *
   * @param rvwQuestion ReviewQnaireAnswer
   * @return ReviewQnaireAnswer
   */
  private ReviewQnaireAnswer getDeepChildQuestionInternal(final ReviewQnaireAnswer rvwQuestion) {
    if (rvwQuestion.isHeading()) {
      SortedSet<ReviewQnaireAnswer> children = rvwQuestion.getChildren();
      if (CommonUtils.isNotEmpty(children)) {
        // Now the code has to handle invisible questions. So do not show first Question.
        for (ReviewQnaireAnswer reviewQnaireAnswer : children) {
          if (reviewQnaireAnswer.isQuestionVisible()) {
            return getDeepChildQuestionInternal(reviewQnaireAnswer);
          }
        }
      }
    }
    // If this is a question, then return directly
    return rvwQuestion;
  }

  /**
   * @return true, if this is a heading
   */
  public boolean isHeading() {
    return this.question.isHeading();
  }

  /**
   * Get the open points against this question response
   *
   * @return the openPointsList
   */
  public List<QnaireAnsOpenPoint> getOpenPointsList() {
    if (isHeading()) {
      return this.openPointsList;
    }
    Set<TRvwQnaireAnswerOpl> openPointsSet;
    this.openPointsList.clear();
    if (CommonUtils.isNotNull(getID())) {
      openPointsSet = getEntityProvider().getDbReviewQnaireAnswer(getID()).getTQnaireAnsOpenPoints();
      if (openPointsSet != null) {
        fillOPList(openPointsSet);
      }
    }

    return this.openPointsList;
  }

  /**
   * Fill open points
   *
   * @param openPointsSet destionation
   */
  private void fillOPList(final Set<TRvwQnaireAnswerOpl> openPointsSet) {
    QnaireAnsOpenPoint openPoint;
    for (TRvwQnaireAnswerOpl dbOpenPoint : openPointsSet) {
      openPoint = getDataCache().getQnaireAnsOpenPointMap().get(dbOpenPoint.getOpenPointsId());
      if (openPoint == null) {
        openPoint = new QnaireAnsOpenPoint(getDataCache().getDataProvider(), this, dbOpenPoint.getOpenPointsId());

      }

      this.openPointsList.add(openPoint);

    }
  }
}
