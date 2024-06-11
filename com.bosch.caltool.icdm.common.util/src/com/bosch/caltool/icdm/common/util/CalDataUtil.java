/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.parser.dcm.DcmParser;
import com.bosch.calcomp.parser.dcm.exception.DCMParserException;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldata.element.DataElement;
import com.bosch.calmodel.caldata.history.CalDataHistory;
import com.bosch.calmodel.caldata.history.HistoryEntry;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.logger.ParserLogger;


/**
 * Utility class for CalData related converion,usages
 */
public final class CalDataUtil {

  /**
   * Key for zipped cal data byte array
   */
  // ICDM-2069
  public static final String KEY_CALDATA_ZIP = "D";

  /**
   * initial size of string builder
   */
  private static final int STRING_BUILDER_SIZE_INITIAL = 40;
  /**
   * Line seperator
   */
  private static final String LINE_SEPERATOR = "\n";


  /**
   * Private constructor
   */
  private CalDataUtil() {
    // Utility classes, which are a collection of static members, are not meant to be instantiated. Even abstract
    // utility classes, which can be extended, should not have public constructors.
  }

  /**
   * Converts DCM String to CalData object
   *
   * @param dcmStr DCM String
   * @param paramName Parameter name
   * @param logger Logger
   * @return CalData
   */
  public static CalData dcmToCalData(final String dcmStr, final String paramName, final ILoggerAdapter logger) {
    if (dcmStr != null) {
      final DcmParser parser = new DcmParser(ParserLogger.getInstance());
      try {
        parser.setFileContent(dcmStr);
        parser.parse();
        return parser.getCalDataMap().get(paramName);
      }
      catch (DCMParserException exp) {
        logger.error("Error parsing DCM String for parameter '" + paramName + "'. " + exp.getMessage(), exp);
      }
    }
    return null;
  }

  /**
   * Converts DCM String to CalData object, with varcode handling
   *
   * @param dcmStr DCM String
   * @param paramName Parameter name
   * @param logger Logger to use
   * @return CalData
   */
  public static CalData dcmToCalDataExt(final String dcmStr, final String paramName, final ILoggerAdapter logger) {
    if (dcmStr == null) {
      return null;
    }

    Map<String, CalData> parsedDataMap = null;
    final DcmParser parser = new DcmParser(ParserLogger.getInstance());
    try {
      parser.parse(dcmStr);
      parsedDataMap = parser.getCalDataMap();
    }
    catch (DCMParserException e) {
      logger.error("Error parsing DCM string for parameter : " + paramName + " - " + e.getMessage(), e);
    }

    CalData retCalData = null;
    // SSD-337
    if (parsedDataMap != null) {
      Optional<CalData> optCalData = parsedDataMap.values().stream().findFirst();

      if (optCalData.isPresent()) {
        retCalData = optCalData.get();

        // To ensure if the CalData obj name matches parameter name that is passed. If not replace Caldata Obj name with
        // parameter name to support variant code
        if (!retCalData.getShortName().equalsIgnoreCase(paramName)) {

          String paramFullName = paramName;
          // added to handle DCM for variant coded classs -- SSD-382
          String p = paramFullName.substring(paramFullName.indexOf('['), paramFullName.indexOf(']') + 1);
          paramFullName = paramFullName.replace(p, "");
          if (paramFullName.contains(retCalData.getShortName())) {
            retCalData.setShortName(paramName);
            retCalData.getCalDataPhy().setName(paramName);
          }
        }
      }

    }

    return retCalData;
  }

  /**
   * Creates the DCM String FESTWERT based on the parameter passed.
   *
   * @param paramName - label name
   * @param unit - unit of the label (EINHEIT_W)
   * @param value - value of label (WERT)
   * @return DCM String
   */
  public static String createDCMStringForNumber(final String paramName, final String unit, final String value) {
    final StringBuilder dcm = new StringBuilder(STRING_BUILDER_SIZE_INITIAL);

    dcm.append("FESTWERT ").append(CommonUtils.checkNull(paramName)).append(LINE_SEPERATOR).append(" EINHEIT_W \"")
        .append(CommonUtils.checkNull(unit)).append('\"').append(LINE_SEPERATOR).append(" WERT ")
        .append(CommonUtils.checkNull(value)).append(LINE_SEPERATOR).append("END").append(LINE_SEPERATOR);

    return dcm.toString();
  }

  /**
   * Creates the DCM String FESTWERT based on the parameter passed.
   *
   * @param paramName - label name
   * @param unit - unit of the label (EINHEIT_W)
   * @param value - value of label (TEXT)
   * @return DCM String
   */
  public static String createDCMStringForText(final String paramName, final String unit, final String value) {
    final StringBuilder dcm = new StringBuilder(STRING_BUILDER_SIZE_INITIAL);
    dcm.append("FESTWERT ").append(CommonUtils.checkNull(paramName)).append(LINE_SEPERATOR).append(" EINHEIT_W \"")
        .append(CommonUtils.checkNull(unit)).append('\"').append(LINE_SEPERATOR).append(" TEXT \"")
        .append(CommonUtils.checkNull(value)).append('\"').append(LINE_SEPERATOR).append("END").append(LINE_SEPERATOR);

    return dcm.toString();
  }

  /**
   * iCDM-1126, supporting changes <br>
   * Method to create a copy of calData along with CalDataHistory information
   *
   * @param calData calData to be copied
   * @param logger the cdm logger to log the error message
   * @return CalData copy
   */
  public static CalData createCopy(final CalData calData, final ILoggerAdapter logger) {
    if (calData == null) {
      return null;
    }
    // iCDM-2071
    CalData calDataCopy = null;
    try {
      calDataCopy = calData.clone();
    }
    catch (CloneNotSupportedException exp) {
      logger.error("Exception occured while creating CalData Copy With History " + exp.getMessage(), exp);
    }
    // return copy
    return calDataCopy;
  }

  // iCDM-1126, supporting changes
  /**
   * Method to create a copy of calData WITHOUT CalDataHistory information
   *
   * @param calData to be copied
   * @param logger the given logger
   * @return CalData copy
   */
  public static CalData createCopyWithoutHistory(final CalData calData, final ILoggerAdapter logger) {
    if (calData == null) {
      return null;
    }
    // iCDM-2071
    CalData calDataCopy = null;
    try {
      calDataCopy = calData.clone();
      calDataCopy.setCalDataHistory(null);
    }
    catch (CloneNotSupportedException exp) {
      logger.error("Exception occured while creating CalData Copy Without History " + exp.getMessage(), exp);
    }
    return calDataCopy;
  }

  // ICDM-1300
  /**
   * Returns the maturity level for the caldata obj
   *
   * @param calData calData
   * @return the status
   */
  public static String getStatus(final CalData calData) {
    if (calData != null) {
      HistoryEntry latestHistoryEntry = getLatestHist(calData);
      if (latestHistoryEntry != null) {
        DataElement stateElement = latestHistoryEntry.getState();
        if (stateElement != null) {
          return stateElement.getValue();
        }
      }
    }
    return "";
  }

  /**
   * Returns the latest history element
   *
   * @param calData calData
   * @return history element
   */
  public static HistoryEntry getLatestHist(final CalData calData) {
    CalDataHistory calDataHistory = calData.getCalDataHistory();
    if ((calDataHistory != null) && (calDataHistory.getHistoryEntryList() != null) &&
        !calDataHistory.getHistoryEntryList().isEmpty()) {
      // Last Element from the HistoryEntryList is considered as the one with latest timestamp
      return calDataHistory.getHistoryEntryList().get(calDataHistory.getHistoryEntryList().size() - 1);
    }
    return null;
  }

  /**
   * Returns the latest hint of the caldata
   *
   * @param calData caldata
   * @return hint
   */
  public static String getRemarks(final CalData calData) {
    HistoryEntry latestHist = CalDataUtil.getLatestHist(calData);
    if ((latestHist != null) && (latestHist.getRemark() != null)) {
      return latestHist.getRemark().getValue();
    }
    return "";
  }

  /**
   * Returns the unit of caldata
   *
   * @param calData caldata object
   * @return unit as string
   */
  public static String getUnit(final CalData calData) {
    return CommonUtils.checkNull(calData.getCalDataPhy(), "", calData.getCalDataPhy().getUnit());
  }

  /**
   * Convert byte array to Caldata object
   *
   * @param data calData Bytes
   * @return caldata object
   * @throws IOException IOException
   * @throws ClassNotFoundException ClassNotFoundException
   */
  // ICDM-1723
  public static CalData getCalDataObj(final byte[] data) throws IOException, ClassNotFoundException {
    byte[] calDataPhyBytes = data == null ? null : data.clone();
    if ((calDataPhyBytes != null) && (calDataPhyBytes.length > 0)) {
      // ICDM-2069
      if (ZipUtils.isZippedData(calDataPhyBytes)) {
        Map<String, byte[]> map = ZipUtils.unzip(calDataPhyBytes);
        calDataPhyBytes = map.get(KEY_CALDATA_ZIP);
      }

      try (ByteArrayInputStream byteInpStream = new ByteArrayInputStream(calDataPhyBytes);
          ObjectInputStream objInpStream = new ObjectInputStream(byteInpStream);) {

        return (CalData) objInpStream.readObject();

      }

    }
    return null;
  }

  /**
   * Convert byte array to Caldata object. Throws IcdmException
   *
   * @param data calData Bytes
   * @return caldata object
   * @throws IcdmException when input cannot be converted
   */
  public static CalData getCalDataObjIEx(final byte[] data) throws IcdmException {
    try {
      return getCalDataObj(data);
    }
    catch (ClassNotFoundException | IOException e) {
      throw new IcdmException(e.getMessage(), e);
    }
  }

  /**
   * Method to convert byte array to CaldataPhy object
   *
   * @param dbData dbData
   * @return actual review output
   */
  public static CalData getCDPObj(final byte[] dbData) {
    try {
      return CalDataUtil.getCalDataObj(dbData);
    }
    catch (ClassNotFoundException | IOException e) {
      CDMLogger.getInstance().error("Error reading CalDataPhy object for parameter : ", e);
    }
    return null;

  }

  /**
   * Convert the caldata object to a zipped byte array
   *
   * @param data caldata object
   * @param logger logger to use
   * @return zipped byte array
   */

  // ICDM-2069
  public static byte[] convertCalDataToZippedByteArr(final CalData data, final ILoggerAdapter logger) {
    if (data == null) {
      return null;
    }

    try (ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream outputStm = new ObjectOutputStream(out);) {

      outputStm.writeObject(data);
      ConcurrentMap<String, byte[]> dataMap = new ConcurrentHashMap<>();
      dataMap.put(KEY_CALDATA_ZIP, out.toByteArray());
      return ZipUtils.createZip(dataMap);

    }
    catch (IOException exp) {
      logger.error("Exception occured while creating byte array for caldata obj" + exp.getMessage(), exp);
    }

    return null;
  }

}
