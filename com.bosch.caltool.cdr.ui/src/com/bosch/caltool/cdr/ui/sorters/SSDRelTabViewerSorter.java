/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.ssd.SSDReleaseIcdmModel;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * @author rgo7cob
 */
public class SSDRelTabViewerSorter extends AbstractViewerSorter {


  /**
   * Index
   */
  private int index;
  /**
   * DESCENDING
   */
  private static final int DESCENDING = 1;
  /**
   * ASCENDING
   */
  private static final int ASCENDING = 0;
  /**
   * direction
   */
  private int direction = ASCENDING;

  /**
   * @param index defines grid tableviewercolumn index
   */
  @Override
  public void setColumn(final int index) {
    // set the direction of sorting
    if (index == this.index) {
      this.direction = 1 - this.direction;
    }
    // Ascending order
    else {
      this.index = index;
      this.direction = ASCENDING;
    }
  }

  @Override
  public int getDirection() {
    return this.direction;
  }


  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
    // get ssdrelease1
    SSDReleaseIcdmModel rel1 = (SSDReleaseIcdmModel) obj1;
    // get ssdrelease2
    SSDReleaseIcdmModel rel2 = (SSDReleaseIcdmModel) obj2;
    int compare;
    switch (this.index) {
      // Column 1
      case 0:
        compare = compareReleases(rel1.getRelease(), rel2.getRelease());
        break;
      // Column 2
      case 1:
        compare = ApicUtil.compare(rel1.getReleaseDate().toString(), rel2.getReleaseDate().toString());
        break;

      case 2:
        compare = ApicUtil.compare(rel1.getReleaseDesc(), rel2.getReleaseDesc());
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

  /**
   * @param string
   * @param string2
   * @return the comparsion in desc order
   */
  private int compareReleases(final String rell, final String rel2) {

    String beforeDec1 = rell.substring(0, rell.indexOf('.'));
    String beforeDec2 = rel2.substring(0, rel2.indexOf('.'));
    Integer relInt1 = Integer.parseInt(beforeDec1);
    Integer relInt2 = Integer.parseInt(beforeDec2);
    if (relInt1.intValue() == relInt2.intValue()) {
      beforeDec1 = rell.substring(rell.indexOf('.') + 1);
      beforeDec2 = rel2.substring(rel2.indexOf('.') + 1);
      relInt1 = Integer.parseInt(beforeDec1);
      relInt2 = Integer.parseInt(beforeDec2);
    }


    return ApicUtil.compare(relInt2, relInt1);

  }


}
