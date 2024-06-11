/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import com.bosch.ssd.icdm.exception.ExceptionUtils;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException.SSDiCDMInterfaceErrorCodes;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.CDRRuleExt;
import com.bosch.ssd.icdm.service.internal.SSDRuleHistoryService;
import com.bosch.ssd.icdm.service.internal.servinterface.SSDNodeInfoAccessor;

/**
 * Controller Class for validating the input and invoking service methods in @SSDRuleHistoryService
 *
 * @author SSN9COB
 */
public class SSDRuleHistoryController {

  private SSDRuleHistoryService ruleHistoryService;
  private final SSDNodeInfoAccessor ssdNodeInfo;

  /**
   * @param ssdNodeInfo SSD Node Info
   */
  public SSDRuleHistoryController(final SSDNodeInfoAccessor ssdNodeInfo) {
    this.ssdNodeInfo = ssdNodeInfo;
  }

  /**
   * @return the ssd Node Info
   */
  public SSDNodeInfoAccessor getSSDNodeInfo() {
    return this.ssdNodeInfo;
  }

  /**
   * @return service Instance
   */
  public SSDRuleHistoryService getSSDRuleHistoryService() {
    if (Objects.isNull(this.ruleHistoryService)) {
      createRuleHistoryService();
    }
    return this.ruleHistoryService;
  }

  /**
   * @throws SSDiCDMInterfaceException exception
   */
  private void createRuleHistoryService() {
    this.ruleHistoryService = new SSDRuleHistoryService(getSSDNodeInfo());
  }

  /**
   * getRuleHistoryForNode - Controller method to handle all validations and pass the input to service class
   *
   * @param rule rules
   * @param ssdNodeId node
   * @param isCompliRule isCompliRule
   * @return list
   * @throws SSDiCDMInterfaceException exception
   */
  public List<CDRRuleExt> getRuleHistoryForNode(final CDRRule rule, final BigDecimal ssdNodeId,
      final boolean isCompliRule)
      throws SSDiCDMInterfaceException {
    if (Objects.isNull(rule) || Objects.isNull(rule.getRuleId())) {
      throw ExceptionUtils.createAndThrowException(null, "Rule Details are not available",
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    return getSSDRuleHistoryService().getRuleHistoryForNode(rule, ssdNodeId, isCompliRule);
  }
}
