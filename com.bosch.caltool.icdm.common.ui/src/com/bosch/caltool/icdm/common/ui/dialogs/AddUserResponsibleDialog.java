/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.dialogs;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.UserServiceClient;

/**
 * @author elm1cob
 */
public class AddUserResponsibleDialog extends AddNewUserDialog {

  // selected user object
  private User selectedUser;
  // instance of a2lWpInfoBo
  private final A2LWPInfoBO a2lWpInfoBo;

  /**
   * @param shell Shell
   * @param a2lWpInfoBo a2lWpInfoBo
   */
  public AddUserResponsibleDialog(final Shell shell, final A2LWPInfoBO a2lWpInfoBo) {
    super(shell, "Add New User", "Add New User", "This is to add new user responsible", "Add", false, false);
    this.a2lWpInfoBo = a2lWpInfoBo;
  }

  /**
   * Get all apic users {@inheritDoc}
   */
  @Override
  public SortedSet<User> getAllApicUsers(final boolean includeMonicaAuditor) {
    SortedSet<User> apicUsers = new TreeSet<>();
    try {
      apicUsers.addAll(new UserServiceClient().getAll(includeMonicaAuditor));
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp);
    }
    return apicUsers;
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
  public void addUsers(final User user) {// if user is created from PIDC Editor a2lWpInfoBo is null
    if ((null == this.a2lWpInfoBo) || !this.a2lWpInfoBo.isDeletedResp(user.getId())) {
      setSelectedUser(user);
    }
    else {
      CDMLogger.getInstance().errorDialog(CommonUIConstants.DELETED_RESP_ERROR_MSG, Activator.PLUGIN_ID);
      open();
    }}

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

}
