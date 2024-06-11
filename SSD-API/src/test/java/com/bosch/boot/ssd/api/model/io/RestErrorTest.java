/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.model.io;


import org.junit.Assert;
import org.junit.Test;

/**
 * @author TUD1COB
 */
public class RestErrorTest {

  /**
   * Test Rest Error method
   */
  @Test
  public void testRestError() {
    RestError restError = new RestError(404, "Not Found");
    Assert.assertEquals(404, restError.getCode());
    Assert.assertEquals("Not Found", restError.getTxt());
    restError.setCode(500);
    restError.setTxt("Internal Server Error");
    Assert.assertEquals(500, restError.getCode());
    Assert.assertEquals("Internal Server Error", restError.getTxt());
  }
}

