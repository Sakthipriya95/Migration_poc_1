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
import com.bosch.caltool.icdm.client.bo.apic.AttributesDataHandler;
import com.bosch.caltool.icdm.common.ui.dialogs.AddNewUserDialog;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueDummy;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.UserServiceClient;

/**
 * @author NIP4COB
 */
public class AddUserAsValueDialog extends AddNewUserDialog {

  /**
   * String constant for select user
   */
  private static final String SELECT_USER = "Select User";
  /**
   * Selected user
   */
  private User selectedUser;
  /**
   * Existing attribute values
   */
  private final Set<AttributeValue> existingValues;
  /**
   * Selected attribute
   */
  private final Attribute selectedAttr;
  /**
   * Created attr value
   */
  private AttributeValue createdValue;


  /**
   * @param shell shell
   * @param enableMultipleSel enableMultipleSel
   * @param attributesDataHandler attributesDataHandler
   * @param attribute selected attr
   */
  public AddUserAsValueDialog(final Shell shell, final boolean enableMultipleSel,
      final AttributesDataHandler attributesDataHandler, final Attribute attribute) {
    super(shell, SELECT_USER, SELECT_USER, "This is to add new value", "Select", enableMultipleSel, false);
    this.existingValues = attributesDataHandler.getAttrValues();
    this.selectedAttr = attribute;
  }


  /**
   * @param activeShell activeShell
   * @param enableMultipleSel enable Multiple Selection
   * @param existingValues existing Attr Values
   * @param attribute selected attr
   */
  public AddUserAsValueDialog(final Shell activeShell, final boolean enableMultipleSel,
      final Set<AttributeValue> existingValues, final Attribute attribute) {
    super(activeShell, SELECT_USER, SELECT_USER, "This is to add new value", "Select", enableMultipleSel, false);
    this.existingValues = existingValues;
    this.selectedAttr = attribute;
  }

  /**
   * This method sets the input to the addNewUserTableViewer
   */
  @Override
  protected void setTabViewerInput() {
    setTabViwerWSInput(getNewUsers(getAllApicUsers(this.includeMonicaAuditor)));
  }

  /**
   * This method removes the users already added to the list from Add Users Dialog
   */
  private SortedSet<User> getNewUsers(final SortedSet<User> apicUsers) {
    apicUsers.removeIf(user -> (Boolean.compare(checkForDuplicate(user.getId()), true) == 0));
    return apicUsers;
  }

  private boolean checkForDuplicate(final Long valToCreate) {
    for (AttributeValue attributeValue : this.existingValues) {
      if (!(attributeValue instanceof AttributeValueDummy) &&
          (Long.compare(attributeValue.getUserId(), valToCreate) == 0)) {
        return true;
      }
    }
    return false;
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
      CDMLogger.getInstance().error(exp.getMessage(), exp);
    }
    return apicUsers;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addMultipleUsers(final List<User> userList) {
    // NA
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addUsers(final User user) {
    setSelectedUser(user);
    AttributeValue valToCreate = new AttributeValue();
    valToCreate.setUserId(this.selectedUser.getId());
    valToCreate.setTextValueEng(this.selectedUser.getDescription());
    valToCreate.setAttributeId(this.selectedAttr.getId());

    try {
      this.createdValue = new AttributeValueServiceClient().create(valToCreate);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
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
   * @return the createdValue
   */
  public AttributeValue getCreatedValue() {
    return this.createdValue;
  }


  /**
   * @param createdValue the createdValue to set
   */
  public void setCreatedValue(final AttributeValue createdValue) {
    this.createdValue = createdValue;
  }

}
