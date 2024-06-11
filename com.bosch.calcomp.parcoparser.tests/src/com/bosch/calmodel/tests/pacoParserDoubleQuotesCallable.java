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
public class pacoParserDoubleQuotesCallable implements Callable<Map<String, Object>> {
  
  private PacoParser pacoParserTest;
  ClassLoader classLoader;
  private String pacoParserFile;
  private String validPacoFile;
  
  public pacoParserDoubleQuotesCallable(final String pacoParserFile, final PacoParser pacoParserTest) {
    this.pacoParserFile = pacoParserFile;
    this.pacoParserTest = pacoParserTest;
  }

  /** 
   * {@inheritDoc}
   */
  @Override
  public Map<String, Object> call() throws Exception {
    // TODO Auto-generated method stub
    ClassLoader classLoader = CalDataModelAdapterFactory.class.getClassLoader();
    pacoParserTest.setTargetModelClassName(CalDataModelAdapterFactory.class.getName());
    pacoParserTest.setTargetModelClassLoader(classLoader);
    this.pacoParserTest.setFileName(this.pacoParserFile);
    Thread.currentThread().setName(this.pacoParserFile);
    return this.pacoParserTest.parse();
  }

}
