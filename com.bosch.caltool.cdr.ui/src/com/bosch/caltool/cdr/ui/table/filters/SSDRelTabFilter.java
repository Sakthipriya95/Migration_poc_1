/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.ssd.SSDReleaseIcdmModel;

/**
 * @author bru2cob
 */
public class SSDRelTabFilter extends AbstractViewerFilter {


  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof SSDReleaseIcdmModel) {
      SSDReleaseIcdmModel selWp = (SSDReleaseIcdmModel) element;
      // fc2wp name filter
      if (matchText(selWp.getReleaseDesc())) {
        return true;
      }
      // division name filter
      if (matchText(selWp.getReleaseDate())) {
        return true;
      }
      // release filter
      if (matchText(selWp.getRelease())) {
        return true;
      }
    }
    // Default return false
    return false;
  }


}
