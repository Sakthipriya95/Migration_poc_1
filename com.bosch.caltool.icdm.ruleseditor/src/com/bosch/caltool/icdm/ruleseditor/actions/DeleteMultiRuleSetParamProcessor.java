/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.actions;

import java.util.Set;

import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.ws.rest.client.a2l.RuleSetParameterServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author rgo7cob
 */
public class DeleteMultiRuleSetParamProcessor {


  private final Set<RuleSetParameter> ruleSetParamSet;


  /**
   * @param ruleSetParamSet ruleSetParamSet
   */
  public DeleteMultiRuleSetParamProcessor(final Set<RuleSetParameter> ruleSetParamSet) {
    this.ruleSetParamSet = ruleSetParamSet;

  }


  /**
   * delete Rule set params
   * 
   * @return true if there is any Exception from Server
   */
  public boolean deleteRuleSetParams() {

    boolean hasErrors = false;
    try {
      RuleSetParameterServiceClient ruleSetParamServiceClient = new RuleSetParameterServiceClient();
      ruleSetParamServiceClient.deleteMultiple(this.ruleSetParamSet);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      hasErrors = true;
    }
    return hasErrors;
  }
}

