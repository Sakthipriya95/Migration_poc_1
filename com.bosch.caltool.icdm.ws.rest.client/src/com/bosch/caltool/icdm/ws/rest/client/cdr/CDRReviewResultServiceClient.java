/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.Future;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.cns.CHANGE_OPERATION;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.datamodel.core.cns.ChangeDataCreator;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibilityStatus;
import com.bosch.caltool.icdm.model.apic.pidc.PidcReviewDetails;
import com.bosch.caltool.icdm.model.apic.pidc.PidcReviewDetailsResponse;
import com.bosch.caltool.icdm.model.apic.pidc.PidcTreeRvwVariant;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.CDRWizardUIModel;
import com.bosch.caltool.icdm.model.cdr.CopyResultToVarData;
import com.bosch.caltool.icdm.model.cdr.ReviewResultDeleteValidation;
import com.bosch.caltool.icdm.model.cdr.ReviewResultEditorData;
import com.bosch.caltool.icdm.model.cdr.TreeViewSelectnRespWP;
import com.bosch.caltool.icdm.model.cdr.WPArchivalServiceModel;
import com.bosch.caltool.icdm.model.cdr.WPRespStatusMsgWrapper;
import com.bosch.caltool.icdm.model.cdr.WPRespStatusOutputModel;
import com.bosch.caltool.icdm.model.cdr.WpArchival;
import com.bosch.caltool.icdm.model.cdr.WpArchivalCommonModel;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.Activator;
import com.bosch.caltool.icdm.ws.rest.client.cns.ChangeHandler;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapperChangeData;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.ModelParser;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for CDRReviewResult
 *
 * @author bru2cob
 */
public class CDRReviewResultServiceClient extends AbstractRestServiceClient {


  private static final IMapperChangeData A2l_WP_RESPONSIBILITY_STATUS_UPDATE_MAPPER = data -> {
    Collection<ChangeData<IModel>> changeDataList = new HashSet<>();
    ChangeDataCreator<IModel> changeDataCreator = new ChangeDataCreator<>();
    WPRespStatusOutputModel wpRespStatusOutputModel = (WPRespStatusOutputModel) data;

    List<A2lWpResponsibilityStatus> listofNewlyCreatedA2lWpRespStatus =
        wpRespStatusOutputModel.getListOfNewlyCreatedA2lWpRespStatus();
    if (!listofNewlyCreatedA2lWpRespStatus.isEmpty()) {
      listofNewlyCreatedA2lWpRespStatus.stream()
          .forEach(a2lWpRespStatus -> changeDataList.add(changeDataCreator.createDataForCreate(0L, a2lWpRespStatus)));
    }

    Map<Long, A2lWpResponsibilityStatus> a2lWPRespStatusAfterUpdMap =
        wpRespStatusOutputModel.getA2lWpRespStatusAfterUpdMap();
    wpRespStatusOutputModel.getA2lWpRespStatusBeforeUpdMap().entrySet().forEach(a2lWPRespStatusEntrySet -> {
      if (a2lWPRespStatusAfterUpdMap.containsKey(a2lWPRespStatusEntrySet.getKey())) {
        changeDataList.add(changeDataCreator.createDataForUpdate(0L, a2lWPRespStatusEntrySet.getValue(),
            a2lWPRespStatusAfterUpdMap.get(a2lWPRespStatusEntrySet.getKey())));
      }
    });

    return changeDataList;
  };

  private static final IMapperChangeData WP_ARCHIVAL_LIST_UPDATE_MAPPER = data -> {
    Collection<ChangeData<IModel>> changeDataList = new HashSet<>();
    ChangeDataCreator<IModel> changeDataCreator = new ChangeDataCreator<>();
    WpArchivalCommonModel archivalModel = (WpArchivalCommonModel) data;

    List<WpArchival> listofNewlyCreatedWpArchival = archivalModel.getWpArchival();
    if (!listofNewlyCreatedWpArchival.isEmpty()) {
      listofNewlyCreatedWpArchival.stream()
          .forEach(archive -> changeDataList.add(changeDataCreator.createDataForCreate(0L, archive)));
    }
    return changeDataList;
  };

  /**
   * Constructor
   */
  public CDRReviewResultServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_CDRREVIEWRESULT);
  }

  /**
   * Get CDRReviewResult using its id
   *
   * @param objId object's id
   * @return CDRReviewResult object
   * @throws ApicWebServiceException exception while invoking service
   */
  public CDRReviewResult getById(final Long objId) throws ApicWebServiceException {
    return getById(new HashSet<>(Arrays.asList(objId))).get(objId);
  }

  /**
   * Get Multiple CDRReviewResult using ids
   *
   * @param objIds set of review result Ids
   * @return CDRReviewResult Map of review result id, review result object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Map<Long, CDRReviewResult> getById(final Set<Long> objIds) throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase();
    for (Long revResId : objIds) {
      wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_OBJ_ID, revResId);
    }
    GenericType<Map<Long, CDRReviewResult>> type = new GenericType<Map<Long, CDRReviewResult>>() {};

    Map<Long, CDRReviewResult> ret = get(wsTarget, type);

    LOGGER.debug("CDR Review Results fetched. No. of records : {}", ret.size());

    return ret;
  }

  /**
   * Get CDR Result using its ids. Note : this is a POST request
   *
   * @param objIdSet set of object ids
   * @return Result object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Map<Long, CDRReviewResult> getMultiple(final Set<Long> objIdSet) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_MULTIPLE);

    GenericType<Map<Long, CDRReviewResult>> type = new GenericType<Map<Long, CDRReviewResult>>() {};
    return post(wsTarget, objIdSet, type);
  }


  /**
   * @param resultId
   * @return
   * @throws ApicWebServiceException
   */
  public ReviewResultDeleteValidation reviewResultDeleteValidation(final Long resultId) throws ApicWebServiceException {
    return get(getWsBase().path(WsCommonConstants.RWS_RVW_CAN_DELETE_VAL).queryParam(WsCommonConstants.RWS_QP_OBJ_ID,
        resultId), ReviewResultDeleteValidation.class);
  }

  /**
   * Update a CDRReviewResult record
   *
   * @param obj object to update
   * @return updated CDRReviewResult object
   * @throws ApicWebServiceException exception while invoking service
   */
  public CDRReviewResult update(final CDRReviewResult obj) throws ApicWebServiceException {
    return update(getWsBase(), obj);
  }


  /**
   * Delete a review result
   *
   * @param obj CDRReviewResult
   * @param asyncFlag for asyn delete
   */
  public void deleteReviewResult(final CDRReviewResult obj, final boolean asyncFlag) {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_RVW_ASYN_DELETE);
    Future<Response> future = createFutureDelete(wsTarget, obj);

    if (asyncFlag) {
      deleteAsync(future);
    }
    else {
      deleteSync(future);
    }
  }

  /**
   * Service client to Update Workpackage Responsibility Status based on Questionnaire Response and Rvw Parameters
   *
   * @param selWpResp as input
   */
  public void updateSelWorkpackageStatus(final TreeViewSelectnRespWP selWpResp) {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_UPDATE_SEL_WP_STATUS);

    Future<Response> future = createFuturePut(wsTarget, selWpResp);
    new Thread(() -> {
      try {
        WPRespStatusOutputModel wpRespStatusOutputModel = getResponseAsync(future, WPRespStatusOutputModel.class);

        StringBuilder msg = constructInfoMsgForReviewLock(wpRespStatusOutputModel);

        Collection<ChangeData<IModel>> newDataModelSet =
            ModelParser.getChangeData(wpRespStatusOutputModel, A2l_WP_RESPONSIBILITY_STATUS_UPDATE_MAPPER);
        (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<>(newDataModelSet));

        displayMessage("A2l Workpackage Responsibility updated!");

        if (!msg.toString().isEmpty() && !wpRespStatusOutputModel.getCompletedWPRespMap().isEmpty()) {
          CDMLogger.getInstance().infoDialog(msg.toString() + "\n" + CDRConstants.WP_ARCHIVAL_INPROGRESS_MSG,
              Activator.PLUGIN_ID);
        }
        else if (!msg.toString().isEmpty()) {
          CDMLogger.getInstance().infoDialog(msg.toString(), Activator.PLUGIN_ID);
        }

        if (!wpRespStatusOutputModel.getCompletedWPRespMap().isEmpty()) {

          WPArchivalServiceModel wpArchivalServiceModel = new WPArchivalServiceModel();
          wpArchivalServiceModel.setTreeViewSelectnRespWP(selWpResp);
          wpArchivalServiceModel.setCompletedWPRespMap(wpRespStatusOutputModel.getCompletedWPRespModelSet());

          WebTarget archWsTarget = getWsBase().path(WsCommonConstants.RWS_ARCHIVE_WORKPACKAGE)
              .queryParam(WsCommonConstants.RWS_QP_OBJ_ID, wpArchivalServiceModel);
          Future<Response> archFuture = createFuturePut(archWsTarget, wpArchivalServiceModel);

          WpArchivalCommonModel wpArchivalModel = getResponseAsync(archFuture, WpArchivalCommonModel.class);

          Collection<ChangeData<IModel>> newArchivalDataModelSet =
              ModelParser.getChangeData(wpArchivalModel, WP_ARCHIVAL_LIST_UPDATE_MAPPER);
          (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<>(newArchivalDataModelSet));

          StringBuilder archivalMsg = constructInfoMsgForArchival(wpArchivalModel);
          if (!archivalMsg.toString().isEmpty()) {
            CDMLogger.getInstance().infoDialog(archivalMsg.toString(), Activator.PLUGIN_ID);
          }
        }
      }
      catch (ApicWebServiceException excep) {
        CDMLogger.getInstance().errorDialog(excep.getMessage(), excep, Activator.PLUGIN_ID);
      }
    }).start();

  }

  /**
   * Service clinet to Update Workpackage Responsibility Status based on Questionnaire Response and Rvw Parameters
   *
   * @param cdrReviewResult as input
   */
  public void updateWorkpackageStatus(final CDRReviewResult cdrReviewResult) {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_WORKPACKAGE_STATUS)
        .queryParam(WsCommonConstants.RWS_QP_OBJ_ID, cdrReviewResult.getId());
    Future<Response> future = createFutureUpdate(wsTarget, cdrReviewResult);

    new Thread(() -> {
      try {
        WPRespStatusOutputModel responseAsync = getResponseAsync(future, WPRespStatusOutputModel.class);
        StringBuilder msg = constructInfoMsgForReviewLock(responseAsync);

        Collection<ChangeData<IModel>> newDataModelSet =
            ModelParser.getChangeData(responseAsync, A2l_WP_RESPONSIBILITY_STATUS_UPDATE_MAPPER);
        (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<>(newDataModelSet));
        displayMessage(CHANGE_OPERATION.UPDATE, cdrReviewResult);

        if (!msg.toString().isEmpty() && !responseAsync.getCompletedWPRespMap().isEmpty()) {
          CDMLogger.getInstance().infoDialog(msg.toString() + "\n" + CDRConstants.WP_ARCHIVAL_INPROGRESS_MSG,
              Activator.PLUGIN_ID);
        }
        else if (!msg.toString().isEmpty()) {
          CDMLogger.getInstance().infoDialog(msg.toString(), Activator.PLUGIN_ID);
        }

        if (!responseAsync.getCompletedWPRespMap().isEmpty()) {

          WPArchivalServiceModel wpArchivalServiceModel = new WPArchivalServiceModel();
          wpArchivalServiceModel.setCdrReviewResult(cdrReviewResult);
          wpArchivalServiceModel.setCompletedWPRespMap(responseAsync.getCompletedWPRespModelSet());

          WebTarget archWsTarget = getWsBase().path(WsCommonConstants.RWS_ARCHIVE_WORKPACKAGE)
              .queryParam(WsCommonConstants.RWS_QP_OBJ_ID, wpArchivalServiceModel);
          Future<Response> archFuture = createFuturePut(archWsTarget, wpArchivalServiceModel);

          WpArchivalCommonModel wpArchivalModel = getResponseAsync(archFuture, WpArchivalCommonModel.class);

          Collection<ChangeData<IModel>> newArchivalDataModelSet =
              ModelParser.getChangeData(wpArchivalModel, WP_ARCHIVAL_LIST_UPDATE_MAPPER);
          (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<>(newArchivalDataModelSet));

          StringBuilder archivalMsg = constructInfoMsgForArchival(wpArchivalModel);

          if (!archivalMsg.toString().isEmpty()) {
            CDMLogger.getInstance().infoDialog(archivalMsg.toString(), Activator.PLUGIN_ID);
          }
        }
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
      }

    }).start();
  }

  /**
   * @param responseAsync
   * @return
   */
  private StringBuilder constructInfoMsgForReviewLock(final WPRespStatusOutputModel responseAsync) {

    Map<String, StringBuilder> msgMap = new HashMap<>();
    StringBuilder msg = new StringBuilder();
    if (!responseAsync.getCompletedWPRespMap().isEmpty()) {
      fillInfoMsgMap(responseAsync.getCompletedWPRespMap(), msgMap);
    }
    if (!responseAsync.getInCompleteWPRespMap().isEmpty()) {
      fillInfoMsgMap(responseAsync.getInCompleteWPRespMap(), msgMap);
    }

    for (Entry<String, StringBuilder> msgBuilderMap : msgMap.entrySet()) {
      if (msg.length() > 0) {
        msg.append("\n");
      }
      msg.append(msgBuilderMap.getKey());
      msg.append("\n");
      msg.append(msgBuilderMap.getValue());
      msg.append("\n");
    }
    return msg;
  }

  /**
   * @param responseAsync
   * @return
   */
  private StringBuilder constructInfoMsgForArchival(final WpArchivalCommonModel responseAsync) {

    Map<String, StringBuilder> msgMap = new HashMap<>();
    StringBuilder msg = new StringBuilder();

    if (!responseAsync.getWpArchivalCompletedWPRespMap().isEmpty()) {
      fillInfoMsgMap(responseAsync.getWpArchivalCompletedWPRespMap(), msgMap);
    }
    if (!responseAsync.getWpArchivalFailedWPRespMap().isEmpty()) {
      fillInfoMsgMap(responseAsync.getWpArchivalFailedWPRespMap(), msgMap);
    }
    for (Entry<String, StringBuilder> msgBuilderMap : msgMap.entrySet()) {
      if (msg.length() > 0) {
        msg.append("\n");
      }
      msg.append(msgBuilderMap.getKey());
      msg.append("\n");
      msg.append(msgBuilderMap.getValue());
      msg.append("\n");
    }
    return msg;
  }

  /**
   * @param wpStatusMap
   * @param respMsg
   * @param wpMsg
   * @param msgMap
   */
  private void fillInfoMsgMap(final Map<Long, Map<Long, WPRespStatusMsgWrapper>> wpStatusMap,
      final Map<String, StringBuilder> msgMap) {
    String respMsg = "Responsibility: ";
    String wpMsg = "Workpackage: ";
    for (Entry<Long, Map<Long, WPRespStatusMsgWrapper>> respWpStatusMsgEntrySet : wpStatusMap.entrySet()) {
      for (Entry<Long, WPRespStatusMsgWrapper> wpStatusMsgEntrySet : respWpStatusMsgEntrySet.getValue().entrySet()) {
        WPRespStatusMsgWrapper respStatusMsgWrapper = wpStatusMsgEntrySet.getValue();
        if (msgMap.containsKey(respStatusMsgWrapper.getOutputStatusMsg())) {
          StringBuilder stringBuilder = msgMap.get(respStatusMsgWrapper.getOutputStatusMsg());
          stringBuilder.append("\n");
          stringBuilder.append(wpMsg).append(respStatusMsgWrapper.getWorkPackageName()).append(", ").append(respMsg)
              .append(respStatusMsgWrapper.getRespName());
        }
        else {
          StringBuilder stringBuilder = new StringBuilder();
          stringBuilder.append(wpMsg).append(respStatusMsgWrapper.getWorkPackageName()).append(", ").append(respMsg)
              .append(respStatusMsgWrapper.getRespName());
          msgMap.put(respStatusMsgWrapper.getOutputStatusMsg(), stringBuilder);
        }
      }
    }
  }


  /**
   * For Deleting review result in asyncronous mode
   *
   * @param future
   */
  private void deleteAsync(final Future<Response> future) {
    new Thread(() -> {
      try {
        String response = getResponseAsync(future, String.class);
        CDMLogger.getInstance().debug("Review Result deletion status : {}", response);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
      }

    }).start();
  }

  /**
   * For Deleting review result in Syncronous mode
   *
   * @param future
   */
  private void deleteSync(final Future<Response> future) {
    try {
      String response = getResponseAsync(future, String.class);
      CDMLogger.getInstance().debug("Review Result deletion status : {}", response, Activator.PLUGIN_ID);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }


  /**
   * @param pidcA2lId pidcA2lId
   * @return sorted set of CDRReviewResult associated with the pidca2l
   * @throws ApicWebServiceException error during webservice call
   */
  public SortedSet<CDRReviewResult> getCDRResultsByPidcA2l(final Long pidcA2lId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_CDRRESULTS_BY_PIDCA2L)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_A2L_ID, pidcA2lId);
    GenericType<SortedSet<CDRReviewResult>> type = new GenericType<SortedSet<CDRReviewResult>>() {};
    return get(wsTarget, type);
  }


  /**
   * @param objectId
   * @param dirPath
   * @return sorted set of CDRReviewResult associated with the pidca2l
   * @throws ApicWebServiceException error during webservice call
   */
  public CDRWizardUIModel getReviewResultForDeltaReview(final Long objectId, final String dirPath)
      throws ApicWebServiceException {

    CDRWizardUIModel cdrWizardUIModel =
        get(getWsBase().path(WsCommonConstants.RWS_RVW_RES_DELTA).queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objectId),
            CDRWizardUIModel.class);
    Map<String, byte[]> selectedInputFiles = cdrWizardUIModel.getSelectedInputFiles();
    String funLabFilePath = cdrWizardUIModel.getFunLabFilePath();
    byte[] funLabFiles = cdrWizardUIModel.getFunLabFiles();
    if ((null != funLabFilePath) && (null != funLabFiles)) {
      File file = new File(dirPath + File.separator + funLabFilePath.trim());
      InputStream inputStream = new ByteArrayInputStream(funLabFiles);
      writeData(funLabFilePath.trim(), file, inputStream);
      cdrWizardUIModel.setFunLabFilePath(file.getAbsolutePath());
    }
    Set<String> selFilesPath = new HashSet<>();
    Set<String> monicaFilesPath = new HashSet<>();
    String selMoniCaSheetName = null;
    if (selectedInputFiles != null) {
      for (Entry<String, byte[]> selectedFile : selectedInputFiles.entrySet()) {
        File file = new File(dirPath + File.separator + selectedFile.getKey().trim());
        InputStream inputStream = new ByteArrayInputStream(selectedFile.getValue());
        writeData(selectedFile.getKey().trim(), file, inputStream);
        selFilesPath.add(file.getAbsolutePath());
      }
    }
    Map<String, byte[]> selectedMonicaFiles = cdrWizardUIModel.getSelectedMonicaFiles();
    if (selectedMonicaFiles != null) {
      for (Entry<String, byte[]> monicaFile : selectedMonicaFiles.entrySet()) {
        String fileName = monicaFile.getKey();

        if (fileName.contains(CDRConstants.FILENAME_SEPERATOR)) {
          selMoniCaSheetName = fileName.substring(0, fileName.lastIndexOf(CDRConstants.FILENAME_SEPERATOR));
          fileName = fileName.substring(fileName.lastIndexOf(CDRConstants.FILENAME_SEPERATOR) + 1, fileName.length());
        }
        File file = new File(dirPath + File.separator + fileName.trim());
        InputStream inputStream = new ByteArrayInputStream(monicaFile.getValue());
        writeData(fileName.trim(), file, inputStream);
        monicaFilesPath.add(file.getAbsolutePath());
      }
    }
    cdrWizardUIModel.setSelFilesPath(selFilesPath);
    cdrWizardUIModel.setSelMonicaFilesPath(monicaFilesPath);
    cdrWizardUIModel.setSelMoniCaSheetName(selMoniCaSheetName);
    return cdrWizardUIModel;
  }

  /**
   * Get the result details required for copy result to variant action
   *
   * @param cdrResultId result id
   * @return details required for copy result to variant action
   * @throws ApicWebServiceException error during webservice call
   */
  public CopyResultToVarData getResultDetailsForAttachVar(final Long cdrResultId) throws ApicWebServiceException {
    return get(getWsBase().path(WsCommonConstants.RWS_COPY_VAR_TO_RESULT).queryParam(WsCommonConstants.RWS_QP_OBJ_ID,
        cdrResultId), CopyResultToVarData.class);
  }

  /**
   * @param cdrResultId result id
   * @return true is result can be modified
   * @throws ApicWebServiceException error during webservice call
   */
  public boolean canModify(final Long cdrResultId) throws ApicWebServiceException {
    return get(getWsBase().path(WsCommonConstants.RWS_CAN_MODIFY_RESULT).queryParam(WsCommonConstants.RWS_QP_OBJ_ID,
        cdrResultId), boolean.class);
  }


  /**
   * @param pidcVerId pidc version id
   * @return flsg to indicate presence of rev resuls in a pidc version
   * @throws ApicWebServiceException Web service exception
   */
  public boolean hasPidcRevResults(final Long pidcVerId) throws ApicWebServiceException {
    LOGGER.debug("Started Fetching cdr results availability for a Pidc");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_HAS_PIDC_REV_RESULTS)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ID, pidcVerId);
    boolean hasRvwResults = get(wsTarget, boolean.class);
    LOGGER.debug("Fetching cdr results availability for a Pidc completed");
    return hasRvwResults;
  }

  /**
   * @param cdrResultId as result id
   * @return boolean
   * @throws ApicWebServiceException Web service exception
   */
  public boolean isUsedInCDFXDelivery(final Long cdrResultId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_CDFX_DELIVERY_USAGE_CHECK)
        .queryParam(WsCommonConstants.RWS_QP_OBJ_ID, cdrResultId);
    return get(wsTarget, boolean.class);
  }

  /**
   * @param pidcVerId pidc version id
   * @return Rev results map
   * @throws ApicWebServiceException Web service exception
   */
  public Map<String, Map<Long, CDRReviewResult>> getPidcRevResults(final Long pidcVerId)
      throws ApicWebServiceException {
    LOGGER.debug("Started Fetching cdr results for a Pidc");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_PIDC_REV_RESULTS)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ID, pidcVerId);
    GenericType<Map<String, Map<Long, CDRReviewResult>>> type =
        new GenericType<Map<String, Map<Long, CDRReviewResult>>>() {};
    Map<String, Map<Long, CDRReviewResult>> pidcCdrMap = get(wsTarget, type);
    LOGGER.debug("Fetching cdr results for a Pidc completed");
    return pidcCdrMap;
  }


  /**
   * @param rvwResultId
   * @return PidcReviewDetails
   * @throws ApicWebServiceException
   */
  public PidcReviewDetails getNewReviewResultInfo(final Long rvwResultId, final Long rvwVariantId)
      throws ApicWebServiceException {
    LOGGER.debug("Started Fetching review result information for new review result");
    WebTarget wsTarget;
    if (null == rvwVariantId) {
      wsTarget = getWsBase().path(WsCommonConstants.RWS_NEW_REVIEW_DETAILS)
          .queryParam(WsCommonConstants.RWS_QP_RESULT_ID, rvwResultId);
    }
    else {
      wsTarget = getWsBase().path(WsCommonConstants.RWS_NEW_REVIEW_DETAILS)
          .queryParam(WsCommonConstants.RWS_QP_VARIANT_ID, rvwVariantId);
    }
    GenericType<PidcReviewDetailsResponse> type = new GenericType<PidcReviewDetailsResponse>() {};
    PidcReviewDetailsResponse pidcReviewDetailsResponse = get(wsTarget, type);
    LOGGER.debug("Fetching review result information for new review result completed");
    return new PidcReviewDetails(pidcReviewDetailsResponse);
  }

  /**
   * Method to fetch the PidcReviewDetails model for review result in PIDC Tree View
   *
   * @param pidcVerId
   * @return
   * @throws ApicWebServiceException
   */
  public PidcReviewDetails getReviewResultInfo(final Long pidcVerId) throws ApicWebServiceException {
    LOGGER.debug("Started Fetching review result information for pidc version");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_PIDC_RVW_RESULT_INFO)
        .queryParam(WsCommonConstants.RWS_QP_OBJ_ID, pidcVerId);
    GenericType<PidcReviewDetailsResponse> type = new GenericType<PidcReviewDetailsResponse>() {};
    PidcReviewDetailsResponse pidcReviewDetailsResponse = get(wsTarget, type);
    PidcReviewDetails pidcReviewDetails = new PidcReviewDetails(pidcReviewDetailsResponse);
    LOGGER.debug("Fetching review result information for pidc version completed");
    return pidcReviewDetails;
  }


  /**
   * @param rvwResId review result ID
   * @return FC2WP name
   * @throws ApicWebServiceException Web service exception
   */
  public String resolveFc2WpName(final Long rvwResId) throws ApicWebServiceException {
    LOGGER.debug("Started Fetching fc2wp name for review result");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_RESOLVE_FC2WP_NAME)
        .queryParam(WsCommonConstants.RWS_QP_OBJ_ID, rvwResId);
    String fc2wpName = get(wsTarget, String.class);
    LOGGER.debug("Fetching fc2wp name for review result completed");
    return fc2wpName;
  }

  /**
   * @param pidcVerId pidc version id
   * @return review results
   * @throws ApicWebServiceException Web service exception
   */
  public Map<String, PidcTreeRvwVariant> getPidcVarRevResults(final Long pidcVerId) throws ApicWebServiceException {
    LOGGER.debug("Started Fetching variant Review Result for a Pidc");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_PIDC_VAR_REV_RES)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ID, pidcVerId);
    GenericType<Map<String, PidcTreeRvwVariant>> type = new GenericType<Map<String, PidcTreeRvwVariant>>() {};
    Map<String, PidcTreeRvwVariant> revResResponse = get(wsTarget, type);
    LOGGER.debug("Fetching variant Review Result for a Pidc completed");
    return revResResponse;
  }


  /**
   * Get CDR Result Editor Data
   *
   * @param resultId Review Result id
   * @param rvwVarId Review Variant id
   * @return ReviewResultEditorData
   * @throws ApicWebServiceException exception while invoking service
   */
  public ReviewResultEditorData getRvwResultEditorData(final Long resultId, final Long rvwVarId)
      throws ApicWebServiceException {
    LOGGER.debug("Started fetching review result editor data");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_RVW_EDITOR_DATA)
        .queryParam(WsCommonConstants.RWS_QP_OBJ_ID, resultId)
        .queryParam(WsCommonConstants.RWS_QP_RVW_VAR_ID, rvwVarId);
    ReviewResultEditorData editorData = get(wsTarget, ReviewResultEditorData.class);
    LOGGER.debug("Fetching review result editor data completed");
    return editorData;
  }


  /**
   * Builds path to get the reviewResultDeleteValidation Map for the given set of review result id
   *
   * @param reviewResultIdSet - review result id set
   * @return review result delete validation map
   * @throws ApicWebServiceException exception on invoking method
   */
  public Map<Long, ReviewResultDeleteValidation> getMultipleReviewResultDeleteValidation(
      final Set<Long> reviewResultIdSet)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_RVW_CAN_MUL_DELETE_VAL);
    for (Long revResId : reviewResultIdSet) {
      wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_OBJ_ID, revResId);
    }
    GenericType<Map<Long, ReviewResultDeleteValidation>> type =
        new GenericType<Map<Long, ReviewResultDeleteValidation>>() {};

    return get(wsTarget, type);
  }


  /**
   * Build the rest service path with the reviewIds Set and calls corresponding delete service
   *
   * @param reviewResultIdSet - review result id set
   * @throws ApicWebServiceException exception on invoking method
   */
  public void deleteMultipleRvwResult(final Set<Long> reviewResultIdSet) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_RVW_MUL_DEL);

    for (Long reviewId : reviewResultIdSet) {
      wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_OBJ_ID, reviewId);
    }
    delete(wsTarget);
  }
}
