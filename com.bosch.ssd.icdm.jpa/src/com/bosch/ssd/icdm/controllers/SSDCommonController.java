/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.controllers;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.bosch.ssd.icdm.exception.ExceptionUtils;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException.SSDiCDMInterfaceErrorCodes;
import com.bosch.ssd.icdm.model.OEMRuleDescriptionInput;
import com.bosch.ssd.icdm.model.RuleIdDescriptionModel;
import com.bosch.ssd.icdm.model.SSDConfigEnums.SSDConfigParams;
import com.bosch.ssd.icdm.service.internal.SSDCommonService;
import com.bosch.ssd.icdm.service.internal.servinterface.SSDNodeInfoAccessor;

/**
 * Controller Class for validating the input and invoking service methods in @SSDCommonService
 *
 * @author SSN9COB
 */
public class SSDCommonController {

  private SSDCommonService commonService;
  private final SSDNodeInfoAccessor ssdNodeInfo;

  /**
   * @param ssdNodeInfo SSD Node Info
   */
  public SSDCommonController(final SSDNodeInfoAccessor ssdNodeInfo) {
    this.ssdNodeInfo = ssdNodeInfo;
  }

  /**
   * @return the Node Info
   */
  public SSDNodeInfoAccessor getSSDNodeInfo() {
    return this.ssdNodeInfo;
  }
  /**
   * @return service instance
   */
  public SSDCommonService getSSDCommonService() {
    if (Objects.isNull(this.commonService)) {
      createSSDCommonService();
    }
    return this.commonService;
  }

  /**
   * @throws SSDiCDMInterfaceException exception
   */
  private void createSSDCommonService() {
    this.commonService = new SSDCommonService(getSSDNodeInfo());
  }

  /**
   * fetchOEMDetails - Controller method to handle all validations and pass the input to service class
   *
   * @param ruleIdWithRevList set
   * @return map
   * @throws SSDiCDMInterfaceException exception
   */
  public Map<String, RuleIdDescriptionModel> fetchOEMDetails(final Set<OEMRuleDescriptionInput> ruleIdWithRevList)
      throws SSDiCDMInterfaceException {
    /*
     * Null or Empty Rule IDs list
     */
    if (Objects.isNull(ruleIdWithRevList) || ruleIdWithRevList.isEmpty()) {
      throw ExceptionUtils.createAndThrowException(null, "Rule IDs List cannot be null or empty",
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    return getSSDCommonService().getOEMDetailsForRuleId(ruleIdWithRevList);
  }

  /**
   * fetchOEMDetails - Controller method to handle all validations and pass the input to service class
   *
   * @param name name
   * @return String
   * @throws SSDiCDMInterfaceException exception
   */
  public String getConfigValue(final SSDConfigParams name) throws SSDiCDMInterfaceException {
    /*
     * Null ENUM
     */
    if (Objects.isNull(name)) {
      throw ExceptionUtils.createAndThrowException(null, "SSD Config Parameter Key cannot be empty",
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    return getSSDCommonService().getConfigValue(name);

  }

  /**
   * getProRevId - Controller method to handle all validations and pass the input to service class
   *
   * @param nodeID id
   * @return id
   * @throws SSDiCDMInterfaceException exception
   */
  public BigDecimal getProRevId(final BigDecimal nodeID) throws SSDiCDMInterfaceException {
    /*
     * Null ID
     */
    if (Objects.isNull(nodeID)) {
      throw ExceptionUtils.createAndThrowException(null, "Node ID cannot be null",
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    return getSSDCommonService().getProRevId(nodeID);
  }
}
