/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.a2lcomparison;

import java.util.Map;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

/**
 * @author bru2cob
 */
class CustomA2lParamCmpHeaderDataProvider implements IDataProvider {

  Map<Integer, String> propertyToLabelMap;

  /**
   * @param propertyToLabelMap
   */
  public CustomA2lParamCmpHeaderDataProvider(final Map<Integer, String> propertyToLabelMap) {
    this.propertyToLabelMap = propertyToLabelMap;
  }

  /**
   * @param columnIndex int
   * @return String
   */
  public String getColumnHeaderLabel(final int columnIndex) {
    String columnHeaderLabel = this.propertyToLabelMap.get(columnIndex);

    return columnHeaderLabel == null ? "" : columnHeaderLabel;
  }

  @Override
  public int getColumnCount() {
    return this.propertyToLabelMap.size();
  }

  @Override
  public int getRowCount() {
    return 1;
  }

  /**
   * This class does not support multiple rows in the column header layer.
   */
  @Override
  public Object getDataValue(final int columnIndex, final int rowIndex) {
    return getColumnHeaderLabel(columnIndex);
  }

  @Override
  public void setDataValue(final int columnIndex, final int rowIndex, final Object newValue) {
    throw new UnsupportedOperationException();
  }


}
