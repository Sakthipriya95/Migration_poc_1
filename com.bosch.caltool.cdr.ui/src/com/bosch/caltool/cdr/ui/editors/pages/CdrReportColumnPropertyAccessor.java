/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages;

import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;

/**
 * column property accessor
 *
 * @author mkl2cob
 * @param <T>
 */
class CdrReportColumnPropertyAccessor<T> implements IColumnAccessor<T> {


  /**
   *
   */
  private final CdrReportListPage dataRvwReportPage;

  /**
   * @param dataRvwReportPage
   */
  CdrReportColumnPropertyAccessor(final CdrReportListPage dataRvwReportPage) {
    this.dataRvwReportPage = dataRvwReportPage;
  }


  /**
   * This method has been overridden so that it returns the passed row object. The above behavior is required for use of
   * custom comparators for sorting which requires the Row object to be passed without converting to a particular column
   * String value {@inheritDoc}
   */
  @Override
  public Object getDataValue(final T type, final int columnIndex) {
    return type;
  }


  @Override
  public void setDataValue(final T sysConstNatModel, final int columnIndex, final Object newValue) {
    // Implementation Not Applicable
  }

  @Override
  public int getColumnCount() {
    return this.dataRvwReportPage.getPropertyToLabelMap().size();
  }


}