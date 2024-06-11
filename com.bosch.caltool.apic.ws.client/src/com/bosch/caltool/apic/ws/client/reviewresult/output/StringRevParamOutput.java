/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.reviewresult.output;

import java.util.Set;

import com.bosch.caltool.apic.ws.client.output.AbstractStringOutput;
import com.bosch.caltool.apic.ws.client.reviewresult.ReviewParameter;
import com.bosch.caltool.apic.ws.client.reviewresult.ReviewResult;


/**
 * @author imi2si
 */
public class StringRevParamOutput extends AbstractStringOutput {

  ReviewParameter revParam;

  public StringRevParamOutput(final ReviewParameter revParam) {
    this.revParam = revParam;
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
    append("Parameter ID");
    append("Parameter Name");
    append("Parameter Longname");
    lineBreak();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createRows() {
    append(this.revParam.getParameterId());
    append(this.revParam.getParameterName());
    append(this.revParam.getParameterLongname());
    lineBreak();

    createDetails(this.revParam.getReviewResults());
  }

  private void createDetails(final Set<ReviewResult> reviewResults) {
    StringRevResultOutput revResult;

    for (ReviewResult result : reviewResults) {
      revResult = new StringRevResultOutput(result);
      revResult.createOutput();
      appendDetail(revResult.getOutput());
      lineBreak();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean outputAvailable() {
    // TODO Auto-generated method stub
    return this.revParam != null;
  }
}
