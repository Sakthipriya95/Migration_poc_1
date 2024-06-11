package com.bosch.caltool.nattable;

import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsDataProvider;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.util.concurrent.Lock;
import ca.odell.glazedlists.util.concurrent.ReadWriteLock;


/**
 * This class is a variation of <code>GlazedListsDataProvider</code> which is modified in a way to use
 * <code>NatInputToColumnConverter</code>
 *
 * @author jvi6cob
 */
public class CustomGlazedListsDataProvider<T> extends GlazedListsDataProvider<T> {

  private int finalRowIndex = -1;
  private T finalRowObj;
  private final AbstractNatInputToColumnConverter natInputToColumnConverter;

  /**
   * @param list EventList<T>
   * @param columnAccessor IColumnAccessor<T>
   * @param natInputToColumnConverter NatInputToColumnConverter
   */
  public CustomGlazedListsDataProvider(final EventList<T> list, final IColumnAccessor<T> columnAccessor,
      final AbstractNatInputToColumnConverter natInputToColumnConverter) {
    super(list, columnAccessor);

    this.natInputToColumnConverter = natInputToColumnConverter;
  }

  /**
   *
   */
  @Override
  public void inputChanged() {
    this.finalRowObj = null;
    this.finalRowIndex = -1;
  }

  @Override
  public T getRowObject(final int rowIndex) {
    if ((rowIndex != this.finalRowIndex) || (this.finalRowObj == null)) {
      EventList evntList = (EventList) this.list;
      ReadWriteLock readWriteLock = evntList.getReadWriteLock();
      Lock readLock = readWriteLock.readLock();
      readLock.lock();
      try {
        // Check the row count, to avoid getting objects for special rows (eg, filter rows..)
        if (super.getRowCount() > 0) {
          T rowObject = super.getRowObject(rowIndex);
          return rowObject;
        }
      }
      finally {
        readLock.unlock();
      }
    }

    return this.finalRowObj;
  }

  @Override
  public Object getDataValue(final int colIndex, final int rowIndex) {
    // new row to cache
    if ((rowIndex != this.finalRowIndex) || (this.finalRowObj == null)) {
      this.finalRowIndex = rowIndex;
      EventList evntList = (EventList) this.list;
      ReadWriteLock readWriteLock = evntList.getReadWriteLock();
      Lock readLock = readWriteLock.readLock();
      readLock.lock();
      try {
        this.finalRowObj = this.list.get(rowIndex);
      }
      finally {
        readLock.unlock();
      }
    }

    // same row as last, use its object as it's way faster than a get() method of list
    Object evaluateObj = this.columnAccessor.getDataValue(this.finalRowObj, colIndex);
    return this.natInputToColumnConverter.getColumnValue(evaluateObj, colIndex);
  }
}
