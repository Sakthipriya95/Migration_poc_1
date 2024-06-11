/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import com.bosch.caltool.admin.ui.dialog.CreateRuleSetDialog;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;

/**
 * @author say8cob
 */
public class OpenCreateRuleSetDialogAction extends Action implements IWorkbenchAction {


  private static final String ACTION_ID = "com.bosch.caltool.icdm.product.actions.OpenCreateRuleSetDialogAction";

  private static final String ACTION_TEXT = "Create RuleSet";

  /**
   * Constructor. Sets text and image
   */
  public OpenCreateRuleSetDialogAction() {
    super(ACTION_TEXT, ImageManager.getImageDescriptor(ImageKeys.RULE_SET_16X16));
    setId(ACTION_ID);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    CreateRuleSetDialog createRuleSetDialog = new CreateRuleSetDialog(Display.getCurrent().getActiveShell());
    createRuleSetDialog.open();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    // TODO Auto-generated method stub

  }

}
