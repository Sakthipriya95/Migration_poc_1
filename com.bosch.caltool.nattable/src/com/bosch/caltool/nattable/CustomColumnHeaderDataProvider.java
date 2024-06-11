package com.bosch.caltool.nattable;

import java.util.Map;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;


/**
 * The <code>CustomColumnHeaderDataProvider</code> is used to provide column header values to the NatTable
 * 
 * @author jvi6cob
 */
public class CustomColumnHeaderDataProvider implements IDataProvider {

  private final Map<Integer, String> columnToLabelMap;


  /**
   * Constructor which assigns null to column Label Map
   */
  public CustomColumnHeaderDataProvider() {
    this.columnToLabelMap = null;
  }

  /**
   * Constructor which accepts a column Label Map which provide String Text for Columns
   * 
   * @param columnToLabelMap Map<Integer, String>
   */
  public CustomColumnHeaderDataProvider(final Map<Integer, String> columnToLabelMap) {
    this.columnToLabelMap = columnToLabelMap;
  }

  /**
   * @param columnIndex int
   * @return String
   */
  public String getColumnHeaderLabel(final int columnIndex) {
    return this.columnToLabelMap.get(columnIndex);
  }

  @Override
  public int getColumnCount() {
    return this.columnToLabelMap.size();
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