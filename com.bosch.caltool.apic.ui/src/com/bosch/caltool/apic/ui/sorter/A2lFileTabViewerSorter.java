/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.sorter;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.client.bo.a2l.PidcA2LBO;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2lFileExt;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

// ICDM-1438
/**
 * @author bru2cob
 */
public class A2lFileTabViewerSorter extends AbstractViewerSorter {

  /**
   * Index
   */
  private int index;
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
  private int direction = DESCENDING;


  /**
   * @param index defines grid tableviewercolumn index
   */
  @Override
  public void setColumn(final int index) {
    // set the direction of sorting
    if (index == this.index) {
      this.direction = 1 - this.direction;
    }
    // Ascending order
    else {
      this.index = index;
      this.direction = ASCENDING;
    }
  }

  @Override
  public int getDirection() {
    return this.direction;
  }

  @Override
  public int compare(final Viewer viewer, final Object object1, final Object object2) {

    // initialize first A2LFile instance
    PidcA2lFileExt pidcA2lObject1 = (PidcA2lFileExt) object1;
    // initialize second A2LFile instance
    PidcA2lFileExt pidcA2lObject2 = (PidcA2lFileExt) object2;

    int compareResult;

    switch (this.index) {
      case CommonUIConstants.COLUMN_INDEX_0:
        // pver version name
        compareResult = compareVerName(pidcA2lObject1, pidcA2lObject2);
        break;
      case CommonUIConstants.COLUMN_INDEX_2:
        compareResult = ApicUtil.compareBoolean(
            CommonUtils.isNotNull(pidcA2lObject1.getPidcA2l()) ? pidcA2lObject1.getPidcA2l().isActive() : Boolean.FALSE,
            CommonUtils.isNotNull(pidcA2lObject2.getPidcA2l()) ? pidcA2lObject2.getPidcA2l().isActive()
                : Boolean.FALSE);
        break;
      case CommonUIConstants.COLUMN_INDEX_3:
        compareResult = ApicUtil.compareBoolean(
            CommonUtils.isNotNull(pidcA2lObject1.getPidcA2l()) ? pidcA2lObject1.getPidcA2l().isWpParamPresentFlag()
                : Boolean.FALSE,
            CommonUtils.isNotNull(pidcA2lObject2.getPidcA2l()) ? pidcA2lObject2.getPidcA2l().isWpParamPresentFlag()
                : Boolean.FALSE);
        break;
      case CommonUIConstants.COLUMN_INDEX_4:
        compareResult = ApicUtil.compareBoolean(
            CommonUtils.isNotNull(pidcA2lObject1.getPidcA2l())
                ? pidcA2lObject1.getPidcA2l().isActiveWpParamPresentFlag() : Boolean.FALSE,
            CommonUtils.isNotNull(pidcA2lObject2.getPidcA2l())
                ? pidcA2lObject2.getPidcA2l().isActiveWpParamPresentFlag() : Boolean.FALSE);
        break;
      case CommonUIConstants.COLUMN_INDEX_10:
        // compare the a2l file mapped date
        compareResult = compareA2lFile(pidcA2lObject1, pidcA2lObject2);
        break;
      case CommonUIConstants.COLUMN_INDEX_11:
        // comapre the user names
        compareResult = compareUserName(pidcA2lObject1, pidcA2lObject2);
        break;
      case CommonUIConstants.COLUMN_INDEX_5:
        // Pver name
        compareResult = comparePverName(pidcA2lObject1, pidcA2lObject2);
        break;
      case CommonUIConstants.COLUMN_INDEX_6:
        // Variant name
        compareResult = ApicUtil.compare(pidcA2lObject1.getA2lFile().getSdomPverVariant(),
            pidcA2lObject2.getA2lFile().getSdomPverVariant());

        break;
      case CommonUIConstants.COLUMN_INDEX_7:
        // A2l file name
        compareResult =
            ApicUtil.compare(pidcA2lObject1.getA2lFile().getFilename(), pidcA2lObject2.getA2lFile().getFilename());
        break;
      case CommonUIConstants.COLUMN_INDEX_9:
        compareResult =
            ApicUtil.compare(pidcA2lObject1.getA2lFile().getFiledate(), pidcA2lObject2.getA2lFile().getFiledate());
        break;
      case CommonUIConstants.COLUMN_INDEX_8:
        compareResult = compareSSDSoftVersions(pidcA2lObject1, pidcA2lObject2);
        break;

      default:
        compareResult = CommonUIConstants.COLUMN_INDEX_0;
    }
    // If descending order, flip the direction
    if (this.direction == DESCENDING) {
      compareResult = -compareResult;
    }
    return compareResult;
  }

  /**
   * @param a2lFile1
   * @param a2lFile2
   * @return the compare result of the SSD software version
   */
  private int compareSSDSoftVersions(final PidcA2lFileExt a2lFile1, final PidcA2lFileExt a2lFile2) {
    int compareResult;
    String version1 = "";
    String version2 = "";
    PidcA2l pidcA2l1 = a2lFile1.getPidcA2l();
    PidcA2l pidcA2l2 = a2lFile2.getPidcA2l();
    if (pidcA2l1 != null) {
      version1 = pidcA2l1.getSsdSoftwareVersion();
    }
    if (pidcA2l2 != null) {
      version2 = pidcA2l2.getSsdSoftwareVersion();
    }
    compareResult = ApicUtil.compare(version1, version2);
    return compareResult;
  }

  /**
   * Comes pver name
   *
   * @param a2lFile1
   * @param a2lFile2
   * @return
   */
  private int comparePverName(final PidcA2lFileExt a2lFile1, final PidcA2lFileExt a2lFile2) {
    int compareResult;
    compareResult = ApicUtil.compare(a2lFile1.getA2lFile().getSdomPverName(), a2lFile2.getA2lFile().getSdomPverName());
    // Second level sorting for pver varaint
    if (compareResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      compareResult =
          ApicUtil.compare(a2lFile1.getA2lFile().getSdomPverVariant(), a2lFile2.getA2lFile().getSdomPverVariant());
    }
    // third level sorting for pver name is a2lfile name
    if (compareResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      compareResult = ApicUtil.compare(a2lFile1.getA2lFile().getFilename(), a2lFile2.getA2lFile().getFilename());
    }
    return compareResult;
  }

  /**
   * Compares the name os the users
   *
   * @param pidcA2lFileExt1
   * @param pidcA2lFileExt2
   * @return
   */
  private int compareUserName(final PidcA2lFileExt pidcA2lFileExt1, final PidcA2lFileExt pidcA2lFileExt2) {
    int compareResult;
    String user1 = "";
    String user2 = "";
    if (null != pidcA2lFileExt1.getPidcA2l()) {
      user1 = new PidcA2LBO(pidcA2lFileExt1.getPidcA2l().getId(), pidcA2lFileExt1).getAssignedUser();

    }
    if (null != pidcA2lFileExt2.getPidcA2l()) {
      user2 = new PidcA2LBO(pidcA2lFileExt2.getPidcA2l().getId(), pidcA2lFileExt2).getAssignedUser();

    }
    // Compare user names
    compareResult = ApicUtil.compareStringAndNum(user1, user2);
    return compareResult;
  }


  /**
   * This method compares two a2l file containing a2l file assigned date
   *
   * @param pidcA2lFileExt1
   * @param pidcA2lFileExt2
   * @return
   * @throws DataException
   */
  private int compareA2lFile(final PidcA2lFileExt pidcA2lFileExt1, final PidcA2lFileExt pidcA2lFileExt2) {
    int compareResult;
    String date1 = null;
    String date2 = null;
    if (null != pidcA2lFileExt1.getPidcA2l()) {
      date1 = new PidcA2LBO(pidcA2lFileExt1.getPidcA2l().getId(), pidcA2lFileExt1).getAssignedDate();
    }
    if (null != pidcA2lFileExt2.getPidcA2l()) {
      date2 = new PidcA2LBO(pidcA2lFileExt2.getPidcA2l().getId(), pidcA2lFileExt2).getAssignedDate();
    }
    compareResult = ApicUtil.compare(date1, date2);
    return compareResult;
  }

  /**
   * Compares the version name
   *
   * @param a2lFile1
   * @param a2lFile2
   * @return
   */
  private int compareVerName(final PidcA2lFileExt a2lFile1, final PidcA2lFileExt a2lFile2) {
    int compareResult;
    String verName1 = "";
    String verName2 = "";
    if ((null != a2lFile1.getPidcA2l()) && (null != a2lFile1.getPidcVersion())) {
      verName1 = a2lFile1.getPidcVersion().getVersionName();
    }
    if ((null != a2lFile2.getPidcA2l()) && (null != a2lFile2.getPidcVersion())) {
      verName2 = a2lFile2.getPidcVersion().getVersionName();
    }
    // comapre version name
    compareResult = ApicUtil.compare(verName1, verName2);
    return compareResult;
  }
}
