/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.CRC32;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.logging.log4j.ThreadContext;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.bosch.calcomp.externallink.LinkRegistry;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldataphy.CalDataPhy;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.logger.CDMLogger;


/**
 * This class provides utility methods
 */
/**
 * @author DMO5COB
 */
public class CommonUtils {

  /**
   * Place holder for message parameters
   */
  private static final String MSG_PARAM_PLACE_HOLDER = "{}";
  /**
   * Length of message parameter place holder
   */
  private static final int MSG_PARAM_PLACE_HOLDER_LEN = MSG_PARAM_PLACE_HOLDER.length();
  /**
   * ICDM client string
   */
  private static final String STR_ICDM_CLIENT = "iCDM Client";

  /**
   * Error message while reading file path
   */
  private static final String IO_ERR_FILE_PATH = "IO error while reading file path";

  /**
   * EADM excluded characters (Regex pattern)
   */
  private static final String EADM_NAME_EXCLD_CHARS = "[ÄÖÜäöü/ß\\s]";

  /**
   * size of mail prefix
   */
  private static final int MAIL_PREFIX_SIZE = 7;

  /**
   * Null Text
   */
  private static final String NULL_TEXT = "null";

  /**
   * OS Name
   */
  private static final String OS_NAME = Messages.getString("CommonUtils.OS_NAME"); //$NON-NLS-1$
  /**
   * User directory
   */
  private static final String USER_DIR = Messages.getString("CommonUtils.USER_DIR"); //$NON-NLS-1$
  /**
   * Java Temp directory
   */
  private static final String JAVA_IO_TMPDIR = Messages.getString("CommonUtils.JAVA_IO_TEMPDIR"); //$NON-NLS-1$
  /**
   * APPDATA dirctory
   */
  private static final String APPDATA_DIR = Messages.getString("CommonUtils.APPDATA_DIR"); //$NON-NLS-1$
  /**
   * Plugin Root directory
   */
  private static final String ROOT = Messages.getString("CommonUtils.SLASH"); //$NON-NLS-1$

  // iCDM-400
  /**
   * Prefix for a shared path (hyperlink)
   */
  private static final String SHARED_PATH_PREFIX = "\\";
  /**
   * Prefix for a file path (hyperlink)
   */
  private static final String FILE_PATH_PREFIX = "file://";

  /**
   * iCDM-849 <br>
   * Constant for preference key to store cdm studio path
   */
  public static final String CDM_STUDIO_INS_PATH = "cdmStudioPath";

  /**
   * Constant for storing CDR Review Import file extn
   */
  public static final String CDR_RVW_FILE_EXTN = "cdrReviewImportFileExtn";

  private static final String MAIL_PREFIX = "mailto:";

  /**
   * CDR Review File name - Preference store key
   */
  public static final String CDR_RVW_FILE_NAME = "cdrReviewImportFileName";
  /**
   * Constant for storing Ref Value Import file extn
   */
  public static final String REFVALUE_IMPORT_FILE_EXTN = "refValImportImportFileExtn";
  /**
   * Constant for storing Ref Value Import file name
   */
  public static final String REFVALUE_IMPORT_FILE_NAME = "refValImportImportFileName";

  /**
   * constant for storing lab fun file extn
   */
  public static final String IMPORT_LAB_FUN_FILES_EXTN = "importLabFunFilesExtn";

  /**
   * constant to store lab fun file name
   */
  public static final String IMPORT_LAB_FUN_FILES_NAME = "importLabFunFileName";


  /**
   * iCDM-1241 define constant to handle storing link editor preferences
   */
  public static final String LINK_WITH_EDITOR_PREF = "linkWithEditorPref";

  /**
   * create initial byte buffer size for ziping the input files
   */
  private static final int CREATE_BYTE_BUFFER_SIZE = 1024;

  /**
   * Percentage value
   */
  private static final int PERCENTAGE_VALUE = 100;

  /**
   * stores whether focus matrix view can be shown or hidden
   */
  public static final String FM_EDIT_VIEW = "fmEditView";

  /**
  *
  */
  public static final String VCDM_DELIMITER = ":";
  /**
   * Constant for Dst not available for varaint name
   */
  public static final String DST_NOT_AVAILABLE = "No DST's available for the selected variant name";


  /**
   * Private constructor for utility class
   */
  private CommonUtils() {
    // Private constructor for utility class
  }

  /**
   * Gets the product install location
   *
   * @return String
   */
  public static String getInstallLocation() {
    URL url = Platform.getInstallLocation().getURL();
    Path path = new Path(url.getPath());
    return path.toString();
  }

  /**
   * While using this method it takes the argument of the Activator class PLUGIN_ID and it returns the Plug-in folder
   * path.
   *
   * @param pluginID the plugin's ID
   * @return String
   */
  public static String getPluginFolderPath(final String pluginID) {
    IPath pluginFolderIPath;
    URL url = Platform.getBundle(pluginID).getEntry(ROOT);
    try {
      url = FileLocator.resolve(url);
    }
    catch (IOException ex) {
      CDMLogger.getInstance().error(concatenate(IO_ERR_FILE_PATH, ex.getLocalizedMessage()), ex, STR_ICDM_CLIENT);
    }
    pluginFolderIPath = new Path(url.getPath());
    return pluginFolderIPath.toString();
  }

  /**
   * Gets the current Operating System name
   *
   * @return String
   */
  public static String getOSType() {
    return Platform.getOS();
  }

  /**
   * Gets the current Operating System architecture
   *
   * @return String
   */
  public static String getOSArchType() {
    return Platform.getOSArch();
  }

  /**
   * Gets the CommandLine arguments
   *
   * @return String[]
   */
  public static String[] getCommandLineArgs() {
    return Platform.getCommandLineArgs();
  }

  /**
   * Gets the log file path
   *
   * @return String
   */
  public static String getLogFilePath() {
    IPath logFileIPath = Platform.getLogFileLocation();
    File file = logFileIPath.toFile();
    return file.getAbsolutePath();
  }

  /**
   * Gets the current using directory path
   *
   * @return String
   */
  public static String getUserDirPath() {
    return System.getProperty(USER_DIR);
  }

  /**
   * Gets the Operating System name
   *
   * @return String
   */
  public static String getOSName() {
    return System.getProperty(OS_NAME).toLowerCase(Locale.getDefault());
  }

  /**
   * Gets the product install location
   *
   * @return String
   */
  public static String getSystemUserTempDirPath() {
    return System.getProperty(JAVA_IO_TMPDIR);
  }

  /**
   * Gets user APPDATA path.
   *
   * @return user's APPDATA path
   */
  private static String getSystemUserAppDataPath() {
    return System.getenv(APPDATA_DIR);
  }

  /**
   * Method to return ICDM Users Workspace file path
   *
   * @return proFile
   */
  public static String getICDMWorkspaceFilePath() {
    return getSystemUserAppDataPath() + File.separator + Messages.getString("CommonUtils.ICDM_PROP_FILE_SUBDIR");
  }

  /**
   * Method to return ICDM Log Directory path
   *
   * @return logDir
   */
  public static String getICDMLogDirectoryDefaultPath() {
    String logDir = concatenate(getSystemUserTempDirPath(), Messages.getString("CommonUtils.ICDM_LOG_FILE_SUBDIR"));
    File dir = new File(logDir);
    if (!dir.exists() && !createDir(logDir)) {
      CDMLogger.getInstance().warn("WARNING!!! Unable to create sub-directory inside users TEMP dir");
    }
    return logDir;
  }

  /**
   * Method to return ICDM Log Directory path
   *
   * @return logDir
   */
  public static String getICDMTmpFileDirectoryPath() {
    String tempFilesDir = getICDMLogDirectoryDefaultPath() + File.separator + "Files";
    File dir = new File(tempFilesDir);
    if (!dir.exists() && !createDir(tempFilesDir)) {
      CDMLogger.getInstance().warn("WARNING!!! Unable to create sub-directory inside users TEMP dir");
    }
    return tempFilesDir;
  }

  /**
   * Method to return ICDM Properties file path
   *
   * @return proFile
   */
  public static String getICDMPropertiesFilePath() {
    String proFile =
        concatenate(getSystemUserAppDataPath(), File.separator, Messages.getString("CommonUtils.ICDM_PROP_FILE_SUBDIR"),
            File.separator, Messages.getString("CommonUtils.ICDM_PROPERTIES_FILENAME"));

    File file = new File(proFile);
    if (!file.exists()) {
      if (!createICDMPropFile(file)) {
        CDMLogger.getInstance().warn("Unable to create sub-directory inside users TEMP dir", STR_ICDM_CLIENT);
        return null;
      }
      CDMLogger.getInstance().info(concatenate("iCDM.properties has been created in the following path : ", proFile),
          STR_ICDM_CLIENT);
    }
    return proFile;
  }

  /**
   * Method to create a directory
   *
   * @param dir the directory path
   * @return true if directory is created, false otherwise
   */
  private static boolean createDir(final String dir) {
    return new File(dir).mkdir();
  }

  /**
   * Method to create properties file, if not exists.
   *
   * @param filePath the path of the file
   * @return true if file is created, false otherwise
   */
  private static boolean createICDMPropFile(final File filePath) {

    File dir = filePath.getParentFile();
    dir.mkdirs();

    File propFile = new File(filePath.getAbsolutePath());
    try {
      propFile.createNewFile();
    }
    catch (IOException exception) {
      // removed the exception trace line and used the exception for logging
      CDMLogger.getInstance().warn(
          concatenate("WARNING!!! Unable to create new iCDM.properties file under the users APPDATA dir", filePath),
          exception);
      return false;
    }
    // check if file is created
    return filePath.exists();
  }


  /**
   * Verifies whether the file is available.
   *
   * @param filePath the file path
   * @return true if the file is available, else false.
   */
  public static boolean isFileAvailable(final String filePath) {
    boolean fileAvailable = false;
    if (CommonUtils.isNotEmptyString(filePath)) {
      File tempFile = new File(FilenameUtils.getFullPath(filePath), FilenameUtils.getName(filePath));
      fileAvailable = true;
      if (!tempFile.exists() || (tempFile.length() <= 0L)) {
        return false;
      }
    }
    return fileAvailable;
  }

  /**
   * @param filePath filePath
   * @return true if the file is readable
   */
  public static boolean isFileReadable(final String filePath) {
    boolean returnVal = true;
    File tempFile = new File(filePath);

    // changing the input stream declaration as per Java-7 standard
    try (FileInputStream input = new FileInputStream(tempFile);) {
      CDMLogger.getInstance().info(concatenate("The file path ", filePath, " has been read"));
    }
    catch (IOException exp) {
      CDMLogger.getInstance().error(concatenate(IO_ERR_FILE_PATH, exp));
      returnVal = false;
    }
    return returnVal;
  }

  /**
   * This method is used to delete the folder given in the filePath
   *
   * @param filePath filePath
   * @throws IOException IOException
   */
  public static void deleteFile(final String filePath) throws IOException {
    if (isFileAvailable(filePath)) {
      FileUtils.deleteDirectory(new File(filePath));
    }
  }

  /**
   * Returns whether the string variable has contents. Use this method instead of the check<br>
   * <code> if(str == null || str.trim().equals("")){<br>
   *    do something<br>
   * }
   * </code>
   *
   * @param string the string to check
   * @return true if input is null or empty, else false
   */
  public static boolean isEmptyString(final String string) {
    if (string == null) {
      return true;
    }

    for (int index = 0; index < string.length(); index++) {
      if (!Character.isWhitespace(string.codePointAt(index))) {
        return false;
      }
    }

    return true;
  }


  /**
   * returns if the string is not empty.
   *
   * @param string the string to check
   * @return true if the string is not empty.
   */
  public static boolean isNotEmptyString(final String string) {
    return !isEmptyString(string);
  }

  /**
   * This method returns an empty string if it is null.
   *
   * @param str str to be checked
   * @return either input string itself or empty string
   */
  public static String checkNull(final String str) {
    return (null == str) ? "" : str;
  }

  /**
   * This method returns an 0 if the input Integer value is null.
   *
   * @param input integer value to be checked
   * @return either input integer value or 0
   */
  public static int checkNull(final Integer input) {
    return null == input ? 0 : input;
  }

  /**
   * This method returns <code>replace</code> if <code>obj</code> is <code>null</code>, else <code>obj</code>. <br>
   * Equivalent to <code>NVL()</code> function in oracle
   *
   * @param <O> any object
   * @param obj Object to be checked
   * @param replace object to be returned if <code>obj</code> is <code>null</code>
   * @return <code>obj</code>, if <code>obj!=null</code>; otherwise <code>replace</code>
   */
  public static <O> O checkNull(final O obj, final O replace) {
    return (null == obj) ? replace : obj;
  }

  /**
   * Method returns <code>ifNull</code> if <code>obj</code> == <code>null</code> else returns <code>ifNotNull</code>
   * <p>
   * IMPORTANT : <code>ifNull</code> and <code>ifNotNull</code> arguments should NOT use <code>obj</code>
   *
   * @param obj object to check for null
   * @param ifNull returned if obj=null
   * @param ifNotNull returned if obj!=null
   * @return as given above
   */
  public static <O> O checkNull(final Object obj, final O ifNull, final O ifNotNull) {
    return (null == obj) ? ifNull : ifNotNull;
  }

  /**
   * Checks whether two objects are equal. This will also consider the input(s) being null.
   *
   * @param <O> Any object
   * @param obj1 first object
   * @param obj2 second object
   * @return true/false
   */
  public static <O> boolean isEqual(final O obj1, final O obj2) {
    return Objects.equals(obj1, obj2);
  }


  /**
   * @param obj1 first object
   * @param obj2 second object
   * @return not of the is equal method
   */
  public static <O> boolean isNotEqual(final O obj1, final O obj2) {
    return !isEqual(obj1, obj2);
  }

  /**
   * if inputString needs to be check equality with 'FC', 'BC', 'WP', then matchExpression should be given as 'FC|BC|WP'
   * this is duplicate of inputString.equals('FC') || inputString.equals('BC') || inputString.equals('WP')
   *
   * @param inputString the input string to be matched
   * @param matchExpression the custom match expression
   * @return true if matches
   */
  public static boolean isMatches(final Object inputString, final String matchExpression) {
    if (inputString == null) {
      return false;
    }
    if (inputString instanceof String) {
      return String.valueOf(inputString).matches(matchExpression);
    }
    return false;
  }

  /**
   * This method is inversion of isMatches(inputString, matchExpression)
   *
   * @param inputString the input string to be matched
   * @param matchExpression the custom match expression
   * @return true if matches
   */
  public static boolean isNotMatches(final Object inputString, final String matchExpression) {
    return !isMatches(inputString, matchExpression);
  }

  /**
   * Checks whether the CalDataPhy Objects of the given CalData objects are equal. This will also consider the input(s)
   * being null.
   * <p>
   * Important : If both objects are different, only caldataphy objects verify for equality.
   *
   * @param cd1 first CalData
   * @param cd2 second CalData
   * @return true/false
   */
  public static boolean isEqualCalDataPhy(final CalData cd1, final CalData cd2) {
    if ((cd1 == null) && (cd2 == null)) {
      return true;
    }
    if ((cd1 == null) || (cd2 == null)) {
      // if either of the objects are null
      return false;
    }
    return isEqual(cd1.getCalDataPhy(), cd2.getCalDataPhy());

  }

  /**
   * Checks whether two CalDataPhy objects are equal. This will also consider the input(s) being null. Separate method
   * is provided as the default equals does not verify the CalDataPhy objects
   *
   * @param cdf1 first object
   * @param cdf2 second object
   * @return true/false
   */
  public static boolean isEqual(final CalDataPhy cdf1, final CalDataPhy cdf2) {
    if ((cdf1 == null) && (cdf2 == null)) {
      return true;
    }
    if ((cdf1 == null) || (cdf2 == null)) {
      // if any one obj is null, then they are not equal as per first condition
      return false;
    }
    return cdf1.equals(cdf2);
  }

  /**
   * Checks whether two CalDataPhy objects are equal. It returns false if the types are different. It does not consider
   * name and unit for checking equality It returns true if all the other bits are matching
   *
   * @param cdf1 first object
   * @param cdf2 second object
   * @return true/false
   */
  public static boolean isEqualValue(final CalDataPhy cdf1, final CalDataPhy cdf2) {
    if ((cdf1 == null) && (cdf2 == null)) {
      return true;
    }
    if ((cdf1 == null) || (cdf2 == null)) {
      // if any one obj is null, then they are not equal as per first condition
      return false;
    }
    BitSet equalsExt = cdf1.equalsExt(cdf2);
    if (equalsExt.get(CalDataPhy.typeBit)) {
      // if they are of different types
      return false;
    }
    for (int bitNo = CalDataPhy.instanceBit; bitNo <= CalDataPhy.axisPtsRef; bitNo++) {
      if (equalsExt.get(bitNo) && (bitNo != CalDataPhy.nameBit) && (bitNo != CalDataPhy.unitBit)) {
        return false;
      }
    }
    return true;
  }

  /**
   * String equality check case insensitively. Uses equalsIgnoreCase() method. Checks for null also
   *
   * @param str1 first string
   * @param str2 second string
   * @return true, if strings are equal, case insensitive
   */
  public static boolean isEqualIgnoreCase(final String str1, final String str2) {
    if ((str1 == null) && (str2 == null)) {
      return true;
    }
    if ((str1 == null) || (str2 == null)) {
      // if any one obj is null, then they are not equal as per first condition
      return false;
    }
    return str1.equalsIgnoreCase(str2);
  }


  /**
   * Same method as compareToIgnoreCase in string with null check.
   *
   * @param str1 first string
   * @param str2 second string
   * @return the compare to Igonore case method with null check.
   */
  public static int compareToIgnoreCase(final String str1, final String str2) {
    // Null check handling
    // If str1 is null
    if (str1 == null) {
      return -1;
    }
    // If str2 is null
    if (str2 == null) {
      return 1;
    }
    if (str1.equals(str2)) {
      return 0;
    }
    // Both are not null.
    return str1.compareToIgnoreCase(str2);
  }


  // iCDM-400,Utilities to validate hyperlink
  /**
   * Checks if the URL is valid
   *
   * @param strUrl URL
   * @return true if valid
   */
  public static boolean isValidURLFormat(final String strUrl) {

    if (isEmptyString(strUrl)) {
      return false;
    }
    UrlValidator urlValidator = new UrlValidator();
    // Icdm-1101 adding icdm:pidc as recognized URL format within icdm
    return urlValidator.isValid(strUrl) || strUrl.toLowerCase(Locale.getDefault()).startsWith("easee:") ||
        strUrl.toLowerCase(Locale.getDefault()).startsWith(LinkRegistry.INSTANCE.getProtocolWithSep());

  }

  /**
   * Checks if the name is valid
   *
   * @param value String inserted
   * @return true if valid
   */
  public static boolean isvCDMCompliantName(final String value) {

    if (isEmptyString(value)) {
      return false;
    }
    Pattern pattern = Pattern.compile("[^a-zA-Z0-9_+-]");
    return !pattern.matcher(value).find();

  }

  /**
   * Checks if the path starts with proper format
   *
   * @param strPath Shared path or Local path
   * @return true if valid
   */
  public static boolean isValidLocalPathFormat(final String strPath) {

    if (isEmptyString(strPath)) {
      return false;
    }

    return strPath.startsWith(SHARED_PATH_PREFIX) || (strPath.startsWith(FILE_PATH_PREFIX) /* ICDM-892 */ &&
        !strPath.replace(FILE_PATH_PREFIX, "").trim().contains(":"));

  }

  /**
   * ICDM-934 checks whether the string contains valid double number
   *
   * @param value String
   * @return boolean
   */
  public static boolean isValidDouble(final String value) {
    boolean flag = false;
    try {
      if (!value.isEmpty()) {
        double parseDouble = Double.parseDouble(value);
        CDMLogger.getInstance().info(parseDouble + " is a valid number");
        flag = true;
      }
    }
    catch (NumberFormatException excep) {
      flag = false;
    }
    return flag;
  }

  /**
   * Checks if the Object is null
   *
   * @param obj object any Object for Null Check
   * @return true if the Object is null
   */
  public static <O> boolean isNull(final O obj) {
    return obj == null;
  }

  /**
   * Checks if the Object is not null
   *
   * @param obj object any Object for Null Check
   * @return true if the Object is not null
   */
  public static <O> boolean isNotNull(final O obj) {
    return obj != null;
  }


  /**
   * @param objects List of objects
   * @return true if all the Objects are not null. Can be used if checking not null for multiple objects
   */
  public static boolean isObjectListNotNull(final Object... objects) {
    for (Object object : objects) {
      if (isNull(object)) {
        return false;
      }
    }
    return true;
  }

  /**
   * checks if the file can be written to the given path
   *
   * @param filePath - path of the file with Extension
   * @param extension - provide the extension for the file user has to provide the input
   * @return a boolean indicating whether the path given is valid and file can be written in it
   */
  public static boolean isWritableFilePath(final String filePath, final String extension) {

    // If either the file path is empty or extenstion is empty then return false
    if (CommonUtils.isEmptyString(extension) || CommonUtils.isEmptyString(filePath)) {
      return false;
    }

    final File file = new File(filePath);
    // If the file path is not a directory and ends with the extenstion given and write acccess is given
    if (!file.isDirectory() && filePath.endsWith(extension)) {
      final File directory = file.getParentFile();
      // If the directory is existing and it is a directory
      if (directory.exists() && directory.isDirectory()) {
        return true;
      }
    }
    return false;
  }

  /**
   * checks whether the directory path is valid
   *
   * @param dirPath - path of the file with Extension
   * @return a boolean indicating whether the path given is valid and file can be written in it
   */
  public static boolean isValidDirectory(final String dirPath) {
    // If either the file path is empty or extenstion is empty then return false
    if (CommonUtils.isEmptyString(dirPath)) {
      return false;
    }
    final File directory = new File(dirPath);

    return directory.exists() && directory.isDirectory();
  }

  /**
   * Checks if the HYPERLINK is in valid format
   *
   * @param strHyperlink hyperlink value
   * @return true if format id valid
   */
  public static boolean isValidHyperlinkFormat(final String strHyperlink) {
    return isValidLocalPathFormat(strHyperlink) || isValidURLFormat(strHyperlink) || isValidMailFormat(strHyperlink);
  }

  // ICDM-1500
  /**
   * Removes the white spaces in the url
   *
   * @param urlLink url to be formated
   * @return url without spaces
   */
  public static String formatUrl(final String urlLink) {
    return urlLink.replace(" ", "%20");
  }

  /**
   * ICDM-1110
   *
   * @param strHyperlink hyperlink string
   * @return true if the link stirng is of proper format
   */
  private static boolean isValidMailFormat(final String strHyperlink) {
    if (isEmptyString(strHyperlink)) {
      return false;
    }
    if (strHyperlink.startsWith(MAIL_PREFIX) && !strHyperlink.replace(MAIL_PREFIX, "").trim().contains(":")) {
      return EmailValidator.getInstance().isValid(strHyperlink.substring(MAIL_PREFIX_SIZE));
    }
    return false;
  }


  /**
   * Convert a file to byte array.
   *
   * @param path the path including the file name. This could be an absolute path or relative path or a simple file
   *          name.
   * @return byte array
   * @throws IOException for any exceptions while reading the file
   */
  public static byte[] getFileAsByteArray(final String path) throws IOException {
    byte[] byteBuff;
    try (FileInputStream fis = new FileInputStream(path); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
      byteBuff = new byte[CREATE_BYTE_BUFFER_SIZE];
      for (int readNum; (readNum = fis.read(byteBuff)) != -1;) {
        // Writes len bytes from the specified byte array starting at offset off to this byte array output stream.
        bos.write(byteBuff, 0, readNum);
      }
      return bos.toByteArray();
    }

  }

  /**
   * Create a file using the byte array of file contents.
   *
   * @param path the path including the file name. This could be an absolute path or relative path or a simple file
   *          name.
   * @param fileData file as byte array
   * @throws IOException for any exceptions during file creation
   */
  public static void createFile(final String path, final byte[] fileData) throws IOException {
    final File file = new File(path);
    new File(file.getParent()).mkdirs();
    try (OutputStream fos = new FileOutputStream(file)) {
      fos.write(fileData);
    }
  }

  /**
   * Format the value to replace the new line char with space for displaying in properties view
   *
   * @param value text
   * @return formated string
   */
  public static String formatText(final String value) {
    if (value.isEmpty()) {
      return "";
    }
    return value.replace("\n", " ");
  }

  // ICDM-1560
  /**
   * Format the eadm name to replace special characters with underscore and remvoe white spaces
   *
   * @param eadmName name to be formated
   * @return new eadm name
   */
  public static String formatEadmName(final String eadmName) {
    String newEadmName = eadmName;
    if (!isValidEadmName(newEadmName)) {
      return newEadmName.replaceAll(EADM_NAME_EXCLD_CHARS, "_");
    }
    return newEadmName;
  }

  // ICDM-1560
  /**
   * Checks whether the eadm name has white spaces or special characters
   *
   * @param eadmName name to be formated
   * @return true if valid
   */
  public static boolean isValidEadmName(final String eadmName) {
    Pattern pattern = Pattern.compile(EADM_NAME_EXCLD_CHARS);
    return !pattern.matcher(eadmName).find();
  }

  /**
   * Concatenates the string representation of the input objects. Uses <code>StringBuilder</code> class to concatenate
   * the strings.
   * <p>
   * Sample usage : <br>
   * <code>
   * int weight = 10;<br>
   * String text = CommonUtils.concatenate(true, "The weight is = ", weight, "kg");
   * </code> <br>
   * gives the result 'The weight is = 10kg'
   *
   * @param skipNull if <code>false</code> adds null to the return, if the object is null
   * @param objects objects to be concatenated
   * @return concatenated string
   */
  public static String concatenate(final boolean skipNull, final Object... objects) {
    // Object array should not be used
    final StringBuilder string = new StringBuilder(objects.length);

    for (Object obj : objects) {
      if (isNotNull(obj)) {
        string.append(obj.toString());
        continue;
      }

      // if obj is null, add null if flag is false
      if (!skipNull) {
        string.append(NULL_TEXT);
      }

    }

    return string.toString();
  }

  /**
   * Concatenates the string representation of the input objects. If an input parameter is <code>null</code>, 'null' is
   * appended. Uses <code>StringBuilder</code> class to concatenate the strings.
   * <p>
   * Sample usage : <br>
   * <code>
   * int weight = 10;<br>
   * String text = CommonUtils.concatenate("The weight is = ", weight, "kg");
   * </code> <br>
   * gives the result 'The weight is = 10kg'
   *
   * @param objects objects to be concatenated
   * @return concatenated string
   */
  public static String concatenate(final Object... objects) {
    return concatenate(false, objects);
  }

  /**
   * @param sortedSet of objects
   * @return true if the set is empty or null
   * @deprecated Use {@link #isNullOrEmpty(Collection)} instead
   */
  @Deprecated
  public static boolean isEmptySet(final SortedSet<? extends Object> sortedSet) {
    if (isNotNull(sortedSet)) {
      return sortedSet.isEmpty();
    }
    return true;
  }

  /**
   * Check if the given ollection is not Empty
   *
   * @param collection any Java Collection
   * @return true if the Collection is not empty and Not null
   */
  // ICdm-1218
  public static boolean isNotEmpty(final Collection<? extends Object> collection) {
    return !(isNull(collection) || collection.isEmpty());
  }

  /**
   * @param collection any java Collection
   * @return true if the collection is null or empty
   */
  public static boolean isNullOrEmpty(final Collection<? extends Object> collection) {
    return !isNotEmpty(collection);
  }

  /**
   * @param array array
   * @return true if the array is <code>null</code> or empty
   */
  public static boolean isNullOrEmpty(final Object[] array) {
    if (array == null) {
      return true;
    }
    for (Object object : array) {
      if (object != null) {
        return false;
      }
    }
    return true;
  }

  /**
   * @param array array
   * @return the reverse of isNullOrEmptyArray
   */
  public static boolean isNotEmpty(final Object[] array) {
    return !isNullOrEmpty(array);
  }

  /**
   * This method is used to Swap an Element to a Given position in an Array. The Input Array should not contain any
   * Duplicates
   *
   * @param inputArray the Input Array
   * @param strToSwap the String to be Swapped
   * @param position position to be swapped.
   */
  public static void swapArrayElement(final String[] inputArray, final String strToSwap, final int position) {

    for (int index = 0; index < inputArray.length; index++) {
      if (CommonUtils.isEqual(inputArray[index], strToSwap)) {
        String temp = inputArray[position];
        inputArray[position] = strToSwap;
        inputArray[index] = temp;
        break;
      }
    }
  }

  /**
   * @param numberStr numberStr
   * @return the double value of a String number
   */
  public static Double convertStringIntoDouble(final String numberStr) {

    // Icdm-1370- new method for Converting String to number.
    NumberFormat numFormat = NumberFormat.getInstance(Locale.ENGLISH);
    ParsePosition parsePos = new ParsePosition(0);
    Double tempDoubleOfString = null;

    /*
     * Convert the value from a String to a Number. Necessary to check if the String could be converted. If just a part
     * of the String is converted, the object is not null. If it is a complete text, number is null. To avoid a
     * NullPointerException when converting into double, the variable number is checked for existence
     */
    Number number = numFormat.parse(numberStr, parsePos);

    // Convert into double if Number is not null
    if (number != null) {

      // NullPointerException when allocated to tempDoubleOfString
      tempDoubleOfString = number.doubleValue();

    }

    // If length of String != parsing position, the whole number couldn't be transformed into a double. Validity = false
    if (numberStr.length() != parsePos.getIndex()) {
      return null;
    }
    // Otherwise store the value

    return tempDoubleOfString;
  }

  /**
   * @param str string which has to converted
   * @return String with * replaced by %
   */
  public static String convertStringForDbQuery(final String str) {
    return str.replace('*', '%');
  }

  /**
   * Splits the strings with the delimiter
   *
   * @param str string
   * @param delimit delimiter
   * @return array of elements
   */
  public static String[] splitWithDelimiter(final String str, final String delimit) {
    return str.split(delimit);
  }

  /**
   * Splits a set to buckets with the given max size
   *
   * @param inputSet set to split
   * @param bucketSize maximum size allowed for a bucket
   * @return list of buckets(set)
   */
  public static <E> List<Set<E>> splitSet(final Set<E> inputSet, final int bucketSize) {
    int bucketCount = ((inputSet.size() + bucketSize) - 1) / bucketSize;

    List<Set<E>> retList = new ArrayList<>(bucketCount);
    for (int count = 0; count < bucketCount; count++) {
      retList.add(new HashSet<E>());
    }

    int idx = 0;
    for (E item : inputSet) {
      retList.get(idx % bucketCount).add(item);
      idx++;
    }
    return retList;
  }

  /**
   * Creates a new array list of strings with customized <code>toString()</code> implementation.
   * <p>
   * This method overrides toString() method of ArrayList and returns list having toString() method with the feature of
   * showing list as a single string joining with given delimiter <br>
   * For example, <br>
   * <code>
   * List<String> list = new getCustomArrayList<String>(true, ', ');<br>
   * list.add("one");<br>
   * list.add("two");<br>
   * list.add("three");<br>
   * System.out.println(list.toString());<br>
   * </code> <br>
   * gives the result as '1. one, 2. two, 3. three'
   *
   * @param isNeededSerialNumber , the flag if true, then serial number will be included in the list
   * @param delimiter delimiter
   * @return ArrayList
   */
  public static <E> List<E> getCustomArrayList(final boolean isNeededSerialNumber, final String delimiter) {
    return new ArrayList<E>() {

      private static final long serialVersionUID = 1L;

      @Override
      public String toString() {
        Iterator<E> itr = iterator();
        if (!itr.hasNext()) {
          return CommonUtilConstants.EMPTY_STRING;
        }

        StringBuilder stringBuilder = new StringBuilder();
        if (isNeededSerialNumber) {
          int count = 0;
          for (;;) {
            E str = itr.next();
            count++;
            stringBuilder.append(count).append(". ").append(str);
            if (!itr.hasNext()) {
              return stringBuilder.toString();
            }
            stringBuilder.append(delimiter);
          }
        }
        for (;;) {
          E str = itr.next();
          stringBuilder.append(str);
          if (!itr.hasNext()) {
            return stringBuilder.toString();
          }
          stringBuilder.append(delimiter);
        }
      }
    };
  }

  /**
   * @param localeStr String
   * @return BigDecimal
   */
  // ICDM-1515
  public static BigDecimal getBigDecimalValueForLocaleString(final String localeStr) {
    String str = localeStr;
    str = str.replace(",", ".");
    return new BigDecimal(str);
  }

  /**
   * @param Object obj
   * @return Long
   */
  public static Long bigDecimalToLong(final Object obj) {
    if (obj == null) {
      return 0L;
    }
    BigDecimal bd = (BigDecimal) obj;
    return bd.longValue();
  }

  /**
   * @param min int
   * @param max int
   * @return random Number
   */
  public static int getRandomNumber(final int min, final int max) {
    return new Random().nextInt((max - min) + 1) + min;
  }

  /**
   * @param map Map
   * @return boolean
   */
  public static boolean isNullOrEmpty(final Map<?, ?> map) {
    return isNull(map) || map.isEmpty();
  }

  /**
   * @param map Map
   * @return boolean
   */
  public static boolean isNotEmpty(final Map<?, ?> map) {
    return !isNullOrEmpty(map);
  }

  /**
   * Get the elements in collection 1, but not in collection 2. If collection1 is null, an empty set is returned. If
   * collection2 is null, all elements in collection 1 are returned as a set
   *
   * @param collection1 collection 1
   * @param collection2 collection 2
   * @return set of elements in collection1, but not in collection2
   */
  public static <A extends Collection<E>, E, B extends Collection<E>> Set<E> getDifference(final A collection1,
      final B collection2) {

    Set<E> retSet = new HashSet<>();
    if (collection1 != null) {
      retSet.addAll(collection1);
      if (collection2 != null) {
        retSet.removeAll(collection2);
      }
    }
    return retSet;
  }

  /**
   * Get the elements in map 1, but not in map 2. If map1 is null, an empty map is returned. If map2 is null, a copy of
   * map1 is returned
   *
   * @param map1 map 1
   * @param map2 map 2
   * @return set of elements in map1, but not in map2
   */
  public static <A extends Map<K, V>, K, V, B extends Map<K, V>> Map<K, V> getDifference(final A map1, final B map2) {

    Map<K, V> retMap = new HashMap<>();

    if (map1 != null) {
      retMap.putAll(map1);
      if (map2 != null) {
        for (K key : map2.keySet()) {
          retMap.remove(key);
        }
      }
    }
    return retMap;
  }


  /**
   * This method gives the full file path with extension
   *
   * @param filePath filePath
   * @param fileExtension fileExtension
   * @return fileFullPath fileFullPath
   */
  public static String getCompleteFilePath(final String filePath, final String fileExtension) {
    String fileFullPath;
    if (filePath.contains(".xlsx") || filePath.contains(".xls")) {
      fileFullPath = filePath;
    }
    else {
      fileFullPath = filePath + "." + fileExtension;
    }
    return fileFullPath;
  }


  /**
   * This method gives the full file path with extension
   *
   * @param filePath filePath
   * @param fileExtension fileExtension
   * @return fileFullPath fileFullPath
   */
  public static String getCompletePdfFilePath(final String filePath, final String fileExtension) {
    String fileFullPath;
    if (filePath.contains(".pdf")) {
      fileFullPath = filePath;
    }
    else {
      fileFullPath = filePath + "." + fileExtension;
    }
    return fileFullPath;
  }


  /**
   * This method checks whether the given file is open or not
   *
   * @param file file
   * @return boolean
   */
  public static boolean checkIfFileOpen(final File file) {
    boolean isFileOpen = false;
    if (file.exists() || (file.length() > 0L)) {// if the given file is not available, returns false
      try (FileOutputStream testOut = new FileOutputStream(file, true)) {
        testOut.close();
        isFileOpen = false;
      }
      catch (IOException exp) {
        isFileOpen = true;
        CDMLogger.getInstance().info("The Selected File " + file.getName() + " is already open", exp,
            Activator.PLUGIN_ID);
      }
    }
    return isFileOpen;
  }


  /**
   * This method returns true if the given file path is not null,total file path length is not greater than 259
   * characters,subfolders in filepath length not greater than 247 and filename should not contain invalid windows
   * character \ / : * ? " < > |
   *
   * @param fileSelected
   */
  public static Predicate<String> isValidFilePath() {

    return filepath -> isNotNull(filepath) &&
        (Paths.get(filepath).getParent().toString()
            .length() <= CommonUtilConstants.WINDOWS_FILEPATH_SUBFOLDERS_LIMIT) &&
        (filepath.length() <= CommonUtilConstants.WINDOWS_TOTAL_FILEPATH_LIMIT) &&
        (Arrays.asList(CommonUtilConstants.INVALID_WINDOWS_FILENAME_CHARS).stream()
            .noneMatch(invalidChar -> Paths.get(filepath).getFileName().toString().contains(invalidChar.toString())));

  }

  /**
   * @return true if initializer started from iCDM Web Service
   */
  public static boolean isStartedFromWebService() {
    return CommonUtilConstants.APP_MODE_TYPE_WBSRVCE.equals(System.getProperty(CommonUtilConstants.APP_MODE_PROP_NAME));
  }


  /**
   * Provides the display value for the boolean value
   *
   * @param flag boolean flag
   * @return YES/NO
   */
  public static String getDisplayText(final boolean flag) {
    return flag ? CommonUtilConstants.DISPLAY_YES : CommonUtilConstants.DISPLAY_NO;
  }

  /**
   * This method converts the String input 'Y' and 'N' to true and false correspondingly
   *
   * @param yesOrNoString 'Y' or 'N'
   * @return true if input is Y. false for 'N' or anything else
   */
  public static boolean getBooleanType(final String yesOrNoString) {
    return CommonUtilConstants.CODE_YES.equals(yesOrNoString);
  }

  /**
   * Convert boolean to is equivalent code
   *
   * @param bool input
   * @return Y if input is true, else N
   */
  public static String getBooleanCode(final boolean bool) {
    return bool ? CommonUtilConstants.CODE_YES : CommonUtilConstants.CODE_NO;
  }

  /**
   * Builds a message using java message format
   *
   * @param message input message
   * @param params parameters
   * @return message with paramers placed in it
   */
  public static String getMessage(final String message, final Object... params) {
    return MessageFormat.format(message, params);
  }

  /**
   * Checks if the given string is number
   *
   * @param str input
   * @return true, if number
   */
  // iCDM-2232
  public static boolean checkIfNumber(final String str) {
    return (str != null) && str.matches("^[-+]?[0-9]*\\.?,?[0-9]+([eE][-+]?[0-9]+)?$");
  }


  /**
   * Combines the string array
   *
   * @param stringArrayA array
   * @param stringArrayB array
   * @return string arrray
   */
  // iCDM-2355
  public static String[] combineStringArray(final String[] stringArrayA, final String[] stringArrayB) {
    int length = stringArrayA.length + stringArrayB.length;
    String[] result = new String[length];
    System.arraycopy(stringArrayA, 0, result, 0, stringArrayA.length);
    System.arraycopy(stringArrayB, 0, result, stringArrayA.length, stringArrayB.length);
    return result;
  }

  // iCDM-2614
  /**
   * @param val1 the first Object
   * @param val2 the second Object
   * @return true if both are equal
   */
  public static boolean isEqualIgnoreNull(final Object val1, final Object val2) {
    if ((val1 == null) && (val2 == null)) {
      return true;
    }
    if ((val1 != null) && (val2 != null)) {
      return val1.equals(val2);
    }
    return false;
  }

  /**
   * Calculates the percent of a given number
   *
   * @param partialPortion given number
   * @param totalPortion total number, mostly 100 for finding percentage
   * @param scaleValue the decimal point value
   * @param roundingMode the rounding mode
   * @return the percentage of number
   */
  // Task 243510
  public static BigDecimal calculatePercentage(final int partialPortion, final int totalPortion, final int scaleValue,
      final RoundingMode roundingMode) {
    BigDecimal number = new BigDecimal(partialPortion);
    number = number.setScale(scaleValue, roundingMode);

    BigDecimal totalValue = new BigDecimal(totalPortion);
    totalValue = totalValue.setScale(scaleValue, roundingMode);

    BigDecimal numberPercent = number.divide(totalValue, scaleValue, roundingMode);

    numberPercent = numberPercent.multiply(BigDecimal.valueOf(PERCENTAGE_VALUE));

    return numberPercent;
  }

  /**
   * To calculate the checksum value of inpute byte array
   *
   * @param byteArray of input
   * @return checksum value
   */
  public static Long calculateCheckSum(final byte[] byteArray) {
    CRC32 checkSum = new CRC32();
    checkSum.update(byteArray);
    return checkSum.getValue();
  }


  /**
   * Clone cal data.
   *
   * @param calDataMap the cal data map
   * @return the map
   */
  public static Map<String, CalData> cloneCalData(final Map<String, CalData> calDataMap) {
    if ((calDataMap == null) || calDataMap.isEmpty()) {
      return calDataMap;
    }
    Map<String, CalData> clonedCalDataMap = new HashMap<>();
    for (Map.Entry<String, CalData> entry : calDataMap.entrySet()) {
      String key = entry.getKey();
      CalData calData = entry.getValue();
      try {
        clonedCalDataMap.put(key, calData.clone());
      }
      catch (Exception ex) {
        CDMLogger.getInstance().error("Error occurred while cloning CalData for : " + key, ex);
      }
    }
    return clonedCalDataMap;
  }

  /**
   * Converts file path into byte array
   *
   * @param filePath the given a2l-file path
   * @return the byte array of the given a2l-file path
   * @throws IOException if no file or invalid found
   */
  public static byte[] getA2lFileAsByteArray(final String filePath) throws IOException {
    java.nio.file.Path path = Paths.get(filePath);
    return Files.readAllBytes(path);
  }

  /**
   * Join the string representation of the objects to a string with the given separator
   *
   * @param col collection of item
   * @param separator separator
   * @return joined string
   */
  public static String join(final Collection<?> col, final String separator) {
    return col.stream().map(item -> item == null ? null : item.toString()).collect(Collectors.joining(separator));
  }

  /**
   * Checks for deep equal of two collection content
   *
   * @param collection1 firstCollInstn
   * @param collection2 secondCollInst
   * @return true if two given collection are equal
   */
  public static <T> boolean equals(final Collection<T> collection1, final Collection<T> collection2) {
    boolean equals = false;
    if ((collection1 != null) && (collection2 != null)) {
      equals = (collection1.size() == collection2.size()) && collection1.containsAll(collection2) &&
          collection2.containsAll(collection1);
    }
    else if ((collection1 == null) && (collection2 == null)) {
      equals = true;
    }
    return equals;
  }


  /**
   * @param inputString given input
   * @param charSequences var args
   * @return true if it is available
   */
  public static boolean containsString(final String inputString, final String... charSequences) {

    for (String obj : charSequences) {
      if (inputString.contains(obj)) {
        return true;
      }
    }

    return false;
  }

  /**
   * copies source Object fields to destination object/Copy property values from the origin bean to the destination bean
   * for all cases where the property names are the same
   *
   * @param <O> any object
   * @param destination Object
   * @param source Object
   */
  public static <O> void shallowCopy(final O destination, final O source) {
    try {
      BeanUtils.copyProperties(destination, source);
    }
    catch (IllegalAccessException | InvocationTargetException e) {
      CDMLogger.getInstance().warn(e.getMessage(), e);
    }
  }

  /**
   * Build the string for the given message and the parameters
   *
   * @param message message with placeholders
   * @param params arguments to be put to placeholders
   * @return log message
   */
  public static String buildString(final String message, final Object... params) {
    if ((message == null) || (message.indexOf(MSG_PARAM_PLACE_HOLDER) < 0)) {
      return message;
    }
    boolean msgNotFinished;
    StringBuilder newMessage = new StringBuilder(100);

    SplittedMessage splitMsg = splitMessage(message);
    String[] msgItems = splitMsg.getMsgItems();

    int idx;
    if ((params == null)) {
      msgNotFinished = true;
      idx = -1;
    }
    else {
      int phCount = splitMsg.getPlaceHolderCount();
      for (idx = 0; idx < phCount; idx++) {
        if (idx >= params.length) {
          break;
        }
        newMessage.append(msgItems[idx]).append(params[idx]);
      }

      msgNotFinished = (phCount > params.length) || (phCount < msgItems.length);
      if (msgNotFinished) {
        idx--;
      }
    }
    if (msgNotFinished) {
      for (int idxBal = idx + 1; idxBal < msgItems.length; idxBal++) {
        newMessage.append(msgItems[idxBal]);
      }
    }
    return newMessage.toString();
  }

  /**
   * Properties of a message splitted based on the place holders
   *
   * @author bne4cob
   */
  private static class SplittedMessage {

    private final int placeHolderCount;
    private final String[] msgItems;

    /**
     * @param placeHolderCount
     * @param msgItems
     */
    SplittedMessage(final int placeHolderCount, final String[] msgItems) {
      super();
      this.placeHolderCount = placeHolderCount;
      this.msgItems = msgItems;
    }


    /**
     * @return the placeHolderCount
     */
    final int getPlaceHolderCount() {
      return this.placeHolderCount;
    }


    /**
     * @return the msgItems
     */
    final String[] getMsgItems() {
      return this.msgItems;
    }

  }

  /**
   * @param message
   * @return
   */
  private static SplittedMessage splitMessage(final String message) {
    String temp = message;
    List<String> msgPartList = new ArrayList<>();
    int phCount = 0;
    int itemEndPos = temp.indexOf(MSG_PARAM_PLACE_HOLDER);
    if (itemEndPos == -1) {
      msgPartList.add(temp);
    }
    else {
      while (true) {
        phCount++;
        msgPartList.add(temp.substring(0, itemEndPos));
        if (temp.length() <= (itemEndPos + MSG_PARAM_PLACE_HOLDER_LEN)) {
          break;
        }
        temp = temp.substring(itemEndPos + MSG_PARAM_PLACE_HOLDER_LEN);

        itemEndPos = temp.indexOf(MSG_PARAM_PLACE_HOLDER);
        if (itemEndPos < 0) {
          msgPartList.add(temp);
          break;
        }
      }
    }
    String[] retArr = new String[msgPartList.size()];
    return new SplittedMessage(phCount, msgPartList.toArray(retArr));
  }

  /**
   * @param dateInStr String
   * @return String
   */
  public static String setDateFormat(final String dateInStr) {
    SimpleDateFormat formatter =
        new SimpleDateFormat(DateFormat.DATE_FORMAT_04, Locale.getDefault(Locale.Category.FORMAT));
    try {
      Date date = formatter.parse(dateInStr);
      String formattedDate = "";
      if (date != null) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        formattedDate = ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_12, cal);
      }
      return formattedDate;
    }
    catch (ParseException e) {
      CDMLogger.getInstance().error("Date not valid", Activator.PLUGIN_ID);
    }

    return "";
  }


  /**
   * Method to convert Long into Bigdecimal
   *
   * @param value - Long value
   * @return - Equivalent Bigdecimal value
   */
  public static BigDecimal getBigdecimalFromLong(final Long value) {
    BigDecimal bigdecimalval = null;
    if (value != null) {
      bigdecimalval = BigDecimal.valueOf(value);
    }
    return bigdecimalval;
  }

  /**
   * Set requestId and method in the logging context
   *
   * @param method
   */
  public static void setLogger(final String requestId, final String method) {
    ThreadContext.put(CommonUtilConstants.REQUEST_ID, requestId);
    ThreadContext.put(CommonUtilConstants.METHOD, method);
  }

  /**
   * check if the provided comment exceeds the max comment limit and truncate the extra characters if exceeding
   *
   * @param str
   * @return
   */
  public static String truncateTextToMaxLen(String str, final int maxLength) {
    while (str.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > maxLength) {
      str = str.substring(0, str.length() - 1);
    }
    return str;
  }

}
