/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.product.dialogs.IcdmLinkOpenDialog;

/**
 * Open the ICDM Link dialog
 *
 * @author bne4cob
 */
// ICDM-2242
public class IcdmLinkOpenDialogAction extends Action implements IWorkbenchAction {

  /**
   * Text for the Action button
   */
  private static final String ACTION_TEXT = "Open ICDM Link";

  /**
   * Action ID
   */
  private static final String ACTION_ID = "com.bosch.caltool.icdm.product.actions.IcdmLinkOpenDialogAction";

  /**
   * Constructor. Sets text and image
   */
  public IcdmLinkOpenDialogAction() {
    super(ACTION_TEXT, ImageManager.getImageDescriptor(ImageKeys.ICDM_LINK_16X16));
    setId(ACTION_ID);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    IcdmLinkOpenDialog linkOpenDialog = new IcdmLinkOpenDialog(Display.getCurrent().getActiveShell());
    linkOpenDialog.open();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    // not required
  }

}
