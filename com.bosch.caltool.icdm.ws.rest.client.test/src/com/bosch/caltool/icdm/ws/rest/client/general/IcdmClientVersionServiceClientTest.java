/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.general;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author nip4cob
 * @deprecated to be removed after 2021.5.0 release
 */
@Deprecated
public class IcdmClientVersionServiceClientTest extends AbstractRestClientTest {

  /**
   * @throws ApicWebServiceException - error during webservice call
   */
  @Test
  public void testGetIcdmVersion() throws ApicWebServiceException {
    // Get the iCDM version from database
    String icdmVersion = new IcdmClientVersionServiceClient().getIcdmVersion();
    assertNotNull("iCDM Version cannot be null", icdmVersion);
  }
}
