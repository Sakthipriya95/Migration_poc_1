/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.rules.bo;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;

import com.bosch.calcomp.caldataphy2dcm.dcmwrite.DcmFromCalData;
import com.bosch.calcomp.parser.dcm.DcmParser;
import com.bosch.calcomp.parser.dcm.exception.DCMParserException;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.common.exception.SsdInterfaceException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.logger.ParserLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.ssd.icdm.model.CDRRule;

// Icdm-1191 - Refactor Cdr Func Param
/**
 * @author rgo7cob New Class created for CdrRule Utility for getting the Ref Value for Basic and Complex Types. Also for
 *         getting the Ready for series String
 */
public final class CDRRuleUtil {


  /**
   * Text constant for VALUE
   */
  public static final String VALUE_TEXT = "VALUE";

  private CDRRuleUtil() {
    // Added default Constructor
  }

  /**
   * @param rule Cdr rule
   * @return the Ref Value Cal data object
   */
  public static CalData getRefValue(final CDRRule rule) {
    if (rule != null) {
      if ((CommonUtils.isNotNull(rule.getRefValue())) && CommonUtils.isEqual(rule.getValueType(), "VALUE")) {
        return rule.dcmToCalData(CalDataUtil.createDCMStringForNumber(rule.getParameterName(), rule.getUnit(),
            rule.getRefValue().toString()), rule.getParameterName());
      }

      if (rule.getRefValueCalData() != null) {
        return rule.getRefValueCalData();
      }

    }

    return null;
  }


  /**
   * @param rule Cdr rule
   * @return the Ref Value Cal data object
   * @throws IOException
   */
  public static CalData getRefValue(final ReviewRule rule) {
    if (rule != null) {
      if ((CommonUtils.isNotNull(rule.getRefValue())) && CommonUtils.isEqual(rule.getValueType(), "VALUE")) {
        return dcmToCalData(CalDataUtil.createDCMStringForNumber(rule.getParameterName(), rule.getUnit(),
            rule.getRefValue().toString()), rule.getParameterName());
      }

      if (rule.getRefValueCalData() != null) {
        return CalDataUtil.getCDPObj(rule.getRefValueCalData());
      }

    }

    return null;
  }

  /**
   * Icdm-500
   *
   * @param rule Cdr Rule
   * @return the Rule Ready for series Ui Value
   */
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
   * Icdm-500
   *
   * @param rule Cdr Rule
   * @return the Rule Ready for series Ui Value
   */
  @Deprecated
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
   * Rule is a default rule, if there are no dependencies attached to it, but the parameter has dependent attributes
   *
   * @param parentParamColln parent paramter collection
   * @param rule CDRRule
   * @return true, if the input is a default rule
   * @throws SsdInterfaceException
   */
  public static boolean isDefaultRule(final IParameterCollection<?> parentParamColln, final CDRRule rule)
      throws SsdInterfaceException {
    return parentParamColln.getParameter(rule.getParameterName(), rule.getValueType()).hasDependencies() &&
        rule.getDependencyList().isEmpty();
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
      ssdRulesModifiedMap.put(ssdRulesEntrySet.getKey().toUpperCase(), ssdRulesEntrySet.getValue());
    }


    List<CDRRule> ruleList = ssdRulesModifiedMap.get(paramName.toUpperCase());
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
   * Custom rule sorting based on the attribute value dependencies of the rule. In this sorting, default rule, if
   * present, will be the first item. Other rules are sorted based on the text representation of the attribute value
   * dependency.
   * <p>
   * If the input collection is <code>null</code>, method returns an empty sorted set.
   * <p>
   * NOTE : This method should only be used to sort rules of a single parameter.
   *
   * @param unsortedRules rules collection
   * @param dataProvider APIC data provider
   * @return sorted rules
   */
  @Deprecated
  public static SortedSet<ReviewRule> sortRulesWithAttrDependency(final Collection<ReviewRule> unsortedRules) {
    /*
     * SortedSet<ReviewRule> cdrRulesSet = new TreeSet<>(new CDRRuleAttrDependencyComparator()); if
     * (CommonUtils.isNotNull(unsortedRules)) { cdrRulesSet.addAll(unsortedRules); } return cdrRulesSet;
     */
    return null;
  }


  /**
   * @param sourceRule
   * @param targetRule
   * @param string param name
   * @param insertRule
   */
  @Deprecated
  public static void updateRuleproperties(final CDRRule sourceRule, final CDRRule targetRule, final String name) {
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


  public static void updateRuleproperties(final ReviewRule sourceRule, final ReviewRule targetRule, final String name) {
    targetRule.setDcm2ssd(sourceRule.isDcm2ssd());
    targetRule.setHint(sourceRule.getHint());
    targetRule.setLabelFunction(sourceRule.getLabelFunction());
    targetRule.setLowerLimit(sourceRule.getLowerLimit());
    targetRule.setMaturityLevel(sourceRule.getMaturityLevel());
    targetRule.setParameterName(name);
    // iCDM-2071
    CalData refValueCalData =
        CalDataUtil.createCopyWithoutHistory(getRefValForRule(sourceRule), ObjectStore.getInstance().getLogger());
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

  /**
   * @param rule
   * @return
   * @throws ClassNotFoundException
   * @throws IOException
   */
  public static CalData getRefValForRule(final ReviewRule rule) {
    CalData caldataObj = null;
    try {
      caldataObj = CalDataUtil.getCalDataObj(rule.getRefValueCalData());
    }
    catch (ClassNotFoundException | IOException exp) {
      ObjectStore.getInstance().getLogger().error("error when creating cal data object" + exp);
    }

    return caldataObj;
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
      Double.parseDouble(refValText);
      // if ref value is a number, set the boolean to false in cdr rule
      isTextRefValue = false;
    }
    catch (NumberFormatException numberException) {
      // if ref value is a text, set the boolean to true in cdr rule
      isTextRefValue = true;
    }

    if ("VALUE".equals(paramType)) {
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
   * ICDM-1893 refactoring the code to set ref value to cdr rule
   *
   * @param newRviewRule CDRRule to be updated
   * @param refValText ref value text
   * @param paramType parameter type
   * @param unitText unit
   * @param caldataObj CalData instance
   * @deprecated Use the same method in <code>ReviewRuleUtil</code>
   */
  @Deprecated
  public static void setRefValueToReviewRule(final ReviewRule newRviewRule, final String refValText,
      final String paramType, final String unitText, final CalData caldataObj) {
    boolean isTextRefValue;
    try {
      Double.parseDouble(refValText);
      // if ref value is a number, set the boolean to false in cdr rule
      isTextRefValue = false;
    }
    catch (NumberFormatException numberException) {
      // if ref value is a text, set the boolean to true in cdr rule
      isTextRefValue = true;
    }

    if ("VALUE".equals(paramType)) {
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
        newRviewRule
            .setRefValue(CommonUtils.isEmptyString(refValText.trim()) ? null : new BigDecimal(refValText.trim()));
        newRviewRule.setRefValueDcmString(null);
      }
    }
    else {
      newRviewRule.setRefValueDcmString(caldataObj == null ? null : DcmFromCalData.getDcmString(caldataObj));
    }
  }

  /**
   * @param cdrRule CDRRule
   * @return copy of cdrRule
   */
  @Deprecated
  public static CDRRule createCopy(final CDRRule cdrRule) {
    CDRRule copyRule = new CDRRule();
    updateRuleproperties(cdrRule, copyRule, cdrRule.getParameterName());
    return copyRule;
  }


  /**
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
      StringBuffer bitValue = new StringBuffer(21);
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
   * @param icdmBitRule
   * @param paramName
   * @return
   */
  public String getSSDBitRule(final String icdmBitRule, final String paramName) {
    String icdmBit = new StringBuffer(icdmBitRule).reverse().toString();
    StringBuffer ssdBitRule = new StringBuffer();
    String rule = "";
    int bitIndex = 0;
    int count;
    if (icdmBitRule.contains("0") || icdmBit.contains("1")) {
      ssdBitRule.append(paramName).append(",BIT(");
      for (count = 0; count <= 18; count++) {
        if ((icdmBit.charAt(count) != 'X') && (icdmBit.charAt(count) != ' ')) {
          ssdBitRule.append(bitIndex).append(":").append(icdmBit.charAt(count)).append(",");
        }
        if ((icdmBit.charAt(count) != ' ')) {
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
   * @return true if rule is complex
   */
  public static boolean isRuleComplex(final String formulaDesc) {
    if ((null != formulaDesc) && formulaDesc.startsWith("!##########")) {
      return true;
    }
    return false;
  }

  /**
   * Rule is complete if any one of the following conditions is satisfied.
   * <p>
   * a) upper limit or lower limit is available<br>
   * b) reference value is available and exact match flag is true
   *
   * @return true if rule is complete
   */
  public static boolean isRuleComplete(final CDRRule rule) {

    if ((null != rule.getLowerLimit()) || (null != rule.getUpperLimit()) ||
        ((null != rule.getBitWiseRule()) && !rule.getBitWiseRule().isEmpty())) {
      return true;
    }
    if (rule.isDcm2ssd() && ((null != rule.getRefValue()) || (null != rule.getRefValueCalData()))) {
      return true;
    }

    return false;
  }

  /**
   * Converts DCM String to CalData object
   *
   * @param dcmStr DCM String
   * @param paramName1 Parameter name
   * @return CalData
   */
  public static CalData dcmToCalData(final String dcmStr, final String paramName1) {
    String paramFullName = paramName1;
    if (dcmStr != null) {
      final DcmParser parser = new DcmParser(ParserLogger.getInstance());
      try {
        parser.setFileContent(dcmStr);
        parser.parse();

        // SSD-337
        CalData calDataObj = null;
        if (parser.getCalDataMap() != null) {
          for (String lblName : parser.getCalDataMap().keySet()) {
            calDataObj = parser.getCalDataMap().get(lblName);
          }

          /**
           * To ensure if the CalData obj name matches parameter name that is passed. If not replace Caldata Obj name
           * with parameter name to support variant code
           */
          if ((calDataObj != null) && !(calDataObj.getShortName().equalsIgnoreCase(paramFullName))) {
            // added to handle DCM for variant coded classse -- SSD-382
            String p = paramFullName.substring(paramFullName.indexOf("["), paramFullName.indexOf("]") + 1);
            paramFullName = paramFullName.replace(p, "");
            if (paramFullName.contains(calDataObj.getShortName())) {
              calDataObj.setShortName(paramName1);
              calDataObj.getCalDataPhy().setName(paramName1);
            }
          }
        }
        return calDataObj;
      }
      catch (DCMParserException e) {
        ObjectStore.getInstance().getLogger()
            .error("Error parsing DCM String for parameter : " + paramFullName + "-" + e.getMessage());
      }
    }
    return null;
  }

  /**
   * ICDM-724
   *
   * @return Display string of Reference string
   */
  public String getRefValueDispString(final CDRRule rule) {
    String refValString = "";
    // VALUE type label
    if ((CommonUtils.isEqual(rule.getValueType(), VALUE_TEXT) && (rule.getRefValueCalData() == null))) {
      refValString = rule.getRefValue() == null ? "" : rule.getRefValue().toString();
    }
    // Complex type labels
    else if (rule.getRefValueCalData() != null) {
      refValString =
          rule.getRefValueCalData() == null ? "" : rule.getRefValueCalData().getCalDataPhy().getSimpleDisplayValue();
    }
    return refValString;
  }


}
