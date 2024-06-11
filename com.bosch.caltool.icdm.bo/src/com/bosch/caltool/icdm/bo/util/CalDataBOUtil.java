/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldata.element.DataElement;
import com.bosch.calmodel.caldata.history.CalDataHistory;
import com.bosch.calmodel.caldata.history.HistoryEntry;
import com.bosch.caltool.icdm.bo.cdr.CDRRuleUtil;
import com.bosch.caltool.icdm.common.bo.a2l.RuleMaturityLevel;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;


/**
 * Utility methods related caldata
 *
 * @author mga1cob Icdm-521 Moved the class to common UI
 */
// TODO to merge with CalDataUtil in common.util plugin
// ICDM-226
public final class CalDataBOUtil {

  /**
   * Utility class. No instance required
   */
  private CalDataBOUtil() {
    // Utility class. No instance required
  }


  /**
   * Creates the CaldataHistory components for CDR rule using the given inputs, and adds to the given CalData object
   *
   * @param currentUserName username
   * @param calData caldata object
   * @param cdrRule rule instance
   * @param param parameter
   * @param paramAttrMap parameter's attribute dependencies
   * @param paramCollection ruleSet/cdrFunction object
   * @return caldata object set with history
   */
  public static CalData getCalDataHistoryDetails(final String currentUserName, final CalData calData,
      final ReviewRule cdrRule, final Parameter param, final Map<String, Set<Long>> paramAttrMap,
      final ParamCollection paramCollection) {
    DataElement dataElement;
    final CalDataHistory calDataHistory = new CalDataHistory();

    final List<HistoryEntry> historyEntryList = new ArrayList<>();
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
    String appendField = CommonUtils.checkNull(param.getParamHint());
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
      dataElement.setValue(CommonUtils.checkNull(param.getpClassText()));
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
    if (paramCollection == null) {
      dataElement.setValue("iCDM - CalDataReview");
    }
    else {
      dataElement.setValue(
          "iCDM - " + MODEL_TYPE.getTypeOfModel(paramCollection).getTypeName() + "(" + paramCollection.getName() + ")");
    }
    historyEntry.setDataIdentifier(dataElement);

    dataElement = getTargetVariant(cdrRule, param, paramAttrMap);

    historyEntry.setTargetVariant(dataElement);

    // Test object
    dataElement = new DataElement();
    dataElement.setValue("");
    historyEntry.setTestObject(dataElement);

    calData.setCalDataHistory(calDataHistory);
    return calData;
  }


  /**
   * @param rule
   * @param ruleSet
   * @return
   */
  private static DataElement getTargetVariant(final ReviewRule rule, final Parameter param,
      final Map<String, Set<Long>> paramAttrMap) {

    // Target Variant
    DataElement dataElement = new DataElement();
    String value = "";

    if ((null != rule) && !CommonUtils.isNullOrEmpty(paramAttrMap.get(param.getName()))) {
      value =
          rule.getDependencyList().isEmpty() ? "Default Rule" : CDRRuleUtil.getAttrValString(rule.getDependencyList());
    }

    dataElement.setValue(value);

    return dataElement;
  }

}
