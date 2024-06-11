/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author mkl2cob
 */
public interface AbstractFMEditComponent {


  /**
   * this method validates remarks
   */
  abstract void validateRemarks();

  /**
   * This method initializes formToolkit
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  abstract FormToolkit getFormToolkit();
}
