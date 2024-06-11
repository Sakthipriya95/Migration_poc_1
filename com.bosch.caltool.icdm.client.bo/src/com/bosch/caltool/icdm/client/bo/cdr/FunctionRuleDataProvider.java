/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import java.util.List;
import java.util.Map;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.SSDMessageWrapper;
import com.bosch.caltool.icdm.model.cdr.ConfigBasedRuleInput;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.ReviewRuleParamCol;
import com.bosch.caltool.icdm.ws.rest.client.cdr.ReviewRuleServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author rgo7cob
 */
public class FunctionRuleDataProvider implements ReviewRuleDataProvider<Function> {


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


  @Override
  public SSDMessageWrapper createCdrRule(final ReviewRuleParamCol<Function> reviewRuleParamCol) {
    ReviewRuleServiceClient client = new ReviewRuleServiceClient();
    SSDMessageWrapper wrapper = null;
    try {
      wrapper = client.create(reviewRuleParamCol);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(ERROR_WHILE_CREATING_RULE + exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return wrapper;
  }

  @Override
  public SSDMessageWrapper updateCdrRule(final ReviewRuleParamCol<Function> reviewRuleParamCol) {
    ReviewRuleServiceClient client = new ReviewRuleServiceClient();
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
  public SSDMessageWrapper deleteRule(final ReviewRuleParamCol<Function> reviewRuleParamCol) {
    ReviewRuleServiceClient client = new ReviewRuleServiceClient();
    try {
      client.delete(reviewRuleParamCol);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(ERROR_WHILE_DELETTING_RULE + exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return null;
  }

  @Override
  public ReviewRuleParamCol readRules(final ReviewRuleParamCol<Function> reviewRuleParamCol) {
    ReviewRuleServiceClient client = new ReviewRuleServiceClient();

    try {
      return client.readRules(reviewRuleParamCol);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(ERROR_WHILE_READING_RULE + exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return null;
  }

  @Override
  public Map<String, List<ReviewRule>> searchRuleForDep(final ConfigBasedRuleInput<Function> configInput) {
    ReviewRuleServiceClient client = new ReviewRuleServiceClient();
    try {
      return client.searchRuleForDep(configInput);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(ERROR_WHILE_READING_RULE + exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return null;
  }


}
