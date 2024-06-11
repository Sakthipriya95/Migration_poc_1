package com.bosch.caltool.icdm.ruleseditor.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * @author dmo5cob
 */
public class CDRFuncParamTabViewerSorter<D extends IParameterAttribute, P extends IParameter>
    extends AbstractViewerSorter { // NOPMD by bne4cob on 3/11/14 12:16 PM


  private int index = 1;
  private static final int DESCENDING = 1;
  private static final int ASCENDING = 0;
  private int direction = ASCENDING;
  private ParameterDataProvider<D, P> parameterDataProvider = null;
  private ParameterSorter paramSorter = null;

  /**
   * @param parameterDataProvider
   */
  public CDRFuncParamTabViewerSorter(final ParameterDataProvider parameterDataProvider) {
    this.parameterDataProvider = parameterDataProvider;
    this.paramSorter = new ParameterSorter(this.parameterDataProvider);
  }

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

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) { // NOPMD by bne4cob on 3/11/14 12:16
                                                                                  // PM
    int result = 0;
    if ((obj1 instanceof IParameter) && (obj2 instanceof IParameter)) {
      final IParameter param1 = (IParameter) obj1;
      final IParameter param2 = (IParameter) obj2;


      switch (this.index) {
        case CommonUIConstants.COLUMN_INDEX_1:
          result = this.paramSorter.compare(param1, param2, ParameterSorter.SortColumns.SORT_PARAM_NAME);
          break;
        case CommonUIConstants.COLUMN_INDEX_2:
          result = this.paramSorter.compare(param1, param2, ParameterSorter.SortColumns.SORT_PARAM_LONGNAME);
          break;
        case CommonUIConstants.COLUMN_INDEX_3:
          result = this.paramSorter.compare(param1, param2, ParameterSorter.SortColumns.SORT_PARAM_CLASS);
          break;
        case CommonUIConstants.COLUMN_INDEX_4:
          result = this.paramSorter.compare(param1, param2, ParameterSorter.SortColumns.SORT_PARAM_CODEWORD);
          break;
        case CommonUIConstants.COLUMN_INDEX_5:
          result = this.paramSorter.compare(param1, param2, ParameterSorter.SortColumns.SORT_PARAM_LOWERLIMIT);
          break;
        case CommonUIConstants.COLUMN_INDEX_6:
          result = this.paramSorter.compare(param1, param2, ParameterSorter.SortColumns.SORT_PARAM_UPPERLIMIT);
          break;
        case CommonUIConstants.COLUMN_INDEX_7:
          result = this.paramSorter.compare(param1, param2, ParameterSorter.SortColumns.SORT_PARAM_BITWISE);
          break;
        case CommonUIConstants.COLUMN_INDEX_8:
          result = this.paramSorter.compare(param1, param2, ParameterSorter.SortColumns.SORT_PARAM_REFVALUE);
          break;
        // ICDM-1173
        case CommonUIConstants.COLUMN_INDEX_9:
          result = this.paramSorter.compare(param1, param2, ParameterSorter.SortColumns.SORT_EXACT_MATCH);
          break;
        case CommonUIConstants.COLUMN_INDEX_10:
          result = this.paramSorter.compare(param1, param2, ParameterSorter.SortColumns.SORT_PARAM_UNIT);
          break;
        case CommonUIConstants.COLUMN_INDEX_11:
          result = this.paramSorter.compare(param1, param2, ParameterSorter.SortColumns.SORT_PARAM_READY_SERIES);
          break;
        case CommonUIConstants.COLUMN_INDEX_12:
          result = this.paramSorter.compare(param1, param2, ParameterSorter.SortColumns.SORT_REMARKS);
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