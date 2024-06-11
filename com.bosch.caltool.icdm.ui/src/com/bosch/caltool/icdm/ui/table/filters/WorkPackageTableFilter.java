/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;

/**
 * @author dja7cob
 */
public class WorkPackageTableFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    // Check for element instance Type
    if (element instanceof WorkPackageDivision) {
      WorkPackageDivision wp = (WorkPackageDivision) element;
      // Filter attribute
      if (matchText(wp.getWpName()) || matchText(wp.getWpDesc()) || matchText(wp.getWpResource())) {
        return true;
      }
    }
    return false;
  }
}
