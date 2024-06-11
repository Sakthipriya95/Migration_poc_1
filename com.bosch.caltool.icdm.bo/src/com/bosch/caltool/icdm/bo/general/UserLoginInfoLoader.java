/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.general;

import java.util.List;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.general.TUserLoginInfo;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.general.UserLoginInfo;

/**
 * @author msp5cob
 */
public class UserLoginInfoLoader extends AbstractBusinessObject<UserLoginInfo, TUserLoginInfo> {

  /**
   * @param serviceData Service Data
   */
  public UserLoginInfoLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.USER_LOGIN_INFO, TUserLoginInfo.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected UserLoginInfo createDataObject(final TUserLoginInfo entity) throws DataException {
    UserLoginInfo object = new UserLoginInfo();

    setCommonFields(object, entity);

    object.setUserNtId(entity.getUserNtId());
    object.setAzureLoginCount(entity.getAzureLoginCount());
    object.setLdapLoginCount(entity.getLdapLoginCount());

    return object;
  }

  /**
   * @param userNtId userNtId
   * @return UserLoginInfo
   * @throws DataException e
   */
  public UserLoginInfo getByUserNtId(final String userNtId) throws DataException {
    TypedQuery<TUserLoginInfo> tQuery =
        getEntMgr().createNamedQuery(TUserLoginInfo.GET_USER_LOGIN_INFO, TUserLoginInfo.class);
    tQuery.setParameter("userNtId", userNtId);
    List<TUserLoginInfo> resultList = tQuery.getResultList();
    if (resultList.isEmpty()) {
      return null;
    }
    return createDataObject(resultList.get(0));
  }
}
