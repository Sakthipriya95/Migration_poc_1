/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.wp.WorkPkg;

/**
 * @author bru2cob
 */
public class WPTabFilter extends AbstractViewerFilter {


  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof WorkPkg) {
      WorkPkg selWp = (WorkPkg) element;
      // fc2wp name filter
      if (matchText(selWp.getName())) {
        return true;
      }
      // division name filter
      if (matchText(selWp.getDescription())) {
        return true;
      }
    }
    return false;
  }


}
