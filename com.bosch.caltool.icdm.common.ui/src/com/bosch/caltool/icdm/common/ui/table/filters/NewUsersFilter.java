/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.user.User;


/**
 * @author mga1cob
 */
public class NewUsersFilter extends AbstractViewerFilter {


  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof User) {

      // when the element is ApicUser
      User apicUser = (User) element;
      // User full name filter
      if (matchText(apicUser.getDescription())) {
        return true;
      }
      // User id filter
      if (matchText(apicUser.getName())) {
        return true;
      }
      // Department filter
      if (matchText(apicUser.getDepartment())) {
        return true;
      }

    }
    return false;
  }
}