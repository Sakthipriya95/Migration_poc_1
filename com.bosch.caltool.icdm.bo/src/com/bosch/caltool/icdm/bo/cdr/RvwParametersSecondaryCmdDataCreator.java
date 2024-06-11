/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.cdr.review.ReviewRuleSetData;
import com.bosch.caltool.icdm.bo.cdr.review.ReviewedInfo;
import com.bosch.caltool.icdm.bo.cdr.review.RuleUtility;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.RvwParametersSecondary;
import com.bosch.caltool.icdm.model.cdr.RvwResultsSecondary;
import com.bosch.caltool.icdm.model.cdr.review.ReviewInput;
import com.bosch.checkssd.reports.reportmodel.FormtdRptValModel;
import com.bosch.checkssd.reports.reportmodel.ReportModel;

/**
 * @author bru2cob
 */
public class RvwParametersSecondaryCmdDataCreator extends AbstractSimpleBusinessObject {

  private final ReviewedInfo reviewInfo;
  private final ReviewInput reviewInputData;
  private final TRvwResult tRvwResult;
  private final Map<ReviewRuleSetData, RvwResultsSecondary> ruleDataMap;
  private int changeBitNum;

  /**
   * @param reviewInfo Reviewed Info
   * @param reviewInputData review input data
   * @param dbRvwResult TRvwResult
   * @param serviceData Service Data
   * @param ruleDataMap Rule data map
   */
  public RvwParametersSecondaryCmdDataCreator(final ReviewedInfo reviewInfo, final ReviewInput reviewInputData,
      final TRvwResult dbRvwResult, final ServiceData serviceData,
      final Map<ReviewRuleSetData, RvwResultsSecondary> ruleDataMap) {

    super(serviceData);

    this.reviewInfo = reviewInfo;
    this.reviewInputData = reviewInputData;
    this.tRvwResult = dbRvwResult;
    this.ruleDataMap = ruleDataMap;
  }

  /**
   * @return set of RvwParametersSecondary
   * @throws IcdmException error while retrieving data
   */
  public SortedSet<RvwParametersSecondary> createSecRvwParams() throws IcdmException {
    SortedSet<RvwParametersSecondary> secondaryParams = new TreeSet<>();
    Long parentResultId = this.reviewInputData.getResultData().getParentResultId();
    CDRReviewResultLoader resultLoader = new CDRReviewResultLoader(getServiceData());
    TRvwResult parentResultEntity = resultLoader.getEntityObject(parentResultId);


    RvwParametersSecondaryLoader secParamLoader = new RvwParametersSecondaryLoader(getServiceData());
    CDRResultParameterLoader paramLoader = new CDRResultParameterLoader(getServiceData());
    // Check if parent result present
    if (parentResultId != null) {
      // get Parent Secondary Result.
      Map<Long, RvwResultsSecondary> secondaryResults =
          new RvwResultsSecondaryLoader(getServiceData()).getByResultObj(parentResultEntity);
      if (secondaryResults != null) {
        for (RvwResultsSecondary parentSecRes : secondaryResults.values()) {
          // Get the parent secondary params

          Map<Long, RvwParametersSecondary> parentSecParams =
              secParamLoader.getResultSecondaryParams(parentSecRes.getId());

          List<String> parentParmasList = createParentPramList(parentSecParams);

          Map<Long, CDRResultParameter> resultParams = paramLoader.getByResultObj(this.tRvwResult);
          Map<String, CDRResultParameter> resultParamNameMap = new HashMap<>();

          for (CDRResultParameter param : resultParams.values()) {
            resultParamNameMap.put(param.getName(), param);
          }
          List<CDRResultParameter> newlyAddedParams = getNewlyAddedParams(parentParmasList, resultParams);
          for (RvwParametersSecondary parentSecParam : parentSecParams.values()) {

            if ((!this.ruleDataMap.isEmpty()) && (this.reviewInfo.getSecRuleSetDataList() != null)) {
              // get the new Secondary Result

              // Iterate the rule set Data
              for (ReviewRuleSetData ruleSetData : this.reviewInfo.getSecRuleSetDataList()) {
                RvwResultsSecondary secondaryResult = this.ruleDataMap.get(ruleSetData);
                if (isSameParentSecResult(parentSecRes, ruleSetData, secondaryResult)) {


                  CDRResultParameter resParam = resultParamNameMap.get(parentSecParam.getName());

                  // If the parent and new Params are same then do insertion for Delta
                  if (resParam != null
                  /* && CommonUtils.isEqual(parentSecParam.getResultParameter().getName(), resParam.getName()) */) {
                    // If the rule set data contains rules.
                    RvwParametersSecondary object = createSecParamObj(ruleSetData, secondaryResult, resParam);
                    ReviewRule rule = null;
                    if (ruleSetData.getCdrRules() != null) {
                      List<ReviewRule> list = ruleSetData.getCdrRules().get(resParam.getName());
                      if (list != null) {
                        rule = list.get(0);
                      }
                    }
                    object.setChangeFlag(getChangeBit(parentSecParam, object, rule));
                    secondaryParams.add(object);

                  }

                }
              }

            }


          }

          // Flow for newly added params.
          for (CDRResultParameter newResParam : newlyAddedParams) {
            if ((!this.ruleDataMap.isEmpty()) && (this.reviewInfo.getSecRuleSetDataList() != null)) {
              for (ReviewRuleSetData ruleSetData : this.reviewInfo.getSecRuleSetDataList()) {

                RvwResultsSecondary secondaryResult = this.ruleDataMap.get(ruleSetData);
                if (isSameParentSecResult(parentSecRes, ruleSetData, secondaryResult)) {
                  RvwParametersSecondary object = createSecParamObj(ruleSetData, secondaryResult, newResParam);
                  secondaryParams.add(object);
                }

              }
            }

          }


        }
      }
      addNewResults(secondaryResults, secondaryParams);


    }

    else {
      storeSecRvwParams(this.ruleDataMap.values(), this.reviewInfo.getSecRuleSetDataList(), secondaryParams);
    }
    return secondaryParams;
  }

  private int getChangeBit(final RvwParametersSecondary parentSecondaryResParam,
      final RvwParametersSecondary secondaryResParam, final ReviewRule rule)
      throws IcdmException {

    if (parentSecondaryResParam != null) {
      try {
        if (!compareObjects(parentSecondaryResParam.getIsbitwise(), secondaryResParam.getIsbitwise())) {
          this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.BITWISE_FLAG.setFlag(this.changeBitNum);
        }
        if (!compareObjects(parentSecondaryResParam.getResult(), secondaryResParam.getResult())) {
          this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.RESULT.setFlag(this.changeBitNum);
        }
        CalData parentRefValueObject;

        parentRefValueObject = CalDataUtil.getCalDataObj(parentSecondaryResParam.getRefValue());


        CalData refCalDataObj = CalDataUtil.getCalDataObj(secondaryResParam.getRefValue());
        if ((parentRefValueObject != null) && ((rule != null) && (refCalDataObj != null))) {
          if (!compareObjects(parentRefValueObject.getCalDataPhy(), refCalDataObj.getCalDataPhy())) {
            this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.REF_VALUE.setFlag(this.changeBitNum);
          }
        }
        else if (!compareObjects(parentRefValueObject, refCalDataObj)) {
          this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.REF_VALUE.setFlag(this.changeBitNum);
        }

        CalData parentCheckValCalData = CalDataUtil.getCalDataObj(new CDRResultParameterLoader(getServiceData())
            .getDataObjectByID(parentSecondaryResParam.getRvwParamId()).getCheckedValue());
        if ((parentCheckValCalData != null) && (parentCheckValCalData.getCalDataPhy() != null)) {
          // Lower limit
          if (!compareObjects(parentSecondaryResParam.getLowerLimit(), secondaryResParam.getLowerLimit())) {
            this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.LOWER_LIMT.setFlag(this.changeBitNum);
          }
          // Upper limit
          if (!compareObjects(parentSecondaryResParam.getUpperLimit(), secondaryResParam.getUpperLimit())) {
            this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.UPPER_LIMIT.setFlag(this.changeBitNum);
          }
          // bitwise rule
          if (("Y".equals(secondaryResParam.getIsbitwise())) &&
              !compareObjects(parentSecondaryResParam.getBitwiseLimit(), secondaryResParam.getBitwiseLimit())) {
            this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.BITWISE_LIMIT.setFlag(this.changeBitNum);
          }
          if (!compareObjects(parentSecondaryResParam.getRvwMethod() ,secondaryResParam.getRvwMethod())) {
            this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.READY_FOR_SERIES.setFlag(this.changeBitNum);
          }
          if (!compareObjects(parentSecondaryResParam.getMatchRefFlag(), parentSecondaryResParam.getMatchRefFlag())) {
            this.changeBitNum = CDRConstants.PARAM_CHANGE_FLAG.EXACT_MATCH.setFlag(this.changeBitNum);
          }
        }
      }
      catch (ClassNotFoundException | IOException exp) {
        throw new IcdmException(exp.getLocalizedMessage(), exp);
      }

    }

    return this.changeBitNum;
  }

  /** Method used to custom compare two objects */
  private boolean compareObjects(final Object obj1, final Object obj2) {
    boolean isEqual;
    if ((obj1 == null) && (obj2 == null)) {
      isEqual = true;
    }
    else if ((obj1 == null) || (obj2 == null)) {
      isEqual = false;
    }

    else {
      isEqual = obj1.equals(obj2);
    }
    return isEqual;
  }

  /**
   * @param parentSecondaryResults
   * @param secondaryParams
   * @throws DataException
   */
  private void addNewResults(final Map<Long, RvwResultsSecondary> parentSecondaryResults,
      final SortedSet<RvwParametersSecondary> secondaryParams)
      throws DataException {
    Set<RvwResultsSecondary> newResults = new HashSet<>();

    for (RvwResultsSecondary cdrSecondaryResult : this.ruleDataMap.values()) {
      boolean alreadyInserted = false;

      for (RvwResultsSecondary parentSecResult : parentSecondaryResults.values()) {
        // current Secondary -- result - Parent and Parent Seconadry -- Result id are not same
        if (CommonUtils.isEqual(cdrSecondaryResult.getSource(), parentSecResult.getSource()) &&
            CommonUtils.isEqual(cdrSecondaryResult.getRsetId(), parentSecResult.getRsetId())) {
          alreadyInserted = true;
        }
      }
      if (!alreadyInserted) {
        newResults.add(cdrSecondaryResult);
      }
    }

    storeSecRvwParams(newResults, this.reviewInfo.getSecRuleSetDataList(), secondaryParams);
  }

  /**
   * @param secondaryParams
   * @param secRuleSetDataList2
   * @param sortedSet
   * @param secondaryParamsAdded
   * @throws DataException
   */
  private void storeSecRvwParams(final Collection<RvwResultsSecondary> curResuts,
      final List<ReviewRuleSetData> reviewRuleSetData, final SortedSet<RvwParametersSecondary> secondaryParams)
      throws DataException {
    if ((CommonUtils.isNotEmpty(curResuts)) && (CommonUtils.isNotEmpty(reviewRuleSetData))) {
      // Iterate result

      // Input data
      for (ReviewRuleSetData ruleSetData : this.reviewInfo.getSecRuleSetDataList()) {
        RvwResultsSecondary secondaryResult = this.ruleDataMap.get(ruleSetData);
        // make sure both thr ule sets are equal
        if (secondaryResult.getSource().equals(ruleSetData.getSource().getDbVal())) {
          // Result params
          Map<Long, CDRResultParameter> resultParams =
              new CDRResultParameterLoader(getServiceData()).getByResultObj(this.tRvwResult);
          for (CDRResultParameter resParam : resultParams.values()) {
            secondaryParams.add(createSecParamObj(ruleSetData, secondaryResult, resParam));

          }

        }

      }

    }

  }

  /**
   * @param ruleSetData
   * @param secondaryResult
   * @param resParam
   * @return
   */
  private RvwParametersSecondary createSecParamObj(final ReviewRuleSetData ruleSetData,
      final RvwResultsSecondary secondaryResult, final CDRResultParameter resParam) {

    RvwParametersSecondary object = new RvwParametersSecondary();
    ReviewRule rule = null;
    if (ruleSetData.getCdrRules() != null) {
      List<ReviewRule> list = ruleSetData.getCdrRules().get(resParam.getName());
      if (list != null) {
        rule = list.get(0);
      }
    }
    object.setRvwParamId(resParam.getId());
    object.setName(resParam.getName());
    if (rule != null) {
      object.setLowerLimit(rule.getLowerLimit());
      object.setUpperLimit(rule.getUpperLimit());
      object.setBitwiseLimit(rule.getBitWiseRule());
      object.setRefValue(getRefValue(rule, resParam));
      object.setRvwMethod(rule.getReviewMethod());
      object.setLabObjId(rule.getRuleId() == null ? null : rule.getRuleId().longValue());
      object.setRevId(rule.getRevId() == null ? null : rule.getRevId().longValue());
      object.setRefUnit(rule.getUnit());
      String matchRefFlag = rule.isDcm2ssd() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO;
      object.setMatchRefFlag(matchRefFlag);
    }
    object.setIsbitwise(resParam.getIsbitwise());

    object.setSecReviewId(secondaryResult.getId());


    if (ruleSetData.getCheckSSDResParamMap() != null) {
      object.setResult(getResultFlag(ruleSetData.getCheckSSDResParamMap().get(resParam.getName()), rule));
    }
    return object;
  }

  /**
   * @param checkSSDResultParam
   * @param result
   * @return
   */
  private String getResultFlag(final CheckSSDResultParam checkSSDResultParam, final ReviewRule rule) {
    // by default, not reviwed
    CDRConstants.RESULT_FLAG result = CDRConstants.RESULT_FLAG.NOT_REVIEWED;
    RuleUtility ruleUtility = new RuleUtility();
    // Set proper result from CheckSSD
    if (checkSSDResultParam != null) {
      ReportModel reportModel = checkSSDResultParam.getCompliReportModel();
      if (reportModel != null) {
        result = setResultFromReport(reportModel);
      }
      else if (ruleUtility.isRuleComplete(rule)) {
        result = CDRConstants.RESULT_FLAG.NOT_OK;
      }
    }
    // Rule is complete and the check ssd is not available
    else if (ruleUtility.isRuleComplete(rule)) {
      result = CDRConstants.RESULT_FLAG.NOT_OK;
    }
    return result.getDbType();
  }

  /**
   * @param reportModel
   * @return
   */
  private CDRConstants.RESULT_FLAG setResultFromReport(final ReportModel reportModel) {
    CDRConstants.RESULT_FLAG result;
    if (reportModel.isRuleOk()) {
      result = CDRConstants.RESULT_FLAG.OK;
    }
    else {
      if (reportModel instanceof FormtdRptValModel) {
        FormtdRptValModel formtdRptValModel = (FormtdRptValModel) reportModel;
        if ((formtdRptValModel.getValGE() != null) && !formtdRptValModel.getValGE().isEmpty()) {
          result = CDRConstants.RESULT_FLAG.HIGH;
        }
        else if ((formtdRptValModel.getValLE() != null) && !formtdRptValModel.getValLE().isEmpty()) {
          result = CDRConstants.RESULT_FLAG.LOW;
        }
        else {
          result = CDRConstants.RESULT_FLAG.NOT_OK;
        }
      }
      else {
        result = CDRConstants.RESULT_FLAG.NOT_OK;
      }
    }
    return result;
  }

  /**
   * Get reference value object from RULE for all types (VALUE, CURVE, MAP..)
   *
   * @return CalData object
   */
  private byte[] getRefValue(final ReviewRule rule, final CDRResultParameter resParam) {
    byte[] calDataBytes = null;
    CalData calData = null;
    // VALUE type label
    if ((ApicUtil.compare(CommonUtils.checkNull(resParam.getpType()), ApicConstants.VALUE_TEXT) == 0) &&
        (rule.getRefValue() != null)) { // ICDM-1253
      // Prepare DCM string for this decimal string and convert to CalData
      calData = CalDataUtil.dcmToCalData(
          CalDataUtil.createDCMStringForNumber(resParam.getName(), rule.getUnit(), rule.getRefValue().toString()),
          resParam.getName(), getLogger());
    } // For Complex type labels, get DCM string
    else if (rule.getRefValueCalData() != null) {
      calDataBytes = rule.getRefValueCalData();
    }
    if (calData != null) {
      calDataBytes = CalDataUtil.convertCalDataToZippedByteArr(calData, CDMLogger.getInstance());
    }
    // default case
    return calDataBytes;
  }

  /**
   * @param parentSecRes
   * @param ruleSetData
   * @param secondaryResult
   * @return teur if the Source and Rule set of parent and current secondary Result are same.
   */
  private boolean isSameParentSecResult(final RvwResultsSecondary parentSecRes, final ReviewRuleSetData ruleSetData,
      final RvwResultsSecondary secondaryResult) {

    return isRulesetValid(secondaryResult, ruleSetData) && isRulesetValid(parentSecRes, ruleSetData) &&
        isSrcValid(secondaryResult, parentSecRes, ruleSetData);
  }

  /**
  *
  */
  private boolean isRulesetValid(final RvwResultsSecondary secondaryResult, final ReviewRuleSetData ruleSetData) {
    return CommonUtils.isEqual(secondaryResult.getRsetId(),
        ruleSetData.getRuleSet() == null ? null : ruleSetData.getRuleSet().getId());
  }

  /**
  *
  */
  private boolean isSrcValid(final RvwResultsSecondary secondaryResult, final RvwResultsSecondary parentSecRes,
      final ReviewRuleSetData ruleSetData) {
    return CommonUtils.isEqual(secondaryResult.getSource(), ruleSetData.getSource().getDbVal()) &&
        (CommonUtils.isEqual(parentSecRes.getSource(), ruleSetData.getSource().getDbVal()));
  }

  /**
   * @param parentParmasList
   * @param parametersMap
   * @return
   */
  private List<CDRResultParameter> getNewlyAddedParams(final List<String> parentParmasList,
      final Map<Long, CDRResultParameter> parametersMap) {

    List<CDRResultParameter> newlyAddedParams = new ArrayList<>();
    for (CDRResultParameter cdrResParam : parametersMap.values()) {
      if (!parentParmasList.contains(cdrResParam.getName())) {
        newlyAddedParams.add(cdrResParam);
      }
    }
    return newlyAddedParams;

  }

  /**
   * @param parentSecParams
   * @return
   */
  private List<String> createParentPramList(final Map<Long, RvwParametersSecondary> parentSecParams) {

    List<String> parentParamList = new ArrayList<>();

    for (RvwParametersSecondary resParamSec : parentSecParams.values()) {
      parentParamList.add(resParamSec.getName());
    }
    return parentParamList;
  }
}
