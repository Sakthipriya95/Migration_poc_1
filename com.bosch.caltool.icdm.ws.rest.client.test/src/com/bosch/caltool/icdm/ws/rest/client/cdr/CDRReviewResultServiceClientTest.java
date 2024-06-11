/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lWPRespModel;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcReviewDetails;
import com.bosch.caltool.icdm.model.apic.pidc.PidcTreeRvwVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.CDRWizardUIModel;
import com.bosch.caltool.icdm.model.cdr.CopyResultToVarData;
import com.bosch.caltool.icdm.model.cdr.ReviewResultDeleteValidation;
import com.bosch.caltool.icdm.model.cdr.ReviewResultEditorData;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.TreeViewSelectnRespWP;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespStatusData;
import com.bosch.caltool.icdm.model.cdr.review.ReviewInput;
import com.bosch.caltool.icdm.model.cdr.review.ReviewOutput;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author say8cob
 */
public class CDRReviewResultServiceClientTest extends AbstractReviewServiceClientTest {

  /**
   *
   */
  private static final String RESPONSE_SHOULD_NOT_BE_NULL = "Response should not be null";


  /**
   * Review Result --icdm:cdrid,28708507478-28590765231-28654274614 A2L WORKPACKAGE--WP_Finished_Tree_View PIDC A2l
   * link-- icdm:a2lid,28654274616-1685598777 variant--1.Variant
   */
  private static final long PIDC_VARIANT_WP_FINISH_TREE_VIEW = 28654274614L;


  /**
   * Review Result --icdm:cdrid,28708507478-28590765231-28654274614 A2L WORKPACKAGE--WP_Finished_Tree_View PIDC A2l
   * link-- icdm:a2lid,28654274616-1685598777
   */
  private static final long PIDC_A2L_ID_WP_FINISH_TREE_VIEW = 28654274616L;


  /**
   * Review Result --icdm:cdrid,28708507478-28590765231-28654274614 A2L WORKPACKAGE--WP_Finished_Tree_View
   */
  private static final long A2L_WP_RESPONSIBILITY_ID = 28694220977L;


  /**
   * Review Result --icdm:cdrid,28708507478-28590765231-28654274614 A2L WORKPACKAGE--WP_Finished_Tree_View
   */
  private static final long A2L_WORKPACKAGE_ID = 28590765245L;


  /**
   * Review Result --icdm:cdrid,28708507478-28590765231-28654274614 A2L RESPONSIBILITY--Abe Ricardo
   */
  private static final long A2L_RESPONSIBILITY_ID = 28694219257L;


  /**
   * Review variant id for RESULT_ID_2
   */
  private static final long RVW_VARIANT_ID2 = 18984883170L;


  /**
   * Review Result ID used in CDFX Delivery
   */
  private static final long RVW_USED_IN_CDFX_DELIVERY = 11106183028L;


  private static final long PIDC_VERS_ID_ACTIVE = 773510915L;// X_Test_002_P866_EA288 : Version 4


  /**
   * CDR Result: Honda->Gasoline Engine->PC - Passenger Car->ME(D)17->HONDA XE1B 2KR (Version 1)->29) Transient
   * Compensation - PS-EC (formerly DGS-EC)->2015-03-17 10:50 - V75S30 - 2KR EU6B - FDR Honda 2KR Transient Compensation
   * <p>
   * URL: icdm:cdrid,770237066-773517365-768673297
   */
  private static final long REVIEW_RESULT_ID = 770237066L;
  private static final long INV_REVIEW_RESULT_ID = -770237066L;

  private static final long REVIEW_RESULT_ID_WITH_GRP_MAPPING = 9733810537L;
  private static final long PIDC_VERS_ID_WITH_GRP_MAPPING = 1165057178L;

  private static final long PIDC_VERS_ID = 773517365L;

  private static final long PIDC_A2L_ID = 773639065L;

  private static Long RVW_RESULT_ID[] = { 770237066l, 766291966l, 769458878l };
  private static final Set<Long> RVW_RESULT_ID_SET = new HashSet<>(Arrays.asList(RVW_RESULT_ID));

  private static Long INV_RVW_RESULT_ID[] = { -770237066l, -766291966l, -769458878l };
  private static final Set<Long> INV_RVW_RESULT_ID_SET = new HashSet<>(Arrays.asList(INV_RVW_RESULT_ID));

  private static final Long RESULT_ID_2 = 18984883078L;

  /**
   * @throws InterruptedException exception
   * @throws ApicWebServiceException exception
   */
  @Test
  public void test04() throws InterruptedException, ApicWebServiceException {
    CDRReviewResultServiceClient client = new CDRReviewResultServiceClient();

    // to delete 769575759
    ReviewResultDeleteValidation reviewResultDeleteValidation = client.reviewResultDeleteValidation(768366014L);
    assertNotNull("object is not null", reviewResultDeleteValidation);
    CDMLogger.getInstance().info("Can Delete " + reviewResultDeleteValidation.isDeletable());
    CDMLogger.getInstance().info("Can User Delete " + reviewResultDeleteValidation.isCanUsrDelReview());
    CDMLogger.getInstance().info("Has Attachments " + reviewResultDeleteValidation.isHasAttchments());
    CDMLogger.getInstance().info("Has Child Attachments " + reviewResultDeleteValidation.isHasChildAttachment());

  }


  /**
   * Test method for {@link CDRReviewResultServiceClient#hasPidcRevResults(java.lang.Long)}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testHasPidcRevResults() throws ApicWebServiceException {
    boolean ret = new CDRReviewResultServiceClient().hasPidcRevResults(PIDC_VERS_ID_ACTIVE);
    LOG.info("Has review results for pidc version {} = {} ", PIDC_VERS_ID_ACTIVE, ret);
    assertTrue("Has review results for pidc version " + PIDC_VERS_ID_ACTIVE, ret);
  }

  /**
   * Test method for {@link CDRReviewResultServiceClient#isUsedInCDFXDelivery(java.lang.Long)}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testIsUsedInCDFXDelivery() throws ApicWebServiceException {
    boolean isUsedInCDFXDelivery = new CDRReviewResultServiceClient().isUsedInCDFXDelivery(RVW_USED_IN_CDFX_DELIVERY);
    LOG.info("IS Review Result Used for CDFX Delivery {} = {} ", RVW_USED_IN_CDFX_DELIVERY, isUsedInCDFXDelivery);
    assertTrue("IS Review Result Used for CDFX Delivery " + RVW_USED_IN_CDFX_DELIVERY, isUsedInCDFXDelivery);
  }

  /**
   * Test method for {@link CDRReviewResultServiceClient#getPidcRevResults(java.lang.Long)}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetPidcRevResults() throws ApicWebServiceException {
    Map<String, Map<Long, CDRReviewResult>> retMap = new CDRReviewResultServiceClient().getPidcRevResults(PIDC_VERS_ID);
    Map<Long, CDRReviewResult> ret = retMap.get("29) Transient Compensation");
    System.out.println(ret.get(REVIEW_RESULT_ID));
    LOG.info("service response size = {}", retMap.size());
    assertFalse("service response available", retMap.isEmpty());
  }

  @Test
  public void testGetNewRvwResultsDetails() throws ApicWebServiceException {
    PidcReviewDetails ret = new CDRReviewResultServiceClient().getNewReviewResultInfo(null, 2192185289L);

    LOG.info("A2lRespMap size = {}", ret.getA2lRespMap().size());
    LOG.info("VarWpMap size = {}", ret.getVarWpMap().size());
    LOG.info("A2lWpMap size = {}", ret.getA2lWpMap().size());
    LOG.info("CdrResultMap size = {}", ret.getCdrResultMap().size());
    LOG.info("OtherSrcTypeResults size = {}", ret.getOtherSrcTypeResults().size());
    LOG.info("PidcVarMap size = {}", ret.getPidcVarMap().size());
    LOG.info("RvwVariantMap size = {}", ret.getRvwVariantMap().size());
    LOG.info("VarRespWpMap size = {}", ret.getVarRespWpMap().size());


    assertFalse("A2lRespMap not empty", ret.getA2lRespMap().isEmpty());
    assertFalse("VarWpMap not empty", ret.getVarWpMap().isEmpty());
    assertFalse("A2lWpMap not empty", ret.getA2lWpMap().isEmpty());
    assertFalse("CdrResultMap not empty", ret.getCdrResultMap().isEmpty());
    assertFalse("OtherSrcTypeResults not empty", ret.getOtherSrcTypeResults().isEmpty());
    assertFalse("pidc variant map not empty", ret.getPidcVarMap().isEmpty());
    assertFalse("RvwVariantMap not empty", ret.getRvwVariantMap().isEmpty());
    assertFalse("VarRespWpMap not empty", ret.getVarRespWpMap().isEmpty());
  }

  /**
   * Test method for {@link CDRReviewResultServiceClient#getReviewResultInfo(java.lang.Long)}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetReviewResultsInfo() throws ApicWebServiceException {
    PidcReviewDetails ret = new CDRReviewResultServiceClient().getReviewResultInfo(1165057178L);
    LOG.info("A2lRespMap size = {}", ret.getA2lRespMap().size());
    LOG.info("VarWpMap size = {}", ret.getVarWpMap().size());
    LOG.info("A2lWpMap size = {}", ret.getA2lWpMap().size());
    LOG.info("CdrResultMap size = {}", ret.getCdrResultMap().size());
    LOG.info("OtherSrcTypeResults size = {}", ret.getOtherSrcTypeResults().size());
    LOG.info("PidcVarMap size = {}", ret.getPidcVarMap().size());
    LOG.info("RvwVariantMap size = {}", ret.getRvwVariantMap().size());
    LOG.info("VarRespWpMap size = {}", ret.getVarRespWpMap().size());


    assertFalse("A2lRespMap not empty", ret.getA2lRespMap().isEmpty());
    assertFalse("VarWpMap not empty", ret.getVarWpMap().isEmpty());
    assertFalse("A2lWpMap not empty", ret.getA2lWpMap().isEmpty());
    assertFalse("CdrResultMap not empty", ret.getCdrResultMap().isEmpty());
    assertFalse("OtherSrcTypeResults not empty", ret.getOtherSrcTypeResults().isEmpty());
    assertFalse("A2lWpMap not empty", ret.getPidcVarMap().isEmpty());
    assertFalse("RvwVariantMap not empty", ret.getRvwVariantMap().isEmpty());
    assertFalse("VarRespWpMap not empty", ret.getVarRespWpMap().isEmpty());

  }

  /**
   * Test method for {@link CDRReviewResultServiceClient#resolveFc2WpName(java.lang.Long)}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testResolveFc2WpName() throws ApicWebServiceException {
    String ret = new CDRReviewResultServiceClient().resolveFc2WpName(REVIEW_RESULT_ID);
    LOG.info("Fc2Wp name = {}", ret);
    assertEquals("Fc2Wp name", "29) Transient Compensation", ret);
  }

  /**
   * Test method for {@link CDRReviewResultServiceClient#getPidcVarRevResults(java.lang.Long)}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetPidcVarRevResults() throws ApicWebServiceException {
    Map<String, PidcTreeRvwVariant> retMap =
        new CDRReviewResultServiceClient().getPidcVarRevResults(PIDC_VERS_ID_ACTIVE);
    LOG.info("service response size = {}", retMap.size());
    assertFalse("service response available", retMap.isEmpty());
  }

  /**
   * Test method for {@link CDRReviewResultServiceClient#getById(Long)}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetById() throws ApicWebServiceException {
    CDRReviewResult ret = new CDRReviewResultServiceClient().getById(REVIEW_RESULT_ID);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, ret);
    testOutput(ret);
  }

  /**
   * @param obj
   */
  private void testOutput(final CDRReviewResult obj) {
    assertEquals("Result Id is equal", Long.valueOf(770237066l), obj.getId());
    assertEquals("Group Workpackage is equal", "<FC2WP>", obj.getGrpWorkPkg());
    assertEquals("Review status is equal", "C", obj.getRvwStatus());
    assertNull("Org Result Id is equal", obj.getOrgResultId());
    assertEquals("Created User is equal", "TWL2SI", obj.getCreatedUser());
    assertEquals("Description is equal", "FDR Honda 2KR Transient Compensation", obj.getDescription());
    assertEquals("Source type is equal", "FC_WP", obj.getSourceType());
    assertEquals("Review type is equal", "O", obj.getReviewType());
    assertNull("RSet Id is equal", obj.getRsetId());
    assertEquals("PIDC A2L Id is equal", Long.valueOf(773639065l), obj.getPidcA2lId());
    assertEquals("WP Div Id is equal", Long.valueOf(795414041l), obj.getWpDivId());
    assertNull("WP Definition Version Id is equal", obj.getWpDefnVersId());
  }

  /**
   * Test method for {@link CDRReviewResultServiceClient#getById(Long)}.Negative test
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetByIdNegative() throws ApicWebServiceException {
    this.thrown.expectMessage("Data Review Result with ID '" + INV_REVIEW_RESULT_ID + "' not found");
    new CDRReviewResultServiceClient().getById(INV_REVIEW_RESULT_ID);
    fail("Expected exception not thrown");
  }

  /**
   * Test method for {@link CDRReviewResultServiceClient#getById(Set)}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetByIds() throws ApicWebServiceException {
    Map<Long, CDRReviewResult> retMap = new CDRReviewResultServiceClient().getById(RVW_RESULT_ID_SET);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, retMap.isEmpty());
    testOutput(retMap.get(REVIEW_RESULT_ID));
  }

  /**
   * Test method for {@link CDRReviewResultServiceClient#getById(Set)}.Negative test
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetByIdsNegative() throws ApicWebServiceException {
    this.thrown.expectMessage("Data Review Result with ID ");
    new CDRReviewResultServiceClient().getById(INV_RVW_RESULT_ID_SET);
    fail("Expected exception not thrown");
  }

  /**
   * Test method for {@link CDRReviewResultServiceClient#getMultiple(Set)}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetMultiple() throws ApicWebServiceException {
    Map<Long, CDRReviewResult> retMap = new CDRReviewResultServiceClient().getMultiple(RVW_RESULT_ID_SET);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, retMap.isEmpty());
    testOutput(retMap.get(REVIEW_RESULT_ID));
  }

  /**
   * Test method for {@link CDRReviewResultServiceClient#getMultiple(Set)}.Negative test
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetMultipleNegative() throws ApicWebServiceException {
    this.thrown.expectMessage("Data Review Result with ID ");
    new CDRReviewResultServiceClient().getMultiple(INV_RVW_RESULT_ID_SET);
    fail("Expected exception not thrown");
  }


  /**
   * Test method for {@link CDRReviewResultServiceClient#update(CDRReviewResult)},
   * {@link CDRReviewResultServiceClient#deleteReviewResult(CDRReviewResult,boolean)}.T_RVW_RESULTS
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testUpdateDeleteSync() throws ApicWebServiceException {

    CDRReviewResultServiceClient client = new CDRReviewResultServiceClient();
    // perform review and get review result
    ReviewInput data = createInput("testdata/cdr/compli.cdfx", CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType(), "",
        1328585266L, 1293613521L);
    data.setFilesToBeReviewed(true);
    ReviewOutput output = executeReview(data);
    CDRReviewResult cdrReviewResult = output.getCdrResult();
    // validate create
    assertNotNull("Created object should not be null", cdrReviewResult);
    assertEquals("Version is equal", Long.valueOf(1), cdrReviewResult.getVersion());
    Long versionBeforeUpdate = cdrReviewResult.getVersion();
    // update review result
    cdrReviewResult.setDescription("test_Updated");
    CDRReviewResult updatedResult = client.update(cdrReviewResult);
    // validate update
    assertNotNull("Created object should not be null", updatedResult);
    assertEquals("Version is equal", Long.valueOf(versionBeforeUpdate + 1), updatedResult.getVersion());
    // delete review result
    client.deleteReviewResult(updatedResult, false);
  }

  /**
   * Test method for {@link CDRReviewResultServiceClient#update(CDRReviewResult)},
   * {@link CDRReviewResultServiceClient#deleteReviewResult(CDRReviewResult,boolean)}.T_RVW_RESULTS
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testUpdateDeleteASync() throws ApicWebServiceException {

    CDRReviewResultServiceClient client = new CDRReviewResultServiceClient();
    // perform review and get review result
    ReviewInput data = createInput("testdata/cdr/compli.cdfx", CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType(), "",
        1328585266L, 1293613521L);
    data.setFilesToBeReviewed(true);
    ReviewOutput output = executeReview(data);
    CDRReviewResult cdrReviewResult = output.getCdrResult();
    // validate create
    assertNotNull("Created object should not be null", cdrReviewResult);
    assertEquals("Version is equal", Long.valueOf(1), cdrReviewResult.getVersion());
    // delete review result
    client.deleteReviewResult(cdrReviewResult, true);
  }

  /**
   * Test method for {@link CDRReviewResultServiceClient#deleteReviewResult(CDRReviewResult,boolean)}.T_RVW_RESULTS
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testAsyncDeleteReviewWithChild() throws ApicWebServiceException {
    // checking expected exception
    CDRReviewResultServiceClient client = new CDRReviewResultServiceClient();
    // get review result with child review by id
    CDRReviewResult cdrReviewResult = client.getById(14556372966L);
    // delete review result
    client.deleteReviewResult(cdrReviewResult, true);
    // since the review has child, the review will not be deleted
    assertNotNull(cdrReviewResult);
  }


  /**
   * Test method for {@link CDRReviewResultServiceClient#getCDRResultsByPidcA2l(Long)}.T_RVW_RESULTS
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetCDRResultsByPidcA2l() throws ApicWebServiceException {
    SortedSet<CDRReviewResult> retSet = new CDRReviewResultServiceClient().getCDRResultsByPidcA2l(PIDC_A2L_ID);
    assertFalse("Response should not be null or empty", retSet.isEmpty());
    for (CDRReviewResult data : retSet) {
      if (data.getId() == REVIEW_RESULT_ID) {
        testOutput(data);
      }
    }
  }

  /**
   * Test method for {@link CDRReviewResultServiceClient#getCDRResultsByPidcA2l(Long)}.Negative test
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetCDRResultsByPidcA2lNegative() throws ApicWebServiceException {
    SortedSet<CDRReviewResult> retSet = new CDRReviewResultServiceClient().getCDRResultsByPidcA2l(-12L);
    assertTrue("Response should be null or empty", retSet.isEmpty());
  }

  /**
   * Test method for {@link CDRReviewResultServiceClient#getReviewResultForDeltaReview(Long, String)}.T_RVW_RESULTS
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetReviewResultForDeltaReviewA2l() throws ApicWebServiceException {
    CDRWizardUIModel ret = new CDRReviewResultServiceClient().getReviewResultForDeltaReview(REVIEW_RESULT_ID,
        CommonUtils.getSystemUserTempDirPath());
    assertNotNull("Response should not be null or empty", ret);
    testOutput(ret.getCdrReviewResult());
  }


  /**
   * Test method for {@link CDRReviewResultServiceClient#getReviewResultForDeltaReview(Long, String)}.T_RVW_RESULTS
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetReviewResultForDeltaReviewLab() throws ApicWebServiceException {
    CDRWizardUIModel ret = new CDRReviewResultServiceClient().getReviewResultForDeltaReview(1574515785L,
        CommonUtils.getSystemUserTempDirPath());
    assertNotNull("Response should not be null or empty", ret);

  }


  /**
   * Test method for {@link CDRReviewResultServiceClient#getReviewResultForDeltaReview(Long, String)}.T_RVW_RESULTS
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetReviewResultForDeltaReviewMonica() throws ApicWebServiceException {
    CDRWizardUIModel ret = new CDRReviewResultServiceClient().getReviewResultForDeltaReview(1537157542L,
        CommonUtils.getSystemUserTempDirPath());
    assertNotNull("Response should not be null or empty", ret);

  }


  /**
   * Test method for {@link CDRReviewResultServiceClient#getReviewResultForDeltaReview(Long, String)}.Negative test with
   * wrong directory path
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetReviewResultForDeltaReviewNegativePath() throws ApicWebServiceException {
    this.thrown.expectMessage("Error while downloading file");
    new CDRReviewResultServiceClient().getReviewResultForDeltaReview(REVIEW_RESULT_ID, null);
    fail("Expected exception not thrown");
  }

  /**
   * Test method for {@link CDRReviewResultServiceClient#getResultDetailsForAttachVar(Long)}.T_RVW_RESULTS
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetResultDetailsForAttachVarWithGrpMapping() throws ApicWebServiceException {
    CopyResultToVarData ret =
        new CDRReviewResultServiceClient().getResultDetailsForAttachVar(REVIEW_RESULT_ID_WITH_GRP_MAPPING);
    assertNotNull("Response should not be null or empty", ret);
    assertEquals("PIDC Version ID is equal", Long.valueOf(PIDC_VERS_ID_WITH_GRP_MAPPING), ret.getPidcVersion().getId());
  }

  /**
   * Test method for {@link CDRReviewResultServiceClient#getResultDetailsForAttachVar(Long)}.T_RVW_RESULTS
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetResultDetailsForAttachVarWithoutGrpMapping() throws ApicWebServiceException {
    CopyResultToVarData ret = new CDRReviewResultServiceClient().getResultDetailsForAttachVar(REVIEW_RESULT_ID);
    assertNotNull("Response should not be null or empty", ret);
    assertEquals("PIDC Version ID is equal", Long.valueOf(PIDC_VERS_ID), ret.getPidcVersion().getId());
  }


  /**
   * Testcase to update the workpackage responsible status
   *
   * @throws ApicWebServiceException as exception
   */
  @Test
  public void testUpdateWorkpackageStatus() throws ApicWebServiceException {
    // icdm:cdrid,22706494951-10827299181-22706494928
    CDRReviewResultServiceClient cdrReviewResultServiceClient = new CDRReviewResultServiceClient();
    cdrReviewResultServiceClient.updateWorkpackageStatus(cdrReviewResultServiceClient.getById(22706494951l));

  }

  /**
   * Testcase to update the workpackage responsible status from tree View
   *
   * @throws ApicWebServiceException as exception
   */
  @Test
  public void testUpdateWorkpackageStatusFrmTreeView() throws ApicWebServiceException {

    TreeViewSelectnRespWP treeViewSelWpResp = new TreeViewSelectnRespWP();
    treeViewSelWpResp.setPidcA2lID(PIDC_A2L_ID_WP_FINISH_TREE_VIEW);
    treeViewSelWpResp.setVariantID(PIDC_VARIANT_WP_FINISH_TREE_VIEW);

    A2lWPRespModel a2lWPRespModel = new A2lWPRespModel();
    a2lWPRespModel.setA2lRespId(A2L_WP_RESPONSIBILITY_ID);
    a2lWPRespModel.setA2lWpId(A2L_WORKPACKAGE_ID);
    a2lWPRespModel.setWpRespId(A2L_WP_RESPONSIBILITY_ID);
    a2lWPRespModel.setInheritedFlag(true);

    Map<Long, A2lWPRespModel> wpIDA2lWpRespIDMap = new HashMap<>();
    wpIDA2lWpRespIDMap.put(A2L_WORKPACKAGE_ID, a2lWPRespModel);

    Map<Long, Map<Long, A2lWPRespModel>> respWpA2lWpRespModelMap = new HashMap<>();
    respWpA2lWpRespModelMap.put(A2L_RESPONSIBILITY_ID, wpIDA2lWpRespIDMap);
    treeViewSelWpResp.setRespWpA2lWpRespModelMap(respWpA2lWpRespModelMap);

    // icdm:cdrid,28708507478-28590765231-28654274614
    CDRReviewResultServiceClient cdrReviewResultServiceClient = new CDRReviewResultServiceClient();

    cdrReviewResultServiceClient.updateSelWorkpackageStatus(treeViewSelWpResp);

  }

  /**
   * Test method for {@link CDRReviewResultServiceClient#getResultDetailsForAttachVar(Long)}.Negative test
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetResultDetailsForAttachVarNegative() throws ApicWebServiceException {
    this.thrown.expectMessage("Data Review Result with ID '" + INV_REVIEW_RESULT_ID + "' not found");
    new CDRReviewResultServiceClient().getResultDetailsForAttachVar(INV_REVIEW_RESULT_ID);
    fail("Expected exception not thrown");
  }


  /**
   * Test method for {@link CDRReviewResultServiceClient#canModify(Long)}.T_RVW_RESULTS
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testCanModify() throws ApicWebServiceException {
    boolean ret = new CDRReviewResultServiceClient().canModify(REVIEW_RESULT_ID);
    LOG.info("Can review result modified: {} ", ret);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, ret);
  }

  /**
   * Test method for {@link CDRReviewResultServiceClient#getRvwResultEditorData(Long, Long) }
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetRvwResultEditorData() throws ApicWebServiceException {
    CDRReviewResultServiceClient servClient = new CDRReviewResultServiceClient();

    ReviewResultEditorData editorData = servClient.getRvwResultEditorData(RESULT_ID_2, RVW_VARIANT_ID2);
    assertFalse("Response should not be null or empty",
        ((editorData.getParamMap() == null) || editorData.getParamMap().isEmpty()));

    CDRReviewResult reviewResult = editorData.getReviewResult();
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, reviewResult);
    testCDRRvwResult(reviewResult);

    PidcVersion pidcVers = editorData.getPidcVers();
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, pidcVers);
    testPidcVers(pidcVers);

    PidcA2l pidcA2l = editorData.getPidcA2l();
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, pidcA2l);
    testPidcA2l(pidcA2l);

    Pidc pidc = editorData.getPidc();
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, pidc);
    testPidc(pidc);

    RuleSet ruleSet = editorData.getRuleSet();
    assertNull("Response should be null", ruleSet);

    PidcVariant firstVariant = editorData.getFirstVariant();
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, firstVariant);
    testVar(firstVariant);

    // test not null of ques resp list
    Set<QnaireRespStatusData> qnaireDataForRvwSet = editorData.getQnaireDataForRvwSet();
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, qnaireDataForRvwSet);
    testQuesRespList(qnaireDataForRvwSet);
  }

  /**
   * @param qnaireDataForRvwSet Set<QnaireRespStatusData>
   */
  private void testQuesRespList(final Set<QnaireRespStatusData> qnaireDataForRvwSet) {
    int size = 2;
    assertEquals("Size of the set is equal", size, qnaireDataForRvwSet.size());
  }


  /**
   * Test method for {@link CDRReviewResultServiceClient#getRvwResultEditorData(Long, Long) } Negative scenario
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetRvwResultEditorDataNegative() throws ApicWebServiceException {
    CDRReviewResultServiceClient servClient = new CDRReviewResultServiceClient();
    this.thrown.expectMessage("invalid for Data Review Result");
    servClient.getRvwResultEditorData(INV_REVIEW_RESULT_ID, null);
    fail("Expected exception not thrown");
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
   * Test case to test multiple review result delete validation and delete review result services
   *
   * @throws ApicWebServiceException- exception thrown on invocation
   */
  @Test
  public void testMultipleReviewResultValidationandDeletion() throws ApicWebServiceException {
    CDRReviewResultServiceClient cdrReviewResultServiceClient = new CDRReviewResultServiceClient();
    Set<Long> reviewIdSet = new HashSet<>();
    Long reviewResultId;
    for (int i = 0; i < 2; i++) {
      ReviewInput reviewResultInput = createInput("testdata/cdr/HEX_DMG1811V11C1920_M20C51.hex",
          CDRConstants.CDR_SOURCE_TYPE.WP.getDbType(), "BGLWM", 41920235354L, 41920235342L);
      reviewResultInput.setReviewType("O");
      ReviewOutput reviewResultOutput = executeReview(reviewResultInput);
      assertNotNull("The review Output should not be null", reviewResultOutput);

      reviewResultId = reviewResultOutput.getCdrResult().getId();

      reviewIdSet.add(reviewResultId);
    }

    for (Long revwId : reviewIdSet) {
      assertNotNull("The review result Id should not be null", revwId);
    }

    Map<Long, ReviewResultDeleteValidation> multipleRvwResultValidation =
        cdrReviewResultServiceClient.getMultipleReviewResultDeleteValidation(reviewIdSet);

    for (Long revwId : reviewIdSet) {
      assertEquals("The review result created has no child", false,
          multipleRvwResultValidation.get(revwId).isHasChildReview());
    }

    cdrReviewResultServiceClient.deleteMultipleRvwResult(reviewIdSet);


  }


}
