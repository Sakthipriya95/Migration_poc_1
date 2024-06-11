/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;


/**
 * @author rgo7cob icdm-221 and icdm-222
 */
public class CustomProgressDialog extends ProgressMonitorDialog {

  /**
   * @param parent obj
   */
  public CustomProgressDialog(final Shell parent) {
    super(parent);
    // TODO Auto-generated constructor stub
  }

  /**
   * overriden for disabling the cancel button
   */
  @Override
  protected void setOperationCancelButtonEnabled(final boolean boolFlg) {

    this.cancel.setEnabled(false);

  }

}
