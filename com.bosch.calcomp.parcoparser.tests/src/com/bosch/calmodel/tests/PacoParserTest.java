/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calmodel.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.junit.Before;
import org.junit.Test;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.calcomp.pacoparser.PacoParser;
import com.bosch.calcomp.pacoparser.exception.PacoParserException;
import com.bosch.calcomp.pacotocaldata.factory.impl.CalDataModelAdapterFactory;
import com.bosch.calmodel.caldata.CalData;

/**
 * @author VDR1COB
 */
public class PacoParserTest {

  private PacoParser pacoParserTest;
  private String pacoFile;
  private String validPacoFile;
  private String pacoParserFile;
  /**
   * Setting objects and files
   */
  @Before
  public void setUp() {
    ILoggerAdapter logger = new Log4JLoggerAdapterImpl(LogManager.getLogger("PacoParser"));
   // this.pacoFile = "TestFiles\\min_paco.xml";
//    this.pacoParserFile = "TestFiles\\DDRC_DebArTime.DFC_SSpMon2OV.tiLimFaild_C.XML";
    this.pacoParserFile = "TestFiles\\Bosch_hydraulic_SSD_ESH_20180125.XML";
    this.pacoFile = "TestFiles\\Paco_Pro.XML";
    this.pacoParserTest = new PacoParser(logger, this.pacoFile);

    this.validPacoFile = "TestFiles\\FDS3_Group3_a.xml";
   
  }

  /**
   * valid testcase
   */
  @Test
  public void pacoParserTest() {


    try {
      ClassLoader classLoader = CalDataModelAdapterFactory.class.getClassLoader();
      pacoParserTest.setTargetModelClassName(CalDataModelAdapterFactory.class.getName());
      pacoParserTest.setTargetModelClassLoader(classLoader);
      this.pacoParserTest.setFileName(this.pacoFile);
      assertNotNull(this.pacoParserTest.parse());
      
    
      System.out.println("passed");
    }

    catch (PacoParserException e) {
      e.printStackTrace();
    }
  }

  /**
   * valid testcase
   */
  @Test
  public void validPacoParserTest() {

    try {
      this.pacoParserTest.setFileName(this.validPacoFile);
      assertNotNull(this.pacoParserTest.parse());
    }

    catch (PacoParserException e) {
      e.printStackTrace();
    }
  }
  /**
   * Paco file testing with double quotes
   */
  @Test
  public void pacoParserDoubleQuotesTest() {

    try {
      ClassLoader classLoader = CalDataModelAdapterFactory.class.getClassLoader();
      pacoParserTest.setTargetModelClassName(CalDataModelAdapterFactory.class.getName());
      pacoParserTest.setTargetModelClassLoader(classLoader);
      this.pacoParserTest.setFileName(this.pacoParserFile);
      Map<String, CalData> info = this.pacoParserTest.parse();
//       CalData calData = (CalData) info.get("DDRC_DebArTime.DFC_SSpMon2OV.tiLimFaild_C");
       CalData calData =  info.get("Rail_pMeUnDvtMin_CUR");//
       CalData calData1 = info.get("DFC_CtlMsk.DFC_RailPGradMon_C");
       CalData calData2 = info.get("RailP_tiEngOff_C");
       /**
        * Assert check
        */
       //assertEquals("Parameters of ArTime debouncing \\\"Failed\\\" limit",calData.getLongName());
//      assertEquals("VALUE",calData.getType());
//      assertEquals("VALUE",calData.getCategoryName());
      assertEquals("CURVE_INDIVIDUAL",calData.getCategoryName());
      assertEquals("VALUE",calData1.getCategoryName());
      assertEquals("VALUE",calData2.getCategoryName());
      
            
    }

    catch (PacoParserException e) {
      e.printStackTrace();
    }
  }
}
