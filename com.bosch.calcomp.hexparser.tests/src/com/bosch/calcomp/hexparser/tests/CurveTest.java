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
import com.bosch.calmodel.a2ldata.module.labels.CharacteristicCurve;

/**
 * @author SON9COB
 */
public class CurveTest {

  private CalDataPhyFactory calDataPhyFactoryTest;
  private String a2lFile;
  private String errorXAxisPtsCurve;
  private String hexFile;
  private String unknownRecLayoutParameterCurve;

  ILoggerAdapter logs = new Log4JLoggerAdapterImpl(LogManager.getLogger("Hex2Phy"));

  /**
   * Setting objects and files
   */
  @Before
  public void setup() {
    this.calDataPhyFactoryTest = new CalDataPhyFactory(this.logs);
    this.a2lFile = "TestFiles\\ValidA2l.a2l";
    this.errorXAxisPtsCurve = "TestFiles\\ErrorXAxisPtsCurve.a2l";
    this.hexFile = "TestFiles\\FDS3_Group2_b.hex";
    this.unknownRecLayoutParameterCurve = "TestFiles\\UnknownRecLayoutParaCurve.a2l";

  }

  /**
   * Files are passed
   */


  @Test
  public void curveFileInputTest() {

    this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.hexFile);
    A2LFileInfo a2lFileInfo = this.calDataPhyFactoryTest.getA2lFileInfo();
    // Curve Tests
    Characteristic characteristic = a2lFileInfo.getAllModulesLabels().get("AirSysPDrift_CtrHeatInThrd_L");
    assertEquals("DC_CNTR", characteristic.getConversion());
    assertEquals("CurG_Wu16", characteristic.getDeposit());
    CharacteristicCurve curve = (CharacteristicCurve) characteristic;
    assertEquals("DC_CNTR", curve.getConversion());
    assertEquals("CurG_Wu16", curve.getDeposit());
    assertEquals(null, curve.getDisplayIdentifier());
    AxisDescription axisd = curve.getAxisDescriptionX();
    assertEquals("Pass", A2LUtil.COM_AXIS, axisd.getAttribute());
    assertEquals("-4096.000", axisd.getExtLowerLimit());
    assertEquals("4095.875", axisd.getExtUpperLimit());

    Characteristic characCurve = a2lFileInfo.getAllModulesLabels().get("ACCmpr_rCnv_CUR");
    assertEquals("DtyCyc", characCurve.getConversion());
    CharacteristicCurve curveType = (CharacteristicCurve) characCurve;
    assertEquals("Cur_Xs16Ws16", curveType.getDeposit());
    AxisDescription axisdes = curveType.getAxisDescriptionX();
    assertEquals("Pass", A2LUtil.STD_AXIS, axisdes.getAttribute());
    assertEquals("-400.0000", axisdes.getExtLowerLimit());
    assertEquals("399.9878", axisdes.getExtUpperLimit());
  }

  /**
   * Error in X axis pts in NO_AXIS_PTS_X in genCurve()
   */
  @Test(expected = CalDataPhyFactoryException.class)
  public void errorXAxisPts() {

    this.calDataPhyFactoryTest.getCalData(this.errorXAxisPtsCurve, this.hexFile);
  }

  /**
   * Unknown Record_Layout Parameter in Curve in genCurve()
   */
  @Test(expected = CalDataPhyFactoryException.class)
  public void unknownRecLayoutParaCurveTest() {

    this.calDataPhyFactoryTest.getCalData(this.unknownRecLayoutParameterCurve, this.hexFile);
  }
}
