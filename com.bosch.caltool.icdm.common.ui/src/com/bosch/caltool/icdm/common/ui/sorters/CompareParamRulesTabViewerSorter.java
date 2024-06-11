package com.bosch.caltool.icdm.common.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportComparisonModel;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportComparisonModel.SortColumns;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * AbstractViewerSorter used in compareRulesWizard page in caldata import file wizard
 *
 * @author dmo5cob
 */
public class CompareParamRulesTabViewerSorter extends AbstractViewerSorter {

  /**
   * Index
   */
  private int index = 1;
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
  /**
   * {@inheritDoc}
   */
  @Override
  public void setColumn(final int index) {
    // set the direction of sorting
    if (index == this.index) {
      this.direction = 1 - this.direction;
    } // Ascending order
    else {
      this.index = index;
      this.direction = ASCENDING;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
    int result = 0;
    if ((obj1 instanceof CalDataImportComparisonModel) && (obj2 instanceof CalDataImportComparisonModel)) {
      final CalDataImportComparisonModel compareObj1 = (CalDataImportComparisonModel) obj1;
      final CalDataImportComparisonModel compareObj2 = (CalDataImportComparisonModel) obj2;

      switch (this.index) {
        // ICDM-2201
        case CommonUIConstants.COLUMN_INDEX_0:
          result = compareCdicModel(compareObj1, compareObj2, SortColumns.SORT_PARAM_TYPE);
          break;
        case CommonUIConstants.COLUMN_INDEX_1:
          result = compareCdicModel(compareObj1, compareObj2, SortColumns.SORT_PARAM_NAME);
          break;
        case CommonUIConstants.COLUMN_INDEX_2:
          result = compareCdicModel(compareObj1, compareObj2, SortColumns.SORT_FUNC_NAME);
          break;
        case CommonUIConstants.COLUMN_INDEX_3:
          result = compareCdicModel(compareObj1, compareObj2, SortColumns.SORT_DEPENDENCY);
          break;
        case CommonUIConstants.COLUMN_INDEX_4:
          result = compareCdicModel(compareObj1, compareObj2, SortColumns.SORT_RULE_EXIST);
          break;
        case CommonUIConstants.COLUMN_INDEX_5:
          result = compareCdicModel(compareObj1, compareObj2, SortColumns.SORT_UPDATE_FLAG);
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
   * Compare To implementation using sort columns
   *
   * @param other compare obj
   * @param sortColumn sortColumn
   * @return as per the standard compareTo implementation
   */
  private int compareCdicModel(final CalDataImportComparisonModel compare1, final CalDataImportComparisonModel compare2,
      final SortColumns sortColumn) {
    int compareResult;
    switch (sortColumn) {
      case SORT_PARAM_TYPE:
        compareResult = ApicUtil.compare(compare1.getParamType(), compare2.getParamType());
        break;
      case SORT_PARAM_NAME:
        compareResult = ApicUtil.compare(compare1.getParamName(), compare2.getParamName());
        break;
      case SORT_FUNC_NAME:
        compareResult = ApicUtil.compare(compare1.getFuncNames(), compare2.getFuncNames());
        break;
      case SORT_DEPENDENCY:
        compareResult = ApicUtil.compareStringAndNum(compare1.getParamDependency(), compare2.getParamDependency());
        break;
      case SORT_RULE_EXIST:
        boolean compare1Ins = compare1.getOldRule() == null;
        boolean compare2Ins = compare2.getOldRule() == null;
        compareResult = ApicUtil.compareBoolean(compare1Ins, compare2Ins);
        break;
      case SORT_UPDATE_FLAG:
        compareResult = ApicUtil.compareBoolean(compare1.isUpdateInDB(), compare2.isUpdateInDB());
        break;
      default:
        compareResult = 0;
        break;
    }

    // additional compare column is the name of the characteristic
    if (compareResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      // compare result is equal, compare the param name go with default comparison
      compareResult = compare1.compareTo(compare2);
    }
    return compareResult;
  }

  /**
   * @return defines
   */
  /**
   * {@inheritDoc}
   */
  @Override
  public int getDirection() {
    return this.direction;
  }
}