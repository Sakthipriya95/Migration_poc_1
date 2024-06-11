/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import java.util.Comparator;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;


/**
 * Custom rule sorting based on the attribute value dependencies of the rule.
 * <p>
 * NOTE : This should be used only to sort rules of a single parameter
 *
 * @author bne4cob
 */
class CDRRuleAttrDependencyComparator implements Comparator<ReviewRule> {


  private final ParameterDataProvider parameterDataProvider;

  /**
   * @param parameterDataProvider
   * @param dataProvider APIC Data Provider
   */
  CDRRuleAttrDependencyComparator(final ParameterDataProvider parameterDataProvider) {
    this.parameterDataProvider = parameterDataProvider;
  }

  /**
   * Compare two rules using the text representation
   * <p>
   * {@inheritDoc}
   */
  @Override
  public int compare(final ReviewRule rule1, final ReviewRule rule2) {
    int result;

    boolean r1DepEmpty = (rule1.getDependencyList() == null) || rule1.getDependencyList().isEmpty();
    boolean r2DepEmpty = (rule2.getDependencyList() == null) || rule2.getDependencyList().isEmpty();

    // If one rule is default rule, keep it at the beginning
    if (r1DepEmpty && r2DepEmpty) {
      // A parameter can have atmost one default rule
      result = 0;
    }
    else if (r1DepEmpty) {
      result = -1;
    }
    else if (r2DepEmpty) {
      result = 1;
    }
    else {
      result = ApicUtil.compare(this.parameterDataProvider.getAttrValString(rule1),
          this.parameterDataProvider.getAttrValString(rule2));
    }

    // Safety - if dependencies are same(unexpected scenario), compare with rule ID
    if (result == 0) {
      result = ApicUtil.compare(rule1.getRuleId(), rule2.getRuleId());
    }

    return result;
  }


}
