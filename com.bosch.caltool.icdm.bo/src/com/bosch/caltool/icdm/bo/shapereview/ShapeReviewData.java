/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.shapereview;

import com.bosch.caltool.dmframework.bo.ServiceData;

/**
 * Stores the input and output data related to shape analysis
 *
 * @author bne4cob
 */
class ShapeReviewData {

  /**
   * Service data
   */
  private final ServiceData serviceData;

  /**
   * Shape review input
   */
  private final ShapeReviewInput input;

  /**
   * Constructor
   *
   * @param serviceData ServiceData
   * @param input Shape Review Input
   */
  ShapeReviewData(final ServiceData serviceData, final ShapeReviewInput input) {
    this.serviceData = serviceData;
    this.input = input;
  }

  /**
   * @return the input
   */
  public ShapeReviewInput getInput() {
    return this.input;
  }

  /**
   * @return the serviceData
   */
  public ServiceData getServiceData() {
    return this.serviceData;
  }
}
