/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.bc.SdomBc;
import com.bosch.caltool.icdm.model.bc.SdomFc;
import com.bosch.caltool.icdm.model.comppkg.CompPkgFc;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * ICDM-748
 *
 * @author bru2cob
 */
public class BCFCSorter extends AbstractViewerSorter {


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
   *
   */
  private int direction = ASCENDING;

  /**
   * {@inheritDoc}
   */
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

    if ((obj1 instanceof CompPkgFc) && (obj2 instanceof CompPkgFc)) {
      // ICdm-1025 Changed Variable name
      final CompPkgFc fc1 = (CompPkgFc) obj1;
      final CompPkgFc fc2 = (CompPkgFc) obj2;
      int compRes;
      switch (this.index) {
        case 1:
          compRes = ApicUtil.compare(fc1.getFcName(), fc2.getFcName());
          break;
        case 2:
          compRes = ApicUtil.compare(fc1.getFcName(), fc2.getFcName());
          break;
        default:
          compRes = 0;
      }
      // If descending order, flip the direction
      if (this.direction == DESCENDING) {
        compRes = -compRes;
      }
      return compRes;
    }

    else if ((obj1 instanceof SdomBc) && (obj2 instanceof SdomBc)) {
      final SdomBc sdomBC1 = (SdomBc) obj1;
      final SdomBc sdomBC2 = (SdomBc) obj2;
      int compRes;
      switch (this.index) {
        case 0:
          compRes = ApicUtil.compare(sdomBC1.getName(), sdomBC2.getName());
          break;
        case 1:
          compRes = ApicUtil.compare(sdomBC1.getDescription(), sdomBC2.getDescription());
          break;
        default:
          compRes = 0;
      }
      // If descending order, flip the direction
      if (this.direction == DESCENDING) {
        compRes = -compRes;
      }
      return compRes;
    }

    else if ((obj1 instanceof SdomFc) && (obj2 instanceof SdomFc)) {
      final SdomFc sdomFC1 = (SdomFc) obj1;
      final SdomFc sdomFC2 = (SdomFc) obj2;
      int compRes;
      switch (this.index) {
        case 0:
          compRes = ApicUtil.compare(sdomFC1.getName(), sdomFC2.getName());
          break;
        case 1:
          compRes = ApicUtil.compare(sdomFC1.getDescription(), sdomFC2.getDescription());
          break;
        default:
          compRes = 0;
      }
      // If descending order, flip the direction
      if (this.direction == DESCENDING) {
        compRes = -compRes;
      }
      return compRes;
    }
    return 0;
  }


  /**
   * @return int defines direction
   */
  @Override
  public int getDirection() {
    return this.direction;
  }
}
