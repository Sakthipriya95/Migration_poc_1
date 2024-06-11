/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.client.bo.a2l.VariantMapClientModel;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * @author mga1cob
 */
public class MapVariantToVarGrpSorter extends AbstractViewerSorter {

  /**
   * Index
   */
  private int index;
  /**
   * Constant for descending
   */
  private static final int DESCENDING = 1;
  /**
   * Constant for ascending
   */
  private static final int ASCENDING = 0;
  /**
   * Default ascending
   */
  private int direction = ASCENDING;

  /**
   * @param index defines tableviewer column index
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

  /**
   * {@inheritDoc} Compare Method for Attr Value
   */
  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
    // Get the Attr Value objects.
    VariantMapClientModel mapping1 = (VariantMapClientModel) obj1;
    VariantMapClientModel mapping2 = (VariantMapClientModel) obj2;
    int compare;
    switch (this.index) {
      // Sort Attr Val.
      case 0:
        compare = ApicUtil.compare(mapping1.getVariantName(), mapping2.getVariantName());
        break;
      // Sort Attr Val Desc.
      case 1:
        compare = ApicUtil.compare(mapping1.getVariantDesc(), mapping2.getVariantDesc());
        break;
      // Icdm-832 new column added to the table
      case 2:
        compare = ApicUtil.compareBoolean(mapping1.isMapped(), mapping2.isMapped());

        break;
      // Sort Val Class .
      case ApicUiConstants.COLUMN_INDEX_3:
        compare = ApicUtil.compare(mapping1.getOtherVarGroupName(), mapping2.getOtherVarGroupName());
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
   * @return int defines sorting direction of tableviewercolumn
   */
  @Override
  public int getDirection() {
    return this.direction;
  }

}
