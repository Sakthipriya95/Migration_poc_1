/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.ssd.SSDFeatureICDMAttrModel;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * @author bru2cob
 */
public class FeaValGridTabViewerSorter extends AbstractViewerSorter {


  /**
   * Index
   */
  private int index = 2;
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
    // get the objects to be compared
    SSDFeatureICDMAttrModel wp1 = (SSDFeatureICDMAttrModel) obj1;
    SSDFeatureICDMAttrModel wp2 = (SSDFeatureICDMAttrModel) obj2;
    int compare = 0;
    switch (this.index) {
      // compare feature text
      case 0:
        String featureText1 = wp1.getFeaValModel().getFeatureText().toUpperCase();
        String featureText2 = wp2.getFeaValModel().getFeatureText().toUpperCase();
        compare = ApicUtil.compare(featureText1, featureText2);
        break;
      // compare value text
      case 1:
        compare = ApicUtil.compare(wp1.getFeaValModel().getValueText(), wp2.getFeaValModel().getValueText());
        break;
      case 2:
        // compare attribute name
        compare = compareAttrNames(wp1, wp2);
        break;
      case 3:
        // compare attribute value
        compare = compareAttrValues(wp1, wp2);
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
   * @param wp1
   * @param wp2
   * @return
   */
  private int compareAttrValues(SSDFeatureICDMAttrModel wp1, SSDFeatureICDMAttrModel wp2) {
    int compare;
    String valueName1 = null;
    String valueName2 = null;
    if ((wp1.getAttrValModel() != null) && (wp2.getAttrValModel() != null)) {
      valueName1 = wp1.getAttrValModel().getValue().getName();
      valueName2 = wp2.getAttrValModel().getValue().getName();
    }
    compare = ApicUtil.compare(valueName1, valueName2);
    return compare;
  }

  /**
   * @param wp1
   * @param wp2
   * @return
   */
  private int compareAttrNames(final SSDFeatureICDMAttrModel wp1, final SSDFeatureICDMAttrModel wp2) {
    int compare;
    String attrName1 = null;
    String attrName2 = null;
    if ((wp1.getAttrValModel() != null) && (wp2.getAttrValModel() != null)) {
      attrName1 = wp1.getAttrValModel().getAttr().getName();
      attrName2 = wp2.getAttrValModel().getAttr().getName();

    }

    if ((CommonUtils.isEmptyString(attrName2) && CommonUtils.isEmptyString(attrName1)) ||
        (!CommonUtils.isEmptyString(attrName2) && !CommonUtils.isEmptyString(attrName1))) {
      attrName1 = wp1.getFeaValModel().getFeatureText().toUpperCase();
      attrName2 = wp1.getFeaValModel().getFeatureText().toUpperCase();
      compare = ApicUtil.compare(attrName1, attrName2);
    }

    else {
      compare = ApicUtil.compare(attrName2, attrName1);
    }
    return compare;
  }


}
