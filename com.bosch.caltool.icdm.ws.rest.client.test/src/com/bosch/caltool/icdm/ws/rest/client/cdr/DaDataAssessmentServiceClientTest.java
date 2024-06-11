package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.ParameterType;
import com.bosch.caltool.icdm.model.cdr.CompliResValues;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.model.cdr.DaDataAssessment;
import com.bosch.caltool.icdm.model.cdr.DaWpResp;
import com.bosch.caltool.icdm.model.cdr.QSSDResValues;
import com.bosch.caltool.icdm.model.dataassessment.DaCompareHexParam;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentCompareHexData;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentQuestionnaires;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentReport;
import com.bosch.caltool.icdm.model.general.ExternalLinkInfo;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for DaDataAssessment
 *
 * @author say8cob
 */
public class DaDataAssessmentServiceClientTest extends AbstractRestClientTest {


  /**
   *
   */
  private static final String QNAIRE_RESPONSE_VAR_LINK_TYPE = "QNAIRE_RESPONSE_VAR";
  /**
   *
   */
  private static final String QNAIRE_LINK_DISP_TEXT_FOR_UI =
      "Abhilash, G (RBEI/EAI4)->WP1->General Questions (Version 2.1)";
  /**
   *
   */
  private static final String QNAIRE_LINK = "icdm:qnrrespid,31886187995-31453509784-31453509793";
  /**
   *
   */
  private static final String QNAIRE_RESP_LINK_DISP_TEXT =
      "Questionnaire Response->AUDI->Diesel Engine->PC - Passenger Car->EDC17->Test_DatAsses_Baseline (Version 1)->TestVariant1->Abhilash, G (RBEI/EAI4)->WP1->General Questions (Version 2.1)";
  /**
   *
   */
  private static final long QNAIRE_RESP_VAR_ID = 31886187996L;
  /**
   * Link icdm:cdrid,34193616286-31453509784-31453509793 31883088178
   */
  private static final long CDR_RESULT_ID = 34193616286L;
  /**
   *
   */
  private static final String FUNC_NAME = "AccMon_aVehSetPoint";
  /**
   *
   */
  private static final String FUN_VERS = "7.5.0";
  /**
   *
   */
  private static final long PARAM_ID = 17639562L;
  /**
   *
   */
  private static final String PARAM_NAME = "AccMon_aPtdMax_C";
  /**
   *
   */
  private static final long RVW_PARAM_ID = 31886187980L;
  /**
   *
   */
  private static final String LATEST_FUNC_VERS = "MC50_DISCR";
  /**
   *
   */
  private static final String RVW_NAME = "Test official review";
  /**
   *
   */
  private static final long QNAIRE_RESP_ID = 31886187995L;
  /**
   *
   */
  private static final String QNAIRE_REVIEWED_DATE = "2022-05-16 00:00:00 000";
  /**
   *
   */
  private static final String QNAIRE_RESP_VERS_NAME = "Working Set";
  /**
   *
   */
  private static final long QNAIRE_RESP_VERS_ID = 31886187997L;
  /**
   *
   */
  private static final String QNAIRE_RESP_NAME = "General Questions (Version 2.1)";
  /**
   *
   */
  private static final String WP_NAME = "WP1";
  /**
   *
   */
  private static final long WP_ID = 31453509806L;
  /**
   *
   */
  private static final String RB_RESP_TYPE = "R";
  /**
   *
   */
  private static final String RESP_NAME = "Abhilash, G (RBEI/EAI4)";
  /**
   *
   */
  private static final long RESP_ID = 31883088213L;
  /**
   *
   */
  private static final String RESP_ALIAS_NAME = "Abhilash.G@in.bosch.com";
  /**
   *
   */
  private static final long PIDC_VERS_ID = 31453509784L;
  /**
   *
   */
  private static final String PIDC_NAME = "Test_DatAsses_Baseline";
  /**
   * A2l file link: icdm:a2lid,34190517982-2068555001
   */
  private static final long PIDC_ID = 31453509782L;
  /**
   * A2l file link: icdm:a2lid,34190517982-2068555001
   */
  private static final long A2L_FILE_ID = 274303074951L;
  /**
   *
   */
  private static final String WP_DEFN_VERS_NAME = "2 : Version 1";
  /**
   *
   */
  private static final long WP_DEFN_VERS_ID = 31883088215L;
  /**
   *
   */
  private static final String HEX_FILE_NAME = "HEX_MMD114A0CC1788_MC50_DISCR_LC.hex";
  /**
   *
   */
  private static final String A2L_FILE_NAME = "A2L_MMD114A0CC1788_MC50_DISCR.A2L";
  /**
   *
   */
  private static final long PIDC_A2L_ID = 31453509801L;
  /**
   *
   */
  private static final String VARIANT_NAME = "TestVariant1";
  /**
   *
   */
  private static final long PIDC_VAR_ID = 31453509793L;
  /**
   *
   */
  private static final String PIDC_VERS_NAME = "Test_DatAsses_Baseline (Version 1)";
  /**
   *
   */
  private static final String BASELINE_DESCRIPTION = "Created for testing create baseline service. Test-2";
  /**
   *
   */
  private static final String BASELINE_NAME = "Test_Baseline_2";
  /**
   * Error message
   */
  private static final String ERROR_IN_WS_CALL = "Error in WS call";
  /**
   * Data Assessment ID
   */
  private static final Long DADATAASSESSMENT_ID = 34589457144L;
  /**
   * Expected exception
   */
  public final ExpectedException excepThrown = ExpectedException.none();

  /**
   * Test method for {@link DaDataAssessmentServiceClientTest#get()}
   */
  @Test
  public void testGet() {
    DaDataAssessmentServiceClient servClient = new DaDataAssessmentServiceClient();
    try {
      DaDataAssessment ret = servClient.get(DADATAASSESSMENT_ID);
      assertFalse("Response should not be null", (ret == null));
      testDaDataAssessment(ret);
    }
    catch (Exception excep) {
      LOG.error(ERROR_IN_WS_CALL, excep);
    }
  }

  /**
   * Test method for {@link DaDataAssessmentServiceClient#create(DataAssessmentReport)}
   */

  @Test
  public void testCreate() {
    DaDataAssessmentServiceClient servClient = new DaDataAssessmentServiceClient();
    try {
      DataAssessmentReport obj = new DataAssessmentReport();
      obj.setBaselineName(BASELINE_NAME);
      obj.setDescription(BASELINE_DESCRIPTION);
      obj.setTypeOfAssignment(CDRConstants.TYPE_OF_ASSESSMENT.DEVELOPMENT.getDbType());
      obj.setPidcVersId(PIDC_VERS_ID);
      obj.setPidcVersName(PIDC_VERS_NAME);
      obj.setPidcVariantId(PIDC_VAR_ID);
      obj.setPidcVariantName(VARIANT_NAME);
      obj.setPidcA2lId(PIDC_A2L_ID);
      obj.setA2lFileName(A2L_FILE_NAME);
      obj.setHexFileName(HEX_FILE_NAME);
      obj.setWpDefnVersId(WP_DEFN_VERS_ID);
      obj.setWpDefnVersName(WP_DEFN_VERS_NAME);
      obj.setA2lFileId(A2L_FILE_ID);
      obj.setAllParametersReviewed(false);
      obj.setAllQnairesAnswered(false);
      obj.setHexFileDataEqualWithDataReviews(true);
      obj.setPidcId(PIDC_ID);
      obj.setPidcName(PIDC_NAME);
      obj.setReadyForSeries(false);
      obj.setDataAssmntCompHexData(fetchCompHexData());
      obj.setDataAssmntWps(fetchDaWpRespData());
      obj.setDataAssmntQnaires(fetchDaQnaireResps());

      // Invoke create method
      DaDataAssessment createdObj = servClient.createBaseline(obj);
      assertNotNull("object not null", createdObj);
      testDaDataAssessment(createdObj);

      DataAssessmentReport dataAssessmentBaseline = servClient.getDataAssessmentBaseline(createdObj.getId());
      assertNotNull("Baseline should not be null", dataAssessmentBaseline);
      testDataAssessmenReportModel(dataAssessmentBaseline);
    }
    catch (Exception excep) {
      LOG.error(ERROR_IN_WS_CALL, excep);
      assertNull(ERROR_IN_WS_CALL, excep);
    }
  }

  /**
   * @param dataAssessmentBaseline
   */
  private void testDataAssessmenReportModel(final DataAssessmentReport dataAssessmentBaseline) {
    assertEquals("BaselineName should be equal", BASELINE_NAME, dataAssessmentBaseline.getBaselineName());
    assertEquals("Description should be equal", BASELINE_DESCRIPTION, dataAssessmentBaseline.getDescription());
    assertEquals("TypeOfAssignment should be equal", CDRConstants.TYPE_OF_ASSESSMENT.DEVELOPMENT.getDbType(),
        dataAssessmentBaseline.getTypeOfAssignment());

    assertEquals("PIDC Id should be equal", PIDC_ID, dataAssessmentBaseline.getPidcId().longValue());
    assertEquals("PIDC Name should be equal", PIDC_NAME, dataAssessmentBaseline.getPidcName());

    assertEquals("PidcVersId should be equal", PIDC_VERS_ID, dataAssessmentBaseline.getPidcVersId().longValue());
    assertEquals("PidcVersFullname should be equal", PIDC_VERS_NAME, dataAssessmentBaseline.getPidcVersName());

    assertEquals("VariantId should be equal", PIDC_VAR_ID, dataAssessmentBaseline.getPidcVariantId().longValue());
    assertEquals("VariantName should be equal", VARIANT_NAME, dataAssessmentBaseline.getPidcVariantName());

    assertEquals("PidcA2lId should be equal", PIDC_A2L_ID, dataAssessmentBaseline.getPidcA2lId().longValue());
    assertEquals("A2lFilename should be equal", A2L_FILE_NAME, dataAssessmentBaseline.getA2lFileName());
    assertEquals("A2l File Id should be equal", A2L_FILE_ID, dataAssessmentBaseline.getA2lFileId().longValue());

    assertEquals("WpDefnVersId should be equal", WP_DEFN_VERS_ID, dataAssessmentBaseline.getWpDefnVersId().longValue());
    assertEquals("WpDefnVersName should be equal", WP_DEFN_VERS_NAME, dataAssessmentBaseline.getWpDefnVersName());

    assertEquals("HEX File name should be equal", HEX_FILE_NAME, dataAssessmentBaseline.getHexFileName());

    assertTrue("HEX value should be equal to Reviewed value",
        dataAssessmentBaseline.isHexFileDataEqualWithDataReviews());
    assertFalse("All Parameters in RB Responsibility should be reiewed",
        dataAssessmentBaseline.isAllParametersReviewed());
    assertFalse("All Questionnares should be answered", dataAssessmentBaseline.isAllQnairesAnswered());
    assertFalse("Ready for series should be true", dataAssessmentBaseline.isReadyForSeries());

    testWPRespDetails(dataAssessmentBaseline);
    testQnaireResp(dataAssessmentBaseline);
    testCompHexData(dataAssessmentBaseline);
  }


  /**
   * @param dataAssessmentBaseline
   */
  private void testWPRespDetails(final DataAssessmentReport dataAssessmentBaseline) {
    assertNotNull("WP Resp should not be null", dataAssessmentBaseline.getDataAssmntWps());
    DaWpResp daWpResp = dataAssessmentBaseline.getDataAssmntWps().iterator().next();

    assertEquals("A2l Resp Alias Name should be equal", RESP_ALIAS_NAME, daWpResp.getA2lRespAliasName());
    assertEquals("Responsibility Id should be equal", RESP_ID, daWpResp.getA2lRespId().longValue());
    assertEquals("Responsibility Name should be equal", RESP_NAME, daWpResp.getA2lRespName());
    assertEquals("Responsibility type should be equal", RB_RESP_TYPE, daWpResp.getA2lRespType());
    assertEquals("WP ID should be equal", WP_ID, daWpResp.getA2lWpId().longValue());
    assertEquals("WP Name should be equal", WP_NAME, daWpResp.getA2lWpName());
    assertEquals("HEX value should be equal to Reviewed value", ApicConstants.CODE_YES, daWpResp.getHexRvwEqualFlag());
    assertEquals("Parameters reviewed Flag should be No", ApicConstants.CODE_NO, daWpResp.getParameterReviewedFlag());
    assertEquals("WP Finished flag should be No", ApicConstants.CODE_NO, daWpResp.getWpFinishedFlag());
    assertEquals("Questionnaire answered Flag should be No", ApicConstants.CODE_NO, daWpResp.getQnairesAnsweredFlag());
    assertEquals("Ready for series Flag should be No", ApicConstants.CODE_NO, daWpResp.getWpReadyForProductionFlag());
  }

  /**
   * @return
   */
  private Set<DaWpResp> fetchDaWpRespData() {
    Set<DaWpResp> dataAssmntWps = new HashSet<>();
    DaWpResp daWpResp = new DaWpResp();
    daWpResp.setA2lRespAliasName(RESP_ALIAS_NAME);
    daWpResp.setA2lRespId(CommonUtils.getBigdecimalFromLong(RESP_ID));
    daWpResp.setA2lRespName(RESP_NAME);
    daWpResp.setA2lRespType(RB_RESP_TYPE);
    daWpResp.setA2lWpId(CommonUtils.getBigdecimalFromLong(WP_ID));
    daWpResp.setA2lWpName(WP_NAME);
    daWpResp.setHexRvwEqualFlag(ApicConstants.CODE_YES);
    daWpResp.setParameterReviewedFlag(ApicConstants.CODE_NO);
    daWpResp.setQnairesAnsweredFlag(ApicConstants.CODE_NO);
    daWpResp.setWpFinishedFlag(ApicConstants.CODE_NO);
    daWpResp.setWpReadyForProductionFlag(ApicConstants.CODE_NO);
    dataAssmntWps.add(daWpResp);
    return dataAssmntWps;
  }


  /**
   * @param dataAssessmentBaseline
   */
  private void testQnaireResp(final DataAssessmentReport dataAssessmentBaseline) {
    assertNotNull("Questionnaires should not be null", dataAssessmentBaseline.getDataAssmntQnaires());
    DataAssessmentQuestionnaires daQnaire = dataAssessmentBaseline.getDataAssmntQnaires().iterator().next();

    assertEquals("Responsibility Id should be equal", RESP_ID, daQnaire.getA2lRespId().longValue());
    assertEquals("WP ID should be equal", WP_ID, daQnaire.getA2lWpId().longValue());

    assertEquals("Responsibility Name should be equal", RESP_NAME, daQnaire.getA2lRespName());
    assertEquals("WP Name should be equal", WP_NAME, daQnaire.getA2lWpName());

    assertFalse("Qnaire Baseline existing flag should be False", daQnaire.isQnaireBaselineExisting());
    assertFalse("Qnaire Ready for Production flag should be False", daQnaire.isQnaireReadyForProd());
    assertEquals("Positive answer count should be equal", 0, daQnaire.getQnairePositiveAnsCount());
    assertEquals("Negative answer count should be equal", 0, daQnaire.getQnaireNegativeAnsCount());
    assertEquals("Neutral answer count should be equal", 0, daQnaire.getQnaireNeutralAnsCount());
    assertEquals("Qnaire Response Id should be equal", QNAIRE_RESP_ID, daQnaire.getQnaireRespId().longValue());
    assertEquals("Qnaire Response Name should be equal", QNAIRE_RESP_NAME, daQnaire.getQnaireRespName());
    assertEquals("Qnaire Response Version Id should be equal", QNAIRE_RESP_VERS_ID,
        daQnaire.getQnaireRespVersId().longValue());
    assertEquals("Qnaire Response Version Name should be equal", QNAIRE_RESP_VERS_NAME,
        daQnaire.getQnaireRespVersName());
    assertEquals("Qnaire Reviewed Date should be equal", "2022-05-16 05:30:00 000", daQnaire.getQnaireReviewedDate());

    ExternalLinkInfo link = daQnaire.getQnaireBaselineLink();
    assertEquals("Qnaire Response Display Text in link should be equal", QNAIRE_RESP_LINK_DISP_TEXT,
        link.getDisplayText());
    assertEquals("Qnaire Response ID in link should be equal", QNAIRE_RESP_ID, link.getLinkableObjId().longValue());
    assertEquals("Link type should be equal", QNAIRE_RESPONSE_VAR_LINK_TYPE, link.getLinkType());
    assertEquals("URL in link should be equal", QNAIRE_LINK, link.getUrl());
    assertEquals("Qnaire Link Display text should be equal", QNAIRE_LINK_DISP_TEXT_FOR_UI,
        daQnaire.getQnaireBaselineLinkDisplayText());

  }


  /**
   * @return
   * @throws ApicWebServiceException
   */
  private Set<DataAssessmentQuestionnaires> fetchDaQnaireResps() {
    Set<DataAssessmentQuestionnaires> dataAssmntQnaires = new HashSet<>();
    DataAssessmentQuestionnaires qnaires = new DataAssessmentQuestionnaires();
    qnaires.setA2lRespId(RESP_ID);
    qnaires.setA2lRespName(RESP_NAME);
    qnaires.setA2lWpId(WP_ID);
    qnaires.setA2lWpName(WP_NAME);
    qnaires.setQnaireBaselineExisting(false);
    qnaires.setQnaireNegativeAnsCount(0);
    qnaires.setQnaireNeutralAnsCount(0);
    qnaires.setQnairePositiveAnsCount(0);
    qnaires.setQnaireReadyForProd(false);
    qnaires.setQnaireRespId(QNAIRE_RESP_ID);
    qnaires.setQnaireRespName(QNAIRE_RESP_NAME);
    qnaires.setQnaireRespVersId(QNAIRE_RESP_VERS_ID);
    qnaires.setQnaireRespVersName(QNAIRE_RESP_VERS_NAME);
    qnaires.setQnaireReviewedDate(QNAIRE_REVIEWED_DATE);

    ExternalLinkInfo link = new ExternalLinkInfo();
    link.setDisplayText(QNAIRE_RESP_LINK_DISP_TEXT);
    link.setLinkableObjId(QNAIRE_RESP_VAR_ID);
    link.setLinkType(QNAIRE_RESPONSE_VAR_LINK_TYPE);
    link.setUrl(QNAIRE_LINK);
    qnaires.setQnaireBaselineLink(link);
    qnaires.setQnaireBaselineLinkDisplayText(QNAIRE_LINK_DISP_TEXT_FOR_UI);

    dataAssmntQnaires.add(qnaires);
    return dataAssmntQnaires;
  }

  /**
   * @param dataAssessmentBaseline
   */
  private void testCompHexData(final DataAssessmentReport dataAssessmentBaseline) {
    DaCompareHexParam daCompareHexParam =
        dataAssessmentBaseline.getDataAssmntCompHexData().getDaCompareHexParam().get(0);

    assertNotNull("DaCompareHexParam should not be null", daCompareHexParam);
    assertFalse("BlackList Flag should be false", daCompareHexParam.isBlackList());
    assertFalse("Compli Flag should be false", daCompareHexParam.isCompli());
    assertEquals("Compli result should be equal", CompliResValues.NO_RULE, daCompareHexParam.getCompliResult());
    assertEquals("CDR Result Id should be equal", CDR_RESULT_ID, daCompareHexParam.getCdrResultId().longValue());
    assertEquals("Compli Tooltip shoul be equal", "NA", daCompareHexParam.getCompliTooltip());
    assertTrue("Dependent characteristics should be true", daCompareHexParam.isDependantCharacteristic());
    assertFalse("Equal Flag should be false", daCompareHexParam.isEqual());
    assertEquals("Function name should be equal", FUNC_NAME, daCompareHexParam.getFuncName());
    assertEquals("Function version should be equal", FUN_VERS, daCompareHexParam.getFuncVers());
    assertEquals("Parameter ID should be equal", PARAM_ID, daCompareHexParam.getParameterId().longValue());
    assertEquals("Parameter Name should be equal", PARAM_NAME, daCompareHexParam.getParamName());
    assertEquals("Parameter type should be equal", ParameterType.VALUE, daCompareHexParam.getParamType());
    assertFalse("Qssd Flag should be False", daCompareHexParam.isQssdParameter());
    assertFalse("Ready Only Flag should be False", daCompareHexParam.isReadOnly());
    assertTrue("Reviewed Flag should be true", daCompareHexParam.isReviewed());
    assertFalse("Never Reviewed Flag should be false", daCompareHexParam.isNeverReviewed());
    assertEquals("Questionnaire Status should be equal", CDRConstants.DA_QS_STATUS_TYPE.N_A.getDbType(),
        daCompareHexParam.getQnaireStatus());
    assertEquals("QSSD Tooltip should be equal", "NA", daCompareHexParam.getQssdTooltip());
    assertEquals("Review result name should be equal", RVW_NAME, daCompareHexParam.getRvwResultName());
    assertEquals("Review Parameter Id should be equal", RVW_PARAM_ID, daCompareHexParam.getRvwParamId().longValue());
    assertEquals("Review score should be equal", DATA_REVIEW_SCORE.S_0.getDbType(), daCompareHexParam.getReviewScore());
    assertEquals("Hundred Percent Review score should be equal", DATA_REVIEW_SCORE.S_0.getHundredPercScoreDisplay(),
        daCompareHexParam.getHundredPecReviewScore());
    assertEquals("Latest Function Version should be equal", LATEST_FUNC_VERS,
        daCompareHexParam.getLatestFunctionVersion());
  }

  /**
   * @param obj
   */
  private DataAssessmentCompareHexData fetchCompHexData() {
    List<DaCompareHexParam> daCompHexParamList = new ArrayList<>();
    DaCompareHexParam daCompareHexParam = new DaCompareHexParam();
    daCompareHexParam.setBlackList(false);
    daCompareHexParam.setCompli(false);
    daCompareHexParam.setCompliResult(CompliResValues.NO_RULE);
    daCompareHexParam.setCdrResultId(CDR_RESULT_ID);
    daCompareHexParam.setCompliTooltip("NA");
    daCompareHexParam.setDependantCharacteristic(true);
    daCompareHexParam.setEqual(false);
    daCompareHexParam.setFuncName(FUNC_NAME);
    daCompareHexParam.setFuncVers(FUN_VERS);
    daCompareHexParam.setParameterId(PARAM_ID);
    daCompareHexParam.setParamName(PARAM_NAME);
    daCompareHexParam.setParamType(ParameterType.VALUE);
    daCompareHexParam.setQssdParameter(false);
    daCompareHexParam.setReadOnly(false);
    daCompareHexParam.setReviewed(true);
    daCompareHexParam.setNeverReviewed(false);
    daCompareHexParam.setQnaireStatus(CDRConstants.DA_QS_STATUS_TYPE.N_A.getDbType());
    daCompareHexParam.setQssdResult(QSSDResValues.NO_RULE);
    daCompareHexParam.setQssdTooltip("NA");
    daCompareHexParam.setRvwResultName(RVW_NAME);
    daCompareHexParam.setRvwParamId(RVW_PARAM_ID);
    daCompareHexParam.setReviewScore(DATA_REVIEW_SCORE.S_0.getDbType());
    daCompareHexParam.setLatestFunctionVersion(LATEST_FUNC_VERS);
    daCompareHexParam.setWpName(WP_NAME);
    daCompareHexParam.setRespName(RESP_NAME);

    daCompHexParamList.add(daCompareHexParam);

    DataAssessmentCompareHexData dataAssmntCompHexData = new DataAssessmentCompareHexData();
    dataAssmntCompHexData.setDaCompareHexParam(daCompHexParamList);
    return dataAssmntCompHexData;

  }

  /**
   * test output data
   */
  private void testDaDataAssessment(final DaDataAssessment obj) {
    assertEquals("BaselineName should be equal", obj.getBaselineName(), BASELINE_NAME);
    assertEquals("Description should be equal", obj.getDescription(), BASELINE_DESCRIPTION);
    assertEquals("TypeOfAssignment should be equal", obj.getTypeOfAssignment(),
        CDRConstants.TYPE_OF_ASSESSMENT.DEVELOPMENT.getDbType());
    assertEquals("PidcVersId should be equal", obj.getPidcVersId(), CommonUtils.getBigdecimalFromLong(PIDC_VERS_ID));
    assertEquals("PidcVersFullname should be equal", obj.getPidcVersFullname(), PIDC_VERS_NAME);
    assertEquals("VariantId should be equal", obj.getVariantId(), CommonUtils.getBigdecimalFromLong(PIDC_VAR_ID));
    assertEquals("VariantName should be equal", obj.getVariantName(), VARIANT_NAME);
    assertEquals("PidcA2lId should be equal", obj.getPidcA2lId(), CommonUtils.getBigdecimalFromLong(PIDC_A2L_ID));
    assertEquals("A2lFilename should be equal", obj.getA2lFilename(), A2L_FILE_NAME);
    assertEquals("WpDefnVersId should be equal", obj.getWpDefnVersId(),
        CommonUtils.getBigdecimalFromLong(WP_DEFN_VERS_ID));
    assertEquals("WpDefnVersName should be equal", obj.getWpDefnVersName(), WP_DEFN_VERS_NAME);
    assertEquals("CreatedUser should be equal", obj.getCreatedUser(), "BNE4COB");
  }

}
