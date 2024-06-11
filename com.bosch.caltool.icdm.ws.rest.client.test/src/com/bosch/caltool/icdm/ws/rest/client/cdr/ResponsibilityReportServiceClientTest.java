/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.bosch.caltool.icdm.model.cdr.ResponsibiltyRvwDataReport;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author rgo7cob
 */
public class ResponsibilityReportServiceClientTest extends AbstractRestClientTest {

  /**
   * X_TestCustomer -> Gasoline -> PC -> ME(D)17 -> CDR_Info_Wp_Defn_Vers_01
   * (CDR_Info_Wp_Defn_Vers_01)->MMD114A0CC1788_MD00.A2L icdm:a2lid,3480208916-2155255001
   */
  private static final Long PIDC_A2L_ID_T01 = 3480208916L;
  private static final Long VARIANT_ID_T01 = 3480208921L;// WP_DEF_TEST_VAR_1
  /**
   * X_TestCustomer -> Gasoline -> PC -> ME(D)17 -> CDR_Info_Wp_Defn_Vers_01
   * (CDR_Info_Wp_Defn_Vers_01)->MMD114A0CA1788_MV00_ext.A2L icdm:a2lid,3480208890-1853790157
   */
  private static final Long PIDC_A2L_ID_T02 = 3480208890L;
  private static final Long VARIANT_ID_T02 = 3480208923L; // WP_DEF_TEST_VAR_1
  
  private static final Long Invalid_ID = -100L;

  /**
   * Test CDR Report generation, with variant input
   *
   * @throws ApicWebServiceException error from service call
   */
  @Test
  public void test01() throws ApicWebServiceException {
    ResponsibiltyRvwDataReport response =
        new ResponsibilityReportServiceClient().getResponsiblityDetails(PIDC_A2L_ID_T01, VARIANT_ID_T01);

    // validation
    assertNotNull("Responsibility report not null", response);
    assertNotNull("param props not null", response.getDataReportSet());
    assertFalse("Param props not empty", response.getDataReportSet().isEmpty());
    assertEquals("Responsibility is equal", "Robert Bosch",
        response.getDataReportSet().iterator().next().getResponsibility());
    assertEquals("NumOfRvwdParams is equal", 1, response.getDataReportSet().iterator().next().getNumOfRvwdParams());
    assertEquals("getNumOfParams is equal", 62112, response.getDataReportSet().iterator().next().getNumOfParams());
    LOG.debug("data report size = {}", response.getDataReportSet().size());
  }

  /**
   * @throws ApicWebServiceException web service error
   */
  @Test
  // Negative test case
  public void test01Negative() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("PIDC A2L File with ID '" + Invalid_ID + "' not found");
    new ResponsibilityReportServiceClient().getResponsiblityDetails(Invalid_ID, VARIANT_ID_T01);
    fail("Expected exception is not thrown");
  }

  /**
   * Test CDR Report generation, with variant input
   *
   * @throws ApicWebServiceException error from service call
   */
  @Test
  public void test02() throws ApicWebServiceException {
    ResponsibiltyRvwDataReport response =
        new ResponsibilityReportServiceClient().getResponsiblityDetails(PIDC_A2L_ID_T02, VARIANT_ID_T02);

    // validation
    assertNotNull("Responsibility report not null", response);
    assertNotNull("param props not null", response.getDataReportSet());
    assertFalse("Param props not empty", response.getDataReportSet().isEmpty());
    assertEquals("Responsibility is equal", "Robert Bosch",
        response.getDataReportSet().iterator().next().getResponsibility());
    assertEquals("NumOfRvwdParams is equal", 0, response.getDataReportSet().iterator().next().getNumOfRvwdParams());
    assertEquals("getNumOfParams is equal", 59552, response.getDataReportSet().iterator().next().getNumOfParams());
    LOG.debug("data report size = {}", response.getDataReportSet().size());
  }

  /**
   * @throws ApicWebServiceException web service error
   */
  // Negative test case
  @Test
  public void test02Negative() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("PIDC A2L File with ID '" + Invalid_ID + "' not found");
    new ResponsibilityReportServiceClient().getResponsiblityDetails(Invalid_ID, VARIANT_ID_T01);
    fail("Expected exception is not thrown");
  }
}
