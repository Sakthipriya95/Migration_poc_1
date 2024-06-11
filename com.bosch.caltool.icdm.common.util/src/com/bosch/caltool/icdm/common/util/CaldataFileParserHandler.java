/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.util;


import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.caldatafromhex.CalDataPhyFactory;
import com.bosch.calcomp.cdfparser.Cdfparser;
import com.bosch.calcomp.cdfparser.exception.CdfParserException;
import com.bosch.calcomp.cdftocaldata.factory.impl.CDFCalDataModelAdapterFactory;
import com.bosch.calcomp.pacoparser.PacoParser;
import com.bosch.calcomp.pacoparser.exception.PacoParserException;
import com.bosch.calcomp.parser.dcm.DcmFunction;
import com.bosch.calcomp.parser.dcm.DcmParser;
import com.bosch.calcomp.parser.dcm.exception.DCMParserException;
import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldataphy.CalDataPhy;
import com.bosch.caltool.icdm.common.exception.CaldataFileException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;


/**
 * This class handles the parsing of the caldata files with extns .hex,.paco,.cdf,.dcm
 *
 * @author dmo5cob
 */
public class CaldataFileParserHandler {

  /**
   * @author bne4cob
   */
  public enum CALDATA_FILE_TYPE {
                                 /**
                                  * DCM File
                                  */
                                 DCM(CommonUtilConstants.DCM_EXTN),
                                 /**
                                  * CDF file
                                  */
                                 CDF(CommonUtilConstants.CDF_EXTN),
                                 /**
                                  * CDFx file
                                  */
                                 CDFX(CommonUtilConstants.CDFX_EXTN),
                                 /**
                                  * Intel HEX file
                                  */
                                 HEX(CommonUtilConstants.HEX_EXTN),
                                 /**
                                  * Intel 32 bit HEX file
                                  */
                                 HEX_32(CommonUtilConstants.HEX_32_EXTN),
                                 /**
                                  * Motorola file (S19)
                                  */
                                 HEX_S19(CommonUtilConstants.HEX_S19_EXTN),
                                 /**
                                  * PaCo file
                                  */
                                 PACO(CommonUtilConstants.PACO_EXTN);


    /**
     * File extension
     */
    private String fileExtension;

    CALDATA_FILE_TYPE(final String fileExtn) {
      this.fileExtension = fileExtn;
    }

    /**
     * @return the File extension
     */
    public String getFileExtension() {
      return this.fileExtension;
    }

    /**
     * Get the Cal data file type by file extension
     *
     * @param fileExtn file extension
     * @return file type
     * @throws CaldataFileException if file type cannot be identified
     */
    public static CALDATA_FILE_TYPE getType(final String fileExtn) throws CaldataFileException {

      String fileExtnToVerify = CommonUtils.checkNull(fileExtn);
      fileExtnToVerify = fileExtnToVerify.startsWith(".") ? fileExtn : "." + fileExtn;
      fileExtnToVerify = fileExtnToVerify.toUpperCase(Locale.getDefault());

      for (CALDATA_FILE_TYPE type : CALDATA_FILE_TYPE.values()) {
        if (type.fileExtension.equalsIgnoreCase(fileExtnToVerify)) {
          return type;
        }
      }

      throw new CaldataFileException("Could not identify caldata file type for file extension '" + fileExtn + "'");
    }

    /**
     * @param fileName file name with or without file path
     * @return file type
     * @throws CaldataFileException if file type cannot be identified
     */
    public static CALDATA_FILE_TYPE getTypeFromFileName(final String fileName) throws CaldataFileException {
      String fileExtnsion = FileIOUtil.getFileExtension(fileName);
      CALDATA_FILE_TYPE retType = doGetTypeFromFileNameNoEx(fileExtnsion);
      if (retType == null) {
        throw new CaldataFileException(
            "Could not identify caldata file type '" + fileExtnsion + "'. File name/path : " + fileName);
      }

      return retType;
    }

    /**
     * @param fileName file name with or without file path
     * @return file type, or null if type cannot be identified
     */
    public static CALDATA_FILE_TYPE getTypeFromFileNameNoEx(final String fileName) {
      String fileExtnsion = FileIOUtil.getFileExtension(fileName);
      return doGetTypeFromFileNameNoEx(fileExtnsion);
    }

    /**
     * @param fileExtnsion
     * @return
     */
    private static CALDATA_FILE_TYPE doGetTypeFromFileNameNoEx(final String fileExtnsion) {
      String fileExtnToVerify = '.' + fileExtnsion.toUpperCase(Locale.getDefault());

      CALDATA_FILE_TYPE retType = null;
      for (CALDATA_FILE_TYPE type : CALDATA_FILE_TYPE.values()) {
        if (type.fileExtension.equalsIgnoreCase(fileExtnToVerify)) {
          retType = type;
          break;
        }
      }

      return retType;
    }
  }

  /**
   * Returned by hex, cdf and dcm parsers during parsing. Map of key - label name, Value - List of warnings
   */
  private final Map<String, List<String>> warningsMap = new HashMap<>();

  /**
   * String constant for error while parsing the cdf file
   */
  private static final String ERROR_WHILE_PARSING_THE_CDF_FILE = "Error while parsing the CDF file: ";

  /**
   * String constant for error while parsing the paco file
   */
  private static final String ERROR_WHILE_PARSING_THE_PACO_FILE = "Error while parsing the Paco file: ";

  /**
  *
  */
  private static final String BYTE_COULD_NOT_BE_READ = "byte could not be read at address";

  /**
   * This error message is thrown from Hex parser when a2l file is a mismatch
   */
  private static final String NUMBER_OF_AXIS_POINTS = "Number of axis points is";

  private static final String A2L_HEX_MISMATCH = ". Mismatch of A2L and HEX.";

  /**
   * Parser Logger
   */
  private final ILoggerAdapter parserLogger;

  /**
   * A2L file object, for Hex file parsing
   */
  private final A2LFileInfo a2lFileInfo;

  /**
   * @param parserLogger logger instance
   * @param a2lFileInfo A2l File contents
   */
  public CaldataFileParserHandler(final ILoggerAdapter parserLogger, final A2LFileInfo a2lFileInfo) {
    this.parserLogger = parserLogger;
    this.a2lFileInfo = a2lFileInfo;
  }


  /**
   * Parse the caldata file to java model
   *
   * @param filePath File Path
   * @return Map of CalData objects, key as parameter name
   * @throws IcdmException any exception while reading/parsing the file or if file type cannot be identified
   */
  public Map<String, CalData> getCalDataObjects(final String filePath) throws IcdmException {
    this.parserLogger.info("CalDataFile parsing started for file input. File : {}", filePath);

    CALDATA_FILE_TYPE fileType = CALDATA_FILE_TYPE.getTypeFromFileName(filePath);
    this.parserLogger.debug("CalData File type : {}", fileType);

    return getCalDataObjects(fileType, null, filePath);
  }

  /**
   * Parse the caldata file to java model from input stream
   *
   * @param fileType CalData file type
   * @param inputStream input stream
   * @return Map of CalData. key - parameter name
   * @throws IcdmException any parsing errors or if file type cannot be identified
   */
  public Map<String, CalData> getCalDataObjects(final CALDATA_FILE_TYPE fileType, final InputStream inputStream)
      throws IcdmException {

    this.parserLogger.info("CalDataFile parsing started for stream input. File type : {}", fileType);
    return getCalDataObjects(fileType, inputStream, null);
  }

  /**
   * Parse the caldata file to java model from input stream
   *
   * @param fileType CalData file type
   * @param inputStream input stream
   * @return Map of CalData. key - parameter name
   * @throws CaldataFileException any parsing errors or if file type cannot be identified
   */
  private Map<String, CalData> getCalDataObjects(final CALDATA_FILE_TYPE fileType, final InputStream inputStream,
      final String filePath)
      throws IcdmException {

    ConcurrentMap<String, CalData> calDataObjMap;

    this.parserLogger.debug("CalData File type is : {}", fileType);

    switch (fileType) {
      case DCM:
        calDataObjMap = invokeDcmParser(inputStream, filePath);
        break;
      case CDF:
      case CDFX:
        calDataObjMap = invokeCdfParser(inputStream, filePath);
        break;
      case HEX:
      case HEX_32:
      case HEX_S19:
        calDataObjMap = invokeHexToPhy(fileType, inputStream, filePath);
        break;
      case PACO:
        calDataObjMap = invokePacoParser(inputStream, filePath);
        break;
      default:
        throw new CaldataFileException("Unsupported Cal data File type : " + fileType);
    }

    this.parserLogger.info("CalDataFile parsing completed. Data count = {}", calDataObjMap.size());

    if (calDataObjMap.isEmpty()) {
      this.parserLogger.warn("No parameters found in the input file !");
    }

    return calDataObjMap;
  }

  /**
   * Invoke dcm parser
   *
   * @param inputStream
   * @param dcmFile file path
   * @return Map of CalData objects, key as parameter name
   * @throws CaldataFileException
   */
  private ConcurrentMap<String, CalData> invokeDcmParser(final InputStream inputStream, final String dcmFile)
      throws CaldataFileException {

    this.parserLogger.debug("Invoking DCM Parser...");

    DcmParser dcmParser = new DcmParser(this.parserLogger);

    try {
      if (inputStream == null) {
        dcmParser.setFileName(dcmFile);
      }
      else {
        dcmParser.setFileStream(inputStream);
      }

      // parse the file
      dcmParser.parse();

      this.parserLogger.debug("Parsing of DCM file completed.");
    }
    catch (DCMParserException exp) {
      throw new CaldataFileException("Error while parsing the DCM file: " + exp.getMessage(), exp);
    }

    return getDcmDataMap(dcmParser);
  }

  /**
   * @param dcmParser
   * @return
   */
  private ConcurrentMap<String, CalData> getDcmDataMap(final DcmParser dcmParser) {
    // get the results
    ConcurrentMap<String, CalData> calDataObjects = new ConcurrentHashMap<>(dcmParser.getCalDataMap());

    // display some info
    if (this.parserLogger.getLogLevel() == ILoggerAdapter.LEVEL_DEBUG) {
      Map<String, CalDataPhy> calDataPhyObjects = dcmParser.getCalDataPhyMap();
      Map<String, DcmFunction> functionMap = dcmParser.getFuncMap();

      // set warnings map
      setWarningsMap(dcmParser.getWarningsMap());

      this.parserLogger.debug("{} characteristics in CalDataPhyMap", calDataPhyObjects.size());
      this.parserLogger.debug("{} characteristics in CalDataMap", calDataObjects.size());
      this.parserLogger.debug("{} functions in FunctionMap", functionMap.size());
      this.parserLogger.debug("runtime: {}", dcmParser.getUsedTime());
    }

    return calDataObjects;
  }


  /**
   * @param inputStream
   * @return
   * @throws CaldataFileException
   */
  private ConcurrentMap<String, CalData> invokePacoParser(final InputStream inputStream, final String pacoFilePath)
      throws CaldataFileException {

    PacoParser pacoParser = new PacoParser(this.parserLogger);
    pacoParser.setValidationOff(false);

    ClassLoader classLoader =
        com.bosch.calcomp.pacotocaldata.factory.impl.CalDataModelAdapterFactory.class.getClassLoader();
    pacoParser.setTargetModelClassName(
        com.bosch.calcomp.pacotocaldata.factory.impl.CalDataModelAdapterFactory.class.getName());
    pacoParser.setTargetModelClassLoader(classLoader);

    try {

      if (inputStream == null) {
        pacoParser.setFileName(pacoFilePath);
      }
      else {
        pacoParser.setFileStream(inputStream);
      }

      this.parserLogger.debug("Invoking paco parser...");

      @SuppressWarnings("unchecked")
      ConcurrentMap<String, CalData> pacoCalDataObjects = new ConcurrentHashMap<>(pacoParser.parse());

      this.parserLogger.debug("Parsing of paco file complete !");

      return pacoCalDataObjects;
    }
    catch (PacoParserException exp) {
      throw new CaldataFileException(ERROR_WHILE_PARSING_THE_PACO_FILE + exp.getMessage(), exp);
    }
  }

  /**
   * Parse Hex file using hex file parser
   *
   * @param hexFileType hex file type
   * @param inputStream
   * @param hexFilePath hex file path
   * @return Map of CalData objects, key as parameter name
   */
  private ConcurrentMap<String, CalData> invokeHexToPhy(final CALDATA_FILE_TYPE hexFileType,
      final InputStream inputStream, final String hexFilePath)
      throws IcdmException {

    String hexFilePathToUse = hexFilePath;

    // ICDM-631
    CalDataPhyFactory calDataFactory = new CalDataPhyFactory(this.parserLogger);
    if (inputStream != null) {
      calDataFactory.setInputStream(inputStream);
      calDataFactory.sethexFileType(hexFileType.getFileExtension().toLowerCase(Locale.getDefault()));
      // When input stream is given, file path to be given as null
      hexFilePathToUse = null;
    }

    ConcurrentMap<String, CalData> calDataObjsMap;
    try {
      calDataObjsMap = new ConcurrentHashMap<>(calDataFactory.getCalData(this.a2lFileInfo, hexFilePathToUse));
    }
    catch (Exception exp) {
      StringBuilder newMsg = new StringBuilder();

      String originalMsg = exp.getMessage();
      newMsg.append("HEX file Read Error : ").append(originalMsg);
      if (originalMsg.contains(BYTE_COULD_NOT_BE_READ) || originalMsg.contains(NUMBER_OF_AXIS_POINTS)) {
        newMsg.append(A2L_HEX_MISMATCH);
      }
      throw new InvalidInputException(newMsg.toString(), exp);
    }

    this.parserLogger.debug("{} characteristics found in hex file", calDataObjsMap.size());

    // calculate all checksums (validation for CALM-19)
    for (CalData calDataPhy : calDataObjsMap.values()) {
      calDataPhy.getCalDataPhy().getChecksum();
    }

    // set warnings map
    setWarningsMap(calDataFactory.getWarningsMap());

    return calDataObjsMap;
  }


  /**
   * Warnings created by parsers during parsing.
   *
   * @return the warningsMap Map of key - label name, Value - List of warnings
   */
  public Map<String, List<String>> getWarningsMap() {
    return this.warningsMap;
  }


  /**
   * @param warningsMap the warningsMap to set
   */
  private void setWarningsMap(final Map<String, List<String>> warningsMap) {
    this.warningsMap.putAll(warningsMap);
    if (this.warningsMap.isEmpty()) {
      this.parserLogger.info("No of labels with warnings : 0");
    }
    else {
      this.parserLogger.warn("No of labels with warnings : " + this.warningsMap.size());
    }
  }

  /**
   * Parse Cdf file using Cdf file parser
   *
   * @param inputStream
   * @param cdfFilePath File Path
   * @return Map of CalData objects, key as parameter name
   * @throws CaldataFileException Error while parsing the CDF file
   */
  private ConcurrentMap<String, CalData> invokeCdfParser(final InputStream inputStream, final String cdfFilePath)
      throws CaldataFileException {

    Cdfparser cdfParser = createCdfxParser();

    try {

      if (inputStream == null) {
        cdfParser.setFileName(cdfFilePath);
      }
      else {
        cdfParser.setFileStream(inputStream);
      }

      @SuppressWarnings("unchecked")
      ConcurrentMap<String, CalData> cdfCalDataObjects = new ConcurrentHashMap<>(cdfParser.parse());

      // set warnings map
      setWarningsMap(cdfParser.getWarningsMap());

      this.parserLogger.debug("Parsing of CDFx file completed !");
      return cdfCalDataObjects;
    }
    catch (CdfParserException exp) {
      throw new CaldataFileException(ERROR_WHILE_PARSING_THE_CDF_FILE + exp.getMessage(), exp);
    }
  }

  /**
   * @return the Cdfx parser
   */
  private Cdfparser createCdfxParser() {
    Cdfparser cdfParser = new Cdfparser(this.parserLogger);
    cdfParser.setValidationOff(false);

    ClassLoader classLoader = CDFCalDataModelAdapterFactory.class.getClassLoader();
    Cdfparser.setTargetModelClassName(CDFCalDataModelAdapterFactory.class.getName());
    Cdfparser.setTargetModelClassLoader(classLoader);

    this.parserLogger.debug("Invoking CDFx Parser...");

    return cdfParser;
  }

}
