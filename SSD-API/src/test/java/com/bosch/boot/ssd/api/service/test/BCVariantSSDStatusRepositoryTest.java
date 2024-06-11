/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.service.test;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

/**
 * @author TUD1COB
 */
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.bosch.boot.ssd.api.entity.TSdomBcVariantSsdStatus;
import com.bosch.boot.ssd.api.repository.BCVariantSSDStatusRepository;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class BCVariantSSDStatusRepositoryTest {

  @Mock
  private BCVariantSSDStatusRepository repositoryMock;

  /**
   * Test FindByElNummerAndVariant
   */
  @Test
  public void testFindByElNummerAndVariant() {
    // Create a sample BigDecimal and String for testing
    BigDecimal elnummer = BigDecimal.valueOf(123);
    String variant = "variant";

    // Create a mock object for the result
    TSdomBcVariantSsdStatus expectedResult = new TSdomBcVariantSsdStatus();
    expectedResult.setId(26678);
    expectedResult.setElNummer(new BigDecimal(21471));
    expectedResult.setVariant("10.3.0_TEST_BEG_JLR_MY14");
    expectedResult.setSsd("N");
    expectedResult.setCreatedBy("POL2FE");
    expectedResult.setCreatedDate(new Date("15-FEB-13"));
    expectedResult.setDeleted("N");
    when(this.repositoryMock.findByElNummerAndVariant(elnummer, variant)).thenReturn(Optional.of(expectedResult));
    assertEquals(this.repositoryMock.findByElNummerAndVariant(elnummer, variant).get(), expectedResult);
  }
}
