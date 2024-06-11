/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.pages;

import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;


/**
 * @author jvi6cob
 */
public class ParamNatInputToColumnConverter extends AbstractNatInputToColumnConverter { // NOPMD by jvi6cob on 7/7/14

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getColumnValue(final Object evaluateObj, final int colIndex) {

    Object result = null;
    // same row as last, use its object as it's way faster than a list get row
    if (evaluateObj instanceof A2LParameter) {
      // Get the a2l parameter data
      result = getA2lParameterColumnData((A2LParameter) evaluateObj, colIndex);
    }

    return result;

  }

  /**
   * @param a2lParameter
   * @param colIndex
   * @return
   */
  private Object getA2lParameterColumnData(final A2LParameter a2lParameter, final int colIndex) { // NOPMD by jvi6cob on
                                                                                                  // 7/7/14
    Object result;
    switch (colIndex) {
      case 1:
        result = getA2lParamFuncName(a2lParameter);
        break;
      case 2:
        result = a2lParameter.getName();
        break;
      case 3:
        result = a2lParameter.getType();
        break;
      case 4:
        result = a2lParameter.getUnit();
        break;
      case 5:
        result = a2lParameter.getPclassString();
        break;
      case 6:
        result = a2lParameter.getCodeWordString();
        break;
      case 7:
        result = a2lParameter.getLongIdentifier();
        break;
      case 8:
        result = getA2lParamCalDataPhy(a2lParameter);
        break;
      case 9:
        result = a2lParameter.getStatus();
        break;

      default:
        result = "";
        break;
    }
    return result;
  }

  /**
   * @param paramData
   * @return
   */
  private Object getA2lParamFuncName(final A2LParameter paramData) {
    Object result;
    // Icdm-469
    // If parameter doesn't have any function, mark it as unassigned
    if (paramData.getDefFunction() == null) {
      result = ApicConstants.UNASSIGNED_PARAM;
    }
    else {
      result = paramData.getDefFunction().getName();
    }
    return result;
  }

  /**
   * @param paramData
   * @return
   */
  private Object getA2lParamCalDataPhy(final A2LParameter paramData) {
    Object result;
    result = "";
    if ((paramData.getCalData() != null) && (paramData.getCalData().getCalDataPhy() != null)) {
      // Get value of cal Data phy
      result = paramData.getCalData().getCalDataPhy().getSimpleDisplayValue();
    }
    return result;
  }

}
