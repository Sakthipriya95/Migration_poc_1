/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.ssd.service.client.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bosch.caltool.ssd.service.client.SsdReleaseInfoServiceClient;
import com.bosch.caltool.ssd.service.client.model.ProjectReleaseModel;
import com.bosch.caltool.ssd.service.client.model.SsdReleaseInfoModel;
import com.bosch.caltool.ssd.service.exception.SsdReleaseInfoException;

/**
 * @author QRK1COB
 * 
 * Unit tests for SsdReleaseInfoServiceClient.
 */
@SuppressWarnings("restriction")
public class SsdReleaseInfoServiceClientTest {

  private SsdReleaseInfoServiceClient ssdReleaseInfoService;

  /**
   * Set up the test environment.
   */
  @Before
  public void setup() {
    this.ssdReleaseInfoService = new SsdReleaseInfoServiceClient();
  }

  /* POSTIVE TEST CASES */
  
  /**
   * Test for getting release info by node ID.
   * @throws SsdReleaseInfoException SsdReleaseInfoException
   */
  @Test
  public void shouldGetReleaseInfoByNodeId() throws SsdReleaseInfoException {

    List<ProjectReleaseModel> data = this.ssdReleaseInfoService.getReleaseInfoByNodeId(3529339148L);

    if (!data.isEmpty()) {
      // Check if the description matches the expected value
      Assert.assertEquals("P662VD1-SSD01 T-Rel NEF4 EU6 24V VCM CRIN2HD CP33 SCR2.2 2014-04-15 V01",
          data.get(0).getDescription());
    }

  }

  /**
   * Test for getting release details.
   * @throws SsdReleaseInfoException SsdReleaseInfoException
   */
  @Test
  public void shouldGetReleaseDetails() throws SsdReleaseInfoException {

    SsdReleaseInfoModel ssdReleaseInfoModel = this.ssdReleaseInfoService.getReleaseDetails(8277157L);

    // Check if the feature value matches the expected value
    Assert.assertEquals("APP1-Master- with Increased Low Idle Engine Speed",
        ssdReleaseInfoModel.getFeatureValueInfo().get("APP-Strategie"));

  }

  /* NEGATIVE TEST CASES */

  /**
   * Test for a null ProjectReleaseId, expecting SsdReleaseInfoException.
   * @throws SsdReleaseInfoException SsdReleaseInfoException
   */
  @Test(expected = SsdReleaseInfoException.class)
  public void shouldThrowExceptionForNullProRelId() throws SsdReleaseInfoException {
    // Check if an exception is thrown when a null ProjectReleaseId is provided
    this.ssdReleaseInfoService.getReleaseDetails(null);
  }

  /**
   * Test for a null NodeId, expecting SsdReleaseInfoException.
   * @throws SsdReleaseInfoException SsdReleaseInfoException
   */
  @Test(expected = SsdReleaseInfoException.class)
  public void shouldThrowExceptionForNullNodeId() throws SsdReleaseInfoException {
    // Check if an exception is thrown when a null NodeId is provided
    this.ssdReleaseInfoService.getReleaseInfoByNodeId(null);
  }
  
  /* NEGATIVE TEST CASES */

  /**
   * Test for an invalid ProjectReleaseId (non-existent ID), expecting SsdReleaseInfoException.
   * @throws SsdReleaseInfoException SsdReleaseInfoException
   */
  @Test(expected = SsdReleaseInfoException.class)
  public void shouldThrowExceptionForInvalidProRelId() throws SsdReleaseInfoException {
      // Provide an invalid ProjectReleaseId that does not exist
      this.ssdReleaseInfoService.getReleaseDetails(-1L); 
  }

  /**
   * Test for an invalid NodeId (non-existent ID), expecting SsdReleaseInfoException.
   * @throws SsdReleaseInfoException SsdReleaseInfoException
   */
  @Test(expected = SsdReleaseInfoException.class)
  public void shouldThrowExceptionForInvalidNodeId() throws SsdReleaseInfoException {
      // Provide an invalid NodeId that does not exist
      this.ssdReleaseInfoService.getReleaseInfoByNodeId(-1L); 
  }

  
}
