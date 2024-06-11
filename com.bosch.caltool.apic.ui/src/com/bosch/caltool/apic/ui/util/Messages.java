package com.bosch.caltool.apic.ui.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.icdm.logger.CDMLogger;

/**
 * @author rgo7cob Class for messages.properties
 */
public final class Messages {

  private static final String BUNDLE_NAME = "com.bosch.caltool.apic.ui.util.messages"; //$NON-NLS-1$

  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

  private Messages() {}

  /**
   * @param key key value in the Prop file
   * @return the value in the Properites file
   */
  public static String getString(final String key) {
    // get the message from the resource bundle
    try {
      return RESOURCE_BUNDLE.getString(key);
    }
    catch (MissingResourceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      return '!' + key + '!';
    }
  }
}
