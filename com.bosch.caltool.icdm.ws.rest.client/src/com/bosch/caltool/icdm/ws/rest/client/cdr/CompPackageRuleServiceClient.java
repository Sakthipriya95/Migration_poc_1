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
import com.bosch.caltool.icdm.model.cdr.ReviewRuleParamCol;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author rgo7cob
 */
public class CompPackageRuleServiceClient extends AbstractRestServiceClient {

  /**
   * @param moduleBase
   * @param serviceBase
   */
  public CompPackageRuleServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_COMP_PKG);

  }

  /**
   * @param rule rule
   * @return the review rule
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public SSDMessageWrapper create(final ReviewRuleParamCol<CompPackage> rule) throws ApicWebServiceException {
    GenericType<SSDMessageWrapper> type = new GenericType<SSDMessageWrapper>() {};
    return post(getWsBase(), rule, type);
  }


  /**
   * @param rule rule
   * @return
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public SSDMessageWrapper update(final ReviewRuleParamCol<CompPackage> rule) throws ApicWebServiceException {
    GenericType<SSDMessageWrapper> type = new GenericType<SSDMessageWrapper>() {};
    return put(getWsBase(), rule, type);
  }


  /**
   * @param rule rule
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public SSDMessageWrapper delete(final ReviewRuleParamCol<CompPackage> rule) throws ApicWebServiceException {
    GenericType<SSDMessageWrapper> type = new GenericType<SSDMessageWrapper>() {};
    return post(getWsBase().path("delete"), rule, type);
  }


  /**
   * @param rule rule
   * @return the review rule param Collection
   * @throws ApicWebServiceException
   */
  public ReviewRuleParamCol<Function> readRules(final ReviewRuleParamCol<CompPackage> rule)
      throws ApicWebServiceException {
    GenericType<ReviewRuleParamCol<Function>> type = new GenericType<ReviewRuleParamCol<Function>>() {};
    return post(getWsBase().path("getRulesForParam"), rule, type);
  }


  /**
   * @param configBasedInput configBasedInput
   * @return the Map of review Rule List
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public Map<String, List<ReviewRule>> searchRuleForDep(final ConfigBasedRuleInput<CompPackage> configBasedInput)
      throws ApicWebServiceException {
    GenericType<Map<String, List<ReviewRule>>> type = new GenericType<Map<String, List<ReviewRule>>>() {};
    return post(getWsBase().path("rulesearch"), configBasedInput, type);
  }

  /**
   * @param rule rule
   * @return the review rule
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public List<ReviewRule> getCompliRuleHistory(final ReviewRuleParamCol paramCol) throws ApicWebServiceException {
    GenericType<List<ReviewRule>> type = new GenericType<List<ReviewRule>>() {};
    return post(getWsBase().path("compliRuleHistory"), paramCol, type);
  }

  /**
   * @param rule rule
   * @return the review rule
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public List<ReviewRule> getRuleHistory(final ReviewRuleParamCol<CompPackage> paramCol)
      throws ApicWebServiceException {
    GenericType<List<ReviewRule>> type = new GenericType<List<ReviewRule>>() {};
    return post(getWsBase().path("ruleHistory"), paramCol, type);
  }

  /**
   * @param paramID
   * @param paramCollectionId
   * @param isRuleSet
   * @return
   * @throws ApicWebServiceException
   */
  public CreateCheckValRuleModel createCheclValueRule(final Long paramID) throws ApicWebServiceException {
    GenericType<CreateCheckValRuleModel> type = new GenericType<CreateCheckValRuleModel>() {};
    return post(getWsBase().path("checkValueRule"), paramID, type);
  }

}
