/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.hexparser.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.junit.Before;
import org.junit.Test;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.calcomp.caldatafromhex.CalDataPhyFactory;
import com.bosch.calcomp.caldatafromhex.CalDataPhyFactoryException;
import com.bosch.calcomp.hexparser.exception.HexParserException;
import com.bosch.calcomp.hexparser.model.HexMemory;
import com.bosch.calcomp.hexparser.model.handler.HexParserDataHandler;
import com.bosch.calcomp.hexparser.util.HexParserUtil;
import com.bosch.calcomp.parser.a2l.exception.A2lParserException;
import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;

/**
 * @author SON9COB
 */
public class CalDataPhyFactoryTest {

  private CalDataPhyFactory calDataPhyFactoryTest;
  private String a2lFile;
  private String hexFile;
  private String textFile;
  private FileInputStream fileInputStream;
  private static boolean islogGeneration;
  private A2LFileInfo a2lFileInfoInst;
  private String noA2lFile;
  private String noHexFile;
  private String unknownKeywordCharType;
  private String emptyHexFile;
  private String errorHexFile;
  private String notZeroEcuAddrExtensionFile;
  private String notDefinedAxisPtRefComAxis;
  private String pWordRecLayout;
  private String notDefinedFixAxis;
  ILoggerAdapter logs = new Log4JLoggerAdapterImpl(LogManager.getLogger("Hex2Phy"));
  HexParserUtil hexParserUtil = new HexParserUtil();


  /**
   * Setting objects and files
   */
  @Before
  public void setup() {
    this.calDataPhyFactoryTest = new CalDataPhyFactory(this.logs);
    this.a2lFile = "TestFiles\\FDS3.a2l";
    this.hexFile = "TestFiles\\FDS3_Group2_b.hex";
    this.textFile = "TestFiles\\FDS3vers.txt";
    this.noA2lFile = "TestFiles\\FDS3cp.a2l";
    this.noHexFile = "TestFiles\\FDS3_Group2_bcd.hex";
    this.unknownKeywordCharType = "TestFiles\\UnknownKeywordCharType.a2l";
    this.emptyHexFile = "TestFiles\\EmptyHexMemory.hex";
    this.errorHexFile = "TestFiles\\ErrorHexFile.hex";
    this.notZeroEcuAddrExtensionFile = "TestFiles\\NotZeroECUAddrExtension.a2l";
    this.notDefinedAxisPtRefComAxis = "TestFiles\\NotDefinedAxisPtRefComAxis.a2l";
    this.pWordRecLayout = "TestFiles\\PWordRecordLayoutParameter.a2l";

    this.notDefinedFixAxis = "TestFiles\\NotDefinedFixAxis.a2l";

    this.calDataPhyFactoryTest = new CalDataPhyFactory(this.logs);
    this.calDataPhyFactoryTest.sethexFileType("hex");
    this.fileInputStream = null;
  }

  /**
   * Files are passed
   */
  @Test
  public void calDataPhyFactoryTest() {

    this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.hexFile);

    this.a2lFileInfoInst = this.calDataPhyFactoryTest.getA2lFileInfo();
    this.calDataPhyFactoryTest.getCalData(this.a2lFileInfoInst, this.hexFile);

    // Value test
    Characteristic characValue = this.a2lFileInfoInst.getAllModulesLabels().get("AADPD_CB4");
    assertEquals("Pass", "VALUE", characValue.getType());
    assertEquals("Pass", "0x808CE684", characValue.getAddress());
    assertEquals("Pass", "Val_Wu8", characValue.getDeposit());
    assertEquals("Pass", "DC_CNTR", characValue.getConversion());
    HexMemory hexmemory = new HexMemory();
    HexParserDataHandler hex = new HexParserDataHandler(hexmemory);
    assertEquals("0", String.valueOf(hex.getByteOrder()));
    assertEquals("0", String.valueOf(hex.getByteAlignment()));
    assertEquals("FDS3.a2l", this.a2lFileInfoInst.getFileName());
    assertEquals("hex", this.calDataPhyFactoryTest.gethexFileType());
    assertEquals("0", String.valueOf(hex.getFloat32Alignment()));
    assertEquals("0", String.valueOf(hex.getFloat64Alignment()));
    assertEquals("0", String.valueOf(hex.getLongAlignment()));
    assertEquals("0", String.valueOf(this.calDataPhyFactoryTest.getParseA2LTime()));
    assertNotNull(this.calDataPhyFactoryTest.getCreateCalDataTime());
    assertNotNull(this.calDataPhyFactoryTest.getParseHEXTime());
    assertEquals("0", String.valueOf(hex.getWordAlignment()));
    assertEquals(null, hexmemory.getFirstHexSegment());
    assertEquals(null, hexmemory.getNextHexSegment());
    assertNotNull(this.calDataPhyFactoryTest.getCharacteristicsList());
    assertNotNull(this.calDataPhyFactoryTest.getWarningsMap());

  }

  /**
   * Text(.txt) A2L File is passed -Fail test
   */

  @Test(expected = A2lParserException.class)
  public void textA2lFileInputFailTest() {

    this.calDataPhyFactoryTest.getCalData(this.textFile, this.hexFile);

  }

  /**
   * Text(.txt) Hex File is passed -Fail test
   */

  @Test(expected = HexParserException.class)
  public void textHexFileInputFailTest() {

    this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.textFile);

  }

  /**
   * Text(.txt) A2l & Hex File is passed -Fail test
   */

  @Test(expected = HexParserException.class)
  public void textA2lHexFileInputFailTest() {

    this.calDataPhyFactoryTest.getCalData(this.textFile, this.textFile);

  }

  /**
   * No A2lFile is found
   */
  @Test(expected = A2lParserException.class)
  public void noA2lFileInputFailTest() {

    this.calDataPhyFactoryTest.getCalData(this.noA2lFile, this.hexFile);

  }

  /**
   * No HexFile is found
   */
  @Test(expected = HexParserException.class)
  public void noHexFileInputFailTest() {

    this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.noHexFile);

  }

  /**
   * Null hex file is passed with a2l file
   */
  @Test

  public void nullHexFileA2lFailTest() {
    InputStream inputStream = null;
    try {
      inputStream = new FileInputStream(this.hexFile);

    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    this.calDataPhyFactoryTest.setInputStream(inputStream);
    this.calDataPhyFactoryTest.getCalData(this.a2lFile, null, false);
    assertNotNull(this.calDataPhyFactoryTest.getWarningsMap());
    this.a2lFileInfoInst = this.calDataPhyFactoryTest.getA2lFileInfo();
    // Val_Blk Test
    Characteristic characValBlk = this.a2lFileInfoInst.getAllModulesLabels().get("ACC_ON_GEARTB");
    assertEquals("Pass", "VAL_BLK", characValBlk.getType());
    assertEquals("Pass", "0x808EA7A3", characValBlk.getAddress());
    assertEquals("Pass", "ValA_Wu8", characValBlk.getDeposit());
    assertEquals("Pass", "1.000", characValBlk.getMaxDiff());
    assertNotNull(this.calDataPhyFactoryTest.getWarningsMap());
  }

  /**
   * Null hex file is passed with a2l file info
   */
  @Test

  public void nullHexFileA2lFileInfoFailTest() {
    InputStream inputStream = null;
    try {
      inputStream = new FileInputStream(this.hexFile);

    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    this.calDataPhyFactoryTest.setInputStream(inputStream);
    this.calDataPhyFactoryTest.getCalData(this.a2lFileInfoInst, null, false);
    assertNotNull(this.calDataPhyFactoryTest.getWarningsMap());
    this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.hexFile);
    // Map Test
    A2LFileInfo a2lFileInfo = this.calDataPhyFactoryTest.getA2lFileInfo();
    Characteristic characteristic = a2lFileInfo.getAllModulesLabels().get("GlwCtl_rPst1_MAP");
    assertEquals("Prc", characteristic.getConversion());
    assertEquals("Map_Xs16Ys16Ws16", characteristic.getDeposit());
  }

  /**
   * HexParserException with error hex file
   */
  @Test(expected = HexParserException.class)
  public void errorHexFileFailNullIpStreamTest() {
    InputStream inputStream = null;
    try {
      inputStream = new FileInputStream(this.errorHexFile);

    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    this.calDataPhyFactoryTest.setInputStream(inputStream);
    this.calDataPhyFactoryTest.getCalData(this.a2lFile, null, false);

  }

  /**
   * InputStream test case
   */

  @Test
  public void fileInputStreamTest() {

    try {
      this.calDataPhyFactoryTest.setInputStream(new FileInputStream(this.hexFile));
      assertNotNull(this.calDataPhyFactoryTest.getInputStream());
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }

  }

  /**
   * Null Object is passed as input -Fail Test
   */
  @Test(expected = HexParserException.class)

  public void inputStreamFailTest() {
    this.calDataPhyFactoryTest.setInputStream(this.fileInputStream);

  }

  /**
   * Test case for double parameter constructor - (null,true)
   */

  @Test
  public void doubleParameterConditionTrueTest() {
    islogGeneration = true;
    CalDataPhyFactory calDataPhyFactory = new CalDataPhyFactory(null, islogGeneration);
    assertNotNull(calDataPhyFactory.getCalData(this.a2lFile, this.hexFile));
  }

  /**
   * Test case for double parameter constructor - (null,false)-No log files will be generated
   */

  @Test
  public void doubleParameterConditionFalseTest() {
    islogGeneration = false;
    CalDataPhyFactory calDataPhyFactory = new CalDataPhyFactory(null, islogGeneration);
    assertNotNull(calDataPhyFactory.getCalData(this.a2lFile, this.hexFile));
  }

  /**
   * Unknown keyword in Characteristic type
   */
  @Test(expected = A2lParserException.class)
  public void unknownKeywordCharTypeFailTest() {

    this.calDataPhyFactoryTest.getCalData(this.unknownKeywordCharType, this.hexFile);

  }

  /**
   * Hex Memory is empty
   */
  @Test(expected = HexParserException.class)
  public void emptyHexFailTest() {

    this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.emptyHexFile);

  }

  /**
   * Error in hexfile is passed with a2l file
   */
  @Test(expected = HexParserException.class)
  public void errorHexFailTest() {

    this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.errorHexFile);

  }

  /**
   * Error in hexfile is passed with a2lfileinfo
   */
  @Test(expected = HexParserException.class)
  public void errorHexFileFailTest() {

    this.calDataPhyFactoryTest.getCalData(this.a2lFileInfoInst, this.errorHexFile);

  }

  /**
   * CalDataPhyFactoryException.ECU_ADDRESS_EXTENSION_NOT_ZERO
   */
  @Test(expected = CalDataPhyFactoryException.class)
  public void notZeroECUAddrExtensionTest() {

    this.calDataPhyFactoryTest.getCalData(this.notZeroEcuAddrExtensionFile, this.hexFile);

  }


  /**
   * AXIS_POINT_REF not defined in COM_AXIS in genComAxisMain()
   */
  @Test(expected = CalDataPhyFactoryException.class)
  public void notDefinedAxisPtRefTest() {

    this.calDataPhyFactoryTest.getCalData(this.notDefinedAxisPtRefComAxis, this.hexFile);

  }

  /**
   * Added Pword in record_layout
   */
  @Test(expected = CalDataPhyFactoryException.class)
  public void pWordRecLayoutest() {

    this.calDataPhyFactoryTest.getCalData(this.pWordRecLayout, this.hexFile);

  }

//  /**
//   * Specified CompuMethod not defined in STD_AXIS in genStdAxisMain()
//   */
//  @Test(expected = CalDataPhyFactoryException.class)
//  public void notDefinedCompuMethodStdAxisTest() {
//
//    this.calDataPhyFactoryTest.getCalData(this.notDefinedCompuMethodStdAxis, this.hexFile);
//
//  }
//
//  /**
//   * Specified CompuMethod not defined in FIX_AXIS in genFixAxis() java.lang.AssertionError: Expected exception: com.bosch.calcomp.caldatafromhex.CalDataPhyFactoryException
//
//
//   */
//  @Test(expected = com.bosch.calcomp.caldatafromhex.CalDataPhyFactoryException.class)
//  public void notDefinedCompuMethodFixAxisTest() {
//
//    this.calDataPhyFactoryTest.getCalData(this.notDefinedCompuMethodFixAxis, this.hexFile);
//
//  }
//
  /**
   * Fix axis not defined for characteristic in genFixAxis()
   */
  @Test

  (expected = CalDataPhyFactoryException.class)
  public void notDefinedFixAxisTest() {

    this.calDataPhyFactoryTest.getCalData(this.notDefinedFixAxis, this.hexFile);

  }

}
