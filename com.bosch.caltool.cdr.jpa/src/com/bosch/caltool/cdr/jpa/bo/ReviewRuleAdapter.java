/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.cdr.FeatureAttributeAdapterNew;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.FeatureValueModel;
import com.bosch.ssd.icdm.model.SSDCase;
import com.bosch.ssd.icdm.model.SSDConfigEnums.ParameterClass;

/**
 * @author rgo7cob
 */
public class ReviewRuleAdapter {


  private final ILoggerAdapter logger;
  private final ServiceData serviceData;


  /**
   * @param logger logger
   */
  public ReviewRuleAdapter(final ILoggerAdapter logger, final ServiceData serviceData) {
    this.logger = logger;
    this.serviceData = serviceData;
  }


  /**
   * @param ruleList Cdr rule list ruleList
   * @return the review rule list from icdm model
   * @throws DataException DataException
   */
  public List<ReviewRule> convertSSDRule(final List<CDRRule> ruleList) throws DataException {
    List<ReviewRule> reviewRuleList = new ArrayList<>();
    FeatureAttributeAdapterNew feaAdapter = new FeatureAttributeAdapterNew(this.serviceData);

    for (CDRRule rule : ruleList) {
      ReviewRule reviewRule = new ReviewRule();
      reviewRule.setBitWiseRule(rule.getBitWiseRule());
      reviewRule.setDcm2ssd(rule.isDcm2ssd());
      reviewRule.setDependenciesForDisplay(rule.getDependenciesForDisplay());

      List<FeatureValueModel> feaValModelList = rule.getDependencyList();
      // To be done.
      reviewRule.setDependencyList(feaAdapter.getAttrValSet(feaValModelList, this.serviceData));
      reviewRule.setFormula(rule.getFormula());
      reviewRule.setFormulaDesc(rule.getFormulaDesc());

      reviewRule.setHint(rule.getHint());
      reviewRule.setLabelFunction(rule.getLabelFunction());
      reviewRule.setLabelId(rule.getLabelId());

      reviewRule.setLowerLimit(rule.getLowerLimit());
      reviewRule.setMaturityLevel(rule.getMaturityLevel());
      reviewRule.setParamClass(rule.getParamClass().name());

      reviewRule.setRefValueDispString(rule.getRefValueDispString());
      reviewRule.setRefValueDcmString(rule.getRefValueDCMString());
      reviewRule.setParameterName(rule.getParameterName());
      if (rule.getRefValueCalData() != null) {
        reviewRule.setRefValCalData(CalDataUtil.convertCalDataToZippedByteArr(rule.getRefValueCalData(), this.logger));

      }

      reviewRule.setRefValue(rule.getRefValue());

      reviewRule.setRevId(rule.getRevId());

      reviewRule.setReviewMethod(rule.getReviewMethod());

      reviewRule.setRuleCreatedDate(rule.getRuleCreatedDate());
      reviewRule.setRuleId(rule.getRuleId());
      if (rule.getSsdCase() != null) {
        reviewRule.setSsdCase(rule.getSsdCase().getCharacter());
      }

      reviewRule.setUnit(rule.getUnit());

      reviewRule.setUpperLimit(rule.getUpperLimit());

      reviewRule.setValueType(rule.getValueType());
      reviewRuleList.add(reviewRule);
    }


    return reviewRuleList;
  }


  /**
   * @param reviewRuleList reviewRuleList
   * @return the Cdr rule
   * @throws DataException DataException
   */
  public List<CDRRule> convertReviewRule(final List<ReviewRule> reviewRuleList) throws DataException {
    List<CDRRule> ruleList = new ArrayList<>();


    for (ReviewRule reviewRule : reviewRuleList) {

      CDRRule rule = createCdrRule(reviewRule);
      ruleList.add(rule);
    }

    return ruleList;

  }


  /**
   * @param reviewRule reviewRule
   * @return the new Rule
   * @throws DataException DataException
   */
  public CDRRule createCdrRule(final ReviewRule reviewRule) throws DataException {
    FeatureAttributeAdapterNew feaAdapter = new FeatureAttributeAdapterNew(this.serviceData);
    CDRRule rule = new CDRRule();
    rule.setBitWiseRule(reviewRule.getBitWiseRule());
    rule.setDcm2ssd(reviewRule.isDcm2ssd());
    rule.setDependenciesForDisplay(reviewRule.getDependenciesForDisplay());
    // to be done
    rule.setDependencyList(feaAdapter.getFeaValModelList(reviewRule.getDependencyList()));
    rule.setFormula(reviewRule.getFormula());
    rule.setFormulaDesc(reviewRule.getFormulaDesc());
    rule.setHint(reviewRule.getHint());
    rule.setLabelFunction(reviewRule.getLabelFunction());
    rule.setLabelId(reviewRule.getLabelId());
    rule.setLowerLimit(reviewRule.getLowerLimit());
    rule.setMaturityLevel(reviewRule.getMaturityLevel());
    if (reviewRule.getParamClass() != null) {
      rule.setParamClass(ParameterClass.getType(reviewRule.getParamClass()));
    }
    rule.setParameterName(reviewRule.getParameterName());
    rule.setRefValCalData(getRefValForRule(reviewRule));
    rule.setRefValue(reviewRule.getRefValue());
    rule.setRefValueDCMString(reviewRule.getRefValueDcmString());
    rule.setRevId(reviewRule.getRevId());
    rule.setReviewMethod(reviewRule.getReviewMethod());
    rule.setRuleCreatedDate(reviewRule.getRuleCreatedDate());
    rule.setRuleCreatedUser(reviewRule.getRuleCreatedUser());
    rule.setRuleId(reviewRule.getRuleId());
    rule.setSsdCase(SSDCase.getType(reviewRule.getSsdCase()));
    rule.setUnit(reviewRule.getUnit());
    rule.setUpperLimit(reviewRule.getUpperLimit());
    rule.setValueType(reviewRule.getValueType());
    return rule;
  }

  /**
   * @param rule
   * @return
   * @throws ClassNotFoundException
   * @throws IOException
   */
  private CalData getRefValForRule(final ReviewRule rule) throws DataException {
    CalData caldataObj = null;
    try {
      caldataObj = CalDataUtil.getCalDataObj(rule.getRefValueCalData());
    }
    catch (ClassNotFoundException | IOException exp) {
      throw new DataException("error when creating cal data object" + exp);
    }

    return caldataObj;
  }


}
