/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.DELTA_REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.model.cdr.ReviewResultEditorData;
import com.bosch.caltool.icdm.model.cdr.review.ReviewInput;
import com.bosch.caltool.icdm.model.cdr.review.ReviewOutput;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author bne4cob
 */
public class ReviewServiceClientDeltaTest extends AbstractReviewServiceClientTest {

  /**
   * Parameter to test Delta review when CalData is changed
   */
  private static final String PARAM_CHANGED_CAL_DATA = "EpmCrS_tiPlsFwd_C";

  private static final String PARAM5_OFFICIAL_PARENT = "DINH_FId.DFC_CEngDsTDynTst_CA";

  private static final String PARAM4_OFFICIAL_PARENT = "CEngDsT_tiPT1T1_C";

  private static final String PARAM3_OFFICIAL_PARENT = "CEngDsT_stSensID_C";

  private static final String PARAM2_OOFICIAL_PARENT = "AccMon_tiaSlip_C";

  private static final String PARAM_OFFICIAL_PARENT = "AccMon_aPtdMax_C";

  private static final String PARAM_START_PARENT = "DDRC_RatDeb.AirC_tiClntSigUDRat_C";

  private static final String CHECKED_COLUMN_IS_NOT_COLOURED = "Checked column is not coloured";

  private static final String SERIES_COLUMN_IS_NOT_COLOURED = "Series column is not coloured";

  private static final String RESULT_COLUMN_IS_NOT_COLOURED = "Result column is not coloured";

  private static final String CHANGE_FLAG_IS_NOT_ZERO = "Change flag is not zero";

  private static final String SCORE_IS_NOT_TAKEN_OVER = "Score is not taken over";

  private static final String FUNCTION_FOR_BOTH_START_AND_TEST_PARENT = "ACClntP_DD";

  private static final String PARAM_TEST_PARENT = "DFC_DisblMsk2.DFC_AirCClntPSig_C";

  private static final String SCORE_COLUMN_IS_NOT_COLOURED = "Score column is not coloured";

  private static final String SCORE_IS_TAKEN_OVER = "Score is taken over";
  /**
   * Hex file
   */
  private static final String HEX_FILE = "testdata/cdr/HEX_MMD114A0CC1788_MC50_DISCR_LC.hex";
  /**
   * CalData files
   */
  private static final String CAL_FILE = "testdata/cdr/Caldata_change_NormalLabel.cdfx";
  private static final String CAL_FILE2 = "testdata/cdr/UEGO_stCJ135Ctl_C.cdfx";

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
   * CDR Result: <br>
   * AUDI->Diesel Engine->PC - Passenger Car->MD1-C->X_Test_HENZE_1788 (V1)->REVIEWED_FILE->2018-12-28 16:34 - MD00 - AT
   * - test
   */
  private static final long PARENT_RESULT_ID = 1453072628L;

  /**
   * PIDC Variant: AUDI->Diesel Engine->PC - Passenger Car->MD1-C->X_Test_HENZE_1788 (V1)->AT
   */
  private static final long VAR_ID_1 = 1293613521L;
  /**
   * PIDC A2L mapping ID for A2L File: <br>
   * AUDI->Diesel Engine->PC - Passenger Car->MD1-C->X_Test_HENZE_1788
   * (V1)->MMD114A0CC1788->MMD114A0CC1788_MD00_withGroups.A2L
   */
  private static final long PIDC_A2L_ID_1_0 = 1328585266L;

  private static final String SCORE_0 = DATA_REVIEW_SCORE.S_0.getDbType();
  private static final String SCORE_7 = DATA_REVIEW_SCORE.S_7.getDbType();
  private static final String SCORE_8 = DATA_REVIEW_SCORE.S_8.getDbType();
  private static final String SCORE_5 = DATA_REVIEW_SCORE.S_5.getDbType();
  private static final String SCORE_9 = DATA_REVIEW_SCORE.S_9.getDbType();
  private static final int SCORE_CLOUMN = CDRConstants.SCORE_VAL_CHG_FLAG_INX;
  private static final int RESULT_CLOUMN = CDRConstants.RESULT_CHG_FLAG_INX;
  private static final int UPPER_LIMIT_CLOUMN = CDRConstants.HIGH_LMT_CHG_FLAG_INX;
  private static final int READY_SERIES_CLOUMN = CDRConstants.READY_FOR_SERIES_FLAG_INX;
  private static final int CHECKED_CLOUMN = CDRConstants.CHK_VAL_CHG_FLAG_INX;
  private static Map<String, CDRResultParameter> rvwParamMap = new HashMap<>();
  private static final Long NO_CHANGE = 0L;

  /**
   * Delta review + common rules + review file - cdfx .
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testDelta001() throws ApicWebServiceException {

    ReviewInput data = createInput("testdata/cdr/compli.cdfx", CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType(),
        "ChrCtl_LoPEgrSetLimn", PIDC_A2L_ID, VAR_ID);

    data.setFilesToBeReviewed(true);
    data.getResultData().setParentResultId(PARENT_RESULT_ID);
    data.setDeltaReviewType(DELTA_REVIEW_TYPE.DELTA_REVIEW.getDbType());
    data.setDeltaReview(true);
    data.setCdrReviewResult(new CDRReviewResult());
    data.getCdrReviewResult().setSdomPverVarName("MMD114A0CC1788");
    assertNotNull(executeReview(data));
  }

  /**
   * Testing when the local file path and param repeat excel path is empty
   *
   * @throws ApicWebServiceException
   */
  @Test
  public void testNegativedownloadFilesFromServer01() throws ApicWebServiceException {
    ReviewServiceClient reviewServiceClient = new ReviewServiceClient();
    String localFilePath = "";
    Set<String> filePaths = new HashSet<>();
    String path = reviewServiceClient.downloadFilesFromServer(filePaths, localFilePath);
    assertEquals("Did not return empty path string", path, "");
  }

  /**
   * Testing when the local file path is null & param repeat excel path is empty
   *
   * @throws ApicWebServiceException
   */
  @Test
  public void testNegativedownloadFilesFromServer02() throws ApicWebServiceException {
    ReviewServiceClient reviewServiceClient = new ReviewServiceClient();
    String localFilePath = null;
    Set<String> filePaths = new HashSet<>();
    String path = reviewServiceClient.downloadFilesFromServer(filePaths, localFilePath);
    assertNull(path);
  }

  /**
   * Testing when the local file path is empty & param repeat excel path is null
   *
   * @throws ApicWebServiceException
   */
  @Test
  public void testNegativedownloadFilesFromServer03() throws ApicWebServiceException {
    ReviewServiceClient reviewServiceClient = new ReviewServiceClient();
    String localFilePath = "";
    Set<String> filePaths = null;
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Internal server error occured. Please contact iCDM Hotline.");
    reviewServiceClient.downloadFilesFromServer(filePaths, localFilePath);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * * Test method for {@link ReviewServiceClient#downloadFilesFromServer(Set, String)}}
   *
   * @throws ApicWebServiceException
   */
  @Test
  public void testdownloadFilesFromServer() throws ApicWebServiceException {
    ReviewServiceClient reviewServiceClient = new ReviewServiceClient();
    final Set<String> filesToBeDownloaded = new HashSet<>();
    ReviewInput data = createInput("testdata/cdr/compli.cdfx,testdata/cdr/compliCopy.cdfx",
        CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType(), "", PIDC_A2L_ID_1_0, VAR_ID_1);
    data.setFilesToBeReviewed(true);
    try {
      executeReview(data);
    }
    catch (ApicWebServiceException e) {
      String errorMsg = e.getMessage();
      if ("CDR.REPEAT_PARAM_EXCEL_REPORT".equalsIgnoreCase(e.getErrorCode()) && (errorMsg != null) &&
          errorMsg.contains("Param Repeat Excel report")) {
        String[] filepath = errorMsg.split("\\|");
        String repeatedParamsExcelPath = filepath[1].trim();
        filesToBeDownloaded.add(repeatedParamsExcelPath);
      }
    }
    String localFilePath = CommonUtils.getICDMTmpFileDirectoryPath();
    String parentPath = reviewServiceClient.downloadFilesFromServer(filesToBeDownloaded, localFilePath);
    assertNotNull("Path is null", parentPath);
  }

  /**
   * Normal label + Normal rule + Test Parent + official Delta
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCaseType02T01() throws ApicWebServiceException {

    ReviewInput data = createInput(HEX_FILE, CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType(),
        FUNCTION_FOR_BOTH_START_AND_TEST_PARENT, 24706608778L, 19359903339L);
    data.getResultData().setParentResultId(24748494378L);
    data.setReviewType("O");
    data.setDeltaReviewType(DELTA_REVIEW_TYPE.DELTA_REVIEW.getDbType());
    data.setDeltaReview(true);
    ReviewResultEditorData editorData = performReview(data);
    loadReviewParams(editorData, rvwParamMap);
    CDRResultParameter resultParam =
        getReviewParam(rvwParamMap, PARAM_TEST_PARENT, editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_TAKEN_OVER, SCORE_0, resultParam.getReviewScore());
    assertEquals(SCORE_COLUMN_IS_NOT_COLOURED, true, isSet(resultParam.getChangeFlag(), SCORE_CLOUMN));
  }

  /**
   * Normal label + Normal rule + Test Parent + Start Delta
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCaseType02T02() throws ApicWebServiceException {

    ReviewInput data = createInput(HEX_FILE, CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType(),
        FUNCTION_FOR_BOTH_START_AND_TEST_PARENT, 24706608778L, 19359903339L);
    data.getResultData().setParentResultId(24748494378L);
    data.setReviewType("S");
    data.setDeltaReviewType(DELTA_REVIEW_TYPE.DELTA_REVIEW.getDbType());
    data.setDeltaReview(true);
    ReviewResultEditorData editorData = performReview(data);
    loadReviewParams(editorData, rvwParamMap);
    CDRResultParameter resultParam =
        getReviewParam(rvwParamMap, PARAM_TEST_PARENT, editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_TAKEN_OVER, SCORE_0, resultParam.getReviewScore());
    assertEquals(SCORE_COLUMN_IS_NOT_COLOURED, true, isSet(resultParam.getChangeFlag(), SCORE_CLOUMN));
  }

  /**
   * Normal label + Normal rule + Test Parent + Test Delta
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCaseType02T03() throws ApicWebServiceException {

    ReviewInput data = createInput(HEX_FILE, CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType(),
        FUNCTION_FOR_BOTH_START_AND_TEST_PARENT, 24706608778L, 19359903339L);
    data.getResultData().setParentResultId(24748494378L);
    data.setReviewType("T");
    data.setDeltaReviewType(DELTA_REVIEW_TYPE.DELTA_REVIEW.getDbType());
    data.setDeltaReview(true);
    ReviewResultEditorData editorData = performReview(data);
    loadReviewParams(editorData, rvwParamMap);
    CDRResultParameter resultParam =
        getReviewParam(rvwParamMap, PARAM_TEST_PARENT, editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_NOT_TAKEN_OVER, SCORE_7, resultParam.getReviewScore());
    assertEquals(CHANGE_FLAG_IS_NOT_ZERO, NO_CHANGE, resultParam.getChangeFlag());
  }

  /**
   * Normal label + Normal rule + Official Parent + Official Delta
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCaseType03T01() throws ApicWebServiceException {

    ReviewInput data = createInput(HEX_FILE, CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType(),
        "AccMon_aVehSetPoint, AccMon_aVehActVal,CEngDsT_VD,EpmRRS_AgDetect,CESys_OpmIf,NSC_DesValCalc", 24706608778L,
        24837600580L);
    data.getResultData().setParentResultId(24861714072L);
    data.setReviewType("O");
    data.setDeltaReviewType(DELTA_REVIEW_TYPE.DELTA_REVIEW.getDbType());
    data.setDeltaReview(true);
    data.setCdrReviewResult(new CDRReviewResult());
    data.getCdrReviewResult().setSdomPverVarName("MMD114A0CC1788");
    ReviewResultEditorData editorData = performReview(data);
    loadReviewParams(editorData, rvwParamMap);

    // Normal label + Normal rule + Parent(O) + Delta(O)+ Ready Series is YES in Both reviews
    CDRResultParameter resultParam =
        getReviewParam(rvwParamMap, PARAM_OFFICIAL_PARENT, editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_NOT_TAKEN_OVER, SCORE_8, resultParam.getReviewScore());
    assertEquals(CHANGE_FLAG_IS_NOT_ZERO, NO_CHANGE, resultParam.getChangeFlag());

    // Normal label + Normal rule + Parent(O) + Delta(O) + Different score
    CDRResultParameter result2Param =
        getReviewParam(rvwParamMap, "AccMon_tiCorrActv_C", editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_NOT_TAKEN_OVER, SCORE_5, result2Param.getReviewScore());
    assertEquals(CHANGE_FLAG_IS_NOT_ZERO, NO_CHANGE, result2Param.getChangeFlag());

    // Normal label + Normal rule + Parent(O) + Delta(O)+ Ready Series is YES in Parent + Rule changed with different
    // result
    CDRResultParameter result3Param =
        getReviewParam(rvwParamMap, PARAM2_OOFICIAL_PARENT, editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_TAKEN_OVER, SCORE_0, result3Param.getReviewScore());
    assertEquals(SCORE_COLUMN_IS_NOT_COLOURED, true, isSet(result3Param.getChangeFlag(), SCORE_CLOUMN));
    assertEquals(RESULT_COLUMN_IS_NOT_COLOURED, true, isSet(result3Param.getChangeFlag(), RESULT_CLOUMN));

    // Normal label + Normal rule + Parent(O) + Delta(O) + Rule changed with same result
    CDRResultParameter result4Param =
        getReviewParam(rvwParamMap, "CEngDsT_nMinEnaDynTst_C", editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_NOT_TAKEN_OVER, SCORE_9, result4Param.getReviewScore());
    assertEquals("Limit column is not coloured", true, isSet(result4Param.getChangeFlag(), UPPER_LIMIT_CLOUMN));

    // Normal label + Normal rule + Parent(O) + Delta(O) + Rule changed with different result
    CDRResultParameter result5Param =
        getReviewParam(rvwParamMap, PARAM3_OFFICIAL_PARENT, editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_TAKEN_OVER, SCORE_0, result5Param.getReviewScore());
    assertEquals(SCORE_COLUMN_IS_NOT_COLOURED, true, isSet(result5Param.getChangeFlag(), SCORE_CLOUMN));
    assertEquals(RESULT_COLUMN_IS_NOT_COLOURED, true, isSet(result5Param.getChangeFlag(), RESULT_CLOUMN));

    // Normal label + Normal rule + Parent(O) + Delta(O) + Rule changed - Ready series set to NO
    CDRResultParameter result6Param =
        getReviewParam(rvwParamMap, "EpmRRS_nEngMax_C", editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_TAKEN_OVER, SCORE_0, result6Param.getReviewScore());
    assertEquals(SCORE_COLUMN_IS_NOT_COLOURED, true, isSet(result6Param.getChangeFlag(), SCORE_CLOUMN));
    assertEquals(SERIES_COLUMN_IS_NOT_COLOURED, true, isSet(result6Param.getChangeFlag(), READY_SERIES_CLOUMN));

    // Normal label + Normal rule + Parent(O) + Delta(O) + Rule changed - Ready series set to YES
    CDRResultParameter result7Param =
        getReviewParam(rvwParamMap, PARAM4_OFFICIAL_PARENT, editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_NOT_TAKEN_OVER, SCORE_8, result7Param.getReviewScore());
    assertEquals(SCORE_COLUMN_IS_NOT_COLOURED, true, isSet(result7Param.getChangeFlag(), SCORE_CLOUMN));
    assertEquals(SERIES_COLUMN_IS_NOT_COLOURED, true, isSet(result7Param.getChangeFlag(), READY_SERIES_CLOUMN));

    // Normal label + Normal rule + Parent(O) + Delta(O) + Value Type : val_bul
    CDRResultParameter result8Param =
        getReviewParam(rvwParamMap, PARAM5_OFFICIAL_PARENT, editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_NOT_TAKEN_OVER, SCORE_9, result8Param.getReviewScore());
    assertEquals(CHANGE_FLAG_IS_NOT_ZERO, NO_CHANGE, result2Param.getChangeFlag());

    // Normal label + Normal rule + Parent(O) + Delta(O) + Value Type :Curve
    CDRResultParameter result9Param =
        getReviewParam(rvwParamMap, "CEngDsT_dtMinDynTst_CUR", editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_NOT_TAKEN_OVER, SCORE_9, result9Param.getReviewScore());
    assertEquals(CHANGE_FLAG_IS_NOT_ZERO, NO_CHANGE, result9Param.getChangeFlag());

    // Normal label + Normal rule + Parent(O) + Delta(O) + Value Type : Map
    CDRResultParameter result10Param =
        getReviewParam(rvwParamMap, "DATA_CESys_OpmIf.CESys_idxOpmCusAsg_M_VW", editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_NOT_TAKEN_OVER, SCORE_9, result10Param.getReviewScore());
    assertEquals(CHANGE_FLAG_IS_NOT_ZERO, NO_CHANGE, result10Param.getChangeFlag());


  }


  /**
   * Normal label + Normal rule + Official Parent + Start Delta
   *
   * @throws ApicWebServiceException error during service call
   */

  @Test
  public void testCaseType04T01() throws ApicWebServiceException {

    ReviewInput data = createInput(HEX_FILE, CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType(),
        "CEngDsT_VD,ACClntP_VD,AccMon_aVehSetPoint,AccMon_aVehActVal", 24706608778L, 24837600580L);

    data.getResultData().setParentResultId(24861714072L);
    data.setReviewType("S");
    data.setDeltaReviewType(DELTA_REVIEW_TYPE.DELTA_REVIEW.getDbType());
    data.setDeltaReview(true);

    ReviewResultEditorData editorData = performReview(data);
    loadReviewParams(editorData, rvwParamMap);


    // Normal label + Normal rule + Parent(O) + Delta(S) Rule changed - Ready series set to YES
    CDRResultParameter resultParam =
        getReviewParam(rvwParamMap, PARAM4_OFFICIAL_PARENT, editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_NOT_TAKEN_OVER, SCORE_8, resultParam.getReviewScore());
    assertEquals(SCORE_COLUMN_IS_NOT_COLOURED, true, isSet(resultParam.getChangeFlag(), SCORE_CLOUMN));
    assertEquals(SERIES_COLUMN_IS_NOT_COLOURED, true, isSet(resultParam.getChangeFlag(), READY_SERIES_CLOUMN));

    // Normal label + Normal rule + Parent(O) + Delta(S) + Rule changed with different result
    CDRResultParameter result2Param =
        getReviewParam(rvwParamMap, PARAM3_OFFICIAL_PARENT, editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_TAKEN_OVER, SCORE_0, result2Param.getReviewScore());
    assertEquals(SCORE_COLUMN_IS_NOT_COLOURED, true, isSet(result2Param.getChangeFlag(), SCORE_CLOUMN));
    assertEquals(RESULT_COLUMN_IS_NOT_COLOURED, true, isSet(result2Param.getChangeFlag(), RESULT_CLOUMN));

    // Normal label + Normal rule + Parent(O) + Delta(S)
    CDRResultParameter result3Param =
        getReviewParam(rvwParamMap, "AirC_pClntIni_C", editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_NOT_TAKEN_OVER, SCORE_7, result3Param.getReviewScore());
    assertEquals(SCORE_COLUMN_IS_NOT_COLOURED, true, isSet(result3Param.getChangeFlag(), SCORE_CLOUMN));

    // Normal label + Normal rule + Parent(O) + Delta(S) + Ready Series is YES in both review
    CDRResultParameter result4Param =
        getReviewParam(rvwParamMap, PARAM_OFFICIAL_PARENT, editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_NOT_TAKEN_OVER, SCORE_8, result4Param.getReviewScore());
    assertEquals(CHANGE_FLAG_IS_NOT_ZERO, NO_CHANGE, result4Param.getChangeFlag());

    // Normal label + Normal rule + Parent(O) + Delta(S) +Ready Series + Rule changed with diff result
    CDRResultParameter result5Param =
        getReviewParam(rvwParamMap, PARAM2_OOFICIAL_PARENT, editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_TAKEN_OVER, SCORE_0, result5Param.getReviewScore());
    assertEquals(SCORE_COLUMN_IS_NOT_COLOURED, true, isSet(result5Param.getChangeFlag(), SCORE_CLOUMN));
    assertEquals(RESULT_COLUMN_IS_NOT_COLOURED, true, isSet(result5Param.getChangeFlag(), RESULT_CLOUMN));

  }


  /**
   * Normal label + Normal rule + Official Parent + Test Delta
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCaseType05T01() throws ApicWebServiceException {

    ReviewInput data = createInput(HEX_FILE, CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType(),
        "CEngDsT_VD,ACClntP_VD,AccMon_aVehSetPoint,AccMon_aVehActVal", 24706608778L, 24837600580L);

    data.getResultData().setParentResultId(24861714072L);
    data.setReviewType("T");
    data.setDeltaReviewType(DELTA_REVIEW_TYPE.DELTA_REVIEW.getDbType());
    data.setDeltaReview(true);
    ReviewResultEditorData editorData = performReview(data);
    loadReviewParams(editorData, rvwParamMap);

    // Normal label + Normal rule + Parent(O) + Delta(T) Rule changed - Ready series set to YES
    CDRResultParameter resultParam =
        getReviewParam(rvwParamMap, PARAM4_OFFICIAL_PARENT, editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_NOT_TAKEN_OVER, SCORE_8, resultParam.getReviewScore());
    assertEquals(SCORE_COLUMN_IS_NOT_COLOURED, true, isSet(resultParam.getChangeFlag(), SCORE_CLOUMN));
    assertEquals(SERIES_COLUMN_IS_NOT_COLOURED, true, isSet(resultParam.getChangeFlag(), READY_SERIES_CLOUMN));

    // Normal label + Normal rule + Parent(O) + Delta(T) + Rule changed with different result
    CDRResultParameter result2Param =
        getReviewParam(rvwParamMap, PARAM3_OFFICIAL_PARENT, editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_TAKEN_OVER, SCORE_0, result2Param.getReviewScore());
    assertEquals(SCORE_COLUMN_IS_NOT_COLOURED, true, isSet(result2Param.getChangeFlag(), SCORE_CLOUMN));
    assertEquals(RESULT_COLUMN_IS_NOT_COLOURED, true, isSet(result2Param.getChangeFlag(), RESULT_CLOUMN));

    // Normal label + Normal rule + Parent(O) + Delta(T)
    CDRResultParameter result3Param =
        getReviewParam(rvwParamMap, "AirC_pClntIni_C", editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_NOT_TAKEN_OVER, SCORE_7, result3Param.getReviewScore());
    assertEquals(SCORE_COLUMN_IS_NOT_COLOURED, true, isSet(result3Param.getChangeFlag(), SCORE_CLOUMN));

    //// Normal label + Normal rule + Parent(O) + Delta(T) + Ready Series is YES in both review
    CDRResultParameter result4Param =
        getReviewParam(rvwParamMap, PARAM_OFFICIAL_PARENT, editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_NOT_TAKEN_OVER, SCORE_8, result4Param.getReviewScore());
    assertEquals("Score is over taken", NO_CHANGE, result4Param.getChangeFlag());

    // Normal label + Normal rule + Parent(O) + Delta(T) +Ready Series + Rule changed with diff result
    CDRResultParameter result5Param =
        getReviewParam(rvwParamMap, PARAM2_OOFICIAL_PARENT, editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_TAKEN_OVER, SCORE_0, result5Param.getReviewScore());
    assertEquals(SCORE_COLUMN_IS_NOT_COLOURED, true, isSet(result5Param.getChangeFlag(), SCORE_CLOUMN));
    assertEquals(RESULT_COLUMN_IS_NOT_COLOURED, true, isSet(result5Param.getChangeFlag(), RESULT_CLOUMN));


  }


  /**
   * Normal label + Normal rule + Official Parent + Official Delta + CalData is Changed
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCaseType06T01() throws ApicWebServiceException {

    ReviewInput data = createInput(CAL_FILE, CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType(),
        "EpmCrS_Plaus,CEngDsT_VD,UEGO_CJ135Ctl", 24706608778L, 24837600580L);

    data.getResultData().setParentResultId(24861714072L);
    data.setReviewType("O");
    CDRReviewResult cdrReviewResult = new CDRReviewResult();
    data.setCdrReviewResult(cdrReviewResult);
    data.getCdrReviewResult().setSdomPverVarName("MMD114A0CC1788");
    data.setDeltaReviewType(DELTA_REVIEW_TYPE.DELTA_REVIEW.getDbType());
    data.setDeltaReview(true);
    ReviewResultEditorData editorData = performReview(data);
    loadReviewParams(editorData, rvwParamMap);

    // Normal label + Normal rule + Official Parent + Official Delta + Ready Series IS YES +CalData is Changed
    CDRResultParameter resultParam =
        getReviewParam(rvwParamMap, "EpmCrS_facGapCrit1_C", editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_TAKEN_OVER, SCORE_0, resultParam.getReviewScore());
    assertEquals(CHECKED_COLUMN_IS_NOT_COLOURED, true, isSet(resultParam.getChangeFlag(), CHECKED_CLOUMN));
    assertEquals(SCORE_COLUMN_IS_NOT_COLOURED, true, isSet(resultParam.getChangeFlag(), SCORE_CLOUMN));

    // Normal label + Normal rule + Parent(O) + Delta(O) +CalData Change
    CDRResultParameter result2Param =
        getReviewParam(rvwParamMap, PARAM_CHANGED_CAL_DATA, editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_TAKEN_OVER, SCORE_0, result2Param.getReviewScore());
    assertEquals(CHECKED_COLUMN_IS_NOT_COLOURED, true, isSet(result2Param.getChangeFlag(), CHECKED_CLOUMN));
    assertEquals(SCORE_COLUMN_IS_NOT_COLOURED, true, isSet(result2Param.getChangeFlag(), SCORE_CLOUMN));

    // Normal label + Normal rule + Parent(O) + Delta(O) + value type :val_bul +CalData Change
    CDRResultParameter result3Param =
        getReviewParam(rvwParamMap, PARAM5_OFFICIAL_PARENT, editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_TAKEN_OVER, SCORE_0, result3Param.getReviewScore());
    assertEquals(CHECKED_COLUMN_IS_NOT_COLOURED, true, isSet(result3Param.getChangeFlag(), CHECKED_CLOUMN));
    assertEquals(SCORE_COLUMN_IS_NOT_COLOURED, true, isSet(result3Param.getChangeFlag(), SCORE_CLOUMN));

    // Normal label + Normal rule + Parent(O) + Delta(O) + value type :Curve +CalData Change
    CDRResultParameter result4Param =
        getReviewParam(rvwParamMap, "CEngDsT_dtMinDynTst_CUR", editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_TAKEN_OVER, SCORE_0, result4Param.getReviewScore());
    assertEquals(CHECKED_COLUMN_IS_NOT_COLOURED, true, isSet(result4Param.getChangeFlag(), CHECKED_CLOUMN));
    assertEquals(SCORE_COLUMN_IS_NOT_COLOURED, true, isSet(result4Param.getChangeFlag(), SCORE_CLOUMN));


  }

  /**
   * Normal label + Normal rule + Official Parent + Start Delta + CalData is Changed
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCaseType06T03() throws ApicWebServiceException {

    ReviewInput data = createInput(CAL_FILE, CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType(), "EpmCrS_Plaus",
        24706608778L, 24837600580L);

    data.getResultData().setParentResultId(24861714072L);
    data.setReviewType("S");
    data.setDeltaReviewType(DELTA_REVIEW_TYPE.DELTA_REVIEW.getDbType());
    data.setDeltaReview(true);
    ReviewResultEditorData editorData = performReview(data);
    loadReviewParams(editorData, rvwParamMap);
    CDRResultParameter resultParam =
        getReviewParam(rvwParamMap, PARAM_CHANGED_CAL_DATA, editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_TAKEN_OVER, SCORE_0, resultParam.getReviewScore());
    assertEquals(CHECKED_COLUMN_IS_NOT_COLOURED, true, isSet(resultParam.getChangeFlag(), CHECKED_CLOUMN));
    assertEquals(SCORE_COLUMN_IS_NOT_COLOURED, true, isSet(resultParam.getChangeFlag(), SCORE_CLOUMN));
  }

  /**
   * Normal label + Normal rule + Official Parent + Test Delta + CalData is Changed
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCaseType06T04() throws ApicWebServiceException {

    ReviewInput data = createInput(CAL_FILE, CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType(), "EpmCrS_Plaus",
        24706608778L, 24837600580L);

    data.getResultData().setParentResultId(24861714072L);
    data.setReviewType("T");
    data.setDeltaReviewType(DELTA_REVIEW_TYPE.DELTA_REVIEW.getDbType());
    data.setDeltaReview(true);
    ReviewResultEditorData editorData = performReview(data);
    loadReviewParams(editorData, rvwParamMap);
    CDRResultParameter resultParam =
        getReviewParam(rvwParamMap, PARAM_CHANGED_CAL_DATA, editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_TAKEN_OVER, SCORE_0, resultParam.getReviewScore());
    assertEquals(CHECKED_COLUMN_IS_NOT_COLOURED, true, isSet(resultParam.getChangeFlag(), CHECKED_CLOUMN));
    assertEquals(SCORE_COLUMN_IS_NOT_COLOURED, true, isSet(resultParam.getChangeFlag(), SCORE_CLOUMN));
  }

  /**
   * Normal label + Normal rule + Start Parent + start Delta
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCaseType07T02() throws ApicWebServiceException {

    ReviewInput data = createInput(HEX_FILE, CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType(),
        FUNCTION_FOR_BOTH_START_AND_TEST_PARENT, 24706608778L, 19359903339L);
    data.getResultData().setParentResultId(24815235413L);
    data.setReviewType("S");
    data.setDeltaReviewType(DELTA_REVIEW_TYPE.DELTA_REVIEW.getDbType());
    data.setDeltaReview(true);
    ReviewResultEditorData editorData = performReview(data);
    loadReviewParams(editorData, rvwParamMap);
    CDRResultParameter resultParam =
        getReviewParam(rvwParamMap, PARAM_START_PARENT, editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_NOT_TAKEN_OVER, SCORE_7, resultParam.getReviewScore());
    assertEquals(CHANGE_FLAG_IS_NOT_ZERO, NO_CHANGE, resultParam.getChangeFlag());
  }

  /**
   * Normal label + Normal rule + Start Parent + Test Delta
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCaseType07T03() throws ApicWebServiceException {

    ReviewInput data = createInput(HEX_FILE, CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType(),
        FUNCTION_FOR_BOTH_START_AND_TEST_PARENT, 24706608778L, 19359903339L);
    data.getResultData().setParentResultId(24815235413L);
    data.setReviewType("T");
    data.setDeltaReviewType(DELTA_REVIEW_TYPE.DELTA_REVIEW.getDbType());
    data.setDeltaReview(true);
    ReviewResultEditorData editorData = performReview(data);
    loadReviewParams(editorData, rvwParamMap);
    CDRResultParameter resultParam =
        getReviewParam(rvwParamMap, PARAM_START_PARENT, editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_NOT_TAKEN_OVER, SCORE_7, resultParam.getReviewScore());
    assertEquals(CHANGE_FLAG_IS_NOT_ZERO, NO_CHANGE, resultParam.getChangeFlag());
  }


  /**
   * Normal label + Normal rule + Start Parent + official Delta
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCaseType07T01() throws ApicWebServiceException {

    ReviewInput data = createInput(HEX_FILE, CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType(),
        FUNCTION_FOR_BOTH_START_AND_TEST_PARENT, 24706608778L, 19359903339L);
    data.getResultData().setParentResultId(24815235413L);
    data.setReviewType("O");
    data.setDeltaReviewType(DELTA_REVIEW_TYPE.DELTA_REVIEW.getDbType());
    data.setDeltaReview(true);
    ReviewResultEditorData editorData = performReview(data);
    loadReviewParams(editorData, rvwParamMap);
    CDRResultParameter resultParam =
        getReviewParam(rvwParamMap, PARAM_START_PARENT, editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_NOT_TAKEN_OVER, SCORE_7, resultParam.getReviewScore());
    assertEquals(CHANGE_FLAG_IS_NOT_ZERO, NO_CHANGE, resultParam.getChangeFlag());
  }

  /**
   * Complience/SSD label + Complience rule + Official Parent + Official Delta
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCaseType01T01() throws ApicWebServiceException {

    ReviewInput data =
        createInput(HEX_FILE, CDRConstants.CDR_SOURCE_TYPE.COMPLI_PARAM.getDbType(), "", 24706608778L, 24837600580L);
    data.getResultData().setParentResultId(25786888999L);
    data.setReviewType("O");
    data.setDeltaReviewType(DELTA_REVIEW_TYPE.DELTA_REVIEW.getDbType());
    data.setDeltaReview(true);
    ReviewResultEditorData editorData = performReview(data);
    loadReviewParams(editorData, rvwParamMap);

    // Compli label + Complience rule + CompRule - CSSD
    CDRResultParameter resultParam =
        getReviewParam(rvwParamMap, "AirCtl_pAirLo_C", editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_TAKEN_OVER, SCORE_0, resultParam.getReviewScore());
    assertEquals(SCORE_COLUMN_IS_NOT_COLOURED, true, isSet(resultParam.getChangeFlag(), SCORE_CLOUMN));

    // Compli label + Complience rule - NO_Rule
    CDRResultParameter result2Param = getReviewParam(rvwParamMap, "DATA_SysOpmVeh.SysOpmVeh_ctMaxStrtFailAt_C_VW",
        editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_TAKEN_OVER, SCORE_0, result2Param.getReviewScore());
    assertEquals(SCORE_COLUMN_IS_NOT_COLOURED, true, isSet(result2Param.getChangeFlag(), SCORE_CLOUMN));

    // Compli label + Complience rule - SSD2RV
    CDRResultParameter result3Param =
        getReviewParam(rvwParamMap, "SCRFFC_stChkRdcAgQChklReqMsk_C", editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_TAKEN_OVER, SCORE_0, result3Param.getReviewScore());
    assertEquals(SCORE_COLUMN_IS_NOT_COLOURED, true, isSet(result3Param.getChangeFlag(), SCORE_CLOUMN));

    // QSSD label + Complience rule - NO_Rule
    CDRResultParameter result4Param =
        getReviewParam(rvwParamMap, "NSC_numBrick2_C", editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_NOT_TAKEN_OVER, SCORE_9, result4Param.getReviewScore());
    assertEquals("Score is over taken", NO_CHANGE, result4Param.getChangeFlag());

  }

  /**
   * Complience/SSD label + Complience rule + Official Parent + Official Delta + calData changed
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testCaseType01T02() throws ApicWebServiceException {

    ReviewInput data =
        createInput(CAL_FILE2, CDRConstants.CDR_SOURCE_TYPE.COMPLI_PARAM.getDbType(), "", 24706608778L, 24837600580L);
    data.getResultData().setParentResultId(25786888999L);
    data.setReviewType("O");
    data.setDeltaReviewType(DELTA_REVIEW_TYPE.DELTA_REVIEW.getDbType());
    data.setDeltaReview(true);
    data.setCdrReviewResult(new CDRReviewResult());
    data.getCdrReviewResult().setSdomPverVarName("MMD114A0CC1788");
    ReviewResultEditorData editorData = performReview(data);
    loadReviewParams(editorData, rvwParamMap);
    CDRResultParameter resultParam =
        getReviewParam(rvwParamMap, "UEGO_stCJ135Ctl_C", editorData.getReviewResult().getName());
    assertEquals(SCORE_IS_TAKEN_OVER, SCORE_0, resultParam.getReviewScore());
    assertEquals(RESULT_COLUMN_IS_NOT_COLOURED, true, isSet(resultParam.getChangeFlag(), RESULT_CLOUMN));
    assertEquals(CHECKED_COLUMN_IS_NOT_COLOURED, true, isSet(resultParam.getChangeFlag(), CHECKED_CLOUMN));
  }

  private static ReviewResultEditorData performReview(final ReviewInput inputData) throws ApicWebServiceException {

    ReviewOutput reviewSummary = new ReviewServiceClient().performReview(inputData);
    ReviewResultEditorData resultData =
        new CDRReviewResultServiceClient().getRvwResultEditorData(reviewSummary.getCdrResult().getId(), null);

    validateGeneralReviewInfo(inputData, reviewSummary, resultData);

    return resultData;
  }

  private static void validateGeneralReviewInfo(final ReviewInput reviewInput, final ReviewOutput reviewSummary,
      final ReviewResultEditorData resultData) {

    checkReviewSummary(reviewSummary);

    validateReviewResults(reviewInput, resultData);
    validateRvwPartcipant(reviewInput, resultData);
  }


  private static void loadReviewParams(final ReviewResultEditorData resultData,
      final Map<String, CDRResultParameter> rvwParamMapToLoad) {
    resultData.getParamMap().values().forEach(rp -> rvwParamMapToLoad.put(rp.getName(), rp));
  }

  private CDRResultParameter getReviewParam(final Map<String, CDRResultParameter> rvwParamMap1, final String paramName,
      final String resultName) {
    CDRResultParameter rvwParam = rvwParamMap1.get(paramName);
    assertNotNull(paramName + " available in " + resultName, rvwParam);
    return rvwParam;
  }

  private boolean isSet(final Long long1, final int index) {
    BitSet bitSet = BitSet.valueOf(new long[] { long1 });
    return bitSet.get(index);
  }

}
