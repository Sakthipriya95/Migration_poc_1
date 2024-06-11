/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.a2l.SSDMessageWrapper;
import com.bosch.caltool.icdm.model.cdr.ConfigBasedRuleInput;
import com.bosch.caltool.icdm.model.cdr.ExportCDFxInputData;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.ReviewRuleExt;
import com.bosch.caltool.icdm.model.cdr.ReviewRuleParamCol;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Client class to fetch rule sets.
 *
 * @author rgo7cob
 */
// Task 263282
public class RuleSetRuleServiceClient extends AbstractRestServiceClient {


  /**
   * initialize client constructor with the compli-param url
   */
  public RuleSetRuleServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_REVIEW_RULE_SET_RULE);
  }


  /**
   * @param rule rule
   * @return the review rule
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public SSDMessageWrapper create(final ReviewRuleParamCol<RuleSet> rule) throws ApicWebServiceException {
    GenericType<SSDMessageWrapper> type = new GenericType<SSDMessageWrapper>() {};
    return post(getWsBase(), rule, type);
  }


  /**
   * @param rule rule
   * @return
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public SSDMessageWrapper update(final ReviewRuleParamCol<RuleSet> rule) throws ApicWebServiceException {
    GenericType<SSDMessageWrapper> type = new GenericType<SSDMessageWrapper>() {};
    return put(getWsBase(), rule, type);
  }


  /**
   * @param rule rule
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public SSDMessageWrapper delete(final ReviewRuleParamCol<RuleSet> rule) throws ApicWebServiceException {
    GenericType<SSDMessageWrapper> type = new GenericType<SSDMessageWrapper>() {};
    return post(getWsBase().path(WsCommonConstants.RWS_DELETE), rule, type);
  }


  /**
   * @param rule rule
   * @return the review rule param Collection
   * @throws ApicWebServiceException
   */
  public ReviewRuleParamCol<RuleSet> readRules(final ReviewRuleParamCol<RuleSet> rule) throws ApicWebServiceException {
    GenericType<ReviewRuleParamCol<RuleSet>> type = new GenericType<ReviewRuleParamCol<RuleSet>>() {};
    return post(getWsBase().path(WsCommonConstants.RWS_RULES_FOR_PARAM), rule, type);
  }

  /**
   * @param configInput configInput
   * @return review rule map
   * @throws ApicWebServiceException exception
   */
  public Map<String, List<ReviewRule>> readRulesForDependency(final ConfigBasedRuleInput<RuleSet> configInput)
      throws ApicWebServiceException {
    GenericType<Map<String, List<ReviewRule>>> type = new GenericType<Map<String, List<ReviewRule>>>() {};
    return post(getWsBase().path(WsCommonConstants.RWS_RULES_FOR_PARAM_WITH_DEP), configInput, type);
  }

  /**
   * @param configBasedInput configBasedInput
   * @return the Map of review Rule List
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public Map<String, List<ReviewRule>> searchRuleForDep(final ConfigBasedRuleInput<RuleSet> configBasedInput)
      throws ApicWebServiceException {
    GenericType<Map<String, List<ReviewRule>>> type = new GenericType<Map<String, List<ReviewRule>>>() {};
    return post(getWsBase().path(WsCommonConstants.RWS_SEARCH), configBasedInput, type);
  }

  /**
   * @param rule rule
   * @return the review rule
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public List<ReviewRuleExt> getRuleHistory(final ReviewRuleParamCol<RuleSet> paramCol) throws ApicWebServiceException {
    GenericType<List<ReviewRuleExt>> type = new GenericType<List<ReviewRuleExt>>() {};
    return post(getWsBase().path(WsCommonConstants.RWS_RULE_HISTORY), paramCol, type);
  }

  /**
   * @param rule rule
   * @return the review rule
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public List<ReviewRuleExt> getCompliRuleHistory(final ReviewRuleParamCol<RuleSet> paramCol)
      throws ApicWebServiceException {
    GenericType<List<ReviewRuleExt>> type = new GenericType<List<ReviewRuleExt>>() {};
    return post(getWsBase().path(WsCommonConstants.RWS_COMPLI_RULE_HISTORY), paramCol, type);
  }


  /**
   * @param exportInput exportInput
   * @return downloaded filePath
   * @throws ApicWebServiceException Exception
   */
  public String exportRuleSetCalDataAsCdfx(final ExportCDFxInputData exportInput) throws ApicWebServiceException {
    return downloadFilePost(getWsBase().path(WsCommonConstants.RWS_EXPORT_RULESET_AS_CDFX), exportInput,
        exportInput.getDestFileName(), exportInput.getDestFileDir());
  }
}
