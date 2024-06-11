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
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentReport;

/**
 * @author TRL1COB
 */
public class DataAssessmentUtil {

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

  private static String DATA_ASSESSMENT_TXT_FILES = "DA_TXT_FILE_";

  /**
   * Logger for this class
   */
  private static ILoggerAdapter logger = new Log4JLoggerAdapterImpl(LogManager.getLogger("DataAssessmentUtil"));

  /**
   * Temp path for storing baseline files
   */
  public static final String DATA_ASSESSMENT_BASE_PATH = Messages.getString("SERVICE_WORK_DIR") + "//DATA_ASSESSMENT//";

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


  private DataAssessmentUtil() {
    // Private Constructor
  }

  /**
   * Create base path for storing baseline files
   *
   * @param customerName Customer Name
   * @return BasePath of baseline directory
   */
  public static File createBasePath(final String customerName) {
    // Check base directory, create if necessary
    File file = new File(DATA_ASSESSMENT_BASE_PATH);
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
  private static String getCustomerFolderName(final String customerName) {
    StringBuilder custNameDirBuilder = new StringBuilder(customerName);
    custNameDirBuilder.append(UNDERSCORE).append(RandomStringUtils.randomNumeric(5));
    return FileNameUtil.formatFileName(custNameDirBuilder.toString(), ApicConstants.INVALID_CHAR_PTRN_EXCLUD_SPACE);
  }

  /**
   * Create folder directories in case they are not available
   *
   * @param dataAssmntReport Data Assessment Report model
   * @param baseFile Base path of the Baseline files folder
   * @param dataAssmntId Data assessment ID
   * @param customerName Name of the customer
   * @param sdomPverName pver name
   * @param sdomPverVar pver variant
   * @return Path of the folder structure
   */
  public static String createBaseFolderStructure(final DataAssessmentReport dataAssmntReport, final File baseFile,
      final Long dataAssmntId, final String customerName, final String sdomPverName, final String sdomPverVar) {

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
        new File(file.getAbsolutePath() + File.separator + getPidcFolderName(dataAssmntReport, file.getAbsolutePath()));
    createDirectory(file);

    // create pidc variant Folder name
    file = new File(file.getAbsolutePath() + File.separator +
        getPidcVariantName(dataAssmntReport, file.getAbsolutePath(), sdomPverName, sdomPverVar));
    createDirectory(file);

    // create baseline folder name
    file = new File(file.getAbsolutePath() + File.separator +
        getBaselineFolderName(dataAssmntReport, dataAssmntId, file.getAbsolutePath()));
    createDirectory(file);

    return file.getAbsolutePath();

  }


  /**
   * PIDC folder name for creating file directory
   *
   * @param dataAssmntReport
   * @param filePath
   * @return
   */
  private static String getPidcFolderName(final DataAssessmentReport dataAssmntReport, final String filePath) {
    logger.debug("Fetching the PIDC folder name");

    String pidcName =
        FileNameUtil.formatFileName(dataAssmntReport.getPidcName(), ApicConstants.INVALID_CHAR_PTRN_EXCLUD_SPACE);

    String fullPidcFolderName = framePidcFolderName(dataAssmntReport, pidcName);

    logger.debug("Trimming the PIDC name to limited length");
    String trimmedPidcName = getTrimmedName(pidcName, MAX_ALLOWED_CHARS);

    // Name of the text file will have IDs included followed by trimmed pidc name
    String txtFileName = framePidcFolderName(dataAssmntReport, trimmedPidcName);

    // Contents of the text file will have the full path including ids and pidc name without trimming
    createTextFile(filePath, txtFileName, fullPidcFolderName);

    // Name of the actual directory will have only the trimmed name
    return trimmedPidcName;
  }


  /**
   * Frame the PIDC folder name for creating file directory
   *
   * @param dataAssmntReport
   * @param pidcName
   */
  private static String framePidcFolderName(final DataAssessmentReport dataAssmntReport, final String pidcName) {

    // pidc id + pidc variant id + pidc name
    StringBuilder pidcFolderName = new StringBuilder();
    pidcFolderName.append(dataAssmntReport.getPidcId());
    pidcFolderName.append(EMPTY_STR);
    pidcFolderName.append(dataAssmntReport.getPidcVersId());
    pidcFolderName.append(EMPTY_STR);
    pidcFolderName.append(pidcName);

    return pidcFolderName.toString();
  }

  /**
   * PIDC variant folder name for creating file directory
   *
   * @param dataAssmntReport
   * @param sdomPverName
   * @param sdomPverVarName
   * @return
   */
  private static String getPidcVariantName(final DataAssessmentReport dataAssmntReport, final String filePath,
      final String sdomPverName, final String sdomPverVar) {

    StringBuilder varSdomNameBuilder = pidcVarStringBuilder(dataAssmntReport, sdomPverName, sdomPverVar);

    String varSdomName =
        FileNameUtil.formatFileName(varSdomNameBuilder.toString(), ApicConstants.INVALID_CHAR_PTRN_EXCLUD_SPACE);
    Long pidcVarId = dataAssmntReport.getPidcVariantId();

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
   * Frame the PIDC variant name for creating file directory
   *
   * @param dataAssmntReport
   * @param varSdomName
   */
  private static String framePidcVarFolderName(final Long pidcVarId, final String varSdomName) {

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
   * Baseline folder name for creating file directory
   *
   * @param dataAssmntReport Data Assessment Report model
   * @param dataAssmntId Data Assessment ID
   * @param filePath File path
   * @return Name of the baseline folder
   */
  public static String getBaselineFolderName(final DataAssessmentReport dataAssmntReport, final Long dataAssmntId,
      final String filePath) {

    String baselineName =
        FileNameUtil.formatFileName(dataAssmntReport.getBaselineName(), ApicConstants.INVALID_CHAR_PTRN_EXCLUD_SPACE);

    logger.debug("Fetching the baseline folder name");
    String fullbaselineFolderName = frameBaselineFolderName(dataAssmntId, baselineName);

    logger.debug("Trimming the baseline folder name to limited length");
    String trimmedBaselineName = getTrimmedName(baselineName, MAX_ALLOWED_CHARS);

    // Name of the text file will have IDs included followed by trimmed pidc name
    String txtFileName = frameBaselineFolderName(dataAssmntId, trimmedBaselineName);

    // Contents of the text file will have the full path including ids and pidc name without trimming
    createTextFile(filePath, txtFileName, fullbaselineFolderName);

    // Name of the actual directory will have only the trimmed name
    return trimmedBaselineName;

  }

  /**
   * Frame the Baseline folder name for creating file directory
   *
   * @param dataAssmntId
   * @param baselineName
   * @return
   */
  private static String frameBaselineFolderName(final Long dataAssmntId, final String baselineName) {

    // Baseline ID + Baseline creation date + Baseline Name
    StringBuilder baselineFolderNameBuilder = new StringBuilder();
    baselineFolderNameBuilder.append(String.valueOf(dataAssmntId));
    baselineFolderNameBuilder.append(EMPTY_STR);
    baselineFolderNameBuilder.append(ApicUtil.getCurrentTime(DateFormat.DATE_FORMAT_10));
    baselineFolderNameBuilder.append(EMPTY_STR);
    baselineFolderNameBuilder.append(baselineName);

    return baselineFolderNameBuilder.toString();
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
   * Create File directory to save the PDF report
   *
   * @param filepath Folder Path
   * @param directoryName Name of the directory
   * @return Created file path
   */
  public static File createFileDirectory(final String filepath, final String directoryName) {
    // Check sub-directory to save PDF,create if necessary
    File file = new File(filepath + File.separator + directoryName);
    createDirectory(file);
    return file;
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

  /**
   * Method to trim the given name to given number of characters
   *
   * @param fullName Full name String
   * @param allowedLength Max allowed length of string
   * @return trimmed string
   */
  public static String getTrimmedName(final String fullName, final int allowedLength) {
    String trimmedName = fullName;
    if (fullName.length() > allowedLength) {
      trimmedName = StringUtils.abbreviateMiddle(fullName, "..", allowedLength);
    }
    return trimmedName;
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

  public static void createArchiveTextFile(final String filePath, final String trimmedName, final String fullName)
      throws IcdmException {
    logger.info("Creating a text file containing the full folder name as content");
    try {
      File txtFile = new File(filePath + File.separator + trimmedName + ".txt");
      if (!txtFile.exists()) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH_mm_ss_SSS");
        String currentDateTimeString = currentDateTime.format(formatter);
        String folderPath = DataAssessmentUtil.DATA_ASSESSMENT_BASE_PATH +
            DataAssessmentUtil.DATA_ASSESSMENT_TXT_FILES + currentDateTimeString;
        File folder = new File(folderPath);
        folder.mkdirs();
        File tempTxtFile =
            new File(DataAssessmentUtil.DATA_ASSESSMENT_BASE_PATH + DataAssessmentUtil.DATA_ASSESSMENT_TXT_FILES +
                currentDateTimeString + File.separator + trimmedName + ".txt");
        boolean fileCreated = tempTxtFile.createNewFile();
        if (fileCreated) {
          StringBuilder content = new StringBuilder("Complete folder name - ").append(fullName);
          Files.write(tempTxtFile.toPath(), content.toString().getBytes(StandardCharsets.UTF_8));
          logger.info("New text File created with the original folder name as content");
        }
        updateLastAccessDateWhileArchiving(
            Paths.get(DataAssessmentUtil.DATA_ASSESSMENT_BASE_PATH + DataAssessmentUtil.DATA_ASSESSMENT_TXT_FILES +
                currentDateTimeString + File.separator + trimmedName + ".txt"));
        roboCopyDataAssessmentFileToArchival(
            DataAssessmentUtil.DATA_ASSESSMENT_BASE_PATH + DataAssessmentUtil.DATA_ASSESSMENT_TXT_FILES +
                currentDateTimeString + File.separator + trimmedName + ".txt",
            filePath + File.separator + trimmedName + ".txt");
        deleteFolder(folder);
      }
    }
    catch (IOException e) {
      logger.error("An error occured while creating text file for full folder name", e);
    }
  }


  /**
   * Create folder directories in case they are not available
   *
   * @param dataAssmntReport Data Assessment Report model
   * @param baseFile Base path of the Baseline files folder
   * @param dataAssmntId Data assessment ID
   * @param a2lFile A2L File
   * @param customerName Name of the customer
   * @return Path of the folder structure
   * @throws IcdmException
   */
  public static String createBaseFolderStructureForArchival(final DataAssessmentReport dataAssmntReport,
      final File baseFile, final Long dataAssmntId, final String sdomPverName, final String sdomPverVar)
      throws IcdmException {

    File file = new File(baseFile.getAbsolutePath());

    // create pidc Folder name
    file = new File(file.getAbsolutePath() + File.separator +
        getPidcFolderNameForArchival(dataAssmntReport, file.getAbsolutePath()));
    createDirectory(file);

    // create pidc variant Folder name
    file = new File(file.getAbsolutePath() + File.separator +
        getPidcVariantNameArchival(dataAssmntReport, sdomPverName, sdomPverVar, file.getAbsolutePath()));
    createDirectory(file);

    // create baseline folder name
    file = new File(file.getAbsolutePath() + File.separator +
        getBaselineFolderNameArchival(dataAssmntReport, dataAssmntId, file.getAbsolutePath()));
    createDirectory(file);

    return file.getAbsolutePath();

  }

  /**
   * PIDC folder name for creating file directory For Archival
   *
   * @param dataAssmntReport
   * @param filePath
   * @return
   * @throws IcdmException
   */
  private static String getPidcFolderNameForArchival(final DataAssessmentReport dataAssmntReport, final String filePath)
      throws IcdmException {
    logger.debug("Fetching the PIDC folder name");

    String fullPidcFolderName = framePidcFolderName(dataAssmntReport, dataAssmntReport.getPidcVersName());

    logger.debug("Trimming the PIDC name to limited length");
    String trimmedPidcName = getTrimmedName(dataAssmntReport.getPidcVersName(), MAX_ALLOWED_CHARS_PIDC_ARCHIVAL);

    // Name of the text file will have IDs included followed by trimmed pidc name
    String txtFileName = framePidcFolderName(dataAssmntReport, trimmedPidcName);

    // Contents of the text file will have the full path including ids and pidc name without trimming
    createArchiveTextFile(filePath, txtFileName, fullPidcFolderName);

    // Name of the actual directory will have only the trimmed name
    return txtFileName;
  }

  /**
   * PIDC variant folder name for creating file directory in Archival Folder
   *
   * @param dataAssmntReport
   * @param sdomPverName
   * @param sdomPverVarName
   * @return
   * @throws IcdmException
   */
  private static String getPidcVariantNameArchival(final DataAssessmentReport dataAssmntReport,
      final String sdomPverName, final String sdomPverVar, final String filePath)
      throws IcdmException {

    StringBuilder varSdomNameBuilder = pidcVarStringBuilder(dataAssmntReport, sdomPverName, sdomPverVar);

    String varSdomName =
        FileNameUtil.formatFileName(varSdomNameBuilder.toString(), ApicConstants.INVALID_CHAR_PTRN_EXCLUD_SPACE);
    Long pidcVarId = dataAssmntReport.getPidcVariantId();

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
   * @param dataAssmntReport
   * @param a2lFile
   * @return
   */
  private static StringBuilder pidcVarStringBuilder(final DataAssessmentReport dataAssmntReport,
      final String sdomPverName, final String sdomPverVar) {
    // pidc variant name + sdom pver name + sdom pver variant
    StringBuilder varSdomNameBuilder = new StringBuilder();
    if (CommonUtils.isNotNull(dataAssmntReport.getPidcVariantId())) {
      varSdomNameBuilder.append(dataAssmntReport.getPidcVariantName());
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
   * Baseline folder name for creating file directory
   *
   * @param dataAssmntReport Data Assessment Report model
   * @param dataAssmntId Data Assessment ID
   * @param filePath File path
   * @return Name of the baseline folder
   * @throws IcdmException
   */
  public static String getBaselineFolderNameArchival(final DataAssessmentReport dataAssmntReport,
      final Long dataAssmntId, final String filePath)
      throws IcdmException {

    String baselineName =
        FileNameUtil.formatFileName(dataAssmntReport.getBaselineName(), ApicConstants.INVALID_CHAR_PTRN_EXCLUD_SPACE);

    logger.debug("Fetching the baseline folder name");
    String fullbaselineFolderName = frameBaselineFolderName(dataAssmntId, baselineName);

    logger.debug("Trimming the baseline folder name to limited length");
    String trimmedBaselineName = getTrimmedName(baselineName, MAX_ALLOWED_CHARS_BASELINE_ARCHIVAL);

    // Name of the text file will have IDs included followed by trimmed pidc name
    String txtFileName = frameBaselineFolderName(dataAssmntId, trimmedBaselineName);

    // Contents of the text file will have the full path including ids and pidc name without trimming
    createArchiveTextFile(filePath, txtFileName, fullbaselineFolderName);

    // Name of the actual directory will have only the trimmed name
    return txtFileName;

  }

  /**
   * @param zipFilePath
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

  public static void roboCopyDataAssessmentFileToArchival(final String outputZipFilePath,
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
      File robocopyLog = new File(DATA_ASSESSMENT_BASE_PATH + File.separator + "RoboCopy_Logs");
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
        throw new IcdmException(
            "Failed to copy Baseline File to Archival Folder. Please contact iCDM Hotline for support.");
      }
    }
    catch (IOException | InterruptedException e) {
      logger.error(e.getLocalizedMessage(), e);
      throw new IcdmException(
          "Failed to copy Baseline File to Archival Folder. Please contact iCDM Hotline for support." +
              e.getLocalizedMessage());
    }

    logger.debug("Completed moving of file to Archival Folder " + createBaseFoldStruForArch);
  }


  private static boolean deleteFolder(final File folder) {
    if (folder.isDirectory()) {
      File[] files = folder.listFiles();
      if (files != null) {
        for (File file : files) {
          deleteFolder(file);
        }
      }
    }
    return folder.delete();
  }
}
