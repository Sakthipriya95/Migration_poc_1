/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.admin.ui.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.admin.ui.Activator;
import com.bosch.caltool.admin.ui.dialog.NodeImportDialog;
import com.bosch.caltool.admin.ui.dialog.NodeSelectionDialog;
import com.bosch.caltool.admin.ui.editors.pages.MultiNodeAccessPage;
import com.bosch.caltool.icdm.common.ui.dialogs.UserSelectionDialog;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.NodeAccessInfo;
import com.bosch.caltool.icdm.model.user.NodeAccessWithUserInput;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.NodeAccessServiceClient;

/**
 * @author say8cob
 */
public class MultiNodeAccessPageActionSet {

  private final MultiNodeAccessPage adminMultiNodeAccessPage;


  /**
  *
  */
  private static final String ADD_USER = "Add User(s)";

  /**
  *
  */
  private static final String SELECT_USER = "Select Users";

  private Action addUserAction;

  private Action deleteUserAction;

  private Action saveAction;

  private static final boolean ENABLE = true;

  private static final boolean DISABLE = false;

  /**
   * @param adminMultiNodeAccessPage instance
   */
  public MultiNodeAccessPageActionSet(final MultiNodeAccessPage adminMultiNodeAccessPage) {
    this.adminMultiNodeAccessPage = adminMultiNodeAccessPage;
  }

  /**
   * to add button listeners for All Read, All Write , All Grant, All Owner
   */
  public void addAllNodeAccessBtnListener() {

    this.adminMultiNodeAccessPage.getAllReadBtn().addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent arg0) {

        for (NodeAccessInfo nodeAccessInfo : MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage
            .getNodeTableInputSet()) {
          nodeAccessInfo.getAccess().setRead(ENABLE);
          nodeAccessInfo.getAccess().setWrite(DISABLE);
          nodeAccessInfo.getAccess().setOwner(DISABLE);
          nodeAccessInfo.getAccess().setGrant(DISABLE);
        }
        MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage.getNodeTableViewer().refresh();

      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // Not Implemented

      }
    });

    this.adminMultiNodeAccessPage.getAllWriteBtn().addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        for (NodeAccessInfo nodeAccessInfo : MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage
            .getNodeTableInputSet()) {
          nodeAccessInfo.getAccess().setRead(ENABLE);
          nodeAccessInfo.getAccess().setWrite(ENABLE);
          nodeAccessInfo.getAccess().setOwner(DISABLE);
          nodeAccessInfo.getAccess().setGrant(DISABLE);
        }
        MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage.getNodeTableViewer().refresh();

      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // Not Implemented

      }
    });

    this.adminMultiNodeAccessPage.getAllGrantBtn().addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        for (NodeAccessInfo nodeAccessInfo : MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage
            .getNodeTableInputSet()) {
          nodeAccessInfo.getAccess().setRead(ENABLE);
          nodeAccessInfo.getAccess().setWrite(ENABLE);
          nodeAccessInfo.getAccess().setOwner(DISABLE);
          nodeAccessInfo.getAccess().setGrant(ENABLE);
        }
        MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage.getNodeTableViewer().refresh();

      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // Not Implemented

      }
    });

    this.adminMultiNodeAccessPage.getAllOwnerBtn().addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        for (NodeAccessInfo nodeAccessInfo : MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage
            .getNodeTableInputSet()) {
          nodeAccessInfo.getAccess().setRead(ENABLE);
          nodeAccessInfo.getAccess().setWrite(ENABLE);
          nodeAccessInfo.getAccess().setOwner(ENABLE);
          nodeAccessInfo.getAccess().setGrant(ENABLE);
        }
        MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage.getNodeTableViewer().refresh();

      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // Not Implemented

      }
    });

  }

  /**
   * to add view user btn listener
   */
  public void addViewUserSelectionBtnListener() {
    this.adminMultiNodeAccessPage.getUserNameBwsBtn().addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        final UserSelectionDialog userDialog = new UserSelectionDialog(Display.getCurrent().getActiveShell(),
            SELECT_USER, SELECT_USER, SELECT_USER, "Select", false, false);
        userDialog.setSelectedUser(null);
        userDialog.open();
        User user = userDialog.getSelectedUser();
        if (CommonUtils.isNotNull(user)) {
          updateNodeTblWithNodeAccessForGvnUsr(user);
        }
      }


      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // Not Implemented

      }
    });
  }

  /**
   * @param user
   */
  private void updateNodeTblWithNodeAccessForGvnUsr(final User user) {
    MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage.getUserNameTextField().setText(user.getDescription());
    try {
      Map<Long, NodeAccessInfo> findNodeAccessForGivenUser =
          new NodeAccessServiceClient().findNodeAccessForGivenUser(user.getName());
      Set<NodeAccessInfo> nodeTableInputSet =
          MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage.getNodeTableInputSet();
      nodeTableInputSet.clear();
      nodeTableInputSet.addAll(findNodeAccessForGivenUser.values());
      MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage.getNodeTableViewer().refresh();
      enableDisableButtons(!nodeTableInputSet.isEmpty());

    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * Method for reset editor action
   * 
   * @return action
   */
  public Action resetEditorAction() {
    Action resetEditorAction = new Action() {

      @Override
      public void run() {
        MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage.getUserNameTextField().setText("");
        Set<NodeAccessInfo> nodeTableInputSet =
            MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage.getNodeTableInputSet();
        nodeTableInputSet.clear();
        MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage.getUserTableInputSet().clear();
        enableDisableButtons(true);
        MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage.getNodeTableViewer().refresh();
        MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage.getNewUserTabViewer().refresh();
        // to diable save btn
        MultiNodeAccessPageActionSet.this.saveAction.setEnabled(false);

        enableDisableButtons(!nodeTableInputSet.isEmpty());
      }
    };
    final ImageDescriptor reset = ImageManager.getImageDescriptor(ImageKeys.CLEAR_HIST_16X16);
    resetEditorAction.setImageDescriptor(reset);
    resetEditorAction.setToolTipText("Reset All");
    return resetEditorAction;
  }


  /**
   *
   */
  public void addUserAction() {
    this.addUserAction = new Action(ADD_USER, SWT.NONE) {

      @Override
      public void run() {

        final UserSelectionDialog userDialog = new UserSelectionDialog(Display.getCurrent().getActiveShell(), ADD_USER,
            ADD_USER, ADD_USER, "Add", true, false);
        userDialog.open();

        Set<User> userTableInputSet = MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage.getUserTableInputSet();
        if (null != userDialog.getSelectedMultipleUser()) {
          userTableInputSet.addAll(userDialog.getSelectedMultipleUser());
          MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage.getNewUserTabViewer().refresh();
        }
        // to enable save btn
        enableDiableSaveBtn(!userTableInputSet.isEmpty());
      }
    };
    this.addUserAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));
    this.addUserAction.setEnabled(false);
    this.adminMultiNodeAccessPage.getToolBarManager().add(this.addUserAction);
  }


  /**
   * method to delete user action
   */
  public void deleteUserAction() {
    this.deleteUserAction = new Action("Delete User(s)", SWT.NONE) {

      @Override
      public void run() {
        Set<User> userTableInputSet = MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage.getUserTableInputSet();
        if ((CommonUtils
            .isNotEmpty(MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage.getSelectedUserTableInputSet()))) {
          for (User user : MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage.getSelectedUserTableInputSet()) {
            userTableInputSet.remove(user);
          }
          MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage.getNewUserTabViewer().refresh();
        }
        enableDiableSaveBtn(!userTableInputSet.isEmpty());
      }


    };

    this.deleteUserAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
    this.deleteUserAction.setEnabled(false);
    this.adminMultiNodeAccessPage.getToolBarManager().add(this.deleteUserAction);
  }


  /**
   *
   */
  public void addNodeAction() {
    Action addNodesAction = new Action("Select Node(s)", SWT.NONE) {

      @Override
      public void run() {
        NodeSelectionDialog nodeSelectionDialog = new NodeSelectionDialog(Display.getCurrent().getActiveShell());
        nodeSelectionDialog.open();

        Set<NodeAccessInfo> nodeTableInputSet =
            MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage.getNodeTableInputSet();
        Set<NodeAccessInfo> selectedNodeAccess = nodeSelectionDialog.getSelectedNodeAccess();
        for (NodeAccessInfo nodeAccessInfo : selectedNodeAccess) {
          nodeAccessInfo.getAccess().setRead(true);
          nodeAccessInfo.getAccess().setWrite(true);
          nodeAccessInfo.getAccess().setGrant(false);
          nodeAccessInfo.getAccess().setOwner(false);
        }
        nodeTableInputSet.addAll(selectedNodeAccess);
        enableDisableButtons(!nodeTableInputSet.isEmpty());
        MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage.getNodeTableViewer().refresh();

      }
    };
    addNodesAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));
    this.adminMultiNodeAccessPage.getToolBarManager().add(addNodesAction);
  }

  /**
  *
  */
  public void importNodeAction() {
    Action addNodesAction = new Action("Import Node(s) From File", SWT.NONE) {

      @Override
      public void run() {
        BusyIndicator.showWhile(Display.getDefault().getActiveShell().getDisplay(), () -> {
          NodeImportDialog nodeSelectionDialog = new NodeImportDialog(Display.getCurrent().getActiveShell());
          nodeSelectionDialog.open();

          Set<NodeAccessInfo> nodeTableInputSet =
              MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage.getNodeTableInputSet();
          for (NodeAccessInfo nodeAccessInfo : nodeSelectionDialog.getNodeAccessInfoMap().values()) {
            nodeAccessInfo.getAccess().setRead(true);
            nodeAccessInfo.getAccess().setWrite(true);
            nodeAccessInfo.getAccess().setGrant(false);
            nodeAccessInfo.getAccess().setOwner(false);
            nodeTableInputSet.add(nodeAccessInfo);
          }
          enableDisableButtons(!nodeTableInputSet.isEmpty());
          MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage.getNodeTableViewer().refresh();
        });
      }
    };
    addNodesAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.FILE_IMPORT_16X16));
    this.adminMultiNodeAccessPage.getToolBarManager().add(addNodesAction);

  }


  /**
   *
   */
  public void deleteNodeAction() {
    Action deleteNodesAction = new Action("Remove Node(s)", SWT.NONE) {

      @Override
      public void run() {
        if ((CommonUtils
            .isNotEmpty(MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage.getSelectedNodeTableInputSet()))) {
          for (NodeAccessInfo nodeAccessInfo : MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage
              .getSelectedNodeTableInputSet()) {
            MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage.getNodeTableInputSet().remove(nodeAccessInfo);
          }

          // To Reset All the contents in user table if nodes table is empty

          boolean isNodeSetEmpty =
              MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage.getNodeTableInputSet().isEmpty();
          if (isNodeSetEmpty) {
            MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage.getUserTableInputSet().clear();
            MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage.getNewUserTabViewer().refresh();
          }
          // To enable/diable buttons
          enableDisableButtons(!isNodeSetEmpty);

          MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage.getNodeTableViewer().refresh();
        }
      }

    };

    deleteNodesAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
    this.adminMultiNodeAccessPage.getToolBarManager().add(deleteNodesAction);
  }


  /**
   * Method for reset editor action
   *
   * @return action
   */
  public Action saveEditorAction() {
    this.saveAction = new Action() {

      @Override
      public void run() {
        try {
          boolean openConfirm = MessageDialog.openConfirm(Display.getDefault().getActiveShell(), "Confirmation Dialog",
              "Note : You are about to modify the access rights of the below users which will affect their access in the system.\n" +
                  "Please verify the data and save the operation\n" + "Do you want to proceed with the save?");
          if (openConfirm) {
            createRUpdateMultiNode();
          }
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }
      }
    };
    final ImageDescriptor reset = ImageManager.getImageDescriptor(ImageKeys.SAVE_28X30);
    this.saveAction.setImageDescriptor(reset);
    this.saveAction.setToolTipText("Save");
    this.saveAction.setEnabled(false);
    return this.saveAction;
  }

  /**
   * @throws ApicWebServiceException
   */
  private void createRUpdateMultiNode() throws ApicWebServiceException {
    List<NodeAccessWithUserInput> nodeAccessWithUserInputList = new ArrayList<>();
    for (NodeAccessInfo nodeAccessInfo : MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage
        .getNodeTableInputSet()) {
      for (User user : MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage.getUserTableInputSet()) {
        NodeAccess access = nodeAccessInfo.getAccess().clone();
        access.setUserId(user.getId());
        NodeAccessWithUserInput nodeAccessWithUserInput = new NodeAccessWithUserInput();
        nodeAccessWithUserInput.setNodeAccess(access);
        // To delete the node access
        nodeAccessWithUserInput.setDelete(!access.isRead());
        nodeAccessWithUserInputList.add(nodeAccessWithUserInput);
      }
    }
    new NodeAccessServiceClient().createUpdateMultiNode(nodeAccessWithUserInputList);
    CDMLogger.getInstance().infoDialog("Node Access created successfully.", Activator.PLUGIN_ID);
  }

  /**
   * @param flag if true, enable the buttons
   */
  public void enableDisableButtons(final boolean flag) {
    MultiNodeAccessPageActionSet.this.adminMultiNodeAccessPage.enableDisableAccessBtns(flag);
    this.addUserAction.setEnabled(flag);
    this.deleteUserAction.setEnabled(flag);
  }

  /**
   * @param userTableInputSet
   */
  private void enableDiableSaveBtn(final boolean statusFlag) {
    // to enable/disable save btn
    MultiNodeAccessPageActionSet.this.saveAction.setEnabled(statusFlag);
  }
}
