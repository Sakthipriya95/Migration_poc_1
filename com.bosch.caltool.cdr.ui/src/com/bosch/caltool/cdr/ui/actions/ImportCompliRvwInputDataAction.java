/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.cdr.ui.dialogs.CompliReviewDialog;
import com.bosch.caltool.cdr.ui.dialogs.ImportCompliRvwInputDataDialog;

/**
 * @author dmr1cob
 */
public class ImportCompliRvwInputDataAction extends Action {

  /**
   * Compliance review Dialog
   */
  private final CompliReviewDialog compliRvwDialog;

  public ImportCompliRvwInputDataAction(final CompliReviewDialog compliRvwDialog) {
    this.compliRvwDialog = compliRvwDialog;
  }

  @Override
  public void run() {
    ImportCompliRvwInputDataDialog inputDataDialog =
        new ImportCompliRvwInputDataDialog(Display.getDefault().getActiveShell(), this.compliRvwDialog);
    inputDataDialog.open();
  }
}
