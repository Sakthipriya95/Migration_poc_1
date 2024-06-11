/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.Calendar;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.exception.IcdmRuntimeException;

/**
 * Dummy question heading for questionnaire version in Questionnaire response
 *
 * @author bne4cob
 */
@Deprecated
// ICDM-2155
public class ReviewQnaireAnswerDummyQnaireVers extends ReviewQnaireAnswer {

  /**
   * Questionnaire version against which this dummy object is created.
   */
  private final QuestionnaireVersion qnaireVers;

  /**
   * @param cdrDataProvider CDRDataProvider
   * @param qnaireVers Questionnaire version
   * @param quesResponse QuestionnaireResponse
   */
  public ReviewQnaireAnswerDummyQnaireVers(final CDRDataProvider cdrDataProvider, final QuestionnaireVersion qnaireVers,
      final QuestionnaireResponse quesResponse) {

    super(cdrDataProvider, null, qnaireVers.getID(), quesResponse);
    this.qnaireVers = qnaireVers;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isHeading() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected long getQNum() {
    return this.qnaireVers.isGeneralType() ? 0 : 1;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = HASHCODE_PRIME;
    int result = super.hashCode();
    result = (prime * result) + ((this.qnaireVers == null) ? 0 : this.qnaireVers.hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (null == obj) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ReviewQnaireAnswerDummyQnaireVers other = (ReviewQnaireAnswerDummyQnaireVers) obj;
    if (this.qnaireVers == null) {
      if (other.qnaireVers != null) {
        return false;
      }
    }
    else if (!this.qnaireVers.equals(other.qnaireVers)) {
      return false;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<ReviewQnaireAnswer> getChildren() {
    // Only heading type children are available for questionnaire version dummy node
    return getChildHeadings();
  }

  /**
   * Returns all first level heading type questions below the questionnaire version
   * <p>
   * {@inheritDoc}
   */
  @Override
  public SortedSet<ReviewQnaireAnswer> getChildHeadings() {
    SortedSet<ReviewQnaireAnswer> reviewQnaireAnswerSet = new TreeSet<>();
    for (Question ques : this.qnaireVers.getFirstLevelQuestions()) {
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
   * {@inheritDoc}
   */
  @Override
  public SortedSet<ReviewQnaireAnswer> getChildQuestions() {
    // No child questions at level 1
    return new TreeSet<>();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    return this.qnaireVers.getCreatedDate();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return this.qnaireVers.getCreatedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return this.qnaireVers.getDescription();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return this.qnaireVers.getEntityType();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getID() {
    return this.qnaireVers.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    return this.qnaireVers.getModifiedDate();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return this.qnaireVers.getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return this.qnaireVers.getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReviewQnaireAnswer getParentQuestion() {
    // This is the top level question(questionnaire version)
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getQuestionNumber() {
    String qNumStr;
    if (this.qnaireVers.isGeneralType()) {
      qNumStr = " I ";
    }
    else {
      qNumStr = getQuesResponse().hasGeneralQuestions() ? " II " : " I ";
    }

    return '[' + qNumStr + ']';
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTip() {
    return this.qnaireVers.getToolTip();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return this.qnaireVers.getVersion();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isModifiable() {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isModified(final Map<String, String> oldObjDetails) {
    return false;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  void setID(final Long revQnaireAnsID) {
    throw new IcdmRuntimeException("this method cannot be called for this type");
  }

}
