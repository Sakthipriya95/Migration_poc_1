/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.cdr.ui.dialogs.CompliReviewDialog;
import com.bosch.caltool.cdr.ui.dialogs.DownloadCompliRvwInputDataDialog;

/**
 * @author dmr1cob
 */
public class DownloadCompliRvwInputDataAction extends Action {

  /**
   * Compliance review Dialog
   */
  private final CompliReviewDialog compliRvwDialog;

  /**
   * @param compliRvwDialog {@link CompliReviewDialog}
   */
  public DownloadCompliRvwInputDataAction(final CompliReviewDialog compliRvwDialog) {
    this.compliRvwDialog = compliRvwDialog;
  }

  @Override
  public void run() {
    DownloadCompliRvwInputDataDialog inputDataDialog =
        new DownloadCompliRvwInputDataDialog(Display.getDefault().getActiveShell(), this.compliRvwDialog);
    inputDataDialog.open();
  }
}
