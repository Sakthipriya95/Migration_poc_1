/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Test;

import com.bosch.caltool.icdm.model.cdr.CompliDstInput;
import com.bosch.caltool.icdm.model.cdr.CompliReviewInputMetaData;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.Matchers;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author rgo7cob
 */
public class CompliReviewServiceClientTest extends AbstractRestClientTest {


  private static final String CNHI_PIDC_A2L = "25601778255";

  private static final String CNHI_A2L_A2L_NAME = "A2L_p1603v721r42_iCDM.A2L";

  private static final String CNHI_PVER = "MD1CE101_P1603";

  private static final String CNHI_PVER_VAR = "7_2_1_REV";

  private static final String CNHI_PVER_REV = "4";

  private static final String CNHI_HEX_FILE_NAME = "ATT77734.HEX";

  private static final String CNHI_HEX_FILE_PATH = "testdata/cdr/" + CNHI_HEX_FILE_NAME;


  /**
   *
   */
  private static final String FEAVAL_ATTR_VALUE_NOT_DEFINED = "FEAVAL.ATTR_VALUE_NOT_DEFINED";
  /**
   *
   */
  private static final String COMPLI_REVIEW_PIDC_VER_VAR_INVALID = "COMPLI_REVIEW.PIDC_VER_VAR_INVALID";
  /**
   *
   */
  private static final String VALUE_NOT_SET_MSG = "Value not set for the following attribute(s) : ";
  /**
  *
  */
  private static final String VALUE_NOT_DEF_MSG = "Value not set for the following attribute";
  /**
   * Invalid PIDC element ID
   */
  private static final String INVALID_ELEMENT_MSG = "Not a valid PIDC Version or Variant";

  /**
   * Input Json file path
   */
  private static final String INPUT_JSON_PATH =
      "testdata/cdr/A2L_P1337_M240_Wline_VM_CY_customer_patch_mm3__2hexFiles.json";
  /**
   * Input a2l file path
   */
  private static final String A2L_FILE_PATH = "testdata/cdr/A2L_P1337_M240_Wline_VM_CY_customer_patch_mm3.a2l";
  /**
   * Input hex file-1 path
   */
  private static final String HEX_FILE_ONE_PATH = "testdata/cdr/HEX_EA56HPE6M240LG032KAHA.hex";
  /**
   * Input hex file-2 path
   */
  private static final String HEX_FILE_TWO_PATH = "testdata/cdr/HEX_EA56HPE6M240LG032KAHA_COMPLI_ok.hex";

  /**
   * Input a2l file path
   */
  private static final String A2L_FILE_PATH_COMPLI = "testdata/cdr/A2L_MMD114A0CC1788_MC50_DISCR.A2L";
  /**
   * Input hex file-1 path
   */
  private static final String HEX_FILE_PATH_COMPLI = "testdata/cdr/MMD114A0CC1788_MC50_DISCR_LC.hex";

  /**
   * Input hex file-2 path
   */
  private static final String HEX_FILE_PATH_COMPLI_UCASE = "testdata/cdr/MMD114A0CC1788_MC50_DISCR_LC_ucaseextn.HEX";

  /**
   * Input s19 file-3 path
   */
  private static final String HEX_FILE_PATH_COMPLI_S19 = "testdata/cdr/D961EB16_75B0_B15100Uo.s19";

  /**
   * Input A2l file for s19
   */
  private static final String A2L_NAME_S19 = "testdata/cdr/D961EB16_75B0_B15100U.a2l";


  private static final String OUTPUT_PATH =
      System.getProperty("java.io.tmpdir") + File.separator + "CompliReviewOutput__" +
          (new SimpleDateFormat("yyyyMMdd_hhmmss")).format(Calendar.getInstance().getTime()) + ".zip";

  private static final String PIDC_A2L_ID = "1423096928";
  private static final String PIDC_A2L_ID1 = "12811781358";
  private static final String HEX_FILE_PATH_PIDCA2L1 = "testdata/cdr/HEX_MMD114A0CC1788_MC50_DISCR_LC.hex";
  private static final String A2L_NAME1 = "MMD114A0CC1788_MC50_DISCR.A2L";

  private static final String A2L_NAME = "MMD114A0CC1788_MD50.A2L";
  private static final String PVER_NAME = "MMD114A0CC1788";

  private static final String HEX_FILE_PATH_PIDCA2L = "testdata/cdr/HEX_MMD114A0CC1788_MD50.hex";

  private static final String A2L_FILE_PATH_WITHOUT_DEP_RULE = "testdata/cdr/MD1CS006_P1474_7A00_r5P0__extended.a2l";
  private static final String HEX_FILE_PATH_WITHOUT_DEP_RULE = "testdata/cdr/Prof_7A00_5P00_9294_8690S.hex";
  private static final String INPUT_JSON_PATH_WITHOUT_DEP_RULE = "testdata/cdr/CompliInputMetaData.json";

  private static final String INPUT_JSON_PATH_A2L_MISSING =
      "testdata/cdr/A2L_P1337_M240_Wline_VM_CY_customer_patch_mm3__2hexFiles_a2lfilename_missing.json";

  /**
   * PIDC Variant:Cursor_Offroad_216kW_stageV_Wheel_Loader <br>
   */
  private static final long VAR_ID_DATA_CHECKER = 1214274865L;
  /**
   * PIDC A2L mapping ID for A2L File:p1603v351r09_iCDM.A2L <br>
   */
  private static final Long PIDC_A2L_ID_DATA_CHECKER = 1256501516L;
  /**
   * vCDM DST: Cursor_Offroad_216kW_stageV_Wheel_Loader.p1603v351 ; 2 (DST)
   */
  private static final Long VCDM_DST_ID = 20836626L;

  /**
   * vCDM DST: AT : MD00 : 0
   */
  private static final Long VCDM_DST_ID_WITHOUT_PIDC_A2L = 20894463L;
  /**
   * PIDC Variant:AT [ PIDC Version : X_Test_HENZE_1788_1 (V1_0) ] <br>
   */
  private static final long VAR_ID_DATA_CHECKER_WITHOUT_PIDC_A2L = 1293613521L;

  private static final String PIDC_A2L_ID2 = "17734781803";
  private static final String PIDC_A2L_ID4 = "17740985046";
  private static final String PIDC_A2L_ID3 = "17740985051";
  private static final String PIDC_A2L_ID5 = "17747200479";

  private static final String PIDC_A2L_FOR_ONLY_QSSD_LABELS = "22670260328";
  private static final String A2L_FILE_NAME_FOR_ONLY_QSSD_LABELS = "10SW009425_withGroups_all2.a2l";
  private static final String PVER_P1658 = "ME17971EF";
  private static final String HEX_FOR_ONLY_QSSD_LABELS = "ME17971EF_16S11_Validation_1.hex";
  private static final String HEX_FILE_PATH_ONLY_QSSD_LABELS = "testdata/cdr/" + HEX_FOR_ONLY_QSSD_LABELS;

  /**
   * Test using json file as meta data
   *
   * @throws IOException errror reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void test01() throws IOException, ApicWebServiceException {
    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    servClient.executeCompliReview(A2L_FILE_PATH, INPUT_JSON_PATH,
        new HashSet<>(Arrays.asList(HEX_FILE_ONE_PATH, HEX_FILE_TWO_PATH)), OUTPUT_PATH);
    assertNotNull(OUTPUT_PATH);
    testResults(OUTPUT_PATH);
  }


  /**
   * Test using java model as meta data
   *
   * @throws IOException errror reading zip file
   * @throws ApicWebServiceException error during service call
   */
  // icdm:pidvid,773513765
  @Test
  public void test02() throws IOException, ApicWebServiceException {
    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    servClient.executeCompliReview(A2L_FILE_PATH, createInputMetaData(),
        new HashSet<>(Arrays.asList(HEX_FILE_ONE_PATH, HEX_FILE_TWO_PATH)), OUTPUT_PATH);
    assertNotNull(OUTPUT_PATH);
    testResults(OUTPUT_PATH);
  }


  /**
   * Test for input pidc version id with valid attr and undefined value
   *
   * @throws IOException errror reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void test1() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode(FEAVAL_ATTR_VALUE_NOT_DEFINED));
    this.thrown.expectMessage(startsWith(VALUE_NOT_DEF_MSG));

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    servClient.executeCompliReview(A2L_FILE_PATH_COMPLI, createInputMetaData1(),
        new HashSet<>(Arrays.asList(HEX_FILE_PATH_COMPLI)), OUTPUT_PATH);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test whether compli review service runs without exception for scenario where there are no dependency rules for
   * labels in hex file
   *
   * @throws IOException errror reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testRulesWithoutFeaVal() throws IOException, ApicWebServiceException {
    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    servClient.executeCompliReview(A2L_FILE_PATH_WITHOUT_DEP_RULE, INPUT_JSON_PATH_WITHOUT_DEP_RULE,
        new HashSet<>(Arrays.asList(HEX_FILE_PATH_WITHOUT_DEP_RULE)), OUTPUT_PATH);
    assertNotNull(OUTPUT_PATH);
    testResults(OUTPUT_PATH);
  }

  /**
   * Test for input pidc version id with valid attr and value not defined
   *
   * @throws IOException errror reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void test2() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode(FEAVAL_ATTR_VALUE_NOT_DEFINED));
    this.thrown.expectMessage(startsWith(VALUE_NOT_DEF_MSG));

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    servClient.executeCompliReview(A2L_FILE_PATH_COMPLI, createInputMetaData2(),
        new HashSet<>(Arrays.asList(HEX_FILE_PATH_COMPLI)), OUTPUT_PATH);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test for input pidc version id with valid attr and value not set
   *
   * @throws IOException errror reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void test3() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode(FEAVAL_ATTR_VALUE_NOT_DEFINED));
    this.thrown.expectMessage(startsWith(VALUE_NOT_SET_MSG));

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    servClient.executeCompliReview(A2L_FILE_PATH_COMPLI, createInputMetaData3(),
        new HashSet<>(Arrays.asList(HEX_FILE_PATH_COMPLI)), OUTPUT_PATH);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test for input pidc version id with valid attr and value
   *
   * @throws IOException errror reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void test4() throws IOException, ApicWebServiceException {

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    servClient.executeCompliReview(A2L_FILE_PATH_COMPLI, createInputMetaData4(),
        new HashSet<>(Arrays.asList(HEX_FILE_PATH_COMPLI)), OUTPUT_PATH);
    assertNotNull(OUTPUT_PATH);
    testResults(OUTPUT_PATH);
  }


  /**
   * Test for input 1.pidc version id with valid attr and value , using pidca2lid 2.iCDM Config Attribute -with Used
   * Flag(Yes with value as PS-EC)
   *
   * @throws IOException errror reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void test5() throws IOException, ApicWebServiceException {
    CompliReviewServiceClient servClient = new CompliReviewServiceClient();


    servClient.executeCompliReviewUsingPidcA2lId(PIDC_A2L_ID, A2L_NAME, createInputMetaData5(),
        new HashSet<>(Arrays.asList(HEX_FILE_PATH_PIDCA2L)), OUTPUT_PATH);
    assertNotNull(OUTPUT_PATH);
    testResults(OUTPUT_PATH);
  }

  /**
   * Compliance review with checked out hex file
   *
   * @throws ApicWebServiceException exception from service
   * @throws IOException exception from file reading
   */
  @Test
  public void testCompliReviewWithCheckedOutHex() throws ApicWebServiceException, IOException {
    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    servClient.executeCompliReviewUsingPidcA2lId(PIDC_A2L_ID, A2L_NAME, createInputMetaData5(),
        new HashSet<>(Arrays.asList(HEX_FILE_PATH_PIDCA2L)), OUTPUT_PATH);
    assertNotNull(OUTPUT_PATH);
    testResults(OUTPUT_PATH);

  }

  /**
   * Test for input variant id with valid attr and undefined value
   *
   * @throws IOException errror reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void test1v() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode(FEAVAL_ATTR_VALUE_NOT_DEFINED));
    this.thrown.expectMessage(startsWith(VALUE_NOT_DEF_MSG));

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    servClient.executeCompliReview(A2L_FILE_PATH_COMPLI, createInputMetaData1v(),
        new HashSet<>(Arrays.asList(HEX_FILE_PATH_COMPLI)), OUTPUT_PATH);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test for input variant id with valid attr and value not defined
   *
   * @throws IOException errror reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void test2v() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode(FEAVAL_ATTR_VALUE_NOT_DEFINED));
    this.thrown.expectMessage(startsWith(VALUE_NOT_DEF_MSG));

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    servClient.executeCompliReview(A2L_FILE_PATH_COMPLI, createInputMetaData2v(),
        new HashSet<>(Arrays.asList(HEX_FILE_PATH_COMPLI)), OUTPUT_PATH);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test for input variant id with valid attr and value not set
   *
   * @throws IOException errror reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void test3v() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode(FEAVAL_ATTR_VALUE_NOT_DEFINED));
    this.thrown.expectMessage(startsWith(VALUE_NOT_SET_MSG));

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    servClient.executeCompliReview(A2L_FILE_PATH_COMPLI, createInputMetaData3v(),
        new HashSet<>(Arrays.asList(HEX_FILE_PATH_COMPLI)), OUTPUT_PATH);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test for input variant id with valid attr and value
   *
   * @throws IOException errror reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void test4v() throws IOException, ApicWebServiceException {

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    servClient.executeCompliReview(A2L_FILE_PATH_COMPLI, createInputMetaData4v(),
        new HashSet<>(Arrays.asList(HEX_FILE_PATH_COMPLI)), OUTPUT_PATH);
    assertNotNull(OUTPUT_PATH);
    testResults(OUTPUT_PATH);
  }

  /**
   * Test for input sub variant id
   *
   * @throws IOException errror reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void test1s() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode(COMPLI_REVIEW_PIDC_VER_VAR_INVALID));
    this.thrown.expectMessage(startsWith(INVALID_ELEMENT_MSG));

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    servClient.executeCompliReview(A2L_FILE_PATH_COMPLI, createInputMetaData1s(),
        new HashSet<>(Arrays.asList(HEX_FILE_PATH_COMPLI)), OUTPUT_PATH);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test for input sub variant id
   *
   * @throws IOException errror reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void test2s() throws IOException, ApicWebServiceException {

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode(COMPLI_REVIEW_PIDC_VER_VAR_INVALID));
    this.thrown.expectMessage(startsWith(INVALID_ELEMENT_MSG));

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    servClient.executeCompliReview(A2L_FILE_PATH_COMPLI, createInputMetaData2s(),
        new HashSet<>(Arrays.asList(HEX_FILE_PATH_COMPLI)), OUTPUT_PATH);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test for input sub variant id
   *
   * @throws IOException errror reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void test3s() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode(COMPLI_REVIEW_PIDC_VER_VAR_INVALID));
    this.thrown.expectMessage(startsWith(INVALID_ELEMENT_MSG));

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    servClient.executeCompliReview(A2L_FILE_PATH_COMPLI, createInputMetaData3s(),
        new HashSet<>(Arrays.asList(HEX_FILE_PATH_COMPLI)), OUTPUT_PATH);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test for input sub variant id
   *
   * @throws IOException errror reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void test4s() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode(COMPLI_REVIEW_PIDC_VER_VAR_INVALID));
    this.thrown.expectMessage(startsWith(INVALID_ELEMENT_MSG));

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    servClient.executeCompliReview(A2L_FILE_PATH_COMPLI, createInputMetaData4s(),
        new HashSet<>(Arrays.asList(HEX_FILE_PATH_COMPLI)), OUTPUT_PATH);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test for case-sensitive HEX - extension files
   *
   * @throws IOException errror reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void test5s() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode(COMPLI_REVIEW_PIDC_VER_VAR_INVALID));
    this.thrown.expectMessage(startsWith(INVALID_ELEMENT_MSG));

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    servClient.executeCompliReview(A2L_FILE_PATH_COMPLI, createInputMetaData5s(),
        new HashSet<>(Arrays.asList(HEX_FILE_PATH_COMPLI_UCASE)), OUTPUT_PATH);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test for S19 extension files, without pidc element id(without pidc-to test compli rvw conducted from welcome page))
   *
   * @throws IOException errror reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void test6s() throws IOException, ApicWebServiceException {
    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    servClient.executeCompliReview(A2L_NAME_S19, createInputMetaData6s(),
        new HashSet<>(Arrays.asList(HEX_FILE_PATH_COMPLI_S19)), OUTPUT_PATH);
    assertNotNull(OUTPUT_PATH);
    testResults(OUTPUT_PATH);
  }

  /**
   * @throws IOException
   */
  private void testResults(final String resultPath) throws IOException {
    try (InputStream fis = new FileInputStream(resultPath); ZipInputStream zis = new ZipInputStream(fis);) {
      int fileCount = 0;
      ZipEntry ze;
      while ((ze = zis.getNextEntry()) != null) {
        fileCount++;
        LOG.info("Ouput File {}. {}", fileCount, ze.getName());
      }

      assertTrue("Output files > 0", fileCount > 0);
    }
  }

  private CompliReviewInputMetaData createInputMetaData() {
    CompliReviewInputMetaData input = new CompliReviewInputMetaData();

    input.setA2lFileName("A2L_P1337_M240_Wline_VM_CY_customer_patch_mm3.a2l");
    input.setPverName("P1337_MEDCC79");
    input.setPverVariant("M240");
    input.setPverRevision("0");
    input.setWebflowID("1001");

    Map<Long, String> hexfileIdxMap = new HashMap<>();
    hexfileIdxMap.put(1L, "HEX_EA56HPE6M240LG032KAHA_COMPLI_ok.hex");
    hexfileIdxMap.put(2L, "HEX_EA56HPE6M240LG032KAHA.hex");
    input.setHexfileIdxMap(hexfileIdxMap);

    Map<Long, Long> hexFilePidcElement = new HashMap<>();
    hexFilePidcElement.put(1L, 768112367L);
    hexFilePidcElement.put(2L, 768112367L);
    input.setHexFilePidcElement(hexFilePidcElement);

    return input;
  }

  private CompliReviewInputMetaData createInputMetaData1() {
    CompliReviewInputMetaData input = setA2lHexData();

    Map<Long, Long> hexFilePidcElement = new HashMap<>();
    hexFilePidcElement.put(1L, 768112367L);
    input.setHexFilePidcElement(hexFilePidcElement);

    return input;
  }

  private CompliReviewInputMetaData createInputMetaData1v() {
    return createInputMetaData1();
  }

  private CompliReviewInputMetaData createInputMetaData1s() {
    CompliReviewInputMetaData input = setA2lHexData();

    Map<Long, Long> hexFilePidcElement = new HashMap<>();
    hexFilePidcElement.put(1L, 892953348L);
    input.setHexFilePidcElement(hexFilePidcElement);

    return input;
  }

  private CompliReviewInputMetaData createInputMetaData2() {
    return createInputMetaData1();
  }

  private CompliReviewInputMetaData createInputMetaData2v() {
    return createInputMetaData1();
  }

  private CompliReviewInputMetaData createInputMetaData2s() {
    CompliReviewInputMetaData input = setA2lHexData();

    Map<Long, Long> hexFilePidcElement = new HashMap<>();
    hexFilePidcElement.put(1L, 892957619L);
    input.setHexFilePidcElement(hexFilePidcElement);

    return input;
  }

  private CompliReviewInputMetaData createInputMetaData3() {
    return createInputMetaData1();
  }

  private CompliReviewInputMetaData createInputMetaData3v() {
    return createInputMetaData1();
  }

  private CompliReviewInputMetaData createInputMetaData3s() {
    CompliReviewInputMetaData input = setA2lHexData();

    Map<Long, Long> hexFilePidcElement = new HashMap<>();
    hexFilePidcElement.put(1L, 892957644L);
    input.setHexFilePidcElement(hexFilePidcElement);

    return input;
  }

  private CompliReviewInputMetaData createInputMetaData4() {
    CompliReviewInputMetaData input = setA2lHexData();

    Map<Long, Long> hexFilePidcElement = new HashMap<>();
    hexFilePidcElement.put(1L, 1203064067L);
    input.setHexFilePidcElement(hexFilePidcElement);

    return input;
  }

  private CompliReviewInputMetaData createMetaDataForCheckedOutHex() {


    CompliReviewInputMetaData input = new CompliReviewInputMetaData();

    input.setA2lFileName(CNHI_A2L_A2L_NAME);
    input.setPverName(CNHI_PVER);
    input.setPverVariant(CNHI_PVER_VAR);
    input.setPverRevision(CNHI_PVER_REV);


    Map<Long, String> hexfileIdxMap = new HashMap<>();
    hexfileIdxMap.put(1L, CNHI_HEX_FILE_NAME);

    input.setHexfileIdxMap(hexfileIdxMap);

    Map<Long, Long> hexFilePidcElement = new HashMap<>();

    // pidc variant id - 25601777130 - Cursor_Onroad_251kW_S-Way_EVI-StepE_SCROnly
    hexFilePidcElement.put(1L, 25601777130L);
    input.setHexFilePidcElement(hexFilePidcElement);

    return input;

  }


  /**
   * @return
   */
  private CompliReviewInputMetaData createInputMetaData5() {

    CompliReviewInputMetaData input = new CompliReviewInputMetaData();

    input.setA2lFileName(A2L_NAME);
    input.setPverName(PVER_NAME);
    input.setPverVariant("MD50");
    input.setPverRevision("3");


    Map<Long, String> hexfileIdxMap = new HashMap<>();
    hexfileIdxMap.put(1L, "HEX_MMD114A0CC1788_MD50.hex");

    input.setHexfileIdxMap(hexfileIdxMap);

    Map<Long, Long> hexFilePidcElement = new HashMap<>();
    // Variant ID - 1293613521L(Variant name - AT)
    hexFilePidcElement.put(1L, 1293613521L);
    input.setHexFilePidcElement(hexFilePidcElement);

    return input;
  }

  private CompliReviewInputMetaData createInputMetaData4v() {
    return createInputMetaData4();
  }

  private CompliReviewInputMetaData createInputMetaData4s() {
    CompliReviewInputMetaData input = setA2lHexData();

    Map<Long, Long> hexFilePidcElement = new HashMap<>();
    hexFilePidcElement.put(1L, 892962119L);
    input.setHexFilePidcElement(hexFilePidcElement);

    return input;
  }


  /**
   * @return
   */
  private CompliReviewInputMetaData createInputMetaData5s() {
    CompliReviewInputMetaData input = setA2lHexData();

    Map<Long, Long> hexFilePidcElement = new HashMap<>();
    hexFilePidcElement.put(1L, 892962119L);
    input.setHexFilePidcElement(hexFilePidcElement);
    Map<Long, String> hexfileIdxMap = new HashMap<>();
    hexfileIdxMap.put(1L, "MMD114A0CC1788_MC50_DISCR_LC_ucaseextn.HEX");
    input.setHexfileIdxMap(hexfileIdxMap);
    return input;
  }


  /**
   * @return
   */
  private CompliReviewInputMetaData createInputMetaData6s() {
    CompliReviewInputMetaData input = setA2lHexData();
    input.setA2lFileName("D961EB16_75B0_B15100U.a2l");
    input.setPverName("D961EB16");

    Map<Long, Long> hexFilePidcElement = new HashMap<>();
    input.setHexFilePidcElement(hexFilePidcElement);
    Map<Long, String> hexfileIdxMap = new HashMap<>();
    hexfileIdxMap.put(1L, "D961EB16_75B0_B15100Uo.s19");
    input.setHexfileIdxMap(hexfileIdxMap);
    return input;
  }


  /**
   * @return
   */
  private CompliReviewInputMetaData setA2lHexData() {
    CompliReviewInputMetaData input = new CompliReviewInputMetaData();

    input.setA2lFileName("A2L_MMD114A0CC1788_MC50_DISCR.A2L");
    input.setPverName(PVER_NAME);
    input.setPverVariant("MC50_DISCR");
    input.setPverRevision("0");
    input.setWebflowID("1001");

    Map<Long, String> hexfileIdxMap = new HashMap<>();
    hexfileIdxMap.put(1L, "MMD114A0CC1788_MC50_DISCR_LC.hex");
    input.setHexfileIdxMap(hexfileIdxMap);
    return input;
  }

  /**
   * @throws IOException Exception
   * @throws ApicWebServiceException Exception
   */
  @Test
  public void testImportCompliReviewInputData() throws IOException, ApicWebServiceException {
    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    servClient.executeCompliReviewUsingPidcA2lId(PIDC_A2L_ID, A2L_NAME, createInputMetaData5(),
        new HashSet<>(Arrays.asList(HEX_FILE_PATH_PIDCA2L)), OUTPUT_PATH);
    String path = servClient.importCompliReviewInputData(servClient.getLastExecutionId());
    assertNotNull(path);
  }

  /**
   * @throws IOException Exception
   * @throws ApicWebServiceException Exception
   */
  @Test
  public void testCompliReviewA2LMissingInJson() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("COMPLI_REVIEW.A2L_MISSING_IN_METADATA"));
    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    servClient.executeCompliReview(A2L_FILE_PATH, INPUT_JSON_PATH_A2L_MISSING,
        new HashSet<>(Arrays.asList(HEX_FILE_ONE_PATH, HEX_FILE_TWO_PATH)), OUTPUT_PATH);

  }


  /**
   * Test compli review service for datachecker without pidc a2l as an input
   *
   * @throws IOException errror reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testDataChkrCompliRvwWithoutPidcA2l() throws IOException, ApicWebServiceException {
    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    CompliDstInput compliDstInp = new CompliDstInput();
    compliDstInp.setVcdmDstId(VCDM_DST_ID_WITHOUT_PIDC_A2L);
    compliDstInp.setPidcElementId(VAR_ID_DATA_CHECKER_WITHOUT_PIDC_A2L);
    compliDstInp.setCheckedIn(true);
    servClient.executeCompliReviewUsingDstId(compliDstInp, OUTPUT_PATH);
    assertNotNull(OUTPUT_PATH);
    testResults(OUTPUT_PATH);
  }

  /**
   * Test compli review service for datachecker with pidc a2l as input
   *
   * @throws IOException errror reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testDataChkrCompliRvwWithPidcA2l() throws IOException, ApicWebServiceException {
    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    CompliDstInput compliDstInp = new CompliDstInput();
    compliDstInp.setVcdmDstId(VCDM_DST_ID);
    compliDstInp.setPidcA2lId(PIDC_A2L_ID_DATA_CHECKER);
    compliDstInp.setPidcElementId(VAR_ID_DATA_CHECKER);
    compliDstInp.setCheckedIn(true);
    servClient.executeCompliReviewUsingDstId(compliDstInp, OUTPUT_PATH);
    assertNotNull(OUTPUT_PATH);
    testResults(OUTPUT_PATH);
  }

  /**
   * Test icdm Questionnaire config attribute with UsedFlag as Yes without value
   *
   * @throws IOException Exception
   * @throws ApicWebServiceException Exception
   */
  @Test
  public void testCompliReviewIcdmConfigValueInPidc1() throws IOException, ApicWebServiceException {
    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    CompliReviewInputMetaData input = createInputMetaData6();
    Map<Long, Long> hexFilePidcElement = new HashMap<>();
    // PIDC vers id for icdm:pidvid,12811780912
    hexFilePidcElement.put(1L, 12811780912L);
    input.setHexFilePidcElement(hexFilePidcElement);
    servClient.executeCompliReviewUsingPidcA2lId(PIDC_A2L_ID1, A2L_NAME1, input,
        new HashSet<>(Arrays.asList(HEX_FILE_PATH_PIDCA2L1)), OUTPUT_PATH);
  }

  /**
   * Test icdm Questionnaire config attribute with UsedFlag as ??? without value
   *
   * @throws IOException Exception
   * @throws ApicWebServiceException Exception
   */
  @Test
  public void testCompliReviewIcdmConfigValueInPidc2() throws IOException, ApicWebServiceException {
    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    CompliReviewInputMetaData input = createInputMetaData6();
    Map<Long, Long> hexFilePidcElement = new HashMap<>();
    // PIDC vers id for icdm:pidvid,17734781006
    hexFilePidcElement.put(1L, 17734781006L);
    input.setHexFilePidcElement(hexFilePidcElement);
    servClient.executeCompliReviewUsingPidcA2lId(PIDC_A2L_ID2, A2L_NAME1, input,
        new HashSet<>(Arrays.asList(HEX_FILE_PATH_PIDCA2L1)), OUTPUT_PATH);
  }

  /**
   * Test icdm Questionnaire config attribute with UsedFlag as No
   *
   * @throws IOException Exception
   * @throws ApicWebServiceException Exception
   */
  @Test
  public void testCompliReviewIcdmConfigValueInPidc3() throws IOException, ApicWebServiceException {
    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    CompliReviewInputMetaData input = createInputMetaData6();
    Map<Long, Long> hexFilePidcElement = new HashMap<>();
    // PIDC vers id for icdm:pidvid,17740984439
    hexFilePidcElement.put(1L, 17740984439L);
    input.setHexFilePidcElement(hexFilePidcElement);
    servClient.executeCompliReviewUsingPidcA2lId(PIDC_A2L_ID3, A2L_NAME1, input,
        new HashSet<>(Arrays.asList(HEX_FILE_PATH_PIDCA2L1)), OUTPUT_PATH);
  }

  /**
   * Test icdm Questionnaire config attribute without setting UsedFlag
   *
   * @throws IOException Exception
   * @throws ApicWebServiceException Exception
   */
  @Test
  public void testCompliReviewIcdmConfigValueInPidc4() throws IOException, ApicWebServiceException {
    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    CompliReviewInputMetaData input = createInputMetaData6();
    Map<Long, Long> hexFilePidcElement = new HashMap<>();
    // PIDC vers id for icdm:pidvid,17740985039
    hexFilePidcElement.put(1L, 17740985039L);
    input.setHexFilePidcElement(hexFilePidcElement);
    servClient.executeCompliReviewUsingPidcA2lId(PIDC_A2L_ID4, A2L_NAME1, input,
        new HashSet<>(Arrays.asList(HEX_FILE_PATH_PIDCA2L1)), OUTPUT_PATH);
  }

  /**
   * Test icdm Questionnaire config attribute with UsedFlag-Yes and value as BEG
   *
   * @throws IOException Exception
   * @throws ApicWebServiceException Exception
   */
  @Test
  public void testCompliReviewIcdmConfigValueInPidc5() throws IOException, ApicWebServiceException {
    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    CompliReviewInputMetaData input = createInputMetaData6();
    Map<Long, Long> hexFilePidcElement = new HashMap<>();
    // PIDC vers id for icdm:pidvid,17747199532
    hexFilePidcElement.put(1L, 17747199532L);
    input.setHexFilePidcElement(hexFilePidcElement);
    servClient.executeCompliReviewUsingPidcA2lId(PIDC_A2L_ID5, A2L_NAME1, input,
        new HashSet<>(Arrays.asList(HEX_FILE_PATH_PIDCA2L1)), OUTPUT_PATH);
  }


  /**
   * Test case for compliance review with a2l with only qssd labels (ie., without compliance labels)
   *
   * @throws ApicWebServiceException error during compliance review
   */
  @Test
  public void testCompliReviewWithOnlyQssdLabels() throws ApicWebServiceException {
    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    CompliReviewInputMetaData input = new CompliReviewInputMetaData();

    input.setA2lFileName(A2L_FILE_NAME_FOR_ONLY_QSSD_LABELS);
    input.setPverName(PVER_P1658);
    input.setPverVariant("16S11");
    input.setPverRevision("0");

    Map<Long, String> hexfileIdxMap = new HashMap<>();
    hexfileIdxMap.put(1L, HEX_FOR_ONLY_QSSD_LABELS);
    input.setHexfileIdxMap(hexfileIdxMap);

    Map<Long, Long> hexFilePidcElement = new HashMap<>();
    hexFilePidcElement.put(1L, 22648326578L);
    input.setHexFilePidcElement(hexFilePidcElement);

    servClient.executeCompliReviewUsingPidcA2lId(PIDC_A2L_FOR_ONLY_QSSD_LABELS, A2L_FILE_NAME_FOR_ONLY_QSSD_LABELS,
        input, new HashSet<>(Arrays.asList(HEX_FILE_PATH_ONLY_QSSD_LABELS)), OUTPUT_PATH);

  }

  private CompliReviewInputMetaData createInputMetaData6() {

    CompliReviewInputMetaData input = new CompliReviewInputMetaData();

    input.setA2lFileName(A2L_NAME1);
    input.setPverName(PVER_NAME);
    input.setPverVariant("MC50_DISCR");
    input.setPverRevision("0");


    Map<Long, String> hexfileIdxMap = new HashMap<>();
    hexfileIdxMap.put(1L, "HEX_MMD114A0CC1788_MC50_DISCR_LC.hex");

    input.setHexfileIdxMap(hexfileIdxMap);

    return input;
  }
}
