/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.service.internal.servinterface;

import java.util.Objects;

import javax.persistence.EntityManager;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.ssd.icdm.constants.SSDiCDMInterfaceConstants;
import com.bosch.ssd.icdm.controllers.SSDCommonController;
import com.bosch.ssd.icdm.controllers.SSDReleaseGenerationController;
import com.bosch.ssd.icdm.controllers.SSDReportController;
import com.bosch.ssd.icdm.controllers.SSDReviewRulesController;
import com.bosch.ssd.icdm.controllers.SSDRuleHistoryController;
import com.bosch.ssd.icdm.exception.ExceptionUtils;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException.SSDiCDMInterfaceErrorCodes;
import com.bosch.ssd.icdm.logger.SSDiCDMInterfaceLogger;

/**
 * Class that acts as an intermediate between Calling module (iCDM & Test Plugin) & Service Module
 *
 * @author SSN9COB
 */
public class SSDInternalAccessor implements SSDiCDMServiceInterface {

  private final String userName;
  private final String password;
  private final String dbProp;
  private final EntityManager entityManager;

  private SSDReleaseGenerationController relGenController;

  private SSDNodeInfoAccessor nodeInformationService;
  private SSDCommonController commonController;
  private SSDRuleHistoryController ruleHistoryController;
  private SSDReportController reportController;
  private SSDReviewRulesController reviewRuleController;


  /**
   * Entry Point for any module to access ssd iCDM interface service methods
   *
   * @param logger   Logger from calling module
   * @param userName user Name
   * @param password password
   * @param dbProp   Database Property file name
   */
  public SSDInternalAccessor(final ILoggerAdapter logger, final String userName, final String password,
      final String dbProp) {
    SSDiCDMInterfaceLogger.initClient();
    // Set the logger to SSDiCDMInterfaceLogger
    SSDiCDMInterfaceLogger.setLogger(logger);
    this.userName = userName;
    this.password = password;
    this.dbProp = dbProp;
    this.entityManager = null;
  }

  /**
   * Entry Point for any module to access ssd iCDM interface service methods
   *
   * @param logger        Logger from calling module
   * @param userName      user Name
   * @param entityManager Entity Manager
   */
  public SSDInternalAccessor(final ILoggerAdapter logger, final String userName, final EntityManager entityManager) {
    SSDiCDMInterfaceLogger.initClient();
    // Set the logger to SSDiCDMInterfaceLogger
    SSDiCDMInterfaceLogger.setLogger(logger);
    this.userName = userName;
    this.entityManager = entityManager;
    this.password = null;
    this.dbProp = null;
  }

  /**
   * Returns SSD Login Service
   *
   * @return SSDLoginService
   * @throws SSDiCDMInterfaceException Exception
   */
  @Override
  public SSDNodeInfoAccessor getSSDNodeInfo() throws SSDiCDMInterfaceException {
    if (Objects.isNull(this.nodeInformationService)) {
      createNodeInformatioService();
    }
    return this.nodeInformationService;
  }


  /**
   * create Login service & Entity Manger(handled internally)
   *
   * @throws SSDiCDMInterfaceException exception
   */
  private void createNodeInformatioService() throws SSDiCDMInterfaceException {

    if (Objects.isNull(this.password)) {
      if (Objects.nonNull(this.userName) && (Objects.nonNull(this.entityManager))) {
        // From iCDM
        this.nodeInformationService = new SSDNodeInfoAccessor(this.userName, this.entityManager);
      }
      else {
        throw ExceptionUtils.createAndThrowException(null,
            SSDiCDMInterfaceConstants.EXCEPTION_MESSAGE_LOGIN_CREDENTIALS, SSDiCDMInterfaceErrorCodes.GENERAL_EXCEPTION,
            true);
      }
    }
    else {
      // From interface Test Module
      this.nodeInformationService = new SSDNodeInfoAccessor(this.userName, this.password, this.dbProp);
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public SSDReviewRulesController getSSDReviewRulesController() {
    if (Objects.isNull(this.reviewRuleController)) {
      reviewRuleController = new SSDReviewRulesController(this.nodeInformationService);
    }
    return reviewRuleController;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDReportController getReportController() {
    if (Objects.isNull(this.reportController)) {
      reportController = new SSDReportController(this.nodeInformationService);
    }
    return reportController;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDRuleHistoryController getRuleHistoryController() {
    if (Objects.isNull(this.ruleHistoryController)) {
      ruleHistoryController = new SSDRuleHistoryController(this.nodeInformationService);
    }
    return ruleHistoryController;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDCommonController getSSDCommonController() {
    if (Objects.isNull(this.commonController)) {
      commonController = new SSDCommonController(this.nodeInformationService);
    }
    return commonController;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDReleaseGenerationController getReleaseGenerationController() {
    if (Objects.isNull(this.relGenController)) {
      this.relGenController = new SSDReleaseGenerationController(this.nodeInformationService);
    }
    return relGenController;
  }

}
