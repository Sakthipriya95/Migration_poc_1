/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.model.test;


import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * @author TUD1COB
 */
import org.junit.Assert;
import org.junit.Test;

import com.bosch.boot.ssd.api.model.SSD2BCInfo;

/**
 *
 */
public class SSD2BCInfoTest {

  /**
   * Test SSD2BCInfo
   */
  @Test
  public void testSSD2BCInfo() {
    // Create an instance of SSD2BCInfo
    SSD2BCInfo ssd2BCInfo = new SSD2BCInfo();

    // Test the getters and setters for bcName
    String bcName = "BC001";
    ssd2BCInfo.setBcName(bcName);
    Assert.assertEquals(bcName, ssd2BCInfo.getBcName());

    // Test the getters and setters for bcNumber
    BigDecimal bcNumber = new BigDecimal("123.45");
    ssd2BCInfo.setBcNumber(bcNumber);
    Assert.assertEquals(bcNumber, ssd2BCInfo.getBcNumber());

    // Test the getters and setters for assignedNodes
    Set<String> assignedNodes = new HashSet<>();
    assignedNodes.add("Node1");
    assignedNodes.add("Node2");
    ssd2BCInfo.setAssignedNodes(assignedNodes);
    Assert.assertEquals(assignedNodes, ssd2BCInfo.getAssignedNodes());

    // Test the getters and setters for bcRevision
    BigDecimal bcRevision = new BigDecimal("1.2");
    ssd2BCInfo.setBcRevision(bcRevision);
    Assert.assertEquals(bcRevision, ssd2BCInfo.getBcRevision());

    // Test the getters and setters for ssdStatus
    String ssdStatus = "Active";
    ssd2BCInfo.setBcStatus(ssdStatus);
    Assert.assertEquals(ssdStatus, ssd2BCInfo.getBcStatus());

    // Test the getters and setters for bcVariant
    String bcVariant = "VariantA";
    ssd2BCInfo.setBcVariant(bcVariant);
    Assert.assertEquals(bcVariant, ssd2BCInfo.getBcVariant());
  }
}
