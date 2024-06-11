/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.reviewresult.output;

import java.util.Set;

import com.bosch.caltool.apic.ws.client.output.AbstractStringOutput;
import com.bosch.caltool.apic.ws.client.reviewresult.ReviewDetail;
import com.bosch.caltool.apic.ws.client.reviewresult.ReviewResult;


/**
 * @author imi2si
 */
public class StringRevResultOutput extends AbstractStringOutput {

  ReviewResult revResult;

  public StringRevResultOutput(final ReviewResult reviewResult) {
    this.revResult = reviewResult;
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
    append("Number of Records");
    append("Value String");
    append("Unit");
    lineBreak();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createRows() {
    append(this.revResult.getNumberOfRecords());
    append(this.revResult.getCheckedValueString());
    append(this.revResult.getUnit());
    lineBreak();

    createDetails(this.revResult.getReviewDetails());
  }

  private void createDetails(final Set<ReviewDetail> reviewDetails) {
    StringRevDetOutput reviewResult;

    for (ReviewDetail result : reviewDetails) {
      reviewResult = new StringRevDetOutput(result);
      reviewResult.createOutput();
      appendDetail(reviewResult.getOutput());
      lineBreak();
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean outputAvailable() {
    // TODO Auto-generated method stub
    return this.revResult != null;
  }

}
