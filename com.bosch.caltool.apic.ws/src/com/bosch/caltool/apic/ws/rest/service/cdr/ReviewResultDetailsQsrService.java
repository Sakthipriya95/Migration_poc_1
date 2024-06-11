/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.calcomp.externallink.exception.ExternalLinkException;
import com.bosch.calcomp.externallink.process.LinkProcessor;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.cdr.ReviewResultDetailsQsrLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.model.cdr.ReviewResultDetailsQsr;

/**
 * Service to fetch review result details for QSR
 *
 * @author DJA7COB
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_CDR_DETAILS)
public class ReviewResultDetailsQsrService extends AbstractRestService {

  /**
   * @param cdrLink input cdr link
   * @return ReviewResultDetailsQsr
   * @throws IcdmException exception from webservice
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response get(@QueryParam(WsCommonConstants.RWS_CDR_LINK) final String cdrLink) throws IcdmException {

    validateCdrLink(cdrLink);

    ReviewResultDetailsQsr reviewResultDetails =
        new ReviewResultDetailsQsrLoader(getServiceData()).fetchReviewResultDetails(cdrLink);

    return Response.ok(reviewResultDetails).build();
  }

  /**
   * Validates the input cdr link
   *
   * @param cdrLink input cdr link
   * @throws IcdmException
   * @throws ExternalLinkException
   */
  private void validateCdrLink(final String cdrLink) throws IcdmException {
    try {
      LinkProcessor linkProcessor = new LinkProcessor(cdrLink);
      linkProcessor.validateLink();
    }
    catch (ExternalLinkException e) {
      throw new InvalidInputException(e.getMessage(), e);
    }
  }
}
