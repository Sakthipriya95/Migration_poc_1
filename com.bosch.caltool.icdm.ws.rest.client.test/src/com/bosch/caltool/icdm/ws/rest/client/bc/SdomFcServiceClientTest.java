package com.bosch.caltool.icdm.ws.rest.client.bc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.bosch.caltool.icdm.model.bc.SdomFc;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;


/**
 * Service Client Test for SdomFc
 *
 * @author say8cob
 */
public class SdomFcServiceClientTest extends AbstractRestClientTest {

  private final static Long SDOMFC_ID = (long) 60939;
  /**
   * Expected exception
   */
  public final ExpectedException thrown = ExpectedException.none();

  /**
   * Test method for {@link com.bosch.caltool.icdm.rest.client.comppkg.SdomFcServiceClientTest#getAll()}
   */


  /**
   * Test method for {@link SdomFcServiceClient#getById(Long)}
   *
   * @throws Exception error in test
   */
  @Test
  public void testGetById() throws Exception {
    SdomFcServiceClient servClient = new SdomFcServiceClient();
    SdomFc ret = servClient.getById(SDOMFC_ID);
    assertNotNull("SDOM FC not null", ret);
    LOG.info("SDOM FC Name " + ret.getName());
    assertEquals("SDOM FC Name check", "CDrv_Shared", ret.getName());
  }

  /**
   * Test method for {@link SdomFcServiceClient#getSDOMFcByBCName(String)}
   *
   * @throws Exception in test
   */
  @Test
  public void testGetBybcName() throws Exception {
    SdomFcServiceClient servClient = new SdomFcServiceClient();
    List<String> ret = servClient.getSDOMFcByBCName("UEGO");

    assertFalse("SDOM FC list not null or empty", (ret == null) || ret.isEmpty());

    LOG.info("SDOM FC list size = " + ret.size());
    LOG.info("First FC = " + ret.iterator().next());
  }
}
