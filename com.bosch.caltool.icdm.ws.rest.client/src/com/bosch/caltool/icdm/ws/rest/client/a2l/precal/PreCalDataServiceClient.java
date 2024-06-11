/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l.precal;

import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.a2l.precal.PreCalAttrValResponse;
import com.bosch.caltool.icdm.model.a2l.precal.PreCalData;
import com.bosch.caltool.icdm.model.a2l.precal.PreCalInputData;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class PreCalDataServiceClient extends AbstractRestServiceClient {

  /**
   * Constructor.
   */
  public PreCalDataServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_PRECAL);
  }

  /**
   * Get pre calibration data for the given input
   *
   * @param input PreCalInputData
   * @return pre-calibration data
   * @throws ApicWebServiceException any error while fetching data
   */
  public PreCalData getPreCalData(final PreCalInputData input) throws ApicWebServiceException {
    LOGGER.debug("PreCalDataServiceClient.getPreCalData() started");

    PreCalData ret = post(getWsBase(), input, PreCalData.class);
    LOGGER.info("PreCalDataServiceClient.getPreCalData() completed. Number of parameters with data = {}",
        ret.getPreCalDataMap().size());

    return ret;
  }

  /**
   * Get the attributes and values to be used to find the pre-cal data for the given project
   *
   * @param input PreCalInputData
   * @return pre-calibration data
   * @throws ApicWebServiceException any error while fetching data
   */
  public PreCalAttrValResponse getPreCalAttrVals(final PreCalInputData input) throws ApicWebServiceException {
    LOGGER.debug("PreCalDataServiceClient.getPreCalAttrVals() started");

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_PRECAL_ATTR_VAL);

    PreCalAttrValResponse ret = post(wsTarget, input, PreCalAttrValResponse.class);
    LOGGER.info("PreCalDataServiceClient.getPreCalAttrVals() completed. Number of dependant attributes = {}",
        ret.getAttrMap().size());

    return ret;
  }

}
