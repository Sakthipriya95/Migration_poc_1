/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.cdr.RvwVariantCommand;
import com.bosch.caltool.icdm.bo.cdr.RvwVariantLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.cdr.AttachRvwResultInput;
import com.bosch.caltool.icdm.model.cdr.AttachRvwResultResponse;
import com.bosch.caltool.icdm.model.cdr.ReviewVariantModel;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;

/**
 * @author say8cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_CDR_RVW_VARIANT)

public class RvwVariantService extends AbstractRestService {

  /**
   * @param objId Review variant ID
   * @return Response with list of functions
   * @throws IcdmException IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response getById(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    // Fetch review variant object
    RvwVariant ret = new RvwVariantLoader(getServiceData()).getDataObjectByID(objId);

    getLogger().info("RvwVariantService.get() completed. RvwVariant found = {}", ret.getName());

    return Response.ok(ret).build();

  }

  /**
   * @param pidcVerId
   * @return Key - WorkPackageName and Value - Sortedset of Review Variant Object
   * @throws IcdmException
   * @deprecated not used
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_RVW_VARIANT_SET)
  @CompressData
  @Deprecated
  public Response getReviewVariantListByPidcVersion(
      @QueryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ID) final Long pidcVerId)
      throws IcdmException {
    RvwVariantLoader loader = new RvwVariantLoader(getServiceData());
    Map<String, SortedSet<ReviewVariantModel>> retSet = loader.getReviewVariantListByPidcVersion(pidcVerId);
    getLogger().info(" TRvwVariant getReviewVariantListByPidcVersion completed. Total records = {}", retSet.size());
    return Response.ok(retSet).build();
  }


  /**
   * @param objId
   * @return Key - ReviewVariantId and Value - Review Variant Object
   * @throws IcdmException
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_RVW_VAR_MAP)
  @CompressData
  public Response getReviewVarMap(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    RvwVariantLoader loader = new RvwVariantLoader(getServiceData());
    Map<Long, ReviewVariantModel> retSet = loader.getReviewVarMap(objId);
    getLogger().info(" TRvwResult getReviewVarMap completed. Total records = {}", retSet.size());
    return Response.ok(retSet).build();
  }

  /**
   * @param cdrResultIds cdr review result ids
   * @return ReviewVariantModel as outlput for review list editor
   * @throws IcdmException as exception
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_RVW_VAR_MODEL_SET)
  @CompressData
  public Response getReviewVariantModelSet(final Set<Long> cdrResultIds) throws IcdmException {
    Set<ReviewVariantModel> reviewVariantModels =
        new RvwVariantLoader(getServiceData()).getReviewVariantModelSet(cdrResultIds);
    getLogger().info(" TRvwResult getReviewVariantModelSet completed. Total records = {}", reviewVariantModels.size());
    return Response.ok(reviewVariantModels).build();
  }


  /**
   * @param pidcVariantId
   * @param pidcVerId
   * @return Key - WorkPackageName and Value - Sortedset of Review Variant Object
   * @throws IcdmException
   * @deprecated not used
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_CDR_WRKPKG)
  @CompressData
  @Deprecated
  public Response getReviewVariantMapWithWPName(
      @QueryParam(WsCommonConstants.RWS_QP_VARIANT_ID) final Long pidcVariantId,
      @QueryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ID) final Long pidcVerId)
      throws IcdmException {
    RvwVariantLoader loader = new RvwVariantLoader(getServiceData());
    Map<String, SortedSet<ReviewVariantModel>> retMap =
        loader.getReviewVariantMapWithWorkPackage(pidcVariantId, pidcVerId);
    getLogger().info(" ReviewVariant getCDRWorkPackages completed. Total records = {}", retMap.size());
    return Response.ok(retMap).build();
  }

  /**
   * @param pidcVariantId
   * @param pidcVerId
   * @return set of reviewVariant object
   * @throws IcdmException
   * @deprecated not used
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_RVW_VARIANT)
  @CompressData
  @Deprecated
  public Response getReviewVariantListByPidcVariant(
      @QueryParam(WsCommonConstants.RWS_QP_VARIANT_ID) final Long pidcVariantId,
      @QueryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ID) final Long pidcVerId)
      throws IcdmException {
    RvwVariantLoader loader = new RvwVariantLoader(getServiceData());
    Set<ReviewVariantModel> retSet = loader.getReviewVariantListByPidcVariant(pidcVariantId, pidcVerId);
    getLogger().info(" TRvwvariant getReviewVariantListByPidcVariant completed. Total records = {}", retSet.size());
    return Response.ok(retSet).build();
  }

  /**
   * Create a RvwVariant record
   *
   * @param obj object to create
   * @return Rest response, with created RvwVariant object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final RvwVariant obj) throws IcdmException {
    RvwVariantCommand cmd = new RvwVariantCommand(getServiceData(), obj, false, false);
    executeCommand(cmd);

    RvwVariant ret = cmd.getNewData();

    getLogger().info("Created RvwVariant Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Delete a RvwVariant record
   *
   * @param obj object to delete
   * @return Rest response, with created Attribute object
   * @throws IcdmException exception while invoking service
   */
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response delete(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long rvwVarId)
      throws IcdmException {

    RvwVariantCommand cmd = new RvwVariantCommand(getServiceData(),
        new RvwVariantLoader(getServiceData()).getDataObjectByID(rvwVarId), false, true);
    executeCommand(cmd);

    return Response.ok().build();
  }

  /**
   * Get review variant object for the given CDR result and Variant ID
   *
   * @param resultId CDR result ID
   * @param variantId Variant ID
   * @return Rest response with review variant
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_RVW_VARIANT_BY_RESULT_N_VAR)
  @CompressData
  public Response getRvwVariantByResultNVarId(
      @QueryParam(value = WsCommonConstants.RWS_QP_RESULT_ID) final long resultId,
      @QueryParam(value = WsCommonConstants.RWS_QP_VARIANT_ID) final long variantId)
      throws IcdmException {

    RvwVariant ret = new RvwVariantLoader(getServiceData()).getRvwVariantByResultNVarId(resultId, variantId);
    return Response.ok(ret).build();
  }

  /**
   * Create a RvwVariant record with questionnaires
   *
   * @param inp object to create
   * @return Rest response, with created RvwVariant object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_ATTACH_RVW_RES_WITH_QNAIRE)
  @CompressData
  public Response attachRvwResWithQnaire(final AttachRvwResultInput inp) throws IcdmException {
    RvwVariantCommand cmd =
        new RvwVariantCommand(getServiceData(), inp.getRvwVariant(), false, false, true, inp.isLinkExistingQnaire());
    executeCommand(cmd);

    AttachRvwResultResponse ret = new AttachRvwResultResponse();
    ret.setRvwVariant(cmd.getNewData());
    ret.setQaireRespSkipped(cmd.getQnaireRespSkipped());

    getLogger().info("Created RvwVariant Id : {}", ret.getRvwVariant().getId());
    return Response.ok(ret).build();
  }
}
