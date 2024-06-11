/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;


/**
 * @author ELM1COB
 */
public class TestLogin extends AbstractSoapClientTest {

  private final APICWebServiceClient stub = new APICWebServiceClient();

  /**
   * login test
   *
   * @throws Exception service error
   */
  @Test
  public void testLogin() throws Exception {
    String sessId = this.stub.login();
    assertNotNull("Login web service  session id", sessId);
    LOG.info("Login session ID " + sessId);
  }

  /**
   * logout test
   *
   * @throws Exception service error
   */
  @Test
  public void testLogout() throws Exception {
    String login = this.stub.login();
    LOG.info("session ID = {}", login);
    boolean cancelSession = this.stub.cancelSession(login);

    assertTrue("logout web service session", cancelSession);
  }

  /**
   * Test GetWebServiceVersion service
   *
   * @throws Exception service error
   */
  @Test
  public void testGetWebServiceVersion() throws Exception {
    // Service is obsolete
    this.thrown.expectMessage("service is no longer available");
    String webServiceVersion = this.stub.getWebServiceVersion();
    fail("exception was not thrown. Service retrurned instead " + webServiceVersion);
  }


}
