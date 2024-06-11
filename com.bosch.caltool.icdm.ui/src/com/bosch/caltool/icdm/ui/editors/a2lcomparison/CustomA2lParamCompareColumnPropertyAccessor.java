/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.a2lcomparison;

import java.util.Map;

import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;

/**
 * @author bru2cob
 */
class CustomA2lParamCompareColumnPropertyAccessor<T> implements IColumnAccessor<T> {

  Map<Integer, String> propertyToLabelMap;

  /**
   * @param propertyToLabelMap
   */
  public CustomA2lParamCompareColumnPropertyAccessor(final Map<Integer, String> propertyToLabelMap) {
    this.propertyToLabelMap = propertyToLabelMap;
  }

  /**
   * This method has been overridden so that it returns the passed row object. The above behavior is required for use of
   * custom comparators for sorting which requires the Row object to be passed without converting to a particular column
   * String value {@inheritDoc}
   */
  @Override
  public Object getDataValue(final T compareRowObject, final int columnIndex) {
    return compareRowObject;
  }


  @Override
  public void setDataValue(final T sysConstNatModel, final int columnIndex, final Object newValue) {
    // TODO:
  }

  @Override
  public int getColumnCount() {
    return CustomA2lParamCompareColumnPropertyAccessor.this.propertyToLabelMap.size();
  }

}

