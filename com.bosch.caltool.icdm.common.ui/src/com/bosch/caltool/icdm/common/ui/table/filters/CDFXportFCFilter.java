/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.table.filters;

import com.bosch.caltool.icdm.client.bo.a2l.A2LFilterFunction;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;


/**
 * This class is to filter FC table in CDFX Export Wizard
 *
 * @author dmo5cob
 */
public class CDFXportFCFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {

    boolean returnVal = false;
    // A2LFilterFunction
    if (element instanceof A2LFilterFunction) {
      final A2LFilterFunction a2lFC = (A2LFilterFunction) element;
      // name
      if (matchText(a2lFC.getName())) {
        returnVal = true;
      }
      // function version
      if (matchText(a2lFC.getFunctionVersion())) {
        returnVal = true;
      }

    }
    return returnVal;

  }
}
