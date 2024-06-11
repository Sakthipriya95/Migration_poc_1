/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import org.hamcrest.core.StringStartsWith;
import org.junit.Test;

import com.bosch.caltool.icdm.model.cdr.CompliDstInput;
import com.bosch.caltool.icdm.model.cdr.CompliReviewInputMetaData;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.Matchers;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author EMS4KOR
 */
public class CompliReviewServiceClientTestNegative extends AbstractRestClientTest {


  /**
   * Pidc Version Id-> Passenger Car->MD1-C->X_Test_HENZE_1788_1 (V1_0) A2L File: AUDI->Diesel Engine->PC - Passenger
   * Car->MD1-C->X_Test_HENZE_1788_1 (V1_0)->MMD114A0CC1788->MMD114A0CC1788_MD00.A2L
   */
  private static final long PIDC_ELEMENT_ID = 1165057178L;

  /**
   * Invalid vcdm dst id
   */
  private static final long INVALID_VCDM_DST_ID = 219660280L;

  /**
   ** Passenger Car->MD1-C->X_Test_HENZE_1788_1 (V1_0) A2L File: AUDI->Diesel Engine->PC - Passenger
   * Car->MD1-C->X_Test_HENZE_1788_1 (V1_0)->MMD114A0CC1788->MMD114A0CC1788_MD00.A2L
   */
  private static final long PIDC_A2L_ID_DATA_CHECKER = 1423096234L;

  /**
   *
   */
  private static final String HEX_FILE_NAME = "HEX_EA56HPE6M240LG032KAHA.hex";

  /**
   * Input a2l file path
   */
  private static final String A2L_FILE_PATH = "testdata/cdr/A2L_P1337_M240_Wline_VM_CY_customer_patch_mm3.a2l";

  /**
   * Input a2l file path
   */
  private static final String A2L_FILE_PATH_COMPLI = "testdata/cdr/A2L_MMD114A0CC1788_MC50_DISCR.A2L";

  /**
   * Input Invalid a2l file path
   */
  private static final String A2L_FILE_PATH_COMPLI_INVALID = "testdata/cdr/A2L_MMD114A0CC1788_MC50_DISSCR_INVALID.A2L";
  /**
   * Input Wrong Format a2l file path
   */
  private static final String A2L_FILE_PATH_COMPLI_WRONGFORMAT =
      "testdata/cdr/A2L_MMD114A0CC1788_MC50_DISSCR_WrongFormat.A2L";
  /**
   * /** Input hex file-1 path
   */
  private static final String HEX_FILE_PATH_COMPLI = "testdata/cdr/MMD114A0CC1788_MC50_DISCR_LC.hex";

  /**
   * Input INVALID hex file-1 path
   */
  private static final String HEX_FILE_PATH_COMPLI_INVALID = "testdata/cdr/MMD114A0CC1788_MC50_DISCR_LC_INVALID.hex";

  /**
   * Input hex file-1 path
   */
  private static final String HEX_FILE_ONE_PATH = "testdata/cdr/HEX_EA56HPE6M240LG032KAHA.hex";

  /**
   * /** Input hex file path
   */
  private static final String HEX_FILE_PATH_MISSING_COMPLI = "testdata/MMD114A0CC1788/BXAZ8ERBIIA5_X810_20894463.hex";

  /**
   * Input a2l file path
   */
  private static final String A2L_FILE_PATH_MISSING_COMPLI =
      "testdata/MMD114A0CC1788/A2L_MMD114A0CC1788_MD00_withGroups.A2L";
  /**
   * /** Input hex file-2 path
   */
  private static final String HEX_FILE_TWO_PATH = "testdata/cdr/HEX_EA56HPE6M240LG032KAHA_COMPLI_ok.hex";

  private static final String OUTPUT_PATH =
      System.getProperty("java.io.tmpdir") + File.separator + "CompliReviewOutput__" +
          (new SimpleDateFormat("yyyyMMdd_hhmmss")).format(Calendar.getInstance().getTime()) + ".zip";
  /**
   * PIDC Variant: <br>
   */
  private static final long VAR_ID_DATA_CHECKER = 1116516480L;
  /**
   * VCDM DST Revision node id is available for pidc version and a2l given below PIDC Version: AUDI->Diesel Engine->PC -
   * Passenger Car->MD1-C->X_Test_HENZE_1788_1 (V1_0) A2L File: AUDI->Diesel Engine->PC - Passenger
   * Car->MD1-C->X_Test_HENZE_1788_1 (V1_0)->MMD114A0CC1788->MMD114A0CC1788_MD00.A2L
   */
  private static final Long VCDM_DST_ID = 21966028L;

  /**
   * Test 3.1
   *
   * @throws IOException error reading zip file
   * @throws ApicWebServiceException error during service call
   */

  public void testA2LMissing() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("TODO"));
    this.thrown.expectMessage("A2L file is mandatory");

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    servClient.executeCompliReview("", createInputMetaData1(), new HashSet<>(Arrays.asList(HEX_FILE_PATH_COMPLI)),
        OUTPUT_PATH);

    testResults(OUTPUT_PATH);
  }

  /**
   * Test 3.2
   *
   * @throws IOException error reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testHexMissing() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Input HEX file(s) missing");

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    servClient.executeCompliReview(A2L_FILE_PATH_COMPLI, createInputMetaData1(), new HashSet<>(), OUTPUT_PATH);

    testResults(OUTPUT_PATH);
  }

  /**
   * Test data checker compli review service for missing vcdm dst id input
   *
   * @throws IOException errror reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testDstMissing() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);

    this.thrown.expectMessage(startsWith("vCDM DST ID is missing"));

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    CompliDstInput compliDstInp = new CompliDstInput();
    compliDstInp.setPidcElementId(VAR_ID_DATA_CHECKER);
    compliDstInp.setCheckedIn(true);

    servClient.executeCompliReviewUsingDstId(compliDstInp, OUTPUT_PATH);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test data checker compli service for missing DST information for checked out dsts - dst name/dst revision/aprj
   * name/aprj revision
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testDstInfoMissingForCheckedOutDsts() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("For checked-out DSTs, APRJ Name, APRJ Revision, DST Name, DST Revision are mandatory.");
    CompliDstInput compliDstInp = new CompliDstInput();
    compliDstInp.setVcdmDstId(VCDM_DST_ID);
    compliDstInp.setPidcA2lId(PIDC_A2L_ID_DATA_CHECKER);
    compliDstInp.setPidcElementId(VAR_ID_DATA_CHECKER);
    compliDstInp.setCheckedIn(false);

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    servClient.executeCompliReviewUsingDstId(compliDstInp, OUTPUT_PATH);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test data checker compli review service for pidc element id as pidc version when variant is available
   *
   * @throws IOException errror reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testPidcVarAvailable() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);

    this.thrown.expectMessage(startsWith("Variant is defined for the PIDC. Variant ID is required for Review"));

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    CompliDstInput compliDstInp = new CompliDstInput();
    compliDstInp.setVcdmDstId(VCDM_DST_ID);
    compliDstInp.setPidcA2lId(PIDC_A2L_ID_DATA_CHECKER);
    compliDstInp.setPidcElementId(PIDC_ELEMENT_ID);
    compliDstInp.setCheckedIn(true);

    servClient.executeCompliReviewUsingDstId(compliDstInp, OUTPUT_PATH);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test data checker compli review service for pidc element id as pidc version when variant is available
   *
   * @throws IOException errror reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testInvalidvCDMDst() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);

    this.thrown.expectMessage(startsWith("Not a valid vCDM DST ID"));

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    CompliDstInput compliDstInp = new CompliDstInput();
    compliDstInp.setVcdmDstId(INVALID_VCDM_DST_ID);
    compliDstInp.setPidcA2lId(PIDC_A2L_ID_DATA_CHECKER);
    compliDstInp.setPidcElementId(PIDC_ELEMENT_ID);
    compliDstInp.setCheckedIn(true);

    servClient.executeCompliReviewUsingDstId(compliDstInp, OUTPUT_PATH);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test data checker compli review service for missing pidc element(pidc version,variant id) input
   *
   * @throws IOException errror reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testPidcElementMissing() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);

    this.thrown.expect(Matchers.matchesErrorCode("FEAVAL.PIDC_ELEMENT_MISSING"));
    this.thrown.expectMessage(
        "PIDC element is mandatory since some of the parameters being reviewed have attribute dependencies");


    CompliReviewServiceClient servClient = new CompliReviewServiceClient();

    CompliDstInput compliDstInp = new CompliDstInput();
    compliDstInp.setVcdmDstId(VCDM_DST_ID);
    compliDstInp.setCheckedIn(true);

    servClient.executeCompliReviewUsingDstId(compliDstInp, OUTPUT_PATH);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }


  /**
   * Test 3.3
   *
   * @throws IOException error reading zip file
   * @throws ApicWebServiceException error during service call
   */

  public void testJsonMissing() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("TODO"));
    this.thrown.expectMessage("Input JSON file is missing");

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    servClient.executeCompliReview(A2L_FILE_PATH_COMPLI, "", new HashSet<>(Arrays.asList(HEX_FILE_PATH_COMPLI)),
        OUTPUT_PATH);

    testResults(OUTPUT_PATH);
  }

  /**
   * Test 3.5
   *
   * @throws IOException error reading zip file
   * @throws ApicWebServiceException error during service call
   */

  public void testCompliReviewA2lInvalid() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("TODO"));
    this.thrown.expectMessage("The given file is Corrupted");

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    CompliReviewInputMetaData metaData = createInputMetaData1();
    metaData.setA2lFileName("A2L_MMD114A0CC1788_MC50_DISSCR_INVALID.A2L");
    servClient.executeCompliReview(A2L_FILE_PATH_COMPLI_INVALID, metaData,
        new HashSet<>(Arrays.asList(HEX_FILE_PATH_COMPLI)), OUTPUT_PATH);

    testResults(OUTPUT_PATH);
  }

  /**
   * Test 3.6
   *
   * @throws IOException error reading zip file
   * @throws ApicWebServiceException error during service call
   */

  public void testCompliReviewHexInvalid() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("TODO"));
    this.thrown.expectMessage("Error in validating HEX File");

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    CompliReviewInputMetaData metaData = createInputMetaData1();
    Map<Long, String> hexfileIdxMap = new HashMap<>();
    hexfileIdxMap.put(1L, "MMD114A0CC1788_MC50_DISCR_LC_INVALID.hex");
    metaData.setHexfileIdxMap(hexfileIdxMap);
    servClient.executeCompliReview(A2L_FILE_PATH_COMPLI, metaData,
        new HashSet<>(Arrays.asList(HEX_FILE_PATH_COMPLI_INVALID)), OUTPUT_PATH);

    testResults(OUTPUT_PATH);
  }

  /**
   * Test 3.7
   *
   * @throws IOException error reading zip file
   * @throws ApicWebServiceException error during service call
   */

  public void testCompliReviewJsonInvalid() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("TODO"));
    this.thrown.expectMessage("If the a2l File name is not present in the Json file");

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    CompliReviewInputMetaData metaData = createInputMetaData1();
    metaData.setA2lFileName("");
    servClient.executeCompliReview(A2L_FILE_PATH_COMPLI, metaData, new HashSet<>(Arrays.asList(HEX_FILE_PATH_COMPLI)),
        OUTPUT_PATH);

    testResults(OUTPUT_PATH);
  }

  /**
   * Test 3.8
   *
   * @throws IOException error reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCompliReviewA2LNameMismatch() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("COMPLI_REVIEW.A2L_MISMATCH"));
    this.thrown.expectMessage("A2L file is different in meta data");

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    CompliReviewInputMetaData metaData = createInputMetaData1();
    metaData.setA2lFileName("A2L_MMD114A0CC1788_MC50_DIAAASCR.A2L");

    servClient.executeCompliReview(A2L_FILE_PATH_COMPLI, metaData, new HashSet<>(Arrays.asList(HEX_FILE_PATH_COMPLI)),
        OUTPUT_PATH);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test 3.9
   *
   * @throws IOException error reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCompliReviewPverNameMissing() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("COMPLI_REVIEW.PVER_MISSING"));
    this.thrown.expectMessage("Input parameter pverName is mandatory");

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    CompliReviewInputMetaData metaData = createInputMetaData1();
    metaData.setPverName("");
    servClient.executeCompliReview(A2L_FILE_PATH_COMPLI, metaData, new HashSet<>(Arrays.asList(HEX_FILE_PATH_COMPLI)),
        OUTPUT_PATH);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test 3.10
   *
   * @throws IOException error reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCompliReviewPverVarMissing() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("COMPLI_REVIEW.PVER_VAR_MISSING"));
    this.thrown.expectMessage("Input parameter pverVariant is mandatory");

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    CompliReviewInputMetaData metaData = createInputMetaData1();
    metaData.setPverVariant("");
    servClient.executeCompliReview(A2L_FILE_PATH_COMPLI, metaData, new HashSet<>(Arrays.asList(HEX_FILE_PATH_COMPLI)),
        OUTPUT_PATH);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test 3.11
   *
   * @throws IOException error reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCompliReviewPverRevMissing() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("COMPLI_REVIEW.PVER_REV_MISSING"));
    this.thrown.expectMessage("Input parameter pverRevision is mandatory");


    CompliReviewServiceClient servClient = new CompliReviewServiceClient();

    CompliReviewInputMetaData metaData = createInputMetaData1();
    metaData.setPverRevision("");
    servClient.executeCompliReview(A2L_FILE_PATH_COMPLI, metaData, new HashSet<>(Arrays.asList(HEX_FILE_PATH_COMPLI)),
        OUTPUT_PATH);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test 3.12
   *
   * @throws IOException error reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCompliReviewPverRevTypeInvalid() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("COMPLI_REVIEW.PVER_REV_TYPE_INVALID"));
    this.thrown.expectMessage("pverRevision must be an integer");

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    CompliReviewInputMetaData metaData = createInputMetaData1();
    metaData.setPverRevision("3XXX");
    servClient.executeCompliReview(A2L_FILE_PATH_COMPLI, metaData, new HashSet<>(Arrays.asList(HEX_FILE_PATH_COMPLI)),
        OUTPUT_PATH);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test 3.13
   *
   * @throws IOException error reading zip file
   * @throws ApicWebServiceException error during service call
   */

  public void testCompliReviewHexMismatch() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("TODO"));
    this.thrown.expectMessage("The hex files defined in the meta data is different");

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    CompliReviewInputMetaData metaData = createInputMetaData1();
    Map<Long, String> hexfileIdxMap = new HashMap<>();
    hexfileIdxMap.put(1L, HEX_FILE_NAME);
    metaData.setHexfileIdxMap(hexfileIdxMap);
    servClient.executeCompliReview(A2L_FILE_PATH_COMPLI, metaData, new HashSet<>(Arrays.asList(HEX_FILE_ONE_PATH)),
        OUTPUT_PATH);

    testResults(OUTPUT_PATH);
  }

  /**
   * Test 3.14
   *
   * @throws IOException error reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCompliReviewHexNameMismatch() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("COMPLI_REVIEW.HEX_NAME_MISMATCH"));
    this.thrown.expectMessage("HEX file names in the meta data is different");

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    CompliReviewInputMetaData metaData = createInputMetaData1();
    Map<Long, String> hexfileIdxMap = new HashMap<>();
    hexfileIdxMap.put(1L, HEX_FILE_NAME);
    metaData.setHexfileIdxMap(hexfileIdxMap);
    servClient.executeCompliReview(A2L_FILE_PATH_COMPLI, metaData, new HashSet<>(Arrays.asList(HEX_FILE_PATH_COMPLI)),
        OUTPUT_PATH);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test 3.15
   *
   * @throws IOException error reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCompliReviewHexFilesMismatch() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("COMPLI_REVIEW.HEX_MISMATCH"));
    this.thrown.expectMessage("HEX file(s) provided is different from the details given in metadata");

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();

    servClient.executeCompliReview(A2L_FILE_PATH, createInputMetaData(),
        new HashSet<>(Arrays.asList(HEX_FILE_ONE_PATH, HEX_FILE_TWO_PATH)), OUTPUT_PATH);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test 3.16
   *
   * @throws IOException error reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCompliReviewPidcVerVarInvalid() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("COMPLI_REVIEW.PIDC_VER_VAR_INVALID"));
    this.thrown.expectMessage("Not a valid PIDC Version or Variant");

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    CompliReviewInputMetaData metaData = createInputMetaData1();
    Map<Long, Long> hexFilePidcElement = new HashMap<>();
    hexFilePidcElement.put(1L, 7681123677L);
    metaData.setHexFilePidcElement(hexFilePidcElement);

    servClient.executeCompliReview(A2L_FILE_PATH_COMPLI, metaData, new HashSet<>(Arrays.asList(HEX_FILE_PATH_COMPLI)),
        OUTPUT_PATH);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test 3.18
   *
   * @throws IOException error reading zip file
   * @throws ApicWebServiceException error during service call
   */

  public void testCompliReviewOnePidcVer() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("TODO"));
    this.thrown.expectMessage("Only one Pidc version is allowed in the input");

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    CompliReviewInputMetaData metaData = createInputMetaData();
    Map<Long, String> hexfileIdxMap = new HashMap<>();

    hexfileIdxMap.put(2L, HEX_FILE_NAME);
    metaData.setHexfileIdxMap(hexfileIdxMap);
    Map<Long, Long> hexFilePidcElement = new HashMap<>();
    hexFilePidcElement.put(1L, 768112367L);
    hexFilePidcElement.put(2L, 860095643L);
    metaData.setHexFilePidcElement(hexFilePidcElement);
    servClient.executeCompliReview(A2L_FILE_PATH, metaData,
        new HashSet<>(Arrays.asList(HEX_FILE_ONE_PATH, HEX_FILE_TWO_PATH)), OUTPUT_PATH);

    testResults(OUTPUT_PATH);
  }


  /**
   * Test 3.20
   *
   * @throws IOException error reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCompliReviewPverVarInvalid() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("COMPLI_REVIEW.PVER_VAR_INVALID"));
    this.thrown.expectMessage("The given PVER variant MC50_DISCRXXX is not valid for the PVER MMD114A0CC1788");

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    CompliReviewInputMetaData metaData = createInputMetaData1();
    metaData.setPverVariant("MC50_DISCRXXX");
    servClient.executeCompliReview(A2L_FILE_PATH_COMPLI, metaData, new HashSet<>(Arrays.asList(HEX_FILE_PATH_COMPLI)),
        OUTPUT_PATH);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test 3.21
   *
   * @throws IOException error reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCompliReviewPverRevInvalid() throws IOException, ApicWebServiceException {
    CompliReviewServiceClient servClient = new CompliReviewServiceClient();

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("COMPLI_REVIEW.PVER_REV_INVALID"));
    this.thrown.expectMessage(
        "The given PVER revision 3 is not valid for the given combination of PVER MMD114A0CC1788 and variant MC50_DISCR");

    CompliReviewInputMetaData metaData = createInputMetaData1();
    metaData.setPverRevision("3");
    servClient.executeCompliReview(A2L_FILE_PATH_COMPLI, metaData, new HashSet<>(Arrays.asList(HEX_FILE_PATH_COMPLI)),
        OUTPUT_PATH);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test 3.22
   *
   * @throws IOException error reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCompliReviewA2LWrongFormat() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("A2L.PARSE_ERROR"));
    this.thrown.expectMessage(StringStartsWith.startsWith("Error while retrieving A2L contents"));

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    CompliReviewInputMetaData metaData = createInputMetaData1();
    metaData.setA2lFileName("A2L_MMD114A0CC1788_MC50_DISSCR_WrongFormat.A2L");
    servClient.executeCompliReview(A2L_FILE_PATH_COMPLI_WRONGFORMAT, metaData,
        new HashSet<>(Arrays.asList(HEX_FILE_PATH_COMPLI)), OUTPUT_PATH);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test 3.33
   *
   * @throws IOException error reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCompliAttrValueNotDefined() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("FEAVAL.ATTR_VALUE_NOT_DEFINED"));
    this.thrown.expectMessage("Value not set for the following attribute(s)");

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    CompliReviewInputMetaData metaData = createInputMetaData2();

    servClient.executeCompliReview(A2L_FILE_PATH_MISSING_COMPLI, metaData,
        new HashSet<>(Arrays.asList(HEX_FILE_PATH_MISSING_COMPLI)), OUTPUT_PATH);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test 3.37 - DEPRECATED
   *
   * @throws IOException error reading zip file
   * @throws ApicWebServiceException error during service call
   */

  public void testCompliReviewChildAttrMissing() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("TODO"));
    this.thrown.expectMessage("The following attributes are defined at child level");

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    CompliReviewInputMetaData metaData = createInputMetaData2();
    // 2108485634 - Variant AT with attribute 'Emission Standard Certification defined at sub variant level'
    Map<Long, Long> hexFilePidcElement = new HashMap<>();
    hexFilePidcElement.put(1L, 2108485634L);
    // V2_Compli_MissingAttr (V2)
    metaData.setHexFilePidcElement(hexFilePidcElement);
    servClient.executeCompliReview(A2L_FILE_PATH_MISSING_COMPLI, metaData,
        new HashSet<>(Arrays.asList(HEX_FILE_PATH_MISSING_COMPLI)), OUTPUT_PATH);

    testResults(OUTPUT_PATH);
  }

  /**
   * Test 3.36
   *
   * @throws IOException error reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCompliReviewPidcMandatory() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("FEAVAL.PIDC_ELEMENT_MISSING"));
    this.thrown.expectMessage(
        "PIDC element is mandatory since some of the parameters being reviewed have attribute dependencies");

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    CompliReviewInputMetaData metaData = createInputMetaData2();
    Map<Long, Long> hexFilePidcElement = new HashMap<>();
    metaData.setHexFilePidcElement(hexFilePidcElement);
    servClient.executeCompliReview(A2L_FILE_PATH_MISSING_COMPLI, metaData,
        new HashSet<>(Arrays.asList(HEX_FILE_PATH_MISSING_COMPLI)), OUTPUT_PATH);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  private CompliReviewInputMetaData createInputMetaData1() {
    CompliReviewInputMetaData input = setA2lHexData1();
    Map<Long, Long> hexFilePidcElement = new HashMap<>();
    hexFilePidcElement.put(1L, 768112367L);
    input.setHexFilePidcElement(hexFilePidcElement);

    return input;
  }

  /**
   * Test 3.34
   *
   * @throws IOException error reading zip file
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCompliReviewInsufficientAccess() throws IOException, ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("FEAVAL.INSUFFICIENT_PIDC_ACCESS"));
    this.thrown.expectMessage(StringStartsWith
        .startsWith("User does not have sufficient acess rights to get the details of the following attributes"));

    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    servClient.setClientConfiguration(createClientConfigTestUser("CTN8COB"));

    CompliReviewInputMetaData metaData = createInputMetaData3();
    Map<Long, Long> hexFilePidcElement = new HashMap<>();
    hexFilePidcElement.put(1L, 2208766629L);
    metaData.setHexFilePidcElement(hexFilePidcElement);
    servClient.executeCompliReview(A2L_FILE_PATH, metaData, new HashSet<>(Arrays.asList(HEX_FILE_ONE_PATH)),
        OUTPUT_PATH);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * @return
   */
  private CompliReviewInputMetaData setA2lHexData1() {
    CompliReviewInputMetaData input = new CompliReviewInputMetaData();
    input.setA2lFileName("A2L_MMD114A0CC1788_MC50_DISCR.A2L");
    input.setPverName("MMD114A0CC1788");
    input.setPverVariant("MC50_DISCR");
    input.setPverRevision("0");
    input.setWebflowID("1001");

    Map<Long, String> hexfileIdxMap = new HashMap<>();
    hexfileIdxMap.put(1L, "MMD114A0CC1788_MC50_DISCR_LC.hex");
    input.setHexfileIdxMap(hexfileIdxMap);
    return input;
  }

  private CompliReviewInputMetaData createInputMetaData3() {
    CompliReviewInputMetaData input = new CompliReviewInputMetaData();

    input.setA2lFileName("A2L_P1337_M240_Wline_VM_CY_customer_patch_mm3.a2l");
    input.setPverName("P1337_MEDCC79");
    input.setPverVariant("M240");
    input.setPverRevision("0");
    input.setWebflowID("1001");

    Map<Long, String> hexfileIdxMap = new HashMap<>();
    hexfileIdxMap.put(1L, HEX_FILE_NAME);

    input.setHexfileIdxMap(hexfileIdxMap);
    Map<Long, Long> hexFilePidcElement = new HashMap<>();
    // COMPLI_ErrorCode_Testing(V2_Insuffi_Access)
    hexFilePidcElement.put(1L, 2208766629L);
    input.setHexFilePidcElement(hexFilePidcElement);

    return input;
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

    input.setHexfileIdxMap(hexfileIdxMap);

    Map<Long, Long> hexFilePidcElement = new HashMap<>();
    hexFilePidcElement.put(1L, 768112367L);
    hexFilePidcElement.put(2L, 768112367L);
    input.setHexFilePidcElement(hexFilePidcElement);

    return input;
  }

  private CompliReviewInputMetaData createInputMetaData2() {
    CompliReviewInputMetaData input = new CompliReviewInputMetaData();

    input.setA2lFileName("A2L_MMD114A0CC1788_MD00_withGroups.A2L");
    input.setPverName("MMD114A0CC1788");
    input.setPverVariant("MD00");
    input.setPverRevision("12");


    Map<Long, String> hexfileIdxMap = new HashMap<>();
    hexfileIdxMap.put(1L, "BXAZ8ERBIIA5_X810_20894463.hex");

    input.setHexfileIdxMap(hexfileIdxMap);
    Map<Long, Long> hexFilePidcElement = new HashMap<>();
    hexFilePidcElement.put(1L, 2108485631L);

    input.setHexFilePidcElement(hexFilePidcElement);

    return input;
  }


  /**
   * @throws IOException
   * @throws FileNotFoundException
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

}
