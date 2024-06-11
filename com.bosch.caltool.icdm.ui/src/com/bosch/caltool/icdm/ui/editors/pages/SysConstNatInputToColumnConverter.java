/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.pages;

import com.bosch.caltool.icdm.model.a2l.A2LSystemConstantValues;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;


/**
 * @author jvi6cob
 */
public class SysConstNatInputToColumnConverter extends AbstractNatInputToColumnConverter {

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getColumnValue(final Object evaluateObj, final int colIndex) {

    Object returnObj = null;
    // same row as last, use its object as it's way faster than a list_get(row)
    // If object is instance of a2l system constant value
    if (evaluateObj instanceof A2LSystemConstantValues) {
      final A2LSystemConstantValues a21SysConstValues = (A2LSystemConstantValues) evaluateObj;
      // Get the column index
      if (colIndex == 0) {
        // System constant name
        returnObj = a21SysConstValues.getSysconName();
      }
      else if (colIndex == 1) {
        // Long name
        returnObj = a21SysConstValues.getSysconLongName();
      }
      else if (colIndex == 2) {
        // Value
        returnObj = a21SysConstValues.getValue();
      }
      else if (colIndex == 3) {
        // Description
        returnObj = a21SysConstValues.getValueDescription();
      }
    }
    return returnObj;
  }

}
