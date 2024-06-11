/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import java.util.List;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.calcomp.ssd.api.client.model.RuleValidationInputModel;
import com.bosch.calcomp.ssd.api.client.model.RuleValidationOutputModel;
import com.bosch.calcomp.ssd.api.service.client.RuleValidationClient;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author OZY1KOR
 */
public class RuleBO {


  /**
   * @param ruleText RuleText
   * @return string theErrors
   */
  public static String validateComplexRule(final String ruleText) {

    StringBuilder ruleErrorSb = new StringBuilder();

    try {
      RuleValidationInputModel input = new RuleValidationInputModel(ruleText);
      ObjectMapper objectMapper = new ObjectMapper();
      String ruleTextJson = objectMapper.writeValueAsString(input);
      String targetUrl = new CommonDataBO().getParameterValue(CommonParamKey.COMPLEX_RULE_VALIDATION_URL);
      RuleValidationClient client = new RuleValidationClient();
      List<RuleValidationOutputModel> ruleErrorsList = client.getRuleValidation(ruleTextJson, targetUrl);
      for (RuleValidationOutputModel ruleValidationOutputModel : ruleErrorsList) {
        ruleErrorSb
            .append("Line " + ruleValidationOutputModel.getLineNo() + " : " + ruleValidationOutputModel.getMessage());
        ruleErrorSb.append(System.getProperty("line.separator"));
      }
    }
    catch (Exception e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }

    return ruleErrorSb.toString();
  }
}