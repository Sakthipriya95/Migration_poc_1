/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.service.internal;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.bosch.ssd.icdm.model.SSDRelease;
import com.bosch.ssd.icdm.model.SoftwareVersion;
import com.bosch.ssd.icdm.model.utils.DBToModelUtil;
import com.bosch.ssd.icdm.service.internal.servinterface.SSDNodeInfoAccessor;
import com.bosch.ssd.icdm.service.internal.servinterface.SSDServiceMethod;
import com.bosch.ssd.icdm.service.utility.CreateSSDFile;
import com.bosch.ssd.icdm.service.utility.CreateSSDRelease;
import com.bosch.ssd.icdm.service.utility.DBQueryUtils;
import com.bosch.ssd.icdm.service.utility.ReleaseReport;
import com.bosch.ssd.icdm.service.utility.SSDReviewRulesUtil;
import com.bosch.ssd.icdm.service.utility.ServiceLogAndTransactionUtil;

/**
 * Service Class to handle all service calls related to SSD Release Report module
 *
 * @author SSN9COB
 */
public class SSDReleaseReportService implements SSDServiceMethod {

  /**
   *
   */
  private static final String DELIMITER = "\\";
  private final SSDNodeInfoAccessor ssdNodeInfo;
  private final DBQueryUtils dbQueryUtils;
  private final ReleaseReport reportUtils;
  private final CreateSSDFile createSSDFile;
  private final SSDReviewRulesUtil reviewRulesUtil;

  /**
   * Constructor for SSD Release Service
   *
   * @param ssdNodeInfo Node Info
   */
  public SSDReleaseReportService(final SSDNodeInfoAccessor ssdNodeInfo) {
    this.ssdNodeInfo = ssdNodeInfo;
    this.dbQueryUtils = ssdNodeInfo.getDbQueryUtils();
    this.reportUtils = new ReleaseReport(this.dbQueryUtils);
    this.createSSDFile = new CreateSSDFile(this.dbQueryUtils);
    this.reviewRulesUtil = new SSDReviewRulesUtil(this.dbQueryUtils);
  }

  /**
   * @return the reviewRulesUtil
   */
  public SSDReviewRulesUtil getReviewRulesUtil() {
    return this.reviewRulesUtil;
  }

  /**
   * Get Releases for the SW Version
   *
   * @param swVersionId version id
   * @return list of releases
   * @throws SSDiCDMInterfaceException exception
   */
  public List<SSDRelease> getSSDRelesesBySwVersionId(final BigDecimal swVersionId) throws SSDiCDMInterfaceException {
    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReleaseReportService.class.getSimpleName(),
        getCurrentMethodName(), true, this.dbQueryUtils.getEntityManager(), false);
    List<SSDRelease> ssdRelList = new ArrayList<>();
    try {
      if (Objects.nonNull(swVersionId)) {
        List<Object[]> dbList = this.dbQueryUtils.getReleaseBySWVersion(swVersionId);
        if (Objects.nonNull(dbList) && !dbList.isEmpty()) {
          for (Object[] object : dbList) {
            SSDRelease ssdRelease = DBToModelUtil.convertToSSDRelease(object);
            // setFeatureValuesforSSDReleseByReleaseId
            ssdRelease.setDependencyList(getFeatureValuesByReleaseId(ssdRelease.getReleaseId()));
            ssdRelList.add(ssdRelease);
          }
        }
      }
    }
    catch (SSDiCDMInterfaceException e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getDetailedErrorMessage(), e.getErrorCode(),
          this.dbQueryUtils.getEntityManager(), false);
    }
    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReleaseReportService.class.getSimpleName(),
        getCurrentMethodName(), false, this.dbQueryUtils.getEntityManager(), true);
    return ssdRelList;
  }

  /**
   * Get SW Version List from SW Proj ID
   *
   * @param swProjNodeId nodei id
   * @return list of SW Version
   * @throws SSDiCDMInterfaceException Exception
   */
  public List<SoftwareVersion> getSwVersionListBySwProjId(final long swProjNodeId) throws SSDiCDMInterfaceException {
    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReleaseReportService.class.getSimpleName(),
        getCurrentMethodName(), true, this.dbQueryUtils.getEntityManager(), false);
    List<SoftwareVersion> swVersionList = new ArrayList<>();
    try {
      List<Object[]> swVersQueryList = this.dbQueryUtils.getSWVersionListBySwProjId(swProjNodeId);
      for (Object[] object : swVersQueryList) {
        swVersionList.add(DBToModelUtil.convertToSoftwareVersionModel(object, new BigDecimal(swProjNodeId)));
      }
    }
    catch (SSDiCDMInterfaceException e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getDetailedErrorMessage(), e.getErrorCode(),
          this.dbQueryUtils.getEntityManager(), false);
    }
    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReleaseReportService.class.getSimpleName(),
        getCurrentMethodName(), false, this.dbQueryUtils.getEntityManager(), false);
    return swVersionList;
  }

  /**
   * Common method for GetRuleForRelease & GetRuleForCompliRelease based on the the boolean flag
   *
   * @param releaseId id
   * @param isCompliRelease iscompli
   * @return list
   * @throws SSDiCDMInterfaceException exception
   */
  public List<CDRRule> getCDRRuleForReleaseID(final BigDecimal releaseId, final boolean isCompliRelease)
      throws SSDiCDMInterfaceException {
    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReleaseReportService.class.getSimpleName(),
        getCurrentMethodName(), true, this.dbQueryUtils.getEntityManager(), false);
    List<CDRRule> cdrRulesList = new ArrayList<>();
    try {
      // Check if release id is not null
      if (Objects.nonNull(releaseId)) {
        List<Object[]> dbList = this.dbQueryUtils.getRulesForRelease(releaseId, isCompliRelease);
        if (Objects.nonNull(dbList) && !dbList.isEmpty()) {
          for (Object[] obj : dbList) {
            // Read the result set mapping and create CDR Rule Object
            cdrRulesList.add(DBToModelUtil.convertToCDRRule(obj, false));
          }
        }
      }
    }
    catch (SSDiCDMInterfaceException e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getDetailedErrorMessage(), e.getErrorCode(),
          this.dbQueryUtils.getEntityManager(), false);
    }
    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReleaseReportService.class.getSimpleName(),
        getCurrentMethodName(), false, this.dbQueryUtils.getEntityManager(), false);
    return cdrRulesList;
  }

  /**
   * Get the feature values associated with the release id
   *
   * @return feature value
   * @throws SSDiCDMInterfaceException Exception
   */
  private List<FeatureValueModel> getFeatureValuesByReleaseId(final BigDecimal releaseId)
      throws SSDiCDMInterfaceException {
    List<FeatureValueModel> fvModelList = new ArrayList<>();
    if (Objects.nonNull(releaseId)) {
      List<Object[]> dbList = this.dbQueryUtils.getFeaValForRelease(releaseId);
      if (Objects.nonNull(dbList) && !dbList.isEmpty()) {
        for (Object[] object : dbList) {
          fvModelList.add(DBToModelUtil.convertToFeatureValueModelModel(object));
        }
      }
    }
    return fvModelList;
  }

  /**
   * Method generates the ssd and cdfx file for the release
   *
   * @param path - path of the release reports
   * @param ruleIdFlag rule flag
   * @param releaseUtils CreateSSDReleaseUtils
   * @return array
   * @throws SSDiCDMInterfaceException exception
   */
  public String[] getReleaseReports(final String path, final boolean ruleIdFlag, final CreateSSDRelease releaseUtils)
      throws SSDiCDMInterfaceException {
    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReleaseReportService.class.getSimpleName(),
        getCurrentMethodName(), true, this.dbQueryUtils.getEntityManager(), false);
    String[] reportsFilePaths = new String[2];
    try {
      String reportName = path.substring(path.lastIndexOf('\\') + 1).trim();
      String pathName = path.substring(0, path.lastIndexOf('\\', path.lastIndexOf('\\'))) + DELIMITER;


      String ssdFilePath = this.reportUtils.generateSSDFile(releaseUtils.getProReleaseId(), pathName, reportName,
          ruleIdFlag, this.createSSDFile);
      reportsFilePaths[0] = ssdFilePath;
      String cdfxFilePath = this.reportUtils.generateCDFxFile(releaseUtils.getProReleaseId(), pathName, reportName,
          this.ssdNodeInfo.getUserName());
      reportsFilePaths[1] = cdfxFilePath;
      SSDiCDMInterfaceLogger.logMessage("SSD Report : " + ssdFilePath, ILoggerAdapter.LEVEL_DEBUG, null);
      SSDiCDMInterfaceLogger.logMessage("CDFx Report : " + cdfxFilePath, ILoggerAdapter.LEVEL_DEBUG, null);
    }
    catch (SSDiCDMInterfaceException e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getDetailedErrorMessage(), e.getErrorCode(),
          this.dbQueryUtils.getEntityManager(), false);
    }
    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReleaseReportService.class.getSimpleName(),
        getCurrentMethodName(), false, this.dbQueryUtils.getEntityManager(), false);
    return reportsFilePaths;
  }

  /**
   * Method generates the ssd file for the compli release
   *
   * @param path path
   * @param ruleIdFlag flag
   * @param releaseUtils CreateSSDReleaseUtils
   * @return String
   * @throws SSDiCDMInterfaceException exception
   */
  public String getReleaseReportsForCompli(final String path, final boolean ruleIdFlag,
      final CreateSSDRelease releaseUtils)
      throws SSDiCDMInterfaceException {
    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReleaseReportService.class.getSimpleName(),
        getCurrentMethodName(), true, this.dbQueryUtils.getEntityManager(), false);
    try {
      if (Objects.nonNull(path)) {
        String reportName = path.substring(path.lastIndexOf('\\') + 1).trim();
        String pathName = path.substring(0, path.lastIndexOf('\\', path.lastIndexOf('\\'))) + DELIMITER;
        String ssdFilePath = this.reportUtils.generateSSDFile(releaseUtils.getProReleaseId(), pathName, reportName,
            ruleIdFlag, this.createSSDFile);
        ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReleaseReportService.class.getSimpleName(),
            getCurrentMethodName(), false, this.dbQueryUtils.getEntityManager(), false);
        return ssdFilePath;
      }
      SSDiCDMInterfaceLogger.logMessage("Invalid path specified. Please check", ILoggerAdapter.LEVEL_DEBUG, null);
      throw ExceptionUtils.createAndThrowException(null, "Invalid path specified. Please check",
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION, true);
    }
    catch (SSDiCDMInterfaceException e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getDetailedErrorMessage(), e.getErrorCode(),
          this.dbQueryUtils.getEntityManager(), false);
    }
  }

  /**
   * Method generates the list of errors for the release
   *
   * @param releaseUtils CreateSSDReleaseUtils
   * @return - list of {@link ReleaseErrorModel} - error details
   * @throws SSDiCDMInterfaceException Exception
   */
  public List<ReleaseErrorModel> getReleaseErrors(final CreateSSDRelease releaseUtils)
      throws SSDiCDMInterfaceException {
    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReleaseReportService.class.getSimpleName(),
        getCurrentMethodName(), true, this.dbQueryUtils.getEntityManager(), false);
    try {
      List<ReleaseErrorModel> errorRuleList = this.reportUtils.generateErrorFile(releaseUtils.getProRevId(),
          releaseUtils.getProReleaseId(), this.ssdNodeInfo.getCompliNodeId());
      // ALM-261507
      for (ReleaseErrorModel errorModel : new ArrayList<ReleaseErrorModel>(errorRuleList)) {
        // TODO - Coverage improvement in Test case - to check 6/17 code details
        if ((errorModel.getErrorNo().intValue() == 6) || (errorModel.getErrorNo().intValue() == 17)) {
          errorRuleList.remove(errorModel);
        }
      }
      ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReleaseReportService.class.getSimpleName(),
          getCurrentMethodName(), false, this.dbQueryUtils.getEntityManager(), false);
      return errorRuleList;
    }
    catch (SSDiCDMInterfaceException e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getDetailedErrorMessage(), e.getErrorCode(),
          this.dbQueryUtils.getEntityManager(), false);
    }
  }

  /**
   * Release reports by Release ID
   *
   * @param path path
   * @param releaseId releaseid
   * @param ruleIdFlag flag
   * @return String
   * @throws SSDiCDMInterfaceException Exception
   */
  public String getReleaseReportsByReleaseId(final String path, final BigDecimal releaseId, final boolean ruleIdFlag)
      throws SSDiCDMInterfaceException {
    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReleaseReportService.class.getSimpleName(),
        getCurrentMethodName(), true, this.dbQueryUtils.getEntityManager(), false);
    try {
      String reportName = path.substring(path.lastIndexOf('\\') + 1).trim();
      String pathName = path.substring(0, path.lastIndexOf('\\', path.lastIndexOf('\\'))) + DELIMITER;
      String ssdFilePath =
          this.reportUtils.generateSSDFile(releaseId, pathName, reportName, ruleIdFlag, this.createSSDFile);
      ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReleaseReportService.class.getSimpleName(),
          getCurrentMethodName(), false, this.dbQueryUtils.getEntityManager(), true);
      return ssdFilePath;
    }
    catch (SSDiCDMInterfaceException e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getDetailedErrorMessage(), e.getErrorCode(),
          this.dbQueryUtils.getEntityManager(), false);
    }
  }


  /**
   * SSDFile for dependency node
   *
   * @param labelNames label name
   * @param dependencies dependency
   * @param ssdNodeId ssd node id
   * @param ssdFilePath ssd file path
   * @return ssd file
   * @throws SSDiCDMInterfaceException Exception
   */
  public String getSSDFileForDependencyForNode(final List<String> labelNames,
      final List<FeatureValueModel> dependencies, final BigDecimal ssdNodeId, final String ssdFilePath)
      throws SSDiCDMInterfaceException {
    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReleaseReportService.class.getSimpleName(),
        getCurrentMethodName(), true, this.dbQueryUtils.getEntityManager(), false);
    try {
      // SSD-333
      this.reviewRulesUtil.readReviewRuleForDependency(labelNames, dependencies, ssdNodeId);
      String ssdFileLoc =
          this.createSSDFile.createSSDFileDependency(ssdNodeId, this.ssdNodeInfo.getUserName(), ssdFilePath);

      ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReleaseReportService.class.getSimpleName(),
          getCurrentMethodName(), false, this.dbQueryUtils.getEntityManager(), true);
      return ssdFileLoc;
    }
    catch (SSDiCDMInterfaceException e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getDetailedErrorMessage(), e.getErrorCode(),
          this.dbQueryUtils.getEntityManager(), false);
    }
  }


  /**
   * read rule method get ssd file dependency for node
   *
   * @param labelNames labelname
   * @param dependencies dependency
   * @param ssdNodeId ssd node id
   * @param ssdFilePath file path
   * @return ssdfile
   * @throws SSDiCDMInterfaceException exception
   */
  public CDRRulesWithFile readRulesandGetSSDFileDpndyForNode(final List<String> labelNames,
      final List<FeatureValueModel> dependencies, final BigDecimal ssdNodeId, final String ssdFilePath)
      throws SSDiCDMInterfaceException {
    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReleaseReportService.class.getSimpleName(),
        getCurrentMethodName(), true, this.dbQueryUtils.getEntityManager(), false);
    try {
      // Response Model
      CDRRulesWithFile ruleFileModel = new CDRRulesWithFile();
      // since this is invoked from Review Rules module, set the flag to true
      this.reviewRulesUtil.setCallFromReview(true);

      ruleFileModel.setCdrRules(this.reviewRulesUtil.getSSDRules(labelNames, dependencies, ssdNodeId));

      // delete & add only valid Labels in TEMPLABELLISTINTERFACE - for same set of rules in SSD File as in MAP
//      Set<String> labels = ruleFileModel.getCdrRules().keySet()
      List<String> validRuleParameters = this.reviewRulesUtil.getValidRuleParameters(ruleFileModel.getCdrRules());
      this.reviewRulesUtil.insertLabelsinTmpTbl(validRuleParameters, ssdNodeId);
      // List of label names that are valid and matching with the EXACT MATCH scenarios for review rule
      ruleFileModel.setValidRuleParameters(validRuleParameters);
      ruleFileModel.setSsdFilePath(
          this.createSSDFile.createSSDFileDependency(ssdNodeId, this.ssdNodeInfo.getUserName(), ssdFilePath));

      // set it false
      this.reviewRulesUtil.setCallFromReview(false);

      ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReleaseReportService.class.getSimpleName(),
          getCurrentMethodName(), false, this.dbQueryUtils.getEntityManager(), true);
      return ruleFileModel;
    }
    catch (SSDiCDMInterfaceException e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getDetailedErrorMessage(), e.getErrorCode(),
          this.dbQueryUtils.getEntityManager(), false);
    }
  }

  /**
   * Get total params in release
   *
   * @return number
   */
  public long getNoOfParamsInRelease() {
    return this.reportUtils.getNoOfParams();
  }

  /**
   * Get caldata map for CDFX
   *
   * @return map
   */
  public Map<String, CalData> getCalDataMapForCdfx() {
    return this.reportUtils.getCalDataMap();
  }
}
