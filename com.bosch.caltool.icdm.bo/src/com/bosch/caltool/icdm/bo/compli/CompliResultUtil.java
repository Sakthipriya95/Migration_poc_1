/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.compli;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.bo.cdr.CheckSSDResultParam;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.COMPLI_RESULT_FLAG;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.QSSD_RESULT_FLAG;
import com.bosch.caltool.icdm.model.cdr.CompliCheckSSDInputModel;
import com.bosch.caltool.icdm.model.cdr.CompliReviewOutputData;
import com.bosch.checkssd.reports.reportMessage.ReportMessages;
import com.bosch.checkssd.reports.reportmodel.ReportModel;
import com.bosch.ssd.icdm.model.CDRRule;

/**
 * @author gge6cob
 */
public class CompliResultUtil {

  private static CompliResultUtil instance;

  /**
   * @return the instance
   */
  public static CompliResultUtil getInstance() {
    if (instance == null) {
      instance = new CompliResultUtil();
    }
    return instance;
  }

  /**
   * Gets the compli result.
   *
   * @param compliRule the compli rule
   * @param compliSSDResParam the compli SSD res param
   * @param errorInSSD errorInSSD
   * @param param param name
   * @param isCompliance isCompliance
   * @param isParamSkipped param skipped due to if condition
   * @return the compli result
   */
  public static COMPLI_RESULT_FLAG getCompliResult(final CDRRule compliRule,
      final CheckSSDResultParam compliSSDResParam, final boolean errorInSSD, final String param,
      final boolean isCompliance, final boolean isParamSkipped) {
    COMPLI_RESULT_FLAG result = CDRConstants.COMPLI_RESULT_FLAG.NO_RULE;
    // if param skipped due to if condition , result is set to ok
    if (isParamSkipped) {
      result = CDRConstants.COMPLI_RESULT_FLAG.OK;
    }
    else if (compliRule == null) {
      if ((ApicUtil.isVariantCoded(param) || !isCompliance) && (compliSSDResParam != null) &&
          (compliSSDResParam.getCompliReportModel() != null)) {
        result = getResultBasedOnReportModel(compliSSDResParam, result, compliSSDResParam.getCompliReportModel());
      }
      else {
        // Error : compli rule not available. Evaluation is mandatory.
        result = CDRConstants.COMPLI_RESULT_FLAG.NO_RULE;
      }
    }
    else {
      // Compli rule is condition. Not relevant. Not evaluated
      if ((compliSSDResParam == null) || (compliSSDResParam.getCompliReportModel() == null)) {
        if (!errorInSSD) {
          result = CDRConstants.COMPLI_RESULT_FLAG.OK;
        }
      }
      else {
        ReportModel reportModel = compliSSDResParam.getCompliReportModel();
        result = getResultBasedOnReportModel(compliSSDResParam, result, reportModel);
      }
    }
    return result;
  }

  /**
   * Gets the compli result.
   *
   * @param qssdRule the compli rule
   * @param compliSSDResParam the compli SSD res param
   * @param errorInSSD errorInSSD
   * @param param param name
   * @param isCompliance isCompliance
   * @param isParamSkipped param skipped due to if condition
   * @param isQssd
   * @return the compli result
   */
  public static QSSD_RESULT_FLAG getQssdResult(final CDRRule qssdRule, final CheckSSDResultParam compliSSDResParam,
      final boolean errorInSSD, final String param, final boolean isQssd) {
    QSSD_RESULT_FLAG result = CDRConstants.QSSD_RESULT_FLAG.NO_RULE;
    // if param skipped due to if condition , result is set to ok
    if (qssdRule == null) {
      if ((ApicUtil.isVariantCoded(param) || !isQssd) && (compliSSDResParam != null) &&
          (compliSSDResParam.getQssdReportModel() != null)) {
        result = getQssdResultBasedOnReportModel(result, compliSSDResParam.getQssdReportModel());
      }
      else {
        // Error : compli rule not available. Evaluation is mandatory.
        result = CDRConstants.QSSD_RESULT_FLAG.NO_RULE;
      }
    }
    else {
      // Compli rule is condition. Not relevant. Not evaluated
      if ((compliSSDResParam == null) || (compliSSDResParam.getQssdReportModel() == null)) {
        if (!errorInSSD) {
          result = CDRConstants.QSSD_RESULT_FLAG.OK;
        }
      }
      else {
        ReportModel reportModel = compliSSDResParam.getQssdReportModel();
        result = getQssdResultBasedOnReportModel(result, reportModel);
      }
    }
    return result;
  }

  /**
   * @param compliSSDResParam
   * @param result
   * @param reportModel
   * @return
   */
  public static COMPLI_RESULT_FLAG getResultBasedOnReportModel(final CheckSSDResultParam compliSSDResParam,
      final COMPLI_RESULT_FLAG result, final ReportModel reportModel) {
    COMPLI_RESULT_FLAG compliResult = result;
    if (isResultOk(reportModel)) {
      // Compli rule evaluated, and found ok.
      compliResult = CDRConstants.COMPLI_RESULT_FLAG.OK;
    }
    else {
      // Compli rule evaluated. Found not OK
      if (compliSSDResParam.getCompliReportModel().getUseCaseInfo()
          .equalsIgnoreCase(CDRConstants.COMPLI_RESULT_FLAG.CSSD.getUiType())) {
        compliResult = CDRConstants.COMPLI_RESULT_FLAG.CSSD;
      }
      else if (compliSSDResParam.getCompliReportModel().getUseCaseInfo()
          .equalsIgnoreCase(CDRConstants.COMPLI_RESULT_FLAG.SSD2RV.getUiType())) {
        compliResult = CDRConstants.COMPLI_RESULT_FLAG.SSD2RV;
      }
    }
    return compliResult;
  }

  /**
   * @param result
   * @param reportModel
   * @return
   */
  private static QSSD_RESULT_FLAG getQssdResultBasedOnReportModel(final QSSD_RESULT_FLAG result,
      final ReportModel reportModel) {
    QSSD_RESULT_FLAG compliResult = result;
    if (isResultOk(reportModel)) {
      // Compli rule evaluated, and found ok.
      compliResult = CDRConstants.QSSD_RESULT_FLAG.OK;
    }
    else {
      // Compli rule evaluated. Found not OK
      if (reportModel.getUseCaseInfo().equalsIgnoreCase(CDRConstants.QSSD_RESULT_FLAG.QSSD.getUiType())) {
        compliResult = CDRConstants.QSSD_RESULT_FLAG.QSSD;
      }
    }
    return compliResult;
  }

  /**
   * verifies whether the checkssd has reported the result as ok or not
   *
   * @param reportModel checkssd report model
   * @return true if status is ok in report model
   */
  public static boolean isResultOk(final ReportModel reportModel) {
    return ReportMessages.STATUS_OK.equals(reportModel.getStatus());
  }

  /**
   * @param calDataMap calDataMap
   * @param parserWarningsMap warnings map from hex
   * @param a2lFileContents a2lFileContents
   * @param ssdFilePath ssdFilePath
   * @param systemUserTempDirPath systemUserTempDirPath
   * @param paramSet paramSet
   * @param hexFileName hex name
   * @param dataFileOption dataFileOption
   * @return the CompliCheckSSDInput CompliCheckSSDInput
   */
  public CompliCheckSSDInput createcompliInput(final Map<String, CalData> calDataMap,
      final Map<String, List<String>> parserWarningsMap, final A2LFileInfo a2lFileContents, final String ssdFilePath,
      final String systemUserTempDirPath, final Set<String> paramSet,
      final CompliCheckSSDInputModel checkSSDInputModel) {

    CompliCheckSSDInput input = new CompliCheckSSDInput();
    input.setA2lFileContents(a2lFileContents);
    input.setCalDataMap(calDataMap);
    input.setParserWarningsMap(parserWarningsMap);
    input.setHexFileName(checkSSDInputModel.getHexFileName());
    input.setParamSet(paramSet);
    input.setServerOrTempPath(systemUserTempDirPath);
    input.setSsdFilePath(ssdFilePath);
    input.setDataFileOption(checkSSDInputModel.getDatafileoption());
    input.setPredecessorCheck(checkSSDInputModel.isPredecessorCheck());
    input.setPidcA2l(checkSSDInputModel.getPidcA2l());
    input.setVariantId(checkSSDInputModel.getVariantId());
    input.setHexFilePidcElement(checkSSDInputModel.getHexFilePidcElement());

    return input;
  }


  /**
   * @param outputData outputData
   * @param resultType resultType
   * @return the compli/qssd Params Count
   */
  private int getCompliParamCount(final CompliReviewOutputData outputData, final String resultType) {
    int total = 0;
    Map<String, String> resultMap = outputData.getCompliResult();
    for (String result : resultMap.values()) {
      if (result.equals(resultType)) {
        total++;
      }
    }
    return total;
  }


  /**
   * @param outputData
   * @param resultType
   * @return
   */
  private int getQssdParamCount(final CompliReviewOutputData outputData, final String resultType) {
    int total = 0;
    Map<String, String> resultMap = outputData.getQssdResult();
    for (String result : resultMap.values()) {
      if (result.equals(resultType)) {
        total++;
      }
    }
    return total;
  }


  /**
   * @param outputData
   * @param isQssd
   */
  public void fillCompliMetrics(final CompliReviewOutputData outputData) {
    outputData.setCompliCount(outputData.getCompliResult().size());
    outputData.setCompliPassCount(getCompliParamCount(outputData, CDRConstants.COMPLI_RESULT_FLAG.OK.getUiType()));
    outputData.setCssdFailCount(getCompliParamCount(outputData, CDRConstants.COMPLI_RESULT_FLAG.CSSD.getUiType()));
    outputData.setSsd2RvFailCount(getCompliParamCount(outputData, CDRConstants.COMPLI_RESULT_FLAG.SSD2RV.getUiType()));
    outputData.setNoRuleCount(getCompliParamCount(outputData, CDRConstants.COMPLI_RESULT_FLAG.NO_RULE.getUiType()));
    outputData.setTotalFailCount(outputData.getNoRuleCount() + outputData.getSsd2RvFailCount() +
        outputData.getCssdFailCount() + outputData.getQssdTotalFailCount());

  }

  public void fillQssdMetrics(final CompliReviewOutputData outputData) {
    outputData.setQssdCount(outputData.getQssdResult().size());
    outputData.setQssdFailCount(getQssdParamCount(outputData, CDRConstants.QSSD_RESULT_FLAG.QSSD.getUiType()));
    outputData.setQssdPassCount(getQssdParamCount(outputData, CDRConstants.QSSD_RESULT_FLAG.OK.getUiType()));
    outputData.setQssdNoRuleCount(getQssdParamCount(outputData, CDRConstants.QSSD_RESULT_FLAG.NO_RULE.getUiType()));
    outputData.setQssdTotalFailCount(outputData.getQssdFailCount() + outputData.getQssdNoRuleCount());
  }


}
