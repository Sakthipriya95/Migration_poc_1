/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import com.bosch.caltool.admin.ui.dialog.ImportA2lRespMergeDataDialog;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;

/**
 * @author DMR1COB
 */
public class ImportA2lRespMergeDataDialogAction extends Action implements IWorkbenchAction {

  private static final String ACTION_ID = "com.bosch.caltool.icdm.product.actions.ImportA2lRespMergeDataDialogAction";

  private static final String ACTION_TEXT = "Merge A2L Responsibility";

  /**
   * Constructor. Sets text and image
   */
  public ImportA2lRespMergeDataDialogAction() {
    super(ACTION_TEXT, ImageManager.getImageDescriptor(ImageKeys.BOSCH_RESPONSIBLE_ICON_16X16));
    setId(ACTION_ID);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    ImportA2lRespMergeDataDialog importA2lRespMergeDataDialog =
        new ImportA2lRespMergeDataDialog(Display.getCurrent().getActiveShell());
    importA2lRespMergeDataDialog.open();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    // TODO Auto-generated method stub
  }
}
