/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.general;

import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.UserPreference;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for tUserPreference
 *
 * @author EKIR1KOR
 */
public class UserPreferenceServiceClient extends AbstractRestServiceClient {

  /**
   * Constructor
   */
  public UserPreferenceServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_USERPREFERENCE);
  }

  /**
   * Get all tUserPreference records in system
   *
   * @param userId the user's Id
   * @return Map. Key - id, Value - UserPreference object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Map<Long, UserPreference> getByUserId(final long userId) throws ApicWebServiceException {
    LOGGER.debug("Get tUserPreference records for user id");
    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_USER_PREF_BY_USER_ID).queryParam(WsCommonConstants.RWS_USER_ID, userId);
    GenericType<Map<Long, UserPreference>> type = new GenericType<Map<Long, UserPreference>>() {};
    Map<Long, UserPreference> userPreferenceMap = get(wsTarget, type);
    LOGGER.debug("tUserPreference records loaded count = {}", userPreferenceMap.size());
    return userPreferenceMap;
  }

  /**
   * Get tUserPreference using its id
   *
   * @param prefId preference's id
   * @return UserPreference object
   * @throws ApicWebServiceException exception while invoking service
   */
  public UserPreference get(final Long prefId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, prefId);
    return get(wsTarget, UserPreference.class);
  }

  /**
   * Create a tUserPreference record
   *
   * @param userPreference object to create
   * @return created UserPreference object
   * @throws ApicWebServiceException exception while invoking service
   */
  public UserPreference create(final UserPreference userPreference) throws ApicWebServiceException {
    // check if user preference exists already for user
    // if exists then call for update
    Map<Long, UserPreference> userPrefMap = getByUserId(userPreference.getUserId());
    for (Entry<Long, UserPreference> set : userPrefMap.entrySet()) {
      UserPreference tempUserPref = set.getValue();
      if (tempUserPref.getUserPrefKey().equals(userPreference.getUserPrefKey())) {
        tempUserPref.setUserPrefVal(userPreference.getUserPrefVal());
        return update(getWsBase(), tempUserPref);
      }
    }
    return create(getWsBase(), userPreference);
  }

  /**
   * Update a tUserPreference record
   *
   * @param userPreference object to update
   * @return updated UserPreference object
   * @throws ApicWebServiceException exception while invoking service
   */
  public UserPreference update(final UserPreference userPreference) throws ApicWebServiceException {
    if (userPreference.getId() == null) {
      LOGGER.debug("Unable to update User Preference as the id is invalid");
      return null;
    }
    return update(getWsBase(), userPreference);
  }

}
