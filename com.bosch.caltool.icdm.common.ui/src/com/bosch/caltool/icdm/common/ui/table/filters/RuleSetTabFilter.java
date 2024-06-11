/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.cdr.RuleSet;


/**
 * @author rgo7cob
 */
// ICDM-1371
public class RuleSetTabFilter extends AbstractViewerFilter {

  /**
   * Filters based on the element
   */
  @Override
  protected boolean selectElement(final Object element) {

    boolean returnVal = false;
    if (element instanceof RuleSet) {
      final RuleSet ruleSet = (RuleSet) element;
      // match filter text with rule Set name
      if (matchText(ruleSet.getName())) {
        returnVal = true;
      }

    }
    return returnVal;

  }
}
