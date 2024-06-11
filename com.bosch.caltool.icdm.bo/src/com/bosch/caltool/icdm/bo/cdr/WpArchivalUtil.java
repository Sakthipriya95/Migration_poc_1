/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.general.IcdmFilesLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.FileNameUtil;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.WpArchival;

/**
 * @author EKIR1KOR
 */
public class WpArchivalUtil {

  /**
   *
   */
  private static final String BASELINE_FAILED_MSG =
      "Failed to copy Baseline File to Archival Folder. Please contact iCDM Hotline for support.";

  /**
   * Constant for Underscore
   */
  private static final String UNDERSCORE = "_";

  /**
   * Constant for Space
   */
  private static final String EMPTY_STR = " ";

  /**
   * Constant for Archive Text File
   */

  private static final String WP_ARCHIVAL_TXT_FILES = "WP_ARCHIVAL_TXT_FILE_";

  /**
   * Logger for this class
   */
  private static ILoggerAdapter logger = new Log4JLoggerAdapterImpl(LogManager.getLogger("WpArchivalUtil"));

  /**
   * Temp path for storing WP Archive files
   */
  public static final String WP_ARCHIVAL_BASE_PATH =
      Messages.getString("SERVICE_WORK_DIR") + File.separator + CDRConstants.WP_ARCHIVAL_FOLDER + File.separator;

  /**
   * Maximum allowed chars for a Zip File Name in Archival Folder
   */
  public static final int MAX_ALLOWED_CHARS_ZIPNAME_ARCHIVAL = 20;

  /**
   * Maximum allowed chars for a directory name
   */
  private static final int MAX_ALLOWED_CHARS = 10;

  /**
   * Maximum allowed chars for a PIDC NAME in Archival Folder
   */
  private static final int MAX_ALLOWED_CHARS_PIDC_ARCHIVAL = 20;

  /**
   * Maximum allowed chars for a PIDC Variant Name in Archival Folder
   */
  private static final int MAX_ALLOWED_CHARS_PIDC_VAR_ARCHIVAL = 25;

  /**
   * Maximum allowed chars for a BASELINE Folder Name in Archival Folder
   */
  private static final int MAX_ALLOWED_CHARS_BASELINE_ARCHIVAL = 20;

  /**
   * Maximum allowed chars for PIDC variant directory name
   */
  private static final int MAX_ALLOWED_CHARS_PIDC_VAR = 15;


  private WpArchivalUtil() {

  }

  /**
   * Create base path for storing baseline files
   *
   * @param customerName Customer Name
   * @return BasePath of baseline directory
   */
  public static File createBasePath(final String customerName) {
    // Check base directory, create if necessary
    File file = new File(WP_ARCHIVAL_BASE_PATH);
    createDirectory(file);
    // create customer folder name
    file = new File(file.getAbsolutePath() + File.separator + getCustomerFolderName(customerName));
    createDirectory(file);
    return file;
  }

  /**
   * Frame the folder name for customer folder to store baseline files
   *
   * @param customerName
   * @return
   */
  static String getCustomerFolderName(final String customerName) {
    StringBuilder custNameDirBuilder = new StringBuilder(customerName);
    custNameDirBuilder.append(UNDERSCORE).append(RandomStringUtils.randomNumeric(5));
    return FileNameUtil.formatFileName(custNameDirBuilder.toString(), ApicConstants.INVALID_CHAR_PTRN_EXCLUD_SPACE);
  }

  /**
   * Frame the Baseline folder name for creating file directory
   *
   * @param wpArchivalId
   * @param wpName
   * @return baseline folder name
   */
  public static String frameBaselineFolderName(final Long wpArchivalId, final String wpName) {
    // Baseline ID + Baseline creation date + Baseline Name
    StringBuilder baselineFolderNameBuilder = new StringBuilder();
    baselineFolderNameBuilder.append(String.valueOf(wpArchivalId));
    baselineFolderNameBuilder.append(EMPTY_STR);
    baselineFolderNameBuilder.append(ApicUtil.getCurrentTime(DateFormat.DATE_FORMAT_10));
    baselineFolderNameBuilder.append(EMPTY_STR);
    baselineFolderNameBuilder.append(wpName);

    return baselineFolderNameBuilder.toString();
  }

  /**
   * @param pidc
   * @param pidcName
   * @param pidcVersId
   * @return pidc folder name
   */
  private static String framePidcFolderName(final Pidc pidc, final String pidcName, final Long pidcVersId) {
    // pidc id + pidc variant id + pidc name
    StringBuilder pidcFolderName = new StringBuilder();
    pidcFolderName.append(pidc.getId());
    pidcFolderName.append(EMPTY_STR);
    pidcFolderName.append(pidcVersId);
    pidcFolderName.append(EMPTY_STR);
    pidcFolderName.append(pidcName);

    return pidcFolderName.toString();
  }

  /**
   * Create folder directories in case they are not available
   *
   * @param wpArchival
   * @param baseFile
   * @param customerName
   * @param sdomPverName
   * @param sdomPverVar
   * @param pidc
   * @return
   */
  public static String createBaseFolderStructure(final WpArchival wpArchival, final File baseFile,
      final String customerName, final String sdomPverName, final String sdomPverVar, final Pidc pidc) {

    File file;
    // create customer folder name
    if (customerName.length() > MAX_ALLOWED_CHARS) {
      String trimmedName = getTrimmedName(customerName, MAX_ALLOWED_CHARS);
      createTextFile(baseFile.getAbsolutePath(), trimmedName, customerName);
      file = new File(baseFile.getAbsolutePath() + File.separator + trimmedName);
    }
    else {
      file = new File(baseFile.getAbsolutePath() + File.separator + customerName);
    }
    createDirectory(file);

    // create pidc Folder name
    file =
        new File(file.getAbsolutePath() + File.separator + getPidcFolderName(pidc, wpArchival, file.getAbsolutePath()));
    createDirectory(file);

    // create pidc variant Folder name
    file = new File(file.getAbsolutePath() + File.separator +
        getPidcVariantName(wpArchival, file.getAbsolutePath(), sdomPverName, sdomPverVar));
    createDirectory(file);

    // create baseline folder name
    file =
        new File(file.getAbsolutePath() + File.separator + getBaselineFolderName(wpArchival, file.getAbsolutePath()));
    createDirectory(file);

    return file.getAbsolutePath();

  }

  /**
   * Baseline folder name for creating file directory
   *
   * @param wpArchival
   * @param filePath
   * @return
   */
  public static String getBaselineFolderName(final WpArchival wpArchival, final String filePath) {

    String wpName = FileNameUtil.formatFileName(wpArchival.getWpName(), ApicConstants.INVALID_CHAR_PTRN_EXCLUD_SPACE);

    logger.debug("Fetching the baseline folder name");
    String fullbaselineFolderName = frameBaselineFolderName(wpArchival.getId(), wpName);

    logger.debug("Trimming the baseline folder name to limited length");
    String trimmedWpName = getTrimmedName(wpName, MAX_ALLOWED_CHARS);

    // Name of the text file will have IDs included followed by trimmed pidc name
    String txtFileName = frameBaselineFolderName(wpArchival.getId(), trimmedWpName);

    // Contents of the text file will have the full path including ids and pidc name without trimming
    createTextFile(filePath, txtFileName, fullbaselineFolderName);

    // Name of the actual directory will have only the trimmed name
    return trimmedWpName;

  }


  /**
   * PIDC variant folder name for creating file directory
   *
   * @param wpArchival
   * @param filePath
   * @param sdomPverName
   * @param sdomPverVar
   * @return
   */
  private static String getPidcVariantName(final WpArchival wpArchival, final String filePath,
      final String sdomPverName, final String sdomPverVar) {

    StringBuilder varSdomNameBuilder = pidcVarStringBuilder(wpArchival, sdomPverName, sdomPverVar);

    String varSdomName =
        FileNameUtil.formatFileName(varSdomNameBuilder.toString(), ApicConstants.INVALID_CHAR_PTRN_EXCLUD_SPACE);
    Long pidcVarId = wpArchival.getVariantId();

    logger.debug("Fetching the PIDC variant folder name");
    String fullPidcVarFolderName = framePidcVarFolderName(pidcVarId, varSdomName);

    logger.debug("Trimming the Sdom pver variant folder name to limited length");
    String trimmedvarSdomName = getTrimmedName(varSdomName, MAX_ALLOWED_CHARS_PIDC_VAR);

    // Name of the text file will have IDs included followed by trimmed pidc name
    String txtFileName = framePidcVarFolderName(pidcVarId, trimmedvarSdomName);

    // Contents of the text file will have the full path including ids and pidc name without trimming
    createTextFile(filePath, txtFileName, fullPidcVarFolderName);

    // Name of the actual directory will have only the trimmed name
    return trimmedvarSdomName;
  }

  /**
   * Create folder directories in case they are not available
   *
   * @param wpArchival Wparchival model
   * @param baseFile File
   * @param sdomPverName SDOM Pversion name
   * @param sdomPverVar SDOM Pversion variant name
   * @return String name of the base folder
   * @throws IcdmException
   */
  public static String createBaseFolderStructureForArchival(final Pidc pidc, final WpArchival wpArchival,
      final File baseFile, final String sdomPverName, final String sdomPverVar)
      throws IcdmException {

    File file = new File(baseFile.getAbsolutePath());

    // create pidc Folder name
    file = new File(file.getAbsolutePath() + File.separator +
        getPidcFolderNameForArchival(pidc, file.getAbsolutePath(), wpArchival));
    createDirectory(file);

    // create pidc variant Folder name
    file = new File(file.getAbsolutePath() + File.separator +
        getPidcVariantNameArchival(wpArchival, sdomPverName, sdomPverVar, file.getAbsolutePath()));
    createDirectory(file);

    // create baseline folder name
    file = new File(
        file.getAbsolutePath() + File.separator + getBaselineFolderNameArchival(wpArchival, file.getAbsolutePath()));
    createDirectory(file);

    return file.getAbsolutePath();

  }

  /**
   * Baseline folder name for creating file directory
   *
   * @param wpArchival
   * @param filePath
   * @return
   * @throws IcdmException
   */
  public static String getBaselineFolderNameArchival(final WpArchival wpArchival, final String filePath)
      throws IcdmException {

    String wpName = FileNameUtil.formatFileName(wpArchival.getWpName(), ApicConstants.INVALID_CHAR_PTRN_EXCLUD_SPACE);

    logger.debug("Fetching the baseline folder name");
    String fullbaselineFolderName = frameBaselineFolderName(wpArchival.getId(), wpName);

    logger.debug("Trimming the baseline folder name to limited length");
    String trimmedWpName = getTrimmedName(wpName, MAX_ALLOWED_CHARS_BASELINE_ARCHIVAL);

    // Name of the text file will have IDs included followed by trimmed pidc name
    String txtFileName = frameBaselineFolderName(wpArchival.getId(), trimmedWpName);

    // Contents of the text file will have the full path including ids and pidc name without trimming
    createArchiveTextFile(filePath, txtFileName, fullbaselineFolderName);

    // Name of the actual directory will have only the trimmed name
    return txtFileName;

  }

  /**
   * @param wpArchival
   * @param sdomPverName
   * @param sdomPverVar
   * @return
   */
  public static StringBuilder pidcVarStringBuilder(final WpArchival wpArchival, final String sdomPverName,
      final String sdomPverVar) {
    // pidc variant name + sdom pver name + sdom pver variant
    StringBuilder varSdomNameBuilder = new StringBuilder();
    if (CommonUtils.isNotEqual(wpArchival.getVariantId(), ApicConstants.NO_VARIANT_ID)) {
      varSdomNameBuilder.append(wpArchival.getVariantName());
    }
    else {
      varSdomNameBuilder.append(CDRConstants.NO_VARIANT);
    }
    if (CommonUtils.isNotNull(sdomPverName) && CommonUtils.isNotNull(sdomPverVar)) {
      varSdomNameBuilder.append(EMPTY_STR);
      varSdomNameBuilder.append(sdomPverName);
      varSdomNameBuilder.append(EMPTY_STR);
      varSdomNameBuilder.append(sdomPverVar);
    }
    return varSdomNameBuilder;
  }

  /**
   * PIDC variant folder name for creating file directory in Archival Folder
   *
   * @param wpArchival
   * @param sdomPverName
   * @param sdomPverVar
   * @param filePath
   * @return
   * @throws IcdmException
   */
  private static String getPidcVariantNameArchival(final WpArchival wpArchival, final String sdomPverName,
      final String sdomPverVar, final String filePath)
      throws IcdmException {

    StringBuilder varSdomNameBuilder = pidcVarStringBuilder(wpArchival, sdomPverName, sdomPverVar);

    String varSdomName =
        FileNameUtil.formatFileName(varSdomNameBuilder.toString(), ApicConstants.INVALID_CHAR_PTRN_EXCLUD_SPACE);
    Long pidcVarId = wpArchival.getVariantId();

    logger.debug("Fetching the PIDC variant folder name");
    String fullPidcVarFolderName = framePidcVarFolderName(pidcVarId, varSdomName);

    logger.debug("Trimming the Sdom pver variant folder name to limited length");
    String trimmedvarSdomName = getTrimmedName(varSdomName, MAX_ALLOWED_CHARS_PIDC_VAR_ARCHIVAL);

    // Name of the text file will have IDs included followed by trimmed pidc name
    String txtFileName = framePidcVarFolderName(pidcVarId, trimmedvarSdomName);

    // Contents of the text file will have the full path including ids and pidc name without trimming
    createArchiveTextFile(filePath, txtFileName, fullPidcVarFolderName);

    // Name of the actual directory will have only the trimmed name
    return txtFileName;
  }

  /**
   * Frame the PIDC variant name for creating file directory
   *
   * @param pidcVarId
   * @param varSdomName
   */
  public static String framePidcVarFolderName(final Long pidcVarId, final String varSdomName) {

    // pidc variant ID + pidc variant name + sdom pver name + sdom pver variant
    StringBuilder pidcVarFolderName = new StringBuilder();
    if (CommonUtils.isNotNull(pidcVarId)) {
      pidcVarFolderName.append(pidcVarId);
      pidcVarFolderName.append(EMPTY_STR);
    }
    pidcVarFolderName.append(varSdomName);

    return pidcVarFolderName.toString();
  }

  /**
   * PIDC folder name for creating file directory For Archival
   *
   * @param pidc
   * @param filePath
   * @param wpArchival
   * @return
   * @throws IcdmException
   */
  public static String getPidcFolderNameForArchival(final Pidc pidc, final String filePath, final WpArchival wpArchival)
      throws IcdmException {
    logger.debug("Fetching the PIDC folder name");

    String fullPidcFolderName = framePidcFolderName(pidc, pidc.getName(), wpArchival.getPidcVersId());

    logger.debug("Trimming the PIDC name to limited length");
    String trimmedPidcName = getTrimmedName(pidc.getName(), MAX_ALLOWED_CHARS_PIDC_ARCHIVAL);

    // Name of the text file will have IDs included followed by trimmed pidc name
    String txtFileName = framePidcFolderName(pidc, trimmedPidcName, wpArchival.getPidcVersId());

    // Contents of the text file will have the full path including ids and pidc name without trimming
    createArchiveTextFile(filePath, txtFileName, fullPidcFolderName);

    // Name of the actual directory will have only the trimmed name
    return txtFileName;
  }

  /**
   * @param fullName
   * @param allowedLength
   * @return
   */
  public static String getTrimmedName(final String fullName, final int allowedLength) {
    String trimmedName = fullName;
    if (fullName.length() > allowedLength) {
      trimmedName = StringUtils.abbreviateMiddle(fullName, "..", allowedLength);
    }
    return trimmedName;
  }

  /**
   * @param filePath
   * @param trimmedName
   * @param fullName
   * @throws IcdmException
   */
  public static void createArchiveTextFile(final String filePath, final String trimmedName, final String fullName)
      throws IcdmException {
    logger.info("Creating a text file containing the full folder name as content");
    try {
      File txtFile = new File(filePath + File.separator + trimmedName + ".txt");
      if (!txtFile.exists()) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH_mm_ss_SSS");
        String currentDateTimeString = currentDateTime.format(formatter);
        String folderPath = WP_ARCHIVAL_BASE_PATH + WP_ARCHIVAL_TXT_FILES + currentDateTimeString;
        File folder = new File(folderPath);
        folder.mkdirs();
        File tempTxtFile = new File(WP_ARCHIVAL_BASE_PATH + WP_ARCHIVAL_TXT_FILES + currentDateTimeString +
            File.separator + trimmedName + ".txt");
        boolean fileCreated = tempTxtFile.createNewFile();
        if (fileCreated) {
          StringBuilder content = new StringBuilder("Complete folder name - ").append(fullName);
          Files.write(tempTxtFile.toPath(), content.toString().getBytes(StandardCharsets.UTF_8));
          logger.info("New text File created with the original folder name as content");
        }
        updateLastAccessDateWhileArchiving(Paths.get(WP_ARCHIVAL_BASE_PATH + WP_ARCHIVAL_TXT_FILES +
            currentDateTimeString + File.separator + trimmedName + ".txt"));
        roboCopyWpArchivalFileToArchival(WP_ARCHIVAL_BASE_PATH + WP_ARCHIVAL_TXT_FILES + currentDateTimeString +
            File.separator + trimmedName + ".txt", filePath + File.separator + trimmedName + ".txt");
        deleteFolder(folder);
      }
    }
    catch (IOException e) {
      logger.error("An error occured while creating text file for full folder name", e);
    }
  }


  /**
   * @param pidc
   * @param wpArchival
   * @param filePath
   * @return
   */
  public static String getPidcFolderName(final Pidc pidc, final WpArchival wpArchival, final String filePath) {
    logger.debug("Fetching the PIDC folder name");
    String pidcName = FileNameUtil.formatFileName(pidc.getName(), ApicConstants.INVALID_CHAR_PTRN_EXCLUD_SPACE);

    String fullPidcFolderName = framePidcFolderName(pidc, pidcName, wpArchival.getPidcVersId());

    logger.debug("Trimming the PIDC name to limited length");
    String trimmedPidcName = getTrimmedName(pidcName, MAX_ALLOWED_CHARS);

    // Name of the text file will have IDs included followed by trimmed pidc name
    String txtFileName = framePidcFolderName(pidc, trimmedPidcName, wpArchival.getPidcVersId());

    // Contents of the text file will have the full path including ids and pidc name without trimming
    createTextFile(filePath, txtFileName, fullPidcFolderName);

    // Name of the actual directory will have only the trimmed name
    return trimmedPidcName;
  }

  /**
   * Method to create a text file in the given path with the given name and content
   *
   * @param filePath Path where text file needs to be created
   * @param trimmedName Name of the text file
   * @param fullName Content of the text file
   */
  public static void createTextFile(final String filePath, final String trimmedName, final String fullName) {
    logger.info("Creating a text file containing the full folder name as content");
    try {
      File txtFile = new File(filePath + File.separator + trimmedName + ".txt");
      boolean fileCreated = txtFile.createNewFile();
      if (fileCreated) {
        StringBuilder content = new StringBuilder("Complete folder name - ").append(fullName);
        Files.write(txtFile.toPath(), content.toString().getBytes(StandardCharsets.UTF_8));
        logger.info("New text File created with the original folder name as content");
      }
    }
    catch (IOException e) {
      logger.error("An error occured while creating text file for full folder name", e);
    }
  }

  /**
   * @param file File path that needs to be created
   */
  public static void createDirectory(final File file) {
    if (!file.exists()) {
      logger.debug("Directory does not exist. Creating directory {}", file.getAbsolutePath());
      file.mkdir();
      logger.debug("Directory created");
    }
  }


  /**
   * @param folder that needs to be deleted
   * @return
   * @throws IOException
   */
  private static boolean deleteFolder(final File folder) throws IOException {
    if (folder.isDirectory()) {
      File[] files = folder.listFiles();
      if (files != null) {
        for (File file : files) {
          deleteFolder(file);
        }
      }
    }
    return Files.deleteIfExists(Paths.get(folder.getPath()));
  }


  /**
   * @param outputZipFilePath
   * @param createBaseFoldStruForArch
   * @throws IcdmException
   */
  public static void roboCopyWpArchivalFileToArchival(final String outputZipFilePath,
      final String createBaseFoldStruForArch)
      throws IcdmException {
    // Src Path
    Path srcPath = Paths.get(outputZipFilePath);
    // dest path
    Path destPath = Paths.get(createBaseFoldStruForArch);

    String srcDir = srcPath.getParent().toString();
    logger.debug("Source Folder Path " + srcDir);
    String destDir = destPath.getParent().toString();
    logger.debug("Destination Folder Path " + destDir);
    try {
      File robocopyLog = new File(WP_ARCHIVAL_BASE_PATH + File.separator + "RoboCopy_Logs");
      if (!robocopyLog.exists()) {
        robocopyLog.mkdirs();
      }
      String logFileName = "robocopy_log_" + System.currentTimeMillis() + "_" + RandomStringUtils.randomNumeric(5);
      ProcessBuilder processBuilder = new ProcessBuilder("robocopy", srcDir, destDir, "/s", "/COPY:DT", "/r:1",
          "/log:\"" + robocopyLog.getPath() + File.separator + logFileName + ".log\"");
      Process process = processBuilder.start();
      boolean success = process.waitFor(120, TimeUnit.SECONDS);
      if (success) {
        logger.debug("Robocopy Command executed successfully from iCDM.");
      }
      else {
        logger.debug("Robocopy Command failed from iCDM");
        throw new IcdmException(BASELINE_FAILED_MSG);
      }
    }
    catch (IOException e) {
      logger.error(e.getLocalizedMessage(), e);
      throw new IcdmException(BASELINE_FAILED_MSG + e.getLocalizedMessage());
    }
    catch (InterruptedException e) {
      logger.error(e.getLocalizedMessage(), e);
      Thread.currentThread().interrupt();
      throw new IcdmException(BASELINE_FAILED_MSG + e.getLocalizedMessage());

    }

    logger.debug("Completed moving of file to Archival Folder " + createBaseFoldStruForArch);
  }

  /**
   * @param zipPath
   */
  public static void updateLastAccessDateWhileArchiving(final Path zipPath) {
    try {
      BasicFileAttributeView fileAttributeViewBefore =
          java.nio.file.Files.getFileAttributeView(zipPath, java.nio.file.attribute.BasicFileAttributeView.class);

      logger.info("Last Access Data for the File before 35Years change: {} ; Path : {}",
          fileAttributeViewBefore.readAttributes().lastAccessTime().toString(), zipPath.toString());

      // Get the current time
      LocalDate currentTime = LocalDate.now();

      // Get the current date and time
      LocalDateTime currentDateTime = LocalDateTime.now();

      ZoneId zone = ZoneId.systemDefault();

      // Add 35 years to the current time
      LocalDate next35Years = currentTime.plusYears(35);

      // get the number of days from current time to 35+ years
      long daysBetween = ChronoUnit.DAYS.between(currentTime, next35Years);

      // Add 35 years to the current last access time
      Instant newLastAccessTime = currentDateTime.atZone(zone).toInstant().plus(daysBetween, ChronoUnit.DAYS);

      // Set the new last access time for the file
      FileTime newFileTime = FileTime.from(newLastAccessTime);

      Files.setAttribute(zipPath, "basic:lastAccessTime", newFileTime, java.nio.file.LinkOption.NOFOLLOW_LINKS);

      BasicFileAttributeView fileAttributeViewAfter =
          java.nio.file.Files.getFileAttributeView(zipPath, java.nio.file.attribute.BasicFileAttributeView.class);

      logger.info("Last Access Data for the File after 35Years change : {} ; Path : {}",
          fileAttributeViewAfter.readAttributes().lastAccessTime().toString(), zipPath.toString());

    }
    catch (IOException e) {
      logger.error("An exception occured during updation of last access date for file", e);
    }
  }

  /**
   * @param wpName
   * @param respName
   * @return String - baseline name for WpArchival
   */
  public static String getArchivalBaselineName(final String wpName, final String respName) {
    StringBuilder archiveBaselineName = new StringBuilder();
    archiveBaselineName.append(ApicUtil.getCurrentTime(DateFormat.DATE_FORMAT_04));
    archiveBaselineName.append(UNDERSCORE);
    archiveBaselineName.append(wpName);
    archiveBaselineName.append(UNDERSCORE);
    archiveBaselineName.append(respName);
    return archiveBaselineName.toString();
  }

  /**
   * Fetch Bosch logo
   *
   * @param serviceData Service data
   * @return logo as byte array
   * @throws DataException Data Exception
   */
  public static byte[] getBoschLogo(final ServiceData serviceData) throws DataException {
    logger.debug("Retrieve report logo from DB");

    // Get logo from db
    IcdmFilesLoader fileLoader = new IcdmFilesLoader(serviceData);
    Map<String, byte[]> fileMap = fileLoader.getFiles(-6L, CDRConstants.REPORT_LOGO_NODE_TYPE);
    byte[] reportLogo = null;
    if ((fileMap != null) && (fileMap.size() > 0)) {
      reportLogo = fileMap.get(CDRConstants.PDF_REPORT_BOSCH_LOGO_IMAGE);
      logger.debug("Report logo retrieved");
    }
    return reportLogo;
  }


}
