/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPDef;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author bne4cob
 */
public class FC2WPDefinitionServiceClient extends AbstractRestServiceClient {

  /**
   * Service client for FC2WPDefinitionService
   */
  public FC2WPDefinitionServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_FC2WP_DEF);
  }

  /**
   * Fetch FC to WP definitions
   *
   * @return data
   * @throws ApicWebServiceException error during service call
   */
  public Set<FC2WPDef> getAll() throws ApicWebServiceException {

    LOGGER.debug("Loading all FC2WP definitions");

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL);
    GenericType<Set<FC2WPDef>> type = new GenericType<Set<FC2WPDef>>() {};

    Set<FC2WPDef> response = get(wsTarget, type);

    LOGGER.debug("FC2WP definitions loaded. No. of definitions : {}", response.size());

    return response;

  }
 
  /**
   * Update an existing FC2WP Definition.
   *
   * @param def the def
   * @return data
   * @throws ApicWebServiceException error during service call
   */
  public FC2WPDef update(final FC2WPDef def) throws ApicWebServiceException {
    return post(getWsBase(), def, FC2WPDef.class);
  }

  /**
   * @param def new definition details
   * @return definition details from service
   * @throws ApicWebServiceException any exception
   */
  public FC2WPDef create(final FC2WPDef def) throws ApicWebServiceException {
    return put(getWsBase(), def, FC2WPDef.class);
  }
}
