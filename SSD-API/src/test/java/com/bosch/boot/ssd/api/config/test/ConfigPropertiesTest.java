/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.config.test;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;

import com.bosch.boot.ssd.api.config.ConfigProperties;

/**
 * @author TUD1COB
 *
 */
public class ConfigPropertiesTest {
  
  /**
   * Testcase to check File location when empty string is passed
   */
  @Test
  public void testGetFileLocation_PropertiesEmpty() {
      String propertyName = "TestProperty";
      String actualLocation =new ConfigProperties("").getFileLocation(propertyName);
      assertNotNull(actualLocation);
  }
  /**
   * Test to check file location when location is passed
   */
  @Test
  public void testGetFileLocation_Properties() {
      String propertyName = "TestProperty";
      String actualLocation =new ConfigProperties("D:/CalTools_Webservice/properties/ssdapi.properties").getFileLocation(propertyName);
      assertNotNull(actualLocation);
  }
}
