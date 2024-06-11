/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.reviewresult;

import java.io.IOException;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.apic.ws.GetParameterReviewResult;
import com.bosch.caltool.apic.ws.GetParameterReviewResultResponse;
import com.bosch.caltool.apic.ws.GetParameterReviewResultResponseType;
import com.bosch.caltool.apic.ws.GetParameterReviewResultsType;
import com.bosch.caltool.apic.ws.ParameterReviewResults;
import com.bosch.caltool.apic.ws.ReviewResultsType;
import com.bosch.caltool.apic.ws.db.IWebServiceResponse;
import com.bosch.caltool.apic.ws.pidc.AbstractPidc;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.logger.WSLogger;


/**
 * @author imi2si
 */
public abstract class AbstractReviewResult implements IWebServiceResponse {

  /**
   * Logger
   */
  private static final ILoggerAdapter LOG = WSLogger.getInstance();

  /**
   *
   */
  protected final GetParameterReviewResultResponse wsResult = new GetParameterReviewResultResponse();

  /**
   *
   */
  protected ParameterReviewResults wsParamReviewResults = new ParameterReviewResults();

  /**
   *
   */
  protected final GetParameterReviewResultsType wsRequestType;

  /**
   *
   */
  protected final AbstractPidc pidcHandler;

  /**
   * @param getParameterReviewResult getParameterReviewResult
   * @param pidcHandler AbstractPidc
   */
  public AbstractReviewResult(final GetParameterReviewResult getParameterReviewResult, final AbstractPidc pidcHandler) {
    this.wsRequestType = getParameterReviewResult.getGetParameterReviewResult();
    this.pidcHandler = pidcHandler;
  }

  public void createWsResponse() throws IcdmException, ClassNotFoundException, IOException {
    final long[] paramIds = this.wsRequestType.getParameterId();
    GetParameterReviewResultResponseType wsResponseType;
    for (long paramId : paramIds) {
      LOG.debug("Creating Review Results for paremeter ID: {} ", paramId);
      wsResponseType = getParameters(paramId);
      wsResponseType.setReviewDetails(getReviewResults(paramId));
      this.wsParamReviewResults.addParameterReviewResults(wsResponseType);
    }
    this.wsResult.setGetParameterReviewResultResponse(this.wsParamReviewResults);
  }

  public GetParameterReviewResultResponse getWsResponse() {
    return this.wsResult;
  }

  /**
   * @param paramId paramId
   * @return
   */
  protected abstract GetParameterReviewResultResponseType getParameters(final long paramId);

  /**
   * @param paramId paramId
   * @return
   * @throws IcdmException
   * @throws IOException
   * @throws ClassNotFoundException
   */
  protected abstract ReviewResultsType[] getReviewResults(final long paramId)
      throws IcdmException, ClassNotFoundException, IOException;

}
