/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.ui.sorters.WorkPackageDetailsWrapper;

/**
 * @author bru2cob
 */
public class WPDivTabFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof WorkPackageDetailsWrapper) {
      WorkPackageDetailsWrapper selWp = (WorkPackageDetailsWrapper) element;
      // division name filter
      if (matchText(selWp.getDivisionName())) {
        return true;
      }
      // resource filter
      if (matchText(selWp.getResourceName())) {
        return true;
      }
      // mcr filter
      if (matchText(selWp.getMCR())) {
        return true;
      }
      // primary contact filter
      if (matchText(selWp.getPrimaryContact())) {
        return true;
      }
      // sec contact filter
      if (matchText(selWp.getSecondaryContact())) {
        return true;
      }
    }
    return false;
  }

}
