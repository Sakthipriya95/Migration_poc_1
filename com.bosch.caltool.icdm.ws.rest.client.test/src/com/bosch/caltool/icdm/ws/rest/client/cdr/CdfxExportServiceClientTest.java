/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.WpRespModel;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.cdfx.CdfxExportInput;
import com.bosch.caltool.icdm.model.cdr.cdfx.CdfxExportOutput;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lResponsibilityServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author pdh2cob
 */
public class CdfxExportServiceClientTest extends AbstractRestClientTest {

  /**
   * 
   */
  private static final long PIDC_A2L_ID_3 = 38995215532L;

  /**
   *
   */
  private static final String NO_VARIANT_NAME = "<NO-VARIANT>";

  /**
   *
   */
  private static final String VARIANT_NAME_2 = "TestVariant2";

  /**
   *
   */
  private static final long A2L_WP_ID = 38557983711L;

  /**
   *
   */
  private static final long A2L_RESP_ID = 38557983712L;

  /**
   *
   */
  private static final String WP1 = "wp1";

  /**
   *
   */
  private static final long INVALID_VAR_ID = 1234567L;

  /**
   * Variant TestVariant2
   */
  private static final long VAR_ID_1 = 38570378750L;

  /**
   * A2L File: X_Testcustomer->Gasoline Engine->PC - Passenger Car->ME(D)17->Test_Multiple_Variants
   * (1.0)->MMD114A0CC1788->MMD114A0CC1788_MC50_DISCR.A2L
   */
  private static final long PIDC_A2L_ID_2 = 38557983702L;

  /**
   *
   */
  private static final String ERROR_MSG = "No review results found for given input";

  // MMD114A0CC1788_MD00_withGroups.A2L
  private static final Long PIDC_A2L_ID = 1328585266L;

  // P1337_M240_Wline_VM_CY_customer_patch_mm3.a2l

  private static final Long PIDC_A2L_ID_1 = 5113098693L;

  // AT_COMPLI
  private static final Long AT_COMPLI_VARIANT_ID = 1537160237L;

  // AT
  private static final Long AT_VARIANT_ID = 38557983700L;

  // Bosch
  private static final Long BOSCH_RESP_ID = 1579486738L;

  // Bosch id for PIDC_A2L_ID_1
  private static final Long BOSCH_RESP_ID_2 = 5113098694L;

  // 105) Brake system
  private static final Long A2L_WP_ID_2 = 2167024953L;

  // DEFAULT for PIDC_A2L_ID_1
  private static final Long A2L_WP_ID_3 = 5113098714L;

  private static final String OUTPUT_PATH = getUserTempDir() + "//CDFX_EXPORT//";

  private static final String INVALID_OUTPUT_PATH = "Z:";

  /**
   *
   */
  private static final String CDFX_READINESS_FILE_PATH = OUTPUT_PATH + ApicConstants.CDFX_READINESS_FILE;


  /**
   *
   */
  private static final String BOSCH = "RB";

  /**
   *
   */
  private static final String DEFAULT_WP = "_DEFAULT_WP";

  /**
   *
   */
  private static final String INAVALID_WP = "INAVALID_WP_NAME";

  /**
  *
  */
  private static final String VARIANT_NAME = "TestVariant1";

  /**
   *
   */
  @Before
  public void init() {
    if (!new File(OUTPUT_PATH).exists()) {
      new File(OUTPUT_PATH).mkdir();
    }
  }


  /**
   * Test Cdfx export functionality with invalid variant
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testExportCdfxInvalidVariant() throws ApicWebServiceException {
    String outputFileName = CDRConstants.CDFX_FILE_NAME + "_" + System.currentTimeMillis() + ".zip";
    CdfxExportInput inputModel = new CdfxExportInput();
    inputModel.setPidcA2lId(PIDC_A2L_ID_2);

    Set<PidcVariant> inputPidcVariants = new HashSet<>();
    PidcVariant pidcVariant = new PidcVariant();
    pidcVariant.setId(INVALID_VAR_ID);
    pidcVariant.setName("TestInvalidVariant");
    inputPidcVariants.add(pidcVariant);

    inputModel.setScope(WpRespType.RB.getCode());
    inputModel.setReadinessFlag(true);
    inputModel.setVariantsList(inputPidcVariants);

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage(ERROR_MSG);

    new CdfxExportServiceClient().exportCdfx(inputModel, OUTPUT_PATH, outputFileName);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }


  /**
   * Test Cdfx export functionality with scope Robert Bosch
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testExportCdfxWithScope() throws ApicWebServiceException {

    String outputFileName = CDRConstants.CDFX_FILE_NAME + "_" + System.currentTimeMillis() + ".zip";

    CdfxExportInput inputModel = new CdfxExportInput();
    inputModel.setPidcA2lId(PIDC_A2L_ID_2);

    Set<PidcVariant> inputPidcVariants = new HashSet<>();
    PidcVariant pidcVariant = new PidcVariant();
    pidcVariant.setId(AT_VARIANT_ID);
    pidcVariant.setName(VARIANT_NAME);
    inputPidcVariants.add(pidcVariant);
    inputModel.setVariantsList(inputPidcVariants);

    inputModel.setScope(WpRespType.RB.getCode());
    inputModel.setReadinessFlag(true);
    CdfxExportOutput outputModel = new CdfxExportServiceClient().exportCdfx(inputModel, OUTPUT_PATH, outputFileName);

    assertNotNull(outputModel);
  }


  /**
   * Test Cdfx export functionality with NO VARIANT
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testExportCdfxWithoutVariant() throws ApicWebServiceException {

    String outputFileName = CDRConstants.CDFX_FILE_NAME + "_" + System.currentTimeMillis() + ".zip";

    CdfxExportInput inputModel = new CdfxExportInput();
    inputModel.setPidcA2lId(PIDC_A2L_ID_1);
    inputModel.setReadinessFlag(true);

    WpRespModel wpRespModel = createWpRespModel(DEFAULT_WP, BOSCH_RESP_ID_2, A2L_WP_ID_3, BOSCH);
    inputModel.getWpRespModelList().add(wpRespModel);

    CdfxExportOutput outputModel = new CdfxExportServiceClient().exportCdfx(inputModel, OUTPUT_PATH, outputFileName);

    assertNotNull(outputModel);
  }


  /**
   * @param wpName
   * @param a2lRespId
   * @param a2lwpId
   * @param wpRespName
   * @return
   * @throws ApicWebServiceException
   */
  private WpRespModel createWpRespModel(final String wpName, final Long a2lRespId, final Long a2lwpId,
      final String wpRespName)
      throws ApicWebServiceException {
    WpRespModel wpRespModel = new WpRespModel();
    A2lResponsibility a2lResponsibility = new A2lResponsibilityServiceClient().get(a2lRespId);
    wpRespModel.setWpName(wpName);
    wpRespModel.setA2lResponsibility(a2lResponsibility);
    wpRespModel.setA2lWpId(a2lwpId);
    wpRespModel.setWpRespName(wpRespName);

    return wpRespModel;
  }


  /**
   * Test Cdfx export functionality with scope work package
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateCdfxWithWP() throws ApicWebServiceException {

    String outputFileName = CDRConstants.CDFX_FILE_NAME + "_" + System.currentTimeMillis() + ".zip";

    CdfxExportInput inputModel = new CdfxExportInput();
    inputModel.setPidcA2lId(PIDC_A2L_ID_2);

    Set<PidcVariant> inputPidcVariants = new HashSet<>();
    PidcVariant pidcVariant = new PidcVariant();
    pidcVariant.setId(AT_VARIANT_ID);
    pidcVariant.setName(VARIANT_NAME);
    inputPidcVariants.add(pidcVariant);
    inputModel.setVariantsList(inputPidcVariants);
    // create constants
    WpRespModel wpRespModel = createWpRespModel(WP1, A2L_RESP_ID, A2L_WP_ID, WpRespType.RB.getCode());
    inputModel.getWpRespModelList().add(wpRespModel);

    inputModel.setReadinessFlag(true);
    CdfxExportOutput outputModel = new CdfxExportServiceClient().exportCdfx(inputModel, OUTPUT_PATH, outputFileName);

    assertNotNull(outputModel);
  }


  /**
   * Test Cdfx export functionality with scope invalid work package
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testExportCdfxInvalidWP() throws ApicWebServiceException {

    String outputFileName = CDRConstants.CDFX_FILE_NAME + "_" + System.currentTimeMillis() + ".zip";

    CdfxExportInput inputModel = new CdfxExportInput();
    inputModel.setPidcA2lId(PIDC_A2L_ID);

    Set<PidcVariant> inputPidcVariants = new HashSet<>();
    PidcVariant pidcVariant = new PidcVariant();
    pidcVariant.setId(AT_COMPLI_VARIANT_ID);
    pidcVariant.setName("AT_COMPLI");
    inputPidcVariants.add(pidcVariant);
    inputModel.setVariantsList(inputPidcVariants);

    WpRespModel wpRespModel = createWpRespModel(INAVALID_WP, BOSCH_RESP_ID, 0L, BOSCH);
    assertNotNull("Created WP Resp Model is not null", wpRespModel);
    inputModel.getWpRespModelList().add(wpRespModel);

    inputModel.setReadinessFlag(true);
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("No review information found for CDFx creation");
    new CdfxExportServiceClient().exportCdfx(inputModel, OUTPUT_PATH, outputFileName);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test Cdfx export functionality with invalid pidc a2l
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testExportCdfxInvalidPidcA2l() throws ApicWebServiceException {

    String outputFileName = CDRConstants.CDFX_FILE_NAME + "_" + System.currentTimeMillis() + ".zip";

    CdfxExportInput inputModel = new CdfxExportInput();
    inputModel.setPidcA2lId(0L);
    WpRespModel wpRespModel = createWpRespModel("105) Brake system", BOSCH_RESP_ID, A2L_WP_ID_2, BOSCH);
    assertNotNull("Created WP Resp Model is not null", wpRespModel);
    inputModel.getWpRespModelList().add(wpRespModel);
    inputModel.setReadinessFlag(true);
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("PIDC A2L File with ID '0' not found");
    new CdfxExportServiceClient().exportCdfx(inputModel, OUTPUT_PATH, outputFileName);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test Cdfx export functionality with readiness condition
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCdfxReadinessConditionFile() throws ApicWebServiceException {
    byte[] cdfxReadinessConditionFile = new CdfxExportServiceClient().getCdfxReadinessConditionFile(OUTPUT_PATH);

    assertNotNull("CDFX Readiness Condition File Byte Array is not null", cdfxReadinessConditionFile);
    assertTrue("CDFX Readiness Condition File is created successfully in the Directory",
        new File(CDFX_READINESS_FILE_PATH).exists());
  }

  /**
   * Test Cdfx export functionality with readiness condition invalid file path
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCdfxReadinessConditionFileInvalidPath() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Error while downloading file cdfx_readiness_condition.zip : " + INVALID_OUTPUT_PATH +
        File.separator + ApicConstants.CDFX_READINESS_FILE + " (The system cannot find the path specified)");

    new CdfxExportServiceClient().getCdfxReadinessConditionFile(INVALID_OUTPUT_PATH);

    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * 608270: Create test data for parameters with no caldata objects during CDFx delivery
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testExportCdfxParamWithNoCalObj() throws ApicWebServiceException {

    String outputFileName = CDRConstants.CDFX_FILE_NAME + "_" + System.currentTimeMillis() + ".zip";

    CdfxExportInput inputModel = new CdfxExportInput();
    // MMD114A0CC1788_MC50_DISCR.A2L
    inputModel.setPidcA2lId(PIDC_A2L_ID_2);

    Set<PidcVariant> inputPidcVariants = new HashSet<>();
    PidcVariant pidcVariant = new PidcVariant();
    // Variant TestVariant2
    pidcVariant.setId(VAR_ID_1);
    pidcVariant.setName(VARIANT_NAME_2);
    inputPidcVariants.add(pidcVariant);
    inputModel.setVariantsList(inputPidcVariants);

    inputModel.setScope(WpRespType.RB.getCode());
    inputModel.setReadinessFlag(true);

    CdfxExportOutput outputModel = new CdfxExportServiceClient().exportCdfx(inputModel, OUTPUT_PATH, outputFileName);
    assertNotNull(outputModel);
  }

  /**
   * Test Cdfx Export with One File Per Workpackage checkbox enable with no variant
   *
   * @throws ApicWebServiceException ApicWebServiceException
   */
  @Test
  public void testExpCdfxWithOneFilePerWpChkBoxWithNoVar() throws ApicWebServiceException {

    String outputFileName = CDRConstants.CDFX_FILE_NAME + "_" + System.currentTimeMillis() + ".zip";
    CdfxExportInput inputModel = new CdfxExportInput();
    inputModel.setPidcA2lId(PIDC_A2L_ID_1);
    inputModel.setOneFilePerWpFlag(true);
    inputModel.setScope(WpRespType.RB.getCode());
    inputModel.setReadinessFlag(true);
    CdfxExportOutput outputModel = new CdfxExportServiceClient().exportCdfx(inputModel, OUTPUT_PATH, outputFileName);
    assertNotNull(outputModel);
  }

  /**
   * Test Cdfx Export with One File Per Workpackage checkbox enable with variant
   *
   * @throws ApicWebServiceException ApicWebServiceException
   */
  @Test
  public void testExpCdfxWithOneFilePerWpChkBoxWithVar() throws ApicWebServiceException {

    String outputFileName = CDRConstants.CDFX_FILE_NAME + "_" + System.currentTimeMillis() + ".zip";
    CdfxExportInput inputModel = new CdfxExportInput();
    inputModel.setPidcA2lId(PIDC_A2L_ID_2);

    Set<PidcVariant> inputPidcVariants = new HashSet<>();
    PidcVariant pidcVariant = new PidcVariant();
    pidcVariant.setId(AT_VARIANT_ID);
    pidcVariant.setName(VARIANT_NAME);
    inputPidcVariants.add(pidcVariant);
    inputModel.setVariantsList(inputPidcVariants);

    inputModel.setOneFilePerWpFlag(true);
    inputModel.setScope(WpRespType.RB.getCode());
    inputModel.setReadinessFlag(true);

    CdfxExportOutput outputModel = new CdfxExportServiceClient().exportCdfx(inputModel, OUTPUT_PATH, outputFileName);
    assertNotNull(outputModel);
  }

  /**
   * Test Cdfx Export with multiple variants
   *
   * @throws ApicWebServiceException ApicWebServiceException
   */
  @Test
  public void testExportCdfxWithMultipleVariants() throws ApicWebServiceException {
    String outputFileName = CDRConstants.CDFX_FILE_NAME + "_" + System.currentTimeMillis() + ".zip";
    CdfxExportInput inputModel = new CdfxExportInput();
    inputModel.setPidcA2lId(PIDC_A2L_ID_2);

    createInputData(inputModel);

    inputModel.setScope(WpRespType.RB.getCode());
    inputModel.setReadinessFlag(true);

    CdfxExportOutput outputModel = new CdfxExportServiceClient().exportCdfx(inputModel, OUTPUT_PATH, outputFileName);
    assertNotNull(outputModel);
  }

  /**
   * Test Cdfx Export with multiple variants with One File Per Work Package check box
   *
   * @throws ApicWebServiceException ApicWebServiceException
   */
  @Test
  public void testExportCdfxWithMultiVarsWithOneFileWpCheckbox() throws ApicWebServiceException {
    String outputFileName = CDRConstants.CDFX_FILE_NAME + "_" + System.currentTimeMillis() + ".zip";
    CdfxExportInput inputModel = new CdfxExportInput();
    inputModel.setPidcA2lId(PIDC_A2L_ID_2);

    createInputData(inputModel);

    inputModel.setScope(WpRespType.RB.getCode());
    inputModel.setReadinessFlag(true);
    inputModel.setOneFilePerWpFlag(true);

    CdfxExportOutput outputModel = new CdfxExportServiceClient().exportCdfx(inputModel, OUTPUT_PATH, outputFileName);
    assertNotNull(outputModel);
  }


  /**
   * @param inputModel test data
   */
  private void createInputData(final CdfxExportInput inputModel) {
    Set<PidcVariant> inputPidcVariants = new HashSet<>();
    PidcVariant v1 = new PidcVariant();
    v1.setId(AT_VARIANT_ID);
    v1.setName(VARIANT_NAME);
    inputPidcVariants.add(v1);
    PidcVariant v2 = new PidcVariant();
    v2.setId(VAR_ID_1);
    v2.setName(VARIANT_NAME_2);
    inputPidcVariants.add(v2);
    inputModel.setVariantsList(inputPidcVariants);
  }

  /**
   * Test cdfx export functionality from resp node
   *
   * @throws ApicWebServiceException ApicWebServiceException
   */
  @Test
  public void testExportCdfxFromRespNode() throws ApicWebServiceException {
    String outputFileName = CDRConstants.CDFX_FILE_NAME + "_" + System.currentTimeMillis() + ".zip";
    CdfxExportInput inputModel = new CdfxExportInput();
    inputModel.setPidcA2lId(PIDC_A2L_ID_3);

    Set<PidcVariant> inputPidcVariants = new HashSet<>();
    PidcVariant v1 = new PidcVariant();
    v1.setName(NO_VARIANT_NAME);
    v1.setId(-1L);
    inputPidcVariants.add(v1);
    inputModel.setVariantsList(inputPidcVariants);

    inputModel.setScope(WpRespType.RB.getCode());
    inputModel.setReadinessFlag(true);
    inputModel.setOneFilePerWpFlag(true);

    CdfxExportOutput outputModel = new CdfxExportServiceClient().exportCdfx(inputModel, OUTPUT_PATH, outputFileName);
    assertNotNull(outputModel);
  }
}