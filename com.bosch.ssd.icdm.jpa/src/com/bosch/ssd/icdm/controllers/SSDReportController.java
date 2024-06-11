/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.ssd.icdm.exception.ExceptionUtils;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException.SSDiCDMInterfaceErrorCodes;
import com.bosch.ssd.icdm.logger.SSDiCDMInterfaceLogger;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.CDRRulesWithFile;
import com.bosch.ssd.icdm.model.FeatureValueModel;
import com.bosch.ssd.icdm.model.ReleaseErrorModel;
import com.bosch.ssd.icdm.model.SSDMessage;
import com.bosch.ssd.icdm.model.SSDRelease;
import com.bosch.ssd.icdm.model.SoftwareVersion;
import com.bosch.ssd.icdm.service.internal.SSDReleaseReportService;
import com.bosch.ssd.icdm.service.internal.servinterface.SSDNodeInfoAccessor;
import com.bosch.ssd.icdm.service.utility.CreateSSDRelease;

/**
 * Controller Class for validating the input and invoking service methods in @SSDReleaseReportService
 *
 * @author SSN9COB
 */
public class SSDReportController {

  private final SSDNodeInfoAccessor ssdNodeInfo;
  /**
   *
   */
  private static final String INVALID_RELEASE_PRO_REL_ID_ID_GIVEN = "Invalid Release (PRO_REL_ID) id given";
  private SSDReleaseReportService releaseReportService;

  /**
   * @param ssdNodeInfo node Info
   */
  public SSDReportController(final SSDNodeInfoAccessor ssdNodeInfo) {
    this.ssdNodeInfo = ssdNodeInfo;
  }

  /**
   * @return the node info
   */
  public SSDNodeInfoAccessor getSSDNodeInfo() {
    return this.ssdNodeInfo;
  }

  /**
   * @return Release Service
   */
  public SSDReleaseReportService getReleaseReportService() {
    if (Objects.isNull(this.releaseReportService)) {
      createReleaseReportService();
    }
    return this.releaseReportService;
  }

  /**
   */
  private void createReleaseReportService() {
    this.releaseReportService = new SSDReleaseReportService(getSSDNodeInfo());
  }

  /**
   * GETRULESANDSSDFILE - Controller method to handle all validations and pass the input to service class
   *
   * @param labelNames list of labels
   * @param dependencies list of dependencies
   * @param ssdNodeId ssdnode id
   * @param ssdFilePath ssd output filepath
   * @return response model
   * @throws SSDiCDMInterfaceException any exception
   */
  public CDRRulesWithFile getRulesAndSSDFile(final List<String> labelNames, final List<FeatureValueModel> dependencies,
      final BigDecimal ssdNodeId, final String ssdFilePath)
      throws SSDiCDMInterfaceException {
    // validate the inputs
    validateInputForSSDFileOutput(labelNames, dependencies, ssdNodeId, ssdFilePath);

    // invoke service method
    CDRRulesWithFile rulesWithFile =
        getReleaseReportService().readRulesandGetSSDFileDpndyForNode(labelNames, dependencies, ssdNodeId, ssdFilePath);

    // Changes requested from iCDM - if no rules available for the provided inputs, set SSD Message accordingly
    if (rulesWithFile.getCdrRules().isEmpty() && Objects.isNull(rulesWithFile.getSsdFilePath())) {
      rulesWithFile.setSsdMessage(SSDMessage.NO_RULES_FETCHED);
      SSDiCDMInterfaceLogger.logMessage(SSDMessage.NO_RULES_FETCHED.getDescription(), ILoggerAdapter.LEVEL_DEBUG, null);
    }
    else {
      rulesWithFile.setSsdMessage(SSDMessage.RULE_AND_FILE_AVL);
    }

    return rulesWithFile;
  }

  /**
   * GETSSDFILE - Controller method to handle all validations and pass the input to service class
   *
   * @param labelNames list of labels
   * @param dependencies list of dependencies
   * @param ssdNodeId ssdnode id
   * @param ssdFilePath ssd output filepath
   * @return response model
   * @throws SSDiCDMInterfaceException any exception
   */
  public String getSSDFile(final List<String> labelNames, final List<FeatureValueModel> dependencies,
      final BigDecimal ssdNodeId, final String ssdFilePath)
      throws SSDiCDMInterfaceException {

    validateInputForSSDFileOutput(labelNames, dependencies, ssdNodeId, ssdFilePath);

    // invoke service method and return
    return getReleaseReportService().getSSDFileForDependencyForNode(labelNames, dependencies, ssdNodeId, ssdFilePath);
  }

  /**
   * getReleaseErrors - Controller method to handle all validations and pass the input to service class
   *
   * @param releaseUtils Uti;
   * @return list
   * @throws SSDiCDMInterfaceException exception
   */
  public List<ReleaseErrorModel> getReleaseErrors(final CreateSSDRelease releaseUtils)
      throws SSDiCDMInterfaceException {
    if (Objects.isNull(releaseUtils) || Objects.isNull(releaseUtils.getProReleaseId())) {
      throw ExceptionUtils.createAndThrowException(null, INVALID_RELEASE_PRO_REL_ID_ID_GIVEN,
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    return getReleaseReportService().getReleaseErrors(releaseUtils);
  }

  /**
   * getReleaseReportsForCompli - Controller method to handle all validations and pass the input to service class
   *
   * @param path path
   * @param ruleIdFlag flag
   * @param releaseUtils util
   * @return path
   * @throws SSDiCDMInterfaceException ex
   */
  public String getReleaseReportsForCompli(final String path, final boolean ruleIdFlag,
      final CreateSSDRelease releaseUtils)
      throws SSDiCDMInterfaceException {
    validateInputForReleaseReports(path, releaseUtils);
    return getReleaseReportService().getReleaseReportsForCompli(path, ruleIdFlag, releaseUtils);
  }

  /**
   * getReleaseReports - Controller method to handle all validations and pass the input to service class
   *
   * @param path path
   * @param ruleIdFlag flag
   * @param releaseUtils util
   * @return string[]
   * @throws SSDiCDMInterfaceException exception
   */
  public String[] getReleaseReports(final String path, final boolean ruleIdFlag, final CreateSSDRelease releaseUtils)
      throws SSDiCDMInterfaceException {
    validateInputForReleaseReports(path, releaseUtils);
    return getReleaseReportService().getReleaseReports(path, ruleIdFlag, releaseUtils);
  }

  /**
   * getCDRRuleForReleaseID - Controller method to handle all validations and pass the input to service class
   *
   * @param releaseId id
   * @param isCompliRelease is compli
   * @return list
   * @throws SSDiCDMInterfaceException exception
   */
  public List<CDRRule> readRuleForRelease(final BigDecimal releaseId, final boolean isCompliRelease)
      throws SSDiCDMInterfaceException {
    if (Objects.isNull(releaseId)) {
      throw ExceptionUtils.createAndThrowException(null, INVALID_RELEASE_PRO_REL_ID_ID_GIVEN,
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    return getReleaseReportService().getCDRRuleForReleaseID(releaseId, isCompliRelease);

  }

  /**
   * getReleaseReportsByReleaseId - Controller method to handle all validations and pass the input to service class
   *
   * @param path path
   * @param releaseId id
   * @param ruleIdFlag flag
   * @return String
   * @throws SSDiCDMInterfaceException exception
   */
  public String getReleaseReportsByReleaseId(final String path, final BigDecimal releaseId, final boolean ruleIdFlag)
      throws SSDiCDMInterfaceException {
    /*
     * Validate if output path is null
     */
    if (Objects.isNull(path)) {
      throw ExceptionUtils.createAndThrowException(null, "Invalid path specified. Please check",
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    /*
     * Validate if release id is specified
     */
    if (Objects.isNull(releaseId)) {
      throw ExceptionUtils.createAndThrowException(null, INVALID_RELEASE_PRO_REL_ID_ID_GIVEN,
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    return getReleaseReportService().getReleaseReportsByReleaseId(path, releaseId, ruleIdFlag);
  }

  /**
   * getSSDRelesesBySwVersionId - Controller method to handle all validations and pass the input to service class
   *
   * @param swVersionId swVersID
   * @return list
   * @throws SSDiCDMInterfaceException exception
   */
  public List<SSDRelease> getSSDRelesesBySwVersionId(final BigDecimal swVersionId) throws SSDiCDMInterfaceException {
    /*
     * Validate if SW Version ID is null
     */
    if (Objects.isNull(swVersionId)) {
      throw ExceptionUtils.createAndThrowException(null, "SW Version Id is not Set",
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    return getReleaseReportService().getSSDRelesesBySwVersionId(swVersionId);
  }


  /**
   * getSwVersionListBySwProjId - Controller method to handle all validations and pass the input to service class
   *
   * @param swProjNodeId id
   * @return list
   * @throws SSDiCDMInterfaceException exception
   */
  public List<SoftwareVersion> getSwVersionListBySwProjId(final long swProjNodeId) throws SSDiCDMInterfaceException {
    /*
     * Validate if SW Version ID is null
     */
    if (Objects.isNull(swProjNodeId)) {
      throw ExceptionUtils.createAndThrowException(null, "SW Project Node Id is not Set",
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    return getReleaseReportService().getSwVersionListBySwProjId(swProjNodeId);
  }

  /**
   * @return number of params
   */
  public long getNoOfParamsInRelease() {
    return getReleaseReportService().getNoOfParamsInRelease();
  }

  /**
   * @return caldata map
   */
  public Map<String, CalData> getCalDataMapForCdfx() {
    return getReleaseReportService().getCalDataMapForCdfx();
  }

  /**
   * @param labelNames
   * @param dependencies
   * @param ssdNodeId
   * @param ssdFilePath
   * @throws SSDiCDMInterfaceException
   */
  private void validateInputForSSDFileOutput(final List<String> labelNames, final List<FeatureValueModel> dependencies,
      final BigDecimal ssdNodeId, final String ssdFilePath)
      throws SSDiCDMInterfaceException {
    /*
     * Null or Empty parameters/labels list
     */
    if (Objects.isNull(labelNames) || labelNames.isEmpty()) {
      throw ExceptionUtils.createAndThrowException(null, "Label Names List cannot be null or empty",
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    /*
     * Null Dependencies list. (FV List can be empty)
     */
    if (Objects.isNull(dependencies)) {
      throw ExceptionUtils.createAndThrowException(null, "Dependencies List cannot be null",
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    /*
     * Null File Path for SSD File
     */
    if (Objects.isNull(ssdFilePath)) {
      throw ExceptionUtils.createAndThrowException(null, "SSD File Output location is not specified",
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    /*
     * Node Id is not set for the service call
     */
    if (Objects.isNull(ssdNodeId)) {
      throw ExceptionUtils.createAndThrowException(null, "Node Id is not set",
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
  }

  /**
   * @param path
   * @param releaseUtils
   * @throws SSDiCDMInterfaceException
   */
  private void validateInputForReleaseReports(final String path, final CreateSSDRelease releaseUtils)
      throws SSDiCDMInterfaceException {
    /*
     * Validate if output path is null
     */
    if (Objects.isNull(path)) {
      throw ExceptionUtils.createAndThrowException(null, "Invalid path specified. Please check",
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    /*
     * Validate if release id is specified
     */
    if (Objects.isNull(releaseUtils) || Objects.isNull(releaseUtils.getProReleaseId())) {
      throw ExceptionUtils.createAndThrowException(null, INVALID_RELEASE_PRO_REL_ID_ID_GIVEN,
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
  }
}
