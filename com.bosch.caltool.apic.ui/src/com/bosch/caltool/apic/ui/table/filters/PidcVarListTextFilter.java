/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;

/**
 * @author apj4cob
 */
public class PidcVarListTextFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    boolean result = false;
    if (element instanceof String) {
      // match string
      result = matchText((String) element);
    }
    return result;
  }
}
