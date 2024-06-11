/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.model.cdr.ReviewResultEditorData;
import com.bosch.caltool.icdm.model.cdr.review.ReviewInput;
import com.bosch.caltool.icdm.model.cdr.review.ReviewOutput;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Tests reviews using different types of rules.
 * <p>
 * Test details : See testdata\cdr\RuleTypesTests.xlsx
 *
 * @author BNE4COB
 */
public class ReviewServiceClientRuleTypesTest extends AbstractReviewServiceClientTest {

  private static final String EXATCH_MATCH_YES = ApicConstants.EXACT_MATCH.YES.dbType;
  private static final String EXATCH_MATCH_NO = ApicConstants.EXACT_MATCH.NO.dbType;

  private static final String RESULT_OK = CDRConstants.RESULT_FLAG.OK.getDbType();
  private static final String RESULT_NOT_OK = CDRConstants.RESULT_FLAG.NOT_OK.getDbType();
  private static final String RESULT_LOW = CDRConstants.RESULT_FLAG.LOW.getDbType();
  private static final String RESULT_HIGH = CDRConstants.RESULT_FLAG.HIGH.getDbType();
  private static final String RESULT_NOT_REVIEWED = null;// For 'not reviewed', db value is null

  private static final String SCORE_0 = DATA_REVIEW_SCORE.S_0.getDbType();
  private static final String SCORE_8 = DATA_REVIEW_SCORE.S_8.getDbType();

  private static final String FILE_REVIEW1 = "testdata/cdr/RuleTypes_File1.cdfx";
  private static final String FILE_REVIEW2 = "testdata/cdr/RuleTypes_File2.cdfx";
  private static final String FILE_REVIEW3 = "testdata/cdr/RuleTypes_File3.cdfx";

  /**
   * A2L File: X_Testcustomer->Diesel Engine->PC - Passenger Car->BCU Gen 1->X_Test_ICDM_RBEI_01 (Version
   * 1)->M1764VDAC866->DA861R4000000.A2L
   * <p>
   * Link : icdm:a2lid,774402434-680600001
   */
  private static final Long PIDC_A2L_ID_1 = 774402434L;

  /**
   * Variant : X_Test_IR01_V01<br>
   * Link : icdm:pidvarid,773515265-760420031
   */
  private static final Long VAR_ID_1 = 760420031L;

  private static final String ASSERT_DESC_SCORE = "Check parameter score";

  private static final String ASSERT_DESC_PARAM_RESULT = "Check parameter review result";

  private static ReviewResultEditorData result1;
  private static Map<String, CDRResultParameter> rvw1ParamMap = new HashMap<>();

  private static ReviewResultEditorData result2;
  private static Map<String, CDRResultParameter> rvw2ParamMap = new HashMap<>();

  private static ReviewResultEditorData result3;
  private static Map<String, CDRResultParameter> rvw3ParamMap = new HashMap<>();

  /**
   * Prepare reviews and load review results for the tests
   *
   * @throws ApicWebServiceException error from service
   */
  @BeforeClass
  public static void prepareReviews() throws ApicWebServiceException {

    AbstractRestClientTest superObj = new AbstractRestClientTest() {
      // No implemenation required here
    };
    superObj.setAttributes();

    result1 = doPrepareReview("RuleTypes tests File 1", FILE_REVIEW1, PIDC_A2L_ID_1, VAR_ID_1);
    loadReviewParams(result1, rvw1ParamMap);

    result2 = doPrepareReview("RuleTypes tests File 2", FILE_REVIEW2, PIDC_A2L_ID_1, VAR_ID_1);
    loadReviewParams(result2, rvw2ParamMap);

    result3 = doPrepareReview("RuleTypes tests File 3", FILE_REVIEW3, PIDC_A2L_ID_1, VAR_ID_1);
    loadReviewParams(result3, rvw3ParamMap);
  }


  /**
   * @param rvwFileMatch2
   * @param rvwFileMatchParamMap2
   */
  private static void loadReviewParams(final ReviewResultEditorData resultData,
      final Map<String, CDRResultParameter> rvwParamMapToLoad) {

    resultData.getParamMap().values().forEach(rp -> rvwParamMapToLoad.put(rp.getName(), rp));
  }


  /**
   * Test for RuleTypes_1.1
   */
//TODO test is failing
  public void testRuleTypes01T01() {
    String testParam = "Com_swtACCTyp_C";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_OK, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res1Param.getReviewScore());

    CDRResultParameter res2Param = getReviewParam(rvw2ParamMap, testParam, result2.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_LOW, res2Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res2Param.getReviewScore());

    CDRResultParameter res3Param = getReviewParam(rvw3ParamMap, testParam, result3.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_HIGH, res3Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res3Param.getReviewScore());
  }


  /**
   * Test for RuleTypes_1.2
   */
  @Test
  public void testRuleTypes01T02() {
    String testParam = "Com_daACCDes_C";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_OK, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_8, res1Param.getReviewScore());

    CDRResultParameter res2Param = getReviewParam(rvw2ParamMap, testParam, result2.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_LOW, res2Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res2Param.getReviewScore());

    CDRResultParameter res3Param = getReviewParam(rvw3ParamMap, testParam, result3.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_HIGH, res3Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res3Param.getReviewScore());
  }

  /**
   * Test for RuleTypes_1.3
   */
//TODO test is failing
  public void testRuleTypes01T03() {
    String testParam = "DFC_DisblMsk2.DFC_AirCClntPSig_C";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_OK, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res1Param.getReviewScore());

    CDRResultParameter res2Param = getReviewParam(rvw2ParamMap, testParam, result2.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_LOW, res2Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res2Param.getReviewScore());

    CDRResultParameter res3Param = getReviewParam(rvw3ParamMap, testParam, result3.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_HIGH, res3Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res3Param.getReviewScore());
  }

  /**
   * Test for RuleTypes_1.4
   */
//TODO test is failing
  public void testRuleTypes01T04() {
    String testParam = "Com_swtACCTyp_C";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_OK, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res1Param.getReviewScore());

    CDRResultParameter res2Param = getReviewParam(rvw2ParamMap, testParam, result2.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_LOW, res2Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res2Param.getReviewScore());

    CDRResultParameter res3Param = getReviewParam(rvw3ParamMap, testParam, result3.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_HIGH, res3Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res3Param.getReviewScore());
  }

  /**
   * Test for RuleTypes_1.5
   */
//TODO test is failing
  public void testRuleTypes01T05() {
    String testParam = "Com_swtACCTyp_C";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_OK, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res1Param.getReviewScore());

    CDRResultParameter res2Param = getReviewParam(rvw2ParamMap, testParam, result2.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_LOW, res2Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res2Param.getReviewScore());

    CDRResultParameter res3Param = getReviewParam(rvw3ParamMap, testParam, result3.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_HIGH, res3Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res3Param.getReviewScore());
  }

  /**
   * Test for RuleTypes_1.6
   */
  @Test
  public void testRuleTypes01T06() {
    String testParam = "DFC_CtlMsk2.DFC_GlwPlgPLUGSC_5_C";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_OK, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res1Param.getReviewScore());

    CDRResultParameter res2Param = getReviewParam(rvw2ParamMap, testParam, result2.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_HIGH, res2Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res2Param.getReviewScore());


  }

  /**
   * Test for RuleTypes_2.1
   */
  @Test
  public void testRuleTypes02T01() {
    String testParam = "AccMon_vDevThres_CUR";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_OK, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res1Param.getReviewScore());

    CDRResultParameter res2Param = getReviewParam(rvw2ParamMap, testParam, result2.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_NOT_OK, res2Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res2Param.getReviewScore());
  }

  /**
   * Test for RuleTypes_2.2
   */
  @Test
  public void testRuleTypes02T02() {
    String testParam = "ACComp_tiTrqDyn_CUR";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_OK, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_8, res1Param.getReviewScore());

    CDRResultParameter res2Param = getReviewParam(rvw2ParamMap, testParam, result2.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_NOT_OK, res2Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res2Param.getReviewScore());


  }

  /**
   * Test for RuleTypes_2.3
   */
  @Test
  public void testRuleTypes02T03() {
    String testParam = "AccMon_nPtd_CUR";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_OK, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res1Param.getReviewScore());

    CDRResultParameter res2Param = getReviewParam(rvw2ParamMap, testParam, result2.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_NOT_OK, res2Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res2Param.getReviewScore());
  }

  /**
   * Test for RuleTypes_2.4
   */
  @Test
  public void testRuleTypes02T04() {
    String testParam = "Air_tTransfTAFSADC_CUR";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_OK, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_8, res1Param.getReviewScore());

    CDRResultParameter res2Param = getReviewParam(rvw2ParamMap, testParam, result2.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_NOT_OK, res2Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res2Param.getReviewScore());
  }

  /**
   * Test for RuleTypes_2.5
   */
  @Test
  public void testRuleTypes02T05() {
    String testParam = "Air_tDiffMeanNgvPos0_CUR";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_OK, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res1Param.getReviewScore());

    CDRResultParameter res2Param = getReviewParam(rvw2ParamMap, testParam, result2.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_NOT_OK, res2Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res2Param.getReviewScore());
  }

  /**
   * Test for RuleTypes_2.6
   */
  @Test
  public void testRuleTypes02T06() {
    String testParam = "Air_tDiffMeanNgvPos1_CUR";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_OK, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res1Param.getReviewScore());

    CDRResultParameter res2Param = getReviewParam(rvw2ParamMap, testParam, result2.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_NOT_OK, res2Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res2Param.getReviewScore());
  }

  /**
   * Test for RuleTypes_3.1
   */
  @Test
  public void testRuleTypes03T01() {
    String testParam = "DFC_CtlMsk2.DFC_GlwPlgPLUGErr_5_C";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_NOT_REVIEWED, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res1Param.getReviewScore());
  }

  /**
   * Test for RuleTypes_3.2
   */
  @Test
  public void testRuleTypes03T02() {
    String testParam = "DFC_CtlMsk2.DFC_GlwPlgPLUGSC_0_C";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_NOT_REVIEWED, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res1Param.getReviewScore());
  }

  /**
   * Test for RuleTypes_3.3
   */
  @Test
  public void testRuleTypes03T03() {
    String testParam = "DFC_CtlMsk2.DFC_GlwPlgPLUGSC_1_C";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_NOT_REVIEWED, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res1Param.getReviewScore());
  }

  /**
   * Test for RuleTypes_3.4
   */
  @Test
  public void testRuleTypes03T04() {
    String testParam = "DFC_CtlMsk2.DFC_GlwPlgPLUGSC_2_C";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_NOT_REVIEWED, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res1Param.getReviewScore());
  }

  /**
   * Test for RuleTypes_3.5
   */
  @Test
  public void testRuleTypes03T05() {
    String testParam = "DFC_CtlMsk2.DFC_GlwPlgPLUGSC_3_C";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_NOT_REVIEWED, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res1Param.getReviewScore());
  }

  /**
   * Test for RuleTypes_4.1
   */
  @Test
  public void testRuleTypes04T01() {
    String testParam = "ACCmpr_rTrqDfl_C";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_OK, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_8, res1Param.getReviewScore());

    CDRResultParameter res2Param = getReviewParam(rvw2ParamMap, testParam, result2.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_LOW, res2Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res2Param.getReviewScore());

    CDRResultParameter res3Param = getReviewParam(rvw3ParamMap, testParam, result3.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_HIGH, res3Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res3Param.getReviewScore());
  }

  /**
   * Test for RuleTypes_4.2
   */
//TODO test is failing
  public void testRuleTypes04T02() {
    String testParam = "AirC_DigOutCmpr.numTstMax_C";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_OK, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_8, res1Param.getReviewScore());

    CDRResultParameter res2Param = getReviewParam(rvw2ParamMap, testParam, result2.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_LOW, res2Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res2Param.getReviewScore());

    CDRResultParameter res3Param = getReviewParam(rvw3ParamMap, testParam, result3.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_HIGH, res3Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res3Param.getReviewScore());
  }

  /**
   * Test for RuleTypes_4.3
   */
  @Test
  public void testRuleTypes04T03() {
    String testParam = "AirC_DigOutCmpr.tiBtwTstOT_C";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_NOT_REVIEWED, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res1Param.getReviewScore());
  }

  /**
   * Test for RuleTypes_4.4
   */
//TODO test is failing
  public void testRuleTypes04T04() {
    String testParam = "Com_swtACCTyp_C";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_OK, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res1Param.getReviewScore());

    CDRResultParameter res2Param = getReviewParam(rvw2ParamMap, testParam, result2.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_LOW, res2Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res2Param.getReviewScore());

    CDRResultParameter res3Param = getReviewParam(rvw3ParamMap, testParam, result3.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_HIGH, res3Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res3Param.getReviewScore());
  }

  /**
   * Test for RuleTypes_5.1
   */
  @Test
  public void testRuleTypes05T01() {
    String testParam = "ASMod_dmSotBasLM2_MAP";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_OK, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res1Param.getReviewScore());

    CDRResultParameter res2Param = getReviewParam(rvw2ParamMap, testParam, result2.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_NOT_OK, res2Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res2Param.getReviewScore());
  }

  /**
   * Test for RuleTypes_5.2
   */
//TODO test is failing
  public void testRuleTypes05T02() {
    String testParam = "Com_swtACCTyp_C";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_OK, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res1Param.getReviewScore());

    CDRResultParameter res2Param = getReviewParam(rvw2ParamMap, testParam, result2.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_LOW, res2Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res2Param.getReviewScore());

    CDRResultParameter res3Param = getReviewParam(rvw3ParamMap, testParam, result3.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_HIGH, res3Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res3Param.getReviewScore());
  }

  /**
   * Test for RuleTypes_5.3
   */
  @Test
  public void testRuleTypes05T03() {
    String testParam = "BasSvrAppl_EepHwPartNum_C";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_OK, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res1Param.getReviewScore());

    CDRResultParameter res2Param = getReviewParam(rvw2ParamMap, testParam, result2.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_NOT_OK, res2Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res2Param.getReviewScore());
  }

  /**
   * Test for RuleTypes_5.4
   */
  @Test
  public void testRuleTypes05T04() {
    String testParam = "ETCtl_facKdNegOutr_CA";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_OK, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_8, res1Param.getReviewScore());

    CDRResultParameter res2Param = getReviewParam(rvw2ParamMap, testParam, result2.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_NOT_OK, res2Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res2Param.getReviewScore());
  }

  /**
   * Test for RuleTypes_6.1
   */
  @Test
  public void testRuleTypes06T01() {
    String testParam = "ACComp_CalcDem_CW";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_OK, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res1Param.getReviewScore());

    CDRResultParameter res2Param = getReviewParam(rvw2ParamMap, testParam, result2.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_LOW, res2Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res2Param.getReviewScore());
  }

  /**
   * Test for RuleTypes_7.1
   */
//TODO test to be created
  public void testRuleTypes07T01() {
    fail("Not yet implemented");
  }

  /**
   * Test for RuleTypes_7.2
   */
//TODO test to be created
  public void testRuleTypes07T02() {
    fail("Not yet implemented");
  }

  /**
   * Test for RuleTypes_7.3
   */
//TODO test to be created
  public void testRuleTypes07T03() {
    fail("Not yet implemented");
  }

  /**
   * Test for RuleTypes_8.1
   */
  @Test
  public void testRuleTypes08T01() {
    String testParam = "AccMon_trqMinLos_C";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_OK, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res1Param.getReviewScore());
    testRuleDetails(res1Param, true, true, EXATCH_MATCH_NO);

    CDRResultParameter res2Param = getReviewParam(rvw2ParamMap, testParam, result2.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_LOW, res2Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res2Param.getReviewScore());
    testRuleDetails(res1Param, true, true, EXATCH_MATCH_NO);

    CDRResultParameter res3Param = getReviewParam(rvw3ParamMap, testParam, result3.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_HIGH, res3Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res3Param.getReviewScore());
    testRuleDetails(res1Param, true, true, EXATCH_MATCH_NO);
  }


  /**
   * Test for RuleTypes_8.2
   */
  @Test
  public void testRuleTypes08T02() {
    String testParam = "AirC_TransStgClntP.stSensId_C";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_OK, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res1Param.getReviewScore());
    testRuleDetails(res1Param, false, true, EXATCH_MATCH_NO);

    CDRResultParameter res2Param = getReviewParam(rvw2ParamMap, testParam, result2.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_LOW, res2Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res2Param.getReviewScore());
    testRuleDetails(res1Param, false, true, EXATCH_MATCH_NO);

    CDRResultParameter res3Param = getReviewParam(rvw3ParamMap, testParam, result3.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_HIGH, res3Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res3Param.getReviewScore());
    testRuleDetails(res1Param, false, true, EXATCH_MATCH_NO);
  }

  /**
   * Test for RuleTypes_8.3
   */
  @Test
  public void testRuleTypes08T03() {
    String testParam = "AirC_RmpSlpClntP.Pos_C";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_NOT_REVIEWED, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res1Param.getReviewScore());
    testRuleDetails(res1Param, false, false, EXATCH_MATCH_NO);

    CDRResultParameter res2Param = getReviewParam(rvw2ParamMap, testParam, result2.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_NOT_REVIEWED, res2Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res2Param.getReviewScore());
    testRuleDetails(res1Param, false, false, EXATCH_MATCH_NO);

    CDRResultParameter res3Param = getReviewParam(rvw3ParamMap, testParam, result3.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_NOT_REVIEWED, res3Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res3Param.getReviewScore());
    testRuleDetails(res1Param, false, false, EXATCH_MATCH_NO);
  }

  /**
   * Test for RuleTypes_8.4
   */
  @Test
  public void testRuleTypes08T04() {
    String testParam = "AirC_pClntIni_C";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_OK, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res1Param.getReviewScore());
    testRuleDetails(res1Param, true, false, EXATCH_MATCH_YES);

    CDRResultParameter res2Param = getReviewParam(rvw2ParamMap, testParam, result2.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_LOW, res2Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res2Param.getReviewScore());
    testRuleDetails(res1Param, true, false, EXATCH_MATCH_YES);

    CDRResultParameter res3Param = getReviewParam(rvw3ParamMap, testParam, result3.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_HIGH, res3Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res3Param.getReviewScore());
    testRuleDetails(res1Param, true, false, EXATCH_MATCH_YES);
  }

  /**
   * Test for RuleTypes_8.5
   */
  @Test
  public void testRuleTypes08T05() {
    String testParam = "AirC_TransStgClntP.Dfl_C";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_NOT_REVIEWED, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res1Param.getReviewScore());
    testRuleDetails(res1Param, true, false, EXATCH_MATCH_NO);

    CDRResultParameter res2Param = getReviewParam(rvw2ParamMap, testParam, result2.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_NOT_REVIEWED, res2Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res2Param.getReviewScore());
    testRuleDetails(res1Param, true, false, EXATCH_MATCH_NO);

    CDRResultParameter res3Param = getReviewParam(rvw3ParamMap, testParam, result3.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_NOT_REVIEWED, res3Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res3Param.getReviewScore());
    testRuleDetails(res1Param, true, false, EXATCH_MATCH_NO);
  }

  /**
   * Test for RuleTypes_9.1
   */
  @Test
  public void testRuleTypes09T01() {
    String testParam = "DIUMPR_Check_CA";

    CDRResultParameter res1Param = getReviewParam(rvw1ParamMap, testParam, result1.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_OK, res1Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res1Param.getReviewScore());

    CDRResultParameter res2Param = getReviewParam(rvw2ParamMap, testParam, result2.getReviewResult().getName());
    assertEquals(ASSERT_DESC_PARAM_RESULT, RESULT_NOT_OK, res2Param.getResult());
    assertEquals(ASSERT_DESC_SCORE, SCORE_0, res2Param.getReviewScore());
  }

  /**
   * @param cdrResultParam
   * @param validLimits
   * @param validRefValue
   * @param exactMatch
   */
  private void testRuleDetails(final CDRResultParameter cdrResultParam, final boolean validRefValue,
      final boolean validLimits, final String exactMatch) {

    testRefValue(cdrResultParam.getRefValue(), validRefValue);
    testLimits(cdrResultParam, validLimits);
    testExactMatchFlag(cdrResultParam.getMatchRefFlag(), exactMatch);
  }


  private void testExactMatchFlag(final String exactMatchFlag, final String exactMatch) {
    if (CommonUtils.isEqual(exactMatch, EXATCH_MATCH_YES)) {
      assertEquals("Exact Match should be 'Yes'", EXATCH_MATCH_YES, exactMatchFlag);
    }
    else if (CommonUtils.isEqual(exactMatch, EXATCH_MATCH_NO)) {
      assertEquals("Exact Match should be 'No'", EXATCH_MATCH_NO, exactMatchFlag);
    }
    else {
      assertNull("Exatch Match To Ref Value Flag should be null", exactMatchFlag);
    }
  }


  private void testLimits(final CDRResultParameter cdrResultParam, final boolean validLimits) {
    if (validLimits) {
      assertNotNull("Lower Limit should not be null", cdrResultParam.getLowerLimit());
      assertNotNull("Upper Limit should not be null", cdrResultParam.getUpperLimit());
    }
    else {
      assertNull("Lower Limit should be null", cdrResultParam.getLowerLimit());
      assertNull("Upper Limit should be null", cdrResultParam.getUpperLimit());
    }
  }


  private void testRefValue(final byte[] refValue, final boolean validRefValue) {
    if (validRefValue) {
      assertNotNull("Ref Value should be not null", refValue);
    }
    else {
      assertNull("Ref Value should be null", refValue);
    }
  }

  /**
   * @return
   * @throws ApicWebServiceException
   */
  private static ReviewResultEditorData doPrepareReview(final String rvwName, final String reviewFile,
      final Long pidcA2lId, final Long varID)
      throws ApicWebServiceException {

    String sourceType = CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType();
    ReviewInput data = sCreateInput(rvwName, reviewFile, sourceType, "", pidcA2lId, varID);
    data.setFilesToBeReviewed(true);

    ReviewOutput reviewSummary = new ReviewServiceClient().performReview(data);

    ReviewResultEditorData resultData =
        new CDRReviewResultServiceClient().getRvwResultEditorData(reviewSummary.getCdrResult().getId(), null);

    validateGeneralReviewInfo(data, reviewSummary, resultData);

    return resultData;
  }

  private static void validateGeneralReviewInfo(final ReviewInput reviewInput, final ReviewOutput reviewSummary,
      final ReviewResultEditorData resultData) {

    checkReviewSummary(reviewSummary);

    validateReviewResults(reviewInput, resultData);
    validateRvwPartcipant(reviewInput, resultData);
  }


  private CDRResultParameter getReviewParam(final Map<String, CDRResultParameter> rvwParamMap, final String paramName,
      final String resultName) {

    CDRResultParameter rvwParam = rvwParamMap.get(paramName);

    assertNotNull(paramName + " available in " + resultName, rvwParam);

    logParamInfo(result1.getReviewResult().getName(), rvwParam);

    return rvwParam;
  }

  private void logParamInfo(final String resultName, final CDRResultParameter rvwParam) {
    LOG.info("CDR result : {}, Checking parameter {} : Result = {}, Score = {}", resultName, rvwParam.getName(),
        rvwParam.getResult(), rvwParam.getReviewScore());
  }

}
