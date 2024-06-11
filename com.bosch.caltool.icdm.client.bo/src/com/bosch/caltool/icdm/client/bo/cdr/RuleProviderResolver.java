/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.RuleSet;

/**
 * @author rgo7cob
 */
public class RuleProviderResolver {


  /**
   * Method to get the specific data provider
   * 
   * @param paramCollection paramCollection
   * @return the Review Rule Provider
   */
  public ReviewRuleDataProvider getRuleProvider(final ParamCollection paramCollection) {

    if (paramCollection instanceof Function) {
      return new FunctionRuleDataProvider();
    }
    if (paramCollection instanceof RuleSet) {
      return new RuleSetRuleProvider();
    }

    return new CompPackageRuleProvider();
  }

}
