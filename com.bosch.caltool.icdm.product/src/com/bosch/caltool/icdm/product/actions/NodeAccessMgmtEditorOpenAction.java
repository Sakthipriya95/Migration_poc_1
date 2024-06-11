/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import com.bosch.caltool.admin.ui.editors.NodeAccessMgmtEditor;
import com.bosch.caltool.admin.ui.editors.NodeAccessMgmtEditorInput;
import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;

/**
 * @author say8cob
 */
public class NodeAccessMgmtEditorOpenAction extends Action implements IWorkbenchAction {


  private static final String ADMIN_ACTION_ID = "com.bosch.icdm.NodeAccessMgmtEditor";

  private static final String ACTION_TEXT = "Node Access Management";

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    // Not Implemented

  }

  /**
  *
  */
  public NodeAccessMgmtEditorOpenAction() {
    setId(ADMIN_ACTION_ID);
    super.setText(ACTION_TEXT);
    // Set the Image for Admin Page
    super.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADMIN_16X16));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    try {
      // To Hide the Intro Page
      CommonUiUtils.hideIntroViewPart();
      NodeAccessMgmtEditorInput adminEditorInput = new NodeAccessMgmtEditorInput();
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(adminEditorInput,
          NodeAccessMgmtEditor.EDITOR_ID);
    }
    catch (PartInitException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }

  }

}
