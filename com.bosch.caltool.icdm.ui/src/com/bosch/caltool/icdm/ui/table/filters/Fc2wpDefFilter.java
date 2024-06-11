/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPDef;

/**
 * @author bru2cob
 *
 */
public class Fc2wpDefFilter extends AbstractViewerFilter {


  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof FC2WPDef) {
      FC2WPDef fc2wp = (FC2WPDef) element;
      // fc2wp name filter
      if (matchText(fc2wp.getName())) {
        return true;
      }
      // division name filter
      if (matchText(fc2wp.getDivisionName())) {
        return true;
      }
    }
    return false;
  }


}