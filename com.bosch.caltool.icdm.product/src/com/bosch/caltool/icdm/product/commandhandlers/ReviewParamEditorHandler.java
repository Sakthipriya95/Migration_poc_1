/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.commandhandlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.bosch.caltool.cdr.ui.dialogs.FunctionSelectionDialog;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;


/**
 * Handler class for ReviewParamEditor open icon
 *
 * @author mkl2cob
 */
public class ReviewParamEditorHandler extends AbstractHandler {

  /**
   * {@inheritDoc}
   */
  @Override
  public Object execute(final ExecutionEvent event) throws ExecutionException {

    // Open the function selection dialog
    FunctionSelectionDialog dialog =
        new FunctionSelectionDialog(CommonUiUtils.getInstance().getDisplay().getActiveShell(), null, null);
    dialog.open();

    return null;
  }
}
