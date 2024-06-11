/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.extlink;

import com.bosch.calcomp.externallink.ILoggingCustomization;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.product.Activator;


/**
 * Logging customization to use CDM Logger's message dialog
 *
 * @author bne4cob
 */
// ICDM-1649
public class ExtLinkLoggingCustomization implements ILoggingCustomization {

  /**
   * {@inheritDoc}
   */
  @Override
  public void showErrorDialog(final String message, final Exception exp) {
    CDMLogger.getInstance().errorDialog(message, exp, Activator.PLUGIN_ID);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void showError(final String message, final Exception exp) {
    CDMLogger.getInstance().error(message, exp, Activator.PLUGIN_ID);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void showInfoDialog(final String message) {
    CDMLogger.getInstance().infoDialog(message, Activator.PLUGIN_ID);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void showInfo(final String message) {
    CDMLogger.getInstance().info(message, Activator.PLUGIN_ID);

  }

}
