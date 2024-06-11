/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;

import com.bosch.calcomp.externallink.ILinkableObject;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParameter;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResultsSecondary;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.DELTA_REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.RESULT_FLAG;

/**
 * @author rgo7cob
 */
public class CDRResParamSecondary extends AbstractCdrObject
    implements Comparable<CDRResParamSecondary>, ILinkableObject {


  private CalData refValue;

  /**
   * Constructor
   *
   * @param dataProvider the data provider
   * @param objID resultID
   */
  protected CDRResParamSecondary(final CDRDataProvider dataProvider, final Long objID) {
    super(dataProvider, objID);
    dataProvider.getDataCache().getAllSecRvwPrams().put(objID, this);
  }


  /**
   * @return the cdr result parameter
   */
  public CDRResultParameter getResultParameter() {
    TRvwParameter tRvwParameter = getEntityProvider().getDbResParamSecondary(getID()).getTRvwParameter();
    return getDataProvider().getDataCache().getAllCDRResultParameters().get(tRvwParameter.getRvwParamId());
  }


  /**
   * @return the secondary Result
   */
  public CDRSecondaryResult getSecondaryResult() {
    TRvwResultsSecondary tRvwResultsSecondary =
        getEntityProvider().getDbResParamSecondary(getID()).getTRvwResultsSecondary();
    return getDataProvider().getDataCache().getAllCDRSecResults().get(tRvwResultsSecondary.getSecReviewId());
  }

  /**
   * @return lower limit
   */
  public BigDecimal getLowerLimit() {
    return getEntityProvider().getDbResParamSecondary(getID()).getLowerLimit();
  }

  /**
   * @return upper limit
   */
  public BigDecimal getUpperLimit() {
    return getEntityProvider().getDbResParamSecondary(getID()).getUpperLimit();
  }

  /**
   * @return the param has bitwise rule or not
   */
  public String isBitWise() {
    return getEntityProvider().getDbResParamSecondary(getID()).getIsbitwise();
  }

  /**
   * @return bitwise limit
   */
  public String getBitwiseLimit() {
    return getEntityProvider().getDbResParamSecondary(getID()).getBitwiseLimit();
  }

  /**
   * @return true if exact match flag set
   */
  public boolean isExactMatchRefValue() {
    if (ApicConstants.YES.equals(getEntityProvider().getDbResParamSecondary(getID()).getMatchRefFlag())) {
      return true;
    }
    return false;
  }

  /**
   * @return the string representation of Ready for series
   */
  public String getReadyForSeriesStr() {
    return getReadyForSeries().getUiType();
  }


  /**
   * @return the ready for series as object
   */
  public ApicConstants.READY_FOR_SERIES getReadyForSeries() {
    return ApicConstants.READY_FOR_SERIES.getType(getEntityProvider().getDbResParamSecondary(getID()).getRvwMethod());
  }


  /**
   * @return the reference unit
   */
  public String getReferenceUnit() {
    return getEntityProvider().getDbResParamSecondary(getID()).getRefUnit();
  }

  /**
   * Method to get Reference value object
   *
   * @return actual review output
   */
  public CalData getRefValueObj() {
    if (this.refValue == null) {
      this.refValue = getCDPObj(getEntityProvider().getDbResParamSecondary(getID()).getRefValue());
    }
    return this.refValue;
  }


  /**
   * Return the string representation of reference value
   *
   * @return String
   */
  public String getRefValueString() {
    if (getRefValueObj() != null) {
      return getRefValueObj().getCalDataPhy().getSimpleDisplayValue();
    }
    return "";
  }

  /**
   * @return result
   */
  public String getCommonResult() {
    return getResultEnum().getUiType();
  }

  /**
   * @return the review result enum.
   */
  private RESULT_FLAG getResultEnum() {
    return CDRConstants.RESULT_FLAG.getType(getEntityProvider().getDbResParamSecondary(getID()).getResult());
  }


  /**
   * @return the lab obj id
   */
  public Long getLabObjID() {
    return getEntityProvider().getDbResParamSecondary(getID()).getLabObjId();
  }

  /**
   * @return the lab obj id
   */
  public Long getRevID() {
    return getEntityProvider().getDbResParamSecondary(getID()).getRevId();
  }

  /**
   * Gets CDR Function Parameter from cache
   *
   * @return
   */
  public CDRFuncParameter getFunctionParameter() {

    return getResultParameter().getFunctionParameter();
  }

  /**
   * @return the parent CDR result function object Icdm-548 method made as public
   */
  public CDRResultFunction getFunction() {
    return getResultParameter().getFunction();
  }

  /**
   * @return the function name of this parameter
   */
  public String getFunctionName() {
    return getFunction().getName();
  }


  /**
   * Method to convert byte array to CaldataPhy object
   *
   * @return actual review output
   */
  private CalData getCDPObj(final byte[] dbData) {
    try {
      return CalDataUtil.getCalDataObj(dbData);
    }
    catch (ClassNotFoundException | IOException e) {
      getLogger().error(
          "Error reading CalDataPhy object for parameter : " + getResultParameter().getFunctionParameter().getName(),
          e);
    }
    return null;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbResParamSecondary(getID()).getCreatedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {

    return getEntityProvider().getDbResParamSecondary(getID()).getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbResParamSecondary(getID()).getCreatedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbResParamSecondary(getID()).getModifiedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return CDREntityType.CDR_RESULT_PARAM_SECONDARY;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return getResultParameter().getDescription();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getResultParameter().getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CDRResParamSecondary obj) {
    int compare = ApicUtil.compare(getSecondaryResult().getSource(), obj.getSecondaryResult().getSource());
    if (compare == 0) {
      return ApicUtil.compare(getSecondaryResult().getRuleSet(), obj.getSecondaryResult().getRuleSet());
    }
    return compare;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * @return string of source
   */
  // Task 231287
  public String getSourceName() {
    return null == getSecondaryResult().getRuleSet() ? "Common Rules" : getSecondaryResult().getRuleSet().getName();
  }

  /**
   * @return the parent CDR result object
   */
  public CDRResult getCDRResult() {
    return getResultParameter().getCDRResult();
  }

  /**
   * Indicates whether lower limit has changed from parent review. If this is not a delta review, the method returns
   * false.
   *
   * @return true/false
   */
  public boolean isLowerLimitChanged() {
    if (!getCDRResult().isDeltaReview()) {
      return false;
    }
    return CDRConstants.PARAM_CHANGE_FLAG.LOWER_LIMT
        .isSet(getEntityProvider().getDbCDRResParameter(getID()).getChangeFlag());
  }

  /**
   * Indicates whether upper limit has changed from parent review. If this is not a delta review, the method returns
   * false.
   *
   * @return true/false
   */
  public boolean isUpperLimitChanged() {
    if (!getCDRResult().isDeltaReview()) {
      return false;
    }
    return CDRConstants.PARAM_CHANGE_FLAG.UPPER_LIMIT
        .isSet(getEntityProvider().getDbCDRResParameter(getID()).getChangeFlag());
  }

  /**
   * Indicates whether result has changed from parent review. If this is not a delta review, the method returns false.
   *
   * @return true/false
   */
  public boolean isResultChanged() {
    if (!getCDRResult().isDeltaReview()) {
      return false;
    }
    return CDRConstants.PARAM_CHANGE_FLAG.RESULT
        .isSet(getEntityProvider().getDbCDRResParameter(getID()).getChangeFlag()) ||
        CDRConstants.PARAM_CHANGE_FLAG.COMPLI_RESULT
            .isSet(getEntityProvider().getDbCDRResParameter(getID()).getChangeFlag());
  }


  /**
   * Icdm-945 Indicates whether Ref value has changed from parent review. If this is not a delta review, the method
   * returns false.
   *
   * @return true/false
   */
  public boolean isRefValChanged() {
    if (!getCDRResult().isDeltaReview()) {
      return false;
    }
    return CDRConstants.PARAM_CHANGE_FLAG.REF_VALUE
        .isSet(getEntityProvider().getDbCDRResParameter(getID()).getChangeFlag());
  }


  // ICDM-1940
  /**
   * @return ParentParam
   */
  public CDRResultParameter getParentParam() {
    if (getCDRResult().isDeltaReview()) {
      // For normal delta review get the parameter from the parent review
      if (getCDRResult().getDeltaReviewType().equals(DELTA_REVIEW_TYPE.DELTA_REVIEW)) {
        return getDeltaReviewParentParam();
      }

    }
    return null;
  }

  /**
   * @return parent param for delta review
   */
  private CDRResultParameter getDeltaReviewParentParam() {
    CDRResult parentReview = getCDRResult().getParentReview();

    for (CDRResultParameter selParam : parentReview.getParametersMap().values()) {
      if (ApicUtil.compare(selParam.getName(), getName()) == 0) {
        return selParam;
      }
    }
    return null;
  }

  /**
   * @return ParentLowerLimitString
   */
  public String getParentLowerLimitString() {
    CDRResultParameter parentParam = getParentParam();
    if ((null != parentParam) && (null != parentParam.getLowerLimit())) {
      return parentParam.getLowerLimit().toString();
    }
    return "";
  }

  /**
   * @return ParentUpperLimitString
   */
  public String getParentUpperLimitString() {
    CDRResultParameter parentParam = getParentParam();
    if ((null != parentParam) && (null != parentParam.getUpperLimit())) {
      return parentParam.getUpperLimit().toString();
    }
    return "";
  }

  /**
   * @return ParentBitwiseValString
   */
  public String getParentBitwiseValString() {
    CDRResultParameter parentParam = getParentParam();
    if ((null != parentParam) && (null != parentParam.isBitWise())) {
      return parentParam.isBitWise().equals(ApicConstants.YES) ? ApicConstants.CODE_WORD_YES
          : ApicConstants.CODE_WORD_NO;
    }
    return "";
  }

  /**
   * @return ParentBitwiseLimitString
   */
  public String getParentBitwiseLimitString() {
    CDRResultParameter parentParam = getParentParam();
    if ((null != parentParam) && (null != parentParam.getBitwiseLimit())) {
      return parentParam.getBitwiseLimit().toString();
    }
    return "";
  }

  /**
   * @return ParentResultValueString
   */
  public String getParentResultValueString() {
    CDRResultParameter parentParam = getParentParam();
    if ((null != parentParam) && (null != parentParam.getResult())) {
      return parentParam.getResult();
    }
    return "";
  }

}
