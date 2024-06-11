package com.bosch.caltool.icdm.ruleseditor.table.filters;


import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.cdr.RuleSet;

/**
 * RuleSetFilter.java This class handles the filters for the Rule Set Selection in dialog
 */
public class RuleSetFilter extends AbstractViewerFilter {

  // Iccm-1368 Navigate to project specific rules set

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof RuleSet) {
      RuleSet ruleElement = (RuleSet) element;
      // check for name
      if ((ruleElement.getName() != null) && matchText(ruleElement.getName())) {
        return true;
      }
    }
    return false;
  }

}