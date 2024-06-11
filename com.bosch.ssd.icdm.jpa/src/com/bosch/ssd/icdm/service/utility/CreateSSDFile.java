package com.bosch.ssd.icdm.service.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import com.bosch.ssd.icdm.common.utility.VarCodeLabelUtil;
import com.bosch.ssd.icdm.constants.SSDiCDMInterfaceConstants;
import com.bosch.ssd.icdm.entity.TempLabellistInterface;
import com.bosch.ssd.icdm.entity.TempSsdFile;
import com.bosch.ssd.icdm.exception.ExceptionUtils;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException.SSDiCDMInterfaceErrorCodes;


/**
 * @author gue1cob - This class creates the SSD file containing the label with rules
 */
public class CreateSSDFile {

  /**
   *
   */
  private static final String DELIMITER = "\\";

  private final DBQueryUtils dbQueryUtils;

  private String filePath;

  private static final String REPORT_NAME = "SSD_Review";
  /**
   * to check if rule is present or not
   */
  private boolean ruleCheck;

  /**
   * Cosntructor
   *
   * @param dbQueryUtils utils
   */
  public CreateSSDFile(final DBQueryUtils dbQueryUtils) {
    this.dbQueryUtils = dbQueryUtils;
  }

  /**
   * Populate the labels in the temp table
   *
   * @Deprecated
   * @param labelNames -list of labels
   * @param ssdNodeId Node ID
   * @param userName User Name
   * @return - path of .ssd file
   * @throws SSDiCDMInterfaceException Exception
   */
  @Deprecated
  public String readLabels(final List<String> labelNames, final BigDecimal ssdNodeId, final String userName)
      throws SSDiCDMInterfaceException {
    insertLabelsinTempWithCase(labelNames, ssdNodeId);
    return createSSDFile(ssdNodeId, userName);
  }

  /**
   * SSD- 333- refactored
   *
   * @Deprecated
   * @param labelNames name
   * @param ssdNodeId nodeid
   */
  @Deprecated
  private void insertLabelsinTempWithCase(final List<String> labelNames, final BigDecimal ssdNodeId) {
//    SSDEntityManagerUtil.handleTransaction(TransactionState.TRANSACTION_BEGIN, this.dbQueryUtils.getEntityManager())
    this.dbQueryUtils.getEntityManager().createNamedQuery("TempLabellistInterface.deleteTempValues").executeUpdate();
    Set<String> label = new HashSet<>();
    int index = 0;
    for (String str : labelNames) {
      if (label.add(str.toUpperCase(Locale.ENGLISH))) {
        TempLabellistInterface tempLabel = new TempLabellistInterface();
        tempLabel.setLabel(str);

        if (str.contains(SSDiCDMInterfaceConstants.VARCODE_SYM)) {
          tempLabel.setUpperLabel(VarCodeLabelUtil.getBaseParamFirstName(str).toUpperCase());
          tempLabel.setDefaultRule("Y");
        }
        else {
          tempLabel.setUpperLabel(str.toUpperCase(Locale.ENGLISH));
          tempLabel.setDefaultRule("N");
        }

        tempLabel.setNodeId(ssdNodeId);
        tempLabel.setSeqNo(new BigDecimal(index));
        index++;
        this.dbQueryUtils.getEntityManager().persist(tempLabel);
      }
    }
   // SSDEntityManagerUtil.handleTransaction(TransactionState.TRANSACTION_COMMIT, this.dbQueryUtils.getEntityManager())
  }

  /**
   * @Deprecated
   * @param nodeId - node on which rules are defined
   * @param userName name
   * @return - path of the .ssd file generated if rules present - returns null if no rules present
   * @throws SSDiCDMInterfaceException Exception
   */
  @Deprecated
  private String createSSDFile(final BigDecimal nodeId, final String userName) throws SSDiCDMInterfaceException {
    String obj = this.dbQueryUtils.ssdFileReportGeneration(nodeId, userName);
    int resultStatus = 0;
    if (Objects.nonNull(obj)) {
      try {
        StringBuilder lvlLvlDataColl = new StringBuilder();
        lvlLvlDataColl = getTempSSDDtls(this.dbQueryUtils.getSSDFileData(obj));
        if (!this.ruleCheck) { // SSD-263
          return null;
        }
        this.filePath = getAppDataTempFolderPath();
        resultStatus = ssdGenerator(lvlLvlDataColl, this.filePath, CreateSSDFile.REPORT_NAME);
      }
      finally {
        this.dbQueryUtils.deleteTempReport(obj, "SSDwithLabels");
      }
    }
    if (resultStatus == -1) {
      return "Report is not generated";
    }
    return this.filePath + CreateSSDFile.REPORT_NAME + ".ssd";
  }

  /**
   * Method to generate .ssd with dependency
   *
   * @param nodeId - node on which rules are defined
   * @param userName name
   * @param ssdFilePath path
   * @return - path of the .ssd file generated if rules present - returns null if no rules present
   * @throws SSDiCDMInterfaceException Exception
   */
  // SSD-314 to create .ssd with multiple dependency
  public String createSSDFileDependency(final BigDecimal nodeId, final String userName, final String ssdFilePath)
      throws SSDiCDMInterfaceException {
    String obj = this.dbQueryUtils.ssdFileReportGenerationDependency(nodeId, userName);
    int resultStatus = 0;
    if (Objects.nonNull(obj)) {
      try {
        StringBuilder lvlLvlDataColl = new StringBuilder();
        lvlLvlDataColl = getTempSSDDtls(this.dbQueryUtils.getSSDFileData(obj));
        // SSD-263
        if (!this.ruleCheck) {
          return null;
        }
        this.filePath = (null != ssdFilePath) ? ssdFilePath : getAppDataTempFolderPath();
        resultStatus = ssdGenerator(lvlLvlDataColl, this.filePath, CreateSSDFile.REPORT_NAME);
      }
      catch (Exception e) {
        ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
            SSDiCDMInterfaceErrorCodes.GENERAL_EXCEPTION,true);
      }
      finally {
        this.dbQueryUtils.deleteTempReport(obj, "SSDwithLabels");
      }
    }
    if (resultStatus == -1) {
      return "Report is not generated";
    }
    return formatFilePath(this.filePath, CreateSSDFile.REPORT_NAME) + ".ssd";
  }

  /**
   * ssd file storage path
   *
   * @return Path of %TEMP%
   */
  public String getAppDataTempFolderPath() {
    String appDataPath = System.getProperty("java.io.tmpdir");
    File dir = new File(appDataPath);
    if (!dir.exists() || !dir.isDirectory()) {
      dir.mkdirs();
    }
    return appDataPath;
  }

  /**
   * @param lvlData - labels with rules
   * @return string buffer concanting the data to be written in ssd file
   */
  public StringBuilder getTempSSDDtls(final List<Object> lvlData) {
    StringBuilder tempData = new StringBuilder();
    int lvlDataLen = lvlData.size();
    // SSD -263
    this.ruleCheck = false;
    for (int ip = 0; ip < lvlDataLen; ip++) {
      // Alm -610198 Supports Big Rule
      TempSsdFile tempSsdFile = (TempSsdFile) lvlData.get(ip);
      if (tempSsdFile.getAdvanceFormula() != null) {
        tempData.append(tempSsdFile.getAdvanceFormula());
        // checks if usecase present for the rule
        if (Objects.nonNull(tempSsdFile.getAdvanceFormula()) &&
            tempSsdFile.getAdvanceFormula().contains("#usecase Review")) {
          this.ruleCheck = true;
        }
      }

      tempData.append(System.getProperty("line.separator"));
    }
    return tempData;
  }

  /**
   * Writes the data into the ssd file
   *
   * @param ssdTempData - data conatins the rules
   * @param path - path of the ssd file
   * @param reportName - name of the report
   * @return 1 or -1
   * @throws SSDiCDMInterfaceException - exception
   */
  public int ssdGenerator(final StringBuilder ssdTempData, final String path, final String reportName)
      throws SSDiCDMInterfaceException {
    /* Formats the file path with path to store the file and file name */
    String fTempPath = formatFilePath(path, reportName);
    String fPath = fTempPath;
    String reportData = "";
    try (BufferedWriter outFile = new BufferedWriter(new FileWriter(fPath + ".ssd"))) {
      /* creates the ".ssd" file and writes the data into it */
      if (ssdTempData.indexOf("BEGINLINE") >= -1) {
        // CRCRLF replaced by CRLF
        reportData = ssdTempData.toString().replace("BEGINLINE", "").replace("\r\r\n", "\r\n");
      }
      outFile.write(reportData);
      return 1;
    }
    catch (IOException e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.GENERAL_EXCEPTION,true);
    }
  }

  /**
   * Method formats the path
   *
   * @param path - path where ssd file stored
   * @param reportName - name of the report
   * @return- file path
   */
  private String formatFilePath(final String path, final String reportName) {
    String newPath = path;
    int pathLen = newPath.length() - 1;
    Character s = newPath.charAt(pathLen);
    Character a = '\\';
    if (s.compareTo(a) == 0) {
      newPath = path + reportName;
    }
    else {
      newPath = path + DELIMITER + reportName;
    }
    return newPath;
  }
}
