/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * @author say8cob
 */
public class QnaireRespVersionSorter extends AbstractViewerSorter {

  /**
   * @author nip4cob
   */
  public enum SortColumnsForVersion {
                                     /**
                                      * Revision number
                                      */
                                     SORT_REVISION_NUMBER,
                                     /**
                                      * Version name
                                      */
                                     SORT_VERSION_NAME,
                                     /**
                                      * description
                                      */
                                     SORT_DESC,
                                     /**
                                      * version status
                                      */
                                     SORT_VERSION_STATUS,
                                     /**
                                      * active
                                      */
                                     SORT_ACTIVE,
                                     /**
                                      * created date
                                      */
                                     SORT_REVIEWED_DATE,
                                     /**
                                      * created user
                                      */
                                     SORT_REVIEWED_USER,
                                     /**
                                      * created date
                                      */
                                     SORT_CREATED_DATE,
                                     /**
                                      * created user
                                      */
                                     SORT_CREATED_USER
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

  /**
   * {@inheritDoc}
   */
  @Override
  public int compare(final Viewer viewer, final Object object1, final Object object2) {
    // get object1
    RvwQnaireRespVersion quesVersion1;
    if (object1 instanceof RvwQnaireRespVersion) {
      quesVersion1 = (RvwQnaireRespVersion) object1;
    }
    else {
      return 0;
    }
    // get object2
    RvwQnaireRespVersion quesVersion2;
    if (object2 instanceof RvwQnaireRespVersion) {
      quesVersion2 = (RvwQnaireRespVersion) object2;
    }
    else {
      return 0;
    }

    int compareResult;

    switch (this.index) {
      case 0:
        // version name
        compareResult = compareTo(quesVersion1, quesVersion2, SortColumnsForVersion.SORT_REVISION_NUMBER);
        break;
      case 1:
        // version name
        compareResult = compareTo(quesVersion1, quesVersion2, SortColumnsForVersion.SORT_VERSION_NAME);
        break;
      case 2:
        // version description
        compareResult = compareTo(quesVersion1, quesVersion2, SortColumnsForVersion.SORT_DESC);
        break;
      case 3:
        // version status
        compareResult = compareTo(quesVersion1, quesVersion2, SortColumnsForVersion.SORT_VERSION_STATUS);
        break;
      case 4:
        // version reviewed date
        compareResult = compareTo(quesVersion1, quesVersion2, SortColumnsForVersion.SORT_REVIEWED_DATE);
        break;
      case 5:
        // version reviewed user
        compareResult = compareTo(quesVersion1, quesVersion2, SortColumnsForVersion.SORT_REVIEWED_USER);
        break;
      case 6:
        // version created date
        compareResult = compareTo(quesVersion1, quesVersion2, SortColumnsForVersion.SORT_CREATED_DATE);
        break;
      case 7:
        // version created user
        compareResult = compareTo(quesVersion1, quesVersion2, SortColumnsForVersion.SORT_CREATED_USER);
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

  /**
   * @param quesVers1 QuestionnaireVersion
   * @param quesVers2 QuestionnaireVersion
   * @param sortColumn column to be compared
   * @return comparison result
   */
  public int compareTo(final RvwQnaireRespVersion quesVers1, final RvwQnaireRespVersion quesVers2,
      final SortColumnsForVersion sortColumn) {
    int compareResult;

    switch (sortColumn) {
      case SORT_REVISION_NUMBER:
        compareResult = ApicUtil.compare(quesVers1.getRevNum(), quesVers2.getRevNum());
        break;

      case SORT_VERSION_NAME:
        compareResult = ApicUtil.compare(quesVers1.getVersionName(), quesVers2.getVersionName());
        break;

      case SORT_DESC:
        compareResult = ApicUtil.compare(quesVers1.getDescription(), quesVers2.getDescription());
        break;

//      case SORT_VERSION_STATUS:
//        compareResult = ApicUtil.compare(quesVers1.getVersionName(), quesVers2.getVersionName());
//        break;

      case SORT_REVIEWED_DATE:
        compareResult = ApicUtil.compare(quesVers1.getReviewedDate(), quesVers2.getReviewedDate());
        break;

      case SORT_REVIEWED_USER:
        compareResult = ApicUtil.compare(quesVers1.getReviewedUser(), quesVers2.getReviewedUser());
        break;

      case SORT_CREATED_DATE:
        compareResult = ApicUtil.compare(quesVers1.getCreatedDate(), quesVers2.getCreatedDate());
        break;

      case SORT_CREATED_USER:
        compareResult = ApicUtil.compare(quesVers1.getCreatedUser(), quesVers2.getCreatedUser());
        break;

      default:
        compareResult = 0;
        break;
    }

    if (compareResult == 0) {
      // compare result is equal, compare the version name
      compareResult = ApicUtil.compare(quesVers1.getVersionName(), quesVers2.getVersionName());

    }
    return compareResult;
  }

  @Override
  public int getDirection() {
    return this.direction;
  }

}
