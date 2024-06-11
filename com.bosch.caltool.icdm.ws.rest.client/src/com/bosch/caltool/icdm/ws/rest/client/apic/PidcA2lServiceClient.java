/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.a2l.A2lWpMapping;
import com.bosch.caltool.icdm.model.apic.PidcA2lDetails;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2lFileExt;
import com.bosch.caltool.icdm.model.cdr.SSDProjectVersion;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * The Class PidcA2lServiceClient.
 *
 * @author gge6cob
 */
public class PidcA2lServiceClient extends AbstractRestServiceClient {

  /**
   * Service client for PidcA2lService.
   */
  public PidcA2lServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_CONTEXT_A2L);
  }

  /**
   * Gets the all pidc A2l by PidcVersionId.
   *
   * @param pidcVersionId pidcVersion id
   * @param sdomPverName
   * @return Map of PidcA2lFileExt
   * @throws ApicWebServiceException error during service call
   */
  public Map<Long, PidcA2l> getA2LFileBySdom(final Long pidcVersionId, final String sdomPverName)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_PIDC_A2L_BY_PIDC_VERS_ID)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ID, pidcVersionId)
        .queryParam(WsCommonConstants.RWS_QP_SDOM_PVER_NAME, sdomPverName);

    GenericType<Map<Long, PidcA2l>> type = new GenericType<Map<Long, PidcA2l>>() {};
    return get(wsTarget, type);
  }

  /**
   * Get PidcA2l using its id
   *
   * @param objId object's id
   * @return PidcA2l object
   * @throws ApicWebServiceException exception while invoking service
   */
  public PidcA2l getById(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_QP_OBJ_ID).queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, PidcA2l.class);
  }


  /**
   * @param pidcId pidcId
   * @return Map of A2l
   * @throws ApicWebServiceException error during web service call
   */
  public Map<Long, PidcA2lFileExt> getAllA2lByPidc(final Long pidcId) throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL_A2L_BY_PIDC_ID)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_ID, pidcId);

    GenericType<Map<Long, PidcA2lFileExt>> type = new GenericType<Map<Long, PidcA2lFileExt>>() {};

    return get(wsTarget, type);

  }

  /**
   * Gets pidc A2l availabilty by PidcVersionId.
   *
   * @param pidcVersionId pidcVersion id
   * @param sdomPverName
   * @return Map of PidcA2L
   * @throws ApicWebServiceException error during service call
   */
  public boolean isPidcA2lPresent(final Long pidcVersionId, final String sdomPverName) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_PIDC_A2L_AVAILBLTY)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ID, pidcVersionId)
        .queryParam(WsCommonConstants.RWS_QP_SDOM_PVER_NAME, sdomPverName);
    return get(wsTarget, boolean.class);
  }

  /**
   * Gets the pidc A2L file details.
   *
   * @param pidcA2lId the pidc A2l id
   * @return the pidc A2L file details
   * @throws ApicWebServiceException the apic web service exception
   */
  public PidcA2lFileExt getPidcA2LFileDetails(final Long pidcA2lId) throws ApicWebServiceException {
    return get(getWsBase().queryParam(WsCommonConstants.RWS_QP_PIDC_A2L_ID, pidcA2lId), PidcA2lFileExt.class);
  }


  /**
   * Gets the a 2 l workpackage mapping.
   *
   * @param pidcA2lId the pidc A 2 l id
   * @return the a 2 l workpackage mapping
   * @throws ApicWebServiceException the apic web service exception
   * @deprecated not used
   */
  @Deprecated
  public A2lWpMapping getA2lWorkpackageMapping(final Long pidcA2lId) throws ApicWebServiceException {
    return get(getWsBase().path(WsCommonConstants.RWS_GET_A2L_WP_MAPPING)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_A2L_ID, pidcA2lId), A2lWpMapping.class);
  }


  /**
   * Resolve A 2 l wp resp.
   *
   * @param pidcA2lId the pidc A 2 l id
   * @return the a 2 l wp mapping
   * @throws ApicWebServiceException the apic web service exception
   * @deprecated not used
   */
  @Deprecated
  public A2lWpMapping resolveA2lWpResp(final Long pidcA2lId) throws ApicWebServiceException {
    return get(getWsBase().path(WsCommonConstants.RWS_RESOLVE_A2L_WP_RESP)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_A2L_ID, pidcA2lId), A2lWpMapping.class);
  }

  /**
   * @param pidcA2ls set of PidcA2l to create
   * @return the created PidcA2l
   * @throws ApicWebServiceException error during web service call
   */
  public Set<PidcA2l> create(final Set<PidcA2l> pidcA2ls) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase();
    GenericType<Set<PidcA2l>> type = new GenericType<Set<PidcA2l>>() {};
    Set<PidcA2l> retSet = create(wsTarget, pidcA2ls, type);
    LOGGER.debug("A2l files have been assigned to Pidcversion. No of files assigned : " + retSet.size());
    return retSet;
  }

  /**
   * @param pidcA2ls set of PidcA2l to update
   * @return the updated PidcA2l
   * @throws ApicWebServiceException error during web service call
   */
  public Set<PidcA2l> update(final Set<PidcA2l> pidcA2ls) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase();
    GenericType<Set<PidcA2l>> type = new GenericType<Set<PidcA2l>>() {};
    Set<PidcA2l> retSet = update(wsTarget, pidcA2ls, type);
    LOGGER.debug("pidcA2ls have been edited. No of pidcA2ls edited : " + retSet.size());
    return retSet;
  }


  /**
   * @param swProjNodeId SSD Project node id
   * @return
   * @throws ApicWebServiceException error during web service call
   */
  public SortedSet<SSDProjectVersion> getSSDServiceHandler(final Long swProjNodeId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_SSD_PROJECT_VERSIONS)
        .queryParam(WsCommonConstants.RWS_QP_SW_PROJ_NODE_ID, swProjNodeId);
    GenericType<SortedSet<SSDProjectVersion>> type = new GenericType<SortedSet<SSDProjectVersion>>() {};

    return get(wsTarget, type);
  }

  /**
   * @param pidcA2lId Selected pidc A2l Id
   * @return boolean Value
   * @throws ApicWebServiceException Exception
   */
  public boolean getPidcA2lAssignmentValidation(final Set<Long> pidcA2lId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_PIDC_A2L_ASSIGN_VALIDATION);
    return post(wsTarget, pidcA2lId, Boolean.class);
  }

  /**
   * @param aprjName aprj name
   * @param variantName pver variant name
   * @param vcdmA2lFileID vcdm a2l file id
   * @return a model to get pidc details
   * @throws ApicWebServiceException service error
   */
  public Map<Long, PidcA2lDetails> getPidcDetailsByAPRJInfo(final String aprjName, final String variantName,
      final Long vcdmA2lFileID)
      throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_PIDC_A2L_DETAILS_BY_APRJ)
        .queryParam(WsCommonConstants.RWS_QP_APRJ_NAME, aprjName)
        .queryParam(WsCommonConstants.RWS_QP_APRJ_VARIANT, variantName)
        .queryParam(WsCommonConstants.RWS_QP_VCDM_A2LFILE_ID, vcdmA2lFileID);
    GenericType<Map<Long, PidcA2lDetails>> type = new GenericType<Map<Long, PidcA2lDetails>>() {};
    Map<Long, PidcA2lDetails> retMap = get(wsTarget, type);

    LOGGER.info("PIDC A2L details fetched. No. of records fetched: {}", retMap.size());
    return retMap;
  }

}
