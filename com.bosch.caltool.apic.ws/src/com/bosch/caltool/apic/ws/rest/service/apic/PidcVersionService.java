/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;
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
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcSdomPverLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeModel;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionByFilterLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionCommand;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionStatisticsReportLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader.LOAD_LEVEL;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.ExternalPidcVersionInfo;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDetStructure;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionInfo;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionStatisticsReport;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetails;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetailsInput;
import com.bosch.caltool.icdm.model.apic.pidc.ProjectIdCardAllVersInfoType;
import com.bosch.caltool.icdm.model.cdr.CompliReviewUsingHexData;


/**
 * @author bne4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_PIDC_VERS)
public class PidcVersionService extends AbstractRestService {

  /**
   * the ATTR_LEVEL used for the vCDM APRJ Name attribute
   */
  public static final int VCDM_APRJ_NAME_ATTR = -20;

  /**
   * get All active Pidc versions With Structure attributes
   *
   * @return Rest response
   * @throws IcdmException exception in service
   * @deprecated use {@link #getActiveVersionsWithStructure(Set)} instead
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_PIDC_VERS_GET_ALL_ACTIVE_VERS_WITH_STRUCT_ATTRS)
  @CompressData
  @Deprecated
  public Response getAllActiveVersionsWithStructure() throws IcdmException {
    return getActiveVersionsWithStructure(null);
  }

  /**
   * Get active Pidc versions With Structure attributes, for the given PIDC IDs. If pidc id is not provided, then all
   * active versions are returned
   *
   * @param pidcIdSet Set of PIDC IDs
   * @return Rest response
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_PIDC_VERS_GET_ACTIVE_VERS_WITH_STRUCT_ATTRS)
  @CompressData
  public Response getActiveVersionsWithStructure(
      @QueryParam(value = WsCommonConstants.RWS_QP_PIDC_ID) final Set<Long> pidcIdSet)
      throws IcdmException {

    ServiceData serviceData = getServiceData();

    Map<Long, PidcVersionInfo> retMap;
    PidcVersionLoader pidcVersLdr = new PidcVersionLoader(serviceData);

    if (CommonUtils.isNullOrEmpty(pidcIdSet)) {
      retMap = pidcVersLdr.getAllActiveVersionsWithStructureAttributes();
    }
    else {
      // Active versions
      retMap = pidcVersLdr.getActiveVersionWithStructureAttributes(pidcIdSet);
    }

    getLogger().info("PidcVersionService.getActiveVersionsWithStructure() finished. Item count = {}, Input = {}",
        retMap.size(), pidcIdSet);

    return Response.ok(retMap).build();
  }


  /**
   * Get active External Pidc versions With Structure attributes, for the given PIDC IDs. If pidc id is not provided,
   * then all active versions are returned
   *
   * @param pidcIdSet Set of PIDC IDs
   * @return Rest response
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_EXT_PIDC_VERS_GET_ACTIVE_VERS_WITH_STRUCT_ATTRS)
  @CompressData
  public Response getActiveExternalVersionsWithStructure(
      @QueryParam(value = WsCommonConstants.RWS_QP_PIDC_ID) final Set<Long> pidcIdSet)
      throws IcdmException {

    Map<Long, PidcVersionInfo> pidcVersInfoMap;
    PidcVersionLoader pidcVersLdr = new PidcVersionLoader(getServiceData());

    if (CommonUtils.isNullOrEmpty(pidcIdSet)) {
      pidcVersInfoMap = pidcVersLdr.getAllActiveVersionsWithStructureAttributes();
    }
    else {
      // Active versions
      pidcVersInfoMap = pidcVersLdr.getActiveVersionWithStructureAttributes(pidcIdSet);
    }

    Map<Long, ExternalPidcVersionInfo> retMap = pidcVersLdr.setExtPidcVersInfoMapDetails(pidcVersInfoMap);

    getLogger().info(
        "PidcVersionService.getActiveExternalVersionsWithStructure() finished. Item count = {}, Input = {}",
        retMap.size(), pidcIdSet);

    return Response.ok(retMap).build();
  }


  /**
   * Get Pidc versions With Structure attributes, for the given PIDC VERSION IDs.
   *
   * @param pidcIdSet Set of PIDCs whose values are to be fetched
   * @param pidcName String whose matching values are to be fetched
   * @param userNtIdSet Set of NT IDs
   * @param accessType Type of access for the provided NTIDs
   * @param pidcVersIdSet Set of PIDC version ids
   * @param activeFlag include active/non active versions. default is only include active. Possible values - Y, N, ALL
   * @return Rest response
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_PIDC_VERS_GET_VERS_WITH_STRUCT_ATTRS)
  @CompressData
  public Response getPidcVersionsWithStructure(
      @QueryParam(value = WsCommonConstants.RWS_QP_USERNAME) final Set<String> userNtIdSet,
      @QueryParam(value = WsCommonConstants.RWS_QP_ACCESS_TYPE) final String accessType,
      @QueryParam(value = WsCommonConstants.RWS_QP_PIDC_ID) final Set<Long> pidcIdSet,
      @QueryParam(value = WsCommonConstants.RWS_QP_PIDC_VERS_ID) final Set<Long> pidcVersIdSet,
      @QueryParam(value = WsCommonConstants.RWS_QP_PIDC_NAME) final String pidcName,
      @QueryParam(value = WsCommonConstants.RWS_QP_ACTIVEFLAG) final String activeFlag)
      throws IcdmException {

    PidcVersionByFilterLoader loader = new PidcVersionByFilterLoader(getServiceData());

    Map<Long, PidcVersionInfo> retMap =
        loader.getPidcVersionInfo(userNtIdSet, accessType, pidcIdSet, pidcVersIdSet, pidcName, activeFlag);

    return Response.ok(retMap).build();
  }

  /**
   * Get active Pidc versions With Structure attributes, for the given other PIDC version IDs. If pidc id is not
   * provided, then all active versions are returned
   *
   * @param pidcVerIdSet Set of PIDC IDs
   * @return Rest response
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ACTIVE_VERS_WITH_STRUCT_BY_OTHER_VER_IDS)
  @CompressData
  public Response getActiveVersionsWithStructureByOtherVerIds(
      @QueryParam(value = WsCommonConstants.RWS_QP_PIDC_VERS_ID) final Set<Long> pidcVerIdSet)
      throws IcdmException {

    ServiceData serviceData = getServiceData();

    Map<Long, PidcVersionInfo> retMap;
    PidcVersionLoader pidcVersLdr = new PidcVersionLoader(serviceData);

    if (CommonUtils.isNullOrEmpty(pidcVerIdSet)) {
      retMap = pidcVersLdr.getAllActiveVersionsWithStructureAttributes();
    }
    else {
      Set<Long> pidcIdSet = new HashSet<>();

      for (Long pidcVerId : pidcVerIdSet) {
        pidcIdSet.add(pidcVersLdr.getDataObjectByID(pidcVerId).getPidcId());
      }
      // Active versions
      retMap = pidcVersLdr.getActiveVersionWithStructureAttributes(pidcIdSet);
    }

    getLogger().info("PidcVersionService.getActiveVersionsWithStructure() finished. Item count = {}, Input = {}",
        retMap.size(), pidcVerIdSet);

    return Response.ok(retMap).build();
  }

  /**
   * Get the PidcVersion using id
   *
   * @param pidcVersionId pidcVersionId
   * @return PidcVersion
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response getById(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long pidcVersionId)
      throws IcdmException {
    ServiceData serviceData = getServiceData();

    // Create loader object
    PidcVersion ret = new PidcVersionLoader(serviceData).getDataObjectByID(pidcVersionId);
    getLogger().info("PidcVersionService.getById() completed for the pidcVersionId {}", pidcVersionId);

    return Response.ok(ret).build();

  }

  /**
   * get pidc det structure details for pidc version id
   *
   * @param pidcVersionId - pidc version id
   * @return Rest response
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  @Path(WsCommonConstants.RWS_PIDC_DET_STRUCTURE_VERS)
  public Response getPidcDetStruct(@QueryParam(value = WsCommonConstants.RWS_PIDC_VERS) final Long pidcVersionId)
      throws IcdmException {

    Map<Long, PidcDetStructure> retMap = new PidcVersionLoader(getServiceData()).getPidcDetStructure(pidcVersionId);
    getLogger().info("PidcVersionService.getPidcDetStruct() completed. Resp size = {}", retMap.size());

    return Response.ok(retMap).build();
  }

  /**
   * @param pidcId pidcId
   * @return response
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  @Path(WsCommonConstants.RWS_GET_ALL_PIDC_VERSIONS_FOR_PIDC)

  public Response getAllPidcVersionForPidc(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long pidcId)
      throws IcdmException {

    Map<Long, PidcVersion> pidcVerMap = new PidcVersionLoader(getServiceData()).getAllPidcVersions(pidcId);
    getLogger().info("PidcVersionService.getAllPidcVersionForPidc() completed. Resp size = {}", pidcVerMap.size());

    return Response.ok(pidcVerMap).build();
  }

  /**
   * Service for fetching all non-active versions for a pidc
   *
   * @param pidcId pidc id for which all versions need to be fetched
   * @return response
   * @throws IcdmException web service exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_NONACTIVE_PIDC_VERSIONS)
  @CompressData
  public Response getOtherVersionsForPidc(@QueryParam(value = WsCommonConstants.RWS_QP_PIDC_ID) final Long pidcId)
      throws IcdmException {
    PidcVersionLoader pidcVerLoader = new PidcVersionLoader(getServiceData());
    Map<Long, PidcVersion> retMap = pidcVerLoader.getAllPidcVersions(pidcId);
    retMap.remove(pidcVerLoader.getActivePidcVersion(pidcId).getId());
    return Response.ok(retMap).build();
  }

  /**
   * get pidc version with details for pidc version id
   *
   * @param input PidcVersDtlsLoaderInput
   * @param quotationRelevantFlag QuotationRelevantFlag
   * @param usecaseGroupIds UsecaseGroupIds
   * @return Rest response
   * @throws IcdmException exception in service
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  @Path(WsCommonConstants.RWS_PIDC_VERS_WITH_DETAILS)
  public Response getPidcVersionWithDetails(final PidcVersionWithDetailsInput input,
      @QueryParam(value = WsCommonConstants.RWS_QP_QUOTATION_RELEVANT_FLAG) final String quotationRelevantFlag,
      @QueryParam(value = WsCommonConstants.RWS_QP_USE_CASE_GROUP_IDS) final Set<Long> usecaseGroupIds)
      throws IcdmException {
    PidcVersionWithDetails pidcVersionWithDetails = new PidcVersionLoader(getServiceData())
        .getPidcVersionWithDetails(input, quotationRelevantFlag, usecaseGroupIds);
    getLogger().info("PidcVersionAttributeMap size = {}", pidcVersionWithDetails.getPidcVersionAttributeMap().size());
    return Response.ok(pidcVersionWithDetails).build();
  }

  /**
   * Get active PIDC version for the given PIDC ID
   *
   * @param pidcId PIDC ID
   * @return response PIDC Version object for the active version
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  @Path(WsCommonConstants.RWS_ACTIVE_PIDC_VERSION)
  public Response getActivePidcVersion(@QueryParam(value = WsCommonConstants.RWS_QP_PIDC_ID) final Long pidcId)
      throws IcdmException {

    PidcVersionLoader loader = new PidcVersionLoader(getServiceData());
    PidcVersion ret = loader.getActivePidcVersion(pidcId);
    return Response.ok(ret).build();
  }

  /**
   * @param selAPRJName String
   * @return response
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  @Path(WsCommonConstants.RWS_APRJ_PIDCS)
  public Response getAprjPIDCs(@QueryParam(value = WsCommonConstants.RWS_QP_APRJ_NAME) final String selAPRJName)
      throws IcdmException {
    PidcVersionLoader loader = new PidcVersionLoader(getServiceData());
    SortedSet<PidcVersion> ret = loader.getAprjPIDCs(selAPRJName);
    getLogger().info("PidcVersionService.getAprjPIDCs() completed. Number of PidcVersion = {}", ret.size());
    return Response.ok(ret).build();
  }

  /**
   * Create a new pidc version record
   *
   * @param newPidcVer input
   * @return Rest response, with created pidc version object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response createNewPidcVer(final PidcVersion newPidcVer) throws IcdmException {
    PidcVersionCommand pidcVerCmd = new PidcVersionCommand(getServiceData(), newPidcVer, true, true, false);
    executeCommand(pidcVerCmd);
    return Response.ok(pidcVerCmd.getNewData()).build();
  }

  /**
   * New revision from an existing version
   *
   * @param pidcVersion reference input
   * @return new pidcVersion created
   * @throws IcdmException service error
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_CREATE_NEW_REVISION)
  @CompressData
  public Response createNewRevisionForPidcVers(final PidcVersion pidcVersion) throws IcdmException {
    PidcVersionCommand pidcVerCmd = new PidcVersionCommand(getServiceData(), pidcVersion, false, true, true);
    executeCommand(pidcVerCmd);
    return Response.ok(pidcVerCmd.getNewData()).build();
  }


  /**
   * Edit pidc version
   *
   * @param pidcVer pidc version to be edited
   * @return Rest response, with created pidc version object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response editPidcVer(final PidcVersion pidcVer) throws IcdmException {
    PidcVersionCommand pidcVerCmd = new PidcVersionCommand(getServiceData(), pidcVer, true, false);
    executeCommand(pidcVerCmd);
    return Response.ok(pidcVerCmd.getNewData()).build();
  }

  /**
   * get pidc version details required for compli review using hex file
   *
   * @param pidcVersionId - pidc version id
   * @return Rest response
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  @Path(WsCommonConstants.COMPLI_PIDC_VERS_WITH_DETAILS)
  public Response getPidcVersDetailsForCompliHex(
      @QueryParam(value = WsCommonConstants.RWS_PIDC_VERS) final Long pidcVersionId)
      throws IcdmException {

    CompliReviewUsingHexData inputData = new CompliReviewUsingHexData();

    inputData.setPidcSdomPverSet(new PidcSdomPverLoader(getServiceData()).getSdomPverNamesByPidcVers(pidcVersionId));

    PidcVariantLoader pidcVarLoader = new PidcVariantLoader(getServiceData());
    Set<PidcVariant> pidcVarSet = new HashSet<>();
    pidcVarSet.addAll(pidcVarLoader.getVariants(pidcVersionId, false).values());
    inputData.setPidcVariants(pidcVarSet);

    PidcVersionLoader pidcVerLoader = new PidcVersionLoader(getServiceData());
    PidcVersion pidcVersion = pidcVerLoader.getDataObjectByID(pidcVersionId);
    Pidc pidc = new PidcLoader(getServiceData()).getDataObjectByID(pidcVersion.getPidcId());
    inputData.setPidcVersion(pidcVersion);
    inputData.setSelPidc(pidc);

    ProjectAttributeLoader pidcAttrLoader = new ProjectAttributeLoader(getServiceData());
    PidcVersionAttributeModel attrModel = pidcAttrLoader.createModel(pidcVersionId, LOAD_LEVEL.L1_PROJ_ATTRS);
    Collection<PidcVersionAttribute> pidcAttrs = attrModel.getPidcVersAttrMap().values();
    boolean isValueSet = false;
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    for (PidcVersionAttribute pidcAttr : pidcAttrs) {

      if ((attrLoader.getDataObjectByID(pidcAttr.getAttrId()).getLevel() == VCDM_APRJ_NAME_ATTR) &&
          (pidcAttr.getValueId() != null)) {
        isValueSet = true;
      }
    }
    inputData.setVcdmAprjValSet(isValueSet);
    return Response.ok(inputData).build();
  }


  /**
   * @param pidcVrsnId pidcversion id
   * @return boolean
   * @throws IcdmException exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_QNAIRE_CONFIG_VAL_ID)
  @CompressData
  public Response isQnaireConfigAttrUsedInReview(
      @QueryParam(value = WsCommonConstants.RWS_PIDC_VERS) final Long pidcVrsnId)
      throws IcdmException {
    return Response.ok(new AttributeValueLoader(getServiceData()).isQnaireConfigValUsedInRvw(pidcVrsnId)).build();
  }

  /**
   * Validate if Pidc Version has CoC Work packages for iCDM Qnaire Config attribute value
   *
   * @param pidcVersionId - PIDC Version ID
   * @return Boolean = TRUE if CoC WPs are mapped, else FALSE
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_COC_WP_AVAILABLE)
  @CompressData
  public Response isCoCWPMappingAvailForPidcVersion(
      @QueryParam(WsCommonConstants.RWS_PIDC_VERS) final Long pidcVersionId)
      throws IcdmException {

    PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());

    pidcVersionLoader.validateId(pidcVersionId);

    TPidcVersion tPidcVersion = pidcVersionLoader.getEntityObject(pidcVersionId);

    Boolean isCocWpAvailable = CommonUtils.isNotEmpty(tPidcVersion.gettPidcVersCocWp());

    return Response.ok(isCocWpAvailable).build();
  }

  /**
   * @return {@link ProjectIdCardAllVersInfoType} List
   * @throws IcdmException Exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ALL_PIDC_VERSIONS)
  @CompressData
  public Response getAllProjectIdCardVersion() throws IcdmException {
    PidcVersionLoader loader = new PidcVersionLoader(getServiceData());
    return Response.ok(loader.getAllPidcVersions()).build();
  }

  /**
   * @return {@link PidcVersionStatisticsReport} List
   * @throws IcdmException Exception
   * @throws ParseException Exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_PIDC_VERSIONS_STATISTICS)
  @CompressData
  public Response getPidcVersionStatisticsReport(
      @QueryParam(value = WsCommonConstants.RWS_QP_PIDC_ID) final Long pidcId,
      @QueryParam(value = WsCommonConstants.RWS_QP_PARAM_TYPE) final String paramType)
      throws IcdmException, ParseException {
    PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());
    PidcVersion version = null;
    // pidcId can be either PIDC ID or PIDC VersionID depending on the paramType
    if ("pidc".equalsIgnoreCase(paramType)) {
      version = pidcVersionLoader.getActivePidcVersion(pidcId);
    }
    else if ("pidcVersion".equalsIgnoreCase(paramType)) {
      version = pidcVersionLoader.getDataObjectByID(pidcId);
    }
    else {
      throw new IcdmException("The type should be either pidc or pidcVersion");
    }
    PidcVersionStatisticsReportLoader loader = new PidcVersionStatisticsReportLoader(getServiceData(), version);
    PidcVersionStatisticsReport pidcVersStatisticsReport = loader.createStatResponse();
    return Response.ok(pidcVersStatisticsReport).build();
  }

  /**
   * @param pidcVariantId PIDC Variant ID
   * @return {@link PidcVersionStatisticsReport} List
   * @throws IcdmException Exception
   * @throws ParseException Exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_PIDC_VARIANT_STATISTICS)
  @CompressData
  public Response getPidcVariantStatisticsReport(
      @QueryParam(value = WsCommonConstants.RWS_QP_PIDC_VARIANT_ID) final Long pidcVariantId)
      throws IcdmException, ParseException {
    PidcVariantLoader pidcVariantLoader = new PidcVariantLoader(getServiceData());
    PidcVariant pidcVariant = pidcVariantLoader.getDataObjectByID(pidcVariantId);
    PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());
    PidcVersion pidcVersion = pidcVersionLoader.getDataObjectByID(pidcVariant.getPidcVersionId());
    PidcVersionStatisticsReportLoader loader = new PidcVersionStatisticsReportLoader(getServiceData(), pidcVersion);

    PidcVersionStatisticsReport pidcVarStatisticsReport = loader.createVarStatResponse(pidcVariant);

    return Response.ok(pidcVarStatisticsReport).build();
  }


}
