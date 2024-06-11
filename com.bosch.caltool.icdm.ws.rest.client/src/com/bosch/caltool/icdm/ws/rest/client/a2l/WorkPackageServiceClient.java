/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.wp.WorkPkg;
import com.bosch.caltool.icdm.model.wp.WorkPkgInput;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service client for Work packages
 *
 * @author bne4cob
 */
public class WorkPackageServiceClient extends AbstractRestServiceClient {


  /**
   * Service client for WorkPackageService
   */
  public WorkPackageServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_WP);
  }

  /**
   * Fetch Work Package with the given Id
   *
   * @param objId Primary Key
   * @return Rest response
   * @throws ApicWebServiceException error during execution
   */
  public WorkPkg findById(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, WorkPkg.class);
  }

  /**
   * Create a WorkPackage
   *
   * @param input WorkPackage
   * @return Rest response
   * @throws ApicWebServiceException error during execution
   */

  public WorkPkg create(final WorkPkg input) throws ApicWebServiceException {
    return put(getWsBase(), input, WorkPkg.class);
  }

  /**
   * Update an existing WorkPackage
   *
   * @param input WorkPackage details
   * @return Rest response
   * @throws ApicWebServiceException error during execution
   */

  public WorkPkg update(final WorkPkg input) throws ApicWebServiceException {
    return post(getWsBase(), input, WorkPkg.class);
  }

  /**
   * Fetch all WorkPackages defined in the system
   *
   * @return Rest response
   * @throws ApicWebServiceException error during execution
   */

  public Set<WorkPkg> findAll() throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL);
    GenericType<Set<WorkPkg>> type = new GenericType<Set<WorkPkg>>() {};

    return get(wsTarget, type);
  }


  /**
   * @param workPkgInput as input
   * @return as Map<Long, String>
   * @throws ApicWebServiceException as exception
   */
  public Map<Long, String> getWorkRespMap(final WorkPkgInput workPkgInput) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_WP_RESPONSE_MAP);
    return post(wsTarget, workPkgInput, new GenericType<Map<Long, String>>() {});
  }


  /**
   * @param qnaireId as input
   * @return as Workpkg object
   * @throws ApicWebServiceException as exception
   */
  public WorkPkg getWorkPkgbyQnaireID(final Long qnaireId) throws ApicWebServiceException {
    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_GET_WORKPKG).queryParam(WsCommonConstants.RWS_QP_QNAIRE_ID, qnaireId);
    return get(wsTarget, WorkPkg.class);
  }


  /**
   * @param qnaireIdSet as input
   * @return Set<String> workpkg name
   * @throws ApicWebServiceException as exception
   */
  public Map<Long, String> getWorkPkgNameUsingQnaireIDSet(final Set<Long> qnaireIdSet) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_WORKPKG_NAME);
    GenericType<Map<Long, String>> type = new GenericType<Map<Long, String>>() {};
    return post(wsTarget, qnaireIdSet, type);
  }
}
