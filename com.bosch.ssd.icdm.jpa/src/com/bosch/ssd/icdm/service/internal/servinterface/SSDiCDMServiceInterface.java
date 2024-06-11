/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.service.internal.servinterface;

import com.bosch.ssd.icdm.controllers.SSDCommonController;
import com.bosch.ssd.icdm.controllers.SSDReleaseGenerationController;
import com.bosch.ssd.icdm.controllers.SSDReportController;
import com.bosch.ssd.icdm.controllers.SSDReviewRulesController;
import com.bosch.ssd.icdm.controllers.SSDRuleHistoryController;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException;

/**
 * Interface for iCDM to access all the service methods
 *
 * @author ssn9cob
 */
public interface SSDiCDMServiceInterface {

  /**
   * Returns SSD Login Service which has all the Login & Transaction Methods
   *
   * @return SSDNodeInfoAccessor
   * @throws SSDiCDMInterfaceException Exception
   */
  SSDNodeInfoAccessor getSSDNodeInfo() throws SSDiCDMInterfaceException;

  /**
   * Returns SSD Review Rules Controller which handles all the review rules module methods
   *
   * @return SSDReviewRulesController
   */
  SSDReviewRulesController getSSDReviewRulesController();

  /**
   * Returns the SSD Release Controller which handles all the SSD Release Report DB Modules
   *
   * @return SSDReportController
   */
  SSDReportController getReportController();

  /**
   * Returns Rule history Controller which handles all the rule history DB queries
   *
   * @return SSDRuleHistoryController
   */
  SSDRuleHistoryController getRuleHistoryController();

  /**
   * Returns SSD Common Controller which handles CheckSSD OEM Information, Get Config value, get pro revid queries
   *
   * @return SSDCommonController
   */
  SSDCommonController getSSDCommonController();

  /**
   * Returns the SSD Release Controller which handles all the SSD Release Generation DB Modules
   *
   * @return SSDReleaseGenerationController
   */
  SSDReleaseGenerationController getReleaseGenerationController();

}
