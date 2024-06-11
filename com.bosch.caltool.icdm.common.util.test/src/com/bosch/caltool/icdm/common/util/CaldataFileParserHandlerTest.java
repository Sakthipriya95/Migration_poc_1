/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.junit.BeforeClass;
import org.junit.Test;

import com.bosch.calcomp.a2lparser.a2l.A2LParser;
import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.calcomp.junittestframework.JUnitTest;
import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.common.exception.CaldataFileException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CaldataFileParserHandler.CALDATA_FILE_TYPE;


/**
 * @author dmo5cob
 */
public class CaldataFileParserHandlerTest extends JUnitTest {

  private static final String INPUT_FILE_PACO = "testdata/SampleCalData.xml";
  private static final String INPUT_FILE_CDFX = "testdata/SampleCalData.cdfx";
  private static final String INPUT_FILE_DCM = "testdata/SampleCalData.dcm";

  private static final String INPUT_FILE_HEX = "testdata/HEX_M1764VDAC866_M861_280214_cleaned_IR01_V01.hex";
  private static final String INPUT_FILE_HEX_S19 = "testdata/HEX_M1764VDAC866_M861_280214_cleaned_IR01_V01.s19";
  private static final String INPUT_FILE_HEX_A2L = "testdata/A2L_DA861R4000000.a2l";

  private static final String INPUT_FILE_INVALID = "testdata/InvalidCalDataFileExtn.txt";
  private static final String INPUT_FILE_INVALID_CDFX = "testdata/InvalidCalDataFile.cdfx";
  private static final String INPUT_FILE_INVALID_DCM = "testdata/InvalidCalDataFile.dcm";
  private static final String INPUT_FILE_INVALID_PACO = "testdata/InvalidCalDataFile.xml";
  private static final String INPUT_FILE_INVALID_HEX = "testdata/InvalidCalDataFile.hex";
  private static final String INPUT_FILE_INVALID_HEX_A2L_MISMATCH = "testdata/BXAZ8ERBIIA5_X810_20894463.hex";

  private static final String LOG_MSG_MAP_SIZE = "Output map size = {}";
  private static final String LOG_MSG_CALDATA_VALUE = "Caldata value for label {} is : {}";

  private static final String TEST_MSG_CALDATA_NOT_EMPTY = "CalData map not empty";
  private static final String TEST_MSG_CALDATA_NOT_NULL = "CalData map not null";
  private static final String TEST_MSG_CONTAINS_CALDATA = "Contains CalData for label ";

  private static final String PARAM_VALUE = "AABSCONF";
  private static final String PARAM_CURVE = "BrkBst_facBrkPrsESPLL_CUR";
  private static final Object PARAM_MAP = "DMD_tiDelStrt_MAP";
  private static final String PARAM_AXIS_PT = "EpmCaS_phiTempCor05_DST";

  private static final ILoggerAdapter A2L_LOGGER = new Log4JLoggerAdapterImpl(LogManager.getLogger("A2L"));
  private static final ILoggerAdapter PARSER_LOGGER = new Log4JLoggerAdapterImpl(LogManager.getLogger("PARSER"));

  private static A2LFileInfo a2lModel;

  /**
   * Initialize common objects before all tests
   */
  @BeforeClass
  public static void initialize() {

    // Log level of parser to INFO
    A2L_LOGGER.setLogLevel(ILoggerAdapter.LEVEL_INFO);
    PARSER_LOGGER.setLogLevel(ILoggerAdapter.LEVEL_INFO);

    TESTER_LOGGER.info("Preparing A2LFileInfo for data file parsing");

    A2LParser parser = new A2LParser(A2L_LOGGER);
    parser.setFileName(INPUT_FILE_HEX_A2L);
    parser.parse();

    a2lModel = parser.getA2LFileInfo();

    TESTER_LOGGER.info("A2LFileInfo created");
  }

  /**
   * Test method for {@link CaldataFileParserHandler#getCalDataObjects(String)} <br>
   * Parse PACO file
   *
   * @throws IcdmException parsing exception
   */
  @Test
  public void testPaCoParsing() throws IcdmException {
    final CaldataFileParserHandler handler = new CaldataFileParserHandler(PARSER_LOGGER, null);
    final Map<String, CalData> cdMap = handler.getCalDataObjects(INPUT_FILE_PACO);

    assertNotNull(TEST_MSG_CALDATA_NOT_NULL, cdMap);
    TESTER_LOGGER.info(LOG_MSG_MAP_SIZE, cdMap.size());

    assertFalse(TEST_MSG_CALDATA_NOT_EMPTY, cdMap.isEmpty());

    CalData calData = cdMap.get(PARAM_AXIS_PT);

    assertNotNull(TEST_MSG_CONTAINS_CALDATA + PARAM_AXIS_PT, calData);
    TESTER_LOGGER.debug(LOG_MSG_CALDATA_VALUE, PARAM_AXIS_PT, calData.getCalDataPhy().getSimpleDisplayValue());
  }

  /**
   * Test method for {@link CaldataFileParserHandler#getCalDataObjects(CALDATA_FILE_TYPE, InputStream)} <br>
   * Parse PACO file
   *
   * @throws IcdmException parsing exception
   * @throws IOException error reading test file
   */
  @Test
  public void testPaCoParsingAsStream() throws IcdmException, IOException {
    final CaldataFileParserHandler handler = new CaldataFileParserHandler(PARSER_LOGGER, null);

    Map<String, CalData> cdMap = null;
    try (InputStream inputStream = new FileInputStream(INPUT_FILE_PACO)) {
      cdMap = handler.getCalDataObjects(CALDATA_FILE_TYPE.PACO, inputStream);
    }

    assertNotNull(TEST_MSG_CALDATA_NOT_NULL, cdMap);
    TESTER_LOGGER.info(LOG_MSG_MAP_SIZE, cdMap.size());

    assertFalse(TEST_MSG_CALDATA_NOT_EMPTY, cdMap.isEmpty());

    CalData calData = cdMap.get(PARAM_AXIS_PT);

    assertNotNull(TEST_MSG_CONTAINS_CALDATA + PARAM_AXIS_PT, calData);
    TESTER_LOGGER.debug(LOG_MSG_CALDATA_VALUE, PARAM_AXIS_PT, calData.getCalDataPhy().getSimpleDisplayValue());
  }


  /**
   * Test method for {@link CaldataFileParserHandler#getCalDataObjects(String)} <br>
   * Parse CDFx file
   *
   * @throws IcdmException parsing exception
   */
  @Test
  public void testCDFxParsing() throws IcdmException {
    final CaldataFileParserHandler handler = new CaldataFileParserHandler(PARSER_LOGGER, null);
    final Map<String, CalData> cdMap = handler.getCalDataObjects(INPUT_FILE_CDFX);

    assertNotNull(TEST_MSG_CALDATA_NOT_NULL, cdMap);
    TESTER_LOGGER.info(LOG_MSG_MAP_SIZE, cdMap.size());

    assertFalse(TEST_MSG_CALDATA_NOT_EMPTY, cdMap.isEmpty());

    CalData calData = cdMap.get(PARAM_VALUE);

    assertNotNull(TEST_MSG_CONTAINS_CALDATA + PARAM_VALUE, calData);
    TESTER_LOGGER.debug(LOG_MSG_CALDATA_VALUE, PARAM_VALUE, calData.getCalDataPhy().getSimpleDisplayValue());
  }


  /**
   * Test method for {@link CaldataFileParserHandler#getCalDataObjects(CALDATA_FILE_TYPE, InputStream)} <br>
   * Parse CDFx file
   *
   * @throws IcdmException parsing exception
   * @throws IOException error reading test file
   */
  @Test
  public void testCDFxParsingFromStream() throws IcdmException, IOException {
    final CaldataFileParserHandler handler = new CaldataFileParserHandler(PARSER_LOGGER, null);

    Map<String, CalData> cdMap = null;
    try (InputStream inputStream = new FileInputStream(INPUT_FILE_CDFX)) {
      cdMap = handler.getCalDataObjects(CALDATA_FILE_TYPE.CDFX, inputStream);
    }

    assertNotNull(TEST_MSG_CALDATA_NOT_NULL, cdMap);
    TESTER_LOGGER.info(LOG_MSG_MAP_SIZE, cdMap.size());

    assertFalse(TEST_MSG_CALDATA_NOT_EMPTY, cdMap.isEmpty());

    CalData calData = cdMap.get(PARAM_VALUE);

    assertNotNull(TEST_MSG_CONTAINS_CALDATA + PARAM_VALUE, calData);
    TESTER_LOGGER.debug(LOG_MSG_CALDATA_VALUE, PARAM_VALUE, calData.getCalDataPhy().getSimpleDisplayValue());
  }

  /**
   * Test method for {@link CaldataFileParserHandler#getCalDataObjects(String)} <br>
   * Parse DCM file
   *
   * @throws IcdmException parsing exception
   */
  @Test
  public void testDCMParsing() throws IcdmException {
    final CaldataFileParserHandler handler = new CaldataFileParserHandler(PARSER_LOGGER, null);
    final Map<String, CalData> cdMap = handler.getCalDataObjects(INPUT_FILE_DCM);

    assertNotNull(TEST_MSG_CALDATA_NOT_NULL, cdMap);
    TESTER_LOGGER.info(LOG_MSG_MAP_SIZE, cdMap.size());

    assertFalse(TEST_MSG_CALDATA_NOT_EMPTY, cdMap.isEmpty());

    CalData calData = cdMap.get(PARAM_CURVE);

    assertNotNull(TEST_MSG_CONTAINS_CALDATA + PARAM_CURVE, calData);
    TESTER_LOGGER.debug(LOG_MSG_CALDATA_VALUE, PARAM_CURVE, calData.getCalDataPhy().getSimpleDisplayValue());
  }

  /**
   * Test method for {@link CaldataFileParserHandler#getCalDataObjects(CALDATA_FILE_TYPE, InputStream)} <br>
   * Parse DCM file
   *
   * @throws IcdmException parsing exception
   * @throws IOException error reading test file
   */
  @Test
  public void testDCMParsingFromStreamFileType() throws IcdmException, IOException {
    final CaldataFileParserHandler handler = new CaldataFileParserHandler(PARSER_LOGGER, null);

    Map<String, CalData> cdMap = null;
    try (InputStream inputStream = new FileInputStream(INPUT_FILE_DCM)) {
      cdMap = handler.getCalDataObjects(CALDATA_FILE_TYPE.DCM, inputStream);
    }

    assertNotNull(TEST_MSG_CALDATA_NOT_NULL, cdMap);
    TESTER_LOGGER.info(LOG_MSG_MAP_SIZE, cdMap.size());

    assertFalse(TEST_MSG_CALDATA_NOT_EMPTY, cdMap.isEmpty());

    CalData calData = cdMap.get(PARAM_CURVE);

    assertNotNull(TEST_MSG_CONTAINS_CALDATA + PARAM_CURVE, calData);
    TESTER_LOGGER.debug(LOG_MSG_CALDATA_VALUE, PARAM_CURVE, calData.getCalDataPhy().getSimpleDisplayValue());
  }

  /**
   * Test method for {@link CaldataFileParserHandler#getCalDataObjects(String)} <br>
   * Parse HEX file
   *
   * @throws IcdmException parsing exception
   */
  @Test
  public void testHEXParsing() throws IcdmException {
    final CaldataFileParserHandler handler = new CaldataFileParserHandler(PARSER_LOGGER, a2lModel);
    final Map<String, CalData> cdMap = handler.getCalDataObjects(INPUT_FILE_HEX);

    assertNotNull(TEST_MSG_CALDATA_NOT_NULL, cdMap);
    TESTER_LOGGER.info(LOG_MSG_MAP_SIZE, cdMap.size());

    assertFalse(TEST_MSG_CALDATA_NOT_EMPTY, cdMap.isEmpty());

    CalData calData = cdMap.get(PARAM_MAP);

    assertNotNull(TEST_MSG_CONTAINS_CALDATA + PARAM_MAP, calData);
    TESTER_LOGGER.debug(LOG_MSG_CALDATA_VALUE, PARAM_MAP, calData.getCalDataPhy().getSimpleDisplayValue());
  }

  /**
   * Test method for {@link CaldataFileParserHandler#getCalDataObjects(String)} <br>
   * Parse HEX 32 file
   *
   * @throws CaldataFileException parsing exception
   */
  @Test
  public void testHEX32Parsing() throws CaldataFileException {
    // TODO find test data file
    fail("Add test");
  }

  /**
   * Test method for {@link CaldataFileParserHandler#getCalDataObjects(String)} <br>
   * Parse HEX - S19 (Motorola) file
   *
   * @throws IcdmException parsing exception
   */
  @Test
  public void testHEXS19Parsing() throws IcdmException {
    final CaldataFileParserHandler handler = new CaldataFileParserHandler(PARSER_LOGGER, a2lModel);
    final Map<String, CalData> cdMap = handler.getCalDataObjects(INPUT_FILE_HEX_S19);

    assertNotNull(TEST_MSG_CALDATA_NOT_NULL, cdMap);
    TESTER_LOGGER.info(LOG_MSG_MAP_SIZE, cdMap.size());

    assertFalse(TEST_MSG_CALDATA_NOT_EMPTY, cdMap.isEmpty());

    CalData calData = cdMap.get(PARAM_MAP);

    assertNotNull(TEST_MSG_CONTAINS_CALDATA + PARAM_MAP, calData);
    TESTER_LOGGER.debug(LOG_MSG_CALDATA_VALUE, PARAM_MAP, calData.getCalDataPhy().getSimpleDisplayValue());
  }

  /**
   * Test method for {@link CaldataFileParserHandler#getCalDataObjects(CALDATA_FILE_TYPE, InputStream)} <br>
   * Parse HEX file
   *
   * @throws IcdmException parsing exception
   * @throws IOException error reading test file
   */
  @Test
  public void testHEXParsingFromStream() throws IcdmException, IOException {
    final CaldataFileParserHandler handler = new CaldataFileParserHandler(PARSER_LOGGER, a2lModel);

    Map<String, CalData> cdMap = null;
    try (InputStream inputStream = new FileInputStream(INPUT_FILE_HEX)) {
      cdMap = handler.getCalDataObjects(CALDATA_FILE_TYPE.HEX, inputStream);
    }

    assertNotNull(TEST_MSG_CALDATA_NOT_NULL, cdMap);
    TESTER_LOGGER.info(LOG_MSG_MAP_SIZE, cdMap.size());

    assertFalse(TEST_MSG_CALDATA_NOT_EMPTY, cdMap.isEmpty());

    CalData calData = cdMap.get(PARAM_MAP);

    assertNotNull(TEST_MSG_CONTAINS_CALDATA + PARAM_MAP, calData);
    TESTER_LOGGER.debug(LOG_MSG_CALDATA_VALUE, PARAM_MAP, calData.getCalDataPhy().getSimpleDisplayValue());
  }

  /**
   * Test method for {@link CaldataFileParserHandler#getCalDataObjects(CALDATA_FILE_TYPE, InputStream)} <br>
   * Parse HEX 32 file
   *
   * @throws CaldataFileException parsing exception
   */
  @Test
  public void testHEX32ParsingFromStream() throws CaldataFileException {
    // TODO find test data file
    fail("Add test");
  }

  /**
   * Test method for {@link CaldataFileParserHandler#getCalDataObjects(CALDATA_FILE_TYPE, InputStream)} <br>
   * Parse HEX - S19 (Motorola) file
   *
   * @throws IcdmException parsing exception
   * @throws IOException error reading test file
   */
  @Test
  public void testHEXS19ParsingFromStream() throws IcdmException, IOException {
    final CaldataFileParserHandler handler = new CaldataFileParserHandler(PARSER_LOGGER, a2lModel);

    Map<String, CalData> cdMap = null;
    try (InputStream inputStream = new FileInputStream(INPUT_FILE_HEX_S19)) {
      cdMap = handler.getCalDataObjects(CALDATA_FILE_TYPE.HEX_S19, inputStream);
    }

    assertNotNull(TEST_MSG_CALDATA_NOT_NULL, cdMap);
    TESTER_LOGGER.info(LOG_MSG_MAP_SIZE, cdMap.size());

    assertFalse(TEST_MSG_CALDATA_NOT_EMPTY, cdMap.isEmpty());

    CalData calData = cdMap.get(PARAM_MAP);

    assertNotNull(TEST_MSG_CONTAINS_CALDATA + PARAM_MAP, calData);
    TESTER_LOGGER.debug(LOG_MSG_CALDATA_VALUE, PARAM_MAP, calData.getCalDataPhy().getSimpleDisplayValue());
  }

  /**
   * Test with Invalid CDFx file
   *
   * @throws IcdmException parsing error
   */
  @Test
  public void testInvalidFileCdfx() throws IcdmException {
    final CaldataFileParserHandler handler = new CaldataFileParserHandler(PARSER_LOGGER, null);
    this.thrown.expect(CaldataFileException.class);
    this.thrown.expectMessage("Error occured in Sax parser, while parsing CDF file");
    handler.getCalDataObjects(INPUT_FILE_INVALID_CDFX);
  }

  /**
   * Test with Invalid DCM file
   *
   * @throws IcdmException parsing error
   */
  @Test
  public void testInvalidFileDcm() throws IcdmException {
    final CaldataFileParserHandler handler = new CaldataFileParserHandler(PARSER_LOGGER, null);
    this.thrown.expect(CaldataFileException.class);
    this.thrown.expectMessage("Invalid DCM File");
    handler.getCalDataObjects(INPUT_FILE_INVALID_DCM);
  }

  /**
   * Test with Invalid PACO file
   *
   * @throws IcdmException parsing error
   */
  @Test
  public void testInvalidFilePaco() throws IcdmException {
    final CaldataFileParserHandler handler = new CaldataFileParserHandler(PARSER_LOGGER, null);
    this.thrown.expect(CaldataFileException.class);
    this.thrown.expectMessage("Error while parsing the Paco file");
    handler.getCalDataObjects(INPUT_FILE_INVALID_PACO);
  }

  /**
   * Test with Invalid HEX file
   *
   * @throws IcdmException parsing error
   */
  @Test
  public void testInvalidFileHex() throws IcdmException {
    final CaldataFileParserHandler handler = new CaldataFileParserHandler(PARSER_LOGGER, a2lModel);
    this.thrown.expectMessage("HEX memory is empty");
    handler.getCalDataObjects(INPUT_FILE_INVALID_HEX);
  }

  /**
   * Test with Invalid CDFx file as stream input
   *
   * @throws IOException error while creating stream
   * @throws IcdmException parsing error
   */
  @Test
  public void testInvalidFileCdfxFromStream() throws IOException, IcdmException {
    final CaldataFileParserHandler handler = new CaldataFileParserHandler(PARSER_LOGGER, null);

    this.thrown.expect(CaldataFileException.class);
    this.thrown.expectMessage("Error occured in Sax parser, while parsing CDF file");

    try (InputStream inputStream = new FileInputStream(INPUT_FILE_INVALID_CDFX)) {
      handler.getCalDataObjects(CALDATA_FILE_TYPE.CDFX, inputStream);
    }
  }

  /**
   * Test with Invalid DCM file as stream input
   *
   * @throws IOException error while creating stream
   * @throws IcdmException parsing error
   */
  @Test
  public void testInvalidFileDcmFromStream() throws IOException, IcdmException {
    final CaldataFileParserHandler handler = new CaldataFileParserHandler(PARSER_LOGGER, null);

    this.thrown.expect(CaldataFileException.class);
    this.thrown.expectMessage("Invalid DCM File");

    try (InputStream inputStream = new FileInputStream(INPUT_FILE_INVALID_DCM)) {
      handler.getCalDataObjects(CALDATA_FILE_TYPE.DCM, inputStream);
    }
  }

  /**
   * Test with Invalid PACO file as stream input
   *
   * @throws IOException error while creating stream
   * @throws IcdmException parsing error
   */
  @Test
  public void testInvalidFilePacoFromStream() throws IOException, IcdmException {
    final CaldataFileParserHandler handler = new CaldataFileParserHandler(PARSER_LOGGER, null);

    this.thrown.expect(CaldataFileException.class);
    this.thrown.expectMessage("Error while parsing the Paco file");

    try (InputStream inputStream = new FileInputStream(INPUT_FILE_INVALID_PACO)) {
      handler.getCalDataObjects(CALDATA_FILE_TYPE.PACO, inputStream);
    }
  }

  /**
   * Test with Invalid HEX file as stream input
   *
   * @throws IOException error while creating stream
   * @throws IcdmException parsing error
   */
  @Test
  public void testInvalidFileHexFromStream() throws IOException, IcdmException {
    final CaldataFileParserHandler handler = new CaldataFileParserHandler(PARSER_LOGGER, a2lModel);

    this.thrown.expectMessage("HEX memory is empty");

    try (InputStream inputStream = new FileInputStream(INPUT_FILE_INVALID_HEX)) {
      handler.getCalDataObjects(CALDATA_FILE_TYPE.HEX, inputStream);
    }
  }

  /**
   * Test with non matching hex, compared to A2L, as stream input
   *
   * @throws IOException error while creating stream
   * @throws IcdmException parsing error
   */
  @Test
  public void testInvalidFileHexFromStreamA2LHexMisMatch() throws IOException, IcdmException {
    final CaldataFileParserHandler handler = new CaldataFileParserHandler(PARSER_LOGGER, a2lModel);

    this.thrown.expectMessage(
        "HEX file Read Error : Number of axis points is bigger than maxAxisPoints in the A2L file in X-Axis of DATA_GEC_DesBas_rEgr.GEC_rEgrBasInj2Bm15_M_VW. Mismatch of A2L and HEX.");

    try (InputStream inputStream = new FileInputStream(INPUT_FILE_INVALID_HEX_A2L_MISMATCH)) {
      handler.getCalDataObjects(CALDATA_FILE_TYPE.HEX, inputStream);
    }
  }

  /**
   * Test with invalid file extn
   *
   * @throws IcdmException parsing error
   */
  @Test
  public void testInvalidFileExtnParsing() throws IcdmException {
    final CaldataFileParserHandler handler = new CaldataFileParserHandler(PARSER_LOGGER, null);

    this.thrown.expect(CaldataFileException.class);
    this.thrown.expectMessage(
        "Could not identify caldata file type 'txt'. File name/path : testdata/InvalidCalDataFileExtn.txt");

    handler.getCalDataObjects(INPUT_FILE_INVALID);
  }

  /**
   * Test with invalid file textnype as stream input
   *
   * @throws IOException stream creation error
   * @throws CaldataFileException parsing error
   */
  @Test
  public void testInvalidFileExtnCheck() throws IOException, CaldataFileException {
    this.thrown.expect(CaldataFileException.class);
    this.thrown.expectMessage(
        "Could not identify caldata file type 'txt'. File name/path : testdata/InvalidCalDataFileExtn.txt");

    CALDATA_FILE_TYPE.getTypeFromFileName(INPUT_FILE_INVALID);
  }
}
