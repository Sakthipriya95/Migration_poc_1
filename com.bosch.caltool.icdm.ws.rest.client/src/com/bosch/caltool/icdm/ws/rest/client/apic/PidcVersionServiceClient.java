/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.pidc.ExternalPidcVersionInfo;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDetStructure;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionInfo;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionStatisticsReport;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetails;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetailsInput;
import com.bosch.caltool.icdm.model.apic.pidc.ProjectIdCardAllVersInfoType;
import com.bosch.caltool.icdm.model.cdr.CompliReviewUsingHexData;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.util.Utils;


/**
 * @author bne4cob
 */
public class PidcVersionServiceClient extends AbstractRestServiceClient {

  private static final String PIDC_ACTIVE_VERSIONS_MAP_LOADED_MSG =
      "PIDCActiveVersionsWithStructure map loaded. No. of records : {}";

  /**
   * Constructor
   */
  public PidcVersionServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_PIDC_VERS);
  }

  /**
   * @return Map of all active pidc versions with structure attribute details. Key - pidc version ID, Value - data
   * @throws ApicWebServiceException error during service call
   */
  public Map<Long, PidcVersionInfo> getAllActiveVersionsWithStructure() throws ApicWebServiceException {
    LOGGER.debug("Loading AllActiveVersionsWithStructure map ");

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_PIDC_VERS_GET_ACTIVE_VERS_WITH_STRUCT_ATTRS);

    GenericType<Map<Long, PidcVersionInfo>> type = new GenericType<Map<Long, PidcVersionInfo>>() {};

    Map<Long, PidcVersionInfo> retMap = get(wsTarget, type);

    LOGGER.debug("AllActiveVersionsWithStructure map loaded. No. of records : {}", retMap.size());

    return retMap;
  }

  /**
   * Get active Pidc versions With Structure attributes, for the given PIDC IDs. If pidc id is not provided, then all
   * active versions are returned
   *
   * @param pidcIdSet set of pidc Id
   * @return Key - pidc version ID, Value - PidcVersionInfo
   * @throws ApicWebServiceException error during service call
   */
  public Map<Long, PidcVersionInfo> getActiveVersionsWithStructure(final Set<Long> pidcIdSet)
      throws ApicWebServiceException {
    LOGGER.debug("Loading PIDCActiveVersionsWithStructure map. Input PIDC Ids = {}", pidcIdSet);

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_PIDC_VERS_GET_ACTIVE_VERS_WITH_STRUCT_ATTRS);

    for (Long pidcId : pidcIdSet) {
      wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_PIDC_ID, pidcId);
    }

    GenericType<Map<Long, PidcVersionInfo>> type = new GenericType<Map<Long, PidcVersionInfo>>() {};

    Map<Long, PidcVersionInfo> retMap = get(wsTarget, type);

    LOGGER.debug(PIDC_ACTIVE_VERSIONS_MAP_LOADED_MSG, retMap.size());

    return retMap;
  }

  /**
   * Get active External Pidc versions With Structure attributes, for the given PIDC IDs. If pidc id is not provided,
   * then all active versions are returned
   *
   * @param pidcIdSet set of pidc Id
   * @return Key - pidc version ID, Value - PidcVersionInfo
   * @throws ApicWebServiceException error during service call
   */
  public Map<Long, ExternalPidcVersionInfo> getActiveExternalVersionsWithStructure(final Set<Long> pidcIdSet)
      throws ApicWebServiceException {
    LOGGER.debug("Loading PIDCActiveVersionsWithStructure map. Input PIDC Ids = {}", pidcIdSet);

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_EXT_PIDC_VERS_GET_ACTIVE_VERS_WITH_STRUCT_ATTRS);

    for (Long pidcId : pidcIdSet) {
      wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_PIDC_ID, pidcId);
    }

    GenericType<Map<Long, ExternalPidcVersionInfo>> type = new GenericType<Map<Long, ExternalPidcVersionInfo>>() {};

    Map<Long, ExternalPidcVersionInfo> retMap = get(wsTarget, type);

    LOGGER.debug(PIDC_ACTIVE_VERSIONS_MAP_LOADED_MSG, retMap.size());

    return retMap;
  }

  /**
   * Get Pidc versions With Structure attributes, for the given PIDC version IDs.
   *
   * @param pidcVersIdSet set of pidc version Id
   * @return Key - pidc version ID, Value - PidcVersionInfo
   * @throws ApicWebServiceException error during service call
   */
  public Map<Long, PidcVersionInfo> getPidcVersionsWithStructure(final Set<Long> pidcVersIdSet)
      throws ApicWebServiceException {
    return getPidcVersionsWithStructure(null, null, null, pidcVersIdSet, null, null);
  }


  /**
   * Get Pidc versions With Structure attributes, for the given PIDC version IDs.
   *
   * @param pidcVersIdSet set of pidc version Id
   * @param pidcIdSet set of pidcId
   * @param userNtIdSet set of username
   * @param accessType string specify the user acess
   * @param pidcName name of pidc
   * @param activeFlag specify the flag
   * @return Value - PidcVersionInfo
   * @throws ApicWebServiceException error during service call
   */
  public Map<Long, PidcVersionInfo> getPidcVersionsWithStructure(final Set<String> userNtIdSet, final String accessType,
      final Set<Long> pidcIdSet, final Set<Long> pidcVersIdSet, final String pidcName, final String activeFlag)
      throws ApicWebServiceException {

    LOGGER.debug(
        "Loading getPidcVersionsWithStructure map. userNtIdSet={}, accessType={}, pidcIdSet={}, pidcVersIdSet={}, pidcName={}, activeFlag={}",
        userNtIdSet, accessType, pidcIdSet, pidcVersIdSet, pidcName, activeFlag);

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_PIDC_VERS_GET_VERS_WITH_STRUCT_ATTRS);
    if (pidcVersIdSet != null) {
      for (Long pidcVersId : pidcVersIdSet) {
        wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ID, pidcVersId);
      }
    }
    if (pidcIdSet != null) {
      for (Long pidcId : pidcIdSet) {
        wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_PIDC_ID, pidcId);
      }
    }
    if (userNtIdSet != null) {
      for (String username : userNtIdSet) {
        wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_USERNAME, username);
      }
    }
    if (!Utils.isEmptyString(pidcName)) {
      wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_PIDC_NAME, pidcName);
    }
    if (!Utils.isEmptyString(activeFlag)) {
      wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_ACTIVEFLAG, activeFlag);
    }
    if (!Utils.isEmptyString(accessType)) {
      wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_ACCESS_TYPE, accessType);
    }

    GenericType<Map<Long, PidcVersionInfo>> type = new GenericType<Map<Long, PidcVersionInfo>>() {};

    Map<Long, PidcVersionInfo> retMap = get(wsTarget, type);

    LOGGER.debug("PIDCVersionsWithStructure map loaded. No. of records : {}", retMap.size());

    return retMap;
  }

  /**
   * Get PidcVersion by using Id
   *
   * @param pidcVersionId pidcversionid
   * @return PidcVersion
   * @throws ApicWebServiceException error during service call
   */
  public PidcVersion getById(final Long pidcVersionId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, pidcVersionId);

    return get(wsTarget, PidcVersion.class);
  }

  /**
   * get pidc version details for pidc version id
   *
   * @param pidcVersionId - pidc version id
   * @return Map. Key - PredefinedValidity Id; value - PredefinedValidity object
   * @throws ApicWebServiceException error while retrieving data
   */
  public Map<Long, PidcDetStructure> getPidcDetStructure(final Long pidcVersionId) throws ApicWebServiceException {
    LOGGER.debug("Get PidcDetStructure for PidcVersion {}", pidcVersionId);

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_PIDC_DET_STRUCTURE_VERS)
        .queryParam(WsCommonConstants.RWS_PIDC_VERS, pidcVersionId);
    GenericType<Map<Long, PidcDetStructure>> type = new GenericType<Map<Long, PidcDetStructure>>() {};

    return get(wsTarget, type);
  }

  /**
   * @param pidcId
   * @return
   * @throws ApicWebServiceException
   */
  public Map<Long, PidcVersion> getAllPidcVersionForPidc(final Long pidcId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL_PIDC_VERSIONS_FOR_PIDC)
        .queryParam(WsCommonConstants.RWS_QP_OBJ_ID, pidcId);
    GenericType<Map<Long, PidcVersion>> type = new GenericType<Map<Long, PidcVersion>>() {};

    return get(wsTarget, type);
  }

  /**
   * Fetch non-active pidc versions for a pidc
   *
   * @param pidcId pidc id for which all versions need to be fetched
   * @return Map of non-active Pidc versions of the input pidc ; key - pidc ver id, value - PidcVersion
   * @throws ApicWebServiceException service exception
   */
  public Map<Long, PidcVersion> getOtherVersionsForPidc(final Long pidcId) throws ApicWebServiceException {
    LOGGER.debug("Started Fetching all pidc versions for a Pidc {}", pidcId);

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_NONACTIVE_PIDC_VERSIONS)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_ID, pidcId);

    GenericType<Map<Long, PidcVersion>> type = new GenericType<Map<Long, PidcVersion>>() {};

    Map<Long, PidcVersion> retMap = get(wsTarget, type);
    LOGGER.debug("Fetching non active pidc versions for a Pidc completed. count = {}", retMap.size());

    return retMap;
  }

  /**
   * get pidc version with details for pidc version id
   *
   * @param pidcVersionId - pidc version id
   * @return PidcVersionWithDetails
   * @throws ApicWebServiceException error while retrieving data
   */
  public PidcVersionWithDetails getPidcVersionWithDetails(final Long pidcVersionId) throws ApicWebServiceException {
    PidcVersionWithDetailsInput input = new PidcVersionWithDetailsInput();
    input.setPidcVersionId(pidcVersionId);
    return getPidcVersionWithDetails(input);
  }

  /**
   * @param input PidcVersDtlsLoaderInput
   * @return PidcVersionWithDetails
   * @throws ApicWebServiceException Exception during service call
   */
  public PidcVersionWithDetails getPidcVersionWithDetails(final PidcVersionWithDetailsInput input)
      throws ApicWebServiceException {
    LOGGER.debug("post getPidcVersionWithDetails for PidcVersion {}", input.getPidcVersionId());
//    Query param: RWS_QP_QUOTATION_RELEVANT_FLAG=ALL and RWS_QP_USE_CASE_GROUP_IDS=null (optional to add since its value is null)
//    is used to not filter the response attributes based on the usecase group ids or quotation relevant flag (optional filters for external API)
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_PIDC_VERS_WITH_DETAILS)
        .queryParam(WsCommonConstants.RWS_QP_QUOTATION_RELEVANT_FLAG, "ALL");
    GenericType<PidcVersionWithDetails> type = new GenericType<PidcVersionWithDetails>() {};
    return post(wsTarget, input, type);
  }

  /**
   * get pidc version details required for compli review using hex file
   *
   * @param pidcVersionId - pidc version id
   * @return PidcVersionWithDetails
   * @throws ApicWebServiceException error while retrieving data
   */
  public CompliReviewUsingHexData getPidcVersDetailsForCompliHex(final Long pidcVersionId)
      throws ApicWebServiceException {
    LOGGER.debug("Get getPidcVersionWithDetails for PidcVersion {}", pidcVersionId);

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.COMPLI_PIDC_VERS_WITH_DETAILS)
        .queryParam(WsCommonConstants.RWS_PIDC_VERS, pidcVersionId);
    GenericType<CompliReviewUsingHexData> type = new GenericType<CompliReviewUsingHexData>() {};

    return get(wsTarget, type);
  }

  /**
   * @param pidcId project id
   * @return active pidcversion
   * @throws ApicWebServiceException any exception
   */
  public PidcVersion getActivePidcVersion(final Long pidcId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_ACTIVE_PIDC_VERSION)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_ID, pidcId);

    return get(wsTarget, PidcVersion.class);
  }

  /**
   * @param selAPRJName String
   * @return Set<PidcVersion>
   * @throws ApicWebServiceException error while retrieving data
   */
  public SortedSet<PidcVersion> getAprjPIDCs(final String selAPRJName) throws ApicWebServiceException {
    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_APRJ_PIDCS).queryParam(WsCommonConstants.RWS_QP_APRJ_NAME, selAPRJName);
    GenericType<SortedSet<PidcVersion>> pidcVersionSet = new GenericType<SortedSet<PidcVersion>>() {};
    return get(wsTarget, pidcVersionSet);

  }

  /**
   * @param pidcVersion new pidc ver
   * @return PidcVersion
   * @throws ApicWebServiceException any exception
   */
  public PidcVersion createNewPidcVersion(final PidcVersion pidcVersion) throws ApicWebServiceException {
    return create(getWsBase(), pidcVersion);
  }

  /**
   * @param pidcVersion
   * @return
   * @throws ApicWebServiceException
   */
  public PidcVersion createNewRevisionForPidcVers(final PidcVersion pidcVersion) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_CREATE_NEW_REVISION);

    return create(wsTarget, pidcVersion, PidcVersion.class);
  }

  /**
   * @param pidcVersion pidc ver
   * @return PidcVersion
   * @throws ApicWebServiceException any exception
   */
  public PidcVersion editPidcVersion(final PidcVersion pidcVersion) throws ApicWebServiceException {
    return update(getWsBase(), pidcVersion);
  }

  /**
   * @param pidcVersionId pidc ver id
   * @return Boolean
   * @throws ApicWebServiceException exception
   */
  public Boolean isQnaireConfigAttrUsedInReview(final Long pidcVersionId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_QNAIRE_CONFIG_VAL_ID)
        .queryParam(WsCommonConstants.RWS_PIDC_VERS, pidcVersionId);

    return get(wsTarget, Boolean.class);
  }

  /**
   * Validate if Pidc Version has CoC Work packages for iCDM Qnaire Config attribute value
   *
   * @param pidcVersionId - PIDC Version ID
   * @return Boolean = TRUE if CoC WPs are mapped, else FALSE
   * @throws ApicWebServiceException exception while invoking service
   */
  public Boolean isCoCWPMappingAvailForPidcVersion(final Long pidcVersionId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_COC_WP_AVAILABLE)
        .queryParam(WsCommonConstants.RWS_PIDC_VERS, pidcVersionId);
    return get(wsTarget, Boolean.class);
  }


  /**
   * @param pidcVerIdSet
   * @return
   * @throws ApicWebServiceException
   */
  public Map<Long, PidcVersionInfo> getActiveVersWithStrByOtherVerId(final Set<Long> pidcVerIdSet)
      throws ApicWebServiceException {
    LOGGER.debug("Loading PIDCActiveVersionsWithStructure map. Input PIDC version Ids = {}", pidcVerIdSet);

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ACTIVE_VERS_WITH_STRUCT_BY_OTHER_VER_IDS);

    for (Long pidcId : pidcVerIdSet) {
      wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ID, pidcId);
    }

    GenericType<Map<Long, PidcVersionInfo>> type = new GenericType<Map<Long, PidcVersionInfo>>() {};

    Map<Long, PidcVersionInfo> retMap = get(wsTarget, type);

    LOGGER.debug(PIDC_ACTIVE_VERSIONS_MAP_LOADED_MSG, retMap.size());

    return retMap;
  }

  /**
   * @return {@link ProjectIdCardAllVersInfoType} List
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public List<ProjectIdCardAllVersInfoType> getAllProjectIdCardVersion() throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL_PIDC_VERSIONS);
    GenericType<List<ProjectIdCardAllVersInfoType>> type = new GenericType<List<ProjectIdCardAllVersInfoType>>() {};

    return get(wsTarget, type);
  }

  /**
   * @param pidcId PIDC ID
   * @param paramType Param Type (pidc or pidcVersion)
   * @return {@link ProjectIdCardAllVersInfoType} List
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public PidcVersionStatisticsReport getPidcVersionStatisticsReport(final Long pidcId, final String paramType)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_PIDC_VERSIONS_STATISTICS)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_ID, pidcId)
        .queryParam(WsCommonConstants.RWS_QP_PARAM_TYPE, paramType);
    GenericType<PidcVersionStatisticsReport> type = new GenericType<PidcVersionStatisticsReport>() {};

    return get(wsTarget, type);
  }

  /**
   * @param pidcVariantId PIDC Variant ID
   * @return {@link ProjectIdCardAllVersInfoType} List
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public PidcVersionStatisticsReport getPidcVariantStatisticsReport(final Long pidcVariantId)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_PIDC_VARIANT_STATISTICS)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_VARIANT_ID, pidcVariantId);
    GenericType<PidcVersionStatisticsReport> type = new GenericType<PidcVersionStatisticsReport>() {};

    return get(wsTarget, type);
  }
}
