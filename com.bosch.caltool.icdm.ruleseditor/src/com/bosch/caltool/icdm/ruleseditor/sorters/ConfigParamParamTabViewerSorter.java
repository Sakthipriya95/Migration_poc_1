package com.bosch.caltool.icdm.ruleseditor.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.model.cdr.ConfigBasedParam;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * @author dmo5cob
 */
public class ConfigParamParamTabViewerSorter extends AbstractViewerSorter { // NOPMD by bne4cob on 3/11/14 12:16 PM


  private int index = 1;
  private static final int DESCENDING = 1;
  private static final int ASCENDING = 0;
  private int direction = ASCENDING;
  private final ParameterDataProvider parameterDataProvider;
  private final ParameterSorter paramSorter;

  /**
   * @param index defines grid tableviewercolumn index
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


  public ConfigParamParamTabViewerSorter(final ParameterDataProvider parameterDataProvider) {

    this.parameterDataProvider = parameterDataProvider;
    this.paramSorter = new ParameterSorter(this.parameterDataProvider);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) { // NOPMD by bne4cob on 3/11/14 12:16
                                                                                  // PM
    int result = 0;
    if ((obj1 instanceof ConfigBasedParam) && (obj2 instanceof ConfigBasedParam)) {
      final IParameter param1 = ((ConfigBasedParam) obj1).getParameter();
      final IParameter param2 = ((ConfigBasedParam) obj2).getParameter();

      switch (this.index) {
        case ApicUiConstants.COLUMN_INDEX_1:
          result = this.paramSorter.compare(param1, param2, ParameterSorter.SortColumns.SORT_PARAM_NAME);
          break;
        case ApicUiConstants.COLUMN_INDEX_2:
          result = this.paramSorter.compare(param1, param2, ParameterSorter.SortColumns.SORT_PARAM_LONGNAME);
          break;
        case ApicUiConstants.COLUMN_INDEX_3:
          result = this.paramSorter.compare(param1, param2, ParameterSorter.SortColumns.SORT_PARAM_CLASS);
          break;
        case ApicUiConstants.COLUMN_INDEX_4:
          result = this.paramSorter.compare(param1, param2, ParameterSorter.SortColumns.SORT_PARAM_CODEWORD);
          break;
        case ApicUiConstants.COLUMN_INDEX_5:
          result = this.paramSorter.compare(param1, param2, ParameterSorter.SortColumns.SORT_PARAM_LOWERLIMIT);
          break;
        case ApicUiConstants.COLUMN_INDEX_6:
          result = this.paramSorter.compare(param1, param2, ParameterSorter.SortColumns.SORT_PARAM_UPPERLIMIT);
          break;
        case ApicUiConstants.COLUMN_INDEX_7:
          result = this.paramSorter.compare(param1, param2, ParameterSorter.SortColumns.SORT_PARAM_BITWISE);
          break;
        case ApicUiConstants.COLUMN_INDEX_8:
          result = this.paramSorter.compare(param1, param2, ParameterSorter.SortColumns.SORT_PARAM_UNIT);
          break;
        case ApicUiConstants.COLUMN_INDEX_9:
          result = this.paramSorter.compare(param1, param2, ParameterSorter.SortColumns.SORT_PARAM_READY_SERIES);
          break;
        default:
          result = 0;
      }
      // If descending order, flip the direction
      if (this.direction == DESCENDING) {
        result = -result;
      }
    }
    return result;
  }

  /**
   * @return defines
   */
  @Override
  public int getDirection() {
    return this.direction;
  }
}