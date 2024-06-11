package com.bosch.caltool.icdm.ws.rest.client.general;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.bosch.caltool.icdm.model.general.AzureUserModel;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;


/**
 * Service Client Test for AzureAuthService
 *
 * @author MSP5COB
 */
public class AzureAuthServiceClientTest extends AbstractRestClientTest {

  /**
   * Test method for {@link AzureAuthServiceClient}
   */
  @Test
  public void testGet() {
    AzureAuthServiceClient servClient = new AzureAuthServiceClient();
    try {
      AzureUserModel ret = servClient.get("-1179555672");
      assertFalse("Response should not be null", (ret == null));
    }
    catch (Exception excep) {
      LOG.error("Error in WS call", excep);
    }
  }
}
