/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.cdr.ReviewResultDetailsQsr;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Service client to fetch review result details for QSR
 * 
 * @author DJA7COB
 */
public class ReviewResultDetailsQsrServiceClient extends AbstractRestServiceClient {

  /**
   * Constructor
   */
  public ReviewResultDetailsQsrServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_CDR_DETAILS);
  }


  /**
   * Fetches review result details by cr link
   *
   * @param cdrLink input cdr link
   * @return ReviewResultDetailsQsr
   * @throws ApicWebServiceException exception
   */
  public ReviewResultDetailsQsr getbyCdrLink(final String cdrLink) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_CDR_LINK, cdrLink);
    return get(wsTarget, ReviewResultDetailsQsr.class);
  }
}
