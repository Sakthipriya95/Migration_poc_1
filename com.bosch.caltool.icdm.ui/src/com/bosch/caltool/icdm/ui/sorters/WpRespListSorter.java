/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * @author elm1cob
 */
public class WpRespListSorter extends AbstractViewerSorter {

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

  private final A2LWPInfoBO a2lWpInfoBo;

  /**
   * @param wpInfoBo
   */
  public WpRespListSorter(final A2LWPInfoBO wpInfoBo) {
    super();
    this.a2lWpInfoBo = wpInfoBo;
  }


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
    A2lWpResponsibility wp1 = (A2lWpResponsibility) obj1;
    A2lWpResponsibility wp2 = (A2lWpResponsibility) obj2;
    int compare;
    switch (this.index) {
      case CommonUIConstants.COLUMN_INDEX_0:
        compare = ApicUtil.compare(wp1.getName(), wp2.getName());
        break;
      case CommonUIConstants.COLUMN_INDEX_1:
        compare = compareRespNames(wp1, wp2);
        break;
      case CommonUIConstants.COLUMN_INDEX_2:
        compare = ApicUtil.compare(this.a2lWpInfoBo.getRespTypeName(wp1), this.a2lWpInfoBo.getRespTypeName(wp2));
        break;

      case CommonUIConstants.COLUMN_INDEX_3:
        compare = ApicUtil.compare(this.a2lWpInfoBo.getVarGrpName(wp1), this.a2lWpInfoBo.getVarGrpName(wp2));
        break;

      case CommonUIConstants.COLUMN_INDEX_4:
        compare = ApicUtil.compare(wp1.getWpNameCust(), wp2.getWpNameCust());
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
  private int compareRespNames(A2lWpResponsibility wp1, A2lWpResponsibility wp2) {
    int compare;
    A2lResponsibility a2lResp1 =
        this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap().get(wp1.getA2lRespId());
    A2lResponsibility a2lResp2 =
        this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap().get(wp2.getA2lRespId());
    compare = ApicUtil.compare(a2lResp1.getName(), a2lResp2.getName());
    return compare;
  }


}
