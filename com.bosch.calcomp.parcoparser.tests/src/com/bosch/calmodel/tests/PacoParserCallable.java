/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calmodel.tests;

import java.util.Map;
import java.util.concurrent.Callable;

import com.bosch.calcomp.pacoparser.PacoParser;
import com.bosch.calcomp.pacotocaldata.factory.impl.CalDataModelAdapterFactory;

/**
 * @author QRK1COB
 *
 */
public class PacoParserCallable implements Callable<Map> {
  
  private PacoParser pacoParserTest;
  ClassLoader classLoader;
  private String pacoFile;
  private String validPacoFile;
  
  /**
   * @param fileName data set name
   */
  public PacoParserCallable(final String pacoFile, final PacoParser pacoParserTest) {
    this.pacoFile = pacoFile;
    this.pacoParserTest = pacoParserTest;
  }
  
  /** 
   * {@inheritDoc}
   */
  @Override
  public Map call() throws Exception {
    // TODO Auto-generated method stub
    
    ClassLoader classLoader = CalDataModelAdapterFactory.class.getClassLoader();
    this.pacoParserTest.setTargetModelClassName(CalDataModelAdapterFactory.class.getName());
    this.pacoParserTest.setTargetModelClassLoader(classLoader);
    this.pacoParserTest.setFileName(this.pacoFile);
    
    return this.pacoParserTest.parse();
  }

}
