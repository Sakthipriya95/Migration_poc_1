/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import com.bosch.caltool.admin.ui.editors.UnmapA2lAdminEditor;
import com.bosch.caltool.admin.ui.editors.UnmapA2lAdminEditorInput;
import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;

/**
 * @author hnu1cob
 */
public class OpenUnmapA2lEditorAction extends Action implements IWorkbenchAction {


  private static final String UNMAP_A2L_ADMIN_EDITOR_ACTION_ID = "com.bosch.caltool.icdm.UnmapA2lAdminEditor";

  private static final String ACTION_TEXT = "Unmap A2L";

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
  public OpenUnmapA2lEditorAction() {
    setId(UNMAP_A2L_ADMIN_EDITOR_ACTION_ID);
    super.setText(ACTION_TEXT);
    // Set the Image for Admin Page
    super.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.A2LFILE_16X16));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    try {
      // To Hide the Intro Page
      CommonUiUtils.hideIntroViewPart();
      UnmapA2lAdminEditorInput unmapA2lEditorInput = new UnmapA2lAdminEditorInput();
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(unmapA2lEditorInput,
          UnmapA2lAdminEditor.EDITOR_ID);
    }
    catch (PartInitException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }

  }

}
