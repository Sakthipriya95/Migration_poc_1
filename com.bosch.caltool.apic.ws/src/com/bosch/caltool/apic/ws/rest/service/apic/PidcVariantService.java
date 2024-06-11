/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

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
import com.bosch.caltool.icdm.bo.apic.cocwp.PidcVariantCocWpLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantCommand;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantsInputData;
import com.bosch.caltool.icdm.model.apic.pidc.ProjectObjectWithAttributes;

/**
 * @author DMR1COB
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_PIDCVARIANT)
public class PidcVariantService extends AbstractRestService {


  /**
   * Get Project variant using its id
   *
   * @param objId object's id
   * @return Rest response, with ProjectVariant object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response get(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    PidcVariantLoader loader = new PidcVariantLoader(getServiceData());
    PidcVariant ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }


  /**
   * Create a Project variant record
   *
   * @param obj object to create
   * @return Rest response, with created ProjectVariant object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final PidcVariantData obj) throws IcdmException {
    PidcVariantCommand cmd = new PidcVariantCommand(getServiceData(), obj, false);
    executeCommand(cmd);
    PidcVariantData ret = new PidcVariantData();
    ret.setDestPidcVar(cmd.getNewData());
    PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());
    ret.setPidcVersion(pidcVersionLoader.getDataObjectByID(cmd.getNewData().getPidcVersionId()));
    // setting pidcvarcocwp set
    ret.setCreatedPidcVarCocWpSet(
        new PidcVariantCocWpLoader(getServiceData()).getAllPidcVarCocWpByVarId(cmd.getObjId()));
    getLogger().info("Created Project variant Id : {}", ret.getDestPidcVar().getId());
    return Response.ok(ret).build();
  }

  /**
   * Update a Project variant record
   *
   * @param obj object to update
   * @return Rest response, with updated ProjectVariant object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final PidcVariantData obj) throws IcdmException {
    PidcVariantCommand cmd = new PidcVariantCommand(getServiceData(), obj, true);
    executeCommand(cmd);
    PidcVariantData ret = new PidcVariantData();
    ret.setDestPidcVar(cmd.getNewData());
    PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());
    ret.setPidcVersion(pidcVersionLoader.getDataObjectByID(cmd.getNewData().getPidcVersionId()));
    getLogger().info("Updated Project variant Id : {}", ret.getDestPidcVar().getId());
    return Response.ok(ret).build();
  }


  /**
   * Get Pidc Variants belonging to the pidc version id and a2l file id
   *
   * @param pidcVersionId pidcVersionId
   * @param a2lFileId a2lFileId
   * @return Rest response
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_PIDC_VARIANT_FOR_PIDC_VERS_N_A2L_ID)
  @CompressData
  public Response getPidcVarForPidcVersAndA2l(
      @QueryParam(WsCommonConstants.RWS_PIDC_VERSION_ID) final Long pidcVersionId,
      @QueryParam(WsCommonConstants.RWS_A2L_FILE_ID) final Long a2lFileId)
      throws IcdmException {

    getLogger().info(
        "PidcVariantService.getPidcVarForPidcVersAndA2l() started. Pidc Version Id : {} and A2l File Id : {} ",
        pidcVersionId, a2lFileId);

    PidcVariantLoader pidcVariantLoader = new PidcVariantLoader(getServiceData());

    SortedSet<PidcVariant> pidcVariants = pidcVariantLoader.getPidcVarForPidcVersAndA2l(pidcVersionId, a2lFileId);

    getLogger().info("PidcVariantService.getPidcVarForPidcVersAndA2l() completed. Number of variants = {}",
        pidcVariants.size());

    return Response.ok(pidcVariants).build();
  }


  /**
   * To check the pidc version have any variant dependency
   *
   * @param pidcVersionId
   * @param includeDeleted
   * @return
   * @throws IcdmException
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_HAS_PIDC_VARIANT)
  @CompressData
  public Response getPidcVariant(@QueryParam(WsCommonConstants.RWS_PIDC_VERSION_ID) final Long pidcVersionId,
      @QueryParam(WsCommonConstants.RWS_QP_INCLUDE_DELETED) final boolean includeDeleted)
      throws IcdmException {
    PidcVariantLoader pidcVariantLoader = new PidcVariantLoader(getServiceData());
    boolean hasPidcVariant = pidcVariantLoader.hasVariants(pidcVersionId, includeDeleted);
    return Response.ok(hasPidcVariant).build();
  }

  /**
   * @param pidcVersionId pidcVersionId
   * @param includeDeleted boolean
   * @return Rest response
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_VAR_FOR_VERSION)
  @CompressData
  public Response getVariantsForVersion(@QueryParam(WsCommonConstants.RWS_PIDC_VERSION_ID) final Long pidcVersionId,
      @QueryParam(WsCommonConstants.RWS_QP_INCLUDE_DELETED) final boolean includeDeleted)
      throws IcdmException {

    Map<Long, PidcVariant> variantsMap =
        new PidcVariantLoader(getServiceData()).getVariants(pidcVersionId, includeDeleted);

    getLogger().info("PidcVariantService.getVariantsForVersion() finished. Variants = {}", variantsMap.size());

    return Response.ok(variantsMap).build();
  }

  /**
   * Get all variants of a pidc version, mapped to an a2l file
   *
   * @param pidcA2lId PIDC A2L mapping ID
   * @return map of pidc variants. key - variant ID, value - variant
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_A2L_MAPPED_VAR_FOR_VERSION)
  @CompressData
  public Response getA2lMappedVariants(@QueryParam(WsCommonConstants.RWS_QP_PIDC_A2L_ID) final Long pidcA2lId)
      throws IcdmException {

    Map<Long, PidcVariant> variantsMap = new PidcVariantLoader(getServiceData()).getA2lMappedVariants(pidcA2lId,false);

    getLogger().info("PidcVariantService.getMappedVariantsForVersion() finished. Variants = {}", variantsMap.size());

    return Response.ok(variantsMap).build();
  }

  /**
   * Get all variants of a pidc version, mapped to sdom
   *
   * @param pidcA2lId PIDC A2L mapping ID
   * @return map of pidc variants. key - variant ID, value - variant
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_SDOM_MAPPED_VAR_FOR_VERSION)
  @CompressData
  public Response getSdomPverMappedVariants(@QueryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ID) final Long pidcVersId,
      @QueryParam(WsCommonConstants.RWS_QP_SDOM_PVER_NAME) final String sdomPverName)
      throws IcdmException {

    Map<Long, PidcVariant> variantsMap =
        new PidcVariantLoader(getServiceData()).getSdomPverMappedVariants(sdomPverName, pidcVersId);

    getLogger().info("PidcVariantService.getSdomPverMappedVariants() finished. Variants = {}", variantsMap.size());

    return Response.ok(variantsMap).build();
  }

  /**
   * Get all variants of a pidc version, mapped to an a2l file
   *
   * @param pidVarId Long
   * @return map of pidc variants. key - variant ID, value - variant
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_VAR_HAS_REVIEWS)
  @CompressData
  public Response hasReviews(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long pidVarId) throws IcdmException {

    Boolean hasReviews = new PidcVariantLoader(getServiceData()).hasReviews(pidVarId);

    getLogger().info("PidcVariantService.getMappedVariantsForVersion() finished. Variants = {}", hasReviews);

    return Response.ok(hasReviews).build();
  }

  /**
   * Get all pidc variants of pidc version, including the variant attributes, for given attribute list
   *
   * @param pidcVersId PIDC version ID
   * @param attrIdSet set of attrribute IDs
   * @return Map - key variant Id, value variant with variant attributes
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_PIDC_VARIANTS_WITH_ATTRS)
  @CompressData
  public Response getVariantsWithAttrs(@QueryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ID) final Long pidcVersId,
      @QueryParam(WsCommonConstants.RWS_QP_ATTRIBUTE_ID) final Set<Long> attrIdSet)
      throws IcdmException {

    getLogger().info(
        "PidcVariantService.getVariantsWithAttrs()  for pidc version {} and specifc attribute definitions - {}",
        pidcVersId, attrIdSet);

    Map<Long, ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute>> retMap =
        new PidcVariantLoader(getServiceData()).getVariantsWithAttrs(pidcVersId, attrIdSet);

    getLogger().info("PidcVariantService.getVariantsWithAttrs() finished. Variants count = {}", retMap.size());

    return Response.ok(retMap).build();
  }

  /**
   * @param elementIdList list of pidc variant / pidc version id
   * @return {@link PidcVariantsInputData}
   * @throws IcdmException exception
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_PIDC_VARIANTS_INPUT_DATA)
  @CompressData
  public PidcVariantsInputData getPidcVariantsInputData(final List<Long> elementIdList) throws IcdmException {
    PidcVariantLoader loader = new PidcVariantLoader(getServiceData());
    return loader.getPidcVariantsInputData(elementIdList);
  }

}
