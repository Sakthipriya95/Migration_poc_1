/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.reviewresult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.SortedSet;

import com.bosch.caltool.apic.ws.GetParameterReviewResult;
import com.bosch.caltool.apic.ws.GetParameterReviewResultResponseType;
import com.bosch.caltool.apic.ws.ReviewResultsType;
import com.bosch.caltool.apic.ws.pidc.AbstractPidc;
import com.bosch.caltool.apic.ws.reviewresult.adapter.parameter.TParamAdapter;
import com.bosch.caltool.apic.ws.reviewresult.adapter.review.CDRResultParamAdapter;
import com.bosch.caltool.apic.ws.session.Session;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.cdr.CDRResultParameterLoader;
import com.bosch.caltool.icdm.bo.cdr.CDRReviewResultLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;


/**
 * @author imi2si
 */
public class IcdmReviewResults extends AbstractReviewResult {


  /**
   *
   */
  protected final Session session;
  private final ServiceData serviceData;

  /**
   * @param session session
   * @param getParameterReviewResult getParameterReviewResult
   * @param pidcHandler pidcHandler
   * @param serviceData serviceData
   */
  public IcdmReviewResults(final Session session, final GetParameterReviewResult getParameterReviewResult,
      final AbstractPidc pidcHandler, final ServiceData serviceData) {
    super(getParameterReviewResult, pidcHandler);
    this.session = session;
    this.serviceData = serviceData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final GetParameterReviewResultResponseType getParameters(final long paramId) {
    TParamAdapter paramAdapter = new TParamAdapter(new ParameterLoader(this.serviceData).getEntityObject(paramId));
    paramAdapter.adapt();
    return paramAdapter;
  }

  /**
   * {@inheritDoc}
   *
   * @throws IcdmException Error during webservice call
   * @throws IOException
   * @throws ClassNotFoundException
   */
  @Override
  protected final ReviewResultsType[] getReviewResults(final long paramId)
      throws IcdmException, ClassNotFoundException, IOException {

    // create loader objects here itself to avoid creating them inside a loop
    CDRResultParameterLoader cdrResultParameterLoader = new CDRResultParameterLoader(this.serviceData);
    CDRReviewResultLoader rvwResultLoader = new CDRReviewResultLoader(this.serviceData);
    PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(this.serviceData);
    SortedSet<CDRResultParameter> cdrResults;
    cdrResults = cdrResultParameterLoader.getReviewDataResponse(paramId);
    ArrayList<ReviewResultsType> reviewResults = new ArrayList<>();

    for (CDRResultParameter cdrResult : cdrResults) {
      reviewResults.add(new CDRResultParamAdapter(this.session, cdrResult, this.pidcHandler, this.serviceData,
          cdrResultParameterLoader, rvwResultLoader, pidcVersionLoader));
    }
    return reviewResults.toArray(new ReviewResultsType[0]);
  }


}
