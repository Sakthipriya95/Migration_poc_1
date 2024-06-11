/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import com.bosch.caltool.dmframework.bo.AbstractDataObject;
import com.bosch.caltool.dmframework.bo.AbstractDataProvider;
import com.bosch.caltool.icdm.common.util.CommonUtils;


/**
 * Abstract class for all APIC module specific data objects
 *
 * @author bne4cob
 */
@Deprecated
public abstract class ApicObject extends AbstractDataObject {

  /**
   * Constructor
   *
   * @param apicDataProvider Data Provider
   * @param objID Object ID
   */
  protected ApicObject(final AbstractDataProvider apicDataProvider, final Long objID) {
    super(apicDataProvider, objID);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected DataLoader getDataLoader() {
    return (DataLoader) super.getDataLoader();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected DataCache getDataCache() {
    return (DataCache) super.getDataCache();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected EntityProvider getEntityProvider() {
    return (EntityProvider) super.getEntityProvider();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUserDisplayName() {
    String userName = getCreatedUser();
    if (CommonUtils.isEmptyString(userName)) {
      // Empty string should not be passed to getApicUser method
      return "";
    }
    // Using the user NT ID, find the apic user object
    // Return the display name of the user
    ApicUser user = getDataCache().getApicUser(userName);
    return user == null ? userName : user.getDisplayName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUserDisplayName() {
    String userName = getModifiedUser();
    if (CommonUtils.isEmptyString(userName)) {
      // Empty string should not be passed to getApicUser method
      return "";
    }
    // Using the user NT ID, find the apic user object
    // Return the display name of the user
    ApicUser user = getDataCache().getApicUser(userName);
    return user == null ? userName : user.getDisplayName();
  }

}
