/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.compli;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2lWpResponsibilityLoader;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.bo.cdr.CheckSSDOutputData;
import com.bosch.caltool.icdm.bo.cdr.CheckSSDResultParam;
import com.bosch.caltool.icdm.bo.checkssd.RuleDescriptionLoader;
import com.bosch.caltool.icdm.bo.general.IcdmFilesLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.logger.CheckSSDLogger;
import com.bosch.caltool.icdm.model.a2l.WpRespLabelResponse;
import com.bosch.caltool.icdm.model.a2l.WpRespModel;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.ExcelReportTypeEnum;
import com.bosch.checkssd.CheckSSDCoreInstances;
import com.bosch.checkssd.CheckSSDInfo;
import com.bosch.checkssd.exception.CheckSSDException;
import com.bosch.checkssd.reports.CheckSSDReport;
import com.bosch.checkssd.reports.errorrules.ErrorRuleModel;
import com.bosch.checkssd.reports.reportMessage.ReportMessages;
import com.bosch.checkssd.reports.reportmodel.ReportModel;
import com.bosch.checkssd.reports.summaryReport.CheckSSDBatchGroup;
import com.bosch.checkssd.reports.summaryReport.CheckSSDBatchRun;

/**
 * @author rgo7cob
 */
public enum CompliParamCheckSSDInvoker {

                                        /**
                                         * Single instance
                                         */
                                        INSTANCE;

  /**
                                         *
                                         */
  private static final String MSG_FOR_SINGLE_FILE_WITH_REDUCTION_SUMMARY = "N/A for Single File with Reduction Summary";

  private static final String LOG_SHEET_NAME = "Log-Message";

  private static final String LOGSHEETPREFIX = "Log-";

  /**
   * Report type Group Switch - ONEFILECHECK
   */
  public static final int CHKSSD_INFO_GRP_SWITCH_ONEFILECHECK = 0;

  /**
   * External report name part
   */
  private static final String EXTERNAL = "External";
  /**
   * External report extn
   */
  private static final String EXCEL_EXTENSION = ".xlsx";

  private final List<String> dataSetList = new ArrayList<>();

  Map<Long, Map<Long, WpRespModel>> paramWpRespMapForVariant = new HashMap<>();

  Map<Long, Map<Long, String>> paramMap = new HashMap<>();


  /**
   * GetInstance()
   *
   * @return thte instance of CDMDataProvider class.
   */
  public static CompliParamCheckSSDInvoker getInstance() {
    return INSTANCE;
  }


  /**
   * Generate all type of CheckSSD reports CHKSSD_GENERATE_ALL_TYPES.
   */
  private static final int CHKSSD_GENERATE_ALL_TYPES = 2;


  /**
   * @param filePath filePath of the SSD file. serverOrTempPath server path or temp path
   * @throws IcdmException
   * @throws IOException path in the server or temp path
   */
  private void removeVectorChannelFromSSDFile(final String filePath, final String serverOrTempPath)
      throws IcdmException {

    String modifiedFileName = "compliSSD_modified";


    String ssdFilePath = serverOrTempPath + File.separator;
    File modifiedFile = new File(ssdFilePath + modifiedFileName);

    try {
      boolean created = modifiedFile.createNewFile();
      if (!created) {
        getLogger().warn("file creation returned FALSE. File : " + modifiedFile);
      }
    }
    catch (IOException e) {
      throw new IcdmException("Failed to create temporary file to remove vector and channel info. " + e.getMessage(),
          e);
    }
    File originalFile = new File(filePath);
    try (FileReader in = new FileReader(originalFile);
        BufferedReader reader = new BufferedReader(in);
        FileWriter out = new FileWriter(modifiedFile);
        BufferedWriter writer = new BufferedWriter(out);) {

      String vector = "Vector";
      String channel = "Channel";
      String currentLine;
      while ((currentLine = reader.readLine()) != null) {
        // trim newline when comparing with lineToRemove
        String trimmedLine = currentLine.trim();
        if (trimmedLine.contains(vector) || trimmedLine.contains(channel)) {
          continue;
        }
        writer.write(currentLine + System.getProperty("line.separator"));
      }
    }
    catch (IOException e) {
      throw new IcdmException("Failed to create temporary file to remove vector and channel info. " + e.getMessage(),
          e);
    }

    try {
      Files.delete(originalFile.toPath());
    }
    catch (IOException e) {
      getLogger().warn("Failed to delete original file " + originalFile + ". " + e.getMessage(), e);
    }


    File newFiletobeCreated = new File(filePath);
    boolean renamed = modifiedFile.renameTo(newFiletobeCreated);
    if (!renamed) {
      getLogger().warn("Rename to new file returned FALSE. File:" + filePath);
    }
  }


  /**
   * @param input input
   * @return CheckSSDOutputData
   * @throws IcdmException error while running CheckSSD
   */
  public synchronized CheckSSDOutputData runCheckSSD(final CompliCheckSSDInput input) throws IcdmException {

    removeVectorChannelFromSSDFile(input.getSsdFilePath(), input.getServerOrTempPath());

    CheckSSDOutputData checkssdOutputData = new CheckSSDOutputData();

    try (ServiceData serviceData = new ServiceData()) {
      // Write the Compli Labels to SSD file.
      ParameterLoader paramLoader = new ParameterLoader(serviceData);
      A2lWpResponsibilityLoader a2lWpRespLoader = new A2lWpResponsibilityLoader(serviceData);
      Map<Long, WpRespModel> paramWpRespMap = new HashMap<>();
      Map<Long, String> paramNameMap = new HashMap<>();
      if (input.getPidcA2l() != null) {
        for (WpRespLabelResponse wpResp : a2lWpRespLoader.getWpResp(input.getPidcA2l(), input.getVariantId())) {
          paramWpRespMap.put(wpResp.getParamId(), wpResp.getWpRespModel());
          paramNameMap.put(wpResp.getParamId(), wpResp.getParamName());
        }
      }

      Map<String, CheckSSDResultParam> checkSSDResultParamMap = new HashMap<>();
      File ssdFile = new File(input.getSsdFilePath());
      if (ssdFile.exists() && !CommonUtils.isNullOrEmpty(input.getCalDataMap()) &&
          (input.getA2lFileContents() != null)) {
        CheckSSDInfo checkSSDInfo = new CheckSSDInfo(input.getSsdFilePath());
        checkSSDInfo.setLogger(CheckSSDLogger.getInstance());
        File dir = createOutputDir(ssdFile);

        Map<String, CalData> clonedCalDataMap = CommonUtils.cloneCalData(input.getCalDataMap());
        checkSSDInfo.setExcelReportName(ReportMessages.EXCEL_REPORT_HEX_NAME_DUMMY);
        checkSSDInfo.setCompliMapFromIcdm(paramLoader.getCompliParamWithType());
        checkSSDInfo.setQualityLblMapFromIcdm(paramLoader.getQssdParams());
        checkSSDInfo.setOptFileLocForRpts(dir.getPath());
        checkSSDInfo.setCalDataMap(clonedCalDataMap);
        checkSSDInfo.setA2lFileInfo(input.getA2lFileContents());
        checkSSDInfo.setRptTypeFlag(CHKSSD_GENERATE_ALL_TYPES);
        checkSSDInfo.setTkOvrReport(input.isPredecessorCheck());
        checkSSDInfo.setGrpSwitch(input.getDataFileOption().getValue());
        checkDataFileOption(input, checkSSDInfo);
        checkSSDInfo.setA2lFilePath(input.getA2lFileContents().getFileName());
        checkSSDInfo.setHexFilePath(CommonUtils.isNotEmptyString(input.getHexFileName()) ? input.getHexFileName()
            : dir.getName() + CommonUtilConstants.HEX_EXTN);

        // to set ruleDescProvider - to invoke service directly
        checkSSDInfo.setRuleDescProvider(new RuleDescriptionLoader(serviceData)::getOEMParamForCheckssd);

        CheckSSDCoreInstances cssdCoreInstances = new CheckSSDCoreInstances();

        // set warnings map for non-monotonous and other labels
        cssdCoreInstances.getSsdModelUtilsInstance().setWarningFileMap(input.getParserWarningsMap());

        checkSSDInfo.setCssdCoreInstances(cssdCoreInstances);
        checkSSDInfo.setExternalExlReport(true);

        String filePath = Messages.getString("SERVICE_WORK_DIR") + File.separator + "disclosure.jpg";
        downloadLogoIfRequired(serviceData, filePath);

        checkSSDInfo.setExtlRptImgFilePath(filePath);

        // Execute CheckSSD
        getLogger().info("CheckSSD call started");
        checkSSDInfo.runCheckSSD();
        getLogger().info("CheckSSD call completed");

        Set<String> outputFiles =
            changeCheckSSDFileNames(checkSSDInfo, checkSSDInfo.getOutPutFilesPath(), checkssdOutputData, input);

        if (input.getPidcA2l() != null) {
          if (CommonUtils.isEqual(input.getDataFileOption(), ExcelReportTypeEnum.SINGLEFILEWITHSUMMARY) ||
              CommonUtils.isEqual(input.getDataFileOption(), ExcelReportTypeEnum.SINGLEFILEWITHREDUCTIONSUMMARY)) {
            this.paramWpRespMapForVariant.put(input.getVariantId(), paramWpRespMap);
            this.paramMap.put(input.getVariantId(), paramNameMap);
          }

          writeWpResp(input, outputFiles, paramWpRespMap, paramNameMap);
        }

        // Report
        CheckSSDReport checkSSDReport = checkSSDInfo.getRptInstance();

        checkssdOutputData.setErrorInSSD(checkSSDReport.isSyntaxErrFound());

        // add all the labels in ssd file
        List<String> lablesInSsd = checkSSDReport.getLabInSSDFileLst();

        // map which contains labels whose rule is skipped
        Map<String, String> labelMap = new HashMap<>();

        for (String labelName : lablesInSsd) {
          if (input.getCalDataMap().containsKey(labelName)) {
            labelMap.put(labelName, CDRConstants.COMPLI_RESULT_FLAG.OK.getUiType());
          }
        }

        Map<String, ReportModel> compliReportModelMap = new HashMap<>();
        Map<String, ReportModel> qssdReportModelMap = new HashMap<>();
        List<ReportModel> allModels = new ArrayList<>();
        // Value Type
        allModels.addAll(checkSSDReport.getValueModelList());
        allModels.addAll(checkSSDReport.getWildCardValLblList());
        // MapCurve Type
        allModels.addAll(checkSSDReport.getMcvModelList());
        allModels.addAll(checkSSDReport.getMcVblkModelList());
        // ValueBlock Type
        allModels.addAll(checkSSDReport.getVblkModelList());
        allModels.addAll(checkSSDReport.getWildCardVblkLblList());

        fillCompliQssdReportModelMap(labelMap, compliReportModelMap, qssdReportModelMap, allModels);

        // for those parameters that have failed due to invalid rules, details from error rule model in checkSSDReport
        // is read and
        // put into compli and qssd report model maps
        fillCompliQssdReportModelForInvalidRules(labelMap, checkSSDReport, compliReportModelMap, qssdReportModelMap);

        Map<String, CheckSSDResultParam> checkSSDMap =
            setCheckSSDMap(clonedCalDataMap, checkSSDResultParamMap, compliReportModelMap, qssdReportModelMap);

        setCheckSSDOutput(checkSSDMap, checkssdOutputData, outputFiles);

        setCompliLabelsWithoutRules(input, checkssdOutputData, paramLoader, labelMap, compliReportModelMap);

        setQssdLabelsWithoutRules(input, checkssdOutputData, paramLoader, qssdReportModelMap);


        // set the labels which has been skipped during review
        checkssdOutputData.setLabelsInSsdMap(labelMap);
        handleErrorInSSDFile(input.getSsdFilePath(), checkssdOutputData.getErrorinSSDFile(), outputFiles);

        getLogger().info("Check SSD output set to datamodel");
      }
    }
    catch (CheckSSDException exception) {
      setErrorInSummary(exception, checkssdOutputData);
    }
    return checkssdOutputData;
  }


  /**
   * @param input
   * @param checkSSDInfo
   */
  private void checkDataFileOption(final CompliCheckSSDInput input, final CheckSSDInfo checkSSDInfo) {
    if (CommonUtils.isNotEqual(input.getDataFileOption(), ExcelReportTypeEnum.ONEFILECHECK)) {
      checkSSDInfo.setBatchRun(true);
      addInstanceToBatchRun(input.getHexFileName(), input.getA2lFileContents().getFileName(), input.getSsdFilePath());
    }
  }


  /**
   * @param input
   * @param paramWPRespMap
   * @param listOfParam
   * @param outputFiles
   * @param paramMap
   * @param paramWpRespMap
   * @param paramRespMap
   */
  private void writeWpResp(final CompliCheckSSDInput input, final Set<String> outputFiles,
      final Map<Long, WpRespModel> paramWpRespMap, final Map<Long, String> paramNameMap) {
    for (String outputFile : outputFiles) {
      if ((outputFile.endsWith(EXCEL_EXTENSION))) {
        if (CommonUtils.isEqual(input.getDataFileOption(), ExcelReportTypeEnum.SINGLEFILEWITHSUMMARY) &&
            CheckSSDBatchRun.getInstance().isLastDataset()) {
          fillSingleFileWithSummary(outputFile, input);
        }
        else if (CommonUtils.isEqual(input.getDataFileOption(), ExcelReportTypeEnum.ONEFILECHECK)) {
          fillOneFilePerCheck(paramWpRespMap, paramNameMap, outputFile);
        }
        else if (CommonUtils.isEqual(input.getDataFileOption(), ExcelReportTypeEnum.SINGLEFILEWITHREDUCTIONSUMMARY) &&
            CheckSSDBatchRun.getInstance().isLastDataset()) {
          fillSingleFileWithRedSum(outputFile, input);
        }
      }
    }
  }


  /**
   * @param listOfParam
   * @param outFile
   */
  private void fillSingleFileWithRedSum(final String outputFile, final CompliCheckSSDInput input) {

    try (FileInputStream fis = new FileInputStream(outputFile); Workbook workbook = new XSSFWorkbook(fis)) {

      CellStyle wrapStyle = workbook.createCellStyle();
      wrapStyle.setWrapText(true);

      int logSheetCount = 1;
      for (Entry<Long, Long> Entry : input.getHexFilePidcElement().entrySet()) {
        String logSheetName = LOGSHEETPREFIX + logSheetCount++;
        Sheet logSheet = workbook.getSheet(logSheetName);

        if (CommonUtils.isNotNull(logSheet)) {
          int columnIndex = 0;
          int destinationCellIndex = 3;

          for (Row row : logSheet) {
            Cell cell = row.getCell(columnIndex);
            if ((cell != null) && this.paramMap.get(Entry.getValue()).values().contains(cell.getStringCellValue())) {
              row.createCell(destinationCellIndex).setCellValue(MSG_FOR_SINGLE_FILE_WITH_REDUCTION_SUMMARY);
              row.getCell(destinationCellIndex).setCellStyle(wrapStyle);

            }
          }
        }
      }

      try (FileOutputStream fileOut = new FileOutputStream(outputFile)) {
        workbook.write(fileOut);
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }

  }


  /**
   * @param listOfParamMapping
   * @param outputFile
   * @param input
   */
  private void fillSingleFileWithSummary(final String outputFile, final CompliCheckSSDInput input) {
    try (FileInputStream fis = new FileInputStream(outputFile); Workbook workbook = new XSSFWorkbook(fis)) {

      CellStyle wrapStyle = workbook.createCellStyle();
      wrapStyle.setWrapText(true);

      int logSheetCount = 1;
      for (Entry<Long, Long> Entry : input.getHexFilePidcElement().entrySet()) {
        String logSheetName = LOGSHEETPREFIX + logSheetCount++;
        Sheet logSheet = workbook.getSheet(logSheetName);
        if (CommonUtils.isNotNull(logSheet)) {
          int columnIndex = 0;
          int destinationCellIndex = 3;

          for (Row row : logSheet) {
            Cell cell = row.getCell(columnIndex);
            if ((CommonUtils.isNotNull(cell)) &&
                this.paramMap.get(Entry.getValue()).values().contains(cell.getStringCellValue())) {
              iterateExcelFile(wrapStyle, Entry, destinationCellIndex, row, cell);
            }
          }
        }
      }

      try (FileOutputStream fileOut = new FileOutputStream(outputFile)) {
        workbook.write(fileOut);
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }


  /**
   * @param wrapStyle
   * @param entry
   * @param destinationCellIndex
   * @param row
   * @param cell
   */
  private void iterateExcelFile(final CellStyle wrapStyle, final Entry<Long, Long> entry,
      final int destinationCellIndex, final Row row, final Cell cell) {
    Long paramId = getKeyByValue(this.paramMap.get(entry.getValue()), cell.getStringCellValue());
    if (CommonUtils.isNotNull(paramId)) {
      row.createCell(destinationCellIndex)
          .setCellValue(this.paramWpRespMapForVariant.get(entry.getValue()).get(paramId).getWpName() + " ; " +
              getTypeName(this.paramWpRespMapForVariant.get(entry.getValue()).get(paramId).getA2lResponsibility()
                  .getRespType()) +
              " ; " + this.paramWpRespMapForVariant.get(entry.getValue()).get(paramId).getWpRespName());
      row.getCell(destinationCellIndex).setCellStyle(wrapStyle);
    }
  }


  /**
   * @param paramNameMap
   * @param paramWpResp
   * @param outFile
   */
  private void fillOneFilePerCheck(final Map<Long, WpRespModel> paramWpResp, final Map<Long, String> paramNameMap,
      final String outFile) {
    try (FileInputStream fis = new FileInputStream(outFile); Workbook workbook = new XSSFWorkbook(fis)) {
      CellStyle wrapStyle = workbook.createCellStyle();
      wrapStyle.setWrapText(true);
      Sheet logSheet = workbook.getSheet(LOG_SHEET_NAME);
      int columnIndex = 0;
      int destinationCellIndex = 3;


      for (Row row : logSheet) {
        Cell cell = row.getCell(columnIndex);
        if (CommonUtils.isNotNull(cell) && paramNameMap.values().contains(cell.getStringCellValue())) {
          Long paramId = getKeyByValue(paramNameMap, cell.getStringCellValue());
          if (CommonUtils.isNotNull(paramId)) {
            row.createCell(destinationCellIndex)
                .setCellValue(paramWpResp.get(paramId).getWpName() + " ; " +
                    getTypeName(paramWpResp.get(paramId).getA2lResponsibility().getRespType()) + " ; " +
                    paramWpResp.get(paramId).getWpRespName());
            row.getCell(destinationCellIndex).setCellStyle(wrapStyle);
          }
        }
      }

      try (FileOutputStream fileOut = new FileOutputStream(outFile)) {
        workbook.write(fileOut);
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }

  }

  public static <K, V> K getKeyByValue(final Map<K, V> map, final V value) {
    for (Map.Entry<K, V> entry : map.entrySet()) {
      if (value.equals(entry.getValue())) {
        return entry.getKey();
      }
    }
    return null; // Value not found
  }

  public static String getTypeName(final String paramType) {

    Map<String, String> typeMap = new HashMap<>();
    typeMap.put("R", "Robert Bosch");
    typeMap.put("C", "Customer");
    typeMap.put("O", "Others");

    String typeName = typeMap.get(paramType);

    return typeName != null ? typeName : paramType;
  }

  private static Set<String> filterStrings(final Set<String> inputSet, final String suffix) {
    Set<String> result = new HashSet<>();
    for (String str : inputSet) {
      if (str.endsWith(suffix)) {
        result.add(str);
      }
    }
    return result;
  }

  /**
   * Creates the check ssd summary report.
   *
   * @param datafilepath the datafilepath
   * @param a2lfilepath the a2lfilepath
   * @param ssdfilepath the ssdfilepath
   * @return the check ssd batch group
   */
  public boolean addInstanceToBatchRun(final String datafilepath, final String a2lfilepath, final String ssdfilepath) {
    boolean isExists = false;

    CheckSSDBatchRun runBatch = CheckSSDBatchRun.getInstance();
    Map<String, CheckSSDBatchGroup> parentGroup = runBatch.getBatchGroups();

    String dataSet = a2lfilepath + ssdfilepath + datafilepath;

    CheckSSDBatchGroup group = new CheckSSDBatchGroup();
    group.setA2lfilepath(a2lfilepath);

    if (CommonUtils.isNotNull(parentGroup.get(a2lfilepath))) {
      group = parentGroup.get(a2lfilepath);
      if (!this.dataSetList.contains(dataSet)) {
        group.getDatafilepath().add(datafilepath);
        group.getSsdfilepath().add(ssdfilepath);
        this.dataSetList.add(dataSet);
      }
      else {
        isExists = true;
      }
    }
    else {
      group.getSsdfilepath().add(ssdfilepath);
      group.getDatafilepath().add(datafilepath);
      this.dataSetList.add(dataSet);
      parentGroup.put(a2lfilepath, group);
    }
    return isExists;
  }

  private CDMLogger getLogger() {
    return CDMLogger.getInstance();
  }


  private void fillCompliQssdReportModelForInvalidRules(final Map<String, String> labelMap,
      final CheckSSDReport checkSSDReport, final Map<String, ReportModel> compliReportModelMap,
      final Map<String, ReportModel> qssdReportModelMap) {
    for (Entry<String, Map<String, ErrorRuleModel>> paramRuleModelEntry : checkSSDReport.getErrorRuleModel()
        .entrySet()) {
      labelMap.remove(paramRuleModelEntry.getKey());

      for (Entry<String, ErrorRuleModel> ruleModelEntry : paramRuleModelEntry.getValue().entrySet()) {

        ReportModel reportModel = new ReportModel();
        reportModel.setLabelName(paramRuleModelEntry.getKey());
        reportModel.setUseCaseInfo(ruleModelEntry.getValue().getUseCase());
        reportModel.setStatus(ruleModelEntry.getValue().getUseCase());
        reportModel.setRuleOk(false);
        if (CDRConstants.COMPLI_RESULT_FLAG.CSSD.getUiType().equals(ruleModelEntry.getValue().getUseCase()) ||
            CDRConstants.COMPLI_RESULT_FLAG.SSD2RV.getUiType().equals(ruleModelEntry.getValue().getUseCase())) {
          compliReportModelMap.put(paramRuleModelEntry.getKey(), reportModel);
        }

        else if (CDRConstants.QSSD_RESULT_FLAG.QSSD.getUiType().equals(ruleModelEntry.getKey())) {
          qssdReportModelMap.put(paramRuleModelEntry.getKey(), reportModel);
        }

      }

    }
  }


  /**
   * @param labelMap
   * @param compliReportModelMap
   * @param qssdReportModelMap
   * @param allModels
   */
  private void fillCompliQssdReportModelMap(final Map<String, String> labelMap,
      final Map<String, ReportModel> compliReportModelMap, final Map<String, ReportModel> qssdReportModelMap,
      final List<ReportModel> allModels) {
    for (ReportModel reportModel : allModels) {
      String labelName = reportModel.getLabelName();
      // remove the labels which has been reviewed
      labelMap.remove(labelName);
      /*
       * ICDM-1460 - If any of the result is not ok , then add it to map , otherwise do not add [eg. AXIS_PTS[4] will
       * have one reportmodel for each of the 4 values.If any of these values are NOT OK then result should be
       * summarised as NOT OK
       */
      if (reportModel.getUseCaseInfo().equals(CDRConstants.COMPLI_RESULT_FLAG.CSSD.getUiType()) ||
          reportModel.getUseCaseInfo().equals(CDRConstants.COMPLI_RESULT_FLAG.SSD2RV.getUiType())) {
        ReportModel existingRepModel = compliReportModelMap.get(labelName);
        if ((existingRepModel == null) || !CompliResultUtil.isResultOk(reportModel)) {
          compliReportModelMap.put(labelName, reportModel);
        }
      }
      else if (reportModel.getUseCaseInfo().equals(CDRConstants.QSSD_RESULT_FLAG.QSSD.getUiType())) {
        ReportModel existingRepModel = qssdReportModelMap.get(labelName);
        if ((existingRepModel == null) || !CompliResultUtil.isResultOk(reportModel)) {
          qssdReportModelMap.put(labelName, reportModel);
        }
      }
    }
  }


  /**
   * @param input
   * @param data
   * @param outputFiles
   * @throws IcdmException
   */
  private void handleErrorInSSDFile(final String ssdFilePath, final boolean errorInSSDFile,
      final Set<String> outputFiles)
      throws IcdmException {
    StringBuilder filePaths = new StringBuilder();
    if (errorInSSDFile) {
      filePaths.append(ssdFilePath).append("|");
      for (String path : outputFiles) {
        if (!path.contains(EXTERNAL)) {
          filePaths.append(path);
        }
      }
      throw new IcdmException("Error in ssd file. The file paths in server side are | " + filePaths.toString(), 100);
    }
  }


  /**
   * In the following scenarios, compli labels are set with NO-RULE status => 1. Compliance class labels which has no
   * valid C-SSD/SSD2Rv usecase rule 2. If a Compliance label is available in C-SSD/SSD2Rv usecase rule but is not
   * evaluated due to any condition
   *
   * @param input
   * @param checkSSDOutputData
   * @param paramLoader
   * @param labelMap
   * @param compliReportModelMap
   */
  private void setCompliLabelsWithoutRules(final CompliCheckSSDInput input, final CheckSSDOutputData checkSSDOutputData,
      final ParameterLoader paramLoader, final Map<String, String> labelMap,
      final Map<String, ReportModel> compliReportModelMap) {

    // get complilabels
    List<String> allCompliParamsList =
        input.getParamSet().stream().filter(paramLoader::isCompliParameter).collect(Collectors.toList());
    // remove the labels which has rules
    allCompliParamsList.removeAll(compliReportModelMap.keySet());
    // contains compli labels which are not evaluated are set as OK. These cannot be set as NO RULE
    allCompliParamsList.removeAll(labelMap.keySet());
    // add to no rule map
    allCompliParamsList.stream().forEach(compliLabel -> checkSSDOutputData.getLabelsWithoutRulesMap().put(compliLabel,
        CDRConstants.COMPLI_RESULT_FLAG.NO_RULE.getUiType()));

  }

  /**
   * In the following scenarios, QSSD labels are set with NO-RULE status => 1. Quality labels which has no valid Q-SSD
   * usecase rule. 2. Quality labels that are available in Q-SSD usecase rule but is not evaluated due to any condition
   *
   * @param input
   * @param checkSSDOutputData
   * @param paramLoader
   * @param qssdReportModelMap
   */
  private void setQssdLabelsWithoutRules(final CompliCheckSSDInput input, final CheckSSDOutputData checkSSDOutputData,
      final ParameterLoader paramLoader, final Map<String, ReportModel> qssdReportModelMap) {
    // get qssdLabels
    List<String> allQssdParamsList =
        input.getParamSet().stream().filter(paramLoader::isQssdParameter).collect(Collectors.toList());
    // remove the labels which has rules
    allQssdParamsList.removeAll(qssdReportModelMap.keySet());
    // add to no rule map
    allQssdParamsList.stream().forEach(qssdLabel -> checkSSDOutputData.getQssdLabelsWithoutRulesMap().put(qssdLabel,
        CDRConstants.QSSD_RESULT_FLAG.NO_RULE.getUiType()));
  }


  /**
   * @param ssdFile
   * @return
   */
  private File createOutputDir(final File ssdFile) {
    String currentDate = ApicUtil.getCurrentTime(DateFormat.DATE_FORMAT_20);
    File dir = new File(ssdFile.getParent() + File.separator + CDRConstants.COMPLI_REVIEW +
        CDRConstants.SSD_FILE_PATH_SEPARATOR + currentDate);
    if (!dir.exists()) {
      dir.mkdir();
    }
    return dir;
  }

  private void downloadLogoIfRequired(final ServiceData serviceData, final String logoPath) throws IcdmException {
    final File tempFile = new File(logoPath);

    if (!tempFile.exists()) {
      // Get logo from db
      IcdmFilesLoader fileLoader = new IcdmFilesLoader(serviceData);
      Map<String, byte[]> fileMap = fileLoader.getFiles(-7L, CDRConstants.CHECKSSD_LOGO_NODE_TYPE);
      byte[] reportLogo;
      if ((fileMap != null) && (fileMap.size() > 0)) {
        reportLogo = fileMap.get(CDRConstants.CHECKSSD_LOGO_IMAGE);
        try {
          FileUtils.writeByteArrayToFile(tempFile, reportLogo);
        }
        catch (IOException e) {
          throw new IcdmException("Error while creating LOGO file. " + e.getMessage(), e);
        }
      }

    }
  }

  /**
   * @param isPrimaryRule
   * @param checkSSDInfo
   * @param checkSSDMap
   * @param data
   * @param outputFiles
   * @param input
   */
  private void setCheckSSDOutput(final Map<String, CheckSSDResultParam> checkSSDMap, final CheckSSDOutputData data,
      final Set<String> outputFiles) {

    Set<String> generatedCheckSSDFiles = data.getGeneratedCheckSSDFiles();
    if (generatedCheckSSDFiles == null) {
      data.setGeneratedCheckSSDFiles(outputFiles);
    }
    else {
      generatedCheckSSDFiles.addAll(outputFiles);
      data.setGeneratedCheckSSDFiles(generatedCheckSSDFiles);
    }
    data.setCheckSSDCompliParamMap(checkSSDMap);
  }

  /**
   * @param exception
   * @param data
   */
  private void setErrorInSummary(final Exception exception, final CheckSSDOutputData data) {
    data.setCdrException(CDRConstants.CDR_EXCEPTION_TYPE.CHECK_SSD_EXCEPTION);
    data.setExceptioninReview(true);
    data.setReviewExceptionObj(exception);
  }

  /**
   * Sets the check SSD map.
   *
   * @param calDataMap the cal data map
   * @param checkSSDResultParamMap the check SSD result param map
   * @param compliReportModelMap the report model map
   * @param qssdReportModelMap
   * @param set
   * @return the map
   */
  private Map<String, CheckSSDResultParam> setCheckSSDMap(final Map<String, CalData> calDataMap,
      final Map<String, CheckSSDResultParam> checkSSDResultParamMap,
      final Map<String, ReportModel> compliReportModelMap, final Map<String, ReportModel> qssdReportModelMap) {

    Set<String> allParamSet = new HashSet<>();
    allParamSet.addAll(compliReportModelMap.keySet());
    allParamSet.addAll(qssdReportModelMap.keySet());

    for (String labelName : allParamSet) {
      CalData calData = calDataMap.get(labelName);
      CheckSSDResultParam checkSSDResultParam = new CheckSSDResultParam(labelName, calData, "",
          compliReportModelMap.get(labelName), qssdReportModelMap.get(labelName));
      checkSSDResultParamMap.put(labelName, checkSSDResultParam);
    }
    return checkSSDResultParamMap;
  }


  /**
   * @param checkSSDInfo
   * @param outputStrSet
   * @param data
   * @param input
   * @return
   */
  private Set<String> changeCheckSSDFileNames(final CheckSSDInfo checkSSDInfo, final Set<String> outputStrSet,
      final CheckSSDOutputData data, final CompliCheckSSDInput input) {

    Set<String> newFileNames = new HashSet<>();
    Set<String> fileNamesToReplace = new HashSet<>();
    String predecessorCheck = checkSSDInfo.isTkOvrReport() ? "VAL_" : "";

    for (String filePath : checkSSDInfo.getOutPutFilesPath()) {
      if ((filePath.endsWith(EXCEL_EXTENSION)) &&
          (CommonUtils.isEqual(input.getDataFileOption(), ExcelReportTypeEnum.ONEFILECHECK) ||
              CheckSSDBatchRun.getInstance().isLastDataset())) {
        File checkSSDFile = new File(filePath);
        String currentDate = ApicUtil.getCurrentTime(DateFormat.DATE_FORMAT_20);

        String newFileName =
            (CDRConstants.COMPLI_REVIEW + CDRConstants.SSD_FILE_PATH_SEPARATOR + CDRConstants.CHECK_SSD +
                CDRConstants.SSD_FILE_PATH_SEPARATOR + predecessorCheck + currentDate + EXCEL_EXTENSION);

        newFileName = checkHexFile(input, predecessorCheck, checkSSDFile, currentDate, newFileName);

        try {
          Path newFilePath = Files.move(checkSSDFile.toPath(),
              new File(filePath.replace(checkSSDFile.getName(), newFileName)).toPath(),
              java.nio.file.StandardCopyOption.REPLACE_EXISTING);
          newFileNames.add(newFilePath.toString());
          fileNamesToReplace.add(checkSSDFile.toString());
          if (!checkSSDFile.toString().contains(EXTERNAL)) {
            data.setCheckSSDReportExcel(newFilePath.toString());
          }

        }
        catch (IOException ex) {
          setErrorInSummary(ex, data);
        }
      }
    }

    outputStrSet.removeAll(fileNamesToReplace);
    outputStrSet.addAll(newFileNames);
    deleteOtherFiles(outputStrSet);

    return CommonUtils.isNotEmpty(newFileNames) ? newFileNames : filterStrings(outputStrSet, EXCEL_EXTENSION);
  }


  /**
   * @param input
   * @param predecessorCheck
   * @param checkSSDFile
   * @param currentDate
   * @param newFileName
   * @return
   */
  private String checkHexFile(final CompliCheckSSDInput input, final String predecessorCheck, final File checkSSDFile,
      final String currentDate, String newFileName) {
    if (input.getHexFileName() != null) {
      newFileName = createNewFileNameFromHexName(input, checkSSDFile, currentDate, predecessorCheck);
    }
    return newFileName;
  }


  private String createNewFileNameFromHexName(final CompliCheckSSDInput input, final File checkSSDFile,
      final String currentDate, final String predecessorCheck) {

    String hexFileName = input.getHexFileName();
    hexFileName = hexFileName.substring(0, hexFileName.length() - 4);

    if (checkSSDFile.toString().contains(EXTERNAL)) {
      hexFileName = hexFileName + "_External_";
    }

    return (hexFileName + CDRConstants.SSD_FILE_PATH_SEPARATOR + predecessorCheck + currentDate + EXCEL_EXTENSION);
  }

  /**
   * @param checkSSDOutputFiles checkSSD Output Files
   */
  private void deleteOtherFiles(final Set<String> checkSSDOutputFiles) {
    for (String file : checkSSDOutputFiles) {
      if (!file.endsWith(EXCEL_EXTENSION)) {
        try {
          Files.delete(new File(file).toPath());
        }
        catch (IOException e) {
          getLogger().info("Error while deleting checkSSD output file : " + e.getMessage(), e);
        }
      }
    }
  }

}
