/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.rules.bo;

import java.util.Comparator;

import com.bosch.caltool.apic.jpa.bo.ApicBOUtil;
import com.bosch.caltool.apic.jpa.bo.ApicDataProvider;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.ssd.icdm.model.CDRRule;


/**
 * Custom rule sorting based on the attribute value dependencies of the rule.
 * <p>
 * NOTE : This should be used only to sort rules of a single parameter
 *
 * @author bne4cob
 */
@Deprecated
class CDRRuleAttrDependencyComparator implements Comparator<CDRRule> {

  /**
   * APIC Data Provider
   */
  private final ApicDataProvider dataProvider;

  /**
   * @param dataProvider APIC Data Provider
   */
  @Deprecated
  CDRRuleAttrDependencyComparator(final ApicDataProvider dataProvider) {
    this.dataProvider = dataProvider;
  }

  /**
   * Compare two rules using the text representation
   * <p>
   * {@inheritDoc}
   */
  @Override
  @Deprecated
  public int compare(final CDRRule rule1, final CDRRule rule2) {
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
      // Both are rules with dependency
      result = ApicUtil.compare(ApicBOUtil.createRulesAttrVal(rule1, this.dataProvider),
          ApicBOUtil.createRulesAttrVal(rule2, this.dataProvider));
    }

    // Safety - if dependencies are same(unexpected scenario), compare with rule ID
    if (result == 0) {
      result = ApicUtil.compare(rule1.getRuleId(), rule2.getRuleId());
    }

    return result;
  }

}
