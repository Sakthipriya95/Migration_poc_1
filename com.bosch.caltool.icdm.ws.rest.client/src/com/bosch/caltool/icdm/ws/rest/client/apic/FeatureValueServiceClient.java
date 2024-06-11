/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.List;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.ssdfeature.FeatureValue;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author rgo7cob
 */
public class FeatureValueServiceClient extends AbstractRestServiceClient {

  /**
   * Constructor
   */
  public FeatureValueServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_FEATURE_VALUE);
  }


  /**
   * @return the sorted set of attributes
   */
  public List<FeatureValue> getFeatureValues(final Long featureId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_BY_FEA_ID)
        .queryParam(WsCommonConstants.RWS_QP_FEATURE_ID, featureId);

    GenericType<List<FeatureValue>> type = new GenericType<List<FeatureValue>>() {};
    return get(wsTarget, type);
  }
}
