/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.sorter;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.client.bo.apic.AttrNValueDependencyClientBO;
import com.bosch.caltool.icdm.client.bo.apic.AttrNValueDependencyClientBO.SortColumns;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * @author dmo5cob
 */
public class AttrDependencyGridTabViewerSorter extends AbstractViewerSorter {

  private int index;
  private static final int DESCENDING = 1;
  private static final int ASCENDING = 0;
  private int direction = ASCENDING;

  @Override
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

    AttrNValueDependency attr1 = (AttrNValueDependency) obj1;
    AttrNValueDependency attr2 = (AttrNValueDependency) obj2;
    AttrNValueDependencyClientBO attr1BO = new AttrNValueDependencyClientBO(attr1);
    AttrNValueDependencyClientBO attr2BO = new AttrNValueDependencyClientBO(attr2);
    int compare;
    switch (this.index) {
      case 0:
        compare = attr1BO.compareTo(attr2BO, SortColumns.SORT_ATTR_DEPEN_NAME);
        break;
      case 1:
        compare = attr1BO.compareTo(attr2BO, SortColumns.SORT_ATTR_DEPEN_VAL);
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

  @Override
  public int getDirection() {
    return this.direction;
  }
}