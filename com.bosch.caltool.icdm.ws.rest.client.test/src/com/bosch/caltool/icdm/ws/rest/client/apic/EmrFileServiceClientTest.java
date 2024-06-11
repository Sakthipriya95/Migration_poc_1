/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;

import com.bosch.caltool.icdm.model.emr.CodexResults;
import com.bosch.caltool.icdm.model.emr.EMRFileUploadResponse;
import com.bosch.caltool.icdm.model.emr.EMRWizardData;
import com.bosch.caltool.icdm.model.emr.EmrDataUploadResponse;
import com.bosch.caltool.icdm.model.emr.EmrFile;
import com.bosch.caltool.icdm.model.emr.EmrFileMapping;
import com.bosch.caltool.icdm.model.emr.EmrInputData;
import com.bosch.caltool.icdm.model.emr.EmrPidcVariant;
import com.bosch.caltool.icdm.model.emr.EmrUploadError;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.apic.emr.EmrFileServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author EMS4KOR
 */
public class EmrFileServiceClientTest extends AbstractRestClientTest {

  // PIDC Version mapped to EMR File without error
  private static final Long PID_VERS_ID = 1422012233L;
  // PIDC Version mapped to EMR File with error
  private static final Long PID_VERS_ID1 = 6437597515L;
  private static final Long INVALID_PID_VERS_ID = -1L;
  // EMR File without error
  private static final Long EMR_FILE_ID = 1425350981L;
  // EMR File with errors
  private static final Long EMR_FILE_ID1 = 6437603729L;
  private static final Long INVALID_EMR_FILE_ID = -1L;
  private static final Long ERROR_ID = 2108523271L;
  private static final String EMR_FILE_PATH = "testdata/PIDC";
  private static final String EMR_FILE_NAME = "JunitTestDownload.xlsx";
  private static final Long PIDC_VERS_ID_UPLOAD = 773517365L;
  // PIDC version mapped to emr file with boolean value (in value column)
  private static final Long PIDC_VERS_ID_UPLOAD1 = 6437630281L;
  private static final String PIDC_VERS_LINK = "icdm:pidvid,773517365";
  private static final String PIDC_VERS_LINK1 = "icdm:pidvid,6437630281";
  private static final String EMR_FILE_PATH_UPLOAD = "testdata/PIDC/JunitTest.xlsm";
  // Path for emr sheet with boolean value in value column
  private static final String EMR_FILE_PATH_UPLOAD1 =
      "testdata/PIDC/Cpy of CRP-Tool_v2.5_RC3_D5-PHEV_ULEV125_MY21_2019-11-05_repaired.xlsm";
  private static final Long PIDC_VARIANT_ID = 768673299L;
  // Emission standard procedure name
  private static final String EMISSION_STANDARD_PROCEDURE = "Emission Robustness Evaluation_V1.4 - Variant 4 - MIDC";
  // EMR Category
  private static final String EMR_CATEGORY = "Variant Details";
  // Regulation Limit Region - US
  private static final String REGION_US = "US";
  // Transmission - AT
  private static final String TRANSMISSION_AT = "AT";
  // Column Transmission
  private static final String TRANSMISSION = "Transmission";
  // Regulation Limit Region - Europe
  private static final String REGION_EUROPE = "Europe";
  // String Valtype
  private static final String VALTYPE_STRING = "STRING";
  // Column Regulation Limit Region
  private static final String REGULATION_LIMIT_REGION = "Regulation Limit Region";
  // EMR Fuel Type
  private static final String FUEL_TYPE = "0000000110000000";
  // Name in EMR File
  private static final String EMR_NAME = "1. NEDC 4 tests in a row";
  // EMR Measure
  private static final String EMR_MEASURE = "CO [mg/km]";
  // To check if the expected Description is received in response
  private static final String DESCRIPTION_EQUAL_CHECK = "Description must be equal";
  // To check if the expected isVariant is received in response
  private static final String GET_VARIANT_CHECK = "Get Variant must be true ";
  // To check if the expected ICDM file id is received in response
  private static final String ICDM_FILE_ID_EQUAL_CHECK = "ICDM File Id is Equal";
  // To check if the response is not null
  private static final String NOT_NULL_RESPONSE = "Response should not be null";
  // Invalid PIDC version ID
  private static final Long INVALID_PIDC_VERS_ID = 45678903L;
  // Invalid PIDC Variant ID
  private static final Long INVALID_PIDC_VARIANT_ID = 896786786L;
  // File name for the EMR data to be created
  private static final String EMR_DATA_FILE_NAME = "Test Upload";
  // File Description for the EMR data to be created
  private static final String EMR_DATA_FILE_DESCRIPTION = "Created as part of Junit testing";
  // Failure message when the expected exception is not thrown
  private static final String EXPECTED_EXCEPTION_NOT_THROWN = "Expected Exception not thrown";


  /**
   * Test case for Emr File With error Test method for {@link EmrFileServiceClient#getPidcEmrFileMapping(Long)}
   *
   * @throws ApicWebServiceException Webservice Error
   */
  @Test
  public void testGetPidcEmrFileMappingWithError() throws ApicWebServiceException {
    EmrFileServiceClient servClient = new EmrFileServiceClient();
    Map<Long, EmrFileMapping> retMap = servClient.getPidcEmrFileMapping(PID_VERS_ID1);
    assertNotNull("Set should not be null or empty", retMap);
    boolean fileIdAvailable = false;
    for (EmrFileMapping emrFile : retMap.values()) {
      if (emrFile.getId().equals(EMR_FILE_ID1)) {
        testOutputEmrFileWithError(emrFile);
        fileIdAvailable = true;
        break;
      }
    }
    assertTrue("FileId is available", fileIdAvailable);
  }


  /**
   * @param emrFile
   */
  private void testOutputEmrFileWithoutError(final EmrFileMapping emrFile) {
    assertEquals(ICDM_FILE_ID_EQUAL_CHECK, Long.valueOf(1425350979), emrFile.getEmrFile().getIcdmFileId());
    assertTrue(GET_VARIANT_CHECK, emrFile.getEmrFile().getIsVariant());
    assertEquals(DESCRIPTION_EQUAL_CHECK, null, emrFile.getEmrFile().getDescription());
    assertTrue("Deleted flag should be true", emrFile.getEmrFile().getDeletedFlag());
    assertTrue("Load Without Error must be true", emrFile.getEmrFile().getLoadedWithoutErrorsFlag());
  }

  /**
   * Test case for EMR File without error
   *
   * @throws ApicWebServiceException Webservice Error
   */
  @Test
  public void testGetPidcEmrFileMappingWithoutError() throws ApicWebServiceException {
    EmrFileServiceClient servClient = new EmrFileServiceClient();
    Map<Long, EmrFileMapping> retMap = servClient.getPidcEmrFileMapping(PID_VERS_ID);
    assertNotNull("Set should not be null or empty", retMap);
    boolean fileIdAvailable = false;
    for (EmrFileMapping emrFile : retMap.values()) {
      if (emrFile.getId().equals(EMR_FILE_ID)) {
        testOutputEmrFileWithoutError(emrFile);
        fileIdAvailable = true;
        break;
      }
    }
    assertTrue("FileId is available", fileIdAvailable);
  }

  /**
   * Test method for {@link EmrFileServiceClient#getPidcEmrFileMappingNegative(Long)}
   *
   * @throws ApicWebServiceException Webservice Error
   */
  @Test
  public void testGetPidcEmrFileMappingNegative() throws ApicWebServiceException {
    EmrFileServiceClient servClient = new EmrFileServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    servClient.getPidcEmrFileMapping(INVALID_PID_VERS_ID);
    fail(EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * Test method for {@link EmrFileServiceClient#getUploadProtocol(Long)}
   *
   * @throws ApicWebServiceException Webservice Error
   */

  public void testGetUploadProtocol() throws ApicWebServiceException {
    EmrFileServiceClient servClient = new EmrFileServiceClient();

    List<EmrUploadError> list = servClient.getUploadProtocol(EMR_FILE_ID);
    assertNotNull("List should not be null or empty", list);
    boolean errorIdAvailable = false;
    for (EmrUploadError emrUploadError : list) {
      if (emrUploadError.getId().equals(ERROR_ID)) {

        testOutput(emrUploadError);
        errorIdAvailable = true;
        break;
      }
    }
    assertTrue("Error Id is available", errorIdAvailable);

  }

  /**
   * Test method for {@link EmrFileServiceClient#getUploadProtocolNegative(Long)}
   *
   * @throws ApicWebServiceException Webservice Error
   */
  @Test
  public void testGetUploadProtocolNegative() throws ApicWebServiceException {
    EmrFileServiceClient servClient = new EmrFileServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    servClient.getUploadProtocol(INVALID_EMR_FILE_ID);
    fail("Exception not thrown");
  }

  /**
   * Test method for {@link EmrFileServiceClient#downloadEmrFile(Long,String,String)}
   *
   * @throws ApicWebServiceException Webservice Error
   */
  @Test
  public void testDownloadEmrFile() throws ApicWebServiceException {
    EmrFileServiceClient servClient = new EmrFileServiceClient();
    String ret = servClient.downloadEmrFile(EMR_FILE_ID, EMR_FILE_NAME, EMR_FILE_PATH);
    assertNotNull(NOT_NULL_RESPONSE, ret);
    assertEquals("Output File Path should be equal", "testdata\\PIDC\\JunitTestDownload.xlsx", ret);

  }

  /**
   * Test method for {@link EmrFileServiceClient#uploadEMRFilesForPidcVersion(Long,String,List,List)}
   *
   * @throws ApicWebServiceException Webservice Error
   * @throws IOException
   */
  @Test
  public void testUploadEMRFilesForPidcVersion() throws ApicWebServiceException, IOException {
    EmrFileServiceClient servClient = new EmrFileServiceClient();
    List<EMRWizardData> tableInput = new ArrayList<>();
    EMRWizardData emrWizData = new EMRWizardData();
    emrWizData.setFileName("JunitTest");
    emrWizData.setFilePath(EMR_FILE_PATH_UPLOAD);
    emrWizData.setDescripton("");
    emrWizData.setPartialPIDCScope(false);
    tableInput.add(emrWizData);
    EMRFileUploadResponse ret = servClient.uploadEMRFilesForPidcVersion(PIDC_VERS_ID_UPLOAD, PIDC_VERS_LINK, tableInput,
        Collections.emptyList());
    assertNotNull(NOT_NULL_RESPONSE, ret);

  }

  /**
   * Upload EMR File with boolean values in value column Test method for
   * {@link EmrFileServiceClient#uploadEMRFilesForPidcVersion(Long,String,List,List)}
   *
   * @throws ApicWebServiceException Webservice Error
   * @throws IOException Exception
   */
  @Test
  public void testUploadEMRFilesWithBoolValForPidcVer() throws ApicWebServiceException, IOException {
    EmrFileServiceClient servClient = new EmrFileServiceClient();
    List<EMRWizardData> tableInput = new ArrayList<>();
    EMRWizardData emrWizData = new EMRWizardData();
    emrWizData.setFileName("Cpy of CRP-Tool_v2.5_RC3_D5-PHEV_ULEV125_MY21_2019-11-05_repaired");
    emrWizData.setFilePath(EMR_FILE_PATH_UPLOAD1);
    emrWizData.setDescripton("");
    emrWizData.setPartialPIDCScope(false);
    tableInput.add(emrWizData);
    EMRFileUploadResponse ret = servClient.uploadEMRFilesForPidcVersion(PIDC_VERS_ID_UPLOAD1, PIDC_VERS_LINK1,
        tableInput, Collections.emptyList());
    assertNotNull(NOT_NULL_RESPONSE, ret);

  }

  /**
   * Test case for EMR File with error Test method for {@link EmrFileServiceClient#reloadEmrFile(Long)}
   *
   * @throws ApicWebServiceException Webservice Error
   */
  @Test
  public void testReloadEmrFileWithError() throws ApicWebServiceException {
    EmrFileServiceClient servClient = new EmrFileServiceClient();
    EMRFileUploadResponse res = servClient.reloadEmrFile(EMR_FILE_ID1);
    assertNotNull(NOT_NULL_RESPONSE, res);
    Map<Long, EmrFile> ret = res.getEmrFileMap();
    assertNotNull(NOT_NULL_RESPONSE, ret);
    testOutputEmrFileWithError(ret);
  }


  /**
   * Test case for Emr File without Error Test method for {@link EmrFileServiceClient#reloadEmrFile(Long)}
   *
   * @throws ApicWebServiceException Webservice Error
   */
  @Test
  public void testReloadEmrFileWithoutError() throws ApicWebServiceException {
    EmrFileServiceClient servClient = new EmrFileServiceClient();
    EMRFileUploadResponse res = servClient.reloadEmrFile(EMR_FILE_ID);
    assertNotNull(NOT_NULL_RESPONSE, res);
    Map<Long, EmrFile> ret = res.getEmrFileMap();
    assertNotNull(NOT_NULL_RESPONSE, ret);
    testOutputEmrFileWithoutError(ret);
  }

  /**
   * @param ret
   */
  private void testOutputEmrFileWithError(final Map<Long, EmrFile> ret) {
    assertEquals(ICDM_FILE_ID_EQUAL_CHECK, Long.valueOf(6437603677L), ret.get(EMR_FILE_ID1).getIcdmFileId());
    assertTrue(GET_VARIANT_CHECK, ret.get(EMR_FILE_ID1).getIsVariant());
    assertEquals(DESCRIPTION_EQUAL_CHECK, null, ret.get(EMR_FILE_ID1).getDescription());
    assertFalse("Deleted flag should be false", ret.get(EMR_FILE_ID1).getDeletedFlag());
    assertFalse("Load Without Error must be false", ret.get(EMR_FILE_ID1).getLoadedWithoutErrorsFlag());
  }

  /**
   * @param ret
   */
  private void testOutputEmrFileWithoutError(final Map<Long, EmrFile> ret) {
    assertEquals(ICDM_FILE_ID_EQUAL_CHECK, Long.valueOf(1425350979), ret.get(EMR_FILE_ID).getIcdmFileId());
    assertTrue(GET_VARIANT_CHECK, ret.get(EMR_FILE_ID).getIsVariant());
    assertEquals(DESCRIPTION_EQUAL_CHECK, null, ret.get(EMR_FILE_ID).getDescription());
    assertTrue("Deleted flag should be true", ret.get(EMR_FILE_ID).getDeletedFlag());
    assertTrue("Load Without Error must be true", ret.get(EMR_FILE_ID).getLoadedWithoutErrorsFlag());
  }

  /**
   * test Output Data
   */
  private void testOutput(final EmrUploadError emrUploadError) {
    assertEquals("Row Number must be equal", Long.valueOf(3), emrUploadError.getRowNumber());
    assertEquals("Error Category must be equal", "NAME", emrUploadError.getErrorCategory());
    assertEquals("Error Message must be equal", "Invalid Data", emrUploadError.getErrorMessage());
    assertEquals("Error Data must be equal", EMR_NAME, emrUploadError.getErrorData());


  }

  /**
   * @test output data
   */
  private void testOutputEmrFileWithError(final EmrFileMapping emrFile) {
    assertEquals(ICDM_FILE_ID_EQUAL_CHECK, Long.valueOf(6437603677L), emrFile.getEmrFile().getIcdmFileId());
    assertTrue(GET_VARIANT_CHECK, emrFile.getEmrFile().getIsVariant());
    assertEquals(DESCRIPTION_EQUAL_CHECK, null, emrFile.getEmrFile().getDescription());
    assertFalse("Deleted flag should be false", emrFile.getEmrFile().getDeletedFlag());
    assertFalse("Load Without Error must be false", emrFile.getEmrFile().getLoadedWithoutErrorsFlag());
  }

  /**
   * When EMR data gets saved into iCDM without any errors
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateEmrData() throws ApicWebServiceException {
    EmrFileServiceClient servClient = new EmrFileServiceClient();
    EmrInputData emrInputData = createEmrInputData();
    EmrDataUploadResponse emrDataUploadResp = servClient.createEmrData(emrInputData);
    assertNotNull(NOT_NULL_RESPONSE, emrDataUploadResp);
    checkOutputForEmrFileResponse(emrDataUploadResp, false);
  }


  /**
   * When EMR data gets saved into database and EMR PIDC variant mapping also gets created
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateEmrDataWithPidcVariantMapping() throws ApicWebServiceException {
    EmrFileServiceClient servClient = new EmrFileServiceClient();
    EmrInputData emrInputData = createEmrInputData();
    List<Long> pidcVarList = new ArrayList<>();
    pidcVarList.add(PIDC_VARIANT_ID);
    emrInputData.setPidcVariantIdList(pidcVarList);
    EmrDataUploadResponse emrDataUploadResp = servClient.createEmrData(emrInputData);
    checkOutputForEmrFileResponse(emrDataUploadResp, true);
    Optional<EmrPidcVariant> data = emrDataUploadResp.getEmrPidcVariantMap().values().stream().findFirst();
    assertTrue("EMR PIDC Variant mapping should be available", data.isPresent());
    if (data.isPresent()) {
      assertEquals("EMR PIDC Variant ID should be equal", PIDC_VARIANT_ID, data.get().getPidcVariantId());
      assertEquals("EMR Variant should be equal", Long.valueOf(00000001L), data.get().getEmrVariant());
    }
  }

  /**
   * When EMR data gets saved with errors into iCDM database
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateEmrDataWithErrors() throws ApicWebServiceException {
    EmrFileServiceClient servClient = new EmrFileServiceClient();
    EmrInputData emrInputData = createEmrInputData();
    emrInputData.getCodexResultsList().add(createCodexResultsWithErrorData());
    EmrDataUploadResponse ret = servClient.createEmrData(emrInputData);
    assertNotNull(NOT_NULL_RESPONSE, ret);
    Optional<List<EmrUploadError>> data = ret.getEmrFileErrorMap().values().stream().findFirst();
    assertNotNull("Error details in Response should not be null", data.isPresent());
    if (data.isPresent()) {
      assertEquals(EMR_MEASURE, data.get().get(0).getErrorData());
      assertEquals("Invalid Data", data.get().get(0).getErrorMessage());
    }
  }

  /**
   * When EMR data creation is requested without PIDC Version ID
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateEmrDataWithMissingPidcVersId() throws ApicWebServiceException {
    EmrFileServiceClient servClient = new EmrFileServiceClient();
    EmrInputData emrInputData = new EmrInputData();
    emrInputData.setPidcVersId(null);
    emrInputData.setFileName(EMR_DATA_FILE_NAME);
    emrInputData.setFileDescription(EMR_DATA_FILE_DESCRIPTION);
    emrInputData.setCodexResultsList(createCodexResultsList());
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("PIDC Version ID is mandatory");
    servClient.createEmrData(emrInputData);
    fail(EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * When EMR data creation is requested without EMR file name
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateEmrDataWithoutFileName() throws ApicWebServiceException {
    EmrFileServiceClient servClient = new EmrFileServiceClient();
    EmrInputData emrInputData = new EmrInputData();
    emrInputData.setPidcVersId(PIDC_VERS_ID_UPLOAD);
    emrInputData.setFileName(null);
    emrInputData.setFileDescription(EMR_DATA_FILE_DESCRIPTION);
    emrInputData.setCodexResultsList(createCodexResultsList());
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("EMR file name is mandatory");
    servClient.createEmrData(emrInputData);
    fail(EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * When EMR data creation is requested without Codex Results
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateEmrDataWithoutCodexResults() throws ApicWebServiceException {
    EmrFileServiceClient servClient = new EmrFileServiceClient();
    EmrInputData emrInputData = new EmrInputData();
    emrInputData.setPidcVersId(PIDC_VERS_ID_UPLOAD);
    emrInputData.setFileName(EMR_DATA_FILE_NAME);
    emrInputData.setFileDescription(EMR_DATA_FILE_DESCRIPTION);
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("EMR Data not provided");
    servClient.createEmrData(emrInputData);
    fail(EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * When EMR data creation is requested with Invalid PIDC Version ID
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateEmrDataWithInvalidPidcVersID() throws ApicWebServiceException {
    EmrFileServiceClient servClient = new EmrFileServiceClient();
    EmrInputData emrInputData = new EmrInputData();
    emrInputData.setPidcVersId(INVALID_PIDC_VERS_ID);
    emrInputData.setFileName(EMR_DATA_FILE_NAME);
    emrInputData.setFileDescription(EMR_DATA_FILE_DESCRIPTION);
    emrInputData.setCodexResultsList(createCodexResultsList());
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("ID '" + INVALID_PIDC_VERS_ID + "' is invalid for PIDC Version");
    servClient.createEmrData(emrInputData);
    fail(EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * When EMR data creation is requested with Invalid PIDC Variant ID
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateEmrDataWithInvalidPidcVariant() throws ApicWebServiceException {
    EmrFileServiceClient servClient = new EmrFileServiceClient();
    EmrInputData emrInputData = createEmrInputData();
    List<Long> pidcVarIdList = new ArrayList<>();
    pidcVarIdList.add(INVALID_PIDC_VARIANT_ID);
    emrInputData.setPidcVariantIdList(pidcVarIdList);
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("ID '" + INVALID_PIDC_VARIANT_ID + "' is invalid for PIDC Variant");
    servClient.createEmrData(emrInputData);
    fail(EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * @return CodexResults
   */
  private CodexResults createCodexResultsWithErrorData() {
    CodexResults codexResults = new CodexResults();
    codexResults.setRefProcedure(EMISSION_STANDARD_PROCEDURE);
    codexResults.setCategory(EMR_CATEGORY);
    codexResults.setName(EMR_NAME);
    codexResults.setFuelType(FUEL_TYPE);
    codexResults.setColumn(REGULATION_LIMIT_REGION);
    codexResults.setMeasure(EMR_MEASURE);
    codexResults.setValType(VALTYPE_STRING);
    codexResults.setStrValue(REGION_EUROPE);
    codexResults.setStrValue(REGION_US);
    return codexResults;
  }


  /**
   * @return EmrInputData
   */
  private EmrInputData createEmrInputData() {
    EmrInputData emrInputData = new EmrInputData();
    emrInputData.setPidcVersId(PIDC_VERS_ID_UPLOAD);
    emrInputData.setFileName(EMR_DATA_FILE_NAME);
    emrInputData.setFileDescription(EMR_DATA_FILE_DESCRIPTION);
    emrInputData.setCodexResultsList(createCodexResultsList());
    return emrInputData;
  }


  /**
   * @return List of CodexResults
   */
  private List<CodexResults> createCodexResultsList() {
    List<CodexResults> codexResultsList = new ArrayList<>();
    CodexResults codexResults = new CodexResults();
    codexResults.setRefProcedure(EMISSION_STANDARD_PROCEDURE);
    codexResults.setCategory(EMR_CATEGORY);
    codexResults.setName(EMR_NAME);
    codexResults.setFuelType(FUEL_TYPE);
    codexResults.setColumn(REGULATION_LIMIT_REGION);
    codexResults.setMeasure(null);
    codexResults.setValType(VALTYPE_STRING);
    codexResults.setStrValue(REGION_EUROPE);
    codexResultsList.add(codexResults);
    CodexResults codexResults1 = new CodexResults();
    codexResults1.setRefProcedure(EMISSION_STANDARD_PROCEDURE);
    codexResults1.setCategory(EMR_CATEGORY);
    codexResults1.setName(EMR_NAME);
    codexResults1.setFuelType(FUEL_TYPE);
    codexResults1.setColumn(TRANSMISSION);
    codexResults1.setMeasure(null);
    codexResults1.setValType(VALTYPE_STRING);
    codexResults1.setStrValue(TRANSMISSION_AT);
    codexResultsList.add(codexResults1);
    return codexResultsList;
  }


  /**
   * Assert checks for EMR file response
   *
   * @param emrDataUploadResp
   */
  private void checkOutputForEmrFileResponse(final EmrDataUploadResponse emrDataUploadResp, final boolean withVariant) {
    Optional<EmrFile> data = emrDataUploadResp.getEmrFileMap().values().stream().findFirst();
    assertTrue("EMR File mapping should be available", data.isPresent());
    if (data.isPresent()) {
      assertTrue("EMR data loadedWithoutErrors Flag should be true", data.get().getLoadedWithoutErrorsFlag());
      assertFalse("EMR data isDeleted Flag should be false", data.get().getDeletedFlag());
      assertEquals("EMR data file name should be equal", EMR_DATA_FILE_NAME, data.get().getName());
      assertEquals("EMR data file description should be equal", EMR_DATA_FILE_DESCRIPTION, data.get().getDescription());
      if (withVariant) {
        assertTrue("EMR data isVariant Flag should be false", data.get().getIsVariant());
      }
      else {
        assertFalse("EMR data isVariant Flag should be false", data.get().getIsVariant());
      }
    }
    assertEquals("Error details in Response should be empty", 0, emrDataUploadResp.getEmrFileErrorMap().size());
  }

}
