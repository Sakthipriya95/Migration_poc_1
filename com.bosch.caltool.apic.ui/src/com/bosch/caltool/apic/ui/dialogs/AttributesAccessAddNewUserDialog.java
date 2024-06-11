/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.apic.ui.editors.pages.AttributesRightsPage;
import com.bosch.caltool.icdm.client.bo.general.NodeAccessPageDataHandler;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.dialogs.AddNewUserDialog;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.UserServiceClient;

/**
 * New Class For Atrributes Access Rights Page
 *
 * @author rgo7cob
 */
public class AttributesAccessAddNewUserDialog extends AddNewUserDialog {

  /**
   * Rights page
   */
  private AttributesRightsPage attrRightsPage;
  /**
   * Node access handler instance
   */
  private final NodeAccessPageDataHandler nodeAccessBO;


  /**
   * Constructor
   *
   * @param shell shell
   * @param formPage AbstractFormPage
   * @param nodeAccessBO NodeAccessBO
   */
  public AttributesAccessAddNewUserDialog(final Shell shell, final AbstractFormPage formPage,
      final NodeAccessPageDataHandler nodeAccessBO) {
    super(shell);
    if (formPage instanceof AttributesRightsPage) {
      this.attrRightsPage = (AttributesRightsPage) formPage;
    }
    this.nodeAccessBO = nodeAccessBO;
  }


  @Override
  public SortedSet<User> getAllApicUsers(final boolean includeMonicaAuditor) {
    SortedSet<User> retUsers = new TreeSet<>();
    try {
      retUsers = new UserServiceClient().getAllByNode(this.nodeAccessBO.isIncludeApicWriteUsers(),
          this.nodeAccessBO.isIncludeMonicaAuditor(), this.nodeAccessBO.getNodeType(), this.nodeAccessBO.getNodeId());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog("Error in retrieving users for the node ", e, Activator.PLUGIN_ID);
    }
    return retUsers;
  }


  /**
   * Add New users
   *
   * @param selectedAttribute selectedAttribute
   * @param apicUser apicUser
   */
  private void addNewUser(final User user) {
    if ((this.attrRightsPage.getAccessRghtsTabViewer() != null) &&
        !this.attrRightsPage.getAccessRghtsTabViewer().getGrid().isDisposed()) {
      NodeAccess dummy = new NodeAccess();
      Random random = new Random();
      dummy.setId(random.nextLong());
      dummy.setNodeId(this.nodeAccessBO.getNodeId());
      dummy.setNodeType(this.nodeAccessBO.getNodeType().getTypeCode());
      dummy.setUserId(user.getId());
      dummy.setRead(true);
      dummy.setWrite(false);
      // Create a dummy node access model - will be added to database on clicking OK
      this.attrRightsPage.addToCreateBuffer(dummy, user);
      setSelectionForNewUser(user);
    }
  }


  /**
   * This method provides the selection to the newly added item in tableviewer
   *
   * @param apicUser instance
   * @throws DataException
   */
  private void setSelectionForNewUser(final User user) {
    GridItem[] gridItems;
    gridItems = this.attrRightsPage.getAccessRghtsTabViewer().getGrid().getItems();
    for (GridItem gridItem : gridItems) {
      final Object data = gridItem.getData();
      // if data is of type NodeAccessRight
      if (data instanceof NodeAccess) {
        final NodeAccess nodeAccessRight = (NodeAccess) data;
        if (nodeAccessRight.getUserId().equals(user.getId())) {
          // Get the index of grid item
          final int index = this.attrRightsPage.getAccessRghtsTabViewer().getGrid().getIndexOfItem(gridItem);
          this.attrRightsPage.getAccessRghtsTabViewer().getGrid().setSelection(index);
          this.attrRightsPage.getDeleteUserAction().setEnabled(true);
          break;
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addMultipleUsers(final List<User> userList) {
    // TODO Auto-generated method stub
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addUsers(final User user) {
    // add new users
    if (this.attrRightsPage != null) {
      addNewUser(user);
    }
  }
}
