package com.bosch.caltool.nattable;

import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;


/**
 * The <code>CustomColumnPropertyAccessor</code> contains a customised getDataValue method used for sorting </br></br>
 * <b>TODO:</b> Need to check if any alternative like overriding <code>GlazedListsSortModel</code> is possible to which
 * custom comparators for sorting can be passed
 * 
 * @param <T> NatTable Input data type
 * @author jvi6cob
 */
public class CustomColumnPropertyAccessor<T> implements IColumnAccessor<T> {

  private final int columnCount;

  /**
   * Constructor
   * 
   * @param columnCount int
   */
  public CustomColumnPropertyAccessor(final int columnCount) {
    this.columnCount = columnCount;
  }

  /**
   * This method has been overridden so that it returns the passed row object. The above behavior is required for use of
   * custom comparators for sorting which requires the Row object to be passed without converting to a particular column
   * String value {@inheritDoc}
   */
  @Override
  public Object getDataValue(final T typeObj, final int columnIndex) {
    return typeObj;
  }


  @Override
  public void setDataValue(final T sysConstNatModel, final int columnIndex, final Object newValue) {
    // TODO:
  }

  @Override
  public int getColumnCount() {
    return this.columnCount;
  }


}
