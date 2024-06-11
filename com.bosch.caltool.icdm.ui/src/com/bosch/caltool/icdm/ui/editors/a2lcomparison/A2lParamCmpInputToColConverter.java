/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.a2lcomparison;

import com.bosch.caltool.icdm.client.bo.a2l.A2lParamCompareRowObject;
import com.bosch.caltool.icdm.ui.util.IUIConstants;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;

/**
 * @author bru2cob
 */
class A2lParamCmpInputToColConverter extends AbstractNatInputToColumnConverter {

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getColumnValue(final Object evaluateObj, final int colIndex) {
    Object result = null;
    if (evaluateObj instanceof A2lParamCompareRowObject) {
      A2lParamCompareRowObject compareRowObject = (A2lParamCompareRowObject) evaluateObj;
      // Switch case based on Col index
      switch (colIndex) {
        // If the col index is 0
        case IUIConstants.A2LCMP_ICON_COL_INDEX:
          break;
        case IUIConstants.A2LCMP_PARAM_COL_INDEX:
          // Get the param name from Row object
          result = compareRowObject.getParamName();
          break;
        case IUIConstants.A2LCMP_DIFF_COL_INDEX:
          // Get the computed diff value from Row object
          result = compareRowObject.isComputedDiff();
          break;
        default:
          // Get the column data from Row object
          result = compareRowObject.getColumnData(colIndex);
          break;
      }
    }
    return result;
  }

}
