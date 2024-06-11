/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.icdm.bo.cdr.CDRResultParameterCommand;
import com.bosch.caltool.icdm.bo.cdr.CDRResultParameterLoader;
import com.bosch.caltool.icdm.bo.cdr.CDRReviewResultCommand;
import com.bosch.caltool.icdm.bo.cdr.CDRReviewResultLoader;
import com.bosch.caltool.icdm.bo.cdr.ReviewDetailDataFetcher;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParameter;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRResultParamUpdateData;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.ImportParamCommentData;
import com.bosch.caltool.icdm.model.cdr.ParameterReviewResult;
import com.bosch.caltool.icdm.model.cdr.ReviewDetailsData;


/**
 * Service class for CDR Result Parameter
 *
 * @author BRU2COB
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_CDRRESULTPARAMETER)
public class CDRResultParameterService extends AbstractRestService {


  /**
   * Get CDR Result Parameter using its id
   *
   * @param objId object's id
   * @return Rest response, with CDRResultParameter object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response get(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    CDRResultParameterLoader loader = new CDRResultParameterLoader(getServiceData());
    CDRResultParameter ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }

  /**
   * Get CDR Result Parameter using its id. Note : this is a POST request
   *
   * @param objIdSet set of object IDs
   * @return Rest response, with CDRResultParameter object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  @Path(WsCommonConstants.RWS_MULTIPLE)
  public Response getMultiple(final Set<Long> objIdSet) throws IcdmException {
    CDRResultParameterLoader loader = new CDRResultParameterLoader(getServiceData());
    Map<Long, CDRResultParameter> ret = loader.getDataObjectByID(objIdSet);
    return Response.ok(ret).build();
  }


  /**
   * Update a CDR Result Parameter record
   *
   * @param paramList object to update
   * @return Rest response, with updated CDRResultParameter object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final List<CDRResultParameter> paramList) throws IcdmException {
    List<AbstractSimpleCommand> cmdList = new ArrayList<>();
    Map<Long, CDRResultParameter> updatedMap = new HashMap<>();
    CDRResultParamUpdateData paramData = new CDRResultParamUpdateData();
    Set<Long> paramIdSet = new HashSet<>();
    Long resultId = paramList.get(0).getResultId();
    CDRReviewResultLoader cdrReviewResultLoader = new CDRReviewResultLoader(getServiceData());
    CDRReviewResult oldResult = cdrReviewResultLoader.getDataObjectByID(resultId);
    paramData.setOldReviewResult(oldResult);
    String oldRvwStatus = oldResult.getRvwStatus();
    for (CDRResultParameter param : paramList) {
      CDRResultParameterCommand cmd = new CDRResultParameterCommand(getServiceData(), param, true, false);
      cmdList.add(cmd);
      paramIdSet.add(param.getId());
    }
    executeCommand(cmdList);

    // set created and deleted Review comment history for refresh
    setRvwCmntHistoryForRefresh(cmdList, paramData);

    // execute result update if needed
    TRvwResult tRvwResult = cdrReviewResultLoader.getEntityObject(resultId);
    if ((!tRvwResult.getRvwStatus().equals(CDRConstants.REVIEW_STATUS.OPEN.getDbType())) &&
        (canUpdateResForStartTest(tRvwResult) || canUpdateResForOfficial(tRvwResult))) {
      CDRReviewResultCommand cmd = new CDRReviewResultCommand(getServiceData(),
          cdrReviewResultLoader.getDataObjectByID(tRvwResult.getResultId()), null, true, false);
      executeCommand(cmd);
    }

    for (AbstractSimpleCommand cmd : cmdList) {
      CDRResultParameter newData = ((CDRResultParameterCommand) cmd).getNewData();
      updatedMap.put(newData.getId(), newData);
    }
    StringBuilder paramIds = new StringBuilder();
    for (Long id : paramIdSet) {
      paramIds.append(id).append("-");
    }
    getLogger().info("Updated CDR Result Parameter Id : {}", paramIds.toString());
    CDRReviewResult newResult = cdrReviewResultLoader.getDataObjectByID(resultId);
    String newRvwStatus = newResult.getRvwStatus();

    paramData.setParamMap(updatedMap);
    if (!newRvwStatus.equalsIgnoreCase(oldRvwStatus)) {
      paramData.setNewReviewResult(newResult);
    }

    return Response.ok(paramData).build();
  }

  /**
   * @param cmdList
   * @param paramData
   */
  private void setRvwCmntHistoryForRefresh(final List<AbstractSimpleCommand> cmdList,
      final CDRResultParamUpdateData paramData) {
    for (AbstractSimpleCommand cmd : cmdList) {
      if (((CDRResultParameterCommand) cmd).getDeletedRvwCmntHistory() != null) {
        paramData.setDeletedRvwCmntHistory(((CDRResultParameterCommand) cmd).getDeletedRvwCmntHistory());
      }
      if (((CDRResultParameterCommand) cmd).getCreatedRvwCmntHistory() != null) {
        paramData.setNewRvwCmntHistory(((CDRResultParameterCommand) cmd).getCreatedRvwCmntHistory());
      }
    }
  }

  /**
   * Checks whether result can be updated for start and test review
   *
   * @param tRvwResult result
   * @return if result has to be updated
   */
  public boolean canUpdateResForStartTest(final TRvwResult tRvwResult) {
    boolean flag = false;
    if (checkRvwType(tRvwResult) && checkRvwStatus(tRvwResult)) {
      flag = true;
    }
    return flag;
  }

  /**
   * @param tRvwResult
   * @return
   */
  private boolean checkRvwStatus(final TRvwResult tRvwResult) {
    return (tRvwResult.getRvwStatus().equals(CDRConstants.REVIEW_STATUS.CLOSED.getDbType()) &&
        !isParamScoreGreaterThanZero(tRvwResult)) ||
        (tRvwResult.getRvwStatus().equals(CDRConstants.REVIEW_STATUS.IN_PROGRESS.getDbType()) &&
            isParamScoreGreaterThanZero(tRvwResult));
  }

  /**
   * @param tRvwResult
   * @return
   */
  private boolean checkRvwType(final TRvwResult tRvwResult) {
    return tRvwResult.getReviewType().equals(CDRConstants.REVIEW_TYPE.START.getDbType()) ||
        tRvwResult.getReviewType().equalsIgnoreCase(CDRConstants.REVIEW_TYPE.TEST.getDbType());
  }

  /**
   * Checks whether result can be updated for official review
   *
   * @param tRvwResult result
   * @return if result has to be updated
   */
  public boolean canUpdateResForOfficial(final TRvwResult tRvwResult) {
    boolean flag = false;
    if (tRvwResult.getReviewType().equalsIgnoreCase(CDRConstants.REVIEW_TYPE.OFFICIAL.getDbType()) &&
        validateParamsRvwd(tRvwResult)) {
      flag = true;
    }
    return flag;
  }

  /**
   * @param tRvwResult
   * @return
   */
  private boolean validateParamsRvwd(final TRvwResult tRvwResult) {
    return (tRvwResult.getRvwStatus().equals(CDRConstants.REVIEW_STATUS.CLOSED.getDbType()) &&
        !isAllParamsReviewed(tRvwResult)) ||
        (tRvwResult.getRvwStatus().equals(CDRConstants.REVIEW_STATUS.IN_PROGRESS.getDbType()) &&
            isAllParamsReviewed(tRvwResult));
  }

  /**
   * Checks if all params are Reviewed for official review iCDM-665
   *
   * @param resultEntity result
   * @return true if all params are reviewed
   */
  public boolean isAllParamsReviewed(final TRvwResult resultEntity) {
    // check if all params are reviewed

    Set<TRvwParameter> tRvwParameters = resultEntity.getTRvwParameters();
    boolean reviewClosed = true;
    for (TRvwParameter tRvwParameter : tRvwParameters) {
      String reviewScore = tRvwParameter.getReviewScore();
      if (reviewScore != null) {
        Integer scoreInt = Integer.valueOf(reviewScore);
        if (scoreInt < 8) {
          reviewClosed = false;
        }
      }
    }
    return reviewClosed;
  }

  /**
   * @return true if all params have score greater than zero for start/test review
   */
  private boolean isParamScoreGreaterThanZero(final TRvwResult resultEntity) {
    // check if all params are reviewed
    Set<TRvwParameter> tRvwParameters = resultEntity.getTRvwParameters();
    boolean reviewClosed = true;
    for (TRvwParameter tRvwParameter : tRvwParameters) {
      String reviewScore = tRvwParameter.getReviewScore();
      if (reviewScore != null) {
        Integer scoreInt = Integer.valueOf(reviewScore);
        if (scoreInt == 0) {
          reviewClosed = false;
        }
      }
    }
    return reviewClosed;
  }


  /**
   * @param resultId resultId
   * @param paramName paramName
   * @return ReviewDetailsData
   * @throws IcdmException IcdmException
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_CDR_RESULT_PARAMETER)
  @CompressData
  public ReviewDetailsData getReviewDetailsDataByResultId(
      @QueryParam(WsCommonConstants.RWS_QP_RESULT_ID) final Long resultId,
      @QueryParam(WsCommonConstants.RWS_QP_PARAM_NAME) final String paramName)
      throws IcdmException {
    ReviewDetailDataFetcher fetcher = new ReviewDetailDataFetcher(getServiceData());
    return fetcher.getReviewDetailsData(resultId, paramName);
  }

  /**
   * @param paramIds - list of parameter Ids
   * @return ParameterReviewResult in response
   * @throws IcdmException error during webservice call
   * @throws IOException input,output exception
   * @throws ClassNotFoundException class not found
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_PARAMETER_REVIEW_RESULT)
  @CompressData
  public ParameterReviewResult[] getParameterReviewResult(
      @QueryParam(WsCommonConstants.RWS_QP_PARAM_ID) final List<Long> paramIds)
      throws IcdmException, ClassNotFoundException, IOException {
    CDRResultParameterLoader loader = new CDRResultParameterLoader(getServiceData());
    return loader.getParameterReviewResult(paramIds);
  }

  /**
   * Method to import review comments from another review
   *
   * @param paramInputData input data
   * @return updated param map
   * @throws IcdmException excep
   */
  @PUT
  @Path(WsCommonConstants.RWS_COPY_COMMENT_FROM_RESULT)
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response importReviewComment(final ImportParamCommentData paramInputData) throws IcdmException {

    List<AbstractSimpleCommand> cmdList = new ArrayList<>();
    CDRResultParameterLoader paramLoader = new CDRResultParameterLoader(getServiceData());
    Map<Long, CDRResultParameter> retMap = new HashMap<>();
    Map<Long, CDRResultParameter> sourceParamsMap = paramLoader.getParamsByResultObj(
        new CDRReviewResultLoader(getServiceData()).getEntityObject(paramInputData.getSourceResultId()));
    // using a collection to validate imported comments as comments can be imported for multiple lables from nat page
    Set<String> sourceCommentsSet = new HashSet<>();
    for (Long resultParamId : paramInputData.getParamList()) {
      CDRResultParameter resultParam = paramLoader.getDataObjectByID(resultParamId);
      String destParamComment = resultParam.getRvwComment();
      boolean updateComment = false;
      CDRResultParameter sourceParam = sourceParamsMap.get(resultParam.getParamId());
      if (sourceParam != null) {
        String sourceComment = paramInputData.isIncludeScore() ? getScoreBasedCmnt(sourceCommentsSet, sourceParam)
            : sourceParam.getRvwComment();
        updateComment =
            setCmntAtDestParam(paramInputData, sourceCommentsSet, resultParam, destParamComment, sourceComment);
      }
      if (updateComment) {
        CDRResultParameterCommand cmd = new CDRResultParameterCommand(getServiceData(), resultParam, true, false);
        cmdList.add(cmd);
      }
      else {
        retMap.put(resultParamId, resultParam);
      }
    }
    if (!cmdList.isEmpty()) {
      executeCommand(cmdList);
    }

    for (AbstractSimpleCommand cmd : cmdList) {
      CDRResultParameter updatedParam = ((CDRResultParameterCommand) cmd).getNewData();
      retMap.put(updatedParam.getId(), updatedParam);
    }
    // check if the imported source comment is empty
    if (CommonUtils.isNullOrEmpty(sourceCommentsSet)) {
      throw new IcdmException("RVW_RESULT_COMMENT_IMPORT.EMPTY_COMMENT");
    }
    return Response.ok(retMap).build();
  }

  /**
   * @param paramInputData
   * @param sourceCommentsSet
   * @param resultParam
   * @param destParamComment
   * @param updateComment
   * @param sourceComment
   * @return
   */
  private boolean setCmntAtDestParam(final ImportParamCommentData paramInputData, final Set<String> sourceCommentsSet,
      final CDRResultParameter resultParam, final String destParamComment, final String sourceComment) {

    boolean updateComment = false;
    if (!CommonUtils.isEmptyString(sourceComment)) {
      if (paramInputData.isOverWriteComments()) {
        resultParam.setRvwComment(sourceComment);
        updateComment = true;
      }
      else {
        if ((destParamComment == null) || destParamComment.isEmpty()) {
          resultParam.setRvwComment(sourceComment);
          updateComment = true;
        }
      }
      sourceCommentsSet.add(sourceComment);
    }
    return updateComment;
  }


  /**
   * @param sourceCommentsSet
   * @param sourceParam
   * @param sourceComment
   * @return
   */
  private String getScoreBasedCmnt(final Set<String> sourceCommentsSet, final CDRResultParameter sourceParam) {
    String sourceComment = null;
    if (Integer.valueOf(sourceParam.getReviewScore()) >= 7) {
      sourceComment = sourceParam.getRvwComment();
    }
    // check if the comment of the label in source review result is not an empty comment,if not empty exception should
    // not be thrown
    if (!CommonUtils.isEmptyString(sourceParam.getRvwComment())) {
      sourceCommentsSet.add(sourceComment);
    }
    return sourceComment;
  }
}
