/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class SdomPverServiceClientTest extends AbstractRestClientTest {

  // X_Test_002_P866_EA288
  private static final long PIDC_VERS_ID_ACTIVE = 773510915L;// Version 4
  private static final String SDOM_PVER_NAME = "M1764VDAC866";

  /**
   * Test retrieval of all pver names
   * <p>
   * Test method for {@link SdomPverServiceClient#getAllPverNames}
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetAllPverNames() throws ApicWebServiceException {
    SortedSet<String> retSet = new SdomPverServiceClient().getAllPverNames();

    assertFalse("Response should not be null or empty", (retSet == null) || (retSet.isEmpty()));

    LOG.info("First PVER = {}", retSet.iterator().next());
  }

  /**
   * Test retrieval of variants for given pver
   * <p>
   * Test method for {@link SdomPverServiceClient#getPverVariantNames(String)}
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetPverVariantNames() throws ApicWebServiceException {
    SortedSet<String> retSet = new SdomPverServiceClient().getPverVariantNames("P1633_HMCEUVI_C");

    assertFalse("Response should not be null or empty", (retSet == null) || (retSet.isEmpty()));

    LOG.info("First variant = {}", retSet.iterator().next());
  }

  /**
   * Test retrieval of variants for given pver(negative - invalid PVER name)
   * <p>
   * Test method for {@link SdomPverServiceClient#getPverVariantNames(String)}
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetPverVariantNamesNeg() throws ApicWebServiceException {
    SortedSet<String> retSet = new SdomPverServiceClient().getPverVariantNames("INVALID Name");

    assertTrue("Response should be empty", retSet.isEmpty());
  }

  /**
   * Test retrieval of versions of given pver and variant
   * <p>
   * Test method for {@link SdomPverServiceClient#getPverVariantVersions(String, String)}
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetPverVariantVersions() throws ApicWebServiceException {
    SortedSet<Long> retSet = new SdomPverServiceClient().getPverVariantVersions("P1633_HMCEUVI_C", "P1633V5003");

    assertFalse("Response should not be null or empty", (retSet == null) || (retSet.isEmpty()));

    LOG.info("First variant = {}", retSet.iterator().next());
  }

  /**
   * Test retrieval of versions of given pver and variant(negative - invalid PVER name)
   * <p>
   * Test method for {@link SdomPverServiceClient#getPverVariantVersions(String, String)}
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetPverVariantVersionsNeg1() throws ApicWebServiceException {
    SortedSet<Long> retSet = new SdomPverServiceClient().getPverVariantVersions("INVALID_pver", "P1633V5003");

    assertTrue("Response should be empty", retSet.isEmpty());
  }

  /**
   * Test retrieval of versions of given pver and variant(negative - invalid variant name)
   * <p>
   * Test method for {@link SdomPverServiceClient#getPverVariantVersions(String, String)}
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetPverVariantVersionsNeg2() throws ApicWebServiceException {
    SortedSet<Long> retSet = new SdomPverServiceClient().getPverVariantVersions("P1633_HMCEUVI_C", "invalid_VAR");

    assertTrue("Response should be empty", retSet.isEmpty());
  }

  /**
   * Test method for {@link SdomPverServiceClient#getAllPverNamesForPidcVersion(long)}. SDOM PVERs are defined at
   * variant level
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetAllPverNamesForPidcVersion() throws ApicWebServiceException {
    Set<String> retSet = new SdomPverServiceClient().getAllPverNamesForPidcVersion(PIDC_VERS_ID_ACTIVE);

    assertTrue("SDOM PVER Info available", CommonUtils.isNotEmpty(retSet));
    LOG.info("SDOM PVER Count = {}, List : {}", retSet.size(), retSet);

    assertTrue("SDOM Info for SDOM PVER " + SDOM_PVER_NAME + " available", retSet.contains(SDOM_PVER_NAME));
  }

  /**
   * Test method for {@link SdomPverServiceClient#getAllPverNamesForPidcVersion(long)}. SDOM PVER is defined at project
   * level
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetAllPverNamesForPidcVersionVersLevel() throws ApicWebServiceException {
    // PIDC Version: Alfa Romeo->Gasoline Engine->PC - Passenger Car->ME(D)17->Alfa 1.8L, FamB, Gen2, US (Version 1)
    // URL: icdm:pidvid,773519265
    Set<String> retSet = new SdomPverServiceClient().getAllPverNamesForPidcVersion(773519265L);

    assertTrue("SDOM PVER Info available", CommonUtils.isNotEmpty(retSet));
    LOG.info("SDOM PVER Count = {}, List : {}", retSet.size(), retSet);

    assertEquals("One SDOM PVER is available", 1, retSet.size());

    // SDOM PVER to check
    String sdomPverExp = "D173307";
    assertTrue("SDOM Info for SDOM PVER " + sdomPverExp + " available", retSet.contains(sdomPverExp));
  }

  /**
   * Test method for {@link SdomPverServiceClient#getAllPverNamesForPidcVersion(long)}. SDOM PVER is defined at project
   * level
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetAllPverNamesForMultiPidc() throws ApicWebServiceException {

    // [1664893405, 1684619018, 1664712092, 1666542706, 1664527228, 1676701402, 1658994121, 1737259297, 1684389527,
    // 1665952127, 1666133434, 1666315143, 1664530785, 1737447988, 1665075170, 1717510177, 773510565]

    // PIDC Version: Alfa Romeo->Gasoline Engine->PC - Passenger Car->ME(D)17->Alfa 1.8L, FamB, Gen2, US (Version 1)
    // URL: icdm:pidvid,773519265
    Map<Long, SortedSet<String>> retMap = new SdomPverServiceClient().getSdomPverByPidc(762L);
    assertTrue("SDOM PVER Info available", CommonUtils.isNotEmpty(retMap));
    LOG.info("SDOM PVER Count = {}, List : {}", retMap.size(), retMap);

    // assertEquals("One SDOM PVER is available", 1, retSet.size());
    SortedSet<String> retSet = retMap.get(1664893405l);
    // SDOM PVER to check
    String sdomPverExp = "DMG1001A01C1398";
    assertTrue("SDOM Info for SDOM PVER " + sdomPverExp + " available", retSet.contains(sdomPverExp));
  }

  /**
   * Test method for {@link SdomPverServiceClient#getSDOMPverName(Long, Long)}.
   * <p>
   * Get SDOM PVER name, for project with variants, attribute defined at variant level
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void getSDOMPverNameVarLevel() throws ApicWebServiceException {
    // PIDC Version: AUDI->Diesel Engine->PC - Passenger Car->MD1-C->Audi V6Gen3 (alle Leistungsklassen)
    long pidcVersionId = 775479170L;
    long pidcVariantId = 781480002L;
    String expPver = "PMD104A0DC1551";

    String ret = new SdomPverServiceClient().getSDOMPverName(pidcVersionId, pidcVariantId);
    LOG.info("SDOM PVER for pidc version {}, variant {} is : {}", pidcVersionId, pidcVariantId, ret);
    assertEquals("SDOM PVER for pidc version " + pidcVersionId + ", variant " + pidcVariantId + " is : " + expPver,
        expPver, ret);

  }


  /**
   * Test method for {@link SdomPverServiceClient#getSDOMPverName(Long, Long)}.
   * <p>
   * Get SDOM PVER name, for project without variants
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void getSDOMPverNameProjLevelA() throws ApicWebServiceException {
    // PIDC Version: Alfa Romeo->Gasoline Engine->PC - Passenger Car->ME(D)17->Alfa 1.8L, FamB, Gen2, US (Version 1)
    // URL: icdm:pidvid,773519265
    long pidcVersionId = 773519265L;
    String expPver = "D173307";

    String ret = new SdomPverServiceClient().getSDOMPverName(pidcVersionId, null);
    LOG.info("SDOM PVER for pidc version {} is : {}", pidcVersionId, ret);
    assertEquals("SDOM PVER for pidc version " + pidcVersionId + " is : " + expPver, expPver, ret);


  }

  /**
   * Test method for {@link SdomPverServiceClient#getSDOMPverName(Long, Long)}.
   * <p>
   * Get SDOM PVER name, for project with variants, but attribute defined at project level
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void getSDOMPverNameProjLevelB() throws ApicWebServiceException {
    // PIDC Variant: Toyota->Diesel Engine->PC - Passenger Car->EDC17->827F (Version 3)->Auris
    // URL: icdm:pidvarid,773511515-874282
    long pidcVersionId = 773511515L;
    long pidcVariantId = 874282L;
    String expPver = "P1383";

    String ret = new SdomPverServiceClient().getSDOMPverName(pidcVersionId, pidcVariantId);
    LOG.info("SDOM PVER for pidc version {}, variant {} is : {}", pidcVersionId, pidcVariantId, ret);
    assertEquals("SDOM PVER for pidc version " + pidcVersionId + ", variant " + pidcVariantId + " is : " + expPver,
        expPver, ret);

  }
}