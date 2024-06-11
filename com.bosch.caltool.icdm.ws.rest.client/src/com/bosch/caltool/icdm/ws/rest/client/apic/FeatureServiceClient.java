/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.concurrent.ConcurrentMap;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.ssdfeature.Feature;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author rgo7cob
 */
public class FeatureServiceClient extends AbstractRestServiceClient {

  /**
   * Constructor
   */
  public FeatureServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_FEATURE);
  }


  /**
   * @return the sorted set of attributes
   */
  public ConcurrentMap<Long, Feature> getAllFeatures() throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL_FEATURES);
    GenericType<ConcurrentMap<Long, Feature>> type = new GenericType<ConcurrentMap<Long, Feature>>() {};
    return get(wsTarget, type);
  }
}
