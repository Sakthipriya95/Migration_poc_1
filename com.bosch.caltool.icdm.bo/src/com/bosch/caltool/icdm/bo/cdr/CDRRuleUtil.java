/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;

import com.bosch.calcomp.caldataphy2dcm.dcmwrite.DcmFromCalData;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.FeatureValueModel;

// Icdm-1191 - Refactor Cdr Func Param
/**
 * @author rgo7cob New Class created for CdrRule Utility for getting the Ref Value for Basic and Complex Types. Also for
 *         getting the Ready for series String
 */
public final class CDRRuleUtil {


  /**
   * To remove semicolon as the end of attr-val string
   */
  private static final int SEMICOLON_SIZE = 4;


  /**
   * Text constant for VALUE
   */
  public static final String VALUE_TEXT = "VALUE";

  private CDRRuleUtil() {
    // Added default Constructor
  }

  /**
   * Ensures that, at the most only one rule is available for the given parameter. Useful when rules are fetched for a
   * given attribute value combination.
   *
   * @param ssdRules Map of rules given by SSD interface
   * @param paramName parameter
   * @return Rule, if exists as only one
   * @throws CommandException if more than one rule present
   */
  public static ReviewRule assertRule(final Map<String, List<ReviewRule>> ssdRules, final String paramName)
      throws CommandException {

    List<ReviewRule> ruleList = ssdRules.get(paramName);
    if ((ruleList == null) && ApicUtil.isVariantCoded(paramName)) {
      ruleList = ssdRules.get(ApicUtil.getBaseParamName(paramName));
    }

    if ((ruleList != null) && (ruleList.size() > 1)) {
      throw new CommandException("Multiple rules given for the parameter " + ruleList.get(0).getParameterName());
    }

    ReviewRule retRule = null;
    if (ruleList != null) {
      retRule = ruleList.get(0);
    }

    return retRule;

  }


  /**
   * @param rule Cdr Rule
   * @return the Rule Ready for series Ui Value
   */
  // Icdm-500
  public static String getReadyForSeriesUIVal(final CDRRule rule) {
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
   * @param rule Cdr Rule
   * @return the Rule Ready for series Ui Value
   */
  // Icdm-500
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
   * Ensures that, at the most only one rule is available for the given parameter. Useful when rules are fetched for a
   * given attribute value combination.
   *
   * @param ssdRules Map of rules given by SSD interface
   * @param paramName parameter
   * @return Rule, if exists as only one
   * @throws CommandException if more than one rule present
   */
  public static CDRRule assertSingleRule(final Map<String, List<CDRRule>> ssdRules, final String paramName)
      throws CommandException {

    Map<String, List<CDRRule>> ssdRulesModifiedMap = new HashMap<>();

    for (Entry<String, List<CDRRule>> ssdRulesEntrySet : ssdRules.entrySet()) {
      ssdRulesModifiedMap.put(ssdRulesEntrySet.getKey().toUpperCase(Locale.getDefault()), ssdRulesEntrySet.getValue());
    }

    List<CDRRule> ruleList = ssdRulesModifiedMap.get(paramName.toUpperCase(Locale.getDefault()));

    if ((ruleList == null) && ApicUtil.isVariantCoded(paramName)) {
      ruleList = ssdRules.get(ApicUtil.getBaseParamName(paramName));
    }

    if ((ruleList != null) && (ruleList.size() > 1)) {
      throw new CommandException("Multiple rules given for the parameter " + ruleList.get(0).getParameterName());
    }

    CDRRule retRule = null;
    if (ruleList != null) {
      retRule = ruleList.get(0);
    }

    return retRule;

  }

  /**
   * @param sourceRule
   * @param targetRule
   * @param string param name
   * @param insertRule
   */
  private static void updateRuleproperties(final CDRRule sourceRule, final CDRRule targetRule, final String name) {
    targetRule.setDcm2ssd(sourceRule.isDcm2ssd());
    targetRule.setHint(sourceRule.getHint());
    targetRule.setLabelFunction(sourceRule.getLabelFunction());
    targetRule.setLowerLimit(sourceRule.getLowerLimit());
    targetRule.setMaturityLevel(sourceRule.getMaturityLevel());
    targetRule.setParameterName(name);
    // iCDM-2071
    CalData refValueCalData =
        CalDataUtil.createCopyWithoutHistory(sourceRule.getRefValueCalData(), ObjectStore.getInstance().getLogger());
    if (refValueCalData != null) {
      refValueCalData.setLongName(name);
    }
    targetRule.setRefValCalData(refValueCalData);
    targetRule.setRefValue(sourceRule.getRefValue());
    targetRule.setRefValueDCMString(sourceRule.getRefValueDCMString());
    targetRule.setReviewMethod(sourceRule.getReviewMethod());
    targetRule.setSsdCase(sourceRule.getSsdCase());
    targetRule.setUnit(sourceRule.getUnit());
    targetRule.setUpperLimit(sourceRule.getUpperLimit());
    targetRule.setValueType(sourceRule.getValueType());
    // ICDM-2009
    targetRule.setBitWiseRule(sourceRule.getBitWiseRule());
  }

  /**
   * ICDM-1893 refactoring the code to set ref value to cdr rule
   *
   * @param newRviewRule CDRRule to be updated
   * @param refValText ref value text
   * @param paramType parameter type
   * @param unitText unit
   * @param caldataObj CalData instance
   */
  public static void setRefValueToRule(final CDRRule newRviewRule, final String refValText, final String paramType,
      final String unitText, final CalData caldataObj) {
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

    if (VALUE_TEXT.equals(paramType)) {
      if (isTextRefValue) {
        if (CommonUtils.isEmptyString(refValText)) {
          // if the text value is empty ,set null as dcm string
          newRviewRule.setRefValueDCMString(null);
        }
        else {
          String dcmStr = CalDataUtil.createDCMStringForText(newRviewRule.getParameterName(), unitText, refValText);
          newRviewRule.setRefValueDCMString(dcmStr);
        }
        newRviewRule.setRefValue(null);
      }
      else {
        newRviewRule
            .setRefValue(CommonUtils.isEmptyString(refValText.trim()) ? null : new BigDecimal(refValText.trim()));
        newRviewRule.setRefValueDCMString(null);
      }
    }
    else {
      newRviewRule.setRefValueDCMString(caldataObj == null ? null : DcmFromCalData.getDcmString(caldataObj));
    }
  }

  /**
   * @param cdrRule CDRRule
   * @return copy of cdrRule
   */
  // Used in ICDM BO layer
  public static CDRRule createCopy(final CDRRule cdrRule) {
    CDRRule copyRule = new CDRRule();
    updateRuleproperties(cdrRule, copyRule, cdrRule.getParameterName());
    return copyRule;
  }


  /**
   * @param formulaDesc formula Description
   * @return icdm format bitwise rule
   */
  public static String getIcdmBitRule(final String formulaDesc) {

    if (null != formulaDesc) {
      String[] bits = new String[16];
      for (int index = 0; index < 16; index++) {
        bits[index] = "X";
      }
      String[] bitVal = formulaDesc.split("[\\(\\)]");
      String[] bitPosition = bitVal[1].split(",");
      for (String element : bitPosition) {
        String[] bitVals = element.split(":");
        bits[Integer.parseInt(bitVals[0])] = bitVals[1];
      }
      StringBuilder bitValue = new StringBuilder(21);
      int bitSpace = 0;
      for (int index = 0; index < 16; index++) {
        bitSpace++;
        bitValue.append(bits[index]);
        if (bitSpace == 4) {
          bitValue.append(" ");
          bitSpace = 0;
        }
      }
      return bitValue.reverse().toString().trim();
    }
    return "";
  }


  /**
   * @param icdmBitRule bitwise rule string
   * @param paramName parameter name
   * @return bitwise rule in ssd format
   */
  public String getSSDBitRule(final String icdmBitRule, final String paramName) {
    String icdmBit = new StringBuilder(icdmBitRule).reverse().toString();
    StringBuilder ssdBitRule = new StringBuilder();
    String rule = "";
    int bitIndex = 0;
    int count;
    if (icdmBitRule.contains("0") || icdmBit.contains("1")) {
      ssdBitRule.append(paramName).append(",BIT(");
      for (count = 0; count <= 18; count++) {
        if ((icdmBit.charAt(count) != 'X') && (icdmBit.charAt(count) != ' ')) {
          ssdBitRule.append(bitIndex).append(":").append(icdmBit.charAt(count)).append(",");
        }
        if (icdmBit.charAt(count) != ' ') {
          bitIndex++;
        }
      }
      ssdBitRule.deleteCharAt(ssdBitRule.lastIndexOf(","));
      ssdBitRule.append(")");
    }
    if (!ssdBitRule.toString().isEmpty()) {
      rule = ssdBitRule.toString();
    }
    return rule;
  }


  /**
   * @param formulaDesc formula Desc
   * @return true if rule is complex
   */
  public static boolean isRuleComplex(final String formulaDesc) {
    return (null != formulaDesc) && formulaDesc.startsWith("!##########");
  }

  /**
   * Rule is complete if any one of the following conditions is satisfied.
   * <p>
   * a) upper limit or lower limit is available<br>
   * b) reference value is available and exact match flag is true
   *
   * @param rule CDRRule
   * @return true if rule is complete
   */
  public static boolean isRuleComplete(final CDRRule rule) {

    if ((null != rule.getLowerLimit()) || (null != rule.getUpperLimit()) ||
        ((null != rule.getBitWiseRule()) && !rule.getBitWiseRule().isEmpty())) {
      return true;
    }
    return rule.isDcm2ssd() && ((null != rule.getRefValue()) || (null != rule.getRefValueCalData()));
  }


  /**
   * @param rule CDRRule
   * @return Display string of Reference string
   */
  // ICDM-724
  public String getRefValueDispString(final CDRRule rule) {
    String refValString = "";
    // VALUE type label
    if (CommonUtils.isEqual(rule.getValueType(), VALUE_TEXT) && (rule.getRefValueCalData() == null)) {
      refValString = rule.getRefValue() == null ? "" : rule.getRefValue().toString();
    }
    // Complex type labels
    else if (rule.getRefValueCalData() != null) {
      refValString =
          rule.getRefValueCalData() == null ? "" : rule.getRefValueCalData().getCalDataPhy().getSimpleDisplayValue();
    }
    return refValString;
  }

  /**
   * @param attrValSet set of attribute value dependency model
   * @return String representation of attribute value dependency model
   */
  public static String getAttrValString(final SortedSet<AttributeValueModel> attrValSet) {
    String result = "";
    StringBuilder depen = new StringBuilder();
    for (AttributeValueModel attrVal : attrValSet) {
      // iCDM-1317
      depen.append(attrVal.getAttr().getName()).append("  --> ").append(attrVal.getAttr().getName()).append("  ;  ");
    }
    if (!CommonUtils.isEmptyString(depen.toString())) {
      result = depen.substring(0, depen.length() - SEMICOLON_SIZE).trim();
    }
    return result;
  }

  /**
   * Method to check whether the incoming Rule and rule created in ssd are same by comparing param name and dependency
   * list rule id is unique for each rule (default/dependency rule)
   *
   * @param newRule CDRRule from DB
   * @param oldRule CDRRule from UI
   * @return check whether incoming rule and in db are same
   */
  public static boolean isSameRuleRecord(final CDRRule newRule, final CDRRule oldRule) {

    return newRule.getRuleId() == null
        ? CommonUtils.isEqualIgnoreCase(CommonUtils.checkNull(oldRule.getParameterName()),
            newRule.getParameterName()) && areDependenciesSame(oldRule.getDependencyList(), newRule.getDependencyList())
        : CommonUtils.isEqual(newRule.getRuleId(), oldRule.getRuleId());
  }

  /**
   * Method to compare rule dependencies
   *
   * @param oldDepList
   * @param newDepList
   * @return
   */
  private static boolean areDependenciesSame(final List<FeatureValueModel> oldDepList,
      final List<FeatureValueModel> newDepList) {
    if (oldDepList.size() == newDepList.size()) {
      for (FeatureValueModel oldFeatVal : oldDepList) {
        boolean isPresent = false;
        for (FeatureValueModel newFeatVal : newDepList) {
          if (oldFeatVal.getFeatureId().equals(newFeatVal.getFeatureId()) &&
              oldFeatVal.getValueId().equals(newFeatVal.getValueId())) {
            isPresent = true;
            break;
          }
        }
        if (!isPresent) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Method to check whether the rules are same by comparing the fields
   *
   * @param rule1 rule1
   * @param rule2 rule 2
   * @return check for changes in data
   */
  public static boolean isSame(final CDRRule rule1, final CDRRule rule2) {

    return CommonUtils.isEqual(CommonUtils.checkNull(rule2.getBitWiseRule()),
        CommonUtils.checkNull(rule1.getBitWiseRule())) && CommonUtils.isEqual(rule2.isDcm2ssd(), rule1.isDcm2ssd()) &&
        CommonUtils.isEqual(rule2.isRuleComplete(), rule1.isRuleComplete()) &&
        CommonUtils.isEqual(rule2.isRuleComplex(), rule1.isRuleComplex()) &&
        CommonUtils.isEqual(CommonUtils.checkNull(rule2.getDependenciesForDisplay()),
            CommonUtils.checkNull(rule1.getDependenciesForDisplay())) &&
        CommonUtils.isEqual(CommonUtils.checkNull(rule2.getFormula()), CommonUtils.checkNull(rule1.getFormula())) &&
        CommonUtils.isEqual(CommonUtils.checkNull(rule2.getFormulaDesc()),
            CommonUtils.checkNull(rule1.getFormulaDesc())) &&
        // getIcdmBitRule is not compared and verfied explicitly since Formula Desc is already compared and verified
        CommonUtils.isEqual(CommonUtils.checkNull(rule2.getHint()), CommonUtils.checkNull(rule1.getHint())) &&
        CommonUtils.isEqual(rule2.getLowerLimit(), rule1.getLowerLimit()) &&
        CommonUtils.isEqual(rule2.getUpperLimit(), rule1.getUpperLimit()) &&
        CommonUtils.isEqual(CommonUtils.checkNull(rule2.getMaturityLevel()),
            CommonUtils.checkNull(rule1.getMaturityLevel())) &&
        CommonUtils.isEqual(rule2.getRefValue(), rule1.getRefValue()) &&
        CommonUtils.isEqual(CommonUtils.checkNull(rule2.getUnit()), CommonUtils.checkNull(rule1.getUnit())) &&
        CommonUtils.isEqual(CommonUtils.checkNull(rule2.getValueType()), CommonUtils.checkNull(rule1.getValueType())) &&
        CommonUtils.isEqual(CommonUtils.checkNull(rule2.getReviewMethod()),
            CommonUtils.checkNull(rule1.getReviewMethod()));
  }

  /**
   * Check if it is a valid complex rule
   *
   * @param cdrRule rule Model
   * @return
   * @throws IcdmException
   */
  public static void appendCommentForComplexRule(final CDRRule cdrRule) {
    if (CommonUtils.isNotEmptyString(cdrRule.getFormulaDesc()) && !cdrRule.isRuleComplex() &&
        (CommonUtils.isNotNull(cdrRule.getBitWiseRule()) && !cdrRule.getFormulaDesc().contains(CDRRule.BIT))) {
      String complexRulesWithComment = "!##########" + System.lineSeparator() + cdrRule.getFormulaDesc();
      cdrRule.setFormulaDesc(complexRulesWithComment);
    }
  }


}
