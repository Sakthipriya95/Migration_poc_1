/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.general;

import java.util.List;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.user.UserDetails;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.VApicUser;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * @author NIP4COB
 */
public class UserDetailsLoader extends AbstractBusinessObject<UserDetails, VApicUser> {


  /**
   * @param serviceData
   * @param modelType
   * @param entityType
   */
  public UserDetailsLoader(final ServiceData serviceData) {
    super(serviceData, "User Details", VApicUser.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected UserDetails createDataObject(final VApicUser entity) throws DataException {
    UserDetails userDetails = new UserDetails();
    userDetails.setId(entity.getUserId());
    userDetails.setCostCenter(entity.getCostcenter());
    userDetails.setCountryCode(entity.getCountryCode());
    userDetails.setCountryDial(entity.getCountryDial());
    userDetails.setCountryName(entity.getCountryName());
    userDetails.setCreatedDate(timestamp2String(entity.getCreatedDate()));
    userDetails.setCreatedUser(entity.getCreatedUser());
    userDetails.setModifiedDate(timestamp2String(entity.getModifiedDate()));
    userDetails.setModifiedUser(entity.getModifiedUser());
    userDetails.setDepartment(entity.getDepartment());
    userDetails.setDisclaimerAcceptedDate(timestamp2String(entity.getDisclmrAcceptedDate()));
    userDetails.setDomain(entity.getDomain());
    userDetails.setEmail(entity.getEmail());
    userDetails.setFax(entity.getFax());
    userDetails.setFirstName(entity.getFirstname());
    userDetails.setLastName(entity.getLastname());
    userDetails.setIsDeleted(ApicConstants.CODE_YES.equals(entity.getIsDeleted()));
    userDetails.setPhone(entity.getPhone());
    userDetails.setUserName(entity.getUsername());
    userDetails.setVersion(entity.getVersion());
    return userDetails;
  }


  /**
   * @param userEmail userEmail
   * @return user details based on email
   * @throws DataException - Exception during fetching user details
   */
  public UserDetails getByEmail(final String userEmail) throws DataException {
    TypedQuery<VApicUser> query =
        getEntMgr().createNamedQuery(VApicUser.NQ_GET_USER_INFO_BY_USER_EMAIL, VApicUser.class);
    query.setParameter("email", userEmail);
    List<VApicUser> resultList = query.getResultList();
    if (!resultList.isEmpty()) {
      return createDataObject(resultList.get(0));
    }
    return null;
  }
}
