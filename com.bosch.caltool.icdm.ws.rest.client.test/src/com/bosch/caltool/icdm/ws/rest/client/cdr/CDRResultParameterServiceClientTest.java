/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.ParameterReviewResult;
import com.bosch.caltool.icdm.model.cdr.ReviewDetailsData;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.Matchers;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for CDR Result Parameter
 *
 * @author BRU2COB
 */
public class CDRResultParameterServiceClientTest extends AbstractRestClientTest {

  /**
   *
   */
  private static final String UPDATED_RVW_COMMENT_IS_EQUAL = "Updated RvwComment is equal";
  /**
   *
   */
  private static final String PARAM_NAME = "AccMon_aFrzCond_C";
  /**
   *
   */
  private static final String RESPONSE_SHOULD_NOT_BE_NULL = "Response should not be null";
  /**
   * icdm:cdrid,12354867878-2189286283-2200243228-->Source review result link
   */
  private static final long SOURCE_RESULT_ID_EMPTY_CMNT = 12354867878L;
  /**
   * Label DATA_SysOpmVeh.SysOpmVeh_ctMaxStrtFailAt_C_VW,review result
   * link-->icdm:cdrid,9984518529-2189286283-2200243228
   */
  private static final long DEST_PARAM_EMPTY_CMNT = 9984518554L;
  /**
   * Label DATA_SysOpmVeh.SysOpmVeh_ctMaxStrtFailAt_C_VW,review result
   * link-->icdm:cdrid,12354867878-2189286283-2200243228
   */
  private static final long SOURCE_PARAM_EMPTY_CMNT = 12354867903L;
  /**
   *
   */
  private static final Long SOURCE_RESULT_ID = 2972010378L;
  private static final Long RVW_PARAM_ID = 1442973851L;
  private static final Long RESULT_ID = 1442973832L;
  private static final Long INVALID_ID = -100L;


  /**
   * Test method for {@link CDRResultParameterServiceClient#get(Long)}
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void testGet() throws ApicWebServiceException {
    CDRResultParameterServiceClient servClient = new CDRResultParameterServiceClient();
    CDRResultParameter ret = servClient.get(RVW_PARAM_ID);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, ret);
    testCDRResultParam(ret);
  }

  /**
   * @param ret
   */
  private void testCDRResultParam(final CDRResultParameter ret) {
    assertEquals("Param Name is equal", PARAM_NAME, ret.getName());
    assertEquals("Parameter Type is equal", "VALUE", ret.getpType());
    assertEquals("Review File Id is equal", Long.valueOf(1442973840), ret.getRvwFileId());
    assertEquals("Review Function Id is equal", Long.valueOf(1442973833), ret.getRvwFunId());
  }

  /**
   * Negative Test method for {@link CDRResultParameterServiceClient#get(Long)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetNegative() throws ApicWebServiceException {
    CDRResultParameterServiceClient servClient = new CDRResultParameterServiceClient();

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Review Result Parameter with ID '" + INVALID_ID + "' not found");
    servClient.get(INVALID_ID);
    fail("Expected exception not thrown");
  }

  /**
   * Test method for {@link CDRResultParameterServiceClient#getMultiple(Set) }
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void testGetMultiple() throws ApicWebServiceException {
    CDRResultParameterServiceClient servClient = new CDRResultParameterServiceClient();

    Map<Long, CDRResultParameter> retMap =
        servClient.getMultiple(new HashSet<>(Arrays.asList(RVW_PARAM_ID, 1442973861L)));
    assertFalse("Response should not be null or empty", ((retMap == null) || retMap.isEmpty()));
    CDRResultParameter ret = retMap.get(RVW_PARAM_ID);
    testCDRResultParam(ret);
  }


  /**
   * Test method for {@link CDRResultParameterServiceClient#getReviewDetailsDataByResultId(Long, String) }
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetReviewDetailsDataByResultId() throws ApicWebServiceException {
    CDRResultParameterServiceClient servClient = new CDRResultParameterServiceClient();

    ReviewDetailsData reviewDetailsData = servClient.getReviewDetailsDataByResultId(RESULT_ID, PARAM_NAME);

    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL, reviewDetailsData == null);
    assertEquals("Response's parameter name check", PARAM_NAME, reviewDetailsData.getCdrResultParameter().getName());

  }


  /**
   * Test meethod for {@link CDRResultParameterServiceClient#getParameterReviewResult(List)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetParameterReviewResult() throws ApicWebServiceException {
    CDRResultParameterServiceClient servClient = new CDRResultParameterServiceClient();

    List<ParameterReviewResult> parameterReviewResult = new ArrayList<>();
    List<Long> paramIds = new ArrayList<>();
    paramIds.add(381214565L);
    paramIds.add(432182565L);
    paramIds.add(433642515L);
    parameterReviewResult = servClient.getParameterReviewResult(paramIds);
    assertTrue(!parameterReviewResult.isEmpty());
    LOG.info("===========RESULT=========" +
        (parameterReviewResult != null ? "Result size : " + parameterReviewResult.size() : "result is null"));
  }

  /**
   * Test method for {@link CDRResultParameterServiceClient#update(List)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testUpdate() throws ApicWebServiceException {
    CDRResultParameterServiceClient servClient = new CDRResultParameterServiceClient();

    CDRResultParameter cdrResultParameter = servClient.get(770871872L);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, cdrResultParameter);
    cdrResultParameter.setRvwComment("JUnit_Test" + getRunId() + "RvwComment Updated");
    cdrResultParameter.setReviewScore("1");
    List<CDRResultParameter> paramList = new ArrayList<>();
    paramList.add(cdrResultParameter);

    // invoke update
    Map<Long, CDRResultParameter> updatedCDRResultParamMap = servClient.update(paramList);
    assertFalse("Response should not be null or empty",
        ((updatedCDRResultParamMap == null) || updatedCDRResultParamMap.isEmpty()));

    // validate update
    CDRResultParameter updatedParam = updatedCDRResultParamMap.get(770871872L);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, updatedParam);
    assertEquals(UPDATED_RVW_COMMENT_IS_EQUAL, "JUnit_Test" + getRunId() + "RvwComment Updated",
        updatedParam.getRvwComment());
    assertEquals("Updated Review Score is equal", "1", updatedParam.getReviewScore());
  }

  /**
   * Test method for {@link CDRResultParameterServiceClient#ImportComments()} overwrite destination param comments -
   * true check score value to take over source param comments - false
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testImportCommentsWithoutScoreCheck() throws ApicWebServiceException {
    CDRResultParameterServiceClient servClient = new CDRResultParameterServiceClient();
    Map<Long, CDRResultParameter> inputParamMap = new HashMap<>();
    CDRResultParameter destParam1 = servClient.get(4963679438L);
    inputParamMap.put(4963679438L, destParam1);
    CDRResultParameter destParam2 = servClient.get(4963679442L);
    inputParamMap.put(4963679442L, destParam2);
    Map<Long, CDRResultParameter> updatedParamMap =
        servClient.importReviewComment(inputParamMap, true, SOURCE_RESULT_ID, false);
    CDRResultParameter sourceParam1 = servClient.get(2972010388L);
    CDRResultParameter sourceParam2 = servClient.get(2972010392L);
    assertEquals(UPDATED_RVW_COMMENT_IS_EQUAL, sourceParam1.getRvwComment(),
        updatedParamMap.get(4963679438L).getRvwComment());
    assertEquals(UPDATED_RVW_COMMENT_IS_EQUAL, sourceParam2.getRvwComment(),
        updatedParamMap.get(4963679442L).getRvwComment());
  }

  /**
   * Test method for {@link CDRResultParameterServiceClient#ImportComments()} to validate imported source commnets *
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testImportEmptyComments() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("RVW_RESULT_COMMENT_IMPORT.EMPTY_COMMENT"));
    // Label to be imported DATA_SysOpmVeh.SysOpmVeh_ctMaxStrtFailAt_C_VW
    CDRResultParameterServiceClient servClient = new CDRResultParameterServiceClient();
    CDRResultParameter sourceParam = servClient.get(SOURCE_PARAM_EMPTY_CMNT);
    // Setting the label's(DATA_SysOpmVeh.SysOpmVeh_ctMaxStrtFailAt_C_VW) ,comment in source review result to empty for
    // test
    sourceParam.setRvwComment("");
    Map<Long, CDRResultParameter> inputParamMap = new HashMap<>();
    // Result Parameter in destination review result
    CDRResultParameter destParam = servClient.get(DEST_PARAM_EMPTY_CMNT);
    inputParamMap.put(DEST_PARAM_EMPTY_CMNT, destParam);
    servClient.importReviewComment(inputParamMap, true, SOURCE_RESULT_ID_EMPTY_CMNT, false);
  }

  /**
   * Test method for {@link CDRResultParameterServiceClient#ImportComments()} overwrite destination param comments -
   * true check score value to take over source param comments - true
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testImportCommentsWithScoreCheck() throws ApicWebServiceException {
    CDRResultParameterServiceClient servClient = new CDRResultParameterServiceClient();
    Map<Long, CDRResultParameter> inputParamMap = new HashMap<>();
    long destParam1Id = 4963679438L;
    CDRResultParameter destParam1 = servClient.get(destParam1Id);
    inputParamMap.put(destParam1Id, destParam1);
    long destParam2Id = 4963679442L;
    CDRResultParameter destParam2 = servClient.get(destParam2Id);
    inputParamMap.put(destParam2Id, destParam2);
    Map<Long, CDRResultParameter> updatedParamMap =
        servClient.importReviewComment(inputParamMap, true, 5760206078L, true);
    CDRResultParameter sourceParam1 = servClient.get(5760206084L);
    // param 1 should be updated as score is 7
    assertEquals(UPDATED_RVW_COMMENT_IS_EQUAL, sourceParam1.getRvwComment(),
        updatedParamMap.get(destParam1Id).getRvwComment());
    // param 2 is not updated as the score is less than 7.
    assertEquals(UPDATED_RVW_COMMENT_IS_EQUAL, updatedParamMap.get(destParam2Id).getRvwComment(),
        updatedParamMap.get(destParam2Id).getRvwComment());
  }

  /**
   * Test method for {@link CDRResultParameterServiceClient#ImportComments()} overwrite destination param comments -
   * true check score value to take over source param comments - false
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testImportCommentsWithoutOverwriteComments() throws ApicWebServiceException {
    CDRResultParameterServiceClient servClient = new CDRResultParameterServiceClient();
    Map<Long, CDRResultParameter> inputParamMap = new HashMap<>();
    long destParam1Id = 4963679438L;
    CDRResultParameter destParam1 = servClient.get(destParam1Id);
    inputParamMap.put(destParam1Id, destParam1);
    long destParam2Id = 4963679442L;
    CDRResultParameter destParam2 = servClient.get(destParam2Id);
    inputParamMap.put(destParam2Id, destParam2);
    Map<Long, CDRResultParameter> updatedParamMap =
        servClient.importReviewComment(inputParamMap, false, 5760206078L, false);
    CDRResultParameter sourceParam1 = servClient.get(5760206084L);
    // param 1 should be updated as score is 7
    assertEquals(UPDATED_RVW_COMMENT_IS_EQUAL, sourceParam1.getRvwComment(),
        updatedParamMap.get(destParam1Id).getRvwComment());
    // param 2 is not updated as the score is less than 7.
    assertEquals("Updated RvwComment is not equal", updatedParamMap.get(destParam2Id).getRvwComment(),
        updatedParamMap.get(destParam2Id).getRvwComment());
  }
}
