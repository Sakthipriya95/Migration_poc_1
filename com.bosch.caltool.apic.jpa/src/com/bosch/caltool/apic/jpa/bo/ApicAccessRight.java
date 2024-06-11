package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;


/**
 * This class represents a user accessRight as stored in the database table TabV_APIC_AccessRight
 *
 * @author hef2fe
 */
public class ApicAccessRight extends ApicObject implements Comparable<ApicAccessRight> {

  /**
   * columns for extended sorting
   *
   * @author hef2fe
   */
  public enum SortColumns {
                           /**
                            * User Name Column
                            */
                           SORT_USER_NAME,
                           /**
                            * Module Name column
                            */
                           SORT_MODULE_NAME,
                           /**
                            * Access Right column
                            */
                           SORT_ACCESS_RIGHT
  }

  /**
   * @param apicDataProvider apic Data Provider
   * @param accrID the unique ID of the users access right in TabV_APIC_AccessRights
   */
  protected ApicAccessRight(final ApicDataProvider apicDataProvider, final Long accrID) {
    super(apicDataProvider, accrID);


    getDataCache().getAllApicAccessRightsMap().put(accrID, this);
  }

  /**
   * Check, if the user ID is valid The ID is valid if the related database entity is available
   *
   * @return TRUE if the userID is valid
   */
  private boolean accrIdValid() {
    return getEntityProvider().getDbApicAccessRight(getID()) != null;
  }

  /**
   * Get the ApicUser of the user belonging to this accessRight
   *
   * @return The ApicUser object
   */
  public ApicUser getApicUser() {
    return getDataCache().getApicUser(getUserName());
  }

  /**
   * Get the name of the user (the Windows username)
   *
   * @return The Windows userName
   */
  public String getUserName() {
    if (accrIdValid()) {
      return getEntityProvider().getDbApicAccessRight(getID()).getTabvApicUser().getUsername();
    }
    return null;
  }

  /**
   * Get the name of the module for this accessRight
   *
   * @return The moduleName
   */
  public String getModuleName() {
    if (accrIdValid()) {
      return getEntityProvider().getDbApicAccessRight(getID()).getModuleName();
    }
    return null;
  }

  /**
   * Get the firstName of the user
   *
   * @return the firstName of the user
   */
  public String getFirstName() {
    return getApicUser().getFirstName();
  }

  /**
   * Get the lastName of the user
   *
   * @return the lastName of the user
   */
  public String getLastName() {
    return getApicUser().getLastName();
  }

  /**
   * Get the full name of the user The full name is the lastName concatenated with the firstName
   *
   * @return the users fullName
   */
  public String getFullName() {
    return getApicUser().getFullName();
  }

  /**
   * Get the department of the user
   *
   * @return the department of the user
   */
  public String getDepartment() {
    return getApicUser().getDepartment();
  }

  /**
   * Get the general APIC access right of the user
   *
   * @return The access right
   */
  public String getAccessRight() {
    if (accrIdValid()) {
      return getEntityProvider().getDbApicAccessRight(getID()).getAccessRight();
    }
    return null;
  }

  /**
   * Get the creation date of the User
   *
   * @return The date when the user has been created in the database
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbApicAccessRight(getID()).getCreatedDate());
  }


  /**
   * Get the ID of the user who has created the User
   *
   * @return The ID of the user who has created the User
   */
  @Override
  public String getCreatedUser() {
    if (accrIdValid()) {
      return getEntityProvider().getDbApicAccessRight(getID()).getCreatedUser();
    }
    return "";
  }

  /**
   * Get the date when the User has been modified the last time
   *
   * @return The date when the User has been modified the last time
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbApicAccessRight(getID()).getModifiedDate());
  }

  /**
   * Get the user who has modified the User the last time
   *
   * @return The user who has modified the User the last time
   */
  @Override
  public String getModifiedUser() {
    if (accrIdValid()) {
      return getEntityProvider().getDbApicAccessRight(getID()).getModifiedUser();
    }
    return "";
  }

  /**
   * Compares this apic user with another apic user based on column to sort
   *
   * @param accessRight2 the second access right
   * @param sortColumn column selected for sorting
   * @return the int value based on String.compare() method
   */
  public int compareTo(final ApicAccessRight accessRight2, final SortColumns sortColumn) {
    int compareResult;

    switch (sortColumn) {
      case SORT_USER_NAME:
        // compare the userNames
        compareResult = ApicUtil.compare(getUserName(), accessRight2.getUserName());

        break;

      case SORT_MODULE_NAME:
        // compare the module names
        compareResult = ApicUtil.compare(getModuleName(), accessRight2.getModuleName());

        break;

      case SORT_ACCESS_RIGHT:
        // compare the accessRiths
        compareResult = ApicUtil.compare(getAccessRight(), accessRight2.getAccessRight());

        break;

      default:
        compareResult = 0;
        break;
    }

    if (compareResult == 0) {
      // both objects are equal, use the default sort order
      compareResult = this.compareTo(accessRight2);
    }

    return compareResult;
  }


  /**
   * {@inheritDoc} returns version of the access right
   */
  @Override
  public Long getVersion() {
    return getEntityProvider().getDbApicAccessRight(getID()).getVersion();
  }

  /**
   * {@inheritDoc} return true since ApicAccessRight can be modified
   */
  @Override
  public boolean isModifiable() {
    return true;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final ApicAccessRight accessRight2) {
    // the default sort order is ModuleName, UserName
    // both together are unique

    int compareResult;

    // compare the module names
    compareResult = ApicUtil.compare(getModuleName(), accessRight2.getModuleName());

    if (compareResult == 0) {
      // compare result is equal, compare the user name
      compareResult = ApicUtil.compare(getUserName(), accessRight2.getUserName());
    }

    return compareResult;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    // module name, by default
    return getModuleName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    // TODO Auto-generated method stub
    return null;
  }

}
