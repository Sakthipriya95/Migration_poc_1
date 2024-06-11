/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views.providers;

import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixAttributeClientBO;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;


/**
 * @author mkl2cob
 */
public class FocusMatrixInputToColumnConverter extends AbstractNatInputToColumnConverter {


  /**
   * {@inheritDoc}
   */
  @Override
  public Object getColumnValue(final Object evaluateObj, final int colIndex) {
    Object result = null;
    // FocusMatrixAttribute represents a row in the table viewer
    if (evaluateObj instanceof FocusMatrixAttributeClientBO) {
      result = getAttributeData((FocusMatrixAttributeClientBO) evaluateObj, colIndex);
    }
    return result;
  }

  /**
   * @param evaluateObj
   * @param colIndex
   * @return
   */
  private Object getAttributeData(final FocusMatrixAttributeClientBO focusNatInput, final int colIndex) {
    Object result;
    switch (colIndex) {
      case 0:// Attr name
        result = focusNatInput.getAttribute().getName();
        break;
      case 1:// Description
        result = focusNatInput.getAttribute().getDescription();
        break;
      case 2:// Value
        // ICDM-2612
        result = focusNatInput.getValueDisplay();
        break;
      case 3:
        result = "";
        break;
      default:
        // Empty string is enough but for enabling combo-box filtering/image based filtering in individual columns the
        // below code is required
        com.bosch.caltool.icdm.client.bo.fm.FocusMatrixUseCaseItem focusmatrixUseCaseItem =
            focusNatInput.getFocusmatrixUseCaseItem(colIndex - 4);
        result = focusmatrixUseCaseItem.getFilterText(focusNatInput.getAttribute());
        break;
    }
    return result;
  }

}
