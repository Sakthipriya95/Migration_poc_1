package com.bosch.caltool.cdr.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.table.filters.BasicObjectViewerTypeFilter;
import com.bosch.caltool.icdm.model.cdr.qnaire.Questionnaire;

/**
 * RuleSetFilter.java This class handles the filters for the Rule Set Selection in dialog
 */
public class QuestionarieFilter extends BasicObjectViewerTypeFilter<Questionnaire> {

  // Filter for Questionare

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    boolean result = super.selectElement(element);
    if (element instanceof Questionnaire) {
      Questionnaire questionnaire = (Questionnaire) element;
      // check for division name
      if (matchText(questionnaire.getName())) {
        result = true;
      }
    }
    return result;
  }

}