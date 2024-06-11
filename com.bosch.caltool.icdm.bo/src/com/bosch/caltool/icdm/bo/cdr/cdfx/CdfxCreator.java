/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.cdfx;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldata.element.DataElement;
import com.bosch.calmodel.caldata.history.CalDataHistory;
import com.bosch.calmodel.caldata.history.HistoryEntry;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2lWpResponsibilityLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.cdr.CDRReportLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.model.a2l.WpRespLabelResponse;
import com.bosch.caltool.icdm.model.a2l.WpRespModel;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_LOCK_STATUS;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRReportData;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.model.cdr.ParameterReviewDetails;
import com.bosch.caltool.icdm.model.cdr.ReviewDetails;
import com.bosch.caltool.icdm.model.cdr.cdfx.CDFxDeliveryWrapper;

/**
 * Class to handle creation of CDFx file
 *
 * @author pdh2cob
 */
public class CdfxCreator extends AbstractSimpleBusinessObject {

  private final CDFxDeliveryWrapper deliveryWrapper;

  private final PidcVersion pidcVersion;

  private static final String TEXT_SEPERATOR = " --> ";

  /**
   * key-"work package name --> responsibility name", value-no. of parameters missing in CDFx
   */
  private final Map<String, Integer> missingParamCountMap = new HashMap<>();

  /**
   * @param deliveryWrapper deliveryWrapper Model
   * @param serviceData service data
   * @throws DataException exception
   */
  public CdfxCreator(final CDFxDeliveryWrapper deliveryWrapper, final ServiceData serviceData) throws DataException {
    super(serviceData);
    this.deliveryWrapper = deliveryWrapper;
    this.pidcVersion = new PidcVersionLoader(getServiceData()).getDataObjectByID(new PidcA2lLoader(getServiceData())
        .getDataObjectByID(this.deliveryWrapper.getInput().getPidcA2lId()).getPidcVersId());
  }

  /**
   * @param variantId VariantId
   * @throws IcdmException exception
   */
  public void createCdrReport(final Long variantId) throws IcdmException {
    // create cdr report for given input
    CDRReportLoader repDataLoader = new CDRReportLoader(getServiceData());

    CDRReportData cdrRprtData = new CDRReportData();
    cdrRprtData.setPidcA2lId(this.deliveryWrapper.getInput().getPidcA2lId());
    cdrRprtData.setMaxResults(1);
    cdrRprtData.setFetchCheckVal(true);
    cdrRprtData.setVarId(variantId);

    this.deliveryWrapper.setCdrReport(repDataLoader.fetchCDRReportData(cdrRprtData));
  }

  /**
   * @param variantId Variant Id
   * @param cdfxDeliveryHandler wpCalDataObjects
   * @return caldata objects
   * @throws IcdmException exception
   */
  public Map<String, CalData> getRelevantCalDataObjects(final Long variantId,
      final CdfxDeliveryHandler cdfxDeliveryHandler)
      throws IcdmException {

    Map<String, WpRespLabelResponse> wpRespLabelMap = new A2lWpResponsibilityLoader(getServiceData())
        .getWpRespLabelMap(this.deliveryWrapper.getInput().getPidcA2lId(), variantId);
    Map<String, CalData> cdfCalDataObjects = new HashMap<>();
    Map<String, WpRespLabelResponse> paramWpRespMap = new HashMap<>();
    Map<String, List<ParameterReviewDetails>> parameterReviewDetailsMap =
        this.deliveryWrapper.getCdrReport().getParamRvwDetMap();

    for (Entry<String, WpRespLabelResponse> wpRespEntry : wpRespLabelMap.entrySet()) {
      addWpRespParamCount(wpRespEntry);
      if (parameterReviewDetailsMap.containsKey(wpRespEntry.getKey())) {
        ParameterReviewDetails parameterReviewDetails = parameterReviewDetailsMap.get(wpRespEntry.getKey()).get(0);
        addToCalDataMap(parameterReviewDetails, wpRespEntry.getValue(), cdfCalDataObjects, wpRespEntry.getKey(),
            variantId, paramWpRespMap, cdfxDeliveryHandler);
      }
      else {
        String wpName = wpRespEntry.getValue().getWpRespModel().getWpName();
        String respName = wpRespEntry.getValue().getWpRespModel().getWpRespName();
        String wpRespKey = wpName + TEXT_SEPERATOR + respName;
        if (!this.missingParamCountMap.containsKey(wpRespKey)) {
          this.missingParamCountMap.put(wpRespKey, 1);
        }
        else {
          int count = this.missingParamCountMap.get(wpRespKey);
          this.missingParamCountMap.put(wpRespKey, ++count);
        }
      }
    }
    return cdfCalDataObjects;
  }

  /**
   * @param wpRespEntry2
   */
  private void addWpRespParamCount(final Entry<String, WpRespLabelResponse> wpRespEntry) {
    if (this.deliveryWrapper.getInput().getScope() != null) {
      String respType = wpRespEntry.getValue().getWpRespModel().getA2lResponsibility().getRespType();
      if (this.deliveryWrapper.getInput().getScope().equals(respType)) {
        this.deliveryWrapper.getWpRespParamSet().add(wpRespEntry.getKey());
      }
    }
    // if wp list is given, fetch only those review results for which wp and resp are matching
    else if (isWPRespMatching(wpRespEntry.getValue())) {
      this.deliveryWrapper.getWpRespParamSet().add(wpRespEntry.getKey());
    }
  }

  /**
   * @param paramName
   * @return
   */
  private boolean isCompletedStatus(final String paramName) {
    return this.deliveryWrapper.getParamReviewStatusMap().containsKey(DATA_REVIEW_SCORE.RATING_COMPLETED) &&
        this.deliveryWrapper.getParamReviewStatusMap().get(DATA_REVIEW_SCORE.RATING_COMPLETED).contains(paramName);
  }


  /**
   * @param parameterReviewDetails
   * @param wpRespLabelResponse
   * @param cdfCalDataObjects
   * @param paramName
   * @param variantId
   * @param wpRespLabelCalData2
   * @param paramWpRespMap
   * @param cdfxDeliveryHandler
   * @param wpRespLabelRespMap
   * @throws IcdmException
   */
  private void addToCalDataMap(final ParameterReviewDetails parameterReviewDetails,
      final WpRespLabelResponse wpRespLabelResponse, final Map<String, CalData> cdfCalDataObjects,
      final String paramName, final Long variantId, final Map<String, WpRespLabelResponse> paramWpRespMap,
      final CdfxDeliveryHandler cdfxDeliveryHandler)
      throws IcdmException {
    try {

      boolean filterMatch = false;
      String scope = this.deliveryWrapper.getInput().getScope();
      // if scope is given, fetch only those review results for which resp type is matching
      if (CommonUtils.isNotEmptyString(scope)) {
        if (scope.equals(wpRespLabelResponse.getWpRespModel().getA2lResponsibility().getRespType())) {
          filterMatch = true;
        }
      }
      // if wp list is given, fetch only those review results for which wp and resp are matching
      else if (isWPRespMatching(wpRespLabelResponse)) {
        filterMatch = true;
      }

      if (filterMatch) {
        // TODO - add history based on req for cdfx file
        CalData calDataObj = CalDataUtil.getCalDataObj(parameterReviewDetails.getCheckedVal());

        // ignore parameters for which caldata object is not available
        if (calDataObj != null) {
          calDataObj.setCalDataHistory(new CalDataHistory());
          addToParamStatusMap(parameterReviewDetails, paramName);
          calDataObj.getCalDataHistory().getHistoryEntryList()
              .add(getReviewHistory(parameterReviewDetails, wpRespLabelResponse, isCompletedStatus(paramName)));
          cdfCalDataObjects.put(paramName, calDataObj);

          // Map is used in the creation of Excel file
          this.deliveryWrapper.getRelevantWpRespLabelMap().put(paramName, wpRespLabelResponse);

          // This is used for creation of wp specific cdfx files on the basis of scope and input wp list
          setWpCaldataObjects(wpRespLabelResponse, calDataObj, cdfxDeliveryHandler);

          if (variantId != null) {
            paramWpRespMap.put(paramName, wpRespLabelResponse);
            this.deliveryWrapper.getVariantWpRespLabelRespMap().put(variantId, paramWpRespMap);
          }
        }
      }
    }
    catch (IOException | ClassNotFoundException e) {
      getLogger().error(e.getMessage(), e);
      throw new IcdmException("Error during caldata object creation for CDFx file");
    }
  }

  /**
   * @param wpRespLabelResponse wpRespLabelResponse
   * @param calDataObj calDataObj
   * @param cdfxDeliveryHandler wpCalDataObjects
   */
  private void setWpCaldataObjects(final WpRespLabelResponse wpRespLabelResponse, final CalData calDataObj,
      final CdfxDeliveryHandler cdfxDeliveryHandler) {

    String wpName = wpRespLabelResponse.getWpRespModel().getWpName();
    if (cdfxDeliveryHandler.getWpCalDataObjects().containsKey(wpName)) {
      Map<WpRespLabelResponse, CalData> existingWpRespLabelCalData =
          cdfxDeliveryHandler.getWpCalDataObjects().get(wpName);
      existingWpRespLabelCalData.put(wpRespLabelResponse, calDataObj);
      cdfxDeliveryHandler.getWpCalDataObjects().put(wpName, existingWpRespLabelCalData);
    }
    else {
      Map<WpRespLabelResponse, CalData> wpRespLabelCalData = new HashMap();
      wpRespLabelCalData.put(wpRespLabelResponse, calDataObj);
      cdfxDeliveryHandler.getWpCalDataObjects().put(wpName, wpRespLabelCalData);
    }
  }

  /**
   * @param parameterReviewDetails
   * @param paramName
   */
  private void addToParamStatusMap(final ParameterReviewDetails parameterReviewDetails, final String paramName) {
    CDRReviewResult result =
        this.deliveryWrapper.getCdrReport().getCdrReviewResultMap().get(parameterReviewDetails.getRvwID());
    DATA_REVIEW_SCORE dataRvwScore = DATA_REVIEW_SCORE.getType(parameterReviewDetails.getReviewScore());
    int rating;
    switch (dataRvwScore) {

      case S_8:
      case S_9:
        if (isRvwOffclLocked(result) && this.deliveryWrapper.getInput().isReadinessFlag()) {
          rating = DATA_REVIEW_SCORE.RATING_COMPLETED;
        }
        else {
          rating = DATA_REVIEW_SCORE.RATING_CHECKED;
        }
        break;

      default:
        rating = dataRvwScore.getRating();
        break;
    }
    fillRvwStatusMap(rating, paramName);
  }

  /**
   * @param result
   * @return
   */
  private boolean isRvwOffclLocked(final CDRReviewResult result) {
    return REVIEW_TYPE.OFFICIAL.getDbType().equals(result.getReviewType()) &&
        REVIEW_LOCK_STATUS.YES.getDbType().equals(result.getLockStatus());
  }

  /**
   * @param rvwRating
   * @param paramName
   */
  private void fillRvwStatusMap(final int rvwRating, final String paramName) {
    if (null != this.deliveryWrapper.getParamReviewStatusMap().get(rvwRating)) {
      this.deliveryWrapper.getParamReviewStatusMap().get(rvwRating).add(paramName);
    }
    else {
      this.deliveryWrapper.getParamReviewStatusMap().put(rvwRating, new HashSet<>(Arrays.asList(paramName)));
    }
  }

  /**
   * @param parameterReviewDetails
   * @return
   */
  private DATA_REVIEW_SCORE getReviewScore(final ParameterReviewDetails parameterReviewDetails) {
    return DATA_REVIEW_SCORE.getType(parameterReviewDetails.getReviewScore());
  }

  /**
   * @param parRevDetails
   * @return
   */
  private ReviewDetails getReviewDetails(final ParameterReviewDetails parRevDetails) {
    return parRevDetails == null ? null
        : this.deliveryWrapper.getCdrReport().getReviewDetMap().get(parRevDetails.getRvwID());
  }

  /**
   * @param value
   * @return
   */
  private DataElement getDataElement(final String value) {
    DataElement dataElement;

    String myValue;

    if (value == null) {
      myValue = "";
    }
    else {
      myValue = value;
    }

    dataElement = new DataElement();
    dataElement.setValue(myValue);

    return dataElement;
  }

  /**
   * @param histEntry
   * @param reviewDetails
   * @param paramName
   * @throws DataException
   */
  private void fillDataFromRevDetails(final HistoryEntry histEntry, final ReviewDetails reviewDetails,
      final WpRespLabelResponse wpRespLabelResponse) {
    String formattedDate;
    try {
      formattedDate = ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_17, this.deliveryWrapper.getFileCreationDate(),
          DateFormat.DATE_FORMAT_16);
      histEntry.setDate(getDataElement(formattedDate));
    }
    catch (ParseException exp) {
      getLogger().error(exp.getLocalizedMessage(), exp.getCause());
    }

    histEntry.setPerformedBy(getDataElement(wpRespLabelResponse.getWpRespModel().getWpRespName()));
    histEntry.setContext(getDataElement(wpRespLabelResponse.getWpRespModel().getWpName()));
    histEntry.setTargetVariant(getDataElement(reviewDetails.getVariantName()));
    histEntry.setTestObject(getDataElement(ApicConstants.EMPTY_STRING));
    histEntry.setProgramIdentifier(getDataElement("Review A2L: " + reviewDetails.getA2lFileName()));

  }

  /**
   * @param parRevDetails
   * @param response
   * @param completed return HistoryEntry
   */
  private HistoryEntry getReviewHistory(final ParameterReviewDetails parRevDetails, final WpRespLabelResponse response,
      final boolean completed) {

    HistoryEntry histEntry = new HistoryEntry();
    DATA_REVIEW_SCORE reviewScoreEnum = getReviewScore(parRevDetails);
    ReviewDetails reviewDetails = getReviewDetails(parRevDetails);
    histEntry.setRemark(getDataElement(ApicConstants.EMPTY_STRING));

    fillDataFromRevDetails(histEntry, reviewDetails, response);
    histEntry.setDataIdentifier(getDataElement(ApicConstants.EMPTY_STRING));
    if (completed) {
      histEntry.setState(getDataElement("completed"));
    }
    else {
      histEntry.setState(getDataElement(reviewScoreEnum.getRatingDesc()));
    }

    histEntry.setProject(getDataElement("PIDC: " + this.pidcVersion.getName()));

    return histEntry;
  }

  /**
   * @param wpRespLabelResponse
   * @return
   */
  private boolean isWPRespMatching(final WpRespLabelResponse wpRespLabelResponse) {
    List<WpRespModel> wpRespModelList = this.deliveryWrapper.getInput().getWpRespModelList();
    if (!wpRespModelList.isEmpty()) {
      for (WpRespModel wpRespModel : wpRespModelList) {
        if (wpRespModel.getA2lResponsibility().getId()
            .equals(wpRespLabelResponse.getWpRespModel().getA2lResponsibility().getId()) &&
            wpRespModel.getA2lWpId().equals(wpRespLabelResponse.getWpRespModel().getA2lWpId())) {
          return true;
        }
      }
    }
    return false;
  }


  /**
   * @return the missingParamCountMap
   */
  public Map<String, Integer> getMissingParamCountMap() {
    return this.missingParamCountMap;
  }

}
