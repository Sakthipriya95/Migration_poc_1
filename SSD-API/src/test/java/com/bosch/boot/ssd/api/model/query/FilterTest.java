/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.model.query;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author TUD1COB
 */
public class FilterTest {

  private Filter filter;

  /**
   * creating new filter object
   */
  @Before
  public void setup() {
    this.filter = new Filter();
  }

  /**
   * Test getLabelName without setting label
   */
  @Test
  public void testEmptyLabelName() {
    Assert.assertNull(this.filter.getLabelName());
  }

  /**
   * Test setters and getters
   */
  @Test
  public void testSetLabelName() {
    List<String> labelNames = Arrays.asList("Label 1", "Label 2");
    this.filter.setLabelName(labelNames);
    Assert.assertEquals(labelNames, this.filter.getLabelName());
  }

  /**
   * Test when label name is modified
   */
  @Test
  public void testModifyLabelName() {
    List<String> initialLabelNames = Arrays.asList("Label 1", "Label 2");
    this.filter.setLabelName(initialLabelNames);
    List<String> modifiedLabelNames = new ArrayList<>(initialLabelNames);
    modifiedLabelNames.add("Label 3");
    this.filter.setLabelName(modifiedLabelNames);
    Assert.assertEquals(modifiedLabelNames, this.filter.getLabelName());
  }
}

