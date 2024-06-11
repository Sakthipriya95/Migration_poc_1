/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.client.bo.qnaire.QnaireDefBO;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireDefBO.SortColumnsForVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireVersion;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * ICDM-1984 Sorter for versions page
 *
 * @author mkl2cob
 */
public class QuestionnaireVersionsSorter extends AbstractViewerSorter {

  private final QnaireDefBO qnaireDefBo;

  /**
   * @param qnaireDefBo QnaireDefBo
   */
  public QuestionnaireVersionsSorter(final QnaireDefBO qnaireDefBo) {
    this.qnaireDefBo = qnaireDefBo;
  }

  /**
   * index for Sorting
   */
  private int index;
  /**
   * Constant for descending
   */
  private static final int DESCENDING = 1;
  /**
   * Constant for ascending
   */
  private static final int ASCENDING = 0;
  // Default ascending
  private int direction = DESCENDING;


  /**
   * @param index
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
  public int compare(final Viewer viewer, final Object object1, final Object object2) {
    // get object1
    QuestionnaireVersion quesVersion1;
    if (object1 instanceof QuestionnaireVersion) {
      quesVersion1 = (QuestionnaireVersion) object1;
    }
    else {
      return 0;
    }
    // get object2
    QuestionnaireVersion quesVersion2;
    if (object2 instanceof QuestionnaireVersion) {
      quesVersion2 = (QuestionnaireVersion) object2;
    }
    else {
      return 0;
    }

    int compareResult;

    switch (this.index) {
      case 0:
        // version name
        compareResult = this.qnaireDefBo.compareTo(quesVersion1, quesVersion2,
            SortColumnsForVersion.SORT_VERSION_NAME);
        break;
      case 1:
        // version description
        compareResult =
            this.qnaireDefBo.compareTo(quesVersion1, quesVersion2, SortColumnsForVersion.SORT_DESC);
        break;
      case 2:
        // version status
        compareResult = this.qnaireDefBo.compareTo(quesVersion1, quesVersion2,
            SortColumnsForVersion.SORT_VERSION_STATUS);
        break;
      case 3:
        // version active
        compareResult =
            this.qnaireDefBo.compareTo(quesVersion1, quesVersion2, SortColumnsForVersion.SORT_ACTIVE);
        break;
      case 4:
        // version created date
        compareResult = this.qnaireDefBo.compareTo(quesVersion1, quesVersion2,
            SortColumnsForVersion.SORT_CREATED_DATE);
        break;
      case 5:
        // version created user
        compareResult = this.qnaireDefBo.compareTo(quesVersion1, quesVersion2,
            SortColumnsForVersion.SORT_CREATED_USER);
        break;
      default:
        compareResult = 0;
    }
    // If descending order, flip the direction
    if (this.direction == DESCENDING) {
      compareResult = -compareResult;
    }
    return compareResult;
  }

  @Override
  public int getDirection() {
    return this.direction;
  }


}
