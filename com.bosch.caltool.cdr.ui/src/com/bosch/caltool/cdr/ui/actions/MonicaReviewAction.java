/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.cdr.ui.dialogs.MonicaReviewDialog;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;

/**
 * @author dmr1cob
 */
public class MonicaReviewAction extends Action {

  /**
   * A2LFile
   */
  private final PidcA2l pidcA2l;

  /**
   * @param pidcA2l pidcA2l
   */
  public MonicaReviewAction(final PidcA2l pidcA2l) {
    super();
    this.pidcA2l = pidcA2l;
    setProperties();
  }

  /**
   * set image, text etc
   */
  private void setProperties() {
    setText("Upload MoniCa Review Report");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.MONICA_REPORT_16X16);
    setImageDescriptor(imageDesc);
    setEnabled(this.pidcA2l.isActive());// this action is enabled for all users right now
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    // create the dialog that accepts inputs for comparison
    MonicaReviewDialog dialog = new MonicaReviewDialog(this.pidcA2l, Display.getCurrent().getActiveShell(), false);
    dialog.open();
  }
}
