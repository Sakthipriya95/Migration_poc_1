/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.comppkg.ui.Activator;
import com.bosch.caltool.comppkg.ui.dialogs.ComponentPackageDialog;
import com.bosch.caltool.comppkg.ui.editors.ComponentPackageEditor;
import com.bosch.caltool.comppkg.ui.editors.ComponentPackageEditorInput;
import com.bosch.caltool.comppkg.ui.editors.RulesEditorCustomization;
import com.bosch.caltool.comppkg.ui.views.ComponentPackagesListViewPart;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.caltool.icdm.model.comppkg.CompPkgType;
import com.bosch.caltool.icdm.ruleseditor.actions.ReviewActionSet;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditorInput;
import com.bosch.caltool.icdm.ws.rest.client.comppkg.CompPkgServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author dmo5cob
 */
public enum ComponentPackageActionSet {

                                       /**
                                        *
                                        */
                                       INSTANCE;

  /**
   * Delete action instance
   */
  private IAction deleteAction;
  /**
   * Add action instance
   */
  private IAction addAction;
  /**
   * Edit action instance
   */
  private Action editAction;

  private static final String CDFX_EXTENSION = ".cdfx";

  /**
   * Default error count
   */
  public static final int NO_ERR_COUNT = 0;


  /**
   * @return instance of ComponentPackageActionSet
   */
  public static ComponentPackageActionSet getInstance() {
    return INSTANCE;
  }

  /**
   * This method is responsible to open the cmp package editor
   *
   * @param compPkg component package instance
   * @return editor instance
   */
  public ComponentPackageEditor openCompPkgEditorWithCompPkgInstance(final CompPackage compPkg) {

    final ComponentPackageEditorInput input = new ComponentPackageEditorInput(compPkg);

    ComponentPackageEditor cmpPkgEditor = null;
    try {
      IEditorPart openEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input,
          ComponentPackageEditor.EDITOR_ID);
      cmpPkgEditor = (ComponentPackageEditor) openEditor;
      cmpPkgEditor.getCmpDetailsPage().setStatusBarMessage(cmpPkgEditor.getCmpDetailsPage().getBcTableViewer());

    }
    catch (PartInitException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return cmpPkgEditor;
  }

  /**
   * This method deletes a selected row in compoent package view
   *
   * @param cpListView viewpart
   * @return IAction
   */
  public IAction createCmpPkgDelAction(final ComponentPackagesListViewPart cpListView) {

    this.deleteAction = new Action("Delete") {

      @Override
      public void run() {
        try {
          CompPackage selectedCmpPkg = cpListView.getSelectedCmpPkg();
          CompPackage forService = selectedCmpPkg.clone();
          forService.setDeleted(true);
          new CompPkgServiceClient().update(forService);
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }

      }
    };
    // Set image for delete action
    this.deleteAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
    this.deleteAction.setEnabled(false);
    return this.deleteAction;
  }

  /**
   * This method adds a compoent package
   *
   * @param cpListView viewpart
   * @return IAction
   */
  public IAction createCmpPkgAddAction(final ComponentPackagesListViewPart cpListView) {

    this.addAction = new Action("Add") {

      @Override
      public void run() {
        ComponentPackageDialog addCpAction =
            new ComponentPackageDialog(Display.getCurrent().getActiveShell(), true, cpListView);
        int returnValue = addCpAction.open();
        if (returnValue == 0) {
          openCompPkgEditorAfterCreation(cpListView);
        }
      }
    };
    // Set image for add action
    this.addAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));
    boolean hasAccess = false;
    try {
      hasAccess = new CurrentUserBO().canCreateCompPackage();
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    this.addAction.setEnabled(hasAccess);

    return this.addAction;
  }

  /**
   * This method is responsilbe to open the comp pkg editor based on the selection of the treeviewer
   *
   * @param cpListView instance
   */
  private void openCompPkgEditorAfterCreation(final ComponentPackagesListViewPart cpListView) {
    final CompPackage selection = cpListView.getSelectedCmpPkg();
    if (selection != null) {
      openEditor(selection);
    }
  }

  /**
   * ICDM-985 opens the component package editor
   *
   * @param menuMgr MenuManager
   * @param cmpPkg CompPkg
   */
  public void openCompPkgEditorFromA2lEditor(final MenuManager menuMgr, final CompPackage cmpPkg) {
    Action openEditorAction = new Action("Open Component Package Specification", SWT.NONE) {

      @Override
      public void run() {
        openEditor(cmpPkg);
      }
    };

    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.CMP_PKG_16X16);
    openEditorAction.setImageDescriptor(imageDesc);
    menuMgr.add(openEditorAction);
  }

  /**
   * This method edits a compoent package
   *
   * @param cpListView viewpart
   * @return IAction
   */
  public IAction createCmpPkgEditAction(final ComponentPackagesListViewPart cpListView) {

    this.editAction = new Action("Edit") {

      @Override
      public void run() {
        ComponentPackageDialog dialog =
            new ComponentPackageDialog(Display.getCurrent().getActiveShell(), false, cpListView);
        dialog.open();
      }
    };

    // Set image for edit action
    this.editAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));

    this.editAction.setEnabled(false);

    return this.editAction;
  }


  /**
   * @return the deleteAction
   */
  public IAction getDeleteAction() {
    return this.deleteAction;
  }


  /**
   * @param deleteAction the deleteAction to set
   */
  public void setDeleteAction(final IAction deleteAction) {
    this.deleteAction = deleteAction;
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

  /**
   * @param selectedCompPkg CompPkg
   */
  public void openEditor(final CompPackage selectedCompPkg) {

    if (CompPkgType.getType(selectedCompPkg.getCompPkgType()) == CompPkgType.NE) {
      RulesEditorCustomization obj = new RulesEditorCustomization(selectedCompPkg);
      ReviewParamEditorInput rvwInput = new ReviewParamEditorInput(selectedCompPkg, obj);
      ReviewActionSet reviewActionSet = new ReviewActionSet();
      try {
        reviewActionSet.openRulesEditor(rvwInput, null);
      }
      catch (PartInitException excep) {
        CDMLogger.getInstance().error(excep.getLocalizedMessage(), excep, Activator.PLUGIN_ID);
      }
    }
    else {
      final ComponentPackageEditor openCmpPkgEditor = openCompPkgEditorWithCompPkgInstance(selectedCompPkg);
      openCmpPkgEditor.setFocus();

    }

    CDMLogger.getInstance().debug("Component Package {} opened in the editor", selectedCompPkg.getName());

  }
}
