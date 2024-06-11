/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.caldataimport;

import java.math.BigDecimal;
import java.util.List;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.apic.jpa.bo.RuleMaturityLevel;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.READY_FOR_SERIES;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.FeatureValueModel;


/**
 * Import data comparison object
 *
 * @author bne4cob
 */
public class CalDataImportComparison implements Comparable<CalDataImportComparison> {

  /**
   * Parameter name
   */
  private final String paramName;

  /**
   * Parameter type
   */
  private final String paramType;

  /**
   * Parameter dependency
   */
  private String paramDependency;

  /**
   * New exact match
   */
  private boolean newExactMatch;
  /**
   * New ready for series
   */
  private String newReayForSeries;

  /**
   * New reference value from object
   */
  private CalData newRefValue;

  /**
   * Old rule
   */
  private CDRRule oldRule;

  /**
   * New Rule
   */
  private CDRRule newRule;

  /**
   * If true, the record will be updated in database.
   */
  private boolean updateInDB;


  private final List<FeatureValueModel> dependencyList;

  private boolean ruleDetailsUpdated;

  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;
  /**
   *
   */
  private boolean useNewRvwMtd = true;

  /**
   *
   */
  private boolean useNewRefVal = true;
  /**
   *
   */
  private boolean useNewUnit = true;
  /**
   *
   */
  private boolean useNewMaturityLvl = true;

  /**
   *
   */
  private boolean useNewLowLmt = true;


  /**
   *
   */
  private boolean useNewUpLmt = true;

  /**
   * boolean to indicate whether an info decor is needed for remarks
   */
  private boolean infoDecorNeeded;

  /**
   * boolean to indicate whether exact match is editable
   */
  private boolean exactMatchEditable;


  /**
   * Sort columns
   */
  public enum SortColumns {
                           /**
                            * Parameter Type
                            */
                           // ICDM-2201
                           SORT_PARAM_TYPE,
                           /**
                            * Parameter Name
                            */
                           SORT_PARAM_NAME,
                           /**
                            * Parameter dependency
                            */
                           SORT_DEPENDENCY,
                           /**
                            * rule exists
                            */
                           SORT_RULE_EXIST,
                           /**
                            * Update
                            */
                           SORT_UPDATE_FLAG;


  }

  /**
   * @param paramName param Name
   * @param paramType param Type
   * @param dependencyList dependency for the rule
   */
  public CalDataImportComparison(final String paramName, final String paramType,
      final List<FeatureValueModel> dependencyList) {
    super();
    this.paramName = paramName;
    this.paramType = paramType;
    this.newReayForSeries = ApicConstants.READY_FOR_SERIES.NO.getDbType();
    this.dependencyList = dependencyList;
  }


  /**
   * @return the newExactMatch
   */
  public boolean isNewExactMatch() {
    return this.newExactMatch;
  }

  /**
   * @param newExactMatch the newExactMatch to set
   */
  public void setNewExactMatch(final boolean newExactMatch) {
    this.newExactMatch = newExactMatch;
  }

  /**
   * @return the newReviewType
   */
  public String getNewReadyForSeries() {
    return this.newReayForSeries;
  }

  /**
   * @param newReviewType the newReviewType to set
   */
  public void setNewReadyForSeries(final String newReviewType) {
    this.newReayForSeries = newReviewType;
  }

  /**
   * @return the oldRefValue
   */
  public CalData getOldRefValue() {
    CalData oldRefValueCalData = this.oldRule == null ? null : this.oldRule.getRefValueCalData();
    if ((oldRefValueCalData == null) && (null != getOldRule()) && (CDRRule.VALUE_TEXT).equals(this.paramType) &&
        CommonUtils.isNotNull(getOldRule().getRefValue())) {
      oldRefValueCalData =
          getOldRule().dcmToCalData(CalDataUtil.createDCMStringForNumber(getOldRule().getParameterName(),
              getOldRule().getUnit(), getOldRule().getRefValue().toString()), getOldRule().getParameterName());
    }
    return oldRefValueCalData;
  }

  /**
   * @return the oldRefValue for display
   */
  public String getOldRefValueDisplay() {
    CalData value = getOldRefValue();
    return value == null ? "" : value.getCalDataPhy().getSimpleDisplayValue();
  }


  /**
   * @return the newRefValue
   */
  public CalData getNewRefValue() {
    CalData newRefValueCalData = this.newRule == null ? null : this.newRule.getRefValueCalData();
    if ((newRefValueCalData == null) && (null != getNewRule()) && (CDRRule.VALUE_TEXT).equals(this.paramType) &&
        CommonUtils.isNotNull(getNewRule().getRefValue())) {
      newRefValueCalData =
          getNewRule().dcmToCalData(CalDataUtil.createDCMStringForNumber(getNewRule().getParameterName(),
              getNewRule().getUnit(), getNewRule().getRefValue().toString()), getNewRule().getParameterName());
    }
    return newRefValueCalData;
  }

  /**
   * @return the oldRefValue for display
   */
  public String getNewRefValueDisplay() {
    CalData value = getNewRefValue();
    return value == null ? "" : value.getCalDataPhy().getSimpleDisplayValue();
  }

  /**
   * @param newRefValue the newRefValue to set
   */
  public void setNewRefValue(final CalData newRefValue) {
    this.newRefValue = newRefValue;
  }


  /**
   * @return the paramName
   */
  public String getParamName() {
    return this.paramName;
  }


  /**
   * @return the paramType
   */
  public String getParamType() {
    return this.paramType;
  }


  /**
   * @return the updateInDB
   */
  public boolean isUpdateInDB() {
    return this.updateInDB;
  }

  /**
   * @return true if insert rule
   */
  public boolean isInsertRule() {
    return this.oldRule == null;
  }

  /**
   * @param updateInDB the UpdateInDB flag to set
   */
  public void setUpdateInDB(final boolean updateInDB) {
    this.updateInDB = updateInDB;
  }


  /**
   * @return the oldRule
   */
  public CDRRule getOldRule() {
    return this.oldRule;
  }


  /**
   * @param oldRule the oldRule to set
   */
  public void setOldRule(final CDRRule oldRule) {
    this.oldRule = oldRule;
  }


  /**
   * Default comparison using parameter name as first level and type as second level
   * <p>
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CalDataImportComparison other) {
    int result = ApicUtil.compare(getParamName(), other.getParamName());
    if (result == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      result = ApicUtil.compare(getParamType(), other.getParamType());
    }
    if (result == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      if ((null == getOldRule()) || (null == other.getOldRule())) {
        result = -1;
      }
      else {
        result = ApicUtil.compare(getOldRule().getRuleId(), other.getOldRule().getRuleId());
      }
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result= (HASH_CODE_PRIME_31 * result) + ((getParamName() == null) ? 0 : getParamName().hashCode());
   return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    CalDataImportComparison other = (CalDataImportComparison) obj;
    return CommonUtils.isEqual(getParamName(), other.getParamName());
  }

  /**
   * @return the old unit of the parameter
   */
  public String getOldUnit() {
    return CommonUtils.checkNull(this.oldRule.getUnit());
  }

  /**
   * @return the new unit from the import file
   */
  public String getNewUnit() {
    return CommonUtils.checkNull(this.newRefValue.getCalDataPhy().getUnit());
  }

  /**
   * @return the old remarks of the parameter
   */
  public String getOldRemarks() {
    return CommonUtils.checkNull(this.oldRule.getHint());
  }


  /**
   * @return the old remarks of the parameter
   */
  public String getNewRemarks() {
    return CommonUtils.checkNull(CalDataUtil.getRemarks(this.newRefValue));
  }

  /**
   * Compare To implementation using sort columns
   *
   * @param other compare obj
   * @param sortColumn sortColumn
   * @return as per the standard compareTo implementation
   */
  public int compareTo(final CalDataImportComparison other, final SortColumns sortColumn) {
    int compareResult;
    switch (sortColumn) {
      case SORT_PARAM_TYPE:
        compareResult = ApicUtil.compare(getParamType(), other.getParamType());
        break;
      case SORT_PARAM_NAME:
        compareResult = ApicUtil.compare(getParamName(), other.getParamName());
        break;
      case SORT_DEPENDENCY:
        compareResult = ApicUtil.compareStringAndNum(getParamDependency(), other.getParamDependency());
        break;
      case SORT_RULE_EXIST:
        compareResult = ApicUtil.compareBoolean(isInsertRule(), other.isInsertRule());
        break;
      case SORT_UPDATE_FLAG:
        compareResult = ApicUtil.compareBoolean(isUpdateInDB(), other.isUpdateInDB());
        break;
      default:
        compareResult = 0;
        break;
    }

    // additional compare column is the name of the characteristic
    if (compareResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      // compare result is equal, compare the param name go with default comparison
      compareResult = compareTo(other);
    }
    return compareResult;
  }


  /**
   * @return the paramDependency
   */
  public String getParamDependency() {
    return this.paramDependency;
  }


  /**
   * @param paramDependency the paramDependency to set
   */
  public void setParamDependency(final String paramDependency) {
    this.paramDependency = paramDependency;
  }


  /**
   * @return the newRule
   */
  public CDRRule getNewRule() {
    return this.newRule;
  }


  /**
   * @param newRule the newRule to set
   */
  public void setNewRule(final CDRRule newRule) {
    this.newRule = newRule;
  }


  /**
   * initialise the hint
   *
   * @param defaultComment default comment from first page of wizard
   * @param maturityLvl Default maturity level
   * @param reviewMethod Review Method
   * @param exactMatch Default exact match flag
   */
  public void initialiseValues(final String defaultComment, final String maturityLvl,
      final READY_FOR_SERIES reviewMethod, final boolean exactMatch) {
    String oldHint = "";
    if (null != this.oldRule) {
      if (null != this.oldRule.getHint()) {
        // set the hint value
        oldHint = this.oldRule.getHint();
      }
      // set the use text box values based on values from old rule
      if (null != this.oldRule.getLowerLimit()) {
        this.useNewLowLmt = false;
      }
      if (null != this.oldRule.getUpperLimit()) {
        this.useNewUpLmt = false;
      }
      if (!CommonUtils.isEmptyString(this.oldRule.getRefValueDispString())) {
        this.useNewRefVal = false;
      }
      if (null != this.oldRule.getUnit()) {
        this.useNewUnit = false;
      }
      if (null != this.oldRule.getMaturityLevel()) {
        this.useNewMaturityLvl = false;
      }
      if (null != this.oldRule.getReviewMethod()) {
        this.useNewRvwMtd = false;
      }
    }

    // initialise Hint
    initialiseHint(defaultComment, oldHint);

    // initialise default values
    // maturity level
    if (null == this.newRule.getMaturityLevel()) {
      this.newRule.setMaturityLevel(RuleMaturityLevel.getSsdMaturityLevelText(maturityLvl));
    }
    // review method
    if (CommonUtils.isEqual(READY_FOR_SERIES.NOT_DEFINED.dbType, this.newRule.getReviewMethod())) {
      this.newRule.setReviewMethod(reviewMethod.getDbType());
    }

    // exact match
    setExactMatch(exactMatch);

    // validation for min and max values
    validateMinMaxValues();
  }


  /**
   * @param exactMatch
   */
  private void setExactMatch(final boolean exactMatch) {
    if (this.oldRule == null) {
      this.newRule.setDcm2ssd(exactMatch);// setting the default value in the new rule
    }
    else {
      this.newRule.setDcm2ssd(this.oldRule.isDcm2ssd());// set value from old rule
    }
    // find the reference value
    String refVal;
    if (this.useNewRefVal) {
      refVal = this.newRule.getRefValueDispString();
    }
    else {
      refVal = this.oldRule.getRefValueDispString();
    }
    this.exactMatchEditable = !CommonUtils.isEmptyString(refVal);
    if (CommonUtils.isEmptyString(refVal)) {
      // if there is no ref value , then set the exact match to false
      this.newRule.setDcm2ssd(false);
    }
  }


  /**
   * @param defaultComment String
   * @param oldHint String
   */
  private void initialiseHint(final String defaultComment, final String oldHint) {
    StringBuilder hint = new StringBuilder(ApicConstants.HINT_STRING_SIZE);
    String newHint = "";

    // append the new comment if it not existing already
    if (!CommonUtils.isEmptyString(this.newRule.getHint())) {
      newHint = this.newRule.getHint();
    }
    boolean appendNewHint = !CommonUtils.isEmptyString(newHint) && !CommonUtils.checkNull(oldHint).contains(newHint);
    boolean appendDefaultComment =
        !CommonUtils.isEmptyString(defaultComment) && !CommonUtils.checkNull(oldHint).contains(defaultComment);
    if (!CommonUtils.isEmptyString(oldHint)) {
      if (!oldHint.startsWith("iCDM:") && (appendNewHint || appendDefaultComment)) {
        hint.append("iCDM:\n");
      }
      hint.append(oldHint);
    }
    // if both old and new comments are present
    this.infoDecorNeeded = !CommonUtils.isEmptyString(oldHint) && (appendNewHint || appendDefaultComment);
    if (this.infoDecorNeeded) {
      hint.append("\n").append("Import - ");// insert line break if old hint is available
    }
    if (appendDefaultComment) {
      // Add Import only if default comment or new hint is present
      hint.append(defaultComment);
    }
    if (appendNewHint && appendDefaultComment) {
      hint.append("\n");
    }
    if (appendNewHint) {
      hint.append(newHint);
    }

    this.newRule.setHint(hint.toString());

  }

  /**
   * validating min and max values before UI
   */
  private void validateMinMaxValues() {
    BigDecimal lowerLimit = null;
    BigDecimal upperLimit = null;
    // find lower limit
    if (this.useNewLowLmt) {
      lowerLimit = this.newRule.getLowerLimit();
    }
    else if (null != this.oldRule) {
      lowerLimit = this.oldRule.getLowerLimit();
    }

    // find upper limit
    if (this.useNewUpLmt) {
      upperLimit = this.newRule.getUpperLimit();
    }
    else if (null != this.oldRule) {
      upperLimit = this.oldRule.getUpperLimit();
    }

    if (ApicUtil.compareBigDecimal(lowerLimit, upperLimit) > 0) {
      // do not use the new values, in case the lower limit is greater than upper limit
      this.useNewLowLmt = false;
      this.useNewUpLmt = false;
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
   * @return the ruleDetailsUpdated
   */
  public boolean isRuleDetailsUpdated() {
    return this.ruleDetailsUpdated;
  }


  /**
   * @param ruleDetailsUpdated the ruleDetailsUpdated to set
   */
  public void setRuleDetailsUpdated(final boolean ruleDetailsUpdated) {
    this.ruleDetailsUpdated = ruleDetailsUpdated;
  }


  /**
   * @return the dependencySet
   */
  public List<FeatureValueModel> getDependencyList() {
    return this.dependencyList;
  }

  /**
   * @return the infoDecorNeeded
   */
  public boolean isInfoDecorNeeded() {
    return this.infoDecorNeeded;
  }

  /**
   * @return the exactMatchEditable
   */
  public boolean isExactMatchEditable() {
    return this.exactMatchEditable;
  }


  /**
   * enable /disable exact match
   */
  public void setExactMatchEnable(final boolean enable) {
    this.exactMatchEditable = enable;
  }
}
