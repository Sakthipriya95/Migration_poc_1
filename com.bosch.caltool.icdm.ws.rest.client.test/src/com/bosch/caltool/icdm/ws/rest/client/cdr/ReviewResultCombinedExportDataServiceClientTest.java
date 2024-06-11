/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.CombinedReviewResultExcelExportData;
import com.bosch.caltool.icdm.model.cdr.CombinedRvwExportInputModel;
import com.bosch.caltool.icdm.model.cdr.QnaireResponseCombinedModel;
import com.bosch.caltool.icdm.model.cdr.ReviewResultEditorData;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespStatusData;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author say8cob
 */
public class ReviewResultCombinedExportDataServiceClientTest extends AbstractRestClientTest {

  private final static Long RESULT_ID_1 = 26140294978L;

  private final static Long RESULT_ID_2 = 18984883078L;

  /**
   * Review variant id for RESULT_ID_2
   */
  private static final long RVW_VARIANT_ID2 = 18984883170L;


  /**
   * Test ReviewResultExcelExportServiceClientTest
   *
   * @throws ApicWebServiceException error from service call
   */
  @Test
  public void test01() throws ApicWebServiceException {

    CombinedRvwExportInputModel exportInputModel = new CombinedRvwExportInputModel();
    exportInputModel.setOnlyReviewResult(false);
    exportInputModel.setOnlyRvwResAndQnaireLstBaseLine(true);
    exportInputModel.setOnlyRvwResAndQnaireWrkSet(false);
    exportInputModel.setRvwResultId(RESULT_ID_2);
    exportInputModel.setLoadEditorData(true);

    CombinedReviewResultExcelExportData combinedReviewAndQnaireExcelExport =
        new ReviewResultCombinedExportDataServiceClient().getCombinedReviewAndQnaireExcelExport(exportInputModel);
    ReviewResultEditorData editorData = combinedReviewAndQnaireExcelExport.getReviewResultEditorData();

    assertFalse("Response should not be null or empty",
        ((editorData.getParamMap() == null) || editorData.getParamMap().isEmpty()));

    CDRReviewResult reviewResult = editorData.getReviewResult();
    assertNotNull("Response should not be null", reviewResult);
    testCDRRvwResult(reviewResult);

    PidcVersion pidcVers = editorData.getPidcVers();
    assertNotNull("Response should not be null", pidcVers);
    testPidcVers(pidcVers);

    PidcA2l pidcA2l = editorData.getPidcA2l();
    assertNotNull("Response should not be null", pidcA2l);
    testPidcA2l(pidcA2l);

    Pidc pidc = editorData.getPidc();
    assertNotNull("Response should not be null", pidc);
    testPidc(pidc);

    RuleSet ruleSet = editorData.getRuleSet();
    assertNull("Response should be null", ruleSet);

    PidcVariant firstVariant = editorData.getFirstVariant();
    assertNotNull("Response should not be null", firstVariant);
    testVar(firstVariant);

    // test not null of ques resp list
    Set<QnaireRespStatusData> qnaireDataForRvwSet = editorData.getQnaireDataForRvwSet();
    assertNotNull("Response should not be null", qnaireDataForRvwSet);
    testQuesRespList(qnaireDataForRvwSet);

    Map<Long, Set<QnaireResponseCombinedModel>> qnaireRespCombinedModelMap =
        combinedReviewAndQnaireExcelExport.getQnaireRespCombinedModelMap();
    assertNotNull("Response should not be null", qnaireRespCombinedModelMap);
    testCombinedExportMap(qnaireRespCombinedModelMap);

  }

  /**
   * Test ReviewResultExcelExportServiceClientTest
   *
   * @throws ApicWebServiceException error from service call
   */
  @Test
  public void test02() throws ApicWebServiceException {

    CombinedRvwExportInputModel exportInputModel = new CombinedRvwExportInputModel();
    exportInputModel.setOnlyReviewResult(false);
    exportInputModel.setOnlyRvwResAndQnaireLstBaseLine(false);
    exportInputModel.setOnlyRvwResAndQnaireWrkSet(true);
    exportInputModel.setRvwResultId(RESULT_ID_1);
    exportInputModel.setLoadEditorData(true);

    CombinedReviewResultExcelExportData combinedReviewAndQnaireExcelExport =
        new ReviewResultCombinedExportDataServiceClient().getCombinedReviewAndQnaireExcelExport(exportInputModel);
    ReviewResultEditorData editorData = combinedReviewAndQnaireExcelExport.getReviewResultEditorData();

    assertFalse("Response should not be null or empty",
        ((editorData.getParamMap() == null) || editorData.getParamMap().isEmpty()));

    CDRReviewResult reviewResult = editorData.getReviewResult();
    assertNotNull("Response should not be null", reviewResult);

    PidcVersion pidcVers = editorData.getPidcVers();
    assertNotNull("Response should not be null", pidcVers);

    PidcA2l pidcA2l = editorData.getPidcA2l();
    assertNotNull("Response should not be null", pidcA2l);

    Pidc pidc = editorData.getPidc();
    assertNotNull("Response should not be null", pidc);

    RuleSet ruleSet = editorData.getRuleSet();
    assertNull("Response should be null", ruleSet);

    PidcVariant firstVariant = editorData.getFirstVariant();
    assertNotNull("Response should not be null", firstVariant);

    // test not null of ques resp list
    Set<QnaireRespStatusData> qnaireDataForRvwSet = editorData.getQnaireDataForRvwSet();
    assertNotNull("Response should not be null", qnaireDataForRvwSet);

    Map<Long, Set<QnaireResponseCombinedModel>> qnaireRespCombinedModelMap =
        combinedReviewAndQnaireExcelExport.getQnaireRespCombinedModelMap();
    assertNotNull("Response should not be null", qnaireRespCombinedModelMap);

  }

  /**
   * Test ReviewResultExcelExportServiceClientTest
   *
   * @throws ApicWebServiceException error from service call
   */
  @Test
  public void test03Negative() throws ApicWebServiceException {
    this.thrown.expectMessage(startsWith("Data Review Result with ID '0' not found"));
    CombinedRvwExportInputModel exportInputModel = new CombinedRvwExportInputModel();
    exportInputModel.setOnlyReviewResult(false);
    exportInputModel.setOnlyRvwResAndQnaireLstBaseLine(false);
    exportInputModel.setOnlyRvwResAndQnaireWrkSet(true);
    exportInputModel.setRvwResultId(0l);
    exportInputModel.setLoadEditorData(true);

    new ReviewResultCombinedExportDataServiceClient().getCombinedReviewAndQnaireExcelExport(exportInputModel);
  }

  /**
   * @param firstVariant
   */
  private void testVar(final PidcVariant firstVariant) {
    assertEquals("description is equal", "Test variant 04 for icdm dev", firstVariant.getDescription());
    assertEquals("Podc Version Id is equal", Long.valueOf(773515265), firstVariant.getPidcVersionId());
  }

  /**
   * @param pidc
   */
  private void testPidc(final Pidc pidc) {
    assertEquals("DescEng is equal", "Test PIDC for ICDM Dev Team (ICDM-852)", pidc.getDescEng());
    assertEquals("Name is equal", "X_Test_ICDM_RBEI_01", pidc.getName());
    assertEquals("Created User is equal", "BNE4COB", pidc.getCreatedUser());
  }

  /**
   * @param pidcA2l
   */
  private void testPidcA2l(final PidcA2l pidcA2l) {
    assertEquals("A2l File id is equal", Long.valueOf(308828283), pidcA2l.getA2lFileId());
    assertEquals("SDOMPverName is equal", "M1764VDBC866", pidcA2l.getSdomPverName());
  }

  /**
   * @param pidcVers
   */
  private void testPidcVers(final PidcVersion pidcVers) {
    assertEquals("Pidc version id is equal", Long.valueOf(773515265), pidcVers.getId());
    assertEquals("Pidc Id is equal", Long.valueOf(760420017), pidcVers.getPidcId());
    assertEquals("Vers_Desc_Eng is equal", "Version 1", pidcVers.getVersDescEng());
  }

  /**
   * @param reviewResult
   */
  private void testCDRRvwResult(final CDRReviewResult reviewResult) {
    assertEquals("Description is equal", "Trial 1 a -delta", reviewResult.getDescription());
    assertEquals("GrpWorkPackage is equal", "<FUNCTION>", reviewResult.getGrpWorkPkg());
    assertEquals("PidcA2lId is equal", Long.valueOf(774402428), reviewResult.getPidcA2lId());
  }

  /**
   * @param qnaireDataForRvwSet Set<QnaireRespStatusData>
   */
  private void testQuesRespList(final Set<QnaireRespStatusData> qnaireDataForRvwSet) {
    int size = 4;
    assertEquals("Size of the set is equal", size, qnaireDataForRvwSet.size());
  }

  /**
   * @param qnaireRespCombinedModelMap Map<Long, Set<QnaireResponseCombinedModel>>
   */
  private void testCombinedExportMap(final Map<Long, Set<QnaireResponseCombinedModel>> qnaireRespCombinedModelMap) {
    int size = 1;
    assertEquals("Size of the set is equal", size, qnaireRespCombinedModelMap.size());
  }

}
