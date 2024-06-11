/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.monicareportparser.test;

import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.bosch.calcomp.junittestframework.JUnitTest;
import com.bosch.caltool.monicareportparser.MonitoringToolParser;
import com.bosch.caltool.monicareportparser.data.MonitoringToolOutput;
import com.bosch.caltool.monicareportparser.data.ParameterInfo;
import com.bosch.caltool.monicareportparser.exception.MonicaRptParserException;
import com.bosch.caltool.monicareportparser.util.MonicaRptParCons;


/**
 * @author rgo7cob
 */
public class TestMonicaReportParser extends JUnitTest {

  /**
   * number of params
   */
  private static final int NUM_PARAMS = 2;


  /**
   * REV_STATUS
   */
  private static final String REV_STATUS = "Not ok";


  /**
   * A2L_PATH A2L_PATH
   */
  private static final String A2L_PATH =
      "C:\\Users\\rek5fe\\Desktop\\Temp\\iCDM_Testregel\\P1541_MR020_internal_Mo.a2l";


  /**
   * A2L_PATH A2L_PATH
   */
  private static final String DCM_PATH =
      "C:\\Users\\rek5fe\\Desktop\\Temp\\iCDM_Testregel\\P1541_MR020_2_DS_with_WK_V2_4-16.03.15_12-45-23_V1.3.47 rc00.dcm";
  /**
   * MoniCa report file name
   */
  private static final String FILE_NAME = "data\\iCDM_Template_with_Named_Cells.xlsx";

  private static final String SHEET_NAME_1 = "Dataset_1-iCDM_Check";

  private static final String SHEET_NAME_2 = "Dataset_2-iCDM_Check";

  private static final String SHEET_NAME_3 = "Dataset_3-iCDM_Check";

  private static final String SHEET_NAME_4 = "Dataset_4-iCDM_Check";

  private static final String INVALID_SHEET_NAME = "Dataset_4-iCDM_Checks";

  /**
   * Test to get the sheets
   */
  @Test
  public void testSheets() {
    MonitoringToolParser parser = getParser();
    List<String> allSheetNames = parser.getAllSheetNames(FILE_NAME);
    assertTrue("Sheets are available", !allSheetNames.isEmpty());
  }

  //
  /**
   * Test to get the sheets
   *
   * @throws IOException thrown while creating file input stream
   * @throws MonicaRptParserException thrown while parsing
   */
  @Test
  public void testInvalidSheet() throws IOException, MonicaRptParserException {

    this.thrown.expect(MonicaRptParserException.class);
    this.thrown.expectMessage(startsWith(MonicaRptParCons.ERR_SHEET_NOT_FOUND));
    MonitoringToolParser parser = getParser();
    InputStream strm = new FileInputStream(new File(FILE_NAME));
    parser.setInputStream(strm);
    parser.parse(TestMonicaReportParser.INVALID_SHEET_NAME);

  }

  /**
   * @param fileName fileName
   * @return the parser object
   */
  private MonitoringToolParser getParser() {
    return new MonitoringToolParser(AUT_LOGGER);
  }

  /**
   * Test to get the sheets
   *
   * @throws MonicaRptParserException exception during file parsing
   * @throws IOException exception during file reading
   */
  @Test
  public void testParser01() throws MonicaRptParserException, IOException {
    MonitoringToolParser parser = getParser();

    try (InputStream strm = new FileInputStream(new File(FILE_NAME))) {
      parser.setInputStream(strm);
      parser.parse(TestMonicaReportParser.SHEET_NAME_4);

      MonitoringToolOutput output = parser.getOutput();
      String a2lFilePath = output.getA2lFilePath();
      String dcmFilePath = output.getDcmFilePath();
      String reviewStatus = output.getReviewStatus();
      Map<String, ParameterInfo> paramInfoMap = output.getParamInfoMap();
      assertEquals(A2L_PATH, a2lFilePath);
      assertEquals(DCM_PATH, dcmFilePath);
      assertEquals(REV_STATUS.toLowerCase(), reviewStatus.toLowerCase());
      assertEquals(NUM_PARAMS, paramInfoMap.size());

    }
  }


  /**
   * Test to Validate whether the a2l file path available in MoniCa excel
   *
   * @throws IOException thrown while creating file input stream
   * @throws MonicaRptParserException thrown while parsing
   */
  @Test
  public void testParser02() throws IOException, MonicaRptParserException {
    this.thrown.expect(MonicaRptParserException.class);
    this.thrown.expectMessage(startsWith(MonicaRptParCons.ERR_NO_A2L));
    MonitoringToolParser parsers = getParser();

    InputStream strm = new FileInputStream(new File(FILE_NAME));
    parsers.setInputStream(strm);
    parsers.parse(TestMonicaReportParser.SHEET_NAME_3);

  }

  /**
   * Test to Validate whether the DCM file path available in MoniCa excel
   *
   * @throws IOException thrown while creating file input stream
   * @throws MonicaRptParserException thrown while parsing
   */
  @Test
  public void testParser03() throws IOException, MonicaRptParserException {
    this.thrown.expect(MonicaRptParserException.class);
    this.thrown.expectMessage(startsWith(MonicaRptParCons.ERR_NO_DCM));
    MonitoringToolParser parsers = getParser();
    InputStream strm = new FileInputStream(new File(FILE_NAME));
    parsers.setInputStream(strm);
    parsers.parse(TestMonicaReportParser.SHEET_NAME_2);

  }

  /**
   * Test to Validate whether the Label is available in MoniCa excel
   *
   * @throws IOException thrown while creating file input stream
   * @throws MonicaRptParserException thrown while parsing
   */
  @Test
  public void testParser04() throws IOException, MonicaRptParserException {
    this.thrown.expect(MonicaRptParserException.class);
    this.thrown.expectMessage(startsWith(MonicaRptParCons.ERR_NO_LABEL));
    MonitoringToolParser parsers = getParser();
    InputStream strm = new FileInputStream(new File(FILE_NAME));
    parsers.setInputStream(strm);
    parsers.parse(TestMonicaReportParser.SHEET_NAME_1);

  }

  /**
   * Test to Validate whether if the MoniCa stream is null
   *
   * @throws MonicaRptParserException thrown while parsing
   */
  @Test
  public void testParser05() throws MonicaRptParserException {
    this.thrown.expect(MonicaRptParserException.class);
    this.thrown.expectMessage(startsWith(MonicaRptParCons.ERR_IN_MONICA_INPUTSTREAM));
    MonitoringToolParser parsers = getParser();
    parsers.setInputStream(null);
    parsers.parse(TestMonicaReportParser.SHEET_NAME_1);

  }


  /**
   * Test to Validate whether if a closed MoniCa stream is sent as input
   *
   * @throws IOException thrown while creating file input stream
   * @throws MonicaRptParserException thrown while parsing
   */
  @Test
  public void testParser06() throws IOException, MonicaRptParserException {
    this.thrown.expect(MonicaRptParserException.class);
    this.thrown.expectMessage(startsWith(MonicaRptParCons.ERR_READ_MONICA_STREAM));
    MonitoringToolParser parsers = getParser();
    InputStream strm = new FileInputStream(new File(FILE_NAME));
    strm.close();
    parsers.setInputStream(strm);
    parsers.parse(TestMonicaReportParser.SHEET_NAME_4);

  }
}
