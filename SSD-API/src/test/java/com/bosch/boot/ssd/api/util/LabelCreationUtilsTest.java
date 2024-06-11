/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.util;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.bosch.calmodel.caldata.CalData;

/**
 * @author TUD1COB
 */
public class LabelCreationUtilsTest {

  private CalData calData;

  /**
   * Before
   */
  @BeforeEach
  public void setup() {
    this.calData = new CalData();
  }

  /**
   * Test GetCalDataAxis
   */
  @Test
  public void testGetCalDataAxis() {
    String axisType = "AXIS_PTS";
    Assert.assertNotNull(new LabelCreationUtils(null, null, null).getCalDataAxis(axisType));
  }


}
