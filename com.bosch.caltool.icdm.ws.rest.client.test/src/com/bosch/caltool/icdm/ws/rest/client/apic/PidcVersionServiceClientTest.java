/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.pidc.ExternalPidcVersionInfo;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDetStructure;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionInfo;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionStatisticsReport;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetails;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetailsInput;
import com.bosch.caltool.icdm.model.apic.pidc.ProjectIdCardAllVersInfoType;
import com.bosch.caltool.icdm.model.cdr.CompliReviewUsingHexData;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author AND4COB
 */
public class PidcVersionServiceClientTest extends AbstractRestClientTest {

  /**
   *
   */
  private static final String EXPECTED_EXCEPTION_NOT_THROWN = "Expected exception not thrown";
  /**
   *
   */
  private static final String NOT_FOUND = "' not found";
  /**
   *
   */
  private static final String SIZE = "Size: {}";
  /**
   *
   */
  private static final String RESPONSE_SHOULD_NOT_BE_NULL = "Response should not be null";
  /**
   *
   */
  private static final String RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY = "Response should not be null or empty";
  /**
  *
  */
  private static final String RESPONSE_SHOULD_BE_NULL_OR_EMPTY = "Response should  be null or empty";
  /**
   *
   */
  private static final String CREATED_DATE_IS_NOT_NULL = "Created date is not null";
  /**
   *
   */
  private static final String CREATED_USER_IS_EQUAL = "Created User is equal";
  /**
   *
   */
  private static final String DESCRIPTION_IS_EQUAL = "Description is equal";
  private static final Long PIDC_VERS_ID = 773510565L;
  private static final Long PIDC_ID = 762L;
  private static final Long INVALID_VERS_ID = -100L;
  private static final Long INVALID_VAR_ID = -1L;
  private static final Long PIDC_VERS_ID3 = 862095420L;
  private static final Long PIDC_VERS_ID1 = 781387043L;
  private static final Long PID_ATTR_LEVEL = 1L;
  private static final Long VARIANT_ID = 6177L;
  private static final Long VARIANT_ID1 = 786139117L;
  private static final String PVER_NAME = "DMG1001A01C1398";
  private static final Long PIDC_ATTR_LEVEL = 1L;
  private static final Long VALUE_ID = 787372417L;
  private static final String ACTIVEFLAG = "Y";
  private static final String NONACTIVEFLAG = "N";
  private static final Long NON_ACT_PIDC_VERS_ID = 786628320L;


  /**
   * Test retrieval of all definitions Test method for
   * {@link PidcVersionServiceClient#getAllActiveVersionsWithStructure() }
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetAllActiveVersionsWithStructure() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();
    Map<Long, PidcVersionInfo> retMap = servClient.getAllActiveVersionsWithStructure();
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, (retMap == null) || retMap.isEmpty());
    LOG.info(SIZE, retMap.size());
    PidcVersionInfo pidcVersionInfo = retMap.get(PIDC_VERS_ID);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, pidcVersionInfo);


    testPidcVersionInfo(pidcVersionInfo);

  }


  /**
   * @param pidcVersion
   */
  private void testPidcVersion(final PidcVersion pidcVersion) {
    assertEquals("Project_Id is equal", Long.valueOf(762), pidcVersion.getPidcId());
    assertEquals("Pro_Rev_Id is equal", Long.valueOf(1), pidcVersion.getProRevId());
    assertEquals("Pidc_Status is equal", "I", pidcVersion.getPidStatus());
    assertEquals("Version Name is equal", "Version 1", pidcVersion.getVersionName());
    assertNotNull(CREATED_DATE_IS_NOT_NULL, pidcVersion.getCreatedDate());
  }


  /**
   * @param pidc
   */
  private void testPidc(final Pidc pidc) {
    assertEquals("Project Id is equal", Long.valueOf(762), pidc.getId());
    assertEquals(DESCRIPTION_IS_EQUAL, "Audi EA888-Gen3-BZyklus", pidc.getDescEng());
    assertEquals(CREATED_USER_IS_EQUAL, "tbd2si", pidc.getCreatedUser());
    assertEquals("AprjId is equal", Long.valueOf(20328501), pidc.getAprjId());
  }


  /**
   * @param pidcVersionAttribute
   */
  private void testPidcVersionAttribute(final PidcVersionAttribute pidcVersionAttribute) {
    assertEquals(DESCRIPTION_IS_EQUAL, "Customer name or brand (e.g. VW, AUDI, SEAT, ...)",
        pidcVersionAttribute.getDescription());
    assertEquals("Id is equal", Long.valueOf(764), pidcVersionAttribute.getId());
    assertEquals("Naame is equal", "Customer/Brand", pidcVersionAttribute.getName());
    assertEquals("Attribute Id is equal", Long.valueOf(36), pidcVersionAttribute.getAttrId());
    assertEquals("Value is equal", "AUDI", pidcVersionAttribute.getValue());
    assertNotNull(CREATED_DATE_IS_NOT_NULL, pidcVersionAttribute.getCreatedDate());
  }

  /**
   * Test retrieval of all definitions Test method for a set of selected pidcs
   * {@link PidcVersionServiceClient#getActiveVersionsWithStructure(Set)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetActiveVersionsWithStructure() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();
    // 762 Audi EA888-Gen3-BZyklus
    // 2454 Audi EA888-Gen2-B8-laengs
    // 4190 HONDA XE1B 2SV
    Map<Long, PidcVersionInfo> retMap =
        servClient.getActiveVersionsWithStructure(new HashSet<>(Arrays.asList(762L, 2454L, 4190L)));
    // Tables: T_Pidc_Version, TabV_ProjectIdCard, TabV_Attr_Values
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, (retMap == null) || retMap.isEmpty());
    LOG.info(SIZE, retMap.size());

    PidcVersionInfo pidcVersionInfo = retMap.get(PIDC_VERS_ID); // for pidcId=762 pidc_vers_id = 773510565L
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, pidcVersionInfo);

    testPidcVersionInfo(pidcVersionInfo);

  }

  /**
   * @param pidcVersionInfo
   */
  private void testPidcVersionInfo(final PidcVersionInfo pidcVersionInfo) {
    // validation of PidcVersion instance
    PidcVersion pidcVersion = pidcVersionInfo.getPidcVersion();
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, pidcVersion);
    testPidcVersion(pidcVersion);

    // validation of Pidc instance
    Pidc pidc = pidcVersionInfo.getPidc();
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, pidc);
    testPidc(pidc);

    // validation of PidcVersionAttribute instance
    Map<Long, PidcVersionAttribute> levelAttrMap = pidcVersionInfo.getLevelAttrMap();
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, (levelAttrMap == null) || levelAttrMap.isEmpty());
    PidcVersionAttribute pidcVersionAttribute = levelAttrMap.get(PIDC_ATTR_LEVEL);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, pidcVersionAttribute);
    testPidcVersionAttribute(pidcVersionAttribute);
  }

  /**
   * Test method for {@link PidcVersionServiceClient#getActiveExternalVersionsWithStructure(Set)}
   */
  @Test
  public void testGetActiveExternalWithStructures() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();
    // 762 Audi EA888-Gen3-BZyklus
    Set<Long> pidcSet = new HashSet<>();
    pidcSet.add(PIDC_ID);
    Map<Long, ExternalPidcVersionInfo> pidcInfoTypeMap = servClient.getActiveExternalVersionsWithStructure(pidcSet);
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, (pidcInfoTypeMap == null) || pidcInfoTypeMap.isEmpty());
    LOG.info(SIZE, pidcInfoTypeMap.size());

    ExternalPidcVersionInfo extPidcVersionInfo = pidcInfoTypeMap.get(PIDC_VERS_ID);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, extPidcVersionInfo);

    testPidcVersionInfo(extPidcVersionInfo);
  }

  /**
   * Testing with empty input set {@link PidcVersionServiceClient#getActiveVersionsWithStructure(Set)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetActiveVersWithStructNegative() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();
    Map<Long, PidcVersionInfo> retMap = servClient.getActiveVersionsWithStructure(new HashSet<>(Arrays.asList()));
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, (retMap == null) || retMap.isEmpty());
    LOG.info(SIZE, retMap.size());// all the records are considered
  }


  /**
   * Test method for {@link PidcVersionServiceClient#getPidcVersionsWithStructure(Set)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetPidcVersionsWithStructure() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();
    Map<Long, PidcVersionInfo> retMap =
        servClient.getPidcVersionsWithStructure(new HashSet<>(Arrays.asList(773510565L, 786628315L, 773511015L)));
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, (retMap == null) || retMap.isEmpty());
    LOG.info(SIZE, retMap.size());
    PidcVersionInfo pidcVersionInfo = retMap.get(PIDC_VERS_ID); // pidc_vers_id = 773510565L
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, pidcVersionInfo);
    testPidcVersionInfo(pidcVersionInfo);

  }

  /**
   * Test method for {@link PidcVersionServiceClient#getPidcVersionsWithStructure(Set)} with Inactive Ids and Inactive
   * flag
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetPidcVersionWithInactiveIdsAndInactiveFlag() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();

    Map<Long, PidcVersionInfo> retMap =
        servClient.getPidcVersionsWithStructure(new HashSet<>(Arrays.asList("SEG5FE", "TRL1COB")), "Write",
            new HashSet<>(Arrays.asList(786628317L, 1274091767L)),
            new HashSet<>(Arrays.asList(786628320L, 12841893231L)), "UEGO", NONACTIVEFLAG);

    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, (retMap == null) || retMap.isEmpty());
    PidcVersionInfo pidcVersionInfo = retMap.get(NON_ACT_PIDC_VERS_ID);// pidc_vers_id = 786628320L
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, pidcVersionInfo);
    testGetPidcVersionInfoWithActive(pidcVersionInfo, NONACTIVEFLAG);
  }

  /**
   * Test method for {@link PidcVersionServiceClient#getPidcVersionsWithStructure(Set)} with active Ids and active flag
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetPidcVersionWithActiveIdsAndActiveFlag() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();
    Map<Long, PidcVersionInfo> retMap =
        servClient.getPidcVersionsWithStructure(null, null, new HashSet<>(Arrays.asList(862095417L, 770019817L)),
            new HashSet<>(Arrays.asList(862095420L, 773518715L)), "P1518", ACTIVEFLAG);
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, (retMap == null) || retMap.isEmpty());
    PidcVersionInfo pidcVersionInfo = retMap.get(PIDC_VERS_ID3);// pidc_vers_id =862095417L
    testGetPidcVersionInfoWithActive(pidcVersionInfo, ACTIVEFLAG);
  }

  /**
   * Test method for {@link PidcVersionServiceClient#getPidcVersionsWithStructure(Set)} with Inactive Ids and active
   * flag
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetPidcVersionWithInActiveIdsAndActiveFlag() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();
    Map<Long, PidcVersionInfo> retMap = servClient.getPidcVersionsWithStructure(new HashSet<>(Arrays.asList("SEG5FE")),
        "Write", new HashSet<>(Arrays.asList(786628317L, 1274091767L)),
        new HashSet<>(Arrays.asList(786628320L, 12841893231L)), "UEGO", ACTIVEFLAG);
    assertTrue(RESPONSE_SHOULD_BE_NULL_OR_EMPTY, (retMap == null) || retMap.isEmpty());

  }

  /**
   * Test method for {@link PidcVersionServiceClient#getPidcVersionsWithStructure(Set)} with pidcversionId and active
   * flag
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetPidcVersionForPidcVersionIDAndActiveFlag() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();
    Map<Long, PidcVersionInfo> retMap = servClient.getPidcVersionsWithStructure(null, null, null,
        new HashSet<>(Arrays.asList(786628320L, 12841893231L)), null, NONACTIVEFLAG);
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, (retMap == null) || retMap.isEmpty());
    PidcVersionInfo pidcVersionInfo = retMap.get(NON_ACT_PIDC_VERS_ID);// pidc_vers_id = 786628320L
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, pidcVersionInfo);
    testGetPidcVersionInfoWithActive(pidcVersionInfo, NONACTIVEFLAG);
  }

  /**
   * Test method for {@link PidcVersionServiceClient#getPidcVersionsWithStructure(Set)} with pidcId and active flag
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetPidcVersionForPidcIDAndActiveFlag() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();
    Map<Long, PidcVersionInfo> retMap = servClient.getPidcVersionsWithStructure(null, null,
        new HashSet<>(Arrays.asList(786628317L, 1274091767L)), null, "UEGO", NONACTIVEFLAG);
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, (retMap == null) || retMap.isEmpty());
  }

  /**
   * Test method for {@link PidcVersionServiceClient#getPidcVersionsWithStructure(Set)} with userNtID and active flag
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetPidcVersionForUserNTIDAndActiveFlag() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();
    Map<Long, PidcVersionInfo> retMap = servClient.getPidcVersionsWithStructure(
        new HashSet<>(Arrays.asList("SEG5FE", "TRL1COB")), "Write", null, null, null, NONACTIVEFLAG);
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, (retMap == null) || retMap.isEmpty());
  }

  /**
   * Test method for {@link PidcVersionServiceClient#getPidcVersionsWithStructure()} with pidcName and active flag
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetPidcVersionForPidcNameAndActiveFlag() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();
    Map<Long, PidcVersionInfo> retMap =
        servClient.getPidcVersionsWithStructure(null, null, null, null, "UEGO", NONACTIVEFLAG);
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, (retMap == null) || retMap.isEmpty());
  }


  /**
   * Testing with empty input set for {@link PidcVersionServiceClient#getPidcVersionsWithStructure(Set)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetPidcVersWithStructNegative() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();
    Map<Long, PidcVersionInfo> retMap = servClient.getPidcVersionsWithStructure(new HashSet<>(Arrays.asList()));
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, (retMap == null) || retMap.isEmpty());
    LOG.info(SIZE, retMap.size());// all the records are considered
  }

  /**
   * Test method for {@link PidcVersionServiceClient#getById(Long)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetById() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();
    PidcVersion pidcVersion = servClient.getById(PIDC_VERS_ID);
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL, (pidcVersion == null));
    testPidcVersion(pidcVersion);
  }


  /**
   * Negative Test method for {@link PidcVersionServiceClient#getById(Long)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetByIdNegative() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("PIDC Version with ID '" + INVALID_VERS_ID + NOT_FOUND);
    servClient.getById(INVALID_VERS_ID);
    fail(EXPECTED_EXCEPTION_NOT_THROWN);
  }


  /**
   * Test method for {@link PidcVersionServiceClient#getPidcDetStructure(Long)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetPidcDetStructure() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();
    Map<Long, PidcDetStructure> retMap = servClient.getPidcDetStructure(PIDC_VERS_ID1);// Table TABV_PIDC_DET_STRUCTURE
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, (retMap == null) || retMap.isEmpty());
    PidcDetStructure pidcDetStructure = retMap.get(PID_ATTR_LEVEL);// for retMap, key is PID_ATTR_LEVEL
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, pidcDetStructure);
    testPidcDetStructure(pidcDetStructure);
  }


  /**
   * @param pidcDetStructure
   */
  private void testPidcDetStructure(final PidcDetStructure pidcDetStructure) {
    assertEquals("Pds_Id is equal", Long.valueOf(788344516), pidcDetStructure.getId());
    assertEquals("Attr_Id is equal", Long.valueOf(258), pidcDetStructure.getAttrId());
    assertEquals(CREATED_USER_IS_EQUAL, "ZIG5FE", pidcDetStructure.getCreatedUser());
    assertNotNull("Created Date is not null", pidcDetStructure.getCreatedDate());
  }


  /**
   * Test method for {@link PidcVersionServiceClient#getAllPidcVersionForPidc(Long)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetAllPidcVersionForPidc() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();
    Map<Long, PidcVersion> retMap = servClient.getAllPidcVersionForPidc(PIDC_ID);
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, (retMap == null) || retMap.isEmpty());

    PidcVersion pidcVersion = retMap.get(PIDC_VERS_ID);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, pidcVersion);
    testPidcVersion(pidcVersion);
  }

  /**
   * Test method for {@link PidcVersionServiceClient#getPidcVersionWithDetails(Long)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetPidcVersionWithDetails() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();
    PidcVersionWithDetails ret = servClient.getPidcVersionWithDetails(PIDC_VERS_ID1);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, ret);
    Map<Long, PidcVariant> pidcVariantMap = ret.getPidcVariantMap(); // TABV_PROJECT_VARIANTS
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, (pidcVariantMap == null) || pidcVariantMap.isEmpty());
    PidcVariant pidcVariant = pidcVariantMap.get(VARIANT_ID1);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, pidcVariant);
    testPidcVariant(pidcVariant);
  }

  /**
   * Test method for {@link PidcVersionServiceClient#getPidcVersionWithDetails(Long)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testPostPidcVersionWithDetails() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();
    PidcVersionWithDetailsInput input = new PidcVersionWithDetailsInput(PIDC_VERS_ID1, true);
    PidcVersionWithDetails ret = servClient.getPidcVersionWithDetails(input);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, ret);
    Map<Long, PidcVariant> pidcVariantMap = ret.getPidcVariantMap(); // TABV_PROJECT_VARIANTS
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, (pidcVariantMap == null) || pidcVariantMap.isEmpty());
    PidcVariant pidcVariant = pidcVariantMap.get(VARIANT_ID1);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, pidcVariant);
    testPidcVariant(pidcVariant);
  }

  /**
   * @param pVariant
   */
  private void testPidcVariant(final PidcVariant pVariant) {
    assertEquals("Value_Id is equal", Long.valueOf(786139116), pVariant.getNameValueId());
    assertNotNull(CREATED_DATE_IS_NOT_NULL, pVariant.getCreatedDate());
    assertEquals(CREATED_USER_IS_EQUAL, "ZIG5FE", pVariant.getCreatedUser());
    assertEquals("Deleted Flag is equal", false, pVariant.isDeleted());
  }


  /**
   * Test method for {@link PidcVersionServiceClient#getPidcVersDetailsForCompliHex(Long)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetPidcVersDetailsForCompliHex() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();
    CompliReviewUsingHexData ret = servClient.getPidcVersDetailsForCompliHex(PIDC_VERS_ID);
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL, ret == null);

    // validation of pidcSdomPverSet
    Set<String> pidcSdomPverSet = ret.getPidcSdomPverSet();
    assertFalse("Response should not be empty", pidcSdomPverSet.isEmpty());
    assertTrue("PVER is available", pidcSdomPverSet.contains(PVER_NAME));

    // validation of selPidc
    Pidc selPidc = ret.getSelPidc();
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL, selPidc == null);
    testPidc(selPidc);

    // validation of pidcVariantSet
    Set<PidcVariant> pidcVariantSet = ret.getPidcVariants();
    assertFalse("Response should not be empty", pidcVariantSet.isEmpty());
    LOG.info("Sixe: {}", pidcVariantSet.size());
    boolean pidcVariantAvailable = false;
    for (PidcVariant pidcVariant : pidcVariantSet) {
      if (pidcVariant.getId().equals(VARIANT_ID)) {
        pidcVariantAvailable = true;
        testPidcVariant1(pidcVariant);
        break;
      }
    }
    assertTrue("Pidc Variant is available", pidcVariantAvailable);

    // validation of pidcVersion
    PidcVersion pidcVersion = ret.getPidcVersion();
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL, pidcVersion == null);
    testPidcVersion(pidcVersion);
  }


  /**
   * @param pidcVariant
   */
  private void testPidcVariant1(final PidcVariant pidcVariant) {
    assertEquals("Value_Id is equal", Long.valueOf(6176), pidcVariant.getNameValueId());
    assertEquals(CREATED_USER_IS_EQUAL, "tbd2si", pidcVariant.getCreatedUser());
    assertEquals("Deleted Flag is equal", false, pidcVariant.isDeleted());
    assertNotNull(CREATED_DATE_IS_NOT_NULL, pidcVariant.getCreatedDate());
  }

  /**
   * Negative Test method for {@link PidcVersionServiceClient#getPidcVersDetailsForCompliHex(Long)}
   *
   * @throws ApicWebServiceException web service error
   */
  // TODO The test fails as invalid ID case is not handled in the service
  // Internal server error occurs for this negative case , this has to be handled
  // Hence, test annotation is removed.
  public void testGetPidcVersDetailsForCompliHexNegative() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("PIDC Version with ID '" + INVALID_VERS_ID + NOT_FOUND);
    servClient.getPidcVersDetailsForCompliHex(INVALID_VERS_ID);
    fail(EXPECTED_EXCEPTION_NOT_THROWN);
  }


  /**
   * Test method for {@link PidcVersionServiceClient#getActivePidcVersion(Long)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetActivePidcVersion() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();
    PidcVersion pidcVersion = servClient.getActivePidcVersion(PIDC_ID);
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL, pidcVersion == null);
    testPidcVersion(pidcVersion);
  }


  /**
   * Test method for {@link PidcVersionServiceClient#getAprjPIDCs(String)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testgetAprjPIDCs() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();
    SortedSet<PidcVersion> retSet = servClient.getAprjPIDCs("X_Test_Hz_AUDI_1788");
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL);

    boolean pidcAvailable = false;
    for (PidcVersion pidcVersion : retSet) {
      if (pidcVersion.getId().equals(1165057178L)) {
        testPidcVer(pidcVersion);
        pidcAvailable = true;
      }
    }
    assertTrue("Pidc is available", pidcAvailable);
  }

  /**
   * @param pidcVersion
   */
  private void testPidcVer(final PidcVersion pidcVersion) {
    assertEquals(DESCRIPTION_IS_EQUAL, "first version for testing", pidcVersion.getDescription());
    assertEquals(CREATED_USER_IS_EQUAL, "HEF2FE", pidcVersion.getCreatedUser());
    assertEquals("Pro_Rev_Id is equal", Long.valueOf(1), pidcVersion.getProRevId());
  }

  /**
   * Test method for {@link PidcVersionServiceClient#getActiveVersWithStrByOtherVerId(Set)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetActiveVersWithStrByOtherVerId() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();
    Map<Long, PidcVersionInfo> retMap =
        servClient.getActiveVersWithStrByOtherVerId(new HashSet<>(Arrays.asList(773510565L, 773510715L, 773511015L)));
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, (retMap == null) || retMap.isEmpty());
    PidcVersionInfo ret = retMap.get(PIDC_VERS_ID); // pidc_vers_id = 773510565L
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL);
    PidcVersion pidcVersion = ret.getPidcVersion();
    testPidcVersion(pidcVersion);

  }


  /**
   * Testing with empty input set for {@link PidcVersionServiceClient#getActiveVersWithStrByOtherVerId(Set)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetActiveVersWithStrByOtherVerIdNegative() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();
    Map<Long, PidcVersionInfo> retMap = servClient.getActiveVersWithStrByOtherVerId(new HashSet<>(Arrays.asList()));
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, (retMap == null) || retMap.isEmpty());
    LOG.info(SIZE, retMap.size());// all the records will be considered
  }

  /**
   * Test method for {@link PidcVersionServiceClient#getAllProjectIdCardVersion()}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetAllProjectIdCardVersion() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();
    List<ProjectIdCardAllVersInfoType> pidcInfoTypeList = servClient.getAllProjectIdCardVersion();
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, (pidcInfoTypeList == null) || pidcInfoTypeList.isEmpty());

    LOG.info(SIZE, pidcInfoTypeList.size());

    ProjectIdCardAllVersInfoType projectIdCardAllVersInfoTypeToTest = null;

    boolean pidcAllVerInfoAvailable = false;

    for (ProjectIdCardAllVersInfoType projectIdCardAllVersInfoType : pidcInfoTypeList) {
      if (projectIdCardAllVersInfoType.getId() == 1048956467L) {
        projectIdCardAllVersInfoTypeToTest = projectIdCardAllVersInfoType;
        testPidcInfo(projectIdCardAllVersInfoTypeToTest);
        pidcAllVerInfoAvailable = true;
        break;
      }
    }
    assertTrue("ProjectIdCardVersiontype is available", pidcAllVerInfoAvailable);
  }

  /**
   * @param projectIdCardAllVersInfoTypeToTest
   */
  private void testPidcInfo(final ProjectIdCardAllVersInfoType projectIdCardAllVersInfoTypeToTest) {
    assertEquals(CREATED_USER_IS_EQUAL, "LOL5SI", projectIdCardAllVersInfoTypeToTest.getCreateUser());
    assertNotNull("Created Date is not null", projectIdCardAllVersInfoTypeToTest.getCreateDate());
  }

  /**
   * Test method for {@link PidcVersionServiceClient#getPidcVersionStatisticsReport(Long, String)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetPidcVersionStatisticsReport() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();
    PidcVersionStatisticsReport pidcVersionStatisticsReport =
        servClient.getPidcVersionStatisticsReport(791409917l, "pidc");

    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, (pidcVersionStatisticsReport == null));
  }

  /**
   * Test method for {@link PidcVersionServiceClient#getPidcVariantStatisticsReport(Long)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetPidcVariantStatisticsReport() throws ApicWebServiceException {
    PidcVersionStatisticsReport pidcVersionStatisticsReport =
        new PidcVersionServiceClient().getPidcVariantStatisticsReport(23508067238L);

    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, (pidcVersionStatisticsReport == null));
  }

  /**
   * Negative Test method for {@link PidcVersionServiceClient#getPidcVersionStatisticsReport(Long, String)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetPidcVersionStatisticsReportNegative() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("ID '" + INVALID_VERS_ID + "' is invalid for Project ID Card");
    new PidcVersionServiceClient().getPidcVersionStatisticsReport(INVALID_VERS_ID, "pidc");
    fail(EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * Negative Test method for {@link PidcVersionServiceClient#getPidcVersionStatisticsReport(Long, String)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetPidcVersionStatisticsReportNegativeType() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("The type should be either pidc or pidcVersion");
    new PidcVersionServiceClient().getPidcVersionStatisticsReport(INVALID_VERS_ID, "");
    fail(EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * Negative Test method for {@link PidcVersionServiceClient#getPidcVariantStatisticsReport(Long)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetPidcVariantStatisticsReportNegative() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("PIDC Variant with ID '" + INVALID_VAR_ID + NOT_FOUND);
    new PidcVersionServiceClient().getPidcVariantStatisticsReport(INVALID_VAR_ID);
    fail(EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * Test method for {@link PidcVersionServiceClient#getOtherVersionsForPidc(Long)}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetOtherVersionsForPidc() throws ApicWebServiceException {
    Map<Long, PidcVersion> retMap = new PidcVersionServiceClient().getOtherVersionsForPidc(2747L);// X_Test_002_P866_EA288
    assertTrue("Other versions available", CommonUtils.isNotEmpty(retMap));
    LOG.info("Other versions (count = {}) : ", retMap.size());
    retMap.entrySet().stream().forEach(e -> LOG.info("  {} : {}", e.getKey(), e.getValue().getVersionName()));
    assertTrue("Other version contains id 773510765(Version 1)", retMap.containsKey(773510765L));
  }


  /**
   * Test method for {@link PidcVersionServiceClient#createNewPidcVersion(PidcVersion)},
   * {@link PidcVersionServiceClient#editPidcVersion(PidcVersion)}.
   *
   * @throws ApicWebServiceException web error
   */
  @Test
  public void testCreateNewPidcVersAndEditPidcVers() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();
    PidcVersion pidcVersion = new PidcVersion();
    pidcVersion.setVersionName("junitTest_Version" + getRunId());
    pidcVersion.setVersDescEng("junit 1");
    pidcVersion.setPidcId(760420017L);
    pidcVersion.setParentPidcVerId(773515265L);

    // Invoke createNewPidcVersion method
    PidcVersion createObj = servClient.createNewPidcVersion(pidcVersion);
    assertNotNull("Created object is  not null", createObj);

    // validate create
    assertEquals("PidcId is equal", Long.valueOf(760420017L), createObj.getPidcId());
    assertEquals("VersionName is equal", "junitTest_Version" + getRunId(), createObj.getVersionName());
    assertEquals("VersDesEeng is equal", "junit 1", createObj.getVersDescEng());


    // invoke editPidcVersion method
    createObj.setVersDescEng("junitTest_DescEng" + getRunId() + "Update");
    PidcVersion editedPidcVersion = servClient.editPidcVersion(createObj);
    assertNotNull("Response should not be null ", editedPidcVersion);
    assertEquals("VersDescEng is equal", "junitTest_DescEng" + getRunId() + "Update",
        editedPidcVersion.getVersDescEng());
  }

  /**
   * Test method for {@link PidcVersionServiceClient#createNewRevisionForPidcVers(PidcVersion)}.
   *
   * @throws ApicWebServiceException web error
   */
  @Test
  public void testCreateNewRevisionForPidcVers() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();

    // invoke createNewRevisionForPidcVers method
    PidcVersion parentPidcVersion = servClient.getById(1737639422L);
    PidcVersion newPidcVersion = new PidcVersion();
    newPidcVersion.setPidcId(parentPidcVersion.getPidcId());
    newPidcVersion.setParentPidcVerId(parentPidcVersion.getId());
    newPidcVersion.setName("JUnit_TEST_PIDC_" + getRunId());
    newPidcVersion.setVersionName("JUnit_V_newVersTest" + getRunId());
    newPidcVersion.setVersDescEng("JUnuit_TEST_PIDC_Desc Eng" + getRunId());
    newPidcVersion.setVersDescGer("JUnuit_TEST_PIDC_Desc Ger" + getRunId());
    PidcVersion createdObj = servClient.createNewRevisionForPidcVers(newPidcVersion);

    // validate create
    assertNotNull("Created object is not null", createdObj);
    assertEquals("VersDescEng is equal", "JUnuit_TEST_PIDC_Desc Eng" + getRunId(), createdObj.getVersDescEng());

  }


  /**
   * Test method for {@link PidcVersionServiceClient#isQnaireConfigAttrUsedInReview(Long)}.
   *
   * @throws ApicWebServiceException web error
   */
  @Test
  public void testHasProjectAttributeUsedInReview() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();
    assertTrue(servClient.isQnaireConfigAttrUsedInReview(10827299181L));
  }


  /**
   * @throws ApicWebServiceException webservice error
   */
  @Test
  public void testIsCoCWPMappingAvailForPidcVersionAssertTrue() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();
    assertTrue(servClient.isCoCWPMappingAvailForPidcVersion(25041655339L));
  }

  /**
   * @throws ApicWebServiceException webservice error
   */
  @Test
  public void testIsCoCWPMappingNotAvailForPidcVersionAssertFalse() throws ApicWebServiceException {
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();
    assertFalse(servClient.isCoCWPMappingAvailForPidcVersion(1007223720L));
  }

  /**
   * @throws ApicWebServiceException webservice error
   */
  @Test
  public void testIsCoCWPMappingNotAvailForPidcVersionInvalidID() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("ID '" + INVALID_VERS_ID + "' is invalid for PIDC Version");
    PidcVersionServiceClient servClient = new PidcVersionServiceClient();
    assertTrue(servClient.isCoCWPMappingAvailForPidcVersion(INVALID_VERS_ID));
  }


  private void testGetPidcVersionInfoWithActive(final PidcVersionInfo pidcVersionInfo, final String active) {
    boolean isActive =
        CommonUtils.isEqual(pidcVersionInfo.getPidc().getProRevId(), pidcVersionInfo.getPidcVersion().getProRevId());

    if (isActive && "Y".equalsIgnoreCase(active)) {
      PidcVersion pidcVersion = pidcVersionInfo.getPidcVersion();
      assertEquals(true, pidcVersionInfo.isActive());

      assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, pidcVersion);


      // validation of Pidc instance
      Pidc pidc = pidcVersionInfo.getPidc();
      assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, pidc);


      // validation of PidcVersionAttribute instance
      Map<Long, PidcVersionAttribute> levelAttrMap = pidcVersionInfo.getLevelAttrMap();
      assertFalse(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, (levelAttrMap == null) || levelAttrMap.isEmpty());
      PidcVersionAttribute pidcVersionAttribute = levelAttrMap.get(PIDC_ATTR_LEVEL);
      assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, pidcVersionAttribute);

    }
    else if (!isActive && "N".equalsIgnoreCase(active)) {
      PidcVersion pidcVersion = pidcVersionInfo.getPidcVersion();
      assertEquals(false, pidcVersionInfo.isActive());
      assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, pidcVersion);


      // validation of Pidc instance
      Pidc pidc = pidcVersionInfo.getPidc();
      assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, pidc);


      // validation of PidcVersionAttribute instance
      Map<Long, PidcVersionAttribute> levelAttrMap = pidcVersionInfo.getLevelAttrMap();
      assertFalse(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, (levelAttrMap == null) || levelAttrMap.isEmpty());
      PidcVersionAttribute pidcVersionAttribute = levelAttrMap.get(PIDC_ATTR_LEVEL);
      assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, pidcVersionAttribute);

    }

  }
}

