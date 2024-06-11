/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.editors.pages.NodeAccessRightsPage;
import com.bosch.caltool.icdm.client.bo.general.NodeAccessPageDataHandler;
import com.bosch.caltool.icdm.common.ui.dialogs.AddNewUserDialog;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.NodeAccessServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.general.UserServiceClient;

/**
 * This dialog adds new user access
 *
 * @author NIP4COB
 */
public class AddNewUserAccessDialog extends AddNewUserDialog {

  /**
   * NodeAccessServiceClient instance
   */
  NodeAccessServiceClient client;
  /**
   * NodeAccessRightsPage instance
   */
  private final NodeAccessRightsPage rightsPage;
  /**
   * NodeAccessPageDataHandler instance
   */
  private final NodeAccessPageDataHandler nodeAccessBO;

  /**
   * Constructor
   *
   * @param parentShell shell
   * @param nodeAccessBO NodeAccessBO
   * @param rightsPage nodeAccessRightsPage
   */
  public AddNewUserAccessDialog(final Shell parentShell, final NodeAccessPageDataHandler nodeAccessBO,
      final NodeAccessRightsPage rightsPage) {
    super(parentShell);
    // initialise the fields
    this.nodeAccessBO = nodeAccessBO;
    this.rightsPage = rightsPage;
  }


  /**
   * {@inheritDoc} this methdo gets all apic users
   */
  @Override
  public SortedSet<User> getAllApicUsers(final boolean includeMonicaAuditor) {
    SortedSet<User> retUsers = new TreeSet<>();
    Set<Long> nodeIdSet = new TreeSet<>();
    nodeIdSet.add(this.nodeAccessBO.getNodeId());
    try {
      // call web service to get all nodes
      retUsers = new UserServiceClient().getAllByNode(this.nodeAccessBO.isIncludeApicWriteUsers(),
          this.nodeAccessBO.isIncludeMonicaAuditor(), this.nodeAccessBO.getNodeType(), this.nodeAccessBO.getNodeId());
    }
    catch (ApicWebServiceException e) {
      // throw error in dialog in case of exception from web service
      CDMLogger.getInstance().errorDialog(
          "Error in retrieving users for " + this.nodeAccessBO.getNodeType().getTypeName(), e, Activator.PLUGIN_ID);
    }
    return retUsers;
  }


  /**
   * This method adds new user
   *
   * @param user User
   */
  private void addNewUser(final User user) {
    // set the user and node details
    NodeAccess access = new NodeAccess();
    access.setNodeId(this.nodeAccessBO.getNodeId());
    access.setNodeType(this.nodeAccessBO.getNodeType().getTypeCode());
    access.setUserId(user.getId());
    access.setRead(true);
    NodeAccess accCreated = null;
    try {
      // call webservice to add new user to the node
      accCreated = new NodeAccessServiceClient().create(access);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog("Error in adding user to access rights page : " + e.getMessage(), e,
          Activator.PLUGIN_ID);
    }
    if ((this.rightsPage.getRightsTabViewer() != null) &&
        !this.rightsPage.getRightsTabViewer().getGrid().isDisposed() && (accCreated != null)) {
      // set the selection to the newly added user
      this.rightsPage.setSelectedNodeAccessId(accCreated.getId());
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
    if (this.rightsPage != null) {
      addNewUser(user);
    }
  }
}
