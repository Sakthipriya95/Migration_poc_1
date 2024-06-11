/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.table.filters;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;

import com.bosch.caltool.icdm.client.bo.qnaire.QnaireDefBO;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireVersion;
import com.bosch.caltool.nattable.CustomFilterGridLayer;

import ca.odell.glazedlists.matchers.Matcher;


/**
 * Outline filter for questionnaire
 *
 * @author dmo5cob
 */
public class QuestionnaireOutlineNatFilter {

  /**
   * CustomFilterGridLayer<Question> instance
   */
  private final CustomFilterGridLayer<Question> filterGridLayer;
  /**
   * Selected question heading
   */
  private Question selectedQuestionHeading;

  QnaireDefBO qnaireDefEditorDataHandler;

  /**
   * @param filterGridLayer CustomFilterGridLayer<Question> instance
   * @param qnaireDefBo QnaireDefBo
   */
  public QuestionnaireOutlineNatFilter(final CustomFilterGridLayer<Question> filterGridLayer,
      final QnaireDefBO qnaireDefBo) {
    this.filterGridLayer = filterGridLayer;
    this.qnaireDefEditorDataHandler = qnaireDefBo;
  }

  /**
   * @return .
   */
  public Matcher<Question> getOutlineMatcher() {

    return new QuestionMatcher<>();
  }

  private class QuestionMatcher<E> implements Matcher<E> {

    /** {@inheritDoc} */
    @Override
    public boolean matches(final E element) {
      if (element instanceof Question) {
        final Question question = (Question) element;
        if (QuestionnaireOutlineNatFilter.this.selectedQuestionHeading.getId().equals(question.getId()) ||
            checkParent(question)) {
          return true;
        }
      }
      // for root node selection
      return false;
    }
  }

  /**
   * @param selection
   */
  public void questionOutlineSelectionListener(final ISelection selection) {
    if ((selection != null) && !selection.isEmpty() && (selection instanceof IStructuredSelection)) {
      final Object first = ((IStructuredSelection) selection).getFirstElement();

      // Check if selected node is a Question
      if (first instanceof Question) {
        Question ques = (Question) first;
        this.selectedQuestionHeading = ques;
        this.filterGridLayer.getFilterStrategy().applyOutlineFilterInAllColumns(false);
        this.filterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(this.filterGridLayer.getSortableColumnHeaderLayer()));
      }
      // check if selected node is questionnaire version
      else if (first instanceof QuestionnaireVersion) {
        this.filterGridLayer.getFilterStrategy().applyOutlineFilterInAllColumns(true);
        this.filterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(this.filterGridLayer.getSortableColumnHeaderLayer()));
      }
    }

  }

  /**
   * Checks parent node value for match
   *
   * @param question
   * @return This method iterates all the parent questions and does a match
   */
  public boolean checkParent(final Question question) {
    if ((question.getParentQId() != null) &&
        (question.getParentQId().equals(QuestionnaireOutlineNatFilter.this.selectedQuestionHeading.getId()) ||
            ((question.getParentQId() != null) && checkParent(
                this.qnaireDefEditorDataHandler.getQnaireDefModel().getQuestionMap().get(question.getParentQId()))))) {
      return true;
    }
    return false;

  }


}
