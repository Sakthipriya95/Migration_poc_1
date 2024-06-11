/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.a2l.A2LSystemConstantValues;


/**
 * @author mkl2cob
 */
public class CDFXportSysCnstFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {

    boolean returnVal = false;
    if (element instanceof A2LSystemConstantValues) {
      // initialise A2LSystemConstantValues
      final A2LSystemConstantValues sysConstElement = (A2LSystemConstantValues) element;

      if (matchText(sysConstElement.getSysconName())) {
        // if the text matches with system constant name
        returnVal = true;
      }
      if (matchText(sysConstElement.getValue())) {
        // if the text matches with system constant value
        returnVal = true;
      }

    }
    return returnVal;

  }

}
