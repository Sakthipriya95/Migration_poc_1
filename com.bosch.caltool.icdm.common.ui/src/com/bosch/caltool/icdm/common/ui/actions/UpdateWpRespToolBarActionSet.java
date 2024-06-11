/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.common.ui.dialogs.CreateEditA2lWrkPkgDialog;
import com.bosch.caltool.icdm.common.ui.dialogs.CreateEditWpRespDialog;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;

/**
 * @author nip4cob
 */
public class UpdateWpRespToolBarActionSet {

  private final CreateEditWpRespDialog updateWpRespDialog;
  private final A2LWPInfoBO a2lWpInfoBo;
  private Action editAction;


  /**
   * @param updateWpRespDialog dialog
   */
  public UpdateWpRespToolBarActionSet(final CreateEditWpRespDialog updateWpRespDialog) {
    this.updateWpRespDialog = updateWpRespDialog;
    this.a2lWpInfoBo = updateWpRespDialog.gtA2lWoInfoBo();
  }

  /**
   * @param toolbarManager toolbarManager
   */
  public void addCreateWpAction(final ToolBarManager toolbarManager) {
    Action addAction = new Action() {

      @Override
      public void run() {
        CreateEditA2lWrkPkgDialog dialog = new CreateEditA2lWrkPkgDialog(Display.getCurrent().getActiveShell(), true,
            UpdateWpRespToolBarActionSet.this.a2lWpInfoBo, null, UpdateWpRespToolBarActionSet.this);
        dialog.open();
        UpdateWpRespToolBarActionSet.this.updateWpRespDialog.enableSave(true);
      }
    };
    addAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));
    addAction.setEnabled(this.a2lWpInfoBo.isEditable());
    addAction.setToolTipText("Add Work Package");
    toolbarManager.add(addAction);
  }

  /**
   * @param toolbarManager toolbarManager
   */
  public void addEditWpAction(final ToolBarManager toolbarManager) {
    this.editAction = new Action() {

      @Override
      public void run() {
        if (UpdateWpRespToolBarActionSet.this.updateWpRespDialog.getSelectedWp() != null) {
          CreateEditA2lWrkPkgDialog dialog = new CreateEditA2lWrkPkgDialog(Display.getCurrent().getActiveShell(), false,
              UpdateWpRespToolBarActionSet.this.a2lWpInfoBo,
              UpdateWpRespToolBarActionSet.this.updateWpRespDialog.getSelectedWp(), UpdateWpRespToolBarActionSet.this);
          dialog.open();
          UpdateWpRespToolBarActionSet.this.updateWpRespDialog.loadWorkPkgDetails();
          UpdateWpRespToolBarActionSet.this.updateWpRespDialog.enableSave(false);
        }
      }
    };
    getEditAction().setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));
    getEditAction().setEnabled(false);
    getEditAction().setToolTipText("Edit Work Package");
    toolbarManager.add(this.editAction);
  }


  /**
   * @return the updateWpRespDialog
   */
  public CreateEditWpRespDialog getUpdateWpRespDialog() {
    return this.updateWpRespDialog;
  }

  /**
   * @return the editAction
   */
  public Action getEditAction() {
    return this.editAction;
  }

  /**
   * @param editAction the editAction to set
   */
  public void setEditAction(final Action editAction) {
    this.editAction = editAction;
  }

}
