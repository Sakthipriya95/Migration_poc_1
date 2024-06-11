/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.cns.CHANGE_OPERATION;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.datamodel.core.cns.ChangeDataCreator;
import com.bosch.caltool.icdm.model.cdr.CDRResultParamUpdateData;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.ImportParamCommentData;
import com.bosch.caltool.icdm.model.cdr.ParameterReviewResult;
import com.bosch.caltool.icdm.model.cdr.ReviewDetailsData;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.ChangeHandler;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapper;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for CDR Result Parameter
 *
 * @author BRU2COB
 */
public class CDRResultParameterServiceClient extends AbstractRestServiceClient {

  /**
   * Constructor
   */
  public CDRResultParameterServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_CDRRESULTPARAMETER);
  }


  /**
   * Get CDR Result Parameter using its id
   *
   * @param objId object's id
   * @return CDRResultParameter object
   * @throws ApicWebServiceException exception while invoking service
   */
  public CDRResultParameter get(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, CDRResultParameter.class);
  }

  /**
   * Get CDR Result Parameters using its ids. Note : this is a POST request
   *
   * @param objIdSet set of object ids
   * @return CDRResultParameter object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Map<Long, CDRResultParameter> getMultiple(final Set<Long> objIdSet) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_MULTIPLE);

    GenericType<Map<Long, CDRResultParameter>> type = new GenericType<Map<Long, CDRResultParameter>>() {};
    return post(wsTarget, objIdSet, type);
  }


  /**
   * Update a CDR Result Parameter record
   *
   * @param paramList object to update
   * @return updated CDRResultParameter object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Map<Long, CDRResultParameter> update(final List<CDRResultParameter> paramList) throws ApicWebServiceException {
    CDRResultParamUpdateData ret = put(getWsBase(), paramList, CDRResultParamUpdateData.class);

    Map<Long, IModel> newDataModelMap = new HashMap<>();
    Map<Long, IModel> oldDataModelMap = new HashMap<>();

    newDataModelMap.putAll(ret.getParamMap());
    for (CDRResultParameter oldParam : paramList) {
      oldDataModelMap.put(oldParam.getId(), oldParam);
    }
    
    if (ret.getNewReviewResult() != null) {
      newDataModelMap.put(ret.getNewReviewResult().getId(), ret.getNewReviewResult());
      oldDataModelMap.put(ret.getOldReviewResult().getId(), ret.getOldReviewResult());
    }
    
    List<ChangeData<IModel>> chgDataList =
        (new ChangeDataCreator<IModel>()).createDataForUpdate(0L, oldDataModelMap, newDataModelMap);

    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(chgDataList));

    displayMessage(CHANGE_OPERATION.UPDATE, newDataModelMap.values());
    
    triggerChangeEventForReviewCmntHistory(ret);

    return ret.getParamMap();

  }
  
  private void triggerChangeEventForReviewCmntHistory(CDRResultParamUpdateData cdrResultParamUpdateData) {
    
    Map<Long, IModel> createdDataModelMap = new HashMap<>();
    
    Map<Long, IModel> deletedDataModelMap = new HashMap<>();
    
    //add newly created review comment history to change data for refresh
    if(cdrResultParamUpdateData.getNewRvwCmntHistory()!=null) {
      createdDataModelMap.put(cdrResultParamUpdateData.getNewRvwCmntHistory().getId(), cdrResultParamUpdateData.getNewRvwCmntHistory());
    }
    
    if(cdrResultParamUpdateData.getDeletedRvwCmntHistory()!=null) {
      deletedDataModelMap.put(cdrResultParamUpdateData.getDeletedRvwCmntHistory().getId(), cdrResultParamUpdateData.getDeletedRvwCmntHistory());
    }
    
    List<ChangeData<IModel>> chgDataList =
        (new ChangeDataCreator<IModel>()).createDataForCreate(0L, createdDataModelMap.values());
    
    List<ChangeData<IModel>> chgDataListForDelete = (new ChangeDataCreator<IModel>()).createDataForDelete(0L, deletedDataModelMap.values());
    
    chgDataList.addAll(chgDataListForDelete);
    
    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(chgDataList));
    
    displayMessage(chgDataList);
  }


  /**
   * @param resultId resultId
   * @param paramName paramName
   * @return ReviewDetailsData object
   * @throws ApicWebServiceException Exception
   */
  public ReviewDetailsData getReviewDetailsDataByResultId(final Long resultId, final String paramName)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_CDR_RESULT_PARAMETER)
        .queryParam(WsCommonConstants.RWS_QP_RESULT_ID, resultId)
        .queryParam(WsCommonConstants.RWS_QP_PARAM_NAME, paramName);
    return get(wsTarget, ReviewDetailsData.class);
  }

  /**
   * @param paramIds - parameter Id
   * @return ParameterReviewResult
   * @throws ApicWebServiceException Error during websevice call
   */
  public List<ParameterReviewResult> getParameterReviewResult(final List<Long> paramIds)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_PARAMETER_REVIEW_RESULT);

    for (Long paramId : paramIds) {
      wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_PARAM_ID, paramId);
    }
    GenericType<List<ParameterReviewResult>> type = new GenericType<List<ParameterReviewResult>>() {};
    return get(wsTarget, type);
  }


  /**
   * Method to import review comments from another review
   *
   * @param paramMap input param map
   * @param overWriteComments true if comments need to be over written
   * @param sourceResultId source result id
   * @param includeScore true if score needs to be considered for take over comments
   * @return updated param map
   * @throws ApicWebServiceException excep
   */
  public Map<Long, CDRResultParameter> importReviewComment(final Map<Long, CDRResultParameter> paramMap,
      final boolean overWriteComments, final Long sourceResultId, final boolean includeScore)
      throws ApicWebServiceException {
    ImportParamCommentData inputData = new ImportParamCommentData();
    inputData.setParamList(paramMap.keySet().stream().collect(Collectors.toList()));
    inputData.setOverWriteComments(overWriteComments);
    inputData.setSourceResultId(sourceResultId);
    inputData.setIncludeScore(includeScore);
    IMapper IMPORT_COMMENT_INPUT_MAPPER = obj -> {
      ImportParamCommentData input = ((ImportParamCommentData) obj);
      return input.getParamList().stream().map(paramMap::get).collect(Collectors.toList());
    };
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_COPY_COMMENT_FROM_RESULT);
    GenericType<Map<Long, CDRResultParameter>> type = new GenericType<Map<Long, CDRResultParameter>>() {};
    Map<Long, CDRResultParameter> retMap = update(wsTarget, inputData, type, IMPORT_COMMENT_INPUT_MAPPER, null);
    LOGGER.debug("CDRResultParameter objects have been updated. No of CDRResultParameter updated : {}", retMap.size());
    return retMap;

  }
}
