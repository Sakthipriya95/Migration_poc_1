/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.bosch.caltool.icdm.model.rm.RmMetaData;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;


/**
 * Test Client class for Meta Data Collection
 *
 * @author rgo7cob
 */
public class RmMetaDataClientTest extends AbstractRestClientTest {


  /**
   * Test method for {@link com.bosch.caltool.icdm.ws.rest.client.apic.RmMetaDataClient#getMetaData()}.
   */
  @Test
  public void testGetAllFunctions() {

    RmMetaDataClient client = new RmMetaDataClient();


    try {
      RmMetaData metaData = client.getMetaData();
      testOutput(metaData);
    }
    catch (Exception e) {
      LOG.error("Error in WS call", e);
      assertNull("Error in WS call", e);
    }
  }

  /**
   * @param string
   * @param wpRes
   */
  private void testOutput(final RmMetaData metaData) {
    LOG.info("Meta data obtained");
    assertNotNull(metaData + ": object not null");

  }
}
