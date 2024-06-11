/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.apic.ApicConstants.READY_FOR_SERIES;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.ReviewRule.SortColumns;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * Sorter class for rules table
 *
 * @author mkl2cob
 */
public class ConfigBasedCDRRuleSorter extends AbstractViewerSorter {

  private int index = 1;
  private static final int DESCENDING = 1;
  private static final int ASCENDING = 0;
  private int direction = ASCENDING;

  /**
   * Ready for series Column number
   */
  private static final int READY_FOR_SERIES_COL_NUM = 8;

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

  /**
   * {@inheritDoc}
   */
  @Override
  public int getDirection() {
    return this.direction;
  }

  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
    int result = 0;
    if ((obj1 instanceof ReviewRule) && (obj2 instanceof ReviewRule)) {
      result = compareRules(obj1, obj2);
    }
    return result;
  }

  /**
   * @param obj1
   * @param obj2
   * @return
   */
  private int compareRules(final Object obj1, final Object obj2) {
    int result;
    final ReviewRule rule1 = (ReviewRule) obj1;
    final ReviewRule rule2 = (ReviewRule) obj2;
    ReviewRuleComparator comparator = new ReviewRuleComparator();

    switch (this.index) {
      case ApicUiConstants.COLUMN_INDEX_1:
        result = comparator.compareTo(rule1, rule2, SortColumns.SORT_PARAM_NAME);
        break;
      case ApicUiConstants.COLUMN_INDEX_2:
        result = comparator.compareTo(rule1, rule2, SortColumns.SORT_LOWER_LIMIT);
        break;
      case ApicUiConstants.COLUMN_INDEX_3:
        result = comparator.compareTo(rule1, rule2, SortColumns.SORT_UPPER_LIMIT);
        break;
      case ApicUiConstants.COLUMN_INDEX_4:
        result = comparator.compareTo(rule1, rule2, SortColumns.SORT_BITWISE_LIMIT);
        break;
      case ApicUiConstants.COLUMN_INDEX_5:
        result = comparator.compareTo(rule1, rule2, SortColumns.SORT_REF_VALUE);
        break;
      // Icdm -1188 - Sorter for new Columns
      case ApicUiConstants.COLUMN_INDEX_7:
        result = comparator.compareTo(rule1, rule2, SortColumns.SORT_UNIT);
        break;
      case ApicUiConstants.COLUMN_INDEX_6:
        result = comparator.compareTo(rule1, rule2, SortColumns.SORT_EXACT_MATCH);
        break;
      case READY_FOR_SERIES_COL_NUM:
        result = ApicUtil.compare(READY_FOR_SERIES.getType(rule1.getReviewMethod()).getUiType(),
            READY_FOR_SERIES.getType(rule2.getReviewMethod()).getUiType());
        break;
      default:
        result = 0;
    }
    // If descending order, flip the direction
    if (this.direction == DESCENDING) {
      result = -result;
    }
    return result;
  }
}
