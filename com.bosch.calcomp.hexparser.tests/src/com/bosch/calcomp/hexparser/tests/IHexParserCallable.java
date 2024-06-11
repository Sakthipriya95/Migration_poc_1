/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.hexparser.tests;

import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.calcomp.hexparser.IHexParser;
import com.bosch.calcomp.hexparser.exception.HexParserException;
import com.bosch.calcomp.hexparser.factory.HexParserFactory;
import com.bosch.calcomp.hexparser.model.HexMemory;

/**
 * @author ICP1COB
 */
public class IHexParserCallable implements Callable<HexMemory> {


  private final String fileName;


  /**
   * @param fileName data set name
   */
  public IHexParserCallable(final String fileName) {
    this.fileName = fileName;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public HexMemory call() throws Exception {

    ILoggerAdapter iHexParserLogger = new Log4JLoggerAdapterImpl(LogManager.getLogger("TESTER"));

    HexParserFactory hexParserFactory = new HexParserFactory();
    IHexParser hexParser = hexParserFactory.createHexParser(this.fileName, iHexParserLogger);
    HexMemory hexMemory = null;
    if (hexParser != null) {
      // parse the HEX File
      try {
        hexParser.parse();
      }

      catch (HexParserException e) {
        throw new HexParserException("*** unexpected HexParser error!", HexParserException.UNEXPECTED_ERROR, e);
      }
      hexMemory = hexParser.getHexMemory();
    }
    return hexMemory;
  }
}
