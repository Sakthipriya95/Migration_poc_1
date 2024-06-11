/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.table.filters;


import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;

import ca.odell.glazedlists.matchers.Matcher;


/**
 * @author dmo5cob This class provides toolbar filter for the Question details page
 */

public class QuestionaireEditorToolBarFilters extends AbstractViewerFilter {

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
  private boolean deletedFlag = true;
  /**
   * Non Deleted flag initially True since the tool bar action is checked
   */
  private boolean nondeletedFlag = true;

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof Question) {
      Question quesObj = (Question) element;
      if (isHeadingActionUnchecked(quesObj)) {
        return false;
      }
      if (isQuestionActionUnchecked(quesObj)) {
        return false;
      }
      if (isDeletedActionUnchecked(quesObj)) {
        return false;
      }
      if (isUnDeletedActionUnchecked(quesObj)) {
        return false;
      }
    }
    return true;
  }

  /**
   * @param question
   * @return
   */
  private boolean isHeadingActionUnchecked(final Question question) {
    return !this.headingFlag && question.getHeadingFlag();
  }

  /**
   * @param question
   * @return
   */
  private boolean isQuestionActionUnchecked(final Question question) {
    return !this.questionsFlag && !question.getHeadingFlag();
  }

  /**
   * @param question
   * @return
   */
  private boolean isDeletedActionUnchecked(final Question question) {
    return !this.deletedFlag && question.getDeletedFlag();
  }

  /**
   * @param question
   * @return
   */
  private boolean isUnDeletedActionUnchecked(final Question question) {
    return !this.nondeletedFlag && !question.getDeletedFlag();
  }

  /**
   * @return Matcher
   */
  @SuppressWarnings("rawtypes")
  public Matcher getToolBarMatcher() {
    return new QuestionsMatcher<Question>();
  }

  private class QuestionsMatcher<E> implements Matcher<E> {

    /** {@inheritDoc} */
    @Override
    public boolean matches(final E element) {
      if (element instanceof Question) {
        return selectElement(element);
      }
      return true;
    }
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
   * @param deletedFlag the deletedFlag to set
   */
  public void setDeletedFlag(final boolean deletedFlag) {
    this.deletedFlag = deletedFlag;
  }


  /**
   * @param nondeletedFlag the nondeletedFlag to set
   */
  public void setNondeletedFlag(final boolean nondeletedFlag) {
    this.nondeletedFlag = nondeletedFlag;
  }
}
