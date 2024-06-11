/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

/**
 * Column header data provider class
 *
 * @author mkl2cob
 */
class CdrReportColumnHeaderDataProvider implements IDataProvider {


  /**
   *
   */
  private final CdrReportListPage dataRvwReportPage;

  /**
   * @param dataRvwReportPage
   */
  CdrReportColumnHeaderDataProvider(final CdrReportListPage dataRvwReportPage) {
    this.dataRvwReportPage = dataRvwReportPage;
  }

  /**
   * @param columnIndex int
   * @return String column header label
   */
  public String getColumnHeaderLabel(final int columnIndex) {
    String string = this.dataRvwReportPage.getPropertyToLabelMap().get(columnIndex);

    return string == null ? "" : string;
  }

  @Override
  public int getColumnCount() {
    return this.dataRvwReportPage.getPropertyToLabelMap().size();
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

  /**
   * This method is not applicable hence throwing UnsupportedOperation Exception when trying to call
   */
  @Override
  public void setDataValue(final int columnIndex, final int rowIndex, final Object newValue) {
    throw new UnsupportedOperationException();
  }

}