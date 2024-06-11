/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.pages;

import com.bosch.caltool.icdm.ui.editors.CompareFC2WPRowObject;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;

/**
 * Class which provides input to fc2wp editor
 *
 * @author gge6cob
 */
public class FC2WPNatInputToColumnConverter extends AbstractNatInputToColumnConverter {


  /**
   * {@inheritDoc}
   */
  @Override
  public Object getColumnValue(final Object evaluateObj, final int colIndex) {
    Object result = null;
    if (evaluateObj instanceof CompareFC2WPRowObject) {
      result = getFC2WPData((CompareFC2WPRowObject) evaluateObj, colIndex);
    }
    return result;
  }

  /**
   * @param evaluateObj
   * @param colIndex
   * @return
   */
  private Object getFC2WPData(final CompareFC2WPRowObject compareRowObject, final int colIndex) {
    Object result;
    switch (colIndex) {
      case 0:
        result = compareRowObject.getFuncName();
        break;
      case 1:
        result = compareRowObject.getFuncDesc();
        break;
      // for other cols get the data from data map based on index
      default:
        result = compareRowObject.getColumnDataMapper().getColumnData(colIndex);
        break;
    }
    return result;
  }


}
