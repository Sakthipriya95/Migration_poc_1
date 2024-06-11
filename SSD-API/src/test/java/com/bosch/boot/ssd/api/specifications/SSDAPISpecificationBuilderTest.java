/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.specifications;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.bosch.boot.ssd.api.model.query.Filter;

/**
 * @author TUD1COB
 */
public class SSDAPISpecificationBuilderTest {


  /**
   * Test Build
   */
  @Test
  public void testBuild() {
    Filter filter = new Filter();
    List<String> label = new ArrayList<>();
    label.add("Label1");
    filter.setLabelName(label);

    SSDAPISpecificationBuilder specificationBuilder = new SSDAPISpecificationBuilder(filter);
    assertNotNull(specificationBuilder.build());

  }

  /**
   * Test build with null
   */
  @Test
  public void testBuildWithNull() {
    SSDAPISpecificationBuilder specificationBuilder = new SSDAPISpecificationBuilder(null);
    assertNull(specificationBuilder.build());

  }
}
