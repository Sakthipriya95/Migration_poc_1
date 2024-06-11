/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.model.wp.Region;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author HNU1COB
 */
public class RegionServiceClientTest extends AbstractRestClientTest {

  private static final Long REG_ID = 1423095877l;
  private static final Long INVALID_REG_ID = -100005877l;


  /**
   * Test method for {@link RegionServiceClient#getAllRegion()}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetAllRegion() throws ApicWebServiceException {
    RegionServiceClient servClient = new RegionServiceClient();
    Map<String, Region> retMap = servClient.getAllRegion();
    assertFalse("Response should not be null or empty", (retMap == null) || (retMap.isEmpty()));
  }


  /**
   * Test method for {@link RegionServiceClient#getRegionById(java.lang.Long)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetRegionById() throws ApicWebServiceException {
    RegionServiceClient servClient = new RegionServiceClient();
    Region ret = servClient.getRegionById(REG_ID);
    assertNotNull("Response should not be null", ret);
    assertEquals("RegionCode is equal", "NA", ret.getRegionCode());
    assertEquals("RegionName is equal", "North America", ret.getRegionName());
    assertEquals("RegionNameGer is equal", "Nordamerika", ret.getRegionNameGer());
    assertEquals("CreatedUser is equal ", "DGS_ICDM", ret.getCreatedUser());
    assertNotNull("CreatedDate is not null", ret.getCreatedDate());

  }

  /**
   * Test method for {@link RegionServiceClient#getRegionById(java.lang.Long)}. Negative test- Region with ID not found
   *
   * @throws ApicWebServiceException -Region with ID not found
   */
  @Test
  public void testGetRegionByIdNegative() throws ApicWebServiceException {
    RegionServiceClient servClient = new RegionServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Region with ID '" + INVALID_REG_ID + "' not found");
    servClient.getRegionById(INVALID_REG_ID);
    fail("Expected Exception not thrown");
  }


}
