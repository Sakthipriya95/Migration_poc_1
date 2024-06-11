/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.general;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.UserPreference;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for tUserPreference
 *
 * @author EKIR1KOR
 */
public class UserPreferenceServiceClientTest extends AbstractRestClientTest {

  private static final Long USER_ID = 1286595367L;
  private static final Long USER_ID1 = 0L;
  private static final String JUNIT_TEST_KEY = "JUNIT_TEST_PREF_";
  private static final String JUNIT_TEST_VALUE_CREATE = "2";
  private static final String JUNIT_TEST_VALUE_UPDATE = "4";


  /**
   * Test method for {@link UserPreferenceServiceClient}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testAll() throws ApicWebServiceException {
    // for user preference create
    UserPreferenceServiceClient servClient = new UserPreferenceServiceClient();
    UserPreference userPrefCreate = servClient.create(getUserPreferenceModel());
    assertFalse("Userpreference create - Response should not be null", userPrefCreate == null);

    // create again using the userPrefCreate model
    UserPreference userPrefCreate1 = servClient.create(userPrefCreate);
    assertTrue("Userpreference create when key value exists for user - record should be updated",
        userPrefCreate.getId().equals(userPrefCreate1.getId()));

    // for user preference get by userId
    Map<Long, UserPreference> userPrefMap = servClient.getByUserId(USER_ID);
    assertFalse("Userpreference getByUserId - Response should not be null or empty",
        ((userPrefMap == null) || userPrefMap.isEmpty()));
    LOG.info("Size : {}", null != userPrefMap ? userPrefMap.size() : "0");

    Map<Long, UserPreference> userPrefMap1 = servClient.getByUserId(USER_ID1);
    assertTrue("Userpreference getByUserId - Response should be null or empty",
        ((userPrefMap1 == null) || userPrefMap1.isEmpty()));
    LOG.info("Size : {}", null != userPrefMap1 ? userPrefMap1.size() : "0");


    // for user preference get by prefId
    if ((userPrefMap != null) && CommonUtils.isNotEmpty(userPrefMap)) {
      long userPrefId = userPrefMap.entrySet().iterator().next().getKey();
      LOG.info("userPrefId: {}", userPrefId);
      UserPreference retUserPref = servClient.get(userPrefId);
      assertFalse("Response should not be null", retUserPref == null);
    }

    // for user preference update
    userPrefCreate.setUserPrefVal(JUNIT_TEST_VALUE_UPDATE);
    UserPreference userPrefUpdate = servClient.update(userPrefCreate);
    assertFalse("Response should not be null", userPrefUpdate == null);

    UserPreference userPrefUpdate1 = servClient.update(getUserPreferenceModel());
    assertTrue("Response should be null", userPrefUpdate1 == null);

  }

  /**
   * @return userpreference
   */
  public UserPreference getUserPreferenceModel() {
    UserPreference userPref = new UserPreference();
    userPref.setUserId(USER_ID);
    userPref.setUserPrefKey(JUNIT_TEST_KEY + System.currentTimeMillis());
    userPref.setUserPrefVal(JUNIT_TEST_VALUE_CREATE);
    return userPref;
  }

}

