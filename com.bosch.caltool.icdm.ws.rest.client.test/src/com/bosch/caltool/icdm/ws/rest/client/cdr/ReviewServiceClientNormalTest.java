/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.RvwWpAndRespModel;
import com.bosch.caltool.icdm.model.cdr.review.ReviewInput;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author say8cob
 */
public class ReviewServiceClientNormalTest extends AbstractReviewServiceClientTest {

  /**
   * PIDC A2L mapping ID for A2L File: <BR>
   * AUDI->Diesel Engine->PC - Passenger Car->MD1-C->X_Test_HENZE_1788
   * (V1)->MMD114A0CC1788->MMD114A0CC1788_MC50_DISCR.A2L
   */
  private static final long PIDC_A2L_ID_1_1 = 1165057193L;

  /**
   * Ruleset : Honda 5BFA EU6b Series Release HON1793A1_V130S64
   */
  private static final long RULE_SET_ID_1 = 797211415L;
  /**
   * Ruleset : Honda 5AZA EVA EUB6 MT FEV Series HON1793B2_V115S48
   */
  private static final long RULE_SET_ID_2 = 984325365L;
  /**
   * Ruleset : Honda 5AZA EVA EUB6 CVT FEV Series HON1793B2_V115S48
   */
  private static final long RULE_SET_ID_3 = 984325415L;

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
  /**
   * PIDC Variant: Honda->Gasoline Engine->PC - Passenger Car->ME(D)17->HONDA XE1B 2KR (Version 1)->2KR JP
   */
  private static final long VAR_ID_2 = 768673299L;
  /**
   * PIDC A2L mapping ID for A2L File :<br>
   * Honda->Gasoline Engine->PC - Passenger Car->ME(D)17->HONDA XE1B 2KR (Version
   * 1)->HON1793A1->159-HONDA_XDRA_4cyl_2.0L_GDI_TC_HON1793A1_V95B_2SV_V01.A2L
   */
  private static final long PIDC_A2L_ID_2 = 773637715L;

  /**
   * PIDC Variant: X_Testcustomer->Diesel Engine->PC - Passenger Car->EDC17->X_Test_002_P866_EA288 (Version 4)->SABQ
   */
  private static final long VAR_ID_3 = 771384589L;
  /**
   * PIDC A2L mapping ID for A2L : <br>
   * X_Testcustomer->Diesel Engine->PC - Passenger Car->EDC17->X_Test_002_P866_EA288 (Version
   * 4)->M1764VDAC866->DA805R4000000.A2L
   */
  private static final long PIDC_A2L_ID_3 = 773714115L;


  /**
   * PIDC A2L mapping ID for A2L File: <br>
   * X_Testcustomer->Gasoline Engine->PC - Passenger Car->ME(D)17->HN PIDC Demonstrator DRT Compli Wrong SSD Formula
   * (Test) ->P1284_I10R2.A2L
   */
  private static final long PIDC_A2L_ID_4 = 1433721790L;

  /**
   * PIDC Version: BMW->Diesel Engine->PC - Passenger Car->MD1-C->test_for_juint_cdrreviews (v1)
   */
  private static final long PIDC_A2L_ID_5 = 2221593091L;

  /**
   * PIDC Variant: BMW->Diesel Engine->PC - Passenger Car->MD1-C->test_for_juint_cdrreviews (v1)->001B_ACC
   */
  private static final long VAR_ID_4 = 2221593089L;


  /**
   * Normal review + common rules + review file - cdfx
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
//  TODOD  to be checked test class failing
  public void testNormal001() throws ApicWebServiceException {

    ReviewInput data = createInput("testdata/cdr/compli.cdfx", CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType(), "",
        PIDC_A2L_ID_1_0, VAR_ID_1);
    data.setFilesToBeReviewed(true);
    data.setCdrReviewResult(new CDRReviewResult());
    data.getCdrReviewResult().setSdomPverVarName("MMD114A0CC1788");
    assertNotNull(executeReview(data));
  }


  /**
   * Official review + common rules + Multiple WP + cdfx files
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testNormal002() throws ApicWebServiceException {
    ReviewInput data = createInput("testdata/cdr/multiple_wp_cdfx.cdfx", CDRConstants.CDR_SOURCE_TYPE.WP.getDbType(),
        "BBKR, DKVS, MoFIA_Co", PIDC_A2L_ID_5, VAR_ID_4);

    data.setReviewType("O");

    data.setRvwWpAndRespModelSet(populateRvwWpRespModelSet(true));

    data.setFunctionMap(setFuncParamMap(true));
    assertNotNull(executeReview(data));
  }

  /**
   * Normal review + common rules + a2l fun + review file - cdfx
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testNormal003() throws ApicWebServiceException {

    ReviewInput data = createInput("testdata/cdr/acctl_demand_cdfx.cdfx",
        CDRConstants.CDR_SOURCE_TYPE.A2L_FILE.getDbType(), "ACCtl_Demand", PIDC_A2L_ID_1_0, VAR_ID_1);
    assertNotNull(executeReview(data));
  }

  /**
   * Normal review + common rules + lab file + review file - cdfx
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testNormal004() throws ApicWebServiceException {

    ReviewInput data = createInput("testdata/cdr/acctl_demand_cdfx.cdfx",
        CDRConstants.CDR_SOURCE_TYPE.LAB_FILE.getDbType(), "ACCtl_Demand", PIDC_A2L_ID_1_0, VAR_ID_1);

    data.getFileData().setFunLabFilePath("testdata/cdr/acctl_demand_lab.lab");
    assertNotNull(executeReview(data));
  }

  /**
   * Normal review + common rules + fun file + review file - cdfx
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testNormal005() throws ApicWebServiceException {

    ReviewInput data = createInput("testdata/cdr/acctl_demand_cdfx.cdfx",
        CDRConstants.CDR_SOURCE_TYPE.FUN_FILE.getDbType(), "ACCtl_Demand", PIDC_A2L_ID_1_0, VAR_ID_1);

    data.getFileData().setFunLabFilePath("testdata/cdr/acctl_demand_fun.fun");
    assertNotNull(executeReview(data));
  }

  /**
   * Normal review + common rules + compli params + review file - cdfx
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testNormal006() throws ApicWebServiceException {

    ReviewInput data = createInput("testdata/cdr/CDFX_Export_Report_compli.cdfx",
        CDRConstants.CDR_SOURCE_TYPE.COMPLI_PARAM.getDbType(), "", PIDC_A2L_ID_1_0, VAR_ID_1);
    data.setCdrReviewResult(new CDRReviewResult());
    data.getCdrReviewResult().setSdomPverVarName("MMD114A0CC1788");
    assertNotNull(executeReview(data));

  }

  /**
   * Normal review + ruleset rules + review file - cdfx
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testNormal007() throws ApicWebServiceException {
    ReviewInput data = createInput("testdata/cdr/honda_ruleset_review.cdfx",
        CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType(), "", PIDC_A2L_ID_2, VAR_ID_2);
    data.setFilesToBeReviewed(true);
    data.getRulesData().setPrimaryRuleSetId(RULE_SET_ID_1);
    data.getRulesData().setCommonRulesPrimary(false);
    assertNotNull(executeReview(data));
  }

  /**
   * Normal review + primary : common rules, secondary : ruleset + review file - cdfx
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testNormal008() throws ApicWebServiceException {

    ReviewInput data = createInput("testdata/cdr/honda_ruleset_review.cdfx",
        CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType(), "", PIDC_A2L_ID_2, VAR_ID_2);

    Set<Long> secondaryRuleSet = new TreeSet<Long>();
    secondaryRuleSet.add(RULE_SET_ID_1);
    data.getRulesData().setSecondaryRuleSetIds(secondaryRuleSet);
    data.setFilesToBeReviewed(true);
    assertNotNull(executeReview(data));
  }

  /**
   * Normal review + primary : common rules + review file - dcm
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testNormal009() throws ApicWebServiceException {

    ReviewInput data = createInput("testdata/cdr/LRAEB-1.24.1-DKVS_Startdaten_V1.0.DCM",
        CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType(), "", PIDC_A2L_ID_2, VAR_ID_2);
    data.setFilesToBeReviewed(true);
    assertNotNull(executeReview(data));
  }

  /**
   * Normal review + primary : common rules + ssd file path + review file - cdfx Parameter has attribute depency -
   * feature value creation is checked
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testNormal010() throws ApicWebServiceException {
    ReviewInput data = createInput("testdata/cdr/UEGO_CenSrv_EA288.cdfx",
        CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType(), "", PIDC_A2L_ID_3, VAR_ID_3);
    data.setFilesToBeReviewed(true);
    data.setCdrReviewResult(new CDRReviewResult());
    data.getCdrReviewResult().setSdomPverVarName("MMD114A0CC1788");
    data.getRulesData().setSsdRuleFilePath("testdata/cdr/SSD_Review.ssd");
    assertNotNull(executeReview(data));
  }

  /**
   * Normal review + common rules + review file - hex (compli params only)
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testNormal011() throws ApicWebServiceException {

    ReviewInput data = createInput("testdata/cdr/HEX_MMD114A0CC1788_MC50_DISCR_LC.hex",
        CDRConstants.CDR_SOURCE_TYPE.COMPLI_PARAM.getDbType(), "", PIDC_A2L_ID_1_1, VAR_ID_1);
    assertNotNull(executeReview(data));
  }

  /**
   * Normal review + common rules + review file - paco
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testNormal012() throws ApicWebServiceException {

    ReviewInput data = createInput("testdata/cdr/compli_PACO.xml", CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType(),
        "", PIDC_A2L_ID_1_0, VAR_ID_1);
    data.setFilesToBeReviewed(true);
    data.setCdrReviewResult(new CDRReviewResult());
    data.getCdrReviewResult().setSdomPverVarName("MMD114A0CC1788");
    assertNotNull(executeReview(data));
  }

  /**
   * Official review + common rules + WP + cdfx file
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testNormal013() throws ApicWebServiceException {
    ReviewInput data = createInput("testdata/cdr/single_wp_cdfx.cdfx", CDRConstants.CDR_SOURCE_TYPE.WP.getDbType(),
        "BBKR", PIDC_A2L_ID_5, VAR_ID_4);

    data.setWpDivId(795414077L);
    data.setReviewType("O");
    data.setRvwWpAndRespModelSet(populateRvwWpRespModelSet(false));
    data.setFunctionMap(setFuncParamMap(false));
    data.setWpRespName("WP1");
    assertNotNull(executeReview(data));
  }

  /**
   * Fill the RvwAndWpRespModel Set
   *
   * @param isMultiple boolean
   * @return Set<RvwAndRespModel>
   */
  private Set<RvwWpAndRespModel> populateRvwWpRespModelSet(final boolean isMultiple) {
    Set<RvwWpAndRespModel> wpAndRespModelSet = new HashSet<>();

    RvwWpAndRespModel rvwWpObj1 = new RvwWpAndRespModel();
    rvwWpObj1.setA2lRespId(2221593105L);
    rvwWpObj1.setA2lWpId(2221593098L);
    wpAndRespModelSet.add(rvwWpObj1);

    if (isMultiple) {
      RvwWpAndRespModel rvwWpObj2 = new RvwWpAndRespModel();
      rvwWpObj2.setA2lRespId(2221593103L);
      rvwWpObj2.setA2lWpId(2221593102L);
      wpAndRespModelSet.add(rvwWpObj2);

      RvwWpAndRespModel rvwWpObj3 = new RvwWpAndRespModel();
      rvwWpObj3.setA2lRespId(2221593100L);
      rvwWpObj3.setA2lWpId(2221593098L);
      wpAndRespModelSet.add(rvwWpObj3);

      RvwWpAndRespModel rvwWpObj4 = new RvwWpAndRespModel();
      rvwWpObj4.setA2lRespId(2221593106L);
      rvwWpObj4.setA2lWpId(2221593102L);
      wpAndRespModelSet.add(rvwWpObj4);
    }

    return wpAndRespModelSet;
  }


  /**
   * Normal review + common rules + WP + cdfx file
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testNormal014() throws ApicWebServiceException {
    ReviewInput data = createInput("testdata/cdr/single_wp_cdfx.cdfx", CDRConstants.CDR_SOURCE_TYPE.WP.getDbType(),
        "BBKR", PIDC_A2L_ID_5, VAR_ID_4);

    data.setRvwWpAndRespModelSet(populateRvwWpRespModelSet(false));
    data.setFunctionMap(setFuncParamMap(false));
    data.setWpRespName("WP1");
    assertNotNull(executeReview(data));
  }


  /**
   * Populate Function and Parameter map
   *
   * @return Map<String - function name, Set<String> - Parameters>
   */
  private Map<String, Set<String>> setFuncParamMap(final boolean multiParamFlag) {
    Map<String, Set<String>> wpFuncParamMap = new HashMap<>();

    if (multiParamFlag) {
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
    }
    else {
      HashSet<String> paramset = new HashSet<String>();
      paramset.add("ZKRKELDYN");

      wpFuncParamMap.put("BBKR", paramset);
    }

    return wpFuncParamMap;
  }

  /**
   * Normal review + common rules + multiple WP + cdfx files
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testNormal015() throws ApicWebServiceException {
    ReviewInput data = createInput("testdata/cdr/multiple_wp_cdfx.cdfx", CDRConstants.CDR_SOURCE_TYPE.WP.getDbType(),
        "BBKR, DKVS, MoFIA_Co", PIDC_A2L_ID_5, VAR_ID_4);

    data.setRvwWpAndRespModelSet(populateRvwWpRespModelSet(true));
    data.setFunctionMap(setFuncParamMap(true));
    assertNotNull(executeReview(data));
  }

  /**
   * Normal review + common rules + lab file with unassigned params + review file - cdfx
   *
   * @throws ApicWebServiceException error during service call
   */
  // @Test
  public void testNormal016() throws ApicWebServiceException {

    ReviewInput data = createInput("testdata/cdr/unassign_assign_CDFX_Export_Report.cdfx",
        CDRConstants.CDR_SOURCE_TYPE.LAB_FILE.getDbType(), ApicConstants.NOT_ASSIGNED + ",DFC", 1453282748L,
        1453282729L);

    data.getFileData().setFunLabFilePath("testdata/cdr/not_assigned_fcs.lab");
    assertNotNull(executeReview(data));
  }

  /**
   * Normal review + common rules + review file - cdfx + ssd file
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testNormal017() throws ApicWebServiceException {

    ReviewInput data = createInput("testdata/cdr/Wrong_SSD.cdfx", CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType(),
        null, PIDC_A2L_ID_4, null);
    data.setFilesToBeReviewed(true);
    data.setCdrReviewResult(new CDRReviewResult());
    data.getCdrReviewResult().setSdomPverVarName("MMD114A0CC1788");
    assertNotNull(executeReview(data));

  }


  /**
   * Normal review + primary : common rules, secondary : two ruleset + review file - cdfx
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testNormal018() throws ApicWebServiceException {

    ReviewInput data = createInput("testdata/cdr/honda_ruleset_review.cdfx",
        CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType(), "", PIDC_A2L_ID_2, VAR_ID_2);

    Set<Long> secondaryRuleSet = new TreeSet<Long>();
    secondaryRuleSet.add(RULE_SET_ID_2);
    secondaryRuleSet.add(RULE_SET_ID_3);
    data.getRulesData().setSecondaryRuleSetIds(secondaryRuleSet);
    data.setFilesToBeReviewed(true);
    assertNotNull(executeReview(data));
  }

  /**
   * Normal review + common rules + compli params + review file - cdfx - remove err,log ,wrn files
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testNormal019() throws ApicWebServiceException {

    ReviewInput data = createInput("testdata/cdr/BXAZ8ERBLT02_AT.hex",
        CDRConstants.CDR_SOURCE_TYPE.COMPLI_PARAM.getDbType(), "", PIDC_A2L_ID_1_0, VAR_ID_1);
    assertNotNull(executeReview(data));

  }

}
