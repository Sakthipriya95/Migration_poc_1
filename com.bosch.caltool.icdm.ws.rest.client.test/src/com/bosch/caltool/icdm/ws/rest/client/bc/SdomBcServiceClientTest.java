package com.bosch.caltool.icdm.ws.rest.client.bc;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.bosch.caltool.icdm.model.bc.SdomBc;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;


/**
 * Service Client Test for SdomBc
 *
 * @author say8cob
 */
public class SdomBcServiceClientTest extends AbstractRestClientTest {

  private final static Long SDOMBC_ID = (long) 2303609;
  /**
   * Expected exception
   */
  public final ExpectedException thrown = ExpectedException.none();

  /**
   * Test method for {@link com.bosch.caltool.icdm.rest.client.comppkg.SdomBcServiceClientTest#getAllDistinctBcName()}
   */
  @Test
  public void testGetAllDistinctBcName() {
    LOG.info("=======================================================================================================");
    LOG.info(
        " TestGetAllDistinctBCNames==============================================================================================");
    LOG.info("=======================================================================================================");
    SdomBcServiceClient servClient = new SdomBcServiceClient();
    try {
      Set<SdomBc> retMap = servClient.getAllDistinctBcName();
      assertTrue(!retMap.isEmpty());
      LOG.info("Total count of SDOMBC" + retMap.size());
      LOG.info("Printing First 5 Records");
      int count = 0;
      for (SdomBc sdomBc : retMap) {
        if (count < 5) {
          LOG.info(sdomBc.getName());
          count++;
        }
        break;
      }
    }
    catch (Exception excep) {
      LOG.error("Error in WS call", excep);
    }
  }


}
