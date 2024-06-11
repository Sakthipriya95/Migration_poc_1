/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.io.IOException;
import java.util.Map;

import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.bo.util.CalDataComparisonWrapper;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;

/**
 * Evaluate change bit from the given inputs
 *
 * @author rgo7cob
 */
class ChangeBitEvaluator {

  private int changeBitNum = 0;

  /**
   * @param parentResultParam parentResultParam
   * @param resultParam checkSSDResultParam
   * @param checkSSDResultParam checkSSDResultParam
   * @param calData calData
   * @param charMap charMap
   * @return the change bit
   * @throws IcdmException error while calculating change bit
   */
  public int findChangeBit(final CDRResultParameter parentResultParam, final CDRResultParameter resultParam,
      final CheckSSDResultParam checkSSDResultParam, final CalData calData, final Map<String, Characteristic> charMap)
      throws IcdmException {


    // Change bit is applicable only for Delta review.
    if (parentResultParam != null) {
      CalData parentCheckValCalData = CalDataUtil.getCalDataObjIEx(parentResultParam.getCheckedValue());
      if ((parentCheckValCalData != null) && (parentCheckValCalData.getCalDataPhy() != null)) {
        setChgBitForLimitAndRes(parentResultParam, resultParam);
      }
      setChgBitForSecRes(parentResultParam, resultParam);

      Characteristic a2lChar = charMap.get(resultParam.getName());

      if (CommonUtils.isNotNull(checkSSDResultParam)) {
        setChgBitForChkVal(checkSSDResultParam, parentCheckValCalData, a2lChar);
      }

      // check for MoniCa delta review. check Ssd result param is null for Monica Review.
      else if (!isCalDataSame(parentCheckValCalData, calData, a2lChar)) {
        this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.CHECK_VALUE.setFlag(this.changeBitNum);
      }

      setChgBitForRefVal(parentResultParam, resultParam, a2lChar);
    }

    return this.changeBitNum;

  }


  /**
   * @param parentResultParam CDRResultParameter
   * @param resultParam CDRResultParameter
   * @param changeBitNum input of already calculated changebit
   * @return change bit value as integer
   * @throws IcdmException Exception
   */
  public int setRvwScoreChangeBit(final CDRResultParameter parentResultParam, final CDRResultParameter resultParam,
      final int changeBitNum)
      throws IcdmException {
    int changeBitNumVal = 0;
    // review score change bit
    if (parentResultParam != null) {
      CalData parentCheckValCalData = CalDataUtil.getCalDataObjIEx(parentResultParam.getCheckedValue());
      if ((parentCheckValCalData != null) && (parentCheckValCalData.getCalDataPhy() != null)) {
        if (!parentResultParam.getReviewScore().equalsIgnoreCase(resultParam.getReviewScore())) {
          changeBitNumVal = CDRConstants.PARAM_CHANGE_FLAG.SCORE_VALUE.setFlag(changeBitNum);
        }
        else {
          changeBitNumVal = CDRConstants.PARAM_CHANGE_FLAG.SCORE_VALUE.removeFlag(changeBitNum);
        }
      }
    }
    return changeBitNumVal;
  }


  /**
   * @param checkSSDResultParam
   * @param parentCheckValCalData
   * @param a2lChar
   */
  private void setChgBitForChkVal(final CheckSSDResultParam checkSSDResultParam, final CalData parentCheckValCalData,
      final Characteristic a2lChar) {

    // No need for a separate call. compare cal obj has Compare Objects method.
    if (!isCalDataSame(parentCheckValCalData, checkSSDResultParam.getCheckedValue(), a2lChar)) {
      this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.CHECK_VALUE.setFlag(this.changeBitNum);
    }

  }

  /**
   * @param parentResultParam
   * @param resultParam
   * @param rule
   * @param a2lChar
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private void setChgBitForRefVal(final CDRResultParameter parentResultParam, final CDRResultParameter resultParam,
      final Characteristic a2lChar)
      throws IcdmException {

    CalData parentRefValueObject = CalDataUtil.getCalDataObjIEx(parentResultParam.getRefValue());
    CalData refVal = CalDataUtil.getCalDataObjIEx(resultParam.getRefValue());

    if (!isCalDataSame(parentRefValueObject, refVal, a2lChar)) {
      this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.REF_VALUE.setFlag(this.changeBitNum);
    }

  }

  private boolean isCalDataSame(final CalData parentRefValueObject, final CalData refVal,
      final Characteristic a2lChar) {
    return new CalDataComparisonWrapper(a2lChar).isEqual(parentRefValueObject, refVal);
  }

  /**
   * @param parentResultParam
   * @param resultParam
   */
  private void setChgBitForSecRes(final CDRResultParameter parentResultParam, final CDRResultParameter resultParam) {
    String secResult1 = "";
    String secResult2 = "";
    if ((parentResultParam.getSecondaryResult() != null) && !parentResultParam.getSecondaryResult().isEmpty()) {
      secResult1 = parentResultParam.getSecondaryResult();
    }
    if ((resultParam.getSecondaryResult() != null) && !resultParam.getSecondaryResult().isEmpty()) {
      secResult2 = resultParam.getSecondaryResult();
    }
    if (!compareObjects(secResult1, secResult2)) {
      this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.SECONDARY_RESULT.setFlag(this.changeBitNum);

    }
  }


  /**
   * @param parentResultParam
   * @param resultParam
   */
  private void setChgBitForLimitAndRes(final CDRResultParameter parentResultParam,
      final CDRResultParameter resultParam) {

    setChangeBitBasedOnBitwiseFlag(parentResultParam, resultParam);
    
    setChangeBitBasedOnCompliResult(parentResultParam, resultParam);
    
    setChangeBitBasedOnQSSDResult(parentResultParam, resultParam);
    
    setChangeBitBasedOnResult(parentResultParam, resultParam);
    // Lower limit
    setChangeBitBasedOnLowerLimit(parentResultParam, resultParam);
    // Upper limit
    setChangeBitBasedOnUpperLimit(parentResultParam, resultParam);
    // bitwise rule
    setChangeBitBasedOnBitwiseRule(parentResultParam, resultParam);
    
    setChangeBitBasedOnReadyForSeries(parentResultParam, resultParam);
    
    setChangeBitBasedOnExactMatch(parentResultParam, resultParam);
  }


  /**
   * @param parentResultParam
   * @param resultParam
   */
  private void setChangeBitBasedOnExactMatch(final CDRResultParameter parentResultParam,
      final CDRResultParameter resultParam) {
    if (!compareObjects(parentResultParam.getMatchRefFlag(), resultParam.getMatchRefFlag())) {
      this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.EXACT_MATCH.setFlag(this.changeBitNum);
    }
  }


  /**
   * @param parentResultParam
   * @param resultParam
   */
  private void setChangeBitBasedOnReadyForSeries(final CDRResultParameter parentResultParam,
      final CDRResultParameter resultParam) {
    if (!compareObjects(parentResultParam.getRvwMethod(), resultParam.getRvwMethod())) {
      this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.READY_FOR_SERIES.setFlag(this.changeBitNum);
    }
  }


  /**
   * @param parentResultParam
   * @param resultParam
   */
  private void setChangeBitBasedOnBitwiseRule(final CDRResultParameter parentResultParam,
      final CDRResultParameter resultParam) {
    if (("Y".equalsIgnoreCase(resultParam.getIsbitwise())) &&
        !compareObjects(parentResultParam.getBitwiseLimit(), resultParam.getBitwiseLimit())) {
      this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.BITWISE_LIMIT.setFlag(this.changeBitNum);
    }
  }


  /**
   * @param parentResultParam
   * @param resultParam
   */
  private void setChangeBitBasedOnUpperLimit(final CDRResultParameter parentResultParam,
      final CDRResultParameter resultParam) {
    if (!compareObjects(parentResultParam.getUpperLimit(), resultParam.getUpperLimit())) {
      this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.UPPER_LIMIT.setFlag(this.changeBitNum);
    }
  }


  /**
   * @param parentResultParam
   * @param resultParam
   */
  private void setChangeBitBasedOnLowerLimit(final CDRResultParameter parentResultParam,
      final CDRResultParameter resultParam) {
    if (!compareObjects(parentResultParam.getLowerLimit(), resultParam.getLowerLimit())) {
      this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.LOWER_LIMT.setFlag(this.changeBitNum);
    }
  }


  /**
   * @param parentResultParam
   * @param resultParam
   */
  private void setChangeBitBasedOnResult(final CDRResultParameter parentResultParam,
      final CDRResultParameter resultParam) {
    if (!CommonUtils.isEqualIgnoreCase(parentResultParam.getResult(), resultParam.getResult())) {
      this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.RESULT.setFlag(this.changeBitNum);
    }
  }


  /**
   * @param parentResultParam
   * @param resultParam
   */
  private void setChangeBitBasedOnQSSDResult(final CDRResultParameter parentResultParam,
      final CDRResultParameter resultParam) {
    if (!compareObjects(parentResultParam.getQssdResult(), resultParam.getQssdResult())) {
      this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.QSSD_RESULT.setFlag(this.changeBitNum);
    }
  }


  /**
   * @param parentResultParam
   * @param resultParam
   */
  private void setChangeBitBasedOnCompliResult(final CDRResultParameter parentResultParam,
      final CDRResultParameter resultParam) {
    if (!compareObjects(parentResultParam.getCompliResult(), resultParam.getCompliResult())) {
      this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.COMPLI_RESULT.setFlag(this.changeBitNum);
    }
  }


  /**
   * @param parentResultParam
   * @param resultParam
   */
  private void setChangeBitBasedOnBitwiseFlag(final CDRResultParameter parentResultParam,
      final CDRResultParameter resultParam) {
    if (!compareObjects(parentResultParam.getIsbitwise(), resultParam.getIsbitwise())) {
      this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.BITWISE_FLAG.setFlag(this.changeBitNum);
    }
  }


  /** Method used to custom compare two objects */
  private boolean compareObjects(final Object obj1, final Object obj2) {
    return CommonUtils.isEqual(obj1, obj2);
  }


}
