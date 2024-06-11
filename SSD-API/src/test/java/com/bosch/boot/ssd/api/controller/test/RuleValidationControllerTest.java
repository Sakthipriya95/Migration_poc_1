/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.controller.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import com.bosch.boot.ssd.api.controller.SSDRuleValidationController;
import com.bosch.boot.ssd.api.exception.ParameterInvalidException;
import com.bosch.boot.ssd.api.model.RuleValidationOutputMessage;
import com.bosch.boot.ssd.api.model.SSDRuleInputModel;
import com.bosch.boot.ssd.api.service.ValidateRuleInvokerService;
import com.bosch.boot.ssd.api.util.SSDAPIUtil;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;

/**
 * @author TAB1JA
 */
@RunWith(MockitoJUnitRunner.class)
public class RuleValidationControllerTest {

  @InjectMocks
  SSDRuleValidationController ssdRuleValidationController;

  @Mock
  private ValidateRuleInvokerService validateRuleInvokerService;

  private String errorMessage;

  private String filePath;

  private final SSDRuleInputModel ssdRuleInputModel = new SSDRuleInputModel();

  private final Map<Integer, String> lineNoAndError = new HashMap<>();

  /**
   *
   */
  @Before()
  public void setUp() {
    this.ssdRuleInputModel.setRuleText("Sample Rule");
    this.errorMessage = "Sample validation message";
    this.filePath = SSDAPIUtil.getAppDataFolderPath();
    this.filePath += "SSDValidate_temp.SSD";

    this.lineNoAndError.put(1, "Error 1");
    this.lineNoAndError.put(2, "Error 2");
  }

  /**
   *
   */
  @Test
  public void getRuleValidationTest() {
    try {

      when(this.validateRuleInvokerService.validateRules(eq(this.filePath), any(Log4JLoggerAdapterImpl.class)))
          .thenReturn(this.errorMessage);
      when(this.validateRuleInvokerService.getLineNoAndError()).thenReturn(this.lineNoAndError);
      ResponseEntity<List<RuleValidationOutputMessage>> result =
          this.ssdRuleValidationController.getRuleValidation(this.ssdRuleInputModel);
      assertEquals(200, result.getStatusCodeValue());
    }
    catch (ParameterInvalidException e) {
      e.printStackTrace();
    }
  }


  /**
   * Test case to check what happens when null is passed as input
   */
  @Test
  public void getRuleValidationWithNullInputTest() {
    try {
      this.ssdRuleInputModel.setRuleText(null);
      ResponseEntity<List<RuleValidationOutputMessage>> result =
          this.ssdRuleValidationController.getRuleValidation(this.ssdRuleInputModel);
      assertEquals(500, result.getStatusCodeValue());
    }
    catch (ParameterInvalidException e) {
      e.printStackTrace();
    }
  }


}
