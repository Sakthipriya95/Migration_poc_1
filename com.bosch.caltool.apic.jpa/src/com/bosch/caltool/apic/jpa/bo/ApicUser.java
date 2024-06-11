/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TUsecaseFavorite;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicAccessRight;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * This class represents an APIC user as stored in the database table TabV_APIC_User
 *
 * @author hef2fe
 */
public class ApicUser extends ApicObject implements Comparable<ApicUser> {

  /**
   * ICDM-1040 Manager instance for private usecases
   */
  private final UseCaseFavNodesMgr ucFavMgr = new UseCaseFavNodesMgr(this);

  /**
   * Columns for advanced sorting (see method compareTo(ApicUser apicUser2, SortColumns sortColumn))
   *
   * @author he2fe
   */
  public enum SortColumns {
                           /**
                            * User name
                            */
                           SORT_USER_NAME,
                           /**
                            * First Name
                            */
                           SORT_FIRST_NAME,
                           /**
                            * Last Name
                            */
                           SORT_LAST_NAME,
                           /**
                            * Full Name
                            */
                           SORT_FULL_NAME,
                           /**
                            * Department
                            */
                           SORT_DEPARTMENT
  }

  /**
   * the one and only constructor
   *
   * @param apicDataProvider the data provider
   * @param userID user's ID. the unique ID of the user (the primary key in the database)
   */
  public ApicUser(final ApicDataProvider apicDataProvider, final Long userID) {
    super(apicDataProvider, userID);

    // add the ApicUser automatically to the users map
    getDataCache().getAllApicUsersMap().put(userID, this);
  }

  /**
   * Check, if the user ID is valid The ID is valid if the related database entity is available
   *
   * @return TRUE if the userID is valid
   */
  private boolean userIdValid() {
    return getEntityProvider().getDbApicUser(getID()) != null;
  }


  /**
   * Get the unique ID of the user This is the primary key in the database
   *
   * @return the unique ID of the user
   */
  public Long getUserID() {
    return getID();
  }

  /**
   * Get the name of the user (the Windows username)
   *
   * @return The user name
   */
  public String getUserName() {
    if (userIdValid()) {
      // the userID is valid, means getDbApicUser does not return null
      return getEntityProvider().getDbApicUser(getID()).getUsername();
    }


    return null;

  }

  /**
   * Get the firstName of the user
   *
   * @return the firstName of the user
   */
  public String getFirstName() {
    if (userIdValid()) {
      // the userID is valid, means getDbApicUser does not return null
      return getEntityProvider().getDbApicUser(getID()).getFirstname();
    }

    return null;

  }

  /**
   * Get the lastName of the user
   *
   * @return the lastName of the user
   */
  public String getLastName() {
    if (userIdValid()) {
      // the userID is valid, means getDbApicUser does not return null
      return getEntityProvider().getDbApicUser(getID()).getLastname();
    }

    return null;

  }

  /**
   * Get the full name of the user The full name is the lastName concatenated with the firstName
   *
   * @return the users fullName
   */
  public String getFullName() {
    if (userIdValid()) { // the userID is valid, means getDbApicUser does not return null

      final StringBuilder fullName = new StringBuilder();
      if (!CommonUtils.isEmptyString(getLastName())) {
        fullName.append(getLastName()).append(", ");
      }
      if (!CommonUtils.isEmptyString(getFirstName())) {
        fullName.append(getFirstName());
      }

      if (CommonUtils.isEmptyString(fullName.toString())) {
        fullName.append(getUserName());
      }

      return fullName.toString();
    }

    return null;

  }

  /**
   * Get the display name of this user
   *
   * @return display name as <Last name> <first name>(<department>)
   */
  public String getDisplayName() {
    if (userIdValid()) { // the userID is valid, means getDbApicUser does not return null
      final StringBuilder displayName = new StringBuilder();

      displayName.append(getFullName());

      if (!CommonUtils.isEmptyString(getDepartment())) {
        displayName.append(" (").append(getDepartment()).append(")");
      }

      return displayName.toString();
    }

    return null;

  }

  /**
   * Get the display name of this user, in a different format
   *
   * @return display name as <Last name> <first name>(<department>)
   */
  public String getDisplayName2() {
    // TODO : To have own implementation when phone number is also available in iCDM system.
    return getDisplayName();
  }


  /**
   * Get the department of the user
   *
   * @return the department of the user
   */
  public String getDepartment() {
    if (userIdValid()) {
      // the userID is valid, means getDbApicUser does not return null
      return getEntityProvider().getDbApicUser(getID()).getDepartment();
    }

    return null;

  }

  /**
   * Get the general access right of the user for a particular module Returns NULL, if the userID is not valid or the
   * userID has no access right for the module.
   *
   * @param moduleName the module name
   * @return The access right
   */
  public String getAccessRight(final String moduleName) {
    if (userIdValid()) {

      // search the access right for the given module name
      for (TabvApicAccessRight accessRight : getEntityProvider().getDbApicUser(getID()).getTabvApicAccessRights()) {
        if (accessRight.getModuleName().equals(moduleName)) {
          return accessRight.getAccessRight();
        }
      }

      // no access right found for the module
      return null;
    }

    // invalid userID
    return null;

  }

  /**
   * Check, if the user has a special APIC access right
   *
   * @param requiredAccessRight the required accessRight
   * @return TRUE, if the user has the required accessRight
   */
  private boolean getApicAccessRight(final String requiredAccessRight) {
    String accessRight = getAccessRight(ApicConstants.APIC_MODULE_NAME);

    if (accessRight != null) {
      return accessRight.equals(requiredAccessRight);
    }

    return false;

  }

  /**
   * Check if the user has write access to the APIC database A user with write access has full access to the database
   *
   * @return TRUE if the user has write access
   */
  public boolean hasApicWriteAccess() {

    return getApicAccessRight(ApicConstants.APIC_WRITE_ACCESS);
  }

  /**
   * Check if the user has PIDC write access to the APIC database A user with PIDC write access can create new PIDC, but
   * can not maintain attributes and values
   *
   * @return TRUE if the user has PIDC write access
   */
  public boolean hasPidcWriteAccess() {
    return getApicAccessRight(ApicConstants.PIDC_WRITE_ACCESS);
  }

  /**
   * Check, if the user is allowed to create SuperGroups Currently, this is only possible with ApicWriteAccess
   *
   * @return TRUE if the user can create SuperGroups
   */
  public boolean canCreateSuperGroup() {
    return getApicAccessRight(ApicConstants.APIC_WRITE_ACCESS);
  }

  /**
   * Check, if the user is allowed to create Groups Currently, this is only possible with ApicWriteAccess
   *
   * @return TRUE if the user can create Groups
   */
  public boolean canCreateGroup() {
    return getApicAccessRight(ApicConstants.APIC_WRITE_ACCESS);
  }

  /**
   * Check, if the user is allowed to create Attributes Currently, this is only possible with ApicWriteAccess
   *
   * @return TRUE if the user can create Attributes
   */

  public boolean canCreateAttribute() {
    return getApicAccessRight(ApicConstants.APIC_WRITE_ACCESS);
  }

  /**
   * Check, if the user is allowed to create PIDC Currently, this is only possible with PidcWriteAccess or
   * ApicWriteAccess
   *
   * @return TRUE if the user can create SuperGroups
   */
  public boolean canCreatePIDC() {
    return hasApicWriteAccess() || hasPidcWriteAccess();
  }

  /**
   * Checks, if the user is allowed to create UseCase Group or UseCase, this is possibly only if the user has
   * ApicWriteAccess
   *
   * @return TRUE if the user can create UseCaseGroups or UseCase
   */
  public boolean canCreateUseCase() {
    return hasApicWriteAccess();
  }

  /**
   * Checks, if the user is allowed to create Component Package, this is possibly only if the user has ApicWriteAccess
   *
   * @return TRUE if the user can create Component Package
   */
  public boolean canCreateCompPackage() {
    return hasApicWriteAccess();
  }

  /**
   * Get the sorted list of the users favorite PIDC
   *
   * @return the sorted list of the users PIDC
   */
  public SortedSet<PIDCVersion> getPidcFavorites() {

    return getDataLoader().getUserPidcFavVersions(this);
  }

  /**
   * Add a PIDC as favorite of the user
   *
   * @param newFavorite the PID Card
   * @return TURE, if new favorite created, FALSE if favorite still exists
   */
  protected boolean addPidcFavorite(final PIDCVersion newFavorite) {

    return getDataLoader().addUserPidcFavorite(this, newFavorite);

  }

  /**
   * Remove a PIDC from the users favorites list
   *
   * @param favorite the favorite PIDC to be removed
   * @return TRUE, if the PIDC has been removed
   */
  protected boolean removePidcFavorite(final PIDCVersion favorite) {

    return getDataLoader().removeUserPidcFavorite(this, favorite);

  }

  /**
   * Get the creation date of the User
   *
   * @return The date when the user has been created in the database
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbApicUser(getID()).getCreatedDate());
  }


  /**
   * Get the ID of the user who has created the User
   *
   * @return The ID of the user who has created the User
   */
  @Override
  public String getCreatedUser() {
    if (userIdValid()) {
      return getEntityProvider().getDbApicUser(getID()).getCreatedUser();
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
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbApicUser(getID()).getModifiedDate());
  }

  /**
   * Get the date when the User has accepted the disclaimer the last time
   *
   * @return The date when the User has accepted the disclaimer the last time
   */

  public Calendar getDisclaimerAcceptedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbApicUser(getID()).getDisclaimerAcceptnceDate());
  }

  /**
   * Get the user who has modified the User the last time
   *
   * @return The user who has modified the User the last time
   */
  @Override
  public String getModifiedUser() {
    if (userIdValid()) {
      return getEntityProvider().getDbApicUser(getID()).getModifiedUser();
    }

    return "";

  }

  /**
   * Compares this apic user with another apic user based on column to sort
   *
   * @param apicUser2 the second apic user
   * @param sortColumn column selected for sorting
   * @return the int value based on String.compare() method
   */
  public int compareTo(final ApicUser apicUser2, final SortColumns sortColumn) {
    int compareResult;

    switch (sortColumn) {
      case SORT_USER_NAME:
        // compare the user IDs
        compareResult = ApicUtil.compare(getUserName(), apicUser2.getUserName());
        break;

      case SORT_FIRST_NAME:
        // compare the first names
        compareResult = ApicUtil.compare(getFirstName(), apicUser2.getFirstName());
        break;

      case SORT_LAST_NAME:
        // compare the last names
        compareResult = ApicUtil.compare(getLastName(), apicUser2.getLastName());
        break;

      case SORT_FULL_NAME:
        // compare the last names
        compareResult = ApicUtil.compare(getFullName(), apicUser2.getFullName());
        break;

      case SORT_DEPARTMENT:
        // compare the departments
        compareResult = ApicUtil.compare(getDepartment(), apicUser2.getDepartment());
        break;


      default:
        compareResult = 0;
        break;
    }

    if (compareResult == 0) {
      // compare result is equal, compare the last name
      compareResult = ApicUtil.compare(getLastName(), apicUser2.getLastName());

      if (compareResult == 0) {
        // compare result is still equal, compare the first name
        compareResult = ApicUtil.compare(getFirstName(), apicUser2.getFirstName());

        if (compareResult == 0) {
          // compare result is still equal, compare the userId
          compareResult = ApicUtil.compare(getUserName(), apicUser2.getUserName());
        }

      }

    }

    return compareResult;
  }


  /**
   * {@inheritDoc} returns the version
   */
  @Override
  public Long getVersion() {
    return getEntityProvider().getDbApicUser(getID()).getVersion();
  }

  /**
   * {@inheritDoc} returns true as there is no restriction
   */
  @Override
  public boolean isModifiable() {
    // currently no restrictions
    return true;
  }

  /**
   * {@inheritDoc} returns the compare result of the usernames
   */
  @Override
  public int compareTo(final ApicUser apicUser2) {
    // by default use the userName
    return ApicUtil.compare(getUserName(), apicUser2.getUserName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }

    final ApicUser apicUser = (ApicUser) obj;
    return getUserID().equals(apicUser.getUserID());
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
    return getUserName();
  }

  /**
   * Returns <code>EntityType.APIC_USER</code>
   * <p>
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return EntityType.APIC_USER;
  }

  /**
   * ICDM-1027
   *
   * @return Map of favourite usecase items for this project id card
   */
  public Map<Long, FavUseCaseItem> getFavoriteUCNodes() {
    return getDataCache().getCurrentUserUCFavMap();
  }

  /**
   * ICDM-1040 Gets the root level nodes of this user.
   *
   * @return Uc fav Nodes at the root level
   */
  public SortedSet<FavUseCaseItemNode> getRootUcFavNodes() {
    // fetch the fav uc items
    getFavoriteUCMap();
    return this.ucFavMgr.getRootVirtualNodes();
  }

  /**
   * ICDM-1028
   *
   * @return Map of favourite usecase items for this user
   */
  public Map<Long, FavUseCaseItem> getFavoriteUCMap() {
    Map<Long, FavUseCaseItem> favUcMap = getDataCache().getCurrentUserUCFavMap();
    if (favUcMap.isEmpty()) {
      for (TUsecaseFavorite userFavUcItem : getEntityProvider().getDbApicUser(getID()).getTUsecaseFavorites()) {
        favUcMap.put(userFavUcItem.getUcFavId(),
            getDataCache().getFavUcItem(userFavUcItem.getUcFavId(), false, getID()));
      }
    }

    return favUcMap;
  }

  /**
   * @param favUcItem FavUseCaseItem
   */
  public void refreshFavNodes(final FavUseCaseItem favUcItem) {
    this.ucFavMgr.refreshNodes(favUcItem);

  }

  /**
   * reset the virtual structure by clearing the root nodes
   */
  public void resetFavNodes() {
    this.ucFavMgr.clearRootNodes();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return getDisplayName();
  }

  /**
   * Task 244427
   *
   * @return true if expired
   */
  public boolean isDisclaimerAcceptanceExpired() {

    Calendar lastAcceptedDate = getDisclaimerAcceptedDate();
    if (null == lastAcceptedDate) {
      return true;
    }
    long diffInDays = TimeUnit.MILLISECONDS
        .toDays(Math.abs(Calendar.getInstance().getTimeInMillis() - lastAcceptedDate.getTimeInMillis()));
    Long intervalDays =
        Long.valueOf(((ApicDataProvider) getDataProvider()).getParameterValue(ApicConstants.DISCLAIMER_VALID_INTERVAL));

    return diffInDays >= intervalDays;

  }
  /**
   * @return
   */
  public boolean checkIfUserHasNoAccess(){
    return getApicAccessRight(ApicConstants.NO_ACCESS);
  }
}
