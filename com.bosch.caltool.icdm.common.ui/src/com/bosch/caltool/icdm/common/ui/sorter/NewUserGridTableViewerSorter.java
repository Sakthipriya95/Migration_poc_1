/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.sorter;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import com.bosch.caltool.icdm.model.user.User;


/**
 * Sorter class for new user grid table
 *
 * @author mga1cob
 */
public class NewUserGridTableViewerSorter extends ViewerSorter {


  /**
   * index
   */
  private int index;
  /**
   * constant for descending direction
   */
  private static final int DESCENDING = 1;
  /**
   * constant for ascending direction
   */
  private static final int ASCENDING = 0;
  /**
   * direction by default set to ascending
   */
  private int direction = ASCENDING;

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

  /**
   * @return int defines direction
   */
  public int getDirection() {
    return this.direction;
  }


  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
    // set the ApicUser objects
    User user1 = (User) obj1;
    User user2 = (User) obj2;
    int compare;
    switch (this.index) {
      case 0:
        // compare user full name
        compare = user1.compareTo(user2, User.SortColumns.SORT_FULL_NAME);
        break;
      case 1:
        // compare user name
        compare = user1.compareTo(user2, User.SortColumns.SORT_USER_NAME);
        break;
      case 2:
        // compare user dept
        compare = user1.compareTo(user2, User.SortColumns.SORT_DEPARTMENT);
        break;

      default:
        compare = 0;
    }
    // If descending order, flip the direction
    if (this.direction == DESCENDING) {
      compare = -compare;
    }
    return compare;
  }

}
