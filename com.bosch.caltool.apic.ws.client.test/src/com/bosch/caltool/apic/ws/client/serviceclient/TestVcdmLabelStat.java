/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.bosch.caltool.apic.ws.client.APICStub.VcdmLabelStats;

/**
 * @author rgo7cob
 */
public class TestVcdmLabelStat extends AbstractSoapClientTest {

  /**
   * PIDC X_Testcustomer->Diesel Engine->PC - Passenger Car->EDC17->Test_HBM_Diesel
   */
  private static final long PIDC_ID = 770816168L;

  private final APICWebServiceClient stub = new APICWebServiceClient();

  /**
   * Check service
   *
   * @throws Exception service error
   */
  @Test
  public void testLabelStat() throws Exception {
    // Time period : 10 years
    VcdmLabelStats[] vcdmLabelStats = this.stub.getVcdmLabelStats(PIDC_ID, 365 * 10);

    assertNotNull("response not null", vcdmLabelStats);
    assertTrue("Label stats are available", vcdmLabelStats.length > 0);

    LOG.info("No. stats returned = {}", vcdmLabelStats.length);
    LOG.info("Vcdm Aprj name : {}", vcdmLabelStats[0].getAprjName());
  }

  /**
   * Check web service without passing time period
   *
   * @throws Exception service error
   */
  @Test
  public void testLabelStatNoTimePeriod() throws Exception {
    VcdmLabelStats[] vcdmLabelStats = this.stub.getVcdmLabelStats(770816168L, 0);

    assertNotNull("response not null", vcdmLabelStats);
    assertTrue("Label stats are available", vcdmLabelStats.length > 0);

    LOG.info("No. stats returned = {}", vcdmLabelStats.length);
    LOG.info("Vcdm Aprj name : {}", vcdmLabelStats[0].getAprjName());
  }

}
