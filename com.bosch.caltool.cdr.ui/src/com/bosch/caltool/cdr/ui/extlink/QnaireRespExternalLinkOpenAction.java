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
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author pdh2cob
 */
public class QnaireRespExternalLinkOpenAction extends AbstractExternalLinkOpenAction {


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
    String qnaireRespIdStr = properties.get(EXTERNAL_LINK_TYPE.QNAIRE_RESPONSE.getKey());
    // separate input link to identify the qnaire resp ID and variant Id
    String[] strIds = qnaireRespIdStr.split(DELIMITER_MULTIPLE_ID);

    // Check for variant id in the input
    // validate the input link and show error if the link is invalid
    if ((strIds == null) || ((strIds.length != CMD_ARGS_SIZE) && (strIds.length != CMD_ARGS_SIZE_WITH_VAR))) {
      CDMLogger.getInstance().errorDialog("Invalid hyperlink for Questionnaire Response!", Activator.PLUGIN_ID);
      return false;
    }
    Long qNaireRespId = Long.valueOf(strIds[0]);

    Long variantId = null;

    if (strIds.length == CMD_ARGS_SIZE_WITH_VAR) {
      variantId = Long.valueOf(strIds[2]);
    }

    // if it is a valid link , open qNaire response editor
    try {
      PidcVariant pidcVariant = variantId == null ? null : getPidcVariant(variantId);
      new CdrActionSet().openQuestionnaireResponseEditor(qNaireRespId, pidcVariant);
      return true;
    }
    catch (NumberFormatException exp) {
      logInvalidUrlNumberError(exp);
    }

    return false;

  }


  /**
   * @param varId variant id
   * @return Pidcvariant object
   */
  private PidcVariant getPidcVariant(final Long varId) {
    try {
      return new PidcVariantServiceClient().getById(varId);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return null;
  }

}
