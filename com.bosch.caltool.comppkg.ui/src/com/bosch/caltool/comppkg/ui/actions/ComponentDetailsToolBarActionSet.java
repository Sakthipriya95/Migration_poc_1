/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.comppkg.ui.Activator;
import com.bosch.caltool.comppkg.ui.dialogs.AddBcFcDialog;
import com.bosch.caltool.comppkg.ui.editors.pages.ComponentDetailsPage;
import com.bosch.caltool.icdm.client.bo.comppkg.ComponentPackageEditorDataHandler;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.comppkg.CompPkgFc;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;


/**
 * ICDM-748
 *
 * @author bru2cob
 */
public class ComponentDetailsToolBarActionSet {

  /**
   * Constant for delete
   */
  private static final String DELETE = "Delete";
  /**
   * add new user action
   */
  private Action addUser;
  /**
   * delete user action
   */
  private Action deleteUser;
  private Action addBCFCDetails;
  private Action deleteBCDetails;
  private Action deleteBCFCDetails;
  private Action addBCDetails;


  /**
   * to add BC
   *
   * @param toolBarManager toolBarManager
   * @param detailsPage detailsPage
   */
  public void addBCAction(final ToolBarManager toolBarManager, final ComponentDetailsPage detailsPage) {
    this.addBCDetails = new Action() {

      @Override
      public void run() {
        if (detailsPage.getDataHandler().canAddBCs()) {
          AddBcFcDialog addBcAction =
              new AddBcFcDialog(Display.getCurrent().getActiveShell(), true, false, detailsPage);
          addBcAction.open();
        }
        else {
          MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Max limit exceeded",
              "No more BCs can be added!");
        }
      }
    };
    this.addBCDetails.setText("Add");

    this.addBCDetails.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));

    this.addBCDetails
        .setEnabled((null != detailsPage.getSelectedCmpPkg()) && detailsPage.getDataHandler().isModifiable());

    toolBarManager.add(this.addBCDetails);

  }

  /**
   * to add FC
   *
   * @param toolBarManager toolBarManager
   * @param detailsPage detailsPage
   */
  public void addBCFCAction(final ToolBarManager toolBarManager, final ComponentDetailsPage detailsPage) {
    this.addBCFCDetails = new Action() {

      @Override
      public void run() {
        if (detailsPage.getSelCompPkgBC() == null) {
          CDMLogger.getInstance().warnDialog("Select a BC to continue", Activator.PLUGIN_ID);
          return;
        }
        AddBcFcDialog addFcAction = new AddBcFcDialog(Display.getCurrent().getActiveShell(), false, true, detailsPage);
        addFcAction.open();
      }
    };
    this.addBCFCDetails.setText("Add");

    this.addBCFCDetails.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));

    this.addBCFCDetails.setEnabled((null != detailsPage.getSelectedCmpPkg()) &&
        detailsPage.getDataHandler().isModifiable() && (null != detailsPage.getSelCompPkgBC()));

    toolBarManager.add(this.addBCFCDetails);

  }

  /**
   * to delete BC
   *
   * @param toolBarManager toolBarManager
   * @param detailsPage detailsPage
   */
  public void deleteBCAction(final ToolBarManager toolBarManager, final ComponentDetailsPage detailsPage) {
    this.deleteBCDetails = new Action() {

      @Override
      public void run() {
        ComponentPackageEditorDataHandler pkgBO = detailsPage.getDataHandler();
        try {

          if ((null != detailsPage.getDataHandler().getCompPkgData().getFcMap()
              .get(detailsPage.getSelCompPkgBC().getId())) &&
              !detailsPage.getDataHandler().getCompPkgData().getFcMap().get(detailsPage.getSelCompPkgBC().getId())
                  .isEmpty()) {
            MessageDialogUtils.getWarningMessageDialog("Warning", "The funtions mapped to the BC : " +
                detailsPage.getSelCompPkgBC().getBcName() + " would also be deleted");
          }
          pkgBO.deleteBC(detailsPage.getSelCompPkgBC());
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }
      }
    };
    this.deleteBCDetails.setText(DELETE);

    this.deleteBCDetails.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));

    this.deleteBCDetails.setEnabled((null != detailsPage.getSelectedCmpPkg()) &&
        detailsPage.getDataHandler().isModifiable() && (null != detailsPage.getSelCompPkgBC()));

    toolBarManager.add(this.deleteBCDetails);

  }

  /**
   * to delte FC
   *
   * @param toolBarManager toolBarManager
   * @param detailsPage detailsPage
   */
  public void deleteBCFCAction(final ToolBarManager toolBarManager, final ComponentDetailsPage detailsPage) {
    this.deleteBCFCDetails = new Action() {

      @Override
      public void run() {
        CompPkgFc selectedFc = detailsPage.getSelCompPkgBCFC();
        if (selectedFc == null) {
          CDMLogger.getInstance().warnDialog("Select a Function to continue", Activator.PLUGIN_ID);
          return;
        }
        try {
          ComponentPackageEditorDataHandler pkgBO = detailsPage.getDataHandler();
          pkgBO.deleteFC(selectedFc);
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }
      }
    };
    this.deleteBCFCDetails.setText(DELETE);

    this.deleteBCFCDetails.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));

    this.deleteBCFCDetails
        .setEnabled(((null != detailsPage.getSelectedCmpPkg()) && detailsPage.getDataHandler().isModifiable() &&
            (null != detailsPage.getSelCompPkgBC()) && (null != detailsPage.getSelCompPkgBCFC())));

    toolBarManager.add(this.deleteBCFCDetails);

  }


  /**
   * @return the addBCFCDetails
   */
  public Action getAddBCFCDetails() {
    return this.addBCFCDetails;
  }


  /**
   * @return the deleteBCDetails
   */
  public Action getDeleteBCDetails() {
    return this.deleteBCDetails;
  }


  /**
   * @return the deleteBCFCDetails
   */
  public Action getDeleteBCFCDetails() {
    return this.deleteBCFCDetails;
  }


  /**
   * @return the addBCDetails
   */
  public Action getAddBCDetails() {
    return this.addBCDetails;
  }


  /**
   * @return the addUser
   */
  public Action getAddUser() {
    return this.addUser;
  }


  /**
   * @return the deleteUser
   */
  public Action getDeleteUser() {
    return this.deleteUser;
  }

}
