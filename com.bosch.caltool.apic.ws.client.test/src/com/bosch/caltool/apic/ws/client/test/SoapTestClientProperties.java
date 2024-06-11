/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.test;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;

/**
 * @author pdh2cob
 */
public final class SoapTestClientProperties {

  /**
   * Token Key for ws server
   */
  public static final String WS_SERVER_KEY = "ICDM_SERVER";

  /**
   * Token Key for ws token
   */
  public static final String WS_TOKEN_KEY = "WS_TOKEN_KEY";

  /**
   * User name for authentication
   */
  public static final String DEF_TEST_USER = "DEFAULT_TEST_USER";

  private static final String BUNDLE_NAME = "com.bosch.caltool.apic.ws.client.test.SoapTestConfig";

  private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault());

  private SoapTestClientProperties() {}

  /**
   * @param key key
   * @return string for the key
   */
  public static String getValue(final String key) {
    return getValue(resourceBundle, key);
  }

  /**
   * Gets the message for the given key from the resource bundle. If the key is not available,
   * <code>'!' + key + '!'</code> is returned
   *
   * @param resBundle resource bundle
   * @param key the key
   * @return the message for the given key
   */
  private static String getValue(final ResourceBundle resBundle, final String key) {
    String val = null;
    try {
      val = resBundle.getString(key);
    }
    catch (MissingResourceException exp) {
      val = '!' + key + '!';
      CDMLogger.getInstance().error(CommonUtils.concatenate("The key ", key, " not found"), exp);
    }

    return val;
  }


}
