/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.general;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author IKI1COB
 */
public class IcdmClientStartupServiceClientTest extends AbstractRestClientTest {

  private final static String INVALID_ID = "####";

  /**
   * @throws ApicWebServiceException service error
   */
  @Test
  public void getWelcomePageFiles() throws ApicWebServiceException {

    IcdmClientStartupServiceClient serviceClient = new IcdmClientStartupServiceClient();
    byte[] b = serviceClient.getWelcomePageFiles(CommonUtils.getSystemUserTempDirPath());
    assertFalse("Response should not be null or empty", (b == null) || (b.length == 0));
  }

  /**
   * @throws ApicWebServiceException service error
   */
  @Test
  public void getDisclaimerFile() throws ApicWebServiceException {

    IcdmClientStartupServiceClient serviceClient = new IcdmClientStartupServiceClient();
    byte[] b = serviceClient.getDisclaimerFile(CommonUtils.getSystemUserTempDirPath());
    assertFalse("Response should not be null or empty", (b == null) || (b.length == 0));
  }

  /**
   * @throws ApicWebServiceException service error
   */
  @Test
  public void testgetWelcomePageFilesInvalid() throws ApicWebServiceException {
    IcdmClientStartupServiceClient servClient = new IcdmClientStartupServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Error while downloading file welcomepage.zip : " + INVALID_ID +
        "\\welcomepage.zip (The system cannot find the path specified)");
    servClient.getWelcomePageFiles(INVALID_ID);
    fail("Expected exception not thrown");
  }

  /**
   * @throws ApicWebServiceException service error
   */
  @Test
  public void testgetDisclaimerFileInvalid() throws ApicWebServiceException {
    IcdmClientStartupServiceClient servClient = new IcdmClientStartupServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Error while downloading file disclaimer.zip : " + INVALID_ID +
        "\\disclaimer.zip (The system cannot find the path specified)");
    servClient.getDisclaimerFile(INVALID_ID);
    fail("Expected exception not thrown");
  }

  /**
   * Test method for {@link IcdmClientStartupServiceClient#getMailtoHotLineFile(String)}
   *
   * @throws ApicWebServiceException
   */
  @Test
  public void testgetMailtoHotLineFile() throws ApicWebServiceException {
    IcdmClientStartupServiceClient servClient = new IcdmClientStartupServiceClient();
    byte[] hotlineFile = servClient.getMailtoHotLineFile("testdata//general");
    assertNotNull(hotlineFile);
  }

  /**
   * Test method for {@link IcdmClientStartupServiceClient#getMailtoHotLineFile(String)} with empty path
   *
   * @throws ApicWebServiceException
   */
  @Test
  public void testgetMailtoHotLineFileInvalid() throws ApicWebServiceException {
    IcdmClientStartupServiceClient servClient = new IcdmClientStartupServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    servClient.getMailtoHotLineFile("");
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }
}