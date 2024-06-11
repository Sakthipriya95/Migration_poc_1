/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPVersion;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author bne4cob
 */
public class FC2WPVersionServiceClient extends AbstractRestServiceClient {

  /**
   * Service client for FC2WPDefinitionService
   */
  public FC2WPVersionServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_FC2WP_VERS);
  }

  /**
   * Fetch all FC to WP versions of the given definition
   *
   * @param fc2wpDefID fc2wp Def ID
   * @return data
   * @throws ApicWebServiceException error during service call
   */
  public Set<FC2WPVersion> getVersionsByDefID(final Long fc2wpDefID) throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL_BY_DEF_ID)
        .queryParam(WsCommonConstants.RWS_QP_FC2WP_DEF_ID, fc2wpDefID);

    GenericType<Set<FC2WPVersion>> type = new GenericType<Set<FC2WPVersion>>() {};

    return get(wsTarget, type);

  }

  /**
   * Get the working set version of the given FC2WP definition
   *
   * @param fc2wpDefID FC to WP Definition ID
   * @return Working set FC2WP Version
   * @throws ApicWebServiceException failure of WS
   */

  public FC2WPVersion getWorkingSetVersionByDefID(final Long fc2wpDefID) throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_WS_VERS_BY_DEF_ID)
        .queryParam(WsCommonConstants.RWS_QP_FC2WP_DEF_ID, fc2wpDefID);

    return get(wsTarget, FC2WPVersion.class);

  }

  /**
   * Get the active version of the given FC2WP definition identified by name and division
   *
   * @param fc2WpName fc2wpName of F2wpDef
   * @param divValueId value ID of division attribute value
   * @return active FC2WP Version
   * @throws ApicWebServiceException failure of WS
   */

  public FC2WPVersion getActiveVersionByValueID(final String fc2WpName, final Long divValueId)
      throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ACTIVE_VERS_BY_VALUE_ID)
        .queryParam(WsCommonConstants.RWS_QP_FC2_WP_NAME, fc2WpName)
        .queryParam(WsCommonConstants.RWS_QP_DIV_VALUE_ID, divValueId);

    return get(wsTarget, FC2WPVersion.class);
  }

  /**
   * Update an existing FC2WP version.
   *
   * @param vers the version
   * @return version details from service
   * @throws ApicWebServiceException error during service call
   */
  public FC2WPVersion update(final FC2WPVersion vers) throws ApicWebServiceException {
    return update(getWsBase(), vers);
  }

  /**
   * @param vers new version details
   * @return version details from service
   * @throws ApicWebServiceException any exception
   */
  public FC2WPVersion create(final FC2WPVersion vers) throws ApicWebServiceException {
    return create(getWsBase(), vers);
  }

  /**
   * Get a version by its ID
   *
   * @param objId version's ID
   * @return version
   * @throws ApicWebServiceException any exception
   */
  public FC2WPVersion get(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);

    return get(wsTarget, FC2WPVersion.class);
  }

  /**
   * Gets the active version by pidc version.
   *
   * @param pidcVersId the pidc vers id
   * @return the active version by pidc version
   * @throws ApicWebServiceException the apic web service exception
   */
  public FC2WPVersion getActiveVersionByPidcVersion(final Long pidcVersId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ACTIVE_VERS_BY_PIDC)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ID, pidcVersId);
    return get(wsTarget, FC2WPVersion.class);
  }

}