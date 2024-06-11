/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;

/**
 * @author bru2cob
 */
public class PverSelectionFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    // returns true if the typed value matches the PVER name
    if (element instanceof String) {
      String selVal = (String) element;
      // name filter
      if (matchText(selVal)) {
        return true;
      }

    }
    return false;
  }

}
