/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.service.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.bosch.boot.ssd.api.entity.VSdomBcSsdinfo;
import com.bosch.boot.ssd.api.exception.ParameterInvalidException;
import com.bosch.boot.ssd.api.exception.ResourceNotFoundException;
import com.bosch.boot.ssd.api.model.SSD2BCInfo;
import com.bosch.boot.ssd.api.repository.VSdomBcSSDInfoRepository;
import com.bosch.boot.ssd.api.service.SSD2BCService;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class SSD2BCServiceTest {

  @InjectMocks
  private SSD2BCService ssd2BCService;

  @Mock
  private VSdomBcSSDInfoRepository vSdomBcSSDInfoRepository;

  private final List<VSdomBcSsdinfo> bcIinfo = new ArrayList<>();


  /**
  *
  */
  @Before()
  public void setUp() {
    VSdomBcSsdinfo info = new VSdomBcSsdinfo();
    info.setBcName("TESTBC");
    info.setBcNumber(new BigDecimal("1234"));
    info.setBcRevision(new BigDecimal("0"));
    info.setBcVariant("1.0.0");
    info.setSsdStatus("SSD for some BC version");
    info.setVarSsdStatus("NO SSD");
    this.bcIinfo.add(info);
  }

  /**
   * @throws ParameterInvalidException
   */
  @Test
  public void testGetSSD2BCInfoByBcNameAndVariant() {
    try {
      // this.vSdomBcSSDInfoRepository.findByBcNameAndBcVariant(bcName, variant)
      when(this.vSdomBcSSDInfoRepository.findByBcNameAndBcVariant("TESTBC", "1.0.0"))
          .thenReturn(Optional.of(this.bcIinfo));
      SSD2BCInfo response = new SSD2BCInfo();
      response = this.ssd2BCService.getSSD2BCInfoByBcNameAndVariant("TESTBC", "1.0.0");
      assertEquals("TESTBC", response.getBcName());

    }
    catch (ResourceNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * @throws ResourceNotFoundException no resource error
   */
  @Test(expected = ResourceNotFoundException.class)
  public void testGetSSD2BCInfoByBcNameAndVariantNegative() throws ResourceNotFoundException {
    try {
      // this.vSdomBcSSDInfoRepository.findByBcNameAndBcVariant(bcName, variant)
      when(this.vSdomBcSSDInfoRepository.findByBcNameAndBcVariant("TESTBC", "1.0.0"))
          .thenReturn(Optional.of(new ArrayList<>()));
      SSD2BCInfo response = new SSD2BCInfo();
      response = this.ssd2BCService.getSSD2BCInfoByBcNameAndVariant("TESTBC", "1.0.0");
      assertEquals("TESTBC", response.getBcName());

    }
    catch (ResourceNotFoundException e) {
      // TODO Auto-generated catch block
      throw e;
    }
  }
}
