/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.icdm.model.cdr.ReviewParamResponse;
import com.bosch.caltool.icdm.model.cdr.ReviewResultType;

/**
 * @author rgo7cob
 */
public class ReviewParameter {

  private final ReviewParamResponse wsResponse;
  private final Set<ReviewResult> reviewResults = new HashSet<>();

  /**
   * @param paramName
   * @param reviewData
   */
  public ReviewParameter(final Map<String, ReviewParamResponse> response, final String paramName) {
    List<ReviewResultType> rvwResType = new ArrayList<>();
    for (ReviewParamResponse reviewParamResponse : response.values()) {
      rvwResType.addAll(reviewParamResponse.getReviewResultType());
    }
    this.wsResponse = response.get(paramName);
    if (this.wsResponse != null) {
      this.wsResponse.setReviewResultType(rvwResType);
      createReviewResult();
    }
  }

  private void createReviewResult() {
    ReviewResult revDet;
    for (ReviewResultType type : this.wsResponse.getReviewResultType()) {
      revDet = new ReviewResult(type);
      ReviewResult reviewResult = getReviewResult(revDet);

      if (reviewResult != null) {
        reviewResult.addReviewDetail(type);
        this.reviewResults.add(reviewResult);
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


  /**
   * @return the reviewResults
   */
  public Set<ReviewResult> getReviewResults() {
    return this.reviewResults;
  }

  public long getParameterId() {
    return this.wsResponse.getParamId();
  }

  public String getParameterName() {
    return this.wsResponse.getParamName();
  }

  public String getParameterLongname() {
    return this.wsResponse.getLongName();
  }


}
