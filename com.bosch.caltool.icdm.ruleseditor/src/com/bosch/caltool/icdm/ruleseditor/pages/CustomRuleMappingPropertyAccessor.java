/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.pages;

import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;

import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;


/**
 * @author adn1cob
 */
public class CustomRuleMappingPropertyAccessor<T> implements IColumnAccessor<T> {


  private final int columnCount;

  private final AbstractNatInputToColumnConverter natColConverter;

  /**
   * Constructor
   * 
   * @param colCount col count
   */
  public CustomRuleMappingPropertyAccessor(final int colCount, final AbstractNatInputToColumnConverter natColConverter) {
    this.columnCount = colCount;
    this.natColConverter = natColConverter;
  }

  /**
   * This method has been overridden so that it returns the passed row object. The above behavior is required for use of
   * custom comparators for sorting which requires the Row object to be passed without converting to a particular column
   * String value {@inheritDoc}
   */
  @Override
  public Object getDataValue(final T data, final int columnIndex) {
    return data;
  }


  @Override
  public void setDataValue(final T sysConstNatModel, final int columnIndex, final Object newValue) {
    // TODO:
  }

  @Override
  public int getColumnCount() {
    // any specific implementation, here
    return this.columnCount;
  }

  /**
   * @return AbstractNatInputToColumnConverter
   */
  public AbstractNatInputToColumnConverter getColumConvertor() {
    return this.natColConverter;
  }
}
