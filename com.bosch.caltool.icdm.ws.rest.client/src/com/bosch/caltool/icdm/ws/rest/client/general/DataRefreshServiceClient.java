/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.general;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.general.DataRefreshInput;
import com.bosch.caltool.icdm.model.general.DataRefreshResult;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class DataRefreshServiceClient extends AbstractRestServiceClient {

  /**
   * New instance of client
   */
  public DataRefreshServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_GEN, WsCommonConstants.RWS_DATA_REFRESH);
  }

  /**
   * Refresh the data in the input model. Can add IDs of different types
   *
   * @param input refresh input
   * @return DataRefreshResult
   * @throws ApicWebServiceException Service Error
   */
  public DataRefreshResult refreshData(final DataRefreshInput input) throws ApicWebServiceException {
    LOGGER.debug("Refreshing data object(s). Input details : {}", input);

    DataRefreshResult result = post(getWsBase(), input, DataRefreshResult.class);

    LOGGER.debug("Refreshing data objects completed. Response info : {}", result);

    return result;
  }


}
