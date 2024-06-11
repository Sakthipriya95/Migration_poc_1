/*
 * Copyright (c) ETAS GmbH 2024. All rights reserved.
 */
package com.bosch.caltool.icdm.product.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import com.bosch.caltool.admin.ui.dialog.DeleteReviewResultDialog;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;

/**
 *
 */
public class AdminDeleteReviewResultAction extends Action implements IWorkbenchAction {

  private static final String ACTION_ID = "com.bosch.caltool.icdm.product.actions.DeleteReviewAction";

  private static final String ACTION_TEXT = "Delete Review Result";

  /**
   * Constructor. Sets text and image
   */
  public AdminDeleteReviewResultAction() {
    super(ACTION_TEXT, ImageManager.getImageDescriptor(ImageKeys.RVW_RES_ADMIN_DEL));
    setId(ACTION_ID);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    DeleteReviewResultDialog deleteReviewResultDialog =
        new DeleteReviewResultDialog(Display.getCurrent().getActiveShell());
    deleteReviewResultDialog.open();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    // empty dispose method

  }
}
