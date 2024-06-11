/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.entity.test;


import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author TUD1COB
 */
import org.junit.Assert;
import org.junit.Test;

import com.bosch.boot.ssd.api.entity.TLdb2SsdCrwReview;
import com.bosch.boot.ssd.api.entity.TLdb2SsdCrwRule;

/**
 * @author TUD1COB
 */
public class TLdb2SsdCrwRuleTest {

  /**
   * Test setters and getters
   */
  @Test
  public void testGetterAndSetterMethods() {
    // Create an instance of TLdb2SsdCrwRule
    TLdb2SsdCrwRule rule = new TLdb2SsdCrwRule();

    // Set values using setter methods
    BigDecimal id = BigDecimal.valueOf(123);
    rule.setId(id);

    String createdBy = "John Doe";
    rule.setCreatedBy(createdBy);

    LocalDateTime createdDate = LocalDateTime.now();
    rule.setCreatedDate(createdDate);

    String isLatestRvw = "Y";
    rule.setIsLatestRvw(isLatestRvw);

    BigDecimal labLabId = BigDecimal.valueOf(456);
    rule.setLabLabId(labLabId);

    BigDecimal labObjId = BigDecimal.valueOf(789);
    rule.setLabObjId(labObjId);

    String modifiedBy = "Jane Smith";
    rule.setModifiedBy(modifiedBy);

    LocalDateTime modifiedDate = LocalDateTime.now();
    rule.setModifiedDate(modifiedDate);

    BigDecimal rev = BigDecimal.valueOf(1);
    rule.setRev(rev);

    TLdb2SsdCrwReview crwReview = new TLdb2SsdCrwReview();
    rule.settLdb2SsdCrwReview(crwReview);

    // Verify the values using getter methods
    Assert.assertEquals(id, rule.getId());
    Assert.assertEquals(createdBy, rule.getCreatedBy());
    Assert.assertEquals(createdDate, rule.getCreatedDate());
    Assert.assertEquals(isLatestRvw, rule.getIsLatestRvw());
    Assert.assertEquals(labLabId, rule.getLabLabId());
    Assert.assertEquals(labObjId, rule.getLabObjId());
    Assert.assertEquals(modifiedBy, rule.getModifiedBy());
    Assert.assertEquals(modifiedDate, rule.getModifiedDate());
    Assert.assertEquals(rev, rule.getRev());
    Assert.assertEquals(crwReview, rule.gettLdb2SsdCrwReview());
  }
}

