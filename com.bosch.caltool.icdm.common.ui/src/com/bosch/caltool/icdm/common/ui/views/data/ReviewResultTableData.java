/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.views.data;

import com.bosch.caltool.icdm.client.bo.cdr.ReviewResult;

/**
 * iCDM-713<br>
 *
 * @author adn1cob
 */
public class ReviewResultTableData {


  private final ReviewResult reviewResult;

  private final int serialIndex;

  private boolean inGraph;


  /**
   * @param reviewResult
   * @param serialIndex
   */
  public ReviewResultTableData(final ReviewResult reviewResult, final int serialIndex) {
    this.reviewResult = reviewResult;
    this.serialIndex = serialIndex;
  }


  /**
   * @return the reviewResult
   */
  public ReviewResult getReviewResult() {
    return this.reviewResult;
  }


  /**
   * @return the serialIndex
   */
  public int getSerialIndex() {
    return this.serialIndex;
  }


  /**
   * @return the inGraph
   */
  public boolean isInGraph() {
    return this.inGraph;
  }


  /**
   * @param isInGraph
   */
  public void setInGraph(final boolean isInGraph) {
    this.inGraph = isInGraph;
  }


}
