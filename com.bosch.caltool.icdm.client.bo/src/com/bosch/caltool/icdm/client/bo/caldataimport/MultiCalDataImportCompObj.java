/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.caldataimport;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.calcomp.caldataphy2dcm.dcmwrite.DcmFromCalData;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.common.bo.cdr.ReviewRuleUtil;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportComparisonModel;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;


/**
 * ICDM-1999
 *
 * @author mkl2cob
 */
public class MultiCalDataImportCompObj {

  public static final String DIFFERENT_VALUES = "<Diff Values>";

  /**
   * Text constant for VALUE
   */
  public static final String VALUE_TEXT = "VALUE";

  private String paramName;

  private String paramType;

  private String oldParamClass;

  private String newParamClass;

  private String codeWord;

  private String calHint;

  private String paramBitwise;

  private boolean exactMatch;

  private String oldLowerLimit = "";

  private String oldUpperLimit = "";

  private String oldRefValue;

  private String oldMaturityLevel;

  private String oldReviewMethod;

  private String oldUnit;

  private String newLowerLimit = "";

  private String newUpperLimit = "";

  private String newRefValue;

  private String newMaturityLevel;

  private String newReviewMethod;

  private String newUnit;

  private String remarks;

  private String bitwiseRule;


  /**
   *
   */
  private boolean useNewRvwMtd;


  /**
   *
   */
  private boolean useNewRefVal;
  /**
   *
   */
  private boolean useNewUnit;
  /**
   *
   */
  private boolean useNewMaturityLvl;

  /**
   *
   */
  private boolean useNewLowLmt;

  /**
   *
   */
  private boolean useNewUpLmt;
  /**
   * list of comparison objects that are selected
   */
  private List<CalDataImportComparisonModel> comparisonObjList = new ArrayList<>();

  /**
   * parameter properties map
   */
  private final Map<String, Map<String, String>> paramPropsMap;

  /**
   * old param class values
   */
  private final Map<String, String> oldParamClassMap;

  /**
   * boolean to use new parameter class
   */
  private boolean useNewPClass;

  /**
   * @param paramPropsMap Map<String, Map<String, String>>
   * @param oldParamClassMap Map<String, String>
   */
  public MultiCalDataImportCompObj(final Map<String, Map<String, String>> paramPropsMap,
      final Map<String, String> oldParamClassMap) {
    this.paramPropsMap = paramPropsMap;
    this.oldParamClassMap = oldParamClassMap;
  }

  /**
   * initialise values for multi edit
   *
   * @throws DataException
   */
  public void initialiseValues() throws DataException {

    if (this.comparisonObjList != null) {
      // checking if all the rules have same maturity levels
      Iterator<CalDataImportComparisonModel> iterator = this.comparisonObjList.iterator();

      CalDataImportComparisonModel nextObj = iterator.next();
      this.paramType = nextObj.getParamType();
      ReviewRule newRule = nextObj.getNewRule();
      ReviewRule oldRule = nextObj.getOldRule();


      // initialising all variables
      this.paramName = nextObj.getParamName();
      // ICDM-2179
      this.oldParamClass = this.oldParamClassMap.get(this.paramName);
      Map<String, String> individualParamProp = this.paramPropsMap.get(this.paramName);
      this.newParamClass = individualParamProp.get(CDRConstants.CDIKEY_PARAM_CLASS);
      this.codeWord = individualParamProp.get(CDRConstants.CDIKEY_CODE_WORD);
      this.calHint = individualParamProp.get(CDRConstants.CDIKEY_CAL_HINT);
      this.paramBitwise = individualParamProp.get(CDRConstants.CDIKEY_BIT_WISE);

      this.newMaturityLevel = newRule.getMaturityLevel();
      this.remarks = newRule.getHint();
      this.newLowerLimit = newRule.getLowerLimit() == null ? "" : newRule.getLowerLimit().toString();
      this.newUpperLimit = newRule.getUpperLimit() == null ? "" : newRule.getUpperLimit().toString();
      BigDecimal newRefVal = null;
      CalData newRefCalData = null;
      if (CommonUtils.isNotNull(newRule.getRefValue())) {
        newRefVal = newRule.getRefValue();
      }
      this.exactMatch = newRule.isDcm2ssd();
      this.newUnit = newRule.getUnit();
      this.newReviewMethod = newRule.getReviewMethod();
      boolean sameRefValNew = true;

      boolean sameRefValOld = true;
      BigDecimal oldRefVal = null;
      CalData oldRefCalData = null;
      if (oldRule != null) {
        this.oldMaturityLevel = oldRule.getMaturityLevel();
        this.oldLowerLimit = oldRule.getLowerLimit() == null ? "" : oldRule.getLowerLimit().toString();
        this.oldUpperLimit = oldRule.getUpperLimit() == null ? "" : oldRule.getUpperLimit().toString();
        if (CommonUtils.isNotNull(oldRule.getRefValue())) {
          oldRefVal = oldRule.getRefValue();
        }
        this.oldUnit = oldRule.getUnit();
        this.oldReviewMethod = oldRule.getReviewMethod();
        this.bitwiseRule = oldRule.getBitWiseRule();
      }

      // Iterate through the rules
      while (iterator.hasNext()) {
        nextObj = iterator.next();
        newRule = nextObj.getNewRule();
        oldRule = nextObj.getOldRule();

        // ICDM-2179 param properties
        if (!CommonUtils.isEqual(this.paramName, DIFFERENT_VALUES) &&
            !CommonUtils.isEqual(this.paramName, nextObj.getParamName())) {
          // make it as DIFFERENT_VALUES if atleast one param name is different
          this.paramName = DIFFERENT_VALUES;
        }
        if (!CommonUtils.isEqual(this.oldParamClass, DIFFERENT_VALUES) &&
            !CommonUtils.isEqual(this.oldParamClass, this.oldParamClassMap.get(nextObj.getParamName()))) {
          // make it as DIFFERENT_VALUES if atleast one old parameter class is different
          this.oldParamClass = DIFFERENT_VALUES;
        }
        individualParamProp = this.paramPropsMap.get(nextObj.getParamName());
        if (!CommonUtils.isEqual(this.newParamClass, DIFFERENT_VALUES) &&
            !CommonUtils.isEqual(this.newParamClass, individualParamProp.get(CDRConstants.CDIKEY_PARAM_CLASS))) {
          // make it as DIFFERENT_VALUES if atleast one new parameter class is different
          this.newParamClass = DIFFERENT_VALUES;
        }
        if (!CommonUtils.isEqual(this.codeWord, DIFFERENT_VALUES) &&
            !CommonUtils.isEqual(this.codeWord, individualParamProp.get(CDRConstants.CDIKEY_CODE_WORD))) {
          // make it as DIFFERENT_VALUES if atleast one code word is different
          this.codeWord = DIFFERENT_VALUES;
        }
        if (!CommonUtils.isEqual(this.calHint, DIFFERENT_VALUES) &&
            !CommonUtils.isEqual(this.calHint, individualParamProp.get(CDRConstants.CDIKEY_CAL_HINT))) {
          // make it as DIFFERENT_VALUES if atleast one calibration hint is different
          this.calHint = DIFFERENT_VALUES;
        }
        if (!CommonUtils.isEqual(this.paramBitwise, DIFFERENT_VALUES) &&
            !CommonUtils.isEqual(this.paramBitwise, individualParamProp.get(CDRConstants.CDIKEY_BIT_WISE))) {
          // make it as DIFFERENT_VALUES if atleast one code word is different
          this.paramBitwise = DIFFERENT_VALUES;
        }

        // rule properties
        if (!CommonUtils.isEqual(this.newMaturityLevel, DIFFERENT_VALUES) &&
            !CommonUtils.isEqual(this.newMaturityLevel, newRule.getMaturityLevel())) {
          // make it as DIFFERENT_VALUES if atleast one maturity level is different
          this.newMaturityLevel = DIFFERENT_VALUES;
        }
        if (!CommonUtils.isEqual(this.remarks, DIFFERENT_VALUES) &&
            !CommonUtils.isEqual(this.remarks, newRule.getHint())) {
          // make it as DIFFERENT_VALUES if atleast one hint is different
          this.remarks = DIFFERENT_VALUES;
        }
        if (!CommonUtils.isEqual(this.newLowerLimit, DIFFERENT_VALUES) && !CommonUtils.isEqual(this.newLowerLimit,
            newRule.getLowerLimit() == null ? "" : newRule.getLowerLimit().toString())) {
          // make it as DIFFERENT_VALUES if atleast one lower limit is different
          this.newLowerLimit = DIFFERENT_VALUES;
        }
        if (!CommonUtils.isEqual(this.newUpperLimit, DIFFERENT_VALUES) && !CommonUtils.isEqual(this.newUpperLimit,
            newRule.getUpperLimit() == null ? "" : newRule.getUpperLimit().toString())) {
          // make it as DIFFERENT_VALUES if atleast one upper limit is different
          this.newUpperLimit = DIFFERENT_VALUES;
        }
        if (sameRefValNew) {
          sameRefValNew = checkIfRefValIsSame(sameRefValNew, newRule, newRefVal, newRefCalData);
        }
        if (!CommonUtils.isEqual(this.newUnit, DIFFERENT_VALUES) &&
            !CommonUtils.isEqual(this.newUnit, newRule.getUnit())) {
          // make it as DIFFERENT_VALUES if atleast one unit is different
          this.newUnit = DIFFERENT_VALUES;
        }
        if (!CommonUtils.isEqual(this.newReviewMethod, DIFFERENT_VALUES) &&
            !CommonUtils.isEqual(this.newReviewMethod, newRule.getReviewMethod())) {
          // make it as DIFFERENT_VALUES if atleast one review method is different
          this.newReviewMethod = DIFFERENT_VALUES;
        }
        if (!CommonUtils.isEqual(this.bitwiseRule, DIFFERENT_VALUES) &&
            !CommonUtils.isEqual(this.bitwiseRule, oldRule == null ? null : oldRule.getBitWiseRule())) {
          // make it as DIFFERENT_VALUES if atleast one bitwise rule is different
          this.bitwiseRule = DIFFERENT_VALUES;
        }

        // old values
        if (!CommonUtils.isEqual(this.oldMaturityLevel, DIFFERENT_VALUES) &&
            !CommonUtils.isEqual(this.oldMaturityLevel, oldRule == null ? null : oldRule.getMaturityLevel())) {
          // make it as DIFFERENT_VALUES if atleast one maturity level is different
          this.oldMaturityLevel = DIFFERENT_VALUES;
        }
        if (!CommonUtils.isEqual(this.oldLowerLimit, DIFFERENT_VALUES) && !CommonUtils.isEqual(this.oldLowerLimit,
            oldRule == null ? "" : oldRule.getLowerLimit() == null ? "" : oldRule.getLowerLimit().toString())) {
          // make it as DIFFERENT_VALUES if atleast one lower limit is different
          this.oldLowerLimit = DIFFERENT_VALUES;
        }
        if (!CommonUtils.isEqual(this.oldUpperLimit, DIFFERENT_VALUES) && !CommonUtils.isEqual(this.oldUpperLimit,
            oldRule == null ? "" : oldRule.getUpperLimit() == null ? "" : oldRule.getUpperLimit().toString())) {
          // make it as DIFFERENT_VALUES if atleast one upper limit is different
          this.oldUpperLimit = DIFFERENT_VALUES;
        }
        if (sameRefValOld && (oldRule != null)) {
          // make it as DIFFERENT_VALUES if atleast one Reference value is different
          sameRefValOld = checkIfRefValIsSame(sameRefValOld, oldRule, oldRefVal, oldRefCalData);
        }
        if (!CommonUtils.isEqual(this.oldUnit, DIFFERENT_VALUES) &&
            !CommonUtils.isEqual(this.oldUnit, oldRule == null ? null : oldRule.getUnit())) {
          // make it as DIFFERENT_VALUES if atleast one unit is different
          this.oldUnit = DIFFERENT_VALUES;
        }
        if (!CommonUtils.isEqual(this.oldReviewMethod, DIFFERENT_VALUES) &&
            !CommonUtils.isEqual(this.oldReviewMethod, oldRule == null ? null : oldRule.getReviewMethod())) {
          // make it as DIFFERENT_VALUES if atleast one review method is different
          this.oldReviewMethod = DIFFERENT_VALUES;
        }
      }
      // set the reference values
      setReferenceValues(newRefVal, newRefCalData, sameRefValNew, sameRefValOld, oldRefVal, oldRefCalData);
    }
  }

  /**
   * @param newRefVal
   * @param newRefCalData
   * @param sameRefValNew
   * @param sameRefValOld
   * @param oldRefVal
   * @param oldRefCalData
   */
  private void setReferenceValues(final BigDecimal newRefVal, final CalData newRefCalData, final boolean sameRefValNew,
      final boolean sameRefValOld, final BigDecimal oldRefVal, final CalData oldRefCalData) {
    if (sameRefValNew) {
      // reference values are same
      if (newRefVal != null) {
        // for VALUE type
        this.newRefValue = newRefVal.toString();
      }
      else {
        this.newRefValue = newRefCalData == null ? null : newRefCalData.getCalDataPhy().getSimpleDisplayValue();
      }
    }
    else {
      this.newRefValue = DIFFERENT_VALUES;
    }

    if (sameRefValOld) {
      // reference values are same
      if (oldRefVal != null) {
        // for VALUE type
        this.oldRefValue = oldRefVal.toString();
      }
      else {
        this.oldRefValue = oldRefCalData == null ? null : oldRefCalData.getCalDataPhy().getSimpleDisplayValue();
      }
    }
    else {
      this.oldRefValue = DIFFERENT_VALUES;
    }
  }

  /**
   * @param sameRefVal boolean
   * @param nextRule CDRRule
   * @param refVal BigDecimal
   * @param refCalData CalData
   * @return true if the rule has same ref value
   * @throws DataException
   */
  private boolean checkIfRefValIsSame(final boolean sameRefVal, final ReviewRule nextRule, final BigDecimal refVal,
      final CalData refCalData)
      throws DataException {
    boolean checkRefValSame = sameRefVal;
    if (CommonUtils.isEqual(nextRule.getValueType(), VALUE_TEXT)) {
      if (CommonUtils.isNotNull(refVal)) {
        if (!CommonUtils.isEqual(refVal, nextRule.getRefValue())) {
          // check if the big decimal value is same
          checkRefValSame = false;
        }
      }
      else {
        // check if the ref val cal data values are same
        checkRefValSame = checkRefValCalDataSame(nextRule, refCalData);
      }
    }
    else {
      checkRefValSame = checkRefValCalDataSame(nextRule, refCalData);
    }
    return checkRefValSame;
  }

  /**
   * @param nextRule CDRRule
   * @param refCalData CalData
   * @return true if the ref value cal data objects are same
   * @throws DataException
   */
  private boolean checkRefValCalDataSame(final ReviewRule nextRule, final CalData refCalData) throws DataException {
    if (CommonUtils.isNotNull(nextRule.getRefValue())) {
      // make the boolean false if the rule has big decimal value
      return false;
    }

    CalData currCalData = getRefValForRule(nextRule);
    // make the boolean true if atleast one reference value is different
    return (isRefAndCurrCalDataValEqual(refCalData, currCalData) ||
        (CommonUtils.isNull(currCalData) && CommonUtils.isNull(refCalData)));
  }

  /**
   * @param refCalData
   * @param currCalData
   * @return
   */
  private boolean isRefAndCurrCalDataValEqual(final CalData refCalData, CalData currCalData) {
    return (refCalData != null) && (currCalData != null) &&
        CommonUtils.isEqualValue(refCalData.getCalDataPhy(), currCalData.getCalDataPhy());
  }

  /**
   * @param rule
   * @return
   * @throws ClassNotFoundException
   * @throws IOException
   */
  private CalData getRefValForRule(final ReviewRule rule) throws DataException {
    CalData caldataObj = null;
    try {
      caldataObj = CalDataUtil.getCalDataObj(rule.getRefValueCalData());
    }
    catch (ClassNotFoundException | IOException exp) {
      throw new DataException("error when creating cal data object" + exp);
    }

    return caldataObj;
  }


  /**
   * @return the comparisonObjList
   */
  public List<CalDataImportComparisonModel> getComparisonObjList() {
    return this.comparisonObjList;
  }

  /**
   * @param comparisonObjList the comparisonObjList to set
   */
  public void setComparisonObjList(final List<CalDataImportComparisonModel> comparisonObjList) {
    this.comparisonObjList = comparisonObjList;
  }


  /**
   * @return the differentValues
   */
  public static String getDifferentValues() {
    return DIFFERENT_VALUES;
  }


  /**
   * @return the paramName
   */
  public String getParamName() {
    return this.paramName;
  }


  /**
   * @return the oldParamClass
   */
  public String getOldParamClass() {
    return this.oldParamClass;
  }


  /**
   * @return the newParamClass
   */
  public String getNewParamClass() {
    return this.newParamClass;
  }


  /**
   * @return the isCodeWord
   */
  public String isCodeWord() {
    return this.codeWord;
  }


  /**
   * @return the calHint
   */
  public String getCalHint() {
    return this.calHint;
  }


  /**
   * @return the exactMatch
   */
  public boolean isExactMatch() {
    return this.exactMatch;
  }


  /**
   * @return the oldLowerLimit
   */
  public String getOldLowerLimit() {
    return this.oldLowerLimit;
  }


  /**
   * @return the oldUpperLimit
   */
  public String getOldUpperLimit() {
    return this.oldUpperLimit;
  }


  /**
   * @return the oldRefValue
   */
  public String getOldRefValue() {
    return this.oldRefValue;
  }


  /**
   * @return the oldMaturityLevel
   */
  public String getOldMaturityLevel() {
    return this.oldMaturityLevel;
  }


  /**
   * @return the oldReviewMethod
   */
  public String getOldReviewMethod() {
    return this.oldReviewMethod;
  }


  /**
   * @return the oldUnit
   */
  public String getOldUnit() {
    return this.oldUnit;
  }


  /**
   * @return the newLowerLimit
   */
  public String getNewLowerLimit() {
    return this.newLowerLimit;
  }


  /**
   * @return the newUpperLimit
   */
  public String getNewUpperLimit() {
    return this.newUpperLimit;
  }


  /**
   * @return the newRefValue
   */
  public String getNewRefValue() {
    return this.newRefValue;
  }


  /**
   * @return the newMaturityLevel
   */
  public String getNewMaturityLevel() {
    return this.newMaturityLevel;
  }


  /**
   * @return the newReviewMethod
   */
  public String getNewReviewMethod() {
    return this.newReviewMethod;
  }


  /**
   * @return the newUnit
   */
  public String getNewUnit() {
    return this.newUnit;
  }


  /**
   * @return the remarks
   */
  public String getRemarks() {
    return this.remarks;
  }

  /**
   * @return the paramType
   */
  public String getParamType() {
    return this.paramType;
  }

  /**
   * @return bitwiseRule
   */
  public String getBitWiseRule() {
    return this.bitwiseRule;
  }

  /**
   * @param apicDataProvider ApicDataProvider
   * @param wizardPge CompareRuleImpWizardPage
   * @return message after validation
   * @throws DataException
   */
  public String validateAndUpdateRules() throws DataException {
    StringBuilder message = new StringBuilder();

    ConcurrentMap<CalDataImportComparisonModel, String> calDataMsgMap = new ConcurrentHashMap<>();

    for (CalDataImportComparisonModel calDataComp : this.comparisonObjList) {

      ReviewRule newRule = calDataComp.getNewRule();
      ReviewRule oldRule = calDataComp.getOldRule();
      // lower and upper limit validation
      BigDecimal lowerLimit = null;
      BigDecimal upperLimit = null;
      String unit = null;
      // find lower limit
      if (this.useNewLowLmt) {
        lowerLimit = new BigDecimal(this.newLowerLimit);
      }
      else if (calDataComp.isUseNewLowLmt()) {
        lowerLimit = newRule.getLowerLimit();
      }
      else if (null != oldRule) {
        lowerLimit = oldRule.getLowerLimit();
      }

      // find upper limit
      if (this.useNewUpLmt) {
        upperLimit = new BigDecimal(this.newUpperLimit);
      }
      else if (calDataComp.isUseNewUpLmt()) {
        upperLimit = newRule.getUpperLimit();
      }
      else if (null != oldRule) {
        upperLimit = oldRule.getUpperLimit();
      }
      if ((lowerLimit != null) && (upperLimit != null) && (ApicUtil.compareBigDecimal(lowerLimit, upperLimit) > 0)) {
        // invalid lower and upper limits
        calDataMsgMap.put(calDataComp, "Lower limit should be less than Upper Limit!");
        continue;
      }

      // unit validation
      if (!CommonUtils.isEqual(this.paramType, VALUE_TEXT)) {
        String refValCalData = null;
        unit = getUnit(calDataComp, newRule, oldRule, unit);
        // initialise reference value
        refValCalData = getRefValue(calDataComp, newRule, oldRule, refValCalData);

        // check if atleast one field exists
        if ((unit != null) && (lowerLimit == null) && (upperLimit == null) && (refValCalData == null)) {
          // ICDM-2102 check for refVal in case of DCM
          calDataMsgMap.put(calDataComp, "When Unit is specified, min,max or dcm value has to be present");
          continue;
        }

      }
      // exact match validation , not needed as the validation is done in UI
      String refValStr;
      // reference value check for exact match flag
      refValStr = checkExactMatch(calDataComp);
      if (CommonUtils.isEmptyString(refValStr) && this.exactMatch) {
        // invalid exact match
        continue;
      }
      // bitwise validation
      Map<String, String> propertiesMap = this.paramPropsMap.get(calDataComp.getParamName());
      boolean isBitwiseRule =
          CommonUtils.isEqual(ApicConstants.CODE_WORD_YES, propertiesMap.get(CDRConstants.CDIKEY_BIT_WISE));
      if (isBitwiseRule) {
        // if it is a bitwise rule, lower and upper limits should not be given
        if ((this.useNewLowLmt && CommonUtils.isNotEmptyString(this.newLowerLimit)) ||
            (this.useNewUpLmt && CommonUtils.isNotEmptyString(this.newUpperLimit))) {
          continue;
        }

      }
      storeValues(calDataComp, lowerLimit, upperLimit, unit);
    }
    createDisplayMsg(message, calDataMsgMap);
    return message.toString();
  }

  /**
   * @param calDataComp
   * @return
   * @throws DataException
   */
  private String checkExactMatch(final CalDataImportComparisonModel calDataComp) throws DataException {
    String refValStr;
    if (this.useNewRefVal) {
      if (CommonUtils.isEqual(this.newRefValue, DIFFERENT_VALUES)) {

        CalData refValForRule = getRefValForRule(calDataComp.getNewRule());
        refValStr = getRefValDisp(refValForRule);
      }
      else {
        refValStr = this.newRefValue;
      }
    }
    else {
      if (calDataComp.isUseNewRefVal()) {
        CalData refValForRule = getRefValForRule(calDataComp.getNewRule());
        refValStr = getRefValDisp(refValForRule);
      }
      else {
        CalData refValForRule = getRefValForRule(calDataComp.getOldRule());
        refValStr = getRefValDisp(refValForRule);

      }
    }
    return refValStr;
  }


  /**
   * @return the oldRefValue for display
   */
  private String getRefValDisp(final CalData refVal) {

    return refVal == null ? "" : refVal.getCalDataPhy().getSimpleDisplayValue();
  }

  /**
   * @param apicDataProvider
   * @param message
   * @param calDataMsgMap
   */
  private void createDisplayMsg(final StringBuilder message,
      final Map<CalDataImportComparisonModel, String> calDataMsgMap) {
    if (CommonUtils.isNotEmpty(calDataMsgMap)) {
      message.append("Updation Failed for the following rules").append("\n");
      for (Entry<CalDataImportComparisonModel, String> errMsg : calDataMsgMap.entrySet()) {
        // if there are errors during validation
        message.append(errMsg.getKey().getParamName());
        String dependencyStr = getAttrValString(errMsg.getKey().getDependencyList());
        if (CommonUtils.isNotEmptyString(dependencyStr)) {
          message.append(":");
          message.append(dependencyStr);
        }
        message.append("-").append(errMsg.getValue()).append("\n");
      }
    }
    else {
      // if there is no error during validation
      message.append("Validation Successful!");
    }
  }

  /**
   * @param attrValSet
   * @return
   */
  private String getAttrValString(final SortedSet<AttributeValueModel> attrValSet) {
    String result = "";
    StringBuilder depen = new StringBuilder();
    for (AttributeValueModel attrVal : attrValSet) {
      // iCDM-1317
      depen.append(attrVal.getAttr().getName()).append("  --> ").append(attrVal.getValue().getName()).append("  ;  ");
    }
    if (!CommonUtils.isEmptyString(depen.toString())) {
      result = depen.substring(0, depen.length() - 4).trim();
    }
    return result;
  }


  /**
   * @param calDataComp
   * @param newRule
   * @param oldRule
   * @param unit
   * @return
   */
  private String getUnit(final CalDataImportComparisonModel calDataComp, final ReviewRule newRule,
      final ReviewRule oldRule, final String unit) {
    String returnUnit = unit;
    if (this.useNewUnit) {
      returnUnit = this.newUnit;
    }
    else if (calDataComp.isUseNewUnit()) {
      returnUnit = newRule.getUnit();
    }
    else {
      if (oldRule != null) {
        returnUnit = oldRule.getUnit();
      }
    }
    return returnUnit;
  }

  /**
   * @param calDataComp
   * @param newRule
   * @param oldRule
   * @param refValCalData
   * @return
   */
  private String getRefValue(final CalDataImportComparisonModel calDataComp, final ReviewRule newRule,
      final ReviewRule oldRule, final String refValCalData) {
    String returnCalData = refValCalData;
    if (this.useNewRefVal) {
      returnCalData = this.newUnit;
    }
    else if (calDataComp.isUseNewRefVal()) {
      returnCalData = newRule.getRefValueDcmString();
    }
    else {
      if (oldRule != null) {
        returnCalData = oldRule.getRefValueDcmString();
      }
    }
    return returnCalData;
  }

  /**
   * @param calDataComp
   * @param lowerLimit
   * @param upperLimit
   * @param unit
   * @throws DataException
   */
  private void storeValues(final CalDataImportComparisonModel calDataComp, final BigDecimal lowerLimit,
      final BigDecimal upperLimit, final String unit)
      throws DataException {
    // store exact match value

    calDataComp.getNewRule().setDcm2ssd(this.exactMatch);

    storeParamProps(calDataComp);
    if (this.useNewLowLmt) {
      if (!CommonUtils.isEqual(lowerLimit, DIFFERENT_VALUES)) {
        calDataComp.getNewRule().setLowerLimit(lowerLimit);
      }
      calDataComp.setUseNewLowLmt(true);
    }
    if (this.useNewUpLmt) {
      if (!CommonUtils.isEqual(lowerLimit, DIFFERENT_VALUES)) {
        calDataComp.getNewRule().setUpperLimit(upperLimit);
      }
      calDataComp.setUseNewUpLmt(true);
    }

    if (CommonUtils.isEqual(this.paramType, VALUE_TEXT) && this.useNewRefVal) {
      if (!CommonUtils.isEqual(this.newRefValue, DIFFERENT_VALUES)) {
        ReviewRuleUtil.setRefValueToReviewRule(calDataComp.getNewRule(), this.newRefValue, calDataComp.getParamType(),
            unit, ReviewRuleUtil.getRefValue(calDataComp.getNewRule()));
      }
      calDataComp.setUseNewRefVal(true);
    }
    if (this.useNewMaturityLvl) {
      if (!CommonUtils.isEqual(this.newMaturityLevel, DIFFERENT_VALUES)) {
        calDataComp.getNewRule().setMaturityLevel(this.newMaturityLevel);
      }
      calDataComp.setUseNewMaturityLvl(true);
    }
    if (this.useNewRvwMtd) {
      if (!CommonUtils.isEqual(this.newReviewMethod, DIFFERENT_VALUES)) {
        calDataComp.getNewRule().setReviewMethod(this.newReviewMethod);
      }
      calDataComp.setUseNewRvwMtd(true);
    }
    if (this.useNewUnit) {
      if (!CommonUtils.isEqual(this.newUnit, DIFFERENT_VALUES)) {
        calDataComp.getNewRule().setUnit(this.newUnit);
      }
      calDataComp.setUseNewUnit(true);
    }
    if (!CommonUtils.isEqual(this.remarks, DIFFERENT_VALUES)) {
      calDataComp.getNewRule().setHint(this.remarks);
    }
    calDataComp.setUpdateInDB(true);
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
  public static void setRefValueToRule(final ReviewRule newRviewRule, final String refValText, final String paramType,
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
   * // * @param calDataComp CalDataImportComparison //
   */
  private void storeParamProps(final CalDataImportComparisonModel calDataComp) {
    // ICDM-2179
    Map<String, String> map = this.paramPropsMap.get(calDataComp.getParamName());
    if (this.useNewPClass) {
      // set the class value if the check box is checked
      map.put(CDRConstants.CDIKEY_PARAM_CLASS, this.newParamClass);
      map.put(CDRConstants.CDIKEY_USE_CLASS, "true");
    }
    if (!CommonUtils.isEqual(this.calHint, DIFFERENT_VALUES)) {
      // set the calibration hint
      map.put(CDRConstants.CDIKEY_CAL_HINT, this.calHint);
    }
    if (!CommonUtils.isEqual(this.codeWord, DIFFERENT_VALUES)) {
      // set the calibration hint
      map.put(CDRConstants.CDIKEY_CODE_WORD, this.codeWord);
    }
  }

  /**
   * @return the useNewRvwMtd
   */
  public boolean isUseNewRvwMtd() {
    return this.useNewRvwMtd;
  }


  /**
   * @param useNewRvwMtd the useNewRvwMtd to set
   */
  public void setUseNewRvwMtd(final boolean useNewRvwMtd) {
    this.useNewRvwMtd = useNewRvwMtd;
  }


  /**
   * @return the useNewRefVal
   */
  public boolean isUseNewRefVal() {
    return this.useNewRefVal;
  }


  /**
   * @param useNewRefVal the useNewRefVal to set
   */
  public void setUseNewRefVal(final boolean useNewRefVal) {
    this.useNewRefVal = useNewRefVal;
  }


  /**
   * @return the useNewUnit
   */
  public boolean isUseNewUnit() {
    return this.useNewUnit;
  }


  /**
   * @param useNewUnit the useNewUnit to set
   */
  public void setUseNewUnit(final boolean useNewUnit) {
    this.useNewUnit = useNewUnit;
  }


  /**
   * @return the useNewMaturityLvl
   */
  public boolean isUseNewMaturityLvl() {
    return this.useNewMaturityLvl;
  }


  /**
   * @param useNewMaturityLvl the useNewMaturityLvl to set
   */
  public void setUseNewMaturityLvl(final boolean useNewMaturityLvl) {
    this.useNewMaturityLvl = useNewMaturityLvl;
  }


  /**
   * @return the useNewLowLmt
   */
  public boolean isUseNewLowLmt() {
    return this.useNewLowLmt;
  }


  /**
   * @param useNewLowLmt the useNewLowLmt to set
   */
  public void setUseNewLowLmt(final boolean useNewLowLmt) {
    this.useNewLowLmt = useNewLowLmt;
  }


  /**
   * @return the useNewUpLmt
   */
  public boolean isUseNewUpLmt() {
    return this.useNewUpLmt;
  }


  /**
   * @param useNewUpLmt the useNewUpLmt to set
   */
  public void setUseNewUpLmt(final boolean useNewUpLmt) {
    this.useNewUpLmt = useNewUpLmt;
  }

  /**
   * @param unit
   */
  public void setUnit(final String unit) {
    this.newUnit = unit;

  }

  /**
   * @param maturityLvl
   */
  public void setNewMaturityLevel(final String maturityLvl) {
    this.newMaturityLevel = maturityLvl;

  }

  /**
   * @param trim
   */
  public void setNewLowLmt(final String lowLmt) {
    this.newLowerLimit = lowLmt;
  }

  /**
   * @param trim
   */
  public void setNewUpLmt(final String upLmt) {
    this.newUpperLimit = upLmt;
  }

  /**
   * @param text
   */
  public void setNewRefVal(final String refVal) {
    this.newRefValue = refVal;
  }

  /**
   * @param exactMatch2
   */
  public void setExactMtch(final boolean exactMatch2) {
    this.exactMatch = exactMatch2;
  }

  /**
   * @param text
   */
  public void setRemarks(final String text) {
    this.remarks = text;
  }

  /**
   * @param selection
   */
  public void setUseNewParamClass(final boolean selection) {
    this.useNewPClass = selection;

  }

  /**
   * @param text
   */
  public void setCalHint(final String text) {
    this.calHint = text;
  }

  /**
   * @param pClass
   */
  public void setNewParamClass(final String pClass) {
    this.newParamClass = pClass;
  }

  /**
   * @param string
   */
  public void setCodeWord(final String codeWord) {
    this.codeWord = codeWord;
  }

  /**
   * @return the paramBitwise
   */
  public String getParamBitwise() {
    return this.paramBitwise;
  }

  /**
   * @param paramBitwise the paramBitwise to set
   */
  public void setParamBitwise(final String paramBitwise) {
    this.paramBitwise = paramBitwise;
  }

  /**
   * @param dbType
   */
  public void setReviewMethod(final String dbType) {
    this.newReviewMethod = dbType;
  }

}
