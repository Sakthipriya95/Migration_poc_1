/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.table.filters;

import com.bosch.caltool.icdm.client.bo.a2l.A2LFilterParameter;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;


/**
 * This class is to filter Parameters table in CDFX Export Wizard
 *
 * @author mkl2cob
 */
public class CDFXportParamFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {

    boolean returnVal = false;
    if (element instanceof A2LFilterParameter) {
      // only if the element is an A2LFilterParameter
      final A2LFilterParameter a2lParameter = (A2LFilterParameter) element;

      if (matchText(a2lParameter.getA2lParam().getName())) {
        // if the text matches with param name
        returnVal = true;
      }
      if (matchText(a2lParameter.getA2lParam().getUnit())) {
        // if the text matches with param unit
        returnVal = true;
      }
      if (matchText(a2lParameter.getA2lParam().getPclassString())) {
        // if the text matches with param class string
        returnVal = true;
      }
      if (matchText(a2lParameter.getMinValue())) {
        // if the text matches with param min value
        returnVal = true;
      }
      if (matchText(a2lParameter.getMaxValue())) {
        // if the text matches with param max value
        returnVal = true;
      }

    }
    return returnVal;

  }
}
