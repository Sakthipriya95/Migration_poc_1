/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.bosch.caltool.icdm.model.apic.PidcA2lDetails;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2lFileExt;
import com.bosch.caltool.icdm.model.apic.pidc.PidcCreationData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcCreationRespData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.SSDProjectVersion;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.UserServiceClient;


/**
 * The Class PidcA2lServiceClientTest.
 *
 * @author dja7cob Test class for service to fetch work package resources
 */
public class PidcA2lServiceClientTest extends AbstractRestClientTest {

  /**
   *
   */
  private static final String REVIEW_RESULT_VALIDATION_ERROR_MSG =
      "Cannot modify A2L mapping since review results are available for the following A2L file(s)";

  /**
  *
  */
  private static final String WP_VALIDATION_ERROR_MSG =
      "Cannot modify A2L mapping since Work Package Definitions are available for the following A2L file(s) ";

  /**
   * PIDC Version: AUDI->Gasoline Engine->PC - Passenger Car->MG1-C->Audi EA888-Gen3-BZyklus (Version 1)
   */
  private static final Long PIDC_ID = 769782967L;
  private static final Long PIDC_A2L_ID = 773610965L;
  private final static Long PIDC_A2L_ID_WITH_WP = 2945230888L;
  private static final Long PIDC_VERS_ID = 773518515L;
  private static final String SDOM_PVER_NAME = "D177";
  private static final Long SSD_SOFT_PROJ_ID = 10852583L;


  private static final Long INV_PIDC_ID = -769782967L;
  private static final Long INV_PIDC_A2L_ID = -773610965L;
  private static final Long INV_PIDC_VERS_ID = -773518515L;
  private static final String INV_SDOM_PVER_NAME = "try";
  private static final Long INV_SSD_SOFT_PROJ_ID = -10852583L;


  private static final String APRJ_NAME = "X_Test_Hz_AUDI_1788";
  private static final Long VCDM_A2LFILE_ID = 20504831L;
  private static final String INVALID_APRJ_NAME = "Invalid_APRJ_Name";
  private static final String NOT_NULL_EMPTY_RESPONSE = "Response should not be null or empty";
  private static final String VCDM_VARIANT_NAME = "AT";
  private static final Long INVALID_ID = -100L;

  /**
   * Expected exceptions
   */
  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  /**
   * Test method for {@link PidcA2lServiceClient#getA2LFileBySdom(Long, String)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetA2LFileBySdom() throws ApicWebServiceException {
    PidcA2lServiceClient service = new PidcA2lServiceClient();
    Map<Long, PidcA2l> retMap = service.getA2LFileBySdom(PIDC_VERS_ID, SDOM_PVER_NAME);
    assertFalse("PIDC A2L map should not be empty", retMap.isEmpty());
    LOG.info("No. of PIDC A2L records = {}", retMap.size());
    for (Long id : retMap.keySet()) {
      if (id == PIDC_A2L_ID) {
        testOutput(retMap.get(id));
      }
    }
  }

  /**
   * Test method for {@link PidcA2lServiceClient#getA2LFileBySdom(Long, String)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetA2LFileBySdomNegative() throws ApicWebServiceException {
    PidcA2lServiceClient service = new PidcA2lServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    service.getA2LFileBySdom(INV_PIDC_VERS_ID, INV_SDOM_PVER_NAME);
    fail("Expected exception not thrown");
  }

  /**
   * Test method for {@link PidcA2lServiceClient#getById(Long)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetById() throws ApicWebServiceException {
    PidcA2lServiceClient service = new PidcA2lServiceClient();
    PidcA2l ret = service.getById(PIDC_A2L_ID);
    assertNotNull("Response should not be empty", ret);
    testOutput(ret);
  }

  /**
   * Test method for {@link PidcA2lServiceClient#getById(Long)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetByIdNegative() throws ApicWebServiceException {
    PidcA2lServiceClient service = new PidcA2lServiceClient();
    this.thrown.expectMessage("PIDC A2L File with ID '" + INV_PIDC_A2L_ID + "' not found");
    service.getById(INV_PIDC_A2L_ID);
    fail("Expected exception not thrown");
  }

  /**
   * @param PidcA2l ret
   */
  private void testOutput(final PidcA2l ret) {
    assertEquals("PIDC A2L Id is equal", Long.valueOf(773610965L), ret.getId());
    assertEquals("Project Id is equal", Long.valueOf(769782967), ret.getProjectId());
    assertEquals("PIDC Version Id is equal", Long.valueOf(773518515), ret.getPidcVersId());
    assertEquals("A2L File Id is equal", Long.valueOf(737100001), ret.getA2lFileId());
    assertEquals("SDOM PVER NAME is equal", "D177", ret.getSdomPverName());
    assertEquals("Created User is equal", "HFZ2SI", ret.getCreatedUser());
    assertNull("VCDM A2L Name is null", ret.getVcdmA2lName());
    assertNull("VCDM A2L Date is null", ret.getVcdmA2lDate());
    assertNull("SSD software Version is null", ret.getSsdSoftwareVersion());
    assertNull("SSD software Version Id is null", ret.getSsdSoftwareVersionId());
    assertNull("SSD software Project Id is null", ret.getSsdSoftwareProjId());
    assertTrue("Is Active", ret.isActive());
    assertFalse("WP Defn Vers Available is true", ret.isWpParamPresentFlag());
    assertFalse("Is active wp defn vers available is true", ret.isActiveWpParamPresentFlag());
  }

  /**
   * {@link PidcA2lServiceClient#getAllA2lByPidc(Long) }.
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetAllA2lByPidc() throws ApicWebServiceException {
    Map<Long, PidcA2lFileExt> retMap = new PidcA2lServiceClient().getAllA2lByPidc(PIDC_ID);
    assertFalse("Response should not be empty", retMap.isEmpty());
    PidcA2lFileExt ret = retMap.get(737100001L);
    testOutput(ret.getPidcA2l());
  }

  /**
   * {@link PidcA2lServiceClient#getAllA2lByPidc(Long) }.
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetAllA2lByPidcNegative() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("ID '" + INV_PIDC_ID + "' is invalid for ");
    new PidcA2lServiceClient().getAllA2lByPidc(INV_PIDC_ID);
    fail("Expected exception not thrown");
  }

  /**
   * Test method for {@link PidcA2lServiceClient#isPidcA2lPresent(Long,String)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testIsPidcA2lPresent() throws ApicWebServiceException {
    PidcA2lServiceClient service = new PidcA2lServiceClient();
    boolean ret = service.isPidcA2lPresent(PIDC_VERS_ID, SDOM_PVER_NAME);
    assertNotNull("Response should not be null", ret);
    LOG.info("Is PidcA2l Present : {}", ret);
  }

  /**
   * Test method for {@link PidcA2lServiceClient#isPidcA2lPresent(Long,String)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testIsPidcA2lPresentNegative() throws ApicWebServiceException {
    PidcA2lServiceClient service = new PidcA2lServiceClient();
    boolean ret = service.isPidcA2lPresent(INV_PIDC_VERS_ID, INV_SDOM_PVER_NAME);
    assertNotNull("Response should not be null", ret);
    LOG.info("Is PidcA2l Present : {}", ret);
  }

  /**
   * {@link PidcA2lServiceClient#getPidcA2LFileDetails(Long)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetPidcA2LFileDetails() throws ApicWebServiceException {
    PidcA2lServiceClient service = new PidcA2lServiceClient();
    PidcA2lFileExt ret = service.getPidcA2LFileDetails(PIDC_A2L_ID);
    assertNotNull("Response should not be empty", ret);
    testOutput(ret.getPidcA2l());
  }

  /**
   * {@link PidcA2lServiceClient#getPidcA2LFileDetails(Long)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetPidcA2LFileDetailsNegative() throws ApicWebServiceException {
    PidcA2lServiceClient service = new PidcA2lServiceClient();
    this.thrown.expectMessage("PIDC A2L File with ID '" + INV_PIDC_A2L_ID + "' not found");
    service.getPidcA2LFileDetails(INV_PIDC_A2L_ID);
    fail("Expected exception not thrown");
  }


  /**
   * Test method for {@link PidcA2lServiceClient#create(Set)}, {@link PidcA2lServiceClient#update(Set)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateUpdate() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();

    Pidc newPidc = new Pidc();
    newPidc.setNameEng("Junit_PIDC_For_PidcA2l_" + getRunId());
    newPidc.setDescEng("Junit_PIDC_For_PidcA2l_" + getRunId());
    newPidc.setDescGer(null);

    PidcVersion pidcVersion = new PidcVersion();
    pidcVersion.setVersionName("Junit_Test_Version_" + getRunId());
    pidcVersion.setVersDescEng("Junit_Test_Version");

    PidcCreationData creationData = new PidcCreationData();
    creationData.setPidc(newPidc);
    creationData.setAllVersionSet(new HashSet<>(Arrays.asList(pidcVersion)));
    creationData.setOwner(new UserServiceClient().getCurrentApicUser());
    creationData.getStructAttrValMap().put(2232l, 2233l);
    creationData.getStructAttrValMap().put(2225l, 2227l);
    creationData.getStructAttrValMap().put(36l, 782110518l);
    creationData.getStructAttrValMap().put(197l, 2271l);

    // create PidcVersion
    PidcCreationRespData createdRespData = new PidcServiceClient().createPidc(creationData);
    assertNotNull("Created object is  not null", createdRespData);

    PidcA2l pidcA2l = new PidcA2l();
    Long createdPidcId = createdRespData.getPidc().getId();
    pidcA2l.setProjectId(createdPidcId);
    Object[] pidcvers = servClient.getAllPidcVersionForPidc(createdPidcId).keySet().toArray();
    Long createdPidcVersId = Long.parseLong(pidcvers[0].toString());
    pidcA2l.setPidcVersId(createdPidcVersId);
    pidcA2l.setA2lFileId(16453064l);
    pidcA2l.setSdomPverName("173001");
    pidcA2l.setSdomPverVarName("05D6");
    Set<PidcA2l> pidcA2ls = new HashSet<>();
    pidcA2ls.add(pidcA2l);
    // create PidcA2L
    PidcA2lServiceClient service = new PidcA2lServiceClient();
    Set<PidcA2l> retSet = service.create(pidcA2ls);
    // validate
    assertNotNull("Response should not be null", retSet.isEmpty());
    PidcA2l createdObject = new PidcA2l();
    for (PidcA2l obj : retSet) {
      if (obj.getA2lFileId() == 16453064l) {
        createdObject = obj;
        assertEquals("Project Id is equal", createdPidcId, obj.getProjectId());
        assertEquals("Pidc version Id is equal", createdPidcVersId, obj.getPidcVersId());
        assertEquals("A2L File Id is equal", Long.valueOf(16453064l), obj.getA2lFileId());
        assertEquals("Sdom Pvername is equal", "173001", obj.getSdomPverName());
        assertEquals("Version is equal", Long.valueOf(1), obj.getVersion());
      }
    }
    assertNotNull("Response should not be null", retSet.isEmpty());
    retSet.remove(createdObject);
    // update PidcA2L
    createdObject.setWpParamPresentFlag(true);
    retSet.add(createdObject);
    // validate
    Set<PidcA2l> updatedObject = service.update(retSet);
    for (PidcA2l obj : updatedObject) {
      if (obj.getA2lFileId() == 16453064l) {
        assertEquals("WP Param Mapping available is equal", true, obj.isWpParamPresentFlag());
      }
    }
  }

  /**
   * {@link PidcA2lServiceClient#getSSDServiceHandler(Long)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetSSDServiceHandler() throws ApicWebServiceException {
    PidcA2lServiceClient service = new PidcA2lServiceClient();
    SortedSet<SSDProjectVersion> ret = service.getSSDServiceHandler(SSD_SOFT_PROJ_ID);
    assertFalse("Response should not be empty", ret.isEmpty());
    LOG.info("SSD Project Version list size = {}", ret.size());
    LOG.info("First SSD Project Version ID = {}, {}", ret.first().getVersionId(), ret.first().getVersionName());
  }

  /**
   * {@link PidcA2lServiceClient#getSSDServiceHandler(Long)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetSSDServiceHandlerNegative() throws ApicWebServiceException {
    PidcA2lServiceClient service = new PidcA2lServiceClient();
    SortedSet<SSDProjectVersion> ret = service.getSSDServiceHandler(INV_SSD_SOFT_PROJ_ID);
    assertTrue("Response should be empty", ret.isEmpty());
  }

  /**
   * {@link PidcA2lServiceClient#getPidcA2lAssignmentValidation(Set)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testgetPidcA2lAssignmentValidationForReviewResult() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage(startsWith(REVIEW_RESULT_VALIDATION_ERROR_MSG));
    PidcA2lServiceClient service = new PidcA2lServiceClient();
    Set<Long> pidcA2lIdSet = new HashSet<>();
    pidcA2lIdSet.add(PIDC_A2L_ID);
    boolean ret = service.getPidcA2lAssignmentValidation(pidcA2lIdSet);
  }

  /**
   * {@link PidcA2lServiceClient#getPidcA2lAssignmentValidation(Set)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testgetPidcA2lAssignmentValidationForWp() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage(startsWith(WP_VALIDATION_ERROR_MSG));
    PidcA2lServiceClient service = new PidcA2lServiceClient();
    Set<Long> pidcA2lIdSet = new HashSet<>();
    pidcA2lIdSet.add(PIDC_A2L_ID_WITH_WP);
    boolean ret = service.getPidcA2lAssignmentValidation(pidcA2lIdSet);
  }

  /**
   * Negative Test method for {@link PidcA2lServiceClient#getPidcDetailsByAPRJInfo(String, String, Long)} Case: invalid
   * aprj name
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetPidcDetByAPRJInfoWithInvalidAPRJName() throws ApicWebServiceException {
    PidcA2lServiceClient servClient = new PidcA2lServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("APRJ Name '" + INVALID_APRJ_NAME + "' not found");
    servClient.getPidcDetailsByAPRJInfo(INVALID_APRJ_NAME, null, VCDM_A2LFILE_ID);
    fail("Expected exception not thrown");
  }

  /**
   * Negative Test method for {@link PidcA2lServiceClient#getPidcDetailsByAPRJInfo(String, String, Long)}. Case: invalid
   * vcdm a2l file id
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetPidcDetByAPRJInfoWithInvalidvcdmA2lFileId() throws ApicWebServiceException {
    PidcA2lServiceClient servClient = new PidcA2lServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("vCDM a2l file with ID '" + INVALID_ID + "' not found");
    servClient.getPidcDetailsByAPRJInfo(APRJ_NAME, VCDM_VARIANT_NAME, INVALID_ID);
    fail("Expected exception not thrown");
  }

  /**
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetPidcDetByAPRJInfoWithoutVCDMTransfer() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("vCDM Transfer was not done from iCDM for the given APRJ.");
    new PidcA2lServiceClient().getPidcDetailsByAPRJInfo("001-M281", null, 20855025L);
  }

  /**
   * Negative Test method for {@link PidcA2lServiceClient#getPidcDetailsByAPRJInfo(String, String, Long)}. Case: varinat
   * name is optional, so can be null
   *
   * @throws ApicWebServiceException web service error
   */
  public void testGetPidcDetByAPRJInfoWithoutVariant() throws ApicWebServiceException {
    PidcA2lServiceClient servClient = new PidcA2lServiceClient();
    // variant name is optional, so can be null
    Map<Long, PidcA2lDetails> retMap = servClient.getPidcDetailsByAPRJInfo(APRJ_NAME, null, VCDM_A2LFILE_ID);
    assertNotNull(NOT_NULL_EMPTY_RESPONSE, retMap);
    assertFalse(NOT_NULL_EMPTY_RESPONSE, retMap.isEmpty());
    PidcA2lDetails dataCheckerModel = retMap.get(2597893281L);// this pidc has no variants, so "variant name=null" is
    // valid input
    testDataCheckerModelWithoutVar(dataCheckerModel);
  }

  /**
   * Test method for {@link PidcA2lServiceClient#getPidcDetailsByAPRJInfo(String, String, Long)} with valid inputs.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetPidcDetailsByAPRJInfo() throws ApicWebServiceException {
    PidcA2lServiceClient servClient = new PidcA2lServiceClient();
    Map<Long, PidcA2lDetails> retMap =
        servClient.getPidcDetailsByAPRJInfo(APRJ_NAME, VCDM_VARIANT_NAME, VCDM_A2LFILE_ID);
    assertNotNull(NOT_NULL_EMPTY_RESPONSE, retMap);
    assertFalse(NOT_NULL_EMPTY_RESPONSE, retMap.isEmpty());
    PidcA2lDetails pidcA2lDetails = retMap.get(1293613521L);
    testDataCheckerModel(pidcA2lDetails);
  }

  /**
   * @param dataCheckerModel
   */
  private void testDataCheckerModel(final PidcA2lDetails dataCheckerModel) {
    assertEquals("Pidc Version Id is equal", Long.valueOf(1165057178), dataCheckerModel.getPidcVersionId());
    assertEquals("Pidc Version Name is equal", "X_Test_HENZE_1788_1 (V1_0)", dataCheckerModel.getPidcVersionName());
    assertEquals("SDOM Pver Name is equal", "MMD114A0CC1788", dataCheckerModel.getPverName());
    assertEquals("Pver Variant Name is equal", "MC00", dataCheckerModel.getPverVariant());
    assertEquals("Pidc Variant Id is equal", Long.valueOf(1293613521L), dataCheckerModel.getPidcVariantId());
    assertEquals("Pidc Variant Name is equal", VCDM_VARIANT_NAME, dataCheckerModel.getPidcVariantName());
    assertEquals("Pidc A2L ID is equal", Long.valueOf(1494632328), dataCheckerModel.getPidcA2lId());
    assertEquals("Pidc A2L File name is equal", "MMD114A0CC1788_MC00.A2L", dataCheckerModel.getPidcA2lName());
  }

  /**
   * @param dataCheckerModel
   */
  private void testDataCheckerModelWithoutVar(final PidcA2lDetails dataCheckerModel) {
    assertEquals("Pidc Version Id is equal", Long.valueOf(2597893281L), dataCheckerModel.getPidcVersionId());
    assertEquals("Pidc Version Name is equal", "test_Aniket2 (Version 1)", dataCheckerModel.getPidcVersionName());
    assertEquals("SDOM Pver Name is equal", "MMD114A0CC1788", dataCheckerModel.getPverName());
    assertEquals("Pver Variant Name is equal", "MC00", dataCheckerModel.getPverVariant());
    assertEquals("Pidc Variant Id is equal", null, dataCheckerModel.getPidcVariantId());
    assertEquals("Pidc Variant Name is equal", null, dataCheckerModel.getPidcVariantName());

  }
}
