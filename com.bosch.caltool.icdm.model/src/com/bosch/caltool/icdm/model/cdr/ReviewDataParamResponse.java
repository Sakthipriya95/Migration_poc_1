/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.Map;

import com.bosch.caltool.icdm.model.a2l.ParameterRulesResponse;

/**
 * @author RDP2COB
 *
 */
public class ReviewDataParamResponse {

  
  private Map<String, ReviewParamResponse> reviewParamResponse;
  
  private ParameterRulesResponse parameterRulesResponse;
  /**
   * @return the reviewParamResponse
   */
  public Map<String, ReviewParamResponse> getReviewParamResponse() {
    return reviewParamResponse;
  }

  
  /**
   * @param reviewParamResponse the reviewParamResponse to set
   */
  public void setReviewParamResponse(Map<String, ReviewParamResponse> reviewParamResponse) {
    this.reviewParamResponse = reviewParamResponse;
  }

  /**
   * @return the parameterRulesResponse
   */
  public ParameterRulesResponse getParameterRulesResponse() {
    return parameterRulesResponse;
  }

  /**
   * @param parameterRulesResponse the parameterRulesResponse to set
   */
  public void setParameterRulesResponse(ParameterRulesResponse parameterRulesResponse) {
    this.parameterRulesResponse = parameterRulesResponse;
  }
}
