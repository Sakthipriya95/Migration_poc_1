/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.hexparser.tests;

import java.io.FileInputStream;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.calcomp.hexparser.MotorolaS19Parser;
import com.bosch.calcomp.hexparser.model.HexMemory;

/**
 * @author ICP1COB
 */
public class MotorolaS19ParserCallable implements Callable<HexMemory> {


  private final String fileName;


  /**
   * @param fileName data set name
   */
  public MotorolaS19ParserCallable(final String fileName) {
    this.fileName = fileName;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public HexMemory call() throws Exception {

    ILoggerAdapter motorolaS19ParserLogger = new Log4JLoggerAdapterImpl(LogManager.getLogger("TESTER"));

    MotorolaS19Parser motoS19Parser =
        new MotorolaS19Parser(new FileInputStream(this.fileName), motorolaS19ParserLogger);
    return motoS19Parser.parse();
  }
}
