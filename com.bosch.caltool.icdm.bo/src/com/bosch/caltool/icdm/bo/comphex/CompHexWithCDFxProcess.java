/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.comphex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.cdfwriter.CDFWriter;
import com.bosch.caltool.cdfwriter.exception.CDFWriterException;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2LFileInfoLoader;
import com.bosch.caltool.icdm.bo.a2l.A2LFileInfoProvider;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeModel;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader.LOAD_LEVEL;
import com.bosch.caltool.icdm.bo.cdr.CDRReportLoader;
import com.bosch.caltool.icdm.bo.cdr.CDRRuleUtil;
import com.bosch.caltool.icdm.bo.cdr.CheckSSDOutputData;
import com.bosch.caltool.icdm.bo.cdr.CheckSSDResultParam;
import com.bosch.caltool.icdm.bo.cdr.FeatureAttributeAdapterNew;
import com.bosch.caltool.icdm.bo.cdr.ReviewRuleAdapter;
import com.bosch.caltool.icdm.bo.cdr.VCDMInterface;
import com.bosch.caltool.icdm.bo.compli.CompliResultUtil;
import com.bosch.caltool.icdm.bo.compli.CompliReviewData;
import com.bosch.caltool.icdm.bo.compli.CompliReviewSummary;
import com.bosch.caltool.icdm.bo.compli.ComplianceParamReview;
import com.bosch.caltool.icdm.bo.report.compli.ComplianceReportInputModel;
import com.bosch.caltool.icdm.bo.report.compli.ComplianceReviewReport;
import com.bosch.caltool.icdm.bo.shapereview.ShapeReview;
import com.bosch.caltool.icdm.bo.util.ErrorCodeHandler;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CaldataFileParserHandler;
import com.bosch.caltool.icdm.common.util.CaldataFileParserHandler.CALDATA_FILE_TYPE;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.FileIOUtil;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.logger.CDFWriterLogger;
import com.bosch.caltool.icdm.logger.EASEELogger;
import com.bosch.caltool.icdm.logger.ParserLogger;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponents;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.COMPLI_RESULT_FLAG;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.ParameterType;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.QSSD_RESULT_FLAG;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_LOCK_STATUS;
import com.bosch.caltool.icdm.model.cdr.CDRReportData;
import com.bosch.caltool.icdm.model.cdr.CdrReport;
import com.bosch.caltool.icdm.model.cdr.CompliResValues;
import com.bosch.caltool.icdm.model.cdr.CompliReviewOutputData;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.model.cdr.ParameterReviewDetails;
import com.bosch.caltool.icdm.model.cdr.QSSDResValues;
import com.bosch.caltool.icdm.model.cdr.ReviewDetails;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.VcdmHexFileData;
import com.bosch.caltool.icdm.model.cdr.review.PidcData;
import com.bosch.caltool.icdm.model.comphex.CompHexMetaData;
import com.bosch.caltool.icdm.model.comphex.CompHexResponse;
import com.bosch.caltool.icdm.model.comphex.CompHexStatistics;
import com.bosch.caltool.icdm.model.comphex.CompHexWithCDFParam;
import com.bosch.ssd.icdm.model.CDRRule;

/**
 * The Class CompHexWithCDFxProcess.
 *
 * @author gge6cob
 */
public class CompHexWithCDFxProcess extends AbstractSimpleBusinessObject {

  /** The Constant FILE_DELIMITER. */
  public static final String FILE_DELIMITER = "//";

  /** COMPARE HEX Working directory. */
  public static final String COMP_HEX_WORK_DIR = Messages.getString("SERVICE_WORK_DIR") + "//COMPARE_HEX//";

  /** The Constant PDF_EXTN. */
  private static final String PDF_EXTN = "pdf";

  /** The A2L file info. */
  private A2LFileInfo a2lFileInfo;

  /** The cal data objects from hex. */
  private ConcurrentMap<String, CalData> calDataObjectsFromHex;

  /**
   * Warnings map from hex Key - label name, value - list of warnings
   */
  private Map<String, List<String>> parserWarningsMap;

  /** The compli param set. */
  private TreeSet<String> compliQSSDParamSet;

  /** The cdr report. */
  private CdrReport cdrReport;

  /** The cal data map to CD fx. */
  private Map<String, CalData> calDataObjectsFromCDFx;

  /** The A2L file. */
  private A2LFile a2lFile;

  /** The cdm studio result. */
  private HexWithCdfxCompareResult cdmStudioResult;

  /** The cdfx file path. */
  private String cdfxFilePath;

  /** The compli rvw data. */
  private CompliReviewData compliRvwData;

  /** The pidc attr model. */
  private PidcVersionAttributeModel pidcAttrModel;

  /** The compli review summary. */
  private CompliReviewSummary compliReviewSummary;

  /** The compare param set. */
  private TreeSet<CompHexWithCDFParam> compHexResult;

  /** The stat param reviewed. */
  private int statParamReviewed;

  /** The rvwd and equal count. */
  private int rvwdAndEqualCount;

  /** The stat param rvwd not equal. */
  private int statParamRvwdNotEqual;

  /** The folder path. */
  private String outputFolderPath;

  /** The all labels map. */
  private Map<String, Characteristic> allLabelsMap;

  /** The reference id. */
  private String referenceId;

  /** The response. */
  private CompHexResponse response;

  /** The rvw rule adapter. */
  private ReviewRuleAdapter rvwRuleAdapter;

  /**
   * Key - Param name, Value - Compli Review Result - OK/Not OK/C-SSD/SSD2RV
   */
  private final Map<String, String> compliResultMap = new HashMap<>();

  /**
   * Key - Param name, Value - QSSD Result - OK/Q-SSD/NO RULE
   */
  private final Map<String, String> qssdResultMap = new HashMap<>();

  /**
   * Contains parameter that present insides rules Key - Param name, Value - QSSD Result - OK/Q-SSD/NO RULE
   */
  private final Map<String, String> paramInSSDFileMap = new HashMap<>();

  /**
   * Map of QSSD parameters. key - param name , value - ptype
   */
  private Map<String, String> qssdParamMap;

  /**
   * Compare hex statistics
   */
  private final CompHexStatistics compHexStats = new CompHexStatistics();

  /**
   * the variable for all compliance labels
   */
  private final Set<String> compliParamSet = new HashSet<>();

  /**
   * qssd param set
   */
  private final Set<String> qssdParamSet = new HashSet<>();

  /**
   * Instantiates a new comp hex with CD fx process.
   *
   * @param serviceData the service data
   */
  public CompHexWithCDFxProcess(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * Perform compare.
   *
   * @param metaData the meta data
   * @param hexData the hex data
   * @param pverClearingStatus
   * @param isNonSDOMBCRelease
   * @return the comp hex response
   */
  public CompHexResponse performCompare(final CompHexMetaData metaData, final byte[] hexData,
      final List<Boolean> isNonSDOMBCRelease, final boolean pverClearingStatus) {

    this.response = new CompHexResponse();
    try {
      // create output folder
      createOutputFolder();

      this.response.setReferenceId(this.referenceId);
      this.a2lFile = new A2LFileInfoLoader(getServiceData()).getDataObjectByID(metaData.getPidcData().getA2lFileId());
      this.a2lFileInfo = new A2LFileInfoProvider(getServiceData()).fetchA2LFileInfo(this.a2lFile);
      this.allLabelsMap = this.a2lFileInfo.getAllModulesLabels();

      // Save hex
      this.response.setHexFileName(metaData.getHexFileName());
      String hexFilePath;
      if (metaData.isHexFromVcdm()) {
        getLogger().debug("Downloading HEX file from vCDM. DST Vers ID : {}", metaData.getVcdmDstVersId());
        this.response.setVcdmDst(metaData.getVcdmDstSource());
        hexFilePath = downloadHexFromVcdm(metaData.getVcdmDstVersId());
      }
      else {
        getLogger().debug("Saving HEX file to server for processing ...");
        this.response.setSrcHexFilePath(metaData.getSrcHexFilePath());
        hexFilePath = saveHexFile(metaData.getHexFileName(), hexData);
      }
      getLogger().debug("Hex file stored in path : {}", hexFilePath);

      // parse HEX file to get the parameter names
      getLogger().debug("Parsing HEX file and getting caldata objects ...");
      fillCalDataMap(metaData.getHexFileName(), hexFilePath);

      // CDR report - latest review
      getLogger().debug("Fetching CDR Report. PidcA2lId: {}", metaData.getPidcData().getPidcA2lId());
      getCdrReport(metaData.getPidcData());
      this.cdrReport.setParamPropsMap(null);
      this.response.setCdrReport(this.cdrReport);

      // Generate CDFx
      getLogger().debug("Generating CDFx file ...");
      generateCDFxFile();

      getLogger().debug("Comparing caldata objects ...");
      callCompQuantized();

      // Compli Check
      getLogger().debug("Running Compliance check ...");
      if (pverClearingStatus) {
        doCompliCheck(metaData.getPidcData(), hexFilePath, isNonSDOMBCRelease);
      }

      // Shape check
      getLogger().debug("Running Shape check ...");
      doShapeCheck();

      // Compare results
      getLogger().debug("Creating compare param list. Parameter count : {}", this.allLabelsMap.size());
      createCompParamList();
      this.response.setCompHexResult(this.compHexResult);

      // Create PDF report
      if (pverClearingStatus) {
        getLogger().debug("Creating Compliance PDF report ...");
        createPDFReport(metaData.getPidcData(), metaData.getHexFileName(), isNonSDOMBCRelease.get(0));
      }


      // compute statistics for response
      fillCompHexStats();

      this.response.setCompHexStatistics(this.compHexStats);

      this.response.setProcessCompleted(true);
    }
    catch (Exception exp) {
      getLogger().error(exp.getMessage(), exp);
      this.response.getErrorMsgSet().add(exp.getMessage());
      this.response.setProcessCompleted(false);
    }

    return this.response;
  }

  /**
   * Download hex from vcdm.
   *
   * @param vcdmDstVersId the vcdm dst vers id
   * @return the string
   * @throws DataException the data exception
   */
  private String downloadHexFromVcdm(final long vcdmDstVersId) throws DataException {
    VcdmHexFileData hexFileData = getVcdmInterfaceForSuperUser().loadHexFile(vcdmDstVersId, this.outputFolderPath);
    if (hexFileData != null) {
      return this.outputFolderPath + FILE_DELIMITER + hexFileData.getFileName();
    }
    return null;
  }

  /**
   * Creates the PDF report.
   *
   * @param response the response
   * @param pidcData the pidc data
   * @param hexFileName the hex file name
   * @param isNonSDOMBCRelease
   */
  private void createPDFReport(final PidcData pidcData, final String hexFileName, final boolean isNonSDOMBCRelease) {
    try {
      String fileName = "CompHEX_" + FilenameUtils.removeExtension(hexFileName);
      // Hex Compare PDF
      String filePath = CompHexWithCDFxProcess.COMP_HEX_WORK_DIR + CompHexWithCDFxProcess.FILE_DELIMITER +
          this.referenceId + CompHexWithCDFxProcess.FILE_DELIMITER + fileName;
      File pdfFile = new File(CommonUtils.getCompletePdfFilePath(filePath, PDF_EXTN));

      ComplianceReportInputModel inputModel = new ComplianceReportInputModel();
      createInputModel(inputModel, pidcData, pdfFile.getAbsolutePath());

      if ((this.compliReviewSummary != null) && (this.compliReviewSummary.getCheckSSDOutData() != null)) {
        ComplianceReviewReport report = new ComplianceReviewReport(getServiceData(), inputModel);
        report.createPdf(isNonSDOMBCRelease);
      }

    }
    catch (Exception e) {
      this.response.getErrorMsgSet().add("PDF report creation failed : " + e.getMessage());
      getLogger().error(e.getMessage(), e);
    }
  }


  private void createInputModel(final ComplianceReportInputModel inputModel, final PidcData pidcData,
      final String filePath) {

    // set a2l file name
    String filename = this.response.getCdrReport().getA2lFile().getFilename();
    if (this.response.getCdrReport().getPidcA2l().getVcdmA2lName() != null) {
      filename = this.response.getCdrReport().getPidcA2l().getVcdmA2lName();
    }
    inputModel.getCompliReviewInputMetaData().setA2lFileName(filename);

    // set pver name details
    inputModel.getCompliReviewInputMetaData().setPverName(this.response.getCdrReport().getA2lFile().getSdomPverName());
    inputModel.getCompliReviewInputMetaData()
        .setPverRevision(String.valueOf(this.response.getCdrReport().getA2lFile().getSdomPverRevision()));
    inputModel.getCompliReviewInputMetaData()
        .setPverVariant(String.valueOf(this.response.getCdrReport().getA2lFile().getSdomPverVariant()));
    inputModel.getCompliReviewInputMetaData().getHexfileIdxMap().put(1L, this.response.getHexFileName());
    // in no-variant cases pidc version id will be used to fetch the pidc name
    inputModel.getCompliReviewInputMetaData().getHexFilePidcElement().put(1L,
        pidcData.getSelPIDCVariantId() != null ? pidcData.getSelPIDCVariantId() : pidcData.getSourcePidcVerId());
    if (null != this.response.getVcdmDst()) {
      inputModel.getCompliReviewInputMetaData().getDstMap().put(1L, this.response.getVcdmDst());
    }

    inputModel.getPidcElementNameMap().put(1L, this.response.getCdrReport().getPidcVersion().getName());

    if (CommonUtils.isNotNull(this.compliRvwData.getAttrValModelSetInclSubVar())) {
      // AttrValueModSet converted to tree set to sort the elements
      //For variant cases pidc variant id will be used as key to the getAttrValueModMap
      // in no-variant cases pidc version id will be used as key to the getAttrValueModMap
      inputModel.getAttrValueModMap().put(
          pidcData.getSelPIDCVariantId() != null ? pidcData.getSelPIDCVariantId() : pidcData.getSourcePidcVerId(),
          new TreeSet<>(this.compliRvwData.getAttrValModelSetInclSubVar()));
    }

    // complireview map
    Map<Long, CompliReviewOutputData> compliReviewMap = new HashMap<>();
    CompliReviewOutputData outputData = new CompliReviewOutputData();
    outputData.setHexfileName(this.response.getHexFileName());

    if (this.compliReviewSummary != null) {
      if (null != this.compliReviewSummary.getCheckSSDOutData()) {
        // setting compli result for all params which are not of type Compliance

        fillCompliResults();

        // add skipped params directly
        this.compliResultMap.putAll(this.compliReviewSummary.getCheckSSDOutData().getLabelsInSsdMap());
        // add params without rule directly
        this.compliResultMap.putAll(this.compliReviewSummary.getCheckSSDOutData().getLabelsWithoutRulesMap());

        // add qssd params without rule directly
        this.qssdResultMap.putAll(this.compliReviewSummary.getCheckSSDOutData().getQssdLabelsWithoutRulesMap());

        this.compliResultMap.putAll(this.paramInSSDFileMap);

        // setting compli result map
        outputData.setCompliResult(this.compliResultMap);
      }

      outputData.setQssdResult(this.qssdResultMap);

      // set statistics data
      computeCompliStatistics(outputData);

      // set qssd statistics
      computeQssdStatistics(outputData);
    }

    compliReviewMap.put(1L, outputData);
    inputModel.getCompliReviewOutput().putAll(compliReviewMap);

    inputModel.setDestFilePath(filePath);

    // set param function map
    inputModel.setParamFunctionMap(getParamFunctionMap(compliReviewMap));

  }

  private void fillCompliResults() {
    for (Entry<String, CheckSSDResultParam> entry : this.compliReviewSummary.getCheckSSDOutData()
        .getCheckSSDCompliParamMap().entrySet()) {

      if (!this.compliResultMap.containsKey(entry.getKey()) && isComplianceParam(entry.getKey())) {
        this.compliResultMap.put(entry.getKey(), entry.getValue().getCompliReportModel().getStatus());
      }

      if (!this.compliResultMap.containsKey(entry.getKey()) && !isComplianceParam(entry.getKey()) &&
          (entry.getValue().getCompliReportModel() != null)) {
        COMPLI_RESULT_FLAG result = CompliResultUtil.getResultBasedOnReportModel(entry.getValue(),
            CDRConstants.COMPLI_RESULT_FLAG.NO_RULE, entry.getValue().getCompliReportModel());
        this.paramInSSDFileMap.put(entry.getKey(), result.getUiType());
      }

    }
  }


  private Map<String, String> getParamFunctionMap(final Map<Long, CompliReviewOutputData> compliReviewOutputDataMap) {
    Map<String, Characteristic> characteristicsMap = this.a2lFileInfo.getAllModulesLabels();
    Map<String, String> allParamFunctionMap = new HashMap<>();
    for (CompliReviewOutputData compliReviewOutputData : compliReviewOutputDataMap.values()) {

      Set<String> paramSet = new HashSet<>();

      paramSet.addAll(compliReviewOutputData.getCompliResult().keySet());
      paramSet.addAll(compliReviewOutputData.getQssdResult().keySet());

      Map<String, String> paramFuncMap = getParamFunctionMapForHex(characteristicsMap, paramSet);
      allParamFunctionMap.putAll(paramFuncMap);
    }

    return allParamFunctionMap;
  }

  private Map<String, String> getParamFunctionMapForHex(final Map<String, Characteristic> characteristicsMap,
      final Set<String> paramSet) {

    Map<String, String> paramFunctionMap = new HashMap<>();
    for (String param : paramSet) {
      Characteristic characteristic = characteristicsMap.get(param);
      if ((characteristic != null) && (characteristic.getDefFunction() != null)) {
        paramFunctionMap.put(param, characteristic.getDefFunction().getName());
      }
      else {
        paramFunctionMap.put(param, "-");
      }
    }

    return paramFunctionMap;
  }

  private void computeQssdStatistics(final CompliReviewOutputData outputData) {
    int qssdPassed = 0;
    int qssdFailed = 0;
    int qssdNoRule = 0;

    for (String qssdResult : outputData.getQssdResult().values()) {
      if (CDRConstants.QSSD_RESULT_FLAG.OK.getUiType().equals(qssdResult)) {
        qssdPassed++;
      }
      else if (CDRConstants.QSSD_RESULT_FLAG.QSSD.getUiType().equals(qssdResult)) {
        qssdFailed++;
      }
      else if (CDRConstants.QSSD_RESULT_FLAG.NO_RULE.getUiType().equals(qssdResult)) {
        qssdNoRule++;
      }

    }

    outputData.setQssdCount(outputData.getQssdResult().size());
    outputData.setQssdPassCount(qssdPassed);
    outputData.setQssdFailCount(qssdFailed);
    outputData.setQssdNoRuleCount(qssdNoRule);
    outputData.setQssdTotalFailCount(qssdFailed + qssdNoRule);
  }


  /**
   * Set compliance statistics
   *
   * @param outputData
   */
  private void computeCompliStatistics(final CompliReviewOutputData outputData) {
    int compliPassed = 0;
    int compliFailedCssd = 0;
    int compliFailedSsd2rv = 0;
    int compliNoRule = 0;

    for (String compliResult : outputData.getCompliResult().values()) {
      if (compliResult.equals(CDRConstants.COMPLI_RESULT_FLAG.OK.getUiType())) {
        compliPassed++;
      }
      else if (compliResult.equals(CDRConstants.COMPLI_RESULT_FLAG.CSSD.getUiType())) {
        compliFailedCssd++;
      }
      else if (compliResult.equals(CDRConstants.COMPLI_RESULT_FLAG.NO_RULE.getUiType())) {
        compliNoRule++;
      }
      else if (compliResult.equals(CDRConstants.COMPLI_RESULT_FLAG.SSD2RV.getUiType())) {
        compliFailedSsd2rv++;
      }

    }

    outputData.setCompliCount(outputData.getCompliResult().size());
    outputData.setCompliPassCount(compliPassed);
    outputData.setCssdFailCount(compliFailedCssd);
    outputData.setNoRuleCount(compliNoRule);
    outputData.setSsd2RvFailCount(compliFailedSsd2rv);

    // set statistics data for response
    this.compHexStats.setStatCompliParamInA2L(outputData.getCompliResult().size());
    this.compHexStats.setStatCompliNoRuleFailed(compliNoRule);
    this.compHexStats.setStatCompliParamPassed(compliPassed);
    this.compHexStats.setStatCompliCssdFailed(compliFailedCssd);
    this.compHexStats.setStatCompliSSDRvFailed(compliFailedSsd2rv);
  }

  /**
   * Save hex file.
   *
   * @param hexFileName the hex file name
   * @param hexData the hex data
   * @return true, if successful
   * @throws IcdmException the icdm exception
   */
  private String saveHexFile(final String hexFileName, final byte[] hexData) throws IcdmException {
    String filePath = this.outputFolderPath + FILE_DELIMITER + hexFileName;
    try (FileOutputStream destOutputStream = new FileOutputStream(filePath)) {
      destOutputStream.write(hexData, 0, hexData.length);
      getLogger().debug("Hex File saved: " + destOutputStream);
      return filePath;
    }
    catch (IOException e) {
      getLogger().error("Error: " + e, e);
      throw new IcdmException("IO Exception while saving Hex File " + e.getLocalizedMessage(), e);
    }
  }

  /**
   * create Output folder.
   */
  private void createOutputFolder() {
    // create the file only for the first time
    if (this.outputFolderPath == null) {
      String currentDate = ApicUtil.getCurrentTime(DateFormat.DATE_FORMAT_20);
      File file = new File(COMP_HEX_WORK_DIR);
      if (!file.exists()) {
        file.mkdir();
      }
      this.referenceId = "compareHex_" + currentDate + "_" + RandomStringUtils.randomNumeric(8);
      file = new File(file.getAbsoluteFile() + FILE_DELIMITER + this.referenceId);
      file.mkdir();
      this.outputFolderPath = file.getAbsolutePath();
    }
  }

  /**
   * Do shape check.
   */
  private void doShapeCheck() {
    ShapeReview shapeReview = new ShapeReview(this.calDataObjectsFromHex, this.a2lFileInfo, getServiceData());
    try {
      shapeReview.performReview();
    }
    catch (DataException e) {
      this.response.getErrorMsgSet().add(e.getMessage());
      getLogger().error(e.getMessage(), e);
    }
  }

  /**
   * Gets the compli rvw data.
   *
   * @param pidcData the pidc data
   * @return the Compli reviewData
   * @throws IcdmException the icdm exception
   */
  private CompliReviewData getCompliRvwData(final PidcData pidcData) throws IcdmException {
    PidcA2l pidcA2l = new PidcA2lLoader(getServiceData()).getDataObjectByID(pidcData.getPidcA2lId());
    PidcVersion selPidc = new PidcVersionLoader(getServiceData()).getDataObjectByID(pidcA2l.getPidcVersId());
    PidcVariant selPidcVar = null;
    if (null != pidcData.getSelPIDCVariantId()) {
      selPidcVar = new PidcVariantLoader(getServiceData()).getDataObjectByID(pidcData.getSelPIDCVariantId());
    }
    // load pidc details
    this.pidcAttrModel = new ProjectAttributeLoader(getServiceData()).createModel(pidcData.getSourcePidcVerId(),
        LOAD_LEVEL.L3_VAR_ATTRS);

    Map<String, A2LBaseComponents> a2lBaseComponentsMap =
        new A2LFileInfoLoader(getServiceData()).getA2lBaseComponents(pidcA2l.getA2lFileId());
    SortedSet<A2LBaseComponents> a2lBcSet = new TreeSet<>(a2lBaseComponentsMap.values());

    this.compliRvwData = new CompliReviewData(selPidc, selPidcVar, this.allLabelsMap, this.calDataObjectsFromHex,
        this.parserWarningsMap, a2lBcSet);
    this.compliRvwData.setCompliSSDFilePath(this.outputFolderPath);

    return this.compliRvwData;
  }

  /**
   * Compares HEX and CDFx file. This method replaces the formerly existing methods to compare data with CDM Studio.
   */
  private void callCompQuantized() {
    this.cdmStudioResult = HexWithCdfxComparison.compare(this.a2lFileInfo, this.calDataObjectsFromHex,
        this.calDataObjectsFromCDFx, getLogger());
  }

  /**
   * Generate CD fx file.
   *
   * @throws ClassNotFoundException the class not found exception
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws IcdmException the icdm exception
   */
  /**
   * @param reportData
   * @param calDataObjectsFromHex
   * @throws IOException
   * @throws ClassNotFoundException
   * @throws IcdmException
   */
  private void generateCDFxFile() throws ClassNotFoundException, IOException, IcdmException {

    this.cdfxFilePath = this.outputFolderPath + File.separator + "LatestRvwCDFxFile" + this.a2lFile.getFilename() +
        CommonUtilConstants.CDFX_EXTN;
    this.calDataObjectsFromCDFx = getChkValCalDataMap();

    // iCDM-1455
    // check if file is used by any other process (Eg; CDM Studio..)
    if (FileIOUtil.checkIfFileIsLocked(this.cdfxFilePath)) {
      getLogger().error(
          "CDFX Export Error : File is already used by another process. \n Please provide a different filename or close the file before export. \n File path: " +
              this.cdfxFilePath);
    }

    // Icdm-932 change the name to CDFX
    getLogger().debug("Exporting as CDFX: ...");

    exportCDF(this.calDataObjectsFromCDFx);

    getLogger().info("CDFX exported successfully to file  " + this.cdfxFilePath);
  }

  /**
   * ICDM-2496 method to get check values of latest reviews in the format Map<String, CalData>.
   *
   * @return map of key- param name, value - caldata object
   * @throws ClassNotFoundException the class not found exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private Map<String, CalData> getChkValCalDataMap() throws ClassNotFoundException, IOException {
    Map<String, CalData> calDataMap = new HashMap<>();
    Set<String> paramNames = this.calDataObjectsFromHex.keySet();

    // iterate the check value map
    for (String paramName : paramNames) {
      ParameterReviewDetails paramReviewDetails = getParamReviewDetails(paramName, 0);
      if (null != paramReviewDetails) {
        CalData calData = CalDataUtil.getCalDataObj(paramReviewDetails.getCheckedVal());
        // this check is done to avoid exception in CDF Writer
        if (calData != null) {
          calDataMap.put(paramName, calData);
        }
      }
    }
    return calDataMap;
  }

  /**
   * Export CDF.
   *
   * @param cdfCalDataObjects the cdf cal data objects
   * @throws IcdmException the icdm exception
   */
  private void exportCDF(final Map<String, CalData> cdfCalDataObjects) throws IcdmException {
    try {
      CDFWriter cdfWriter = new CDFWriter(cdfCalDataObjects, this.cdfxFilePath, CDFWriterLogger.getInstance());
      // invoke this method to generate CDF file
      cdfWriter.writeCalDataToXML();
      // after clicking cancel button in the progress window
    }
    catch (CDFWriterException cdfwe) {
      getLogger().error(cdfwe.getLocalizedMessage(), cdfwe);
      throw new IcdmException(cdfwe.getLocalizedMessage(), cdfwe);
    }
  }

  /**
   * Gets the cdr report.
   *
   * @param pidcData the pidc data
   * @return the cdr report
   * @throws IcdmException the icdm exception
   */
  private void getCdrReport(final PidcData pidcData) throws IcdmException {

    CDRReportData cdrRprtData = new CDRReportData();
    cdrRprtData.setPidcA2lId(pidcData.getPidcA2lId());
    cdrRprtData.setMaxResults(1);// 1 is given as we need only lastest review
    cdrRprtData.setFetchCheckVal(true);// true as the check value needs to be exported
    cdrRprtData.setVarId(pidcData.getSelPIDCVariantId());

    this.cdrReport = new CDRReportLoader(getServiceData()).fetchCDRReportData(cdrRprtData);
  }

  /**
   * Load compli params.
   */
  private void loadCompliOrQSSDParamFromA2l() {
    Set<String> allModulesLabels = this.allLabelsMap.keySet();
    ParameterLoader prmLdr = new ParameterLoader(getServiceData());
    this.compliQSSDParamSet = new TreeSet<>();
    for (String paramName : allModulesLabels) {
      boolean isCompli = prmLdr.isCompliParameter(paramName);
      boolean isQssd = prmLdr.isQssdParameter(paramName);
      if (isCompli || isQssd) {
        this.compliQSSDParamSet.add(paramName);
        if (isCompli) {
          this.compliParamSet.add(paramName);
        }
        if (isQssd) {
          this.qssdParamSet.add(paramName);
        }
      }
    }
    getLogger().debug("Number of compli params in A2l File = {}", this.compliParamSet.size());
    getLogger().debug("Number of qssd params in A2l File = {}", this.qssdParamSet.size());
  }


  /**
   * to check for qssd parameter
   */
  private boolean isQssdParameter(final String paramName) {
    return this.qssdParamMap.containsKey(paramName);
  }

  /**
   * Fill cal data map.
   *
   * @param hexFileName the hex file name
   * @param hexFilePth the hex file pth
   * @return the concurrent map
   * @throws IcdmException the icdm exception
   */
  private ConcurrentMap<String, CalData> fillCalDataMap(final String hexFileName, final String hexFilePth)
      throws IcdmException {

    CALDATA_FILE_TYPE fileType = CALDATA_FILE_TYPE.getTypeFromFileName(hexFileName);
    // New method to get the Caldata Hex file map.
    try (FileInputStream hexInputStream = new FileInputStream(new File(hexFilePth))) {
      CaldataFileParserHandler handler = new CaldataFileParserHandler(ParserLogger.getInstance(), this.a2lFileInfo);
      this.calDataObjectsFromHex = new ConcurrentHashMap<>(handler.getCalDataObjects(fileType, hexInputStream));
      this.parserWarningsMap = handler.getWarningsMap();
      getLogger().debug("Number of Caldata objects in the file {}", this.calDataObjectsFromHex.size());
    }
    // For now Keep it as exception.throw the Exception from Hex parser
    catch (IcdmException exp) {
      throw exp;
    }
    catch (Exception exp) {
      throw new IcdmException(exp.getMessage(), exp);
    }
    return this.calDataObjectsFromHex;
  }

  /**
   * Review for Compliance Parameters.
   *
   * @param pidcData the pidc data
   * @param hexFilePth the hex file pth
   * @param isNonSDOMBCRelease
   * @param sdomPverName
   * @throws IcdmException the icdm exception
   */
  // ICDM-2440
  private void doCompliCheck(final PidcData pidcData, final String hexFilePth, final List<Boolean> isNonSDOMBCRelease)
      throws IcdmException {

    try {
      this.rvwRuleAdapter = new ReviewRuleAdapter(getServiceData());

      // Identify Compli Parameters in A2l
      loadCompliOrQSSDParamFromA2l();

      // Check for QSSD only labels - w/o compli
      boolean isQSSDOnlyRelease = this.compliParamSet.isEmpty() && !this.qssdParamSet.isEmpty();

      // Compli check
      ComplianceParamReview compliRvw =
          new ComplianceParamReview(getCompliRvwData(pidcData), new FeatureAttributeAdapterNew(getServiceData()),
              this.compliQSSDParamSet, getServiceData(), this.pidcAttrModel, this.outputFolderPath, isQSSDOnlyRelease);

      // Invoke SSD release
      compliRvw.invokeSSDReleaseForCompli(isNonSDOMBCRelease);

      // Invoke CheckSSD here
      if (this.compliRvwData.getCompliSSDFilePath() != null) {
        // Invoke CheckSSD
        this.compliReviewSummary = compliRvw.invokeCheckSSD(this.a2lFileInfo, this.compliRvwData.getCompliSSDFilePath(),
            new File(hexFilePth).getName());

        if ((this.compliReviewSummary.getCheckSSDOutData() != null) &&
            this.compliReviewSummary.getCheckSSDOutData().isReviewHasExp() &&
            (this.compliReviewSummary.getCheckSSDOutData().getReviewExceptionObj() != null)) {
          throw new IcdmException(this.compliReviewSummary.getCheckSSDOutData().getReviewExceptionObj().getMessage(),
              this.compliReviewSummary.getCheckSSDOutData().getReviewExceptionObj());

        }
      }
    }
    catch (IcdmException ex) {
      String errMsg = new ErrorCodeHandler(getServiceData()).getErrorMessage(ex);
      this.response.getErrorMsgSet().add(errMsg);
      this.response.setCompliCheckFailed(true);
      getLogger().error(errMsg, ex);
    }

    // Result - to be set into SetCommonFields()
    if (this.compliReviewSummary != null) {
      if ((this.compliReviewSummary.getCheckSSDOutData() != null) &&
          !this.compliReviewSummary.getCheckSSDOutData().isReviewHasExp()) {
        this.compliRvwData
            .setCheckSSDCompliParamMap(this.compliReviewSummary.getCheckSSDOutData().getCheckSSDCompliParamMap());
        this.compliRvwData
            .setCompliCheckSSDOutputFiles(this.compliReviewSummary.getCheckSSDOutData().getGeneratedCheckSSDFiles());
        this.compliRvwData.setErrorInSSDfile(this.compliReviewSummary.getCheckSSDOutData().getErrorinSSDFile());
      }
      // Errors
      if ((this.compliReviewSummary.getCheckSSDOutData() != null) &&
          this.compliReviewSummary.getCheckSSDOutData().isReviewHasExp()) {
        this.response.getErrorMsgSet().add("CompareHex - COMPLI check failed : CheckSSD report not generated! ");
        getLogger().error("CompareHex - COMPLI check failed : CheckSSD report not generated! ");
        this.response.setCompliCheckFailed(true);
      }
    }
  }

  /**
   * create the compared parameters list.
   */
  private void createCompParamList() {
    this.compHexResult = new TreeSet<>();
    ParameterLoader prmldr = new ParameterLoader(getServiceData());
    this.qssdParamMap = prmldr.getQssdParams();
    Set<String> blackListParams = prmldr.getBlackListParams();
    for (Entry<String, Characteristic> entry : this.allLabelsMap.entrySet()) {
      String paramName = entry.getKey();
      // create new object for each parameter in the map
      CompHexWithCDFParam compHexParam = new CompHexWithCDFParam();
      // set fields
      compHexParam.setParamName(paramName);

      compHexParam.setDependantCharacteristic(entry.getValue().isDependentCharacteristic());

      if (CommonUtils.isNotNull(entry.getValue().getDependentCharacteristic()) &&
          CommonUtils.isNotNull(entry.getValue().getDependentCharacteristic().getCharacteristicName())) {
        compHexParam.setDepCharsName(entry.getValue().getDependentCharacteristic().getCharacteristicName());
      }

      compHexParam.setReadOnly(entry.getValue().isReadOnly());
      compHexParam.setBlackList(isBlackListParameter(paramName, blackListParams));
      // Review Score and Review Comments
      DATA_REVIEW_SCORE reviewScore = getReviewScore(paramName);
      if (reviewScore != null) {
        compHexParam.setReviewScore(reviewScore.getScoreDisplay());
        compHexParam.setHundredPecReviewScore(reviewScore.getHundredPercScoreDisplay());
      }
      compHexParam.setLatestReviewComments(getLatestReviewComment(paramName));

      compHexParam.setQssdParameter(isQssdParameter(paramName));
      String paramType = this.allLabelsMap.get(paramName).getType();
      compHexParam.setParamType(ParameterType.valueOf(paramType));
      if (null != this.allLabelsMap.get(paramName).getDefFunction()) {
        compHexParam.setFuncName(this.allLabelsMap.get(paramName).getDefFunction().getName());
        compHexParam.setFuncVers(this.allLabelsMap.get(paramName).getDefFunction().getFunctionVersion());
      }

      ParameterReviewDetails paramRvw = getParamReviewDetails(paramName, 0);
      if (paramRvw != null) {
        ReviewDetails det = this.cdrReport.getReviewDetMap().get(paramRvw.getRvwID());
        // CDR report data
        compHexParam.setLatestA2lVersion(getLatestA2LVersion(det));
        compHexParam.setLatestFunctionVersion(getLatestFunctionVersion(paramRvw));
      }

      setCdfxAndHexCalData(paramName, compHexParam);

      // REVIEW Result
      setReviewStatus(paramName, compHexParam);
      boolean isEqual = !this.cdmStudioResult.getUnequalPar().contains(paramName);
      compHexParam.setEqual(isEqual);

      // COMPLI Result
      compHexParam.setCompli(isComplianceParam(paramName));

      // if param is of class compliance or if param is used in a compli rule
      setComplianceResult(compHexParam);

      // QSSD check
      setResultForQssdParameters(compHexParam);

      // to compute failed qssd paramerter
      computeQssdFailure(compHexParam);

      // Set comphex statistics
      addParamCdrResultStat(compHexParam, isEqual);

      // Added condition to open checkssd excel file if there is any QSSD failures
      if (compHexParam.isQssdParameter() && (compHexParam.getQssdResult() != QSSDResValues.OK)) {
        this.response.setQSSDCheckFailed(true);
      }

      this.compHexResult.add(compHexParam);
    }
  }

  private void setCdfxAndHexCalData(final String paramName, final CompHexWithCDFParam compHexParam) {
    // Cdfx data
    if (this.calDataObjectsFromCDFx.get(paramName) != null) {
      compHexParam.setCdfxCalDataPhySimpleDispValue(
          this.calDataObjectsFromCDFx.get(paramName).getCalDataPhy().getSimpleDisplayValue());
    }

    // Hex data
    if (this.calDataObjectsFromHex.get(paramName) != null) {
      compHexParam.setHexCalDataPhySimpleDispValue(
          this.calDataObjectsFromHex.get(paramName).getCalDataPhy().getSimpleDisplayValue());
    }
  }

  private void computeQssdFailure(final CompHexWithCDFParam compHexParam) {
    if (compHexParam.isQssdParameter() && QSSDResValues.QSSD.equals(compHexParam.getQssdResult())) {
      this.compHexStats.setStatQSSDParamFailed(this.compHexStats.getStatQSSDParamFailed() + 1);
    }
  }

  private void setComplianceResult(final CompHexWithCDFParam compHexParam) {
    if (compHexParam.isCompli() || isCompliParamAndReportModelAvail(compHexParam)) {
      setCompliResult(compHexParam);
      if (compHexParam.getCompliResult() != CompliResValues.OK) {
        this.response.setCompliCheckFailed(true);
      }
      if (compHexParam.isCompli()) {
        // add the compli param review results for pdf report
        this.compliResultMap.put(compHexParam.getParamName(), compHexParam.getCompliResult().getUiValue());
      }

      if (!this.compliResultMap.containsKey(compHexParam.getParamName()) &&
          !isQssdParameter(compHexParam.getParamName()) && (this.compliReviewSummary.getCheckSSDOutData()
              .getCheckSSDCompliParamMap().get(compHexParam.getParamName()).getCompliReportModel() != null)) {
        this.paramInSSDFileMap.put(compHexParam.getParamName(), compHexParam.getCompliResult().getUiValue());
      }
    }
    else {
      // NON-COMPLI labels here
      compHexParam.setCompliResult(CompliResValues.NA);
      compHexParam.setCompliTooltip("NA");
    }
  }


  /**
   * @param paramName
   * @param blackListParams
   * @return
   */
  private boolean isBlackListParameter(final String paramName, final Set<String> blackListParams) {
    return blackListParams.contains(paramName);
  }

  private void setResultForQssdParameters(final CompHexWithCDFParam compParam) {

    if (isCompliParamAvailInMap(compParam) && isQssdParamAndReportModelAvail(compParam)) {

      if ((this.compliReviewSummary.getCompliReviewData() == null) ||
          (this.compliReviewSummary.getCompliReviewData().getCheckSSDCompliParamMap() == null) ||
          this.compliReviewSummary.getCheckSSDOutData().isReviewHasExp()) {
        compParam.setQssdTooltip("NOT Evaluated");
        compParam.setQssdResult(QSSDResValues.NO_RULE);
        this.response.setQSSDCheckFailed(true);
        return;
      }

      setDetailsForQssdParams(compParam);
    }
    else {
      compParam.setQssdTooltip("NA");
      compParam.setQssdResult(QSSDResValues.NA);
    }

  }

  /**
   * @param compParam
   * @return
   */
  private boolean isQssdParamAndReportModelAvail(final CompHexWithCDFParam compParam) {
    return compParam.isQssdParameter() || (this.compliReviewSummary.getCheckSSDOutData().getCheckSSDCompliParamMap()
        .get(compParam.getParamName()).getQssdReportModel() != null);
  }

  /**
   * @param compParam
   * @return
   */
  private boolean isCompliParamAvailInMap(final CompHexWithCDFParam compParam) {
    return (this.compliReviewSummary != null) && (this.compliReviewSummary.getCheckSSDOutData() != null) &&
        (this.compliReviewSummary.getCheckSSDOutData().getCheckSSDCompliParamMap()
            .get(compParam.getParamName()) != null) &&
        (this.compliReviewSummary.getCheckSSDOutData().getCheckSSDCompliParamMap()
            .containsKey(compParam.getParamName()));
  }

  /**
   * @param compParam
   * @return
   */
  private boolean isCompliParamAndReportModelAvail(final CompHexWithCDFParam compParam) {
    return (this.compliReviewSummary != null) && (this.compliReviewSummary.getCheckSSDOutData() != null) &&
        (this.compliReviewSummary.getCheckSSDOutData().getCheckSSDCompliParamMap()
            .get(compParam.getParamName()) != null) &&
        (this.compliReviewSummary.getCheckSSDOutData().getCheckSSDCompliParamMap().get(compParam.getParamName())
            .getCompliReportModel() != null);
  }

  private void setDetailsForQssdParams(final CompHexWithCDFParam compParam) {
    CDRRule qssdRule = null;
    Map<String, List<CDRRule>> ssdRulesForQssd = this.compliReviewSummary.getCompliReviewData().getSsdRulesForQssd();

    if (ssdRulesForQssd != null) {
      Map<String, List<CDRRule>> ssdRulesForQssdIgonoreCase =
          this.compliReviewSummary.getCompliReviewData().getSsdRulesforQssdIgnore();
      try {
        if (ssdRulesForQssd.get(compParam.getParamName()) == null) {
          qssdRule = CDRRuleUtil.assertSingleRule(ssdRulesForQssdIgonoreCase, compParam.getParamName());
        }
        else {
          qssdRule = CDRRuleUtil.assertSingleRule(ssdRulesForQssd, compParam.getParamName());
        }

        if ((this.response.getSsdRulesForQssd() != null) && (qssdRule != null)) {
          ReviewRule rule = this.rvwRuleAdapter.createReviewRule(qssdRule);
          if (!this.response.getSsdRulesForQssd().containsKey(compParam.getParamName())) {
            this.response.getSsdRulesForQssd().put(compParam.getParamName(), new ArrayList<ReviewRule>());
          }
          this.response.getSsdRulesForQssd().get(compParam.getParamName()).add(rule);
        }
      }

      catch (Exception ex) {
        getLogger().error(ex.getMessage(), ex);
      }
    }
    CheckSSDResultParam checkSSDResultParam =
        this.compliReviewSummary.getCheckSSDOutData().getCheckSSDCompliParamMap().get(compParam.getParamName());
    QSSD_RESULT_FLAG qssdResult = getQSSDResult(compParam, checkSSDResultParam);

    if (qssdResult != null) {
      compParam.setQssdResult(QSSDResValues.getQSSDResValue(qssdResult.getUiType()));

      setQssdResultTooltip(compParam, qssdResult);

      this.qssdResultMap.put(compParam.getParamName(), qssdResult.getUiType());
    }
  }

  private void setQssdResultTooltip(final CompHexWithCDFParam compParam, final QSSD_RESULT_FLAG qssdResult) {
    if (CompliResValues.NO_RULE.getUiValue().equals(qssdResult.getUiType()) && !compParam.isQssdParameter()) {
      compParam.setQssdTooltip("NA");
    }
    else {
      compParam.setQssdTooltip(qssdResult.getUiType());
    }
  }

  /**
   * Get the a2l version(SDOM PVER Varian)t of the first review.
   *
   * @param rvwDet the rvw det
   * @return latest a2l version(variant name
   */
  private String getLatestA2LVersion(final ReviewDetails rvwDet) {
    return rvwDet == null ? null : rvwDet.getSdomPverVariant();
  }

  /**
   * Get latest Function version in the latest review.
   *
   * @param paramRvwDetails the param rvw details
   * @return function version
   */
  private String getLatestFunctionVersion(final ParameterReviewDetails paramRvwDetails) {
    return paramRvwDetails == null ? "" : this.cdrReport.getRvwFuncMap().get(paramRvwDetails.getFuncID());
  }

  /**
   * Checks if is compliance param.
   *
   * @param paramName the param name
   * @return if the param is compliant
   */
  private boolean isComplianceParam(final String paramName) {
    return new ParameterLoader(getServiceData()).isCompliParameter(paramName);
  }

  // ICDM-1703
  /**
   * Returns whether the parameter is reviewed as 'YES' or 'NO'.
   *
   * @param paramName selc param name
   * @param compParam the comp param
   */
  private void setReviewStatus(final String paramName, final CompHexWithCDFParam compParam) {
    DATA_REVIEW_SCORE reviewScore = getReviewScore(paramName);

    String rvwStatus = ApicConstants.NEVER_REVIEWED;
    if (reviewScore != null) {
      // ICDM-2585 (Parent Task ICDM-2412)-2
      if (reviewScore.isReviewed() && isOfficialAndLocked(paramName)) {
        compParam.setReviewed(true);
        rvwStatus = ApicConstants.REVIEWED;
      }
      else {
        rvwStatus = ApicConstants.NOT_REVIEWED;
      }
    }
    else {
      compParam.setNeverReviewed(true);
    }
    compParam.setReviewResult(rvwStatus);
  }

  /**
   * Gets the review score.
   *
   * @param paramName paramName
   * @return the enumeration value. For no value retrun null.
   */
  private DATA_REVIEW_SCORE getReviewScore(final String paramName) {
    String reviewScore = getLatestReviewScore(paramName);
    if ((reviewScore == null) || reviewScore.isEmpty()) {
      return null;
    }
    return DATA_REVIEW_SCORE.getType(reviewScore);

  }

  /**
   * Returns latest review score string.
   *
   * @param paramName selc param name
   * @return true if reviewed
   */
  private String getLatestReviewScore(final String paramName) {
    ParameterReviewDetails paramReviewDetailsLatest = getParamReviewDetailsLatest(paramName);
    if (paramReviewDetailsLatest == null) {
      return "";
    }
    return paramReviewDetailsLatest.getReviewScore();
  }

  /**
   * Returns latest review comment
   *
   * @param paramName selc param name
   * @return true if reviewed
   */
  private String getLatestReviewComment(final String paramName) {
    ParameterReviewDetails paramReviewDetailsLatest = getParamReviewDetailsLatest(paramName);
    if (paramReviewDetailsLatest == null) {
      return "";
    }
    return paramReviewDetailsLatest.getRvwComment();
  }

  /**
   * Gets the param review details latest.
   *
   * @param paramName the param name
   * @return the latest review of the parameter
   */
  private ParameterReviewDetails getParamReviewDetailsLatest(final String paramName) {
    return getParamReviewDetails(paramName, 0);
  }

  /**
   * Get the parameter review detail object for the given parameter and column index.
   *
   * @param paramName name of parameter
   * @param colIndex review result number, identified by column index
   * @return ParameterReviewDetails
   */
  private ParameterReviewDetails getParamReviewDetails(final String paramName, final int colIndex) {
    List<ParameterReviewDetails> paramRvwDetList = this.cdrReport.getParamRvwDetMap().get(paramName);
    if ((paramRvwDetList != null) && (colIndex < paramRvwDetList.size())) {
      return paramRvwDetList.get(colIndex);
    }
    return null;
  }

  /**
   * Returns true if the a2l parameter is reviewed and official and locked.
   *
   * @param paramName the given a2l param name
   * @return true if the a2l parameter is reviewed and official and locked
   */
  // ICDM-2585 (Parent Task ICDM-2412)-2
  private boolean isOfficialAndLocked(final String paramName) {
    ReviewDetails reviewDetails = getReviewDetailsLatest(paramName);
    return (reviewDetails != null) &&
        (CDRConstants.REVIEW_TYPE.OFFICIAL == CDRConstants.REVIEW_TYPE.getType(reviewDetails.getReviewType())) &&
        (REVIEW_LOCK_STATUS.YES == REVIEW_LOCK_STATUS.getType(reviewDetails.getLockStatus()));
  }

  /**
   * Gets the review details latest.
   *
   * @param paramName the param name
   * @return the latest review of the parameter
   */
  private ReviewDetails getReviewDetailsLatest(final String paramName) {
    // ICDM-2585 (Parent Task ICDM-2412), changing private scope to public scope, to re-use this method.
    return getReviewDetails(paramName, 0);
  }

  /**
   * Get the review detail object for the review represented by given parameter and column index.
   *
   * @param paramName name of parameter
   * @param colIndex review result number, identified by column index
   * @return ReviewDetails
   */
  private ReviewDetails getReviewDetails(final String paramName, final int colIndex) {
    ParameterReviewDetails paramRvwDet = getParamReviewDetails(paramName, colIndex);
    return paramRvwDet == null ? null : this.cdrReport.getReviewDetMap().get(paramRvwDet.getRvwID());
  }


  /**
   * Sets the compli result. - Logic as per CmdModCDRResultParam.class(getCompliResultFlag())
   *
   * @param param the param
   */
  private void setCompliResult(final CompHexWithCDFParam param) {
    // by default result is COMPLI - No Rule
    param.setCompliResult(CompliResValues.NO_RULE);
    param.setCompliTooltip(CompliResValues.NO_RULE.getUiValue());

    if ((this.compliReviewSummary == null) || (this.compliReviewSummary.getCompliReviewData() == null) ||
        this.compliReviewSummary.getCheckSSDOutData().isReviewHasExp()) {
      // Compli check not evaluated

      param.setCompliTooltip("NOT Evaluated");
      this.response.setCompliCheckFailed(true);
      return;
    }

    CDRRule compliRule = addCompliRuleInfoToResponse(param);

    CDRConstants.COMPLI_RESULT_FLAG result = getCompliResFromCheckSsdOutData(param, compliRule);
    if (result != null) {
      param.setCompliResult(CompliResValues.getCompliResValue(result.getUiType()));
      if (CompliResValues.NO_RULE.getUiValue().equals(result.getUiType()) && !param.isCompli()) {
        param.setCompliTooltip("NA");
      }
      else {
        param.setCompliTooltip(result.getUiType());
      }
    }
  }

  private CDRRule addCompliRuleInfoToResponse(final CompHexWithCDFParam param) {
    CDRRule compliRule = null;

    Map<String, List<CDRRule>> ssdRulesForCompliance =
        this.compliReviewSummary.getCompliReviewData().getSsdRulesForCompliance();
    if (ssdRulesForCompliance != null) {
      Map<String, List<CDRRule>> ssdRulesIgonoreCase =
          this.compliReviewSummary.getCompliReviewData().getSsdRulesForComplianceCaseIgnore();
      try {
        if (ssdRulesForCompliance.get(param.getParamName()) == null) {
          compliRule = CDRRuleUtil.assertSingleRule(ssdRulesIgonoreCase, param.getParamName());
        }
        else {
          compliRule = CDRRuleUtil.assertSingleRule(ssdRulesForCompliance, param.getParamName());
        }

        if ((this.response.getSsdRulesForCompliance() != null) && (compliRule != null)) {
          ReviewRule rule = this.rvwRuleAdapter.createReviewRule(compliRule);
          if (!this.response.getSsdRulesForCompliance().containsKey(param.getParamName())) {
            this.response.getSsdRulesForCompliance().put(param.getParamName(), new ArrayList<ReviewRule>());
          }
          this.response.getSsdRulesForCompliance().get(param.getParamName()).add(rule);
        }

      }
      catch (Exception ex) {
        getLogger().error(ex.getMessage(), ex);
      }
    }
    return compliRule;
  }

  private CDRConstants.COMPLI_RESULT_FLAG getCompliResFromCheckSsdOutData(final CompHexWithCDFParam param,
      final CDRRule compliRule) {

    // COMPLI check failed due to unexpected errors
    CheckSSDOutputData checkSSDOutData = this.compliReviewSummary.getCheckSSDOutData();

    CheckSSDResultParam compliSSDResParam = null;
    if ((checkSSDOutData != null) && (checkSSDOutData.getCheckSSDCompliParamMap() != null)) {
      compliSSDResParam = checkSSDOutData.getCheckSSDCompliParamMap().get(param.getParamName());
    }
    CDRConstants.COMPLI_RESULT_FLAG result = CompliResultUtil.getCompliResult(compliRule, compliSSDResParam, false,
        param.getParamName(), param.isCompli(), false);

    // check the result with the labels available in the Check SSD file.
    // This check already done in Compli Review in welcome page.
    if (checkSSDOutData != null) {
      String labelRes = checkSSDOutData.getLabelsInSsdMap().get(param.getParamName());
      if (labelRes != null) {
        result = CDRConstants.COMPLI_RESULT_FLAG.getTypeFromUiType(labelRes);
      }
      labelRes = checkSSDOutData.getLabelsWithoutRulesMap().get(param.getParamName());
      if (labelRes != null) {
        result = CDRConstants.COMPLI_RESULT_FLAG.getTypeFromUiType(labelRes);
      }
    }

    return result;
  }

  /**
   * @param param
   * @param compliSSDResParam
   * @return
   */
  private QSSD_RESULT_FLAG getQSSDResult(final CompHexWithCDFParam param, final CheckSSDResultParam compliSSDResParam) {
    try {
      CDRRule qssdRule = null;
      if (CommonUtils.isNotEmpty(this.compliReviewSummary.getCompliReviewData().getSsdRulesForQssd())) {
        qssdRule = CDRRuleUtil.assertSingleRule(this.compliReviewSummary.getCompliReviewData().getSsdRulesForQssd(),
            param.getParamName());
      }
      return CompliResultUtil.getQssdResult(qssdRule, compliSSDResParam,
          this.compliReviewSummary.getCompliReviewData().isErrorinSSDFile(), param.getParamName(),
          param.isQssdParameter());
    }
    catch (CommandException ex) {
      getLogger().error(ex.getMessage(), ex);
    }
    return null;
  }


  /**
   * Sets the param result stat.
   *
   * @param param the param
   * @param isEqual the is equal
   */
  private void addParamCdrResultStat(final CompHexWithCDFParam param, final boolean isEqual) {
    // count reviewed parameters
    boolean reviewed = param.isReviewed();
    if (reviewed) {
      this.statParamReviewed++;
      // count parameter that are equal and reviewed
      if (isEqual) {
        this.rvwdAndEqualCount++;
      }
      else {
        this.statParamRvwdNotEqual++;
      }
    }

  }


  /**
   * Gets the comp hex stats.
   */
  private void fillCompHexStats() {
    this.compHexStats.setStatFilteredParam(this.statParamReviewed);
    this.compHexStats.setStatFilteredParamRvwd(this.statParamRvwdNotEqual);
    this.compHexStats.setStatFilteredParamRvwdNotEqual(this.statParamRvwdNotEqual);
    this.compHexStats.setStatParamReviewed(this.statParamReviewed);
    this.compHexStats.setStatParamRvwdEqual(this.rvwdAndEqualCount);
    this.compHexStats.setStatParamRvwdNotEqual(this.statParamRvwdNotEqual);
    this.compHexStats.setStatTotalParamInA2L(this.allLabelsMap.size());
    this.compHexStats.setStatEqualParCount(this.cdmStudioResult.getEqualParCount());
  }

  /**
   * Gets the vcdm interface for super user.
   *
   * @return the vcdm interface for super user
   * @throws DataException the data exception
   */
  private VCDMInterface getVcdmInterfaceForSuperUser() throws DataException {
    VCDMInterface vCdmSuperUser = null;
    try {
      vCdmSuperUser = new VCDMInterface(EASEELogger.getInstance());
    }
    catch (Exception exp) {
      throw new DataException("VCDM Webservice login failed. Opening A2L Files is not possible. " + exp.getMessage(),
          exp);
    }
    return vCdmSuperUser;
  }
}
