/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

import com.bosch.caltool.icdm.model.a2l.RuleSetRulesResponse;
import com.bosch.caltool.icdm.model.a2l.SSDMessageWrapper;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.ConfigBasedRuleInput;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.ReviewRuleExt;
import com.bosch.caltool.icdm.model.cdr.ReviewRuleParamCol;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author AND4COB
 */
public class RuleSetRuleServiceClientTest extends AbstractRestClientTest {

  /**
   *
   */
  private static final String RESPONSE_CODE_SHOULD_BE_ZERO = "Response Code should be zero";
  /**
   *
   */
  private static final String CUSTSPEC = "CUSTSPEC";
  /**
   * PARAM ID for AccMon_aPtdMax_C
   */
  private static final int PARAM_ID = 1495580477;
  /**
   *
   */
  private static final String REVIEW_RULE_UPDATED_SHOULD_NOT_BE_NULL = "Review rule updated should not be null";
  private static final String RESPONSE_SHOULD_NOT_BE_NULL = "Response should not be null";
  private static final String RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY = "Response should not be null or empty";
  /**
   * RuleSet Id for TestRuleset INT
   */
  private static final Long RSET_ID = 14276249227L;
  /**
   * RuleSet Id for HN Rule Set with attribute dependencies
   */
  private static final Long RSET_ID1 = 1525167327L;
  /**
   * RuleSet Id for AirCtl_pAirHi_C_3
   */
  private static final Long RSET_ID2 = 1495580477L;
  private static final String PARAM_UNICODE_TEST = "AirCtl_pAirHi_C";
  private static final String PARAM_RULESET_TEST = "AccMon_aPtdMax_C";


  /**
   * Creation of an object of ReviewRuleParamCol
   */

  private ReviewRuleParamCol<RuleSet> createRvwRuleParamCol() throws ApicWebServiceException {
    RuleSetServiceClient ruleSetServiceClient = new RuleSetServiceClient();
    RuleSet ruleSetToTest = ruleSetServiceClient.get(RSET_ID2);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, ruleSetToTest);

    ReviewRuleParamCol<RuleSet> paramCol = new ReviewRuleParamCol<>();
    paramCol.setParamCollection(ruleSetToTest);

    RuleSetRulesResponse ruleSetRulesResponse = ruleSetServiceClient.getRuleSetParamRules(RSET_ID2);
    Map<String, List<ReviewRule>> rulesMap = ruleSetRulesResponse.getReviewRuleMap();
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, rulesMap.isEmpty());
    List<ReviewRule> reviewRuleList = rulesMap.get(RuleSetRuleServiceClientTest.PARAM_RULESET_TEST);

    ReviewRule reviewRule = reviewRuleList.get(0);
    paramCol.setReviewRule(reviewRule);
    paramCol.setReviewRuleList(reviewRuleList);
    return paramCol;
  }

  /**
   * Creation of an object of ConfigBasedRuleInput
   */

  private ConfigBasedRuleInput<RuleSet> createConfigBasedRuleInput() throws ApicWebServiceException {

    ConfigBasedRuleInput<RuleSet> configBasedRuleInput = new ConfigBasedRuleInput<>();
    // Instances: SortedSet<AttributeValueModel> attrValueModSet, List<String> labelNames, ReviewRuleParamCol<C>
    // paramCol

    RuleSetServiceClient ruleSetServiceClient = new RuleSetServiceClient();
    RuleSet ruleSetToTest = ruleSetServiceClient.get(RSET_ID1);// RSET_ID1 contains attribute dependencies
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, ruleSetToTest);

    RuleSetRulesResponse ruleSetRulesResponse = ruleSetServiceClient.getRuleSetParamRules(RSET_ID1);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, ruleSetRulesResponse);

    Map<Long, Attribute> attrMap = ruleSetRulesResponse.getAttrObjMap();
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, attrMap.isEmpty());
    Attribute attribute = attrMap.get(788369133L); // key:ATTR_ID

    AttributeValueServiceClient attributeValueServiceClient = new AttributeValueServiceClient();
    Map<Long, Map<Long, AttributeValue>> attributeValMap =
        attributeValueServiceClient.getValuesByAttribute(attribute.getId());
    AttributeValue attributeValue = attributeValMap.get(attribute.getId()).get(789808114L); // VALUE_ID = 789808114L

    // creation of an instance of AttributeValueModel
    AttributeValueModel attributeValueModel = new AttributeValueModel();
    attributeValueModel.setAttr(attribute);
    attributeValueModel.setValue(attributeValue);

    SortedSet<AttributeValueModel> attrValueModSet = new TreeSet<>();
    attrValueModSet.add(attributeValueModel);
    configBasedRuleInput.setAttrValueModSet(attrValueModSet);

    Map<String, RuleSetParameter> paramMap = ruleSetRulesResponse.getParamMap();
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, paramMap.isEmpty());
    List<String> labelNames = new ArrayList<>(paramMap.keySet());
    configBasedRuleInput.setLabelNames(labelNames);

    ReviewRuleParamCol<RuleSet> reviewRuleParamCol = new ReviewRuleParamCol<>();
    reviewRuleParamCol.setParamCollection(ruleSetToTest);
    configBasedRuleInput.setParamCol(reviewRuleParamCol);
    return configBasedRuleInput;

  }

  /**
   * Test method for {@link RuleSetRuleServiceClient#readRules(ReviewRuleParamCol)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testReadRules() throws ApicWebServiceException {
    ReviewRuleParamCol<RuleSet> paramCol = createRvwRuleParamCol();

    RuleSetRuleServiceClient servClient = new RuleSetRuleServiceClient();
    ReviewRuleParamCol<RuleSet> ret = servClient.readRules(paramCol);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, ret);

    // validation of a review rule
    ReviewRule retreviewRule = ret.getReviewRule();
    testReviewRule(retreviewRule);

    // validation of paramCollection
    ParamCollection paramCollection = ret.getParamCollection();
    testParamCollection(paramCollection);
  }

  /**
   * Test method for {@link RuleSetRuleServiceClient#create(ReviewRuleParamCol)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testCreateRuleSetRulesUnicode() throws ApicWebServiceException {

    RuleSetServiceClient ruleSetServiceClient = new RuleSetServiceClient();
    RuleSet ruleSetToTest = ruleSetServiceClient.get(RSET_ID2);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, ruleSetToTest);

    // Create new ruleset rule without unicode remarks
    validateCreateRuleSetRuleUnicode(ruleSetServiceClient, ruleSetToTest, null);

    // Create new ruleset rule with unicode remarks
    validateCreateRuleSetRuleUnicode(ruleSetServiceClient, ruleSetToTest, "chinese :这只是一个测试备注！ 这只是一个测试备注！这只是一个测试备注！");
  }

  /**
   * @param ruleSetServiceClient
   * @param ruleSetToTest
   * @param string
   * @return
   * @throws ApicWebServiceException
   */
  private ReviewRule validateCreateRuleSetRuleUnicode(final RuleSetServiceClient ruleSetServiceClient,
      final RuleSet ruleSetToTest, final String unicodeRemarks)
      throws ApicWebServiceException {

    // Initialize parameter collection
    ReviewRuleParamCol<RuleSet> paramCol = new ReviewRuleParamCol<>();
    paramCol.setParamCollection(ruleSetToTest);
    RuleSetRulesResponse ruleSetRulesResponse = ruleSetServiceClient.getRuleSetParamRules(RSET_ID2);
    Map<String, List<ReviewRule>> rulesMap = ruleSetRulesResponse.getReviewRuleMap();
    Map<String, RuleSetParameter> paramMap = ruleSetRulesResponse.getParamMap();
    paramCol.setReviewRuleList(rulesMap.get(RuleSetRuleServiceClientTest.PARAM_UNICODE_TEST));
    RuleSetParameter paramWithoutRule = paramMap.get(RuleSetRuleServiceClientTest.PARAM_UNICODE_TEST);

    // Delete existing rule
    RuleSetRuleServiceClient servClient = new RuleSetRuleServiceClient();
    if (null != paramCol.getReviewRuleList()) {
      servClient.delete(paramCol);
    }

    // creating a rule for that paramter
    createReviewRuleObj(unicodeRemarks, paramCol, paramWithoutRule);

    // invoke create
    SSDMessageWrapper retCreate = servClient.create(paramCol);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, retCreate);
    assertEquals("Value should be zero", 0, retCreate.getCode());

    RuleSetRulesResponse ruleSetRulesResponseNew = ruleSetServiceClient.getRuleSetParamRules(RSET_ID2);
    Map<String, List<ReviewRule>> rulesMapNew = ruleSetRulesResponseNew.getReviewRuleMap();
    ReviewRule newRevRule = rulesMapNew.get(RuleSetRuleServiceClientTest.PARAM_UNICODE_TEST).get(0);
    assertNotNull("Review rule created should not be null", newRevRule);
    assertEquals("Review rule created should  be qual", unicodeRemarks, newRevRule.getUnicodeRemarks());
    return newRevRule;
  }

  /**
   * @param unicodeRemarks
   * @param paramCol
   * @param paramWithoutRule
   */
  private void createReviewRuleObj(final String unicodeRemarks, final ReviewRuleParamCol<RuleSet> paramCol,
      final RuleSetParameter paramWithoutRule) {
    ReviewRule newReviewRule = new ReviewRule();
    newReviewRule.setParameterName(paramWithoutRule.getName());
    newReviewRule.setDcm2ssd(false);
    newReviewRule.setRevId(new BigDecimal("1"));
    newReviewRule.setReviewMethod("M");
    newReviewRule.setLowerLimit(new BigDecimal("15"));
    newReviewRule.setUpperLimit(new BigDecimal("20"));
    newReviewRule.setParamClass(paramWithoutRule.getpClassText());
    newReviewRule.setValueType("VALUE");
    newReviewRule.setHint("Created from JUnit");
    newReviewRule.setRuleCreatedUser("DJA7COB");
    if (null != unicodeRemarks) {
      newReviewRule.setUnicodeRemarks(unicodeRemarks);
    }
    List<ReviewRule> reviewRuleList = new ArrayList<>();
    reviewRuleList.add(newReviewRule);
    paramCol.setReviewRule(newReviewRule);
    paramCol.setReviewRuleList(reviewRuleList);
  }


  /**
   * Test method for {@link RuleSetRuleServiceClient#update(ReviewRuleParamCol)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testUpdateRulesetRuleWithUnicode() throws ApicWebServiceException {
    RuleSetServiceClient ruleSetServiceClient = new RuleSetServiceClient();
    RuleSet ruleSetToTest = ruleSetServiceClient.get(RSET_ID2);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, ruleSetToTest);

    // Create new ruleset rule
    ReviewRule newRevRule = validateCreateRuleSetRuleUnicode(ruleSetServiceClient, ruleSetToTest, null);

    ReviewRuleParamCol<RuleSet> paramCol = new ReviewRuleParamCol<>();
    paramCol.setParamCollection(ruleSetToTest);

    // Update ruleset rule only
    ReviewRule ruleSetRuleUpdated = validateUpdateRuleSetRule(newRevRule, paramCol, ruleSetServiceClient);

    // Update unicode remark of ruleset rule only
    ReviewRule ruleSetRuleUnicodeUpdated = validateUpdateUnicodeRemarks(ruleSetRuleUpdated, paramCol,
        ruleSetServiceClient, "Ruleset Rule updated (only unicode remarks)");

    // Update both unicode remark and ruleset rule
    validateUpdateRuleSetRuleAndUnicodeRmrk(ruleSetRuleUnicodeUpdated, paramCol, ruleSetServiceClient);
  }

  /**
   * Test method for {@link RuleSetRuleServiceClient#delete(ReviewRuleParamCol)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testDeleteRulesetRuleWithUnicode() throws ApicWebServiceException {
    RuleSetServiceClient ruleSetServiceClient = new RuleSetServiceClient();
    RuleSet ruleSetToTest = ruleSetServiceClient.get(RSET_ID2);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, ruleSetToTest);

    // Create new ruleset rule with unicode remarks
    ReviewRule newRevRule = validateCreateRuleSetRuleUnicode(ruleSetServiceClient, ruleSetToTest,
        "japanese : これは単なるテストコメントです。 これは単なるテストコメントです。これは単なるテストコメントです！");

    ReviewRuleParamCol<RuleSet> paramCol = new ReviewRuleParamCol<>();
    paramCol.setParamCollection(ruleSetToTest);

    // validate Rule set rule unicode remark Deletion (remove only unicode remarks)
    ReviewRule ruleSetRuleUnicodeDeleted =
        validateUpdateUnicodeRemarks(newRevRule, paramCol, ruleSetServiceClient, null);

    validateUpdateUnicodeRemarks(ruleSetRuleUnicodeDeleted, paramCol, ruleSetServiceClient,
        "Unicode Remarks added again");

    // Initialize parameter collection
    RuleSet ruleSetToTestDel = ruleSetServiceClient.get(RSET_ID2);
    ReviewRuleParamCol<RuleSet> paramColDel = new ReviewRuleParamCol<>();
    paramColDel.setParamCollection(ruleSetToTestDel);
    RuleSetRulesResponse ruleSetRulesResponse = ruleSetServiceClient.getRuleSetParamRules(RSET_ID2);
    Map<String, List<ReviewRule>> rulesMap = ruleSetRulesResponse.getReviewRuleMap();
    paramColDel.setReviewRuleList(rulesMap.get(RuleSetRuleServiceClientTest.PARAM_UNICODE_TEST));

    // Delete existing rule (unicode remark should not be deleted)
    RuleSetRuleServiceClient servClient = new RuleSetRuleServiceClient();
    servClient.delete(paramColDel);
  }

  /**
   * @param newRevRule
   * @param paramCol
   * @param ruleSetServiceClient
   * @param unicodeRemarks
   * @return
   * @throws ApicWebServiceException
   */
  private ReviewRule validateUpdateUnicodeRemarks(final ReviewRule newRevRule,
      final ReviewRuleParamCol<RuleSet> paramCol, final RuleSetServiceClient ruleSetServiceClient,
      final String unicodeRemarks)
      throws ApicWebServiceException {
    newRevRule.setUnicodeRemarks(unicodeRemarks);
    paramCol.setReviewRule(newRevRule);
    RuleSetRuleServiceClient servClient = new RuleSetRuleServiceClient();
    servClient.update(paramCol);

    RuleSetRulesResponse ruleSetRulesResponseNew = ruleSetServiceClient.getRuleSetParamRules(RSET_ID2);
    Map<String, List<ReviewRule>> rulesMapNew = ruleSetRulesResponseNew.getReviewRuleMap();
    ReviewRule revRuleUpdated = rulesMapNew.get(RuleSetRuleServiceClientTest.PARAM_UNICODE_TEST).get(0);
    assertNotNull(REVIEW_RULE_UPDATED_SHOULD_NOT_BE_NULL, revRuleUpdated);
    assertEquals("Unicode Remarks should be same", unicodeRemarks, revRuleUpdated.getUnicodeRemarks());
    assertArrayEquals("New rule remark should not be created", new long[] { newRevRule.getRevId().longValue() },
        new long[] { revRuleUpdated.getRevId().longValue() });
    return revRuleUpdated;
  }

  /**
   * @param newRevRule
   * @param paramCol
   * @param ruleSetServiceClient
   * @throws ApicWebServiceException
   */
  private void validateUpdateRuleSetRuleAndUnicodeRmrk(final ReviewRule newRevRule,
      final ReviewRuleParamCol<RuleSet> paramCol, final RuleSetServiceClient ruleSetServiceClient)
      throws ApicWebServiceException {
    newRevRule.setHint("Remark updated from Junit (rule remark and unicode remark)");
    String unicodeRemarks = "Unicode Remark updated from Junit (rule remark and unicode remark)";
    newRevRule.setUnicodeRemarks(unicodeRemarks);
    paramCol.setReviewRule(newRevRule);
    RuleSetRuleServiceClient servClient = new RuleSetRuleServiceClient();
    servClient.update(paramCol);

    RuleSetRulesResponse ruleSetRulesResponseNew = ruleSetServiceClient.getRuleSetParamRules(RSET_ID2);
    Map<String, List<ReviewRule>> rulesMapNew = ruleSetRulesResponseNew.getReviewRuleMap();
    ReviewRule revRuleUpdated = rulesMapNew.get(RuleSetRuleServiceClientTest.PARAM_UNICODE_TEST).get(0);
    assertNotNull(REVIEW_RULE_UPDATED_SHOULD_NOT_BE_NULL, revRuleUpdated);
    assertEquals("Unicode Remarks should be same", unicodeRemarks, revRuleUpdated.getUnicodeRemarks());
    assertEquals("New rule remark should be created (rev Id)", revRuleUpdated.getRevId().longValue(),
        newRevRule.getRevId().longValue() + 1L);
  }

  /**
   * @param newRevRule
   * @param paramCol
   * @param ruleSetServiceClient
   * @return
   * @throws ApicWebServiceException
   */
  private ReviewRule validateUpdateRuleSetRule(final ReviewRule newRevRule, final ReviewRuleParamCol<RuleSet> paramCol,
      final RuleSetServiceClient ruleSetServiceClient)
      throws ApicWebServiceException {
    newRevRule.setHint("Remark updated from Junit (rule remarks only)");
    paramCol.setReviewRule(newRevRule);
    RuleSetRuleServiceClient servClient = new RuleSetRuleServiceClient();
    servClient.update(paramCol);

    RuleSetRulesResponse ruleSetRulesResponseNew = ruleSetServiceClient.getRuleSetParamRules(RSET_ID2);
    Map<String, List<ReviewRule>> rulesMapNew = ruleSetRulesResponseNew.getReviewRuleMap();
    ReviewRule revRuleUpdated = rulesMapNew.get(RuleSetRuleServiceClientTest.PARAM_UNICODE_TEST).get(0);
    assertNotNull(REVIEW_RULE_UPDATED_SHOULD_NOT_BE_NULL, revRuleUpdated);
    assertNull("Rule remark (unicode) should be null", revRuleUpdated.getUnicodeRemarks());
    assertArrayEquals("New rule remark should be created", new long[] { newRevRule.getRevId().longValue() + 1L },
        new long[] { revRuleUpdated.getRevId().longValue() });
    return revRuleUpdated;
  }

  /**
   * @param reviewRule
   */
  private void testReviewRule(final ReviewRule reviewRule) {
    assertEquals("Maturity level is equal", "STANDARD", reviewRule.getMaturityLevel());
    assertEquals("paramClass is equal", CUSTSPEC, reviewRule.getParamClass());
    assertEquals("Created User is equal", "DJA7COB", reviewRule.getRuleCreatedUser());
    assertEquals("Review Method is equal", "M", reviewRule.getReviewMethod());
    assertEquals("Unit is equal", "km", reviewRule.getUnit());
  }


  /**
   * @param paramCollection
   */
  private void testParamCollection(final ParamCollection paramCollection) {
    assertEquals("Attribute Value Id is equal", Long.valueOf(PARAM_ID), paramCollection.getId());
    assertEquals("Created user is equal", "DGS_ICDM", paramCollection.getCreatedUser());
    assertEquals("Desc_Eng is equal", "AirCtl_pAirHi_C_3", paramCollection.getDescription());
  }


  /**
   * Test method for {@link RuleSetRuleServiceClient#readRulesForDependency(ConfigBasedRuleInput)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testReadRulesForDependency() throws ApicWebServiceException {

    ConfigBasedRuleInput<RuleSet> configBasedRuleInput = createConfigBasedRuleInput();

    RuleSetRuleServiceClient serviceClient = new RuleSetRuleServiceClient();
    Map<String, List<ReviewRule>> depRules = serviceClient.readRulesForDependency(configBasedRuleInput);
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, depRules.isEmpty());
    List<ReviewRule> reviewRuleList = depRules.get("InjSys_facCteHosMin_C"); // key: Parameter_Name
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, reviewRuleList);
    ReviewRule reviewRule = reviewRuleList.get(0);

    // validation of a review rule
    testReviewRule1(reviewRule);
  }

  /**
   * @param reviewRule
   */
  private void testReviewRule1(final ReviewRule reviewRule) {
    assertEquals("Maturity level is equal", null, reviewRule.getMaturityLevel());
    assertEquals("paramClass is equal", CUSTSPEC, reviewRule.getParamClass());
    assertEquals("Created User is equal", "RGO7COB", reviewRule.getRuleCreatedUser());
    assertEquals("Review Method is equal", "M", reviewRule.getReviewMethod());
    assertEquals("Unit is equal", null, reviewRule.getUnit());
  }

  /**
   * Test method for {@link RuleSetRuleServiceClient#searchRuleForDep(ConfigBasedRuleInput)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testSearchRuleForDep() throws ApicWebServiceException {

    ConfigBasedRuleInput<RuleSet> configBasedRuleInput = createConfigBasedRuleInput();

    RuleSetRuleServiceClient serviceClient = new RuleSetRuleServiceClient();
    Map<String, List<ReviewRule>> depRules = serviceClient.searchRuleForDep(configBasedRuleInput);
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, depRules.isEmpty());
    List<ReviewRule> reviewRuleList = depRules.get("InjSys_facCteHosMin_C"); // key: Parameter_Name
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, reviewRuleList);
    ReviewRule reviewRule = reviewRuleList.get(0);


    // validation of a review rule object
    testReviewRule1(reviewRule);
  }

  /**
   * Test method for {@link RuleSetRuleServiceClient#getRuleHistory(ReviewRuleParamCol)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetRuleHistory() throws ApicWebServiceException {

    ReviewRuleParamCol<RuleSet> paramCol = createRvwRuleParamCol();

    RuleSetRuleServiceClient servClient = new RuleSetRuleServiceClient();
    List<ReviewRuleExt> retReviewRuleList = servClient.getRuleHistory(paramCol);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, retReviewRuleList);

    ReviewRule retreviewRule = retReviewRuleList.get(0);

    // validation of a review rule
    testReviewRule(retreviewRule);
  }

  /**
   * Test method for {@link RuleSetRuleServiceClient#getCompliRuleHistory(ReviewRuleParamCol)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetCompliRuleHistory() throws ApicWebServiceException {
    ReviewRuleParamCol<RuleSet> paramCol = createRvwRuleParamCol();

    RuleSetRuleServiceClient servClient = new RuleSetRuleServiceClient();
    List<ReviewRuleExt> retReviewRuleList = servClient.getCompliRuleHistory(paramCol);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, retReviewRuleList);

    ReviewRule retreviewRule = retReviewRuleList.get(0);

    // validation of a review rule
    testReviewRule(retreviewRule);
  }

  /**
   * Test method for {@link RuleSetRuleServiceClient#create(ReviewRuleParamCol)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testCreateUpdateDelete() throws ApicWebServiceException {

    RuleSetServiceClient ruleSetServiceClient = new RuleSetServiceClient();
    RuleSet ruleSetToTest = ruleSetServiceClient.get(RSET_ID);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, ruleSetToTest);
    ReviewRuleParamCol<RuleSet> paramCol = new ReviewRuleParamCol<>();
    paramCol.setParamCollection(ruleSetToTest);
    RuleSetRulesResponse ruleSetRulesResponse = ruleSetServiceClient.getRuleSetParamRules(RSET_ID);
    Map<String, List<ReviewRule>> rulesMap = ruleSetRulesResponse.getReviewRuleMap();
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, rulesMap.isEmpty());
    Map<String, RuleSetParameter> paramMap = ruleSetRulesResponse.getParamMap();
    // Looping through all Parameters in Rule Set and finding param without Rule
    String paramWithoutRule = "";
    for (String p : paramMap.keySet()) {
      boolean flag = true;
      for (String r : rulesMap.keySet()) {
        if (r.equals(p)) {
          flag = false;
          break;
        }
      }
      if (flag) {
        paramWithoutRule = p;
        break;
      }
    }

    // creating a rule for that paramter
    ReviewRule newReviewRule = new ReviewRule();
    newReviewRule.setParameterName(paramWithoutRule);
    newReviewRule.setDcm2ssd(false);
    newReviewRule.setRevId(new BigDecimal("1"));
    newReviewRule.setReviewMethod("M");
    newReviewRule.setLowerLimit(new BigDecimal("15"));
    newReviewRule.setUpperLimit(new BigDecimal("20"));
    newReviewRule.setParamClass(CUSTSPEC);
    newReviewRule.setValueType("VALUE");
    newReviewRule.setRuleCreatedUser("AND4COB");
    List<ReviewRule> reviewRuleList = new ArrayList<>();
    reviewRuleList.add(newReviewRule);
    paramCol.setReviewRule(newReviewRule);
    paramCol.setReviewRuleList(reviewRuleList);
    RuleSetRuleServiceClient servClient = new RuleSetRuleServiceClient();

    // invoke create
    SSDMessageWrapper retCreate = servClient.create(paramCol);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, retCreate);
    assertEquals(RESPONSE_CODE_SHOULD_BE_ZERO, 0, retCreate.getCode());

    // Fetching the created Review Rule for update
    ReviewRule createdReviewRule =
        ruleSetServiceClient.getRuleSetParamRules(RSET_ID).getReviewRuleMap().get(paramWithoutRule).get(0);
    createdReviewRule.setLowerLimit(new BigDecimal("15"));
    createdReviewRule.setUpperLimit(new BigDecimal("25"));

    paramCol.setReviewRule(createdReviewRule);

    // update
    SSDMessageWrapper retUpdate = servClient.update(paramCol);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, retUpdate);
    assertEquals(RESPONSE_CODE_SHOULD_BE_ZERO, 0, retUpdate.getCode());

    // Fetching the Updated Review Rule
    ReviewRule updatedReviewRule =
        ruleSetServiceClient.getRuleSetParamRules(RSET_ID).getReviewRuleMap().get(paramWithoutRule).get(0);
    // Setting the updated Review Rule in List for Delete operation
    List<ReviewRule> updatedReviewRuleList = new ArrayList<>();
    updatedReviewRuleList.add(updatedReviewRule);
    paramCol.setReviewRuleList(updatedReviewRuleList);

    // delete
    SSDMessageWrapper retDelete = servClient.delete(paramCol);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, retDelete);
    assertEquals(RESPONSE_CODE_SHOULD_BE_ZERO, 0, retDelete.getCode());
  }
}
