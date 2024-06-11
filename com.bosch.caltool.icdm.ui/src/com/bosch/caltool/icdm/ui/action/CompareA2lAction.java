/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.action;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.ui.dialogs.CompareA2lDialog;

/**
 * @author apj4cob
 */
public class CompareA2lAction extends Action {

  /**
   * List of selected A2l from Favorites/Pidc Tree View
   */
  private final List<PidcA2l> pidcA2l;


  /**
   * @param pidcA2l selected Element (PIDC file)
   */
  public CompareA2lAction(final List<PidcA2l> pidcA2l) {
    this.pidcA2l = pidcA2l;
    setProperties();

  }


  /**
   *
   */
  private void setProperties() {
    setText("Compare A2L with Work Packages");
    setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.COMPARE_EDITOR_16X16));
    setEnabled(true);
  }

  @Override
  public void run() {
    CompareA2lDialog compareA2lDialog = new CompareA2lDialog(this.pidcA2l, null, Display.getCurrent().getActiveShell());
    compareA2lDialog.open();
    super.run();
  }

}

