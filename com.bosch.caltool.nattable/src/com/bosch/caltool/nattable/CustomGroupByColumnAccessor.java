package com.bosch.caltool.nattable;

import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByObject;


/**
 * @author jvi6cob
 */
public class CustomGroupByColumnAccessor<T> implements IColumnAccessor<Object> {

  protected final AbstractNatInputToColumnConverter natInputToColumnConverter;
  protected final IColumnAccessor<T> columnAccessor;

  public CustomGroupByColumnAccessor(final IColumnAccessor<T> columnAccessor,
      final AbstractNatInputToColumnConverter natInputToColumnConverter) {
    this.columnAccessor = columnAccessor;
    this.natInputToColumnConverter = natInputToColumnConverter;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Object getDataValue(final Object rowObject, final int columnIndex) {
    if (rowObject instanceof GroupByObject) {
      GroupByObject groupByObject = (GroupByObject) rowObject;
      return groupByObject.getValue();
    }
    return this.natInputToColumnConverter.getColumnValue(rowObject, columnIndex);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void setDataValue(final Object rowObject, final int columnIndex, final Object newValue) {
    if (rowObject instanceof GroupByObject) {
      // do nothing
    }
    else {
      this.columnAccessor.setDataValue((T) rowObject, columnIndex, newValue);
    }
  }

  @Override
  public int getColumnCount() {
    return this.columnAccessor.getColumnCount();
  }
}
