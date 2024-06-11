/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizards.pages;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;


/**
 * Class for filtering workpackage functions list
 * 
 * @author bru2cob
 */
public class FunsFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    // function names are compared
    if (element instanceof String) {
      String funcName = (String) element;
      if (matchText(funcName)) {
        return true;
      }
    }
    return false;
  }

}
