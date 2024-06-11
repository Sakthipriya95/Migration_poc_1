/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.a2l.A2lWpResponsibilityStatusLoader;
import com.bosch.caltool.icdm.bo.a2l.FunctionLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.cdr.CDRReviewResultCommand;
import com.bosch.caltool.icdm.bo.cdr.CDRReviewResultLoader;
import com.bosch.caltool.icdm.bo.cdr.RuleSetLoader;
import com.bosch.caltool.icdm.bo.cdr.RvwParticipantLoader;
import com.bosch.caltool.icdm.bo.cdr.review.ReviewProcess;
import com.bosch.caltool.icdm.bo.cdr.review.ReviewRuleSetData;
import com.bosch.caltool.icdm.bo.cdr.review.ReviewedInfo;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.bo.general.IcdmFilesLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.model.a2l.A2lWpRespStatusUpdationModel;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.CDR_SOURCE_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.ReviewVariantWrapper;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.model.cdr.RvwParticipant;
import com.bosch.caltool.icdm.model.cdr.review.ReviewInput;
import com.bosch.caltool.icdm.model.cdr.review.ReviewOutput;
import com.bosch.caltool.icdm.model.general.CommonParamKey;


/**
 * @author bru2cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_REVIEW)
public class ReviewService extends AbstractRestService {

  /**
   * Run review
   *
   * @param multiPart multipart data
   * @param reviewInputData review input data
   * @return service response as a zip file
   * @throws IcdmException error during service
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.MULTIPART_FORM_DATA })
  @Path(WsCommonConstants.RWS_PERFORM_REVIEW)
  @CompressData
  public Response performReview(final FormDataMultiPart multiPart,
      @FormDataParam(WsCommonConstants.RWS_QP_REVIEW_OBJECT) final ReviewInput reviewInputData)
      throws IcdmException {

    getLogger().debug("Starting ReviewService.performReview()...");

    ReviewProcess reviewProcess = createReviewProcessObject(multiPart, reviewInputData);
    ReviewedInfo reviewOutput = reviewProcess.performReview();

    CDRReviewResult result = new CDRReviewResult();
    ReviewOutput reviewSummary = createORUpdateResults(reviewOutput,
        getResultToCreate(reviewInputData, reviewProcess.getReviewOutput(), true, result), reviewInputData, false);

    getLogger().debug("ReviewService.performReview() completed");

    return Response.ok(reviewSummary).build();

  }


  /**
   * @param multiPart
   * @param reviewInputData
   * @return
   * @throws InvalidInputException
   * @throws IcdmException
   * @throws UnAuthorizedAccessException
   */
  private ReviewProcess createReviewProcessObject(final FormDataMultiPart multiPart, final ReviewInput reviewInputData)
      throws IcdmException {

    Map<String, byte[]> filesByteArrMap = new HashMap<>();

    List<FormDataBodyPart> filesList = multiPart.getFields(WsCommonConstants.REVIEW_FILE_MULTIPART);
    if (filesList != null) {
      filesByteArrMap.putAll(addFileListToByteArrMap(filesList, WsCommonConstants.REVIEW_FILE_NAME));
    }

    List<FormDataBodyPart> ssdReleaseFileList = multiPart.getFields(WsCommonConstants.SSD_RULE_FILE_MULTIPART);
    if (ssdReleaseFileList != null) {
      filesByteArrMap.putAll(addFileListToByteArrMap(ssdReleaseFileList, WsCommonConstants.SSD_RELEASE_FILE_NAME));
    }

    List<FormDataBodyPart> labFunFileList = multiPart.getFields(WsCommonConstants.LAB_FUN_FILE_MULTIPART);
    if (labFunFileList != null) {
      filesByteArrMap.putAll(addFileListToByteArrMap(labFunFileList, WsCommonConstants.LAB_FUN_FILE_NAME));
    }

    ReviewProcess process = new ReviewProcess(reviewInputData, filesByteArrMap, getServiceData());

    setGrpWPForMapped(process.getReviewOutput(), reviewInputData);

    return process;
  }


  /**
   * @param fileList
   * @param fileName
   * @return
   * @throws IcdmException
   */
  private Map<String, byte[]> addFileListToByteArrMap(final List<FormDataBodyPart> fileList, final String fileName)
      throws IcdmException {

    Map<String, byte[]> filesByteArrMap = new HashMap<>();
    for (FormDataBodyPart field : fileList) {
      String filePath = field.getHeaders().get(fileName).get(0);
      InputStream unzipIfZippedStream = ZipUtils.unzipIfZipped(field.getValueAs(InputStream.class));
      addFileBytesToMap(filesByteArrMap, getUTFFilePath(filePath), unzipIfZippedStream);
    }
    return filesByteArrMap;
  }

  /**
   * Run review
   *
   * @param multiPart multipart data
   * @param reviewInputData review input data
   * @return service response as a zip file
   * @throws IcdmException error during service
   */
  @PUT
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.MULTIPART_FORM_DATA })
  @Path(WsCommonConstants.RWS_UPDATE_REVIEW)
  @CompressData
  public Response updatePerformReview(final FormDataMultiPart multiPart,
      @FormDataParam(WsCommonConstants.RWS_QP_REVIEW_OBJECT) final ReviewInput reviewInputData)
      throws IcdmException {

    getLogger().debug("Starting ReviewService.updatePerformReview()...");

    ReviewProcess reviewProcess = createReviewProcessObject(multiPart, reviewInputData);
    ReviewedInfo reviewOutput = reviewProcess.performReview();

    CDRReviewResult result = new CDRReviewResult();
    if (null != reviewInputData.getResultData().getCanceledResultId()) {
      result = new CDRReviewResultLoader(getServiceData())
          .getDataObjectByID(reviewInputData.getResultData().getCanceledResultId());
    }

    ReviewOutput reviewSummary = createORUpdateResults(reviewOutput,
        getResultToCreate(reviewInputData, reviewProcess.getReviewOutput(), true, result), reviewInputData, true);

    getLogger().debug("ReviewService.updatePerformReview() completed");

    return Response.ok(reviewSummary).build();
  }

  /**
   * @param filesStreamMap
   * @param filePath
   * @param unzipIfZippedStream
   * @throws IcdmException
   */
  private void addFileBytesToMap(final Map<String, byte[]> filesStreamMap, final String filePath,
      final InputStream unzipIfZippedStream)
      throws IcdmException {
    try {
      byte[] inputFileBytes = IOUtils.toByteArray(unzipIfZippedStream);
      filesStreamMap.put(filePath, inputFileBytes);
    }
    catch (IOException exp) {
      throw new IcdmException(exp.getLocalizedMessage(), exp);
    }
  }

  private ReviewOutput createORUpdateResults(final ReviewedInfo reviewData, final CDRReviewResult cdrReviewResult,
      final ReviewInput reviewInputData, final boolean isUpdate)
      throws IcdmException {

    CDRReviewResultCommand resultCmd =
        new CDRReviewResultCommand(getServiceData(), cdrReviewResult, reviewData, reviewInputData, isUpdate, false);
    executeCommand(resultCmd);


    return createOutputSummary(resultCmd, reviewData);
  }


  /**
   * @param resultCmd
   * @param reviewOutput
   * @return
   * @throws UnAuthorizedAccessException
   * @throws DataException
   */
  private ReviewOutput createOutputSummary(final CDRReviewResultCommand resultCmd, final ReviewedInfo reviewOutput)
      throws DataException, UnAuthorizedAccessException {
    CDRReviewResult result = resultCmd.getNewData();
    ReviewOutput output = new ReviewOutput();
    output.setA2lFileName(reviewOutput.getA2lFileContents().getFileName());
    output.setDeltaReviewValid(resultCmd.isDeltaReviewValid());
    RvwParticipantLoader participantsLoader = new RvwParticipantLoader(getServiceData());
    output.setRvwParticipantsList(
        participantsLoader.getByResultObj(new CDRReviewResultLoader(getServiceData()).getEntityObject(result.getId()))
            .values().stream().collect(Collectors.toList()));
    for (RvwParticipant participant : output.getRvwParticipantsList()) {
      if (CDRConstants.REVIEW_USER_TYPE
          .getType(participant.getActivityType()) == CDRConstants.REVIEW_USER_TYPE.AUDITOR) {
        output.setAuditorName(participant.getName());
      }
      if (CDRConstants.REVIEW_USER_TYPE
          .getType(participant.getActivityType()) == CDRConstants.REVIEW_USER_TYPE.CAL_ENGINEER) {
        output.setCalEngineerName(participant.getName());
      }
    }


    output.setCdrResult(result);
    output.setNoOfReviewedFunctions(getNoOfReviewedFunctions(reviewOutput));
    output.setNoOfReviewedParam(getNoOfReviewedParam(reviewOutput));
    output.setParamsNotReviewedInA2l(getNoOfNotRvwdParamsInA2l(reviewOutput));
    output.setParamsNotRvwdInRuleset(reviewOutput.getParamNotInRuleset().size());
    output.setParamsNotRvwdWithoutRule(reviewOutput.getParamWithoutRule().size());
    if (null != result.getPrimaryVariantId()) {
      output.setPidcVariantName(
          new PidcVariantLoader(getServiceData()).getDataObjectByID(result.getPrimaryVariantId()).getName());
    }
    output.setPidcVersion(reviewOutput.getPidcDetails().getPidcVersion());
    output.setWorkPackageGroupName(result.getGrpWorkPkg());
    output.setRvwVariant(resultCmd.getRvwVariant());
    output.setRvwCreatedUser(result.getCreatedUser());

    A2lWpRespStatusUpdationModel responseUpdationModel = new A2lWpResponsibilityStatusLoader(getServiceData())
        .getOutputUpdationModel(resultCmd.getListOfNewlyCreatedA2lWpRespStatus(),
            resultCmd.getA2lWpRespStatusBeforeUpd(), resultCmd.getA2lWpRespStatusAfterUpd());

    output.setA2lWpResponsibilityBeforeUpdate(responseUpdationModel.getA2lWpRespStatusToBeUpdatedMap());
    output.setA2lWpResponsibilityAfterUpdate(responseUpdationModel.getA2lWpRespStatusMapAfterUpdate());
    output.setListOfNewlyCreatedA2lWpRespStatus(responseUpdationModel.getListOfNewlyCreatedA2lWpRespStatus());

    return output;
  }

  /**
   * @param reviewOutput
   * @return the noOfReviewedParam
   */
  private int getNoOfReviewedFunctions(final ReviewedInfo reviewOutput) {
    int fucnsReviewed = 0;
    SortedSet<?> cdrFunctionsList = reviewOutput.getCdrFunctionsList();
    if ((cdrFunctionsList == null) || cdrFunctionsList.isEmpty()) {
      return fucnsReviewed;
    }
    fucnsReviewed = cdrFunctionsList.size();
    return fucnsReviewed;
  }

  /**
   * @return the noOfReviewedFunctions
   */
  private int getNoOfReviewedParam(final ReviewedInfo reviewOutput) {
    int parametersReviewed = 0;
    Set<?> cdrFuncParams = reviewOutput.getCdrFuncParams();
    if (cdrFuncParams == null) {
      return parametersReviewed;
    }

    return cdrFuncParams.size();
  }


  /**
   * @return no of not reviewd params
   */
  private int getNoOfNotRvwdParamsInA2l(final ReviewedInfo reviewOutput) {

    Set<String> paramsRvwd = new TreeSet<>();
    Set<String> paramsNotRvwd = new TreeSet<>();
    Set<String> a2lParams = new TreeSet<>();
    Set<String> paramsNotInA2L = new TreeSet<>();
    if (reviewOutput.getCdrFuncParams() != null) {
      for (Object obj : reviewOutput.getCdrFuncParams()) {
        if (obj instanceof RuleSetParameter) {
          paramsRvwd.add(((RuleSetParameter) obj).getName());
        }
      }
    }
    for (String paramtoBeRvwd : reviewOutput.getCalDataMap().keySet()) {
      if (!paramsRvwd.contains(paramtoBeRvwd)) {
        paramsNotRvwd.add(paramtoBeRvwd);
      }
    }
    for (Characteristic a2lParam : reviewOutput.getA2lFileContents().getAllSortedLabels(true)) {
      a2lParams.add(a2lParam.getName());
    }

    for (String ntRvwdParam : paramsNotRvwd) {
      if (!a2lParams.contains(ntRvwdParam.trim())) {
        paramsNotInA2L.add(ntRvwdParam);
      }
    }
    return paramsNotInA2L.size();
  }


  /**
   * @param multiPart multipart data
   * @param reviewInputData ReviewInput
   * @return service response as a zip file
   * @throws IcdmException error during service
   */
  @PUT
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.MULTIPART_FORM_DATA })
  @Path(WsCommonConstants.RWS_UPDATE_CANCELLED_REVIEW)
  @CompressData
  public Response updateCancelledReview(final FormDataMultiPart multiPart,
      @FormDataParam(WsCommonConstants.RWS_REVIEW_OBJECT) final ReviewInput reviewInputData)
      throws IcdmException {

    ReviewedInfo reviewOutput = convertFormDataToReviewOutput(multiPart, reviewInputData);

    CDRReviewResult result = new CDRReviewResult();
    ReviewVariantWrapper reviewVariantWrapper = updateCancelledResults(reviewOutput,
        getResultToCreate(reviewInputData, reviewOutput, false, result), reviewInputData);
    return Response.ok(reviewVariantWrapper).build();

  }

  /**
   * Save cancelled review
   *
   * @param multiPart multipart data
   * @param reviewInputData ReviewInput
   * @return service response as a zip file
   * @throws IcdmException error during service
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.MULTIPART_FORM_DATA })
  @Path(WsCommonConstants.RWS_SAVE_CANCELLED_REVIEW)
  @CompressData
  public Response saveCancelledReview(final FormDataMultiPart multiPart,
      @FormDataParam(WsCommonConstants.RWS_REVIEW_OBJECT) final ReviewInput reviewInputData)
      throws IcdmException {

    ReviewedInfo reviewOutput = convertFormDataToReviewOutput(multiPart, reviewInputData);

    CDRReviewResult result = new CDRReviewResult();
    ReviewVariantWrapper reviewVariantWrapper = saveCancelledResults(reviewOutput,
        getResultToCreate(reviewInputData, reviewOutput, false, result), reviewInputData);

    return Response.ok(reviewVariantWrapper).build();
  }

  /**
   * @param multiPart
   * @param reviewInputData
   * @return
   * @throws InvalidInputException
   * @throws IcdmException
   * @throws DataException
   * @throws UnAuthorizedAccessException
   */
  private ReviewedInfo convertFormDataToReviewOutput(final FormDataMultiPart multiPart,
      final ReviewInput reviewInputData)
      throws IcdmException {
    Map<String, byte[]> inputFileStreamMap = new HashMap<>();
    List<FormDataBodyPart> filesList = multiPart.getFields(WsCommonConstants.REVIEW_FILE_MULTIPART);
    // add review files
    addRvwFile(inputFileStreamMap, filesList);
    List<FormDataBodyPart> ssdReleaseFileList = multiPart.getFields(WsCommonConstants.SSD_RULE_FILE_MULTIPART);
    // add ssd release files
    addSsdRealeaseFile(inputFileStreamMap, ssdReleaseFileList);

    List<FormDataBodyPart> labFunFileList = multiPart.getFields(WsCommonConstants.LAB_FUN_FILE_MULTIPART);
    // add lab fun file
    addLabFunFile(inputFileStreamMap, labFunFileList);

    ReviewedInfo reviewOutput = new ReviewedInfo();
    reviewOutput.setFilesStreamMap(inputFileStreamMap);

    if (null != reviewInputData.getRvwWpAndRespModelSet()) {
      reviewOutput.setRvwWpAndRespModelSet(reviewInputData.getRvwWpAndRespModelSet());
    }

    // For Saving SSD Rules when cancelling a review
    saveSSDRulesOnCancelRvw(reviewInputData, reviewOutput);

    // For Storing Secondary ruleset result
    saveSecRuleSetResult(reviewInputData, reviewOutput);

    setGrpWPForMapped(reviewOutput, reviewInputData);
    return reviewOutput;
  }


  /**
   * @param reviewInputData
   * @param reviewOutput
   * @throws DataException
   * @throws UnAuthorizedAccessException
   */
  private void saveSSDRulesOnCancelRvw(final ReviewInput reviewInputData, final ReviewedInfo reviewOutput)
      throws DataException, UnAuthorizedAccessException {
    if (null != reviewInputData.getRulesData().getSsdReleaseId()) {
      PidcA2l pidcA2l =
          new PidcA2lLoader(getServiceData()).getDataObjectByID(reviewInputData.getPidcData().getPidcA2lId());
      reviewOutput.setSsdSoftwareVersionId(pidcA2l.getSsdSoftwareVersionId());
      ReviewRuleSetData ruleData = new ReviewRuleSetData();
      ruleData.setSsdReleaseID(reviewInputData.getRulesData().getSsdReleaseId());
      ruleData.setSsdVersionID(reviewOutput.getSsdSoftwareVersionId());
      reviewOutput.getSecRuleSetDataList().add(ruleData);
    }
  }


  /**
   * @param reviewInputData
   * @param reviewOutput
   * @throws DataException
   * @throws UnAuthorizedAccessException
   */
  private void saveSecRuleSetResult(final ReviewInput reviewInputData, final ReviewedInfo reviewOutput)
      throws DataException, UnAuthorizedAccessException {
    if ((null != reviewInputData.getRulesData().getSecondaryRuleSetIds()) &&
        !reviewInputData.getRulesData().getSecondaryRuleSetIds().isEmpty()) {
      for (Long secRuleSetId : reviewInputData.getRulesData().getSecondaryRuleSetIds()) {
        RuleSet secondaryRuleSet = new RuleSetLoader(getServiceData()).getDataObjectByID(secRuleSetId);
        ReviewRuleSetData ruleData = new ReviewRuleSetData();
        ruleData.setRuleSet(secondaryRuleSet);
        reviewOutput.getSecRuleSetDataList().add(ruleData);
      }
    }
  }


  /**
   * @param inputFileStreamMap
   * @param labFunFileList
   * @throws IcdmException
   */
  private void addLabFunFile(final Map<String, byte[]> inputFileStreamMap, final List<FormDataBodyPart> labFunFileList)
      throws IcdmException {
    if (labFunFileList != null) {
      for (FormDataBodyPart field : labFunFileList) {
        InputStream unzippedInputStream = ZipUtils.unzipIfZipped(field.getValueAs(InputStream.class));
        String filePath = field.getHeaders().get(WsCommonConstants.LAB_FUN_FILE_NAME).get(0);
        addFileBytesToMap(inputFileStreamMap, filePath, unzippedInputStream);
      }
    }
  }


  /**
   * @param inputFileStreamMap
   * @param ssdReleaseFileList
   * @throws IcdmException
   */
  private void addSsdRealeaseFile(final Map<String, byte[]> inputFileStreamMap,
      final List<FormDataBodyPart> ssdReleaseFileList)
      throws IcdmException {
    if (ssdReleaseFileList != null) {
      for (FormDataBodyPart field : ssdReleaseFileList) {
        InputStream unzippedInputStream = ZipUtils.unzipIfZipped(field.getValueAs(InputStream.class));
        String filePath = field.getHeaders().get(WsCommonConstants.SSD_RELEASE_FILE_NAME).get(0);
        addFileBytesToMap(inputFileStreamMap, filePath, unzippedInputStream);
      }
    }
  }


  /**
   * @param inputFileStreamMap
   * @param filesList
   * @throws IcdmException
   */
  private void addRvwFile(final Map<String, byte[]> inputFileStreamMap, final List<FormDataBodyPart> filesList)
      throws IcdmException {
    if (filesList != null) {
      for (FormDataBodyPart field : filesList) {
        String filePath = field.getHeaders().get(WsCommonConstants.REVIEW_FILE_NAME).get(0);
        InputStream unzippedInputStream = ZipUtils.unzipIfZipped(field.getValueAs(InputStream.class));
        addFileBytesToMap(inputFileStreamMap, filePath, unzippedInputStream);
      }
    }
  }

  /**
   * @param reviewInputData
   * @param reviewOutput
   * @param canFinish
   * @param result
   * @return
   */
  private CDRReviewResult getResultToCreate(final ReviewInput reviewInputData, final ReviewedInfo reviewOutput,
      final boolean canFinish, final CDRReviewResult result) {

    result.setDeltaReviewType(reviewInputData.getDeltaReviewType());
    result.setDescription(reviewInputData.getDescription());
    result.setGrpWorkPkg(reviewOutput.getGrpWorkPackageName());
    result.setOrgResultId(reviewInputData.getResultData().getParentResultId());
    result.setPidcA2lId(reviewInputData.getPidcData().getPidcA2lId());
    result.setReviewType(reviewInputData.getReviewType());
    result.setRsetId(reviewInputData.getRulesData().getPrimaryRuleSetId());
    String rvwStatus = CDRConstants.REVIEW_STATUS.OPEN.getDbType();
    if (canFinish) {
      rvwStatus = CDRConstants.REVIEW_STATUS.IN_PROGRESS.getDbType();
    }
    // db type for in progress
    result.setRvwStatus(rvwStatus);
    result.setSourceType(reviewInputData.getSourceType());
    result.setPrimaryVariantId(reviewInputData.getPidcData().getSelPIDCVariantId());
    result.setWpDefnVersId(reviewInputData.getA2lWpDefVersId());
    result.setObdFlag(reviewInputData.getObdFlag());

    return result;
  }

  /**
   * @param reviewData
   * @param cdrReviewResult
   * @param reviewInputData
   * @param reviewOutput
   * @return
   * @throws IcdmException
   */
  private ReviewVariantWrapper saveCancelledResults(final ReviewedInfo reviewOutput,
      final CDRReviewResult cdrReviewResult, final ReviewInput reviewInputData)
      throws IcdmException {
    ReviewVariantWrapper reviewVariantWrapper = new ReviewVariantWrapper();

    setCDRFuctions(reviewOutput, reviewInputData);
    CDRReviewResultCommand resultCmd;
    resultCmd =
        new CDRReviewResultCommand(getServiceData(), cdrReviewResult, reviewOutput, reviewInputData, false, false);
    executeCommand(resultCmd);

    reviewVariantWrapper.setRvwVariant(resultCmd.getRvwVariant());
    reviewVariantWrapper.setCdrReviewResult(resultCmd.getNewData());
    return reviewVariantWrapper;

  }

  /**
   * @param reviewOutput
   * @param reviewInputData
   * @throws DataException
   * @throws UnAuthorizedAccessException
   */
  private void setCDRFuctions(final ReviewedInfo reviewOutput, final ReviewInput reviewInputData)
      throws DataException, UnAuthorizedAccessException {
    if (CommonUtils.isNotNull(reviewInputData.getSelReviewFuncs()) &&
        CommonUtils.isNotEmpty(reviewInputData.getSelReviewFuncs())) {
      Map<String, com.bosch.caltool.icdm.model.a2l.Function> funcMappingMap =
          new FunctionLoader(getServiceData()).getFunctionsByName(reviewInputData.getSelReviewFuncs(),
              CommonUtils.isNotEmpty(reviewOutput.getUnassParaminReview()));
      SortedSet<com.bosch.caltool.icdm.model.a2l.Function> reviewFuncsSet = new TreeSet<>(funcMappingMap.values());
      if (CommonUtils.isNotEmpty(reviewFuncsSet)) {
        reviewOutput.setCdrFunctionsList(reviewFuncsSet);
      }
    }
  }


  private ReviewVariantWrapper updateCancelledResults(final ReviewedInfo reviewOutput,
      final CDRReviewResult cdrReviewResult, final ReviewInput reviewInputData)
      throws IcdmException {
    ReviewVariantWrapper reviewVariantWrapper = new ReviewVariantWrapper();
    setCDRFuctions(reviewOutput, reviewInputData);
    CDRReviewResultCommand resultCmd;
    cdrReviewResult.setId(reviewInputData.getResultData().getCanceledResultId());
    cdrReviewResult.setVersion(reviewInputData.getReviewVersion());
    resultCmd =
        new CDRReviewResultCommand(getServiceData(), cdrReviewResult, reviewOutput, reviewInputData, true, false);

    executeCommand(resultCmd);

    reviewVariantWrapper.setRvwVariant(resultCmd.getRvwVariant());
    reviewVariantWrapper.setCdrReviewResult(resultCmd.getNewData());

    return reviewVariantWrapper;
  }


  /**
   * @param sourceType
   * @param cmdModCDRResult
   * @throws DataException
   * @throws UnAuthorizedAccessException
   */
  private void setGrpWPForMapped(final ReviewedInfo reviewOutput, final ReviewInput reviewInputData) {
    final CDR_SOURCE_TYPE sourceType = CDRConstants.CDR_SOURCE_TYPE.getType(reviewInputData.getSourceType());
    if (sourceType == CDR_SOURCE_TYPE.WP) {
      setFieldsForWPResp(reviewOutput, reviewInputData);
    }
    // ICDM-2026
    else if (sourceType == CDR_SOURCE_TYPE.LAB_FILE) {
      reviewOutput.setGrpWorkPackageName(CDR_SOURCE_TYPE.LAB_FILE.getUIType());
    }
    else if ((sourceType == CDR_SOURCE_TYPE.FUN_FILE) || (sourceType == CDR_SOURCE_TYPE.A2L_FILE)) {
      reviewOutput.setGrpWorkPackageName(CDR_SOURCE_TYPE.FUN_FILE.getUIType());
    }
    else if (sourceType == CDR_SOURCE_TYPE.REVIEW_FILE) {
      reviewOutput.setGrpWorkPackageName(CDR_SOURCE_TYPE.REVIEW_FILE.getUIType());
    }
    else if (sourceType == CDR_SOURCE_TYPE.MONICA_FILE) {
      reviewOutput.setGrpWorkPackageName(CDR_SOURCE_TYPE.MONICA_FILE.getUIType());
    }
    else if (sourceType == CDR_SOURCE_TYPE.COMPLI_PARAM) {// Task 237135
      reviewOutput.setGrpWorkPackageName(CDR_SOURCE_TYPE.COMPLI_PARAM.getUIType());
    }
  }


  /**
   * @throws DataException
   * @throws UnAuthorizedAccessException
   */
  private void setFieldsForWPResp(final ReviewedInfo reviewOutput, final ReviewInput reviewInputData) {

    if (reviewInputData.getWpRespName() != null) {
      reviewOutput.setGrpWorkPackageName(reviewInputData.getWpRespName());
    }
    else if ((reviewInputData.getRvwWpAndRespModelSet() != null) &&
        (reviewInputData.getRvwWpAndRespModelSet().size() > 1)) {
      setGrpWpName(reviewOutput, reviewInputData);
    }
  }

  private void setGrpWpName(final ReviewedInfo reviewOutput, final ReviewInput reviewInputData) {
    Set<Long> wpIdSet = new HashSet<>();
    reviewInputData.getRvwWpAndRespModelSet().forEach(wpResp -> wpIdSet.add(wpResp.getA2lWpId()));
    if (wpIdSet.size() > 1) {
      reviewOutput.setGrpWorkPackageName(CDRConstants.MUL_WP);
    }
    else {
      reviewInputData.getSelectedWpRespList().forEach(wpRespModel -> {
        if (wpIdSet.iterator().next().equals(wpRespModel.getA2lWpId())) {
          reviewOutput.setGrpWorkPackageName(wpRespModel.getWpName());
        }
      });
    }
  }

  /**
   * load ssd files
   *
   * @param filePaths server file path
   * @return Rest response
   * @throws IOException exception
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_FILE_DOWNLOAD)
  @CompressData
  public Response downloadSSDFiles(final Set<String> filePaths) throws IOException {

    Map<String, byte[]> fileDataMap = new HashMap<>();
    for (String filePath : filePaths) {
      File ssdFile = new File(filePath);
      byte[] fileBytes = Files.readAllBytes(ssdFile.toPath());
      fileDataMap.put(filePath, fileBytes);
    }

    return Response.ok(fileDataMap).build();
  }

  /**
   * Download the iCDM OSS Document
   *
   * @return rest response
   * @throws IcdmException error during webservice call
   */

  @GET
  @Produces({ MediaType.APPLICATION_OCTET_STREAM })
  @Path(WsCommonConstants.RWS_SIMPLIFIED_QNAIRE_DECLARATION_FILE)
  @CompressData
  public Response getSimpQnaireDeclrtnFile() throws IcdmException {

    CommonParamKey simpQnaireDeclFileNodeId = null;
    String simpQnaireDeclFileName = null;
    if (CommonUtils.isEqualIgnoreCase(getServiceData().getLanguage(), Language.ENGLISH.toString())) {
      simpQnaireDeclFileNodeId = CommonParamKey.SIMP_QNAIRE_DECLAR_NODE_EN_ID;
      simpQnaireDeclFileName = ApicConstants.SIMP_QNAIRE_DECLAR_FILE_NAME_EN;
    }
    else {
      simpQnaireDeclFileNodeId = CommonParamKey.SIMP_QNAIRE_DECLAR_NODE_ID;
      simpQnaireDeclFileName = ApicConstants.SIMP_QNAIRE_DECLAR_FILE_NAME_DE;
    }
    Map<String, byte[]> ret = new IcdmFilesLoader(getServiceData()).getFiles(
        Long.valueOf(new CommonParamLoader(getServiceData()).getValue(simpQnaireDeclFileNodeId)), "TEMPLATES");

    byte[] simpQnaireDeclarFile = null;
    if (CommonUtils.isNotEmpty(ret)) {
      simpQnaireDeclarFile = ret.get(simpQnaireDeclFileName);
    }

    return Response.ok(simpQnaireDeclarFile).build();
  }
}
