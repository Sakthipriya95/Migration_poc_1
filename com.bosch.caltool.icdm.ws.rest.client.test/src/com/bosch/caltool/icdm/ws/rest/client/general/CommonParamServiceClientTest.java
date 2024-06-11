/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.general;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author HNU1COB
 */
public class CommonParamServiceClientTest extends AbstractRestClientTest {

  private final static String PARAM_ID = "QUOT_ATTR_ID";
  private final static String INVALID_PARAM_ID = "INVAL_PARAM ";

  /**
   * Test method for {@link CommonParamServiceClient#getAll()}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetAll() throws ApicWebServiceException {
    CommonParamServiceClient servClient = new CommonParamServiceClient();
    Map<String, String> retMap = servClient.getAll();
    assertFalse("Response should not be Empty or null", ((retMap == null) || retMap.isEmpty()));
  }

  /**
   * Test method for {@link CommonParamServiceClient#getParameterValue(java.lang.String)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetParameterValue() throws ApicWebServiceException {
    CommonParamServiceClient servClient = new CommonParamServiceClient();
    String ret = servClient.getParameterValue(PARAM_ID);
    assertFalse("Response should not be null or empty", ((ret == null) || (ret.isEmpty())));
  }

  /**
   * Test method for {@link CommonParamServiceClient#getParameterValue(java.lang.String)}. Negative test
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetParameterValueNegative() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Invalid parameter name '" + INVALID_PARAM_ID + "'");

    new CommonParamServiceClient().getParameterValue(INVALID_PARAM_ID);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

}
