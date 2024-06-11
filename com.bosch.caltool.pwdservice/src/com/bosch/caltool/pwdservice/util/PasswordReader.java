/**
 * 
 */
package com.bosch.caltool.pwdservice.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.bosch.calcomp.adapter.logger.util.LoggerUtil;

/**
 * @author vau3cob
 */
public class PasswordReader {

  private static ResourceBundle resourceBundle;

  /**
   * @param passwordName
   * @return password if available else null
   */
  @SuppressWarnings("javadoc")
  public static String getPassword(String passwordName) {
    String password = null;
    setResourceBundleFile(System.getProperty("PropertiesFilePath"));
    if (resourceBundle != null) {
      try {
        password = resourceBundle.getString(passwordName);
      }
      catch (NullPointerException | ClassCastException | MissingResourceException e) {
        LoggerUtil.getLogger().error("Error while getting password. " + e.getLocalizedMessage());
      }
    }
    return password;
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
      resourceBundle = ResourceBundle.getBundle("password", Locale.getDefault(), loader);
    }
    catch (MalformedURLException exp) {
      LoggerUtil.getLogger().error("Error while loading password file. " + exp.getLocalizedMessage());
    }
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    PasswordReader.getPassword("ESM4SERVLET");
  }
}
