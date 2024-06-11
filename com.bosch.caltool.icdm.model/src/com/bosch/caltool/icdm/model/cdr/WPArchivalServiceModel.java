/*
 * Copyright (c) ETAS GmbH 2024. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.icdm.model.a2l.A2lWPRespModel;

/**
 *
 */
public class WPArchivalServiceModel {

  private CDRReviewResult cdrReviewResult;

  private TreeViewSelectnRespWP treeViewSelectnRespWP;

  private Set<A2lWPRespModel> completedWPRespMap = new HashSet<>();


  /**
   * @return the cdrReviewResult
   */
  public CDRReviewResult getCdrReviewResult() {
    return this.cdrReviewResult;
  }


  /**
   * @param cdrReviewResult the cdrReviewResult to set
   */
  public void setCdrReviewResult(final CDRReviewResult cdrReviewResult) {
    this.cdrReviewResult = cdrReviewResult;
  }


  /**
   * @return the treeViewSelectnRespWP
   */
  public TreeViewSelectnRespWP getTreeViewSelectnRespWP() {
    return this.treeViewSelectnRespWP;
  }


  /**
   * @param treeViewSelectnRespWP the treeViewSelectnRespWP to set
   */
  public void setTreeViewSelectnRespWP(final TreeViewSelectnRespWP treeViewSelectnRespWP) {
    this.treeViewSelectnRespWP = treeViewSelectnRespWP;
  }


  /**
   * @return the completedWPRespMap
   */
  public Set<A2lWPRespModel> getCompletedWPRespMap() {
    return this.completedWPRespMap;
  }


  /**
   * @param completedWPRespMap the completedWPRespMap to set
   */
  public void setCompletedWPRespMap(final Set<A2lWPRespModel> completedWPRespMap) {
    this.completedWPRespMap = completedWPRespMap;
  }
}
