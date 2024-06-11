package com.bosch.caltool.icdm.common.ui.sorters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import com.bosch.caltool.icdm.common.bo.a2l.A2lResponsibilityCommon;
import com.bosch.caltool.icdm.model.a2l.WpRespModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * Sorter class for WP Grid table viewer
 *
 * @author mkl2cob
 */
public class WPGridTabViewerSorter extends ViewerSorter {

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
   * direction by default set to ASCENDING
   */
  private int direction = ASCENDING;

  /**
   * @param index int
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
   * {@inheritDoc} returns the compare result as integer value
   */
  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
    int compareResult = 0;
    if ((obj1 instanceof WpRespModel) && (obj2 instanceof WpRespModel)) {
      // initialise WorkPackage instances
      WpRespModel groupOne = (WpRespModel) obj1;
      WpRespModel groupTwo = (WpRespModel) obj2;


      switch (this.index) {
        case 0:
          // compare WP names
          compareResult = groupOne.compareTo(groupTwo, WpRespModel.SortColumns.SORT_WP_NAME);
          break;
        case 1:
          // compare WP Resp Names
          compareResult = groupOne.compareTo(groupTwo, WpRespModel.SortColumns.SORT_WP_RESPONSIBLE);
          break;
        case 2:
          // compare WP Resp Types
          compareResult =
              ModelUtil.compare(A2lResponsibilityCommon.getRespType(groupOne.getA2lResponsibility()).getDispName(),
                  A2lResponsibilityCommon.getRespType(groupTwo.getA2lResponsibility()).getDispName());
          break;
        case 3:
          // compare WP Param count
          compareResult = groupOne.compareTo(groupTwo, WpRespModel.SortColumns.SORT_WP_PARAM_COUNT);
          break;
        default:
          compareResult = 0;
      }
      // If descending order, flip the direction
      if (this.direction == DESCENDING) {
        compareResult = -compareResult;
      }
    }

    return compareResult;
  }

  /**
   * @return int
   */
  public int getDirection() {
    return this.direction;
  }
}