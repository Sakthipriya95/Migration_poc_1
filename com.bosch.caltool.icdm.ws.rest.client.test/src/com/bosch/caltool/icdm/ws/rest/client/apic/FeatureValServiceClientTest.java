/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

import com.bosch.caltool.icdm.model.ssdfeature.FeatureValue;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;


/**
 * @author bne4cob
 */
public class FeatureValServiceClientTest extends AbstractRestClientTest {


  /**
   * Test retrieval of all attributes
   */
  @Test
  public void test01() {
    LOG.info("=======================================================================================================");
    LOG.info(
        "Test 01 =======================================================================================================");
    LOG.info("=======================================================================================================");

    FeatureValueServiceClient servClient = new FeatureValueServiceClient();
    try {

      List<FeatureValue> retMap = servClient.getFeatureValues(10023l);

      assertFalse("Response should not be null or empty", (retMap == null) || (retMap.isEmpty()));

      testOutput(retMap);

    }
    catch (Exception e) {
      LOG.error("Error in WS call", e);
      assertNull("Error in WS call", e);
    }
  }


  /**
   * Test output
   *
   * @param reportData output object
   */
  private void testOutput(final List<FeatureValue> feature) {

    for (FeatureValue featureValue : feature) {
      LOG.info("Feature Value - id = {}, name = {}", featureValue.getId(), featureValue.getName());
      assertFalse("Feature Value name not empty", (featureValue.getName() == null));
      assertNotNull("Feature Value id not empty", featureValue.getId());
    }

  }


}
