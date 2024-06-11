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
import com.bosch.calcomp.calutil.tools.CalFileReader;
import com.bosch.calcomp.hexparser.IHexParser;
import com.bosch.calcomp.hexparser.IntelHexParser;
import com.bosch.calcomp.hexparser.MotorolaS19Parser;
import com.bosch.calcomp.hexparser.exception.HexParserException;
import com.bosch.calcomp.hexparser.factory.HexParserFactory;
import com.bosch.calcomp.hexparser.model.HexMemory;
import com.bosch.calcomp.hexparser.model.handler.HexParserDataHandler;
import com.bosch.calcomp.hexparser.process.IntelHexRecord;
import com.bosch.calcomp.hexparser.process.MotorolaS19FileRecord;
import com.bosch.calcomp.hexparser.util.HexParserConstants;
import com.bosch.calcomp.hexparser.util.HexParserUtil;
import com.bosch.calcomp.hexparser.util.MotorolaS19ParserUtil;
import com.bosch.calmodel.a2ldata.A2LFileInfo;

/**
 * @author SON9COB
 */
public class HexParserTest {

  private CalDataPhyFactory calDataPhyFactoryTest;
  private String a2lFile;
  private String invalidS5a2lFile;
  private String hexFile;
  private CalFileReader file;
  private IntelHexParser parser;
  private MotorolaS19Parser motoParser;
  private MotorolaS19FileRecord record;
  private String noFile;
  private String noS19File;
  private String s19File;
  private String invalidHexRecS19File;
  private String invalidS5Record;
  private String parseErrS19File;
  private String noTerminationS19File;
  private String s19ErrFile;
  private String invalidExtensionTypeFile;
  private ILoggerAdapter logger;
  private String s6S19File;
  private String s1ByteAddrS19File;
  private String s2ByteAddrS19File;
  private String s3ByteAddrS19File;
  private String s19ByteAtFile;
  private String notValidExtensionWithS19;
  private String notValidExtensionWithHex;
  private String invalidByteAddrS19File;
  private String startLinearAddrS19File;
  private String invalidHexRecType;
  private String overlapDiffDataHex;
  private String LOGGER_PATTERN;
  private String DEFAULT_LOG_PATH;
  private final HexParserUtil hexParserUtil = new HexParserUtil();
  private final MotorolaS19ParserUtil motorolaS19ParserUtil = new MotorolaS19ParserUtil();
  ILoggerAdapter logs = new Log4JLoggerAdapterImpl(LogManager.getLogger("Hex2Phy"));


  /**
   * Setting objects and files
   */
  @Before
  public void setUp() {
    this.calDataPhyFactoryTest = new CalDataPhyFactory(this.logs);

    this.a2lFile = "TestFiles\\Annotation_test2.a2l";
    this.hexFile = "TestFiles\\FDS3_Group2_b.hex";
    this.file = null;
    this.noFile = "TestFiles\\1.hex";
    this.noS19File = "TestFiles\\1.s19";
    this.s19File = "TestFiles\\S19File.s19";
    this.invalidHexRecS19File = "TestFiles\\InvalidHexRecS19File.s19";
    this.parseErrS19File = "TestFiles\\ParseErrS19File.s19";
    this.noTerminationS19File = "TestFiles\\NoTerminationS19File.s19";
    this.s19ErrFile = "TestFiles\\S19ErrFile.s19";
    this.s6S19File = "TestFiles\\S6S19Record.s19";
    this.s1ByteAddrS19File = "TestFiles\\S1ByteAddrS19.s19";
    this.s2ByteAddrS19File = "TestFiles\\S2ByteAddrS19.s19";
    this.s3ByteAddrS19File = "TestFiles\\S3ByteAddrS19.s19";
    this.invalidByteAddrS19File = "TestFiles\\InvalidByteAddrS19.s19";
    this.invalidExtensionTypeFile = "TestFiles\\S19ErrFile.hix";
    this.notValidExtensionWithS19 = "TestFiles\\NotValidExtensionWithS19.hix";
    this.notValidExtensionWithHex = "TestFiles\\NotValidExtensionWithHex.hix";
    this.s19ByteAtFile = "TestFiles\\S19ByteAt.s19";
    this.LOGGER_PATTERN = "[%-5p] - %d{dd MMM yyyy, HH:mm:ss} - %m%n";
    this.DEFAULT_LOG_PATH = "C:/temp/CalDataPhyFactory.log";
    this.logger = new Log4JLoggerAdapterImpl(this.DEFAULT_LOG_PATH, this.LOGGER_PATTERN);
    this.startLinearAddrS19File = "TestFiles\\StartLinearAddrRec.hex";
    this.invalidHexRecType = "TestFiles\\InvalidRecHex.hex";
    this.overlapDiffDataHex = "TestFiles\\OverlapDiffDataHex.hex";
    this.invalidS5Record ="TestFiles\\invalids5record.s19";
    this.invalidS5a2lFile ="TestFiles\\EHANDBOOK_FlexECU-Demo.a2l";
  }

  /**
   * Valid File input are passed
   */
  @Test
  public void characteristicsFileInputTest() {

    this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.hexFile);
    assertNotNull(this.calDataPhyFactoryTest.getA2lFileInfo());

    if (this.file == null) {
      // ALM-280807
      try {
        this.file = new CalFileReader(new FileInputStream(this.hexFile));
        IntelHexRecord intelRecord = new IntelHexRecord(this.file.getLine(), this.logger, this.hexParserUtil);
        assertNotNull(intelRecord.getChecksum());
        assertEquals(false, intelRecord.isChecksumValid());
        IntelHexParser hexParser = new IntelHexParser(this.hexFile, this.logger, this.hexParserUtil);
        assertEquals(-1, hexParser.getLineCount());
        assertNotNull(hexParser.getInputStream());
        assertNotNull(hexParser.getWarningsMap());
      }
      catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * No Hex file is found in the location
   */
  @Test(expected = HexParserException.class)
  public void fileNotFoundTest() {
    this.parser = new IntelHexParser(this.noFile, this.logger, this.hexParserUtil);
    this.parser.parse();
  }

  /**
   * NullInputStreamTest
   */
  @Test(expected = HexParserException.class)
  public void nullInputStreamTest() {
    InputStream inputStream = null;
    this.parser = new IntelHexParser(inputStream, this.logger, this.hexParserUtil);
    this.parser.setInputStream(inputStream);
    this.parser.parse();
  }

  /**
   * set IpStream for CaldataPhyFactory
   */
  @Test

  public void setIpStreamTest() {

    try {
      this.calDataPhyFactoryTest.setInputStream(new FileInputStream(this.s19File));
      this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.s19File);
      A2LFileInfo a2lFileInfo = this.calDataPhyFactoryTest.getA2lFileInfo();
      assertEquals("P695", a2lFileInfo.getProjectName());
      assertNotNull(a2lFileInfo.getA2lFileSize());
      assertNotNull(this.calDataPhyFactoryTest.getWarningsMap());
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * InputStream Test
   */
  @Test

  public void inputStreamTest() {
    try {
      MotorolaS19Parser motoS19Parser = new MotorolaS19Parser(new FileInputStream(this.s19File), this.logger);
      String currentLine = "S00600004844521B";

      HexMemory parse = motoS19Parser.parse();
      assertNotNull(parse.getFirstHexBlock());
      assertNotNull(motoS19Parser.getInputStream());
      assertEquals(61192, motoS19Parser.getLineCount());
      this.record = new MotorolaS19FileRecord(currentLine, this.logger, this.hexParserUtil,0);
      this.motorolaS19ParserUtil.setMotorolaS19TerminationInfo(this.record.getRecType(), this.record.getBinData());
      this.motorolaS19ParserUtil.setMotorolaS19Header(this.record.getBinData());
      assertNotNull(this.motorolaS19ParserUtil.getMotorolaS19TerminationData());
      assertNotNull(this.motorolaS19ParserUtil.getMotorolaS19HeaderData());
      assertNotNull(this.record.getSaddress());
      assertEquals(false, this.record.isChecksumValid());
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * HexParserException in parse() in MotorolaS19Parser class
   */
  @Test(expected = HexParserException.class)
  public void noS19FileTest() {
    this.motoParser = new MotorolaS19Parser(this.noS19File, this.logger);
    this.motoParser.parse();
    this.motoParser.getWarningsMap();
  }

  /**
   * HexParserException in readMotorolaS19Record() in MotorolaS19FileRecordReader class
   */
  @Test(expected = HexParserException.class)
  public void invalidHexRecordTest() {
    this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.invalidHexRecS19File);
  }

  /**
   * HexParserException in parse() in MotorolaS19Parser class
   */
  @Test
  public void parseFailTest() {
    this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.parseErrS19File);
  }

  /**
   * HexParserException in parse() in MotorolaS19Parser class when the termination record is not found throw an
   * exception
   */
  @Test(expected = HexParserException.class)
  public void notFoundterminationrecordTest() {
    this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.noTerminationS19File);
  }

  /**
   * HexParserException in validateEndOfFile() in MotorolaS19ParserUtil class
   */
  @Test(expected = HexParserException.class)
  public void validateEndOfFileTest() {
    this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.s19ErrFile);
  }

  /**
   *
   *
   */
  @Test
  public void invalidExtensionTypeFile() {

    try {
      this.calDataPhyFactoryTest.setInputStream(new FileInputStream(this.invalidExtensionTypeFile));
      assertNotNull(this.calDataPhyFactoryTest.getWarningsMap());
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }

  }

  /**
   * HexMemory First and next segment
   */
  @Test

  public void hexMemoryTest() {
    HexParserFactory hexParserFactory = new HexParserFactory();

    IHexParser hexParser = null;
    hexParser = hexParserFactory.createHexParser(this.hexFile, this.logger);
    if (hexParser != null) {
      // parse the HEX File
      try {
        hexParser.parse();
      }

      catch (HexParserException e) {
        throw new HexParserException("*** unexpected HexParser error!", HexParserException.UNEXPECTED_ERROR, e);
      }
      HexMemory hexMemory = hexParser.getHexMemory();
      assertNotNull(hexMemory.getFirstHexSegment());
      assertNotNull(hexMemory.getNextHexSegment());
    }
  }

  /**
   * Added S6 record in S19 File
   */

  public void s6S19FileTest() {
    assertNotNull(this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.s6S19File));
    assertNotNull(this.calDataPhyFactoryTest.getCharacteristicsList());
    assertEquals(12, this.calDataPhyFactoryTest.getParseHEXTime());
  }

  /**
   * HexParserException in getByteAt() in HexParserUtil class
   */
  @Test(expected = HexParserException.class)
  public void s19ByteAtFailTest() {
    String sRecord = "S00600004844521B";
    this.hexParserUtil.getByteAt(sRecord, 0, this.logger);
    this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.s19ByteAtFile);
  }

  /**
   * HexParserException in getWordAt() in HexParserUtil class
   */
  @Test(expected = HexParserException.class)
  public void s19WordAtFailTest() {
    String sRecord = "S00600004844521B";
    this.hexParserUtil.getWordAt(sRecord, 0, this.logger);
    this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.s19ByteAtFile);
  }

  /**
   * HexParserException in getD6WordAt() in HexParserUtil class
   */
  @Test(expected = HexParserException.class)
  public void s19D6WordAtFailTest() {
    String sRecord = "S00600004844521B";
    this.hexParserUtil.getD6WordAt(sRecord, 0, this.logger);
    this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.s19ByteAtFile);
  }

  /**
   * HexParserException in getDWordAt() in HexParserUtil class
   */
  @Test(expected = HexParserException.class)
  public void s19DWordAtFailTest() {
    String sRecord = "S00600004844521B";
    this.hexParserUtil.getDWordAt(sRecord, 0, this.logger);
    this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.s19ByteAtFile);
  }

  /**
   * ".s19" and ".h32" extension files in createHexParser method HexParserFactory class
   */
  @Test

  public void extensionS19Test() {

    this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.s19File);
    this.motoParser = new MotorolaS19Parser(this.s19File, this.logger);
    HexParserFactory factory = new HexParserFactory();
    try {
      factory.createHexParser(new FileInputStream(this.s19File), HexParserConstants.INTEL_H32_FILE_EXTENSION,
          this.logger);
      factory.createHexParser(new FileInputStream(this.s19File), ".s19", this.logger);
      assertNotNull(this.motoParser.getWarningsMap());
      assertNotNull(this.motoParser.getMessageBuffer());

    }

    catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * Unable to identify hex type
   */
  @Test(expected = HexParserException.class)
  public void notFoundHexTypeTest() {
    this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.s6S19File);
    // IHexParser iHexParser = new
    HexParserFactory factory = new HexParserFactory();
    try {
      factory.createHexParser(new FileInputStream(this.s19File), this.s19File, this.logger);
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * HexParserException in datahandler class
   */
  @Test(expected = HexParserException.class)
  public void dataHandlerTest() {
    IHexParser hexParser = null;
    HexParserFactory factory = new HexParserFactory();
    hexParser = factory.createHexParser(this.hexFile, this.logger);
    hexParser.parse();
    HexMemory hexMemory = hexParser.getHexMemory();
    HexParserDataHandler dataHandler = new HexParserDataHandler(hexMemory);
    assertNotNull(dataHandler.getByte());
    assertNotNull(dataHandler.getHexByte());
    assertNotNull(dataHandler.getHexByteAt("S00600004844521B"));
  }

  /**
   * S1_2BYTE_ADDRESS_RECORD in validateEndOfFile() in MotorolaS19ParserUtil class
   */
  @Test(expected = HexParserException.class)
  public void s12ByteAddressRecordTest() {

    this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.s1ByteAddrS19File);
  }

  /**
   * S2_3BYTE_ADDRESS_RECORD in validateEndOfFile() in MotorolaS19ParserUtil class
   */
  @Test(expected = HexParserException.class)
  public void s22ByteAddressRecordTest() {

    this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.s2ByteAddrS19File);
  }

  /**
   * S3_3BYTE_ADDRESS_RECORD in validateEndOfFile() in MotorolaS19ParserUtil class
   */
  @Test(expected = HexParserException.class)
  public void s34ByteAddressRecordTest() {

    this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.s3ByteAddrS19File);
  }

  /**
   * Invalid BYTE ADDRESS RECORD in validateEndOfFile() in MotorolaS19ParserUtil class
   */
  @Test(expected = HexParserException.class)
  public void invalidByteAddressRecordTest() {

    this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.invalidByteAddrS19File);
  }

  /**
   * InvalidHixExtensionS19FileTest in determineHexParser() in HexParserUtil class
   */
  @Test(expected = HexParserException.class)
  public void invalidHixExtensionS19FileTest() {

    this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.notValidExtensionWithS19);
  }

  /**
   * InvalidHixExtensionHexFileTest in determineHexParser() in HexParserUtil class
   */
  @Test(expected = HexParserException.class)
  public void invalidHixExtensionHexFileTest() {

    this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.notValidExtensionWithHex);
  }

  /**
   * Included START_LINEAR_ADDRESS_RECORD, START_SEGMENT_ADDRESS_RECORD, EXTENDED_SEGMENT_ADDRESS_RECORD in parse() in
   * IntelHexParser class
   */
  @Test

  public void startLinearAddrRecHexFileTest() {
    assertNotNull(this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.startLinearAddrS19File));
    assertNotNull(this.calDataPhyFactoryTest.getA2lFileInfo());
  }

  /**
   * Invalid HexRecType in parse() in IntelHexParser class
   */
  @Test

  public void invalidRecTypeHexFileTest() {
    this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.invalidHexRecType);
    assertNotNull(this.calDataPhyFactoryTest.getA2lFileInfo());
    assertNotNull(this.calDataPhyFactoryTest.getParseA2LTime());
  }

  /**
   * Overlapping with different data in parseDataRecordType() in IntelHexParser class
   */
  @Test(expected = HexParserException.class)
  public void overlapDiffDataHexTest() {

    this.calDataPhyFactoryTest.getCalData(this.a2lFile, this.overlapDiffDataHex);
  }

  @Test
  public void testinvalidS5Record()
  {
    this.calDataPhyFactoryTest.getCalData(this.invalidS5a2lFile, this.invalidS5Record);
  
  }
}