/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.cdr.PreCalibrationDataInput;
import com.bosch.caltool.icdm.model.cdr.PreCalibrationDataResponse;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Client class for fetching pre caldata from review results
 *
 * @author svj7cob
 */
// Task 243510
public class PreCalibrationDataServiceClient extends AbstractRestServiceClient {

  /**
   * Service client for PreCalibrationDataService
   */
  public PreCalibrationDataServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_CDR_PRECALDATA);
  }

  /**
   * Fetch Pre caldata service
   *
   * @param input PreCalibrationDataInput
   * @return PreCalibrationDataResponse data
   * @throws ApicWebServiceException error during service call
   */
  public PreCalibrationDataResponse getPreCalData(final PreCalibrationDataInput input) throws ApicWebServiceException {

    LOGGER.debug("Fetch Pre caldata service started...");

    PreCalibrationDataResponse response = post(getWsBase(), input, PreCalibrationDataResponse.class);

    LOGGER.debug("Fetch Pre caldata service ended");
    return response;

  }
}
