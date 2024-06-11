/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.model.ssdfeature.Feature;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;


/**
 * @author bne4cob
 */
public class FeatureServiceClientTest extends AbstractRestClientTest {


  /**
   * Test retrieval of all attributes
   */
  @Test
  public void test01() {
    LOG.info("=======================================================================================================");
    LOG.info(
        "Test 01 =======================================================================================================");
    LOG.info("=======================================================================================================");

    FeatureServiceClient servClient = new FeatureServiceClient();
    try {

      Map<Long, Feature> retMap = servClient.getAllFeatures();

      assertFalse("Response should not be null or empty", (retMap == null) || (retMap.isEmpty()));

      testOutput(retMap.values().iterator().next());

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
  private void testOutput(final Feature feature) {
    LOG.info("Attribute - id = {}, name = {}", feature.getId(), feature.getName());
    assertFalse("attributes name not empty", (feature.getName() == null));
    assertNotNull("attributes id not empty", feature.getId());
  }


}
