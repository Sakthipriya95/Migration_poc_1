/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.table.filters;

import com.bosch.caltool.icdm.client.bo.a2l.A2LFilterBaseComponents;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;


/**
 * @author bru2cob
 */
// ICDM-1011
public class CDFXportBCFilter extends AbstractViewerFilter {

  /**
   * Filters based on the element
   */
  @Override
  protected boolean selectElement(final Object element) {

    boolean returnVal = false;
    if (element instanceof A2LFilterBaseComponents) {
      final A2LFilterBaseComponents a2lBC = (A2LFilterBaseComponents) element;
      // match filter text with BC name
      if (matchText(a2lBC.getBcName())) {
        returnVal = true;
      }
      // match filter text with BC version
      if (matchText(a2lBC.getBcVersion())) {
        returnVal = true;
      }
    }
    return returnVal;

  }
}
