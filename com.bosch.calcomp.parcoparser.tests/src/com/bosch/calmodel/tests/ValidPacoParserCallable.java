/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calmodel.tests;

import java.util.Map;
import java.util.concurrent.Callable;

import com.bosch.calcomp.pacoparser.PacoParser;

/**
 * @author QRK1COB
 *
 */
public class ValidPacoParserCallable  implements Callable<Map> {
  
  private PacoParser pacoParserTest;
  private String validPacoFile;
  
  /**
   * @param fileName data set name
   */
  public ValidPacoParserCallable(final String validPacoFile, final PacoParser pacoParserTest) {
    this.validPacoFile = validPacoFile;
    this.pacoParserTest = pacoParserTest;
  }

  /** 
   * {@inheritDoc}
   */
  @Override
  public Map call() throws Exception {
    // TODO Auto-generated method stub
    this.pacoParserTest.setFileName(this.validPacoFile);
    return this.pacoParserTest.parse();
  }

}
