/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.ReviewVariantWrapper;
import com.bosch.caltool.icdm.model.cdr.RvwWpAndRespModel;
import com.bosch.caltool.icdm.model.cdr.review.ReviewInput;
import com.bosch.caltool.icdm.model.cdr.review.ReviewOutput;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author bne4cob
 */
public class ReviewServiceClientCancelledReviewTest extends AbstractReviewServiceClientTest {

  /**
   * PIDC Variant: AUDI->Diesel Engine->PC - Passenger Car->MD1-C->X_Test_HENZE_1788 (V1)->AT
   */
  private static final long VAR_ID = 1293613521L;
  /**
   * PIDC A2L mapping ID for A2L File: <br>
   * AUDI->Diesel Engine->PC - Passenger Car->MD1-C->X_Test_HENZE_1788
   * (V1)->MMD114A0CC1788->MMD114A0CC1788_MD00_withGroups.A2L
   */
  private static final long PIDC_A2L_ID = 1328585266L;
  /**
   * PIDC Version: BMW->Diesel Engine->PC - Passenger Car->MD1-C->test_for_juint_cdrreviews (v1)
   */
  private static final long PIDC_A2L_ID_5 = 2221593091L;

  /**
   * PIDC Variant: BMW->Diesel Engine->PC - Passenger Car->MD1-C->test_for_juint_cdrreviews (v1)->001B_ACC
   */
  private static final long VAR_ID_4 = 2221593089L;


  /**
   * Saving the cancelled review in first page
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCancelled001() throws ApicWebServiceException {

    ReviewInput data =
        createInput(null, CDRConstants.CDR_SOURCE_TYPE.NOT_DEFINED.getDbType(), null, PIDC_A2L_ID, VAR_ID);
    ReviewVariantWrapper result = new ReviewServiceClient().saveCancelledResult(data);
    assertNotNull("review result not null", result);
    LOG.debug("Result id - {}", result.getCdrReviewResult().getId());

  }

  /**
   * Saving the Cancelled review in second page + common rules and without review file
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCancelled002() throws ApicWebServiceException {

    ReviewInput data =
        createInput(null, CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType(), null, PIDC_A2L_ID, VAR_ID);
    data.setFilesToBeReviewed(true);
    ReviewVariantWrapper result = new ReviewServiceClient().saveCancelledResult(data);
    assertNotNull("review result not null", result);
    LOG.debug("Result id - {}", result.getCdrReviewResult().getId());
  }

  /**
   * Saving the Cancelled review in third page + common rules + wp + review file - cdfx
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCancelled003() throws ApicWebServiceException {

    ReviewInput data = createInput("testdata/cdr/acctl_demand_cdfx.cdfx",
        CDRConstants.CDR_SOURCE_TYPE.GROUP.getDbType(), "ACCtl_Demand", PIDC_A2L_ID, VAR_ID);
    data.setA2lGroupName("_RESP__0_0");
    ReviewVariantWrapper result = new ReviewServiceClient().saveCancelledResult(data);
    assertNotNull("review result not null", result);
    LOG.debug("Result id - {}", result.getCdrReviewResult().getId());
  }


  /**
   * Executing the cancelled review corresponding to testCancelled001
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCancelled004() throws ApicWebServiceException {
    // create a cancelled result
    ReviewInput data =
        createInput(null, CDRConstants.CDR_SOURCE_TYPE.NOT_DEFINED.getDbType(), null, PIDC_A2L_ID, VAR_ID);
    ReviewVariantWrapper result = new ReviewServiceClient().saveCancelledResult(data);
    assertNotNull("review result not null", result);
    LOG.debug("Result id - {}", result.getCdrReviewResult().getId());
    // execute the cancelled result
    ReviewInput data2 = createInput("testdata/cdr/compli.cdfx", CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType(),
        null, PIDC_A2L_ID, VAR_ID);
    data2.getResultData().setCanceledResultId(result.getCdrReviewResult().getId());
    data2.setRvwVariant(result.getRvwVariant());
    data2.setCdrReviewResult(result.getCdrReviewResult());
    data2.setFilesToBeReviewed(true);
    ReviewOutput reviewOutput = executeReview(data2);
    new CDRReviewResultServiceClient().deleteReviewResult(reviewOutput.getCdrResult(), false);

  }

  /**
   * Executing the cancelled review corresponding to testCancelled002
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCancelled005() throws ApicWebServiceException {
    // create a cancelled result
    ReviewInput data =
        createInput(null, CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType(), null, PIDC_A2L_ID, VAR_ID);
    data.setFilesToBeReviewed(true);
    ReviewVariantWrapper result = new ReviewServiceClient().saveCancelledResult(data);
    assertNotNull("review result not null", result);
    LOG.debug("Result id - {}", result.getCdrReviewResult().getId());
    // execute the cancelled result
    ReviewInput data2 = createInput("testdata/cdr/acctl_demand_cdfx.cdfx",
        CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType(), null, PIDC_A2L_ID, VAR_ID);
    data2.setFilesToBeReviewed(true);
    data2.getResultData().setCanceledResultId(result.getCdrReviewResult().getId());
    data2.setRvwVariant(result.getRvwVariant());
    data2.setCdrReviewResult(result.getCdrReviewResult());
    ReviewOutput reviewOutput = executeReview(data2);
    new CDRReviewResultServiceClient().deleteReviewResult(reviewOutput.getCdrResult(), false);

  }

  /**
   * Executing the cancelled review corresponding to testCancelled003
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCancelled006() throws ApicWebServiceException {

    String funcName = "BBKR, DKVS, MoFIA_Co";
    // create a cancelled result
    ReviewInput data = createInput("testdata/cdr/multiple_wp_cdfx.cdfx", CDRConstants.CDR_SOURCE_TYPE.WP.getDbType(),
        funcName, PIDC_A2L_ID_5, VAR_ID_4);
    setRvwInputData(data);

    ReviewVariantWrapper result = new ReviewServiceClient().saveCancelledResult(data);
    assertNotNull("review result not null", result);
    LOG.debug("Result id - {}", result.getCdrReviewResult().getId());

    // execute the cancelled result
    ReviewInput data2 = createInput("testdata/cdr/multiple_wp_cdfx.cdfx", CDRConstants.CDR_SOURCE_TYPE.WP.getDbType(),
        funcName, PIDC_A2L_ID_5, VAR_ID_4);
    setRvwInputData(data2);
    data2.getResultData().setCanceledResultId(result.getCdrReviewResult().getId());
    data2.setRvwVariant(result.getRvwVariant());
    data2.setCdrReviewResult(result.getCdrReviewResult());

    ReviewOutput reviewOutput = executeReview(data2);
    new CDRReviewResultServiceClient().deleteReviewResult(reviewOutput.getCdrResult(), false);

  }

  /**
   * Create Review input data object
   *
   * @param data ReviewInput
   */
  private void setRvwInputData(final ReviewInput data) {
    data.setReviewType("O");
    data.setRvwWpAndRespModelSet(populateRvwRespModelSet());
    data.setFunctionMap(populateWpParamMap());
  }

  /**
   * Fill the RvwAndWpRespModel Set
   *
   * @return Set<RvwAndRespModel>
   */
  private Set<RvwWpAndRespModel> populateRvwRespModelSet() {
    Set<RvwWpAndRespModel> wpAndRespModelSet = new HashSet<>();

    RvwWpAndRespModel rvwWpObj1 = new RvwWpAndRespModel();
    rvwWpObj1.setA2lRespId(2221593103L);
    rvwWpObj1.setA2lWpId(2221593102L);
    wpAndRespModelSet.add(rvwWpObj1);

    RvwWpAndRespModel rvwWpObj2 = new RvwWpAndRespModel();
    rvwWpObj2.setA2lRespId(2221593100L);
    rvwWpObj2.setA2lWpId(2221593098L);
    wpAndRespModelSet.add(rvwWpObj2);

    RvwWpAndRespModel rvwWpObj3 = new RvwWpAndRespModel();
    rvwWpObj3.setA2lRespId(2221593106L);
    rvwWpObj3.setA2lWpId(2221593102L);
    wpAndRespModelSet.add(rvwWpObj3);

    return wpAndRespModelSet;
  }

  /**
   * Populate Function and Parameter map
   *
   * @return Map<String - function name, Set<String> - Parameters>
   */
  private Map<String, Set<String>> populateWpParamMap() {
    Map<String, Set<String>> wpFuncParamMap = new HashMap<>();

    HashSet<String> paramset1 = new HashSet<String>();
    paramset1.add("ZKRKELDYN");
    paramset1.add("ZKRKENDYN");

    HashSet<String> paramset2 = new HashSet<String>();
    paramset2.add("ZKDFRM");
    paramset2.add("TLRASTMN");

    HashSet<String> paramset3 = new HashSet<String>();
    paramset3.add("ZYLANZ_UM");

    wpFuncParamMap.put("BBKR", paramset1);
    wpFuncParamMap.put("DKVS", paramset2);
    wpFuncParamMap.put("MoFIA_Co", paramset3);

    return wpFuncParamMap;
  }

  /**
   * Saving the cancelled review when ssd rule file path is set
   *
   * @throws ApicWebServiceException
   */
  @Test
  public void testCancelled007() throws ApicWebServiceException {
    ReviewInput data = createInput("testdata/cdr/acctl_demand_cdfx.cdfx",
        CDRConstants.CDR_SOURCE_TYPE.GROUP.getDbType(), "ACCtl_Demand", PIDC_A2L_ID, VAR_ID);
    data.setA2lGroupName("_RESP__0_0");
    data.getRulesData().setSsdRuleFilePath("testdata/ssd/MainReview__CommonRules__20190610_140020.ssd");
    ReviewVariantWrapper result = new ReviewServiceClient().saveCancelledResult(data);
    assertNotNull("Review result is null", result);
  }


  /**
   * Saving the cancelled review when function lab file path is set
   *
   * @throws ApicWebServiceException
   */
  @Test
  public void testCancelled008() throws ApicWebServiceException {
    ReviewInput data = createInput("testdata/cdr/acctl_demand_cdfx.cdfx",
        CDRConstants.CDR_SOURCE_TYPE.LAB_FILE.getDbType(), "ACCtl_Demand", PIDC_A2L_ID, VAR_ID);
    data.getFileData().setFunLabFilePath("testdata/lab/acctl_demand_lab.lab");
    ReviewVariantWrapper result = new ReviewServiceClient().saveCancelledResult(data);
    assertNotNull("Review result is null", result);
  }
}
