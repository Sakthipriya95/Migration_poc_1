/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.review;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bosch.ssd.icdm.model.CDRRule;

/**
 * @author rgo7cob
 */
public class RulesValidator {

  /**
   * @param ruleMap1
   * @param ruleMap2
   * @return
   */
  public boolean validateRules(final Map<String, List<CDRRule>> ruleMap1, final Map<String, List<CDRRule>> ruleMap2) {

    // if the size is not same retuen false
    if (ruleMap1.size() != ruleMap2.size()) {
      return false;
    }

    Set<String> ruleKeySet = ruleMap1.keySet();

    // get the key set of rulemap1
    for (String rule1Key : ruleKeySet) {


      List<CDRRule> rule1 = ruleMap1.get(rule1Key);
      List<CDRRule> rule2 = ruleMap2.get(rule1Key);


      if (rule1 != null) {
        if (rule2 == null) {
          return false;
        }
        CDRRule cdrRule1 = rule1.get(0);
        CDRRule cdrRule2 = rule2.get(0);
        // Compare rules based on rule id
        if (!cdrRule1.getRuleId().equals(cdrRule2.getRuleId())) {
          return false;
        }


      }

    }


    return true;

  }


}
