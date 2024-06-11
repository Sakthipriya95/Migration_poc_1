/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.review;

import com.bosch.caltool.icdm.model.cdr.ReviewRule;

/**
 * @author bru2cob
 */
public class RuleUtility {

  /**
   * Rule is complete if any one of the following conditions is satisfied.
   * <p>
   * a) upper limit or lower limit is available<br>
   * b) reference value is available and exact match flag is true
   * 
   * @param rule Review Rule obj
   * @return true if rule is complete
   */
  public boolean isRuleComplete(final ReviewRule rule) {
    if (rule == null) {
      return false;
    }

    if ((null != rule.getLowerLimit()) || (null != rule.getUpperLimit()) ||
        ((null != rule.getBitWiseRule()) && !rule.getBitWiseRule().isEmpty())) {
      return true;
    }
    return rule.isDcm2ssd() && ((null != rule.getRefValue()) || (null != rule.getRefValueCalData()));
  }


}
