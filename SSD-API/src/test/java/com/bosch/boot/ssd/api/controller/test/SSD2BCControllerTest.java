/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.controller.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.boot.ssd.api.controller.SSD2BCController;
import com.bosch.boot.ssd.api.exception.ParameterInvalidException;
import com.bosch.boot.ssd.api.exception.ResourceNotFoundException;
import com.bosch.boot.ssd.api.model.SSD2BCInfo;
import com.bosch.boot.ssd.api.service.SSD2BCService;

/**
 * @author TUD1COB
 */
@RunWith(MockitoJUnitRunner.class)
public class SSD2BCControllerTest {

  Logger logger = LoggerFactory.getLogger(SSD2BCControllerTest.class);

  @InjectMocks
  private SSD2BCController ssd2BCController;

  @Mock
  private SSD2BCService ssd2BCService;

  /**
   * Test getSSD2BC with null
   */
  @Test
  public void testGetSSD2BCNegative() {
    SSD2BCController ssd2bcController = new SSD2BCController();
    try {
      ssd2bcController.getSSD2BC(null, "");
      assertNotNull(ssd2bcController);
    }
    catch (ResourceNotFoundException | ParameterInvalidException e) {
      this.logger.error(e.getMessage());
    }

  }


  /**
   * Test getSSD2BCInfoByElementNumber with null
   */
  @Test
  public void testGetSSD2BCInfoByElementNumberNegative() {
    SSD2BCController ssd2bcController = new SSD2BCController();
    try {
      ssd2bcController.getSSD2BCInfoByElementNumber(null, "");
      assertNotNull(ssd2bcController);
    }
    catch (ResourceNotFoundException | ParameterInvalidException e) {
      this.logger.error(e.getMessage());
    }

  }

  /**
   * Test getSSD2BCInfoByBCName with null
   */
  @Test
  public void testGetSSD2BCInfoByBCNameNegative() {
    SSD2BCController ssd2bcController = new SSD2BCController();
    try {
      ssd2bcController.getSSD2BCInfoByBCName(null, "");
      assertNotNull(ssd2bcController);
    }
    catch (ResourceNotFoundException | ParameterInvalidException e) {
      this.logger.error(e.getMessage());
    }

  }

  /**
   * Test getSSD2BCInfoByBCName with mock entries
   */
  @Test
  public void testGetSSD2BCInfoByBCName() {
    try {
      SSD2BCInfo info = new SSD2BCInfo();
      info.setBcName("TESTBC");
      info.setBcNumber(new BigDecimal("1234"));
      info.setBcRevision(new BigDecimal("0"));
      info.setBcVariant("1.0.0");
      info.setBcStatus("SSD for some BC version");
      info.setVarSsdStatus("NO SSD");

      when(this.ssd2BCController.getSSD2BCInfoByBCName("TESTBC", "1.0.0")).thenReturn(info);

      SSD2BCInfo responseEntity = this.ssd2BCController.getSSD2BCInfoByBCName("TESTBC", "1.0.0");

      assertEquals("TESTBC", responseEntity.getBcName());

    }
    catch (ResourceNotFoundException | ParameterInvalidException e) {
      this.logger.error(e.getMessage());
    }

  }

}
