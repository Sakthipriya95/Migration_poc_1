/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.pages.natsupport;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.validate.IDataValidator;
import org.eclipse.nebula.widgets.nattable.data.validate.ValidationFailedException;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.ui.util.IUIConstants;

/**
 * @author pdh2cob
 */
public class A2lWPDefinitionNatValidator implements IDataValidator {


  private final A2LWPInfoBO a2lWpInfoBo;


  /**
   * @param a2lWpInfoBo
   */
  public A2lWPDefinitionNatValidator(final A2LWPInfoBO a2lWpInfoBo) {
    this.a2lWpInfoBo = a2lWpInfoBo;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean validate(final ILayerCell cell, final IConfigRegistry configRegistry, final Object newValue) {
    int colIndex = cell.getColumnIndex();
    boolean flag = false;
    String errorMsg = "";
    switch (colIndex) {
      case IUIConstants.COLUMN_INDEX_WP:
        cell.getDataValue();
        if (!cell.getDataValue().equals(newValue)) {
          errorMsg = this.a2lWpInfoBo.getA2lWpValidationBO().validateWPName((String) newValue);
        }
        break;
      case IUIConstants.COLUMN_INDEX_WP_CUST:
        if (!CommonUtils.isEmptyString((String) newValue)) {
          errorMsg = this.a2lWpInfoBo.getA2lWpValidationBO().validateWPCustName((String) newValue);
        }
        break;
      default:
        break;
    }
    if (errorMsg.isEmpty()) {
      flag = true;
    }
    else {
      // Error message will be shown as tooltip in nattable cell
      throw new ValidationFailedException(errorMsg);
    }
    return flag;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean validate(final int columnIndex, final int rowIndex, final Object newValue) {
    // TODO Auto-generated method stub
    return false;
  }

}
