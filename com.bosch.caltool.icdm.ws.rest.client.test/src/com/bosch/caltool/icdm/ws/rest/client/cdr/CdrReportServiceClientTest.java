/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

import com.bosch.caltool.icdm.model.a2l.ParamProperties;
import com.bosch.caltool.icdm.model.cdr.CDRReportModel;
import com.bosch.caltool.icdm.model.cdr.CdrReport;
import com.bosch.caltool.icdm.model.cdr.ParameterReviewDetails;
import com.bosch.caltool.icdm.model.cdr.ReviewDetails;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class CdrReportServiceClientTest extends AbstractRestClientTest {

  /**
   *
   */
  private static final String CONST_ASTERIX_LINE = "*********************************************";
  /**
   *
   */
  private static final String MSG_REPORT_DATA_NOT_NULL = "report data not null";
  // ---------------- Variant level review fetch ----------------
  // A2L File: Honda->Gasoline Engine->PC - Passenger Car->ME(D)17->HONDA XE1B 2KR (Version
  // 1)->HON1793A1->HON1793A1_V75S41_internal_withGroups_V01.a2l
  // icdm:a2lid,773637415-911435001
  private static final Long PIDC_A2L_ID_T01 = 773637415L;
  private static final Long VARIANT_ID_T01 = 768673297L;// 2KR-EU6B

  // ---------------- Project level review fetch(project without variant)
  // A2L File: Alfa Romeo->Gasoline Engine->PC - Passenger Car->ME(D)17->Alfa 1.8L, FamB, Gen2, US (Version
  // 1)->D173307->009-Alfa_FamB_Gen3_1.8L_TFSI_MED17.3.3_D17330714A0_or.A2L
  // icdm:a2lid,792190866-1631755001
  private static final Long PIDC_A2L_ID_T02 = 792190866L;

  // icdm:a2lid,1440309729-2226037903
  private static final Long PIDC_A2L_ID_T03 = 1440309729L;

  private static final Long PIDC_A2L_ID_T03_RESP_ID = 1684389460L;

  private static final Long PIDC_A2L_ID_T03_WP_ID = 2147754269L;// Braking system

  // icdm:a2lid,27591650840-2226037903
  private static final Long PIDC_A2L_ID_T04 = 27591650840L;

  private static final Long PIDC_A2L_ID_T04_RESP_ID = 27595983877L;// Test, BOsch user

  private static final Long PIDC_A2L_ID_T04_WP_ID = 27595983759L;// 106) Thermomangement

  private static final int MAX_REVIEWS = 5;

  private static final Long PIDC_A2L_ID_T05 = 29650814522L;

  /**
   * Test CDR Report generation, with variant input
   *
   * @throws ApicWebServiceException error from service call
   */
  @Test
  public void test01() throws ApicWebServiceException {

    CdrReport report =
        new CdrReportServiceClient().getCdrReport(PIDC_A2L_ID_T01, VARIANT_ID_T01, MAX_REVIEWS, false, null, null);
    assertNotNull(MSG_REPORT_DATA_NOT_NULL, report);
    testOutput(report);
    logOutput(report);
  }

  /**
   * Test CDR Report generation, when there are no variants
   *
   * @throws ApicWebServiceException error from service call
   */
  @Test
  public void test02() throws ApicWebServiceException {

    CdrReport report = new CdrReportServiceClient().getCdrReport(PIDC_A2L_ID_T02, null, MAX_REVIEWS, true, null, null);
    assertNotNull(MSG_REPORT_DATA_NOT_NULL, report);
    testOutput(report);
    logOutput(report);
  }

  /**
   * Test new CDR Report generation, when there are no variants
   *
   * @throws ApicWebServiceException error from service call
   */
  @Test
  public void test03() throws ApicWebServiceException {

    CDRReportModel report =
        new CdrReportServiceClient().getCdrReportModel(PIDC_A2L_ID_T05, null, MAX_REVIEWS, false, null, null);
    assertNotNull(MSG_REPORT_DATA_NOT_NULL, report);
    logOutput(report);
  }

  /**
   * @param report
   */
  private void logOutput(final CDRReportModel report) {
    LOG.info("Number of CDR Report Parameters = " + report.getCdrReportParams().size());

  }

  /**
   * Test CDR Report generation with WP id , resp id
   *
   * @throws ApicWebServiceException error from service call
   */
  @Test
  public void testGetCdrReportWithWPResp() throws ApicWebServiceException {

    CdrReport report = new CdrReportServiceClient().getCdrReport(PIDC_A2L_ID_T03, 1027853969L, MAX_REVIEWS, true,
        PIDC_A2L_ID_T03_RESP_ID, PIDC_A2L_ID_T03_WP_ID);
    assertNotNull(MSG_REPORT_DATA_NOT_NULL, report);
    testOutput(report);
    logOutput(report);
  }

  /**
   * Test CDR Report generation with resp id
   *
   * @throws ApicWebServiceException error from service call
   */
  @Test
  public void testGetCdrReportWithResp() throws ApicWebServiceException {

    CdrReport report = new CdrReportServiceClient().getCdrReport(PIDC_A2L_ID_T03, 1027853969L, MAX_REVIEWS, true,
        PIDC_A2L_ID_T03_RESP_ID, null);
    assertNotNull(MSG_REPORT_DATA_NOT_NULL, report);
    testOutput(report);
    logOutput(report);
  }

  /**
   * Test CDR Report generation, when there are no variants and with WP id , resp id
   *
   * @throws ApicWebServiceException error from service call
   */
  @Test
  public void testGetCdrReportWithWPRespForNoVariant() throws ApicWebServiceException {

    CdrReport report = new CdrReportServiceClient().getCdrReport(PIDC_A2L_ID_T04, null, MAX_REVIEWS, true,
        PIDC_A2L_ID_T04_RESP_ID, PIDC_A2L_ID_T04_WP_ID);
    assertNotNull(MSG_REPORT_DATA_NOT_NULL, report);
    testOutput(report);
    logOutput(report);
  }


  /**
   * Test output
   *
   * @param reportData output object
   */
  private void testOutput(final CdrReport reportData) {


    assertNotNull("param props not null", reportData.getParamPropsMap());
    assertFalse("Param props not empty", reportData.getParamPropsMap().isEmpty());

    assertNotNull("param review details not null", reportData.getParamRvwDetMap());
    assertFalse("Param review details not empty", reportData.getParamPropsMap().isEmpty());

    assertNotNull("review details not null", reportData.getReviewDetMap());
    assertFalse("review details not empty", reportData.getParamPropsMap().isEmpty());
  }

  /**
   * Log output
   *
   * @param reportData output object
   */
  private void logOutput(final CdrReport reportData) {

    LOG.info("Parameters with reviews = " + reportData.getParamRvwDetMap().size());
    LOG.info("Parameters with properties = " + reportData.getParamPropsMap().size());
    LOG.info("Total reviews = " + reportData.getReviewDetMap().size());

    SortedSet<String> paramSet = new TreeSet<>(reportData.getParamRvwDetMap().keySet());
    paramSet.addAll(reportData.getParamPropsMap().keySet());

    String param = paramSet.first();
    LOG.info(CONST_ASTERIX_LINE);
    LOG.info("Parameter : " + param);
    LOG.info(CONST_ASTERIX_LINE);
    ParamProperties paramProps = reportData.getParamPropsMap().get(param);
    if (paramProps != null) {
      LOG.info("   " + paramProps.toString());
    }

    List<ParameterReviewDetails> prmRvwDetList = reportData.getParamRvwDetMap().get(param);

    if (prmRvwDetList != null) {
      LOG.info("  Parameter Review details - Review Count=" + prmRvwDetList.size());
      for (ParameterReviewDetails prmRvwDet : prmRvwDetList) {
        LOG.info("    " + prmRvwDet.toString());
      }
    }

    if (!reportData.getReviewDetMap().isEmpty()) {
      Long reviewID = reportData.getReviewDetMap().keySet().iterator().next();
      LOG.info(CONST_ASTERIX_LINE);
      LOG.info("Review : " + reviewID);
      LOG.info(CONST_ASTERIX_LINE);
      ReviewDetails reviewDetail = reportData.getReviewDetMap().get(reviewID);
      LOG.info("  " + reviewDetail.toString());
      LOG.info(CONST_ASTERIX_LINE);
    }
  }
}
