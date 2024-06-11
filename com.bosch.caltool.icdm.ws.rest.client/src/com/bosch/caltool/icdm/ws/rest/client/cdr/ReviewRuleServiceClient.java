/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.SSDMessageWrapper;
import com.bosch.caltool.icdm.model.cdr.ConfigBasedRuleInput;
import com.bosch.caltool.icdm.model.cdr.CreateCheckValRuleModel;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.ReviewRuleExt;
import com.bosch.caltool.icdm.model.cdr.ReviewRuleParamCol;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Client class to fetch rule sets.
 *
 * @author rgo7cob
 */
// Task 263282
public class ReviewRuleServiceClient extends AbstractRestServiceClient {


  /**
   * initialize client constructor with the compli-param url
   */
  public ReviewRuleServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_REVIEW_RULE);
  }


  /**
   * @param rule rule
   * @return the review rule
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public SSDMessageWrapper create(final ReviewRuleParamCol<Function> rule) throws ApicWebServiceException {
    GenericType<SSDMessageWrapper> type = new GenericType<SSDMessageWrapper>() {};
    return post(getWsBase(), rule, type);
  }

  /**
   * @param rule rule
   * @return
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public SSDMessageWrapper update(final ReviewRuleParamCol<Function> rule) throws ApicWebServiceException {
    GenericType<SSDMessageWrapper> type = new GenericType<SSDMessageWrapper>() {};
    return put(getWsBase(), rule, type);
  }


  /**
   * @param rule rule
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public SSDMessageWrapper delete(final ReviewRuleParamCol<Function> rule) throws ApicWebServiceException {
    GenericType<SSDMessageWrapper> type = new GenericType<SSDMessageWrapper>() {};
    return post(getWsBase().path(WsCommonConstants.RWS_DELETE), rule, type);
  }


  /**
   * @param rule rule
   * @return the review rule param Collection
   * @throws ApicWebServiceException
   */
  public ReviewRuleParamCol<Function> readRules(final ReviewRuleParamCol<Function> rule)
      throws ApicWebServiceException {
    GenericType<ReviewRuleParamCol<Function>> type = new GenericType<ReviewRuleParamCol<Function>>() {};
    return post(getWsBase().path(WsCommonConstants.RWS_RULES_FOR_PARAM), rule, type);
  }

  /**
   * @param labelNames labelNames
   * @param dependencies dependencies
   * @return review rule map
   * @throws ApicWebServiceException exception
   */
  public Map<String, List<ReviewRule>> readRulesForDependency(final ConfigBasedRuleInput<Function> configInput)
      throws ApicWebServiceException {
    GenericType<Map<String, List<ReviewRule>>> type = new GenericType<Map<String, List<ReviewRule>>>() {};
    return post(getWsBase().path(WsCommonConstants.RWS_RULES_FOR_PARAM_WITH_DEP), configInput, type);
  }

  /**
   * @param configBasedInput configBasedInput
   * @return the Map of review Rule List
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public Map<String, List<ReviewRule>> searchRuleForDep(final ConfigBasedRuleInput<Function> configBasedInput)
      throws ApicWebServiceException {
    GenericType<Map<String, List<ReviewRule>>> type = new GenericType<Map<String, List<ReviewRule>>>() {};
    return post(getWsBase().path(WsCommonConstants.RWS_SEARCH), configBasedInput, type);
  }

  /**
   * @return the review rule
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public List<ReviewRuleExt> getCompliRuleHistory(final ReviewRuleParamCol<Function> paramCol)
      throws ApicWebServiceException {
    GenericType<List<ReviewRuleExt>> type = new GenericType<List<ReviewRuleExt>>() {};
    return post(getWsBase().path(WsCommonConstants.RWS_COMPLI_RULE_HISTORY), paramCol, type);
  }

  /**
   * @return the review rule
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public List<ReviewRuleExt> getRuleHistory(final ReviewRuleParamCol<Function> paramCol)
      throws ApicWebServiceException {
    GenericType<List<ReviewRuleExt>> type = new GenericType<List<ReviewRuleExt>>() {};
    return post(getWsBase().path(WsCommonConstants.RWS_RULE_HISTORY), paramCol, type);
  }

  /**
   * @param paramID
   * @param ruleSetId
   * @return
   * @throws ApicWebServiceException
   */
  public CreateCheckValRuleModel createCheclValueRule(final Long paramID, final Long ruleSetId)
      throws ApicWebServiceException {
    GenericType<CreateCheckValRuleModel> type = new GenericType<CreateCheckValRuleModel>() {};
    return post(getWsBase().path(WsCommonConstants.RWS_CHECK_VALUE_RULE).queryParam(WsCommonConstants.RWS_QP_RULESET_ID,
        ruleSetId), paramID, type);
  }
}
