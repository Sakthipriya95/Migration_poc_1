/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.sorter;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import com.bosch.caltool.authentication.ldap.UserInfo;
import com.bosch.caltool.icdm.common.util.ApicUtil;


/**
 * @author rgo7cob ICdm-1127 - Sorter for user info
 */
public class LdapUserGridTableViewerSorter extends ViewerSorter {


  /**
   *
   */
  private int index;
  /**
   *
   */
  private static final int DESCENDING = 1;
  /**
   *
   */
  private static final int ASCENDING = 0;
  /**
   * Default direction
   */
  private int direction = ASCENDING;

  /**
   * Full name index
   */
  private static final int FULL_NAME_IDX = 0;

  /**
   * User id index
   */
  private static final int USR_ID_IDX = 1;

  /**
   * dep name index
   */
  private static final int DEP_NAME_IDX = 2;

  /**
   * @param index defines tableviewercolumn index
   */
  public void setColumn(final int index) {
    if (index == this.index) {
      this.direction = 1 - this.direction;
    }
    else {
      this.index = index;
      this.direction = ASCENDING;
    }
  }

  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
    // User info Obj1
    UserInfo userInfo1 = (UserInfo) obj1;
    // User info Obj2
    UserInfo userInfo2 = (UserInfo) obj2;
    // Compare Result
    int result;
    switch (this.index) {
      case FULL_NAME_IDX:
        // Sort for full name
        result = ApicUtil.compare(ApicUtil.getFullName(userInfo1.getGivenName(), userInfo1.getSurName()),
            ApicUtil.getFullName(userInfo2.getGivenName(), userInfo2.getSurName()));
        break;
      case USR_ID_IDX:
        // Sort for user id
        result = ApicUtil.compare(userInfo1.getUserName(), userInfo2.getUserName());
        break;
      case DEP_NAME_IDX:
        // Sort for Department
        result = ApicUtil.compare(userInfo1.getDepartment(), userInfo2.getDepartment());
        break;

      default:
        result = 0;
    }
    if (result == 0) {
      result = ApicUtil.compare(userInfo1.getUserName(), userInfo2.getUserName());
    }
    // If descending order, flip the direction
    if (this.direction == DESCENDING) {
      result = -result;
    }
    return result;
  }

  /**
   * @return int defines direction
   */
  public int getDirection() {
    return this.direction;
  }


}
