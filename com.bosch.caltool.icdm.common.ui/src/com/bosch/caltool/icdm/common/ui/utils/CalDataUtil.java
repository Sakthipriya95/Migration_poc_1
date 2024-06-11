/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.bosch.calcomp.caldataanalyzer.vo.LabelInfoVO;
import com.bosch.calcomp.caldataanalyzer.vo.LabelValueInfoVO;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldata.element.DataElement;
import com.bosch.calmodel.caldata.history.CalDataHistory;
import com.bosch.calmodel.caldata.history.HistoryEntry;
import com.bosch.calmodel.caldataphy.CalDataPhy;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.client.bo.ss.CalDataType;
import com.bosch.caltool.icdm.common.bo.a2l.RuleMaturityLevel;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.ParameterRulesResponse;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.ws.rest.client.a2l.ParameterServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Utility methods related caldata
 *
 * @author mga1cob Icdm-521 Moved the class to common UI
 */
// TODO to merge with CalDataUtil in common.util plugin
// ICDM-226
public final class CalDataUtil {

  /**
   * Percentage Multiplier
   */
  private static final int PERCENTAGE_MULTIPLIER = 100;
  /**
   * Startup size for remark property in Cal data history entry
   */
  private static final int SB_CDHIST_REM_START_SIZE = 100;


  /**
   * Utility class. No instance required
   */
  private CalDataUtil() {
    // Utility class. No instance required
  }

  /**
   * Creates a new cal data object using the given series statistics input
   *
   * @param calDataPhy instance
   * @param seriesStatisticsType the statistics type of the value
   * @param labelInfo instance
   * @param lblValInfoVO instance
   * @param currentUserName defines current user name
   * @return CalData
   */
  public static CalData getCalData(final CalDataPhy calDataPhy, final CalDataType seriesStatisticsType,
      final LabelInfoVO labelInfo, final LabelValueInfoVO lblValInfoVO, final String currentUserName) {
    final CalData calData = new CalData();

    calData.setCalDataPhy(calDataPhy);
    calData.setShortName(calDataPhy.getName());

    getCalDataHistory(seriesStatisticsType, labelInfo, lblValInfoVO, currentUserName, calData);
    return calData;
  }

  /**
   * Creates the CaldataHistory components for series statistics using the given inputs, and adds to the given CalData
   * object
   *
   * @param seriesStatisticsType type of cal data
   * @param labelInfo labelInfo from series statistics
   * @param lblValInfoVO lblValInfoVO from series statistics
   * @param currentUserName current user
   * @param calData cal data object to be updated
   */
  public static void getCalDataHistory(final CalDataType seriesStatisticsType, final LabelInfoVO labelInfo,
      final LabelValueInfoVO lblValInfoVO, final String currentUserName, final CalData calData) {
    DataElement dataElement;
    final CalDataHistory calDataHistory = new CalDataHistory();

    final List<HistoryEntry> historyEntryList = new ArrayList<HistoryEntry>();
    calDataHistory.setHistoryEntryList(historyEntryList);

    final HistoryEntry historyEntry = new HistoryEntry();
    historyEntryList.add(historyEntry);

    // State
    dataElement = new DataElement();
    dataElement.setValue("prelimCalibrated");
    historyEntry.setState(dataElement);

    // Date - current date
    dataElement = new DataElement();
    SimpleDateFormat cdfxFormat =
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault(Locale.Category.FORMAT));
    dataElement.setValue(cdfxFormat.format(new Date()));
    historyEntry.setDate(dataElement);

    // User - current user
    dataElement = new DataElement();
    dataElement.setValue(currentUserName);
    historyEntry.setPerformedBy(dataElement);

    // Remark
    dataElement = new DataElement();

    StringBuilder remark = new StringBuilder(SB_CDHIST_REM_START_SIZE);

    remark.append(seriesStatisticsType.getLabel()).append(" from Series Statistics\n")
        .append(labelInfo.getSumDataSets()).append(" datasets using this parameter\nParameter has ")
        .append(labelInfo.getNumberOfValues()).append(" different values\n");

    // ICDM-266
    switch (seriesStatisticsType) {
      case PEAK:
        casePeak(labelInfo, remark);

        break;

      case VALUE:
        caseVal(labelInfo, lblValInfoVO, remark);

        break;

      default:
        break;
    }

    dataElement.setValue(remark.toString());
    historyEntry.setRemark(dataElement);

    // Context
    dataElement = new DataElement();
    dataElement.setValue("iCDM pre-calibration from SeriesStatistics");
    historyEntry.setContext(dataElement);

    calData.setCalDataHistory(calDataHistory);
  }

  /**
   * @param labelInfo
   * @param lblValInfoVO
   * @param remark
   */
  private static void caseVal(final LabelInfoVO labelInfo, final LabelValueInfoVO lblValInfoVO,
      final StringBuilder remark) {
    remark.append(lblValInfoVO.getFileIDList().size()).append(" datasets using this value (")
        .append((lblValInfoVO.getFileIDList().size() * PERCENTAGE_MULTIPLIER) / labelInfo.getSumDataSets())
        .append(" %)\n");
  }

  /**
   * @param labelInfo
   * @param remark
   */
  private static void casePeak(final LabelInfoVO labelInfo, final StringBuilder remark) {
    remark.append(labelInfo.getValuesMap().get(labelInfo.getPeakValueId()).getFileIDList().size())
        .append(" datasets using the most frequent value (").append(labelInfo.getPeakValuePercentage()).append(" %)\n");
  }


  /**
   * Creates the CaldataHistory components for CDR rule using the given inputs, and adds to the given CalData object
   *
   * @param currentUserName username
   * @param calData caldata object
   * @param appendField string to be appended
   * @param cdrRule rule instance
   * @param paramsCollection ruleSet/cdrFunction object
   * @return caldata object set with history
   */
  public static CalData getCalDataHistoryDetails(final String currentUserName, final CalData calData,
      final String appendField, final ReviewRule cdrRule, final ParamCollection paramsCollection) {
    DataElement dataElement;
    final CalDataHistory calDataHistory = new CalDataHistory();

    final List<HistoryEntry> historyEntryList = new ArrayList<HistoryEntry>();
    calDataHistory.setHistoryEntryList(historyEntryList);

    final HistoryEntry historyEntry = new HistoryEntry();
    historyEntryList.add(historyEntry);

    // State
    dataElement = new DataElement();

    if (null == cdrRule) {
      // Default is prelimCalibrated
      dataElement.setValue("prelimCalibrated");
    }
    else {
      dataElement.setValue(RuleMaturityLevel.getIcdmMaturityLvlFromImportFileTxt(cdrRule.getMaturityLevel()));
    }
    historyEntry.setState(dataElement);

    // Date - current date
    dataElement = new DataElement();
    SimpleDateFormat cdfxFormat =
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault(Locale.Category.FORMAT));
    dataElement.setValue(cdfxFormat.format(new Date()));
    historyEntry.setDate(dataElement);

    // User name - current user
    dataElement = new DataElement();
    dataElement.setValue(currentUserName);
    historyEntry.setPerformedBy(dataElement);

    // Remark
    dataElement = new DataElement();

    StringBuilder remark = new StringBuilder();
    if (null == cdrRule) {
      remark.append(appendField).append(" from iCDM Calibration Data Review rule.");
    }
    else {
      // If rule is given, add the hint of rule to the append field in remarks
      if (!CommonUtils.isEmptyString(appendField)) {
        remark.append(appendField).append("\n\n");
      }
      remark.append(null == cdrRule.getHint() ? "" : cdrRule.getHint());
    }
    dataElement.setValue(remark.toString());
    historyEntry.setRemark(dataElement);

    // Context
    dataElement = new DataElement();
    if ((null == cdrRule) || (null == cdrRule.getParameterName())) {
      dataElement.setValue("iCDM " + appendField + " from Calibration Data Review");
    }
    else {
      IParameter param = getParameter(cdrRule);

      dataElement.setValue(null == param.getpClassText() ? "" : param.getpClassText());
    }
    historyEntry.setContext(dataElement);

    // Program Identifier
    dataElement = new DataElement();
    dataElement.setValue("");
    historyEntry.setProgramIdentifier(dataElement);

    // Project
    dataElement = new DataElement();
    dataElement.setValue("");
    historyEntry.setProject(dataElement);

    // Data Identifier
    dataElement = new DataElement();
    if (paramsCollection == null) {
      dataElement.setValue("iCDM - CalDataReview");
    }
    else {
      // paramsCollection.getObjectTypeName()
      dataElement.setValue("iCDM - " + paramsCollection.getDescription() + "(" + paramsCollection.getName() + ")");
    }
    historyEntry.setDataIdentifier(dataElement);

    dataElement = getTargetVariant(cdrRule);

    historyEntry.setTargetVariant(dataElement);

    // Test object
    dataElement = new DataElement();
    dataElement.setValue("");
    historyEntry.setTestObject(dataElement);

    calData.setCalDataHistory(calDataHistory);
    return calData;
  }

  /**
   * @param cdrRule
   * @param paramsCollection
   * @return
   */
  private static IParameter getParameter(final ReviewRule cdrRule) {
    IParameter param = null;
    ParameterDataProvider<IParameterAttribute, IParameter> parameterDataProvider = new ParameterDataProvider<>(null);
    try {
      param = parameterDataProvider.getParamByName(cdrRule.getParameterName(), cdrRule.getValueType());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return param;
  }

  /**
   * @param cdrRule
   * @return
   */
  private static DataElement getTargetVariant(final ReviewRule cdrRule) {

    // Target Variant
    DataElement dataElement = new DataElement();
    if (null == cdrRule) {
      dataElement.setValue("");
    }
    else {
      ParameterDataProvider<IParameterAttribute, IParameter> parameterDataProvider = new ParameterDataProvider<>(null);
      IParameter cdrFuncParameter = getParameter(cdrRule);
      ParameterServiceClient parameterServiceClient = new ParameterServiceClient();
      ParameterRulesResponse singleParamRules = null;
      try {
        singleParamRules =
            parameterServiceClient.getSingleParamRules(cdrFuncParameter.getName(), cdrFuncParameter.getType());
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
      if ((null != singleParamRules) && !singleParamRules.getAttrMap().get(cdrFuncParameter.getName()).isEmpty() &&
          cdrRule.getDependencyList().isEmpty()) {
        dataElement.setValue("Default Rule");
      }
      else if ((null != singleParamRules) && !singleParamRules.getAttrMap().get(cdrRule.getParameterName()).isEmpty() &&
          !cdrRule.getDependencyList().isEmpty()) {
        dataElement.setValue(parameterDataProvider.getAttrValString(cdrRule));
      }
      else {
        dataElement.setValue("");
      }

    }
    return dataElement;
  }


}
