/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.bosch.caltool.apic.ws.client.APICStub.PidcAccessRightResponseType;
import com.bosch.caltool.apic.ws.client.APICStub.PidcAccessRights;
import com.bosch.caltool.apic.ws.client.serviceclient.APICWebServiceClient.PidcAccessRightsTyp;

/**
 * @author rgo7cob
 */
public class TestPidcAccessRights extends AbstractSoapClientTest {

  private final APICWebServiceClient stub = new APICWebServiceClient();

  /**
   * fetch access with owner, grant, read = true
   *
   * @throws Exception response error
   */
  @Test
  public void testGetPidcAccessRightsAllRightsYES() throws Exception {

    PidcAccessRights accessRights = this.stub.getPidcAccessRights(PidcAccessRightsTyp.ACCESS_TRUE,
        PidcAccessRightsTyp.ACCESS_TRUE, PidcAccessRightsTyp.ACCESS_FALSE, null);

    assertNotNull(accessRights);

    PidcAccessRightResponseType[] accessRights2 = accessRights.getAccessRights();
    assertNotNull(accessRights2);
    assertTrue("Access rights retrieved", (accessRights2.length > 0));

    LOG.info("Number of records: {}", accessRights.getAccessRights().length);

    PidcAccessRightResponseType firstItem = accessRights2[0];
    assertTrue("Owner YES", firstItem.getOwner());
    assertTrue("Write YES", firstItem.getWrite());
    assertTrue("Grant YES", firstItem.getGrant());

    logAccessRightItem(firstItem);

  }

  /**
   * This test case shows how to get the data to have an object for further usage. Passed: - Excepted: All data should
   * be shown as Logger result.
   *
   * @throws Exception service error
   */
  @Test
  public void testGetPidcAccessRightsSinglePidc() throws Exception {
    PidcAccessRights response = this.stub.getPidcAccessRights(PidcAccessRightsTyp.ALL, PidcAccessRightsTyp.ALL,
        PidcAccessRightsTyp.ALL, null, 758553267L);
    assertNotNull("Result returned", response);

    PidcAccessRightResponseType[] accessRightsArr = response.getAccessRights();
    assertNotNull("Result records returned", accessRightsArr);

    LOG.info("result count : {}", accessRightsArr.length);
    assertTrue("Access rights retrieved", (accessRightsArr.length > 0));

    int count = 0;
    for (PidcAccessRightResponseType pidcAccessRightResponseType : accessRightsArr) {
      if (count >= 5) {
        // Print first 5 items
        break;
      }
      logAccessRightItem(pidcAccessRightResponseType);
      count++;
    }

    assertResult(accessRightsArr, "CHO5FE", false, false, false);

  }


  /**
   * Test all access rights
   *
   * @throws Exception response error
   */
  @Test
  public void testGetAllPidcAccess() throws Exception {

    PidcAccessRights accessRights = this.stub.getAllPidcAccessRights();
    PidcAccessRightResponseType[] accessRights2 = accessRights.getAccessRights();
    assertNotNull(accessRights2);
    int count = 0;
    for (PidcAccessRightResponseType pidcAccessRightResponseType : accessRights2) {
      if (count >= 5) {
        // Print first 5 items
        break;
      }
      logAccessRightItem(pidcAccessRightResponseType);
      count++;
    }

  }

  private void logAccessRightItem(final PidcAccessRightResponseType accessRight) {
    LOG.info("User : {}, PIDC-ID = {}, Department = {}, Owner = {}, Write = {}, Grant = {}", accessRight.getUserName(),
        accessRight.getPidcId(), accessRight.getDepartment(), accessRight.getOwner(), accessRight.getWrite(),
        accessRight.getGrant());
  }


  /**
   * Checks a Junit test for correctness. The Conditions are passed to the webservice. The result is validated against
   * the passed excepted parameters. All excepted access rights must match to pass a test.
   *
   * @param wsResponse the web service response object
   * @param username the username which should be tested
   * @param exceptWrite the excepted value for 'write allowed'
   * @param exceptGrant the excepted value for 'grant allowed'
   * @param exceptOwner the excepted value for 'is owner'
   */
  private void assertResult(final PidcAccessRightResponseType[] accessRightsArr, final String username,
      final boolean exceptWrite, final boolean exceptGrant, final boolean exceptOwner) {

    LOG.info("Validating access rights of user {}", username);

    for (PidcAccessRightResponseType access : accessRightsArr) {

      if (access.getUserName().equalsIgnoreCase(username)) {
        assertEquals("Validate owner rights", exceptOwner, access.getOwner());
        assertEquals("Validate Write rights", exceptWrite, access.getWrite());
        assertEquals("Validate Grant rights", exceptGrant, access.getGrant());
        return;
      }
    }

    fail("Access Right not found for user: " + username);

  }

}
