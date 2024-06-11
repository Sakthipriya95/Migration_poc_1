/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.entity.test;


import java.math.BigDecimal;
import java.util.Date;

/**
 * @author TUD1COB
 */
import org.junit.Assert;
import org.junit.Test;

import com.bosch.boot.ssd.api.entity.TSdomBcVariantSsdStatus;

/**
 * @author TUD1COB
 */
public class TSdomBcVariantSsdStatusTest {

  /**
   * Test getters and setters
   */
  @Test
  public void testGetterAndSetterMethods() {
    // Create an instance of TSdomBcVariantSsdStatus
    TSdomBcVariantSsdStatus status = new TSdomBcVariantSsdStatus();

    // Set values using setter methods
    long id = 123;
    status.setId(id);

    String createdBy = "John Doe";
    status.setCreatedBy(createdBy);

    Date createdDate = new Date();
    status.setCreatedDate(createdDate);

    String deleted = "Y";
    status.setDeleted(deleted);

    String deletedBy = "Jane Smith";
    status.setDeletedBy(deletedBy);

    Date deletedDate = new Date();
    status.setDeletedDate(deletedDate);

    BigDecimal elNummer = BigDecimal.valueOf(456);
    status.setElNummer(elNummer);

    String ssd = "Y";
    status.setSsd(ssd);

    String variant = "Variant A";
    status.setVariant(variant);

    // Verify the values using getter methods
    Assert.assertEquals(id, status.getId());
    Assert.assertEquals(createdBy, status.getCreatedBy());
    Assert.assertEquals(createdDate, status.getCreatedDate());
    Assert.assertEquals(deleted, status.getDeleted());
    Assert.assertEquals(deletedBy, status.getDeletedBy());
    Assert.assertEquals(deletedDate, status.getDeletedDate());
    Assert.assertEquals(elNummer, status.getElNummer());
    Assert.assertEquals(ssd, status.getSsd());
    Assert.assertEquals(variant, status.getVariant());
  }
}
