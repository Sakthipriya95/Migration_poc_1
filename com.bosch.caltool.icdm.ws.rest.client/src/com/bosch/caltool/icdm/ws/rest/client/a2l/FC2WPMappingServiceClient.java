/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import java.util.HashSet;
import java.util.List;

import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.cdr.RvwFuncDetails;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMapping;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMappingWithDetails;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapper;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author bne4cob
 */
public class FC2WPMappingServiceClient extends AbstractRestServiceClient {

  
  private static final String FC2WP_MAPPING_LOADED_MSG = "FC2WP mapping loaded. No. of mappings : {}";
  
  private static final IMapper FC2WP_MAPPING_MAPPER =
      (data) -> new HashSet<>(((FC2WPMappingWithDetails) data).getFc2wpMappingMap().values());

  /**
   * Service client for FC2WPMapping
   */
  public FC2WPMappingServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_FC2WP_MAPPING);
  }

  /**
   * Fetch FC to WP mappings for the given FC to WP Definition Version
   *
   * @param fc2wpVersID version ID
   * @return data
   * @throws ApicWebServiceException error during service call
   */
  public FC2WPMappingWithDetails getFC2WPMappingByVersion(final Long fc2wpVersID) throws ApicWebServiceException {

    LOGGER.debug("Loading FC2WP mapping for version {}", fc2wpVersID);

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_FC2WP_MAPPING_BY_VERSION)
        .queryParam(WsCommonConstants.RWS_QP_FC2WP_VERS_ID, fc2wpVersID);

    FC2WPMappingWithDetails response = get(wsTarget, FC2WPMappingWithDetails.class);

    LOGGER.debug(FC2WP_MAPPING_LOADED_MSG, response.getFc2wpMappingMap().size());

    return response;
  }

  /**
   * Fetch FC to WP mapping details for the given ID
   *
   * @param fc2wpMappingID Mapping ID
   * @return data
   * @throws ApicWebServiceException error during service call
   */
  public FC2WPMappingWithDetails getFC2WPMappingById(final Long fc2wpMappingID) throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_FC2WP_MAPPING_BY_ID)
        .queryParam(WsCommonConstants.RWS_QP_FC2WP_MAPPING_ID, fc2wpMappingID);

    return get(wsTarget, FC2WPMappingWithDetails.class);
  }

  /**
   * Find FC to WP mappings for the given 'PIDC A2L ID'
   *
   * @param pidcA2LId PIDC A2L ID
   * @return FC2WPVersMapping
   * @throws ApicWebServiceException if active version of FC2WP not available
   */
  public FC2WPMappingWithDetails findByPidcA2lId(final Long pidcA2LId) throws ApicWebServiceException {

    LOGGER.debug("Loading FC2WP mapping for PidcA2lId {}", pidcA2LId);

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_FC2WP_MAPPING_BY_PIDC_A2L)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_A2L_ID, pidcA2LId);

    FC2WPMappingWithDetails response = get(wsTarget, FC2WPMappingWithDetails.class);

    LOGGER.debug(FC2WP_MAPPING_LOADED_MSG, response.getFc2wpMappingMap().size());

    return response;
  }

  /**
   * Find FC2WP mapping for the given A2L, FC2WP name and division
   *
   * @param a2lId a2l file Id
   * @param divValId Division ID
   * @param fc2wpName fc2wpname of FC2WP definition
   * @return FC2WPVersMapping
   * @throws ApicWebServiceException error from service call
   */
  public FC2WPMappingWithDetails findByA2lId(final Long a2lId, final Long divValId, final String fc2wpName)
      throws ApicWebServiceException {

    LOGGER.debug("Loading FC2WP mapping for A2L. a2lId = {}; divValId = {}; nameValId = {}", a2lId, divValId,
        fc2wpName);

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_FC2WP_MAPPING_BY_A2L_N_NAME_N_DIV)
        .queryParam(WsCommonConstants.RWS_QP_A2L_FILE_ID, a2lId)
        .queryParam(WsCommonConstants.RWS_QP_DIV_VALUE_ID, divValId)
        .queryParam(WsCommonConstants.RWS_QP_FC2_WP_NAME, fc2wpName);

    FC2WPMappingWithDetails response = get(wsTarget, FC2WPMappingWithDetails.class);

    LOGGER.debug(FC2WP_MAPPING_LOADED_MSG, response.getFc2wpMappingMap().size());

    return response;
  }

  /**
   * Update an existing FC2WP Mapping
   *
   * @param mapping version ID
   * @return data
   * @throws ApicWebServiceException error during service call
   */
  public FC2WPMappingWithDetails updateFC2WPMapping(final List<FC2WPMapping> mapping) throws ApicWebServiceException {
    return update(getWsBase(), mapping, FC2WPMappingWithDetails.class, FC2WPMappingServiceClient.FC2WP_MAPPING_MAPPER);
  }


  /**
   * @param rvwFuncDetails Model to hold Division Id & list of functions used in data review
   * @return FC2WPVersMapping
   * @throws ApicWebServiceException error from service call
   */
  public FC2WPMappingWithDetails getQFC2WPMappingByDivId(final RvwFuncDetails rvwFuncDetails)
      throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_Q_FC2WP_MAPPING_BY_DIV_ID);
    return post(wsTarget, rvwFuncDetails, FC2WPMappingWithDetails.class);
  }

  /**
   * @param newFc2WpMappingList list of fc2wpmapping to be create
   * @return FC2WPMappingWithDetails
   * @throws ApicWebServiceException error from service call
   */
  public FC2WPMappingWithDetails createFC2WPMapping(final List<FC2WPMapping> newFc2WpMappingList)
      throws ApicWebServiceException {

    return create(getWsBase(), newFc2WpMappingList, FC2WPMappingWithDetails.class,
        FC2WPMappingServiceClient.FC2WP_MAPPING_MAPPER);
  }

}
