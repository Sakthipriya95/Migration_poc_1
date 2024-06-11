/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.entity.test;


import java.math.BigDecimal;

/**
 * @author TUD1COB
 */
import org.junit.Assert;
import org.junit.Test;

import com.bosch.boot.ssd.api.entity.VSdomBcSsdinfo;

/**
 * @author TUD1COB
 */
public class VSdomBcSsdinfoTest {

  /**
   * Test Setters and Getters
   */
  @Test
  public void testSettersAndGetters() {
    // Create an instance of VSdomBcSsdinfo
    VSdomBcSsdinfo vSdomBcSsdinfo = new VSdomBcSsdinfo();

    // Set values using setters
    String bcName = "Test BC Name";
    vSdomBcSsdinfo.setBcName(bcName);

    BigDecimal bcNumber = BigDecimal.valueOf(123);
    vSdomBcSsdinfo.setBcNumber(bcNumber);

    String idc = "Test IDC";
    vSdomBcSsdinfo.setIdc(idc);

    String nodeName = "Test Node Name";
    vSdomBcSsdinfo.setNodeName(nodeName);

    BigDecimal bcRevision = BigDecimal.valueOf(456);
    vSdomBcSsdinfo.setBcRevision(bcRevision);

    String ssdStatus = "Test SSD Status";
    vSdomBcSsdinfo.setSsdStatus(ssdStatus);

    String bcVariant = "Test BC Variant";
    vSdomBcSsdinfo.setBcVariant(bcVariant);

    // Test getters to verify if values are correctly set
    Assert.assertEquals(bcName, vSdomBcSsdinfo.getBcName());
    Assert.assertEquals(bcNumber, vSdomBcSsdinfo.getBcNumber());
    Assert.assertEquals(idc, vSdomBcSsdinfo.getIdc());
    Assert.assertEquals(nodeName, vSdomBcSsdinfo.getNodeName());
    Assert.assertEquals(bcRevision, vSdomBcSsdinfo.getBcRevision());
    Assert.assertEquals(ssdStatus, vSdomBcSsdinfo.getSsdStatus());
    Assert.assertEquals(bcVariant, vSdomBcSsdinfo.getBcVariant());
  }
}

