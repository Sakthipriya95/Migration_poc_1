/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import com.bosch.caltool.apic.jpa.bo.ApicDataProvider;
import com.bosch.caltool.apic.jpa.bo.ApicUser;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;

/**
 * This class is a wrapper class for the bo WorkPackageDetails to implement sorting..
 *
 * @author dmo5cob
 */
public class WorkPackageDetailsWrapper implements Comparable<WorkPackageDetailsWrapper> {

  /**
   * Columns for advanced sorting
   */
  public enum SortColumns {
                           /**
                            * division name
                            */
                           SORT_DIVISION,
                           /**
                            * resource Name
                            */
                           SORT_RESOURCE,
                           /**
                            * mcr id
                            */
                           SORT_MCR,
                           /**
                            * resource Name
                            */
                           SORT_PRIMARY_CONTACT,
                           /**
                            * resource Name
                            */
                           SORT_SEC_CONTACT
  }


  private final WorkPackageDivision workPkgDetails;
  private final ApicDataProvider apicDp;


  /**
   * @param wpDetails WorkPackageDetails
   * @param apicDp ApicDataProvider
   */
  public WorkPackageDetailsWrapper(final WorkPackageDivision wpDetails, final ApicDataProvider apicDp) {
    this.workPkgDetails = wpDetails;
    this.apicDp = apicDp;

  }

  /**
   * @return division name
   */
  public String getDivisionName() {
    return this.workPkgDetails.getDivName();
  }

  /**
   * @return resource name
   */
  public String getResourceName() {
    return this.workPkgDetails.getWpResource();
  }

  /**
   * @return mcr name
   */
  public String getMCR() {
    return this.workPkgDetails.getWpIdMcr();
  }

  /**
   * @return PrimaryContact
   */
  public String getPrimaryContact() {
    if (null != this.workPkgDetails.getContactPersonId()) {
      ApicUser apicuser = this.apicDp.getApicUserById(this.workPkgDetails.getContactPersonId());
      return apicuser.getDisplayName();
    }
    return ApicConstants.EMPTY_STRING;
  }

  /**
   * @return Sec Contact
   */
  public String getSecondaryContact() {
    if (null != this.workPkgDetails.getContactPersonSecondId()) {
      ApicUser apicuser = this.apicDp.getApicUserById(this.workPkgDetails.getContactPersonSecondId());
      return apicuser.getDisplayName();
    }
    return ApicConstants.EMPTY_STRING;
  }

  /**
   * @return Sec Contact
   */
  public String getDeleted() {
    return this.workPkgDetails.getDeleted();
  }

  /**
   * Compares this wp with another wp based on column to sort
   *
   * @param wp2 the second wp
   * @param sortColumn column selected for sorting
   * @return the int value based on String.compare() method
   */
  public int compareTo(final WorkPackageDetailsWrapper wp2, final SortColumns sortColumn) {
    int compareResult;

    switch (sortColumn) {
      case SORT_DIVISION:
        compareResult = ApicUtil.compare(getDivisionName(), wp2.getDivisionName());
        break;

      case SORT_RESOURCE:
        // compare the resources
        compareResult = ApicUtil.compare(getResourceName(), wp2.getResourceName());
        break;
      case SORT_MCR:
        // compare the mcrs
        compareResult = ApicUtil.compare(getMCR(), wp2.getMCR());
        break;
      case SORT_PRIMARY_CONTACT:
        // compare the primary contact
        compareResult = ApicUtil.compare(getPrimaryContact(), wp2.getPrimaryContact());
        break;
      case SORT_SEC_CONTACT:
        // compare the sec contact
        compareResult = ApicUtil.compare(getPrimaryContact(), wp2.getSecondaryContact());
        break;
      default:
        compareResult = 0;
        break;
    }

    if (compareResult == 0) {
      // compare result is equal, compare the last name
      compareResult = ApicUtil.compare(getDivisionName().concat(getMCR()), wp2.getDivisionName().concat(wp2.getMCR()));
    }

    return compareResult;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final WorkPackageDetailsWrapper o) {
    return ApicUtil.compare(getDivisionName().concat(getMCR()), o.getDivisionName().concat(o.getMCR()));

  }


  /**
   * @return the workPkgDetails
   */
  public WorkPackageDivision getWorkPkgDetails() {
    return this.workPkgDetails;
  }

}
