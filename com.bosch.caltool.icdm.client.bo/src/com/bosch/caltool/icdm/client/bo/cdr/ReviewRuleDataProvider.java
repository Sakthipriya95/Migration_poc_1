/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import java.util.List;
import java.util.Map;

import com.bosch.caltool.icdm.model.a2l.SSDMessageWrapper;
import com.bosch.caltool.icdm.model.cdr.ConfigBasedRuleInput;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.ReviewRuleParamCol;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author rgo7cob
 */
public interface ReviewRuleDataProvider<C extends ParamCollection> {

  // Method to create cdr rule
  public SSDMessageWrapper createCdrRule(final ReviewRuleParamCol<C> reviewRuleParamCol);

  // Method to update cdr rule
  public SSDMessageWrapper updateCdrRule(final ReviewRuleParamCol<C> reviewRuleParamCol);

  // Method to delete cdr rule
  public SSDMessageWrapper deleteRule(final ReviewRuleParamCol<C> reviewRuleParamCol);

  // Method to read cdr rule
  public ReviewRuleParamCol readRules(final ReviewRuleParamCol<C> reviewRuleParamCol);

  // Method to search rule dependencies
  public Map<String, List<ReviewRule>> searchRuleForDep(final ConfigBasedRuleInput<C> configInput)
      throws ApicWebServiceException;

}
