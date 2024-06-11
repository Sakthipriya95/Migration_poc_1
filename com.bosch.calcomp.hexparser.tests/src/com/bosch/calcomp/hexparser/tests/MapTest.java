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
import com.bosch.calcomp.calutil.tools.A2LUtil;
import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.axis.AxisDescription;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.calmodel.a2ldata.module.labels.CharacteristicMap;

/**
 * @author SON9COB
 */
public class MapTest {

  private CalDataPhyFactory calDataPhyFactoryTest;
  private String a2lFile;
  private String errorYAxisPtsMap;
  private String errorXAxisPtsMap;
  private String hexFile;
  private String unknownRecLayoutParameterMap;
  private String notSupportedAddrType;
  ILoggerAdapter logs = new Log4JLoggerAdapterImpl(LogManager.getLogger("Hex2Phy"));

  /**
   * Setting objects and files
   */
  @Before
  public void setup() {
    this.calDataPhyFactoryTest = new CalDataPhyFactory(this.logs);
    this.a2lFile = "TestFiles\\ValidA2l.a2l";
    this.errorYAxisPtsMap = "TestFiles\\ErrorYAxisPtsMap.a2l";
    this.errorXAxisPtsMap = "TestFiles\\ErrorXAxisPtsMap.a2l";
    this.hexFile = "TestFiles\\FDS3_Group2_b.hex";
    this.unknownRecLayoutParameterMap = "TestFiles\\UnknownRecLayoutParameterMap.a2l";
    this.notSupportedAddrType = "TestFiles\\NotSupportedAddrType.a2l";
  }

  /**
   * Files are passed
   */


  @Test
  public void mapFileInputTest() {

    this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.hexFile);
    A2LFileInfo a2lFileInfo = this.calDataPhyFactoryTest.getA2lFileInfo();
    Characteristic characteristic = a2lFileInfo.getAllModulesLabels().get("GlwCtl_rPst1_MAP");
    assertEquals("Prc", characteristic.getConversion());
    assertEquals("Map_Xs16Ys16Ws16", characteristic.getDeposit());
    CharacteristicMap map = (CharacteristicMap) characteristic;
    assertEquals("-400.0000", map.getExtLowerLimit());
    assertEquals("399.9878", map.getExtUpperLimit());

    AxisDescription axisDescriptionX = map.getAxisDescriptionX();
    AxisDescription axisDescriptionY = map.getAxisDescriptionY();
    assertEquals("Pass", A2LUtil.STD_AXIS, axisDescriptionX.getAttribute());
    assertEquals("Pass", A2LUtil.STD_AXIS, axisDescriptionY.getAttribute());
    assertEquals("InjMass", axisDescriptionX.getConversion());
    assertEquals("EngN", axisDescriptionY.getConversion());
  }

  /**
   * Error in Y axis pts in NO_AXIS_PTS_Y in genMap()
   */
  @Test(expected = CalDataPhyFactoryException.class)
  public void errorYAxisPtsTest() {

    this.calDataPhyFactoryTest.getCalData(this.errorYAxisPtsMap, this.hexFile);
  }

  /**
   * Error in X axis pts in NO_AXIS_PTS_Y in genMap()
   */
  @Test(expected = CalDataPhyFactoryException.class)
  public void errorXAxisPtsTest() {

    this.calDataPhyFactoryTest.getCalData(this.errorXAxisPtsMap, this.hexFile);
  }

  /**
   * Unknown Record_Layout Parameter in Map in genMap()
   */
  @Test(expected = CalDataPhyFactoryException.class)
  public void unknownRecLayoutParaMapTest() {

    this.calDataPhyFactoryTest.getCalData(this.unknownRecLayoutParameterMap, this.hexFile);
  }

  /**
   * Not Supported Address Type in checkDirectAccessTypeOnly()
   */
  @Test(expected = CalDataPhyFactoryException.class)
  public void notSupportedAddrTypeTest() {

    this.calDataPhyFactoryTest.getCalData(this.notSupportedAddrType, this.hexFile);
  }
}
