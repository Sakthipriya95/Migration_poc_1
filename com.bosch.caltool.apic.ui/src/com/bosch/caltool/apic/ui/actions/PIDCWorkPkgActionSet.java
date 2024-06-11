/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.apic.ui.editors.pages.WorkPackagesPage;
import com.bosch.caltool.icdm.client.bo.apic.WorkPkgResponsibilityBO;
import com.bosch.caltool.icdm.common.ui.dialogs.CreateEditA2lWrkPkgDialog;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;

/**
 * @author elm1cob
 */
public class PIDCWorkPkgActionSet {

  /**
   * WorkPackagesPage instance
   */
  private final WorkPackagesPage wrkPkgPage;

  /**
   * add workpackage action
   */
  private Action addWrkPkgAction;

  /**
   * edit workpackage action
   */
  private Action editWrkPkgAction;

  /**
   * WorkPkgResponsibilityBO
   */
  private final WorkPkgResponsibilityBO respBoObj;

  /**
   * Constructor
   *
   * @param wpRespBO WorkPkgResponsibilityBO
   */
  public PIDCWorkPkgActionSet(final WorkPkgResponsibilityBO wpRespBO, final WorkPackagesPage wrkPackgPage) {
    super();
    this.respBoObj = wpRespBO;
    this.wrkPkgPage = wrkPackgPage;
  }

  /**
   * create action for adding workpackage
   *
   * @param toolbarManager ToolBarManager
   */
  public void addWorkPkgAction(final ToolBarManager toolbarManager) {
    this.addWrkPkgAction = new Action() {

      @Override
      public void run() {
        // open create workpackage dialog
        CreateEditA2lWrkPkgDialog dialog = new CreateEditA2lWrkPkgDialog(Display.getCurrent().getActiveShell(), true,
            null, PIDCWorkPkgActionSet.this.respBoObj);
        dialog.open();
        if (CommonUtils.isNotNull(dialog.getCurrentWorkpackage())) {
          // set the selection to the newly created workpackage
          PIDCWorkPkgActionSet.this.wrkPkgPage.setSelectedRow(dialog.getCurrentWorkpackage());
          PIDCWorkPkgActionSet.this.wrkPkgPage.setSelectedWorkpackage(dialog.getCurrentWorkpackage());
        }
      }
    };
    // use add image
    this.addWrkPkgAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));
    this.addWrkPkgAction.setToolTipText("Add Work Package");
    // enable the action based on the access rights of workpackage page
    this.addWrkPkgAction.setEnabled(this.wrkPkgPage.hasAccessRights());
    toolbarManager.add(this.addWrkPkgAction);

  }

  /**
   * create action for edit workpackage
   *
   * @param toolbarManager ToolBarManager
   */
  public void editWrkPkgAction(final ToolBarManager toolbarManager) {
    this.editWrkPkgAction = new Action() {

      @Override
      public void run() {
        // open edit dialog
        CreateEditA2lWrkPkgDialog dialog = new CreateEditA2lWrkPkgDialog(Display.getCurrent().getActiveShell(), false,
            PIDCWorkPkgActionSet.this.wrkPkgPage.getSelectedRow(), PIDCWorkPkgActionSet.this.respBoObj);
        dialog.open();
      }
    };
    // use edit image
    this.editWrkPkgAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));
    // enable this button based on current user access and when a row is selected
    this.editWrkPkgAction.setEnabled(
        (PIDCWorkPkgActionSet.this.wrkPkgPage.getSelectedRow() != null) && this.wrkPkgPage.hasAccessRights());
    this.editWrkPkgAction.setToolTipText("Edit Work Package");
    toolbarManager.add(this.editWrkPkgAction);
  }


  /**
   * @return the addAction
   */
  public Action getAddAction() {
    return this.addWrkPkgAction;
  }


  /**
   * @param addAction the addAction to set
   */
  public void setAddAction(final Action addAction) {
    this.addWrkPkgAction = addAction;
  }


  /**
   * @return the editAction
   */
  public Action getEditAction() {
    return this.editWrkPkgAction;
  }


  /**
   * @param editAction the editAction to set
   */
  public void setEditAction(final Action editAction) {
    this.editWrkPkgAction = editAction;
  }

}
