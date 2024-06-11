/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author AND4COB
 */
public class PidcRequestorServiceClientTest extends AbstractRestClientTest {

  private final static String INVALID_DIR_PATH = "###\\###";

  /**
   * Test method for {@link PidcRequestorServiceClient#getPidcRequestorFile(String)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetPidcRequestorFile() throws ApicWebServiceException {
    PidcRequestorServiceClient servClient = new PidcRequestorServiceClient();
    byte[] b = servClient.getPidcRequestorFile(CommonUtils.getSystemUserTempDirPath());
    assertFalse("Response should not be null or empty", (b == null) || (b.length == 0));
    // System.out.println(b);
  }

  /**
   * Testing with some invalid directory path (Negative Test Case)
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetPidcRequestorFileNegative() throws ApicWebServiceException {
    PidcRequestorServiceClient servClient = new PidcRequestorServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Error while downloading file PIDC-Requester_V_4.0_Template.xlsm : " + INVALID_DIR_PATH +
        "\\PIDC-Requester_V_4.0_Template.xlsm (The system cannot find the path specified)");
    servClient.getPidcRequestorFile(INVALID_DIR_PATH);
    fail("Expected exception not thrown");
  }
}
