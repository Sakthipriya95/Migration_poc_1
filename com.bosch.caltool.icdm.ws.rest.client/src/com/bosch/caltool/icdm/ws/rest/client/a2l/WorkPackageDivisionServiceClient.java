/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author bne4cob
 */
public class WorkPackageDivisionServiceClient extends AbstractRestServiceClient {

  /**
   * Service client for WorkPackageDivisionService
   */
  public WorkPackageDivisionServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_WP_DIV);
  }

  /**
   * Fetch all FC to WP versions of the given definition
   *
   * @param divId Divsion ID (attibute value ID for the division)
   * @param includeDeleted flag to indicate if deleted
   * @return data
   * @throws ApicWebServiceException error during service call
   */
  public Set<WorkPackageDivision> getWPDivisionsByByDivID(final Long divId, final boolean includeDeleted)
      throws ApicWebServiceException {

    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_GET_WP_DIV_BY_DIV_ID).queryParam(WsCommonConstants.RWS_QP_OBJ_ID, divId)
            .queryParam(WsCommonConstants.RWS_QP_INCLUDE_DELETED, includeDeleted);
    GenericType<Set<WorkPackageDivision>> type = new GenericType<Set<WorkPackageDivision>>() {};

    return get(wsTarget, type);

  }

  /**
   * this method has been added to facilitate the inclusion of iccRelevantFlag while fetching the Wps by Div ID
   *
   * @param divId divId Divsion ID (attibute value ID for the division)
   * @param includeDeleted includeDeleted flag to indicate if deleted
   * @param iccRelevantFlag flag to indicate if the division is related
   * @return
   * @throws ApicWebServiceException
   */
  public Set<WorkPackageDivision> getWPDivisionsByDivIDandIccRelevantFlag(final Long divId,
      final boolean includeDeleted, final String iccRelevantFlag)
      throws ApicWebServiceException {

    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_GET_WP_DIV_BY_DIV_ID).queryParam(WsCommonConstants.RWS_QP_OBJ_ID, divId)
            .queryParam(WsCommonConstants.RWS_QP_INCLUDE_DELETED, includeDeleted)
            .queryParam(WsCommonConstants.ICC_RELEVANT_FLAG, iccRelevantFlag);
    GenericType<Set<WorkPackageDivision>> type = new GenericType<Set<WorkPackageDivision>>() {};

    return get(wsTarget, type);
  }


  /**
   * Update an existing FC2WP version.
   *
   * @param vers the version
   * @return version details from service
   * @throws ApicWebServiceException error during service call
   */
  public WorkPackageDivision update(final WorkPackageDivision vers) throws ApicWebServiceException {
    return update(getWsBase(), vers);
  }

  /**
   * @param input new division details
   * @return version details from service
   * @throws ApicWebServiceException any exception
   */
  public WorkPackageDivision create(final WorkPackageDivision input) throws ApicWebServiceException {
    return post(getWsBase(), input, WorkPackageDivision.class);
  }

  /**
   * Get a wp division by its ID
   *
   * @param objId version's ID
   * @return version
   * @throws ApicWebServiceException any exception
   */
  public WorkPackageDivision get(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);

    return get(wsTarget, WorkPackageDivision.class);
  }

  /**
   * Get a wp division by its WP ID
   *
   * @param wpId wp id
   * @return version
   * @throws ApicWebServiceException any exception
   */
  public Set<WorkPackageDivision> getByWpId(final Long wpId) throws ApicWebServiceException {
    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_GET_WP_DIV_BY_WP_ID).queryParam(WsCommonConstants.RWS_QP_WP_ID, wpId);
    GenericType<Set<WorkPackageDivision>> type = new GenericType<Set<WorkPackageDivision>>() {};
    return get(wsTarget, type);
  }
}
