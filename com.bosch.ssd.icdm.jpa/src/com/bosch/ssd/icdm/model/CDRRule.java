/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.model;


import java.math.BigDecimal;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.bosch.calcomp.caldataphy2dcm.dcmwrite.DcmFromCalData;
import com.bosch.calcomp.parser.dcm.DcmParser;
import com.bosch.calcomp.parser.dcm.exception.DCMParserException;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.ssd.icdm.exception.ExceptionUtils;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException.SSDiCDMInterfaceErrorCodes;
import com.bosch.ssd.icdm.logger.SSDiCDMInterfaceLogger;
import com.bosch.ssd.icdm.model.SSDConfigEnums.ParameterClass;

/**
 * This class is the business object of the Calibration data review Rules
 *
 * @author HSU4COB iCDM-498
 */
public class CDRRule implements Comparable<CDRRule> {

  /**
   *
   */
  private static final String SQUARE_PARANTHESIS_CLOSE = "]";

  /**
   *
   */
  private static final String SQUARE_PARANTHESIS_OPEN = "[";

  /**
   *
   */
  public static final String BIT = ",BIT(";
  
  /**
   * 
   */
  private static final String COMPLEX_RULE = "COMPLEX RULE!";

  /**
   * 
   */
  private static final String COMPLEX_RULE_PREFIX = "!##########";


  /**
   * @author HSU4COB
   */
  public enum SortColumns {
                           /**
                            * Parameter Name
                            */
                           SORT_PARAM_NAME,
                           /**
                            * Value Type
                            */
                           SORT_VALUE_TYPE,
                           /**
                            * Lower limit
                            */
                           SORT_LOWER_LIMIT,
                           /**
                            * Upper limit
                            */
                           SORT_UPPER_LIMIT,
                           /**
                            * BitWise limit
                            */
                           SORT_BITWISE_LIMIT,
                           /**
                            * Reference Value
                            */
                           SORT_REF_VALUE,
                           /**
                            * Review Method
                            */
                           SORT_REVIEW_METHOD,
                           /**
                            * Revision ID
                            */
                           SORT_REVISION_ID,
                           /**
                            * Created Date
                            */
                           SORT_CREATED_DATE,
                           /**
                            * Created User
                            */
                           SORT_CREATED_USER,

                           /**
                            * Unit
                            */
                           SORT_UNIT,

                           /**
                            * Exact match to reference value/ is dcmtossd
                            */
                           SORT_EXACT_MATCH

  }

  /**
   * Text constant for VALUE
   */
  public static final String VALUE_TEXT = "VALUE";

  /**
   * Label Function
   */
  private String labelFunction;
  /**
   * Review Rule Id
   */
  private BigDecimal ruleId;

  /**
   * Label id from LDB
   */
  private BigDecimal labelId;
  /**
   * revision id of the rule
   */
  private BigDecimal revId;
  /**
   * Hint
   */
  private String hint;
  /**
   * Lower Limit
   */
  private BigDecimal lowerLimit;
  /**
   * Parameter name
   */
  private String paramName;
  /**
   * Reference Value
   */
  private BigDecimal refValue;

  /**
   * Review method
   */
  private String reviewMethod = "M";
  /**
   * Unit
   */
  private String unit;
  /**
   * Upper Limit
   */
  private BigDecimal upperLimit;
  /**
   * Parameter Type
   */
  private String valueType;


  /**
   * ICDM-724 RefValue Complex Type
   */
  private CalData refValueCalData;

  /**
   * boolean to define if dcm has to be considered as ssd
   */
  private boolean dcm2ssd;

  /**
   * user who created the rule
   */
  private String ruleCreatedUser;

  /**
   * date when rule got created
   */
  private Date ruleCreatedDate;
  /**
   * list of feature values for a rule
   */
  private List<FeatureValueModel> dependencyList = new ArrayList<>();
  /**
   * maturity level of the label
   */
  private String maturityLevel;

  /**
   * Attribute value dependencies of rule in a String representation for display (iCDM-713)
   */
  private String ruleDependencyStr;
  /**
   * to set and get ssd case using interface
   */
  private SSDCase ssdCase;

  /**
   * ddefault value has been added as custspec
   */
  private ParameterClass paramClass = ParameterClass.CUSTSPEC;


  /**
   * parameter to hold the advanced formula SSD-357
   */
  private String formulaDesc;

  /**
   * parameter to hold the advanced formula name SSD-357
   */
  private String formula;
  /**
   * Bitwise rule value
   */
  private String bitWiseRule;

  /**
   * @return the ssdClass
   */
  public ParameterClass getParamClass() {
    return this.paramClass;
  }


  /**
   * @param ssdClass the ssdClass to set
   */
  public void setParamClass(final ParameterClass ssdClass) {
    this.paramClass = ssdClass;
  }


  /**
   * @return the ssdCase
   */
  public SSDCase getSsdCase() {
    return this.ssdCase;
  }


  /**
   * @param ssdCase the ssdCase to set
   */
  public void setSsdCase(final SSDCase ssdCase) {
    this.ssdCase = ssdCase;
  }

  /**
   * Get the parameter name
   *
   * @return the name
   */
  public String getParameterName() {
    return this.paramName;
  }

  /**
   * Sets the parameter name
   *
   * @param name parameter name
   */
  public void setParameterName(final String name) {
    this.paramName = (name == null) ? null : name.trim();
  }

  /**
   * get the parameter type
   *
   * @return the type
   */
  public String getValueType() {
    return this.valueType;
  }

  /**
   * Sets the value type
   *
   * @param valueType parameter type
   */
  public void setValueType(final String valueType) {
    this.valueType = valueType;
  }

  /**
   * @return the lower limit
   */
  public BigDecimal getLowerLimit() {
    return this.lowerLimit;
  }

  /**
   * @param lowerLimit lowerLimit
   */
  public void setLowerLimit(final BigDecimal lowerLimit) {
    this.lowerLimit = lowerLimit;
  }

  /**
   * @return the upper limit
   */
  public BigDecimal getUpperLimit() {
    return this.upperLimit;
  }

  /**
   * @param upperLimit upperLimit
   */
  public void setUpperLimit(final BigDecimal upperLimit) {
    this.upperLimit = upperLimit;
  }

  /**
   * @return the RefValue
   */
  public BigDecimal getRefValue() {
    return this.refValue;

  }

  /**
   * @param refValue RefValue
   */
  public void setRefValue(final BigDecimal refValue) {
    this.refValue = refValue;
  }

  /**
   * @return the ReviewMethod
   */
  public String getReviewMethod() {
    return this.reviewMethod;

  }

  /**
   * @param reviewMethod ReviewMethod
   */
  public void setReviewMethod(final String reviewMethod) {
    this.reviewMethod = reviewMethod;
  }

  /**
   * @return the hint
   */
  public String getHint() {
    return this.hint;
  }

  /**
   * @param hint Hint
   */
  public void setHint(final String hint) {
    this.hint = hint;
  }

  /**
   * @return the unit
   */
  public String getUnit() {
    return this.unit;
  }

  /**
   * @param unit unit
   */
  public void setUnit(final String unit) {
    if ((null != unit) && (unit.isEmpty())) {
      this.unit = null;
    }
    else {
      this.unit = unit;
    }
  }


  /**
   * @return the labelId
   */
  public BigDecimal getLabelId() {
    return this.labelId;
  }


  /**
   * @param labelId the labelId to set
   */
  public void setLabelId(final BigDecimal labelId) {
    this.labelId = labelId;
  }


  /**
   * @param ruleId the ruleId to set
   */
  public void setRuleId(final BigDecimal ruleId) {
    this.ruleId = ruleId;
  }


  /**
   * @return the revId
   */
  public BigDecimal getRevId() {
    return this.revId;
  }


  /**
   * @param revId the revId to set
   */
  public void setRevId(final BigDecimal revId) {
    this.revId = revId;
  }


  /**
   * @return the function for the label
   */
  public String getLabelFunction() {
    return this.labelFunction;
  }

  /**
   * @param labelFunction - set the label function
   */
  public void setLabelFunction(final String labelFunction) {
    this.labelFunction = labelFunction;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CDRRule other) {
    int compareResult = compare(getParameterName(), other.getParameterName());
    if (compareResult == 0) {
      compareResult = compareBigDecimal(getRuleId(), other.getRuleId());
    }
    return compareResult;
  }

  /**
   * @param param2 param2
   * @param sortColumn sortColumn
   * @return int
   */
  public int compareTo(final CDRRule param2, final SortColumns sortColumn) {
    int compareResult = 0;

    switch (sortColumn) {

      case SORT_PARAM_NAME:
        // use compare method for Strings
        compareResult = compare(getParameterName(), param2.getParameterName());
        break;

      case SORT_VALUE_TYPE:
        // use compare method for Strings
        compareResult = compare(getValueType(), param2.getValueType());
        break;

      case SORT_LOWER_LIMIT:
        // use compare method for Strings
        compareResult = compareBigDecimal(getLowerLimit(), param2.getLowerLimit());
        break;

      case SORT_UPPER_LIMIT:
        // use compare method for Strings
        compareResult = compareBigDecimal(getUpperLimit(), param2.getUpperLimit());
        break;
      case SORT_BITWISE_LIMIT:
        // use compare method for Strings
        compareResult = compare(getBitWiseRule(), param2.getBitWiseRule());
        break;
      case SORT_REF_VALUE:
        // use compare method for Strings
        compareResult = compareBigDecimal(getRefValue(), param2.getRefValue());
        break;
      case SORT_REVISION_ID:
        // use compare method for Strings
        compareResult = getRevId().compareTo(param2.getRevId());
        break;
      case SORT_CREATED_USER:
        // use compare method for Strings
        compareResult = compare(getRuleCreatedUser(), param2.getRuleCreatedUser());
        break;
      case SORT_CREATED_DATE:
        // use compare method for Strings
        compareResult = getRuleCreatedDate().compareTo(param2.getRuleCreatedDate());
        break;
      case SORT_REVIEW_METHOD:
        // use compare method for Strings
        compareResult = compare(getReviewMethod(), param2.getReviewMethod());
        break;
      case SORT_UNIT:
        // use compare method for strings
        compareResult = compare(getUnit(), param2.getUnit());
        break;
      case SORT_EXACT_MATCH:
        // use compare method for boolean
        compareResult = compareBoolean(isDcm2ssd(), param2.isDcm2ssd());
        break;
      default:
        compareResult = 0;
        break;
    }

    // additional compare column is the name of the characteristic
    if (compareResult == 0) {
      // compare result is equal, compare the attribute name
      compareResult = compare(getParameterName(), param2.getParameterName());
    }

    return compareResult;
  }

  /**
   * @param obj1 BigDecimal
   * @param obj2 BigDecimal
   * @return compare result
   */
  private int compareBigDecimal(final BigDecimal obj1, final BigDecimal obj2) {
    if ((obj1 == null) && (obj2 == null)) {
      // both object are NULL => return EQUAL
      return 0;
    }
    if (obj1 == null) {
      // first object is NULL => return LESS THAN
      return -1;
    }
    if (obj2 == null) {
      // second object is NULL => return GREATER THAN
      return 1;
    }
    return obj1.compareTo(obj2);
  }

  /**
   * Compare two boolean values Consider TRUE as less than FALSE this will order TRUE to top in ascending order
   *
   * @param value1 value1
   * @param value2 value2
   * @return compare result
   */
  public static int compareBoolean(final boolean value1, final boolean value2) {
    if (value1 == value2) {
      return 0;
    }
    else if (value1) {
      return -1;
    }
    else {
      return 1;
    }
  }

  /**
   * @return ruleId
   */
  public BigDecimal getRuleId() {
    return this.ruleId;
  }


  /**
   * Compare two String values Consider a NULL value as LESS THAN a not NULL value.
   *
   * @param name String
   * @param name2 String
   * @return compare result
   */
  public static int compare(final String name, final String name2) {

    if ((name == null) && (name2 == null)) {
      // both Strings are NULL => return EQUAL
      return 0;
    }
    else if ((name == null)) {
      // first String is NULL => return LESS THAN
      return -1;
    }
    else if ((name2 == null)) {
      // second String is NULL => return GREATER THAN
      return 1;
    }
    else {
      // both String are not NULL, compare them
      final Collator collator = Collator.getInstance(Locale.GERMAN);
      collator.setStrength(Collator.SECONDARY);
      return collator.compare(name, name2);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }

  /**
   * ICDM-724
   *
   * @return the refValueDCMString
   */
  public String getRefValueDCMString() {
    return this.refValueCalData == null ? null : DcmFromCalData.getDcmString(this.refValueCalData);
  }

  /**
   * ICDM-724
   *
   * @return Display string of Reference string
   */
  public String getRefValueDispString() {
    String refValString = "";
    // VALUE type label
    if ((compare(getValueType(), VALUE_TEXT) == 0) && (getRefValueCalData() == null)) {
      refValString = getRefValue() == null ? "" : getRefValue().toString();
    }
    // Complex type labels
    else if (getRefValueCalData() != null) {
      refValString = getRefValueCalData() == null ? "" : getRefValueCalData().getCalDataPhy().getSimpleDisplayValue();
    }
    return refValString;
  }

  /**
   * ICDM-724
   *
   * @return the refValue
   */
  public CalData getRefValueCalData() {
    return this.refValueCalData;
  }

  /**
   * @param calData CalData
   */
  public void setRefValCalData(final CalData calData) {
    this.refValueCalData = calData;
  }

  /**
   * ICDM-724
   *
   * @param refValueDCMString the refValueDCMString to set
   */
  public void setRefValueDCMString(final String refValueDCMString) {
    this.refValueCalData = dcmToCalData(refValueDCMString, getParameterName());
  }

  /**
   * Converts DCM String to CalData object
   *
   * @param dcmStr DCM String
   * @param paramName1 Parameter name
   * @return CalData
   */
  public CalData dcmToCalData(final String dcmStr, final String paramName1) {

    if (dcmStr != null) {
      final DcmParser parser = new DcmParser(SSDiCDMInterfaceLogger.getLogger());
      try {
        parser.parse(dcmStr);

        return getCalDataObj(paramName1, parser);
      }
      catch (DCMParserException e) {
        ExceptionUtils.createAndThrowException(e,
            "Error parsing DCM String for parameter : " + paramName1 + "-" + e.getMessage(),
            SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION,true);
      }
    }
    return null;
  }


  /**
   * @param paramName1
   * @param parser
   * @return
   */
  private CalData getCalDataObj(final String paramName1, final DcmParser parser) {
    // SSD-337
    CalData calDataObj = null;
    String paramFullName = paramName1;
    if (parser.getCalDataMap() != null) {
      for (String lblName : parser.getCalDataMap().keySet()) {
        calDataObj = parser.getCalDataMap().get(lblName);
      }

      /**
       * To ensure if the CalData obj name matches parameter name that is passed. If not replace Caldata Obj name with
       * parameter name to support variant code
       */
      // included as
      // requested by
      // Ravi from
      // iCDM Team
      // added to handle DCM for variant coded classse -- SSD-382
      if ((calDataObj != null) && !(calDataObj.getShortName().equalsIgnoreCase(paramFullName)) &&
          (paramFullName != null) && (paramFullName.indexOf(SQUARE_PARANTHESIS_OPEN) != -1)) {
        String p = paramFullName.substring(paramFullName.indexOf(SQUARE_PARANTHESIS_OPEN),
            paramFullName.indexOf(SQUARE_PARANTHESIS_CLOSE) + 1);
        paramFullName = paramFullName.replace(p, "");
        if (paramFullName.contains(calDataObj.getShortName())) {
          calDataObj.setShortName(paramName1);
          calDataObj.getCalDataPhy().setName(paramName1);
        }
      }
    }
    return calDataObj;
  }

  /**
   * @return the dcm2ssd
   */
  public boolean isDcm2ssd() {
    return this.dcm2ssd;
  }

  /**
   * @param dcm2ssd the dcm2ssd to set
   */
  public void setDcm2ssd(final boolean dcm2ssd) {
    this.dcm2ssd = dcm2ssd;
  }

  /**
   * @return the ruleCreatedUser
   */
  public String getRuleCreatedUser() {
    return this.ruleCreatedUser;
  }

  /**
   * @param ruleCreatedUser the ruleCreatedUser to set
   */
  public void setRuleCreatedUser(final String ruleCreatedUser) {
    this.ruleCreatedUser = ruleCreatedUser;
  }

  /**
   * @return the ruleCreatedDate
   */
  public Date getRuleCreatedDate() {
    if(Objects.nonNull(this.ruleCreatedDate)) {
    return (Date) this.ruleCreatedDate.clone();
  }
    return null;
  }

  /**
   * @param ruleCreatedDate the ruleCreatedDate to set
   */
  public void setRuleCreatedDate(final Date ruleCreatedDate) {
    if (Objects.nonNull(ruleCreatedDate)) {
      this.ruleCreatedDate = (Date) ruleCreatedDate.clone();
    }
  }


  /**
   * @return the dependencyList
   */
  public List<FeatureValueModel> getDependencyList() {
    return new ArrayList<>(this.dependencyList);
  }


  /**
   * @param dependencyList the dependencyList to set
   */
  public void setDependencyList(final List<FeatureValueModel> dependencyList) {
    this.dependencyList = new ArrayList<>(dependencyList);
  }


  /**
   * @return the maturityLevel
   */
  public String getMaturityLevel() {
    return this.maturityLevel;
  }


  /**
   * @param maturityLevel the maturityLevel to set
   */
  public void setMaturityLevel(final String maturityLevel) {
    this.maturityLevel = maturityLevel;
  }

  /**
   * iCDM-713 <br>
   * Returns attr-val dependencies of rule in formatted string eg: ( Air Condition -> TRUE)
   *
   * @return the ruleDependencyStr
   */
  public String getDependenciesForDisplay() {
    return this.ruleDependencyStr;
  }

  /**
   * @param dependencyDisplayStr the formattedDepenStr to set
   */
  public void setDependenciesForDisplay(final String dependencyDisplayStr) {
    this.ruleDependencyStr = dependencyDisplayStr;
  }

  /**
   * Rule is complete if any one of the following conditions is satisfied.
   * <p>
   * a) upper limit or lower limit is available<br>
   * b) reference value is available and exact match flag is true
   *
   * @return true if rule is complete
   */
  public boolean isRuleComplete() {

    if ((null != getLowerLimit()) || (null != getUpperLimit()) ||
        ((null != getBitWiseRule()) && !getBitWiseRule().isEmpty())) {
      return true;
    }
    return (isDcm2ssd() && ((null != getRefValue()) || (null != getRefValueCalData())));
  }


  /**
   * @return the formulaDesc
   */
  public String getFormulaDesc() {
    return this.formulaDesc;
  }

  /**
   * @return true if rule is complex
   */
  public boolean isRuleComplex() {
    return ((null != this.formulaDesc) && this.formulaDesc.startsWith(COMPLEX_RULE_PREFIX));
  }

  // ALM-277855--method changed based on request frim iCDM refer the mail attached with this ticket
  // ALM-277855
  /**
   * @param formulaDesc the formulaDesc to set
   */
  public void setFormulaDesc(final String formulaDesc) {
    if (formulaDesc != null) {
      this.formulaDesc = formulaDesc.trim();


      if (this.formulaDesc.toUpperCase(Locale.ENGLISH).contains(BIT)) {
        this.bitWiseRule = getIcdmBitRule();
      }
      else {
        this.bitWiseRule = COMPLEX_RULE;
      }
    }
  }


  /**
   * @return the formula
   */
  public String getFormula() {
    return this.formula;
  }


  /**
   * @param formula the formula to set
   */
  public void setFormula(final String formula) {
    this.formula = formula;
  }

  /**
   * @return the bitWiseRule
   */
  public String getBitWiseRule() {
    return this.bitWiseRule;
  }


  /**
   * @param bitWiseRule the bitWiseRule to set
   */
  public void setBitWiseRule(final String bitWiseRule) {
    this.bitWiseRule = bitWiseRule;
    if ((null == this.formulaDesc) || this.formulaDesc.trim().toUpperCase(Locale.ENGLISH).contains(BIT)) {
      if (null != bitWiseRule) {
        this.formulaDesc = getSSDBitRule(bitWiseRule, this.paramName);
      }
      else {
        this.bitWiseRule = "";
        this.formulaDesc = null;
      }
    }
  }


  /**
   * @param icdmBitRule rule
   * @param parameterName name
   * @return String
   */
  public String getSSDBitRule(final String icdmBitRule, final String parameterName) {
    String icdmBit = new StringBuilder(icdmBitRule).reverse().toString();
    StringBuilder ssdBitRule = new StringBuilder();
    String rule = "";
    int bitIndex = 0;
    int count;
    if (icdmBitRule.contains("0") || icdmBit.contains("1")) {
      ssdBitRule.append(parameterName).append(BIT);
      for (count = 0; count <= 38; count++) {
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
   * @return icdm format bitwise rule
   */
  public String getIcdmBitRule() {

    if (null != this.formulaDesc) {
      String[] bits = new String[32];
      for (int index = 0; index < 32; index++) {
        bits[index] = "X";
      }
      String[] bitVal = this.formulaDesc.split("[\\(\\)]");
      String[] bitPosition = bitVal[1].split(",");
      for (String element : bitPosition) {
        String[] bitVals = element.split(":");
        bits[Integer.parseInt(bitVals[0])] = bitVals[1];
      }
      StringBuilder bitValue = new StringBuilder();
      int bitSpace = 0;
      for (int index = 0; index < 32; index++) {
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
   * TO check if the current CDRRule is based on a variant coded label
   * 
   * @return boolean
   */
  public boolean isVarCodedParameterRule() {
    return Objects.nonNull(getParameterName()) && getParameterName().contains("[");
  }
}

