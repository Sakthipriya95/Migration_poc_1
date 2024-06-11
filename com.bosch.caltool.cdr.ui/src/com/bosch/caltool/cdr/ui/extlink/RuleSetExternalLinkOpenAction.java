/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.extlink;

import java.util.Map;

import org.eclipse.ui.PartInitException;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.icdm.common.bo.general.EXTERNAL_LINK_TYPE;
import com.bosch.caltool.icdm.common.ui.AbstractExternalLinkOpenAction;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.ruleseditor.actions.ReviewActionSet;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditorInput;
import com.bosch.caltool.icdm.ws.rest.client.a2l.RuleSetParameterServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RuleSetServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Open a Rule set link
 *
 * @author bne4cob
 */
// ICDM-1649
public class RuleSetExternalLinkOpenAction extends AbstractExternalLinkOpenAction {

  /**
   * Open the rule set in editor using the rule set ID from URL
   * <p>
   * {@inheritDoc}
   */
  @Override
  public boolean openLink(final Map<String, String> properties) {
    try {
      // get the ruleset ID from the input link
      String ruleSetProperties = properties.get(EXTERNAL_LINK_TYPE.RULE_SET.getKey());
      String ruleSetId = null;
      String ruleSetParamId = null;
      String ruleId = null;

      RuleSetParameter ruleSetParameter = null;
      if (ruleSetProperties.contains("-")) {
        String[] ruleSetLink = ruleSetProperties.split("-");
        ruleSetId = ruleSetLink[0];
        ruleSetParamId = ruleSetLink[1];
        ruleId = ruleSetLink[2];
        ruleSetParameter = new RuleSetParameterServiceClient().get(Long.valueOf(ruleSetParamId));
      }
      else {
        ruleSetId = ruleSetProperties;
      }

      CDMLogger.getInstance().debug("Opening the Ruleset Editor {}", ruleSetProperties);
      ReviewActionSet paramActionSet = new ReviewActionSet();

      // get the Rule set object from the cache using the rule set id.
      RuleSet ruleSet = new RuleSetServiceClient().get(Long.valueOf(ruleSetId));

      // Open the rule set editor.
      ReviewParamEditorInput input = new ReviewParamEditorInput(ruleSet);
      if (ruleId != null) {
        input.setRuleId(Long.valueOf(ruleId));
        input.setCdrFuncParam(ruleSetParameter);
      }
      paramActionSet.openRulesEditor(input, ruleSetParameter);
      return true;
    }
    catch (PartInitException excep) {
      CDMLogger.getInstance().error(excep.getMessage(), excep, Activator.PLUGIN_ID);
    }
    catch (NumberFormatException exp) {
      logInvalidUrlNumberError(exp);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return false;
  }

}
