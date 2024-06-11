/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l.precal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.model.a2l.precal.PRECAL_SOURCE_TYPE;
import com.bosch.caltool.icdm.model.a2l.precal.PreCalAttrValResponse;
import com.bosch.caltool.icdm.model.a2l.precal.PreCalData;
import com.bosch.caltool.icdm.model.a2l.precal.PreCalInputData;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class PreCalDataServiceClientTest extends AbstractRestClientTest {

  /**
   *
   */
  // A2L File: Alfa Romeo->Gasoline Engine->PC - Passenger Car->ME(D)17->Alfa 1.8L, FamB, Gen2, US (Version
  // 1)->D173307->009-Alfa_FamB_Gen3_1.8L_TFSI_MED17.3.3_D17330714A0_or.A2L
  // Link icdm:a2lid,792190866-1631755001
  private static final Long PIDC_A2L_ID_01 = 792190866L;
  private static final List<String> PARAM_LIST_01 = Arrays.asList("CWTIPIN", "DFC_CtlMsk.DFC_BBKRnldg_C",
      "DFC_CtlMsk.DFC_BBKRsyne_C", "DFC_DisblMsk.DFC_BBKRnldg_C", "DFC_DisblMsk.DFC_BBKRsyne_C", "DYESPFFKT",
      "KFAVGRDKR", "KFDYESPF", "KFEVGRDKR", "KRAL1N", "KRAL2N", "KRAL3N", "KRALH", "KRAN1", "KRAN2", "KRAN3", "KRAN4",
      "KRANH", "KRDYDELAY", "KRLNMDY", "NGKRWN", "NKRAMIN", "NKRF", "TATIPIN", "TMDYNA", "TMKR", "TMKRA", "ZKRDYDELAY",
      "ZKREGLDYN", "ZKRKELDYN", "ZKRKENDYN", "KAMFZ", "DINH_Lim.DSQ_stAPP_CA", "DINH_Lim.DSQ_stAPP_CA",
      "DINH_Lim.DSQ_stAPP_CA", "APP_uKDLowAPP_C", "APP_uKDLowAPP_C", "APP_uKDLowAPP_C", "DFRAESTAB",
      "DFES_Cls.DFC_SRCHighAPP1_C", "DFES_Cls.DFC_SRCHighAPP1_C");

  // A2L File: VW->Diesel Engine->PC - Passenger Car->EDC17->MLBevo DL (EU6AG
  // variants)->M1774VDLC1274->DL9QZR4000000.A2L
  // Link icdm:a2lid,986719416-1892955001
  private static final Long PIDC_A2L_ID_02 = 986719416L;
  // RuleSet : VW_EA211_R3_MED17127_TSI_MQBV31
  private static final Long RULE_SET_ID_01 = 782996965L;
  private static final List<String> PARAM_LIST_02 = Arrays.asList("HtrEl1_StdCal.tiDesValFlt_C",
      "HtrEl1_StdCal.tiSlwRespSimPT1_C", "HtrEl1_swtConf_C", "HtrEl1_ratActLin_CUR", "DDRC_DurDeb.HtrEl1_tiOLDebDef_C",
      "DDRC_DurDeb.HtrEl1_tiOLDebOk_C", "DDRC_DurDeb.HtrEl1_tiOvrTempDebDef_C");


  // A2L File: Daimler->Gasoline Engine->PC - Passenger Car->ME(D)17->M264/M260 (VC18A (Version
  // 3))->D177->MED41-18A-SST_010_00-STAR23-xx-M26Xxx_withGroups.A2L
  // URL : icdm:a2lid,1036284916-1966516410
  private static final Long PIDC_A2L_ID_03 = 1036284916L;
  private static final List<String> PARAM_LIST_03 = Arrays.asList("CWBLL", "CWA2MDRVDE", "TWHICAN", "CWDETAKH2",
      "DFC_CtlMsk.DFC_UEGOOLIPES1B1_C", "DFC_CtlMsk.DFC_UEGOOLRES1B1_C");
  private static final List<String> PARAM_LIST_04 = Arrays.asList("KRAN1", "KRAN2", "KRAN3", "KRDYDELAY", "LKRN");

  // A2L File: Daimler->Gasoline Engine->PC - Passenger Car->MG1-C->M256-a (Version
  // 6)->DA_MDG1G->MRG1x-18A-FRG_042_00-Star23-xx-M256xx_withGroups.A2L
  // URL : icdm:a2lid,1318421316-2194470001
  private static final Long PIDC_A2L_ID_04 = 1318421316L;
  private static final List<String> PARAM_LIST_05 = Arrays.asList("IMLATMMX[0]", "IMLATMMX[1]", "IMLATMMX[2]",
      "KLFGLSKT[0]", "KLFGLSKT[1]", "KLFGLSKT[2]", "KLMLSKHAGE[0]", "KLMLSKHAGE[1]", "ExhMgT_mAirCatHeatgDes_M[4]",
      "ExhMgT_mAirCatHeatgDes_M[5]", "ExhMgT_mAirCatHeatgDes_M[6]", "ExhMgT_mAirCatHeatgDesMin_C[0]",
      "ExhMgT_mAirCatHeatgDesMin_C[1]", "ExhMgT_mAirCatHeatgDesMin_C[2]");

  // A2L File: Daimler->Diesel Engine->PC - Passenger Car->MD1-C->Daimler MRD1 (Version
  // 1)->DA_MDG1D->MRD1-18A-0M_B040D.A2L
  // URL : icdm:a2lid,1267082616-2160855001
  private static final Long PIDC_A2L_ID_05 = 1267082616L;
  // Variant : 167-WAX-OM654D20_180kW-EAd_CRI325-EU6SCR_MPE-F
  // Link : icdm:pidvarid,773520365-781678302
  private static final Long VAR_ID1 = 781678302L;
  // Ruleset : Daimler MRD1 (DS)
  private static final Long RULE_SET_ID_02 = 776534165L;
  private static final List<String> PARAM_LIST_06 = Arrays.asList("CoEng_facGainPrioDmd_CUR",
      "DFC_DisblMsk.DFC_ETClbETFltMax_4_C", "ETClb_dtiIniETFlt0_CA", "ETClb_tiBstPresCorr0_CUR", "HLSDem_tqReqRstrt_M");
  // Attribute : CRI
  private static final long EXPECTED_ATTR_1 = 758864571L;

  /**
   * Test method for {@link PreCalDataServiceClient#getPreCalData( PreCalInputData) }for normal rules source - negative
   * test. Expects missing dependency error
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetPreCalDataNormalRulesMissingDep() throws ApicWebServiceException {
    PreCalInputData input = createInputData(PIDC_A2L_ID_01, false, null, null, PARAM_LIST_01);
    this.thrown.expectMessage("Missing definition for attribute 'APM - Type' in project");
    PreCalData ret = new PreCalDataServiceClient().getPreCalData(input);
    assertNotNull("Response should not be null", ret);
  }


  /**
   * Test method for {@link PreCalDataServiceClient#getPreCalData( PreCalInputData) }for normal rules source
   *
   * @throws ApicWebServiceException error in service call
   * @throws IOException while creating caldata object
   * @throws ClassNotFoundException while creating caldata object
   */
  @Test
  public void testGetPreCalDataNormalRulesNoDep() throws ApicWebServiceException, ClassNotFoundException, IOException {

    PreCalInputData input = createInputData(PIDC_A2L_ID_03, false, null, null, PARAM_LIST_04);
    PreCalData ret = new PreCalDataServiceClient().getPreCalData(input);

    testPreCalDataServiceResponse(ret);
    assertNotNull("Response should not be null", ret);
    testCalData(ret, "LKRN", "CURVE [156]");
  }


  /**
   * Test method for {@link PreCalDataServiceClient#getPreCalData( PreCalInputData) }for normal rules source with
   * dependencies at project level
   *
   * @throws ApicWebServiceException error in service call
   * @throws IOException while creating caldata object
   * @throws ClassNotFoundException while creating caldata object
   */
  @Test
  public void testGetPreCalDataNormalRulesWithDepNoVar()
      throws ApicWebServiceException, ClassNotFoundException, IOException {

    PreCalInputData input = createInputData(PIDC_A2L_ID_03, false, null, null, PARAM_LIST_03);
    PreCalData ret = new PreCalDataServiceClient().getPreCalData(input);

    testPreCalDataServiceResponse(ret);
    assertNotNull("Response should not be null", ret);
    testCalData(ret, "DFC_CtlMsk.DFC_UEGOOLRES1B1_C", "512");
  }

  /**
   * Test method for {@link PreCalDataServiceClient#getPreCalData( PreCalInputData) }for ruleset type source, no
   * dependencies
   *
   * @throws ApicWebServiceException error in service call
   * @throws IOException error while creating caldata object
   * @throws ClassNotFoundException error while creating caldata object
   */
  @Test
  public void testGetPreCalDataRuleSetNoDep() throws ApicWebServiceException, ClassNotFoundException, IOException {

    PreCalInputData input = createInputData(PIDC_A2L_ID_02, false, RULE_SET_ID_01, null, PARAM_LIST_02);
    PreCalData ret = new PreCalDataServiceClient().getPreCalData(input);

    testPreCalDataServiceResponse(ret);
    assertNotNull("Response should not be null", ret);
    testCalData(ret, "HtrEl1_swtConf_C", "0.0");
  }

  /**
   * Test method for {@link PreCalDataServiceClient#getPreCalData( PreCalInputData) }for ruleset type source, with
   * dependencies at variant level
   *
   * @throws ApicWebServiceException error in service call
   * @throws IOException error while creating caldata object
   * @throws ClassNotFoundException error while creating caldata object
   */
  @Test
  public void testGetPreCalDataRuleSetWithDep() throws ApicWebServiceException, ClassNotFoundException, IOException {

    PreCalInputData input = createInputData(PIDC_A2L_ID_05, false, RULE_SET_ID_02, VAR_ID1, PARAM_LIST_06);
    PreCalData ret = new PreCalDataServiceClient().getPreCalData(input);

    testPreCalDataServiceResponse(ret);
    assertNotNull("Response should not be null", ret);
    testCalData(ret, "HLSDem_tqReqRstrt_M", "MAP [8,4]");
  }

  /**
   * Test method for {@link PreCalDataServiceClient#getPreCalData( PreCalInputData) }for commmon rules type source, with
   * variant coded parameters, no dependencies
   *
   * @throws ApicWebServiceException error in service call
   * @throws IOException error while creating caldata object
   * @throws ClassNotFoundException error while creating caldata object
   */
  @Test
  public void testGetPreCalDataVarCodedNoDep() throws ApicWebServiceException, ClassNotFoundException, IOException {
    PreCalInputData input = createInputData(PIDC_A2L_ID_04, false, null, null, PARAM_LIST_05);
    PreCalData ret = new PreCalDataServiceClient().getPreCalData(input);

    testPreCalDataServiceResponse(ret);
    assertNotNull("Response should not be null", ret);
    testCalData(ret, "KLFGLSKT[0]", "CURVE [4]");
  }

  /**
   * Test method for {@link PreCalDataServiceClient#getPreCalAttrVals( PreCalInputData) }for normal rules source with
   * dependencies
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetPreCalAttrValsNormalRulesWithDeps() throws ApicWebServiceException {

    PreCalInputData input = createInputData(PIDC_A2L_ID_01, false, null, null, PARAM_LIST_01);
    PreCalAttrValResponse ret = new PreCalDataServiceClient().getPreCalAttrVals(input);

    assertNotNull("Response should not be null", ret);
    assertFalse("Response - dependent attributes should not be empty",
        (ret.getAttrMap() == null) || ret.getAttrMap().isEmpty());

    LOG.debug("Dependent attrs : {}", ret.getAttrMap().keySet());

  }

  /**
   * Test method for {@link PreCalDataServiceClient#getPreCalAttrVals ( PreCalInputData) } for ruleset type source, no
   * dependencies
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetPreCalAttrValsRuleSetNoDep() throws ApicWebServiceException {
    PreCalInputData input = createInputData(PIDC_A2L_ID_02, false, RULE_SET_ID_01, null, PARAM_LIST_02);
    PreCalAttrValResponse ret = new PreCalDataServiceClient().getPreCalAttrVals(input);

    assertNotNull("Response should not be null", ret);
    assertTrue("Response - dependent attributes should be empty", ret.getAttrMap().isEmpty());
  }

  /**
   * Test method for {@link PreCalDataServiceClient#getPreCalAttrVals ( PreCalInputData) } for ruleset type source, with
   * dependencies
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetPreCalAttrValsRuleSetWithDep() throws ApicWebServiceException {

    PreCalInputData input = createInputData(PIDC_A2L_ID_05, false, RULE_SET_ID_02, VAR_ID1, PARAM_LIST_06);
    PreCalAttrValResponse ret = new PreCalDataServiceClient().getPreCalAttrVals(input);

    assertNotNull("Response should not be null", ret);
    assertFalse("Response - dependent attributes should not be empty",
        (ret.getAttrMap() == null) || ret.getAttrMap().isEmpty());

    LOG.debug("Dependent attrs : {}", ret.getAttrMap().keySet());

    Attribute attr = ret.getAttrMap().get(EXPECTED_ATTR_1);
    assertNotNull("Test dependecy attr", attr);
    LOG.debug("attr is present - ID = {}, name = {}", attr.getId(), attr.getName());

    PidcVariantAttribute varAttr = ret.getPidcVarAttrMap().get(EXPECTED_ATTR_1);
    assertNotNull("Test dependecy attr in variant", varAttr);
    assertEquals("Test dependecy attr value in variant", "CRI3-25", varAttr.getValue());
  }


  private PreCalInputData createInputData(final Long pidcA2lId, final boolean onlyExactMatch, final Long ruleSetId,
      final Long variantId, final List<String> paramList) {

    PreCalInputData data = new PreCalInputData();

    data.setPidcA2lId(pidcA2lId);
    data.setVariantId(variantId);

    data.setSourceType(null == ruleSetId ? PRECAL_SOURCE_TYPE.COMMON_RULES.getTypeCode()
        : PRECAL_SOURCE_TYPE.RULESET_RULES.getTypeCode());
    data.setRuleSetId(ruleSetId);

    data.setRefValues(true);
    data.setOnlyExactMatch(onlyExactMatch);

    data.setParamSet(new HashSet<>(paramList));

    return data;
  }


  private void testPreCalDataServiceResponse(final PreCalData response) {
    assertNotNull("Response should not be null", response);
    assertFalse("Response - pre-calibration data should not be empty",
        (response.getPreCalDataMap() == null) || response.getPreCalDataMap().isEmpty());
  }

  private CalData testCalData(final PreCalData ret, final String expParamName, final String expDisplayVal)
      throws IOException, ClassNotFoundException {

    LOG.debug("PreCalData response verification : Expected param - {}, expected value - {}", expParamName,
        expDisplayVal);

    byte[] dataByte = ret.getPreCalDataMap().get(expParamName);
    assertNotNull("Get data for one param - " + expParamName, dataByte);

    CalData calData = CalDataUtil.getCalDataObj(dataByte);
    assertNotNull("Pre cal data for one param - " + expParamName, calData);
    assertEquals("Check param name in cal data - " + expParamName, expParamName, calData.getShortName());
    String value = calData.getCalDataPhy().getSimpleDisplayValue();
    assertEquals("Check param value in cal data - N" + expParamName, expDisplayVal, value);

    return calData;
  }

}
