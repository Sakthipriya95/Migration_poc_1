/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCase;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCaseGroup;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * This class represents usecase group as stored in the database table TABV_USE_CASE_GROUPS
 *
 * @author bne4cob
 */
public class UseCaseGroup extends AbstractUseCaseItem {


  /**
   * Child groups
   */
  private Set<UseCaseGroup> childGroups;

  /**
   * Child use cases
   */
  private Set<UseCase> useCases;

  /**
   * Constructor
   *
   * @param apicDataProvider data provider
   * @param groupID unique id of this group
   */
  public UseCaseGroup(final ApicDataProvider apicDataProvider, final Long groupID) {
    super(apicDataProvider, groupID);
  }

  /**
   * @param includeDeleted whether the deleted items to be included or not
   * @return set of Child Groups
   */
  private final Set<UseCaseGroup> getChildGroups(final boolean includeDeleted) {
    if (includeDeleted) {
      return getChildGroups();
    }

    final Set<UseCaseGroup> ucgSet = new HashSet<UseCaseGroup>();

    for (UseCaseGroup childUcg : getChildGroups()) {
      if ((childUcg == null) || childUcg.isDeleted()) {
        continue;
      }
      ucgSet.add(childUcg);
    }

    return ucgSet;

  }

  /**
   * Retrieves the set of child groups directly below this group. The set is initialised when the method is invoked for
   * the first time.
   *
   * @return the childGroupSet
   */
  protected final Set<UseCaseGroup> getChildGroups() {
    if (this.childGroups == null) {
      this.childGroups = new HashSet<UseCaseGroup>();
      for (TabvUseCaseGroup childDbUcg : getEntityProvider().getDbUseCaseGroup(getID()).getTabvUseCaseGroups()) {
        this.childGroups.add(getDataCache().getAllUseCaseGroupMap().get(childDbUcg.getGroupId()));
      }
    }
    return this.childGroups;
  }

  /**
   * Get the sorted set of the Child Groups in this group.
   *
   * @param includeDeleted whether the deleted items to be included or not
   * @return the childGroupSet
   */
  public final SortedSet<UseCaseGroup> getChildGroupSet(final boolean includeDeleted) {
    return new TreeSet<UseCaseGroup>(getChildGroups(includeDeleted));
  }

  /**
   * @param includeDeleted whether the deleted items to be included or not
   * @return set of use cases
   */
  private final Set<UseCase> getUseCases(final boolean includeDeleted) {
    if (includeDeleted) {
      return getUseCases();
    }
    final Set<UseCase> ucSet = new HashSet<UseCase>();

    for (UseCase ucase : getUseCases()) {
      if (ucase.isDeleted()) {
        continue;
      }
      ucSet.add(ucase);
    }

    return ucSet;
  }


  /**
   * Returns the set of use cases in this group. The set is initialised when the method is invoked for the first time.
   *
   * @return the useCaseSet
   */
  protected final Set<UseCase> getUseCases() {
    if (this.useCases == null) {
      this.useCases = new HashSet<UseCase>();
      for (TabvUseCase dbUc : getEntityProvider().getDbUseCaseGroup(getID()).getTabvUseCases()) {
        this.useCases.add(getDataCache().getAllUseCaseMap().get(dbUc.getUseCaseId()));
      }
    }
    return this.useCases;
  }

  /**
   * Get the sorted set of the use cases in this group.
   *
   * @param includeDeleted whether the deleted items to be included or not
   * @return the use Case Set
   */
  public final SortedSet<UseCase> getUseCaseSet(final boolean includeDeleted) {
    return new TreeSet<UseCase>(getUseCases(includeDeleted));
  }

  /**
   * gets the name in english
   */
  @Override
  public final String getNameEng() {
    if (isIdValid()) {
      String returnValue = getEntityProvider().getDbUseCaseGroup(getID()).getNameEng();
      if (returnValue == null) {
        returnValue = "";
      }
      return returnValue;
    }

    return "";

  }

  /**
   * gets the name in german
   */
  @Override
  public final String getNameGer() {
    if (isIdValid()) {
      String returnValue = getEntityProvider().getDbUseCaseGroup(getID()).getNameGer();
      if (returnValue == null) {
        returnValue = "";
      }
      return returnValue;
    }

    return "";
  }

  /**
   * gets the description in english
   */
  @Override
  public final String getDescEng() {
    if (isIdValid()) {
      String returnValue = getEntityProvider().getDbUseCaseGroup(getID()).getDescEng();
      if (returnValue == null) {
        returnValue = "";
      }
      return returnValue;
    }

    return "";
  }

  /**
   * gets the description in german
   */
  @Override
  public final String getDescGer() {
    if (isIdValid()) {
      String returnValue = getEntityProvider().getDbUseCaseGroup(getID()).getDescGer();
      if (returnValue == null) {
        returnValue = "";
      }
      return returnValue;
    }

    return "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String getCreatedUser() {
    if (isIdValid()) {
      return getEntityProvider().getDbUseCaseGroup(getID()).getCreatedUser();
    }
    return "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final Calendar getCreatedDate() {
    if (isIdValid()) {
      return ApicUtil.timestamp2calendar(getEntityProvider().getDbUseCaseGroup(getID()).getCreatedDate());
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final Calendar getModifiedDate() {
    if (isIdValid()) {
      return ApicUtil.timestamp2calendar(getEntityProvider().getDbUseCaseGroup(getID()).getModifiedDate());
    }
    return null;
  }

  /**
   * if the id is valid, then returns the user
   */
  @Override
  public final String getModifiedUser() {
    if (isIdValid()) {
      return getEntityProvider().getDbUseCaseGroup(getID()).getModifiedUser();
    }
    return "";

  }

  /**
   * Check, if the ID is valid. The ID is valid if the related database entity is available.
   *
   * @return TRUE if the ID is valid
   */
  @Override
  protected final boolean isIdValid() {
    return getEntityProvider().getDbUseCaseGroup(getID()) != null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final boolean isMapped(final Attribute attr) {

    // Check whether child groups are mapped
    for (UseCaseGroup childUcg : getChildGroups(false)) {
      if (childUcg.isMapped(attr)) {
        return true;
      }
    }

    // Check whether use cases are mapped
    for (UseCase ucase : getUseCases(false)) {
      if (ucase.isMapped(attr)) {
        return true;
      }
    }

    // if no mapping done, then return false
    return false;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final boolean canMapAttributes() {
    // Not applicable
    return false;
  }

  /**
   * {@inheritDoc}
   * <p>
   * The return is an empty collection for use case group.
   */
  @Override
  public final SortedSet<AbstractUseCaseItem> getMappableItems() {
    // initialise the treeset of abstract use case item
    return new TreeSet<AbstractUseCaseItem>();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isDeleted() {
    if (isIdValid()) {
      return ApicConstants.YES.equals(getEntityProvider().getDbUseCaseGroup(getID()).getDeletedFlag());
    }
    // if the id is not valid, then return false
    return false;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isAllUCItemsMapped(final Attribute attribute) {
    // Not applicable
    return false;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isAllUCItemsUnMapped(final Attribute attribute) {
    // Not applicable
    return false;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isAnyUCItemMapped(final Attribute attribute) {
    // Not applicable
    return false;
  }

  // Icdm-368
  /**
   * gets the parent use case item
   */
  @Override
  public AbstractUseCaseItem getParent() {
    TabvUseCaseGroup dbUcGrp = getEntityProvider().getDbUseCaseGroup(getID()).getTabvUseCaseGroup();
    if (dbUcGrp != null) {
      return getDataCache().getUseCaseGroup(dbUcGrp.getGroupId());
    }
    return null;
  }


  /**
   * returns false if the use group is deleted or any parent level deleted or no cache for apic write access of current
   * user
   */
  @Override
  public boolean isModifiable() {
    if (isDeleted() || isParentLevelDeleted() || !getDataCache().getCurrentUser().hasApicWriteAccess()) {
      return false;
    }
    return true;

  }

  // ICDM-2610
  /**
   * Not Applicable for Use case group
   */
  @Override
  public boolean isModifyCellAllowed() {
    return false;
  }

  /**
   * Use Case Check icdm-368 made in model
   *
   * @return if there is already a non deleted Use case Child group we cannot create a Use Case
   */
  public boolean canCreateUseCase() {
    return this.getChildGroups(false).isEmpty();
  }

  /**
   * Use Case Check icdm-368 made in model
   *
   * @return if there is already a non deleted Use case we cannot create a sub group
   */
  public boolean canCreateSubGroup() {
    return this.getUseCases(false).isEmpty();
  }

  /**
   * @return No Implementation required
   */
  @Override
  public boolean isMappingModifiable() {
    return false;
  }

  // Icdm-467 Use case DCN
  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return getID().intValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }

    if (getID().intValue() == ((UseCaseGroup) obj).getID().intValue()) {
      return true;
    }

    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return EntityType.USE_CASE_GROUP;
  }

  /**
   * {@inheritDoc} returns child uc group set if it is not empty. Otherwise child usecase set.
   */
  @Override
  public SortedSet<AbstractUseCaseItem> getChildUCItems() {
    SortedSet<AbstractUseCaseItem> childSet = new TreeSet<AbstractUseCaseItem>();
    if (CommonUtils.isEmptySet(getChildGroupSet(false))) {
      childSet.addAll(getUseCaseSet(false));
    }
    else {
      childSet.addAll(getChildGroupSet(false));
    }
    return childSet;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isFocusMatrixRelevant(final boolean checkRelevancyInParent) {
    // Not Applicable
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<AbstractUseCaseItem> getChildFocusMatrixItems() {
    SortedSet<AbstractUseCaseItem> childSet = new TreeSet<AbstractUseCaseItem>();
    if (CommonUtils.isNullOrEmpty(getChildGroupSet(false))) {
      childSet.addAll(getFocusMatrixRelevantUseCases(getUseCases()));
    }
    else {
      for (UseCaseGroup childGrp : getChildGroupSet(false)) {
        if (!childGrp.getChildFocusMatrixItems().isEmpty()) {
          childSet.add(childGrp);
        }
      }
    }
    return childSet;
  }

  /**
   * @param includeDeleted whether the deleted items to be included or not
   * @param usecaseSet
   * @return set of use cases
   */
  private final Set<UseCase> getFocusMatrixRelevantUseCases(final Set<UseCase> usecaseSet) {

    Set<UseCase> ucSet = new HashSet<UseCase>();

    for (UseCase useCase : usecaseSet) {
      if ((useCase.isFocusMatrixRelevant(true) || !useCase.getChildFocusMatrixItems().isEmpty()) &&
          !useCase.isDeleted() && !useCase.getMappableItems().isEmpty()) {
        ucSet.add(useCase);
      }
    }
    return ucSet;
  }

  /**
   * Checking the availablity of focus matrix while un mapping the data
   */
  @Override
  public boolean isFocusMatrixAvailableWhileUnMapping(final Attribute attribute) {
    /**
     * Not applicable
     */
    return false;
  }

  /**
   * Checking the availablity of focus matrix while mapping the data
   */
  @Override
  public boolean isFocusMatrixAvailableWhileMapping(final Attribute attribute) {
    /**
     * Not applicable
     */
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getLastConfirmationDate() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isUpToDate() {
    // TODO Auto-generated method stub
    return false;
  }
}
