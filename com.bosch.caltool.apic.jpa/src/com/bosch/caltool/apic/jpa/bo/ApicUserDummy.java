/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;
import java.util.Map;
import java.util.SortedSet;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * This class represents an dummy APIC user
 *
 * @author dmo5cob
 */
public class ApicUserDummy extends ApicUser {

  /**
   * Constant for dummy ID value
   */
  public static final Long USER_ID = -1L;

  /**
   * the one and only constructor
   *
   * @param apicDataProvider the data provider
   */
  public ApicUserDummy(final ApicDataProvider apicDataProvider) {
    super(apicDataProvider, USER_ID);
  }


  /**
   * Get the unique ID of the user This is the primary key in the database
   *
   * @return the unique ID of the user
   */
  @Override
  public Long getUserID() {
    return getID();
  }

  /**
   * Get the name of the user (the Windows username)
   *
   * @return The user name
   */
  @Override
  public String getUserName() {
    return ApicConstants.EMPTY_STRING;
  }

  /**
   * Get the firstName of the user
   *
   * @return the firstName of the user
   */
  @Override
  public String getFirstName() {
    return ApicConstants.EMPTY_STRING;
  }

  /**
   * Get the lastName of the user
   *
   * @return the lastName of the user
   */
  @Override
  public String getLastName() {
    return ApicConstants.EMPTY_STRING;
  }

  /**
   * Get the full name of the user The full name is the lastName concatenated with the firstName
   *
   * @return the users fullName
   */
  @Override
  public String getFullName() {
    return ApicConstants.EMPTY_STRING;
  }

  /**
   * Get the display name of this user
   *
   * @return display name as <Last name> <first name>(<department>)
   */
  @Override
  public String getDisplayName() {
    return ApicConstants.EMPTY_STRING;

  }

  /**
   * Get the display name of this user, in a different format
   *
   * @return display name as <Last name> <first name>(<department>)
   */
  @Override
  public String getDisplayName2() {
    return ApicConstants.EMPTY_STRING;
  }


  /**
   * Get the department of the user
   *
   * @return the department of the user
   */
  @Override
  public String getDepartment() {
    return ApicConstants.EMPTY_STRING;
  }

  /**
   * Get the general access right of the user for a particular module Returns NULL, if the userID is not valid or the
   * userID has no access right for the module.
   *
   * @param moduleName the module name
   * @return The access right
   */
  @Override
  public String getAccessRight(final String moduleName) {
    return ApicConstants.EMPTY_STRING;
  }


  /**
   * Check if the user has write access to the APIC database A user with write access has full access to the database
   *
   * @return TRUE if the user has write access
   */
  @Override
  public boolean hasApicWriteAccess() {

    return false;
  }

  /**
   * Check if the user has PIDC write access to the APIC database A user with PIDC write access can create new PIDC, but
   * can not maintain attributes and values
   *
   * @return TRUE if the user has PIDC write access
   */
  @Override
  public boolean hasPidcWriteAccess() {
    return false;
  }

  /**
   * Check, if the user is allowed to create SuperGroups Currently, this is only possible with ApicWriteAccess
   *
   * @return TRUE if the user can create SuperGroups
   */
  @Override
  public boolean canCreateSuperGroup() {
    return false;
  }

  /**
   * Check, if the user is allowed to create Groups Currently, this is only possible with ApicWriteAccess
   *
   * @return TRUE if the user can create Groups
   */
  @Override
  public boolean canCreateGroup() {
    return false;
  }

  /**
   * Check, if the user is allowed to create Attributes Currently, this is only possible with ApicWriteAccess
   *
   * @return TRUE if the user can create Attributes
   */

  @Override
  public boolean canCreateAttribute() {
    return false;
  }

  /**
   * Check, if the user is allowed to create PIDC Currently, this is only possible with PidcWriteAccess or
   * ApicWriteAccess
   *
   * @return TRUE if the user can create SuperGroups
   */
  @Override
  public boolean canCreatePIDC() {
    return false;
  }

  /**
   * Checks, if the user is allowed to create UseCase Group or UseCase, this is possibly only if the user has
   * ApicWriteAccess
   *
   * @return TRUE if the user can create UseCaseGroups or UseCase
   */
  @Override
  public boolean canCreateUseCase() {
    return false;
  }

  /**
   * Checks, if the user is allowed to create Component Package, this is possibly only if the user has ApicWriteAccess
   *
   * @return TRUE if the user can create Component Package
   */
  @Override
  public boolean canCreateCompPackage() {
    return false;
  }

  /**
   * Get the sorted list of the users favorite PIDC
   *
   * @return the sorted list of the users PIDC
   */
  @Override
  public SortedSet<PIDCVersion> getPidcFavorites() {

    return null;
  }

  /**
   * Add a PIDC as favorite of the user
   *
   * @param newFavorite the PID Card
   * @return TURE, if new favorite created, FALSE if favorite still exists
   */
  @Override
  protected boolean addPidcFavorite(final PIDCVersion newFavorite) {

    return false;

  }

  /**
   * Remove a PIDC from the users favorites list
   *
   * @param favorite the favorite PIDC to be removed
   * @return TRUE, if the PIDC has been removed
   */
  @Override
  protected boolean removePidcFavorite(final PIDCVersion favorite) {

    return false;

  }

  /**
   * Get the creation date of the User
   *
   * @return The date when the user has been created in the database
   */
  @Override
  public Calendar getCreatedDate() {
    return null;
  }


  /**
   * Get the ID of the user who has created the User
   *
   * @return The ID of the user who has created the User
   */
  @Override
  public String getCreatedUser() {
    return ApicConstants.EMPTY_STRING;
  }

  /**
   * Get the date when the User has been modified the last time
   *
   * @return The date when the User has been modified the last time
   */
  @Override
  public Calendar getModifiedDate() {
    return null;
  }

  /**
   * Get the user who has modified the User the last time
   *
   * @return The user who has modified the User the last time
   */
  @Override
  public String getModifiedUser() {
    return ApicConstants.EMPTY_STRING;
  }


  /**
   * {@inheritDoc} returns the version
   */
  @Override
  public Long getVersion() {
    return null;
  }

  /**
   * {@inheritDoc} returns true as there is no restriction
   */
  @Override
  public boolean isModifiable() {

    return false;
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

    final ApicUserDummy apicUser = (ApicUserDummy) obj;
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
    return null;
  }

  /**
   * ICDM-1027
   *
   * @return Map of favourite usecase items for this project id card
   */
  @Override
  public Map<Long, FavUseCaseItem> getFavoriteUCNodes() {
    return null;
  }

  /**
   * @return Uc fav Nodes at the root level
   */
  @Override
  public SortedSet<FavUseCaseItemNode> getRootUcFavNodes() {
    return null;
  }

  /**
   * ICDM-1028
   *
   * @return Map of favourite usecase items for this user
   */
  @Override
  public Map<Long, FavUseCaseItem> getFavoriteUCMap() {

    return null;
  }

  /**
   * @param favUcItem FavUseCaseItem
   */
  @Override
  public void refreshFavNodes(final FavUseCaseItem favUcItem) {
    // NA
  }

  /**
   * reset the virtual structure by clearing the root nodes
   */
  @Override
  public void resetFavNodes() {
    // NA
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return getDisplayName();
  }
}
