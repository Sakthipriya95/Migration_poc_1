/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.table.filters;

import com.bosch.caltool.authentication.ldap.UserInfo;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.util.ApicUtil;


/**
 * @author rgo7cob
 * Icdm-1127 - Filter for user info
 */
public class LdapUsersFilter extends AbstractViewerFilter {


  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof UserInfo) {
      UserInfo userInfo = (UserInfo) element;
      // Department filter
      if (matchText(userInfo.getDepartment())) {
        return true;
      }
      // User id filter
      if (matchText(userInfo.getUserName())) {
        return true;
      }
      // Full name Filter
      if (matchText(ApicUtil.getFullName(userInfo.getGivenName(), userInfo.getSurName()))) {
        return true;
      }

    }
    return false;
  }


}