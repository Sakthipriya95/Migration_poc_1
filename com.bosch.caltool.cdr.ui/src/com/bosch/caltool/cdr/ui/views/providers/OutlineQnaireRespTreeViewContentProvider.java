/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.views.providers;

import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.client.bo.qnaire.QnaireRespEditorDataHandler;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;


/**
 * Content provider for questionnaire response outline tree view
 *
 * @author svj7cob
 */
// iCDM-1991
public class OutlineQnaireRespTreeViewContentProvider implements ITreeContentProvider {


  /**
   * Questionnaire Response object
   */
  private final QnaireRespEditorDataHandler respDataHandler;

  /**
   * If true, only headings are displayed
   */
  private final boolean onlyHeadings;


  /**
   * Intialize the Outline Questionnaire Response Tree view content provider
   *
   * @param respDataHandler QnaireRespEditorDataHandler
   * @param showListPag showListPag
   */
  public OutlineQnaireRespTreeViewContentProvider(final QnaireRespEditorDataHandler respDataHandler,
      final boolean showListPag) {
    this.respDataHandler = respDataHandler;
    this.onlyHeadings = showListPag;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void inputChanged(final Viewer arg0, final Object arg1, final Object arg2) {
    // Not applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object[] getElements(final Object inputElement) {
    if (inputElement instanceof String) {
      return new Object[] { this.respDataHandler.getQuesResponse() };
    }
    return new Object[0];
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object[] getChildren(final Object parentElement) {
    if (parentElement instanceof RvwQnaireResponse) {
      SortedSet<RvwQnaireAnswer> quesTopLevelQuesSet = new TreeSet<>(this.respDataHandler.getRvwQuestionAnserComp());
      quesTopLevelQuesSet.addAll(this.respDataHandler.getFirstLevelHeading());
      return quesTopLevelQuesSet.toArray();
    }
    if (parentElement instanceof RvwQnaireAnswer) {
      SortedSet<RvwQnaireAnswer> visibleQues = new TreeSet<>(this.respDataHandler.getRvwQuestionAnserComp());
      SortedSet<RvwQnaireAnswer> visibleHeadings = new TreeSet<>(this.respDataHandler.getRvwQuestionAnserComp());

      RvwQnaireAnswer parentRvwQnaireAns = (RvwQnaireAnswer) parentElement;
      SortedSet<RvwQnaireAnswer> children = this.respDataHandler.getChildQuestions(parentRvwQnaireAns);
      for (RvwQnaireAnswer reviewQnaireAnswer : children) {
        if (this.respDataHandler.isQuestionVisible(reviewQnaireAnswer)) {
          visibleQues.add(reviewQnaireAnswer);
        }
      }
      for (RvwQnaireAnswer reviewQnaireAnswer : this.respDataHandler.getChildHeadings(parentRvwQnaireAns)) {
        if (this.respDataHandler.isQuestionVisible(reviewQnaireAnswer)) {
          visibleHeadings.add(reviewQnaireAnswer);
        }
      }
      return this.onlyHeadings ? visibleHeadings.toArray() : visibleQues.toArray();
    }
    return new Object[0];
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getParent(final Object element) {
    // Not required
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasChildren(final Object element) {
    // Since all nodes are displayed in the outline view, the method can explicitly return true
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    // Not required
  }
}
