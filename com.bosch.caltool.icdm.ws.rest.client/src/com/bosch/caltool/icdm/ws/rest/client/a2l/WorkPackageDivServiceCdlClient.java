/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.wp.WorkpackageDivisionCdl;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author apj4cob
 *
 */
public class WorkPackageDivServiceCdlClient extends AbstractRestServiceClient{
  /**
   * Service client for WorkPackageDivisionService
   */
  public WorkPackageDivServiceCdlClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_WP_DIV_CDL);
  }
  
  /**
   * @param input new division cdl details
   * @return WorkpackageDivisionCdl details from service
   * @throws ApicWebServiceException any exception
   */
  public WorkpackageDivisionCdl create(final WorkpackageDivisionCdl input) throws ApicWebServiceException {
    return put(getWsBase(), input, WorkpackageDivisionCdl.class);
  }
  /**
   * Delete a TWorkpackageDivisionCdl record
   *
   * @param objId id of object to delete
   * @throws ApicWebServiceException exception while invoking service
   */
  public void delete(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    delete(wsTarget);
  }

  /**
   * Update an existing Cdl.
   * @param cdl  WorkpackageDivisionCdl
   *
   * @return WorkpackageDivisionCdl details from service
   * @throws ApicWebServiceException error during service call
   */
  public WorkpackageDivisionCdl update(final WorkpackageDivisionCdl cdl) throws ApicWebServiceException {
    return post(getWsBase(),cdl, WorkpackageDivisionCdl.class);
  }
  /**
   * Get Cdl data by WP Div Id
   * 
   * @param wpDivId work package div id
   * @return set of Workpackage Division Cdl
   * @throws ApicWebServiceException any exception
   */
  public Set<WorkpackageDivisionCdl> getCdlByDivId(final Long wpDivId)throws ApicWebServiceException{
    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_GET_ALL_BY_WP_DIV_ID).queryParam(WsCommonConstants.RWS_QP_WP_DIV_ID, wpDivId);
    GenericType<Set<WorkpackageDivisionCdl>> type = new GenericType<Set<WorkpackageDivisionCdl>>() {};
    return get(wsTarget, type); 
  }

}
