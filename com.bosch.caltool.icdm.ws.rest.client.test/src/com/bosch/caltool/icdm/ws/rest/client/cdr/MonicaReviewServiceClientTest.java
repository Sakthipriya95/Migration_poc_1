/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.bosch.caltool.icdm.model.cdr.CDRConstants.DELTA_REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResultLink;
import com.bosch.caltool.icdm.model.cdr.MonicaInputData;
import com.bosch.caltool.icdm.model.cdr.MonicaInputModel;
import com.bosch.caltool.icdm.model.cdr.MonicaReviewData;
import com.bosch.caltool.icdm.model.cdr.MonicaReviewInputData;
import com.bosch.caltool.icdm.model.cdr.MonicaReviewOutput;
import com.bosch.caltool.icdm.model.cdr.MonicaReviewOutputData;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author say8cob
 */
/**
 * @author say8cob
 */
public class MonicaReviewServiceClientTest extends AbstractRestClientTest {

  /**
   * 
   */
  private static final String ACC_PED_N_MAX_C = "AccPed_nMax_C";

  private static final String USER_NAME = "SMN6KOR";

  private static final String CAL_USER_NAME = "SAY8COB";

  private static final String MONICA_TECH_USER = "UTS";

  private static final Long PIDC_A2L_ID = 777985566L;

  private static final Long VARIANT_ID = 777160667L;

  private static final String DESCRIPTION = "MonicaReview for Testing";

  private static final String DESCRIPTION_01 = "MonicaReview for Testing for Normal Review";

  private static final String DESCRIPTION_02 = "MonicaReview for Testing for Delta Review";

  private static final String DESCRIPTION_03 = "MonicaReview for Testing for Negative case";


  private static final String MONICA_EXCEL_PATH = "testdata/monica/DMG1002AH2C1584_MY18F11-17.04.04_16-46-41_icdm.xlsx";

  private static final String DCM_PATH =
      "testdata/monica/EGAS_PVS 1_N2_EA839_ULEV70_DMG1002AH2P1584_MY18F11_U8F11n2n6-SY1_X712.dcm";

  private static final String DCM_PATH1 = "testdata/monica/Dataset1_2.dcm";

  private static final String MONICA_EXCEL_PATH1 = "testdata/monica/Testcase Different Datasets 2.xlsx";

  // Test Data for negative testcase

  private static final String INVALID_DCM_PATH_1 =
      "testdata/monica/INVALID_EGAS_PVS 1_N2_EA839_ULEV70_DMG1002AH2P1584_MY18F11_U8F11n2n6-SY1_X712.dcm";

  private static final String INVALID_DCM_PATH_2 =
      "testdata/monica/INVALID_DMG1002AH2C1584_MY18F11-17.04.04_16-46-41_icdm.xlsx";

  private static final String INVALID_MONICA_EXCEL_PATH_2 =
      "testdata/monica/INVALID_EGAS_PVS 1_N2_EA839_ULEV70_DMG1002AH2P1584_MY18F11_U8F11n2n6-SY1_X712.dcm";

  private static final String EMPTY_DCM_PATH =
      "testdata/monica/EMPTY_EGAS_PVS 1_N2_EA839_ULEV70_DMG1002AH2P1584_MY18F11_U8F11n2n6-SY1_X712.dcm";

  private static final String INVALID_USER_NAME = "SAYCOB";

  // Belong to PVER DMG1002AH2C1584
  private static final Long PIDC_A2L_ID_WITH_DIFF_PVER = 785817907L;

  private static final Long INVALID_PIDC_A2L_ID_1 = 0L;

  private static final Long INVALID_VARIANT_ID = 0L;

  // Error Messages for negative testcase

  private static final String ERR_INVALID_USER_NAME = "The user name ";

  private static final String ERR_FILE_NOT_EXIST = " is invalid, Provide a valid file.";

  private static final String ERR_PIDC_VARIANT_IS_NULL =
      "Review could not be created. The given Variant id is not existing. Provide an existing Variant ID.";

  private static final String ERR_INVD_DCM_FILE =
      "Error while parsing the DCM file: Invalid DCM File - Unknown Keyword Found at 1";

  private static final String ERR_NO_CAL_INFO_IN_DCMFILE =
      "Errors found when parsing the DCM file.Input Stream is Empty";

  private static final String ERR_MONICAPROTOCOL_STATUS_EMPTY = "Provided MoniCa Protocol parameter status is Empty";

  // Constants for INTERNAL monica service

  private static final String INTERNAL_DCM_FILE1 = "testdata/monica/monicaInternalService/Dataset1_1.dcm";

  private static final String INTERNAL_DCM_FILE_NAME1 = "Dataset1_1.dcm";

  private static final String INTERNAL_DCM_FILE_NAME2 = "Dataset2_1.dcm";

  private static final String INTERNAL_DCM_FILE2 = "testdata/monica/monicaInternalService/Dataset2_1.dcm";

  private static final String INTERNAL_MONICA_FILE_NAME1 = "TestcaseDifferentDatasets.xlsx";

  private static final String INTERNAL_MONICA_FILE_NAME2 = "TestcaseDifferentDatasets2.xlsx";

  private static final String INTERNAL_MONICA_EXCEL_FILE1 =
      "testdata/monica/monicaInternalService/TestcaseDifferentDatasets.xlsx";

  private static final String INTERNAL_MONICA_EXCEL_FILE2 =
      "testdata/monica/monicaInternalService/TestcaseDifferentDatasets2.xlsx";

  private static final String INTERNAL_SHEET_NAME = "Dataset_1-iCDM_Check";

  // TEST_HONDA_MONICA_REPORT_SAY8COB (v1)
  // 159-HONDA_XDRA_4cyl_2.0L_GDI_TC_HON1793A1_V130S11_V1.A2L
  private static final Long INTERNAL_PIDC_A2L_ID = 3063034390l;

  private static final Long INTERNAL_INVALID_PIDC_A2L_ID = 777985566L;

  private static final Long INTERNAL_VARIANT_ID = 3063034398l;

  // icdm:cdrid,3121659928-3063034381-3063034398
  private static final Long INTERNAL_PARENT_RES_ID = 3121659928l;

  /**
   * Review comment 1
   */
  private static final String RVW_COMMENT_1 = "TESTCOMMENT1";

  /**
   * Review comment 2
   */
  private static final String RVW_COMMENT_2 = "TESTCOMMENT2";

  /**
   * Review comment 3
   */
  private static final String RVW_COMMENT_3 = "TESTCOMMENT3";

  /**
   * Test using json file as meta data
   *
   * @throws ApicWebServiceException error during service call
   * @throws InterruptedException when thread is interrunpted
   */
  @Test
  public void test01() throws ApicWebServiceException, InterruptedException {
    CDRReviewResultServiceClient client = new CDRReviewResultServiceClient();

    MonicaReviewServiceClient servClient = new MonicaReviewServiceClient();
    MonicaReviewInputData createMonicaInputData = createMonicaInputData(PIDC_A2L_ID, VARIANT_ID, DESCRIPTION,
        createMonicaReviewDataList(), USER_NAME, USER_NAME, CAL_USER_NAME);
    CDRReviewResultLink executeMonicaReview =
        servClient.executeMonicaReview(createMonicaInputData, DCM_PATH, MONICA_EXCEL_PATH);
    assertTrue(executeMonicaReview.getResultId() != 0);
    assertNotNull(executeMonicaReview.getCdrLinkUrl());
    assertNotNull(executeMonicaReview.getCdrLinkDisplayText());
    assertNotNull(executeMonicaReview.getMonicaReviewStatus());
    client.deleteReviewResult(client.getById(executeMonicaReview.getResultId()), false);
  }


  /**
   * Test using json file as meta data for Monica technical user (UTS)
   *
   * @throws ApicWebServiceException error during service call
   * @throws InterruptedException when thread is interrunpted
   */
  @Test
  public void test01A() throws ApicWebServiceException, InterruptedException {
    CDRReviewResultServiceClient client = new CDRReviewResultServiceClient();

    MonicaReviewServiceClient servClient = new MonicaReviewServiceClient();
    MonicaReviewInputData createMonicaInputData = createMonicaInputData(PIDC_A2L_ID, VARIANT_ID, DESCRIPTION,
        createMonicaReviewDataList(), USER_NAME, MONICA_TECH_USER, CAL_USER_NAME);
    CDRReviewResultLink executeMonicaReview =
        servClient.executeMonicaReview(createMonicaInputData, DCM_PATH, MONICA_EXCEL_PATH);
    assertTrue(executeMonicaReview.getResultId() != 0);
    assertNotNull(executeMonicaReview.getCdrLinkUrl());
    assertNotNull(executeMonicaReview.getCdrLinkDisplayText());
    assertNotNull(executeMonicaReview.getMonicaReviewStatus());
    client.deleteReviewResult(client.getById(executeMonicaReview.getResultId()), false);

  }

  /**
   * Positive Test for internal monica review
   *
   * @throws ApicWebServiceException error during service call
   * @throws InterruptedException when thread is interrunpted
   */
  @Test
  public void testMonicaReviewInternal01() throws ApicWebServiceException, InterruptedException {
    CDRReviewResultServiceClient client = new CDRReviewResultServiceClient();

    MonicaReviewServiceClient servClient = new MonicaReviewServiceClient();

    MonicaInputModel monicaInputModel = createMonicaReviewInputData(INTERNAL_PIDC_A2L_ID, DESCRIPTION_01, USER_NAME,
        USER_NAME, CAL_USER_NAME, false, null);
    MonicaReviewOutput executeMonicaReview = servClient.executeMonicaReviewInternal(monicaInputModel);

    for (MonicaReviewOutputData monicaReviewOutputData : executeMonicaReview.getMonicaReviewOutputDataList()) {
      if (!monicaReviewOutputData.isReviewFailed()) {
        assertTrue(monicaReviewOutputData.getCdrReviewResult() != null);
        testOutputInternalPositiveCase(monicaReviewOutputData);
        client.deleteReviewResult(client.getById(monicaReviewOutputData.getCdrReviewResult().getId()), false);
      }
    }
  }


  /**
   * Positive Test for internal monica review for Monica Technical User (UTS)
   *
   * @throws ApicWebServiceException error during service call
   * @throws InterruptedException when thread is interrunpted
   */
  @Test
  public void testMonicaReviewInternal01A() throws ApicWebServiceException, InterruptedException {
    CDRReviewResultServiceClient client = new CDRReviewResultServiceClient();

    MonicaReviewServiceClient servClient = new MonicaReviewServiceClient();

    MonicaInputModel monicaInputModel = createMonicaReviewInputData(INTERNAL_PIDC_A2L_ID, DESCRIPTION_01, USER_NAME,
        MONICA_TECH_USER, CAL_USER_NAME, false, null);
    MonicaReviewOutput executeMonicaReview = servClient.executeMonicaReviewInternal(monicaInputModel);

    for (MonicaReviewOutputData monicaReviewOutputData : executeMonicaReview.getMonicaReviewOutputDataList()) {
      if (!monicaReviewOutputData.isReviewFailed()) {
        assertTrue(monicaReviewOutputData.getCdrReviewResult() != null);
        testOutputInternalPositiveCase(monicaReviewOutputData);
        client.deleteReviewResult(client.getById(monicaReviewOutputData.getCdrReviewResult().getId()), false);
      }
    }
  }

  /**
   * Positive Test for internal monica review delta review
   *
   * @throws ApicWebServiceException error during service call
   * @throws InterruptedException when thread is interrunpted
   */
  @Test
  public void testMonicaReviewInternal02() throws ApicWebServiceException, InterruptedException {
    CDRReviewResultServiceClient client = new CDRReviewResultServiceClient();

    MonicaReviewServiceClient servClient = new MonicaReviewServiceClient();

    MonicaInputModel monicaInputModel = createMonicaReviewInputData(INTERNAL_PIDC_A2L_ID, DESCRIPTION_02, USER_NAME,
        USER_NAME, CAL_USER_NAME, true, DELTA_REVIEW_TYPE.DELTA_REVIEW.getDbType());
    MonicaReviewOutput executeMonicaReview = servClient.executeMonicaReviewInternal(monicaInputModel);

    for (MonicaReviewOutputData monicaReviewOutputData : executeMonicaReview.getMonicaReviewOutputDataList()) {
      if (!monicaReviewOutputData.isReviewFailed()) {
        assertNotNull(monicaReviewOutputData.getCdrReviewResult());
        testOutputInternalPositiveCase(monicaReviewOutputData);
        client.deleteReviewResult(client.getById(monicaReviewOutputData.getCdrReviewResult().getId()), false);
      }
    }
  }

  /**
   * Negative Test for internal monica review
   *
   * @throws ApicWebServiceException error during service call
   * @throws InterruptedException when thread is interrunpted
   */
  @Test
  public void testMonicaReviewInternal03() throws ApicWebServiceException, InterruptedException {
    MonicaReviewServiceClient servClient = new MonicaReviewServiceClient();

    MonicaInputModel monicaInputModel = createMonicaReviewInputData(INTERNAL_INVALID_PIDC_A2L_ID, DESCRIPTION_03,
        USER_NAME, USER_NAME, CAL_USER_NAME, false, null);
    MonicaReviewOutput executeMonicaReview = servClient.executeMonicaReviewInternal(monicaInputModel);

    for (MonicaReviewOutputData monicaReviewOutputData : executeMonicaReview.getMonicaReviewOutputDataList()) {
      if (monicaReviewOutputData.isReviewFailed()) {
        assertTrue((monicaReviewOutputData.getErrorMsg() != null) || !monicaReviewOutputData.getErrorMsg().isEmpty());
        assertTrue(monicaReviewOutputData.isReviewFailed());
      }
    }
  }

  private void testOutputInternalPositiveCase(final MonicaReviewOutputData monicaReviewOutputData) {
    assertTrue(monicaReviewOutputData.getRvwVariant() != null);
    assertTrue((monicaReviewOutputData.getErrorMsg() == null) || monicaReviewOutputData.getErrorMsg().isEmpty());
    assertTrue(!monicaReviewOutputData.isReviewFailed());
  }


  private MonicaInputModel createMonicaReviewInputData(final Long pidcA2lId, final String desc,
      final String ownerUserName, final String audUserName, final String calUserName, final boolean isDeltaReview,
      final String deltaReviewType) {
    MonicaInputModel monicaInputModel = new MonicaInputModel();

    monicaInputModel.setAudUserName(audUserName);
    monicaInputModel.setCalEngUserName(calUserName);
    monicaInputModel.setDescription(desc);
    monicaInputModel.setOwnUserName(ownerUserName);
    monicaInputModel.setPidcA2lId(pidcA2lId);
    monicaInputModel.setMonicaInputDataList(createMonicaInputData(isDeltaReview, deltaReviewType));

    return monicaInputModel;
  }

  private List<MonicaInputData> createMonicaInputData(final boolean isDeltaReview, final String deltaReviewType) {
    List<MonicaInputData> monicaInputDatas = new ArrayList<>();

    MonicaInputData monicaInputData1 = new MonicaInputData();
    monicaInputData1.setDcmFileName(INTERNAL_DCM_FILE_NAME1);
    monicaInputData1.setDcmFilePath(INTERNAL_DCM_FILE1);
    monicaInputData1.setDeltaReview(isDeltaReview);
    monicaInputData1.setDeltaReviewType(deltaReviewType);
    if (isDeltaReview) {
      monicaInputData1.setOrgResultId(INTERNAL_PARENT_RES_ID);
    }
    monicaInputData1.setMonicaExcelFileName(INTERNAL_MONICA_FILE_NAME1);
    monicaInputData1.setMonicaExcelFilePath(INTERNAL_MONICA_EXCEL_FILE1);
    monicaInputData1.setSelMoniCaSheet(INTERNAL_SHEET_NAME);
    monicaInputData1.setVariantId(INTERNAL_VARIANT_ID);
    monicaInputData1.setMonicaObject(createMonicaReviewDataInternalList1());


    MonicaInputData monicaInputData2 = new MonicaInputData();
    monicaInputData2.setDcmFileName(INTERNAL_DCM_FILE_NAME2);
    monicaInputData2.setDcmFilePath(INTERNAL_DCM_FILE2);
    monicaInputData2.setDeltaReview(isDeltaReview);
    monicaInputData2.setDeltaReviewType(deltaReviewType);
    if (isDeltaReview) {
      monicaInputData2.setOrgResultId(INTERNAL_PARENT_RES_ID);
    }
    monicaInputData2.setMonicaExcelFileName(INTERNAL_MONICA_FILE_NAME2);
    monicaInputData2.setMonicaExcelFilePath(INTERNAL_MONICA_EXCEL_FILE2);
    monicaInputData2.setSelMoniCaSheet(INTERNAL_SHEET_NAME);
    monicaInputData2.setVariantId(INTERNAL_VARIANT_ID);
    monicaInputData2.setMonicaObject(createMonicaReviewDataInternalList2());


    monicaInputDatas.add(monicaInputData1);
    monicaInputDatas.add(monicaInputData2);

    return monicaInputDatas;
  }

  private List<MonicaReviewData> createMonicaReviewDataInternalList1() {
    List<MonicaReviewData> monicaReviewDatas = new ArrayList<>();
    MonicaReviewData monicaReviewData = new MonicaReviewData();
    monicaReviewData.setLabel(ACC_PED_N_MAX_C);
    monicaReviewData.setStatus("OK");
    monicaReviewData.setComment(RVW_COMMENT_1);
    monicaReviewDatas.add(monicaReviewData);
    MonicaReviewData monicaReviewData2 = new MonicaReviewData();
    monicaReviewData2.setLabel("AccPed_nMinBrk_C");
    monicaReviewData2.setStatus("OK");
    monicaReviewData2.setComment(RVW_COMMENT_2);
    monicaReviewDatas.add(monicaReviewData2);
    MonicaReviewData monicaReviewData3 = new MonicaReviewData();
    monicaReviewData3.setLabel("Com_numCsumACCA_C");
    monicaReviewData3.setStatus("OK");
    monicaReviewData3.setComment(RVW_COMMENT_3);
    monicaReviewDatas.add(monicaReviewData3);
    return monicaReviewDatas;
  }

  private List<MonicaReviewData> createMonicaReviewDataInternalList2() {
    List<MonicaReviewData> monicaReviewDatas = new ArrayList<>();
    MonicaReviewData monicaReviewData = new MonicaReviewData();
    monicaReviewData.setLabel(ACC_PED_N_MAX_C);
    monicaReviewData.setStatus("OK");
    monicaReviewData.setComment(RVW_COMMENT_1);
    monicaReviewDatas.add(monicaReviewData);
    MonicaReviewData monicaReviewData2 = new MonicaReviewData();
    monicaReviewData2.setLabel("AccPed_nMinBrkApp_C");
    monicaReviewData2.setStatus("OK");
    monicaReviewData2.setComment(RVW_COMMENT_2);
    monicaReviewDatas.add(monicaReviewData2);
    return monicaReviewDatas;
  }

  private List<MonicaReviewData> createMonicaReviewDataList() {
    List<MonicaReviewData> monicaReviewDatas = new ArrayList<>();
    MonicaReviewData monicaReviewData = new MonicaReviewData();
    monicaReviewData.setLabel("APP_drUnFltLim_C");
    monicaReviewData.setStatus("OK");
    monicaReviewData.setComment(RVW_COMMENT_1);
    monicaReviewDatas.add(monicaReviewData);
    MonicaReviewData monicaReviewData2 = new MonicaReviewData();
    monicaReviewData2.setLabel("Mo_swtClrCntrMax_C");
    monicaReviewData2.setStatus("OK");
    monicaReviewData2.setComment(RVW_COMMENT_2);
    monicaReviewDatas.add(monicaReviewData2);
    return monicaReviewDatas;
  }

  private List<MonicaReviewData> createMonicaReviewDataListForWpFinished() {
    List<MonicaReviewData> monicaReviewDatas = new ArrayList<>();
    MonicaReviewData monicaReviewData = new MonicaReviewData();
    monicaReviewData.setLabel(ACC_PED_N_MAX_C);
    monicaReviewData.setStatus("OK");
    monicaReviewData.setComment(RVW_COMMENT_1);
    monicaReviewDatas.add(monicaReviewData);
    MonicaReviewData monicaReviewData2 = new MonicaReviewData();
    monicaReviewData2.setLabel("AccPed_nMinBrk_C");
    monicaReviewData2.setStatus("OK");
    monicaReviewData2.setComment(RVW_COMMENT_2);
    monicaReviewDatas.add(monicaReviewData2);
    MonicaReviewData monicaReviewData3 = new MonicaReviewData();
    monicaReviewData3.setLabel("Com_numCsumACCA_C");
    monicaReviewData3.setStatus("NOT_OK");
    monicaReviewData3.setComment(RVW_COMMENT_2);
    monicaReviewDatas.add(monicaReviewData3);
    return monicaReviewDatas;
  }

  private List<MonicaReviewData> createInvalidMonicaReviewDataList() {
    List<MonicaReviewData> monicaReviewDatas = new ArrayList<>();
    MonicaReviewData monicaReviewData = new MonicaReviewData();
    monicaReviewData.setLabel("APP_drUnFltLim_C");
    monicaReviewData.setStatus("");
    monicaReviewData.setComment(RVW_COMMENT_1);
    monicaReviewDatas.add(monicaReviewData);
    MonicaReviewData monicaReviewData2 = new MonicaReviewData();
    monicaReviewData2.setLabel("Mo_swtClrCntrMax_C");
    monicaReviewData2.setStatus("OK");
    monicaReviewData2.setComment(RVW_COMMENT_2);
    monicaReviewDatas.add(monicaReviewData2);
    return monicaReviewDatas;
  }

  /**
   * Method to create MoniCa input data
   *
   * @return MonicaResultInputData
   */
  private MonicaReviewInputData createMonicaInputData(final Long pidcA2lId, final Long variantId, final String desc,
      final List<MonicaReviewData> monicaReviewDatas, final String ownerUserName, final String audUserName,
      final String calUserName) {
    MonicaReviewInputData inputData = new MonicaReviewInputData();

    inputData.setPidcA2lId(pidcA2lId);
    inputData.setVariantId(variantId);
    inputData.setDescription(desc);
    inputData.setMonicaObject(monicaReviewDatas);
    inputData.setOwnUserName(ownerUserName);
    inputData.setAudUserName(audUserName);
    inputData.setCalEngUserName(calUserName);

    return inputData;

  }


  /**
   * To validate the input user name
   *
   * @throws ApicWebServiceException from webservice
   */
  @Test
  public void test02() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage(startsWith(ERR_INVALID_USER_NAME));
    MonicaReviewServiceClient servClient = new MonicaReviewServiceClient();
    MonicaReviewInputData createMonicaInputData = createMonicaInputData(PIDC_A2L_ID, VARIANT_ID, DESCRIPTION,
        createMonicaReviewDataList(), INVALID_USER_NAME, USER_NAME, CAL_USER_NAME);
    servClient.executeMonicaReview(createMonicaInputData, DCM_PATH, MONICA_EXCEL_PATH);

  }

  /**
   * To validate the input DCM file
   *
   * @throws ApicWebServiceException from webservice
   */
  @Test
  public void test03() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage(ERR_INVD_DCM_FILE);
    MonicaReviewServiceClient servClient = new MonicaReviewServiceClient();
    MonicaReviewInputData createMonicaInputData = createMonicaInputData(PIDC_A2L_ID, VARIANT_ID, DESCRIPTION,
        createMonicaReviewDataList(), USER_NAME, USER_NAME, CAL_USER_NAME);
    servClient.executeMonicaReview(createMonicaInputData, INVALID_DCM_PATH_2, MONICA_EXCEL_PATH);

  }

  /**
   * To validate the MoniCa Excel Report
   *
   * @throws ApicWebServiceException from webservice
   */
  @Test
  public void test04() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage(endsWith(ERR_FILE_NOT_EXIST));
    MonicaReviewServiceClient servClient = new MonicaReviewServiceClient();
    MonicaReviewInputData createMonicaInputData = createMonicaInputData(PIDC_A2L_ID, VARIANT_ID, DESCRIPTION,
        createMonicaReviewDataList(), USER_NAME, USER_NAME, CAL_USER_NAME);
    servClient.executeMonicaReview(createMonicaInputData, DCM_PATH, INVALID_MONICA_EXCEL_PATH_2);

  }


  /**
   * To validate the input PIDC A2l ID is below to different PVER
   *
   * @throws ApicWebServiceException from webservice
   */
  @Test
  public void test05() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage(endsWith("is allowed for this variant."));
    MonicaReviewServiceClient servClient = new MonicaReviewServiceClient();
    // Allowed Pver PVER DMG1002A01C1303
    MonicaReviewInputData createMonicaInputData = createMonicaInputData(PIDC_A2L_ID_WITH_DIFF_PVER, VARIANT_ID,
        DESCRIPTION, createMonicaReviewDataList(), USER_NAME, USER_NAME, CAL_USER_NAME);
    servClient.executeMonicaReview(createMonicaInputData, DCM_PATH, MONICA_EXCEL_PATH);

  }

  /**
   * To validate the input Variant Id
   *
   * @throws ApicWebServiceException from webservice
   */
  @Test
  public void test06() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage(startsWith(ERR_PIDC_VARIANT_IS_NULL));
    MonicaReviewServiceClient servClient = new MonicaReviewServiceClient();
    MonicaReviewInputData createMonicaInputData = createMonicaInputData(PIDC_A2L_ID, INVALID_VARIANT_ID, DESCRIPTION,
        createMonicaReviewDataList(), USER_NAME, USER_NAME, CAL_USER_NAME);
    servClient.executeMonicaReview(createMonicaInputData, DCM_PATH, MONICA_EXCEL_PATH);

  }

  /**
   * To validate the input PIDC A2l ID is not found in DB
   *
   * @throws ApicWebServiceException from webservice
   */
  @Test
  public void test07() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage(startsWith("ID '" + INVALID_PIDC_A2L_ID_1 + "' is invalid for PIDC A2L File"));
    MonicaReviewServiceClient servClient = new MonicaReviewServiceClient();
    MonicaReviewInputData createMonicaInputData = createMonicaInputData(INVALID_PIDC_A2L_ID_1, VARIANT_ID, DESCRIPTION,
        createMonicaReviewDataList(), USER_NAME, USER_NAME, CAL_USER_NAME);
    servClient.executeMonicaReview(createMonicaInputData, DCM_PATH, MONICA_EXCEL_PATH);

  }


  /**
   * To validate the whether the label count is mismatched in MoniCa objects and DCM file
   *
   * @throws ApicWebServiceException from webservice
   */
  @Test
  public void test08() throws ApicWebServiceException {
    MonicaReviewServiceClient servClient = new MonicaReviewServiceClient();
    MonicaReviewInputData createMonicaInputData = createMonicaInputData(PIDC_A2L_ID, VARIANT_ID, DESCRIPTION,
        createMonicaReviewDataList(), USER_NAME, USER_NAME, CAL_USER_NAME);
    CDRReviewResultLink cdrlink =
        servClient.executeMonicaReview(createMonicaInputData, INVALID_DCM_PATH_1, MONICA_EXCEL_PATH);
    assertTrue(cdrlink.getMonicaReviewStatus().equalsIgnoreCase("Completed"));
  }

  /**
   * @throws ApicWebServiceException
   */
  @Test
  public void testWpFinishedStatusExternal() throws ApicWebServiceException {
    MonicaReviewServiceClient servClient = new MonicaReviewServiceClient();
    MonicaReviewInputData createMonicaInputData = createMonicaInputData(29725879205L, 29725879203L, DESCRIPTION,
        createMonicaReviewDataListForWpFinished(), "NDV4KOR", "NDV4KOR", CAL_USER_NAME);
    CDRReviewResultLink cdrlink = servClient.executeMonicaReview(createMonicaInputData, DCM_PATH1, MONICA_EXCEL_PATH1);
    assertTrue(cdrlink.getMonicaReviewStatus().equalsIgnoreCase("Completed"));
  }

  /**
   * To validate when the input MoniCa object is empty
   *
   * @throws ApicWebServiceException from webservice
   */
  @Test
  public void test09() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage(ERR_NO_CAL_INFO_IN_DCMFILE);
    MonicaReviewServiceClient servClient = new MonicaReviewServiceClient();
    MonicaReviewInputData createMonicaInputData = createMonicaInputData(PIDC_A2L_ID, VARIANT_ID, DESCRIPTION,
        createMonicaReviewDataList(), USER_NAME, USER_NAME, CAL_USER_NAME);
    servClient.executeMonicaReview(createMonicaInputData, EMPTY_DCM_PATH, MONICA_EXCEL_PATH);

  }

  /**
   * To validate when the input MoniCa object is empty status
   *
   * @throws ApicWebServiceException from webservice
   */
  @Test
  public void test10() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage(startsWith(ERR_MONICAPROTOCOL_STATUS_EMPTY));
    MonicaReviewServiceClient servClient = new MonicaReviewServiceClient();
    MonicaReviewInputData createMonicaInputData = createMonicaInputData(PIDC_A2L_ID, VARIANT_ID, DESCRIPTION,
        createInvalidMonicaReviewDataList(), USER_NAME, USER_NAME, CAL_USER_NAME);
    servClient.executeMonicaReview(createMonicaInputData, DCM_PATH, MONICA_EXCEL_PATH);

  }

}
