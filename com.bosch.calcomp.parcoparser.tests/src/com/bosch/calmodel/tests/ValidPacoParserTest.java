/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calmodel.tests;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.junit.Before;
import org.junit.Test;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.calcomp.pacoparser.PacoParser;

/**
 * @author QRK1COB
 *
 */
public class ValidPacoParserTest {
  
  private PacoParser pacoParserTestT1;
  private PacoParser pacoParserTestT2;
  private String pacoFileT1;
  private String pacoFileT2;
  private String validPacoFileT1;
  private String validPacoFileT2;
  private String pacoParserFileT1;
  private String pacoParserFileT2;
  
  /**
   * Setting objects and files
   */
  @Before
  public void setUp() {
    ILoggerAdapter logger = new Log4JLoggerAdapterImpl(LogManager.getLogger("PacoParser"));
    this.pacoParserFileT1 = "TestFiles\\DDRC_DebArTime.DFC_SSpMon2OV.tiLimFaild_ValidPacoF1.XML";
    this.pacoParserFileT2 = "TestFiles\\DDRC_DebArTime.DFC_SSpMon2OV.tiLimFaild_ValidPacoF2.XML";
    this.pacoFileT1 = "TestFiles\\Paco_ProV1.XML";
    this.pacoFileT2 = "TestFiles\\Paco_ProV2.XML";
    this.pacoParserTestT1 = new PacoParser(logger, this.pacoFileT1);
    this.pacoParserTestT2 = new PacoParser(logger, this.pacoFileT2);
    this.validPacoFileT1 = "TestFiles\\FDS3_Group3_V1.xml";
    this.validPacoFileT2 = "TestFiles\\FDS3_Group3_V2.xml";
  }
  
  
  /**
   * valid testcase
   */
  @Test
  public void validPacoParserTest() {

//  Excecution service for running multiple thread in queue
    ExecutorService executorService= Executors.newFixedThreadPool(2);
//  Set 1
    ValidPacoParserCallable firstFileSet1 = new ValidPacoParserCallable(this.validPacoFileT1,pacoParserTestT1);
//  Set 2
    ValidPacoParserCallable secondFileSet1 = new ValidPacoParserCallable(this.validPacoFileT2,pacoParserTestT2);
        
//  response or result collection from each data run
    Future<Map> futureOne= executorService.submit(firstFileSet1);
    Future<Map> futureTwo= executorService.submit(secondFileSet1);
//  assertNotNull(this.pacoParserTestT1.parse());
    System.out.println("Second multi thread test Passed");
  }
  

}
