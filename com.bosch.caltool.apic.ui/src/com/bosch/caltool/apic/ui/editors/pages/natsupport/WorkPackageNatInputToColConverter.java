/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages.natsupport;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;

/**
 * @author elm1cob
 */
public class WorkPackageNatInputToColConverter extends AbstractNatInputToColumnConverter {

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getColumnValue(final Object evaluateObj, final int colIndex) {
    Object result = null;
    // A2lResponsibility represents a row in the table viewer
    if (evaluateObj instanceof A2lWorkPackage) {
//      get data based on column index
      result = getData((A2lWorkPackage) evaluateObj, colIndex);
    }
    return result;
  }

  /**
   * @param a2lWp
   * @param colIndex
   * @return
   */
  private Object getData(final A2lWorkPackage a2lWp, final int colIndex) {
    Object result = null;
//    get a2l work package data
    switch (colIndex) {
//      work package name
      case ApicUiConstants.COLUMN_INDEX_0:
        result = a2lWp.getName();
        break;
//      work package description
      case ApicUiConstants.COLUMN_INDEX_1:
        result = CommonUtils.checkNull(a2lWp.getDescription());
        break;
//      work package customer name
      case ApicUiConstants.COLUMN_INDEX_2:
        result = CommonUtils.checkNull(a2lWp.getNameCustomer());
        break;
      default:
        break;
    }
    return result;
  }

}
