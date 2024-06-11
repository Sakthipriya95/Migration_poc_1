/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.commandhandlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.bosch.caltool.apic.ui.dialogs.AliasDefinitionDialog;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;


/**
 * Handler class for ReviewParamEditor open icon
 *
 * @author mkl2cob
 */
public class AliasDefinitionHandler extends AbstractHandler {

  /**
   * {@inheritDoc}
   */
  @Override
  public Object execute(final ExecutionEvent event) throws ExecutionException {
    // fetch the defintions
    AliasDefinitionDialog dialog = new AliasDefinitionDialog(CommonUiUtils.getInstance().getDisplay().getActiveShell());
    // open the AliasDefinitionDialog
    dialog.open();

    return null;
  }
}
