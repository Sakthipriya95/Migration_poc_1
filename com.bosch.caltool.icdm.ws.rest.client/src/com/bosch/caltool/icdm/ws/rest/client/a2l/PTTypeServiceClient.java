/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.a2l.PTType;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author gge6cob
 */
public class PTTypeServiceClient extends AbstractRestServiceClient {

  /**
   * Service client for FC2WPDefinitionService
   */
  public PTTypeServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_POWER_TRAIN_TYPE);
  }

  /**
   * Fetch All PowerTrain-types
   *
   * @return data
   * @throws ApicWebServiceException error during service call
   */
  public Set<PTType> getAllPTtypes() throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL);
    GenericType<Set<PTType>> type = new GenericType<Set<PTType>>() {};

    return get(wsTarget, type);
  }
}
