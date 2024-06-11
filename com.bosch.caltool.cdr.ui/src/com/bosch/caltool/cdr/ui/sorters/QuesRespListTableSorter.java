/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespStatusData;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * @author mkl2cob
 */
public class QuesRespListTableSorter extends AbstractViewerSorter {

  /**
   * index initialised to 1000 in order to enable custom default sorting
   */
  private int index = 1000;
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


  /**
   * {@inheritDoc}
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

    // create the compare objs
    QnaireRespStatusData data1 = (QnaireRespStatusData) obj1;
    QnaireRespStatusData data2 = (QnaireRespStatusData) obj2;
    int compare = 0;
    switch (this.index) {
      // Sort workpackage
      case ApicUiConstants.COLUMN_INDEX_0:
        compare = ApicUtil.compare(data1.getWpName(), data2.getWpName());
        break;
      // Sort responsibility
      case ApicUiConstants.COLUMN_INDEX_1:
        compare = ApicUtil.compare(data1.getRespName(), data2.getRespName());
        break;
      // Sort ques response
      case ApicUiConstants.COLUMN_INDEX_2:
        compare = ApicUtil.compare(data1.getQnaireRespName(), data2.getQnaireRespName());
        break;
      // Sort Primary Variant
      case ApicUiConstants.COLUMN_INDEX_3:
        compare = ApicUtil.compare(data1.getPrimaryVarName(), data2.getPrimaryVarName());
        break;
      // Sort Questionnaire status
      case ApicUiConstants.COLUMN_INDEX_4:
        compare = ApicUtil.compare(data1.getStatus(), data2.getStatus());
        break;
      case ApicUiConstants.COLUMN_INDEX_5:
        compare = ApicUtil.compare(data1.getRevisionNum(), data2.getRevisionNum());
        break;
      // custom Sort
      default:
        compare = compareByDefault(data1, data2);
    }
    // If descending order, flip the direction
    if (this.direction == DESCENDING) {
      compare = -compare;
    }
    return compare;
  }

  /**
   * This method does Custom Sorting by default
   * @param data1
   * @param data2
   * @return
   * 
   */
  private int compareByDefault(final QnaireRespStatusData data1, final QnaireRespStatusData data2) {
    int compare = 0;
    // First level Sorting based on Workpackage
    compare = ApicUtil.compare(data1.getWpName(), data2.getWpName());
    // Second level sorting based on Resposibility
    if (compare == 0) {
      compare = ApicUtil.compare(data1.getRespName(), data2.getRespName());
    }
    // Third level sorting based on Questionnaire Response
    if (compare == 0) {
      compare = ApicUtil.compare(data1.getQnaireRespName(), data2.getQnaireRespName());
    }
    // Fourth level sorting based on Pramiary variant
    if (compare == 0) {
      compare = ApicUtil.compare(data1.getPrimaryVarName(), data2.getPrimaryVarName());
    }
    // Fifth level sorting based on Questionnaire status
    if (compare == 0) {
      compare = ApicUtil.compare(data1.getStatus(), data2.getStatus());
    }
    // Sixth level sorting based on Questionnaire version
    if (compare == 0) {
      compare = ApicUtil.compare(data1.getRevisionNum(), data2.getRevisionNum());
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
