/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.table.filters;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;

import com.bosch.caltool.icdm.client.bo.qnaire.QnaireRespEditorDataHandler;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.nattable.CustomFilterGridLayer;

import ca.odell.glazedlists.matchers.Matcher;


/**
 * @author svj7cob
 */
public class QnaireRespOutlineNatFilter {

  /**
   * CustomFilterGridLayer<Question> instance
   */
  private final CustomFilterGridLayer<Object> filterGridLayer;

  /**
   * Selected answer node in outline view. Null if selection is root node(response node)
   */
  private RvwQnaireAnswer selAnswrNode;
  /**
   * Data Handler for Review Questionaire Response
   */
  private final QnaireRespEditorDataHandler dataHandler;

  /**
   * Instantiates a new qnaire resp outline nat filter.
   *
   * @param filterGridLayer CustomFilterGridLayer<Question> instance
   * @param editorDataHandler the editor data handler
   */
  public QnaireRespOutlineNatFilter(final CustomFilterGridLayer<Object> filterGridLayer,
      final QnaireRespEditorDataHandler editorDataHandler) {
    this.filterGridLayer = filterGridLayer;
    this.dataHandler = editorDataHandler;
  }

  /**
   * the inner class used to get the outline matcher
   *
   * @return the matched question
   */
  public Matcher<Question> getOutlineMatcher() {
    return new QuestionMatcher<>();
  }

  /**
   * the inner class to implement the matcher function
   *
   * @author svj7cob
   * @param <E>
   */
  private class QuestionMatcher<E> implements Matcher<E> {

    /** Singleton instance of TrueMatcher. */

    /** {@inheritDoc} */
    @Override
    public boolean matches(final E element) {
      boolean ret = false;

      if (element instanceof RvwQnaireAnswer) {
        // Includes questionnaire nodes, headings, questions
        final RvwQnaireAnswer rvwQnaireAns = (RvwQnaireAnswer) element;
        if (QnaireRespOutlineNatFilter.this.selAnswrNode != null) {
          ret = CommonUtils.isEqual(rvwQnaireAns, QnaireRespOutlineNatFilter.this.selAnswrNode) ||
              matchParent(rvwQnaireAns);
        }
      }
      else {
        // for root node selection
        ret = true;
      }
      return ret;
    }

    /**
     * @param selAnswrNode
     * @param curElement
     * @return
     */
    private boolean matchParent(final RvwQnaireAnswer curElement) {
      RvwQnaireAnswer parent = QnaireRespOutlineNatFilter.this.dataHandler.getParentQuestion(curElement);
      boolean ret;
      if (parent == null) {
        // Questionnaire node (dummy node)
        ret = false;
      }
      else if (CommonUtils.isEqual(QnaireRespOutlineNatFilter.this.selAnswrNode, parent)) {
        // Parent is same
        ret = true;
      }
      else {
        // Check parent of this parent
        ret = matchParent(parent);
      }
      return ret;
    }
  }

  /**
   * To apply the filter event for the given Question/Questionnaire Version/Questionnaire Response
   *
   * @param selection the selected node
   */
  public void questionOutlineSelectionListener(final ISelection selection) {
    if ((selection != null) && !selection.isEmpty() && (selection instanceof IStructuredSelection)) {

      final Object first = ((IStructuredSelection) selection).getFirstElement();

      if (first instanceof RvwQnaireAnswer) {
        this.selAnswrNode = (RvwQnaireAnswer) first;
        this.filterGridLayer.getFilterStrategy().applyOutlineFilterInAllColumns(false);
      }
      else {
        this.filterGridLayer.getFilterStrategy().applyOutlineFilterInAllColumns(true);
      }

      this.filterGridLayer.getSortableColumnHeaderLayer()
          .fireLayerEvent(new FilterAppliedEvent(this.filterGridLayer.getSortableColumnHeaderLayer()));

    }
  }

}
