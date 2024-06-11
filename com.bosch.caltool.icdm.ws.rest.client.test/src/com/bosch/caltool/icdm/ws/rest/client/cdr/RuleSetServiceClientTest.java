/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.RuleSetRulesResponse;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.RuleSetInputData;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameterAttr;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author dja7cob Test class for service to fetch work package resources
 */
public class RuleSetServiceClientTest extends AbstractRestClientTest {

  /**
   *
   */
  private static final String RULESET_DESC = "test_ruleset_desc";

  /**
   *
   */
  private static final String RULESET_NAME = "test_ruleset";

  /**
  *
  */
  private static final String RULESET_NAME_NULL = null;

  /**
  *
  */
  private static final String NEW_LINE = "\n";

  /**
   * Pidc Variant id with rule set without attribute dependency
   */
  private static final Long PIDC_ELEMENT_ID1 = 6165737234L;
  /**
   * Pidc Version (Honda 1.0l MED1793 v1.1) id
   */
  private static final Long PIDC_ELEMENT_ID2 = 1013489805L;
  /**
   * Pidc Version (Honda 1.0l MED1793 v1.1) deleted variant id
   */
  private static final Long PIDC_ELEMENT_ID3 = 1013490568L;

  /**
   * Pidc Version (Honda 1.0l MED1793 v1.1) non deleted variant id
   */
  private static final Long PIDC_ELEMENT_ID4 = 1013490509L;
  /**
   * Pidc Variant (test_pidcVar_with_attr_dependency) with rule set having attribute value dependency rule
   */
  private static final Long PIDC_ELEMENT_ID5 = 6150840639L;
  /**
   * Pidc Version without variant test_pidcversion_with_attr_dependency
   */
  private static final Long PIDC_ELEMENT_ID6 = 6134560757L;
  /**
   * Pidc Version with missing attribute value (test_ruleFile_missing_attr_val)
   */
  private static final Long PIDC_ELEMENT_ID7 = 6134560731L;
  /**
   * Pidc Variant id with attribute dependency in rule set to an attribute which is not mapped to a feature
   * (test_rulefile_featureVal_unmapped)
   */
  private static final Long PIDC_ELEMENT_ID8 = 6134560753L;
  /**
   * Pidc Version with rule set without attr dependency
   */
  private static final Long PIDC_ELEMENT_ID9 = 6134560757L;
  /**
   * Pidc Version with rule set for which user does not have read access
   */
  private static final Long PIDC_ELEMENT_ID10 = 4994889342L;


  // Rule Set : Audi_V6Evo3_RB-Ladedruckregelung
  private static final Long RULE_SET_ID = 1214155565L;


  private static final Long RULE_SET_ID1 = 1525167327L;
  /*
   * Passing variant id for Pidc Version (test_sharavan_v1)-> wherein mandatory rule set attribute is not
   * defined(Negative Scenario)
   */
  private static final Long PIDC_ELEMENT_ID11 = 2596001497L;

  /**
   * Test method for {@link RuleSetServiceClient#get(Long) }.
   *
   * @throws ApicWebServiceException error while invoking service
   */
  @Test
  public void testGet() throws ApicWebServiceException {
    RuleSetServiceClient client = new RuleSetServiceClient();
    RuleSet ruleSet = client.get(RULE_SET_ID);
    assertEquals("Description is equal", "Audi_V6Evo3_RB-Ladedruckregelung", ruleSet.getDescription());
    assertEquals("CreatedUser is equal", "IMI2SI", ruleSet.getCreatedUser());
    assertNotNull("Created date should not be null", ruleSet.getCreatedDate());
  }

  /**
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetNegative() throws ApicWebServiceException {
    RuleSetServiceClient client = new RuleSetServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Rule Set with ID '" + -100L + "' not found");
    client.get(-100L);
    fail("Expected exception not thrown");
  }


  /**
   * Test method for {@link com.bosch.caltool.icdm.ws.rest.client.cdr.RuleSetServiceClient#getAllRuleSets()}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetAllRuleSets() throws ApicWebServiceException {
    RuleSetServiceClient client = new RuleSetServiceClient();
    Set<RuleSet> ruleSets = client.getAllRuleSets();
    boolean RuleSetisavailable = false;
    for (ParamCollection collection : ruleSets) {
      System.out.println(collection.getId());
      if (collection.getId().equals(RULE_SET_ID)) {
        RuleSetisavailable = true;
        testRuleSet(collection);

      }
    }
    assertTrue("RuleSet is available", RuleSetisavailable);
  }


  /**
   * @param collection
   */
  private void testRuleSet(final ParamCollection collection) {
    assertEquals("Description is equal", "Audi_V6Evo3_RB-Ladedruckregelung", collection.getDescription());
    assertEquals("CreatedUser is equal", "IMI2SI", collection.getCreatedUser());
    assertNotNull("Created date should not be null", collection.getCreatedDate());

  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.ws.rest.client.cdr.RuleSetServiceClient#getRuleSetParamRules(Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetRuleSetParams() throws ApicWebServiceException {
    RuleSetServiceClient client = new RuleSetServiceClient();
    RuleSetRulesResponse ruleSetRulesOutput = client.getRuleSetParamRules(RULE_SET_ID1);

    Map<String, RuleSetParameter> paramMap = ruleSetRulesOutput.getParamMap();
    assertFalse("Response should not be null or empty", ((paramMap == null) || paramMap.isEmpty()));

    Map<String, List<ReviewRule>> rulesMap = ruleSetRulesOutput.getReviewRuleMap();
    assertFalse("Response should not be null or empty", ((rulesMap == null) || rulesMap.isEmpty()));

    List<ReviewRule> rvwRuleList = rulesMap.get("InjSys_facKiCteHos_C");
    ReviewRule reviewRule = rvwRuleList.get(0);
    testReviewRule(reviewRule);

    Map<String, List<RuleSetParameterAttr>> attrMap = ruleSetRulesOutput.getAttrMap();
    assertFalse("Response should not be null or empty", ((attrMap == null) || attrMap.isEmpty()));

    List<RuleSetParameterAttr> rspAttr = attrMap.get("InjSys_facKiCteHos_C");
    for (RuleSetParameterAttr attr : rspAttr) {
      if (attr.getId().equals(788369133L)) {
        testRuleSetParameterAttr(attr);
      }
    }

    Map<Long, Attribute> attrValModelMap = ruleSetRulesOutput.getAttrObjMap();
    assertFalse("Response should not be null or empty", ((attrValModelMap == null) || attrValModelMap.isEmpty()));
    testOutput(ruleSetRulesOutput);

  }

  /**
   * @param attr
   */
  // validate attrMap
  private void testRuleSetParameterAttr(final RuleSetParameterAttr attr) {
    assertEquals("RuleSetParamId is equal", Long.valueOf(1214344515L), attr.getRuleSetParamId());
    assertEquals("AttrId is equal", Long.valueOf(788369133L), attr.getAttrId());
    assertEquals("Created user is equal", "RGO7COB", attr.getCreatedUser());
    assertNotNull("Created date should not be null", attr.getCreatedDate());
  }

  /**
   * @param reviewRule
   */
  // validate rulesMap
  private void testReviewRule(final ReviewRule reviewRule) {
    assertEquals("Maturity level is equal", null, reviewRule.getMaturityLevel());
    assertEquals("paramClass is equal", "CUSTSPEC", reviewRule.getParamClass());
    assertEquals("Created User is equal", "IMI2SI", reviewRule.getRuleCreatedUser());
    assertEquals("Review Method is equal", "M", reviewRule.getReviewMethod());
    assertEquals("Unit is equal", null, reviewRule.getUnit());

  }

  /**
   * @param ruleSetRulesOutput
   */
  private void testOutput(final RuleSetRulesResponse ruleSetRulesOutput) {

    // Validate paramMap
    assertEquals("CodeWord is equal", "N", ruleSetRulesOutput.getParamMap().values().iterator().next().getCodeWord());
    assertEquals("Created user is equal", "IMI2SI",
        ruleSetRulesOutput.getParamMap().values().iterator().next().getCreatedUser());
    assertEquals("CustPrm user is equal", "N",
        ruleSetRulesOutput.getParamMap().values().iterator().next().getCustPrm());
    assertEquals("Description user is equal", "I-Content of all Cylinder Torque Equalization Controller for HOS",
        ruleSetRulesOutput.getParamMap().values().iterator().next().getDescription());
    assertEquals("FuncId is equal", Long.valueOf(445719015L),
        ruleSetRulesOutput.getParamMap().values().iterator().next().getFuncId());

    // validate attrValModelMap
    assertEquals("AttrGrpId is equal", Long.valueOf(3242L),
        ruleSetRulesOutput.getAttrObjMap().values().iterator().next().getAttrGrpId());
    assertEquals("Level is equal", Long.valueOf(0L),
        ruleSetRulesOutput.getAttrObjMap().values().iterator().next().getLevel());
    assertEquals("CharacteristicId", Long.valueOf(789778865),
        ruleSetRulesOutput.getAttrObjMap().values().iterator().next().getCharacteristicId());
    assertEquals("Description", "How is AC controled?",
        ruleSetRulesOutput.getAttrObjMap().values().iterator().next().getDescription());
    assertNotNull("Created date should not be null",
        ruleSetRulesOutput.getAttrObjMap().values().iterator().next().getCreatedDate());
    assertEquals("Created User is equal", "RDI5FE",
        ruleSetRulesOutput.getAttrObjMap().values().iterator().next().getCreatedUser());

  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.ws.rest.client.cdr.RuleSetServiceClient#getRules(Long,RuleSetParameter)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testgetRules() throws ApicWebServiceException {
    RuleSetServiceClient client = new RuleSetServiceClient();
    RuleSetRulesResponse rules = client.getRuleSetParamRules(RULE_SET_ID1);

    // retrieving RuleSetParameter from RuleSetParameter
    Map<String, RuleSetParameter> paramMap = rules.getParamMap();
    RuleSetParameter param = paramMap.get("InjSys_facKiCteHos_C");// key

    // passing the parameters
    RuleSetRulesResponse responce = client.getRules(RULE_SET_ID1, param);

    Map<String, RuleSetParameter> paramMap1 = responce.getParamMap();
    assertFalse("Response should not be null or empty", ((paramMap1 == null) || paramMap1.isEmpty()));
    // validating paramMap
    testOutput(paramMap1);


    Map<String, List<ReviewRule>> rulesMap = responce.getReviewRuleMap();
    assertFalse("Response should not be null or empty", ((rulesMap == null) || rulesMap.isEmpty()));

    List<ReviewRule> rvwRule = rulesMap.get("InjSys_facKiCteHos_C");// key
    ReviewRule reviewRule = rvwRule.get(0);// has only one row
    // validating the list
    testReviewRule(reviewRule);

    Map<String, List<RuleSetParameterAttr>> attrMap = responce.getAttrMap();
    assertFalse("Response should not be null or empty", ((attrMap == null) || attrMap.isEmpty()));

    List<RuleSetParameterAttr> rspAttr = attrMap.get("InjSys_facKiCteHos_C");// key
    for (RuleSetParameterAttr attr : rspAttr) {
      if (attr.getId().equals(788369133L)) {
        testRuleSetParameterAttr(attr);
      }
    }

    Map<Long, Attribute> attrValModelMap = responce.getAttrObjMap();
    assertFalse("Response should not be null or empty", ((attrValModelMap == null) || attrValModelMap.isEmpty()));

    // validating attrValModelMap
    assertEquals("AttrGrpId is equal", Long.valueOf(3242L), attrValModelMap.values().iterator().next().getAttrGrpId());
    assertEquals("Level is equal", Long.valueOf(0L), attrValModelMap.values().iterator().next().getLevel());
    assertEquals("CharacteristicId", Long.valueOf(767172065),
        attrValModelMap.values().iterator().next().getCharacteristicId());
    assertEquals("Description", "Air Condition (AC) installed",
        attrValModelMap.values().iterator().next().getDescription());
    assertNotNull("Created date should not be null", attrValModelMap.values().iterator().next().getCreatedDate());
    assertEquals("Created User is equal", "hef2fe", attrValModelMap.values().iterator().next().getCreatedUser());


  }

  /**
   * @param paramMap1
   */
  // validating paramMap
  private void testOutput(final Map<String, RuleSetParameter> paramMap1) {
    assertEquals("CodeWord is equal", "N", paramMap1.values().iterator().next().getCodeWord());
    assertEquals("Created user is equal", "IMI2SI", paramMap1.values().iterator().next().getCreatedUser());
    assertEquals("CustPrm user is equal", "N", paramMap1.values().iterator().next().getCustPrm());
    assertEquals("Description user is equal", "I-Content of all Cylinder Torque Equalization Controller for HOS",
        paramMap1.values().iterator().next().getDescription());
    assertEquals("FuncId is equal", Long.valueOf(445719015L), paramMap1.values().iterator().next().getFuncId());

  }

//Test cases related to Rule File fetch for pidc mandatory attribute rule set
  /**
   * Passing version for Pidc Version (test_pidcversion_with_attr_dependency)->without variant and with dependency rule
   * for parameters(Positive Scenario)
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetRuleFileForPidcVerWithDepRule() throws ApicWebServiceException {
    RuleSetServiceClient servClient = new RuleSetServiceClient();
    String outputFileDirectory = CommonUtils.getICDMTmpFileDirectoryPath();
    String outptFileName = "RuleFile.ssd";
    String ruleFilePath = servClient.getSsdFileByPidcElement(PIDC_ELEMENT_ID6, outptFileName, outputFileDirectory);
    assertFalse("Response should not be null or empty", (ruleFilePath == null));
  }

  /**
   * Passing Pidc Version id(test_pidcversion_without_dependency) without variants and with rule set without attribute
   * dependency rule (Positive Scenario)
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetRuleFileForPidcVersion() throws ApicWebServiceException {
    RuleSetServiceClient servClient = new RuleSetServiceClient();
    String outputFileDirectory = CommonUtils.getICDMTmpFileDirectoryPath();
    String outptFileName = "RuleFile.ssd";
    String ruleFilePath = servClient.getSsdFileByPidcElement(PIDC_ELEMENT_ID9, outptFileName, outputFileDirectory);
    assertFalse("Response should not be null or empty", (ruleFilePath == null));
  }

  /**
   * Passing Pidc Variant id with rule set having parameters with attribute dependency rule (Positive Scenario)
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetRuleFileForPidcVariantWithDepRule() throws ApicWebServiceException {
    RuleSetServiceClient servClient = new RuleSetServiceClient();
    String outputFileDirectory = CommonUtils.getICDMTmpFileDirectoryPath();
    String outptFileName = "RuleFile.ssd";
    String ruleFilePath = servClient.getSsdFileByPidcElement(PIDC_ELEMENT_ID5, outptFileName, outputFileDirectory);
    assertFalse("Response should not be null or empty", (ruleFilePath == null));
  }

  /**
   * Passing Pidc Variant id(test_pidcvariant_without_dependency) with rule set without attribute dependency rule
   * (Positive Scenario)
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetRuleFileForPidcVariant() throws ApicWebServiceException {
    RuleSetServiceClient servClient = new RuleSetServiceClient();
    String outputFileDirectory = CommonUtils.getICDMTmpFileDirectoryPath();
    String outptFileName = "RuleFile.ssd";
    String ruleFilePath = servClient.getSsdFileByPidcElement(PIDC_ELEMENT_ID1, outptFileName, outputFileDirectory);
    assertFalse("Response should not be null or empty", (ruleFilePath == null));
  }

  /**
   * Passing Pidc Variant id for which user does not have access to mandatory attribute rule set(Negative Scenario)
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testNegativeGetRuleFileAccess() throws ApicWebServiceException {
    RuleSetServiceClient servClient = new RuleSetServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Insufficient privileges to access rule set");
    String outputFileDirectory = CommonUtils.getICDMTmpFileDirectoryPath();
    String outputFileName = "RuleFile.ssd";
    servClient.getSsdFileByPidcElement(PIDC_ELEMENT_ID10, outputFileName, outputFileDirectory);
    fail("Expected exception not thrown");
  }

  /**
   * Passing Pidc Version(test_ruleFile_missing_attr_val) id for a pidc with missing attribute value (Negative Scenario)
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testNegativeGetRuleFileForMissingAttrVal() throws ApicWebServiceException {
    RuleSetServiceClient servClient = new RuleSetServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Please define a value in the PIDC for those Attributes." + NEW_LINE +
        "Afterwards you can proceed with the review." + NEW_LINE);
    String outputFileDirectory = CommonUtils.getICDMTmpFileDirectoryPath();
    String outputFileName = "RuleFile.ssd";
    servClient.getSsdFileByPidcElement(PIDC_ELEMENT_ID7, outputFileName, outputFileDirectory);
    fail("Expected exception not thrown");
  }

  /**
   * Passing Pidc Variant id for a pidc where attribute values does is not mapped to a feature value (Negative Scenario)
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testNegativeGetRuleFileForUnmappedFeaVal() throws ApicWebServiceException {
    RuleSetServiceClient servClient = new RuleSetServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Please define a value in the PIDC for those Attributes." + NEW_LINE +
        "Afterwards you can proceed with the review." + NEW_LINE);
    String outputFileDirectory = CommonUtils.getICDMTmpFileDirectoryPath();
    String outputFileName = "RuleFile.ssd";
    servClient.getSsdFileByPidcElement(PIDC_ELEMENT_ID8, outputFileName, outputFileDirectory);
    fail("Expected exception not thrown");
  }

  /**
   * Passing variant id for Pidc Version (Honda 1.0l MED1793 v1.1)->non deleted variant id(Positive Scenario)
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetMandateRuleSetForPIDC() throws ApicWebServiceException {
    RuleSetServiceClient servClient = new RuleSetServiceClient();
    RuleSet ruleSet = servClient.getMandateRuleSetForPIDC(PIDC_ELEMENT_ID4);
    assertFalse("Response should not be null or empty", (ruleSet == null));
  }

  /**
   * Passing variant id for Pidc Version (test_sharavan_v1)-> wherein mandatory rule set attribute is not
   * defined(Negative Scenario)
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetMandateRuleSetForPIDCNegative1() throws ApicWebServiceException {
    RuleSetServiceClient servClient = new RuleSetServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Mandatory Rule Set attribute not defined");
    servClient.getMandateRuleSetForPIDC(PIDC_ELEMENT_ID11);
    fail("Expected exception not thrown");
  }

  /**
   * Passing version id for Pidc Version (Honda 1.0l MED1793 v1.1)-> wherein pid card also has variants(Negative
   * Scenario)
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetMandateRuleSetForPIDCNegative2() throws ApicWebServiceException {
    RuleSetServiceClient servClient = new RuleSetServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Invalid PIDC Element ID");
    servClient.getMandateRuleSetForPIDC(PIDC_ELEMENT_ID2);
    fail("Invalid PIDC Element ID");
  }

  /**
   * Passing deleted variant id for Pidc Version (Honda 1.0l MED1793 v1.1)(Negative Scenario)
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetMandateRuleSetForPIDCNegative3() throws ApicWebServiceException {
    RuleSetServiceClient servClient = new RuleSetServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Invalid PIDC Element ID");
    servClient.getMandateRuleSetForPIDC(PIDC_ELEMENT_ID3);
    fail("Invalid PIDC Element ID");
  }

  /**
   * Create Ruleset Service Negative cases - To check if the Ruleset is aleady available
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateRuleSetAlreadyAvailableNegative() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("The Rule Set with the name '" + RULESET_NAME + "' already exists.");
    RuleSetServiceClient ruleSetServiceClient = new RuleSetServiceClient();
    ruleSetServiceClient.setClientConfiguration(createClientConfigTestUser("SAY8COB"));
    ruleSetServiceClient.create(getRuleSetInputData(RULESET_NAME, RULESET_DESC));
  }

  /**
   * Create Ruleset Service Negative cases - To check if user has admin access
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateRuleSetNameNULLNegative() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("The Rule Set name cannot be null or empty.");
    RuleSetServiceClient ruleSetServiceClient = new RuleSetServiceClient();
    ruleSetServiceClient.setClientConfiguration(createClientConfigTestUser("SAY8COB"));
    ruleSetServiceClient.create(getRuleSetInputData(RULESET_NAME_NULL, RULESET_DESC));
  }

  /**
   * Create Ruleset Service Negative cases - To check if input ruleset name is null
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateRuleSetNoAdminAccessNegative() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("The user don't have special admin access rights to create rule set.");
    RuleSetServiceClient ruleSetServiceClient = new RuleSetServiceClient();
    ruleSetServiceClient.setClientConfiguration(createClientConfigTestUser("CTN8COB"));
    ruleSetServiceClient.create(getRuleSetInputData(RULESET_NAME, RULESET_DESC));
  }

  /**
   *
   */
  private RuleSetInputData getRuleSetInputData(final String ruleSetName, final String ruleSetDesc) {
    RuleSetInputData ruleSetInputData = new RuleSetInputData();
    Set<Long> ruleSetOwnerIds = new HashSet<>();
    ruleSetOwnerIds.add(937778116l);
    RuleSet ruleSet = new RuleSet();
    ruleSet.setName(ruleSetName);
    ruleSet.setDescription(ruleSetDesc);
    ruleSetInputData.setRuleSet(ruleSet);
    ruleSetInputData.setRuleSetOwnerIds(ruleSetOwnerIds);
    return ruleSetInputData;
  }
}
