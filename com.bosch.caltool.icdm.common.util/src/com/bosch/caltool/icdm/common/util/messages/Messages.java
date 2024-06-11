/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.util.messages;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;

/**
 * @author adn1cob
 */
public final class Messages {

  /**
   * Token Key for icdm ws server
   */
  public static final String ICDM_WS_SERVER_KEY = "CommonUtils.APIC_WS_SERVER";

  /**
   * Token Key for icdm ws token
   */
  public static final String ICDM_WS_TOKEN_KEY = "ICDM.WS_TOKEN_KEY";

  /**
   * Token Key for iCDM intial User
   */
  public static final String ICDM_STARTUP_LOGIN_USER = "ICDM.STARTUP_LOGIN_USER";

  private static final String BUNDLE_NAME = "com.bosch.caltool.icdm.common.util.messages.messages"; //$NON-NLS-1$

  private static ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

  private Messages() {}

  /**
   * @param key key
   * @return string for the key
   */
  public static String getString(final String key) {
    return getString(RESOURCE_BUNDLE, key);
  }

  /**
   * Sets the Resource Bundle to a file located in the file system by using the Java class UrlCLassLoader. Needed to set
   * the Resource Bundle location for the iCDM webservice. The filename of the Resource Bundle file is always
   * 'messages.properties'. The file name must not be added to the passed path.
   *
   * @param filePath the full qualified path to the resource bundle file. The file name must not be added to the path.
   */
  public static void setResourceBundleFile(final String filePath) {
    try {
      final File file = new File(filePath);
      final URL[] urls = { file.toURI().toURL() };
      final ClassLoader loader = new URLClassLoader(urls);
      final ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.getDefault(), loader);

      Messages.RESOURCE_BUNDLE = resourceBundle;

      CDMLogger.getInstance().info("External Resourcebundle at " + filePath + " loaded succesfully.");
    }
    catch (MalformedURLException exp) {
      CDMLogger.getInstance().error("External Resourcebundle at " + filePath + " could not be loaded.", exp);
    }
  }

  /**
   * Gets the message for the given key from the resource bundle. If the key is not available,
   * <code>'!' + key + '!'</code> is returned
   *
   * @param resBundle resource bundle
   * @param key the key
   * @return the message for the given key
   */
  private static String getString(final ResourceBundle resBundle, final String key) {
    String message = null;
    try {
      message = resBundle.getString(key);
    }
    catch (MissingResourceException exp) {
      message = '!' + key + '!';
      CDMLogger.getInstance().error(
          CommonUtils.concatenate("The key ", key, " does not found in the given ResourceBundle ", resBundle), exp);
    }

    return message;
  }


}
