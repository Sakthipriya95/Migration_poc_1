package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.bosch.caltool.icdm.model.a2l.A2lResp;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;


/**
 * Service Client Test for A2L Responsibility
 *
 * @author apj4cob
 */
public class A2lRespServiceClientTest extends AbstractRestClientTest {

  /**
   * Expected exception
   */
  public final ExpectedException thrown = ExpectedException.none();


  /**
   * Test method for {@link A2lRespServiceClient#getA2lResponsibility(Long, Long, Long)}
   */
  @Test
  public void testGetA2lResponsibility() {
    LOG.info("=======================================================================================================");
    LOG.info(
        " testGetA2lResponsibility==============================================================================================");
    LOG.info("=======================================================================================================");
    A2lRespServiceClient servClient = new A2lRespServiceClient();
    try {
      Long pidcA2lId = 794722716l;
      Long wpTypeId = 230672l;
      A2lResp a2lresp = servClient.getA2lResponsibility(pidcA2lId, wpTypeId, 0l);
      assertFalse("Response should not be null or empty", (a2lresp == null));
    }
    catch (Exception excep) {
      LOG.error("Error in WS call", excep);
      assertNull("Error in WS call", excep);
    }
  }

  /**
   * Test method for {@link A2lRespServiceClient#getA2lResponsibility(Long, Long, Long)}
   */
  @Test
  public void testGetA2lResponsibility2() {
    LOG.info("=======================================================================================================");
    LOG.info(
        " testGetA2lResponsibility==============================================================================================");
    LOG.info("=======================================================================================================");
    A2lRespServiceClient servClient = new A2lRespServiceClient();
    try {
      Long pidcA2lId = 794722716l;
      Long wpTypeId = 230672l;
      Long wpRootId = 259569l;
      A2lResp a2lresp = servClient.getA2lResponsibility(pidcA2lId, wpTypeId, wpRootId);
      assertFalse("Response should not be null or empty", (a2lresp == null));
    }
    catch (Exception excep) {
      LOG.error("Error in WS call", excep);
      assertNull("Error in WS call", excep);
    }
  }

  /**
   * test output data
   */
  private void testOutput(final A2lResp obj) {
    assertNotNull("A2LResp is not null", (obj == null));
  }

}
