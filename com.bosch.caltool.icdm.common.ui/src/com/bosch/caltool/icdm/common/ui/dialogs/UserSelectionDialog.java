/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.UserServiceClient;

/**
 * @author bru2cob
 */
public class UserSelectionDialog extends AddNewUserDialog {

  // ICDM-525

  // List of selected users
  List<User> selectedUsers = new ArrayList<>();
  // selected user
  User selectedUser;
  // true if dummy user to be added
  private boolean addDummyUser;

  /**
   * @param shell Shell
   * @param shellTitle String
   * @param dialogTitle String
   * @param message String
   * @param okBtnLabel String
   * @param enableMultipleSel boolean
   * @param includeMonicaAuditor boolean
   */
  public UserSelectionDialog(final Shell shell, final String shellTitle, final String dialogTitle, final String message,
      final String okBtnLabel, final boolean enableMultipleSel, final boolean includeMonicaAuditor) {
    super(shell, shellTitle, dialogTitle, message, okBtnLabel, enableMultipleSel, includeMonicaAuditor);
  }

  private User createDummyUser() {
    User user = new User();
    user.setDepartment(ApicConstants.EMPTY_STRING);
    user.setDescription(ApicConstants.EMPTY_STRING);
    user.setFirstName(ApicConstants.EMPTY_STRING);
    user.setLastName(ApicConstants.EMPTY_STRING);
    user.setName(ApicConstants.EMPTY_STRING);
    user.setId(ApicConstants.APIC_DUMMY_USER_ID);
    user.setVersion(null);
    return user;
  }


  @Override
  public SortedSet<User> getAllApicUsers(final boolean includeMonicaAuditor) {
    final SortedSet<User> retUsers = new TreeSet<>();
    try {
      if (isAddDummyUser()) {
        retUsers.add(createDummyUser());
      }
      retUsers.addAll(new UserServiceClient().getAll(includeMonicaAuditor));
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

    return retUsers;
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
    if (selectedUsers != null) {
      this.selectedUsers = selectedUsers;
    }
    else {
      this.selectedUsers.clear();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addMultipleUsers(final List<User> selectedUsers2) {
    setSelectedMultipleUser(selectedUsers2);
  }


  /**
   * @return the addDummyUser
   */
  public boolean isAddDummyUser() {
    return this.addDummyUser;
  }


  /**
   * @param addDummyUser the addDummyUser to set
   */
  public void setAddDummyUser(final boolean addDummyUser) {
    this.addDummyUser = addDummyUser;
  }

}
