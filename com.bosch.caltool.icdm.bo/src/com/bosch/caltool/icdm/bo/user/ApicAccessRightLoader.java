/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.user;


import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicAccessRight;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicUser;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.user.ApicAccessRight;


/**
 * @author bne4cob
 */
public class ApicAccessRightLoader extends AbstractBusinessObject<ApicAccessRight, TabvApicAccessRight> {

  /**
   *
   */
  private static final String SESSKEY_CUR_USER_APIC_ACCESS = "CUR_USER_APIC_ACCESS";

  /**
   * @param serviceData Service Data
   */
  public ApicAccessRightLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.APIC_ACCESS_RIGHT, TabvApicAccessRight.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ApicAccessRight createDataObject(final TabvApicAccessRight entity) throws DataException {
    ApicAccessRight data = new ApicAccessRight();

    data.setId(entity.getAccessrightId());
    data.setVersion(entity.getVersion());

    String accessRight = entity.getAccessRight();
    data.setAccessRight(accessRight);
    data.setModuleName(entity.getModuleName());
    data.setUserId(entity.getTabvApicUser().getUserId());

    data.setApicRead(ApicConstants.APIC_READ_ACCESS.equals(accessRight));
    data.setApicWrite(ApicConstants.APIC_WRITE_ACCESS.equals(accessRight));
    data.setPidcWrite(ApicConstants.PIDC_WRITE_ACCESS.equals(accessRight));
    data.setApicReadAll(ApicConstants.APIC_READ_ALL_ACCESS.equals(accessRight));
    return data;
  }

  /**
   * @return apic acess right for current user, as available in service data
   * @throws DataException error while creating data object
   */
  public ApicAccessRight getAccessRightsCurrentUser() throws DataException {
    if (!getServiceData().isAuthenticatedUser()) {
      return null;
    }
    ApicAccessRight data = null;

    TabvApicUser dbUser = (new UserLoader(getServiceData())).getEntityObject(getServiceData().getUserId());
    Set<TabvApicAccessRight> dbRightsSet = dbUser.getTabvApicAccessRights();

    if ((dbRightsSet != null) && !dbRightsSet.isEmpty()) {
      TabvApicAccessRight dbRight = dbRightsSet.iterator().next();
      data = createDataObject(dbRight);
    }

    return data;
  }

  /**
   * @return true if current user has APIC_WRITE access
   * @throws DataException error while creating data object
   */
  public boolean isCurrentUserApicWrite() throws DataException {
    return ApicConstants.APIC_WRITE_ACCESS.equals(getAccessRightsCurrentUserStr());
  }


  /**
   * @return true if current user has APIC_READ_ALL access
   * @throws DataException error while creating data object
   */
  public boolean isCurrentUserApicReadAll() throws DataException {
    return ApicConstants.APIC_READ_ALL_ACCESS.equals(getAccessRightsCurrentUserStr());
  }

  private String getAccessRightsCurrentUserStr() throws DataException {
    String sysAccess = (String) getServiceData().retrieveData(getClass(), SESSKEY_CUR_USER_APIC_ACCESS);
    if (sysAccess == null) {
      ApicAccessRight acessObj = getAccessRightsCurrentUser();
      // Set NO_ACCESS to prevent coming here multiple times, if user is not defined yet
      sysAccess = CommonUtils.isNull(acessObj) ? "NO_ACCESS" : acessObj.getAccessRight();
      getServiceData().storeData(getClass(), SESSKEY_CUR_USER_APIC_ACCESS, sysAccess);
    }
    return sysAccess;
  }

  /**
   * Get a given user's access rights.
   *
   * @param userName user NT ID
   * @return apic acess right for given user, as available in service data
   * @throws DataException error while creating data object
   */
  public ApicAccessRight getAccessRightsByUserName(final String userName) throws DataException {
    ApicAccessRight data = null;

    TabvApicUser dbUser = (new UserLoader(getServiceData())).getEntityObjectByUserName(userName);
    Set<TabvApicAccessRight> dbRightsSet = dbUser.getTabvApicAccessRights();

    if ((dbRightsSet != null) && !dbRightsSet.isEmpty()) {
      TabvApicAccessRight dbRight = dbRightsSet.iterator().next();
      data = createDataObject(dbRight);
    }
    return data;
  }

  /**
   * @return true if current user has PIDC_WRITE access
   * @throws DataException error while creating data object
   */
  public boolean isCurrentUserPidWrite() throws DataException {
    return ApicConstants.PIDC_WRITE_ACCESS.equals(getAccessRightsCurrentUserStr());
  }
}
