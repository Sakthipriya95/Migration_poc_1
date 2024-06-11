/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.vcdm;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dja7cob
 */
public class VcdmAvailabilityCheckServiceClientTest extends AbstractRestClientTest {

  /**
   * Test method for {@link VcdmAvailabilityCheckServiceClient#isVcdmAvailable()}
   *
   * @throws ApicWebServiceException Exception in vcdm availability check
   */
  @Test
  public void testVcdmAvailability() throws ApicWebServiceException {
    boolean vcdmAvailability = new VcdmAvailabilityCheckServiceClient().isVcdmAvailable();
    assertTrue("Check vCDM Services availability", vcdmAvailability);
  }
}
