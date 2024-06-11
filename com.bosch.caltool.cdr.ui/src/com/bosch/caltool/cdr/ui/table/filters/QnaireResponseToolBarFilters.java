/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.table.filters;

import com.bosch.caltool.icdm.client.bo.qnaire.QnaireRespEditorDataHandler;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswerOpl;

import ca.odell.glazedlists.matchers.Matcher;


// ICDM-1987
/**
 * @author bru2cob
 */
public class QnaireResponseToolBarFilters extends AbstractViewerFilter {

  /**
   * Heading flag initially True since the tool bar action is checked
   */
  private boolean headingFlag = true;
  /**
   * Questions flag initially True since the tool bar action is checked
   */
  private boolean questionsFlag = true;
  /**
   * Deleted flag initially True since the tool bar action is checked
   */
  private boolean postiveResultsFlag = true;
  /**
   * Non Deleted flag initially True since the tool bar action is checked
   */
  private boolean negativeResultsFlag = true;
  private boolean neutralResultsFlag = true;
  /**
   *
   */
  private final QnaireRespEditorDataHandler dataHandler;


  /**
   * @param dataHandler QnaireRespEditorDataHandler
   */

  public QnaireResponseToolBarFilters(final QnaireRespEditorDataHandler dataHandler) {
    this.dataHandler = dataHandler;
  }

  /**
   * @param headingFlag the headingFlag to set
   */
  public void setHeadingFlag(final boolean headingFlag) {
    this.headingFlag = headingFlag;
  }


  /**
   * @param questionsFlag the questionsFlag to set
   */
  public void setQuestionsFlag(final boolean questionsFlag) {
    this.questionsFlag = questionsFlag;
  }


  /**
   * @param postiveResultsFlag the postiveResultsFlag to set
   */
  public void setPostiveResultsFlag(final boolean postiveResultsFlag) {
    this.postiveResultsFlag = postiveResultsFlag;
  }


  /**
   * @param negativeResultsFlag the negativeResultsFlag to set
   */
  public void setNegativeResultsFlag(final boolean negativeResultsFlag) {
    this.negativeResultsFlag = negativeResultsFlag;
  }

  /**
   * @param neutralResultsFlag the neutralResultsFlag to set
   */
  public void setNeutralResultsFlag(final boolean neutralResultsFlag) {
    this.neutralResultsFlag = neutralResultsFlag;
  }


  /**
   * @param notAnsweredFlag the notAnsweredFlag to set
   */
  public void setNotAnsweredFlag(final boolean notAnsweredFlag) {
    this.notAnsweredFlag = notAnsweredFlag;
  }

  /**
   * Non Deleted flag initially True since the tool bar action is checked
   */
  private boolean notAnsweredFlag = true;
  /**
   * show invisible flag default to false
   */
  private boolean showInvisbleFlag;

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof RvwQnaireAnswer) {
      RvwQnaireAnswer ansObj = (RvwQnaireAnswer) element;
      if (isHeadingActionUnchecked(ansObj)) {
        return false;
      }
      if (isQuestionActionUnchecked(ansObj)) {
        return false;
      }
      if (isPositiveResultActionUnchecked(ansObj)) {
        return false;
      }
      if (isNegativeResultActionUnchecked(ansObj)) {
        return false;
      }
      if (isNeutralResultActionUnchecked(ansObj)) {
        return false;
      }
      if (isToBeDoneActionUnchecked(ansObj)) {
        return false;
      }
      if (isShowInvisibleUnchecked(ansObj)) {
        return false;
      }
    }
    else if (element instanceof RvwQnaireAnswerOpl) {
      RvwQnaireAnswerOpl ansOpl = (RvwQnaireAnswerOpl) element;
      RvwQnaireAnswer rvwQnaireAnswer = this.dataHandler.getAllRvwAnswerMap().get(ansOpl.getRvwAnswerId());
      if (isShowInvisibleUnchecked(rvwQnaireAnswer)) {
        return false;
      }
    }
    return true;
  }

  /**
   * @param ansObj
   * @return true if the Question is visible
   */
  private boolean isShowInvisibleUnchecked(final RvwQnaireAnswer rvwAns) {
    return !this.showInvisbleFlag && !this.dataHandler.isQuestionVisible(rvwAns);
  }


  /**
   * @param quesObj
   * @return
   */
  private boolean isToBeDoneActionUnchecked(final RvwQnaireAnswer rvwAns) {
    return !this.notAnsweredFlag && this.dataHandler.isResultNotAnswered(rvwAns);
  }

  /**
   * @param quesObj
   * @return
   */
  private boolean isNegativeResultActionUnchecked(final RvwQnaireAnswer rvwAns) {
    return !this.negativeResultsFlag && this.dataHandler.isResultNegative(rvwAns);
  }

  /**
   * @param quesObj
   * @return
   */
  private boolean isNeutralResultActionUnchecked(final RvwQnaireAnswer rvwAns) {
    return !this.neutralResultsFlag && this.dataHandler.isResultNeutral(rvwAns);
  }

  /**
   * @param quesObj
   * @return
   */
  private boolean isPositiveResultActionUnchecked(final RvwQnaireAnswer rvwAns) {
    return !this.postiveResultsFlag && this.dataHandler.isResultPositive(rvwAns);
  }

  /**
   * @param question
   * @return
   */
  private boolean isHeadingActionUnchecked(final RvwQnaireAnswer rvwAns) {
    return !this.headingFlag && this.dataHandler.checkHeading(rvwAns);
  }

  /**
   * @param question
   * @return
   */
  private boolean isQuestionActionUnchecked(final RvwQnaireAnswer rvwAns) {
    return !this.questionsFlag && !this.dataHandler.checkHeading(rvwAns);
  }

  /**
   * @return Matcher
   */
  @SuppressWarnings("rawtypes")
  public Matcher getToolBarMatcher() {
    return new QuestionsRespMatcher<RvwQnaireAnswer>();
  }

  private class QuestionsRespMatcher<E> implements Matcher<E> {

    /** {@inheritDoc} */
    @Override
    public boolean matches(final E element) {
      if (element instanceof RvwQnaireAnswer) {
        return selectElement(element);
      }
      return true;
    }
  }

  /**
   * @param showInvisbleFlag showInvisbleFlag
   */
  public void setShowInvisible(final boolean showInvisbleFlag) {
    this.showInvisbleFlag = showInvisbleFlag;

  }
}
