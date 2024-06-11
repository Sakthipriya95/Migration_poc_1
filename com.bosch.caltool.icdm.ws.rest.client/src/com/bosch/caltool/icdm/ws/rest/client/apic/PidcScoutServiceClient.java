/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.Set;

import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSearchInput;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSearchResponse;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSearchResult;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author gge6cob
 */
public class PidcScoutServiceClient extends AbstractRestServiceClient {

  /**
   * Instantiates a new pidc scout service client.
   */
  public PidcScoutServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_PIDC_SCOUT);
  }

  /**
   * PIDC Search rest service ICDM-2326.
   *
   * @param input PidcSearchInput
   * @return Set of PidcSearchResult
   * @throws ApicWebServiceException the apic web service exception
   */
  public Set<PidcSearchResult> searchProjects(final PidcSearchInput input) throws ApicWebServiceException {
    LOGGER.debug("Started Searching projects with PIDC Scout web service. Search input = {}", input);

    GenericType<PidcSearchResponse> type = new GenericType<PidcSearchResponse>() {};
    PidcSearchResponse response = post(getWsBase(), input, type);

    LOGGER.debug("Number of project versions returned - {}", response.getResults().size());

    return response.getResults();
  }
}
