/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rgo7cob
 * @param <C> ParamCollection
 */
public class ReviewRuleParamCol<C extends ParamCollection> {

  /*
   * Review Rule List used for creating and deleting review rules
   */
  private List<ReviewRule> reviewRuleList;

  private C paramCollection;

  /*
   * Review Rule Object used for updating review rules
   */
  private ReviewRule reviewRule;


  /**
   * @return the reviewRuleList
   */
  public List<ReviewRule> getReviewRuleList() {
    return this.reviewRuleList;
  }


  /**
   * @param reviewRuleList the reviewRuleList to set
   */
  public void setReviewRuleList(final List<ReviewRule> reviewRuleList) {
    this.reviewRuleList = reviewRuleList == null ? null : new ArrayList<>(reviewRuleList);
  }


  /**
   * @return the paramCollection
   */
  public C getParamCollection() {
    return this.paramCollection;
  }


  /**
   * @param paramCollection the paramCollection to set
   */
  public void setParamCollection(final C paramCollection) {
    this.paramCollection = paramCollection;
  }


  /**
   * @return the reviewRule
   */
  public ReviewRule getReviewRule() {
    return this.reviewRule;
  }


  /**
   * @param reviewRule the reviewRule to set
   */
  public void setReviewRule(final ReviewRule reviewRule) {
    this.reviewRule = reviewRule;
  }


}
