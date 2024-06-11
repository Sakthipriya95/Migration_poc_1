/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;

/**
 * @author dja7cob
 */
public class PidcVarRvwDetails {

  private CDRReviewResult reviewResult;

  private RvwVariant reviewVariant;


  /**
   * @return the reviewResult
   */
  public CDRReviewResult getReviewResult() {
    return this.reviewResult;
  }


  /**
   * @param reviewResult the reviewResult to set
   */
  public void setReviewResult(final CDRReviewResult reviewResult) {
    this.reviewResult = reviewResult;
  }


  /**
   * @return the reviewVariant
   */
  public RvwVariant getReviewVariant() {
    return this.reviewVariant;
  }


  /**
   * @param reviewVariant the reviewVariant to set
   */
  public void setReviewVariant(final RvwVariant reviewVariant) {
    this.reviewVariant = reviewVariant;
  }

}
