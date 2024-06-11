/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.ThreadContext;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2LFileInfoProvider;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.bo.report.pdf.CombinedReportPdfExport;
import com.bosch.caltool.icdm.bo.report.pdf.CompliReviewPdfExport;
import com.bosch.caltool.icdm.bo.report.pdf.PidcAttributesPdfExport;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.FileNameUtil;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrExportModel;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetails;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetailsInput;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.RvwFile;
import com.bosch.caltool.icdm.model.cdr.WpArchival;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.user.User;


/**
 * @author EKIR1KOR
 */
public class WpArchivalBO extends AbstractSimpleBusinessObject {

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
   * Bosch logo as byte array
   */
  private byte[] boschLogo;

  /**
   * User object
   */
  private User user;

  private final WpArchival wpArchival;

  private boolean exceptionOccured = false;

  /**
   * Constant to hold the base path name of baseline files
   */
  private String baselineDirPath = null;

  /**
   * Review Result Id
   */
  private final List<Long> rvwResultIdList;

  /**
   * Extension for HEX file
   */
  private static final String HEX_EXT = ".hex";

  /**
   * Extension for Text file
   */
  private static final String TXT_EXT = ".txt";

  /**
   * Logger for this class
   */
  private static ILoggerAdapter logger = new Log4JLoggerAdapterImpl(LogManager.getLogger("WpArchivalBO"));

  /**
   * @return the baselineDirectory
   */
  public String getBaselineDirPath() {
    return this.baselineDirPath;
  }


  /**
   * @param serviceData
   * @param wpArchival
   * @param rvwResultIdList
   */
  public WpArchivalBO(final ServiceData serviceData, final WpArchival wpArchival, final List<Long> rvwResultIdList) {
    super(serviceData);
    this.wpArchival = wpArchival;
    this.rvwResultIdList = rvwResultIdList;
  }


  /**
   * Create ZIP files combining all the baseline files
   *
   * @return zipped baseline files as byte array
   * @throws IcdmException ICDM Exception
   * @throws IOException
   */
  public byte[] createBaselineFiles() throws IcdmException, IOException {
    String customerAttrValForPidcVer =
        new PidcVersionAttributeLoader(getServiceData()).getCustomerAttrValForPidcVer(this.wpArchival.getPidcVersId());
    PidcA2l pidcA2l = new PidcA2lLoader(getServiceData()).getDataObjectByID(this.wpArchival.getPidcA2lId());

    // To create the folder structure required for storing baseline files
    logger.debug("Creating folder structure for storing baseline files");
    File baseFile = WpArchivalUtil.createBasePath(customerAttrValForPidcVer);
    this.baselineDirPath = baseFile.getAbsolutePath();
    String filePath = WpArchivalUtil.createBaseFolderStructure(this.wpArchival, baseFile, customerAttrValForPidcVer,
        pidcA2l.getSdomPverName(), pidcA2l.getSdomPverVarName(), getPidc());

    // Get Bosch Logo required for adding into all PDF reports
    logger.debug("Fetching Bosch logo to include in PDF header");
    this.boschLogo = WpArchivalUtil.getBoschLogo(getServiceData());
    logger.debug("Bosch logo retrieved successfully");

    logger.debug("Fetching User information");
    this.user = new UserLoader(getServiceData()).getDataObjectByID(getServiceData().getUserId());
    logger.debug("User information retrieved");

    String requestId = ThreadContext.get(CommonUtilConstants.REQUEST_ID);
    String method = ThreadContext.get(CommonUtilConstants.METHOD);

    // A2L file
    CompletableFuture<Void> a2lExport = runA2lExport(filePath, requestId, method);

    // Review files report
    CompletableFuture<Void> reviewFilesExport = runReviewFilesExport(filePath, requestId, method);

    // PIDC Attributes Report
    CompletableFuture<Void> pidcExport = runPidcAttributesExport(filePath, requestId, method);

    // Compliance Review Reports
    CompletableFuture<Void> compliRvwExport = runCompliRvwExport(filePath, requestId, method);


    try {
      // Wait for all threads to complete before proceeding
      CompletableFuture.allOf(a2lExport, reviewFilesExport, pidcExport, compliRvwExport).join();
    }
    catch (CompletionException e) {
      this.exceptionOccured = true;
      a2lExport.cancel(true);
      reviewFilesExport.cancel(true);
      pidcExport.cancel(true);
      compliRvwExport.cancel(true);
      logger.error("Exception occured. Could not complete creation of baseline files", e);
    }

    byte[] baselineFiles = null;
    if (!this.exceptionOccured) {
      logger.debug("Converting the created baseline files into zip file");
      baselineFiles = zipBaselineFiles(pidcA2l);
    }

    return baselineFiles;
  }

  /**
   * Method to asynchronously run export of A2L file
   *
   * @param filePath
   * @param method
   * @param requestId
   * @return
   */
  private CompletableFuture<Void> runA2lExport(final String filePath, final String requestId, final String method) {
    return CompletableFuture.runAsync(() -> {
      try {
        ServiceData serviceData = new ServiceData();
        getServiceData().copyTo(serviceData, true);
        CommonUtils.setLogger(requestId, method);
        exportA2lFile(filePath, serviceData);
      }
      catch (IcdmException e) {
        this.exceptionOccured = true;
        logger.error("An exception occurred during the export of A2L file", e);
      }
    });
  }

  /**
   * Method to asynchronously run export of compli review files
   *
   * @param filePath
   * @param method
   * @param requestId
   * @return
   */
  private CompletableFuture<Void> runCompliRvwExport(final String filePath, final String requestId,
      final String method) {
    return CompletableFuture.runAsync(() -> {
      try {
        ServiceData serviceData = new ServiceData();
        getServiceData().copyTo(serviceData, true);
        CommonUtils.setLogger(requestId, method);
        exportComplianceCheckFiles(filePath, serviceData);
      }
      catch (IcdmException e) {
        this.exceptionOccured = true;
        logger.error("An exception occurred during the export of compliance check report", e);
      }
    });
  }

  /**
   * Method to asynchronously run export of review files
   *
   * @param filePath
   * @param method
   * @param requestId
   * @return
   */
  private CompletableFuture<Void> runReviewFilesExport(final String filePath, final String requestId,
      final String method) {
    return CompletableFuture.runAsync(() -> {
      try {
        ServiceData serviceData = new ServiceData();
        getServiceData().copyTo(serviceData, true);
        CommonUtils.setLogger(requestId, method);
        exportReviewRelatedFiles(filePath, serviceData);
      }
      catch (IcdmException e) {
        this.exceptionOccured = true;
        logger.error("An exception occurred during the export of review related files", e);
      }
    });
  }

  /**
   * Method to asynchronously run export of PIDC Attributes export
   *
   * @param filePath
   * @param method
   * @param requestId
   * @return
   */
  private CompletableFuture<Void> runPidcAttributesExport(final String filePath, final String requestId,
      final String method) {
    return CompletableFuture.runAsync(() -> {
      try {
        ServiceData serviceData = new ServiceData();
        getServiceData().copyTo(serviceData, true);
        CommonUtils.setLogger(requestId, method);
        exportPidcAttributes(filePath, this.wpArchival.getVariantId(), serviceData);
      }
      catch (IcdmException e) {
        this.exceptionOccured = true;
        logger.error("An exception occurred during the export of Pidc attributes", e);
      }
    });
  }

  /**
   * @param filePath
   * @param serviceData
   * @throws IcdmException
   */
  private void exportA2lFile(final String filePath, final ServiceData serviceData) throws IcdmException {
    logger.debug("Export of A2L File started");
    PidcA2l pidcA2l = new PidcA2lLoader(serviceData).getDataObjectByID(this.wpArchival.getPidcA2lId());
    byte[] a2lFileData = new A2LFileInfoProvider(serviceData).fetchA2LFileInfoSerialized(pidcA2l.getA2lFileId());
    if (a2lFileData != null) {
      logger.debug("Creating A2L file from file bytes");
      createFileFromBytes(filePath, a2lFileData, "A2L File", this.wpArchival.getA2lFilename());
    }
    logger.debug("Export of A2L File completed");
  }

  /**
   * Method to asynchronously run export of Compliance review and check files
   *
   * @param filePath
   * @param serviceData
   * @throws IcdmException
   */
  private void exportComplianceCheckFiles(final String filePath, final ServiceData serviceData) throws IcdmException {
    logger.debug("Export of compliance check report started");
    CompliReviewPdfExport compliReviewPdfExport =
        new CompliReviewPdfExport(serviceData, null, this.wpArchival, filePath);
    try {
      // Compliance Check Report
      compliReviewPdfExport.createCompliCheckPdfAReport();
    }
    catch (IOException e) {
      throw new IcdmException("Exception occurred during compliance check report creation", e);
    }
    logger.debug("Export of compliance check report completed");
  }

  /**
   * Export the PIDC Information and attributes into PDF/A
   *
   * @param filePath Folder path where the file needs to be exported
   * @param pidcVarId as variant id
   * @param serviceData
   * @throws IcdmException
   * @throws IOException
   */
  private void exportPidcAttributes(final String filePath, final Long pidcVarId, final ServiceData serviceData)
      throws IcdmException {
    // Fetching AttributeExportModel
    logger.info("Export of PIDC Attributes Information started");
    logger.info("Fetching AttributeExportModel in exportPidcAttributes started");
    AttributeLoader attrLdr = new AttributeLoader(serviceData);
    AttrExportModel attrExp = attrLdr.getAtrrExportModel();
    logger.debug("Fetching AttributeExportModel in exportPidcAttributes completed");

    // Fetch PIDC Version with details
    logger.info("Fetching PidcVersionWithDetails in exportPidcAttributes started");
    PidcVersionWithDetailsInput input = new PidcVersionWithDetailsInput();
    input.setPidcVersionId(this.wpArchival.getPidcVersId());
    PidcVersionWithDetails pidcVersionWithDetails =
        new PidcVersionLoader(serviceData).getPidcVersionWithDetails(input, "ALL", null);
    logger.info("Fetching PidcVersionWithDetails in exportPidcAttributes completed");

    // Exporting the fetched values into a PDF
    logger.info("Writing the PIDC Information in a PDF");
    PidcAttributesPdfExport pdf = new PidcAttributesPdfExport(serviceData, attrExp, pidcVersionWithDetails, filePath);
    pdf.setBoschLogo(this.boschLogo);
    pdf.setUser(this.user);
    try {
      pdf.createPidcAttrPdf(pidcVarId);
    }
    catch (IOException e) {
      throw new IcdmException("Exception while creating PIDC Attribues PDF", e);
    }
    logger.info("Export of PIDC Attributes Information completed");
  }


  /**
   * Export all types of files that are related to the creation of review results
   *
   * @param filePath Folder path where the file needs to be exported
   * @param serviceData
   * @throws IcdmException
   * @throws IOException
   */
  private void exportReviewRelatedFiles(final String filePath, final ServiceData serviceData) throws IcdmException {
    logger.debug("Export of Review related Files started");
    RvwFileLoader rvwFileLoader = new RvwFileLoader(serviceData);
    CDRReviewResultLoader cdrResLoader = new CDRReviewResultLoader(serviceData);

    // Review And Questionnaire Root Folder
    logger.debug("Creating Reviews and Questionnaires directory");
    String rvwRootFolder = filePath + File.separator + "Reviews and Questionnaires";
    StringBuilder txtFileContent = new StringBuilder();

    for (Long rvwResultId : this.rvwResultIdList) {
      TRvwResult tRvwResult = cdrResLoader.getEntityObject(rvwResultId);
      CDRReviewResult cdrReviewResult = cdrResLoader.createDataObject(tRvwResult);
      String reviewFilePath = createReviewFileDirectory(rvwRootFolder, cdrReviewResult, txtFileContent);

      // Create Combined Report for Review Result and Qnaire
      try {
        new CombinedReportPdfExport(serviceData, reviewFilePath).createCombinedReport(rvwResultId);
      }
      catch (IOException e) {
        throw new IcdmException("Exception while creating combined review report", e);
      }

      Map<Long, RvwFile> rvwFileMap = rvwFileLoader.getByResultId(tRvwResult);
      // Download Rvw file
      logger.debug("Exporting review files");
      if (CommonUtils.isNotEmpty(rvwFileMap)) {
        rvwFileMap.forEach(
            (rvwFileId, rvwFile) -> exportRvwFiles(rvwFileLoader, rvwRootFolder, reviewFilePath, rvwFileId, rvwFile));
      }
      logger.debug("Review Files getByResultId completed. Total records = {}", rvwFileMap.size());
    }

    if (CommonUtils.isNotEmptyString(txtFileContent.toString())) {
      WpArchivalUtil.createTextFile(rvwRootFolder, "Complete_folder_names", txtFileContent.toString());
    }

    logger.debug("Export of Review related files completed");
  }


  /**
   * @param pidcA2l
   * @return baselinefiles
   * @throws IcdmException
   * @throws IOException
   */
  private byte[] zipBaselineFiles(final PidcA2l pidcA2l) throws IcdmException, IOException {
    logger.debug("Creating a zipped file containing all baseline files");
    String baseName = FilenameUtils.getBaseName(this.baselineDirPath);
    String trimmedzipName = WpArchivalUtil.getTrimmedName(baseName, MAX_ALLOWED_CHARS_ZIPNAME_ARCHIVAL);
    String zipfileName = trimmedzipName + ".zip";
    logger.debug("ZipfileName : " + zipfileName);

    // Create a zipped file with all baseline files
    String outputZipFilePath = createZippedBaselineFiles(this.baselineDirPath, zipfileName, trimmedzipName);

    logger.debug("Getting the zipped file in byte array");
    byte[] fileAsBytes = null;
    try {
      fileAsBytes = CommonUtils.getFileAsByteArray(outputZipFilePath);
      logger.debug("Zipped file converted into byte array");
      // to copy the zip file to archival folder incase of series release
      wpArchivalProcess(pidcA2l, zipfileName, outputZipFilePath);
    }
    catch (IOException e) {
      logger.error("An error occured during the creation of byte array from file", e);
    }

    logger.debug("Deleting the baseline zip file from temp server path {}", outputZipFilePath);
    deleteDirectory(new File(outputZipFilePath));
    logger.debug("Baseline zip file deletion completed");

    return fileAsBytes;
  }


  /**
   * @param pidcA2l
   * @param zipfileName
   * @param outputZipFilePath
   * @throws IcdmException
   */
  private void wpArchivalProcess(final PidcA2l pidcA2l, final String zipfileName, final String outputZipFilePath)
      throws IcdmException {
    // Create the base folder structure and get it ready for archival
    File wpArchivalFolder =
        new File(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.DATAASSESSMENT_ARCHIVAL_FOLDER) +
            File.separator + CDRConstants.WP_ARCHIVAL_FOLDER);
    if (!wpArchivalFolder.exists()) {
      wpArchivalFolder.mkdirs();
    }
    String createBaseFolderStructureForArchival = WpArchivalUtil.createBaseFolderStructureForArchival(getPidc(),
        this.wpArchival, wpArchivalFolder, pidcA2l.getSdomPverName(), pidcA2l.getSdomPverVarName());
    logger.debug("Created WP Archival Folder");
    logger.debug("Started moving of Zipped file to Archival Folder " + createBaseFolderStructureForArchival);
    StringBuilder arcZipPath = new StringBuilder();
    arcZipPath.append(createBaseFolderStructureForArchival);
    arcZipPath.append(File.separator);
    arcZipPath.append(zipfileName);

    // to update the last access date of the zip file to 35+years
    WpArchivalUtil.updateLastAccessDateWhileArchiving(Paths.get(outputZipFilePath));
    // Logic to move wp archival Generated Files to Archival Schema
    WpArchivalUtil.roboCopyWpArchivalFileToArchival(outputZipFilePath, arcZipPath.toString());
  }

  /**
   * Create a zipped file of baseline files
   *
   * @param baselinePath Base path of the baseline files
   * @param zipfileName Name of the zip file
   * @return
   * @throws IOException
   */
  private String createZippedBaselineFiles(final String baselinePath, final String zipfileName,
      final String trimmedzipName)
      throws IOException {

    logger.debug("Compressing all the baseline files into a single zipped file");

    String zipFileDir = WP_ARCHIVAL_BASE_PATH + trimmedzipName + "_DIR";
    String zipFilePath = zipFileDir + File.separator + zipfileName;
    // Create the Directory
    Files.createDirectory(Paths.get(zipFileDir));

    logger.debug("zip file path : {}", zipFilePath);

    // Compress output files to a single zip file
    try {
      ZipUtils.zip(Paths.get(baselinePath), Paths.get(zipFilePath));
    }
    catch (IOException e) {
      logger.error("An exception occured during the creation of zipped baseline files", e);
    }

    logger.debug("Baseline files compressed into a Zip file");

    // Delete zip file's original folder
    logger.debug("Delete zip file's original folder {}", baselinePath);
    deleteDirectory(new File(baselinePath));
    logger.debug("Folder deletion completed");

    logger.info("Baseline zip file creation completed. File : {} ; Path : {}", zipfileName, zipFilePath);
    return zipFilePath;
  }

  /**
   * @param rvwFileLoader
   * @param rvwRootFolder
   * @param reviewFilePath
   * @param rvwFileId
   * @param rvwFile
   */
  private void exportRvwFiles(final RvwFileLoader rvwFileLoader, final String rvwRootFolder,
      final String reviewFilePath, final Long rvwFileId, final RvwFile rvwFile) {
    boolean canSkipThisFile = false;
    // check for Hex File
    // if the Hex file is already available then a TXT file has to be created with the path to the hex file as content
    if (rvwFile.getName().toLowerCase().contains(HEX_EXT)) {
      canSkipThisFile = checkForHexFileAndCreateTxtFile(rvwRootFolder, reviewFilePath, rvwFile, canSkipThisFile);
    }
    if (!canSkipThisFile) {
      try {
        byte[] rvwFileData = rvwFileLoader.getRvwFile(rvwFileId);
        createFileFromBytes(reviewFilePath, rvwFileData, rvwFile.getName());
      }
      catch (IcdmException ex) {
        this.exceptionOccured = true;
        logger.error("An error occured while fetching review files", ex);
      }
    }
  }

  /**
   * @param rvwRootFolder
   * @param reviewFilePath
   * @param rvwFile
   * @param canSkipThisFile
   * @return
   */
  private boolean checkForHexFileAndCreateTxtFile(final String rvwRootFolder, final String reviewFilePath,
      final RvwFile rvwFile, boolean canSkipThisFile) {
    String filenameWithoutEXt = rvwFile.getName().replace(HEX_EXT, "");
    File rvwRootDir = new File(rvwRootFolder);
    for (Object fileObj : FileUtils.listFiles(rvwRootDir, null, true)) {
      File file = (File) fileObj;
      if (file.getName().equals(rvwFile.getName())) {
        File crtTxtFile = new File(reviewFilePath + File.separator + filenameWithoutEXt + TXT_EXT);
        try {
          if (crtTxtFile.createNewFile()) {
            Files.write(crtTxtFile.toPath(), file.getAbsolutePath().getBytes(StandardCharsets.UTF_8));
          }
          canSkipThisFile = true;
          // first identified hex file path alone has to be stored in txt file
          break;
        }
        catch (IOException e) {
          CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
        }
      }
    }
    return canSkipThisFile;
  }

  /**
   * @param filePath
   * @param cdrReviewResult
   * @param txtFileContent
   * @return
   */
  private String createReviewFileDirectory(final String filePath, final CDRReviewResult cdrReviewResult,
      final StringBuilder txtFileContent) {
    logger.debug("Creating directory for storing review files");
    String reviewResultName =
        FileNameUtil.formatFileName(cdrReviewResult.getName(), ApicConstants.INVALID_CHAR_PTRN_EXCLUD_SPACE);

    StringBuilder fullRvwNameBuilder = new StringBuilder();
    fullRvwNameBuilder.append(cdrReviewResult.getId());
    fullRvwNameBuilder.append(" ");
    fullRvwNameBuilder.append(reviewResultName);
    String rvwResultName = fullRvwNameBuilder.toString();

    if (reviewResultName.length() > 10) {
      logger.debug("Trimming the review result folder name since it exceeds the maximum allowed length");
      txtFileContent.append(rvwResultName).append("\n");
      StringBuilder trimmedRvwName = new StringBuilder();
      trimmedRvwName.append(cdrReviewResult.getId());
      trimmedRvwName.append(" ");
      trimmedRvwName.append(WpArchivalUtil.getTrimmedName(reviewResultName, 10));
      rvwResultName = trimmedRvwName.toString();
    }

    String rvwFilePath = filePath + File.separator + rvwResultName;
    File file = new File(rvwFilePath);
    WpArchivalUtil.createDirectory(file);
    return file.getPath().trim();
  }

  /**
   * @return Pidc for the current WpArchival model
   * @throws DataException
   */
  private Pidc getPidc() throws DataException {
    return new PidcLoader(getServiceData()).getDataObjectByID(new PidcVersionLoader(getServiceData())
        .getEntityObject(this.wpArchival.getPidcVersId()).getTabvProjectidcard().getProjectId());
  }


  /**
   * Method to write file data from byte[] to a physical file location
   *
   * @param filePath
   * @param fileData
   * @param fileName
   * @throws IcdmException
   */
  private void createFileFromBytes(final String filePath, final byte[] fileData, final String fileName)
      throws IcdmException {
    try {
      // Writing Files to disk
      logger.debug("Writing files to disk ... ");
      String outputFilePath = filePath + File.separator + fileName;
      CommonUtils.createFile(outputFilePath, fileData);
      logger.debug("File created in path: {}", outputFilePath);
    }
    catch (IOException exp) {
      throw new IcdmException("Creation of files failed", exp);
    }

  }

  /**
   * Method to write file data from byte[] to a physical file location in a directory
   *
   * @param string
   * @param a2lBytes
   * @throws IcdmException
   */
  private void createFileFromBytes(final String filePath, final byte[] fileData, final String dirName,
      final String fileName)
      throws IcdmException {

    File file = new File(filePath + File.separator + dirName);
    if (!file.exists()) {
      logger.debug("Directory does not exist. Creating directory {}", dirName);
      file.mkdir();
      logger.debug("Directory created");
    }

    String inputDataPath = file.getAbsolutePath();
    try {
      // Writing Files to disk
      logger.debug("Writing files to disk ");
      String outputFilePath = inputDataPath + File.separator + fileName;
      CommonUtils.createFile(outputFilePath, fileData);
      logger.debug("File created in path: {}", outputFilePath);
    }
    catch (IOException exp) {
      throw new IcdmException("Creation of files failed", exp);
    }
  }


  /**
   * Method to delete original directory recursively
   *
   * @param file Directory to be deleted
   */
  public void deleteDirectory(final File file) {
    if (file.isDirectory()) {

      // If the directory is not empty, the delete all children first
      if (file.list().length != 0) {
        // recursive delete
        logger.debug(" Deleting children of Dir - {}", file.getName());
        Arrays.stream(file.list()).map(c -> new File(file, c)).forEach(this::deleteDirectory);
      }

      // check the directory again, if empty then delete it
      if (file.list().length == 0) {
        delete(file.toPath());
        logger.debug("  Deleted Dir - {}", file.getName());
      }
    }
    else {
      // if file, then delete it
      delete(file.toPath());
      logger.debug("  Deleted File - {}", file.getName());
    }
  }

  /**
   * To delete the given path
   *
   * @param path Path to be deleted
   */
  private void delete(final java.nio.file.Path path) {
    try {
      Files.delete(path);
    }
    catch (IOException e) {
      logger.info(e.getMessage(), e);
    }
  }
}
