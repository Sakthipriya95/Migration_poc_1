/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.extlink;

import java.util.Map;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.cdr.ui.actions.CdrActionSet;
import com.bosch.caltool.icdm.common.bo.general.EXTERNAL_LINK_TYPE;
import com.bosch.caltool.icdm.common.ui.AbstractExternalLinkOpenAction;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CDRReviewResultServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Open a CDR Result Link
 *
 * @author bne4cob
 */
// ICDM-1649
public class CdrResultExternalLinkOpenAction extends AbstractExternalLinkOpenAction {


  /**
   * Define size for the cmd args
   */
  private static final int CMD_ARGS_SIZE = 2;

  /**
   * Define size for the cmd args
   */
  private static final int CMD_ARGS_SIZE_WITH_VAR = 3;

  /**
   * Index of variant id
   */
  private static final int ARRIDX_VAR_ID = 2;


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean openLink(final Map<String, String> properties) {
    String cdrID = properties.get(EXTERNAL_LINK_TYPE.CDR_RESULT.getKey());
    // separate input link to identify the result ID and review variant ID
    String[] strIds = cdrID.split(DELIMITER_MULTIPLE_ID);

    // Check for variant id in the input for checking the cdr result
    // validate the input link and show error if the link is invalid
    if ((strIds == null) || ((strIds.length != CMD_ARGS_SIZE) && (strIds.length != CMD_ARGS_SIZE_WITH_VAR))) {
      CDMLogger.getInstance().errorDialog("Invalid hyperlink for CDR Result!", Activator.PLUGIN_ID);
      return false;
    }
    // if it is a valid link , open resutl editor
    try {
      if (strIds.length == CMD_ARGS_SIZE_WITH_VAR) {
        RvwVariant rvwVar = new RvwVariantServiceClient().getRvwVariantByResultNVarId(Long.valueOf(strIds[0]),
            Long.valueOf(strIds[ARRIDX_VAR_ID]));
        new CdrActionSet().openReviewResultEditor(rvwVar, null, null, null);
      }
      else {
        // Get cdr result
        CDRReviewResult cdrResult = new CDRReviewResultServiceClient().getById(Long.valueOf(strIds[0]));
        // open review result
        new CdrActionSet().openReviewResultEditor(null, null, cdrResult, null);
      }
      return true;

    }
    catch (NumberFormatException exp) {
      logInvalidUrlNumberError(exp);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }

    return false;

  }

}

