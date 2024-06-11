/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.sorters;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.cdr.ui.dialogs.QuestionnaireNameSelDialog;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.wp.WorkPkg;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * Sorter class for questionnaire name selection dialog
 *
 * @author bru2cob
 */
public class QuestionnaireNameTableSorter extends AbstractViewerSorter {

  /**
   * index
   */
  private int index = 1;
  /**
   * Constant for descending
   */
  private static final int DESCENDING = 1;
  /**
   * Constant for ascending
   */
  private static final int ASCENDING = 0;
  /**
   * direction by default set to ascending
   */
  private int direction = ASCENDING;

  private final QuestionnaireNameSelDialog questionnaireNameSelDialog;

  private Map<Long, String> wpRespMap = new ConcurrentHashMap<>();


  /**
   * @param questionnaireNameSelDialog questionnaireNameSelDialog
   */
  public QuestionnaireNameTableSorter(final QuestionnaireNameSelDialog questionnaireNameSelDialog) {
    super();
    this.questionnaireNameSelDialog = questionnaireNameSelDialog;
  }


  /**
   * @param index defines grid tableviewercolumn index
   */
  @Override
  public void setColumn(final int index) {
    // set the direction
    if (index == this.index) {
      this.direction = 1 - this.direction;
    }
    else {
      this.index = index;
      this.direction = ASCENDING;
    }
  }


  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {

    this.wpRespMap = this.questionnaireNameSelDialog.getWorkPkgResp();

    // create the compare objs
    WorkPkg wp1 = (WorkPkg) obj1;
    WorkPkg wp2 = (WorkPkg) obj2;
    int compare = 0;
    switch (this.index) {
      // Sort open point
      case ApicUiConstants.COLUMN_INDEX_0:
        compare = ApicUtil.compare(this.wpRespMap.get(wp1.getId()), this.wpRespMap.get(wp2.getId()));
        break;
      // Sort measure
      case ApicUiConstants.COLUMN_INDEX_1:
        compare = wp1.compareWpName(wp2);
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
   * @return defines
   */
  @Override
  public int getDirection() {
    return this.direction;
  }

}
