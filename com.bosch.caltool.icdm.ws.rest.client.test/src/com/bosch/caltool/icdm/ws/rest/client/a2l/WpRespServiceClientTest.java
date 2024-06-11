package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.bosch.caltool.icdm.model.a2l.WpResp;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;


/**
 * Service Client Test for WP Responsibility
 *
 * @author apj4cob
 */
public class WpRespServiceClientTest extends AbstractRestClientTest {

  private final static Long WPRESP_ID = 795891515l;
  /**
   * Expected exception
   */
  public final ExpectedException thrown = ExpectedException.none();

  /**
   * Test method for {@link WpRespServiceClient#getAll()}
   */
  @Test
  public void testGetAll() {
    LOG.info("=======================================================================================================");
    LOG.info(
        " TestGetAll==============================================================================================");
    LOG.info("=======================================================================================================");
    WpRespServiceClient servClient = new WpRespServiceClient();
    try {
      Map<Long, WpResp> retMap = servClient.getAll();
      assertFalse("Response should not be null or empty", ((retMap == null) || retMap.isEmpty()));
      testOutput((retMap.values().iterator().next()));
    }
    catch (Exception excep) {
      LOG.error("Error in WS call", excep);
      assertNull("Error in WS call", excep);
    }
  }

  /**
   * Test method for {@link WpRespServiceClient#getWpResponsibility(Long)}
   */
  @Test
  public void testGetWpResponsibility() {
    LOG.info("=======================================================================================================");
    LOG.info(
        " TestGetAll==============================================================================================");
    LOG.info("=======================================================================================================");
    WpRespServiceClient servClient = new WpRespServiceClient();
    try {
      WpResp ret = servClient.getWpResponsibility(WPRESP_ID);
      assertFalse("Response should not be null or empty", (ret == null));
      testOutput(ret);
    }
    catch (Exception excep) {
      LOG.error("Error in WS call", excep);
      assertNull("Error in WS call", excep);
    }
  }

  /**
   * test output data
   */
  private void testOutput(final WpResp obj) {
    assertEquals("RespName is equal", obj.getRespName(), "R");
  }

}
