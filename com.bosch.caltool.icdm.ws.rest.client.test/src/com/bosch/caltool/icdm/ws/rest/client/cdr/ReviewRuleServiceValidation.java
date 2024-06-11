/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.CreateCheckValRuleModel;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.ReviewRuleExt;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameterAttr;

/**
 * Class with validation methods for ReviewRuleServiceClientTest class
 *
 * @author pdh2cob
 */
public class ReviewRuleServiceValidation {


  /**
   * @param checkValueRuleModel - model to be validated
   */
  public void validateCheckValueRuleModel(final CreateCheckValRuleModel checkValueRuleModel) {
    assertTrue(checkValueRuleModel.isCanCreateRule());
    assertEquals("Checked value object is equal", 595, checkValueRuleModel.getCheckedValueObj().length);
    assertEquals("Error msg is equal", null, checkValueRuleModel.getErroMsg());
    assertEquals("param rules is equal", null, checkValueRuleModel.getParamRules());
    assertEquals("pidc version name is equal", "AUDI_A2l_TEST (Version 1)", checkValueRuleModel.getPidcVersName());
    assertEquals("review result name is equal", "2020-04-29 18:05 - MD50 - variant_1 - test_review",
        checkValueRuleModel.getResultName());
    assertEquals("rules to be edited is equal", null, checkValueRuleModel.getRuleToBeEdited());
    assertTrue(CommonUtils.isNotNull(checkValueRuleModel.getRulesetRules()));
    assertTrue(CommonUtils.isNotEmpty(checkValueRuleModel.getRulesetRules().getAttrMap()));
    assertTrue(checkValueRuleModel.getRulesetRules().getAttrMap().containsKey("NMDSYNC"));

    List<RuleSetParameterAttr> paramAttrList = checkValueRuleModel.getRulesetRules().getAttrMap().get("NMDSYNC");
    RuleSetParameterAttr paramAttr = null;
    for (RuleSetParameterAttr ruleSetParameterAttr : paramAttrList) {
      if (ruleSetParameterAttr.getId().equals(Long.valueOf(794412166))) {
        paramAttr = ruleSetParameterAttr;
      }
    }
    assertTrue(paramAttr != null);
    assertTrue(CommonUtils.isNotEmpty(checkValueRuleModel.getRulesetRules().getAttrObjMap()));
    assertTrue(checkValueRuleModel.getRulesetRules().getAttrObjMap().containsKey(48L));
    Attribute attribute = checkValueRuleModel.getRulesetRules().getAttrObjMap().get(48L);
    assertEquals("Attribute Id is equal", Long.valueOf(48), attribute.getId());
    assertEquals("Attribute name is equal", "Cylinder (No.)", attribute.getName());
    assertTrue(CommonUtils.isNotEmpty(checkValueRuleModel.getRulesetRules().getParamMap()));
    assertTrue(checkValueRuleModel.getRulesetRules().getParamMap().containsKey("DFC_DisblMsk2.DFC_SSpMon2OV_C"));
    RuleSetParameter ruleSetParam =
        checkValueRuleModel.getRulesetRules().getParamMap().get("DFC_DisblMsk2.DFC_SSpMon2OV_C");
    assertEquals("Rule set param id is equal", Long.valueOf(795447566), ruleSetParam.getId());

    assertTrue(checkValueRuleModel.getRulesetRules().getReviewRuleMap().containsKey("DFC_DisblMsk2.DFC_SSpMon2OV_C"));
    List<ReviewRule> ruleList =
        checkValueRuleModel.getRulesetRules().getReviewRuleMap().get("DFC_DisblMsk2.DFC_SSpMon2OV_C");

    ReviewRule rule = null;
    for (ReviewRule reviewRule : ruleList) {
      if (reviewRule.getRuleId().equals(new BigDecimal("3532248481"))) {
        rule = reviewRule;
      }
    }
    assertTrue(rule != null);
  }


  /**
   * @param reviewRuleExt rule to be validated
   */
  public void validateReviewRuleForRuleHistory(final ReviewRuleExt reviewRuleExt) {

    String expectedHint =
        "Valid for Hella 5Q1.723.503.H, 5Q1.721.503.H, 5Q0.907.505, 03C.907.505, 6PV.010.621-11, 6PV.010.621-10";
    String expectedDcmString =
        "FESTWERT\tAPP_swtHiImpdEna_C\r\n" + "\tLANGNAME\t\"Schalter für die Hochohmigkeitsprüfung\"\r\n" +
            "\tEINHEIT_W \"-\"\r\n" + "\tWERT 0.0\r\n" + "END\r\n" + "";


    assertEquals("DCM2SSD is equal", true, reviewRuleExt.isDcm2ssd());
    assertEquals("Attribute id is equal", Long.valueOf(776864698),
        reviewRuleExt.getDependencyList().first().getAttr().getId());
    assertEquals("Value id is equal", Long.valueOf(777426167),
        reviewRuleExt.getDependencyList().first().getValue().getId());
    assertEquals("Formula is equal", null, reviewRuleExt.getFormula());
    assertEquals("Formula description is equal", null, reviewRuleExt.getFormulaDesc());
    assertEquals("Hint is equal", expectedHint, reviewRuleExt.getHint());
    assertEquals("Label function is equal", null, reviewRuleExt.getLabelFunction());
    assertEquals("Label id is equal", new BigDecimal("602061"), reviewRuleExt.getLabelId());
    assertEquals("Lower limit is equal", null, reviewRuleExt.getLowerLimit());
    assertEquals("Maturity level is equal", "FIXED", reviewRuleExt.getMaturityLevel());
    assertEquals("Param class is equal", "CUSTSPEC", reviewRuleExt.getParamClass());
    assertEquals("Parameter name is equal", "APP_swtHiImpdEna_C", reviewRuleExt.getParameterName());
    assertEquals("Ref value is equal", null, reviewRuleExt.getRefValue());
    assertEquals("Ref value for caldata is equal", 589, reviewRuleExt.getRefValueCalData().length);
    assertEquals("Ref value dcm string is equal", expectedDcmString, reviewRuleExt.getRefValueDcmString());
    assertEquals("Ref value display string is equal", "0.0", reviewRuleExt.getRefValueDispString());
    assertEquals("Review method is equal", "A", reviewRuleExt.getReviewMethod());
    assertNotNull("Created date is not null", reviewRuleExt.getRuleCreatedDate());
    assertEquals("Created user is equal", "MEW2ABT", reviewRuleExt.getRuleCreatedUser());
    assertEquals("Dependency for display is equal", null, reviewRuleExt.getDependenciesForDisplay());
    assertEquals("Rule id is equal", new BigDecimal("3531761434"), reviewRuleExt.getRuleId());
    assertEquals("SsdCase is equal", "Review", reviewRuleExt.getSsdCase());
    assertEquals("Unit is equal", "-", reviewRuleExt.getUnit());
    assertEquals("Upper limit is equal", null, reviewRuleExt.getUpperLimit());
    assertEquals("Value type is equal", "VALUE", reviewRuleExt.getValueType());
    assertEquals("Rule owner is equal", null, reviewRuleExt.getRuleOwner());
    assertEquals("CoC is equal", null, reviewRuleExt.getCoc());
    assertEquals("Internal Adaptation Description is equal", null, reviewRuleExt.getInternalAdaptationDescription());
    assertEquals("Data Description is equal",
        "Valid for Hella 5Q1.723.503.H, 5Q1.721.503.H, 5Q0.907.505, 03C.907.505, 6PV.010.621-11, 6PV.010.621-10",
        reviewRuleExt.getDataDescription());
    assertEquals("Historie Description is equal", "Created by iCDM", reviewRuleExt.getHistorieDescription());


  }


  /**
   * @param reviewRule rule to be validated
   * @param parameterName parameter to be validated
   */
  public void validateReviewRuleWithDependency(final ReviewRule reviewRule, final String parameterName) {


    assertEquals("DCM2SSD is equal", false, reviewRule.isDcm2ssd());
    assertEquals("Attribute id is equal", Long.valueOf(391), reviewRule.getDependencyList().first().getAttr().getId());
    assertEquals("Value id is equal", Long.valueOf(396), reviewRule.getDependencyList().first().getValue().getId());
    assertEquals("Formula is equal", null, reviewRule.getFormula());
    assertEquals("Formula description is equal", null, reviewRule.getFormulaDesc());
    assertEquals("Hint is equal", null, reviewRule.getHint());
    assertEquals("Label function is equal", null, reviewRule.getLabelFunction());
    assertEquals("Label id is equal", new BigDecimal("5428033"), reviewRule.getLabelId());
    assertEquals("Lower limit is equal", new BigDecimal("0.5"), reviewRule.getLowerLimit());
    assertEquals("Maturity level is equal", "FIXED", reviewRule.getMaturityLevel());
    assertEquals("Param class is equal", "CUSTSPEC", reviewRule.getParamClass());
    assertEquals("Parameter name is equal", parameterName, reviewRule.getParameterName());
    assertEquals("Ref value is equal", null, reviewRule.getRefValue());
    assertEquals("Ref value for caldata is equal", null, reviewRule.getRefValueCalData());
    assertEquals("Ref value dcm string is equal", null, reviewRule.getRefValueDcmString());
    assertEquals("Ref value display string is equal", "", reviewRule.getRefValueDispString());
    assertEquals("Review method is equal", "M", reviewRule.getReviewMethod());
    assertNotNull("Created date is not null", reviewRule.getRuleCreatedDate());
    assertEquals("Created user is equal", "PDH2COB", reviewRule.getRuleCreatedUser());
    assertEquals("Dependency for display is equal", null, reviewRule.getDependenciesForDisplay());
    assertEquals("Rule id is equal", new BigDecimal("3532997271"), reviewRule.getRuleId());
    assertEquals("SsdCase is equal", "Review", reviewRule.getSsdCase());
    assertEquals("Unit is equal", "ppm", reviewRule.getUnit());
    assertEquals("Upper limit is equal", new BigDecimal("0.9"), reviewRule.getUpperLimit());
    assertEquals("Value type is equal", "CURVE", reviewRule.getValueType());


  }


  /**
   * @param reviewRule rule to be validated
   * @param parameterName parameter to be validated
   */
  public void validateReviewRule(final ReviewRule reviewRule, final String parameterName) {
    assertEquals("Bitwise rule is equal", null, reviewRule.getBitWiseRule());
    assertEquals("Formula description is equal", null, reviewRule.getFormulaDesc());
    assertEquals("Hint substring is present", true, reviewRule.getHint().contains("The rule is used for auto tests"));
    assertEquals("Label id is equal", new BigDecimal("5453442"), reviewRule.getLabelId());
    assertEquals("Lower limit is equal", new BigDecimal("1"), reviewRule.getLowerLimit());
    assertEquals("Upper limit is equal", new BigDecimal("10"), reviewRule.getUpperLimit());
    assertEquals("Parameter name is equal", parameterName, reviewRule.getParameterName());
    assertEquals("Rule id is equal", new BigDecimal("3530253915"), reviewRule.getRuleId());
    assertEquals("Unit is equal", null, reviewRule.getUnit());
    assertEquals("Value type is equal", "CURVE", reviewRule.getValueType());
  }


  /**
   * @param reviewRule rule to be validated
   * @param paramToValidate parameter
   * @param dep if dependency attributes are present
   */
  public void validateCreatedReviewRule(final ReviewRule reviewRule, final String paramToValidate, final boolean dep) {


    assertEquals("Bitwise rule is equal", null, reviewRule.getBitWiseRule());
    assertEquals("DCM2SSD is equal", false, reviewRule.isDcm2ssd());

    if (dep) {
      for (AttributeValueModel attributeValueModel : reviewRule.getDependencyList()) {
        if (attributeValueModel.getAttr().getId().equals(36L)) {
          assertEquals("Attribute id is equal", Long.valueOf(36), attributeValueModel.getAttr().getId());
          assertEquals("Value id is equal", Long.valueOf(671), attributeValueModel.getValue().getId());
        }
      }
    }


    assertEquals("Formula is equal", null, reviewRule.getFormula());
    assertEquals("Formula description is equal", null, reviewRule.getFormulaDesc());
    assertEquals("Hint is equal", "Junit test hint", reviewRule.getHint());
    assertEquals("Label function is equal", null, reviewRule.getLabelFunction());
    assertEquals("Lower limit is equal", new BigDecimal("10"), reviewRule.getLowerLimit());
    assertEquals("Maturity level is equal", "FIXED", reviewRule.getMaturityLevel());
    assertEquals("Param class is equal", "CUSTSPEC", reviewRule.getParamClass());
    assertEquals("Parameter name is equal", paramToValidate, reviewRule.getParameterName());
    assertEquals("Ref value is equal", null, reviewRule.getRefValue());
    assertEquals("Ref value for caldata is equal", null, reviewRule.getRefValueCalData());
    assertEquals("Ref value dcm string is equal", null, reviewRule.getRefValueDcmString());
    assertEquals("Ref value display string is equal", "", reviewRule.getRefValueDispString());
    assertEquals("Review method is equal", "M", reviewRule.getReviewMethod());
    assertEquals("Created user is equal", "BNE4COB", reviewRule.getRuleCreatedUser());
    assertEquals("Dependency for display is equal", null, reviewRule.getDependenciesForDisplay());
    assertEquals("SsdCase is equal", "Review", reviewRule.getSsdCase());
    assertEquals("Unit is equal", "ppm", reviewRule.getUnit());
    assertEquals("Upper limit is equal", new BigDecimal("20"), reviewRule.getUpperLimit());
    assertEquals("Value type is equal", "VALUE", reviewRule.getValueType());


  }

  /**
   * @param reviewRule rule to be validated
   * @param paramToValidate parameter
   * @param dep if dependency attributes are present
   */
  public void validateUpdatedReviewRule(final ReviewRule reviewRule, final String paramToValidate, final boolean dep) {


    assertEquals("Bitwise rule is equal", null, reviewRule.getBitWiseRule());
    assertEquals("DCM2SSD is equal", false, reviewRule.isDcm2ssd());

    if (dep) {
      for (AttributeValueModel attributeValueModel : reviewRule.getDependencyList()) {
        if (attributeValueModel.getAttr().getId().equals(36L)) {
          assertEquals("Attribute id is equal", Long.valueOf(36), attributeValueModel.getAttr().getId());
          assertEquals("Value id is equal", Long.valueOf(671), attributeValueModel.getValue().getId());
        }
      }
    }

    assertEquals("Formula is equal", null, reviewRule.getFormula());
    assertEquals("Formula description is equal", null, reviewRule.getFormulaDesc());
    assertEquals("Hint is equal", "Junit test hint", reviewRule.getHint());
    assertEquals("Label function is equal", null, reviewRule.getLabelFunction());
    assertEquals("Lower limit is equal", new BigDecimal("30"), reviewRule.getLowerLimit());
    assertEquals("Maturity level is equal", "FIXED", reviewRule.getMaturityLevel());
    assertEquals("Param class is equal", "CUSTSPEC", reviewRule.getParamClass());
    assertEquals("Parameter name is equal", paramToValidate, reviewRule.getParameterName());
    assertEquals("Ref value is equal", null, reviewRule.getRefValue());
    assertEquals("Ref value for caldata is equal", null, reviewRule.getRefValueCalData());
    assertEquals("Ref value dcm string is equal", null, reviewRule.getRefValueDcmString());
    assertEquals("Ref value display string is equal", "", reviewRule.getRefValueDispString());
    assertEquals("Review method is equal", "M", reviewRule.getReviewMethod());
    assertEquals("Created user is equal", "BNE4COB", reviewRule.getRuleCreatedUser());
    assertEquals("Dependency for display is equal", null, reviewRule.getDependenciesForDisplay());
    assertEquals("SsdCase is equal", "Review", reviewRule.getSsdCase());
    assertEquals("Unit is equal", "ppm", reviewRule.getUnit());
    assertEquals("Upper limit is equal", new BigDecimal("40"), reviewRule.getUpperLimit());
    assertEquals("Value type is equal", "VALUE", reviewRule.getValueType());


  }

}
