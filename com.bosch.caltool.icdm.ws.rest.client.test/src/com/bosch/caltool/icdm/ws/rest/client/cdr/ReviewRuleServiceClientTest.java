/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.SSDMessageWrapper;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.ConfigBasedRuleInput;
import com.bosch.caltool.icdm.model.cdr.CreateCheckValRuleModel;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.ReviewRuleExt;
import com.bosch.caltool.icdm.model.cdr.ReviewRuleParamCol;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.a2l.FunctionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Test class for Review Rule Service Client
 *
 * @author pdh2cob
 */
public class ReviewRuleServiceClientTest extends AbstractRestClientTest {

  private static final int SSD_MSG_CODE_SUCCESS = 0;
  private static final String SSD_MSG_DESCR_SUCCESS = "Operation successful";


  private static final String PARAMETER_1 = "AZKRLDYN";
  private static final String PARAMETER_2 = "ZKRKENDYN";
  private static final String PARAMETER_3 = "APP_swtHiImpdEna_C";
  private static final String PARAMETER_4 = "DFC_CtlMsk2.DFC_SFTYRTDT_C";
  private static final String FUNCTION_NAME_1 = "BBKR";
  private static final String FUNCTION_NAME_2 = "APP_VD";
  private static final String FUNCTION_UNICODE_TEST = "ABK";
  private static final String PARAMETER_UNICODE_TEST = "ABK_TON_CCM";

  private final ReviewRuleServiceValidation validation = new ReviewRuleServiceValidation();

  /**
   * @throws ApicWebServiceException - exception from service
   */
  @Test
  public void testCreateUpdateDeleteRuleWithDep() throws ApicWebServiceException {
    SortedSet<AttributeValueModel> attrValSet = new TreeSet<>();

    // apm partnumber
    AttributeValueModel model1 = new AttributeValueModel();
    model1.setAttr(new AttributeServiceClient().get(776864698L));
    model1.setValue(new AttributeValueServiceClient().getById(777426167L));

    // apm supplier type
    AttributeValueModel model2 = new AttributeValueModel();
    model2.setAttr(new AttributeServiceClient().get(624067L));
    model2.setValue(new AttributeValueServiceClient().getById(1135792L));

    // customer/brand
    AttributeValueModel model3 = new AttributeValueModel();
    model3.setAttr(new AttributeServiceClient().get(36L));
    model3.setValue(new AttributeValueServiceClient().getById(671L));

    attrValSet.add(model1);
    attrValSet.add(model2);
    attrValSet.add(model3);

    // PreRequesite : Delete the rule if already exists
    SSDMessageWrapper wrapperPreDelete = deleteRule(attrValSet, PARAMETER_3, FUNCTION_NAME_2, false);
    LOG.debug("Pre delete rule status : {}", wrapperPreDelete);

    // create rule
    SSDMessageWrapper wrapperAfterCreation = createRule(attrValSet, PARAMETER_3, FUNCTION_NAME_2);

    // validate rule
    assertEquals(SSD_MSG_CODE_SUCCESS, wrapperAfterCreation.getCode());
    assertEquals(SSD_MSG_DESCR_SUCCESS, wrapperAfterCreation.getDescription());
    List<ReviewRule> reviewRuleListAfterCreate = getReviewRuleWithAttrDep(attrValSet, PARAMETER_3, FUNCTION_NAME_2);
    assertNotNull(reviewRuleListAfterCreate);
    this.validation.validateCreatedReviewRule(reviewRuleListAfterCreate.get(0), PARAMETER_3, true);


    // update rule
    SSDMessageWrapper wrapperAfterUpdate = updateRule(attrValSet, PARAMETER_3, FUNCTION_NAME_2);

    // validate rule
    assertEquals(SSD_MSG_CODE_SUCCESS, wrapperAfterUpdate.getCode());
    assertEquals(SSD_MSG_DESCR_SUCCESS, wrapperAfterUpdate.getDescription());
    List<ReviewRule> reviewRuleListAfterUpdate = getReviewRuleWithAttrDep(attrValSet, PARAMETER_3, FUNCTION_NAME_2);
    assertNotNull(reviewRuleListAfterUpdate);
    this.validation.validateUpdatedReviewRule(reviewRuleListAfterUpdate.get(0), PARAMETER_3, true);

    // delete rule
    SSDMessageWrapper wrapperAfterDelete = deleteRule(attrValSet, PARAMETER_3, FUNCTION_NAME_2);

    // validate rule
    assertEquals(SSD_MSG_CODE_SUCCESS, wrapperAfterDelete.getCode());
    assertEquals(SSD_MSG_DESCR_SUCCESS, wrapperAfterDelete.getDescription());
    List<ReviewRule> reviewRuleListAfterDelete = getReviewRuleWithAttrDep(attrValSet, PARAMETER_3, FUNCTION_NAME_2);
    assertNull(reviewRuleListAfterDelete);
  }

  /**
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testCreateUpdateDeleteRuleWithoutDep() throws ApicWebServiceException {
    // PreRequesite : Delete the rule if already exists
    SSDMessageWrapper wrapperPreDelete = deleteRule(new TreeSet<>(), PARAMETER_4, FUNCTION_NAME_1, false);
    LOG.debug("Pre delete rule status : {}", wrapperPreDelete);

    // create rule
    SSDMessageWrapper wrapperAfterCreation = createRule(new TreeSet<>(), PARAMETER_4, FUNCTION_NAME_1);

    // validate rule
    assertEquals(SSD_MSG_CODE_SUCCESS, wrapperAfterCreation.getCode());
    assertEquals(SSD_MSG_DESCR_SUCCESS, wrapperAfterCreation.getDescription());
    List<ReviewRule> reviewRuleListAfterCreate =
        getReviewRuleWithAttrDep(new TreeSet<>(), PARAMETER_4, FUNCTION_NAME_1);
    assertNotNull(reviewRuleListAfterCreate);
    this.validation.validateCreatedReviewRule(reviewRuleListAfterCreate.get(0), PARAMETER_4, false);

    // update rule
    SSDMessageWrapper wrapperAfterUpdate = updateRule(new TreeSet<>(), PARAMETER_4, FUNCTION_NAME_1);
    // validate rule
    assertEquals(SSD_MSG_CODE_SUCCESS, wrapperAfterUpdate.getCode());
    assertEquals(SSD_MSG_DESCR_SUCCESS, wrapperAfterUpdate.getDescription());
    List<ReviewRule> reviewRuleListAfterUpdate =
        getReviewRuleWithAttrDep(new TreeSet<>(), PARAMETER_4, FUNCTION_NAME_1);
    assertNotNull(reviewRuleListAfterUpdate);
    this.validation.validateUpdatedReviewRule(reviewRuleListAfterUpdate.get(0), PARAMETER_4, false);

    // delete rule
    SSDMessageWrapper wrapperAfterDelete = deleteRule(new TreeSet<>(), PARAMETER_4, FUNCTION_NAME_1);
    // validate rule
    assertEquals(SSD_MSG_CODE_SUCCESS, wrapperAfterDelete.getCode());
    assertEquals(SSD_MSG_DESCR_SUCCESS, wrapperAfterDelete.getDescription());
    List<ReviewRule> reviewRuleListAfterDelete =
        getReviewRuleWithAttrDep(new TreeSet<>(), PARAMETER_4, FUNCTION_NAME_1);
    assertNull(reviewRuleListAfterDelete);
  }

  /**
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testCreateRuleUnicodeRemarks() throws ApicWebServiceException {

    // delete rule
    deleteRuleUnicode(new TreeSet<>(), PARAMETER_UNICODE_TEST, FUNCTION_UNICODE_TEST);

    List<ReviewRule> reviewRuleListAfterDelete =
        getReviewRuleWithAttrDep(new TreeSet<>(), PARAMETER_UNICODE_TEST, FUNCTION_UNICODE_TEST);
    assertNull(reviewRuleListAfterDelete);

    // create rule without unicode remarks
    validateCreateFuncRuleUnicode(null);
    // delete rule
    deleteRuleUnicode(new TreeSet<>(), PARAMETER_UNICODE_TEST, FUNCTION_UNICODE_TEST);
    // create with unicode remarks
    validateCreateFuncRuleUnicode("japanese : これは単なるテストコメントです。 これは単なるテストコメントです。これは単なるテストコメントです！");
  }

  /**
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testUpdateRuleUnicodeRemarks() throws ApicWebServiceException {

    // delete rule
    deleteRuleUnicode(new TreeSet<>(), PARAMETER_UNICODE_TEST, FUNCTION_UNICODE_TEST);

    List<ReviewRule> reviewRuleListAfterDelete =
        getReviewRuleWithAttrDep(new TreeSet<>(), PARAMETER_UNICODE_TEST, FUNCTION_UNICODE_TEST);
    assertNull(reviewRuleListAfterDelete);

    ReviewRule newRevRule = validateCreateFuncRuleUnicode(null);

    ReviewRule updatedRevRule = validateUpdateFuncRule(newRevRule);
    ReviewRule unicodeupdatedRevRule = validateUpdateUnicodeRemark(updatedRevRule, "Only unicode remarks updated");
    validateUpdateFuncRuleAndUnicodeRmrk(unicodeupdatedRevRule);
  }

  /**
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testDeleteRuleUnicodeRemarks() throws ApicWebServiceException {

    // delete rule
    deleteRuleUnicode(new TreeSet<>(), PARAMETER_UNICODE_TEST, FUNCTION_UNICODE_TEST);

    List<ReviewRule> reviewRuleListAfterDelete =
        getReviewRuleWithAttrDep(new TreeSet<>(), PARAMETER_UNICODE_TEST, FUNCTION_UNICODE_TEST);
    assertNull(reviewRuleListAfterDelete);

    ReviewRule newRevRule = validateCreateFuncRuleUnicode("Junit - unicode remarks");

    // Only unicode remarks deleted
    ReviewRule unicodeDelRevRule = validateUpdateUnicodeRemark(newRevRule, null);

    // Rule updated with unicode remarks
    validateUpdateUnicodeRemark(unicodeDelRevRule, "Junit - unicode remarks");
    // delete rule with unicode remarks - rule will be deleted but unicode remarks will be still there in the database
    deleteRuleUnicode(new TreeSet<>(), PARAMETER_UNICODE_TEST, FUNCTION_UNICODE_TEST);
  }


  /**
   * @param unicodeupdatedRevRule
   * @throws ApicWebServiceException
   */
  private void validateUpdateFuncRuleAndUnicodeRmrk(final ReviewRule unicodeupdatedRevRule)
      throws ApicWebServiceException {
    // update rule
    String unicodeRmrk = "Function rule and unicode remarks updated";
    SSDMessageWrapper wrapperAfterUpdate = updateRuleUnicode(new TreeSet<>(), PARAMETER_UNICODE_TEST,
        FUNCTION_UNICODE_TEST, "Updated from junit - rule remarks and unicode remarks", unicodeRmrk);
    // validate rule
    assertEquals(SSD_MSG_CODE_SUCCESS, wrapperAfterUpdate.getCode());
    assertEquals(SSD_MSG_DESCR_SUCCESS, wrapperAfterUpdate.getDescription());
    List<ReviewRule> reviewRuleListAfterUpdate =
        getReviewRuleWithAttrDep(new TreeSet<>(), PARAMETER_UNICODE_TEST, FUNCTION_UNICODE_TEST);
    assertNotNull(reviewRuleListAfterUpdate);
    assertEquals("Review rule should be updated", reviewRuleListAfterUpdate.get(0).getRevId().longValue(),
        unicodeupdatedRevRule.getRevId().longValue() + 1L);
    assertEquals("Unicode remarks should be same", reviewRuleListAfterUpdate.get(0).getUnicodeRemarks(), unicodeRmrk);

  }

  /**
   * @param updatedRevRule
   * @return
   * @throws ApicWebServiceException
   */
  private ReviewRule validateUpdateUnicodeRemark(final ReviewRule updatedRevRule, final String unicodeRmrk)
      throws ApicWebServiceException {
    // update rule
    SSDMessageWrapper wrapperAfterUpdate =
        updateRuleUnicode(new TreeSet<>(), PARAMETER_UNICODE_TEST, FUNCTION_UNICODE_TEST, null, unicodeRmrk);
    // validate rule
    assertEquals(SSD_MSG_CODE_SUCCESS, wrapperAfterUpdate.getCode());
    assertEquals(SSD_MSG_DESCR_SUCCESS, wrapperAfterUpdate.getDescription());
    List<ReviewRule> reviewRuleListAfterUpdate =
        getReviewRuleWithAttrDep(new TreeSet<>(), PARAMETER_UNICODE_TEST, FUNCTION_UNICODE_TEST);
    assertNotNull(reviewRuleListAfterUpdate);
    assertEquals("Review rule should not be updated", reviewRuleListAfterUpdate.get(0).getRevId().longValue(),
        updatedRevRule.getRevId().longValue());
    assertEquals("Unicode remarks should be same", reviewRuleListAfterUpdate.get(0).getUnicodeRemarks(), unicodeRmrk);
    return reviewRuleListAfterUpdate.get(0);
  }

  /**
   * @param newRevRule
   * @return
   * @throws ApicWebServiceException
   */
  private ReviewRule validateUpdateFuncRule(final ReviewRule newRevRule) throws ApicWebServiceException {
    // update rule
    SSDMessageWrapper wrapperAfterUpdate = updateRuleUnicode(new TreeSet<>(), PARAMETER_UNICODE_TEST,
        FUNCTION_UNICODE_TEST, "Updated from junit - only rule remarks", null);
    // validate rule
    assertEquals(SSD_MSG_CODE_SUCCESS, wrapperAfterUpdate.getCode());
    assertEquals(SSD_MSG_DESCR_SUCCESS, wrapperAfterUpdate.getDescription());
    List<ReviewRule> reviewRuleListAfterUpdate =
        getReviewRuleWithAttrDep(new TreeSet<>(), PARAMETER_UNICODE_TEST, FUNCTION_UNICODE_TEST);
    assertNotNull(reviewRuleListAfterUpdate);
    assertEquals("Review rule should be updated", reviewRuleListAfterUpdate.get(0).getRevId().longValue(),
        newRevRule.getRevId().longValue() + 1L);
    return reviewRuleListAfterUpdate.get(0);
  }

  /**
   * @param treeSet
   * @param parameterUnicodeTest
   * @param functionUnicodeTest
   * @return
   * @throws ApicWebServiceException
   */
  private void deleteRuleUnicode(final TreeSet attrValSet, final String param, final String function)
      throws ApicWebServiceException {

    List<ReviewRule> reviewRuleList = getReviewRuleWithAttrDep(attrValSet, param, function);

    ReviewRuleParamCol<Function> parmCol = new ReviewRuleParamCol<Function>();
    parmCol.setParamCollection(getFunction(function).get(function));
    parmCol.setReviewRuleList(reviewRuleList);

    // delete
    if (null != parmCol.getReviewRuleList()) {
      new ReviewRuleServiceClient().delete(parmCol);
    }
  }

  /**
   * @return
   * @return
   * @throws ApicWebServiceException
   */
  private ReviewRule validateCreateFuncRuleUnicode(final String unicodeRemarks) throws ApicWebServiceException {
    // create rule
    SSDMessageWrapper wrapperAfterCreation =
        createRulewithUnicode(new TreeSet<>(), PARAMETER_UNICODE_TEST, FUNCTION_UNICODE_TEST, unicodeRemarks);
    // validate rule
    assertEquals(SSD_MSG_CODE_SUCCESS, wrapperAfterCreation.getCode());
    assertEquals(SSD_MSG_DESCR_SUCCESS, wrapperAfterCreation.getDescription());
    List<ReviewRule> reviewRuleListAfterCreate =
        getReviewRuleWithAttrDep(new TreeSet<>(), PARAMETER_UNICODE_TEST, FUNCTION_UNICODE_TEST);
    assertNotNull(reviewRuleListAfterCreate);
    assertEquals("Rule Remarks(Unicode) should be same", reviewRuleListAfterCreate.get(0).getUnicodeRemarks(),
        unicodeRemarks);
    return reviewRuleListAfterCreate.get(0);
  }


  /**
   * @param treeSet
   * @param parameterUnicodeTest
   * @param functionUnicodeTest
   * @param object
   * @return
   * @throws ApicWebServiceException
   */
  private SSDMessageWrapper createRulewithUnicode(final TreeSet attrValSet, final String param, final String function,
      final String unicodeRemarks)
      throws ApicWebServiceException {

    List<ReviewRule> ruleList = new ArrayList<>();

    final ReviewRule newRviewRule = createNewReviewRuleObject(param, function, attrValSet);
    newRviewRule.setUnicodeRemarks(unicodeRemarks);
    ruleList.add(newRviewRule);

    ReviewRuleParamCol<Function> parmCol = new ReviewRuleParamCol<Function>();
    parmCol.setParamCollection(getFunction(function).get(function));
    parmCol.setReviewRuleList(ruleList);

    // create rule
    return new ReviewRuleServiceClient().create(parmCol);

  }

  /**
   * Create rule with existing dependency
   *
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testCreateRuleExisting() throws ApicWebServiceException {
    SortedSet<AttributeValueModel> attrValSet = new TreeSet<>();
    List<ReviewRule> ruleList = new ArrayList<>();

    // apm partnumber
    AttributeValueModel model1 = new AttributeValueModel();
    model1.setAttr(new AttributeServiceClient().get(776864698L));
    model1.setValue(new AttributeValueServiceClient().getById(777426167L));

    // apm supplier type
    AttributeValueModel model2 = new AttributeValueModel();
    model2.setAttr(new AttributeServiceClient().get(624067L));
    model2.setValue(new AttributeValueServiceClient().getById(1135792L));

    // customer/brand
    AttributeValueModel model3 = new AttributeValueModel();
    model3.setAttr(new AttributeServiceClient().get(36L));
    model3.setValue(new AttributeValueServiceClient().getById(671L));

    attrValSet.add(model1);
    attrValSet.add(model2);
    attrValSet.add(model3);

    final ReviewRule newRviewRule = createNewReviewRuleObject(PARAMETER_2, FUNCTION_NAME_2, attrValSet);
    ruleList.add(newRviewRule);

    ReviewRuleParamCol<Function> parmCol = new ReviewRuleParamCol<>();
    parmCol.setParamCollection(getFunction(FUNCTION_NAME_2).get(FUNCTION_NAME_2));
    parmCol.setReviewRuleList(ruleList);
    // -25
    // Rule for the same combination of the dependency already exists
    SSDMessageWrapper wrapper = new ReviewRuleServiceClient().create(parmCol);

    assertEquals("Rule for the same combination of the dependency already exists", wrapper.getDescription());
    assertEquals(-22, wrapper.getCode());

  }

  /**
   * Call Create rule method with param collection = null
   *
   * @throws ApicWebServiceException exception from service
   */
  public void testCreateRuleNullParamCollection() throws ApicWebServiceException {
    List<ReviewRule> ruleList = new ArrayList<>();
    final ReviewRule newRviewRule = createNewReviewRuleObject(PARAMETER_2, FUNCTION_NAME_2, new TreeSet<>());
    ruleList.add(newRviewRule);

    ReviewRuleParamCol<Function> parmCol = new ReviewRuleParamCol<Function>();
    parmCol.setParamCollection(null);
    parmCol.setReviewRuleList(ruleList);

    new ReviewRuleServiceClient().create(parmCol);
  }

  /**
   * Call Create rule method with dependency = null
   *
   * @throws ApicWebServiceException exception from service
   */
  public void testCreateRuleNullDependency() throws ApicWebServiceException {
    List<ReviewRule> ruleList = new ArrayList<>();
    final ReviewRule newRviewRule = createNewReviewRuleObject(PARAMETER_2, FUNCTION_NAME_2, null);
    ruleList.add(newRviewRule);

    ReviewRuleParamCol<Function> parmCol = new ReviewRuleParamCol<Function>();
    parmCol.setParamCollection(getFunction(FUNCTION_NAME_2).get(FUNCTION_NAME_2));
    parmCol.setReviewRuleList(ruleList);

    new ReviewRuleServiceClient().create(parmCol);
  }

  /**
   * Call Create rule method with reviewRuleList = null
   *
   * @throws ApicWebServiceException exception from service
   */
  public void testCreateRuleNullReviewRuleList() throws ApicWebServiceException {
    ReviewRuleParamCol<Function> parmCol = new ReviewRuleParamCol<Function>();
    parmCol.setParamCollection(getFunction(FUNCTION_NAME_2).get(FUNCTION_NAME_2));
    parmCol.setReviewRuleList(null);
    new ReviewRuleServiceClient().create(parmCol);
  }

  /**
   * update rule with review rule=null
   *
   * @throws ApicWebServiceException exception from service
   */
  public void testUpdateRuleNullReviewRule() throws ApicWebServiceException {
    ReviewRuleParamCol<Function> parmCol = new ReviewRuleParamCol<Function>();
    parmCol.setParamCollection(getFunction(FUNCTION_NAME_2).get(FUNCTION_NAME_2));
    parmCol.setReviewRule(null);
    new ReviewRuleServiceClient().update(parmCol);
  }

  /**
   * update rule with param collection = null
   *
   * @throws ApicWebServiceException exception from service
   */
  public void testUpdateRuleNullParamCollection() throws ApicWebServiceException {
    ReviewRuleParamCol<Function> parmCol = new ReviewRuleParamCol<Function>();
    parmCol.setParamCollection(null);
    parmCol.setReviewRule(new ReviewRule());
    new ReviewRuleServiceClient().update(parmCol);
  }

  /**
   * delete rule with review rule list = null
   *
   * @throws ApicWebServiceException exception from service
   */
  public void testDeleteNullReviewRuleList() throws ApicWebServiceException {

    ReviewRuleParamCol<Function> parmCol = new ReviewRuleParamCol<Function>();
    parmCol.setParamCollection(getFunction(FUNCTION_NAME_2).get(FUNCTION_NAME_2));
    parmCol.setReviewRuleList(null);

    // delete
    new ReviewRuleServiceClient().delete(parmCol);
  }

  /**
   * delete rule with param collection = null
   *
   * @throws ApicWebServiceException exception from service
   */
  public void testDeleteNullParamCollection() throws ApicWebServiceException {

    ReviewRuleParamCol<Function> parmCol = new ReviewRuleParamCol<Function>();
    parmCol.setParamCollection(null);
    parmCol.setReviewRuleList(new ArrayList<>());

    // delete
    new ReviewRuleServiceClient().delete(parmCol);
  }


  /**
   * Test method for readRules()
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testReadRules() throws ApicWebServiceException {

    // create dummy review rule
    ReviewRule reviewRule = new ReviewRule();
    reviewRule.setParameterName(PARAMETER_1);

    // get function
    List<String> functionNameList = new ArrayList<>();
    functionNameList.add(FUNCTION_NAME_1);
    Map<String, Function> functionMap = new FunctionServiceClient().getFunctionsByName(functionNameList);

    // create input
    ReviewRuleParamCol<Function> funcReviewRule = new ReviewRuleParamCol<>();
    funcReviewRule.setReviewRule(reviewRule);
    funcReviewRule.setParamCollection(functionMap.get(FUNCTION_NAME_1));

    ReviewRuleParamCol<Function> output = new ReviewRuleServiceClient().readRules(funcReviewRule);

    assertTrue(CommonUtils.isNotEmpty(output.getReviewRuleList()));

    ReviewRule rule = null;
    for (ReviewRule revRule : output.getReviewRuleList()) {
      if (revRule.getRuleId().equals(new BigDecimal("3530253915"))) {
        rule = revRule;
      }
    }
    assertNotNull(rule);
    this.validation.validateReviewRule(rule, PARAMETER_1);
  }

  /**
   * @throws ApicWebServiceException exception from service
   */
  public void testReadRulesNullReviewRule() throws ApicWebServiceException {

    // get function
    List<String> functionNameList = new ArrayList<>();
    functionNameList.add(FUNCTION_NAME_1);
    Map<String, Function> functionMap = new FunctionServiceClient().getFunctionsByName(functionNameList);

    // create input
    ReviewRuleParamCol<Function> funcReviewRule = new ReviewRuleParamCol<>();
    funcReviewRule.setParamCollection(functionMap.get(FUNCTION_NAME_1));

    new ReviewRuleServiceClient().readRules(funcReviewRule);
  }

  /**
   * @throws ApicWebServiceException exception from service
   */
  public void testReadRulesNullParamCollection() throws ApicWebServiceException {
    // create dummy review rule
    ReviewRule reviewRule = new ReviewRule();
    reviewRule.setParameterName(PARAMETER_1);

    // create input
    ReviewRuleParamCol<Function> funcReviewRule = new ReviewRuleParamCol<>();
    funcReviewRule.setReviewRule(reviewRule);

    new ReviewRuleServiceClient().readRules(funcReviewRule);
  }


  /**
   * Test method for readRulesForDependency()
   *
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testReadRulesForDependency() throws ApicWebServiceException {

    AttributeValueModel attrValueModel = new AttributeValueModel();
    attrValueModel.setAttr(new AttributeServiceClient().get(391L));
    attrValueModel.setValue(new AttributeValueServiceClient().getById(396L));

    SortedSet<AttributeValueModel> attrValSet = new TreeSet<>();
    attrValSet.add(attrValueModel);

    List<ReviewRule> reviewRuleList = getReviewRuleWithAttrDep(attrValSet, PARAMETER_2, FUNCTION_NAME_1);
    assertEquals("Bitwise rule is equal", null, reviewRuleList.get(0).getBitWiseRule());
    this.validation.validateReviewRuleWithDependency(reviewRuleList.get(0), PARAMETER_2);

  }


  /**
   * @param attrValSet attr value dependency
   * @param paramName - parameter name
   * @param funcName - function name
   * @return List of review rules for given param, func
   * @throws ApicWebServiceException
   */
  private List<ReviewRule> getReviewRuleWithAttrDep(final SortedSet<AttributeValueModel> attrValSet,
      final String paramName, final String funcName)
      throws ApicWebServiceException {

    // create input
    ConfigBasedRuleInput<Function> configInput = new ConfigBasedRuleInput<>();

    List<String> labelNameList = new ArrayList<>();
    labelNameList.add(paramName);
    configInput.setLabelNames(labelNameList);

    configInput.setAttrValueModSet(attrValSet);

    ReviewRuleParamCol<Function> paramCol = new ReviewRuleParamCol<Function>();

    Map<String, Function> functionMap = getFunction(funcName);
    paramCol.setParamCollection(functionMap.get(funcName));
    configInput.setParamCol(paramCol);


    Map<String, List<ReviewRule>> reviewRuleMap = new ReviewRuleServiceClient().readRulesForDependency(configInput);

    return reviewRuleMap.get(paramName);


  }


  /**
   * @param funcName
   * @return
   * @throws ApicWebServiceException
   */
  private Map<String, Function> getFunction(final String funcName) throws ApicWebServiceException {
    // get function
    List<String> functionNameList = new ArrayList<>();
    functionNameList.add(funcName);
    Map<String, Function> functionMap = new FunctionServiceClient().getFunctionsByName(functionNameList);
    return functionMap;
  }

  /**
   * Test method for getRuleHistory()
   *
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testGetRuleHistory() throws ApicWebServiceException {
    SortedSet<AttributeValueModel> attrValSet = new TreeSet<>();

    // apm partnumber
    AttributeValueModel model1 = new AttributeValueModel();
    model1.setAttr(new AttributeServiceClient().get(776864698L));
    model1.setValue(new AttributeValueServiceClient().getById(777426167L));

    // apm supplier type
    AttributeValueModel model2 = new AttributeValueModel();
    model2.setAttr(new AttributeServiceClient().get(624067L));
    model2.setValue(new AttributeValueServiceClient().getById(1135792L));

    // customer/brand
    AttributeValueModel model3 = new AttributeValueModel();
    model3.setAttr(new AttributeServiceClient().get(36L));
    model3.setValue(new AttributeValueServiceClient().getById(154L));

    attrValSet.add(model1);
    attrValSet.add(model2);
    attrValSet.add(model3);

    List<ReviewRule> reviewRuleList = getReviewRuleWithAttrDep(attrValSet, PARAMETER_3, FUNCTION_NAME_2);

    // create input
    ConfigBasedRuleInput<Function> configInput = new ConfigBasedRuleInput<>();

    List<String> labelNameList = new ArrayList<>();
    labelNameList.add(PARAMETER_3);
    configInput.setLabelNames(labelNameList);

    configInput.setAttrValueModSet(attrValSet);

    ReviewRuleParamCol<Function> reviewRuleParamCol = new ReviewRuleParamCol<>();
    reviewRuleParamCol.setReviewRule(reviewRuleList.get(0));
    Map<String, Function> functionMap = getFunction(FUNCTION_NAME_2);
    reviewRuleParamCol.setParamCollection(functionMap.get(FUNCTION_NAME_2));

    List<ReviewRuleExt> ruleList = new ReviewRuleServiceClient().getRuleHistory(reviewRuleParamCol);
    assertEquals("Bitwise rule is equal", null, ruleList.get(0).getBitWiseRule());
    this.validation.validateReviewRuleForRuleHistory(ruleList.get(0));
  }

  /**
   * Test method for getRuleHistory()
   *
   * @throws ApicWebServiceException exception from service
   */
  public void testGetRuleHistoryNullReviewRule() throws ApicWebServiceException {

    SortedSet<AttributeValueModel> attrValSet = new TreeSet<>();

    // apm partnumber
    AttributeValueModel model1 = new AttributeValueModel();
    model1.setAttr(new AttributeServiceClient().get(776864698L));
    model1.setValue(new AttributeValueServiceClient().getById(777426167L));

    // apm supplier type
    AttributeValueModel model2 = new AttributeValueModel();
    model2.setAttr(new AttributeServiceClient().get(624067L));
    model2.setValue(new AttributeValueServiceClient().getById(1135792L));

    // customer/brand
    AttributeValueModel model3 = new AttributeValueModel();
    model3.setAttr(new AttributeServiceClient().get(36L));
    model3.setValue(new AttributeValueServiceClient().getById(154L));

    attrValSet.add(model1);
    attrValSet.add(model2);
    attrValSet.add(model3);


    // create input
    ConfigBasedRuleInput<Function> configInput = new ConfigBasedRuleInput<>();

    List<String> labelNameList = new ArrayList<>();
    labelNameList.add(PARAMETER_3);
    configInput.setLabelNames(labelNameList);

    configInput.setAttrValueModSet(attrValSet);

    ReviewRuleParamCol<Function> reviewRuleParamCol = new ReviewRuleParamCol<>();
    Map<String, Function> functionMap = getFunction(FUNCTION_NAME_2);
    reviewRuleParamCol.setParamCollection(functionMap.get(FUNCTION_NAME_2));

    new ReviewRuleServiceClient().getRuleHistory(reviewRuleParamCol);


  }

  /**
   * @throws ApicWebServiceException exception from service
   */
  public void testGetRuleHistoryNullParamCollection() throws ApicWebServiceException {

    SortedSet<AttributeValueModel> attrValSet = new TreeSet<>();

    // apm partnumber
    AttributeValueModel model1 = new AttributeValueModel();
    model1.setAttr(new AttributeServiceClient().get(776864698L));
    model1.setValue(new AttributeValueServiceClient().getById(777426167L));

    // apm supplier type
    AttributeValueModel model2 = new AttributeValueModel();
    model2.setAttr(new AttributeServiceClient().get(624067L));
    model2.setValue(new AttributeValueServiceClient().getById(1135792L));

    // customer/brand
    AttributeValueModel model3 = new AttributeValueModel();
    model3.setAttr(new AttributeServiceClient().get(36L));
    model3.setValue(new AttributeValueServiceClient().getById(154L));

    attrValSet.add(model1);
    attrValSet.add(model2);
    attrValSet.add(model3);

    List<ReviewRule> reviewRuleList = getReviewRuleWithAttrDep(attrValSet, PARAMETER_3, FUNCTION_NAME_2);

    // create input
    ConfigBasedRuleInput<Function> configInput = new ConfigBasedRuleInput<>();

    List<String> labelNameList = new ArrayList<>();
    labelNameList.add(PARAMETER_3);
    configInput.setLabelNames(labelNameList);

    configInput.setAttrValueModSet(attrValSet);

    ReviewRuleParamCol<Function> reviewRuleParamCol = new ReviewRuleParamCol<>();
    reviewRuleParamCol.setReviewRule(reviewRuleList.get(0));

    new ReviewRuleServiceClient().getRuleHistory(reviewRuleParamCol);


  }

  /**
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testGetRuleHistoryNullParameter() throws ApicWebServiceException {

    SortedSet<AttributeValueModel> attrValSet = new TreeSet<>();

    // apm partnumber
    AttributeValueModel model1 = new AttributeValueModel();
    model1.setAttr(new AttributeServiceClient().get(776864698L));
    model1.setValue(new AttributeValueServiceClient().getById(777426167L));

    // apm supplier type
    AttributeValueModel model2 = new AttributeValueModel();
    model2.setAttr(new AttributeServiceClient().get(624067L));
    model2.setValue(new AttributeValueServiceClient().getById(1135792L));

    // customer/brand
    AttributeValueModel model3 = new AttributeValueModel();
    model3.setAttr(new AttributeServiceClient().get(36L));
    model3.setValue(new AttributeValueServiceClient().getById(154L));

    attrValSet.add(model1);
    attrValSet.add(model2);
    attrValSet.add(model3);

    List<ReviewRule> reviewRuleList = getReviewRuleWithAttrDep(attrValSet, PARAMETER_3, FUNCTION_NAME_2);

    // create input
    ConfigBasedRuleInput<Function> configInput = new ConfigBasedRuleInput<>();


    configInput.setAttrValueModSet(attrValSet);

    ReviewRuleParamCol<Function> reviewRuleParamCol = new ReviewRuleParamCol<>();
    reviewRuleParamCol.setReviewRule(reviewRuleList.get(0));
    Map<String, Function> functionMap = getFunction(FUNCTION_NAME_2);
    reviewRuleParamCol.setParamCollection(functionMap.get(FUNCTION_NAME_2));

    List<ReviewRuleExt> ruleList = new ReviewRuleServiceClient().getRuleHistory(reviewRuleParamCol);
    assertEquals("Bitwise rule is equal", null, ruleList.get(0).getBitWiseRule());
    this.validation.validateReviewRuleForRuleHistory(ruleList.get(0));


  }


  /**
   * Test method for getCompliRuleHistory()
   *
   * @throws ApicWebServiceException - exception from service
   */
  @Test
  public void testGetCompliRuleHistory() throws ApicWebServiceException {

    SortedSet<AttributeValueModel> attrValSet = new TreeSet<>();

    // apm partnumber
    AttributeValueModel model1 = new AttributeValueModel();
    model1.setAttr(new AttributeServiceClient().get(776864698L));
    model1.setValue(new AttributeValueServiceClient().getById(777426167L));

    // apm supplier type
    AttributeValueModel model2 = new AttributeValueModel();
    model2.setAttr(new AttributeServiceClient().get(624067L));
    model2.setValue(new AttributeValueServiceClient().getById(1135792L));

    // customer/brand
    AttributeValueModel model3 = new AttributeValueModel();
    model3.setAttr(new AttributeServiceClient().get(36L));
    model3.setValue(new AttributeValueServiceClient().getById(154L));

    attrValSet.add(model1);
    attrValSet.add(model2);
    attrValSet.add(model3);

    List<ReviewRule> reviewRuleList = getReviewRuleWithAttrDep(attrValSet, PARAMETER_3, FUNCTION_NAME_2);

    // create input
    ConfigBasedRuleInput<Function> configInput = new ConfigBasedRuleInput<>();

    List<String> labelNameList = new ArrayList<>();
    labelNameList.add(PARAMETER_3);
    configInput.setLabelNames(labelNameList);

    configInput.setAttrValueModSet(attrValSet);

    ReviewRuleParamCol<Function> reviewRuleParamCol = new ReviewRuleParamCol<>();
    reviewRuleParamCol.setReviewRule(reviewRuleList.get(0));
    Map<String, Function> functionMap = getFunction(FUNCTION_NAME_2);
    reviewRuleParamCol.setParamCollection(functionMap.get(FUNCTION_NAME_2));

    List<ReviewRuleExt> ruleList = new ReviewRuleServiceClient().getCompliRuleHistory(reviewRuleParamCol);
    assertEquals("Bitwise rule is equal", null, ruleList.get(0).getBitWiseRule());
    this.validation.validateReviewRuleForRuleHistory(ruleList.get(0));
  }


  /**
   * Test method for searchRuleForDep()
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testSearchRuleForDep() throws ApicWebServiceException {

    // create input
    ConfigBasedRuleInput<Function> configInput = new ConfigBasedRuleInput<>();

    List<String> labelNameList = new ArrayList<>();
    labelNameList.add(PARAMETER_2);
    configInput.setLabelNames(labelNameList);

    AttributeValueModel attrValueModel = new AttributeValueModel();
    attrValueModel.setAttr(new AttributeServiceClient().get(391L));
    attrValueModel.setValue(new AttributeValueServiceClient().getById(396L));

    SortedSet<AttributeValueModel> attrValSet = new TreeSet<>();
    attrValSet.add(attrValueModel);
    configInput.setAttrValueModSet(attrValSet);

    ReviewRuleParamCol<Function> paramCol = new ReviewRuleParamCol<Function>();

    // get function
    List<String> functionNameList = new ArrayList<>();
    functionNameList.add(FUNCTION_NAME_1);
    Map<String, Function> functionMap = new FunctionServiceClient().getFunctionsByName(functionNameList);
    paramCol.setParamCollection(functionMap.get(FUNCTION_NAME_1));
    configInput.setParamCol(paramCol);


    Map<String, List<ReviewRule>> reviewRuleMap = new ReviewRuleServiceClient().searchRuleForDep(configInput);
    assertEquals("Bitwise rule is equal", null, reviewRuleMap.get(PARAMETER_2).get(0).getBitWiseRule());
    this.validation.validateReviewRuleWithDependency(reviewRuleMap.get(PARAMETER_2).get(0), PARAMETER_2);


  }

  /**
   * Test method for createCheclValueRule()
   *
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testCreateCheckValueRule() throws ApicWebServiceException {
    CreateCheckValRuleModel checkValueRuleModel =
        new ReviewRuleServiceClient().createCheclValueRule(3576770811L, 781071265L);
    assertNotNull(checkValueRuleModel);
    this.validation.validateCheckValueRuleModel(checkValueRuleModel);
  }

  /**
   * Negative test case - review parameter
   *
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testCreateCheclValueRuleNegative1() throws ApicWebServiceException {
    Long dummyId = 929292L;
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Review Result Parameter with ID '" + dummyId + "' not found");
    new ReviewRuleServiceClient().createCheclValueRule(dummyId, 781071265L);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Negative test case - rule set id
   *
   * @throws ApicWebServiceException exception from service
   */
  public void testCreateCheclValueRuleNegative2() throws ApicWebServiceException {
    Long dummyId = 929292L;
    CreateCheckValRuleModel model = new ReviewRuleServiceClient().createCheclValueRule(3576770811L, dummyId);
    assertNotNull(model);
    this.validation.validateCheckValueRuleModel(model);
  }


  /**
   * @throws ApicWebServiceException exception from service
   */
  private SSDMessageWrapper createRule(final SortedSet<AttributeValueModel> attrValSet, final String param,
      final String function)
      throws ApicWebServiceException {

    List<ReviewRule> ruleList = new ArrayList<>();

    final ReviewRule newRviewRule = createNewReviewRuleObject(param, function, attrValSet);
    ruleList.add(newRviewRule);

    ReviewRuleParamCol<Function> parmCol = new ReviewRuleParamCol<Function>();
    parmCol.setParamCollection(getFunction(function).get(function));
    parmCol.setReviewRuleList(ruleList);

    // create rule
    return new ReviewRuleServiceClient().create(parmCol);

  }


  private ReviewRule createNewReviewRuleObject(final String param, final String function,
      final SortedSet<AttributeValueModel> attrValSet) {
    final ReviewRule newRviewRule = new ReviewRule();
    newRviewRule.setParameterName(param);
    newRviewRule.setValueType("VALUE");
    newRviewRule.setLowerLimit(new BigDecimal("10"));
    newRviewRule.setUpperLimit(new BigDecimal("20"));


    newRviewRule.setHint("Junit test hint");
    newRviewRule.setLabelFunction(function);

    newRviewRule.setUnit("ppm");
    newRviewRule.setReviewMethod("M");

    newRviewRule.setDcm2ssd(false);
    newRviewRule.setDependencyList(attrValSet);
    newRviewRule.setMaturityLevel("FIXED");
    return newRviewRule;
  }

  /**
   * @param unicodeRmrk
   * @throws ApicWebServiceException exception from service
   */
  private SSDMessageWrapper updateRuleUnicode(final SortedSet<AttributeValueModel> attrValSet, final String param,
      final String function, final String ruleRmrk, final String unicodeRmrk)
      throws ApicWebServiceException {

    List<ReviewRule> reviewRuleList = getReviewRuleWithAttrDep(attrValSet, param, function);

    ReviewRule ruleToUpdate = reviewRuleList.get(0);
    if (null != ruleRmrk) {
      ruleToUpdate.setHint(ruleRmrk);
    }
    ruleToUpdate.setUnicodeRemarks(unicodeRmrk);

    ReviewRuleParamCol<Function> parmCol = new ReviewRuleParamCol<Function>();
    parmCol.setParamCollection(getFunction(function).get(function));
    parmCol.setReviewRule(ruleToUpdate);

    // update rule
    return new ReviewRuleServiceClient().update(parmCol);


  }

  /**
   * @throws ApicWebServiceException exception from service
   */
  private SSDMessageWrapper updateRule(final SortedSet<AttributeValueModel> attrValSet, final String param,
      final String function)
      throws ApicWebServiceException {

    List<ReviewRule> reviewRuleList = getReviewRuleWithAttrDep(attrValSet, param, function);

    ReviewRule ruleToUpdate = reviewRuleList.get(0);
    ruleToUpdate.setLowerLimit(new BigDecimal("30"));
    ruleToUpdate.setUpperLimit(new BigDecimal("40"));


    ReviewRuleParamCol<Function> parmCol = new ReviewRuleParamCol<Function>();
    parmCol.setParamCollection(getFunction(function).get(function));
    parmCol.setReviewRule(ruleToUpdate);

    // update rule
    return new ReviewRuleServiceClient().update(parmCol);


  }

  /**
   * @throws ApicWebServiceException exception from service
   */
  private SSDMessageWrapper deleteRule(final SortedSet<AttributeValueModel> attrValSet, final String param,
      final String function)
      throws ApicWebServiceException {

    // delete
    return deleteRule(attrValSet, param, function, true);
  }

  /**
   * @throws ApicWebServiceException exception from service
   */
  private SSDMessageWrapper deleteRule(final SortedSet<AttributeValueModel> attrValSet, final String param,
      final String function, final boolean force)
      throws ApicWebServiceException {

    List<ReviewRule> reviewRuleList = getReviewRuleWithAttrDep(attrValSet, param, function);

    if (force || (reviewRuleList != null)) {
      ReviewRuleParamCol<Function> parmCol = new ReviewRuleParamCol<>();
      parmCol.setParamCollection(getFunction(function).get(function));
      parmCol.setReviewRuleList(reviewRuleList);

      // delete
      return new ReviewRuleServiceClient().delete(parmCol);
    }

    return new SSDMessageWrapper();

  }


}
