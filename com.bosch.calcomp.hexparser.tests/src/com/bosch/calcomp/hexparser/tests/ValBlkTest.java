/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.hexparser.tests;

import static org.junit.Assert.assertEquals;

import org.apache.logging.log4j.LogManager;
import org.junit.Before;
import org.junit.Test;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.calcomp.caldatafromhex.CalDataPhyFactory;
import com.bosch.calcomp.caldatafromhex.CalDataPhyFactoryException;
import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;

/**
 * @author SON9COB
 */
public class ValBlkTest {

  private CalDataPhyFactory calDataPhyFactoryTest;
  private String a2lFile;
  private String notSupportedDimValBlk;
  private String hexFile;
  private A2LFileInfo a2lFileInfoInst;
  ILoggerAdapter logs = new Log4JLoggerAdapterImpl(LogManager.getLogger("Hex2Phy"));


  /**
   * Setting objects and files
   */
  @Before
  public void setup() {
    this.calDataPhyFactoryTest = new CalDataPhyFactory(this.logs);
    this.a2lFile = "TestFiles\\ValidA2l.a2l";
    this.notSupportedDimValBlk = "TestFiles\\NotSupportedDimValBlk.a2l";
    this.hexFile = "TestFiles\\FDS3_Group2_b.hex";


  }

  /**
   * Valid Files are passed
   */
  @Test
  public void valBlkFileInputTest() {

    this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.hexFile);
    this.a2lFileInfoInst = this.calDataPhyFactoryTest.getA2lFileInfo();
    this.calDataPhyFactoryTest.getCalData(this.a2lFileInfoInst, this.hexFile);
    Characteristic characValBlk = this.a2lFileInfoInst.getAllModulesLabels().get("ACC_ON_GEARTB");
    assertEquals("Pass", "VAL_BLK", characValBlk.getType());
    assertEquals("Pass", "0x808EA7A3", characValBlk.getAddress());
    assertEquals("Pass", "ValA_Wu8", characValBlk.getDeposit());
    assertEquals("Pass", "1.000", characValBlk.getMaxDiff());
    assertEquals("Pass", "DC_FLAG", characValBlk.getConversion());
    assertEquals("Pass", "0.00", characValBlk.getLowerLimit());
    assertEquals("Pass", "1.000", characValBlk.getUpperLimit());
    assertEquals("Pass", "%5.3", characValBlk.getFormat());
    assertEquals("Pass", "0.00", characValBlk.getExtLowerLimit());
    assertEquals("Pass", "255.0", characValBlk.getExtUpperLimit());
    assertEquals("Pass", 16, characValBlk.getNumValues());
  }

  /**
   * Not Supported Dimension in VAL_BLK
   */
  @Test(expected = CalDataPhyFactoryException.class)
  public void notSupportedDimValBlkTest() {

    this.calDataPhyFactoryTest.getCalData(this.notSupportedDimValBlk, this.hexFile);
  }
}
