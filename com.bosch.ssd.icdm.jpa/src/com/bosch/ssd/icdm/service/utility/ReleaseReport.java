/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.service.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.parser.dcm.DcmParser;
import com.bosch.calcomp.parser.dcm.exception.DCMParserException;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldata.element.DataElement;
import com.bosch.calmodel.caldata.history.CalDataHistory;
import com.bosch.calmodel.caldata.history.HistoryEntry;
import com.bosch.caltool.cdfwriter.CDFWriter;
import com.bosch.caltool.cdfwriter.exception.CDFWriterException;
import com.bosch.ssd.icdm.entity.TempCdfdatalist;
import com.bosch.ssd.icdm.entity.TempDcmdatalist;
import com.bosch.ssd.icdm.entity.TempReleasesErrorLbldtl;
import com.bosch.ssd.icdm.exception.ExceptionUtils;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException.SSDiCDMInterfaceErrorCodes;
import com.bosch.ssd.icdm.logger.SSDiCDMInterfaceLogger;
import com.bosch.ssd.icdm.model.ReleaseErrorModel;

/**
 * Revision History<br>
 * Version Date Name Description<br>
 * 3.2.0 04-08-2014 Renuka SSD-272 generate .ssd and .cdfx files 3.2.0 06-08-2014 Renuka SSD-277 errors in the release
 * 3.2.0 21.08.2014 Renuka SSD-277 change in input send proRevId and proRelId
 */
/**
 * @author gue1cob - This class creates .ssd and .cdfx reports for the given release id
 */
public class ReleaseReport {

  /**
   *
   */
  private static final String LINE_SEPARATOR = "line.separator";
  private final DBQueryUtils dbQueryUtils;

  /**
   * path whether file is generated
   */
  String filePath;
  private List<ReleaseErrorModel> errorRuleList;

  private long noOfParams = 0;
  /**
   * caldata map for cdfx file generation
   */
  private Map<String, CalData> calDataMap;

  /**
   * Constructor
   *
   * @param queryUtils service
   */
  public ReleaseReport(final DBQueryUtils queryUtils) {
    this.dbQueryUtils = queryUtils;
  }

  /**
   * @return the noOfParams
   */
  public long getNoOfParams() {
    return this.noOfParams;
  }


  /**
   * @return the calDataMap
   */
  public Map<String, CalData> getCalDataMap() {
    return this.calDataMap;
  }

  /**
   * Method generates the .ssd file for a release id
   *
   * @param releaseId     - id of the release
   * @param path          - path for the reports
   * @param reportName    - report Name
   * @param ruleIdFlag    rule id
   * @param createSSDFile ssdfile
   * @return - path of the ssd file generated
   * @throws SSDiCDMInterfaceException exception
   */
  public String generateSSDFile(final BigDecimal releaseId, final String path, final String reportName,
      final boolean ruleIdFlag, final CreateSSDFile createSSDFile) throws SSDiCDMInterfaceException {
    // calls the procedure to get the data based on id
    String obj = this.dbQueryUtils.dataForSSD(releaseId, ruleIdFlag);
    if (Objects.nonNull(obj)) {
      // fetch the data from temp tables
      StringBuilder lvlLvlDataColl = createSSDFile.getTempSSDDtls(this.dbQueryUtils.getSSDFileData(obj));
      this.filePath = path;
      // creates .ssd file
      createSSDFile.ssdGenerator(lvlLvlDataColl, this.filePath, reportName);

      // deletes the temp data
      this.dbQueryUtils.deleteTempReport(obj, "SSDwithLabels");

      return this.filePath + reportName + ".ssd";
    }
    // TODO check db procedure method for release id invalid scenario
    throw ExceptionUtils.createAndThrowException(null, "Invalid Release Id is given",
        SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);

  }

  /**
   * Method generates the .cdfx file for a release id
   *
   * @param releaseId  - id of the release
   * @param path       - path for the reports
   * @param reportName - report Name
   * @param userName   UserName
   * @return - path of the ssd file generated
   * @throws SSDiCDMInterfaceException exception
   */
  public String generateCDFxFile(final BigDecimal releaseId, final String path, final String reportName,
      final String userName) throws SSDiCDMInterfaceException {
    String obj = this.dbQueryUtils.dataForCdfx(releaseId);
    if (Objects.nonNull(obj)) {

      // gets the dcm data
      StringBuilder lvlLblData = getDcmListDtls(this.dbQueryUtils.getDcmListDtls(obj));
      // check for the %temp% path to store the files
      this.filePath = path;
      // stores the data in temp table
      List<Object> stateList = this.dbQueryUtils.getCdfListDtls(obj);
      // creates the dcm file
      boolean dcmFileCreated = dcmCDFGenerator(lvlLblData, this.filePath + "test", ".dcm");
      if (dcmFileCreated) {
        // generates the cdfx file using the dcm file
        cdfFileGenerator(this.filePath + "test.dcm", stateList, this.filePath, obj, reportName, userName);

      }
  
      this.dbQueryUtils.deleteTempReport(obj, "CdfDataReport");
   
    }
    return this.filePath + reportName + ".cdfx";

  }

  /**
   * @param lvlData - list of dcm data
   * @return - string contains the dcm data appended
   */
  public StringBuilder getDcmListDtls(final List<Object> lvlData) {
    // Iterates the List elements and store the data in StringBuilder, mapping every data to their heading
    StringBuilder sbData = new StringBuilder();
    sbData.append(System.getProperty(LINE_SEPARATOR));
    sbData.append("KONSERVIERUNG_FORMAT 2.0");
    sbData.append(System.getProperty(LINE_SEPARATOR));
    sbData.append(System.getProperty(LINE_SEPARATOR));
    int lvlDataLen = lvlData.size();
    for (int ip = 0; ip < lvlDataLen; ip++) {
      TempDcmdatalist dcmDtls = (TempDcmdatalist) lvlData.get(ip);
      if (dcmDtls.getDataDesc() == null) {
        dcmDtls.setDataDesc(" ");
      }
      else {
        sbData.append(dcmDtls.getDataDesc());
      }
      sbData.append(System.getProperty(LINE_SEPARATOR));
    }
    return sbData;
  }

  /**
   * Method creates the dcm file
   *
   * @param lblData     - string buffer stores the label details of the node
   * @param filePathCdf - path of the cdfx files
   * @param format      - format
   * @return - true or false
   * @throws SSDiCDMInterfaceException exception
   */
  public boolean dcmCDFGenerator(final StringBuilder lblData, final String filePathCdf, final String format)
      throws SSDiCDMInterfaceException {
    if (filePathCdf == null) {
      return false;
    }
    try (BufferedWriter outFile = new BufferedWriter(new FileWriter(filePathCdf + format))) {
      // creates the ".dcm" file and writes the data into it
      outFile.write(lblData.toString());
      return true;
    }
    catch (IOException e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.GENERAL_EXCEPTION,true);
    }
  }

  /**
   * To generate CDF file using the given inout.
   *
   * @param path       - path where temp dcm file has been generated
   * @param stateList  - state and comment details for a label from db
   * @param cdfPath    - where cdf file has to be generated
   * @param uniId      - unique identifier generated from report generatio module (db identification)
   * @param reportName - report Name
   * @param userName   userName
   * @throws SSDiCDMInterfaceException exception
   */
  public void cdfFileGenerator(final String path, final List<Object> stateList, final String cdfPath,
      final String uniId, final String reportName, final String userName) throws SSDiCDMInterfaceException {

    // String userName = SSDService.getInstance().getUserName(); // get ths icdm username
    try {
      // cdm log file created under app data folder (as like ssd log)
      DcmParser dcmParser = new DcmParser(SSDiCDMInterfaceLogger.getLogger());

      dcmParser.parse(path);
      // caldata objects retrieved from dcm parser using the temp dcm file generated
      this.calDataMap = dcmParser.getCalDataMap();
      this.noOfParams = this.calDataMap.size();
      CDFWriter cdfWriter;

      // cdf writer to be initialized with the filename where cdf has to be generated
      cdfWriter = new CDFWriter(this.calDataMap, cdfPath + reportName + ".cdfx", SSDiCDMInterfaceLogger.getLogger());

      // cdf log file created under app data folder (as like ssd log)
      // CDFWriterLogger.setLogFile(CreateSSDFile.getInstance().getAppDataTempFolderPath() + "\\cdfw_1_0_0.log")


      // loop through available state of labels and add the same to caldata objects
      for (Object obj : stateList) {
        TempCdfdatalist cdfState = (TempCdfdatalist) obj;
        CalData calData = this.calDataMap.get(cdfState.getLabel());

        if (calData != null) {
          // state and comment to be added to cs-entry flag
          CalDataHistory calDataHistory = new CalDataHistory();
          List<HistoryEntry> historyEntryList = new ArrayList<>();
          HistoryEntry historyEntry = new HistoryEntry();
          // state addition
          DataElement dataElement = new DataElement();
          dataElement.setValue(cdfState.getState());
          historyEntry.setState(dataElement);
          // date to be added
          dataElement = new DataElement();
          dataElement.setValue(new Date().toString());
          historyEntry.setDate(dataElement);

          // performed by CSDI tag to be added
          dataElement = new DataElement();
          dataElement.setValue("SSD DATABASE");
          historyEntry.setDataIdentifier(dataElement);

          // performed by cswp tag to be added
          dataElement = new DataElement();
          dataElement.setValue("Value from SSD DATABASE");
          historyEntry.setContext(dataElement);

          // performed by csus tag to be added
          dataElement = new DataElement();
          dataElement.setValue(userName);
          historyEntry.setPerformedBy(dataElement);

          // performed by cstv tag to be added
          dataElement = new DataElement();
          dataElement.setValue(cdfState.getFeVal());
          historyEntry.setTargetVariant(dataElement);

          // performed by csto tag to be added
          dataElement = new DataElement();
          dataElement.setValue(cdfState.getUsecase());
          historyEntry.setTestObject(dataElement);

          // comment to be added
          dataElement = new DataElement();
          // dataElement.setValue(cdfState.getComments())
          dataElement.setValue(getCommentsFromTempRecord(cdfState));
          historyEntry.setRemark(dataElement);
          historyEntryList.add(historyEntry);
          calDataHistory.setHistoryEntryList(historyEntryList);
          calData.setCalDataHistory(calDataHistory);
        }
        else {
          SSDiCDMInterfaceLogger.logMessage("label not found in dcm " + cdfState.getLabel(), ILoggerAdapter.LEVEL_INFO,
              null);
        }
      }

      // cdf file to be written (using the caldata objects passed in the constructor)
      cdfWriter.writeCalDataToXML();

      // temporary dcm file has to be deleted
      File file = new File(path);
      boolean deleted = file.delete();
      if (deleted) {
        SSDiCDMInterfaceLogger.logMessage("Temp DCM File Deleted", ILoggerAdapter.LEVEL_DEBUG, null);
      }
      SSDiCDMInterfaceLogger.logMessage("End of File Creation", ILoggerAdapter.LEVEL_INFO, null);
    }
    catch (DCMParserException | CDFWriterException e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.GENERAL_EXCEPTION,true);
    }

  }

  /**
   * Method gets the comments for data description, maturity,usecase,feature-value
   *
   * @param cdfState - data from the temp table
   * @return - comments
   */
  private String getCommentsFromTempRecord(final TempCdfdatalist cdfState) {

    String tempComment = "SSD Maturity Level   :" + cdfState.getMaturity() + "\n" + "SSD Use Case         :" +
        cdfState.getUsecase() + "\n" + "Rule Defined At      :" + cdfState.getNodeInf() + "\n" +
        "For Feature-Value    :" + cdfState.getFeVal();

    if (cdfState.getTempDataDesc() != null) {
      tempComment = tempComment + "\n" + "Data Desc in SSD rule  :" + cdfState.getTempDataDesc();
    }
    else {
      tempComment = tempComment + "\n" + "Data Desc in SSD rule  :";
    }

    return tempComment;
  }

  /**
   * Method used to get the error details for the release
   *
   * @param proRevId  - revision id
   * @param proRelId  - release id
   * @param ssdNodeId node id
   * @return - list of {@link ReleaseErrorModel} - error details
   * @throws SSDiCDMInterfaceException Exception
   */
  public List<ReleaseErrorModel> generateErrorFile(final BigDecimal proRevId, final BigDecimal proRelId,
      final BigDecimal ssdNodeId) throws SSDiCDMInterfaceException { // SSD-277
    String obj = this.dbQueryUtils.dataForError(proRevId, proRelId, ssdNodeId);
    if (Objects.nonNull(obj)) {

      this.errorRuleList = new ArrayList<>();
      // Executes the JPA entity to select the required data and stores it in a collection of maps
      this.errorRuleList = getErrorLblDtls(this.dbQueryUtils.getTempRelErrorLblData(obj), this.errorRuleList);


      // deletes the temp data

      this.dbQueryUtils.deleteTempReport(obj, "ReleasesErrorReport");


    }
    return new ArrayList<>(this.errorRuleList);

  }

  /**
   * Method sets the error data into the model
   *
   * @param lblData   - list of error data stored in temp tables
   * @param model     - {@link ReleaseErrorModel} - contains error data
   * @param errorList - list of errors
   * @return - list containing error information
   */
  private List<ReleaseErrorModel> getErrorLblDtls(final List<Object> lblData, final List<ReleaseErrorModel> errorList) {
    ReleaseErrorModel model;
    int lvlDataLen = lblData.size();
    SSDiCDMInterfaceLogger.logMessage("no of errors " + lvlDataLen, ILoggerAdapter.LEVEL_INFO, null);
    for (int ip = 0; ip < lvlDataLen; ip++) {
      model = new ReleaseErrorModel();
      TempReleasesErrorLbldtl errorLblDtl = (TempReleasesErrorLbldtl) lblData.get(ip);
      model.setErrorDescription(errorLblDtl.getGridErrTest());
      model.setLabel(errorLblDtl.getGridLabelName());
      model.setErrorNo(errorLblDtl.getGridErrNumber());
      model.setNodeScope(errorLblDtl.getGridScope());
      errorList.add(model);
    }
    return errorList;
  }

}
