package com.bosch.caltool.icdm.ws.rest.client.bc;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.bosch.caltool.icdm.model.bc.SdomFc2bc;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for SdomFc2bc
 *
 * @author say8cob
 */
public class SdomFc2bcServiceClientTest extends AbstractRestClientTest {

  private final static Long SDOMFC2BC_ID = 4126270000L;
  /**
   * Expected exception
   */
  public final ExpectedException thrown = ExpectedException.none();


  /**
   * Test method for {@link SdomFc2bcServiceClient#getById(Long)}
   *
   * @throws ApicWebServiceException error in WS call
   */
  @Test
  public void testGetById() throws ApicWebServiceException {
    SdomFc2bcServiceClient servClient = new SdomFc2bcServiceClient();
    SdomFc2bc ret = servClient.getById(SDOMFC2BC_ID);
    assertNotNull("response not null", ret);
    LOG.info("FC ID returned = {}", ret.getFcId());
  }


}
