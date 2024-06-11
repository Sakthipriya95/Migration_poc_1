/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.hexparser.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.logging.log4j.LogManager;
import org.junit.Before;
import org.junit.Test;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.calcomp.caldatafromhex.CalDataPhyFactory;
import com.bosch.calcomp.caldatafromhex.CalDataPhyFactoryException;
import com.bosch.calcomp.calutil.tools.A2LUtil;
import com.bosch.calcomp.calutil.tools.CalFileReader;
import com.bosch.calcomp.hexparser.IntelHexParser;
import com.bosch.calcomp.hexparser.process.IntelHexRecord;
import com.bosch.calcomp.hexparser.util.HexParserUtil;
import com.bosch.calcomp.parser.a2l.exception.A2lParserException;
import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.axis.AxisDescription;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.calmodel.a2ldata.module.labels.CharacteristicCurve;

/**
 * @author SON9COB
 */
public class CharacteristicsTest {

  private CalDataPhyFactory calDataPhyFactoryTest;
  private String a2lFile;
  private String hexFile;
  private String noCompuMethod;
  private String notSupportCompuResAxis;
  private String notSupportCompuCurveAxis;
  private String axisHex;
  private String charFile;
  private String errorNoRescaleXAxisPts;
  private CalFileReader file;
  private String noRecordLayoutParameters;
  private String notSupportedDataType;
  private String s19File;
  ILoggerAdapter logs = new Log4JLoggerAdapterImpl(LogManager.getLogger("Hex2Phy"));

  /**
   * Setting objects and files
   */
  @Before
  public void setup() {
    this.calDataPhyFactoryTest = new CalDataPhyFactory(this.logs);
    this.a2lFile = "TestFiles\\Annotation_test2.a2l";
    this.hexFile = "TestFiles\\FDS3_Group2_b.hex";
    this.noCompuMethod = "TestFiles\\NoCompuMethod.a2l";
    this.notSupportCompuResAxis = "TestFiles\\NotSupportCompuMethodResAxis.a2l";
    this.notSupportCompuCurveAxis = "TestFiles\\NotSupportCompuMethodCurveAxis.a2l";
    this.axisHex = "TestFiles\\AxisHex.hex";
    this.charFile = "TestFiles\\FDS3_characteristicParameters.a2l";
    this.errorNoRescaleXAxisPts = "TestFiles\\ErrorNoRescaleXAxisPts.a2l";
    this.file = null;
    this.noRecordLayoutParameters = "TestFiles\\NoRecordLayoutParameters.a2l";
    this.notSupportedDataType = "TestFiles\\NotSupportedDataType.a2l";
    this.s19File = "TestFiles\\S19File.s19";
  }

  /**
   * Files are passed
   */


  @Test
  public void characteristicsFileInputTest() {
    ILoggerAdapter hexParserlogger = new Log4JLoggerAdapterImpl(LogManager.getLogger("hexParserlogger"));
    HexParserUtil hexParserUtil = new HexParserUtil();
    this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.hexFile);
    A2LFileInfo a2lFileInfo = this.calDataPhyFactoryTest.getA2lFileInfo();

    if (this.file == null) {
      // ALM-280807
      try {
        this.file = new CalFileReader(new FileInputStream(this.hexFile));
      }
      catch (FileNotFoundException e) {
        e.printStackTrace();
      }

    }
    IntelHexRecord record = new IntelHexRecord(this.file.getLine(), hexParserlogger, hexParserUtil);
    assertNotNull(record.getChecksum());
    assertEquals(false, record.isChecksumValid());
    IntelHexParser hexParser = new IntelHexParser(this.hexFile, hexParserlogger, hexParserUtil);
    assertEquals(-1, hexParser.getLineCount());
    assertNotNull(hexParser.getInputStream());
    assertNotNull(hexParser.getWarningsMap());
    // Characteristic test
    Characteristic characteristic = a2lFileInfo.getAllModulesLabels().get("AirSysPDrift_CtrHeatInThrd_L");
    assertEquals("DC_CNTR", characteristic.getConversion());
    assertEquals("CurG_Wu16", characteristic.getDeposit());
    CharacteristicCurve curve = (CharacteristicCurve) characteristic;
    AxisDescription axisd = curve.getAxisDescriptionX();
    assertEquals("Pass", A2LUtil.RES_AXIS, axisd.getAttribute());
  }

  /**
   * No compu method
   */
  @Test(expected = A2lParserException.class)
  public void noCompuMethodInputFailTest() {

    this.calDataPhyFactoryTest.getCalData(this.noCompuMethod, this.hexFile);

  }

  /**
   * Byte could not be read
   */
  @Test(expected = CalDataPhyFactoryException.class)
  public void byteNotReadHexFailTest() {

    this.calDataPhyFactoryTest.getCalData(this.notSupportCompuResAxis, this.hexFile);

  }

  /**
   * Compu_Method not supported for RES_AXIS
   */
  @Test(expected = CalDataPhyFactoryException.class)
  public void notSupportCompuResAxisTest() {

    this.calDataPhyFactoryTest.getCalData(this.notSupportCompuResAxis, this.axisHex);

  }

  /**
   * Compu_Method not supported for CURVE_AXIS
   */
  @Test(expected = CalDataPhyFactoryException.class)
  public void notSupportCompuCurveAxisTest() {

    this.calDataPhyFactoryTest.getCalData(this.notSupportCompuCurveAxis, this.axisHex);

  }

  /**
   * Unknown characteristic type in getCalData4Characteristic()
   */
  @Test(expected = CalDataPhyFactoryException.class)
  public void unknownCharacteristicTypeTest() {

    this.calDataPhyFactoryTest.getCalData(this.charFile, this.hexFile);

  }

  /**
   * Error in N0_RESCALE_X in genAxisPts()
   */
  @Test(expected = CalDataPhyFactoryException.class)
  public void errorNoRescaleXAxisPtsTest() {

    this.calDataPhyFactoryTest.getCalData(this.errorNoRescaleXAxisPts, this.hexFile);

  }

  /**
   * No Record_Layout Parameters in getMinParNum()
   */
  @Test(expected = CalDataPhyFactoryException.class)
  public void noRecordLayoutParametersTest() {

    this.calDataPhyFactoryTest.getCalData(this.noRecordLayoutParameters, this.hexFile);

  }

  /**
   * Not Supported Data Type in checkIf64BitIntDataType()
   */
  @Test
  // (expected = CalDataPhyFactoryException.class)
  public void notSupportedDataTypeTest() {

    this.calDataPhyFactoryTest.getCalData(this.notSupportedDataType, this.hexFile);

  }

  /**
   * A2L and S19 extension files are passed
   */
  @Test

  public void s19Test() {

    this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.s19File);
    A2LFileInfo a2lFileInfo = this.calDataPhyFactoryTest.getA2lFileInfo();
    // Curve Tests
    Characteristic characteristic = a2lFileInfo.getAllModulesLabels().get("AirSysPDrift_CtrHeatInThrd_L");
    assertEquals("DC_CNTR", characteristic.getConversion());
    assertEquals("CurG_Wu16", characteristic.getDeposit());
  }


  /**
   * Test for different rec type for S19 file
   */
  @Test
  public void s19TestwithDifferentRecType() {

    this.calDataPhyFactoryTest.getCalData(this.a2lFile, "TestFiles\\S19File_1.s19");

    this.calDataPhyFactoryTest.getCalData(this.a2lFile, "TestFiles\\S19File_9.s19");

    A2LFileInfo a2lFileInfo = this.calDataPhyFactoryTest.getA2lFileInfo();

    assertNotNull(a2lFileInfo);
  }

  /**
   * test for different rec type for hex file
   */
  @Test
  public void TestwithDifferentRecType() {

    this.calDataPhyFactoryTest.getCalData(this.a2lFile, "TestFiles\\FDS3_Group2_b1.hex");

    this.calDataPhyFactoryTest.getCalData(this.a2lFile, "TestFiles\\FDS3_Group2_b_2.hex");

    this.calDataPhyFactoryTest.getCalData(this.a2lFile, "TestFiles\\FDS3_Group2_b_5.hex");

    this.calDataPhyFactoryTest.getCalData(this.a2lFile, "TestFiles\\FDS3_Group2_b_5.hex");

    A2LFileInfo a2lFileInfo = this.calDataPhyFactoryTest.getA2lFileInfo();

    assertNotNull(a2lFileInfo);
  }


}
