/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.sorter;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.client.bo.apic.AliasDefEditorDataHandler;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * @author rgo7cob
 */
public class AliasAttrSorter extends AbstractViewerSorter {

  /**
   * Represents the column number to be sorted
   */
  private transient int index;

  /**
   * Default diraction ascending
   */
  private transient int direction = ApicConstants.ASCENDING;

  private final AliasDefEditorDataHandler dataHandler;

  /**
   * @param dataHandler Alias Def Editor Data Handler
   */
  public AliasAttrSorter(final AliasDefEditorDataHandler dataHandler) {
    this.dataHandler = dataHandler;
  }

  /**
   * index defines grid tableviewercolumn index {@inheritDoc}
   */
  @Override
  public void setColumn(final int index) {
    // set Direction
    if (index == this.index) {
      this.direction = 1 - this.direction;
    }
    else {
      this.index = index;
      this.direction = ApicConstants.ASCENDING;
    }

  }

  /**
   * Compares two pid's details {@inheritDoc}
   */
  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) { // NOPMD by bne4cob on 3/11/14 12:16
    int result = 0;
    // check pidc instance
    if ((obj1 instanceof Attribute) && (obj2 instanceof Attribute)) {
      Attribute result1 = (Attribute) obj1;
      Attribute result2 = (Attribute) obj2;

      switch (this.index) {
        // compare pidc's name
        case CommonUIConstants.COLUMN_INDEX_0:
          result = ApicUtil.compare(result1.getName(), result2.getName());
          break;
        // value type
        case CommonUIConstants.COLUMN_INDEX_1:
          result = ApicUtil.compare(result1.getValueType(), result2.getValueType());
          break;
        // alias name column
        case CommonUIConstants.COLUMN_INDEX_2:
          result = ApicUtil.compare(this.dataHandler.getAttrAliasName(result1.getId()),
              this.dataHandler.getAttrAliasName(result2.getId()));
          break;
        default:
          // if no column available
          result = 0;
          break;
      }
      // If descending order, flip the direction
      if (this.direction == ApicConstants.DESCENDING) {
        result = -result;
      }
    }
    return result;
  }

  /**
   * @return defines direction of sort
   */
  @Override
  public int getDirection() {
    return this.direction;
  }


}
