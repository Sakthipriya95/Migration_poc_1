package com.bosch.caltool.icdm.common.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.client.bo.a2l.CdfxExportParameter;
import com.bosch.caltool.icdm.client.bo.a2l.CdfxExportParameter.SortColumns;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * @author rgo7cob Icdm-857 Sorter for CDFX Filter table Viewer
 */
public class CdfxFilterTabViewerSorter extends AbstractViewerSorter {

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
   * {@inheritDoc} SET COLUMN INDEX
   */
  @Override
  public void setColumn(final int index) {
    // set the sort order
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
    int compRes = 0;
    if ((obj1 instanceof CdfxExportParameter) && (obj2 instanceof CdfxExportParameter)) {
      // initialise the parameters
      final CdfxExportParameter param1 = (CdfxExportParameter) obj1;
      final CdfxExportParameter param2 = (CdfxExportParameter) obj2;

      switch (this.index) {
        case 0:
          // sort by character names
          compRes = param1.compareTo(param2, SortColumns.SORT_CHAR_NAME);
          break;
        case 1:
          // sort by filter boolean
          compRes = param1.compareTo(param2, SortColumns.SORT_FILTERED);
          break;
        default:
          compRes = 0;
      }


      // If descending order, flip the direction
      if (this.direction == DESCENDING) {
        compRes = -compRes;
      }
    }


    return compRes;
  }

  @Override
  public int getDirection() {
    return this.direction;
  }
}