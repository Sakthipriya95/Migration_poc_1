/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.client.bo.cdr.RuleDefinition;
import com.bosch.caltool.icdm.client.bo.cdr.RuleDefinition.SortColumns;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * Sorter class for rules table
 *
 * @author mkl2cob
 */
public class RulesViewerSorter extends AbstractViewerSorter {

  private int index = 1;
  private static final int DESCENDING = 1;
  private static final int ASCENDING = 0;
  private int direction = ASCENDING;

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
    if ((obj1 instanceof RuleDefinition) && (obj2 instanceof RuleDefinition)) {
      final RuleDefinition rule1 = (RuleDefinition) obj1;
      final RuleDefinition rule2 = (RuleDefinition) obj2;

      switch (this.index) {
        case ApicUiConstants.COLUMN_INDEX_1:
          result = rule1.compareTo(rule2, SortColumns.SORT_LOWER_LIMIT);
          break;
        case ApicUiConstants.COLUMN_INDEX_2:
          result = rule1.compareTo(rule2, SortColumns.SORT_UPPER_LIMIT);
          break;
        case ApicUiConstants.COLUMN_INDEX_3:
          result = rule1.compareTo(rule2, SortColumns.SORT_BITWISE);
          break;
        case ApicUiConstants.COLUMN_INDEX_4:
          result = rule1.compareTo(rule2, SortColumns.SORT_REFERENCE_VALUE);
          break;
        case ApicUiConstants.COLUMN_INDEX_5:
          result = rule1.compareTo(rule2, SortColumns.SORT_UNIT);
          break;
        case ApicUiConstants.COLUMN_INDEX_6:
          result = rule1.compareTo(rule2, SortColumns.SORT_REVIEW_METHOD);
          break;
        case ApicUiConstants.COLUMN_INDEX_7:
          result = rule1.compareTo(rule2, SortColumns.SORT_EXACT_MATCH);
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
}
