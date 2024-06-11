/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.utils;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;

/**
 * @author dmo5cob
 */
public final class Messages {

  /**
   * the current bundle name
   */
  private static final String BUNDLE_NAME = "com.bosch.caltool.icdm.ruleseditor.utils.messages"; //$NON-NLS-1$

  /**
   * the resource bundle
   */
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

  /**
   * giving access restriction 
   */
  private Messages() {}

  /**
   * @param key key
   * @return key
   */
  public static String getString(final String key) {
    // TODO Auto-generated method stub
    try {
      return RESOURCE_BUNDLE.getString(key);
    }
    catch (MissingResourceException exp) {
      CDMLogger.getInstance().error(
          CommonUtils.concatenate("The key ", key, " does not found in the given ResourceBundle ", RESOURCE_BUNDLE),
          exp);
      return '!' + key + '!';
    }
  }
}
