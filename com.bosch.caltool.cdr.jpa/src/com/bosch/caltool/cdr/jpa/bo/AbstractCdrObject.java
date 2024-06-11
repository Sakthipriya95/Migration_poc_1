/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import com.bosch.caltool.apic.jpa.bo.ApicDataProvider;
import com.bosch.caltool.apic.jpa.bo.ApicUser;
import com.bosch.caltool.dmframework.bo.AbstractDataObject;
import com.bosch.caltool.dmframework.bo.AbstractDataProvider;
import com.bosch.caltool.icdm.common.util.CommonUtils;


/**
 * Abstract class for all CDR objects
 *
 * @author bne4cob
 */
@Deprecated
public abstract class AbstractCdrObject extends AbstractDataObject {

  /**
   * Constructor
   *
   * @param cdrDataProvider CDR data provider
   * @param objID id
   */
  protected AbstractCdrObject(final AbstractDataProvider cdrDataProvider, final Long objID) {
    super(cdrDataProvider, objID);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected CDRDataLoader getDataLoader() {
    return (CDRDataLoader) super.getDataLoader();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected CDRDataCache getDataCache() {
    return (CDRDataCache) super.getDataCache();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected CDREntityProvider getEntityProvider() {
    return (CDREntityProvider) super.getEntityProvider();
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
   * Get data provider
   *
   * @return Apic data provider
   */
  @Override
  protected CDRDataProvider getDataProvider() {
    return (CDRDataProvider) super.getDataProvider();
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
