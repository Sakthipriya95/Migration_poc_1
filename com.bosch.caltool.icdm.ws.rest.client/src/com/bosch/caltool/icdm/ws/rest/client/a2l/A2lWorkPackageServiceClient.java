package com.bosch.caltool.icdm.ws.rest.client.a2l;

import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */

/**
 * @author nip4cob
 */
public class A2lWorkPackageServiceClient extends AbstractRestServiceClient {

  /**
   *
   */
  public A2lWorkPackageServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_A2L_WORK_PACKAGE);
  }

  /**
   * @param a2lWpToCreate - WP to be created
   * @return - the created WP
   * @throws ApicWebServiceException - Error during webservice call
   */
  public A2lWorkPackage create(final A2lWorkPackage a2lWpToCreate) throws ApicWebServiceException {
    return create(getWsBase(), a2lWpToCreate);
  }

  /**
   * @param a2lWpToUpdate - WP to be updated
   * @return - the updated WP
   * @throws ApicWebServiceException - Error during webservice call
   */
  public A2lWorkPackage update(final A2lWorkPackage a2lWpToUpdate) throws ApicWebServiceException {
    return update(getWsBase(), a2lWpToUpdate);
  }

  /**
   * @param pidcVersId - pidcversion id
   * @return map of workpackages mapped to the pidcversion
   * @throws ApicWebServiceException - error during webservice call
   */
  public Map<Long, A2lWorkPackage> getWpByPidcVers(final Long pidcVersId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_WP_BY_PIDC_VERS)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ID, pidcVersId);
    GenericType<Map<Long, A2lWorkPackage>> type = new GenericType<Map<Long, A2lWorkPackage>>() {};
    return get(wsTarget, type);
  }


  /**
   * Get A2lWorkPackage using id
   *
   * @param objId of A2lWorkPackage Ids
   * @return A2lWorkPackage object
   * @throws ApicWebServiceException exception while invoking service
   */
  public A2lWorkPackage getById(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.A2L_WP_ID, objId);
    return get(wsTarget, A2lWorkPackage.class);
  }
}
