/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.reviewresult.output;

import com.bosch.caltool.apic.ws.client.output.AbstractStringOutput;
import com.bosch.caltool.apic.ws.client.reviewresult.ReviewDetail;


/**
 * @author imi2si
 */
public class StringRevDetOutput extends AbstractStringOutput {

  ReviewDetail revDetail;

  public StringRevDetOutput(final ReviewDetail revDetail) {
    this.revDetail = revDetail;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getOutput() {
    return this.output.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createHeader() {
    append("PIDC ID");
    append("PIDC Name");
    append("Variant ID");
    append("Variant Name");
    append("Parameter Name");
    append("Review Result ID");
    append("Review Name");
    append("Review Date");
    append("Review Comment");
    lineBreak();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createRows() {
    append(this.revDetail.getPidcId());
    append(this.revDetail.getPidcName());
    append(this.revDetail.getVariantId());
    append(this.revDetail.getVariantName());
    append(this.revDetail.getParamName());
    append(this.revDetail.getReviewResultId());
    append(this.revDetail.getReviewName());
    append(this.revDetail.getDateOfReview().getTime());
    append(this.revDetail.getReviewComment());
    lineBreak();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean outputAvailable() {
    return this.revDetail != null;
  }

}
