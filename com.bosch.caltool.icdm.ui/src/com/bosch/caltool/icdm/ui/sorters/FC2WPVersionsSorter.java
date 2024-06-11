/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.sorters;

import java.util.regex.Pattern;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPVersion;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * Sorter for versions page
 *
 * @author gge6cob
 */
public class FC2WPVersionsSorter extends AbstractViewerSorter {


  private int index;
  private static final int DESCENDING = 1;
  private static final int ASCENDING = 0;
  private int direction = DESCENDING;

  /**
   * Name for working set version
   */
  public static final String WORKING_SET = "Working Set";
  private static final String VERSION_STR = "Version ";


  /**
   * @param index
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

  @Override
  public int compare(final Viewer viewer, final Object object1, final Object object2) {

    FC2WPVersion vers1;
    if (object1 instanceof FC2WPVersion) {
      vers1 = (FC2WPVersion) object1;
    }
    else {
      return 0;
    }
    FC2WPVersion vers2;
    if (object2 instanceof FC2WPVersion) {
      vers2 = (FC2WPVersion) object2;
    }
    else {
      return 0;
    }

    int compareResult = 0;

    switch (this.index) {
      case 0:
        // case of version name column, index 0 is not considered, as it is the second level sorting
        break;
      case 1:
        // version description
        compareResult = ApicUtil.compare(vers1.getDescription(), vers2.getDescription());
        break;
      case 2:
        // version active
        compareResult = ApicUtil.compare(vers1.isActive(), vers2.isActive());
        break;
      case 3:
        // version created date
        compareResult = ApicUtil.compare(vers1.getCreatedDate(), vers2.getCreatedDate());
        break;
      case 4:
        // version created user
        compareResult = ApicUtil.compare(vers1.getCreatedUser(), vers2.getCreatedUser());
        break;
      default:
        compareResult = 0;
    }
    // Second level sorting using version name
    if (compareResult == 0) {
      compareResult = compareVersionName(vers1, vers2);
    }
    // If descending order, flip the direction
    if (this.direction == DESCENDING) {
      compareResult = -compareResult;
    }
    return compareResult;
  }

  /**
   * @param vers1
   * @param vers2
   * @return
   */
  private int compareVersionName(final FC2WPVersion vers1, final FC2WPVersion vers2) {
    int compareResult;
    compareResult = -1;
    if (vers1.getVersionName().equals(WORKING_SET)) {
      compareResult = 1;
    }
    if (vers2.getVersionName().equals(WORKING_SET)) {
      compareResult = -1;
    }
    if (!(vers1.getVersionName().equals(WORKING_SET) || vers2.getVersionName().equals(WORKING_SET))) {
      compareResult = compareVersion(vers1.getVersionName(), vers2.getVersionName());
    }
    return compareResult;
  }

  /**
   * @param version1
   * @param version2
   * @return
   */
  private int compareVersion(final String v1, final String v2) {
    int cmp = 0;
    try {
      String temp1 = v1.replace(VERSION_STR, "").trim();
      String temp2 = v2.replace(VERSION_STR, "").trim();
      String v1Str = normalisedVersion(temp1, ".", 3);
      String v2Str = normalisedVersion(temp2, ".", 3);
      cmp = v1Str.compareTo(v2Str);
    }
    catch (Exception ex) {
      CDMLogger.getInstance().debug("Error occurred while comparing FC2WP versions in Versions Page", ex);
    }
    return cmp;
  }

  /**
   * Normalised version.
   *
   * @param version the version
   * @param sep the sep
   * @param maxWidth the max width
   * @return the string
   */
  public String normalisedVersion(final String version, final String sep, final int maxWidth) {
    String[] split = Pattern.compile(sep, Pattern.LITERAL).split(version);
    StringBuilder sb = new StringBuilder();
    for (String s : split) {
      String format = "%" + maxWidth + 's';
      sb.append(String.format(format, s));
    }
    return sb.toString();
  }

  @Override
  public int getDirection() {
    return this.direction;
  }

}
