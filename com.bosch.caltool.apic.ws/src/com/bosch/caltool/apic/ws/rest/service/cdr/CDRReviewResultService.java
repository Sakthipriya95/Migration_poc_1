/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.CompletionCallback;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.ThreadContext;
import org.glassfish.jersey.server.ManagedAsync;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2lWorkPackageLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWpDefnVersionLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWpRespStatusUpdationCommand;
import com.bosch.caltool.icdm.bo.a2l.A2lWpResponsibilityLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWpResponsibilityStatusLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeModel;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader.LOAD_LEVEL;
import com.bosch.caltool.icdm.bo.cdr.CDRReportLoader;
import com.bosch.caltool.icdm.bo.cdr.CDRReviewResultCommand;
import com.bosch.caltool.icdm.bo.cdr.CDRReviewResultLoader;
import com.bosch.caltool.icdm.bo.cdr.CDRRvwResultEditorDataLoader;
import com.bosch.caltool.icdm.bo.cdr.RvwAttrValueLoader;
import com.bosch.caltool.icdm.bo.cdr.RvwParticipantLoader;
import com.bosch.caltool.icdm.bo.cdr.RvwVariantLoader;
import com.bosch.caltool.icdm.bo.cdr.WorkPackageStatusHandler;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.bo.user.NodeAccessLoader;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TPidcA2l;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.model.a2l.A2LDetailsStructureModel;
import com.bosch.caltool.icdm.model.a2l.A2lVarGrpVariantMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWPRespModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.A2lWpRespStatusUpdationModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibilityStatus;
import com.bosch.caltool.icdm.model.a2l.WpRespLabelResponse;
import com.bosch.caltool.icdm.model.a2l.WpRespModel;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcReviewDetailsResponse;
import com.bosch.caltool.icdm.model.apic.pidc.PidcTreeRvwVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.CDRWizardUIModel;
import com.bosch.caltool.icdm.model.cdr.CopyResultToVarData;
import com.bosch.caltool.icdm.model.cdr.ReviewResultDeleteValidation;
import com.bosch.caltool.icdm.model.cdr.ReviewResultEditorData;
import com.bosch.caltool.icdm.model.cdr.RvwAttrValue;
import com.bosch.caltool.icdm.model.cdr.RvwParticipant;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;
import com.bosch.caltool.icdm.model.cdr.TreeViewSelectnRespWP;
import com.bosch.caltool.icdm.model.cdr.WPArchivalServiceModel;
import com.bosch.caltool.icdm.model.cdr.WPFinishRvwDet;
import com.bosch.caltool.icdm.model.cdr.WPRespStatusMsgWrapper;
import com.bosch.caltool.icdm.model.cdr.WPRespStatusOutputInternalModel;
import com.bosch.caltool.icdm.model.cdr.WPRespStatusOutputModel;
import com.bosch.caltool.icdm.model.cdr.WpArchivalCommonModel;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.general.LoggingContext;
import com.bosch.caltool.icdm.model.user.User;


/**
 * Service class for CDRReviewResult
 *
 * @author bru2cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_CDRREVIEWRESULT)
public class CDRReviewResultService extends AbstractRestService {

  /**
   * the ATTR_LEVEL used for the SDOM Project Name attribute
   */
  public static final int SDOM_PROJECT_NAME_ATTR = -30;

  /**
   * Get CDRReviewResult using its id
   *
   * @param objIdSet Set of review result ids
   * @return Rest response, with map of CDRReviewResult object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getById(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Set<Long> objIdSet) throws IcdmException {
    CDRReviewResultLoader loader = new CDRReviewResultLoader(getServiceData());
    Map<Long, CDRReviewResult> ret = loader.getDataObjectByID(objIdSet);
    return Response.ok(ret).build();
  }


  /**
   * Get the Delete Review Result Validation Map for passed review resultId set
   *
   * @param rvwResultIdSet set of review result id set
   * @return delete validation map
   * @throws IcdmException exception thrown while invocation
   */
  @GET
  @Path(WsCommonConstants.RWS_RVW_CAN_MUL_DELETE_VAL)
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getMultipleRvwResultDeleteValidation(
      @QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Set<Long> rvwResultIdSet)
      throws IcdmException {
    CDRReviewResultLoader cdrReviewResultLoader = new CDRReviewResultLoader(getServiceData());
    Map<Long, ReviewResultDeleteValidation> reviewResultValidationMap =
        cdrReviewResultLoader.getMultipleReviewResultDeleteValidation(rvwResultIdSet);
    return Response.ok(reviewResultValidationMap).build();
  }


  /**
   * Get CDR Result participants using its id. Note : this is a POST request
   *
   * @param objIdSet set of object IDs
   * @return Rest response, with CDRReviewResult object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  @Path(WsCommonConstants.RWS_MULTIPLE)
  public Response getMultiple(final Set<Long> objIdSet) throws IcdmException {
    CDRReviewResultLoader loader = new CDRReviewResultLoader(getServiceData());
    Map<Long, CDRReviewResult> ret = loader.getDataObjectByID(objIdSet);
    return Response.ok(ret).build();
  }

  /**
   * @param objId
   * @return
   * @throws IcdmException
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_RVW_CAN_DELETE_VAL)
  @CompressData
  public Response reviewResultDeleteValidation(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId)
      throws IcdmException {
    CDRReviewResultLoader loader = new CDRReviewResultLoader(getServiceData());
    ReviewResultDeleteValidation deleteValidation = loader.reviewResultDeleteValidation(objId);
    return Response.ok(deleteValidation).build();
  }


  /**
   * Update a CDRReviewResult record
   *
   * @param obj object to update
   * @return Rest response, with updated CDRReviewResult object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final CDRReviewResult obj) throws IcdmException {
    CDRReviewResultCommand cmd = new CDRReviewResultCommand(getServiceData(), obj, null, true, false);
    executeCommand(cmd);
    CDRReviewResult ret = cmd.getNewData();
    getLogger().info("Updated CDRReviewResult Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Update status of A2l Wp Responsibility record selected from tree view
   *
   * @param asyncResponse Response type as input
   * @param selTreeViewWpResp selected a2l wp responsibility from tree view
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Path(WsCommonConstants.RWS_UPDATE_SEL_WP_STATUS)
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  @ManagedAsync
  public void updateSelWPStatus(@Suspended final AsyncResponse asyncResponse,
      final TreeViewSelectnRespWP selTreeViewWpResp)
      throws IcdmException {

    asyncResponse.register(new CompletionCallback() {

      @Override
      public void onComplete(final Throwable throwable) {
        if (throwable != null) {
          getLogger().error(throwable.getMessage(), throwable);
        }
      }
    });

    try (ServiceData sdata = new ServiceData()) {

      super.getServiceData().copyTo(sdata, false);

      WorkPackageStatusHandler workPackageStatusHandler = new WorkPackageStatusHandler(getServiceData());
      WPRespStatusOutputModel wpRespStatusOutputModel = new WPRespStatusOutputModel();

      // Map to collect A2lWPResponsibilityStatus Before Update for CNS Refresh
      Map<Long, A2lWpResponsibilityStatus> a2lWPRespStatusBeforeUpdMap = new HashMap<>();
      List<A2lWpResponsibilityStatus> listOfA2lWPRespStatusToBeCreated = new ArrayList<>();
      A2lWpResponsibilityStatusLoader a2lWPRespStatusLoader = new A2lWpResponsibilityStatusLoader(getServiceData());

      Long variantId = selTreeViewWpResp.getVariantID();
      Long varId = CommonUtils.isNull(variantId) || CommonUtils.isEqual(variantId, ApicConstants.NO_VARIANT_ID) ? null
          : variantId;

      CDRReviewResultLoader cdrReviewResultLoader = new CDRReviewResultLoader(getServiceData());
      WPRespStatusOutputInternalModel wpRespStatusOutputInternalModel =
          workPackageStatusHandler.computeTreeViewSelWPRespStatus(selTreeViewWpResp);
      A2lWpRespStatusUpdationModel a2lWpRespStatusInputUpdModel =
          cdrReviewResultLoader.getInputUpdationModel(a2lWPRespStatusBeforeUpdMap, listOfA2lWPRespStatusToBeCreated,
              a2lWPRespStatusLoader, varId, wpRespStatusOutputInternalModel.getWpRespStatus());

      // Update the 'WP finished Status' in T_A2L_WP_Responsibility_Status
      A2lWpRespStatusUpdationCommand a2lWPRespUpdCmd =
          new A2lWpRespStatusUpdationCommand(getServiceData(), a2lWpRespStatusInputUpdModel);
      executeCommand(a2lWPRespUpdCmd);

      // Update the wpRespStatusOutputModel with output value of executed command for handling CNS refresh
      cdrReviewResultLoader.setUpdModelOutputValue(wpRespStatusOutputModel, a2lWPRespStatusLoader, a2lWPRespUpdCmd);

      wpRespStatusOutputModel.setCompletedWPRespMap(
          convertToRespWpStatusMsgWrapper(wpRespStatusOutputInternalModel.getCompletedWPRespMap()));
      wpRespStatusOutputModel.setInCompleteWPRespMap(
          convertToRespWpStatusMsgWrapper(wpRespStatusOutputInternalModel.getInCompleteWPRespMap()));
      wpRespStatusOutputModel
          .setCompletedWPRespModelSet(wpRespStatusOutputInternalModel.getCompletedWPRespMap().keySet());
      wpRespStatusOutputModel
          .setInCompleteWPRespModelSet(wpRespStatusOutputInternalModel.getInCompleteWPRespMap().keySet());

      asyncResponse.resume(wpRespStatusOutputModel);
    }
  }


  /**
   * @param asyncResponse Response type
   * @param reviewResultId review Result Id
   * @throws IcdmException Exception
   */
  @DELETE
  @Path(WsCommonConstants.RWS_RVW_ASYN_DELETE)
  @Produces(MediaType.APPLICATION_JSON)
  @ManagedAsync
  public void reviewResultDelete(@Suspended final AsyncResponse asyncResponse,
      @QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long reviewResultId)
      throws IcdmException {

    asyncResponse.register(new CompletionCallback() {

      @Override
      public void onComplete(final Throwable throwable) {
        if (throwable != null) {
          getLogger().error(throwable.getMessage(), throwable);
        }
      }
    });

    try (ServiceData sdata = new ServiceData()) {
      super.getServiceData().copyTo(sdata, false);
      CDRReviewResultLoader loader = new CDRReviewResultLoader(sdata);
      if (loader.hasChildReview(reviewResultId)) {
        throw new IcdmException("The Review cannot be deleted as child review(s) exist");
      }
      CDRReviewResult obj = loader.getDataObjectByID(reviewResultId);
      CDRReviewResultCommand cmd = new CDRReviewResultCommand(sdata, obj, false, true);
      executeCommand(cmd);
      asyncResponse.resume("Review Result with Id " + reviewResultId + "deleted");
    }
  }


  /**
   * The methods inputs review result Id set,for each review Id -fetches child reviews and set their parent ref as null
   * and update these changes and delete the review Id
   *
   * @param reviewResultIdSet - Review Result Ids
   * @return RESPONSE ok
   * @throws IcdmException - Exception
   */
  @DELETE
  @Path(WsCommonConstants.RWS_RVW_MUL_DEL)
  @Produces(MediaType.APPLICATION_JSON)

  public Response deleteMultipleReviewResult(
      @QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Set<Long> reviewResultIdSet)
      throws IcdmException {

    CDRReviewResult childCdrReviewResult;
    Set<TRvwResult> tRvwResults;
    CDRReviewResultCommand childCdrReviewResultUpdateCommand;
    List<AbstractSimpleCommand> updateCommandList = new LinkedList<>();
    CDRReviewResultLoader cdrRvwResultLoader = new CDRReviewResultLoader(getServiceData());

    for (Long reviewResultId : reviewResultIdSet) {
      tRvwResults = cdrRvwResultLoader.getChildReviewResults(reviewResultId);
      if (CommonUtils.isNotEmpty(tRvwResults)) {
        for (TRvwResult trvwResult : tRvwResults) {
          childCdrReviewResult = cdrRvwResultLoader.getDataObjectByID(trvwResult.getResultId());
          childCdrReviewResult.setOrgResultId(null);
          childCdrReviewResultUpdateCommand =
              new CDRReviewResultCommand(getServiceData(), childCdrReviewResult, true, false);
          updateCommandList.add(childCdrReviewResultUpdateCommand);
        }
        executeCommand(updateCommandList);
        updateCommandList.clear();

      }
      CDRReviewResult cdrReviewResult = cdrRvwResultLoader.getDataObjectByID(reviewResultId);
      CDRReviewResultCommand cdrReviewResultDeleteCommand =
          new CDRReviewResultCommand(getServiceData(), cdrReviewResult, false, true);
      executeCommand(cdrReviewResultDeleteCommand);

    }
    return Response.ok().build();

  }


  /**
   * Service to update Workpackage Status
   *
   * @param asyncResponse as input
   * @param rvwResultId as input
   * @throws IcdmException as exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_WORKPACKAGE_STATUS)
  @CompressData
  @ManagedAsync
  public void updateWorkpackageStatus(@Suspended final AsyncResponse asyncResponse,
      @QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long rvwResultId)
      throws IcdmException {

    asyncResponse.register(new CompletionCallback() {

      @Override
      public void onComplete(final Throwable throwable) {
        if (throwable != null) {
          getLogger().error(throwable.getMessage(), throwable);
        }
      }
    });

    try (ServiceData sdata = new ServiceData()) {
      super.getServiceData().copyTo(sdata, false);
      WorkPackageStatusHandler workPackageStatusHandler = new WorkPackageStatusHandler(getServiceData());
      CDRReviewResultLoader cdrReviewResultLoader = new CDRReviewResultLoader(getServiceData());
      TRvwResult tRvwResult = cdrReviewResultLoader.getEntityObject(rvwResultId);

      WPRespStatusOutputInternalModel wpRespStatusOutputInternalModel =
          workPackageStatusHandler.calculateWorkpackageRespStatus(tRvwResult);

      PidcVariant variant = cdrReviewResultLoader.getVariant(tRvwResult);

      WPRespStatusOutputModel wpRespStatusOutputModel = new WPRespStatusOutputModel();

      A2lWpResponsibilityStatusLoader a2lWPRespStatusLoader = new A2lWpResponsibilityStatusLoader(getServiceData());
      List<A2lWpResponsibilityStatus> listOfA2lWPRespStatusToBeCreated = new ArrayList<>();
      Map<Long, A2lWpResponsibilityStatus> a2lWPRespStatusBeforeUpdMap = new HashMap<>();
      Long varId = CommonUtils.isNull(variant) || CommonUtils.isEqual(variant.getId(), ApicConstants.NO_VARIANT_ID)
          ? null : variant.getId();

      A2lWpRespStatusUpdationModel a2lWpRespStatusInptUpdModel =
          cdrReviewResultLoader.getInputUpdationModel(a2lWPRespStatusBeforeUpdMap, listOfA2lWPRespStatusToBeCreated,
              a2lWPRespStatusLoader, varId, wpRespStatusOutputInternalModel.getWpRespStatus());

      // Update the 'WP finished Status' in T_A2L_WP_Responsibility_Status
      A2lWpRespStatusUpdationCommand a2lWPRespUpdCmd =
          new A2lWpRespStatusUpdationCommand(getServiceData(), a2lWpRespStatusInptUpdModel);
      executeCommand(a2lWPRespUpdCmd);

      // Update the wpRespStatusOutputModel with output value of executed command for handling CNS refresh
      cdrReviewResultLoader.setUpdModelOutputValue(wpRespStatusOutputModel, a2lWPRespStatusLoader, a2lWPRespUpdCmd);

      wpRespStatusOutputModel.setCompletedWPRespMap(
          convertToRespWpStatusMsgWrapper(wpRespStatusOutputInternalModel.getCompletedWPRespMap()));
      wpRespStatusOutputModel.setInCompleteWPRespMap(
          convertToRespWpStatusMsgWrapper(wpRespStatusOutputInternalModel.getInCompleteWPRespMap()));
      wpRespStatusOutputModel
          .setCompletedWPRespModelSet(wpRespStatusOutputInternalModel.getCompletedWPRespMap().keySet());
      wpRespStatusOutputModel
          .setInCompleteWPRespModelSet(wpRespStatusOutputInternalModel.getInCompleteWPRespMap().keySet());

      asyncResponse.resume(wpRespStatusOutputModel);

    }
  }

  /**
   * create WP Archive from A2l tree structure
   *
   * @param asyncResponse Response type as input
   * @param wpArchivalServiceModel as input
   * @throws IcdmException exception while invoking service
   * @throws ExecutionException as exception
   * @throws InterruptedException as exception
   */
  @PUT
  @Path(WsCommonConstants.RWS_ARCHIVE_WORKPACKAGE)
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  @ManagedAsync
  public void a2lArchiveWorkPackage(@Suspended final AsyncResponse asyncResponse,
      final WPArchivalServiceModel wpArchivalServiceModel)
      throws IcdmException, InterruptedException, ExecutionException {

    asyncResponse.register(new CompletionCallback() {

      @Override
      public void onComplete(final Throwable throwable) {
        if (throwable != null) {
          getLogger().error(throwable.getMessage(), throwable);
        }
      }
    });

    try (ServiceData sdata = new ServiceData()) {

      super.getServiceData().copyTo(sdata, false);

      WpArchivalCommonModel wpArchivalModel = new WpArchivalCommonModel();
      LoggingContext loggingContext = new LoggingContext();

      // create WP archives for all the WpResps that are completed and marked as finished
      // One of getCdrReviewResult or getTreeViewSelectnRespWP will be null based on if the call is from review result
      // editor or a2l tree
      // Handle the unique SID(requestId) and method for entire API service call
      CommonUtils.setLogger(UUID.randomUUID().toString(), CommonUtilConstants.WPA_CREATE_BASELINE_FILES);
      getLogger().debug("Create WP Archive from A2l tree structure started.");
      loggingContext.setRequestId(ThreadContext.get(CommonUtilConstants.REQUEST_ID));
      loggingContext.setMethod(ThreadContext.get(CommonUtilConstants.METHOD));
      createWpArchivesForCompletedWpResp(wpArchivalServiceModel.getCompletedWPRespMap(),
          CommonUtils.isNotNull(wpArchivalServiceModel.getCdrReviewResult())
              ? wpArchivalServiceModel.getCdrReviewResult().getId() : null,
          wpArchivalServiceModel.getTreeViewSelectnRespWP(), wpArchivalModel, loggingContext);

      asyncResponse.resume(wpArchivalModel);
    }
  }

  /**
   * @param wpRespStatusOutputInternalModel
   * @param rvwResultId
   * @param selTreeViewWpResp
   * @param loggingContext
   * @param wpRespStatusOutputModel
   * @throws UnAuthorizedAccessException
   * @throws InterruptedException
   */
  private void createWpArchivesForCompletedWpResp(final Set<A2lWPRespModel> completedWPRespMap, final Long rvwResultId,
      final TreeViewSelectnRespWP selTreeViewWpResp, final WpArchivalCommonModel wpArchivalModel,
      final LoggingContext loggingContext)
      throws UnAuthorizedAccessException, InterruptedException {
    // maps to store the status of the WPResp models
    ConcurrentMap<A2lWPRespModel, WPRespStatusMsgWrapper> archiveCompletedWPRespMap = new ConcurrentHashMap<>();
    ConcurrentMap<A2lWPRespModel, WPRespStatusMsgWrapper> archiveFailedWPRespMap = new ConcurrentHashMap<>();

    if (!completedWPRespMap.isEmpty()) {
      // fetch the no of threads to be used for multithreading in WP archival
      String threadCnt = new CommonParamLoader(getServiceData()).getValue(CommonParamKey.WP_ARCHIVAL_THREAD_CNT);
      int cnt = CommonUtils.isNotEmptyString(threadCnt) ? Integer.valueOf(threadCnt) : 3;
      ExecutorService executorService = Executors.newFixedThreadPool(cnt);
      for (A2lWPRespModel a2lWPRespModel : completedWPRespMap) {
        try (ServiceData serviceData = new ServiceData()) {
          List<Long> rvwResultIdList =
              getLatestRvwResultIdsForWP(a2lWPRespModel, serviceData, rvwResultId, selTreeViewWpResp);
          getServiceData().copyTo(serviceData, true);
          if (!rvwResultIdList.isEmpty()) {
            // invoke a thread for each WP resp model
            WpArchivalRunnableService wpArchivalRunnableService =
                new WpArchivalRunnableService(a2lWPRespModel, serviceData, archiveCompletedWPRespMap,
                    archiveFailedWPRespMap, rvwResultIdList, wpArchivalModel, loggingContext);
            executorService.submit(wpArchivalRunnableService);
          }
        }
      }
      try {
        // wait for all threads to be executed
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
      }
      catch (InterruptedException e) {
        getLogger().error("An exception occured during creation of wp archival files" + e.getMessage());
        Thread.currentThread().interrupt();
      }
    }
    // set all the final data in the wpRespStatusOutputModel which will be returned to client UI
    wpArchivalModel.setWpArchivalCompletedWPRespMap(convertToRespWpStatusMsgWrapper(archiveCompletedWPRespMap));
    wpArchivalModel.setWpArchivalFailedWPRespMap(convertToRespWpStatusMsgWrapper(archiveFailedWPRespMap));
  }

  /**
   * Get all the Reviews results of the latest labels for the WPResp
   *
   * @param a2lWPRespModel
   * @param serviceData
   * @param rvwResultId
   * @param selTreeViewWpResp
   * @return
   */
  private List<Long> getLatestRvwResultIdsForWP(final A2lWPRespModel a2lWPRespModel, final ServiceData serviceData,
      final Long rvwResultId, final TreeViewSelectnRespWP selTreeViewWpResp) {
    Set<Long> resList = new HashSet<>();
    try {
      Long variantID;
      Long pidcA2lID;
      // fetch variant id and pidca2lid
      if (CommonUtils.isNotNull(selTreeViewWpResp)) {
        variantID = selTreeViewWpResp.getVariantID();
        pidcA2lID = selTreeViewWpResp.getPidcA2lID();
      }
      else {
        CDRReviewResultLoader cdrReviewResultLoader = new CDRReviewResultLoader(serviceData);
        TRvwResult tRvwResult = cdrReviewResultLoader.getEntityObject(rvwResultId);
        PidcVariant variant = cdrReviewResultLoader.getVariant(tRvwResult);
        variantID = (CommonUtils.isNull(variant) || CommonUtils.isEqual(variant.getId(), ApicConstants.NO_VARIANT_ID))
            ? ApicConstants.NO_VARIANT_ID : variant.getId();
        pidcA2lID = tRvwResult.getTPidcA2l().getPidcA2lId();
      }

      TPidcA2l tPidcA2l = new PidcA2lLoader(serviceData).getEntityObject(pidcA2lID);
      Long pidcVersId = new A2lWorkPackageLoader(serviceData).getEntityObject(a2lWPRespModel.getA2lWpId())
          .getPidcVersion().getPidcVersId();

      // fetch all the paramIds that are in the WPResp
      List<WpRespLabelResponse> wpRespLabResponse =
          new A2lWpResponsibilityLoader(getServiceData()).getWpResp(pidcA2lID, variantID);
      WorkPackageStatusHandler workPackageStatusHandler = new WorkPackageStatusHandler(serviceData);
      Map<WpRespModel, List<Long>> resolveWpRespLabels =
          workPackageStatusHandler.resolveWpRespLabels(wpRespLabResponse);
      WpRespModel wpRespModelVal = workPackageStatusHandler.getWpRespModel(resolveWpRespLabels,
          a2lWPRespModel.getA2lRespId(), a2lWPRespModel.getA2lWpId());
      List<Long> paramIdList = resolveWpRespLabels.get(wpRespModelVal);

      // for all the ParamIds fetch the respective latest review results and add them into a set
      Map<Long, WPFinishRvwDet> paramWpFinishedRvwDetMap =
          new CDRReportLoader(getServiceData()).fetchRvwDataForA2lWpRespParam(
              tPidcA2l.getTabvProjectidcard().getProjectId(), pidcVersId, variantID, pidcA2lID, paramIdList);
      if (!paramWpFinishedRvwDetMap.isEmpty()) {
        for (WPFinishRvwDet wpFinishRvwDet : paramWpFinishedRvwDetMap.values()) {
          resList.add(wpFinishRvwDet.getResultId());
        }
      }
    }
    catch (IcdmException e) {
      getLogger().error("An exception occured during creation of wp archival files" + e.getMessage());
    }
    return new ArrayList<>(resList);
  }

  /**
   * @param wPRespMap
   * @return
   */
  private Map<Long, Map<Long, WPRespStatusMsgWrapper>> convertToRespWpStatusMsgWrapper(
      final Map<A2lWPRespModel, WPRespStatusMsgWrapper> wPRespMap) {
    Map<Long, Map<Long, WPRespStatusMsgWrapper>> wpRespMsgWrapperMap = new HashMap<>();
    for (Entry<A2lWPRespModel, WPRespStatusMsgWrapper> completedWpRespMapEntry : wPRespMap.entrySet()) {
      A2lWPRespModel a2lWPRespModel = completedWpRespMapEntry.getKey();
      if (wpRespMsgWrapperMap.containsKey(a2lWPRespModel.getA2lRespId())) {
        Map<Long, WPRespStatusMsgWrapper> wpStatusMsgWrapperMap =
            wpRespMsgWrapperMap.get(a2lWPRespModel.getA2lRespId());
        wpStatusMsgWrapperMap.put(a2lWPRespModel.getA2lWpId(), completedWpRespMapEntry.getValue());
      }
      else {
        Map<Long, WPRespStatusMsgWrapper> wpStatusMshMap = new HashMap<>();
        wpStatusMshMap.put(a2lWPRespModel.getA2lWpId(), completedWpRespMapEntry.getValue());
        wpRespMsgWrapperMap.put(a2lWPRespModel.getA2lRespId(), wpStatusMshMap);
      }
    }
    return wpRespMsgWrapperMap;
  }

  /**
   * @param pidcA2lId pidca2lid
   * @return the review results associated with the pidca2lid
   * @throws IcdmException error during webservice call
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_CDRRESULTS_BY_PIDCA2L)
  @CompressData
  public Response getCDRResultsByPidcA2l(@QueryParam(value = WsCommonConstants.RWS_QP_PIDC_A2L_ID) final Long pidcA2lId)
      throws IcdmException {
    PidcA2lLoader loader = new PidcA2lLoader(getServiceData());
    SortedSet<CDRReviewResult> retSet = loader.getCDRResultsByPidcA2l(pidcA2lId);
    return Response.ok(retSet).build();
  }


  /**
   * @param objectId
   * @return
   * @throws IcdmException
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_RVW_RES_DELTA)
  @CompressData
  public Response getReviewResultForDeltaReview(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objectId)
      throws IcdmException {
    CDRReviewResultLoader loader = new CDRReviewResultLoader(getServiceData());
    CDRWizardUIModel retSet = loader.fillCDRModelForDeltaReview(objectId);
    return Response.ok(retSet).build();
  }

  /**
   * @param resultId resultId
   * @return result details required for copy result to variant
   * @throws IcdmException error during webservice call
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_COPY_VAR_TO_RESULT)
  @CompressData
  public Response getResultDetailsForAttachVar(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long resultId)
      throws IcdmException {
    CopyResultToVarData resultData = new CopyResultToVarData();

    CDRReviewResultLoader resultLoader = new CDRReviewResultLoader(getServiceData());
    TRvwResult entityObject = resultLoader.getEntityObject(resultId);
    CDRReviewResult result = resultLoader.getDataObjectByID(resultId);


    RvwVariantLoader variantLoader = new RvwVariantLoader(getServiceData());
    resultData.setRvwVariantsMap(variantLoader.getByResultObj(entityObject));
    PidcVariantLoader pidcVarLoader = new PidcVariantLoader(getServiceData());

    for (RvwVariant rvwVar : resultData.getRvwVariantsMap().values()) {
      resultData.getReviewPidcVars().add(pidcVarLoader.getDataObjectByID(rvwVar.getVariantId()));
    }


    SortedSet<PidcVariant> pidcVarSet = new TreeSet<>();
    long pidcVersId = entityObject.getTPidcA2l().getTPidcVersion().getPidcVersId();
    entityObject.getTPidcA2l().getActiveWpParamPresentFlag();
    pidcVarSet.addAll(pidcVarLoader.getVariants(pidcVersId, false).values());
    resultData.setPidcVariants(pidcVarSet);

    Map<Long, PidcVariant> mappedVariantsMap =
        new PidcVariantLoader(getServiceData()).getA2lMappedVariants(entityObject.getTPidcA2l().getPidcA2lId(), false);
    resultData.setA2lMappedVariantsMap(mappedVariantsMap);

    ProjectAttributeLoader pidcAttrLoader = new ProjectAttributeLoader(getServiceData());
    PidcVersionAttributeModel attrModel = pidcAttrLoader.createModel(pidcVersId, LOAD_LEVEL.L3_VAR_ATTRS);
    resultData.setAllVariantAttributeMap(attrModel.getAllVariantAttributeMap());

    Map<Long, Long> attrValMap = new ConcurrentHashMap<>();
    Map<Long, String> attrUsedMap = new ConcurrentHashMap<>();
    RvwAttrValueLoader attrValLoader = new RvwAttrValueLoader(getServiceData());

    setAttrMap(attrValLoader.getByResultObj(entityObject), attrValMap, attrUsedMap);
    if (!resultData.getRvwVariantsMap().isEmpty()) {
      Collection<PidcVariantAttribute> pidcVarAttrs =
          attrModel.getAllVariantAttributeMap().get(result.getPrimaryVariantId()).values();
      addSdomPverToAttrMap(pidcVarAttrs, attrValMap);
    }

    resultData.setAttrUsedMap(attrUsedMap);
    resultData.setAttrValMap(attrValMap);
    resultData.setPidcVersion(attrModel.getPidcVersion());

    getVarGrpStatus(entityObject.getTPidcA2l(), result.getPrimaryVariantId(), resultData.getSameVarGrpFlag());
    return Response.ok(resultData).build();
  }

  /**
   * @param tPidcA2l
   * @param primaryVarId
   * @param statusMap
   * @return
   * @throws IcdmException
   */
  private void getVarGrpStatus(final TPidcA2l tPidcA2l, final Long primaryVarId, final Map<Long, Boolean> statusMap)
      throws IcdmException {


    // get the active version
    A2lWpDefnVersionLoader wpDefVerLoader = new A2lWpDefnVersionLoader(getServiceData());
    Map<Long, A2lWpDefnVersion> a2lWpDefVersionMap =
        wpDefVerLoader.getWPDefnVersionsForPidcA2lId(tPidcA2l.getPidcA2lId());


    A2lWpDefnVersion activeVersion = null;
    for (A2lWpDefnVersion a2lWpDefVersion : a2lWpDefVersionMap.values()) {
      if (a2lWpDefVersion.isActive()) {
        activeVersion = a2lWpDefVersion;
        break;
      }
    }

    // case only if Active version is available.
    if (activeVersion != null) {
      A2LDetailsStructureModel detailsModel = wpDefVerLoader.getDetailsModel(activeVersion.getId(), false);
      A2lVarGrpVariantMapping a2lVarGrpMapping = detailsModel.getGroupMappingMap().get(primaryVarId);
      Map<Long, PidcVariant> a2lMappedVariantsMap = detailsModel.getA2lMappedVariantsMap();
      if (null == a2lVarGrpMapping) {
        // if there is no var group mapping
        a2lMappedVariantsMap.values().forEach(vari -> statusMap.put(vari.getId(), false));

        // fill status map for unmapped variants
        detailsModel.getUnmappedVariants().forEach(var -> statusMap.put(var.getId(), true));
      }
      else {
        Map<Long, List<PidcVariant>> mappedVariantsMap = detailsModel.getMappedVariantsMap();
        Long a2lVarGroupId = a2lVarGrpMapping.getA2lVarGroupId();

        // fill status map for mapped variants
        fillMappedVarStatus(statusMap, a2lVarGroupId, mappedVariantsMap);

        // fill status map for unmapped variants
        detailsModel.getUnmappedVariants().forEach(var -> statusMap.put(var.getId(), false));
      }


    }

  }

  /**
   * @param statusMap
   * @param a2lVarGroupId
   * @param mappedVariantsMap
   */
  private void fillMappedVarStatus(final Map<Long, Boolean> statusMap, final Long a2lVarGroupId,
      final Map<Long, List<PidcVariant>> mappedVariantsMap) {
    mappedVariantsMap.entrySet().forEach(varGrpMapEntry -> {

      final boolean sameGroup;
      if (varGrpMapEntry.getKey().longValue() == a2lVarGroupId.longValue()) {
        sameGroup = true;
      }
      else {
        sameGroup = false;
      }
      varGrpMapEntry.getValue().forEach(var -> statusMap.put(var.getId(), sameGroup));

    });
  }

  /**
   * Adds sdom pvers to map
   *
   * @param variant
   * @param attrValMap
   * @throws IcdmException
   */
  private void addSdomPverToAttrMap(final Collection<PidcVariantAttribute> pidcVarAttrs,
      final Map<Long, Long> attrValMap)
      throws IcdmException {

    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    for (PidcVariantAttribute pidcAttr : pidcVarAttrs) {
      Attribute attr = attrLoader.getDataObjectByID(pidcAttr.getAttrId());
      if (attr.getLevel() == SDOM_PROJECT_NAME_ATTR) {
        attrValMap.put(attr.getId(), pidcAttr.getValueId());
      }
    }
  }

  /**
   * @param resAttrMap
   * @param attrValMap
   * @param attrUsedMap
   */
  private void setAttrMap(final Map<Long, RvwAttrValue> resAttrMap, final Map<Long, Long> attrValMap,
      final Map<Long, String> attrUsedMap) {

    for (RvwAttrValue pidcAttr : resAttrMap.values()) {
      // Set the used flag yes or no if the value is null.
      if (pidcAttr.getValueId() == null) {
        if (pidcAttr.getUsed().equals(CommonUtilConstants.CODE_YES)) {
          attrUsedMap.put(pidcAttr.getAttrId(), ApicConstants.USED_YES_DISPLAY);
        }
        else {
          attrUsedMap.put(pidcAttr.getAttrId(), ApicConstants.USED_NO_DISPLAY);
        }
      }
      // set the attr value map
      else {
        attrValMap.put(pidcAttr.getAttrId(), pidcAttr.getValueId());
      }
    }
  }

  /**
   * @param cdrResultId result id
   * @return true is result can be modified
   * @throws IcdmException error during webservice call
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_CAN_MODIFY_RESULT)
  @CompressData
  public Response canModify(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long cdrResultId) throws IcdmException {
    boolean isModifiable = false;
    CDRReviewResultLoader resultLoader = new CDRReviewResultLoader(getServiceData());
    CDRReviewResult result = resultLoader.getDataObjectByID(cdrResultId);
    TPidcA2l tPidcA2l = new PidcA2lLoader(getServiceData()).getEntityObject(result.getPidcA2lId());
    UserLoader userLoader = new UserLoader(getServiceData());
    User currentUser = userLoader.getDataObjectCurrentUser();

    RvwParticipantLoader participantsLoader = new RvwParticipantLoader(getServiceData());
    Map<Long, RvwParticipant> participantsMap =
        participantsLoader.getByResultObj(resultLoader.getEntityObject(cdrResultId));
    RvwParticipant calibrationEngr = null;
    RvwParticipant auditor = null;
    final SortedSet<RvwParticipant> othrUsrSet = new TreeSet<>();
    for (RvwParticipant participant : participantsMap.values()) {
      if ("C".equals(participant.getActivityType())) {
        calibrationEngr = participant;
      }
      else if ("A".equals(participant.getActivityType())) {
        auditor = participant;
      }
      else if ("P".equals(participant.getActivityType())) {
        othrUsrSet.add(participant);
      }
    }
    if (validateCurrUserDetails(currentUser, calibrationEngr) || validateCurrUserDetails(currentUser, auditor) ||
        CommonUtils.isEqual(result.getCreatedUser(), currentUser.getName()) ||
        canOtherParticipantsModify(othrUsrSet, currentUser) ||
        new NodeAccessLoader(getServiceData()).isCurrentUserOwner(tPidcA2l.getTabvProjectidcard().getProjectId())) {
      isModifiable = true;
    }
    return Response.ok(isModifiable).build();
  }

  /**
   * @param currentUser
   * @param rvwParticipant
   * @return
   */
  private boolean validateCurrUserDetails(final User currentUser, final RvwParticipant rvwParticipant) {
    return (rvwParticipant != null) && (currentUser.getId().longValue() == rvwParticipant.getUserId().longValue());
  }

  /**
   * @param currUser
   * @return
   */
  private boolean canOtherParticipantsModify(final SortedSet<RvwParticipant> othrUsrSet, final User currentUser) {

    for (RvwParticipant otherUser : othrUsrSet) {
      if (otherUser.getUserId().longValue() == currentUser.getId().longValue()) {
        return true;
      }
    }
    return false;

  }

  /**
   * Service for fetching review results availability for a pidc version
   *
   * @param pidcVerId pidc ver id
   * @return response
   * @throws IcdmException web service exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_HAS_PIDC_REV_RESULTS)
  @CompressData
  public Response hasPidcRevResults(@QueryParam(value = WsCommonConstants.RWS_QP_PIDC_VERS_ID) final Long pidcVerId)
      throws IcdmException {
    CDRReviewResultLoader pidcCdrLoader = new CDRReviewResultLoader(getServiceData());
    return Response.ok(pidcCdrLoader.getPidcCdrAvailability(pidcVerId)).build();
  }


  /**
   * @param cdrResultId as result id
   * @return boolean
   * @throws IcdmException as exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_CDFX_DELIVERY_USAGE_CHECK)
  @CompressData
  public Response isUsedInCDFXDelivery(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long cdrResultId)
      throws IcdmException {
    return Response.ok(new CDRReviewResultLoader(getServiceData()).isUsedInCDFXDelivery(cdrResultId)).build();
  }

  /**
   * Service for fetching all pidc versions for a pidc
   *
   * @param pidcVerId pidc ver id
   * @return response
   * @throws IcdmException web service exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_PIDC_REV_RESULTS)
  @CompressData
  public Response getPidcRevResults(@QueryParam(value = WsCommonConstants.RWS_QP_PIDC_VERS_ID) final Long pidcVerId)
      throws IcdmException {
    CDRReviewResultLoader pidcCdrLoader = new CDRReviewResultLoader(getServiceData());
    Map<String, Map<Long, CDRReviewResult>> grpwpCdrMap = pidcCdrLoader.getCdrResultsForPidcVer(pidcVerId);
    return Response.ok(grpwpCdrMap).build();
  }


  /**
   * Service for fetching variant review results for PIDC tree
   *
   * @param pidcVerId pidc ver id
   * @return response
   * @throws IcdmException web service exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_PIDC_RVW_RESULT_INFO)
  @CompressData
  public Response getReviewResultsInfo(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long pidcVerId)
      throws IcdmException {
    CDRReviewResultLoader pidcCdrLoader = new CDRReviewResultLoader(getServiceData());
    PidcReviewDetailsResponse pidcReviewDetails = pidcCdrLoader.fetchPidcReviewResultInfo(pidcVerId);
    return Response.ok(pidcReviewDetails).build();
  }


  /**
   * @param resultId review result id
   * @return PidcReviewDetails
   * @throws IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_NEW_REVIEW_DETAILS)
  @CompressData
  public Response getNewReviewResultsDetails(
      @QueryParam(value = WsCommonConstants.RWS_QP_RESULT_ID) final Long resultId,
      @QueryParam(value = WsCommonConstants.RWS_QP_VARIANT_ID) final Long rvwVarId)
      throws IcdmException {
    PidcReviewDetailsResponse pidcReviewDetails;
    if (rvwVarId == null) {
      CDRReviewResultLoader pidcCdrLoader = new CDRReviewResultLoader(getServiceData());
      pidcReviewDetails = pidcCdrLoader.fetchNewRvwResultDetailsForNoVariant(resultId);
    }
    else {
      CDRReviewResultLoader pidcCdrLoader = new CDRReviewResultLoader(getServiceData());
      pidcReviewDetails = pidcCdrLoader.fetchNewRvwResultDetailsForVariant(rvwVarId);
    }
    return Response.ok(pidcReviewDetails).build();
  }

  /**
   * service for resolving fc2wp name
   *
   * @param rvwResId review result id
   * @return response
   * @throws IcdmException web service exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_RESOLVE_FC2WP_NAME)
  @CompressData
  public Response resolveFc2WpName(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long rvwResId)
      throws IcdmException {
    CDRReviewResultLoader cdrLoader = new CDRReviewResultLoader(getServiceData());
    String f2wpName = cdrLoader.resolveFc2Wp(cdrLoader.getEntityObject(rvwResId));
    return Response.ok(f2wpName).build();
  }

  /**
   * Service for fetching variant review results
   *
   * @param pidcVerId pidc ver id
   * @return response
   * @throws IcdmException web service exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_PIDC_VAR_REV_RES)
  @CompressData
  public Response getPidcVarRevResults(@QueryParam(value = WsCommonConstants.RWS_QP_PIDC_VERS_ID) final Long pidcVerId)
      throws IcdmException {
    CDRReviewResultLoader pidcCdrLoader = new CDRReviewResultLoader(getServiceData());
    Map<String, PidcTreeRvwVariant> grpwpCdrMap = pidcCdrLoader.getVarRevResultsForPidcVer(pidcVerId);
    return Response.ok(grpwpCdrMap).build();
  }

  /**
   * Get CDR Review Result Editor Data
   *
   * @param resultId ,Result Id id
   * @param rvwVarId review variant id
   * @return Rest response, with ReviewResultEditorData
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_RVW_EDITOR_DATA)
  @CompressData
  public Response getRvwResultEditorData(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long resultId,
      @QueryParam(WsCommonConstants.RWS_QP_RVW_VAR_ID) final Long rvwVarId)
      throws IcdmException {
    ReviewResultEditorData editorData =
        new CDRRvwResultEditorDataLoader(getServiceData()).getReviewResultEditorData(resultId, rvwVarId);
    return Response.ok(editorData).build();
  }
}
