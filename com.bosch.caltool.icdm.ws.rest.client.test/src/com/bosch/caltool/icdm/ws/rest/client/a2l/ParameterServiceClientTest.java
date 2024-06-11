/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.junit.Test;

import com.bosch.caltool.icdm.model.a2l.FunctionParamProperties;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.a2l.ParameterAttribute;
import com.bosch.caltool.icdm.model.a2l.ParameterRulesResponse;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author rgo7cob
 */
public class ParameterServiceClientTest extends AbstractRestClientTest {

  /**
   *
   */
  private static final String TSTMSG_RESP_NOT_NULL = "Response should not be null";
  private static final String TSTMSG_RESP_NOT_EMPTY = "Response should not be empty";

  // Count on 2021-06-15, for functions : Ac_DataAcq, ACCtl_Demand ,BBKR, BGKV
  private static final long PARAM_COUNT_EXPECTED_MULT_FUNS = 3562L;

  private static final String PARAM_NAME = "CWKR";

  private static final String PARAM_TYPE_VALUE = "VALUE";

  private static final Long ATTR_ID = 391L;

  private static final Long GETTEST_PARAMETER_ID = 379414915L;
  private static final Long INVALID_PARAMETER_ID = -1L;

  private static final String INVALID_PARAM_NAME = "TestingInvalidString";


  /**
   * Test method for {@link ParameterServiceClient#getParamRulesOutput(String, String, String)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetParamRulesOutput() throws ApicWebServiceException {
    ParameterServiceClient servClient = new ParameterServiceClient();
    ParameterRulesResponse paramRulesOutput = servClient.getParamRulesOutput("BBKR", null, null);
    assertNotNull(TSTMSG_RESP_NOT_NULL, paramRulesOutput);

    Map<String, Parameter> paramMap = paramRulesOutput.getParamMap();
    assertNotNull(TSTMSG_RESP_NOT_NULL, paramMap);
    assertFalse(TSTMSG_RESP_NOT_EMPTY, paramMap.isEmpty());
    Parameter parameter = paramMap.get(PARAM_NAME);
    assertNotNull(TSTMSG_RESP_NOT_NULL, parameter);
    testParameter(parameter);

    Map<String, List<ReviewRule>> rulesMap = paramRulesOutput.getReviewRuleMap();
    assertNotNull(TSTMSG_RESP_NOT_NULL, rulesMap);
    assertFalse(TSTMSG_RESP_NOT_EMPTY, rulesMap.isEmpty());

    List<ReviewRule> reviewRuleList = rulesMap.get(PARAM_NAME);
    LOG.info("Size of ReviewRule List : {}", reviewRuleList.size());
    assertFalse(TSTMSG_RESP_NOT_EMPTY, reviewRuleList.isEmpty());

    Map<String, List<ParameterAttribute>> attrMap = paramRulesOutput.getAttrMap();
    assertNotNull(TSTMSG_RESP_NOT_NULL, attrMap);
    assertFalse(TSTMSG_RESP_NOT_EMPTY, attrMap.isEmpty());

    List<ParameterAttribute> paramAttrList = attrMap.get(PARAM_NAME);
    assertFalse(TSTMSG_RESP_NOT_EMPTY, paramAttrList.isEmpty());
    boolean paramAttrAvailable = false;
    for (ParameterAttribute parameterAttribute : paramAttrList) {
      if (parameterAttribute.getId().equals(1508676778L)) {
        paramAttrAvailable = true;
        testParamAttr(parameterAttribute);
        break;
      }
    }
    assertTrue("Parameter Attribute is available", paramAttrAvailable);

    Map<Long, Attribute> attrValModelMap = paramRulesOutput.getAttrObjMap();
    assertNotNull(TSTMSG_RESP_NOT_NULL, attrValModelMap);
    assertFalse(TSTMSG_RESP_NOT_EMPTY, attrValModelMap.isEmpty());

    Attribute attribute = attrValModelMap.get(ATTR_ID);
    assertNotNull(TSTMSG_RESP_NOT_NULL, attribute);
    testAttribute(attribute);

  }

  /**
   * @param attribute
   */
  private void testAttribute(final Attribute attribute) {
    assertEquals("Name_Eng is equal", "AC", attribute.getNameEng());
    assertEquals("Name_Ger is equal", "Klimaanlage", attribute.getNameGer());
    assertEquals("Desc_Eng is equal", "Air Condition (AC) installed", attribute.getDescriptionEng());
    assertEquals("Desc_Ger is equal", "Klimaanlage verbaut", attribute.getDescriptionGer());
  }

  /**
   * @param parameterAttribute
   */
  private void testParamAttr(final ParameterAttribute parameterAttribute) {
    assertEquals("Attr Id is equal", Long.valueOf(391), parameterAttribute.getAttrId());
    assertEquals("Parameter Id is equal", Long.valueOf(433507915), parameterAttribute.getParamId());
    assertEquals("Name is equal", "AC", parameterAttribute.getName());
    assertEquals("Description is equal", "Air Condition (AC) installed", parameterAttribute.getDescription());
    assertEquals("Created User is equal", "DMR1COB", parameterAttribute.getCreatedUser());
  }

  /**
   * @param parameter
   */
  private void testParameter(final Parameter parameter) {
    assertEquals("LongName is equal", "code word knock control", parameter.getLongName());
    assertEquals("LongNameGer is equal", "Codewort Klopfregelung", parameter.getLongNameGer());
    assertEquals("SSD_Class is equal", "CUSTSPEC", parameter.getSsdClass());
  }


  /**
   * Test method for {@link ParameterServiceClient#getSingleParamRules(String, String)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetSingleParamRules() throws ApicWebServiceException {
    ParameterServiceClient servClient = new ParameterServiceClient();


    ParameterRulesResponse paramRulesOutput = servClient.getSingleParamRules(PARAM_NAME, PARAM_TYPE_VALUE);
    assertNotNull(TSTMSG_RESP_NOT_NULL, paramRulesOutput);

    Map<String, Parameter> paramMap = paramRulesOutput.getParamMap();
    assertNotNull(TSTMSG_RESP_NOT_NULL, paramMap);
    assertFalse(TSTMSG_RESP_NOT_EMPTY, paramMap.isEmpty());

    Parameter parameter = paramMap.get(PARAM_NAME);
    assertNotNull(TSTMSG_RESP_NOT_NULL, parameter);
    testParameter(parameter);

    Map<String, List<ReviewRule>> rulesMap = paramRulesOutput.getReviewRuleMap();
    assertNotNull(TSTMSG_RESP_NOT_NULL, rulesMap);
    assertFalse(TSTMSG_RESP_NOT_EMPTY, rulesMap.isEmpty());

    List<ReviewRule> reviewRuleList = rulesMap.get(PARAM_NAME);
    LOG.info("Size of ReviewRule List : {}", reviewRuleList.size());
    assertFalse(TSTMSG_RESP_NOT_EMPTY, reviewRuleList.isEmpty());

    Map<String, List<ParameterAttribute>> attrMap = paramRulesOutput.getAttrMap();
    assertNotNull(TSTMSG_RESP_NOT_NULL, attrMap);
    assertFalse(TSTMSG_RESP_NOT_EMPTY, attrMap.isEmpty());

    List<ParameterAttribute> paramAttrList = attrMap.get(PARAM_NAME);
    assertFalse(TSTMSG_RESP_NOT_EMPTY, paramAttrList.isEmpty());
    boolean paramAttrAvailable = false;
    for (ParameterAttribute parameterAttribute : paramAttrList) {
      if (parameterAttribute.getId().equals(1508676778L)) {
        paramAttrAvailable = true;
        testParamAttr(parameterAttribute);
        break;
      }
    }

    assertTrue("Parameter Attribute is available", paramAttrAvailable);

    Map<Long, Attribute> attrValModelMap = paramRulesOutput.getAttrObjMap();
    assertNotNull(TSTMSG_RESP_NOT_NULL, attrValModelMap);
    assertFalse(TSTMSG_RESP_NOT_EMPTY, attrValModelMap.isEmpty());

    Attribute attribute = attrValModelMap.get(ATTR_ID);
    assertNotNull(TSTMSG_RESP_NOT_NULL, attribute);
    testAttribute(attribute);
  }

  /**
   * Test method for {@link ParameterServiceClient#getParamCountbyFunctionNameSet(Set)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetParamCountbyFunctionNameSet() throws ApicWebServiceException {
    ParameterServiceClient servClient = new ParameterServiceClient();

    Set<String> functionNameSet = new HashSet<>();
    functionNameSet.add("Ac_DataAcq");
    functionNameSet.add("ACCtl_Demand");
    functionNameSet.add("BBKR");
    functionNameSet.add("BGKV");

    Long paramCount = servClient.getParamCountbyFunctionNameSet(functionNameSet);
    LOG.info("Parameter Count for the given FC set : {}", paramCount);

    assertTrue("Check param count in response", paramCount.longValue() >= PARAM_COUNT_EXPECTED_MULT_FUNS);
  }


  /**
   * Test method for {@link ParameterServiceClient#getParameter(String, String)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetParameter() throws ApicWebServiceException {
    ParameterServiceClient servClient = new ParameterServiceClient();

    Parameter parameter = servClient.getParameter(PARAM_NAME, PARAM_TYPE_VALUE);
    assertNotNull(TSTMSG_RESP_NOT_NULL, parameter);
    testParameter(parameter);
  }

  /**
   * Test method for {@link ParameterServiceClient#getParameterOnlyByName(String)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetParameterOnlyByName() throws ApicWebServiceException {
    ParameterServiceClient servClient = new ParameterServiceClient();

    Parameter parameter = servClient.getParameterOnlyByName(PARAM_NAME);
    assertNotNull(TSTMSG_RESP_NOT_NULL, parameter);
    testParameter(parameter);
  }

  /**
   * Test method for {@link ParameterServiceClient#getParamByNameOnly(String) }
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetParamByNameOnly() throws ApicWebServiceException {
    ParameterServiceClient servClient = new ParameterServiceClient();

    Map<Long, Parameter> retMap = servClient.getParamByNameOnly("CWKR");
    assertNotNull(TSTMSG_RESP_NOT_NULL, retMap);
    assertFalse(TSTMSG_RESP_NOT_EMPTY, retMap.isEmpty());

    Parameter parameter = retMap.get(433507915L);
    testParameter(parameter);
  }


  /**
   * Test method for {@link ParameterServiceClient#getParamsByName(Set) }
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetParamsByName() throws ApicWebServiceException {
    ParameterServiceClient servClient = new ParameterServiceClient();

    Set<String> nameSet = new HashSet<>();
    nameSet.add("UHC_uBattUThd_C");
    // nameSet.add("CWKR"); with multiple parameters empty map is returned
    Map<String, Parameter> parameterMap = servClient.getParamsByName(nameSet);
    assertNotNull(TSTMSG_RESP_NOT_NULL, parameterMap);
    assertFalse(TSTMSG_RESP_NOT_EMPTY, parameterMap.isEmpty());
    Parameter parameter = parameterMap.get("UHC_uBattUThd_C");
    assertNotNull(TSTMSG_RESP_NOT_NULL, parameter);
    testParameterNew(parameter);
  }


  /**
   * @param parameter
   */
  private void testParameterNew(final Parameter parameter) {
    assertEquals("LongName is equal",
        "Voltage threshold to limit mass flow of reducing agent, when Battery voltage is above it.",
        parameter.getLongName());
    assertEquals("LongNameGer is equal",
        "Grenzwert für die Versorgungsspannung, ab der der UREA-Massenstrom begrenzt wird.",
        parameter.getLongNameGer());
    assertEquals("ssdClass is equal", "COMPLIANCE", parameter.getSsdClass());
    assertEquals("PType is equal", PARAM_TYPE_VALUE, parameter.getType());
  }

  /**
   * Test method for {@link ParameterServiceClient#getParamsUsingIds(Set) }
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetParamMapUsingParamIds() throws ApicWebServiceException {
    ParameterServiceClient servClient = new ParameterServiceClient();

    Set<Long> paramIdSet = new HashSet<>();
    paramIdSet.add(429506915L); // KFLURB
    paramIdSet.add(394819115L); // NMKAMFZKH
    paramIdSet.add(405038515L); // DNMKHKT
    paramIdSet.add(405683365L); // ZLRFRI
    paramIdSet.add(411330465L); // CWMISALI
    paramIdSet.add(424684415L); // LURKTM
    paramIdSet.add(399680565L); // KLMIMXFON
    paramIdSet.add(425086415L); // KAMFZKH
    paramIdSet.add(433507915L); // CWKR

    Map<Long, Parameter> paramMap = servClient.getParamsUsingIds(paramIdSet);
    assertNotNull(TSTMSG_RESP_NOT_NULL, paramMap);
    assertFalse(TSTMSG_RESP_NOT_EMPTY, paramMap.isEmpty());
    Parameter parameter = paramMap.get(433507915L);// CWKR
    testParameter(parameter);
  }


  /**
   * Test method for {@link ParameterServiceClient#update(Parameter)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testUpdate() throws ApicWebServiceException {
    ParameterServiceClient servClient = new ParameterServiceClient();
    Parameter parameter = servClient.getParameter("wped_C", PARAM_TYPE_VALUE);
    parameter.setCodeWord("Y");
    parameter.setParamHint("JUnit_TestParamHint");

    // invoke update
    Parameter updatedParam = servClient.update(parameter);
    // validate update
    assertEquals("Updated ParamHint is equal", "JUnit_TestParamHint", updatedParam.getParamHint());
    assertEquals("Updated ParamHint is equal", "Y", updatedParam.getCodeWord());
  }

  /**
   * Test method for {@link ParameterServiceClient#getMissingLabels(Set, Set)}
   *
   * @throws ApicWebServiceException ApicWebServiceException
   */
  @Test
  public void testGetMissingLabels() throws ApicWebServiceException {
    ParameterServiceClient servClient = new ParameterServiceClient();

    Set<String> labels = new HashSet<>();
    labels.add("TNMOTMK");
    labels.add("DFES_DTCO.DFC_MDmin_C");
    labels.add("DSCHED_UpLimRateOk.FID_BLATP2_C");
    labels.add("DFES_Cls.DFC_MKUPSnpl_C");

    Set<String> a2llabels = new HashSet<>();
    a2llabels.add("TNMOTMK");
    a2llabels.add("DFES_DTCO.DFC_MDmin_C");
    a2llabels.add("TDMKWG");
    for (int i = 1; i < 101; i++) {
      labels.add("TEST" + i);
      a2llabels.add("TESTa2l" + i);
    }

    Set<String> label = servClient.getMissingLabels(labels, a2llabels);
    LOG.info("Invalid Label list size = {}", label.size());

    assertFalse("Missing labels not empty", label.isEmpty());
  }

  /**
   * Test method for {@link ParameterServiceClient#getsearchParameters(String, String, Long)}
   *
   * @throws ApicWebServiceException ApicWebServiceException
   */
  @Test
  public void testGetSearchParameters() throws ApicWebServiceException {
    ParameterServiceClient serviceClient = new ParameterServiceClient();

    SortedSet<FunctionParamProperties> funcParamPropSet =
        serviceClient.getsearchParameters("ACCMPR", "ACCMPR", 1494459027L);
    assertFalse(TSTMSG_RESP_NOT_EMPTY, funcParamPropSet.isEmpty());

    boolean funcAvailable = false;
    boolean paramAvailable = false;
    for (FunctionParamProperties functionParamProperties : funcParamPropSet) {
      if (functionParamProperties.getFunctionName().equals("ACCmpr_DD")) {
        funcAvailable = true;
        if (functionParamProperties.getParamId().equals(769703372L)) {
          paramAvailable = true;
          testFunction(functionParamProperties);
          break;
        }
      }
    }
    assertTrue("Function is available", funcAvailable);
    assertTrue("Parameter is available", paramAvailable);
  }

  /**
   * @param functionParamProperties
   */
  private void testFunction(final FunctionParamProperties functionParamProperties) {
    assertEquals("Parameter Id is equal", Long.valueOf(769703372), functionParamProperties.getParamId());
    assertEquals("Parameter name is equal", "ACCmpr_tiT15ActvAwakeDly_C", functionParamProperties.getParamName());
    assertEquals("Param Type is equal", PARAM_TYPE_VALUE, functionParamProperties.getParamType());
  }


  /**
   * Test method for {@link ParameterServiceClient#getParamIdList(String, String)}
   *
   * @throws ApicWebServiceException ApicWebServiceException
   */
  @Test
  public void testGetParamIdList() throws ApicWebServiceException {
    ParameterServiceClient servClient = new ParameterServiceClient();
    List<Long> paramIdList = servClient.getParamIdList("ExhMod_cpAbsltCann_CA", "");
    LOG.info("List size = {}", paramIdList.size());

    assertFalse(TSTMSG_RESP_NOT_EMPTY, paramIdList.isEmpty());
    assertTrue("ParamId is available", paramIdList.contains(385216515L));
  }

  /**
   * Test method for {@link ParameterServiceClient#getParamsListByFuncNameSet(Set) }
   *
   * @throws ApicWebServiceException ApicWebServiceException
   */
  @Test
  public void testGetParamsListByFuncNameSet() throws ApicWebServiceException {
    Set<String> funcSet = new HashSet<>(Arrays.asList("BBKR", "ACCECU_Acc", "ACClntP_DD"));
    Set<String> paramsSet = new ParameterServiceClient().getParamsListByFuncNameSet(funcSet);

    LOG.info("Ret size = {}", paramsSet.size());

    assertFalse("params not empty", paramsSet.isEmpty());
    assertTrue("param is available", paramsSet.contains("Acc_ActivMask_C"));// From ACCECU_Acc
    assertTrue("param is available", paramsSet.contains("ACClntP_ACPR0_C"));// From ACClntP_DD
  }

  /**
   * Test method for {@link ParameterServiceClient#get(Long)}
   *
   * @throws ApicWebServiceException Webservice Error
   */
  @Test
  public void testGet() throws ApicWebServiceException {
    Parameter parameter = new ParameterServiceClient().get(GETTEST_PARAMETER_ID);

    assertEquals("Id is equal", "379414915", parameter.getId().toString());
    assertEquals("Name is equal", "DFC_CtlMsk.DFC_ComACCADLC_C", parameter.getName());
    assertEquals("pClassText is equal", "", parameter.getpClassText());
    assertEquals("custPrm is equal", "N", parameter.getCustPrm());
  }

  /**
   * Negative Test method for {@link ParameterServiceClient#get(Long)}
   *
   * @throws ApicWebServiceException Webservice Error
   */
  @Test
  public void testGetNegative() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Parameter with ID " + "'" + INVALID_PARAMETER_ID + "'" + " not found");
    new ParameterServiceClient().get(INVALID_PARAMETER_ID);
    fail("Expected Exception not thrown");
  }


  /**
   * Test method for {@link ParameterServiceClient#getInvalidParameters(List)}
   *
   * @throws ApicWebServiceException Web Service Error
   */
  @Test
  public void testGetInvalidParamService() throws ApicWebServiceException {
    List<String> set = new ArrayList<>();
    set.add("DSCHED_UpLimRateOk.FID_BLATP2_C");
    set.add("DFES_DTCO.DFC_ComTOutBRK_C");
    set.add(INVALID_PARAM_NAME);
    set.add("AnotherInvalidName");

    List<String> retList = new ParameterServiceClient().getInvalidParameters(set);

    assertNotNull("List should not be null", retList);
    assertEquals("List size is 2", 2, retList.size());

    // Verify the invalid input given in the response
    boolean flag = false;
    for (String value : retList) {
      if (value.equals(INVALID_PARAM_NAME)) {
        flag = true;
      }
    }
    assertTrue("Invalid string should be present", flag);
  }

}
