/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author bru2cob
 */
public class DefaultRuleDefinition implements Comparable<DefaultRuleDefinition> {

  private static final String description = "Default Rule";


  private ReviewRule reviewRule;


  /**
   * @return the description
   */
  public String getDescription() {
    return DefaultRuleDefinition.description;
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

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final DefaultRuleDefinition o) {
    return this.reviewRule.compareTo(o.getReviewRule());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    return ModelUtil.isEqual(getReviewRule().getRuleId(), ((DefaultRuleDefinition) obj).getReviewRule().getRuleId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getReviewRule().getRuleId());
  }

}
