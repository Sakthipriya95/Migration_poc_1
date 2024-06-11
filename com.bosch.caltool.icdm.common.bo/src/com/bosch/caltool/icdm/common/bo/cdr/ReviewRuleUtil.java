/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.bo.cdr;

import java.math.BigDecimal;

import com.bosch.calcomp.caldataphy2dcm.dcmwrite.DcmFromCalData;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;

/**
 * @author bne4cob
 */
public final class ReviewRuleUtil {

  private ReviewRuleUtil() {
    // Private constructor prevent instance creation
  }

  /**
   * Rule is a default rule, if there are no dependencies attached to it, but the parameter has dependent attributes
   *
   * @param rule Review Rule
   * @return true, if the input is a default rule
   */
  public static boolean isDefaultRule(final ReviewRule rule) {
    return rule.getDependencyList().isEmpty();
  }


  /**
   * @param rule rule
   * @return true if rule is valid
   */
  public static boolean isRuleValid(final ReviewRule rule) {
    return (rule != null) && (rule.getReviewMethod() != null);
  }

  /**
   * @param rule rule
   * @return true if ready for series is true
   */
  public static boolean isReadyForSeries(final ReviewRule rule) {
    return (rule != null) && ApicConstants.READY_FOR_SERIES.YES.getDbType().equals(rule.getReviewMethod());
  }

  /**
   * @param rule Cdr rule
   * @return the Ref Value Cal data object
   */
  public static CalData getRefValue(final ReviewRule rule) {
    if (rule != null) {
      if (rule.getRefValueCalData() != null) {
        return CalDataUtil.getCDPObj(rule.getRefValueCalData());
      }

      if ((CommonUtils.isNotNull(rule.getRefValue())) && CommonUtils.isEqual(rule.getValueType(), "VALUE")) {
        String paramName = rule.getParameterName();
        String dcmStr = CalDataUtil.createDCMStringForNumber(paramName, rule.getUnit(), rule.getRefValue().toString());
        return CalDataUtil.dcmToCalDataExt(dcmStr, paramName, CDMLogger.getInstance());
      }
    }

    return null;
  }

  /**
   * refactoring the code to set ref value to cdr rule
   *
   * @param newRviewRule CDRRule to be updated
   * @param refValText ref value text
   * @param paramType parameter type
   * @param unitText unit
   * @param caldataObj CalData instance
   */
  // ICDM-1893
  public static void setRefValueToReviewRule(final ReviewRule newRviewRule, final String refValText,
      final String paramType, final String unitText, final CalData caldataObj) {
    boolean isTextRefValue;
    try {
      double parseDouble = Double.parseDouble(refValText);
      CDMLogger.getInstance().info("The Ref val is a number" + parseDouble);
      // if ref value is a number, set the boolean to false in cdr rule
      isTextRefValue = false;
    }
    catch (NumberFormatException numberException) {
      // if ref value is a text, set the boolean to true in cdr rule
      isTextRefValue = true;
    }

    if ("VALUE".equals(paramType)) {
      displayForTextVal(newRviewRule, refValText, unitText, isTextRefValue);
    }
    else {
      newRviewRule.setRefValueDcmString(caldataObj == null ? null : DcmFromCalData.getDcmString(caldataObj));
      // display for the ref value in the UI.
      if ((caldataObj != null) && (caldataObj.getCalDataPhy() != null)) {

        newRviewRule.setRefValueDispString(caldataObj.getCalDataPhy().getSimpleDisplayValue());
      }
    }
  }

  /**
   * @param newRviewRule
   * @param refValText
   * @param unitText
   * @param isTextRefValue
   */
  private static void displayForTextVal(final ReviewRule newRviewRule, final String refValText, final String unitText,
      final boolean isTextRefValue) {
    if (isTextRefValue) {
      if (CommonUtils.isEmptyString(refValText)) {
        // if the text value is empty ,set null as dcm string
        newRviewRule.setRefValueDcmString(null);
      }
      else {
        String dcmStr = CalDataUtil.createDCMStringForText(newRviewRule.getParameterName(), unitText, refValText);
        newRviewRule.setRefValueDcmString(dcmStr);
      }
      newRviewRule.setRefValue(null);
    }
    else {
      newRviewRule.setRefValue(CommonUtils.isEmptyString(refValText.trim()) ? null : new BigDecimal(refValText.trim()));
      newRviewRule.setRefValueDcmString(null);
      // display purpose for the ref value in the UI.
      newRviewRule.setRefValueDispString(refValText.trim());
    }
  }

  /**
   * Icdm-500
   *
   * @param rule Cdr Rule
   * @return the Rule Ready for series Ui Value
   */
  public static String getReadyForSeriesUIVal(final ReviewRule rule) {
    if (rule != null) {
      if (ApicConstants.READY_FOR_SERIES.YES.dbType.equals(rule.getReviewMethod())) {
        return ApicConstants.READY_FOR_SERIES.YES.uiType;
      }
      else if (ApicConstants.READY_FOR_SERIES.NO.dbType.equals(rule.getReviewMethod())) {
        return ApicConstants.READY_FOR_SERIES.NO.uiType;
      }
    }
    return null;
  }

  /**
   * @param sourceRule source Rule
   * @param targetRule target Rule
   * @param name parameter name
   */
  public static void updateRuleProperties(final ReviewRule sourceRule, final ReviewRule targetRule, final String name) {

    targetRule.setDcm2ssd(sourceRule.isDcm2ssd());
    targetRule.setHint(sourceRule.getHint());
    targetRule.setLabelFunction(sourceRule.getLabelFunction());
    targetRule.setLowerLimit(sourceRule.getLowerLimit());
    targetRule.setMaturityLevel(sourceRule.getMaturityLevel());
    targetRule.setParameterName(name);
    // iCDM-2071
    CalData refValueCalData = CalDataUtil.createCopyWithoutHistory(getRefValue(sourceRule), CDMLogger.getInstance());
    if (refValueCalData != null) {
      refValueCalData.setLongName(name);
    }
    targetRule.setRefValCalData(CalDataUtil.convertCalDataToZippedByteArr(refValueCalData, CDMLogger.getInstance()));
    targetRule.setRefValue(sourceRule.getRefValue());
    targetRule.setRefValueDcmString(sourceRule.getRefValueDcmString());
    targetRule.setRefValueDispString(sourceRule.getRefValueDispString());
    targetRule.setReviewMethod(sourceRule.getReviewMethod());
    targetRule.setSsdCase(sourceRule.getSsdCase());
    targetRule.setUnit(sourceRule.getUnit());
    targetRule.setUpperLimit(sourceRule.getUpperLimit());
    targetRule.setValueType(sourceRule.getValueType());
    // ICDM-2009
    targetRule.setBitWiseRule(sourceRule.getBitWiseRule());
  }

}
