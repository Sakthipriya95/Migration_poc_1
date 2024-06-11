/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.testcommon.caldata;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.calcomp.a2lparser.a2l.A2LParser;
import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.caltool.testframe.exception.TestDataReaderException;


/**
 * @author dmo5cob
 */
public class A2lFileReader {

  /**
   * A2L Logger
   */
  private final ILoggerAdapter logger;

  /**
   * @param logger ILoggerAdapter
   */
  public A2lFileReader(final ILoggerAdapter logger) {
    this.logger = logger;
  }

  /**
   * @param tdFiles test data files
   * @return map of entities
   * @throws TestDataReaderException for error while reading a2l file
   */
  public Map<String, A2LFileInfo> readA2lFiles(final String... tdFiles) throws TestDataReaderException {
    this.logger.info("A2lFileReader : Reading files, number of files to read - " + tdFiles.length);
    ConcurrentMap<String, A2LFileInfo> a2lInfoMap = new ConcurrentHashMap<>();
    for (String testDataFile : tdFiles) {
      A2LFileInfo a2lFileInfo = getA2LModel(testDataFile);
      a2lInfoMap.put(a2lFileInfo.getFileName(), a2lFileInfo);
    }
    this.logger.info("A2lFileReader : Successfully read a2l file(s)");

    return a2lInfoMap;
  }


  /**
   * Read the test data from the given input file. Returns the list of <code>Data</code> objects
   *
   * @param a2lFile input file
   * @return list of test data
   * @throws TestDataReaderException TestDataReaderException
   */
  private A2LFileInfo getA2LModel(final String a2lFile) throws TestDataReaderException {

    // Get the a2l parser
    A2LParser a2lParser = new A2LParser(this.logger);
    a2lParser.setFileName(a2lFile);

    try {
      a2lParser.parse();
    }
    catch (Exception exp) {
      this.logger.error("Messages (A2L file): ");
      this.logger.error(a2lParser.getMessageBuffer().toString());
      this.logger.error(exp.getMessage());
      throw new TestDataReaderException("*** unexpected A2LParser error!" + exp.getMessage(), exp);
    }

    long parseA2LTime = a2lParser.getAnalyzeTime();
    this.logger.info("analyze time A2L in milli seconds: " + parseA2LTime);

    if (a2lParser.getA2LFileInfo() == null) {
      throw new TestDataReaderException("A2L data model is null");
    }
    // Get the A2lFileInfo object
    return a2lParser.getA2LFileInfo();
  }

}
