/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calmodel.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
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
public class pacoParserDoubleQuotesInvokeAllTest {
  
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
    
    this.pacoParserFileT7 = "TestFiles\\Paco_Pro.XML";
    this.pacoParserTestT7 = new PacoParser(logger, this.pacoParserFileT7);
    
  }
  
  
  /**
   * Paco file testing with double quotes
   * @throws ExecutionException 
   * @throws InterruptedException 
   */
  @Test
  public void pacoParserDoubleQuotesTest() throws InterruptedException, ExecutionException {

      /*Excecution service for running multiple thread in queue*/
      ExecutorService executorService= Executors.newFixedThreadPool(10);
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

      /*response or result collection from each data run*/
      
//      Future<Map<String, CalData>> futureOne= executorService.invokeAll(firstFileSet1)
//      Future<Map<String, CalData>> futureOne= executorService.submit(firstFileSet1)
//      Future<Map<String, CalData>> futureTwo= executorService.submit(secondFileSet1)
//      Future<Map<String, CalData>> future3= executorService.submit(FileSet3)
//      Future<Map<String, CalData>> future4= executorService.submit(FileSet4)
//      Future<Map<String, CalData>> future5= executorService.submit(FileSet5)
//      Future<Map<String, CalData>> future6= executorService.submit(FileSet6)
//      Future<Map<String, CalData>> future7= executorService.submit(FileSet7)
      
      List<Callable<Map<String, Object>>> callableTasks = new ArrayList<>();
      callableTasks.add(firstFileSet1);
      callableTasks.add(secondFileSet1);
      callableTasks.add(FileSet3);
      callableTasks.add(FileSet4);
      callableTasks.add(FileSet5); 
      callableTasks.add(FileSet6);
      callableTasks.add(FileSet7);
      
      List<Future<Map<String, Object>>> futures = executorService.invokeAll(callableTasks);
        
     /**
      * Assert check
      */
      int count = 1;
      for(Future<Map<String, Object>> future : futures){  
      
      if(count == 1) {
        /*Checking future 1 -> DDRC_DebArTime.DFC_SSpMon2OV.tiLimFaild_ValidPacoF1.XML*/
        /*File_4_MUS41A50HQR00M01_FormA.xml*/
        System.out.println("First iteration : "+count);
//        count++;
//        continue;
//        Map<String, CalData> info =  future.get();
//        CalData calData = info.get("DDRC_DebArTime.DFC_SSpMon2OV.tiLimFaild_C")
//        assertEquals("VALUE",calData.getCategoryName())
//        System.out.println("calData.getFunctionName() : "+calData.getFunctionName())
//        assertEquals("rba_IoDiagPwr",calData.getFunctionName())
//        assertEquals("DDRC_DebArTime.DFC_SSpMon2OV.tiLimFaild_C",calData.getShortName())
//        System.out.println("Assert 1 completed")
      } else if(count == 2) {
        /*Checking future 2 -> File_2_CR6EU5-642-49DC-Ch4_nNW-164WA-150kW_EU5OP-ME15.xml*/
        /*DDRC_DebArTime.DFC_SSpMon2OV.tiLimFaild_ValidPacoF1.XML*/
        System.out.println("Second iteration : "+count);
//        count++;
//        continue;
//        Map<String, CalData> info1 = future.get()
//        CalData calData1 = info1.get("DDRC_DebArTime.DFC_SSpMon2OV.tiLimFaild_ValidPacoF1_File")
//        assertEquals("VALUE",calData1.getCategoryName())
//        assertEquals("DDRC_DemDeb",calData1.getFunctionName())
//        assertEquals("DDRC_DebArTime.DFC_SSpMon2OV.tiLimFaild_ValidPacoF1_File",calData1.getShortName())
//        System.out.println("Assert 2 completed")
      } 
        else if(count == 3) {
        /*Checking future 3 -> FDS3_Group3_a.xml*/
        System.out.println("3 iteration : "+count);

        CalData calData2 =(CalData) future.get().get("ZMM_tiShrtTmrThresMaxTTCL_C");

//        assertEquals("VALUE",calData2.getType());
        assertEquals("ZMM_OBDTTCL",calData2.getFunctionName());
//        assertEquals("FDS3_Group3_a_File",calData2.getName());
        System.out.println("Assert 3 completed");
      } 
//        else if(count == 4) {
//        /*Checking future 4 -> File_4_MUS41A50HQR00M01_FormA.xml*/
//        Map<String, CalData> info3 = future.get()
//        CalData calData3 = info3.get("DDRC_DurDeb.StSpLmp_tiSCBDebOk_C")
//        assertEquals("VALUE",calData3.getCategoryName())
//        assertEquals("StSpLmp_DD",calData3.getFunctionName())
//        assertEquals("DDRC_DurDeb.StSpLmp_tiSCBDebOk_C",calData3.getShortName())
//        System.out.println("Assert 4 completed")
//      } else if(count == 5) 
//        /*Checking future 5 -> File_1_T7AXEJP3CS06_X144.xml*/
//        Map<String, CalData> info4 = future.get()
//        CalData calData4 = info4.get("DFC_CtlMsk2.DFC_EGRClgLPMonEta_C")
//        assertEquals("VALUE",calData4.getCategoryName())
//        assertEquals("EGRClgLP_MonEta",calData4.getFunctionName())
//        assertEquals("DFC_CtlMsk2.DFC_EGRClgLPMonEta_C",calData4.getShortName())
//        System.out.println("Assert 5 completed")
//      } else if(count == 6) {
//        /*Checking future 6 -> File_3_HEX_MMD114A0CC1788_MC50_DISCR_LC.xml*/
//        Map<String, CalData> info5 = future.get()
//        CalData calData5 = info5.get("DATA_PDEnvObj.PDEnvObj_dstFrntObjTarDifSlowApx_C_VW")
//        assertEquals("VALUE",calData5.getCategoryName())
//        assertEquals("PDEnvObj",calData5.getFunctionName())
//        assertEquals("DATA_PDEnvObj.PDEnvObj_dstFrntObjTarDifSlowApx_C_VW",calData5.getShortName())
//        System.out.println("Assert 6 completed")
//      } else if(count == 7) {
//        /*Checking future 7 -> Paco_Pro.XML */
//        Map<String, CalData> info6 = future.get()
//        CalData calData6 = info6.get("DDRC_DebArCounter.DFC_ECUM_E_CONFIGURATION_DATA_INCONSISTENT.isJmpUpActv_C")
//        assertEquals("VALUE",calData6.getCategoryName())
//        assertEquals("DDRC_DemDeb",calData6.getFunctionName())
//        assertEquals("DDRC_DebArCounter.DFC_ECUM_E_CONFIGURATION_DATA_INCONSISTENT.isJmpUpActv_C",calData6.getShortName())
//        System.out.println("Assert 7 completed")
//      }
      
      count++;
      
      } 
      
      System.out.println("Third multi thread test Passed");
  }
  

}
