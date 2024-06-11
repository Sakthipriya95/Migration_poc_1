/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.caldataimport;

import java.util.SortedSet;

import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * Import data comparison object
 *
 * @author bne4cob
 */
public class CalDataImportComparisonModel implements Comparable<CalDataImportComparisonModel> {

  private static final int HASH_CODE_PRIME_31 = 0;

  /**
   * Parameter name
   */
  private String paramName;

  /**
   * Parameter type
   */
  private String paramType;
  /**
   * Function names
   */
  private String funcNames;
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
   * Old rule
   */
  private ReviewRule oldRule;

  /**
   * New Rule
   */
  private ReviewRule newRule;

  /**
   * If true, the record will be updated in database.
   */
  private boolean updateInDB;


  private SortedSet<AttributeValueModel> dependencyList;

  private boolean ruleDetailsUpdated;


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

  private String newUnit;

  private String newRemarks;

  private byte[] newRefValue;


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
                            * Func Name
                            */
                           SORT_FUNC_NAME,
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
   * @param newRefValue the newRefValue to set
   */
  public void setNewRefValue(final byte[] newRefValue) {
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
   * @return the newRefValue
   */
  public byte[] getNewRefValue() {
    return this.newRefValue;
  }

  /**
   * @return the updateInDB
   */
  public boolean isUpdateInDB() {
    return this.updateInDB;
  }


  /**
   * @param updateInDB the UpdateInDB flag to set
   */
  public void setUpdateInDB(final boolean updateInDB) {
    this.updateInDB = updateInDB;
  }


  /**
   * @param infoDecorNeeded the infoDecorNeeded to set
   */
  public void setInfoDecorNeeded(final boolean infoDecorNeeded) {
    this.infoDecorNeeded = infoDecorNeeded;
  }


  /**
   * @param exactMatchEditable the exactMatchEditable to set
   */
  public void setExactMatchEditable(final boolean exactMatchEditable) {
    this.exactMatchEditable = exactMatchEditable;
  }

  /**
   * @return the oldRule
   */
  public ReviewRule getOldRule() {
    return this.oldRule;
  }


  /**
   * @param oldRule the oldRule to set
   */
  public void setOldRule(final ReviewRule oldRule) {
    this.oldRule = oldRule;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASH_CODE_PRIME_31 * result) + ((getParamName() == null) ? 0 : getParamName().hashCode());
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
    CalDataImportComparisonModel other = (CalDataImportComparisonModel) obj;
    return getParamName().equals(other.getParamName());
  }


  /**
   * @return the new unit from the import file
   */
  public String getNewUnit() {
    return this.newUnit;
  }


  /**
   * @return the old remarks of the parameter
   */
  public String getNewRemarks() {
    return this.newRemarks;
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
  public ReviewRule getNewRule() {
    return this.newRule;
  }


  /**
   * @param newRule the newRule to set
   */
  public void setNewRule(final ReviewRule newRule) {
    this.newRule = newRule;
  }


  /**
   * @return the useNewRvwMtd
   */
  public boolean isUseNewRvwMtd() {
    return this.useNewRvwMtd;
  }


  /**
   * @param paramName the paramName to set
   */
  public void setParamName(final String paramName) {
    this.paramName = paramName;
  }


  /**
   * @param paramType the paramType to set
   */
  public void setParamType(final String paramType) {
    this.paramType = paramType;
  }


  /**
   * @param dependencyList the dependencyList to set
   */
  public void setDependencyList(final SortedSet<AttributeValueModel> dependencyList) {
    this.dependencyList = dependencyList;
  }


  /**
   * @param newUnit the newUnit to set
   */
  public void setNewUnit(final String newUnit) {
    this.newUnit = newUnit;
  }


  /**
   * @param newRemarks the newRemarks to set
   */
  public void setNewRemarks(final String newRemarks) {
    this.newRemarks = newRemarks;
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
  public SortedSet<AttributeValueModel> getDependencyList() {
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

  /**
   * Default comparison using parameter name as first level and type as second level
   * <p>
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CalDataImportComparisonModel other) {
    int result = ModelUtil.compare(getParamName(), other.getParamName());
    if (result == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      result = ModelUtil.compare(getParamType(), other.getParamType());
    }
    if (result == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      if ((null == getOldRule()) || (null == other.getOldRule())) {
        result = -1;
      }
      else {
        result = ModelUtil.compare(getOldRule().getRuleId(), other.getOldRule().getRuleId());
      }
    }
    return result;
  }


  /**
   * @return the funcNames
   */
  public String getFuncNames() {
    return this.funcNames;
  }


  /**
   * @param funcNames the funcNames to set
   */
  public void setFuncNames(final String funcNames) {
    this.funcNames = funcNames;
  }

}
