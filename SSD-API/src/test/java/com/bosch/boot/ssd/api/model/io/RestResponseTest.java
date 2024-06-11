/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.model.io;


import org.junit.Assert;
import org.junit.Test;

/**
 * @author TUD1COB
 */
public class RestResponseTest {

  /**
   * Test success response
   */
  @Test
  public void testSuccessResponse() {
    RestResponse response = new RestResponse(true);
    Assert.assertTrue(response.isSuccess());
    Assert.assertNull(response.getResult());
    Assert.assertNull(response.getError());
    response.setSuccess(false);
    Assert.assertFalse(response.isSuccess());
  }

  /**
   * Test response which has some result
   */
  @Test
  public void testResponseWithResult() {
    String result = "Sample Result";
    RestResponse response = new RestResponse(true, result);
    Assert.assertTrue(response.isSuccess());
    Assert.assertEquals(result, response.getResult());
    Assert.assertNull(response.getError());
    String newResult = "Updated Result";
    response.setResult(newResult);
    Assert.assertEquals(newResult, response.getResult());
  }


}
