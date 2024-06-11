/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.wp.WPResourceDetails;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dja7cob
 */
public class WorkPackageResourceServiceClient extends AbstractRestServiceClient {

  /**
   * Service client for WorkPackageDivisionService
   */
  public WorkPackageResourceServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_WP_RES);
  }


  /**
   * Update an existing WP resource.
   *
   * @param vers the wp resource
   * @return version details from service
   * @throws ApicWebServiceException error during service call
   */
  public WPResourceDetails update(final WPResourceDetails vers) throws ApicWebServiceException {
    return post(getWsBase(), vers, WPResourceDetails.class);
  }

  /**
   * @param input new WP resource
   * @return WPResourceDetails
   * @throws ApicWebServiceException any exception
   */
  public WPResourceDetails create(final WPResourceDetails input) throws ApicWebServiceException {
    return put(getWsBase(), input, WPResourceDetails.class);
  }

  /**
   * Get all wp resources
   *
   * @return all wp resources as set
   * @throws ApicWebServiceException error during service call
   */
  public Set<WPResourceDetails> getAllWpRes() throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL_WP_RES);
    GenericType<Set<WPResourceDetails>> type = new GenericType<Set<WPResourceDetails>>() {};
    return get(wsTarget, type);
  }
}
