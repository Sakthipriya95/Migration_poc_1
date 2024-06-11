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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.ThreadContext;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2LFileInfoLoader;
import com.bosch.caltool.icdm.bo.a2l.A2LFileInfoProvider;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.comphex.CompHexWithCDFxProcess;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.bo.report.pdf.CombinedReportPdfExport;
import com.bosch.caltool.icdm.bo.report.pdf.CompareHexPdfExport;
import com.bosch.caltool.icdm.bo.report.pdf.CompliReviewPdfExport;
import com.bosch.caltool.icdm.bo.report.pdf.DataAssessmentPdfExport;
import com.bosch.caltool.icdm.bo.report.pdf.PidcAttributesPdfExport;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.FileNameUtil;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrExportModel;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetails;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetailsInput;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.RvwFile;
import com.bosch.caltool.icdm.model.dataassessment.DaCompareHexParam;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentReport;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.user.User;


/**
 * @author TRL1COB
 */
public class DataAssessmentBO extends AbstractSimpleBusinessObject {

  /**
   * Extension for Text file
   */
  private static final String TXT_EXT = ".txt";

  /**
   * Extension for HEX file
   */
  private static final String HEX_EXT = ".hex";

  /**
   * Data Assessment Report model
   */
  private final DataAssessmentReport dataAssessmentReport;

  /**
   * DatAssessment/Baseline ID
   */
  private final Long dataAssmntId;

  /**
   * Constant to hold the base path name of baseline files
   */
  private String baselineDirPath = null;

  /**
   * Bosch logo as byte array
   */
  private byte[] boschLogo;

  /**
   * User object
   */
  private User user;

  /**
   * Boolean to store the status of exception
   */
  private boolean exceptionOccured = false;


  /**
   * @return the baselineDirectory
   */
  public String getBaselineDirPath() {
    return this.baselineDirPath;
  }


  /**
   * Constructor
   *
   * @param serviceData ServiceData
   * @param dataAssessmentReport Data Assessment Report model
   * @param dataAssmntId - Data assessment/Baseline ID
   */
  public DataAssessmentBO(final ServiceData serviceData, final DataAssessmentReport dataAssessmentReport,
      final long dataAssmntId) {
    super(serviceData);
    this.dataAssessmentReport = dataAssessmentReport;
    this.dataAssmntId = dataAssmntId;
  }

  /**
   * Create ZIP files combining all the baseline files
   *
   * @return zipped baseline files as byte array
   * @throws IcdmException ICDM Exception
   * @throws IOException
   */
  public byte[] createBaselineFiles() throws IcdmException, IOException {
    String customerAttrValForPidcVer = new PidcVersionAttributeLoader(getServiceData())
        .getCustomerAttrValForPidcVer(this.dataAssessmentReport.getPidcVersId());

    A2LFile a2lFile =
        new A2LFileInfoLoader(getServiceData()).getDataObjectByID(this.dataAssessmentReport.getA2lFileId());

    // To create the folder structure required for storing baseline files
    getLogger().debug("Creating folder structure for storing baseline files");
    File baseFile = DataAssessmentUtil.createBasePath(customerAttrValForPidcVer);
    this.baselineDirPath = baseFile.getAbsolutePath();
    String filePath = DataAssessmentUtil.createBaseFolderStructure(this.dataAssessmentReport, baseFile,
        this.dataAssmntId, customerAttrValForPidcVer, a2lFile.getSdomPverName(), a2lFile.getSdomPverVariant());

    // Get Bosch Logo required for adding into all PDF reports
    getLogger().debug("Fetching Bosch logo to include in PDF header");
    this.boschLogo = DataAssessmentUtil.getBoschLogo(getServiceData());
    getLogger().debug("Bosch logo retrieved successfully");

    getLogger().debug("Fetching User information");
    this.user = new UserLoader(getServiceData()).getDataObjectByID(getServiceData().getUserId());
    getLogger().debug("User information retrieved");

    String requestId = ThreadContext.get(CommonUtilConstants.REQUEST_ID);
    String method = ThreadContext.get(CommonUtilConstants.METHOD);

    // A2L file
    CompletableFuture<Void> a2lExport = runA2lExport(filePath, requestId, method);

    // HEX file
    CompletableFuture<Void> hexExport = runHexFileExport(filePath, requestId, method);

    // Review files report
    CompletableFuture<Void> reviewFilesExport = runReviewFilesExport(filePath, requestId, method);

    // Compare Hex Report
    CompletableFuture<Void> compHexExport = runCompareHexExport(filePath, requestId, method);

    // PIDC Attributes Report
    CompletableFuture<Void> pidcExport = runPidcAttributesExport(filePath, requestId, method);

    // Compliance Review and Compliance Check Reports
    CompletableFuture<Void> compliRvwExport = runCompliRvwExport(filePath, requestId, method);

    // Data Assessment Report
    CompletableFuture<Void> dataAssmntExport = runDataAssmntExport(filePath, requestId, method);

    try {
      // Wait for all threads to complete before proceeding
      CompletableFuture
          .allOf(a2lExport, hexExport, reviewFilesExport, compHexExport, pidcExport, compliRvwExport, dataAssmntExport)
          .join();
    }
    catch (CompletionException e) {
      this.exceptionOccured = true;
      a2lExport.cancel(true);
      hexExport.cancel(true);
      reviewFilesExport.cancel(true);
      compHexExport.cancel(true);
      pidcExport.cancel(true);
      compliRvwExport.cancel(true);
      dataAssmntExport.cancel(true);

      getLogger().error("Exception occured. Could not complete creation of baseline files", e);
    }

    byte[] baselineFiles = null;
    if (!this.exceptionOccured) {
      getLogger().debug("Converting the created baseline files into zip file");
      baselineFiles = zipBaselineFiles(a2lFile);
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
        getLogger().error("An exception occurred during the export of A2L file", e);
      }
    });
  }


  /**
   * Method to asynchronously run export of HEX file
   *
   * @param filePath
   * @param method
   * @param requestId
   * @return
   */
  private CompletableFuture<Void> runHexFileExport(final String filePath, final String requestId, final String method) {
    return CompletableFuture.runAsync(() -> {
      try {
        CommonUtils.setLogger(requestId, method);
        exportHexFile(filePath);
      }
      catch (IcdmException e) {
        this.exceptionOccured = true;
        getLogger().error("An exception occurred during the export of HEX file", e);
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
        getLogger().error("An exception occurred during the export of review related files", e);
      }
    });
  }


  /**
   * Method to asynchronously run export of compare HEX report
   *
   * @param filePath
   * @param method
   * @param requestId
   * @return
   */
  private CompletableFuture<Void> runCompareHexExport(final String filePath, final String requestId,
      final String method) {
    return CompletableFuture.runAsync(() -> {
      try {
        ServiceData serviceData = new ServiceData();
        getServiceData().copyTo(serviceData, true);
        CommonUtils.setLogger(requestId, method);
        exportCompareHexReport(filePath, serviceData);
      }
      catch (IcdmException e) {
        this.exceptionOccured = true;
        getLogger().error("An exception occurred during the export of Compare HEX report", e);
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
        exportPidcAttributes(filePath, this.dataAssessmentReport.getPidcVariantId(), serviceData);
      }
      catch (IcdmException e) {
        this.exceptionOccured = true;
        getLogger().error("An exception occurred during the export of Pidc attributes", e);
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
        exportCompliReviewAndCheckFiles(filePath, serviceData);
      }
      catch (IcdmException e) {
        this.exceptionOccured = true;
        getLogger().error("An exception occurred during the export of Compli review and compli check files", e);
      }
    });
  }


  /**
   * Method to asynchronously run export of data assessment report
   *
   * @param filePath
   * @param method
   * @param requestId
   * @return
   */
  private CompletableFuture<Void> runDataAssmntExport(final String filePath, final String requestId,
      final String method) {
    return CompletableFuture.runAsync(() -> {
      try {
        ServiceData serviceData = new ServiceData();
        getServiceData().copyTo(serviceData, true);
        CommonUtils.setLogger(requestId, method);
        exportDataAssmntReport(filePath, serviceData);
      }
      catch (IcdmException e) {
        this.exceptionOccured = true;
        getLogger().error("An exception occurred during the export of Data Assessment report", e);
      }
    });
  }


  /**
   * Export A2L File using A2L file id param filePath Folder path where the file needs to be exported
   *
   * @param serviceData
   * @throws IcdmException
   */
  private void exportA2lFile(final String filePath, final ServiceData serviceData) throws IcdmException {
    getLogger().debug("Export of A2L File started");
    byte[] a2lFileData =
        new A2LFileInfoProvider(serviceData).fetchA2LFileInfoSerialized(this.dataAssessmentReport.getA2lFileId());
    if (a2lFileData != null) {
      getLogger().debug("Creating A2L file from file bytes");
      createFileFromBytes(filePath, a2lFileData, "A2L and Hex File", this.dataAssessmentReport.getA2lFileName());
    }
    getLogger().debug("Export of A2L File completed");
  }

  /**
   * Export HEX file to the given path
   *
   * @param filePath Folder path where the file needs to be exported
   * @throws IcdmException ICDM Exception in case of errors
   */
  private void exportHexFile(final String filePath) throws IcdmException {
    getLogger().debug("Export of HEX File started");
    File hexFile = null;
    // Fetch HEX file from compare hex results
    if (CommonUtils.isNotNull(this.dataAssessmentReport.getDataAssmntCompHexData()) &&
        CommonUtils.isNotEmptyString(this.dataAssessmentReport.getDataAssmntCompHexData().getReferenceId())) {
      getLogger().debug("Fetching HEX file from compare HEX reference ID");
      String referenceId = this.dataAssessmentReport.getDataAssmntCompHexData().getReferenceId();
      hexFile = new File(CompHexWithCDFxProcess.COMP_HEX_WORK_DIR + CompHexWithCDFxProcess.FILE_DELIMITER +
          referenceId + CompHexWithCDFxProcess.FILE_DELIMITER + this.dataAssessmentReport.getHexFileName());
    }

    // Copy the hex file from source to data assessment temp directory
    File destDir = new File(filePath + File.separator + "A2L and Hex File");
    if ((hexFile != null) && hexFile.exists()) {
      try {
        getLogger().debug("Copying HEX file from path {} into baseline directory", hexFile.getAbsolutePath());
        FileUtils.copyFileToDirectory(hexFile, destDir);
      }
      catch (IOException e) {
        throw new IcdmException(e.getLocalizedMessage(), e);
      }
    }
    else {
      getLogger().info("HEX File cannot be downloaded as it doesnot exist");
    }
    getLogger().debug("Export of HEX File completed");

  }

  /**
   * Ecport all types of files that are related to the creation of review results
   *
   * @param filePath Folder path where the file needs to be exported
   * @param serviceData
   * @throws IcdmException
   * @throws IOException
   */
  private void exportReviewRelatedFiles(final String filePath, final ServiceData serviceData) throws IcdmException {
    getLogger().debug("Export of Review related Files started");
    RvwFileLoader rvwFileLoader = new RvwFileLoader(serviceData);
    CDRReviewResultLoader cdrResLoader = new CDRReviewResultLoader(serviceData);
    Set<Long> rvwResultsIdList = this.dataAssessmentReport.getDataAssmntCompHexData().getDaCompareHexParam().stream()
        .filter(params -> params.getCdrResultId() != null).map(DaCompareHexParam::getCdrResultId)
        .collect(Collectors.toSet());

    getLogger().debug("List of review result ids considered during hex compare: {}. Total number of Ids : {}",
        rvwResultsIdList, rvwResultsIdList.size());

    // Review And Questionnaire Root Folder
    getLogger().debug("Creating Reviews and Questionnaires directory");
    String rvwRootFolder = filePath + File.separator + "Reviews and Questionnaires";
    StringBuilder txtFileContent = new StringBuilder();

    for (Long rvwResId : rvwResultsIdList) {
      TRvwResult tRvwResult = cdrResLoader.getEntityObject(rvwResId);
      CDRReviewResult cdrReviewResult = cdrResLoader.createDataObject(tRvwResult);
      String reviewFilePath = createReviewFileDirectory(rvwRootFolder, cdrReviewResult, txtFileContent);
      exportRvwA2lFile(tRvwResult, reviewFilePath, serviceData);

      // Create Combined Report for Review Result and Qnaire
      try {
        new CombinedReportPdfExport(serviceData, reviewFilePath).createCombinedReport(rvwResId);
      }
      catch (IOException e) {
        throw new IcdmException("Exception while creating combined review report", e);
      }

      Map<Long, RvwFile> rvwFileMap = rvwFileLoader.getByResultId(tRvwResult);
      // Download Rvw file
      getLogger().debug("Exporting review files");
      if (CommonUtils.isNotEmpty(rvwFileMap)) {
        rvwFileMap.forEach(
            (rvwFileId, rvwFile) -> exportRvwFiles(rvwFileLoader, rvwRootFolder, reviewFilePath, rvwFileId, rvwFile));
      }
      getLogger().debug("Review Files getByResultId completed. Total records = {}", rvwFileMap.size());
    }

    if (CommonUtils.isNotEmptyString(txtFileContent.toString())) {
      createTextFile(rvwRootFolder, "Complete_folder_names", txtFileContent.toString());
    }

    getLogger().debug("Export of Review related files completed");
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
        getLogger().error("An error occured while fetching review files", ex);
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
   * @param tRvwResult
   * @param reviewFilePath
   * @param serviceData
   * @throws IcdmException
   */
  private void exportRvwA2lFile(final TRvwResult tRvwResult, final String reviewFilePath, final ServiceData serviceData)
      throws IcdmException {
    getLogger().debug("Export of Review related A2L File started");
    byte[] a2lFileData = new A2LFileInfoProvider(serviceData)
        .fetchA2LFileInfoSerialized(tRvwResult.getTPidcA2l().getMvTa2lFileinfo().getId());
    String filename = tRvwResult.getTPidcA2l().getMvTa2lFileinfo().getFilename();
    if (tRvwResult.getTPidcA2l().getVcdmA2lName() != null) {
      filename = tRvwResult.getTPidcA2l().getVcdmA2lName();
    }
    createFileFromBytes(reviewFilePath, a2lFileData, filename);
    getLogger().debug("Export of Review related A2L File completed");
  }


  private String createReviewFileDirectory(final String filePath, final CDRReviewResult cdrReviewResult,
      final StringBuilder txtFileContent) {
    getLogger().debug("Creating directory for storing review files");
    String reviewResultName =
        FileNameUtil.formatFileName(cdrReviewResult.getName(), ApicConstants.INVALID_CHAR_PTRN_EXCLUD_SPACE);

    StringBuilder fullRvwNameBuilder = new StringBuilder();
    fullRvwNameBuilder.append(cdrReviewResult.getId());
    fullRvwNameBuilder.append(" ");
    fullRvwNameBuilder.append(reviewResultName);
    String rvwResultName = fullRvwNameBuilder.toString();

    if (reviewResultName.length() > 10) {
      getLogger().debug("Trimming the review result folder name since it exceeds the maximum allowed length");
      txtFileContent.append(rvwResultName).append("\n");
      StringBuilder trimmedRvwName = new StringBuilder();
      trimmedRvwName.append(cdrReviewResult.getId());
      trimmedRvwName.append(" ");
      trimmedRvwName.append(DataAssessmentUtil.getTrimmedName(reviewResultName, 10));
      rvwResultName = trimmedRvwName.toString();
    }

    String rvwFilePath = filePath + File.separator + rvwResultName;
    File file = new File(rvwFilePath);
    DataAssessmentUtil.createDirectory(file);
    return file.getPath();
  }

  /**
   * Method to create a text file in the given path with the given name and content
   *
   * @param filePath Path where text file needs to be created
   * @param txtFileName Name of the text file
   * @param fileContent Content of the text file
   */
  public void createTextFile(final String filePath, final String txtFileName, final String fileContent) {
    getLogger().debug("Creating a text file containing the full folder names as content");
    try {
      File txtFile = new File(filePath + File.separator + txtFileName + ".txt");
      boolean fileCreated = txtFile.createNewFile();
      if (fileCreated) {
        Files.write(txtFile.toPath(), fileContent.getBytes(StandardCharsets.UTF_8));
        getLogger().debug("New text File created with the original folder names as content");
      }
    }
    catch (IOException e) {
      getLogger().error("An error occured while creating text file for full folder name", e);
    }
  }

  /**
   * Export Compare Hex Report
   *
   * @param filePath Folder path where the file needs to be exported
   * @param serviceData
   * @throws IcdmException
   * @throws IOException
   */
  private void exportCompareHexReport(final String filePath, final ServiceData serviceData) throws IcdmException {

    getLogger().info("Export of Hex compare report started");
    CompareHexPdfExport compHexPdf = new CompareHexPdfExport(serviceData, this.dataAssessmentReport, filePath);
    try {
      compHexPdf.setBoschLogo(this.boschLogo);
      compHexPdf.setUser(this.user);
      compHexPdf.createCompHexPdf();
    }
    catch (ClassNotFoundException | IOException ex) {
      throw new IcdmException("An error occured during the export of Compare HEX Report", ex);
    }
    getLogger().info("Export of Hex compare report completed");

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
    getLogger().info("Export of PIDC Attributes Information started");
    getLogger().info("Fetching AttributeExportModel in exportPidcAttributes started");
    AttributeLoader attrLdr = new AttributeLoader(serviceData);
    AttrExportModel attrExp = attrLdr.getAtrrExportModel();
    getLogger().debug("Fetching AttributeExportModel in exportPidcAttributes completed");

    // Fetch PIDC Version with details
    getLogger().info("Fetching PidcVersionWithDetails in exportPidcAttributes started");
    PidcVersionWithDetailsInput input = new PidcVersionWithDetailsInput();
    input.setPidcVersionId(this.dataAssessmentReport.getPidcVersId());
    PidcVersionWithDetails pidcVersionWithDetails =
        new PidcVersionLoader(serviceData).getPidcVersionWithDetails(input, "ALL", null);
    getLogger().info("Fetching PidcVersionWithDetails in exportPidcAttributes completed");

    // Exporting the fetched values into a PDF
    getLogger().info("Writing the PIDC Information in a PDF");
    PidcAttributesPdfExport pdf = new PidcAttributesPdfExport(serviceData, attrExp, pidcVersionWithDetails, filePath);
    pdf.setBoschLogo(this.boschLogo);
    pdf.setUser(this.user);
    try {
      pdf.createPidcAttrPdf(pidcVarId);
    }
    catch (IOException e) {
      throw new IcdmException("Exception while creating PIDC Attribues PDF", e);
    }
    getLogger().info("Export of PIDC Attributes Information completed");

  }

  private void exportCompliReviewAndCheckFiles(final String filePath, final ServiceData serviceData)
      throws IcdmException {
    getLogger().debug("Export of compliance review and compliance check report started");
    CompliReviewPdfExport compliReviewPdfExport =
        new CompliReviewPdfExport(serviceData, this.dataAssessmentReport, null, filePath);

    try {
      // Compliance Review Report
      compliReviewPdfExport.createCompliReviewPdfAReport();

      // Compliance Check Report
      compliReviewPdfExport.createCompliCheckPdfAReport();
    }
    catch (IOException e) {
      throw new IcdmException("Exception occurred during compliReview and compli check files creation", e);
    }

    getLogger().debug("Export of compliance review and compliance check report completed");
  }

  /**
   * @param filePath
   * @param serviceData
   * @throws IcdmException IcdmException
   * @throws IOException IO Exception
   */
  private void exportDataAssmntReport(final String filePath, final ServiceData serviceData) throws IcdmException {
    getLogger().info("Export of Data Assessment Report started");
    DataAssessmentPdfExport daPdfExport = new DataAssessmentPdfExport(serviceData, this.dataAssessmentReport, filePath);
    daPdfExport.setBoschLogo(this.boschLogo);
    daPdfExport.setUser(this.user);
    try {
      daPdfExport.createDataAssessmntReportPdf();
    }
    catch (IOException e) {
      throw new IcdmException("Exception occured during Data assessment Report creation", e);
    }
    getLogger().info("Export of Data Assessment Report completed");

  }

  /**
   * Method to write file data from byte[] to a physical file location
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
      getLogger().debug("Directory does not exist. Creating directory {}", dirName);
      file.mkdir();
      getLogger().debug("Directory created");
    }

    String inputDataPath = file.getAbsolutePath();
    try {
      // Writing Files to disk
      getLogger().debug("Writing files to disk ");
      String outputFilePath = inputDataPath + File.separator + fileName;
      CommonUtils.createFile(outputFilePath, fileData);
      getLogger().debug("File created in path: {}", outputFilePath);
    }
    catch (IOException exp) {
      throw new IcdmException("Creation of files failed", exp);
    }

  }

  private void createFileFromBytes(final String filePath, final byte[] fileData, final String fileName)
      throws IcdmException {
    try {
      // Writing Files to disk
      getLogger().debug("Writing files to disk ... ");
      String outputFilePath = filePath + File.separator + fileName;
      CommonUtils.createFile(outputFilePath, fileData);
      getLogger().debug("File created in path: {}", outputFilePath);
    }
    catch (IOException exp) {
      throw new IcdmException("Creation of files failed", exp);
    }

  }

  private byte[] zipBaselineFiles(final A2LFile a2lFile) throws IcdmException, IOException {
    getLogger().debug("Creating a zipped file containing all baseline files");
    String baseName = FilenameUtils.getBaseName(this.baselineDirPath);
    String trimmedzipName =
        DataAssessmentUtil.getTrimmedName(baseName, DataAssessmentUtil.MAX_ALLOWED_CHARS_ZIPNAME_ARCHIVAL);
    String zipfileName = trimmedzipName + ".zip";
    getLogger().debug("ZipfileName : " + zipfileName);

    // Create a zipped file with all baseline files
    String outputZipFilePath = createZippedBaselineFiles(this.baselineDirPath, zipfileName, trimmedzipName);

    getLogger().debug("Getting the zipped file in byte array");
    byte[] fileAsBytes = null;
    try {
      fileAsBytes = CommonUtils.getFileAsByteArray(outputZipFilePath);
      getLogger().debug("Zipped file converted into byte array");
      // to copy the zip file to archival folder incase of series release
      dataAssessmentArchivalProcess(a2lFile, zipfileName, outputZipFilePath);
    }
    catch (IOException e) {
      getLogger().error("An error occured during the creation of byte array from file", e);
    }

    getLogger().debug("Deleting the baseline zip file from temp server path {}", outputZipFilePath);
    deleteDirectory(new File(outputZipFilePath));
    getLogger().debug("Baseline zip file deletion completed");

    return fileAsBytes;
  }


  /**
   * @param createBaseFoldStruForArch
   * @param isSeriesRelease
   * @param zipfileName
   * @param outputZipFilePath
   * @throws IOException
   * @throws IcdmException
   */
  private void dataAssessmentArchivalProcess(final A2LFile a2lFile, final String zipfileName,
      final String outputZipFilePath)
      throws IcdmException {
    if (this.dataAssessmentReport.isReadyForSeries()) {
      // Fetching Data Assessment Archival Folder Path if the Data Assessment Type is Ready for Series
      String createBaseFolderStructureForArchival =
          DataAssessmentUtil.createBaseFolderStructureForArchival(this.dataAssessmentReport,
              new File(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.DATAASSESSMENT_ARCHIVAL_FOLDER)),
              this.dataAssmntId, a2lFile.getSdomPverName(), a2lFile.getSdomPverVariant());
      getLogger().debug("Created Data Assessment Archival Folder");
      getLogger().debug("Started moving of Zipped file to Archival Folder " + createBaseFolderStructureForArchival);
      StringBuilder arcZipPath = new StringBuilder();
      arcZipPath.append(createBaseFolderStructureForArchival);
      arcZipPath.append("\\");
      arcZipPath.append(zipfileName);
      // to update the last access date of the zip file to 35+years
      DataAssessmentUtil.updateLastAccessDateWhileArchiving(Paths.get(outputZipFilePath));
      // Logic to move DataAssessment Generated Files to Archival Schema
      DataAssessmentUtil.roboCopyDataAssessmentFileToArchival(outputZipFilePath, arcZipPath.toString());
    }
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

    getLogger().debug("Compressing all the baseline files into a single zipped file");

    String zipFileDir = DataAssessmentUtil.DATA_ASSESSMENT_BASE_PATH + File.separator + trimmedzipName + "_DIR";
    String zipFilePath = zipFileDir + File.separator + zipfileName;
    // Create the Directory
    Files.createDirectory(Paths.get(zipFileDir));

    getLogger().debug("zip file path : {}", zipFilePath);

    // Compress output files to a single zip file
    try {
      ZipUtils.zip(Paths.get(baselinePath), Paths.get(zipFilePath));
    }
    catch (IOException e) {
      getLogger().error("An exception occured during the creation of zipped baseline files", e);
    }

    getLogger().debug("Baseline files compressed into a Zip file");

    // Delete zip file's original folder
    getLogger().debug("Delete zip file's original folder {}", baselinePath);
    deleteDirectory(new File(baselinePath));
    getLogger().debug("Folder deletion completed");

    getLogger().info("Baseline zip file creation completed. File : {} ; Path : {}", zipfileName, zipFilePath);
    return zipFilePath;
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
        getLogger().debug(" Deleting children of Dir - {}", file.getName());
        Arrays.stream(file.list()).map(c -> new File(file, c)).forEach(this::deleteDirectory);
      }

      // check the directory again, if empty then delete it
      if (file.list().length == 0) {
        delete(file.toPath());
        getLogger().debug("  Deleted Dir - {}", file.getName());
      }
    }
    else {
      // if file, then delete it
      delete(file.toPath());
      getLogger().debug("  Deleted File - {}", file.getName());
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
      getLogger().info(e.getMessage(), e);
    }
  }


}
