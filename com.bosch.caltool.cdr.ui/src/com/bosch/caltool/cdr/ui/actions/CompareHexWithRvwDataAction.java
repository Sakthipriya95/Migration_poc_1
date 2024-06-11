/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.cdr.ui.dialogs.CompHexWithRvwDataDialog;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;

/**
 * icdm-2474 action to compare HEX file with latest review data
 *
 * @author mkl2cob
 */
public class CompareHexWithRvwDataAction extends Action {

  /**
   * A2LFile
   */
  private final PidcA2l pidcA2l;

  /**
   * @param pidcA2l
   */
  public CompareHexWithRvwDataAction(final PidcA2l pidcA2l) {
    super();
    this.pidcA2l = pidcA2l;
    setProperties();
  }

  /**
   * set image, text etc
   */
  private void setProperties() {
    setText("Compare HEX With Latest Reviewed Data");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.COMPARE_EDITOR_16X16);
    setImageDescriptor(imageDesc);
    setEnabled(true);// this action is enabled for all users right now
    if (CommonUtils.isNotNull(this.pidcA2l) && !this.pidcA2l.isActive()) {
      setEnabled(false);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    CompHexWithRvwDataDialog dialog = new CompHexWithRvwDataDialog(this.pidcA2l, Display.getCurrent().getActiveShell());
    dialog.open();
  }
}
