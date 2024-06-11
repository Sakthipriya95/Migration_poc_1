/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.labfunparser.textparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.bosch.calcomp.junittestframework.JUnitTest;
import com.bosch.caltool.labfunparser.exception.ParserException;
import com.bosch.caltool.labfunparser.textparser.FileParserConstants.INPUT_FILE_TYPE;


/**
 * @author adn1cob
 */
public class TestInputFileParser extends JUnitTest {

  private static final String SAMPLE_FILE_ROOT_DIR = "testfiles/";

  /*
   * Test 01 constants
   */
  private static final String TEST01_SAMPLE_FILE = SAMPLE_FILE_ROOT_DIR + "Test01.lab";
  private static final String TEST01_FUNNAME_01 = "Function1";
  private static final String TEST01_LABELNAME_01 = "Label01";


  /*
   * Test 02 constants
   */
  private static final String TEST02_SAMPLE_FILE = SAMPLE_FILE_ROOT_DIR + "Test02.fun";
  private static final String TEST02_LABELNAME_01 = "Label02";
  private static final String TEST02_FUNNAME_01 = "Function2";

  /*
   * Test 03 constants
   */
  private static final String TEST03_INVALID_FILE = "FileDoesNotExist.lab";


  /*
   * Test 06 constants
   */
  private static final String TEST06_SAMPLE_FILE_01 = SAMPLE_FILE_ROOT_DIR + "FC_PA_MQBCAN_CANFD_Fxx_with_BOM.fun";
  private static final String TEST06_SAMPLE_FILE_02 = SAMPLE_FILE_ROOT_DIR + "FC_PA_MQBCAN_CANFD_Fxx_without_BOM.fun";

  private static final String TEST06_FUNNAME_01 = "APP_Kickdown";

  /*
   * Test 08 constants
   */
  private static final String TEST08_INVALID_FILE_EXTN = "InvalidExtn.label";

  /*
   * Test 11 constants
   */
  private static final String TEST11_SAMPLE_FILE_01 = SAMPLE_FILE_ROOT_DIR + "lab_4_1.lab";
  private static final String TEST11_LABEL_NAME = "AirMod_facTDifAirFil_T";
  private static final String TEST11_FUNC_NAME = "SeqStrt";
  private static final String TEST11_GRP_NAME = "_WP__6_2_2__Kraftstoff___Injektor_Zylinder";

  /**
   * Test a LAB file
   *
   * @throws ParserException exception during file parsing
   */
  @Test
  public void test01() throws ParserException {
    InputFileParser parser = new InputFileParser(AUT_LOGGER, TEST01_SAMPLE_FILE);
    parser.parse();

    // Test Labels
    List<String> labels = parser.getLabels();
    assertFalse("Labels are available in file 1", labels.isEmpty());
    assertTrue("Test a sample label's availability in file 1", labels.contains(TEST01_LABELNAME_01));
    assertEquals("fileType is lab", INPUT_FILE_TYPE.LAB, parser.getFileType());

    // Test functions
    List<String> functions = parser.getFunctions();
    assertFalse("Functions are available in file 1", functions.isEmpty());
    assertTrue("Test a sample function's availability in file 1", functions.contains(TEST01_FUNNAME_01));

  }

  /**
   * Test a FUN file
   *
   * @throws ParserException exception during file parsing
   */
  @Test
  public void test02() throws ParserException {
    InputFileParser parser = new InputFileParser(AUT_LOGGER, TEST02_SAMPLE_FILE);
    parser.parse();

    // Test Labels
    List<String> labels = parser.getLabels();
    assertFalse("Labels are available in file 2", labels.isEmpty());
    assertTrue("Test a sample label's availability in file 2", labels.contains(TEST02_LABELNAME_01));

    // Test functions
    List<String> functions = parser.getFunctions();
    assertFalse("Functions are available in file 2", functions.isEmpty());
    assertTrue("Test a sample function's availability in file 2", functions.contains(TEST02_FUNNAME_01));

  }

  /**
   * Negative test. try to parse a file that does not exist
   *
   * @throws ParserException exception during file parsing
   */
  @Test
  public void test03() throws ParserException {
    this.thrown.expect(ParserException.class);
    this.thrown.expectMessage("Error reading file");
    new InputFileParser(AUT_LOGGER, TEST03_INVALID_FILE).parse();
  }


  /**
   * Test a LAB file with stream input
   *
   * @throws ParserException exception during file parsing
   * @throws IOException exception during stream creation
   */
  @Test
  public void test04() throws ParserException, IOException {
    List<String> labels = new ArrayList<>();
    List<String> functions = new ArrayList<>();

    try (InputStream in = new FileInputStream(TEST01_SAMPLE_FILE)) {
      InputFileParser parser = new InputFileParser(AUT_LOGGER, in, TEST01_SAMPLE_FILE);
      parser.parse();
      labels = parser.getLabels();
      functions = parser.getFunctions();
    }

    // Test Labels
    assertFalse("Labels are available in file 1", labels.isEmpty());
    assertTrue("Test a sample label's availability in file 1", labels.contains(TEST01_LABELNAME_01));

    // Test functions
    assertFalse("Functions are available in file 1", functions.isEmpty());
    assertTrue("Test a sample function's availability in file 1", functions.contains(TEST01_FUNNAME_01));

  }

  /**
   * Test a FUN file with stream input
   *
   * @throws ParserException exception during file parsing
   * @throws IOException exception during stream creation
   */
  @Test
  public void test05() throws ParserException, IOException {

    List<String> labels = new ArrayList<>();
    List<String> functions = new ArrayList<>();

    try (InputStream in = new FileInputStream(TEST02_SAMPLE_FILE)) {
      InputFileParser parser = new InputFileParser(AUT_LOGGER, in, TEST02_SAMPLE_FILE);
      parser.parse();
      labels = parser.getLabels();
      functions = parser.getFunctions();
    }

    // Test Labels
    assertFalse("Labels are available in sample file 2", labels.isEmpty());
    assertTrue("Test a sample label's availability in sample file 2", labels.contains(TEST02_LABELNAME_01));

    // Test functions
    assertFalse("Functions are available in sample file 2", functions.isEmpty());
    assertTrue("Test a sample function's availability in sample file 2", functions.contains(TEST02_FUNNAME_01));

  }

  /**
   * TestCase to Read Fun file with BOM
   *
   * @throws ParserException as Exception
   * @throws IOException as Exception
   */
  @Test
  public void test06() throws ParserException, IOException {

    List<String> functions = new ArrayList<>();

    try (InputStream in = new FileInputStream(TEST06_SAMPLE_FILE_01)) {
      InputFileParser parser = new InputFileParser(AUT_LOGGER, in, TEST06_SAMPLE_FILE_01);
      parser.parse();
      functions = parser.getFunctions();
    }

    // Test functions
    assertFalse("Functions are available in sample file 1", functions.isEmpty());
    assertTrue("Test a sample function's availability in sample file 1", functions.contains(TEST06_FUNNAME_01));

  }


  /**
   * TestCase to Read Fun file without BOM
   *
   * @throws ParserException as Exception
   * @throws IOException as Exception
   */
  @Test
  public void test07() throws ParserException, IOException {

    List<String> functions = new ArrayList<>();

    try (InputStream in = new FileInputStream(TEST06_SAMPLE_FILE_02)) {
      InputFileParser parser = new InputFileParser(AUT_LOGGER, in, TEST06_SAMPLE_FILE_02);
      parser.parse();
      functions = parser.getFunctions();
    }

    // Test functions
    assertFalse("Functions are available in sample file 2", functions.isEmpty());
    assertTrue("Test a sample function's availability in sample file 2", functions.contains(TEST06_FUNNAME_01));

  }

  /**
   * Negative test. try to parse a file with invalid extn
   *
   * @throws ParserException exception during file parsing
   */
  @Test
  public void test08() throws ParserException {
    this.thrown.expect(ParserException.class);
    this.thrown.expectMessage("Input file type not supported. Please check the file extension : InvalidExtn.label");
    new InputFileParser(AUT_LOGGER, TEST08_INVALID_FILE_EXTN).parse();
  }

  /**
   * Negative test. try to parse a file without file name
   *
   * @throws ParserException exception during file parsing
   */
  @Test
  public void test09t1() throws ParserException {
    this.thrown.expect(ParserException.class);
    this.thrown.expectMessage("File path is mandatory");
    new InputFileParser(AUT_LOGGER, null).parse();
  }

  /**
   * Negative test. try to parse a file without file name
   *
   * @throws ParserException exception during file parsing
   */
  @Test
  public void test09t2() throws ParserException {
    this.thrown.expect(ParserException.class);
    this.thrown.expectMessage("File path is mandatory");
    new InputFileParser(AUT_LOGGER, "").parse();
  }

  /**
   * Negative test. try to parse a file without file name
   *
   * @throws ParserException exception during file parsing
   * @throws IOException error from test
   */
  @Test
  public void test10t1() throws ParserException, IOException {
    this.thrown.expect(ParserException.class);
    this.thrown.expectMessage("File name/path is mandatory for stream input");
    try (InputStream in = new FileInputStream(TEST06_SAMPLE_FILE_02)) {
      new InputFileParser(AUT_LOGGER, in, "").parse();
    }
  }

  /**
   * Negative test. try to parse a file without file name
   *
   * @throws ParserException exception during file parsing
   * @throws IOException error from test
   */
  @Test
  public void test10t2() throws ParserException, IOException {
    this.thrown.expect(ParserException.class);
    this.thrown.expectMessage("File name/path is mandatory for stream input");
    try (InputStream in = new FileInputStream(TEST06_SAMPLE_FILE_02)) {
      new InputFileParser(AUT_LOGGER, in, null).parse();
    }
  }

  /**
   * Test Method to check for [GROUP] block in new format file format with inputstream as input
   *
   * @throws ParserException e
   * @throws IOException e
   */
  @Test
  public void test11AsInpStream() throws ParserException, IOException {
    List<String> labels = new ArrayList<>();
    List<String> functions = new ArrayList<>();
    List<String> groups = new ArrayList<>();

    try (InputStream in = new FileInputStream(TEST11_SAMPLE_FILE_01)) {
      InputFileParser parser = new InputFileParser(AUT_LOGGER, in, TEST11_SAMPLE_FILE_01);
      parser.parse();
      labels = parser.getLabels();
      functions = parser.getFunctions();
      groups = parser.getGroups();
    }

    // Test Labels
    assertFalse("Labels are available", labels.isEmpty());
    assertTrue("Test a sample label's availability", labels.contains(TEST11_LABEL_NAME));

    // Test functions
    assertFalse("Functions are available", functions.isEmpty());
    assertTrue("Test sample function's availability", functions.contains(TEST11_FUNC_NAME));

    // Test Groups
    assertFalse("Groups are available", groups.isEmpty());
    assertTrue("Test a sample Groups's availability", groups.contains(TEST11_GRP_NAME));
  }

  /**
   * Test method to check latest LAB File format with File path as input
   *
   * @throws ParserException exc
   */
  @Test
  public void test11AsFile() throws ParserException {
    InputFileParser parser = new InputFileParser(AUT_LOGGER, TEST11_SAMPLE_FILE_01);
    parser.parse();
    List<String> labels = parser.getLabels();
    List<String> functions = parser.getFunctions();
    List<String> groups = parser.getGroups();

    // Test Labels
    assertFalse("Labels are available", labels.isEmpty());
    assertTrue("Test a sample label's availability", labels.contains(TEST11_LABEL_NAME));
    assertEquals("fileType is lab", INPUT_FILE_TYPE.LAB, parser.getFileType());

    // Test functions
    assertFalse("Functions are available", functions.isEmpty());
    assertTrue("Test a sample function's availability", functions.contains(TEST11_FUNC_NAME));

    // Test Groups
    assertFalse("Groups are available", groups.isEmpty());
    assertTrue("Test a sample function's availability", groups.contains(TEST11_GRP_NAME));

  }
}
