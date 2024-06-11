/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.review;

import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.model.cdr.review.ReviewInput;

/**
 * @author bru2cob
 */
public class RuleSetReview implements IReviewProcessResolver {

  ReviewInput reviewInputData;
  ServiceData serviceData;

  /**
   * @param reviewInputData
   * @param serviceData
   */
  public RuleSetReview(final ReviewInput reviewInputData, final ServiceData serviceData) {
    super();
    this.reviewInputData = reviewInputData;
    this.serviceData = serviceData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void performReview() {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReviewedInfo getReviewOutput() {
    // TODO Auto-generated method stub
    return null;
  }

}
