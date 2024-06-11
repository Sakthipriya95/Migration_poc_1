/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.jpa.bo;

import com.bosch.caltool.apic.jpa.bo.ApicDataProvider;
import com.bosch.caltool.apic.jpa.bo.ApicUser;
import com.bosch.caltool.dmframework.bo.AbstractDataObject;
import com.bosch.caltool.dmframework.bo.AbstractDataProvider;
import com.bosch.caltool.icdm.common.util.CommonUtils;


/**
 * Component package object Icdm-949 isIdValid method removed
 */
@Deprecated
public abstract class AbstractCPObject extends AbstractDataObject {

  /**
   * Constrcutor
   *
   * @param cpDataProvider CP data provider
   * @param objID id
   */
  protected AbstractCPObject(final AbstractDataProvider cpDataProvider, final Long objID) {
    super(cpDataProvider, objID);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected CPDataLoader getDataLoader() {
    return (CPDataLoader) super.getDataLoader();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected CPDataCache getDataCache() {
    return (CPDataCache) super.getDataCache();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected CPEntityProvider getEntityProvider() {
    return (CPEntityProvider) super.getEntityProvider();
  }

  /**
   * @return the dataProvider
   */
  @Override
  protected CPDataProvider getDataProvider() {
    return (CPDataProvider) super.getDataProvider();
  }

  /**
   * Returns APIC data provider
   *
   * @return ApicDataProvider
   */
  protected ApicDataProvider getApicDataProvider() {
    return getDataCache().getDataProvider().getApicDataProvider();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUserDisplayName() {
    String userName = getCreatedUser();
    // Empty string should not be passed to getApicUser method
    if (CommonUtils.isEmptyString(userName)) {
      return "";
    }
    // Using the user NT ID, find the apic user object
    // Return the display name of the user
    ApicUser user = getApicDataProvider().getApicUser(userName);
    return user == null ? userName : user.getDisplayName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUserDisplayName() {
    String userName = getModifiedUser();
    // Empty string should not be passed to getApicUser method
    if (CommonUtils.isEmptyString(userName)) {
      return "";
    }
    // Using the user NT ID, find the apic user object
    // Return the display name of the user
    ApicUser user = getApicDataProvider().getApicUser(userName);
    return user == null ? userName : user.getDisplayName();
  }
}
