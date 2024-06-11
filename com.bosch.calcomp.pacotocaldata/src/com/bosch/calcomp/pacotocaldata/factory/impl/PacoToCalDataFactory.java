/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.pacotocaldata.factory.impl;

import java.util.Map;

import com.bosch.calcomp.a2lparser.a2l.A2LParser;
import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.calcomp.pacoparser.PacoParser;
import com.bosch.calcomp.pacoparser.exception.PacoParserException;
import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.caldata.CalData;

/**
 * @author SSN9COB
 */
public class PacoToCalDataFactory {

  private static final String LOGGER_PATTERN = "[%-5p] - %d{dd MMM yyyy, HH:mm:ss} - %m%n";

  private static final String DEFAULT_LOG_PATH = "c:/temp/PacoToCalDataFactory.log";

  private ILoggerAdapter logger;


  private A2LFileInfo a2lFileInfo = null;

  /**
   * CDFToCalDataFactory
   *
   * @param logger the logger to be used for the CalDataPhyFactory
   */
  public PacoToCalDataFactory(final ILoggerAdapter logger) {

    super();

    if (logger == null) {
      this.logger = new Log4JLoggerAdapterImpl(DEFAULT_LOG_PATH, LOGGER_PATTERN);
    }
    else {
      this.logger = logger;
    }

  }

  /**
   * Retrieves the CalDataPhy in a hashMap from the a2l file and CDFx file specified as paramters.
   * <p>
   * The hashMap consists the name as 'CalDataPhy name' and value as the 'calDataPhy' instance.
   *
   * @param a2lFile
   * @param cdfxFile
   * @return Map
   * @throws PacoParserException
   * @throws CdfParserException
   */
  // ALP-22 & 24, HEXDCM-9
  public Map<String, CalData> getCalData(final String a2lFile, final String cdfxFile) throws PacoParserException {
    return getCalData(a2lFile, cdfxFile, true);
  }

  /**
   * Retrieves the CalDataPhy in a hashMap from the a2l file and hex file specified as paramters.
   * <p>
   * The hashMap consists the name as 'CalDataPhy name' and value as the 'calDataPhy' instance.
   *
   * @param a2lFile
   * @param pacoFile
   * @param breakInCaseOfErrors
   * @param logger
   * @return Map
   * @throws PacoParserException
   * @throws CdfParserException
   */
  // ALP-22 & 24, HEXDCM-9
  public Map<String, CalData> getCalData(final String a2lFile, final String pacoFile, final boolean breakInCaseOfErrors)
      throws PacoParserException {
    this.a2lFileInfo = parseA2LFile(a2lFile);
    PacoParser pacoParser = new PacoParser(this.logger, pacoFile);
    ClassLoader classLoader = CalDataModelAdapterFactory.class.getClassLoader();
    pacoParser.setTargetModelClassName(CalDataModelAdapterFactory.class.getName());
    pacoParser.setTargetModelClassLoader(classLoader);
    this.logger.info("Invoking paco parser...");
    pacoParser.setFileName(pacoFile);
    Map<String, CalData> calDataMap ;
    calDataMap = pacoParser.parse();


    return calDataMap;
  }

  /**
   * Parse A2L File
   *
   * @param a2lFile
   * @return A2LFileInfo
   */
  // ALP-22 & 24
  private A2LFileInfo parseA2LFile(final String a2lFile) {
    A2LParser a2lParser = new A2LParser(this.logger);
    // HEXPHY-11
    a2lParser.setFileName(a2lFile);
    a2lParser.parse();

    // A2LParser will throw an exception in case of any errors
    // so, it is not necessary to check if there are errors
    // CDL-56
    this.logger.info("analyze time A2L in milli seconds: " + a2lParser.getAnalyzeTime());

    return a2lParser.getA2LFileInfo();
  }

  /**
   * @return the a2lFileInfo
   */
  public A2LFileInfo getA2lFileInfo() {
    return this.a2lFileInfo;
  }
}
