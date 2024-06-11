/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;

import com.bosch.caltool.dmframework.bo.AbstractDataProvider;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;

// ICDM-1027
/**
 * This class provides all the properties of TUsecaseFavorite entity
 * 
 * @author mkl2cob
 */
public class FavUseCaseItem extends ApicObject {

  /**
   * Constructor
   * 
   * @param dataProvider AbstractDataProvider
   * @param objID Long
   */
  protected FavUseCaseItem(final AbstractDataProvider dataProvider, final Long objID) {
    super(dataProvider, objID);
  }

  /**
   * Returns the name of the use case item
   * <p>
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getUseCaseItem().getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return EntityType.T_UCFAV;
  }

  /**
   * Returns the use case item object - Usecase/Usecase group /Usecase Section
   * 
   * @return AbstractUseCaseItem
   */
  public AbstractUseCaseItem getUseCaseItem() {
    AbstractUseCaseItem ucItem = null;
    if (getEntityProvider().getDbFavUcItem(getID()).getTabvUseCaseGroup() != null) {
      // get the usecase group from data cache
      ucItem =
          getDataCache()
              .getUseCaseGroup(getEntityProvider().getDbFavUcItem(getID()).getTabvUseCaseGroup().getGroupId());
    }
    else if (getEntityProvider().getDbFavUcItem(getID()).getTabvUseCas() != null) {
      // get the usecase from data cache
      ucItem = getDataCache().getUseCase(getEntityProvider().getDbFavUcItem(getID()).getTabvUseCas().getUseCaseId());
    }
    else if (getEntityProvider().getDbFavUcItem(getID()).getTabvUseCaseSection() != null) {
      // get the usecase section from data cache
      ucItem =
          getDataCache().getUseCaseSection(
              getEntityProvider().getDbFavUcItem(getID()).getTabvUseCaseSection().getSectionId());
    }
    return ucItem;
  }

  /**
   * @return true if the usecase item is project favourite usecase item
   */
  public boolean isProjectFavUCItem() {
    if (getEntityProvider().getDbFavUcItem(getID()).getTabvProjectidcard() != null) {
      return true;
    }
    return false;
  }

  /**
   * @return Apic User if this is a private Favourite item
   */
  public ApicUser getApicUser() {
    if (getEntityProvider().getDbFavUcItem(getID()).getTabvApicUser() != null) {
      return getDataCache().getApicUser(getEntityProvider().getDbFavUcItem(getID()).getTabvApicUser().getUsername());
    }
    return null;
  }

  /**
   * @return PIDCard if this is a project favourite item
   */
  public PIDCard getPIDC() {
    if (getEntityProvider().getDbFavUcItem(getID()).getTabvProjectidcard() != null) {
      return getDataCache().getPidc(getEntityProvider().getDbFavUcItem(getID()).getTabvProjectidcard().getProjectId());
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return getUseCaseItem().getDescription();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbFavUcItem(getID()).getCreatedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbFavUcItem(getID()).getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbFavUcItem(getID()).getCreatedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbFavUcItem(getID()).getModifiedDate());
  }
}
