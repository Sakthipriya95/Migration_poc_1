/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.icdm.client.bo.fc2wp.FC2WPDefBO;
import com.bosch.caltool.icdm.common.ui.dialogs.AddNewUserDialog;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.User;

/**
 * @author dja7cob
 */
public class FC2WPContactPersonDialog extends AddNewUserDialog {

  // List of selected users
  List<User> selectedUsers = new ArrayList<User>();

  User selectedUser;

  private final FC2WPDefBO fc2wpDefBO;

  /**
   * @param shell
   * @param map
   * @param b
   * @param string4
   * @param string3
   * @param string2
   * @param string
   */
  public FC2WPContactPersonDialog(final Shell shell, final String shellTitle, final String dialogTitle,
      final String message, final String okBtnLabel, final boolean enableMultipleSel, final FC2WPDefBO fc2wpDefBO) {
    super(shell, shellTitle, dialogTitle, message, okBtnLabel, enableMultipleSel, false);
    this.fc2wpDefBO = fc2wpDefBO;
  }


  @Override
  public SortedSet<User> getAllApicUsers(final boolean includeMonicaAuditor) {

    SortedSet<User> fc2wpApicUsers = new TreeSet<>();

    fc2wpApicUsers.add(createDummyUser());

    for (NodeAccess nodeAccessRight : this.fc2wpDefBO.getAccessRights()) {
      User apicUser;
      apicUser = this.fc2wpDefBO.getNodeAccessWithUserInfo().getUserMap().get(nodeAccessRight.getUserId());
      if (null != apicUser) {
        fc2wpApicUsers.add(apicUser);
      }
    }
    return fc2wpApicUsers;
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
