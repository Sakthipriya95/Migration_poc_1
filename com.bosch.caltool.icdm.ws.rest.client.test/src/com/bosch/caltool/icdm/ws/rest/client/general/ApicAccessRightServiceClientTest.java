/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.general;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.bosch.caltool.icdm.model.user.ApicAccessRight;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author EMS4KOR
 */
public class ApicAccessRightServiceClientTest extends AbstractRestClientTest {

  private final static String USER_NAME1 = "bne4cob";
  private final static String USER_NAME2 = "BNE4COB";
  private final static String INVALID_USER_NAME = "sdwsd";

  /**
   * Test method for {@link ApicAccessRightServiceClient#getCurrentUserApicAccessRight()}
   *
   * @throws ApicWebServiceException Webservice Error
   */
  @Test
  public void testGetCurrentUserApicAccessRight() throws ApicWebServiceException {
    ApicAccessRightServiceClient servClient = new ApicAccessRightServiceClient();
    ApicAccessRight ret = servClient.getCurrentUserApicAccessRight();
    assertFalse("Response should not be null or empty", ((ret == null) || ret.getAccessRight().isEmpty()));
    testOutput(ret);
  }

  /**
   * Test method for {@link ApicAccessRightServiceClient#getUserApicAccessRight(String)}
   *
   * @throws ApicWebServiceException Webservice Error
   */
  @Test
  public void testGetUserApicAccessRight() throws ApicWebServiceException {
    ApicAccessRightServiceClient servClient = new ApicAccessRightServiceClient();
    ApicAccessRight ret = servClient.getUserApicAccessRight(USER_NAME1);
    assertNotNull("Response should not be null", ret);
    testOutput(ret);
  }

  /**
   * Test method for {@link ApicAccessRightServiceClient#getUserApicAccessRight(String)} UpperCase Testcase
   *
   * @throws ApicWebServiceException Webservice Error
   */
  @Test
  public void testGetUserApicAccessRightUpperCase() throws ApicWebServiceException {
    ApicAccessRightServiceClient servClient = new ApicAccessRightServiceClient();
    ApicAccessRight ret = servClient.getUserApicAccessRight(USER_NAME2);
    assertNotNull("Response should not be null", ret);
    testOutput(ret);
  }

  /**
   * { @linkApicAccessRightServiceClient#getUserApicAccessRight(String)} Negative testcase
   *
   * @throws ApicWebServiceException Webservice Error
   */
  @Test
  public void testGetUserApicAccessRightNegative() throws ApicWebServiceException {
    ApicAccessRightServiceClient servClient = new ApicAccessRightServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Internal server error occured. Please contact iCDM Hotline.");
    ApicAccessRight ret = servClient.getUserApicAccessRight(INVALID_USER_NAME);
    fail("Expected Exception not thrown");
    testOutput(ret);
  }

  /**
   * { @linkApicAccessRightServiceClient#getUserApicAccessRight(String)} Null testcase
   *
   * @throws ApicWebServiceException Webservice Error
   */

  @Test
  public void testGetUserApicAccessRightNull() throws ApicWebServiceException {
    ApicAccessRightServiceClient servClient = new ApicAccessRightServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Internal server error occured. Please contact iCDM Hotline.");
    ApicAccessRight ret = servClient.getUserApicAccessRight("");
    fail("Expected Exception not thrown");
    testOutput(ret);
  }

  /**
   * test output data
   */
  private void testOutput(final ApicAccessRight ret) {
    assertNotNull("AccessRight is not null", ret.getAccessRight());
    assertEquals("id is equal", "230017", ret.getId().toString());
    assertEquals("User id is equal", "230016", ret.getUserId().toString());
    assertEquals("ModuleName is equal", "APIC", ret.getModuleName().toString());
    assertNotNull("Apic Write is not null", ret.isApicWrite());

  }
}
