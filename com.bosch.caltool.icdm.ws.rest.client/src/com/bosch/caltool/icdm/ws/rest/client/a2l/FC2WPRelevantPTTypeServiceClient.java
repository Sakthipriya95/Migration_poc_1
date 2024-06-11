/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPRelvPTType;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dmo5cob
 */
public class FC2WPRelevantPTTypeServiceClient extends AbstractRestServiceClient {

  /**
   * Service client for FC2WPDefinitionService
   */
  public FC2WPRelevantPTTypeServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_RELEVANT_PT_TYPE);
  }

  /**
   * Fetch the relevant PT types for an FC2WPDefinition
   *
   * @param fc2wpDefID FC2WP defenition id
   * @return data
   * @throws ApicWebServiceException error during service call
   */
  public Set<FC2WPRelvPTType> getRelevantPTTypes(final Long fc2wpDefID) throws ApicWebServiceException {
    LOGGER.debug("Loading relevant PT types for FC2WP definition {}", fc2wpDefID);

    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_FC2WP_DEF_ID, fc2wpDefID);

    GenericType<Set<FC2WPRelvPTType>> type = new GenericType<Set<FC2WPRelvPTType>>() {};

    Set<FC2WPRelvPTType> response = get(wsTarget, type);
    LOGGER.debug("FC2WP relevant PT types loaded. No. of types : {}", response.size());

    return response;
  }


  /**
   * @param def new definition details
   * @return definition details from service
   * @throws ApicWebServiceException any exception
   */
  public FC2WPRelvPTType create(final FC2WPRelvPTType def) throws ApicWebServiceException {
    return create(getWsBase(), def, FC2WPRelvPTType.class);
  }

  /**
   * @param def new definition details
   * @throws ApicWebServiceException any exception
   */
  public void delete(final FC2WPRelvPTType def) throws ApicWebServiceException {
    delete(getWsBase(), def);
  }
}
