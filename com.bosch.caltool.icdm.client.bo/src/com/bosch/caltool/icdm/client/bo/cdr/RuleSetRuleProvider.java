/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import java.util.List;
import java.util.Map;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.SSDMessageWrapper;
import com.bosch.caltool.icdm.model.cdr.ConfigBasedRuleInput;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.ReviewRuleParamCol;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RuleSetRuleServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author rgo7cob
 */
public class RuleSetRuleProvider implements ReviewRuleDataProvider<RuleSet> {


  /**
   * String constant for error while creating a new rule
   */
  private static final String ERROR_WHILE_CREATING_RULE = "Error while creating rule(s) : ";
  /**
   * String constant for error while deleting a new rule
   */
  private static final String ERROR_WHILE_DELETTING_RULE = "Error while deleting rule(s) : ";
  private static final String ERROR_WHILE_UPDATING_RULE = "Error while updating rule(s) : ";
  private static final String ERROR_WHILE_READING_RULE = "Error while retrieving rule(s) : ";


  /**
   * @param reviewRuleParamCol reviewRuleParamCol
   * @return SSD Message Wrapper
   */
  @Override
  public SSDMessageWrapper createCdrRule(final ReviewRuleParamCol<RuleSet> reviewRuleParamCol) {
    RuleSetRuleServiceClient client = new RuleSetRuleServiceClient();
    SSDMessageWrapper wrapper = null;
    try {
      wrapper = client.create(reviewRuleParamCol);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(ERROR_WHILE_CREATING_RULE + exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return wrapper;
  }


  /**
   * @param reviewRuleParamCol reviewRuleParamCol
   * @return SSD Message Wrapper
   */
  @Override
  public SSDMessageWrapper updateCdrRule(final ReviewRuleParamCol<RuleSet> reviewRuleParamCol) {
    RuleSetRuleServiceClient client = new RuleSetRuleServiceClient();
    SSDMessageWrapper message = null;
    try {
      message = client.update(reviewRuleParamCol);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(ERROR_WHILE_UPDATING_RULE + exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return message;
  }

  @Override
  public SSDMessageWrapper deleteRule(final ReviewRuleParamCol<RuleSet> reviewRuleParamCol) {
    RuleSetRuleServiceClient client = new RuleSetRuleServiceClient();
    try {
      client.delete(reviewRuleParamCol);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(ERROR_WHILE_DELETTING_RULE + exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return null;
  }

  @Override
  public ReviewRuleParamCol readRules(final ReviewRuleParamCol<RuleSet> reviewRuleParamCol) {
    RuleSetRuleServiceClient client = new RuleSetRuleServiceClient();

    try {
      return client.readRules(reviewRuleParamCol);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(ERROR_WHILE_READING_RULE + exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, List<ReviewRule>> searchRuleForDep(final ConfigBasedRuleInput<RuleSet> configInput) {
    RuleSetRuleServiceClient client = new RuleSetRuleServiceClient();

    try {
      return client.searchRuleForDep(configInput);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(ERROR_WHILE_READING_RULE + exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return null;
  }

}
