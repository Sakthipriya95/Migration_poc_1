/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.reviewresult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.apic.ws.client.APICStub.GetParameterReviewResultResponseType;
import com.bosch.caltool.apic.ws.client.APICStub.ReviewResultsType;


/**
 * @author imi2si
 */
public class ReviewParameter {

  private GetParameterReviewResultResponseType wsResponse = null;
  private final Set<ReviewResult> reviewResults = new HashSet<>();

  public ReviewParameter(final GetParameterReviewResultResponseType wsResponse)
      throws ClassNotFoundException, IOException {
    this.wsResponse = wsResponse;
    createReviewResult();
  }

  public ReviewParameter(final GetParameterReviewResultResponseType[] wsResponse, final String parameterName)
      throws ClassNotFoundException, IOException {

    ArrayList<ReviewResultsType> revResTypList = new ArrayList<>();

    // In case of getting the result for variant encoded parameters, all the results will be considered
    // as belonging to the base parameter. This means, the passed parameter will include all the review results for all
    // returned parameters.
    for (GetParameterReviewResultResponseType response : wsResponse) {

      // The passed parameter name is the base parameter which will be stored as
      // the parameter where all review results will be collected
      if (response.getParameterName().equalsIgnoreCase(parameterName)) {
        this.wsResponse = response;
      }

      revResTypList.addAll(Arrays.asList(response.getReviewDetails()));
    }

    // If the wsResponse object is not filled, the passed parameter is not existing in the result set. Thus, nothing
    // should happen
    if (this.wsResponse != null) {

      this.wsResponse.setReviewDetails(revResTypList.toArray(new ReviewResultsType[0]));
      createReviewResult();
    }
  }

  private void createReviewResult() throws ClassNotFoundException, IOException {
    ReviewResult revDet;

    for (ReviewResultsType type : this.wsResponse.getReviewDetails()) {
      revDet = new ReviewResult(type);
      ReviewResult reviewResult = getReviewResult(revDet);
      if (reviewResult != null) {
        reviewResult.addReviewDetail(type);
      }
      else {
        this.reviewResults.add(revDet);
      }
    }
  }

  private ReviewResult getReviewResult(final ReviewResult result) {
    for (ReviewResult entry : this.reviewResults) {
      if (entry.equals(result)) {
        return entry;
      }
    }
    return null;
  }

  public long getParameterId() {
    return this.wsResponse.getParameterId();
  }

  public String getParameterName() {
    return this.wsResponse.getParameterName();
  }

  public String getParameterLongname() {
    return this.wsResponse.isParameterLongnameSpecified() ? this.wsResponse.getParameterLongname() : "";
  }

  public Set<ReviewResult> getReviewResults() {
    return this.reviewResults;
  }

}
