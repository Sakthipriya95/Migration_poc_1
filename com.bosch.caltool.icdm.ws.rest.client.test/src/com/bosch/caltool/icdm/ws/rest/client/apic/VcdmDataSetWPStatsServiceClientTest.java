/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.VcdmDataSetWPStats;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class VcdmDataSetWPStatsServiceClientTest extends AbstractRestClientTest {


  /**
   * Test retrieval of all VcdmDataSetWPStats for a PIDC
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void test01() throws ApicWebServiceException {
    VcdmDataSetWPStatsServiceClient servClient = new VcdmDataSetWPStatsServiceClient();
    Set<VcdmDataSetWPStats> retSet = servClient.getStatisticsByPidcId(774555767L, 20043644, 0);

    assertNotNull("Response should not be null", retSet);
    assertFalse("Response should not be empty", retSet.isEmpty());

    testOutput(retSet.iterator().next());
  }

  /**
   * Test output
   *
   * @param reportData output object
   */
  private void testOutput(final VcdmDataSetWPStats data) {
    LOG.info("ID = {}, Work Package Name = {}", data.getId(), data.getWorkpkgName());
    assertNotNull("Work Package name not null", data.getWorkpkgName());
  }


}
