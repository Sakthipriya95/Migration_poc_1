/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.views.providers;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.user.User;


/**
 * @author mga1cob
 */
public class NewUserLabelProvider implements ITableLabelProvider {

  /**
   * {@inheritDoc}
   */
  @Override
  public void addListener(final ILabelProviderListener listener) {
    // TO-DO
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    // TO-DO
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isLabelProperty(final Object element, final String property) {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeListener(final ILabelProviderListener listener) {
    // TO-DO
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Image getColumnImage(final Object element, final int columnIndex) {
    return null;
  }

  @Override
  public String getColumnText(final Object element, final int columnIndex) {
    String value = null;
    if (element instanceof User) {
      User user = (User) element;
      switch (columnIndex) {
        case 0:
          // get user full name
          value = getFullName(user);
          break;
        case 1:
          // get user name
          value = user.getName();
          break;

        case 2:
          // get user department
          value = user.getDepartment();
          break;
        default:
          value = "";
          break;
      }
    }
    return value;
  }

  /**
   * Get the full name of the user The full name is the lastName concatenated with the firstName
   *
   * @param user user object
   * @return the users fullName
   */
  public String getFullName(final User user) {
    if (user != null) {
      final StringBuilder fullName = new StringBuilder();
      if (!CommonUtils.isEmptyString(user.getLastName())) {
        fullName.append(user.getLastName()).append(", ");
      }
      if (!CommonUtils.isEmptyString(user.getFirstName())) {
        fullName.append(user.getFirstName());
      }
      if (CommonUtils.isEmptyString(fullName.toString())) {
        fullName.append(user.getName());
      }
      return fullName.toString();
    }
    // return emtpy strig is there is no user
    return ApicConstants.EMPTY_STRING;
  }
}
