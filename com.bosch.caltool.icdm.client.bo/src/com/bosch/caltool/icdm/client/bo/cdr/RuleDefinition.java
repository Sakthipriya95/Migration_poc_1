/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldataphy.CalDataPhy;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;


/**
 * @author adn1cob
 */
public class RuleDefinition implements Comparable<RuleDefinition> {

  /**
   * Important ensure to maintain Column order as enum.values() method is used in relativeCompareTo()
   */
  public enum SortColumns {
                           /**
                            * Lower limit
                            */
                           SORT_LOWER_LIMIT,
                           /**
                            * Upper limit
                            */
                           SORT_UPPER_LIMIT,
                           /**
                            * bitiwse limit
                            */
                           SORT_BITWISE,
                           /**
                            * Reference Value
                            */
                           SORT_REFERENCE_VALUE,
                           /**
                            * Exact match
                            */
                           SORT_EXACT_MATCH,
                           /**
                            * Unit
                            */
                           SORT_UNIT,
                           /**
                            * Review Method
                            */
                           SORT_REVIEW_METHOD,
                           /**
                            * Reference Value
                            */
                           SORT_REF_CHECKSUM_VALUE;

  }

  /**
   * Parameter name
   */
  private String paramName;
  /**
   * Lower Limit
   */
  private BigDecimal lowerLimit;
  /**
   * Upper Limit
   */
  private BigDecimal upperLimit;
  /**
   * Upper Limit
   */
  private String bitWise;

  /**
   * Reference Value
   */
  private String refValueDisplayString;
  /**
   * Review method
   */
  private String reviewMethod = "M";

  /**
   * exactMatch flag
   */
  private boolean exactMatch;
  /**
   * maturity level of the label
   */
  private String maturityLevel;
  /**
   * Text constant for VALUE
   */
  public static final String VALUE_TEXT = "VALUE";

  /**
   * HASHCODE_PRIME constant
   */
  private static final int HASHCODE_PRIME = 31;

  /**
   * whether default rule or not
   */
  private boolean isDefaultRuleFlag;

  /**
   * CDRRule instance
   */
  private ReviewRule defaultRule;

  /**
   * Unit
   */
  private String unit;
  /**
   * CDRRule mapping based on AttributeValue combination
   */
  private Map<String, ReviewRule> ruleMapping = new HashMap<>();
  /**
   * THis map has the column index as key and maturity level as value
   */
  private final Map<Integer, String> ruleMappedIndex = new HashMap<>();
  /**
   * Reference Value
   */
  private BigDecimal refValue;
  /**
   * Reference Value for complex types
   */
  private CalData refValueCalData;
  /**
   * Parameter Type
   */
  private String valueType;

  /**
   * 491086 - Defect fix - Validate properties view for Normal Rule in Parameter Rule Page in Rule Editor. user who
   * created the rule
   */
  private String ruleCreatedUser;

  /**
   * 491086 - Defect fix - Validate properties view for Normal Rule in Parameter Rule Page in Rule Editor. date when
   * rule got created
   */
  private Date ruleCreatedDate;

  private String formulaDesc;


  /**
   * @return the paramName
   */
  public String getParamName() {
    return this.paramName;
  }

  /**
   * @param paramName the paramName to set
   */
  public void setParamName(final String paramName) {
    this.paramName = paramName;
  }

  /**
   * @return the lowerLimit
   */
  public BigDecimal getLowerLimit() {
    return this.lowerLimit;
  }

  /**
   * @param lowerLimit the lowerLimit to set
   */
  public void setLowerLimit(final BigDecimal lowerLimit) {
    this.lowerLimit = lowerLimit;
  }

  /**
   * @return the upperLimit
   */
  public BigDecimal getUpperLimit() {
    return this.upperLimit;
  }

  /**
   * @param upperLimit the upperLimit to set
   */
  public void setUpperLimit(final BigDecimal upperLimit) {
    this.upperLimit = upperLimit;
  }

  /**
   * @return the refValue
   */
  public String getRefValueDisplayString() {
    return this.refValueDisplayString;
  }

  /**
   * @param refValue the refValue to set
   */
  public void setRefValueDisplayString(final String refValue) {
    this.refValueDisplayString = refValue;
  }

  /**
   * @return the ruleMapping
   */
  public Map<String, ReviewRule> getRuleMapping() {
    return this.ruleMapping;
  }

  /**
   * @param ruleMapping the ruleMapping to set
   */
  public void setRuleMapping(final Map<String, ReviewRule> ruleMapping) {
    this.ruleMapping = ruleMapping;
  }

  /**
   * @return the ruleMappedIndex
   */
  public Map<Integer, String> getRuleMappedIndex() {
    return this.ruleMappedIndex;
  }


  /**
   * @return the reviewMethod
   */
  public String getReviewMethod() {
    return this.reviewMethod;
  }


  /**
   * @return the unit
   */
  public String getUnit() {
    return this.unit;
  }


  /**
   * @param unit the unit to set
   */
  public void setUnit(final String unit) {
    this.unit = unit;
  }


  /**
   * @param reviewMethod the reviewMethod to set
   */
  public void setReviewMethod(final String reviewMethod) {
    this.reviewMethod = reviewMethod;
  }


  /**
   * @return the exactMatch
   */
  public boolean isExactMatch() {
    return this.exactMatch;
  }


  /**
   * @param exactMatch the exactMatch to set
   */
  public void setExactMatch(final boolean exactMatch) {
    this.exactMatch = exactMatch;
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
   * @return the refValue
   */
  public BigDecimal getRefValue() {
    return this.refValue;
  }


  /**
   * @param refValue the refValue to set
   */
  public void setRefValue(final BigDecimal refValue) {
    this.refValue = refValue;
  }


  /**
   * @return the refValueCalData
   */
  public CalData getRefValueCalData() {
    return this.refValueCalData;
  }


  /**
   * @param refValueCalData the refValueCalData to set
   */
  public void setRefValueCalData(final CalData refValueCalData) {
    this.refValueCalData = refValueCalData;
  }


  /**
   * @return the valueType
   */
  public String getValueType() {
    return this.valueType;
  }


  /**
   * @param valueType the valueType to set
   */
  public void setValueType(final String valueType) {
    this.valueType = valueType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }
    return ruleDefnEquals(obj);
  }

  /**
   * @param obj
   */
  private boolean ruleDefnEquals(final Object obj) {
    RuleDefinition ruleDefn = (RuleDefinition) obj;
    return (compareValueAndRuleDefnFields(ruleDefn) || compareRuleDefnFields(ruleDefn));
  }

  private boolean compareValueAndRuleDefnFields(final RuleDefinition ruleDefn) {
    return ruleDefn.getValueType().equalsIgnoreCase(VALUE_TEXT) && compareLimitAndBitWiseFields(ruleDefn) &&
        compareUnitAndRevMethod(ruleDefn) && compareDefRuleAndRefValue(ruleDefn);
  }

  private boolean compareLimitAndBitWiseFields(final RuleDefinition ruleDefn) {
    return compareLimit(ruleDefn) && compareBitWiseAndExactMatchFields(ruleDefn);
  }

  private boolean compareDefRuleAndRefValue(final RuleDefinition ruleDefn) {
    return (ApicUtil.compareStringAndNum(getRefValueDisplayString(), ruleDefn.getRefValueDisplayString()) == 0) &&
        (ruleDefn.isDefaultRule() == isDefaultRule());
  }

  private boolean compareBitWiseAndExactMatchFields(final RuleDefinition ruleDefn) {
    return CommonUtils.isEqual(ruleDefn.getBitWise(), getBitWise()) &&
        CommonUtils.isEqual(ruleDefn.isExactMatch(), isExactMatch());
  }

  private boolean compareUnitAndRevMethod(final RuleDefinition ruleDefn) {
    return CommonUtils.isEqual(ruleDefn.getUnit(), getUnit()) &&
        CommonUtils.isEqual(ruleDefn.getReviewMethod(), getReviewMethod());
  }

  private boolean compareLimit(final RuleDefinition ruleDefn) {
    return CommonUtils.isEqual(ruleDefn.getLowerLimit(), getLowerLimit()) &&
        CommonUtils.isEqual(ruleDefn.getUpperLimit(), getUpperLimit());
  }

  private boolean compareRuleDefnFields(final RuleDefinition ruleDefn) {
    return compareLimitAndBitWiseFields(ruleDefn) && compareUnitAndRevMethod(ruleDefn) &&
        (compareRefValueCmplxType(ruleDefn)) && compareDefRuleAndRefValue(ruleDefn);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    int refValueHC = 0;
    // Added if condition to relate hashcode to equals method which uses Ref CalData when value type is not text
    if (!getValueType().equalsIgnoreCase(VALUE_TEXT) && (getRefValueCalData() != null)) {
      refValueHC = getRefValueCalData().hashCode();
    }
    result = (HASHCODE_PRIME * result) + ((getLowerLimit() == null) ? 0 : getLowerLimit().hashCode()) +
        ((getUpperLimit() == null) ? 0 : getUpperLimit().hashCode()) + Boolean.valueOf(isExactMatch()).hashCode() +
        ((getRefValueDisplayString() == null) ? 0 : getRefValueDisplayString().hashCode()) +
        ((getBitWise() == null) ? 0 : getBitWise().hashCode()) + refValueHC +
        ((getUnit() == null) ? 0 : getUnit().hashCode()) +
        ((getReviewMethod() == null) ? 0 : getReviewMethod().hashCode()) + Boolean.valueOf(isDefaultRule()).hashCode();
    return result;
  }


  /**
   * @param ruleDefn parameter to be compared with
   * @param sortColumn name of the sortColumn
   * @return int
   */
  public int compareTo(final RuleDefinition ruleDefn, final SortColumns sortColumn) { // NOPMD

    int compareResult;

    switch (sortColumn) {

      case SORT_LOWER_LIMIT:
        // compare the lower limits
        compareResult = ApicUtil.compareBigDecimal(getLowerLimit(), ruleDefn.getLowerLimit());
        break;
      case SORT_UPPER_LIMIT:
        // comparing the upper limits
        compareResult = ApicUtil.compareBigDecimal(getUpperLimit(), ruleDefn.getUpperLimit());
        break;
      case SORT_BITWISE:
        compareResult = ApicUtil.compare(getBitWise(), ruleDefn.getBitWise());
        break;
      case SORT_REFERENCE_VALUE:
        compareResult = compareRefValue(ruleDefn);
        break;
      // SORT_REF_CHECKSUM_VALUE is used to differentiate RefValue CalDataObjects
      // The getCheckSum() method of CalDataPhy objects is used in the compareTo method as CalDataPhy does'nt implement
      // comparable
      case SORT_REF_CHECKSUM_VALUE:
        compareResult = compareCheckSum(ruleDefn);
        break;
      case SORT_UNIT:
        compareResult = ApicUtil.compare(getUnit(), ruleDefn.getUnit());
        break;
      case SORT_REVIEW_METHOD:
        // comparing the review methods using string compare
        compareResult = ApicUtil.compare(getReviewMethod(), ruleDefn.getReviewMethod());
        break;
      case SORT_EXACT_MATCH:
        compareResult = ApicUtil.compareBoolean(isExactMatch(), ruleDefn.isExactMatch());
        break;
      default:
        // Compare name
        compareResult = compareTo(ruleDefn);
        break;

    }

    return compareResult;
  }

  /**
   * @param ruleDefn
   * @param compareResult
   * @return
   */
  private int compareCheckSum(final RuleDefinition ruleDefn) {
    int compareResult = 0;
    if (!ruleDefn.getValueType().equalsIgnoreCase(VALUE_TEXT)) {
      compareResult = ApicUtil.compare(getRefValueDisplayString(), ruleDefn.getRefValueDisplayString());
      if (compareResult == 0) {
        boolean isEquals = compareRefValueCmplxType(ruleDefn);
        if (!isEquals) {
          compareResult = compareUsingCalDataPhyCheckSum(ruleDefn);
        }
      }
    }
    return compareResult;
  }

  /**
   * @param ruleDefn
   * @return
   */
  private int compareRefValue(final RuleDefinition ruleDefn) {
    int compareResult;
    if (ruleDefn.getValueType().equalsIgnoreCase(VALUE_TEXT)) {
      compareResult = ApicUtil.compareStringAndNum(getRefValueDisplayString(), ruleDefn.getRefValueDisplayString());
    }
    else {
      compareResult = ApicUtil.compare(getRefValueDisplayString(), ruleDefn.getRefValueDisplayString());
    }
    return compareResult;
  }

  /**
   * @param ruleDefn
   * @return
   */
  private int compareUsingCalDataPhyCheckSum(final RuleDefinition ruleDefn) {
    int compareResult;
    CalDataPhy calDataPhy1;
    long caldataPhy1CheckSum = 0L;
    if (null != getRefValueCalData()) {
      calDataPhy1 = getRefValueCalData().getCalDataPhy();
      if (CommonUtils.isNotNull(calDataPhy1)) {
        caldataPhy1CheckSum = calDataPhy1.getChecksum();
      }
    }
    CalDataPhy calDataPhy2;
    long caldataPhy2CheckSum = 0L;
    if (null != ruleDefn.getRefValueCalData()) {
      calDataPhy2 = ruleDefn.getRefValueCalData().getCalDataPhy();
      if (!CommonUtils.isNotNull(calDataPhy2)) {
        caldataPhy2CheckSum = calDataPhy2.getChecksum();
      }
    }
    compareResult = ApicUtil.compareLong(caldataPhy1CheckSum, caldataPhy2CheckSum);
    return compareResult;
  }

  /**
   * @param ruleDefn
   * @return
   */
  private boolean compareRefValueCmplxType(final RuleDefinition ruleDefn) {
    CalDataPhy comp1 = null;
    if (null != getRefValueCalData()) {
      comp1 = getRefValueCalData().getCalDataPhy();
    }
    CalDataPhy comp2 = null;
    if (null != ruleDefn.getRefValueCalData()) {
      comp2 = ruleDefn.getRefValueCalData().getCalDataPhy();
    }
    return CommonUtils.isEqual(comp1, comp2);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final RuleDefinition ruleDefn) {
    if (isDefaultRule()) {
      return -1;
    }
    else if (ruleDefn.isDefaultRule()) {
      return 1;
    }
    int compResult = 0;
    compResult = ruleDefnCompareTo(ruleDefn, compResult);

    return compResult;
  }

  /**
   * @param ruleDefn
   * @param compResult
   * @return
   */
  private int ruleDefnCompareTo(final RuleDefinition ruleDefn, final int compResult) {
    int result = compResult;
    if (result == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      result = compareTo(ruleDefn, SortColumns.SORT_LOWER_LIMIT);
    }
    if (result == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      result = compareTo(ruleDefn, SortColumns.SORT_UPPER_LIMIT);
    }
    if (result == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      result = compareTo(ruleDefn, SortColumns.SORT_BITWISE);
    }
    if (result == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      result = compareTo(ruleDefn, SortColumns.SORT_REFERENCE_VALUE);
    }
    if (result == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      result = compareTo(ruleDefn, SortColumns.SORT_EXACT_MATCH);
    }
    if (result == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      result = compareTo(ruleDefn, SortColumns.SORT_UNIT);
    }
    if (result == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      result = compareTo(ruleDefn, SortColumns.SORT_REVIEW_METHOD);
    }
    // Compare using CheckSum of Reference value as the last step
    if (result == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      result = compareTo(ruleDefn, SortColumns.SORT_REF_CHECKSUM_VALUE);
    }
    return result;
  }


  /**
   * @param ruleDefn parameter to be compared with
   * @param sortColumn name of the sortColumn
   * @return compareResult
   */
  public int relativeCompareTo(final RuleDefinition ruleDefn, final SortColumns sortColumn) { // NOPMD

    int compResult;
    compResult = compareTo(ruleDefn, sortColumn);
    if (compResult == 0) {
      for (SortColumns sortCol : SortColumns.values()) {
        if (sortCol != sortColumn) {
          compResult = compareTo(ruleDefn, sortCol);
          if (compResult == 0) {
            continue;
          }
          break;
        }
      }
    }

    return compResult;

  }

  /**
   * @return the isDefaultRule
   */
  public boolean isDefaultRule() {
    return this.isDefaultRuleFlag;
  }


  /**
   * @param isDefaultRule the isDefaultRule to set
   */
  public void setDefaultRule(final boolean isDefaultRule) {
    this.isDefaultRuleFlag = isDefaultRule;
  }


  /**
   * @return the defaultRule
   */
  public ReviewRule getDefaultRule() {
    return this.defaultRule;
  }


  /**
   * @param defaultRule the defaultRule to set
   */
  public void setDefaultRule(final ReviewRule defaultRule) {
    this.defaultRule = defaultRule;
  }

  /**
   * @return the bitWise
   */
  public String getBitWise() {
    return this.bitWise;
  }


  /**
   * @param bitWise the bitWise to set
   */
  public void setBitWise(final String bitWise) {
    this.bitWise = bitWise;
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
    return this.ruleCreatedDate;
  }


  /**
   * @param ruleCreatedDate the ruleCreatedDate to set
   */
  public void setRuleCreatedDate(final Date ruleCreatedDate) {
    this.ruleCreatedDate = ruleCreatedDate;
  }


  /**
   * @return the formulaDesc
   */
  public String getFormulaDesc() {
    return this.formulaDesc;
  }


  /**
   * @param formulaDesc the formulaDesc to set
   */
  public void setFormulaDesc(final String formulaDesc) {
    this.formulaDesc = formulaDesc;
  }
}
