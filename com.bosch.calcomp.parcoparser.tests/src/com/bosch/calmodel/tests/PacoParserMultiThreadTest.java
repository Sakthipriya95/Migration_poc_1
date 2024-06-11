/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calmodel.tests;

import static org.junit.Assert.assertNotNull;

import java.util.Map;
import java.util.concurrent.ExecutionException;
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
public class PacoParserMultiThreadTest {
  
  private PacoParser pacoParserTestT1;
  private PacoParser pacoParserTestT2;
  private String pacoFileT1;
  private String pacoFileT2;
  private String validPacoFileT1;
  private String validPacoFileT2;
  private String pacoParserFileT1;
  private String pacoParserFileT2;
  // Excecution service for running multiple thread in queue
  ExecutorService executorService= Executors.newFixedThreadPool(2);
  /**
   * Setting objects and files
   */
  @Before
  public void setUp() {
    ILoggerAdapter logger = new Log4JLoggerAdapterImpl(LogManager.getLogger("PacoParser"));
    this.pacoParserFileT1 = "TestFiles\\DDRC_DebArTime.DFC_SSpMon2OV.tiLimFaild_C.XML";
    this.pacoParserFileT2 = "TestFiles\\DDRC_DebArTime.DFC_SSpMon2OV.tiLimFaild_C1.XML";
    this.pacoFileT1 = "TestFiles\\Paco_Pro.XML";
    this.pacoFileT2 = "TestFiles\\min_paco.XML";
    this.pacoParserTestT1 = new PacoParser(logger, this.pacoFileT1);
    this.pacoParserTestT2 = new PacoParser(logger, this.pacoFileT2);
    this.validPacoFileT1 = "TestFiles\\FDS3_Group3_a.xml";
    this.validPacoFileT2 = "TestFiles\\FDS3_Group3_a1.xml";
  }
  
  /**
   * valid testcase
   * @throws ExecutionException 
   * @throws InterruptedException 
   */
  @Test
  public void pacoParserTest() throws InterruptedException, ExecutionException {
//  Set 1
    PacoParserCallable firstFileSet= new PacoParserCallable(this.pacoFileT1,pacoParserTestT1);
//  Set 2
    PacoParserCallable secondFileSet= new PacoParserCallable(this.pacoFileT2,pacoParserTestT2);
    
//  response or result collection from each data run
    Future<Map> futureOne= executorService.submit(firstFileSet);
    Future<Map> futureTwo= executorService.submit(secondFileSet);
    
//    assertNotNull(this.pacoParserTestT1.parse());
    assertNotNull(futureOne.get().values());
    assertNotNull(futureTwo.get().values());
    System.out.println("First multi thread test Passed");
   
  }
  
}
