/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;


/**
 * @author bru2cob
 */
public class PverVariantFilter extends AbstractViewerFilter {


  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    // If the selected element is a string
    // pver variant
    if (element instanceof String) {
      if (matchText((String) element)) {
        return true;
      }

    }
    return false;
  }


}
