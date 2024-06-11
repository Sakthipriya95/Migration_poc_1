/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages.natsupport;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.validate.IDataValidator;
import org.eclipse.nebula.widgets.nattable.data.validate.ValidationFailedException;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.client.bo.a2l.A2lWPValidationBO;
import com.bosch.caltool.icdm.client.bo.apic.WorkPkgResponsibilityBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;

/**
 * @author elm1cob
 */
public class WorkPackagesNatValidator implements IDataValidator {

  /**
   * validates WP
   */
  private final A2lWPValidationBO wpValidationBo;

  /**
   * @param wrkPkgBo WorkPkgResponsibilityBO
   */
  public WorkPackagesNatValidator(final WorkPkgResponsibilityBO wrkPkgBo) {
    this.wpValidationBo = new A2lWPValidationBO(wrkPkgBo);
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
      case ApicUiConstants.COLUMN_INDEX_0:
        // validate WP name
        // WP name should not be empty or duplicate
        // WP name should not exceed 128 characters
        cell.getDataValue();
        if (!cell.getDataValue().equals(newValue)) {
          errorMsg = this.wpValidationBo.validateWPName((String) newValue);
        }
        break;
      case ApicUiConstants.COLUMN_INDEX_2:
        // validate name at customer
        // should not exceed 128 characters
        if (!CommonUtils.isEmptyString((String) newValue)) {
          errorMsg = this.wpValidationBo.validateWPCustName((String) newValue);
        }
        break;
      default:
        break;
    }
    // if validation is success return true
    if (errorMsg.isEmpty()) {
      flag = true;
    }
    else {
      // if validation fails, show error message
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
    return false;
  }

}
