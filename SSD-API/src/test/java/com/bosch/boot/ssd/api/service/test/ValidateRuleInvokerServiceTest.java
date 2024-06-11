/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.service.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.bosch.boot.ssd.api.controller.SSDRuleValidationController;
import com.bosch.boot.ssd.api.exception.ParameterInvalidException;
import com.bosch.boot.ssd.api.model.query.Filter;
import com.bosch.boot.ssd.api.service.SSDFileEditorService;
import com.bosch.boot.ssd.api.service.ValidateRuleInvokerService;
import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.checkssd.datamodel.SSDStatement;

/**
 * @author TAB1JA
 */
@RunWith(MockitoJUnitRunner.class)
public class ValidateRuleInvokerServiceTest {

  @InjectMocks
  private ValidateRuleInvokerService validateRuleInvokerService;

  @Mock
  private SSDFileEditorService ssdFileEditorService;

  private final ILoggerAdapter logger =
      new Log4JLoggerAdapterImpl(LogManager.getLogger(SSDRuleValidationController.class));

  private final Map<String, String> resultMap = new HashMap<>();

  /**
   *
   */
  @Before()
  public void setUp() {
    this.resultMap.put("Rail_swtOverPresEna_C", "VALUE");
    this.resultMap.put("DDRC_RatDeb.Rail_numOverPresReactUDRat_C", "VALUE");
  }


  /**
   * @throws ParameterInvalidException e
   */
  @Test
  public void validateRuleTest() throws ParameterInvalidException {
    when(this.ssdFileEditorService.getLabelByType(any(Filter.class))).thenReturn(this.resultMap);
    String message = this.validateRuleInvokerService.validateRules("TestFiles\\fileEditortest.ssd", this.logger);
    assertNotNull(message);
  }

  /**
   * @throws ParameterInvalidException e
   */
  @Test
  public void validateGetErrorInfo() throws ParameterInvalidException {
    SSDStatement ssdStatement = new SSDStatement() {/**/};
    ssdStatement.setError(10);
    this.validateRuleInvokerService.getErrorInfo(ssdStatement);
    assertEquals(this.validateRuleInvokerService.isError(), true);
  }

  /**
   * Test ValidateRuleInvokerService
   */
  @Test
  public void testValidateRuleInvokerService() {
    assertNotNull(this.validateRuleInvokerService.validateRules("./././TestFiles/fileEditortest.ssd", this.logger));
    assertNotNull(
        this.validateRuleInvokerService.validateRules("./././TestFiles/P1768V250_R100c_F1C.SSD", this.logger));
  }
}
