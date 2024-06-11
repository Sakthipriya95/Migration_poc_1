/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages.natsupport;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.validate.IDataValidator;
import org.eclipse.nebula.widgets.nattable.data.validate.ValidationFailedException;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;

import com.bosch.caltool.icdm.client.bo.apic.WorkPkgResponsibilityBO;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;


/**
 * @author apj4cob
 */
public class RespNatValidator implements IDataValidator {

  private final WorkPkgResponsibilityBO wpRespBO;
  private A2lResponsibility selA2lResp;


  /**
   * @param wpRespBO WorkPkgResponsibilityBO
   */
  public RespNatValidator(final WorkPkgResponsibilityBO wpRespBO) {
    this.wpRespBO = wpRespBO;
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
      case 2:
        errorMsg = this.wpRespBO.getRespValidationBO().validateLFirstName((String) newValue, this.selA2lResp);
        break;
      case 3:
        errorMsg = this.wpRespBO.getRespValidationBO().validateLLastName((String) newValue, this.selA2lResp);
        break;
      case 4:
        errorMsg = this.wpRespBO.getRespValidationBO().validateLDept((String) newValue, this.selA2lResp);
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
    return false;
  }


  /**
   * @return A2lResponsibility
   */
  public A2lResponsibility getSelA2lResp() {
    return this.selA2lResp;
  }


  /**
   * @param selA2lResp A2lResponsibility
   */
  public void setSelA2lResp(final A2lResponsibility selA2lResp) {
    this.selA2lResp = selA2lResp;
  }
}
