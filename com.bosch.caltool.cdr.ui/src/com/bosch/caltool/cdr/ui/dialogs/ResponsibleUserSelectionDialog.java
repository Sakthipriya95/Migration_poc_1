/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.icdm.common.ui.dialogs.AddNewUserDialog;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.UserServiceClient;

/**
 * @author bru2cob
 */
public class ResponsibleUserSelectionDialog extends AddNewUserDialog {

  // ICDM-525

  // List of selected users
  List<User> selectedUsers = new ArrayList<User>();

  User selectedUser;

  /**
   * @param shell
   * @param shellTitle
   * @param dialogTitle
   * @param message
   */
  public ResponsibleUserSelectionDialog(final Shell shell, final String shellTitle, final String dialogTitle,
      final String message, final String okBtnLabel, final boolean enableMultipleSel) {
    super(shell, shellTitle, dialogTitle, message, okBtnLabel, enableMultipleSel, false);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<User> getAllApicUsers(final boolean includeMonicaAuditor) {
    // Story 221802
    SortedSet<User> apicUsers = new TreeSet<>();
    try {
      apicUsers.addAll(new UserServiceClient().getAll(includeMonicaAuditor));
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp);    }
    return apicUsers;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addUsers(final User user) {
    setSelectedUser(user);
  }

  /**
   * @return the selectedUser
   */
  public User getSelectedUser() {
    return this.selectedUser;
  }


  /**
   * @param selectedUser the selectedUser to set
   */
  @Override
  public void setSelectedUser(final User selectedUser) {
    this.selectedUser = selectedUser;
  }

  /**
   * @return the selected multiple users
   */
  public List<User> getSelectedMultipleUser() {
    return this.selectedUsers;
  }


  /**
   * @param selectedUsers the selected multiple User to set
   */
  public void setSelectedMultipleUser(final List<User> selectedUsers) {
    if (selectedUsers == null) {
      this.selectedUsers.clear();
    }
    else {
      this.selectedUsers = selectedUsers;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addMultipleUsers(final List<User> selectedUsers2) {
    setSelectedMultipleUser(selectedUsers2);
  }

}
