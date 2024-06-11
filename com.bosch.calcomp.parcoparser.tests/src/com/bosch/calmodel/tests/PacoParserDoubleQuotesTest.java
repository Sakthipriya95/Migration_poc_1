/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calmodel.tests;

import static org.junit.Assert.assertEquals;

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
import com.bosch.calmodel.caldata.CalData;

/**
 * @author QRK1COB
 *
 */
public class PacoParserDoubleQuotesTest {
  
  private PacoParser pacoParserTestT1;
  private PacoParser pacoParserTestT2;
  private PacoParser pacoParserTestT3;
  private PacoParser pacoParserTestT4;
  private PacoParser pacoParserTestT5;
  private PacoParser pacoParserTestT6;
  private PacoParser pacoParserTestT7;
  
  private String pacoParserFileT1;
  private String pacoParserFileT2;
  private String pacoParserFileT3;
  private String pacoParserFileT4;
  private String pacoParserFileT5;
  private String pacoParserFileT6;
  private String pacoParserFileT7;
  
  /**
   * Setting objects and files
   */
  @Before
  public void setUp() {
    ILoggerAdapter logger = new Log4JLoggerAdapterImpl(LogManager.getLogger("PacoParser"));
    this.pacoParserFileT1 = "TestFiles\\DDRC_DebArTime.DFC_SSpMon2OV.tiLimFaild_ValidPacoF1.XML";
    this.pacoParserTestT1 = new PacoParser(logger, this.pacoParserFileT1);
    
    this.pacoParserFileT2 = "TestFiles\\File_2_CR6EU5-642-49DC-Ch4_nNW-164WA-150kW_EU5OP-ME15.xml";
    this.pacoParserTestT2 = new PacoParser(logger, this.pacoParserFileT2);
    
    this.pacoParserFileT3 = "TestFiles\\FDS3_Group3_a.xml";
    this.pacoParserTestT3 = new PacoParser(logger, this.pacoParserFileT3);
    
    this.pacoParserFileT4 = "TestFiles\\File_4_MUS41A50HQR00M01_FormA.xml";
    this.pacoParserTestT4 = new PacoParser(logger, this.pacoParserFileT4);
    
    this.pacoParserFileT5 = "TestFiles\\File_1_T7AXEJP3CS06_X144.xml";
    this.pacoParserTestT5 = new PacoParser(logger, this.pacoParserFileT5);
    
    this.pacoParserFileT6 = "TestFiles\\File_3_HEX_MMD114A0CC1788_MC50_DISCR_LC.xml";
    this.pacoParserTestT6 = new PacoParser(logger, this.pacoParserFileT6);
    
    this.pacoParserFileT7 = "TestFiles\\min_paco.xml";
    this.pacoParserTestT7 = new PacoParser(logger, this.pacoParserFileT7);
    
  }
  
  
  /**
   * Paco file testing with double quotes
   * @throws ExecutionException 
   * ExecutionException
   * @throws InterruptedException 
   * InterruptedException
   */
  @Test
  public void pacoParserDoubleQuotesTest() throws InterruptedException, ExecutionException {

      /*Excecution service for running multiple thread in queue*/
      ExecutorService executorService= Executors.newFixedThreadPool(7);
      /*Set 1*/
      pacoParserDoubleQuotesCallable firstFileSet1 = new pacoParserDoubleQuotesCallable(this.pacoParserFileT1,pacoParserTestT1);
      /*Set 2*/
      pacoParserDoubleQuotesCallable secondFileSet1 = new pacoParserDoubleQuotesCallable(this.pacoParserFileT2,pacoParserTestT2);
      /*Set 3*/
      pacoParserDoubleQuotesCallable FileSet3 = new pacoParserDoubleQuotesCallable(this.pacoParserFileT3,pacoParserTestT3);
      /*Set 4*/
      pacoParserDoubleQuotesCallable FileSet4 = new pacoParserDoubleQuotesCallable(this.pacoParserFileT4,pacoParserTestT4);
      /*Set 5*/
      pacoParserDoubleQuotesCallable FileSet5 = new pacoParserDoubleQuotesCallable(this.pacoParserFileT5,pacoParserTestT5);
      /*Set 6*/
      pacoParserDoubleQuotesCallable FileSet6 = new pacoParserDoubleQuotesCallable(this.pacoParserFileT6,pacoParserTestT6);
      /*Set 7*/
      pacoParserDoubleQuotesCallable FileSet7 = new pacoParserDoubleQuotesCallable(this.pacoParserFileT7,pacoParserTestT7);

      Future<Map<String, Object>> futureOne= executorService.submit(firstFileSet1);
      Future<Map<String, Object>> futureTwo= executorService.submit(secondFileSet1);
      Future<Map<String, Object>> future3= executorService.submit(FileSet3);
      Future<Map<String, Object>> future4= executorService.submit(FileSet4);
      Future<Map<String, Object>> future5= executorService.submit(FileSet5);
      Future<Map<String, Object>> future6= executorService.submit(FileSet6);
      Future<Map<String, Object>> future7= executorService.submit(FileSet7);
        
     /**
      * Assert check
      */
      
      /*Checking future 1 -> DDRC_DebArTime.DFC_SSpMon2OV.tiLimFaild_ValidPacoF1.XML*/
      System.out.println("Assert 1 Started");
      Map<String, Object> info = futureOne.get();
      CalData calData =(CalData) info.get("DDRC_DebArTime.DFC_SSpMon2OV.tiLimFaild_ValidPacoF1_File");
//      assertEquals("VALUE",calData.getType());
    assertEquals("DDRC_DemDeb",calData.getFunctionName());
//      assertEquals("DDRC_DebArTime.DFC_SSpMon2OV.tiLimFaild_ValidPacoF1_File",calData.getName());
//     assertEquals("100.0",calData.getSimpleDisplayValue());
      System.out.println("Assert 1 completed");
     
    /*  Checking future 2 -> File_2_CR6EU5-642-49DC-Ch4_nNW-164WA-150kW_EU5OP-ME15.xml*/
      Map<String, Object> info1 = futureTwo.get();
      CalData calData1 =(CalData) info1.get("ZMM_tiETDvtMax_CA");
//      assertEquals("VALUE",calData1.getType());
      assertEquals("ZMM_ESM",calData1.getFunctionName());
//      assertEquals("DDRC_RatDeb.GlwPlg_ctSCGUDRat_C",calData1.getName());
      System.out.println("Assert 2 completed");
      
     /* Checking future 3 -> FDS3_Group3_a.xml*/
      Map<String, Object> info2 = future3.get();
      CalData calData2 =(CalData)info2.get("ZMM_tiShrtTmrThresMaxTTCL_C");
//      assertEquals("VALUE",calData2.getType());
      assertEquals("ZMM_OBDTTCL",calData2.getFunctionName());
//      assertEquals("ScrObdCo_StEngLock_P",calData2.getName());
      System.out.println("Assert 3 completed");
      
     /* Checking future 4 -> File_4_MUS41A50HQR00M01_FormA.xml*/
      Map<String, Object> info3 = future4.get();
      CalData calData3 =(CalData)info3.get("ZZAEHLMN");
//      assertEquals("VALUE",calData3.getType());
      assertEquals("TMO2ETS",calData3.getFunctionName());
//      assertEquals("DDRC_DurDeb.Ebs_tiTempMaxDebDef_C",calData3.getName());
      System.out.println("Assert 4 completed");
      
      /*Checking future 5 -> File_1_T7AXEJP3CS06_X144.xml*/
      Map<String, Object> info4 = future5.get();
      CalData calData4 =(CalData)info4.get("ZMM_tiShrtTmrThresMaxTTCL_C");
//      assertEquals("VALUE",calData4.getType());
      assertEquals("ZMM_OBDTTCL",calData4.getFunctionName());
//      assertEquals("DFC_CtlMsk2.DFC_EGRClgLPMonEta_C",calData4.getName());
      System.out.println("Assert 5 completed");
      
      /*Checking future 6 -> File_3_HEX_MMD114A0CC1788_MC50_DISCR_LC.xml*/
      Map<String, Object> info5 = future6.get();
      CalData calData5 =(CalData) info5.get("ZMM_tiShrtTmrThresMaxTTCL_C");
//      assertEquals("VALUE",calData5.getType());
      assertEquals("ZMM_OBDTTCL",calData5.getFunctionName());
//      assertEquals("DATA_PDEnvObj.PDEnvObj_dstFrntObjTarDifSlowApx_C_VW",calData5.getName());
      System.out.println("Assert 6 completed");
      
     /* Checking future 7 -> min_paco.xml */
      Map<String, Object> info6 = future7.get();
      CalData calData6 =(CalData) info6.get("IgnClPs_Count");
//      assertEquals("MAP",calData6.getType());
      assertEquals("IgnClPs_ConCK",calData6.getFunctionName());
//      assertEquals("Afr_rpmEngSpdfacRelAir2facLambdaReqBase_MAP",calData6.getName());
      System.out.println("Assert 7 completed");
      
      System.out.println("Multi thread test1 Passed");
      executorService.shutdown();  
  }
  
  

  /**
   * @throws InterruptedException InterruptedException
   * @throws ExecutionException ExecutionException
   */
  @Test(expected = NullPointerException.class) 
  public void MultiThreadingTestFail() throws InterruptedException, ExecutionException {
    
    /*Excecution service for running multiple thread in queue*/
    ExecutorService executorService= Executors.newFixedThreadPool(7);
    /*Set 1*/
    pacoParserDoubleQuotesCallable firstFileSet1 = new pacoParserDoubleQuotesCallable(this.pacoParserFileT1,pacoParserTestT1);
    /*Set 2*/
    pacoParserDoubleQuotesCallable secondFileSet1 = new pacoParserDoubleQuotesCallable(this.pacoParserFileT2,pacoParserTestT2);
   
    /*response or result collection from each data run*/
    /*Negative usecase*/
    Future<Map<String, Object>> futureOne= executorService.submit(secondFileSet1);
    Future<Map<String, Object>> futureTwo= executorService.submit(firstFileSet1);
    
   /**
    * Assert check
    */
    
    /*Checking future 1 -> DDRC_DebArTime.DFC_SSpMon2OV.tiLimFaild_ValidPacoF1.XML*/
    System.out.println("Multi thread fail test1 Passed");
    Map<String, Object> info = futureOne.get();
    CalData calData = (CalData) info.get("DDRC_DebArTime.DFC_SSpMon2OV.tiLimFaild_ValidPacoF1_File");
    assertEquals("VALUE",calData.getCategoryName());
    assertEquals("DDRC_DemDeb",calData.getFunctionName());
    assertEquals("DDRC_DebArTime.DFC_SSpMon2OV.tiLimFaild_ValidPacoF1_File",calData.getShortName());
    assertEquals("100.0",calData.getCalDataPhy().getSimpleDisplayValue());
    System.out.println("Assert 1 fail completed");
    
    /*Checking future 2 -> File_2_CR6EU5-642-49DC-Ch4_nNW-164WA-150kW_EU5OP-ME15.xml*/
    Map<String, Object> info1 = futureTwo.get();
    CalData calData1 = (CalData) info1.get("DDRC_RatDeb.GlwPlg_ctSCGUDRat_C");
    assertEquals("VALUE",calData1.getCategoryName());
    assertEquals("DDRC",calData1.getFunctionName());
    assertEquals("DDRC_RatDeb.GlwPlg_ctSCGUDRat_C",calData1.getShortName());
    System.out.println("Assert 2 fail completed");
    
    System.out.println("Multi thread fail test1 Passed");
    executorService.shutdown();  
    
  }
  
  

}
