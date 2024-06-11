/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.model.a2l.UnmapA2LResponse;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.Matchers;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcA2lServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author mkl2cob
 */
public class UnmapA2LServiceClientTest extends AbstractRestClientTest {

  /**
   *
   */
  private static final Long PIDC_VERS_ID = 3203765158L;
  /**
   * Pidc version name-PIDC_For_Questionnaire_Response (v1)
   */
  private static final Long PIDC_A2L_ID = 10937393391L;
  /**
   * Pidc version name - test_muthu_3 (v1)<br>
   * A2L file name - 003_VW_MMD114A0CC1788_MEA0_5.A2L
   */
  private static final Long PIDC_A2L_ID1 = 14439423878L;

  /**
   * Pidc version name-JUNIT_test_Unmap_service (DO NOT EDIT) <br>
   * A2L file name - 003_VW_MMD114A0CC1788_MEF0_5.A2L
   */
  private static final Long PIDC_A2L_ID_2 = 14786634328L;

  /**
   * Pidc version name-X_Test_HENZE_1788_1 (V1_0) <br>
   * A2L File name -MMD114A0CC1788_MC50_DISCR.A2L
   */
  private static final Long PIDC_A2L_ID_3 = 1165057193L;

  /**
   * Pidc version name-test_monica_no_var_1 (v1) <br>
   * A2L File name -009-Fiat-Fire-Mx17_3B0_Tb_S2.A2L
   */
  private static final Long PIDC_A2L_ID_4 = 10300579678L;

  /**
   * Pidc version name-test_monica_no_var_1 (v1) <br>
   * A2L File name -009-Fiat-Fire-Mx17_4B0_CuSW&S2_TB.A2L
   */
  private static final Long PIDC_A2L_ID_5 = 3504636560L;

  /**
   * @throws ApicWebServiceException Exception {@link UnmapA2LServiceClient#getRelatedDbEntries(Long)}
   */
  @Test
  public void testGetRelatedEntries() throws ApicWebServiceException {
    UnmapA2LServiceClient unmapA2LClient = new UnmapA2LServiceClient();
    UnmapA2LResponse relatedDbEntries = unmapA2LClient.getRelatedDbEntries(PIDC_A2L_ID);
    assertNotNull(relatedDbEntries);
  }

  /**
   * @throws ApicWebServiceException Exception {@link UnmapA2LServiceClient#getRelatedDbEntries(Long)}
   */
  @Test
  public void testGetRelatedEntriesCount() throws ApicWebServiceException {
    UnmapA2LServiceClient unmapA2LClient = new UnmapA2LServiceClient();
    UnmapA2LResponse relatedDbEntries = unmapA2LClient.getRelatedDbEntries(PIDC_A2L_ID_2);
    assertNotNull(relatedDbEntries);
    // initialise expected values
    int[] expectedValues = new int[6];
    expectedValues[0] = 1;
    expectedValues[1] = 2;
    expectedValues[2] = 6;
    expectedValues[3] = 4;
    expectedValues[4] = 126394;
    // initialise actual values
    int[] actualValues = new int[6];
    actualValues[0] = relatedDbEntries.getRvwResCount();
    actualValues[1] = relatedDbEntries.getDefVersCount();
    actualValues[2] = relatedDbEntries.getVarGrpCount();
    actualValues[3] = relatedDbEntries.getWpRespCombinationsCount();
    actualValues[4] = relatedDbEntries.getParamMappingCount();

    assertEquals("A2l file name is same", "003_VW_MMD114A0CC1788_MEF0_5.A2L", relatedDbEntries.getA2lFileName());
    assertArrayEquals("Count is equal", expectedValues, actualValues);
  }

  /**
   * @throws ApicWebServiceException Exception {@link UnmapA2LServiceClient#getRelatedDbEntries(Long)}
   */
  @Test
  public void testA2LUnmapWithCompli() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("UNMAP_A2L.COMPLI_REVIEWS_ATTACHED"));
    this.thrown.expectMessage("A2L file cannot be unmapped as there are compliance reviews attached to it");

    UnmapA2LServiceClient unmapA2LClient = new UnmapA2LServiceClient();
    unmapA2LClient.getRelatedDbEntries(PIDC_A2L_ID_3);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * @throws ApicWebServiceException Exception
   */
  @Test
  public void testA2LUnmapWithCDFxDirect() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("UNMAP_A2L.CDFX_DELIVERIES_ATTACHED"));
    this.thrown.expectMessage("A2L file cannot be unmapped as there are 100% CDFX exports attached to it");

    UnmapA2LServiceClient unmapA2LClient = new UnmapA2LServiceClient();
    unmapA2LClient.getRelatedDbEntries(PIDC_A2L_ID_4);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * @throws ApicWebServiceException Exception
   */
  @Test
  public void testA2LUnmapWithCDFxInDirect() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("UNMAP_A2L.CDFX_DELIVERIES_ATTACHED"));
    this.thrown.expectMessage("A2L file cannot be unmapped as there are 100% CDFX exports attached to it");

    UnmapA2LServiceClient unmapA2LClient = new UnmapA2LServiceClient();
    unmapA2LClient.getRelatedDbEntries(PIDC_A2L_ID_5);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * {@link UnmapA2LServiceClient#deleteA2lrelatedEntries(Long)}
   *
   * @throws ApicWebServiceException error from service
   */
  @Test
  public void testDeleteA2lrelatedEntries() throws ApicWebServiceException {
    PidcA2lServiceClient pidcA2lSerClinet = new PidcA2lServiceClient();
    // map A2L to PIDC Version
    PidcA2l pidcA2lToMap = pidcA2lSerClinet.getById(PIDC_A2L_ID1);
    pidcA2lToMap.setPidcVersId(PIDC_VERS_ID);
    pidcA2lToMap.setActive(true);

    Set<PidcA2l> pidcA2ls = new HashSet<>();
    pidcA2ls.add(pidcA2lToMap);
    pidcA2lSerClinet.update(pidcA2ls);

    // unmap A2L
    new UnmapA2LServiceClient().deleteA2lrelatedEntries(PIDC_A2L_ID1);
    PidcA2l pidcA2l = pidcA2lSerClinet.getById(PIDC_A2L_ID1);
    assertNull("PIDC Vers id should be null after unmapping of A2L", pidcA2l.getPidcVersId());
    assertFalse("Wp Param Present flag should be false after unmapping of A2L", pidcA2l.isWpParamPresentFlag());
    assertFalse("Active flag should be false after unmapping of A2L", pidcA2l.isActive());
    assertFalse("Active Wp Param Present flag should be false after unmapping of A2L",
        pidcA2l.isActiveWpParamPresentFlag());
    assertFalse("Working Set modified flag should be false after unmapping of A2L", pidcA2l.isWorkingSetModified());
  }
}
