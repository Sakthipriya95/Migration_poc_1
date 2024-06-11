/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TUserPreference;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.UserPreference;


/**
 * Loader class for tUserPreference
 *
 * @author EKIR1KOR
 */
public class UserPreferenceLoader extends AbstractBusinessObject<UserPreference, TUserPreference> {

  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public UserPreferenceLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.USER_PREFERENCE, TUserPreference.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected UserPreference createDataObject(final TUserPreference entity) throws DataException {
    UserPreference userPreference = new UserPreference();

    setCommonFields(userPreference, entity);

    userPreference.setUserId(entity.getTabvApicUser().getUserId());
    userPreference.setUserPrefKey(entity.getUserPrefKey());
    userPreference.setUserPrefVal(entity.getUserPrefVal());

    return userPreference;
  }

  /**
   * Get tUserPreference records in system for user id
   *
   * @param userId the user's Id
   * @return Map. Key - id, Value - UserPreference object
   * @throws DataException error while retrieving data
   */
  public Map<Long, UserPreference> getByUserId(final long userId) throws DataException {
    Map<Long, UserPreference> userPreferenceMap = new ConcurrentHashMap<>();
    TypedQuery<TUserPreference> tQuery =
        getEntMgr().createNamedQuery(TUserPreference.NQ_FIND_BY_USER_ID, TUserPreference.class);
    tQuery.setParameter("userId", userId);
    List<TUserPreference> userPreferenceList = tQuery.getResultList();
    for (TUserPreference entity : userPreferenceList) {
      userPreferenceMap.put(entity.getUserPrefId(), createDataObject(entity));
    }
    return userPreferenceMap;
  }

}
