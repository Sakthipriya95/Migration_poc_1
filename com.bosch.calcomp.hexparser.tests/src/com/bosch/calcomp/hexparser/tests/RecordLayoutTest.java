/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.hexparser.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.logging.log4j.LogManager;
import org.junit.Before;
import org.junit.Test;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.calcomp.caldatafromhex.CalDataPhyFactory;
import com.bosch.calcomp.caldatafromhex.CalDataPhyFactoryException;
import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.memory.layout.RecordLayout;

/**
 * @author SON9COB
 */
public class RecordLayoutTest {

  private CalDataPhyFactory calDataPhyFactoryTest;
  private String a2lFile;
  private String hexFile;
  private String unknownRecordLayoutParaAxisPts;
  private A2LFileInfo a2lFileInfoInst;
  ILoggerAdapter logs = new Log4JLoggerAdapterImpl(LogManager.getLogger("Hex2Phy"));


  /**
   * Setting objects and files
   */
  @Before
  public void setUp() {
    this.calDataPhyFactoryTest = new CalDataPhyFactory(this.logs);
    this.a2lFile = "TestFiles\\FDS3.a2l";
    this.hexFile = "TestFiles\\FDS3_Group2_b.hex";
    this.unknownRecordLayoutParaAxisPts = "TestFiles\\UnknownRecordLayoutParaAxisPts.a2l";
  }

  /**
   * Files are passed and added RESERVED in curve and map, FIX_NO_AXIS_PTS_X
   */
  @Test
  public void recordLayoutFileInputTest() {

    this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.hexFile);
    this.a2lFileInfoInst = this.calDataPhyFactoryTest.getA2lFileInfo();
    this.calDataPhyFactoryTest.getCalData(this.a2lFileInfoInst, this.hexFile);
    RecordLayout recordLayout = this.a2lFileInfoInst.getNext().getRecordLayoutsMap().get("RB_Curve_U8_U8");
    assertNotNull(recordLayout.getAlignmentByte());
    assertEquals("RB_Curve_U8_U8", recordLayout.getName());
    assertEquals(false, recordLayout.isFixNoAxisPtsSet());
  }

  /**
   * Unknown record layout parameter type in axis_pts in genAxisPts()
   */
  @Test(expected = CalDataPhyFactoryException.class)
  public void unknownRecordLayoutParaAxisPtsTest() {

    this.calDataPhyFactoryTest.getCalData(this.unknownRecordLayoutParaAxisPts, this.hexFile);
  }

}